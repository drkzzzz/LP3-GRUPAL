-- ============================================================
-- DRINKGO - SISTEMA MULTI-TENANT PARA LICORERÍAS
-- Base de datos relacional MySQL/MariaDB (XAMPP compatible)
-- Refactorizado: 2026-02-13
-- Idioma: Español técnico
-- ============================================================
-- BLOQUE 1: INFRAESTRUCTURA BASE, PLATAFORMA SaaS Y NEGOCIOS
-- ============================================================

DROP DATABASE IF EXISTS drinkgo_db;
CREATE DATABASE drinkgo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE drinkgo_db;

-- ============================================================
-- 1.1 PLANES DE SUSCRIPCIÓN
-- ============================================================

CREATE TABLE planes_suscripcion (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NULL,
    precio DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    periodo_facturacion ENUM('mensual','anual') NOT NULL DEFAULT 'mensual',
    max_sedes INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Máximo de sedes permitidas',
    max_usuarios INT UNSIGNED NOT NULL DEFAULT 5,
    max_productos INT UNSIGNED NOT NULL DEFAULT 500,
    max_almacenes_por_sede INT UNSIGNED NOT NULL DEFAULT 2,
    permite_pos TINYINT(1) NOT NULL DEFAULT 1,
    permite_tienda_online TINYINT(1) NOT NULL DEFAULT 0,
    permite_delivery TINYINT(1) NOT NULL DEFAULT 0,
    permite_mesas TINYINT(1) NOT NULL DEFAULT 0,
    permite_facturacion_electronica TINYINT(1) NOT NULL DEFAULT 0,
    permite_multi_almacen TINYINT(1) NOT NULL DEFAULT 0,
    permite_reportes_avanzados TINYINT(1) NOT NULL DEFAULT 0,
    permite_acceso_api TINYINT(1) NOT NULL DEFAULT 0,
    funcionalidades_json JSON NULL COMMENT 'Funcionalidades extra en formato JSON',
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    orden INT NOT NULL DEFAULT 0,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plsus_activo (esta_activo)
) ENGINE=InnoDB COMMENT='Planes de suscripción de la plataforma (RF-PLT-002..005)';

-- ============================================================
-- 1.2 NEGOCIOS (LICORERÍAS / TENANTS)
-- ============================================================

CREATE TABLE negocios (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    razon_social VARCHAR(200) NOT NULL,
    nombre_comercial VARCHAR(200) NULL COMMENT 'Nombre comercial',
    ruc VARCHAR(20) NULL COMMENT 'RUC',
    tipo_documento_fiscal ENUM('RUC','DNI','CE','OTRO') NOT NULL DEFAULT 'RUC',
    representante_legal VARCHAR(200) NULL,
    documento_representante VARCHAR(20) NULL,
    tipo_negocio VARCHAR(100) NULL,
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(30) NULL,
    direccion VARCHAR(300) NULL,
    ciudad VARCHAR(100) NULL,
    departamento VARCHAR(100) NULL,
    pais VARCHAR(3) NOT NULL DEFAULT 'PE',
    codigo_postal VARCHAR(20) NULL,
    url_logo VARCHAR(500) NULL,
    estado ENUM('activo','suspendido','cancelado','pendiente') NOT NULL DEFAULT 'pendiente',
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    eliminado_en TIMESTAMP NULL,
    INDEX idx_neg_estado (estado),
    INDEX idx_neg_uuid (uuid),
    INDEX idx_neg_ruc (ruc)
) ENGINE=InnoDB COMMENT='Negocios/Licorerías - Entidad principal multi-tenant (RF-PLT-001, RF-ADM-001..002)';

-- ============================================================
-- 1.3 SUSCRIPCIONES DE NEGOCIOS
-- ============================================================

CREATE TABLE suscripciones (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    plan_id BIGINT UNSIGNED NOT NULL,
    estado ENUM('activa','vencida','suspendida','cancelada','expirada') NOT NULL DEFAULT 'activa',
    inicio_periodo_actual DATE NOT NULL,
    fin_periodo_actual DATE NOT NULL,
    proxima_fecha_facturacion DATE NULL,
    cancelado_en TIMESTAMP NULL,
    razon_cancelacion TEXT NULL,
    suspendido_en TIMESTAMP NULL,
    razon_suspension TEXT NULL,
    auto_renovar TINYINT(1) NOT NULL DEFAULT 1,
    token_metodo_pago VARCHAR(255) NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_susc_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_susc_plan FOREIGN KEY (plan_id) REFERENCES planes_suscripcion(id),
    INDEX idx_susc_negocio (negocio_id),
    INDEX idx_susc_estado (estado),
    INDEX idx_susc_prox_fact (proxima_fecha_facturacion)
) ENGINE=InnoDB COMMENT='Suscripciones activas de cada negocio (RF-PLT-006, RF-FAC-001..002)';

CREATE TABLE facturas_suscripcion (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    suscripcion_id BIGINT UNSIGNED NOT NULL,
    negocio_id BIGINT UNSIGNED NOT NULL,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    inicio_periodo DATE NOT NULL,
    fin_periodo DATE NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    estado ENUM('borrador','pendiente','pagada','fallida','reembolsada','anulada') NOT NULL DEFAULT 'pendiente',
    metodo_pago VARCHAR(50) NULL,
    referencia_pago VARCHAR(255) NULL,
    pagado_en TIMESTAMP NULL,
    notas TEXT NULL,
    emitido_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento DATE NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_fsusc_suscripcion FOREIGN KEY (suscripcion_id) REFERENCES suscripciones(id),
    CONSTRAINT fk_fsusc_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    INDEX idx_fsusc_negocio (negocio_id),
    INDEX idx_fsusc_estado (estado),
    INDEX idx_fsusc_vencimiento (fecha_vencimiento)
) ENGINE=InnoDB COMMENT='Facturas de suscripción plataforma (RF-FAC-001)';

-- ============================================================
-- BLOQUE 2: USUARIOS, ROLES, PERMISOS Y SEGURIDAD
-- ============================================================

-- ============================================================
-- 2.1 USUARIOS DE PLATAFORMA (SUPERADMIN)
-- ============================================================

CREATE TABLE usuarios_plataforma (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    hash_contrasena VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NULL,
    rol ENUM('superadmin','soporte_plataforma','visualizador_plataforma') NOT NULL DEFAULT 'superadmin',
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    ultimo_acceso_en TIMESTAMP NULL,
    contrasena_cambiada_en TIMESTAMP NULL,
    intentos_fallidos_acceso INT UNSIGNED NOT NULL DEFAULT 0,
    bloqueado_hasta TIMESTAMP NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_uplat_email (email),
    INDEX idx_uplat_rol (rol)
) ENGINE=InnoDB COMMENT='Usuarios de la plataforma (SuperAdmin y soporte)';

-- ============================================================
-- 2.2 MÓDULOS Y PERMISOS DEL SISTEMA
-- ============================================================

CREATE TABLE modulos_sistema (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NULL,
    modulo_padre_id BIGINT UNSIGNED NULL,
    icono VARCHAR(50) NULL,
    orden INT NOT NULL DEFAULT 0,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_modsys_padre FOREIGN KEY (modulo_padre_id) REFERENCES modulos_sistema(id),
    INDEX idx_modsys_codigo (codigo)
) ENGINE=InnoDB COMMENT='Módulos del sistema para control de acceso';

CREATE TABLE permisos_sistema (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    modulo_id BIGINT UNSIGNED NOT NULL,
    codigo VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(300) NULL,
    tipo_accion ENUM('ver','crear','editar','eliminar','exportar','aprobar','configurar','completo') NOT NULL DEFAULT 'ver',
    CONSTRAINT fk_permsys_modulo FOREIGN KEY (modulo_id) REFERENCES modulos_sistema(id),
    INDEX idx_permsys_codigo (codigo),
    INDEX idx_permsys_modulo (modulo_id)
) ENGINE=InnoDB COMMENT='Permisos granulares por módulo (RF-ADM-017)';

CREATE TABLE modulos_negocio (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    modulo_id BIGINT UNSIGNED NOT NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    activado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    desactivado_en TIMESTAMP NULL,
    CONSTRAINT fk_modneg_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_modneg_modulo FOREIGN KEY (modulo_id) REFERENCES modulos_sistema(id),
    UNIQUE KEY uk_modneg_negocio_modulo (negocio_id, modulo_id),
    INDEX idx_modneg_negocio (negocio_id),
    INDEX idx_modneg_modulo (modulo_id),
    INDEX idx_modneg_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Módulos activos por negocio según plan de suscripción';

-- ============================================================
-- 2.3 ROLES (POR NEGOCIO)
-- ============================================================

CREATE TABLE roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NULL,
    es_rol_sistema TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Roles predefinidos del sistema',
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rol_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_rol_negocio_nombre (negocio_id, nombre),
    INDEX idx_rol_negocio (negocio_id)
) ENGINE=InnoDB COMMENT='Roles por negocio (RF-ADM-016)';

