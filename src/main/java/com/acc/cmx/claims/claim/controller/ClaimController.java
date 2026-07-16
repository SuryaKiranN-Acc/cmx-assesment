package com.acc.cmx.claims.claim.controller;

import com.acc.cmx.claims.api.ClaimsApi;
import com.acc.cmx.claims.api.model.AdditionalInfoRequest;
import com.acc.cmx.claims.api.model.ClaimEventResponse;
import com.acc.cmx.claims.api.model.ClaimResponse;
import com.acc.cmx.claims.api.model.CreateClaimRequest;
import com.acc.cmx.claims.api.model.DashboardResponse;
import com.acc.cmx.claims.claim.service.ClaimService;
import com.acc.cmx.claims.common.exceptions.ClaimNotFoundException;
import com.acc.cmx.claims.common.exceptions.InvalidRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClaimController implements ClaimsApi {
    private final ClaimService claimService;

    @Override
    public ResponseEntity<DashboardResponse> getDashboard() {
        log.debug("API call: getDashboard requested");
        DashboardResponse response = claimService.getDashboard();
        log.debug("API call: getDashboard completed successfully");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ClaimResponse> createClaim(@Valid CreateClaimRequest createClaimRequest) {
        log.debug("API call: createClaim requested");
        if (createClaimRequest == null) {
            throw new InvalidRequestException("request body", "Create claim request cannot be null");
        }
        ClaimResponse resp = claimService.createClaim(createClaimRequest);
        log.debug("API call: createClaim completed with claimId: {}", resp.getClaimId());
        return ResponseEntity.status(201).body(resp);
    }

    @Override
    public ResponseEntity<ClaimResponse> getClaim(String claimId) {
        log.debug("API call: getClaim requested for claimId: {}", claimId);
        if (claimId == null || claimId.isBlank()) {
            throw new InvalidRequestException("claimId", "Claim ID cannot be empty");
        }
        ClaimResponse r = claimService.getClaim(claimId);
        if (r == null) {
            log.debug("API call: getClaim - claim not found for claimId: {}", claimId);
            throw new ClaimNotFoundException(claimId);
        }
        log.debug("API call: getClaim completed for claimId: {}", claimId);
        return ResponseEntity.ok(r);
    }

    @Override
    public ResponseEntity<Void> submitAdditionalInformation(String claimId, @Valid AdditionalInfoRequest additionalInfoRequest) {
        log.debug("API call: submitAdditionalInformation requested for claimId: {}", claimId);
        if (claimId == null || claimId.isBlank()) {
            throw new InvalidRequestException("claimId", "Claim ID cannot be empty");
        }
        if (additionalInfoRequest == null) {
            throw new InvalidRequestException("request body", "Additional info request cannot be null");
        }
        claimService.submitAdditionalInfo(claimId, additionalInfoRequest);
        log.debug("API call: submitAdditionalInformation completed for claimId: {}", claimId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ClaimEventResponse>> getClaimTimeline(String claimId) {
        List<ClaimEventResponse> timeline = claimService.getClaimTimeline(claimId);
        return ResponseEntity.ok(timeline);
    }
}
