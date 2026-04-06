#!/bin/bash

# Student PDF Generator - VPS Deployment Script
echo "🚀 Student PDF Generator - VPS Deployment"
echo "=========================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create necessary directories
echo "📁 Creating directories..."
mkdir -p java_service/output
mkdir -p output

# Build and start services
echo "🔨 Building Docker images..."
docker-compose build

echo "🚀 Starting services..."
docker-compose up -d

# Wait for services to start
echo "⏳ Waiting for services to start..."
sleep 10

# Check service health
echo "🏥 Checking service health..."
curl -s http://localhost:8000/health > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Backend is healthy"
else
    echo "❌ Backend is not responding"
fi

curl -s http://localhost:8081/api/status > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Java service is healthy"
else
    echo "❌ Java service is not responding"
fi

echo ""
echo "🎉 Deployment completed!"
echo "🌐 Access your application at: http://localhost:8000"
echo "📊 API docs at: http://localhost:8000/docs"
echo "📋 Java service at: http://localhost:8081/api/status"
echo ""
echo "🔧 Management commands:"
echo "  Stop: docker-compose down"
echo "  Restart: docker-compose restart"
echo "  Logs: docker-compose logs -f"
echo "  Status: docker-compose ps"
