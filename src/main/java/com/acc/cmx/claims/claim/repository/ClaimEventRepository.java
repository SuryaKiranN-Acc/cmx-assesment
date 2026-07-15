package com.acc.cmx.claims.claim.repository;

import com.acc.cmx.claims.claim.entity.ClaimEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimEventRepository extends JpaRepository<ClaimEvent, Long> {
}
