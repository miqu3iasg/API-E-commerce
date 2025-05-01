package com.application.API_E_commerce.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiResponse<T> {

	private int statusCode;
	private String message;
	private T data;
	private LocalDateTime timestamp;

	public ApiResponse (int statusCode, String message, T data, LocalDateTime timestamp) {
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
		this.timestamp = timestamp;
	}

	public static <T> ApiResponse<T> success (String message, T data, HttpStatus httpStatus) {
		return new ApiResponse<>(httpStatus.value(), message, data, LocalDateTime.now());
	}

	public static <T> ApiResponse<T> success (String message, HttpStatus httpStatus) {
		return new ApiResponse<>(httpStatus.value(), message, null, LocalDateTime.now());
	}

	public static <T> ApiResponse<T> error (String message, HttpStatus httpStatus, T data) {
		return new ApiResponse<>(httpStatus.value(), message, data, LocalDateTime.now());
	}

	public static <T> ApiResponse<T> error (String message, HttpStatus httpStatus) {
		return new ApiResponse<>(httpStatus.value(), message, null, LocalDateTime.now());
	}


	public int getStatusCode () {
		return statusCode;
	}

	public void setStatusCode (int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage () {
		return message;
	}

	public void setMessage (String message) {
		this.message = message;
	}

	public T getData () {
		return data;
	}

	public void setData (T data) {
		this.data = data;
	}

	public LocalDateTime getTimestamp () {
		return timestamp;
	}

	public void setTimestamp (LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
