FROM openjdk:17-jdk-slim

# Install curl to download wait-for-it and make it executable
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/* \
    && curl -o /usr/local/bin/wait-for-it https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x /usr/local/bin/wait-for-it

WORKDIR /app
COPY target/myobservation-0.0.1-SNAPSHOT.jar app.jar

# Use the same port as in application.properties and docker-compose
EXPOSE 8085

ENTRYPOINT ["/usr/local/bin/wait-for-it", "postgres-jwt:5432", "--timeout=60", "--", "java", "-jar", "app.jar"]