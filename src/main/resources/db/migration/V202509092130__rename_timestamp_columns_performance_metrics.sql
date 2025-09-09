-- Rename timestamp columns in performance_metrics table to match BaseEntity field mappings
ALTER TABLE performance_metrics 
    RENAME COLUMN created_at TO created_date;

ALTER TABLE performance_metrics 
    RENAME COLUMN updated_at TO updated_date;

-- Drop existing trigger
DROP TRIGGER IF EXISTS update_performance_metrics_updated_at ON performance_metrics;

-- Create new trigger with correct column name
CREATE TRIGGER update_performance_metrics_updated_date BEFORE UPDATE
    ON performance_metrics FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

-- Update the index name to reflect the new column name
DROP INDEX IF EXISTS idx_performance_metrics_created_at;
CREATE INDEX idx_performance_metrics_created_date ON performance_metrics(created_date);