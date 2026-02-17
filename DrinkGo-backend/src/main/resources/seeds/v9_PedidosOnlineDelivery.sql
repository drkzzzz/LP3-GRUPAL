-- ============================================================
-- SEED v9: Pedidos (Online, Delivery, Recojo, Mesa)
-- DrinkGo Platform – Bloque 9
-- PREREQUISITOS: ejecutar v3 → v8 → v2 primero
--   (negocios 1-2 / sedes 1-2 / usuarios 1-8 / clientes 1-3
--    / productos 1-10 / metodos_pago 1-7)
-- NOTA: Las tablas de este bloque usan ENTITY column names
--   que difieren significativamente del DDL.
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 1. REPARTIDORES (necesario antes de pedidos con repartidor_id)
-- ============================================================
INSERT INTO repartidores (
    id, negocio_id, usuario_id,
    tipo_vehiculo, placa_vehiculo, numero_licencia,
    esta_disponible, esta_activo,
    latitud_actual, longitud_actual, ultima_ubicacion_en,
    creado_en, actualizado_en
) VALUES
(1, 1, 7,
 'motocicleta', 'ABC-123', 'Q12-34567890',
 1, 1,
 -12.0970920, -77.0365160, NOW(),
 NOW(), NOW()),
(2, 1, 8,
 'motocicleta', 'DEF-456', 'Q12-98765432',
 1, 1,
 -11.9631500, -77.0713800, NOW(),
 NOW(), NOW());

-- ============================================================
-- 2. PEDIDOS (entity table: pedido)
-- ============================================================
INSERT INTO pedido (
    id, tenant_id, sede_id, numero_pedido,
    cliente_id, cliente_nombre, cliente_telefono,
    modalidad, direccion_entrega, direccion_referencia,
    distrito_entrega, coordenadas_lat, coordenadas_lng,
    costo_delivery, hora_recojo, mesa_id, cuenta_mesa_id,
    fecha_creacion, fecha_entrega_estimada, fecha_entrega_real,
    estado, origen,
    pago_online_estado, pago_online_referencia, pago_online_metodo,
    subtotal, descuento_total, impuesto, total,
    preparado_por_id, repartidor_id,
    requiere_verificacion_edad, verificacion_realizada,
    observaciones, notas_preparacion,
    creado_en, actualizado_en
) VALUES
-- PED-001: Delivery entregado (pagado online con Yape)
(1, 1, 1, 'PED-20260201-001',
 1, 'Pedro Martínez Soto', '912345678',
 'delivery', 'Av. Conquistadores 1234, San Isidro', 'Frente al parque',
 'San Isidro', -12.0980000, -77.0380000,
 8.00, NULL, NULL, NULL,
 '2026-02-01 18:30:00', '2026-02-01 19:15:00', '2026-02-01 19:10:00',
 'entregado', 'tienda_online',
 'aprobado', 'YAPE-2026020118301234', 'yape',
 185.00, 0.00, 33.30, 226.30,
 3, 1,
 1, 1,
 'Tocar timbre 2 veces', NULL,
 '2026-02-01 18:30:00', '2026-02-01 19:10:00'),

-- PED-002: Recojo completado
(2, 1, 1, 'PED-20260202-001',
 2, 'Ana Flores Quispe', '923456789',
 'pickup', NULL, NULL,
 NULL, NULL, NULL,
 0.00, '19:00:00', NULL, NULL,
 '2026-02-02 17:45:00', '2026-02-02 19:00:00', '2026-02-02 18:55:00',
 'entregado', 'tienda_online',
 'aprobado', 'VISA-2026020217454567', 'tarjeta_credito',
 95.00, 10.00, 15.30, 100.30,
 NULL, NULL,
 1, 1,
 'Recojo en mostrador', NULL,
 '2026-02-02 17:45:00', '2026-02-02 18:55:00'),

