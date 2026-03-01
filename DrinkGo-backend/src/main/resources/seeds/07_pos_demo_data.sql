-- ============================================================
-- SEED 07: DATOS DEMO PARA POS (PRECIOS, STOCK, FACTURACIÓN)
-- ============================================================
-- Completa los datos necesarios para el módulo POS:
--   - Precios de venta y compra de productos
--   - Almacenes (si no existen)
--   - Stock inventario con cantidades disponibles
--   - Lotes de inventario para deducción FIFO
--   - Series de facturación activas
--   - Caja registradora para abrir turno
--   - Cliente de prueba
--
-- Dependencias: 03_negocios_demo.sql, 04_catalogo_demo.sql
-- Idempotente: puede ejecutarse múltiples veces
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- VARIABLES DE REFERENCIA
-- ============================================================
SET @n_donpepe = (SELECT id FROM negocios WHERE ruc = '20123456789' LIMIT 1);
SET @sede_dp   = (SELECT id FROM sedes WHERE codigo = 'SEDE-PRINCIPAL' AND negocio_id = @n_donpepe LIMIT 1);
SET @user_dp   = (SELECT id FROM usuarios WHERE email = 'admin@donpepe.com' LIMIT 1);

-- ============================================================
-- 1. PRECIOS DE PRODUCTOS (Don Pepe)
-- ============================================================
-- Actualizar precio_venta y precio_compra para los 5 productos demo
-- Precios realistas de licorería peruana (incluyen IGV)

UPDATE productos SET 
    precio_venta = 45.90, 
    precio_compra = 28.50
WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001';

UPDATE productos SET 
    precio_venta = 8.50, 
    precio_compra = 3.20
WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001';

UPDATE productos SET 
    precio_venta = 65.00, 
    precio_compra = 42.00
WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001';

UPDATE productos SET 
    precio_venta = 12.50, 
    precio_compra = 5.80
WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001';

UPDATE productos SET 
    precio_venta = 9.00, 
    precio_compra = 4.50
WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001';

-- ============================================================
-- 2. ALMACÉN (Don Pepe)
-- ============================================================
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @sede_dp, 'ALM-PRINCIPAL', 'Almacén Principal',
    'Almacén principal para productos de alta rotación', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-PRINCIPAL');

SET @alm_dp = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-PRINCIPAL' LIMIT 1);

-- ============================================================
-- 3. STOCK INVENTARIO (Don Pepe - Almacén Principal)
-- ============================================================
-- Usa columnas reales del entity: cantidad_actual, cantidad_disponible, cantidad_reservada

-- Ron Cartavio Black
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_dp, 200.00, 200.00, 0.00, 28.50, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1)
    AND almacen_id = @alm_dp
);

-- Cerveza Pilsen Callao
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    @alm_dp, 400.00, 400.00, 0.00, 3.20, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1)
    AND almacen_id = @alm_dp
);

-- Vino Casillero del Diablo
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    @alm_dp, 60.00, 60.00, 0.00, 42.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1)
    AND almacen_id = @alm_dp
);

-- Papitas Lay's
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1),
    @alm_dp, 150.00, 150.00, 0.00, 5.80, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1)
    AND almacen_id = @alm_dp
);

-- Coca-Cola 1.5L
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    @alm_dp, 300.00, 300.00, 0.00, 4.50, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1)
    AND almacen_id = @alm_dp
);

-- ============================================================
-- 4. LOTES INVENTARIO (Don Pepe - FIFO)
-- ============================================================
-- Columnas reales: cantidad_inicial, cantidad_actual, costo_unitario, fecha_ingreso

-- Ron Cartavio - Lote 1 (más antiguo, se deduce primero FIFO)
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_dp, 'LT-POS-RON-001', 120.00, 120.00, 28.50, '2028-01-15', '2026-01-15 10:30:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-RON-001' AND almacen_id = @alm_dp
);

-- Ron Cartavio - Lote 2 (más reciente)
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_dp, 'LT-POS-RON-002', 80.00, 80.00, 29.00, '2028-02-05', '2026-02-05 14:20:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-RON-002' AND almacen_id = @alm_dp
);

-- Cerveza Pilsen - Lote 1
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    @alm_dp, 'LT-POS-CER-001', 240.00, 240.00, 3.20, '2026-08-10', '2026-02-10 09:15:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-CER-001' AND almacen_id = @alm_dp
);

-- Cerveza Pilsen - Lote 2
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    @alm_dp, 'LT-POS-CER-002', 160.00, 160.00, 3.25, '2026-09-20', '2026-02-20 11:45:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-CER-002' AND almacen_id = @alm_dp
);

-- Vino Casillero - Lote 1
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    @alm_dp, 'LT-POS-VIN-001', 60.00, 60.00, 42.00, '2029-12-31', '2026-01-18 15:00:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-VIN-001' AND almacen_id = @alm_dp
);

