#!/bin/bash

# Student PDF Generator - Automated Setup Script
echo "🎓 Student PDF Generator - Automated Setup"
echo "======================================"

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

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    print_error "Python 3 is not installed. Please install Python 3.11+ first."
    echo "Visit: https://www.python.org/downloads/"
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    print_error "Java is not installed. Please install Java 17+ first."
    echo "Visit: https://www.java.com/download/"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven 3.6+ first."
    echo "Visit: https://maven.apache.org/download.cgi"
    exit 1
fi

print_status "All prerequisites found!"

# Create virtual environment
echo ""
print_info "Creating Python virtual environment..."
python3 -m venv .venv
source .venv/bin/activate

# Install Python dependencies
print_info "Installing Python dependencies..."
pip install --upgrade pip
pip install -r backend_fastapi/requirements.txt

# Build Java service
print_info "Building Java service..."
cd java_service
mvn clean package -q
cd ..

# Create output directories
print_info "Creating output directories..."
mkdir -p java_service/output
mkdir -p output

# Start services
echo ""
print_info "Starting services..."

# Start Java service in background with PM2 (if available)
if command -v pm2 &> /dev/null; then
    print_status "Using PM2 for Java service (auto-restart enabled)"
    cd java_service
    pm2 start "java -jar target/student-pdf-service-1.0.0.jar --server.port=8081" --name student-pdf-java
    pm2 save
    cd ..
    JAVA_PID="PM2 Managed"
else
    print_warning "PM2 not found, using basic background process"
    print_info "Install PM2 for auto-restart: npm install -g pm2"
    print_info "Starting Java service on port 8081..."
    cd java_service
    java -jar target/student-pdf-service-1.0.0.jar --server.port=8081 > ../output/java-service.log 2>&1 &
    JAVA_PID=$!
    cd ..
fi

# Wait a moment for Java service to start
sleep 3

# Start FastAPI backend in background
print_info "Starting FastAPI backend on port 8000..."
cd backend_fastapi
source ../.venv/bin/activate
uvicorn main:app --host 0.0.0.0 --port 8000 > ../output/backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Wait a moment for backend to start
sleep 3

# Start frontend
print_info "Starting frontend on port 3000..."
cd frontend
python3 -m http.server 3000 > ../output/frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

# Wait for services to be ready
print_info "Waiting for services to start..."
sleep 5

# Test services
echo ""
print_info "Testing service health..."

# Test backend
if curl -s http://localhost:8000/health > /dev/null 2>&1; then
    print_status "Backend is healthy"
else
    print_error "Backend is not responding"
fi

# Test Java service
if curl -s http://localhost:8081/api/status > /dev/null 2>&1; then
    print_status "Java service is healthy"
else
    print_error "Java service is not responding"
fi

# Test frontend
if curl -s -o /dev/null -w "%{http_code}" http://localhost:3000 | grep -q "200"; then
    print_status "Frontend is running"
else
    print_error "Frontend is not responding"
fi

echo ""
print_status "Setup completed!"
echo ""
echo -e "${GREEN}🌐 Access your application:${NC}"
echo -e "   Frontend:  ${BLUE}http://localhost:3000${NC}"
echo -e "   Backend:   ${BLUE}http://localhost:8000/docs${NC}"
echo -e "   API:       ${BLUE}http://localhost:8000${NC}"
echo ""
echo -e "${GREEN}🔧 Service management:${NC}"
if [ "$JAVA_PID" = "PM2 Managed" ]; then
    echo -e "   Java service: ${BLUE}pm2 status student-pdf-java${NC}"
    echo -e "   Restart Java:  ${BLUE}pm2 restart student-pdf-java${NC}"
    echo -e "   View logs:    ${BLUE}pm2 logs student-pdf-java${NC}"
else
    echo -e "   Stop all:  ${BLUE}kill $JAVA_PID $BACKEND_PID $FRONTEND_PID${NC}"
    echo -e "   View logs:  ${BLUE}tail -f output/*.log${NC}"
fi
echo ""
echo -e "${GREEN}� Java Service Always Live Options:${NC}"
echo -e "   📖 Guide: ${BLUE}JAVA_SERVICE_ALWAYS_LIVE.md${NC}"
echo -e "   🌐 Render:  ${BLUE}https://dashboard.render.com${NC}"
echo -e "   🖥️  PM2:     ${BLUE}pm2 start student-pdf-java${NC}"
echo -e "   🐳 Docker:  ${BLUE}docker-compose up java-service${NC}"
echo ""
print_status "Student PDF Generator is ready to use!"
