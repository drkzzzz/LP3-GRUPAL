-- ============================================================
-- DATOS DE PRUEBA - BLOQUE 7: CLIENTES
-- Sistema DrinkGo - Multi-tenant
-- ============================================================
-- IMPORTANTE: Ejecutar DESPUÉS de tener datos de:
--   - negocios (negocio_id = 1)
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 7.1 CLIENTES
-- ============================================================

INSERT INTO clientes (
    negocio_id, uuid, tipo_cliente, nombres, apellidos, razon_social,
    tipo_documento, numero_documento, email, telefono, telefono_secundario,
    fecha_nacimiento, genero, hash_contrasena, email_verificado_en,
    total_compras, total_pedidos, acepta_marketing, canal_marketing,
    esta_activo, notas, ultima_compra_en, creado_en, actualizado_en
) VALUES
-- Cliente 1: Cliente individual activo — comprador frecuente
(1, '550e8400-e29b-41d4-a716-446655440001', 'individual',
 'Carlos Alberto', 'Mendoza Quispe', NULL,
 'DNI', '72345678', 'carlos.mendoza@gmail.com', '951100201', NULL,
 '1990-05-15', 'M', NULL, NULL,
 2450.50, 18, 1, 'redes_sociales',
 1, 'Cliente frecuente. Prefiere whisky y vinos tintos.', '2026-02-15 20:30:00', NOW(), NOW()),

-- Cliente 2: Cliente individual activo — joven
(1, '550e8400-e29b-41d4-a716-446655440002', 'individual',
 'María Fernanda', 'Torres Castillo', NULL,
 'DNI', '71234567', 'maria.torres@hotmail.com', '952200302', '014567890',
 '1998-11-22', 'F', '$2a$10$fakehashexample000000000000000000000000000002', '2026-01-10 08:00:00',
 890.00, 7, 1, 'tienda_online',
 1, 'Registrada en tienda online. Prefiere cócteles y cervezas artesanales.', '2026-02-12 18:45:00', NOW(), NOW()),

-- Cliente 3: Cliente empresa activo
(1, '550e8400-e29b-41d4-a716-446655440003', 'empresa',
 'Juan Carlos', 'Vargas Rojas', 'Eventos & Celebraciones S.A.C.',
 'RUC', '20601234567', 'compras@eventoscelebraciones.pe', '953300403', '016789012',
 NULL, 'M', NULL, NULL,
 12500.00, 35, 0, 'referido',
 1, 'Empresa de eventos. Compra al por mayor para fiestas corporativas y bodas.', '2026-02-10 10:00:00', NOW(), NOW()),

-- Cliente 4: Cliente individual activo — adulto mayor
(1, '550e8400-e29b-41d4-a716-446655440004', 'individual',
 'Roberto', 'Huamán Palomino', NULL,
 'DNI', '08765432', 'roberto.huaman@yahoo.com', '954400504', NULL,
 '1965-03-08', 'M', NULL, NULL,
 3200.00, 22, 0, 'presencial',
 1, 'Cliente leal desde apertura. Prefiere pisco y vino tinto.', '2026-02-14 19:15:00', NOW(), NOW()),

-- Cliente 5: Cliente individual activa
(1, '550e8400-e29b-41d4-a716-446655440005', 'individual',
 'Lucía Patricia', 'Fernández Gutiérrez', NULL,
 'DNI', '74567890', 'lucia.fernandez@gmail.com', '955500605', NULL,
 '1992-07-19', 'F', '$2a$10$fakehashexample000000000000000000000000000005', '2026-02-01 12:00:00',
 560.00, 4, 1, 'tienda_online',
 1, 'Compra principalmente por delivery. Le gustan los combos.', '2026-02-08 21:00:00', NOW(), NOW()),

