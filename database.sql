-- ═══════════════════════════════════════════
--  Billing System - Database Setup
--  MySQL mein run karo
-- ═══════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS billing;
USE billing;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- Invoices Table (ek invoice = ek customer ka ek bill)
CREATE TABLE IF NOT EXISTS invoices (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(50) NOT NULL,
    invoice_type VARCHAR(20) NOT NULL,   -- Product / Service / Subscription
    total        DOUBLE NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- Invoice Items Table (ek invoice mein multiple items)
CREATE TABLE IF NOT EXISTS invoice_items (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT NOT NULL,
    item_name  VARCHAR(100) NOT NULL,
    quantity   INT NOT NULL,
    price      DOUBLE NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
);

-- Test user add karo
INSERT IGNORE INTO users (username, password) VALUES ('ali', '1234');
INSERT IGNORE INTO users (username, password) VALUES ('sara', '1234');
