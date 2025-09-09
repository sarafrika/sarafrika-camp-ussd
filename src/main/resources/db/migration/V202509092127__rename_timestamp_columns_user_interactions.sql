-- Rename timestamp columns in user_interactions table to match BaseEntity field mappings
ALTER TABLE user_interactions 
    RENAME COLUMN created_at TO created_date;

ALTER TABLE user_interactions 
    RENAME COLUMN updated_at TO updated_date;

-- Drop existing trigger that references old column name
DROP TRIGGER IF EXISTS update_user_interactions_updated_at ON user_interactions;

-- Create new trigger with correct column name
CREATE TRIGGER update_user_interactions_updated_date BEFORE UPDATE
    ON user_interactions FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();

-- Update the function to work with the new column name
DROP FUNCTION IF EXISTS update_updated_at_column();

CREATE OR REPLACE FUNCTION update_updated_date_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Recreate the trigger with the new function
DROP TRIGGER IF EXISTS update_user_interactions_updated_date ON user_interactions;

CREATE TRIGGER update_user_interactions_updated_date BEFORE UPDATE
    ON user_interactions FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

-- Update the index name to reflect the new column name
DROP INDEX IF EXISTS idx_user_interactions_created_at;
CREATE INDEX idx_user_interactions_created_date ON user_interactions(created_date);