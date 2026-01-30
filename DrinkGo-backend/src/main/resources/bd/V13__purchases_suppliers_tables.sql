-- =============================================================================
-- BD_DRINKGO - V13: Módulo Completo de Compras a Proveedores
-- Fecha: 2026-01-26
-- Descripción: Gestión de compras, proveedores, ingreso a almacén
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 0. AGREGAR FKs DIFERIDAS A lote_inventario (definido en V6)
-- =============================================================================
-- Estas FKs no se pudieron crear en V6 porque proveedor y compra no existían
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_lote_proveedor') THEN
    ALTER TABLE drinkgo.lote_inventario 
    ADD CONSTRAINT fk_lote_proveedor 
    FOREIGN KEY (proveedor_id) REFERENCES drinkgo.proveedor(id) ON DELETE SET NULL;
  END IF;
END $$;

-- La FK de compra_id se agrega después de crear la tabla compra (más abajo)

-- =============================================================================
-- 1. TABLA: proveedor (Proveedores de licores y productos)
-- =============================================================================
CREATE TABLE IF NOT EXISTS proveedor (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  -- Identificación
  tipo_documento          VARCHAR(2) NOT NULL DEFAULT '6',  -- 6=RUC, 1=DNI
  numero_documento        VARCHAR(20) NOT NULL,
  razon_social            VARCHAR(250) NOT NULL,
  nombre_comercial        VARCHAR(250),
  
  -- Contacto principal
  contacto_nombre         VARCHAR(100),
  contacto_telefono       VARCHAR(20),
  contacto_email          citext,
  
  -- Ubicación
  direccion               VARCHAR(300),
  distrito                VARCHAR(100),
  provincia               VARCHAR(100),
  departamento            VARCHAR(100),
  
  -- Comercial
  rubro                   VARCHAR(100),  -- Licores, Cervezas, Insumos, etc.
  linea_credito           NUMERIC(12,2) DEFAULT 0,
  dias_credito            INT DEFAULT 0,
  
  -- Bancarios (para pagos)
  banco_nombre            VARCHAR(100),
  banco_cuenta            VARCHAR(50),
  banco_cci               VARCHAR(25),
  
  -- Estado
  activo                  BOOLEAN NOT NULL DEFAULT TRUE,
  calificacion            INT CHECK (calificacion BETWEEN 1 AND 5),  -- Rating del proveedor
  notas                   TEXT,
  
  -- Auditoría
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_documento)
);

CREATE INDEX idx_proveedor_tenant ON proveedor(tenant_id);
CREATE INDEX idx_proveedor_documento ON proveedor(numero_documento);
CREATE INDEX idx_proveedor_razon ON proveedor(razon_social);

CREATE TRIGGER proveedor_set_updated_at
BEFORE UPDATE ON proveedor FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE proveedor IS 'Proveedores de productos y servicios para el negocio';

-- =============================================================================
-- 2. TABLA: proveedor_producto
-- =============================================================================
-- Productos que maneja cada proveedor (para buscar rápido)
CREATE TABLE IF NOT EXISTS proveedor_producto (
  id                BIGSERIAL PRIMARY KEY,
  proveedor_id      BIGINT NOT NULL REFERENCES proveedor(id) ON DELETE CASCADE,
  producto_id       BIGINT NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
  
  codigo_proveedor  VARCHAR(50),  -- Código del producto según el proveedor
  precio_compra     NUMERIC(12,2),  -- Último precio conocido
  tiempo_entrega_dias INT,
  es_principal      BOOLEAN NOT NULL DEFAULT FALSE,  -- Proveedor principal de este producto
  
  UNIQUE(proveedor_id, producto_id)
);

CREATE INDEX idx_proveedor_producto_proveedor ON proveedor_producto(proveedor_id);
CREATE INDEX idx_proveedor_producto_producto ON proveedor_producto(producto_id);

COMMENT ON TABLE proveedor_producto IS 'Relación de productos por proveedor';

