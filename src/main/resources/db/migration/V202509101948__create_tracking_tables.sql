-- Create user interaction and tracking tables for USSD analytics and monitoring

-- Create user_interactions table for tracking user actions
CREATE TABLE user_interactions (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    session_id VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    action VARCHAR(100) NOT NULL,
    menu_level VARCHAR(50),
    user_input VARCHAR(500),
    response_sent VARCHAR(1000),
    interaction_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create session_events table for session lifecycle tracking
CREATE TABLE session_events (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    session_id VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    event_data TEXT,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create navigation_events table for menu navigation tracking
CREATE TABLE navigation_events (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    session_id VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    from_menu VARCHAR(100),
    to_menu VARCHAR(100),
    navigation_input VARCHAR(10),
    navigation_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create performance_metrics table for system performance monitoring
CREATE TABLE performance_metrics (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,4),
    metric_unit VARCHAR(20),
    metric_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    additional_data TEXT
);

-- Create indexes for better performance
CREATE INDEX idx_user_interactions_uuid ON user_interactions(uuid);
CREATE INDEX idx_user_interactions_session_id ON user_interactions(session_id);
CREATE INDEX idx_user_interactions_phone_number ON user_interactions(phone_number);
CREATE INDEX idx_user_interactions_timestamp ON user_interactions(interaction_timestamp);
CREATE INDEX idx_user_interactions_is_deleted ON user_interactions(is_deleted);

CREATE INDEX idx_session_events_uuid ON session_events(uuid);
CREATE INDEX idx_session_events_session_id ON session_events(session_id);
CREATE INDEX idx_session_events_phone_number ON session_events(phone_number);
CREATE INDEX idx_session_events_timestamp ON session_events(event_timestamp);
CREATE INDEX idx_session_events_is_deleted ON session_events(is_deleted);

CREATE INDEX idx_navigation_events_uuid ON navigation_events(uuid);
CREATE INDEX idx_navigation_events_session_id ON navigation_events(session_id);
CREATE INDEX idx_navigation_events_phone_number ON navigation_events(phone_number);
CREATE INDEX idx_navigation_events_timestamp ON navigation_events(navigation_timestamp);
CREATE INDEX idx_navigation_events_is_deleted ON navigation_events(is_deleted);

CREATE INDEX idx_performance_metrics_uuid ON performance_metrics(uuid);
CREATE INDEX idx_performance_metrics_name ON performance_metrics(metric_name);
CREATE INDEX idx_performance_metrics_timestamp ON performance_metrics(metric_timestamp);
CREATE INDEX idx_performance_metrics_is_deleted ON performance_metrics(is_deleted);