-- ============================================================
-- SEEDS BLOQUE 1: INFRAESTRUCTURA BASE, PLATAFORMA SaaS Y NEGOCIOS
-- ============================================================
-- Este archivo contiene datos de prueba para inicializar el sistema
-- Fecha: 17 de febrero de 2026
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 1.1 PLANES DE SUSCRIPCIÓN
-- ============================================================

INSERT INTO planes_suscripcion (
    id, nombre, slug, descripcion, precio, moneda, periodo_facturacion,
    max_sedes, max_usuarios, max_productos, max_almacenes_por_sede,
    permite_pos, permite_tienda_online, permite_delivery, permite_mesas,
    permite_facturacion_electronica, permite_multi_almacen,
    permite_reportes_avanzados, permite_acceso_api,
    esta_activo, version, orden
) VALUES
-- Plan Básico (Pequeña licorería)
(1, 'Plan Básico', 'basico', 'Ideal para pequeñas licorerías con una sola sede', 
 49.00, 'PEN', 'mensual', 
 1, 3, 200, 1,
 1, 0, 0, 0,
 0, 0, 0, 0,
 1, 1, 1),

-- Plan Estándar (Licorerías medianas)
(2, 'Plan Estándar', 'estandar', 'Para licorerías en crecimiento con múltiples puntos de venta', 
 149.00, 'PEN', 'mensual',
 3, 10, 1000, 2,
 1, 1, 1, 0,
 1, 1, 0, 0,
 1, 1, 2),

-- Plan Premium (Cadenas grandes)
(3, 'Plan Premium', 'premium', 'Solución completa para cadenas de licorerías', 
 349.00, 'PEN', 'mensual',
 10, 30, 5000, 5,
 1, 1, 1, 1,
 1, 1, 1, 1,
 1, 1, 3),

-- Plan Anual Básico (Con descuento)
(4, 'Plan Básico Anual', 'basico-anual', 'Plan Básico con facturación anual (2 meses gratis)', 
 490.00, 'PEN', 'anual',
 1, 3, 200, 1,
 1, 0, 0, 0,
 0, 0, 0, 0,
 1, 1, 4),

-- Plan Anual Premium (Con descuento)
(5, 'Plan Premium Anual', 'premium-anual', 'Plan Premium con facturación anual (2 meses gratis)', 
 3490.00, 'PEN', 'anual',
 10, 30, 5000, 5,
 1, 1, 1, 1,
 1, 1, 1, 1,
 1, 1, 5);

-- ============================================================
-- 1.2 NEGOCIOS (LICORERÍAS / TENANTS)
-- ============================================================

INSERT INTO negocios (
    id, uuid, razon_social, nombre_comercial, ruc, tipo_documento_fiscal,
    representante_legal, documento_representante, tipo_negocio,
    email, telefono, direccion, ciudad, departamento, pais,
    codigo_postal, url_logo, color_primario, color_secundario,
    moneda_predeterminada, idioma_predeterminado, zona_horaria,
    formato_fecha, estado, esta_activo
) VALUES
-- Negocio 1: Licorería Don Pedro (Pequeña - Plan Básico)
(1, '550e8400-e29b-41d4-a716-446655440001', 
 'LICORERÍA DON PEDRO S.A.C.', 'Licorería Don Pedro', '20457896321', 'RUC',
 'Pedro Ramirez Garcia', '43567891', 'licoreria',
 'contacto@licoreriaDonpedro.com', '+51 987654321',
 'Av. Los Proceres 567', 'Lima', 'Lima', 'PE',
 '15001', 'https://cdn.licoreriaDonpedro.com/logo.png',
 '#1A1A2E', '#E94560',
 'PEN', 'es', 'America/Lima',
 'DD/MM/YYYY', 'activo', 1),

