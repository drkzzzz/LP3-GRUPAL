-- ============================================================
-- SEED 06: PROVEEDORES Y COMPRAS DEMO
-- Idempotente: usa WHERE NOT EXISTS para no duplicar al reiniciar backend
-- Dependencia: 03_negocios_demo.sql, 04_catalogo_demo.sql, 05_inventario_demo.sql
-- Propósito: Crear proveedores, relaciones producto-proveedor y órdenes de compra
-- para preparar el sistema para el módulo de Ventas y Facturación
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

-- Almacenes
SET @alm_dp_01       = (SELECT id FROM almacenes WHERE codigo = 'ALM-DP-01'    LIMIT 1);
SET @alm_dp_02       = (SELECT id FROM almacenes WHERE codigo = 'ALM-DP-02'    LIMIT 1);
SET @alm_lb_si_01    = (SELECT id FROM almacenes WHERE codigo = 'ALM-LB-SI-01' LIMIT 1);
SET @alm_lb_si_02    = (SELECT id FROM almacenes WHERE codigo = 'ALM-LB-SI-02' LIMIT 1);
SET @alm_lb_mira_01  = (SELECT id FROM almacenes WHERE codigo = 'ALM-LB-MI-01' LIMIT 1);
SET @alm_ei_01       = (SELECT id FROM almacenes WHERE codigo = 'ALM-EI-01'    LIMIT 1);

-- Usuarios admin (para órdenes de compra)
SET @usr_donpepe     = (SELECT id FROM usuarios WHERE negocio_id = @n_donpepe   AND email = 'admin@donpepe.com'      LIMIT 1);
SET @usr_labodega    = (SELECT id FROM usuarios WHERE negocio_id = @n_labodega  AND email = 'admin@labodega.com.pe'  LIMIT 1);
SET @usr_elimperio   = (SELECT id FROM usuarios WHERE negocio_id = @n_elimperio AND email = 'admin@elimperio.pe'     LIMIT 1);


-- ╔══════════════════════════════════════════════════════════════════════════╗
-- ║  1. PROVEEDORES REALES DE LICORERÍA                                      ║
-- ║  Proveedores típicos del rubro: distribuidoras de licores, cervecerías  ║
-- ╚══════════════════════════════════════════════════════════════════════════╝

-- ═══════ PROVEEDOR 1: BACKUS (Cervecería) ═══════
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'UNION DE CERVECERIAS PERUANAS BACKUS Y JOHNSTON S.A.A.', 'BACKUS', 'RUC', '20100113610',
    'Av. Nicolás Ayllón 3986, Ate - Lima', '(01) 311-3000', 'backus@backus.com.pe', 'Carlos Méndez', '987112233',
    'ventas.empresas@backus.com.pe', 30, 50000.00, 'https://www.backus.com.pe',
    'Proveedor de cervezas Pilsen, Cristal, Cusqueña. Entrega según zona.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20100113610');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'UNION DE CERVECERIAS PERUANAS BACKUS Y JOHNSTON S.A.A.', 'BACKUS', 'RUC', '20100113610',
    'Av. Nicolás Ayllón 3986, Ate - Lima', '(01) 311-3000', 'backus@backus.com.pe', 'Carlos Méndez', '987112233',
    'ventas.empresas@backus.com.pe', 30, 80000.00, 'https://www.backus.com.pe',
    'Proveedor de cervezas Pilsen, Cristal, Cusqueña. Entrega según zona.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20100113610');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'UNION DE CERVECERIAS PERUANAS BACKUS Y JOHNSTON S.A.A.', 'BACKUS', 'RUC', '20100113610',
    'Av. Nicolás Ayllón 3986, Ate - Lima', '(01) 311-3000', 'backus@backus.com.pe', 'Carlos Méndez', '987112233',
    'ventas.empresas@backus.com.pe', 30, 120000.00, 'https://www.backus.com.pe',
    'Proveedor de cervezas Pilsen, Cristal, Cusqueña. Entrega según zona.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20100113610');

