package com.application.API_E_commerce.domain.payment;

public enum PaymentMethod {
  CARD("card"),
  BOLETO("boleto"),
  PIX("pix"),
  PAYPAL("paypal"),
  APPLE_PAY("apple_pay"),
  GOOGLE_PAY("google_pay");

  private final String stripeMethod;

  PaymentMethod ( String stripeMethod ) {
    this.stripeMethod = stripeMethod;
  }

  public String getStripeMethod () {
    return stripeMethod;
  }
}
