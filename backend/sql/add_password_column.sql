-- Add password_hash column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255) AFTER email;

-- Verify the column was added
DESCRIBE users;

