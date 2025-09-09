-- Camp Sarafrika: Remove redundant activities JSONB column from camps table
-- Activities are now properly normalized in the activities table with proper relationships

-- Remove the activities JSONB column since we have a proper Activity entity
ALTER TABLE camps DROP COLUMN IF EXISTS activities;