-- =============================================================================
-- BD_DRINKGO - V2: Funciones Utilitarias y Triggers
-- Fecha: 2026-01-26
-- Descripción: Funciones PL/pgSQL para automatización, validaciones y lógica FIFO
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. Función: Actualización automática de timestamps
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.set_updated_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.actualizado_en := NOW();
  RETURN NEW;
END $$;

-- =============================================================================
-- 2. Función: Validar tenant_id en operaciones (Row Level Security Helper)
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.check_tenant_isolation()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  -- En INSERT, asegurar que tenant_id sea del contexto actual
  IF TG_OP = 'INSERT' AND NEW.tenant_id IS NULL THEN
    RAISE EXCEPTION 'tenant_id es requerido para aislamiento de datos multi-tenant';
  END IF;
  
  -- En UPDATE, no permitir cambio de tenant
  IF TG_OP = 'UPDATE' AND OLD.tenant_id <> NEW.tenant_id THEN
    RAISE EXCEPTION 'No se permite cambiar el tenant_id de un registro existente';
  END IF;
  
  RETURN NEW;
END $$;

-- =============================================================================
-- 3. Función: Calcular total de items en ventas
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.calcular_total_item_venta()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  NEW.total_item := ROUND((NEW.cantidad * NEW.precio_unitario) - COALESCE(NEW.descuento, 0), 2);
  
  IF NEW.total_item < 0 THEN
    RAISE EXCEPTION 'El total_item no puede ser negativo. Verifique cantidad, precio y descuento.';
  END IF;
  
  RETURN NEW;
END $$;

-- =============================================================================
-- 4. Función: Recalcular totales de venta
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.recalcular_totales_venta()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  v_venta_id BIGINT := COALESCE(NEW.venta_id, OLD.venta_id);
  v_subtotal NUMERIC(12,2);
  v_impuesto NUMERIC(12,2);
  v_total    NUMERIC(12,2);
  v_pct      NUMERIC(5,2);
BEGIN
  SELECT COALESCE(ROUND(SUM(total_item), 2), 0) INTO v_subtotal
  FROM drinkgo.venta_item 
  WHERE venta_id = v_venta_id;

  SELECT impuesto_porcentaje INTO v_pct
  FROM drinkgo.venta 
  WHERE id = v_venta_id;

  v_impuesto := ROUND(v_subtotal * (COALESCE(v_pct, 0) / 100.0), 2);
  v_total    := ROUND(v_subtotal + v_impuesto, 2);

  UPDATE drinkgo.venta
    SET subtotal = v_subtotal,
        impuesto = v_impuesto,
        total = v_total,
        actualizado_en = NOW()
  WHERE id = v_venta_id;

  RETURN NULL;
END $$;

-- =============================================================================
-- 5. Función: Validar cambios de estado en pedidos (Máquina de Estados)
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.validar_cambio_estado_pedido()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
  e_old TEXT := COALESCE(OLD.estado::TEXT, '');
  e_new TEXT := NEW.estado::TEXT;
  valido BOOLEAN := FALSE;
BEGIN
  IF TG_OP = 'INSERT' THEN
    IF NEW.estado <> 'pendiente'::drinkgo.order_status THEN
      RAISE EXCEPTION 'El estado inicial de un pedido debe ser "pendiente"';
    END IF;
    RETURN NEW;
  END IF;

  -- Validar transiciones permitidas
  IF e_old = 'pendiente' AND e_new IN ('en_preparacion', 'anulado') THEN
    valido := TRUE;
  ELSIF e_old = 'en_preparacion' AND e_new IN ('listo', 'anulado') THEN
    valido := TRUE;
  ELSIF e_old = 'listo' AND e_new IN ('entregado', 'anulado') THEN
    valido := TRUE;
  ELSIF e_old = 'entregado' AND e_new = 'entregado' THEN
    valido := TRUE;
  ELSIF e_old = 'anulado' AND e_new = 'anulado' THEN
    valido := TRUE;
  END IF;

  IF NOT valido THEN
    RAISE EXCEPTION 'Transición de estado no permitida: % → %', e_old, e_new;
  END IF;

  RETURN NEW;
