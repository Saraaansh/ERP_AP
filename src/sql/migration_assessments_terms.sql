-- Migration script for Assessment and Term tables
-- Run this on erp_db database

USE erp_db;

-- Create assessments table
CREATE TABLE IF NOT EXISTS assessments (
    assessment_id INT AUTO_INCREMENT PRIMARY KEY,
    section_id INT NOT NULL,
    component_name VARCHAR(100) NOT NULL,
    weight INT NOT NULL CHECK (weight > 0 AND weight <= 100),
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY unique_section_component (section_id, component_name)
);

-- Create terms table for academic calendar
CREATE TABLE IF NOT EXISTS terms (
    term_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    drop_deadline DATE NOT NULL
);

-- Add sample term data
INSERT INTO terms (name, start_date, end_date, drop_deadline) VALUES
('Spring 2025', '2025-01-15', '2025-05-15', '2025-02-15'),
('Fall 2024', '2024-08-15', '2024-12-15', '2024-09-15')
ON DUPLICATE KEY UPDATE name=name;

-- Update sections table to include semester and year if not already present
-- (These columns already exist in the schema, so this is just a safety check)
-- ALTER TABLE sections 
-- ADD COLUMN IF NOT EXISTS semester VARCHAR(50),
-- ADD COLUMN IF NOT EXISTS year INT;
