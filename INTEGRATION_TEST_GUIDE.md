# Guía de Pruebas de Integración - TeranVet API

**Fecha:** 2025-11-12  
**Versión:** 2.0 (Fase 2: Pruebas con JWT)  
**Estado:** Listo para ejecutar

---

## 1. CONFIGURACIÓN PREVIA

### 1.1 Requisitos
- ✅ JWT implementado y compilando sin errores
- ✅ API Spring Boot corriendo en `http://localhost:8080`
- ✅ Base de datos MySQL conectada
- ✅ Postman/Postman CLI instalado
- ✅ Variables de entorno configuradas

### 1.2 Variables de Entorno Postman

```javascript
{
  "jwt_token": "{{generado-en-login}}",
  "cliente_id": "{{generado-en-crear-cliente}}",
  "mascota_id": "{{generado-en-crear-mascota}}",
  "atencion_id": "{{generado-en-crear-atencion}}",
  "factura_id": "{{generado-en-crear-factura}}",
  "base_url": "http://localhost:8080"
}
```

### 1.3 Credenciales de Prueba

```
Email: admin@example.com
Password: admin123
```

---

## 2. PLAN DE PRUEBAS

### 2.1 FASE 1: Autenticación (Prioridad Alta)

#### Test: Login - Obtener JWT Token
**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "email": "admin@example.com",
  "passwordHash": "admin123"
}
```

**Expected Response:** 200 OK
```json
{
  "exito": true,
  "mensaje": "Login exitoso",
  "data": {
    "idUsuario": 1,
    "nombre": "Admin Teran",
    "email": "admin@example.com",
    "rol": "ADMIN",
    "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer"
  }
}
```

**Assertions:**
- ✓ Status code = 200
- ✓ Response contiene token
- ✓ tokenType = "Bearer"
- ✓ idUsuario existe
- ✓ Token válido (no está vacío)

**Próximo Paso:** Guardar token en variable `jwt_token` para usar en todas las pruebas posteriores

---

### 2.2 FASE 2: Flujo Walk-In Completo

#### 2.2.1 Test: Crear Cliente
**Endpoint:** `POST /api/clientes`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Request:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "dniRuc": "12345678",
  "email": "juan.perez@example.com",
  "telefono": "987654321",
  "direccion": "Av. Principal 123"
}
```

**Expected Response:** 201 Created
```json
{
  "exito": true,
  "mensaje": "Cliente creado exitosamente",
  "data": {
    "idCliente": 5,
    "nombre": "Juan",
    "apellido": "Pérez",
    "dniRuc": "12345678",
    "email": "juan.perez@example.com",
    "telefono": "987654321",
    "direccion": "Av. Principal 123",
    "fechaRegistro": "2025-11-12T10:00:00"
  }
}
```

**Assertions:**
- ✓ Status code = 201
- ✓ idCliente existe y es numérico
- ✓ Todos los campos retornan correctamente

**Próximo Paso:** Guardar `idCliente` en variable

---

#### 2.2.2 Test: Crear Mascota
**Endpoint:** `POST /api/mascotas`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Request:**
```json
{
  "idCliente": {{cliente_id}},
  "nombre": "Rex",
  "especie": "perro",
  "raza": "Labrador",
  "sexo": "macho",
  "fechaNacimiento": "2020-01-15",
  "microchip": "MC123456",
  "observaciones": "Perro muy juguetón"
}
```

**Expected Response:** 201 Created

**Assertions:**
- ✓ Status code = 201
- ✓ idMascota existe
- ✓ idCliente coincide con el enviado
- ✓ Relación cliente-mascota creada

**Próximo Paso:** Guardar `idMascota` en variable

---

#### 2.2.3 Test: Crear Atención Walk-In
**Endpoint:** `POST /api/atenciones/walk-in`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Request:**
```json
{
  "idMascota": {{mascota_id}},
  "idCliente": {{cliente_id}},
  "idGroomer": 1,
  "idSucursal": 1,
  "turnoNum": 1,
  "tiempoEstimadoInicio": "2025-11-12T10:00:00",
  "tiempoEstimadoFin": "2025-11-12T10:45:00",
  "prioridad": 0,
  "observaciones": "Baño completo y corte"
}
```

