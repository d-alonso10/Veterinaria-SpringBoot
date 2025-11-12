
---

Excelente trabajo con la integración de los Stored Procedures (SPs) y la finalización de los módulos de Facturación y Pagos. La base del proyecto es ahora extremadamente sólida.

El plan de acción se centrará en dos objetivos:
1.  Exponer los repositorios ya creados a través de sus correspondientes `Services` y `Controllers`.
2.  Implementar los módulos de Reportes y Autenticación.

---

### Prioridad 1: Módulos de Administración y Personal

Has hecho el trabajo difícil de crear los `Repository` y mapear los SPs para `Groomer`, `Audit`, `Configuracion`, etc. (listados en tu `GUIA_STORED_PROCEDURES.md`). Ahora, solo necesitas crear los `Service` y `Controller` para exponerlos como API REST.

**Acciones:**

1.  **Módulo de Personal (Groomers):**
    * Crear `GroomerService.java`.
    * Crear `GroomerController.java` (`/api/groomers`).
    * **Endpoints Requeridos:**
        * `GET /`: `sp_ObtenerGroomers`
        * `POST /`: `sp_InsertarGroomer`
        * `PUT /{id}`: `sp_ActualizarGroomer`
        * `GET /disponibilidad/{fecha}`: `sp_ObtenerDisponibilidadGroomers`
        * `GET /ocupacion/{fecha}`: `sp_OcupacionGroomer`
        * `GET /tiempos-promedio`: `sp_TiemposPromedioGroomer`

2.  **Módulo de Administración (Core):**
    * Crear `UsuarioSistemaService.java` y `UsuarioSistemaController.java` (`/api/admin/usuarios`) para el CRUD básico de usuarios del sistema (SPs: `sp_InsertarUsuario`, `sp_ActualizarUsuario`, etc. - *Asegúrate de tenerlos*).
    * Crear `SucursalService.java` y `SucursalController.java` (`/api/admin/sucursales`) para el CRUD de sucursales (SPs: `sp_InsertarSucursal`, etc.).

3.  **Módulo de Administración (Utilidades):**
    * Crear `AuditService.java` y `AuditController.java` (`/api/admin/audit`) para exponer `sp_ObtenerLogsAuditoria`.
    * Crear `ConfiguracionService.java` y `ConfiguracionController.java` (`/api/admin/configuracion`) para exponer `sp_AjustarTiempoEstimado` y `sp_ObtenerEstimacionesTiempo`.

---

### Prioridad 2: Módulos de Soporte (Servicios Avanzados)

Al igual que en la prioridad 1, los repositorios ya están listos. Solo falta exponerlos.

**Acciones:**

* Crear `PaqueteServicioService.java` y `PaqueteServicioController.java` (`/api/servicios/paquetes`) para exponer `sp_CrearPaqueteServicio` y `sp_AgregarServicioPaquete`.
* Crear `DetalleServicioService.java` y `DetalleServicioController.java` (`/api/atenciones/{id}/detalles`) para exponer `sp_DetalleServiciosAtencion`.
* Crear `NotificacionService.java` y `NotificacionController.java` (`/api/notificaciones`) para exponer `sp_RegistrarNotificacion` y `sp_ObtenerNotificacionesCliente`.
* Crear `PromocionService.java` y `PromocionController.java` (`/api/promociones`) (El `PromocionRepository` ya existe, solo falta exponerlo).

---

### Prioridad 3: Módulo de Reportes (Crítico - Requiere SPs)

Este módulo es el único que parece faltar por completo, **incluyendo los SPs y el Repositorio**. El proyecto original (`veterinaria-trabajo-final`) tenía un `ReporteDao.java`.

**Acciones:**

1.  **Crear Repositorio:**
    * Crear la interfaz `ReporteRepository.java`. (No necesita extender `JpaRepository` si solo contendrá llamadas a SPs).
2.  **Mapear SPs de Reportes (¡NUEVOS!):**
    * Debes añadir a tu base de datos y mapear en `ReporteRepository` los SPs que faltan del proyecto original:
        * `sp_ReporteIngresos(fecha_inicio, fecha_fin)`
        * `sp_ClientesFrecuentes(limite)`
        * `sp_ServiciosMasSolicitados(limite)`
3.  **Crear Controladores de Reportes:**
    * Crear `DashboardService.java` y `DashboardController.java` (`/api/dashboard`) para métricas rápidas (ej. `sp_ObtenerColaActual`, `sp_ProximasCitas`, etc.).
    * Crear `ReporteService.java` y `ReporteController.java` (`/api/reportes`) para exponer los nuevos SPs (Ingresos, Clientes Frecuentes, Servicios Más Solicitados).

---

### Prioridad 4: Autenticación (Login)

Este es el último paso funcional.

**Acciones:**

1.  **Mapear SP de Login:**
    * Asegúrate de tener un `sp_ValidarUsuario(username, password)`.
    * Mapea este SP en `UsuarioSistemaRepository.java`.
2.  **Crear Controlador de Auth:**
    * Crear `AuthController.java` (`/api/auth`).
    * Implementar el endpoint `POST /login` que reciba `LoginRequest`, llame al `sp_ValidarUsuario` y devuelva un `LoginResponse`.
    * *(Idealmente, esto debería generar un JWT, pero por ahora, devolver el objeto `UsuarioSistema` o un booleano de éxito es suficiente).*

---

### Prioridad 5: Finalización y Calidad

* **Pruebas:** Realizar pruebas de todos los endpoints usando Postman, como sugieres en tu informe `AVANCE_SESION_20251112.md`.
* **Documentación:** (Opcional) Implementar Swagger/OpenAPI para documentar la API.
* **Tests Unitarios:** (Opcional) Añadir pruebas unitarias básicas para los `Service`.