package com.application.API_E_commerce.application.services;

import com.application.API_E_commerce.application.usecases.StockUseCases;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.dtos.StockUpdateEvent;
import com.application.API_E_commerce.domain.product.repository.ProductRepository;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidOperationTypeException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductOutOfStockException;
import com.application.API_E_commerce.port.outbound.StockUpdatePort;
import com.application.API_E_commerce.utils.validators.ProductValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockServiceImplementation implements StockUseCases {

	private final ProductRepository productRepository;
	private final StockUpdatePort stockUpdatePort;
	private final ProductValidator productValidator;

	public StockServiceImplementation (ProductRepository productRepository,
	                                   StockUpdatePort stockUpdatePort, ProductValidator productValidator) {
		this.productRepository = productRepository;
		this.stockUpdatePort = stockUpdatePort;
		this.productValidator = productValidator;
	}

	@Override
	public void increaseProductStock (UUID productId, int quantityToIncrease) {
		StockUpdateEvent event = new StockUpdateEvent(
				productId,
				quantityToIncrease,
				StockUpdateEvent.OperationType.INCREASE,
				UUID.randomUUID().toString()
		);

		stockUpdatePort.publishStockUpdate(event);
	}

	@Override
	public void decreaseProductStock (UUID productId, int quantityToDecrease) {
		StockUpdateEvent event = new StockUpdateEvent(
				productId,
				quantityToDecrease,
				StockUpdateEvent.OperationType.DECREASE,
				UUID.randomUUID().toString()
		);

		stockUpdatePort.publishStockUpdate(event);
	}

	public void processStockUpdate (StockUpdateEvent event) {
		Product product = productValidator.validateIfProductExistsAndReturnTheExistingProduct(event.productId());

		int actualStock = product.getStock();

		int newStock = switch (event.operation()) {
			case INCREASE -> actualStock + event.quantity();
			case DECREASE -> {
				if (actualStock - event.quantity() < 0)
					throw new ProductOutOfStockException("Insufficient stock for product " + event.productId() +
							": current=" + product.getStock() + ", requested=" + event.quantity());
				yield actualStock - event.quantity();
			}
			default -> throw new InvalidOperationTypeException("Invalid operation " +
					"type.");
		};

		product.setStock(newStock);
		productRepository.saveProduct(product);
	}

}
