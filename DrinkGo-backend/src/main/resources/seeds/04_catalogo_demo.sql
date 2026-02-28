-- ============================================================
-- SEED 04: CATÁLOGO DEMO (CATEGORÍAS, MARCAS, UNIDADES, PRODUCTOS, COMBOS)
-- Idempotente: usa WHERE NOT EXISTS para no duplicar al reiniciar backend
-- Dependencia: 03_negocios_demo.sql (negocios ya creados)
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- REFERENCIAS A NEGOCIOS (del seed 03)
-- ============================================================
SET @n_donpepe   = (SELECT id FROM negocios WHERE ruc = '20123456789' LIMIT 1);
SET @n_labodega  = (SELECT id FROM negocios WHERE ruc = '20987654321' LIMIT 1);
SET @n_elimperio = (SELECT id FROM negocios WHERE ruc = '20456789123' LIMIT 1);
SET @n_premium   = (SELECT id FROM negocios WHERE ruc = '20111222333' LIMIT 1);

-- ╔══════════════════════════════════════════════════════════════╗
-- ║  1. CATEGORÍAS  (5 por negocio × 4 negocios = 20)          ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Rones', 'rones', 'Rones nacionales e importados de diversas añejamientos', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Rones');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Cervezas', 'cervezas', 'Cervezas nacionales e importadas', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Cervezas');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Vinos y Espumantes', 'vinos-y-espumantes', 'Vinos tintos, blancos, rosados y espumantes', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Vinos y Espumantes');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Snacks y Piqueos', 'snacks-y-piqueos', 'Acompañamientos ideales para tus bebidas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Snacks y Piqueos');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Gaseosas y Bebidas', 'gaseosas-y-bebidas', 'Bebidas sin alcohol, gaseosas, jugos y aguas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Gaseosas y Bebidas');

-- ── LA BODEGA ──
INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Rones', 'rones', 'Rones nacionales e importados de diversas añejamientos', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Rones');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Cervezas', 'cervezas', 'Cervezas nacionales e importadas', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Cervezas');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Vinos y Espumantes', 'vinos-y-espumantes', 'Vinos tintos, blancos, rosados y espumantes', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Vinos y Espumantes');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Snacks y Piqueos', 'snacks-y-piqueos', 'Acompañamientos ideales para tus bebidas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Snacks y Piqueos');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Gaseosas y Bebidas', 'gaseosas-y-bebidas', 'Bebidas sin alcohol, gaseosas, jugos y aguas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Gaseosas y Bebidas');

-- ── EL IMPERIO ──
INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Rones', 'rones', 'Rones nacionales e importados de diversas añejamientos', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Rones');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Cervezas', 'cervezas', 'Cervezas nacionales e importadas', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Cervezas');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Vinos y Espumantes', 'vinos-y-espumantes', 'Vinos tintos, blancos, rosados y espumantes', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Vinos y Espumantes');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Snacks y Piqueos', 'snacks-y-piqueos', 'Acompañamientos ideales para tus bebidas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Snacks y Piqueos');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Gaseosas y Bebidas', 'gaseosas-y-bebidas', 'Bebidas sin alcohol, gaseosas, jugos y aguas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Gaseosas y Bebidas');

-- ── PREMIUM WINES ──
INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Rones', 'rones', 'Rones nacionales e importados de diversas añejamientos', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Rones');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Cervezas', 'cervezas', 'Cervezas nacionales e importadas', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Cervezas');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Vinos y Espumantes', 'vinos-y-espumantes', 'Vinos tintos, blancos, rosados y espumantes', 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Vinos y Espumantes');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Snacks y Piqueos', 'snacks-y-piqueos', 'Acompañamientos ideales para tus bebidas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Snacks y Piqueos');