**Expected Response:** 201 Created

**Assertions:**
- ✓ Status code = 201
- ✓ idAtencion existe
- ✓ Estado = "creado" (por defecto)
- ✓ Timestamps válidos

**Próximo Paso:** Guardar `idAtencion` en variable

---

#### 2.2.4 Test: Marcar Atención como Terminada
**Endpoint:** `PUT /api/atenciones/{{atencion_id}}/estado`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Request:**
```json
{
  "estado": "terminado"
}
```

**Expected Response:** 200 OK

**Assertions:**
- ✓ Status code = 200
- ✓ Estado actualizado a "terminado"
- ✓ Timestamp de finalización registrado

---

#### 2.2.5 Test: Crear Factura
**Endpoint:** `POST /api/facturas`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Request:**
```json
{
  "serie": "F001",
  "numero": "0100",
  "idAtencion": {{atencion_id}},
  "metodoPagoSugerido": "efectivo"
}
```

**Expected Response:** 201 Created

**Assertions:**
- ✓ Status code = 201
- ✓ idFactura existe
- ✓ Subtotal calculado correctamente
- ✓ IGV calculado
- ✓ Total final correcto

**Próximo Paso:** Guardar `idFactura` en variable

---

#### 2.2.6 Test: Registrar Pago
**Endpoint:** `POST /api/pagos`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Request:**
```json
{
  "idFactura": {{factura_id}},
  "monto": 105.50,
  "metodo": "efectivo",
  "referencia": "Pago contado"
}
```

**Expected Response:** 201 Created

**Assertions:**
- ✓ Status code = 201
- ✓ idPago existe
- ✓ Estado del pago = "pagado"
- ✓ Factura marca como pagada

---

### 2.3 FASE 3: Validaciones de Seguridad (JWT)

#### 2.3.1 Test: Acceso sin JWT - Debe Fallar
**Endpoint:** `GET /api/clientes` (SIN encabezado Authorization)

**Expected Response:** 401 Unauthorized o 403 Forbidden

**Assertions:**
- ✓ Status code ∈ {401, 403}
- ✓ Solicitud rechazada

---

#### 2.3.2 Test: JWT Inválido - Debe Fallar
**Endpoint:** `GET /api/clientes`  
**Encabezado:** `Authorization: Bearer invalid.token.here`

**Expected Response:** 401 Unauthorized

**Assertions:**
- ✓ Status code ∈ {401, 403}
- ✓ Token rechazado

---

#### 2.3.3 Test: JWT Válido - Debe Funcionar
**Endpoint:** `GET /api/clientes`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Expected Response:** 200 OK

**Assertions:**
- ✓ Status code = 200
- ✓ Lista de clientes retornada

---

## 3. EJECUCIÓN DE PRUEBAS CON POSTMAN RUNNER

### 3.1 Opción 1: GUI de Postman

1. Abrir Postman
2. Importar `Postman_Collection.json`
3. Configurar variables de entorno:
   - Click en "No Environment" → "Add" → Crear "TeranVet-Testing"
   - Agregar variables según sección 1.2
4. Click en "Collection" → "Run"
5. Seleccionar la colección importada
6. Click en "Run Postman Collection"
7. Guardar resultados como `POSTMAN_TEST_RESULTS.json`

### 3.2 Opción 2: CLI (Newman)

```bash
# Instalar Newman (una sola vez)
npm install -g newman

# Ejecutar colección
newman run Postman_Collection.json \
  --environment postman_environment.json \
  --reporters cli,json \
  --reporter-json-export test-results.json

# Generar reporte HTML
newman run Postman_Collection.json \
  --environment postman_environment.json \
  --reporters cli,htmlextra \
  --reporter-htmlextra-export test-report.html
```

---

## 4. VALIDACIONES DE BASE DE DATOS

Después de ejecutar pruebas, validar en MySQL:

```sql
-- Verificar cliente creado
SELECT * FROM Cliente WHERE email = 'juan.perez@example.com';

-- Verificar mascota creada
SELECT * FROM Mascota WHERE nombre = 'Rex';

-- Verificar atención registrada
SELECT * FROM Atencion WHERE idMascota = {{mascota_id}};

-- Verificar factura
SELECT * FROM Factura WHERE serie = 'F001' AND numero = '0100';

-- Verificar pago
SELECT * FROM Pago WHERE idFactura = {{factura_id}};

-- Verificar auditoría de cambios
SELECT * FROM AuditLog ORDER BY fechaCambio DESC LIMIT 10;
```

