# 📘 Documentación Completa: Endpoints de Atenciones

**Proyecto:** Veterinaria SpringBoot  
**Versión API:** 1.0  
**Base URL:** `http://localhost:8080`  
**Última Actualización:** 2025-11-24

---

## 📑 Índice

1. [Endpoints de Atenciones](#endpoints-de-atenciones)
2. [Endpoints de Detalles de Servicio](#endpoints-de-detalles-de-servicio)
3. [Respuestas y Estructuras](#respuestas-y-estructuras)
4. [Guía de Uso por Escenario](#guía-de-uso-por-escenario)
5. [Códigos de Estado HTTP](#códigos-de-estado-http)

---

## 🔵 Endpoints de Atenciones

**Base Path:** `/api/atenciones`

### 1. Obtener Todas las Atenciones

```http
GET /api/atenciones
Authorization: Bearer {token}
```

**Descripción:** Obtiene la lista completa de todas las atenciones en el sistema.

**Respuesta Exitosa:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Atenciones obtenidas exitosamente",
  "datos": [
    {
      "idAtencion": 1,
      "mascota": { "idMascota": 1, "nombre": "Max", ... },
      "cliente": { "idCliente": 1, "nombre": "Juan", ... },
      "groomer": { "idGroomer": 1, "nombre": "María", ... },
      "estado": "en_servicio",
      "turnoNum": 1,
      "prioridad": 2,
      ...
    }
  ],
  "error": null
}
```

**Respuesta de Error:** HTTP 500
```json
{
  "exito": false,
  "mensaje": "Error al obtener atenciones",
  "datos": null,
  "error": "Mensaje de error"
}
```

---

### 2. Obtener Atención por ID

```http
GET /api/atenciones/{id}
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `id` (Integer, requerido): ID de la atención

**Descripción:** Obtiene una atención específica con toda su información relacionada.

**Ejemplo:**
```http
GET /api/atenciones/10
```

**Respuesta Exitosa:** HTTP 200 OK
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
      ...
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
    "observaciones": null,
    "createdAt": "2025-11-26T09:55:00",
    "updatedAt": "2025-11-26T09:55:00"
  },
  "error": null
}
```

**⚠️ Nota:** La respuesta incluye TODA la información anidada (cita completa, mascota, cliente, groomer, sucursal). Es extremadamente verbosa.

**Respuesta de Error:** HTTP 404
```json
{
  "exito": false,
  "mensaje": "Atención no encontrada",
  "datos": null,
  "error": "Mensaje de error"
}
```

---

### 3. Obtener Cola de Atenciones por Sucursal

```http
GET /api/atenciones/cola/{idSucursal}
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `idSucursal` (Integer, requerido): ID de la sucursal

**Descripción:** Obtiene todas las atenciones activas de una sucursal (no terminadas).

**Ejemplo:**
```http
GET /api/atenciones/cola/1
```

**Respuesta Exitosa:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Cola obtenida exitosamente",
  "datos": [
    {
      "idAtencion": 20,
      "mascota": { "idMascota": 1, "nombre": "Max" },
      "cliente": { "idCliente": 1, "nombre": "Juan Pérez" },
      "groomer": { "idGroomer": 1, "nombre": "María González" },
      "estado": "en_espera",
      "turnoNum": 1,
      "prioridad": 1,
      "tiempoEstimadoInicio": "2025-11-26T10:00:00",
      "tiempoRealInicio": null,
      "tiempoRealFin": null
    },
    {
      "idAtencion": 21,
      "estado": "en_servicio",
      "turnoNum": 2,
      ...
    }
  ],
  "error": null
}
```

**Uso Típico:** Vista Kanban en frontend, auto-refresh cada 30 segundos.

---

### 4. Obtener Atenciones de un Cliente

```http
GET /api/atenciones/cliente/{idCliente}
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `idCliente` (Integer, requerido): ID del cliente

**Descripción:** Obtiene el historial de todas las atenciones de un cliente específico.

**Ejemplo:**
```http
GET /api/atenciones/cliente/1
```

**Respuesta Exitosa:** HTTP 200 OK
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
      "tiempoRealFin": "2025-11-20T11:15:00",
      ...
    }
  ],
  "error": null
}
```

**Uso Típico:** Historial de atenciones en el perfil del cliente.

---

### 5. Crear Atención desde Cita

```http
POST /api/atenciones/desde-cita
Authorization: Bearer {token}
Content-Type: application/x-www-form-urlencoded
```

**⚠️ IMPORTANTE:** Este endpoint usa **form-urlencoded**, NO JSON.

**Parámetros (form-urlencoded):**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `idCita` | Integer | ✅ Sí | ID de la cita a atender |
| `idGroomer` | Integer | ✅ Sí | ID del groomer asignado |
| `idSucursal` | Integer | ✅ Sí | ID de la sucursal |
| `turnoNum` | Integer | ✅ Sí | Número de turno del día |
| `tiempoEstimadoInicio` | DateTime | ✅ Sí | Formato: `2025-11-26T10:00:00` |
| `tiempoEstimadoFin` | DateTime | ✅ Sí | Formato: `2025-11-26T11:30:00` |
| `prioridad` | Integer | ✅ Sí | 1 (alta) a 5 (baja) |

**Ejemplo (Postman):**
```
POST /api/atenciones/desde-cita
Body: x-www-form-urlencoded

