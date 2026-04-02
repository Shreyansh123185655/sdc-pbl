# Student PDF Generator

**Excel → PDF Generator with Universal Excel Support**

A simple yet powerful application that converts any Excel file into individual student PDF question papers.

---

## 🚀 Quick Start

### **1. Clone & Setup**
```bash
git clone https://github.com/dibakarsinha/ShreyanshMinorProjectDemo.git
cd student-pdf-generator
python -m venv .venv
source .venv/bin/activate  # Windows: .venv\Scripts\activate
```

### **2. Install Dependencies**
```bash
# Python dependencies
cd backend_fastapi
pip install fastapi uvicorn httpx pandas openpyxl python-multipart

# Java service
cd ../java_service
mvn package -DskipTests

# Go back to root
cd ..
```

### **3. Start Services**
```bash
# Terminal 1: Start Java service
cd java_service
java -jar target/student-pdf-service-1.0.0.jar --server.port=8081

# Terminal 2: Start FastAPI
source .venv/bin/activate
cd backend_fastapi
uvicorn main:app --port 8000
```

### **4. Use the Application**
Open browser: **http://localhost:8000/ui**

---

## 📁 Project Structure

```
student-pdf-generator/
├── frontend/                 # Web interface
│   └── index.html         # Single-page app
├── backend_fastapi/          # Python API
│   └── main.py           # FastAPI server
├── java_service/            # Java PDF generator
│   └── target/
│       └── student-pdf-service-1.0.0.jar
├── sample_data/            # Utilities & examples
└── output/                # Generated PDFs
```

---

## 🎯 Features

### **Universal Excel Support**
- **Any Excel format** works automatically
- **Smart structure detection** 
- **Automatic conversion** when needed
- **Question bank support**
- **Tabular data support**

### **Advanced Configuration**
- **Number of students** (1-50)
- **Questions per student** (10-20)
- **Question range selection**
- **Shuffle options** (None/Random/Random Subset)

### **File Management**
- **Open output folder** directly
- **List all PDFs** with download links
- **Bulk download** all PDFs
- **Individual file** downloads

---

## 📋 Excel File Formats

### **Standard Format** (Works directly)
```
| Name | EnrollmentNo | Q1 | Q1_A | Q1_B | Q1_C | Q1_D | Q1_Answer |
|-------|--------------|-----|-------|-------|-------|-------|------------|
| John  | EN2024001    | ... | ...   | ...   | ...   | ...        |
```

### **Question Bank** (Auto-converted)
```
| QuestionText | OptionA | OptionB | OptionC | OptionD | CorrectAnswer |
|-------------|----------|----------|----------|----------|---------------|
| What is 2+2? | 3        | 4        | 5        | 6              | B
```

### **Any Other Format** (Auto-detected & converted)
- Tabular data
- Simple student format
- Custom layouts
- Mixed columns

---

## 🛠️ Usage

### **Web Interface**
1. **Upload Excel file** - Drag & drop or click to browse
2. **Configure settings** - Number of students, questions, shuffle
3. **Generate PDFs** - Click generate button
4. **Download files** - Use file directory buttons

### **Command Line Tools**
```bash
# Convert any Excel file
cd sample_data
./convert_any_excel.sh your_file.xlsx

# Analyze only
./convert_any_excel.sh -a your_file.xlsx
```

---

## 🔧 Configuration

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
