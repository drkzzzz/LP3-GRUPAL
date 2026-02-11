-- ============================================================
-- DRINKGO - SISTEMA MULTI-TENANT PARA LICORERÍAS
-- Base de datos relacional MySQL/MariaDB (XAMPP compatible)
-- Generado: 2026-02-09
-- ============================================================
-- BLOQUE 1: INFRAESTRUCTURA BASE, PLATAFORMA SaaS Y TENANTS
-- ============================================================

DROP DATABASE IF EXISTS drinkgo_db;
CREATE DATABASE drinkgo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE drinkgo_db;

-- ============================================================
-- 1.1 PLANES DE SUSCRIPCIÓN
-- ============================================================

CREATE TABLE subscription_plans (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NULL,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'PEN',
    billing_period ENUM('monthly','quarterly','semiannual','annual') NOT NULL DEFAULT 'monthly',
    trial_days INT UNSIGNED NOT NULL DEFAULT 0,
    max_branches INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Máximo de sedes permitidas',
    max_users INT UNSIGNED NOT NULL DEFAULT 5,
    max_products INT UNSIGNED NOT NULL DEFAULT 500,
    max_warehouses_per_branch INT UNSIGNED NOT NULL DEFAULT 2,
    allows_pos TINYINT(1) NOT NULL DEFAULT 1,
    allows_storefront TINYINT(1) NOT NULL DEFAULT 0,
    allows_delivery TINYINT(1) NOT NULL DEFAULT 0,
    allows_tables TINYINT(1) NOT NULL DEFAULT 0,
    allows_electronic_billing TINYINT(1) NOT NULL DEFAULT 0,
    allows_multi_warehouse TINYINT(1) NOT NULL DEFAULT 0,
    allows_advanced_reports TINYINT(1) NOT NULL DEFAULT 0,
    allows_api_access TINYINT(1) NOT NULL DEFAULT 0,
    features_json JSON NULL COMMENT 'Funcionalidades extra en formato JSON',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    version INT UNSIGNED NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_plans_active (is_active),
    INDEX idx_plans_slug (slug)
) ENGINE=InnoDB COMMENT='Planes de suscripción de la plataforma (RF-PLT-002..005)';

CREATE TABLE subscription_plan_versions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT UNSIGNED NOT NULL,
    version INT UNSIGNED NOT NULL,
    snapshot_json JSON NOT NULL COMMENT 'Snapshot completo del plan en esta versión',
    changed_by BIGINT UNSIGNED NULL,
    change_reason VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_plan_ver_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id),
    UNIQUE KEY uk_plan_version (plan_id, version)
) ENGINE=InnoDB COMMENT='Versionado de planes (RF-PLT-003)';

-- ============================================================
-- 1.2 TENANTS (NEGOCIOS / LICORERÍAS)
-- ============================================================

CREATE TABLE tenants (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    business_name VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200) NULL COMMENT 'Nombre comercial',
    tax_id VARCHAR(20) NULL COMMENT 'RUC',
    tax_id_type ENUM('RUC','DNI','CE','OTHER') NOT NULL DEFAULT 'RUC',
    legal_representative VARCHAR(200) NULL,
    legal_rep_document VARCHAR(20) NULL,
    business_type VARCHAR(100) NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30) NULL,
    address_line VARCHAR(300) NULL,
    city VARCHAR(100) NULL,
    state_province VARCHAR(100) NULL,
    country VARCHAR(3) NOT NULL DEFAULT 'PE',
    postal_code VARCHAR(20) NULL,
    logo_url VARCHAR(500) NULL,
    favicon_url VARCHAR(500) NULL,
    primary_color VARCHAR(7) NULL DEFAULT '#1A1A2E' COMMENT 'Color corporativo HEX',
    secondary_color VARCHAR(7) NULL DEFAULT '#E94560',
    accent_color VARCHAR(7) NULL,
    default_currency VARCHAR(3) NOT NULL DEFAULT 'PEN',
    default_language VARCHAR(5) NOT NULL DEFAULT 'es',
    timezone VARCHAR(50) NOT NULL DEFAULT 'America/Lima',
    date_format VARCHAR(20) NOT NULL DEFAULT 'DD/MM/YYYY',
    status ENUM('active','suspended','cancelled','pending','trial') NOT NULL DEFAULT 'pending',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_tenant_status (status),
    INDEX idx_tenant_uuid (uuid),
    INDEX idx_tenant_tax_id (tax_id)
) ENGINE=InnoDB COMMENT='Negocios/Licorerías - Entidad principal multi-tenant (RF-PLT-001, RF-ADM-001..002)';

-- ============================================================
-- 1.3 SUSCRIPCIONES DE TENANTS
-- ============================================================

CREATE TABLE subscriptions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    plan_id BIGINT UNSIGNED NOT NULL,
    plan_version INT UNSIGNED NOT NULL DEFAULT 1,
    status ENUM('active','trial','past_due','suspended','cancelled','expired') NOT NULL DEFAULT 'trial',
    trial_starts_at DATE NULL,
    trial_ends_at DATE NULL,
    current_period_start DATE NOT NULL,
    current_period_end DATE NOT NULL,
    next_billing_date DATE NULL,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason TEXT NULL,
    suspended_at TIMESTAMP NULL,
    suspension_reason TEXT NULL,
    auto_renew TINYINT(1) NOT NULL DEFAULT 1,
    payment_method_token VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id),
    INDEX idx_sub_tenant (tenant_id),
    INDEX idx_sub_status (status),
    INDEX idx_sub_next_billing (next_billing_date)
) ENGINE=InnoDB COMMENT='Suscripciones activas de cada tenant (RF-PLT-006, RF-FAC-001..002)';

CREATE TABLE subscription_invoices (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT UNSIGNED NOT NULL,
    tenant_id BIGINT UNSIGNED NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'PEN',
    status ENUM('draft','pending','paid','failed','refunded','void') NOT NULL DEFAULT 'pending',
    payment_method VARCHAR(50) NULL,
    payment_reference VARCHAR(255) NULL,
    paid_at TIMESTAMP NULL,
    retry_count INT UNSIGNED NOT NULL DEFAULT 0,
    next_retry_at TIMESTAMP NULL,
    notes TEXT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sinv_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    CONSTRAINT fk_sinv_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    INDEX idx_sinv_tenant (tenant_id),
    INDEX idx_sinv_status (status),
    INDEX idx_sinv_due (due_date)
) ENGINE=InnoDB COMMENT='Facturas de suscripción plataforma (RF-FAC-001)';

-- ============================================================
-- 1.4 CONFIGURACIÓN GLOBAL DE PLATAFORMA
-- ============================================================

CREATE TABLE platform_global_config (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(150) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    value_type ENUM('string','number','boolean','json','date') NOT NULL DEFAULT 'string',
    category VARCHAR(100) NOT NULL DEFAULT 'general',
    description VARCHAR(500) NULL,
    is_editable TINYINT(1) NOT NULL DEFAULT 1,
    version INT UNSIGNED NOT NULL DEFAULT 1,
    updated_by BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pgc_category (category),
    INDEX idx_pgc_key (config_key)
) ENGINE=InnoDB COMMENT='Parámetros globales de la plataforma (RF-CGL-001)';

CREATE TABLE platform_config_history (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    config_id BIGINT UNSIGNED NOT NULL,
    old_value TEXT NULL,
    new_value TEXT NOT NULL,
    changed_by BIGINT UNSIGNED NULL,
    change_reason VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pch_config FOREIGN KEY (config_id) REFERENCES platform_global_config(id),
    INDEX idx_pch_config (config_id)
) ENGINE=InnoDB COMMENT='Historial de cambios de configuración global';

-- ============================================================
-- 1.5 REPORTES DE PLATAFORMA (SUPERADMIN)
-- ============================================================

CREATE TABLE platform_reports (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    report_type VARCHAR(100) NOT NULL,
    report_name VARCHAR(200) NOT NULL,
    parameters_json JSON NULL,
    result_summary_json JSON NULL,
    file_url VARCHAR(500) NULL,
    generated_by BIGINT UNSIGNED NULL,
    status ENUM('pending','processing','completed','failed') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    INDEX idx_pr_type (report_type),
    INDEX idx_pr_status (status)
) ENGINE=InnoDB COMMENT='Reportes consolidados de plataforma (RF-RPL-001)';

-- ============================================================
-- BLOQUE 2: USUARIOS, ROLES, PERMISOS Y SEGURIDAD
-- ============================================================

-- ============================================================
-- 2.1 USUARIOS DE PLATAFORMA (SUPERADMIN)
-- ============================================================

CREATE TABLE platform_users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NULL,
    avatar_url VARCHAR(500) NULL,
    role ENUM('superadmin','platform_support','platform_viewer') NOT NULL DEFAULT 'superadmin',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    last_login_at TIMESTAMP NULL,
    last_login_ip VARCHAR(45) NULL,
    password_changed_at TIMESTAMP NULL,
    failed_login_attempts INT UNSIGNED NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_pu_email (email),
    INDEX idx_pu_role (role)
) ENGINE=InnoDB COMMENT='Usuarios de la plataforma (SuperAdmin y soporte)';

-- ============================================================
-- 2.2 MÓDULOS Y PERMISOS DEL SISTEMA
-- ============================================================

CREATE TABLE system_modules (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(300) NULL,
    parent_module_id BIGINT UNSIGNED NULL,
    icon VARCHAR(50) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_smod_parent FOREIGN KEY (parent_module_id) REFERENCES system_modules(id),
    INDEX idx_smod_code (code)
) ENGINE=InnoDB COMMENT='Módulos del sistema para control de acceso';

CREATE TABLE system_permissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    module_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(300) NULL,
    action_type ENUM('view','create','edit','delete','export','approve','config','full') NOT NULL DEFAULT 'view',
    CONSTRAINT fk_sperm_module FOREIGN KEY (module_id) REFERENCES system_modules(id),
    INDEX idx_sperm_code (code),
    INDEX idx_sperm_module (module_id)
) ENGINE=InnoDB COMMENT='Permisos granulares por módulo (RF-ADM-017)';

-- ============================================================
-- 2.3 ROLES (POR TENANT)
-- ============================================================

