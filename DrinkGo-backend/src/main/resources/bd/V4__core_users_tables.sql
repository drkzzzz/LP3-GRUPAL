-- =============================================================================
-- BD_DRINKGO - V4: Tablas Core - Sedes, Usuarios y Permisos (Multi-tenant)
-- Fecha: 2026-01-26
-- Descripción: Sedes, roles, permisos, usuarios con aislamiento por tenant
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: sede
-- =============================================================================
-- Sucursales de cada negocio (tenant)
CREATE TABLE IF NOT EXISTS sede (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  codigo          VARCHAR(20) NOT NULL,
  nombre          VARCHAR(120) NOT NULL,
  direccion       VARCHAR(250),
  distrito        VARCHAR(80),
  ciudad          VARCHAR(80),
  telefono        VARCHAR(20) CHECK (telefono ~ '^[0-9 +()-]{6,20}$' OR telefono IS NULL),
  email           CITEXT,
  coordenadas_lat NUMERIC(10,7),
  coordenadas_lng NUMERIC(10,7),
  
  -- Capacidades de la sede
  has_tables      BOOLEAN NOT NULL DEFAULT FALSE,  -- Hereda o override del negocio
  has_delivery    BOOLEAN NOT NULL DEFAULT TRUE,
  has_pickup      BOOLEAN NOT NULL DEFAULT TRUE,
  capacidad_mesas INT DEFAULT 0,
  
  -- Almacén principal de la sede (se actualiza después de crear almacén)
  almacen_principal_id BIGINT,  -- FK se agrega después de crear tabla almacen
  
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, codigo)
);

CREATE INDEX idx_sede_tenant ON sede(tenant_id);
CREATE INDEX idx_sede_activo ON sede(tenant_id, activo) WHERE activo = TRUE;

CREATE TRIGGER sede_set_updated_at
BEFORE UPDATE ON sede FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

-- Trigger de aislamiento de tenant
CREATE TRIGGER sede_check_tenant
BEFORE INSERT OR UPDATE ON sede FOR EACH ROW
EXECUTE FUNCTION drinkgo.check_tenant_isolation();

COMMENT ON TABLE sede IS 'Sucursales de cada licorería (aisladas por tenant_id)';
COMMENT ON COLUMN sede.has_tables IS 'Override local: puede tener mesas aunque el negocio no lo tenga global';

-- =============================================================================
-- 2. TABLA: almacen
-- =============================================================================
-- Almacenes por sede (una sede puede tener varios almacenes)
CREATE TABLE IF NOT EXISTS almacen (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id         BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  
  codigo          VARCHAR(20) NOT NULL,
  nombre          VARCHAR(100) NOT NULL,
  descripcion     VARCHAR(250),
  
  -- Tipo de almacén
  tipo            VARCHAR(30) NOT NULL DEFAULT 'general'
                   CHECK (tipo IN ('general', 'frio', 'exhibicion', 'transito')),
  
  -- Capacidad (opcional)
  capacidad_unidades INT,
  
  es_principal    BOOLEAN NOT NULL DEFAULT FALSE,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(sede_id, codigo)
);

-- Solo un almacén principal por sede
CREATE UNIQUE INDEX unq_almacen_principal_sede
ON almacen(sede_id) WHERE es_principal = TRUE AND activo = TRUE;

CREATE INDEX idx_almacen_tenant ON almacen(tenant_id);
CREATE INDEX idx_almacen_sede ON almacen(sede_id);

CREATE TRIGGER almacen_set_updated_at
BEFORE UPDATE ON almacen FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE almacen IS 'Almacenes por sede (general, frío, exhibición)';
COMMENT ON COLUMN almacen.tipo IS 'general=temperatura ambiente, frio=refrigerado, exhibicion=mostrador, transito=temporal';

-- Agregar FK de almacen_principal_id a sede (ahora que almacen existe)
ALTER TABLE sede ADD CONSTRAINT fk_sede_almacen_principal 
  FOREIGN KEY (almacen_principal_id) REFERENCES almacen(id) ON DELETE SET NULL;

CREATE INDEX idx_sede_almacen_principal ON sede(almacen_principal_id) WHERE almacen_principal_id IS NOT NULL;

