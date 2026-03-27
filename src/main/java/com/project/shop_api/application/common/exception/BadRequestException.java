package com.project.shop_api.application.common.exception;

public class BadRequestException extends RuntimeException{
	public BadRequestException(String message) {
        super(message);
    }
}