CREATE TABLE roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description VARCHAR(300) NULL,
    is_system_role TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Roles predefinidos del sistema',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_role_tenant_slug (tenant_id, slug),
    INDEX idx_role_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Roles por tenant (RF-ADM-016)';

CREATE TABLE role_permissions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT UNSIGNED NOT NULL,
    permission_id BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES system_permissions(id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_rp_role (role_id)
) ENGINE=InnoDB COMMENT='Permisos asignados a roles (RF-ADM-017)';

-- ============================================================
-- 2.4 USUARIOS DE TENANT
-- ============================================================

CREATE TABLE users (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    tenant_id BIGINT UNSIGNED NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    document_type ENUM('DNI','CE','PASSPORT','OTHER') NULL,
    document_number VARCHAR(20) NULL,
    phone VARCHAR(30) NULL,
    avatar_url VARCHAR(500) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    email_verified_at TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,
    last_login_ip VARCHAR(45) NULL,
    password_changed_at TIMESTAMP NULL,
    failed_login_attempts INT UNSIGNED NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULL,
    must_change_password TINYINT(1) NOT NULL DEFAULT 0,
    language VARCHAR(5) NOT NULL DEFAULT 'es',
    preferred_date_format VARCHAR(20) NULL,
    preferred_timezone VARCHAR(50) NULL,
    notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    notification_preferences_json JSON NULL COMMENT 'Preferencias de notificación detalladas',
    default_pos_view ENUM('grid','list') NOT NULL DEFAULT 'grid',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_user_tenant_email (tenant_id, email),
    INDEX idx_user_tenant (tenant_id),
    INDEX idx_user_email (email),
    INDEX idx_user_uuid (uuid),
    INDEX idx_user_active (tenant_id, is_active)
) ENGINE=InnoDB COMMENT='Usuarios de cada negocio (RF-ADM-011..013, RF-ADM-020..023)';

CREATE TABLE user_roles (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    role_id BIGINT UNSIGNED NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT UNSIGNED NULL,
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_ur_user (user_id)
) ENGINE=InnoDB COMMENT='Roles asignados a usuarios (RF-ADM-014)';

CREATE TABLE password_reset_tokens (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NULL,
    platform_user_id BIGINT UNSIGNED NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_prt_puser FOREIGN KEY (platform_user_id) REFERENCES platform_users(id) ON DELETE CASCADE,
    INDEX idx_prt_token (token),
    INDEX idx_prt_expires (expires_at)
) ENGINE=InnoDB COMMENT='Tokens de recuperación de contraseña (RF-ADM-021)';

CREATE TABLE user_sessions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NULL,
    platform_user_id BIGINT UNSIGNED NULL,
    token_hash VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,
    device_info VARCHAR(200) NULL,
    expires_at TIMESTAMP NOT NULL,
    last_activity_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_us_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_us_puser FOREIGN KEY (platform_user_id) REFERENCES platform_users(id) ON DELETE CASCADE,
    INDEX idx_us_token (token_hash),
    INDEX idx_us_user (user_id),
    INDEX idx_us_active (is_active, expires_at)
) ENGINE=InnoDB COMMENT='Sesiones de usuario activas';

-- ============================================================
-- 2.5 AUDITORÍA
-- ============================================================

CREATE TABLE audit_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NULL COMMENT 'NULL para acciones de plataforma',
    user_id BIGINT UNSIGNED NULL,
    platform_user_id BIGINT UNSIGNED NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT UNSIGNED NULL,
    old_values JSON NULL,
    new_values JSON NULL,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(500) NULL,
    module VARCHAR(100) NULL,
    description TEXT NULL,
    severity ENUM('info','warning','critical') NOT NULL DEFAULT 'info',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_al_tenant (tenant_id),
    INDEX idx_al_user (user_id),
    INDEX idx_al_action (action),
    INDEX idx_al_entity (entity_type, entity_id),
    INDEX idx_al_created (created_at),
    INDEX idx_al_module (module),
    INDEX idx_al_severity (severity)
) ENGINE=InnoDB COMMENT='Log de auditoría completo (RF-L-002, RF-ADM-018)';

-- ============================================================
-- BLOQUE 3: SEDES, ALMACENES, HORARIOS Y CONFIGURACIÓN DE NEGOCIO
-- ============================================================

-- ============================================================
-- 3.1 SEDES (BRANCHES)
-- ============================================================

CREATE TABLE branches (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    address_line VARCHAR(300) NOT NULL,
    city VARCHAR(100) NULL,
    state_province VARCHAR(100) NULL,
    country VARCHAR(3) NOT NULL DEFAULT 'PE',
    postal_code VARCHAR(20) NULL,
    latitude DECIMAL(10,8) NULL,
    longitude DECIMAL(11,8) NULL,
    phone VARCHAR(30) NULL,
    email VARCHAR(150) NULL,
    manager_user_id BIGINT UNSIGNED NULL,
    is_main TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Sede principal',
    tables_module_enabled TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'RF-ADM-010',
    delivery_enabled TINYINT(1) NOT NULL DEFAULT 0,
    pickup_enabled TINYINT(1) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deactivated_at TIMESTAMP NULL,
    CONSTRAINT fk_branch_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_branch_manager FOREIGN KEY (manager_user_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_branch_tenant_code (tenant_id, code),
    INDEX idx_branch_tenant (tenant_id),
    INDEX idx_branch_active (tenant_id, is_active)
) ENGINE=InnoDB COMMENT='Sedes del negocio (RF-ADM-003..005, RF-ADM-010)';

CREATE TABLE user_branches (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    is_default TINYINT(1) NOT NULL DEFAULT 0,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT UNSIGNED NULL,
    CONSTRAINT fk_ub_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ub_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    UNIQUE KEY uk_user_branch (user_id, branch_id),
    INDEX idx_ub_user (user_id),
    INDEX idx_ub_branch (branch_id)
) ENGINE=InnoDB COMMENT='Asignación de usuarios a sedes (RF-ADM-015)';

-- ============================================================
-- 3.2 ALMACENES
-- ============================================================

CREATE TABLE warehouses (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    storage_type ENUM('ambient','cold','frozen','mixed') NOT NULL DEFAULT 'ambient',
    temperature_min DECIMAL(5,2) NULL COMMENT 'Temperatura mínima °C',
    temperature_max DECIMAL(5,2) NULL COMMENT 'Temperatura máxima °C',
    capacity_description VARCHAR(300) NULL,
    is_default TINYINT(1) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wh_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_wh_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    UNIQUE KEY uk_wh_tenant_code (tenant_id, code),
    INDEX idx_wh_branch (branch_id),
    INDEX idx_wh_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Almacenes por sede (RF-ADM-006)';

-- ============================================================
-- 3.3 HORARIOS DE OPERACIÓN
-- ============================================================

CREATE TABLE branch_schedules (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT UNSIGNED NOT NULL,
    day_of_week TINYINT UNSIGNED NOT NULL COMMENT '0=Domingo, 1=Lunes ... 6=Sábado',
    opens_at TIME NOT NULL,
    closes_at TIME NOT NULL,
    is_closed TINYINT(1) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bs_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    UNIQUE KEY uk_bs_branch_day (branch_id, day_of_week),
    INDEX idx_bs_branch (branch_id)
) ENGINE=InnoDB COMMENT='Horarios de operación por sede (RF-ADM-007)';

CREATE TABLE branch_special_schedules (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    branch_id BIGINT UNSIGNED NOT NULL,
    date DATE NOT NULL,
    opens_at TIME NULL,
    closes_at TIME NULL,
    is_closed TINYINT(1) NOT NULL DEFAULT 0,
    reason VARCHAR(200) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bss_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    UNIQUE KEY uk_bss_branch_date (branch_id, date),
    INDEX idx_bss_branch (branch_id)
) ENGINE=InnoDB COMMENT='Horarios especiales/feriados por sede';

-- ============================================================
-- 3.4 RESTRICCIONES DE VENTA DE ALCOHOL
-- ============================================================

CREATE TABLE alcohol_sale_restrictions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NULL COMMENT 'NULL aplica a todo el tenant',
    restriction_type ENUM('age_verification','time_restriction','day_restriction','special_event') NOT NULL,
    min_age INT UNSIGNED NULL DEFAULT 18,
    allowed_from_time TIME NULL,
    allowed_to_time TIME NULL,
    restricted_days JSON NULL COMMENT 'Array de días restringidos [0,6]',
    description VARCHAR(300) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_asr_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_asr_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    INDEX idx_asr_tenant (tenant_id),
    INDEX idx_asr_branch (branch_id)
) ENGINE=InnoDB COMMENT='Restricciones de venta de alcohol (RF-ADM-008)';

-- ============================================================
-- 3.5 ZONAS DE DELIVERY
-- ============================================================

CREATE TABLE delivery_zones (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(300) NULL,
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    min_order_amount DECIMAL(10,2) NULL,
    estimated_delivery_minutes INT UNSIGNED NULL,
    polygon_coordinates JSON NULL COMMENT 'Coordenadas GeoJSON del polígono',
    radius_km DECIMAL(6,2) NULL COMMENT 'Radio en km si es circular',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dz_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_dz_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    INDEX idx_dz_tenant (tenant_id),
    INDEX idx_dz_branch (branch_id)
) ENGINE=InnoDB COMMENT='Zonas de delivery por sede (RF-ADM-009)';

-- ============================================================
-- 3.6 MESAS (PARA CONSUMO EN LOCAL)
-- ============================================================

CREATE TABLE table_areas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT 'Ej: Terraza, Salón Principal, Barra',
    description VARCHAR(300) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ta_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_ta_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    INDEX idx_ta_branch (branch_id)
) ENGINE=InnoDB COMMENT='Áreas/zonas de mesas por sede';