END $$;

-- =============================================================================
-- 6. Función CRÍTICA: Selección de lote FIFO para descuento de inventario
-- =============================================================================
-- Retorna los lotes a descontar siguiendo FIFO (First In, First Out)
-- Prioriza: 1) Fecha de vencimiento más cercana, 2) Fecha de entrada más antigua
CREATE OR REPLACE FUNCTION drinkgo.seleccionar_lotes_fifo(
  p_producto_id BIGINT,
  p_sede_id BIGINT,
  p_cantidad_requerida NUMERIC(12,3)
)
RETURNS TABLE (
  lote_id BIGINT,
  cantidad_a_descontar NUMERIC(12,3),
  fecha_vencimiento DATE
) LANGUAGE plpgsql AS $$
DECLARE
  v_cantidad_pendiente NUMERIC(12,3) := p_cantidad_requerida;
  v_lote RECORD;
BEGIN
  -- Iterar sobre lotes disponibles ordenados por FIFO
  FOR v_lote IN 
    SELECT 
      li.id,
      li.cantidad_disponible,
      li.fecha_vencimiento
    FROM drinkgo.lote_inventario li
    WHERE li.producto_id = p_producto_id
      AND li.sede_id = p_sede_id
      AND li.estado = 'disponible'::drinkgo.batch_status
      AND li.cantidad_disponible > 0
      AND (li.fecha_vencimiento IS NULL OR li.fecha_vencimiento > CURRENT_DATE)
    ORDER BY 
      li.fecha_vencimiento ASC NULLS LAST,  -- Próximos a vencer primero
      li.fecha_entrada ASC                   -- Más antiguos primero
  LOOP
    EXIT WHEN v_cantidad_pendiente <= 0;
    
    lote_id := v_lote.id;
    fecha_vencimiento := v_lote.fecha_vencimiento;
    
    IF v_lote.cantidad_disponible >= v_cantidad_pendiente THEN
      cantidad_a_descontar := v_cantidad_pendiente;
      v_cantidad_pendiente := 0;
    ELSE
      cantidad_a_descontar := v_lote.cantidad_disponible;
      v_cantidad_pendiente := v_cantidad_pendiente - v_lote.cantidad_disponible;
    END IF;
    
    RETURN NEXT;
  END LOOP;
  
  -- Si aún hay cantidad pendiente, no hay suficiente stock
  IF v_cantidad_pendiente > 0 THEN
    RAISE EXCEPTION 'Stock insuficiente para producto_id=%. Faltan % unidades.',
      p_producto_id, v_cantidad_pendiente;
  END IF;
END $$;

COMMENT ON FUNCTION drinkgo.seleccionar_lotes_fifo IS 
'Selecciona lotes siguiendo FIFO: primero los próximos a vencer, luego los más antiguos';

-- =============================================================================
-- 7. Función: Descontar inventario con FIFO automático
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.descontar_inventario_fifo(
  p_producto_id BIGINT,
  p_sede_id BIGINT,
  p_cantidad NUMERIC(12,3),
  p_referencia_tipo VARCHAR(50),  -- 'venta', 'pedido', 'merma', 'ajuste'
  p_referencia_id BIGINT
)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE
  v_lote RECORD;
BEGIN
  -- Descontar de cada lote seleccionado por FIFO
  FOR v_lote IN SELECT * FROM drinkgo.seleccionar_lotes_fifo(p_producto_id, p_sede_id, p_cantidad)
  LOOP
    -- Actualizar cantidad disponible del lote
    UPDATE drinkgo.lote_inventario
    SET cantidad_disponible = cantidad_disponible - v_lote.cantidad_a_descontar,
        actualizado_en = NOW()
    WHERE id = v_lote.lote_id;
    
    -- Marcar como agotado si corresponde
    UPDATE drinkgo.lote_inventario
    SET estado = 'agotado'::drinkgo.batch_status
    WHERE id = v_lote.lote_id AND cantidad_disponible <= 0;
    
    -- Registrar movimiento de inventario
    INSERT INTO drinkgo.movimiento_inventario (
      tenant_id, lote_id, producto_id, sede_id, tipo_movimiento,
      cantidad, referencia_tipo, referencia_id
    )
    SELECT 
      li.tenant_id, v_lote.lote_id, p_producto_id, p_sede_id, 'salida',
      v_lote.cantidad_a_descontar, p_referencia_tipo, p_referencia_id
    FROM drinkgo.lote_inventario li WHERE li.id = v_lote.lote_id;
  END LOOP;
