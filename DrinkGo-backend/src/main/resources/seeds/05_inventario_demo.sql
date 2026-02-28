-- ============================================================
-- SEEDS DEMO: INVENTARIO
-- ============================================================
-- Este archivo inicializa datos de demostración para el módulo de Inventario:
--   - Almacenes por sede
--   - Stock de productos  
--   - Lotes de inventario con fechas de vencimiento
--   - Movimientos de inventario (historial)
--
-- Instrucciones:
--   1. Ejecutar DESPUÉS de 03_negocios_demo.sql y 04_catalogo_demo.sql
--   2. Este script es idempotente (puede ejecutarse múltiples veces)
--   3. Usa WHERE NOT EXISTS para evitar duplicados
--
-- Fecha: Febrero 2026
-- ============================================================

-- ============================================================
-- 1. ALMACENES
-- ============================================================

-- Variables de negocios (ya creados en 03_negocios_demo.sql)
SET @n_donpepe      = (SELECT id FROM negocios WHERE ruc = '20123456789' LIMIT 1);
SET @n_labodega     = (SELECT id FROM negocios WHERE ruc = '20987654321' LIMIT 1);
SET @n_elimperio    = (SELECT id FROM negocios WHERE ruc = '20456789123' LIMIT 1);
SET @n_premium      = (SELECT id FROM negocios WHERE ruc = '20111222333' LIMIT 1);

-- Variables de sedes
SET @sede_donpepe_main    = (SELECT id FROM sedes WHERE codigo = 'SEDE-PRINCIPAL' AND negocio_id = @n_donpepe LIMIT 1);
SET @sede_labodega_01     = (SELECT id FROM sedes WHERE codigo = 'LB-01' AND negocio_id = @n_labodega LIMIT 1);
SET @sede_labodega_02     = (SELECT id FROM sedes WHERE codigo = 'LB-02' AND negocio_id = @n_labodega LIMIT 1);
SET @sede_elimperio_main  = (SELECT id FROM sedes WHERE codigo = 'EI-MAIN' AND negocio_id = @n_elimperio LIMIT 1);
SET @sede_premium_main    = (SELECT id FROM sedes WHERE codigo = 'PW-001' AND negocio_id = @n_premium LIMIT 1);

-- ── DON PEPE ──
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_donpepe, @sede_donpepe_main, 'ALM-PRINCIPAL', 'Almacén Principal',
    'Almacén principal para productos de alta rotación', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-PRINCIPAL');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_donpepe, @sede_donpepe_main, 'ALM-DEPOSITO', 'Depósito Secundario',
    'Almacén para stock de reserva y productos de baja rotación', 0, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-DEPOSITO');

-- ── LA BODEGA ──
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_labodega, @sede_labodega_01, 'LB01-ALM-MAIN', 'Almacén San Isidro',
    'Almacén principal sede San Isidro', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB01-ALM-MAIN');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_labodega, @sede_labodega_02, 'LB02-ALM-MAIN', 'Almacén Miraflores',
    'Almacén principal sede Miraflores', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB02-ALM-MAIN');

-- ── EL IMPERIO ──
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_elimperio, @sede_elimperio_main, 'EI-ALM-GENERAL', 'Almacén General',
    'Almacén principal El Imperio', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-GENERAL');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_elimperio, @sede_elimperio_main, 'EI-ALM-FRIO', 'Almacén Refrigerado',
    'Almacén con refrigeración para cervezas y vinos especiales', 0, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-FRIO');

-- ── PREMIUM WINES (SUSPENDIDO PERO CON DATOS) ──
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_premium, @sede_premium_main, 'PW-ALM-MAIN', 'Almacén Principal Premium',
    'Almacén principal Premium Wines', 1, 0
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_premium AND codigo = 'PW-ALM-MAIN');

-- Variables de almacenes creados
SET @alm_donpepe_principal  = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-PRINCIPAL' LIMIT 1);
SET @alm_donpepe_deposito   = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-DEPOSITO' LIMIT 1);
SET @alm_labodega_01        = (SELECT id FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB01-ALM-MAIN' LIMIT 1);
SET @alm_labodega_02        = (SELECT id FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB02-ALM-MAIN' LIMIT 1);
SET @alm_elimperio_general  = (SELECT id FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-GENERAL' LIMIT 1);
SET @alm_elimperio_frio     = (SELECT id FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-FRIO' LIMIT 1);
SET @alm_premium_main       = (SELECT id FROM almacenes WHERE negocio_id = @n_premium AND codigo = 'PW-ALM-MAIN' LIMIT 1);

-- ============================================================
-- 2. STOCK INVENTARIO (RESUMEN)
-- ============================================================
-- Creamos primero los registros de stock para que los lotes puedan referenciarlos
-- Nota: La cantidad_total se actualizará después de crear los lotes

-- ── DON PEPE ──
-- Producto: Ron Cartavio Black 750ml
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_donpepe_principal, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1)
    AND almacen_id = @alm_donpepe_principal
);

-- Producto: Cerveza Pilsen Callao 630ml
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    @alm_donpepe_principal, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1)
    AND almacen_id = @alm_donpepe_principal
);

