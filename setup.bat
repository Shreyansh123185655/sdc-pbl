@echo off
REM Student PDF Generator - Quick Setup Script for Windows
REM This script automates the complete setup process

echo 🚀 Student PDF Generator - Quick Setup
echo =====================================
echo.

REM Check if we're in the right directory
if not exist "README.md" (
    echo ❌ Please run this script from the student-pdf-generator root directory
    pause
    exit /b 1
)

if not exist "backend_fastapi" (
    echo ❌ Please run this script from the student-pdf-generator root directory
    pause
    exit /b 1
)

if not exist "java_service" (
    echo ❌ Please run this script from the student-pdf-generator root directory
    pause
    exit /b 1
)

echo ℹ️  Step 1: Verifying prerequisites...

REM Check Python
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Python not found. Please install Python 3.9+
    pause
    exit /b 1
)

for /f "tokens=2" %%i in ('python --version') do set PYTHON_VERSION=%%i
echo ✅ Python found: %PYTHON_VERSION%

REM Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java not found. Please install Java 17+
    pause
    exit /b 1
)

for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| find "version"') do set JAVA_VERSION=%%i
echo ✅ Java found: %JAVA_VERSION%

REM Check Maven
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven not found. Please install Maven 3.8+
    pause
    exit /b 1
)

for /f "tokens=3" %%i in ('mvn --version ^| find "Apache Maven"') do set MAVEN_VERSION=%%i
echo ✅ Maven found: %MAVEN_VERSION%

echo.
echo ℹ️  Step 2: Setting up Python virtual environment...

REM Create virtual environment if it doesn't exist
if not exist ".venv" (
    echo ℹ️  Creating virtual environment...
    python -m venv .venv
    echo ✅ Virtual environment created
) else (
    echo ✅ Virtual environment already exists
)

REM Activate virtual environment
echo ℹ️  Activating virtual environment...
call .venv\Scripts\activate.bat
echo ✅ Virtual environment activated

echo.
echo ℹ️  Step 3: Installing Python dependencies...

REM Navigate to backend and install dependencies
cd backend_fastapi

echo ℹ️  Installing FastAPI dependencies...
pip install fastapi==0.111.0
pip install uvicorn[standard]==0.24.0
pip install httpx==0.25.2
pip install pandas==2.2.1
pip install openpyxl==3.1.5
pip install python-multipart==0.0.9
pip install rich==13.7.1
pip install rich-toolkit==0.14.8

echo ✅ Python dependencies installed

cd ..

echo.
echo ℹ️  Step 4: Building Java service...

cd java_service

echo ℹ️  Compiling Java service...
mvn clean compile -DskipTests

echo ℹ️  Packaging Java service...
mvn package -DskipTests

if exist "target\student-pdf-service-1.0.0.jar" (
    echo ✅ Java service built successfully
) else (
    echo ❌ Java service build failed
    pause
    exit /b 1
)

cd ..

echo.
echo ℹ️  Step 5: Generating sample data...

cd sample_data

REM Install openpyxl for sample data generation
pip install openpyxl==3.1.5

echo ℹ️  Generating sample Excel file...
python generate_sample.py

if exist "students_sample.xlsx" (
    echo ✅ Sample data generated
) else (
    echo ⚠️  Sample data generation failed (optional)
)

cd ..

echo.
echo ✅ 🎉 Setup completed successfully!
echo.
echo 📋 Next Steps:
echo 1. Open Command Prompt 1 and run:
echo    cd java_service ^&^& java -jar target\student-pdf-service-1.0.0.jar --server.port=8081
echo.
echo 2. Open Command Prompt 2 and run:
echo    .venv\Scripts\activate.bat ^&^& cd backend_fastapi ^&^& uvicorn main:app --port 8000
echo.
echo 3. Open your browser and navigate to:
echo    http://localhost:8000/ui
echo.
echo 📁 Generated files will be saved in: java_service\output\
echo.
echo ℹ️  You can now start using the Student PDF Generator!
echo.
pause
