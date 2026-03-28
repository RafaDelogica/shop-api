package com.project.shop_api.infrastructure.rest.exception;



import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.shop_api.application.common.exception.BadRequestException;
import com.project.shop_api.application.common.exception.ConflictException;
import com.project.shop_api.application.common.exception.ForbiddenException;
import com.project.shop_api.application.common.exception.ResourceNotFoundException;
import com.project.shop_api.application.common.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiErrorResponse buildError(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<FieldErrorResponse> details,
            String traceId
    ) {

        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .details(details)
                .traceId(traceId)
                .build();
    }


    // ============================================================
    // 400 - VALIDATION ERROR (Jakarta Validation @Valid)
    // ============================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        log.warn("VALIDATION_ERROR in {}", request.getRequestURI());

        List<FieldErrorResponse> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> FieldErrorResponse.builder()
                        .field(err.getField())
                        .message(err.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(
                buildError(
                        HttpStatus.BAD_REQUEST,
                        "VALIDATION_ERROR",
                        "The data submitted has not passed validation",
                        request,
                        details,
                        null
                )
        );
    }


    // ============================================================
    // 400 - VALIDATION ERROR (dominio)
    // ============================================================
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainValidation(
            ValidationException ex,
            HttpServletRequest request
    ) {

        log.warn("VALIDATION_ERROR (domain) in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.badRequest().body(
                buildError(
                        HttpStatus.BAD_REQUEST,
                        "VALIDATION_ERROR",
                        ex.getMessage(),
                        request,
                        null,
                        null
                )
        );
    }


    // ============================================================
    // 404 - RESOURCE NOT FOUND
    // ============================================================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        log.warn("RESOURCE_NOT_FOUND in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildError(
                        HttpStatus.NOT_FOUND,
                        "RESOURCE_NOT_FOUND",
                        ex.getMessage(),
                        request,
                        null,
                        null
                )
        );
    }


    // ============================================================
    // 409 - CONFLICT
    // ============================================================
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {

        log.warn("CONFLICT in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError(
                        HttpStatus.CONFLICT,
                        "CONFLICT",
                        ex.getMessage(),
                        request,
                        null,
                        null
                )
        );
    }


    // ============================================================
    // 403 - FORBIDDEN
    // ============================================================
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            ForbiddenException ex,
            HttpServletRequest request
    ) {

        log.warn("FORBIDDEN in {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildError(
                        HttpStatus.FORBIDDEN,
                        "FORBIDDEN",
                        ex.getMessage(),
                        request,
                        null,
                        null
                )
        );
    }


    // ============================================================
    // 400 - BAD REQUEST (customized)
    // ============================================================
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                buildError(
                        HttpStatus.BAD_REQUEST,
                        "BAD_REQUEST",
                        ex.getMessage(),
                        request,
                        null,
                        null
                )
        );
    }


    // ============================================================
    // 500 - INTERNAL ERROR
    // ============================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("INTERNAL_ERROR in {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "INTERNAL_ERROR",
                        "Internal error from server",
                        request,
                        null,
                        null
                )
        );
    }

}