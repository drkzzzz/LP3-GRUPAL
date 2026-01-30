-- =============================================================================
-- BD_DRINKGO - V18: Módulo de Configuración Completo
-- Fecha: 2026-01-26
-- Descripción: Configuración general del negocio, parámetros, preferencias
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. Agregar columnas al negocio si no existen
-- =============================================================================
DO $$
BEGIN
  -- Datos fiscales
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'tipo_documento') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN tipo_documento VARCHAR(2) DEFAULT '6';  -- RUC
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'numero_documento') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN numero_documento VARCHAR(20);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'nombre_comercial') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN nombre_comercial VARCHAR(250);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'direccion_fiscal') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN direccion_fiscal VARCHAR(300);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'distrito') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN distrito VARCHAR(100);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'provincia') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN provincia VARCHAR(100);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'departamento') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN departamento VARCHAR(100);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'ubigeo') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN ubigeo VARCHAR(6);
  END IF;
  
  -- Logo y branding
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'logo_url') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN logo_url VARCHAR(500);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'favicon_url') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN favicon_url VARCHAR(500);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'color_primario') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN color_primario VARCHAR(7) DEFAULT '#1976D2';
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'color_secundario') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN color_secundario VARCHAR(7) DEFAULT '#424242';
  END IF;
  
  -- Contacto
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'telefono') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN telefono VARCHAR(20);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'email') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN email citext;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'website') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN website VARCHAR(250);
  END IF;
  
  -- Redes sociales
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'facebook_url') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN facebook_url VARCHAR(250);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'instagram_url') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN instagram_url VARCHAR(250);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'tiktok_url') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN tiktok_url VARCHAR(250);
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'whatsapp') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN whatsapp VARCHAR(20);
  END IF;
  
  -- Configuración regional
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'zona_horaria') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN zona_horaria VARCHAR(50) DEFAULT 'America/Lima';
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'moneda_principal') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN moneda_principal CHAR(3) DEFAULT 'PEN';
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'idioma') THEN
    ALTER TABLE drinkgo.negocio ADD COLUMN idioma VARCHAR(5) DEFAULT 'es';
  END IF;
END $$;

-- OBLIGATORIO: constraints de datos fiscales/branding (evita datos inválidos y duplicidad de tenants por RUC).
-- Escenario: SuperAdmin registra 2 veces el mismo RUC por error; la BD debe impedirlo.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'numero_documento'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'uq_negocio_numero_documento'
  ) THEN
    ALTER TABLE drinkgo.negocio
    ADD CONSTRAINT uq_negocio_numero_documento UNIQUE (numero_documento);
  END IF;

  -- OPCIONAL: validar formato de ubigeo.
  -- Escenario: reportes y facturación requieren ubigeo consistente.
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'ubigeo'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'chk_negocio_ubigeo'
  ) THEN
    ALTER TABLE drinkgo.negocio
    ADD CONSTRAINT chk_negocio_ubigeo
    CHECK (ubigeo IS NULL OR length(ubigeo) = 6);
  END IF;

  -- OPCIONAL: validar colores hex.
  -- Escenario: cada licorería personaliza su branding sin guardar valores inválidos.
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'color_primario'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'chk_negocio_color_primario'
  ) THEN
    ALTER TABLE drinkgo.negocio
    ADD CONSTRAINT chk_negocio_color_primario
    CHECK (color_primario ~ '^#[0-9A-Fa-f]{6}$');
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'negocio' AND column_name = 'color_secundario'
  ) AND NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'chk_negocio_color_secundario'
  ) THEN
    ALTER TABLE drinkgo.negocio
    ADD CONSTRAINT chk_negocio_color_secundario
    CHECK (color_secundario ~ '^#[0-9A-Fa-f]{6}$');
  END IF;
END $$;

