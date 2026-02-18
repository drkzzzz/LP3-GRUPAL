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
    tipo_documento, numero_documento, email, telefono, direccion,
    total_compras, total_pedidos,
    esta_activo, notas, ultima_compra_en, creado_en, actualizado_en
) VALUES
-- Cliente 1: Cliente individual activo — comprador frecuente
(1, '550e8400-e29b-41d4-a716-446655440001', 'individual',
 'Carlos Alberto', 'Mendoza Quispe', NULL,
 'DNI', '72345678', 'carlos.mendoza@gmail.com', '951100201',
 'Av. Javier Prado Este 1234, San Isidro',
 2450.50, 18,
 1, 'Cliente frecuente. Prefiere whisky y vinos tintos.', '2026-02-15 20:30:00', NOW(), NOW()),

-- Cliente 2: Cliente individual activo — joven
(1, '550e8400-e29b-41d4-a716-446655440002', 'individual',
 'María Fernanda', 'Torres Castillo', NULL,
 'DNI', '71234567', 'maria.torres@hotmail.com', '952200302',
 'Jr. Huallaga 356, Cercado de Lima',
 890.00, 7,
 1, 'Registrada en tienda online. Prefiere cócteles y cervezas artesanales.', '2026-02-12 18:45:00', NOW(), NOW()),

-- Cliente 3: Cliente empresa activo
(1, '550e8400-e29b-41d4-a716-446655440003', 'empresa',
 'Juan Carlos', 'Vargas Rojas', 'Eventos & Celebraciones S.A.C.',
 'RUC', '20601234567', 'compras@eventoscelebraciones.pe', '953300403',
 'Av. El Sol 890, San Borja',
 12500.00, 35,
 1, 'Empresa de eventos. Compra al por mayor para fiestas corporativas y bodas.', '2026-02-10 10:00:00', NOW(), NOW()),

-- Cliente 4: Cliente individual activo — adulto mayor
(1, '550e8400-e29b-41d4-a716-446655440004', 'individual',
 'Roberto', 'Huamán Palomino', NULL,
 'DNI', '08765432', 'roberto.huaman@yahoo.com', '954400504',
 'Av. Brasil 2580, Jesús María',
 3200.00, 22,
 1, 'Cliente leal desde apertura. Prefiere pisco y vino tinto.', '2026-02-14 19:15:00', NOW(), NOW()),

-- Cliente 5: Cliente individual activa
(1, '550e8400-e29b-41d4-a716-446655440005', 'individual',
 'Lucía Patricia', 'Fernández Gutiérrez', NULL,
 'DNI', '74567890', 'lucia.fernandez@gmail.com', '955500605',
 'Calle Las Begonias 120, Surquillo',
 560.00, 4,
 1, 'Compra principalmente por delivery. Le gustan los combos.', '2026-02-08 21:00:00', NOW(), NOW()),

-- Cliente 6: Cliente empresa activo
(1, '550e8400-e29b-41d4-a716-446655440006', 'empresa',
 'Ana María', 'López Díaz', 'Restaurante El Buen Sabor E.I.R.L.',
 'RUC', '20509876543', 'alopez@elbuensabor.pe', '956600706',
 'Av. Petit Thouars 1780, Lince',
 8900.00, 15,
 1, 'Restaurante aliado. Compra vinos y licores para su carta de bebidas.', '2026-02-13 11:30:00', NOW(), NOW()),

-- Cliente 7: Cliente individual — extranjero con CE
(1, '550e8400-e29b-41d4-a716-446655440007', 'individual',
 'Diego Alejandro', 'Martínez Rivera', NULL,
 'CE', 'CE20198765', 'diego.martinez@outlook.com', '957700807',
 'Malecón Cisneros 1450, Miraflores',
 320.00, 3,
 1, 'Extranjero residente. Compra gin y tónica.', '2026-01-25 17:00:00', NOW(), NOW()),

