-- -- Sample test data for local claims database
-- -- Run this file against the local H2 database or import into another JDBC-compatible database.

-- DROP TABLE IF EXISTS claim_event;
-- DROP TABLE IF EXISTS claim;

-- CREATE TABLE claim (
--     claim_id VARCHAR(255) PRIMARY KEY,
--     claimant_name VARCHAR(255),
--     policy_number VARCHAR(255),
--     incident_description VARCHAR(2000),
--     estimated_amount DOUBLE,
--     approved_amount DOUBLE,
--     status VARCHAR(50),
--     assigned_officer_id VARCHAR(255),
--     additional_info VARCHAR(2000),
--     rejection_reason VARCHAR(1000),
--     created_at TIMESTAMP,
--     updated_at TIMESTAMP
-- );

-- CREATE TABLE claim_event (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     claim_id VARCHAR(255),
--     event_type VARCHAR(255),
--     from_status VARCHAR(50),
--     to_status VARCHAR(50),
--     payload VARCHAR(4000),
--     created_at TIMESTAMP
-- );

INSERT INTO claim (claim_id, claimant_name, policy_number, incident_description, estimated_amount, approved_amount, status, assigned_officer_id, additional_info, rejection_reason, created_at, updated_at)
VALUES
('CLAIM-001', 'Alice Rivera', 'POL-1001', 'Wind damage to home roof during storm.', 12500.00, NULL, 'SUBMITTED', NULL, NULL, NULL, TIMESTAMP '2026-07-01 09:15:00', TIMESTAMP '2026-07-01 09:15:00'),
('CLAIM-002', 'Brian Smith', 'POL-1002', 'Water leak in kitchen ceiling from pipe burst.', 4200.00, NULL, 'ASSIGNED', 'OFFICER-01', NULL, NULL, TIMESTAMP '2026-07-02 11:20:00', TIMESTAMP '2026-07-02 11:20:00'),
('CLAIM-003', 'Carmen Lee', 'POL-1003', 'Rear-end collision with vehicle at intersection.', 8700.00, NULL, 'INFO_REQUESTED', 'OFFICER-02', NULL, NULL, TIMESTAMP '2026-07-03 14:30:00', TIMESTAMP '2026-07-03 14:30:00'),
('CLAIM-004', 'Daniel Johnson', 'POL-1004', 'Fire damage to garage and stored equipment.', 24000.00, 22000.00, 'APPROVED', 'OFFICER-03', 'Customer confirmed repair vendor estimate.', NULL, TIMESTAMP '2026-07-04 16:45:00', TIMESTAMP '2026-07-04 16:45:00'),
('CLAIM-005', 'Emma Patel', 'POL-1005', 'Theft of bicycle from front yard.', 800.00, NULL, 'REJECTED', NULL, NULL, 'Policy excludes theft without evidence of forced entry.', TIMESTAMP '2026-07-05 08:05:00', TIMESTAMP '2026-07-05 08:05:00');

INSERT INTO claim_event (claim_id, event_type, from_status, to_status, payload, created_at)
VALUES
('CLAIM-001', 'ClaimSubmitted', NULL, 'SUBMITTED', NULL, TIMESTAMP '2026-07-01 09:15:00'),
('CLAIM-002', 'ClaimSubmitted', NULL, 'SUBMITTED', NULL, TIMESTAMP '2026-07-02 11:20:00'),
('CLAIM-002', 'ClaimAssigned', 'SUBMITTED', 'ASSIGNED', 'OFFICER-01', TIMESTAMP '2026-07-02 12:05:00'),
('CLAIM-003', 'ClaimSubmitted', NULL, 'SUBMITTED', NULL, TIMESTAMP '2026-07-03 14:30:00'),
('CLAIM-003', 'AdditionalInfoRequested', 'SUBMITTED', 'INFO_REQUESTED', 'Need collision report and photos.', TIMESTAMP '2026-07-03 15:00:00'),
('CLAIM-004', 'ClaimSubmitted', NULL, 'SUBMITTED', NULL, TIMESTAMP '2026-07-04 16:45:00'),
('CLAIM-004', 'ClaimApproved', 'SUBMITTED', 'APPROVED', '22000.0', TIMESTAMP '2026-07-04 17:20:00'),
('CLAIM-005', 'ClaimSubmitted', NULL, 'SUBMITTED', NULL, TIMESTAMP '2026-07-05 08:05:00'),
('CLAIM-005', 'ClaimRejected', 'SUBMITTED', 'REJECTED', 'Policy excludes theft without evidence of forced entry.', TIMESTAMP '2026-07-05 08:30:00');
