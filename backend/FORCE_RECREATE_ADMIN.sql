-- Quick SQL script to delete and force recreate admin user
-- Run this in MySQL, then restart the backend to trigger the seeder

USE expenses_tracker;

-- Delete existing admin user and its role associations
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE email = 'admin@gmail.com');
DELETE FROM users WHERE email = 'admin@gmail.com';

-- After running this, restart your Spring Boot backend
-- The AdminAndCategoryDataSeeder will automatically create a fresh admin user
-- with email: admin@gmail.com and password: admin@123

