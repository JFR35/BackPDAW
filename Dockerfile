FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/* \
    && curl -o /usr/local/bin/wait-for-it https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
    && chmod +x /usr/local/bin/wait-for-it

WORKDIR /app
COPY target/myobservation-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/fhir-profiles /app/resources/fhir-profiles

EXPOSE 8085

ENTRYPOINT ["/usr/local/bin/wait-for-it", "postgres-jwt:5432", "--timeout=60", "--", "java", "-jar", "app.jar"]