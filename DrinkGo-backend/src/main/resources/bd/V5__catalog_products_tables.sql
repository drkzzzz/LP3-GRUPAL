-- =============================================================================
-- BD_DRINKGO - V5: Catálogo de Productos y Combos para Licorería
-- Fecha: 2026-01-26
-- Descripción: Productos con atributos de licor, categorías, combos/packs
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: categoria_producto
-- =============================================================================
CREATE TABLE IF NOT EXISTS categoria_producto (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  codigo          VARCHAR(30) NOT NULL,
  nombre          VARCHAR(120) NOT NULL,
  descripcion     VARCHAR(250),
  imagen_url      VARCHAR(500),
  orden           INT NOT NULL DEFAULT 0,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, codigo)
);

CREATE INDEX idx_categoria_tenant ON categoria_producto(tenant_id);
CREATE INDEX idx_categoria_orden ON categoria_producto(tenant_id, orden);

CREATE TRIGGER categoria_producto_set_updated_at
BEFORE UPDATE ON categoria_producto FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE categoria_producto IS 'Categorías de productos por tenant (cervezas, vinos, etc.)';

-- =============================================================================
-- 2. TABLA: marca
-- =============================================================================
-- Marcas de productos (importante en licorería)
CREATE TABLE IF NOT EXISTS marca (
  id              BIGSERIAL PRIMARY KEY,
  tenant_id       BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  nombre          VARCHAR(120) NOT NULL,
  pais_origen     VARCHAR(60),
  logo_url        VARCHAR(500),
  descripcion     TEXT,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, nombre)
);

CREATE INDEX idx_marca_tenant ON marca(tenant_id);

CREATE TRIGGER marca_set_updated_at
BEFORE UPDATE ON marca FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE marca IS 'Marcas de productos (Johnnie Walker, Corona, etc.)';

-- =============================================================================
-- 3. TABLA: producto (CRÍTICA - Adaptada para Licorería)
-- =============================================================================
CREATE TABLE IF NOT EXISTS producto (
  id                    BIGSERIAL PRIMARY KEY,
  tenant_id             BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  
  -- Identificación
  codigo                VARCHAR(50) NOT NULL,
  codigo_barras         VARCHAR(50),
  nombre                VARCHAR(200) NOT NULL,
  descripcion           TEXT,
  
  -- Clasificación
  categoria_id          BIGINT REFERENCES categoria_producto(id) ON DELETE SET NULL,
  marca_id              BIGINT REFERENCES marca(id) ON DELETE SET NULL,
  tipo_producto         drinkgo.product_type NOT NULL DEFAULT 'complemento',
  
  -- Atributos específicos de licorería
  grado_alcoholico      NUMERIC(4,1) CHECK (grado_alcoholico IS NULL OR (grado_alcoholico >= 0 AND grado_alcoholico <= 100)),
  volumen_ml            INT,  -- Volumen en mililitros (750ml, 330ml, etc.)
  pais_origen           VARCHAR(60),
  anio_cosecha          INT CHECK (anio_cosecha IS NULL OR (anio_cosecha >= 1900 AND anio_cosecha <= EXTRACT(YEAR FROM CURRENT_DATE) + 5)),
  region                VARCHAR(100),  -- Para vinos: Rioja, Mendoza, etc.
  variedad_uva          VARCHAR(100),  -- Cabernet, Merlot, etc.
  temperatura_requerida drinkgo.storage_temp NOT NULL DEFAULT 'ambiente',
  
  -- Precios
  precio_venta          NUMERIC(12,2) NOT NULL CHECK (precio_venta >= 0),
  precio_mayorista      NUMERIC(12,2) CHECK (precio_mayorista IS NULL OR precio_mayorista >= 0),
  costo_referencia      NUMERIC(12,4) CHECK (costo_referencia IS NULL OR costo_referencia >= 0),
  
  -- Unidades
  unidad_medida         VARCHAR(30) NOT NULL DEFAULT 'unidad',
  unidad_x_paquete      INT DEFAULT 1 CHECK (unidad_x_paquete >= 1),  -- Ej: six-pack = 6
  
  -- Storefront (e-commerce)
  visible_storefront    BOOLEAN NOT NULL DEFAULT TRUE,
  destacado             BOOLEAN NOT NULL DEFAULT FALSE,
  
  -- Control
  es_alcoholico         BOOLEAN NOT NULL DEFAULT TRUE,  -- Para validaciones de edad
  requiere_refrigeracion BOOLEAN NOT NULL DEFAULT FALSE,
  activo                BOOLEAN NOT NULL DEFAULT TRUE,
  
  creado_en             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, codigo)
);

