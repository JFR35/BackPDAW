FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/myobservation-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["wait-for-it", "postgres-jwt:5432", "--timeout=60", "--", "java", "-jar", "app.jar"]
