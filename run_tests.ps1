# Script para ejecutar pruebas de integración con Newman (Windows PowerShell)
# Uso: .\run_tests.ps1

Write-Host "=========================================="
Write-Host "TeranVet API - Integration Tests Runner"
Write-Host "=========================================="
Write-Host ""

# Variables
$COLLECTION_FILE = "Postman_Collection.json"
$ENVIRONMENT_FILE = "postman_environment.json"
$RESULTS_DIR = "test-results"
$TIMESTAMP = (Get-Date -Format "yyyyMMdd_HHmmss")

# Crear directorio de resultados
if (!(Test-Path $RESULTS_DIR)) {
    New-Item -ItemType Directory -Path $RESULTS_DIR | Out-Null
}

# Verificar si Newman está instalado
try {
    $newman = (npm list -g newman 2>&1 | Select-String "newman")
    if ($null -eq $newman) {
        throw "Newman no encontrado"
    }
    Write-Host "✅ Newman encontrado"
} catch {
    Write-Host "❌ Newman no está instalado."
    Write-Host "Instalando Newman globalmente..."
    npm install -g newman newman-reporter-htmlextra
    Write-Host "✅ Newman instalado correctamente"
}

# Verificar si los archivos existen
if (!(Test-Path $COLLECTION_FILE)) {
    Write-Host "❌ Error: No se encontró $COLLECTION_FILE"
    exit 1
}

if (!(Test-Path $ENVIRONMENT_FILE)) {
    Write-Host "❌ Error: No se encontró $ENVIRONMENT_FILE"
    exit 1
}

Write-Host ""
Write-Host "📋 Ejecutando pruebas..."
Write-Host "   Colección: $COLLECTION_FILE"
Write-Host "   Entorno: $ENVIRONMENT_FILE"
Write-Host "   Resultados guardados en: $RESULTS_DIR\"
Write-Host ""

# Ejecutar Newman con reportes
$resultsJson = "$RESULTS_DIR/results_$TIMESTAMP.json"
$resultsHtml = "$RESULTS_DIR/report_$TIMESTAMP.html"

newman run "$COLLECTION_FILE" `
  --environment "$ENVIRONMENT_FILE" `
  --reporters cli,json,htmlextra `
  --reporter-json-export "$resultsJson" `
  --reporter-htmlextra-export "$resultsHtml" `
  --bail `
  --verbose

# Capturar resultado
$NEWMAN_EXIT_CODE = $LASTEXITCODE

Write-Host ""
Write-Host "=========================================="

if ($NEWMAN_EXIT_CODE -eq 0) {
    Write-Host "✅ TODAS LAS PRUEBAS PASARON"
    Write-Host "   📊 Resultados JSON: $resultsJson"
    Write-Host "   📈 Reporte HTML: $resultsHtml"
    Write-Host ""
    Write-Host "💡 Abre el reporte HTML para más detalles:"
    Write-Host "   Invoke-Item `"$resultsHtml`""
} else {
    Write-Host "❌ ALGUNAS PRUEBAS FALLARON"
    Write-Host "   Código de salida: $NEWMAN_EXIT_CODE"
}

Write-Host "=========================================="
Write-Host ""

exit $NEWMAN_EXIT_CODE