-- =============================================================================
-- 3. TABLA: orden_compra
-- =============================================================================
-- Órdenes de compra a proveedores
CREATE TABLE IF NOT EXISTS orden_compra (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id                 BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  proveedor_id            BIGINT NOT NULL REFERENCES proveedor(id) ON DELETE RESTRICT,
  
  -- Numeración
  numero_orden            VARCHAR(20) NOT NULL,
  fecha_orden             DATE NOT NULL DEFAULT CURRENT_DATE,
  fecha_entrega_esperada  DATE,
  
  -- Montos
  moneda                  CHAR(3) NOT NULL DEFAULT 'PEN',
  subtotal                NUMERIC(12,2) NOT NULL DEFAULT 0,
  descuento               NUMERIC(12,2) NOT NULL DEFAULT 0,
  igv                     NUMERIC(12,2) NOT NULL DEFAULT 0,
  total                   NUMERIC(12,2) NOT NULL DEFAULT 0,
  
  -- Estado
  estado                  VARCHAR(20) NOT NULL DEFAULT 'borrador'
                           CHECK (estado IN ('borrador', 'enviada', 'confirmada', 'parcial', 
                                             'recibida', 'cancelada')),
  
  -- Condiciones de pago
  condicion_pago          VARCHAR(30) NOT NULL DEFAULT 'contado'
                           CHECK (condicion_pago IN ('contado', 'credito_15', 'credito_30', 
                                                     'credito_45', 'credito_60')),
  
  -- Documentación
  notas                   TEXT,
  
  -- Auditoría
  creado_por_id           BIGINT NOT NULL REFERENCES usuario(id),
  aprobado_por_id         BIGINT REFERENCES usuario(id),
  aprobado_en             TIMESTAMPTZ,
  
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_orden)
);

CREATE INDEX idx_orden_compra_tenant ON orden_compra(tenant_id);
CREATE INDEX idx_orden_compra_proveedor ON orden_compra(proveedor_id);
CREATE INDEX idx_orden_compra_estado ON orden_compra(estado);
CREATE INDEX idx_orden_compra_fecha ON orden_compra(fecha_orden);

CREATE TRIGGER orden_compra_set_updated_at
BEFORE UPDATE ON orden_compra FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE orden_compra IS 'Órdenes de compra a proveedores';

-- =============================================================================
-- 4. TABLA: orden_compra_item
-- =============================================================================
-- Detalle de items de la orden de compra
CREATE TABLE IF NOT EXISTS orden_compra_item (
  id                  BIGSERIAL PRIMARY KEY,
  orden_compra_id     BIGINT NOT NULL REFERENCES orden_compra(id) ON DELETE CASCADE,
  producto_id         BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  
  cantidad_solicitada NUMERIC(12,3) NOT NULL CHECK (cantidad_solicitada > 0),
  cantidad_recibida   NUMERIC(12,3) NOT NULL DEFAULT 0,
  precio_unitario     NUMERIC(12,4) NOT NULL CHECK (precio_unitario >= 0),
  descuento           NUMERIC(12,2) NOT NULL DEFAULT 0,
  subtotal            NUMERIC(12,2) NOT NULL,
  
  notas               VARCHAR(250)
);

CREATE INDEX idx_orden_compra_item_orden ON orden_compra_item(orden_compra_id);

COMMENT ON TABLE orden_compra_item IS 'Detalle de items de orden de compra';

