package com.acc.cmx.claims.claim.entity;

import com.acc.cmx.claims.claim.entity.ClaimStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.*;

@Entity
@Table(name = "claim")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {
    @Id
    private String claimId;

    private String claimantName;
    private String policyNumber;
    @Column(length = 2000)
    private String incidentDescription;

    private Double estimatedAmount;
    private Double approvedAmount;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private String assignedOfficerId;

    @Column(length = 2000)
    private String additionalInfo;

    @Column(length = 1000)
    private String rejectionReason;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
