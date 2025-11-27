# 📘 Guía de Pruebas Postman: Módulo Atenciones

**Proyecto:** Veterinaria SpringBoot  
**Módulo:** Atenciones y Detalles de Servicio  
**Base URL:** `http://localhost:8080`  
**Fecha:** 2025-11-24

---

## 📑 Índice

1. [Configuración Inicial](#configuración-inicial)
2. [Datos de Prueba](#datos-de-prueba)
3. [Endpoints de Atenciones](#endpoints-de-atenciones)
4. [Endpoints de Detalles de Servicio](#endpoints-de-detalles-de-servicio)
5. [Flujo Completo de Prueba](#flujo-completo-de-prueba)
6. [Casos de Error](#casos-de-error)
7. [Checklist de Validación](#checklist-de-validación)

---

## 🔧 Configuración Inicial

### 1. Variables de Entorno en Postman

```json
{
  "baseUrl": "http://localhost:8080",
  "token": "{{tu_jwt_token}}",
  "idCita": "15",
  "idMascota": "1",
  "idCliente": "1",
  "idGroomer": "1",
  "idSucursal": "1",
  "idAtencion": "",
  "idDetalle": ""
}
```

### 2. Obtener Token de Autenticación

```http
POST {{baseUrl}}/api/auth/login
Content-Type: application/json

{
  "usuario": "admin",
  "password": "admin123"
}
```

**Guardar el token** en la variable `{{token}}`.

### 3. Headers Globales

Configurar en Postman Collection:

```
Authorization: Bearer {{token}}
```

---

## 📋 Datos de Prueba

### Estructura de Atención

```json
{
  "idAtencion": 10,
  "cita": { ... },
  "mascota": { ... },
  "cliente": { ... },
  "groomer": { ... },
  "sucursal": { ... },
  "estado": "en_espera",
  "turnoNum": 1,
  "prioridad": 1,
  "tiempoEstimadoInicio": "2025-11-26T10:00:00",
  "tiempoEstimadoFin": "2025-11-26T11:30:00",
  "tiempoRealInicio": null,
  "tiempoRealFin": null,
  "observaciones": null,
  "createdAt": "2025-11-26T09:55:00",
  "updatedAt": "2025-11-26T09:55:00"
}
```

### Estados Válidos

```
en_espera → en_servicio → terminado
           ↓
         pausado
```

### Prioridades

```
1 = Urgente (alta)
2 = Alta
3 = Normal
4 = Baja
5 = Muy Baja
0 = Walk-in
```

---

## 🔵 Endpoints de Atenciones

### PRUEBA 1: Obtener Todas las Atenciones

**Método:** GET  
**URL:** `{{baseUrl}}/api/atenciones`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Atenciones obtenidas exitosamente",
  "datos": [
    {
      "idAtencion": 10,
      "mascota": {
        "idMascota": 1,
        "nombre": "Max",
        "especie": "perro",
        "raza": "Golden Retriever"
      },
      "cliente": {
        "idCliente": 1,
        "nombre": "Juan",
        "apellido": "Pérez"
      },
      "groomer": {
        "idGroomer": 1,
        "nombre": "María González"
      },
      "estado": "en_espera",
      "turnoNum": 1,
      "prioridad": 1
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] `exito` es `true`
- [ ] `datos` es un array
- [ ] Cada atención tiene `idAtencion`, `mascota`, `cliente`, `groomer`, `estado`
- [ ] Los objetos anidados tienen información completa

---

### PRUEBA 2: Obtener Atención por ID

**Método:** GET  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Variables necesarias:**
- `idAtencion`: ID de una atención existente (ej: 10)

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Atención obtenida exitosamente",
  "datos": {
    "idAtencion": 10,
    "cita": {
      "idCita": 15,
      "mascota": { ... },
      "cliente": { ... },
      "sucursal": { ... },
      "servicio": { ... },
      "fechaProgramada": "2025-11-26T10:00:00",
      "modalidad": "presencial",
      "estado": "atendido"
    },
    "mascota": { ... },
    "cliente": { ... },
    "groomer": { ... },
    "sucursal": { ... },
    "estado": "en_espera",
    "turnoNum": 1,
    "tiempoEstimadoInicio": "2025-11-26T10:00:00",
    "tiempoEstimadoFin": "2025-11-26T11:30:00",
    "tiempoRealInicio": null,
    "tiempoRealFin": null,
    "prioridad": 1,
    "observaciones": null
  },
  "error": null
}
```

**⚠️ NOTA:** La respuesta es muy verbosa con información duplicada y anidada.

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] Retorna objeto completo de atención
- [ ] Contiene `cita`, `mascota`, `cliente`, `groomer`, `sucursal` completos
- [ ] `tiempoRealInicio` y `tiempoRealFin` son `null` si no ha iniciado/terminado

---

### PRUEBA 3: Obtener Cola de Sucursal

**Método:** GET  
**URL:** `{{baseUrl}}/api/atenciones/cola/{{idSucursal}}`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Variables necesarias:**
- `idSucursal`: ID de la sucursal (ej: 1)

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Cola obtenida exitosamente",
  "datos": [
    {
      "idAtencion": 20,
      "mascota": { "nombre": "Max" },
      "cliente": { "nombre": "Juan", "apellido": "Pérez" },
      "groomer": { "nombre": "María González" },
      "estado": "en_espera",
      "turnoNum": 1,
      "prioridad": 1,
      "tiempoEstimadoInicio": "2025-11-26T10:00:00"
    },
    {
      "idAtencion": 21,
      "estado": "en_servicio",
      "turnoNum": 2,
      "tiempoRealInicio": "2025-11-26T10:30:00"
    }
  ],
  "error": null
}
```

**Uso:** Vista Kanban con columnas: En Espera, En Servicio, Terminado

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] Retorna atenciones activas (no terminadas)
- [ ] Incluye información de mascota, cliente, groomer
- [ ] Ordenadas por turno o prioridad

---

### PRUEBA 4: Obtener Atenciones de un Cliente

**Método:** GET  
**URL:** `{{baseUrl}}/api/atenciones/cliente/{{idCliente}}`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Variables necesarias:**
- `idCliente`: ID del cliente (ej: 1)

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Atenciones del cliente obtenidas",
  "datos": [
    {
      "idAtencion": 5,
      "mascota": { "nombre": "Max" },
      "estado": "terminado",
      "tiempoRealInicio": "2025-11-20T10:00:00",
      "tiempoRealFin": "2025-11-20T11:15:00"
    }
  ],
  "error": null
}
```

**Uso:** Historial de atenciones en perfil del cliente

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] Retorna solo atenciones de ese cliente
- [ ] Incluye atenciones de todas las mascotas del cliente

---

### PRUEBA 5: Crear Atención desde Cita

**Método:** POST  
**URL:** `{{baseUrl}}/api/atenciones/desde-cita`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Body Type:** `x-www-form-urlencoded` ⚠️ **NO JSON**

**Body:**
```
idCita=15
&idGroomer=1
&idSucursal=1
&turnoNum=1
&tiempoEstimadoInicio=2025-11-26T10:00:00
&tiempoEstimadoFin=2025-11-26T11:30:00
&prioridad=1
```

**En Postman:**
1. Selecciona Body → `x-www-form-urlencoded`
2. Agrega cada parámetro como key-value:

| Key | Value |
|-----|-------|
| idCita | 15 |
| idGroomer | 1 |
| idSucursal | 1 |
| turnoNum | 1 |
| tiempoEstimadoInicio | 2025-11-26T10:00:00 |
| tiempoEstimadoFin | 2025-11-26T11:30:00 |
| prioridad | 1 |

**Respuesta Esperada:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Atención creada exitosamente desde la cita",
  "datos": null,
  "error": null
}
```

**Efecto en Backend:**
- ✅ Crea atención con estado `en_espera`
- ✅ Cambia estado de la cita a `atendido`

**✅ Validaciones:**
- [ ] Status code es 201
- [ ] `exito` es `true`
- [ ] Verificar en BD que la atención se creó
- [ ] Verificar que estado de cita cambió a `atendido`

**Verificación SQL:**
```sql
-- Ver atención creada
SELECT * FROM atencion ORDER BY id_atencion DESC LIMIT 1;

-- Verificar cambio de estado de cita
SELECT id_cita, estado FROM cita WHERE id_cita = 15;
```

---

### PRUEBA 6: Crear Atención Walk-In

**Método:** POST  
**URL:** `{{baseUrl}}/api/atenciones/walk-in`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Body Type:** `x-www-form-urlencoded` ⚠️ **NO JSON**

**Body:**
```
idMascota=1
&idCliente=1
&idGroomer=1
&idSucursal=1
&turnoNum=2
&tiempoEstimadoInicio=2025-11-26T12:00:00
&tiempoEstimadoFin=2025-11-26T13:00:00
&prioridad=0
&observaciones=Cliente walk-in sin cita previa
```

**En Postman:**

| Key | Value |
|-----|-------|
| idMascota | 1 |
| idCliente | 1 |
| idGroomer | 1 |
| idSucursal | 1 |
| turnoNum | 2 |
| tiempoEstimadoInicio | 2025-11-26T12:00:00 |
| tiempoEstimadoFin | 2025-11-26T13:00:00 |
| prioridad | 0 |
| observaciones | Cliente walk-in sin cita previa |

**Respuesta Esperada:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Atención walk-in creada exitosamente",
  "datos": null,
  "error": null
}
```

**Uso:** Cliente llega sin cita programada

**✅ Validaciones:**
- [ ] Status code es 201
- [ ] Atención se crea sin `idCita`
- [ ] Observaciones se guardan correctamente

---

### PRUEBA 7: Cambiar Estado de Atención

**Método:** PUT  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/estado?nuevoEstado=en_servicio`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Query Parameters:**
- `nuevoEstado`: `en_servicio` | `pausado` | `terminado`

**Ejemplo:** Iniciar servicio
```
PUT {{baseUrl}}/api/atenciones/10/estado?nuevoEstado=en_servicio
```

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Estado actualizado exitosamente",
  "datos": {
    "idAtencion": 10,
    "estado": "en_servicio",
    "tiempoRealInicio": "2025-11-26T10:05:23",
    ...
  },
  "error": null
}
```

**Efecto en Backend:**
- Al cambiar a `en_servicio`: Establece `tiempoRealInicio`

**Transiciones Válidas:**
```
en_espera → en_servicio ✅
en_servicio → pausado ✅
pausado → en_servicio ✅
en_servicio → terminado ✅ (mejor usar /terminar)
```

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] `estado` cambió correctamente
- [ ] Si es `en_servicio`, `tiempoRealInicio` tiene valor
- [ ] No se puede cambiar si ya está `terminado`

---

### PRUEBA 8: Terminar Atención

**Método:** PUT  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/terminar`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Ejemplo:**
```
PUT {{baseUrl}}/api/atenciones/10/terminar
```

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Atención terminada exitosamente",
  "datos": {
    "idAtencion": 10,
    "estado": "terminado",
    "tiempoRealInicio": "2025-11-26T10:05:00",
    "tiempoRealFin": "2025-11-26T11:25:00",
    ...
  },
  "error": null
}
```

**Efecto en Backend:**
- ✅ Cambia estado a `terminado`
- ✅ Establece `tiempoRealFin` con hora actual
- ✅ Calcula duración real

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] `estado` = `"terminado"`
- [ ] `tiempoRealFin` tiene valor
- [ ] No se puede volver a terminar (error si se intenta)

---

## 🟢 Endpoints de Detalles de Servicio

### PRUEBA 9: Obtener Detalles de una Atención

**Método:** GET  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/detalles`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Ejemplo:**
```
GET {{baseUrl}}/api/atenciones/10/detalles
```

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Detalles obtenidos correctamente",
  "datos": [
    {
      "idDetalle": 1,
      "atencion": { "idAtencion": 10 },
      "servicio": {
        "idServicio": 1,
        "codigo": "B001",
        "nombre": "Baño Básico",
        "precioBase": 35.00,
        "categoria": "baño"
      },
      "cantidad": 1,
      "precioUnitario": 35.00,
      "subtotal": 35.00,
      "observaciones": "Baño completo con champú especial",
      "createdAt": "2025-11-26T11:00:00"
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] `datos` es un array
- [ ] Cada detalle tiene `servicio` completo
- [ ] `subtotal` = `cantidad` × `precioUnitario`

---

### PRUEBA 10: Agregar Detalle de Servicio

**Método:** POST  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/detalles`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**⚠️ IMPORTANTE:** Este endpoint usa **JSON**, NO form-urlencoded

**Body (JSON):**
```json
{
  "servicio": {
    "idServicio": 1
  },
  "cantidad": 1,
  "precioUnitario": 35.00,
  "subtotal": 35.00,
  "observaciones": "Baño completo con champú especial"
}
```

**Ejemplo:**
```http
POST {{baseUrl}}/api/atenciones/10/detalles
Content-Type: application/json

{
  "servicio": {
    "idServicio": 1
  },
  "cantidad": 1,
  "precioUnitario": 35.00,
  "subtotal": 35.00,
  "observaciones": "Baño completo"
}
```

**Respuesta Esperada:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Detalle creado correctamente",
  "datos": {
    "idDetalle": 5,
    "atencion": { "idAtencion": 10 },
    "servicio": {
      "idServicio": 1,
      "codigo": "B001",
      "nombre": "Baño Básico",
      "precioBase": 35.00
    },
    "cantidad": 1,
    "precioUnitario": 35.00,
    "subtotal": 35.00,
    "observaciones": "Baño completo",
    "createdAt": "2025-11-26T11:26:00"
  },
  "error": null
}
```

**⚠️ CRÍTICO:** Sin agregar detalles, la factura tendrá total S/ 0.00

**✅ Validaciones:**
- [ ] Status code es 201
- [ ] Se crea el detalle correctamente
- [ ] `servicio` se retorna completo
- [ ] `subtotal` es correcto

**Guardar `idDetalle`** en variable para pruebas siguientes.

---

### PRUEBA 11: Agregar Segundo Servicio

**Método:** POST  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/detalles`

**Body (JSON):**
```json
{
  "servicio": {
    "idServicio": 8
  },
  "cantidad": 1,
  "precioUnitario": 15.00,
  "subtotal": 15.00,
  "observaciones": "Corte y limado de uñas"
}
```

**Respuesta Esperada:** HTTP 201 CREATED

**Verificar:** Ahora hay 2 detalles en la atención

---

### PRUEBA 12: Obtener Subtotal de Atención

**Método:** GET  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/detalles/subtotal`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Ejemplo:**
```
GET {{baseUrl}}/api/atenciones/10/detalles/subtotal
```

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Subtotal calculado correctamente",
  "datos": 50.00,
  "error": null
}
```

**Cálculo:**
```
Baño Básico: S/ 35.00
Corte de Uñas: S/ 15.00
------------------------
Subtotal: S/ 50.00
```

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] `datos` es un número (BigDecimal)
- [ ] Subtotal = suma de todos los `subtotal` de detalles

---

### PRUEBA 13: Actualizar Detalle de Servicio

**Método:** PUT  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/detalles/{{idDetalle}}`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "servicio": {
    "idServicio": 1
  },
  "cantidad": 2,
  "precioUnitario": 35.00,
  "subtotal": 70.00,
  "observaciones": "Baño doble - mascota sucia"
}
```

**Ejemplo:**
```http
PUT {{baseUrl}}/api/atenciones/10/detalles/5
Content-Type: application/json

{
  "cantidad": 2,
  "subtotal": 70.00,
  "observaciones": "Baño doble"
}
```

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Detalle actualizado correctamente",
  "datos": {
    "idDetalle": 5,
    "cantidad": 2,
    "subtotal": 70.00,
    ...
  },
  "error": null
}
```

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] Detalle se actualizó correctamente
- [ ] Subtotal de atención se recalcula

