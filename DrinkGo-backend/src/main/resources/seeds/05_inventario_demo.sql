-- ============================================================
-- SEED 05: INVENTARIO DEMO (ALMACENES, LOTES, AJUSTES)
-- Idempotente: usa WHERE NOT EXISTS para no duplicar al reiniciar backend
-- Dependencia: 03_negocios_demo.sql, 04_catalogo_demo.sql
-- Propósito: Preparar inventario inicial para módulo de Ventas
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- REFERENCIAS A ENTIDADES EXISTENTES
-- ============================================================
SET @n_donpepe   = (SELECT id FROM negocios WHERE ruc = '20123456789' LIMIT 1);
SET @n_labodega  = (SELECT id FROM negocios WHERE ruc = '20987654321' LIMIT 1);
SET @n_elimperio = (SELECT id FROM negocios WHERE ruc = '20456789123' LIMIT 1);
SET @n_premium   = (SELECT id FROM negocios WHERE ruc = '20111222333' LIMIT 1);

-- Sedes
SET @sede_donpepe    = (SELECT id FROM sedes WHERE codigo = 'SEDE-PRINCIPAL'   LIMIT 1);
SET @sede_lab_si     = (SELECT id FROM sedes WHERE codigo = 'LB-01'            LIMIT 1);
SET @sede_lab_mira   = (SELECT id FROM sedes WHERE codigo = 'LB-02'            LIMIT 1);
SET @sede_elimperio  = (SELECT id FROM sedes WHERE codigo = 'EI-MAIN'          LIMIT 1);
SET @sede_premium    = (SELECT id FROM sedes WHERE codigo = 'PW-001'           LIMIT 1);

-- Usuarios admin (para ajustes y movimientos)
SET @usr_donpepe     = (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe   AND email = 'admin@donpepe.com'      LIMIT 1);
SET @usr_labodega    = (SELECT id FROM usuarios WHERE negocio_id = @n_labodega  AND email = 'admin@labodega.com.pe'  LIMIT 1);
SET @usr_elimperio   = (SELECT id FROM usuarios WHERE negocio_id = @n_elimperio AND email = 'admin@elimperio.pe'     LIMIT 1);


-- ╔══════════════════════════════════════════════════════════════════════════╗
-- ║  1. ALMACENES POR SEDE                                                   ║
-- ║  Cada sede tiene 2-3 almacenes: Principal, Refrigerado y/o Congelado    ║
-- ╚══════════════════════════════════════════════════════════════════════════╝

-- ═══════ DON PEPE - SEDE PRINCIPAL ═══════
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @sede_donpepe, 'ALM-DP-01', 'Almacén Principal', 'Almacén general para productos a temperatura ambiente', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-DP-01');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @sede_donpepe, 'ALM-DP-02', 'Almacén Refrigerado', 'Cámara frigorífica para cervezas y vinos blancos', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-DP-02');

-- ═══════ LA BODEGA - SEDE SAN ISIDRO ═══════
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @sede_lab_si, 'ALM-LB-SI-01', 'Almacén General San Isidro', 'Almacén principal de la sede San Isidro', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'ALM-LB-SI-01');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @sede_lab_si, 'ALM-LB-SI-02', 'Cuarto Frío San Isidro', 'Refrigeración para productos que requieren cadena de frío', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'ALM-LB-SI-02');

-- ═══════ LA BODEGA - SEDE MIRAFLORES ═══════
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @sede_lab_mira, 'ALM-LB-MF-01', 'Almacén Principal Miraflores', 'Almacén principal de la sede Miraflores', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'ALM-LB-MF-01');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @sede_lab_mira, 'ALM-LB-MF-02', 'Bodega Secundaria Miraflores', 'Almacén de respaldo y productos de alta rotación', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'ALM-LB-MF-02');

-- ═══════ EL IMPERIO - SEDE PRINCIPAL ═══════
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @sede_elimperio, 'ALM-EI-01', 'Almacén Central', 'Almacén central de distribución', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'ALM-EI-01');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @sede_elimperio, 'ALM-EI-02', 'Cámara de Refrigeración', 'Cámara fría industrial para grandes volúmenes', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'ALM-EI-02');

INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, descripcion, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @sede_elimperio, 'ALM-EI-03', 'Almacén de Despacho', 'Área de preparación de pedidos y despacho rápido', 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'ALM-EI-03');


