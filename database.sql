CREATE DATABASE IF NOT EXISTS banking_management;
USE banking_management;

CREATE TABLE IF NOT EXISTS customers (
  acno BIGINT PRIMARY KEY,
  cname VARCHAR(100) NOT NULL,
  email VARCHAR(150),
  accounttype VARCHAR(30) NOT NULL,
  bal DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  CHECK (bal >= 0)
);

CREATE TABLE IF NOT EXISTS transactions (
  tid BIGINT AUTO_INCREMENT PRIMARY KEY,
  transaction_type VARCHAR(20) NOT NULL,
  acno BIGINT NOT NULL,
  amount DECIMAL(15,2) NOT NULL,
  transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (acno) REFERENCES customers(acno)
);

CREATE TABLE IF NOT EXISTS app_users (
  username VARCHAR(50) PRIMARY KEY,
  password VARCHAR(255) NOT NULL
);

INSERT IGNORE INTO app_users(username, password) VALUES ('admin', 'admin123');
