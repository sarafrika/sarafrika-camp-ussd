-- Create base entities: locations, camps, and activities
-- These are the foundation entities for the Camp Sarafrika USSD system

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
    fee DECIMAL(10,2) NOT NULL,
    dates VARCHAR(500)
);

-- Create camps table
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
    camp_type VARCHAR(50) NOT NULL CHECK (camp_type IN ('HALF_DAY', 'BOOT_CAMP'))
);

-- Create activities table (simplified - no category or description)
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
    camp_uuid UUID NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT fk_activity_camp FOREIGN KEY (camp_uuid) REFERENCES camps(uuid)
);

-- Create camp_locations junction table for many-to-many relationship
CREATE TABLE camp_locations (
    camp_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    PRIMARY KEY (camp_id, location_id),
    CONSTRAINT fk_camp_locations_camp FOREIGN KEY (camp_id) REFERENCES camps(id),
    CONSTRAINT fk_camp_locations_location FOREIGN KEY (location_id) REFERENCES locations(id)
);

-- Create indexes for better performance
CREATE INDEX idx_locations_uuid ON locations(uuid);
CREATE INDEX idx_locations_is_deleted ON locations(is_deleted);

CREATE INDEX idx_camps_uuid ON camps(uuid);
CREATE INDEX idx_camps_is_deleted ON camps(is_deleted);
CREATE INDEX idx_camps_name ON camps(name);

CREATE INDEX idx_activities_uuid ON activities(uuid);
CREATE INDEX idx_activities_camp_uuid ON activities(camp_uuid);
CREATE INDEX idx_activities_is_deleted ON activities(is_deleted);
CREATE INDEX idx_activities_is_available ON activities(is_available);

CREATE INDEX idx_camp_locations_camp_id ON camp_locations(camp_id);
CREATE INDEX idx_camp_locations_location_id ON camp_locations(location_id);