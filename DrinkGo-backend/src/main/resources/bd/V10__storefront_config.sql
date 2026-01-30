-- =============================================================================
-- BD_DRINKGO - V10: Storefront y Configuración de Tienda Online
-- Fecha: 2026-01-26
-- Descripción: Configuración de e-commerce con validaciones legales para licorería
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: storefront_config
-- =============================================================================
-- Configuración principal de la tienda online por negocio
CREATE TABLE IF NOT EXISTS storefront_config (
  negocio_id              BIGINT PRIMARY KEY REFERENCES negocio(id) ON DELETE CASCADE,
  tenant_id               BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  -- Dominio y URL
  subdominio              VARCHAR(60) UNIQUE,  -- miticoreria.drinkgo.app
  dominio_personalizado   VARCHAR(250),
  
  -- Branding
  nombre_tienda           VARCHAR(150),
  slogan                  VARCHAR(250),
  logo_url                VARCHAR(500),
  favicon_url             VARCHAR(500),
  
  -- Colores (tema visual)
  color_primario          VARCHAR(7) DEFAULT '#1a1a2e',  -- Hex
  color_secundario        VARCHAR(7) DEFAULT '#e94560',
  color_acento            VARCHAR(7) DEFAULT '#f5af19',
  color_fondo             VARCHAR(7) DEFAULT '#0f0f0f',
  
  -- Assets por defecto para licorería
  imagen_hero_url         VARCHAR(500),
  imagen_age_gate_url     VARCHAR(500),  -- Imagen para verificación de edad
  
  -- SEO
  meta_titulo             VARCHAR(70),
  meta_descripcion        VARCHAR(160),
  keywords                TEXT[],
  
  -- Redes sociales
  facebook_url            VARCHAR(250),
  instagram_url           VARCHAR(250),
  tiktok_url              VARCHAR(250),
  whatsapp_numero         VARCHAR(20),
  
  -- Configuración de pagos
  acepta_efectivo_entrega BOOLEAN NOT NULL DEFAULT TRUE,
  acepta_yape             BOOLEAN NOT NULL DEFAULT TRUE,
  acepta_plin             BOOLEAN NOT NULL DEFAULT TRUE,
  acepta_tarjeta          BOOLEAN NOT NULL DEFAULT FALSE,
  pasarela_pago           VARCHAR(50),  -- 'stripe', 'mercadopago', 'izipay', 'niubiz'
  pasarela_api_key        VARCHAR(250),  -- Encriptado en backend
  
  -- ==========================================================================
  -- VALIDACIONES LEGALES (CRÍTICO PARA LICORERÍA)
  -- ==========================================================================
  
  -- Age Gate (Verificación de mayoría de edad)
  age_gate_activo         BOOLEAN NOT NULL DEFAULT TRUE,
  age_gate_mensaje        TEXT DEFAULT 'Debes ser mayor de 18 años para ingresar a este sitio. El consumo de alcohol es nocivo para la salud.',
  age_gate_boton_aceptar  VARCHAR(50) DEFAULT 'Soy mayor de 18 años',
  age_gate_boton_rechazar VARCHAR(50) DEFAULT 'No soy mayor de edad',
  
  -- Mensajes legales
  mensaje_legal_checkout  TEXT DEFAULT 'Al realizar esta compra, confirmo que soy mayor de 18 años y asumo la responsabilidad del consumo de bebidas alcohólicas.',
  mensaje_responsabilidad TEXT DEFAULT 'TOMAR BEBIDAS ALCOHÓLICAS EN EXCESO ES DAÑINO. PROHIBIDA LA VENTA A MENORES DE 18 AÑOS.',
  
  -- Checkbox obligatorios en checkout
  requiere_acepta_terminos      BOOLEAN NOT NULL DEFAULT TRUE,
  requiere_confirma_mayor_edad  BOOLEAN NOT NULL DEFAULT TRUE,
  
  -- Configuración de envío
  envio_minimo            NUMERIC(12,2) DEFAULT 0,
  envio_gratis_desde      NUMERIC(12,2),  -- NULL = nunca gratis
  
  -- Horarios de atención online
  horario_atencion_inicio TIME,
  horario_atencion_fin    TIME,
  
  -- Estado
  activo                  BOOLEAN NOT NULL DEFAULT FALSE,  -- Debe activarse manualmente
  en_mantenimiento        BOOLEAN NOT NULL DEFAULT FALSE,
  mensaje_mantenimiento   VARCHAR(250),
  
  creado_en               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_storefront_subdominio ON storefront_config(subdominio) WHERE subdominio IS NOT NULL;

CREATE TRIGGER storefront_config_set_updated_at
BEFORE UPDATE ON storefront_config FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE storefront_config IS 'Configuración de tienda online con validaciones legales para licorería';
COMMENT ON COLUMN storefront_config.age_gate_activo IS 'OBLIGATORIO: Verificación de mayoría de edad al entrar';
COMMENT ON COLUMN storefront_config.requiere_confirma_mayor_edad IS 'Checkbox en checkout confirmando mayoría de edad';

-- =============================================================================
-- 2. TABLA: storefront_horario_venta
-- =============================================================================
-- Horarios permitidos de venta de alcohol online por día
CREATE TABLE IF NOT EXISTS storefront_horario_venta (
  id              BIGSERIAL PRIMARY KEY,
  storefront_id   BIGINT NOT NULL REFERENCES storefront_config(negocio_id) ON DELETE CASCADE,
  
  dia_semana      INT NOT NULL CHECK (dia_semana BETWEEN 0 AND 6),  -- 0=Domingo
  hora_inicio     TIME NOT NULL,
  hora_fin        TIME NOT NULL,
  
  -- Excepciones
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  motivo_inactivo VARCHAR(100),  -- "Ley seca electoral", etc.
  
  UNIQUE(storefront_id, dia_semana)
);

CREATE INDEX idx_storefront_horario ON storefront_horario_venta(storefront_id);

COMMENT ON TABLE storefront_horario_venta IS 'Horarios permitidos de venta online por día (restricciones legales)';

-- =============================================================================
-- 3. TABLA: storefront_banner
-- =============================================================================
-- Banners promocionales del carousel
CREATE TABLE IF NOT EXISTS storefront_banner (
  id              BIGSERIAL PRIMARY KEY,
  storefront_id   BIGINT NOT NULL REFERENCES storefront_config(negocio_id) ON DELETE CASCADE,
  
  titulo          VARCHAR(100),
  subtitulo       VARCHAR(200),
  imagen_url      VARCHAR(500) NOT NULL,
  imagen_mobile_url VARCHAR(500),
  
  -- Link del banner
  link_tipo       VARCHAR(20) CHECK (link_tipo IN ('producto', 'categoria', 'combo', 'externo', 'ninguno')),
  link_id         BIGINT,  -- ID del producto/categoria/combo
  link_url        VARCHAR(500),  -- Para links externos
  
  -- Configuración visual
  color_texto     VARCHAR(7) DEFAULT '#FFFFFF',
  posicion_texto  VARCHAR(20) DEFAULT 'center' CHECK (posicion_texto IN ('left', 'center', 'right')),
  
  orden           INT NOT NULL DEFAULT 0,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  
  vigente_desde   TIMESTAMPTZ,
  vigente_hasta   TIMESTAMPTZ,
  
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_storefront_banner ON storefront_banner(storefront_id);
CREATE INDEX idx_storefront_banner_activo ON storefront_banner(storefront_id, activo, orden);

COMMENT ON TABLE storefront_banner IS 'Banners del carousel de la tienda online';

-- =============================================================================
-- 4. TABLA: storefront_pagina
-- =============================================================================
-- Páginas estáticas (Términos, Política de privacidad, Nosotros, etc.)
CREATE TABLE IF NOT EXISTS storefront_pagina (
  id              BIGSERIAL PRIMARY KEY,
  storefront_id   BIGINT NOT NULL REFERENCES storefront_config(negocio_id) ON DELETE CASCADE,
  
  slug            VARCHAR(60) NOT NULL,
  titulo          VARCHAR(200) NOT NULL,
  contenido       TEXT NOT NULL,
  
  -- SEO
  meta_titulo     VARCHAR(70),
  meta_descripcion VARCHAR(160),
  
  es_legal        BOOLEAN NOT NULL DEFAULT FALSE,  -- Términos, Privacidad, etc.
  mostrar_en_footer BOOLEAN NOT NULL DEFAULT TRUE,
  orden           INT NOT NULL DEFAULT 0,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(storefront_id, slug)
);

CREATE INDEX idx_storefront_pagina ON storefront_pagina(storefront_id);

CREATE TRIGGER storefront_pagina_set_updated_at
BEFORE UPDATE ON storefront_pagina FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE storefront_pagina IS 'Páginas estáticas de la tienda (términos, privacidad, etc.)';

-- Insertar páginas legales por defecto al crear storefront (trigger)
CREATE OR REPLACE FUNCTION drinkgo.crear_paginas_legales_default()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  -- Términos y condiciones
  INSERT INTO drinkgo.storefront_pagina (storefront_id, slug, titulo, contenido, es_legal, orden)
  VALUES (
    NEW.negocio_id,
    'terminos-condiciones',
    'Términos y Condiciones',
    '<h2>1. Restricción de Edad</h2><p>Este sitio web está destinado exclusivamente para personas mayores de 18 años. Al acceder y realizar compras, usted confirma que cumple con este requisito legal.</p><h2>2. Venta de Bebidas Alcohólicas</h2><p>La venta y consumo de bebidas alcohólicas está sujeta a las regulaciones locales. Está prohibida la venta a menores de edad.</p>',
    TRUE,
    1
  );
  
  -- Política de privacidad
  INSERT INTO drinkgo.storefront_pagina (storefront_id, slug, titulo, contenido, es_legal, orden)
  VALUES (
    NEW.negocio_id,
    'politica-privacidad',
    'Política de Privacidad',
    '<h2>Recopilación de Datos</h2><p>Recopilamos información personal necesaria para procesar pedidos y verificar la mayoría de edad de nuestros clientes.</p>',
    TRUE,
    2
  );
  
  -- Política de envíos
  INSERT INTO drinkgo.storefront_pagina (storefront_id, slug, titulo, contenido, es_legal, orden)
  VALUES (
    NEW.negocio_id,
    'politica-envios',
    'Política de Envíos',
    '<h2>Verificación de Edad en Entrega</h2><p>El repartidor está autorizado a solicitar documento de identidad para verificar la mayoría de edad antes de entregar el pedido. En caso de no poder verificar la edad, el pedido no será entregado.</p>',
    TRUE,
    3
  );
  
  RETURN NEW;
END $$;

CREATE TRIGGER storefront_crear_paginas_default
AFTER INSERT ON storefront_config
FOR EACH ROW
EXECUTE FUNCTION drinkgo.crear_paginas_legales_default();

-- =============================================================================
-- 5. TABLA: storefront_session
-- =============================================================================
-- Sesiones de visitantes (para age gate y carrito)
CREATE TABLE IF NOT EXISTS storefront_session (
  id              BIGSERIAL PRIMARY KEY,
  session_token   UUID UNIQUE NOT NULL DEFAULT uuid_generate_v4(),
  storefront_id   BIGINT NOT NULL REFERENCES storefront_config(negocio_id) ON DELETE CASCADE,
  
  -- Verificación de edad
  age_verified    BOOLEAN NOT NULL DEFAULT FALSE,
  age_verified_at TIMESTAMPTZ,
  
  -- Carrito (JSON)
  carrito         JSONB DEFAULT '[]'::JSONB,
  
  -- Cliente (si se identifica)
  cliente_id      BIGINT REFERENCES cliente(id) ON DELETE SET NULL,
  
  -- Metadata
  ip_address      INET,
  user_agent      VARCHAR(500),
  
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  expira_en       TIMESTAMPTZ NOT NULL DEFAULT NOW() + INTERVAL '7 days'
);

CREATE INDEX idx_storefront_session_token ON storefront_session(session_token);
CREATE INDEX idx_storefront_session_expira ON storefront_session(expira_en);

CREATE TRIGGER storefront_session_set_updated_at
BEFORE UPDATE ON storefront_session FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE storefront_session IS 'Sesiones de visitantes de la tienda online';
COMMENT ON COLUMN storefront_session.age_verified IS 'TRUE si el visitante confirmó ser mayor de edad';

-- =============================================================================
-- 6. FUNCIÓN: Validar pedido online (edad + horario)
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.validar_pedido_online(
  p_storefront_id BIGINT,
  p_sede_id BIGINT,
  p_session_token UUID
)
RETURNS TABLE (
  puede_comprar BOOLEAN,
  motivo_rechazo VARCHAR
) LANGUAGE plpgsql AS $$
DECLARE
  v_session RECORD;
  v_config RECORD;
  v_horario RECORD;
  v_dia_semana INT;
  v_hora_actual TIME;
BEGIN
  -- Obtener sesión
  SELECT * INTO v_session FROM drinkgo.storefront_session 
  WHERE session_token = p_session_token AND storefront_id = p_storefront_id;
  
  IF NOT FOUND THEN
    RETURN QUERY SELECT FALSE, 'Sesión inválida o expirada'::VARCHAR;
    RETURN;
  END IF;
  
  -- Verificar age gate
  IF NOT v_session.age_verified THEN
    RETURN QUERY SELECT FALSE, 'Debe confirmar que es mayor de 18 años'::VARCHAR;
    RETURN;
  END IF;
  
  -- Verificar horario de venta de alcohol
  IF NOT drinkgo.validar_horario_venta_alcohol(p_sede_id) THEN
    RETURN QUERY SELECT FALSE, 'Fuera del horario permitido para venta de bebidas alcohólicas'::VARCHAR;
    RETURN;
  END IF;
  
  -- Obtener config storefront
  SELECT * INTO v_config FROM drinkgo.storefront_config WHERE negocio_id = p_storefront_id;
  
  IF NOT v_config.activo THEN
    RETURN QUERY SELECT FALSE, 'Tienda temporalmente cerrada'::VARCHAR;
    RETURN;
  END IF;
  
  IF v_config.en_mantenimiento THEN
    RETURN QUERY SELECT FALSE, COALESCE(v_config.mensaje_mantenimiento, 'Tienda en mantenimiento')::VARCHAR;
    RETURN;
  END IF;
  
  -- Verificar horario de atención online
  v_hora_actual := CURRENT_TIME;
  IF v_config.horario_atencion_inicio IS NOT NULL AND v_config.horario_atencion_fin IS NOT NULL THEN
    IF v_hora_actual < v_config.horario_atencion_inicio OR v_hora_actual > v_config.horario_atencion_fin THEN
      RETURN QUERY SELECT FALSE, 'Fuera del horario de atención online'::VARCHAR;
      RETURN;
    END IF;
  END IF;
  
  -- Todo OK
  RETURN QUERY SELECT TRUE, NULL::VARCHAR;
END $$;

COMMENT ON FUNCTION drinkgo.validar_pedido_online IS 
'Valida que un pedido online cumpla requisitos legales: edad verificada y horario permitido';