-- Producto: Vino Casillero del Diablo
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    @alm_donpepe_principal, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1)
    AND almacen_id = @alm_donpepe_principal
);

-- Producto: Papitas Lay's
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1),
    @alm_donpepe_principal, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1)
    AND almacen_id = @alm_donpepe_principal
);

-- Producto: Coca-Cola 1.5L
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    @alm_donpepe_principal, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1)
    AND almacen_id = @alm_donpepe_principal
);

-- Almacén secundario - algunos productos con menos stock
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_donpepe_deposito, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1)
    AND almacen_id = @alm_donpepe_deposito
);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    @alm_donpepe_deposito, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1)
    AND almacen_id = @alm_donpepe_deposito
);

-- ── LA BODEGA ── (Sede 01)
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    @alm_labodega_01, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1)
    AND almacen_id = @alm_labodega_01
);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    @alm_labodega_01, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1)
    AND almacen_id = @alm_labodega_01
);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1),
    @alm_labodega_01, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1)
    AND almacen_id = @alm_labodega_01
);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1),
    @alm_labodega_01, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1)
    AND almacen_id = @alm_labodega_01
);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1),
    @alm_labodega_01, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1)
    AND almacen_id = @alm_labodega_01
);

-- ── LA BODEGA ── (Sede 02)
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    @alm_labodega_02, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1)
    AND almacen_id = @alm_labodega_02
);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    @alm_labodega_02, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1)
    AND almacen_id = @alm_labodega_02
);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1),
    @alm_labodega_02, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1)
    AND almacen_id = @alm_labodega_02
);

-- ── EL IMPERIO __ (Almacén General)
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_total)
SELECT @n_elimperio,
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    @alm_elimperio_general, 0
WHERE NOT EXISTS (
    SELECT 1 FROM stock_inventario 
    WHERE producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1)
    AND almacen_id = @alm_elimperio_general
);

-- ============================================================
-- 3. LOTES INVENTARIO
-- ============================================================
-- Creamos múltiples lotes por producto con fechas de vencimiento realistas

-- ── DON PEPE - Ron Cartavio (Almacén Principal) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260115-001', 120, 28.50, '2028-01-15', 'disponible',
    '2026-01-15 10:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260115-001' AND almacen_id = @alm_donpepe_principal
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260205-002', 80, 29.00, '2028-02-05', 'disponible',
    '2026-02-05 14:20:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260205-002' AND almacen_id = @alm_donpepe_principal
);

-- ── DON PEPE - Cerveza Pilsen (Almacén Principal) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260210-003', 240, 3.20, '2026-08-10', 'disponible',
    '2026-02-10 09:15:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260210-003' AND almacen_id = @alm_donpepe_principal
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260220-004', 160, 3.25, '2026-09-20', 'disponible',
    '2026-02-20 11:45:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260220-004' AND almacen_id = @alm_donpepe_principal
);

-- ── DON PEPE - Vino Casillero (Almacén Principal) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260118-005', 60, 42.00, '2029-12-31', 'disponible',
    '2026-01-18 15:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260118-005' AND almacen_id = @alm_donpepe_principal
);

-- ── DON PEPE - Papitas Lay's (Almacén Principal) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260212-006', 150, 5.80, '2026-08-12', 'disponible',
    '2026-02-12 10:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260212-006' AND almacen_id = @alm_donpepe_principal
);

-- ── DON PEPE - Coca-Cola (Almacén Principal) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260215-007', 200, 4.50, '2026-08-15', 'disponible',
    '2026-02-15 13:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260215-007' AND almacen_id = @alm_donpepe_principal
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    @alm_donpepe_principal, 'LT-20260225-008', 100, 4.45, '2026-09-25', 'disponible',
    '2026-02-25 16:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260225-008' AND almacen_id = @alm_donpepe_principal
);

