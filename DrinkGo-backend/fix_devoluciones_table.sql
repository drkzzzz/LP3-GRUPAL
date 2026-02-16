-- Script para corregir la tabla devoluciones
-- Elimina monto_total y asegura que existen subtotal, monto_impuesto, total

USE drinkgo_db;

-- Ver estructura actual
DESCRIBE devoluciones;

-- Eliminar monto_total si existe
ALTER TABLE devoluciones 
DROP COLUMN IF EXISTS monto_total;

-- Asegurar que existen las columnas correctas
ALTER TABLE devoluciones 
ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER detalle_motivo;

ALTER TABLE devoluciones 
ADD COLUMN IF NOT EXISTS monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER subtotal;

ALTER TABLE devoluciones 
ADD COLUMN IF NOT EXISTS total DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER monto_impuesto;

-- Verificar cambios
DESCRIBE devoluciones;

SELECT 'Tabla actualizada exitosamente' AS resultado;
