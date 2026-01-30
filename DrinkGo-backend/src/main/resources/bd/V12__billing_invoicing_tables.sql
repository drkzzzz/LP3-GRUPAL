-- =============================================================================
-- BD_DRINKGO - V12: Módulos Completos - Facturación, Series y Comprobantes
-- Fecha: 2026-01-26
-- Descripción: Sistema completo de facturación electrónica SUNAT
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: tipo_comprobante
-- =============================================================================
-- Catálogo de tipos de comprobantes según SUNAT
CREATE TABLE IF NOT EXISTS tipo_comprobante (
  id              BIGSERIAL PRIMARY KEY,
  codigo_sunat    VARCHAR(2) UNIQUE NOT NULL,  -- 01=Factura, 03=Boleta, etc.
  nombre          VARCHAR(60) NOT NULL,
  abreviatura     VARCHAR(10) NOT NULL,
  requiere_ruc    BOOLEAN NOT NULL DEFAULT FALSE,
  activo          BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO tipo_comprobante (codigo_sunat, nombre, abreviatura, requiere_ruc) VALUES
('01', 'Factura', 'FAC', TRUE),
('03', 'Boleta de Venta', 'BOL', FALSE),
('07', 'Nota de Crédito', 'NC', FALSE),
('08', 'Nota de Débito', 'ND', FALSE),
('09', 'Guía de Remisión', 'GR', FALSE),
('00', 'Ticket/Voucher', 'TKT', FALSE);

COMMENT ON TABLE tipo_comprobante IS 'Catálogo de tipos de comprobantes SUNAT';

-- =============================================================================
-- 2. TABLA: serie_comprobante
-- =============================================================================
-- Series de comprobantes por sede (F001, B001, etc.)
CREATE TABLE IF NOT EXISTS serie_comprobante (
  id                    BIGSERIAL PRIMARY KEY,
  tenant_id             BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id               BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  tipo_comprobante_id   BIGINT NOT NULL REFERENCES tipo_comprobante(id) ON DELETE RESTRICT,
  
  serie                 VARCHAR(4) NOT NULL,  -- F001, B001, etc.
  numero_actual         BIGINT NOT NULL DEFAULT 0,
  numero_inicio         BIGINT NOT NULL DEFAULT 1,
  
  -- Para facturación electrónica
  es_electronica        BOOLEAN NOT NULL DEFAULT TRUE,
  certificado_digital   TEXT,  -- Encriptado
  clave_certificado     VARCHAR(250),  -- Encriptado
  
  activo                BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, sede_id, tipo_comprobante_id, serie)
);

CREATE INDEX idx_serie_comprobante_tenant ON serie_comprobante(tenant_id);
CREATE INDEX idx_serie_comprobante_sede ON serie_comprobante(sede_id);

CREATE TRIGGER serie_comprobante_set_updated_at
BEFORE UPDATE ON serie_comprobante FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE serie_comprobante IS 'Series de comprobantes por sede';
COMMENT ON COLUMN serie_comprobante.serie IS 'Serie del comprobante: F001 (Factura), B001 (Boleta), etc.';

-- =============================================================================
-- 3. FUNCIÓN: Obtener siguiente número de comprobante
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.obtener_siguiente_comprobante(
  p_serie_id BIGINT
)
RETURNS VARCHAR LANGUAGE plpgsql AS $$
DECLARE
  v_serie VARCHAR(4);
  v_numero BIGINT;
BEGIN
  -- Incrementar y obtener el número atómicamente
  UPDATE drinkgo.serie_comprobante
  SET numero_actual = numero_actual + 1,
      actualizado_en = NOW()
  WHERE id = p_serie_id
  RETURNING serie, numero_actual INTO v_serie, v_numero;
  
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Serie de comprobante no encontrada: %', p_serie_id;
  END IF;
  
  -- Formato: F001-00000001
  RETURN v_serie || '-' || LPAD(v_numero::TEXT, 8, '0');
END $$;

