-- ============================================================
-- SEEDS BLOQUE 12: GASTOS E INGRESOS
-- ============================================================
-- Este archivo contiene datos de prueba para el módulo de gastos
-- Fecha: 17 de febrero de 2026
-- ============================================================

USE drinkgo_db;

-- NOTA: Este seed asume que ya existen las siguientes entidades:
-- - negocios (id: 1, 2, 3)
-- - sedes (al menos 1 por negocio)
-- - proveedores (al menos 5)
-- - usuarios (al menos 5 usuarios)

-- ============================================================
-- CATEGORÍAS DE GASTOS
-- ============================================================

INSERT INTO categorias_gasto (
    id, negocio_id, nombre, codigo, padre_id, descripcion, esta_activo
) VALUES
-- Categorías principales para Negocio 1 (Licorería Don Pedro)
(1, 1, 'Operativos', 'OPR', NULL, 'Gastos operativos del día a día', 1),
(2, 1, 'Administrativos', 'ADM', NULL, 'Gastos administrativos y de gestión', 1),
(3, 1, 'Servicios', 'SRV', NULL, 'Servicios básicos y recurrentes', 1),
(4, 1, 'Marketing', 'MKT', NULL, 'Gastos de publicidad y marketing', 1),
(5, 1, 'Personal', 'PER', NULL, 'Gastos relacionados al personal', 1),
(6, 1, 'Mantenimiento', 'MNT', NULL, 'Mantenimiento de local y equipos', 1),

-- Subcategorías de Operativos (Negocio 1)
(7, 1, 'Transporte', 'OPR-TRN', 1, 'Gastos de transporte y combustible', 1),
(8, 1, 'Empaque y Embalaje', 'OPR-EMP', 1, 'Materiales de empaque para delivery', 1),
(9, 1, 'Suministros de Oficina', 'OPR-SUM', 1, 'Papelería y suministros de oficina', 1),

-- Subcategorías de Servicios (Negocio 1)
(10, 1, 'Luz', 'SRV-LUZ', 3, 'Servicio de energía eléctrica', 1),
(11, 1, 'Agua', 'SRV-AGU', 3, 'Servicio de agua potable', 1),
(12, 1, 'Internet', 'SRV-INT', 3, 'Servicio de internet', 1),
(13, 1, 'Teléfono', 'SRV-TEL', 3, 'Servicio telefónico', 1),

-- Subcategorías de Personal (Negocio 1)
(14, 1, 'Salarios', 'PER-SAL', 5, 'Salarios del personal', 1),
(15, 1, 'Bonificaciones', 'PER-BON', 5, 'Bonificaciones y gratificaciones', 1),
(16, 1, 'Capacitación', 'PER-CAP', 5, 'Capacitación del personal', 1),

-- Categorías principales para Negocio 2 (Licorería Los Andes)
(17, 2, 'Operativos', 'OPR', NULL, 'Gastos operativos diarios', 1),
(18, 2, 'Servicios', 'SRV', NULL, 'Servicios básicos', 1),
(19, 2, 'Personal', 'PER', NULL, 'Gastos de personal', 1),
(20, 2, 'Alquiler', 'ALQ', NULL, 'Alquiler de locales', 1),

-- Subcategorías para Negocio 2
(21, 2, 'Salarios', 'PER-SAL', 19, 'Salarios mensuales', 1),
(22, 2, 'Luz', 'SRV-LUZ', 18, 'Energía eléctrica', 1),
(23, 2, 'Internet', 'SRV-INT', 18, 'Conectividad', 1),

-- Categorías principales para Negocio 3 (DrinkGo Express)
(24, 3, 'Operativos', 'OPR', NULL, 'Gastos operativos', 1),
(25, 3, 'Tecnología', 'TEC', NULL, 'Software y hardware', 1),
(26, 3, 'Marketing Digital', 'MKT-DIG', NULL, 'Publicidad online', 1),
(27, 3, 'Personal', 'PER', NULL, 'Recursos humanos', 1),