CREATE TABLE roles_permisos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    rol_id BIGINT UNSIGNED NOT NULL,
    permiso_id BIGINT UNSIGNED NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rolperm_rol FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rolperm_permiso FOREIGN KEY (permiso_id) REFERENCES permisos_sistema(id),
    UNIQUE KEY uk_rol_permiso (rol_id, permiso_id),
    INDEX idx_rolperm_rol (rol_id)
) ENGINE=InnoDB COMMENT='Permisos asignados a roles (RF-ADM-017)';

-- ============================================================
-- 2.4 USUARIOS DE NEGOCIO
-- ============================================================

CREATE TABLE usuarios (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    negocio_id BIGINT UNSIGNED NOT NULL,
    email VARCHAR(150) NOT NULL,
    hash_contrasena VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    tipo_documento ENUM('DNI','CE','PASAPORTE','OTRO') NULL,
    numero_documento VARCHAR(20) NULL,
    telefono VARCHAR(30) NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    ultimo_acceso_en TIMESTAMP NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    eliminado_en TIMESTAMP NULL,
    CONSTRAINT fk_usr_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_usr_negocio_email (negocio_id, email),
    INDEX idx_usr_negocio (negocio_id),
    INDEX idx_usr_email (email),
    INDEX idx_usr_uuid (uuid),
    INDEX idx_usr_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Usuarios de cada negocio (RF-ADM-011..013, RF-ADM-020..023)';

CREATE TABLE usuarios_roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    rol_id BIGINT UNSIGNED NOT NULL,
    asignado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    asignado_por BIGINT UNSIGNED NULL,
    CONSTRAINT fk_usrrol_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usrrol_rol FOREIGN KEY (rol_id) REFERENCES roles(id),
    UNIQUE KEY uk_usuario_rol (usuario_id, rol_id),
    INDEX idx_usrrol_usuario (usuario_id)
) ENGINE=InnoDB COMMENT='Roles asignados a usuarios (RF-ADM-014)';

CREATE TABLE sesiones_usuario (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    hash_token VARCHAR(500) NOT NULL UNIQUE COMMENT 'Token JWT hasheado',
    direccion_ip VARCHAR(45) NULL,
    agente_usuario VARCHAR(500) NULL,
    info_dispositivo VARCHAR(200) NULL,
    expira_en TIMESTAMP NOT NULL,
    ultima_actividad_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sesus_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_sesus_usuario (usuario_id),
    INDEX idx_sesus_token (hash_token),
    INDEX idx_sesus_activo (esta_activo),
    INDEX idx_sesus_expira (expira_en)
) ENGINE=InnoDB COMMENT='Sesiones de usuario para autenticación JWT (RF-ADM-020)';

-- ============================================================
-- 2.5 AUDITORÍA
-- ============================================================

CREATE TABLE registros_auditoria (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NULL COMMENT 'NULL para acciones de plataforma',
    usuario_id BIGINT UNSIGNED NULL,
    usuario_plataforma_id BIGINT UNSIGNED NULL,
    accion VARCHAR(100) NOT NULL,
    tipo_entidad VARCHAR(100) NOT NULL,
    entidad_id BIGINT UNSIGNED NULL,
    valores_anteriores JSON NULL,
    valores_nuevos JSON NULL,
    direccion_ip VARCHAR(45) NULL,
    agente_usuario VARCHAR(500) NULL,
    modulo VARCHAR(100) NULL,
    descripcion TEXT NULL,
    severidad ENUM('info','advertencia','critico') NOT NULL DEFAULT 'info',
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_regaud_negocio (negocio_id),
    INDEX idx_regaud_usuario (usuario_id),
    INDEX idx_regaud_accion (accion),
    INDEX idx_regaud_entidad (tipo_entidad, entidad_id),
    INDEX idx_regaud_creado (creado_en),
    INDEX idx_regaud_modulo (modulo),
    INDEX idx_regaud_severidad (severidad)
) ENGINE=InnoDB COMMENT='Log de auditoría completo (RF-L-002, RF-ADM-018)';

-- ============================================================
-- BLOQUE 3: SEDES, ALMACENES, HORARIOS Y CONFIGURACIÓN DE NEGOCIO
-- ============================================================

-- ============================================================
-- 3.1 SEDES
-- ============================================================

CREATE TABLE sedes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(300) NOT NULL,
    ciudad VARCHAR(100) NULL,
    departamento VARCHAR(100) NULL,
    pais VARCHAR(3) NOT NULL DEFAULT 'PE',
    codigo_postal VARCHAR(20) NULL,
    latitud DECIMAL(10,8) NULL,
    longitud DECIMAL(11,8) NULL,
    telefono VARCHAR(30) NULL,
    usuario_gerente_id BIGINT UNSIGNED NULL,
    es_principal TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Sede principal',
    delivery_habilitado TINYINT(1) NOT NULL DEFAULT 0,
    recojo_habilitado TINYINT(1) NOT NULL DEFAULT 0,
    horario_config JSON NULL COMMENT 'Horarios por día: [{"dia": 1, "apertura": "08:00", "cierre": "20:00"}, {"dia": 7, "cerrado": true}]',
    calendario_especial JSON NULL COMMENT 'Fechas especiales: [{"fecha": "2024-12-25", "motivo": "Navidad", "cerrado": true}, {"fecha": "2024-12-31", "horario": "08:00-13:00"}]',
    configuracion JSON NULL COMMENT 'Políticas y configuraciones: {"edad_minima": 18, "pet_friendly": false, "codigo_vestimenta": "casual", "dias_restringidos": [0,6]}',
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    desactivado_en DATETIME NULL,
    CONSTRAINT fk_sede_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_sede_gerente FOREIGN KEY (usuario_gerente_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    UNIQUE KEY uk_sede_negocio_codigo (negocio_id, codigo),
    INDEX idx_sede_negocio (negocio_id),
    INDEX idx_sede_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Sedes del negocio (RF-ADM-003..005, RF-ADM-010)';

CREATE TABLE usuarios_sedes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    es_predeterminado TINYINT(1) NOT NULL DEFAULT 0,
    asignado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    asignado_por BIGINT UNSIGNED NULL,
    CONSTRAINT fk_usrsede_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_usrsede_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    UNIQUE KEY uk_usuario_sede (usuario_id, sede_id),
    INDEX idx_usrsede_usuario (usuario_id),
    INDEX idx_usrsede_sede (sede_id)
) ENGINE=InnoDB COMMENT='Asignación de usuarios a sedes (RF-ADM-015)';

-- ============================================================
-- 3.2 ALMACENES
-- ============================================================

CREATE TABLE almacenes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(300) NULL,
    es_predeterminado TINYINT(1) NOT NULL DEFAULT 0,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_alm_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_alm_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    UNIQUE KEY uk_alm_negocio_codigo (negocio_id, codigo),
    INDEX idx_alm_sede (sede_id),
    INDEX idx_alm_negocio (negocio_id)
) ENGINE=InnoDB COMMENT='Almacenes por sede (RF-ADM-006)';

-- ============================================================
-- 3.3 HORARIOS Y RESTRICCIONES (Movidos a JSON en tabla sedes)
-- ============================================================
-- Los horarios regulares ahora están en: sedes.horario_config
-- Los horarios especiales ahora están en: sedes.calendario_especial
-- Las restricciones de alcohol ahora están en: sedes.configuracion

-- ============================================================
-- 3.5 ZONAS DE DELIVERY
-- ============================================================

CREATE TABLE zonas_delivery (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300) NULL,
    tarifa_delivery DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_minimo_pedido DECIMAL(10,2) NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_zdel_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_zdel_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    INDEX idx_zdel_negocio (negocio_id),
    INDEX idx_zdel_sede (sede_id)
) ENGINE=InnoDB COMMENT='Zonas de delivery por sede (RF-ADM-009)';

-- ============================================================
-- 3.6 MESAS (PARA CONSUMO EN LOCAL)
-- ============================================================

CREATE TABLE mesas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sede_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(100) NOT NULL COMMENT 'Nombre/número de la mesa: Mesa 1, VIP 3, Terraza A',
    capacidad INT UNSIGNED NOT NULL DEFAULT 4,
    estado ENUM('disponible','ocupada','reservada','mantenimiento') NOT NULL DEFAULT 'disponible',
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mesa_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    UNIQUE KEY uk_mesa_sede_nombre (sede_id, nombre),
    INDEX idx_mesa_sede (sede_id),
    INDEX idx_mesa_estado (sede_id, estado)
) ENGINE=InnoDB COMMENT='Mesas por sede - Sistema minimalista (RF-ADM-010)';

-- ============================================================
-- 3.9 MÉTODOS DE PAGO POR NEGOCIO
-- ============================================================

