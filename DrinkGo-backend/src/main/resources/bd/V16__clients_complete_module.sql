-- =============================================================================
-- BD_DRINKGO - V16: Módulo Completo de Clientes
-- Fecha: 2026-01-26
-- Descripción: Gestión completa de clientes, fidelización, crédito, historial
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. MEJORAR TABLA cliente (ya existe, agregar columnas faltantes)
-- =============================================================================

-- Agregar columnas adicionales si no existen
DO $$
BEGIN
  -- Documento de identidad
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'tipo_documento') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN tipo_documento VARCHAR(2) DEFAULT '1';  -- 1=DNI, 6=RUC, 4=CE
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'numero_documento') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN numero_documento VARCHAR(20);
  END IF;
  
  -- Fecha de nacimiento (importante para verificación de edad en licorería)
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'fecha_nacimiento') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN fecha_nacimiento DATE;
  END IF;
  
  -- Verificación de edad
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'edad_verificada') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN edad_verificada BOOLEAN NOT NULL DEFAULT FALSE;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'fecha_verificacion_edad') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN fecha_verificacion_edad TIMESTAMPTZ;
  END IF;
  
  -- Género
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'genero') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN genero VARCHAR(1) CHECK (genero IN ('M', 'F', 'O'));
  END IF;
  
  -- Dirección completa
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'distrito') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN distrito VARCHAR(100);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'provincia') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN provincia VARCHAR(100);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'departamento') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN departamento VARCHAR(100);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'referencia') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN referencia VARCHAR(250);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'coordenadas_lat') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN coordenadas_lat NUMERIC(10,7);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'coordenadas_lng') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN coordenadas_lng NUMERIC(10,7);
  END IF;
  
  -- (Campos de crédito y fidelización removidos - no se utilizarán)
  
  -- Estadísticas
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'total_compras') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN total_compras NUMERIC(12,2) NOT NULL DEFAULT 0;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'cantidad_compras') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN cantidad_compras INT NOT NULL DEFAULT 0;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'ultima_compra') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN ultima_compra DATE;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'promedio_compra') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN promedio_compra NUMERIC(12,2) GENERATED ALWAYS AS (
      CASE WHEN cantidad_compras > 0 THEN total_compras / cantidad_compras ELSE 0 END
    ) STORED;
  END IF;
  
  -- Preferencias
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'recibe_promociones') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN recibe_promociones BOOLEAN NOT NULL DEFAULT TRUE;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'notas') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN notas TEXT;
  END IF;
  
  -- Origen
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'origen') THEN
    ALTER TABLE drinkgo.cliente ADD COLUMN origen VARCHAR(30) DEFAULT 'tienda';
  END IF;
END $$;

-- OBLIGATORIO: reforzar reglas de documentos e integridad multi-tenant.
-- Escenario: un Admin busca por DNI/RUC dentro de su licorería sin riesgo de ver clientes de otra.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'tipo_documento'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'chk_cliente_tipo_documento'
  ) THEN
    ALTER TABLE drinkgo.cliente
    ADD CONSTRAINT chk_cliente_tipo_documento
    CHECK (tipo_documento IN ('1','4','6'));
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'tenant_id'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uq_cliente_tenant_id_id'
  ) THEN
    ALTER TABLE drinkgo.cliente
    ADD CONSTRAINT uq_cliente_tenant_id_id UNIQUE (tenant_id, id);
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'tenant_id'
  ) AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'numero_documento'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uq_cliente_tenant_documento'
  ) THEN
    ALTER TABLE drinkgo.cliente
    ADD CONSTRAINT uq_cliente_tenant_documento UNIQUE (tenant_id, numero_documento);
  END IF;

  -- OPCIONAL: unicidad por email dentro del tenant (evita duplicados por licorería, sin bloquear emails repetidos entre licorerías).
  -- Escenario: Cliente se registra 2 veces con el mismo email en la misma licorería.
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'tenant_id'
  ) AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'cliente' AND column_name = 'email'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'uq_cliente_tenant_email'
  ) THEN
    ALTER TABLE drinkgo.cliente
    ADD CONSTRAINT uq_cliente_tenant_email UNIQUE (tenant_id, email);
  END IF;
END $$;

-- Índices adicionales
CREATE INDEX IF NOT EXISTS idx_cliente_documento ON drinkgo.cliente(numero_documento);

