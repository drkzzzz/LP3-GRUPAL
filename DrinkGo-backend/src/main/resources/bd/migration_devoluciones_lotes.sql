-- ============================================================
-- Migration: Ajustes módulo de devoluciones
-- - Agrega estado 'devuelta' a ventas
-- - Agrega lote_id a detalle_devoluciones para devoluciones a proveedores basadas en lotes
-- ============================================================

-- 1. Agregar 'devuelta' al ENUM de estado de ventas
ALTER TABLE ventas MODIFY COLUMN estado ENUM('pendiente','completada','parcialmente_pagada','cancelada','reembolsada','anulada','devuelta') NOT NULL DEFAULT 'pendiente';

-- 2. Agregar columna lote_id a detalle_devoluciones
-- NOTA: la columna lote_id ya existe en la tabla, solo agregar FK si no existe
-- ALTER TABLE detalle_devoluciones ADD COLUMN lote_id BIGINT UNSIGNED NULL AFTER almacen_id;
-- Si la FK tampoco existe, ejecutar:
-- ALTER TABLE detalle_devoluciones ADD CONSTRAINT fk_detdev_lote FOREIGN KEY (lote_id) REFERENCES lotes_inventario(id);
