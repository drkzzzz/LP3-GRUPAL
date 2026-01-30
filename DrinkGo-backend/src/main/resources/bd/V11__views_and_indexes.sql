-- =============================================================================
-- BD_DRINKGO - V11: Vistas de Reporte y Optimizaciones
-- Fecha: 2026-01-26
-- Descripción: Vistas para reportes, alertas de vencimiento y análisis
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. VISTA: Productos próximos a vencer (CRÍTICA para licorería)
-- =============================================================================
CREATE OR REPLACE VIEW vw_productos_proximos_vencer AS
SELECT 
  li.tenant_id,
  li.sede_id,
  s.nombre AS sede_nombre,
  li.id AS lote_id,
  li.codigo_lote,
  li.producto_id,
  p.codigo AS producto_codigo,
  p.nombre AS producto_nombre,
  p.tipo_producto,
  m.nombre AS marca,
  li.cantidad_disponible,
  li.fecha_vencimiento,
  (li.fecha_vencimiento - CURRENT_DATE)::INT AS dias_para_vencer,
  CASE 
    WHEN li.fecha_vencimiento <= CURRENT_DATE THEN 'VENCIDO'
    WHEN li.fecha_vencimiento <= CURRENT_DATE + 7 THEN 'CRÍTICO'
    WHEN li.fecha_vencimiento <= CURRENT_DATE + 15 THEN 'URGENTE'
    WHEN li.fecha_vencimiento <= CURRENT_DATE + 30 THEN 'ALERTA'
    ELSE 'OK'
  END AS nivel_urgencia,
  li.costo_unitario,
  ROUND(li.cantidad_disponible * li.costo_unitario, 2) AS valor_en_riesgo
FROM drinkgo.lote_inventario li
INNER JOIN drinkgo.producto p ON p.id = li.producto_id
INNER JOIN drinkgo.sede s ON s.id = li.sede_id
LEFT JOIN drinkgo.marca m ON m.id = p.marca_id
WHERE li.estado = 'disponible'
  AND li.cantidad_disponible > 0
  AND li.fecha_vencimiento IS NOT NULL
  AND li.fecha_vencimiento <= CURRENT_DATE + 60
ORDER BY li.fecha_vencimiento ASC, li.cantidad_disponible DESC;

COMMENT ON VIEW vw_productos_proximos_vencer IS 
'Alerta de productos próximos a vencer - CRÍTICO para gestión de perecibles';

-- =============================================================================
-- 2. VISTA: Inventario consolidado con alertas
-- =============================================================================
CREATE OR REPLACE VIEW vw_inventario_alertas AS
SELECT 
  ic.tenant_id,
  ic.sede_id,
  s.nombre AS sede_nombre,
  ic.producto_id,
  p.codigo AS producto_codigo,
  p.nombre AS producto_nombre,
  p.tipo_producto,
  cat.nombre AS categoria,
  m.nombre AS marca,
  ic.stock_total,
  ic.stock_minimo,
  ic.stock_maximo,
  ic.punto_reorden,
  ic.lotes_por_vencer,
  CASE 
    WHEN ic.stock_total <= 0 THEN 'SIN_STOCK'
    WHEN ic.stock_total <= ic.stock_minimo THEN 'BAJO'
    WHEN ic.punto_reorden IS NOT NULL AND ic.stock_total <= ic.punto_reorden THEN 'REORDENAR'
    WHEN ic.stock_maximo IS NOT NULL AND ic.stock_total >= ic.stock_maximo THEN 'EXCESO'
    ELSE 'OK'
  END AS estado_stock,
  CASE 
    WHEN ic.lotes_por_vencer > 0 THEN TRUE
    ELSE FALSE
  END AS tiene_productos_por_vencer
FROM drinkgo.inventario_consolidado ic
INNER JOIN drinkgo.producto p ON p.id = ic.producto_id
INNER JOIN drinkgo.sede s ON s.id = ic.sede_id
LEFT JOIN drinkgo.categoria_producto cat ON cat.id = p.categoria_id
LEFT JOIN drinkgo.marca m ON m.id = p.marca_id
WHERE p.activo = TRUE;

