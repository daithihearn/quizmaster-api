#!/bin/bash

echo "
    Setting up the environment variables"
./setupDevEnvironmentVariables.sh

echo "
    Starting app...."
./gradlew bootrun