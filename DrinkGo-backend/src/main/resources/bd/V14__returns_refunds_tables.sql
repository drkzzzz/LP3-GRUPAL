-- =============================================================================
-- BD_DRINKGO - V14: Módulo de Devoluciones y Notas de Crédito
-- Fecha: 2026-01-26
-- Descripción: Sistema completo de devoluciones de clientes y a proveedores
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: motivo_devolucion
-- =============================================================================
-- Catálogo de motivos de devolución
CREATE TABLE IF NOT EXISTS motivo_devolucion (
  id              BIGSERIAL PRIMARY KEY,
  codigo          VARCHAR(2) NOT NULL UNIQUE,
  nombre          VARCHAR(100) NOT NULL,
  descripcion     VARCHAR(250),
  aplica_a        VARCHAR(20) NOT NULL CHECK (aplica_a IN ('cliente', 'proveedor', 'ambos')),
  requiere_producto_fisico BOOLEAN NOT NULL DEFAULT TRUE,
  activo          BOOLEAN NOT NULL DEFAULT TRUE
);

-- Motivos según SUNAT + específicos de licorería
INSERT INTO motivo_devolucion (codigo, nombre, aplica_a, requiere_producto_fisico) VALUES
('01', 'Anulación de la operación', 'cliente', FALSE),
('02', 'Anulación por error en documento', 'cliente', FALSE),
('03', 'Descuento global', 'cliente', FALSE),
('04', 'Devolución de item', 'cliente', TRUE),
('05', 'Producto en mal estado', 'cliente', TRUE),
('06', 'Producto defectuoso/roto', 'ambos', TRUE),
('07', 'Producto vencido', 'ambos', TRUE),
('08', 'Error en el pedido', 'ambos', TRUE),
('09', 'Producto equivocado entregado', 'ambos', TRUE),
('10', 'Cliente cambió de opinión', 'cliente', TRUE),
('11', 'Diferencia en cantidad', 'proveedor', TRUE),
('12', 'Producto no solicitado', 'proveedor', TRUE);

COMMENT ON TABLE motivo_devolucion IS 'Catálogo de motivos de devolución (SUNAT + específicos)';

-- =============================================================================
-- 2. TABLA: devolucion_cliente
-- =============================================================================
-- Devoluciones de clientes (genera Nota de Crédito)
CREATE TABLE IF NOT EXISTS devolucion_cliente (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id                 BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  
  -- Numeración interna
  numero_devolucion       VARCHAR(20) NOT NULL,
  fecha_devolucion        DATE NOT NULL DEFAULT CURRENT_DATE,
  hora_devolucion         TIME NOT NULL DEFAULT CURRENT_TIME,
  
  -- Referencia a la venta original
  venta_id                BIGINT REFERENCES venta(id) ON DELETE SET NULL,
  comprobante_ref_id      BIGINT REFERENCES comprobante_emitido(id) ON DELETE SET NULL,
  
  -- Cliente
  cliente_id              BIGINT REFERENCES cliente(id) ON DELETE SET NULL,
  cliente_nombre          VARCHAR(250) NOT NULL,
  cliente_documento       VARCHAR(20),
  
  -- Motivo
  motivo_devolucion_id    BIGINT NOT NULL REFERENCES motivo_devolucion(id),
  motivo_detalle          TEXT,
  
  -- Tipo de devolución
  tipo_devolucion         VARCHAR(20) NOT NULL DEFAULT 'parcial'
                           CHECK (tipo_devolucion IN ('total', 'parcial', 'cambio')),
  
  -- Montos
  moneda                  CHAR(3) NOT NULL DEFAULT 'PEN',
  subtotal                NUMERIC(12,2) NOT NULL DEFAULT 0,
  igv                     NUMERIC(12,2) NOT NULL DEFAULT 0,
  total                   NUMERIC(12,2) NOT NULL DEFAULT 0,
  
  -- Estado
  estado                  VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                           CHECK (estado IN ('pendiente', 'aprobada', 'procesada', 
                                             'reembolsada', 'rechazada', 'anulada')),
  
  -- Reembolso
  metodo_reembolso        VARCHAR(30) CHECK (metodo_reembolso IN 
                           ('efectivo', 'tarjeta', 'credito_tienda', 'transferencia', 'mismo_medio')),
  reembolso_monto         NUMERIC(12,2),
  reembolso_fecha         DATE,
  reembolso_referencia    VARCHAR(100),
  
  -- Nota de crédito generada
  nota_credito_id         BIGINT REFERENCES comprobante_emitido(id) ON DELETE SET NULL,
  
  -- Auditoría
  solicitado_por_id       BIGINT NOT NULL REFERENCES usuario(id),
  aprobado_por_id         BIGINT REFERENCES usuario(id),
  aprobado_en             TIMESTAMPTZ,
  procesado_por_id        BIGINT REFERENCES usuario(id),
  procesado_en            TIMESTAMPTZ,
  
  notas_internas          TEXT,
  
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_devolucion)
);