-- Negocio 2: Licorería Los Andes (Mediana - Plan Estándar)
(2, '550e8400-e29b-41d4-a716-446655440002',
 'LICORERÍA LOS ANDES E.I.R.L.', 'Licorería Los Andes', '20123456789', 'RUC',
 'Maria Elena Torres', '41234567', 'licoreria',
 'info@losandes.com.pe', '+51 912345678',
 'Jr. Comercio 234', 'Arequipa', 'Arequipa', 'PE',
 '04001', 'https://cdn.losandes.com.pe/logo.png',
 '#2C3E50', '#3498DB',
 'PEN', 'es', 'America/Lima',
 'DD/MM/YYYY', 'activo', 1),

-- Negocio 3: DrinkGo Express (Grande - Plan Premium)
(3, '550e8400-e29b-41d4-a716-446655440003',
 'DRINKGO EXPRESS S.A.', 'DrinkGo Express', '20987654321', 'RUC',
 'Carlos Mendoza Silva', '07654321', 'licoreria',
 'contacto@drinkgoexpress.pe', '+51 998877665',
 'Av. Javier Prado 1456', 'Lima', 'Lima', 'PE',
 '15023', 'https://cdn.drinkgoexpress.pe/logo.png',
 '#16A085', '#F39C12',
 'PEN', 'es', 'America/Lima',
 'DD/MM/YYYY', 'activo', 1);

-- ============================================================
-- 1.3 SUSCRIPCIONES DE NEGOCIOS
-- ============================================================

INSERT INTO suscripciones (
    id, negocio_id, plan_id, version_plan, estado,
    inicio_periodo_actual, fin_periodo_actual, proxima_fecha_facturacion,
    auto_renovar, token_metodo_pago
) VALUES
-- Suscripción Licorería Don Pedro (Plan Básico mensual)
(1, 1, 1, 1, 'activa',
 '2026-01-01', '2026-02-01', '2026-02-01',
 1, 'tok_visa_xxxx4567'),

-- Suscripción Licorería Los Andes (Plan Estándar mensual)
(2, 2, 2, 1, 'activa',
 '2025-12-15', '2026-01-15', '2026-01-15',
 1, 'tok_mc_xxxx8912'),

-- Suscripción DrinkGo Express (Plan Premium mensual)
(3, 3, 3, 1, 'activa',
 '2026-01-10', '2026-02-10', '2026-02-10',
 1, 'tok_visa_xxxx3456');

-- ============================================================
-- 1.4 FACTURAS DE SUSCRIPCIÓN
-- ============================================================

INSERT INTO facturas_suscripcion (
    id, suscripcion_id, negocio_id, numero_factura,
    inicio_periodo, fin_periodo, subtotal, monto_impuesto, monto_descuento, total,
    moneda, estado, metodo_pago, referencia_pago, pagado_en, fecha_vencimiento
) VALUES
-- Factura enero 2026 - Don Pedro
(1, 1, 1, 'FS-2026-000001',
 '2026-01-01', '2026-02-01', 41.53, 7.47, 0.00, 49.00,
 'PEN', 'pagada', 'tarjeta_credito', 'PAY-2026-001-ABC123', '2026-01-01 10:30:00', '2026-01-10'),

-- Factura diciembre 2025 - Los Andes
(2, 2, 2, 'FS-2025-000045',
 '2025-12-15', '2026-01-15', 126.27, 22.73, 0.00, 149.00,
 'PEN', 'pagada', 'tarjeta_credito', 'PAY-2025-045-XYZ789', '2025-12-15 09:15:00', '2025-12-25'),

-- Factura enero 2026 - Los Andes
(3, 2, 2, 'FS-2026-000002',
 '2026-01-15', '2026-02-15', 126.27, 22.73, 0.00, 149.00,
 'PEN', 'pagada', 'tarjeta_credito', 'PAY-2026-002-DEF456', '2026-01-15 11:20:00', '2026-01-25'),

-- Factura enero 2026 - DrinkGo Express
(4, 3, 3, 'FS-2026-000003',
 '2026-01-10', '2026-02-10', 295.76, 53.24, 0.00, 349.00,
 'PEN', 'pagada', 'transferencia_bancaria', 'TRF-2026-003-GHI789', '2026-01-10 14:45:00', '2026-01-20'),

