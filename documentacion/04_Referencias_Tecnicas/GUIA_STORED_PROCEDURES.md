╔════════════════════════════════════════════════════════════════════════════════╗
║                                                                                ║
║            📚 GUÍA DE STORED PROCEDURES INTEGRADOS EN SPRING BOOT             ║
║                                                                                ║
║                     Referencia de SPs y sus Mappings en Java                  ║
║                                                                                ║
╚════════════════════════════════════════════════════════════════════════════════╝


🔗 TABLA DE CONTENIDOS

1. ATENCIONES
2. CITAS
3. CLIENTES
4. MASCOTAS
5. FACTURAS
6. PAGOS
7. SERVICIOS
8. GROOMERS
9. NUEVAS ENTIDADES


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 1️⃣ ATENCIONES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: AtencionRepository.java

**sp_CrearAtencionDesdeCita**
├─ Firma SQL:      CALL sp_CrearAtencionDesdeCita(p_id_cita, p_id_groomer, p_id_sucursal, 
│                   p_turno_num, p_tiempo_estimado_inicio, p_tiempo_estimado_fin, p_prioridad)
├─ Método Java:    crearAtencionDesdeCita(Integer, Integer, Integer, Integer, 
│                  LocalDateTime, LocalDateTime, Integer)
├─ Uso:            AtencionService.crearDesdeCita(...)
├─ Endpoint:       POST /api/atenciones/desde-cita
└─ Propósito:      Crea una Atención a partir de una Cita confirmada

**sp_CrearAtencionWalkIn**
├─ Firma SQL:      CALL sp_CrearAtencionWalkIn(p_id_mascota, p_id_cliente, p_id_groomer,
│                   p_id_sucursal, p_turno_num, p_tiempo_estimado_inicio, 
│                   p_tiempo_estimado_fin, p_prioridad, p_observaciones)
├─ Método Java:    crearAtencionWalkIn(Integer, Integer, Integer, Integer, Integer,
│                  LocalDateTime, LocalDateTime, Integer, String)
├─ Uso:            AtencionService.crearWalkIn(...)
├─ Endpoint:       POST /api/atenciones/walk-in
└─ Propósito:      Crea una Atención sin cita previa

**sp_ActualizarEstadoAtencion**
├─ Firma SQL:      CALL sp_ActualizarEstadoAtencion(p_id_atencion, p_nuevo_estado)
├─ Método Java:    actualizarEstadoAtencion(Integer, String)
├─ Uso:            AtencionService.actualizarEstado(...)
├─ Endpoint:       PUT /api/atenciones/{id}/estado
├─ Estados:        en_espera, en_servicio, pausado, terminado
└─ Propósito:      Cambia el estado de una Atención

**sp_ObtenerColaActual**
├─ Firma SQL:      CALL sp_ObtenerColaActual(p_id_sucursal)
├─ Método Java:    obtenerColaActualSP(Integer)
├─ Uso:            AtencionService.obtenerColaActual(...)
├─ Endpoint:       GET /api/atenciones/cola/{idSucursal}
└─ Propósito:      Obtiene cola de atención ordenada por prioridad


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 2️⃣ CITAS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: CitaRepository.java

**sp_CrearCita**
├─ Firma SQL:      CALL sp_CrearCita(p_id_mascota, p_id_cliente, p_id_sucursal,
│                   p_id_servicio, p_fecha_programada, p_modalidad, p_notas)
├─ Método Java:    crearCita(Integer, Integer, Integer, Integer, 
│                  LocalDateTime, String, String)
├─ Uso:            CitaService.crear(...)
├─ Endpoint:       POST /api/citas
├─ Modalidad:      presencial, virtual
└─ Propósito:      Crea una nueva cita

**sp_ReprogramarCita**
├─ Firma SQL:      CALL sp_ReprogramarCita(p_id_cita, p_nueva_fecha)
├─ Método Java:    reprogramarCita(Integer, LocalDateTime)
├─ Uso:            CitaService.reprogramar(...)
├─ Endpoint:       PUT /api/citas/{id}/reprogramar
└─ Propósito:      Cambia la fecha programada de una cita

