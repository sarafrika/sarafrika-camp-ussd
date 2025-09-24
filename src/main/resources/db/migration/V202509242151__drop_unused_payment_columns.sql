-- Drop unused columns from payments table that are not set by payment webhook controller

-- Drop payment_method column (not set by webhook controller and causing NOT NULL constraint violation)
ALTER TABLE payments DROP COLUMN payment_method;

-- Drop payment_details column (not set by webhook controller)
ALTER TABLE payments DROP COLUMN payment_details;