CREATE TABLE tables (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    area_id BIGINT UNSIGNED NULL,
    table_number VARCHAR(20) NOT NULL,
    label VARCHAR(50) NULL COMMENT 'Nombre visible: Mesa VIP 1',
    capacity INT UNSIGNED NOT NULL DEFAULT 4,
    qr_code VARCHAR(255) NULL UNIQUE,
    status ENUM('available','occupied','reserved','maintenance','inactive') NOT NULL DEFAULT 'available',
    position_x INT NULL COMMENT 'Coordenada X en mapa visual',
    position_y INT NULL COMMENT 'Coordenada Y en mapa visual',
    shape ENUM('round','square','rectangle') NULL DEFAULT 'square',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tbl_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_tbl_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_tbl_area FOREIGN KEY (area_id) REFERENCES table_areas(id) ON DELETE SET NULL,
    UNIQUE KEY uk_tbl_branch_number (branch_id, table_number),
    INDEX idx_tbl_tenant (tenant_id),
    INDEX idx_tbl_branch (branch_id),
    INDEX idx_tbl_status (branch_id, status)
) ENGINE=InnoDB COMMENT='Mesas por sede (módulo de mesas RF-ADM-010)';

-- ============================================================
-- 3.7 CONFIGURACIÓN POR TENANT
-- ============================================================

CREATE TABLE tenant_config (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    config_key VARCHAR(150) NOT NULL,
    config_value TEXT NOT NULL,
    value_type ENUM('string','number','boolean','json','date') NOT NULL DEFAULT 'string',
    category VARCHAR(100) NOT NULL DEFAULT 'general',
    description VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tc_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_tc_tenant_key (tenant_id, config_key),
    INDEX idx_tc_tenant (tenant_id),
    INDEX idx_tc_category (tenant_id, category)
) ENGINE=InnoDB COMMENT='Parámetros de configuración por tenant (RF-ADM-022)';

-- ============================================================
-- 3.8 NOTIFICACIONES
-- ============================================================

CREATE TABLE notification_templates (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NULL COMMENT 'NULL = plantilla global',
    code VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    channel ENUM('email','sms','push','in_app','whatsapp') NOT NULL DEFAULT 'in_app',
    subject VARCHAR(300) NULL,
    body_template TEXT NOT NULL,
    variables_json JSON NULL COMMENT 'Variables disponibles para la plantilla',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_nt_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    INDEX idx_nt_tenant (tenant_id),
    INDEX idx_nt_code (code)
) ENGINE=InnoDB COMMENT='Plantillas de notificación (RF-ADM-024)';

CREATE TABLE notifications (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NULL,
    user_id BIGINT UNSIGNED NULL,
    platform_user_id BIGINT UNSIGNED NULL,
    template_id BIGINT UNSIGNED NULL,
    title VARCHAR(300) NOT NULL,
    message TEXT NOT NULL,
    channel ENUM('email','sms','push','in_app','whatsapp') NOT NULL DEFAULT 'in_app',
    data_json JSON NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    read_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    delivery_status ENUM('pending','sent','delivered','failed') NOT NULL DEFAULT 'pending',
    priority ENUM('low','normal','high','urgent') NOT NULL DEFAULT 'normal',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notif_puser FOREIGN KEY (platform_user_id) REFERENCES platform_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_notif_template FOREIGN KEY (template_id) REFERENCES notification_templates(id) ON DELETE SET NULL,
    INDEX idx_notif_user (user_id),
    INDEX idx_notif_tenant (tenant_id),
    INDEX idx_notif_read (user_id, is_read),
    INDEX idx_notif_created (created_at)
) ENGINE=InnoDB COMMENT='Notificaciones del sistema (RF-ADM-024)';

-- ============================================================
-- 3.9 MÉTODOS DE PAGO POR TENANT
-- ============================================================

CREATE TABLE payment_methods (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    type ENUM('cash','credit_card','debit_card','bank_transfer','digital_wallet','yape','plin','qr','other') NOT NULL,
    provider VARCHAR(100) NULL COMMENT 'Proveedor: Visa, Mastercard, Yape, etc.',
    config_json JSON NULL COMMENT 'Configuración del método (llaves, endpoints)',
    requires_reference TINYINT(1) NOT NULL DEFAULT 0,
    requires_approval TINYINT(1) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    is_available_pos TINYINT(1) NOT NULL DEFAULT 1,
    is_available_storefront TINYINT(1) NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pm_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_pm_tenant_code (tenant_id, code),
    INDEX idx_pm_tenant (tenant_id),
    INDEX idx_pm_active (tenant_id, is_active)
) ENGINE=InnoDB COMMENT='Métodos de pago por tenant (RF-ADM-025)';

-- ============================================================
-- BLOQUE 4: CATÁLOGO DE PRODUCTOS
-- ============================================================

-- ============================================================
-- 4.1 CATEGORÍAS DE PRODUCTOS
-- ============================================================

CREATE TABLE categories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    parent_id BIGINT UNSIGNED NULL,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL,
    description TEXT NULL,
    image_url VARCHAR(500) NULL,
    icon VARCHAR(50) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_visible_storefront TINYINT(1) NOT NULL DEFAULT 1,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cat_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_cat_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    UNIQUE KEY uk_cat_tenant_slug (tenant_id, slug),
    INDEX idx_cat_tenant (tenant_id),
    INDEX idx_cat_parent (parent_id),
    INDEX idx_cat_active (tenant_id, is_active)
) ENGINE=InnoDB COMMENT='Categorías de productos (RF-PRO-006..009)';

-- ============================================================
-- 4.2 MARCAS
-- ============================================================

CREATE TABLE brands (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(150) NOT NULL,
    country_of_origin VARCHAR(100) NULL,
    logo_url VARCHAR(500) NULL,
    description TEXT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_brand_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_brand_tenant_slug (tenant_id, slug),
    INDEX idx_brand_tenant (tenant_id),
    INDEX idx_brand_active (tenant_id, is_active)
) ENGINE=InnoDB COMMENT='Marcas de productos (RF-PRO-010..013)';

-- ============================================================
-- 4.3 UNIDADES DE MEDIDA
-- ============================================================

CREATE TABLE units_of_measure (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10) NOT NULL,
    type ENUM('volume','weight','unit','pack','other') NOT NULL DEFAULT 'unit',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_uom_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_uom_tenant_code (tenant_id, code),
    INDEX idx_uom_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Unidades de medida del negocio';

-- ============================================================
-- 4.4 PRODUCTOS
-- ============================================================

CREATE TABLE products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    sku VARCHAR(50) NOT NULL,
    barcode VARCHAR(50) NULL,
    name VARCHAR(250) NOT NULL,
    slug VARCHAR(250) NOT NULL,
    short_description VARCHAR(500) NULL,
    description TEXT NULL,
    category_id BIGINT UNSIGNED NULL,
    brand_id BIGINT UNSIGNED NULL,
    unit_of_measure_id BIGINT UNSIGNED NULL,

    -- Atributos específicos de licorería
    product_type ENUM('alcoholic','non_alcoholic','food','accessory','combo','other') NOT NULL DEFAULT 'alcoholic',
    beverage_type VARCHAR(100) NULL COMMENT 'Tipo: Whisky, Ron, Cerveza, Vino, Pisco, etc.',
    alcohol_degree DECIMAL(5,2) NULL COMMENT 'Grado alcohólico %',
    volume_ml INT UNSIGNED NULL COMMENT 'Volumen en ml',
    origin_country VARCHAR(100) NULL,
    vintage_year SMALLINT UNSIGNED NULL COMMENT 'Año de cosecha (vinos)',
    aging VARCHAR(100) NULL COMMENT 'Tiempo de añejamiento',

    -- Precios
    purchase_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    sale_price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    min_sale_price DECIMAL(10,2) NULL COMMENT 'Precio mínimo permitido',
    wholesale_price DECIMAL(10,2) NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 18.00 COMMENT 'IGV % Perú',
    is_tax_included TINYINT(1) NOT NULL DEFAULT 1,

    -- Stock y almacenamiento
    min_stock INT NOT NULL DEFAULT 0,
    max_stock INT NULL,
    reorder_point INT NULL,
    storage_type ENUM('ambient','cold','frozen') NOT NULL DEFAULT 'ambient',
    optimal_temp_min DECIMAL(5,2) NULL,
    optimal_temp_max DECIMAL(5,2) NULL,
    is_perishable TINYINT(1) NOT NULL DEFAULT 0,
    shelf_life_days INT UNSIGNED NULL,

    -- Dimensiones y peso
    weight_kg DECIMAL(8,3) NULL,
    height_cm DECIMAL(6,2) NULL,
    width_cm DECIMAL(6,2) NULL,
    depth_cm DECIMAL(6,2) NULL,

    -- Visibilidad y flags
    is_visible_pos TINYINT(1) NOT NULL DEFAULT 1,
    is_visible_storefront TINYINT(1) NOT NULL DEFAULT 0,
    is_featured TINYINT(1) NOT NULL DEFAULT 0,
    requires_age_verification TINYINT(1) NOT NULL DEFAULT 0,
    allows_discount TINYINT(1) NOT NULL DEFAULT 1,
    is_active TINYINT(1) NOT NULL DEFAULT 1,

    -- SEO Storefront
    meta_title VARCHAR(200) NULL,
    meta_description VARCHAR(500) NULL,
    meta_keywords VARCHAR(300) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT fk_prod_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_prod_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_prod_brand FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE SET NULL,
    CONSTRAINT fk_prod_uom FOREIGN KEY (unit_of_measure_id) REFERENCES units_of_measure(id) ON DELETE SET NULL,

    UNIQUE KEY uk_prod_tenant_sku (tenant_id, sku),
    INDEX idx_prod_tenant (tenant_id),
    INDEX idx_prod_category (category_id),
    INDEX idx_prod_brand (brand_id),
    INDEX idx_prod_barcode (tenant_id, barcode),
    INDEX idx_prod_type (tenant_id, product_type),
    INDEX idx_prod_active (tenant_id, is_active),
    INDEX idx_prod_slug (tenant_id, slug),
    INDEX idx_prod_storefront (tenant_id, is_visible_storefront, is_active),
    FULLTEXT INDEX ft_prod_search (name, short_description, beverage_type)
) ENGINE=InnoDB COMMENT='Productos del catálogo (RF-PRO-001..004)';

-- ============================================================
-- 4.5 IMÁGENES DE PRODUCTOS
-- ============================================================

CREATE TABLE product_images (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNSIGNED NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500) NULL,
    alt_text VARCHAR(300) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_primary TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pimg_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_pimg_product (product_id)
) ENGINE=InnoDB COMMENT='Imágenes de productos (RF-PRO-005)';

