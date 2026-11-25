@echo off
chcp 65001 >nul
title Aprendo con IA en Santiago - Iniciar Backend
echo =======================================================================
echo   Iniciando los 3 Microservicios de Aprendo con IA en Santiago
echo =======================================================================
echo.
echo Puertos asignados:
echo   [1] ms-clientes        -^> http://localhost:8081
echo   [2] ms-localizaciones  -^> http://localhost:8083
echo   [3] ms-premios         -^> http://localhost:8082
echo.
echo =======================================================================
echo NOTA: Tu otro proyecto en el puerto 8080 NO se verá afectado.
echo Recuerda tener MySQL (puerto 3306) INICIADO (ej. XAMPP / Laragon).
echo =======================================================================
echo.

set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%"

rem Si los JARs no existen, compilarlos una sola vez
if not exist "ms-localizaciones\target\ms-localizaciones-1.0.0-SNAPSHOT.jar" (
    echo Empaquetando los JARs ejecutables (solo la primera vez)...
    call mvnw.cmd package -DskipTests
)

echo [1/3] Levantando ms-clientes en puerto 8081...
start "Backend - ms-clientes (8081)" cmd /k "cd /d "%SCRIPT_DIR%" && java -jar ms-clientes\target\ms-clientes-1.0.0-SNAPSHOT.jar"

timeout /t 3 >nul

echo [2/3] Levantando ms-localizaciones en puerto 8083...
start "Backend - ms-localizaciones (8083)" cmd /k "cd /d "%SCRIPT_DIR%" && java -jar ms-localizaciones\target\ms-localizaciones-1.0.0-SNAPSHOT.jar"

timeout /t 4 >nul

echo [3/3] Levantando ms-premios en puerto 8082...
start "Backend - ms-premios (8082)" cmd /k "cd /d "%SCRIPT_DIR%" && java -jar ms-premios\target\ms-premios-1.0.0-SNAPSHOT.jar"

echo.
echo =======================================================================
echo  Listo. Los 3 microservicios se están ejecutando en ventanas separadas.
echo  Para detener cualquiera de ellos, simplemente cierra su ventana CMD.
echo =======================================================================
echo.
pause
