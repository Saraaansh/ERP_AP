-- ============================================
-- ERP Database Schema
-- University ERP System
-- ============================================

DROP DATABASE IF EXISTS erp_db;
CREATE DATABASE erp_db;
USE erp_db;

-- ============================================
-- STUDENTS TABLE
-- ============================================
CREATE TABLE students (
    user_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    roll_no VARCHAR(50) UNIQUE NOT NULL,
    program VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- INSTRUCTORS TABLE
-- ============================================
CREATE TABLE instructors (
    user_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    department VARCHAR(100),
    email VARCHAR(255),
    office_hours VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- COURSES TABLE
-- ============================================
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    credits INT NOT NULL CHECK (credits > 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- SECTIONS TABLE
-- ============================================
CREATE TABLE sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    instructor_id INT,
    capacity INT NOT NULL CHECK (capacity > 0),
    day_time VARCHAR(255),
    room VARCHAR(50),
    semester VARCHAR(50),
    year INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id) ON DELETE SET NULL
);

-- ============================================
-- ENROLLMENTS TABLE
-- ============================================
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status ENUM('active', 'dropped', 'completed') DEFAULT 'active',
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, section_id)
);

-- ============================================
-- GRADES TABLE
-- ============================================
CREATE TABLE grades (
    grade_id INT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id INT NOT NULL,
    component VARCHAR(100) NOT NULL,
    score DOUBLE NOT NULL CHECK (score >= 0 AND score <= 100),
    final_grade VARCHAR(5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE
);

-- ============================================
-- ASSESSMENTS TABLE
-- ============================================
CREATE TABLE assessments (
    assessment_id INT AUTO_INCREMENT PRIMARY KEY,
    section_id INT NOT NULL,
    component_name VARCHAR(100) NOT NULL,
    weight INT NOT NULL CHECK (weight > 0 AND weight <= 100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY unique_section_component (section_id, component_name)
);

-- ============================================
-- TERMS TABLE
-- ============================================
CREATE TABLE terms (
    term_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    drop_deadline DATE NOT NULL,
    registration_deadline DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- SETTINGS TABLE
-- ============================================
CREATE TABLE settings (
    settings_key VARCHAR(100) PRIMARY KEY,
    value VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Sample Students (matching auth_db user_ids 4, 5, 6)
INSERT INTO students (user_id, name, roll_no, program, year, email) VALUES
(4, 'Alice Johnson', 'STU001', 'Computer Science', 2, 'alice@university.edu'),
(5, 'Bob Smith', 'STU002', 'Computer Science', 2, 'bob@university.edu'),
(6, 'Charlie Brown', 'STU003', 'Information Technology', 3, 'charlie@university.edu');

-- Sample Instructors (matching auth_db user_ids 2, 3)
INSERT INTO instructors (user_id, name, department, email, office_hours) VALUES
(2, 'Dr. Sarah Williams', 'Computer Science', 'swilliams@university.edu', 'MWF 2-4 PM'),
(3, 'Prof. John Davis', 'Information Technology', 'jdavis@university.edu', 'TTh 10-12 PM');

-- Sample Courses
INSERT INTO courses (code, title, credits, description) VALUES
('CS101', 'Introduction to Programming', 3, 'Basic programming concepts using Java'),
('CS201', 'Data Structures', 4, 'Advanced data structures and algorithms'),
('CS301', 'Database Systems', 3, 'Relational database design and SQL'),
('IT101', 'Web Development', 3, 'HTML, CSS, JavaScript and modern web frameworks'),
('IT201', 'Network Security', 3, 'Principles of network security and cryptography');

-- Sample Sections
INSERT INTO sections (course_id, instructor_id, capacity, day_time, room, semester, year) VALUES
(1, 2, 30, 'MWF 9:00-10:00', 'Room 101', 'Spring', 2025),
(2, 2, 25, 'TTh 11:00-12:30', 'Room 102', 'Spring', 2025),
(3, 3, 30, 'MWF 2:00-3:00', 'Room 201', 'Spring', 2025),
(4, 3, 20, 'TTh 9:00-10:30', 'Room 103', 'Spring', 2025);

-- Sample Enrollments
INSERT INTO enrollments (student_id, section_id, status) VALUES
(4, 1, 'active'),
(4, 2, 'active'),
(5, 1, 'active'),
(5, 3, 'active'),
(6, 4, 'active');

-- Sample Grades
INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES
(1, 'Quiz', 85.0, NULL),
(1, 'Midterm', 78.0, NULL),
(1, 'Final', 82.0, NULL),
(2, 'Quiz', 90.0, NULL),
(3, 'Quiz', 75.0, NULL),
(3, 'Midterm', 80.0, NULL);

-- Sample Assessments
INSERT INTO assessments (section_id, component_name, weight) VALUES
(1, 'Quiz', 20),
(1, 'Midterm', 30),
(1, 'Final', 50),
(2, 'Quiz', 15),
(2, 'Assignment', 25),
(2, 'Midterm', 30),
(2, 'Final', 30),
(3, 'Quiz', 20),
(3, 'Midterm', 35),
(3, 'Final', 45);

-- Sample Terms
INSERT INTO terms (name, start_date, end_date, drop_deadline, registration_deadline) VALUES
('Spring 2025', '2025-01-15', '2025-05-15', '2025-02-15', '2025-01-10'),
('Fall 2024', '2024-08-20', '2024-12-20', '2024-09-20', '2024-08-15'),
('Summer 2025', '2025-06-01', '2025-07-31', '2025-06-15', '2025-05-25');

-- System Settings
INSERT INTO settings (settings_key, value) VALUES
('maintenance', 'false'),
('maintenance_message', 'System is currently under maintenance. Please try again later.'),
('min_password_length', '8'),
('max_failed_attempts', '3'),
('lock_duration_minutes', '15');

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_section ON enrollments(section_id);
CREATE INDEX idx_grades_enrollment ON grades(enrollment_id);
CREATE INDEX idx_sections_course ON sections(course_id);
CREATE INDEX idx_sections_instructor ON sections(instructor_id);
CREATE INDEX idx_assessments_section ON assessments(section_id);
