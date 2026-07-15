package com.acc.cmx.claims.claim;

import com.acc.cmx.claims.claim.entity.ClaimStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClaimStateMachineTest {
    private final ClaimStateMachine machine = new ClaimStateMachine();

    @Test
    void validTransitions() {
        machine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.ASSIGNED);
        machine.validateTransition(ClaimStatus.ASSIGNED, ClaimStatus.UNDER_REVIEW);
        machine.validateTransition(ClaimStatus.UNDER_REVIEW, ClaimStatus.APPROVED);
    }

    @Test
    void invalidTransitionThrows() {
        assertThrows(IllegalStateException.class, () -> machine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.APPROVED));
    }

    @Test
    void nullFromStatusCreationTransition() {
        machine.validateTransition(null, ClaimStatus.SUBMITTED);
    }

    @Test
    void nullFromStatusInvalidTransition() {
        assertThrows(IllegalStateException.class, () -> machine.validateTransition(null, ClaimStatus.APPROVED));
    }

    @Test
    void sameStatusTransition() {
        machine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.SUBMITTED);
    }

    @Test
    void submittedToAssigned() {
        machine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.ASSIGNED);
    }

    @Test
    void submittedToUnderReviewInvalid() {
        assertThrows(IllegalStateException.class, () -> machine.validateTransition(ClaimStatus.SUBMITTED, ClaimStatus.UNDER_REVIEW));
    }

    @Test
    void underReviewToMultipleEndpoints() {
        machine.validateTransition(ClaimStatus.UNDER_REVIEW, ClaimStatus.INFO_REQUESTED);
        machine.validateTransition(ClaimStatus.UNDER_REVIEW, ClaimStatus.APPROVED);
        machine.validateTransition(ClaimStatus.UNDER_REVIEW, ClaimStatus.REJECTED);
    }

    @Test
    void infoRequestedToInfoReceived() {
        machine.validateTransition(ClaimStatus.INFO_REQUESTED, ClaimStatus.INFO_RECEIVED);
    }

    @Test
    void infoReceivedBackToUnderReview() {
        machine.validateTransition(ClaimStatus.INFO_RECEIVED, ClaimStatus.UNDER_REVIEW);
    }

    @Test
    void approvedToSettlementPending() {
        machine.validateTransition(ClaimStatus.APPROVED, ClaimStatus.SETTLEMENT_PENDING);
    }

    @Test
    void settlementPendingToSettled() {
        machine.validateTransition(ClaimStatus.SETTLEMENT_PENDING, ClaimStatus.SETTLED);
    }

    @Test
    void rejectedToClosed() {
        machine.validateTransition(ClaimStatus.REJECTED, ClaimStatus.CLOSED);
    }

    @Test
    void settledToClosed() {
        machine.validateTransition(ClaimStatus.SETTLED, ClaimStatus.CLOSED);
    }

    @Test
    void closedStatusNoTransitions() {
        assertThrows(IllegalStateException.class, () -> machine.validateTransition(ClaimStatus.CLOSED, ClaimStatus.SUBMITTED));
    }

    @Test
    void invalidTransitionFromApprovedToRejected() {
        assertThrows(IllegalStateException.class, () -> machine.validateTransition(ClaimStatus.APPROVED, ClaimStatus.REJECTED));
    }
}
