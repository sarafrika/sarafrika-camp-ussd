-- Migration to wipe all table records clean for migration versioning
-- This will delete all data from existing tables while preserving table structure

-- Disable foreign key constraints temporarily to avoid dependency issues
SET session_replication_role = replica;

-- Delete all records from tables in reverse dependency order
DELETE FROM sms_notifications;
DELETE FROM performance_metrics;
DELETE FROM navigation_events;
DELETE FROM session_events;
DELETE FROM user_interactions;
DELETE FROM payments;
DELETE FROM orders;
DELETE FROM registrations;
DELETE FROM activities;
DELETE FROM camps;
DELETE FROM locations;

-- Reset sequences to start from 1
ALTER SEQUENCE IF EXISTS camps_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS locations_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS registrations_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS orders_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS payments_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS activities_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS user_interactions_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS session_events_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS navigation_events_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS performance_metrics_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS sms_notifications_id_seq RESTART WITH 1;

-- Re-enable foreign key constraints
SET session_replication_role = DEFAULT;

-- All table records cleared - clean slate for data