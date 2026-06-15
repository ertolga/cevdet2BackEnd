# 1. Aşama: Kodları Derleme (Build)
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Aşama: Uygulamayı Çalıştırma (Run)
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/customer-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
