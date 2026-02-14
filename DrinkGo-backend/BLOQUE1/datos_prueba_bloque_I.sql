-- ============================================
-- Datos de Prueba - Bloque I
-- DrinkGo Platform
-- ============================================

USE drinkgo_db;

-- 1. Crear planes de suscripción
INSERT INTO planes_suscripcion (
    nombre, slug, descripcion, precio, moneda, periodo_facturacion,
    max_sedes, max_usuarios, max_productos, max_almacenes_por_sede,
    permite_pos, permite_tienda_online, permite_delivery, permite_mesas,
    permite_facturacion_electronica, permite_multi_almacen,
    permite_reportes_avanzados, permite_acceso_api,
    orden, esta_activo, version
) VALUES
-- Plan Básico
('Plan Básico', 'basico', 'Ideal para pequeños negocios que están comenzando', 
49.90, 'PEN', 'mensual', 1, 3, 100, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1),

-- Plan Pro
('Plan Pro', 'pro', 'Para negocios en crecimiento con más funcionalidades',
99.90, 'PEN', 'mensual', 3, 10, 1000, 3, 1, 1, 1, 1, 0, 1, 1, 0, 2, 1, 1),

-- Plan Enterprise
('Plan Enterprise', 'enterprise', 'Sin límites para grandes empresas',  
299.90, 'PEN', 'mensual', 999, 999, 999999, 999, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1);

-- 2. Crear suscripción para el negocio existente (id=1)
INSERT INTO suscripciones (
    negocio_id, plan_id, estado,
    inicio_periodo_actual, fin_periodo_actual,
    proxima_fecha_facturacion, precio_periodo_actual
) VALUES (
    1, 2, 'activa',
    '2026-02-01', '2026-03-01', 
    '2026-03-01', 99.90
);

-- 3. Configuraciones globales de plataforma
INSERT INTO configuracion_global_plataforma (
    clave_configuracion, valor, tipo_dato, descripcion, es_publica
) VALUES
('IGV_PERU', '0.18', 'decimal', 'Impuesto General a las Ventas en Perú', 1),
('DIAS_PRUEBA_GRATIS', '15', 'integer', 'Días de prueba gratisita para nuevos clientes', 1),
('EMAIL_SOPORTE', 'soporte@drinkgo.pe', 'string', 'Email de contacto para soporte', 1),
('MAX_INTENTOS_LOGIN', '5', 'integer', 'Máximo de intentos de login fallidos antes de bloquear', 0),
('TIEMPO_SESSION_MINUTOS', '480', 'integer', 'Tiempo de sesión en minutos (8 horas)', 0);

-- Verificar datos insertados
SELECT 'Planes insertados:' as mensaje, COUNT(*) as total FROM planes_suscripcion WHERE esta_activo = 1;
SELECT 'Suscripciones activas:' as mensaje, COUNT(*) as total FROM suscripciones WHERE estado = 'activa';
SELECT 'Configuraciones:' as mensaje, COUNT(*) as total FROM configuracion_global_plataforma;