-- ============================================================
-- 4.6 PRODUCTOS POR SEDE (Precios y disponibilidad)
-- ============================================================

CREATE TABLE product_branches (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    sale_price_override DECIMAL(10,2) NULL COMMENT 'Precio específico de sede',
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_pb_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_pb_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    UNIQUE KEY uk_pb_product_branch (product_id, branch_id),
    INDEX idx_pb_branch (branch_id)
) ENGINE=InnoDB COMMENT='Disponibilidad y precio de producto por sede';

-- ============================================================
-- 4.7 COMBOS PROMOCIONALES
-- ============================================================

CREATE TABLE combos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL COMMENT 'Producto virtual tipo combo',
    name VARCHAR(200) NOT NULL,
    description TEXT NULL,
    combo_price DECIMAL(10,2) NOT NULL,
    original_total_price DECIMAL(10,2) NOT NULL COMMENT 'Suma de precios individuales',
    discount_percentage DECIMAL(5,2) NULL,
    max_uses INT UNSIGNED NULL COMMENT 'Límite de usos',
    current_uses INT UNSIGNED NOT NULL DEFAULT 0,
    valid_from DATETIME NULL,
    valid_until DATETIME NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_combo_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_combo_product FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_combo_tenant (tenant_id),
    INDEX idx_combo_active (tenant_id, is_active),
    INDEX idx_combo_dates (valid_from, valid_until)
) ENGINE=InnoDB COMMENT='Combos promocionales (RF-PRO-014)';

CREATE TABLE combo_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    combo_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    quantity INT UNSIGNED NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL COMMENT 'Precio unitario al momento de crear combo',
    sort_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_ci_combo FOREIGN KEY (combo_id) REFERENCES combos(id) ON DELETE CASCADE,
    CONSTRAINT fk_ci_product FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_ci_combo (combo_id)
) ENGINE=InnoDB COMMENT='Productos dentro de un combo (RF-PRO-014)';

-- ============================================================
-- 4.8 ETIQUETAS DE PRODUCTOS
-- ============================================================

CREATE TABLE product_tags (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    color VARCHAR(7) NULL,
    CONSTRAINT fk_ptag_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_ptag_tenant_slug (tenant_id, slug),
    INDEX idx_ptag_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Etiquetas para productos';

CREATE TABLE product_tag_assignments (
    product_id BIGINT UNSIGNED NOT NULL,
    tag_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (product_id, tag_id),
    CONSTRAINT fk_pta_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_pta_tag FOREIGN KEY (tag_id) REFERENCES product_tags(id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='Relación N:M productos-etiquetas';

-- ============================================================
-- BLOQUE 5: INVENTARIO, LOTES Y MOVIMIENTOS
-- ============================================================

-- ============================================================
-- 5.1 STOCK POR ALMACÉN
-- ============================================================

CREATE TABLE inventory_stock (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    warehouse_id BIGINT UNSIGNED NOT NULL,
    quantity_on_hand INT NOT NULL DEFAULT 0,
    quantity_reserved INT NOT NULL DEFAULT 0 COMMENT 'Reservado por pedidos pendientes',
    quantity_available INT GENERATED ALWAYS AS (quantity_on_hand - quantity_reserved) STORED,
    last_counted_at TIMESTAMP NULL,
    last_movement_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_is_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_is_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_is_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    UNIQUE KEY uk_is_product_warehouse (product_id, warehouse_id),
    INDEX idx_is_tenant (tenant_id),
    INDEX idx_is_warehouse (warehouse_id),
    INDEX idx_is_low_stock (tenant_id, quantity_on_hand)
) ENGINE=InnoDB COMMENT='Stock actual por producto y almacén (RF-INV-001)';

-- ============================================================
-- 5.2 LOTES (FIFO)
-- ============================================================

CREATE TABLE inventory_batches (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    warehouse_id BIGINT UNSIGNED NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    quantity_initial INT NOT NULL,
    quantity_remaining INT NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL,
    manufacture_date DATE NULL,
    expiry_date DATE NULL,
    received_date DATE NOT NULL,
    supplier_id BIGINT UNSIGNED NULL,
    purchase_order_id BIGINT UNSIGNED NULL,
    status ENUM('available','depleted','expired','quarantine','returned') NOT NULL DEFAULT 'available',
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ib_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_ib_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_ib_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    UNIQUE KEY uk_ib_tenant_batch (tenant_id, batch_number),
    INDEX idx_ib_tenant (tenant_id),
    INDEX idx_ib_product (product_id, warehouse_id),
    INDEX idx_ib_expiry (expiry_date),
    INDEX idx_ib_status (status),
    INDEX idx_ib_fifo (product_id, warehouse_id, received_date, status)
) ENGINE=InnoDB COMMENT='Lotes de inventario con FIFO (RF-INV-002..003)';

-- ============================================================
-- 5.3 MOVIMIENTOS DE INVENTARIO
-- ============================================================

CREATE TABLE inventory_movements (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    warehouse_id BIGINT UNSIGNED NOT NULL,
    batch_id BIGINT UNSIGNED NULL,
    movement_type ENUM(
        'purchase_entry','sale_exit','return_entry','return_exit',
        'transfer_in','transfer_out','adjustment_in','adjustment_out',
        'waste','breakage','expiry','initial_stock','production_in','production_out'
    ) NOT NULL,
    quantity INT NOT NULL COMMENT 'Positivo o negativo según tipo',
    unit_cost DECIMAL(10,2) NULL,
    reference_type VARCHAR(50) NULL COMMENT 'Tipo de documento referencia',
    reference_id BIGINT UNSIGNED NULL COMMENT 'ID del documento referencia',
    reason VARCHAR(300) NULL,
    performed_by BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_im_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_im_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_im_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_im_batch FOREIGN KEY (batch_id) REFERENCES inventory_batches(id),
    CONSTRAINT fk_im_user FOREIGN KEY (performed_by) REFERENCES users(id),
    INDEX idx_im_tenant (tenant_id),
    INDEX idx_im_product (product_id),
    INDEX idx_im_warehouse (warehouse_id),
    INDEX idx_im_type (movement_type),
    INDEX idx_im_reference (reference_type, reference_id),
    INDEX idx_im_created (tenant_id, created_at)
) ENGINE=InnoDB COMMENT='Movimientos de inventario (RF-INV-004..006)';

-- ============================================================
-- 5.4 TRANSFERENCIAS ENTRE ALMACENES
-- ============================================================

CREATE TABLE inventory_transfers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    transfer_number VARCHAR(30) NOT NULL,
    from_warehouse_id BIGINT UNSIGNED NOT NULL,
    to_warehouse_id BIGINT UNSIGNED NOT NULL,
    status ENUM('draft','pending','in_transit','received','cancelled') NOT NULL DEFAULT 'draft',
    requested_by BIGINT UNSIGNED NULL,
    approved_by BIGINT UNSIGNED NULL,
    received_by BIGINT UNSIGNED NULL,
    notes TEXT NULL,
    requested_at TIMESTAMP NULL,
    approved_at TIMESTAMP NULL,
    shipped_at TIMESTAMP NULL,
    received_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_it_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_it_from_wh FOREIGN KEY (from_warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_it_to_wh FOREIGN KEY (to_warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_it_req_by FOREIGN KEY (requested_by) REFERENCES users(id),
    CONSTRAINT fk_it_app_by FOREIGN KEY (approved_by) REFERENCES users(id),
    CONSTRAINT fk_it_rec_by FOREIGN KEY (received_by) REFERENCES users(id),
    UNIQUE KEY uk_it_tenant_number (tenant_id, transfer_number),
    INDEX idx_it_tenant (tenant_id),
    INDEX idx_it_status (status)
) ENGINE=InnoDB COMMENT='Transferencias entre almacenes (RF-INV-005)';

CREATE TABLE inventory_transfer_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    transfer_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    batch_id BIGINT UNSIGNED NULL,
    quantity_requested INT NOT NULL,
    quantity_sent INT NULL,
    quantity_received INT NULL,
    notes VARCHAR(300) NULL,
    CONSTRAINT fk_iti_transfer FOREIGN KEY (transfer_id) REFERENCES inventory_transfers(id) ON DELETE CASCADE,
    CONSTRAINT fk_iti_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_iti_batch FOREIGN KEY (batch_id) REFERENCES inventory_batches(id),
    INDEX idx_iti_transfer (transfer_id)
) ENGINE=InnoDB COMMENT='Items de transferencia de inventario';

-- ============================================================
-- 5.5 ALERTAS DE INVENTARIO
-- ============================================================

CREATE TABLE inventory_alerts (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    warehouse_id BIGINT UNSIGNED NULL,
    alert_type ENUM('low_stock','overstock','expiring_soon','expired','reorder_point') NOT NULL,
    message VARCHAR(500) NOT NULL,
    threshold_value INT NULL,
    current_value INT NULL,
    is_resolved TINYINT(1) NOT NULL DEFAULT 0,
    resolved_at TIMESTAMP NULL,
    resolved_by BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ia_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_ia_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_ia_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    INDEX idx_ia_tenant (tenant_id),
    INDEX idx_ia_active (tenant_id, is_resolved),
    INDEX idx_ia_type (alert_type)
) ENGINE=InnoDB COMMENT='Alertas de inventario automáticas';

-- ============================================================
-- BLOQUE 6: PROVEEDORES Y COMPRAS
-- ============================================================

-- ============================================================
-- 6.1 PROVEEDORES
-- ============================================================

CREATE TABLE suppliers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    code VARCHAR(20) NOT NULL,
    business_name VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200) NULL,
    tax_id VARCHAR(20) NULL COMMENT 'RUC del proveedor',
    contact_name VARCHAR(150) NULL,
    contact_phone VARCHAR(30) NULL,
    contact_email VARCHAR(150) NULL,
    address_line VARCHAR(300) NULL,
    city VARCHAR(100) NULL,
    state_province VARCHAR(100) NULL,
    country VARCHAR(3) NOT NULL DEFAULT 'PE',
    payment_terms_days INT UNSIGNED NOT NULL DEFAULT 30 COMMENT 'Plazo de pago en días',
    credit_limit DECIMAL(12,2) NULL,
    bank_name VARCHAR(100) NULL,
    bank_account_number VARCHAR(50) NULL,
    bank_account_cci VARCHAR(50) NULL,
    rating TINYINT UNSIGNED NULL COMMENT 'Rating 1-5',
    notes TEXT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sup_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_sup_tenant_code (tenant_id, code),
    INDEX idx_sup_tenant (tenant_id),
    INDEX idx_sup_active (tenant_id, is_active),
    INDEX idx_sup_tax_id (tenant_id, tax_id)
) ENGINE=InnoDB COMMENT='Proveedores (RF-COM-001..003)';

CREATE TABLE supplier_products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    supplier_sku VARCHAR(50) NULL,
    supplier_price DECIMAL(10,2) NULL,
    lead_time_days INT UNSIGNED NULL,
    min_order_quantity INT UNSIGNED NULL,
    is_preferred TINYINT(1) NOT NULL DEFAULT 0,
    last_purchase_date DATE NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sp_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_product FOREIGN KEY (product_id) REFERENCES products(id),
    UNIQUE KEY uk_sp_supplier_product (supplier_id, product_id),
    INDEX idx_sp_product (product_id)
) ENGINE=InnoDB COMMENT='Productos por proveedor (RF-COM-001)';

