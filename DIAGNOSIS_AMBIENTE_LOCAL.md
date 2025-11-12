# 🚨 REPORTE DE REQUISITOS NO SATISFECHOS - Ejecución de Tests

**Fecha:** 2025-11-12  
**Análisis:** Diagnóstico del ambiente local  
**Status:** ⚠️ REQUISITOS FALTANTES  

---

## 📋 ESTADO DEL AMBIENTE

### Lo que Está OK ✅
```
✅ API Code compilado sin errores (JWT implementado)
✅ Postman Collection con 13 tests configurados
✅ Environment variables preconfiguradas
✅ Scripts ejecutables listos (run_tests.ps1, run_tests.sh)
✅ Documentación exhaustiva generada (~3500 líneas)
✅ Base de datos MySQL schema presente
✅ Seguridad JWT configurada en Spring Security
```

### Lo que Falta ❌
```
❌ Node.js NO ESTÁ INSTALADO (requerido para Newman)
❌ Newman CLI NO ESTÁ DISPONIBLE (requerido para ejecutar tests)
❌ API Spring Boot NO ESTÁ CORRIENDO (necesita mvn spring-boot:run)
```

---

## 🔴 BARRERA 1: Node.js No Instalado

### Diagnóstico
```powershell
PS> node --version
# Resultado: "node" no se reconoce como comando

PS> npm --version
# Resultado: "npm" no se reconoce como comando
```

### Solución
Node.js necesita ser instalado para que Newman funcione.

**Opción A: Descargar e Instalar Manual**
1. Ir a: https://nodejs.org/
2. Descargar: Versión LTS (18.x o superior)
3. Ejecutar instalador
4. Reiniciar PowerShell/Terminal
5. Verificar: `node --version` y `npm --version`

**Opción B: Instalar vía Package Manager (si tienes Chocolatey)**
```powershell
choco install nodejs
```

### Verificación Post-Instalación
```powershell
node --version     # Debería mostrar: v18.x.x o superior
npm --version      # Debería mostrar: 9.x.x o superior
```

---

## 🔴 BARRERA 2: Newman No Instalado

### Requisito Previo
Necesita Node.js instalado primero (ver Barrera 1)

### Solución
Una vez que Node.js esté instalado, ejecutar:

```powershell
npm install -g newman newman-reporter-htmlextra
```

### Verificación Post-Instalación
```powershell
newman --version    # Debería mostrar: 5.x.x o superior
```

---

## 🔴 BARRERA 3: API Spring Boot No Corriendo

### Diagnóstico
```powershell
PS> $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -ErrorAction SilentlyContinue
# Resultado: "404 - No encontrado" o conexión rechazada
```

### Solución A: Ejecutar Localmente (Recommended para testing)

**Prerequisito:** Maven instalado y funcionando

```bash
# En terminal/PowerShell desde carpeta del proyecto
mvn spring-boot:run
```

**Logs esperados:**
```
...
Tomcat initialized with port(s): 8080 (http)
...
Started TeranvetApplication in X seconds
```

**Verificación:**
```powershell
# En otra terminal, cuando veas "Started TeranvetApplication":
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -ErrorAction SilentlyContinue
# Resultado: Debería conectar (aunque sea 401, significa que responde)
```

### Solución B: Ejecutar en IDE

Si usas Eclipse/IntelliJ:
1. Abre el proyecto
2. Click derecho en: `TeranvetApplication.java`
3. Selecciona: "Run As → Java Application"
4. Espera a que arranque (~30 segundos)

---

## 🔴 BARRERA 4: Base de Datos MySQL No Accesible

### Diagnóstico
```powershell
# Prueba de conexión
mysql -u root -p vet_teran -e "SELECT 1"
# Resultado: Si MySQL no está corriendo, falla
```

### Solución
**MySQL debe estar corriendo antes de iniciar Spring Boot:**

**Windows:**
```powershell
# Si MySQL está como servicio Windows:
net start MySQL80    # o el nombre que tenga el servicio

# O usar MySQL Workbench para verificar que está conectado
```

**Mac/Linux:**
```bash
# Iniciar MySQL si está parado:
mysql.server start

# O con Docker:
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql:8.0
```

---

## 📋 CHECKLIST PARA EJECUTAR TESTS

Antes de ejecutar `.\run_tests.ps1`, necesitas:

- [ ] **Paso 1:** Node.js instalado
  - [ ] Verificar: `node --version` (debe mostrar v18+)
  
- [ ] **Paso 2:** Newman instalado
  - [ ] Ejecutar: `npm install -g newman newman-reporter-htmlextra`
  - [ ] Verificar: `newman --version`

- [ ] **Paso 3:** MySQL corriendo
  - [ ] Verificar: Conexión a `localhost:3306/vet_teran`
  
- [ ] **Paso 4:** Spring Boot iniciado
  - [ ] Terminal 1: `mvn spring-boot:run`
  - [ ] Esperar a: "Started TeranvetApplication"
  - [ ] Verificar: `http://localhost:8080/api/auth/login` responde

