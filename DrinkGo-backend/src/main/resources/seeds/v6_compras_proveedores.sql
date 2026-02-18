-- ============================================================
-- DATOS DE PRUEBA - BLOQUE 6: PROVEEDORES Y COMPRAS
-- Sistema DrinkGo - Multi-tenant
-- ============================================================
-- IMPORTANTE: Ejecutar DESPUÉS de tener datos de:
--   - negocios (negocio_id = 1)
--   - usuarios (usuario_id = 1, 2, 3)
--   - sedes (sede_id = 1, 2)
--   - almacenes (almacen_id = 1, 2)
--   - productos (producto_id = 1..20+)
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 6.1 PROVEEDORES
-- ============================================================

INSERT INTO proveedores (
    negocio_id, codigo, razon_social, nombre_comercial, ruc,
    direccion, telefono, email,
    esta_activo, rubro, creado_en, actualizado_en
) VALUES
-- Proveedor 1: Distribuidora de vinos
(1, 'PROV-001', 'Distribuidora de Vinos del Sur S.A.C.', 'Vinos del Sur', '20456789012',
 'Av. La Marina 1520, Pueblo Libre', '951234567', 'cmendoza@vinosdelsur.pe',
 1, 'Proveedor principal de vinos importados y nacionales. Excelente servicio.', NOW(), NOW()),

-- Proveedor 2: Distribuidora de cervezas
(1, 'PROV-002', 'Cervecerías Peruanas Unidas S.A.', 'CPU Distribuciones', '20301234567',
 'Jr. Lampa 250, Cercado de Lima', '952345678', 'mtorres@cpudist.pe',
 1, 'Distribuidor oficial de cervezas artesanales e industriales.', NOW(), NOW()),

-- Proveedor 3: Licores importados
(1, 'PROV-003', 'Importaciones Premium Licores E.I.R.L.', 'Premium Licores', '20567890123',
 'Calle Los Álamos 340, San Isidro', '953456789', 'jparedes@premiumlicores.pe',
 1, 'Importador directo de whisky, vodka, gin y ron premium.', NOW(), NOW()),

-- Proveedor 4: Bebidas no alcohólicas
(1, 'PROV-004', 'Refrescos y Complementos S.A.C.', 'RefreshPeru', '20612345678',
 'Av. Argentina 2150, Carmen de la Legua', '954567890', 'avega@refreshperu.pe',
 1, 'Proveedor de gaseosas, aguas, jugos y mixers para coctelería.', NOW(), NOW()),

-- Proveedor 5: Pisco y destilados nacionales
(1, 'PROV-005', 'Destilería Nacional del Perú S.A.', 'Piscos del Perú', '20498765432',
 'Fundo Viña Grande Km 42, Ica', '955678901', 'rquispe@piscosdelperu.pe',
 1, 'Bodega artesanal de piscos quebranta, italia, acholado y mosto verde.', NOW(), NOW()),

-- Proveedor 6: Inactivo (borrado lógico)
(1, 'PROV-006', 'Licorería Mayorista Lima Norte S.R.L.', 'Licores Lima Norte', '20345678901',
 'Av. Túpac Amaru 5400, Comas', '956789012', 'psanchez@licoleslimanorte.pe',
 0, 'Proveedor dado de baja por incumplimiento reiterado en plazos de entrega.', NOW(), NOW()),

-- Proveedor 7: Snacks y acompañamientos
(1, 'PROV-007', 'Distribuidora de Snacks Andinos S.A.C.', 'SnackAndes', '20723456789',
 'Av. Benavides 3580, Surco', '957890123', 'lfernandez@snackandes.pe',
 1, 'Snacks, frutos secos y acompañamientos para licorería.', NOW(), NOW()),

-- Proveedor 8: Envases y descartables
(1, 'PROV-008', 'Envases y Empaques del Pacífico S.A.C.', 'EmpaPack', '20834567890',
 'Calle Industrial 120, Ate', '958901234', 'dramirez@empapack.pe',
 1, 'Vasos, copas descartables, bolsas y empaques para delivery.', NOW(), NOW());

-- ============================================================
-- 6.1.1 PRODUCTOS POR PROVEEDOR (productos_proveedor)
-- ============================================================

INSERT INTO productos_proveedor (
    proveedor_id, producto_id, sku_proveedor, precio_proveedor,
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido,
    fecha_ultima_compra, creado_en, actualizado_en
) VALUES
-- Proveedor 1 (Vinos del Sur) → productos de vino
(1, 1, 'VDS-VT-001', 35.00, 3, 12, 1, '2026-02-10', NOW(), NOW()),
(1, 2, 'VDS-VT-002', 28.50, 3, 12, 1, '2026-02-10', NOW(), NOW()),
(1, 3, 'VDS-VB-001', 42.00, 5, 6, 0, '2026-01-20', NOW(), NOW()),