-- ═══════ PROVEEDOR 2: Distribuidora Cartavio (Rones) ═══════
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'DISTRIBUIDORA DE LICORES CARTAVIO S.A.C.', 'CARTAVIO PERÚ', 'RUC', '20345678901',
    'Av. Industrial 567, Trujillo - La Libertad', '(044) 223-456', 'ventas@cartavio.com.pe', 'Ana Rodríguez', '945112233',
    'distribucion@cartavio.com.pe', 20, 30000.00,
    'Proveedor oficial de rones Cartavio. Pedido mínimo 50 unidades.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20345678901');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'DISTRIBUIDORA DE LICORES CARTAVIO S.A.C.', 'CARTAVIO PERÚ', 'RUC', '20345678901',
    'Av. Industrial 567, Trujillo - La Libertad', '(044) 223-456', 'ventas@cartavio.com.pe', 'Ana Rodríguez', '945112233',
    'distribucion@cartavio.com.pe', 20, 60000.00,
    'Proveedor oficial de rones Cartavio. Pedido mínimo 50 unidades.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20345678901');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'DISTRIBUIDORA DE LICORES CARTAVIO S.A.C.', 'CARTAVIO PERÚ', 'RUC', '20345678901',
    'Av. Industrial 567, Trujillo - La Libertad', '(044) 223-456', 'ventas@cartavio.com.pe', 'Ana Rodríguez', '945112233',
    'distribucion@cartavio.com.pe', 20, 90000.00,
    'Proveedor oficial de rones Cartavio. Pedido mínimo 50 unidades.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20345678901');

-- ═══════ PROVEEDOR 3: Viña Concha y Toro Perú (Vinos) ═══════
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'VIÑA CONCHA Y TORO PERU S.A.C.', 'CONCHA Y TORO PERÚ', 'RUC', '20513456789',
    'Av. República de Chile 456, Miraflores - Lima', '(01) 445-6700', 'ventasperu@conchaytoro.com', 'Luis Vargas', '988334455',
    'distribucion.lima@conchaytoro.com', 30, 40000.00, 'https://www.conchaytoro.com',
    'Distribuidor autorizado de vinos Casillero del Diablo, Frontera, Cono Sur. Entrega 5-7 días.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20513456789');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'VIÑA CONCHA Y TORO PERU S.A.C.', 'CONCHA Y TORO PERÚ', 'RUC', '20513456789',
    'Av. República de Chile 456, Miraflores - Lima', '(01) 445-6700', 'ventasperu@conchaytoro.com', 'Luis Vargas', '988334455',
    'distribucion.lima@conchaytoro.com', 30, 70000.00, 'https://www.conchaytoro.com',
    'Distribuidor autorizado de vinos Casillero del Diablo, Frontera, Cono Sur. Entrega 5-7 días.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20513456789');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'VIÑA CONCHA Y TORO PERU S.A.C.', 'CONCHA Y TORO PERÚ', 'RUC', '20513456789',
    'Av. República de Chile 456, Miraflores - Lima', '(01) 445-6700', 'ventasperu@conchaytoro.com', 'Luis Vargas', '988334455',
    'distribucion.lima@conchaytoro.com', 30, 100000.00, 'https://www.conchaytoro.com',
    'Distribuidor autorizado de vinos Casillero del Diablo, Frontera, Cono Sur. Entrega 5-7 días.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20513456789');

