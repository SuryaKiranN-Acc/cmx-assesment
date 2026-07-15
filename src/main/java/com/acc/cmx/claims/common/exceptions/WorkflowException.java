package com.acc.cmx.claims.common.exceptions;

/**
 * Thrown when a workflow-related operation fails.
 * Includes operation type and claim ID for easy identification.
 */
public class WorkflowException extends RuntimeException {
    private final String operation;
    private final String claimId;

    public WorkflowException(String operation, String claimId, String message) {
        super(String.format("Workflow error during %s for claim %s: %s",
                operation, claimId, message));
        this.operation = operation;
        this.claimId = claimId;
    }

    public WorkflowException(String operation, String claimId, Throwable cause) {
        super(String.format("Workflow error during %s for claim %s: %s",
                operation, claimId, cause.getMessage()), cause);
        this.operation = operation;
        this.claimId = claimId;
    }

    public String getOperation() {
        return operation;
    }

    public String getClaimId() {
        return claimId;
    }
}