-- Factura febrero 2026 - Don Pedro (Pendiente)
(5, 1, 1, 'FS-2026-000004',
 '2026-02-01', '2026-03-01', 41.53, 7.47, 0.00, 49.00,
 'PEN', 'pendiente', NULL, NULL, NULL, '2026-02-10');

-- ============================================================
-- 1.5 CONFIGURACIÓN GLOBAL DE PLATAFORMA
-- ============================================================

INSERT INTO configuracion_global_plataforma (
    id, clave_configuracion, valor_configuracion, tipo_valor, categoria,
    descripcion, es_editable, version
) VALUES
(1, 'plataforma.nombre', 'DrinkGo', 'texto', 'general',
 'Nombre de la plataforma SaaS', 1, 1),

(2, 'plataforma.version', '1.0.0', 'texto', 'general',
 'Versión actual de la plataforma', 0, 1),

(3, 'plataforma.email_soporte', 'soporte@drinkgo.pe', 'texto', 'general',
 'Email de soporte técnico', 1, 1),

(4, 'plataforma.telefono_soporte', '+51 1 234-5678', 'texto', 'general',
 'Teléfono de soporte técnico', 1, 1),

(5, 'facturacion.igv_peru', '18', 'numero', 'facturacion',
 'Porcentaje de IGV en Perú', 1, 1),

(6, 'facturacion.dias_gracia_pago', '5', 'numero', 'facturacion',
 'Días de gracia para pago de suscripción', 1, 1),

(7, 'facturacion.intentos_max_reintento', '3', 'numero', 'facturacion',
 'Intentos máximos de cobro automático', 1, 1),

(8, 'sistema.mantenimiento_activo', 'false', 'booleano', 'sistema',
 'Indica si el sistema está en mantenimiento', 1, 1),

(9, 'sistema.mensaje_mantenimiento', 'Sistema en mantenimiento programado', 'texto', 'sistema',
 'Mensaje mostrado durante mantenimiento', 1, 1),

(10, 'seguridad.max_intentos_login', '5', 'numero', 'seguridad',
 'Intentos máximos de login antes de bloqueo', 1, 1),

(11, 'seguridad.duracion_bloqueo_minutos', '30', 'numero', 'seguridad',
 'Duración del bloqueo de cuenta (minutos)', 1, 1),

(12, 'seguridad.duracion_sesion_horas', '8', 'numero', 'seguridad',
 'Duración de sesión de usuario (horas)', 1, 1),

(13, 'api.rate_limit_requests', '1000', 'numero', 'api',
 'Límite de requests por hora por API key', 1, 1),

(14, 'api.version_actual', 'v1', 'texto', 'api',
 'Versión actual de la API REST', 0, 1),

(15, 'notificaciones.email_habilitado', 'true', 'booleano', 'notificaciones',
 'Habilita envío de emails', 1, 1),

(16, 'notificaciones.sms_habilitado', 'false', 'booleano', 'notificaciones',
 'Habilita envío de SMS', 1, 1),

(17, 'almacenamiento.max_tamaño_imagen_mb', '5', 'numero', 'almacenamiento',
 'Tamaño máximo de imagen en MB', 1, 1),

(18, 'almacenamiento.formatos_imagen_permitidos', '["jpg","jpeg","png","webp"]', 'json', 'almacenamiento',
 'Formatos de imagen permitidos', 1, 1),

(19, 'moneda.tasa_cambio_usd_pen', '3.75', 'numero', 'moneda',
 'Tasa de cambio USD a PEN', 1, 1),

(20, 'periodo_prueba.duracion_dias', '14', 'numero', 'suscripciones',
 'Duración del periodo de prueba (días)', 1, 1);

-- ============================================================
-- FIN SEEDS BLOQUE 1
-- ============================================================