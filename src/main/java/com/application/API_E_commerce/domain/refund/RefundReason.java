package com.application.API_E_commerce.domain.refund;

public enum RefundReason {
	DUPLICATED("duplicated"),
	FRAUDULENT("fraudulent"),
	REQUESTED_BY_CUSTOMER("requested_by_customer"),
	NONE("none");

	private final String status;

	RefundReason (String status) {
		this.status = status;
	}

	public String getRefundReasonStatus () {
		return status;
	}
}