**sp_CancelarCita**
├─ Firma SQL:      CALL sp_CancelarCita(p_id_cita)
├─ Método Java:    cancelarCita(Integer)
├─ Uso:            CitaService.cancelar(...)
├─ Endpoint:       PUT /api/citas/{id}/cancelar
└─ Propósito:      Cancela una cita

**sp_ConfirmarAsistenciaCita**
├─ Firma SQL:      CALL sp_ConfirmarAsistenciaCita(p_id_cita)
├─ Método Java:    confirmarAsistenciaCita(Integer)
├─ Uso:            CitaService.confirmarAsistencia(...)
├─ Endpoint:       PUT /api/citas/{id}/confirmar-asistencia
└─ Propósito:      Marca una cita como asistida

**sp_ObtenerProximasCitas**
├─ Firma SQL:      CALL sp_ObtenerProximasCitas(p_id_cliente)
├─ Método Java:    obtenerProximasCitasSP(Integer)
├─ Uso:            CitaService.obtenerProximasCitas(...)
├─ Endpoint:       GET /api/citas/cliente/{id}/proximas
└─ Propósito:      Obtiene próximas citas de un cliente


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 3️⃣ CLIENTES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: ClienteRepository.java

**sp_BuscarClientes**
├─ Firma SQL:      CALL sp_BuscarClientes(p_termino)
├─ Método Java:    buscarClientesSP(String)
├─ Uso:            ClienteService.buscar(...)
├─ Endpoint:       GET /api/clientes/buscar/{termino}
└─ Propósito:      Busca clientes por nombre, apellido o DNI

**sp_InsertarCliente**
├─ Firma SQL:      CALL sp_InsertarCliente(p_nombre, p_apellido, p_dni_ruc,
│                   p_email, p_telefono, p_direccion, p_preferencias)
├─ Método Java:    insertarCliente(String, String, String, String, String, String, String)
├─ Uso:            ClienteService.crear(...)
├─ Endpoint:       POST /api/clientes
└─ Propósito:      Inserta un nuevo cliente


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 4️⃣ MASCOTAS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: MascotaRepository.java

**sp_BuscarMascotas**
├─ Firma SQL:      CALL sp_BuscarMascotas(p_termino)
├─ Método Java:    buscarMascotasSP(String)
├─ Uso:            MascotaService.buscar(...)
├─ Endpoint:       GET /api/mascotas/buscar/{termino}
└─ Propósito:      Busca mascotas por nombre o raza

**sp_ObtenerMascotasPorCliente**
├─ Firma SQL:      CALL sp_ObtenerMascotasPorCliente(p_id_cliente)
├─ Método Java:    obtenerMascotasPorClienteSP(Integer)
├─ Uso:            MascotaService.obtenerPorCliente(...)
├─ Endpoint:       GET /api/mascotas/cliente/{id}
└─ Propósito:      Obtiene todas las mascotas de un cliente

**sp_InsertarMascota**
├─ Firma SQL:      CALL sp_InsertarMascota(p_id_cliente, p_nombre, p_especie,
│                   p_raza, p_sexo, p_fecha_nacimiento, p_microchip, p_observaciones)
├─ Método Java:    insertarMascota(Integer, String, String, String, String,
│                  LocalDate, String, String)
├─ Uso:            MascotaService.crear(...)
├─ Endpoint:       POST /api/mascotas
├─ Especies:       perro, gato, otro
└─ Propósito:      Inserta una nueva mascota

**sp_HistorialMascota**
├─ Firma SQL:      CALL sp_HistorialMascota(p_id_mascota)
├─ Método Java:    historialMascota(Integer)
├─ Uso:            MascotaService.obtenerHistorial(...)
├─ Endpoint:       GET /api/mascotas/{id}/historial
└─ Propósito:      Obtiene historial completo de atenciones


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 5️⃣ FACTURAS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: FacturaRepository.java

**sp_CrearFactura**
├─ Firma SQL:      CALL sp_CrearFactura(p_serie, p_numero, p_id_atencion,
│                   p_metodo_pago_sugerido)
├─ Método Java:    crearFactura(String, String, Integer, String)
├─ Uso:            FacturaService.crearFactura(...)
├─ Endpoint:       POST /api/facturas
└─ Propósito:      Crea una factura a partir de una atención