-- =============================================================================
-- 5. TABLA: compra (Ingreso de mercadería - Recepción)
-- =============================================================================
-- Registro de compras recibidas (ingreso a almacén)
CREATE TABLE IF NOT EXISTS compra (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id                 BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  almacen_id              BIGINT NOT NULL REFERENCES almacen(id) ON DELETE RESTRICT,
  proveedor_id            BIGINT NOT NULL REFERENCES proveedor(id) ON DELETE RESTRICT,
  
  -- Referencia a orden de compra (opcional)
  orden_compra_id         BIGINT REFERENCES orden_compra(id) ON DELETE SET NULL,
  
  -- Comprobante del proveedor
  comprobante_tipo        VARCHAR(2),  -- 01=Factura, 03=Boleta
  comprobante_serie       VARCHAR(10),
  comprobante_numero      VARCHAR(20),
  comprobante_fecha       DATE,
  
  -- Fechas
  fecha_recepcion         DATE NOT NULL DEFAULT CURRENT_DATE,
  hora_recepcion          TIME NOT NULL DEFAULT CURRENT_TIME,
  
  -- Montos
  moneda                  CHAR(3) NOT NULL DEFAULT 'PEN',
  subtotal                NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
  descuento               NUMERIC(12,2) NOT NULL DEFAULT 0,
  igv                     NUMERIC(12,2) NOT NULL DEFAULT 0,
  otros_cargos            NUMERIC(12,2) NOT NULL DEFAULT 0,  -- Flete, etc.
  total                   NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
  
  -- Estado
  estado                  VARCHAR(20) NOT NULL DEFAULT 'recibida'
                           CHECK (estado IN ('recibida', 'verificada', 'ingresada', 'anulada')),
  
  -- Pago
  condicion_pago          VARCHAR(30) NOT NULL DEFAULT 'contado',
  pagada                  BOOLEAN NOT NULL DEFAULT FALSE,
  fecha_pago              DATE,
  
  -- Notas
  notas                   TEXT,
  guia_remision           VARCHAR(50),  -- Guía de remisión del proveedor
  
  -- Auditoría
  recibido_por_id         BIGINT NOT NULL REFERENCES usuario(id),
  verificado_por_id       BIGINT REFERENCES usuario(id),
  verificado_en           TIMESTAMPTZ,
  
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_compra_tenant ON compra(tenant_id);
CREATE INDEX idx_compra_proveedor ON compra(proveedor_id);
CREATE INDEX idx_compra_fecha ON compra(fecha_recepcion);
CREATE INDEX idx_compra_estado ON compra(estado);
CREATE INDEX idx_compra_comprobante ON compra(comprobante_serie, comprobante_numero);

CREATE TRIGGER compra_set_updated_at
BEFORE UPDATE ON compra FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE compra IS 'Registro de compras/recepciones de mercadería';

-- Agregar FK de compra_id a lote_inventario (ahora que compra existe)
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_lote_compra') THEN
    ALTER TABLE drinkgo.lote_inventario 
    ADD CONSTRAINT fk_lote_compra 
    FOREIGN KEY (compra_id) REFERENCES drinkgo.compra(id) ON DELETE SET NULL;
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_lote_compra ON drinkgo.lote_inventario(compra_id) WHERE compra_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_lote_proveedor ON drinkgo.lote_inventario(proveedor_id) WHERE proveedor_id IS NOT NULL;

-- =============================================================================
-- 6. TABLA: compra_item
-- =============================================================================
-- Detalle de items de la compra con información de lotes
CREATE TABLE IF NOT EXISTS compra_item (
  id                      BIGSERIAL PRIMARY KEY,
  compra_id               BIGINT NOT NULL REFERENCES compra(id) ON DELETE CASCADE,
  producto_id             BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  
  -- Cantidades
  cantidad_recibida       NUMERIC(12,3) NOT NULL CHECK (cantidad_recibida > 0),
  cantidad_aceptada       NUMERIC(12,3),  -- Después de verificación
  cantidad_rechazada      NUMERIC(12,3) DEFAULT 0,
  motivo_rechazo          VARCHAR(250),
  
  -- Precios
  precio_unitario         NUMERIC(12,4) NOT NULL CHECK (precio_unitario >= 0),
  descuento               NUMERIC(12,2) NOT NULL DEFAULT 0,
  subtotal                NUMERIC(12,2) NOT NULL,
  
  -- Información del lote (para trazabilidad)
  lote_numero             VARCHAR(50),  -- Número de lote del proveedor
  fecha_produccion        DATE,
  fecha_vencimiento       DATE,  -- IMPORTANTE para licores
  
  -- Lote generado en sistema
  lote_inventario_id      BIGINT REFERENCES lote_inventario(id) ON DELETE SET NULL,
  
  notas                   VARCHAR(250)
);

CREATE INDEX idx_compra_item_compra ON compra_item(compra_id);
CREATE INDEX idx_compra_item_producto ON compra_item(producto_id);
CREATE INDEX idx_compra_item_lote ON compra_item(lote_inventario_id);

COMMENT ON TABLE compra_item IS 'Detalle de items de cada compra con trazabilidad de lotes';

-- =============================================================================
-- 7. FUNCIÓN: Registrar ingreso de compra al inventario
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.registrar_ingreso_compra(
  p_compra_id BIGINT,
  p_usuario_id BIGINT
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_item RECORD;
  v_lote_id BIGINT;
  v_compra RECORD;
BEGIN
  -- Obtener datos de la compra
  SELECT * INTO v_compra FROM drinkgo.compra WHERE id = p_compra_id;
  
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Compra no encontrada: %', p_compra_id;
  END IF;
  
  IF v_compra.estado = 'ingresada' THEN
    RAISE EXCEPTION 'Esta compra ya fue ingresada al inventario';
  END IF;
  
  -- Procesar cada item
  FOR v_item IN 
    SELECT * FROM drinkgo.compra_item WHERE compra_id = p_compra_id
  LOOP
    -- Crear lote de inventario (usando nombres correctos de columnas de V6)
    INSERT INTO drinkgo.lote_inventario (
      tenant_id, sede_id, almacen_id, producto_id, codigo_lote,
      cantidad_inicial, cantidad_disponible,
      fecha_fabricacion, fecha_vencimiento,
      costo_unitario, compra_id, proveedor_id, estado
    ) VALUES (
      v_compra.tenant_id, v_compra.sede_id, v_compra.almacen_id, v_item.producto_id,
      COALESCE(v_item.lote_numero, 'LOT-' || TO_CHAR(NOW(), 'YYYYMMDD-HH24MISS') || '-' || v_item.id),
      COALESCE(v_item.cantidad_aceptada, v_item.cantidad_recibida),
      COALESCE(v_item.cantidad_aceptada, v_item.cantidad_recibida),
      v_item.fecha_produccion, v_item.fecha_vencimiento,
      v_item.precio_unitario, p_compra_id, v_compra.proveedor_id, 'disponible'
    )
    RETURNING id INTO v_lote_id;
    
    -- Actualizar referencia del lote en el item
    UPDATE drinkgo.compra_item 
    SET lote_inventario_id = v_lote_id
    WHERE id = v_item.id;
    
    -- Registrar movimiento de inventario
    INSERT INTO drinkgo.movimiento_inventario (
      tenant_id, sede_id, producto_id, lote_id,
      tipo_movimiento, cantidad,
      referencia_tipo, referencia_id, motivo, usuario_id
    ) VALUES (
      v_compra.tenant_id, v_compra.sede_id, v_item.producto_id, v_lote_id,
      'entrada', COALESCE(v_item.cantidad_aceptada, v_item.cantidad_recibida),
      'compra', p_compra_id, 'Ingreso por compra', p_usuario_id
    );
    
    -- NOTA: El stock se actualiza automáticamente en inventario_consolidado
    -- via el trigger lote_actualizar_consolidado definido en V6
  END LOOP;
  
  -- Actualizar estado de la compra
  UPDATE drinkgo.compra
  SET estado = 'ingresada',
      verificado_por_id = p_usuario_id,
      verificado_en = NOW(),
      actualizado_en = NOW()
  WHERE id = p_compra_id;
  
  RETURN TRUE;
END $$;

COMMENT ON FUNCTION drinkgo.registrar_ingreso_compra IS 'Registra el ingreso de una compra al inventario creando lotes';

-- =============================================================================
-- 8. TABLA: cuenta_por_pagar
-- =============================================================================
-- Cuentas por pagar a proveedores (compras a crédito)
CREATE TABLE IF NOT EXISTS cuenta_por_pagar (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id             BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  proveedor_id        BIGINT NOT NULL REFERENCES proveedor(id) ON DELETE RESTRICT,
  compra_id           BIGINT REFERENCES compra(id) ON DELETE SET NULL,
  
  descripcion         VARCHAR(250),
  monto_original      NUMERIC(12,2) NOT NULL CHECK (monto_original > 0),
  monto_pagado        NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (monto_pagado >= 0),
  monto_pendiente     NUMERIC(12,2) GENERATED ALWAYS AS (monto_original - monto_pagado) STORED,
  
  fecha_emision       DATE NOT NULL DEFAULT CURRENT_DATE,
  fecha_vencimiento   DATE NOT NULL,
  
  estado              VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                       CHECK (estado IN ('pendiente', 'parcial', 'pagado', 'vencido', 'anulado')),
  
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cuenta_pagar_tenant ON cuenta_por_pagar(tenant_id);
CREATE INDEX idx_cuenta_pagar_proveedor ON cuenta_por_pagar(proveedor_id);
CREATE INDEX idx_cuenta_pagar_estado ON cuenta_por_pagar(estado, fecha_vencimiento);

CREATE TRIGGER cuenta_por_pagar_set_updated_at
BEFORE UPDATE ON cuenta_por_pagar FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE cuenta_por_pagar IS 'Cuentas por pagar a proveedores';

-- =============================================================================
-- 9. TABLA: pago_proveedor
-- =============================================================================
-- Pagos realizados a proveedores
CREATE TABLE IF NOT EXISTS pago_proveedor (
  id                BIGSERIAL PRIMARY KEY,
  tenant_id         BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  proveedor_id      BIGINT NOT NULL REFERENCES proveedor(id) ON DELETE RESTRICT,
  cuenta_pagar_id   BIGINT REFERENCES cuenta_por_pagar(id) ON DELETE SET NULL,
  
  fecha_pago        DATE NOT NULL DEFAULT CURRENT_DATE,
  monto             NUMERIC(12,2) NOT NULL CHECK (monto > 0),
  metodo_pago_id    BIGINT NOT NULL REFERENCES metodo_pago(id),
  
  -- Datos bancarios
  numero_operacion  VARCHAR(50),
  banco             VARCHAR(100),
  
  notas             VARCHAR(250),
  
  realizado_por_id  BIGINT NOT NULL REFERENCES usuario(id),
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pago_proveedor_tenant ON pago_proveedor(tenant_id);
CREATE INDEX idx_pago_proveedor_proveedor ON pago_proveedor(proveedor_id);
CREATE INDEX idx_pago_proveedor_fecha ON pago_proveedor(fecha_pago);

COMMENT ON TABLE pago_proveedor IS 'Registro de pagos a proveedores';

-- Trigger para actualizar cuenta por pagar
CREATE OR REPLACE FUNCTION drinkgo.actualizar_cuenta_por_pagar()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  v_total_pagado NUMERIC(12,2);
  v_monto_original NUMERIC(12,2);
BEGIN
  IF NEW.cuenta_pagar_id IS NULL THEN
    RETURN NEW;
  END IF;
  
  SELECT SUM(monto) INTO v_total_pagado
  FROM drinkgo.pago_proveedor
  WHERE cuenta_pagar_id = NEW.cuenta_pagar_id;
  
  SELECT monto_original INTO v_monto_original
  FROM drinkgo.cuenta_por_pagar
  WHERE id = NEW.cuenta_pagar_id;
  
  UPDATE drinkgo.cuenta_por_pagar
  SET monto_pagado = COALESCE(v_total_pagado, 0),
      estado = CASE 
        WHEN COALESCE(v_total_pagado, 0) >= v_monto_original THEN 'pagado'
        WHEN COALESCE(v_total_pagado, 0) > 0 THEN 'parcial'
        ELSE 'pendiente'
      END,
      actualizado_en = NOW()
  WHERE id = NEW.cuenta_pagar_id;
  
  RETURN NEW;
END $$;

CREATE TRIGGER pago_proveedor_actualizar_cuenta
AFTER INSERT ON pago_proveedor
FOR EACH ROW
EXECUTE FUNCTION drinkgo.actualizar_cuenta_por_pagar();
