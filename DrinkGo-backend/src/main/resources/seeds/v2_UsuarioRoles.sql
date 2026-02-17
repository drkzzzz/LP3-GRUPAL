-- ============================================================
-- SEED v2: Usuarios, Roles, Permisos y Seguridad
-- DrinkGo Platform – Bloque 2
-- PREREQUISITOS: ejecutar v3 primero (negocios id=1,2 / usuarios 1-3
--   / roles 1-3 / permiso 1-10 / usuarios_plataforma id=1)
-- Contraseña para todos los usuarios: admin123
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 1. ROLES ADICIONALES (negocio 2 + rol REPARTIDOR negocio 1)
-- ============================================================
INSERT INTO rol (id, tenant_id, codigo, nombre, descripcion, es_sistema, activo, creado_en, actualizado_en) VALUES
(4, 2, 'ADMIN',      'Administrador',    'Acceso total al negocio',               1, 1, NOW(), NOW()),
(5, 2, 'CAJERO',     'Cajero',           'Gestión de ventas y caja registradora', 1, 1, NOW(), NOW()),
(6, 2, 'ALMACEN',    'Almacenero',       'Gestión de inventario y almacenes',     1, 1, NOW(), NOW()),
(7, 1, 'REPARTIDOR', 'Repartidor',       'Entrega de pedidos delivery',           1, 1, NOW(), NOW()),
(8, 2, 'REPARTIDOR', 'Repartidor',       'Entrega de pedidos delivery',           1, 1, NOW(), NOW());

-- Asignar permisos a roles nuevos
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES
-- Admin negocio 2: todos los permisos
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 8), (4, 9), (4, 10),
-- Cajero negocio 2: ventas, cajas, clientes
(5, 4), (5, 5), (5, 6),
-- Almacenero negocio 2: inventario, reportes
(6, 3), (6, 8),
-- Repartidor negocio 1: delivery
(7, 10),
-- Repartidor negocio 2: delivery
(8, 10);

-- ============================================================
-- 2. USUARIOS ADICIONALES
-- ============================================================

