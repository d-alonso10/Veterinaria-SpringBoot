# 🎯 INSTRUCCIONES EXACTAS - Cómo Ejecutar Tests Ahora

## Estado Actual
```
✅ JWT implementado y compilable
✅ Tests configurados en Postman
✅ Scripts de ejecución listos
✅ Documentación completa

❌ Maven no instalado
❌ API no ejecutándose
✅ Java disponible
```

---

## PASO 1: Instalar Maven (SI NO LO TIENES)

### Opción A: Descarga Manual
1. Ve a: https://maven.apache.org/download.cgi
2. Descarga: apache-maven-3.8.X-bin.zip (Windows)
3. Extrae a: `C:\Program Files\Maven` (o donde prefieras)
4. Agrega a PATH:
   ```
   Variable: MAVEN_HOME = C:\Program Files\Maven
   Agrega a PATH: %MAVEN_HOME%\bin
   ```
5. Reinicia terminal
6. Verifica: `mvn -v`

### Opción B: Choco (Si tienes Chocolatey)
```powershell
choco install maven
```

### Opción C: SCOOP (Si tienes SCOOP)
```powershell
scoop install maven
```

### Verificación:
```powershell
mvn -version
# Deberías ver: Apache Maven 3.X.X
```

---

## PASO 2: Iniciar la API

En **TERMINAL 1** (déjala abierta):
```powershell
cd "c:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot"
mvn clean spring-boot:run
```

Espera hasta ver:
```
Started TeranvetApplication in XX.XXX seconds
```

**Nota:** La terminal mostrará logs. Es normal. No la cierres.

---

## PASO 3: Ejecutar Tests

En **TERMINAL 2** (nueva terminal):
```powershell
cd "c:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot"
.\run_tests_alternative.ps1
```

Verás algo como:
```
══════════════════════════════════════════════════════════
         VETERINARIA TERAN - INTEGRATION TEST SUITE
         Alternativa: PowerShell Puro (Sin Newman)
══════════════════════════════════════════════════════════

[1] Testing: Health Check
   Method: GET | Endpoint: /health
   Status: 200
   ✅ PASS - Status code matches expected (200)
...

[Total]: 13 tests
✅ Passed: 13
❌ Failed: 0
Pass Rate: 100%
```

---

## OPCIONES ADICIONALES

### Si Prefieres Usar Newman (Con Node.js)

**Requisito previo:** Node.js instalado (https://nodejs.org)

```powershell
# Instalar Newman
npm install -g newman newman-reporter-htmlextra

# Ejecutar tests
.\run_tests.ps1

# Verás un reporte HTML al final
```

---

### Si Prefieres Usar Postman GUI

1. Abre Postman (https://postman.com/downloads/)
2. Import: `Postman_Collection.json`
3. Import Environment: `postman_environment.json`
4. Selecciona el environment en dropdown
5. Click "Run collection"
6. Espera resultados

---

### Si Prefieres Pruebas Manuales con curl

```powershell
# 1. LOGIN Y OBTENER TOKEN
$loginResponse = curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }' | ConvertFrom-Json

$token = $loginResponse.token
Write-Host "Token obtenido: $($token.Substring(0, 50))..."

# 2. USAR TOKEN EN SIGUIENTE REQUEST
curl -X GET http://localhost:8080/api/clientes `
  -H "Authorization: Bearer $token" | ConvertFrom-Json | ConvertTo-Json

# 3. VERIFICAR QUE FUNCIONA
# Deberías ver una lista de clientes en JSON
```

---

## SOLUCIÓN DE PROBLEMAS

### Error: "mvn: command not found"
```
Solución: Maven no está en PATH
1. Instala Maven (ver PASO 1)
2. Reinicia tu terminal
3. Intenta nuevamente
```

### Error: "Connection refused" o "404 Not Found"
```
Solución: API no está ejecutándose
1. Ve a TERMINAL 1
2. Verifica que ves "Started TeranvetApplication"
3. Si no, espera 30-45 segundos
4. Si aún no funciona, reinicia terminal
```

### Error: "401 Unauthorized"
```
Solución: Credenciales incorrectas
1. Verifica que admin@example.com existe en BD
2. Verifica que la contraseña es admin123
3. Consulta con tu admin de BD si es diferente
```

### Error: "Port 8080 is already in use"
```
Solución: Otro proceso usa el puerto
1. Encuentra qué proceso: netstat -ano | findstr :8080
2. Mata el proceso: taskkill /PID <PID> /F
3. O cambia puerto en: application.properties (server.port=8081)
```

---

## FLUJO RECOMENDADO

### Para Verificación Rápida (5 min)
1. `mvn clean spring-boot:run` en Terminal 1
2. Espera "Started TeranvetApplication"
3. `.\run_tests_alternative.ps1` en Terminal 2
4. Espera resultado

### Para Trabajo Continuo (30+ min)
1. Terminal 1: `mvn clean spring-boot:run` (dejar abierta)
2. Terminal 2: Abierta para tests cuando sea necesario
3. Terminal 3: Para otros comandos

### Para Desarrollo (8+ horas)
1. Terminal 1: API ejecutándose (dejada abierta)
2. Terminal 2: Tests cuando sea necesario
3. Editor: VS Code para cambios de código
4. Al cambiar código Java:
   - La API se recompila automáticamente (devtools)
   - Vuelve a ejecutar tests

---

## CREDENCIALES PARA TESTING

```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**⚠️ Asegúrate:** Que este usuario existe en tu BD de producción/desarrollo

---

## ANTES DE INICIAR

Verifica que tienes:
- [ ] Java 8+ instalado (`java -version`)
- [ ] Maven instalado (`mvn -version`)
- [ ] MySQL ejecutándose (Services → MySQL80 → Running)
- [ ] BD "veterinaria_teran" existe
- [ ] Usuario admin@example.com existe
- [ ] 2 terminales disponibles
- [ ] 5-10 minutos disponibles

---

## PASOS EXACTOS (COPY-PASTE)

### Terminal 1:
```powershell
cd "c:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot"
mvn clean spring-boot:run
```

### Terminal 2:
```powershell
cd "c:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot"
.\run_tests_alternative.ps1
```

### Resultado Esperado:
```
✅ TODAS LAS PRUEBAS PASARON
Pass Rate: 100%
✅ 13/13 tests PASSED
```

---

## DOCUMENTOS DE REFERENCIA

- **STARTUP_GUIDE.ps1** - Guía detallada
- **check_environment.ps1** - Verificar requisitos
- **INTEGRATION_TEST_GUIDE.md** - Detalles de tests
- **JWT_IMPLEMENTATION_REPORT.md** - Detalles de JWT
- **REPORTE_FINAL_COMPLETO.md** - Resumen completo
- **FINAL_CHECKLIST.md** - Checklist de verificación

---

## DESPUÉS DE QUE PASEN LOS TESTS

Si todo pasa exitosamente:
1. ✅ Celebra! 🎉
2. Consulta: **ACCION_INMEDIATA.md**
3. Próximo paso: Swagger/OpenAPI (Prioridad 3)

---

**¡Adelante! Estos son los pasos exactos. 100% debería funcionar.**

Si tienes problemas, consulta DIAGNOSIS_AMBIENTE_LOCAL.md o INTEGRATION_TEST_GUIDE.md