-- Proveedor 2 (CPU Distribuciones) → cervezas
(2, 5, 'CPU-CZ-001', 3.80, 1, 24, 1, '2026-02-14', NOW(), NOW()),
(2, 6, 'CPU-CZ-002', 4.20, 1, 24, 1, '2026-02-14', NOW(), NOW()),
(2, 7, 'CPU-CZ-003', 6.50, 2, 12, 0, '2026-02-05', NOW(), NOW()),

-- Proveedor 3 (Premium Licores) → licores importados
(3, 10, 'PL-WH-001', 95.00, 7, 6, 1, '2026-01-30', NOW(), NOW()),
(3, 11, 'PL-WH-002', 120.00, 7, 6, 1, '2026-01-30', NOW(), NOW()),
(3, 12, 'PL-VK-001', 55.00, 7, 12, 0, '2026-02-01', NOW(), NOW()),
(3, 13, 'PL-GN-001', 68.00, 7, 6, 0, '2026-01-15', NOW(), NOW()),
(3, 14, 'PL-RN-001', 85.00, 10, 6, 1, '2026-02-08', NOW(), NOW()),

-- Proveedor 4 (RefreshPeru) → bebidas sin alcohol
(4, 17, 'RP-GA-001', 1.80, 1, 48, 1, '2026-02-12', NOW(), NOW()),
(4, 18, 'RP-AG-001', 0.90, 1, 48, 1, '2026-02-12', NOW(), NOW()),
(4, 19, 'RP-JG-001', 2.50, 2, 24, 0, '2026-02-05', NOW(), NOW()),

-- Proveedor 5 (Piscos del Perú) → piscos
(5, 15, 'PP-PQ-001', 32.00, 5, 12, 1, '2026-02-06', NOW(), NOW()),
(5, 16, 'PP-PA-001', 45.00, 5, 6, 1, '2026-02-06', NOW(), NOW()),

-- Proveedor 7 (SnackAndes) → snacks
(7, 20, 'SA-SN-001', 5.50, 2, 24, 1, '2026-02-10', NOW(), NOW());

-- ============================================================
-- 6.2 ÓRDENES DE COMPRA
-- ============================================================

-- Orden 1: BORRADOR — preparando pedido de vinos
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0001', 1, 1, 1,
    'borrador', 1610.00, 289.80, 0.00, 1899.80,
    'PEN', '2026-02-25', NULL,
    30, '2026-03-27', 'Pedido mensual de vinos tintos y blancos para stock.',
    1, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL,
    NOW(), NOW()
);

-- Orden 2: APROBADA — pedido de cervezas listo para enviar
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0002', 2, 1, 1,
    'aprobada', 960.00, 172.80, 0.00, 1132.80,
    'PEN', '2026-02-20', NULL,
    15, '2026-03-07', 'Reposición de cervezas artesanales e industriales.',
    1, 2, NULL,
    '2026-02-16 10:00:00', NULL, NULL,
    NULL, NULL,
    '2026-02-15 09:00:00', NOW()
);

-- Orden 3: ENVIADA — pedido de licores importados en tránsito
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0003', 3, 1, 1,
    'enviada', 3430.00, 617.40, 0.00, 4047.40,
    'PEN', '2026-02-22', NULL,
    45, '2026-04-03', 'Importación de whisky, vodka y gin premium.',
    1, 2, NULL,
    '2026-02-12 14:30:00', '2026-02-13 09:00:00', NULL,
    NULL, NULL,
    '2026-02-10 11:00:00', NOW()
);

-- Orden 4: RECIBIDA — pedido de piscos ya entregado
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0004', 5, 1, 1,
    'recibida', 1614.00, 290.52, 0.00, 1904.52,
    'PEN', '2026-02-10', '2026-02-09',
    30, '2026-03-11', 'Pedido de piscos quebranta y acholado. Recibido un día antes.',
    1, 2, 3,
    '2026-02-03 10:00:00', '2026-02-04 08:30:00', '2026-02-09 14:00:00',
    NULL, NULL,
    '2026-02-01 09:00:00', '2026-02-09 14:00:00'
);

-- Orden 5: RECEPCION_PARCIAL — gaseosas recibidas parcialmente
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0005', 4, 2, 2,
    'recepcion_parcial', 492.00, 88.56, 0.00, 580.56,
    'PEN', '2026-02-14', NULL,
    15, '2026-03-01', 'Pedido de gaseosas y aguas. Solo llegó la mitad del pedido.',
    2, 1, 3,
    '2026-02-08 11:00:00', '2026-02-09 08:00:00', NULL,
    NULL, NULL,
    '2026-02-07 10:30:00', NOW()
);

