#!/bin/bash

echo "
    Setting up the environment variables"
export MONGODB_URI=mongodb://mongodb:27017
export MONGODB_DATABASE_NAME=quizmaster
export PASSWORD_ENCODER_SEED=password
export JWT_SECRET=password
export SPRING_PROFILES=primary,prod
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=password
#export SENDGRID_API_KEY=YOUR API KEY GOES HERE
export SENDGRID_API_KEY=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
export PLAYER_LOGIN_URL=http://localhost:8080

echo "
    Starting app...."
./gradlew bootrun