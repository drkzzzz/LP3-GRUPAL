-- ============================================================
-- SEED 05: INVENTARIO DEMO (ALMACENES, STOCK, LOTES, MOVIMIENTOS)
-- Idempotente: usa WHERE NOT EXISTS para no duplicar al reiniciar backend
-- Dependencia: 03_negocios_demo.sql, 04_catalogo_demo.sql
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- REFERENCIAS
-- ============================================================
SET @n_donpepe   = (SELECT id FROM negocios WHERE ruc = '20123456789' LIMIT 1);
SET @n_labodega  = (SELECT id FROM negocios WHERE ruc = '20987654321' LIMIT 1);
SET @n_elimperio = (SELECT id FROM negocios WHERE ruc = '20456789123' LIMIT 1);

-- Sedes
SET @sede_donpepe   = (SELECT id FROM sedes WHERE codigo = 'SEDE-PRINCIPAL' AND negocio_id = @n_donpepe   LIMIT 1);
SET @sede_labodega1 = (SELECT id FROM sedes WHERE codigo = 'LB-01'          AND negocio_id = @n_labodega  LIMIT 1);
SET @sede_labodega2 = (SELECT id FROM sedes WHERE codigo = 'LB-02'          AND negocio_id = @n_labodega  LIMIT 1);
SET @sede_elimperio = (SELECT id FROM sedes WHERE codigo = 'EI-MAIN'        AND negocio_id = @n_elimperio LIMIT 1);

-- Productos DON PEPE
SET @dp_ron = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1);
SET @dp_cer = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1);
SET @dp_vin = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1);
SET @dp_snk = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1);
SET @dp_gas = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1);

-- Productos LA BODEGA
SET @lb_ron = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1);
SET @lb_cer = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1);
SET @lb_vin = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1);
SET @lb_snk = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1);
SET @lb_gas = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1);

-- Productos EL IMPERIO
SET @ei_ron = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1);
SET @ei_cer = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-CER-001' LIMIT 1);
SET @ei_vin = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-VIN-001' LIMIT 1);
SET @ei_snk = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-SNK-001' LIMIT 1);
SET @ei_gas = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-GAS-001' LIMIT 1);

-- Usuarios admin
SET @usr_donpepe   = (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe   AND email = 'admin@donpepe.com'      LIMIT 1);
SET @usr_labodega  = (SELECT id FROM usuarios WHERE negocio_id = @n_labodega  AND email = 'admin@labodega.com.pe'  LIMIT 1);
SET @usr_elimperio = (SELECT id FROM usuarios WHERE negocio_id = @n_elimperio AND email = 'admin@elimperio.pe'     LIMIT 1);


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  1. ALMACENES  (2 por negocio × 3 negocios = 6)            ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_donpepe, @sede_donpepe, 'DP-ALM-PRINCIPAL', 'Almacén Principal',
    'Almacén principal para productos de alta rotación', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'DP-ALM-PRINCIPAL');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_donpepe, @sede_donpepe, 'DP-ALM-FRIO', 'Almacén Refrigerado',
    'Espacio refrigerado para cervezas y bebidas frías', 0, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'DP-ALM-FRIO');

-- ── LA BODEGA ──
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_labodega, @sede_labodega1, 'LB-ALM-SANISIDRO', 'Almacén San Isidro',
    'Almacén principal sede San Isidro', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB-ALM-SANISIDRO');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_labodega, @sede_labodega2, 'LB-ALM-MIRAFLORES', 'Almacén Miraflores',
    'Almacén principal sede Miraflores', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB-ALM-MIRAFLORES');

-- ── EL IMPERIO ──
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_elimperio, @sede_elimperio, 'EI-ALM-GENERAL', 'Almacén General',
    'Almacén general de distribución', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-GENERAL');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo)
SELECT @n_elimperio, @sede_elimperio, 'EI-ALM-FRIO', 'Cámara Fría',
    'Cámara refrigerada para productos que requieren frío', 0, 1
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-FRIO');

