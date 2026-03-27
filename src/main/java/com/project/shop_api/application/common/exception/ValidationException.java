package com.project.shop_api.application.common.exception;

public class ValidationException extends RuntimeException{
	public ValidationException(String message) {
        super(message);
    }
}
