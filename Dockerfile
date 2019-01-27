FROM openjdk:8-jdk-alpine

ARG JAR_FILE

COPY ${JAR_FILE} myapp.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/myapp.jar"]