idCita=15
&idGroomer=1
&idSucursal=1
&turnoNum=1
&tiempoEstimadoInicio=2025-11-26T10:00:00
&tiempoEstimadoFin=2025-11-26T11:30:00
&prioridad=1
```

**Ejemplo (cURL):**
```bash
curl -X POST "http://localhost:8080/api/atenciones/desde-cita" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "idCita=15&idGroomer=1&idSucursal=1&turnoNum=1&tiempoEstimadoInicio=2025-11-26T10:00:00&tiempoEstimadoFin=2025-11-26T11:30:00&prioridad=1"
```

**Respuesta Exitosa:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Atención creada exitosamente desde la cita",
  "datos": null,
  "error": null
}
```

**Efecto en Backend:**
- Crea registro en tabla `atencion` con estado `en_espera`
- Cambia estado de la cita de `reservada` a `atendido`
- Ejecuta stored procedure `crear_atencion_desde_cita`

**Respuesta de Error:** HTTP 400
```json
{
  "exito": false,
  "mensaje": "Error al crear atención",
  "datos": null,
  "error": "Cita no encontrada" // u otro error
}
```

---

### 6. Crear Atención Walk-In (Sin Cita)

```http
POST /api/atenciones/walk-in
Authorization: Bearer {token}
Content-Type: application/x-www-form-urlencoded
```

**⚠️ IMPORTANTE:** Este endpoint también usa **form-urlencoded**, NO JSON.

**Parámetros (form-urlencoded):**

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `idMascota` | Integer | ✅ Sí | ID de la mascota |
| `idCliente` | Integer | ✅ Sí | ID del cliente |
| `idGroomer` | Integer | ✅ Sí | ID del groomer asignado |
| `idSucursal` | Integer | ✅ Sí | ID de la sucursal |
| `turnoNum` | Integer | ✅ Sí | Número de turno del día |
| `tiempoEstimadoInicio` | DateTime | ✅ Sí | Formato: `2025-11-26T12:00:00` |
| `tiempoEstimadoFin` | DateTime | ✅ Sí | Formato: `2025-11-26T13:00:00` |
| `prioridad` | Integer | ✅ Sí | 0-5 (0 es baja para walk-in) |
| `observaciones` | String | ❌ No | Notas adicionales |

**Ejemplo (Postman):**
```
POST /api/atenciones/walk-in
Body: x-www-form-urlencoded

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

**Respuesta Exitosa:** HTTP 201 CREATED
```json
{
  "exito": true,
  "mensaje": "Atención walk-in creada exitosamente",
  "datos": null,
  "error": null
}
```

**Uso Típico:** Cuando un cliente llega sin cita programada.

---

### 7. Cambiar Estado de Atención

```http
PUT /api/atenciones/{id}/estado?nuevoEstado={estado}
Authorization: Bearer {token}
```

**Parámetros:**
- `id` (Integer, ruta, requerido): ID de la atención
- `nuevoEstado` (String, query, requerido): Nuevo estado

**Estados Válidos:**
- `en_espera`
- `en_servicio`
- `pausado`
- `terminado` (⚠️ Mejor usar el endpoint `/terminar`)

**Transiciones Válidas:**
- `en_espera` → `en_servicio` ✅
- `en_servicio` → `pausado` ✅
- `pausado` → `en_servicio` ✅
- `en_servicio` → `terminado` ✅ (usar `/terminar`)

**Ejemplo:**
```http
PUT /api/atenciones/10/estado?nuevoEstado=en_servicio
```

**Respuesta Exitosa:** HTTP 200 OK
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
- Cambia el estado en la tabla `atencion`
- Si cambia a `en_servicio`, establece `tiempoRealInicio`
- Ejecuta stored procedure `actualizar_estado_atencion`

**Respuesta de Error:** HTTP 400
```json
{
  "exito": false,
  "mensaje": "Error al cambiar estado",
  "datos": null,
  "error": "Estado inválido"
}
```

---

### 8. Terminar Atención

```http
PUT /api/atenciones/{id}/terminar
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `id` (Integer, requerido): ID de la atención

