# 🚀 STARTUP GUIDE - Ejecutar TeranVet API y Tests
# ================================================

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║    TeranVet: GUÍA DE INICIO RÁPIDO (Quick Start)          ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Magenta
Write-Host ""
Write-Host "Esta guía te ayudará a iniciar la API y ejecutar los tests." -ForegroundColor Cyan
Write-Host ""

Write-Host "📋 REQUISITOS PREVIOS:" -ForegroundColor Yellow
Write-Host "  ✓ MySQL 8.0 ejecutándose" -ForegroundColor Gray
Write-Host "  ✓ Base de datos 'veterinaria_teran' creada" -ForegroundColor Gray
Write-Host "  ✓ Java 8+ instalado" -ForegroundColor Gray
Write-Host "  ✓ Maven instalado" -ForegroundColor Gray
Write-Host ""

Write-Host "PASO 1: Verificar que MySQL está ejecutándose" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host "En Windows:"
Write-Host "  • Abre Services (services.msc)" -ForegroundColor Gray
Write-Host "  • Busca 'MySQL80'" -ForegroundColor Gray
Write-Host "  • Verifica que está en estado 'Running'" -ForegroundColor Gray
Write-Host ""

Write-Host "O ejecuta este comando:" -ForegroundColor Gray
Write-Host '  Get-Service | Where-Object {$_.Name -like "*MySQL*"} | Select-Object Name, Status' -ForegroundColor Green
Write-Host ""

Write-Host "PASO 2: Navega a la carpeta del proyecto" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host '  cd "c:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot"' -ForegroundColor Green
Write-Host ""

Write-Host "PASO 3: Inicia la API (Abre NUEVA Terminal/PowerShell)" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host "  mvn clean spring-boot:run" -ForegroundColor Green
Write-Host ""
Write-Host "Espera hasta ver: 'Started TeranvetApplication'" -ForegroundColor Yellow
Write-Host ""

Write-Host "PASO 4: En OTRA terminal, ejecuta los tests" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host "Opción A - Con el script alternativo (PowerShell):" -ForegroundColor White
Write-Host "  .\run_tests_alternative.ps1" -ForegroundColor Green
Write-Host ""
Write-Host "Opción B - Con Postman CLI (si tienes Newman instalado):" -ForegroundColor White
Write-Host "  .\run_tests.ps1" -ForegroundColor Green
Write-Host ""
Write-Host "Opción C - Con curl manual:" -ForegroundColor White
Write-Host '  curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{""email"":""admin@example.com"",""password"":""admin123""}"' -ForegroundColor Green
Write-Host ""

Write-Host "📊 CREDENCIALES POR DEFECTO:" -ForegroundColor Magenta
Write-Host "  Email: admin@example.com" -ForegroundColor Gray
Write-Host "  Password: admin123" -ForegroundColor Gray
Write-Host ""

Write-Host "🔍 VERIFICACIÓN RÁPIDA:" -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host ""
Write-Host "Para verificar que la API está ejecutándose:" -ForegroundColor White
Write-Host '  curl http://localhost:8080/health' -ForegroundColor Green
Write-Host ""
Write-Host "Deberías recibir: 200 OK" -ForegroundColor Gray
Write-Host ""

Write-Host "📚 ARCHIVOS ÚTILES:" -ForegroundColor Yellow
Write-Host "  • run_tests_alternative.ps1 - Tests en PowerShell (SIN necesidad de Node.js)" -ForegroundColor Gray
Write-Host "  • run_tests.ps1 - Tests con Newman (requiere Node.js)" -ForegroundColor Gray
Write-Host "  • DIAGNOSIS_AMBIENTE_LOCAL.md - Diagnóstico del ambiente" -ForegroundColor Gray
Write-Host "  • INTEGRATION_TEST_GUIDE.md - Guía completa de tests" -ForegroundColor Gray
Write-Host ""

Write-Host "⏱️  TIEMPO ESTIMADO:" -ForegroundColor Cyan
Write-Host "  • Iniciar API: ~45 segundos" -ForegroundColor Gray
Write-Host "  • Ejecutar tests: ~10 segundos" -ForegroundColor Gray
Write-Host "  • Total: ~1 minuto" -ForegroundColor Gray
Write-Host ""

Write-Host "💡 TIPS:" -ForegroundColor Yellow
Write-Host "  1. Mantén ambas terminales (API y Tests) abiertas para ver logs" -ForegroundColor Gray
Write-Host "  2. Si ves error de conexión, verifica que MySQL está corriendo" -ForegroundColor Gray
Write-Host "  3. Los logs de la API están en: target/classes (si compilas con Maven)" -ForegroundColor Gray
Write-Host "  4. El token JWT expira en 24 horas desde su creación" -ForegroundColor Gray
Write-Host ""

Write-Host "❓ PROBLEMAS COMUNES:" -ForegroundColor Red
Write-Host ""
Write-Host "  Error: 'mvn: command not found'" -ForegroundColor White
Write-Host "    → Maven no está en PATH. Instala Maven o agrega a PATH." -ForegroundColor Gray
Write-Host ""
Write-Host "  Error: 'Connection refused' en tests" -ForegroundColor White
Write-Host "    → La API no está ejecutándose. Ejecuta el PASO 3." -ForegroundColor Gray
Write-Host ""
Write-Host "  Error: '401 Unauthorized' en tests" -ForegroundColor White
Write-Host "    → Credenciales incorrectas. Verifica email/password en BD." -ForegroundColor Gray
Write-Host ""

Write-Host "═════════════════════════════════════════════════════════════" -ForegroundColor Magenta
Write-Host "¡Listo! Sigue los pasos anteriores para ejecutar los tests." -ForegroundColor Magenta
Write-Host "═════════════════════════════════════════════════════════════" -ForegroundColor Magenta
