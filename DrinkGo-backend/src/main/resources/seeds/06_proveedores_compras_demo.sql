-- ============================================================
-- SEED 06: PROVEEDORES Y COMPRAS DEMO
-- Idempotente: usa WHERE NOT EXISTS para no duplicar al reiniciar
-- Dependencia: 03_negocios_demo.sql, 04_catalogo_demo.sql,
--              05_inventario_demo.sql  (almacenes)
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
SET @sede_elimperio = (SELECT id FROM sedes WHERE codigo = 'EI-MAIN'        AND negocio_id = @n_elimperio LIMIT 1);

-- Almacenes
SET @alm_dp_principal = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe   AND codigo = 'DP-ALM-PRINCIPAL'  LIMIT 1);
SET @alm_lb_sanisidro = (SELECT id FROM almacenes WHERE negocio_id = @n_labodega  AND codigo = 'LB-ALM-SANISIDRO'  LIMIT 1);
SET @alm_ei_general   = (SELECT id FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-GENERAL'    LIMIT 1);

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
-- ║  1. PROVEEDORES  (3 por negocio × 3 negocios = 9)          ║
-- ║  Columnas: negocio_id, razon_social, nombre_comercial,      ║
-- ║    tipo_documento, numero_documento, direccion, telefono,   ║
-- ║    email, contacto_principal, telefono_contacto,            ║
-- ║    email_contacto, dias_credito, limite_credito,            ║
-- ║    observaciones                                            ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_donpepe, 'Destilería Cartavio S.A.C.', 'Cartavio', 'RUC', '20481076370',
    'Av. Industrial 2450, La Libertad', '044-432100', 'ventas@cartavio.com.pe',
    'Carlos Mendoza', '944-123456', 'cmendoza@cartavio.com.pe',
    30, '50000.00', 'Proveedor principal de rones y destilados', 1, '2026-01-05 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20481076370');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_donpepe, 'Backus y Johnston S.A.A.', 'Backus', 'RUC', '20100113610',
    'Jr. Chiclayo 594, Rímac, Lima', '01-4611000', 'distribuidores@backus.com.pe',
    'Martín Rojas', '951-234567', 'mrojas@backus.com.pe',
    15, '80000.00', 'Proveedor principal de cervezas', 1, '2026-01-05 10:30:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20100113610');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_donpepe, 'Distribuidora Viñas del Sur E.I.R.L.', 'Viñas del Sur', 'RUC', '20556677889',
    'Calle Las Uvas 180, Ica', '056-221100', 'pedidos@vinasdelsur.pe',
    'Ana Torres', '962-345678', 'atorres@vinasdelsur.pe',
    45, '30000.00', 'Importador de vinos chilenos y argentinos', 1, '2026-01-06 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20556677889');

-- ── LA BODEGA ──
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_labodega, 'Destilería Cartavio S.A.C.', 'Cartavio', 'RUC', '20481076371',
    'Av. Industrial 2450, La Libertad', '044-432101', 'ventas.lima@cartavio.com.pe',
    'Luis Paredes', '944-456789', 'lparedes@cartavio.com.pe',
    30, '40000.00', 'Proveedor de rones nacionales', 1, '2026-01-06 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20481076371');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_labodega, 'Backus y Johnston S.A.A.', 'Backus', 'RUC', '20100113611',
    'Jr. Chiclayo 594, Rímac, Lima', '01-4611001', 'cuentasclave@backus.com.pe',
    'Sandra Vera', '951-567890', 'svera@backus.com.pe',
    15, '60000.00', 'Cervecería principal, programa de fidelización', 1, '2026-01-06 08:30:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20100113611');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_labodega, 'Importaciones Gourmet Perú S.A.C.', 'Gourmet Perú', 'RUC', '20667788990',
    'Av. La Marina 3200, San Miguel, Lima', '01-5678900', 'compras@gourmetperu.com.pe',
    'Roberto Díaz', '973-456789', 'rdiaz@gourmetperu.com.pe',
    60, '25000.00', 'Importador de snacks y bebidas artesanales', 1, '2026-01-07 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20667788990');