-- ============================================================
-- 6.2 ÓRDENES DE COMPRA
-- ============================================================

CREATE TABLE purchase_orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    order_number VARCHAR(30) NOT NULL,
    supplier_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    warehouse_id BIGINT UNSIGNED NOT NULL,
    status ENUM('draft','pending_approval','approved','sent','partial_received','received','cancelled') NOT NULL DEFAULT 'draft',
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'PEN',
    expected_delivery_date DATE NULL,
    actual_delivery_date DATE NULL,
    payment_terms_days INT UNSIGNED NULL,
    payment_due_date DATE NULL,
    notes TEXT NULL,
    created_by BIGINT UNSIGNED NULL,
    approved_by BIGINT UNSIGNED NULL,
    received_by BIGINT UNSIGNED NULL,
    approved_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    received_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_po_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_po_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_po_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_po_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_po_approved_by FOREIGN KEY (approved_by) REFERENCES users(id),
    CONSTRAINT fk_po_received_by FOREIGN KEY (received_by) REFERENCES users(id),
    UNIQUE KEY uk_po_tenant_number (tenant_id, order_number),
    INDEX idx_po_tenant (tenant_id),
    INDEX idx_po_supplier (supplier_id),
    INDEX idx_po_status (tenant_id, status),
    INDEX idx_po_date (tenant_id, created_at)
) ENGINE=InnoDB COMMENT='Órdenes de compra (RF-COM-004..007)';

CREATE TABLE purchase_order_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    quantity_ordered INT NOT NULL,
    quantity_received INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10,2) NOT NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 18.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    notes VARCHAR(300) NULL,
    CONSTRAINT fk_poi_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_poi_product FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_poi_po (purchase_order_id),
    INDEX idx_poi_product (product_id)
) ENGINE=InnoDB COMMENT='Items de orden de compra';

CREATE TABLE purchase_receptions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    purchase_order_id BIGINT UNSIGNED NOT NULL,
    reception_number VARCHAR(30) NOT NULL,
    received_by BIGINT UNSIGNED NULL,
    reception_date DATE NOT NULL,
    notes TEXT NULL,
    status ENUM('pending','partial','completed') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prec_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_prec_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
    CONSTRAINT fk_prec_user FOREIGN KEY (received_by) REFERENCES users(id),
    UNIQUE KEY uk_prec_tenant_number (tenant_id, reception_number),
    INDEX idx_prec_po (purchase_order_id)
) ENGINE=InnoDB COMMENT='Recepciones de mercadería';

CREATE TABLE purchase_reception_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reception_id BIGINT UNSIGNED NOT NULL,
    purchase_order_item_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    batch_id BIGINT UNSIGNED NULL,
    quantity_received INT NOT NULL,
    quantity_rejected INT NOT NULL DEFAULT 0,
    rejection_reason VARCHAR(300) NULL,
    CONSTRAINT fk_pri_reception FOREIGN KEY (reception_id) REFERENCES purchase_receptions(id) ON DELETE CASCADE,
    CONSTRAINT fk_pri_poi FOREIGN KEY (purchase_order_item_id) REFERENCES purchase_order_items(id),
    CONSTRAINT fk_pri_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_pri_batch FOREIGN KEY (batch_id) REFERENCES inventory_batches(id),
    INDEX idx_pri_reception (reception_id)
) ENGINE=InnoDB COMMENT='Items recibidos por recepción';

-- ============================================================
-- BLOQUE 7: CLIENTES
-- ============================================================

CREATE TABLE customers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    uuid CHAR(36) NOT NULL UNIQUE,
    customer_type ENUM('individual','business') NOT NULL DEFAULT 'individual',
    first_name VARCHAR(100) NULL,
    last_name VARCHAR(100) NULL,
    business_name VARCHAR(200) NULL,
    document_type ENUM('DNI','RUC','CE','PASSPORT','OTHER') NULL,
    document_number VARCHAR(20) NULL,
    email VARCHAR(150) NULL,
    phone VARCHAR(30) NULL,
    secondary_phone VARCHAR(30) NULL,
    birth_date DATE NULL,
    gender ENUM('M','F','OTHER','UNDISCLOSED') NULL,
    -- Credenciales para storefront
    password_hash VARCHAR(255) NULL COMMENT 'Para login en storefront',
    email_verified_at TIMESTAMP NULL,
    -- Programa de fidelidad
    loyalty_points INT NOT NULL DEFAULT 0,
    loyalty_tier ENUM('none','bronze','silver','gold','platinum') NOT NULL DEFAULT 'none',
    total_purchases DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_orders INT UNSIGNED NOT NULL DEFAULT 0,
    -- Marketing
    accepts_marketing TINYINT(1) NOT NULL DEFAULT 0,
    marketing_channel VARCHAR(50) NULL COMMENT 'Canal de captación',
    -- Estado
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    notes TEXT NULL,
    last_purchase_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cust_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_cust_tenant_doc (tenant_id, document_type, document_number),
    INDEX idx_cust_tenant (tenant_id),
    INDEX idx_cust_email (tenant_id, email),
    INDEX idx_cust_phone (tenant_id, phone),
    INDEX idx_cust_doc (tenant_id, document_number),
    INDEX idx_cust_uuid (uuid),
    INDEX idx_cust_active (tenant_id, is_active)
) ENGINE=InnoDB COMMENT='Clientes del negocio (RF-CLI-001..005)';

CREATE TABLE customer_addresses (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT UNSIGNED NOT NULL,
    label VARCHAR(50) NULL COMMENT 'Casa, Oficina, etc.',
    address_line VARCHAR(300) NOT NULL,
    address_line_2 VARCHAR(300) NULL,
    city VARCHAR(100) NULL,
    state_province VARCHAR(100) NULL,
    country VARCHAR(3) NOT NULL DEFAULT 'PE',
    postal_code VARCHAR(20) NULL,
    latitude DECIMAL(10,8) NULL,
    longitude DECIMAL(11,8) NULL,
    reference VARCHAR(300) NULL,
    contact_phone VARCHAR(30) NULL,
    is_default TINYINT(1) NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ca_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_ca_customer (customer_id)
) ENGINE=InnoDB COMMENT='Direcciones de clientes (RF-CLI-001)';

-- ============================================================
-- BLOQUE 8: VENTAS, POS Y CAJAS
-- ============================================================

-- ============================================================
-- 8.1 CAJAS REGISTRADORAS
-- ============================================================

CREATE TABLE cash_registers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cr_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_cr_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    UNIQUE KEY uk_cr_branch_code (branch_id, code),
    INDEX idx_cr_tenant (tenant_id),
    INDEX idx_cr_branch (branch_id)
) ENGINE=InnoDB COMMENT='Cajas registradoras por sede (RF-VEN-001)';

CREATE TABLE cash_register_sessions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    cash_register_id BIGINT UNSIGNED NOT NULL,
    opened_by BIGINT UNSIGNED NOT NULL,
    closed_by BIGINT UNSIGNED NULL,
    opening_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    closing_amount DECIMAL(12,2) NULL,
    expected_amount DECIMAL(12,2) NULL,
    difference DECIMAL(12,2) NULL COMMENT 'Diferencia = closing - expected',
    total_sales DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_refunds DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_cash_in DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_cash_out DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    transactions_count INT UNSIGNED NOT NULL DEFAULT 0,
    status ENUM('open','closed','reconciled') NOT NULL DEFAULT 'open',
    opened_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP NULL,
    notes TEXT NULL,
    closing_notes TEXT NULL,
    CONSTRAINT fk_crs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_crs_register FOREIGN KEY (cash_register_id) REFERENCES cash_registers(id),
    CONSTRAINT fk_crs_opened_by FOREIGN KEY (opened_by) REFERENCES users(id),
    CONSTRAINT fk_crs_closed_by FOREIGN KEY (closed_by) REFERENCES users(id),
    INDEX idx_crs_tenant (tenant_id),
    INDEX idx_crs_register (cash_register_id),
    INDEX idx_crs_status (tenant_id, status),
    INDEX idx_crs_opened (opened_at)
) ENGINE=InnoDB COMMENT='Sesiones/turnos de caja (RF-VEN-001..002)';

CREATE TABLE cash_movements (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT UNSIGNED NOT NULL,
    movement_type ENUM('cash_in','cash_out','sale','refund','adjustment') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    reason VARCHAR(300) NULL,
    reference_type VARCHAR(50) NULL,
    reference_id BIGINT UNSIGNED NULL,
    performed_by BIGINT UNSIGNED NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cm_session FOREIGN KEY (session_id) REFERENCES cash_register_sessions(id),
    CONSTRAINT fk_cm_user FOREIGN KEY (performed_by) REFERENCES users(id),
    INDEX idx_cm_session (session_id),
    INDEX idx_cm_type (movement_type)
) ENGINE=InnoDB COMMENT='Movimientos de efectivo en caja';

-- ============================================================
-- 8.2 VENTAS
-- ============================================================