INSERT INTO categorias (negocio_id, nombre, slug, descripcion, es_alcoholica, visible_tienda_online, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Gaseosas y Bebidas', 'gaseosas-y-bebidas', 'Bebidas sin alcohol, gaseosas, jugos y aguas', 0, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Gaseosas y Bebidas');


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  2. MARCAS  (5 por negocio × 4 negocios = 20)              ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Cartavio', 'cartavio', 'Perú', 'Rones peruanos de tradición desde 1929', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Cartavio');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Pilsen', 'pilsen', 'Perú', 'Cerveza peruana desde 1863', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Pilsen');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Concha y Toro', 'concha-y-toro', 'Chile', 'Viña chilena líder en Sudamérica', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Concha y Toro');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Lay''s', 'lays', 'Estados Unidos', 'Snacks y papas fritas reconocidas mundialmente', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Lay''s');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Coca-Cola', 'coca-cola', 'Estados Unidos', 'La bebida gaseosa más popular del mundo', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Coca-Cola');

-- ── LA BODEGA ──
INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Cartavio', 'cartavio', 'Perú', 'Rones peruanos de tradición desde 1929', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Cartavio');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Pilsen', 'pilsen', 'Perú', 'Cerveza peruana desde 1863', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Pilsen');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Concha y Toro', 'concha-y-toro', 'Chile', 'Viña chilena líder en Sudamérica', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Concha y Toro');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Lay''s', 'lays', 'Estados Unidos', 'Snacks y papas fritas reconocidas mundialmente', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Lay''s');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Coca-Cola', 'coca-cola', 'Estados Unidos', 'La bebida gaseosa más popular del mundo', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Coca-Cola');

-- ── EL IMPERIO ──
INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Cartavio', 'cartavio', 'Perú', 'Rones peruanos de tradición desde 1929', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Cartavio');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Pilsen', 'pilsen', 'Perú', 'Cerveza peruana desde 1863', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Pilsen');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Concha y Toro', 'concha-y-toro', 'Chile', 'Viña chilena líder en Sudamérica', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Concha y Toro');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Lay''s', 'lays', 'Estados Unidos', 'Snacks y papas fritas reconocidas mundialmente', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Lay''s');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Coca-Cola', 'coca-cola', 'Estados Unidos', 'La bebida gaseosa más popular del mundo', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Coca-Cola');

-- ── PREMIUM WINES ──
INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Cartavio', 'cartavio', 'Perú', 'Rones peruanos de tradición desde 1929', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Cartavio');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Pilsen', 'pilsen', 'Perú', 'Cerveza peruana desde 1863', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Pilsen');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Concha y Toro', 'concha-y-toro', 'Chile', 'Viña chilena líder en Sudamérica', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Concha y Toro');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Lay''s', 'lays', 'Estados Unidos', 'Snacks y papas fritas reconocidas mundialmente', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Lay''s');

INSERT INTO marcas (negocio_id, nombre, slug, pais_origen, descripcion, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Coca-Cola', 'coca-cola', 'Estados Unidos', 'La bebida gaseosa más popular del mundo', 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Coca-Cola');


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  3. UNIDADES DE MEDIDA  (5 por negocio × 4 negocios = 20)  ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_donpepe, 'UND', 'Unidad', 'und', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'UND');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_donpepe, 'BOT', 'Botella', 'bot', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'BOT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_donpepe, 'PAQ', 'Paquete', 'paq', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'PAQ');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_donpepe, 'LAT', 'Lata', 'lat', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'LAT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_donpepe, 'SIX', 'Six Pack', '6pk', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'SIX');

-- ── LA BODEGA ──
INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_labodega, 'UND', 'Unidad', 'und', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'UND');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_labodega, 'BOT', 'Botella', 'bot', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'BOT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_labodega, 'PAQ', 'Paquete', 'paq', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'PAQ');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_labodega, 'LAT', 'Lata', 'lat', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'LAT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_labodega, 'SIX', 'Six Pack', '6pk', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'SIX');

-- ── EL IMPERIO ──
INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_elimperio, 'UND', 'Unidad', 'und', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'UND');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_elimperio, 'BOT', 'Botella', 'bot', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'BOT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_elimperio, 'PAQ', 'Paquete', 'paq', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'PAQ');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_elimperio, 'LAT', 'Lata', 'lat', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'LAT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_elimperio, 'SIX', 'Six Pack', '6pk', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'SIX');

