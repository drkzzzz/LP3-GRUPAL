-- ============================================================
-- SEEDS BLOQUE 11: DEVOLUCIONES Y REEMBOLSOS
-- ============================================================
-- Este archivo contiene datos de prueba para el módulo de devoluciones
-- Fecha: 17 de febrero de 2026
-- ============================================================

USE drinkgo_db;

-- NOTA: Este seed asume que ya existen las siguientes entidades:
-- - negocios (id: 1, 2, 3)
-- - sedes (al menos 1 por negocio)
-- - clientes (al menos 3)
-- - productos (al menos 10)
-- - ventas y detalle_ventas (al menos 5 ventas)
-- - usuarios (al menos 5 usuarios)
-- - almacenes (al menos 2)

-- ============================================================
-- DEVOLUCIONES
-- ============================================================

INSERT INTO devoluciones (
    id, negocio_id, sede_id, numero_devolucion, venta_id, pedido_id, cliente_id,
    tipo_devolucion, categoria_motivo, detalle_motivo,
    subtotal, monto_impuesto, total,
    metodo_reembolso, estado,
    solicitado_por, aprobado_por, procesado_por,
    solicitado_en, aprobado_en, completado_en, notas
) VALUES
-- Devolución 1: Producto defectuoso (Completada)
(1, 1, 1, 'DEV-2026-0001', 1, NULL, 1,
 'parcial', 'defectuoso', 'Botella con tapa rota, no se puede usar',
 84.75, 15.25, 100.00,
 'efectivo', 'completada',
 1, 2, 2,
 '2026-02-10 14:30:00', '2026-02-10 15:00:00', '2026-02-10 16:30:00',
 'Cliente muy molesto, se le atendió con prioridad'),

-- Devolución 2: Artículo incorrecto (Completada)
(2, 1, 1, 'DEV-2026-0002', 2, NULL, 2,
 'parcial', 'articulo_incorrecto', 'Pidió whisky 12 años y se le entregó 8 años',
 127.12, 22.88, 150.00,
 'pago_original', 'completada',
 1, 2, 3,
 '2026-02-11 10:15:00', '2026-02-11 10:45:00', '2026-02-11 11:30:00',
 'Error en el picking, se reentrenó al personal'),

-- Devolución 3: Cambio de cliente (Aprobada, pendiente de procesar)
(3, 1, 1, 'DEV-2026-0003', 3, NULL, 1,
 'total', 'cambio_cliente', 'Cliente se arrepintió de la compra, dentro del periodo permitido',
 254.24, 45.76, 300.00,
 'credito_tienda', 'aprobada',
 1, 2, NULL,
 '2026-02-15 09:00:00', '2026-02-15 09:30:00', NULL,
 'Cliente frecuente, se le otorgó crédito en tienda'),

-- Devolución 4: Producto vencido (Procesando)
(4, 2, 2, 'DEV-2026-0004', 4, NULL, 3,
 'parcial', 'vencido', 'Cerveza con fecha de vencimiento pasada',
 50.85, 9.15, 60.00,
 'efectivo', 'procesando',
 2, 3, 3,
 '2026-02-14 16:45:00', '2026-02-14 17:00:00', NULL,
 'Se verificó el lote completo, se retirará todo el stock vencido'),

-- Devolución 5: Producto dañado en transporte (Completada)
(5, 2, 2, 'DEV-2026-0005', 5, NULL, 2,
 'parcial', 'danado', 'Botella rota durante el delivery',
 42.37, 7.63, 50.00,
 'pago_original', 'completada',
 2, 3, 4,
 '2026-02-13 18:20:00', '2026-02-13 18:30:00', '2026-02-13 19:00:00',
 'Se sancionó al repartidor por mal manejo'),

-- Devolución 6: Solicitada pero rechazada
(6, 1, 1, 'DEV-2026-0006', 2, NULL, 2,
 'total', 'cambio_cliente', 'Cliente quiere devolver después de 15 días',
 423.73, 76.27, 500.00,
 'efectivo', 'rechazada',
 1, 2, NULL,
 '2026-02-16 11:00:00', NULL, NULL, '2026-02-16 11:30:00',
 'Fuera del periodo de devolución (máximo 7 días)'),

