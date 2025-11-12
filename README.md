# TeranVet - Sistema de Gestión de Veterinaria

Sistema integral de gestión para una clínica veterinaria, desarrollado con **Spring Boot 2.7**, **Java 8**, **MySQL** y **API RESTful**.

## 📋 Descripción del Proyecto

Este es un proyecto de migración y reconstrucción de un sistema de gestión veterinaria desde JSP/Servlets a **Spring Boot**, creando una arquitectura moderna, escalable y mantenible con separación clara entre backend (API REST) y frontend.

El sistema gestiona:
- **Clientes** (dueños de mascotas)
- **Mascotas** (pacientes)
- **Citas** (agendamientos)
- **Atenciones** (servicios en progreso)
- **Servicios** (oferta de servicios)
- **Groomers/Personal** (empleados)
- **Facturación y Pagos**
- **Reportes y Dashboard**
- **Auditoría y Notificaciones**

## 🛠️ Stack Tecnológico

- **Framework:** Spring Boot 2.7.14
- **Lenguaje:** Java 8
- **Base de Datos:** MySQL 8.0
- **Gestor de Dependencias:** Maven 3.6+
- **ORM:** Spring Data JPA / Hibernate
- **Autenticación:** JWT (JSON Web Tokens)
- **Logging:** SLF4J + Logback
- **IDE Recomendada:** NetBeans 8.2+ o IntelliJ IDEA / Eclipse

## 📁 Estructura del Proyecto

```
veterinaria-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/com/teranvet/
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── service/             # Servicios (lógica de negocio)
│   │   │   ├── entity/              # Entidades JPA
│   │   │   ├── repository/          # Repositorios (acceso a datos)
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── config/              # Configuración
│   │   │   └── TeranvetApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── vet_teran_mysql.sql              # Script de base de datos
└── plantilla_menu.html              # Plantilla frontend
```

## 🔧 Configuración

### 1. Base de Datos

Crear la base de datos ejecutando el script:

```sql
mysql -u root < vet_teran_mysql.sql
```

### 2. Configuración de Propiedades

Editar `src/main/resources/application.properties`:

```properties
# Base de Datos
spring.datasource.url=jdbc:mysql://localhost:3306/vet_teran
spring.datasource.username=root
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=none

# Servidor
server.port=8080
server.servlet.context-path=/api

# JWT
jwt.secret=teranvet_secret_key_2025
jwt.expiration=86400000
```

### 3. Compilación y Ejecución

**Compilar:**
```bash
mvn clean install
```

**Ejecutar:**
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api`

## 📚 Módulos y Endpoints

### 1. Módulo de Clientes

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/clientes` | Obtener todos los clientes |
| GET | `/api/clientes/{id}` | Obtener cliente por ID |
| GET | `/api/clientes/buscar/{termino}` | Buscar clientes por término |
| POST | `/api/clientes` | Crear nuevo cliente |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |

**Ejemplo de Solicitud POST:**
```json
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

### 2. Módulo de Mascotas

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/mascotas` | Obtener todas las mascotas |
| GET | `/api/mascotas/{id}` | Obtener mascota por ID |
| GET | `/api/mascotas/cliente/{idCliente}` | Obtener mascotas de un cliente |
| POST | `/api/mascotas` | Registrar nueva mascota |
| PUT | `/api/mascotas/{id}` | Actualizar mascota |
| DELETE | `/api/mascotas/{id}` | Eliminar mascota |

### 3. Módulo de Citas

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/citas` | Obtener todas las citas |
| GET | `/api/citas/{id}` | Obtener cita por ID |
| GET | `/api/citas/cliente/{idCliente}` | Próximas citas de cliente |
| POST | `/api/citas` | Crear nueva cita |
| PUT | `/api/citas/{id}` | Reprogramar cita |
| DELETE | `/api/citas/{id}` | Cancelar cita |

### 4. Módulo de Servicios

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/servicios` | Obtener todos los servicios |
| GET | `/api/servicios/{id}` | Obtener servicio por ID |
| GET | `/api/servicios/categoria/{categoria}` | Servicios por categoría |
| POST | `/api/servicios` | Crear nuevo servicio |
| PUT | `/api/servicios/{id}` | Actualizar servicio |
| DELETE | `/api/servicios/{id}` | Eliminar servicio |

### 5. Módulo de Atenciones

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/atenciones` | Obtener todas las atenciones |
| GET | `/api/atenciones/{id}` | Obtener atención por ID |
| GET | `/api/atenciones/cola/{idSucursal}` | Cola de atención por sucursal |
| POST | `/api/atenciones` | Crear nueva atención |
| PUT | `/api/atenciones/{id}` | Actualizar estado de atención |

### 6. Módulo de Facturación

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/facturas` | Obtener todas las facturas |
| GET | `/api/facturas/{id}` | Obtener factura por ID |
| GET | `/api/facturas/cliente/{idCliente}` | Facturas por cliente |
| POST | `/api/facturas` | Crear nueva factura |
| PUT | `/api/facturas/{id}` | Actualizar factura |