-- =============================================================================
-- 2. TABLA: configuracion_negocio
-- =============================================================================
-- Parámetros de configuración clave-valor
CREATE TABLE IF NOT EXISTS configuracion_negocio (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  categoria       VARCHAR(50) NOT NULL,  -- 'ventas', 'inventario', 'facturacion', etc.
  clave           VARCHAR(100) NOT NULL,
  valor           TEXT,
  tipo_dato       VARCHAR(20) NOT NULL DEFAULT 'string'
                   CHECK (tipo_dato IN ('string', 'number', 'boolean', 'json', 'date')),
  
  descripcion     VARCHAR(250),
  es_sensible     BOOLEAN NOT NULL DEFAULT FALSE,  -- Si es contraseña o dato sensible
  
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_por_id BIGINT REFERENCES usuario(id),
  
  UNIQUE(tenant_id, categoria, clave)
);

CREATE INDEX idx_config_tenant ON configuracion_negocio(tenant_id);
CREATE INDEX idx_config_categoria ON configuracion_negocio(categoria);

COMMENT ON TABLE configuracion_negocio IS 'Configuración flexible clave-valor por negocio';

-- =============================================================================
-- 3. FUNCIÓN: Obtener configuración
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.obtener_config(
  p_tenant_id BIGINT,
  p_categoria VARCHAR(50),
  p_clave VARCHAR(100),
  p_default TEXT DEFAULT NULL
)
RETURNS TEXT LANGUAGE plpgsql AS $$
DECLARE
  v_valor TEXT;
BEGIN
  SELECT valor INTO v_valor
  FROM drinkgo.configuracion_negocio
  WHERE tenant_id = p_tenant_id
    AND categoria = p_categoria
    AND clave = p_clave;
  
  RETURN COALESCE(v_valor, p_default);
END $$;

-- OBLIGATORIO: wrapper para compatibilidad con firma anterior (sin categoria).
-- Escenario: código legado busca por clave; si existe en más de una categoría, debe forzar a especificar.
CREATE OR REPLACE FUNCTION drinkgo.obtener_config(
  p_tenant_id BIGINT,
  p_clave VARCHAR(100),
  p_default TEXT DEFAULT NULL
)
RETURNS TEXT LANGUAGE plpgsql AS $$
DECLARE
  v_categorias INT;
  v_categoria VARCHAR(50);
BEGIN
  SELECT COUNT(DISTINCT categoria) INTO v_categorias
  FROM drinkgo.configuracion_negocio
  WHERE tenant_id = p_tenant_id
    AND clave = p_clave;

  IF v_categorias = 0 THEN
    RETURN p_default;
  ELSIF v_categorias = 1 THEN
    SELECT categoria INTO v_categoria
    FROM drinkgo.configuracion_negocio
    WHERE tenant_id = p_tenant_id
      AND clave = p_clave
    LIMIT 1;

    RETURN drinkgo.obtener_config(p_tenant_id, v_categoria, p_clave, p_default);
  ELSE
    RAISE EXCEPTION 'Clave % existe en múltiples categorías para tenant_id=%. Use obtener_config(p_tenant_id, p_categoria, p_clave).', p_clave, p_tenant_id;
  END IF;
END $$;

-- =============================================================================
-- 4. FUNCIÓN: Establecer configuración
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.establecer_config(
  p_tenant_id BIGINT,
  p_categoria VARCHAR(50),
  p_clave VARCHAR(100),
  p_valor TEXT,
  p_tipo_dato VARCHAR(20) DEFAULT 'string',
  p_usuario_id BIGINT DEFAULT NULL
)
RETURNS void LANGUAGE plpgsql AS $$
BEGIN
  INSERT INTO drinkgo.configuracion_negocio (tenant_id, categoria, clave, valor, tipo_dato, actualizado_por_id)
  VALUES (p_tenant_id, p_categoria, p_clave, p_valor, p_tipo_dato, p_usuario_id)
  ON CONFLICT (tenant_id, categoria, clave) 
  DO UPDATE SET 
    valor = EXCLUDED.valor,
    actualizado_en = NOW(),
    actualizado_por_id = EXCLUDED.actualizado_por_id;
END $$;