**sp_AnularFactura**
├─ Firma SQL:      CALL sp_AnularFactura(p_id_factura)
├─ Método Java:    anularFactura(Integer)
├─ Uso:            FacturaService.anularFactura(...)
├─ Endpoint:       DELETE /api/facturas/{id}
└─ Propósito:      Anula una factura confirmada

**sp_ObtenerFacturasPorCliente**
├─ Firma SQL:      CALL sp_ObtenerFacturasPorCliente(p_id_cliente)
├─ Método Java:    obtenerFacturasPorClienteSP(Integer)
├─ Uso:            FacturaService.obtenerFacturasPorClienteSP(...)
├─ Endpoint:       GET /api/facturas/cliente/{id}
└─ Propósito:      Obtiene todas las facturas de un cliente

**sp_RecalcularTotalesFacturas**
├─ Firma SQL:      CALL sp_RecalcularTotalesFacturas()
├─ Método Java:    recalcularTotalesFacturas()
├─ Uso:            FacturaService.recalcularTotalesFacturas()
├─ Endpoint:       POST /api/facturas/recalcular
└─ Propósito:      Recalcula subtotales, impuestos y totales


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 6️⃣ PAGOS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: PagoRepository.java

**sp_RegistrarPago**
├─ Firma SQL:      CALL sp_RegistrarPago(p_id_factura, p_monto, p_metodo,
│                   p_referencia)
├─ Método Java:    registrarPago(Integer, BigDecimal, String, String)
├─ Uso:            PagoService.registrarPago(...)
├─ Endpoint:       POST /api/pagos
├─ Métodos:        efectivo, tarjeta, transfer, otro
└─ Propósito:      Registra un pago en la factura

**sp_ObtenerPagosPorFactura**
├─ Firma SQL:      CALL sp_ObtenerPagosPorFactura(p_id_factura)
├─ Método Java:    obtenerPagosPorFacturaSP(Integer)
├─ Uso:            PagoService.obtenerPagosPorFacturaSP(...)
├─ Endpoint:       GET /api/pagos/factura/{id}
└─ Propósito:      Obtiene todos los pagos de una factura


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 7️⃣ SERVICIOS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: ServicioRepository.java

**sp_ObtenerServicios**
├─ Firma SQL:      CALL sp_ObtenerServicios()
├─ Método Java:    obtenerServiciosSP()
├─ Uso:            ServicioService.obtenerTodos()
├─ Endpoint:       GET /api/servicios
└─ Propósito:      Obtiene todos los servicios

**sp_ObtenerServiciosPorCategoria**
├─ Firma SQL:      CALL sp_ObtenerServiciosPorCategoria(p_categoria)
├─ Método Java:    obtenerServiciosPorCategoriaSP(String)
├─ Uso:            ServicioService.obtenerPorCategoria(...)
├─ Endpoint:       GET /api/servicios/categoria/{categoria}
├─ Categorías:     baño, corte, dental, paquete, otro
└─ Propósito:      Obtiene servicios de una categoría

**sp_InsertarServicio**
├─ Firma SQL:      CALL sp_InsertarServicio(p_codigo, p_nombre, p_descripcion,
│                   p_duracion_estimada_min, p_precio_base, p_categoria)
├─ Método Java:    insertarServicio(String, String, String, Integer, BigDecimal, String)
├─ Uso:            ServicioService.crear(...)
├─ Endpoint:       POST /api/servicios
└─ Propósito:      Crea un nuevo servicio

**sp_ActualizarServicio**
├─ Firma SQL:      CALL sp_ActualizarServicio(p_id_servicio, p_codigo, p_nombre,
│                   p_descripcion, p_duracion_estimada_min, p_precio_base, p_categoria)
├─ Método Java:    actualizarServicio(Integer, String, String, String, Integer,
│                  BigDecimal, String)
├─ Uso:            ServicioService.actualizar(...)
├─ Endpoint:       PUT /api/servicios/{id}
└─ Propósito:      Actualiza un servicio existente


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 8️⃣ GROOMERS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📍 UBICACIÓN: GroomerRepository.java