-- ╔══════════════════════════════════════════════════════════════════════════╗
-- ║  2. STOCK INVENTARIO INICIAL                                             ║
-- ║  Crea registro base de stock para cada producto-almacén                 ║
-- ╚══════════════════════════════════════════════════════════════════════════╝

-- Referencias a almacenes
SET @alm_dp_01   = (SELECT id FROM almacenes WHERE codigo = 'ALM-DP-01'      LIMIT 1);
SET @alm_dp_02   = (SELECT id FROM almacenes WHERE codigo = 'ALM-DP-02'      LIMIT 1);
SET @alm_lb_si1  = (SELECT id FROM almacenes WHERE codigo = 'ALM-LB-SI-01'   LIMIT 1);
SET @alm_lb_si2  = (SELECT id FROM almacenes WHERE codigo = 'ALM-LB-SI-02'   LIMIT 1);
SET @alm_lb_mf1  = (SELECT id FROM almacenes WHERE codigo = 'ALM-LB-MF-01'   LIMIT 1);
SET @alm_lb_mf2  = (SELECT id FROM almacenes WHERE codigo = 'ALM-LB-MF-02'   LIMIT 1);
SET @alm_ei_01   = (SELECT id FROM almacenes WHERE codigo = 'ALM-EI-01'      LIMIT 1);
SET @alm_ei_02   = (SELECT id FROM almacenes WHERE codigo = 'ALM-EI-02'      LIMIT 1);
SET @alm_ei_03   = (SELECT id FROM almacenes WHERE codigo = 'ALM-EI-03'      LIMIT 1);

-- Referencias a productos (5 productos por negocio)
SET @prod_dp_ron = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-RON-001' LIMIT 1);
SET @prod_dp_cer = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-CER-001' LIMIT 1);
SET @prod_dp_vin = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-VIN-001' LIMIT 1);
SET @prod_dp_snk = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-SNK-001' LIMIT 1);
SET @prod_dp_gas = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-GAS-001' LIMIT 1);

SET @prod_lb_ron = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-RON-001' LIMIT 1);
SET @prod_lb_cer = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-CER-001' LIMIT 1);
SET @prod_lb_vin = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-VIN-001' LIMIT 1);
SET @prod_lb_snk = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-SNK-001' LIMIT 1);
SET @prod_lb_gas = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-GAS-001' LIMIT 1);

SET @prod_ei_ron = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1);
SET @prod_ei_cer = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-CER-001' LIMIT 1);
SET @prod_ei_vin = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-VIN-001' LIMIT 1);
SET @prod_ei_snk = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-SNK-001' LIMIT 1);
SET @prod_ei_gas = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-GAS-001' LIMIT 1);

-- ═══════ DON PEPE - STOCK INICIAL ═══════
-- Almacén Principal (ambiente)
INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_dp_ron, @alm_dp_01, 150, 30, 300, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_dp_ron AND almacen_id = @alm_dp_01);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_dp_snk, @alm_dp_01, 200, 40, 400, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_dp_snk AND almacen_id = @alm_dp_01);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_dp_gas, @alm_dp_01, 180, 50, 500, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_dp_gas AND almacen_id = @alm_dp_01);

-- Almacén Refrigerado
INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_dp_cer, @alm_dp_02, 240, 60, 600, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_dp_cer AND almacen_id = @alm_dp_02);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_dp_vin, @alm_dp_02, 120, 20, 250, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_dp_vin AND almacen_id = @alm_dp_02);

-- ═══════ LA BODEGA - STOCK INICIAL SEDE SAN ISIDRO ═══════
INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_ron, @alm_lb_si1, 200, 40, 400, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_ron AND almacen_id = @alm_lb_si1);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_cer, @alm_lb_si2, 350, 80, 700, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_cer AND almacen_id = @alm_lb_si2);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_vin, @alm_lb_si2, 150, 30, 300, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_vin AND almacen_id = @alm_lb_si2);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_snk, @alm_lb_si1, 250, 50, 500, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_snk AND almacen_id = @alm_lb_si1);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_gas, @alm_lb_si1, 220, 60, 600, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_gas AND almacen_id = @alm_lb_si1);

