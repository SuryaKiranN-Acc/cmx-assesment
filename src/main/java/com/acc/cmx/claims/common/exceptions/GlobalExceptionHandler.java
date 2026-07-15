package com.acc.cmx.claims.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all controllers.
 * Ensures consistent error responses across the application.
 * Logs appropriate details based on exception type.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ClaimNotFoundException - Returns 404 Not Found
     */
    @ExceptionHandler(ClaimNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleClaimNotFound(ClaimNotFoundException e) {
        log.warn("Claim not found: {}", e.getClaimId());
        ErrorResponse error = new ErrorResponse("CLAIM_NOT_FOUND", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle InvalidClaimStateException - Returns 409 Conflict
     */
    @ExceptionHandler(InvalidClaimStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleInvalidClaimState(InvalidClaimStateException e) {
        log.warn("Invalid state transition for claim {}: {} -> {}", 
                e.getClaimId(), e.getFromStatus(), e.getToStatus());
        ErrorResponse error = new ErrorResponse(
                "INVALID_CLAIM_STATE",
                e.getMessage(),
                String.format("Cannot transition from %s to %s", e.getFromStatus(), e.getToStatus())
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle InvalidRequestException - Returns 400 Bad Request
     */
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException e) {
        log.warn("Invalid request: {}", e.getReason());
        ErrorResponse error = new ErrorResponse(
                "INVALID_REQUEST",
                e.getMessage(),
                e.getFieldName() != null ? "Field: " + e.getFieldName() : null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle WorkflowException - Returns 500 Internal Server Error
     */
    @ExceptionHandler(WorkflowException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleWorkflowException(WorkflowException e) {
        log.error("Workflow error during operation {} for claim {}: {}",
                e.getOperation(), e.getClaimId(), e.getMessage(), e);
        ErrorResponse error = new ErrorResponse(
                "WORKFLOW_ERROR",
                e.getMessage(),
                "Operation: " + e.getOperation()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle validation errors from @Valid annotation - Returns 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            fieldErrors.put(fieldName, message);
            log.warn("Validation error: field={}, message={}", fieldName, message);
        });
        
        ErrorResponse error = new ErrorResponse(
                "VALIDATION_FAILED",
                "Request validation failed",
                fieldErrors.toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other exceptions - Returns 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please contact support.",
                e.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
