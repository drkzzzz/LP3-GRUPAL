-- ============================================================
-- SEEDS DEMO: PROVEEDORES Y COMPRAS
-- ============================================================
-- Este archivo inicializa datos de demostración para el módulo de Compras:
--   - Proveedores por negocio
--   - Productos por proveedor con precios de compra
--   - Órdenes de compra con diferentes estados
--   - Detalle de órdenes con cantidades y precios
--
-- Instrucciones:
--   1. Ejecutar DESPUÉS de 04_catalogo_demo.sql y 05_inventario_demo.sql
--   2. Este script es idempotente (puede ejecutarse múltiples veces)
--   3. Usa WHERE NOT EXISTS para evitar duplicados
--
-- Fecha: Febrero 2026
-- ============================================================

-- ============================================================
-- 1. PROVEEDORES
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

-- Variables de almacenes
SET @alm_donpepe_principal  = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-PRINCIPAL' LIMIT 1);
SET @alm_donpepe_deposito   = (SELECT id FROM almacenes WHERE negocio_id = @n_donpepe AND codigo = 'ALM-DEPOSITO' LIMIT 1);
SET @alm_labodega_01        = (SELECT id FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB01-ALM-MAIN' LIMIT 1);
SET @alm_labodega_02        = (SELECT id FROM almacenes WHERE negocio_id = @n_labodega AND codigo = 'LB02-ALM-MAIN' LIMIT 1);
SET @alm_elimperio_general  = (SELECT id FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-GENERAL' LIMIT 1);
SET @alm_elimperio_frio     = (SELECT id FROM almacenes WHERE negocio_id = @n_elimperio AND codigo = 'EI-ALM-FRIO' LIMIT 1);
SET @alm_premium_main       = (SELECT id FROM almacenes WHERE negocio_id = @n_premium AND codigo = 'PW-ALM-MAIN' LIMIT 1);

-- ── DON PEPE - Proveedores ──
INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_donpepe, 'PROV-001', 'IMPORTADORA SAN MARTIN S.A.C.', 'Importadora San Martín',
    '20567891234', 'Av. Colonial 1234, Callao', '014567890', 'ventas@importadorasm.pe',
    1, 'Importación y distribución de licores premium', '2025-11-10 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-001');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_donpepe, 'PROV-002', 'DISTRIBUIDORA BACKUS Y JOHNSTON S.A.', 'Backus',
    '20100113610', 'Av. Nicolás Ayllón 3986, Ate', '016289000', 'clientes@backus.pe',
    1, 'Fabricación y distribución de cervezas', '2025-11-15 11:30:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-002');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_donpepe, 'PROV-003', 'CORPORACION INCA KOLA PERU S.R.L.', 'Inca Kola Perú',
    '20100132356', 'Av. República de Panamá 4050, Surquillo', '016157000', 'pedidos@incakola.pe',
    1, 'Fabricación y distribución de bebidas gaseosas', '2025-12-01 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-003');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_donpepe, 'PROV-004', 'SNACKS AMERICA LATINA S.R.L.', 'Lay\'s Perú',
    '20512345678', 'Jr. Morococha 1280, La Victoria', '014781234', 'contacto@layspe.com',
    1, 'Distribución de snacks y piqueos', '2025-12-05 14:20:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-004');

-- ── LA BODEGA - Proveedores ──
INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_labodega, 'PROV-LB-001', 'IMPORTADORA SAN MARTIN S.A.C.', 'Importadora San Martín',
    '20567891234', 'Av. Colonial 1234, Callao', '014567890', 'ventas@importadorasm.pe',
    1, 'Importación y distribución de licores premium', '2025-10-20 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-001');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_labodega, 'PROV-LB-002', 'DISTRIBUIDORA BACKUS Y JOHNSTON S.A.', 'Backus',
    '20100113610', 'Av. Nicolás Ayllón 3986, Ate', '016289000', 'clientes@backus.pe',
    1, 'Fabricación y distribución de cervezas', '2025-10-22 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-002');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_labodega, 'PROV-LB-003', 'VINOS DEL MUNDO S.A.', 'Vinos del Mundo',
    '20345678901', 'Calle Las Palmeras 890, San Isidro', '016421111', 'pedidos@vinosdelmundo.pe',
    1, 'Importación de vinos finos', '2025-11-01 15:30:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-003');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_labodega, 'PROV-LB-004', 'DISTRIBUIDORA ALIMENTOS Y BEBIDAS SAC', 'DISTRAL',
    '20678912345', 'Av. Javier Prado 5467, Surco', '016333444', 'ventas@distral.pe',
    1, 'Distribución mayorista de snacks y bebidas', '2025-11-10 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-004');

