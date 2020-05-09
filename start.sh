#!/bin/bash

echo "
    Setting up the environment variables"
export MONGODB_URI=mongodb://mongodb:27017
export MONGODB_DATABASE_NAME=quizmaster
export PASSWORD_ENCODER_SEED=password
export JWT_SECRET=password
export SPRING_PROFILES=primary,dev,cache-mongo
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=password
export SENDGRID_API_KEY=YOUR API KEY GOES HERE
export PLAYER_LOGIN_URL=http://localhost:8080/\#/autologin
export CORS_WHITELIST=http://localhost:3000
export SCORING_THRESHOLD_LOWER=0f
export SCORING_THRESHOLD_UPPER=2.4f
export CLOUDINARY_URL=YOUR URL GOES HERE

echo "
    Starting app...."
./gradlew bootrun