COMMENT ON VIEW vw_inventario_alertas IS 
'Inventario consolidado con alertas de stock bajo y productos por vencer';

-- =============================================================================
-- 3. VISTA: Resumen de ventas diarias por sede
-- =============================================================================
CREATE OR REPLACE VIEW vw_ventas_diarias AS
SELECT 
  v.tenant_id,
  v.sede_id,
  s.nombre AS sede_nombre,
  DATE(v.fecha) AS fecha,
  COUNT(*) FILTER (WHERE v.estado = 'emitida') AS total_ventas,
  COUNT(*) FILTER (WHERE v.estado = 'anulada') AS ventas_anuladas,
  COALESCE(SUM(v.subtotal) FILTER (WHERE v.estado = 'emitida'), 0) AS subtotal,
  COALESCE(SUM(v.impuesto) FILTER (WHERE v.estado = 'emitida'), 0) AS impuesto,
  COALESCE(SUM(v.total) FILTER (WHERE v.estado = 'emitida'), 0) AS total,
  COALESCE(AVG(v.total) FILTER (WHERE v.estado = 'emitida'), 0) AS ticket_promedio
FROM drinkgo.venta v
INNER JOIN drinkgo.sede s ON s.id = v.sede_id
GROUP BY v.tenant_id, v.sede_id, s.nombre, DATE(v.fecha);

COMMENT ON VIEW vw_ventas_diarias IS 
'Resumen de ventas diarias por sede';

-- =============================================================================
-- 4. VISTA: Resumen de pagos por venta
-- =============================================================================
CREATE OR REPLACE VIEW vw_resumen_venta_pago AS
SELECT 
  v.id AS venta_id,
  v.tenant_id,
  v.sede_id,
  v.fecha,
  v.estado,
  v.subtotal,
  v.impuesto,
  v.total,
  COALESCE(SUM(pv.monto) FILTER (WHERE pv.metodo_pago = 'efectivo'), 0) AS efectivo,
  COALESCE(SUM(pv.monto) FILTER (WHERE pv.metodo_pago = 'yape'), 0) AS yape,
  COALESCE(SUM(pv.monto) FILTER (WHERE pv.metodo_pago = 'plin'), 0) AS plin,
  COALESCE(SUM(pv.monto) FILTER (WHERE pv.metodo_pago = 'tarjeta'), 0) AS tarjeta,
  COALESCE(SUM(pv.monto) FILTER (WHERE pv.metodo_pago = 'transferencia'), 0) AS transferencia,
  COALESCE(SUM(pv.monto), 0) AS total_pagado
FROM drinkgo.venta v
LEFT JOIN drinkgo.pago_venta pv ON pv.venta_id = v.id
GROUP BY v.id, v.tenant_id, v.sede_id, v.fecha, v.estado, v.subtotal, v.impuesto, v.total;

COMMENT ON VIEW vw_resumen_venta_pago IS 
'Resumen de ventas con desglose de métodos de pago';

-- =============================================================================
-- 5. VISTA: Top productos más vendidos
-- =============================================================================
CREATE OR REPLACE VIEW vw_productos_top_ventas AS
SELECT 
  p.tenant_id,
  p.id AS producto_id,
  p.codigo,
  p.nombre,
  p.tipo_producto,
  cat.nombre AS categoria,
  m.nombre AS marca,
  SUM(vi.cantidad) AS cantidad_total_vendida,
  SUM(vi.total_item) AS total_vendido,
  COUNT(DISTINCT vi.venta_id) AS num_ventas,
  AVG(vi.precio_unitario) AS precio_promedio