-- ── EL IMPERIO - Proveedores ──
INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_elimperio, 'PROV-EI-001', 'COMERCIALIZADORA DE LICORES PREMIUM S.A.', 'Licores Premium',
    '20789123456', 'Av. Argentina 2345, Callao', '014582222', 'contacto@licorespremium.pe',
    1, 'Distribución de licores importados', '2025-12-01 09:30:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND codigo = 'PROV-EI-001');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_elimperio, 'PROV-EI-002', 'DISTRIBUIDORA BACKUS Y JOHNSTON S.A.', 'Backus',
    '20100113610', 'Av. Nicolás Ayllón 3986, Ate', '016289000', 'clientes@backus.pe',
    1, 'Fabricación y distribución de cervezas', '2025-12-05 10:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND codigo = 'PROV-EI-002');

INSERT INTO proveedores (negocio_id, codigo, razon_social, nombre_comercial, ruc, direccion, 
    telefono, email, esta_activo, rubro, creado_en)
SELECT @n_elimperio, 'PROV-EI-003', 'VINOS SELECTOS DEL PERU S.A.C.', 'Vinos Selectos',
    '20891234567', 'Calle Las Begonias 456, San Isidro', '016789999', 'ordenes@vinosselectos.pe',
    1, 'Comercialización de vinos premium', '2025-12-08 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND codigo = 'PROV-EI-003');

-- Variables de proveedores creados
SET @prov_donpepe_001    = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-001' LIMIT 1);
SET @prov_donpepe_002    = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-002' LIMIT 1);
SET @prov_donpepe_003    = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-003' LIMIT 1);
SET @prov_donpepe_004    = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe AND codigo = 'PROV-004' LIMIT 1);

SET @prov_labodega_001   = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-001' LIMIT 1);
SET @prov_labodega_002   = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-002' LIMIT 1);
SET @prov_labodega_003   = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-003' LIMIT 1);
SET @prov_labodega_004   = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega AND codigo = 'PROV-LB-004' LIMIT 1);

SET @prov_elimperio_001  = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND codigo = 'PROV-EI-001' LIMIT 1);
SET @prov_elimperio_002  = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND codigo = 'PROV-EI-002' LIMIT 1);
SET @prov_elimperio_003  = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND codigo = 'PROV-EI-003' LIMIT 1);

-- ============================================================
-- 2. PRODUCTOS POR PROVEEDOR
-- ============================================================

-- ── DON PEPE - Productos por proveedor ──
-- Proveedor: Importadora San Martín (Rones y Vinos)
INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_donpepe_001,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    'ISM-RON-CART-750', 28.50, 3, 20, 1, '2025-11-10 10:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_donpepe_001 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1)
);

INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_donpepe_001,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    'ISM-VIN-CASI-750', 42.00, 5, 12, 1, '2025-11-10 10:35:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_donpepe_001 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1)
);

-- Proveedor: Backus (Cervezas)
INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_donpepe_002,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    'BACK-PIL-630', 3.20, 1, 24, 1, '2025-11-15 11:40:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_donpepe_002 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1)
);

-- Proveedor: Inca Kola (Gaseosas)
INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_donpepe_003,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    'COCA-150', 4.50, 2, 12, 1, '2025-12-01 09:15:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_donpepe_003 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1)
);

-- Proveedor: Lay's (Snacks)
INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_donpepe_004,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1),
    'LAYS-CLA-200', 5.80, 2, 20, 1, '2025-12-05 14:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_donpepe_004 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1)
);

-- ── LA BODEGA - Productos por proveedor ──
INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_labodega_001,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    'ISM-RON-CART-750', 28.80, 3, 20, 1, '2025-10-20 10:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_labodega_001 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1)
);

INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_labodega_002,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    'BACK-PIL-630', 3.22, 1, 24, 1, '2025-10-22 11:15:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_labodega_002 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1)
);

INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_labodega_003,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1),
    'VDM-CASI-CAB-750', 42.50, 7, 6, 1, '2025-11-01 15:45:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_labodega_003 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1)
);

INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_labodega_004,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1),
    'DIST-LAYS-200', 5.90, 2, 20, 1, '2025-11-10 10:20:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_labodega_004 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1)
);

INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_labodega_004,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1),
    'DIST-COCA-150', 4.55, 2, 12, 1, '2025-11-10 10:25:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_labodega_004 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1)
);

-- ── EL IMPERIO - Productos por proveedor ──
INSERT INTO productos_proveedor (proveedor_id, producto_id, sku_proveedor, precio_proveedor, 
    dias_tiempo_entrega, cantidad_minima_pedido, es_preferido, creado_en)
SELECT @prov_elimperio_001,
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    'LCP-CART-BLK-750', 28.40, 2, 24, 1, '2025-12-01 09:45:00'
WHERE NOT EXISTS (
    SELECT 1 FROM productos_proveedor 
    WHERE proveedor_id = @prov_elimperio_001 
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1)
);

-- ============================================================
-- 3. ÓRDENES DE COMPRA
-- ============================================================

-- ── DON PEPE - Orden 1: RECIBIDA (ya ingresó a inventario) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260115-001', @prov_donpepe_001, @sede_donpepe_main, @alm_donpepe_principal,
    'recibida', 5640.00, 'Primera compra de rones para stock inicial',
    (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe AND email = 'admin@donpepe.com' LIMIT 1),
    '2026-01-15 08:00:00', '2026-01-15 10:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260115-001'
);

-- __ DON PEPE - Orden 2: RECIBIDA (cervezas) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260210-001', @prov_donpepe_002, @sede_donpepe_main, @alm_donpepe_principal,
    'recibida', 907.20, 'Reposición cervezas para fin de semana',
    (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe AND email = 'admin@donpepe.com' LIMIT 1),
    '2026-02-10 07:30:00', '2026-02-10 09:15:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260210-001'
);

-- ── DON PEPE - Orden 3: PENDIENTE (mixta con varios productos) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260225-001', @prov_donpepe_001, @sede_donpepe_main, @alm_donpepe_principal,
    'pendiente', 5040.00, 'Pedido semanal - arribaría el 28/02',
    (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe AND email = 'admin@donpepe.com' LIMIT 1),
    '2026-02-25 10:00:00', '2026-02-25 10:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260225-001'
);

-- ── DON PEPE - Orden 4: PENDIENTE (snacks y bebidas) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260226-001', @prov_donpepe_003, @sede_donpepe_main, @alm_donpepe_principal,
    'pendiente', 1593.00, 'Abarrotes para la semana',
    (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe AND email = 'admin@donpepe.com' LIMIT 1),
    '2026-02-26 11:30:00', '2026-02-26 11:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260226-001'
);

-- ── LA BODEGA - Orden 1: RECIBIDA (Sede 01) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_labodega, 'OC-LB-20260205-001', @prov_labodega_001, @sede_labodega_01, @alm_labodega_01,
    'recibida', 3384.00, 'Stock inicial rones sede San Isidro',
    (SELECT id FROM usuarios WHERE negocio_id = @n_labodega AND email = 'admin@labodega.com.pe' LIMIT 1),
    '2026-02-05 09:00:00', '2026-02-05 11:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-LB-20260205-001'
);

-- ── LA BODEGA - Orden 2: RECIBIDA (Sede 01 - Cervezas) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_labodega, 'OC-LB-20260218-001', @prov_labodega_002, @sede_labodega_01, @alm_labodega_01,
    'recibida', 685.30, 'Reposición cervezas San Isidro',
    (SELECT id FROM usuarios WHERE negocio_id = @n_labodega AND email = 'admin@labodega.com.pe' LIMIT 1),
    '2026-02-18 08:00:00', '2026-02-18 09:30:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-LB-20260218-001'
);