---

## 5. FLUJO CITA (Pruebas Adicionales - Fase 2.5)

### 5.1 Test: Crear Cita
**Endpoint:** `POST /api/citas`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

**Request:**
```json
{
  "idCliente": {{cliente_id}},
  "idMascota": {{mascota_id}},
  "idGroomer": 1,
  "idSucursal": 1,
  "fechaCita": "2025-11-15",
  "horaCita": "14:30",
  "duracionMinutos": 45,
  "observaciones": "Cita programada para corte"
}
```

---

### 5.2 Test: Confirmar Asistencia
**Endpoint:** `PUT /api/citas/{{cita_id}}/confirmar-asistencia`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

---

### 5.3 Test: Crear Atención desde Cita
**Endpoint:** `POST /api/atenciones/desde-cita`  
**Encabezado:** `Authorization: Bearer {{jwt_token}}`

---

## 6. MÉTRICAS DE ÉXITO

Para que las pruebas sean **100% exitosas**, se necesita:

| Métrica | Objetivo | Status |
|---------|----------|--------|
| Login funciona | JWT generado correctamente | ⏳ |
| Walk-In flow completo | 6/6 endpoints funcionan | ⏳ |
| Seguridad JWT | Sin token = 401/403 | ⏳ |
| BD sincronizada | Todos los registros en DB | ⏳ |
| Swagger accesible | `/swagger-ui.html` funciona | ⏳ |
| Tasa de éxito | 100% de pruebas pasan | ⏳ |

---

## 7. REPORTES A GENERAR

Al completar todas las pruebas:

1. **INTEGRATION_TEST_RESULTS.md**
   - Resumen de pruebas ejecutadas
   - Tasa de éxito/fallo
   - Tiempos de respuesta
   - Errores encontrados (si los hay)

2. **test-results.json** (Newman)
   - Salida raw de Postman Runner
   - Timestamps de cada petición

3. **test-report.html** (Newman)
   - Reporte visual con gráficos
   - Detalles de cada test

---

## 8. PRÓXIMOS PASOS DESPUÉS DE PRUEBAS

✓ **Una vez todas las pruebas PASEN:**

1. Swagger/OpenAPI Documentation (Prioridad 3)
   - Agregar anotaciones a controllers
   - Generar documentación en `/swagger-ui.html`

2. Dockerización (Prioridad 4)
   - Crear Dockerfile
   - Crear docker-compose.yml
   - Testear en contenedor

3. Reporte Final
   - Documentación completa del proyecto
   - Estadísticas finales
   - Instrucciones de deployment

---

## 9. TROUBLESHOOTING

### Problema: "Connection refused"
**Solución:** Verificar que Spring Boot esté corriendo en localhost:8080
```bash
curl -s http://localhost:8080/api/auth/login || echo "API no responde"
```

### Problema: "Invalid JWT token"
**Solución:** Verificar que el token esté dentro de su tiempo de expiración (24 horas)

### Problema: "Cliente/Mascota no encontrado"
**Solución:** Usar los IDs generados en las pruebas previas, no hardcodear

### Problema: Errores de CORS
**Solución:** SecurityConfig ya permite CORS; verificar headers Authorization

---

## 10. CHECKLIST FINAL

- [ ] Base de datos MySQL corriendo
- [ ] API Spring Boot iniciada en puerto 8080
- [ ] JWT implementado y compilando
- [ ] Postman/Newman instalado
- [ ] Colección importada en Postman
- [ ] Variables de entorno configuradas
- [ ] Test de login ejecutado exitosamente
- [ ] Walk-In flow completado (6 pasos)
- [ ] Seguridad JWT validada
- [ ] BD verificada manualmente
- [ ] Reportes generados
- [ ] Documentación actualizada

---

**Última Actualización:** 2025-11-12  
**Próxima Fase:** Swagger/OpenAPI Integration (Prioridad 3)
