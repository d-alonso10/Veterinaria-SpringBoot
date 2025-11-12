Claro, aquí tienes un borrador de "prompt" o conjunto de requerimientos para solicitar el desarrollo de este proyecto desde cero, migrándolo a Spring Boot.

-----

## Prompt de Proyecto: Sistema de Gestión de Veterinaria (Migración a Spring Boot)

### 1\. Objetivo General

El objetivo de este proyecto es (re)construir un sistema integral de gestión para una clínica veterinaria, basándose en las funcionalidades de un proyecto existente (hecho en JSP/Servlets). La nueva versión debe ser desarrollada utilizando el framework **Spring Boot**, creando una API RESTful moderna, mantenible y escalable.

El sistema debe gestionar pacientes (mascotas), clientes (dueños), citas, atenciones, personal (groomers), servicios, facturación e informes.

### 2\. Stack Tecnológico Requerido

  * **Framework Backend:** Spring Boot
  * **Lenguaje:** Java 8
  * **IDE (Preferencia):** NetBeans 8.2
  * **Base de Datos:** MySQL
  * **Gestión de Dependencias:** Maven
  * **Arquitectura:** API RESTful (backend) separada del frontend. El frontend se conectará a esta API.

### 3\. Configuración de la Base de Datos

Se proporcionarán dos archivos SQL:

1.  `Base de datos Veterinaria.sql`: Contiene el esquema completo (todas las tablas, relaciones, etc.).
2.  `stored_procedures_veterinaria.sql`: Contiene todos los procedimientos almacenados que utiliza la lógica de negocio.

**La lógica de negocio depende fuertemente de estos procedimientos almacenados. El backend debe invocarlos.**

#### Conexión en Spring Boot

La conexión a la base de datos debe configurarse en el archivo `src/main/resources/application.properties` de la siguiente manera:

```properties
# Configuración de la Base de Datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/vet_teran
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

# Configuración de JPA (Spring Data)
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=none # 'none' porque usaremos el script SQL proporcionado
```

### 4\. Arquitectura del Backend

1.  **Modelo (Entidades):**
      * Crear clases de entidad (POJOs con anotaciones `@Entity`) que mapeen las tablas de la base de datos (ej. `Cliente`, `Mascota`, `Cita`, `Atencion`, `Factura`, etc.).
2.  **Repositorios (Spring Data JPA):**
      * Crear interfaces de repositorio (extendiendo `JpaRepository`) para el acceso básico a datos (CRUD).
      * Para la lógica de negocio que depende de **procedimientos almacenados**, se deben implementar métodos que los invoquen, ya sea con la anotación `@Query(nativeQuery=true, value="CALL ...")` o utilizando `SimpleJdbcCall` / `JdbcTemplate`.
3.  **Capa de Servicio (`@Service`):**
      * Toda la lógica de negocio (reglas, cálculos, orquestación de llamadas a repositorios) debe residir en esta capa.
4.  **Capa de Controladores (`@RestController`):**
      * Exponer la lógica de negocio como **endpoints de API REST**.
      * Deben manejar las solicitudes HTTP (GET, POST, PUT, DELETE) y devolver respuestas en formato JSON.

### 5\. Funcionalidades Detalladas (Módulos)

El sistema debe implementar las siguientes funcionalidades, agrupadas por módulo:

#### Módulo de Autenticación y Usuarios

  * **Login:** Endpoint de autenticación para `UsuarioSistema`.
  * **Gestión de Usuarios:** CRUD completo para los usuarios del sistema (administradores, recepcionistas, etc.).

#### Módulo de Clientes

  * **CRUD de Clientes:** Altas, Bajas, Modificaciones y Listado de clientes.
  * **Búsqueda de Clientes:** Búsqueda por DNI, nombre o apellido.
  * **Reporte de Clientes Frecuentes:** Endpoint que devuelva los clientes con más visitas/atenciones.

