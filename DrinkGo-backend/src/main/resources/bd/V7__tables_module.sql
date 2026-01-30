-- =============================================================================
-- BD_DRINKGO - V7: Mesas Simplificadas para Licorería
-- Fecha: 2026-01-30
-- Descripción: Mesas simples para consumo en local (sin reservas, zonas, etc.)
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: mesa
-- =============================================================================
-- Mesas simples del establecimiento
CREATE TABLE IF NOT EXISTS mesa (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id         BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  
  numero          VARCHAR(20) NOT NULL,  -- "1", "2", "A1", etc.
  descripcion     VARCHAR(100),          -- "Cerca de la entrada", etc.
  
  -- Estado simple: disponible u ocupada
  ocupada         BOOLEAN NOT NULL DEFAULT FALSE,
  
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(sede_id, numero)
);

CREATE INDEX idx_mesa_tenant ON mesa(tenant_id);
CREATE INDEX idx_mesa_sede ON mesa(sede_id);
CREATE INDEX idx_mesa_disponible ON mesa(sede_id, ocupada) WHERE activo = TRUE;

CREATE TRIGGER mesa_set_updated_at
BEFORE UPDATE ON mesa FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE mesa IS 'Mesas del local para consumo en sitio';

-- =============================================================================
-- 2. TABLA: cuenta_mesa
-- =============================================================================
-- Cuenta de una mesa (historial de consumos)
CREATE TABLE IF NOT EXISTS cuenta_mesa (
  id                BIGSERIAL PRIMARY KEY,
  tenant_id         BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id           BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  mesa_id           BIGINT NOT NULL REFERENCES mesa(id) ON DELETE RESTRICT,
  
  -- Quién atiende
  atendido_por_id   BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
  
  -- Tiempos
  abierta_en        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  cerrada_en        TIMESTAMPTZ,
  
  -- Estado simple
  estado            VARCHAR(20) NOT NULL DEFAULT 'abierta'
                     CHECK (estado IN ('abierta', 'pagada', 'anulada')),
  
  -- Totales
  subtotal          NUMERIC(12,2) NOT NULL DEFAULT 0,
  descuento         NUMERIC(12,2) NOT NULL DEFAULT 0,
  total             NUMERIC(12,2) NOT NULL DEFAULT 0,
  
  notas             TEXT,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Solo una cuenta abierta por mesa
CREATE UNIQUE INDEX unq_cuenta_mesa_abierta 
ON cuenta_mesa(mesa_id) WHERE estado = 'abierta';

CREATE INDEX idx_cuenta_mesa_tenant ON cuenta_mesa(tenant_id);
CREATE INDEX idx_cuenta_mesa_sede ON cuenta_mesa(sede_id);
CREATE INDEX idx_cuenta_mesa_estado ON cuenta_mesa(estado);

CREATE TRIGGER cuenta_mesa_set_updated_at
BEFORE UPDATE ON cuenta_mesa FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE cuenta_mesa IS 'Cuentas de consumo en mesas';

-- =============================================================================
-- 3. TABLA: cuenta_mesa_item
-- =============================================================================
-- Productos pedidos en la cuenta
CREATE TABLE IF NOT EXISTS cuenta_mesa_item (
  id                BIGSERIAL PRIMARY KEY,
  cuenta_id         BIGINT NOT NULL REFERENCES cuenta_mesa(id) ON DELETE CASCADE,
  producto_id       BIGINT REFERENCES producto(id) ON DELETE SET NULL,
  combo_id          BIGINT REFERENCES combo(id) ON DELETE SET NULL,
  
  nombre_producto   VARCHAR(200) NOT NULL,
  cantidad          NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  precio_unitario   NUMERIC(12,2) NOT NULL CHECK (precio_unitario >= 0),
  descuento         NUMERIC(12,2) NOT NULL DEFAULT 0,
  total_item        NUMERIC(12,2) NOT NULL,
  
  -- Para pagos individuales: marcar qué items ya fueron pagados
  pagado            BOOLEAN NOT NULL DEFAULT FALSE,
  
  notas             VARCHAR(250),  -- "sin hielo", "con limón"
  agregado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  CONSTRAINT chk_cuenta_item_tipo CHECK (producto_id IS NOT NULL OR combo_id IS NOT NULL)
);

CREATE INDEX idx_cuenta_item_cuenta ON cuenta_mesa_item(cuenta_id);
CREATE INDEX idx_cuenta_item_pendientes ON cuenta_mesa_item(cuenta_id, pagado) WHERE pagado = FALSE;

COMMENT ON TABLE cuenta_mesa_item IS 'Items de cada cuenta de mesa';

-- =============================================================================
-- 4. TABLA: pago_cuenta_mesa
-- =============================================================================
-- Pagos de cuentas (soporta pagos mixtos: efectivo + yape)
CREATE TABLE IF NOT EXISTS pago_cuenta_mesa (
  id              BIGSERIAL PRIMARY KEY,
  cuenta_id       BIGINT NOT NULL REFERENCES cuenta_mesa(id) ON DELETE CASCADE,
  
  metodo_pago     drinkgo.payment_method NOT NULL,
  monto           NUMERIC(12,2) NOT NULL CHECK (monto > 0),
  referencia      VARCHAR(120),
  
  recibido_por_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  pagado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pago_cuenta_cuenta ON pago_cuenta_mesa(cuenta_id);

COMMENT ON TABLE pago_cuenta_mesa IS 'Pagos de cuentas de mesa';

-- =============================================================================
-- 5. TABLA: pago_item_detalle
-- =============================================================================
-- Qué items cubre cada pago (para pagos individuales)
CREATE TABLE IF NOT EXISTS pago_item_detalle (
  id              BIGSERIAL PRIMARY KEY,
  pago_id         BIGINT NOT NULL REFERENCES pago_cuenta_mesa(id) ON DELETE CASCADE,
  item_id         BIGINT NOT NULL REFERENCES cuenta_mesa_item(id) ON DELETE CASCADE,
  
  UNIQUE(item_id)  -- Cada item solo puede estar en un pago
);

CREATE INDEX idx_pago_item_pago ON pago_item_detalle(pago_id);

COMMENT ON TABLE pago_item_detalle IS 'Detalle de qué items cubre cada pago';

-- =============================================================================
-- 5. FUNCIÓN: Recalcular totales de cuenta
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.recalcular_totales_cuenta_mesa()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  v_subtotal NUMERIC(12,2);
  v_descuento NUMERIC(12,2);
  v_cuenta_id BIGINT;
BEGIN
  v_cuenta_id := COALESCE(NEW.cuenta_id, OLD.cuenta_id);
  
  SELECT 
    COALESCE(SUM(cantidad * precio_unitario), 0),
    COALESCE(SUM(descuento), 0)
  INTO v_subtotal, v_descuento
  FROM drinkgo.cuenta_mesa_item
  WHERE cuenta_id = v_cuenta_id;
  
  UPDATE drinkgo.cuenta_mesa
  SET subtotal = v_subtotal,
      descuento = v_descuento,
      total = GREATEST(0, v_subtotal - v_descuento),
      actualizado_en = NOW()
  WHERE id = v_cuenta_id;
  
  RETURN COALESCE(NEW, OLD);
END $$;

CREATE TRIGGER cuenta_mesa_item_recalcular
AFTER INSERT OR UPDATE OR DELETE ON cuenta_mesa_item
FOR EACH ROW
EXECUTE FUNCTION drinkgo.recalcular_totales_cuenta_mesa();

-- =============================================================================
-- 7. FUNCIÓN: Marcar items como pagados tras un pago
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.marcar_items_pagados()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  -- Marcar como pagados los items vinculados a este pago
  UPDATE drinkgo.cuenta_mesa_item
  SET pagado = TRUE
  WHERE id IN (
    SELECT item_id FROM drinkgo.pago_item_detalle WHERE pago_id = NEW.pago_id
  );
  
  RETURN NEW;
END $$;

CREATE TRIGGER pago_item_marcar_pagado
AFTER INSERT ON pago_item_detalle
FOR EACH ROW
EXECUTE FUNCTION drinkgo.marcar_items_pagados();

-- =============================================================================
-- 8. FUNCIÓN: Verificar si cuenta está completamente pagada
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.verificar_cuenta_pagada()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  v_cuenta_id BIGINT;
  v_items_pendientes INT;
BEGIN
  -- Obtener cuenta_id desde el item que se actualizó
  SELECT cuenta_id INTO v_cuenta_id
  FROM drinkgo.cuenta_mesa_item WHERE id = NEW.id;
  
  -- Contar items no pagados
  SELECT COUNT(*) INTO v_items_pendientes
  FROM drinkgo.cuenta_mesa_item
  WHERE cuenta_id = v_cuenta_id AND pagado = FALSE;
  
  -- Si no hay items pendientes, cerrar la cuenta automáticamente
  IF v_items_pendientes = 0 THEN
    UPDATE drinkgo.cuenta_mesa
    SET estado = 'pagada',
        cerrada_en = NOW()
    WHERE id = v_cuenta_id AND estado = 'abierta';
  END IF;
  
  RETURN NEW;
END $$;

CREATE TRIGGER cuenta_item_verificar_pagada
AFTER UPDATE OF pagado ON cuenta_mesa_item
FOR EACH ROW
WHEN (NEW.pagado = TRUE)
EXECUTE FUNCTION drinkgo.verificar_cuenta_pagada();

-- =============================================================================
-- 9. FUNCIÓN: Actualizar estado de mesa (ocupada/disponible)
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.actualizar_estado_mesa()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF TG_OP = 'INSERT' THEN
    -- Nueva cuenta: mesa ocupada
    UPDATE drinkgo.mesa SET ocupada = TRUE WHERE id = NEW.mesa_id;
  ELSIF TG_OP = 'UPDATE' THEN
    -- Cuenta cerrada (pagada o anulada): mesa disponible
    IF NEW.estado IN ('pagada', 'anulada') AND OLD.estado = 'abierta' THEN
      UPDATE drinkgo.mesa SET ocupada = FALSE WHERE id = NEW.mesa_id;
    END IF;
  END IF;
  
  RETURN NEW;
END $$;

CREATE TRIGGER cuenta_actualizar_mesa
AFTER INSERT OR UPDATE ON cuenta_mesa
FOR EACH ROW
EXECUTE FUNCTION drinkgo.actualizar_estado_mesa();