-- Cliente 6: Cliente empresa activo
(1, '550e8400-e29b-41d4-a716-446655440006', 'empresa',
 'Ana María', 'López Díaz', 'Restaurante El Buen Sabor E.I.R.L.',
 'RUC', '20509876543', 'alopez@elbuensabor.pe', '956600706', '012345678',
 NULL, 'F', NULL, NULL,
 8900.00, 15, 1, 'visita_comercial',
 1, 'Restaurante aliado. Compra vinos y licores para su carta de bebidas.', '2026-02-13 11:30:00', NOW(), NOW()),

-- Cliente 7: Cliente individual — extranjero con CE
(1, '550e8400-e29b-41d4-a716-446655440007', 'individual',
 'Diego Alejandro', 'Martínez Rivera', NULL,
 'CE', 'CE20198765', 'diego.martinez@outlook.com', '957700807', NULL,
 '1988-12-01', 'M', NULL, NULL,
 320.00, 3, 0, 'redes_sociales',
 1, 'Extranjero residente. Compra gin y tónica.', '2026-01-25 17:00:00', NOW(), NOW()),

-- Cliente 8: Cliente individual activa — con pasaporte
(1, '550e8400-e29b-41d4-a716-446655440008', 'individual',
 'Valentina', 'Romero Salazar', NULL,
 'PASAPORTE', 'AB1234567', 'valentina.romero@gmail.com', '958800908', NULL,
 '1995-09-30', 'F', NULL, NULL,
 150.00, 1, 1, 'tienda_online',
 1, 'Turista recurrente. Primera compra reciente.', '2026-02-05 16:30:00', NOW(), NOW()),

-- Cliente 9: Cliente individual — INACTIVO (borrado lógico)
(1, '550e8400-e29b-41d4-a716-446655440009', 'individual',
 'Pedro Pablo', 'Sánchez Chávez', NULL,
 'DNI', '09876543', 'pedro.sanchez@gmail.com', '959900109', NULL,
 '1978-01-20', 'M', NULL, NULL,
 75.00, 1, 0, 'presencial',
 0, 'Cliente inactivo. Solicitó eliminación de cuenta.', '2025-08-10 14:00:00',
 '2025-06-01 10:00:00', '2025-12-15 09:00:00'),

-- Cliente 10: Cliente empresa — INACTIVO (borrado lógico)
(1, '550e8400-e29b-41d4-a716-446655440010', 'empresa',
 'Rosa', 'Paredes Vega', 'Discoteca Neon Nights S.R.L.',
 'RUC', '20701122334', 'rparedes@neonnights.pe', '960000210', '017654321',
 NULL, 'F', NULL, NULL,
 4500.00, 8, 0, 'visita_comercial',
 0, 'Empresa cerró operaciones. Cuenta desactivada.', '2025-10-20 23:00:00',
 '2025-03-15 08:00:00', '2025-11-01 10:00:00'),

-- Cliente 11: Cliente individual — género no especificado
(1, '550e8400-e29b-41d4-a716-446655440011', 'individual',
 'Alex', 'Ríos Medina', NULL,
 'DNI', '76543210', 'alex.rios@gmail.com', '961100311', NULL,
 '2000-04-12', 'NO_ESPECIFICADO', NULL, NULL,
 420.00, 5, 1, 'redes_sociales',
 1, 'Cliente joven. Compra cervezas artesanales y snacks.', '2026-02-16 22:00:00', NOW(), NOW()),

-- Cliente 12: Cliente individual — género OTRO
(1, '550e8400-e29b-41d4-a716-446655440012', 'individual',
 'Camila Andrea', 'Delgado Cruz', NULL,
 'DNI', '77654321', 'camila.delgado@yahoo.com', '962200412', '019876543',
 '1993-06-25', 'OTRO', '$2a$10$fakehashexample000000000000000000000000000012', '2026-01-20 15:00:00',
 680.00, 6, 1, 'tienda_online',
 1, 'Clienta activa en tienda online. Le gustan las promociones.', '2026-02-11 20:15:00', NOW(), NOW()),

