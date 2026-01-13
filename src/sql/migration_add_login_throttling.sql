-- Migration Script: Add Login Throttling Support
-- Run this script on your auth_db database

USE auth_db;

-- Add failed_attempts column (default 0)
ALTER TABLE users_auth 
ADD COLUMN IF NOT EXISTS failed_attempts INT DEFAULT 0;

-- Add lock_time column (nullable, stores timestamp when account is locked)
ALTER TABLE users_auth 
ADD COLUMN IF NOT EXISTS lock_time DATETIME NULL;

-- Create index on lock_time for faster queries
CREATE INDEX IF NOT EXISTS idx_lock_time ON users_auth(lock_time);

-- Reset any existing failed attempts to 0 (optional, for clean start)
-- UPDATE users_auth SET failed_attempts = 0 WHERE failed_attempts IS NULL;

-- Verify the changes
SELECT 
    user_id, 
    username, 
    role, 
    status, 
    failed_attempts, 
    lock_time 
FROM users_auth 
LIMIT 5;

