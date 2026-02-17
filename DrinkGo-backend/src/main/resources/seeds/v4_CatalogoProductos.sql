-- ============================================================
-- SEED v4: Catálogo de Productos
-- DrinkGo Platform – Bloque 4
-- PREREQUISITOS: ejecutar v3 → v8 primero
--   (negocios 1-2 / sedes 1-2 / productos 1-10)
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 1. CATEGORÍAS DE PRODUCTOS (con jerarquía)
-- ============================================================
INSERT INTO categorias (
    id, negocio_id, padre_id, nombre, slug,
    descripcion, url_imagen, icono, orden,
    visible_tienda_online, esta_activo, creado_en, actualizado_en
) VALUES
-- Categorías raíz negocio 1
(1,  1, NULL, 'Licores',           'licores',
 'Bebidas espirituosas y destilados',
 'https://cdn.drinkgo.pe/cat/licores.jpg', 'local_bar', 1, 1, 1, NOW(), NOW()),
(2,  1, NULL, 'Vinos',             'vinos',
 'Vinos tintos, blancos, rosados y espumantes',
 'https://cdn.drinkgo.pe/cat/vinos.jpg', 'wine_bar', 2, 1, 1, NOW(), NOW()),
(3,  1, NULL, 'Cervezas',          'cervezas',
 'Cervezas nacionales e importadas',
 'https://cdn.drinkgo.pe/cat/cervezas.jpg', 'sports_bar', 3, 1, 1, NOW(), NOW()),
(4,  1, NULL, 'Piscos',            'piscos',
 'Piscos peruanos de diversas cepas',
 'https://cdn.drinkgo.pe/cat/piscos.jpg', 'liquor', 4, 1, 1, NOW(), NOW()),
(5,  1, NULL, 'Bebidas sin Alcohol','bebidas-sin-alcohol',
 'Gaseosas, aguas, jugos y mixers',
 'https://cdn.drinkgo.pe/cat/sin-alcohol.jpg', 'local_drink', 5, 1, 1, NOW(), NOW()),
-- Subcategorías de Licores
(6,  1, 1,   'Ron',                'ron',
 'Rones blancos, dorados y añejos',
 NULL, NULL, 1, 1, 1, NOW(), NOW()),
(7,  1, 1,   'Whisky',             'whisky',
 'Scotch, Bourbon e Irish Whisky',
 NULL, NULL, 2, 1, 1, NOW(), NOW()),
(8,  1, 1,   'Vodka',              'vodka',
 'Vodkas clásicos y saborizados',
 NULL, NULL, 3, 1, 1, NOW(), NOW()),
(9,  1, 1,   'Gin',                'gin',
 'Gin London Dry y artesanales',
 NULL, NULL, 4, 1, 1, NOW(), NOW()),
(10, 1, 1,   'Tequila',            'tequila',
 'Tequilas blancos, reposados y añejos',
 NULL, NULL, 5, 1, 1, NOW(), NOW()),
-- Subcategorías de Vinos
(11, 1, 2,   'Vino Tinto',         'vino-tinto',
 'Cabernet Sauvignon, Malbec, Merlot',
 NULL, NULL, 1, 1, 1, NOW(), NOW()),
(12, 1, 2,   'Espumantes',         'espumantes',
 'Champagne, Cava y espumantes nacionales',
 NULL, NULL, 2, 1, 1, NOW(), NOW()),
-- Subcategorías de Cervezas
(13, 1, 3,   'Cerveza Nacional',   'cerveza-nacional',
 'Cusqueña, Pilsen, Cristal y más',
 NULL, NULL, 1, 1, 1, NOW(), NOW()),
(14, 1, 3,   'Cerveza Importada',  'cerveza-importada',
 'Corona, Heineken, Stella Artois',
 NULL, NULL, 2, 1, 1, NOW(), NOW()),
-- Categorías negocio 2
(15, 2, NULL, 'Licores',           'licores',
 'Bebidas destiladas',
 NULL, 'local_bar', 1, 1, 1, NOW(), NOW()),
(16, 2, NULL, 'Cervezas',          'cervezas',
 'Cervezas variadas',
 NULL, 'sports_bar', 2, 1, 1, NOW(), NOW());

