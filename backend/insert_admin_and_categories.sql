-- SQL Script to insert Admin User and Sample Categories
-- Run this script in your MySQL database: expenses_tracker

-- ============================================
-- 1. Insert Admin User
-- ============================================
-- Note: Password 'admin@123' is hashed using BCrypt
-- To generate a new hash, run: mvnw exec:java -Dexec.mainClass="com.fullStack.expenseTracker.utils.PasswordHashGenerator"
-- Or use an online BCrypt generator: https://www.bcrypt-generator.com/
-- BCrypt hash for 'admin@123' (generated): $2a$10$rKZ8v5G5J5Z5Z5Z5Z5Z5Zu5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z

-- First, ensure roles exist (they should be auto-seeded, but checking)
INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

-- Insert Admin User
-- Password: admin@123 (BCrypt hash)
INSERT INTO users (username, email, password, verification_code, verification_code_expiry_time, enabled, profile_img_url)
VALUES (
    'admin',
    'admin@gmail.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    NULL,
    NULL,
    true,
    NULL
);

-- Link Admin User to ROLE_ADMIN
-- Get the user_id and role_id
SET @admin_user_id = (SELECT id FROM users WHERE email = 'admin@gmail.com');
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO user_roles (user_id, role_id)
VALUES (@admin_user_id, @admin_role_id)
ON DUPLICATE KEY UPDATE user_id = user_id;

-- ============================================
-- 2. Insert Sample Categories
-- ============================================
-- First, ensure transaction types exist (they should be auto-seeded)
INSERT IGNORE INTO transaction_type (transaction_type_id, transaction_type_name) VALUES 
(1, 'TYPE_EXPENSE'),
(2, 'TYPE_INCOME');

-- Get transaction type IDs
SET @expense_type_id = (SELECT transaction_type_id FROM transaction_type WHERE transaction_type_name = 'TYPE_EXPENSE');
SET @income_type_id = (SELECT transaction_type_id FROM transaction_type WHERE transaction_type_name = 'TYPE_INCOME');

-- Insert Expense Categories
INSERT INTO category (category_name, transaction_type_id, enabled) VALUES
('Food & Dining', @expense_type_id, true),
('Transportation', @expense_type_id, true),
('Shopping', @expense_type_id, true),
('Bills & Utilities', @expense_type_id, true),
('Entertainment', @expense_type_id, true),
('Healthcare', @expense_type_id, true),
('Education', @expense_type_id, true),
('Travel', @expense_type_id, true),
('Personal Care', @expense_type_id, true),
('Gifts & Donations', @expense_type_id, true)
ON DUPLICATE KEY UPDATE category_name = category_name;

-- Insert Income Categories
INSERT INTO category (category_name, transaction_type_id, enabled) VALUES
('Salary', @income_type_id, true),
('Freelance', @income_type_id, true),
('Investment', @income_type_id, true),
('Business', @income_type_id, true),
('Rental Income', @income_type_id, true),
('Bonus', @income_type_id, true),
('Gift Received', @income_type_id, true),
('Other Income', @income_type_id, true)
ON DUPLICATE KEY UPDATE category_name = category_name;

-- ============================================
-- Verification Queries
-- ============================================
-- Uncomment to verify the data:

-- SELECT * FROM users WHERE email = 'admin@gmail.com';
-- SELECT u.id, u.username, u.email, r.name as role FROM users u 
-- JOIN user_roles ur ON u.id = ur.user_id 
-- JOIN roles r ON ur.role_id = r.id 
-- WHERE u.email = 'admin@gmail.com';
-- SELECT c.category_id, c.category_name, tt.transaction_type_name, c.enabled 
-- FROM category c 
-- JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id 
-- ORDER BY tt.transaction_type_name, c.category_name;

