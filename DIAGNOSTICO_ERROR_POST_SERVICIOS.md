# 🔴 Diagnóstico: Error en POST /servicios

**Fecha:** 2025-11-23  
**Endpoint Problemático:** `POST /servicios`  
**Estado:** 🔍 INVESTIGANDO

---

## 🚨 Problema Reportado

Al intentar crear un nuevo servicio con POST, el servidor retorna la lista de TODOS los servicios en lugar del servicio creado.

### Request Enviado

```http
POST http://localhost:8080/servicios
Content-Type: application/json

{
  "codigo": "SRV-011",
  "nombre": "Corte de Bigote",
  "descripcion": "Corte y limado de uñas para mascotas",
  "duracionEstimadaMin": 20,
  "precioBase": 15.00,
  "categoria": "otro"
}
```

### Respuesta Recibida (INCORRECTA)

```json
{
  "exito": true,
  "mensaje": "Servicios obtenidos exitosamente",  // ❌ Mensaje de GET
  "datos": [
    {
      "idServicio": 1,
      "codigo": "B001",
      "nombre": "Baño Básico (Perro Pequeño)",
      ...
    },
    ... // TODOS los servicios (10 en total)
  ],
  "error": null
}
```

**Problemas Detectados:**
1. ❌ Status code: 200 (debería ser 201)
2. ❌ Mensaje: "Servicios obtenidos exitosamente" (de Buscar, no de crear)
3. ❌ Datos: array de TODOS los servicios (no el servicio creado)
4. ❌ El nuevo servicio NO se creó en la BD

---

## 🔍 Análisis del Código

### 1. Controller: ServicioController.java

**Línea 15:**
```java
@RequestMapping("/servicios")  // ⚠️ ADVERTENCIA: Falta /api
```

**Comparación con otros controllers:**

| Controller | RequestMapping | ¿Correcto? |
|------------|----------------|------------|
| CitaController | `/api/citas` | ✅ |
| ClienteController | `/clientes` | ❌ Falta `/api` |
| ServicioController | `/servicios` | ❌ Falta `/api` |
| DashboardController | `/api/dashboard` | ✅ |

**Método POST (líneas 99-111):**
```java
@PostMapping
public ResponseEntity<ApiResponse<Servicio>> crear(@RequestBody Servicio servicio) {
    try {
        log.info("POST /servicios - Creando nuevo servicio");
        Servicio nuevoServicio = servicioService.crear(servicio);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.exitoso("Servicio creado exitosamente", nuevoServicio));
    } catch (Exception e) {
        log.error("Error al crear servicio", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error al crear servicio", e.getMessage()));
    }
}
```

✅ El código del POST se ve correcto.

---

### 2. Service: ServicioService.java

**Método crear (líneas 65-78):**
```java
public Servicio crear(Servicio servicio) {
    log.info("Creando nuevo servicio: {}", servicio.getNombre());
    
    if (servicioRepository.findByCodigo(servicio.getCodigo()).isPresent()) {
        throw new RuntimeException("El código de servicio ya existe");
    }
    
    servicio.setCreatedAt(LocalDateTime.now());
    servicio.setUpdatedAt(LocalDateTime.now());
    
    Servicio guardado = servicioRepository.save(servicio);
    log.info("Servicio creado con ID: {}", guardado.getIdServicio());
    return guardado;
}
```

✅ El código del Service también se ve correcto.

---

## 🔎 Posibles Causas

### Hipótesis 1: Problema con Postman (Más Probable)

**El request podría estar usando GET en lugar de POST.**

Verificar en Postman:
1. El dropdown de método debe decir **POST** (no GET)
2. El header `Content-Type: application/json` debe estar incluido
3. El body debe estar en formato **raw JSON**

---

### Hipótesis 2: Problema de Routing en Spring Boot

Si hay dos endpoints con el mismo path, Spring podría estar eligiendo el incorrecto.

**Verificación:**
```bash
# En logs de Spring Boot al iniciar, buscar:
Mapped "{[/servicios],methods=[GET]}" onto ...
Mapped "{[/servicios],methods=[POST]}" onto ...
```

Si solo aparece el GET, hay un problema de configuración.

---

### Hipótesis 3: Proxy o Load Balancer

Si hay un proxy o nginx entre Postman y el backend, podría estar convirtiendo POST a GET.

---

## ✅ Soluciones a Probar

### Solución 1: Verificar Configuración de Postman

