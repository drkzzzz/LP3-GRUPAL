-- Migration: Add lockout fields to usuarios table
-- Applies the same security lockout mechanism that platform users (usuarios_plataforma) already have

ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS intentos_fallidos_acceso INT UNSIGNED NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS bloqueado_hasta TIMESTAMP NULL DEFAULT NULL;
