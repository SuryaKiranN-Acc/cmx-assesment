package com.acc.cmx.claims.notification.events;

import lombok.Value;

@Value
public class ClaimApprovedEvent {
    String claimId;
    Double approvedAmount;
}