-- =============================================================================
-- 5. TABLA: configuracion_sede
-- =============================================================================
-- Configuración específica por sede
CREATE TABLE IF NOT EXISTS configuracion_sede (
  id              BIGSERIAL PRIMARY KEY,
  sede_id         BIGINT NOT NULL REFERENCES sede(id) ON DELETE CASCADE,
  tenant_id       BIGINT,
  
  -- Horarios
  hora_apertura   TIME NOT NULL DEFAULT '09:00',
  hora_cierre     TIME NOT NULL DEFAULT '22:00',
  dias_operacion  VARCHAR(20)[] NOT NULL DEFAULT ARRAY['lun','mar','mie','jue','vie','sab','dom'],
  
  -- Restricción de venta de alcohol (horario legal)
  hora_inicio_venta_alcohol TIME NOT NULL DEFAULT '08:00',
  hora_fin_venta_alcohol TIME NOT NULL DEFAULT '23:00',
  dias_sin_venta_alcohol VARCHAR(20)[] DEFAULT ARRAY[]::VARCHAR[],  -- días sin venta
  
  -- Delivery
  permite_delivery BOOLEAN NOT NULL DEFAULT TRUE,
  radio_delivery_km NUMERIC(5,2) DEFAULT 5.0,
  costo_delivery_base NUMERIC(10,2) DEFAULT 0,
  costo_delivery_por_km NUMERIC(10,2) DEFAULT 0,
  tiempo_estimado_min INT DEFAULT 30,
  pedido_minimo_delivery NUMERIC(10,2) DEFAULT 0,
  
  -- Pickup
  permite_pickup BOOLEAN NOT NULL DEFAULT TRUE,
  tiempo_preparacion_min INT DEFAULT 15,
  
  -- Mesas
  permite_mesas BOOLEAN NOT NULL DEFAULT FALSE,
  cobro_servicio_porcentaje NUMERIC(5,2) DEFAULT 0,
  
  -- Impresión
  impresora_tickets VARCHAR(100),
  impresora_cocina VARCHAR(100),
  formato_ticket VARCHAR(20) DEFAULT '80mm',  -- 58mm, 80mm
  
  -- Facturación
  genera_factura_electronica BOOLEAN NOT NULL DEFAULT TRUE,
  
  actualizado_en TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- OBLIGATORIO: tenant_id y FK compuesta para impedir cruces entre licorerías.
-- Escenario: Admin modifica horarios de una sede; esa sede siempre pertenece a su negocio.
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'configuracion_sede' AND column_name = 'tenant_id'
  ) THEN
    ALTER TABLE drinkgo.configuracion_sede ADD COLUMN tenant_id BIGINT;
  END IF;

  UPDATE drinkgo.configuracion_sede cs
  SET tenant_id = s.tenant_id
  FROM drinkgo.sede s
  WHERE cs.tenant_id IS NULL
    AND s.id = cs.sede_id;

  IF NOT EXISTS (SELECT 1 FROM drinkgo.configuracion_sede WHERE tenant_id IS NULL) THEN
    ALTER TABLE drinkgo.configuracion_sede ALTER COLUMN tenant_id SET NOT NULL;
  END IF;

  -- OPCIONAL: una configuración por sede.
  -- Escenario: evitar duplicar configuraciones para la misma sede.
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_configuracion_sede_sede_id') THEN
    ALTER TABLE drinkgo.configuracion_sede
    ADD CONSTRAINT uq_configuracion_sede_sede_id UNIQUE (sede_id);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_configuracion_sede_tenant_sede') THEN
    ALTER TABLE drinkgo.configuracion_sede
    ADD CONSTRAINT fk_configuracion_sede_tenant_sede
    FOREIGN KEY (tenant_id, sede_id)
    REFERENCES drinkgo.sede(tenant_id, id)
    ON DELETE CASCADE;
  END IF;
END $$;

CREATE INDEX idx_config_sede ON configuracion_sede(sede_id);

-- OBLIGATORIO: índice por tenant para queries del Admin.
-- Escenario: listar configuración de sedes por negocio sin escanear toda la tabla.
CREATE INDEX IF NOT EXISTS idx_config_sede_tenant ON drinkgo.configuracion_sede(tenant_id, sede_id);

