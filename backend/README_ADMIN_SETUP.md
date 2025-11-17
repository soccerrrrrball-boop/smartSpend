# Admin User and Categories Setup Guide

This guide will help you set up the admin user and sample categories in your database.

## Admin Credentials
- **Email:** admin@gmail.com
- **Password:** admin@123
- **Role:** ROLE_ADMIN

## Method 1: Using the SQL Script (Recommended)

1. **Generate BCrypt Hash for Password:**
   - Run the `PasswordHashGenerator.java` utility class:
   ```bash
   cd backend
   mvnw exec:java -Dexec.mainClass="com.fullStack.expenseTracker.utils.PasswordHashGenerator"
   ```
   - Copy the generated hash

2. **Update the SQL Script:**
   - Open `insert_admin_and_categories.sql`
   - Replace the BCrypt hash on line 22 with the one you generated

3. **Run the SQL Script:**
   - Open MySQL command line or MySQL Workbench
   - Connect to your database: `expenses_tracker`
   - Run the script:
   ```sql
   source backend/insert_admin_and_categories.sql;
   ```
   Or copy and paste the contents into your MySQL client

## Method 2: Generate Hash Using Spring Boot

If the application is running, you can also generate the hash by creating a temporary endpoint or using the Spring Boot console.

## What the Script Does:

1. **Inserts Admin User:**
   - Username: `admin`
   - Email: `admin@gmail.com`
   - Password: `admin@123` (BCrypt hashed)
   - Enabled: `true`
   - Role: `ROLE_ADMIN`

2. **Inserts Sample Categories:**

   **Expense Categories:**
   - Food & Dining
   - Transportation
   - Shopping
   - Bills & Utilities
   - Entertainment
   - Healthcare
   - Education
   - Travel
   - Personal Care
   - Gifts & Donations

   **Income Categories:**
   - Salary
   - Freelance
   - Investment
   - Business
   - Rental Income
   - Bonus
   - Gift Received
   - Other Income

## Verification

After running the script, verify the data:

```sql
-- Check admin user
SELECT u.id, u.username, u.email, r.name as role 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id 
WHERE u.email = 'admin@gmail.com';

-- Check categories
SELECT c.category_id, c.category_name, tt.transaction_type_name, c.enabled 
FROM category c 
JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id 
ORDER BY tt.transaction_type_name, c.category_name;
```

## Login

Once set up, you can log in to the application using:
- Email: `admin@gmail.com`
- Password: `admin@123`

You will have admin privileges and can manage users, categories, and transactions.

