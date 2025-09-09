-- Create session_events table for tracking session lifecycle
CREATE TABLE session_events (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    session_id VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- 'CREATED', 'EXTENDED', 'EXPIRED', 'TERMINATED', 'DATA_UPDATED'
    session_data_snapshot JSONB, -- Full session state at this point
    duration_seconds INTEGER,
    redis_operation_time_ms INTEGER,
    network_code VARCHAR(20),
    service_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_session_events_session_id ON session_events(session_id);
CREATE INDEX idx_session_events_phone_number ON session_events(phone_number);
CREATE INDEX idx_session_events_event_type ON session_events(event_type);
CREATE INDEX idx_session_events_created_at ON session_events(created_at);
CREATE INDEX idx_session_events_uuid ON session_events(uuid);

-- Trigger to update updated_at timestamp
CREATE TRIGGER update_session_events_updated_at BEFORE UPDATE
    ON session_events FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();