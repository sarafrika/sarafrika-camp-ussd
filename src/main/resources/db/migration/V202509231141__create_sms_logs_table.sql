CREATE TABLE sms_logs (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    phone_number VARCHAR(255) NOT NULL,
    message VARCHAR(1024) NOT NULL,
    message_id VARCHAR(255) UNIQUE,
    delivery_status VARCHAR(255) NOT NULL,
    provider_response VARCHAR(1024)
);

CREATE INDEX idx_sms_logs_message_id ON sms_logs(message_id);
CREATE INDEX idx_sms_logs_phone_number ON sms_logs(phone_number);
CREATE INDEX idx_sms_logs_created_date ON sms_logs(created_date);
