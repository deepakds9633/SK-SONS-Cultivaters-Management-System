-- ============================================
-- SK SONS Cultivaters Management System
-- MySQL Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS sk_sons_cultivaters
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sk_sons_cultivaters;


-- Clients Table
CREATE TABLE IF NOT EXISTS clients (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100) NOT NULL,
    phone             VARCHAR(20),
    village           VARCHAR(100),
    field_details     TEXT,
    total_charge      DOUBLE DEFAULT 0,
    total_paid        DOUBLE DEFAULT 0,
    pending_balance   DOUBLE DEFAULT 0
);

-- Drivers Table
CREATE TABLE IF NOT EXISTS drivers (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                  VARCHAR(100) NOT NULL,
    phone                 VARCHAR(20),
    license_number        VARCHAR(50),
    address               TEXT,
    daily_salary          DOUBLE DEFAULT 0,
    total_salary_earned   DOUBLE DEFAULT 0,
    total_salary_paid     DOUBLE DEFAULT 0,
    pending_salary        DOUBLE DEFAULT 0
);

-- Work Entries Table
CREATE TABLE IF NOT EXISTS work_entries (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id       BIGINT NOT NULL,
    vehicle_type    VARCHAR(50) NOT NULL,
    driver_id       BIGINT,
    work_date       DATE NOT NULL,
    start_time      TIME,
    end_time        TIME,
    total_minutes   INT,
    total_cost      DOUBLE,
    is_manual_cost  BOOLEAN DEFAULT FALSE,
    notes           TEXT,
    FOREIGN KEY (client_id)  REFERENCES clients(id)  ON DELETE CASCADE,
    FOREIGN KEY (driver_id)  REFERENCES drivers(id)  ON DELETE SET NULL
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id         BIGINT NOT NULL,
    work_entry_id     BIGINT,
    payment_date      DATE NOT NULL,
    paid_amount       DOUBLE,
    remaining_balance DOUBLE,
    payment_mode      VARCHAR(30) DEFAULT 'CASH',
    remarks           TEXT,
    FOREIGN KEY (client_id)     REFERENCES clients(id)      ON DELETE CASCADE,
    FOREIGN KEY (work_entry_id) REFERENCES work_entries(id) ON DELETE SET NULL
);

-- Attendance Table
CREATE TABLE IF NOT EXISTS attendance (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id       BIGINT NOT NULL,
    date            DATE NOT NULL,
    present         BOOLEAN DEFAULT FALSE,
    salary_for_day  DOUBLE DEFAULT 0,
    paid_amount     DOUBLE DEFAULT 0,
    pending_amount  DOUBLE DEFAULT 0,
    remarks         TEXT,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE,
    UNIQUE KEY unique_driver_date (driver_id, date)
);

