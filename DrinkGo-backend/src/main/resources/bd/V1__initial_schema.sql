-- =============================================================================
-- BD_DRINKGO - V1: Esquema Base, Extensiones y Tipos Enumerados
-- Fecha: 2026-01-26
-- Descripción: Sistema SaaS Multi-tenant para Licorerías
-- =============================================================================

-- Crear esquema principal
CREATE SCHEMA IF NOT EXISTS drinkgo;

-- Configurar búsqueda de esquemas
SET search_path = drinkgo, public;

-- =============================================================================
-- EXTENSIONES
-- =============================================================================
CREATE EXTENSION IF NOT EXISTS citext;      -- Texto case-insensitive para emails
CREATE EXTENSION IF NOT EXISTS pg_trgm;     -- Búsqueda por similitud (trigrams)
CREATE EXTENSION IF NOT EXISTS btree_gist;  -- Índices para rangos y exclusiones
CREATE EXTENSION IF NOT EXISTS "uuid-ossp"; -- Generación de UUIDs

-- =============================================================================
-- TIPOS ENUMERADOS (DOMAIN TYPES)
-- =============================================================================

-- Tipos de suscripción SaaS
CREATE TYPE drinkgo.subscription_type AS ENUM ('monthly', 'annual', 'trial', 'enterprise');

-- Estados de suscripción
CREATE TYPE drinkgo.subscription_status AS ENUM ('active', 'suspended', 'cancelled', 'expired', 'pending_payment');

-- Tipos de producto de licorería
CREATE TYPE drinkgo.product_type AS ENUM (
  'cerveza',        -- Cervezas (vencimiento corto)
  'vino',           -- Vinos (delicados, pueden añejar)
  'destilado',      -- Licores destilados (larga duración)
  'crema_licor',    -- Cremas de licor (vencimiento medio)
  'espumante',      -- Champagne, prosecco, etc.
  'complemento',    -- Hielo, gaseosas, snacks, limones
  'accesorio'       -- Vasos, hieleras, sacacorchos
);

-- Temperaturas requeridas de almacenamiento
CREATE TYPE drinkgo.storage_temp AS ENUM ('ambiente', 'frio', 'congelado');

-- Estados de lote de inventario
CREATE TYPE drinkgo.batch_status AS ENUM ('disponible', 'reservado', 'agotado', 'vencido', 'dañado');

-- Modalidades de pedido (polimorfismo)
CREATE TYPE drinkgo.order_modality AS ENUM ('delivery', 'pickup', 'mesa', 'barra');

-- Estados de pedido
CREATE TYPE drinkgo.order_status AS ENUM ('pendiente', 'en_preparacion', 'listo', 'entregado', 'anulado');

-- Métodos de pago
CREATE TYPE drinkgo.payment_method AS ENUM ('efectivo', 'yape', 'plin', 'tarjeta', 'transferencia');

COMMENT ON TYPE drinkgo.product_type IS 'Clasificación de productos por naturaleza (afecta manejo de inventario)';
COMMENT ON TYPE drinkgo.storage_temp IS 'Temperatura requerida: ambiente (15-25°C), frio (2-8°C), congelado (<0°C)';
COMMENT ON TYPE drinkgo.batch_status IS 'Estado del lote: FIFO prioriza disponible con fecha_vencimiento más cercana';
