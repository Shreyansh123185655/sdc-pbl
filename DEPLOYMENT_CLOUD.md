# Student PDF Generator - Backend Deployment

## 🚀 Render.com Deployment

### **1. Prepare Backend for Render**

#### **Update FastAPI for Production**
```python
# backend_fastapi/main.py - Add CORS for production
from fastapi.middleware.cors import CORSMiddleware

app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://your-frontend.vercel.app", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

#### **Create render.yaml**
```yaml
services:
  - type: web
    name: student-pdf-backend
    env: python
    plan: free
    buildCommand: "pip install -r requirements.txt"
    startCommand: "uvicorn main:app --host 0.0.0.0 --port $PORT"
    envVars:
      - key: JAVA_SERVICE_URL
        value: https://your-java-service.onrender.com
```

### **2. Deploy to Render**

1. **Create new Web Service** on Render Dashboard
2. **Connect GitHub repository**
3. **Root directory**: `backend_fastapi/`
4. **Runtime**: Python 3.9+
5. **Build Command**: `pip install -r requirements.txt`
6. **Start Command**: `uvicorn main:app --host 0.0.0.0 --port $PORT`
7. **Add Environment Variables**:
   - `JAVA_SERVICE_URL`: Your Java service URL
   - `PORT`: 8000

### **3. Java Service Deployment**

#### **Create Dockerfile for Java Service**
```dockerfile
# java_service/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/student-pdf-service-1.0.0.jar app.jar
COPY application.properties .

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

#### **Deploy Java Service**
1. **Create new Web Service** on Render
2. **Root directory**: `java_service/`
3. **Runtime**: Docker
4. **Dockerfile Path**: `Dockerfile`
5. **Port**: 8081

---

## 🌐 Vercel Frontend Deployment

### **1. Deploy Frontend to Vercel**

#### **Method 1: Vercel CLI**
```bash
# Install Vercel CLI
npm i -g vercel

# Deploy from project root
vercel --prod
```

#### **Method 2: Vercel Dashboard**
1. **Import Project** on Vercel Dashboard
2. **Connect GitHub repository**
3. **Framework Preset**: Other
4. **Build Settings**: Use `vercel.json`
5. **Output Directory**: `frontend`

### **2. Update Frontend API URL**

#### **In frontend/index.html**
```javascript
// Update API_BASE() function
function API_BASE() {
  // Production: Render backend
  return 'https://your-backend.onrender.com';
  
  // Development: Local backend
  // return 'http://localhost:8000';
}
```

---

## 🔗 Connecting Services

### **1. Update Service URLs**

#### **Backend Configuration**
```python
# backend_fastapi/main.py
JAVA_SERVICE_URL = os.getenv("JAVA_SERVICE_URL", "http://localhost:8081")
```

#### **Frontend Configuration**
```javascript
// frontend/index.html
const JAVA_BASE = () => {
  return 'https://your-java-service.onrender.com';
};
```

### **2. CORS Configuration**

#### **Java Service CORS**
```java
// java_service/src/main/java/com/studentpdf/config/WebConfig.java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("https://your-frontend.vercel.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
```

---

## 📋 Deployment Checklist

### **✅ Pre-Deployment**
- [ ] Update CORS origins in all services
- [ ] Set production URLs in configuration
- [ ] Test locally with production URLs
- [ ] Add environment variables for secrets
- [ ] Optimize assets for production

### **✅ Post-Deployment**
- [ ] Test API endpoints
- [ ] Verify file upload functionality
- [ ] Test PDF generation
- [ ] Check error handling
- [ ] Monitor logs for issues

---

## 🚀 Quick Deploy Commands

```bash
# Deploy Frontend (Vercel)
vercel --prod

# Deploy Backend (Render - via GitHub)
git push origin main

# Deploy Java Service (Render - via GitHub)
git push origin main
```

---

## 🌐 Final URLs Structure

- **Frontend**: https://student-pdf-generator.vercel.app
- **Backend API**: https://student-pdf-backend.onrender.com
- **Java Service**: https://student-pdf-java.onrender.com

---

**🎯 All services deployed and connected!**