CREATE TABLE sales (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    sale_number VARCHAR(30) NOT NULL,
    sale_type ENUM('pos','storefront','table','phone','other') NOT NULL DEFAULT 'pos',
    session_id BIGINT UNSIGNED NULL COMMENT 'Sesión de caja (ventas POS)',
    customer_id BIGINT UNSIGNED NULL,
    table_id BIGINT UNSIGNED NULL COMMENT 'Mesa (ventas en local)',
    order_id BIGINT UNSIGNED NULL COMMENT 'Pedido asociado (storefront)',
    seller_user_id BIGINT UNSIGNED NULL,

    -- Montos
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_percentage DECIMAL(5,2) NULL,
    discount_reason VARCHAR(200) NULL,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tip_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'PEN',

    -- Estado
    status ENUM('pending','completed','partially_paid','cancelled','refunded','voided') NOT NULL DEFAULT 'pending',

    -- Info fiscal
    requires_invoice TINYINT(1) NOT NULL DEFAULT 0,
    customer_tax_id VARCHAR(20) NULL COMMENT 'RUC para factura',
    customer_business_name VARCHAR(200) NULL,

    -- Verificación edad
    age_verified TINYINT(1) NOT NULL DEFAULT 0,
    age_verified_by BIGINT UNSIGNED NULL,

    notes TEXT NULL,
    internal_notes TEXT NULL,
    completed_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason VARCHAR(500) NULL,
    cancelled_by BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_sale_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_sale_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_sale_session FOREIGN KEY (session_id) REFERENCES cash_register_sessions(id),
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE SET NULL,
    CONSTRAINT fk_sale_table FOREIGN KEY (table_id) REFERENCES tables(id) ON DELETE SET NULL,
    CONSTRAINT fk_sale_seller FOREIGN KEY (seller_user_id) REFERENCES users(id),

    UNIQUE KEY uk_sale_tenant_number (tenant_id, sale_number),
    INDEX idx_sale_tenant (tenant_id),
    INDEX idx_sale_branch (branch_id),
    INDEX idx_sale_customer (customer_id),
    INDEX idx_sale_status (tenant_id, status),
    INDEX idx_sale_date (tenant_id, created_at),
    INDEX idx_sale_type (tenant_id, sale_type),
    INDEX idx_sale_session (session_id)
) ENGINE=InnoDB COMMENT='Ventas (RF-VEN-003..008)';

CREATE TABLE sale_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    batch_id BIGINT UNSIGNED NULL COMMENT 'Lote FIFO asignado',
    combo_id BIGINT UNSIGNED NULL COMMENT 'Si pertenece a un combo',
    product_name VARCHAR(250) NOT NULL COMMENT 'Snapshot del nombre',
    product_sku VARCHAR(50) NOT NULL COMMENT 'Snapshot del SKU',
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_percentage DECIMAL(5,2) NULL,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 18.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    notes VARCHAR(300) NULL,
    CONSTRAINT fk_si_sale FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_si_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_si_batch FOREIGN KEY (batch_id) REFERENCES inventory_batches(id),
    CONSTRAINT fk_si_combo FOREIGN KEY (combo_id) REFERENCES combos(id),
    INDEX idx_si_sale (sale_id),
    INDEX idx_si_product (product_id)
) ENGINE=InnoDB COMMENT='Items de venta';

-- ============================================================
-- 8.3 PAGOS DE VENTAS
-- ============================================================

CREATE TABLE sale_payments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT UNSIGNED NOT NULL,
    payment_method_id BIGINT UNSIGNED NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    received_amount DECIMAL(12,2) NULL COMMENT 'Monto recibido (para calcular vuelto)',
    change_amount DECIMAL(12,2) NULL COMMENT 'Vuelto',
    reference_number VARCHAR(100) NULL COMMENT 'Nro. operación, voucher, etc.',
    authorization_code VARCHAR(100) NULL,
    status ENUM('pending','approved','rejected','refunded') NOT NULL DEFAULT 'approved',
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_by BIGINT UNSIGNED NULL,
    notes VARCHAR(300) NULL,
    CONSTRAINT fk_sp_sale FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
    CONSTRAINT fk_sp_user FOREIGN KEY (processed_by) REFERENCES users(id),
    INDEX idx_sp_sale (sale_id),
    INDEX idx_sp_method (payment_method_id)
) ENGINE=InnoDB COMMENT='Pagos de ventas (soporta pago mixto)';

-- ============================================================
-- BLOQUE 9: PEDIDOS (STOREFRONT, DELIVERY, PICKUP)
-- ============================================================

CREATE TABLE orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    order_number VARCHAR(30) NOT NULL,
    customer_id BIGINT UNSIGNED NOT NULL,
    order_type ENUM('delivery','pickup','dine_in') NOT NULL DEFAULT 'delivery',
    order_source ENUM('storefront','phone','whatsapp','pos','other') NOT NULL DEFAULT 'storefront',

    -- Dirección de entrega (delivery)
    delivery_address_id BIGINT UNSIGNED NULL,
    delivery_address_snapshot JSON NULL COMMENT 'Snapshot de la dirección al momento del pedido',
    delivery_zone_id BIGINT UNSIGNED NULL,

    -- Montos
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tip_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'PEN',

    -- Estado del pedido
    status ENUM(
        'pending','confirmed','preparing','ready','assigned',
        'in_delivery','delivered','picked_up','completed','cancelled','refunded'
    ) NOT NULL DEFAULT 'pending',

    -- Programación
    is_scheduled TINYINT(1) NOT NULL DEFAULT 0,
    scheduled_delivery_at DATETIME NULL,
    estimated_delivery_at DATETIME NULL,

    -- Verificación de edad
    age_verified TINYINT(1) NOT NULL DEFAULT 0,
    age_verification_method VARCHAR(50) NULL,

    -- Info fiscal
    requires_invoice TINYINT(1) NOT NULL DEFAULT 0,
    customer_tax_id VARCHAR(20) NULL,
    customer_business_name VARCHAR(200) NULL,

    notes TEXT NULL COMMENT 'Notas del cliente',
    internal_notes TEXT NULL,

    confirmed_at TIMESTAMP NULL,
    preparing_at TIMESTAMP NULL,
    ready_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    cancelled_at TIMESTAMP NULL,
    cancellation_reason VARCHAR(500) NULL,
    cancelled_by_type ENUM('customer','admin','system') NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_ord_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_ord_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_ord_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_ord_address FOREIGN KEY (delivery_address_id) REFERENCES customer_addresses(id) ON DELETE SET NULL,
    CONSTRAINT fk_ord_zone FOREIGN KEY (delivery_zone_id) REFERENCES delivery_zones(id) ON DELETE SET NULL,

    UNIQUE KEY uk_ord_tenant_number (tenant_id, order_number),
    INDEX idx_ord_tenant (tenant_id),
    INDEX idx_ord_branch (branch_id),
    INDEX idx_ord_customer (customer_id),
    INDEX idx_ord_status (tenant_id, status),
    INDEX idx_ord_date (tenant_id, created_at),
    INDEX idx_ord_type (tenant_id, order_type)
) ENGINE=InnoDB COMMENT='Pedidos de clientes (RF-PED-001..006)';

CREATE TABLE order_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    combo_id BIGINT UNSIGNED NULL,
    product_name VARCHAR(250) NOT NULL,
    product_sku VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 18.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    notes VARCHAR(300) NULL,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_oi_combo FOREIGN KEY (combo_id) REFERENCES combos(id),
    INDEX idx_oi_order (order_id),
    INDEX idx_oi_product (product_id)
) ENGINE=InnoDB COMMENT='Items del pedido';

CREATE TABLE order_payments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    payment_method_id BIGINT UNSIGNED NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    reference_number VARCHAR(100) NULL,
    authorization_code VARCHAR(100) NULL,
    gateway_response JSON NULL,
    status ENUM('pending','processing','approved','rejected','refunded') NOT NULL DEFAULT 'pending',
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_op_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_op_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
    INDEX idx_op_order (order_id),
    INDEX idx_op_status (status)
) ENGINE=InnoDB COMMENT='Pagos de pedidos';

-- ============================================================
-- 9.1 TRACKING DE PEDIDOS
-- ============================================================

CREATE TABLE order_tracking (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    status VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    latitude DECIMAL(10,8) NULL,
    longitude DECIMAL(11,8) NULL,
    performed_by BIGINT UNSIGNED NULL,
    is_visible_to_customer TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ot_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_ot_user FOREIGN KEY (performed_by) REFERENCES users(id),
    INDEX idx_ot_order (order_id),
    INDEX idx_ot_created (created_at)
) ENGINE=InnoDB COMMENT='Historial de tracking de pedidos (RF-PED-005)';

CREATE TABLE delivery_drivers (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    vehicle_type ENUM('motorcycle','car','bicycle','walking') NULL,
    vehicle_plate VARCHAR(20) NULL,
    license_number VARCHAR(30) NULL,
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    current_latitude DECIMAL(10,8) NULL,
    current_longitude DECIMAL(11,8) NULL,
    last_location_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dd_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_dd_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_dd_tenant (tenant_id),
    INDEX idx_dd_available (tenant_id, is_available, is_active)
) ENGINE=InnoDB COMMENT='Repartidores de delivery';

CREATE TABLE order_delivery_assignments (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT UNSIGNED NOT NULL,
    driver_id BIGINT UNSIGNED NOT NULL,
    status ENUM('assigned','accepted','picked_up','in_transit','delivered','failed') NOT NULL DEFAULT 'assigned',
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP NULL,
    picked_up_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    notes VARCHAR(300) NULL,
    CONSTRAINT fk_oda_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_oda_driver FOREIGN KEY (driver_id) REFERENCES delivery_drivers(id),
    INDEX idx_oda_order (order_id),
    INDEX idx_oda_driver (driver_id),
    INDEX idx_oda_status (status)
) ENGINE=InnoDB COMMENT='Asignación de repartidores a pedidos';

-- ============================================================
-- BLOQUE 10: FACTURACIÓN ELECTRÓNICA (SUNAT)
-- ============================================================