-- Variables de almacenes
SET @alm_dp_principal = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe   AND codigo = 'DP-ALM-PRINCIPAL'  LIMIT 1);
SET @alm_dp_frio      = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe   AND codigo = 'DP-ALM-FRIO'       LIMIT 1);
SET @alm_lb_sanisidro = (SELECT id FROM almacenes WHERE negocio_id = @n_labodega  AND codigo = 'LB-ALM-SANISIDRO'  LIMIT 1);
SET @alm_lb_miraflor  = (SELECT id FROM almacenes WHERE negocio_id = @n_labodega  AND codigo = 'LB-ALM-MIRAFLORES' LIMIT 1);
SET @alm_ei_general   = (SELECT id FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-GENERAL'    LIMIT 1);
SET @alm_ei_frio      = (SELECT id FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-FRIO'       LIMIT 1);


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  2. LOTES DE INVENTARIO                                     ║
-- ║  Columnas: negocio_id, producto_id, almacen_id, numero_lote║
-- ║            fecha_ingreso, fecha_vencimiento, cantidad_inicial║
-- ║            cantidad_actual, costo_unitario                  ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_donpepe, @dp_ron, @alm_dp_principal, 'DP-LT-2026-001',
    '2026-01-10', '2028-06-10', 120.00, 95.00, 28.50, 1, '2026-01-10 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'DP-LT-2026-001');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_donpepe, @dp_cer, @alm_dp_frio, 'DP-LT-2026-002',
    '2026-01-15', '2026-07-15', 240.00, 180.00, 3.80, 1, '2026-01-15 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'DP-LT-2026-002');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_donpepe, @dp_vin, @alm_dp_principal, 'DP-LT-2026-003',
    '2026-01-20', '2029-12-31', 48.00, 42.00, 38.00, 1, '2026-01-20 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'DP-LT-2026-003');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_donpepe, @dp_snk, @alm_dp_principal, 'DP-LT-2026-004',
    '2026-02-01', '2026-08-01', 100.00, 72.00, 4.20, 1, '2026-02-01 08:30:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'DP-LT-2026-004');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_donpepe, @dp_gas, @alm_dp_frio, 'DP-LT-2026-005',
    '2026-02-05', '2026-12-05', 60.00, 45.00, 3.50, 1, '2026-02-05 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'DP-LT-2026-005');

-- Segundo lote de ron (reposición reciente)
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_donpepe, @dp_ron, @alm_dp_principal, 'DP-LT-2026-006',
    '2026-02-20', '2028-08-20', 60.00, 60.00, 29.00, 1, '2026-02-20 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'DP-LT-2026-006');

-- ── LA BODEGA ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_labodega, @lb_ron, @alm_lb_sanisidro, 'LB-LT-2026-001',
    '2026-01-08', '2028-07-08', 80.00, 55.00, 27.00, 1, '2026-01-08 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LB-LT-2026-001');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_labodega, @lb_cer, @alm_lb_sanisidro, 'LB-LT-2026-002',
    '2026-01-12', '2026-07-12', 300.00, 210.00, 3.50, 1, '2026-01-12 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LB-LT-2026-002');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_labodega, @lb_vin, @alm_lb_miraflor, 'LB-LT-2026-003',
    '2026-01-18', '2029-06-18', 36.00, 28.00, 40.00, 1, '2026-01-18 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LB-LT-2026-003');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_labodega, @lb_snk, @alm_lb_sanisidro, 'LB-LT-2026-004',
    '2026-02-03', '2026-08-03', 80.00, 55.00, 4.00, 1, '2026-02-03 08:30:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LB-LT-2026-004');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_labodega, @lb_gas, @alm_lb_sanisidro, 'LB-LT-2026-005',
    '2026-02-08', '2026-11-08', 48.00, 30.00, 3.20, 1, '2026-02-08 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LB-LT-2026-005');

-- ── EL IMPERIO ──
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_elimperio, @ei_ron, @alm_ei_general, 'EI-LT-2026-001',
    '2026-01-05', '2028-05-05', 200.00, 140.00, 26.50, 1, '2026-01-05 07:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-001');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_elimperio, @ei_cer, @alm_ei_frio, 'EI-LT-2026-002',
    '2026-01-10', '2026-07-10', 500.00, 350.00, 3.20, 1, '2026-01-10 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-002');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_elimperio, @ei_vin, @alm_ei_general, 'EI-LT-2026-003',
    '2026-01-15', '2029-08-15', 60.00, 48.00, 36.00, 1, '2026-01-15 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-003');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_elimperio, @ei_snk, @alm_ei_general, 'EI-LT-2026-004',
    '2026-01-22', '2026-07-22', 150.00, 100.00, 3.80, 1, '2026-01-22 08:30:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-004');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_elimperio, @ei_gas, @alm_ei_frio, 'EI-LT-2026-005',
    '2026-01-28', '2026-12-28', 100.00, 70.00, 3.00, 1, '2026-01-28 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-005');

-- Segundo lote cerveza El Imperio
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote,
    fecha_ingreso, fecha_vencimiento, cantidad_inicial, cantidad_actual, costo_unitario, esta_activo, creado_en)
