@echo off
echo ================================
echo Systeme de Gestion de Colis
echo ================================
echo.

echo [1/3] Starting MongoDB with Docker...
docker-compose up -d mongodb

echo.
echo [2/3] Waiting for MongoDB to be ready...
timeout /t 5 /nobreak > nul

echo.
echo [3/3] Starting Spring Boot application...
call mvnw.cmd spring-boot:run

pause
