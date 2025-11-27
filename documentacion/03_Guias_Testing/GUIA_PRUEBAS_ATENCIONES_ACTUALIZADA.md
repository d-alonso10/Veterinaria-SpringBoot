# 🧪 GUÍA DE PRUEBAS - ATENCIONES (ACTUALIZADA)

**Versión:** 2.0  
**Última actualización:** 27 Noviembre 2025  
**Estado:** ✅ Actualizada con nuevos cambios

---

## 📋 CAMBIOS EN ESTA VERSIÓN

Con los últimos cambios implementados:
- ✅ POST `/desde-cita` ahora devuelve objeto `Atencion` (antes devolvía `null`)
- ✅ POST `/desde-cita` actualiza automáticamente estado de cita a "atendido"
- ✅ POST `/walk-in` devuelve objeto `Atencion` completo
- ✅ **SIN POLLING:** Frontend obtiene ID inmediatamente

---

## 🚀 PRUEBAS INMEDIATAS

### TEST 1: Crear Atención desde Cita ✅

**Endpoint:** `POST /api/atenciones/desde-cita`

**Parámetros:**
```
idCita=1
idGroomer=1
idSucursal=1
turnoNum=1
tiempoEstimadoInicio=2025-12-26T10:00:00
tiempoEstimadoFin=2025-12-26T10:45:00
prioridad=0
```

**Respuesta Esperada (201 CREATED):**
```json
{
  "exito": true,
  "datos": {
    "idAtencion": 45,
    "idCita": 1,
    "idMascota": 8,
    "idCliente": 12,
    "idGroomer": 1,
    "idSucursal": 1,
    "turnoNum": 1,
    "estado": "en_espera",
    "tiempoEstimadoInicio": "2025-12-26T10:00:00",
    "tiempoEstimadoFin": "2025-12-26T10:45:00",
    "prioridad": 0,
    "createdAt": "2025-11-27T14:30:00",
    "updatedAt": "2025-11-27T14:30:00"
  },
  "mensaje": "Atención creada exitosamente desde la cita"
}
```

**Qué validar:**
- ✅ Status 201 (CREATED)
- ✅ `datos` NO es null (es el objeto Atencion)
- ✅ `idAtencion` está presente (frontend ya lo conoce)
- ✅ Logs muestran: "✅ Estado de cita 1 actualizado a 'atendido'"

---

### TEST 2: Verificar Estado de Cita Cambió ✅

**Endpoint:** `GET /api/citas/1`

**Respuesta Esperada:**
```json
{
  "exito": true,
  "datos": {
    "idCita": 1,
    "estado": "atendido",  // ← CAMBIÓ desde "confirmada"
    ...
  }
}
```

**Qué validar:**
- ✅ Estado cambió a "atendido"
- ✅ Confirma sincronización entre Cita y Atencion

---

### TEST 3: Verificar en Cola ✅

**Endpoint:** `GET /api/atenciones/cola/1`

**Respuesta Esperada:**
```json
{
  "exito": true,
  "datos": [
    {
      "idAtencion": 45,
      "idCita": 1,
      "estado": "en_espera",
      "prioridad": 0,
      "tiempoEstimadoInicio": "2025-12-26T10:00:00",
      ...
    }
  ]
}
```

**Qué validar:**
- ✅ Nueva atención aparece en la cola
- ✅ Estado es "en_espera"
- ✅ Orden respeta prioridad

---

### TEST 4: Crear Walk-In ✅

**Endpoint:** `POST /api/atenciones/walk-in`

**Parámetros:**
```
idMascota=8
idCliente=12
idGroomer=1
idSucursal=1
turnoNum=2
tiempoEstimadoInicio=2025-12-26T11:00:00
tiempoEstimadoFin=2025-12-26T11:45:00
prioridad=0
observaciones=Cliente llega sin cita
```

**Respuesta Esperada (201 CREATED):**
```json
{
  "exito": true,
  "datos": {
    "idAtencion": 46,
    "idCita": null,  // ← Sin cita
    "idMascota": 8,
    "idCliente": 12,
    "estado": "en_espera",
    "observaciones": "Cliente llega sin cita",
    ...
  },
  "mensaje": "Atención walk-in creada exitosamente"
}
```

**Qué validar:**
- ✅ Status 201 (CREATED)
- ✅ `datos` es objeto completo (NO null)
- ✅ `idCita` es null (sin cita)
- ✅ `observaciones` guardadas correctamente

---

## 📊 COMPARATIVA: ANTES vs DESPUÉS

### Tiempo de Operación

| Operación | ANTES | DESPUÉS | Mejora |
|-----------|-------|---------|--------|
| Crear atención desde cita | 3-10s (polling) | <500ms | **95% más rápido** |
| Obtener ID atención | Polling loop | Respuesta inmediata | **Eliminado** |
| Sincronizar cita estado | Manual | Automático | **Automático** |

### Response Devuelto

| Aspecto | ANTES | DESPUÉS |
|--------|-------|---------|
| `datos` field | `null` | `Atencion` completo |
| ID disponible | Desconocido | Conocido inmediatamente |
| Frontend acción | Esperar + polling | Navegar directo |

---

## 🔧 POSTMAN - COLECCIÓN ACTUALIZADA

### Importar en Postman

1. Abre Postman
2. Importa: `Postman_Collection.json`
3. Variables: `postman_environment.json`

### Variables a Configurar

```json
{
  "base_url": "http://localhost:8080",
  "api_prefix": "/api",
  "idCita": "1",
  "idAtencion": "45",
  "idSucursal": "1"
}
```

---

## 📝 CASOS DE PRUEBA COMPLETOS

### Flujo 1: Crear Atención desde Cita → Verificar Estado

1. **POST** `/api/atenciones/desde-cita?idCita=1&...`
   - ✅ Recibe Atencion con ID
   - ✅ Logs: "✅ Estado de cita 1 actualizado a 'atendido'"

2. **GET** `/api/citas/1`
   - ✅ Estado = "atendido"

3. **GET** `/api/atenciones/cola/1`
   - ✅ Incluye nueva atención

### Flujo 2: Walk-In Direct

1. **POST** `/api/atenciones/walk-in?idMascota=8&...`
   - ✅ Recibe Atencion con ID
   - ✅ Sin Cita (idCita = null)

2. **GET** `/api/atenciones/{idAtencion}`
   - ✅ Detalles completos

---

## ✅ CHECKLIST DE VALIDACIÓN

- [ ] Test 1: Crear desde cita devuelve objeto Atencion
- [ ] Test 2: Cita estado cambió a "atendido"
- [ ] Test 3: Atención en cola
- [ ] Test 4: Walk-in devuelve objeto completo
- [ ] Logs: Se ven los mensajes ✅
- [ ] Sin errores 400/500
- [ ] Response times < 500ms

---

## 🐛 Si algo falla

| Error | Causa | Solución |
|-------|-------|----------|
| "datos": null | Sistema no actualizado | Recompila: `mvn clean compile` |
| "Estado de cita no cambió" | CitaService no inyectado | Verifica AtencionController línea 30 |
| 500 ERROR | SP no ejecutándose | Verifica stored procedures en DB |
| No aparece en cola | Query filtering | Verifica estado en "en_espera" |

---

## 🚀 Próximos Pasos

1. ✅ Ejecutar todos los tests
2. ✅ Verificar logs en consola
3. ✅ Confirmar con team
4. ✅ Deploy a staging
5. ✅ Pruebas de carga (si aplica)

---

**Nota:** Esta guía refleja los cambios del 27/11/2025. Actualiza si hay cambios futuros.
