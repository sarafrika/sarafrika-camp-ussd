-- Rename timestamp columns in session_events table to match BaseEntity field mappings
ALTER TABLE session_events 
    RENAME COLUMN created_at TO created_date;

ALTER TABLE session_events 
    RENAME COLUMN updated_at TO updated_date;

-- Drop existing trigger
DROP TRIGGER IF EXISTS update_session_events_updated_at ON session_events;

-- Create new trigger with correct column name
CREATE TRIGGER update_session_events_updated_date BEFORE UPDATE
    ON session_events FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

-- Update the index name to reflect the new column name
DROP INDEX IF EXISTS idx_session_events_created_at;
CREATE INDEX idx_session_events_created_date ON session_events(created_date);