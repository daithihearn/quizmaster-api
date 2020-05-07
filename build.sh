#!/bin/sh

echo "
Building quizmaster-api"

./gradlew build install

if [ -x "$(command -v docker)" ]; then
    
    docker build -t localhost:5000/quizmaster-api:latest .
    docker push localhost:5000/quizmaster-api:latest
    
else
    echo "Docker not installed"
fi