-- ── DON PEPE - Depósito (stock de reserva) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_donpepe_deposito, 'LT-20251220-009', 50, 27.80, '2027-12-20', 'disponible',
    '2025-12-20 10:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20251220-009' AND almacen_id = @alm_donpepe_deposito
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    @alm_donpepe_deposito, 'LT-20251215-010', 40, 41.50, '2029-06-30', 'disponible',
    '2025-12-15 14:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20251215-010' AND almacen_id = @alm_donpepe_deposito
);

-- ── LA BODEGA - Sede 01 (San Isidro) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    @alm_labodega_01, 'LT-20260205-011', 100, 28.80, '2028-02-05', 'disponible',
    '2026-02-05 11:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260205-011' AND almacen_id = @alm_labodega_01
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    @alm_labodega_01, 'LT-20260218-012', 180, 3.22, '2026-08-18', 'disponible',
    '2026-02-18 09:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260218-012' AND almacen_id = @alm_labodega_01
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1),
    @alm_labodega_01, 'LT-20260120-013', 75, 42.50, '2029-11-30', 'disponible',
    '2026-01-20 16:15:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260120-013' AND almacen_id = @alm_labodega_01
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1),
    @alm_labodega_01, 'LT-20260222-014', 120, 5.90, '2026-08-22', 'disponible',
    '2026-02-22 10:45:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260222-014' AND almacen_id = @alm_labodega_01
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1),
    @alm_labodega_01, 'LT-20260224-015', 150, 4.55, '2026-08-24', 'disponible',
    '2026-02-24 14:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260224-015' AND almacen_id = @alm_labodega_01
);

-- ── LA BODEGA - Sede 02 (Miraflores) ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    @alm_labodega_02, 'LT-20260208-016', 90, 28.90, '2028-02-08', 'disponible',
    '2026-02-08 10:20:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260208-016' AND almacen_id = @alm_labodega_02
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    @alm_labodega_02, 'LT-20260210-017', 200, 3.18, '2026-08-10', 'disponible',
    '2026-02-10 11:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260210-017' AND almacen_id = @alm_labodega_02
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1),
    @alm_labodega_02, 'LT-20260125-018', 65, 42.20, '2029-10-31', 'disponible',
    '2026-01-25 15:45:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260125-018' AND almacen_id = @alm_labodega_02
);

-- ── EL IMPERIO ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio,
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    @alm_elimperio_general, 'LT-20260201-019', 150, 28.40, '2028-01-31', 'disponible',
    '2026-02-01 12:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260201-019' AND almacen_id = @alm_elimperio_general
);

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, 
    costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio,
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    @alm_elimperio_general, 'LT-20260215-020', 100, 28.60, '2028-02-15', 'disponible',
    '2026-02-15 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM lotes_inventario 
    WHERE numero_lote = 'LT-20260215-020' AND almacen_id = @alm_elimperio_general
);

-- ============================================================
-- 4. ACTUALIZAR CANTIDAD TOTAL EN STOCK_INVENTARIO
-- ============================================================

-- ── DON PEPE - Almacén Principal ──
UPDATE stock_inventario SET cantidad_total = (
    SELECT COALESCE(SUM(cantidad_restante), 0)
    FROM lotes_inventario 
    WHERE lotes_inventario.producto_id = stock_inventario.producto_id
    AND lotes_inventario.almacen_id = stock_inventario.almacen_id
    AND lotes_inventario.estado = 'disponible'
)
WHERE almacen_id = @alm_donpepe_principal;

-- ── DON PEPE - Depósito ──
UPDATE stock_inventario SET cantidad_total = (
    SELECT COALESCE(SUM(cantidad_restante), 0)
    FROM lotes_inventario 
    WHERE lotes_inventario.producto_id = stock_inventario.producto_id
    AND lotes_inventario.almacen_id = stock_inventario.almacen_id
    AND lotes_inventario.estado = 'disponible'
)
WHERE almacen_id = @alm_donpepe_deposito;

-- ── LA BODEGA - Sede 01 ──
UPDATE stock_inventario SET cantidad_total = (
    SELECT COALESCE(SUM(cantidad_restante), 0)
    FROM lotes_inventario 
    WHERE lotes_inventario.producto_id = stock_inventario.producto_id
    AND lotes_inventario.almacen_id = stock_inventario.almacen_id
    AND lotes_inventario.estado = 'disponible'
)
WHERE almacen_id = @alm_labodega_01;

-- ── LA BODEGA - Sede 02 ──
UPDATE stock_inventario SET cantidad_total = (
    SELECT COALESCE(SUM(cantidad_restante), 0)
    FROM lotes_inventario 
    WHERE lotes_inventario.producto_id = stock_inventario.producto_id
    AND lotes_inventario.almacen_id = stock_inventario.almacen_id
    AND lotes_inventario.estado = 'disponible'
)
WHERE almacen_id = @alm_labodega_02;