#### Módulo de Mascotas (Pacientes)

  * **CRUD de Mascotas:** Altas, Bajas, Modificaciones y Listado de mascotas. Cada mascota debe estar asociada a un cliente.
  * **Búsqueda de Mascotas:** Búsqueda por nombre o por cliente.
  * **Listar Mascotas por Cliente:** Endpoint que reciba un ID de cliente y devuelva todas sus mascotas.
  * **Historial de Mascota:** Endpoint que devuelva todas las atenciones, citas y diagnósticos de una mascota específica.

#### Módulo de Citas

  * **Gestión de Citas:** CRUD completo para Citas.
  * **Crear Cita:** Agendar una nueva cita para una mascota, seleccionando servicio y groomer (si aplica).
  * **Reprogramar Cita:** Modificar la fecha/hora de una cita existente.
  * **Cancelar Cita:** Marcar una cita como cancelada.
  * **Listar Próximas Citas:** Endpoint para ver las citas agendadas (ej. para el día o la semana).
  * **Diagnóstico de Citas:** (Funcionalidad de utilidad/debug para revisar estados de citas).

#### Módulo de Atención y Cola

  * **Crear Atención (Walk-In):** Registrar una atención para un cliente que llega sin cita previa.
  * **Crear Atención desde Cita:** Convertir una cita agendada en una atención activa cuando el cliente llega.
  * **Gestión de Cola de Atención:** Endpoint que muestre las mascotas en espera de ser atendidas (en cola), su orden y el estado actual.
  * **Actualizar Estado de Atención:** Modificar el estado de una atención (ej. "En espera", "En proceso", "Finalizada", "Pagada").

#### Módulo de Personal (Groomers)

  * **CRUD de Groomers:** Altas, Bajas y Modificaciones del personal (veterinarios, peluqueros).
  * **Disponibilidad de Groomers:** Endpoint para consultar la disponibilidad (horarios) de un groomer.
  * **Reporte de Ocupación:** Endpoint que muestre la carga de trabajo o porcentaje de ocupación de los groomers.
  * **Reporte de Tiempos Promedio:** Endpoint que calcule el tiempo promedio que tarda cada groomer por servicio.

#### Módulo de Servicios y Paquetes

  * **CRUD de Servicios:** Gestión de los servicios ofrecidos por la veterinaria (ej. "Consulta", "Baño", "Corte", "Vacunación") con sus precios.
  * **Gestión de Detalles de Servicio:** CRUD para los sub-ítems o detalles de un servicio.
  * **Gestión de Paquetes:** Crear paquetes que agrupen varios servicios (posiblemente con descuento).
  * **Gestión de Promociones:** CRUD para promociones aplicables a servicios o paquetes.
  * **Reportes de Servicios:**
      * Servicios por Categoría.
      * Servicios Más Solicitados.

#### Módulo de Facturación y Pagos

  * **Crear Factura:** Generar una factura asociada a una o varias atenciones/servicios.
  * **Listar y Buscar Facturas:** Consultar facturas por cliente, fecha o estado (pendiente, pagada).
  * **Registrar Pago:** Aplicar un pago (parcial o total) a una factura.
  * **Listar Pagos:** Ver un historial de los pagos recibidos.

#### Módulo de Reportes y Dashboard

  * **Dashboard:** Endpoint principal que devuelva métricas clave (ej. citas del día, pacientes en espera, ingresos del día).
  * **Reporte de Ingresos:** Endpoint para consultar ingresos totales en un rango de fechas.

#### Módulo de Configuración y Auditoría

  * **Gestión de Sucursales:** CRUD para las diferentes sucursales de la veterinaria.
  * **Logs de Auditoría:** Endpoint para consultar el `AuditLog` (quién hizo qué y cuándo).
  * **Estimaciones de Tiempo:** CRUD para configurar los tiempos estimados de duración de cada servicio (usado para la cola y agenda).

#### Módulo de Notificaciones

  * **CRUD de Notificaciones:** Sistema interno para crear y listar notificaciones (ej. recordatorios de citas, avisos de promociones).