# 📡 API REST - TeranVet - Referencia de Endpoints

## Base URL
```
http://localhost:8080/api
```

## Estructura de Respuesta

Todos los endpoints devuelven respuestas con formato estándar:

```json
{
  "exito": true/false,
  "mensaje": "Descripción de la operación",
  "datos": {},
  "error": null
}
```

---

## 👥 CLIENTES

### Listar todos los clientes
```http
GET /api/clientes HTTP/1.1
```

### Obtener cliente por ID
```http
GET /api/clientes/1 HTTP/1.1
```

### Buscar clientes por término
```http
GET /api/clientes/buscar/juan HTTP/1.1
```

### Crear nuevo cliente
```http
POST /api/clientes HTTP/1.1
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellido": "Ramírez",
  "dniRuc": "12345678",
  "email": "carlos@email.com",
  "telefono": "987654321",
  "direccion": "Av. Los Olivos 123",
  "preferencias": "{\"comunicacion\": \"email\"}"
}
```

### Actualizar cliente
```http
PUT /api/clientes/1 HTTP/1.1
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellido": "Ramírez López",
  "email": "carlos.nuevo@email.com",
  "telefono": "987654321",
  "direccion": "Av. Los Olivos 456"
}
```

### Eliminar cliente
```http
DELETE /api/clientes/1 HTTP/1.1
```

---

## 🐾 MASCOTAS

### Listar todas las mascotas
```http
GET /api/mascotas HTTP/1.1
```

### Obtener mascota por ID
```http
GET /api/mascotas/1 HTTP/1.1
```

### Obtener mascotas de un cliente
```http
GET /api/mascotas/cliente/1 HTTP/1.1
```

### Buscar mascotas
```http
GET /api/mascotas/buscar/firulais HTTP/1.1
```

### Registrar nueva mascota
```http
POST /api/mascotas HTTP/1.1
Content-Type: application/json

{
  "idCliente": 1,
  "nombre": "Firulais",
  "especie": "perro",
  "raza": "Labrador",
  "sexo": "macho",
  "fechaNacimiento": "2020-05-15",
  "microchip": "ABC123456",
  "observaciones": "Alérgico a ciertos medicamentos"
}
```

### Actualizar mascota
```http
PUT /api/mascotas/1 HTTP/1.1
Content-Type: application/json

{
  "nombre": "Firulais",
  "especie": "perro",
  "raza": "Labrador Retriever",
  "sexo": "macho"
}
```

### Eliminar mascota
```http
DELETE /api/mascotas/1 HTTP/1.1
```

---

## 📅 CITAS

### Listar todas las citas
```http
GET /api/citas HTTP/1.1
```

### Obtener cita por ID
```http
GET /api/citas/1 HTTP/1.1
```

### Obtener próximas citas de cliente
```http
GET /api/citas/cliente/1/proximas HTTP/1.1
```

### Obtener todas las citas de cliente
```http
GET /api/citas/cliente/1 HTTP/1.1
```

### Crear nueva cita
```http
POST /api/citas HTTP/1.1
Content-Type: application/json

{
  "idMascota": 1,
  "idCliente": 1,
  "idSucursal": 1,
  "idServicio": 1,
  "fechaProgramada": "2025-11-20T10:30:00",
  "modalidad": "presencial",
  "notas": "Cliente solicita baño regular"
}
```

### Reprogramar cita
```http
PUT /api/citas/1/reprogramar?nuevaFecha=2025-11-25T14:00:00 HTTP/1.1
```

### Cancelar cita
```http
PUT /api/citas/1/cancelar HTTP/1.1
```

### Confirmar asistencia
```http
PUT /api/citas/1/confirmar-asistencia HTTP/1.1
```

### Marcar como no-show
```http
PUT /api/citas/1/no-show HTTP/1.1
```

---

## 🛠️ SERVICIOS

### Listar todos los servicios
```http
GET /api/servicios HTTP/1.1
```

