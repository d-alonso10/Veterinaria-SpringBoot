# 📋 Guía de Pruebas Manuales en Postman - Módulo Servicios

**Proyecto:** Veterinaria SpringBoot  
**Módulo:** Gestión de Servicios  
**Base URL:** `http://localhost:8080`  
**Fecha:** 2025-11-23

---

## 📑 Índice

1. [Configuración Inicial](#configuración-inicial)
2. [Datos de Prueba](#datos-de-prueba)
3. [Pruebas de Endpoints](#pruebas-de-endpoints)
   - [1. Obtener Todos los Servicios](#1-obtener-todos-los-servicios)
   - [2. Obtener Servicio por ID](#2-obtener-servicio-por-id)
   - [3. Buscar Servicios por Nombre](#3-buscar-servicios-por-nombre)
   - [4. Filtrar por Categoría](#4-filtrar-por-categoría)
   - [5. Crear Nuevo Servicio](#5-crear-nuevo-servicio)
   - [6. Actualizar Servicio](#6-actualizar-servicio)
   - [7. Eliminar Servicio](#7-eliminar-servicio)
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

### Categorías Válidas

Según la entidad `Servicio.java`, las categorías válidas son:

```java
public enum Categoria {
    baño,      // Servicios de baño
    corte,     // Servicios de corte de pelo
    dental,    // Servicios dentales
    paquete,   // Paquetes de servicios
    otro       // Otros servicios
}
```

### Servicios de Ejemplo en BD

```sql
SELECT id_servicio, codigo, nombre, categoria, precio_base 
FROM servicio 
LIMIT 5;
```

**Ejemplo de datos:**

| ID | Código | Nombre | Categoría | Precio |
|----|--------|--------|-----------|--------|
| 1 | SRV-001 | Baño Completo | baño | 50.00 |
| 2 | SRV-002 | Corte de Pelo | corte | 35.00 |
| 3 | SRV-003 | Limpieza Dental | dental | 80.00 |

---

## 🧪 Pruebas de Endpoints

### 1. Obtener Todos los Servicios

**Objetivo:** Listar todos los servicios registrados en el catálogo

**Endpoint:**
```http
GET {{baseUrl}}/servicios
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Servicios obtenidos exitosamente",
  "datos": [
    {
      "idServicio": 1,
      "codigo": "SRV-001",
      "nombre": "Baño Completo",
      "descripcion": "Baño completo con champú especial y acondicionador",
      "duracionEstimadaMin": 60,
      "precioBase": 50.00,
      "categoria": "baño",
      "createdAt": "2025-01-15T10:30:00",
      "updatedAt": "2025-01-15T10:30:00"
    },
    {
      "idServicio": 2,
      "codigo": "SRV-002",
      "nombre": "Corte de Pelo Estándar",
      "descripcion": "Corte de pelo básico para todas las razas",
      "duracionEstimadaMin": 45,
      "precioBase": 35.00,
      "categoria": "corte",
      "createdAt": "2025-01-15T10:35:00",
      "updatedAt": "2025-01-15T10:35:00"
    },
    {
      "idServicio": 3,
      "codigo": "SRV-003",
      "nombre": "Limpieza Dental",
      "descripcion": "Limpieza y revisión dental completa",
      "duracionEstimadaMin": 90,
      "precioBase": 80.00,
      "categoria": "dental",
      "createdAt": "2025-01-15T10:40:00",
      "updatedAt": "2025-01-15T10:40:00"
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` es un array (puede estar vacío)
- Cada servicio tiene todos los campos (`idServicio`, `codigo`, `nombre`, `categoria`, `precioBase`)

---

### 2. Obtener Servicio por ID

**Objetivo:** Consultar un servicio específico por su ID

**Endpoint:**
```http
GET {{baseUrl}}/servicios/1
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Servicio obtenido exitosamente",
  "datos": {
    "idServicio": 1,
    "codigo": "SRV-001",
    "nombre": "Baño Completo",
    "descripcion": "Baño completo con champú especial y acondicionador",
    "duracionEstimadaMin": 60,
    "precioBase": 50.00,
    "categoria": "baño",
    "createdAt": "2025-01-15T10:30:00",
    "updatedAt": "2025-01-15T10:30:00"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` contiene el objeto del servicio completo
- Todos los campos están presentes

**❌ Caso de Error - Servicio no existe:**
```http
GET {{baseUrl}}/servicios/99999
```

Expected Response: HTTP 404 NOT FOUND
```json
{
  "exito": false,
  "mensaje": "Servicio no encontrado",
  "datos": null,
  "error": "Servicio no encontrado"
}
```

---

### 3. Buscar Servicios por Nombre

**Objetivo:** Buscar servicios cuyo nombre contenga el término especificado

**Endpoint:**
```http
GET {{baseUrl}}/servicios/buscar/baño
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Búsqueda completada",
  "datos": [
    {
      "idServicio": 1,
      "codigo": "SRV-001",
      "nombre": "Baño Completo",
      "descripcion": "Baño completo con champú especial y acondicionador",
      "duracionEstimadaMin": 60,
      "precioBase": 50.00,
      "categoria": "baño",
      "createdAt": "2025-01-15T10:30:00",
      "updatedAt": "2025-01-15T10:30:00"
    },
    {
      "idServicio": 5,
      "codigo": "SRV-005",
      "nombre": "Baño Express",
      "descripcion": "Baño rápido sin secado",
      "duracionEstimadaMin": 30,
      "precioBase": 25.00,
      "categoria": "baño",
      "createdAt": "2025-01-16T09:00:00",
      "updatedAt": "2025-01-16T09:00:00"
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- Retorna solo servicios cuyo nombre contiene el término buscado
- La búsqueda es **case-insensitive** (probablemente)
- Array vacío `[]` si no hay coincidencias

**⚠️ Casos de Prueba Adicionales:**

1. **Búsqueda parcial:**
```http
GET {{baseUrl}}/servicios/buscar/cor
```
Debería encontrar "Corte de Pelo", "Corte Especial", etc.

2. **Búsqueda sin resultados:**
```http
GET {{baseUrl}}/servicios/buscar/xxxnoexistexxx
```
Debería retornar array vacío con HTTP 200.

3. **Búsqueda con espacios:**
```http
GET {{baseUrl}}/servicios/buscar/corte pelo
```
Debería encontrar "Corte de Pelo".

---

### 4. Filtrar por Categoría

**Objetivo:** Obtener todos los servicios de una categoría específica

**Endpoint:**
```http
GET {{baseUrl}}/servicios/categoria/baño
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Servicios obtenidos exitosamente",
  "datos": [
    {
      "idServicio": 1,
      "codigo": "SRV-001",
      "nombre": "Baño Completo",
      "descripcion": "Baño completo con champú especial y acondicionador",
      "duracionEstimadaMin": 60,
      "precioBase": 50.00,
      "categoria": "baño",
      "createdAt": "2025-01-15T10:30:00",
      "updatedAt": "2025-01-15T10:30:00"
    },
    {
      "idServicio": 5,
      "codigo": "SRV-005",
      "nombre": "Baño Express",
      "descripcion": "Baño rápido sin secado",
      "duracionEstimadaMin": 30,
      "precioBase": 25.00,
      "categoria": "baño",
      "createdAt": "2025-01-16T09:00:00",
      "updatedAt": "2025-01-16T09:00:00"
    }
  ],
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- Todos los servicios retornados tienen `categoria` igual al parámetro
- Array vacío si no hay servicios en esa categoría

**⚠️ Probar con Todas las Categorías:**

1. **Categoría "corte":**
```http
GET {{baseUrl}}/servicios/categoria/corte
```

2. **Categoría "dental":**
```http
GET {{baseUrl}}/servicios/categoria/dental
```

3. **Categoría "paquete":**
```http
GET {{baseUrl}}/servicios/categoria/paquete
```

4. **Categoría "otro":**
```http
GET {{baseUrl}}/servicios/categoria/otro
```

**❌ Categoría inválida:**
```http
GET {{baseUrl}}/servicios/categoria/veterinaria
```
Expected: HTTP 400 BAD REQUEST o array vacío (verificar comportamiento).

---

### 5. Crear Nuevo Servicio

**Objetivo:** Registrar un nuevo servicio en el catálogo

**Endpoint:**
```http
POST {{baseUrl}}/servicios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "codigo": "SRV-010",
  "nombre": "Corte de Uñas",
  "descripcion": "Corte y limado de uñas para mascotas",
  "duracionEstimadaMin": 20,
  "precioBase": 15.00,
  "categoria": "otro"
}
```

**Campos del Request:**

| Campo | Tipo | Requerido | Descripción | Restricciones |
|-------|------|-----------|-------------|---------------|
| `codigo` | String | ✅ Sí | Código único del servicio | MAX 20 chars, UNIQUE |
| `nombre` | String | ✅ Sí | Nombre del servicio | MAX 100 chars |
| `descripcion` | String | ❌ No | Descripción detallada | Texto libre |
| `duracionEstimadaMin` | Integer | ❌ No | Duración en minutos | Número positivo |
| `precioBase` | Decimal | ✅ Sí | Precio base del servicio | DECIMAL(10,2) |
| `categoria` | String | ✅ Sí | Categoría del servicio | `baño`, `corte`, `dental`, `paquete`, `otro` |

**Expected Response:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Servicio creado exitosamente",
  "datos": {
    "idServicio": 10,
    "codigo": "SRV-010",
    "nombre": "Corte de Uñas",
    "descripcion": "Corte y limado de uñas para mascotas",
    "duracionEstimadaMin": 20,
    "precioBase": 15.00,
    "categoria": "otro",
    "createdAt": "2025-11-23T13:45:00",
    "updatedAt": "2025-11-23T13:45:00"
  },
  "error": null
}
```

**✅ Validaciones:**
- Status code: 201
- `exito` es `true`
- `datos` contiene el servicio creado con su `idServicio` asignado
- `createdAt` y `updatedAt` se generan automáticamente

**⚠️ Casos de Prueba Adicionales:**

1. **Sin descripción (campo opcional):**
```json
{
  "codigo": "SRV-011",
  "nombre": "Servicio Básico",
  "precioBase": 10.00,
  "categoria": "otro"
}
```

2. **Sin duración (campo opcional):**
```json
{
  "codigo": "SRV-012",
  "nombre": "Consulta Virtual",
  "descripcion": "Consulta veterinaria por videollamada",
  "precioBase": 40.00,
  "categoria": "otro"
}
```

3. **Precio con decimales:**
```json
{
  "codigo": "SRV-013",
  "nombre": "Vacunación Antirrábica",
  "descripcion": "Aplicación de vacuna antirrábica",
  "duracionEstimadaMin": 15,
  "precioBase": 45.50,
  "categoria": "otro"
}
```

4. **Categoría "paquete":**
```json
{
  "codigo": "PKG-001",
  "nombre": "Paquete Completo Mensual",
  "descripcion": "Incluye baño, corte y limpieza dental",
  "duracionEstimadaMin": 180,
  "precioBase": 150.00,
  "categoria": "paquete"
}
```

---

### 6. Actualizar Servicio

**Objetivo:** Modificar los datos de un servicio existente

**Endpoint:**
```http
PUT {{baseUrl}}/servicios/10
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "codigo": "SRV-010",
  "nombre": "Corte de Uñas Premium",
  "descripcion": "Corte, limado y pulido de uñas con productos naturales",
  "duracionEstimadaMin": 30,
  "precioBase": 20.00,
  "categoria": "otro"
}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Servicio actualizado exitosamente",
  "datos": {
    "idServicio": 10,
    "codigo": "SRV-010",
    "nombre": "Corte de Uñas Premium",
    "descripcion": "Corte, limado y pulido de uñas con productos naturales",
    "duracionEstimadaMin": 30,
    "precioBase": 20.00,
    "categoria": "otro",
    "createdAt": "2025-11-23T13:45:00",
    "updatedAt": "2025-11-23T14:10:00"
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

**⚠️ Casos de Prueba:**

1. **Actualizar solo el precio:**
```json
{
  "codigo": "SRV-010",
  "nombre": "Corte de Uñas Premium",
  "descripcion": "Corte, limado y pulido de uñas con productos naturales",
  "duracionEstimadaMin": 30,
  "precioBase": 25.00,  // ✅ Solo cambió esto
  "categoria": "otro"
}
```

2. **Cambiar de categoría:**
```json
{
  "codigo": "SRV-002",
  "nombre": "Corte de Pelo Estándar",
  "descripcion": "Corte de pelo básico para todas las razas",
  "duracionEstimadaMin": 45,
  "precioBase": 35.00,
  "categoria": "baño"  // ✅ Cambió de "corte" a "baño"
}
```

**❌ Casos de Error:**

**Código duplicado (si se intenta cambiar a un código ya existente):**
```json
{
  "codigo": "SRV-001",  // ❌ Ya existe
  "nombre": "Otro Servicio",
  "precioBase": 50.00,
  "categoria": "otro"
}
```
Expected: HTTP 400 BAD REQUEST o CONFLICT

---

### 7. Eliminar Servicio

**Objetivo:** Eliminar un servicio del catálogo

**Endpoint:**
```http
DELETE {{baseUrl}}/servicios/10
Authorization: Bearer {{token}}
```

**Expected Response:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Servicio eliminado exitosamente",
  "datos": null,
  "error": null
}
```

**✅ Validaciones:**
- Status code: 200
- `exito` es `true`
- `datos` es `null`
- Verificar en BD que el servicio fue eliminado:
  ```sql
  SELECT * FROM servicio WHERE id_servicio = 10;
  -- NO debería retornar nada
  ```

**❌ Intentar eliminar servicio inexistente:**
```http
DELETE {{baseUrl}}/servicios/99999
```
Expected: HTTP 404 NOT FOUND o HTTP 400 BAD REQUEST

**⚠️ Restricción de Foreign Key:**

Si intentas eliminar un servicio que está siendo usado en:
- Citas
- Atenciones
- Paquetes de servicios

**Puede que la BD lo impida** (por FOREIGN KEY constraints).

**Ejemplo:**
```http
DELETE {{baseUrl}}/servicios/1
```
Si el servicio ID 1 tiene citas asociadas, debería retornar:
```json
{
  "exito": false,
  "mensaje": "Error al eliminar servicio",
  "datos": null,
  "error": "No se puede eliminar porque el registro está relacionado con otros datos"
}
```

---

## ❌ Casos de Error

### 1. Sin Token de Autenticación

```http
GET {{baseUrl}}/servicios
# Sin header Authorization
```

**Expected:** HTTP 401 UNAUTHORIZED

---

### 2. Token Inválido o Expirado

```http
GET {{baseUrl}}/servicios
Authorization: Bearer token_invalido_123
```

**Expected:** HTTP 401 UNAUTHORIZED

---

### 3. Servicio No Encontrado

```http
GET {{baseUrl}}/servicios/99999
Authorization: Bearer {{token}}
```

**Expected:** HTTP 404 NOT FOUND
```json
{
  "exito": false,
  "mensaje": "Servicio no encontrado",
  "datos": null,
  "error": "..."
}
```

---

### 4. Crear Servicio con Código Duplicado

```json
POST {{baseUrl}}/servicios
{
  "codigo": "SRV-001",  // ❌ Ya existe
  "nombre": "Otro Baño",
  "precioBase": 60.00,
  "categoria": "baño"
}
```

**Expected:** HTTP 400 BAD REQUEST o CONFLICT
```json
{
  "exito": false,
  "mensaje": "Error al crear servicio",
  "datos": null,
  "error": "El código ya existe en el sistema"
}
```

---

### 5. Categoría Inválida

```json
POST {{baseUrl}}/servicios
{
  "codigo": "SRV-020",
  "nombre": "Servicio de Prueba",
  "precioBase": 30.00,
  "categoria": "veterinaria"  // ❌ No existe esta categoría
}
```

**Expected:** HTTP 400 BAD REQUEST

---

### 6. Campos Faltantes

```json
POST {{baseUrl}}/servicios
{
  "codigo": "SRV-021"
  // ❌ Faltan: nombre, precioBase, categoria
}
```

**Expected:** HTTP 400 BAD REQUEST

---

### 7. Precio Negativo

```json
POST {{baseUrl}}/servicios
{
  "codigo": "SRV-022",
  "nombre": "Servicio Negativo",
  "precioBase": -10.00,  // ❌ Precio negativo
  "categoria": "otro"
}
```

**Expected:** HTTP 400 BAD REQUEST (si hay validación) o se acepta (verificar comportamiento).

---

### 8. Código Muy Largo

```json
POST {{baseUrl}}/servicios
{
  "codigo": "SRV-0123456789012345678901234567890",  // ❌ Más de 20 caracteres
  "nombre": "Servicio",
  "precioBase": 10.00,
  "categoria": "otro"
}
```

**Expected:** HTTP 400 BAD REQUEST

---

## ✅ Checklist de Validación

### Funcionalidad Básica
- [ ] `GET /servicios` retorna todos los servicios
- [ ] `GET /servicios/{id}` retorna un servicio específico
- [ ] `POST /servicios` crea un nuevo servicio
- [ ] El servicio creado aparece en la BD con `idServicio` asignado
- [ ] `createdAt` y `updatedAt` se generan automáticamente

### Consultas y Filtros
- [ ] `GET /servicios/buscar/{nombre}` encuentra servicios por nombre
- [ ] La búsqueda es case-insensitive
- [ ] `GET /servicios/categoria/{categoria}` filtra por categoría
- [ ] Se puede filtrar por todas las categorías: baño, corte, dental, paquete, otro

### Actualización
- [ ] `PUT /servicios/{id}` actualiza un servicio existente
- [ ] `updatedAt` se actualiza al modificar
- [ ] `createdAt` NO cambia al actualizar
- [ ] Se puede cambiar el precio
- [ ] Se puede cambiar la categoría
- [ ] Se puede cambiar la descripción

### Eliminación
- [ ] `DELETE /servicios/{id}` elimina un servicio
- [ ] No se puede eliminar si hay Foreign Keys (citas, atenciones)
- [ ] Eliminar servicio inexistente retorna error apropiado

### Validaciones de Campos
- [ ] `codigo` es único (no permite duplicados)
- [ ] `codigo` tiene máximo 20 caracteres
- [ ] `nombre` es requerido
- [ ] `precioBase` es requerido
- [ ] `categoria` es requerida
- [ ] `categoria` solo acepta valores válidos
- [ ] `descripcion` es opcional
- [ ] `duracionEstimadaMin` es opcional

### Manejo de Errores
- [ ] Servicio inexistente retorna HTTP 404
- [ ] Sin token retorna HTTP 401
- [ ] Token inválido retorna HTTP 401
- [ ] Código duplicado retorna HTTP 400/CONFLICT
- [ ] Categoría inválida retorna HTTP 400
- [ ] Campos faltantes retornan HTTP 400

### Respuestas
- [ ] Todas las respuestas exitosas tienen `exito: true`
- [ ] Todas las respuestas de error tienen `exito: false`
- [ ] Los códigos HTTP son apropiados (200, 201, 400, 404)
- [ ] Los mensajes son claros y descriptivos

---

## 📝 Notas Importantes

### Categorías Válidas

```java
public enum Categoria {
    baño,      // Servicios de baño y limpieza
    corte,     // Servicios de estética y corte
    dental,    // Servicios de odontología
    paquete,   // Paquetes combinados
    otro       // Otros servicios no categorizados
}
```

### Restricciones de la Base de Datos

- **`codigo`**: VARCHAR(20), UNIQUE, NOT NULL
- **`nombre`**: VARCHAR(100), NOT NULL
- **`descripcion`**: TEXT, NULLABLE
- **`duracionEstimadaMin`**: INT, NULLABLE
- **`precioBase`**: DECIMAL(10,2), NOT NULL
- **`categoria`**: ENUM, NOT NULL

### Relaciones (Foreign Keys)

El servicio puede estar relacionado con:
- **Citas** (`cita.id_servicio`)
- **Detalles de Servicio** (`detalle_servicio.id_servicio`)
- **Paquetes** (`paquete_servicio_item.id_servicio`)

⚠️ **No se puede eliminar un servicio si tiene relaciones activas.**

---

## 🔄 Flujo de Prueba Recomendado

1. **Autenticarse** y obtener token
2. **Listar todos los servicios** para ver catálogo actual
3. **Filtrar por categoría** (ej: baño)
4. **Buscar por nombre** (ej: "corte")
5. **Crear un nuevo servicio** y guardar su ID
6. **Consultar el servicio creado** por ID
7. **Actualizar el servicio** (cambiar precio)
8. **Verificar que se actualizó** correctamente
9. **Intentar crear servicio con código duplicado** (debe fallar)
10. **Eliminar el servicio de prueba**
11. **Probar casos de error** (IDs inexistentes, sin token, etc.)

---

## 📊 Tabla Resumen de Endpoints

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| `GET` | `/servicios` | Listar todos los servicios | ✅ |
| `GET` | `/servicios/{id}` | Obtener servicio por ID | ✅ |
| `GET` | `/servicios/buscar/{nombre}` | Buscar por nombre | ✅ |
| `GET` | `/servicios/categoria/{categoria}` | Filtrar por categoría | ✅ |
| `POST` | `/servicios` | Crear nuevo servicio | ✅ |
| `PUT` | `/servicios/{id}` | Actualizar servicio | ✅ |
| `DELETE` | `/servicios/{id}` | Eliminar servicio | ✅ |

---

## 💡 Tips para Testing

### 1. Usar Variables en Postman

Guarda IDs en variables para reutilizar:

```javascript
// En el script "Tests" de una respuesta:
pm.environment.set("servicioId", pm.response.json().datos.idServicio);

// Luego usa {{servicioId}} en otros requests
```

### 2. Colección Ordenada

Organiza las pruebas en este orden:
1. Login (obtener token)
2. GET (consultas sin modificar datos)
3. POST (crear datos de prueba)
4. PUT (actualizar datos)
5. DELETE (limpiar datos de prueba)

### 3. Scripts de Validación

Agrega validaciones automáticas en Postman:

```javascript
// En tab "Tests":
pm.test("Status code es 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Respuesta exitosa", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.exito).to.be.true;
});

pm.test("Servicio tiene código", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.datos.codigo).to.exist;
});
```

---

**Preparado por:** Backend Team  
**Fecha:** 2025-11-23  
**Versión:** 1.0  
**Para usar con:** Postman, Insomnia o similar
