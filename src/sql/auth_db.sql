-- ============================================
-- Authentication Database Schema
-- University ERP System
-- ============================================

DROP DATABASE IF EXISTS auth_db;
CREATE DATABASE auth_db;
USE auth_db;

-- Users Authentication Table
CREATE TABLE users_auth (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'INSTRUCTOR', 'STUDENT') NOT NULL,
    status ENUM('ACTIVE', 'DISABLED') DEFAULT 'ACTIVE',
    failed_attempts INT DEFAULT 0,
    lock_time DATETIME NULL,
    last_login DATETIME NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- Sample Data (Passwords are all: password123)
-- BCrypt hash for 'password123'
-- ============================================

INSERT INTO users_auth (username, password_hash, role, status) VALUES
('admin1', '$2a$12$4Ng7YhOVLnaWXxQV.6.xseNhg0L.dfMbGO.qPEKXpEX1VFxsdwmUm', 'ADMIN', 'ACTIVE'),
('inst1', '$2a$12$4Ng7YhOVLnaWXxQV.6.xseNhg0L.dfMbGO.qPEKXpEX1VFxsdwmUm', 'INSTRUCTOR', 'ACTIVE'),
('inst2', '$2a$12$4Ng7YhOVLnaWXxQV.6.xseNhg0L.dfMbGO.qPEKXpEX1VFxsdwmUm', 'INSTRUCTOR', 'ACTIVE'),
('stu1', '$2a$12$4Ng7YhOVLnaWXxQV.6.xseNhg0L.dfMbGO.qPEKXpEX1VFxsdwmUm', 'STUDENT', 'ACTIVE'),
('stu2', '$2a$12$4Ng7YhOVLnaWXxQV.6.xseNhg0L.dfMbGO.qPEKXpEX1VFxsdwmUm', 'STUDENT', 'ACTIVE'),
('stu3', '$2a$12$4Ng7YhOVLnaWXxQV.6.xseNhg0L.dfMbGO.qPEKXpEX1VFxsdwmUm', 'STUDENT', 'ACTIVE');

-- Note: Replace the hash above with actual BCrypt hashes generated from your application
-- The sample hash is placeholder only
