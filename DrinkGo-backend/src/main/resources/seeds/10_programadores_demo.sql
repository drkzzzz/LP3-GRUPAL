-- ============================================================
-- SEED 10: USUARIOS PROGRAMADORES DEMO
-- Base de datos: drinkgo_db
-- Tabla: usuarios_plataforma
-- ============================================================
-- Contraseñas (todas hasheadas con BCrypt):
-- Todas = Admin123!
-- ============================================================
-- Los módulos asignados siguen el formato del permiso del frontend
-- (con prefijo "m."): m.catalogo, m.inventario, m.ventas, etc.
-- El campo modulos_asignados es un array JSON de códigos padre;
-- gracias a la herencia en hasPermiso(), si tiene "m.catalogo"
-- automáticamente puede ver m.catalogo.productos, .clasificaciones, etc.
-- ============================================================

USE drinkgo_db;

INSERT IGNORE INTO usuarios_plataforma (
    uuid,
    email,
    hash_contrasena,
    nombres,
    apellidos,
    telefono,
    rol,
    modulos_asignados,
    esta_activo,
    contrasena_cambiada_en
) VALUES
-- Anggelo: Catálogo + Devoluciones
(
    UUID(),
    'anggelo@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Anggelo',
    'Programador',
    NULL,
    'programador',
    JSON_ARRAY('m.catalogo', 'm.devoluciones'),
    1,
    NOW()
),
-- Santiago: Inventario + Proveedores/Compras + Pedidos
(
    UUID(),
    'santiago@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Santiago',
    'Programador',
    NULL,
    'programador',
    JSON_ARRAY('m.inventario', 'm.compras', 'm.pedidos'),
    1,
    NOW()
),
-- Willy: Facturación + Ventas/POS
(
    UUID(),
    'willy@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Willy',
    'Programador',
    NULL,
    'programador',
    JSON_ARRAY('m.facturacion', 'm.ventas'),
    1,
    NOW()
),
-- Chavez: Reportes + Usuarios/Clientes
(
    UUID(),
    'chavez@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Chavez',
    'Programador',
    NULL,
    'programador',
    JSON_ARRAY('m.reportes', 'm.usuarios'),
    1,
    NOW()
),
-- Carlos: Configuración + Dashboard
(
    UUID(),
    'carlos@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Carlos',
    'Programador',
    NULL,
    'programador',
    JSON_ARRAY('m.configuracion', 'm.dashboard', 'm.ventas.mesas'),
    1,
    NOW()
);

-- Verificar inserción
SELECT
    id,
    email,
    nombres,
    apellidos,
    rol,
    modulos_asignados,
    esta_activo,
    creado_en
FROM usuarios_plataforma
WHERE rol = 'programador'
ORDER BY creado_en DESC;

-- ============================================================
-- CREDENCIALES PARA PRUEBAS:
-- ============================================================
-- Email: anggelo@drinkgo.dev
-- Password: Admin123!
-- Módulos: Catálogo, Devoluciones
-- ============================================================
-- Email: santiago@drinkgo.dev
-- Password: Admin123!
-- Módulos: Inventario, Proveedores/Compras, Pedidos
-- ============================================================
-- Email: willy@drinkgo.dev
-- Password: Admin123!
-- Módulos: Facturación, Ventas/POS
-- ============================================================
-- Email: chavez@drinkgo.dev
-- Password: Admin123!
-- Módulos: Reportes, Usuarios y Clientes
-- ============================================================
-- Email: carlos@drinkgo.dev
-- Password: Admin123!
-- Módulos: Configuración, Dashboard, Gestión de Mesas
-- ============================================================