CREATE TABLE billing_series (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    document_type ENUM('boleta','factura','nota_credito','nota_debito','guia_remision') NOT NULL,
    series_prefix VARCHAR(10) NOT NULL COMMENT 'Ej: B001, F001, BC01, FC01',
    current_number INT UNSIGNED NOT NULL DEFAULT 0,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bs2_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_bs2_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    UNIQUE KEY uk_bs_tenant_series (tenant_id, branch_id, document_type, series_prefix),
    INDEX idx_bs2_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Series de facturación por sede (RF-FACT-001)';

CREATE TABLE billing_documents (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    series_id BIGINT UNSIGNED NOT NULL,
    sale_id BIGINT UNSIGNED NULL,
    order_id BIGINT UNSIGNED NULL,

    document_type ENUM('boleta','factura','nota_credito','nota_debito','guia_remision') NOT NULL,
    series VARCHAR(10) NOT NULL,
    correlative_number INT UNSIGNED NOT NULL,
    full_number VARCHAR(30) NOT NULL COMMENT 'Ej: B001-00000123',

    -- Datos del emisor (snapshot)
    issuer_tax_id VARCHAR(20) NOT NULL,
    issuer_business_name VARCHAR(200) NOT NULL,
    issuer_address VARCHAR(300) NULL,

    -- Datos del receptor
    receiver_document_type VARCHAR(5) NULL,
    receiver_document_number VARCHAR(20) NULL,
    receiver_name VARCHAR(200) NOT NULL,
    receiver_address VARCHAR(300) NULL,
    receiver_email VARCHAR(150) NULL,

    -- Montos
    subtotal DECIMAL(12,2) NOT NULL,
    total_discount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_taxable DECIMAL(12,2) NOT NULL COMMENT 'Base imponible',
    total_igv DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_isc DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Impuesto selectivo al consumo',
    total_other_taxes DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'PEN',
    exchange_rate DECIMAL(10,6) NULL,

    -- SUNAT
    sunat_status ENUM('pending','sent','accepted','rejected','voided','error') NOT NULL DEFAULT 'pending',
    sunat_ticket VARCHAR(100) NULL,
    sunat_response_code VARCHAR(10) NULL,
    sunat_response_message TEXT NULL,
    sunat_hash VARCHAR(255) NULL,
    sunat_xml_url VARCHAR(500) NULL,
    sunat_cdr_url VARCHAR(500) NULL,
    sunat_pdf_url VARCHAR(500) NULL,
    sent_to_sunat_at TIMESTAMP NULL,
    sunat_accepted_at TIMESTAMP NULL,

    -- Documento referenciado (para notas de crédito/débito)
    referenced_document_id BIGINT UNSIGNED NULL,
    reference_reason VARCHAR(300) NULL,

    issue_date DATE NOT NULL,
    due_date DATE NULL,
    status ENUM('draft','issued','sent','accepted','voided','error') NOT NULL DEFAULT 'draft',

    created_by BIGINT UNSIGNED NULL,
    voided_by BIGINT UNSIGNED NULL,
    voided_at TIMESTAMP NULL,
    void_reason VARCHAR(300) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_bd_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_bd_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_bd_series FOREIGN KEY (series_id) REFERENCES billing_series(id),
    CONSTRAINT fk_bd_sale FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE SET NULL,
    CONSTRAINT fk_bd_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    CONSTRAINT fk_bd_ref FOREIGN KEY (referenced_document_id) REFERENCES billing_documents(id),
    CONSTRAINT fk_bd_created_by FOREIGN KEY (created_by) REFERENCES users(id),

    UNIQUE KEY uk_bd_full_number (tenant_id, full_number),
    INDEX idx_bd_tenant (tenant_id),
    INDEX idx_bd_branch (branch_id),
    INDEX idx_bd_sale (sale_id),
    INDEX idx_bd_order (order_id),
    INDEX idx_bd_type (tenant_id, document_type),
    INDEX idx_bd_sunat_status (sunat_status),
    INDEX idx_bd_issue_date (tenant_id, issue_date),
    INDEX idx_bd_status (tenant_id, status)
) ENGINE=InnoDB COMMENT='Documentos de facturación electrónica SUNAT (RF-FACT-001..005)';

CREATE TABLE billing_document_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NULL,
    item_number INT UNSIGNED NOT NULL,
    description VARCHAR(500) NOT NULL,
    unit_code VARCHAR(10) NOT NULL DEFAULT 'NIU' COMMENT 'Código SUNAT de unidad',
    quantity DECIMAL(12,4) NOT NULL,
    unit_price DECIMAL(10,4) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    taxable_amount DECIMAL(10,2) NOT NULL,
    igv_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    isc_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_bdi_document FOREIGN KEY (document_id) REFERENCES billing_documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_bdi_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
    INDEX idx_bdi_document (document_id)
) ENGINE=InnoDB COMMENT='Items de documento de facturación';

-- ============================================================
-- BLOQUE 11: DEVOLUCIONES Y REEMBOLSOS
-- ============================================================

CREATE TABLE returns (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    return_number VARCHAR(30) NOT NULL,
    sale_id BIGINT UNSIGNED NULL,
    order_id BIGINT UNSIGNED NULL,
    customer_id BIGINT UNSIGNED NULL,
    return_type ENUM('full','partial') NOT NULL DEFAULT 'partial',
    reason_category ENUM('defective','wrong_item','customer_change','expired','damaged','other') NOT NULL,
    reason_detail TEXT NULL,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    refund_method ENUM('cash','original_payment','store_credit','bank_transfer') NOT NULL DEFAULT 'original_payment',
    status ENUM('requested','approved','processing','completed','rejected') NOT NULL DEFAULT 'requested',
    requested_by BIGINT UNSIGNED NULL,
    approved_by BIGINT UNSIGNED NULL,
    processed_by BIGINT UNSIGNED NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    rejected_at TIMESTAMP NULL,
    rejection_reason VARCHAR(500) NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ret_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_ret_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_ret_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
    CONSTRAINT fk_ret_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_ret_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    UNIQUE KEY uk_ret_tenant_number (tenant_id, return_number),
    INDEX idx_ret_tenant (tenant_id),
    INDEX idx_ret_sale (sale_id),
    INDEX idx_ret_order (order_id),
    INDEX idx_ret_status (tenant_id, status)
) ENGINE=InnoDB COMMENT='Devoluciones y reembolsos (RF-DEV-001..003)';

CREATE TABLE return_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    return_id BIGINT UNSIGNED NOT NULL,
    product_id BIGINT UNSIGNED NOT NULL,
    sale_item_id BIGINT UNSIGNED NULL,
    order_item_id BIGINT UNSIGNED NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    condition_status ENUM('good','damaged','expired','opened') NOT NULL DEFAULT 'good',
    return_to_stock TINYINT(1) NOT NULL DEFAULT 0,
    warehouse_id BIGINT UNSIGNED NULL COMMENT 'Almacén de retorno',
    notes VARCHAR(300) NULL,
    CONSTRAINT fk_ri_return FOREIGN KEY (return_id) REFERENCES returns(id) ON DELETE CASCADE,
    CONSTRAINT fk_ri_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_ri_sale_item FOREIGN KEY (sale_item_id) REFERENCES sale_items(id),
    CONSTRAINT fk_ri_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id),
    CONSTRAINT fk_ri_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    INDEX idx_ri_return (return_id)
) ENGINE=InnoDB COMMENT='Items devueltos';

-- ============================================================
-- BLOQUE 12: GASTOS E INGRESOS
-- ============================================================

CREATE TABLE expense_categories (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(20) NOT NULL,
    parent_id BIGINT UNSIGNED NULL,
    description VARCHAR(300) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ec_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_ec_parent FOREIGN KEY (parent_id) REFERENCES expense_categories(id),
    UNIQUE KEY uk_ec_tenant_code (tenant_id, code),
    INDEX idx_ec_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Categorías de gastos (RF-GAS-001)';

CREATE TABLE expenses (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NULL,
    expense_number VARCHAR(30) NOT NULL,
    category_id BIGINT UNSIGNED NOT NULL,
    supplier_id BIGINT UNSIGNED NULL,
    description VARCHAR(500) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'PEN',
    expense_date DATE NOT NULL,
    payment_method ENUM('cash','bank_transfer','credit_card','check','other') NOT NULL DEFAULT 'cash',
    payment_reference VARCHAR(100) NULL,
    receipt_url VARCHAR(500) NULL,
    status ENUM('pending','approved','paid','rejected','voided') NOT NULL DEFAULT 'pending',
    is_recurring TINYINT(1) NOT NULL DEFAULT 0,
    recurrence_period ENUM('weekly','biweekly','monthly','quarterly','annual') NULL,
    approved_by BIGINT UNSIGNED NULL,
    registered_by BIGINT UNSIGNED NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_exp_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_exp_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_exp_category FOREIGN KEY (category_id) REFERENCES expense_categories(id),
    CONSTRAINT fk_exp_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_exp_approved FOREIGN KEY (approved_by) REFERENCES users(id),
    CONSTRAINT fk_exp_registered FOREIGN KEY (registered_by) REFERENCES users(id),
    UNIQUE KEY uk_exp_tenant_number (tenant_id, expense_number),
    INDEX idx_exp_tenant (tenant_id),
    INDEX idx_exp_branch (branch_id),
    INDEX idx_exp_category (category_id),
    INDEX idx_exp_date (tenant_id, expense_date),
    INDEX idx_exp_status (tenant_id, status)
) ENGINE=InnoDB COMMENT='Gastos del negocio (RF-GAS-001..003)';

-- ============================================================
-- BLOQUE 13: DESCUENTOS Y PROMOCIONES
-- ============================================================

CREATE TABLE promotions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NULL COMMENT 'Código promocional',
    description TEXT NULL,
    discount_type ENUM('percentage','fixed_amount','buy_x_get_y','free_shipping') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_purchase_amount DECIMAL(10,2) NULL,
    max_discount_amount DECIMAL(10,2) NULL,
    max_uses INT UNSIGNED NULL,
    max_uses_per_customer INT UNSIGNED NULL,
    current_uses INT UNSIGNED NOT NULL DEFAULT 0,
    applies_to ENUM('all','category','product','brand','combo') NOT NULL DEFAULT 'all',
    valid_from DATETIME NOT NULL,
    valid_until DATETIME NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    is_combinable TINYINT(1) NOT NULL DEFAULT 0,
    channels JSON NULL COMMENT '["pos","storefront"]',
    created_by BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_promo_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_promo_created FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_promo_tenant (tenant_id),
    INDEX idx_promo_code (tenant_id, code),
    INDEX idx_promo_dates (valid_from, valid_until),
    INDEX idx_promo_active (tenant_id, is_active)
) ENGINE=InnoDB COMMENT='Promociones y descuentos (RF-PRO-015)';