CREATE TRIGGER configuracion_sede_set_updated_at
BEFORE UPDATE ON configuracion_sede FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE configuracion_sede IS 'Configuración específica de cada sede';

-- =============================================================================
-- 6. TABLA: configuracion_impuestos
-- =============================================================================
-- Configuración de impuestos
CREATE TABLE IF NOT EXISTS configuracion_impuestos (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  codigo          VARCHAR(10) NOT NULL,  -- 'IGV', 'ISC', etc.
  nombre          VARCHAR(50) NOT NULL,
  porcentaje      NUMERIC(5,2) NOT NULL,
  
  aplica_a        VARCHAR(30) NOT NULL DEFAULT 'todos'
                   CHECK (aplica_a IN ('todos', 'productos', 'servicios', 'licores')),
  
  codigo_sunat    VARCHAR(10),  -- Código SUNAT si aplica
  afecta_precio   BOOLEAN NOT NULL DEFAULT TRUE,  -- Si está incluido en el precio
  
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  orden           INT NOT NULL DEFAULT 0,
  
  UNIQUE(tenant_id, codigo)
);

-- OBLIGATORIO: validar rango de porcentaje.
-- Escenario: Admin configura IGV/ISC y no debe poder guardar 180% por error.
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_config_impuestos_porcentaje') THEN
    ALTER TABLE drinkgo.configuracion_impuestos
    ADD CONSTRAINT chk_config_impuestos_porcentaje
    CHECK (porcentaje >= 0 AND porcentaje <= 100);
  END IF;
END $$;

-- OBLIGATORIO: índice por tenant/activo para cálculos de venta.
-- Escenario: POS calcula impuestos activos de la licorería en cada venta.
CREATE INDEX IF NOT EXISTS idx_config_impuestos_tenant_activo ON drinkgo.configuracion_impuestos(tenant_id, activo);

-- IGV por defecto
INSERT INTO configuracion_impuestos (tenant_id, codigo, nombre, porcentaje, codigo_sunat, orden)
SELECT n.id, 'IGV', 'IGV', 18.00, '1000', 1
FROM drinkgo.negocio n
WHERE NOT EXISTS (
  SELECT 1 FROM drinkgo.configuracion_impuestos ci 
  WHERE ci.tenant_id = n.id AND ci.codigo = 'IGV'
);

COMMENT ON TABLE configuracion_impuestos IS 'Configuración de impuestos por negocio';

-- =============================================================================
-- 7. TABLA: configuracion_notificaciones
-- =============================================================================
-- Configuración de notificaciones
CREATE TABLE IF NOT EXISTS configuracion_notificaciones (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  evento          VARCHAR(50) NOT NULL,  -- 'pedido_nuevo', 'stock_bajo', etc.
  canal           VARCHAR(20) NOT NULL CHECK (canal IN ('email', 'push', 'whatsapp', 'sms')),
  
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  destinatarios   TEXT[],  -- Emails o números
  plantilla_id    BIGINT,  -- Referencia a plantilla si existe
  
  UNIQUE(tenant_id, evento, canal)
);

-- OBLIGATORIO: evitar que una notificación apunte a una plantilla de otra licorería.
-- Escenario: Licorería A usa plantilla de WhatsApp distinta a Licorería B.
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_plantilla_documento_tenant_id_id') THEN
    ALTER TABLE drinkgo.plantilla_documento
    ADD CONSTRAINT uq_plantilla_documento_tenant_id_id UNIQUE (tenant_id, id);
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_config_notif_tenant_plantilla') THEN
    ALTER TABLE drinkgo.configuracion_notificaciones
    ADD CONSTRAINT fk_config_notif_tenant_plantilla
    FOREIGN KEY (tenant_id, plantilla_id)
    REFERENCES drinkgo.plantilla_documento(tenant_id, id)
    ON DELETE SET NULL;
  END IF;
END $$;

