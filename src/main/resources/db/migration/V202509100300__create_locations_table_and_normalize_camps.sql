-- Camp Sarafrika: Create locations table and normalize camp-location relationship

-- Create locations table
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    name VARCHAR(255) NOT NULL,
    fee DECIMAL(10,2) NOT NULL
);

-- Create camp_locations junction table for many-to-many relationship
CREATE TABLE camp_locations (
    camp_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    PRIMARY KEY (camp_id, location_id),
    FOREIGN KEY (camp_id) REFERENCES camps(id) ON DELETE CASCADE,
    FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE
);

-- Add indexes for better performance
CREATE INDEX idx_locations_uuid ON locations(uuid);
CREATE INDEX idx_locations_name ON locations(name);
CREATE INDEX idx_locations_is_deleted ON locations(is_deleted);
CREATE INDEX idx_camp_locations_camp_id ON camp_locations(camp_id);
CREATE INDEX idx_camp_locations_location_id ON camp_locations(location_id);

-- Insert distinct locations from existing camps with standard fees based on camp examples
INSERT INTO locations (name, fee) VALUES
('Kiambu', 12500.00),
('Nairobi', 12500.00),
('Rongai', 12500.00),
('Naivasha', 15000.00),
('Karen', 12000.00),
('Mombasa', 18000.00),
('Mount Kenya', 22000.00),
('Kisumu', 14000.00),
('Nairobi Sports Club', 15000.00),
('University of Nairobi', 18000.00),
('National Museums', 13000.00),
('Aberdare National Park', 25000.00),
('iHub Nairobi', 22000.00);

-- Populate camp_locations junction table based on existing camp data
-- First, for existing YMAC camps
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id 
FROM camps c
JOIN locations l ON c.location = l.name
WHERE c.category = 'Young Musicians & Artists Camp (YMAC)';

-- For other existing camps
INSERT INTO camp_locations (camp_id, location_id)
SELECT c.id, l.id 
FROM camps c
JOIN locations l ON c.location = l.name
WHERE c.category != 'Young Musicians & Artists Camp (YMAC)';

-- Remove the old location and fee columns from camps table
-- Note: We'll keep these for now to ensure data integrity, but they can be removed in a future migration
-- ALTER TABLE camps DROP COLUMN location;
-- ALTER TABLE camps DROP COLUMN fee;