-- PED-003: Delivery en preparación
(3, 1, 1, 'PED-20260215-001',
 1, 'Pedro Martínez Soto', '912345678',
 'delivery', 'Calle Las Flores 567, Miraflores', 'Al costado de la farmacia',
 'Miraflores', -12.1190000, -77.0320000,
 10.00, NULL, NULL, NULL,
 NOW(), DATE_ADD(NOW(), INTERVAL 45 MINUTE), NULL,
 'en_preparacion', 'tienda_online',
 'aprobado', 'PLIN-2026021512001111', 'plin',
 320.00, 0.00, 57.60, 387.60,
 3, NULL,
 1, 0,
 'Dejar en recepción del edificio', 'Empacar con cuidado las botellas',
 NOW(), NOW()),

-- PED-004: Pedido pendiente (recién creado)
(4, 1, 2, 'PED-20260215-002',
 3, NULL, '014567890',
 'delivery', 'Av. Universitaria 4567, Los Olivos', 'Cerca al metro',
 'Los Olivos', -11.9580000, -77.0730000,
 12.00, NULL, NULL, NULL,
 NOW(), DATE_ADD(NOW(), INTERVAL 60 MINUTE), NULL,
 'pendiente', 'tienda_online',
 'pendiente', NULL, 'transferencia',
 450.00, 20.00, 77.40, 519.40,
 NULL, NULL,
 1, 0,
 'Factura a nombre de Empresa ABC SAC', NULL,
 NOW(), NOW()),

-- PED-005: Pedido anulado
(5, 1, 1, 'PED-20260210-001',
 2, 'Ana Flores Quispe', '923456789',
 'delivery', 'Jr. Los Pinos 890, San Borja', NULL,
 'San Borja', -12.1050000, -77.0100000,
 8.00, NULL, NULL, NULL,
 '2026-02-10 20:00:00', '2026-02-10 20:45:00', NULL,
 'anulado', 'tienda_online',
 NULL, NULL, NULL,
 155.00, 0.00, 27.90, 190.90,
 NULL, NULL,
 1, 0,
 NULL, NULL,
 '2026-02-10 20:00:00', '2026-02-10 20:05:00'),

-- PED-006: Consumo en mesa (modalidad mesa) - listo
(6, 1, 1, 'PED-20260214-001',
 NULL, 'Cliente mesa 3', NULL,
 'mesa', NULL, NULL,
 NULL, NULL, NULL,
 0.00, NULL, 3, NULL,
 '2026-02-14 21:00:00', NULL, NULL,
 'listo', 'pos',
 NULL, NULL, NULL,
 210.00, 0.00, 37.80, 247.80,
 3, NULL,
 1, 1,
 NULL, 'Servir en copa',
 '2026-02-14 21:00:00', '2026-02-14 21:25:00'),

-- PED-007: Pedido por WhatsApp - entregado
(7, 1, 1, 'PED-20260212-001',
 1, 'Pedro Martínez Soto', '912345678',
 'delivery', 'Av. Javier Prado 2000, San Isidro', 'Edificio azul piso 5',
 'San Isidro', -12.0900000, -77.0400000,
 5.00, NULL, NULL, NULL,
 '2026-02-12 13:00:00', '2026-02-12 13:45:00', '2026-02-12 13:40:00',
 'entregado', 'whatsapp',
 NULL, NULL, NULL,
 34.00, 0.00, 6.12, 45.12,
 NULL, 2,
 1, 1,
 'Pago contra entrega en efectivo', NULL,
 '2026-02-12 13:00:00', '2026-02-12 13:40:00'),

-- PED-008: Barra - entregado
(8, 1, 1, 'PED-20260213-001',
 NULL, 'Cliente barra', NULL,
 'barra', NULL, NULL,
 NULL, NULL, NULL,
 0.00, NULL, NULL, NULL,
 '2026-02-13 22:30:00', NULL, '2026-02-13 22:35:00',
 'entregado', 'pos',
 NULL, NULL, NULL,
 55.00, 0.00, 9.90, 64.90,
 NULL, NULL,
 0, 0,
 NULL, NULL,
 '2026-02-13 22:30:00', '2026-02-13 22:35:00');