-- =============================================================================
-- 4. TABLA: metodo_pago
-- =============================================================================
-- Catálogo de métodos de pago
CREATE TABLE IF NOT EXISTS metodo_pago (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT REFERENCES negocio(id) ON DELETE CASCADE,  -- NULL = global
  codigo          VARCHAR(30) NOT NULL,
  nombre          VARCHAR(60) NOT NULL,
  tipo            VARCHAR(20) NOT NULL CHECK (tipo IN ('efectivo', 'digital', 'tarjeta', 'credito', 'otro')),
  requiere_referencia BOOLEAN NOT NULL DEFAULT FALSE,
  comision_porcentaje NUMERIC(5,2) DEFAULT 0,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  orden           INT NOT NULL DEFAULT 0,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Métodos de pago globales
INSERT INTO metodo_pago (tenant_id, codigo, nombre, tipo, requiere_referencia, orden) VALUES
(NULL, 'efectivo', 'Efectivo', 'efectivo', FALSE, 1),
(NULL, 'yape', 'Yape', 'digital', TRUE, 2),
(NULL, 'plin', 'Plin', 'digital', TRUE, 3),
(NULL, 'tarjeta_debito', 'Tarjeta Débito', 'tarjeta', TRUE, 4),
(NULL, 'tarjeta_credito', 'Tarjeta Crédito', 'tarjeta', TRUE, 5),
(NULL, 'transferencia', 'Transferencia Bancaria', 'digital', TRUE, 6);

COMMENT ON TABLE metodo_pago IS 'Catálogo de métodos de pago';

-- =============================================================================
-- 5. TABLA: comprobante_emitido
-- =============================================================================
-- Registro maestro de todos los comprobantes emitidos
CREATE TABLE IF NOT EXISTS comprobante_emitido (
  id                    BIGSERIAL PRIMARY KEY,
  tenant_id             BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id               BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  
  -- Identificación del comprobante
  tipo_comprobante_id   BIGINT NOT NULL REFERENCES tipo_comprobante(id),
  serie_id              BIGINT NOT NULL REFERENCES serie_comprobante(id),
  serie                 VARCHAR(4) NOT NULL,
  numero                VARCHAR(20) NOT NULL,
  numero_completo       VARCHAR(15) GENERATED ALWAYS AS (serie || '-' || numero) STORED,
  
  -- Fecha de emisión
  fecha_emision         DATE NOT NULL DEFAULT CURRENT_DATE,
  fecha_vencimiento     DATE,
  
  -- Cliente/Receptor
  cliente_id            BIGINT REFERENCES cliente(id) ON DELETE SET NULL,
  cliente_tipo_doc      VARCHAR(2),  -- 1=DNI, 6=RUC
  cliente_num_doc       VARCHAR(20),
  cliente_nombre        VARCHAR(250) NOT NULL,
  cliente_direccion     VARCHAR(300),
  
  -- Montos
  moneda                CHAR(3) NOT NULL DEFAULT 'PEN',
  subtotal              NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
  descuento_global      NUMERIC(12,2) NOT NULL DEFAULT 0,
  base_imponible        NUMERIC(12,2) NOT NULL CHECK (base_imponible >= 0),
  igv_porcentaje        NUMERIC(5,2) NOT NULL DEFAULT 18.00,
  igv                   NUMERIC(12,2) NOT NULL CHECK (igv >= 0),
  total                 NUMERIC(12,2) NOT NULL CHECK (total >= 0),
  
  -- Referencia (para NC/ND)
  comprobante_ref_id    BIGINT REFERENCES comprobante_emitido(id),
  motivo_nota           VARCHAR(250),
  
  -- SUNAT - Facturación Electrónica
  sunat_enviado         BOOLEAN NOT NULL DEFAULT FALSE,
  sunat_fecha_envio     TIMESTAMPTZ,
  sunat_estado          VARCHAR(20) DEFAULT 'pendiente'
                         CHECK (sunat_estado IN ('pendiente', 'enviado', 'aceptado', 'rechazado', 'anulado', 'baja')),
  sunat_codigo_respuesta VARCHAR(10),
  sunat_mensaje         TEXT,
  sunat_hash            VARCHAR(100),
  sunat_xml             TEXT,
  sunat_cdr             TEXT,  -- Constancia de Recepción
  sunat_pdf             VARCHAR(500),  -- URL del PDF
  
  -- Origen
  origen_tipo           VARCHAR(20) NOT NULL CHECK (origen_tipo IN ('venta', 'pedido', 'manual')),
  origen_id             BIGINT,
  
  -- Estado
  estado                VARCHAR(20) NOT NULL DEFAULT 'emitido'
                         CHECK (estado IN ('emitido', 'anulado', 'baja')),
  anulado_en            TIMESTAMPTZ,
  anulado_por_id        BIGINT REFERENCES usuario(id),
  motivo_anulacion      VARCHAR(250),
  
  -- Auditoría
  emitido_por_id        BIGINT NOT NULL REFERENCES usuario(id),
  creado_en             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, tipo_comprobante_id, serie, numero)
);

