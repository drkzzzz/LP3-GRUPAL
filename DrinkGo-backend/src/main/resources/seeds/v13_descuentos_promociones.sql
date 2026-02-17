-- ============================================================
-- DATOS DE PRUEBA - BLOQUE 13: DESCUENTOS Y PROMOCIONES
-- Sistema DrinkGo - Multi-tenant
-- ============================================================
-- IMPORTANTE: Ejecutar DESPUÉS de tener datos de:
--   - negocios (negocio_id = 1)
--   - usuarios (usuario_id = 1, 2, 3)
--   - sedes (sede_id = 1, 2)
--   - productos (producto_id = 1..20+)
--   - categorias (categoria_id = 1..5+)
--   - marcas (marca_id = 1..5+)
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 13.1 PROMOCIONES
-- ============================================================

INSERT INTO promociones (
    negocio_id, nombre, codigo, descripcion,
    tipo_descuento, valor_descuento, monto_minimo_compra, monto_maximo_descuento,
    max_usos, max_usos_por_cliente, usos_actuales,
    aplica_a, valido_desde, valido_hasta,
    esta_activo, es_combinable, canales,
    creado_por, creado_en, actualizado_en
) VALUES
-- Promoción 1: Descuento porcentaje — ACTIVA y VIGENTE (general)
(1, 'Verano Refrescante 2026', 'VERANO2026', 'Descuento del 15% en toda la tienda durante la temporada de verano.',
 'porcentaje', 15.00, 50.00, 100.00,
 500, 3, 87,
 'todo', '2026-01-15 00:00:00', '2026-03-31 23:59:59',
 1, 0, '["pos","tienda_online"]',
 1, NOW(), NOW()),

-- Promoción 2: Monto fijo — ACTIVA y VIGENTE (por categoría)
(1, 'Descuento Vinos Selectos', 'VINOS10', 'S/10.00 de descuento en vinos seleccionados.',
 'monto_fijo', 10.00, 80.00, NULL,
 200, 2, 34,
 'categoria', '2026-02-01 00:00:00', '2026-02-28 23:59:59',
 1, 1, '["pos","tienda_online"]',
 1, NOW(), NOW()),

-- Promoción 3: Compre X lleve Y — ACTIVA y VIGENTE (por producto)
(1, 'Compra 3 Cervezas Lleva 4', 'CERVEZA3X4', 'Compra 3 cervezas artesanales y llévate una gratis.',
 'compre_x_lleve_y', 1.00, NULL, NULL,
 300, 5, 112,
 'producto', '2026-02-01 00:00:00', '2026-04-30 23:59:59',
 1, 0, '["pos"]',
 1, NOW(), NOW()),

-- Promoción 4: Envío gratis — ACTIVA y VIGENTE (tienda online)
(1, 'Envío Gratis +S/100', 'ENVIOGRATIS', 'Envío gratuito en pedidos superiores a S/100.00.',
 'envio_gratis', 0.00, 100.00, NULL,
 NULL, NULL, 245,
 'todo', '2026-01-01 00:00:00', '2026-06-30 23:59:59',
 1, 1, '["tienda_online"]',
 1, NOW(), NOW()),

-- Promoción 5: Porcentaje — ACTIVA y VIGENTE (por marca)
(1, 'Festival de Piscos', 'PISCO20', '20% de descuento en todos los piscos de marca seleccionada.',
 'porcentaje', 20.00, 60.00, 50.00,
 150, 2, 28,
 'marca', '2026-02-10 00:00:00', '2026-03-10 23:59:59',
 1, 0, '["pos","tienda_online"]',
 2, NOW(), NOW()),

-- Promoción 6: Monto fijo — ACTIVA y VIGENTE (por combo)
(1, 'Combo Happy Hour', 'HAPPY15', 'S/15.00 de descuento en combos durante happy hour.',
 'monto_fijo', 15.00, 45.00, NULL,
 100, 1, 15,
 'combo', '2026-02-01 00:00:00', '2026-03-31 23:59:59',
 1, 0, '["pos"]',
 1, NOW(), NOW()),

-- Promoción 7: Porcentaje — VENCIDA (ya no vigente)
(1, 'Año Nuevo 2026', 'ANONUEVO26', 'Descuento del 25% para celebrar el Año Nuevo.',
 'porcentaje', 25.00, 100.00, 80.00,
 1000, 2, 456,
 'todo', '2025-12-28 00:00:00', '2026-01-05 23:59:59',
 0, 0, '["pos","tienda_online"]',
 1, '2025-12-20 10:00:00', '2026-01-06 00:00:00'),

-- Promoción 8: Monto fijo — VENCIDA
(1, 'Fiestas Patrias 2025', 'PERU2025', 'S/20.00 de descuento en compras mayores a S/150.00 por Fiestas Patrias.',
 'monto_fijo', 20.00, 150.00, NULL,
 500, 1, 389,
 'todo', '2025-07-20 00:00:00', '2025-07-31 23:59:59',
 0, 0, '["pos","tienda_online"]',
 1, '2025-07-15 09:00:00', '2025-08-01 00:00:00'),

-- Promoción 9: Porcentaje — INACTIVA manualmente (deshabilitada)
(1, 'Descuento Empleados', 'STAFF30', 'Descuento especial del 30% para empleados del negocio.',
 'porcentaje', 30.00, NULL, 200.00,
 NULL, NULL, 12,
 'todo', '2026-01-01 00:00:00', '2026-12-31 23:59:59',
 0, 0, '["pos"]',
 1, NOW(), NOW()),

