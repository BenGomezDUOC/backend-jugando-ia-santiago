@echo off
chcp 65001 >nul
title Aprendo con IA en Santiago - Iniciar Backend
echo =======================================================================
echo   Iniciando los 3 Microservicios de Aprendo con IA en Santiago
echo =======================================================================
echo.
echo Puertos asignados:
echo   [1] ms-clientes        -> http://localhost:8081
echo   [2] ms-localizaciones  -> http://localhost:8083
echo   [3] ms-premios         -> http://localhost:8082
echo.
echo NOTA: Tu otro proyecto en el puerto 8080 NO se verá afectado.
echo Asegúrate de tener MySQL iniciado en el puerto 3306.
echo =======================================================================
echo.

set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%"

echo [1/3] Levantando ms-clientes (Puerto 8081)...
start "Backend - ms-clientes (8081)" cmd /k "cd /d "%SCRIPT_DIR%" && mvnw.cmd spring-boot:run -pl ms-clientes"

timeout /t 3 >nul

echo [2/3] Levantando ms-localizaciones (Puerto 8083)...
start "Backend - ms-localizaciones (8083)" cmd /k "cd /d "%SCRIPT_DIR%" && mvnw.cmd spring-boot:run -pl ms-localizaciones"

timeout /t 4 >nul

echo [3/3] Levantando ms-premios (Puerto 8082)...
start "Backend - ms-premios (8082)" cmd /k "cd /d "%SCRIPT_DIR%" && mvnw.cmd spring-boot:run -pl ms-premios"

echo.
echo =======================================================================
echo  Listo. Los 3 microservicios se están ejecutando en ventanas separadas.
echo  Para detener cualquiera de ellos, simplemente cierra su ventana CMD.
echo =======================================================================
echo.
pause
