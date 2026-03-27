package com.project.shop_api.infrastructure.rest.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiErrorResponse {

    private LocalDateTime timestamp;  // fecha exacta
    private String path;              // endpoint que falló
    private int status;               // HTTP status code (400, 404…)
    private String error;             // “Bad Request”, “Not Found”
    private String code;              // VALIDATION_ERROR, RESOURCE_NOT_FOUND…
    private String message;           // mensaje principal
    private List<FieldErrorResponse> details; // lista de errores de validación
    private String traceId;           // opcional
}