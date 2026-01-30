-- =============================================================================
-- BD_DRINKGO - V9: Sistema de Pedidos (Delivery, Pickup, Mesa)
-- Fecha: 2026-01-26
-- Descripción: Pedidos polimórficos con soporte para múltiples modalidades
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: pedido (Polimórfico)
-- =============================================================================
CREATE TABLE IF NOT EXISTS pedido (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id                 BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  
  -- Número único de pedido
  numero_pedido           VARCHAR(30) NOT NULL,
  
  -- Cliente (puede ser NULL para pedidos anónimos en barra)
  cliente_id              BIGINT REFERENCES cliente(id) ON DELETE SET NULL,
  cliente_nombre          VARCHAR(150),  -- Para pedidos sin registro
  cliente_telefono        VARCHAR(20),
  
  -- MODALIDAD POLIMÓRFICA (CRÍTICO)
  modalidad               drinkgo.order_modality NOT NULL,
  
  -- Para Delivery
  direccion_entrega       VARCHAR(300),
  direccion_referencia    VARCHAR(250),
  distrito_entrega        VARCHAR(80),
  coordenadas_lat         NUMERIC(10,7),
  coordenadas_lng         NUMERIC(10,7),
  costo_delivery          NUMERIC(12,2) DEFAULT 0 CHECK (costo_delivery >= 0),
  
  -- Para Pickup
  hora_recojo             TIME,
  
  -- Para Mesa/Barra (referencia a cuenta de mesa si aplica)
  mesa_id                 BIGINT REFERENCES mesa(id) ON DELETE SET NULL,
  cuenta_mesa_id          BIGINT REFERENCES cuenta_mesa(id) ON DELETE SET NULL,
  
  -- Fechas
  fecha_creacion          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  fecha_entrega_estimada  TIMESTAMPTZ,
  fecha_entrega_real      TIMESTAMPTZ,
  
  -- Estado
  estado                  drinkgo.order_status NOT NULL DEFAULT 'pendiente',
  
  -- Origen del pedido
  origen                  VARCHAR(20) NOT NULL DEFAULT 'pos'
                           CHECK (origen IN ('pos', 'storefront', 'whatsapp', 'telefono', 'app')),
  
  -- Pago online (para storefront)
  pago_online_estado      VARCHAR(20) CHECK (pago_online_estado IN ('pendiente', 'procesando', 'pagado', 'fallido', 'reembolsado')),
  pago_online_referencia  VARCHAR(120),
  pago_online_metodo      VARCHAR(50),
  
  -- Montos
  subtotal                NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
  descuento_total         NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (descuento_total >= 0),
  impuesto                NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (impuesto >= 0),
  total                   NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
  
  -- Asignaciones
  preparado_por_id        BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  repartidor_id           BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  
  -- Verificación de edad (CRÍTICO para licorería)
  requiere_verificacion_edad BOOLEAN NOT NULL DEFAULT TRUE,
  verificacion_realizada  BOOLEAN NOT NULL DEFAULT FALSE,
  
  observaciones           TEXT,
  notas_preparacion       TEXT,
  
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_pedido),
  
  -- Validaciones por modalidad
  CONSTRAINT chk_pedido_delivery CHECK (
    modalidad <> 'delivery' OR direccion_entrega IS NOT NULL
  ),
  CONSTRAINT chk_pedido_mesa CHECK (
    modalidad NOT IN ('mesa', 'barra') OR mesa_id IS NOT NULL
  )
);

CREATE INDEX idx_pedido_tenant ON pedido(tenant_id);
CREATE INDEX idx_pedido_sede ON pedido(sede_id);
CREATE INDEX idx_pedido_estado ON pedido(estado, fecha_creacion);
CREATE INDEX idx_pedido_modalidad ON pedido(modalidad, estado);
CREATE INDEX idx_pedido_cliente ON pedido(cliente_id);
CREATE INDEX idx_pedido_mesa ON pedido(mesa_id) WHERE mesa_id IS NOT NULL;
CREATE INDEX idx_pedido_repartidor ON pedido(repartidor_id) WHERE repartidor_id IS NOT NULL;
CREATE INDEX idx_pedido_fecha ON pedido(fecha_creacion);

CREATE TRIGGER pedido_set_updated_at
BEFORE UPDATE ON pedido FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

-- Trigger: Validar máquina de estados
CREATE TRIGGER pedido_validar_estado
BEFORE INSERT OR UPDATE ON pedido FOR EACH ROW
EXECUTE FUNCTION drinkgo.validar_cambio_estado_pedido();

COMMENT ON TABLE pedido IS 'Pedidos polimórficos: delivery, pickup, mesa o barra';
COMMENT ON COLUMN pedido.modalidad IS 'Define el tipo de pedido y campos requeridos';
COMMENT ON COLUMN pedido.requiere_verificacion_edad IS 'TRUE si el pedido contiene productos alcohólicos';

-- =============================================================================
-- 2. TABLA: pedido_item
-- =============================================================================
CREATE TABLE IF NOT EXISTS pedido_item (
  id                BIGSERIAL PRIMARY KEY,
  pedido_id         BIGINT NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
  producto_id       BIGINT REFERENCES producto(id) ON DELETE SET NULL,
  combo_id          BIGINT REFERENCES combo(id) ON DELETE SET NULL,
  
  codigo_producto   VARCHAR(50),
  nombre_producto   VARCHAR(200) NOT NULL,
  
  cantidad          NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  precio_unitario   NUMERIC(12,2) NOT NULL CHECK (precio_unitario >= 0),
  descuento         NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (descuento >= 0),
  subtotal          NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
  
  promocion_id      BIGINT REFERENCES promocion(id) ON DELETE SET NULL,
  
  notas             VARCHAR(250),
  
  CONSTRAINT chk_pedido_item_tipo CHECK (producto_id IS NOT NULL OR combo_id IS NOT NULL)
);

