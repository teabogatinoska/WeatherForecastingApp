FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/weatherfetcher-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 3333

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=3333"]