-- Orden 6: CANCELADA — pedido al proveedor inactivo
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0006', 6, 1, 1,
    'cancelada', 800.00, 144.00, 0.00, 944.00,
    'PEN', '2026-02-05', NULL,
    60, NULL, 'Pedido cancelado: el proveedor no pudo cumplir con el plazo.',
    1, NULL, NULL,
    NULL, NULL, NULL,
    '2026-02-04 16:00:00', 'Proveedor comunicó imposibilidad de entrega. Se busca alternativa.',
    '2026-01-28 09:00:00', '2026-02-04 16:00:00'
);

-- Orden 7: PENDIENTE_APROBACION — pedido de snacks esperando aprobación
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0007', 7, 1, 1,
    'pendiente_aprobacion', 660.00, 118.80, 0.00, 778.80,
    'PEN', '2026-02-22', NULL,
    15, '2026-03-09', 'Pedido de snacks y frutos secos para acompañamiento.',
    3, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL,
    NOW(), NOW()
);

-- Orden 8: BORRADOR — pedido de envases para delivery (sede 2)
INSERT INTO ordenes_compra (
    negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, monto_impuesto, monto_descuento, total,
    moneda, fecha_entrega_esperada, fecha_entrega_real,
    plazo_pago_dias, fecha_vencimiento_pago, notas,
    creado_por, aprobado_por, recibido_por,
    aprobado_en, enviado_en, recibido_en,
    cancelado_en, razon_cancelacion,
    creado_en, actualizado_en
) VALUES (
    1, 'OC-2026-0008', 8, 2, 2,
    'borrador', 450.00, 81.00, 0.00, 531.00,
    'PEN', '2026-02-28', NULL,
    30, '2026-03-30', 'Compra de vasos, copas descartables y bolsas para delivery.',
    2, NULL, NULL,
    NULL, NULL, NULL,
    NULL, NULL,
    NOW(), NOW()
);

-- ============================================================
-- 6.3 DETALLE DE ÓRDENES DE COMPRA
-- ============================================================

-- Detalle Orden 1 (OC-2026-0001) — Vinos
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(1, 1, 24, 0, 35.00, 18.00, 151.20, 0.00, 840.00, 991.20, 'Vino tinto reserva x24 unidades'),
(1, 2, 18, 0, 28.50, 18.00, 92.34, 0.00, 513.00, 605.34, 'Vino tinto varietal x18 unidades'),
(1, 3, 6, 0, 42.00, 18.00, 45.36, 0.00, 252.00, 297.36, 'Vino blanco sauvignon x6 unidades');

-- Detalle Orden 2 (OC-2026-0002) — Cervezas
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(2, 5, 120, 0, 3.80, 18.00, 82.08, 0.00, 456.00, 538.08, 'Cerveza lager x120 unidades'),
(2, 6, 72, 0, 4.20, 18.00, 54.43, 0.00, 302.40, 356.83, 'Cerveza artesanal IPA x72 unidades'),
(2, 7, 24, 0, 6.50, 18.00, 28.08, 0.00, 156.00, 184.08, 'Cerveza stout premium x24 unidades');

-- Detalle Orden 3 (OC-2026-0003) — Licores importados
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(3, 10, 12, 0, 95.00, 18.00, 205.20, 0.00, 1140.00, 1345.20, 'Whisky escocés 12 años x12'),
(3, 11, 6, 0, 120.00, 18.00, 129.60, 0.00, 720.00, 849.60, 'Whisky bourbon premium x6'),
(3, 12, 18, 0, 55.00, 18.00, 178.20, 0.00, 990.00, 1168.20, 'Vodka importado x18'),
(3, 13, 6, 0, 68.00, 18.00, 73.44, 0.00, 408.00, 481.44, 'Gin London Dry x6');

-- Detalle Orden 4 (OC-2026-0004) — Piscos (ya recibido)
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(4, 15, 24, 24, 32.00, 18.00, 138.24, 0.00, 768.00, 906.24, 'Pisco quebranta x24 — recibido completo'),
(4, 16, 12, 12, 45.00, 18.00, 97.20, 0.00, 540.00, 637.20, 'Pisco acholado x12 — recibido completo');

-- Detalle Orden 5 (OC-2026-0005) — Gaseosas (recepción parcial)
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(5, 17, 96, 48, 1.80, 18.00, 31.10, 0.00, 172.80, 203.90, 'Gaseosa cola 500ml x96 — recibidas 48'),
(5, 18, 96, 48, 0.90, 18.00, 15.55, 0.00, 86.40, 101.95, 'Agua mineral 500ml x96 — recibidas 48'),
(5, 19, 48, 24, 2.50, 18.00, 21.60, 0.00, 120.00, 141.60, 'Jugo de naranja 1L x48 — recibidos 24');

