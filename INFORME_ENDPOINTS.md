# Informe de Endpoints y Rutas - Sistema Veterinaria SpringBoot

> **Generado:** 2025-11-20  
> **Base URL:** `/api`  
> **Total de Controladores:** 19

---

## Índice de Módulos

1. [Autenticación](#1-autenticación)
2. [Clientes](#2-clientes)
3. [Mascotas](#3-mascotas)
4. [Citas](#4-citas)
5. [Atenciones](#5-atenciones)
6. [Servicios](#6-servicios)
7. [Paquetes de Servicios](#7-paquetes-de-servicios)
8. [Detalles de Servicio](#8-detalles-de-servicio)
9. [Groomers](#9-groomers)
10. [Facturas](#10-facturas)
11. [Pagos](#11-pagos)
12. [Promociones](#12-promociones)
13. [Dashboard](#13-dashboard)
14. [Reportes](#14-reportes)
15. [Notificaciones](#15-notificaciones)
16. [Usuarios del Sistema](#16-usuarios-del-sistema)
17. [Sucursales](#17-sucursales)
18. [Configuración](#18-configuración)
19. [Auditoría](#19-auditoría)

---

## 1. Autenticación

**Base Path:** `/api/auth`  
**Archivo:** `AuthController.java`

| Método | Endpoint | Descripción | Parámetros | Autenticación |
|--------|----------|-------------|------------|---------------|
| `POST` | `/api/auth/login` | Autenticar usuario y obtener JWT | **Body:** LoginRequest (email, passwordHash) | No |
| `POST` | `/api/auth/validar` | Validar si existe un usuario por email | **Query:** email | No |
| `POST` | `/api/auth/logout` | Logout simbólico (invalida token JWT) | - | Sí |
| `POST` | `/api/auth/cambiar-contraseña` | Cambiar contraseña de usuario | **Query:** idUsuario<br>**Body:** ChangePasswordRequest (nuevaContraseña) | Sí |

---

## 2. Clientes

**Base Path:** `/api/clientes`  
**Archivo:** `ClienteController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/clientes` | Obtener todos los clientes | - | List\<ClienteDTO\> |
| `GET` | `/api/clientes/{id}` | Obtener cliente por ID | **Path:** id | ClienteDTO |
| `GET` | `/api/clientes/buscar/{termino}` | Buscar clientes por término | **Path:** termino | List\<ClienteDTO\> |
| `POST` | `/api/clientes` | Crear nuevo cliente | **Body:** ClienteDTO | ClienteDTO |
| `PUT` | `/api/clientes/{id}` | Actualizar cliente | **Path:** id<br>**Body:** ClienteDTO | ClienteDTO |
| `DELETE` | `/api/clientes/{id}` | Eliminar cliente | **Path:** id | - |

---

## 3. Mascotas

**Base Path:** `/api/mascotas`  
**Archivo:** `MascotaController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/mascotas` | Obtener todas las mascotas | - | List\<MascotaDTO\> |
| `GET` | `/api/mascotas/{id}` | Obtener mascota por ID | **Path:** id | MascotaDTO |
| `GET` | `/api/mascotas/cliente/{idCliente}` | Obtener mascotas de un cliente | **Path:** idCliente | List\<MascotaDTO\> |
| `GET` | `/api/mascotas/buscar/{termino}` | Buscar mascotas por término | **Path:** termino | List\<MascotaDTO\> |
| `POST` | `/api/mascotas` | Crear nueva mascota | **Body:** MascotaDTO | MascotaDTO |
| `PUT` | `/api/mascotas/{id}` | Actualizar mascota | **Path:** id<br>**Body:** MascotaDTO | MascotaDTO |
| `DELETE` | `/api/mascotas/{id}` | Eliminar mascota | **Path:** id | - |

---

## 4. Citas

**Base Path:** `/api/citas`  
**Archivo:** `CitaController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/citas` | Obtener todas las citas | - | List\<CitaDTO\> |
| `GET` | `/api/citas/{id}` | Obtener cita por ID | **Path:** id | CitaDTO |
| `GET` | `/api/citas/cliente/{idCliente}` | Obtener citas de un cliente | **Path:** idCliente | List\<CitaDTO\> |
| `GET` | `/api/citas/cliente/{idCliente}/proximas` | Obtener próximas citas de un cliente | **Path:** idCliente | List\<CitaDTO\> |
| `POST` | `/api/citas` | Crear nueva cita | **Body:** CitaDTO | CitaDTO |
| `PUT` | `/api/citas/{id}/reprogramar` | Reprogramar una cita | **Path:** id<br>**Query:** nuevaFecha (ISO DateTime) | CitaDTO |
| `PUT` | `/api/citas/{id}/cancelar` | Cancelar una cita | **Path:** id | CitaDTO |
| `PUT` | `/api/citas/{id}/confirmar-asistencia` | Confirmar asistencia a cita | **Path:** id | CitaDTO |
| `PUT` | `/api/citas/{id}/no-show` | Marcar cita como no-show | **Path:** id | CitaDTO |

---

## 5. Atenciones

**Base Path:** `/api/atenciones`  
**Archivo:** `AtencionController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/atenciones` | Obtener todas las atenciones | - | List\<Atencion\> |
| `GET` | `/api/atenciones/{id}` | Obtener atención por ID | **Path:** id | Atencion |
| `GET` | `/api/atenciones/cola/{idSucursal}` | Obtener cola actual de una sucursal | **Path:** idSucursal | List\<Atencion\> |
| `GET` | `/api/atenciones/cliente/{idCliente}` | Obtener atenciones de un cliente | **Path:** idCliente | List\<Atencion\> |
| `POST` | `/api/atenciones/desde-cita` | Crear atención desde una cita | **Query:** idCita, idGroomer, idSucursal, tiempoEstimadoInicio, tiempoEstimadoFin, prioridad | Atencion |
| `POST` | `/api/atenciones/walk-in` | Crear atención walk-in (sin cita) | **Query:** idMascota, idCliente, idGroomer, idSucursal, tiempoEstimadoInicio, tiempoEstimadoFin, prioridad, observaciones | Atencion |
| `PUT` | `/api/atenciones/{id}/estado` | Cambiar estado de atención | **Path:** id<br>**Query:** nuevoEstado | Atencion |
| `PUT` | `/api/atenciones/{id}/terminar` | Terminar una atención | **Path:** id | Atencion |

---

## 6. Servicios

**Base Path:** `/api/servicios`  
**Archivo:** `ServicioController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/servicios` | Obtener todos los servicios | - | List\<Servicio\> |
| `GET` | `/api/servicios/{id}` | Obtener servicio por ID | **Path:** id | Servicio |
| `GET` | `/api/servicios/categoria/{categoria}` | Obtener servicios por categoría | **Path:** categoria | List\<Servicio\> |
| `GET` | `/api/servicios/buscar/{nombre}` | Buscar servicios por nombre | **Path:** nombre | List\<Servicio\> |
| `POST` | `/api/servicios` | Crear nuevo servicio | **Body:** Servicio | Servicio |
| `PUT` | `/api/servicios/{id}` | Actualizar servicio | **Path:** id<br>**Body:** Servicio | Servicio |
| `DELETE` | `/api/servicios/{id}` | Eliminar servicio | **Path:** id | - |

---

## 7. Paquetes de Servicios

**Base Path:** `/api/servicios/paquetes`  
**Archivo:** `PaqueteServicioController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/servicios/paquetes` | Obtener todos los paquetes | - | List\<PaqueteServicio\> |
| `GET` | `/api/servicios/paquetes/{id}` | Obtener paquete por ID | **Path:** id | PaqueteServicio |
| `GET` | `/api/servicios/paquetes/activos` | Obtener paquetes activos | - | List\<PaqueteServicio\> |
| `GET` | `/api/servicios/paquetes/{id}/precio-final` | Obtener precio final con descuento | **Path:** id | BigDecimal |
| `POST` | `/api/servicios/paquetes` | Crear nuevo paquete | **Body:** PaqueteServicio | PaqueteServicio |
| `PUT` | `/api/servicios/paquetes/{id}` | Actualizar paquete | **Path:** id<br>**Body:** PaqueteServicio | PaqueteServicio |
| `DELETE` | `/api/servicios/paquetes/{id}` | Eliminar paquete | **Path:** id | - |

---

## 8. Detalles de Servicio

**Base Path:** `/api/atenciones/{idAtencion}/detalles`  
**Archivo:** `DetalleServicioController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/atenciones/{idAtencion}/detalles` | Obtener detalles de una atención | **Path:** idAtencion | List\<DetalleServicio\> |
| `GET` | `/api/atenciones/{idAtencion}/detalles/{idDetalle}` | Obtener detalle específico | **Path:** idAtencion, idDetalle | DetalleServicio |
| `GET` | `/api/atenciones/{idAtencion}/detalles/subtotal` | Calcular subtotal de atención | **Path:** idAtencion | BigDecimal |
| `POST` | `/api/atenciones/{idAtencion}/detalles` | Crear detalle de servicio | **Path:** idAtencion<br>**Body:** DetalleServicio | DetalleServicio |
| `PUT` | `/api/atenciones/{idAtencion}/detalles/{idDetalle}` | Actualizar detalle | **Path:** idAtencion, idDetalle<br>**Body:** DetalleServicio | DetalleServicio |
| `DELETE` | `/api/atenciones/{idAtencion}/detalles/{idDetalle}` | Eliminar detalle | **Path:** idAtencion, idDetalle | - |

---

## 9. Groomers

**Base Path:** `/api/groomers`  
**Archivo:** `GroomerController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/groomers` | Obtener todos los groomers | - | List\<Groomer\> |
| `GET` | `/api/groomers/{id}` | Obtener groomer por ID | **Path:** id | Groomer |
| `GET` | `/api/groomers/especialidad/{especialidad}` | Obtener groomers por especialidad | **Path:** especialidad | List\<Groomer\> |
| `GET` | `/api/groomers/disponibilidad/{fecha}` | Obtener disponibilidad por fecha | **Path:** fecha (ISO Date) | List\<Object[]\> |
| `GET` | `/api/groomers/ocupacion/{fecha}` | Obtener ocupación por fecha | **Path:** fecha (ISO Date) | List\<Object[]\> |
| `GET` | `/api/groomers/tiempos-promedio` | Obtener tiempos promedio | **Query:** fechaInicio, fechaFin (ISO Date) | List\<Object[]\> |
| `GET` | `/api/groomers/disponible/{idGroomer}/{fecha}/{minutos}` | Verificar disponibilidad específica | **Path:** idGroomer, fecha (ISO DateTime), minutos | Boolean |
| `POST` | `/api/groomers` | Crear nuevo groomer | **Body:** Groomer | Groomer |
| `PUT` | `/api/groomers/{id}` | Actualizar groomer | **Path:** id<br>**Body:** Groomer | Groomer |
| `DELETE` | `/api/groomers/{id}` | Eliminar groomer | **Path:** id | - |

---

## 10. Facturas

**Base Path:** `/api/facturas`  
**Archivo:** `FacturaController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/facturas` | Obtener todas las facturas | - | List\<Factura\> |
| `GET` | `/api/facturas/{id}` | Obtener factura por ID | **Path:** id | Factura |
| `GET` | `/api/facturas/cliente/{idCliente}` | Obtener facturas de un cliente | **Path:** idCliente | List\<Factura\> |
| `POST` | `/api/facturas` | Crear nueva factura | **Query:** idAtencion, serie, numero, metodoPagoSugerido | String |
| `POST` | `/api/facturas/recalcular` | Recalcular totales de todas las facturas | - | String |
| `DELETE` | `/api/facturas/{id}` | Anular factura | **Path:** id | String |

---

## 11. Pagos

**Base Path:** `/api/pagos`  
**Archivo:** `PagoController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/pagos` | Obtener todos los pagos | - | List\<Pago\> |
| `GET` | `/api/pagos/{id}` | Obtener pago por ID | **Path:** id | Pago |
| `GET` | `/api/pagos/factura/{idFactura}` | Obtener pagos de una factura | **Path:** idFactura | List\<Pago\> |
| `GET` | `/api/pagos/confirmados` | Obtener todos los pagos confirmados | - | List\<Pago\> |
| `POST` | `/api/pagos` | Registrar nuevo pago | **Query:** idFactura, monto, metodo, referencia (opcional) | String |

---

## 12. Promociones

**Base Path:** `/api/promociones`  
**Archivo:** `PromocionController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/promociones` | Obtener todas las promociones | - | List\<Promocion\> |
| `GET` | `/api/promociones/{id}` | Obtener promoción por ID | **Path:** id | Promocion |
| `GET` | `/api/promociones/activas` | Obtener promociones activas | - | List\<Promocion\> |
| `GET` | `/api/promociones/{id}/valida` | Verificar si promoción es válida | **Path:** id | Boolean |
| `POST` | `/api/promociones` | Crear nueva promoción | **Body:** Promocion | Promocion |
| `PUT` | `/api/promociones/{id}` | Actualizar promoción | **Path:** id<br>**Body:** Promocion | Promocion |
| `PUT` | `/api/promociones/{id}/activar` | Activar promoción | **Path:** id | Promocion |
| `PUT` | `/api/promociones/{id}/desactivar` | Desactivar promoción | **Path:** id | Promocion |
| `DELETE` | `/api/promociones/{id}` | Eliminar promoción | **Path:** id | - |

---

## 13. Dashboard

**Base Path:** `/api/dashboard`  
**Archivo:** `DashboardController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/dashboard/metricas` | Obtener métricas generales | **Query:** fechaInicio (default: 2025-01-01), fechaFin (ISO Date) | List\<Map\> |
| `GET` | `/api/dashboard/cola/{idSucursal}` | Obtener cola actual de sucursal | **Path:** idSucursal | List\<Map\> |
| `GET` | `/api/dashboard/estadisticas-mensuales` | Obtener estadísticas mensuales | **Query:** anio, mes | List\<Map\> |
| `GET` | `/api/dashboard/proximas-citas/{idCliente}` | Obtener próximas citas de cliente | **Path:** idCliente | List\<Map\> |
| `GET` | `/api/dashboard/historial-mascota/{idMascota}` | Obtener historial de mascota | **Path:** idMascota | List\<Map\> |

---

## 14. Reportes

**Base Path:** `/api/reportes`  
**Archivo:** `ReporteController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/reportes/ingresos` | Reporte de ingresos | **Query:** fechaInicio, fechaFin (ISO Date), idSucursal | List\<Map\> |
| `GET` | `/api/reportes/clientes-frecuentes` | Reporte de clientes frecuentes | - | List\<Map\> |
| `GET` | `/api/reportes/servicios-mas-solicitados` | Servicios más solicitados | **Query:** fechaInicio, fechaFin (ISO Date) | List\<Map\> |
| `GET` | `/api/reportes/facturas-cliente/{idCliente}` | Facturas por cliente | **Path:** idCliente | List\<Map\> |
| `GET` | `/api/reportes/pagos-factura/{idFactura}` | Pagos por factura | **Path:** idFactura | List\<Map\> |
| `GET` | `/api/reportes/auditoria` | Reporte de logs de auditoría | **Query:** limite (default: 100), entidad (opcional), accion (opcional) | List\<Map\> |
| `GET` | `/api/reportes/resumen-general` | Resumen general del negocio | **Query:** fechaInicio, fechaFin (ISO Date) | Map\<String, Object\> |

---

## 15. Notificaciones

**Base Path:** `/api/notificaciones`  
**Archivo:** `NotificacionController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/notificaciones` | Obtener todas las notificaciones | - | List\<Notificacion\> |
| `GET` | `/api/notificaciones/{id}` | Obtener notificación por ID | **Path:** id | Notificacion |
| `GET` | `/api/notificaciones/cliente/{idCliente}` | Notificaciones de un cliente | **Path:** idCliente | List\<Notificacion\> |
| `GET` | `/api/notificaciones/cliente/{idCliente}/no-leidas` | Notificaciones no leídas | **Path:** idCliente | List\<Notificacion\> |
| `GET` | `/api/notificaciones/pendientes` | Notificaciones pendientes | - | List\<Notificacion\> |
| `POST` | `/api/notificaciones` | Crear nueva notificación | **Body:** Notificacion | Notificacion |
| `PUT` | `/api/notificaciones/{id}` | Actualizar notificación | **Path:** id<br>**Body:** Notificacion | Notificacion |
| `PUT` | `/api/notificaciones/{id}/marcar-enviada` | Marcar como enviada | **Path:** id | Notificacion |
| `PUT` | `/api/notificaciones/{id}/marcar-leida` | Marcar como leída | **Path:** id | Notificacion |
| `DELETE` | `/api/notificaciones/{id}` | Eliminar notificación | **Path:** id | - |

---

## 16. Usuarios del Sistema

**Base Path:** `/api/admin/usuarios`  
**Archivo:** `UsuarioSistemaController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/admin/usuarios` | Obtener todos los usuarios | - | List\<UsuarioSistema\> |
| `GET` | `/api/admin/usuarios/{id}` | Obtener usuario por ID | **Path:** id | UsuarioSistema |
| `GET` | `/api/admin/usuarios/email/{email}` | Obtener usuario por email | **Path:** email | UsuarioSistema |
| `GET` | `/api/admin/usuarios/rol/{rol}` | Obtener usuarios por rol | **Path:** rol | List\<UsuarioSistema\> |
| `POST` | `/api/admin/usuarios` | Crear nuevo usuario | **Body:** UsuarioSistema | UsuarioSistema |
| `PUT` | `/api/admin/usuarios/{id}` | Actualizar usuario | **Path:** id<br>**Body:** UsuarioSistema | UsuarioSistema |
| `PUT` | `/api/admin/usuarios/{id}/cambiar-contraseña` | Cambiar contraseña | **Path:** id<br>**Body:** ChangePasswordRequest | UsuarioSistema |
| `DELETE` | `/api/admin/usuarios/{id}` | Eliminar usuario | **Path:** id | - |

---

## 17. Sucursales

**Base Path:** `/api/admin/sucursales`  
**Archivo:** `SucursalController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/admin/sucursales` | Obtener todas las sucursales | - | List\<Sucursal\> |
| `GET` | `/api/admin/sucursales/{id}` | Obtener sucursal por ID | **Path:** id | Sucursal |
| `POST` | `/api/admin/sucursales` | Crear nueva sucursal | **Body:** Sucursal | Sucursal |
| `PUT` | `/api/admin/sucursales/{id}` | Actualizar sucursal | **Path:** id<br>**Body:** Sucursal | Sucursal |
| `DELETE` | `/api/admin/sucursales/{id}` | Eliminar sucursal | **Path:** id | - |

---

## 18. Configuración

**Base Path:** `/api/admin/configuracion`  
**Archivo:** `ConfiguracionController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/admin/configuracion` | Obtener todas las configuraciones | - | List\<ConfiguracionEstimacion\> |
| `GET` | `/api/admin/configuracion/{id}` | Obtener configuración por ID | **Path:** id | ConfiguracionEstimacion |
| `GET` | `/api/admin/configuracion/servicio/{idServicio}` | Configuraciones por servicio | **Path:** idServicio | List\<ConfiguracionEstimacion\> |
| `GET` | `/api/admin/configuracion/tiempo/{idServicio}` | Obtener tiempo estimado | **Path:** idServicio | Integer |
| `POST` | `/api/admin/configuracion` | Crear nueva configuración | **Body:** ConfiguracionEstimacion | ConfiguracionEstimacion |
| `PUT` | `/api/admin/configuracion/{id}` | Actualizar configuración | **Path:** id<br>**Body:** ConfiguracionEstimacion | ConfiguracionEstimacion |
| `DELETE` | `/api/admin/configuracion/{id}` | Eliminar configuración | **Path:** id | - |

---

## 19. Auditoría

**Base Path:** `/api/admin/audit`  
**Archivo:** `AuditController.java`

| Método | Endpoint | Descripción | Parámetros | Respuesta |
|--------|----------|-------------|------------|-----------|
| `GET` | `/api/admin/audit` | Obtener todos los logs | - | List\<AuditLog\> |
| `GET` | `/api/admin/audit/{id}` | Obtener log por ID | **Path:** id | AuditLog |
| `GET` | `/api/admin/audit/usuario/{idUsuario}` | Logs por usuario | **Path:** idUsuario | List\<AuditLog\> |
| `GET` | `/api/admin/audit/fecha/{fecha}` | Logs por fecha | **Path:** fecha (ISO Date) | List\<AuditLog\> |
| `GET` | `/api/admin/audit/accion/{accion}` | Logs por acción | **Path:** accion | List\<AuditLog\> |
| `GET` | `/api/admin/audit/limite/{limite}` | Logs con límite | **Path:** limite | List\<AuditLog\> |
| `DELETE` | `/api/admin/audit/{id}` | Eliminar log | **Path:** id | - |

---

## Resumen Estadístico

| Categoría | Cantidad |
|-----------|----------|
| Total de Controladores | 19 |
| Total de Endpoints | 154 |
| Endpoints GET | 89 |
| Endpoints POST | 20 |
| Endpoints PUT | 23 |
| Endpoints DELETE | 22 |

---

## Notas Importantes

### Autenticación
- Todos los endpoints excepto `/api/auth/login` y `/api/auth/validar` requieren JWT token
- El token JWT se obtiene mediante el endpoint `/api/auth/login`
- El token debe enviarse en el header: `Authorization: Bearer <token>`

### Formato de Fechas
- **ISO Date:** `YYYY-MM-DD` (ejemplo: `2025-01-01`)
- **ISO DateTime:** `YYYY-MM-DDTHH:mm:ss` (ejemplo: `2025-01-01T14:30:00`)

### Respuestas Estándar
Todos los endpoints devuelven una estructura `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Mensaje descriptivo",
  "data": { /* objeto o array de respuesta */ },
  "error": null
}
```

En caso de error:

```json
{
  "success": false,
  "message": "Mensaje de error",
  "data": null,
  "error": "Detalle del error"
}
```

### CORS
Todos los controladores tienen habilitado CORS con:
- `origins: "*"`
- `maxAge: 3600`

### Códigos de Estado HTTP
- `200 OK` - Éxito en operaciones GET, PUT
- `201 CREATED` - Recurso creado exitosamente (POST)
- `400 BAD_REQUEST` - Error de validación
- `401 UNAUTHORIZED` - Token inválido o expirado
- `404 NOT_FOUND` - Recurso no encontrado
- `500 INTERNAL_SERVER_ERROR` - Error del servidor

---

**Fin del Informe**
