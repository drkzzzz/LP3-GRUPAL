-- =============================================================================
-- BD_DRINKGO - V8: Sistema de Ventas POS (Punto de Venta)
-- Fecha: 2026-01-26
-- Descripción: Caja, ventas, items con descuento de lotes FIFO
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: caja_sesion
-- =============================================================================
CREATE TABLE IF NOT EXISTS caja_sesion (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id             BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  
  -- Apertura
  usuario_apertura_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
  apertura_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  monto_apertura      NUMERIC(12,2) NOT NULL CHECK (monto_apertura >= 0),
  
  -- Cierre
  usuario_cierre_id   BIGINT REFERENCES usuario(id) ON DELETE RESTRICT,
  cierre_en           TIMESTAMPTZ,
  monto_cierre_esperado NUMERIC(12,2),
  monto_cierre_real     NUMERIC(12,2),
  diferencia            NUMERIC(12,2),
  
  observaciones_apertura TEXT,
  observaciones_cierre   TEXT,
  
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  CHECK (cierre_en IS NULL OR cierre_en >= apertura_en)
);

-- Solo una caja abierta por sede
CREATE UNIQUE INDEX unq_caja_abierta_sede
ON caja_sesion(sede_id) WHERE cierre_en IS NULL;

CREATE INDEX idx_caja_tenant ON caja_sesion(tenant_id);
CREATE INDEX idx_caja_sede ON caja_sesion(sede_id);
CREATE INDEX idx_caja_apertura ON caja_sesion(apertura_en);

CREATE TRIGGER caja_sesion_set_updated_at
BEFORE UPDATE ON caja_sesion FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE caja_sesion IS 'Sesiones de caja por sede';
COMMENT ON INDEX unq_caja_abierta_sede IS 'Solo una caja abierta por sede';

-- =============================================================================
-- 2. TABLA: cliente
-- =============================================================================
CREATE TABLE IF NOT EXISTS cliente (
  id                BIGSERIAL PRIMARY KEY,
  tenant_id         BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  -- Identificación
  tipo_documento    VARCHAR(10) DEFAULT 'dni' CHECK (tipo_documento IN ('dni', 'ruc', 'ce', 'pasaporte')),
  numero_documento  VARCHAR(20),
  
  -- Datos personales
  nombres           VARCHAR(120) NOT NULL,
  apellidos         VARCHAR(120),
  razon_social      VARCHAR(250),  -- Para facturas a empresas
  
  -- Contacto
  email             CITEXT,
  telefono          VARCHAR(20) CHECK (telefono ~ '^[0-9 +()-]{6,20}$' OR telefono IS NULL),
  direccion         VARCHAR(300),
  distrito          VARCHAR(80),
  
  -- Verificación de edad (CRÍTICO para licorería)
  fecha_nacimiento  DATE,
  mayor_edad_verificado BOOLEAN NOT NULL DEFAULT FALSE,
  verificado_en     TIMESTAMPTZ,
  verificado_por_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  
  -- Marketing
  acepta_marketing  BOOLEAN DEFAULT FALSE,
  notas             TEXT,
  
  activo            BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, tipo_documento, numero_documento)
);

CREATE INDEX idx_cliente_tenant ON cliente(tenant_id);
CREATE INDEX idx_cliente_documento ON cliente(numero_documento);
CREATE INDEX idx_cliente_nombres_trgm ON cliente USING gin (nombres gin_trgm_ops);

CREATE TRIGGER cliente_set_updated_at
BEFORE UPDATE ON cliente FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE cliente IS 'Clientes del negocio';
COMMENT ON COLUMN cliente.mayor_edad_verificado IS 'CRÍTICO: Verificación de mayoría de edad para venta de alcohol';

