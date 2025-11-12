# 📋 TODO - Tareas Pendientes TeranVet Spring Boot

## 🔐 AUTENTICACIÓN Y SEGURIDAD (CRÍTICO)

### Implementación de JWT
- [ ] Crear clase `JwtTokenProvider`
  - [ ] Generar token con claims
  - [ ] Validar token
  - [ ] Extraer claims del token
  - [ ] Renovar token

- [ ] Crear `JwtAuthenticationFilter`
  - [ ] Interceptar requests
  - [ ] Validar token en header
  - [ ] Pasar al siguiente filtro

- [ ] Crear `UsuarioService`
  - [ ] `validarCredenciales(email, password)`
  - [ ] `buscarPorEmail(email)`
  - [ ] `crearUsuario(usuarioDTO)`
  - [ ] `obtenerTodos()`
  - [ ] `actualizarUsuario(id, usuarioDTO)`

- [ ] Crear `AutenticacionController`
  - [ ] `POST /auth/login` - Generar token
  - [ ] `POST /auth/logout` - Invalidar sesión
  - [ ] `POST /auth/registro` - Registrar usuario
  - [ ] `POST /auth/refresh` - Renovar token
  - [ ] `GET /auth/perfil` - Obtener perfil actual

- [ ] Crear `SecurityConfig`
  - [ ] Configurar Spring Security
  - [ ] Agregar filtro JWT
  - [ ] Configurar rutas públicas vs protegidas
  - [ ] Hashing de passwords (BCryptPasswordEncoder)

---

## 💳 MÓDULO DE FACTURACIÓN

### Crear `FacturaService`
- [ ] `crearFactura(idAtencion, idCliente)`
- [ ] `obtenerFacturasPorCliente(idCliente)`
- [ ] `obtenerFacturasPorFecha(fechaInicio, fechaFin)`
- [ ] `obtenerFacturaPorId(idFactura)`
- [ ] `actualizarFactura(idFactura, facturaDTO)`
- [ ] `anularFactura(idFactura)`
- [ ] `recalcularTotal(idFactura)`
- [ ] `obtenerFacturasDelDia()`

### Crear `FacturaController`
- [ ] Endpoints CRUD completo
- [ ] Búsquedas avanzadas
- [ ] Generación de PDF (opcional)

### Crear `FacturaDTO`
- [ ] Mapeo de datos

---

## 💰 MÓDULO DE PAGOS

### Crear `PagoService`
- [ ] `registrarPago(idFactura, monto, metodo, referencia)`
- [ ] `obtenerPagosPorFactura(idFactura)`
- [ ] `obtenerPagosPorFecha(fechaInicio, fechaFin)`
- [ ] `obtenerPagosConfirmados()`
- [ ] `rechazarPago(idPago)`
- [ ] `obtenerEstadoPago(idPago)`

### Crear `PagoController`
- [ ] Endpoints CRUD para pagos
- [ ] Búsquedas por rango de fechas
- [ ] Reportes de pagos recibidos

### Crear `PagoDTO`
- [ ] Mapeo de datos

---

## 👥 MÓDULO DE GROOMERS

### Crear `GroomerService`
- [ ] `obtenerGroom(idGroomer)`
- [ ] `obtenerDisponibilidad(idGroomer, fecha)`
- [ ] `actualizarDisponibilidad(idGroomer, horarios)`
- [ ] `obtenerCargaTrabajoDelDia(idGroomer, fecha)`
- [ ] `obtenerTiempoPromedio(idGroomer, rango_fechas)`
- [ ] `registrarEspecialidades(idGroomer, especialidades)`
- [ ] `crearGroomer(groomerDTO)`
- [ ] `actualizarGroomer(idGroomer, groomerDTO)`

### Crear `GroomerController`
- [ ] Endpoints CRUD
- [ ] Endpoints de disponibilidad
- [ ] Endpoints de reportes de ocupación

### Crear `GroomerDTO`
- [ ] Mapeo de datos

---

## 🏢 MÓDULO DE SUCURSALES

### Crear `SucursalService`
- [ ] `obtenerTodasLasSucursales()`
- [ ] `obtenerSucursal(idSucursal)`
- [ ] `crearSucursal(sucursalDTO)`
- [ ] `actualizarSucursal(idSucursal, sucursalDTO)`
- [ ] `eliminarSucursal(idSucursal)`

