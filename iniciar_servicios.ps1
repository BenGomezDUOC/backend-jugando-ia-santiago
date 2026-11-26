# =======================================================================
# Script de inicio para Windows PowerShell
# =======================================================================
$ErrorActionPreference = "Stop"
$DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $DIR

Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "  Iniciando Arquitectura Completa de Microservicios (5 Componentes)" -ForegroundColor Cyan
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Puertos:" -ForegroundColor Yellow
Write-Host "  1. Eureka Server   : http://localhost:8761" -ForegroundColor Yellow
Write-Host "  2. API Gateway     : http://localhost:8080" -ForegroundColor Yellow
Write-Host "  3. ms-clientes     : http://localhost:8081" -ForegroundColor Yellow
Write-Host "  4. ms-premios      : http://localhost:8082" -ForegroundColor Yellow
Write-Host "  5. ms-localizaciones: http://localhost:8083" -ForegroundColor Yellow
Write-Host ""

Write-Host "[1/5] Levantando Eureka Server (8761)..." -ForegroundColor Green
Start-Process cmd.exe -ArgumentList "/k cd /d `"$DIR`" && java -jar eureka-server\target\eureka-server-1.0.0-SNAPSHOT.jar"
Start-Sleep -Seconds 5

Write-Host "[2/5] Levantando ms-clientes (8081)..." -ForegroundColor Green
Start-Process cmd.exe -ArgumentList "/k cd /d `"$DIR`" && java -jar ms-clientes\target\ms-clientes-1.0.0-SNAPSHOT.jar"
Start-Sleep -Seconds 3

Write-Host "[3/5] Levantando ms-localizaciones (8083)..." -ForegroundColor Green
Start-Process cmd.exe -ArgumentList "/k cd /d `"$DIR`" && java -jar ms-localizaciones\target\ms-localizaciones-1.0.0-SNAPSHOT.jar"
Start-Sleep -Seconds 3

Write-Host "[4/5] Levantando ms-premios (8082)..." -ForegroundColor Green
Start-Process cmd.exe -ArgumentList "/k cd /d `"$DIR`" && java -jar ms-premios\target\ms-premios-1.0.0-SNAPSHOT.jar"
Start-Sleep -Seconds 3

Write-Host "[5/5] Levantando API Gateway (8080)..." -ForegroundColor Green
Start-Process cmd.exe -ArgumentList "/k cd /d `"$DIR`" && java -jar api-gateway\target\api-gateway-1.0.0-SNAPSHOT.jar"

Write-Host ""
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "Listo. Todos los servicios iniciados en ventanas independientes." -ForegroundColor Cyan
Write-Host "=======================================================================" -ForegroundColor Cyan