-- ── LA BODEGA - Orden 3: PENDIENTE (Sede 02) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_labodega, 'OC-LB-20260227-001', @prov_labodega_003, @sede_labodega_02, @alm_labodega_02,
    'pendiente', 3187.50, 'Vinos especiales Miraflores - Campaña marzo',
    (SELECT id FROM usuarios WHERE negocio_id = @n_labodega AND email = 'admin@labodega.com.pe' LIMIT 1),
    '2026-02-27 14:00:00', '2026-02-27 14:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-LB-20260227-001'
);

-- ── LA BODEGA - Orden 4: PENDIENTE (Sede 01 - mixta) ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_labodega, 'OC-LB-20260228-001', @prov_labodega_004, @sede_labodega_01, @alm_labodega_01,
    'pendiente', 1239.50, 'Snacks y gaseosas San Isidro',
    (SELECT id FROM usuarios WHERE negocio_id = @n_labodega AND email = 'admin@labodega.com.pe' LIMIT 1),
    '2026-02-28 09:00:00', '2026-02-28 09:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-LB-20260228-001'
);

-- ── EL IMPERIO - Orden 1: RECIBIDA ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_elimperio, 'OC-EI-20260201-001', @prov_elimperio_001, @sede_elimperio_main, @alm_elimperio_general,
    'recibida', 4996.80, 'Stock inicial El Imperio',
    (SELECT id FROM usuarios WHERE negocio_id = @n_elimperio AND email = 'admin@elimperio.pe' LIMIT 1),
    '2026-02-01 10:00:00', '2026-02-01 12:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-EI-20260201-001'
);

-- ── EL IMPERIO - Orden 2: PENDIENTE ──
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, estado, 
    total, notas, creado_por, creado_en, actualizado_en)
SELECT @n_elimperio, 'OC-EI-20260226-001', @prov_elimperio_001, @sede_elimperio_main, @alm_elimperio_general,
    'pendiente', 3337.20, 'Reposición rones premium',
    (SELECT id FROM usuarios WHERE negocio_id = @n_elimperio AND email = 'admin@elimperio.pe' LIMIT 1),
    '2026-02-26 15:00:00', '2026-02-26 15:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-EI-20260226-001'
);

-- Variables de órdenes creadas
SET @orden_donpepe_001     = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-20260115-001' LIMIT 1);
SET @orden_donpepe_002     = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-20260210-001' LIMIT 1);
SET @orden_donpepe_003     = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-20260225-001' LIMIT 1);
SET @orden_donpepe_004     = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-20260226-001' LIMIT 1);

SET @orden_labodega_001    = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-LB-20260205-001' LIMIT 1);
SET @orden_labodega_002    = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-LB-20260218-001' LIMIT 1);
SET @orden_labodega_003    = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-LB-20260227-001' LIMIT 1);
SET @orden_labodega_004    = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-LB-20260228-001' LIMIT 1);

SET @orden_elimperio_001   = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-EI-20260201-001' LIMIT 1);
SET @orden_elimperio_002   = (SELECT id FROM ordenes_compra WHERE numero_orden = 'OC-EI-20260226-001' LIMIT 1);

-- ============================================================
-- 4. DETALLE DE ÓRDENES DE COMPRA
-- ============================================================

-- ═══════════════════════════════════════════════════════════
-- DON PEPE - Orden 1: OC-20260115-001 (RECIBIDA)
-- ═══════════════════════════════════════════════════════════
-- Producto: Ron Cartavio Black 750ml × 150 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_001,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    150, 150, 0, NULL, 28.50, 18.00, 768.30, 0.00, 4275.00, 5043.30, 'Lote LT-20260115-001'
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_001
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1)
);

-- Producto: Vino Casillero × 20 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_001,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    20, 20, 0, NULL, 42.00, 18.00, 151.20, 0.00, 840.00, 991.20, 'Lote LT-20260118-005'
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_001
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- DON PEPE - Orden 2: OC-20260210-001 (RECIBIDA)
-- ═══════════════════════════════════════════════════════════
-- Producto: Cerveza Pilsen × 240 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_002,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    240, 240, 0, NULL, 3.20, 18.00, 138.24, 0.00, 768.00, 906.24, 'Lote LT-20260210-003'
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_002
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- DON PEPE - Orden 3: OC-20260225-001 (PENDIENTE)
-- ═══════════════════════════════════════════════════════════
-- Producto: Ron Cartavio × 80 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_003,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    80, 0, 0, NULL, 29.00, 18.00, 417.60, 0.00, 2320.00, 2737.60, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_003
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1)
);