CREATE TABLE metodos_pago (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    tipo ENUM('efectivo','tarjeta_credito','tarjeta_debito','transferencia_bancaria','billetera_digital','yape','plin','qr','otro') NOT NULL,
    configuracion_json JSON NULL COMMENT 'Config del método: {"api_key": "xxx", "endpoint": "url", "behavior": {"require_ref": true, "manual_approval": true, "require_photo": false}}',
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    disponible_pos TINYINT(1) NOT NULL DEFAULT 1,
    disponible_tienda_online TINYINT(1) NOT NULL DEFAULT 0,
    orden INT NOT NULL DEFAULT 0,
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mpago_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_mpago_negocio_codigo (negocio_id, codigo),
    INDEX idx_mpago_negocio (negocio_id),
    INDEX idx_mpago_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Métodos de pago por negocio (RF-ADM-025)';

-- ============================================================
-- BLOQUE 4: CATÁLOGO DE PRODUCTOS
-- ============================================================

-- ============================================================
-- 4.1 CATEGORÍAS DE PRODUCTOS
-- ============================================================

CREATE TABLE categorias (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    padre_id BIGINT UNSIGNED NULL,
    nombre VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL,
    descripcion TEXT NULL,
    url_imagen VARCHAR(500) NULL,
    icono VARCHAR(50) NULL,
    orden INT NOT NULL DEFAULT 0,
    visible_tienda_online TINYINT(1) NOT NULL DEFAULT 1,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cat_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_cat_padre FOREIGN KEY (padre_id) REFERENCES categorias(id) ON DELETE SET NULL,
    UNIQUE KEY uk_cat_negocio_slug (negocio_id, slug),
    INDEX idx_cat_negocio (negocio_id),
    INDEX idx_cat_padre (padre_id),
    INDEX idx_cat_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Categorías de productos (RF-PRO-006..009)';

-- ============================================================
-- 4.2 MARCAS
-- ============================================================

CREATE TABLE marcas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL,
    pais_origen VARCHAR(100) NULL,
    url_logo VARCHAR(500) NULL,
    descripcion TEXT NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_marca_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_marca_negocio_slug (negocio_id, slug),
    INDEX idx_marca_negocio (negocio_id),
    INDEX idx_marca_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Marcas de productos (RF-PRO-010..013)';

-- ============================================================
-- 4.3 UNIDADES DE MEDIDA
-- ============================================================

CREATE TABLE unidades_medida (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    abreviatura VARCHAR(10) NOT NULL,
    tipo ENUM('volumen','peso','unidad','paquete','otro') NOT NULL DEFAULT 'unidad',
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_umed_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_umed_negocio_codigo (negocio_id, codigo),
    INDEX idx_umed_negocio (negocio_id)
) ENGINE=InnoDB COMMENT='Unidades de medida del negocio';

-- ============================================================
-- 4.4 PRODUCTOS
-- ============================================================

CREATE TABLE productos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sku VARCHAR(50) NOT NULL,
    codigo_barras VARCHAR(50) NULL,
    nombre VARCHAR(250) NOT NULL,
    slug VARCHAR(250) NOT NULL,
    descripcion_corta VARCHAR(500) NULL,
    descripcion TEXT NULL,
    url_imagen VARCHAR(500) NULL COMMENT 'URL de la imagen del producto',
    categoria_id BIGINT UNSIGNED NULL,
    marca_id BIGINT UNSIGNED NULL,
    unidad_medida_id BIGINT UNSIGNED NULL,

    -- Atributos específicos de licorería
    tipo_producto ENUM('alcoholica','no_alcoholica','comida','accesorio','combo','otro') NOT NULL DEFAULT 'alcoholica',
    tipo_bebida VARCHAR(100) NULL COMMENT 'Tipo: Whisky, Ron, Cerveza, Vino, Pisco, etc.',
    grado_alcoholico DECIMAL(5,2) NULL COMMENT 'Grado alcohólico %',
    volumen_ml INT UNSIGNED NULL COMMENT 'Volumen en ml',
    pais_origen VARCHAR(100) NULL,
    anio_cosecha SMALLINT UNSIGNED NULL COMMENT 'Año de cosecha (vinos)',
    aniejamiento VARCHAR(100) NULL COMMENT 'Tiempo de añejamiento',

    -- Precios
    precio_compra DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    precio_venta DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    precio_venta_minimo DECIMAL(10,2) NULL COMMENT 'Precio mínimo permitido',
    precio_mayorista DECIMAL(10,2) NULL,
    tasa_impuesto DECIMAL(5,2) NOT NULL DEFAULT 18.00 COMMENT 'IGV % Perú',
    impuesto_incluido TINYINT(1) NOT NULL DEFAULT 1,

    -- Stock y almacenamiento
    stock_minimo INT NOT NULL DEFAULT 0,
    stock_maximo INT NULL,
    punto_reorden INT NULL,
    tipo_almacenamiento ENUM('ambiente','frio','congelado') NOT NULL DEFAULT 'ambiente',
    temp_optima_min DECIMAL(5,2) NULL,
    temp_optima_max DECIMAL(5,2) NULL,
    es_perecible TINYINT(1) NOT NULL DEFAULT 0,
    dias_vida_util INT UNSIGNED NULL,

    -- Dimensiones y peso
    peso_kg DECIMAL(8,3) NULL,
    alto_cm DECIMAL(6,2) NULL,
    ancho_cm DECIMAL(6,2) NULL,

    -- Visibilidad y flags
    visible_pos TINYINT(1) NOT NULL DEFAULT 1,
    visible_tienda_online TINYINT(1) NOT NULL DEFAULT 0,
    es_destacado TINYINT(1) NOT NULL DEFAULT 0,
    requiere_verificacion_edad TINYINT(1) NOT NULL DEFAULT 0,
    permite_descuento TINYINT(1) NOT NULL DEFAULT 1,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    eliminado_en TIMESTAMP NULL,

    CONSTRAINT fk_prod_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_prod_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL,
    CONSTRAINT fk_prod_marca FOREIGN KEY (marca_id) REFERENCES marcas(id) ON DELETE SET NULL,
    CONSTRAINT fk_prod_umed FOREIGN KEY (unidad_medida_id) REFERENCES unidades_medida(id) ON DELETE SET NULL,

    UNIQUE KEY uk_prod_negocio_sku (negocio_id, sku),
    INDEX idx_prod_negocio (negocio_id),
    INDEX idx_prod_categoria (categoria_id),
    INDEX idx_prod_marca (marca_id),
    INDEX idx_prod_codigo_barras (negocio_id, codigo_barras),
    INDEX idx_prod_tipo (negocio_id, tipo_producto),
    INDEX idx_prod_activo (negocio_id, esta_activo),
    INDEX idx_prod_slug (negocio_id, slug),
    INDEX idx_prod_tienda_online (negocio_id, visible_tienda_online, esta_activo),
    FULLTEXT INDEX ft_prod_busqueda (nombre, descripcion_corta, tipo_bebida)
) ENGINE=InnoDB COMMENT='Productos del catálogo (RF-PRO-001..004)';

-- ============================================================
-- 4.5 COMBOS PROMOCIONALES
-- ============================================================

CREATE TABLE combos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL COMMENT 'Producto virtual tipo combo',
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT NULL,
    precio_combo DECIMAL(10,2) NOT NULL,
    precio_total_original DECIMAL(10,2) NOT NULL COMMENT 'Suma de precios individuales',
    porcentaje_descuento DECIMAL(5,2) NULL,
    max_usos INT UNSIGNED NULL COMMENT 'Límite de usos',
    usos_actuales INT UNSIGNED NOT NULL DEFAULT 0,
    valido_desde DATETIME NULL,
    valido_hasta DATETIME NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_combo_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_combo_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    INDEX idx_combo_negocio (negocio_id),
    INDEX idx_combo_activo (negocio_id, esta_activo),
    INDEX idx_combo_fechas (valido_desde, valido_hasta)
) ENGINE=InnoDB COMMENT='Combos promocionales (RF-PRO-014)';

CREATE TABLE detalle_combos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    combo_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    cantidad INT UNSIGNED NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL COMMENT 'Precio unitario al momento de crear combo',
    orden INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_detcombo_combo FOREIGN KEY (combo_id) REFERENCES combos(id) ON DELETE CASCADE,
    CONSTRAINT fk_detcombo_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    INDEX idx_detcombo_combo (combo_id)
) ENGINE=InnoDB COMMENT='Productos dentro de un combo (RF-PRO-014)';


-- ============================================================
-- BLOQUE 5: INVENTARIO, LOTES Y MOVIMIENTOS
-- ============================================================

-- ============================================================
-- 5.1 STOCK POR ALMACÉN
-- ============================================================

