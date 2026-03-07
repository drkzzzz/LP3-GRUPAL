-- Agrega columna tiene_mesas a la tabla sedes
-- Indica si la sede gestiona mesas para atención en local

ALTER TABLE sedes
  ADD COLUMN tiene_mesas TINYINT(1) NOT NULL DEFAULT 0
  AFTER recojo_habilitado;
