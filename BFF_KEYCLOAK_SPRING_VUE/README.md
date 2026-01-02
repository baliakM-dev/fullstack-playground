# Backend for Frontend architecture example with Keycloak and Spring Boot and Vue

This repository contains a simple example of a Backend for Frontend architecture with Keycloak and Spring Boot and Vue.


## Tech Stack
- Spring Boot 4.0.0 BFF (`keycloak_app`)
- Vue frontend 3.5.24 (`vue-bff`)
- Docker Compose setup pre Keycloak 26.1.2 a Postgres (`docker/keycloak-compose.yml`)

## How to run
- .env files are included in both projects for easy configuration.
- Clone the repository
- cd to the project folder
- Install dependencies
- Run docker compose for keycloak and postgres
```
docker-compose -f keycloak-compose.yml -p keycloak up -d
```
- Run a backend project with IntelliJ IDEA or gradle
- cd keycloak_app and run command
```
./gradlew bootRun
```
- Run frontend project with intelij ide or vscode or run command
- cd vue-bff and run command
```
npm run dev
```
