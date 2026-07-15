package com.acc.cmx.claims.claim;

import com.acc.cmx.claims.claim.entity.ClaimStatus;
import org.springframework.stereotype.Component;

@Component
public class ClaimStateMachine {

    public void validateTransition(ClaimStatus from, ClaimStatus to) {
        if (from == null) {
            if (to == ClaimStatus.SUBMITTED) return; // creation
            fail(from, to);
        }
        if (from == to) return;

        switch (from) {
            case SUBMITTED:
                if (to != ClaimStatus.ASSIGNED) fail(from, to);
                break;
            case ASSIGNED:
                if (to != ClaimStatus.UNDER_REVIEW) fail(from, to);
                break;
            case UNDER_REVIEW:
                if (to != ClaimStatus.INFO_REQUESTED && to != ClaimStatus.APPROVED && to != ClaimStatus.REJECTED) fail(from, to);
                break;
            case INFO_REQUESTED:
                if (to != ClaimStatus.INFO_RECEIVED) fail(from, to);
                break;
            case INFO_RECEIVED:
                if (to != ClaimStatus.UNDER_REVIEW) fail(from, to);
                break;
            case APPROVED:
                if (to != ClaimStatus.SETTLEMENT_PENDING) fail(from, to);
                break;
            case SETTLEMENT_PENDING:
                if (to != ClaimStatus.SETTLED) fail(from, to);
                break;
            case REJECTED:
                if (to != ClaimStatus.CLOSED) fail(from, to);
                break;
            case SETTLED:
                if (to != ClaimStatus.CLOSED) fail(from, to);
                break;
            case CLOSED:
                fail(from, to);
                break;
            default:
                break;
        }
    }

    private void fail(ClaimStatus from, ClaimStatus to) {
        throw new IllegalStateException("Invalid transition from " + from + " to " + to);
    }
}
