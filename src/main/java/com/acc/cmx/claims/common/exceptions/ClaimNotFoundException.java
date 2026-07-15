package com.acc.cmx.claims.common.exceptions;

/**
 * Thrown when a claim is not found in the system.
 * Includes the claim ID that was not found for easy debugging.
 */
public class ClaimNotFoundException extends RuntimeException {
    private final String claimId;

    public ClaimNotFoundException(String claimId) {
        super("Claim not found with ID: " + claimId);
        this.claimId = claimId;
    }

    public ClaimNotFoundException(String claimId, String message) {
        super(message);
        this.claimId = claimId;
    }

    public String getClaimId() {
        return claimId;
    }
}
