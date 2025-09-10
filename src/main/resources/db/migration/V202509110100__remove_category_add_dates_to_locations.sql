-- Camp Sarafrika: Remove category from camps and add dates to locations
-- Categories will be determined by camp names, and dates belong to specific locations

-- Add dates column to locations table
ALTER TABLE locations ADD COLUMN dates VARCHAR(255);

-- Remove category column from camps table
ALTER TABLE camps DROP COLUMN IF EXISTS category;

-- Drop the category index since it's no longer needed
DROP INDEX IF EXISTS idx_camps_category;

-- Add index for location dates for better query performance
CREATE INDEX idx_locations_dates ON locations(dates);

-- Note: Data should be populated via API endpoints with the new structure:
-- Locations: POST /api/locations (now with dates)
-- Camps: POST /api/camps (without category field)
-- Camp-location relationships: Handled automatically through camp creation