CREATE TABLE stock_inventario (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    almacen_id BIGINT UNSIGNED NOT NULL,
    cantidad_en_mano INT NOT NULL DEFAULT 0,
    cantidad_reservada INT NOT NULL DEFAULT 0 COMMENT 'Reservado por pedidos pendientes',
    cantidad_disponible INT GENERATED ALWAYS AS (cantidad_en_mano - cantidad_reservada) STORED,
    ultimo_conteo_en TIMESTAMP NULL,
    ultimo_movimiento_en TIMESTAMP NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stkinv_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_stkinv_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_stkinv_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes(id),
    UNIQUE KEY uk_stkinv_producto_almacen (producto_id, almacen_id),
    INDEX idx_stkinv_negocio (negocio_id),
    INDEX idx_stkinv_almacen (almacen_id),
    INDEX idx_stkinv_stock_bajo (negocio_id, cantidad_en_mano)
) ENGINE=InnoDB COMMENT='Stock actual por producto y almacén (RF-INV-001)';

-- ============================================================
-- 5.2 LOTES (FIFO)
-- ============================================================

CREATE TABLE lotes_inventario (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    almacen_id BIGINT UNSIGNED NOT NULL,
    numero_lote VARCHAR(50) NOT NULL,
    cantidad_inicial INT NOT NULL,
    cantidad_restante INT NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL,
    fecha_fabricacion DATE NULL,
    fecha_vencimiento DATE NULL,
    fecha_recepcion DATE NOT NULL,
    proveedor_id BIGINT UNSIGNED NULL,
    orden_compra_id BIGINT UNSIGNED NULL,
    estado ENUM('disponible','agotado','vencido','cuarentena','devuelto') NOT NULL DEFAULT 'disponible',
    notas TEXT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lotinv_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_lotinv_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_lotinv_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes(id),
    UNIQUE KEY uk_lotinv_negocio_lote (negocio_id, numero_lote),
    INDEX idx_lotinv_negocio (negocio_id),
    INDEX idx_lotinv_producto (producto_id, almacen_id),
    INDEX idx_lotinv_vencimiento (fecha_vencimiento),
    INDEX idx_lotinv_estado (estado),
    INDEX idx_lotinv_fifo (producto_id, almacen_id, fecha_recepcion, estado)
) ENGINE=InnoDB COMMENT='Lotes de inventario con FIFO (RF-INV-002..003)';

-- ============================================================
-- 5.3 MOVIMIENTOS DE INVENTARIO
-- ============================================================

CREATE TABLE movimientos_inventario (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    almacen_id BIGINT UNSIGNED NOT NULL,
    lote_id BIGINT UNSIGNED NULL,
    tipo_movimiento ENUM(
        'entrada_compra','salida_venta','entrada_devolucion','salida_devolucion',
        'entrada_transferencia','salida_transferencia','ajuste_entrada','ajuste_salida',
        'merma','rotura','vencimiento','stock_inicial','entrada_produccion','salida_produccion'
    ) NOT NULL,
    cantidad INT NOT NULL COMMENT 'Positivo o negativo según tipo',
    costo_unitario DECIMAL(10,2) NULL,
    tipo_referencia VARCHAR(50) NULL COMMENT 'Tipo de documento referencia',
    referencia_id BIGINT UNSIGNED NULL COMMENT 'ID del documento referencia',
    motivo VARCHAR(300) NULL,
    realizado_por BIGINT UNSIGNED NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movinv_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_movinv_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_movinv_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes(id),
    CONSTRAINT fk_movinv_lote FOREIGN KEY (lote_id) REFERENCES lotes_inventario(id),
    CONSTRAINT fk_movinv_usuario FOREIGN KEY (realizado_por) REFERENCES usuarios(id),
    INDEX idx_movinv_negocio (negocio_id),
    INDEX idx_movinv_producto (producto_id),
    INDEX idx_movinv_almacen (almacen_id),
    INDEX idx_movinv_tipo (tipo_movimiento),
    INDEX idx_movinv_referencia (tipo_referencia, referencia_id),
    INDEX idx_movinv_creado (negocio_id, creado_en)
) ENGINE=InnoDB COMMENT='Movimientos de inventario (RF-INV-004..006)';

-- ============================================================
-- BLOQUE 6: PROVEEDORES Y COMPRAS
-- ============================================================

-- ============================================================
-- 6.1 PROVEEDORES
-- ============================================================

CREATE TABLE proveedores (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    razon_social VARCHAR(200) NOT NULL,
    nombre_comercial VARCHAR(200) NULL,
    ruc VARCHAR(20) NULL COMMENT 'RUC del proveedor',
    direccion VARCHAR(300) NULL,
    telefono VARCHAR(30) NULL,
    email VARCHAR(150) NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    rubro TEXT NULL COMMENT 'Rubro o giro del proveedor',
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prov_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_prov_negocio_codigo (negocio_id, codigo),
    INDEX idx_prov_negocio (negocio_id),
    INDEX idx_prov_activo (negocio_id, esta_activo),
    INDEX idx_prov_ruc (negocio_id, ruc)
) ENGINE=InnoDB COMMENT='Proveedores (RF-COM-001..003)';

CREATE TABLE productos_proveedor (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    proveedor_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    sku_proveedor VARCHAR(50) NULL,
    precio_proveedor DECIMAL(10,2) NULL,
    dias_tiempo_entrega INT UNSIGNED NULL,
    cantidad_minima_pedido INT UNSIGNED NULL,
    es_preferido TINYINT(1) NOT NULL DEFAULT 0,
    fecha_ultima_compra DATE NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prodprov_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE CASCADE,
    CONSTRAINT fk_prodprov_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    UNIQUE KEY uk_prodprov_proveedor_producto (proveedor_id, producto_id),
    INDEX idx_prodprov_producto (producto_id)
) ENGINE=InnoDB COMMENT='Productos por proveedor (RF-COM-001)';

-- ============================================================
-- 6.2 ÓRDENES DE COMPRA
-- ============================================================

CREATE TABLE ordenes_compra (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    numero_orden VARCHAR(30) NOT NULL,
    proveedor_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    almacen_id BIGINT UNSIGNED NOT NULL,
    estado ENUM('borrador','pendiente_aprobacion','aprobada','enviada','recepcion_parcial','recibida','cancelada') NOT NULL DEFAULT 'borrador',
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    fecha_entrega_esperada DATE NULL,
    fecha_entrega_real DATE NULL,
    plazo_pago_dias INT UNSIGNED NULL,
    fecha_vencimiento_pago DATE NULL,
    notas TEXT NULL,
    creado_por BIGINT UNSIGNED NULL,
    aprobado_por BIGINT UNSIGNED NULL,
    recibido_por BIGINT UNSIGNED NULL,
    aprobado_en TIMESTAMP NULL,
    enviado_en TIMESTAMP NULL,
    recibido_en TIMESTAMP NULL,
    cancelado_en TIMESTAMP NULL,
    razon_cancelacion VARCHAR(500) NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ordcom_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_ordcom_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    CONSTRAINT fk_ordcom_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    CONSTRAINT fk_ordcom_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes(id),
    CONSTRAINT fk_ordcom_creado FOREIGN KEY (creado_por) REFERENCES usuarios(id),
    CONSTRAINT fk_ordcom_aprobado FOREIGN KEY (aprobado_por) REFERENCES usuarios(id),
    CONSTRAINT fk_ordcom_recibido FOREIGN KEY (recibido_por) REFERENCES usuarios(id),
    UNIQUE KEY uk_ordcom_negocio_numero (negocio_id, numero_orden),
    INDEX idx_ordcom_negocio (negocio_id),
    INDEX idx_ordcom_proveedor (proveedor_id),
    INDEX idx_ordcom_estado (negocio_id, estado),
    INDEX idx_ordcom_fecha (negocio_id, creado_en)
) ENGINE=InnoDB COMMENT='Órdenes de compra (RF-COM-004..007)';

CREATE TABLE detalle_ordenes_compra (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    orden_compra_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    cantidad_ordenada INT NOT NULL,
    cantidad_recibida INT NOT NULL DEFAULT 0,
    cantidad_rechazada INT NOT NULL DEFAULT 0 COMMENT 'Productos rechazados por daños o defectos',
    razon_rechazo VARCHAR(300) NULL COMMENT 'Motivo del rechazo de productos',
    precio_unitario DECIMAL(10,2) NOT NULL,
    tasa_impuesto DECIMAL(5,2) NOT NULL DEFAULT 18.00,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    porcentaje_descuento DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    notas VARCHAR(300) NULL,
    CONSTRAINT fk_detordcom_orden FOREIGN KEY (orden_compra_id) REFERENCES ordenes_compra(id) ON DELETE CASCADE,
    CONSTRAINT fk_detordcom_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    INDEX idx_detordcom_orden (orden_compra_id),
    INDEX idx_detordcom_producto (producto_id)
) ENGINE=InnoDB COMMENT='Items de orden de compra con recepción con recepción';

-- ============================================================
-- BLOQUE 7: CLIENTES
-- ============================================================

