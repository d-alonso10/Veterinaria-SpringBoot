# Script de Pruebas Alternativo - Usando PowerShell Puro (sin Newman)
# Propósito: Ejecutar tests del API sin depender de Node.js/Newman
# Prerrequisito: API ejecutándose en localhost:8080

param(
    [string]$ApiUrl = "http://localhost:8080",
    [string]$AdminEmail = "admin@example.com",
    [string]$AdminPassword = "admin123"
)

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║         VETERINARIA TERAN - INTEGRATION TEST SUITE         ║" -ForegroundColor Cyan
Write-Host "║         Alternativa: PowerShell Puro (Sin Newman)         ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""
Write-Host "API URL: $ApiUrl" -ForegroundColor Yellow
Write-Host "Test Start Time: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Gray
Write-Host ""

# Contadores para resumen
$totalTests = 0
$passedTests = 0
$failedTests = 0
$errors = @()

# Función auxiliar para hacer requests
function Invoke-ApiTest {
    param(
        [string]$TestName,
        [string]$Method,
        [string]$Endpoint,
        [string]$Body,
        [int]$ExpectedStatusCode,
        [hashtable]$Headers = @{}
    )
    
    $totalTests++
    $testNumber = $totalTests
    $testUrl = "$ApiUrl$Endpoint"
    
    Write-Host "[$testNumber] Testing: $TestName" -ForegroundColor White
    Write-Host "   Method: $Method | Endpoint: $Endpoint" -ForegroundColor Gray
    
    try {
        $params = @{
            Uri             = $testUrl
            Method          = $Method
            ContentType     = "application/json"
            ErrorAction     = "Stop"
            SkipHttpErrorCheck = $true
        }
        
        if ($Headers.Count -gt 0) {
            $params['Headers'] = $Headers
        }
        
        if ($Body) {
            $params['Body'] = $Body
        }
        
        $response = Invoke-WebRequest @params
        $statusCode = $response.StatusCode
        $responseBody = $response.Content
        
        Write-Host "   Status: $statusCode" -ForegroundColor Green
        
        if ($statusCode -eq $ExpectedStatusCode) {
            Write-Host "   ✅ PASS - Status code matches expected ($ExpectedStatusCode)" -ForegroundColor Green
            $passedTests++
            $result = $true
        } else {
            Write-Host "   ❌ FAIL - Expected $ExpectedStatusCode but got $statusCode" -ForegroundColor Red
            $failedTests++
            $errors += "Test $testNumber ($TestName): Expected $ExpectedStatusCode, got $statusCode"
            $result = $false
        }
        
        Write-Host "   Response: $($responseBody.Substring(0, [Math]::Min(200, $responseBody.Length)))" -ForegroundColor Gray
        Write-Host ""
        
        return @{
            Success = $result
            StatusCode = $statusCode
            Body = $responseBody
        }
    }
    catch {
        Write-Host "   ❌ FAIL - Error: $($_.Exception.Message)" -ForegroundColor Red
        $failedTests++
        $errors += "Test $testNumber ($TestName): $($_.Exception.Message)"
        Write-Host ""
        return @{
            Success = $false
            StatusCode = 0
            Body = ""
        }
    }
}

# =====================================
# TEST 1: Health Check
# =====================================
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "TEST GROUP: HEALTH & AVAILABILITY" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

$healthResult = Invoke-ApiTest `
    -TestName "Health Check" `
    -Method "GET" `
    -Endpoint "/health" `
    -ExpectedStatusCode 200

# =====================================
# TEST 2: Login - Credenciales válidas
# =====================================
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "TEST GROUP: AUTHENTICATION (JWT)" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

$loginBody = @{
    email    = $AdminEmail
    password = $AdminPassword
} | ConvertTo-Json