-- ═══════ PROVEEDOR 4: Alicorp (Snacks - Lay's) ═══════
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'ALICORP S.A.A.', 'ALICORP - SNACKS', 'RUC', '20100055237',
    'Av. Argentina 4793, Callao', '(01) 315-0000', 'clientes@alicorp.com.pe', 'María Suárez', '991556677',
    'ventas.retail@alicorp.com.pe', 15, 25000.00, 'https://www.alicorp.com.pe',
    'Proveedor de snacks Lay''s, Doritos, Karinto. Pedido mínimo 100 unidades.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20100055237');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'ALICORP S.A.A.', 'ALICORP - SNACKS', 'RUC', '20100055237',
    'Av. Argentina 4793, Callao', '(01) 315-0000', 'clientes@alicorp.com.pe', 'María Suárez', '991556677',
    'ventas.retail@alicorp.com.pe', 15, 45000.00, 'https://www.alicorp.com.pe',
    'Proveedor de snacks Lay''s, Doritos, Karinto. Pedido mínimo 100 unidades.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20100055237');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'ALICORP S.A.A.', 'ALICORP - SNACKS', 'RUC', '20100055237',
    'Av. Argentina 4793, Callao', '(01) 315-0000', 'clientes@alicorp.com.pe', 'María Suárez', '991556677',
    'ventas.retail@alicorp.com.pe', 15, 65000.00, 'https://www.alicorp.com.pe',
    'Proveedor de snacks Lay''s, Doritos, Karinto. Pedido mínimo 100 unidades.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20100055237');

-- ═══════ PROVEEDOR 5: Arca Continental Lindley (Coca-Cola) ═══════
INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'CORPORACION LINDLEY S.A.', 'ARCA CONTINENTAL', 'RUC', '20101036826',
    'Jr. Cajamarquilla 1241, Rímac - Lima', '(01) 417-3030', 'info@lindley.pe', 'Roberto Flores', '993778899',
    'ventasempresas@lindley.pe', 15, 35000.00, 'https://www.arcacontal.com',
    'Embotelladora oficial de Coca-Cola, Sprite, Fanta, Inca Kola. Entrega diaria.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_donpepe AND numero_documento = '20101036826');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'CORPORACION LINDLEY S.A.', 'ARCA CONTINENTAL', 'RUC', '20101036826',
    'Jr. Cajamarquilla 1241, Rímac - Lima', '(01) 417-3030', 'info@lindley.pe', 'Roberto Flores', '993778899',
    'ventasempresas@lindley.pe', 15, 55000.00, 'https://www.arcacontal.com',
    'Embotelladora oficial de Coca-Cola, Sprite, Fanta, Inca Kola. Entrega diaria.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_labodega AND numero_documento = '20101036826');

