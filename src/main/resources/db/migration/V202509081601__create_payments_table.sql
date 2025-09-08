-- Camp Sarafrika: Create payment status enum and payments table
CREATE TYPE payment_status AS ENUM ('PENDING', 'INITIATED', 'SUCCESS', 'FAILED', 'CANCELLED', 'REFUNDED');
CREATE TYPE order_status AS ENUM ('PENDING', 'CONFIRMED', 'PAID', 'CANCELLED', 'REFUNDED');

-- Update orders table to use enum for status
ALTER TABLE orders ALTER COLUMN status TYPE order_status USING status::order_status;

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
    
    payment_reference VARCHAR(255) NOT NULL UNIQUE,
    order_uuid UUID NOT NULL REFERENCES orders(uuid),
    payment_amount DECIMAL(10,2) NOT NULL,
    payment_status payment_status NOT NULL DEFAULT 'PENDING',
    external_transaction_id VARCHAR(255),
    payment_date TIMESTAMP,
    callback_received_date TIMESTAMP
);

-- Add indexes for better query performance
CREATE INDEX idx_payments_uuid ON payments(uuid);
CREATE INDEX idx_payments_payment_reference ON payments(payment_reference);
CREATE INDEX idx_payments_order_uuid ON payments(order_uuid);
CREATE INDEX idx_payments_payment_status ON payments(payment_status);
CREATE INDEX idx_payments_payment_date ON payments(payment_date);
CREATE INDEX idx_payments_external_transaction_id ON payments(external_transaction_id);
CREATE INDEX idx_payments_is_deleted ON payments(is_deleted);