-- ============================================================
-- 2. MARCAS
-- ============================================================
INSERT INTO marcas (
    id, negocio_id, nombre, slug,
    pais_origen, url_logo, descripcion,
    esta_activo, creado_en, actualizado_en
) VALUES
(1,  1, 'Diplomático',      'diplomatico',
 'Venezuela', 'https://cdn.drinkgo.pe/marcas/diplomatico.png',
 'Ron venezolano premium', 1, NOW(), NOW()),
(2,  1, 'Tabernero',        'tabernero',
 'Perú', 'https://cdn.drinkgo.pe/marcas/tabernero.png',
 'Piscos y vinos peruanos', 1, NOW(), NOW()),
(3,  1, 'Casillero del Diablo','casillero-del-diablo',
 'Chile', 'https://cdn.drinkgo.pe/marcas/casillero.png',
 'Vinos chilenos Concha y Toro', 1, NOW(), NOW()),
(4,  1, 'Backus',           'backus',
 'Perú', 'https://cdn.drinkgo.pe/marcas/backus.png',
 'Cervezas peruanas (Cusqueña, Pilsen)', 1, NOW(), NOW()),
(5,  1, 'Johnnie Walker',   'johnnie-walker',
 'Escocia', 'https://cdn.drinkgo.pe/marcas/jw.png',
 'Whisky escocés', 1, NOW(), NOW()),
(6,  1, 'Absolut',          'absolut',
 'Suecia', 'https://cdn.drinkgo.pe/marcas/absolut.png',
 'Vodka sueco', 1, NOW(), NOW()),
(7,  1, 'Tanqueray',        'tanqueray',
 'Inglaterra', 'https://cdn.drinkgo.pe/marcas/tanqueray.png',
 'Gin inglés London Dry', 1, NOW(), NOW()),
(8,  1, 'José Cuervo',      'jose-cuervo',
 'México', 'https://cdn.drinkgo.pe/marcas/josecuervo.png',
 'Tequila mexicano', 1, NOW(), NOW()),
(9,  1, 'Chandon',          'chandon',
 'Argentina', 'https://cdn.drinkgo.pe/marcas/chandon.png',
 'Espumantes argentinos', 1, NOW(), NOW()),
(10, 2, 'Backus',           'backus',
 'Perú', NULL, 'Cervezas peruanas', 1, NOW(), NOW());

-- ============================================================
-- 3. UNIDADES DE MEDIDA
-- ============================================================
INSERT INTO unidades_medida (
    id, negocio_id, codigo, nombre, abreviatura, tipo, esta_activo
) VALUES
(1, 1, 'UND',  'Unidad',      'und',  'unidad',  1),
(2, 1, 'BOT',  'Botella',     'bot',  'unidad',  1),
(3, 1, 'PACK', 'Six-Pack',    'pk',   'paquete', 1),
(4, 1, 'CAJ',  'Caja',        'caj',  'paquete', 1),
(5, 1, 'ML',   'Mililitro',   'ml',   'volumen', 1),
(6, 1, 'LT',   'Litro',       'lt',   'volumen', 1),
(7, 1, 'KG',   'Kilogramo',   'kg',   'peso',    1),
(8, 2, 'UND',  'Unidad',      'und',  'unidad',  1),
(9, 2, 'BOT',  'Botella',     'bot',  'unidad',  1);

-- ============================================================
-- 4. IMÁGENES DE PRODUCTOS
-- ============================================================
INSERT INTO imagenes_producto (
    id, producto_id, url_imagen, url_miniatura,
    texto_alternativo, orden, es_principal, creado_en
) VALUES
-- Producto 1: Ron Diplomático
(1,  1, 'https://cdn.drinkgo.pe/prod/ron-diplomatico-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/ron-diplomatico-750.jpg',
      'Ron Diplomático Reserva Exclusiva 12 Años 750ml', 1, 1, NOW()),
(2,  1, 'https://cdn.drinkgo.pe/prod/ron-diplomatico-750-b.jpg',
      'https://cdn.drinkgo.pe/prod/th/ron-diplomatico-750-b.jpg',
      'Ron Diplomático etiqueta trasera', 2, 0, NOW()),
