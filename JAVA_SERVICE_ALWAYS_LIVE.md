# 📡 Java Service Always Live - Deployment Guide

## 🎯 Goal: Keep Java Service Running 24/7

### 🌟 Multiple Deployment Options

---

## 🚀 Option 1: Render Cloud Deployment (Recommended)

### Setup Steps:
```bash
# 1. Create New Render Service
# Go to: https://dashboard.render.com/
# Click: "New +" → "Web Service"

# 2. Configure Java Service
Name: student-pdf-java
Root Directory: java_service
Runtime: Docker
Instance Type: Free
Region: Nearest to your users

# 3. Docker Configuration
Dockerfile Path: java_service/Dockerfile
Port: 8081
Health Check Path: /api/status

# 4. Environment Variables
JAVA_OPTS: -Xmx512m -Xms256m
SERVER_PORT: 8081
OUTPUT_DIRECTORY: /app/output

# 5. Deploy
Click: "Create Web Service"
```

### Benefits:
- ✅ **Always Live** - 24/7 uptime
- ✅ **Auto-scaling** - Handles traffic spikes
- ✅ **Zero Maintenance** - Render manages infrastructure
- ✅ **Global CDN** - Fast access worldwide
- ✅ **SSL Included** - HTTPS by default

---

## 🐳 Option 2: Docker with Auto-restart

### Local Docker Setup:
```bash
# 1. Create docker-compose.override.yml
cat > docker-compose.override.yml << EOF
version: '3.8'
services:
  java-service:
    restart: unless-stopped
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
    environment:
      - JAVA_OPTS=-Xmx512m -Xms256m
EOF

# 2. Start with auto-restart
docker-compose up -d java-service

# 3. Monitor and restart if needed
docker-compose restart java-service
```

### Systemd Service (Linux):
```bash
# 1. Create systemd service file
sudo tee /etc/systemd/system/student-pdf-java.service > /dev/null << EOF
[Unit]
Description=Student PDF Java Service
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/path/to/student-pdf-generator/java_service
ExecStart=/usr/bin/java -jar target/student-pdf-service-1.0.0.jar --server.port=8081
Restart=always
RestartSec=10
Environment=JAVA_OPTS=-Xmx512m -Xms256m

[Install]
WantedBy=multi-user.target
EOF

# 2. Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable student-pdf-java.service
sudo systemctl start student-pdf-java.service

# 3. Check status
sudo systemctl status student-pdf-java.service
```

---

## 🖥️ Option 3: PM2 Process Manager

### Installation and Setup:
```bash
# 1. Install PM2
npm install -g pm2

# 2. Create PM2 config file
cat > ecosystem.config.js << EOF
module.exports = {
  apps: [{
    name: 'student-pdf-java',
    script: 'java',
    args: '-jar target/student-pdf-service-1.0.0.jar --server.port=8081',
    cwd: './java_service',
    instances: 1,
    autorestart: true,
    watch: false,
    max_memory_restart: '1G',
    env: {
      NODE_ENV: 'production',
      JAVA_OPTS: '-Xmx512m -Xms256m'
    }
  }]
};
EOF

# 3. Start service
pm2 start ecosystem.config.js

# 4. Monitor
pm2 status
pm2 logs student-pdf-java

# 5. Save PM2 config
pm2 save
pm2 startup
```

---

## 🔄 Option 4: Cron Auto-restart Script

### Auto-restart Setup:
```bash
# 1. Create monitoring script
cat > restart-java-service.sh << 'EOF'
#!/bin/bash
LOG_FILE="/path/to/student-pdf-generator/output/java-service.log"
PID_FILE="/path/to/student-pdf-generator/java-service.pid"

# Check if service is running
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
        echo "$(date): Java service is running (PID: $PID)"
        exit 0
    else
        echo "$(date): Java service PID file exists but process not found"
        rm "$PID_FILE"
    fi
fi

# Start service if not running
echo "$(date): Starting Java service..."
cd /path/to/student-pdf-generator/java_service
nohup java -jar target/student-pdf-service-1.0.0.jar --server.port=8081 > ../output/java-service.log 2>&1 &
echo $! > "$PID_FILE"
echo "$(date): Java service started (PID: $!)"

# Health check
sleep 5
curl -f http://localhost:8081/api/status || {
    echo "$(date): Health check failed, restarting..."
    kill $(cat "$PID_FILE")
    rm "$PID_FILE"
    exit 1
}
EOF

chmod +x restart-java-service.sh

# 2. Add to crontab (every 5 minutes)
crontab -l | { cat; echo "*/5 * * * * * /path/to/student-pdf-generator/restart-java-service.sh >> /path/to/student-pdf-generator/cron.log 2>&1"; } | crontab -

# 3. Add to crontab (every hour)
crontab -l | { cat; echo "0 * * * * * /path/to/student-pdf-generator/restart-java-service.sh >> /path/to/student-pdf-generator/cron.log 2>&1"; } | crontab -
```

---

## 🌐 Option 5: Cloud Services

