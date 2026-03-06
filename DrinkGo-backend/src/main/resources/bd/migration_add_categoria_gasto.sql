-- ============================================================
-- MIGRACIÓN: AGREGAR CATEGORÍA DE GASTO A GASTOS
-- Descripción: Agrega campo categoria_gasto_id a la tabla gastos
--              para vincular gastos con categorías creadas por el negocio.
-- NOTA: Con ddl-auto=update, Hibernate crea la columna automáticamente.
--       Este archivo es solo referencia en caso de necesitar ejecución manual.
-- ============================================================

USE drinkgo_db;

-- Agregar FK de categoría de gasto
ALTER TABLE gastos
ADD COLUMN categoria_gasto_id BIGINT NULL AFTER numero_gasto,
ADD CONSTRAINT fk_gastos_categoria_gasto
    FOREIGN KEY (categoria_gasto_id) REFERENCES categorias_gasto(id);
