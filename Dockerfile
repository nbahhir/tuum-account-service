# Use a base image with the required Java runtime
FROM eclipse-temurin:23-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your build directory into the container
COPY build/libs/tuum-account-service-0.0.1.jar app.jar

# Expose the application port (optional)
EXPOSE 8080

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "app.jar"]