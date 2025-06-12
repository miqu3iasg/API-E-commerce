package com.application.API_E_commerce.common.utils.enums;

public enum PaymentIntentStatus {
	REQUIRES_PAYMENT_METHOD("requires_payment_method"),
	REQUIRES_CONFIRMATION("requires_confirmation"),
	REQUIRES_ACTION("requires_action"),
	PROCESSING("processing"),
	REQUIRES_CAPTURE("requires_capture"),
	SUCCEEDED("succeeded"),
	CANCELED("canceled");

	private final String value;

	PaymentIntentStatus (String value) {
		this.value = value;
	}

	public static PaymentIntentStatus fromValue (String value) {
		for (PaymentIntentStatus status : PaymentIntentStatus.values())
			if (status.value.equalsIgnoreCase(value)) return status;
		throw new IllegalArgumentException("Unknown payment intent status: " + value);
	}

	public String getPaymentIntentStatus () {
		return value;
	}
}
