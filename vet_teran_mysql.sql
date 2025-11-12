DROP DATABASE IF EXISTS vet_teran;
CREATE DATABASE vet_teran;
USE vet_teran;
SET FOREIGN_KEY_CHECKS=0;

START TRANSACTION;




DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ActualizarEstadoAtencion` (IN `p_id_atencion` INT, IN `p_nuevo_estado` ENUM('en_espera','en_servicio','pausado','terminado'))   BEGIN
    UPDATE atencion
        tiempo_real_inicio = CASE 
            WHEN p_nuevo_estado = 'en_servicio' AND tiempo_real_inicio IS NULL THEN NOW() 
            ELSE tiempo_real_inicio 
        END,
        tiempo_real_fin = CASE 
            WHEN p_nuevo_estado = 'terminado' AND tiempo_real_fin IS NULL THEN NOW() 
            ELSE tiempo_real_fin 
        END,
        updated_at = NOW()
    WHERE id_atencion = p_id_atencion;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ActualizarEstadoPromocion` (IN `p_id_promocion` INT, IN `p_estado` ENUM('activa','inactiva'))   BEGIN
    UPDATE promocion
    WHERE id_promocion = p_id_promocion;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ActualizarGroomer` (IN `p_id_groomer` INT, IN `p_nombre` VARCHAR(100), IN `p_especialidades` JSON, IN `p_disponibilidad` JSON)   BEGIN
    UPDATE groomer
        especialidades = p_especialidades,
        disponibilidad = p_disponibilidad,
        updated_at = NOW()
    WHERE id_groomer = p_id_groomer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ActualizarServicio` (IN `p_id_servicio` INT, IN `p_codigo` VARCHAR(20), IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, IN `p_duracion_estimada_min` INT, IN `p_precio_base` DECIMAL(10,2), IN `p_categoria` ENUM('baño','corte','dental','paquete','otro'))   BEGIN
    UPDATE servicio
        nombre = p_nombre,
        descripcion = p_descripcion,
        duracion_estimada_min = p_duracion_estimada_min,
        precio_base = p_precio_base,
        categoria = p_categoria,
        updated_at = NOW()
    WHERE id_servicio = p_id_servicio;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ActualizarSucursal` (IN `p_id_sucursal` INT, IN `p_nombre` VARCHAR(100), IN `p_direccion` VARCHAR(200), IN `p_telefono` VARCHAR(20))   BEGIN
    UPDATE sucursal
        direccion = p_direccion,
        telefono = p_telefono,
        updated_at = NOW()
    WHERE id_sucursal = p_id_sucursal;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_AgregarServicioAtencion` (IN `p_id_atencion` INT, IN `p_id_servicio` INT, IN `p_cantidad` INT, IN `p_precio_unitario` DECIMAL(10,2), IN `p_descuento_id` INT, IN `p_observaciones` TEXT)   BEGIN
    DECLARE v_subtotal DECIMAL(10,2);
    
    
    INSERT INTO detalle_servicio (id_atencion, id_servicio, cantidad, precio_unitario, 
                                 descuento_id, subtotal, observaciones)
    VALUES (p_id_atencion, p_id_servicio, p_cantidad, p_precio_unitario, 
            p_descuento_id, v_subtotal, p_observaciones);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_AgregarServicioPaquete` (IN `p_id_paquete` INT, IN `p_id_servicio` INT, IN `p_cantidad` INT)   BEGIN
    INSERT INTO paquete_servicio_item (id_paquete, id_servicio, cantidad)
    VALUES (p_id_paquete, p_id_servicio, p_cantidad)
    ON DUPLICATE KEY UPDATE cantidad = p_cantidad;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_AjustarTiempoEstimado` (IN `p_id_servicio` INT, IN `p_id_groomer` INT, IN `p_tiempo_estimado_min` INT)   BEGIN
    IF EXISTS (SELECT 1 FROM configuracion_estimacion 
               WHERE id_servicio = p_id_servicio AND id_groomer = p_id_groomer) THEN
        UPDATE configuracion_estimacion
        WHERE id_servicio = p_id_servicio AND id_groomer = p_id_groomer;
    ELSE
        INSERT INTO configuracion_estimacion (id_servicio, id_groomer, tiempo_estimado_min)
        VALUES (p_id_servicio, p_id_groomer, p_tiempo_estimado_min);
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_AnularFactura` (IN `p_id_factura` INT)   BEGIN
    UPDATE factura
    WHERE id_factura = p_id_factura;
    
    -- También anular pagos asociados
    UPDATE pago
    WHERE id_factura = p_id_factura AND estado = 'confirmado';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_BuscarClientes` (IN `p_termino` VARCHAR(100))   BEGIN
    SELECT id_cliente, nombre, apellido, dni_ruc, email, telefono, direccion
    FROM cliente
    WHERE nombre LIKE CONCAT('%', p_termino, '%') 
       OR apellido LIKE CONCAT('%', p_termino, '%')
       OR dni_ruc LIKE CONCAT('%', p_termino, '%')
       OR email LIKE CONCAT('%', p_termino, '%')
    ORDER BY nombre, apellido;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_BuscarMascotas` (IN `p_termino` VARCHAR(100))   BEGIN
    SELECT m.id_mascota, m.nombre, m.especie, m.raza, m.microchip,
           c.nombre AS cliente_nombre, c.apellido AS cliente_apellido
    FROM mascota m
    INNER JOIN cliente c ON m.id_cliente = c.id_cliente
    WHERE m.nombre LIKE CONCAT('%', p_termino, '%')
       OR m.microchip LIKE CONCAT('%', p_termino, '%')
       OR c.nombre LIKE CONCAT('%', p_termino, '%')
       OR c.apellido LIKE CONCAT('%', p_termino, '%')
    ORDER BY m.nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CancelarCita` (IN `p_id_cita` INT)   BEGIN
    UPDATE cita
    WHERE id_cita = p_id_cita;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ClientesFrecuentes` ()   BEGIN
    SELECT c.id_cliente, c.nombre, c.apellido, c.email, c.telefono,
           COUNT(DISTINCT a.id_atencion) AS total_atenciones,
           COUNT(DISTINCT m.id_mascota) AS total_mascotas,
           SUM(f.total) AS total_gastado
    FROM cliente c
    LEFT JOIN mascota m ON c.id_cliente = m.id_cliente
    LEFT JOIN atencion a ON m.id_mascota = a.id_mascota
    LEFT JOIN factura f ON a.id_atencion = f.id_atencion AND f.estado = 'emitida'
    GROUP BY c.id_cliente, c.nombre, c.apellido, c.email, c.telefono
    HAVING total_atenciones > 0
    ORDER BY total_atenciones DESC, total_gastado DESC
    LIMIT 10;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ConfirmarAsistenciaCita` (IN `p_id_cita` INT)   BEGIN
    UPDATE cita
    WHERE id_cita = p_id_cita;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CrearAtencionDesdeCita` (IN `p_id_cita` INT, IN `p_id_groomer` INT, IN `p_id_sucursal` INT, IN `p_turno_num` INT, IN `p_tiempo_estimado_inicio` DATETIME, IN `p_tiempo_estimado_fin` DATETIME, IN `p_prioridad` TINYINT)   BEGIN
    DECLARE v_id_mascota INT;
    DECLARE v_id_cliente INT;
    
    -- Obtener datos de la cita
    SELECT id_mascota, id_cliente INTO v_id_mascota, v_id_cliente
    FROM cita WHERE id_cita = p_id_cita;
    
    -- Crear atención
    INSERT INTO atencion (id_cita, id_mascota, id_cliente, id_groomer, id_sucursal, 
                         estado, turno_num, tiempo_estimado_inicio, tiempo_estimado_fin, prioridad)
    VALUES (p_id_cita, v_id_mascota, v_id_cliente, p_id_groomer, p_id_sucursal,
            'en_espera', p_turno_num, p_tiempo_estimado_inicio, p_tiempo_estimado_fin, p_prioridad);
            
    -- Actualizar estado de la cita
    UPDATE cita SET estado = 'confirmada', updated_at = NOW() WHERE id_cita = p_id_cita;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CrearAtencionWalkIn` (IN `p_id_mascota` INT, IN `p_id_cliente` INT, IN `p_id_groomer` INT, IN `p_id_sucursal` INT, IN `p_turno_num` INT, IN `p_tiempo_estimado_inicio` DATETIME, IN `p_tiempo_estimado_fin` DATETIME, IN `p_prioridad` TINYINT, IN `p_observaciones` TEXT)   BEGIN
		INSERT INTO atencion (id_mascota, id_cliente, id_groomer, id_sucursal, 
							 estado, turno_num, tiempo_estimado_inicio, tiempo_estimado_fin, 
							 prioridad, observaciones)
		VALUES (p_id_mascota, p_id_cliente, p_id_groomer, p_id_sucursal,
				'en_espera', p_turno_num, p_tiempo_estimado_inicio, p_tiempo_estimado_fin, 
				p_prioridad, p_observaciones);
	END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CrearCita` (IN `p_id_mascota` INT, IN `p_id_cliente` INT, IN `p_id_sucursal` INT, IN `p_id_servicio` INT, IN `p_fecha_programada` DATETIME, IN `p_modalidad` ENUM('presencial','virtual'), IN `p_notas` TEXT)   BEGIN
    INSERT INTO cita (id_mascota, id_cliente, id_sucursal, id_servicio, fecha_programada, modalidad, notas)
    VALUES (p_id_mascota, p_id_cliente, p_id_sucursal, p_id_servicio, p_fecha_programada, p_modalidad, p_notas);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CrearFactura` (IN `p_serie` VARCHAR(10), IN `p_numero` VARCHAR(20), IN `p_id_atencion` INT, IN `p_metodo_pago_sugerido` ENUM('efectivo','tarjeta','transfer','otro'))   BEGIN
    DECLARE v_id_cliente INT;
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_descuento_total DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_impuesto DECIMAL(10,2);
    
    -- Obtener datos de la atención
    SELECT a.id_cliente INTO v_id_cliente
    FROM atencion a WHERE a.id_atencion = p_id_atencion;
    
    -- Calcular totales desde detalle_servicio
    SELECT COALESCE(SUM(ds.subtotal), 0), 
           COALESCE(SUM(ds.subtotal * 0.18), 0) -- 18% de impuesto
    INTO v_subtotal, v_impuesto
    FROM detalle_servicio ds
    WHERE ds.id_atencion = p_id_atencion;
    
    
    INSERT INTO factura (serie, numero, id_cliente, id_atencion, subtotal, 
                        impuesto, descuento_total, total, metodo_pago_sugerido)
    VALUES (p_serie, p_numero, v_id_cliente, p_id_atencion, v_subtotal, 
            v_impuesto, 0, v_total, p_metodo_pago_sugerido);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_CrearPaqueteServicio` (IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, IN `p_precio_total` DECIMAL(10,2))   BEGIN
    INSERT INTO paquete_servicio (nombre, descripcion, precio_total)
    VALUES (p_nombre, p_descripcion, p_precio_total);
    
    SELECT LAST_INSERT_ID() AS id_paquete;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_DetalleServiciosAtencion` (IN `p_id_atencion` INT)   BEGIN
    SELECT ds.id_detalle, s.nombre AS servicio, s.categoria,
           ds.cantidad, ds.precio_unitario, ds.subtotal,
           ds.observaciones
    FROM detalle_servicio ds
    INNER JOIN servicio s ON ds.id_servicio = s.id_servicio
    WHERE ds.id_atencion = p_id_atencion
    ORDER BY ds.id_detalle;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_GenerarBackupEsencial` (IN `p_fecha_backup` DATE)   BEGIN
    -- Crear tabla temporal para backup de clientes
    CREATE TABLE IF NOT EXISTS backup_clientes_YYYYMMDD AS
    SELECT * FROM cliente WHERE DATE(created_at) <= p_fecha_backup;
    
    -- Crear tabla temporal para backup de mascotas
    CREATE TABLE IF NOT EXISTS backup_mascotas_YYYYMMDD AS
    SELECT * FROM mascota WHERE DATE(created_at) <= p_fecha_backup;
    
    -- Crear tabla temporal para backup de facturas
    CREATE TABLE IF NOT EXISTS backup_facturas_YYYYMMDD AS
    SELECT * FROM factura WHERE DATE(fecha_emision) <= p_fecha_backup;
    
    SELECT 'Backup completado exitosamente' AS mensaje;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_HistorialMascota` (IN `p_id_mascota` INT)   BEGIN
    SELECT a.id_atencion, a.tiempo_real_inicio, a.tiempo_real_fin,
           g.nombre AS groomer, s.nombre AS sucursal,
           GROUP_CONCAT(DISTINCT serv.nombre SEPARATOR ', ') AS servicios,
           f.total AS monto_facturado
    FROM atencion a
    LEFT JOIN groomer g ON a.id_groomer = g.id_groomer
    LEFT JOIN sucursal s ON a.id_sucursal = s.id_sucursal
    LEFT JOIN detalle_servicio ds ON a.id_atencion = ds.id_atencion
    LEFT JOIN servicio serv ON ds.id_servicio = serv.id_servicio
    LEFT JOIN factura f ON a.id_atencion = f.id_atencion
    WHERE a.id_mascota = p_id_mascota AND a.estado = 'terminado'
    GROUP BY a.id_atencion, a.tiempo_real_inicio, a.tiempo_real_fin, g.nombre, s.nombre, f.total
    ORDER BY a.tiempo_real_inicio DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_InsertarCliente` (IN `p_nombre` VARCHAR(100), IN `p_apellido` VARCHAR(100), IN `p_dni_ruc` VARCHAR(20), IN `p_email` VARCHAR(120), IN `p_telefono` VARCHAR(20), IN `p_direccion` VARCHAR(200), IN `p_preferencias` JSON)   BEGIN
    INSERT INTO cliente (nombre, apellido, dni_ruc, email, telefono, direccion, preferencias)
    VALUES (p_nombre, p_apellido, p_dni_ruc, p_email, p_telefono, p_direccion, p_preferencias);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_InsertarGroomer` (IN `p_nombre` VARCHAR(100), IN `p_especialidades` JSON, IN `p_disponibilidad` JSON)   BEGIN
    INSERT INTO groomer (nombre, especialidades, disponibilidad)
    VALUES (p_nombre, p_especialidades, p_disponibilidad);
    
    SELECT LAST_INSERT_ID() AS id_groomer;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_InsertarMascota` (IN `p_id_cliente` INT, IN `p_nombre` VARCHAR(100), IN `p_especie` ENUM('perro','gato','otro'), IN `p_raza` VARCHAR(100), IN `p_sexo` ENUM('macho','hembra','otro'), IN `p_fecha_nacimiento` DATE, IN `p_microchip` VARCHAR(50), IN `p_observaciones` TEXT)   BEGIN
    INSERT INTO mascota (id_cliente, nombre, especie, raza, sexo, fecha_nacimiento, microchip, observaciones)
    VALUES (p_id_cliente, p_nombre, p_especie, p_raza, p_sexo, p_fecha_nacimiento, p_microchip, p_observaciones);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_InsertarPromocion` (IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, IN `p_tipo` ENUM('porcentaje','monto'), IN `p_valor` DECIMAL(10,2), IN `p_fecha_inicio` DATE, IN `p_fecha_fin` DATE)   BEGIN
    INSERT INTO promocion (nombre, descripcion, tipo, valor, fecha_inicio, fecha_fin)
    VALUES (p_nombre, p_descripcion, p_tipo, p_valor, p_fecha_inicio, p_fecha_fin);
    
    SELECT LAST_INSERT_ID() AS id_promocion;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_InsertarServicio` (IN `p_codigo` VARCHAR(20), IN `p_nombre` VARCHAR(100), IN `p_descripcion` TEXT, IN `p_duracion_estimada_min` INT, IN `p_precio_base` DECIMAL(10,2), IN `p_categoria` ENUM('baño','corte','dental','paquete','otro'))   BEGIN
    INSERT INTO servicio (codigo, nombre, descripcion, duracion_estimada_min, precio_base, categoria)
    VALUES (p_codigo, p_nombre, p_descripcion, p_duracion_estimada_min, p_precio_base, p_categoria);
    
    SELECT LAST_INSERT_ID() AS id_servicio;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_InsertarSucursal` (IN `p_nombre` VARCHAR(100), IN `p_direccion` VARCHAR(200), IN `p_telefono` VARCHAR(20))   BEGIN
    INSERT INTO sucursal (nombre, direccion, telefono)
    VALUES (p_nombre, p_direccion, p_telefono);
    
    SELECT LAST_INSERT_ID() AS id_sucursal;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_LimpiarDatosTemporales` (IN `p_dias_antiguedad` INT)   BEGIN
    -- Eliminar notificaciones antiguas
    DELETE FROM notificacion 
    WHERE DATEDIFF(NOW(), enviado_at) > p_dias_antiguedad;
    
    -- Eliminar logs de auditoría antiguos
    DELETE FROM audit_log 
    WHERE DATEDIFF(NOW(), timestamp) > p_dias_antiguedad;
    
    -- Anular facturas pendientes muy antiguas
    UPDATE factura 
    WHERE estado = 'emitida'
    AND DATEDIFF(NOW(), fecha_emision) > p_dias_antiguedad
    AND id_factura NOT IN (SELECT DISTINCT id_factura FROM pago WHERE estado = 'confirmado');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerColaActual` (IN `p_id_sucursal` INT)   BEGIN
    SELECT a.id_atencion, m.nombre AS mascota, c.nombre AS cliente, 
           g.nombre AS groomer, a.estado, a.turno_num, 
           a.tiempo_estimado_inicio, a.tiempo_estimado_fin
    FROM atencion a
    INNER JOIN mascota m ON a.id_mascota = m.id_mascota
    INNER JOIN cliente c ON a.id_cliente = c.id_cliente
    LEFT JOIN groomer g ON a.id_groomer = g.id_groomer
    WHERE a.estado IN ('en_espera','en_servicio','pausado')
          AND (p_id_sucursal IS NULL OR a.id_sucursal = p_id_sucursal)
    ORDER BY a.prioridad DESC, a.tiempo_estimado_inicio ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerDisponibilidadGroomers` (IN `p_fecha` DATE)   BEGIN
    SELECT g.id_groomer, g.nombre, g.disponibilidad,
           COUNT(a.id_atencion) AS atenciones_programadas
    FROM groomer g
    LEFT JOIN atencion a ON g.id_groomer = a.id_groomer 
        AND DATE(a.tiempo_estimado_inicio) = p_fecha
        AND a.estado IN ('en_espera','en_servicio')
    GROUP BY g.id_groomer, g.nombre, g.disponibilidad
    ORDER BY g.nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerEstadisticasMensuales` (IN `p_anio` INT, IN `p_mes` INT)   BEGIN
    SELECT 
        -- Total facturado
        (SELECT COALESCE(SUM(total), 0) 
         FROM factura 
         WHERE estado = 'emitida' 
         AND YEAR(fecha_emision) = p_anio 
         AND MONTH(fecha_emision) = p_mes) AS total_facturado,
        
        -- Total clientes nuevos
        (SELECT COUNT(*) 
         FROM cliente 
         WHERE YEAR(created_at) = p_anio 
         AND MONTH(created_at) = p_mes) AS clientes_nuevos,
        
        -- Total atenciones
        (SELECT COUNT(*) 
         FROM atencion 
         WHERE estado = 'terminado'
         AND YEAR(created_at) = p_anio 
         AND MONTH(created_at) = p_mes) AS atenciones_realizadas,
        
        -- Servicio más popular
        (SELECT s.nombre 
         FROM detalle_servicio ds
         INNER JOIN servicio s ON ds.id_servicio = s.id_servicio
         INNER JOIN atencion a ON ds.id_atencion = a.id_atencion
         INNER JOIN factura f ON a.id_atencion = f.id_atencion
         WHERE f.estado = 'emitida'
         AND YEAR(f.fecha_emision) = p_anio 
         AND MONTH(f.fecha_emision) = p_mes
         GROUP BY s.id_servicio, s.nombre
         ORDER BY SUM(ds.cantidad) DESC
         LIMIT 1) AS servicio_popular;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerEstimacionesTiempo` ()   BEGIN
    SELECT 
        ce.id_servicio,
        ce.id_groomer,
        s.nombre AS servicio, 
        g.nombre AS groomer, 
        ce.tiempo_estimado_min,
        s.duracion_estimada_min AS duracion_base
    FROM configuracion_estimacion ce
    INNER JOIN servicio s ON ce.id_servicio = s.id_servicio
    INNER JOIN groomer g ON ce.id_groomer = g.id_groomer
    ORDER BY s.nombre, g.nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerFacturasPorCliente` (IN `p_id_cliente` INT)   BEGIN
    SELECT f.id_factura, f.serie, f.numero, f.fecha_emision, f.subtotal, 
           f.impuesto, f.total, f.estado, f.metodo_pago_sugerido
    FROM factura f
    WHERE f.id_cliente = p_id_cliente
    ORDER BY f.fecha_emision DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerGroomers` ()   BEGIN
    SELECT id_groomer, nombre, especialidades, disponibilidad, created_at
    FROM groomer
    ORDER BY nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerLogsAuditoria` (IN `p_limite` INT, IN `p_entidad` VARCHAR(50), IN `p_accion` ENUM('INSERT','UPDATE','DELETE'))   BEGIN
    SELECT al.entidad, al.entidad_id, al.accion, 
           u.nombre AS usuario, al.timestamp, al.antes, al.despues
    FROM audit_log al
    LEFT JOIN usuario_sistema u ON al.id_usuario = u.id_usuario
    WHERE (p_entidad IS NULL OR al.entidad = p_entidad)
          AND (p_accion IS NULL OR al.accion = p_accion)
    ORDER BY al.timestamp DESC
    LIMIT p_limite;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerMascotasPorCliente` (IN `p_id_cliente` INT)   BEGIN
    SELECT m.id_mascota, m.nombre, m.especie, m.raza, m.sexo, m.fecha_nacimiento, m.microchip
    FROM mascota m
    WHERE m.id_cliente = p_id_cliente
    ORDER BY m.nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerMetricasDashboard` (IN `p_fecha_inicio` DATE, IN `p_fecha_fin` DATE)   BEGIN
    -- Total de clientes
    SELECT COUNT(*) AS total_clientes FROM cliente;
    
    -- Total de mascotas
    SELECT COUNT(*) AS total_mascotas FROM mascota;
    
    -- Citas del día
    SELECT COUNT(*) AS citas_hoy 
    FROM cita 
    WHERE DATE(fecha_programada) = CURDATE() 
    AND estado IN ('reservada','confirmada');
    
    -- Ingresos del mes
    SELECT COALESCE(SUM(total), 0) AS ingresos_mes
    FROM factura
    WHERE estado = 'emitida'
    AND MONTH(fecha_emision) = MONTH(CURDATE())
    AND YEAR(fecha_emision) = YEAR(CURDATE());
    
    -- Atenciones en curso
    SELECT COUNT(*) AS atenciones_curso
    FROM atencion
    WHERE estado IN ('en_espera','en_servicio');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerNotificacionesCliente` (IN `p_destinatario_id` INT, IN `p_limite` INT)   BEGIN
    SELECT n.tipo, n.contenido, n.enviado_at, n.estado,
           n.referencia_tipo, n.referencia_id
    FROM notificacion n
    WHERE n.destinatario_id = p_destinatario_id AND n.canal = 'cliente'
    ORDER BY n.enviado_at DESC
    LIMIT p_limite;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerPagosPorFactura` (IN `p_id_factura` INT)   BEGIN
    SELECT p.id_pago, p.fecha_pago, p.monto, p.metodo, p.referencia, p.estado
    FROM pago p
    WHERE p.id_factura = p_id_factura
    ORDER BY p.fecha_pago DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerPromociones` ()   BEGIN
    SELECT id_promocion, nombre, descripcion, tipo, valor, 
           fecha_inicio, fecha_fin, estado
    FROM promocion
    ORDER BY fecha_inicio DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerPromocionesActivas` ()   BEGIN
    SELECT id_promocion, nombre, descripcion, tipo, valor, 
           fecha_inicio, fecha_fin, estado
    FROM promocion
    WHERE estado = 'activa' AND CURDATE() BETWEEN fecha_inicio AND fecha_fin
    ORDER BY fecha_inicio DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerProximasCitas` (IN `p_id_cliente` INT)   BEGIN
    SELECT c.id_cita, c.fecha_programada, m.nombre AS mascota, 
           s.nombre AS servicio, c.estado, c.modalidad
    FROM cita c
    INNER JOIN mascota m ON c.id_mascota = m.id_mascota
    LEFT JOIN servicio s ON c.id_servicio = s.id_servicio
    WHERE c.id_cliente = p_id_cliente 
          AND c.fecha_programada > NOW()
          AND c.estado IN ('reservada','confirmada')
    ORDER BY c.fecha_programada ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerServicios` ()   BEGIN
    SELECT id_servicio, codigo, nombre, descripcion, duracion_estimada_min, 
           precio_base, categoria, created_at
    FROM servicio
    ORDER BY categoria, nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerServiciosPorCategoria` (IN `p_categoria` VARCHAR(20))   BEGIN
    SELECT id_servicio, codigo, nombre, descripcion, duracion_estimada_min, 
           precio_base, created_at
    FROM servicio
    WHERE categoria = p_categoria
    ORDER BY nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ObtenerSucursales` ()   BEGIN
    SELECT id_sucursal, nombre, direccion, telefono, created_at
    FROM sucursal
    ORDER BY nombre;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_OcupacionGroomer` (IN `p_fecha` DATE)   BEGIN
    SELECT g.nombre,
           COUNT(a.id_atencion) AS atenciones_realizadas,
           SUM(TIMESTAMPDIFF(MINUTE, a.tiempo_real_inicio, a.tiempo_real_fin)) AS minutos_trabajados,
           ROUND((SUM(TIMESTAMPDIFF(MINUTE, a.tiempo_real_inicio, a.tiempo_real_fin)) / 480.0) * 100, 2) AS porcentaje_ocupacion
    FROM atencion a
    INNER JOIN groomer g ON a.id_groomer = g.id_groomer
    WHERE DATE(a.tiempo_real_inicio) = p_fecha
          AND a.estado = 'terminado'
    GROUP BY g.id_groomer, g.nombre
    ORDER BY minutos_trabajados DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_RecalcularTotalesFacturas` ()   BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id_factura INT;
    DECLARE v_subtotal DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    
    DECLARE cur_facturas CURSOR FOR 
        SELECT DISTINCT f.id_factura
        FROM factura f
        INNER JOIN atencion a ON f.id_atencion = a.id_atencion
        INNER JOIN detalle_servicio ds ON a.id_atencion = ds.id_atencion;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur_facturas;
    
    read_loop: LOOP
        FETCH cur_facturas INTO v_id_factura;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Calcular nuevo subtotal
        SELECT COALESCE(SUM(ds.subtotal), 0) INTO v_subtotal
        FROM detalle_servicio ds
        INNER JOIN atencion a ON ds.id_atencion = a.id_atencion
        INNER JOIN factura f ON a.id_atencion = f.id_atencion
        WHERE f.id_factura = v_id_factura;
        
        -- Calcular nuevo total (subtotal + 18% impuesto)
        
        -- Actualizar factura
        UPDATE factura 
            impuesto = v_subtotal * 0.18,
            total = v_total,
            updated_at = NOW()
        WHERE id_factura = v_id_factura;
    END LOOP;
    
    CLOSE cur_facturas;
    
    SELECT 'Totales recalculados exitosamente' AS mensaje;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_RegistrarNotificacion` (IN `p_tipo` ENUM('sms','email','push'), IN `p_destinatario_id` INT, IN `p_canal` ENUM('cliente','usuario'), IN `p_contenido` TEXT, IN `p_referencia_tipo` VARCHAR(50), IN `p_referencia_id` INT)   BEGIN
    INSERT INTO notificacion (tipo, destinatario_id, canal, contenido, 
                             enviado_at, estado, referencia_tipo, referencia_id)
    VALUES (p_tipo, p_destinatario_id, p_canal, p_contenido, 
            NOW(), 'enviado', p_referencia_tipo, p_referencia_id);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_RegistrarPago` (IN `p_id_factura` INT, IN `p_monto` DECIMAL(10,2), IN `p_metodo` ENUM('efectivo','tarjeta','transfer','otro'), IN `p_referencia` VARCHAR(100))   BEGIN
    INSERT INTO pago (id_factura, monto, metodo, referencia, estado)
    VALUES (p_id_factura, p_monto, p_metodo, p_referencia, 'confirmado');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_RegistrarUsuarioSistema` (IN `p_nombre` VARCHAR(100), IN `p_rol` ENUM('recepcionista','admin','groomer','contador','veterinario'), IN `p_email` VARCHAR(120), IN `p_password_hash` VARCHAR(255))   BEGIN
    INSERT INTO usuario_sistema (nombre, rol, email, password_hash)
    VALUES (p_nombre, p_rol, p_email, p_password_hash);
    
    SELECT LAST_INSERT_ID() AS id_usuario;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ReporteIngresos` (IN `p_fecha_inicio` DATE, IN `p_fecha_fin` DATE, IN `p_id_sucursal` INT)   BEGIN
		SELECT DATE(f.fecha_emision) AS fecha, 
			   COUNT(f.id_factura) AS cantidad_facturas,
			   SUM(f.total) AS ingresos_totales,
			   AVG(f.total) AS promedio_por_factura
		FROM factura f
		INNER JOIN atencion a ON f.id_atencion = a.id_atencion
		WHERE f.estado = 'emitida' 
			  AND DATE(f.fecha_emision) BETWEEN p_fecha_inicio AND p_fecha_fin
			  AND (p_id_sucursal IS NULL OR a.id_sucursal = p_id_sucursal)
		GROUP BY DATE(f.fecha_emision)
		ORDER BY fecha;
	END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ReprogramarCita` (IN `p_id_cita` INT, IN `p_nueva_fecha` DATETIME)   BEGIN
    UPDATE cita
    WHERE id_cita = p_id_cita AND estado IN ('reservada','confirmada');
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ServiciosMasSolicitados` (IN `p_fecha_inicio` DATE, IN `p_fecha_fin` DATE)   BEGIN
    SELECT s.nombre, s.categoria, 
           COUNT(ds.id_servicio) AS veces_solicitado,
           SUM(ds.cantidad) AS cantidad_total,
           SUM(ds.subtotal) AS ingresos_generados
    FROM detalle_servicio ds
    INNER JOIN servicio s ON ds.id_servicio = s.id_servicio
    INNER JOIN atencion a ON ds.id_atencion = a.id_atencion
    INNER JOIN factura f ON a.id_atencion = f.id_atencion
    WHERE f.estado = 'emitida'
          AND DATE(f.fecha_emision) BETWEEN p_fecha_inicio AND p_fecha_fin
    GROUP BY s.id_servicio, s.nombre, s.categoria
    ORDER BY veces_solicitado DESC, ingresos_generados DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_TiemposPromedioGroomer` (IN `p_fecha_inicio` DATE, IN `p_fecha_fin` DATE)   BEGIN
    SELECT g.nombre, 
           COUNT(a.id_atencion) AS total_atenciones,
           AVG(TIMESTAMPDIFF(MINUTE, a.tiempo_real_inicio, a.tiempo_real_fin)) AS tiempo_promedio_minutos,
           MIN(TIMESTAMPDIFF(MINUTE, a.tiempo_real_inicio, a.tiempo_real_fin)) AS tiempo_minimo,
           MAX(TIMESTAMPDIFF(MINUTE, a.tiempo_real_inicio, a.tiempo_real_fin)) AS tiempo_maximo
    FROM atencion a
    INNER JOIN groomer g ON a.id_groomer = g.id_groomer
    WHERE a.estado = 'terminado' 
          AND a.tiempo_real_inicio IS NOT NULL 
          AND a.tiempo_real_fin IS NOT NULL
          AND DATE(a.tiempo_real_inicio) BETWEEN p_fecha_inicio AND p_fecha_fin
    GROUP BY g.id_groomer, g.nombre
    ORDER BY tiempo_promedio_minutos ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_ValidarUsuario` (IN `p_email` VARCHAR(120), IN `p_password_hash` VARCHAR(255))   BEGIN
    SELECT 
        id_usuario, 
        nombre, 
        rol, 
        email, 
        created_at,
        estado
    FROM 
        usuario_sistema
    WHERE 
        -- CORRECCIÓN: Forzamos la colación para que coincida con la de la tabla
        email = p_email COLLATE utf8mb4_unicode_ci
        AND 
        password_hash = p_password_hash COLLATE utf8mb4_unicode_ci
    LIMIT 1;
