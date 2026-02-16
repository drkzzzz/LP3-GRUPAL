-- ================================================================
-- DATOS DE PRUEBA - BLOQUE 11: DEVOLUCIONES Y REEMBOLSOS
-- RF-FIN-008 al RF-FIN-012
-- ================================================================

-- Nota: Estos datos asumen que ya existen:
-- - negocio_id = 1
-- - Productos con IDs válidos en la tabla 'productos'
-- - Ventas/pedidos registrados (opcional)

-- ================================================================
-- 1. INSERTAR DEVOLUCIÓN DE PRUEBA #1 (Solicitada - Total)
-- ================================================================
INSERT INTO devoluciones (
    negocio_id,
    venta_id,
    cliente_id,
    numero_devolucion,
    estado,
    tipo_devolucion,
    categoria_motivo,
    motivo,
    metodo_reembolso,
    monto_total,
    impuestos_devueltos,
    monto_reembolso,
    notas
) VALUES (
    1,  -- negocio_id
    101,  -- venta_id (si existe)
    1,  -- cliente_id (si existe)
    'DEV-1-20250214001',
    'solicitada',
    'total',
    'defectuoso',
    'Producto llegó con la botella rota y el líquido derramado',
    'pago_original',
    125.50,
    22.59,
    125.50,
    'Cliente solicita reembolso completo a través del método de pago original'
);

-- ================================================================
-- 2. INSERTAR DETALLES DE DEVOLUCIÓN #1
-- ================================================================
-- Asumiendo que la devolución insertada tiene ID=1
INSERT INTO detalle_devoluciones (
    devolucion_id,
    producto_id,
    cantidad,
    precio_unitario,
    subtotal,
    devolver_stock,
    notas
) VALUES 
(1, 5, 2.000, 45.00, 90.00, TRUE, 'Ron Diplomático Reserva - 2 botellas rotas'),
(1, 8, 1.000, 35.50, 35.50, TRUE, 'Vodka Absolut - Tapa defectuosa');

-- ================================================================
-- 3. INSERTAR DEVOLUCIÓN DE PRUEBA #2 (Parcial - Para aprobar)
-- ================================================================
INSERT INTO devoluciones (
    negocio_id,
    pedido_id,
    cliente_id,
    numero_devolucion,
    estado,
    tipo_devolucion,
    categoria_motivo,
    motivo,
    metodo_reembolso,
    monto_total,
    impuestos_devueltos,
    monto_reembolso,
    notas
) VALUES (
    1,
    NULL,
    2,
    'DEV-1-20250214002',
    'solicitada',
    'parcial',
    'articulo_incorrecto',
    'Se envió Whisky Johnnie Walker Red Label en lugar de Black Label',
    'credito_tienda',
    89.90,
    16.18,
    89.90,
    'Cliente acepta crédito en tienda para próxima compra'
);

-- ================================================================
-- 4. INSERTAR DETALLES DE DEVOLUCIÓN #2
-- ================================================================
INSERT INTO detalle_devoluciones (
    devolucion_id,
    producto_id,
    cantidad,
    precio_unitario,
    subtotal,
    devolver_stock,
    notas
) VALUES 
(2, 12, 1.000, 89.90, 89.90, TRUE, 'Producto incorrecto - devolver a inventario');

-- ================================================================
-- 5. INSERTAR DEVOLUCIÓN DE PRUEBA #3 (Aprobada - Lista para completar)
-- ================================================================
INSERT INTO devoluciones (
    negocio_id,
    venta_id,
    cliente_id,
    numero_devolucion,
    estado,
    tipo_devolucion,
    categoria_motivo,
    motivo,
    metodo_reembolso,
    monto_total,
    impuestos_devueltos,
    monto_reembolso,
    notas,
    aprobado_en,
    aprobado_por
) VALUES (
    1,
    103,
    3,
    'DEV-1-20250214003',
    'aprobada',
    'total',
    'vencido',
    'Producto cerca de fecha de vencimiento - no fue notificado al cliente',
    'efectivo',
    55.00,
    9.90,
    55.00,
    'Devolución aprobada por gerencia - proceder con reembolso en efectivo',
    NOW(),
    1  -- ID del usuario que aprobó
);

