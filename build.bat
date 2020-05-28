@ECHO off

ECHO "1. Building quizmaster-api"

gradlew.bat build install

ECHO "2. Building image quizmaster-api"
CALL docker build -t localhost:5000/quizmaster-api:latest .
CALL docker push localhost:5000/quizmaster-api:latest