### Crear `SucursalController`
- [ ] Endpoints CRUD básico

### Crear `SucursalDTO`
- [ ] Mapeo de datos

---

## 🎁 MÓDULO DE PROMOCIONES

### Crear `PromocionService`
- [ ] `obtenerPromociones()`
- [ ] `obtenerPromocionesActivas()`
- [ ] `crearPromocion(promocionDTO)`
- [ ] `actualizarPromocion(idPromocion, promocionDTO)`
- [ ] `desactivarPromocion(idPromocion)`
- [ ] `calcularDescuento(idPromocion, monto)`

### Crear `PromocionController`
- [ ] Endpoints CRUD

### Crear `PromocionDTO`
- [ ] Mapeo de datos

---

## 📊 MÓDULO DE REPORTES (AVANZADO)

### Crear `ReporteService`
- [ ] **Dashboard**
  - [ ] `obtenerMetricasDashboard(fechaInicio, fechaFin)`
    - [ ] Total citas del día
    - [ ] Atenciones en curso
    - [ ] Ingresos del día
    - [ ] Total clientes
    
- [ ] **Ingresos**
  - [ ] `obtenerIngresos(fechaInicio, fechaFin, idSucursal)`
  - [ ] `obtenerIngresosPorServicio(fechaInicio, fechaFin)`
  - [ ] `obtenerIngresosPorMetodoPago(fechaInicio, fechaFin)`
  
- [ ] **Clientes**
  - [ ] `obtenerClientesFrecuentes(limite)`
  - [ ] `obtenerClientesNuevos(fechaInicio, fechaFin)`
  - [ ] `obtenerClietesConDeuda()`
  
- [ ] **Servicios**
  - [ ] `obtenerServiciosMasSolicitados(fechaInicio, fechaFin)`
  - [ ] `obtenerServiciosPocoSolicitados(fechaInicio, fechaFin)`
  
- [ ] **Groomers**
  - [ ] `obtenerProductividadGroomers(fechaInicio, fechaFin)`
  - [ ] `obtenerTiemposPromedio(idGroomer, fechaInicio, fechaFin)`
  - [ ] `obtenerOcupacionGroomers(fecha)`

### Crear `ReporteController`
- [ ] GET /api/reportes/dashboard
- [ ] GET /api/reportes/ingresos
- [ ] GET /api/reportes/clientes-frecuentes
- [ ] GET /api/reportes/servicios-solicitados
- [ ] GET /api/reportes/productividad-groomers

---

## 📧 MÓDULO DE NOTIFICACIONES

### Crear entidad `Notificacion` si no existe
- [ ] Mapeo JPA

### Crear `NotificacionRepository`

### Crear `NotificacionService`
- [ ] `enviarNotificacion(tipo, destinatario, contenido, referencia)`
- [ ] `obtenerNotificacionesCliente(idCliente)`
- [ ] `marcarComoLeida(idNotificacion)`
- [ ] `enviarRecordatorio(idCita)`
- [ ] `enviarReceboFactura(idFactura)`

### Crear `NotificacionController`
- [ ] GET /api/notificaciones
- [ ] PUT /api/notificaciones/{id}/marcar-leida

---

## 🧪 TESTING

### Test Unitarios
- [ ] `ClienteServiceTest`
- [ ] `MascotaServiceTest`
- [ ] `CitaServiceTest`
- [ ] `ServicioServiceTest`
- [ ] `AtencionServiceTest`

### Test de Integración
- [ ] `ClienteControllerTest`
- [ ] `MascotaControllerTest`
- [ ] `CitaControllerTest`
- [ ] `ServicioControllerTest`
- [ ] `AtencionControllerTest`

### Test de Endpoints
- [ ] Test de respuestas HTTP
- [ ] Test de validaciones
- [ ] Test de errores
- [ ] Cobertura mínima: 80%

---

## 📖 DOCUMENTACIÓN