FROM drinkgo.venta_item vi
INNER JOIN drinkgo.producto p ON p.id = vi.producto_id
INNER JOIN drinkgo.venta v ON v.id = vi.venta_id
LEFT JOIN drinkgo.categoria_producto cat ON cat.id = p.categoria_id
LEFT JOIN drinkgo.marca m ON m.id = p.marca_id
WHERE v.estado = 'emitida'
  AND v.fecha >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY p.tenant_id, p.id, p.codigo, p.nombre, p.tipo_producto, cat.nombre, m.nombre
ORDER BY cantidad_total_vendida DESC;

COMMENT ON VIEW vw_productos_top_ventas IS 
'Productos más vendidos en los últimos 30 días';

-- =============================================================================
-- 6. VISTA: Estado de pedidos por modalidad
-- =============================================================================
CREATE OR REPLACE VIEW vw_pedidos_por_modalidad AS
SELECT 
  p.tenant_id,
  p.sede_id,
  p.modalidad,
  p.estado,
  DATE(p.fecha_creacion) AS fecha,
  COUNT(*) AS cantidad,
  COALESCE(SUM(p.total), 0) AS total
FROM drinkgo.pedido p
GROUP BY p.tenant_id, p.sede_id, p.modalidad, p.estado, DATE(p.fecha_creacion);

COMMENT ON VIEW vw_pedidos_por_modalidad IS 
'Resumen de pedidos agrupados por modalidad (delivery, pickup, mesa, barra)';

-- =============================================================================
-- 7. VISTA: Cuentas de mesas activas
-- =============================================================================
CREATE OR REPLACE VIEW vw_cuentas_mesas_activas AS
SELECT 
  cm.tenant_id,
  cm.sede_id,
  s.nombre AS sede_nombre,
  cm.mesa_id,
  m.numero AS mesa_numero,
  cm.id AS cuenta_id,
  cm.atendido_por_id,
  u.nombres || ' ' || COALESCE(u.apellidos, '') AS atendido_por_nombre,
  cm.abierta_en,
  EXTRACT(EPOCH FROM (NOW() - cm.abierta_en)) / 60 AS minutos_abierta,
  cm.estado,
  cm.subtotal,
  cm.descuento,
  cm.total,
  COUNT(cmi.id) AS items_count,
  COUNT(cmi.id) FILTER (WHERE cmi.pagado = TRUE) AS items_pagados,
  COUNT(cmi.id) FILTER (WHERE cmi.pagado = FALSE) AS items_pendientes
FROM drinkgo.cuenta_mesa cm
INNER JOIN drinkgo.mesa m ON m.id = cm.mesa_id
INNER JOIN drinkgo.sede s ON s.id = cm.sede_id
INNER JOIN drinkgo.usuario u ON u.id = cm.atendido_por_id
LEFT JOIN drinkgo.cuenta_mesa_item cmi ON cmi.cuenta_id = cm.id
WHERE cm.estado = 'abierta'
GROUP BY cm.id, s.nombre, m.numero, u.nombres, u.apellidos;

COMMENT ON VIEW vw_cuentas_mesas_activas IS 
'Cuentas de mesas actualmente abiertas con detalle de items pagados/pendientes';

-- =============================================================================
-- 8. VISTA: Movimientos de inventario por tipo
-- =============================================================================
CREATE OR REPLACE VIEW vw_movimientos_inventario AS
SELECT 
  mi.tenant_id,
  mi.sede_id,
  DATE(mi.creado_en) AS fecha,
  mi.tipo_movimiento,
  mi.producto_id,
  p.nombre AS producto_nombre,
  SUM(mi.cantidad) AS cantidad_total,
  COUNT(*) AS num_movimientos
FROM drinkgo.movimiento_inventario mi
INNER JOIN drinkgo.producto p ON p.id = mi.producto_id
GROUP BY mi.tenant_id, mi.sede_id, DATE(mi.creado_en), mi.tipo_movimiento, mi.producto_id, p.nombre;

COMMENT ON VIEW vw_movimientos_inventario IS 
'Resumen de movimientos de inventario por tipo y día';

