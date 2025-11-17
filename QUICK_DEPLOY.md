# Quick Deployment Checklist

## Backend (Render) - 5 Steps

1. **Create Web Service on Render**
   - Connect GitHub repo
   - Build: `./mvnw clean package -DskipTests`
   - Start: `java -jar target/expenseTracker-0.0.1-SNAPSHOT.jar`

2. **Set Environment Variables:**
   ```
   SPRING_PROFILES_ACTIVE=production
   SPRING_DATASOURCE_URL=jdbc:mysql://host:port/db
   SPRING_DATASOURCE_USERNAME=user
   SPRING_DATASOURCE_PASSWORD=pass
   APP_JWT_SECRET=<generate-64-char-secret>
   APP_CORS_ALLOWED_ORIGINS=https://your-app.netlify.app,http://localhost:5000
   APP_USER_PROFILE_UPLOAD_DIR=/tmp/uploads
   ```

3. **Generate JWT Secret** (use this command):
   ```bash
   openssl rand -base64 64
   ```

4. **Deploy and copy backend URL**

5. **Update CORS** with your Netlify URL after frontend is deployed

---

## Frontend (Netlify) - 4 Steps

1. **Connect GitHub repo to Netlify**

2. **Set Build Settings:**
   - Base directory: `frontend`
   - Build command: `npm run build`
   - Publish: `frontend/build`

3. **Set Environment Variable:**
   ```
   REACT_APP_API_BASE_URL=https://your-backend.onrender.com/mypockit
   ```

4. **Deploy**

---

## Important URLs to Update

After deployment, update these:

1. **Render Environment Variable:**
   - `APP_CORS_ALLOWED_ORIGINS` → Add your Netlify URL

2. **Netlify Environment Variable:**
   - `REACT_APP_API_BASE_URL` → Your Render backend URL

---

## Testing

1. Visit your Netlify URL
2. Try to register/login
3. Check browser console for errors
4. Check Render logs if backend calls fail

---

## Common First-Time Issues

- **CORS Error**: Update `APP_CORS_ALLOWED_ORIGINS` in Render
- **401 Unauthorized**: Backend might be spinning up (wait 30 seconds)
- **Database Error**: Check connection string format
- **Build Fails**: Check Java version (needs 21)

