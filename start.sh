#!/bin/bash

echo "
    Setting up the environment variables"
export MONGODB_URI=mongodb://mongodb:27017
export MONGODB_DATABASE_NAME=quizmaster
export PASSWORD_ENCODER_SEED=password
export JWT_SECRET=password
export SPRING_PROFILES=primary,dev
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=password
export SENDGRID_API_KEY=YOUR API KEY GOES HERE
export PLAYER_LOGIN_URL=http://localhost:8080
export CORS_WHITELIST=http://192.168.1.59:8080

echo "
    Starting app...."
./gradlew bootrun