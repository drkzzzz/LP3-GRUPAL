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
-- RESULTADO ESPERADO:
--   modulos_sistema: 28 filas (10 padres + 18 hijos)
--   permisos_sistema: 28 permisos (uno por módulo)
--   roles_permisos: N × 28 (donde N = negocios con rol Administrador)
-- ============================================================
