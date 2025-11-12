El proyecto tiene una base estructural sólida. Sin embargo, la implementación de la lógica de negocio y la cobertura de funcionalidades están incompletas. El siguiente plan de acción debe ejecutarse para finalizar el backend.

-----

### Prioridad 1: Integrar Lógica de Negocio (Procedimientos Almacenados)

Esta es la tarea más crítica. La lógica de negocio no debe residir en los servicios de Java, sino en los SPs de la base de datos, tal como en el proyecto original.

**Acciones:**

1.  **Revisar todos los `Service` (ej. `AtencionService`):**
      * Identificar todos los métodos que crean o modifican datos (ej. `crearAtencion`, `actualizarEstado`).
2.  **Modificar los `Repository` (ej. `AtencionRepository`):**
      * Por cada método de servicio que deba usar un SP, crea un método en el repositorio que lo llame.
      * Utiliza la anotación `@Query` con `nativeQuery = true` o `@Procedure`.

**Ejemplo (basado en tu código de `AtencionDao.java`):**

En tu `AtencionRepository.java`, en lugar de solo extender `JpaRepository`, añade esto:

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// ... otras importaciones

public interface AtencionRepository extends JpaRepository<Atencion, Integer> {

    // Método para llamar al SP de crear Atencion Walk-In
    // Asegúrate de que los nombres de parámetros (:mascotaId, :servicioId, etc.) coincidan
    // con los @Param y los nombres en tu SP.
    @Modifying // Necesario para consultas que modifican datos (INSERT, UPDATE, DELETE)
    @Transactional // Necesario para que la modificación se ejecute en una transacción
    @Query(value = "CALL sp_crear_atencion_walkin(:mascotaId, :servicioId, :groomerId, :sucursalId)", nativeQuery = true)
    void crearAtencionWalkIn(@Param("mascotaId") int mascotaId, 
                             @Param("servicioId") int servicioId, 
                             @Param("groomerId") int groomerId, 
                             @Param("sucursalId") int sucursalId);

    // Método para el SP de actualizar estado
    @Modifying
    @Transactional
    @Query(value = "CALL sp_actualizar_estado_atencion(:atencionId, :nuevoEstado)", nativeQuery = true)
    void actualizarEstadoAtencion(@Param("atencionId") int atencionId, 
                                  @Param("nuevoEstado") String nuevoEstado);

    // ... otros métodos para SPs (ej. listar cola, crear desde cita, etc.)
}
```

3.  **Actualizar el `Service` (ej. `AtencionService.java`):**
      * Tu servicio ahora debe llamar a estos nuevos métodos del repositorio, en lugar de `repository.save()`.

<!-- end list -->

```java
// En AtencionService.java
@Autowired
private AtencionRepository atencionRepository;

// Este método ahora usará el SP
public void crearAtencionWalkIn(Atencion datos) { 
    // Asumiendo que 'datos' tiene los IDs necesarios
    atencionRepository.crearAtencionWalkIn(
        datos.getMascota().getId(),
        datos.getServicio().getId(),
        datos.getGroomer().getId(),
        datos.getSucursal().getId()
    );
}

// ... otros métodos del servicio llamando a los SPs correspondientes
```

-----

### Prioridad 2: Completar Módulos Faltantes

Debes crear el `Controller`, `Service`, `Repository` (con sus SPs) y `DTOs` para todos los módulos restantes del proyecto original.

**Checklist de Módulos Faltantes:**

  * [ ] **Módulo de Facturación:**
      * `FacturaController`, `FacturaService`, `FacturaRepository`
      * Endpoints para: Crear Factura (SP), Listar Facturas, Buscar Facturas (SP).
  * [ ] **Módulo de Pagos:**
      * `PagoController`, `PagoService`, `PagoRepository`
      * Endpoints para: Registrar Pago (SP), Listar Pagos.
  * [ ] **Módulo de Personal:**
      * `GroomerController`, `GroomerService`, `GroomerRepository`
      * Endpoints para: CRUD de Groomers, Disponibilidad (SP), Ocupación (SP), Tiempos Promedio (SP).
  * [ ] **Módulo de Administración:**
      * `UsuarioSistemaController` (para CRUD de Usuarios).
      * `SucursalController` (para CRUD de Sucursales).
      * `AuditController` (para ver Logs de Auditoría - SP).
      * `ConfiguracionController` (para gestionar estimaciones de tiempo - SP).
  * [ ] **Módulo de Reportes:**
      * `DashboardController`: Endpoints para las métricas del Dashboard (SPs).
      * `ReporteController`: Endpoints para Clientes Frecuentes, Ingresos, Servicios Más Solicitados (todos vía SPs).
  * [ ] **Módulo de Servicios (Avanzado):**
      * `DetalleServicioController`, `PaqueteServicioController`, `PromocionController` (CRUDs y SPs correspondientes).
  * [ ] **Módulo de Notificaciones:**
      * `NotificacionController` (CRUD y SPs).

-----

### Prioridad 3: Implementar Autenticación (Login)

Veo DTOs `LoginRequest` y `LoginResponse`, pero falta el controlador.

**Acciones:**

1.  Crear un `AuthController` o añadir a `UsuarioSistemaController`.
2.  Implementar un endpoint `/api/auth/login` que:
      * Reciba un `LoginRequest`.
      * Use `UsuarioSistemaRepository` para llamar al SP de login (o validar contra la tabla).
      * Devuelva un `LoginResponse` (idealmente con un token JWT, pero un simple objeto de usuario o un booleano de éxito es un comienzo).

-----

### Prioridad 4: Mapear Entidades Faltantes

Asegúrate de que todas las tablas de la base de datos (especialmente las usadas por los módulos faltantes) estén mapeadas en el paquete `com.teranvet.entity`.

**Entidades Faltantes Detectadas:**

  * [ ] `DetalleServicio.java`
  * [ ] `PaqueteServicio.java`
  * [ ] `PaqueteServicioItem.java`
  * [ ] `Notificacion.java`
  * [ ] `AuditLog.java`
  * [ ] `ConfiguracionEstimacion.java`