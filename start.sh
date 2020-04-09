#!/bin/bash

echo "
    Setting up the environment variables"
export MONGODB_HOSTNAME=mongodb://mongodb:27017
export MONGODB_DATABASE_NAME=quizmaster
export PASSWORD_ENCODER_SEED=password
export JWT_SECRET=password
export SPRING_PROFILES=primary
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=password

echo "
    Starting app...."
./gradlew bootrun