package com.acc.cmx.claims.claim.controller;

import com.acc.cmx.claims.api.model.ApproveClaimRequest;
import com.acc.cmx.claims.api.model.AssignClaimRequest;
import com.acc.cmx.claims.api.model.RejectClaimRequest;
import com.acc.cmx.claims.api.model.RequestInfoRequest;
import com.acc.cmx.claims.claim.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowControllerTest {
    @Mock
    private ClaimService claimService;


    @InjectMocks
    private WorkflowController workflowController;

    private ApproveClaimRequest approveRequest;
    private RejectClaimRequest rejectRequest;
    private RequestInfoRequest infoRequest;

    private AssignClaimRequest assignRequest;

    @BeforeEach
    void setUp() {
        approveRequest = new ApproveClaimRequest();
        approveRequest.setApprovedAmount(4500.0);

        rejectRequest = new RejectClaimRequest();
        rejectRequest.setReason("Insufficient documentation");

        infoRequest = new RequestInfoRequest();
        infoRequest.setReason("Need accident report");

        assignRequest = new AssignClaimRequest();
        assignRequest.setOfficerId("OFFICER-01");
    }

    @Test
    void approveClaimReturnsOk() {
        ResponseEntity<Void> response = workflowController.approveClaim("CLAIM-001", approveRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(claimService, times(1)).approveClaim("CLAIM-001", approveRequest);
    }

    @Test
    void rejectClaimReturnsOk() {
        ResponseEntity<Void> response = workflowController.rejectClaim("CLAIM-001", rejectRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(claimService, times(1)).rejectClaim("CLAIM-001", rejectRequest);
    }

    @Test
    void requestAdditionalInformationReturnsOk() {
        ResponseEntity<Void> response = workflowController.requestAdditionalInformation("CLAIM-001", infoRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(claimService, times(1)).requestInfo("CLAIM-001", infoRequest);
    }

    @Test
    void assignClaimReturnsOk() {
        ResponseEntity<Void> response = workflowController.assignClaim("CLAIM-001", assignRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(claimService, times(1)).assignClaim("CLAIM-001", assignRequest);
    }
}
