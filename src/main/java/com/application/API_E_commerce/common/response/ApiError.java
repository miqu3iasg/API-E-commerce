package com.application.API_E_commerce.common.response;

import java.time.LocalDateTime;

public class ApiError {

	private String path;
	private String error;
	private LocalDateTime timestamp;

	public ApiError (String path, String error) {
		this.path = path;
		this.error = error;
		timestamp = LocalDateTime.now();
	}

	public String getPath () {
		return path;
	}

	public void setPath (String path) {
		this.path = path;
	}

	public String getError () {
		return error;
	}

	public void setError (String error) {
		this.error = error;
	}

	public LocalDateTime getTimestamp () {
		return timestamp;
	}

	public void setTimestamp (LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
