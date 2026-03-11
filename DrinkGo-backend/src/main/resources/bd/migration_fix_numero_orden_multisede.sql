-- ============================================
-- Migration: Fix numero_orden unique constraint
-- Problema: numero_orden tiene UNIQUE global, impide que distintos negocios
--           tengan el mismo número de orden (ej: OC-20260311-001).
-- Solución: Cambiar a UNIQUE compuesto (numero_orden + negocio_id).
-- ============================================

-- 1. Eliminar el constraint UNIQUE global sobre numero_orden
ALTER TABLE ordenes_compra DROP INDEX UK2rutng80wjqhuj2blxh2dxw1d;

-- 2. Crear un nuevo UNIQUE compuesto (numero_orden + negocio_id)
ALTER TABLE ordenes_compra ADD CONSTRAINT uk_ordenes_compra_numero_negocio UNIQUE (numero_orden, negocio_id);
