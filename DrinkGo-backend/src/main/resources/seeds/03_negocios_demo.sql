-- ============================================================
-- SEED: NEGOCIOS DEMO Y SUSCRIPCIONES
-- Base de datos: licores_drinkgo
-- Tablas: negocios, suscripciones, sedes, almacenes
-- ============================================================
-- Este seed crea negocios de ejemplo con sus suscripciones y sedes
-- para facilitar las pruebas del módulo SuperAdmin
-- ============================================================

USE licores_drinkgo;

-- ============================================================
-- VARIABLES DE REFERENCIA (Obtener IDs de planes)
-- ============================================================
SET @plan_basico_id = (SELECT id FROM planes_suscripcion WHERE nombre = 'Plan Básico' LIMIT 1);
SET @plan_profesional_id = (SELECT id FROM planes_suscripcion WHERE nombre = 'Plan Profesional' LIMIT 1);
SET @plan_enterprise_id = (SELECT id FROM planes_suscripcion WHERE nombre = 'Plan Enterprise' LIMIT 1);

-- ============================================================
-- NEGOCIO 1: LICORERÍA DON PEPE (ACTIVO - PLAN BÁSICO)
-- ============================================================
INSERT INTO negocios (
    uuid,
    razon_social,
    nombre_comercial,
    ruc,
    tipo_documento_fiscal,
    representante_legal,
    documento_representante,
    tipo_negocio,
    email,
    telefono,
    direccion,
    ciudad,
    departamento,
    pais,
    codigo_postal,
    url_logo,
    estado,
    esta_activo
) VALUES (
    UUID(),
    'LICORERÍA DON PEPE S.A.C.',
    'Don Pepe Licores',
    '20123456789',
    'RUC',
    'José Pérez García',
    '43215678',
    'Licorería',
    'contacto@donpepe.com',
    '+51987123456',
    'Av. Los Incas 1234, Cercado de Lima',
    'Lima',
    'Lima',
    'PE',
    '15001',
    'https://via.placeholder.com/200x200/4F46E5/FFFFFF?text=DP',
    'activo',
    1
);

SET @negocio_donpepe_id = LAST_INSERT_ID();

-- Sede principal de Don Pepe
INSERT INTO sedes (
    negocio_id,
    codigo,
    nombre,
    direccion,
    ciudad,
    departamento,
    pais,
    telefono,
    es_principal,
    delivery_habilitado,
    recojo_habilitado,
    horario_config,
    esta_activo
) VALUES (
    @negocio_donpepe_id,
    'SEDE-PRINCIPAL',
    'Don Pepe - Sede Principal',
    'Av. Los Incas 1234, Cercado de Lima',
    'Lima',
    'Lima',
    'PE',
    '+51987123456',
    1,
    0,
    1,
    JSON_ARRAY(
        JSON_OBJECT('dia', 1, 'apertura', '08:00', 'cierre', '20:00'),
        JSON_OBJECT('dia', 2, 'apertura', '08:00', 'cierre', '20:00'),
        JSON_OBJECT('dia', 3, 'apertura', '08:00', 'cierre', '20:00'),
        JSON_OBJECT('dia', 4, 'apertura', '08:00', 'cierre', '20:00'),
        JSON_OBJECT('dia', 5, 'apertura', '08:00', 'cierre', '20:00'),
        JSON_OBJECT('dia', 6, 'apertura', '08:00', 'cierre', '22:00'),
        JSON_OBJECT('dia', 7, 'cerrado', true)
    ),
    1
);

-- Suscripción activa de Don Pepe
INSERT INTO suscripciones (
    negocio_id,
    plan_id,
    estado,
    inicio_periodo_actual,
    fin_periodo_actual,
    proxima_fecha_facturacion,
    auto_renovar
) VALUES (
    @negocio_donpepe_id,
    @plan_basico_id,
    'activa',
    CURDATE(),
    DATE_ADD(CURDATE(), INTERVAL 30 DAY),
    DATE_ADD(CURDATE(), INTERVAL 30 DAY),
    1
);

-- ============================================================
-- NEGOCIO 2: LICORES LA BODEGA (ACTIVO - PLAN PROFESIONAL)
-- ============================================================
INSERT INTO negocios (
    uuid,
    razon_social,
    nombre_comercial,
    ruc,
    tipo_documento_fiscal,
    representante_legal,
    documento_representante,
    tipo_negocio,
    email,
    telefono,
    direccion,
    ciudad,
    departamento,
    pais,
    codigo_postal,
    url_logo,
    estado,
    esta_activo
) VALUES (
    UUID(),
    'DISTRIBUIDORA LA BODEGA E.I.R.L.',
    'La Bodega Premium',
    '20987654321',
    'RUC',
    'María Fernández Torres',
    '87654321',
    'Distribuidora',
    'info@labodega.com.pe',
    '+51912345678',
    'Jr. Comercial 456, San Isidro',
    'Lima',
    'Lima',
    'PE',
    '15073',
    'https://via.placeholder.com/200x200/10B981/FFFFFF?text=LB',
    'activo',
    1
);

