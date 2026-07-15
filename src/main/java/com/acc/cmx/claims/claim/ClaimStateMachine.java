package com.acc.cmx.claims.claim;

import com.acc.cmx.claims.claim.entity.ClaimStatus;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Component
public class ClaimStateMachine {
    
    private static final Map<ClaimStatus, Set<ClaimStatus>> STATE_TRANSITIONS;
    
    static {
        Map<ClaimStatus, Set<ClaimStatus>> transitions = new EnumMap<>(ClaimStatus.class);
        transitions.put(ClaimStatus.SUBMITTED, Set.of(ClaimStatus.ASSIGNED));
        transitions.put(ClaimStatus.ASSIGNED, Set.of(ClaimStatus.UNDER_REVIEW));
        transitions.put(ClaimStatus.UNDER_REVIEW, Set.of(ClaimStatus.INFO_REQUESTED, ClaimStatus.APPROVED, ClaimStatus.REJECTED));
        transitions.put(ClaimStatus.INFO_REQUESTED, Set.of(ClaimStatus.INFO_RECEIVED));
        transitions.put(ClaimStatus.INFO_RECEIVED, Set.of(ClaimStatus.UNDER_REVIEW));
        transitions.put(ClaimStatus.APPROVED, Set.of(ClaimStatus.SETTLEMENT_PENDING));
        transitions.put(ClaimStatus.SETTLEMENT_PENDING, Set.of(ClaimStatus.SETTLED));
        transitions.put(ClaimStatus.REJECTED, Set.of(ClaimStatus.CLOSED));
        transitions.put(ClaimStatus.SETTLED, Set.of(ClaimStatus.CLOSED));
        transitions.put(ClaimStatus.CLOSED, Collections.emptySet());
        STATE_TRANSITIONS = Collections.unmodifiableMap(transitions);
    }

    public void validateTransition(ClaimStatus from, ClaimStatus to) {
        if (from == null) {
            if (to == ClaimStatus.SUBMITTED) return;
            fail(from, to);
        }
        if (from == to) return;
        
        Set<ClaimStatus> allowedTransitions = STATE_TRANSITIONS.getOrDefault(from, Collections.emptySet());
        if (!allowedTransitions.contains(to)) {
            fail(from, to);
        }
    }

    private void fail(ClaimStatus from, ClaimStatus to) {
        throw new IllegalStateException("Invalid transition from " + from + " to " + to);
    }
}