-- Subcategorías para Negocio 3
(28, 3, 'Software SaaS', 'TEC-SAS', 25, 'Suscripciones de software', 1),
(29, 3, 'Hosting', 'TEC-HST', 25, 'Servicios de hosting', 1),
(30, 3, 'Redes Sociales', 'MKT-RED', 26, 'Publicidad en redes sociales', 1);

-- ============================================================
-- GASTOS
-- ============================================================

INSERT INTO gastos (
    id, negocio_id, sede_id, numero_gasto, categoria_id, proveedor_id,
    descripcion, monto, monto_impuesto, total, moneda,
    fecha_gasto, metodo_pago, referencia_pago, url_comprobante,
    estado, es_recurrente, periodo_recurrencia,
    aprobado_por, registrado_por, notas
) VALUES
-- GASTOS NEGOCIO 1 (Licorería Don Pedro) - ENERO 2026

-- Gasto 1: Luz (Recurrente mensual - Pagado)
(1, 1, 1, 'GAS-2026-0001', 10, NULL,
 'Recibo de luz - Enero 2026', 254.24, 45.76, 300.00, 'PEN',
 '2026-01-15', 'transferencia_bancaria', 'ENEL-2026-01-0012345', 'https://storage.drinkgo.pe/comprobantes/gas-0001.pdf',
 'pagado', 1, 'mensual',
 2, 1, 'Consumo mayor por refrigeradoras nuevas'),

-- Gasto 2: Internet (Recurrente mensual - Pagado)
(2, 1, 1, 'GAS-2026-0002', 12, NULL,
 'Plan internet 100 Mbps - Enero 2026', 84.75, 15.25, 100.00, 'PEN',
 '2026-01-01', 'efectivo', NULL, NULL,
 'pagado', 1, 'mensual',
 2, 1, 'Plan empresarial Movistar'),

-- Gasto 3: Transporte delivery (Aprobado - Pendiente pago)
(3, 1, 1, 'GAS-2026-0003', 7, NULL,
 'Combustible para mototaxi de delivery', 169.49, 30.51, 200.00, 'PEN',
 '2026-01-20', 'efectivo', NULL, NULL,
 'aprobado', 0, NULL,
 2, 1, 'Tanque lleno más propina al conductor'),

-- Gasto 4: Empaque y embalaje
(4, 1, 1, 'GAS-2026-0004', 8, 1,
 'Cajas de cartón y bolsas para delivery', 127.12, 22.88, 150.00, 'PEN',
 '2026-01-18', 'efectivo', NULL, 'https://storage.drinkgo.pe/comprobantes/gas-0004.pdf',
 'pagado', 0, NULL,
 2, 1, '100 cajas medianas + 200 bolsas'),

-- Gasto 5: Suministros de oficina
(5, 1, 1, 'GAS-2026-0005', 9, NULL,
 'Papel bond, lapiceros, folders', 42.37, 7.63, 50.00, 'PEN',
 '2026-01-22', 'efectivo', NULL, NULL,
 'pagado', 0, NULL,
 2, 1, 'Compra en Tai Loy'),

-- Gasto 6: Publicidad en Facebook
(6, 1, 1, 'GAS-2026-0006', 4, NULL,
 'Campaña publicitaria Facebook Ads - Enero', 423.73, 76.27, 500.00, 'PEN',
 '2026-01-10', 'tarjeta_credito', 'FB-ADS-2026-01-XXXX', NULL,
 'pagado', 0, NULL,
 2, 1, 'Campaña de San Valentín'),

-- Gasto 7: Salarios personal (Enero 2026)
(7, 1, 1, 'GAS-2026-0007', 14, NULL,
 'Planilla de sueldos - Enero 2026', 4237.29, 762.71, 5000.00, 'PEN',
 '2026-01-31', 'transferencia_bancaria', 'PLANILLA-2026-01', NULL,
 'pagado', 1, 'mensual',
 2, 1, '3 empleados + 1 part-time'),

-- Gasto 8: Mantenimiento de refrigeradora
(8, 1, 1, 'GAS-2026-0008', 6, NULL,
 'Reparación de refrigeradora industrial', 338.98, 61.02, 400.00, 'PEN',
 '2026-02-05', 'efectivo', NULL, 'https://storage.drinkgo.pe/comprobantes/gas-0008.pdf',
 'pagado', 0, NULL,
 2, 1, 'Cambio de compresor'),

