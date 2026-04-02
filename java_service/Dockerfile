FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/student-pdf-service-1.0.0.jar app.jar

# Copy application properties
COPY application.properties .

# Expose port
EXPOSE 8081

# Run the application
CMD ["java", "-jar", "app.jar"]