-- ── PREMIUM WINES ──
INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_premium, 'UND', 'Unidad', 'und', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'UND');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_premium, 'BOT', 'Botella', 'bot', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'BOT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_premium, 'PAQ', 'Paquete', 'paq', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'PAQ');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_premium, 'LAT', 'Lata', 'lat', 'unidad', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'LAT');

INSERT INTO unidades_medida (negocio_id, codigo, nombre, abreviatura, tipo, esta_activo)
SELECT @n_premium, 'SIX', 'Six Pack', '6pk', 'paquete', 1
WHERE NOT EXISTS (SELECT 1 FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'SIX');


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  4. PRODUCTOS  (5 por negocio × 4 negocios = 20)           ║
-- ║                                                              ║
-- ║  Nota: Los precios se manejan ahora en otros módulos:       ║
-- ║  - Inventario → lotes_inventario.costo_unitario_compra      ║
-- ║  - Ventas     → detalle_ventas.precio_unitario              ║
-- ║  - Combos     → combos.precio_combo / detalle_combos        ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_donpepe, 'DP-RON-001', 'Ron Cartavio Black 750ml', 'ron-cartavio-black-750ml',
    'Ron negro peruano de 750ml con 40° de grado alcohólico. Ideal para cócteles y consumo directo.',
    40.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Rones' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Cartavio' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_donpepe, 'DP-CER-001', 'Cerveza Pilsen Callao 630ml', 'cerveza-pilsen-callao-630ml',
    'Cerveza lager peruana de 630ml con 5° de grado alcohólico. La cerveza del Perú.',
    5.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Cervezas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Pilsen' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_donpepe, 'DP-VIN-001', 'Vino Casillero del Diablo Cabernet 750ml', 'vino-casillero-diablo-cabernet-750ml',
    'Vino tinto chileno Cabernet Sauvignon de 750ml con 13.5° de grado alcohólico.',
    13.50, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Vinos y Espumantes' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Concha y Toro' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-VIN-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_donpepe, 'DP-SNK-001', 'Papitas Lay''s Clásica 200g', 'papitas-lays-clasica-200g',
    'Papas fritas sabor clásico en bolsa de 200g. Acompañamiento perfecto.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Snacks y Piqueos' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Lay''s' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'PAQ' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_donpepe, 'DP-GAS-001', 'Coca-Cola 1.5L', 'coca-cola-1-5l',
    'Gaseosa Coca-Cola botella de 1.5 litros. Ideal para mezclar o acompañar.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_donpepe AND nombre = 'Gaseosas y Bebidas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_donpepe AND nombre = 'Coca-Cola' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_donpepe AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001');

-- ── LA BODEGA ──
INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_labodega, 'LB-RON-001', 'Ron Cartavio Black 750ml', 'ron-cartavio-black-750ml',
    'Ron negro peruano de 750ml con 40° de grado alcohólico. Ideal para cócteles y consumo directo.',
    40.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Rones' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Cartavio' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_labodega, 'LB-CER-001', 'Cerveza Pilsen Callao 630ml', 'cerveza-pilsen-callao-630ml',
    'Cerveza lager peruana de 630ml con 5° de grado alcohólico. La cerveza del Perú.',
    5.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Cervezas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Pilsen' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_labodega, 'LB-VIN-001', 'Vino Casillero del Diablo Cabernet 750ml', 'vino-casillero-diablo-cabernet-750ml',
    'Vino tinto chileno Cabernet Sauvignon de 750ml con 13.5° de grado alcohólico.',
    13.50, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Vinos y Espumantes' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Concha y Toro' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-VIN-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_labodega, 'LB-SNK-001', 'Papitas Lay''s Clásica 200g', 'papitas-lays-clasica-200g',
    'Papas fritas sabor clásico en bolsa de 200g. Acompañamiento perfecto.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Snacks y Piqueos' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Lay''s' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'PAQ' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_labodega, 'LB-GAS-001', 'Coca-Cola 1.5L', 'coca-cola-1-5l',
    'Gaseosa Coca-Cola botella de 1.5 litros. Ideal para mezclar o acompañar.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_labodega AND nombre = 'Gaseosas y Bebidas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_labodega AND nombre = 'Coca-Cola' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_labodega AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001');