CREATE INDEX idx_pedido_item_pedido ON pedido_item(pedido_id);
CREATE INDEX idx_pedido_item_producto ON pedido_item(producto_id);

COMMENT ON TABLE pedido_item IS 'Items de cada pedido';

-- =============================================================================
-- 3. TABLA: pago_pedido
-- =============================================================================
CREATE TABLE IF NOT EXISTS pago_pedido (
  id              BIGSERIAL PRIMARY KEY,
  pedido_id       BIGINT NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
  
  metodo_pago     drinkgo.payment_method NOT NULL,
  monto           NUMERIC(12,2) NOT NULL CHECK (monto > 0),
  referencia      VARCHAR(120),
  
  -- Para pagos online
  gateway         VARCHAR(50),  -- 'stripe', 'mercadopago', 'izipay', etc.
  gateway_id      VARCHAR(120),
  
  pagado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pago_pedido_pedido ON pago_pedido(pedido_id);
CREATE INDEX idx_pago_pedido_metodo ON pago_pedido(metodo_pago);

COMMENT ON TABLE pago_pedido IS 'Pagos de pedidos (soporta pagos parciales)';

-- =============================================================================
-- 4. TABLA: seguimiento_pedido
-- =============================================================================
-- Historial de estados del pedido (tracking)
CREATE TABLE IF NOT EXISTS seguimiento_pedido (
  id              BIGSERIAL PRIMARY KEY,
  pedido_id       BIGINT NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
  
  estado_anterior drinkgo.order_status,
  estado_nuevo    drinkgo.order_status NOT NULL,
  
  mensaje         VARCHAR(250),
  ubicacion_lat   NUMERIC(10,7),  -- Para tracking de delivery
  ubicacion_lng   NUMERIC(10,7),
  
  usuario_id      BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_seguimiento_pedido ON seguimiento_pedido(pedido_id);
CREATE INDEX idx_seguimiento_fecha ON seguimiento_pedido(creado_en);

COMMENT ON TABLE seguimiento_pedido IS 'Historial de estados y tracking de pedidos';

-- =============================================================================
-- 5. TABLA: pedido_adjunto
-- =============================================================================
-- Archivos adjuntos (comprobantes de pago, etc.)
CREATE TABLE IF NOT EXISTS pedido_adjunto (
  id              BIGSERIAL PRIMARY KEY,
  pedido_id       BIGINT NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
  
  tipo            VARCHAR(30) NOT NULL CHECK (tipo IN ('comprobante_pago', 'documento_cliente', 'foto_entrega', 'otro')),
  nombre_archivo  VARCHAR(200) NOT NULL,
  ruta_archivo    VARCHAR(500) NOT NULL,
  tipo_mime       VARCHAR(80),
  tamano_bytes    BIGINT CHECK (tamano_bytes IS NULL OR tamano_bytes >= 0),
  
  subido_por_id   BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  subido_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pedido_adjunto_pedido ON pedido_adjunto(pedido_id);

COMMENT ON TABLE pedido_adjunto IS 'Archivos adjuntos a pedidos';

-- =============================================================================
-- 6. FUNCIÓN: Crear seguimiento automático de estados
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.registrar_cambio_estado_pedido()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF TG_OP = 'INSERT' OR OLD.estado <> NEW.estado THEN
    INSERT INTO drinkgo.seguimiento_pedido (pedido_id, estado_anterior, estado_nuevo)
    VALUES (
      NEW.id,
      CASE WHEN TG_OP = 'UPDATE' THEN OLD.estado ELSE NULL END,
      NEW.estado
    );
  END IF;
  RETURN NEW;
END $$;

CREATE TRIGGER pedido_registrar_estado
AFTER INSERT OR UPDATE ON pedido
FOR EACH ROW
EXECUTE FUNCTION drinkgo.registrar_cambio_estado_pedido();

-- =============================================================================
-- 7. TABLA: zona_delivery
-- =============================================================================
-- Zonas de cobertura para delivery por sede
CREATE TABLE IF NOT EXISTS zona_delivery (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id         BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  
  nombre          VARCHAR(80) NOT NULL,
  distritos       TEXT[],  -- Array de distritos cubiertos
  
  costo_delivery  NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (costo_delivery >= 0),
  tiempo_estimado_minutos INT,
  pedido_minimo   NUMERIC(12,2) DEFAULT 0,
  
  -- Polígono de cobertura (para geofencing)
  poligono        JSONB,  -- GeoJSON del polígono
  
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_zona_delivery_sede ON zona_delivery(sede_id);

COMMENT ON TABLE zona_delivery IS 'Zonas de cobertura para delivery';

-- =============================================================================
-- 8. TABLA: calificacion_pedido
-- =============================================================================
-- Calificaciones de pedidos (para delivery)
CREATE TABLE IF NOT EXISTS calificacion_pedido (
  id              BIGSERIAL PRIMARY KEY,
  pedido_id       BIGINT UNIQUE NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
  
  estrellas       INT NOT NULL CHECK (estrellas BETWEEN 1 AND 5),
  comentario      TEXT,
  
  -- Detalles
  puntualidad     INT CHECK (puntualidad BETWEEN 1 AND 5),
  calidad_producto INT CHECK (calidad_producto BETWEEN 1 AND 5),
  atencion        INT CHECK (atencion BETWEEN 1 AND 5),
  
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_calificacion_pedido ON calificacion_pedido(pedido_id);

COMMENT ON TABLE calificacion_pedido IS 'Calificaciones de clientes sobre pedidos';
