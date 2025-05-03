package com.application.API_E_commerce.infrastructure.exceptions.order.orderItem;

import com.application.API_E_commerce.infrastructure.exceptions.NullParametersException;

public class MissingOrderItemIdException extends NullParametersException {

	private static final String DEFAULT_MESSAGE = "The order item id cannot be null.";

	public MissingOrderItemIdException () { super(DEFAULT_MESSAGE); }

	public MissingOrderItemIdException (String message, Throwable cause) { super(message, cause); }

	public MissingOrderItemIdException (String message) {
		super(message);
	}

}
