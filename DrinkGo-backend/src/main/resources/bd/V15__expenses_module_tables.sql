-- =============================================================================
-- BD_DRINKGO - V15: Módulo de Gastos y Egresos
-- Fecha: 2026-01-26
-- Descripción: Control de gastos operativos, caja chica, egresos
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: categoria_gasto
-- =============================================================================
-- Categorías de gastos para clasificación y reportes
CREATE TABLE IF NOT EXISTS categoria_gasto (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT REFERENCES negocio(id) ON DELETE CASCADE,  -- NULL = global
  nombre          VARCHAR(100) NOT NULL,
  descripcion     VARCHAR(250),
  codigo_contable VARCHAR(20),  -- Para integración contable
  presupuesto_mensual NUMERIC(12,2),  -- Presupuesto opcional
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  es_deducible    BOOLEAN NOT NULL DEFAULT TRUE,  -- Para efectos tributarios
  orden           INT NOT NULL DEFAULT 0
);

-- Categorías globales predefinidas
INSERT INTO categoria_gasto (tenant_id, nombre, codigo_contable, es_deducible, orden) VALUES
(NULL, 'Servicios Públicos', '6300', TRUE, 1),
(NULL, 'Alquiler', '6310', TRUE, 2),
(NULL, 'Sueldos y Salarios', '6200', TRUE, 3),
(NULL, 'Publicidad y Marketing', '6370', TRUE, 4),
(NULL, 'Mantenimiento Local', '6350', TRUE, 5),
(NULL, 'Transporte y Delivery', '6340', TRUE, 6),
(NULL, 'Materiales de Limpieza', '6360', TRUE, 7),
(NULL, 'Materiales de Empaque', '6365', TRUE, 8),
(NULL, 'Combustible', '6345', TRUE, 9),
(NULL, 'Licencias y Permisos', '6380', TRUE, 10),
(NULL, 'Seguros', '6390', TRUE, 11),
(NULL, 'Impuestos', '6400', TRUE, 12),
(NULL, 'Servicios Profesionales', '6320', TRUE, 13),
(NULL, 'Reparaciones', '6355', TRUE, 14),
(NULL, 'Insumos de Oficina', '6375', TRUE, 15),
(NULL, 'Gastos Bancarios', '6710', TRUE, 16),
(NULL, 'Otros Gastos', '6500', TRUE, 99);

CREATE INDEX idx_categoria_gasto_tenant ON categoria_gasto(tenant_id);

COMMENT ON TABLE categoria_gasto IS 'Categorías de gastos para clasificación';

-- =============================================================================
-- 2. TABLA: gasto
-- =============================================================================
-- Registro de gastos y egresos
CREATE TABLE IF NOT EXISTS gasto (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id                 BIGINT NOT NULL REFERENCES sede(id) ON DELETE RESTRICT,
  categoria_id            BIGINT NOT NULL REFERENCES categoria_gasto(id) ON DELETE RESTRICT,
  
  -- Identificación
  numero_gasto            VARCHAR(20) NOT NULL,
  fecha_gasto             DATE NOT NULL DEFAULT CURRENT_DATE,
  
  -- Descripción
  concepto                VARCHAR(300) NOT NULL,
  descripcion_detallada   TEXT,
  
  -- Proveedor/Beneficiario (opcional)
  proveedor_id            BIGINT REFERENCES proveedor(id) ON DELETE SET NULL,
  beneficiario_nombre     VARCHAR(250),
  beneficiario_documento  VARCHAR(20),
  
  -- Comprobante de pago
  tiene_comprobante       BOOLEAN NOT NULL DEFAULT FALSE,
  comprobante_tipo        VARCHAR(2),  -- 01=Factura, 03=Boleta, 00=Sin comprobante
  comprobante_serie       VARCHAR(10),
  comprobante_numero      VARCHAR(20),
  comprobante_fecha       DATE,
  
  -- Montos
  moneda                  CHAR(3) NOT NULL DEFAULT 'PEN',
  subtotal                NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
  igv                     NUMERIC(12,2) NOT NULL DEFAULT 0,
  total                   NUMERIC(12,2) NOT NULL CHECK (total > 0),
  
  -- Pago
  metodo_pago_id          BIGINT REFERENCES metodo_pago(id),
  pagado                  BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_pago              DATE,
  numero_operacion        VARCHAR(50),  -- Ref. de transferencia, etc.
  
  -- Para gastos recurrentes
  es_recurrente           BOOLEAN NOT NULL DEFAULT FALSE,
  frecuencia_dias         INT,  -- Cada cuántos días se repite
  proximo_vencimiento     DATE,
  
  -- Estado
  estado                  VARCHAR(20) NOT NULL DEFAULT 'registrado'
                           CHECK (estado IN ('registrado', 'aprobado', 'pagado', 'anulado')),
  
  -- (Campo de caja chica removido)
  
  -- Archivos adjuntos
  archivo_comprobante_url VARCHAR(500),
  
  -- Auditoría
  registrado_por_id       BIGINT NOT NULL REFERENCES usuario(id),
  aprobado_por_id         BIGINT REFERENCES usuario(id),
  aprobado_en             TIMESTAMPTZ,
  
  notas_internas          TEXT,
  
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, numero_gasto)
);