-- Papitas Lay's - Lote 1
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1),
    @alm_dp, 'LT-POS-SNK-001', 150.00, 150.00, 5.80, '2026-08-12', '2026-02-12 10:00:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-SNK-001' AND almacen_id = @alm_dp
);

-- Coca-Cola - Lote 1
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    @alm_dp, 'LT-POS-GAS-001', 200.00, 200.00, 4.50, '2026-08-15', '2026-02-15 13:30:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-GAS-001' AND almacen_id = @alm_dp
);

-- Coca-Cola - Lote 2
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, 
    cantidad_inicial, cantidad_actual, costo_unitario, fecha_vencimiento, fecha_ingreso, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    @alm_dp, 'LT-POS-GAS-002', 100.00, 100.00, 4.45, '2026-09-25', '2026-02-25 16:00:00', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario WHERE numero_lote = 'LT-POS-GAS-002' AND almacen_id = @alm_dp
);

-- ============================================================
-- 5. SERIES DE FACTURACIÓN (Don Pepe)
-- ============================================================
-- Arreglar series existentes o crear nuevas activas y predeterminadas

-- Primero desactivar y "des-borrar" las series existentes si las hay
UPDATE series_facturacion 
SET es_predeterminada = 1, esta_activo = 1, eliminado_en = NULL, actualizado_en = NOW()
WHERE negocio_id = @n_donpepe AND serie = 'B001' AND tipo_documento = 'boleta';

-- Si no existe serie de boleta, crear una
INSERT INTO series_facturacion (negocio_id, sede_id, serie, tipo_documento, numero_actual, es_predeterminada, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @sede_dp, 'B001', 'boleta', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM series_facturacion WHERE negocio_id = @n_donpepe AND serie = 'B001' AND tipo_documento = 'boleta'
);

-- Serie de factura
UPDATE series_facturacion 
SET es_predeterminada = 1, esta_activo = 1, eliminado_en = NULL, actualizado_en = NOW()
WHERE negocio_id = @n_donpepe AND tipo_documento = 'factura';

INSERT INTO series_facturacion (negocio_id, sede_id, serie, tipo_documento, numero_actual, es_predeterminada, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @sede_dp, 'F001', 'factura', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM series_facturacion WHERE negocio_id = @n_donpepe AND tipo_documento = 'factura'
);

-- ============================================================
-- 6. CAJA REGISTRADORA (Don Pepe)
-- ============================================================
INSERT INTO cajas_registradoras (negocio_id, sede_id, codigo, nombre_caja, monto_apertura_defecto, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @sede_dp, 'CAJA-001', 'Caja Principal', 200.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM cajas_registradoras WHERE negocio_id = @n_donpepe AND codigo = 'CAJA-001'
);

-- ============================================================
-- 7. CLIENTE DE PRUEBA (Don Pepe)
-- ============================================================
INSERT INTO clientes (negocio_id, tipo_documento, numero_documento, nombres, apellidos, 
    razon_social, email, telefono, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'DNI', '12345678', 'Juan Carlos', 'Pérez López',
    NULL, 'juancarlos@email.com', '987654321', 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM clientes WHERE negocio_id = @n_donpepe AND numero_documento = '12345678'
);

-- ============================================================
-- VERIFICACIÓN
-- ============================================================
SELECT '=== PRODUCTOS CON PRECIOS ===' AS info;
SELECT id, sku, nombre, precio_venta, precio_compra 
FROM productos WHERE negocio_id = @n_donpepe;

SELECT '=== ALMACENES ===' AS info;
SELECT id, codigo, nombre, es_predeterminado FROM almacenes WHERE negocio_id = @n_donpepe;

SELECT '=== STOCK INVENTARIO ===' AS info;
SELECT si.producto_id, p.sku, si.cantidad_disponible, si.cantidad_actual
FROM stock_inventario si
JOIN productos p ON p.id = si.producto_id
WHERE si.negocio_id = @n_donpepe;

SELECT '=== LOTES INVENTARIO ===' AS info;
SELECT li.numero_lote, p.sku, li.cantidad_actual, li.costo_unitario, li.fecha_ingreso
FROM lotes_inventario li
JOIN productos p ON p.id = li.producto_id
WHERE li.negocio_id = @n_donpepe
ORDER BY li.fecha_ingreso;

SELECT '=== SERIES FACTURACIÓN ===' AS info;
SELECT id, serie, tipo_documento, numero_actual, es_predeterminada, esta_activo, eliminado_en
FROM series_facturacion WHERE negocio_id = @n_donpepe;

SELECT '=== CAJAS ===' AS info;
SELECT * FROM cajas_registradoras WHERE negocio_id = @n_donpepe;

SELECT '=== SEED POS COMPLETADO ===' AS info;