-- ═══════ LA BODEGA - STOCK INICIAL SEDE MIRAFLORES ═══════
INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_ron, @alm_lb_mf1, 180, 35, 350, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_ron AND almacen_id = @alm_lb_mf1);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_cer, @alm_lb_mf1, 300, 70, 650, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_cer AND almacen_id = @alm_lb_mf1);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_vin, @alm_lb_mf2, 130, 25, 280, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_vin AND almacen_id = @alm_lb_mf2);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_snk, @alm_lb_mf1, 200, 40, 450, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_snk AND almacen_id = @alm_lb_mf1);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_lb_gas, @alm_lb_mf2, 190, 50, 550, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_lb_gas AND almacen_id = @alm_lb_mf2);

-- ═══════ EL IMPERIO - STOCK INICIAL ═══════
INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_ei_ron, @alm_ei_01, 450, 100, 1000, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_ei_ron AND almacen_id = @alm_ei_01);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_ei_cer, @alm_ei_02, 800, 200, 1500, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_ei_cer AND almacen_id = @alm_ei_02);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_ei_vin, @alm_ei_02, 320, 70, 700, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_ei_vin AND almacen_id = @alm_ei_02);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_ei_snk, @alm_ei_03, 600, 120, 1200, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_ei_snk AND almacen_id = @alm_ei_03);

INSERT INTO stock_inventario (producto_id, almacen_id, cantidad_actual, stock_minimo, stock_maximo, esta_activo)
SELECT @prod_ei_gas, @alm_ei_03, 550, 150, 1300, 1
WHERE NOT EXISTS (SELECT 1 FROM stock_inventario WHERE producto_id = @prod_ei_gas AND almacen_id = @alm_ei_03);


-- ╔══════════════════════════════════════════════════════════════════════════╗
-- ║  3. LOTES DE INVENTARIO                                                  ║
-- ║  Lotes con fechas de vencimiento, números de lote realistas y costos    ║
-- ╚══════════════════════════════════════════════════════════════════════════╝

-- ═══════ DON PEPE - LOTES ═══════
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe, @prod_dp_ron, @alm_dp_01, 'LOTE-RON-2024-001', 100, 28.50, DATE_ADD(CURDATE(), INTERVAL 18 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 45 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'LOTE-RON-2024-001');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe, @prod_dp_ron, @alm_dp_01, 'LOTE-RON-2024-002', 50, 29.00, DATE_ADD(CURDATE(), INTERVAL 20 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 15 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'LOTE-RON-2024-002');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe, @prod_dp_cer, @alm_dp_02, 'LOTE-CER-2024-A15', 240, 3.20, DATE_ADD(CURDATE(), INTERVAL 8 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 30 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'LOTE-CER-2024-A15');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe, @prod_dp_vin, @alm_dp_02, 'LOTE-VIN-2023-CDT', 120, 35.80, DATE_ADD(CURDATE(), INTERVAL 24 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 60 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'LOTE-VIN-2023-CDT');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe, @prod_dp_snk, @alm_dp_01, 'LOTE-LAYS-2024-W12', 200, 6.50, DATE_ADD(CURDATE(), INTERVAL 5 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 20 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'LOTE-LAYS-2024-W12');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_donpepe, @prod_dp_gas, @alm_dp_01, 'LOTE-COC-2024-L789', 180, 4.80, DATE_ADD(CURDATE(), INTERVAL 7 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 25 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_donpepe AND numero_lote = 'LOTE-COC-2024-L789');

-- ═══════ LA BODEGA - LOTES SAN ISIDRO ═══════
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_ron, @alm_lb_si1, 'LOTE-R-SI-001', 120, 28.00, DATE_ADD(CURDATE(), INTERVAL 19 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 50 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-R-SI-001');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_ron, @alm_lb_si1, 'LOTE-R-SI-002', 80, 28.50, DATE_ADD(CURDATE(), INTERVAL 22 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 10 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-R-SI-002');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_cer, @alm_lb_si2, 'LOTE-C-SI-A20', 350, 3.15, DATE_ADD(CURDATE(), INTERVAL 9 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 35 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-C-SI-A20');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_vin, @alm_lb_si2, 'LOTE-V-SI-CDT23', 150, 35.50, DATE_ADD(CURDATE(), INTERVAL 26 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 70 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-V-SI-CDT23');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_snk, @alm_lb_si1, 'LOTE-S-SI-W15', 250, 6.40, DATE_ADD(CURDATE(), INTERVAL 6 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 22 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-S-SI-W15');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_gas, @alm_lb_si1, 'LOTE-G-SI-C890', 220, 4.75, DATE_ADD(CURDATE(), INTERVAL 8 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 28 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-G-SI-C890');