-- OBLIGATORIO: índices compuestos por tenant para evitar scans y accesos cross-tenant.
-- Escenario: Admin filtra clientes activos por su licorería y busca por documento.
CREATE INDEX IF NOT EXISTS idx_cliente_tenant_documento ON drinkgo.cliente(tenant_id, numero_documento);
CREATE INDEX IF NOT EXISTS idx_cliente_tenant_activo ON drinkgo.cliente(tenant_id, activo);

-- (Tabla nivel_fidelidad removida - no se utilizará programa de fidelización)

-- (Tabla movimiento_puntos removida - no se utilizará programa de fidelización)

-- =============================================================================
-- 4. TABLA: direccion_cliente
-- =============================================================================
-- Múltiples direcciones por cliente (para delivery)
CREATE TABLE IF NOT EXISTS direccion_cliente (
  id                  BIGSERIAL PRIMARY KEY,
  cliente_id          BIGINT NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
  tenant_id           BIGINT,
  
  etiqueta            VARCHAR(50) NOT NULL,  -- 'Casa', 'Trabajo', 'Otro'
  direccion           VARCHAR(300) NOT NULL,
  referencia          VARCHAR(250),
  distrito            VARCHAR(100),
  provincia           VARCHAR(100),
  departamento        VARCHAR(100),
  
  coordenadas_lat     NUMERIC(10,7),
  coordenadas_lng     NUMERIC(10,7),
  
  telefono_contacto   VARCHAR(20),
  instrucciones       VARCHAR(250),  -- Instrucciones de entrega
  
  es_principal        BOOLEAN NOT NULL DEFAULT FALSE,
  activo              BOOLEAN NOT NULL DEFAULT TRUE,
  
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- OBLIGATORIO: completar tenant_id para direcciones y evitar cruces entre licorerías.
-- Escenario: Cliente de Licorería A no debe tener direcciones apuntando a registros de Licorería B.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'direccion_cliente' AND column_name = 'tenant_id'
  ) THEN
    -- Asegurar columna en caso de que la tabla ya exista y no se haya creado con tenant_id.
    NULL;
  ELSE
    IF EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'drinkgo' AND table_name = 'direccion_cliente'
    ) THEN
      ALTER TABLE drinkgo.direccion_cliente ADD COLUMN tenant_id BIGINT;
    END IF;
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'direccion_cliente' AND column_name = 'tenant_id'
  ) THEN
    UPDATE drinkgo.direccion_cliente dc
    SET tenant_id = c.tenant_id
    FROM drinkgo.cliente c
    WHERE dc.tenant_id IS NULL
      AND c.id = dc.cliente_id;

    -- Intentar reforzar NOT NULL solo si se pudo poblar.
    IF NOT EXISTS (
      SELECT 1 FROM drinkgo.direccion_cliente WHERE tenant_id IS NULL
    ) THEN
      ALTER TABLE drinkgo.direccion_cliente
      ALTER COLUMN tenant_id SET NOT NULL;
    END IF;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'fk_direccion_cliente_tenant_cliente'
  ) AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'direccion_cliente' AND column_name = 'tenant_id'
  ) THEN
    ALTER TABLE drinkgo.direccion_cliente
    ADD CONSTRAINT fk_direccion_cliente_tenant_cliente
    FOREIGN KEY (tenant_id, cliente_id)
    REFERENCES drinkgo.cliente(tenant_id, id)
    ON DELETE CASCADE;
  END IF;
END $$;

CREATE INDEX idx_direccion_cliente ON direccion_cliente(cliente_id);

-- OBLIGATORIO: asegurar 1 dirección principal activa por cliente.
-- Escenario: Cliente marca "Casa" como principal; el sistema no debe dejar 2 principales activas.
CREATE UNIQUE INDEX IF NOT EXISTS ux_direccion_cliente_principal
ON drinkgo.direccion_cliente(cliente_id)
WHERE es_principal = TRUE AND activo = TRUE;

COMMENT ON TABLE direccion_cliente IS 'Direcciones de entrega del cliente';

