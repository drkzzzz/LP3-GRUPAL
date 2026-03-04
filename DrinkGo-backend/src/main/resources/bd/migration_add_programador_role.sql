-- ============================================================
-- MIGRACIÓN: Agrega rol 'programador' a usuarios_plataforma
-- Fecha: 2026-03-04
-- Descripción:
--   1. Extiende el ENUM rol con el valor 'programador'
--   2. Agrega columna modulos_asignados (JSON) para guardar
--      los códigos de módulo asignados al programador
-- ============================================================

USE drinkgo_db;

-- 1. Ampliar el ENUM de rol
ALTER TABLE usuarios_plataforma
    MODIFY COLUMN rol
        ENUM('superadmin','soporte_plataforma','visualizador_plataforma','programador')
        NOT NULL DEFAULT 'superadmin';

-- 2. Agregar columna de módulos asignados
ALTER TABLE usuarios_plataforma
    ADD COLUMN IF NOT EXISTS modulos_asignados JSON NULL
        COMMENT 'Array JSON de códigos de módulo asignados. Solo aplica para rol=programador. Ej: ["m.catalogo","m.devoluciones"]'
    AFTER rol;

-- Verificación
SELECT
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'drinkgo_db'
  AND TABLE_NAME   = 'usuarios_plataforma'
  AND COLUMN_NAME IN ('rol', 'modulos_asignados')
ORDER BY ORDINAL_POSITION;
