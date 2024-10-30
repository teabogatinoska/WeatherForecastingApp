FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/authentication-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 2222

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=2222"]

