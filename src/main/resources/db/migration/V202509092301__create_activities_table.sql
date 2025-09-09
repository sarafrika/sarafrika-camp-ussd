-- Camp Sarafrika: Create activities table for dynamic activity management
CREATE TABLE activities (
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
    description TEXT,
    category VARCHAR(100),
    camp_uuid UUID REFERENCES camps(uuid),
    is_available BOOLEAN DEFAULT TRUE
);

-- Add indexes for better query performance
CREATE INDEX idx_activities_uuid ON activities(uuid);
CREATE INDEX idx_activities_camp_uuid ON activities(camp_uuid);
CREATE INDEX idx_activities_category ON activities(category);
CREATE INDEX idx_activities_is_deleted ON activities(is_deleted);
CREATE INDEX idx_activities_is_available ON activities(is_available);

-- Insert sample activities based on existing camp data
INSERT INTO activities (name, description, category, camp_uuid, is_available)
SELECT 
    activity_name,
    'Activity for ' || c.name,
    c.category,
    c.uuid,
    true
FROM camps c
CROSS JOIN LATERAL (
    SELECT jsonb_array_elements_text(c.activities) as activity_name
) activities_json
WHERE c.activities IS NOT NULL;

-- Insert additional YMAC activities as per document
DO $$
DECLARE
    camp_uuid_val UUID;
BEGIN
    -- Find a sample camp to assign YMAC activities to, or create one if needed
    SELECT uuid INTO camp_uuid_val FROM camps WHERE category = 'Arts' LIMIT 1;
    
    IF camp_uuid_val IS NULL THEN
        INSERT INTO camps (name, category, location, dates, fee, camp_type) 
        VALUES ('Young Musicians & Artists Camp', 'Young Musicians & Artists Camp (YMAC)', 'Nairobi', 'Dec 1-12, 2024', 12500.00, 'HALF_DAY')
        RETURNING uuid INTO camp_uuid_val;
    END IF;
    
    -- Insert YMAC activities from the document
    INSERT INTO activities (name, description, category, camp_uuid, is_available) VALUES
    ('Know your Talent Beginners Program', 'Talent discovery program', 'YMAC', camp_uuid_val, true),
    ('Photography', 'Learn photography basics and techniques', 'YMAC', camp_uuid_val, true),
    ('Musical Theatre', 'Combine music, acting and dance', 'YMAC', camp_uuid_val, true),
    ('String Ensemble', 'Learn string instruments in group setting', 'YMAC', camp_uuid_val, true),
    ('Marching Band', 'Learn marching band formations and music', 'YMAC', camp_uuid_val, true),
    ('Pop, Rock & RnB', 'Modern music genres and performance', 'YMAC', camp_uuid_val, true),
    ('Ballet', 'Classical dance techniques', 'YMAC', camp_uuid_val, true),
    ('Dance', 'Various dance styles and choreography', 'YMAC', camp_uuid_val, true),
    ('Piano Ensemble', 'Piano group playing and theory', 'YMAC', camp_uuid_val, true),
    ('Guitar Ensemble', 'Guitar group playing and techniques', 'YMAC', camp_uuid_val, true),
    ('Jazz Band', 'Jazz music theory and performance', 'YMAC', camp_uuid_val, true),
    ('Creative Writing', 'Story writing and literary skills', 'YMAC', camp_uuid_val, true),
    ('Visual Arts', 'Drawing, painting and visual expression', 'YMAC', camp_uuid_val, true),
    ('Deejaying', 'DJ skills and music mixing', 'YMAC', camp_uuid_val, true),
    ('Voice', 'Vocal training and singing techniques', 'YMAC', camp_uuid_val, true),
    ('Drumline', 'Percussion ensemble and rhythms', 'YMAC', camp_uuid_val, true),
    ('Technical Theatre', 'Backstage production and technical skills', 'YMAC', camp_uuid_val, true),
    ('Songwriting & Recording', 'Music composition and studio recording', 'YMAC', camp_uuid_val, true);
END $$;