CREATE INDEX idx_devolucion_cliente_tenant ON devolucion_cliente(tenant_id);
CREATE INDEX idx_devolucion_cliente_sede ON devolucion_cliente(sede_id);
CREATE INDEX idx_devolucion_cliente_venta ON devolucion_cliente(venta_id);
CREATE INDEX idx_devolucion_cliente_fecha ON devolucion_cliente(fecha_devolucion);
CREATE INDEX idx_devolucion_cliente_estado ON devolucion_cliente(estado);

CREATE TRIGGER devolucion_cliente_set_updated_at
BEFORE UPDATE ON devolucion_cliente FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE devolucion_cliente IS 'Devoluciones de clientes con generación de Nota de Crédito';

-- =============================================================================
-- 3. TABLA: devolucion_cliente_item
-- =============================================================================
-- Detalle de productos devueltos
CREATE TABLE IF NOT EXISTS devolucion_cliente_item (
  id                  BIGSERIAL PRIMARY KEY,
  devolucion_id       BIGINT NOT NULL REFERENCES devolucion_cliente(id) ON DELETE CASCADE,
  
  -- Producto
  producto_id         BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  descripcion         VARCHAR(300) NOT NULL,
  
  -- Cantidades
  cantidad            NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  precio_unitario     NUMERIC(12,4) NOT NULL,
  descuento           NUMERIC(12,2) NOT NULL DEFAULT 0,
  subtotal            NUMERIC(12,2) NOT NULL,
  
  -- Lote (para trazabilidad)
  lote_id             BIGINT REFERENCES lote_inventario(id) ON DELETE SET NULL,
  
  -- Estado del producto devuelto
  estado_producto     VARCHAR(30) NOT NULL DEFAULT 'bueno'
                       CHECK (estado_producto IN ('bueno', 'dañado', 'vencido', 'no_retornable')),
  
  -- Qué hacer con el producto
  accion_inventario   VARCHAR(20) NOT NULL DEFAULT 'reingreso'
                       CHECK (accion_inventario IN ('reingreso', 'merma', 'revision', 'destruccion')),
  reingresado         BOOLEAN NOT NULL DEFAULT FALSE,
  
  notas               VARCHAR(250)
);

CREATE INDEX idx_devolucion_cliente_item_dev ON devolucion_cliente_item(devolucion_id);
CREATE INDEX idx_devolucion_cliente_item_prod ON devolucion_cliente_item(producto_id);

COMMENT ON TABLE devolucion_cliente_item IS 'Detalle de items devueltos por cliente';