-- Índices para búsqueda y filtrado
CREATE INDEX idx_producto_tenant ON producto(tenant_id);
CREATE INDEX idx_producto_categoria ON producto(tenant_id, categoria_id);
CREATE INDEX idx_producto_marca ON producto(tenant_id, marca_id);
CREATE INDEX idx_producto_tipo ON producto(tenant_id, tipo_producto);
CREATE INDEX idx_producto_nombre_trgm ON producto USING gin (nombre gin_trgm_ops);
CREATE INDEX idx_producto_storefront ON producto(tenant_id, visible_storefront, activo) 
  WHERE visible_storefront = TRUE AND activo = TRUE;
CREATE INDEX idx_producto_codigo_barras ON producto(codigo_barras) WHERE codigo_barras IS NOT NULL;

CREATE TRIGGER producto_set_updated_at
BEFORE UPDATE ON producto FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

CREATE TRIGGER producto_check_tenant
BEFORE INSERT OR UPDATE ON producto FOR EACH ROW
EXECUTE FUNCTION drinkgo.check_tenant_isolation();

COMMENT ON TABLE producto IS 'Catálogo de productos con atributos específicos para licorería';
COMMENT ON COLUMN producto.tipo_producto IS 'Clasifica el producto (cerveza, vino, destilado, etc.)';
COMMENT ON COLUMN producto.grado_alcoholico IS 'Porcentaje de alcohol (0-100%)';
COMMENT ON COLUMN producto.temperatura_requerida IS 'Requerimiento de almacenamiento';
COMMENT ON COLUMN producto.es_alcoholico IS 'TRUE requiere validación de mayoría de edad';

-- =============================================================================
-- 4. TABLA: producto_imagen
-- =============================================================================
CREATE TABLE IF NOT EXISTS producto_imagen (
  id            BIGSERIAL PRIMARY KEY,
  tenant_id     BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  producto_id   BIGINT NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
  url           VARCHAR(500) NOT NULL,
  alt_text      VARCHAR(200),
  orden         INT NOT NULL DEFAULT 1,
  es_principal  BOOLEAN NOT NULL DEFAULT FALSE,
  
  UNIQUE(producto_id, orden)
);

CREATE INDEX idx_producto_imagen_producto ON producto_imagen(producto_id);

COMMENT ON TABLE producto_imagen IS 'Imágenes de productos para e-commerce';

-- =============================================================================
-- 5. TABLA: combo (Packs de Licorería)
-- =============================================================================
-- Combos/Packs: "Pack Chilcano", "Six Pack + Snacks", etc.
CREATE TABLE IF NOT EXISTS combo (
  id                    BIGSERIAL PRIMARY KEY,
  tenant_id             BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  codigo                VARCHAR(50) NOT NULL,
  nombre                VARCHAR(200) NOT NULL,
  descripcion           TEXT,
  imagen_url            VARCHAR(500),
  
  -- Precio del combo
  precio_combo          NUMERIC(12,2) NOT NULL CHECK (precio_combo >= 0),
  precio_original       NUMERIC(12,2),  -- Suma de precios individuales (para mostrar ahorro)
  
  -- Vigencia de la promoción
  vigente_desde         TIMESTAMPTZ,
  vigente_hasta         TIMESTAMPTZ,
  
  -- Storefront
  visible_storefront    BOOLEAN NOT NULL DEFAULT TRUE,
  destacado             BOOLEAN NOT NULL DEFAULT FALSE,
  
  activo                BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, codigo)
);

