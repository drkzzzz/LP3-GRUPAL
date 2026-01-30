-- =============================================================================
-- BD_DRINKGO - V6: Sistema de Inventario con Lotes y FIFO (CRÍTICO)
-- Fecha: 2026-01-26
-- Descripción: Gestión de inventario por lotes, fechas de vencimiento, FIFO
-- Nota: La tabla proveedor completa está en V13
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: lote_inventario (CRÍTICA - Manejo de Perecibles)
-- =============================================================================
-- Cada entrada de producto crea un lote con su fecha de vencimiento
-- FIFO: Se vende primero el lote con fecha_vencimiento más cercana
CREATE TABLE IF NOT EXISTS lote_inventario (
  id                    BIGSERIAL PRIMARY KEY,
  tenant_id             BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  -- Ubicación
  sede_id               BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  almacen_id            BIGINT NOT NULL REFERENCES almacen(id) ON DELETE RESTRICT,
  producto_id           BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  
  -- Identificación del lote
  codigo_lote           VARCHAR(50),  -- Código del proveedor/fabricante
  numero_ingreso        VARCHAR(30),  -- Número interno de ingreso
  
  -- Cantidades
  cantidad_inicial      NUMERIC(12,3) NOT NULL CHECK (cantidad_inicial > 0),
  cantidad_disponible   NUMERIC(12,3) NOT NULL CHECK (cantidad_disponible >= 0),
  
  -- Fechas críticas para FIFO
  fecha_entrada         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  fecha_fabricacion     DATE,
  fecha_vencimiento     DATE,  -- NULL = producto sin vencimiento (destilados)
  
  -- Costos
  costo_unitario        NUMERIC(12,4) NOT NULL CHECK (costo_unitario >= 0),
  costo_total           NUMERIC(14,2) GENERATED ALWAYS AS (cantidad_inicial * costo_unitario) STORED,
  
  -- Proveedor y compra asociada (FKs se agregan después en V13)
  proveedor_id          BIGINT,  -- FK a proveedor se agrega en V13
  compra_id             BIGINT,  -- FK a compra se agrega en V13
  
  -- Estado del lote
  estado                drinkgo.batch_status NOT NULL DEFAULT 'disponible',
  
  -- Auditoría
  registrado_por_id     BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  notas                 TEXT,
  creado_en             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  -- Validación: vencimiento debe ser >= fabricación
  CONSTRAINT chk_lote_fechas CHECK (
    fecha_fabricacion IS NULL OR fecha_vencimiento IS NULL 
    OR fecha_vencimiento >= fecha_fabricacion
  )
);

-- Índices CRÍTICOS para FIFO y alertas de vencimiento
CREATE INDEX idx_lote_tenant ON lote_inventario(tenant_id);
CREATE INDEX idx_lote_sede_producto ON lote_inventario(sede_id, producto_id);
CREATE INDEX idx_lote_producto ON lote_inventario(producto_id);

-- ÍNDICE FIFO: Prioriza por vencimiento cercano y entrada antigua
CREATE INDEX idx_lote_fifo ON lote_inventario(
  sede_id, 
  producto_id, 
  fecha_vencimiento ASC NULLS LAST, 
  fecha_entrada ASC
) WHERE estado = 'disponible' AND cantidad_disponible > 0;

-- ÍNDICE Alertas de vencimiento
CREATE INDEX idx_lote_vencimiento ON lote_inventario(fecha_vencimiento)
WHERE estado = 'disponible' AND cantidad_disponible > 0 AND fecha_vencimiento IS NOT NULL;

-- ÍNDICE Productos vencidos
CREATE INDEX idx_lote_vencido ON lote_inventario(fecha_vencimiento, estado)
WHERE fecha_vencimiento < CURRENT_DATE AND estado = 'disponible';

CREATE INDEX idx_lote_estado ON lote_inventario(tenant_id, estado);
CREATE INDEX idx_lote_compra ON lote_inventario(compra_id) WHERE compra_id IS NOT NULL;

CREATE TRIGGER lote_inventario_set_updated_at
BEFORE UPDATE ON lote_inventario FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

CREATE TRIGGER lote_check_tenant
BEFORE INSERT OR UPDATE ON lote_inventario FOR EACH ROW
EXECUTE FUNCTION drinkgo.check_tenant_isolation();

COMMENT ON TABLE lote_inventario IS 'Lotes de inventario con control de vencimiento y FIFO';
COMMENT ON COLUMN lote_inventario.fecha_vencimiento IS 'Fecha de caducidad. NULL para productos sin vencimiento (destilados)';
COMMENT ON COLUMN lote_inventario.estado IS 'FIFO prioriza estado=disponible con fecha_vencimiento más cercana';
COMMENT ON INDEX idx_lote_fifo IS 'Índice optimizado para selección FIFO: vencimiento cercano + entrada antigua';

