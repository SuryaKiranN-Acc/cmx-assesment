package com.acc.cmx.claims.claim.service;

import com.acc.cmx.claims.api.model.*;
import com.acc.cmx.claims.claim.entity.Claim;
import com.acc.cmx.claims.claim.entity.ClaimEvent;
import com.acc.cmx.claims.claim.entity.ClaimStatus;
import com.acc.cmx.claims.claim.mapper.ClaimMapper;
import com.acc.cmx.claims.claim.repository.ClaimEventRepository;
import com.acc.cmx.claims.claim.repository.ClaimRepository;
import com.acc.cmx.claims.claim.repository.DashboardStatsProjection;
import com.acc.cmx.claims.claim.ClaimStateMachine;
import com.acc.cmx.claims.common.DomainEventPublisher;
import com.acc.cmx.claims.notification.events.AdditionalInfoReceivedEvent;
import com.acc.cmx.claims.notification.events.AdditionalInfoRequestedEvent;
import com.acc.cmx.claims.notification.events.ClaimApprovedEvent;
import com.acc.cmx.claims.notification.events.ClaimAssignedEvent;
import com.acc.cmx.claims.notification.events.ClaimRejectedEvent;
import com.acc.cmx.claims.notification.events.ClaimSubmittedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import com.acc.cmx.claims.common.exceptions.ClaimNotFoundException;
import com.acc.cmx.claims.common.exceptions.InvalidClaimStateException;

