-- Migration to restructure registration and order tables to separate payment concerns
-- Move activity and location selection to orders table since orders track what's being purchased

-- Remove payment-related columns from registrations table (they belong in orders)
ALTER TABLE registrations 
DROP COLUMN IF EXISTS fee_paid,
DROP COLUMN IF EXISTS reference_code,
DROP COLUMN IF EXISTS status,
DROP COLUMN IF EXISTS payment_date;

-- Add activity and location selection to orders table (what's being purchased)
ALTER TABLE orders 
ADD COLUMN IF NOT EXISTS selected_activity_uuid UUID,
ADD COLUMN IF NOT EXISTS selected_location_uuid UUID,
ADD COLUMN IF NOT EXISTS reference_code VARCHAR(255) UNIQUE,
ADD COLUMN IF NOT EXISTS payment_date TIMESTAMP;

-- Add foreign key constraints for activity and location in orders
ALTER TABLE orders
ADD CONSTRAINT IF NOT EXISTS fk_order_activity 
    FOREIGN KEY (selected_activity_uuid) REFERENCES activities(uuid),
ADD CONSTRAINT IF NOT EXISTS fk_order_location 
    FOREIGN KEY (selected_location_uuid) REFERENCES locations(uuid);

-- Copy data from order_code to reference_code if it exists
UPDATE orders SET reference_code = order_code WHERE reference_code IS NULL AND order_code IS NOT NULL;

-- Drop the old order_code column
ALTER TABLE orders DROP COLUMN IF EXISTS order_code;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_orders_activity_uuid ON orders(selected_activity_uuid);
CREATE INDEX IF NOT EXISTS idx_orders_location_uuid ON orders(selected_location_uuid);
CREATE INDEX IF NOT EXISTS idx_orders_reference_code ON orders(reference_code);

-- Update any existing orders to have a proper reference code format if needed
UPDATE orders 
SET reference_code = 'CS-' || UPPER(SUBSTRING(MD5(RANDOM()::text) FROM 1 FOR 8))
WHERE reference_code IS NULL OR reference_code = '' OR NOT reference_code LIKE 'CS-%';