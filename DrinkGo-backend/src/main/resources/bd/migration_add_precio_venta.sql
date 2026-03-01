-- ============================================================
-- MIGRACIÓN: AGREGAR PRECIO DE VENTA A PRODUCTOS
-- Fecha: 01 de Marzo, 2026
-- Descripción: Agrega campos de precio_venta y precio_venta_minimo
--              a la tabla productos para soportar ventas
-- ============================================================

USE licores_drinkgo;

-- Agregar campos de precio de venta
ALTER TABLE productos 
ADD COLUMN precio_venta DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Precio de venta al público' AFTER impuesto_incluido,
ADD COLUMN precio_venta_minimo DECIMAL(10,2) NULL COMMENT 'Precio mínimo de venta permitido (opcional)' AFTER precio_venta;

-- Actualizar productos existentes con precio de venta = 0 (debe configurarse manualmente)
-- Los administradores deberán actualizar estos precios en el sistema

-- Crear índice para búsquedas por rango de precio
CREATE INDEX idx_prod_precio_venta ON productos(negocio_id, precio_venta);

-- Verificar cambios
SELECT 
    'productos' as tabla,
    COUNT(*) as total_productos,
    SUM(CASE WHEN precio_venta = 0 THEN 1 ELSE 0 END) as productos_sin_precio
FROM productos
WHERE esta_activo = 1;

COMMIT;