-- =============================================================================
-- 5. TABLA: nota_cliente
-- =============================================================================
-- Notas internas sobre clientes (CRM básico)
CREATE TABLE IF NOT EXISTS nota_cliente (
  id                  BIGSERIAL PRIMARY KEY,
  cliente_id          BIGINT NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
  tenant_id           BIGINT,
  
  tipo_nota           VARCHAR(30) NOT NULL DEFAULT 'general'
                       CHECK (tipo_nota IN ('general', 'queja', 'preferencia', 
                                            'alerta', 'seguimiento', 'oportunidad')),
  contenido           TEXT NOT NULL,
  
  -- Seguimiento
  requiere_seguimiento BOOLEAN NOT NULL DEFAULT FALSE,
  fecha_seguimiento   DATE,
  seguimiento_completado BOOLEAN NOT NULL DEFAULT FALSE,
  
  creado_por_id       BIGINT NOT NULL REFERENCES usuario(id),
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- OBLIGATORIO: tenant_id en notas para evitar cruces y reforzar reportes por licorería.
-- Escenario: Admin de Licorería A registra una alerta sobre un cliente; no debe aparecer en Licorería B.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'nota_cliente' AND column_name = 'tenant_id'
  ) THEN
    NULL;
  ELSE
    IF EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'drinkgo' AND table_name = 'nota_cliente'
    ) THEN
      ALTER TABLE drinkgo.nota_cliente ADD COLUMN tenant_id BIGINT;
    END IF;
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'nota_cliente' AND column_name = 'tenant_id'
  ) THEN
    UPDATE drinkgo.nota_cliente nc
    SET tenant_id = c.tenant_id
    FROM drinkgo.cliente c
    WHERE nc.tenant_id IS NULL
      AND c.id = nc.cliente_id;

    IF NOT EXISTS (
      SELECT 1 FROM drinkgo.nota_cliente WHERE tenant_id IS NULL
    ) THEN
      ALTER TABLE drinkgo.nota_cliente
      ALTER COLUMN tenant_id SET NOT NULL;
    END IF;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'fk_nota_cliente_tenant_cliente'
  ) AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'nota_cliente' AND column_name = 'tenant_id'
  ) THEN
    ALTER TABLE drinkgo.nota_cliente
    ADD CONSTRAINT fk_nota_cliente_tenant_cliente
    FOREIGN KEY (tenant_id, cliente_id)
    REFERENCES drinkgo.cliente(tenant_id, id)
    ON DELETE CASCADE;
  END IF;

  -- OPCIONAL: consistencia de seguimiento.
  -- Escenario: si la nota requiere seguimiento, debe tener fecha programada.
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'chk_nota_cliente_seguimiento_fecha'
  ) THEN
    ALTER TABLE drinkgo.nota_cliente
    ADD CONSTRAINT chk_nota_cliente_seguimiento_fecha
    CHECK (requiere_seguimiento = FALSE OR fecha_seguimiento IS NOT NULL);
  END IF;
END $$;

CREATE INDEX idx_nota_cliente ON nota_cliente(cliente_id);
CREATE INDEX idx_nota_seguimiento ON nota_cliente(fecha_seguimiento) 
  WHERE requiere_seguimiento = TRUE AND seguimiento_completado = FALSE;

COMMENT ON TABLE nota_cliente IS 'Notas internas sobre clientes para CRM';

-- =============================================================================
-- 6. TABLA: preferencia_cliente
-- =============================================================================
-- Preferencias de productos del cliente
CREATE TABLE IF NOT EXISTS preferencia_cliente (
  id                  BIGSERIAL PRIMARY KEY,
  cliente_id          BIGINT NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
  tenant_id           BIGINT,
  
  tipo_preferencia    VARCHAR(30) NOT NULL CHECK (tipo_preferencia IN 
                       ('favorito', 'no_le_gusta', 'alergia', 'restriccion')),
  
  -- Puede ser producto, marca o categoría
  producto_id         BIGINT REFERENCES producto(id) ON DELETE CASCADE,
  marca_id            BIGINT REFERENCES marca(id) ON DELETE CASCADE,
  categoria_id        BIGINT REFERENCES categoria(id) ON DELETE CASCADE,
  
  notas               VARCHAR(250),
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  -- Al menos uno debe estar definido
  CHECK (producto_id IS NOT NULL OR marca_id IS NOT NULL OR categoria_id IS NOT NULL)
);

