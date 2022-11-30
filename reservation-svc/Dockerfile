FROM --platform=linux/amd64 eclipse-temurin:17.0.5_8-jre-alpine

RUN apk --no-cache add curl

COPY build/libs/reservation-svc-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