END $$;

-- =============================================================================
-- 8. Función: Verificar productos próximos a vencer
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.obtener_productos_proximos_vencer(
  p_tenant_id BIGINT,
  p_sede_id BIGINT DEFAULT NULL,
  p_dias_alerta INT DEFAULT 30
)
RETURNS TABLE (
  lote_id BIGINT,
  producto_id BIGINT,
  producto_nombre VARCHAR,
  sede_id BIGINT,
  sede_nombre VARCHAR,
  cantidad_disponible NUMERIC,
  fecha_vencimiento DATE,
  dias_restantes INT,
  nivel_urgencia VARCHAR
) LANGUAGE plpgsql AS $$
BEGIN
  RETURN QUERY
  SELECT 
    li.id AS lote_id,
    li.producto_id,
    p.nombre AS producto_nombre,
    li.sede_id,
    s.nombre AS sede_nombre,
    li.cantidad_disponible,
    li.fecha_vencimiento,
    (li.fecha_vencimiento - CURRENT_DATE)::INT AS dias_restantes,
    CASE 
      WHEN li.fecha_vencimiento <= CURRENT_DATE THEN 'VENCIDO'
      WHEN li.fecha_vencimiento <= CURRENT_DATE + 7 THEN 'CRÍTICO'
      WHEN li.fecha_vencimiento <= CURRENT_DATE + 15 THEN 'URGENTE'
      ELSE 'ALERTA'
    END AS nivel_urgencia
  FROM drinkgo.lote_inventario li
  INNER JOIN drinkgo.producto p ON p.id = li.producto_id
  INNER JOIN drinkgo.sede s ON s.id = li.sede_id
  WHERE li.tenant_id = p_tenant_id
    AND (p_sede_id IS NULL OR li.sede_id = p_sede_id)
    AND li.estado = 'disponible'::drinkgo.batch_status
    AND li.cantidad_disponible > 0
    AND li.fecha_vencimiento IS NOT NULL
    AND li.fecha_vencimiento <= CURRENT_DATE + p_dias_alerta
  ORDER BY li.fecha_vencimiento ASC, li.cantidad_disponible DESC;
END $$;

COMMENT ON FUNCTION drinkgo.obtener_productos_proximos_vencer IS 
'Lista productos próximos a vencer para gestión proactiva de inventario perecible';

-- =============================================================================
-- 9. Función: Descontar componentes de combo/pack atómicamente
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.descontar_combo_inventario(
  p_combo_id BIGINT,
  p_sede_id BIGINT,
  p_cantidad_combos INT,
  p_referencia_tipo VARCHAR(50),
  p_referencia_id BIGINT
)
RETURNS void LANGUAGE plpgsql AS $$
DECLARE
  v_componente RECORD;
  v_cantidad_total NUMERIC(12,3);
