# Deployment Guide

This guide will help you deploy the Mypockit application:
- **Frontend**: Netlify
- **Backend**: Render

## Prerequisites

1. GitHub account with your code pushed to a repository
2. Netlify account (free tier available)
3. Render account (free tier available)
4. MySQL database (you can use Render's free PostgreSQL or external MySQL like PlanetScale, Railway, etc.)

---

## Backend Deployment (Render)

### Step 1: Prepare Database

1. Set up a MySQL database (options):
   - **Render PostgreSQL** (free tier)
   - **PlanetScale** (free tier, MySQL compatible)
   - **Railway** (free tier)
   - **Aiven** (free tier)

2. Note down your database connection details:
   - Database URL
   - Username
   - Password
   - Host and Port

### Step 2: Deploy to Render

1. Go to [Render Dashboard](https://dashboard.render.com/)
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository
4. Configure the service:
   - **Name**: `mypockit-backend`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/expenseTracker-0.0.1-SNAPSHOT.jar`
   - **Plan**: Free

5. Add Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE=production
   SPRING_DATASOURCE_URL=jdbc:mysql://your-host:3306/your-database
   SPRING_DATASOURCE_USERNAME=your-username
   SPRING_DATASOURCE_PASSWORD=your-password
   APP_JWT_SECRET=your-very-long-random-secret-key-here-minimum-64-characters
   APP_CORS_ALLOWED_ORIGINS=https://your-netlify-app.netlify.app,http://localhost:5000
   APP_USER_PROFILE_UPLOAD_DIR=/tmp/uploads
   ```

6. Click **"Create Web Service"**
7. Wait for deployment (first build takes 5-10 minutes)
8. Copy your backend URL (e.g., `https://mypockit-backend.onrender.com`)

### Step 3: Update Database Schema

After first deployment, the application will create tables automatically (if `ddl-auto=update`). You may need to:
- Run the admin seeder manually or wait for it to run on startup
- Create initial categories if needed

---

## Frontend Deployment (Netlify)

### Step 1: Update Environment Variable

1. In your local `frontend` folder, create/update `.env.production`:
   ```env
   REACT_APP_API_BASE_URL=https://your-backend-app.onrender.com/mypockit
   ```
   Replace `your-backend-app.onrender.com` with your actual Render backend URL.

2. Commit and push this change to GitHub.

### Step 2: Deploy to Netlify

1. Go to [Netlify Dashboard](https://app.netlify.com/)
2. Click **"Add new site"** → **"Import an existing project"**
3. Connect to GitHub and select your repository
4. Configure build settings:
   - **Base directory**: `frontend`
   - **Build command**: `npm run build`
   - **Publish directory**: `frontend/build`

5. Add Environment Variable:
   - **Key**: `REACT_APP_API_BASE_URL`
   - **Value**: `https://your-backend-app.onrender.com/mypockit`
   (Replace with your actual Render backend URL)

6. Click **"Deploy site"**
7. Wait for deployment (2-5 minutes)
8. Your site will be available at `https://random-name.netlify.app`

### Step 3: Update Backend CORS

1. Go back to Render dashboard
2. Update the `APP_CORS_ALLOWED_ORIGINS` environment variable:
   ```
   https://your-netlify-app.netlify.app,http://localhost:5000
   ```
   Replace `your-netlify-app.netlify.app` with your actual Netlify URL.

3. Render will automatically redeploy with the new CORS settings.

---

## Custom Domain (Optional)

### Netlify Custom Domain

1. In Netlify dashboard, go to **Site settings** → **Domain management**
2. Click **"Add custom domain"**
3. Follow the instructions to configure DNS

### Update CORS After Custom Domain

1. Update `APP_CORS_ALLOWED_ORIGINS` in Render:
   ```
   https://your-custom-domain.com,https://your-netlify-app.netlify.app,http://localhost:5000
   ```

2. Update `REACT_APP_API_BASE_URL` in Netlify if needed

---

## Environment Variables Summary

### Backend (Render)
```
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_URL=jdbc:mysql://host:port/database
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
APP_JWT_SECRET=your-secret-key
APP_CORS_ALLOWED_ORIGINS=https://your-frontend.netlify.app,http://localhost:5000
APP_USER_PROFILE_UPLOAD_DIR=/tmp/uploads
```

### Frontend (Netlify)
```
REACT_APP_API_BASE_URL=https://your-backend.onrender.com/mypockit
```

---

## Troubleshooting

### Backend Issues

1. **Build fails**: Check Java version (should be 21)
2. **Database connection fails**: Verify connection string and credentials
3. **CORS errors**: Ensure `APP_CORS_ALLOWED_ORIGINS` includes your frontend URL
4. **Application won't start**: Check Render logs for errors

### Frontend Issues

1. **API calls fail**: Verify `REACT_APP_API_BASE_URL` is set correctly
2. **Build fails**: Check Node version (should be 18+)
3. **404 on refresh**: Netlify redirects should handle this (configured in `netlify.toml`)

### Common Issues

1. **401 Unauthorized**: Backend might not be running or CORS is blocking
2. **Database errors**: Ensure database is accessible from Render
3. **Slow first request**: Render free tier spins down after 15 minutes of inactivity

---

## Notes

- **Render Free Tier**: Services spin down after 15 minutes of inactivity. First request after spin-down takes ~30 seconds.
- **Netlify Free Tier**: Unlimited builds and bandwidth (with some limits)
- **Database**: Consider using a managed database service for production
- **File Uploads**: Files uploaded to `/tmp/uploads` on Render will be lost on restart. Consider using cloud storage (S3, Cloudinary) for production.

---

## Support

If you encounter issues:
1. Check Render logs: Dashboard → Your Service → Logs
2. Check Netlify logs: Site → Deploys → Click on deploy → View build log
3. Verify all environment variables are set correctly
4. Ensure database is accessible and credentials are correct