1. **Método HTTP:**
   - Asegúrate que dice **POST** (no GET)

2. **Headers:**
   ```
   Content-Type: application/json
   Authorization: Bearer {{token}}
   ```

3. **Body:**
   - Selecciona **raw**
   - Dropdown: **JSON**
   - Pega el JSON del servicio

4. **URL:**
   ```
   http://localhost:8080/servicios
   ```
   (sin `/api` porque el controller usa `/servicios`)

---

### Solución 2: Probar con cURL

```bash
curl -X POST http://localhost:8080/servicios \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -d '{
    "codigo": "SRV-TEST",
    "nombre": "Servicio de Prueba",
    "descripcion": "Test",
    "duracionEstimadaMin": 30,
    "precioBase": 25.00,
    "categoria": "otro"
  }'
```

**Si cURL funciona pero Postman no:** El problema está en la configuración de Postman.

---

### Solución 3: Revisar Logs del Backend

Ejecutar el POST y revisar los logs:

**Esperado (si POST funciona):**
```
INFO ... - POST /servicios - Creando nuevo servicio
INFO ... - Creando nuevo servicio: Corte de Bigote
INFO ... - Servicio creado con ID: 11
```

**Lo que podría estar pasando (si llega como GET):**
```
INFO ... - GET /servicios - Obteniendo todos los servicios
INFO ... - Obteniendo todos los servicios
```

---

### Solución 4: Estandarizar rutas con `/api`

Aunque no es la causa directa, es buena práctica:

```java
// En ServicioController.java, línea 15:
@RequestMapping("/api/servicios")  // Agregar /api
```

Luego actualizar el URL en Postman:
```
http://localhost:8080/api/servicios
```

---

## 🧪 Prueba de Diagnóstico

### Paso 1: Verificar que el POST está mapeado

En los logs de inicio de Spring Boot, busca:

```
Mapped "{[/servicios],methods=[POST]}" onto ...ServicioController.crear(...)
Mapped "{[/servicios],methods=[GET]}" onto ...ServicioController.obtenerTodos()
```

Si solo aparece GET, el POST no está registrado.

---

### Paso 2: Probar con método diferente

Temporalmente, cambia el `@PostMapping` por `@PostMapping("/nuevo")`:

```java
@PostMapping("/nuevo")
public ResponseEntity<ApiResponse<Servicio>> crear(@RequestBody Servicio servicio) {
    ...
}
```

Luego prueba:
```
POST http://localhost:8080/servicios/nuevo
```

Si funciona, el problema era un conflicto de rutas.

---

### Paso 3: Verificar que NO hay interceptores

Revisar si hay algún `Filter` o `Interceptor` que esté cambiando el método HTTP.

---

## 📊 Comparación Expected vs Actual

| Aspecto | Esperado (POST) | Actual (Recibido) |
|---------|----------------|-------------------|
| **HTTP Status** | 201 CREATED | 200 OK |
| **Mensaje** | "Servicio creado exitosamente" | "Servicios obtenidos exitosamente" |
| **Datos** | Objeto del servicio creado | Array de TODOS los servicios |
| **Campo idServicio** | Presente con ID nuevo | Múltiples IDs (1-10) |
| **Logs backend** | "Creando nuevo servicio..." | "Obteniendo todos los servicios" |

---

## 🔧 Acción Inmediata Recomendada

1. **Revisar Postman:**
   - Verificar que el método es **POST**
   - Verificar que el body es **raw JSON**

2. **Revisar Logs:**
   - Ejecutar el request
   - Ver qué se loggea: ¿"POST /servicios" o "GET /servicios"?

3. **Probar con cURL:**
   - Si funciona con cURL, el problema es Postman

4. **Reportar hallazgos:**
   - ¿Qué dice el log del backend?
   - ¿El método en Postman está correcto?

---

## 💡 Información Adicional Necesaria

Para continuar el diagnóstico, necesito:

1. **Screenshots de Postman:**
   - Dropdown del método HTTP
   - Tab de Headers
   - Tab de Body

2. **Logs del backend:**
   - Al ejecutar el POST, ¿qué aparece?
   - Copia las líneas que empiezan con `INFO ... - POST` o `GET`

3. **Prueba con cURL:**
   - ¿Funciona el comando cURL que proporcioné?

---

**Actualizado:** 2025-11-23  
**Estado:** Esperando información adicional del usuario
