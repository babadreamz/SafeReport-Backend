
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 2000
ENTRYPOINT ["java", "-Xmx350m", "-Xss512k", "-jar", "app.jar"]