CREATE INDEX idx_combo_tenant ON combo(tenant_id);
CREATE INDEX idx_combo_vigencia ON combo(tenant_id, vigente_desde, vigente_hasta) 
  WHERE activo = TRUE;
CREATE INDEX idx_combo_storefront ON combo(tenant_id, visible_storefront, activo)
  WHERE visible_storefront = TRUE AND activo = TRUE;

CREATE TRIGGER combo_set_updated_at
BEFORE UPDATE ON combo FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE combo IS 'Combos/Packs promocionales (ej: Pack Chilcano)';
COMMENT ON COLUMN combo.precio_original IS 'Precio sin descuento para mostrar el ahorro';

-- =============================================================================
-- 6. TABLA: combo_item (Componentes del combo)
-- =============================================================================
CREATE TABLE IF NOT EXISTS combo_item (
  id              BIGSERIAL PRIMARY KEY,
  combo_id        BIGINT NOT NULL REFERENCES combo(id) ON DELETE CASCADE,
  producto_id     BIGINT NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
  cantidad        NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
  
  UNIQUE(combo_id, producto_id)
);

CREATE INDEX idx_combo_item_combo ON combo_item(combo_id);
CREATE INDEX idx_combo_item_producto ON combo_item(producto_id);

COMMENT ON TABLE combo_item IS 'Productos que componen cada combo';
COMMENT ON COLUMN combo_item.cantidad IS 'Cantidad del producto en el combo';

-- =============================================================================
-- 7. TABLA: promocion
-- =============================================================================
-- Promociones generales (descuentos por categoría, marca, etc.)
CREATE TABLE IF NOT EXISTS promocion (
  id                BIGSERIAL PRIMARY KEY,
  tenant_id         BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  codigo            VARCHAR(50) NOT NULL,
  nombre            VARCHAR(200) NOT NULL,
  descripcion       TEXT,
  
  -- Tipo de descuento
  tipo_descuento    VARCHAR(20) NOT NULL CHECK (tipo_descuento IN ('porcentaje', 'monto_fijo', 'precio_especial')),
  valor_descuento   NUMERIC(12,2) NOT NULL CHECK (valor_descuento >= 0),
  
  -- Aplicación
  aplica_a          VARCHAR(30) NOT NULL CHECK (aplica_a IN ('producto', 'categoria', 'marca', 'total_venta')),
  producto_id       BIGINT REFERENCES producto(id) ON DELETE CASCADE,
  categoria_id      BIGINT REFERENCES categoria_producto(id) ON DELETE CASCADE,
  marca_id          BIGINT REFERENCES marca(id) ON DELETE CASCADE,
  
  -- Condiciones
  compra_minima     NUMERIC(12,2) DEFAULT 0,
  cantidad_minima   INT DEFAULT 1,
  
  -- Vigencia
  vigente_desde     TIMESTAMPTZ NOT NULL,
  vigente_hasta     TIMESTAMPTZ NOT NULL,
  
  activo            BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  actualizado_en    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(tenant_id, codigo)
);

CREATE INDEX idx_promocion_tenant ON promocion(tenant_id);
CREATE INDEX idx_promocion_vigencia ON promocion(tenant_id, vigente_desde, vigente_hasta) 
  WHERE activo = TRUE;

CREATE TRIGGER promocion_set_updated_at
BEFORE UPDATE ON promocion FOR EACH ROW
EXECUTE FUNCTION drinkgo.set_updated_at();

COMMENT ON TABLE promocion IS 'Promociones y descuentos configurables';