-- Gasto 9: Agua (Pendiente de aprobación)
(9, 1, 1, 'GAS-2026-0009', 11, NULL,
 'Recibo de agua - Febrero 2026', 84.75, 15.25, 100.00, 'PEN',
 '2026-02-15', 'transferencia_bancaria', NULL, NULL,
 'pendiente', 1, 'mensual',
 NULL, 1, 'Aún no llegó el recibo físico'),

-- Gasto 10: Capacitación de personal
(10, 1, 1, 'GAS-2026-0010', 16, NULL,
 'Curso de atención al cliente', 254.24, 45.76, 300.00, 'PEN',
 '2026-02-08', 'transferencia_bancaria', 'CAPACITA-2026-002', 'https://storage.drinkgo.pe/comprobantes/gas-0010.pdf',
 'pagado', 0, NULL,
 2, 1, 'Certificación para 2 empleados'),

-- GASTOS NEGOCIO 2 (Licorería Los Andes) - ENERO-FEBRERO 2026

-- Gasto 11: Alquiler local principal (Pagado)
(11, 2, 2, 'GAS-2026-0011', 20, NULL,
 'Alquiler de local - Enero 2026', 2542.37, 457.63, 3000.00, 'PEN',
 '2026-01-05', 'transferencia_bancaria', 'ALQ-ENERO-2026', 'https://storage.losandes.pe/recibos/alq-ene26.pdf',
 'pagado', 1, 'mensual',
 3, 2, 'Local en zona comercial céntrica'),

-- Gasto 12: Salarios (Pagado)
(12, 2, 2, 'GAS-2026-0012', 21, NULL,
 'Planilla de sueldos - Enero 2026', 8474.58, 1525.42, 10000.00, 'PEN',
 '2026-01-31', 'transferencia_bancaria', 'PLAN-ENE-2026', NULL,
 'pagado', 1, 'mensual',
 3, 2, '7 empleados tiempo completo'),

-- Gasto 13: Luz (Pagado)
(13, 2, 2, 'GAS-2026-0013', 22, NULL,
 'Recibo de luz - Enero 2026', 508.47, 91.53, 600.00, 'PEN',
 '2026-01-12', 'transferencia_bancaria', 'LUZ-ENE-2026', NULL,
 'pagado', 1, 'mensual',
 3, 2, 'Consumo elevado por 4 refrigeradoras'),

-- Gasto 14: Internet + Teléfono (Pagado)
(14, 2, 2, 'GAS-2026-0014', 23, NULL,
 'Internet 200Mbps + líneas telefónicas', 169.49, 30.51, 200.00, 'PEN',
 '2026-01-01', 'tarjeta_credito', 'CLR-2026-01-XXXX', NULL,
 'pagado', 1, 'mensual',
 3, 2, 'Plan corporativo Claro'),

-- Gasto 15: Transporte de mercadería
(15, 2, 2, 'GAS-2026-0015', 17, 2,
 'Flete de mercadería desde Lima', 423.73, 76.27, 500.00, 'PEN',
 '2026-01-25', 'efectivo', NULL, 'https://storage.losandes.pe/comprobantes/flete-0015.pdf',
 'pagado', 0, NULL,
 3, 2, 'Transporte de 500 unidades'),

-- GASTOS NEGOCIO 3 (DrinkGo Express) - ENERO-FEBRERO 2026

-- Gasto 16: Software SaaS - CRM
(16, 3, 3, 'GAS-2026-0016', 28, NULL,
 'Suscripción Salesforce - Enero 2026', 4237.29, 762.71, 5000.00, 'PEN',
 '2026-01-01', 'tarjeta_credito', 'SF-2026-01-XXXX', NULL,
 'pagado', 1, 'mensual',
 4, 3, '10 licencias enterprise'),

-- Gasto 17: Hosting AWS
(17, 3, 3, 'GAS-2026-0017', 29, NULL,
 'Amazon Web Services - Enero 2026', 1694.92, 305.08, 2000.00, 'PEN',
 '2026-01-05', 'tarjeta_credito', 'AWS-2026-01-XXXX', NULL,
 'pagado', 1, 'mensual',
 4, 3, 'Infraestructura cloud'),