INSERT INTO proveedores (negocio_id, razon_social, nombre_comercial, tipo_documento, numero_documento,
    direccion, telefono, email, contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito, pagina_web, observaciones, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'CORPORACION LINDLEY S.A.', 'ARCA CONTINENTAL', 'RUC', '20101036826',
    'Jr. Cajamarquilla 1241, Rímac - Lima', '(01) 417-3030', 'info@lindley.pe', 'Roberto Flores', '993778899',
    'ventasempresas@lindley.pe', 15, 85000.00, 'https://www.arcacontal.com',
    'Embotelladora oficial de Coca-Cola, Sprite, Fanta, Inca Kola. Entrega diaria.', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20101036826');


-- ╔══════════════════════════════════════════════════════════════════════════╗
-- ║  2. PRODUCTOS POR PROVEEDOR                                              ║
-- ║  Relacionar productos con sus respectivos proveedores y precios          ║
-- ╚══════════════════════════════════════════════════════════════════════════╝

-- Variables de proveedores
SET @prov_backus_dp     = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20100113610' LIMIT 1);
SET @prov_cartavio_dp   = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20345678901' LIMIT 1);
SET @prov_concha_dp     = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20513456789' LIMIT 1);
SET @prov_alicorp_dp    = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20100055237' LIMIT 1);
SET @prov_lindley_dp    = (SELECT id FROM proveedores WHERE negocio_id = @n_donpepe   AND numero_documento = '20101036826' LIMIT 1);

SET @prov_backus_lb     = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20100113610' LIMIT 1);
SET @prov_cartavio_lb   = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20345678901' LIMIT 1);
SET @prov_concha_lb     = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20513456789' LIMIT 1);
SET @prov_alicorp_lb    = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20100055237' LIMIT 1);
SET @prov_lindley_lb    = (SELECT id FROM proveedores WHERE negocio_id = @n_labodega  AND numero_documento = '20101036826' LIMIT 1);

SET @prov_backus_ei     = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20100113610' LIMIT 1);
SET @prov_cartavio_ei   = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20345678901' LIMIT 1);
SET @prov_concha_ei     = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20513456789' LIMIT 1);
SET @prov_alicorp_ei    = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20100055237' LIMIT 1);
SET @prov_lindley_ei    = (SELECT id FROM proveedores WHERE negocio_id = @n_elimperio AND numero_documento = '20101036826' LIMIT 1);

-- Variables de productos
SET @prod_ron_dp     = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-RON-001' LIMIT 1);
SET @prod_cerv_dp    = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-CER-001' LIMIT 1);
SET @prod_vino_dp    = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-VIN-001' LIMIT 1);
SET @prod_snack_dp   = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-SNK-001' LIMIT 1);
SET @prod_gas_dp     = (SELECT id FROM productos WHERE negocio_id = @n_donpepe   AND sku = 'DP-GAS-001' LIMIT 1);

SET @prod_ron_lb     = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-RON-001' LIMIT 1);
SET @prod_cerv_lb    = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-CER-001' LIMIT 1);
SET @prod_vino_lb    = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-VIN-001' LIMIT 1);
SET @prod_snack_lb   = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-SNK-001' LIMIT 1);
SET @prod_gas_lb     = (SELECT id FROM productos WHERE negocio_id = @n_labodega  AND sku = 'LB-GAS-001' LIMIT 1);

SET @prod_ron_ei     = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1);
SET @prod_cerv_ei    = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-CER-001' LIMIT 1);
SET @prod_vino_ei    = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-VIN-001' LIMIT 1);
SET @prod_snack_ei   = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-SNK-001' LIMIT 1);
SET @prod_gas_ei     = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-GAS-001' LIMIT 1);


-- ═══════ DON PEPE - PRODUCTOS POR PROVEEDOR ═══════
INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @prov_cartavio_dp, @prod_ron_dp, 'CART-BLK-750', 38.50, 3, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND proveedor_id = @prov_cartavio_dp AND producto_id = @prod_ron_dp);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @prov_backus_dp, @prod_cerv_dp, 'PILS-630', 2.80, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND proveedor_id = @prov_backus_dp AND producto_id = @prod_cerv_dp);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @prov_concha_dp, @prod_vino_dp, 'CASI-CAB-750', 28.90, 5, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND proveedor_id = @prov_concha_dp AND producto_id = @prod_vino_dp);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @prov_alicorp_dp, @prod_snack_dp, 'LAYS-CLAS-200', 3.50, 2, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND proveedor_id = @prov_alicorp_dp AND producto_id = @prod_snack_dp);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, @prov_lindley_dp, @prod_gas_dp, 'COCA-1.5L', 3.20, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_donpepe AND proveedor_id = @prov_lindley_dp AND producto_id = @prod_gas_dp);

-- ═══════ LA BODEGA - PRODUCTOS POR PROVEEDOR ═══════
INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @prov_cartavio_lb, @prod_ron_lb, 'CART-BLK-750', 37.80, 3, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND proveedor_id = @prov_cartavio_lb AND producto_id = @prod_ron_lb);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @prov_backus_lb, @prod_cerv_lb, 'PILS-630', 2.75, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND proveedor_id = @prov_backus_lb AND producto_id = @prod_cerv_lb);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @prov_concha_lb, @prod_vino_lb, 'CASI-CAB-750', 28.50, 5, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND proveedor_id = @prov_concha_lb AND producto_id = @prod_vino_lb);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @prov_alicorp_lb, @prod_snack_lb, 'LAYS-CLAS-200', 3.40, 2, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND proveedor_id = @prov_alicorp_lb AND producto_id = @prod_snack_lb);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, @prov_lindley_lb, @prod_gas_lb, 'COCA-1.5L', 3.15, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_labodega AND proveedor_id = @prov_lindley_lb AND producto_id = @prod_gas_lb);