**sp_ObtenerGroomers**
├─ Firma SQL:      CALL sp_ObtenerGroomers()
├─ Método Java:    obtenerGroomersSP()
├─ Uso:            GroomerService.obtenerTodos()
├─ Endpoint:       GET /api/groomers
└─ Propósito:      Obtiene todos los Groomers

**sp_InsertarGroomer**
├─ Firma SQL:      CALL sp_InsertarGroomer(p_nombre, p_especialidades,
│                   p_disponibilidad)
├─ Método Java:    insertarGroomer(String, String, String)
├─ Uso:            GroomerService.crear(...)
├─ Endpoint:       POST /api/groomers
├─ Datos JSON:     especialidades y disponibilidad como JSON
└─ Propósito:      Crea un nuevo Groomer

**sp_ActualizarGroomer**
├─ Firma SQL:      CALL sp_ActualizarGroomer(p_id_groomer, p_nombre,
│                   p_especialidades, p_disponibilidad)
├─ Método Java:    actualizarGroomer(Integer, String, String, String)
├─ Uso:            GroomerService.actualizar(...)
├─ Endpoint:       PUT /api/groomers/{id}
└─ Propósito:      Actualiza un Groomer

**sp_ObtenerDisponibilidadGroomers**
├─ Firma SQL:      CALL sp_ObtenerDisponibilidadGroomers(p_fecha)
├─ Método Java:    obtenerDisponibilidadGroomers(LocalDate)
├─ Uso:            GroomerService.obtenerDisponibilidad(...)
├─ Endpoint:       GET /api/groomers/disponibilidad/{fecha}
└─ Propósito:      Obtiene disponibilidad de Groomers en una fecha

**sp_OcupacionGroomer**
├─ Firma SQL:      CALL sp_OcupacionGroomer(p_fecha)
├─ Método Java:    ocupacionGroomer(LocalDate)
├─ Uso:            GroomerService.obtenerOcupacion(...)
├─ Endpoint:       GET /api/groomers/ocupacion/{fecha}
└─ Propósito:      Obtiene ocupación (minutos trabajados) por groomer

**sp_TiemposPromedioGroomer**
├─ Firma SQL:      CALL sp_TiemposPromedioGroomer(p_fecha_inicio, p_fecha_fin)
├─ Método Java:    tiemposPromedioGroomer(LocalDate, LocalDate)
├─ Uso:            GroomerService.obtenerTiemposPromedio(...)
├─ Endpoint:       GET /api/groomers/tiempos-promedio
└─ Propósito:      Obtiene tiempos promedio en un rango de fechas


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 9️⃣ NUEVAS ENTIDADES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

DETALLES DE SERVICIOS

📍 UBICACIÓN: DetalleServicioRepository.java

**sp_DetalleServiciosAtencion**
├─ Firma SQL:      CALL sp_DetalleServiciosAtencion(p_id_atencion)
├─ Método Java:    obtenerDetalleServiciosAtencionSP(Integer)
├─ Propósito:      Obtiene detalles de servicios de una atención

---

NOTIFICACIONES

📍 UBICACIÓN: NotificacionRepository.java

**sp_RegistrarNotificacion**
├─ Firma SQL:      CALL sp_RegistrarNotificacion(p_tipo, p_destinatario_id,
│                   p_canal, p_contenido, p_referencia_tipo, p_referencia_id)
├─ Método Java:    registrarNotificacion(String, Integer, String, String, String, Integer)
├─ Tipos:          sms, email, push
├─ Canales:        cliente, usuario
└─ Propósito:      Registra una notificación enviada

**sp_ObtenerNotificacionesCliente**
├─ Firma SQL:      CALL sp_ObtenerNotificacionesCliente(p_destinatario_id, p_limite)
├─ Método Java:    obtenerNotificacionesClienteSP(Integer, Integer)
└─ Propósito:      Obtiene notificaciones de un cliente

---

AUDITORÍA

📍 UBICACIÓN: AuditLogRepository.java

**sp_ObtenerLogsAuditoria**
├─ Firma SQL:      CALL sp_ObtenerLogsAuditoria(p_limite, p_entidad, p_accion)
├─ Método Java:    obtenerLogsAuditoriaSP(Integer, String, String)
├─ Acciones:       INSERT, UPDATE, DELETE
└─ Propósito:      Obtiene logs de auditoría con filtros

