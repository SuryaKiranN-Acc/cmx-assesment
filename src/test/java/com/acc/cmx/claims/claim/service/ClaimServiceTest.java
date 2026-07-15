package com.acc.cmx.claims.claim.service;

import com.acc.cmx.claims.api.model.*;
import com.acc.cmx.claims.claim.ClaimStateMachine;
import com.acc.cmx.claims.claim.entity.Claim;
import com.acc.cmx.claims.claim.entity.ClaimEvent;
import com.acc.cmx.claims.claim.entity.ClaimStatus;
import com.acc.cmx.claims.claim.mapper.ClaimMapper;
import com.acc.cmx.claims.claim.repository.ClaimEventRepository;
import com.acc.cmx.claims.claim.repository.ClaimRepository;
import com.acc.cmx.claims.claim.repository.DashboardStatsProjection;
import com.acc.cmx.claims.common.DomainEventPublisher;
import com.acc.cmx.claims.common.exceptions.InvalidClaimStateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {
    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimEventRepository claimEventRepository;

    @Mock
    private ClaimMapper mapper;

    @Mock
    private ClaimStateMachine stateMachine;

    @Mock
    private DomainEventPublisher publisher;

    @Mock
    private DashboardStatsProjection dashboardStats;

    @InjectMocks
    private ClaimService claimService;

    private CreateClaimRequest createRequest;
    private Claim testClaim;
    private ClaimResponse testResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateClaimRequest();
        createRequest.setClaimantName("John Doe");
        createRequest.setPolicyNumber("POL-1001");
        createRequest.setIncidentDescription("Car accident");
        createRequest.setEstimatedAmount(5000.0);

        testClaim = Claim.builder()
                .claimId("CLAIM-001")
                .claimantName("John Doe")
                .policyNumber("POL-1001")
                .status(ClaimStatus.SUBMITTED)
                .createdAt(OffsetDateTime.now())
                .build();

        testResponse = new ClaimResponse();
        testResponse.setClaimId("CLAIM-001");
        testResponse.setClaimantName("John Doe");
    }

    @Test
    void createClaimSuccessfully() {
        when(mapper.toEntity(createRequest)).thenReturn(testClaim);
        when(mapper.toResponse(any(Claim.class))).thenReturn(testResponse);
        when(claimRepository.save(any(Claim.class))).thenReturn(testClaim);

        ClaimResponse result = claimService.createClaim(createRequest);

        assertNotNull(result);
        assertEquals("John Doe", result.getClaimantName());
        verify(claimRepository, times(1)).save(any(Claim.class));
        verify(claimEventRepository, times(1)).save(any(ClaimEvent.class));
        verify(publisher, times(1)).publish(any());
    }

    @Test
    void getClaimReturnsExistingClaim() {
        when(claimRepository.findById("CLAIM-001")).thenReturn(Optional.of(testClaim));
        when(mapper.toResponse(testClaim)).thenReturn(testResponse);

        ClaimResponse result = claimService.getClaim("CLAIM-001");

        assertNotNull(result);
        assertEquals("John Doe", result.getClaimantName());
        verify(claimRepository, times(1)).findById("CLAIM-001");
    }

    @Test
    void assignClaimSuccessfully() {
        AssignClaimRequest assignRequest = new AssignClaimRequest();
        assignRequest.setOfficerId("OFFICER-01");

        Claim assignedClaim = Claim.builder()
                .claimId("CLAIM-001")
                .status(ClaimStatus.SUBMITTED)
                .build();

        when(claimRepository.findById("CLAIM-001")).thenReturn(Optional.of(assignedClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(assignedClaim);

        claimService.assignClaim("CLAIM-001", assignRequest);

        assertEquals("OFFICER-01", assignedClaim.getAssignedOfficerId());
        assertEquals(ClaimStatus.ASSIGNED, assignedClaim.getStatus());
        verify(claimRepository, times(1)).save(any(Claim.class));
        verify(claimEventRepository, times(1)).save(any(ClaimEvent.class));
    }

    @Test
    void approvClaimSuccessfully() {
        ApproveClaimRequest approveRequest = new ApproveClaimRequest();
        approveRequest.setApprovedAmount(4500.0);

        Claim approveClaim = Claim.builder()
                .claimId("CLAIM-001")
                .status(ClaimStatus.UNDER_REVIEW)
                .build();

        when(claimRepository.findById("CLAIM-001")).thenReturn(Optional.of(approveClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(approveClaim);

        claimService.approveClaim("CLAIM-001", approveRequest);

        assertEquals(4500.0, approveClaim.getApprovedAmount());
        assertEquals(ClaimStatus.APPROVED, approveClaim.getStatus());
        verify(claimRepository, times(1)).save(any(Claim.class));
        verify(claimEventRepository, times(1)).save(any(ClaimEvent.class));
    }

    @Test
    void rejectClaimSuccessfully() {
        RejectClaimRequest rejectRequest = new RejectClaimRequest();
        rejectRequest.setReason("Insufficient documentation");

        Claim rejectClaim = Claim.builder()
                .claimId("CLAIM-001")
                .status(ClaimStatus.UNDER_REVIEW)
                .build();

        when(claimRepository.findById("CLAIM-001")).thenReturn(Optional.of(rejectClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(rejectClaim);

        claimService.rejectClaim("CLAIM-001", rejectRequest);

        assertEquals("Insufficient documentation", rejectClaim.getRejectionReason());
        assertEquals(ClaimStatus.REJECTED, rejectClaim.getStatus());
        verify(claimRepository, times(1)).save(any(Claim.class));
        verify(claimEventRepository, times(1)).save(any(ClaimEvent.class));
    }

    @Test
    void requestAdditionalInfoSuccessfully() {
        RequestInfoRequest infoRequest = new RequestInfoRequest();
        infoRequest.setReason("Need accident report");

        Claim infoClaim = Claim.builder()
                .claimId("CLAIM-001")
                .status(ClaimStatus.UNDER_REVIEW)
                .build();

        when(claimRepository.findById("CLAIM-001")).thenReturn(Optional.of(infoClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(infoClaim);

        claimService.requestInfo("CLAIM-001", infoRequest);

        assertEquals(ClaimStatus.INFO_REQUESTED, infoClaim.getStatus());
        verify(claimRepository, times(1)).save(any(Claim.class));
        verify(claimEventRepository, times(1)).save(any(ClaimEvent.class));
    }

    @Test
    void submitAdditionalInfoTransitionsToReceived() {
        AdditionalInfoRequest additionalRequest = new AdditionalInfoRequest();
        additionalRequest.setComments("Attached report");

        Claim infoClaim = Claim.builder()
                .claimId("CLAIM-001")
                .status(ClaimStatus.INFO_REQUESTED)
                .build();

        when(claimRepository.findById("CLAIM-001")).thenReturn(Optional.of(infoClaim));
        when(claimRepository.save(any(Claim.class))).thenReturn(infoClaim);

        claimService.submitAdditionalInfo("CLAIM-001", additionalRequest);

        assertEquals(ClaimStatus.INFO_RECEIVED, infoClaim.getStatus());
        assertEquals("Attached report", infoClaim.getAdditionalInfo());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void getDashboardReturnsMetrics() {
        when(dashboardStats.getTotal()).thenReturn(10L);
        when(dashboardStats.getOpen()).thenReturn(5L);
        when(dashboardStats.getApproved()).thenReturn(3L);
        when(dashboardStats.getRejected()).thenReturn(2L);
        when(dashboardStats.getOutstandingExposure()).thenReturn(50000.0);
        when(claimRepository.findDashboardStats(any(), any())).thenReturn(dashboardStats);

        DashboardResponse response = claimService.getDashboard();

        assertNotNull(response);
        assertEquals(10, response.getTotalClaims());
        assertEquals(5, response.getOpenClaims());
        assertEquals(3, response.getApprovedClaims());
        assertEquals(2, response.getRejectedClaims());
        assertEquals(50000.0, response.getOutstandingExposure());
        verify(claimRepository, times(1)).findDashboardStats(any(), any());
    }

    @Test
    void invalidStateTransitionThrows() {
        AssignClaimRequest assignRequest = new AssignClaimRequest();
        assignRequest.setOfficerId("OFFICER-01");

        Claim invalidClaim = Claim.builder()
                .claimId("CLAIM-001")
                .status(ClaimStatus.REJECTED)
                .build();

        when(claimRepository.findById("CLAIM-001")).thenReturn(Optional.of(invalidClaim));
        doThrow(new IllegalStateException("Invalid transition"))
                .when(stateMachine)
                .validateTransition(ClaimStatus.REJECTED, ClaimStatus.ASSIGNED);

        assertThrows(InvalidClaimStateException.class, () -> claimService.assignClaim("CLAIM-001", assignRequest));
    }

    @Test
    void getUnassignedClaimsReturnsUnassignedList() {
        Claim unassignedClaim = Claim.builder()
                .claimId("CLAIM-001")
                .claimantName("John Doe")
                .build();

        when(claimRepository.findByAssignedOfficerIdIsNull()).thenReturn(java.util.List.of(unassignedClaim));
        when(mapper.toSummaries(any())).thenReturn(java.util.List.of());

        claimService.getUnassignedClaims();

        verify(claimRepository, times(1)).findByAssignedOfficerIdIsNull();
        verify(mapper, times(1)).toSummaries(any());
    }
}
