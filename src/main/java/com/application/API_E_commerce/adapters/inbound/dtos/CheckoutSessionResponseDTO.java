package com.application.API_E_commerce.adapters.inbound.dtos;

public class CheckoutSessionResponseDTO {

	private String checkoutUrl;

	public CheckoutSessionResponseDTO (String checkoutUrl) {
		this.checkoutUrl = checkoutUrl;
	}

	public String getCheckoutUrl () {
		return checkoutUrl;
	}

	public void setCheckoutUrl (String checkoutUrl) {
		this.checkoutUrl = checkoutUrl;
	}

}
