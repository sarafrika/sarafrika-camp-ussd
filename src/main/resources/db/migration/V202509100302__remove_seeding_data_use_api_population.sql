-- Camp Sarafrika: Remove seeding data from migrations - use API endpoints for data population

-- Note: All data should now be populated via API endpoints:
-- Locations: POST /api/locations
-- Camps: POST /api/camps  
-- Activities: POST /api/activities
-- Camp-Location relationships: Handled automatically through camp creation

-- This migration removes any sample data that may have been inserted in previous migrations
-- to ensure clean database state for API-based population

-- Clean up any existing sample data (if present)
DELETE FROM camp_locations WHERE TRUE;
DELETE FROM activities WHERE created_by = 'system';
DELETE FROM camps WHERE created_by = 'system';
DELETE FROM locations WHERE created_by = 'system';