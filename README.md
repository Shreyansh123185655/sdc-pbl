# Student PDF Generator

🎓 Universal Excel to Student PDF Generator with Interactive UI

## 🌐 Live Demo

- **Frontend**: https://student-pdf-generator.vercel.app
- **Backend API**: https://student-pdf-backend.onrender.com/docs
- **API Health**: https://student-pdf-backend.onrender.com/health

## 🚀 Quick Installation & Setup

### Prerequisites
- Python 3.11+
- Java 17+
- Maven 3.6+
- Docker Desktop (optional)

### One-Command Installation

#### Option 1: Automated Setup (Recommended)
```bash
# Clone the repository
git clone https://github.com/Shreyansh123185655/sdc-pbl.git
cd sdc-pbl

# Run automated setup
chmod +x setup.sh
./setup.sh
```

#### Option 2: Manual Setup
```bash
# 1. Setup Python Environment
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
pip install -r backend_fastapi/requirements.txt

# 2. Build Java Service
cd java_service
mvn clean package
cd ..

# 3. Start Services
# Start Java Service
cd java_service
java -jar target/student-pdf-service-1.0.0.jar --server.port=8081 &

# Start Backend
cd backend_fastapi
source ../.venv/bin/activate
uvicorn main:app --host 0.0.0.0 --port 8000 &

# Start Frontend
cd frontend
python -m http.server 3000
```

## 🐳 Docker Deployment (All-in-One)

### Quick Start
```bash
# Build and run all services
docker-compose up -d

# Access application
http://localhost:8000/ui
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

### One-Command VPS Deploy
```bash
# Export and deploy
./export-for-vps.sh

# Transfer to VPS
scp student-pdf-vps-deploy.tar.gz user@vps-ip:/opt/

# Deploy on VPS
ssh user@vps-ip
cd /opt
tar -xzf student-pdf-vps-deploy.tar.gz
cd docker-images
./deploy-vps.sh
```

## 🎯 Features

- ✅ **Universal Excel Support** - Handles any Excel format automatically
- ✅ **Smart Structure Detection** - Identifies student vs question bank format
- ✅ **Interactive UI** - Modern drag-and-drop interface
- ✅ **Real-time Analysis** - Instant file structure feedback
- ✅ **Format Conversion** - Question bank to student format
- ✅ **PDF Generation** - Individual student papers
- ✅ **Bulk Operations** - Process multiple students at once
- ✅ **Mobile Responsive** - Works on all devices

## 📊 Project Structure

```
student-pdf-generator/
├── 📁 backend_fastapi/           # FastAPI backend
│   ├── main.py                 # Main FastAPI application
│   ├── requirements.txt          # Python dependencies
│   └── Dockerfile              # Docker configuration
├── 📁 java_service/              # Java PDF service
│   ├── src/main/java/          # Java source code
│   ├── pom.xml                 # Maven configuration
│   ├── target/                 # Compiled JAR file
│   └── Dockerfile              # Docker configuration
├── 📁 frontend/                 # Web interface
│   ├── index.html              # Main application UI
│   └── Dockerfile              # Docker configuration
├── 📁 sample_data/              # Sample files and utilities
│   ├── file1_basic_students.xlsx      # 10 students, 5 questions
│   ├── file2_advanced_students.xlsx   # 15 students, 10 questions
│   ├── file3_simple_students.xlsx    # 5 students, 3 questions
│   ├── file4_complete_students.xlsx   # 8 students, 15 questions
│   └── create_sample_files.py    # Generate sample files
├── 📁 vercel-deploy/             # Vercel deployment files
├── 🐳 docker-compose.yml         # Docker orchestration
├── 🚀 setup.sh                  # Automated setup script
└── 📖 README.md                 # This file
```

## 🔧 Configuration

### Environment Variables
```bash
# Backend Configuration
JAVA_SERVICE_URL=http://localhost:8081
PORT=8000

