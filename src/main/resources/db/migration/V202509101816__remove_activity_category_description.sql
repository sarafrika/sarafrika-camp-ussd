-- Remove category and description columns from activities table
ALTER TABLE activities 
DROP COLUMN IF EXISTS category,
DROP COLUMN IF EXISTS description;

-- Drop the associated index for category
DROP INDEX IF EXISTS idx_activities_category;