-- Detalle Orden 6 (OC-2026-0006) — Cancelada
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(6, 5, 120, 0, 4.00, 18.00, 86.40, 0.00, 480.00, 566.40, 'Cerveza lager — pedido cancelado'),
(6, 6, 48, 0, 4.50, 18.00, 38.88, 0.00, 216.00, 254.88, 'Cerveza artesanal — pedido cancelado');

-- Detalle Orden 7 (OC-2026-0007) — Snacks (pendiente aprobación)
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(7, 20, 120, 0, 5.50, 18.00, 118.80, 0.00, 660.00, 778.80, 'Mix de snacks surtidos x120 bolsas');

-- Detalle Orden 8 (OC-2026-0008) — Envases (borrador)
INSERT INTO detalle_ordenes_compra (
    orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida,
    precio_unitario, tasa_impuesto, monto_impuesto,
    porcentaje_descuento, subtotal, total, notas
) VALUES
(8, 20, 50, 0, 4.00, 18.00, 36.00, 0.00, 200.00, 236.00, 'Vasos descartables 12oz x50 paquetes'),
(8, 20, 50, 0, 5.00, 18.00, 45.00, 0.00, 250.00, 295.00, 'Bolsas para delivery x50 paquetes');

-- ============================================================
-- 6.4 RECEPCIONES DE COMPRA
-- ============================================================

-- Recepción 1: Recepción completa de la Orden 4 (piscos)
INSERT INTO recepciones_compra (
    negocio_id, orden_compra_id, numero_recepcion, recibido_por,
    fecha_recepcion, notas, estado, creado_en
) VALUES (
    1, 4, 'REC-2026-0001', 3,
    '2026-02-09', 'Recepción completa. Todos los productos en buen estado. Etiquetado correcto.', 'completada',
    '2026-02-09 14:00:00'
);

-- Recepción 2: Recepción parcial de la Orden 5 (gaseosas — primera entrega)
INSERT INTO recepciones_compra (
    negocio_id, orden_compra_id, numero_recepcion, recibido_por,
    fecha_recepcion, notas, estado, creado_en
) VALUES (
    1, 5, 'REC-2026-0002', 3,
    '2026-02-14', 'Primera entrega parcial. Falta la mitad del pedido. Proveedor confirma segunda entrega para el 20/02.', 'parcial',
    '2026-02-14 10:30:00'
);

-- ============================================================
-- 6.5 DETALLE DE RECEPCIONES DE COMPRA
-- ============================================================

-- Detalle Recepción 1 (Orden 4 — piscos, completa)
INSERT INTO detalle_recepciones_compra (
    recepcion_id, detalle_orden_compra_id, producto_id, lote_id,
    cantidad_recibida, cantidad_rechazada, razon_rechazo
) VALUES
(1, 13, 15, NULL, 24, 0, NULL),
(1, 14, 16, NULL, 12, 0, NULL);

-- Detalle Recepción 2 (Orden 5 — gaseosas, parcial)
INSERT INTO detalle_recepciones_compra (
    recepcion_id, detalle_orden_compra_id, producto_id, lote_id,
    cantidad_recibida, cantidad_rechazada, razon_rechazo
) VALUES
(2, 15, 17, NULL, 48, 0, NULL),
(2, 16, 18, NULL, 48, 0, NULL),
(2, 17, 19, NULL, 24, 0, NULL);

-- ============================================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================================
SELECT '=== RESUMEN DATOS BLOQUE 6 ===' AS '';
SELECT 'Proveedores:' AS Tabla, COUNT(*) AS Total FROM proveedores WHERE negocio_id = 1;
SELECT 'Proveedores activos:' AS Tabla, COUNT(*) AS Total FROM proveedores WHERE negocio_id = 1 AND esta_activo = 1;
SELECT 'Productos-Proveedor:' AS Tabla, COUNT(*) AS Total FROM productos_proveedor;
SELECT 'Órdenes de compra:' AS Tabla, COUNT(*) AS Total FROM ordenes_compra WHERE negocio_id = 1;
SELECT 'Detalle órdenes:' AS Tabla, COUNT(*) AS Total FROM detalle_ordenes_compra;
SELECT 'Recepciones:' AS Tabla, COUNT(*) AS Total FROM recepciones_compra WHERE negocio_id = 1;
SELECT 'Detalle recepciones:' AS Tabla, COUNT(*) AS Total FROM detalle_recepciones_compra;
SELECT 'Órdenes por estado:' AS '';
SELECT estado, COUNT(*) AS cantidad FROM ordenes_compra WHERE negocio_id = 1 GROUP BY estado;