# Java Service Configuration
SERVER_PORT=8081
OUTPUT_DIRECTORY=/app/output
```

### Service URLs
```bash
# Development
Frontend:  http://localhost:3000
Backend:   http://localhost:8000
Java:       http://localhost:8081

# Production
Frontend:  https://student-pdf-generator.vercel.app
Backend:   https://student-pdf-backend.onrender.com
```

## � Usage Guide

### 1. Upload Excel File
- **Supported Formats**: .xlsx, .xls
- **File Types**: Student format, Question bank format
- **Method**: Drag & drop or click to browse

### 2. Analyze File Structure
- **Automatic Detection**: Identifies columns and format
- **Confidence Score**: Shows detection accuracy
- **Sample Data**: Preview of first few rows
- **Recommendations**: Suggests next steps

### 3. Convert Format (if needed)
- **Question Bank → Student**: Converts automatically
- **Customizable**: Number of students, questions per student
- **Shuffle Options**: Random, subset, or none
- **Preview**: Shows conversion results

### 4. Generate PDFs
- **Individual Papers**: One PDF per student
- **Customizable**: Questions per student, shuffle mode
- **Batch Processing**: Handle multiple students
- **Download Options**: Individual or bulk ZIP

## 🛠️ Development

### Adding New Features
```bash
# Backend changes
cd backend_fastapi
# Edit main.py for new endpoints

# Frontend changes
cd frontend
# Edit index.html for UI updates

# Java service changes
cd java_service
# Edit Java files for PDF features
```

### Testing
```bash
# Test backend
curl http://localhost:8000/health

# Test Java service
curl http://localhost:8081/api/status

# Test file upload
curl -X POST -F "file=@sample.xlsx" http://localhost:8000/analyze-excel
```

## 🔍 Troubleshooting

### Common Issues

#### Backend Won't Start
```bash
# Check Python version
python --version  # Should be 3.11+

# Check dependencies
pip install -r backend_fastapi/requirements.txt

# Check port availability
lsof -i :8000
```

#### Java Service Issues
```bash
# Check Java version
java -version  # Should be 17+

# Rebuild JAR
cd java_service
mvn clean package

# Check Maven
mvn --version  # Should be 3.6+
```

#### Frontend Issues
```bash
# Check if port is available
lsof -i :3000

# Start manually
cd frontend
python -m http.server 3000
```

## 📚 API Documentation

### Main Endpoints
```
GET  /health                    # Service health check
GET  /docs                      # API documentation
POST /analyze-excel             # Analyze Excel file
POST /convert-excel             # Convert Excel format
POST /upload-advanced            # Generate PDFs
GET  /java-status               # Java service status
GET  /converter-info            # Converter information
```

### Request/Response Examples
```javascript
// Analyze Excel
const formData = new FormData();
formData.append('file', excelFile);

const response = await fetch('/analyze-excel', {
  method: 'POST',
  body: formData
});

const result = await response.json();
// Returns: { analysis, needs_conversion, ... }
```

## 🌐 Deployment

### Vercel (Frontend)
```bash
# Deploy frontend
cd vercel-deploy
vercel --prod
```

### Render (Backend)
```bash
# Deploy backend
# Connect GitHub repository to Render
# Set root directory: backend_fastapi
# Auto-deploys on push
```

### Docker Production
```bash
# Build images
docker-compose build

# Export for VPS
./export-for-vps.sh

# Deploy anywhere
docker load < images.tar
docker-compose up -d
```

## 🔗 Links

- **📱 Live App**: https://student-pdf-generator.vercel.app
- **📚 API Docs**: https://student-pdf-backend.onrender.com/docs
- **🐙 Docker Hub**: [Add your Docker Hub repo]
- **📋 Issues**: [GitHub Issues](https://github.com/Shreyansh123185655/sdc-pbl/issues)

## 📄 License

MIT License - Free to use, modify, and distribute

---

**🎓 Made with ❤️ for educational institutions**

**🚀 Universal Excel to PDF Generation Made Simple**
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
