-- ============================================================
-- SEED 08: MÓDULOS DEL SISTEMA Y PERMISOS
-- Basado en la estructura real de navegación del panel admin
-- Idempotente: usa WHERE NOT EXISTS / INSERT IGNORE
-- ============================================================

USE drinkgo_db;

-- Fijar esta_activo en registros existentes con NULL (fix para @SQLRestriction)
UPDATE modulos_sistema SET esta_activo = 1 WHERE esta_activo IS NULL;

-- ============================================================
-- 1. MÓDULOS PADRE (niveles principales del menú)
-- ============================================================

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, icono, orden, esta_activo) VALUES
('dashboard',      'Dashboard',              'Panel principal con métricas del negocio',       'LayoutDashboard', 1,  1),
('configuracion',  'Configuración',          'Configuración del negocio y operaciones',         'Settings',        2,  1),
('usuarios',       'Usuarios y Clientes',    'Gestión de usuarios, clientes y roles',           'Users',           3,  1),
('catalogo',       'Catálogo',               'Gestión de productos, clasificaciones y combos',  'Package',         4,  1),
('inventario',     'Inventario',             'Control de almacenes, lotes y transferencias',    'Warehouse',       5,  1),
('compras',        'Proveedores y Compras',  'Proveedores, órdenes y recepción de mercadería',  'Truck',           6,  1),
('ventas',         'Ventas / POS',           'Punto de venta, caja e historial de ventas',      'CreditCard',      7,  1),
('pedidos',        'Pedidos',                'Gestión de pedidos de clientes',                  'ShoppingCart',    8,  1),
('devoluciones',   'Devoluciones',           'Gestión de devoluciones de clientes y proveedores','RotateCcw',       9,  1),
('facturacion',    'Facturación',            'Comprobantes, series y métodos de pago',          'ClipboardList',   10, 1),
('reportes',       'Reportes',               'Reportes y análisis del negocio',                 'BarChart3',       11, 1);

-- ============================================================
-- 2. SUBMÓDULOS
-- ============================================================

-- Submódulos de Configuración
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.negocio', 'Negocio y Sedes', 'Datos del negocio y gestión de sedes',
    id, 'Building2', 1, 1 FROM modulos_sistema WHERE codigo = 'configuracion';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.operaciones', 'Operaciones', 'Configuración de parámetros operativos',
    id, 'SlidersHorizontal', 2, 1 FROM modulos_sistema WHERE codigo = 'configuracion';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.categorias-gastos', 'Categorías de Gastos', 'Categorías para clasificar gastos operativos',
    id, 'Tags', 3, 1 FROM modulos_sistema WHERE codigo = 'configuracion';

-- Submódulos de Catálogo
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'catalogo.productos', 'Productos', 'Gestión del catálogo de productos',
    id, 'Package', 1, 1 FROM modulos_sistema WHERE codigo = 'catalogo';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'catalogo.clasificaciones', 'Clasificaciones', 'Categorías, tipos y marcas',
    id, 'Layers', 2, 1 FROM modulos_sistema WHERE codigo = 'catalogo';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'catalogo.combos', 'Combos', 'Combos y paquetes de productos',
    id, 'Gift', 3, 1 FROM modulos_sistema WHERE codigo = 'catalogo';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'catalogo.promociones', 'Promociones', 'Descuentos y promociones por tiempo',
    id, 'Tag', 4, 1 FROM modulos_sistema WHERE codigo = 'catalogo';

-- Submódulos de Inventario
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'inventario.almacenes', 'Almacenes', 'Gestión de almacenes y ubicaciones',
    id, 'Warehouse', 1, 1 FROM modulos_sistema WHERE codigo = 'inventario';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'inventario.lotes', 'Lotes', 'Control de lotes y fechas de vencimiento',
    id, 'Boxes', 2, 1 FROM modulos_sistema WHERE codigo = 'inventario';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'inventario.transferencias', 'Transferencias', 'Transferencias entre almacenes',
    id, 'ArrowRightLeft', 3, 1 FROM modulos_sistema WHERE codigo = 'inventario';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'inventario.reportes', 'Reportes de Inventario', 'Reportes de stock y valorización',
    id, 'BarChart3', 4, 1 FROM modulos_sistema WHERE codigo = 'inventario';