**Descripción:** Marca la atención como terminada y registra la hora de finalización.

**Ejemplo:**
```http
PUT /api/atenciones/10/terminar
```

**Respuesta Exitosa:** HTTP 200 OK
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
- Cambia estado a `terminado`
- Establece `tiempoRealFin` con la hora actual
- Calcula duración real del servicio
- Ejecuta stored procedure `actualizar_estado_atencion`

**⚠️ Importante:** Después de terminar, ya no se puede cambiar el estado ni los detalles.

**Respuesta de Error:** HTTP 400
```json
{
  "exito": false,
  "mensaje": "Error al terminar atención",
  "datos": null,
  "error": "La atención ya está terminada"
}
```

---

## 🟢 Endpoints de Detalles de Servicio

**Base Path:** `/api/atenciones/{idAtencion}/detalles`

### 9. Obtener Detalles de una Atención

```http
GET /api/atenciones/{idAtencion}/detalles
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `idAtencion` (Integer, requerido): ID de la atención

**Descripción:** Obtiene la lista de todos los servicios realizados en una atención.

**Ejemplo:**
```http
GET /api/atenciones/10/detalles
```

**Respuesta Exitosa:** HTTP 200 OK
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
        ...
      },
      "cantidad": 1,
      "precioUnitario": 35.00,
      "subtotal": 35.00,
      "observaciones": "Baño completo con champú especial",
      "createdAt": "2025-11-26T11:00:00"
    },
    {
      "idDetalle": 2,
      "servicio": { "nombre": "Corte de Uñas", ... },
      "cantidad": 1,
      "precioUnitario": 15.00,
      "subtotal": 15.00,
      ...
    }
  ],
  "error": null
}
```

**Uso Típico:** Mostrar tabla de servicios realizados en la pantalla de atención.

---

### 10. Obtener un Detalle Específico

```http
GET /api/atenciones/{idAtencion}/detalles/{idDetalle}
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `idAtencion` (Integer, requerido): ID de la atención
- `idDetalle` (Integer, requerido): ID del detalle

**Ejemplo:**
```http
GET /api/atenciones/10/detalles/1
```

**Respuesta Exitosa:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Detalle encontrado",
  "datos": {
    "idDetalle": 1,
    "atencion": { "idAtencion": 10 },
    "servicio": { ... },
    "cantidad": 1,
    "precioUnitario": 35.00,
    "subtotal": 35.00,
    "observaciones": "Baño completo",
    "createdAt": "2025-11-26T11:00:00"
  },
  "error": null
}
```

**Respuesta de Error:** HTTP 404
```json
{
  "exito": false,
  "mensaje": "Detalle no encontrado",
  "datos": null,
  "error": null
}
```

---

### 11. Agregar Detalle de Servicio

```http
POST /api/atenciones/{idAtencion}/detalles
Authorization: Bearer {token}
Content-Type: application/json
```

**⚠️ IMPORTANTE:** Este endpoint usa **JSON**, NO form-urlencoded.

**Parámetros de Ruta:**
- `idAtencion` (Integer, requerido): ID de la atención

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

**Campos del Body:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `servicio.idServicio` | Integer | ✅ Sí | ID del servicio realizado |
| `cantidad` | Integer | ✅ Sí | Cantidad de veces que se realizó |
| `precioUnitario` | Decimal | ✅ Sí | Precio por unidad |
| `subtotal` | Decimal | ✅ Sí | Total (cantidad × precioUnitario) |
| `observaciones` | String | ❌ No | Notas sobre el servicio |

**Ejemplo Completo:**
```http
POST /api/atenciones/10/detalles
Content-Type: application/json

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

**Respuesta Exitosa:** HTTP 201 CREATED
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
      "precioBase": 35.00,
      ...
    },
    "cantidad": 1,
    "precioUnitario": 35.00,
    "subtotal": 35.00,
    "observaciones": "Baño completo con champú especial",
    "createdAt": "2025-11-26T11:26:00"
  },
  "error": null
}
```

**⚠️ CRÍTICO:** Sin agregar detalles de servicio, la factura tendrá total S/ 0.00.

**Respuesta de Error:** HTTP 400
```json
{
  "exito": false,
  "mensaje": "Error al crear detalle",
  "datos": null,
  "error": "Mensaje de error"
}
```

---

### 12. Actualizar Detalle de Servicio

