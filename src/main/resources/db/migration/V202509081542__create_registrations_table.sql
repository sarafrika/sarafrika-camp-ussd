-- Camp Sarafrika: Create registrations table
CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    participant_name VARCHAR(255) NOT NULL,
    participant_age INTEGER NOT NULL,
    participant_phone VARCHAR(20) NOT NULL,
    guardian_phone VARCHAR(20),
    camp_uuid UUID NOT NULL REFERENCES camps(uuid),
    selected_activity VARCHAR(255),
    fee_paid DECIMAL(10,2) NOT NULL,
    reference_code VARCHAR(20) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_date TIMESTAMP
);

-- Add indexes for better query performance
CREATE INDEX idx_registrations_uuid ON registrations(uuid);
CREATE INDEX idx_registrations_reference_code ON registrations(reference_code);
CREATE INDEX idx_registrations_participant_phone ON registrations(participant_phone);
CREATE INDEX idx_registrations_camp_uuid ON registrations(camp_uuid);
CREATE INDEX idx_registrations_status ON registrations(status);
CREATE INDEX idx_registrations_registration_date ON registrations(registration_date);
CREATE INDEX idx_registrations_is_deleted ON registrations(is_deleted);

-- Add constraint to ensure guardian phone is provided for minors
ALTER TABLE registrations ADD CONSTRAINT chk_guardian_phone_for_minors 
    CHECK (participant_age >= 18 OR guardian_phone IS NOT NULL);