---

CONFIGURACIÓN DE ESTIMACIONES

📍 UBICACIÓN: ConfiguracionEstimacionRepository.java

**sp_AjustarTiempoEstimado**
├─ Firma SQL:      CALL sp_AjustarTiempoEstimado(p_id_servicio, p_id_groomer,
│                   p_tiempo_estimado_min)
├─ Método Java:    ajustarTiempoEstimado(Integer, Integer, Integer)
└─ Propósito:      Ajusta el tiempo estimado para un servicio/groomer

**sp_ObtenerEstimacionesTiempo**
├─ Firma SQL:      CALL sp_ObtenerEstimacionesTiempo()
├─ Método Java:    obtenerEstimacionesTiempoSP()
└─ Propósito:      Obtiene todas las configuraciones de tiempo

---

PAQUETES DE SERVICIOS

📍 UBICACIÓN: PaqueteServicioRepository.java

**sp_CrearPaqueteServicio**
├─ Firma SQL:      CALL sp_CrearPaqueteServicio(p_nombre, p_descripcion,
│                   p_precio_total)
├─ Método Java:    crearPaqueteServicio(String, String, BigDecimal)
└─ Propósito:      Crea un nuevo paquete de servicios

**sp_AgregarServicioPaquete**
├─ Firma SQL:      CALL sp_AgregarServicioPaquete(p_id_paquete, p_id_servicio,
│                   p_cantidad)
├─ Método Java:    agregarServicioPaquete(Integer, Integer, Integer)
└─ Propósito:      Agrega un servicio a un paquete


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 💡 NOTAS IMPORTANTES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. TRANSACTIONALIDAD:
   Todos los métodos que usan SPs están marcados con @Transactional
   Esto garantiza que si algo falla, se hace rollback automático

2. VALIDACIONES:
   - Java valida que los IDs existen ANTES de llamar al SP
   - Si no existe → RuntimeException 404
   - Si SP falla → RuntimeException 500 con mensaje detallado

3. LOGGING:
   - log.info() para operaciones exitosas
   - log.warn() para validaciones fallidas
   - log.error() para excepciones

4. ERROR HANDLING:
   - Try-catch alrededor de todos los SPs
   - ApiResponse estandarizada: {exito, mensaje, datos, error}
   - HttpStatus correcto en cada endpoint (200, 201, 400, 404, 500)

5. PARÁMETROS:
   - Todos usan @Param para nombrar parámetros
   - Tipos de dato coinciden exactamente con el SP
   - LocalDateTime ↔ DATETIME
   - String ↔ VARCHAR
   - Integer ↔ INT
   - BigDecimal ↔ DECIMAL

6. RETORNO:
   - SPs que modifican: @Modifying + @Transactional
   - SPs que consultan: List<Object[]> (para mapeo manual)
   - Conversión a DTOs en los controladores


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 📋 CHECKLIST DE MIGRACIÓN A SPs
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

COMPLETADO (30 SPs):
   ✅ Atenciones (4 SPs)
   ✅ Citas (5 SPs)
   ✅ Clientes (2 SPs)
   ✅ Mascotas (4 SPs)
   ✅ Facturas (4 SPs)
   ✅ Pagos (2 SPs)
   ✅ Servicios (4 SPs)
   ✅ Groomers (6 SPs)
   ✅ Nuevas Entidades (8 SPs)

PENDIENTE:
   ⏳ Módulo de Administración (UsuarioSistema, Sucursal)
   ⏳ Módulo de Reportes (Dashboard, Reportes)
   ⏳ Autenticación (Login, JWT)
   ⏳ Tests unitarios


╔════════════════════════════════════════════════════════════════════════════════╗
║                                                                                ║
║                  🎯 TODOS LOS SPs YA ESTÁN MAPEADOS EN JAVA 🎯              ║
║                                                                                ║
║              La lógica de negocio ahora RESIDE EN LA BASE DE DATOS            ║
║                Java solo hace validación y orquestación de llamadas            ║
║                                                                                ║
╚════════════════════════════════════════════════════════════════════════════════╝
