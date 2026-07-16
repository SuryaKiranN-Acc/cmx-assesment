package com.acc.cmx.claims.claim.repository;

import com.acc.cmx.claims.claim.entity.ClaimEvent;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimEventRepository extends JpaRepository<ClaimEvent, Long> {
    List<ClaimEvent> findByClaimIdOrderByCreatedAtAsc(String claimId);
}
