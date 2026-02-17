[-- Inserts mínimos para probar el backend del Bloque 5 (Inventario, Lotes, Movimientos, Multi-negocio licorerías)]

-- 1. Negocio
INSERT INTO negocios (uuid, razon_social, nombre_comercial, tipo_negocio, email, estado, esta_activo)
VALUES (UUID(), 'Licorería DrinkGo SAC', 'DrinkGo', 'licoreria', 'info@drinkgo.com', 'activo', 1);

-- 2. Rol
INSERT INTO roles (negocio_id, nombre, slug, descripcion, es_rol_sistema, esta_activo)
VALUES (1, 'Administrador', 'admin', 'Rol de administrador', 0, 1);

-- 3. Usuario (contraseña: 123456)
INSERT INTO usuarios (uuid, negocio_id, email, hash_contrasena, nombres, apellidos, idioma, esta_activo)
VALUES (UUID(), 1, 'admin@drinkgo.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92Y.nGPhLxVmRaxgLEaO6', 'Admin', 'DrinkGo', 'es', 1);

-- 4. Asignar rol al usuario
INSERT INTO usuarios_roles (usuario_id, rol_id)
VALUES (1, 1);

-- 5. Sede
INSERT INTO sedes (negocio_id, codigo, nombre, direccion, pais, esta_activo)
VALUES (1, 'SEDE-001', 'Sede Principal', 'Av. Principal 123', 'PE', 1);

-- 6. Almacén principal
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, tipo_almacenamiento, esta_activo)
VALUES (1, 1, 'ALM-001', 'Almacén Principal', 'ambiente', 1);

-- 7. Almacén secundario
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, tipo_almacenamiento, esta_activo)
VALUES (1, 1, 'ALM-002', 'Almacén Secundario', 'frio', 1);

-- 8. Producto
INSERT INTO productos (negocio_id, sku, nombre, slug, precio_compra, precio_venta, stock_minimo, stock_maximo, punto_reorden, es_perecible, dias_vida_util, esta_activo)
VALUES (1, 'PROD-001', 'Cerveza Artesanal 500ml', 'cerveza-artesanal-500ml', 8.50, 15.00, 10, 500, 20, 1, 180, 1);

-- 9. Stock inicial
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_en_mano, cantidad_reservada)
VALUES (1, 1, 1, 100, 0);

-- 10. Lote 1
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_inicial, cantidad_restante, precio_compra, fecha_fabricacion, fecha_vencimiento, fecha_recepcion, estado)
VALUES (1, 1, 1, 'LOTE-2024-001', 50, 50, 8.50, '2024-01-15', '2024-07-15', '2024-02-01', 'disponible');

-- 11. Lote 2
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_inicial, cantidad_restante, precio_compra, fecha_fabricacion, fecha_vencimiento, fecha_recepcion, estado)
VALUES (1, 1, 1, 'LOTE-2024-002', 80, 80, 9.00, '2024-03-01', '2024-09-01', '2024-03-15', 'disponible');

-- 12. Movimiento de salida (venta, activa FIFO)
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, costo_unitario, motivo, realizado_por)
VALUES (1, 1, 1, 1, 'salida_venta', 60, 8.50, 'Venta del día - Pedido #123', 1);

-- 13. Movimiento de entrada (ajuste manual)
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, tipo_movimiento, cantidad, motivo, realizado_por)
VALUES (1, 1, 1, 'ajuste_entrada', 20, 'Ajuste por inventario físico', 1);

-- 14. Transferencia entre almacenes (borrador)
INSERT INTO transferencias_inventario (negocio_id, numero_transferencia, almacen_origen_id, almacen_destino_id, estado, solicitado_por, solicitado_en)
VALUES (1, 'TRF-001', 1, 2, 'borrador', 1, NOW());

-- 15. Detalle de transferencia
INSERT INTO detalle_transferencias_inventario (transferencia_id, producto_id, cantidad_solicitada, notas)
VALUES (1, 1, 30, 'Cerveza Artesanal para bar');

-- 16. Alerta de inventario (stock bajo)
INSERT INTO alertas_inventario (negocio_id, producto_id, almacen_id, tipo_alerta, mensaje, valor_umbral, valor_actual, esta_resuelta, creado_en)
VALUES (1, 1, 1, 'stock_bajo', 'Stock bajo de Cerveza Artesanal', 10, 10, 0, NOW());