SET @negocio_labodega_id = LAST_INSERT_ID();

-- Sede principal La Bodega
INSERT INTO sedes (
    negocio_id,
    codigo,
    nombre,
    direccion,
    ciudad,
    departamento,
    pais,
    telefono,
    es_principal,
    delivery_habilitado,
    recojo_habilitado,
    horario_config,
    esta_activo
) VALUES (
    @negocio_labodega_id,
    'LB-01',
    'La Bodega - San Isidro',
    'Jr. Comercial 456, San Isidro',
    'Lima',
    'Lima',
    'PE',
    '+51912345678',
    1,
    1,
    1,
    JSON_ARRAY(
        JSON_OBJECT('dia', 1, 'apertura', '09:00', 'cierre', '21:00'),
        JSON_OBJECT('dia', 2, 'apertura', '09:00', 'cierre', '21:00'),
        JSON_OBJECT('dia', 3, 'apertura', '09:00', 'cierre', '21:00'),
        JSON_OBJECT('dia', 4, 'apertura', '09:00', 'cierre', '21:00'),
        JSON_OBJECT('dia', 5, 'apertura', '09:00', 'cierre', '22:00'),
        JSON_OBJECT('dia', 6, 'apertura', '09:00', 'cierre', '23:00'),
        JSON_OBJECT('dia', 7, 'apertura', '10:00', 'cierre', '20:00')
    ),
    1
);

-- Sede secundaria La Bodega
INSERT INTO sedes (
    negocio_id,
    codigo,
    nombre,
    direccion,
    ciudad,
    departamento,
    pais,
    telefono,
    es_principal,
    delivery_habilitado,
    recojo_habilitado,
    horario_config,
    esta_activo
) VALUES (
    @negocio_labodega_id,
    'LB-02',
    'La Bodega - Miraflores',
    'Av. Larco 789, Miraflores',
    'Lima',
    'Lima',
    'PE',
    '+51912345679',
    0,
    1,
    1,
    JSON_ARRAY(
        JSON_OBJECT('dia', 1, 'apertura', '10:00', 'cierre', '22:00'),
        JSON_OBJECT('dia', 2, 'apertura', '10:00', 'cierre', '22:00'),
        JSON_OBJECT('dia', 3, 'apertura', '10:00', 'cierre', '22:00'),
        JSON_OBJECT('dia', 4, 'apertura', '10:00', 'cierre', '22:00'),
        JSON_OBJECT('dia', 5, 'apertura', '10:00', 'cierre', '23:00'),
        JSON_OBJECT('dia', 6, 'apertura', '10:00', 'cierre', '23:00'),
        JSON_OBJECT('dia', 7, 'apertura', '11:00', 'cierre', '21:00')
    ),
    1
);

-- Suscripción activa de La Bodega
INSERT INTO suscripciones (
    negocio_id,
    plan_id,
    estado,
    inicio_periodo_actual,
    fin_periodo_actual,
    proxima_fecha_facturacion,
    auto_renovar
) VALUES (
    @negocio_labodega_id,
    @plan_profesional_id,
    'activa',
    DATE_SUB(CURDATE(), INTERVAL 15 DAY),
    DATE_ADD(CURDATE(), INTERVAL 15 DAY),
    DATE_ADD(CURDATE(), INTERVAL 15 DAY),
    1
);

-- ============================================================
-- NEGOCIO 3: DISTRIBUIDORA EL IMPERIO (PENDIENTE - SIN PLAN)
-- ============================================================
INSERT INTO negocios (
    uuid,
    razon_social,
    nombre_comercial,
    ruc,
    tipo_documento_fiscal,
    representante_legal,
    documento_representante,
    tipo_negocio,
    email,
    telefono,
    direccion,
    ciudad,
    departamento,
    pais,
    url_logo,
    estado,
    esta_activo
) VALUES (
    UUID(),
    'EL IMPERIO DISTRIBUCIONES S.R.L.',
    'El Imperio',
    '20456789123',
    'RUC',
    'Roberto Castillo Méndez',
    '12345678',
    'Distribuidora',
    'contacto@elimperio.pe',
    '+51998765432',
    'Av. Industrial 321, Callao',
    'Callao',
    'Callao',
    'PE',
    'https://via.placeholder.com/200x200/F59E0B/FFFFFF?text=EI',
    'pendiente',
    1
);