```http
PUT /api/atenciones/{idAtencion}/detalles/{idDetalle}
Authorization: Bearer {token}
Content-Type: application/json
```

**Parámetros de Ruta:**
- `idAtencion` (Integer, requerido): ID de la atención
- `idDetalle` (Integer, requerido): ID del detalle a actualizar

**Body (JSON):**
```json
{
  "servicio": {
    "idServicio": 1
  },
  "cantidad": 2,
  "precioUnitario": 35.00,
  "subtotal": 70.00,
  "observaciones": "Baño doble"
}
```

**Ejemplo:**
```http
PUT /api/atenciones/10/detalles/5
Content-Type: application/json

{
  "cantidad": 2,
  "subtotal": 70.00,
  "observaciones": "Baño doble"
}
```

**Respuesta Exitosa:** HTTP 200 OK
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

---

### 13. Eliminar Detalle de Servicio

```http
DELETE /api/atenciones/{idAtencion}/detalles/{idDetalle}
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `idAtencion` (Integer, requerido): ID de la atención
- `idDetalle` (Integer, requerido): ID del detalle a eliminar

**Ejemplo:**
```http
DELETE /api/atenciones/10/detalles/5
```

**Respuesta Exitosa:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Detalle eliminado correctamente",
  "datos": null,
  "error": null
}
```

**Uso Típico:** Corregir servicios agregados por error.

---

### 14. Obtener Subtotal de la Atención

```http
GET /api/atenciones/{idAtencion}/detalles/subtotal
Authorization: Bearer {token}
```

**Parámetros de Ruta:**
- `idAtencion` (Integer, requerido): ID de la atención

**Descripción:** Calcula y retorna la suma de todos los subtotales de los detalles.

**Ejemplo:**
```http
GET /api/atenciones/10/detalles/subtotal
```

**Respuesta Exitosa:** HTTP 200 OK
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
Detalle 1: S/ 35.00 (Baño)
Detalle 2: S/ 15.00 (Corte de Uñas)
---------------------------
Subtotal: S/ 50.00
```

**Uso Típico:** Mostrar el total antes de terminar la atención.

---

## 📊 Respuestas y Estructuras

### Estructura de ApiResponse

Todas las respuestas siguen esta estructura estándar:

```json
{
  "exito": true/false,
  "mensaje": "Descripción del resultado",
  "datos": { ... },  // o null
  "error": "Mensaje de error"  // solo si exito = false
}
```

### Estados de Atención

```typescript
enum Estado {
  en_espera,    // Esperando a ser atendido
  en_servicio,  // Siendo atendido actualmente
  pausado,      // Servicio pausado temporalmente
  terminado     // Servicio completado
}
```

### Niveles de Prioridad

```
1 = Urgente (rojo) - Emergencia
2 = Alta (naranja) - Cliente VIP
3 = Normal (amarillo) - Regular
4 = Baja (azul) - Puede esperar
5 = Muy Baja (gris) - Sin prisa
0 = Walk-in
```

---

## 🎯 Guía de Uso por Escenario

### Escenario 1: Cliente con Cita Llega

```
1. GET /api/citas/{id}
   → Verificar que la cita existe y está "confirmada"

2. POST /api/atenciones/desde-cita
   → Crear atención con estado "en_espera"
   → Cita cambia a estado "atendido"

3. GET /api/atenciones/cola/{sucursalId}
   → Ver atención en cola

4. PUT /api/atenciones/{id}/estado?nuevoEstado=en_servicio
   → Iniciar servicio cuando llegue el turno

5. POST /api/atenciones/{id}/detalles (repetir por cada servicio)
   → Agregar servicios realizados

6. PUT /api/atenciones/{id}/terminar
   → Terminar servicio

7. POST /api/facturas
   → Generar factura (ver manual de facturas)
```

---

### Escenario 2: Cliente Walk-In (Sin Cita)

```
1. POST /api/atenciones/walk-in
   → Crear atención directamente

2. GET /api/atenciones/cola/{sucursalId}
   → Ver en cola

3-7. (igual que escenario 1 desde paso 4)
```

---

### Escenario 3: Atender a Mascota (Vista Groomer)

```
1. GET /api/atenciones/cola/{sucursalId}
   → Ver atenciones pendientes

2. PUT /api/atenciones/{id}/estado?nuevoEstado=en_servicio
   → Comenzar atención

3. POST /api/atenciones/{id}/detalles
   → Agregar cada servicio realizado

4. GET /api/atenciones/{id}/detalles/subtotal
   → Ver total acumulado

5. PUT /api/atenciones/{id}/terminar
   → Finalizar atención
