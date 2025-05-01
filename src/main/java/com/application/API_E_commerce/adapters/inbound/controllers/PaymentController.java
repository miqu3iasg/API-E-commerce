package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.application.usecases.PaymentUseCases;
import com.application.API_E_commerce.domain.payment.CheckoutSessionResponseDTO;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentIntentResponseDTO;
import com.application.API_E_commerce.response.ApiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

  private final PaymentUseCases paymentService;

  public PaymentController ( PaymentUseCases paymentService ) {
    this.paymentService = paymentService;
  }

  @PostMapping("/intent")
  ResponseEntity<ApiResponse<PaymentIntentResponseDTO>> processPaymentIntent ( @RequestBody Payment paymentRequest ) throws StripeException {
    PaymentIntent paymentIntent = paymentService.processPayment(paymentRequest);

    PaymentIntentResponseDTO response = new PaymentIntentResponseDTO(
            paymentIntent.getId(),
            paymentIntent.getStatus(),
            paymentIntent.getAmount(),
            paymentIntent.getCurrency(),
            paymentIntent.getClientSecret(),
            paymentIntent.getDescription()
    );

    return ResponseEntity.ok(ApiResponse.success("Payment intent was process successfully.", response, HttpStatus.OK));
  }

  @PostMapping("/checkout-session")
  ResponseEntity<ApiResponse<CheckoutSessionResponseDTO>> createCheckoutSession ( @RequestBody Payment paymentRequest ) throws StripeException {
    String checkoutSessionUrl = paymentService.createCheckoutSession(paymentRequest);

    CheckoutSessionResponseDTO response = new CheckoutSessionResponseDTO(checkoutSessionUrl);

    return ResponseEntity.ok(ApiResponse.success("Checkout session was created successfully.", response, HttpStatus.OK));
  }

}
