FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/weatherPresenter-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5555

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=5555"]