-- Eventos de notificación predefinidos
INSERT INTO configuracion_notificaciones (tenant_id, evento, canal, activo) 
SELECT n.id, evento, 'push', TRUE
FROM drinkgo.negocio n
CROSS JOIN (VALUES 
  ('pedido_nuevo'),
  ('pedido_cancelado'),
  ('stock_bajo'),
  ('vencimiento_proximo'),
  ('pago_recibido'),
  ('cuenta_vencida'),
  ('nueva_resena')
) AS eventos(evento)
ON CONFLICT DO NOTHING;

COMMENT ON TABLE configuracion_notificaciones IS 'Configuración de notificaciones por evento';

-- =============================================================================
-- 8. TABLA: plantilla_documento
-- =============================================================================
-- Plantillas para tickets, comprobantes, emails
CREATE TABLE IF NOT EXISTS plantilla_documento (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  tipo            VARCHAR(30) NOT NULL CHECK (tipo IN 
                   ('ticket', 'factura', 'boleta', 'nota_credito', 'guia', 
                    'email_pedido', 'email_bienvenida', 'whatsapp')),
  nombre          VARCHAR(100) NOT NULL,
  contenido       TEXT NOT NULL,  -- HTML/template
  
  -- Configuración
  ancho_mm        INT,  -- Para tickets
  muestra_logo    BOOLEAN DEFAULT TRUE,
  pie_pagina      TEXT,
  
  es_default      BOOLEAN NOT NULL DEFAULT FALSE,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- OBLIGATORIO: solo una plantilla default por tipo y licorería.
-- Escenario: al emitir boleta, el sistema no debe tener ambigüedad de plantilla default.
CREATE UNIQUE INDEX IF NOT EXISTS ux_plantilla_default_por_tipo
ON drinkgo.plantilla_documento(tenant_id, tipo)
WHERE es_default = TRUE;

-- OPCIONAL: trazabilidad de quién modifica plantillas.
-- Escenario: SuperAdmin audita cambios de formato de ticket realizados por Admin.
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'plantilla_documento' AND column_name = 'creado_por_id'
  ) THEN
    ALTER TABLE drinkgo.plantilla_documento ADD COLUMN creado_por_id BIGINT REFERENCES usuario(id);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'plantilla_documento' AND column_name = 'actualizado_por_id'
  ) THEN
    ALTER TABLE drinkgo.plantilla_documento ADD COLUMN actualizado_por_id BIGINT REFERENCES usuario(id);
  END IF;
END $$;

CREATE INDEX idx_plantilla_tenant ON plantilla_documento(tenant_id);
CREATE INDEX idx_plantilla_tipo ON plantilla_documento(tipo);

CREATE TRIGGER plantilla_documento_set_updated_at
BEFORE UPDATE ON plantilla_documento FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE plantilla_documento IS 'Plantillas de documentos personalizables';

-- =============================================================================
-- 9. TABLA: configuracion_integraciones
-- =============================================================================
-- Configuración de integraciones con servicios externos
CREATE TABLE IF NOT EXISTS configuracion_integraciones (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  servicio        VARCHAR(50) NOT NULL,  -- 'sunat', 'nubefact', 'culqi', 'izipay', etc.
  ambiente        VARCHAR(20) NOT NULL DEFAULT 'sandbox'
                   CHECK (ambiente IN ('sandbox', 'produccion')),
  
  -- Credenciales (encriptadas en aplicación)
  api_key         VARCHAR(500),
  api_secret      VARCHAR(500),
  token           VARCHAR(1000),
  usuario         VARCHAR(100),
  
  -- Configuración adicional como JSON
  config_extra    JSONB,
  
  activo          BOOLEAN NOT NULL DEFAULT FALSE,
  ultimo_uso      TIMESTAMPTZ,

  -- Rotación de credenciales
  rotated_at      TIMESTAMPTZ,
  rotated_by_id   BIGINT REFERENCES usuario(id),
  
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, servicio)
);

CREATE INDEX idx_integracion_tenant ON configuracion_integraciones(tenant_id);

CREATE TRIGGER configuracion_integraciones_set_updated_at
BEFORE UPDATE ON configuracion_integraciones FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE configuracion_integraciones IS 'Credenciales de integraciones externas';