CREATE INDEX idx_gasto_tenant ON gasto(tenant_id);
CREATE INDEX idx_gasto_sede ON gasto(sede_id);
CREATE INDEX idx_gasto_categoria ON gasto(categoria_id);
CREATE INDEX idx_gasto_fecha ON gasto(fecha_gasto);
CREATE INDEX idx_gasto_proveedor ON gasto(proveedor_id) WHERE proveedor_id IS NOT NULL;
CREATE INDEX idx_gasto_estado ON gasto(estado);
CREATE INDEX idx_gasto_recurrente ON gasto(es_recurrente, proximo_vencimiento) WHERE es_recurrente = TRUE;

CREATE TRIGGER gasto_set_updated_at
BEFORE UPDATE ON gasto FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE gasto IS 'Registro de gastos y egresos del negocio';

-- (Caja chica removida - no se utilizará en este sistema)

-- (Movimientos de caja chica removidos)

-- (Presupuestos mensuales removidos - no se utilizará en este sistema)

-- (Vista de gastos vs presupuesto removida)

-- =============================================================================
-- 7. FUNCIÓN: Obtener totales de gastos por período
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.obtener_resumen_gastos(
  p_tenant_id BIGINT,
  p_fecha_inicio DATE,
  p_fecha_fin DATE,
  p_sede_id BIGINT DEFAULT NULL
)
RETURNS TABLE (
  categoria_id BIGINT,
  categoria_nombre VARCHAR(100),
  cantidad_gastos BIGINT,
  total_subtotal NUMERIC(12,2),
  total_igv NUMERIC(12,2),
  total_general NUMERIC(12,2),
  porcentaje_del_total NUMERIC(5,2)
)
LANGUAGE plpgsql AS $$
DECLARE
  v_total_general NUMERIC(12,2);
BEGIN
  -- Calcular total general primero
  SELECT COALESCE(SUM(g.total), 0) INTO v_total_general
  FROM drinkgo.gasto g
  WHERE g.tenant_id = p_tenant_id
    AND g.fecha_gasto BETWEEN p_fecha_inicio AND p_fecha_fin
    AND g.estado <> 'anulado'
    AND (p_sede_id IS NULL OR g.sede_id = p_sede_id);
  
  RETURN QUERY
  SELECT 
    cg.id AS categoria_id,
    cg.nombre AS categoria_nombre,
    COUNT(g.id) AS cantidad_gastos,
    COALESCE(SUM(g.subtotal), 0) AS total_subtotal,
    COALESCE(SUM(g.igv), 0) AS total_igv,
    COALESCE(SUM(g.total), 0) AS total_general,
    CASE 
      WHEN v_total_general > 0 
      THEN ROUND((COALESCE(SUM(g.total), 0) / v_total_general) * 100, 2)
      ELSE 0
    END AS porcentaje_del_total
  FROM drinkgo.categoria_gasto cg
  LEFT JOIN drinkgo.gasto g ON g.categoria_id = cg.id 
    AND g.tenant_id = p_tenant_id
    AND g.fecha_gasto BETWEEN p_fecha_inicio AND p_fecha_fin
    AND g.estado <> 'anulado'
    AND (p_sede_id IS NULL OR g.sede_id = p_sede_id)
  WHERE cg.tenant_id IS NULL OR cg.tenant_id = p_tenant_id
  GROUP BY cg.id, cg.nombre
  HAVING COUNT(g.id) > 0
  ORDER BY total_general DESC;
END $$;

COMMENT ON FUNCTION drinkgo.obtener_resumen_gastos IS 'Obtiene resumen de gastos agrupados por categoría';

-- =============================================================================
-- 8. VISTA: Gastos recurrentes próximos a vencer
-- =============================================================================
CREATE OR REPLACE VIEW drinkgo.v_gastos_recurrentes_proximos AS
SELECT 
  g.id,
  g.tenant_id,
  g.sede_id,
  n.nombre AS negocio_nombre,
  s.nombre AS sede_nombre,
  cg.nombre AS categoria,
  g.concepto,
  g.total,
  g.frecuencia_dias,
  g.proximo_vencimiento,
  g.proximo_vencimiento - CURRENT_DATE AS dias_para_vencer
FROM drinkgo.gasto g
JOIN drinkgo.negocio n ON g.tenant_id = n.id
JOIN drinkgo.sede s ON g.sede_id = s.id
JOIN drinkgo.categoria_gasto cg ON g.categoria_id = cg.id
WHERE g.es_recurrente = TRUE
  AND g.estado <> 'anulado'
  AND g.proximo_vencimiento IS NOT NULL
ORDER BY g.proximo_vencimiento ASC;

COMMENT ON VIEW drinkgo.v_gastos_recurrentes_proximos IS 'Lista de gastos recurrentes ordenados por próximo vencimiento';
