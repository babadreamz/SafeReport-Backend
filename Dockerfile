FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 2000

ENTRYPOINT ["java","-Xmx350m","-Xss512k", "-jar","app.jar"]