-- ═══════ EL IMPERIO - PRODUCTOS POR PROVEEDOR ═══════
INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @prov_cartavio_ei, @prod_ron_ei, 'CART-BLK-750', 37.20, 3, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND proveedor_id = @prov_cartavio_ei AND producto_id = @prod_ron_ei);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @prov_backus_ei, @prod_cerv_ei, 'PILS-630', 2.70, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND proveedor_id = @prov_backus_ei AND producto_id = @prod_cerv_ei);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @prov_concha_ei, @prod_vino_ei, 'CASI-CAB-750', 28.00, 5, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND proveedor_id = @prov_concha_ei AND producto_id = @prod_vino_ei);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @prov_alicorp_ei, @prod_snack_ei, 'LAYS-CLAS-200', 3.30, 2, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND proveedor_id = @prov_alicorp_ei AND producto_id = @prod_snack_ei);

INSERT INTO productos_proveedor (negocio_id, proveedor_id, producto_id, sku_proveedor, precio_compra, tiempo_entrega_dias, es_predeterminado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, @prov_lindley_ei, @prod_gas_ei, 'COCA-1.5L', 3.10, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM productos_proveedor WHERE negocio_id = @n_elimperio AND proveedor_id = @prov_lindley_ei AND producto_id = @prod_gas_ei);


-- ╔══════════════════════════════════════════════════════════════════════════╗
-- ║  3. ÓRDENES DE COMPRA                                                    ║
-- ║  Órdenes con estado recibida (ya ingresadas a inventario)               ║
-- ╚══════════════════════════════════════════════════════════════════════════╝

-- ═══════ DON PEPE - ORDEN 1: CERVEZAS (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260215-001', @prov_backus_dp, @sede_donpepe, @alm_dp_02, '2026-02-15', '2026-02-16',
    'recibida', 672.00, 120.96, 792.96, 'credito',
    'Pedido urgente para fin de semana. Entregar en cámara refrigerada.', @usr_donpepe, '2026-02-15 09:30:00', '2026-02-16 14:20:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260215-001');

SET @oc_dp_001 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260215-001' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_dp_001, @prod_cerv_dp, 240.00, 240.00, 2.80, 672.00, 120.96, 792.96, 1, '2026-02-15 09:30:00', '2026-02-16 14:20:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_001 AND producto_id = @prod_cerv_dp);

-- ═══════ DON PEPE - ORDEN 2: RONES Y VINOS (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260218-002', @prov_cartavio_dp, @sede_donpepe, @alm_dp_01, '2026-02-18', '2026-02-21',
    'recibida', 2310.00, 415.80, 2725.80, 'credito',
    'Reposición de rones para inventario. Verificar fecha de vencimiento.', @usr_donpepe, '2026-02-18 10:15:00', '2026-02-21 11:30:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260218-002');

SET @oc_dp_002 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260218-002' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_dp_002, @prod_ron_dp, 60.00, 60.00, 38.50, 2310.00, 415.80, 2725.80, 1, '2026-02-18 10:15:00', '2026-02-21 11:30:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_002 AND producto_id = @prod_ron_dp);

-- ═══════ DON PEPE - ORDEN 3: SNACKS Y GASEOSAS (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260220-003', @prov_alicorp_dp, @sede_donpepe, @alm_dp_01, '2026-02-20', '2026-02-22',
    'recibida', 700.00, 126.00, 826.00, 'credito',
    'Pedido de snacks para promoción cruzada con licores.', @usr_donpepe, '2026-02-20 08:45:00', '2026-02-22 15:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260220-003');