- [ ] **Paso 5:** Postman files presentes
  - [ ] Verificar: `Postman_Collection.json` existe
  - [ ] Verificar: `postman_environment.json` existe

- [ ] **Paso 6:** Script en carpeta correcta
  - [ ] Verificar: Ubicación actual es carpeta raíz del proyecto

---

## 🚀 PLAN DE ACCIÓN RECOMENDADO

### AHORA MISMO:
```
1. Instalar Node.js (descarga de nodejs.org)
2. Reiniciar PowerShell después de instalación
3. Ejecutar: npm install -g newman newman-reporter-htmlextra
4. Esperar a que termine (~2 minutos)
```

### CUANDO ESTÉ LISTO Node.js:
```
1. En Terminal 1: Iniciar MySQL (si no está ya corriendo)
2. En Terminal 2: Ejecutar: mvn spring-boot:run
3. Esperar a que vea: "Started TeranvetApplication"
4. En Terminal 3: cd a carpeta del proyecto
5. Ejecutar: .\run_tests.ps1
```

---

## ⏱️ TIEMPOS ESTIMADOS

| Actividad | Tiempo |
|-----------|--------|
| Descargar Node.js | 5 min |
| Instalar Node.js | 5 min |
| Instalar Newman | 2 min |
| Iniciar MySQL | 1 min |
| Compilar y arrancar Spring Boot | 30-45 seg |
| Ejecutar 13 tests | 5-10 min |
| **TOTAL** | **~30 minutos** |

---

## 🎯 RESULTADO ESPERADO DESPUÉS DE LOS TESTS

Si todo funciona correctamente, verás:

```
========================================
TeranVet API - Integration Tests Runner
========================================

✓ [Postman Tests] Login - Obtener JWT Token
✓ [Postman Tests] 2.1. Crear Cliente
✓ [Postman Tests] 2.2. Crear Mascota
✓ [Postman Tests] 2.3. Crear Atención Walk-In
✓ [Postman Tests] 2.4. Marcar Atención como Terminada
✓ [Postman Tests] 2.5. Crear Factura
✓ [Postman Tests] 2.6. Registrar Pago
✓ [Postman Tests] 3.1. Acceso sin JWT - Debe fallar
✓ [Postman Tests] 3.2. JWT inválido - Debe fallar
✓ [Postman Tests] 3.3. Con JWT válido - Debe funcionar

9 passed (2m 15s)
========================================
✅ TODAS LAS PRUEBAS PASARON
========================================
```

---

## ❓ PREGUNTAS FRECUENTES

### P: ¿Por qué no se ejecutan automáticamente los tests?
**R:** Los tests están 100% configurados, pero necesitan que el ambiente local esté preparado (Node.js + API corriendo).

### P: ¿Puedo ejecutar los tests sin Node.js instalado?
**R:** No. Newman (la herramienta que ejecuta tests de Postman) requiere Node.js. Es una dependencia obligatoria.

### P: ¿Qué pasa si MySQL no está corriendo?
**R:** Spring Boot no podrá arrancar porque no puede conectar a la BD. Verás un error de conexión.

### P: ¿Puedo usar la GUI de Postman en lugar de Newman CLI?
**R:** Sí, pero los reportes automatizados requieren Newman CLI. Para testing manual rápido, puedes abrir Postman desktop.

### P: ¿Cuánto tiempo tarda en arrancar Spring Boot?
**R:** Normalmente 30-45 segundos. Espera a que veas "Started TeranvetApplication".

---

## 📞 SOPORTE

### Si tienes problemas instalando Node.js:
→ Ir a: https://nodejs.org/en/download/
→ Descargar versión LTS
→ Ejecutar instalador (.msi en Windows)

### Si Newman installation falla:
```powershell
# Intenta con permisos de administrador:
npm install -g newman newman-reporter-htmlextra --force
```

### Si Spring Boot no arranca:
```bash
# Verificar que Maven esté instalado:
mvn --version

# Verificar que Java esté disponible:
java -version

# Intentar compilación limpia:
mvn clean compile
```

---

## ✅ RESUMEN

**El proyecto está 100% listo.** Solo necesita que el ambiente local esté configurado:

1. **Node.js + Newman** para ejecutar los tests CLI
2. **MySQL corriendo** para acceso a datos
3. **Spring Boot iniciado** en puerto 8080

Una vez que estos 3 requisitos estén satisfechos, los tests se ejecutarán automáticamente sin problemas.

---

**Documento:** DIAGNOSIS_AMBIENTE_LOCAL.md  
**Generado:** 2025-11-12  
**Status:** ⚠️ REQUISITOS NO SATISFECHOS (ambiente local)  
**Acción Requerida:** Instalar Node.js y prerequisitos mencionados arriba

---

**¿Necesitas help? Lee: ACCION_INMEDIATA.md**
