╔════════════════════════════════════════════════════════════════════════════════╗
║                                                                                ║
║                  🚀 COMPILACIÓN Y PRUEBA DE CAMBIOS                           ║
║                                                                                ║
║                      Guía paso a paso para verificar                           ║
║                     que todo funciona correctamente                            ║
║                                                                                ║
╚════════════════════════════════════════════════════════════════════════════════╝


📋 TABLA DE CONTENIDOS

1. PREREQUISITOS
2. COMPILACIÓN
3. EJECUCIÓN
4. PRUEBAS CON CURL
5. SOLUCIÓN DE PROBLEMAS


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 1️⃣ PREREQUISITOS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Verifica que tengas instalado:

✅ Java 8+ 
   $ java -version

✅ Maven 3.6+
   $ mvn -v

✅ MySQL 8.0+ con la base de datos vet_teran
   $ mysql -u root -p
   > USE vet_teran;
   > SHOW TABLES;

✅ Git (para clonar el proyecto si es necesario)
   $ git --version


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 2️⃣ COMPILACIÓN
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Paso 1: Navega al directorio del proyecto
─────────────────────────────────────────
$ cd c:\Users\dalon\OneDrive\Escritorio\Veterinaria-Spring-Boot


Paso 2: Limpia compilaciones previas
─────────────────────────────────────
$ mvn clean

Output esperado:
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ teranvet ---
[INFO] Deleting c:\...\target
[INFO] BUILD SUCCESS


Paso 3: Descarga dependencias e instala
────────────────────────────────────────
$ mvn install

Output esperado:
[INFO] Building jar: c:\...\target\teranvet-1.0.0.jar
[INFO] BUILD SUCCESS

⏱️  Esto puede tomar 2-5 minutos la primera vez


Paso 4: Compila únicamente (sin tests por ahora)
──────────────────────────────────────────────────
$ mvn compile

Output esperado:
[INFO] Building teranvet 1.0.0
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS


✅ Si ves "BUILD SUCCESS", la compilación fue exitosa
❌ Si ves errores de compilación, revisa el mensaje y corrige


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 3️⃣ EJECUCIÓN
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

OPCIÓN 1: Ejecutar desde Maven
──────────────────────────────
$ mvn spring-boot:run

Output esperado:
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |\__,_|/ / / / /
 =========|_|==============|___/=======

[INFO] Starting TeranvetApplication on YOUR-COMPUTER with PID 1234
[INFO] The following profiles are active: ...
[INFO] Tomcat started on port(s): 8080 (http)
[INFO] Started TeranvetApplication in X.XXX seconds


OPCIÓN 2: Ejecutar desde NetBeans (IDE)
────────────────────────────────────────
1. Abre el proyecto en NetBeans
2. Click derecho → Run Project
3. Espera a que compile y se inicie


OPCIÓN 3: Ejecutar JAR compilado
─────────────────────────────────
$ java -jar target/teranvet-1.0.0.jar

Output:
Similar al anterior, indicando que el servidor está en puerto 8080


✅ Verifica que la aplicación esté corriendo
$ curl http://localhost:8080/api/clientes
Deberías recibir una respuesta JSON


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 4️⃣ PRUEBAS CON CURL
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

CONFIGURACIÓN INICIAL

1. Abre una nueva terminal PowerShell
2. La aplicación debe estar ejecutándose en http://localhost:8080


PRUEBAS DE ENDPOINTS (Nuevos)

═══════════════════════════════════════════════════════════════════════════════
FACTURAS
═══════════════════════════════════════════════════════════════════════════════

1. Obtener todas las facturas
────────────────────────────
$ curl -X GET http://localhost:8080/api/facturas

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Facturas obtenidas exitosamente",
  "datos": [
    {
      "idFactura": 1,
      "serie": "F001",
      "numero": "0001",
      ...
    }
  ],
  "error": null
}


2. Obtener factura por ID
─────────────────────────
$ curl -X GET http://localhost:8080/api/facturas/1

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Factura obtenida exitosamente",
  "datos": { "idFactura": 1, ... },
  "error": null
}


