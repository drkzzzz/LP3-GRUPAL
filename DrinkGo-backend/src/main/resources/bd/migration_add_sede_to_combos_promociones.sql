-- ============================================================
-- MIGRACIÓN: Agregar sede_id a combos y promociones
-- Permite filtrar combos y promociones por sede
-- ============================================================
USE drinkgo_db;

-- ── COMBOS: agregar sede_id ──
ALTER TABLE combos
    ADD COLUMN IF NOT EXISTS sede_id BIGINT NULL AFTER negocio_id;

ALTER TABLE combos
    ADD CONSTRAINT fk_combos_sede
    FOREIGN KEY (sede_id) REFERENCES sedes(id);

-- Rellenar sede_id con la sede principal del negocio para registros existentes
UPDATE combos c
    JOIN sedes s ON s.negocio_id = c.negocio_id AND s.es_principal = 1
SET c.sede_id = s.id
WHERE c.sede_id IS NULL;

-- ── PROMOCIONES: agregar sede_id ──
ALTER TABLE promociones
    ADD COLUMN IF NOT EXISTS sede_id BIGINT NULL AFTER negocio_id;

ALTER TABLE promociones
    ADD CONSTRAINT fk_promociones_sede
    FOREIGN KEY (sede_id) REFERENCES sedes(id);

-- Rellenar sede_id con la sede principal del negocio para registros existentes
UPDATE promociones p
    JOIN sedes s ON s.negocio_id = p.negocio_id AND s.es_principal = 1
SET p.sede_id = s.id
WHERE p.sede_id IS NULL;