SELECT @n_elimperio, @ei_cer, @alm_ei_frio, 'EI-LT-2026-006',
    '2026-02-15', '2026-08-15', 400.00, 400.00, 3.30, 1, '2026-02-15 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-006');


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  3. STOCK INVENTARIO (cantidad consolidada por producto)    ║
-- ║  Columnas: negocio_id, producto_id, almacen_id,             ║
-- ║            cantidad_actual, cantidad_disponible,             ║
-- ║            cantidad_reservada, costo_promedio               ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_donpepe, @dp_ron, @alm_dp_principal, 155.00, 150.00, 5.00, 28.67, 1, '2026-01-10 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_donpepe AND producto_id = @dp_ron AND almacen_id = @alm_dp_principal);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_donpepe, @dp_cer, @alm_dp_frio, 180.00, 175.00, 5.00, 3.80, 1, '2026-01-15 10:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_donpepe AND producto_id = @dp_cer AND almacen_id = @alm_dp_frio);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_donpepe, @dp_vin, @alm_dp_principal, 42.00, 42.00, 0.00, 38.00, 1, '2026-01-20 11:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_donpepe AND producto_id = @dp_vin AND almacen_id = @alm_dp_principal);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_donpepe, @dp_snk, @alm_dp_principal, 72.00, 72.00, 0.00, 4.20, 1, '2026-02-01 08:35:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_donpepe AND producto_id = @dp_snk AND almacen_id = @alm_dp_principal);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_donpepe, @dp_gas, @alm_dp_frio, 45.00, 45.00, 0.00, 3.50, 1, '2026-02-05 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_donpepe AND producto_id = @dp_gas AND almacen_id = @alm_dp_frio);

-- ── LA BODEGA ──
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_labodega, @lb_ron, @alm_lb_sanisidro, 55.00, 52.00, 3.00, 27.00, 1, '2026-01-08 08:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_labodega AND producto_id = @lb_ron AND almacen_id = @alm_lb_sanisidro);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_labodega, @lb_cer, @alm_lb_sanisidro, 210.00, 200.00, 10.00, 3.50, 1, '2026-01-12 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_labodega AND producto_id = @lb_cer AND almacen_id = @alm_lb_sanisidro);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_labodega, @lb_vin, @alm_lb_miraflor, 28.00, 28.00, 0.00, 40.00, 1, '2026-01-18 10:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_labodega AND producto_id = @lb_vin AND almacen_id = @alm_lb_miraflor);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_labodega, @lb_snk, @alm_lb_sanisidro, 55.00, 55.00, 0.00, 4.00, 1, '2026-02-03 08:35:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_labodega AND producto_id = @lb_snk AND almacen_id = @alm_lb_sanisidro);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_labodega, @lb_gas, @alm_lb_sanisidro, 30.00, 30.00, 0.00, 3.20, 1, '2026-02-08 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_labodega AND producto_id = @lb_gas AND almacen_id = @alm_lb_sanisidro);

-- ── EL IMPERIO ──
INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_elimperio, @ei_ron, @alm_ei_general, 140.00, 130.00, 10.00, 26.50, 1, '2026-01-05 07:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_elimperio AND producto_id = @ei_ron AND almacen_id = @alm_ei_general);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_elimperio, @ei_cer, @alm_ei_frio, 750.00, 730.00, 20.00, 3.24, 1, '2026-01-10 08:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_elimperio AND producto_id = @ei_cer AND almacen_id = @alm_ei_frio);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_elimperio, @ei_vin, @alm_ei_general, 48.00, 48.00, 0.00, 36.00, 1, '2026-01-15 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_elimperio AND producto_id = @ei_vin AND almacen_id = @alm_ei_general);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_elimperio, @ei_snk, @alm_ei_general, 100.00, 100.00, 0.00, 3.80, 1, '2026-01-22 08:35:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_elimperio AND producto_id = @ei_snk AND almacen_id = @alm_ei_general);

