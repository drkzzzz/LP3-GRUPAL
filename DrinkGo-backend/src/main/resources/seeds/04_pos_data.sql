-- ============================================================
-- SEED: DATOS POS - MÉTODOS DE PAGO Y CAJAS REGISTRADORAS
-- Usa WHERE NOT EXISTS para no duplicar al reiniciar backend
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- VARIABLES DE REFERENCIA
-- ============================================================
SET @negocio_donpepe_id      = (SELECT id FROM negocios WHERE ruc = '20123456789' LIMIT 1);
SET @negocio_labodega_id     = (SELECT id FROM negocios WHERE ruc = '20567890123' LIMIT 1);
SET @negocio_elimperio_id    = (SELECT id FROM negocios WHERE ruc = '20345678901' LIMIT 1);
SET @negocio_premiumwines_id = (SELECT id FROM negocios WHERE ruc = '20456789012' LIMIT 1);

SET @sede_donpepe_id         = (SELECT id FROM sedes WHERE negocio_id = @negocio_donpepe_id AND es_principal = 1 LIMIT 1);
SET @sede_labodega_id        = (SELECT id FROM sedes WHERE negocio_id = @negocio_labodega_id AND es_principal = 1 LIMIT 1);
SET @sede_elimperio_id       = (SELECT id FROM sedes WHERE negocio_id = @negocio_elimperio_id AND es_principal = 1 LIMIT 1);
SET @sede_premiumwines_id    = (SELECT id FROM sedes WHERE negocio_id = @negocio_premiumwines_id AND es_principal = 1 LIMIT 1);

-- ============================================================
-- MÉTODOS DE PAGO - DON PEPE
-- ============================================================
INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_donpepe_id, 'Efectivo', 'EFECTIVO', 'efectivo', NULL, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_donpepe_id AND codigo = 'EFECTIVO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_donpepe_id, 'Tarjeta de Crédito', 'TARJETA_CREDITO', 'tarjeta_credito', NULL, 1, 1, 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_donpepe_id AND codigo = 'TARJETA_CREDITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_donpepe_id, 'Tarjeta de Débito', 'TARJETA_DEBITO', 'tarjeta_debito', NULL, 1, 1, 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_donpepe_id AND codigo = 'TARJETA_DEBITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_donpepe_id, 'Yape', 'YAPE', 'yape', NULL, 1, 1, 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_donpepe_id AND codigo = 'YAPE' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_donpepe_id, 'Plin', 'PLIN', 'plin', NULL, 1, 1, 1, 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_donpepe_id AND codigo = 'PLIN' AND esta_activo = 1);

-- ============================================================
-- MÉTODOS DE PAGO - LA BODEGA
-- ============================================================
INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_labodega_id, 'Efectivo', 'EFECTIVO', 'efectivo', NULL, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_labodega_id AND codigo = 'EFECTIVO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_labodega_id, 'Tarjeta de Crédito', 'TARJETA_CREDITO', 'tarjeta_credito', NULL, 1, 1, 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_labodega_id AND codigo = 'TARJETA_CREDITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_labodega_id, 'Tarjeta de Débito', 'TARJETA_DEBITO', 'tarjeta_debito', NULL, 1, 1, 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_labodega_id AND codigo = 'TARJETA_DEBITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_labodega_id, 'Yape', 'YAPE', 'yape', NULL, 1, 1, 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_labodega_id AND codigo = 'YAPE' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_labodega_id, 'Plin', 'PLIN', 'plin', NULL, 1, 1, 1, 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_labodega_id AND codigo = 'PLIN' AND esta_activo = 1);

-- ============================================================
-- MÉTODOS DE PAGO - EL IMPERIO
-- ============================================================
INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_elimperio_id, 'Efectivo', 'EFECTIVO', 'efectivo', NULL, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_elimperio_id AND codigo = 'EFECTIVO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_elimperio_id, 'Tarjeta de Crédito', 'TARJETA_CREDITO', 'tarjeta_credito', NULL, 1, 1, 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_elimperio_id AND codigo = 'TARJETA_CREDITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_elimperio_id, 'Tarjeta de Débito', 'TARJETA_DEBITO', 'tarjeta_debito', NULL, 1, 1, 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_elimperio_id AND codigo = 'TARJETA_DEBITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_elimperio_id, 'Yape', 'YAPE', 'yape', NULL, 1, 1, 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_elimperio_id AND codigo = 'YAPE' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_elimperio_id, 'Plin', 'PLIN', 'plin', NULL, 1, 1, 1, 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_elimperio_id AND codigo = 'PLIN' AND esta_activo = 1);

