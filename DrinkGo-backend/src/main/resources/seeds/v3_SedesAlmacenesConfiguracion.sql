-- ============================================================
-- SEED v3: Sedes, Almacenes y Configuración
-- DrinkGo Platform – Bloque 3
-- Usar con ddl-auto=update (tablas creadas por Hibernate)
-- Contraseña para todos los usuarios: admin123
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 0. DATOS PREREQUISITO (negocio, plataforma, roles, usuarios)
-- ============================================================

-- 0-A  Negocio principal
INSERT INTO negocios (
    id, uuid, razon_social, nombre_comercial, ruc,
    tipo_documento_fiscal, representante_legal, documento_representante,
    tipo_negocio, email, telefono, direccion, ciudad, departamento,
    pais, moneda_predeterminada, idioma_predeterminado,
    zona_horaria, formato_fecha, estado, esta_activo,
    creado_en, actualizado_en
) VALUES (
    1, UUID(), 'DrinkGo Premium SAC', 'DrinkGo Premium', '20612345678',
    'RUC', 'Carlos García López', '45678901',
    'Licorería', 'admin@drinkgo.pe', '987654321',
    'Av. Javier Prado 1234, San Isidro', 'Lima', 'Lima',
    'PE', 'PEN', 'es',
    'America/Lima', 'DD/MM/YYYY', 'activo', 1,
    NOW(), NOW()
);

-- 0-A2  Segundo negocio (requerido por Bloque 14)
INSERT INTO negocios (
    id, uuid, razon_social, nombre_comercial, ruc,
    tipo_documento_fiscal, email, tipo_negocio,
    pais, moneda_predeterminada, idioma_predeterminado,
    zona_horaria, formato_fecha, estado, esta_activo,
    creado_en, actualizado_en
) VALUES (
    2, UUID(), 'Licorería Express EIRL', 'Licorería Express', '10456789012',
    'RUC', 'contacto@licoreriaexpress.pe', 'Licorería',
    'PE', 'PEN', 'es',
    'America/Lima', 'DD/MM/YYYY', 'activo', 1,
    NOW(), NOW()
);

-- 0-B  Super-administrador de plataforma
INSERT INTO usuarios_plataforma (
    id, uuid, email, hash_contrasena, nombres, apellidos,
    rol, esta_activo, creado_en, actualizado_en
) VALUES (
    1, UUID(), 'superadmin@drinkgo.pe',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'Super', 'Admin',
    'superadmin', 1, NOW(), NOW()
);

-- 0-C  Permisos base del sistema
INSERT INTO permiso (id, codigo, nombre, descripcion, activo, creado_en) VALUES
(1,  'GESTIONAR_SEDES',        'Gestionar Sedes',               'Crear, editar y eliminar sedes',                 1, NOW()),
(2,  'GESTIONAR_USUARIOS',     'Gestionar Usuarios',            'Administrar usuarios del negocio',                1, NOW()),
(3,  'GESTIONAR_INVENTARIO',   'Gestionar Inventario',          'Administrar productos y stock',                   1, NOW()),
(4,  'GESTIONAR_VENTAS',       'Gestionar Ventas',              'Registrar y administrar ventas',                  1, NOW()),
(5,  'GESTIONAR_CAJAS',        'Gestionar Cajas',               'Abrir/cerrar cajas y movimientos',                1, NOW()),
(6,  'GESTIONAR_CLIENTES',     'Gestionar Clientes',            'Administrar clientes del negocio',                1, NOW()),
(7,  'GESTIONAR_CONFIGURACION','Gestionar Configuración',       'Configurar parámetros del negocio',               1, NOW()),
(8,  'VER_REPORTES',           'Ver Reportes',                  'Acceso a reportes y estadísticas',                1, NOW()),
(9,  'GESTIONAR_TIENDA_ONLINE','Gestionar Tienda Online',       'Configurar y administrar la tienda online',       1, NOW()),
(10, 'GESTIONAR_DELIVERY',     'Gestionar Delivery',            'Administrar zonas y pedidos delivery',            1, NOW());