-- =============================================================================
-- 3. TABLA: sede_config
-- =============================================================================
-- Configuración específica de cada sede (horarios, restricciones)
CREATE TABLE IF NOT EXISTS sede_config (
  sede_id                     BIGINT PRIMARY KEY REFERENCES sede(id) ON DELETE CASCADE,
  tenant_id                   BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  -- Horarios de operación
  hora_apertura               TIME DEFAULT '09:00',
  hora_cierre                 TIME DEFAULT '23:00',
  
  -- Restricciones de venta de alcohol
  hora_inicio_venta_alcohol   TIME,  -- NULL = sin restricción
  hora_fin_venta_alcohol      TIME,
  dias_sin_venta_alcohol      INT[],  -- Array de días (0=Dom, 1=Lun, etc.)
  ley_seca_activa             BOOLEAN NOT NULL DEFAULT FALSE,
  
  -- Configuración de delivery
  delivery_radio_km           NUMERIC(5,2),
  delivery_costo_base         NUMERIC(12,2) DEFAULT 0,
  delivery_costo_por_km       NUMERIC(12,2) DEFAULT 0,
  delivery_pedido_minimo      NUMERIC(12,2) DEFAULT 0,
  
  -- Impuesto se toma de negocio.aplica_igv y negocio.porcentaje_igv
  
  actualizado_en              TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sede_config_tenant ON sede_config(tenant_id);

CREATE TRIGGER sede_config_set_updated_at
BEFORE UPDATE ON sede_config FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE sede_config IS 'Configuración operativa y restricciones legales por sede';
COMMENT ON COLUMN sede_config.hora_inicio_venta_alcohol IS 'Restricción horaria local de venta de alcohol';
COMMENT ON COLUMN sede_config.ley_seca_activa IS 'Emergencia: suspende toda venta de alcohol';

-- =============================================================================
-- 3. TABLA: rol
-- =============================================================================
-- Roles de usuario (globales del sistema + personalizados por tenant)
CREATE TABLE IF NOT EXISTS rol (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT REFERENCES negocio(id) ON DELETE CASCADE,  -- NULL = rol global
  codigo          VARCHAR(60) NOT NULL,
  nombre          VARCHAR(120) NOT NULL,
  descripcion     VARCHAR(250),
  es_sistema      BOOLEAN NOT NULL DEFAULT FALSE,  -- Roles del sistema no editables
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, codigo)
);

CREATE INDEX idx_rol_tenant ON rol(tenant_id);
CREATE INDEX idx_rol_sistema ON rol(es_sistema) WHERE es_sistema = TRUE;

CREATE TRIGGER rol_set_updated_at
BEFORE UPDATE ON rol FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE rol IS 'Roles: globales (tenant_id NULL) o específicos de tenant';
COMMENT ON COLUMN rol.es_sistema IS 'TRUE = rol del sistema, no modificable por el tenant';

-- Insertar roles globales del sistema
INSERT INTO rol (tenant_id, codigo, nombre, descripcion, es_sistema) VALUES
(NULL, 'superadmin', 'Super Administrador', 'Administrador de la plataforma SaaS', TRUE),
(NULL, 'admin', 'Administrador', 'Dueño/administrador del negocio', TRUE),
(NULL, 'gerente', 'Gerente de Sede', 'Gerente de una sede específica', TRUE),
(NULL, 'cajero', 'Cajero', 'Operador de caja/POS', TRUE),
(NULL, 'mesero', 'Mesero', 'Atención en mesas (solo si has_tables=TRUE)', TRUE),
(NULL, 'almacenero', 'Almacenero', 'Gestión de inventario y lotes', TRUE),
(NULL, 'repartidor', 'Repartidor', 'Entregas de delivery', TRUE);