-- ── EL IMPERIO ──
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_elimperio, 'Destilería Cartavio S.A.C.', 'Cartavio', 'RUC', '20481076372',
    'Av. Industrial 2450, La Libertad', '044-432102', 'mayoristas@cartavio.com.pe',
    'Diego Salazar', '944-789012', 'dsalazar@cartavio.com.pe',
    45, '120000.00', 'Contrato mayorista - precios especiales', 1, '2026-01-03 07:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20481076372');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_elimperio, 'Backus y Johnston S.A.A.', 'Backus', 'RUC', '20100113612',
    'Jr. Chiclayo 594, Rímac, Lima', '01-4611002', 'mayorista@backus.com.pe',
    'Pedro Gutiérrez', '951-890123', 'pgutierrez@backus.com.pe',
    30, '200000.00', 'Distribuidor mayorista - volumen alto', 1, '2026-01-03 07:30:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20100113612');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en)
SELECT @n_elimperio, 'Viña Concha y Toro Perú S.A.', 'Concha y Toro', 'RUC', '20778899001',
    'Av. Javier Prado Este 4600, Surco, Lima', '01-6789012', 'importaciones@conchaytoro.pe',
    'Patricia Morales', '984-567890', 'pmorales@conchaytoro.pe',
    30, '80000.00', 'Importador directo de vinos chilenos', 1, '2026-01-04 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20778899001');

-- Variables de proveedores
SET @prov_dp_cartavio  = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20481076370' LIMIT 1);
SET @prov_dp_backus    = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20100113610' LIMIT 1);
SET @prov_dp_vinas     = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20556677889' LIMIT 1);

SET @prov_lb_cartavio  = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20481076371' LIMIT 1);
SET @prov_lb_backus    = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20100113611' LIMIT 1);
SET @prov_lb_gourmet   = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20667788990' LIMIT 1);

SET @prov_ei_cartavio  = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20481076372' LIMIT 1);
SET @prov_ei_backus    = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20100113612' LIMIT 1);
SET @prov_ei_concha    = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20778899001' LIMIT 1);


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  2. PRODUCTOS PROVEEDOR  (relación producto ↔ proveedor)   ║
-- ║  Columnas: negocio_id, producto_id, proveedor_id,           ║
-- ║    sku_proveedor, precio_compra, tiempo_entrega_dias,       ║
-- ║    es_predeterminado                                        ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_donpepe, @dp_ron, @prov_dp_cartavio, 'CART-RON-BLK-750', 28.50, 3, 1, 1, '2026-01-05 10:10:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND producto_id = @dp_ron AND proveedor_id = @prov_dp_cartavio);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_donpepe, @dp_cer, @prov_dp_backus, 'BCK-PIL-630', 3.80, 1, 1, 1, '2026-01-05 10:20:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND producto_id = @dp_cer AND proveedor_id = @prov_dp_backus);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_donpepe, @dp_vin, @prov_dp_vinas, 'VS-CSLL-750', 38.00, 7, 1, 1, '2026-01-06 09:10:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND producto_id = @dp_vin AND proveedor_id = @prov_dp_vinas);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_donpepe, @dp_gas, @prov_dp_backus, 'BCK-GAS-1500', 3.50, 1, 1, 1, '2026-01-05 10:30:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND producto_id = @dp_gas AND proveedor_id = @prov_dp_backus);

-- ── LA BODEGA ──
INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_labodega, @lb_ron, @prov_lb_cartavio, 'CART-RON-BLK-750', 27.00, 3, 1, 1, '2026-01-06 08:10:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND producto_id = @lb_ron AND proveedor_id = @prov_lb_cartavio);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_labodega, @lb_cer, @prov_lb_backus, 'BCK-PIL-630', 3.50, 1, 1, 1, '2026-01-06 08:20:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND producto_id = @lb_cer AND proveedor_id = @prov_lb_backus);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_labodega, @lb_vin, @prov_lb_gourmet, 'GP-CSLL-750', 40.00, 10, 1, 1, '2026-01-07 09:10:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND producto_id = @lb_vin AND proveedor_id = @prov_lb_gourmet);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_labodega, @lb_snk, @prov_lb_gourmet, 'GP-SNK-MIX', 4.00, 5, 1, 1, '2026-01-07 09:20:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND producto_id = @lb_snk AND proveedor_id = @prov_lb_gourmet);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_labodega, @lb_gas, @prov_lb_backus, 'BCK-GAS-1500', 3.20, 1, 1, 1, '2026-01-06 08:30:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND producto_id = @lb_gas AND proveedor_id = @prov_lb_backus);

