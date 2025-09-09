-- Rename timestamp columns in navigation_events table to match BaseEntity field mappings
ALTER TABLE navigation_events 
    RENAME COLUMN created_at TO created_date;

ALTER TABLE navigation_events 
    RENAME COLUMN updated_at TO updated_date;

-- Drop existing trigger
DROP TRIGGER IF EXISTS update_navigation_events_updated_at ON navigation_events;

-- Create new trigger with correct column name
CREATE TRIGGER update_navigation_events_updated_date BEFORE UPDATE
    ON navigation_events FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

-- Update the index name to reflect the new column name
DROP INDEX IF EXISTS idx_navigation_events_created_at;
CREATE INDEX idx_navigation_events_created_date ON navigation_events(created_date);