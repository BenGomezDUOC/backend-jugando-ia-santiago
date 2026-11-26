@echo off
title Aprendo con IA en Santiago - Backend (Microservicios + Eureka + Gateway)
cls
echo =======================================================================
echo   Iniciando Arquitectura Completa de Microservicios (5 Componentes)
echo =======================================================================
echo.
echo Componentes y Puertos:
echo   1. Eureka Server   : http://localhost:8761  (Service Discovery)
echo   2. API Gateway     : http://localhost:8080  (Puerta de Enlace Global)
echo   3. ms-clientes     : http://localhost:8081  (Identidad y Usuarios)
echo   4. ms-premios      : http://localhost:8082  (Gamificacion y Recompensas)
echo   5. ms-localizaciones: http://localhost:8083 (POIs, Capsulas e Imagenes)
echo.
echo NOTA: La aplicacion Android se conecta directamente al API Gateway (8080).
echo Requiere MySQL en puerto 3306.
echo =======================================================================
echo.

set DIR=%~dp0
cd /d "%DIR%"

echo [1/5] Levantando Eureka Server (8761)...
start "Eureka Server (8761)" cmd /k "cd /d "%DIR%" && java -jar eureka-server\target\eureka-server-1.0.0-SNAPSHOT.jar"

ping 127.0.0.1 -n 6 >nul

echo [2/5] Levantando ms-clientes (8081)...
start "ms-clientes (8081)" cmd /k "cd /d "%DIR%" && java -jar ms-clientes\target\ms-clientes-1.0.0-SNAPSHOT.jar"

ping 127.0.0.1 -n 4 >nul

echo [3/5] Levantando ms-localizaciones (8083)...
start "ms-localizaciones (8083)" cmd /k "cd /d "%DIR%" && java -jar ms-localizaciones\target\ms-localizaciones-1.0.0-SNAPSHOT.jar"

ping 127.0.0.1 -n 4 >nul

echo [4/5] Levantando ms-premios (8082)...
start "ms-premios (8082)" cmd /k "cd /d "%DIR%" && java -jar ms-premios\target\ms-premios-1.0.0-SNAPSHOT.jar"

ping 127.0.0.1 -n 4 >nul

echo [5/5] Levantando API Gateway (8080)...
start "API Gateway (8080)" cmd /k "cd /d "%DIR%" && java -jar api-gateway\target\api-gateway-1.0.0-SNAPSHOT.jar"

echo.
echo =======================================================================
echo Listo. Los 5 servicios se abrieron en ventanas independientes.
echo Para detenerlos, simplemente cierra sus ventanas correspondientes.
echo =======================================================================
echo.
pause