-- OBLIGATORIO: tenant_id en preferencias para evitar cruces (cliente/producto/marca/categoría deben pertenecer a la misma licorería).
-- Escenario: Cliente de Licorería A marca como favorito un producto de Licorería B; la BD debe impedirlo.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'preferencia_cliente' AND column_name = 'tenant_id'
  ) THEN
    NULL;
  ELSE
    IF EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'drinkgo' AND table_name = 'preferencia_cliente'
    ) THEN
      ALTER TABLE drinkgo.preferencia_cliente ADD COLUMN tenant_id BIGINT;
    END IF;
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'preferencia_cliente' AND column_name = 'tenant_id'
  ) THEN
    UPDATE drinkgo.preferencia_cliente pc
    SET tenant_id = c.tenant_id
    FROM drinkgo.cliente c
    WHERE pc.tenant_id IS NULL
      AND c.id = pc.cliente_id;

    IF NOT EXISTS (
      SELECT 1 FROM drinkgo.preferencia_cliente WHERE tenant_id IS NULL
    ) THEN
      ALTER TABLE drinkgo.preferencia_cliente
      ALTER COLUMN tenant_id SET NOT NULL;
    END IF;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'fk_preferencia_cliente_tenant_cliente'
  ) AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'preferencia_cliente' AND column_name = 'tenant_id'
  ) THEN
    ALTER TABLE drinkgo.preferencia_cliente
    ADD CONSTRAINT fk_preferencia_cliente_tenant_cliente
    FOREIGN KEY (tenant_id, cliente_id)
    REFERENCES drinkgo.cliente(tenant_id, id)
    ON DELETE CASCADE;
  END IF;
END $$;

-- OPCIONAL: evitar duplicados exactos de preferencias por cliente.
-- Escenario: Cliente marca 3 veces el mismo producto como "favorito".
CREATE UNIQUE INDEX IF NOT EXISTS ux_preferencia_cliente_no_duplicados
ON drinkgo.preferencia_cliente(cliente_id, tipo_preferencia, producto_id, marca_id, categoria_id)
;

CREATE INDEX idx_preferencia_cliente ON preferencia_cliente(cliente_id);

COMMENT ON TABLE preferencia_cliente IS 'Preferencias de productos del cliente';

-- =============================================================================
-- 7. FUNCIÓN: Actualizar estadísticas del cliente (simplificada)
-- =============================================================================
-- OBLIGATORIO: función tenant-safe (evita actualización cross-tenant por error o bug).
-- Escenario: Admin registra una venta; la estadística debe afectar solo al cliente de su licorería.
CREATE OR REPLACE FUNCTION drinkgo.actualizar_estadisticas_cliente(
  p_tenant_id BIGINT,
  p_cliente_id BIGINT,
  p_monto_venta NUMERIC(12,2)
)
RETURNS void LANGUAGE plpgsql AS $$
BEGIN
  UPDATE drinkgo.cliente
  SET total_compras = total_compras + p_monto_venta,
      cantidad_compras = cantidad_compras + 1,
      ultima_compra = CURRENT_DATE,
      actualizado_en = NOW()
  WHERE tenant_id = p_tenant_id
    AND id = p_cliente_id;

  IF NOT FOUND THEN
    RAISE EXCEPTION 'Cliente no encontrado o no pertenece al tenant: tenant_id=%, cliente_id=%', p_tenant_id, p_cliente_id;
  END IF;
END $$;

-- OPCIONAL: mantener compatibilidad con llamadas antiguas (deriva tenant_id desde cliente).
-- Escenario: código legado que aún llama a la función sin tenant explícito.
CREATE OR REPLACE FUNCTION drinkgo.actualizar_estadisticas_cliente(
  p_cliente_id BIGINT,
  p_monto_venta NUMERIC(12,2)
)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE
  v_tenant_id BIGINT;
BEGIN
  SELECT tenant_id INTO v_tenant_id
  FROM drinkgo.cliente
  WHERE id = p_cliente_id;

  IF v_tenant_id IS NULL THEN
    RAISE EXCEPTION 'Cliente no existe: cliente_id=%', p_cliente_id;
  END IF;

  PERFORM drinkgo.actualizar_estadisticas_cliente(v_tenant_id, p_cliente_id, p_monto_venta);
END $$;

COMMENT ON FUNCTION drinkgo.actualizar_estadisticas_cliente IS 'Actualiza estadísticas de compras del cliente';