-- ============================================================
-- 3. ITEMS DE PEDIDO (entity table: pedido_item)
-- ============================================================
INSERT INTO pedido_item (
    id, pedido_id, producto_id, combo_id,
    codigo_producto, nombre_producto,
    cantidad, precio_unitario, descuento, subtotal,
    promocion_id, notas
) VALUES
-- PED-001: Ron Diplomático + Pisco
(1,  1, 1,  NULL, 'RON-DIP-12',    'Ron Diplomático Reserva Exclusiva 12 Años',
 1.000, 135.00, 0.00, 135.00, NULL, NULL),
(2,  1, 2,  NULL, 'PISCO-QBR-ACH', 'Pisco Quebranta Acholado 750ml',
 1.000, 50.00,  0.00, 50.00,  NULL, NULL),

-- PED-002: Chandon con descuento
(3,  2, 10, NULL, 'ESPUM-CHDN-750','Espumante Chandon Brut 750ml',
 1.000, 95.00,  10.00, 85.00, NULL, 'Para regalo, envolver'),

-- PED-003: JW Black + 2 Cusqueña + Vodka
(4,  3, 5,  NULL, 'WHSK-JWK-BLK',  'Johnnie Walker Black Label 750ml',
 1.000, 180.00, 0.00, 180.00, NULL, NULL),
(5,  3, 4,  NULL, 'BEER-CUS-330',  'Cerveza Cusqueña Dorada 330ml Six-Pack',
 2.000, 34.00,  0.00, 68.00,  NULL, NULL),
(6,  3, 6,  NULL, 'VODKA-ABS-750', 'Absolut Vodka Original 750ml',
 1.000, 72.00,  0.00, 72.00,  NULL, NULL),

-- PED-004: Pedido grande corporativo
(7,  4, 5,  NULL, 'WHSK-JWK-BLK',  'Johnnie Walker Black Label 750ml',
 2.000, 180.00, 10.00, 350.00, NULL, NULL),
(8,  4, 10, NULL, 'ESPUM-CHDN-750','Espumante Chandon Brut 750ml',
 1.000, 95.00,  10.00, 85.00,  NULL, 'Enfriar antes de entregar'),
(9,  4, 9,  NULL, 'TEQUILA-JC-SLV','José Cuervo Especial Silver 750ml',
 2.000, 72.00,  0.00,  144.00, NULL, NULL),

-- PED-005: Pedido anulado (tenía Ron)
(10, 5, 1,  NULL, 'RON-DIP-12',    'Ron Diplomático Reserva Exclusiva 12 Años',
 1.000, 155.00, 0.00, 155.00, NULL, NULL),

-- PED-006: Consumo en mesa
(11, 6, 5,  NULL, 'WHSK-JWK-BLK',  'Johnnie Walker Black Label 750ml',
 1.000, 180.00, 0.00, 180.00, NULL, 'Servir con hielo'),
(12, 6, 8,  NULL, 'GIN-TNQY-750',  'Tanqueray London Dry Gin 750ml',
 1.000, 100.00, 0.00, 100.00, NULL, 'Gin & Tonic'),

-- PED-007: Pedido WhatsApp simple
(13, 7, 4,  NULL, 'BEER-CUS-330',  'Cerveza Cusqueña Dorada 330ml Six-Pack',
 1.000, 34.00,  0.00, 34.00,  NULL, NULL),

-- PED-008: Consumo en barra
(14, 8, 6,  NULL, 'VODKA-ABS-750', 'Absolut Vodka Original 750ml',
 1.000, 55.00,  0.00, 55.00,  NULL, 'Con agua tónica');