-- ── EL IMPERIO ──
INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_elimperio, @ei_ron, @prov_ei_cartavio, 'CART-RON-BLK-750', 26.50, 2, 1, 1, '2026-01-03 07:10:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND producto_id = @ei_ron AND proveedor_id = @prov_ei_cartavio);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_elimperio, @ei_cer, @prov_ei_backus, 'BCK-PIL-630', 3.20, 1, 1, 1, '2026-01-03 07:20:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND producto_id = @ei_cer AND proveedor_id = @prov_ei_backus);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_elimperio, @ei_vin, @prov_ei_concha, 'CYT-CSLL-750', 36.00, 5, 1, 1, '2026-01-04 08:10:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND producto_id = @ei_vin AND proveedor_id = @prov_ei_concha);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_elimperio, @ei_snk, @prov_ei_cartavio, 'CART-SNK-MIX', 3.80, 3, 0, 1, '2026-01-03 07:30:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND producto_id = @ei_snk AND proveedor_id = @prov_ei_cartavio);

INSERT INTO productos_proveedor (negocio_id, producto_id, proveedor_id, sku_proveedor, precio_compra,
    tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en)
SELECT @n_elimperio, @ei_gas, @prov_ei_backus, 'BCK-GAS-1500', 3.00, 1, 1, 1, '2026-01-03 07:40:00'
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND producto_id = @ei_gas AND proveedor_id = @prov_ei_backus);


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  3. ÓRDENES DE COMPRA                                       ║
-- ║  Columnas: negocio_id, numero_orden (UNIQUE), proveedor_id, ║
-- ║    sede_id, almacen_id, estado (pendiente|recibida|         ║
-- ║    cancelada), total, subtotal, impuestos, notas,           ║
-- ║    fecha_orden, usuario_id, creado_por                      ║
-- ║  ⚠ SIN esta_activo, SIN eliminado_en (NO soft delete)      ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_donpepe, 'OC-DP-2026-001', @prov_dp_cartavio, @sede_donpepe, @alm_dp_principal,
    'recibida', 3420.00, 615.60, 4035.60, 'Compra inicial de Ron Cartavio Black 750ml - 120 unidades',
    '2026-01-10 09:00:00', @usr_donpepe, @usr_donpepe, '2026-01-08 14:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-001');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_donpepe, 'OC-DP-2026-002', @prov_dp_backus, @sede_donpepe, @alm_dp_principal,
    'recibida', 1284.00, 231.12, 1515.12, 'Compra Cerveza Pilsen 630ml y Gaseosa 1.5L',
    '2026-01-15 10:00:00', @usr_donpepe, @usr_donpepe, '2026-01-13 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-002');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_donpepe, 'OC-DP-2026-003', @prov_dp_vinas, @sede_donpepe, @alm_dp_principal,
    'recibida', 1824.00, 328.32, 2152.32, 'Compra Vino Casillero del Diablo - 48 botellas',
    '2026-01-20 11:00:00', @usr_donpepe, @usr_donpepe, '2026-01-17 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-003');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_donpepe, 'OC-DP-2026-008', @prov_dp_cartavio, @sede_donpepe, @alm_dp_principal,
    'recibida', 1740.00, 313.20, 2053.20, 'Reposición de Ron Cartavio - 60 unidades',
    '2026-02-20 10:00:00', @usr_donpepe, @usr_donpepe, '2026-02-18 15:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-008');

-- Orden pendiente Don Pepe
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_donpepe, 'OC-DP-2026-012', @prov_dp_backus, @sede_donpepe, @alm_dp_principal,
    'pendiente', 1520.00, 273.60, 1793.60, 'Reposición cerveza y gaseosas - Marzo',
    '2026-03-01 08:00:00', @usr_donpepe, @usr_donpepe, '2026-02-28 16:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-012');

-- ── LA BODEGA ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_labodega, 'OC-LB-2026-001', @prov_lb_cartavio, @sede_labodega1, @alm_lb_sanisidro,
    'recibida', 2160.00, 388.80, 2548.80, 'Compra inicial Ron Cartavio - 80 unidades',
    '2026-01-08 08:00:00', @usr_labodega, @usr_labodega, '2026-01-06 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-LB-2026-001');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_labodega, 'OC-LB-2026-002', @prov_lb_backus, @sede_labodega1, @alm_lb_sanisidro,
    'recibida', 1203.60, 216.65, 1420.25, 'Cerveza Pilsen y gaseosas',
    '2026-01-12 09:00:00', @usr_labodega, @usr_labodega, '2026-01-10 14:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-LB-2026-002');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_labodega, 'OC-LB-2026-003', @prov_lb_gourmet, @sede_labodega1, @alm_lb_sanisidro,
    'recibida', 1760.00, 316.80, 2076.80, 'Vinos y snacks importados',
    '2026-01-18 10:00:00', @usr_labodega, @usr_labodega, '2026-01-15 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-LB-2026-003');

