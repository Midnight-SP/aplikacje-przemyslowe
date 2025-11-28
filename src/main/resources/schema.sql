-- Schema for TechCorp Employee Management System
-- Creates employees table with H2 database

CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    position VARCHAR(50) NOT NULL,
    salary DECIMAL(19,2) NOT NULL,
    status VARCHAR(50),
    photo_file_name VARCHAR(255),
    department_id BIGINT
);

-- Index on email for faster lookups
CREATE INDEX IF NOT EXISTS idx_employee_email ON employees(email);

-- Index on company_name for statistics queries
CREATE INDEX IF NOT EXISTS idx_employee_company ON employees(company_name);