-- ============================================================
-- 4. PAGOS DE PEDIDO (entity table: pago_pedido)
-- ============================================================
INSERT INTO pago_pedido (
    id, pedido_id, metodo_pago,
    monto, referencia, gateway, gateway_id, pagado_en
) VALUES
-- PED-001: pagado con Yape
(1, 1, 'yape',
 226.30, 'YAPE-2026020118301234', 'niubiz', 'NB-001-2026', '2026-02-01 18:32:00'),

-- PED-002: pagado con tarjeta de crédito
(2, 2, 'tarjeta_credito',
 100.30, 'VISA-2026020217454567', 'niubiz', 'NB-002-2026', '2026-02-02 17:47:00'),

-- PED-003: pagado con Plin
(3, 3, 'plin',
 387.60, 'PLIN-2026021512001111', 'niubiz', 'NB-003-2026', NOW()),

-- PED-006: pagado en efectivo (mesa)
(4, 6, 'efectivo',
 247.80, NULL, NULL, NULL, '2026-02-14 21:40:00'),

-- PED-007: pagado contra entrega efectivo
(5, 7, 'efectivo',
 45.12, NULL, NULL, NULL, '2026-02-12 13:40:00'),

-- PED-008: pagado con tarjeta de débito (barra)
(6, 8, 'tarjeta_debito',
 64.90, 'VISA-DEB-20260213', 'niubiz', 'NB-008-2026', '2026-02-13 22:35:00');

-- ============================================================
-- 5. SEGUIMIENTO DE PEDIDOS (entity table: seguimiento_pedido)
-- ============================================================
INSERT INTO seguimiento_pedido (
    id, pedido_id, estado_anterior, estado_nuevo,
    mensaje, ubicacion_lat, ubicacion_lng, usuario_id, creado_en
) VALUES
-- PED-001: flujo completo delivery
(1,  1, NULL,              'pendiente',       'Pedido recibido',                   NULL, NULL, NULL, '2026-02-01 18:30:00'),
(2,  1, 'pendiente',       'en_preparacion',  'Pedido confirmado, preparando',     NULL, NULL, 1,    '2026-02-01 18:35:00'),
(3,  1, 'en_preparacion',  'listo',           'Pedido listo para despacho',        NULL, NULL, 3,    '2026-02-01 18:55:00'),
(4,  1, 'listo',           'entregado',       'Entregado al cliente',              -12.0980000, -77.0380000, 7, '2026-02-01 19:10:00'),

-- PED-002: flujo recojo
(5,  2, NULL,              'pendiente',       'Pedido recibido',                   NULL, NULL, NULL, '2026-02-02 17:45:00'),
(6,  2, 'pendiente',       'en_preparacion',  'Preparando pedido',                 NULL, NULL, 1,    '2026-02-02 17:50:00'),
(7,  2, 'en_preparacion',  'listo',           'Listo para recoger en tienda',      NULL, NULL, 3,    '2026-02-02 18:30:00'),
(8,  2, 'listo',           'entregado',       'Recogido por el cliente',           NULL, NULL, 2,    '2026-02-02 18:55:00'),

-- PED-003: en preparación actualmente
(9,  3, NULL,              'pendiente',       'Pedido recibido',                   NULL, NULL, NULL, NOW()),
(10, 3, 'pendiente',       'en_preparacion',  'Iniciando preparación',             NULL, NULL, 1,    NOW()),

-- PED-005: pedido anulado
(11, 5, NULL,              'pendiente',       'Pedido recibido',                   NULL, NULL, NULL, '2026-02-10 20:00:00'),
(12, 5, 'pendiente',       'anulado',         'Anulado por el cliente - sin stock', NULL, NULL, 1, '2026-02-10 20:05:00'),

-- PED-006: mesa
(13, 6, NULL,              'pendiente',       'Pedido de mesa registrado',         NULL, NULL, 2,    '2026-02-14 21:00:00'),
(14, 6, 'pendiente',       'en_preparacion',  'Preparando en barra',              NULL, NULL, 1,    '2026-02-14 21:05:00'),
(15, 6, 'en_preparacion',  'listo',           'Listo para servir en mesa',        NULL, NULL, 3,    '2026-02-14 21:25:00'),