-- Producto: Vino Casillero × 40 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_003,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1),
    40, 0, 0, NULL, 42.00, 18.00, 302.40, 0.00, 1680.00, 1982.40, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_003
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001' LIMIT 1)
);

-- Producto: Cerveza Pilsen × 60 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_003,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    60, 0, 0, NULL, 3.25, 18.00, 35.10, 0.00, 195.00, 230.10, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_003
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- DON PEPE - Orden 4: OC-20260226-001 (PENDIENTE)
-- ═══════════════════════════════════════════════════════════
-- Producto: Coca-Cola 1.5L × 200 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_004,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    200, 0, 0, NULL, 4.50, 18.00, 162.00, 0.00, 900.00, 1062.00, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_004
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1)
);

-- Producto: Papitas Lay's × 80 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_donpepe_004,
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1),
    80, 0, 0, NULL, 5.80, 18.00, 83.52, 0.00, 464.00, 547.52, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_donpepe_004
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- LA BODEGA - Orden 1: OC-LB-20260205-001 (RECIBIDA)
-- ═══════════════════════════════════════════════════════════
-- Producto: Ron Cartavio × 100 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_labodega_001,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    100, 100, 0, NULL, 28.80, 18.00, 518.40, 0.00, 2880.00, 3398.40, 'Lote LT-20260205-011'
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_labodega_001
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- LA BODEGA - Orden 2: OC-LB-20260218-001 (RECIBIDA)
-- ═══════════════════════════════════════════════════════════
-- Producto: Cerveza Pilsen × 180 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_labodega_002,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    180, 180, 0, NULL, 3.22, 18.00, 104.33, 0.00, 579.60, 683.93, 'Lote LT-20260218-012'
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_labodega_002
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- LA BODEGA - Orden 3: OC-LB-20260227-001 (PENDIENTE)
-- ═══════════════════════════════════════════════════════════
-- Producto: Vino Casillero × 75 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_labodega_003,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1),
    75, 0, 0, NULL, 42.50, 18.00, 573.75, 0.00, 3187.50, 3761.25, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_labodega_003
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- LA BODEGA - Orden 4: OC-LB-20260228-001 (PENDIENTE)
-- ═══════════════════════════════════════════════════════════
-- Producto: Papitas Lay's × 120 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_labodega_004,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1),
    120, 0, 0, NULL, 5.90, 18.00, 127.44, 0.00, 708.00, 835.44, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_labodega_004
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1)
);

-- Producto: Coca-Cola 1.5L × 80 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_labodega_004,
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1),
    80, 0, 0, NULL, 4.55, 18.00, 65.52, 0.00, 364.00, 429.52, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_labodega_004
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- EL IMPERIO - Orden 1: OC-EI-20260201-001 (RECIBIDA)
-- ═══════════════════════════════════════════════════════════
-- Producto: Ron Cartavio × 150 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_elimperio_001,
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    150, 150, 0, NULL, 28.40, 18.00, 767.52, 0.00, 4260.00, 5027.52, 'Lote LT-20260201-019'
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_elimperio_001
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1)
);

-- ═══════════════════════════════════════════════════════════
-- EL IMPERIO - Orden 2: OC-EI-20260226-001 (PENDIENTE)
-- ═══════════════════════════════════════════════════════════
-- Producto: Ron Cartavio × 100 unidades
INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_ordenada, cantidad_recibida, 
    cantidad_rechazada, razon_rechazo, precio_unitario, tasa_impuesto, monto_impuesto, 
    porcentaje_descuento, subtotal, total, notas)
SELECT @orden_elimperio_002,
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    100, 0, 0, NULL, 28.60, 18.00, 514.80, 0.00, 2860.00, 3374.80, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_ordenes_compra 
    WHERE orden_compra_id = @orden_elimperio_002
    AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1)
);

-- ============================================================
-- FIN DEL SEED: PROVEEDORES Y COMPRAS
-- ============================================================
