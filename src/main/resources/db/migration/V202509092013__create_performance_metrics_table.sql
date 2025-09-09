-- Create performance_metrics table for tracking system performance
CREATE TABLE performance_metrics (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    session_id VARCHAR(255),
    phone_number VARCHAR(20),
    metric_type VARCHAR(50) NOT NULL, -- 'RESPONSE_TIME', 'DB_QUERY_TIME', 'REDIS_TIME', 'PROCESSING_TIME'
    metric_value DECIMAL(10,3) NOT NULL, -- milliseconds with 3 decimal precision
    context_data JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_performance_metrics_type ON performance_metrics(metric_type);
CREATE INDEX idx_performance_metrics_session_id ON performance_metrics(session_id);
CREATE INDEX idx_performance_metrics_created_at ON performance_metrics(created_at);
CREATE INDEX idx_performance_metrics_uuid ON performance_metrics(uuid);

-- Trigger to update updated_at timestamp
CREATE TRIGGER update_performance_metrics_updated_at BEFORE UPDATE
    ON performance_metrics FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();