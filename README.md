# Student PDF Generator

🎓 Universal Excel to Student PDF Generator with Interactive UI

## 🌐 Live Demo

- **Frontend**: https://student-pdf-generator.vercel.app
- **Backend API**: https://student-pdf-backend.onrender.com/docs
- **API Health**: https://student-pdf-backend.onrender.com/health

## 🚀 Quick Start

### Prerequisites
- Python 3.11+
- Java 17+
- Maven 3.6+
- Docker Desktop (optional)

### Local Setup

#### 1. Backend (FastAPI)
```bash
cd backend_fastapi
source ../.venv/bin/activate
uvicorn main:app --host 0.0.0.0 --port 8000
```

#### 2. Java Service
```bash
cd java_service
mvn clean package
java -jar target/student-pdf-service-1.0.0.jar --server.port=8081
```

#### 3. Frontend
```bash
# Open in browser
http://localhost:8000/ui
```

## 🐳 Docker Deployment

### Build & Run All Services
```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# Access at: http://localhost:8000
```

### Individual Services
```bash
# Backend only
docker-compose up backend

# Java service only
docker-compose up java-service

# Frontend only
docker-compose up frontend
```

## 📦 VPS Deployment

### 1. Export Docker Images
```bash
# Export images
docker save student-pdf-backend:latest > backend.tar
docker save student-pdf-java:latest > java.tar
docker save student-pdf-frontend:latest > frontend.tar
```

### 2. Transfer to VPS
```bash
# Copy to VPS
scp backend.tar user@vps-ip:/path/
scp java.tar user@vps-ip:/path/
scp frontend.tar user@vps-ip:/path/
scp docker-compose.yml user@vps-ip:/path/
```

### 3. Import on VPS
```bash
# On VPS
docker load < backend.tar
docker load < java.tar
docker load < frontend.tar

# Start services
docker-compose up -d
```

## 🎯 Features

- ✅ Universal Excel Support (any format)
- ✅ Smart Structure Detection
- ✅ Interactive UI with animations
- ✅ Real-time file analysis
- ✅ PDF generation for individual students
- ✅ Bulk download support
- ✅ Docker containerization

## 📊 Project Structure

```
student-pdf-generator/
├── backend_fastapi/     # FastAPI backend
├── java_service/        # Java PDF service
├── frontend/           # Interactive web UI
├── vercel-deploy/      # Vercel deployment
├── docker-compose.yml  # Docker orchestration
└── README.md          # This file
```

## 🔗 Links

- **Live App**: https://student-pdf-generator.vercel.app
- **GitHub**: https://github.com/Shreyansh123185655/sdc-pbl
- **API Docs**: https://student-pdf-backend.onrender.com/docs

---

**Made with ❤️ for educational institutions**
### **Java Service Port**
```bash
# Default: 8081
java -jar target/student-pdf-service-1.0.0.jar --server.port=8081
```

### **FastAPI Port**
```bash
# Default: 8000
uvicorn main:app --port 8000
```

### **Output Directory**
```bash
# Default: ./output/
# Can be changed in application.properties
```

---

## 🐛 Troubleshooting

### **Port Already in Use**
```bash
# Check ports
lsof -i :8000  # FastAPI
lsof -i :8081  # Java

# Kill processes
kill -9 <PID>

# Use different ports
uvicorn main:app --port 8001
java -jar target/student-pdf-service-1.0.0.jar --server.port=8082
```

### **Excel File Issues**
- **Any Excel format works** - just upload it
- **System auto-detects** structure
- **Auto-converts** if needed
- **No manual formatting** required

### **Service Not Starting**
```bash
# Check Java version (needs 17+)
java -version

# Check Python version (needs 3.9+)
python --version

# Check Maven (needs 3.8+)
mvn --version
```

---

## 📊 API Endpoints

| Method | Endpoint | Description |
|---------|-----------|-------------|
| GET | `/health` | Service health |
| GET | `/java-status` | Java service status |
| POST | `/upload` | Upload & generate PDFs |
| POST | `/analyze-excel` | Analyze Excel structure |
| POST | `/convert-excel` | Convert Excel format |

---

## 🎯 Quick Commands

```bash
# Start everything (run in separate terminals)
cd java_service && java -jar target/student-pdf-service-1.0.0.jar --server.port=8081
source .venv/bin/activate && cd backend_fastapi && uvicorn main:app --port 8000

# Test conversion
cd sample_data && ./convert_any_excel.sh test_file.xlsx

# Generate sample data
cd sample_data && python generate_sample.py
```

---

## 📱 Access Points

- **Web Interface**: http://localhost:8000/ui
- **API Docs**: http://localhost:8000/docs
- **Java Status**: http://localhost:8081/api/status
- **File Downloads**: http://localhost:8081/api/files

---

**🎉 Upload any Excel file and generate PDFs instantly!**

The system automatically detects your Excel format and converts it if needed. No manual formatting required.
