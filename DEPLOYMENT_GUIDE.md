# 🚀 Student PDF Generator - Cloud Deployment Guide

## 📋 Deployment Summary

### **✅ Completed:**
- **GitHub Repository**: Updated with latest changes
- **Vercel Deployment**: Frontend files ready
- **Render Configuration**: Backend configuration ready
- **Environment Variables**: Configured for production

---

## 🌐 Frontend Deployment (Vercel)

### **Method 1: Vercel CLI**
```bash
# Navigate to deployment folder
cd "/Users/shreyanshgupta/Documents/6th Sem /student-pdf-generator/vercel-deploy"

# Deploy to production
npx vercel --prod
```

### **Method 2: Vercel Dashboard**
1. Go to [Vercel Dashboard](https://vercel.com/dashboard)
2. Click **"New Project"**
3. **Import Git Repository**: https://github.com/Shreyansh123185655/sdc-pbl.git
4. **Root Directory**: `vercel-deploy`
5. **Framework Preset**: Other
6. **Build Settings**: Use `vercel.json`
7. **Deploy** → Get URL

### **Expected Frontend URL:**
```
https://student-pdf-generator.vercel.app
```

---

## ⚡ Backend Deployment (Render)

### **Step 1: Deploy Java Service**
1. Go to [Render Dashboard](https://render.com)
2. Click **"New +"** → **"Web Service"**
3. **Connect Repository**: https://github.com/Shreyansh123185655/sdc-pbl.git
4. **Name**: `student-pdf-java`
5. **Root Directory**: `java_service`
6. **Runtime**: Docker
7. **Dockerfile Path**: `Dockerfile`
8. **Port**: 8081
9. **Click "Create Web Service"**

### **Step 2: Deploy Backend API**
1. Click **"New +"** → **"Web Service"**
2. **Connect Repository**: Same repository
3. **Name**: `student-pdf-backend`
4. **Root Directory**: `backend_fastapi`
5. **Runtime**: Python 3.9+
6. **Build Command**: `pip install -r requirements.txt`
7. **Start Command**: `uvicorn main:app --host 0.0.0.0 --port $PORT`
8. **Environment Variables**:
   - `JAVA_SERVICE_URL`: `{Your Java service URL from Step 1}`
   - `PORT`: 8000
9. **Health Check Path**: `/health`
10. **Click "Create Web Service"**

### **Expected Backend URLs:**
```
Java Service: https://student-pdf-java.onrender.com
Backend API: https://student-pdf-backend.onrender.com
```

---

## 🔗 Post-Deployment Configuration

### **Update Frontend URLs**
After deployment, update the frontend to use production URLs:

```javascript
// In vercel-deploy/index.html
const API_BASE = () => 'https://student-pdf-backend.onrender.com';
```

### **Test All Services**
1. **Frontend**: Visit Vercel URL
2. **Backend Health**: `https://student-pdf-backend.onrender.com/health`
3. **Java Service**: `https://student-pdf-java.onrender.com/api/status`

---

## 📱 Final Application URLs

| Service | URL | Purpose |
|---------|-----|---------|
| **Frontend** | https://student-pdf-generator.vercel.app | Main application |
| **Backend API** | https://student-pdf-backend.onrender.com/docs | API documentation |
| **Java Service** | https://student-pdf-java.onrender.com/api/status | PDF generation |

---

## 🎯 Quick Deployment Commands

```bash
# Deploy Frontend
cd vercel-deploy && npx vercel --prod

# Deploy Backend (via Render Dashboard)
# Visit: https://render.com/dashboard
```

---

## ✅ Deployment Checklist

### **Frontend (Vercel):**
- [ ] Deploy vercel-deploy folder
- [ ] Update API URLs if needed
- [ ] Test file upload functionality
- [ ] Verify UI interactions work

### **Backend (Render):**
- [ ] Deploy Java service (Docker)
- [ ] Deploy FastAPI backend
- [ ] Set environment variables
- [ ] Test API endpoints
- [ ] Verify PDF generation

### **Integration:**
- [ ] Test full workflow
- [ ] Verify CORS configuration
- [ ] Test file upload → PDF generation
- [ ] Check error handling

---

**🎉 Your Student PDF Generator is ready for cloud deployment!**

Follow the steps above and you'll have a fully functional cloud application with Universal Excel support! 🚀
