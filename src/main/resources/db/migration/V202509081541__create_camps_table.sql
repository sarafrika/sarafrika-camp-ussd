-- Camp Sarafrika: Create camps table
CREATE TABLE camps (
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
    category VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    dates VARCHAR(255) NOT NULL,
    fee DECIMAL(10,2) NOT NULL,
    activities JSONB
);

-- Add indexes for better query performance
CREATE INDEX idx_camps_uuid ON camps(uuid);
CREATE INDEX idx_camps_category ON camps(category);
CREATE INDEX idx_camps_name ON camps(name);
CREATE INDEX idx_camps_is_deleted ON camps(is_deleted);

-- Insert sample camp data
INSERT INTO camps (name, category, location, dates, fee, activities) VALUES
('Adventure Seekers Camp', 'Adventure', 'Naivasha', 'Dec 15-20, 2024', 15000.00, 
 '["Rock Climbing", "Zip Lining", "Team Building", "Campfire Stories"]'::jsonb),
 
('Creative Arts Camp', 'Arts', 'Karen', 'Dec 22-27, 2024', 12000.00,
 '["Painting", "Music", "Drama", "Crafts"]'::jsonb),
 
('Sports Excellence Camp', 'Sports', 'Mombasa', 'Dec 18-23, 2024', 18000.00,
 '["Football", "Basketball", "Swimming", "Track & Field"]'::jsonb),
 
('Tech Innovation Camp', 'Technology', 'Nairobi', 'Jan 2-7, 2025', 20000.00,
 '["Coding", "Robotics", "3D Printing", "AI Basics"]'::jsonb),
 
('Nature Explorers Camp', 'Adventure', 'Mount Kenya', 'Dec 28-Jan 2, 2025', 22000.00,
 '["Hiking", "Wildlife Photography", "Stargazing", "Environmental Conservation"]'::jsonb),
 
('Music & Dance Camp', 'Arts', 'Kisumu', 'Jan 5-10, 2025', 14000.00,
 '["Traditional Dances", "Modern Music", "Instrument Lessons", "Performance"]'::jsonb);