-- Orden cancelada La Bodega
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_labodega, 'OC-LB-2026-007', @prov_lb_cartavio, @sede_labodega1, @alm_lb_sanisidro,
    'cancelada', 1350.00, 243.00, 1593.00, 'CANCELADA - proveedor sin stock temporalmente',
    '2026-02-20 09:00:00', @usr_labodega, @usr_labodega, '2026-02-18 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-LB-2026-007');

-- ── EL IMPERIO ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_elimperio, 'OC-EI-2026-001', @prov_ei_cartavio, @sede_elimperio, @alm_ei_general,
    'recibida', 5300.00, 954.00, 6254.00, 'Compra mayorista Ron Cartavio - 200 unidades',
    '2026-01-05 07:00:00', @usr_elimperio, @usr_elimperio, '2026-01-03 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-001');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_elimperio, 'OC-EI-2026-002', @prov_ei_backus, @sede_elimperio, @alm_ei_general,
    'recibida', 1900.00, 342.00, 2242.00, 'Compra mayorista Cerveza y gaseosas',
    '2026-01-10 08:00:00', @usr_elimperio, @usr_elimperio, '2026-01-08 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-002');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_elimperio, 'OC-EI-2026-003', @prov_ei_concha, @sede_elimperio, @alm_ei_general,
    'recibida', 2160.00, 388.80, 2548.80, 'Compra vinos Casillero del Diablo - 60 botellas',
    '2026-01-15 09:00:00', @usr_elimperio, @usr_elimperio, '2026-01-12 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-003');

INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_elimperio, 'OC-EI-2026-005', @prov_ei_backus, @sede_elimperio, @alm_ei_general,
    'recibida', 1320.00, 237.60, 1557.60, 'Reposición Cerveza Pilsen - 400 unidades',
    '2026-02-15 08:00:00', @usr_elimperio, @usr_elimperio, '2026-02-13 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-005');

-- Orden pendiente El Imperio
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id,
    estado, subtotal, impuestos, total, notas, fecha_orden, usuario_id, creado_por, creado_en)
SELECT @n_elimperio, 'OC-EI-2026-008', @prov_ei_cartavio, @sede_elimperio, @alm_ei_general,
    'pendiente', 7950.00, 1431.00, 9381.00, 'Pedido grande de Ron - para evento corporativo',
    '2026-03-05 07:00:00', @usr_elimperio, @usr_elimperio, '2026-03-03 08:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-008');

-- Variables de órdenes
SET @oc_dp_001 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-001' LIMIT 1);
SET @oc_dp_002 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-002' LIMIT 1);
SET @oc_dp_003 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-003' LIMIT 1);
SET @oc_dp_008 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-008' LIMIT 1);
SET @oc_dp_012 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-DP-2026-012' LIMIT 1);

SET @oc_lb_001 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-LB-2026-001' LIMIT 1);
SET @oc_lb_002 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-LB-2026-002' LIMIT 1);
SET @oc_lb_003 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-LB-2026-003' LIMIT 1);

SET @oc_ei_001 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-001' LIMIT 1);
SET @oc_ei_002 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-002' LIMIT 1);
SET @oc_ei_003 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-003' LIMIT 1);
SET @oc_ei_005 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-005' LIMIT 1);
SET @oc_ei_008 = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-EI-2026-008' LIMIT 1);


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  4. DETALLE ÓRDENES DE COMPRA                               ║
-- ║  Columnas: orden_compra_id, producto_id, cantidad_solicitada║
-- ║    cantidad_recibida, precio_unitario, subtotal,            ║
-- ║    impuesto, total                                          ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE: OC-DP-2026-001 (Ron Cartavio, recibida) ──
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_001, @dp_ron, 120.00, 120.00, 28.50, 3420.00, 615.60, 4035.60, 1, '2026-01-08 14:05:00'
WHERE @oc_dp_001 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_001 AND producto_id = @dp_ron);

-- OC-DP-2026-002 (Cerveza + Gaseosa, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_002, @dp_cer, 240.00, 240.00, 3.80, 912.00, 164.16, 1076.16, 1, '2026-01-13 11:05:00'
WHERE @oc_dp_002 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_002 AND producto_id = @dp_cer);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_002, @dp_gas, 60.00, 60.00, 3.50, 210.00, 37.80, 247.80, 1, '2026-01-13 11:06:00'
WHERE @oc_dp_002 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_002 AND producto_id = @dp_gas);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_002, @dp_snk, 100.00, 100.00, 4.20, 420.00, 75.60, 495.60, 1, '2026-01-13 11:07:00'
WHERE @oc_dp_002 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_002 AND producto_id = @dp_snk);