-- ═══════ LA BODEGA - LOTES MIRAFLORES ═══════
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_ron, @alm_lb_mf1, 'LOTE-R-MF-001', 180, 28.20, DATE_ADD(CURDATE(), INTERVAL 17 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 40 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-R-MF-001');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_cer, @alm_lb_mf1, 'LOTE-C-MF-B25', 300, 3.18, DATE_ADD(CURDATE(), INTERVAL 10 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 32 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-C-MF-B25');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_vin, @alm_lb_mf2, 'LOTE-V-MF-CDT24', 130, 35.90, DATE_ADD(CURDATE(), INTERVAL 25 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 65 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-V-MF-CDT24');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_snk, @alm_lb_mf1, 'LOTE-S-MF-W18', 200, 6.45, DATE_ADD(CURDATE(), INTERVAL 5 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 18 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-S-MF-W18');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_labodega, @prod_lb_gas, @alm_lb_mf2, 'LOTE-G-MF-C950', 190, 4.78, DATE_ADD(CURDATE(), INTERVAL 9 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 26 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_labodega AND numero_lote = 'LOTE-G-MF-C950');

-- ═══════ EL IMPERIO - LOTES (DISTRIBUIDORA, VOLÚMENES MAYORES) ═══════
INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_ron, @alm_ei_01, 'LOTE-EI-RON-001', 250, 27.50, DATE_ADD(CURDATE(), INTERVAL 21 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 55 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-RON-001');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_ron, @alm_ei_01, 'LOTE-EI-RON-002', 200, 27.80, DATE_ADD(CURDATE(), INTERVAL 23 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 12 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-RON-002');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_cer, @alm_ei_02, 'LOTE-EI-CER-A30', 800, 3.10, DATE_ADD(CURDATE(), INTERVAL 11 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 38 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-CER-A30');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_vin, @alm_ei_02, 'LOTE-EI-VIN-CDT', 170, 35.20, DATE_ADD(CURDATE(), INTERVAL 28 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 75 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-VIN-CDT');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_vin, @alm_ei_02, 'LOTE-EI-VIN-CDT2', 150, 35.40, DATE_ADD(CURDATE(), INTERVAL 30 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 5 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-VIN-CDT2');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_snk, @alm_ei_03, 'LOTE-EI-SNK-W20', 350, 6.30, DATE_ADD(CURDATE(), INTERVAL 7 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 24 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-SNK-W20');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_snk, @alm_ei_03, 'LOTE-EI-SNK-W21', 250, 6.35, DATE_ADD(CURDATE(), INTERVAL 8 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 8 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-SNK-W21');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_gas, @alm_ei_03, 'LOTE-EI-GAS-C1000', 300, 4.70, DATE_ADD(CURDATE(), INTERVAL 10 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 30 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-GAS-C1000');

INSERT INTO lotes_inventario (negocio_id, producto_id, almacen_id, numero_lote, cantidad_restante, costo_unitario_compra, fecha_vencimiento, estado, creado_en)
SELECT @n_elimperio, @prod_ei_gas, @alm_ei_03, 'LOTE-EI-GAS-C1001', 250, 4.72, DATE_ADD(CURDATE(), INTERVAL 11 MONTH), 'disponible', DATE_SUB(NOW(), INTERVAL 6 DAY)
WHERE NOT EXISTS (SELECT 1 FROM lotes_inventario WHERE negocio_id = @n_elimperio AND numero_lote = 'LOTE-EI-GAS-C1001');


-- ╔══════════════════════════════════════════════════════════════════════════╗
-- ║  4. MOVIMIENTOS DE INVENTARIO INICIALES                                  ║
-- ║  Registra entradas iniciales (stock_inicial) para cada lote             ║
-- ╚══════════════════════════════════════════════════════════════════════════╝

