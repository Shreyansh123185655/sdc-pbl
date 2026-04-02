#!/bin/bash

# Student PDF Generator - Quick Setup Script
# This script automates the complete setup process

set -e  # Exit on any error

echo "🚀 Student PDF Generator - Quick Setup"
echo "====================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if we're in the right directory
if [ ! -f "README.md" ] || [ ! -d "backend_fastapi" ] || [ ! -d "java_service" ]; then
    print_error "Please run this script from the student-pdf-generator root directory"
    exit 1
fi

print_info "Step 1: Verifying prerequisites..."

# Check Python version
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version | cut -d' ' -f2)
    print_status "Python found: $PYTHON_VERSION"
elif command -v python &> /dev/null; then
    PYTHON_VERSION=$(python --version | cut -d' ' -f2)
    print_status "Python found: $PYTHON_VERSION"
else
    print_error "Python not found. Please install Python 3.9+"
    exit 1
fi

# Check Java version
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2)
    print_status "Java found: $JAVA_VERSION"
else
    print_error "Java not found. Please install Java 17+"
    exit 1
fi

# Check Maven
if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn --version | head -n1 | cut -d' ' -f3)
    print_status "Maven found: $MAVEN_VERSION"
else
    print_error "Maven not found. Please install Maven 3.8+"
    exit 1
fi

print_info "Step 2: Setting up Python virtual environment..."

# Create virtual environment if it doesn't exist
if [ ! -d ".venv" ]; then
    print_info "Creating virtual environment..."
    python3 -m venv .venv || python -m venv .venv
    print_status "Virtual environment created"
else
    print_status "Virtual environment already exists"
fi

# Activate virtual environment
print_info "Activating virtual environment..."
source .venv/bin/activate
print_status "Virtual environment activated"

print_info "Step 3: Installing Python dependencies..."

# Navigate to backend and install dependencies
cd backend_fastapi

print_info "Installing FastAPI dependencies..."
pip install fastapi==0.111.0
pip install uvicorn[standard]==0.24.0
pip install httpx==0.25.2
pip install pandas==2.2.1
pip install openpyxl==3.1.5
pip install python-multipart==0.0.9
pip install rich==13.7.1
pip install rich-toolkit==0.14.8

print_status "Python dependencies installed"

cd ..

print_info "Step 4: Building Java service..."

cd java_service

print_info "Compiling Java service..."
mvn clean compile -DskipTests

print_info "Packaging Java service..."
mvn package -DskipTests

if [ -f "target/student-pdf-service-1.0.0.jar" ]; then
    print_status "Java service built successfully"
else
    print_error "Java service build failed"
    exit 1
fi

cd ..

print_info "Step 5: Generating sample data..."

cd sample_data

# Install openpyxl for sample data generation
pip install openpyxl==3.1.5

print_info "Generating sample Excel file..."
python generate_sample.py

if [ -f "students_sample.xlsx" ]; then
    print_status "Sample data generated"
else
    print_warning "Sample data generation failed (optional)"
fi

cd ..

echo ""
print_status "🎉 Setup completed successfully!"
echo ""
echo "📋 Next Steps:"
echo "1. Open Terminal 1 and run:"
echo "   cd java_service && java -jar target/student-pdf-service-1.0.0.jar --server.port=8081"
echo ""
echo "2. Open Terminal 2 and run:"
echo "   source .venv/bin/activate && cd backend_fastapi && uvicorn main:app --port 8000"
echo ""
echo "3. Open your browser and navigate to:"
echo "   http://localhost:8000/ui"
echo ""
echo "📁 Generated files will be saved in: java_service/output/"
echo ""
print_info "You can now start using the Student PDF Generator!"
