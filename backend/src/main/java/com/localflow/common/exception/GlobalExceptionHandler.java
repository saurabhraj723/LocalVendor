package com.localflow.common.exception;

import com.localflow.common.api.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiError> handleApiException(ApiException exception, HttpServletRequest request) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(toError(exception.getCode(), exception.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Request validation failed.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(toError("VALIDATION_ERROR", message, request));
    }

    private static ApiError toError(String code, String message, HttpServletRequest request) {
        String requestId = (String) request.getAttribute("requestId");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        return new ApiError(code, message, Instant.now(), requestId);
    }
}