-- Gasto 18: Publicidad Facebook + Instagram
(18, 3, 3, 'GAS-2026-0018', 30, NULL,
 'Campaña Meta Business Suite - Enero', 3389.83, 610.17, 4000.00, 'PEN',
 '2026-01-10', 'tarjeta_credito', 'META-2026-01-XXXX', NULL,
 'pagado', 0, NULL,
 4, 3, 'Campaña de lanzamiento nueva sede'),

-- Gasto 19: Salarios (Directorio + personal)
(19, 3, 3, 'GAS-2026-0019', 27, NULL,
 'Planilla de sueldos - Enero 2026', 25423.73, 4576.27, 30000.00, 'PEN',
 '2026-01-31', 'transferencia_bancaria', 'PLAN-ENE-26', NULL,
 'pagado', 1, 'mensual',
 4, 3, '20 empleados + 5 gerentes'),

-- Gasto 20: Licencias Microsoft 365
(20, 3, 3, 'GAS-2026-0020', 28, NULL,
 'Microsoft 365 Business Premium', 847.46, 152.54, 1000.00, 'PEN',
 '2026-02-01', 'tarjeta_credito', 'MS-2026-02-XXXX', NULL,
 'pagado', 1, 'mensual',
 4, 3, '25 licencias'),

-- Gasto 21: Mantenimiento flota de motos (Rechazado por monto excesivo)
(21, 3, 3, 'GAS-2026-0021', 24, NULL,
 'Mantenimiento preventivo de 10 motos', 5084.75, 915.25, 6000.00, 'PEN',
 '2026-02-10', 'transferencia_bancaria', NULL, NULL,
 'rechazado', 0, NULL,
 NULL, 3, 'Monto muy elevado, solicitar nueva cotización'),

-- Gasto 22: Combustible flota delivery (Aprobado)
(22, 3, 3, 'GAS-2026-0022', 24, NULL,
 'Combustible mensual para flota delivery', 2542.37, 457.63, 3000.00, 'PEN',
 '2026-02-01', 'tarjeta_credito', 'PRIMAX-2026-02', NULL,
 'aprobado', 1, 'mensual',
 4, 3, 'Consumo de 10 motos delivery'),

-- Gasto 23: Anulado por duplicidad
(23, 3, 3, 'GAS-2026-0023', 28, NULL,
 'Suscripción duplicada - ERROR', 100.00, 18.00, 118.00, 'PEN',
 '2026-02-15', 'tarjeta_credito', NULL, NULL,
 'anulado', 0, NULL,
 NULL, 3, 'Registro duplicado, se anuló');

-- ============================================================
-- CONSULTAS ÚTILES PARA VERIFICAR LOS DATOS
-- ============================================================

-- Ver todas las categorías con su jerarquía
-- SELECT c1.nombre as categoria_principal, c2.nombre as subcategoria, c2.codigo
-- FROM categorias_gasto c1
-- LEFT JOIN categorias_gasto c2 ON c1.id = c2.padre_id
-- WHERE c1.negocio_id = 1 AND c1.padre_id IS NULL
-- ORDER BY c1.nombre, c2.nombre;

-- Ver gastos por categoría (Negocio 1)
-- SELECT cg.nombre as categoria, COUNT(g.id) as total_gastos, SUM(g.total) as monto_total
-- FROM gastos g
-- JOIN categorias_gasto cg ON g.categoria_id = cg.id
-- WHERE g.negocio_id = 1 AND g.estado = 'pagado'
-- GROUP BY cg.nombre
-- ORDER BY monto_total DESC;

-- Ver gastos recurrentes
-- SELECT numero_gasto, descripcion, total, periodo_recurrencia, estado
-- FROM gastos
-- WHERE es_recurrente = 1
-- ORDER BY fecha_gasto DESC;

-- Resumen de gastos por estado
-- SELECT estado, COUNT(*) as cantidad, SUM(total) as monto_total
-- FROM gastos
-- GROUP BY estado;

-- ============================================================
-- FIN SEEDS BLOQUE 12
-- ============================================================