CREATE TABLE clientes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    uuid CHAR(36) NOT NULL UNIQUE,
    tipo_cliente ENUM('individual','empresa') NOT NULL DEFAULT 'individual',
    nombres VARCHAR(100) NULL,
    apellidos VARCHAR(100) NULL,
    razon_social VARCHAR(200) NULL,
    tipo_documento ENUM('DNI','RUC','CE','PASAPORTE','OTRO') NULL,
    numero_documento VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    telefono VARCHAR(30) NULL,
    direccion VARCHAR(500) NULL,
    -- Estadísticas
    total_compras DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_pedidos INT UNSIGNED NOT NULL DEFAULT 0,
    -- Estado
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cli_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_cli_negocio_doc (negocio_id, tipo_documento, numero_documento),
    INDEX idx_cli_negocio (negocio_id),
    INDEX idx_cli_email (negocio_id, email),
    INDEX idx_cli_telefono (negocio_id, telefono),
    INDEX idx_cli_doc (negocio_id, numero_documento),
    INDEX idx_cli_uuid (uuid),
    INDEX idx_cli_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Clientes del negocio (RF-CLI-001..005)';

-- ============================================================
-- BLOQUE 8: VENTAS, POS Y CAJAS
-- ============================================================

-- ============================================================
-- 8.1 CAJAS REGISTRADORAS
-- ============================================================

CREATE TABLE cajas_registradoras (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cajreg_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_cajreg_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    UNIQUE KEY uk_cajreg_sede_codigo (sede_id, codigo),
    INDEX idx_cajreg_negocio (negocio_id),
    INDEX idx_cajreg_sede (sede_id)
) ENGINE=InnoDB COMMENT='Cajas registradoras por sede (RF-VEN-001)';

CREATE TABLE sesiones_caja (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    caja_registradora_id BIGINT UNSIGNED NOT NULL,
    abierta_por BIGINT UNSIGNED NOT NULL,
    cerrada_por BIGINT UNSIGNED NULL,
    monto_apertura DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    monto_cierre DECIMAL(12,2) NULL,
    monto_esperado DECIMAL(12,2) NULL,
    diferencia DECIMAL(12,2) NULL COMMENT 'Diferencia = cierre - esperado',
    total_ventas DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_reembolsos DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_entradas_efectivo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_salidas_efectivo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    cantidad_transacciones INT UNSIGNED NOT NULL DEFAULT 0,
    estado ENUM('abierta','cerrada','conciliada') NOT NULL DEFAULT 'abierta',
    abierta_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cerrada_en TIMESTAMP NULL,
    notas TEXT NULL,
    notas_cierre TEXT NULL,
    CONSTRAINT fk_sescaj_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_sescaj_caja FOREIGN KEY (caja_registradora_id) REFERENCES cajas_registradoras(id),
    CONSTRAINT fk_sescaj_abierta FOREIGN KEY (abierta_por) REFERENCES usuarios(id),
    CONSTRAINT fk_sescaj_cerrada FOREIGN KEY (cerrada_por) REFERENCES usuarios(id),
    INDEX idx_sescaj_negocio (negocio_id),
    INDEX idx_sescaj_caja (caja_registradora_id),
    INDEX idx_sescaj_estado (negocio_id, estado),
    INDEX idx_sescaj_abierta (abierta_en)
) ENGINE=InnoDB COMMENT='Sesiones/turnos de caja (RF-VEN-001..002)';

CREATE TABLE movimientos_caja (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sesion_id BIGINT UNSIGNED NOT NULL,
    tipo_movimiento ENUM('entrada_efectivo','salida_efectivo','venta','reembolso','ajuste') NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    motivo VARCHAR(300) NULL,
    tipo_referencia VARCHAR(50) NULL,
    referencia_id BIGINT UNSIGNED NULL,
    realizado_por BIGINT UNSIGNED NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movcaj_sesion FOREIGN KEY (sesion_id) REFERENCES sesiones_caja(id),
    CONSTRAINT fk_movcaj_usuario FOREIGN KEY (realizado_por) REFERENCES usuarios(id),
    INDEX idx_movcaj_sesion (sesion_id),
    INDEX idx_movcaj_tipo (tipo_movimiento)
) ENGINE=InnoDB COMMENT='Movimientos de efectivo en caja';

-- ============================================================
-- 8.2 VENTAS
-- ============================================================

CREATE TABLE ventas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sede_id BIGINT UNSIGNED NOT NULL,
    numero_venta VARCHAR(30) NOT NULL,
    tipo_venta ENUM('pos','tienda_online','mesa','telefono','otro') NOT NULL DEFAULT 'pos',
    
    -- Relacionales
    sesion_caja_id BIGINT UNSIGNED NULL COMMENT 'Sesión de caja para ventas POS',
    cliente_id BIGINT UNSIGNED NULL,
    mesa_id BIGINT UNSIGNED NULL COMMENT 'Mesa para consumo en local',
    vendedor_id BIGINT UNSIGNED NULL,

    -- Montos
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    monto_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    razon_descuento VARCHAR(200) NULL,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    costo_envio DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Tarifa de delivery/envío',
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,

    -- Estado
    estado ENUM('pendiente','completada','parcialmente_pagada','cancelada','reembolsada','anulada') NOT NULL DEFAULT 'pendiente',
    
    -- Estado de entrega (e-commerce)
    estado_entrega ENUM('entregado','pendiente_envio','en_ruta','para_recoger') NULL COMMENT 'Estado logístico para tienda online',
    direccion_entrega JSON NULL COMMENT 'Dirección de entrega: {"direccion": "...", "lat": 0.0, "lng": 0.0, "referencia": "..."}',

    -- Información fiscal consolidada (Boletas, Facturas, Notas de Venta)
    tipo_comprobante ENUM('boleta','factura','nota_venta') NOT NULL DEFAULT 'boleta',
    doc_cliente_numero VARCHAR(20) NULL COMMENT 'DNI, RUC, Pasaporte o CE del cliente',
    doc_cliente_nombre VARCHAR(200) NULL COMMENT 'Nombre completo o Razón Social del cliente',

    completado_en DATETIME NULL,
    cancelado_en DATETIME NULL,
    razon_cancelacion VARCHAR(500) NULL,
    cancelado_por BIGINT UNSIGNED NULL,
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_ven_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    CONSTRAINT fk_ven_sesion_caja FOREIGN KEY (sesion_caja_id) REFERENCES sesiones_caja(id),
    CONSTRAINT fk_ven_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL,
    CONSTRAINT fk_ven_mesa FOREIGN KEY (mesa_id) REFERENCES mesas(id) ON DELETE SET NULL,
    CONSTRAINT fk_ven_vendedor FOREIGN KEY (vendedor_id) REFERENCES usuarios(id),

    INDEX idx_ven_sede (sede_id),
    INDEX idx_ven_numero (sede_id, numero_venta),
    INDEX idx_ven_cliente (cliente_id),
    INDEX idx_ven_estado (sede_id, estado),
    INDEX idx_ven_fecha (sede_id, creado_en),
    INDEX idx_ven_tipo (sede_id, tipo_venta),
    INDEX idx_ven_sesion (sesion_caja_id),
    INDEX idx_ven_comprobante (tipo_comprobante, doc_cliente_numero)
) ENGINE=InnoDB COMMENT='Ventas - Sistema híbrido POS + E-commerce (RF-VEN-003..008)';

CREATE TABLE detalle_ventas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    lote_id BIGINT UNSIGNED NOT NULL COMMENT 'Lote FIFO obligatorio para trazabilidad',
    combo_id BIGINT UNSIGNED NULL COMMENT 'Si pertenece a un combo',
    
    -- Snapshot histórico
    nombre_producto VARCHAR(250) NOT NULL COMMENT 'Snapshot del nombre al momento de la venta',
    sku_producto VARCHAR(50) NOT NULL COMMENT 'Snapshot del SKU al momento de la venta',
    
    -- Cantidades y montos
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL COMMENT 'Precio unitario al momento de la venta',
    monto_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tasa_impuesto DECIMAL(5,2) NOT NULL DEFAULT 18.00,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT fk_detven_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detven_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_detven_lote FOREIGN KEY (lote_id) REFERENCES lotes_inventario(id),
    CONSTRAINT fk_detven_combo FOREIGN KEY (combo_id) REFERENCES combos(id),
    INDEX idx_detven_venta (venta_id),
    INDEX idx_detven_producto (producto_id),
    INDEX idx_detven_lote (lote_id)
) ENGINE=InnoDB COMMENT='Detalle de ventas - Control FIFO de inventario';

-- ============================================================
-- 8.3 PAGOS DE VENTAS
-- ============================================================

