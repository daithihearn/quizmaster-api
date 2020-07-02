#!/bin/sh

echo "
1. Building jar quizmaster-api"

./gradlew build install

if [ -x "$(command -v docker)" ]; then
    echo "
	2. Building image quizmaster-api"
    docker build -t localhost:5000/quizmaster-api:latest .
    docker push localhost:5000/quizmaster-api:latest
    
else
    echo "Docker not installed"
fi