-- Cliente 13: Cliente individual — sin documento (solo nombre)
(1, '550e8400-e29b-41d4-a716-446655440013', 'individual',
 'Miguel Ángel', 'Condori Apaza', NULL,
 NULL, NULL, NULL, '963300513', NULL,
 '1985-08-14', 'M', NULL, NULL,
 180.00, 2, 0, 'presencial',
 1, 'Cliente ocasional. Solo compra en tienda física.', '2026-01-30 19:45:00', NOW(), NOW()),

-- Cliente 14: Cliente empresa — compras grandes
(1, '550e8400-e29b-41d4-a716-446655440014', 'empresa',
 'Fernando', 'Guzmán Tapia', 'Distribuidora de Licores del Centro S.A.',
 'RUC', '20801234568', 'fguzman@distlicocentro.pe', '964400614', '013456789',
 NULL, 'M', NULL, NULL,
 25000.00, 50, 1, 'referido',
 1, 'Distribuidor minorista. Compra grandes volúmenes para reventa.', '2026-02-16 09:00:00', NOW(), NOW()),

-- Cliente 15: Cliente individual — INACTIVO adicional
(1, '550e8400-e29b-41d4-a716-446655440015', 'individual',
 'Sofía Esperanza', 'Villanueva Ramos', NULL,
 'DNI', '73210987', 'sofia.villanueva@gmail.com', '965500715', NULL,
 '1997-02-28', 'F', NULL, NULL,
 0.00, 0, 0, NULL,
 0, 'Se registró pero nunca compró. Cuenta desactivada por inactividad.', NULL,
 '2025-09-01 12:00:00', '2026-01-01 00:00:00');

-- ============================================================
-- 7.2 DIRECCIONES DE CLIENTES
-- ============================================================

INSERT INTO direcciones_cliente (
    cliente_id, etiqueta, direccion, direccion_2, ciudad, departamento,
    pais, codigo_postal, latitud, longitud, referencia,
    telefono_contacto, es_predeterminado, esta_activo, creado_en, actualizado_en
) VALUES
-- Cliente 1: Casa
(1, 'Casa', 'Av. Javier Prado Este 1234, San Isidro', 'Dpto. 501', 'Lima', 'Lima',
 'PE', '15036', -12.09710000, -77.03680000, 'Frente al Centro Comercial Camino Real',
 '951100201', 1, 1, NOW(), NOW()),

-- Cliente 2: Casa y Oficina
(2, 'Casa', 'Jr. Huallaga 356, Cercado de Lima', NULL, 'Lima', 'Lima',
 'PE', '15001', -12.04620000, -77.02990000, 'A una cuadra de la Plaza de Armas',
 '952200302', 1, 1, NOW(), NOW()),

(2, 'Oficina', 'Av. Larco 650, Miraflores', 'Piso 3', 'Lima', 'Lima',
 'PE', '15074', -12.12190000, -77.03010000, 'Edificio Torre Larco, frente al Parque Kennedy',
 '952200302', 0, 1, NOW(), NOW()),

-- Cliente 3: Empresa — Local de eventos
(3, 'Local Principal', 'Av. El Sol 890, San Borja', NULL, 'Lima', 'Lima',
 'PE', '15037', -12.10180000, -76.99850000, 'Al costado del Museo de la Nación',
 '953300403', 1, 1, NOW(), NOW()),

(3, 'Almacén', 'Calle Los Industriales 245, Ate', NULL, 'Lima', 'Lima',
 'PE', '15023', -12.05670000, -76.91230000, 'Zona industrial de Ate, cerca al Óvalo Santa Anita',
 '016789012', 0, 1, NOW(), NOW()),

-- Cliente 4: Casa
(4, 'Casa', 'Av. Brasil 2580, Jesús María', NULL, 'Lima', 'Lima',
 'PE', '15072', -12.07890000, -77.04560000, 'Cerca al Hospital Rebagliati',
 '954400504', 1, 1, NOW(), NOW()),

