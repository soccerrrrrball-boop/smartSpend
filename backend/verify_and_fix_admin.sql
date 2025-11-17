-- Script to verify and fix admin user
-- Run this in your MySQL database: expenses_tracker

USE expenses_tracker;

-- Step 1: Check if admin exists
SELECT 'Checking for existing admin user...' as status;
SELECT u.id, u.username, u.email, u.enabled, r.name as role 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id 
LEFT JOIN roles r ON ur.role_id = r.id 
WHERE u.email = 'admin@gmail.com';

-- Step 2: Delete admin if it exists (to force recreation with correct password)
SELECT 'Deleting existing admin user (if exists)...' as status;
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE email = 'admin@gmail.com');
DELETE FROM users WHERE email = 'admin@gmail.com';

SELECT 'Admin user deleted. Now restart your Spring Boot backend.' as status;
SELECT 'The AdminAndCategoryDataSeeder will automatically create:' as info;
SELECT '  - Email: admin@gmail.com' as info;
SELECT '  - Password: admin@123' as info;
SELECT '  - Role: ROLE_ADMIN' as info;

