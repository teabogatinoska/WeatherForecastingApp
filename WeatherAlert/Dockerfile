FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/weatheralert-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 6666

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=6666"]

