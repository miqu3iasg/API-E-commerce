package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.application.usecases.PaymentUseCases;
import com.application.API_E_commerce.domain.payment.CheckoutSessionResponseDTO;
import com.application.API_E_commerce.domain.payment.Payment;
import com.application.API_E_commerce.domain.payment.PaymentIntentResponseDTO;
import com.application.API_E_commerce.response.ApiResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@Tag(name = "Payment", description = "Operations pertaining to payment processing")
public class PaymentController {

	private final PaymentUseCases paymentService;

	public PaymentController (PaymentUseCases paymentService) {
		this.paymentService = paymentService;
	}

	@Operation(
			summary = "Create a payment intent",
			description = "Processes a payment intent for the provided payment data",
			tags = {"Payment processing"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Payment intent created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid payment data or validation error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error or Stripe API failure",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping("/intent")
	public ResponseEntity<ApiResponse<PaymentIntentResponseDTO>> processPaymentIntent (
			@Parameter(description = "Payment data to process the intent", required = true)
			@RequestBody Payment paymentData) throws StripeException {
		PaymentIntent paymentIntent = paymentService.processPayment(paymentData);
		PaymentIntentResponseDTO response = new PaymentIntentResponseDTO(
				paymentIntent.getId(),
				paymentIntent.getStatus(),
				paymentIntent.getAmount(),
				paymentIntent.getCurrency(),
				paymentIntent.getClientSecret(),
				paymentIntent.getDescription()
		);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Payment intent created successfully", response, HttpStatus.CREATED));
	}

	@Operation(
			summary = "Create a checkout session",
			description = "Creates a checkout session for the provided payment data",
			tags = {"Payment processing"}
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Checkout session created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid payment data or validation error",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error or Stripe API failure",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@PostMapping("/checkout-session")
	public ResponseEntity<ApiResponse<CheckoutSessionResponseDTO>> createCheckoutSession (
			@Parameter(description = "Payment data to create the checkout session", required = true)
			@RequestBody Payment paymentData) throws StripeException {
		String checkoutSessionUrl = paymentService.createCheckoutSession(paymentData);
		CheckoutSessionResponseDTO response = new CheckoutSessionResponseDTO(checkoutSessionUrl);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Checkout session created successfully", response, HttpStatus.CREATED));
	}

}