-- =============================================================================
-- 3. TABLA: venta
-- =============================================================================
CREATE TABLE IF NOT EXISTS venta (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id             BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  caja_sesion_id      BIGINT REFERENCES caja_sesion(id) ON DELETE SET NULL,
  
  -- Quien vende
  usuario_id          BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
  cliente_id          BIGINT REFERENCES cliente(id) ON DELETE SET NULL,
  
  -- Identificación
  numero_venta        VARCHAR(30) NOT NULL,
  fecha               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  -- Estado
  estado              VARCHAR(20) NOT NULL DEFAULT 'emitida'
                       CHECK (estado IN ('emitida', 'anulada')),
  
  -- Comprobante
  comprobante_tipo    VARCHAR(20) CHECK (comprobante_tipo IN ('boleta', 'factura', 'ticket', 'nota_credito')),
  comprobante_serie   VARCHAR(10),
  comprobante_numero  VARCHAR(20),
  
  -- SUNAT (Perú)
  sunat_estado        VARCHAR(20) DEFAULT 'pendiente'
                       CHECK (sunat_estado IN ('pendiente', 'enviado', 'aceptado', 'rechazado', 'anulado')),
  sunat_mensaje       TEXT,
  
  -- Montos
  moneda              CHAR(3) NOT NULL DEFAULT 'PEN',
  subtotal            NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
  descuento_total     NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (descuento_total >= 0),
  impuesto_porcentaje NUMERIC(5,2) NOT NULL DEFAULT 18.00,
  impuesto            NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (impuesto >= 0),
  total               NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
  
  -- Anulación
  anulado_en          TIMESTAMPTZ,
  anulado_por_id      BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  motivo_anulacion    VARCHAR(250),
  
  -- Verificación de edad realizada
  verificacion_edad   BOOLEAN NOT NULL DEFAULT FALSE,
  
  notas               TEXT,
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_venta)
);

-- Comprobante único
CREATE UNIQUE INDEX unq_comprobante_venta
ON venta(tenant_id, comprobante_tipo, comprobante_serie, comprobante_numero)
WHERE comprobante_tipo IS NOT NULL AND comprobante_serie IS NOT NULL;

CREATE INDEX idx_venta_tenant ON venta(tenant_id);
CREATE INDEX idx_venta_sede ON venta(sede_id);
CREATE INDEX idx_venta_caja ON venta(caja_sesion_id);
CREATE INDEX idx_venta_fecha ON venta(fecha);
CREATE INDEX idx_venta_estado ON venta(estado, fecha);
CREATE INDEX idx_venta_cliente ON venta(cliente_id);

CREATE TRIGGER venta_set_updated_at
BEFORE UPDATE ON venta FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE venta IS 'Ventas del POS';
COMMENT ON COLUMN venta.verificacion_edad IS 'Indica si se verificó mayoría de edad (venta de alcohol)';

-- =============================================================================
-- 4. TABLA: venta_item
-- =============================================================================
CREATE TABLE IF NOT EXISTS venta_item (
  id                BIGSERIAL PRIMARY KEY,
  venta_id          BIGINT NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
  producto_id       BIGINT REFERENCES producto(id) ON DELETE SET NULL,
  combo_id          BIGINT REFERENCES combo(id) ON DELETE SET NULL,
  
  -- Snapshot del producto al momento de venta
  codigo_producto   VARCHAR(50),
  nombre_producto   VARCHAR(200) NOT NULL,
  
  cantidad          NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  precio_unitario   NUMERIC(12,2) NOT NULL CHECK (precio_unitario >= 0),
  descuento         NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (descuento >= 0),
  total_item        NUMERIC(12,2) NOT NULL CHECK (total_item >= 0),
  
  -- Promoción aplicada
  promocion_id      BIGINT REFERENCES promocion(id) ON DELETE SET NULL,
  
  notas             VARCHAR(250),
  
  CONSTRAINT chk_venta_item_tipo CHECK (producto_id IS NOT NULL OR combo_id IS NOT NULL)
);

CREATE INDEX idx_venta_item_venta ON venta_item(venta_id);
CREATE INDEX idx_venta_item_producto ON venta_item(producto_id);
CREATE INDEX idx_venta_item_combo ON venta_item(combo_id);

-- Trigger: Calcular total_item
CREATE TRIGGER venta_item_calc_total
BEFORE INSERT OR UPDATE ON venta_item FOR EACH ROW
EXECUTE FUNCTION drinkgo.calcular_total_item_venta();

-- Trigger: Recalcular totales de venta
CREATE TRIGGER venta_item_recalc_venta
AFTER INSERT OR UPDATE OR DELETE ON venta_item FOR EACH ROW
EXECUTE FUNCTION drinkgo.recalcular_totales_venta();

COMMENT ON TABLE venta_item IS 'Items de cada venta';

-- =============================================================================
-- 5. TABLA: venta_item_lote
-- =============================================================================
-- Relaciona cada item vendido con los lotes de donde se descontó (trazabilidad FIFO)
CREATE TABLE IF NOT EXISTS venta_item_lote (
  id              BIGSERIAL PRIMARY KEY,
  venta_item_id   BIGINT NOT NULL REFERENCES venta_item(id) ON DELETE CASCADE,
  lote_id         BIGINT NOT NULL REFERENCES lote_inventario(id) ON DELETE RESTRICT,
  cantidad        NUMERIC(12,3) NOT NULL CHECK (cantidad > 0)
);

