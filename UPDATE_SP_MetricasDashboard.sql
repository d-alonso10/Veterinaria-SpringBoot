-- =====================================================================
-- SCRIPT PARA ACTUALIZAR sp_ObtenerMetricasDashboard
-- Ejecuta este script en MySQL Workbench o línea de comandos
-- =====================================================================

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
        -- Total de clientes
        (SELECT COUNT(*) FROM cliente) AS total_clientes,

        -- Total de mascotas
        (SELECT COUNT(*) FROM mascota) AS total_mascotas,

        -- Citas del día (usa las fechas de parámetro o CURDATE)
        (SELECT COUNT(*)
         FROM cita
         WHERE DATE(fecha_programada) BETWEEN COALESCE(p_fecha_inicio, CURDATE())
                                          AND COALESCE(p_fecha_fin, CURDATE())
         AND estado IN ('reservada','confirmada')) AS citas_hoy,

        -- Ingresos del período (usa las fechas de parámetro)
        (SELECT COALESCE(SUM(total), 0)
         FROM factura
         WHERE estado IN ('emitida', 'pagada')
         AND DATE(fecha_emision) BETWEEN COALESCE(p_fecha_inicio, DATE(DATE_SUB(NOW(), INTERVAL 30 DAY)))
                                    AND COALESCE(p_fecha_fin, CURDATE())) AS ingresos_periodo,

        -- Atenciones en curso
        (SELECT COUNT(*)
         FROM atencion
         WHERE estado IN ('en_espera','en_servicio')) AS atenciones_en_curso;
END$$

-- Restaurar el delimitador
DELIMITER ;

-- Verificar que el procedimiento fue creado correctamente
SELECT 'Stored procedure sp_ObtenerMetricasDashboard actualizado correctamente' AS resultado;

-- Prueba del procedimiento (opcional)
CALL sp_ObtenerMetricasDashboard('2025-01-01', '2025-12-31');
