-- ============================================================
-- SEED v8: Ventas, POS y Cajas
-- DrinkGo Platform – Bloque 8
-- PREREQUISITOS: ejecutar v3 primero (negocios, usuarios, sedes, métodos pago)
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 0. PREREQUISITOS: Clientes y Productos
-- ============================================================

-- 0-A  Clientes del negocio 1
INSERT INTO clientes (
    id, negocio_id, uuid, tipo_cliente, nombres, apellidos,
    tipo_documento, numero_documento, email, telefono,
    fecha_nacimiento, genero, total_compras, total_pedidos,
    acepta_marketing, esta_activo
) VALUES
(1, 1, UUID(), 'individual', 'Pedro', 'Martínez Soto',
 'DNI', '45678901', 'pedro.martinez@gmail.com', '912345678',
 '1990-05-15', 'M', 0.00, 0, 1, 1),
(2, 1, UUID(), 'individual', 'Ana', 'Flores Quispe',
 'DNI', '46789012', 'ana.flores@gmail.com', '923456789',
 '1988-11-20', 'F', 0.00, 0, 1, 1),
(3, 1, UUID(), 'empresa', NULL, NULL,
 'RUC', '20501234567', 'compras@empresaabc.pe', '014567890',
 NULL, 'NO_ESPECIFICADO', 0.00, 0, 0, 1);
-- Actualizar razon_social separadamente (columna puede ser especial)
UPDATE clientes SET razon_social = 'Empresa ABC SAC' WHERE id = 3;

-- 0-B  Productos del negocio 1 (IDs 1-10)
INSERT INTO productos (
    id, negocio_id, sku, nombre,
    stock_minimo, stock_maximo, punto_reorden,
    es_perecible, dias_vida_util, precio_compra, esta_activo
) VALUES
(1,  1, 'RON-DIP-12',    'Ron Diplomático Reserva Exclusiva 12 Años',    5,  50, 10, 0, NULL, 85.00,  1),
(2,  1, 'PISCO-QBR-ACH', 'Pisco Quebranta Acholado 750ml',               5,  60, 12, 0, NULL, 45.00,  1),
(3,  1, 'WINE-CSA-RES',  'Vino Casillero del Diablo Reserva Cab. Sauv.', 10, 80, 20, 0, NULL, 32.00,  1),
(4,  1, 'BEER-CUS-330',  'Cerveza Cusqueña Dorada 330ml Six-Pack',       20,100, 30, 1, 180,  18.00,  1),
(5,  1, 'WHSK-JWK-BLK',  'Johnnie Walker Black Label 750ml',             3,  30,  8, 0, NULL,120.00,  1),
(6,  1, 'VODKA-ABS-750', 'Absolut Vodka Original 750ml',                 5,  40, 10, 0, NULL, 55.00,  1),
(7,  1, 'BEER-PIL-620',  'Cerveza Pilsen Callao 620ml',                  30,200, 50, 1, 120,   5.50,  1),
(8,  1, 'GIN-TNQY-750',  'Tanqueray London Dry Gin 750ml',               3,  25,  6, 0, NULL, 78.00,  1),
(9,  1, 'TEQUILA-JC-SLV','José Cuervo Especial Silver 750ml',            3,  30,  8, 0, NULL, 62.00,  1),
(10, 1, 'ESPUM-CHDN-750','Espumante Chandon Brut 750ml',                 5,  30, 10, 0, NULL, 48.00,  1);

-- ============================================================
-- 1. CAJAS REGISTRADORAS
-- ============================================================
INSERT INTO cajas_registradoras (
    id, negocio_id, sede_id, nombre_caja, codigo_caja,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'Caja Principal - Mostrador', 'CAJA-001', 1, NOW(), NOW()),
(2, 1, 1, 'Caja Secundaria - Express',  'CAJA-002', 1, NOW(), NOW()),
(3, 1, 1, 'Caja VIP - Clientes Premium','CAJA-003', 1, NOW(), NOW()),
(4, 1, 2, 'Caja Norte Principal',       'CAJA-N01', 1, NOW(), NOW()),
(5, 1, 2, 'Caja Norte Secundaria',      'CAJA-N02', 1, NOW(), NOW()),
(6, 1, 1, 'Caja Antigua (Desactivada)', 'CAJA-OLD', 0, NOW(), NOW());

