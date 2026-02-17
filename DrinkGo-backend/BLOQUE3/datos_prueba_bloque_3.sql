-- ============================================
-- Datos de Prueba - Bloque 3
-- DrinkGo Platform
-- Sedes, Horarios, Mesas y Configuración
-- ============================================

USE drinkgo_db;

-- ============================================
-- 1. USUARIOS-SEDES: Asignar usuarios a sedes
-- ============================================
INSERT INTO usuarios_sedes (usuario_id, sede_id, es_predeterminado, asignado_en) VALUES
(1, 1, 1, NOW()), -- Usuario 1 asignado a Sede Principal (predeterminada)
(1, 2, 0, NOW()), -- Usuario 1 también tiene acceso a Sede 2
(2, 1, 1, NOW()), -- Usuario 2 asignado a Sede Principal
(3, 2, 1, NOW()); -- Usuario 3 asignado a Sede 2

-- ============================================
-- 2. HORARIOS SEDE: Horarios semanales
-- ============================================
-- Sede 1: De Lunes a Domingo (0=Domingo, 6=Sábado)
INSERT INTO horarios_sede (sede_id, dia_semana, hora_apertura, hora_cierre, esta_cerrado, creado_en, actualizado_en) VALUES
-- Sede 1
(1, 0, '10:00:00', '22:00:00', 0, NOW(), NOW()), -- Domingo
(1, 1, '08:00:00', '23:00:00', 0, NOW(), NOW()), -- Lunes
(1, 2, '08:00:00', '23:00:00', 0, NOW(), NOW()), -- Martes
(1, 3, '08:00:00', '23:00:00', 0, NOW(), NOW()), -- Miércoles
(1, 4, '08:00:00', '23:00:00', 0, NOW(), NOW()), -- Jueves
(1, 5, '08:00:00', '01:00:00', 0, NOW(), NOW()), -- Viernes (hasta 1am)
(1, 6, '08:00:00', '02:00:00', 0, NOW(), NOW()), -- Sábado (hasta 2am)
-- Sede 2
(2, 0, NULL, NULL, 1, NOW(), NOW()), -- Domingo cerrado
(2, 1, '09:00:00', '22:00:00', 0, NOW(), NOW()), -- Lunes
(2, 2, '09:00:00', '22:00:00', 0, NOW(), NOW()), -- Martes
(2, 3, '09:00:00', '22:00:00', 0, NOW(), NOW()), -- Miércoles
(2, 4, '09:00:00', '22:00:00', 0, NOW(), NOW()), -- Jueves
(2, 5, '09:00:00', '00:00:00', 0, NOW(), NOW()), -- Viernes
(2, 6, '10:00:00', '01:00:00', 0, NOW(), NOW()); -- Sábado

-- ============================================
-- 3. HORARIOS ESPECIALES: Feriados y fechas especiales
-- ============================================
INSERT INTO horarios_especiales_sede (sede_id, fecha, hora_apertura, hora_cierre, motivo, creado_en) VALUES
(1, '2026-12-25', NULL, NULL, 'Cerrado por Navidad', NOW()),
(1, '2026-01-01', NULL, NULL, 'Cerrado por Año Nuevo', NOW()),
(1, '2026-07-28', '10:00:00', '18:00:00', 'Fiestas Patrias - Horario Especial', NOW()),
(1, '2026-12-24', '08:00:00', '20:00:00', 'Nochebuena - Cierre Temprano', NOW()),
(2, '2026-12-25', NULL, NULL, 'Cerrado por Navidad', NOW());

-- ============================================
-- 4. RESTRICCIONES VENTA ALCOHOL
-- ============================================
INSERT INTO restricciones_venta_alcohol (
    negocio_id, sede_id, tipo_restriccion, dias_restringidos, 
    hora_inicio_restriccion, hora_fin_restriccion, edad_minima_requerida, 
    aplica_a_delivery, aplica_a_pos, aplica_a_tienda_online, 
    esta_activo, creado_en, actualizado_en
) VALUES
-- Restricción nacional: No venta domingos y feriados
(1, NULL, 'dia_completo', '["domingo"]', NULL, NULL, 18, 1, 1, 1, 1, NOW(), NOW()),
-- Restricción sede 1: No venta después de 11pm entre semana
(1, 1, 'horario', '["lunes","martes","miercoles","jueves"]', '23:00:00', '06:00:00', 18, 1, 1, 0, 1, NOW(), NOW()),
-- Restricción edad mínima
(1, NULL, 'edad_minima', NULL, NULL, NULL, 18, 1, 1, 1, 1, NOW(), NOW());

