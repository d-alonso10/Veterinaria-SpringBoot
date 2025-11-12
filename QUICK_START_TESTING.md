# 🚀 GUÍA RÁPIDA - EJECUTAR PRUEBAS DE INTEGRACIÓN

**Última Actualización:** 2025-11-12  
**Estado:** Listo para ejecutar  
**Duración Estimada:** 15-30 minutos  

---

## ⚡ INICIO RÁPIDO (5 MINUTOS)

### Si estás en WINDOWS (PowerShell):

```powershell
# 1. Navega a la carpeta del proyecto
cd "c:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot"

# 2. Verifica que los archivos existan
dir Postman_Collection.json
dir postman_environment.json
dir run_tests.ps1

# 3. Ejecuta las pruebas
.\run_tests.ps1

# 4. Espera a que terminen y abre el reporte
Invoke-Item "test-results\report_*.html"
```

### Si estás en MAC o LINUX:

```bash
# 1. Navega a la carpeta del proyecto
cd ~/Desktop/Veterinaria-Spring-Boot

# 2. Verifica que los archivos existan
ls -la Postman_Collection.json
ls -la postman_environment.json
ls -la run_tests.sh

# 3. Dale permisos al script
chmod +x run_tests.sh

# 4. Ejecuta las pruebas
bash run_tests.sh

# 5. Abre el reporte (macOS)
open test-results/report_*.html

# 5. Abre el reporte (Linux)
xdg-open test-results/report_*.html
```

---

## 🔍 VERIFICACIÓN PREVIA (3 MINUTOS)

Antes de ejecutar pruebas, verifica lo siguiente:

### ✅ API Spring Boot Corriendo

```bash
# Prueba que la API responda
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","passwordHash":"admin123"}'

# Deberías ver una respuesta con JWT token
# Si no funciona: Inicia Spring Boot en otra terminal
```

### ✅ Base de Datos Accesible

```bash
# Si tienes MySQL CLI:
mysql -u root -p vet_teran -e "SELECT COUNT(*) FROM Cliente;"

# Si no tienes MySQL CLI, verifica en tu GUI de MySQL:
# - Servidor: localhost
# - Base de datos: vet_teran
# - Usuario: root
# - Contraseña: (tu password)
```

### ✅ Newman Instalado

```bash
# Verifica si Newman está instalado
newman --version

# Si no está, instala (requiere Node.js):
npm install -g newman newman-reporter-htmlextra

# Verifica de nuevo
newman --version
```

---

## 🧪 EJECUCIÓN DE PRUEBAS (10 MINUTOS)

### Opción 1: Script Automático (Recomendado)

```powershell
# Windows
.\run_tests.ps1

# Responde a cualquier pregunta con "Y" (sí)
```

```bash
# Mac/Linux
bash run_tests.sh
```

### Opción 2: Comando Manual Newman

```bash
# Ejecutar directamente sin scripts
newman run Postman_Collection.json \
  --environment postman_environment.json \
  --reporters cli,json,htmlextra \
  --reporter-json-export test-results/results.json \
  --reporter-htmlextra-export test-results/report.html
```

### Opción 3: Interfaz Gráfica Postman

1. Abre **Postman**
2. Click: **File** → **Import**
3. Selecciona `Postman_Collection.json`
4. Click: **Import**
5. Click: **Environment** → **Import**
6. Selecciona `postman_environment.json`
7. Click: **Import**
8. Selecciona el entorno importado (dropdown arriba)
9. Click en la colección → **Run** (botón a la derecha)
10. Aparecerá "Runner" → Click **Run**
11. Espera a que terminen todos los tests

---

## 📊 INTERPRETAR RESULTADOS (5 MINUTOS)

Después de ejecutar, verás:

### En Terminal:
```
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
```

### Significado:
- ✅ **Passed**: El test ejecutó correctamente
- ❌ **Failed**: El test falló (revisar error)
- ⏭️ **Skipped**: El test fue saltado

### En Reporte HTML:
- **Resumen:** Gráficos de éxito/fallo
- **Detalles:** Cada request/response
- **Tiempos:** Duración de cada test
- **Errores:** Detalles de fallos (si los hay)

---

## 🐛 SI ALGO FALLA

### Error 1: "Connection Refused"
```
Error: connect ECONNREFUSED 127.0.0.1:8080
```
**Solución:** Inicia Spring Boot en otra terminal
```bash
mvn spring-boot:run
# o en tu IDE: Run → Run TeranvetApplication.java
```

