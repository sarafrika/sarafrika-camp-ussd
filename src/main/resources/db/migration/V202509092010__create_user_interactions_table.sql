-- Create user_interactions table for tracking all user inputs and responses
CREATE TABLE user_interactions (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    session_id VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    interaction_type VARCHAR(50) NOT NULL, -- 'INPUT', 'NAVIGATION', 'VALIDATION_ERROR', 'SESSION_START', 'SESSION_END'
    current_state VARCHAR(100),
    previous_state VARCHAR(100),
    user_input TEXT,
    response_generated TEXT,
    processing_time_ms INTEGER,
    error_message TEXT,
    metadata JSONB, -- Additional context data
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_user_interactions_session_id ON user_interactions(session_id);
CREATE INDEX idx_user_interactions_phone_number ON user_interactions(phone_number);
CREATE INDEX idx_user_interactions_created_at ON user_interactions(created_at);
CREATE INDEX idx_user_interactions_type_state ON user_interactions(interaction_type, current_state);
CREATE INDEX idx_user_interactions_uuid ON user_interactions(uuid);

-- Trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_interactions_updated_at BEFORE UPDATE
    ON user_interactions FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();