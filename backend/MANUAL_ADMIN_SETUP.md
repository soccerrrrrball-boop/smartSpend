# Manual Admin Setup - Quick Fix

If the automatic seeder didn't work, follow these steps:

## Option 1: Check Backend Console

1. Look at the backend console window where Spring Boot is running
2. You should see one of these messages:
   - "Creating admin user and categories..." (seeder is running)
   - "Admin user already exists. Skipping admin creation." (admin exists but might have wrong password)
   - "Admin and categories setup completed successfully!" (success!)

## Option 2: Delete and Recreate Admin (Using SQL)

If admin exists but password is wrong, delete and let seeder recreate:

```sql
-- Connect to your MySQL database: expenses_tracker
-- Delete existing admin user if it exists
DELETE FROM user_roles WHERE user_id IN (SELECT id FROM users WHERE email = 'admin@gmail.com');
DELETE FROM users WHERE email = 'admin@gmail.com';

-- Then restart the backend - the seeder will create a fresh admin user
```

## Option 3: Generate BCrypt Hash and Use SQL

1. **Generate BCrypt Hash:**
   - Run this in your backend directory:
   ```bash
   mvnw exec:java -Dexec.mainClass="com.fullStack.expenseTracker.utils.PasswordHashGenerator"
   ```
   - Copy the generated hash

2. **Update SQL Script:**
   - Open `backend/insert_admin_and_categories.sql`
   - Replace the hash on line 23 with your generated hash

3. **Run SQL Script:**
   ```sql
   -- In MySQL
   USE expenses_tracker;
   SOURCE backend/insert_admin_and_categories.sql;
   ```

## Option 4: Force Seeder to Run

If you want to force the seeder to recreate admin (useful if password is wrong):

1. Temporarily modify `AdminAndCategoryDataSeeder.java` line 45:
   ```java
   // Change from:
   if (userRepository.existsByEmail("admin@gmail.com")) {
       return;
   }
   
   // To:
   if (userRepository.existsByEmail("admin@gmail.com")) {
       // Delete existing admin first
       User existingAdmin = userRepository.findByEmail("admin@gmail.com").orElse(null);
       if (existingAdmin != null) {
           userRepository.delete(existingAdmin);
       }
   }
   ```

2. Restart backend
3. Revert the change after admin is created

## Verify Admin User

After setup, verify in MySQL:

```sql
SELECT u.id, u.username, u.email, u.enabled, r.name as role 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id 
LEFT JOIN roles r ON ur.role_id = r.id 
WHERE u.email = 'admin@gmail.com';
```

You should see:
- username: admin
- email: admin@gmail.com
- enabled: 1 (true)
- role: ROLE_ADMIN

## Login Credentials

- **Email:** admin@gmail.com
- **Password:** admin@123