-- ============================================================
-- MÉTODOS DE PAGO - PREMIUM WINES
-- ============================================================
INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_premiumwines_id, 'Efectivo', 'EFECTIVO', 'efectivo', NULL, 1, 1, 0, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_premiumwines_id AND codigo = 'EFECTIVO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_premiumwines_id, 'Tarjeta de Crédito', 'TARJETA_CREDITO', 'tarjeta_credito', NULL, 1, 1, 1, 2, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_premiumwines_id AND codigo = 'TARJETA_CREDITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_premiumwines_id, 'Tarjeta de Débito', 'TARJETA_DEBITO', 'tarjeta_debito', NULL, 1, 1, 1, 3, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_premiumwines_id AND codigo = 'TARJETA_DEBITO' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_premiumwines_id, 'Yape', 'YAPE', 'yape', NULL, 1, 1, 1, 4, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_premiumwines_id AND codigo = 'YAPE' AND esta_activo = 1);

INSERT INTO metodos_pago (negocio_id, nombre, codigo, tipo, configuracion_json, esta_activo, disponible_pos, disponible_tienda_online, orden, creado_en, actualizado_en)
SELECT @negocio_premiumwines_id, 'Plin', 'PLIN', 'plin', NULL, 1, 1, 1, 5, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM metodos_pago WHERE negocio_id = @negocio_premiumwines_id AND codigo = 'PLIN' AND esta_activo = 1);

-- ============================================================
-- CAJAS REGISTRADORAS - DON PEPE
-- ============================================================
INSERT INTO cajas_registradoras (negocio_id, sede_id, nombre_caja, codigo, monto_apertura_defecto, esta_activo, creado_en, actualizado_en)
SELECT @negocio_donpepe_id, @sede_donpepe_id, 'Caja Principal', 'CAJA-001', 100.00, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM cajas_registradoras WHERE negocio_id = @negocio_donpepe_id AND codigo = 'CAJA-001' AND esta_activo = 1);

INSERT INTO cajas_registradoras (negocio_id, sede_id, nombre_caja, codigo, monto_apertura_defecto, esta_activo, creado_en, actualizado_en)
SELECT @negocio_donpepe_id, @sede_donpepe_id, 'Caja Secundaria', 'CAJA-002', 50.00, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM cajas_registradoras WHERE negocio_id = @negocio_donpepe_id AND codigo = 'CAJA-002' AND esta_activo = 1);

-- ============================================================
-- CAJAS REGISTRADORAS - LA BODEGA
-- ============================================================
INSERT INTO cajas_registradoras (negocio_id, sede_id, nombre_caja, codigo, monto_apertura_defecto, esta_activo, creado_en, actualizado_en)
SELECT @negocio_labodega_id, @sede_labodega_id, 'Caja Principal', 'CAJA-001', 100.00, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM cajas_registradoras WHERE negocio_id = @negocio_labodega_id AND codigo = 'CAJA-001' AND esta_activo = 1);

-- ============================================================
-- CAJAS REGISTRADORAS - EL IMPERIO
-- ============================================================
INSERT INTO cajas_registradoras (negocio_id, sede_id, nombre_caja, codigo, monto_apertura_defecto, esta_activo, creado_en, actualizado_en)
SELECT @negocio_elimperio_id, @sede_elimperio_id, 'Caja Principal', 'CAJA-001', 200.00, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM cajas_registradoras WHERE negocio_id = @negocio_elimperio_id AND codigo = 'CAJA-001' AND esta_activo = 1);

-- ============================================================
-- CAJAS REGISTRADORAS - PREMIUM WINES
-- ============================================================
INSERT INTO cajas_registradoras (negocio_id, sede_id, nombre_caja, codigo, monto_apertura_defecto, esta_activo, creado_en, actualizado_en)
SELECT @negocio_premiumwines_id, @sede_premiumwines_id, 'Caja Principal', 'CAJA-001', 150.00, 1, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM cajas_registradoras WHERE negocio_id = @negocio_premiumwines_id AND codigo = 'CAJA-001' AND esta_activo = 1);
