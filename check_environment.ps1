# Herramienta de Diagnostico - Verificar requisitos previos
# =========================================================

Write-Host "====== VERIFICANDO REQUISITOS ======" -ForegroundColor Cyan
Write-Host ""

$allGood = $true

# 1. Verificar Java
Write-Host "1. Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "   OK: Java instalado" -ForegroundColor Green
}
catch {
    Write-Host "   ERROR: Java NO esta instalado" -ForegroundColor Red
    $allGood = $false
}

# 2. Verificar Maven
Write-Host "2. Verificando Maven..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "   OK: Maven instalado" -ForegroundColor Green
}
catch {
    Write-Host "   ERROR: Maven NO esta instalado" -ForegroundColor Red
    $allGood = $false
}

# 3. Verificar MySQL
Write-Host "3. Verificando MySQL..." -ForegroundColor Yellow
try {
    $mysqlServices = Get-Service | Where-Object {$_.Name -like "*MySQL*"} | Select-Object -First 1
    if ($mysqlServices) {
        if ($mysqlServices.Status -eq "Running") {
            Write-Host "   OK: MySQL esta ejecutandose" -ForegroundColor Green
        }
        else {
            Write-Host "   ADVERTENCIA: MySQL no esta ejecutandose" -ForegroundColor Yellow
        }
    }
}
catch {
    Write-Host "   ADVERTENCIA: No se pudo verificar MySQL" -ForegroundColor Yellow
}

# 4. Verificar si el API esta ejecutandose
Write-Host "4. Verificando si API esta en localhost:8080..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/health" -Method GET -SkipHttpErrorCheck -TimeoutSec 2
    if ($response.StatusCode -eq 200) {
        Write-Host "   OK: API esta ejecutandose" -ForegroundColor Green
    }
    else {
        Write-Host "   ERROR: API no responde correctamente (Status: $($response.StatusCode))" -ForegroundColor Red
        $allGood = $false
    }
}
catch {
    Write-Host "   ERROR: API NO esta ejecutandose en http://localhost:8080" -ForegroundColor Red
    Write-Host "   Ejecuta: mvn clean spring-boot:run" -ForegroundColor Gray
    $allGood = $false
}

Write-Host ""
Write-Host "====== RESUMEN ======" -ForegroundColor Cyan

if ($allGood) {
    Write-Host "TODO ESTA LISTO!" -ForegroundColor Green
    Write-Host "Puedes ejecutar: .\run_tests_alternative.ps1" -ForegroundColor Green
}
else {
    Write-Host "FALTAN REQUISITOS" -ForegroundColor Red
    Write-Host "Consulta STARTUP_GUIDE.ps1 para mas informacion" -ForegroundColor Yellow
}