SET @negocio_elimperio_id = LAST_INSERT_ID();

-- Sede principal El Imperio (pendiente)
INSERT INTO sedes (
    negocio_id,
    codigo,
    nombre,
    direccion,
    ciudad,
    departamento,
    pais,
    telefono,
    es_principal,
    delivery_habilitado,
    recojo_habilitado,
    esta_activo
) VALUES (
    @negocio_elimperio_id,
    'EI-MAIN',
    'El Imperio - Sede Principal',
    'Av. Industrial 321, Callao',
    'Callao',
    'Callao',
    'PE',
    '+51998765432',
    1,
    0,
    0,
    1
);

-- NO se crea suscripción para El Imperio (estado pendiente)

-- ============================================================
-- NEGOCIO 4: LICORERÍA PREMIUM WINES (SUSPENDIDO)
-- ============================================================
INSERT INTO negocios (
    uuid,
    razon_social,
    nombre_comercial,
    ruc,
    tipo_documento_fiscal,
    representante_legal,
    documento_representante,
    tipo_negocio,
    email,
    telefono,
    direccion,
    ciudad,
    departamento,
    pais,
    url_logo,
    estado,
    esta_activo
) VALUES (
    UUID(),
    'PREMIUM WINES PERU S.A.',
    'Premium Wines',
    '20111222333',
    'RUC',
    'Carlos Mendoza Ruiz',
    '98765432',
    'Licorería Premium',
    'contacto@premiumwines.pe',
    '+51945678901',
    'Calle Las Viñas 555, Surco',
    'Lima',
    'Lima',
    'PE',
    'https://via.placeholder.com/200x200/EF4444/FFFFFF?text=PW',
    'suspendido',
    0
);

SET @negocio_premiumwines_id = LAST_INSERT_ID();

-- Sede principal Premium Wines
INSERT INTO sedes (
    negocio_id,
    codigo,
    nombre,
    direccion,
    ciudad,
    departamento,
    pais,
    telefono,
    es_principal,
    delivery_habilitado,
    recojo_habilitado,
    esta_activo
) VALUES (
    @negocio_premiumwines_id,
    'PW-001',
    'Premium Wines - Surco',
    'Calle Las Viñas 555, Surco',
    'Lima',
    'Lima',
    'PE',
    '+51945678901',
    1,
    1,
    1,
    0
);

-- Suscripción suspendida de Premium Wines
INSERT INTO suscripciones (
    negocio_id,
    plan_id,
    estado,
    inicio_periodo_actual,
    fin_periodo_actual,
    suspendido_en,
    razon_suspension,
    auto_renovar
) VALUES (
    @negocio_premiumwines_id,
    @plan_profesional_id,
    'suspendida',
    DATE_SUB(CURDATE(), INTERVAL 45 DAY),
    DATE_SUB(CURDATE(), INTERVAL 15 DAY),
    DATE_SUB(CURDATE(), INTERVAL 10 DAY),
    'Falta de pago - 3 facturas vencidas',
    0
);

-- ============================================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================================

-- Ver negocios creados
SELECT 
    id,
    nombre_comercial,
    razon_social,
    ruc,
    email,
    estado,
    esta_activo,
    ciudad,
    departamento
FROM negocios 
ORDER BY creado_en DESC 
LIMIT 10;

-- Ver suscripciones creadas
SELECT 
    s.id,
    n.nombre_comercial,
    p.nombre AS plan,
    s.estado,
    s.inicio_periodo_actual,
    s.fin_periodo_actual,
    s.auto_renovar
FROM suscripciones s
INNER JOIN negocios n ON s.negocio_id = n.id
INNER JOIN planes_suscripcion p ON s.plan_id = p.id
ORDER BY s.creado_en DESC;

-- Ver sedes creadas
SELECT 
    se.id,
    n.nombre_comercial,
    se.codigo,
    se.nombre,
    se.ciudad,
    se.es_principal,
    se.esta_activo
FROM sedes se
INNER JOIN negocios n ON se.negocio_id = n.id
ORDER BY se.creado_en DESC;

-- ============================================================
-- RESUMEN DE NEGOCIOS CREADOS:
-- ============================================================
-- 1. DON PEPE: Activo, Plan Básico, 1 sede
-- 2. LA BODEGA: Activo, Plan Profesional, 2 sedes
-- 3. EL IMPERIO: Pendiente, Sin plan, 1 sede
-- 4. PREMIUM WINES: Suspendido, Plan Profesional (suspendido), 1 sede
-- ============================================================
