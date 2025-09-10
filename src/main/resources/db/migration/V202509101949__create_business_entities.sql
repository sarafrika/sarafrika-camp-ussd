-- Create business entities: registrations, orders, and payments
-- These handle the core business logic of camp registration and payment processing

-- Create registrations table for participant data
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
    participant_age INTEGER,
    participant_phone VARCHAR(20) NOT NULL,
    guardian_phone VARCHAR(20),
    camp_uuid UUID NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_registration_camp FOREIGN KEY (camp_uuid) REFERENCES camps(uuid)
);

-- Create orders table for purchase transactions
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    reference_code VARCHAR(255) UNIQUE NOT NULL,
    registration_uuid UUID NOT NULL,
    selected_activity_uuid UUID,
    selected_location_uuid UUID,
    order_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'CANCELLED', 'FAILED')),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_date TIMESTAMP,
    
    CONSTRAINT fk_order_registration FOREIGN KEY (registration_uuid) REFERENCES registrations(uuid),
    CONSTRAINT fk_order_activity FOREIGN KEY (selected_activity_uuid) REFERENCES activities(uuid),
    CONSTRAINT fk_order_location FOREIGN KEY (selected_location_uuid) REFERENCES locations(uuid)
);

-- Create payments table for payment tracking
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_by VARCHAR(255),
    deleted_date TIMESTAMP,
    
    order_uuid UUID NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_reference VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    external_reference VARCHAR(255),
    payment_details TEXT,
    
    CONSTRAINT fk_payment_order FOREIGN KEY (order_uuid) REFERENCES orders(uuid)
);

-- Create indexes for better performance
CREATE INDEX idx_registrations_uuid ON registrations(uuid);
CREATE INDEX idx_registrations_participant_phone ON registrations(participant_phone);
CREATE INDEX idx_registrations_camp_uuid ON registrations(camp_uuid);
CREATE INDEX idx_registrations_is_deleted ON registrations(is_deleted);
CREATE INDEX idx_registrations_registration_date ON registrations(registration_date);

CREATE INDEX idx_orders_uuid ON orders(uuid);
CREATE INDEX idx_orders_reference_code ON orders(reference_code);
CREATE INDEX idx_orders_registration_uuid ON orders(registration_uuid);
CREATE INDEX idx_orders_activity_uuid ON orders(selected_activity_uuid);
CREATE INDEX idx_orders_location_uuid ON orders(selected_location_uuid);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_is_deleted ON orders(is_deleted);
CREATE INDEX idx_orders_order_date ON orders(order_date);

CREATE INDEX idx_payments_uuid ON payments(uuid);
CREATE INDEX idx_payments_order_uuid ON payments(order_uuid);
CREATE INDEX idx_payments_reference ON payments(payment_reference);
CREATE INDEX idx_payments_status ON payments(payment_status);
CREATE INDEX idx_payments_is_deleted ON payments(is_deleted);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);