---

### PRUEBA 14: Eliminar Detalle de Servicio

**Método:** DELETE  
**URL:** `{{baseUrl}}/api/atenciones/{{idAtencion}}/detalles/{{idDetalle}}`

**Headers:**
```
Authorization: Bearer {{token}}
```

**Ejemplo:**
```
DELETE {{baseUrl}}/api/atenciones/10/detalles/5
```

**Respuesta Esperada:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Detalle eliminado correctamente",
  "datos": null,
  "error": null
}
```

**✅ Validaciones:**
- [ ] Status code es 200
- [ ] Detalle ya no aparece en GET /detalles
- [ ] Subtotal de atención se recalcula

---

## 🎯 Flujo Completo de Prueba

### Escenario: Cliente con Cita Llega a Atención

**Secuencia de Pruebas:**

```
1. POST /api/auth/login
   → Obtener token

2. GET /api/citas/15
   → Verificar que cita existe y está "confirmada"

3. POST /api/atenciones/desde-cita
   → Crear atención (estado: en_espera)
   → Guardar ID de atención

4. GET /api/atenciones/cola/1
   → Verificar que atención aparece en cola

5. PUT /api/atenciones/{id}/estado?nuevoEstado=en_servicio
   → Iniciar servicio

6. POST /api/atenciones/{id}/detalles
   → Agregar servicio 1 (Baño)