-- ── EL IMPERIO ──
INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_elimperio, 'EI-RON-001', 'Ron Cartavio Black 750ml', 'ron-cartavio-black-750ml',
    'Ron negro peruano de 750ml con 40° de grado alcohólico. Ideal para cócteles y consumo directo.',
    40.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Rones' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Cartavio' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_elimperio, 'EI-CER-001', 'Cerveza Pilsen Callao 630ml', 'cerveza-pilsen-callao-630ml',
    'Cerveza lager peruana de 630ml con 5° de grado alcohólico. La cerveza del Perú.',
    5.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Cervezas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Pilsen' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-CER-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_elimperio, 'EI-VIN-001', 'Vino Casillero del Diablo Cabernet 750ml', 'vino-casillero-diablo-cabernet-750ml',
    'Vino tinto chileno Cabernet Sauvignon de 750ml con 13.5° de grado alcohólico.',
    13.50, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Vinos y Espumantes' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Concha y Toro' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-VIN-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_elimperio, 'EI-SNK-001', 'Papitas Lay''s Clásica 200g', 'papitas-lays-clasica-200g',
    'Papas fritas sabor clásico en bolsa de 200g. Acompañamiento perfecto.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Snacks y Piqueos' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Lay''s' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'PAQ' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-SNK-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_elimperio, 'EI-GAS-001', 'Coca-Cola 1.5L', 'coca-cola-1-5l',
    'Gaseosa Coca-Cola botella de 1.5 litros. Ideal para mezclar o acompañar.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_elimperio AND nombre = 'Gaseosas y Bebidas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_elimperio AND nombre = 'Coca-Cola' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_elimperio AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-GAS-001');

-- ── PREMIUM WINES ──
INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_premium, 'PW-RON-001', 'Ron Cartavio Black 750ml', 'ron-cartavio-black-750ml',
    'Ron negro peruano de 750ml con 40° de grado alcohólico. Ideal para cócteles y consumo directo.',
    40.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Rones' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Cartavio' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-RON-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_premium, 'PW-CER-001', 'Cerveza Pilsen Callao 630ml', 'cerveza-pilsen-callao-630ml',
    'Cerveza lager peruana de 630ml con 5° de grado alcohólico. La cerveza del Perú.',
    5.00, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Cervezas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Pilsen' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-CER-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_premium, 'PW-VIN-001', 'Vino Casillero del Diablo Cabernet 750ml', 'vino-casillero-diablo-cabernet-750ml',
    'Vino tinto chileno Cabernet Sauvignon de 750ml con 13.5° de grado alcohólico.',
    13.50, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Vinos y Espumantes' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Concha y Toro' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-VIN-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_premium, 'PW-SNK-001', 'Papitas Lay''s Clásica 200g', 'papitas-lays-clasica-200g',
    'Papas fritas sabor clásico en bolsa de 200g. Acompañamiento perfecto.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Snacks y Piqueos' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Lay''s' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'PAQ' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-SNK-001');

INSERT INTO productos (negocio_id, sku, nombre, slug, descripcion, grado_alcoholico, tasa_impuesto, impuesto_incluido, permite_descuento, esta_activo, creado_en, actualizado_en,
    categoria_id, marca_id, unidad_medida_id)
