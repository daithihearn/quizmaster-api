FROM openjdk:12-alpine

WORKDIR /opt/app

COPY build/libs/quizmaster-api-0.1.0-SNAPSHOT.jar /opt/app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./app.jar", "-XX:+UseContainerSupport"]