-- Fix tracking tables schema to match JPA entity definitions

-- Fix navigation_events table - add missing columns and rename existing ones
ALTER TABLE navigation_events 
    ADD COLUMN from_state VARCHAR(100),
    ADD COLUMN to_state VARCHAR(100),
    ADD COLUMN navigation_type VARCHAR(50),
    ADD COLUMN time_on_previous_page_ms INTEGER,
    ADD COLUMN page_data JSONB;

-- Migrate data from old columns to new columns
UPDATE navigation_events 
SET from_state = from_menu, 
    to_state = to_menu;

-- Drop indexes on old columns before dropping the columns
DROP INDEX IF EXISTS idx_navigation_events_timestamp;

-- Drop old columns
ALTER TABLE navigation_events 
    DROP COLUMN from_menu,
    DROP COLUMN to_menu,
    DROP COLUMN navigation_input,
    DROP COLUMN navigation_timestamp;

-- Fix session_events table - add missing columns
ALTER TABLE session_events 
    ADD COLUMN duration_seconds INTEGER,
    ADD COLUMN redis_operation_time_ms INTEGER,
    ADD COLUMN network_code VARCHAR(20),
    ADD COLUMN service_code VARCHAR(20),
    ADD COLUMN session_data_snapshot JSONB;

-- Migrate event_data to session_data_snapshot if it exists
UPDATE session_events 
SET session_data_snapshot = event_data::jsonb 
WHERE event_data IS NOT NULL;

-- Drop indexes on old columns before dropping the columns
DROP INDEX IF EXISTS idx_session_events_timestamp;

-- Drop old columns that are no longer needed
ALTER TABLE session_events 
    DROP COLUMN event_data,
    DROP COLUMN event_timestamp;

-- Add indexes for new columns
CREATE INDEX idx_navigation_events_from_state ON navigation_events(from_state);
CREATE INDEX idx_navigation_events_to_state ON navigation_events(to_state);
CREATE INDEX idx_navigation_events_navigation_type ON navigation_events(navigation_type);

CREATE INDEX idx_session_events_event_type ON session_events(event_type);
CREATE INDEX idx_session_events_network_code ON session_events(network_code);
CREATE INDEX idx_session_events_service_code ON session_events(service_code);