-- ============================================================
-- 2. SESIONES DE CAJA
-- ============================================================

-- Sesión 1: ABIERTA ahora en CAJA-001
INSERT INTO sesiones_caja (
    id, negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    observaciones_apertura, observaciones_cierre,
    esta_abierta, abierta_en, cerrada_en,
    creado_en, actualizado_en
) VALUES (
    1, 1, 1, 1, NULL,
    200.00, NULL, NULL, NULL,
    'Apertura turno mañana - Fondo fijo 200 soles', NULL,
    1, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL,
    NOW(), NOW()
);

-- Sesión 2: CERRADA ayer en CAJA-001 (sobrante)
INSERT INTO sesiones_caja (
    id, negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    observaciones_apertura, observaciones_cierre,
    esta_abierta, abierta_en, cerrada_en,
    creado_en, actualizado_en
) VALUES (
    2, 1, 1, 1, 1,
    200.00, 3450.50, 3420.00, 30.50,
    'Apertura turno completo',
    'Arqueo con sobrante de 30.50 - Se registró en libro de caja',
    0, DATE_SUB(NOW(), INTERVAL 1 DAY),
       DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 10 HOUR,
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)
);

-- Sesión 3: CERRADA anteayer en CAJA-002 (arqueo perfecto)
INSERT INTO sesiones_caja (
    id, negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    observaciones_apertura, observaciones_cierre,
    esta_abierta, abierta_en, cerrada_en,
    creado_en, actualizado_en
) VALUES (
    3, 1, 2, 2, 2,
    150.00, 2100.00, 2100.00, 0.00,
    'Apertura turno express',
    'Arqueo perfecto - Sin diferencias',
    0, DATE_SUB(NOW(), INTERVAL 2 DAY),
       DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 8 HOUR,
    DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)
);

-- Sesión 4: ABIERTA en CAJA-002
INSERT INTO sesiones_caja (
    id, negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    observaciones_apertura, observaciones_cierre,
    esta_abierta, abierta_en, cerrada_en,
    creado_en, actualizado_en
) VALUES (
    4, 1, 2, 2, NULL,
    150.00, NULL, NULL, NULL,
    'Turno tarde - Express', NULL,
    1, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL,
    NOW(), NOW()
);

-- Sesión 5: CERRADA con faltante en CAJA-003
INSERT INTO sesiones_caja (
    id, negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    observaciones_apertura, observaciones_cierre,
    esta_abierta, abierta_en, cerrada_en,
    creado_en, actualizado_en
) VALUES (
    5, 1, 3, 3, 3,
    300.00, 1870.00, 1920.00, -50.00,
    'VIP - Alto movimiento esperado',
    'Faltante de 50.00 - Se solicitó revisión de grabaciones',
    0, DATE_SUB(NOW(), INTERVAL 3 DAY),
       DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 9 HOUR,
    DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)
);

-- ============================================================
-- 3. MOVIMIENTOS DE CAJA
-- ============================================================

-- Movimientos de la Sesión 1 (ABIERTA)
INSERT INTO movimientos_caja (
    negocio_id, sesion_id, tipo_movimiento, monto, concepto,
    realizado_por_usuario_id, creado_en, actualizado_en
) VALUES
(1, 1, 'INGRESO', 50.00,  'Ajuste de fondo inicial autorizado por gerencia',           1, DATE_SUB(NOW(), INTERVAL 110 MINUTE), NOW()),
(1, 1, 'EGRESO',  35.00,  'Compra de bolsas plásticas y materiales de empaque',        1, DATE_SUB(NOW(), INTERVAL 90 MINUTE),  NOW()),
(1, 1, 'EGRESO', 120.00,  'Pago a proveedor de hielo - Deuda pendiente',               1, DATE_SUB(NOW(), INTERVAL 60 MINUTE),  NOW()),
(1, 1, 'INGRESO',100.00,  'Recarga de fondo para vueltos',                             1, DATE_SUB(NOW(), INTERVAL 30 MINUTE),  NOW()),
(1, 1, 'AJUSTE',  10.00,  'Ajuste por billete falso detectado',                        1, DATE_SUB(NOW(), INTERVAL 15 MINUTE),  NOW());

