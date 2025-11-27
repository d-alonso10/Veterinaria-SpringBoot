# 🔧 SOLUCIÓN RÁPIDA: Error "Column 'total_mascotas' not found"

**Error:** `Column 'total_mascotas' not found`  
**Causa:** El stored procedure NO fue actualizado en la base de datos MySQL  
**Solución:** Ejecutar el script de actualización

---

## ⚡ PASOS PARA CORREGIR (5 minutos)

### Opción 1: MySQL Workbench (RECOMENDADO) ⭐

1. **Abrir MySQL Workbench**
2. **Conectarse a la base de datos `veterinaria`**
3. **Abrir el archivo** `UPDATE_SP_MetricasDashboard.sql`
   - File → Open SQL Script
   - Seleccionar: `Veterinaria-SpringBoot/UPDATE_SP_MetricasDashboard.sql`
4. **Ejecutar todo el script** (Click en el ⚡ o Ctrl+Shift+Enter)
5. **Verificar resultado:** Deberías ver:
   ```
   Stored procedure sp_ObtenerMetricasDashboard actualizado correctamente
   ```
6. **Ver la prueba:** Deberías ver una fila con 5 columnas:
   ```
   total_clientes | total_mascotas | citas_hoy | ingresos_periodo | atenciones_en_curso
   ```

---

### Opción 2: Línea de Comandos MySQL

```bash
# Conectarse a MySQL
mysql -u root -p

# Seleccionar la base de datos
USE veterinaria;

# Copiar y pegar el contenido de UPDATE_SP_MetricasDashboard.sql
# O ejecutar el archivo directamente:
source C:/Users/user/Documents/Veterinaria-SpringBoot/UPDATE_SP_MetricasDashboard.sql

# Salir
exit;
```

---

### Opción 3: Copiar/Pegar Manual

Si prefieres copiar y pegar directamente en MySQL Workbench:

```sql
USE veterinaria;

-- Eliminar el procedimiento existente
DROP PROCEDURE IF EXISTS sp_ObtenerMetricasDashboard;

-- Cambiar el delimitador
DELIMITER $$

-- Crear el procedimiento actualizado
CREATE PROCEDURE `sp_ObtenerMetricasDashboard` (
    IN `p_fecha_inicio` DATE,
    IN `p_fecha_fin` DATE
)
BEGIN
    -- Retornar TODAS las métricas en UN SOLO result set
    SELECT
        (SELECT COUNT(*) FROM cliente) AS total_clientes,
        (SELECT COUNT(*) FROM mascota) AS total_mascotas,
        (SELECT COUNT(*)
         FROM cita
         WHERE DATE(fecha_programada) BETWEEN COALESCE(p_fecha_inicio, CURDATE())
                                          AND COALESCE(p_fecha_fin, CURDATE())
         AND estado IN ('reservada','confirmada')) AS citas_hoy,
        (SELECT COALESCE(SUM(total), 0)
         FROM factura
         WHERE estado IN ('emitida', 'pagada')
         AND DATE(fecha_emision) BETWEEN COALESCE(p_fecha_inicio, DATE(DATE_SUB(NOW(), INTERVAL 30 DAY)))
                                    AND COALESCE(p_fecha_fin, CURDATE())) AS ingresos_periodo,
        (SELECT COUNT(*)
         FROM atencion
         WHERE estado IN ('en_espera','en_servicio')) AS atenciones_en_curso;
END$$

-- Restaurar el delimitador
DELIMITER ;

-- Verificar
CALL sp_ObtenerMetricasDashboard('2025-01-01', '2025-12-31');
```

---

## ✅ VERIFICACIÓN

### 1. Verificar que el SP fue actualizado:

```sql
-- Ver el código del procedimiento
SHOW CREATE PROCEDURE sp_ObtenerMetricasDashboard;
```

Deberías ver el código actualizado con las 5 subconsultas.

### 2. Probar manualmente:

```sql
CALL sp_ObtenerMetricasDashboard('2025-01-01', '2025-12-31');
```

**Resultado esperado:** Una fila con 5 columnas:

```
+----------------+----------------+-----------+------------------+---------------------+
| total_clientes | total_mascotas | citas_hoy | ingresos_periodo | atenciones_en_curso |
+----------------+----------------+-----------+------------------+---------------------+
|              9 |             15 |         5 |          2500.00 |                   2 |
+----------------+----------------+-----------+------------------+---------------------+
```

✅ Si ves **5 columnas**, el SP está correcto.  
❌ Si ves **solo 1 columna** o un error, el SP no se actualizó.

---

## 🔄 DESPUÉS DE ACTUALIZAR EL SP

### 3. Probar el endpoint en Postman:

```
GET http://localhost:8080/api/dashboard/metricas?fechaInicio=2025-01-01&fechaFin=2025-12-31
Authorization: Bearer <tu_token>
```

**Respuesta esperada:**

```json
{
  "exito": true,
  "mensaje": "Métricas obtenidas correctamente",
  "datos": {
    "totalClientes": 9,
    "totalMascotas": 15,
    "citasHoy": 5,
    "ingresosPeriodo": 2500.00,
    "atencionesEnCurso": 2
  }
}
```

✅ **Si ves las 5 propiedades, todo está funcionando correctamente.**

---

## ❓ TROUBLESHOOTING

### Si sigue sin funcionar:

#### 1. ¿El SP se actualizó?
```sql
SHOW CREATE PROCEDURE sp_ObtenerMetricasDashboard;
```
Busca en el código: `AS total_mascotas`, `AS citas_hoy`, etc.

#### 2. ¿Estás conectado a la base de datos correcta?
```sql
SELECT DATABASE();
```
Debe decir: `veterinaria`

#### 3. ¿Reiniciaste Spring Boot después de actualizar el SP?
Aunque no es necesario, a veces ayuda:
- Detén el servidor Spring Boot
- Vuelve a iniciarlo

#### 4. ¿El SP está retornando los datos correctos?
```sql
CALL sp_ObtenerMetricasDashboard('2025-01-01', CURDATE());
```
Cuenta las columnas manualmente.

---

## 📝 NOTA IMPORTANTE

**NO es suficiente editar el archivo `.sql`.**  
Los cambios en archivos `.sql` NO se reflejan automáticamente en la base de datos.  
**DEBES ejecutar el script en MySQL** para que los cambios surtan efecto.

### Flujo correcto:
1. ✏️ Editar `vet_teran_mysql.sql` (YA HECHO)
2. ▶️ **Ejecutar el script en MySQL** (PENDIENTE ← ESTÁS AQUÍ)
3. 🧪 Probar endpoint en Postman
4. ✅ Confirmar que funciona

---

## 🎯 RESUMEN

1. **Ejecuta** `UPDATE_SP_MetricasDashboard.sql` en MySQL Workbench
2. **Verifica** que el SP retorna 5 columnas con `CALL sp_ObtenerMetricasDashboard('2025-01-01', CURDATE());`
3. **Prueba** el endpoint en Postman
4. **Confirma** que obtienes las 5 propiedades en el JSON

---

**Tiempo estimado:** 2-5 minutos  
**Dificultad:** Baja  
**Prioridad:** Alta (Bloqueante para el frontend)