-- =============================================================================
-- 10. TABLA: backup_configuracion
-- =============================================================================
-- Historial de cambios de configuración
CREATE TABLE IF NOT EXISTS backup_configuracion (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  fecha_backup    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  descripcion     VARCHAR(250),
  
  -- Snapshot de toda la configuración
  config_negocio  JSONB NOT NULL,
  config_sedes    JSONB,
  config_valores  JSONB,  -- De tabla configuracion_negocio
  
  creado_por_id   BIGINT REFERENCES usuario(id)
);

CREATE INDEX idx_backup_config_tenant ON backup_configuracion(tenant_id);

COMMENT ON TABLE backup_configuracion IS 'Backups de configuración para restauración';

-- =============================================================================
-- 11. FUNCIÓN: Crear backup de configuración
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.crear_backup_configuracion(
  p_tenant_id BIGINT,
  p_descripcion VARCHAR(250) DEFAULT 'Backup automático',
  p_usuario_id BIGINT DEFAULT NULL
)
RETURNS BIGINT LANGUAGE plpgsql AS $$
DECLARE
  v_backup_id BIGINT;
  v_config_negocio JSONB;
  v_config_sedes JSONB;
  v_config_valores JSONB;
BEGIN
  -- Obtener datos del negocio
  SELECT to_jsonb(n.*) INTO v_config_negocio
  FROM drinkgo.negocio n WHERE id = p_tenant_id;
  
  -- Obtener configuración de sedes
  SELECT jsonb_agg(to_jsonb(cs.*)) INTO v_config_sedes
  FROM drinkgo.configuracion_sede cs
  JOIN drinkgo.sede s ON cs.sede_id = s.id
  WHERE s.tenant_id = p_tenant_id;
  
  -- Obtener configuraciones clave-valor
  -- OBLIGATORIO: no perder categoria en el backup (restauración confiable).
  -- Escenario: Admin guarda la misma clave en categorías distintas; el backup debe preservar ambas.
  SELECT COALESCE(jsonb_agg(to_jsonb(cn.*)), '[]'::jsonb) INTO v_config_valores
  FROM drinkgo.configuracion_negocio cn
  WHERE cn.tenant_id = p_tenant_id;
  
  -- Insertar backup
  INSERT INTO drinkgo.backup_configuracion (
    tenant_id, descripcion, config_negocio, config_sedes, config_valores, creado_por_id
  ) VALUES (
    p_tenant_id, p_descripcion, v_config_negocio, v_config_sedes, v_config_valores, p_usuario_id
  )
  RETURNING id INTO v_backup_id;
  
  RETURN v_backup_id;
END $$;

COMMENT ON FUNCTION drinkgo.crear_backup_configuracion IS 'Crea un backup de toda la configuración del negocio';

-- =============================================================================
-- 12. VISTA: Resumen de configuración del negocio
-- =============================================================================
CREATE OR REPLACE VIEW drinkgo.v_resumen_configuracion AS
SELECT 
  n.id AS tenant_id,
  n.nombre AS negocio_nombre,
  n.numero_documento AS ruc,
  n.moneda_principal,
  n.zona_horaria,
  
  -- Sedes
  (SELECT COUNT(*) FROM drinkgo.sede s WHERE s.tenant_id = n.id AND s.activo) AS sedes_activas,
  
  -- Series de facturación
  (SELECT COUNT(*) FROM drinkgo.serie_comprobante sc WHERE sc.tenant_id = n.id AND sc.activo) AS series_activas,
  
  -- Integraciones
  (SELECT jsonb_object_agg(ci.servicio, ci.activo) 
   FROM drinkgo.configuracion_integraciones ci WHERE ci.tenant_id = n.id) AS integraciones,
  
  -- Último backup
  (SELECT fecha_backup FROM drinkgo.backup_configuracion bc 
   WHERE bc.tenant_id = n.id ORDER BY fecha_backup DESC LIMIT 1) AS ultimo_backup
   
FROM drinkgo.negocio n
WHERE n.activo = TRUE;

COMMENT ON VIEW drinkgo.v_resumen_configuracion IS 'Vista resumen del estado de configuración de cada negocio';