CREATE INDEX idx_comprobante_tenant ON comprobante_emitido(tenant_id);
CREATE INDEX idx_comprobante_sede ON comprobante_emitido(sede_id);
CREATE INDEX idx_comprobante_fecha ON comprobante_emitido(fecha_emision);
CREATE INDEX idx_comprobante_cliente ON comprobante_emitido(cliente_id);
CREATE INDEX idx_comprobante_sunat ON comprobante_emitido(sunat_estado) WHERE sunat_estado <> 'aceptado';
CREATE INDEX idx_comprobante_numero ON comprobante_emitido(numero_completo);

CREATE TRIGGER comprobante_emitido_set_updated_at
BEFORE UPDATE ON comprobante_emitido FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE comprobante_emitido IS 'Registro maestro de comprobantes emitidos (boletas, facturas, NC, ND)';

-- =============================================================================
-- 6. TABLA: comprobante_item
-- =============================================================================
-- Detalle de items del comprobante
CREATE TABLE IF NOT EXISTS comprobante_item (
  id                  BIGSERIAL PRIMARY KEY,
  comprobante_id      BIGINT NOT NULL REFERENCES comprobante_emitido(id) ON DELETE CASCADE,
  
  -- Producto
  producto_id         BIGINT REFERENCES producto(id) ON DELETE SET NULL,
  codigo_producto     VARCHAR(50),
  descripcion         VARCHAR(500) NOT NULL,
  unidad_medida       VARCHAR(10) NOT NULL DEFAULT 'NIU',  -- Código SUNAT
  
  -- Cantidades y precios
  cantidad            NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  precio_unitario     NUMERIC(12,4) NOT NULL CHECK (precio_unitario >= 0),
  valor_unitario      NUMERIC(12,4) NOT NULL,  -- Sin IGV
  descuento           NUMERIC(12,2) NOT NULL DEFAULT 0,
  
  -- Totales
  subtotal            NUMERIC(12,2) NOT NULL,
  igv                 NUMERIC(12,2) NOT NULL DEFAULT 0,
  total               NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_comprobante_item_comprobante ON comprobante_item(comprobante_id);

COMMENT ON TABLE comprobante_item IS 'Detalle de items de cada comprobante';

-- =============================================================================
-- 7. TABLA: pago_comprobante
-- =============================================================================
-- Pagos asociados a comprobantes
CREATE TABLE IF NOT EXISTS pago_comprobante (
  id                BIGSERIAL PRIMARY KEY,
  comprobante_id    BIGINT NOT NULL REFERENCES comprobante_emitido(id) ON DELETE CASCADE,
  metodo_pago_id    BIGINT NOT NULL REFERENCES metodo_pago(id),
  
  monto             NUMERIC(12,2) NOT NULL CHECK (monto > 0),
  referencia        VARCHAR(120),
  
  -- Para tarjetas
  tarjeta_marca     VARCHAR(20),
  tarjeta_ultimos4  VARCHAR(4),
  
  recibido_por_id   BIGINT REFERENCES usuario(id),
  recibido_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pago_comprobante ON pago_comprobante(comprobante_id);

COMMENT ON TABLE pago_comprobante IS 'Pagos de comprobantes (soporta pagos mixtos)';

-- (Tabla cuenta_por_cobrar removida - no se manejará crédito a clientes)

-- (Tablas de abonos y cuentas por cobrar removidas)
