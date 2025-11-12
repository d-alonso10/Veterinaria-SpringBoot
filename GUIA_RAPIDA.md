# 📋 Guía Rápida de Integración - TeranVet Spring Boot

## ✅ Estado del Proyecto

El proyecto **TeranVet** ha sido estructurado completamente con la arquitectura Spring Boot moderna y está listo para desarrollo.

## 🏗️ Estructura Creada

### 1. Configuración Base
- ✅ `pom.xml` - Dependencias Maven completas
- ✅ `application.properties` - Configuración de BD, servidor, JWT
- ✅ `TeranvetApplication.java` - Clase principal de Spring Boot

### 2. Entidades JPA (Entity Layer)
Todas las entidades mapeadas a la BD:
- ✅ `Cliente.java` - Clientes/Dueños
- ✅ `Mascota.java` - Pacientes (con FK a Cliente)
- ✅ `UsuarioSistema.java` - Usuarios del sistema
- ✅ `Servicio.java` - Servicios ofrecidos
- ✅ `Sucursal.java` - Sucursales
- ✅ `Groomer.java` - Personal de atención
- ✅ `Cita.java` - Agendamientos
- ✅ `Atencion.java` - Servicios en ejecución
- ✅ `Factura.java` - Facturación
- ✅ `Pago.java` - Registros de pago
- ✅ `Promocion.java` - Promociones

### 3. Repositorios (Repository Layer)
- ✅ `ClienteRepository` - CRUD + búsquedas
- ✅ `MascotaRepository` - CRUD + por cliente
- ✅ `UsuarioSistemaRepository` - Login
- ✅ `ServicioRepository` - Por categoría, búsqueda
- ✅ `CitaRepository` - Próximas citas, por fecha
- ✅ `AtencionRepository` - Cola actual, del día
- ✅ `FacturaRepository` - Por cliente, rango fechas
- ✅ `PagoRepository` - Por factura, confirmados
- ✅ `GroomerRepository` - Búsqueda por nombre
- ✅ `SucursalRepository` - CRUD básico
- ✅ `PromocionRepository` - Activas

### 4. DTOs (Data Transfer Objects)
- ✅ `ApiResponse<T>` - Respuesta estándar JSON
- ✅ `ClienteDTO` - Para transferencia de datos de Cliente
- ✅ `MascotaDTO` - Para transferencia de datos de Mascota
- ✅ `CitaDTO` - Para transferencia de datos de Cita
- ✅ `LoginRequest` - Credenciales de login
- ✅ `LoginResponse` - Token y datos de usuario

### 5. Servicios (Service Layer)
- ✅ `ClienteService` - Lógica CRUD + búsquedas
- ✅ `MascotaService` - Lógica CRUD + por cliente
- ✅ `CitaService` - Crear, reprogramar, cancelar, confirmar asistencia

### 6. Controladores REST (Controller Layer)
- ✅ `ClienteController` - Endpoints CRUD de clientes
- ✅ `MascotaController` - Endpoints CRUD de mascotas
- ✅ `CitaController` - Endpoints CRUD y acciones de citas

## 🔌 Endpoints Implementados

### Clientes
```
GET    /api/clientes              - Listar todos
GET    /api/clientes/{id}         - Obtener uno
GET    /api/clientes/buscar/{t}   - Buscar por término
POST   /api/clientes              - Crear
PUT    /api/clientes/{id}         - Actualizar
DELETE /api/clientes/{id}         - Eliminar
```

### Mascotas
```
GET    /api/mascotas              - Listar todos
GET    /api/mascotas/{id}         - Obtener uno
GET    /api/mascotas/cliente/{id} - Por cliente
GET    /api/mascotas/buscar/{t}   - Buscar
POST   /api/mascotas              - Crear
PUT    /api/mascotas/{id}         - Actualizar
DELETE /api/mascotas/{id}         - Eliminar
```