-- Producto 2: Pisco Quebranta
(3,  2, 'https://cdn.drinkgo.pe/prod/pisco-quebranta-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/pisco-quebranta-750.jpg',
      'Pisco Quebranta Acholado 750ml', 1, 1, NOW()),
-- Producto 3: Vino Casillero
(4,  3, 'https://cdn.drinkgo.pe/prod/vino-casillero-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/vino-casillero-750.jpg',
      'Vino Casillero del Diablo Reserva Cabernet Sauvignon', 1, 1, NOW()),
-- Producto 4: Cerveza Cusqueña
(5,  4, 'https://cdn.drinkgo.pe/prod/cusquena-6pack.jpg',
      'https://cdn.drinkgo.pe/prod/th/cusquena-6pack.jpg',
      'Cerveza Cusqueña Dorada 330ml Six-Pack', 1, 1, NOW()),
-- Producto 5: Johnnie Walker
(6,  5, 'https://cdn.drinkgo.pe/prod/jw-black-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/jw-black-750.jpg',
      'Johnnie Walker Black Label 750ml', 1, 1, NOW()),
(7,  5, 'https://cdn.drinkgo.pe/prod/jw-black-750-b.jpg',
      'https://cdn.drinkgo.pe/prod/th/jw-black-750-b.jpg',
      'Johnnie Walker Black Label caja', 2, 0, NOW()),
-- Producto 6: Absolut Vodka
(8,  6, 'https://cdn.drinkgo.pe/prod/absolut-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/absolut-750.jpg',
      'Absolut Vodka Original 750ml', 1, 1, NOW()),
-- Producto 7: Pilsen Callao
(9,  7, 'https://cdn.drinkgo.pe/prod/pilsen-620.jpg',
      'https://cdn.drinkgo.pe/prod/th/pilsen-620.jpg',
      'Cerveza Pilsen Callao 620ml', 1, 1, NOW()),
-- Producto 8: Tanqueray
(10, 8, 'https://cdn.drinkgo.pe/prod/tanqueray-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/tanqueray-750.jpg',
      'Tanqueray London Dry Gin 750ml', 1, 1, NOW()),
-- Producto 9: José Cuervo
(11, 9, 'https://cdn.drinkgo.pe/prod/jose-cuervo-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/jose-cuervo-750.jpg',
      'José Cuervo Especial Silver 750ml', 1, 1, NOW()),
-- Producto 10: Chandon
(12, 10,'https://cdn.drinkgo.pe/prod/chandon-brut-750.jpg',
      'https://cdn.drinkgo.pe/prod/th/chandon-brut-750.jpg',
      'Espumante Chandon Brut 750ml', 1, 1, NOW());

-- ============================================================
-- 5. DISPONIBILIDAD POR SEDE (productos_sedes)
-- ============================================================
INSERT INTO productos_sedes (
    id, producto_id, sede_id, precio_venta_especial,
    esta_disponible, creado_en, actualizado_en
) VALUES
-- Sede 1 (San Isidro): todos los productos disponibles
(1,  1, 1, NULL,   1, NOW(), NOW()),
(2,  2, 1, NULL,   1, NOW(), NOW()),
(3,  3, 1, NULL,   1, NOW(), NOW()),
(4,  4, 1, NULL,   1, NOW(), NOW()),
(5,  5, 1, NULL,   1, NOW(), NOW()),
(6,  6, 1, NULL,   1, NOW(), NOW()),
(7,  7, 1, NULL,   1, NOW(), NOW()),
(8,  8, 1, NULL,   1, NOW(), NOW()),
(9,  9, 1, NULL,   1, NOW(), NOW()),
(10, 10,1, NULL,   1, NOW(), NOW()),
-- Sede 2 (Los Olivos): selección + algunos con precio especial
(11, 1, 2, 155.00, 1, NOW(), NOW()),   -- Ron Diplomático precio especial
(12, 2, 2, NULL,   1, NOW(), NOW()),
(13, 3, 2, NULL,   1, NOW(), NOW()),
(14, 4, 2, 32.00,  1, NOW(), NOW()),   -- Cusqueña precio especial
(15, 5, 2, NULL,   0, NOW(), NOW()),   -- JW no disponible en sede 2
(16, 7, 2, 9.50,   1, NOW(), NOW()),   -- Pilsen precio especial
(17, 9, 2, NULL,   1, NOW(), NOW()),
(18, 10,2, NULL,   1, NOW(), NOW());

