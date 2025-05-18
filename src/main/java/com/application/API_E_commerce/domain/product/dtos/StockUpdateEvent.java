package com.application.API_E_commerce.domain.product.dtos;

import java.util.UUID;

public record StockUpdateEvent(
		UUID productId,
		int quantity,
		OperationType operation,
		String correlationId
) {

	public enum OperationType {
		INCREASE, DECREASE
	}

}