SET @oc_dp_003 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260220-003' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_dp_003, @prod_snack_dp, 200.00, 200.00, 3.50, 700.00, 126.00, 826.00, 1, '2026-02-20 08:45:00', '2026-02-22 15:00:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_003 AND producto_id = @prod_snack_dp);

-- ═══════ DON PEPE - ORDEN 4: GASEOSAS (PENDIENTE) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_donpepe, 'OC-20260227-004', @prov_lindley_dp, @sede_donpepe, @alm_dp_01, '2026-02-27', '2026-02-28',
    'pendiente', 768.00, 138.24, 906.24, 'credito',
    'Reposición semanal de gaseosas. Coordinar entrega temprano.', @usr_donpepe, '2026-02-27 09:00:00', '2026-02-27 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260227-004');

SET @oc_dp_004 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_donpepe AND numero_orden = 'OC-20260227-004' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_dp_004, @prod_gas_dp, 240.00, 0.00, 3.20, 768.00, 138.24, 906.24, 1, '2026-02-27 09:00:00', '2026-02-27 09:00:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_dp_004 AND producto_id = @prod_gas_dp);


-- ═══════ LA BODEGA - ORDEN 1: CERVEZAS (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_labodega, 'OC-20260212-001', @prov_backus_lb, @sede_lab_si, @alm_lb_si_02, '2026-02-12', '2026-02-13',
    'recibida', 1650.00, 297.00, 1947.00, 'credito',
    'Pedido para sede San Isidro. Entrega en refrigerado.', @usr_labodega, '2026-02-12 10:00:00', '2026-02-13 08:30:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-20260212-001');

SET @oc_lb_001 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-20260212-001' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_lb_001, @prod_cerv_lb, 600.00, 600.00, 2.75, 1650.00, 297.00, 1947.00, 1, '2026-02-12 10:00:00', '2026-02-13 08:30:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_001 AND producto_id = @prod_cerv_lb);

-- ═══════ LA BODEGA - ORDEN 2: RONES (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_labodega, 'OC-20260217-002', @prov_cartavio_lb, @sede_lab_si, @alm_lb_si_01, '2026-02-17', '2026-02-20',
    'recibida', 3024.00, 544.32, 3568.32, 'credito',
    'Reposición de rones para ambas sedes.', @usr_labodega, '2026-02-17 11:20:00', '2026-02-20 10:15:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-20260217-002');

SET @oc_lb_002 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-20260217-002' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_lb_002, @prod_ron_lb, 80.00, 80.00, 37.80, 3024.00, 544.32, 3568.32, 1, '2026-02-17 11:20:00', '2026-02-20 10:15:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_002 AND producto_id = @prod_ron_lb);

-- ═══════ LA BODEGA - ORDEN 3: VINOS (PENDIENTE) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_labodega, 'OC-20260226-003', @prov_concha_lb, @sede_lab_mira, @alm_lb_mira_01, '2026-02-26', '2026-03-03',
    'pendiente', 1425.00, 256.50, 1681.50, 'credito',
    'Pedido especial para sede Miraflores. Verificar calidad.', @usr_labodega, '2026-02-26 14:00:00', '2026-02-26 14:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-20260226-003');

SET @oc_lb_003 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_labodega AND numero_orden = 'OC-20260226-003' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_lb_003, @prod_vino_lb, 50.00, 0.00, 28.50, 1425.00, 256.50, 1681.50, 1, '2026-02-26 14:00:00', '2026-02-26 14:00:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_lb_003 AND producto_id = @prod_vino_lb);


