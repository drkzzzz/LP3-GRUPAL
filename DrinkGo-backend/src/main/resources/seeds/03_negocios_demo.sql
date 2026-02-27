-- ============================================================
-- SEED: NEGOCIOS DEMO Y SUSCRIPCIONES (IDEMPOTENTE)
-- Usa WHERE NOT EXISTS para no duplicar al reiniciar backend
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- VARIABLES DE REFERENCIA
-- ============================================================
SET @plan_basico_id      = (SELECT id FROM planes_suscripcion WHERE nombre = 'Plan Básico'       LIMIT 1);
SET @plan_profesional_id = (SELECT id FROM planes_suscripcion WHERE nombre = 'Plan Profesional'  LIMIT 1);
SET @plan_enterprise_id  = (SELECT id FROM planes_suscripcion WHERE nombre = 'Plan Enterprise'   LIMIT 1);

-- ============================================================
-- NEGOCIO 1: LICORERÍA DON PEPE (ACTIVO - PLAN BÁSICO)
-- ============================================================
INSERT INTO negocios (uuid, razon_social, nombre_comercial, ruc, tipo_documento_fiscal,
    representante_legal, documento_representante, tipo_negocio, email, telefono,
    direccion, ciudad, departamento, pais, codigo_postal, url_logo, estado, esta_activo)
SELECT UUID(), 'LICORERÍA DON PEPE S.A.C.', 'Don Pepe Licores', '20123456789', 'RUC',
    'José Pérez García', '43215678', 'Licorería', 'contacto@donpepe.com', '987123456',
    'Av. Los Incas 1234, Cercado de Lima', 'Lima', 'Lima', 'PE', '15001',
    'https://via.placeholder.com/200x200/4F46E5/FFFFFF?text=DP', 'activo', 1
WHERE NOT EXISTS (SELECT 1 FROM negocios WHERE ruc = '20123456789');

UPDATE negocios SET
    razon_social = 'LICORERÍA DON PEPE S.A.C.',
    representante_legal = 'José Pérez García',
    tipo_negocio = 'Licorería'
WHERE ruc = '20123456789';

SET @negocio_donpepe_id = (SELECT id FROM negocios WHERE ruc = '20123456789' LIMIT 1);

INSERT INTO sedes (negocio_id, codigo, nombre, direccion, ciudad, departamento, pais,
    telefono, es_principal, delivery_habilitado, recojo_habilitado, esta_activo)
SELECT @negocio_donpepe_id, 'SEDE-PRINCIPAL', 'Don Pepe - Sede Principal',
    'Av. Los Incas 1234, Cercado de Lima', 'Lima', 'Lima', 'PE', '987123456', 1, 0, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sedes WHERE codigo = 'SEDE-PRINCIPAL');

INSERT INTO suscripciones (negocio_id, plan_id, estado, inicio_periodo_actual,
    fin_periodo_actual, proxima_fecha_facturacion, auto_renovar)