CREATE TABLE pagos_venta (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT UNSIGNED NOT NULL,
    metodo_pago_id BIGINT UNSIGNED NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    
    -- Control de efectivo (para caja física)
    monto_recibido DECIMAL(12,2) NULL COMMENT 'Monto recibido del cliente (para efectivo)',
    monto_cambio DECIMAL(12,2) NULL COMMENT 'Vuelto entregado al cliente',
    
    -- Referencia consolidada (voucher, operación, auth code)
    referencia_pago VARCHAR(100) NULL COMMENT 'Voucher, Nro. Operación, Auth Code o Transacción ID',
    
    -- Estado (para pagos asíncronos web)
    estado ENUM('pendiente','aprobado','rechazado','reembolsado') NOT NULL DEFAULT 'aprobado',
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pven_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    CONSTRAINT fk_pven_metodo FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(id),
    INDEX idx_pven_venta (venta_id),
    INDEX idx_pven_metodo (metodo_pago_id),
    INDEX idx_pven_referencia (referencia_pago)
) ENGINE=InnoDB COMMENT='Pagos de ventas - Soporta pagos mixtos (Efectivo + Digital)';

-- ============================================================
-- BLOQUE 9: PEDIDOS (TIENDA ONLINE, DELIVERY, RECOJO)
-- ============================================================

CREATE TABLE pedidos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    repartidor_id BIGINT UNSIGNED NULL COMMENT 'Usuario asignado como repartidor',
    numero_pedido VARCHAR(30) NOT NULL,
    cliente_id BIGINT UNSIGNED NOT NULL,
    tipo_pedido ENUM('delivery','recojo','consumo_local') NOT NULL DEFAULT 'delivery',
    origen_pedido ENUM('tienda_online','telefono','whatsapp','pos','otro') NOT NULL DEFAULT 'tienda_online',

    -- Zona de delivery
    zona_delivery_id BIGINT UNSIGNED NULL,

    -- Montos
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    monto_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tarifa_delivery DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_propina DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',

    -- Estado del pedido
    estado ENUM(
        'pendiente','confirmado','preparando','listo','asignado',
        'en_delivery','entregado','recogido','completado','cancelado','reembolsado'
    ) NOT NULL DEFAULT 'pendiente',

    -- Verificación de edad
    edad_verificada TINYINT(1) NOT NULL DEFAULT 0,
    metodo_verificacion_edad VARCHAR(50) NULL,

    -- Info fiscal
    requiere_factura TINYINT(1) NOT NULL DEFAULT 0,
    ruc_cliente VARCHAR(20) NULL,
    razon_social_cliente VARCHAR(200) NULL,
    confirmado_en TIMESTAMP NULL,
    preparando_en TIMESTAMP NULL,
    listo_en TIMESTAMP NULL,
    entregado_en TIMESTAMP NULL,
    completado_en TIMESTAMP NULL,
    cancelado_en TIMESTAMP NULL,
    razon_cancelacion VARCHAR(500) NULL,
    tipo_cancelador ENUM('cliente','admin','sistema') NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_ped_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_ped_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    CONSTRAINT fk_ped_repartidor FOREIGN KEY (repartidor_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT fk_ped_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    -- FK fk_ped_direccion eliminada: tabla direcciones_cliente fue removida (Bloque 7 simplificado)
    CONSTRAINT fk_ped_zona FOREIGN KEY (zona_delivery_id) REFERENCES zonas_delivery(id) ON DELETE SET NULL,

    UNIQUE KEY uk_ped_negocio_numero (negocio_id, numero_pedido),
    INDEX idx_ped_negocio (negocio_id),
    INDEX idx_ped_sede (sede_id),
    INDEX idx_ped_cliente (cliente_id),
    INDEX idx_ped_estado (negocio_id, estado),
    INDEX idx_ped_fecha (negocio_id, creado_en),
    INDEX idx_ped_tipo (negocio_id, tipo_pedido)
) ENGINE=InnoDB COMMENT='Pedidos de clientes (RF-PED-001..006)';

CREATE TABLE detalle_pedidos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    combo_id BIGINT UNSIGNED NULL,
    nombre_producto VARCHAR(250) NOT NULL,
    sku_producto VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    monto_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tasa_impuesto DECIMAL(5,2) NOT NULL DEFAULT 18.00,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    notas VARCHAR(300) NULL,
    CONSTRAINT fk_detped_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_detped_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_detped_combo FOREIGN KEY (combo_id) REFERENCES combos(id),
    INDEX idx_detped_pedido (pedido_id),
    INDEX idx_detped_producto (producto_id)
) ENGINE=InnoDB COMMENT='Items del pedido';

CREATE TABLE pagos_pedido (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT UNSIGNED NOT NULL,
    metodo_pago_id BIGINT UNSIGNED NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    numero_referencia VARCHAR(100) NULL,
    estado ENUM('pendiente','procesando','aprobado','rechazado','reembolsado') NOT NULL DEFAULT 'pendiente',
    pagado_en TIMESTAMP NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pped_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_pped_metodo FOREIGN KEY (metodo_pago_id) REFERENCES metodos_pago(id),
    INDEX idx_pped_pedido (pedido_id),
    INDEX idx_pped_estado (estado)
) ENGINE=InnoDB COMMENT='Pagos de pedidos';

-- ============================================================
-- 9.1 SEGUIMIENTO DE PEDIDOS
-- ============================================================

CREATE TABLE seguimiento_pedidos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT UNSIGNED NOT NULL,
    estado VARCHAR(50) NOT NULL,
    descripcion VARCHAR(500) NULL,
    realizado_por BIGINT UNSIGNED NULL,
    visible_para_cliente TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_segped_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_segped_usuario FOREIGN KEY (realizado_por) REFERENCES usuarios(id),
    INDEX idx_segped_pedido (pedido_id),
    INDEX idx_segped_creado (creado_en)
) ENGINE=InnoDB COMMENT='Historial de seguimiento de pedidos (RF-PED-005)';

-- ============================================================
-- BLOQUE 10: FACTURACIÓN ELECTRÓNICA (SUNAT)
-- ============================================================

CREATE TABLE series_facturacion (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    tipo_documento ENUM('boleta','factura','nota_credito','nota_debito','guia_remision') NOT NULL,
    prefijo_serie VARCHAR(10) NOT NULL COMMENT 'Ej: B001, F001, BC01, FC01',
    numero_actual INT UNSIGNED NOT NULL DEFAULT 0,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_serfac_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_serfac_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    UNIQUE KEY uk_serfac_negocio_serie (negocio_id, sede_id, tipo_documento, prefijo_serie),
    INDEX idx_serfac_negocio (negocio_id)
) ENGINE=InnoDB COMMENT='Series de facturación por sede (RF-FACT-001)';

CREATE TABLE documentos_facturacion (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    serie_id BIGINT UNSIGNED NOT NULL,
    venta_id BIGINT UNSIGNED NULL,
    pedido_id BIGINT UNSIGNED NULL,

    tipo_documento ENUM('boleta','factura','nota_credito','nota_debito','guia_remision') NOT NULL,
    serie VARCHAR(10) NOT NULL,
    numero_correlativo INT UNSIGNED NOT NULL,
    numero_completo VARCHAR(30) NOT NULL COMMENT 'Ej: B001-00000123',

    -- Datos del emisor (snapshot)
    ruc_emisor VARCHAR(20) NOT NULL,
    razon_social_emisor VARCHAR(200) NOT NULL,
    direccion_emisor VARCHAR(300) NULL,

    -- Datos del receptor
    tipo_documento_receptor VARCHAR(5) NULL,
    numero_documento_receptor VARCHAR(20) NULL,
    nombre_receptor VARCHAR(200) NOT NULL,
    direccion_receptor VARCHAR(300) NULL,
    email_receptor VARCHAR(150) NULL,

    -- Montos
    subtotal DECIMAL(12,2) NOT NULL,
    total_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_gravado DECIMAL(12,2) NOT NULL COMMENT 'Base imponible',
    total_igv DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_isc DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Impuesto selectivo al consumo',
    total_otros_impuestos DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL,
    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    tipo_cambio DECIMAL(10,6) NULL,

    -- SUNAT
    estado_sunat ENUM('pendiente','enviado','aceptado','rechazado','anulado','error') NOT NULL DEFAULT 'pendiente',
    ticket_sunat VARCHAR(100) NULL,
    codigo_respuesta_sunat VARCHAR(10) NULL,
    mensaje_respuesta_sunat TEXT NULL,
    hash_sunat VARCHAR(255) NULL,
    url_xml_sunat VARCHAR(500) NULL,
    url_cdr_sunat VARCHAR(500) NULL,
    url_pdf_sunat VARCHAR(500) NULL,
    enviado_sunat_en TIMESTAMP NULL,
    aceptado_sunat_en TIMESTAMP NULL,

    -- Documento referenciado (para notas de crédito/débito)
    documento_referenciado_id BIGINT UNSIGNED NULL,
    motivo_referencia VARCHAR(300) NULL,

    fecha_emision DATE NOT NULL,
    fecha_vencimiento DATE NULL,
    estado ENUM('borrador','emitido','enviado','aceptado','anulado','error') NOT NULL DEFAULT 'borrador',

    creado_por BIGINT UNSIGNED NULL,
    anulado_por BIGINT UNSIGNED NULL,
    anulado_en TIMESTAMP NULL,
    motivo_anulacion VARCHAR(300) NULL,

    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_docfac_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_docfac_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    CONSTRAINT fk_docfac_serie FOREIGN KEY (serie_id) REFERENCES series_facturacion(id),
    CONSTRAINT fk_docfac_venta FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE SET NULL,
    CONSTRAINT fk_docfac_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL,
    CONSTRAINT fk_docfac_ref FOREIGN KEY (documento_referenciado_id) REFERENCES documentos_facturacion(id),
    CONSTRAINT fk_docfac_creado FOREIGN KEY (creado_por) REFERENCES usuarios(id),

    UNIQUE KEY uk_docfac_numero_completo (negocio_id, numero_completo),
    INDEX idx_docfac_negocio (negocio_id),
    INDEX idx_docfac_sede (sede_id),
    INDEX idx_docfac_venta (venta_id),
    INDEX idx_docfac_pedido (pedido_id),
    INDEX idx_docfac_tipo (negocio_id, tipo_documento),
    INDEX idx_docfac_estado_sunat (estado_sunat),
    INDEX idx_docfac_fecha_emision (negocio_id, fecha_emision),
    INDEX idx_docfac_estado (negocio_id, estado)
) ENGINE=InnoDB COMMENT='Documentos de facturación electrónica SUNAT (RF-FACT-001..005)';

