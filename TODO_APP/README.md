docker compose -f db-compose.yml --env-file .env up -d
docker compose -f pgadmin-compose.yml up -d
http://localhost:8080/swagger-ui