-- =============================================================================
-- 4. FUNCIÓN: Procesar devolución de cliente
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.procesar_devolucion_cliente(
  p_devolucion_id BIGINT,
  p_usuario_id BIGINT
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_dev RECORD;
  v_item RECORD;
  v_lote_id BIGINT;
BEGIN
  -- Obtener devolución
  SELECT * INTO v_dev FROM drinkgo.devolucion_cliente WHERE id = p_devolucion_id;
  
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Devolución no encontrada: %', p_devolucion_id;
  END IF;
  
  IF v_dev.estado NOT IN ('pendiente', 'aprobada') THEN
    RAISE EXCEPTION 'La devolución ya fue procesada o está en un estado inválido';
  END IF;
  
  -- Procesar cada item
  FOR v_item IN 
    SELECT * FROM drinkgo.devolucion_cliente_item 
    WHERE devolucion_id = p_devolucion_id AND NOT reingresado
  LOOP
    -- Solo reingresar si el producto está en buen estado
    IF v_item.accion_inventario = 'reingreso' AND v_item.estado_producto = 'bueno' THEN
      -- Obtener o crear lote
      v_lote_id := v_item.lote_id;
      
      IF v_lote_id IS NOT NULL THEN
        -- Reingresar al lote existente
        UPDATE drinkgo.lote_inventario
        SET cantidad_disponible = cantidad_disponible + v_item.cantidad,
            actualizado_en = NOW()
        WHERE id = v_lote_id;
      ELSE
        -- Crear nuevo lote para la devolución
        INSERT INTO drinkgo.lote_inventario (
          tenant_id, sede_id, almacen_id, producto_id, codigo_lote,
          cantidad_inicial, cantidad_disponible,
          costo_unitario, estado, notas
        )
        SELECT 
          v_dev.tenant_id, v_dev.sede_id,
          (SELECT almacen_principal_id FROM drinkgo.sede WHERE id = v_dev.sede_id),
          v_item.producto_id,
          'DEV-' || TO_CHAR(NOW(), 'YYYYMMDD-HH24MISS'),
          v_item.cantidad, v_item.cantidad,
          v_item.precio_unitario, 'disponible', 'Reingreso por devolución'
        RETURNING id INTO v_lote_id;
      END IF;
      
      -- Registrar movimiento
      INSERT INTO drinkgo.movimiento_inventario (
        tenant_id, sede_id, producto_id, lote_id,
        tipo_movimiento, cantidad,
        referencia_tipo, referencia_id, motivo, usuario_id
      )
      VALUES (
        v_dev.tenant_id, v_dev.sede_id,
        v_item.producto_id, v_lote_id,
        'devolucion', v_item.cantidad,
        'devolucion', p_devolucion_id, 'Reingreso por devolución de cliente', p_usuario_id
      );
      
      -- NOTA: El stock se actualiza automáticamente en inventario_consolidado
      -- via el trigger lote_actualizar_consolidado definido en V6
      
      -- Marcar como reingresado
      UPDATE drinkgo.devolucion_cliente_item
      SET reingresado = TRUE
      WHERE id = v_item.id;
      
    ELSIF v_item.accion_inventario = 'merma' THEN
      -- Registrar como merma en movimiento_inventario (tipo 'merma')
      INSERT INTO drinkgo.movimiento_inventario (
        tenant_id, sede_id, producto_id, lote_id,
        tipo_movimiento, cantidad,
        referencia_tipo, referencia_id, motivo, usuario_id
      )
      VALUES (
        v_dev.tenant_id, v_dev.sede_id,
        v_item.producto_id, v_item.lote_id,
        'merma', v_item.cantidad,
        'devolucion', p_devolucion_id, 'Producto devuelto en mal estado - merma', p_usuario_id
      );
      
      -- Descontar del lote si existe
      IF v_item.lote_id IS NOT NULL THEN
        UPDATE drinkgo.lote_inventario
        SET cantidad_disponible = cantidad_disponible - v_item.cantidad,
            actualizado_en = NOW()
        WHERE id = v_item.lote_id;
      END IF;
    END IF;
  END LOOP;
  
  -- Actualizar estado de la devolución
  UPDATE drinkgo.devolucion_cliente
  SET estado = 'procesada',
      procesado_por_id = p_usuario_id,
      procesado_en = NOW(),
      actualizado_en = NOW()
  WHERE id = p_devolucion_id;
  
  RETURN TRUE;
END $$;

COMMENT ON FUNCTION drinkgo.procesar_devolucion_cliente IS 'Procesa una devolución reingresando productos al inventario';

-- =============================================================================
-- 5. TABLA: devolucion_proveedor
-- =============================================================================
-- Devoluciones a proveedores
CREATE TABLE IF NOT EXISTS devolucion_proveedor (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id                 BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  almacen_id              BIGINT NOT NULL REFERENCES almacen(id) ON DELETE RESTRICT,
  proveedor_id            BIGINT NOT NULL REFERENCES proveedor(id) ON DELETE RESTRICT,
  
  -- Numeración
  numero_devolucion       VARCHAR(20) NOT NULL,
  fecha_devolucion        DATE NOT NULL DEFAULT CURRENT_DATE,
  
  -- Referencia a la compra original
  compra_id               BIGINT REFERENCES compra(id) ON DELETE SET NULL,
  
  -- Motivo
  motivo_devolucion_id    BIGINT NOT NULL REFERENCES motivo_devolucion(id),
  motivo_detalle          TEXT,
  
  -- Montos
  moneda                  CHAR(3) NOT NULL DEFAULT 'PEN',
  subtotal                NUMERIC(12,2) NOT NULL DEFAULT 0,
  igv                     NUMERIC(12,2) NOT NULL DEFAULT 0,
  total                   NUMERIC(12,2) NOT NULL DEFAULT 0,
  
  -- Estado
  estado                  VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                           CHECK (estado IN ('pendiente', 'aprobada', 'enviada', 
                                             'recibida_proveedor', 'nota_credito_recibida', 
                                             'rechazada', 'anulada')),
  
  -- Nota de crédito del proveedor
  nc_proveedor_serie      VARCHAR(10),
  nc_proveedor_numero     VARCHAR(20),
  nc_proveedor_fecha      DATE,
  nc_proveedor_monto      NUMERIC(12,2),
  
  -- Guía de remisión (para envío al proveedor)
  guia_remision_serie     VARCHAR(10),
  guia_remision_numero    VARCHAR(20),
  
  -- Auditoría
  solicitado_por_id       BIGINT NOT NULL REFERENCES usuario(id),
  aprobado_por_id         BIGINT REFERENCES usuario(id),
  aprobado_en             TIMESTAMPTZ,
  
  notas_internas          TEXT,
  
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_devolucion)
);