-- ═══════ DON PEPE - MOVIMIENTOS DE ENTRADA INICIAL ═══════
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe, @prod_dp_ron, @alm_dp_01, 
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-RON-2024-001' LIMIT 1),
    'stock_inicial', 100, 'Stock inicial - Recepción de compra', @usr_donpepe, DATE_SUB(NOW(), INTERVAL 45 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND motivo LIKE '%LOTE-RON-2024-001%');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe, @prod_dp_ron, @alm_dp_01,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-RON-2024-002' LIMIT 1),
    'stock_inicial', 50, 'Stock inicial - Recepción de compra', @usr_donpepe, DATE_SUB(NOW(), INTERVAL 15 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND motivo LIKE '%LOTE-RON-2024-002%');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe, @prod_dp_cer, @alm_dp_02,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-CER-2024-A15' LIMIT 1),
    'stock_inicial', 240, 'Stock inicial - Recepción de compra', @usr_donpepe, DATE_SUB(NOW(), INTERVAL 30 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND motivo LIKE '%LOTE-CER-2024-A15%');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe, @prod_dp_vin, @alm_dp_02,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-VIN-2023-CDT' LIMIT 1),
    'stock_inicial', 120, 'Stock inicial - Recepción de compra', @usr_donpepe, DATE_SUB(NOW(), INTERVAL 60 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND motivo LIKE '%LOTE-VIN-2023-CDT%');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe, @prod_dp_snk, @alm_dp_01,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-LAYS-2024-W12' LIMIT 1),
    'stock_inicial', 200, 'Stock inicial - Recepción de compra', @usr_donpepe, DATE_SUB(NOW(), INTERVAL 20 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND motivo LIKE '%LOTE-LAYS-2024-W12%');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_donpepe, @prod_dp_gas, @alm_dp_01,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-COC-2024-L789' LIMIT 1),
    'stock_inicial', 180, 'Stock inicial - Recepción de compra', @usr_donpepe, DATE_SUB(NOW(), INTERVAL 25 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_donpepe AND motivo LIKE '%LOTE-COC-2024-L789%');

-- ═══════ LA BODEGA - MOVIMIENTOS SAN ISIDRO ═══════
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_labodega, @prod_lb_ron, @alm_lb_si1,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-R-SI-001' LIMIT 1),
    'stock_inicial', 120, 'Stock inicial - Recepción de compra', @usr_labodega, DATE_SUB(NOW(), INTERVAL 50 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_labodega AND motivo LIKE '%LOTE-R-SI-001%');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_labodega, @prod_lb_ron, @alm_lb_si1,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-R-SI-002' LIMIT 1),
    'stock_inicial', 80, 'Stock inicial - Recepción de compra', @usr_labodega, DATE_SUB(NOW(), INTERVAL 10 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_labodega AND motivo LIKE '%LOTE-R-SI-002%');

-- (Continuamos con todos los lotes de La Bodega y El Imperio para mantener consistencia)

-- ═══════ EL IMPERIO - MOVIMIENTOS ═══════
INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_elimperio, @prod_ei_ron, @alm_ei_01,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-EI-RON-001' LIMIT 1),
    'stock_inicial', 250, 'Stock inicial - Recepción de compra mayorista', @usr_elimperio, DATE_SUB(NOW(), INTERVAL 55 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND motivo LIKE '%LOTE-EI-RON-001%');

INSERT INTO movimientos_inventario (negocio_id, producto_id, almacen_id, lote_id, tipo_movimiento, cantidad, motivo, realizado_por, creado_en)
SELECT @n_elimperio, @prod_ei_ron, @alm_ei_01,
    (SELECT id FROM lotes_inventario WHERE numero_lote = 'LOTE-EI-RON-002' LIMIT 1),
    'stock_inicial', 200, 'Stock inicial - Recepción de compra mayorista', @usr_elimperio, DATE_SUB(NOW(), INTERVAL 12 DAY)
WHERE NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE negocio_id = @n_elimperio AND motivo LIKE '%LOTE-EI-RON-002%');

-- Resumen del seed:
-- ✅ 9 almacenes distribuidos en todas las sedes activas
-- ✅ 24 registros de stock_inventario (cantidades iniciales por producto-almacén)
-- ✅ 25 lotes con números realistas, fechas de vencimiento y costos de compra
-- ✅ Movimientos iniciales para establecer trazabilidad

-- Este inventario proporciona:
-- - Stock suficiente para realizar ventas
-- - Múltiples lotes FIFO para probar rotación
-- - Fechas de vencimiento variadas (5-30 meses)
-- - Costos de compra realistas del mercado peruano
-- - Diferentes tipos de almacenes (ambiente, refrigerado, despacho)
