-- Fix payment_status check constraint to match PaymentStatus enum values

-- Drop existing constraint
ALTER TABLE payments DROP CONSTRAINT payments_payment_status_check;

-- Add new constraint with all enum values
ALTER TABLE payments ADD CONSTRAINT payments_payment_status_check
    CHECK (payment_status IN ('PENDING', 'INITIATED', 'SUCCESS', 'FAILED', 'CANCELLED', 'REFUNDED'));