CREATE TABLE detalle_documentos_facturacion (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    documento_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NULL,
    numero_item INT UNSIGNED NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    codigo_unidad VARCHAR(10) NOT NULL DEFAULT 'NIU' COMMENT 'Código SUNAT de unidad',
    cantidad DECIMAL(12,4) NOT NULL,
    precio_unitario DECIMAL(10,4) NOT NULL,
    monto_descuento DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_gravado DECIMAL(10,2) NOT NULL,
    monto_igv DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_isc DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detdocfac_documento FOREIGN KEY (documento_id) REFERENCES documentos_facturacion(id) ON DELETE CASCADE,
    CONSTRAINT fk_detdocfac_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE SET NULL,
    INDEX idx_detdocfac_documento (documento_id)
) ENGINE=InnoDB COMMENT='Items de documento de facturación';

-- ============================================================
-- BLOQUE 11: DEVOLUCIONES Y REEMBOLSOS
-- ============================================================

CREATE TABLE devoluciones (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NOT NULL,
    numero_devolucion VARCHAR(30) NOT NULL,
    venta_id BIGINT UNSIGNED NULL,
    pedido_id BIGINT UNSIGNED NULL,
    cliente_id BIGINT UNSIGNED NULL,
    tipo_devolucion ENUM('total','parcial') NOT NULL DEFAULT 'parcial',
    categoria_motivo ENUM('defectuoso','articulo_incorrecto','cambio_cliente','vencido','danado','otro') NOT NULL,
    detalle_motivo TEXT NULL,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    metodo_reembolso ENUM('efectivo','pago_original','credito_tienda','transferencia_bancaria') NOT NULL DEFAULT 'pago_original',
    estado ENUM('solicitada','aprobada','procesando','completada','rechazada') NOT NULL DEFAULT 'solicitada',
    solicitado_por BIGINT UNSIGNED NULL,
    aprobado_por BIGINT UNSIGNED NULL,
    procesado_por BIGINT UNSIGNED NULL,
    solicitado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aprobado_en TIMESTAMP NULL,
    completado_en TIMESTAMP NULL,
    rechazado_en TIMESTAMP NULL,
    razon_rechazo VARCHAR(500) NULL,
    notas TEXT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dev_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_dev_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    CONSTRAINT fk_dev_venta FOREIGN KEY (venta_id) REFERENCES ventas(id),
    CONSTRAINT fk_dev_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_dev_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    UNIQUE KEY uk_dev_negocio_numero (negocio_id, numero_devolucion),
    INDEX idx_dev_negocio (negocio_id),
    INDEX idx_dev_venta (venta_id),
    INDEX idx_dev_pedido (pedido_id),
    INDEX idx_dev_estado (negocio_id, estado)
) ENGINE=InnoDB COMMENT='Devoluciones y reembolsos (RF-DEV-001..003)';

CREATE TABLE detalle_devoluciones (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    devolucion_id BIGINT UNSIGNED NOT NULL,
    producto_id BIGINT UNSIGNED NOT NULL,
    detalle_venta_id BIGINT UNSIGNED NULL,
    detalle_pedido_id BIGINT UNSIGNED NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    estado_condicion ENUM('bueno','danado','vencido','abierto') NOT NULL DEFAULT 'bueno',
    devolver_stock TINYINT(1) NOT NULL DEFAULT 0,
    almacen_id BIGINT UNSIGNED NULL COMMENT 'Almacén de retorno',
    notas VARCHAR(300) NULL,
    CONSTRAINT fk_detdev_devolucion FOREIGN KEY (devolucion_id) REFERENCES devoluciones(id) ON DELETE CASCADE,
    CONSTRAINT fk_detdev_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
    CONSTRAINT fk_detdev_detven FOREIGN KEY (detalle_venta_id) REFERENCES detalle_ventas(id),
    CONSTRAINT fk_detdev_detped FOREIGN KEY (detalle_pedido_id) REFERENCES detalle_pedidos(id),
    CONSTRAINT fk_detdev_almacen FOREIGN KEY (almacen_id) REFERENCES almacenes(id),
    INDEX idx_detdev_devolucion (devolucion_id)
) ENGINE=InnoDB COMMENT='Items devueltos';

-- ============================================================
-- BLOQUE 12: GASTOS E INGRESOS
-- ============================================================

CREATE TABLE categorias_gasto (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    codigo VARCHAR(20) NOT NULL,
    tipo ENUM('operativo','administrativo','servicio','personal','marketing','mantenimiento','tecnologia','otro') NULL COMMENT 'Tipo de categoría para agrupación',
    descripcion VARCHAR(300) NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_catgas_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_catgas_negocio_codigo (negocio_id, codigo),
    INDEX idx_catgas_negocio (negocio_id),
    INDEX idx_catgas_tipo (negocio_id, tipo)
) ENGINE=InnoDB COMMENT='Categorías de gastos (RF-GAS-001)';

CREATE TABLE gastos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    sede_id BIGINT UNSIGNED NULL,
    numero_gasto VARCHAR(30) NOT NULL,
    categoria_id BIGINT UNSIGNED NOT NULL,
    proveedor_id BIGINT UNSIGNED NULL,
    descripcion VARCHAR(500) NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    monto_impuesto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL,





    moneda VARCHAR(3) NOT NULL DEFAULT 'PEN',
    fecha_gasto DATE NOT NULL,
    metodo_pago ENUM('efectivo','transferencia_bancaria','tarjeta_credito','cheque','otro') NOT NULL DEFAULT 'efectivo',
    referencia_pago VARCHAR(100) NULL,
    url_comprobante VARCHAR(500) NULL,
    estado ENUM('pendiente','aprobado','pagado','rechazado','anulado') NOT NULL DEFAULT 'pendiente',
    es_recurrente TINYINT(1) NOT NULL DEFAULT 0,
    periodo_recurrencia ENUM('semanal','quincenal','mensual','trimestral','anual') NULL,
    aprobado_por BIGINT UNSIGNED NULL,
    registrado_por BIGINT UNSIGNED NULL,
    notas TEXT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_gas_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_gas_sede FOREIGN KEY (sede_id) REFERENCES sedes(id),
    CONSTRAINT fk_gas_categoria FOREIGN KEY (categoria_id) REFERENCES categorias_gasto(id),
    CONSTRAINT fk_gas_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    CONSTRAINT fk_gas_aprobado FOREIGN KEY (aprobado_por) REFERENCES usuarios(id),
    CONSTRAINT fk_gas_registrado FOREIGN KEY (registrado_por) REFERENCES usuarios(id),
    UNIQUE KEY uk_gas_negocio_numero (negocio_id, numero_gasto),
    INDEX idx_gas_negocio (negocio_id),
    INDEX idx_gas_sede (sede_id),
    INDEX idx_gas_categoria (categoria_id),
    INDEX idx_gas_fecha (negocio_id, fecha_gasto),
    INDEX idx_gas_estado (negocio_id, estado)
) ENGINE=InnoDB COMMENT='Gastos del negocio (RF-GAS-001..003)';

-- ============================================================
-- BLOQUE 13: DESCUENTOS Y PROMOCIONES
-- ============================================================

