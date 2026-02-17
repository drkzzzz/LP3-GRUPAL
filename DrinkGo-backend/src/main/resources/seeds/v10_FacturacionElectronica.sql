[-- Inserts mínimos para probar el backend del Bloque 10 (Facturación Electrónica, multi-negocio licorerías)]

-- 1. Negocio
INSERT INTO negocios (uuid, razon_social, nombre_comercial, tipo_negocio, email, estado, esta_activo)
VALUES (UUID(), 'Licorería DrinkGo SAC', 'DrinkGo', 'licoreria', 'info@drinkgo.com', 'activo', 1);

-- 2. Sede
INSERT INTO sedes (negocio_id, codigo, nombre, direccion, pais, esta_activo)
VALUES (1, 'SEDE-001', 'Sede Principal', 'Av. Principal 123', 'PE', 1);

-- 3. Series de facturación
INSERT INTO series_facturacion (negocio_id, sede_id, tipo_documento, prefijo_serie, numero_actual, esta_activo)
VALUES (1, 1, 'boleta', 'B001', 0, 1);
INSERT INTO series_facturacion (negocio_id, sede_id, tipo_documento, prefijo_serie, numero_actual, esta_activo)
VALUES (1, 1, 'factura', 'F001', 0, 1);
INSERT INTO series_facturacion (negocio_id, sede_id, tipo_documento, prefijo_serie, numero_actual, esta_activo)
VALUES (1, 1, 'nota_credito', 'FC01', 0, 1);
INSERT INTO series_facturacion (negocio_id, sede_id, tipo_documento, prefijo_serie, numero_actual, esta_activo)
VALUES (1, 1, 'nota_debito', 'BN01', 0, 1);

-- 4. Producto
INSERT INTO productos (negocio_id, sku, nombre, slug, precio_compra, precio_venta, stock_minimo, stock_maximo, punto_reorden, es_perecible, dias_vida_util, esta_activo)
VALUES (1, 'PROD-001', 'Pisco Quebranta 750ml', 'pisco-quebranta-750ml', 45.00, 60.00, 5, 100, 10, 1, 365, 1);
INSERT INTO productos (negocio_id, sku, nombre, slug, precio_compra, precio_venta, stock_minimo, stock_maximo, punto_reorden, es_perecible, dias_vida_util, esta_activo)
VALUES (1, 'PROD-002', 'Cerveza Cusqueña 330ml Six Pack', 'cerveza-cusquena-sixpack', 28.00, 40.00, 10, 200, 20, 1, 180, 1);

-- 5. Documento de facturación (Boleta)
INSERT INTO documentos_facturacion (negocio_id, sede_id, serie_id, tipo_documento, serie, numero_correlativo, numero_completo, ruc_emisor, razon_social_emisor, direccion_emisor, tipo_documento_receptor, numero_documento_receptor, nombre_receptor, direccion_receptor, email_receptor, subtotal, total_descuento, total_gravado, total_isc, tasa_igv, total_igv, total, moneda, estado, estado_sunat, fecha_emision, creado_por)
VALUES (1, 1, 1, 'boleta', 'B001', 1, 'B001-00000001', '20123456789', 'Licorería DrinkGo S.A.C.', 'Av. Arequipa 1234, Lima', '1', '45678912', 'Juan Pérez García', 'Calle Los Olivos 456, Lima', 'juan@email.com', 118.00, 5.00, 113.00, 11.70, 18.00, 22.45, 147.15, 'PEN', 'emitido', 'pendiente', '2026-02-16', 1);

-- 6. Detalles de boleta
INSERT INTO detalle_documentos_facturacion (documento_id, producto_id, numero_item, descripcion, codigo_unidad, cantidad, precio_unitario, monto_descuento, monto_gravado, monto_isc, monto_igv, total)
VALUES (1, 1, 1, 'Pisco Quebranta 750ml', 'NIU', 2, 45.00, 5.00, 85.00, 8.50, 16.83, 110.33);
INSERT INTO detalle_documentos_facturacion (documento_id, producto_id, numero_item, descripcion, codigo_unidad, cantidad, precio_unitario, monto_descuento, monto_gravado, monto_isc, monto_igv, total)
VALUES (1, 2, 2, 'Cerveza Cusqueña 330ml Six Pack', 'NIU', 1, 28.00, 0, 28.00, 3.20, 5.62, 36.82);