INSERT INTO stock_inventario (negocio_id, producto_id, almacen_id, cantidad_actual, cantidad_disponible, cantidad_reservada, costo_promedio, esta_activo, creado_en)
SELECT @n_elimperio, @ei_gas, @alm_ei_frio, 70.00, 70.00, 0.00, 3.00, 1, '2026-01-28 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE negocio_id = @n_elimperio AND producto_id = @ei_gas AND almacen_id = @alm_ei_frio);


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  4. MOVIMIENTOS DE INVENTARIO                               ║
-- ║  Columnas: negocio_id, producto_id, almacen_origen_id,      ║
-- ║    almacen_destino_id, lote_id, tipo_movimiento, cantidad,  ║
-- ║    costo_unitario, monto_total, motivo_movimiento,          ║
-- ║    referencia_documento, usuario_id, fecha_movimiento       ║
-- ║  Enum: entrada|salida|transferencia|ajuste_positivo|        ║
-- ║        ajuste_negativo|devolucion|merma                     ║
-- ╚══════════════════════════════════════════════════════════════╝

-- Variables de lotes para subqueries
SET @lt_dp_001 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_donpepe   AND numero_lote = 'DP-LT-2026-001' LIMIT 1);
SET @lt_dp_002 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_donpepe   AND numero_lote = 'DP-LT-2026-002' LIMIT 1);
SET @lt_dp_003 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_donpepe   AND numero_lote = 'DP-LT-2026-003' LIMIT 1);
SET @lt_dp_004 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_donpepe   AND numero_lote = 'DP-LT-2026-004' LIMIT 1);
SET @lt_dp_005 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_donpepe   AND numero_lote = 'DP-LT-2026-005' LIMIT 1);
SET @lt_dp_006 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_donpepe   AND numero_lote = 'DP-LT-2026-006' LIMIT 1);

SET @lt_lb_001 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_labodega  AND numero_lote = 'LB-LT-2026-001' LIMIT 1);
SET @lt_lb_002 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_labodega  AND numero_lote = 'LB-LT-2026-002' LIMIT 1);
SET @lt_lb_003 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_labodega  AND numero_lote = 'LB-LT-2026-003' LIMIT 1);

SET @lt_ei_001 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-001' LIMIT 1);
SET @lt_ei_002 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-002' LIMIT 1);
SET @lt_ei_003 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-003' LIMIT 1);
SET @lt_ei_006 = (SELECT id FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'EI-LT-2026-006' LIMIT 1);

-- ── DON PEPE: Entradas iniciales ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_donpepe, @dp_ron, @alm_dp_principal, 'entrada',
    120.00, 28.50, 3420.00, 'Compra inicial de Ron Cartavio Black 750ml', 'OC-DP-2026-001',
    @usr_donpepe, '2026-01-10 09:00:00', @lt_dp_001, 1, '2026-01-10 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND referencia_documento = 'OC-DP-2026-001');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_donpepe, @dp_cer, @alm_dp_frio, 'entrada',
    240.00, 3.80, 912.00, 'Compra de Cerveza Pilsen Callao 630ml', 'OC-DP-2026-002',
    @usr_donpepe, '2026-01-15 10:00:00', @lt_dp_002, 1, '2026-01-15 10:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND referencia_documento = 'OC-DP-2026-002');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_donpepe, @dp_vin, @alm_dp_principal, 'entrada',
    48.00, 38.00, 1824.00, 'Compra de Vino Casillero del Diablo', 'OC-DP-2026-003',
    @usr_donpepe, '2026-01-20 11:00:00', @lt_dp_003, 1, '2026-01-20 11:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND referencia_documento = 'OC-DP-2026-003');

-- Don Pepe: Ventas (salidas)
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_donpepe, @dp_ron, @alm_dp_principal, 'salida',
    25.00, 28.50, 712.50, 'Venta al público - Enero', 'VTA-DP-2026-015',
    @usr_donpepe, '2026-01-25 16:00:00', @lt_dp_001, 1, '2026-01-25 16:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND referencia_documento = 'VTA-DP-2026-015');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_donpepe, @dp_cer, @alm_dp_frio, 'salida',
    60.00, 3.80, 228.00, 'Venta al público - Febrero', 'VTA-DP-2026-042',
    @usr_donpepe, '2026-02-10 18:00:00', @lt_dp_002, 1, '2026-02-10 18:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND referencia_documento = 'VTA-DP-2026-042');

-- Don Pepe: Reposición
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_donpepe, @dp_ron, @alm_dp_principal, 'entrada',
    60.00, 29.00, 1740.00, 'Reposición de Ron Cartavio - Lote nuevo', 'OC-DP-2026-008',
    @usr_donpepe, '2026-02-20 10:00:00', @lt_dp_006, 1, '2026-02-20 10:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND referencia_documento = 'OC-DP-2026-008');

