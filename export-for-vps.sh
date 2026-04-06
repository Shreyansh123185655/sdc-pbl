#!/bin/bash

# Export Docker Images for VPS Deployment
echo "📦 Exporting Docker Images for VPS Deployment"
echo "============================================"

# Build images first
echo "🔨 Building Docker images..."
docker-compose build

# Create export directory
mkdir -p docker-images

# Export images
echo "📤 Exporting images..."
docker save student-pdf-generator_backend:latest > docker-images/backend.tar
docker save student-pdf-generator_java-service:latest > docker-images/java.tar
docker save student-pdf-generator_frontend:latest > docker-images/frontend.tar

# Copy configuration files
echo "📋 Copying configuration files..."
cp docker-compose.yml docker-images/
cp deploy-vps.sh docker-images/

# Create deployment package
echo "📦 Creating deployment package..."
tar -czf student-pdf-vps-deploy.tar.gz docker-images/

echo ""
echo "✅ Export completed!"
echo "📦 Files created:"
echo "  - docker-images/backend.tar"
echo "  - docker-images/java.tar"
echo "  - docker-images/frontend.tar"
echo "  - docker-images/docker-compose.yml"
echo "  - docker-images/deploy-vps.sh"
echo "  - student-pdf-vps-deploy.tar.gz"
echo ""
echo "🚀 Transfer to VPS:"
echo "  scp student-pdf-vps-deploy.tar.gz user@vps-ip:/path/"
echo ""
echo "📦 On VPS:"
echo "  tar -xzf student-pdf-vps-deploy.tar.gz"
echo "  cd docker-images"
echo "  chmod +x deploy-vps.sh"
echo "  ./deploy-vps.sh"