-- ============================================
-- 5. ÁREAS DE MESAS
-- ============================================
INSERT INTO areas_mesas (negocio_id, sede_id, nombre, descripcion, orden, esta_activo, creado_en, actualizado_en) VALUES
(1, 1, 'Terraza', 'Mesas al aire libre con vista a la calle', 1, 1, NOW(), NOW()),
(1, 1, 'Salón Principal', 'Área interior principal', 2, 1, NOW(), NOW()),
(1, 1, 'Barra', 'Mesas altas junto a la barra', 3, 1, NOW(), NOW()),
(1, 1, 'Zona VIP', 'Área privada para grupos', 4, 1, NOW(), NOW()),
(1, 2, 'Salón Principal', 'Área principal de la sede 2', 1, 1, NOW(), NOW()),
(1, 2, 'Patio', 'Área al aire libre', 2, 1, NOW(), NOW());

-- ============================================
-- 6. MESAS
-- ============================================
INSERT INTO mesas (
    negocio_id, sede_id, area_mesa_id, numero_mesa, codigo_qr, 
    capacidad, estado, forma, posicion_x, posicion_y, 
    esta_activo, creado_en, actualizado_en
) VALUES
-- Terraza (area_id = 1)
(1, 1, 1, 'T01', 'QR-MESA-T01-SEDE1', 4, 'disponible', 'circular', 10, 10, 1, NOW(), NOW()),
(1, 1, 1, 'T02', 'QR-MESA-T02-SEDE1', 4, 'disponible', 'circular', 50, 10, 1, NOW(), NOW()),
(1, 1, 1, 'T03', 'QR-MESA-T03-SEDE1', 6, 'disponible', 'rectangular', 90, 10, 1, NOW(), NOW()),
-- Salón Principal (area_id = 2)
(1, 1, 2, 'S01', 'QR-MESA-S01-SEDE1', 4, 'ocupada', 'cuadrada', 10, 50, 1, NOW(), NOW()),
(1, 1, 2, 'S02', 'QR-MESA-S02-SEDE1', 4, 'disponible', 'cuadrada', 50, 50, 1, NOW(), NOW()),
(1, 1, 2, 'S03', 'QR-MESA-S03-SEDE1', 2, 'reservada', 'circular', 90, 50, 1, NOW(), NOW()),
-- Barra (area_id = 3)
(1, 1, 3, 'B01', 'QR-MESA-B01-SEDE1', 2, 'disponible', 'alta', 10, 90, 1, NOW(), NOW()),
(1, 1, 3, 'B02', 'QR-MESA-B02-SEDE1', 2, 'disponible', 'alta', 40, 90, 1, NOW(), NOW()),
-- Zona VIP (area_id = 4)
(1, 1, 4, 'VIP01', 'QR-MESA-VIP01-SEDE1', 10, 'disponible', 'rectangular', 150, 50, 1, NOW(), NOW()),
-- Sede 2 - Salón Principal (area_id = 5)
(1, 2, 5, 'S01', 'QR-MESA-S01-SEDE2', 4, 'disponible', 'cuadrada', 20, 20, 1, NOW(), NOW()),
(1, 2, 5, 'S02', 'QR-MESA-S02-SEDE2', 4, 'mantenimiento', 'cuadrada', 60, 20, 1, NOW(), NOW());