-- 0-D  Roles del negocio 1
INSERT INTO rol (id, tenant_id, codigo, nombre, descripcion, es_sistema, activo, creado_en, actualizado_en) VALUES
(1, 1, 'ADMIN',    'Administrador',  'Acceso total al negocio',                  1, 1, NOW(), NOW()),
(2, 1, 'CAJERO',   'Cajero',         'Gestión de ventas y caja registradora',    1, 1, NOW(), NOW()),
(3, 1, 'ALMACEN',  'Almacenero',     'Gestión de inventario y almacenes',        1, 1, NOW(), NOW());

-- 0-E  Asignar permisos a roles
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES
-- Admin: todos los permisos
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
-- Cajero: ventas, cajas, clientes
(2, 4), (2, 5), (2, 6),
-- Almacenero: inventario, reportes
(3, 3), (3, 8);

-- 0-F  Usuarios del negocio (password: admin123 → BCrypt)
INSERT INTO usuarios (
    id, uuid, negocio_id, email, hash_contrasena,
    nombres, apellidos, telefono, esta_activo,
    creado_en, actualizado_en
) VALUES
(1, UUID(), 1, 'admin@drinkgo.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Carlos', 'García López', '987654321', 1, NOW(), NOW()),
(2, UUID(), 1, 'cajero@drinkgo.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'María', 'Rodríguez Pérez', '987654322', 1, NOW(), NOW()),
(3, UUID(), 1, 'almacen@drinkgo.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Juan', 'López Díaz', '987654323', 1, NOW(), NOW());

-- 0-G  Asignar roles a usuarios
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(1, 1),  -- Carlos  → Admin
(2, 2),  -- María   → Cajero
(3, 3);  -- Juan    → Almacenero

-- ============================================================
-- 1. SEDES
-- ============================================================
INSERT INTO sede (
    id, tenant_id, codigo, nombre, direccion, distrito, ciudad,
    telefono, email, coordenadas_lat, coordenadas_lng,
    has_tables, has_delivery, has_pickup,
    capacidad_mesas, activo, creado_en, actualizado_en
) VALUES
(1, 1, 'SEDE-001', 'Sede Principal - San Isidro',
 'Av. Javier Prado 1234', 'San Isidro', 'Lima',
 '014567890', 'principal@drinkgo.pe', -12.0970920, -77.0365160,
 1, 1, 1, 20, 1, NOW(), NOW()),
(2, 1, 'SEDE-002', 'Sede Norte - Los Olivos',
 'Av. Carlos Izaguirre 567', 'Los Olivos', 'Lima',
 '015678901', 'norte@drinkgo.pe', -11.9631500, -77.0713800,
 1, 1, 1, 12, 1, NOW(), NOW());

-- ============================================================
-- 2. CONFIGURACIÓN DE SEDE (PK = sede_id, OneToOne)
-- ============================================================
INSERT INTO sede_config (
    sede_id, tenant_id,
    hora_apertura, hora_cierre,
    hora_inicio_venta_alcohol, hora_fin_venta_alcohol,
    ley_seca_activa,
    delivery_radio_km, delivery_costo_base,
    delivery_costo_por_km, delivery_pedido_minimo,
    actualizado_en
) VALUES
(1, 1, '08:00:00', '23:00:00', '10:00:00', '23:00:00',
 0, 10.00, 5.00, 1.50, 30.00, NOW()),
(2, 1, '09:00:00', '22:00:00', '10:00:00', '22:00:00',
 0, 8.00, 6.00, 2.00, 25.00, NOW());

-- ============================================================
-- 3. ALMACENES
-- ============================================================
INSERT INTO almacenes (
    id, negocio_id, sede_id, codigo, nombre,
    tipo_almacenamiento, temperatura_min, temperatura_max,
    descripcion_capacidad, es_predeterminado, esta_activo,
    creado_en, actualizado_en
) VALUES
(1, 1, 1, 'ALM-PRIN-001', 'Almacén Principal Sede 1',
 'ambiente', NULL, NULL, 'Almacén principal con capacidad de 500 cajas',
 1, 1, NOW(), NOW()),
(2, 1, 1, 'ALM-FRIO-001', 'Cámara Fría Sede 1',
 'frio', 2.00, 8.00, 'Cámara para cervezas y vinos blancos (200 cajas)',
 0, 1, NOW(), NOW()),
(3, 1, 2, 'ALM-PRIN-002', 'Almacén Principal Sede 2',
 'ambiente', NULL, NULL, 'Almacén principal sede norte (300 cajas)',
 1, 1, NOW(), NOW());

-- Vincular almacén principal a cada sede
UPDATE sede SET almacen_principal_id = 1 WHERE id = 1;
UPDATE sede SET almacen_principal_id = 3 WHERE id = 2;

-- ============================================================
-- 4. ASIGNACIÓN USUARIOS ↔ SEDES
-- ============================================================
INSERT INTO usuario_sede (usuario_id, sede_id) VALUES
(1, 1),  -- Carlos  → Sede Principal
(1, 2),  -- Carlos  → Sede Norte (acceso a ambas)
(2, 1),  -- María   → Sede Principal
(3, 2);  -- Juan    → Sede Norte

-- ============================================================
-- 5. HORARIOS SEMANALES POR SEDE (0=Dom … 6=Sáb)
-- ============================================================
INSERT INTO horarios_sede (
    sede_id, dia_semana, hora_apertura, hora_cierre,
    esta_cerrado, esta_activo, creado_en, actualizado_en
) VALUES
-- Sede 1
(1, 0, '10:00:00', '22:00:00', 0, 1, NOW(), NOW()),  -- Dom
(1, 1, '08:00:00', '23:00:00', 0, 1, NOW(), NOW()),  -- Lun
(1, 2, '08:00:00', '23:00:00', 0, 1, NOW(), NOW()),  -- Mar
(1, 3, '08:00:00', '23:00:00', 0, 1, NOW(), NOW()),  -- Mié
(1, 4, '08:00:00', '23:00:00', 0, 1, NOW(), NOW()),  -- Jue
(1, 5, '08:00:00', '01:00:00', 0, 1, NOW(), NOW()),  -- Vie
(1, 6, '08:00:00', '02:00:00', 0, 1, NOW(), NOW()),  -- Sáb
-- Sede 2
(2, 0, NULL,       NULL,       1, 1, NOW(), NOW()),  -- Dom cerrado
(2, 1, '09:00:00', '22:00:00', 0, 1, NOW(), NOW()),
(2, 2, '09:00:00', '22:00:00', 0, 1, NOW(), NOW()),
(2, 3, '09:00:00', '22:00:00', 0, 1, NOW(), NOW()),
(2, 4, '09:00:00', '22:00:00', 0, 1, NOW(), NOW()),
(2, 5, '09:00:00', '00:00:00', 0, 1, NOW(), NOW()),
(2, 6, '10:00:00', '01:00:00', 0, 1, NOW(), NOW());

-- ============================================================
-- 6. HORARIOS ESPECIALES (feriados)
-- ============================================================
INSERT INTO horarios_especiales_sede (
    sede_id, fecha, hora_apertura, hora_cierre,
    esta_cerrado, motivo, creado_en
) VALUES
(1, '2026-12-25', NULL,       NULL,       1, 'Cerrado por Navidad',                    NOW()),
(1, '2026-01-01', NULL,       NULL,       1, 'Cerrado por Año Nuevo',                  NOW()),
(1, '2026-07-28', '10:00:00', '18:00:00', 0, 'Fiestas Patrias - Horario Especial',     NOW()),
(1, '2026-12-24', '08:00:00', '20:00:00', 0, 'Nochebuena - Cierre Temprano',           NOW()),
(2, '2026-12-25', NULL,       NULL,       1, 'Cerrado por Navidad',                    NOW());

-- ============================================================
-- 7. RESTRICCIONES DE VENTA DE ALCOHOL
-- ============================================================
INSERT INTO restricciones_venta_alcohol (
    negocio_id, sede_id, tipo_restriccion,
    edad_minima, hora_permitida_desde, hora_permitida_hasta,
    dias_restringidos, descripcion, esta_activo,
    creado_en, actualizado_en
) VALUES
-- Restricción nacional: edad mínima 18 años
(1, NULL, 'verificacion_edad',
 18, NULL, NULL,
 NULL, 'Edad mínima obligatoria para venta de alcohol', 1,
 NOW(), NOW()),
-- Sede 1: restricción horaria entre semana
(1, 1, 'restriccion_horaria',
 18, '10:00:00', '23:00:00',
 '["1","2","3","4"]', 'Venta de alcohol solo de 10:00 a 23:00 L-J', 1,
 NOW(), NOW()),
-- Restricción de día: domingos restringido
(1, NULL, 'restriccion_dia',
 18, NULL, NULL,
 '["0"]', 'No se vende alcohol los domingos', 1,
 NOW(), NOW());

-- ============================================================
-- 8. ZONAS DE DELIVERY
-- ============================================================
INSERT INTO zona_delivery (
    id, tenant_id, sede_id, nombre,
    costo_delivery, tiempo_estimado_minutos, pedido_minimo,
    activo, creado_en
) VALUES
(1, 1, 1, 'Lima Moderna (San Isidro, Miraflores, San Borja)',
 5.00, 35, 30.00, 1, NOW()),
(2, 1, 1, 'Lima Sur (Surco, La Molina, Barranco)',
 8.00, 50, 40.00, 1, NOW()),
(3, 1, 2, 'Lima Norte (Los Olivos, Independencia, SMP)',
 6.00, 40, 25.00, 1, NOW());

-- ============================================================
-- 9. ÁREAS DE MESAS
-- ============================================================
INSERT INTO areas_mesas (
    id, negocio_id, sede_id, nombre, descripcion,
    orden, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'Terraza',          'Mesas al aire libre con vista a la calle', 1, 1, NOW(), NOW()),
(2, 1, 1, 'Salón Principal',  'Área interior principal',                  2, 1, NOW(), NOW()),
(3, 1, 1, 'Barra',            'Mesas altas junto a la barra',             3, 1, NOW(), NOW()),
(4, 1, 1, 'Zona VIP',         'Área privada para grupos',                 4, 1, NOW(), NOW()),
(5, 1, 2, 'Salón Principal',  'Área principal de la sede norte',          1, 1, NOW(), NOW()),
(6, 1, 2, 'Patio',            'Área al aire libre',                       2, 1, NOW(), NOW());

-- ============================================================
-- 10. MESAS
-- ============================================================
INSERT INTO mesas (
    negocio_id, sede_id, area_id, numero_mesa, etiqueta,
    capacidad, codigo_qr, estado, forma,
    posicion_x, posicion_y, esta_activo, creado_en, actualizado_en
) VALUES
-- Terraza (area_id = 1)
(1, 1, 1, 'T01', 'Terraza 1',  4, 'QR-S1-T01', 'disponible',   'redonda',      10, 10, 1, NOW(), NOW()),
(1, 1, 1, 'T02', 'Terraza 2',  4, 'QR-S1-T02', 'disponible',   'redonda',      50, 10, 1, NOW(), NOW()),
(1, 1, 1, 'T03', 'Terraza 3',  6, 'QR-S1-T03', 'disponible',   'rectangular',  90, 10, 1, NOW(), NOW()),
-- Salón Principal (area_id = 2)
(1, 1, 2, 'S01', 'Salón 1',    4, 'QR-S1-S01', 'ocupada',      'cuadrada',     10, 50, 1, NOW(), NOW()),
(1, 1, 2, 'S02', 'Salón 2',    4, 'QR-S1-S02', 'disponible',   'cuadrada',     50, 50, 1, NOW(), NOW()),
(1, 1, 2, 'S03', 'Salón 3',    2, 'QR-S1-S03', 'reservada',    'redonda',      90, 50, 1, NOW(), NOW()),
-- Barra (area_id = 3)
(1, 1, 3, 'B01', 'Barra 1',    2, 'QR-S1-B01', 'disponible',   'cuadrada',     10, 90, 1, NOW(), NOW()),
(1, 1, 3, 'B02', 'Barra 2',    2, 'QR-S1-B02', 'disponible',   'cuadrada',     40, 90, 1, NOW(), NOW()),
-- Zona VIP (area_id = 4)
(1, 1, 4, 'VIP01','VIP 1',    10, 'QR-S1-VIP01','disponible',  'rectangular', 150, 50, 1, NOW(), NOW()),
-- Sede 2 - Salón Principal (area_id = 5)
(1, 2, 5, 'S01', 'Norte S1',   4, 'QR-S2-S01', 'disponible',   'cuadrada',     20, 20, 1, NOW(), NOW()),
(1, 2, 5, 'S02', 'Norte S2',   4, 'QR-S2-S02', 'mantenimiento','cuadrada',     60, 20, 1, NOW(), NOW()),
-- Sede 2 - Patio (area_id = 6)
(1, 2, 6, 'P01', 'Patio 1',    6, 'QR-S2-P01', 'disponible',   'rectangular',  20, 60, 1, NOW(), NOW());

-- ============================================================
-- 11. CONFIGURACIÓN DEL NEGOCIO
-- ============================================================
INSERT INTO configuracion_negocio (
    negocio_id, clave_configuracion, valor_configuracion,
    tipo_valor, categoria, descripcion,
    creado_en, actualizado_en
) VALUES
(1, 'MONEDA_BASE',              'PEN',                                     'texto',    'general',        'Moneda base del negocio',                         NOW(), NOW()),
(1, 'ZONA_HORARIA',             'America/Lima',                            'texto',    'general',        'Zona horaria del negocio',                        NOW(), NOW()),
(1, 'TIEMPO_RESERVA_MINUTOS',   '120',                                    'numero',   'mesas',          'Tiempo máximo de reserva de mesa (minutos)',       NOW(), NOW()),
(1, 'PERMITE_RESERVAS_ONLINE',  'true',                                   'booleano', 'mesas',          'Permitir reservas de mesa en línea',               NOW(), NOW()),
(1, 'STOCK_MINIMO_ALERTA',      '10',                                     'numero',   'inventario',     'Cantidad mínima para alertas de stock',            NOW(), NOW()),
(1, 'EMAIL_NOTIFICACIONES',     'admin@drinkgo.pe',                        'texto',    'notificaciones', 'Email para notificaciones administrativas',        NOW(), NOW()),
(1, 'PUNTOS_POR_SOL',           '10',                                     'numero',   'fidelidad',      'Puntos de fidelidad por cada sol gastado',         NOW(), NOW()),
(1, 'DESCUENTO_HAPPY_HOUR',     '0.20',                                   'numero',   'promociones',    'Porcentaje de descuento en happy hour',            NOW(), NOW()),
(1, 'HORARIO_HAPPY_HOUR',       '{"inicio":"17:00","fin":"19:00"}',        'json',     'promociones',    'Horario de happy hour',                            NOW(), NOW()),
(1, 'FECHA_APERTURA',           '2025-01-15',                              'fecha',    'general',        'Fecha de apertura del negocio',                    NOW(), NOW());

-- ============================================================
-- 12. PLANTILLAS DE NOTIFICACIÓN
-- ============================================================
INSERT INTO plantillas_notificacion (
    negocio_id, codigo, nombre, canal, asunto,
    plantilla_cuerpo, variables_json, esta_activo,
    creado_en, actualizado_en
) VALUES
-- Plantillas globales (negocio_id = NULL)
(NULL, 'BIENVENIDA', 'Plantilla de Bienvenida', 'email',
 'Bienvenido a DrinkGo, {{nombre}}!',
 'Hola {{nombre}},\n\nGracias por registrarte en DrinkGo. Tu cuenta ha sido creada exitosamente.\n\nSaludos,\nEquipo DrinkGo',
 '["nombre","email"]', 1, NOW(), NOW()),

(NULL, 'RESETEO_PASSWORD', 'Reseteo de Contraseña', 'email',
 'Solicitud de Reseteo de Contraseña',
 'Hola {{nombre}},\n\nHas solicitado resetear tu contraseña. Usa este código: {{codigo}}\n\nSi no fuiste tú, ignora este mensaje.',
 '["nombre","codigo"]', 1, NOW(), NOW()),

-- Plantillas del negocio 1
(1, 'PEDIDO_CONFIRMADO', 'Pedido Confirmado', 'sms',
 'Pedido #{{numero_pedido}} Confirmado',
 'Tu pedido #{{numero_pedido}} por S/{{total}} ha sido confirmado. Tiempo estimado: {{tiempo_estimado}} minutos.',
 '["numero_pedido","total","tiempo_estimado"]', 1, NOW(), NOW()),

(1, 'PEDIDO_ENTREGADO', 'Pedido Entregado', 'push',
 'Pedido Entregado',
 'Tu pedido #{{numero_pedido}} ha sido entregado. ¡Disfrútalo!',
 '["numero_pedido"]', 1, NOW(), NOW()),

(1, 'RESERVA_CONFIRMADA', 'Reserva de Mesa Confirmada', 'whatsapp',
 'Reserva Confirmada',
 'Hola {{nombre}}, tu reserva para la mesa {{numero_mesa}} el {{fecha}} a las {{hora}} ha sido confirmada.',
 '["nombre","numero_mesa","fecha","hora"]', 1, NOW(), NOW()),

(1, 'STOCK_BAJO', 'Alerta de Stock Bajo', 'en_app',
 'Alerta: Stock Bajo',
 'El producto {{nombre_producto}} tiene solo {{cantidad}} unidades en stock.',
 '["nombre_producto","cantidad"]', 1, NOW(), NOW());

-- ============================================================
-- 13. NOTIFICACIONES DE EJEMPLO
-- ============================================================
INSERT INTO notificaciones (
    negocio_id, usuario_id, usuario_plataforma_id, plantilla_id,
    titulo, mensaje, canal, estado_entrega, prioridad,
    esta_leido, enviado_en, creado_en
) VALUES
(1, 1, NULL, 1, 'Bienvenido a DrinkGo!',
 'Tu cuenta ha sido creada exitosamente.',
 'email', 'entregada', 'normal', 1, NOW(), NOW()),
(1, 1, NULL, 3, 'Pedido #001 Confirmado',
 'Tu pedido #001 por S/150.00 ha sido confirmado.',
 'sms', 'entregada', 'alta', 1, NOW(), NOW()),
(1, 2, NULL, 5, 'Reserva Confirmada',
 'Tu reserva para la mesa T01 ha sido confirmada.',
 'whatsapp', 'enviada', 'alta', 0, NOW(), NOW()),
(1, 1, NULL, 6, 'Alerta: Stock Bajo',
 'El producto Ron Diplomático tiene solo 5 unidades.',
 'en_app', 'entregada', 'alta', 0, NOW(), NOW());

-- ============================================================
-- 14. MÉTODOS DE PAGO
-- ============================================================
INSERT INTO metodos_pago (
    id, negocio_id, nombre, codigo, tipo, proveedor,
    configuracion_json, requiere_referencia, requiere_aprobacion,
    esta_activo, disponible_pos, disponible_tienda_online,
    orden, creado_en, actualizado_en
) VALUES
(1, 1, 'Efectivo',              'EFECTIVO',       'efectivo',               NULL,
 NULL, 0, 0, 1, 1, 0, 1, NOW(), NOW()),
(2, 1, 'Visa / Mastercard',     'VISA',           'tarjeta_credito',        'Niubiz',
 '{"pasarela":"niubiz","merchant_id":"12345"}', 1, 1, 1, 1, 1, 2, NOW(), NOW()),
(3, 1, 'Yape',                  'YAPE',           'yape',                   'BCP',
 '{"numero":"987654321","titular":"DrinkGo SAC"}', 1, 0, 1, 1, 1, 3, NOW(), NOW()),
(4, 1, 'Plin',                  'PLIN',           'plin',                   'Interbank',
 '{"numero":"987654321","banco":"Interbank"}', 1, 0, 1, 1, 1, 4, NOW(), NOW()),
(5, 1, 'Transferencia Bancaria','TRANSFERENCIA',  'transferencia_bancaria', 'BCP',
 '{"banco":"BCP","cuenta":"191-1234567890","cci":"00219100123456789012"}', 1, 1, 1, 0, 1, 5, NOW(), NOW()),
(6, 1, 'American Express',      'AMEX',           'tarjeta_credito',        'Niubiz',
 '{"pasarela":"niubiz"}', 1, 1, 1, 1, 1, 6, NOW(), NOW()),
(7, 1, 'Tunki',                 'TUNKI',          'billetera_digital',      NULL,
 '{"numero":"987654321"}', 1, 0, 1, 1, 1, 7, NOW(), NOW());

-- ============================================================
-- FIN v3
-- ============================================================