SELECT @negocio_donpepe_id, @plan_enterprise_id, 'activa', CURDATE(),
    DATE_ADD(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1
WHERE NOT EXISTS (SELECT 1 FROM suscripciones WHERE negocio_id = @negocio_donpepe_id);

UPDATE suscripciones SET plan_id = @plan_enterprise_id
WHERE negocio_id = @negocio_donpepe_id;

-- ============================================================
-- NEGOCIO 2: DISTRIBUIDORA LA BODEGA (ACTIVO - PLAN PROFESIONAL)
-- ============================================================
INSERT INTO negocios (uuid, razon_social, nombre_comercial, ruc, tipo_documento_fiscal,
    representante_legal, documento_representante, tipo_negocio, email, telefono,
    direccion, ciudad, departamento, pais, codigo_postal, url_logo, estado, esta_activo)
SELECT UUID(), 'DISTRIBUIDORA LA BODEGA E.I.R.L.', 'La Bodega Premium', '20987654321', 'RUC',
    'María Fernández Torres', '87654321', 'Distribuidora', 'info@labodega.com.pe', '912345678',
    'Jr. Comercial 456, San Isidro', 'Lima', 'Lima', 'PE', '15073',
    'https://via.placeholder.com/200x200/10B981/FFFFFF?text=LB', 'activo', 1
WHERE NOT EXISTS (SELECT 1 FROM negocios WHERE ruc = '20987654321');

UPDATE negocios SET representante_legal = 'María Fernández Torres'
WHERE ruc = '20987654321';

SET @negocio_labodega_id = (SELECT id FROM negocios WHERE ruc = '20987654321' LIMIT 1);

INSERT INTO sedes (negocio_id, codigo, nombre, direccion, ciudad, departamento, pais,
    telefono, es_principal, delivery_habilitado, recojo_habilitado, esta_activo)
SELECT @negocio_labodega_id, 'LB-01', 'La Bodega - San Isidro',
    'Jr. Comercial 456, San Isidro', 'Lima', 'Lima', 'PE', '912345678', 1, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sedes WHERE codigo = 'LB-01');

INSERT INTO sedes (negocio_id, codigo, nombre, direccion, ciudad, departamento, pais,
    telefono, es_principal, delivery_habilitado, recojo_habilitado, esta_activo)
SELECT @negocio_labodega_id, 'LB-02', 'La Bodega - Miraflores',
    'Av. Larco 789, Miraflores', 'Lima', 'Lima', 'PE', '912345679', 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sedes WHERE codigo = 'LB-02');

INSERT INTO suscripciones (negocio_id, plan_id, estado, inicio_periodo_actual,
    fin_periodo_actual, proxima_fecha_facturacion, auto_renovar)
SELECT @negocio_labodega_id, @plan_profesional_id, 'activa',
    DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 15 DAY),
    DATE_ADD(CURDATE(), INTERVAL 15 DAY), 1
WHERE NOT EXISTS (SELECT 1 FROM suscripciones WHERE negocio_id = @negocio_labodega_id);

-- ============================================================
-- NEGOCIO 3: EL IMPERIO (ACTIVO - PLAN ENTERPRISE)
-- ============================================================
INSERT INTO negocios (uuid, razon_social, nombre_comercial, ruc, tipo_documento_fiscal,
    representante_legal, documento_representante, tipo_negocio, email, telefono,
    direccion, ciudad, departamento, pais, url_logo, estado, esta_activo)
SELECT UUID(), 'EL IMPERIO DISTRIBUCIONES S.R.L.', 'El Imperio', '20456789123', 'RUC',
    'Roberto Castillo Méndez', '12345678', 'Distribuidora', 'contacto@elimperio.pe', '998765432',
    'Av. Industrial 321, Callao', 'Callao', 'Callao', 'PE',
    'https://via.placeholder.com/200x200/F59E0B/FFFFFF?text=EI', 'activo', 1
WHERE NOT EXISTS (SELECT 1 FROM negocios WHERE ruc = '20456789123');

UPDATE negocios SET representante_legal = 'Roberto Castillo Méndez'
WHERE ruc = '20456789123';

SET @negocio_elimperio_id = (SELECT id FROM negocios WHERE ruc = '20456789123' LIMIT 1);

INSERT INTO sedes (negocio_id, codigo, nombre, direccion, ciudad, departamento, pais,
    telefono, es_principal, delivery_habilitado, recojo_habilitado, esta_activo)
SELECT @negocio_elimperio_id, 'EI-MAIN', 'El Imperio - Sede Principal',
    'Av. Industrial 321, Callao', 'Callao', 'Callao', 'PE', '998765432', 1, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sedes WHERE codigo = 'EI-MAIN');

INSERT INTO suscripciones (negocio_id, plan_id, estado, inicio_periodo_actual,
    fin_periodo_actual, proxima_fecha_facturacion, auto_renovar)