-- =============================================================================
-- 8. FUNCIÓN: Verificar edad del cliente
-- =============================================================================
-- OBLIGATORIO: verificación de edad tenant-safe.
-- Escenario: delivery de alcohol exige comprobar mayoría de edad solo dentro de la licorería correcta.
CREATE OR REPLACE FUNCTION drinkgo.verificar_edad_cliente(
  p_tenant_id BIGINT,
  p_cliente_id BIGINT,
  p_fecha_nacimiento DATE DEFAULT NULL
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_fecha_nacimiento DATE;
  v_edad INT;
BEGIN
  IF p_fecha_nacimiento IS NOT NULL THEN
    v_fecha_nacimiento := p_fecha_nacimiento;

    UPDATE drinkgo.cliente
    SET fecha_nacimiento = p_fecha_nacimiento
    WHERE tenant_id = p_tenant_id
      AND id = p_cliente_id;
  ELSE
    SELECT fecha_nacimiento INTO v_fecha_nacimiento
    FROM drinkgo.cliente
    WHERE tenant_id = p_tenant_id
      AND id = p_cliente_id;
  END IF;

  IF v_fecha_nacimiento IS NULL THEN
    RETURN FALSE;
  END IF;

  v_edad := EXTRACT(YEAR FROM AGE(CURRENT_DATE, v_fecha_nacimiento));

  IF v_edad >= 18 THEN
    UPDATE drinkgo.cliente
    SET edad_verificada = TRUE,
        fecha_verificacion_edad = NOW(),
        actualizado_en = NOW()
    WHERE tenant_id = p_tenant_id
      AND id = p_cliente_id;

    RETURN TRUE;
  END IF;

  RETURN FALSE;
END $$;

-- OPCIONAL: compatibilidad con llamadas antiguas (deriva tenant_id).
-- Escenario: flujo legacy de registro de cliente que aún no pasa tenant explícito.
CREATE OR REPLACE FUNCTION drinkgo.verificar_edad_cliente(
  p_cliente_id BIGINT,
  p_fecha_nacimiento DATE DEFAULT NULL
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_tenant_id BIGINT;
BEGIN
  SELECT tenant_id INTO v_tenant_id
  FROM drinkgo.cliente
  WHERE id = p_cliente_id;

  IF v_tenant_id IS NULL THEN
    RETURN FALSE;
  END IF;

  RETURN drinkgo.verificar_edad_cliente(v_tenant_id, p_cliente_id, p_fecha_nacimiento);
END $$;

COMMENT ON FUNCTION drinkgo.verificar_edad_cliente IS 'Verifica si el cliente es mayor de 18 años';

-- (Función obtener_credito_disponible removida - no se manejará crédito)

-- =============================================================================
-- 10. VISTA: Resumen de clientes por valor
-- =============================================================================
-- OBLIGATORIO: evitar fugas multi-tenant en vistas (si no hay tenant en contexto, no retorna filas).
-- Escenario: Admin entra a "Clientes VIP"; nunca debe ver clientes de otras licorerías.
CREATE OR REPLACE VIEW drinkgo.v_clientes_por_valor AS
SELECT 
  c.tenant_id,
  c.id AS cliente_id,
  c.nombre,
  c.apellido,
  c.email,
  c.telefono,
  c.numero_documento,
  c.total_compras,
  c.cantidad_compras,
  c.promedio_compra,
  c.ultima_compra,
  CURRENT_DATE - c.ultima_compra AS dias_sin_comprar,
  CASE
    WHEN c.cantidad_compras >= 10 AND c.total_compras >= 1000 THEN 'VIP'
    WHEN c.cantidad_compras >= 5 AND c.total_compras >= 500 THEN 'Frecuente'
    WHEN c.cantidad_compras >= 2 THEN 'Regular'
    ELSE 'Nuevo'
  END AS segmento
FROM drinkgo.cliente c
WHERE c.activo = TRUE
  AND c.tenant_id = NULLIF(current_setting('app.tenant_id', true), '')::BIGINT
ORDER BY c.total_compras DESC;

COMMENT ON VIEW drinkgo.v_clientes_por_valor IS 'Vista de clientes ordenados por valor de compras';

-- (Vista v_clientes_con_deuda removida - no se manejará crédito a clientes)