7. POST /api/atenciones/{id}/detalles
   → Agregar servicio 2 (Corte de uñas)

8. GET /api/atenciones/{id}/detalles/subtotal
   → Verificar total (S/ 50.00)

9. PUT /api/atenciones/{id}/terminar
   → Terminar atención

10. POST /api/facturas
    → Generar factura
    → Verificar que total no es 0.00

11. POST /api/pagos
    → Registrar pago
```

**Tiempo estimado:** 10-15 minutos

---

## ❌ Casos de Error

### ERROR 1: Crear Atención con Cita Inexistente

**Request:**
```
POST {{baseUrl}}/api/atenciones/desde-cita
Body (form-urlencoded):
idCita=99999
&...
```

**Error Esperado:** HTTP 400
```json
{
  "exito": false,
  "mensaje": "Error al crear atención",
  "datos": null,
  "error": "Cita no encontrada"
}
```

---

### ERROR 2: Obtener Atención Inexistente

**Request:**
```
GET {{baseUrl}}/api/atenciones/99999
```

**Error Esperado:** HTTP 404
```json
{
  "exito": false,
  "mensaje": "Atención no encontrada",
  "datos": null,
  "error": "Mensaje de error"
}
```

---

### ERROR 3: Terminar Atención Ya Terminada

**Request:**
```
PUT {{baseUrl}}/api/atenciones/10/terminar
```
(Ejecutar dos veces)

**Error Esperado:** HTTP 400
```json
{
  "exito": false,
  "mensaje": "Error al terminar atención",
  "datos": null,
  "error": "La atención ya está terminada"
}
```

---

### ERROR 4: Agregar Detalle sin Token

**Request:**
```
POST {{baseUrl}}/api/atenciones/10/detalles
(Sin Header Authorization)
```

**Error Esperado:** HTTP 401
```json
{
  "timestamp": "2025-11-26T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/atenciones/10/detalles"
}
```

---

### ERROR 5: Formato de Fecha Inválido

**Request:**
```
POST {{baseUrl}}/api/atenciones/desde-cita
Body:
tiempoEstimadoInicio=26/11/2025 10:00
```

**Error Esperado:** HTTP 400

**✅ Correcto:**
```
tiempoEstimadoInicio=2025-11-26T10:00:00
```

---

## ✅ Checklist de Validación

### Setup y Autenticación
- [ ] Token JWT obtenido correctamente
- [ ] Variables de Postman configuradas
- [ ] Headers de autorización en colección

### Endpoints de Atenciones
- [ ] GET /api/atenciones (listar todas)
- [ ] GET /api/atenciones/{id} (obtener una)
- [ ] GET /api/atenciones/cola/{sucursalId} (cola activa)
- [ ] GET /api/atenciones/cliente/{idCliente} (historial)
- [ ] POST /api/atenciones/desde-cita (crear desde cita)
- [ ] POST /api/atenciones/walk-in (crear walk-in)
- [ ] PUT /api/atenciones/{id}/estado (cambiar estado)
- [ ] PUT /api/atenciones/{id}/terminar (terminar)

### Endpoints de Detalles
- [ ] GET /api/atenciones/{id}/detalles (listar)
- [ ] POST /api/atenciones/{id}/detalles (agregar)
- [ ] PUT /api/atenciones/{id}/detalles/{idDetalle} (actualizar)
- [ ] DELETE /api/atenciones/{id}/detalles/{idDetalle} (eliminar)
- [ ] GET /api/atenciones/{id}/detalles/subtotal (calcular)

### Validaciones de Negocio
- [ ] Al crear atención desde cita, estado de cita cambia a "atendido"
- [ ] Al iniciar servicio, se establece `tiempoRealInicio`
- [ ] Al terminar, se establece `tiempoRealFin`
- [ ] No se puede terminar una atención ya terminada
- [ ] Subtotal se calcula correctamente
- [ ] Sin detalles, subtotal es 0.00

### Flujo End-to-End
- [ ] Flujo completo: cita → atención → servicio → detalles → terminar
- [ ] Estados transicionan correctamente
- [ ] Fechas y tiempos se registran bien
- [ ] Factura se genera con total correcto

### Casos de Error
- [ ] Error 401 sin token
- [ ] Error 404 con ID inexistente
- [ ] Error 400 con datos inválidos
- [ ] Error 400 con formato de fecha incorrecto

---

## 📊 Tabla Resumen de Endpoints

| # | Método | Endpoint | Content-Type | Descripción |
|---|--------|----------|--------------|-------------|
| 1 | GET | `/api/atenciones` | - | Listar todas |
| 2 | GET | `/api/atenciones/{id}` | - | Obtener una |
| 3 | GET | `/api/atenciones/cola/{sucursalId}` | - | Cola activa |
| 4 | GET | `/api/atenciones/cliente/{idCliente}` | - | Historial |
| 5 | POST | `/api/atenciones/desde-cita` | form-urlencoded | Crear desde cita |
| 6 | POST | `/api/atenciones/walk-in` | form-urlencoded | Crear walk-in |
| 7 | PUT | `/api/atenciones/{id}/estado` | - | Cambiar estado |
| 8 | PUT | `/api/atenciones/{id}/terminar` | - | Terminar |
| 9 | GET | `/api/atenciones/{id}/detalles` | - | Listar detalles |
| 10 | POST | `/api/atenciones/{id}/detalles` | JSON | Agregar detalle |
| 11 | PUT | `/api/atenciones/{id}/detalles/{idDetalle}` | JSON | Actualizar detalle |
| 12 | DELETE | `/api/atenciones/{id}/detalles/{idDetalle}` | - | Eliminar detalle |
| 13 | GET | `/api/atenciones/{id}/detalles/subtotal` | - | Calcular subtotal |

---

## 💡 Tips para Pruebas Eficientes

### 1. Queries SQL Útiles

```sql
-- Ver última atención creada
SELECT * FROM atencion ORDER BY id_atencion DESC LIMIT 1;

