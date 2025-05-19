package com.application.API_E_commerce.domain.stock.services;

import com.application.API_E_commerce.adapters.inbound.dtos.StockUpdateEvent;
import com.application.API_E_commerce.common.utils.validators.ProductValidator;
import com.application.API_E_commerce.domain.product.Product;
import com.application.API_E_commerce.domain.product.repository.ProductRepositoryPort;
import com.application.API_E_commerce.domain.stock.StockUpdatePort;
import com.application.API_E_commerce.domain.stock.useCase.StockUseCase;
import com.application.API_E_commerce.infrastructure.exceptions.product.InvalidOperationTypeException;
import com.application.API_E_commerce.infrastructure.exceptions.product.ProductOutOfStockException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockService implements StockUseCase {

	private final ProductRepositoryPort productRepositoryPort;
	private final StockUpdatePort stockUpdatePort;
	private final ProductValidator productValidator;

	public StockService (ProductRepositoryPort productRepositoryPort,
	                     StockUpdatePort stockUpdatePort, ProductValidator productValidator) {
		this.productRepositoryPort = productRepositoryPort;
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
		productRepositoryPort.saveProduct(product);
	}

}