CREATE TABLE promociones (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(50) NULL COMMENT 'Código promocional',
    descripcion TEXT NULL,
    tipo_descuento ENUM('porcentaje','monto_fijo','compre_x_lleve_y','envio_gratis') NOT NULL,
    valor_descuento DECIMAL(10,2) NOT NULL,
    monto_minimo_compra DECIMAL(10,2) NULL,
    monto_maximo_descuento DECIMAL(10,2) NULL,
    max_usos INT UNSIGNED NULL,
    max_usos_por_cliente INT UNSIGNED NULL,
    usos_actuales INT UNSIGNED NOT NULL DEFAULT 0,
    aplica_a ENUM('todo','categoria','producto','marca','combo') NOT NULL DEFAULT 'todo',
    valido_desde DATETIME NOT NULL,
    valido_hasta DATETIME NOT NULL,
    esta_activo TINYINT(1) NOT NULL DEFAULT 1,
    es_combinable TINYINT(1) NOT NULL DEFAULT 0,
    canales JSON NULL COMMENT '["pos","tienda_online"]',
    creado_por BIGINT UNSIGNED NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_promo_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    CONSTRAINT fk_promo_creado FOREIGN KEY (creado_por) REFERENCES usuarios(id),
    INDEX idx_promo_negocio (negocio_id),
    INDEX idx_promo_codigo (negocio_id, codigo),
    INDEX idx_promo_fechas (valido_desde, valido_hasta),
    INDEX idx_promo_activo (negocio_id, esta_activo)
) ENGINE=InnoDB COMMENT='Promociones y descuentos (RF-PRO-015)';

CREATE TABLE condiciones_promocion (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    promocion_id BIGINT UNSIGNED NOT NULL,
    tipo_entidad ENUM('producto','categoria','marca') NOT NULL,
    entidad_id BIGINT UNSIGNED NOT NULL,
    CONSTRAINT fk_condpromo_promocion FOREIGN KEY (promocion_id) REFERENCES promociones(id) ON DELETE CASCADE,
    INDEX idx_condpromo_promocion (promocion_id),
    INDEX idx_condpromo_entidad (tipo_entidad, entidad_id)
) ENGINE=InnoDB COMMENT='Condiciones de aplicación de promociones';

-- ============================================================
-- BLOQUE 14: TIENDA ONLINE (STOREFRONT)
-- ============================================================

CREATE TABLE configuracion_tienda_online (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    esta_habilitado TINYINT(1) NOT NULL DEFAULT 0,
    
    -- Identificación del storefront
    nombre_tienda VARCHAR(200) NULL,
    slug_tienda VARCHAR(100) NULL UNIQUE COMMENT 'URL amigable: mitienda.drinkgo.com',
    dominio_personalizado VARCHAR(200) NULL COMMENT 'Dominio propio: www.mitienda.com',
    
    -- Configuración visual (banners, mensaje de bienvenida, colores)
    config_visual JSON NULL COMMENT '{"banners": [{"url":"...", "orden":1}], "categorias_destacadas": [1,5,8], "mensaje_bienvenida": "...", "color_primario": "#FF5733"}',
    
    -- SEO (título, descripción, keywords)
    config_seo JSON NULL COMMENT '{"titulo": "...", "descripcion": "...", "keywords": "licores, vinos, cerveza"}',
    
    -- Marketing (Analytics, Pixel, Redes Sociales)
    config_marketing JSON NULL COMMENT '{"google_analytics": "UA-...", "pixel_facebook": "...", "redes_sociales": {"facebook": "...", "instagram": "..."}}',
    
    -- Reglas de negocio (montos, invitados, edad, impuestos)
    config_reglas JSON NULL COMMENT '{"monto_minimo_pedido": 20.00, "monto_maximo_pedido": 5000.00, "permitir_compra_invitado": false, "requiere_verificacion_edad": true, "mostrar_precios_con_impuesto": true}',
    
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_cfgtienda_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_cfgtienda_negocio (negocio_id),
    INDEX idx_cfgtienda_slug (slug_tienda)
) ENGINE=InnoDB COMMENT='Configuración de la tienda online - Optimizada con JSON temáticos (RF-STF-001..003)';

CREATE TABLE paginas_tienda_online (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    negocio_id BIGINT UNSIGNED NOT NULL,
    tipo ENUM('contenido','legal','sistema') NOT NULL DEFAULT 'contenido' COMMENT 'contenido=Sobre Nosotros, legal=Términos/Políticas, sistema=404/Mantenimiento',
    titulo VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL COMMENT 'URL amigable: terminos-y-condiciones, sobre-nosotros',
    contenido LONGTEXT NULL,
    esta_publicado TINYINT(1) NOT NULL DEFAULT 0,
    orden INT NOT NULL DEFAULT 0,
    
    -- SEO por página
    meta_titulo VARCHAR(200) NULL,
    meta_descripcion VARCHAR(500) NULL,
    
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_pagtienda_negocio FOREIGN KEY (negocio_id) REFERENCES negocios(id),
    UNIQUE KEY uk_pagtienda_negocio_slug (negocio_id, slug),
    INDEX idx_pagtienda_negocio (negocio_id),
    INDEX idx_pagtienda_tipo (negocio_id, tipo)
) ENGINE=InnoDB COMMENT='Páginas del CMS - Contenido dinámico, legal y sistema';

-- ============================================================
-- BLOQUE 15: VISTAS DE CONSULTA Y RESUMEN
-- ============================================================

CREATE OR REPLACE VIEW vw_resumen_negocio AS
SELECT
    n.id AS negocio_id,
    n.razon_social,
    n.nombre_comercial,
    n.estado AS estado_negocio,
    s.id AS suscripcion_id,
    ps.nombre AS nombre_plan,
    s.estado AS estado_suscripcion,
    s.fin_periodo_actual,
    (SELECT COUNT(*) FROM sedes sd WHERE sd.negocio_id = n.id AND sd.esta_activo = 1) AS sedes_activas,
    (SELECT COUNT(*) FROM usuarios u WHERE u.negocio_id = n.id AND u.esta_activo = 1) AS usuarios_activos,
    (SELECT COUNT(*) FROM productos p WHERE p.negocio_id = n.id AND p.esta_activo = 1) AS productos_activos
FROM negocios n
LEFT JOIN suscripciones s ON s.negocio_id = n.id AND s.estado = 'activa'
LEFT JOIN planes_suscripcion ps ON ps.id = s.plan_id;

CREATE OR REPLACE VIEW vw_resumen_stock_producto AS
SELECT
    p.negocio_id,
    p.id AS producto_id,
    p.sku,
    p.nombre AS nombre_producto,
    p.precio_venta,
    a.sede_id,
    si.almacen_id,
    a.nombre AS nombre_almacen,
    si.cantidad_en_mano,
    si.cantidad_reservada,
    si.cantidad_disponible,
    p.stock_minimo,
    p.punto_reorden,
    CASE
        WHEN si.cantidad_en_mano <= 0 THEN 'sin_stock'
        WHEN si.cantidad_en_mano <= COALESCE(p.stock_minimo, 0) THEN 'stock_bajo'
        WHEN p.stock_maximo IS NOT NULL AND si.cantidad_en_mano >= p.stock_maximo THEN 'sobrestock'
        ELSE 'normal'
    END AS estado_stock
FROM productos p
INNER JOIN stock_inventario si ON si.producto_id = p.id
INNER JOIN almacenes a ON a.id = si.almacen_id
WHERE p.esta_activo = 1;

CREATE OR REPLACE VIEW vw_resumen_ventas_diarias AS
SELECT
    v.negocio_id,
    v.sede_id,
    DATE(v.creado_en) AS fecha_venta,
    v.tipo_venta,
    COUNT(v.id) AS total_ventas,
    SUM(v.subtotal) AS total_subtotal,
    SUM(v.monto_descuento) AS total_descuentos,
    SUM(v.monto_impuesto) AS total_impuestos,
    SUM(v.total) AS total_ingresos,
    AVG(v.total) AS ticket_promedio
FROM ventas v
WHERE v.estado = 'completada'
GROUP BY v.negocio_id, v.sede_id, DATE(v.creado_en), v.tipo_venta;

CREATE OR REPLACE VIEW vw_lotes_por_vencer AS
SELECT
    li.negocio_id,
    li.producto_id,
    p.nombre AS nombre_producto,
    li.almacen_id,
    a.nombre AS nombre_almacen,
    li.numero_lote,
    li.cantidad_restante,
    li.fecha_vencimiento,
    DATEDIFF(li.fecha_vencimiento, CURDATE()) AS dias_para_vencer
FROM lotes_inventario li
INNER JOIN productos p ON p.id = li.producto_id
INNER JOIN almacenes a ON a.id = li.almacen_id
WHERE li.estado = 'disponible'
  AND li.cantidad_restante > 0
  AND li.fecha_vencimiento IS NOT NULL
  AND li.fecha_vencimiento <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER BY li.fecha_vencimiento ASC;

-- ============================================================
-- FIN DEL SCRIPT - DRINKGO DATABASE
-- ============================================================
