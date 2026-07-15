package com.acc.cmx.claims.claim.controller;

import com.acc.cmx.claims.api.model.*;
import com.acc.cmx.claims.claim.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimControllerTest {
    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimController claimController;

    private CreateClaimRequest createRequest;
    private ClaimResponse claimResponse;
    private DashboardResponse dashboardResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateClaimRequest();
        createRequest.setClaimantName("John Doe");
        createRequest.setPolicyNumber("POL-1001");
        createRequest.setIncidentDescription("Car accident");
        createRequest.setEstimatedAmount(5000.0);

        claimResponse = new ClaimResponse();
        claimResponse.setClaimId("CLAIM-001");
        claimResponse.setClaimantName("John Doe");
        claimResponse.setStatus(ClaimStatus.SUBMITTED);

        dashboardResponse = new DashboardResponse();
        dashboardResponse.setTotalClaims(10);
        dashboardResponse.setOpenClaims(5);
        dashboardResponse.setApprovedClaims(3);
        dashboardResponse.setRejectedClaims(2);
        dashboardResponse.setOutstandingExposure(50000.0);
    }

    @Test
    void createClaimReturns201() {
        when(claimService.createClaim(any(CreateClaimRequest.class))).thenReturn(claimResponse);

        ResponseEntity<ClaimResponse> response = claimController.createClaim(createRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("CLAIM-001", response.getBody().getClaimId());
        verify(claimService, times(1)).createClaim(any(CreateClaimRequest.class));
    }

    @Test
    void getClaimReturnsOk() {
        when(claimService.getClaim("CLAIM-001")).thenReturn(claimResponse);

        ResponseEntity<ClaimResponse> response = claimController.getClaim("CLAIM-001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody().getClaimantName());
        verify(claimService, times(1)).getClaim("CLAIM-001");
    }

    @Test
    void submitAdditionalInformationReturnsOk() {
        AdditionalInfoRequest request = new AdditionalInfoRequest();
        request.setComments("Attached report");

        ResponseEntity<Void> response = claimController.submitAdditionalInformation("CLAIM-001", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(claimService, times(1)).submitAdditionalInfo("CLAIM-001", request);
    }

    @Test
    void getDashboardReturnsOk() {
        when(claimService.getDashboard()).thenReturn(dashboardResponse);

        ResponseEntity<DashboardResponse> response = claimController.getDashboard();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().getTotalClaims());
        assertEquals(50000.0, response.getBody().getOutstandingExposure());
        verify(claimService, times(1)).getDashboard();
    }
}
