-- Add missing user_input column to navigation_events table

ALTER TABLE navigation_events 
    ADD COLUMN user_input VARCHAR(500);


















