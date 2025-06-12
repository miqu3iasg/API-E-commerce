package com.application.API_E_commerce.adapters.inbound.controllers;

import com.application.API_E_commerce.adapters.inbound.dtos.RefundRequestDTO;
import com.application.API_E_commerce.adapters.inbound.dtos.RefundResponseDTO;
import com.application.API_E_commerce.common.response.ApiResponse;
import com.application.API_E_commerce.domain.refund.PaymentRefund;
import com.application.API_E_commerce.domain.refund.RefundStatus;
import com.application.API_E_commerce.domain.refund.useCase.RefundUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refund")
@Tag(name = "Refund", description = "Operations pertaining to refund processing")
public class RefundController {

	private final RefundUseCase refundService;

	public RefundController (RefundUseCase refundService) {
		this.refundService = refundService;
	}

	@Operation(
			summary = "Create a refund",
			description = "Processes a new refund for a completed payment"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "201",
					description = "Refund created successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid refund request data",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error during refund processing",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})

	@PostMapping("/payment")
	public ResponseEntity<ApiResponse<RefundResponseDTO>> createRefund (
			@Parameter(description = "Refund request data", required = true)
			@RequestBody RefundRequestDTO refundRequest) {
		try {
			PaymentRefund refund = refundService.createRefund(refundRequest);

			RefundResponseDTO response = new RefundResponseDTO(
					String.valueOf(refund.getId()),
					refund.getPaymentIntentId(),
					refund.getAmount(),
					refund.getReason(),
					refund.getStatus(),
					refund.getStripeRefundId()
			);

			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(ApiResponse.success("Refund created successfully", response, HttpStatus.CREATED));
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(
							"An unexpected error occurred while processing the refund: " + e.getMessage(),
							HttpStatus.INTERNAL_SERVER_ERROR)
					);
		}
	}

	@Operation(
			summary = "Get refund status",
			description = "Retrieves the status of a previously created refund by its ID"
	)
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Refund status retrieved successfully",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "Refund not found for the given ID",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "500",
					description = "Internal server error while retrieving refund status",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ApiResponse.class)
					)
			)
	})
	@GetMapping("/{refundId}/status")
	public ResponseEntity<ApiResponse<RefundStatus>> getRefundStatus (
			@Parameter(description = "Unique identifier for the refund", required = true)
			@PathVariable UUID refundId) {
		try {
			RefundStatus refundStatus = refundService.getRefundStatus(refundId);

			return ResponseEntity
					.ok(ApiResponse.success("Refund status retrieved successfully", refundStatus, HttpStatus.OK));
		} catch (Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(
							"An unexpected error occurred while retrieving the refund status: " + e.getMessage(),
							HttpStatus.INTERNAL_SERVER_ERROR)
					);
		}
	}

}
