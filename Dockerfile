# Start with a base image containing Java runtime
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# ✅ Make mvnw executable (after it's in the right place)
RUN chmod +x mvnw

# Download dependencies (will cache unless pom.xml changes)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/timetable-backend-0.0.1-SNAPSHOT.jar"]
