package com.application.API_E_commerce.domain.refund;

public enum RefundStatus {
	PENDING("pending"),
	PROCESSING("processing"),
	COMPLETED("completed"),
	ACTION_REQUIRED("action_required"),
	FAILED("failed"),
	SUCCEEDED("succeeded"),
	UNKNOWN("unknown"),
	REJECTED("rejected");

	private final String status;

	RefundStatus (String status) { this.status = status; }

	public String getRefundStatus () {
		return status;
	}

}
