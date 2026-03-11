-- ============================================================
-- Migración: Corregir unique constraint en documentos_facturacion
-- ============================================================
-- PROBLEMA: numero_documento tenía un UNIQUE global (generado por Hibernate).
--   Esto impedía que dos negocios tuvieran el mismo código de serie
--   (ej: ambos con B001-00000001), causando "Transaction rollback-only"
--   al intentar registrar ventas en el segundo negocio.
--
-- FIX: Cambiar a UNIQUE compuesto (negocio_id, numero_documento).
--   Cada negocio ahora tiene su propio espacio de numeración.
-- ============================================================

-- 1. Eliminar el UNIQUE global generado por Hibernate
ALTER TABLE documentos_facturacion
  DROP INDEX UK4xn0nlfb00dip4eqrdhgnp0sw;

-- 2. Crear UNIQUE compuesto por negocio
ALTER TABLE documentos_facturacion
  ADD CONSTRAINT uk_docfac_negocio_numdoc UNIQUE (negocio_id, numero_documento);