-- =============================================================================
-- 4. TABLA: permiso (estructura base)
-- =============================================================================
-- NOTA: Los permisos completos se insertan en V17__security_complete_module.sql
-- Aquí solo se crea la estructura mínima para que las FKs funcionen
CREATE TABLE IF NOT EXISTS permiso (
  id              BIGSERIAL PRIMARY KEY,
  codigo          VARCHAR(80) UNIQUE NOT NULL,
  nombre          VARCHAR(80),
  descripcion     VARCHAR(250),
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE permiso IS 'Permisos del sistema - datos completos en V17';

-- =============================================================================
-- 5. TABLA: rol_permiso
-- =============================================================================
-- Asignación de permisos a roles
CREATE TABLE IF NOT EXISTS rol_permiso (
  rol_id      BIGINT NOT NULL REFERENCES rol(id) ON DELETE CASCADE,
  permiso_id  BIGINT NOT NULL REFERENCES permiso(id) ON DELETE CASCADE,
  PRIMARY KEY (rol_id, permiso_id)
);

COMMENT ON TABLE rol_permiso IS 'Asignación de permisos a roles';

-- =============================================================================
-- 6. TABLA: usuario
-- =============================================================================
-- Usuarios del sistema (empleados de cada negocio)
CREATE TABLE IF NOT EXISTS usuario (
  id                BIGSERIAL PRIMARY KEY,
  tenant_id         BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  uuid              UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
  codigo_empleado   VARCHAR(30),
  nombres           VARCHAR(120) NOT NULL,
  apellidos         VARCHAR(120),
  email             CITEXT NOT NULL,
  telefono          VARCHAR(20) CHECK (telefono ~ '^[0-9 +()-]{6,20}$' OR telefono IS NULL),
  contrasena_hash   VARCHAR(200) NOT NULL CHECK (LENGTH(contrasena_hash) >= 60),
  avatar_url        VARCHAR(500),
  sede_preferida_id BIGINT REFERENCES sede(id) ON DELETE SET NULL,
  pin_rapido        VARCHAR(10),  -- PIN para acceso rápido en POS
  activo            BOOLEAN NOT NULL DEFAULT TRUE,
  ultimo_acceso     TIMESTAMPTZ,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, email)
);

CREATE INDEX idx_usuario_tenant ON usuario(tenant_id);
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_uuid ON usuario(uuid);
CREATE INDEX idx_usuario_activo ON usuario(tenant_id, activo) WHERE activo = TRUE;

CREATE TRIGGER usuario_set_updated_at
BEFORE UPDATE ON usuario FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

CREATE TRIGGER usuario_check_tenant
BEFORE INSERT OR UPDATE ON usuario FOR EACH ROW
EXECUTE FUNCTION drinkgo.check_tenant_isolation();

COMMENT ON TABLE usuario IS 'Usuarios/empleados de cada licorería (aislados por tenant)';
COMMENT ON COLUMN usuario.pin_rapido IS 'PIN de 4-6 dígitos para login rápido en POS';

-- =============================================================================
-- 7. TABLA: usuario_rol
-- =============================================================================
-- Asignación de roles a usuarios
CREATE TABLE IF NOT EXISTS usuario_rol (
  usuario_id  BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  rol_id      BIGINT NOT NULL REFERENCES rol(id) ON DELETE CASCADE,
  PRIMARY KEY (usuario_id, rol_id)
);

COMMENT ON TABLE usuario_rol IS 'Roles asignados a cada usuario';

-- =============================================================================
-- 8. TABLA: usuario_sede
-- =============================================================================
-- Sedes a las que tiene acceso cada usuario
CREATE TABLE IF NOT EXISTS usuario_sede (
  usuario_id  BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  sede_id     BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  es_principal BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (usuario_id, sede_id)
);

CREATE INDEX idx_usuario_sede_sede ON usuario_sede(sede_id);

COMMENT ON TABLE usuario_sede IS 'Sedes donde el usuario puede operar';

-- =============================================================================
-- 9. TABLA: sesion_usuario
-- =============================================================================
-- Tokens de sesión para autenticación
CREATE TABLE IF NOT EXISTS sesion_usuario (
  id              BIGSERIAL PRIMARY KEY,
  usuario_id      BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  token           VARCHAR(500) UNIQUE NOT NULL,
  ip_address      INET,
  user_agent      VARCHAR(500),
  expira_en       TIMESTAMPTZ NOT NULL,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sesion_usuario_token ON sesion_usuario(token) WHERE activo = TRUE;
CREATE INDEX idx_sesion_usuario_expira ON sesion_usuario(expira_en) WHERE activo = TRUE;

COMMENT ON TABLE sesion_usuario IS 'Sesiones activas de usuarios';

-- =============================================================================
-- 10. TABLA: usuario_recuperacion
-- =============================================================================
-- Tokens para recuperación de contraseña
CREATE TABLE IF NOT EXISTS usuario_recuperacion (
  id          BIGSERIAL PRIMARY KEY,
  usuario_id  BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  token       VARCHAR(120) UNIQUE NOT NULL,
  expira_en   TIMESTAMPTZ NOT NULL,
  usado       BOOLEAN NOT NULL DEFAULT FALSE,
  creado_en   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuario_recuperacion_token 
ON usuario_recuperacion(token) WHERE usado = FALSE;

COMMENT ON TABLE usuario_recuperacion IS 'Tokens para recuperación de contraseña';
