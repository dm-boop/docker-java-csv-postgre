# Use OpenJDK as base image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy and build the application
COPY . .
RUN ./mvnw package -DskipTests
CMD ["java", "-jar", "target/converter.jar"]