### 7. Módulo de Pagos

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/pagos` | Obtener todos los pagos |
| GET | `/api/pagos/factura/{idFactura}` | Pagos de una factura |
| POST | `/api/pagos` | Registrar nuevo pago |

### 8. Módulo de Autenticación

| Método | Endpoint | Descripción |
|--------|----------|------------|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/registro` | Registrar nuevo usuario |
| POST | `/api/auth/logout` | Cerrar sesión |

### 9. Módulo de Reportes

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/reportes/dashboard` | Métricas principales |
| GET | `/api/reportes/ingresos` | Reporte de ingresos |
| GET | `/api/reportes/servicios-solicitados` | Servicios más solicitados |
| GET | `/api/reportes/clientes-frecuentes` | Clientes frecuentes |

## 🗄️ Entidades (Modelos)

### Cliente
- idCliente (PK)
- nombre, apellido
- dniRuc (UNIQUE)
- email, telefono, direccion
- preferencias (JSON)
- timestamps

### Mascota
- idMascota (PK)
- idCliente (FK)
- nombre, especie, raza
- sexo, fechaNacimiento
- microchip, observaciones
- timestamps

### Cita
- idCita (PK)
- idMascota, idCliente, idSucursal, idServicio (FK)
- fechaProgramada
- modalidad (presencial/virtual)
- estado (reservada, confirmada, asistio, cancelada, no_show)
- notas, timestamps

### Atencion
- idAtencion (PK)
- idCita, idMascota, idCliente, idGroomer, idSucursal (FK)
- estado (en_espera, en_servicio, pausado, terminado)
- tiempoEstimado/Real (inicio/fin)
- prioridad, observaciones, timestamps

### Factura
- idFactura (PK)
- idAtencion, idCliente (FK)
- serie, numero
- fechaEmision
- subtotal, impuesto, total
- estado (pendiente, confirmado, anulado)
- timestamps

### Pago
- idPago (PK)
- idFactura (FK)
- fechaPago, monto
- metodo (efectivo, tarjeta, transfer, otro)
- referencia, estado, timestamps

## 🔐 Autenticación y Autorización

El sistema utiliza **JWT (JSON Web Tokens)** para autenticación.

**Flujo de Autenticación:**

1. Cliente envía credenciales (email + password)
2. Sistema valida y genera JWT
3. Cliente incluye JWT en header: `Authorization: Bearer <token>`
4. Sistema valida JWT en cada solicitud

**Roles Disponibles:**
- `admin` - Administrador total
- `recepcionista` - Gestión de clientes y citas
- `groomer` - Personal de servicio
- `veterinario` - Atención médica
- `contador` - Reportes financieros

## 📊 Procedimientos Almacenados

El sistema invoca procedimientos almacenados de MySQL para lógica compleja:

- `sp_CrearCita` - Agendar cita
- `sp_CrearAtencionDesdeCita` - Convertir cita a atención
- `sp_ObtenerColaActual` - Obtener cola de atención
- `sp_ObtenerMetricasDashboard` - Métricas del dashboard
- `sp_ReporteIngresos` - Ingresos por rango de fechas
- `sp_ClientesFrecuentes` - Top clientes
- `sp_ServiciosMasSolicitados` - Servicios populares

Más detalles en `vet_teran_mysql.sql`

## 🎨 Plantilla Frontend

La plantilla `plantilla_menu.html` proporciona:

- **Sidebar** con menú navegable
- **Dashboard** con estadísticas
- **Tarjetas de stats** animadas
- **Acciones rápidas** de usuario
- **Diseño responsivo** (mobile-friendly)
- **Paleta de colores** TeranVet
- **Componentes reutilizables**

**Colores Principales:**
- Primary: `#abcbd5` (Azul claro)
- Secondary: `#d5c4ad` (Beige)
- Accent: `#d5adc7` (Rosa)
- Success: `#4CAF50`
- Warning: `#FFC107`
- Danger: `#F44336`

## 🚀 Deployment

### Docker

```dockerfile
FROM openjdk:8-jdk-alpine
COPY target/veterinaria-spring-boot-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build con Maven

```bash
mvn clean package -DskipTests
```

Genera: `target/veterinaria-spring-boot-1.0.0.jar`

## 🧪 Testing

Ejecutar tests:

```bash
mvn test
```

## 📝 Logging

Los logs se generan con SLF4J:

```properties
logging.level.root=INFO
logging.level.com.teranvet=DEBUG
```

Archivo: `logs/teranvet.log`

## 🤝 Contribuciones

Por favor seguir las convenciones:

1. Rama `main` - Producción
2. Rama `develop` - Desarrollo
3. Pull Requests con descripción clara

## 📞 Contacto

**Equipo TeranVet**
- Email: info@teranvet.com
- Teléfono: +51 123 456 789

## 📄 Licencia

Este proyecto es propiedad de TeranVet y está bajo licencia propietaria.

---

**Versión:** 1.0.0  
**Última actualización:** Noviembre 2025