-- =============================================================================
-- 9. VISTA: Dashboard de negocios (para superadmin)
-- =============================================================================
CREATE OR REPLACE VIEW vw_dashboard_negocios AS
SELECT 
  n.id AS negocio_id,
  n.nombre_comercial,
  n.razon_social,
  n.suscripcion_estado,
  n.suscripcion_fin,
  ps.nombre AS plan_nombre,
  n.has_tables,
  n.has_delivery,
  n.has_storefront,
  COUNT(DISTINCT s.id) AS num_sedes,
  COUNT(DISTINCT u.id) AS num_usuarios,
  (SELECT COUNT(*) FROM drinkgo.venta v WHERE v.tenant_id = n.id AND v.fecha >= CURRENT_DATE - INTERVAL '30 days') AS ventas_30d,
  (SELECT COALESCE(SUM(total), 0) FROM drinkgo.venta v WHERE v.tenant_id = n.id AND v.estado = 'emitida' AND v.fecha >= CURRENT_DATE - INTERVAL '30 days') AS total_ventas_30d
FROM drinkgo.negocio n
INNER JOIN drinkgo.plan_suscripcion ps ON ps.id = n.plan_id
LEFT JOIN drinkgo.sede s ON s.tenant_id = n.id AND s.activo = TRUE
LEFT JOIN drinkgo.usuario u ON u.tenant_id = n.id AND u.activo = TRUE
GROUP BY n.id, n.nombre_comercial, n.razon_social, n.suscripcion_estado, n.suscripcion_fin, ps.nombre, n.has_tables, n.has_delivery, n.has_storefront;

COMMENT ON VIEW vw_dashboard_negocios IS 
'Dashboard para superadmin: estado de todos los negocios SaaS';

-- =============================================================================
-- 10. VISTA: Lotes FIFO disponibles (para depuración)
-- =============================================================================
CREATE OR REPLACE VIEW vw_lotes_fifo AS
SELECT 
  li.tenant_id,
  li.sede_id,
  li.producto_id,
  p.nombre AS producto_nombre,
  li.id AS lote_id,
  li.codigo_lote,
  li.cantidad_disponible,
  li.fecha_vencimiento,
  li.fecha_entrada,
  li.costo_unitario,
  ROW_NUMBER() OVER (
    PARTITION BY li.sede_id, li.producto_id 
    ORDER BY li.fecha_vencimiento ASC NULLS LAST, li.fecha_entrada ASC
  ) AS prioridad_fifo
FROM drinkgo.lote_inventario li
INNER JOIN drinkgo.producto p ON p.id = li.producto_id
WHERE li.estado = 'disponible'
  AND li.cantidad_disponible > 0
ORDER BY li.sede_id, li.producto_id, prioridad_fifo;

COMMENT ON VIEW vw_lotes_fifo IS 
'Lotes ordenados por prioridad FIFO para depuración y auditoría';

-- =============================================================================
-- ÍNDICES ADICIONALES DE OPTIMIZACIÓN
-- =============================================================================

-- Índice para búsqueda de ventas por rango de fechas
CREATE INDEX IF NOT EXISTS idx_venta_fecha_estado 
ON venta(tenant_id, fecha, estado) WHERE estado = 'emitida';

-- Índice para pedidos pendientes
CREATE INDEX IF NOT EXISTS idx_pedido_pendientes 
ON pedido(tenant_id, estado, fecha_creacion) 
WHERE estado NOT IN ('entregado', 'anulado');

-- Índice para búsqueda de clientes por documento
CREATE INDEX IF NOT EXISTS idx_cliente_documento_busqueda
ON cliente(tenant_id, numero_documento) WHERE numero_documento IS NOT NULL;

-- Índice para productos destacados en storefront
CREATE INDEX IF NOT EXISTS idx_producto_destacado
ON producto(tenant_id, destacado, visible_storefront)
WHERE destacado = TRUE AND visible_storefront = TRUE AND activo = TRUE;
