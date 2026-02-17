-- =====================================================
-- DATOS DE PRUEBA - BLOQUE 8: VENTAS, POS Y CAJAS
-- Sistema DrinkGo - Multi-tenant
-- =====================================================

-- IMPORTANTE: Ejecutar DESPUÉS de tener datos de:
-- - Negocios (negocio_id = 1)
-- - Sedes (sede_id = 1, 2)
-- - Usuarios (usuario_id = 1, 2, 3)
-- - Clientes (cliente_id = 1, 2, 3)
-- - Productos (producto_id >= 10)
-- - Métodos de Pago (metodo_pago_id = 1-Efectivo, 2-Tarjeta, 3-Transferencia, 4-Yape)

-- =====================================================
-- 1. CAJAS REGISTRADORAS
-- =====================================================

INSERT INTO caja_registradora (negocio_id, sede_id, codigo_caja, nombre_caja, esta_activo, eliminado, fecha_creacion, fecha_actualizacion)
VALUES
    -- Sede Principal (sede_id = 1)
    (1, 1, 'CAJA-001', 'Caja Principal - Mostrador', 1, 0, NOW(), NOW()),
    (1, 1, 'CAJA-002', 'Caja Secundaria - Express', 1, 0, NOW(), NOW()),
    (1, 1, 'CAJA-003', 'Caja VIP - Clientes Premium', 1, 0, NOW(), NOW()),
    
    -- Sede Norte (sede_id = 2, ajustar según tu configuración)
    (1, 2, 'CAJA-N01', 'Caja Norte Principal', 1, 0, NOW(), NOW()),
    (1, 2, 'CAJA-N02', 'Caja Norte Secundaria', 1, 0, NOW(), NOW()),
    
    -- Caja inactiva (para testing)
    (1, 1, 'CAJA-OLD', 'Caja Antigua (Desactivada)', 0, 0, NOW(), NOW());

-- =====================================================
-- 2. SESIONES DE CAJA
-- =====================================================

-- Sesión 1: Sesión ABIERTA actual en CAJA-001
INSERT INTO sesion_caja (
    negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    fecha_apertura, fecha_cierre,
    observaciones_apertura, observaciones_cierre,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 1, NULL,
    200.00, NULL, NULL, NULL,
    DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL,
    'Apertura turno mañana - Fondo fijo 200 soles',
    NULL,
    0, NOW(), NOW()
);

-- Sesión 2: Sesión CERRADA de ayer en CAJA-001 (con diferencia positiva)
INSERT INTO sesion_caja (
    negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    fecha_apertura, fecha_cierre,
    observaciones_apertura, observaciones_cierre,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 1, 1,
    200.00, 3450.50, 3420.00, 30.50,
    DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 10 HOUR,
    'Apertura turno completo',
    'Arqueo con sobrante de 30.50 - Se registró en libro de caja',
    0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)
);

-- Sesión 3: Sesión CERRADA de anteayer en CAJA-002 (arqueo perfecto)
INSERT INTO sesion_caja (
    negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    fecha_apertura, fecha_cierre,
    observaciones_apertura, observaciones_cierre,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 2, 2, 2,
    150.00, 2100.00, 2100.00, 0.00,
    DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 8 HOUR,
    'Apertura turno express',
    'Arqueo perfecto - Sin diferencias',
    0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)
);

-- Sesión 4: Sesión ABIERTA en CAJA-002 (para testing concurrente)
INSERT INTO sesion_caja (
    negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    fecha_apertura, fecha_cierre,
    observaciones_apertura, observaciones_cierre,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 2, 2, NULL,
    150.00, NULL, NULL, NULL,
    DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL,
    'Turno tarde - Express',
    NULL,
    0, NOW(), NOW()
);

-- Sesión 5: Sesión CERRADA con faltante (diferencia negativa)
INSERT INTO sesion_caja (
    negocio_id, caja_id, usuario_apertura_id, usuario_cierre_id,
    monto_inicial, monto_final, monto_esperado, diferencia,
    fecha_apertura, fecha_cierre,
    observaciones_apertura, observaciones_cierre,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 3, 3, 3,
    300.00, 1870.00, 1920.00, -50.00,
    DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 9 HOUR,
    'VIP - Alto movimiento esperado',
    'Faltante de 50.00 - Se solicitó revisión de grabaciones',
    0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)
);

