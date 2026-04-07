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
source .venv/bin/activate

# Install Python dependencies
echo "📦 Installing Python dependencies..."
pip install --upgrade pip
pip install -r backend_fastapi/requirements.txt

# Build Java service
echo "☕ Building Java service..."
cd java_service
mvn clean package -q
cd ..

# Create output directories
echo "📁 Creating output directories..."
mkdir -p java_service/output
mkdir -p output

# Start services
echo "🚀 Starting services..."

# Start Java service in background
echo "Starting Java service on port 8081..."
cd java_service
java -jar target/student-pdf-service-1.0.0.jar --server.port=8081 > ../output/java-service.log 2>&1 &
JAVA_PID=$!
cd ..

# Wait a moment for Java service to start
sleep 3

# Start FastAPI backend in background
echo "Starting FastAPI backend on port 8000..."
cd backend_fastapi
source ../.venv/bin/activate
uvicorn main:app --host 0.0.0.0 --port 8000 > ../output/backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Wait a moment for backend to start
sleep 3

# Start frontend
echo "🌐 Starting frontend on port 3000..."
cd frontend
python3 -m http.server 3000 > ../output/frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

# Wait for services to be ready
echo "⏳ Waiting for services to start..."
sleep 5

# Test services
echo "🏥 Testing service health..."

# Test backend
if curl -s http://localhost:8000/health > /dev/null 2>&1; then
    echo "✅ Backend is healthy"
else
    echo "❌ Backend is not responding"
fi

# Test Java service
if curl -s http://localhost:8081/api/status > /dev/null 2>&1; then
    echo "✅ Java service is healthy"
else
    echo "❌ Java service is not responding"
fi

# Test frontend
if curl -s -o /dev/null -w "%{http_code}" http://localhost:3000 | grep -q "200"; then
    echo "✅ Frontend is running"
else
    echo "❌ Frontend is not responding"
fi

echo ""
echo "🎉 Setup completed!"
echo ""
echo "🌐 Access your application:"
echo "   Frontend:  http://localhost:3000"
echo "   Backend:   http://localhost:8000/docs"
echo "   API:       http://localhost:8000"
echo ""
echo "� Service management:"
echo "   Stop all:  kill $JAVA_PID $BACKEND_PID $FRONTEND_PID"
echo "   View logs:  tail -f output/*.log"
echo ""
echo "📖 For more information, see README.md"
echo ""
echo "✅ Student PDF Generator is ready to use!"
