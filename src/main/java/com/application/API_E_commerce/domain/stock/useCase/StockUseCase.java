package com.application.API_E_commerce.domain.stock.useCase;

import java.util.UUID;

public interface StockUseCase {

	void increaseProductStock (UUID productId, int quantityToIncrease);

	void decreaseProductStock (UUID productId, int quantityToDecrease);

}
