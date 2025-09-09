-- Fix all tracking table column names to match BaseEntity field mappings

-- Drop all existing triggers first
DROP TRIGGER IF EXISTS update_user_interactions_updated_at ON user_interactions;
DROP TRIGGER IF EXISTS update_session_events_updated_at ON session_events;
DROP TRIGGER IF EXISTS update_navigation_events_updated_at ON navigation_events;
DROP TRIGGER IF EXISTS update_performance_metrics_updated_at ON performance_metrics;

-- Drop existing indexes that reference old column names
DROP INDEX IF EXISTS idx_user_interactions_created_at;
DROP INDEX IF EXISTS idx_session_events_created_at;
DROP INDEX IF EXISTS idx_navigation_events_created_at;
DROP INDEX IF EXISTS idx_performance_metrics_created_at;

-- Rename columns in all tracking tables
ALTER TABLE user_interactions 
    RENAME COLUMN created_at TO created_date;
ALTER TABLE user_interactions 
    RENAME COLUMN updated_at TO updated_date;

ALTER TABLE session_events 
    RENAME COLUMN created_at TO created_date;
ALTER TABLE session_events 
    RENAME COLUMN updated_at TO updated_date;

ALTER TABLE navigation_events 
    RENAME COLUMN created_at TO created_date;
ALTER TABLE navigation_events 
    RENAME COLUMN updated_at TO updated_date;

ALTER TABLE performance_metrics 
    RENAME COLUMN created_at TO created_date;
ALTER TABLE performance_metrics 
    RENAME COLUMN updated_at TO updated_date;

-- Create new indexes with correct column names
CREATE INDEX idx_user_interactions_created_date ON user_interactions(created_date);
CREATE INDEX idx_session_events_created_date ON session_events(created_date);
CREATE INDEX idx_navigation_events_created_date ON navigation_events(created_date);
CREATE INDEX idx_performance_metrics_created_date ON performance_metrics(created_date);

-- Now we can safely drop and recreate the function
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;

-- Create new function that works with updated_date column
CREATE OR REPLACE FUNCTION update_updated_date_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for all tables with the new function
CREATE TRIGGER update_user_interactions_updated_date BEFORE UPDATE
    ON user_interactions FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

CREATE TRIGGER update_session_events_updated_date BEFORE UPDATE
    ON session_events FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

CREATE TRIGGER update_navigation_events_updated_date BEFORE UPDATE
    ON navigation_events FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

CREATE TRIGGER update_performance_metrics_updated_date BEFORE UPDATE
    ON performance_metrics FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();