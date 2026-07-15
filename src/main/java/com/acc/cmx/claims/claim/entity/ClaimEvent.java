package com.acc.cmx.claims.claim.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.*;

@Entity
@Table(name = "claim_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String claimId;
    private String eventType;
    private String fromStatus;
    private String toStatus;

    @Column(length = 4000)
    private String payload;

    private OffsetDateTime createdAt;
}