-- Promoción 10: Porcentaje — ACTIVA y VIGENTE (futuro cercano)
(1, 'Día de la Mujer 2026', 'MUJER2026', '10% de descuento en vinos y espumantes por el Día de la Mujer.',
 'porcentaje', 10.00, 40.00, 60.00,
 200, 2, 0,
 'categoria', '2026-03-06 00:00:00', '2026-03-10 23:59:59',
 1, 1, '["pos","tienda_online"]',
 2, NOW(), NOW()),

-- Promoción 11: Compre X lleve Y — ACTIVA y VIGENTE (snacks)
(1, 'Snacks 2x1', 'SNACK2X1', 'Compra un snack y llévate otro gratis.',
 'compre_x_lleve_y', 1.00, NULL, NULL,
 100, 3, 45,
 'producto', '2026-02-01 00:00:00', '2026-02-28 23:59:59',
 1, 1, '["pos"]',
 3, NOW(), NOW()),

-- Promoción 12: Porcentaje — ACTIVA (cupón exclusivo tienda online)
(1, 'Bienvenida Online', 'WELCOME10', '10% de descuento en tu primera compra online.',
 'porcentaje', 10.00, 30.00, 50.00,
 NULL, 1, 67,
 'todo', '2026-01-01 00:00:00', '2026-12-31 23:59:59',
 1, 0, '["tienda_online"]',
 1, NOW(), NOW());

-- ============================================================
-- 13.2 CONDICIONES DE PROMOCIÓN
-- (Vincula promociones con entidades específicas)
-- ============================================================

INSERT INTO condiciones_promocion (
    promocion_id, tipo_entidad, entidad_id
) VALUES
-- Promoción 2 (Vinos Selectos) → aplica a categoría de vinos (categoria_id = 1)
(2, 'categoria', 1),

-- Promoción 3 (Cerveza 3x4) → aplica a productos de cerveza específicos
(3, 'producto', 5),
(3, 'producto', 6),
(3, 'producto', 7),

-- Promoción 5 (Festival de Piscos) → aplica a marca de piscos (marca_id = 3)
(5, 'marca', 3),

-- Promoción 10 (Día de la Mujer) → aplica a categoría de vinos y espumantes
(10, 'categoria', 1),
(10, 'categoria', 2),

-- Promoción 11 (Snacks 2x1) → aplica a producto de snacks específico
(11, 'producto', 20);

-- ============================================================
-- 13.3 PROMOCIONES POR SEDE
-- (Define en qué sedes aplica cada promoción)
-- ============================================================

INSERT INTO promociones_sedes (promocion_id, sede_id) VALUES
-- Promoción 1 (Verano Refrescante) → todas las sedes
(1, 1),
(1, 2),

-- Promoción 2 (Vinos Selectos) → solo sede principal
(2, 1),

-- Promoción 3 (Cerveza 3x4) → todas las sedes
(3, 1),
(3, 2),

-- Promoción 4 (Envío Gratis) → todas las sedes (online)
(4, 1),
(4, 2),

-- Promoción 5 (Festival de Piscos) → sede principal
(5, 1),

-- Promoción 6 (Combo Happy Hour) → sede principal
(6, 1),

-- Promoción 7 (Año Nuevo — vencida) → todas las sedes
(7, 1),
(7, 2),

-- Promoción 8 (Fiestas Patrias — vencida) → todas las sedes
(8, 1),
(8, 2),

-- Promoción 9 (Empleados — inactiva) → todas las sedes
(9, 1),
(9, 2),

-- Promoción 10 (Día de la Mujer) → todas las sedes
(10, 1),
(10, 2),

-- Promoción 11 (Snacks 2x1) → solo sede principal
(11, 1),

-- Promoción 12 (Bienvenida Online) → todas las sedes
(12, 1),
(12, 2);

-- ============================================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================================
SELECT '=== RESUMEN DATOS BLOQUE 13 ===' AS '';
SELECT 'Promociones total:' AS Tabla, COUNT(*) AS Total FROM promociones WHERE negocio_id = 1;
SELECT 'Promociones activas:' AS Tabla, COUNT(*) AS Total FROM promociones WHERE negocio_id = 1 AND esta_activo = 1;
SELECT 'Promociones inactivas:' AS Tabla, COUNT(*) AS Total FROM promociones WHERE negocio_id = 1 AND esta_activo = 0;
SELECT 'Promociones por tipo_descuento:' AS '';
SELECT tipo_descuento, COUNT(*) AS cantidad FROM promociones WHERE negocio_id = 1 GROUP BY tipo_descuento;
SELECT 'Promociones por aplica_a:' AS '';
SELECT aplica_a, COUNT(*) AS cantidad FROM promociones WHERE negocio_id = 1 GROUP BY aplica_a;
SELECT 'Promociones vigentes (fecha actual):' AS Tabla,
    COUNT(*) AS Total FROM promociones
    WHERE negocio_id = 1 AND esta_activo = 1
    AND NOW() BETWEEN valido_desde AND valido_hasta;
SELECT 'Promociones vencidas:' AS Tabla,
    COUNT(*) AS Total FROM promociones
    WHERE negocio_id = 1 AND valido_hasta < NOW();
SELECT 'Condiciones de promoción:' AS Tabla, COUNT(*) AS Total FROM condiciones_promocion;
SELECT 'Promociones-Sedes:' AS Tabla, COUNT(*) AS Total FROM promociones_sedes;