-- Usuarios negocio 2
INSERT INTO usuarios (
    id, uuid, negocio_id, email, hash_contrasena,
    nombres, apellidos, telefono, esta_activo,
    creado_en, actualizado_en
) VALUES
(4, UUID(), 2, 'admin@licoreriaexpress.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Rosa', 'Huamán Quispe', '956789012', 1, NOW(), NOW()),
(5, UUID(), 2, 'cajero@licoreriaexpress.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Luis', 'Chávez Morales', '967890123', 1, NOW(), NOW()),
(6, UUID(), 2, 'almacen@licoreriaexpress.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Miguel', 'Torres Sánchez', '978901234', 1, NOW(), NOW()),
-- Repartidores negocio 1
(7, UUID(), 1, 'repartidor1@drinkgo.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Diego', 'Vargas Rojas', '934567890', 1, NOW(), NOW()),
(8, UUID(), 1, 'repartidor2@drinkgo.pe',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Andrés', 'Mendoza Lima', '945678901', 1, NOW(), NOW());

-- Asignar roles a nuevos usuarios (ManyToMany join table)
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(4, 4),  -- Rosa   → Admin negocio 2
(5, 5),  -- Luis   → Cajero negocio 2
(6, 6),  -- Miguel → Almacenero negocio 2
(7, 7),  -- Diego  → Repartidor negocio 1
(8, 7);  -- Andrés → Repartidor negocio 1

-- ============================================================
-- 3. MÓDULOS DEL SISTEMA
-- ============================================================
INSERT INTO modulos_sistema (id, codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo) VALUES
(1,  'DASHBOARD',      'Dashboard',            'Panel principal de métricas',               NULL, 'dashboard',      1,  1),
(2,  'USUARIOS',       'Usuarios',             'Gestión de usuarios y accesos',             NULL, 'people',         2,  1),
(3,  'INVENTARIO',     'Inventario',           'Control de stock y almacenes',              NULL, 'inventory_2',    3,  1),
(4,  'VENTAS',         'Ventas',               'Registro de ventas POS',                    NULL, 'point_of_sale',  4,  1),
(5,  'CAJAS',          'Cajas',                'Gestión de cajas registradoras',             NULL, 'payments',       5,  1),
(6,  'PEDIDOS',        'Pedidos',              'Pedidos online y delivery',                 NULL, 'shopping_cart',  6,  1),
(7,  'CONFIGURACION',  'Configuración',        'Configuración del negocio',                 NULL, 'settings',       7,  1),
(8,  'REPORTES',       'Reportes',             'Reportes y estadísticas',                   NULL, 'analytics',      8,  1),
(9,  'CLIENTES',       'Clientes',             'Gestión de clientes',                       NULL, 'group',          9,  1),
(10, 'CATALOGO',       'Catálogo',             'Gestión de productos y categorías',         NULL, 'storefront',    10,  1),
-- Submódulos de Inventario
(11, 'INV_STOCK',      'Stock',                'Control de stock por almacén',               3,  'inventory',     1,  1),
(12, 'INV_MOVIMIENTOS','Movimientos',          'Historial de movimientos de inventario',     3,  'swap_horiz',    2,  1),
(13, 'INV_ALERTAS',    'Alertas',              'Alertas de inventario',                      3,  'notifications', 3,  1),
-- Submódulos de Pedidos
(14, 'PED_DELIVERY',   'Delivery',             'Gestión de pedidos delivery',                6,  'delivery_dining',1, 1),
(15, 'PED_RECOJO',     'Recojo',               'Gestión de pedidos para recojo',             6,  'store',          2, 1);

-- ============================================================
-- 4. PERMISOS DEL SISTEMA (granulares por módulo)
-- ============================================================
INSERT INTO permisos_sistema (id, modulo_id, codigo, nombre, descripcion, tipo_accion) VALUES
-- Dashboard
(1,  1,  'DASHBOARD_VER',             'Ver Dashboard',                    'Acceso al panel principal',                'ver'),
-- Usuarios
(2,  2,  'USUARIOS_VER',              'Ver Usuarios',                     'Listar usuarios del negocio',              'ver'),
(3,  2,  'USUARIOS_CREAR',            'Crear Usuarios',                   'Registrar nuevos usuarios',                'crear'),
(4,  2,  'USUARIOS_EDITAR',           'Editar Usuarios',                  'Modificar datos de usuarios',              'editar'),
(5,  2,  'USUARIOS_ELIMINAR',         'Eliminar Usuarios',                'Desactivar usuarios',                      'eliminar'),
-- Inventario
(6,  3,  'INVENTARIO_VER',            'Ver Inventario',                   'Consultar stock y productos',              'ver'),
(7,  3,  'INVENTARIO_AJUSTAR',        'Ajustar Inventario',               'Realizar ajustes de stock',                'editar'),
(8,  3,  'INVENTARIO_TRANSFERIR',     'Transferir Stock',                 'Transferir entre almacenes',               'editar'),
-- Ventas
(9,  4,  'VENTAS_VER',                'Ver Ventas',                       'Consultar historial de ventas',            'ver'),
(10, 4,  'VENTAS_CREAR',              'Registrar Ventas',                 'Registrar nuevas ventas en POS',           'crear'),
(11, 4,  'VENTAS_ANULAR',             'Anular Ventas',                    'Anular ventas registradas',                'eliminar'),
-- Cajas
(12, 5,  'CAJAS_VER',                 'Ver Cajas',                        'Consultar estado de cajas',                'ver'),
(13, 5,  'CAJAS_ABRIR_CERRAR',        'Abrir/Cerrar Cajas',              'Gestionar apertura y cierre de caja',      'configurar'),
-- Pedidos
(14, 6,  'PEDIDOS_VER',               'Ver Pedidos',                      'Consultar pedidos',                        'ver'),
(15, 6,  'PEDIDOS_GESTIONAR',         'Gestionar Pedidos',                'Cambiar estado de pedidos',                'editar'),
-- Configuración
(16, 7,  'CONFIG_VER',                'Ver Configuración',                'Consultar configuración del negocio',      'ver'),
(17, 7,  'CONFIG_EDITAR',             'Editar Configuración',             'Modificar configuración del negocio',      'configurar'),
-- Reportes
(18, 8,  'REPORTES_VER',              'Ver Reportes',                     'Acceso a reportes básicos',                'ver'),
(19, 8,  'REPORTES_EXPORTAR',         'Exportar Reportes',                'Exportar reportes en Excel/PDF',           'exportar'),
-- Clientes
(20, 9,  'CLIENTES_VER',              'Ver Clientes',                     'Consultar base de clientes',               'ver'),
(21, 9,  'CLIENTES_GESTIONAR',        'Gestionar Clientes',               'Crear y editar clientes',                  'editar'),
-- Catálogo
(22, 10, 'CATALOGO_VER',              'Ver Catálogo',                     'Consultar productos y categorías',         'ver'),
(23, 10, 'CATALOGO_GESTIONAR',        'Gestionar Catálogo',               'Crear y editar productos y categorías',    'editar');

-- ============================================================
-- 5. ROLES – PERMISOS SISTEMA (standalone entity roles_permisos)
-- ============================================================
INSERT INTO roles_permisos (rol_id, permiso_id, creado_en) VALUES
-- Admin negocio 1 (rol 1): todos los permisos sistema
(1, 1, NOW()), (1, 2, NOW()), (1, 3, NOW()), (1, 4, NOW()), (1, 5, NOW()),
(1, 6, NOW()), (1, 7, NOW()), (1, 8, NOW()), (1, 9, NOW()), (1, 10, NOW()),
(1, 11, NOW()), (1, 12, NOW()), (1, 13, NOW()), (1, 14, NOW()), (1, 15, NOW()),
(1, 16, NOW()), (1, 17, NOW()), (1, 18, NOW()), (1, 19, NOW()),
(1, 20, NOW()), (1, 21, NOW()), (1, 22, NOW()), (1, 23, NOW()),
-- Cajero (rol 2): ventas, cajas, clientes
(2, 9, NOW()), (2, 10, NOW()), (2, 12, NOW()), (2, 13, NOW()), (2, 20, NOW()),
-- Almacenero (rol 3): inventario, reportes
(3, 6, NOW()), (3, 7, NOW()), (3, 8, NOW()), (3, 18, NOW()),
-- Admin negocio 2 (rol 4): todos
(4, 1, NOW()), (4, 2, NOW()), (4, 3, NOW()), (4, 4, NOW()), (4, 5, NOW()),
(4, 6, NOW()), (4, 7, NOW()), (4, 8, NOW()), (4, 9, NOW()), (4, 10, NOW()),
(4, 11, NOW()), (4, 12, NOW()), (4, 13, NOW()), (4, 14, NOW()), (4, 15, NOW()),
(4, 16, NOW()), (4, 17, NOW()), (4, 18, NOW()), (4, 19, NOW()),
(4, 20, NOW()), (4, 21, NOW()), (4, 22, NOW()), (4, 23, NOW()),
-- Repartidor negocio 1 (rol 7): pedidos delivery
(7, 14, NOW()), (7, 15, NOW());

-- ============================================================
-- 6. USUARIOS-ROLES (standalone entity usuarios_roles)
-- ============================================================
INSERT INTO usuarios_roles (usuario_id, rol_id, asignado_en, asignado_por) VALUES
(1, 1, NOW(), NULL),   -- Carlos  → Admin (auto-asignado)
(2, 2, NOW(), 1),      -- María   → Cajero (asignado por Carlos)
(3, 3, NOW(), 1),      -- Juan    → Almacenero (asignado por Carlos)
(4, 4, NOW(), NULL),   -- Rosa    → Admin neg 2
(5, 5, NOW(), 4),      -- Luis    → Cajero neg 2 (asignado por Rosa)
(6, 6, NOW(), 4),      -- Miguel  → Almacenero neg 2
(7, 7, NOW(), 1),      -- Diego   → Repartidor neg 1
(8, 7, NOW(), 1);      -- Andrés  → Repartidor neg 1

-- Asignaciones de sedes para nuevos usuarios
INSERT INTO usuarios_sedes (usuario_id, sede_id, es_predeterminado, asignado_en, asignado_por) VALUES
(4, 1, 1, NOW(), NULL),   -- Rosa en sede 1 (la única, predeterminada)
(5, 1, 1, NOW(), 4),      -- Luis en sede 1
(6, 1, 1, NOW(), 4),      -- Miguel en sede 1
(7, 1, 1, NOW(), 1),      -- Diego → Sede San Isidro
(7, 2, 0, NOW(), 1),      -- Diego → también Los Olivos
(8, 2, 1, NOW(), 1);      -- Andrés → Sede Los Olivos

-- ============================================================
-- 7. SESIONES DE USUARIO (muestras de sesiones activas)
-- ============================================================
INSERT INTO sesiones_usuario (
    id, usuario_id, hash_token, direccion_ip, agente_usuario,
    info_dispositivo, expira_en, ultima_actividad_en,
    esta_activo, creado_en
) VALUES
(1, 1,
 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6',
 '192.168.1.100',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0',
 'Desktop - Windows',
 DATE_ADD(NOW(), INTERVAL 24 HOUR), NOW(), 1, NOW()),
(2, 2,
 'b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1',
 '192.168.1.101',
 'Mozilla/5.0 (iPad; CPU OS 17_0) Safari/605.1',
 'Tablet - iPad',
 DATE_ADD(NOW(), INTERVAL 8 HOUR), NOW(), 1, NOW()),
(3, 4,
 'c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2',
 '10.0.0.50',
 'Mozilla/5.0 (Linux; Android 14) Chrome/121.0 Mobile',
 'Mobile - Android',
 DATE_ADD(NOW(), INTERVAL 12 HOUR), NOW(), 1, NOW()),
-- Sesión expirada (histórica)
(4, 3,
 'd4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a1b2c3',
 '192.168.1.102',
 'Mozilla/5.0 (Windows NT 10.0) Firefox/122.0',
 'Desktop - Windows',
 DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR), 0,
 DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ============================================================
-- 8. TOKENS DE RECUPERACIÓN DE CONTRASEÑA
-- ============================================================
INSERT INTO usuario_recuperacion (
    id, usuario_id, token, expira_en, usado, creado_en
) VALUES
-- Token activo (no usado, no expirado)
(1, 2,
 'tkn_rec_activo_abc123def456ghi789jkl012mno345pqr678stu901vwx234',
 DATE_ADD(NOW(), INTERVAL 1 HOUR), 0, NOW()),
-- Token ya usado
(2, 3,
 'tkn_rec_usado_xyz987wvu654tsr321qpo098nml765kji432hgf109edc876',
 DATE_ADD(NOW(), INTERVAL 1 HOUR), 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
-- Token expirado
(3, 5,
 'tkn_rec_expirado_aaa111bbb222ccc333ddd444eee555fff666ggg777hhh',
 DATE_SUB(NOW(), INTERVAL 6 HOUR), 0, DATE_SUB(NOW(), INTERVAL 12 HOUR));

-- ============================================================
-- 9. REGISTROS DE AUDITORÍA
-- ============================================================
INSERT INTO registros_auditoria (
    id, negocio_id, usuario_id, accion, tipo_entidad,
    entidad_id, valores_anteriores, valores_nuevos,
    ip_address, user_agent, fecha_accion
) VALUES
(1, 1, 1, 'CREAR', 'Sede', 1, NULL,
 '{"nombre":"Sede Principal - San Isidro","codigo":"SEDE-001"}',
 '192.168.1.100',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0',
 NOW()),

(2, 1, 1, 'CREAR', 'Sede', 2, NULL,
 '{"nombre":"Sede Norte - Los Olivos","codigo":"SEDE-002"}',
 '192.168.1.100',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0',
 NOW()),

(3, 1, 1, 'CREAR', 'Usuario', 2, NULL,
 '{"email":"cajero@drinkgo.pe","nombres":"María","apellidos":"Rodríguez Pérez"}',
 '192.168.1.100',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0',
 NOW()),

(4, 1, 1, 'EDITAR', 'Producto', 1,
 '{"precio_compra":80.00}',
 '{"precio_compra":85.00}',
 '192.168.1.100',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0',
 NOW()),

(5, 1, 2, 'CREAR', 'Venta', 1, NULL,
 '{"total":185.00,"tipo":"venta_pos"}',
 '192.168.1.101',
 'Mozilla/5.0 (iPad; CPU OS 17_0) Safari/605.1',
 NOW()),

(6, 2, 4, 'CREAR', 'Usuario', 5, NULL,
 '{"email":"cajero@licoreriaexpress.pe","nombres":"Luis"}',
 '10.0.0.50',
 'Mozilla/5.0 (Linux; Android 14) Chrome/121.0 Mobile',
 NOW()),

(7, NULL, NULL, 'CREAR', 'Negocio', 2, NULL,
 '{"razon_social":"Licorería Express EIRL","ruc":"10456789012"}',
 '10.10.10.1',
 'Mozilla/5.0 (Windows NT 10.0) Edge/121.0',
 NOW()),

(8, 1, 1, 'CREAR', 'Rol', 7, NULL,
 '{"codigo":"REPARTIDOR","nombre":"Repartidor"}',
 '192.168.1.100',
 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0',
 NOW());

-- ============================================================
-- FIN v2
-- ============================================================