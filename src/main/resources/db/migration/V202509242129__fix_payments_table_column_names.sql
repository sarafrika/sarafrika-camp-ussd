-- Fix column name mismatches in payments table to match entity fields

-- Rename external_reference to external_transaction_id
ALTER TABLE payments RENAME COLUMN external_reference TO external_transaction_id;

-- Rename amount to payment_amount
ALTER TABLE payments RENAME COLUMN amount TO payment_amount;