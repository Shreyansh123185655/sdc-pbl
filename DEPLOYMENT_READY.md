# Student PDF Generator - Production Deployment

## 🚀 Ready for Cloud Deployment

### **🌐 Frontend (Vercel)**
- **Status**: Ready to deploy
- **Files**: `frontend/index.html`, `vercel.json`
- **URL**: Will be `https://student-pdf-generator.vercel.app`

### **⚡ Backend (Render)**
- **Status**: Ready to deploy  
- **Files**: `backend_fastapi/`, `requirements.txt`
- **URL**: Will be `https://student-pdf-backend.onrender.com`

### **☕ Java Service (Render)**
- **Status**: Ready to deploy
- **Files**: `java_service/`, `Dockerfile`
- **URL**: Will be `https://student-pdf-java.onrender.com`

---

## 📋 Deployment Steps

### **1. Deploy Java Service to Render**
1. Go to [Render Dashboard](https://render.com)
2. Click "New +" → "Web Service"
3. Connect GitHub repository
4. **Root Directory**: `java_service`
5. **Runtime**: Docker
6. **Dockerfile Path**: `Dockerfile`
7. **Port**: 8081
8. Click "Create Web Service"

### **2. Deploy Backend to Render**
1. Click "New +" → "Web Service" 
2. Connect same GitHub repository
3. **Root Directory**: `backend_fastapi`
4. **Runtime**: Python 3.9+
5. **Build Command**: `pip install -r requirements.txt`
6. **Start Command**: `uvicorn main:app --host 0.0.0.0 --port $PORT`
7. **Environment Variables**:
   - `JAVA_SERVICE_URL`: `{Your Java Service URL}`
   - `PORT`: 8000
8. Click "Create Web Service"

### **3. Deploy Frontend to Vercel**
1. Install Vercel CLI: `npm i -g vercel`
2. Run: `vercel --prod`
3. Follow prompts to connect GitHub
4. Deploy from project root

---

## 🔧 Configuration Updates Needed

### **After Deployment, Update These URLs:**

#### **Frontend (frontend/index.html)**
```javascript
// Update API_BASE() function
function API_BASE() {
  return 'https://student-pdf-backend.onrender.com';
}

// Update JAVA_BASE() function  
const JAVA_BASE = () => 'https://student-pdf-java.onrender.com';
```

#### **Backend (backend_fastapi/main.py)**
```python
# Update environment variables
JAVA_SERVICE_URL = os.getenv("JAVA_SERVICE_URL", "https://student-pdf-java.onrender.com")
```

#### **Java Service (java_service/config/WebConfig.java)**
```java
// Update CORS origins
.allowedOrigins("https://student-pdf-generator.vercel.app")
```

---

## 🎯 Production URLs

Once deployed, your application will be available at:

- **🌐 Main App**: https://student-pdf-generator.vercel.app
- **⚡ API**: https://student-pdf-backend.onrender.com/docs
- **☕ PDF Service**: https://student-pdf-java.onrender.com/api/status

---

## ✅ Testing Production

### **Test Checklist:**
- [ ] Frontend loads correctly
- [ ] API endpoints respond
- [ ] File upload works
- [ ] PDF generation works
- [ ] Cross-origin requests allowed
- [ ] Error handling works

---

**🚀 Your Student PDF Generator is ready for cloud deployment!**