SELECT @n_premium, 'PW-GAS-001', 'Coca-Cola 1.5L', 'coca-cola-1-5l',
    'Gaseosa Coca-Cola botella de 1.5 litros. Ideal para mezclar o acompañar.',
    NULL, 18.00, 1, 1, 1, NOW(), NOW(),
    (SELECT id FROM categorias WHERE negocio_id = @n_premium AND nombre = 'Gaseosas y Bebidas' LIMIT 1),
    (SELECT id FROM marcas WHERE negocio_id = @n_premium AND nombre = 'Coca-Cola' LIMIT 1),
    (SELECT id FROM unidades_medida WHERE negocio_id = @n_premium AND codigo = 'BOT' LIMIT 1)
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-GAS-001');


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  5. COMBOS  (2 por negocio × 4 negocios = 8)               ║
-- ║                                                              ║
-- ║  Combo 1: "Pack Parrillero"                                 ║
-- ║    → 2x Cerveza Pilsen + 1x Papitas Lay's                  ║
-- ║    → Regular: S/22.00  |  Combo: S/18.90 (14% desc.)       ║
-- ║                                                              ║
-- ║  Combo 2: "Combo Ron + Gaseosa"                             ║
-- ║    → 1x Ron Cartavio + 2x Coca-Cola                        ║
-- ║    → Regular: S/57.00  |  Combo: S/49.90 (12% desc.)       ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE ──
INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Pack Parrillero', 'dp-pack-parrillero',
    '2 Cervezas Pilsen Callao 630ml + 1 Papitas Lay''s 200g. El combo perfecto para la parrilla.',
    22.00, 18.90, 14.09, 1, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'dp-pack-parrillero');

INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_donpepe, 'Combo Ron + Gaseosa', 'dp-combo-ron-gaseosa',
    '1 Ron Cartavio Black 750ml + 2 Coca-Cola 1.5L. Listo para la reunión.',
    57.00, 49.90, 12.46, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'dp-combo-ron-gaseosa');

-- ── LA BODEGA ──
INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Pack Parrillero', 'lb-pack-parrillero',
    '2 Cervezas Pilsen Callao 630ml + 1 Papitas Lay''s 200g. El combo perfecto para la parrilla.',
    22.00, 18.90, 14.09, 1, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'lb-pack-parrillero');

INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_labodega, 'Combo Ron + Gaseosa', 'lb-combo-ron-gaseosa',
    '1 Ron Cartavio Black 750ml + 2 Coca-Cola 1.5L. Listo para la reunión.',
    57.00, 49.90, 12.46, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'lb-combo-ron-gaseosa');

-- ── EL IMPERIO ──
INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Pack Parrillero', 'ei-pack-parrillero',
    '2 Cervezas Pilsen Callao 630ml + 1 Papitas Lay''s 200g. El combo perfecto para la parrilla.',
    22.00, 18.90, 14.09, 1, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'ei-pack-parrillero');

INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_elimperio, 'Combo Ron + Gaseosa', 'ei-combo-ron-gaseosa',
    '1 Ron Cartavio Black 750ml + 2 Coca-Cola 1.5L. Listo para la reunión.',
    57.00, 49.90, 12.46, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'ei-combo-ron-gaseosa');

-- ── PREMIUM WINES ──
INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Pack Parrillero', 'pw-pack-parrillero',
    '2 Cervezas Pilsen Callao 630ml + 1 Papitas Lay''s 200g. El combo perfecto para la parrilla.',
    22.00, 18.90, 14.09, 1, 1, 1, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'pw-pack-parrillero');

INSERT INTO combos (negocio_id, nombre, slug, descripcion, precio_regular, precio_combo, porcentaje_descuento, visible_pos, visible_tienda_online, es_destacado, esta_activo, creado_en, actualizado_en)
SELECT @n_premium, 'Combo Ron + Gaseosa', 'pw-combo-ron-gaseosa',
    '1 Ron Cartavio Black 750ml + 2 Coca-Cola 1.5L. Listo para la reunión.',
    57.00, 49.90, 12.46, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM combos WHERE slug = 'pw-combo-ron-gaseosa');


-- ╔══════════════════════════════════════════════════════════════╗
-- ║  6. DETALLE COMBOS  (productos dentro de cada combo)        ║
-- ╚══════════════════════════════════════════════════════════════╝

-- ── DON PEPE: Pack Parrillero ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'dp-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1),
    2, 7.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'dp-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-CER-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'dp-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1),
    1, 8.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'dp-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-SNK-001' LIMIT 1)
);

-- ── DON PEPE: Combo Ron + Gaseosa ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'dp-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1),
    1, 45.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'dp-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-RON-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'dp-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1),
    2, 6.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'dp-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_donpepe AND sku = 'DP-GAS-001' LIMIT 1)
);