-- ═══════ EL IMPERIO - ORDEN 1: CERVEZAS (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_elimperio, 'OC-20260210-001', @prov_backus_ei, @sede_elimperio, @alm_ei_01, '2026-02-10', '2026-02-11',
    'recibida', 2700.00, 486.00, 3186.00, 'credito',
    'Pedido masivo para distribución. Prioridad alta.', @usr_elimperio, '2026-02-10 08:00:00', '2026-02-11 07:30:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-20260210-001');

SET @oc_ei_001 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-20260210-001' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_ei_001, @prod_cerv_ei, 1000.00, 1000.00, 2.70, 2700.00, 486.00, 3186.00, 1, '2026-02-10 08:00:00', '2026-02-11 07:30:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_001 AND producto_id = @prod_cerv_ei);

-- ═══════ EL IMPERIO - ORDEN 2: RONES (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_elimperio, 'OC-20260214-002', @prov_cartavio_ei, @sede_elimperio, @alm_ei_01, '2026-02-14', '2026-02-17',
    'recibida', 4464.00, 803.52, 5267.52, 'credito',
    'Reposición mensual de rones. Stock alto.', @usr_elimperio, '2026-02-14 09:30:00', '2026-02-17 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-20260214-002');

SET @oc_ei_002 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-20260214-002' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_ei_002, @prod_ron_ei, 120.00, 120.00, 37.20, 4464.00, 803.52, 5267.52, 1, '2026-02-14 09:30:00', '2026-02-17 11:00:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_002 AND producto_id = @prod_ron_ei);

-- ═══════ EL IMPERIO - ORDEN 3: SNACKS (RECIBIDA) ═══════
INSERT INTO ordenes_compra (negocio_id, numero_orden, proveedor_id, sede_id, almacen_id, fecha_orden, fecha_entrega_esperada,
    estado, subtotal, impuestos, total, metodo_pago, notas, usuario_id, creado_en, actualizado_en)
SELECT @n_elimperio, 'OC-20260222-003', @prov_alicorp_ei, @sede_elimperio, @alm_ei_01, '2026-02-22', '2026-02-24',
    'recibida', 1320.00, 237.60, 1557.60, 'credito',
    'Snacks para paquetes promocionales.', @usr_elimperio, '2026-02-22 10:45:00', '2026-02-24 14:00:00'
WHERE NOT EXISTS (SELECT 1 FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-20260222-003');

SET @oc_ei_003 = (SELECT id FROM ordenes_compra WHERE negocio_id = @n_elimperio AND numero_orden = 'OC-20260222-003' LIMIT 1);

INSERT INTO detalle_ordenes_compra (orden_compra_id, producto_id, cantidad_solicitada, cantidad_recibida, precio_unitario, subtotal, impuesto, total, esta_activo, creado_en, actualizado_en)
SELECT @oc_ei_003, @prod_snack_ei, 400.00, 400.00, 3.30, 1320.00, 237.60, 1557.60, 1, '2026-02-22 10:45:00', '2026-02-24 14:00:00'
WHERE NOT EXISTS (SELECT 1 FROM detalle_ordenes_compra WHERE orden_compra_id = @oc_ei_003 AND producto_id = @prod_snack_ei);


-- ════════════════════════════════════════════════════════════════════════════
-- RESUMEN DEL SEED 06
-- ════════════════════════════════════════════════════════════════════════════
-- ✓ 5 Proveedores reales por negocio (Backus, Cartavio, Concha y Toro, Alicorp, Lindley)
-- ✓ 5 Relaciones producto-proveedor por negocio con precios de compra realistas
-- ✓ 3-4 Órdenes de compra por negocio con estados pendiente/recibida
-- ✓ Totales calculados: subtotal + IGV (18%) = total
-- ✓ Fechas coherentes: órdenes recibidas en el pasado, pendientes recientes
-- ✓ Datos listos para módulo de Ventas y Facturación
-- ════════════════════════════════════════════════════════════════════════════

SELECT '✓ SEED 06: PROVEEDORES Y COMPRAS CARGADO EXITOSAMENTE' AS resultado;
