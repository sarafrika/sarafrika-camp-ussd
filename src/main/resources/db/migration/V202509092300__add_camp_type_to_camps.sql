-- Camp Sarafrika: Add camp_type column to camps table
ALTER TABLE camps ADD COLUMN camp_type VARCHAR(20);

-- Set default camp type for existing records
UPDATE camps SET camp_type = 'HALF_DAY' WHERE camp_type IS NULL;

-- Add index for better query performance
CREATE INDEX idx_camps_camp_type ON camps(camp_type);

-- Add check constraint to ensure valid camp types
ALTER TABLE camps ADD CONSTRAINT chk_camps_camp_type 
    CHECK (camp_type IN ('HALF_DAY', 'BOOT_CAMP'));

-- Update existing sample data with camp types
UPDATE camps SET camp_type = 'BOOT_CAMP' WHERE name IN ('Adventure Seekers Camp', 'Nature Explorers Camp');
UPDATE camps SET camp_type = 'HALF_DAY' WHERE camp_type IS NULL;