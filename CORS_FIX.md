# CORS Configuration Fix

## Current Issue
The `APP_CORS_ALLOWED_ORIGINS` environment variable in Render is incorrectly set to `value`, causing CORS errors when the frontend tries to access the backend API.

## Solution

### Update Render Environment Variable

1. Go to your Render dashboard: https://dashboard.render.com/
2. Navigate to your backend service (smartspend-1-g3ri)
3. Go to **Environment** tab
4. Find the `APP_CORS_ALLOWED_ORIGINS` variable
5. Update it to:
   ```
   https://visionary-griffin-a1c13e.netlify.app,http://localhost:5000
   ```
6. Click **Save Changes**
7. Render will automatically redeploy with the new CORS settings

## Current Configuration

- **Frontend URL**: `https://visionary-griffin-a1c13e.netlify.app`
- **Backend URL**: `https://smartspend-1-g3ri.onrender.com`
- **Local Development**: `http://localhost:5000`

## Expected Value

```
APP_CORS_ALLOWED_ORIGINS=https://visionary-griffin-a1c13e.netlify.app,http://localhost:5000
```

## Notes

- The comma-separated list allows multiple origins
- Include `http://localhost:5000` for local development
- After updating, wait for Render to redeploy (usually 1-2 minutes)
- The CORS error should be resolved once the redeployment completes

## Verification

After updating, test by:
1. Opening your Netlify app: https://visionary-griffin-a1c13e.netlify.app
2. Attempting to log in
3. Checking the browser console - CORS errors should be gone
4. The login request should succeed