### Citas
```
GET    /api/citas                           - Listar todos
GET    /api/citas/{id}                      - Obtener uno
GET    /api/citas/cliente/{id}              - Por cliente
GET    /api/citas/cliente/{id}/proximas    - Próximas
POST   /api/citas                           - Crear
PUT    /api/citas/{id}/reprogramar         - Reprogramar
PUT    /api/citas/{id}/cancelar            - Cancelar
PUT    /api/citas/{id}/confirmar-asistencia - Confirmar
PUT    /api/citas/{id}/no-show             - No-show
```

## 📦 Por Implementar (TODO)

### Servicios y Controladores Pendientes:
- [ ] `UsuarioService` + `AutenticacionController` (Login/JWT)
- [ ] `ServicioService` + `ServicioController`
- [ ] `AtencionService` + `AtencionController`
- [ ] `FacturaService` + `FacturaController`
- [ ] `PagoService` + `PagoController`
- [ ] `GroomerService` + `GroomerController`
- [ ] `SucursalService` + `SucursalController`
- [ ] `PromocionService` + `PromocionController`
- [ ] `ReporteService` + `ReporteController` (Dashboard, Ingresos, etc.)

### Configuración Adicional:
- [ ] Configuración de Seguridad (SecurityConfig)
- [ ] Filtro JWT (JwtAuthenticationFilter)
- [ ] Manejo Global de Excepciones (GlobalExceptionHandler)
- [ ] Validación de entrada (Bean Validation)
- [ ] Configuración de CORS completa
- [ ] Documentación Swagger/OpenAPI

### Testing:
- [ ] Test unitarios para servicios
- [ ] Test de integración para controladores
- [ ] Test de endpoints REST

## 🗄️ Base de Datos

### Instalación:

1. Asegurate que MySQL 8.0+ esté corriendo
2. Ejecuta el script de la BD:
```bash
mysql -u root < vet_teran_mysql.sql
```

3. Verifica que la BD `vet_teran` se haya creado:
```sql
USE vet_teran;
SHOW TABLES;
```

## 🚀 Próximos Pasos Recomendados

### 1. **Implementar Autenticación (PRIORITARIO)**
```java
// UsuarioService.java
- Validar credenciales
- Generar JWT
- Renovar token
```

### 2. **Crear Servicio de Reportes**
```java
// ReporteService.java
- Dashboard metrics
- Ingresos por fecha
- Clientes frecuentes
- Servicios más solicitados
```

### 3. **Implementar Atenciones (Cola)**
```java
// AtencionService.java
- Crear atención desde cita
- Crear walk-in
- Obtener cola actual
- Cambiar estado
```

### 4. **Integrar Facturación**
```java
// FacturaService + PagoService
- Generar facturas
- Registrar pagos
- Estados de factura
```

### 5. **Seguridad y Validación**
- GlobalExceptionHandler para errores
- JWT interceptor
- Validación de entrada con anotaciones

## 📝 Ejemplo de Uso - Crear Cliente

```bash
# Request
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "dniRuc": "12345678",
    "email": "juan@mail.com",
    "telefono": "987654321",
    "direccion": "Calle Principal 100",
    "preferencias": "{\"comunicacion\": \"whatsapp\"}"
  }'

# Response
{
  "exito": true,
  "mensaje": "Cliente creado exitosamente",
  "datos": {
    "idCliente": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    ...
  }
}
```

## 📚 Documentación Importante

- `README.md` - Documentación completa del proyecto
- `instrucciones.md` - Requisitos y especificaciones
- `vet_teran_mysql.sql` - Schema y procedimientos almacenados
- `plantilla_menu.html` - Referencia visual del frontend

## 🎨 Integración con Frontend

La plantilla `plantilla_menu.html` proporciona:
- Sidebar navegable
- Dashboard con estadísticas
- Diseño responsivo
- Componentes reutilizables
- Paleta de colores TeranVet

**Pasos de integración:**
1. Servir HTML desde Spring Boot o servidor separado
2. Consumir endpoints de `/api/` desde JavaScript
3. Manejar respuestas con formato `ApiResponse<T>`
4. Implementar formularios para CRUD operations

## 🔧 Compilar y Ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# Ejecutar JAR generado
java -jar target/veterinaria-spring-boot-1.0.0.jar
```

## 📞 Contacto

Equipo TeranVet - Desarrollo Spring Boot
Versión: 1.0.0 (En Progreso)
