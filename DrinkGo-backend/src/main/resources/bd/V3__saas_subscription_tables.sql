-- =============================================================================
-- BD_DRINKGO - V3: Arquitectura SaaS - Suscripciones y Tenants
-- Fecha: 2026-01-26
-- Descripción: Gestión de suscripciones, negocios (tenants) y superadmin
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: plan_suscripcion
-- =============================================================================
-- Define los planes disponibles para suscripción SaaS
CREATE TABLE IF NOT EXISTS plan_suscripcion (
  id                    BIGSERIAL PRIMARY KEY,
  codigo                VARCHAR(50) UNIQUE NOT NULL,
  nombre                VARCHAR(120) NOT NULL,
  descripcion           TEXT,
  tipo                  drinkgo.subscription_type NOT NULL,
  precio_mensual        NUMERIC(12,2) NOT NULL CHECK (precio_mensual >= 0),
  precio_anual          NUMERIC(12,2) CHECK (precio_anual IS NULL OR precio_anual >= 0),
  max_sedes             INT NOT NULL DEFAULT 1 CHECK (max_sedes > 0),
  max_usuarios          INT NOT NULL DEFAULT 5 CHECK (max_usuarios > 0),
  max_productos         INT DEFAULT NULL,  -- NULL = ilimitado
  tiene_mesas           BOOLEAN NOT NULL DEFAULT FALSE,
  tiene_delivery        BOOLEAN NOT NULL DEFAULT TRUE,
  tiene_storefront      BOOLEAN NOT NULL DEFAULT FALSE,
  tiene_reportes_avanzados BOOLEAN NOT NULL DEFAULT FALSE,
  dias_prueba           INT NOT NULL DEFAULT 14 CHECK (dias_prueba >= 0),
  activo                BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER plan_suscripcion_set_updated_at
BEFORE UPDATE ON plan_suscripcion FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE plan_suscripcion IS 'Planes de suscripción SaaS disponibles';
COMMENT ON COLUMN plan_suscripcion.max_sedes IS 'Máximo de sucursales permitidas en el plan';
COMMENT ON COLUMN plan_suscripcion.tiene_mesas IS 'Habilita módulo de mesas/consumo en local';

-- Insertar planes por defecto
INSERT INTO plan_suscripcion (codigo, nombre, tipo, precio_mensual, precio_anual, max_sedes, max_usuarios, tiene_mesas, tiene_storefront, dias_prueba) VALUES
('basico', 'Plan Básico', 'monthly', 29.00, 290.00, 1, 2, FALSE, FALSE, 14),
('starter', 'Plan Starter', 'monthly', 49.00, 490.00, 1, 3, FALSE, FALSE, 14),
('professional', 'Plan Profesional', 'monthly', 149.00, 1490.00, 3, 10, TRUE, TRUE, 14),
('enterprise', 'Plan Enterprise', 'enterprise', 399.00, 3990.00, 10, 50, TRUE, TRUE, 30);

-- =============================================================================
-- 2. TABLA: superadmin
-- =============================================================================
-- Administradores de la plataforma SaaS (nivel más alto)
CREATE TABLE IF NOT EXISTS superadmin (
  id                BIGSERIAL PRIMARY KEY,
  uuid              UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
  nombres           VARCHAR(120) NOT NULL,
  apellidos         VARCHAR(120),
  email             CITEXT UNIQUE NOT NULL,
  contrasena_hash   VARCHAR(200) NOT NULL CHECK (LENGTH(contrasena_hash) >= 60),
  activo            BOOLEAN NOT NULL DEFAULT TRUE,
  ultimo_acceso     TIMESTAMPTZ,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TRIGGER superadmin_set_updated_at
BEFORE UPDATE ON superadmin FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE superadmin IS 'Administradores de la plataforma DrinkGo (gestionan suscripciones)';

-- =============================================================================
-- 3. TABLA: negocio (TENANT PRINCIPAL)
-- =============================================================================
-- Cada negocio es un tenant aislado (licorería)
CREATE TABLE IF NOT EXISTS negocio (
  id                    BIGSERIAL PRIMARY KEY,
  uuid                  UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
  ruc                   VARCHAR(20),
  razon_social          VARCHAR(250) NOT NULL,
  nombre_comercial      VARCHAR(200) NOT NULL,
  email_contacto        CITEXT NOT NULL,
  telefono              VARCHAR(20) CHECK (telefono ~ '^[0-9 +()-]{6,20}$' OR telefono IS NULL),
  direccion_fiscal      VARCHAR(300),
  logo_url              VARCHAR(500),
  sitio_web             VARCHAR(250),
  
  -- Configuración del negocio
  moneda                CHAR(3) NOT NULL DEFAULT 'PEN',
  zona_horaria          VARCHAR(60) NOT NULL DEFAULT 'America/Lima',
  has_tables            BOOLEAN NOT NULL DEFAULT FALSE,  -- Flag: habilita mesas
  has_delivery          BOOLEAN NOT NULL DEFAULT TRUE,
  has_storefront        BOOLEAN NOT NULL DEFAULT FALSE,
  
  -- Configuración tributaria (IGV)
  -- El superadmin define si el negocio cobra IGV o está exonerado (ej: Amazonía)
  aplica_igv            BOOLEAN NOT NULL DEFAULT TRUE,
  porcentaje_igv        NUMERIC(5,2) NOT NULL DEFAULT 18.00 CHECK (porcentaje_igv >= 0 AND porcentaje_igv <= 100),
  
  -- Suscripción
  plan_id               BIGINT NOT NULL REFERENCES plan_suscripcion(id) ON DELETE RESTRICT,
  suscripcion_estado    drinkgo.subscription_status NOT NULL DEFAULT 'pending_payment',
  suscripcion_inicio    DATE,
  suscripcion_fin       DATE,
  dias_gracia           INT NOT NULL DEFAULT 7,
  
  -- Auditoría
  creado_por_superadmin BIGINT REFERENCES superadmin(id) ON DELETE SET NULL,
  activo                BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_negocio_uuid ON negocio(uuid);
CREATE INDEX idx_negocio_ruc ON negocio(ruc) WHERE ruc IS NOT NULL;
CREATE INDEX idx_negocio_estado_suscripcion ON negocio(suscripcion_estado);
CREATE INDEX idx_negocio_fin_suscripcion ON negocio(suscripcion_fin) WHERE suscripcion_fin IS NOT NULL;

CREATE TRIGGER negocio_set_updated_at
BEFORE UPDATE ON negocio FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE negocio IS 'Tenant principal: cada registro es una licorería cliente del SaaS';
COMMENT ON COLUMN negocio.has_tables IS 'Si TRUE, habilita módulo de mesas y rol mesero';
COMMENT ON COLUMN negocio.uuid IS 'Identificador público del tenant (no exponer ID interno)';
COMMENT ON COLUMN negocio.aplica_igv IS 'TRUE=cobra IGV 18%, FALSE=exonerado (zonas de Amazonía)';
COMMENT ON COLUMN negocio.porcentaje_igv IS 'Porcentaje de IGV (18% estándar, 0% si exonerado)';

-- =============================================================================
-- 4. TABLA: historial_suscripcion
-- =============================================================================
-- Registro histórico de cambios de suscripción
CREATE TABLE IF NOT EXISTS historial_suscripcion (
  id                BIGSERIAL PRIMARY KEY,
  negocio_id        BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  plan_anterior_id  BIGINT REFERENCES plan_suscripcion(id),
  plan_nuevo_id     BIGINT NOT NULL REFERENCES plan_suscripcion(id),
  estado_anterior   drinkgo.subscription_status,
  estado_nuevo      drinkgo.subscription_status NOT NULL,
  fecha_cambio      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  motivo            VARCHAR(250),
  procesado_por     BIGINT REFERENCES superadmin(id),
  monto_pagado      NUMERIC(12,2),
  metodo_pago       VARCHAR(50),
  referencia_pago   VARCHAR(120)
);

CREATE INDEX idx_historial_suscripcion_negocio ON historial_suscripcion(negocio_id);
CREATE INDEX idx_historial_suscripcion_fecha ON historial_suscripcion(fecha_cambio);

COMMENT ON TABLE historial_suscripcion IS 'Auditoría de cambios en suscripciones de negocios';

-- =============================================================================
-- 5. TABLA: factura_suscripcion
-- =============================================================================
-- Facturas generadas por suscripciones
CREATE TABLE IF NOT EXISTS factura_suscripcion (
  id                BIGSERIAL PRIMARY KEY,
  negocio_id        BIGINT NOT NULL REFERENCES negocio(id) ON DELETE RESTRICT,
  numero            VARCHAR(30) UNIQUE NOT NULL,
  periodo_inicio    DATE NOT NULL,
  periodo_fin       DATE NOT NULL,
  plan_id           BIGINT NOT NULL REFERENCES plan_suscripcion(id),
  subtotal          NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
  impuesto          NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (impuesto >= 0),
  total             NUMERIC(12,2) NOT NULL CHECK (total >= 0),
  estado            VARCHAR(20) NOT NULL DEFAULT 'pendiente'
                     CHECK (estado IN ('pendiente', 'pagada', 'vencida', 'anulada')),
  fecha_emision     DATE NOT NULL DEFAULT CURRENT_DATE,
  fecha_vencimiento DATE NOT NULL,
  fecha_pago        DATE,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_factura_suscripcion_negocio ON factura_suscripcion(negocio_id);
CREATE INDEX idx_factura_suscripcion_estado ON factura_suscripcion(estado, fecha_vencimiento);

COMMENT ON TABLE factura_suscripcion IS 'Facturas de cobro por suscripción SaaS';
