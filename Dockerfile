# Use Debian-based JDK image instead of Alpine to avoid native Netty crash
FROM eclipse-temurin:21-jdk

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Preload dependencies
RUN ./mvnw dependency:go-offline

# Copy the full project
COPY . .

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

# Expose the app port
EXPOSE 8080

# Run the app with Netty native disabled (still optional now)
CMD ["java", "-Dio.netty.noNative=true", "-jar", "target/timetable-backend-0.0.1-SNAPSHOT.jar"]
