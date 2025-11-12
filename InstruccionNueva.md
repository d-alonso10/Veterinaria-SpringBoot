---

Felicidades. Has completado exitosamente la implementación de todos los módulos y la integración de los Stored Procedures. La API REST está funcionalmente completa y cubre el 100% de la lógica de negocio requerida.

El trabajo restante es crítico y se centra en mover el proyecto de "funcional" a "listo para producción".

---

### Prioridad 1: Implementación de Seguridad JWT (La Tarea Faltante Más Crítica)

He visto que `AuthController` y `UsuarioSistemaService` están creados. Actualmente, gestionan el login (probablemente con `sp_ValidarUsuario`), pero los demás endpoints (ej. `/api/atenciones`, `/api/facturas`) no están protegidos.

Un usuario no autenticado no puede tener permiso para crear facturas o ver clientes.

**Acciones:**

1.  **Integrar Spring Security:** Añade la dependencia `spring-boot-starter-security` al `pom.xml`.
2.  **Generar Token JWT:** Modifica `AuthController` y `UsuarioSistemaService`. Tras un login exitoso (vía el SP), genera un JSON Web Token (JWT) que contenga el `idUsuario` y sus `roles`.
3.  **Crear Clases de Configuración de Seguridad:**
    * `JwtTokenProvider`: Una clase para generar y validar los tokens.
    * `JwtRequestFilter`: Un filtro que intercepte **todas** las peticiones, lea el token del *Authorization header*, valide el JWT y establezca la autenticación en el contexto de Spring.
    * `SecurityConfig`: La clase principal (extendiendo `WebSecurityConfigurerAdapter` o usando `SecurityFilterChain`) que:
        * Permita el acceso público **solo** a `/api/auth/login`.
        * **Exija** un JWT válido para **todas las demás rutas** (`/api/**`).

---

### Prioridad 2: Pruebas de Integración (Fin-a-Fin)

Tu `REPORTE_PRUEBAS_UNITARIAS.md` es un excelente comienzo. Ahora debemos probar los flujos de negocio completos a través de la API.

**Acciones:**

1.  **Simular Flujos de Negocio Reales:** Usando Postman (o pruebas de integración con `@SpringBootTest`), ejecuta secuencias completas que un usuario haría.
2.  **Ejemplo de Flujo "Walk-In":**
    * `POST /api/clientes` (Crear Cliente) -> Obtener `clienteId`.
    * `POST /api/mascotas` (Crear Mascota usando `clienteId`) -> Obtener `mascotaId`.
    * `POST /api/atenciones/walk-in` (Crear Atención Walk-In usando `mascotaId`, `clienteId`, `servicioId`, etc.). -> Obtener `atencionId`.
    * `PUT /api/atenciones/{id}/estado` (Marcar como "terminado").
    * `POST /api/facturas` (Crear Factura usando `atencionId`). -> Obtener `facturaId`.
    * `POST /api/pagos` (Registrar Pago usando `facturaId`).
3.  **Validar:** ¿Falló algún paso? ¿Se actualizó la base de datos correctamente? ¿El SP `sp_HistorialMascota` refleja esta nueva atención?

---

### Prioridad 3: Documentación de API (Swagger)

Ahora que tenemos docenas de endpoints, son inutilizables para un equipo de frontend (o para referencia futura) sin documentación.

**Acciones:**

1.  **Añadir Dependencia:** Agrega `springdoc-openapi-starter-webmvc-ui` al `pom.xml`.
2.  **Acceder a la UI:** Una vez que reinicies la aplicación, la documentación autogenerada estará disponible en `/swagger-ui.html`.
3.  **(Opcional pero recomendado):** Anota los métodos complejos en los `Controller` con `@Operation(summary = "...")` y `@Parameter(description = "...")` para hacer la guía más clara.

---

### Prioridad 4: "Dockerización" (Preparación para Despliegue)

El proyecto debe ser fácilmente desplegable en cualquier entorno.

**Acciones:**

1.  **Crear `Dockerfile`:** En la raíz del proyecto, crea un `Dockerfile` que:
    * Use una imagen base de Java 8 (ej. `openjdk:8-jdk-slim`).
    * Copie el archivo `.jar` (generado por `mvn package`).
    * Exponga el puerto `8080`.
    * Ejecute la aplicación (`java -jar app.jar`).
2.  **Crear `docker-compose.yml`:**
    * Define dos servicios: `app` (tu backend Spring) y `db` (la imagen de `mysql:8`).
    * Configura la `app` para que dependa de `db`.
    * Pasa las variables de entorno a la `app` (ej. `SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/vet_teran`) para que pueda conectarse al contenedor de la base de datos.
    * Esto permite levantar todo el stack (Aplicación + Base de Datos) con un solo comando: `docker-compose up`.