-- ============================================================
-- SEED: PLANES DE SUSCRIPCIÓN
-- Base de datos: licores_drinkgo
-- Tabla: planes_suscripcion
-- ============================================================
-- Estructura de planes:
-- 1. Plan Básico (Emprendedor)
-- 2. Plan Profesional (Crecimiento)
-- 3. Plan Enterprise (Corporativo)
-- 4. Plan Free (Prueba gratuita)
-- ============================================================

USE drinkgo_db;

-- Limpiar datos existentes (solo para desarrollo)
-- DELETE FROM planes_suscripcion;

-- ============================================================
-- PLAN 1: BÁSICO / EMPRENDEDOR
-- ============================================================
INSERT IGNORE INTO planes_suscripcion (
    nombre,
    descripcion,
    precio,
    moneda,
    periodo_facturacion,
    max_sedes,
    max_usuarios,
    max_productos,
    max_almacenes_por_sede,
    permite_pos,
    permite_tienda_online,
    permite_delivery,
    permite_mesas,
    permite_facturacion_electronica,
    permite_multi_almacen,
    permite_reportes_avanzados,
    permite_acceso_api,
    funcionalidades_json,
    esta_activo,
    orden
) VALUES (
    'Plan Básico',
    'Ideal para emprendedores y licorerías pequeñas. Incluye funcionalidades esenciales para gestionar tu negocio.',
    149.00,
    'PEN',
    'mensual',
    1,                  -- 1 sede
    3,                  -- 3 usuarios
    500,                -- 500 productos
    1,                  -- 1 almacén por sede
    1,                  -- Permite POS
    0,                  -- NO permite tienda online
    0,                  -- NO permite delivery
    0,                  -- NO permite mesas
    0,                  -- NO permite facturación electrónica
    0,                  -- NO permite multi-almacén
    0,                  -- NO permite reportes avanzados
    0,                  -- NO permite API
    JSON_OBJECT(
        'soporte', 'email',
        'backup_diario', true,
        'almacenamiento_gb', 5,
        'transacciones_mes', 1000,
        'reportes_basicos', true
    ),
    1,                  -- Activo
    1                   -- Orden de visualización
);

-- ============================================================
-- PLAN 2: PROFESIONAL / CRECIMIENTO
-- ============================================================
INSERT IGNORE INTO planes_suscripcion (
    nombre,
    descripcion,
    precio,
    moneda,
    periodo_facturacion,
    max_sedes,
    max_usuarios,
    max_productos,
    max_almacenes_por_sede,
    permite_pos,
    permite_tienda_online,
    permite_delivery,
    permite_mesas,
    permite_facturacion_electronica,
    permite_multi_almacen,
    permite_reportes_avanzados,
    permite_acceso_api,
    funcionalidades_json,
    esta_activo,
    orden
) VALUES (
    'Plan Profesional',
    'Para negocios en crecimiento. Incluye tienda online, delivery y facturación electrónica.',
    349.00,
    'PEN',
    'mensual',
    3,                  -- 3 sedes
    10,                 -- 10 usuarios
    2000,               -- 2000 productos
    2,                  -- 2 almacenes por sede
    1,                  -- Permite POS
    1,                  -- Permite tienda online
    1,                  -- Permite delivery
    1,                  -- Permite mesas (restaurantes)
    1,                  -- Permite facturación electrónica
    1,                  -- Permite multi-almacén
    1,                  -- Permite reportes avanzados
    0,                  -- NO permite API
    JSON_OBJECT(
        'soporte', 'prioritario',
        'backup_diario', true,
        'almacenamiento_gb', 25,
        'transacciones_mes', 5000,
        'reportes_basicos', true,
        'reportes_avanzados', true,
        'integraciones', JSON_ARRAY('sunat', 'pasarelas_pago', 'whatsapp'),
        'whatsapp_notificaciones', true
    ),
    1,                  -- Activo
    2                   -- Orden de visualización
);

