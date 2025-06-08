# Start with a base image containing Java runtime
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (will cache unless pom.xml changes)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port (adjust if your Spring Boot app runs on a different port)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/timetable-backend-0.0.1-SNAPSHOT.jar"]
