package com.acc.cmx.claims.claim.repository;

import com.acc.cmx.claims.claim.entity.Claim;
import com.acc.cmx.claims.claim.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, String> {
    List<Claim> findByAssignedOfficerIdIsNull();

    @Query("select count(c) as total, " +
           "sum(case when c.status in :openStatuses then 1 else 0 end) as open, " +
           "sum(case when c.status = com.acc.cmx.claims.claim.entity.ClaimStatus.APPROVED then 1 else 0 end) as approved, " +
           "sum(case when c.status = com.acc.cmx.claims.claim.entity.ClaimStatus.REJECTED then 1 else 0 end) as rejected, " +
           "coalesce(sum(case when c.status not in :excludedStatuses then c.estimatedAmount else 0 end), 0) as outstandingExposure " +
           "from Claim c")
    DashboardStatsProjection findDashboardStats(@Param("openStatuses") Collection<ClaimStatus> openStatuses,
                                               @Param("excludedStatuses") Collection<ClaimStatus> excludedStatuses);
}
