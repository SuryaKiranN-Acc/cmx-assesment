package com.acc.cmx.claims.claim.controller;

import com.acc.cmx.claims.api.WorkflowApi;
import com.acc.cmx.claims.api.model.ApproveClaimRequest;
import com.acc.cmx.claims.api.model.AssignClaimRequest;
import com.acc.cmx.claims.api.model.RejectClaimRequest;
import com.acc.cmx.claims.api.model.RequestInfoRequest;
import com.acc.cmx.claims.claim.service.ClaimService;
import com.acc.cmx.claims.common.exceptions.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WorkflowController implements WorkflowApi {
	private final ClaimService claimService;

	@Override
	public ResponseEntity<Void> approveClaim(String claimId, @Valid ApproveClaimRequest approveClaimRequest) {
		log.debug("API call: approveClaim requested for claimId: {}", claimId);
		if (approveClaimRequest.getApprovedAmount() == null || approveClaimRequest.getApprovedAmount() <= 0) {
			throw new InvalidRequestException("approvedAmount", "Amount must be positive");
		}
		claimService.approveClaim(claimId, approveClaimRequest);
		log.debug("API call: approveClaim completed for claimId: {}", claimId);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<Void> rejectClaim(String claimId, @Valid RejectClaimRequest rejectClaimRequest) {
		log.debug("API call: rejectClaim requested for claimId: {}", claimId);
		if (rejectClaimRequest.getReason() == null || rejectClaimRequest.getReason().isBlank()) {
			throw new InvalidRequestException("reason", "Rejection reason cannot be empty");
		}
		claimService.rejectClaim(claimId, rejectClaimRequest);
		log.debug("API call: rejectClaim completed for claimId: {}", claimId);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<Void> requestAdditionalInformation(String claimId, @Valid RequestInfoRequest requestInfoRequest) {
		log.debug("API call: requestAdditionalInformation requested for claimId: {}", claimId);
		if (requestInfoRequest.getReason() == null || requestInfoRequest.getReason().isBlank()) {
			throw new InvalidRequestException("reason", "Information request reason cannot be empty");
		}
		claimService.requestInfo(claimId, requestInfoRequest);
		log.debug("API call: requestAdditionalInformation completed for claimId: {}", claimId);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<Void> assignClaim(String claimId, @Valid AssignClaimRequest assignClaimRequest) {
		log.debug("API call: assignClaim requested for claimId: {}", claimId);
		if (assignClaimRequest.getOfficerId() == null || assignClaimRequest.getOfficerId().isBlank()) {
			throw new InvalidRequestException("officerId", "Officer ID cannot be empty");
		}
		claimService.assignClaim(claimId, assignClaimRequest);
		log.debug("API call: assignClaim completed for claimId: {}", claimId);
		return ResponseEntity.ok().build();
	}
}
