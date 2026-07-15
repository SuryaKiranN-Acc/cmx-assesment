package com.acc.cmx.claims.claim.repository;

public interface DashboardStatsProjection {
    long getTotal();
    long getOpen();
    long getApproved();
    long getRejected();
    double getOutstandingExposure();
}