-- Ver atenciones activas
SELECT id_atencion, estado, turno_num FROM atencion 
WHERE estado != 'terminado';

-- Ver detalles de una atención
SELECT * FROM detalle_servicio WHERE id_atencion = 10;

-- Calcular subtotal manualmente
SELECT SUM(subtotal) FROM detalle_servicio WHERE id_atencion = 10;
```

### 2. Pre-request Scripts en Postman

```javascript
// Generar fecha/hora actual + 1 hora
const now = new Date();
const futureDate = new Date(now.getTime() + 60*60*1000);
pm.environment.set("tiempoInicio", now.toISOString().slice(0,19));
pm.environment.set("tiempoFin", futureDate.toISOString().slice(0,19));
```

### 3. Tests Automatizados

```javascript
// En tab "Tests" de cada request
pm.test("Status code es 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Respuesta es exitosa", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.exito).to.eql(true);
});

pm.test("Datos no son null", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.datos).to.not.be.null;
});
```

---

## 🎓 Notas Importantes

### 1. Form-urlencoded vs JSON

**Form-urlencoded:**
- POST `/api/atenciones/desde-cita`
- POST `/api/atenciones/walk-in`

**JSON:**
- POST `/api/atenciones/{id}/detalles`
- PUT `/api/atenciones/{id}/detalles/{idDetalle}`

### 2. Formato de Fechas

**Siempre ISO 8601:**
```
2025-11-26T10:00:00
```

### 3. Orden del Flujo

```
✅ CORRECTO:
1. Terminar atención
2. Agregar detalles
3. Generar factura

❌ INCORRECTO: Agregar detalles antes de terminar
```

### 4. Respuestas Verbosas

Los GET retornan MUCHA información anidada. Es normal y contiene el contexto completo.

---

**Preparado por:** Backend Team  
**Fecha:** 2025-11-24  
**Versión:** 1.0  
**Para usar con:** Postman, Insomnia o cualquier cliente HTTP