-- OC-DP-2026-003 (Vino, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_003, @dp_vin, 48.00, 48.00, 38.00, 1824.00, 328.32, 2152.32, 1, '2026-01-17 09:05:00'
WHERE @oc_dp_003 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_003 AND producto_id = @dp_vin);

-- OC-DP-2026-008 (Reposición ron, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_008, @dp_ron, 60.00, 60.00, 29.00, 1740.00, 313.20, 2053.20, 1, '2026-02-18 15:05:00'
WHERE @oc_dp_008 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_008 AND producto_id = @dp_ron);

-- OC-DP-2026-012 (Pendiente, no recibida aún)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_012, @dp_cer, 300.00, 0.00, 3.80, 1140.00, 205.20, 1345.20, 1, '2026-02-28 16:05:00'
WHERE @oc_dp_012 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_012 AND producto_id = @dp_cer);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_dp_012, @dp_gas, 50.00, 0.00, 3.50, 175.00, 31.50, 206.50, 1, '2026-02-28 16:06:00'
WHERE @oc_dp_012 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_012 AND producto_id = @dp_gas);

-- ── LA BODEGA: OC-LB-2026-001 (Ron, recibida) ──
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_lb_001, @lb_ron, 80.00, 80.00, 27.00, 2160.00, 388.80, 2548.80, 1, '2026-01-06 10:05:00'
WHERE @oc_lb_001 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_001 AND producto_id = @lb_ron);

-- OC-LB-2026-002 (Cerveza + gaseosa, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_lb_002, @lb_cer, 300.00, 300.00, 3.50, 1050.00, 189.00, 1239.00, 1, '2026-01-10 14:05:00'
WHERE @oc_lb_002 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_002 AND producto_id = @lb_cer);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_lb_002, @lb_gas, 48.00, 48.00, 3.20, 153.60, 27.65, 181.25, 1, '2026-01-10 14:06:00'
WHERE @oc_lb_002 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_002 AND producto_id = @lb_gas);

-- OC-LB-2026-003 (Vino + snacks, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_lb_003, @lb_vin, 36.00, 36.00, 40.00, 1440.00, 259.20, 1699.20, 1, '2026-01-15 11:05:00'
WHERE @oc_lb_003 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_003 AND producto_id = @lb_vin);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_lb_003, @lb_snk, 80.00, 80.00, 4.00, 320.00, 57.60, 377.60, 1, '2026-01-15 11:06:00'
WHERE @oc_lb_003 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_003 AND producto_id = @lb_snk);

-- ── EL IMPERIO: OC-EI-2026-001 (Ron mayorista, recibida) ──
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_ei_001, @ei_ron, 200.00, 200.00, 26.50, 5300.00, 954.00, 6254.00, 1, '2026-01-03 09:05:00'
WHERE @oc_ei_001 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_001 AND producto_id = @ei_ron);

-- OC-EI-2026-002 (Cerveza + gaseosa, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_ei_002, @ei_cer, 500.00, 500.00, 3.20, 1600.00, 288.00, 1888.00, 1, '2026-01-08 10:05:00'
WHERE @oc_ei_002 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_002 AND producto_id = @ei_cer);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_ei_002, @ei_gas, 100.00, 100.00, 3.00, 300.00, 54.00, 354.00, 1, '2026-01-08 10:06:00'
WHERE @oc_ei_002 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_002 AND producto_id = @ei_gas);

-- OC-EI-2026-003 (Vinos, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_ei_003, @ei_vin, 60.00, 60.00, 36.00, 2160.00, 388.80, 2548.80, 1, '2026-01-12 11:05:00'
WHERE @oc_ei_003 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_003 AND producto_id = @ei_vin);

-- OC-EI-2026-005 (Reposición cerveza, recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_ei_005, @ei_cer, 400.00, 400.00, 3.30, 1320.00, 237.60, 1557.60, 1, '2026-02-13 09:05:00'
WHERE @oc_ei_005 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_005 AND producto_id = @ei_cer);

-- OC-EI-2026-008 (Pendiente, no recibida)
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total, esta_activo, creado_en)
SELECT @oc_ei_008, @ei_ron, 300.00, 0.00, 26.50, 7950.00, 1431.00, 9381.00, 1, '2026-03-03 08:05:00'
WHERE @oc_ei_008 IS NOT NULL
AND NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_008 AND producto_id = @ei_ron);