```

---

### Escenario 4: Ver Historial de Cliente

```
1. GET /api/atenciones/cliente/{idCliente}
   → Obtener todas las atenciones del cliente

2. GET /api/atenciones/{id}
   → Ver detalles de una atención específica

3. GET /api/atenciones/{id}/detalles
   → Ver servicios de esa atención
```

---

## 📋 Códigos de Estado HTTP

| Código | Significado | Cuándo se retorna |
|--------|-------------|-------------------|
| 200 OK | Éxito | GET, PUT, DELETE exitosos |
| 201 Created | Creado | POST exitoso |
| 400 Bad Request | Solicitud inválida | Parámetros faltantes/inválidos |
| 401 Unauthorized | No autorizado | Token JWT inválido/expirado |
| 404 Not Found | No encontrado | Recurso no existe |
| 500 Internal Server Error | Error del servidor | Error inesperado en backend |

---

## ⚠️ Notas Importantes

### 1. Autenticación

**Todos los endpoints requieren JWT:**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 2. Content-Type

| Endpoint | Content-Type |
|----------|--------------|
| POST `/desde-cita` | `application/x-www-form-urlencoded` |
| POST `/walk-in` | `application/x-www-form-urlencoded` |
| POST `/detalles` | `application/json` |
| PUT `/detalles/{id}` | `application/json` |

### 3. Formato de Fechas

**Siempre usar ISO 8601:**
```
2025-11-26T10:00:00
```

**❌ NO usar:**
```
26/11/2025 10:00
11-26-2025 10:00:00
```

### 4. Orden de Operaciones Crítico

```
✅ CORRECTO:
1. Terminar atención
2. Agregar detalles de servicio
3. Generar factura

❌ INCORRECTO:
1. Agregar detalles
2. Terminar atención
3. Generar factura
```

### 5. Validaciones del Backend

El backend validará:
- ✅ Que la cita/mascota/cliente/groomer existan
- ✅ Que las fechas estén en formato correcto
- ✅ Que la prioridad esté entre 0-5
- ❌ **NO valida** disponibilidad del groomer

### 6. Respuestas Verbosas

Los endpoints de GET retornan MUCHA información anidada:
- Cita completa con mascota, cliente, servicio, sucursal
- Información duplicada en varios niveles
- Útil para tener todo el contexto, pero verbose

**Sugerencia:** Crear mappers en frontend para extraer solo lo necesario.

---

## 🔄 Flujo Completo Recomendado

```mermaid
graph TD
    A[Cliente llega] --> B{¿Tiene cita?}
    B -->|Sí| C[POST /desde-cita]
    B -->|No| D[POST /walk-in]
    C --> E[Atención en cola]
    D --> E
    E --> F[PUT /estado en_servicio]
    F --> G[POST /detalles múltiples]
    G --> H{¿Más servicios?}
    H -->|Sí| G
    H -->|No| I[GET /subtotal]
    I --> J[PUT /terminar]
    J --> K[POST /facturas]
    K --> L[POST /pagos]
    L --> M[✅ Completado]
```

---

## 📞 Resumen de Endpoints

| # | Método | Endpoint | Descripción |
|---|--------|----------|-------------|
| 1 | GET | `/api/atenciones` | Listar todas |
| 2 | GET | `/api/atenciones/{id}` | Obtener una |
| 3 | GET | `/api/atenciones/cola/{sucursalId}` | Cola activa |
| 4 | GET | `/api/atenciones/cliente/{idCliente}` | Historial cliente |
| 5 | POST | `/api/atenciones/desde-cita` | Crear desde cita |
| 6 | POST | `/api/atenciones/walk-in` | Crear walk-in |
| 7 | PUT | `/api/atenciones/{id}/estado` | Cambiar estado |
| 8 | PUT | `/api/atenciones/{id}/terminar` | Terminar |
| 9 | GET | `/api/atenciones/{id}/detalles` | Listar detalles |
| 10 | GET | `/api/atenciones/{id}/detalles/{idDetalle}` | Obtener detalle |
| 11 | POST | `/api/atenciones/{id}/detalles` | Agregar detalle |
| 12 | PUT | `/api/atenciones/{id}/detalles/{idDetalle}` | Actualizar detalle |
| 13 | DELETE | `/api/atenciones/{id}/detalles/{idDetalle}` | Eliminar detalle |
| 14 | GET | `/api/atenciones/{id}/detalles/subtotal` | Calcular subtotal |

**Total:** 14 endpoints disponibles

---

**Documentado por:** Backend Team  
**Para:** Frontend Team  
**Versión:** 1.0  
**Última actualización:** 2025-11-24