SELECT @negocio_elimperio_id, @plan_enterprise_id, 'activa',
    DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 305 DAY),
    DATE_ADD(CURDATE(), INTERVAL 305 DAY), 1
WHERE NOT EXISTS (SELECT 1 FROM suscripciones WHERE negocio_id = @negocio_elimperio_id);

-- ============================================================
-- NEGOCIO 4: PREMIUM WINES (SUSPENDIDO)
-- ============================================================
INSERT INTO negocios (uuid, razon_social, nombre_comercial, ruc, tipo_documento_fiscal,
    representante_legal, documento_representante, tipo_negocio, email, telefono,
    direccion, ciudad, departamento, pais, url_logo, estado, esta_activo)
SELECT UUID(), 'PREMIUM WINES PERU S.A.', 'Premium Wines', '20111222333', 'RUC',
    'Carlos Mendoza Ruiz', '98765432', 'Licorería Premium', 'contacto@premiumwines.pe', '945678901',
    'Calle Las Viñas 555, Surco', 'Lima', 'Lima', 'PE',
    'https://via.placeholder.com/200x200/EF4444/FFFFFF?text=PW', 'suspendido', 0
WHERE NOT EXISTS (SELECT 1 FROM negocios WHERE ruc = '20111222333');

UPDATE negocios SET
    tipo_negocio = 'Licorería Premium',
    direccion = 'Calle Las Viñas 555, Surco'
WHERE ruc = '20111222333';

SET @negocio_premiumwines_id = (SELECT id FROM negocios WHERE ruc = '20111222333' LIMIT 1);

INSERT INTO sedes (negocio_id, codigo, nombre, direccion, ciudad, departamento, pais,
    telefono, es_principal, delivery_habilitado, recojo_habilitado, esta_activo)
SELECT @negocio_premiumwines_id, 'PW-001', 'Premium Wines - Surco',
    'Calle Las Viñas 555, Surco', 'Lima', 'Lima', 'PE', '945678901', 1, 1, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM sedes WHERE codigo = 'PW-001');

INSERT INTO suscripciones (negocio_id, plan_id, estado, inicio_periodo_actual,
    fin_periodo_actual, suspendido_en, razon_suspension, auto_renovar)
SELECT @negocio_premiumwines_id, @plan_profesional_id, 'suspendida',
    DATE_SUB(CURDATE(), INTERVAL 45 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY),
    DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'Falta de pago - 3 facturas vencidas', 0
WHERE NOT EXISTS (SELECT 1 FROM suscripciones WHERE negocio_id = @negocio_premiumwines_id);

-- ============================================================
-- ROLES Y USUARIOS ADMIN PARA CADA NEGOCIO DEMO
-- Contraseña de todos: Admin123!
-- Hash BCrypt de Admin123!: $2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC
-- ============================================================

-- Rol Administrador por negocio
INSERT INTO roles (negocio_id, nombre, descripcion, es_rol_sistema, esta_activo)
SELECT @negocio_donpepe_id, 'Administrador', 'Acceso total al negocio', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE negocio_id = @negocio_donpepe_id AND nombre = 'Administrador');

INSERT INTO roles (negocio_id, nombre, descripcion, es_rol_sistema, esta_activo)
SELECT @negocio_labodega_id, 'Administrador', 'Acceso total al negocio', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE negocio_id = @negocio_labodega_id AND nombre = 'Administrador');

INSERT INTO roles (negocio_id, nombre, descripcion, es_rol_sistema, esta_activo)
SELECT @negocio_elimperio_id, 'Administrador', 'Acceso total al negocio', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE negocio_id = @negocio_elimperio_id AND nombre = 'Administrador');

INSERT INTO roles (negocio_id, nombre, descripcion, es_rol_sistema, esta_activo)
SELECT @negocio_premiumwines_id, 'Administrador', 'Acceso total al negocio', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE negocio_id = @negocio_premiumwines_id AND nombre = 'Administrador');