-- ============================================================
-- 6. COMBOS PROMOCIONALES
-- ============================================================

-- Primero crear productos virtuales tipo combo (IDs 11-12)
INSERT INTO productos (
    id, negocio_id, sku, nombre,
    stock_minimo, es_perecible, precio_compra, esta_activo
) VALUES
(11, 1, 'COMBO-FIESTA-01', 'Combo Fiesta Premium',
 0, 0, 0.00, 1),
(12, 1, 'COMBO-BRINDIS-01','Combo Brindis Especial',
 0, 0, 0.00, 1);

INSERT INTO combos (
    id, negocio_id, producto_id, nombre, descripcion,
    precio_combo, precio_total_original, porcentaje_descuento,
    max_usos, usos_actuales, valido_desde, valido_hasta,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 11, 'Combo Fiesta Premium',
 'Pack ideal para tu fiesta: Johnnie Walker Black + 2 Six-Pack Cusqueña + Pisco Quebranta',
 260.00, 298.00, 12.75,
 NULL, 12, '2026-01-01 00:00:00', '2026-12-31 23:59:59',
 1, NOW(), NOW()),
(2, 1, 12, 'Combo Brindis Especial',
 'Celebra con estilo: Chandon Brut + Ron Diplomático',
 220.00, 250.00, 12.00,
 50, 8, '2026-02-01 00:00:00', '2026-06-30 23:59:59',
 1, NOW(), NOW());

INSERT INTO detalle_combos (
    id, combo_id, producto_id, cantidad, precio_unitario, orden
) VALUES
-- Combo 1: Fiesta Premium
(1, 1, 5, 1, 180.00, 1),   -- Johnnie Walker Black ×1
(2, 1, 4, 2,  34.00, 2),   -- Cusqueña Six-Pack ×2
(3, 1, 2, 1,  50.00, 3),   -- Pisco Quebranta ×1
-- Combo 2: Brindis Especial
(4, 2, 10, 1, 95.00, 1),   -- Chandon Brut ×1
(5, 2, 1,  1, 155.00, 2);  -- Ron Diplomático ×1

-- ============================================================
-- 7. ETIQUETAS DE PRODUCTOS
-- ============================================================
INSERT INTO etiquetas_producto (id, negocio_id, nombre, slug, color) VALUES
(1, 1, 'Nuevo',          'nuevo',          '#28a745'),
(2, 1, 'Más Vendido',    'mas-vendido',    '#007bff'),
(3, 1, 'Oferta',         'oferta',         '#dc3545'),
(4, 1, 'Premium',        'premium',        '#ffc107'),
(5, 1, 'Importado',      'importado',      '#6f42c1'),
(6, 1, 'Edición Limitada','edicion-limitada','#fd7e14'),
(7, 2, 'Oferta',         'oferta',         '#dc3545'),
(8, 2, 'Popular',        'popular',        '#17a2b8');

-- Asignación de etiquetas a productos
INSERT INTO asignacion_etiquetas_producto (producto_id, etiqueta_id) VALUES
-- Ron Diplomático: Premium, Importado
(1, 4), (1, 5),
-- Pisco Quebranta: Más Vendido
(2, 2),
-- Vino Casillero: Importado
(3, 5),
-- Cusqueña: Más Vendido
(4, 2),
-- Johnnie Walker: Premium, Importado
(5, 4), (5, 5),
-- Absolut: Importado
(6, 5),
-- Pilsen: Más Vendido
(7, 2),
-- Tanqueray: Premium, Importado
(8, 4), (8, 5),
-- José Cuervo: Importado, Nuevo
(9, 5), (9, 1),
-- Chandon: Nuevo, Premium
(10, 1), (10, 4);

-- ============================================================
-- FIN v4
-- ============================================================