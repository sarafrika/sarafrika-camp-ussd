-- Create SMS Message Type enum
CREATE TYPE sms_message_type AS ENUM (
    'REGISTRATION_CONFIRMATION',
    'GUARDIAN_NOTIFICATION', 
    'PAYMENT_REMINDER',
    'BULK_NOTIFICATION',
    'PAYMENT_CONFIRMATION',
    'CAMP_UPDATE'
);

-- Create SMS Status enum
CREATE TYPE sms_status AS ENUM (
    'PENDING',
    'SENT',
    'DELIVERED',
    'FAILED',
    'REJECTED'
);

-- SMS Notifications Tracking Table
CREATE TABLE sms_notifications (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    
    -- SMS Details
    recipient_phone VARCHAR(20) NOT NULL,
    message_content TEXT NOT NULL,
    message_type sms_message_type NOT NULL,
    
    -- Africa's Talking Response
    message_id VARCHAR(255),
    status sms_status NOT NULL DEFAULT 'PENDING',
    cost VARCHAR(20),
    
    -- Related Entities
    registration_uuid UUID,
    camp_uuid UUID,
    
    -- Delivery Tracking
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,
    retry_count INTEGER DEFAULT 0,
    
    -- BaseEntity columns
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    -- Constraints
    CONSTRAINT fk_sms_registration FOREIGN KEY (registration_uuid) REFERENCES registrations(uuid) ON DELETE SET NULL,
    CONSTRAINT fk_sms_camp FOREIGN KEY (camp_uuid) REFERENCES camps(uuid) ON DELETE SET NULL
);

-- Indexes for better query performance
CREATE INDEX idx_sms_notifications_recipient_phone ON sms_notifications(recipient_phone);
CREATE INDEX idx_sms_notifications_status ON sms_notifications(status);
CREATE INDEX idx_sms_notifications_message_type ON sms_notifications(message_type);
CREATE INDEX idx_sms_notifications_registration_uuid ON sms_notifications(registration_uuid);
CREATE INDEX idx_sms_notifications_created_date ON sms_notifications(created_date);
CREATE INDEX idx_sms_notifications_uuid ON sms_notifications(uuid);
CREATE INDEX idx_sms_notifications_is_deleted ON sms_notifications(is_deleted);