-- PED-007: delivery WhatsApp
(16, 7, NULL,              'pendiente',       'Pedido vía WhatsApp',              NULL, NULL, 1,    '2026-02-12 13:00:00'),
(17, 7, 'pendiente',       'listo',           'Listo para despacho',              NULL, NULL, 1,    '2026-02-12 13:20:00'),
(18, 7, 'listo',           'entregado',       'Entregado al cliente',             -12.0900000, -77.0400000, 8, '2026-02-12 13:40:00');

-- ============================================================
-- 6. ASIGNACIONES DE DELIVERY
-- ============================================================
INSERT INTO asignaciones_delivery_pedido (
    id, pedido_id, repartidor_id,
    estado, asignado_en, aceptado_en, recogido_en, entregado_en, notas
) VALUES
-- PED-001: Diego entregó
(1, 1, 1,
 'entregado', '2026-02-01 18:55:00', '2026-02-01 18:56:00',
 '2026-02-01 18:58:00', '2026-02-01 19:10:00', 'Entrega sin novedad'),
-- PED-003: Diego asignado (en curso)
(2, 3, 1,
 'asignado', NOW(), NULL, NULL, NULL, 'Pendiente de preparación'),
-- PED-004: Andrés asignado (pendiente)
(3, 4, 2,
 'asignado', NOW(), NULL, NULL, NULL, 'Esperando confirmación de pago'),
-- PED-007: Andrés entregó
(4, 7, 2,
 'entregado', '2026-02-12 13:20:00', '2026-02-12 13:22:00',
 '2026-02-12 13:25:00', '2026-02-12 13:40:00', NULL);

-- ============================================================
-- 7. CALIFICACIONES DE PEDIDOS
-- ============================================================
INSERT INTO calificacion_pedido (
    id, pedido_id, estrellas, comentario,
    puntualidad, calidad_producto, atencion, creado_en
) VALUES
(1, 1, 5,
 '¡Excelente servicio! Llegó muy rápido y todo en perfecto estado.',
 5, 5, 5, '2026-02-01 19:30:00'),
(2, 2, 4,
 'Buen producto, aunque tardaron un poco más de lo esperado.',
 3, 5, 4, '2026-02-02 19:15:00'),
(3, 7, 4,
 'Todo bien, el repartidor fue amable.',
 4, 4, 5, '2026-02-12 14:00:00');

-- ============================================================
-- 8. ADJUNTOS DE PEDIDOS
-- ============================================================
INSERT INTO pedido_adjunto (
    id, pedido_id, tipo, nombre_archivo,
    ruta_archivo, tipo_mime, tamano_bytes,
    subido_por_id, subido_en
) VALUES
(1, 1, 'comprobante_pago', 'yape_comprobante_ped001.jpg',
 '/uploads/pedidos/1/yape_comprobante_ped001.jpg',
 'image/jpeg', 245760, NULL, '2026-02-01 18:32:00'),
(2, 7, 'foto_entrega', 'entrega_ped007.jpg',
 '/uploads/pedidos/7/entrega_ped007.jpg',
 'image/jpeg', 512000, 8, '2026-02-12 13:40:00'),
(3, 4, 'documento_cliente', 'ruc_empresa_abc.pdf',
 '/uploads/pedidos/4/ruc_empresa_abc.pdf',
 'application/pdf', 102400, NULL, NOW());

-- ============================================================
-- Actualizar estadísticas de clientes
-- ============================================================
UPDATE clientes SET
    total_compras = total_compras + 226.30 + 45.12,
    total_pedidos = total_pedidos + 3
WHERE id = 1;

UPDATE clientes SET
    total_compras = total_compras + 100.30,
    total_pedidos = total_pedidos + 2
WHERE id = 2;

-- ============================================================
-- FIN v9
-- ============================================================