### Obtener servicio por ID
```http
GET /api/servicios/1 HTTP/1.1
```

### Obtener servicios por categoría
```http
GET /api/servicios/categoria/baño HTTP/1.1
```

Categorías disponibles: `baño`, `corte`, `dental`, `paquete`, `otro`

### Buscar servicios por nombre
```http
GET /api/servicios/buscar/baño HTTP/1.1
```

### Crear nuevo servicio
```http
POST /api/servicios HTTP/1.1
Content-Type: application/json

{
  "codigo": "BAÑ001",
  "nombre": "Baño Básico",
  "descripcion": "Baño con shampoo regular",
  "duracionEstimadaMin": 45,
  "precioBase": 50.00,
  "categoria": "baño"
}
```

### Actualizar servicio
```http
PUT /api/servicios/1 HTTP/1.1
Content-Type: application/json

{
  "nombre": "Baño Premium",
  "precioBase": 75.00
}
```

### Eliminar servicio
```http
DELETE /api/servicios/1 HTTP/1.1
```

---

## ⚡ ATENCIONES

### Listar todas las atenciones
```http
GET /api/atenciones HTTP/1.1
```

### Obtener atención por ID
```http
GET /api/atenciones/1 HTTP/1.1
```

### Obtener cola actual de sucursal
```http
GET /api/atenciones/cola/1 HTTP/1.1
```

### Obtener atenciones de cliente
```http
GET /api/atenciones/cliente/1 HTTP/1.1
```

### Crear atención desde cita
```http
POST /api/atenciones/desde-cita?idCita=1&idGroomer=1&idSucursal=1&tiempoEstimadoInicio=2025-11-20T10:30:00&tiempoEstimadoFin=2025-11-20T11:15:00 HTTP/1.1
```

### Crear atención walk-in
```http
POST /api/atenciones/walk-in?idMascota=1&idCliente=1&idGroomer=1&idSucursal=1&tiempoEstimadoInicio=2025-11-20T10:30:00&tiempoEstimadoFin=2025-11-20T11:15:00&observaciones=Mascota%20agresiva HTTP/1.1
```

### Cambiar estado de atención
```http
PUT /api/atenciones/1/estado?nuevoEstado=en_servicio HTTP/1.1
```

Estados disponibles: `en_espera`, `en_servicio`, `pausado`, `terminado`

### Terminar atención
```http
PUT /api/atenciones/1/terminar HTTP/1.1
```

---

## 📝 Códigos de Respuesta HTTP

| Código | Significado |
|--------|------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Datos inválidos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

---

## 📋 Ejemplo de Flujo Completo

### 1. Crear Cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Pérez","dniRuc":"12345678","email":"juan@mail.com","telefono":"987654321","direccion":"Calle Principal 100"}'
```

### 2. Registrar Mascota
```bash
curl -X POST http://localhost:8080/api/mascotas \
  -H "Content-Type: application/json" \
  -d '{"idCliente":1,"nombre":"Firulais","especie":"perro","raza":"Labrador","sexo":"macho"}'
```

### 3. Crear Cita
```bash
curl -X POST http://localhost:8080/api/citas \
  -H "Content-Type: application/json" \
  -d '{"idMascota":1,"idCliente":1,"idSucursal":1,"idServicio":1,"fechaProgramada":"2025-11-20T10:30:00","modalidad":"presencial"}'
```

### 4. Crear Atención
```bash
curl -X POST 'http://localhost:8080/api/atenciones/desde-cita?idCita=1&idGroomer=1&idSucursal=1&tiempoEstimadoInicio=2025-11-20T10:30:00&tiempoEstimadoFin=2025-11-20T11:15:00'
```

---

## 🔒 Notas de Seguridad

- Los endpoints están configurados para CORS
- Se recomienda implementar JWT para autenticación
- Las respuestas incluyen validación de errores
- Siempre usar HTTPS en producción

---

**Última actualización:** Noviembre 2025
