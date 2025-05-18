package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.ProductUseCases;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.CreateProductRequestDTO;
import com.application.API_E_commerce.domain.product.dtos.StockUpdateEvent;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.infrastructure.configuration.RabbitMQConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class StockIntegrationTest {

	@Container
	private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management-alpine")
			.withExposedPorts(5672, 15672);

	@Autowired
	StockServiceImplementation stockService;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ProductUseCases productService;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@DynamicPropertySource
	static void rabbitmqProperties (DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
		registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
		registry.add("spring.rabbitmq.username", () -> "guest");
		registry.add("spring.rabbitmq.password", () -> "guest");

	}

	@BeforeEach
	void setUp () {
		rabbitTemplate.setChannelTransacted(true);
	}

	@Test
	void shouldIncreaseStockSuccessfully () throws InterruptedException {
		CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
				"Test Name",
				"Test description",
				BigDecimal.valueOf(20.0),
				10
		);

		Product product = productService.createProduct(createProductRequest);

		UUID productId = product.getId();
		int initialStock = product.getStock();
		final int quantityToIncrease = 5;

		stockService.increaseProductStock(productId, quantityToIncrease);

		TimeUnit.SECONDS.sleep(2);

		Product updatedProduct = productRepository.findProductById(productId).orElseThrow();
		int finalStock = updatedProduct.getStock();
		int expectedStock = initialStock + quantityToIncrease;
		assertEquals(expectedStock, finalStock, "Stock should be increased by " + quantityToIncrease);
	}

	@Test
	void shouldDecreaseStockSuccessfully () throws InterruptedException {
		CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
				"Test Name",
				"Test description",
				BigDecimal.valueOf(20.0),
				10
		);

		Product product = productService.createProduct(createProductRequest);

		UUID productId = product.getId();
		int initialStock = product.getStock();
		final int quantityToDecrease = 5;

		stockService.decreaseProductStock(productId, quantityToDecrease);

		TimeUnit.SECONDS.sleep(2);

		Product updatedProduct = productRepository.findProductById(productId).orElseThrow();
		int finalStock = updatedProduct.getStock();
		int expectedStock = initialStock - quantityToDecrease;
		assertEquals(expectedStock, finalStock, "Stock should be decreased by " + quantityToDecrease);
	}

	public Product mockProduct () {
		Product product = new Product();
		product.setId(UUID.randomUUID());
		product.setName("Test Product");
		product.setDescription("Description");
		product.setPrice(BigDecimal.valueOf(100.0));
		product.setStock(10);
		productRepository.saveProduct(product);
		return product;
	}

	@Test
	void shouldSendToDLQWhenInsufficientStock () throws InterruptedException {
		CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
				"Test Name",
				"Test description",
				BigDecimal.valueOf(20.0),
				10
		);

		Product product = productService.createProduct(createProductRequest);

		UUID productId = product.getId();
		int quantityToDecrease = product.getStock() + 1;

		stockService.decreaseProductStock(productId, quantityToDecrease);

		TimeUnit.SECONDS.sleep(2);

		Message dlqMessage = rabbitTemplate.receive(RabbitMQConfiguration.DLQ_NAME, 5000);
		assertNotNull(dlqMessage, "A message should be sent to the DLQ");

		assertEquals("Business rule violation: Insufficient stock for product " + productId +
						": current=" + product.getStock() + ", requested=" + quantityToDecrease,
				dlqMessage.getMessageProperties().getHeaders().get("x-failure-reason"));
		assertEquals(0, dlqMessage.getMessageProperties().getHeaders().getOrDefault("x-retry-count", 0));

		StockUpdateEvent event = (StockUpdateEvent) rabbitTemplate.getMessageConverter()
				.fromMessage(dlqMessage);

		assertEquals(productId, event.productId());
		assertEquals(quantityToDecrease, event.quantity());
		assertEquals(StockUpdateEvent.OperationType.DECREASE, event.operation());
		assertNotNull(event.correlationId(), "Correlation id should not be null");

		Product unchangedProduct = productRepository.findProductById(productId).orElseThrow();
		assertEquals(product.getStock(), unchangedProduct.getStock(),
				"Stock should not change due to insufficient stock");
	}

	@Test
	void shouldSendTDLQForInvalidQuantityEvent () throws InterruptedException {
		CreateProductRequestDTO createProductRequest = new CreateProductRequestDTO(
				"Test Name",
				"Test description",
				BigDecimal.valueOf(20.0),
				10
		);

		Product product = productService.createProduct(createProductRequest);

		UUID productId = product.getId();
		final int invalidQuantity = 0;
		String correlationId = UUID.randomUUID().toString();

		StockUpdateEvent event = new StockUpdateEvent(
				productId,
				invalidQuantity,
				StockUpdateEvent.OperationType.INCREASE,
				correlationId
		);

		rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_NAME,
				RabbitMQConfiguration.ROUTING_KEY, event);


		TimeUnit.SECONDS.sleep(2);

		Message dlqMessage = rabbitTemplate.receive(RabbitMQConfiguration.DLQ_NAME, 5000);
		assertNotNull(dlqMessage, "A message should be sent to the DLQ");

		assertEquals("Invalid quantity: quantity must be greater than zero.",
				dlqMessage.getMessageProperties().getHeaders().get("x-failure-reason"));
		assertEquals(0, dlqMessage.getMessageProperties().getHeaders().getOrDefault("x-retry-count", 0));

		StockUpdateEvent dlqEvent = (StockUpdateEvent) rabbitTemplate.getMessageConverter().fromMessage(dlqMessage);
		assertEquals(productId, dlqEvent.productId());
		assertEquals(invalidQuantity, dlqEvent.quantity());
		assertEquals(StockUpdateEvent.OperationType.INCREASE, dlqEvent.operation());
		assertEquals(correlationId, dlqEvent.correlationId());

		Product unchangedProduct = productRepository.findProductById(productId).orElseThrow();
		assertEquals(product.getStock(), unchangedProduct.getStock(),
				"Stock should not change due to invalid quantity");
	}

	@Test
	void shouldSendToDLQForNonExistingProduct () throws InterruptedException {
		UUID nonExistentProductId = UUID.randomUUID();
		final int quantityToIncrease = 5;
		String correlationId = UUID.randomUUID().toString();
		StockUpdateEvent event = new StockUpdateEvent(
				nonExistentProductId,
				quantityToIncrease,
				StockUpdateEvent.OperationType.INCREASE,
				correlationId
		);

		rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE_NAME,
				RabbitMQConfiguration.ROUTING_KEY, event);

		TimeUnit.SECONDS.sleep(2);

		Message dlqMessage = rabbitTemplate.receive(RabbitMQConfiguration.DLQ_NAME, 5000);
		assertNotNull(dlqMessage, "A message should be sent to the DLQ");

		assertEquals(0, dlqMessage.getMessageProperties().getHeaders().getOrDefault("x-retry-count", 0));

		StockUpdateEvent dlqEvent = (StockUpdateEvent) rabbitTemplate.getMessageConverter().fromMessage(dlqMessage);
		assertEquals(nonExistentProductId, dlqEvent.productId());
		assertEquals(quantityToIncrease, dlqEvent.quantity());
		assertEquals(StockUpdateEvent.OperationType.INCREASE,
				dlqEvent.operation());
		assertEquals(correlationId, dlqEvent.correlationId());
	}


}