-- =============================================================================
-- 2. TABLA: movimiento_inventario
-- =============================================================================
-- Registro de todos los movimientos de inventario (trazabilidad completa)
CREATE TABLE IF NOT EXISTS movimiento_inventario (
  id                BIGSERIAL PRIMARY KEY,
  tenant_id         BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  -- Referencias
  lote_id           BIGINT REFERENCES lote_inventario(id) ON DELETE SET NULL,
  producto_id       BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  sede_id           BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  
  -- Tipo y cantidad
  tipo_movimiento   VARCHAR(30) NOT NULL CHECK (tipo_movimiento IN (
    'entrada',          -- Ingreso de compra
    'salida',           -- Venta/pedido
    'ajuste_positivo',  -- Ajuste que suma
    'ajuste_negativo',  -- Ajuste que resta
    'merma',            -- Pérdida/rotura/vencimiento
    'transferencia_in', -- Recepción de otra sede
    'transferencia_out',-- Envío a otra sede
    'devolucion'        -- Devolución de cliente
  )),
  cantidad          NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  
  -- Stock resultante (snapshot)
  stock_anterior    NUMERIC(12,3),
  stock_nuevo       NUMERIC(12,3),
  
  -- Referencias de origen
  referencia_tipo   VARCHAR(50),  -- 'venta', 'pedido', 'compra', 'ajuste', etc.
  referencia_id     BIGINT,
  
  -- Auditoría
  motivo            VARCHAR(250),
  usuario_id        BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_movimiento_tenant ON movimiento_inventario(tenant_id);
CREATE INDEX idx_movimiento_lote ON movimiento_inventario(lote_id);
CREATE INDEX idx_movimiento_producto ON movimiento_inventario(producto_id);
CREATE INDEX idx_movimiento_sede ON movimiento_inventario(sede_id);
CREATE INDEX idx_movimiento_tipo ON movimiento_inventario(tipo_movimiento, creado_en);
CREATE INDEX idx_movimiento_fecha ON movimiento_inventario(creado_en);
CREATE INDEX idx_movimiento_referencia ON movimiento_inventario(referencia_tipo, referencia_id);

COMMENT ON TABLE movimiento_inventario IS 'Registro de trazabilidad de todos los movimientos de inventario';

-- =============================================================================
-- 3. TABLA: inventario_consolidado (Vista materializada como tabla)
-- =============================================================================
-- Stock consolidado por producto-sede (suma de todos los lotes disponibles)
-- Se actualiza automáticamente vía trigger
CREATE TABLE IF NOT EXISTS inventario_consolidado (
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id         BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  producto_id     BIGINT NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
  
  -- Cantidades
  stock_total     NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (stock_total >= 0),
  stock_minimo    NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),
  stock_maximo    NUMERIC(12,3),
  punto_reorden   NUMERIC(12,3),
  
  -- Alertas
  lotes_por_vencer INT NOT NULL DEFAULT 0,  -- Lotes que vencen en 30 días
  
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  PRIMARY KEY (sede_id, producto_id)
);

CREATE INDEX idx_inv_consolidado_tenant ON inventario_consolidado(tenant_id);
CREATE INDEX idx_inv_consolidado_bajo_stock ON inventario_consolidado(tenant_id, sede_id)
WHERE stock_total <= stock_minimo;

COMMENT ON TABLE inventario_consolidado IS 'Stock total por producto-sede (suma de lotes disponibles)';
COMMENT ON COLUMN inventario_consolidado.lotes_por_vencer IS 'Contador de lotes próximos a vencer (alertas)';

-- =============================================================================
-- 4. FUNCIÓN: Actualizar inventario consolidado
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.actualizar_inventario_consolidado()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  v_stock NUMERIC(12,3);
  v_lotes_vencer INT;
BEGIN
  -- Calcular stock total de lotes disponibles
  SELECT COALESCE(SUM(cantidad_disponible), 0)
  INTO v_stock
  FROM drinkgo.lote_inventario
  WHERE sede_id = COALESCE(NEW.sede_id, OLD.sede_id)
    AND producto_id = COALESCE(NEW.producto_id, OLD.producto_id)
    AND estado = 'disponible';
  
  -- Contar lotes próximos a vencer (30 días)
  SELECT COUNT(*)
  INTO v_lotes_vencer
  FROM drinkgo.lote_inventario
  WHERE sede_id = COALESCE(NEW.sede_id, OLD.sede_id)
    AND producto_id = COALESCE(NEW.producto_id, OLD.producto_id)
    AND estado = 'disponible'
    AND cantidad_disponible > 0
    AND fecha_vencimiento IS NOT NULL
    AND fecha_vencimiento <= CURRENT_DATE + 30;
  
  -- Insertar o actualizar consolidado
  INSERT INTO drinkgo.inventario_consolidado (
    tenant_id, sede_id, producto_id, stock_total, lotes_por_vencer, actualizado_en
  )
  VALUES (
    COALESCE(NEW.tenant_id, OLD.tenant_id),
    COALESCE(NEW.sede_id, OLD.sede_id),
    COALESCE(NEW.producto_id, OLD.producto_id),
    v_stock,
    v_lotes_vencer,
    NOW()
  )
  ON CONFLICT (sede_id, producto_id)
  DO UPDATE SET
    stock_total = v_stock,
    lotes_por_vencer = v_lotes_vencer,
    actualizado_en = NOW();
  
  RETURN COALESCE(NEW, OLD);