-- ============================================
-- 7. CONFIGURACIÓN DE NEGOCIO
-- ============================================
INSERT INTO configuracion_negocio (
    negocio_id, clave_configuracion, valor, tipo_valor, 
    descripcion, categoria, esta_activo, creado_en, actualizado_en
) VALUES
(1, 'MONEDA_BASE', 'PEN', 'texto', 'Moneda base del negocio', 'general', 1, NOW(), NOW()),
(1, 'ZONA_HORARIA', 'America/Lima', 'texto', 'Zona horaria del negocio', 'general', 1, NOW(), NOW()),
(1, 'TIEMPO_RESERVA_MINUTOS', '120', 'numero', 'Tiempo máximo de reserva de mesa en minutos', 'mesas', 1, NOW(), NOW()),
(1, 'PERMITE_RESERVAS_ONLINE', 'true', 'booleano', 'Permitir reservas de mesa online', 'mesas', 1, NOW(), NOW()),
(1, 'STOCK_MINIMO_ALERTA', '10', 'numero', 'Cantidad mínima para alertas de stock', 'inventario', 1, NOW(), NOW()),
(1, 'EMAIL_NOTIFICACIONES', 'admin@drinkgo.pe', 'texto', 'Email para notificaciones administrativas', 'notificaciones', 1, NOW(), NOW()),
(1, 'PUNTOS_POR_SOL', '10', 'numero', 'Puntos de fidelidad por cada sol gastado', 'fidelidad', 1, NOW(), NOW()),
(1, 'DESCUENTO_HAPPY_HOUR', '0.20', 'numero', 'Porcentaje de descuento en happy hour', 'promociones', 1, NOW(), NOW()),
(1, 'HORARIO_HAPPY_HOUR', '{"inicio": "17:00", "fin": "19:00"}', 'json', 'Horario de happy hour', 'promociones', 1, NOW(), NOW()),
(1, 'FECHA_APERTURA', '2025-01-15', 'fecha', 'Fecha de apertura del negocio', 'general', 1, NOW(), NOW());

-- ============================================
-- 8. PLANTILLAS DE NOTIFICACIÓN
-- ============================================
INSERT INTO plantillas_notificacion (
    negocio_id, codigo, nombre, canal, plantilla_asunto, plantilla_cuerpo, 
    variables_json, es_global, esta_activo, creado_en, actualizado_en
) VALUES
-- Plantillas globales (negocio_id = NULL)
(NULL, 'BIENVENIDA', 'Plantilla de Bienvenida', 'email', 
 'Bienvenido a DrinkGo, {{nombre}}!',
 'Hola {{nombre}},\n\nGracias por registrarte en DrinkGo. Tu cuenta ha sido creada exitosamente.\n\nSaludos,\nEquipo DrinkGo',
 '["nombre", "email"]', 1, 1, NOW(), NOW()),

(NULL, 'RESETEO_PASSWORD', 'Reseteo de Contraseña', 'email',
 'Solicitud de Reseteo de Contraseña',
 'Hola {{nombre}},\n\nHas solicitado resetear tu contraseña. Usa este código: {{codigo}}\n\nSi no fuiste tú, ignora este mensaje.',
 '["nombre", "codigo"]', 1, 1, NOW(), NOW()),

-- Plantillas del negocio
(1, 'PEDIDO_CONFIRMADO', 'Pedido Confirmado', 'sms',
 'Pedido #{{numero_pedido}} Confirmado',
 'Tu pedido #{{numero_pedido}} por S/{{total}} ha sido confirmado. Tiempo estimado: {{tiempo_estimado}} minutos.',
 '["numero_pedido", "total", "tiempo_estimado"]', 0, 1, NOW(), NOW()),

(1, 'PEDIDO_ENTREGADO', 'Pedido Entregado', 'push',
 'Pedido Entregado',
 'Tu pedido #{{numero_pedido}} ha sido entregado. ¡Disfrútalo!',
 '["numero_pedido"]', 0, 1, NOW(), NOW()),

(1, 'RESERVA_CONFIRMADA', 'Reserva de Mesa Confirmada', 'whatsapp',
 'Reserva Confirmada',
 'Hola {{nombre}}, tu reserva para la mesa {{numero_mesa}} el {{fecha}} a las {{hora}} ha sido confirmada.',
 '["nombre", "numero_mesa", "fecha", "hora"]', 0, 1, NOW(), NOW()),

(1, 'STOCK_BAJO', 'Alerta de Stock Bajo', 'en_app',
 'Alerta: Stock Bajo',
 'El producto {{nombre_producto}} tiene solo {{cantidad}} unidades en stock.',
 '["nombre_producto", "cantidad"]', 0, 1, NOW(), NOW());

