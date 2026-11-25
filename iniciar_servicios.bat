@echo off
title Aprendo con IA en Santiago - Backend
cls
echo =======================================================================
echo   Iniciando los 3 Microservicios de Aprendo con IA en Santiago
echo =======================================================================
echo.
echo Puertos:
echo   ms-clientes       : http://localhost:8081
echo   ms-localizaciones : http://localhost:8083
echo   ms-premios        : http://localhost:8082
echo.
echo NOTA: Tu proyecto en puerto 8080 no se vera afectado.
echo Requiere MySQL en puerto 3306.
echo =======================================================================
echo.

set DIR=%~dp0
cd /d "%DIR%"

rem Puente ADB para celulares fisicos (si hay uno conectado)
if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    echo Configurando puente ADB para celular fisico...
    "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" reverse tcp:8081 tcp:8081 >nul 2>nul
    "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" reverse tcp:8082 tcp:8082 >nul 2>nul
    "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" reverse tcp:8083 tcp:8083 >nul 2>nul
)

echo [1/3] Levantando ms-clientes en puerto 8081...
start "ms-clientes (8081)" cmd /k "cd /d "%DIR%" && java -jar ms-clientes\target\ms-clientes-1.0.0-SNAPSHOT.jar"

timeout /t 3 >nul

echo [2/3] Levantando ms-localizaciones en puerto 8083...
start "ms-localizaciones (8083)" cmd /k "cd /d "%DIR%" && java -jar ms-localizaciones\target\ms-localizaciones-1.0.0-SNAPSHOT.jar"

timeout /t 3 >nul

echo [3/3] Levantando ms-premios en puerto 8082...
start "ms-premios (8082)" cmd /k "cd /d "%DIR%" && java -jar ms-premios\target\ms-premios-1.0.0-SNAPSHOT.jar"

echo.
echo =======================================================================
echo Listo. Los 3 microservicios se abrieron en ventanas independientes.
echo Para detenerlos, simplemente cierra sus ventanas correspondientes.
echo =======================================================================
echo.
pause