CREATE TABLE promotion_conditions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    promotion_id BIGINT UNSIGNED NOT NULL,
    entity_type ENUM('product','category','brand') NOT NULL,
    entity_id BIGINT UNSIGNED NOT NULL,
    CONSTRAINT fk_pc_promo FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    INDEX idx_pc_promo (promotion_id),
    INDEX idx_pc_entity (entity_type, entity_id)
) ENGINE=InnoDB COMMENT='Condiciones de aplicación de promociones';

CREATE TABLE promotion_branches (
    promotion_id BIGINT UNSIGNED NOT NULL,
    branch_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (promotion_id, branch_id),
    CONSTRAINT fk_prb_promo FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    CONSTRAINT fk_prb_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
) ENGINE=InnoDB COMMENT='Sedes donde aplica la promoción';

-- ============================================================
-- BLOQUE 14: STOREFRONT (TIENDA ONLINE)
-- ============================================================

CREATE TABLE storefront_config (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    is_enabled TINYINT(1) NOT NULL DEFAULT 0,
    store_name VARCHAR(200) NULL,
    store_slug VARCHAR(100) NULL UNIQUE,
    custom_domain VARCHAR(200) NULL,
    welcome_message TEXT NULL,
    banner_images JSON NULL COMMENT 'Array de URLs de banners',
    featured_categories JSON NULL COMMENT 'Array de IDs de categorías destacadas',
    seo_title VARCHAR(200) NULL,
    seo_description VARCHAR(500) NULL,
    seo_keywords VARCHAR(300) NULL,
    google_analytics_id VARCHAR(50) NULL,
    facebook_pixel_id VARCHAR(50) NULL,
    social_links JSON NULL,
    min_order_amount DECIMAL(10,2) NULL,
    max_order_amount DECIMAL(12,2) NULL,
    terms_and_conditions TEXT NULL,
    privacy_policy TEXT NULL,
    return_policy TEXT NULL,
    show_prices_with_tax TINYINT(1) NOT NULL DEFAULT 1,
    allow_guest_checkout TINYINT(1) NOT NULL DEFAULT 0,
    require_age_verification TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sf_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_sf_tenant (tenant_id),
    INDEX idx_sf_slug (store_slug)
) ENGINE=InnoDB COMMENT='Configuración del storefront (RF-STF-001..003)';

CREATE TABLE storefront_pages (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200) NOT NULL,
    content LONGTEXT NULL,
    is_published TINYINT(1) NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    meta_title VARCHAR(200) NULL,
    meta_description VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sfp_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_sfp_tenant_slug (tenant_id, slug),
    INDEX idx_sfp_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Páginas personalizadas del storefront';

-- ============================================================
-- BLOQUE 15: FIDELIZACIÓN Y PUNTOS
-- ============================================================

CREATE TABLE loyalty_config (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    is_enabled TINYINT(1) NOT NULL DEFAULT 0,
    points_per_currency_unit DECIMAL(10,2) NOT NULL DEFAULT 1.00 COMMENT 'Puntos por cada sol gastado',
    currency_per_point DECIMAL(10,4) NOT NULL DEFAULT 0.01 COMMENT 'Valor monetario de cada punto',
    min_points_redemption INT UNSIGNED NOT NULL DEFAULT 100,
    points_expiry_days INT UNSIGNED NULL COMMENT 'Días hasta expiración de puntos',
    tier_rules_json JSON NULL COMMENT 'Reglas por nivel',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lc_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    UNIQUE KEY uk_lc_tenant (tenant_id)
) ENGINE=InnoDB COMMENT='Configuración del programa de fidelidad';

CREATE TABLE loyalty_transactions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    customer_id BIGINT UNSIGNED NOT NULL,
    transaction_type ENUM('earn','redeem','expire','adjust','bonus') NOT NULL,
    points INT NOT NULL,
    balance_after INT NOT NULL,
    reference_type VARCHAR(50) NULL COMMENT 'sale, order, manual, etc.',
    reference_id BIGINT UNSIGNED NULL,
    description VARCHAR(300) NULL,
    expires_at DATE NULL,
    created_by BIGINT UNSIGNED NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lt_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_lt_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_lt_tenant (tenant_id),
    INDEX idx_lt_customer (customer_id),
    INDEX idx_lt_type (transaction_type),
    INDEX idx_lt_expires (expires_at)
) ENGINE=InnoDB COMMENT='Transacciones de puntos de fidelidad (RF-CLI-004)';

-- ============================================================
-- BLOQUE 16: REPORTES DE NEGOCIO
-- ============================================================

CREATE TABLE report_templates (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NULL COMMENT 'NULL = plantilla global',
    code VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    module VARCHAR(100) NOT NULL,
    description TEXT NULL,
    query_template TEXT NULL,
    parameters_schema JSON NULL,
    default_format ENUM('pdf','excel','csv') NOT NULL DEFAULT 'pdf',
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rt_tenant (tenant_id),
    INDEX idx_rt_module (module)
) ENGINE=InnoDB COMMENT='Plantillas de reportes (RF-REP-001..005)';

CREATE TABLE generated_reports (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT UNSIGNED NOT NULL,
    template_id BIGINT UNSIGNED NULL,
    report_name VARCHAR(200) NOT NULL,
    module VARCHAR(100) NOT NULL,
    parameters_json JSON NULL,
    filters_json JSON NULL,
    format ENUM('pdf','excel','csv') NOT NULL DEFAULT 'pdf',
    file_url VARCHAR(500) NULL,
    file_size_bytes BIGINT UNSIGNED NULL,
    status ENUM('pending','processing','completed','failed') NOT NULL DEFAULT 'pending',
    generated_by BIGINT UNSIGNED NOT NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    error_message TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gr_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_gr_template FOREIGN KEY (template_id) REFERENCES report_templates(id),
    CONSTRAINT fk_gr_user FOREIGN KEY (generated_by) REFERENCES users(id),
    INDEX idx_gr_tenant (tenant_id),
    INDEX idx_gr_module (tenant_id, module),
    INDEX idx_gr_status (status),
    INDEX idx_gr_date (tenant_id, created_at)
) ENGINE=InnoDB COMMENT='Reportes generados (RF-REP-001..005)';

-- ============================================================
-- BLOQUE 17: VISTAS DE CONSULTA Y RESUMEN
-- ============================================================

CREATE OR REPLACE VIEW vw_tenant_summary AS
SELECT
    t.id AS tenant_id,
    t.business_name,
    t.trade_name,
    t.status AS tenant_status,
    s.id AS subscription_id,
    sp.name AS plan_name,
    s.status AS subscription_status,
    s.current_period_end,
    (SELECT COUNT(*) FROM branches b WHERE b.tenant_id = t.id AND b.is_active = 1) AS active_branches,
    (SELECT COUNT(*) FROM users u WHERE u.tenant_id = t.id AND u.is_active = 1) AS active_users,
    (SELECT COUNT(*) FROM products p WHERE p.tenant_id = t.id AND p.is_active = 1) AS active_products
FROM tenants t
LEFT JOIN subscriptions s ON s.tenant_id = t.id AND s.status IN ('active','trial')
LEFT JOIN subscription_plans sp ON sp.id = s.plan_id;

CREATE OR REPLACE VIEW vw_product_stock_summary AS
SELECT
    p.tenant_id,
    p.id AS product_id,
    p.sku,
    p.name AS product_name,
    p.sale_price,
    w.branch_id,
    ist.warehouse_id,
    w.name AS warehouse_name,
    ist.quantity_on_hand,
    ist.quantity_reserved,
    ist.quantity_available,
    p.min_stock,
    p.reorder_point,
    CASE
        WHEN ist.quantity_on_hand <= 0 THEN 'out_of_stock'
        WHEN ist.quantity_on_hand <= COALESCE(p.min_stock, 0) THEN 'low_stock'
        WHEN p.max_stock IS NOT NULL AND ist.quantity_on_hand >= p.max_stock THEN 'overstock'
        ELSE 'normal'
    END AS stock_status
FROM products p
INNER JOIN inventory_stock ist ON ist.product_id = p.id
INNER JOIN warehouses w ON w.id = ist.warehouse_id
WHERE p.is_active = 1;

CREATE OR REPLACE VIEW vw_daily_sales_summary AS
SELECT
    s.tenant_id,
    s.branch_id,
    DATE(s.created_at) AS sale_date,
    s.sale_type,
    COUNT(s.id) AS total_sales,
    SUM(s.subtotal) AS total_subtotal,
    SUM(s.discount_amount) AS total_discounts,
    SUM(s.tax_amount) AS total_tax,
    SUM(s.total) AS total_revenue,
    AVG(s.total) AS avg_ticket
FROM sales s
WHERE s.status = 'completed'
GROUP BY s.tenant_id, s.branch_id, DATE(s.created_at), s.sale_type;

CREATE OR REPLACE VIEW vw_expiring_batches AS
SELECT
    ib.tenant_id,
    ib.product_id,
    p.name AS product_name,
    ib.warehouse_id,
    w.name AS warehouse_name,
    ib.batch_number,
    ib.quantity_remaining,
    ib.expiry_date,
    DATEDIFF(ib.expiry_date, CURDATE()) AS days_until_expiry
FROM inventory_batches ib
INNER JOIN products p ON p.id = ib.product_id
INNER JOIN warehouses w ON w.id = ib.warehouse_id
WHERE ib.status = 'available'
  AND ib.quantity_remaining > 0
  AND ib.expiry_date IS NOT NULL
  AND ib.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)
ORDER BY ib.expiry_date ASC;

-- ============================================================
-- FIN DEL SCRIPT - DRINKGO DATABASE
-- ============================================================