-- Submódulos de Compras
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'compras.proveedores', 'Proveedores', 'Registro y gestión de proveedores',
    id, 'Users', 1, 1 FROM modulos_sistema WHERE codigo = 'compras';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'compras.productos', 'Productos Proveedor', 'Catálogo de productos por proveedor',
    id, 'Package', 2, 1 FROM modulos_sistema WHERE codigo = 'compras';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'compras.ordenes', 'Órdenes de Compra', 'Creación y seguimiento de órdenes',
    id, 'ClipboardList', 3, 1 FROM modulos_sistema WHERE codigo = 'compras';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'compras.recepcion', 'Recepción', 'Recepción e ingreso al almacén',
    id, 'ShoppingCart', 4, 1 FROM modulos_sistema WHERE codigo = 'compras';

-- Submódulos de Ventas
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'ventas.pos', 'Punto de Venta', 'Interfaz de caja y cobro al cliente',
    id, 'Monitor', 1, 1 FROM modulos_sistema WHERE codigo = 'ventas';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'ventas.cajas', 'Caja', 'Apertura y cierre de caja',
    id, 'Wallet', 2, 1 FROM modulos_sistema WHERE codigo = 'ventas';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'ventas.movimientos', 'Movimientos de Caja', 'Ingresos y egresos de caja',
    id, 'Receipt', 3, 1 FROM modulos_sistema WHERE codigo = 'ventas';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'ventas.historial', 'Historial de Ventas', 'Historial de ventas realizadas',
    id, 'History', 4, 1 FROM modulos_sistema WHERE codigo = 'ventas';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)

SELECT 'ventas.gastos', 'Gastos', 'Registro de gastos operativos por sede',
    id, 'TrendingDown', 5, 1 FROM modulos_sistema WHERE codigo = 'ventas';

-- Submódulos de Devoluciones
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'devoluciones.clientes', 'Clientes', 'Devoluciones de clientes por ventas realizadas',
    id, 'UserCircle', 1, 1 FROM modulos_sistema WHERE codigo = 'devoluciones';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'devoluciones.proveedores', 'Proveedores', 'Devoluciones a proveedores por mercadería recibida',
    id, 'Truck', 2, 1 FROM modulos_sistema WHERE codigo = 'devoluciones';

-- Submódulos de Facturación
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'facturacion.comprobantes', 'Comprobantes', 'Facturas, boletas y notas',
    id, 'FileText', 1, 1 FROM modulos_sistema WHERE codigo = 'facturacion';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'facturacion.series', 'Series', 'Series de comprobantes electrónicos',
    id, 'Hash', 2, 1 FROM modulos_sistema WHERE codigo = 'facturacion';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'facturacion.metodos', 'Métodos de Pago', 'Configuración de métodos de pago',
    id, 'CreditCard', 3, 1 FROM modulos_sistema WHERE codigo = 'facturacion';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'facturacion.pse', 'PSE Electrónico', 'Configuración y emisión de comprobantes electrónicos PSE',
    id, 'Zap', 4, 1 FROM modulos_sistema WHERE codigo = 'facturacion';

-- ============================================================
-- 2.5. SUB-SUBMÓDULOS (tabs internos dentro de submódulos)
-- ============================================================

-- Tabs internos de configuracion.negocio
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.negocio.negocio', 'Mi Negocio', 'Datos generales del negocio',
    id, 'Building2', 1, 1 FROM modulos_sistema WHERE codigo = 'configuracion.negocio';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.negocio.sedes', 'Sedes', 'Gestión de sedes y almacenes',
    id, 'MapPin', 2, 1 FROM modulos_sistema WHERE codigo = 'configuracion.negocio';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.negocio.tienda-online', 'Tienda Online', 'Configuración de la tienda online',
    id, 'Globe', 3, 1 FROM modulos_sistema WHERE codigo = 'configuracion.negocio';

-- Tabs internos de configuracion.operaciones
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.operaciones.metodos-pago', 'Métodos de Pago', 'Configuración de métodos de pago aceptados',
    id, 'CreditCard', 1, 1 FROM modulos_sistema WHERE codigo = 'configuracion.operaciones';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.operaciones.zonas-delivery', 'Zonas Delivery', 'Configuración de zonas de reparto',
    id, 'Truck', 2, 1 FROM modulos_sistema WHERE codigo = 'configuracion.operaciones';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'configuracion.operaciones.mesas', 'Mesas', 'Gestión de mesas del local',
    id, 'LayoutGrid', 3, 1 FROM modulos_sistema WHERE codigo = 'configuracion.operaciones';

