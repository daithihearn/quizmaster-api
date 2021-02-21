FROM openjdk:13-alpine

WORKDIR /opt/app

COPY build/libs/quizmaster-api-0.2.0-SNAPSHOT.jar /opt/app/app.jar

ENTRYPOINT ["java", "-Djdk.tls.client.protocols=TLSv1.2", "-jar", "./app.jar", "-XX:+UseContainerSupport"]