END $$;

-- Trigger para actualizar consolidado automáticamente
CREATE TRIGGER lote_actualizar_consolidado
AFTER INSERT OR UPDATE OR DELETE ON lote_inventario
FOR EACH ROW
EXECUTE FUNCTION drinkgo.actualizar_inventario_consolidado();

-- =============================================================================
-- 5. TABLA: ajuste_inventario
-- =============================================================================
-- NOTA: Las tablas 'compra' y 'compra_item' están definidas en V13__purchases_suppliers_tables.sql
-- Ajustes manuales de inventario (mermas, conteos, etc.)
CREATE TABLE IF NOT EXISTS ajuste_inventario (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id         BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  lote_id         BIGINT REFERENCES lote_inventario(id) ON DELETE SET NULL,
  producto_id     BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  
  tipo_ajuste     VARCHAR(30) NOT NULL CHECK (tipo_ajuste IN (
    'conteo_fisico',
    'merma',
    'rotura',
    'vencimiento',
    'robo',
    'error_sistema',
    'otro'
  )),
  
  cantidad_anterior NUMERIC(12,3) NOT NULL,
  cantidad_nueva    NUMERIC(12,3) NOT NULL,
  diferencia        NUMERIC(12,3) GENERATED ALWAYS AS (cantidad_nueva - cantidad_anterior) STORED,
  
  motivo          VARCHAR(500),
  aprobado_por_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  
  creado_por_id   BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ajuste_tenant ON ajuste_inventario(tenant_id);
CREATE INDEX idx_ajuste_sede ON ajuste_inventario(sede_id);
CREATE INDEX idx_ajuste_producto ON ajuste_inventario(producto_id);
CREATE INDEX idx_ajuste_tipo ON ajuste_inventario(tipo_ajuste, creado_en);

COMMENT ON TABLE ajuste_inventario IS 'Registro de ajustes manuales de inventario';

-- =============================================================================
-- 6. TABLA: transferencia_inventario
-- =============================================================================
-- Transferencias entre sedes del mismo tenant
CREATE TABLE IF NOT EXISTS transferencia_inventario (
  id                BIGSERIAL PRIMARY KEY,
  tenant_id         BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  sede_origen_id    BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  sede_destino_id   BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  
  estado            VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                     CHECK (estado IN ('pendiente', 'en_transito', 'recibida', 'anulada')),
  
  solicitado_por_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  aprobado_por_id   BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  recibido_por_id   BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  
  fecha_solicitud   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  fecha_envio       TIMESTAMPTZ,
  fecha_recepcion   TIMESTAMPTZ,
  
  notas             TEXT,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  CONSTRAINT chk_transferencia_sedes_diferentes CHECK (sede_origen_id <> sede_destino_id)
);

CREATE INDEX idx_transferencia_tenant ON transferencia_inventario(tenant_id);
CREATE INDEX idx_transferencia_origen ON transferencia_inventario(sede_origen_id);
CREATE INDEX idx_transferencia_destino ON transferencia_inventario(sede_destino_id);
CREATE INDEX idx_transferencia_estado ON transferencia_inventario(estado);

CREATE TRIGGER transferencia_set_updated_at
BEFORE UPDATE ON transferencia_inventario FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE transferencia_inventario IS 'Transferencias de inventario entre sedes';

-- =============================================================================
-- 7. TABLA: transferencia_item
-- =============================================================================
CREATE TABLE IF NOT EXISTS transferencia_item (
  id                BIGSERIAL PRIMARY KEY,
  transferencia_id  BIGINT NOT NULL REFERENCES transferencia_inventario(id) ON DELETE CASCADE,
  lote_origen_id    BIGINT REFERENCES lote_inventario(id) ON DELETE SET NULL,
  producto_id       BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  
  cantidad_enviada  NUMERIC(12,3) NOT NULL CHECK (cantidad_enviada > 0),
  cantidad_recibida NUMERIC(12,3) DEFAULT 0 CHECK (cantidad_recibida >= 0),
  
  -- Nuevo lote creado en destino
  lote_destino_id   BIGINT REFERENCES lote_inventario(id) ON DELETE SET NULL,
  
  UNIQUE(transferencia_id, lote_origen_id)
);

CREATE INDEX idx_transferencia_item_trans ON transferencia_item(transferencia_id);
CREATE INDEX idx_transferencia_item_lote ON transferencia_item(lote_origen_id);

COMMENT ON TABLE transferencia_item IS 'Productos transferidos con detalle de lotes';