-- Tabs internos de usuarios (tabs de la página principal)
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'usuarios.usuarios', 'Usuarios', 'Gestión de usuarios del negocio',
    id, 'Users', 1, 1 FROM modulos_sistema WHERE codigo = 'usuarios';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'usuarios.clientes', 'Clientes', 'Base de datos de clientes',
    id, 'UserCheck', 2, 1 FROM modulos_sistema WHERE codigo = 'usuarios';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'usuarios.roles', 'Roles', 'Gestión de roles y permisos personalizados',
    id, 'Shield', 3, 1 FROM modulos_sistema WHERE codigo = 'usuarios';

-- Tabs internos de catalogo.clasificaciones
INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'catalogo.clasificaciones.categorias', 'Categorías', 'Gestión de categorías de productos',
    id, 'FolderTree', 1, 1 FROM modulos_sistema WHERE codigo = 'catalogo.clasificaciones';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'catalogo.clasificaciones.marcas', 'Marcas', 'Gestión de marcas de productos',
    id, 'Award', 2, 1 FROM modulos_sistema WHERE codigo = 'catalogo.clasificaciones';

INSERT IGNORE INTO modulos_sistema (codigo, nombre, descripcion, modulo_padre_id, icono, orden, esta_activo)
SELECT 'catalogo.clasificaciones.unidades', 'Unidades de Medida', 'Gestión de unidades de medida',
    id, 'Ruler', 3, 1 FROM modulos_sistema WHERE codigo = 'catalogo.clasificaciones';

-- ============================================================
-- 3. PERMISOS DE ACCESO (tipo_accion = 'ver')
--    Un permiso por módulo/submódulo = acceso a esa sección
-- ============================================================

INSERT IGNORE INTO permisos_sistema (modulo_id, codigo, nombre, descripcion, tipo_accion)
SELECT id, CONCAT('m.', codigo), CONCAT('Acceder a ', nombre), descripcion, 'ver'
FROM modulos_sistema;

-- ============================================================
-- 4. ASIGNAR TODOS LOS PERMISOS A LOS ROLES ADMINISTRADOR
--    (es_rol_sistema = 1 Y nombre = 'Administrador')
-- ============================================================

-- Asegurar que la columna alcance tenga valor por defecto en registros existentes
UPDATE roles_permisos SET alcance = 'completo' WHERE alcance IS NULL;

INSERT IGNORE INTO roles_permisos (rol_id, permiso_id, alcance)
SELECT r.id, p.id, 'completo'
FROM roles r
CROSS JOIN permisos_sistema p
WHERE r.nombre = 'Administrador' AND r.es_rol_sistema = 1;

-- ============================================================
-- 5. CREAR ROL "Cajero" PREDEFINIDO (es_rol_sistema = 1)
--    Se crea por cada negocio existente, con alcance caja_asignada
-- ============================================================

INSERT IGNORE INTO roles (negocio_id, nombre, descripcion, es_rol_sistema)
SELECT id, 'Cajero', 'Cajero con acceso limitado al POS, caja e historial del día', 1
FROM negocios;

-- Asignar permisos de cajero (alcance caja_asignada)
-- Módulos: ventas, ventas.pos, ventas.cajas, ventas.movimientos, ventas.historial, facturacion, facturacion.comprobantes
INSERT IGNORE INTO roles_permisos (rol_id, permiso_id, alcance)
SELECT r.id, p.id, 'caja_asignada'
FROM roles r
JOIN permisos_sistema p ON p.codigo IN (
    'm.ventas', 'm.ventas.pos', 'm.ventas.cajas',
    'm.ventas.movimientos', 'm.ventas.historial',
    'm.facturacion', 'm.facturacion.comprobantes'
)
WHERE r.nombre = 'Cajero' AND r.es_rol_sistema = 1;

-- ============================================================
-- RESULTADO ESPERADO:

--   modulos_sistema: 46 filas (11 padres + 20 hijos + 15 sub-hijos de tabs)
--   permisos_sistema: 46 permisos (uno por módulo)
--   roles_permisos: N × 46 (donde N = negocios con rol Administrador)
-- ============================================================