-- ============================================
-- 9. NOTIFICACIONES
-- ============================================
INSERT INTO notificaciones (
    usuario_id, usuario_plataforma_id, plantilla_id, titulo, mensaje, canal,
    estado_entrega, prioridad, esta_leido, enviado_en, creado_en
) VALUES
(1, NULL, 1, 'Bienvenido a DrinkGo!', 'Tu cuenta ha sido creada exitosamente.', 'email', 'entregada', 'normal', 1, NOW(), NOW()),
(1, NULL, 3, 'Pedido #001 Confirmado', 'Tu pedido #001 por S/150.00 ha sido confirmado.', 'sms', 'entregada', 'alta', 1, NOW(), NOW()),
(2, NULL, 5, 'Reserva Confirmada', 'Tu reserva para la mesa T01 ha sido confirmada.', 'whatsapp', 'enviada', 'alta', 0, NOW(), NOW()),
(1, NULL, 6, 'Alerta: Stock Bajo', 'El producto Ron Diplomático tiene solo 5 unidades.', 'en_app', 'entregada', 'alta', 0, NOW(), NOW());

-- ============================================
-- 10. MÉTODOS DE PAGO
-- ============================================
INSERT INTO metodos_pago (
    negocio_id, codigo, nombre, tipo, descripcion, configuracion_json,
    disponible_pos, disponible_tienda_online, requiere_validacion,
    dias_procesamiento, comision_porcentaje, orden, esta_activo,
    creado_en, actualizado_en
) VALUES
(1, 'EFECTIVO', 'Efectivo', 'efectivo', 'Pago en efectivo', NULL, 
 1, 0, 0, 0, 0.00, 1, 1, NOW(), NOW()),

(1, 'VISA', 'Visa/Mastercard', 'tarjeta_credito', 'Tarjetas de crédito/débito', 
 '{"pasarela": "niubiz", "merchant_id": "12345"}',
 1, 1, 1, 0, 3.50, 2, 1, NOW(), NOW()),

(1, 'YAPE', 'Yape', 'billetera_digital', 'Pago mediante Yape BCP', 
 '{"numero": "987654321", "titular": "DrinkGo SAC"}',
 1, 1, 1, 0, 0.00, 3, 1, NOW(), NOW()),

(1, 'PLIN', 'Plin', 'billetera_digital', 'Pago mediante Plin', 
 '{"numero": "987654321", "banco": "BCP"}',
 1, 1, 1, 0, 0.00, 4, 1, NOW(), NOW()),

(1, 'TRANSFERENCIA', 'Transferencia Bancaria', 'transferencia_bancaria', 'Transferencia a cuenta BCP',
 '{"banco": "BCP", "cuenta": "191-1234567890", "cci": "00219100123456789012"}',
 0, 1, 1, 1, 0.00, 5, 1, NOW(), NOW()),

(1, 'AMEX', 'American Express', 'tarjeta_credito', 'Tarjeta American Express',
 '{"pasarela": "niubiz"}',
 1, 1, 1, 0, 4.00, 6, 1, NOW(), NOW()),

(1, 'TUNKI', 'Tunki', 'billetera_digital', 'Pago mediante Tunki',
 '{"numero": "987654321"}',
 1, 1, 1, 0, 0.00, 7, 1, NOW(), NOW());

-- ============================================
-- Verificar datos insertados
-- ============================================
SELECT '=== RESUMEN DE DATOS INSERTADOS ===' as '';
SELECT 'Usuarios-Sedes asignados:' as Tabla, COUNT(*) as Total FROM usuarios_sedes;
SELECT 'Horarios por sede:' as Tabla, COUNT(*) as Total FROM horarios_sede;
SELECT 'Horarios especiales:' as Tabla, COUNT(*) as Total FROM horarios_especiales_sede;
SELECT 'Restricciones alcohol:' as Tabla, COUNT(*) as Total FROM restricciones_venta_alcohol;
SELECT 'Áreas de mesas:' as Tabla, COUNT(*) as Total FROM areas_mesas;
SELECT 'Mesas configuradas:' as Tabla, COUNT(*) as Total FROM mesas;
SELECT 'Configuraciones:' as Tabla, COUNT(*) as Total FROM configuracion_negocio;
SELECT 'Plantillas notif:' as Tabla, COUNT(*) as Total FROM plantillas_notificacion;
SELECT 'Notificaciones:' as Tabla, COUNT(*) as Total FROM notificaciones;
SELECT 'Métodos de pago:' as Tabla, COUNT(*) as Total FROM metodos_pago;

-- Verificar mesas por estado
SELECT estado, COUNT(*) as cantidad FROM mesas GROUP BY estado;
