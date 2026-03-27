package com.project.shop_api.infrastructure.rest.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldErrorResponse {
    private String field;
    private String message;
}
