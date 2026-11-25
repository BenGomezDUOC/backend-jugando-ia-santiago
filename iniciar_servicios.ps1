Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "  Iniciando los 3 Microservicios de Aprendo con IA en Santiago" -ForegroundColor Green
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "  [1] ms-clientes        -> http://localhost:8081" -ForegroundColor Yellow
Write-Host "  [2] ms-localizaciones  -> http://localhost:8083" -ForegroundColor Yellow
Write-Host "  [3] ms-premios         -> http://localhost:8082" -ForegroundColor Yellow
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "NOTA: Tu otro proyecto en el puerto 8080 no se vera afectado."
Write-Host "Requiere MySQL iniciado en el puerto 3306."
Write-Host ""

$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (Test-Path $adbPath) {
    Write-Host "Configurando puente ADB (adb reverse) para celular fisico..." -ForegroundColor Gray
    & $adbPath reverse tcp:8081 tcp:8081 2>$null
    & $adbPath reverse tcp:8082 tcp:8082 2>$null
    & $adbPath reverse tcp:8083 tcp:8083 2>$null
}

$dir = $PSScriptRoot

Write-Host "[1/3] Levantando ms-clientes (8081)..." -ForegroundColor White
Start-Process cmd.exe -ArgumentList "/k cd /d `"$dir`" && java -jar ms-clientes\target\ms-clientes-1.0.0-SNAPSHOT.jar"
Start-Sleep -Seconds 3

Write-Host "[2/3] Levantando ms-localizaciones (8083)..." -ForegroundColor White
Start-Process cmd.exe -ArgumentList "/k cd /d `"$dir`" && java -jar ms-localizaciones\target\ms-localizaciones-1.0.0-SNAPSHOT.jar"
Start-Sleep -Seconds 3

Write-Host "[3/3] Levantando ms-premios (8082)..." -ForegroundColor White
Start-Process cmd.exe -ArgumentList "/k cd /d `"$dir`" && java -jar ms-premios\target\ms-premios-1.0.0-SNAPSHOT.jar"

Write-Host ""
Write-Host "=======================================================================" -ForegroundColor Cyan
Write-Host "Listo. Los 3 microservicios se estan ejecutando en ventanas separadas." -ForegroundColor Green
Write-Host "Para detener cualquiera, simplemente cierra su respectiva ventana." -ForegroundColor White
Write-Host "=======================================================================" -ForegroundColor Cyan