3. Obtener facturas de un cliente
──────────────────────────────────
$ curl -X GET http://localhost:8080/api/facturas/cliente/1

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Facturas del cliente obtenidas",
  "datos": [ { ... }, { ... } ],
  "error": null
}


4. Crear nueva factura
──────────────────────
$ curl -X POST "http://localhost:8080/api/facturas?idAtencion=1&serie=F001&numero=0002&metodoPagoSugerido=efectivo"

Respuesta esperada (201 Created):
{
  "exito": true,
  "mensaje": "Factura creada exitosamente",
  "datos": "Factura registrada en la BD",
  "error": null
}


5. Recalcular totales
──────────────────────
$ curl -X POST http://localhost:8080/api/facturas/recalcular

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Totales recalculados",
  "datos": "Todos los totales han sido recalculados",
  "error": null
}


═══════════════════════════════════════════════════════════════════════════════
PAGOS
═══════════════════════════════════════════════════════════════════════════════

1. Obtener todos los pagos
──────────────────────────
$ curl -X GET http://localhost:8080/api/pagos

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Pagos obtenidos exitosamente",
  "datos": [ ... ],
  "error": null
}


2. Obtener pago por ID
──────────────────────
$ curl -X GET http://localhost:8080/api/pagos/1

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Pago obtenido exitosamente",
  "datos": { "idPago": 1, ... },
  "error": null
}


3. Obtener pagos de una factura
────────────────────────────────
$ curl -X GET http://localhost:8080/api/pagos/factura/1

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Pagos de factura obtenidos",
  "datos": [ ... ],
  "error": null
}


4. Obtener pagos confirmados
──────────────────────────────
$ curl -X GET http://localhost:8080/api/pagos/confirmados

Respuesta esperada:
{
  "exito": true,
  "mensaje": "Pagos confirmados obtenidos",
  "datos": [ ... ],
  "error": null
}


5. Registrar nuevo pago
───────────────────────
$ curl -X POST "http://localhost:8080/api/pagos?idFactura=1&monto=100.50&metodo=tarjeta&referencia=REF123"

Respuesta esperada (201 Created):
{
  "exito": true,
  "mensaje": "Pago registrado exitosamente",
  "datos": "Pago confirmado",
  "error": null
}


════════════════════════════════════════════════════════════════════════════════
ENDPOINTS EXISTENTES (Verificar que siguen funcionando)
════════════════════════════════════════════════════════════════════════════════

Clientes:
$ curl -X GET http://localhost:8080/api/clientes
✅ Debe retornar lista de clientes

Mascotas:
$ curl -X GET http://localhost:8080/api/mascotas
✅ Debe retornar lista de mascotas

Citas:
$ curl -X GET http://localhost:8080/api/citas
✅ Debe retornar lista de citas

Servicios:
$ curl -X GET http://localhost:8080/api/servicios
✅ Debe retornar lista de servicios

Atenciones:
$ curl -X GET http://localhost:8080/api/atenciones
✅ Debe retornar lista de atenciones


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 5️⃣ SOLUCIÓN DE PROBLEMAS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

PROBLEMA 1: "Connection refused" en puerto 8080
───────────────────────────────────────────────
❌ Error: curl: (7) Failed to connect to localhost port 8080
✅ Solución:
   - Verifica que la app está ejecutándose: ps aux | grep java
   - Espera 10 segundos después de iniciar
   - Revisa logs de Spring Boot para errores


PROBLEMA 2: "Could not get a resource from the pool"
──────────────────────────────────────────────────────
❌ Error: Could not get a resource from the pool
✅ Solución:
   - Verifica que MySQL está corriendo: mysql -u root -p
   - Verifica que la BD vet_teran existe: SHOW DATABASES;
   - Revisa credenciales en application.properties


PROBLEMA 3: "SP does not exist"
────────────────────────────────
❌ Error: Stored procedure sp_CrearFactura does not exist
✅ Solución:
   - Ejecuta el script SQL: mysql -u root -p vet_teran < vet_teran_mysql.sql
   - Verifica que los SPs fueron creados: SHOW PROCEDURE STATUS;