-- Cliente 8: Cliente individual activa — con pasaporte
(1, '550e8400-e29b-41d4-a716-446655440008', 'individual',
 'Valentina', 'Romero Salazar', NULL,
 'PASAPORTE', 'AB1234567', 'valentina.romero@gmail.com', '958800908',
 NULL,
 150.00, 1,
 1, 'Turista recurrente. Primera compra reciente.', '2026-02-05 16:30:00', NOW(), NOW()),

-- Cliente 9: Cliente individual — INACTIVO (borrado lógico)
(1, '550e8400-e29b-41d4-a716-446655440009', 'individual',
 'Pedro Pablo', 'Sánchez Chávez', NULL,
 'DNI', '09876543', 'pedro.sanchez@gmail.com', '959900109',
 NULL,
 75.00, 1,
 0, 'Cliente inactivo. Solicitó eliminación de cuenta.', '2025-08-10 14:00:00',
 '2025-06-01 10:00:00', '2025-12-15 09:00:00'),

-- Cliente 10: Cliente empresa — INACTIVO (borrado lógico)
(1, '550e8400-e29b-41d4-a716-446655440010', 'empresa',
 'Rosa', 'Paredes Vega', 'Discoteca Neon Nights S.R.L.',
 'RUC', '20701122334', 'rparedes@neonnights.pe', '960000210',
 NULL,
 4500.00, 8,
 0, 'Empresa cerró operaciones. Cuenta desactivada.', '2025-10-20 23:00:00',
 '2025-03-15 08:00:00', '2025-11-01 10:00:00'),

-- Cliente 11: Cliente individual
(1, '550e8400-e29b-41d4-a716-446655440011', 'individual',
 'Alex', 'Ríos Medina', NULL,
 'DNI', '76543210', 'alex.rios@gmail.com', '961100311',
 'Av. Universitaria 3456, Los Olivos',
 420.00, 5,
 1, 'Cliente joven. Compra cervezas artesanales y snacks.', '2026-02-16 22:00:00', NOW(), NOW()),

-- Cliente 12: Cliente individual
(1, '550e8400-e29b-41d4-a716-446655440012', 'individual',
 'Camila Andrea', 'Delgado Cruz', NULL,
 'DNI', '77654321', 'camila.delgado@yahoo.com', '962200412',
 'Jr. Cusco 560, Breña',
 680.00, 6,
 1, 'Clienta activa en tienda online. Le gustan las promociones.', '2026-02-11 20:15:00', NOW(), NOW()),

-- Cliente 13: Cliente individual — sin documento (solo nombre)
(1, '550e8400-e29b-41d4-a716-446655440013', 'individual',
 'Miguel Ángel', 'Condori Apaza', NULL,
 NULL, NULL, NULL, '963300513',
 NULL,
 180.00, 2,
 1, 'Cliente ocasional. Solo compra en tienda física.', '2026-01-30 19:45:00', NOW(), NOW()),

-- Cliente 14: Cliente empresa — compras grandes
(1, '550e8400-e29b-41d4-a716-446655440014', 'empresa',
 'Fernando', 'Guzmán Tapia', 'Distribuidora de Licores del Centro S.A.',
 'RUC', '20801234568', 'fguzman@distlicocentro.pe', '964400614',
 'Av. Abancay 234, Cercado de Lima',
 25000.00, 50,
 1, 'Distribuidor minorista. Compra grandes volúmenes para reventa.', '2026-02-16 09:00:00', NOW(), NOW()),

-- Cliente 15: Cliente individual — INACTIVO adicional
(1, '550e8400-e29b-41d4-a716-446655440015', 'individual',
 'Sofía Esperanza', 'Villanueva Ramos', NULL,
 'DNI', '73210987', 'sofia.villanueva@gmail.com', '965500715',
 NULL,
 0.00, 0,
 0, 'Se registró pero nunca compró. Cuenta desactivada por inactividad.', NULL,
 '2025-09-01 12:00:00', '2026-01-01 00:00:00');

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
