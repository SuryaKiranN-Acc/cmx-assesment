package com.acc.cmx.claims.common.exceptions;

/**
 * Thrown when an invalid request is received.
 * Includes the field name and reason for the validation failure.
 */
public class InvalidRequestException extends RuntimeException {
    private final String fieldName;
    private final String reason;

    public InvalidRequestException(String fieldName, String reason) {
        super(String.format("Invalid request: %s - %s", fieldName, reason));
        this.fieldName = fieldName;
        this.reason = reason;
    }

    public InvalidRequestException(String message) {
        super("Invalid request: " + message);
        this.fieldName = null;
        this.reason = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getReason() {
        return reason;
    }
}
