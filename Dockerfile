# Stage 1: Build the application with Java 21
FROM openjdk:21-jdk-slim AS builder

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Build the project, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image with Java 21
FROM openjdk:21-jdk-slim
WORKDIR /application

# Copy the JAR file from the build stage to the runtime stage
COPY --from=builder /app/target/spaceships-api-0.0.1-SNAPSHOT.jar /application/spaceships-api.jar

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "/application/spaceships-api.jar"]

# Expose the application port
EXPOSE 8080