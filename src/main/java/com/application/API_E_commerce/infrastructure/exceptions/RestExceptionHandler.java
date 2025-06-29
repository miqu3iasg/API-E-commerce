package com.application.API_E_commerce.infrastructure.exceptions;

import com.application.API_E_commerce.common.response.ApiError;
import com.application.API_E_commerce.common.response.ApiResponse;
import com.application.API_E_commerce.infrastructure.exceptions.address.AddressNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.cart.EmptyCartException;
import com.application.API_E_commerce.infrastructure.exceptions.cart.InvalidCartException;
import com.application.API_E_commerce.infrastructure.exceptions.cart.UserCartNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryAlreadyExistsException;
import com.application.API_E_commerce.infrastructure.exceptions.category.CategoryNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.order.EmptyOrderException;
import com.application.API_E_commerce.infrastructure.exceptions.order.OrderNotFoundException;
import com.application.API_E_commerce.infrastructure.exceptions.order.OrderProcessingException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.CreatingCheckoutSessionException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.InvalidAmountException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.InvalidPaymentMethodException;
import com.application.API_E_commerce.infrastructure.exceptions.payment.ProcessingPaymentException;
import com.application.API_E_commerce.infrastructure.exceptions.product.*;
import com.application.API_E_commerce.infrastructure.exceptions.user.InvalidUserPasswordException;
import com.application.API_E_commerce.infrastructure.exceptions.user.InvalidUserRoleException;
import com.application.API_E_commerce.infrastructure.exceptions.user.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({
			UserNotFoundException.class,
			ProductNotFoundException.class,
			ProductImageNotFoundException.class,
			OrderNotFoundException.class,
			UserCartNotFoundException.class,
			CategoryNotFoundException.class,
			AddressNotFoundException.class
	})
	public ResponseEntity<ApiResponse<ApiError>> handleNotFoundExceptions (
			RuntimeException ex,
			HttpServletRequest request
	) {
		return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<ApiResponse<ApiError>> buildErrorResponse (
			Exception ex,
			HttpServletRequest request,
			HttpStatus status
	) {
		var error = new ApiError(request.getRequestURI(), status.getReasonPhrase());

		var response = ApiResponse.error(ex.getMessage(), status, error);

		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler({
			ProductOutOfStockException.class,
			CategoryAlreadyExistsException.class,
			InvalidUserPasswordException.class,
			InvalidUserRoleException.class,
			InvalidPriceException.class,
			InvalidQuantityException.class,
			InvalidAmountException.class,
			InvalidPaymentMethodException.class,
			InvalidCartException.class,
			EmptyCartException.class,
			EmptyOrderException.class,
			NullParametersException.class
	})
	public ResponseEntity<ApiResponse<ApiError>> handleBadRequestExceptions (
			RuntimeException ex,
			HttpServletRequest request
	) {
		return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({
			OrderProcessingException.class,
			CreatingCheckoutSessionException.class,
			ProcessingPaymentException.class
	})
	public ResponseEntity<ApiResponse<ApiError>> handleServerExceptions (
			RuntimeException ex,
			HttpServletRequest request
	) {
		return buildErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ApiResponse<ApiError>> handleAllUncaughtExceptions (Exception ex, WebRequest request) {
		ApiError error = new ApiError(
				request.getContextPath(),
				ex.getMessage()
		);

		var response = ApiResponse.error(ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR, error);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

}

