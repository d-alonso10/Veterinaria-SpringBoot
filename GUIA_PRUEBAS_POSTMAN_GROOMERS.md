# 📋 Guía de Pruebas Manuales en Postman - Módulo Groomers

**Proyecto:** Veterinaria SpringBoot  
**Módulo:** Gestión de Groomers (Peluqueros/Estilistas)  
**Base URL:** `http://localhost:8080`  
**Fecha:** 2025-11-23

---

## 📑 Índice

1. [Configuración Inicial](#configuración-inicial)
2. [Datos de Prueba](#datos-de-prueba)
3. [Pruebas de Endpoints](#pruebas-de-endpoints)
   - [1. Obtener Todos los Groomers](#1-obtener-todos-los-groomers)
   - [2. Obtener Groomer por ID](#2-obtener-groomer-por-id)
   - [3. Crear Nuevo Groomer](#3-crear-nuevo-groomer)
   - [4. Actualizar Groomer](#4-actualizar-groomer)
   - [5. Eliminar Groomer](#5-eliminar-groomer)
   - [6. Filtrar por Especialidad](#6-filtrar-por-especialidad)
   - [7. Ver Disponibilidad por Fecha](#7-ver-disponibilidad-por-fecha)
   - [8. Ver Ocupación por Fecha](#8-ver-ocupación-por-fecha)
   - [9. Verificar Disponibilidad Específica](#9-verificar-disponibilidad-específica)
   - [10. Tiempos Promedio](#10-tiempos-promedio)
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

### Estructura de la Entidad Groomer

```java
public class Groomer {
    private Integer idGroomer;                // ID autogenerado
    private String nombre;                    // Nombre completo (requerido)
    private String especialidades;            // JSON con especialidades
    private String disponibilidad;            // JSON con horarios
    private LocalDateTime createdAt;          // Fecha de creación (auto)
    private LocalDateTime updatedAt;          // Fecha de actualización (auto)
}
```

### Ejemplos de Campos JSON

#### Especialidades (JSON)
```json
{
  "principales": ["corte_raza", "baño_premium"],
  "adicionales": ["tinte", "spa"]
}
```

O simplemente un array:
```json
["corte", "baño", "dental"]
```

#### Disponibilidad (JSON)
```json
{
  "lunes": {"inicio": "09:00", "fin": "18:00"},
  "martes": {"inicio": "09:00", "fin": "18:00"},
  "miercoles": {"inicio": "09:00", "fin": "18:00"},
  "jueves": {"inicio": "09:00", "fin": "18:00"},
  "viernes": {"inicio": "09:00", "fin": "18:00"},
  "sabado": {"inicio": "09:00", "fin": "14:00"}
}
```

### Groomers de Ejemplo en BD

```sql
SELECT id_groomer, nombre FROM groomer;
```

**Ejemplo de datos:**

| ID | Nombre |
|----|--------|
| 1 | María González |
| 2 | Juan Pérez |
| 3 | Ana Torres |

---

## 🧪 Pruebas de Endpoints

### 1. Obtener Todos los Groomers

**Objetivo:** Listar todos los groomers registrados en el sistema

**Endpoint:**
```http
GET {{baseUrl}}/api/groomers
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Groomers obtenidos correctamente",
  "datos": [
    {
      "idGroomer": 1,
      "nombre": "María González",
      "especialidades": "{\"principales\":[\"corte_raza\",\"baño_premium\"]}",
      "disponibilidad": "{\"lunes\":{\"inicio\":\"09:00\",\"fin\":\"18:00\"}}",
      "createdAt": "2025-11-15T10:00:00",
      "updatedAt": "2025-11-15T10:00:00"
    },
    {
      "idGroomer": 2,
      "nombre": "Juan Pérez",
      "especialidades": "{\"principales\":[\"corte\",\"baño\"]}",
      "disponibilidad": "{\"lunes\":{\"inicio\":\"10:00\",\"fin\":\"19:00\"}}",
      "createdAt": "2025-11-16T11:00:00",
      "updatedAt": "2025-11-16T11:00:00"
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` es un array (puede estar vacío)
- Cada groomer tiene `idGroomer`, `nombre`
- `especialidades` y `disponibilidad` son JSON strings

---

### 2. Obtener Groomer por ID

**Objetivo:** Consultar un groomer específico por su ID

**Endpoint:**
```http
GET {{baseUrl}}/api/groomers/1
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Groomer encontrado",
  "datos": {
    "idGroomer": 1,
    "nombre": "María González",
    "especialidades": "{\"principales\":[\"corte_raza\",\"baño_premium\"],\"adicionales\":[\"tinte\"]}",
    "disponibilidad": "{\"lunes\":{\"inicio\":\"09:00\",\"fin\":\"18:00\"},\"martes\":{\"inicio\":\"09:00\",\"fin\":\"18:00\"}}",
    "createdAt": "2025-11-15T10:00:00",
    "updatedAt": "2025-11-15T10:00:00"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` contiene el objeto del groomer completo
- Todos los campos están presentes

**❌ Caso de Error - Groomer no existe:**
```http
GET {{baseUrl}}/api/groomers/99999
```

Expected Response: HTTP 404 NOT FOUND
```json
{
  "exito": false,
  "mensaje": "Groomer no encontrado con ID: 99999",
  "datos": null,
  "error": null
}
```

---

### 3. Crear Nuevo Groomer

**Objetivo:** Registrar un nuevo groomer en el sistema

**Endpoint:**
```http
POST {{baseUrl}}/api/groomers
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nombre": "Carlos Ramírez",
  "especialidades": "{\"principales\":[\"corte\",\"baño\"],\"adicionales\":[\"spa\"]}",
  "disponibilidad": "{\"lunes\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"martes\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"miercoles\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"jueves\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"viernes\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"}}"
}
```

**Campos del Request:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `nombre` | String | ✅ Sí | Nombre completo del groomer |
| `especialidades` | String (JSON) | ❌ No | JSON con especialidades |
| `disponibilidad` | String (JSON) | ❌ No | JSON con horarios de trabajo |

**Expected Response:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Groomer creado correctamente",
  "datos": {
    "idGroomer": 4,
    "nombre": "Carlos Ramírez",
    "especialidades": "{\"principales\":[\"corte\",\"baño\"],\"adicionales\":[\"spa\"]}",
    "disponibilidad": "{\"lunes\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"martes\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"miercoles\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"jueves\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"},\"viernes\":{\"inicio\":\"08:00\",\"fin\":\"17:00\"}}",
    "createdAt": "2025-11-23T17:45:00",
    "updatedAt": "2025-11-23T17:45:00"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 201
- `exito` es `true`
- `datos` contiene el groomer creado con su `idGroomer` asignado
- `createdAt` y `updatedAt` se generan automáticamente

**⚠️ Casos de Prueba Adicionales:**

1. **Groomer solo con nombre (campos opcionales vacíos):**
```json
{
  "nombre": "Luis Fernández"
}
```

2. **Especialidades como array simple:**
```json
{
  "nombre": "Ana María López",
  "especialidades": "[\"corte\",\"baño\",\"dental\"]"
}
```

3. **Disponibilidad parcial:**
```json
{
  "nombre": "Pedro Sánchez",
  "disponibilidad": "{\"lunes\":{\"inicio\":\"13:00\",\"fin\":\"21:00\"},\"martes\":{\"inicio\":\"13:00\",\"fin\":\"21:00\"}}"
}
```

---

### 4. Actualizar Groomer

**Objetivo:** Modificar los datos de un groomer existente

**Endpoint:**
```http
PUT {{baseUrl}}/api/groomers/4
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nombre": "Carlos Ramírez Gutiérrez",
  "especialidades": "{\"principales\":[\"corte_raza\",\"baño_premium\",\"tinte\"],\"adicionales\":[\"spa\",\"masajes\"]}",
  "disponibilidad": "{\"lunes\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"martes\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"miercoles\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"jueves\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"viernes\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"sabado\":{\"inicio\":\"09:00\",\"fin\":\"14:00\"}}"
}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Groomer actualizado correctamente",
  "datos": {
    "idGroomer": 4,
    "nombre": "Carlos Ramírez Gutiérrez",
    "especialidades": "{\"principales\":[\"corte_raza\",\"baño_premium\",\"tinte\"],\"adicionales\":[\"spa\",\"masajes\"]}",
    "disponibilidad": "{\"lunes\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"martes\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"miercoles\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"jueves\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"viernes\":{\"inicio\":\"08:00\",\"fin\":\"18:00\"},\"sabado\":{\"inicio\":\"09:00\",\"fin\":\"14:00\"}}",
    "createdAt": "2025-11-23T17:45:00",
    "updatedAt": "2025-11-23T18:10:00"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- Los campos se actualizaron correctamente
- `updatedAt` cambió a la fecha/hora actual
- `createdAt` permanece igual

---

### 5. Eliminar Groomer

**Objetivo:** Eliminar un groomer del sistema

**Endpoint:**
```http
DELETE {{baseUrl}}/api/groomers/4
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Groomer eliminado correctamente",
  "datos": null,
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` es `null`
- Verificar en BD que el groomer fue eliminado

**⚠️ Restricción de Foreign Key:**

Si intentas eliminar un groomer que tiene:
- Atenciones asignadas
- Horarios registrados

**La BD podría impedirlo** (por FOREIGN KEY constraints).

---

### 6. Filtrar por Especialidad

**Objetivo:** Obtener todos los groomers que tienen una especialidad específica

**Endpoint:**
```http
GET {{baseUrl}}/api/groomers/especialidad/corte_raza
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Groomers obtenidos por especialidad correctamente",
  "datos": [
    {
      "idGroomer": 1,
      "nombre": "María González",
      "especialidades": "{\"principales\":[\"corte_raza\",\"baño_premium\"]}",
      "disponibilidad": "{\"lunes\":{\"inicio\":\"09:00\",\"fin\":\"18:00\"}}",
      "createdAt": "2025-11-15T10:00:00",
      "updatedAt": "2025-11-15T10:00:00"
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- Retorna solo groomers que tienen la especialidad buscada
- Busca en el JSON de `especialidades`

**⚠️ Casos de Prueba:**

1. **Especialidad "baño":**
```http
GET {{baseUrl}}/api/groomers/especialidad/baño
```

2. **Especialidad que no existe:**
```http
GET {{baseUrl}}/api/groomers/especialidad/veterinaria
```
Debería retornar array vacío `[]`.

---

### 7. Ver Disponibilidad por Fecha

**Objetivo:** Obtener la disponibilidad de todos los groomers para una fecha específica

**Endpoint:**
```http
GET {{baseUrl}}/api/groomers/disponibilidad/2025-11-25
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Datos de disponibilidad obtenidos correctamente",
  "datos": [
    [1, "María González", 8],
    [2, "Juan Pérez", 5],
    [3, "Ana Torres", 12]
  ],
  "error": null
}
```

**Formato de Datos:**
Cada elemento es un array con:
- `[0]`: ID del groomer
- `[1]`: Nombre del groomer
- `[2]`: Horas disponibles

**✅ Validaciones:**
- Status code: 200
- Retorna array de arrays (`Object[]`)
- Cada array interno tiene 3 elementos

---

### 8. Ver Ocupación por Fecha

**Objetivo:** Ver cuántas atenciones tiene cada groomer en una fecha

**Endpoint:**
```http
GET {{baseUrl}}/api/groomers/ocupacion/2025-11-25
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Información de ocupación obtenida correctamente",
  "datos": [
    [1, "María González", 3, 180],
    [2, "Juan Pérez", 2, 120],
    [3, "Ana Torres", 1, 60]
  ],
  "error": null
}
```

**Formato de Datos:**
Cada elemento es un array con:
- `[0]`: ID del groomer
- `[1]`: Nombre del groomer
- `[2]`: Número de atenciones
- `[3]`: Minutos totales ocupados

**✅ Validaciones:**
- Status code: 200
- Retorna array de arrays
- Cada array interno tiene 4 elementos

---

### 9. Verificar Disponibilidad Específica

**Objetivo:** Verificar si un groomer está disponible en una fecha/hora específica por X minutos

**Endpoint:**
```http
GET {{baseUrl}}/api/groomers/disponible/1/2025-11-25T14:00:00/60
Authorization: Bearer {{token}}
```

**Parámetros:**
- `{idGroomer}`: ID del groomer (ej: 1)
- `{fecha}`: Fecha y hora en formato ISO (ej: 2025-11-25T14:00:00)
- `{minutos}`: Duración necesaria en minutos (ej: 60)

**Expected Response - Disponible:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Groomer está disponible",
  "datos": true,
  "error": null
}
```

**Expected Response - NO Disponible:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Groomer no está disponible",
  "datos": false,
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `datos` es un booleano (`true` o `false`)
- El mensaje refleja la disponibilidad

**⚠️ Casos de Prueba:**

1. **Horario fuera de rango (noche):**
```http
GET {{baseUrl}}/api/groomers/disponible/1/2025-11-25T22:00:00/30
```
Debería retornar `false`.

2. **Servicio muy largo:**
```http
GET {{baseUrl}}/api/groomers/disponible/1/2025-11-25T10:00:00/600
```
(600 minutos = 10 horas)

---

### 10. Tiempos Promedio

**Objetivo:** Obtener los tiempos promedio de atención de cada groomer en un período

**Endpoint:**
```http
GET {{baseUrl}}/api/groomers/tiempos-promedio?fechaInicio=2025-11-01&fechaFin=2025-11-30
Authorization: Bearer {{token}}
```

**Parámetros (Query Params):**
- `fechaInicio`: Fecha de inicio (formato: YYYY-MM-DD)
- `fechaFin`: Fecha de fin (formato: YYYY-MM-DD)

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Tiempos promedio obtenidos correctamente",
  "datos": [
    [1, "María González", 75.5],
    [2, "Juan Pérez", 60.2],
    [3, "Ana Torres", 90.0]
  ],
  "error": null
}
```

**Formato de Datos:**
Cada elemento es un array con:
- `[0]`: ID del groomer
- `[1]`: Nombre del groomer
- `[2]`: Tiempo promedio en minutos (decimal)

**✅ Validaciones:**
- Status code: 200
- Retorna array de arrays
- Cada array interno tiene 3 elementos
- El tiempo promedio es un número decimal

---

## ❌ Casos de Error

### 1. Sin Token de Autenticación

```http
GET {{baseUrl}}/api/groomers
# Sin header Authorization
```

**Expected:** HTTP 401 UNAUTHORIZED

---

### 2. Groomer No Encontrado

```http
GET {{baseUrl}}/api/groomers/99999
```

**Expected:** HTTP 404 NOT FOUND
```json
{
  "exito": false,
  "mensaje": "Groomer no encontrado con ID: 99999",
  "datos": null,
  "error": null
}
```

---

### 3. Campo Nombre Faltante

```json
POST {{baseUrl}}/api/groomers
{
  "especialidades": "[\"corte\"]"
  // ❌ Falta "nombre"
}
```

**Expected:** HTTP 400 BAD REQUEST

---

### 4. JSON Inválido en Especialidades

```json
{
  "nombre": "Test",
  "especialidades": "corte,baño"  // ❌ No es JSON válido
}
```

**Expected:** Podría aceptarse como string simple (verificar comportamiento)

---

### 5. Fecha Inválida

```http
GET {{baseUrl}}/api/groomers/disponibilidad/2025-13-40
```

**Expected:** HTTP 400 BAD REQUEST

---

## ✅ Checklist de Validación

### Funcionalidad Básica (CRUD)
- [ ] `GET /api/groomers` retorna todos los groomers
- [ ] `GET /api/groomers/{id}` retorna un groomer específico
- [ ] `POST /api/groomers` crea un nuevo groomer
- [ ] El groomer creado aparece en la BD con `idGroomer` asignado
- [ ] `PUT /api/groomers/{id}` actualiza un groomer
- [ ] `DELETE /api/groomers/{id}` elimina un groomer
- [ ] `createdAt` y `updatedAt` se generan automáticamente

### Consultas Especializadas
- [ ] `GET /api/groomers/especialidad/{especialidad}` filtra por especialidad
- [ ] `GET /api/groomers/disponibilidad/{fecha}` retorna disponibilidad por fecha
- [ ] `GET /api/groomers/ocupacion/{fecha}` retorna ocupación por fecha
- [ ] `GET /api/groomers/disponible/{id}/{fecha}/{min}` verifica disponibilidad
- [ ] `GET /api/groomers/tiempos-promedio` retorna tiempos con fechas

### Validaciones de Campos
- [ ] `nombre` es requerido
- [ ] `especialidades` es opcional (JSON)
- [ ] `disponibilidad` es opcional (JSON)
- [ ] JSON strings se almacenan correctamente

### Manejo de Errores
- [ ] Groomer inexistente retorna HTTP 404
- [ ] Sin token retorna HTTP 401
- [ ] Campos faltantes retornan HTTP 400
- [ ] Fechas inválidas retornan HTTP 400

### Respuestas
- [ ] Todas las respuestas exitosas tienen `exito: true`
- [ ] Todas las respuestas de error tienen `exito: false`
- [ ] Los códigos HTTP son apropiados (200, 201, 400, 404)
- [ ] Los mensajes son claros y descriptivos

---

## 📝 Notas Importantes

### Campos JSON

Los campos `especialidades` y `disponibilidad` son **JSON strings**, no objetos JSON directos.

**Correcto:**
```json
{
  "nombre": "Test",
  "especialidades": "{\"principales\":[\"corte\"]}"
}
```

**Incorrecto:**
```json
{
  "nombre": "Test",
  "especialidades": {"principales": ["corte"]}  // ❌ No es string
}
```

### Formato de Fechas

- **Para endpoints de disponibilidad/ocupación:** `YYYY-MM-DD` (solo fecha)
  - Ejemplo: `2025-11-25`

- **Para verificar disponibilidad específica:** `YYYY-MM-DDTHH:MM:SS` (fecha y hora ISO 8601)
  - Ejemplo: `2025-11-25T14:30:00`

- **Para tiempos promedio:** `YYYY-MM-DD` (query params)
  - Ejemplo: `?fechaInicio=2025-11-01&fechaFin=2025-11-30`

### Respuestas con `Object[]`

Algunos endpoints retornan `List<Object[]>` (array de arrays):

```json
{
  "datos": [
    [1, "María", 8],
    [2, "Juan", 5]
  ]
}
```

**No es un objeto JSON con propiedades**, es un array bidimensional.

---

## 🔄 Flujo de Prueba Recomendado

1. **Autenticarse** y obtener token
2. **Listar todos los groomers** para ver datos existentes
3. **Consultar un groomer** por ID
4. **Crear un nuevo groomer** y guardar su ID
5. **Actualizar el groomer** creado
6. **Filtrar por especialidad**
7. **Ver disponibilidad** para una fecha futura
8. **Ver ocupación** para hoy
9. **Verificar disponibilidad** de un groomer específico
10. **Obtener tiempos promedio** del último mes
11. **Eliminar el groomer** de prueba
12. **Probar casos de error**

---

## 📊 Tabla Resumen de Endpoints

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/groomers` | Listar todos | ✅ |
| `GET` | `/api/groomers/{id}` | Obtener por ID | ✅ |
| `GET` | `/api/groomers/especialidad/{especialidad}` | Filtrar por especialidad | ✅ |
| `GET` | `/api/groomers/disponibilidad/{fecha}` | Disponibilidad por fecha | ✅ |
| `GET` | `/api/groomers/ocupacion/{fecha}` | Ocupación por fecha | ✅ |
| `GET` | `/api/groomers/disponible/{id}/{fecha}/{min}` | Verificar disponibilidad | ✅ |
| `GET` | `/api/groomers/tiempos-promedio` | Tiempos promedio | ✅ |
| `POST` | `/api/groomers` | Crear nuevo groomer | ✅ |
| `PUT` | `/api/groomers/{id}` | Actualizar groomer | ✅ |
| `DELETE` | `/api/groomers/{id}` | Eliminar groomer | ✅ |

---

**Preparado por:** Backend Team  
**Fecha:** 2025-11-23  
**Versión:** 1.0  
**Para usar con:** Postman, Insomnia o similar