-- Devolución 7: Otro motivo (Solicitada, pendiente aprobación)
(7, 3, 3, 'DEV-2026-0007', 6, NULL, 3,
 'parcial', 'otro', 'Sabor diferente al esperado según descripción del producto',
 33.90, 6.10, 40.00,
 'credito_tienda', 'solicitada',
 3, NULL, NULL,
 '2026-02-17 08:00:00', NULL, NULL,
 'En revisión por el supervisor'),

-- Devolución 8: Artículo incorrecto (Completada - múltiples items)
(8, 3, 3, 'DEV-2026-0008', 7, NULL, 1,
 'parcial', 'articulo_incorrecto', 'Pedido mezclado con otro cliente',
 169.49, 30.51, 200.00,
 'pago_original', 'completada',
 3, 4, 4,
 '2026-02-12 12:30:00', '2026-02-12 13:00:00', '2026-02-12 14:00:00',
 'Error grave en el sistema de gestión de pedidos, se reportó al área de TI');

-- ============================================================
-- DETALLE DE DEVOLUCIONES
-- ============================================================

INSERT INTO detalle_devoluciones (
    id, devolucion_id, producto_id, detalle_venta_id, detalle_pedido_id,
    cantidad, precio_unitario, total,
    estado_condicion, devolver_stock, almacen_id, notas
) VALUES
-- Detalles de Devolución 1 (1 producto defectuoso)
(1, 1, 5, 1, NULL,
 1, 100.00, 100.00,
 'danado', 0, NULL, 'Botella con tapa rota, se descartará'),

-- Detalles de Devolución 2 (1 producto incorrecto)
(2, 2, 8, 3, NULL,
 1, 150.00, 150.00,
 'bueno', 1, 1, 'Producto en buen estado, se reintegrará al stock'),

-- Detalles de Devolución 3 (devolución total - 3 productos)
(3, 3, 10, 5, NULL,
 2, 75.00, 150.00,
 'bueno', 1, 1, 'Sin abrir, sellado original'),

(4, 3, 12, 6, NULL,
 1, 100.00, 100.00,
 'bueno', 1, 1, 'Sin abrir, sellado original'),

(5, 3, 3, 7, NULL,
 1, 50.00, 50.00,
 'bueno', 1, 1, 'Sin abrir, sellado original'),

-- Detalles de Devolución 4 (producto vencido)
(6, 4, 15, 8, NULL,
 6, 10.00, 60.00,
 'vencido', 0, NULL, 'Six-pack vencido hace 2 meses, se destruirá'),

-- Detalles de Devolución 5 (producto dañado)
(7, 5, 7, 9, NULL,
 1, 50.00, 50.00,
 'danado', 0, NULL, 'Botella rota en delivery'),

-- Detalles de Devolución 6 (rechazada - no se procesaron)
(8, 6, 20, 10, NULL,
 5, 100.00, 500.00,
 'bueno', 0, NULL, 'Devolución rechazada, no se procesa'),

-- Detalles de Devolución 7 (pendiente aprobación)
(9, 7, 18, 11, NULL,
 2, 20.00, 40.00,
 'abierto', 0, NULL, 'Producto abierto pero lleno, sabor no gustó'),

-- Detalles de Devolución 8 (múltiples productos incorrectos)
(10, 8, 14, 13, NULL,
 2, 60.00, 120.00,
 'bueno', 1, 2, 'Productos correctos pero pedido equivocado'),

(11, 8, 16, 14, NULL,
 1, 80.00, 80.00,
 'bueno', 1, 2, 'Productos correctos pero pedido equivocado');

-- ============================================================
-- CONSULTAS ÚTILES PARA VERIFICAR LOS DATOS
-- ============================================================

-- Ver todas las devoluciones con su estado
-- SELECT d.numero_devolucion, n.nombre_comercial, d.tipo_devolucion, 
--        d.categoria_motivo, d.total, d.estado, d.solicitado_en
-- FROM devoluciones d
-- JOIN negocios n ON d.negocio_id = n.id
-- ORDER BY d.solicitado_en DESC;

-- Ver detalle completo de una devolución
-- SELECT dd.*, p.nombre as producto_nombre
-- FROM detalle_devoluciones dd
-- JOIN productos p ON dd.producto_id = p.id
-- WHERE dd.devolucion_id = 1;

-- Estadísticas de devoluciones por motivo
-- SELECT categoria_motivo, COUNT(*) as total, SUM(total) as monto_total
-- FROM devoluciones
-- GROUP BY categoria_motivo;

-- ============================================================
-- FIN SEEDS BLOQUE 11
-- ============================================================