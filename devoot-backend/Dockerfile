# 1️⃣ Base Image - JDK 21.0.5 (Temurin OpenJDK)
FROM openjdk:21-jdk AS base

# 2️⃣ Set Working Directory
WORKDIR /app

# 3️⃣ Copy JAR File (build/libs/*.jar)
COPY build/libs/*-SNAPSHOT.jar app.jar

# 4️⃣ Expose Port (예: 8080)
EXPOSE 8080

# 5️⃣ Run the Application
ENTRYPOINT ["java", "-jar", "app.jar"]
