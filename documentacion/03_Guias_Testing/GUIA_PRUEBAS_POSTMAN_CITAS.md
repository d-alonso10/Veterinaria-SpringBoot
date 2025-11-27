# 📋 Guía de Pruebas Manuales en Postman - Módulo Citas

**Proyecto:** Veterinaria SpringBoot  
**Módulo:** Gestión de Citas  
**Base URL:** `http://localhost:8080`  
**Fecha:** 2023-11-23

---

## 📑 Índice

1. [Configuración Inicial](#configuración-inicial)
2. [Datos de Prueba](#datos-de-prueba)
3. [Pruebas de Endpoints](#pruebas-de-endpoints)
   - [1. Obtener Todas las Citas](#1-obtener-todas-las-citas)
   - [2. Obtener Cita por ID](#2-obtener-cita-por-id)
   - [3. Crear Nueva Cita](#3-crear-nueva-cita)
   - [4. Obtener Citas de un Cliente](#4-obtener-citas-de-un-cliente)
   - [5. Obtener Próximas Citas de Cliente](#5-obtener-próximas-citas-de-cliente)
   - [6. Reprogramar Cita](#6-reprogramar-cita)
   - [7. Cancelar Cita](#7-cancelar-cita)
   - [8. Confirmar Asistencia](#8-confirmar-asistencia)
   - [9. Marcar Como No-Show](#9-marcar-como-no-show)
4. [Casos de Error](#casos-de-error)
5. [Checklist de Validación](#checklist-de-validación)

---

## 🔧 Configuración Inicial

### 1. Variables de Entorno en Postman

Crea un Environment en Postman con estas variables:

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `baseUrl` | `http://localhost:8080` | URL base del backend |
| `token` | `<tu_jwt_token>` | Token JWT de autenticación |

### 2. Autenticación

**Todos los endpoints requieren autenticación JWT.**

#### Obtener Token

```http
POST {{baseUrl}}/api/auth/login
Content-Type: application/json

{
  "usuario": "admin",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "exito": true,
  "mensaje": "Login exitoso",
  "datos": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "usuario": "admin",
    "rol": "ADMIN"
  }
}
```

Copia el token y guárdalo en la variable `{{token}}` del Environment.

### 3. Headers Comunes

Para **TODOS** los requests, incluye estos headers:

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

## 📊 Datos de Prueba

Antes de empezar, asegúrate de tener estos datos en tu base de datos:

### Clientes
```sql
SELECT id_cliente, nombre, apellido FROM cliente LIMIT 3;
-- Ejemplo: IDs 1, 2, 3
```

### Mascotas
```sql
SELECT id_mascota, nombre, id_cliente FROM mascota LIMIT 3;
-- Ejemplo: IDs 1, 2, 3
```

### Sucursales
```sql
SELECT id_sucursal, nombre FROM sucursal LIMIT 2;
-- Ejemplo: IDs 1, 2
```

### Servicios
```sql
SELECT id_servicio, nombre FROM servicio LIMIT 3;
-- Ejemplo: IDs 1, 2, 3
```

**💡 Tip:** Guarda estos IDs para usarlos en las pruebas.

---

## 🧪 Pruebas de Endpoints

### 1. Obtener Todas las Citas

**Objetivo:** Listar todas las citas registradas en el sistema

**Endpoint:**
```http
GET {{baseUrl}}/api/citas
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Citas obtenidas exitosamente",
  "datos": [
    {
      "idCita": 1,
      "idMascota": 1,
      "nombreMascota": "Max",
      "idCliente": 1,
      "nombreCliente": "Juan Pérez",
      "idSucursal": 1,
      "nombreSucursal": "Sucursal Centro",
      "idServicio": 1,
      "nombreServicio": "Baño y Corte",
      "fechaProgramada": "2025-11-25T10:00:00",
      "modalidad": "presencial",
      "estado": "reservada",
      "notas": "Cliente solicita usar champú especial"
    },
    {
      "idCita": 2,
      "idMascota": 2,
      "nombreMascota": "Luna",
      "idCliente": 2,
      "nombreCliente": "María García",
      "idSucursal": 1,
      "nombreSucursal": "Sucursal Centro",
      "idServicio": 2,
      "nombreServicio": "Consulta Veterinaria",
      "fechaProgramada": "2025-11-26T15:30:00",
      "modalidad": "virtual",
      "estado": "confirmada",
      "notas": null
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` es un array (puede estar vacío)
- Cada cita tiene todos los campos necesarios

---

### 2. Obtener Cita por ID

**Objetivo:** Consultar una cita específica por su ID

**Endpoint:**
```http
GET {{baseUrl}}/api/citas/1
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Cita obtenida exitosamente",
  "datos": {
    "idCita": 1,
    "idMascota": 1,
    "nombreMascota": "Max",
    "idCliente": 1,
    "nombreCliente": "Juan Pérez",
    "idSucursal": 1,
    "nombreSucursal": "Sucursal Centro",
    "idServicio": 1,
    "nombreServicio": "Baño y Corte",
    "fechaProgramada": "2025-11-25T10:00:00",
    "modalidad": "presencial",
    "estado": "reservada",
    "notas": "Cliente solicita usar champú especial"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` contiene el objeto de la cita
- Todos los campos están presentes

**❌ Caso de Error - Cita no existe:**
```http
GET {{baseUrl}}/api/citas/99999
```

Expected Response: HTTP 404 NOT FOUND
```json
{
  "exito": false,
  "mensaje": "Cita no encontrada",
  "datos": null,
  "error": "Cita no encontrada"
}
```

---

### 3. Crear Nueva Cita

**Objetivo:** Registrar una nueva cita en el sistema

**Endpoint:**
```http
POST {{baseUrl}}/api/citas
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "idMascota": 1,
  "idCliente": 1,
  "idSucursal": 1,
  "idServicio": 1,
  "fechaProgramada": "2025-12-01T14:00:00",
  "modalidad": "presencial",
  "notas": "Primera consulta del mes"
}
```

**Campos del Request:**

| Campo | Tipo | Requerido | Descripción | Valores |
|-------|------|-----------|-------------|---------|
| `idMascota` | Integer | ✅ Sí | ID de la mascota | Debe existir en BD |
| `idCliente` | Integer | ✅ Sí | ID del cliente | Debe existir en BD |
| `idSucursal` | Integer | ✅ Sí | ID de la sucursal | Debe existir en BD |
| `idServicio` | Integer | ✅ Sí | ID del servicio | Debe existir en BD |
| `fechaProgramada` | DateTime | ✅ Sí | Fecha y hora de la cita | ISO 8601 format |
| `modalidad` | String | ✅ Sí | Modalidad de la cita | `presencial` o `virtual` |
| `notas` | String | ❌ No | Notas adicionales | Texto libre |

**Expected Response:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Cita creada exitosamente",
  "datos": "Registro creado en base de datos",
  "error": null
}
```

**✅ Validaciones:**
- Status code: 201
- `exito` es `true`
- Verificar en BD que la cita se creó:
  ```sql
  SELECT * FROM cita ORDER BY id_cita DESC LIMIT 1;
  ```

**⚠️ Casos de Prueba Adicionales:**

1. **Fecha en el pasado:**
```json
{
  "idMascota": 1,
  "idCliente": 1,
  "idSucursal": 1,
  "idServicio": 1,
  "fechaProgramada": "2023-01-01T10:00:00",  // ✅ Pasado
  "modalidad": "presencial",
  "notas": "Cita pasada"
}
```
Debería aceptarse si no hay validación (verifica comportamiento esperado).

2. **Modalidad virtual:**
```json
{
  "idMascota": 2,
  "idCliente": 2,
  "idSucursal": 1,
  "idServicio": 2,
  "fechaProgramada": "2025-12-05T16:00:00",
  "modalidad": "virtual",  // ✅ Virtual
  "notas": "Consulta por videollamada"
}
```

3. **Sin notas (opcional):**
```json
{
  "idMascota": 3,
  "idCliente": 3,
  "idSucursal": 2,
  "idServicio": 3,
  "fechaProgramada": "2025-12-10T11:00:00",
  "modalidad": "presencial"
  // ✅ Sin campo "notas"
}
```

---

### 4. Obtener Citas de un Cliente

**Objetivo:** Listar todas las citas (pasadas y futuras) de un cliente específico

**Endpoint:**
```http
GET {{baseUrl}}/api/citas/cliente/1
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Citas del cliente obtenidas",
  "datos": [
    {
      "idCita": 1,
      "idMascota": 1,
      "nombreMascota": "Max",
      "idCliente": 1,
      "nombreCliente": "Juan Pérez",
      "idSucursal": 1,
      "nombreSucursal": "Sucursal Centro",
      "idServicio": 1,
      "nombreServicio": "Baño y Corte",
      "fechaProgramada": "2025-11-25T10:00:00",
      "modalidad": "presencial",
      "estado": "reservada",
      "notas": "Cliente solicita usar champú especial"
    },
    {
      "idCita": 5,
      "idMascota": 1,
      "nombreMascota": "Max",
      "idCliente": 1,
      "nombreCliente": "Juan Pérez",
      "idSucursal": 1,
      "nombreSucursal": "Sucursal Centro",
      "idServicio": 3,
      "nombreServicio": "Control dental",
      "fechaProgramada": "2025-10-15T09:00:00",  // Cita pasada
      "modalidad": "presencial",
      "estado": "atendido",
      "notas": null
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `datos` incluye citas pasadas y futuras
- Citas ordenadas por fecha (verificar orden)

**❌ Cliente sin citas:**
```http
GET {{baseUrl}}/api/citas/cliente/999
```
Debería retornar array vacío `[]` con HTTP 200.

---

### 5. Obtener Próximas Citas de Cliente

**Objetivo:** Listar solo las próximas citas (futuras) de un cliente

**Endpoint:**
```http
GET {{baseUrl}}/api/citas/cliente/1/proximas
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Próximas citas obtenidas",
  "datos": [
    {
      "idCita": 1,
      "idMascota": 1,
      "nombreMascota": "Max",
      "idCliente": 1,
      "nombreCliente": "Juan Pérez",
      "idSucursal": 1,
      "nombreSucursal": "Sucursal Centro",
      "idServicio": 1,
      "nombreServicio": "Baño y Corte",
      "fechaProgramada": "2025-11-25T10:00:00",  // Solo futuras
      "modalidad": "presencial",
      "estado": "reservada",
      "notas": "Cliente solicita usar champú especial"
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- Solo citas con `fechaProgramada` >= fecha actual
- NO incluye citas pasadas
- Ordenadas por fecha ascendente (más cercana primero)

---

### 6. Reprogramar Cita

**Objetivo:** Cambiar la fecha y hora de una cita existente

**Endpoint:**
```http
PUT {{baseUrl}}/api/citas/1/reprogramar?nuevaFecha=2025-12-15T11:00:00
Authorization: Bearer {{token}}
```

**Parámetros:**
- `nuevaFecha` (query param): Nueva fecha y hora en formato ISO 8601

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Cita reprogramada exitosamente",
  "datos": {
    "idCita": 1,
    "idMascota": 1,
    "nombreMascota": "Max",
    "idCliente": 1,
    "nombreCliente": "Juan Pérez",
    "idSucursal": 1,
    "nombreSucursal": "Sucursal Centro",
    "idServicio": 1,
    "nombreServicio": "Baño y Corte",
    "fechaProgramada": "2025-12-15T11:00:00",  // ✅ Fecha actualizada
    "modalidad": "presencial",
    "estado": "reservada",
    "notas": "Cliente solicita usar champú especial"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `fechaProgramada` se actualizó correctamente
- Estado permanece igual (no cambia a "confirmada" automáticamente)

**⚠️ Casos de Prueba:**

1. **Reprogramar a fecha pasada:**
```http
PUT {{baseUrl}}/api/citas/1/reprogramar?nuevaFecha=2023-01-01T10:00:00
```
Verificar si se acepta o rechaza (depende de reglas de negocio).

2. **Reprogramar cita cancelada:**
```http
PUT {{baseUrl}}/api/citas/X/reprogramar?nuevaFecha=2025-12-20T14:00:00
```
(Donde X es una cita con `estado=cancelada`)

---

### 7. Cancelar Cita

**Objetivo:** Marcar una cita como cancelada

**Endpoint:**
```http
PUT {{baseUrl}}/api/citas/1/cancelar
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Cita cancelada exitosamente",
  "datos": {
    "idCita": 1,
    "idMascota": 1,
    "nombreMascota": "Max",
    "idCliente": 1,
    "nombreCliente": "Juan Pérez",
    "idSucursal": 1,
    "nombreSucursal": "Sucursal Centro",
    "idServicio": 1,
    "nombreServicio": "Baño y Corte",
    "fechaProgramada": "2025-12-15T11:00:00",
    "modalidad": "presencial",
    "estado": "cancelada",  // ✅ Estado cambiado
    "notas": "Cliente solicita usar champú especial"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `estado` cambió a `"cancelada"`
- Resto de campos permanecen iguales

**⚠️ Caso de Prueba:**

**Cancelar cita ya cancelada:**
```http
PUT {{baseUrl}}/api/citas/1/cancelar
```
(Ejecutar dos veces seguidas)
Debería ser idempotente (no causar error).

---

### 8. Confirmar Asistencia

**Objetivo:** Cambiar el estado de una cita a "confirmada"

**Endpoint:**
```http
PUT {{baseUrl}}/api/citas/2/confirmar-asistencia
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Asistencia confirmada",
  "datos": {
    "idCita": 2,
    "idMascota": 2,
    "nombreMascota": "Luna",
    "idCliente": 2,
    "nombreCliente": "María García",
    "idSucursal": 1,
    "nombreSucursal": "Sucursal Centro",
    "idServicio": 2,
    "nombreServicio": "Consulta Veterinaria",
    "fechaProgramada": "2025-11-26T15:30:00",
    "modalidad": "virtual",
    "estado": "confirmada",  // ✅ Estado cambiado
    "notas": null
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `estado` cambió de `"reservada"` a `"confirmada"`

**⚠️ Caso de Prueba:**

**Confirmar cita cancelada:**
```http
PUT {{baseUrl}}/api/citas/X/confirmar-asistencia
```
(Donde X tiene `estado=cancelada`)
¿Se debe permitir confirmar una cita cancelada? Verificar comportamiento.

---

### 9. Marcar Como No-Show

**Objetivo:** Marcar que el cliente no asistió a su cita programada

**Endpoint:**
```http
PUT {{baseUrl}}/api/citas/3/no-show
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Cita marcada como no-show",
  "datos": {
    "idCita": 3,
    "idMascota": 3,
    "nombreMascota": "Rocky",
    "idCliente": 3,
    "nombreCliente": "Carlos López",
    "idSucursal": 2,
    "nombreSucursal": "Sucursal Norte",
    "idServicio": 1,
    "nombreServicio": "Baño y Corte",
    "fechaProgramada": "2025-11-23T10:00:00",
    "modalidad": "presencial",
    "estado": "no_asistio",  // ✅ Estado cambiado
    "notas": null
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `estado` cambió a `"no_asistio"`
- Se usa cuando la cita pasó y el cliente no llegó

---

## ❌ Casos de Error

### 1. Sin Token de Autenticación

```http
GET {{baseUrl}}/api/citas
# Sin header Authorization
```

**Expected:** HTTP 401 UNAUTHORIZED

---

### 2. Token Inválido o Expirado

```http
GET {{baseUrl}}/api/citas
Authorization: Bearer token_invalido_123
```

**Expected:** HTTP 401 UNAUTHORIZED

---

### 3. Cita No Encontrada

```http
GET {{baseUrl}}/api/citas/99999
Authorization: Bearer {{token}}
```

**Expected:** HTTP 404 NOT FOUND
```json
{
  "exito": false,
  "mensaje": "Cita no encontrada",
  "datos": null,
  "error": "..."
}
```

---

### 4. Crear Cita con IDs Inexistentes

```json
POST {{baseUrl}}/api/citas
{
  "idMascota": 99999,  // No existe
  "idCliente": 1,
  "idSucursal": 1,
  "idServicio": 1,
  "fechaProgramada": "2025-12-01T14:00:00",
  "modalidad": "presencial"
}
```

**Expected:** HTTP 400 BAD REQUEST o 500 (verificar comportamiento)

---

### 5. Modalidad Inválida

```json
POST {{baseUrl}}/api/citas
{
  "idMascota": 1,
  "idCliente": 1,
  "idSucursal": 1,
  "idServicio": 1,
  "fechaProgramada": "2025-12-01T14:00:00",
  "modalidad": "telefonica"  // ❌ Valor inválido
}
```

**Expected:** HTTP 400 BAD REQUEST

---

### 6. Campos Faltantes

```json
POST {{baseUrl}}/api/citas
{
  "idMascota": 1,
  // ❌ Falta idCliente
  "idSucursal": 1,
  "idServicio": 1,
  "fechaProgramada": "2025-12-01T14:00:00",
  "modalidad": "presencial"
}
```

**Expected:** HTTP 400 BAD REQUEST

---

## ✅ Checklist de Validación

### Funcionalidad Básica
- [ ] `GET /api/citas` retorna todas las citas
- [ ] `GET /api/citas/{id}` retorna una cita específica
- [ ] `POST /api/citas` crea una nueva cita
- [ ] La cita creada aparece en la BD
- [ ] Los IDs de cliente/mascota/sucursal/servicio deben existir

### Consultas por Cliente
- [ ] `GET /api/citas/cliente/{id}` retorna todas las citas del cliente
- [ ] `GET /api/citas/cliente/{id}/proximas` retorna solo citas futuras
- [ ] Las citas pasadas NO aparecen en `/proximas`

### Gestión de Estados
- [ ] `PUT /api/citas/{id}/reprogramar` actualiza la fecha
- [ ] `PUT /api/citas/{id}/cancelar` cambia estado a "cancelada"
- [ ] `PUT /api/citas/{id}/confirmar-asistencia` cambia estado a "confirmada"
- [ ] `PUT /api/citas/{id}/no-show` cambia estado a "no_asistio"

### Manejo de Errores
- [ ] Cita inexistente retorna HTTP 404
- [ ] Sin token retorna HTTP 401
- [ ] Token inválido retorna HTTP 401
- [ ] Campos faltantes retornan HTTP 400
- [ ] Modalidad inválida retorna HTTP 400

### Validaciones de Negocio
- [ ] Se puede crear cita con fecha futura
- [ ] Se puede (o no) crear cita con fecha pasada
- [ ] Se puede reprogramar cita cancelada (verificar)
- [ ] Se puede confirmar cita cancelada (verificar)
- [ ] IDs inexistentes causan error apropiado

### Respuestas
- [ ] Todas las respuestas exitosas tienen `exito: true`
- [ ] Todas las respuestas de error tienen `exito: false`
- [ ] Los códigos HTTP son apropiados
- [ ] Los mensajes son claros y descriptivos

---

## 📝 Notas Importantes

### Estados Válidos de Citas

Según `Cita.java`, los estados válidos son:

```java
public enum Estado {
    reservada,     // Cita recién creada
    confirmada,    // Cliente confirmó asistencia
    cancelada,     // Cita cancelada
    no_asistio,    // Cliente no llegó (antes era "no_show")
    atendido       // Cita completada
}
```

### Modalidades Válidas

```java
public enum Modalidad {
    presencial,    // Cita en sucursal física
    virtual        // Cita por videollamada/telemedicina
}
```

### Formato de Fechas

Todas las fechas deben estar en formato **ISO 8601**:
```
2025-12-01T14:30:00
YYYY-MM-DDTHH:MM:SS
```

---

## 🔄 Flujo de Prueba Recomendado

1. **Autenticarse** y obtener token
2. **Listar todas las citas** para ver datos existentes
3. **Crear una nueva cita** y guardar su ID
4. **Consultar la cita creada** por ID
5. **Reprogramar la cita** a otra fecha
6. **Confirmar asistencia** de la cita
7. **Listar citas del cliente** para verificar
8. **Cancelar la cita**
9. **Crear otra cita** y marcarla como no-show
10. **Probar casos de error** (IDs inexistentes, sin token, etc.)

---

## 📊 Tabla Resumen de Endpoints

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/citas` | Listar todas las citas | ✅ |
| `GET` | `/api/citas/{id}` | Obtener cita por ID | ✅ |
| `GET` | `/api/citas/cliente/{idCliente}` | Citas de un cliente | ✅ |
| `GET` | `/api/citas/cliente/{idCliente}/proximas` | Próximas citas de cliente | ✅ |
| `POST` | `/api/citas` | Crear nueva cita | ✅ |
| `PUT` | `/api/citas/{id}/reprogramar` | Cambiar fecha de cita | ✅ |
| `PUT` | `/api/citas/{id}/cancelar` | Cancelar cita | ✅ |
| `PUT` | `/api/citas/{id}/confirmar-asistencia` | Confirmar que cliente asistirá | ✅ |
| `PUT` | `/api/citas/{id}/no-show` | Marcar que cliente no asistió | ✅ |

---

**Preparado por:** Backend Team  
**Fecha:** 2025-11-23  
**Versión:** 1.0  
**Para usar con:** Postman, Insomnia o similar
