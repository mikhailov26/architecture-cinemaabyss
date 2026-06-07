# Build stage
FROM openjdk:21-jdk-slim as build

# Set working directory
WORKDIR /app

# Copy build.gradle and settings.gradle files
COPY . .

# Download dependencies
RUN ./mvnw clean install -DskipTests

# Copy the source code
COPY src /app/src

# Build the application
RUN ./mvnw clean install -DskipTests

# Runtime stage
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the binary from the build stage
COPY --from=build /app/target/app.jar app.jar

# Expose the port the app runs on
EXPOSE 8082

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]