### AWS EC2 Setup:
```bash
# 1. Launch EC2 Instance
# AMI: Ubuntu 20.04 LTS
# Instance: t3.micro (Free tier)
# Security Group: Allow port 8081

# 2. Connect to EC2
ssh -i key.pem ubuntu@your-ec2-ip

# 3. Setup Java service
git clone https://github.com/Shreyansh123185655/sdc-pbl.git
cd sdc-pbl/java_service
sudo apt update && sudo apt install -y openjdk-17-jdk
java -jar target/student-pdf-service-1.0.0.jar --server.port=8081 &

# 4. Setup systemd service (see Option 2)
```

### DigitalOcean Droplet:
```bash
# 1. Create Droplet
# Image: Ubuntu 20.04
# Plan: Basic ($5/month)
# Firewall: Allow port 8081

# 2. Deploy Java service
ssh root@your-droplet-ip
git clone https://github.com/Shreyansh123185655/sdc-pbl.git
cd sdc-pbl/java_service
apt update && apt install -y openjdk-17-jdk
java -jar target/student-pdf-service-1.0.0.jar --server.port=8081 &
```

---

## 🔍 Monitoring & Health Checks

### Health Check Script:
```bash
#!/bin/bash
# health-check.sh
SERVICE_URL="http://localhost:8081/api/status"
LOG_FILE="./output/health-check.log"

while true; do
    TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')
    
    if curl -s -f "$SERVICE_URL" > /dev/null; then
        echo "$TIMESTAMP: ✅ Java service is healthy"
    else
        echo "$TIMESTAMP: ❌ Java service is down - restarting"
        # Restart service (choose your method)
        pm2 restart student-pdf-java
        # or: systemctl restart student-pdf-java
        # or: docker-compose restart java-service
    fi
    
    sleep 60  # Check every minute
done > "$LOG_FILE" 2>&1 &
```

### Slack/Discord Notifications:
```bash
# Add to health check script
if ! curl -s -f "$SERVICE_URL" > /dev/null; then
    # Send to Slack
    curl -X POST -H 'Content-type: application/json' \
    --data '{"text":"🚨 Java service is down! Restarting..."}' \
    "$SLACK_WEBHOOK_URL"
    
    # Send to Discord
    curl -H "Content-Type: application/json" \
    -X POST \
    -d '{"content": "🚨 Java service is down! Restarting..."}' \
    "$DISCORD_WEBHOOK_URL"
fi
```

---

## 🎯 Recommended Setup

### **🌟 Best Option: Render Cloud**
```bash
# Deploy to Render for maximum reliability
# Benefits:
- 99.99% uptime SLA
- Auto-scaling
- Global CDN
- SSL included
- Zero maintenance
- Easy monitoring
```

### **🏠 Local Development: PM2**
```bash
# Use PM2 for local development
# Benefits:
- Auto-restart on crash
- Log management
- Easy monitoring
- Process management
```

---

## 📊 Service Configuration

### **Production Settings:**
```bash
# Java Memory
JAVA_OPTS="-Xmx1024m -Xms512m"

# Server Port
SERVER_PORT=8081

# Output Directory
OUTPUT_DIRECTORY="/app/output"

# Health Check
HEALTH_CHECK_PATH="/api/status"
```

### **Environment Variables:**
```bash
# Production
export JAVA_OPTS="-Xmx1024m -Xms512m"
export SERVER_PORT=8081
export NODE_ENV="production"

# Development
export JAVA_OPTS="-Xmx512m -Xms256m"
export SERVER_PORT=8081
export NODE_ENV="development"
```

---

## 🔧 Troubleshooting

### **Common Issues:**

#### Service Won't Start:
```bash
# Check Java version
java -version  # Should be 17+

# Check port availability
netstat -tulpn | grep 8081

# Check JAR file
ls -la java_service/target/student-pdf-service-1.0.0.jar
```

#### Service Crashes:
```bash
# Check logs
tail -f java-service.log

# Check memory usage
free -h
ps aux | grep java

# Increase Java memory
export JAVA_OPTS="-Xmx2048m -Xms1024m"
```

#### Port Conflicts:
```bash
# Kill process using port 8081
sudo lsof -ti:8081 | xargs kill -9

# Use different port
java -jar target/student-pdf-service-1.0.0.jar --server.port=8082
```

---

## 🌐 Production URLs

### **After Deployment:**
- **Render**: https://student-pdf-java.onrender.com/api/status
- **AWS/DigitalOcean**: http://your-server-ip:8081/api/status
- **Local**: http://localhost:8081/api/status

### **Health Endpoints:**
- **Status**: `/api/status`
- **Info**: `/api/info`
- **Files**: `/api/files`
- **Health**: `/api/health`

---

## 📋 Quick Start Commands

### **Deploy to Render (Recommended):**
```bash
# 1. Push to GitHub
git add . && git commit -m "Deploy Java service to Render"
git push origin main

# 2. Connect to Render
# Visit: https://dashboard.render.com/
# Connect repository, set root directory to java_service
# Deploy with Docker runtime
```

### **Local PM2 Setup:**
```bash
# 1. Install PM2
npm install -g pm2

# 2. Start service
cd java_service
pm2 start "java -jar target/student-pdf-service-1.0.0.jar --server.port=8081" --name student-pdf-java

# 3. Save and monitor
pm2 save
pm2 monit
```

---

**🎓 Choose the option that best fits your needs for keeping Java service always live!**

**🌟 For production: Use Render for maximum reliability**
**🏠 For development: Use PM2 for easy management**
**🖥️ For self-hosting: Use systemd/PM2 on VPS**