-- Movimientos de la Sesión 2 (CERRADA ayer)
INSERT INTO movimientos_caja (
    negocio_id, sesion_id, tipo_movimiento, monto, concepto,
    realizado_por_usuario_id, creado_en, actualizado_en
) VALUES
(1, 2, 'INGRESO', 200.00, 'Recarga de fondo para cambio',      1, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 2, 'EGRESO',   80.00, 'Pago de delivery externo',           1, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 2, 'EGRESO',   25.00, 'Propina repartidor pool',            1, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 7 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Movimiento de la Sesión 4 (ABIERTA CAJA-002)
INSERT INTO movimientos_caja (
    negocio_id, sesion_id, tipo_movimiento, monto, concepto,
    realizado_por_usuario_id, creado_en, actualizado_en
) VALUES
(1, 4, 'EGRESO', 20.00, 'Compra de cambio en banco', 2, DATE_SUB(NOW(), INTERVAL 45 MINUTE), NOW());

-- ============================================================
-- 4. VENTAS
-- ============================================================

-- Venta 1: LOCAL - Efectivo - Sesión 1 (CONFIRMADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, observaciones, creado_en, actualizado_en
) VALUES (
    1, 1, 1, 'VEN-001-001', 1,
    1, 1, 'LOCAL', 'POS',
    150.00, 0.00, 0.00, 0.00, 0.00, 150.00,
    'CONFIRMADA', 'Cliente frecuente - Pago en efectivo',
    DATE_SUB(NOW(), INTERVAL 90 MINUTE), NOW()
);

-- Venta 2: LOCAL - Pago Mixto - Sesión 1 (CONFIRMADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, observaciones, creado_en, actualizado_en
) VALUES (
    2, 1, 1, 'VEN-001-002', 1,
    2, 1, 'LOCAL', 'POS',
    320.00, 20.00, 0.00, 0.00, 0.00, 300.00,
    'CONFIRMADA', 'Descuento por cantidad - Pago mixto',
    DATE_SUB(NOW(), INTERVAL 60 MINUTE), NOW()
);

-- Venta 3: DELIVERY - Yape - Sesión 1 (CONFIRMADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, direccion_entrega, telefono_entrega, observaciones,
    creado_en, actualizado_en
) VALUES (
    3, 1, 1, 'VEN-001-003', 1,
    3, 1, 'DELIVERY', 'WEB',
    180.00, 0.00, 0.00, 12.00, 8.00, 200.00,
    'CONFIRMADA', 'Av. Los Alamos 456, Surco', '912345678',
    'Entregar antes de las 20:00 - Llamar al llegar',
    DATE_SUB(NOW(), INTERVAL 45 MINUTE), NOW()
);

-- Venta 4: LOCAL - Tarjeta - Sesión 4 (CONFIRMADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, observaciones, creado_en, actualizado_en
) VALUES (
    4, 1, 1, 'VEN-001-004', 4,
    1, 2, 'LOCAL', 'POS',
    450.00, 50.00, 0.00, 0.00, 0.00, 400.00,
    'CONFIRMADA', 'Compra al por mayor - Descuento especial',
    DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()
);

-- Venta 5: LOCAL - Efectivo - Sesión 2 ayer (CONFIRMADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, observaciones, creado_en, actualizado_en
) VALUES (
    5, 1, 1, 'VEN-001-005', 2,
    2, 1, 'LOCAL', 'POS',
    850.00, 0.00, 0.00, 0.00, 0.00, 850.00,
    'CONFIRMADA', 'Evento corporativo - Venta grande',
    DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 3 HOUR,
    DATE_SUB(NOW(), INTERVAL 1 DAY)
);

-- Venta 6: DELIVERY - Sesión 2 ayer (ANULADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, direccion_entrega, observaciones,
    creado_en, actualizado_en
) VALUES (
    6, 1, 1, 'VEN-001-006', 2,
    3, 1, 'DELIVERY', 'WEB',
    120.00, 0.00, 0.00, 10.00, 0.00, 130.00,
    'ANULADA', 'Jr. Las Flores 789, San Isidro',
    'ANULADA: Cliente no atendió llamadas - Pedido cancelado',
    DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 HOUR,
    DATE_SUB(NOW(), INTERVAL 1 DAY)
);

-- Venta 7: PICK_UP - Sesión 3 anteayer (COMPLETADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, observaciones, creado_en, actualizado_en
) VALUES (
    7, 1, 1, 'VEN-001-007', 3,
    1, 2, 'PICK_UP', 'WEB',
    200.00, 10.00, 0.00, 0.00, 0.00, 190.00,
    'COMPLETADA', 'Pedido web - Recojo en tienda - Descuento online',
    DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 4 HOUR,
    DATE_SUB(NOW(), INTERVAL 2 DAY)
);

-- Venta 8: MARKETPLACE - Sesión 3 (COMPLETADA)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, observaciones, creado_en, actualizado_en
) VALUES (
    8, 1, 1, 'VEN-001-008', 3,
    2, 2, 'MARKETPLACE', 'APP',
    95.00, 0.00, 0.00, 0.00, 0.00, 95.00,
    'COMPLETADA', 'Pedido desde app Rappi - Comisión 15%',
    DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 6 HOUR,
    DATE_SUB(NOW(), INTERVAL 2 DAY)
);

