-- Create navigation_events table for tracking page/state transitions
CREATE TABLE navigation_events (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    session_id VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    from_state VARCHAR(100),
    to_state VARCHAR(100) NOT NULL,
    navigation_type VARCHAR(50) NOT NULL, -- 'FORWARD', 'BACK', 'DIRECT', 'EXIT', 'PAGINATION'
    user_input VARCHAR(255),
    time_on_previous_page_ms INTEGER,
    page_data JSONB, -- Menu items, selected options, etc.
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_navigation_events_session_id ON navigation_events(session_id);
CREATE INDEX idx_navigation_events_phone_number ON navigation_events(phone_number);
CREATE INDEX idx_navigation_events_states ON navigation_events(from_state, to_state);
CREATE INDEX idx_navigation_events_navigation_type ON navigation_events(navigation_type);
CREATE INDEX idx_navigation_events_created_date ON navigation_events(created_date);
CREATE INDEX idx_navigation_events_uuid ON navigation_events(uuid);
CREATE INDEX idx_navigation_events_is_deleted ON navigation_events(is_deleted);