# 🎯 Render Deployment Form - What to Fill

## 📋 **Render Web Service Configuration**

### **🔧 Basic Settings:**

#### **Name:**
```
student-pdf-backend
```

#### **Environment:**
```
Python 3.9+
```

#### **Region:**
```
Choose closest to your location (e.g., Oregon, Frankfurt)
```

---

### **📁 Repository Settings:**

#### **Repository:**
```
Shreyansh123185655/sdc-pbl
```

#### **Root Directory:**
```
backend_fastapi
```

#### **Branch:**
```
main
```

---

### **🔨 Build Settings:**

#### **Build Command:**
```
pip install -r requirements.txt
```

#### **Start Command:**
```
uvicorn main:app --host 0.0.0.0 --port $PORT
```

---

### **⚙️ Environment Variables:**

#### **Add Environment Variables:**

**Variable 1:**
- **Key**: `JAVA_SERVICE_URL`
- **Value**: `https://student-pdf-java.onrender.com`
- **Type**: `Plain`

**Variable 2:**
- **Key**: `PORT`
- **Value**: `8000`
- **Type**: `Plain`

---

### **🏥 Health Check:**

#### **Health Check Path:**
```
/health
```

#### **Health Check Timeout:**
```
30 seconds
```

---

### **📊 Advanced Settings:**

#### **Instance Type:**
```
Free (or choose paid plan for better performance)
```

#### **Auto-Deploy:**
```
✅ Yes (to auto-deploy on git push)
```

---

### **🔗 Next Steps After Backend:**

#### **Deploy Java Service Separately:**
1. **Name**: `student-pdf-java`
2. **Root Directory**: `java_service`
3. **Runtime**: `Docker`
4. **Dockerfile Path**: `Dockerfile`
5. **Port**: `8081`

---

## 🎯 **Complete Form Summary:**

```
┌─────────────────────────────────────┐
│ Name: student-pdf-backend           │
│ Environment: Python 3.9+            │
│ Repository: Shreyansh123185655/sdc-pbl │
│ Root Directory: backend_fastapi      │
│ Build Command: pip install -r requirements.txt │
│ Start Command: uvicorn main:app --host 0.0.0.0 --port $PORT │
│ Health Check: /health               │
│                                     │
│ Environment Variables:              │
│ JAVA_SERVICE_URL = https://student-pdf-java.onrender.com │
│ PORT = 8000                         │
└─────────────────────────────────────┘
```

---

## 🚀 **After Deployment:**

1. **Copy Backend URL**: `https://student-pdf-backend.onrender.com`
2. **Deploy Java Service**: Use Docker configuration
3. **Update Frontend**: Replace localhost with production URLs
4. **Test Integration**: Verify all services work together

---

**📝 Fill out the Render form exactly as shown above for successful deployment!**