CREATE INDEX idx_venta_item_lote_item ON venta_item_lote(venta_item_id);
CREATE INDEX idx_venta_item_lote_lote ON venta_item_lote(lote_id);

COMMENT ON TABLE venta_item_lote IS 'Trazabilidad: de qué lotes se descontó cada item vendido (FIFO)';

-- =============================================================================
-- 6. TABLA: pago_venta
-- =============================================================================
CREATE TABLE IF NOT EXISTS pago_venta (
  id              BIGSERIAL PRIMARY KEY,
  venta_id        BIGINT NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
  
  metodo_pago     drinkgo.payment_method NOT NULL,
  monto           NUMERIC(12,2) NOT NULL CHECK (monto > 0),
  referencia      VARCHAR(120),
  
  recibido_en     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pago_venta_venta ON pago_venta(venta_id);
CREATE INDEX idx_pago_venta_metodo ON pago_venta(metodo_pago);

COMMENT ON TABLE pago_venta IS 'Pagos de ventas (soporta pagos mixtos)';

-- =============================================================================
-- 7. TABLA: arqueo_caja
-- =============================================================================
-- Arqueos intermedios durante la sesión de caja
CREATE TABLE IF NOT EXISTS arqueo_caja (
  id                BIGSERIAL PRIMARY KEY,
  caja_sesion_id    BIGINT NOT NULL REFERENCES caja_sesion(id) ON DELETE CASCADE,
  
  -- Conteo por denominación
  efectivo_contado  NUMERIC(12,2) NOT NULL,
  efectivo_esperado NUMERIC(12,2) NOT NULL,
  diferencia        NUMERIC(12,2) GENERATED ALWAYS AS (efectivo_contado - efectivo_esperado) STORED,
  
  -- Detalle de billetes/monedas (JSON)
  detalle_conteo    JSONB,
  
  observaciones     TEXT,
  realizado_por_id  BIGINT NOT NULL REFERENCES usuario(id) ON DELETE RESTRICT,
  realizado_en      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_arqueo_caja_sesion ON arqueo_caja(caja_sesion_id);

COMMENT ON TABLE arqueo_caja IS 'Arqueos intermedios de caja';

-- =============================================================================
-- 8. FUNCIÓN: Procesar venta con descuento FIFO de inventario
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.procesar_venta_inventario(p_venta_id BIGINT)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE
  v_item RECORD;
  v_venta RECORD;
  v_lote RECORD;
BEGIN
  -- Obtener datos de la venta
  SELECT * INTO v_venta FROM drinkgo.venta WHERE id = p_venta_id;
  
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Venta no encontrada: %', p_venta_id;
  END IF;
  
  -- Procesar cada item de la venta
  FOR v_item IN 
    SELECT vi.*, p.tipo_producto
    FROM drinkgo.venta_item vi
    LEFT JOIN drinkgo.producto p ON p.id = vi.producto_id
    WHERE vi.venta_id = p_venta_id
  LOOP
    IF v_item.combo_id IS NOT NULL THEN
      -- Es un combo: descontar cada componente
      PERFORM drinkgo.descontar_combo_inventario(
        v_item.combo_id,
        v_venta.sede_id,
        v_item.cantidad::INT,
        'venta',
        p_venta_id
      );
    ELSIF v_item.producto_id IS NOT NULL THEN
      -- Es un producto individual: descontar con FIFO
      FOR v_lote IN 
        SELECT * FROM drinkgo.seleccionar_lotes_fifo(
          v_item.producto_id, 
          v_venta.sede_id, 
          v_item.cantidad
        )
      LOOP
        -- Registrar de qué lote se descontó (trazabilidad)
        INSERT INTO drinkgo.venta_item_lote (venta_item_id, lote_id, cantidad)
        VALUES (v_item.id, v_lote.lote_id, v_lote.cantidad_a_descontar);
      END LOOP;
      
      -- Realizar el descuento efectivo
      PERFORM drinkgo.descontar_inventario_fifo(
        v_item.producto_id,
        v_venta.sede_id,
        v_item.cantidad,
        'venta',
        p_venta_id
      );
    END IF;
  END LOOP;
END $$;

COMMENT ON FUNCTION drinkgo.procesar_venta_inventario IS 
'Procesa el descuento de inventario de una venta usando FIFO y registra trazabilidad de lotes';
