package com.application.API_E_commerce.adapters.inbound.dtos;

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
