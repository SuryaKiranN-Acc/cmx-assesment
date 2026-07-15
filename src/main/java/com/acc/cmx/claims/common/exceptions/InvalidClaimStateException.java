package com.acc.cmx.claims.common.exceptions;

import com.acc.cmx.claims.claim.entity.ClaimStatus;

/**
 * Thrown when a claim state transition is invalid.
 * Includes from/to states for clear identification of the invalid transition.
 */
public class InvalidClaimStateException extends RuntimeException {
    private final String claimId;
    private final ClaimStatus fromStatus;
    private final ClaimStatus toStatus;

    public InvalidClaimStateException(String claimId, ClaimStatus fromStatus, ClaimStatus toStatus) {
        super(String.format("Invalid state transition for claim %s: %s -> %s",
                claimId, fromStatus, toStatus));
        this.claimId = claimId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public String getClaimId() {
        return claimId;
    }

    public ClaimStatus getFromStatus() {
        return fromStatus;
    }

    public ClaimStatus getToStatus() {
        return toStatus;
    }
}
