FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/weatherProcessor-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4444

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=4444"]