-- Cliente 5: Casa
(5, 'Casa', 'Calle Las Begonias 120, Surquillo', 'Dpto. 202', 'Lima', 'Lima',
 'PE', '15038', -12.11340000, -77.02120000, 'A dos cuadras del Mercado N°1 de Surquillo',
 '955500605', 1, 1, NOW(), NOW()),

-- Cliente 6: Restaurante
(6, 'Restaurante', 'Av. Petit Thouars 1780, Lince', NULL, 'Lima', 'Lima',
 'PE', '15073', -12.08450000, -77.03420000, 'Esquina con Av. Arequipa',
 '956600706', 1, 1, NOW(), NOW()),

-- Cliente 7: Departamento
(7, 'Departamento', 'Malecón Cisneros 1450, Miraflores', 'Dpto. 1201', 'Lima', 'Lima',
 'PE', '15074', -12.12560000, -77.03780000, 'Edificio frente al malecón, con vista al mar',
 '957700807', 1, 1, NOW(), NOW()),

-- Cliente 11: Casa
(11, 'Casa', 'Av. Universitaria 3456, Los Olivos', NULL, 'Lima', 'Lima',
 'PE', '15304', -11.98120000, -77.07140000, 'Cerca al Centro Comercial Mega Plaza',
 '961100311', 1, 1, NOW(), NOW()),

-- Cliente 12: Casa y Trabajo
(12, 'Casa', 'Jr. Cusco 560, Breña', 'Int. B', 'Lima', 'Lima',
 'PE', '15083', -12.05670000, -77.05430000, 'A media cuadra de la Av. Venezuela',
 '962200412', 1, 1, NOW(), NOW()),

(12, 'Trabajo', 'Av. Rivera Navarrete 762, San Isidro', 'Piso 8', 'Lima', 'Lima',
 'PE', '15036', -12.09980000, -77.03210000, 'Centro Financiero de San Isidro',
 '019876543', 0, 1, NOW(), NOW()),

-- Cliente 14: Empresa — Oficina y almacén
(14, 'Oficina Central', 'Av. Abancay 234, Cercado de Lima', 'Piso 2', 'Lima', 'Lima',
 'PE', '15001', -12.05120000, -77.02780000, 'A una cuadra del Congreso de la República',
 '964400614', 1, 1, NOW(), NOW()),

(14, 'Almacén Distribución', 'Av. Naranjal 1180, Los Olivos', NULL, 'Lima', 'Lima',
 'PE', '15301', -11.97230000, -77.06890000, 'Zona industrial de Los Olivos',
 '013456789', 0, 1, NOW(), NOW());

-- ============================================================
-- VERIFICACIÓN DE DATOS INSERTADOS
-- ============================================================
SELECT '=== RESUMEN DATOS BLOQUE 7 ===' AS '';
SELECT 'Clientes total:' AS Tabla, COUNT(*) AS Total FROM clientes WHERE negocio_id = 1;
SELECT 'Clientes activos:' AS Tabla, COUNT(*) AS Total FROM clientes WHERE negocio_id = 1 AND esta_activo = 1;
SELECT 'Clientes inactivos:' AS Tabla, COUNT(*) AS Total FROM clientes WHERE negocio_id = 1 AND esta_activo = 0;
SELECT 'Clientes por tipo:' AS '';
SELECT tipo_cliente, COUNT(*) AS cantidad FROM clientes WHERE negocio_id = 1 GROUP BY tipo_cliente;
SELECT 'Clientes por tipo_documento:' AS '';
SELECT IFNULL(tipo_documento, 'SIN_DOCUMENTO') AS tipo_doc, COUNT(*) AS cantidad FROM clientes WHERE negocio_id = 1 GROUP BY tipo_documento;
SELECT 'Clientes por género:' AS '';
SELECT IFNULL(genero, 'NULL') AS genero, COUNT(*) AS cantidad FROM clientes WHERE negocio_id = 1 GROUP BY genero;
SELECT 'Direcciones de clientes:' AS Tabla, COUNT(*) AS Total FROM direcciones_cliente;