### Error 2: "401 Unauthorized"
```
Error: Expected 200, but got 401
```
**Solución:** El token expiró o no se generó. Ejecuta login primero:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","passwordHash":"admin123"}'
```

### Error 3: "500 Internal Server Error"
```
Error: Expected 201, but got 500
```
**Solución:** Hay un error en la API. Revisa los logs:
1. Abre la terminal donde corre Spring Boot
2. Busca líneas con "ERROR" o "Exception"
3. Copia el stack trace completo
4. Verifica en `GlobalExceptionHandler.java`

### Error 4: "No se encontró Postman_Collection.json"
```
Error: ENOENT: no such file or directory
```
**Solución:** Verifica que el archivo existe y estás en la carpeta correcta:
```bash
# Windows
cd C:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot
dir Postman_Collection.json

# Mac/Linux  
cd ~/Desktop/Veterinaria-Spring-Boot
ls -la Postman_Collection.json
```

### Error 5: "Newman no está instalado"
```
Error: 'newman' is not recognized
```
**Solución:** Instala Node.js primero, luego Newman:
```bash
# Descargar Node.js desde: https://nodejs.org/
# Luego:
npm install -g newman newman-reporter-htmlextra
```

---

## ✅ VALIDACIÓN MANUAL EN BD

Después de que los tests pasen, verifica que los datos llegaron a BD:

### Abrir MySQL Cliente:
```bash
# Opción 1: MySQL CLI
mysql -u root -p vet_teran

# Opción 2: Workbench
# Abrir MySQL Workbench → Connection "localhost"

# Opción 3: DBeaver
# Abrir DBeaver → Nueva conexión MySQL
```

### Ejecutar Queries:

```sql
-- 1. Verificar cliente creado
SELECT * FROM Cliente WHERE email = 'juan.perez@example.com';
-- Deberías ver: idCliente=5 (aproximadamente), nombre=Juan, etc.

-- 2. Verificar mascota
SELECT * FROM Mascota WHERE nombre = 'Rex';
-- Deberías ver: raza=Labrador, especie=perro

-- 3. Verificar atención
SELECT * FROM Atencion WHERE idMascota = (SELECT idMascota FROM Mascota WHERE nombre='Rex');
-- Deberías ver: estado=terminado

-- 4. Verificar factura
SELECT * FROM Factura WHERE serie = 'F001' AND numero = '0100';
-- Deberías ver: total, estado=pagado

-- 5. Verificar pago
SELECT * FROM Pago WHERE idFactura = (SELECT idFactura FROM Factura WHERE serie='F001' AND numero='0100');
-- Deberías ver: monto=105.50, metodo=efectivo

-- 6. Ver audit log (cambios registrados)
SELECT * FROM AuditLog ORDER BY fechaCambio DESC LIMIT 10;
```

---

## 📁 ARCHIVOS GENERADOS

Después de ejecutar tests, verás:

```
test-results/
├── results_20251112_100000.json    ← Datos crudos de tests
├── report_20251112_100000.html     ← Reporte visual
└── (más resultados si ejecutas varias veces)
```

---

## 📈 PRÓXIMOS PASOS DESPUÉS DE TESTS

### Si TODO pasó ✅:
1. Generar reporte final: `INTEGRATION_TEST_RESULTS.md`
2. Pasar a Prioridad 3: **Swagger/OpenAPI**
3. Luego Prioridad 4: **Dockerización**
4. Finalmente: **Reporte de Completación**

### Si algo falló ❌:
1. Revisar error en terminal
2. Verificar logs de Spring Boot
3. Consultar Troubleshooting arriba
4. Ejecutar nuevamente

---

## 🎯 CHECKLIST FINAL

- [ ] API Spring Boot corriendo en localhost:8080
- [ ] MySQL conectado y BD accessible
- [ ] Newman instalado (verifica con `newman --version`)
- [ ] Archivos Postman presentes en carpeta raíz
- [ ] Credenciales correctas (admin@example.com / admin123)
- [ ] Script ejecutado (`run_tests.ps1` o `run_tests.sh`)
- [ ] 9 tests pasaron ✅
- [ ] Reporte HTML generado
- [ ] Datos verificados en BD
- [ ] Documentación actualizada

---

## 📞 SOPORTE RÁPIDO

| Problema | Solución Rápida |
|----------|-----------------|
| API no responde | `mvn spring-boot:run` |
| Token expirado | Ejecutar login nuevamente |
| BD no conecta | Verificar MySQL corriendo |
| Newman no funciona | `npm install -g newman` |
| Permisos denegados | `chmod +x run_tests.sh` (Mac/Linux) |

---

**¿Necesitas ayuda?** Revisa los detalles en `INTEGRATION_TEST_GUIDE.md`

**Documento:** QUICK_START_TESTING.md  
**Versión:** 1.0  
**Duración Total:** 15-30 minutos