PROBLEMA 4: "BUILD FAILURE" en Maven
──────────────────────────────────────
❌ Error: [ERROR] BUILD FAILURE
✅ Solución:
   - Limpia: mvn clean
   - Borra .m2/repository: rm -r ~/.m2/repository
   - Intenta de nuevo: mvn install


PROBLEMA 5: "404 Not Found" en endpoint
─────────────────────────────────────────
❌ Error: {"exito":false,"mensaje":"404 Not Found","error":"..."}
✅ Solución:
   - Verifica que el ID existe en la BD
   - Verifica que la ruta es correcta (sensible a mayúsculas)
   - Revisa los logs para detalles


PROBLEMA 6: "ValidationException" en POST
───────────────────────────────────────────
❌ Error: {"exito":false,"mensaje":"Validación fallida",...}
✅ Solución:
   - Verifica que pasas todos los @RequestParam requeridos
   - Verifica tipos de dato (Integer vs String)
   - Revisa mensaje de error para detalles específicos


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 📊 VERIFICACIÓN RÁPIDA
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Script de verificación (ejecuta en PowerShell):

# Verifica que la app está corriendo
$response = curl -s http://localhost:8080/api/clientes | ConvertFrom-Json
if ($response.exito) {
    Write-Host "✅ App está funcionando correctamente" -ForegroundColor Green
    Write-Host "✅ Total clientes: $($response.datos.Count)" -ForegroundColor Green
} else {
    Write-Host "❌ Error: $($response.error)" -ForegroundColor Red
}

# Verifica facturas
$facturas = curl -s http://localhost:8080/api/facturas | ConvertFrom-Json
Write-Host "✅ Facturas disponibles: $($facturas.datos.Count)" -ForegroundColor Green

# Verifica pagos
$pagos = curl -s http://localhost:8080/api/pagos | ConvertFrom-Json
Write-Host "✅ Pagos disponibles: $($pagos.datos.Count)" -ForegroundColor Green


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 📝 LOGS Y DEBUGGING
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Los logs se encuentran en:

1. Consola de ejecución:
   - INFO: Operaciones exitosas
   - WARN: Validaciones fallidas
   - ERROR: Excepciones

2. Archivo (si lo configuraste):
   - logs/application.log

3. Buscar errores específicos:
   - [ERROR] SearchEngineException
   - [ERROR] DataAccessException
   - [ERROR] SQLSyntaxErrorException


═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════

Comandos útiles para debugging:

# Ver procesos Java
ps aux | grep java

# Ver si MySQL está corriendo
Get-Service MySQL80

# Conectar a MySQL y ver SPs
mysql -u root -p
> USE vet_teran;
> SHOW PROCEDURE STATUS;
> CALL sp_ObtenerServicios();

# Ver logs en tiempo real (Linux/Mac)
tail -f logs/application.log

# Buscar errores en logs
grep ERROR logs/application.log


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 ✅ CHECKLIST FINAL
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Antes de considerar la build como exitosa:

☐ mvn clean install → BUILD SUCCESS
☐ mvn spring-boot:run → App inicia sin errores
☐ GET /api/clientes → Retorna lista
☐ GET /api/facturas → Retorna lista (puede estar vacía)
☐ GET /api/pagos → Retorna lista (puede estar vacía)
☐ POST /api/facturas → Crea factura exitosamente
☐ POST /api/pagos → Registra pago exitosamente
☐ Todos los endpoints retornan ApiResponse con formato correcto
☐ No hay excepciones sin capturar en consola
☐ Logs muestran operaciones esperadas


╔════════════════════════════════════════════════════════════════════════════════╗
║                                                                                ║
║                   🎉 ¡LISTO PARA PRUEBAS! 🎉                                ║
║                                                                                ║
║                         Sigue estas instrucciones para                         ║
║                      verificar que todo está funcionando                       ║
║                                                                                ║
╚════════════════════════════════════════════════════════════════════════════════╝