-- ── LA BODEGA: Pack Parrillero ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'lb-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1),
    2, 7.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'lb-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-CER-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'lb-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1),
    1, 8.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'lb-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-SNK-001' LIMIT 1)
);

-- ── LA BODEGA: Combo Ron + Gaseosa ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'lb-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1),
    1, 45.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'lb-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-RON-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'lb-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1),
    2, 6.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'lb-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_labodega AND sku = 'LB-GAS-001' LIMIT 1)
);

-- ── EL IMPERIO: Pack Parrillero ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'ei-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-CER-001' LIMIT 1),
    2, 7.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'ei-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-CER-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'ei-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-SNK-001' LIMIT 1),
    1, 8.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'ei-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-SNK-001' LIMIT 1)
);

-- ── EL IMPERIO: Combo Ron + Gaseosa ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'ei-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1),
    1, 45.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'ei-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-RON-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'ei-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-GAS-001' LIMIT 1),
    2, 6.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'ei-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_elimperio AND sku = 'EI-GAS-001' LIMIT 1)
);

-- ── PREMIUM WINES: Pack Parrillero ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'pw-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-CER-001' LIMIT 1),
    2, 7.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'pw-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-CER-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'pw-pack-parrillero' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-SNK-001' LIMIT 1),
    1, 8.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'pw-pack-parrillero' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-SNK-001' LIMIT 1)
);

-- ── PREMIUM WINES: Combo Ron + Gaseosa ──
INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'pw-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-RON-001' LIMIT 1),
    1, 45.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'pw-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-RON-001' LIMIT 1)
);

INSERT INTO detalle_combos (combo_id, producto_id, cantidad, precio_unitario, esta_activo, creado_en, actualizado_en)
SELECT
    (SELECT id FROM combos WHERE slug = 'pw-combo-ron-gaseosa' LIMIT 1),
    (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-GAS-001' LIMIT 1),
    2, 6.00, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM detalle_combos
    WHERE combo_id = (SELECT id FROM combos WHERE slug = 'pw-combo-ron-gaseosa' LIMIT 1)
      AND producto_id = (SELECT id FROM productos WHERE negocio_id = @n_premium AND sku = 'PW-GAS-001' LIMIT 1)
);


-- ============================================================
-- RESUMEN DE DATOS INSERTADOS
-- ============================================================
-- Categorías:      5 × 4 negocios = 20 registros
--   • Rones (alcohólica)
--   • Cervezas (alcohólica)
--   • Vinos y Espumantes (alcohólica)
--   • Snacks y Piqueos
--   • Gaseosas y Bebidas
--
-- Marcas:          5 × 4 negocios = 20 registros
--   • Cartavio (Perú)
--   • Pilsen (Perú)
--   • Concha y Toro (Chile)
--   • Lay's (Estados Unidos)
--   • Coca-Cola (Estados Unidos)
--
-- Unidades:        5 × 4 negocios = 20 registros
--   • UND - Unidad
--   • BOT - Botella
--   • PAQ - Paquete
--   • LAT - Lata
--   • SIX - Six Pack
--
-- Productos:       5 × 4 negocios = 20 registros
--   • Ron Cartavio Black 750ml        (SKU: XX-RON-001, 40°)
--   • Cerveza Pilsen Callao 630ml     (SKU: XX-CER-001, 5°)
--   • Vino Casillero del Diablo 750ml (SKU: XX-VIN-001, 13.5°)
--   • Papitas Lay's Clásica 200g      (SKU: XX-SNK-001, sin alcohol)
--   • Coca-Cola 1.5L                  (SKU: XX-GAS-001, sin alcohol)
--
-- Combos:          2 × 4 negocios = 8 registros
--   • Pack Parrillero       → 2 Cervezas + 1 Snack  (S/22→S/18.90)
--   • Combo Ron + Gaseosa   → 1 Ron + 2 Coca-Cola   (S/57→S/49.90)
--
-- Detalle Combos:  4 × 4 negocios = 16 registros
-- ============================================================