BEGIN
  -- Verificar stock de todos los componentes primero (transacción atómica)
  FOR v_componente IN 
    SELECT ci.producto_id, ci.cantidad, p.nombre
    FROM drinkgo.combo_item ci
    INNER JOIN drinkgo.producto p ON p.id = ci.producto_id
    WHERE ci.combo_id = p_combo_id
  LOOP
    v_cantidad_total := v_componente.cantidad * p_cantidad_combos;
    
    -- Verificar disponibilidad
    IF NOT EXISTS (
      SELECT 1 FROM drinkgo.lote_inventario li
      WHERE li.producto_id = v_componente.producto_id
        AND li.sede_id = p_sede_id
        AND li.estado = 'disponible'::drinkgo.batch_status
        AND li.cantidad_disponible > 0
      HAVING SUM(li.cantidad_disponible) >= v_cantidad_total
    ) THEN
      RAISE EXCEPTION 'Stock insuficiente para componente "%" del combo. Se requieren % unidades.',
        v_componente.nombre, v_cantidad_total;
    END IF;
  END LOOP;
  
  -- Descontar cada componente
  FOR v_componente IN 
    SELECT producto_id, cantidad
    FROM drinkgo.combo_item
    WHERE combo_id = p_combo_id
  LOOP
    v_cantidad_total := v_componente.cantidad * p_cantidad_combos;
    
    PERFORM drinkgo.descontar_inventario_fifo(
      v_componente.producto_id,
      p_sede_id,
      v_cantidad_total,
      p_referencia_tipo,
      p_referencia_id
    );
  END LOOP;
END $$;

COMMENT ON FUNCTION drinkgo.descontar_combo_inventario IS 
'Descuenta todos los componentes de un combo/pack atómicamente usando FIFO';

-- =============================================================================
-- 10. Función: Validar horario de venta de alcohol
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.validar_horario_venta_alcohol(
  p_sede_id BIGINT,
  p_hora_actual TIME DEFAULT CURRENT_TIME
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_config RECORD;
  v_dia_semana INT := EXTRACT(DOW FROM CURRENT_DATE)::INT; -- 0=Domingo
BEGIN
  -- Obtener configuración de horario de la sede
  SELECT 
    hora_inicio_venta_alcohol,
    hora_fin_venta_alcohol,
    dias_sin_venta_alcohol,
    ley_seca_activa
  INTO v_config
  FROM drinkgo.sede_config
  WHERE sede_id = p_sede_id;
  
  -- Si no hay config, permitir venta
  IF NOT FOUND THEN
    RETURN TRUE;
  END IF;
  
  -- Verificar ley seca activa
  IF v_config.ley_seca_activa THEN
    RETURN FALSE;
  END IF;
  
  -- Verificar días sin venta
  IF v_config.dias_sin_venta_alcohol IS NOT NULL 
     AND v_dia_semana = ANY(v_config.dias_sin_venta_alcohol) THEN
    RETURN FALSE;
  END IF;
  
  -- Verificar horario permitido
  IF v_config.hora_inicio_venta_alcohol IS NOT NULL 
     AND v_config.hora_fin_venta_alcohol IS NOT NULL THEN
    
    -- Manejar horarios que cruzan medianoche
    IF v_config.hora_inicio_venta_alcohol <= v_config.hora_fin_venta_alcohol THEN
      -- Horario normal (ej: 08:00 a 23:00)
      RETURN p_hora_actual >= v_config.hora_inicio_venta_alcohol 
         AND p_hora_actual <= v_config.hora_fin_venta_alcohol;
    ELSE
      -- Horario cruzando medianoche (ej: 18:00 a 02:00)
      RETURN p_hora_actual >= v_config.hora_inicio_venta_alcohol 
          OR p_hora_actual <= v_config.hora_fin_venta_alcohol;
    END IF;
  END IF;
  
  RETURN TRUE;
END $$;

COMMENT ON FUNCTION drinkgo.validar_horario_venta_alcohol IS 
'Valida si la venta de alcohol está permitida según configuración de sede y horarios legales';

-- =============================================================================
-- 11. Función: Automarcar lotes vencidos (para job programado)
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.marcar_lotes_vencidos()
RETURNS INT LANGUAGE plpgsql AS $$
DECLARE
  v_count INT;
BEGIN
  UPDATE drinkgo.lote_inventario
  SET estado = 'vencido'::drinkgo.batch_status,
      actualizado_en = NOW()
  WHERE fecha_vencimiento < CURRENT_DATE
    AND estado = 'disponible'::drinkgo.batch_status;
  
  GET DIAGNOSTICS v_count = ROW_COUNT;
  RETURN v_count;
END $$;

COMMENT ON FUNCTION drinkgo.marcar_lotes_vencidos IS 
'Job diario: marca automáticamente como vencidos los lotes con fecha pasada';