CREATE INDEX idx_devolucion_proveedor_tenant ON devolucion_proveedor(tenant_id);
CREATE INDEX idx_devolucion_proveedor_proveedor ON devolucion_proveedor(proveedor_id);
CREATE INDEX idx_devolucion_proveedor_fecha ON devolucion_proveedor(fecha_devolucion);
CREATE INDEX idx_devolucion_proveedor_estado ON devolucion_proveedor(estado);

CREATE TRIGGER devolucion_proveedor_set_updated_at
BEFORE UPDATE ON devolucion_proveedor FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE devolucion_proveedor IS 'Devoluciones de productos a proveedores';

-- =============================================================================
-- 6. TABLA: devolucion_proveedor_item
-- =============================================================================
CREATE TABLE IF NOT EXISTS devolucion_proveedor_item (
  id                  BIGSERIAL PRIMARY KEY,
  devolucion_id       BIGINT NOT NULL REFERENCES devolucion_proveedor(id) ON DELETE CASCADE,
  producto_id         BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  
  cantidad            NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  precio_unitario     NUMERIC(12,4) NOT NULL,
  subtotal            NUMERIC(12,2) NOT NULL,
  
  -- Lote específico
  lote_id             BIGINT REFERENCES lote_inventario(id) ON DELETE SET NULL,
  lote_numero         VARCHAR(50),
  fecha_vencimiento   DATE,
  
  -- Estado
  estado_producto     VARCHAR(30) NOT NULL,
  descontado_inventario BOOLEAN NOT NULL DEFAULT FALSE,
  
  notas               VARCHAR(250)
);

CREATE INDEX idx_devolucion_proveedor_item_dev ON devolucion_proveedor_item(devolucion_id);

COMMENT ON TABLE devolucion_proveedor_item IS 'Detalle de items devueltos a proveedor';

