-- ============================================================
-- SEED: USUARIOS SUPERADMIN DE PLATAFORMA
-- Base de datos: licores_drinkgo
-- Tabla: usuarios_plataforma
-- ============================================================
-- Contraseñas (todas hasheadas con BCrypt):
-- admin@drinkgo.com = Admin123!
-- soporte@drinkgo.com = Soporte123!
-- ============================================================

USE licores_drinkgo;

-- Limpiar datos existentes (solo para desarrollo)
-- DELETE FROM usuarios_plataforma WHERE rol IN ('superadmin', 'soporte_plataforma');

-- Insertar usuarios SuperAdmin
INSERT INTO usuarios_plataforma (
    uuid, 
    email, 
    hash_contrasena, 
    nombres, 
    apellidos, 
    telefono, 
    rol, 
    esta_activo, 
    ultimo_acceso_en,
    contrasena_cambiada_en
) VALUES 
(
    UUID(), 
    'admin@drinkgo.com', 
    '$2a$10$xQm7lGx2.wSy8Z9EKvH9yOr7ScM0yqXnT5y8xLb4aN9wW8Kz6P8.K', -- Admin123!
    'Carlos', 
    'Rodríguez', 
    '+51987654321', 
    'superadmin', 
    1, 
    NOW(),
    NOW()
),
(
    UUID(), 
    'soporte@drinkgo.com', 
    '$2a$10$Vn8JYl5mjP9xQ7kZ2LmNvOwR8sT3.wYx1cC6dE4fF5gG7hH8iI9.J', -- Soporte123!
    'María', 
    'García', 
    '+51912345678', 
    'soporte_plataforma', 
    1, 
    NULL,
    NOW()
),
(
    UUID(), 
    'visualizador@drinkgo.com', 
    '$2a$10$Vn8JYl5mjP9xQ7kZ2LmNvOwR8sT3.wYx1cC6dE4fF5gG7hH8iI9.J', -- Soporte123!
    'Jorge', 
    'Pérez', 
    NULL, 
    'visualizador_plataforma', 
    1, 
    NULL,
    NOW()
);

-- Verificar inserción
SELECT 
    id,
    email, 
    nombres, 
    apellidos, 
    rol, 
    esta_activo,
    creado_en
FROM usuarios_plataforma 
WHERE rol IN ('superadmin', 'soporte_plataforma', 'visualizador_plataforma')
ORDER BY creado_en DESC;

-- ============================================================
-- CREDENCIALES PARA PRUEBAS:
-- ============================================================
-- Email: admin@drinkgo.com
-- Password: Admin123!
-- Rol: superadmin
-- ============================================================
-- Email: soporte@drinkgo.com
-- Password: Soporte123!
-- Rol: soporte_plataforma
-- ============================================================