-- Venta 9: LOCAL - Pendiente de pago (PENDIENTE)
INSERT INTO ventas (
    id, negocio_id, sede_id, codigo_venta, sesion_id,
    cliente_id, vendedor_id, tipo_venta, canal_venta,
    subtotal, descuento_monto, impuesto_monto, costo_delivery, propina, total,
    estado, observaciones, creado_en, actualizado_en
) VALUES (
    9, 1, 1, 'VEN-001-009', 1,
    1, 1, 'LOCAL', 'POS',
    280.00, 0.00, 0.00, 0.00, 0.00, 280.00,
    'PENDIENTE', 'Venta en proceso - Esperando confirmación de pago',
    DATE_SUB(NOW(), INTERVAL 10 MINUTE), NOW()
);

-- ============================================================
-- 5. DETALLE DE VENTAS
-- ============================================================

-- Venta 1 (150.00)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 1, 1, NULL, 1, 85.00,  0.00, 0.00, 85.00,  NOW(), NOW()),   -- Ron Diplomático
(1, 1, 7, NULL, 5,  5.50,  0.00, 0.00, 27.50,  NOW(), NOW()),   -- 5x Pilsen
(1, 1, 3, NULL, 1, 32.00,  0.00, 0.00, 32.00,  NOW(), NOW()),   -- Vino Casillero
(1, 1, 4, NULL, 1, 18.00, 12.50, 0.00,  5.50,  NOW(), NOW());   -- Cusqueña 6-pack (desc ajuste)

-- Venta 2 (subtotal 320, descuento gral 20 → 300)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 2, 5, NULL, 2, 120.00, 0.00, 0.00, 240.00, NOW(), NOW()),   -- 2x JW Black
(1, 2, 6, NULL, 1,  55.00, 0.00, 0.00,  55.00, NOW(), NOW()),   -- Absolut
(1, 2, 9, NULL, 1,  62.00, 37.00,0.00,  25.00, NOW(), NOW());   -- Tequila JC (desc)

-- Venta 3 DELIVERY (subtotal 180 + delivery 12 + propina 8 = 200)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 3, 2, NULL, 2, 45.00, 0.00, 0.00, 90.00, NOW(), NOW()),     -- 2x Pisco
(1, 3, 8, NULL, 1, 78.00, 0.00, 0.00, 78.00, NOW(), NOW()),     -- Tanqueray
(1, 3, 7, NULL, 2,  5.50, 0.00, 0.00, 12.00, NOW(), NOW());     -- 2x Pilsen (ajuste)

-- Venta 4 (subtotal 450, descuento 50 → 400)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 4, 5, NULL, 3, 120.00, 0.00, 0.00, 360.00, NOW(), NOW()),   -- 3x JW Black
(1, 4, 10,NULL, 2,  48.00, 3.00, 0.00,  90.00, NOW(), NOW());   -- 2x Chandon (desc)