-- =====================================================
-- 3. MOVIMIENTOS DE CAJA
-- =====================================================

-- Movimientos de la Sesión 1 (ABIERTA actual)
INSERT INTO movimiento_caja (
    negocio_id, sesion_id, usuario_id,
    tipo_movimiento, monto, metodo_pago_id,
    concepto, comprobante_referencia,
    fecha_movimiento, eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    -- Ingreso: Ajuste inicial
    (1, 1, 1, 'INGRESO', 50.00, 1, 'Ajuste de fondo inicial autorizado por gerencia', '', DATE_SUB(NOW(), INTERVAL 110 MINUTE), 0, NOW(), NOW()),
    
    -- Egreso: Compra de materiales
    (1, 1, 1, 'EGRESO', 35.00, 1, 'Compra de bolsas plásticas y materiales de empaque', 'FAC-001', DATE_SUB(NOW(), INTERVAL 90 MINUTE), 0, NOW(), NOW()),
    
    -- Egreso: Pago a proveedor
    (1, 1, 1, 'EGRESO', 120.00, 1, 'Pago a proveedor de hielo - Deuda pendiente', 'REC-045', DATE_SUB(NOW(), INTERVAL 60 MINUTE), 0, NOW(), NOW()),
    
    -- Ingreso: Recarga de fondo
    (1, 1, 1, 'INGRESO', 100.00, 1, 'Recarga de fondo para vueltos', '', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 0, NOW(), NOW()),
    
    -- Ajuste: Corrección de arqueo
    (1, 1, 1, 'AJUSTE', 10.00, 1, 'Ajuste por billete falso detectado', 'INF-012', DATE_SUB(NOW(), INTERVAL 15 MINUTE), 0, NOW(), NOW());

-- Movimientos de la Sesión 2 (CERRADA de ayer)
INSERT INTO movimiento_caja (
    negocio_id, sesion_id, usuario_id,
    tipo_movimiento, monto, metodo_pago_id,
    concepto, comprobante_referencia,
    fecha_movimiento, eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (1, 2, 1, 'INGRESO', 200.00, 1, 'Recarga de fondo para cambio', '', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (1, 2, 1, 'EGRESO', 80.00, 1, 'Pago de delivery externo', 'REC-100', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 HOUR, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (1, 2, 1, 'EGRESO', 25.00, 1, 'Propina repartidor pool', '', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 7 HOUR, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Movimientos de la Sesión 4 (ABIERTA en CAJA-002)
INSERT INTO movimiento_caja (
    negocio_id, sesion_id, usuario_id,
    tipo_movimiento, monto, metodo_pago_id,
    concepto, comprobante_referencia,
    fecha_movimiento, eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (1, 4, 2, 'EGRESO', 20.00, 1, 'Compra de cambio en banco', '', DATE_SUB(NOW(), INTERVAL 45 MINUTE), 0, NOW(), NOW());

-- =====================================================
-- 4. VENTAS
-- =====================================================

-- NOTA: Ajustar producto_id según los productos disponibles en tu BD
-- Asumiendo productos con IDs del 10 al 30

-- Venta 1: LOCAL - Efectivo - Sesión actual (CONFIRMADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 1, 1, 1,
    'VEN-1-1704369600001', 'LOCAL', 'POS', NULL,
    150.00, 0.00, 0.00, 0.00, 0.00, 150.00,
    'CONFIRMADA', DATE_SUB(NOW(), INTERVAL 90 MINUTE), NULL,
    NULL, 'Cliente frecuente - Pago en efectivo',
    NULL, NULL,
    0, NOW(), NOW()
);

-- Venta 2: LOCAL - Pago Mixto - Sesión actual (CONFIRMADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 1, 2, 1,
    'VEN-1-1704369600002', 'LOCAL', 'POS', NULL,
    320.00, 20.00, 0.00, 0.00, 0.00, 300.00,
    'CONFIRMADA', DATE_SUB(NOW(), INTERVAL 60 MINUTE), NULL,
    NULL, 'Descuento por cantidad - Pago mixto',
    NULL, NULL,
    0, NOW(), NOW()
);

-- Venta 3: DELIVERY - Yape - Sesión actual (CONFIRMADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 1, 3, 1,
    'VEN-1-1704369600003', 'DELIVERY', 'WEB', NULL,
    180.00, 0.00, 0.00, 12.00, 8.00, 200.00,
    'CONFIRMADA', DATE_SUB(NOW(), INTERVAL 45 MINUTE), NULL,
    'Av. Los Alamos 456, Surco', 'Entregar antes de las 20:00 - Llamar al llegar',
    NULL, NULL,
    0, NOW(), NOW()
);

-- Venta 4: LOCAL - Tarjeta - Sesión 4 (CONFIRMADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 4, 1, 2,
    'VEN-1-1704369600004', 'LOCAL', 'POS', NULL,
    450.00, 50.00, 0.00, 0.00, 0.00, 400.00,
    'CONFIRMADA', DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL,
    NULL, 'Compra al por mayor - Descuento especial',
    NULL, NULL,
    0, NOW(), NOW()
);

-- Venta 5: LOCAL - Efectivo - Sesión 2 de ayer (CONFIRMADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 2, 2, 1,
    'VEN-1-1704283200001', 'LOCAL', 'POS', NULL,
    850.00, 0.00, 0.00, 0.00, 0.00, 850.00,
    'CONFIRMADA', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 3 HOUR, NULL,
    NULL, 'Evento corporativo - Venta grande',
    NULL, NULL,
    0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)
);

-- Venta 6: DELIVERY - Transferencia - Sesión 2 de ayer (ANULADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 2, 3, 1,
    'VEN-1-1704283200002', 'DELIVERY', 'TELEFONO', NULL,
    120.00, 0.00, 0.00, 10.00, 0.00, 130.00,
    'ANULADA', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 6 HOUR,
    'Jr. Las Flores 789, San Isidro', 'ANULADA: Cliente no atendió llamadas - Pedido cancelado',
    NULL, NULL,
    0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)
);

-- Venta 7: PICK_UP - Efectivo - Sesión 3 de anteayer (CONFIRMADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 3, 1, 2,
    'VEN-1-1704196800001', 'PICK_UP', 'WEB', NULL,
    200.00, 10.00, 0.00, 0.00, 0.00, 190.00,
    'CONFIRMADA', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 4 HOUR, NULL,
    NULL, 'Pedido web - Recojo en tienda - Descuento online',
    NULL, NULL,
    0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)
);

-- Venta 8: MARKETPLACE (Rappi) - Sesión 3 (CONFIRMADA)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 3, 2, 2,
    'VEN-1-1704196800002', 'MARKETPLACE', 'MARKETPLACE', 'Rappi',
    95.00, 0.00, 0.00, 0.00, 0.00, 95.00,
    'CONFIRMADA', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 6 HOUR, NULL,
    'Gestionado por Rappi', 'Pedido desde app Rappi - Comisión 15%',
    NULL, NULL,
    0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)
);

-- Venta 9: LOCAL - Pendiente de pago (PENDIENTE)
INSERT INTO venta (
    negocio_id, sede_id, sesion_caja_id, cliente_id, vendedor_id,
    codigo_venta, tipo_venta, canal_venta, plataforma_marketplace,
    subtotal, descuento, impuesto, costo_delivery, propina, total,
    estado, fecha_venta, fecha_anulacion,
    direccion_entrega, observaciones,
    mesa_id, pedido_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES (
    1, 1, 1, 1, 1,
    'VEN-1-1704369600005', 'LOCAL', 'POS', NULL,
    280.00, 0.00, 0.00, 0.00, 0.00, 280.00,
    'PENDIENTE', DATE_SUB(NOW(), INTERVAL 10 MINUTE), NULL,
    NULL, 'Venta en proceso - Esperando confirmación de pago',
    NULL, NULL,
    0, NOW(), NOW()
);

-- =====================================================
-- 5. DETALLE DE VENTAS
-- =====================================================

-- Detalles de Venta 1 (VEN-1-1704369600001)
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (1, 10, 2, 25.00, 0.00, 50.00, NULL, 0, NOW(), NOW()),
    (1, 12, 3, 20.00, 0.00, 60.00, NULL, 0, NOW(), NOW()),
    (1, 15, 1, 40.00, 0.00, 40.00, NULL, 0, NOW(), NOW());

-- Detalles de Venta 2 (VEN-1-1704369600002)
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (2, 18, 4, 50.00, 0.00, 200.00, NULL, 0, NOW(), NOW()),
    (2, 20, 2, 60.00, 0.00, 120.00, NULL, 0, NOW(), NOW());
    -- Subtotal: 320.00 - Descuento general: 20.00 = Total: 300.00

-- Detalles de Venta 3 (VEN-1-1704369600003) - DELIVERY
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (3, 11, 3, 30.00, 0.00, 90.00, NULL, 0, NOW(), NOW()),
    (3, 14, 2, 45.00, 0.00, 90.00, NULL, 0, NOW(), NOW());
    -- Subtotal: 180.00 + Delivery: 12.00 + Propina: 8.00 = Total: 200.00

-- Detalles de Venta 4 (VEN-1-1704369600004)
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (4, 25, 5, 70.00, 0.00, 350.00, NULL, 0, NOW(), NOW()),
    (4, 28, 2, 50.00, 0.00, 100.00, NULL, 0, NOW(), NOW());
    -- Subtotal: 450.00 - Descuento: 50.00 = Total: 400.00

-- Detalles de Venta 5 (VEN-1-1704283200001) - Ayer
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (5, 10, 10, 25.00, 0.00, 250.00, NULL, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (5, 12, 10, 20.00, 0.00, 200.00, NULL, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (5, 15, 10, 40.00, 0.00, 400.00, NULL, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Detalles de Venta 6 (ANULADA)
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (6, 16, 4, 30.00, 0.00, 120.00, NULL, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Detalles de Venta 7 (PICK_UP)
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (7, 22, 4, 50.00, 0.00, 200.00, NULL, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Detalles de Venta 8 (MARKETPLACE - Rappi)
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (8, 13, 1, 45.00, 0.00, 45.00, NULL, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (8, 17, 2, 25.00, 0.00, 50.00, NULL, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Detalles de Venta 9 (PENDIENTE)
INSERT INTO detalle_venta (
    venta_id, producto_id, cantidad, precio_unitario, descuento_por_item, subtotal_item, lote_id,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (9, 19, 4, 70.00, 0.00, 280.00, NULL, 0, NOW(), NOW());

-- =====================================================
-- 6. PAGOS DE VENTAS
-- =====================================================

-- Pagos de Venta 1 (Efectivo - Total: 150.00)
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (1, 1, 150.00, '', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 90 MINUTE), 0, NOW(), NOW());

-- Pagos de Venta 2 (Pago Mixto - Total: 300.00)
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (2, 1, 150.00, '', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 60 MINUTE), 0, NOW(), NOW()),
    (2, 2, 150.00, 'VISA-****4521', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 60 MINUTE), 0, NOW(), NOW());

-- Pagos de Venta 3 (Yape - Total: 200.00)
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (3, 4, 200.00, 'YAPE-OP-789456123', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 45 MINUTE), 0, NOW(), NOW());

-- Pagos de Venta 4 (Tarjeta - Total: 400.00)
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (4, 2, 400.00, 'MASTER-****8765', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 0, NOW(), NOW());

-- Pagos de Venta 5 (Efectivo - Total: 850.00) - Ayer
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (5, 1, 850.00, '', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 3 HOUR, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Pagos de Venta 6 (ANULADA - Reembolsado)
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (6, 3, 130.00, 'TRANS-987654', 'REEMBOLSADO', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 5 HOUR, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- Pagos de Venta 7 (Efectivo - Total: 190.00)
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (7, 1, 190.00, '', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 4 HOUR, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Pagos de Venta 8 (Marketplace - Total: 95.00)
INSERT INTO pago_venta (
    venta_id, metodo_pago_id, monto, referencia_pago, estado, fecha_pago,
    eliminado, fecha_creacion, fecha_actualizacion
) VALUES
    (8, 2, 95.00, 'RAPPI-PAY-001', 'COMPLETADO', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 6 HOUR, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Pagos de Venta 9 (PENDIENTE - Sin pago aún)
-- NO se insertan pagos para ventas pendientes

-- =====================================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- =====================================================

-- Contar registros insertados
SELECT 'Cajas Registradoras' AS tabla, COUNT(*) AS total FROM caja_registradora WHERE eliminado = 0
UNION ALL
SELECT 'Sesiones de Caja' AS tabla, COUNT(*) AS total FROM sesion_caja WHERE eliminado = 0
UNION ALL
SELECT 'Movimientos de Caja' AS tabla, COUNT(*) AS total FROM movimiento_caja WHERE eliminado = 0
UNION ALL
SELECT 'Ventas' AS tabla, COUNT(*) AS total FROM venta WHERE eliminado = 0
UNION ALL
SELECT 'Detalles de Venta' AS tabla, COUNT(*) AS total FROM detalle_venta WHERE eliminado = 0
UNION ALL
SELECT 'Pagos de Venta' AS tabla, COUNT(*) AS total FROM pago_venta WHERE eliminado = 0;

-- Verificar sesiones abiertas
SELECT 
    sc.id,
    sc.caja_id,
    cr.nombre_caja,
    sc.monto_inicial,
    sc.fecha_apertura,
    'ABIERTA' AS estado
FROM sesion_caja sc
JOIN caja_registradora cr ON sc.caja_id = cr.id
WHERE sc.fecha_cierre IS NULL AND sc.eliminado = 0;

-- Verificar ventas por estado
SELECT 
    estado,
    COUNT(*) AS cantidad,
    SUM(total) AS total_monto
FROM venta
WHERE eliminado = 0
GROUP BY estado;

-- Verificar diferencias en cierres de caja
SELECT 
    sc.id,
    cr.nombre_caja,
    sc.monto_esperado,
    sc.monto_final,
    sc.diferencia,
    CASE 
        WHEN sc.diferencia > 0 THEN 'SOBRANTE'
        WHEN sc.diferencia < 0 THEN 'FALTANTE'
        ELSE 'PERFECTO'
    END AS tipo_diferencia,
    sc.fecha_cierre
FROM sesion_caja sc
JOIN caja_registradora cr ON sc.caja_id = cr.id
WHERE sc.fecha_cierre IS NOT NULL AND sc.eliminado = 0
ORDER BY sc.fecha_cierre DESC;

-- =====================================================
-- FIN DE DATOS DE PRUEBA
-- =====================================================

-- Notas importantes:
-- 1. Los IDs de productos (producto_id) deben existir previamente en la tabla "producto"
-- 2. Ajustar sede_id según las sedes disponibles en tu BD
-- 3. Los métodos de pago (metodo_pago_id) deben existir: 1-Efectivo, 2-Tarjeta, 3-Transferencia, 4-Yape
-- 4. Las sesiones ABIERTAS (id=1 y id=4) están listas para crear nuevas ventas
-- 5. La venta PENDIENTE (id=9) puede usarse para testing de confirmación de pagos
-- 6. La venta ANULADA (id=6) muestra el flujo de cancelación

-- Para limpiar los datos de prueba:
-- DELETE FROM pago_venta WHERE venta_id IN (1,2,3,4,5,6,7,8,9);
-- DELETE FROM detalle_venta WHERE venta_id IN (1,2,3,4,5,6,7,8,9);
-- DELETE FROM venta WHERE id IN (1,2,3,4,5,6,7,8,9);
-- DELETE FROM movimiento_caja WHERE sesion_id IN (1,2,3,4,5);
-- DELETE FROM sesion_caja WHERE id IN (1,2,3,4,5);
-- DELETE FROM caja_registradora WHERE codigo_caja LIKE 'CAJA-%';