-- ================================================================
-- 6. INSERTAR DETALLES DE DEVOLUCIÓN #3
-- ================================================================
INSERT INTO detalle_devoluciones (
    devolucion_id,
    producto_id,
    cantidad,
    precio_unitario,
    subtotal,
    devolver_stock,
    notas
) VALUES 
(3, 15, 1.000, 55.00, 55.00, FALSE, 'Producto vencido - NO devolver a inventario');

-- ================================================================
-- 7. INSERTAR DEVOLUCIÓN DE PRUEBA #4 (Completada - Histórico)
-- ================================================================
INSERT INTO devoluciones (
    negocio_id,
    venta_id,
    cliente_id,
    numero_devolucion,
    estado,
    tipo_devolucion,
    categoria_motivo,
    motivo,
    metodo_reembolso,
    monto_total,
    impuestos_devueltos,
    monto_reembolso,
    notas,
    aprobado_en,
    aprobado_por,
    completado_en,
    procesado_por
) VALUES (
    1,
    98,
    1,
    'DEV-1-20250210001',
    'completada',
    'parcial',
    'cambio_cliente',
    'Cliente cambió de opinión sobre producto - devolución dentro de política',
    'transferencia_bancaria',
    150.00,
    27.00,
    150.00,
    'Devolución completada con éxito - reembolso procesado',
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    1,
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    1
);

-- ================================================================
-- 8. INSERTAR DETALLES DE DEVOLUCIÓN #4
-- ================================================================
INSERT INTO detalle_devoluciones (
    devolucion_id,
    producto_id,
    cantidad,
    precio_unitario,
    subtotal,
    devolver_stock,
    notas
) VALUES 
(4, 20, 2.000, 75.00, 150.00, TRUE, 'Tequila - Productos devueltos en perfecto estado');

-- ================================================================
-- 9. INSERTAR DEVOLUCIÓN DE PRUEBA #5 (Rechazada - Histórico)
-- ================================================================
INSERT INTO devoluciones (
    negocio_id,
    venta_id,
    cliente_id,
    numero_devolucion,
    estado,
    tipo_devolucion,
    categoria_motivo,
    motivo,
    metodo_reembolso,
    monto_total,
    impuestos_devueltos,
    monto_reembolso,
    notas,
    rechazado_en,
    rechazado_por
) VALUES (
    1,
    105,
    4,
    'DEV-1-20250212001',
    'rechazada',
    'total',
    'danado',
    'Botella llegó dañada',
    NULL,
    200.00,
    36.00,
    0.00,
    'RECHAZO: Devolución rechazada - producto fue recibido hace más de 30 días y no se reportó daño en el momento de entrega',
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    1
);

-- ================================================================
-- 10. INSERTAR DETALLES DE DEVOLUCIÓN #5
-- ================================================================
INSERT INTO detalle_devoluciones (
    devolucion_id,
    producto_id,
    cantidad,
    precio_unitario,
    subtotal,
    devolver_stock,
    notas
) VALUES 
(5, 25, 1.000, 200.00, 200.00, FALSE, 'Reclamación fuera de tiempo');

-- ================================================================
-- RESUMEN DE DATOS INSERTADOS
-- ================================================================
-- Total de devoluciones: 5
-- Estados:
--   - solicitada: 2 (IDs 1, 2)
--   - aprobada: 1 (ID 3)
--   - completada: 1 (ID 4)
--   - rechazada: 1 (ID 5)
--
-- Tipos:
--   - total: 4
--   - parcial: 1
--
-- Categorías de motivo:
--   - defectuoso: 1
--   - articulo_incorrecto: 1
--   - vencido: 1
--   - cambio_cliente: 1
--   - danado: 1
--
-- Métodos de reembolso:
--   - pago_original: 1
--   - credito_tienda: 1
--   - efectivo: 1
--   - transferencia_bancaria: 1
-- ================================================================

-- NOTA: Estos scripts SQL son para insertar datos directamente en la base de datos.
-- Para probar los endpoints REST, usar Postman con los ejemplos del archivo de pruebas.