-- ── EL IMPERIO ──
UPDATE stock_inventario SET cantidad_total = (
    SELECT COALESCE(SUM(cantidad_restante), 0)
    FROM lotes_inventario 
    WHERE lotes_inventario.producto_id = stock_inventario.producto_id
    AND lotes_inventario.almacen_id = stock_inventario.almacen_id
    AND lotes_inventario.estado = 'disponible'
)
WHERE almacen_id = @alm_elimperio_general;

-- ============================================================
-- 5. MOVIMIENTOS INVENTARIO (HISTORIAL)
-- ============================================================
-- Creamos algunos movimientos históricos para demostrar el kardex

-- ── DON PEPE - Entrada inicial de Ron ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, 
    cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_donpepe_principal,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260115-001' LIMIT 1),
    'stock_inicial', 150, 'Inventario inicial - Lote LT-20260115-001',
    (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe AND email = 'admin@donpepe.com' LIMIT 1),
    '2026-01-15 10:35:00'
WHERE NOT EXISTS (
    SELECT 1 FROM movimientos_inventario 
    WHERE lote_id = (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260115-001' LIMIT 1)
    AND tipo_movimiento = 'stock_inicial'
);

-- ── DON PEPE - Salida por venta de Ron ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, 
    cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    @alm_donpepe_principal,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260115-001' LIMIT 1),
    'salida_venta', -30, 'Venta - Pedido #VT-001',
    (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe AND email = 'admin@donpepe.com' LIMIT 1),
    '2026-01-20 14:20:00'
WHERE NOT EXISTS (
    SELECT 1 FROM movimientos_inventario 
    WHERE lote_id = (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260115-001' LIMIT 1)
    AND motivo = 'Venta - Pedido #VT-001'
);

-- ── DON PEPE - Entrada de nueva compra de Cerveza ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, 
    cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    @alm_donpepe_principal,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260210-003' LIMIT 1),
    'entrada_compra', 240, 'Compra - Orden OC-20260210-001',
    (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe AND email = 'admin@donpepe.com' LIMIT 1),
    '2026-02-10 09:20:00'
WHERE NOT EXISTS (
    SELECT 1 FROM movimientos_inventario 
    WHERE lote_id = (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260210-003' LIMIT 1)
    AND tipo_movimiento = 'entrada_compra'
);

-- ── LA BODEGA - Stock inicial Sede 01 ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, 
    cantidad, motivo, realizado_por, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    @alm_labodega_01,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260205-011' LIMIT 1),
    'stock_inicial', 100, 'Inventario inicial - San Isidro',
    (SELECT id FROM usuarios WHERE negocio_id = @n_labodega AND email = 'admin@labodega.com.pe' LIMIT 1),
    '2026-02-05 11:10:00'
WHERE NOT EXISTS (
    SELECT 1 FROM movimientos_inventario 
    WHERE lote_id = (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260205-011' LIMIT 1)
    AND tipo_movimiento = 'stock_inicial'
);

-- ── LA BODEGA - Ajuste de inventario (merma) ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, 
    cantidad, motivo, realizado_por, creado_en)
SELECT @n_labodega,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    @alm_labodega_01,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260218-012' LIMIT 1),
    'ajuste_salida', -20, 'Ajuste por inventario físico - Roturas',
    (SELECT id FROM usuarios WHERE negocio_id = @n_labodega AND email = 'admin@labodega.com.pe' LIMIT 1),
    '2026-02-19 16:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM movimientos_inventario 
    WHERE lote_id = (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260218-012' LIMIT 1)
    AND motivo LIKE 'Ajuste por inventario físico%'
);

-- ── EL IMPERIO - Stock inicial ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, 
    cantidad, motivo, realizado_por, creado_en)
SELECT @n_elimperio,
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    @alm_elimperio_general,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260201-019' LIMIT 1),
    'stock_inicial', 150, 'Inventario inicial El Imperio',
    (SELECT id FROM usuarios WHERE negocio_id = @n_elimperio AND email = 'admin@elimperio.pe' LIMIT 1),
    '2026-02-01 12:10:00'
WHERE NOT EXISTS (
    SELECT 1 FROM movimientos_inventario 
    WHERE lote_id = (SELECT id FROM lotes_inventario WHERE numero_lote = 'LT-20260201-019' LIMIT 1)
    AND tipo_movimiento = 'stock_inicial'
);

-- ============================================================
-- FIN DEL SEED: INVENTARIO
-- ============================================================
