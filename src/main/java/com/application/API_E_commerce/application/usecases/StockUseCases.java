package com.application.API_E_commerce.application.usecases;

import java.util.UUID;

public interface StockUseCases {

	void increaseProductStock (UUID productId, int quantityToIncrease);

	void decreaseProductStock (UUID productId, int quantityToDecrease);

}
