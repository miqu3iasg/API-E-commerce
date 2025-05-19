package com.application.API_E_commerce.adapters.inbound.messaging;

import com.application.API_E_commerce.adapters.inbound.dtos.StockUpdateEvent;
import com.application.API_E_commerce.domain.stock.services.StockService;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidQuantityException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductOutOfStockException;
import com.application.API_E_commerce.infrastructure.exceptions.product.StockUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class StockUpdateListener {

	private static final Logger logger = LoggerFactory.getLogger(StockUpdateListener.class);

	private static final int MAX_RETRIES = 3;
	private static final String RETRY_COUNT_HEADER = "x-retry-count";
	private static final String FAILURE_REASON_HEADER = "x-failure-reason";
	private static final String DLX_NAME = "stock.dlx";
	private static final String DLQ_ROUTING_KEY = "stock.update.dlq";

	private final StockService stockService;
	private final RabbitTemplate rabbitTemplate;

	public StockUpdateListener (StockService stockService, RabbitTemplate rabbitTemplate) {
		this.stockService = stockService;
		this.rabbitTemplate = rabbitTemplate;
	}

	@RabbitListener(queues = "stock.update.queue")
	public void handleStockUpdate (StockUpdateEvent event, Message message) {
		String correlationId = event.correlationId();

		logger.info("Processing stock update event: correlationId={}, productId={}, operation={}, quantity={}",
				correlationId, event.productId(), event.operation(), event.quantity());

		if (event.quantity() <= 0) {
			final String failureReason = "Invalid quantity: quantity must be greater than zero.";
			logger.error("Validation failed for stock update: correlationId={}, error={}", correlationId, failureReason);
			sendToDlq(event, message, failureReason);
			return;
		}

		MessageProperties props = message.getMessageProperties();
		Integer retryCount = (Integer) props.getHeaders().getOrDefault(RETRY_COUNT_HEADER, 0);

		try {
			stockService.processStockUpdate(event);
			logger.info("Stock update processed successfully: correlationId={}", correlationId);
		} catch (
				InvalidQuantityException |
				ProductOutOfStockException |
				IllegalArgumentException e
		) {
			// Business rule violations: send to DLQ immediately
			logger.error("Business rule violation for stock update: correlationId={}, error={}", correlationId, e.getMessage());
			sendToDlq(event, message, "Business rule violation: " + e.getMessage());
		} catch (Exception e) {
			if (retryCount >= MAX_RETRIES) {
				logger.error("Max retries reached for stock update: correlationId={}, error={}", correlationId, e.getMessage());
				sendToDlq(event, message, "Max retries exceeded: " + e.getMessage());
			} else {
				props.getHeaders().put(RETRY_COUNT_HEADER, retryCount + 1);
				logger.warn("Retrying stock update: correlationId={}, attempt={}/{}", correlationId, retryCount + 1, MAX_RETRIES);
				throw new StockUpdateException("Retrying stock update", e);
			}
		}
	}

	private void sendToDlq (
			StockUpdateEvent event,
			Message originalMessage,
			String failureReason
	) {
		logger.info("Sending message to DLQ: correlationId={}", event.correlationId());

		MessageProperties dlqProperties = new MessageProperties();
		dlqProperties.getHeaders().putAll(originalMessage.getMessageProperties().getHeaders());
		dlqProperties.getHeaders().put(FAILURE_REASON_HEADER, failureReason);

		Message dlqMessage = MessageBuilder
				.withBody(originalMessage.getBody())
				.andProperties(dlqProperties)
				.build();

		rabbitTemplate.send(DLX_NAME, DLQ_ROUTING_KEY, dlqMessage);
		logger.info("Message sent to DLQ: correlationId={}, reason={}", event.correlationId(), failureReason);
	}

}