### API Documentation (Swagger/OpenAPI)
- [ ] Agregar dependencia `springdoc-openapi-ui`
- [ ] Crear `SwaggerConfig`
- [ ] Anotaciones `@Operation` en controllers
- [ ] Anotaciones `@Schema` en DTOs
- [ ] Endpoint: /swagger-ui.html

### Documentación de Código
- [ ] JavaDoc en todas las clases públicas
- [ ] JavaDoc en todos los métodos públicos
- [ ] Comentarios en lógica compleja
- [ ] Ejemplos de uso en comentarios

### Documentación de Proyecto
- [ ] Guía de instalación
- [ ] Guía de configuración
- [ ] Guía de desarrollo
- [ ] Diagrama de entidades (ER)
- [ ] Diagrama de arquitectura

---

## ⚙️ CONFIGURACIÓN Y DEPLOYMENT

### Configuración Adicional
- [ ] Perfil de producción (application-prod.properties)
- [ ] Perfil de desarrollo (application-dev.properties)
- [ ] Perfil de testing (application-test.properties)
- [ ] Variables de entorno para secretos

### Docker
- [ ] Dockerfile para la aplicación
- [ ] docker-compose.yml para BD + APP
- [ ] Scripts de build y deploy

### Monitoring
- [ ] Spring Boot Actuator configurado
- [ ] Métricas de Micrometer
- [ ] Logs centralizados (opcional: ELK)

---

## 🔧 MEJORAS DE CÓDIGO

### Refactoring
- [ ] Extraer métodos duplicados
- [ ] Eliminar código muerto
- [ ] Mejorar nombres de variables
- [ ] Aplicar design patterns donde sea necesario

### Performance
- [ ] Lazy loading en relaciones
- [ ] Cachés (Redis opcional)
- [ ] Paginación en listados grandes
- [ ] Índices en BD

### Seguridad
- [ ] Validar entrada con anotaciones
- [ ] Encriptar datos sensibles
- [ ] Rate limiting en endpoints
- [ ] HTTPS en producción

---

## 📱 INTEGRACIÓN CON FRONTEND

### Implementaciones Necesarias
- [ ] Login y autenticación en UI
- [ ] CRUD forms para cada entidad
- [ ] Dashboard interactivo
- [ ] Gráficos de reportes
- [ ] Notificaciones en tiempo real (WebSocket)

### Consumo de API
- [ ] Cliente HTTP (Axios/Fetch)
- [ ] Manejo de tokens JWT
- [ ] Manejo de errores de API
- [ ] Formularios reactivos

---

## 🎯 MILESTONES

### Fase 1: Base ✅ COMPLETADA
- [x] Entidades y repositorios
- [x] Servicios básicos
- [x] Controladores REST
- [x] Configuración inicial
- **Endpoints funcionales: 36**

### Fase 2: Seguridad 🔄 EN PROGRESO
- [ ] Autenticación JWT
- [ ] Cifrado de passwords
- [ ] Validación de entrada
- [ ] Control de acceso
- **Estimado: 1-2 semanas**

### Fase 3: Módulos Avanzados ⏳ PENDIENTE
- [ ] Facturación
- [ ] Reportes
- [ ] Notificaciones
- **Estimado: 2-3 semanas**

### Fase 4: Testing & Deployment ⏳ PENDIENTE
- [ ] Test coverage
- [ ] Docker
- [ ] CI/CD
- **Estimado: 1-2 semanas**

---

## 📊 Progreso General

```
████████████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░
Arquitectura: ████████████████████ 100%
Endpoints: ████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 40%
Documentación: ██████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 30%
Testing: ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 0%
Deployment: ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 0%

Avance Total: ~35% ✅ En Progreso
```

---

## 📌 Notas Importantes

1. **Prioridad Alta**: Implementar autenticación JWT ASAP
2. **Consideración**: Usar JpaAuditing para auditoría automática
3. **Buena Práctica**: Agregar validación con @Valid
4. **Rendimiento**: Implementar paginación en listados grandes
5. **Seguridad**: Nunca guardar passwords en texto plano
6. **Testing**: Escribir tests mientras se desarrolla, no después
7. **Documentación**: Mantenerla actualizada constantemente

---

**Última Actualización**: Noviembre 2025  
**Versión**: 1.0.0  
**Fase Actual**: Estructura Base Completada ✅
