package com.application.API_E_commerce.infrastructure.exceptions.product;

public class StockUpdateException extends RuntimeException {

	private static final String DEFAULT_MESSAGE = "Stock update failed";

	public StockUpdateException () { super(DEFAULT_MESSAGE); }

	public StockUpdateException (String retryingStockUpdate) {
		super(retryingStockUpdate);
	}

	public StockUpdateException (String retryingStockUpdate, Exception e) {
		super(retryingStockUpdate, e);
	}

}