$loginResult = Invoke-ApiTest `
    -TestName "Login con Credenciales Válidas" `
    -Method "POST" `
    -Endpoint "/api/auth/login" `
    -Body $loginBody `
    -ExpectedStatusCode 200

# Extraer token si el login fue exitoso
$jwtToken = ""
if ($loginResult.Success) {
    try {
        $loginResponse = $loginResult.Body | ConvertFrom-Json
        $jwtToken = $loginResponse.token
        Write-Host "   📌 JWT Token obtenido: $($jwtToken.Substring(0, [Math]::Min(50, $jwtToken.Length)))..." -ForegroundColor Yellow
        Write-Host ""
    }
    catch {
        Write-Host "   ⚠️  No se pudo extraer token: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

# =====================================
# TEST 3: Login - Credenciales inválidas
# =====================================

$invalidLoginBody = @{
    email    = "invalid@example.com"
    password = "wrongpassword"
} | ConvertTo-Json

$invalidLoginResult = Invoke-ApiTest `
    -TestName "Login con Credenciales Inválidas (Debe Fallar)" `
    -Method "POST" `
    -Endpoint "/api/auth/login" `
    -Body $invalidLoginBody `
    -ExpectedStatusCode 401

# =====================================
# TESTS CON JWT TOKEN (Si disponible)
# =====================================
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "TEST GROUP: PROTECTED ENDPOINTS (Requieren JWT)" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

if ($jwtToken) {
    $authHeaders = @{
        "Authorization" = "Bearer $jwtToken"
    }
    
    # TEST 4: Listar Clientes (Autenticado)
    $clientesResult = Invoke-ApiTest `
        -TestName "Listar Clientes (Autenticado)" `
        -Method "GET" `
        -Endpoint "/api/clientes" `
        -Headers $authHeaders `
        -ExpectedStatusCode 200
    
    # TEST 5: Listar Mascotas (Autenticado)
    $mascotasResult = Invoke-ApiTest `
        -TestName "Listar Mascotas (Autenticado)" `
        -Method "GET" `
        -Endpoint "/api/mascotas" `
        -Headers $authHeaders `
        -ExpectedStatusCode 200
    
    # TEST 6: Listar Servicios (Autenticado)
    $serviciosResult = Invoke-ApiTest `
        -TestName "Listar Servicios (Autenticado)" `
        -Method "GET" `
        -Endpoint "/api/servicios" `
        -Headers $authHeaders `
        -ExpectedStatusCode 200
}
else {
    Write-Host "⚠️  JWT Token no disponible. Saltando tests de endpoints protegidos." -ForegroundColor Yellow
    Write-Host ""
}

# =====================================
# TEST: Sin Autenticación (Debe Fallar)
# =====================================
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "TEST GROUP: SECURITY - Sin Autenticación (Debe Fallar)" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

$noAuthResult = Invoke-ApiTest `
    -TestName "Acceso a /api/clientes SIN Token (Debe Fallar - 401)" `
    -Method "GET" `
    -Endpoint "/api/clientes" `
    -ExpectedStatusCode 401

# =====================================
# RESUMEN FINAL
# =====================================
Write-Host ""
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "RESUMEN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total Tests: $totalTests" -ForegroundColor White
Write-Host "✅ Passed: $passedTests" -ForegroundColor Green
Write-Host "❌ Failed: $failedTests" -ForegroundColor Red
Write-Host ""

$passRate = if ($totalTests -gt 0) { [Math]::Round(($passedTests / $totalTests) * 100, 2) } else { 0 }
Write-Host "Pass Rate: $passRate%" -ForegroundColor Cyan
Write-Host ""

if ($failedTests -gt 0) {
    Write-Host "ERRORES DETECTADOS:" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  • $error" -ForegroundColor Red
    }
    Write-Host ""
}

# Status final
if ($failedTests -eq 0 -and $passedTests -gt 0) {
    Write-Host "✅ TODAS LAS PRUEBAS PASARON" -ForegroundColor Green
    exit 0
}
else {
    Write-Host "❌ ALGUNAS PRUEBAS FALLARON" -ForegroundColor Red
    exit 1
}
