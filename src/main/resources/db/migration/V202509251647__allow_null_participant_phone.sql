-- Allow null values for participant_phone in registrations table
ALTER TABLE registrations ALTER COLUMN participant_phone DROP NOT NULL;