END$$

DELIMITER ;



CREATE TABLE `atencion` (
  `id_atencion` int(11) NOT NULL,
  `id_cita` int(11) DEFAULT NULL,
  `id_mascota` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `id_groomer` int(11) DEFAULT NULL,
  `id_sucursal` int(11) DEFAULT NULL,
  `estado` enum('en_espera','en_servicio','pausado','terminado') DEFAULT 'en_espera',
  `turno_num` int(11) DEFAULT NULL,
  `tiempo_estimado_inicio` datetime DEFAULT NULL,
  `tiempo_estimado_fin` datetime DEFAULT NULL,
  `tiempo_real_inicio` datetime DEFAULT NULL,
  `tiempo_real_fin` datetime DEFAULT NULL,
  `prioridad` tinyint(4) DEFAULT 0,
  `observaciones` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `atencion` (`id_atencion`, `id_cita`, `id_mascota`, `id_cliente`, `id_groomer`, `id_sucursal`, `estado`, `turno_num`, `tiempo_estimado_inicio`, `tiempo_estimado_fin`, `tiempo_real_inicio`, `tiempo_real_fin`, `prioridad`, `observaciones`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 1, 1, 1, 'terminado', 1, '2025-10-02 10:00:00', '2025-10-02 10:45:00', '2025-10-02 14:41:34', '2025-10-02 14:41:53', 0, 'En progreso', '2025-10-01 21:58:33', '2025-10-03 00:41:53'),
(2, 2, 2, 1, 2, 1, 'terminado', 2, '2025-10-02 11:30:00', '2025-10-02 12:20:00', NULL, '2025-10-02 11:33:15', 1, 'Cliente espera', '2025-10-01 21:58:33', '2025-10-02 21:33:15'),
(3, 3, 3, 2, 3, 2, 'terminado', 1, '2025-10-03 14:00:00', '2025-10-03 15:00:00', NULL, NULL, 0, 'Finalizado', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(4, 4, 4, 3, 4, 2, 'terminado', 2, '2025-10-03 15:00:00', '2025-10-03 15:30:00', '2025-10-02 14:42:02', '2025-10-02 14:42:17', 0, 'En limpieza', '2025-10-01 21:58:33', '2025-10-03 00:42:17'),
(5, 5, 5, 4, 5, 3, 'terminado', 1, '2025-10-04 09:00:00', '2025-10-04 11:00:00', '2025-10-02 11:50:45', '2025-10-02 12:47:11', 1, 'Paquete preparado', '2025-10-01 21:58:33', '2025-10-02 22:47:11'),
(6, 6, 6, 5, 6, 4, 'terminado', 1, '2025-10-04 10:00:00', '2025-10-04 10:40:00', NULL, '2025-10-03 13:53:37', 0, 'Cliente ausente', '2025-10-01 21:58:33', '2025-10-03 23:54:38'),
(7, 7, 7, 6, 7, 5, 'terminado', 1, '2025-10-05 10:00:00', '2025-10-05 11:10:00', NULL, NULL, 0, 'Finalizado corte', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(8, 8, 8, 7, 8, 6, 'en_espera', 1, '2025-10-05 11:00:00', '2025-10-05 11:25:00', NULL, NULL, 0, 'Cliente no llegó', '2025-10-01 21:58:33', '2025-10-04 00:15:53'),
(9, 9, 9, 8, 9, 7, 'terminado', 1, '2025-10-06 12:00:00', '2025-10-06 12:30:00', NULL, NULL, 0, 'Peinado completo', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(10, 10, 10, 9, 10, 8, 'terminado', 1, '2025-10-06 13:00:00', '2025-10-06 13:20:00', '2025-10-02 11:51:13', '2025-10-06 09:23:54', 0, 'Cepillado', '2025-10-01 21:58:33', '2025-10-06 19:23:54'),
(11, NULL, 1, 1, 1, 1, 'terminado', 0, '2025-10-17 11:38:00', '2025-10-17 11:40:00', '2025-10-02 12:17:18', '2025-10-02 12:17:36', 4, 'no hay', '2025-10-02 21:35:40', '2025-10-02 22:17:36');



CREATE TABLE `audit_log` (
  `id_log` int(11) NOT NULL,
  `entidad` varchar(50) DEFAULT NULL,
  `entidad_id` int(11) DEFAULT NULL,
  `accion` enum('INSERT','UPDATE','DELETE') DEFAULT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `antes` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`antes`)),
  `despues` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`despues`)),
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `audit_log` (`id_log`, `entidad`, `entidad_id`, `accion`, `id_usuario`, `antes`, `despues`, `timestamp`) VALUES
(1, 'cliente', 1, 'INSERT', 1, NULL, '{\"nombre\": \"Carlos\"}', '2025-10-01 21:58:33'),
(2, 'cliente', 2, 'INSERT', 1, NULL, '{\"nombre\": \"María\"}', '2025-10-01 21:58:33'),
(3, 'mascota', 1, 'INSERT', 1, NULL, '{\"nombre\": \"Firulais\"}', '2025-10-01 21:58:33'),
(4, 'mascota', 2, 'INSERT', 1, NULL, '{\"nombre\": \"Michi\"}', '2025-10-01 21:58:33'),
(5, 'servicio', 1, 'INSERT', 1, NULL, '{\"nombre\": \"Baño básico\"}', '2025-10-01 21:58:33'),
(6, 'servicio', 2, 'INSERT', 1, NULL, '{\"nombre\": \"Baño medicado\"}', '2025-10-01 21:58:33'),
(7, 'cita', 1, 'INSERT', 2, NULL, '{\"estado\": \"confirmada\"}', '2025-10-01 21:58:33'),
(8, 'cita', 2, 'INSERT', 2, NULL, '{\"estado\": \"reservada\"}', '2025-10-01 21:58:33'),
(9, 'factura', 1, 'INSERT', 3, NULL, '{\"total\": \"47.20\"}', '2025-10-01 21:58:33'),
(10, 'pago', 1, 'INSERT', 3, NULL, '{\"estado\": \"confirmado\"}', '2025-10-01 21:58:33');



CREATE TABLE `cita` (
  `id_cita` int(11) NOT NULL,
  `id_mascota` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `id_sucursal` int(11) DEFAULT NULL,
  `id_servicio` int(11) DEFAULT NULL,
  `fecha_programada` datetime NOT NULL,
  `modalidad` enum('presencial','virtual') DEFAULT 'presencial',
  `estado` enum('reservada','confirmada','cancelada','asistio','no_show') DEFAULT 'reservada',
  `notas` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `cita` (`id_cita`, `id_mascota`, `id_cliente`, `id_sucursal`, `id_servicio`, `fecha_programada`, `modalidad`, `estado`, `notas`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 1, 1, '2025-10-02 10:00:00', 'presencial', 'confirmada', 'Baño regular', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(2, 2, 1, 1, 3, '2025-10-02 11:30:00', 'presencial', 'asistio', 'Corte sencillo', '2025-10-01 21:58:33', '2025-10-04 01:11:42'),
(3, 3, 2, 2, 2, '2025-10-03 14:00:00', 'presencial', 'confirmada', 'Baño medicado', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(4, 4, 3, 2, 5, '2025-10-03 15:00:00', 'presencial', 'asistio', 'Limpieza dental', '2025-10-01 21:58:33', '2025-10-04 01:11:47'),
(5, 5, 4, 3, 6, '2025-10-04 09:00:00', 'presencial', 'confirmada', 'Paquete completo', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(6, 6, 5, 4, 7, '2025-10-04 10:00:00', 'presencial', 'cancelada', 'Deslanado', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(7, 7, 6, 5, 4, '2025-10-05 10:00:00', 'presencial', 'asistio', 'Corte especializado', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(8, 8, 7, 6, 9, '2025-10-05 11:00:00', 'presencial', 'no_show', 'Baño express', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(9, 9, 8, 7, 8, '2025-10-06 12:00:00', 'presencial', 'confirmada', 'Peinado exposición', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(10, 10, 9, 8, 10, '2025-10-06 13:00:00', 'presencial', 'reservada', 'Cepillado', '2025-10-01 21:58:33', '2025-10-01 21:58:33');



CREATE TABLE `cliente` (
  `id_cliente` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `dni_ruc` varchar(20) DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `preferencias` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`preferencias`)),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `cliente` (`id_cliente`, `nombre`, `apellido`, `dni_ruc`, `email`, `telefono`, `direccion`, `preferencias`, `created_at`, `updated_at`) VALUES
(1, 'Carlos', 'Ramírez', '12345678', 'carlos.ramirez@mail.com', '987654321', 'Av. Los Olivos 123', '{\"comunicacion\": \"email\"}', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(2, 'María', 'Gómez', '87654321', 'maria.gomez@mail.com', '912345678', 'Jr. Primavera 456', '{\"recordatorio\": \"sms\"}', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(3, 'Luis', 'Fernández', '11223344', 'luis.fernandez@mail.com', '934567890', 'Calle Sol 789', '{\"horario\": \"mañana\"}', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(4, 'Ana', 'Torres', '22334455', 'ana.torres@mail.com', '945612378', 'Av. Grau 555', NULL, '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(5, 'Pedro', 'Martínez', '33445566', 'pedro.martinez@mail.com', '956789123', 'Calle Central 99', NULL, '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(6, 'Lucía', 'Quispe', '44556677', 'lucia.quispe@mail.com', '967891234', 'Mz J Lt 5 San Juan', '{\"preferencia\": \"paquete premium\"}', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(7, 'Jorge', 'Santos', '55667788', 'jorge.santos@mail.com', '978912345', 'Av. Progreso 44', NULL, '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(8, 'Carla', 'Mendoza', '66778899', 'carla.mendoza@mail.com', '989123456', 'Jr. Arequipa 333', NULL, '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(9, 'Miguel', 'Lopez', '77889900', 'miguel.lopez@mail.com', '900123456', 'Av. La Marina 1200', NULL, '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(10, 'Rosa', 'Salazar', '88990011', 'rosa.salazar@mail.com', '911234567', 'Calle Norte 88', '{\"avisos\": \"push\"}', '2025-10-01 21:58:32', '2025-10-01 21:58:32');



CREATE TABLE `configuracion_estimacion` (
  `id_config` int(11) NOT NULL,
  `id_servicio` int(11) DEFAULT NULL,
  `id_groomer` int(11) DEFAULT NULL,
  `tiempo_estimado_min` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `configuracion_estimacion` (`id_config`, `id_servicio`, `id_groomer`, `tiempo_estimado_min`) VALUES
(1, 1, 1, 45),
(2, 2, 2, 60),
(3, 3, 3, 50),
(4, 4, 4, 70),
(5, 5, 5, 30),
(6, 6, 6, 120),
(7, 7, 7, 40),
(8, 8, 8, 45),
(9, 9, 9, 25),
(10, 10, 10, 20);



CREATE TABLE `detalle_servicio` (
  `id_detalle` int(11) NOT NULL,
  `id_atencion` int(11) NOT NULL,
  `id_servicio` int(11) NOT NULL,
  `cantidad` int(11) DEFAULT 1,
  `precio_unitario` decimal(10,2) NOT NULL,
  `descuento_id` int(11) DEFAULT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `observaciones` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `detalle_servicio` (`id_detalle`, `id_atencion`, `id_servicio`, `cantidad`, `precio_unitario`, `descuento_id`, `subtotal`, `observaciones`) VALUES
(1, 1, 1, 1, 40.00, NULL, 40.00, 'Baño estándar'),
(2, 2, 3, 1, 50.00, NULL, 50.00, 'Corte simple'),
(3, 3, 2, 1, 60.00, NULL, 60.00, 'Baño medicado'),
(4, 4, 5, 1, 70.00, NULL, 70.00, 'Limpieza dental'),
(5, 5, 6, 1, 150.00, NULL, 150.00, 'Paquete completo'),
(6, 6, 7, 1, 55.00, NULL, 55.00, 'Deslanado'),
(7, 7, 4, 1, 80.00, NULL, 80.00, 'Corte especializado'),
(8, 8, 9, 1, 25.00, NULL, 25.00, 'Baño express'),
(9, 9, 8, 1, 65.00, NULL, 65.00, 'Peinado especial'),
(10, 10, 10, 1, 20.00, NULL, 20.00, 'Cepillado');



CREATE TABLE `factura` (
  `id_factura` int(11) NOT NULL,
  `serie` varchar(10) DEFAULT NULL,
  `numero` varchar(20) DEFAULT NULL,
  `id_cliente` int(11) NOT NULL,
  `id_atencion` int(11) NOT NULL,
  `fecha_emision` datetime DEFAULT current_timestamp(),
  `subtotal` decimal(10,2) NOT NULL,
  `impuesto` decimal(10,2) DEFAULT NULL,
  `descuento_total` decimal(10,2) DEFAULT 0.00,
  `total` decimal(10,2) NOT NULL,
  `estado` enum('emitida','anulada') DEFAULT 'emitida',
  `metodo_pago_sugerido` enum('efectivo','tarjeta','transfer','otro') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `factura` (`id_factura`, `serie`, `numero`, `id_cliente`, `id_atencion`, `fecha_emision`, `subtotal`, `impuesto`, `descuento_total`, `total`, `estado`, `metodo_pago_sugerido`) VALUES
(1, 'F001', '0001', 1, 1, '2025-10-01 11:58:33', 40.00, 7.20, 0.00, 47.20, 'emitida', 'efectivo'),
(2, 'F001', '0002', 1, 2, '2025-10-01 11:58:33', 50.00, 9.00, 0.00, 59.00, 'emitida', 'tarjeta'),
(3, 'F001', '0003', 2, 3, '2025-10-01 11:58:33', 60.00, 10.80, 0.00, 70.80, 'emitida', 'efectivo'),
(4, 'F001', '0004', 3, 4, '2025-10-01 11:58:33', 70.00, 12.60, 0.00, 82.60, 'emitida', 'tarjeta'),
(5, 'F001', '0005', 4, 5, '2025-10-01 11:58:33', 150.00, 27.00, 0.00, 177.00, 'emitida', 'transfer'),
(6, 'F001', '0006', 5, 6, '2025-10-01 11:58:33', 55.00, 9.90, 0.00, 64.90, 'emitida', 'efectivo'),
(7, 'F001', '0007', 6, 7, '2025-10-01 11:58:33', 80.00, 14.40, 0.00, 94.40, 'emitida', 'tarjeta'),
(8, 'F001', '0008', 7, 8, '2025-10-01 11:58:33', 25.00, 4.50, 0.00, 29.50, 'emitida', 'efectivo'),
(9, 'F001', '0009', 8, 9, '2025-10-01 11:58:33', 65.00, 11.70, 0.00, 76.70, 'emitida', 'efectivo'),
(10, 'F001', '0010', 9, 10, '2025-10-01 11:58:33', 20.00, 3.60, 0.00, 23.60, 'emitida', 'efectivo');



CREATE TABLE `groomer` (
  `id_groomer` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `especialidades` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`especialidades`)),
  `disponibilidad` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`disponibilidad`)),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `groomer` (`id_groomer`, `nombre`, `especialidades`, `disponibilidad`, `created_at`, `updated_at`) VALUES
(1, 'Andrea', '[\"bano\",\"corte\",\"Terapia\"]', '{\"dias\":[\"lunes\",\"miercoles\",\"jueves\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 20:03:55'),
(2, 'Jose Perez', '[\"corte\",\"dental\"]', '{\"dias\":[\"martes\",\"sabado\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 19:33:12'),
(3, 'Claudia Ramos', '[\"ba√Īo\"]', '{\"dias\":[\"jueves\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 02:42:37'),
(4, 'Ricardo Díaz', '[\"corte\",\"paquete\"]', '{\"dias\":[\"miercoles\",\"jueves\",\"viernes\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 19:35:56'),
(5, 'Laura Fernández', '[\"ba?o\",\"corte\",\"dental\"]', '{\"dias\":[\"domingo\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 19:33:33'),
(6, 'David Morales', '[\"corte\"]', '{\"dias\":[\"lunes\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 19:30:31'),
(7, 'Carolina Vega', '[\"ba√Īo\",\"paquete\"]', '{\"dias\":[\"martes\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 02:31:40'),
(8, 'Hugo Castro', '[\"dental\"]', '{\"dias\":[\"jueves\",\"domingo\"],\"horaInicio\":\"08:00\",\"horaFin\":\"18:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 19:30:58'),
(9, 'Paola Su√°rez', '[\"corte\",\"ba?o\"]', '{\"dias\":[\"lunes\",\"martes\",\"viernes\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 19:35:41'),
(10, 'Felipe Torres', '[\"paquete\",\"corte\"]', '{\"dias\":[\"miercoles\",\"domingo\"],\"horaInicio\":\"08:00\",\"horaFin\":\"17:00\",\"tiempoCita\":60}', '2025-10-01 21:58:33', '2025-10-02 19:30:39');



CREATE TABLE `mascota` (
  `id_mascota` int(11) NOT NULL,
  `id_cliente` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `especie` enum('perro','gato','otro') NOT NULL,
  `raza` varchar(100) DEFAULT NULL,
  `sexo` enum('macho','hembra','otro') DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `microchip` varchar(50) DEFAULT NULL,
  `observaciones` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `mascota` (`id_mascota`, `id_cliente`, `nombre`, `especie`, `raza`, `sexo`, `fecha_nacimiento`, `microchip`, `observaciones`, `created_at`, `updated_at`) VALUES
(1, 1, 'Firulais', 'perro', 'Labrador', 'macho', '2018-05-01', 'MC1001', 'Le gusta correr', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(2, 1, 'Michi', 'gato', 'Siames', 'hembra', '2020-02-14', 'MC1002', 'Muy tranquila', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(3, 2, 'Rocky', 'perro', 'Bulldog', 'macho', '2019-07-20', 'MC1003', 'Alérgico a pollo', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(4, 3, 'Luna', 'gato', 'Persa', 'hembra', '2021-01-10', 'MC1004', 'Se asusta fácil', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(5, 4, 'Toby', 'perro', 'Poodle', 'macho', '2020-09-09', 'MC1005', 'Usa correa roja', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(6, 5, 'Nina', 'gato', 'Mestizo', 'hembra', '2017-11-22', 'MC1006', 'Come poco', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(7, 6, 'Rex', 'perro', 'Pastor Alemán', 'macho', '2016-03-18', 'MC1007', 'Muy protector', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(8, 7, 'Kira', 'perro', 'Husky', 'hembra', '2021-06-30', 'MC1008', 'Juguetona', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(9, 8, 'Coco', 'gato', 'Maine Coon', 'macho', '2019-04-15', 'MC1009', 'Grande y peludo', '2025-10-01 21:58:32', '2025-10-01 21:58:32'),
(10, 9, 'Boby', 'perro', 'Beagle', 'macho', '2020-12-25', 'MC1010', 'Ladra mucho', '2025-10-01 21:58:32', '2025-10-01 21:58:32');



CREATE TABLE `notificacion` (
  `id_notificacion` int(11) NOT NULL,
  `tipo` enum('sms','email','push') DEFAULT NULL,
  `destinatario_id` int(11) NOT NULL,
  `canal` enum('cliente','usuario') DEFAULT NULL,
  `contenido` text DEFAULT NULL,
  `enviado_at` datetime DEFAULT NULL,
  `estado` enum('pendiente','enviado','fallido') DEFAULT 'pendiente',
  `referencia_tipo` varchar(50) DEFAULT NULL,
  `referencia_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `notificacion` (`id_notificacion`, `tipo`, `destinatario_id`, `canal`, `contenido`, `enviado_at`, `estado`, `referencia_tipo`, `referencia_id`) VALUES
(1, 'sms', 1, 'cliente', 'Su cita está confirmada', '2025-10-01 09:00:00', 'enviado', 'cita', 1),
(2, 'email', 2, 'cliente', 'Recordatorio de cita', '2025-10-01 10:00:00', 'enviado', 'cita', 2),
(3, 'push', 3, 'cliente', 'Gracias por su visita', '2025-10-01 11:00:00', 'enviado', 'atencion', 3),
(4, 'sms', 4, 'cliente', 'Su factura está lista', '2025-10-01 12:00:00', 'enviado', 'factura', 4),
(5, 'email', 5, 'cliente', 'Pago confirmado', '2025-10-01 13:00:00', 'enviado', 'pago', 5),
(6, 'push', 6, 'cliente', 'Promoción activa para grooming', '2025-10-01 14:00:00', 'enviado', 'promocion', 1),
(7, 'sms', 7, 'cliente', 'Cita reprogramada', '2025-10-01 15:00:00', 'pendiente', 'cita', 7),
(8, 'email', 8, 'cliente', 'Factura disponible', '2025-10-01 16:00:00', 'enviado', 'factura', 8),
(9, 'push', 9, 'cliente', 'Recuerde su cita mañana', '2025-10-01 17:00:00', 'enviado', 'cita', 9),
(10, 'sms', 10, 'cliente', 'Nuevo paquete disponible', '2025-10-01 18:00:00', 'pendiente', 'paquete', 2);



CREATE TABLE `pago` (
  `id_pago` int(11) NOT NULL,
  `id_factura` int(11) NOT NULL,
  `fecha_pago` datetime DEFAULT current_timestamp(),
  `monto` decimal(10,2) NOT NULL,
  `metodo` enum('efectivo','tarjeta','transfer','otro') DEFAULT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `estado` enum('pendiente','confirmado','fallido') DEFAULT 'confirmado'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `pago` (`id_pago`, `id_factura`, `fecha_pago`, `monto`, `metodo`, `referencia`, `estado`) VALUES
(1, 1, '2025-10-01 11:58:33', 47.20, 'efectivo', 'Caja1', 'confirmado'),
(2, 2, '2025-10-01 11:58:33', 59.00, 'tarjeta', 'POS123', 'confirmado'),
(3, 3, '2025-10-01 11:58:33', 70.80, 'efectivo', 'Caja2', 'confirmado'),
(4, 4, '2025-10-01 11:58:33', 82.60, 'tarjeta', 'POS124', 'confirmado'),
(5, 5, '2025-10-01 11:58:33', 177.00, 'transfer', 'TRX001', 'confirmado'),
(6, 6, '2025-10-01 11:58:33', 64.90, 'efectivo', 'Caja3', 'confirmado'),
(7, 7, '2025-10-01 11:58:33', 94.40, 'tarjeta', 'POS125', 'confirmado'),
(8, 8, '2025-10-01 11:58:33', 29.50, 'efectivo', 'Caja4', 'confirmado'),
(9, 9, '2025-10-01 11:58:33', 76.70, 'efectivo', 'Caja5', 'confirmado'),
(10, 10, '2025-10-01 11:58:33', 23.60, 'efectivo', 'Caja6', 'confirmado');



CREATE TABLE `paquete_servicio` (
  `id_paquete` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio_total` decimal(10,2) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `paquete_servicio` (`id_paquete`, `nombre`, `descripcion`, `precio_total`, `created_at`, `updated_at`) VALUES
(1, 'Paquete Premium', 'Incluye baño, corte y dental', 160.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(2, 'Paquete Básico', 'Incluye baño y corte', 90.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(3, 'Paquete Dental', 'Solo limpieza dental y revisión', 75.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(4, 'Paquete Relax', 'Baño medicado y corte básico', 110.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(5, 'Paquete Express', 'Baño express + cepillado', 40.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(6, 'Paquete Full Grooming', 'Todos los servicios disponibles', 250.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(7, 'Paquete Cachorro', 'Servicios adaptados a cachorros', 85.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(8, 'Paquete Senior', 'Servicios para mascotas adultas', 95.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(9, 'Paquete Raza', 'Corte especializado + peinado', 120.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(10, 'Paquete Festival', 'Paquete especial con descuento', 100.00, '2025-10-01 21:58:33', '2025-10-01 21:58:33');



CREATE TABLE `paquete_servicio_item` (
  `id_paquete` int(11) NOT NULL,
  `id_servicio` int(11) NOT NULL,
  `cantidad` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `paquete_servicio_item` (`id_paquete`, `id_servicio`, `cantidad`) VALUES
(1, 1, 1),
(1, 3, 1),
(1, 5, 1),
(2, 1, 1),
(2, 3, 1),
(3, 5, 1),
(4, 2, 1),
(4, 3, 1),
(5, 9, 1),
(5, 10, 1),
(6, 1, 1),
(6, 2, 1),
(6, 3, 1),
(6, 5, 1),
(6, 7, 1),
(6, 8, 1),
(6, 10, 1),
(7, 1, 1),
(7, 10, 1),
(8, 2, 1),
(8, 7, 1),
(9, 4, 1),
(9, 8, 1),
(10, 1, 1),
(10, 3, 1);



CREATE TABLE `promocion` (
  `id_promocion` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `tipo` enum('porcentaje','monto') DEFAULT NULL,
  `valor` decimal(10,2) DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `estado` enum('activa','inactiva') DEFAULT 'activa'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `promocion` (`id_promocion`, `nombre`, `descripcion`, `tipo`, `valor`, `fecha_inicio`, `fecha_fin`, `estado`) VALUES
(1, 'Promo Octubre', '10% en todos los baños', 'porcentaje', 10.00, '2025-10-01', '2025-10-31', 'activa'),
(2, 'Promo Dental', 'Descuento fijo en dental', 'monto', 15.00, '2025-10-01', '2025-10-15', 'activa'),
(3, 'Promo Corte', '15% en cortes', 'porcentaje', 15.00, '2025-10-10', '2025-10-20', 'activa'),
(4, 'Promo Cachorros', '20% en grooming cachorros', 'porcentaje', 20.00, '2025-10-05', '2025-10-30', 'activa'),
(5, 'Promo Express', '5 soles menos en baño express', 'monto', 5.00, '2025-10-01', '2025-10-31', 'activa'),
(6, 'Promo Senior', '10% en mascotas mayores', 'porcentaje', 10.00, '2025-10-01', '2025-11-01', 'activa'),
(7, 'Promo Halloween', '25% en grooming temático', 'porcentaje', 25.00, '2025-10-20', '2025-10-31', 'activa'),
(8, 'Promo Paquete Full', '30 soles menos en Full Grooming', 'monto', 30.00, '2025-10-01', '2025-10-31', 'activa'),
(9, 'Promo Amigos', 'Trae 2 mascotas y obtén 15%', 'porcentaje', 15.00, '2025-10-01', '2025-10-31', 'activa'),
(10, 'Promo Bienvenida', 'Descuento 20 soles nuevos clientes', 'monto', 20.00, '2025-10-01', '2025-12-01', 'activa');



CREATE TABLE `servicio` (
  `id_servicio` int(11) NOT NULL,
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `duracion_estimada_min` int(11) NOT NULL,
  `precio_base` decimal(10,2) NOT NULL,
  `categoria` enum('baño','corte','dental','paquete','otro') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `servicio` (`id_servicio`, `codigo`, `nombre`, `descripcion`, `duracion_estimada_min`, `precio_base`, `categoria`, `created_at`, `updated_at`) VALUES
(1, 'S001', 'Baño básico', 'Baño con shampoo estándar', 45, 45.00, 'baño', '2025-10-01 21:58:33', '2025-10-03 20:58:51'),
(2, 'S002', 'Baño medicado', 'Baño especial para piel sensible', 60, 60.00, 'baño', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(3, 'S003', 'Corte básico', 'Corte de pelo estándar', 50, 50.00, 'corte', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(4, 'S004', 'Corte especializado', 'Corte de raza', 70, 80.00, 'corte', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(5, 'S005', 'Limpieza dental', 'Higiene bucal básica', 30, 70.00, 'dental', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(6, 'S006', 'Paquete completo', 'Baño + corte + limpieza dental', 120, 150.00, 'paquete', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(7, 'S007', 'Deslanado', 'Eliminación de pelo muerto', 40, 55.00, 'corte', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(8, 'S008', 'Peinado especial', 'Peinado para exposición', 30, 65.00, 'corte', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(9, 'S009', 'Baño express', 'Baño rápido', 25, 25.00, 'baño', '2025-10-01 21:58:33', '2025-10-03 22:09:55'),
(10, 'S010', 'Cepillado', 'Cepillado básico', 20, 20.00, 'otro', '2025-10-01 21:58:33', '2025-10-01 21:58:33');



CREATE TABLE `sucursal` (
  `id_sucursal` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `sucursal` (`id_sucursal`, `nombre`, `direccion`, `telefono`, `created_at`, `updated_at`) VALUES
(1, 'Sucursal Centro', 'Av. Central 100', '987111222', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(2, 'Sucursal Norte', 'Av. Los Andes 200', '987222333', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(3, 'Sucursal Sur', 'Av. Primavera 300', '987333444', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(4, 'Sucursal Este', 'Av. Sol 400', '987444555', '0000-00-00 00:00:00', '2025-10-01 21:58:33'),
(5, 'Sucursal Oeste', 'Av. Luna 500', '987555666', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(6, 'Sucursal San Juan', 'Av. San Juan 600', '987666777', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(7, 'Sucursal Callao', 'Av. Callao 700', '987777888', '2025-10-01 21:58:33', '2025-10-07 03:05:27'),
(8, 'Sucursal Miraflores', 'Av. Pardo 800', '987888999', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(9, 'Sucursal San Isidro', 'Av. Javier Prado 900', '987999000', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(10, 'Sucursal Lince', 'Av. Arequipa 1000', '986111222', '2025-10-01 21:58:33', '2025-10-01 21:58:33'),
(11, 'Sucursal Surquillo', 'Av. Panama 520', '016127878', '2025-10-07 03:06:09', '2025-10-07 03:06:09'),
(12, 'Sucursal Surquillo', 'Av. Panama 520', '016127878', '2025-10-07 03:06:19', '2025-10-07 03:06:19'),
(13, 'Sucursal Surquillo', 'Av. Panama 520', '016127878', '2025-10-07 03:21:37', '2025-10-07 03:21:37'),
(14, 'Sucursal Surquillo', 'Av. Panama 520', '016127878', '2025-10-07 03:25:33', '2025-10-07 03:25:33'),
(15, 'Sucursal Surquillo', 'Av. Panama 520', '016127878', '2025-10-07 03:30:37', '2025-10-07 03:30:37');



CREATE TABLE `usuario_sistema` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `rol` enum('recepcionista','admin','groomer','contador','veterinario') DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'ACTIVO'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `usuario_sistema` (`id_usuario`, `nombre`, `rol`, `email`, `password_hash`, `created_at`, `updated_at`, `estado`) VALUES
(1, 'Admin General', 'admin', 'admin@vet.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', '2025-10-01 21:58:33', '2025-11-06 04:49:31', 'ACTIVO'),
(2, 'Recepción Central', 'recepcionista', 'recepcion@vet.com', 'hash2', '2025-10-01 21:58:33', '2025-11-05 05:14:53', 'ACTIVO'),
(3, 'Contador Principal', 'contador', 'conta@vet.com', 'hash3', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO'),
(4, 'Vet Juan', 'veterinario', 'vetjuan@vet.com', 'hash4', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO'),
(5, 'Groomer Andrea', 'groomer', 'groomer1@vet.com', 'hash5', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO'),
(6, 'Groomer José', 'groomer', 'groomer2@vet.com', 'hash6', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO'),
(7, 'Groomer Claudia', 'groomer', 'groomer3@vet.com', 'hash7', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO'),
(8, 'Groomer Ricardo', 'groomer', 'groomer4@vet.com', 'hash8', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO'),
(9, 'Groomer Laura', 'groomer', 'groomer5@vet.com', 'hash9', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO'),
(10, 'Soporte Técnico', 'admin', 'soporte@vet.com', 'hash10', '2025-10-01 21:58:33', '2025-10-01 21:58:33', 'ACTIVO');


ALTER TABLE `atencion`
  ADD PRIMARY KEY (`id_atencion`),
  ADD KEY `fk_atencion_cita` (`id_cita`),
  ADD KEY `fk_atencion_mascota` (`id_mascota`),
  ADD KEY `fk_atencion_cliente` (`id_cliente`),
  ADD KEY `fk_atencion_groomer` (`id_groomer`);

ALTER TABLE `audit_log`
  ADD PRIMARY KEY (`id_log`),
  ADD KEY `fk_audit_usuario` (`id_usuario`);

ALTER TABLE `cita`
  ADD PRIMARY KEY (`id_cita`),
  ADD KEY `fk_cita_mascota` (`id_mascota`),
  ADD KEY `fk_cita_cliente` (`id_cliente`),
  ADD KEY `fk_cita_servicio` (`id_servicio`);

ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id_cliente`),
  ADD UNIQUE KEY `dni_ruc` (`dni_ruc`);

ALTER TABLE `configuracion_estimacion`
  ADD PRIMARY KEY (`id_config`),
  ADD KEY `fk_conf_servicio` (`id_servicio`),
  ADD KEY `fk_conf_groomer` (`id_groomer`);

ALTER TABLE `detalle_servicio`
  ADD PRIMARY KEY (`id_detalle`),
  ADD KEY `fk_detalle_atencion` (`id_atencion`),
  ADD KEY `fk_detalle_servicio` (`id_servicio`);

ALTER TABLE `factura`
  ADD PRIMARY KEY (`id_factura`),
  ADD KEY `fk_factura_cliente` (`id_cliente`),
  ADD KEY `fk_factura_atencion` (`id_atencion`);

ALTER TABLE `groomer`
  ADD PRIMARY KEY (`id_groomer`);

ALTER TABLE `mascota`
  ADD PRIMARY KEY (`id_mascota`),
  ADD UNIQUE KEY `microchip` (`microchip`),
  ADD KEY `fk_mascota_cliente` (`id_cliente`);

ALTER TABLE `notificacion`
  ADD PRIMARY KEY (`id_notificacion`);

ALTER TABLE `pago`
  ADD PRIMARY KEY (`id_pago`),
  ADD KEY `fk_pago_factura` (`id_factura`);

ALTER TABLE `paquete_servicio`
  ADD PRIMARY KEY (`id_paquete`);

ALTER TABLE `paquete_servicio_item`
  ADD PRIMARY KEY (`id_paquete`,`id_servicio`),
  ADD KEY `fk_item_servicio` (`id_servicio`);

ALTER TABLE `promocion`
  ADD PRIMARY KEY (`id_promocion`);

ALTER TABLE `servicio`
  ADD PRIMARY KEY (`id_servicio`),
  ADD UNIQUE KEY `codigo` (`codigo`);

ALTER TABLE `sucursal`
  ADD PRIMARY KEY (`id_sucursal`);

ALTER TABLE `usuario_sistema`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `email` (`email`);


ALTER TABLE `atencion`
  MODIFY `id_atencion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

ALTER TABLE `audit_log`
  MODIFY `id_log` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `cita`
  MODIFY `id_cita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `cliente`
  MODIFY `id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `configuracion_estimacion`
  MODIFY `id_config` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

ALTER TABLE `detalle_servicio`
  MODIFY `id_detalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `factura`
  MODIFY `id_factura` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `groomer`
  MODIFY `id_groomer` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `mascota`
  MODIFY `id_mascota` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `notificacion`
  MODIFY `id_notificacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `pago`
  MODIFY `id_pago` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `paquete_servicio`
  MODIFY `id_paquete` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `promocion`
  MODIFY `id_promocion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

ALTER TABLE `servicio`
  MODIFY `id_servicio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

ALTER TABLE `sucursal`
  MODIFY `id_sucursal` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

ALTER TABLE `usuario_sistema`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;


ALTER TABLE `atencion`
  ADD CONSTRAINT `fk_atencion_cita` FOREIGN KEY (`id_cita`) REFERENCES `cita` (`id_cita`),
  ADD CONSTRAINT `fk_atencion_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  ADD CONSTRAINT `fk_atencion_groomer` FOREIGN KEY (`id_groomer`) REFERENCES `groomer` (`id_groomer`),
  ADD CONSTRAINT `fk_atencion_mascota` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`);

ALTER TABLE `audit_log`
  ADD CONSTRAINT `fk_audit_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario_sistema` (`id_usuario`);

ALTER TABLE `cita`
  ADD CONSTRAINT `fk_cita_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  ADD CONSTRAINT `fk_cita_mascota` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`id_mascota`),
  ADD CONSTRAINT `fk_cita_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`);

ALTER TABLE `configuracion_estimacion`
  ADD CONSTRAINT `fk_conf_groomer` FOREIGN KEY (`id_groomer`) REFERENCES `groomer` (`id_groomer`),
  ADD CONSTRAINT `fk_conf_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`);

ALTER TABLE `detalle_servicio`
  ADD CONSTRAINT `fk_detalle_atencion` FOREIGN KEY (`id_atencion`) REFERENCES `atencion` (`id_atencion`),
  ADD CONSTRAINT `fk_detalle_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`);

ALTER TABLE `factura`
  ADD CONSTRAINT `fk_factura_atencion` FOREIGN KEY (`id_atencion`) REFERENCES `atencion` (`id_atencion`),
  ADD CONSTRAINT `fk_factura_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`);

ALTER TABLE `mascota`
  ADD CONSTRAINT `fk_mascota_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`);

ALTER TABLE `pago`
  ADD CONSTRAINT `fk_pago_factura` FOREIGN KEY (`id_factura`) REFERENCES `factura` (`id_factura`);

ALTER TABLE `paquete_servicio_item`
  ADD CONSTRAINT `fk_item_paquete` FOREIGN KEY (`id_paquete`) REFERENCES `paquete_servicio` (`id_paquete`),
  ADD CONSTRAINT `fk_item_servicio` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`);
COMMIT;

SET FOREIGN_KEY_CHECKS=1;