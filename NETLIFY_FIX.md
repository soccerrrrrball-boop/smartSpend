# Netlify Frontend Configuration Fix

## Issue
The login page shows "Something went wrong: Try again later!" because the frontend cannot connect to the backend API.

## Root Cause
The frontend is trying to use `http://localhost:8080/mypockit` (the default) instead of your Render backend URL because the environment variable is not set in Netlify.

## Solution

### Step 1: Set Environment Variable in Netlify

1. Go to your Netlify dashboard: https://app.netlify.com/
2. Select your site: `visionary-griffin-a1c13e`
3. Go to **Site settings** → **Environment variables**
4. Click **Add a variable**
5. Add the following:
   - **Key**: `REACT_APP_API_BASE_URL`
   - **Value**: `https://smartspend-1-g3ri.onrender.com/mypockit`
6. Click **Save**

### Step 2: Redeploy the Site

After adding the environment variable, you need to trigger a new deployment:

1. In Netlify, go to **Deploys** tab
2. Click **Trigger deploy** → **Deploy site**
3. Wait for the deployment to complete (2-3 minutes)

**OR** you can push any small change to your repository to trigger a new build.

### Step 3: Verify

1. After redeployment, visit: `https://visionary-griffin-a1c13e.netlify.app`
2. Try logging in with:
   - Email: `admin@gmail.com`
   - Password: `admin@123`
3. The login should now work!

## Current Configuration

- **Frontend URL**: `https://visionary-griffin-a1c13e.netlify.app`
- **Backend URL**: `https://smartspend-1-g3ri.onrender.com`
- **Required Environment Variable**: 
  ```
  REACT_APP_API_BASE_URL=https://smartspend-1-g3ri.onrender.com/mypockit
  ```

## Why This Happens

React apps need environment variables to be set at **build time**. The `REACT_APP_` prefix tells Create React App to include these variables in the built JavaScript bundle. If the variable isn't set during the build, it defaults to `http://localhost:8080/mypockit`, which doesn't work from a deployed site.

## Troubleshooting

If login still doesn't work after setting the variable:

1. **Clear browser cache** (Ctrl+Shift+Delete)
2. **Hard refresh** the page (Ctrl+Shift+R or Ctrl+F5)
3. **Check browser console** (F12) for any error messages
4. **Verify the environment variable** is set correctly in Netlify
5. **Check that the backend is running** by visiting: `https://smartspend-1-g3ri.onrender.com/health`