-- Venta 5 (850)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 5, 1,  NULL, 3, 85.00, 0.00, 0.00, 255.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 5, 5,  NULL, 2,120.00, 0.00, 0.00, 240.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 5, 10, NULL, 5, 48.00, 0.00, 0.00, 240.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 5, 2,  NULL, 2, 45.00, 0.00, 0.00,  90.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 5, 4,  NULL, 1, 18.00, 0.00, 0.00,  18.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 5, 7,  NULL, 1,  5.50, 0.00, 0.00,   7.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Venta 6 (ANULADA - 120)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 6, 8, NULL, 1, 78.00, 0.00, 0.00, 78.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 6, 9, NULL, 1, 62.00, 20.00,0.00, 42.00, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Venta 7 (PICK_UP - subtotal 200, desc 10 → 190)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 7, 6, NULL, 2, 55.00, 0.00, 0.00, 110.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 7, 3, NULL, 2, 32.00, 0.00, 0.00,  64.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 7, 4, NULL, 1, 18.00, 0.00, 0.00,  18.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 7, 7, NULL, 1,  5.50, 0.00, 0.00,   8.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Venta 8 (MARKETPLACE - 95)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 8, 2, NULL, 1, 45.00, 0.00, 0.00, 45.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 8, 4, NULL, 2, 18.00, 0.00, 0.00, 36.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 8, 7, NULL, 2,  5.50, 0.00, 0.00, 14.00, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Venta 9 (PENDIENTE - 280)
INSERT INTO detalle_ventas (
    negocio_id, venta_id, producto_id, lote_id,
    cantidad, precio_unitario, descuento_por_item, impuesto_por_item,
    subtotal_item, creado_en, actualizado_en
) VALUES
(1, 9, 5, NULL, 2, 120.00, 0.00, 0.00, 240.00, NOW(), NOW()),
(1, 9, 7, NULL, 4,   5.50, 0.00, 0.00,  22.00, NOW(), NOW()),
(1, 9, 4, NULL, 1,  18.00, 0.00, 0.00,  18.00, NOW(), NOW());

-- ============================================================
-- 6. PAGOS DE VENTAS
-- ============================================================

-- Venta 1: Efectivo 150
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 1, 1, 150.00, 'PEN', NULL, 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 90 MINUTE), NOW());

-- Venta 2: Pago Mixto (Efectivo 150 + Tarjeta 150 = 300)
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 2, 1, 150.00, 'PEN', NULL,              'COMPLETADO', DATE_SUB(NOW(), INTERVAL 60 MINUTE), NOW()),
(1, 2, 2, 150.00, 'PEN', 'VISA-****4521',   'COMPLETADO', DATE_SUB(NOW(), INTERVAL 60 MINUTE), NOW());

-- Venta 3: Yape 200
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 3, 3, 200.00, 'PEN', 'YAPE-OP-789456123', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 45 MINUTE), NOW());

-- Venta 4: Tarjeta 400
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 4, 2, 400.00, 'PEN', 'MASTER-****8765', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW());

-- Venta 5: Efectivo 850 (ayer)
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 5, 1, 850.00, 'PEN', NULL, 'COMPLETADO',
 DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 3 HOUR,
 DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Venta 6: Transferencia 130 (REEMBOLSADO - venta anulada)
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 6, 5, 130.00, 'PEN', 'TRANS-987654', 'REEMBOLSADO',
 DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 HOUR,
 DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Venta 7: Efectivo 190
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 7, 1, 190.00, 'PEN', NULL, 'COMPLETADO',
 DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 4 HOUR,
 DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Venta 8: Tarjeta 95 (marketplace)
INSERT INTO pagos_venta (
    negocio_id, venta_id, metodo_pago_id, monto, moneda,
    referencia_pago, estado, creado_en, actualizado_en
) VALUES
(1, 8, 2, 95.00, 'PEN', 'RAPPI-PAY-001', 'COMPLETADO',
 DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 6 HOUR,
 DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Venta 9: PENDIENTE, sin pago aún

-- ============================================================
-- 7. ACTUALIZAR ESTADÍSTICAS DE CLIENTES
-- ============================================================
UPDATE clientes SET
    total_compras = (SELECT COALESCE(SUM(v.total),0) FROM ventas v WHERE v.cliente_id = clientes.id AND v.estado NOT IN ('ANULADA','CANCELADA')),
    total_pedidos = (SELECT COUNT(*) FROM ventas v WHERE v.cliente_id = clientes.id AND v.estado NOT IN ('ANULADA','CANCELADA')),
    ultima_compra_en = (SELECT MAX(v.creado_en) FROM ventas v WHERE v.cliente_id = clientes.id AND v.estado NOT IN ('ANULADA','CANCELADA'))
WHERE negocio_id = 1;

-- ============================================================
-- FIN v8
-- ============================================================
