# Quizmaster API
The API layer for the [Quizmaster application](https://github.com/daithihearn/quizmaster)

# Reqirements
To run this application you need a MongoDB instance to point to. The MongoDB URI can be configured in the `start.sh` script.
If you use docker-compose then you don't need a MongoDB instance as a mongo container will be used.

# Technical Stack
- Kotlin
- Spring-Boot
- Swagger
- MongoDB
- Gradle

# Building
To build this app into a spring boot jar simply run the `./build.sh` script.
The jar will be installed in the local maven repo and can be run with `java -jar ~/.m2/repository/ie/daithi/quizmaster/quizmaster-api/0.1.0-SNAPSHOT/quizmaster-api-0.1.0-SNAPSHOT.jar`

# Docker Compose
To run using Docker see the [top level project](https://github.com/daithihearn/quizmaster)'s README file 

# Frontend
The [frontend](https://github.com/daithihearn/quizmaster-frontend) is packages as a webjar in the `/libs` folder of this project. Running the `./build.sh` script in the [frontend](https://github.com/daithihearn/quizmaster-frontend) will update this webjar. The [frontend](https://github.com/daithihearn/quizmaster-frontend) can also be run separately. See the README in [frontend](https://github.com/daithihearn/quizmaster-frontend) for more details.

# Running (Develop mode)
To run this app locally in development mode simply run `./start.sh`. There is no need to run the `./build.sh` script first.
