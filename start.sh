#!/bin/bash

echo "
    Setting up the environment variables"
export MONGODB_URI=mongodb://mongodb:27017
export MONGODB_DATABASE_NAME=quizmaster
export SPRING_PROFILES=primary,dev
export SENDGRID_API_KEY=YOUR API KEY GOES HERE
export PLAYER_LOGIN_URL=http://localhost:8080/\#/autologin
export CORS_WHITELIST=http://localhost:3000
export SCORING_THRESHOLD_LOWER=0f
export SCORING_THRESHOLD_UPPER=2.4f
export CLOUDINARY_URL=YOUR URL GOES HERE
export AUTH0_AUDIENCE=http://localhost:8080
export AUTH0_CLIENT_ID=Issuer URI Goes here

echo "
    Starting app...."
./gradlew bootrun