-- Don Pepe: Merma
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_donpepe, @dp_snk, @alm_dp_principal, 'merma',
    3.00, 4.20, 12.60, 'Productos dañados durante almacenamiento', 'MRM-DP-2026-001',
    @usr_donpepe, '2026-02-18 14:00:00', @lt_dp_004, 1, '2026-02-18 14:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND referencia_documento = 'MRM-DP-2026-001');

-- ── LA BODEGA: Entradas iniciales ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_labodega, @lb_ron, @alm_lb_sanisidro, 'entrada',
    80.00, 27.00, 2160.00, 'Compra inicial Ron Cartavio', 'OC-LB-2026-001',
    @usr_labodega, '2026-01-08 08:00:00', @lt_lb_001, 1, '2026-01-08 08:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_labodega AND referencia_documento = 'OC-LB-2026-001');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_labodega, @lb_cer, @alm_lb_sanisidro, 'entrada',
    300.00, 3.50, 1050.00, 'Compra Cerveza Pilsen Callao', 'OC-LB-2026-002',
    @usr_labodega, '2026-01-12 09:00:00', @lt_lb_002, 1, '2026-01-12 09:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_labodega AND referencia_documento = 'OC-LB-2026-002');

-- La Bodega: Ventas
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_labodega, @lb_ron, @alm_lb_sanisidro, 'salida',
    25.00, 27.00, 675.00, 'Ventas Enero - Ron', 'VTA-LB-2026-020',
    @usr_labodega, '2026-01-30 17:00:00', @lt_lb_001, 1, '2026-01-30 17:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_labodega AND referencia_documento = 'VTA-LB-2026-020');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_labodega, @lb_cer, @alm_lb_sanisidro, 'salida',
    90.00, 3.50, 315.00, 'Ventas Febrero - Cerveza', 'VTA-LB-2026-055',
    @usr_labodega, '2026-02-15 19:00:00', @lt_lb_002, 1, '2026-02-15 19:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_labodega AND referencia_documento = 'VTA-LB-2026-055');

-- ── EL IMPERIO: Entradas iniciales ──
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_elimperio, @ei_ron, @alm_ei_general, 'entrada',
    200.00, 26.50, 5300.00, 'Compra mayorista Ron Cartavio', 'OC-EI-2026-001',
    @usr_elimperio, '2026-01-05 07:00:00', @lt_ei_001, 1, '2026-01-05 07:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND referencia_documento = 'OC-EI-2026-001');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_elimperio, @ei_cer, @alm_ei_frio, 'entrada',
    500.00, 3.20, 1600.00, 'Compra mayorista Cerveza Pilsen', 'OC-EI-2026-002',
    @usr_elimperio, '2026-01-10 08:00:00', @lt_ei_002, 1, '2026-01-10 08:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND referencia_documento = 'OC-EI-2026-002');

-- El Imperio: Ventas mayoristas
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_elimperio, @ei_ron, @alm_ei_general, 'salida',
    60.00, 26.50, 1590.00, 'Despacho a clientes mayoristas - Enero', 'VTA-EI-2026-010',
    @usr_elimperio, '2026-01-20 15:00:00', @lt_ei_001, 1, '2026-01-20 15:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND referencia_documento = 'VTA-EI-2026-010');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_elimperio, @ei_cer, @alm_ei_frio, 'salida',
    150.00, 3.20, 480.00, 'Despacho a bodegas - Febrero', 'VTA-EI-2026-030',
    @usr_elimperio, '2026-02-05 14:00:00', @lt_ei_002, 1, '2026-02-05 14:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND referencia_documento = 'VTA-EI-2026-030');

-- El Imperio: Reposición
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_elimperio, @ei_cer, @alm_ei_frio, 'entrada',
    400.00, 3.30, 1320.00, 'Reposición Cerveza Pilsen - Segundo lote', 'OC-EI-2026-005',
    @usr_elimperio, '2026-02-15 08:00:00', @lt_ei_006, 1, '2026-02-15 08:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND referencia_documento = 'OC-EI-2026-005');

-- El Imperio: Ajuste positivo (inventario físico encontró más)
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_origen_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total, motivo_movimiento, referencia_documento,
    usuario_id, fecha_movimiento, lote_id, esta_activo, creado_en)
SELECT @n_elimperio, @ei_vin, @alm_ei_general, 'ajuste_positivo',
    3.00, 36.00, 108.00, 'Inventario físico - se encontraron 3 botellas adicionales', 'AJP-EI-2026-001',
    @usr_elimperio, '2026-02-22 11:00:00', @lt_ei_003, 1, '2026-02-22 11:05:00'
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND referencia_documento = 'AJP-EI-2026-001');