-- =============================================================================
-- 7. FUNCIÓN: Procesar devolución a proveedor (descontar inventario)
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.procesar_devolucion_proveedor(
  p_devolucion_id BIGINT,
  p_usuario_id BIGINT
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_dev RECORD;
  v_item RECORD;
BEGIN
  SELECT * INTO v_dev FROM drinkgo.devolucion_proveedor WHERE id = p_devolucion_id;
  
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Devolución no encontrada: %', p_devolucion_id;
  END IF;
  
  IF v_dev.estado NOT IN ('pendiente', 'aprobada') THEN
    RAISE EXCEPTION 'La devolución ya fue procesada';
  END IF;
  
  FOR v_item IN 
    SELECT * FROM drinkgo.devolucion_proveedor_item 
    WHERE devolucion_id = p_devolucion_id AND NOT descontado_inventario
  LOOP
    -- Descontar del lote específico si existe
    IF v_item.lote_id IS NOT NULL THEN
      UPDATE drinkgo.lote_inventario
      SET cantidad_disponible = cantidad_disponible - v_item.cantidad,
          actualizado_en = NOW()
      WHERE id = v_item.lote_id;
    END IF;
    
    -- Registrar movimiento de salida
    INSERT INTO drinkgo.movimiento_inventario (
      tenant_id, sede_id, producto_id, lote_id,
      tipo_movimiento, cantidad,
      referencia_tipo, referencia_id, motivo, usuario_id
    ) VALUES (
      v_dev.tenant_id, v_dev.sede_id, v_item.producto_id, v_item.lote_id,
      'salida', v_item.cantidad,
      'devolucion_proveedor', p_devolucion_id, 'Salida por devolución a proveedor', p_usuario_id
    );
    
    -- NOTA: El stock se actualiza automáticamente en inventario_consolidado via trigger
    
    UPDATE drinkgo.devolucion_proveedor_item
    SET descontado_inventario = TRUE
    WHERE id = v_item.id;
  END LOOP;
  
  UPDATE drinkgo.devolucion_proveedor
  SET estado = 'enviada',
      aprobado_por_id = p_usuario_id,
      aprobado_en = NOW(),
      actualizado_en = NOW()
  WHERE id = p_devolucion_id;
  
  RETURN TRUE;
END $$;

-- =============================================================================
-- 8. TABLA: cambio_producto
-- =============================================================================
-- Cambios de producto (no devuelve dinero, cambia por otro)
CREATE TABLE IF NOT EXISTS cambio_producto (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id                 BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  
  numero_cambio           VARCHAR(20) NOT NULL,
  fecha_cambio            DATE NOT NULL DEFAULT CURRENT_DATE,
  
  -- Venta original
  venta_original_id       BIGINT REFERENCES venta(id) ON DELETE SET NULL,
  
  -- Cliente
  cliente_id              BIGINT REFERENCES cliente(id) ON DELETE SET NULL,
  cliente_nombre          VARCHAR(250) NOT NULL,
  
  -- Motivo
  motivo                  VARCHAR(250) NOT NULL,
  
  -- Diferencia de precio
  diferencia_precio       NUMERIC(12,2) NOT NULL DEFAULT 0,  -- Positivo = cliente paga más
  diferencia_pagada       BOOLEAN NOT NULL DEFAULT FALSE,
  
  -- Estado
  estado                  VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                           CHECK (estado IN ('pendiente', 'procesado', 'anulado')),
  
  atendido_por_id         BIGINT NOT NULL REFERENCES usuario(id),
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_cambio)
);

CREATE INDEX idx_cambio_producto_tenant ON cambio_producto(tenant_id);
CREATE INDEX idx_cambio_producto_fecha ON cambio_producto(fecha_cambio);

COMMENT ON TABLE cambio_producto IS 'Cambios de productos (sin devolución de dinero)';

-- =============================================================================
-- 9. TABLA: cambio_producto_item
-- =============================================================================
CREATE TABLE IF NOT EXISTS cambio_producto_item (
  id                  BIGSERIAL PRIMARY KEY,
  cambio_id           BIGINT NOT NULL REFERENCES cambio_producto(id) ON DELETE CASCADE,
  
  -- Producto devuelto
  producto_devuelto_id BIGINT NOT NULL REFERENCES producto(id),
  cantidad_devuelta   NUMERIC(12,3) NOT NULL,
  precio_devuelto     NUMERIC(12,2) NOT NULL,
  
  -- Producto entregado a cambio
  producto_nuevo_id   BIGINT NOT NULL REFERENCES producto(id),
  cantidad_nueva      NUMERIC(12,3) NOT NULL,
  precio_nuevo        NUMERIC(12,2) NOT NULL,
  
  -- Estado del producto devuelto
  estado_producto     VARCHAR(30) NOT NULL DEFAULT 'bueno',
  accion_inventario   VARCHAR(20) NOT NULL DEFAULT 'reingreso'
);

CREATE INDEX idx_cambio_producto_item_cambio ON cambio_producto_item(cambio_id);

COMMENT ON TABLE cambio_producto_item IS 'Detalle de productos en un cambio';