-- IDs de roles
SET @rol_donpepe_id      = (SELECT id FROM roles WHERE negocio_id = @negocio_donpepe_id      AND nombre = 'Administrador' LIMIT 1);
SET @rol_labodega_id     = (SELECT id FROM roles WHERE negocio_id = @negocio_labodega_id     AND nombre = 'Administrador' LIMIT 1);
SET @rol_elimperio_id    = (SELECT id FROM roles WHERE negocio_id = @negocio_elimperio_id    AND nombre = 'Administrador' LIMIT 1);
SET @rol_premiumwines_id = (SELECT id FROM roles WHERE negocio_id = @negocio_premiumwines_id AND nombre = 'Administrador' LIMIT 1);

-- Usuario admin por negocio
INSERT INTO usuarios (uuid, negocio_id, email, hash_contrasena, nombres, apellidos, esta_activo)
SELECT UUID(), @negocio_donpepe_id, 'admin@donpepe.com',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC',
    'José', 'Pérez', 1
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE negocio_id = @negocio_donpepe_id AND email = 'admin@donpepe.com');

INSERT INTO usuarios (uuid, negocio_id, email, hash_contrasena, nombres, apellidos, esta_activo)
SELECT UUID(), @negocio_labodega_id, 'admin@labodega.com.pe',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC',
    'María', 'Fernández', 1
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE negocio_id = @negocio_labodega_id AND email = 'admin@labodega.com.pe');

INSERT INTO usuarios (uuid, negocio_id, email, hash_contrasena, nombres, apellidos, esta_activo)
SELECT UUID(), @negocio_elimperio_id, 'admin@elimperio.pe',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC',
    'Roberto', 'Castillo', 1
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE negocio_id = @negocio_elimperio_id AND email = 'admin@elimperio.pe');

INSERT INTO usuarios (uuid, negocio_id, email, hash_contrasena, nombres, apellidos, esta_activo)
SELECT UUID(), @negocio_premiumwines_id, 'admin@premiumwines.pe',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC',
    'Carlos', 'Mendoza', 1
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE negocio_id = @negocio_premiumwines_id AND email = 'admin@premiumwines.pe');

-- IDs de usuarios creados
SET @usr_donpepe_id      = (SELECT id FROM usuarios WHERE negocio_id = @negocio_donpepe_id      AND email = 'admin@donpepe.com'      LIMIT 1);
SET @usr_labodega_id     = (SELECT id FROM usuarios WHERE negocio_id = @negocio_labodega_id     AND email = 'admin@labodega.com.pe'  LIMIT 1);
SET @usr_elimperio_id    = (SELECT id FROM usuarios WHERE negocio_id = @negocio_elimperio_id    AND email = 'admin@elimperio.pe'     LIMIT 1);
SET @usr_premiumwines_id = (SELECT id FROM usuarios WHERE negocio_id = @negocio_premiumwines_id AND email = 'admin@premiumwines.pe'  LIMIT 1);

-- Asignar rol Administrador a cada usuario
INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT @usr_donpepe_id, @rol_donpepe_id
WHERE NOT EXISTS (SELECT 1 FROM usuarios_roles WHERE usuario_id = @usr_donpepe_id AND rol_id = @rol_donpepe_id);

INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT @usr_labodega_id, @rol_labodega_id
WHERE NOT EXISTS (SELECT 1 FROM usuarios_roles WHERE usuario_id = @usr_labodega_id AND rol_id = @rol_labodega_id);

INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT @usr_elimperio_id, @rol_elimperio_id
WHERE NOT EXISTS (SELECT 1 FROM usuarios_roles WHERE usuario_id = @usr_elimperio_id AND rol_id = @rol_elimperio_id);

INSERT INTO usuarios_roles (usuario_id, rol_id)
SELECT @usr_premiumwines_id, @rol_premiumwines_id
WHERE NOT EXISTS (SELECT 1 FROM usuarios_roles WHERE usuario_id = @usr_premiumwines_id AND rol_id = @rol_premiumwines_id);