-- ============================================================
-- PLAN 3: ENTERPRISE / CORPORATIVO
-- ============================================================
INSERT IGNORE INTO planes_suscripcion (
    nombre,
    descripcion,
    precio,
    moneda,
    periodo_facturacion,
    max_sedes,
    max_usuarios,
    max_productos,
    max_almacenes_por_sede,
    permite_pos,
    permite_tienda_online,
    permite_delivery,
    permite_mesas,
    permite_facturacion_electronica,
    permite_multi_almacen,
    permite_reportes_avanzados,
    permite_acceso_api,
    funcionalidades_json,
    esta_activo,
    orden
) VALUES (
    'Plan Enterprise',
    'Solución completa para cadenas y corporativos. Incluye API, sedes ilimitadas y soporte 24/7.',
    899.00,
    'PEN',
    'mensual',
    999,                -- Ilimitado (999 representa ilimitado)
    999,                -- Usuarios ilimitados
    999999,             -- Productos ilimitados
    10,                 -- 10 almacenes por sede
    1,                  -- Permite POS
    1,                  -- Permite tienda online
    1,                  -- Permite delivery
    1,                  -- Permite mesas
    1,                  -- Permite facturación electrónica
    1,                  -- Permite multi-almacén
    1,                  -- Permite reportes avanzados
    1,                  -- Permite acceso API
    JSON_OBJECT(
        'soporte', '24/7',
        'backup_diario', true,
        'backup_tiempo_real', true,
        'almacenamiento_gb', 500,
        'transacciones_mes', 999999,
        'reportes_basicos', true,
        'reportes_avanzados', true,
        'reportes_personalizados', true,
        'integraciones', JSON_ARRAY('sunat', 'pasarelas_pago', 'whatsapp', 'erp', 'crm'),
        'whatsapp_notificaciones', true,
        'api_acceso', 'completo',
        'webhooks', true,
        'sso', true,
        'ip_whitelist', true,
        'audit_logs', true,
        'gerente_cuenta_dedicado', true
    ),
    1,                  -- Activo
    3                   -- Orden de visualización
);

-- ============================================================
-- PLAN 4: FREE / PRUEBA GRATUITA
-- ============================================================
INSERT IGNORE INTO planes_suscripcion (
    nombre,
    descripcion,
    precio,
    moneda,
    periodo_facturacion,
    max_sedes,
    max_usuarios,
    max_productos,
    max_almacenes_por_sede,
    permite_pos,
    permite_tienda_online,
    permite_delivery,
    permite_mesas,
    permite_facturacion_electronica,
    permite_multi_almacen,
    permite_reportes_avanzados,
    permite_acceso_api,
    funcionalidades_json,
    esta_activo,
    orden
) VALUES (
    'Plan Free',
    'Prueba gratuita por 30 días. Perfecto para conocer la plataforma antes de suscribirte.',
    0.00,
    'PEN',
    'mensual',
    1,                  -- 1 sede
    2,                  -- 2 usuarios
    100,                -- 100 productos
    1,                  -- 1 almacén
    1,                  -- Permite POS
    0,                  -- NO permite tienda online
    0,                  -- NO permite delivery
    0,                  -- NO permite mesas
    0,                  -- NO permite facturación electrónica
    0,                  -- NO permite multi-almacén
    0,                  -- NO permite reportes avanzados
    0,                  -- NO permite API
    JSON_OBJECT(
        'soporte', 'email',
        'duracion_dias', 30,
        'backup_diario', false,
        'almacenamiento_gb', 1,
        'transacciones_mes', 100,
        'marca_agua', true,
        'reportes_basicos', true,
        'trial', true
    ),
    1,                  -- Activo
    4                   -- Orden de visualización
);

-- Verificar inserción
SELECT 
    id,
    nombre,
    precio,
    moneda,
    periodo_facturacion,
    max_sedes,
    max_usuarios,
    max_productos,
    permite_pos,
    permite_tienda_online,
    permite_delivery,
    permite_facturacion_electronica,
    esta_activo,
    orden
FROM planes_suscripcion 
ORDER BY orden;

-- ============================================================
-- RESUMEN DE PLANES CREADOS:
-- ============================================================
-- 1. Plan Básico: S/149/mes - 1 sede, 3 usuarios, 500 productos
-- 2. Plan Profesional: S/349/mes - 3 sedes, 10 usuarios, 2000 productos
-- 3. Plan Enterprise: S/899/mes - Ilimitado
-- 4. Plan Free: S/0 - Prueba 30 días, limitado
-- ============================================================
