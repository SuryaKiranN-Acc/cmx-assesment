package com.acc.cmx.claims.notification.events;

import lombok.Value;

@Value
public class ClaimSubmittedEvent {
    String claimId;
}
