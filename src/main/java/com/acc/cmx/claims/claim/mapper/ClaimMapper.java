package com.acc.cmx.claims.claim.mapper;

import com.acc.cmx.claims.api.model.*;
import com.acc.cmx.claims.claim.entity.Claim;
import com.acc.cmx.claims.claim.entity.ClaimStatus;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClaimMapper {

    public Claim toEntity(CreateClaimRequest req) {
        return Claim.builder()
                .claimId(null)
                .claimantName(req.getClaimantName())
                .policyNumber(req.getPolicyNumber())
                .incidentDescription(req.getIncidentDescription())
                .estimatedAmount(req.getEstimatedAmount())
                .status(ClaimStatus.SUBMITTED)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    public ClaimResponse toResponse(Claim c) {
        ClaimResponse r = new ClaimResponse();
        r.setClaimId(c.getClaimId());
        r.setClaimantName(c.getClaimantName());
        r.setPolicyNumber(c.getPolicyNumber());
        r.setEstimatedAmount(c.getEstimatedAmount());
        r.setStatus(com.acc.cmx.claims.api.model.ClaimStatus.fromValue(c.getStatus().name()));
        return r;
    }

    public ClaimSummary toSummary(Claim c) {
        ClaimSummary s = new ClaimSummary();
        s.setClaimId(c.getClaimId());
        s.setClaimantName(c.getClaimantName());
        s.setStatus(com.acc.cmx.claims.api.model.ClaimStatus.fromValue(c.getStatus().name()));
        return s;
    }

    public List<ClaimSummary> toSummaries(List<Claim> claims) {
        return claims.stream().map(this::toSummary).collect(Collectors.toList());
    }
}
