-- ============================================================
-- SEED 10: USUARIOS PROGRAMADORES DEMO
-- Base de datos: licores_drinkgo (o drinkgo_db según entorno)
-- Tabla: usuarios_plataforma
-- ============================================================
-- Contraseñas (todas hasheadas con BCrypt):
-- anggelo@drinkgo.dev  = Admin123!
-- programador2@drinkgo.dev = Admin123!
-- programador3@drinkgo.dev = Admin123!
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
-- Programador 1: módulo Catálogo + Devoluciones (ventas)
(
    UUID(),
    'anggelo@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Anggelo',
    'Programador',
    NULL,
    'programador',
    JSON_ARRAY('m.catalogo', 'm.ventas'),
    1,
    NOW()
),
-- Programador 2: módulo Inventario + Compras
(
    UUID(),
    'programador.inventario@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Demo',
    'Inventario',
    NULL,
    'programador',
    JSON_ARRAY('m.inventario', 'm.compras'),
    1,
    NOW()
),
-- Programador 3: módulo Facturación + Reportes
(
    UUID(),
    'programador.facturacion@drinkgo.dev',
    '$2a$10$nb74QxjneuyJQDzt3uBCt.ZRGdqT1O5P6aIvSyCqUh1cD4bzyrlpC', -- Admin123!
    'Demo',
    'Facturacion',
    NULL,
    'programador',
    JSON_ARRAY('m.facturacion', 'm.reportes'),
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
-- Módulos: Catálogo, Ventas/POS
-- ============================================================
-- Email: programador.inventario@drinkgo.dev
-- Password: Admin123!
-- Módulos: Inventario, Compras
-- ============================================================
-- Email: programador.facturacion@drinkgo.dev
-- Password: Admin123!
-- Módulos: Facturación, Reportes
-- ============================================================