@Service
@RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final ClaimEventRepository claimEventRepository;
    private final ClaimMapper mapper;
    private final ClaimStateMachine stateMachine;
    private final DomainEventPublisher publisher;

    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest req) {
        Claim c = mapper.toEntity(req);
        c.setClaimId(UUID.randomUUID().toString());
        c.setStatus(ClaimStatus.SUBMITTED);
        c.setCreatedAt(OffsetDateTime.now());
        c.setUpdatedAt(OffsetDateTime.now());
        claimRepository.save(c);

        ClaimEvent ev = ClaimEvent.builder()
                .claimId(c.getClaimId())
                .eventType("ClaimSubmitted")
                .fromStatus(null)
                .toStatus(c.getStatus().name())
                .payload(null)
                .createdAt(OffsetDateTime.now())
                .build();
        claimEventRepository.save(ev);

        publisher.publish(new ClaimSubmittedEvent(c.getClaimId()));

        return mapper.toResponse(c);
    }

    @Transactional(readOnly = true)
    public ClaimResponse getClaim(String claimId) {
        return claimRepository.findById(claimId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ClaimNotFoundException(claimId));
    }

    @Transactional
    public void submitAdditionalInfo(String claimId, AdditionalInfoRequest req) {
        Claim c = claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException(claimId));
        ClaimStatus from = c.getStatus();
        if (from == ClaimStatus.INFO_REQUESTED) {
            try {
                stateMachine.validateTransition(from, ClaimStatus.INFO_RECEIVED);
            } catch (IllegalStateException e) {
                throw new InvalidClaimStateException(claimId, from, ClaimStatus.INFO_RECEIVED);
            }
            c.setStatus(ClaimStatus.INFO_RECEIVED);
        }
        c.setAdditionalInfo(req.getComments());
        c.setUpdatedAt(OffsetDateTime.now());
        claimRepository.save(c);

        ClaimEvent ev = ClaimEvent.builder()
                .claimId(claimId)
                .eventType("AdditionalInfoSubmitted")
                .fromStatus(from == null ? null : from.name())
                .toStatus(c.getStatus() == null ? null : c.getStatus().name())
                .payload(req.getComments())
                .createdAt(OffsetDateTime.now())
                .build();
        claimEventRepository.save(ev);

        publisher.publish(new AdditionalInfoReceivedEvent(claimId));
    }

    @Transactional(readOnly = true)
    public List<ClaimSummary> getUnassignedClaims() {
        List<Claim> list = claimRepository.findByAssignedOfficerIdIsNull();
        return mapper.toSummaries(list);
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        EnumSet<ClaimStatus> openStatuses = EnumSet.of(
                ClaimStatus.SUBMITTED,
                ClaimStatus.ASSIGNED,
                ClaimStatus.UNDER_REVIEW,
                ClaimStatus.INFO_REQUESTED,
                ClaimStatus.INFO_RECEIVED,
                ClaimStatus.APPROVED,
                ClaimStatus.SETTLEMENT_PENDING);
        DashboardStatsProjection stats = claimRepository.findDashboardStats(
                openStatuses,
                List.of(ClaimStatus.REJECTED, ClaimStatus.CLOSED, ClaimStatus.SETTLED));

        DashboardResponse r = new DashboardResponse();
        r.setTotalClaims((int) stats.getTotal());
        r.setOpenClaims((int) stats.getOpen());
        r.setApprovedClaims((int) stats.getApproved());
        r.setRejectedClaims((int) stats.getRejected());
        r.setOutstandingExposure(stats.getOutstandingExposure());
        return r;
    }

    @Transactional
    public void assignClaim(String claimId, AssignClaimRequest req) {
        Claim c = claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException(claimId));
        try {
            stateMachine.validateTransition(c.getStatus(), ClaimStatus.ASSIGNED);
        } catch (IllegalStateException e) {
            throw new InvalidClaimStateException(claimId, c.getStatus(), ClaimStatus.ASSIGNED);
        }
        ClaimStatus from = c.getStatus();
        c.setAssignedOfficerId(req.getOfficerId());
        c.setStatus(ClaimStatus.ASSIGNED);
        c.setUpdatedAt(OffsetDateTime.now());
        claimRepository.save(c);

        ClaimEvent ev = ClaimEvent.builder()
                .claimId(claimId)
                .eventType("ClaimAssigned")
                .fromStatus(from.name())
                .toStatus(c.getStatus().name())
                .payload(req.getOfficerId())
                .createdAt(OffsetDateTime.now())
                .build();
        claimEventRepository.save(ev);

        publisher.publish(new ClaimAssignedEvent(claimId, req.getOfficerId()));
    }

    @Transactional
    public void requestInfo(String claimId, RequestInfoRequest req) {
        Claim c = claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException(claimId));
        try {
            stateMachine.validateTransition(c.getStatus(), ClaimStatus.INFO_REQUESTED);
        } catch (IllegalStateException e) {
            throw new InvalidClaimStateException(claimId, c.getStatus(), ClaimStatus.INFO_REQUESTED);
        }
        ClaimStatus from = c.getStatus();
        c.setStatus(ClaimStatus.INFO_REQUESTED);
        c.setUpdatedAt(OffsetDateTime.now());
        claimRepository.save(c);

        ClaimEvent ev = ClaimEvent.builder()
                .claimId(claimId)
                .eventType("AdditionalInfoRequested")
                .fromStatus(from.name())
                .toStatus(c.getStatus().name())
                .payload(req.getReason())
                .createdAt(OffsetDateTime.now())
                .build();
        claimEventRepository.save(ev);

        publisher.publish(new AdditionalInfoRequestedEvent(claimId, req.getReason()));
    }

    @Transactional
    public void approveClaim(String claimId, ApproveClaimRequest req) {
        Claim c = claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException(claimId));
        try {
            stateMachine.validateTransition(c.getStatus(), ClaimStatus.APPROVED);
        } catch (IllegalStateException e) {
            throw new InvalidClaimStateException(claimId, c.getStatus(), ClaimStatus.APPROVED);
        }
        ClaimStatus from = c.getStatus();
        c.setApprovedAmount(req.getApprovedAmount());
        c.setStatus(ClaimStatus.APPROVED);
        c.setUpdatedAt(OffsetDateTime.now());
        claimRepository.save(c);

        ClaimEvent ev = ClaimEvent.builder()
                .claimId(claimId)
                .eventType("ClaimApproved")
                .fromStatus(from.name())
                .toStatus(c.getStatus().name())
                .payload(String.valueOf(req.getApprovedAmount()))
                .createdAt(OffsetDateTime.now())
                .build();
        claimEventRepository.save(ev);

        publisher.publish(new ClaimApprovedEvent(claimId, req.getApprovedAmount()));
    }

    @Transactional
    public void rejectClaim(String claimId, RejectClaimRequest req) {
        Claim c = claimRepository.findById(claimId).orElseThrow(() -> new ClaimNotFoundException(claimId));
        try {
            stateMachine.validateTransition(c.getStatus(), ClaimStatus.REJECTED);
        } catch (IllegalStateException e) {
            throw new InvalidClaimStateException(claimId, c.getStatus(), ClaimStatus.REJECTED);
        }
        ClaimStatus from = c.getStatus();
        c.setRejectionReason(req.getReason());
        c.setStatus(ClaimStatus.REJECTED);
        c.setUpdatedAt(OffsetDateTime.now());
        claimRepository.save(c);

        ClaimEvent ev = ClaimEvent.builder()
                .claimId(claimId)
                .eventType("ClaimRejected")
                .fromStatus(from.name())
                .toStatus(c.getStatus().name())
                .payload(req.getReason())
                .createdAt(OffsetDateTime.now())
                .build();
        claimEventRepository.save(ev);

        publisher.publish(new ClaimRejectedEvent(claimId, req.getReason()));
    }

    @Transactional
    public void changeClaimStatus(String claimId, ChangeStatusRequest req) {

        Claim c = claimRepository.findById(claimId)
                .orElseThrow(() -> new ClaimNotFoundException(claimId));

        ClaimStatus targetStatus = ClaimStatus.valueOf(req.getTargetStatus().name());

        try {
            stateMachine.validateTransition(c.getStatus(), targetStatus);
        } catch (IllegalStateException e) {
            throw new InvalidClaimStateException(
                    claimId,
                    c.getStatus(),
                    targetStatus);
        }

        ClaimStatus from = c.getStatus();

        c.setStatus(targetStatus);
        c.setUpdatedAt(OffsetDateTime.now());

        claimRepository.save(c);

        ClaimEvent event = ClaimEvent.builder()
                .claimId(claimId)
                .eventType("ClaimStatusChanged")
                .fromStatus(from.name())
                .toStatus(targetStatus.name())
                .payload(req.getComments())
                .createdAt(OffsetDateTime.now())
                .build();

        claimEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<ClaimEventResponse> getClaimTimeline(String claimId) {
        try {
            if (!claimRepository.existsById(claimId)) {
                throw new ClaimNotFoundException(claimId);
            }

            List<ClaimEvent> events = claimEventRepository.findByClaimIdOrderByCreatedAtAsc(claimId);

            return events.stream()
                    .map(mapper::mapToClaimEventResponse)
                    .toList();

        } catch (ClaimNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Unable to retrieve claim timeline for claimId: " + claimId,
                    ex);
        }
    }
}
