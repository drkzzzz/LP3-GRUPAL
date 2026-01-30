-- =============================================================================
-- BD_DRINKGO - V17: Módulo de Seguridad Completo
-- Fecha: 2026-01-26
-- Descripción: Roles, permisos granulares, auditoría, sesiones
-- =============================================================================

SET search_path = drinkgo, public;

-- =============================================================================
-- 1. TABLA: modulo_sistema
-- =============================================================================
-- Catálogo de módulos del sistema
CREATE TABLE IF NOT EXISTS modulo_sistema (
  id              BIGSERIAL PRIMARY KEY,
  codigo          VARCHAR(30) UNIQUE NOT NULL,
  nombre          VARCHAR(60) NOT NULL,
  descripcion     VARCHAR(250),
  icono           VARCHAR(50),
  orden           INT NOT NULL DEFAULT 0,
  activo          BOOLEAN NOT NULL DEFAULT TRUE
);

-- OBLIGATORIO: inserts idempotentes para migraciones repetibles.
-- Escenario: el docente re-ejecuta migraciones en laboratorio/CI y no debe fallar por duplicados.
INSERT INTO modulo_sistema (codigo, nombre, descripcion, orden) VALUES
('dashboard', 'Dashboard', 'Panel principal con indicadores', 1),
('pos', 'Punto de Venta', 'Caja y ventas directas', 2),
('pedidos', 'Pedidos', 'Gestión de pedidos (delivery, pickup, mesas)', 3),
('mesas', 'Mesas/Barra', 'Gestión de mesas y atención en local', 4),
('inventario', 'Inventario', 'Control de stock y lotes', 5),
('productos', 'Catálogo', 'Gestión de productos, combos, marcas', 6),
('compras', 'Compras', 'Compras a proveedores y recepciones', 7),
('proveedores', 'Proveedores', 'Gestión de proveedores', 8),
('facturacion', 'Facturación', 'Comprobantes, series, SUNAT', 9),
('devoluciones', 'Devoluciones', 'Devoluciones y notas de crédito', 10),
('clientes', 'Clientes', 'Gestión de clientes y fidelización', 11),
('gastos', 'Gastos', 'Control de gastos y egresos', 12),
('caja', 'Caja', 'Apertura/cierre de caja, arqueos', 13),
('reportes', 'Reportes', 'Reportes y análisis', 14),
('usuarios', 'Usuarios', 'Gestión de usuarios del negocio', 15),
('roles', 'Roles', 'Gestión de roles y permisos', 16),
('configuracion', 'Configuración', 'Configuración general del negocio', 17),
('sedes', 'Sedes', 'Gestión de sedes/sucursales', 18),
('storefront', 'Tienda Online', 'Configuración de e-commerce', 19),
('integraciones', 'Integraciones', 'APIs y servicios externos', 20);

-- OBLIGATORIO: evitar duplicados si el script corre más de una vez.
-- Escenario: reinstalación del esquema sin limpieza previa.
ON CONFLICT (codigo) DO NOTHING;

COMMENT ON TABLE modulo_sistema IS 'Catálogo de módulos del sistema';

-- =============================================================================
-- 2. Expandir tabla PERMISO (creada en V4)
-- =============================================================================
-- Agregar columnas adicionales a la tabla permiso creada en V4
DO $$
BEGIN
  -- Agregar columna modulo_id para relacionar con modulo_sistema
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'permiso' AND column_name = 'modulo_id') THEN
    ALTER TABLE drinkgo.permiso ADD COLUMN modulo_id BIGINT REFERENCES modulo_sistema(id) ON DELETE SET NULL;
  END IF;
  
  -- Agregar columna requiere_mesas si no existe
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'permiso' AND column_name = 'requiere_mesas') THEN
    ALTER TABLE drinkgo.permiso ADD COLUMN requiere_mesas BOOLEAN NOT NULL DEFAULT FALSE;
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_permiso_modulo ON permiso(modulo_id);

-- Permisos completos del sistema
-- OBLIGATORIO: inserts idempotentes para permisos.
-- Escenario: al re-ejecutar migraciones, no debe fallar por permisos ya insertados.
INSERT INTO permiso (modulo_id, codigo, nombre, requiere_mesas) VALUES
-- Dashboard (1)
((SELECT id FROM modulo_sistema WHERE codigo = 'dashboard'), 'dashboard.ver', 'Ver dashboard', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'dashboard'), 'dashboard.ver_ventas', 'Ver indicadores de ventas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'dashboard'), 'dashboard.ver_inventario', 'Ver indicadores de inventario', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'dashboard'), 'dashboard.ver_finanzas', 'Ver indicadores financieros', FALSE),

-- POS (2)
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.acceder', 'Acceder al POS', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.vender', 'Realizar ventas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.aplicar_descuento', 'Aplicar descuentos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.descuento_sin_limite', 'Descuentos sin límite', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.anular_venta', 'Anular ventas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.ver_ventas_otros', 'Ver ventas de otros usuarios', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.reimprimir', 'Reimprimir comprobantes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pos'), 'pos.cobrar', 'Cobrar ventas', FALSE),

-- Pedidos (3)
((SELECT id FROM modulo_sistema WHERE codigo = 'pedidos'), 'pedidos.ver', 'Ver pedidos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pedidos'), 'pedidos.crear', 'Crear pedidos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pedidos'), 'pedidos.editar', 'Editar pedidos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pedidos'), 'pedidos.cancelar', 'Cancelar pedidos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pedidos'), 'pedidos.asignar_repartidor', 'Asignar repartidor', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'pedidos'), 'pedidos.cambiar_estado', 'Cambiar estado de pedidos', FALSE),

-- Mesas (4) - requiere_mesas = TRUE
((SELECT id FROM modulo_sistema WHERE codigo = 'mesas'), 'mesas.ver', 'Ver mesas', TRUE),
((SELECT id FROM modulo_sistema WHERE codigo = 'mesas'), 'mesas.abrir', 'Abrir/ocupar mesa', TRUE),
((SELECT id FROM modulo_sistema WHERE codigo = 'mesas'), 'mesas.cerrar', 'Cerrar mesa', TRUE),
((SELECT id FROM modulo_sistema WHERE codigo = 'mesas'), 'mesas.transferir', 'Transferir mesa', TRUE),
((SELECT id FROM modulo_sistema WHERE codigo = 'mesas'), 'mesas.tomar_pedido', 'Tomar pedido en mesa', TRUE),
((SELECT id FROM modulo_sistema WHERE codigo = 'mesas'), 'mesas.administrar', 'Administrar configuración de mesas', TRUE),

-- Inventario (5)
((SELECT id FROM modulo_sistema WHERE codigo = 'inventario'), 'inventario.ver', 'Ver inventario', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'inventario'), 'inventario.ver_costos', 'Ver costos de productos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'inventario'), 'inventario.ajustar', 'Realizar ajustes de inventario', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'inventario'), 'inventario.transferir', 'Transferir entre almacenes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'inventario'), 'inventario.ver_lotes', 'Ver detalle de lotes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'inventario'), 'inventario.registrar_merma', 'Registrar mermas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'inventario'), 'inventario.kardex', 'Ver kardex', FALSE),

-- Productos (6)
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.ver', 'Ver productos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.crear', 'Crear productos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.editar', 'Editar productos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.eliminar', 'Eliminar productos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.ver_precios', 'Ver precios de compra', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.editar_precios', 'Editar precios', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.gestionar_categorias', 'Gestionar categorías', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.gestionar_marcas', 'Gestionar marcas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'productos'), 'productos.gestionar_combos', 'Gestionar combos', FALSE),

-- Compras (7)
((SELECT id FROM modulo_sistema WHERE codigo = 'compras'), 'compras.ver', 'Ver compras', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'compras'), 'compras.crear_orden', 'Crear orden de compra', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'compras'), 'compras.aprobar_orden', 'Aprobar orden de compra', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'compras'), 'compras.recibir', 'Recibir mercadería', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'compras'), 'compras.anular', 'Anular compras', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'compras'), 'compras.ver_costos', 'Ver costos de compra', FALSE),

-- Proveedores (8)
((SELECT id FROM modulo_sistema WHERE codigo = 'proveedores'), 'proveedores.ver', 'Ver proveedores', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'proveedores'), 'proveedores.crear', 'Crear proveedores', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'proveedores'), 'proveedores.editar', 'Editar proveedores', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'proveedores'), 'proveedores.eliminar', 'Eliminar proveedores', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'proveedores'), 'proveedores.ver_cuentas', 'Ver cuentas por pagar', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'proveedores'), 'proveedores.registrar_pago', 'Registrar pagos', FALSE),

-- Facturación (9)
((SELECT id FROM modulo_sistema WHERE codigo = 'facturacion'), 'facturacion.ver', 'Ver comprobantes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'facturacion'), 'facturacion.emitir', 'Emitir comprobantes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'facturacion'), 'facturacion.anular', 'Anular comprobantes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'facturacion'), 'facturacion.enviar_sunat', 'Enviar a SUNAT', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'facturacion'), 'facturacion.gestionar_series', 'Gestionar series', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'facturacion'), 'facturacion.ver_reportes', 'Ver reportes de facturación', FALSE),

-- Devoluciones (10)
((SELECT id FROM modulo_sistema WHERE codigo = 'devoluciones'), 'devoluciones.ver', 'Ver devoluciones', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'devoluciones'), 'devoluciones.crear', 'Crear devolución', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'devoluciones'), 'devoluciones.aprobar', 'Aprobar devoluciones', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'devoluciones'), 'devoluciones.procesar', 'Procesar devoluciones', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'devoluciones'), 'devoluciones.reembolsar', 'Realizar reembolsos', FALSE),

-- Clientes (11)
((SELECT id FROM modulo_sistema WHERE codigo = 'clientes'), 'clientes.ver', 'Ver clientes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'clientes'), 'clientes.crear', 'Crear clientes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'clientes'), 'clientes.editar', 'Editar clientes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'clientes'), 'clientes.eliminar', 'Eliminar clientes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'clientes'), 'clientes.ver_historial', 'Ver historial de compras', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'clientes'), 'clientes.ver_cuentas', 'Ver cuentas por cobrar', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'clientes'), 'clientes.registrar_cobro', 'Registrar cobros', FALSE),

-- Gastos (12)
((SELECT id FROM modulo_sistema WHERE codigo = 'gastos'), 'gastos.ver', 'Ver gastos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'gastos'), 'gastos.crear', 'Registrar gastos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'gastos'), 'gastos.editar', 'Editar gastos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'gastos'), 'gastos.aprobar', 'Aprobar gastos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'gastos'), 'gastos.anular', 'Anular gastos', FALSE),

-- Caja (13)
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.ver', 'Ver cajas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.abrir', 'Abrir caja', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.cerrar', 'Cerrar caja', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.arqueo', 'Realizar arqueo', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.retiro', 'Realizar retiros', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.ingreso', 'Registrar ingresos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.ver_otros', 'Ver cajas de otros usuarios', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'caja'), 'caja.anular_movimiento', 'Anular movimientos', FALSE),

-- Reportes (14)
((SELECT id FROM modulo_sistema WHERE codigo = 'reportes'), 'reportes.ventas', 'Ver reportes de ventas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'reportes'), 'reportes.inventario', 'Ver reportes de inventario', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'reportes'), 'reportes.compras', 'Ver reportes de compras', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'reportes'), 'reportes.gastos', 'Ver reportes de gastos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'reportes'), 'reportes.clientes', 'Ver reportes de clientes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'reportes'), 'reportes.financieros', 'Ver reportes financieros', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'reportes'), 'reportes.exportar', 'Exportar reportes', FALSE),

-- Usuarios (15)
((SELECT id FROM modulo_sistema WHERE codigo = 'usuarios'), 'usuarios.ver', 'Ver usuarios', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'usuarios'), 'usuarios.crear', 'Crear usuarios', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'usuarios'), 'usuarios.editar', 'Editar usuarios', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'usuarios'), 'usuarios.desactivar', 'Desactivar usuarios', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'usuarios'), 'usuarios.resetear_password', 'Resetear contraseñas', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'usuarios'), 'usuarios.asignar_rol', 'Asignar roles', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'usuarios'), 'usuarios.ver_actividad', 'Ver actividad de usuarios', FALSE),

-- Roles (16)
((SELECT id FROM modulo_sistema WHERE codigo = 'roles'), 'roles.ver', 'Ver roles', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'roles'), 'roles.crear', 'Crear roles', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'roles'), 'roles.editar', 'Editar roles', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'roles'), 'roles.eliminar', 'Eliminar roles', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'roles'), 'roles.asignar_permisos', 'Asignar permisos', FALSE),

-- Configuración (17)
((SELECT id FROM modulo_sistema WHERE codigo = 'configuracion'), 'config.ver', 'Ver configuración', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'configuracion'), 'config.editar', 'Editar configuración', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'configuracion'), 'config.datos_empresa', 'Editar datos de empresa', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'configuracion'), 'config.impuestos', 'Configurar impuestos', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'configuracion'), 'config.metodos_pago', 'Gestionar métodos de pago', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'configuracion'), 'config.impresoras', 'Configurar impresoras', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'configuracion'), 'config.notificaciones', 'Configurar notificaciones', FALSE),

-- Sedes (18)
((SELECT id FROM modulo_sistema WHERE codigo = 'sedes'), 'sedes.ver', 'Ver sedes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'sedes'), 'sedes.crear', 'Crear sedes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'sedes'), 'sedes.editar', 'Editar sedes', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'sedes'), 'sedes.desactivar', 'Desactivar sedes', FALSE),

-- Storefront (19)
((SELECT id FROM modulo_sistema WHERE codigo = 'storefront'), 'storefront.ver', 'Ver tienda online', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'storefront'), 'storefront.configurar', 'Configurar tienda online', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'storefront'), 'storefront.productos', 'Gestionar productos publicados', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'storefront'), 'storefront.banners', 'Gestionar banners', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'storefront'), 'storefront.promociones', 'Gestionar promociones online', FALSE),

-- Integraciones (20)
((SELECT id FROM modulo_sistema WHERE codigo = 'integraciones'), 'integraciones.ver', 'Ver integraciones', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'integraciones'), 'integraciones.configurar', 'Configurar integraciones', FALSE),
((SELECT id FROM modulo_sistema WHERE codigo = 'integraciones'), 'integraciones.api_keys', 'Gestionar API keys', FALSE)

ON CONFLICT (codigo) DO NOTHING;

COMMENT ON TABLE permiso IS 'Permisos granulares del sistema por módulo';

-- =============================================================================
-- 3. Mejorar tabla ROL si existe
-- =============================================================================
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'rol' AND column_name = 'es_predefinido') THEN
    ALTER TABLE drinkgo.rol ADD COLUMN es_predefinido BOOLEAN NOT NULL DEFAULT FALSE;
  END IF;
  
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                 WHERE table_schema = 'drinkgo' AND table_name = 'rol' AND column_name = 'nivel_jerarquia') THEN
    ALTER TABLE drinkgo.rol ADD COLUMN nivel_jerarquia INT NOT NULL DEFAULT 0;
  END IF;

  -- OBLIGATORIO: roles multi-tenant (evita que roles de una licorería afecten a otra).
  -- Escenario: Admin crea rol "Repartidor" solo dentro de su licorería.
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                 WHERE table_schema = 'drinkgo' AND table_name = 'rol' AND column_name = 'tenant_id') THEN
    ALTER TABLE drinkgo.rol ADD COLUMN tenant_id BIGINT REFERENCES negocio(id) ON DELETE CASCADE;
  END IF;

  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_schema = 'drinkgo' AND table_name = 'rol' AND column_name = 'tenant_id')
     AND NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_rol_tenant_id_id') THEN
    ALTER TABLE drinkgo.rol ADD CONSTRAINT uq_rol_tenant_id_id UNIQUE (tenant_id, id);
  END IF;

  -- OBLIGATORIO: evitar nombres de rol duplicados dentro del tenant.
  -- Escenario: Admin no debe crear 2 veces el rol "Cajero" en la misma licorería.
  IF EXISTS (SELECT 1 FROM information_schema.columns
             WHERE table_schema = 'drinkgo' AND table_name = 'rol' AND column_name = 'tenant_id')
     AND EXISTS (SELECT 1 FROM information_schema.columns
                 WHERE table_schema = 'drinkgo' AND table_name = 'rol' AND column_name = 'nombre')
     AND NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_rol_tenant_nombre') THEN
    ALTER TABLE drinkgo.rol ADD CONSTRAINT uq_rol_tenant_nombre UNIQUE (tenant_id, nombre);
  END IF;
END $$;

-- =============================================================================
-- 4. EXPANDIR TABLA: rol_permiso (creada en V4 con estructura básica)
-- =============================================================================
-- V4 crea: rol_permiso(rol_id, permiso_id) con PRIMARY KEY compuesta
-- V17 agrega: id, tenant_id, creado_en para multi-tenant

DO $$
BEGIN
  -- Agregar columna id si no existe (V4 usa PK compuesta, no id)
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'rol_permiso' AND column_name = 'id'
  ) THEN
    ALTER TABLE drinkgo.rol_permiso ADD COLUMN id BIGSERIAL;
  END IF;
  
  -- Agregar columna tenant_id si no existe
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'rol_permiso' AND column_name = 'tenant_id'
  ) THEN
    ALTER TABLE drinkgo.rol_permiso ADD COLUMN tenant_id BIGINT;
  END IF;
  
  -- Agregar columna creado_en si no existe
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'rol_permiso' AND column_name = 'creado_en'
  ) THEN
    ALTER TABLE drinkgo.rol_permiso ADD COLUMN creado_en TIMESTAMPTZ NOT NULL DEFAULT NOW();
  END IF;

  -- Poblar tenant_id desde rol
  UPDATE drinkgo.rol_permiso rp
  SET tenant_id = r.tenant_id
  FROM drinkgo.rol r
  WHERE rp.tenant_id IS NULL
    AND r.id = rp.rol_id;

  -- Hacer tenant_id NOT NULL si todos los registros lo tienen
  IF NOT EXISTS (SELECT 1 FROM drinkgo.rol_permiso WHERE tenant_id IS NULL) THEN
    BEGIN
      ALTER TABLE drinkgo.rol_permiso ALTER COLUMN tenant_id SET NOT NULL;
    EXCEPTION WHEN OTHERS THEN
      NULL; -- Ya es NOT NULL
    END;
  END IF;

  -- Agregar FK compuesta si no existe
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_rol_permiso_tenant_rol'
  ) THEN
    ALTER TABLE drinkgo.rol_permiso
    ADD CONSTRAINT fk_rol_permiso_tenant_rol
    FOREIGN KEY (tenant_id, rol_id)
    REFERENCES drinkgo.rol(tenant_id, id)
    ON DELETE CASCADE;
  END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_rol_permiso_rol ON rol_permiso(rol_id);
CREATE INDEX IF NOT EXISTS idx_rol_permiso_permiso ON rol_permiso(permiso_id);

-- OBLIGATORIO: índices por tenant para evaluación rápida de permisos.
-- Escenario: en login y autorización, la app verifica permisos cientos de veces por minuto.
CREATE INDEX IF NOT EXISTS idx_rol_permiso_tenant_rol ON drinkgo.rol_permiso(tenant_id, rol_id);

COMMENT ON TABLE rol_permiso IS 'Relación de permisos por rol (expandida con multi-tenant)';

-- =============================================================================
-- 5. TABLA: usuario_permiso_especial
-- =============================================================================
-- Permisos especiales asignados directamente al usuario (excepciones)
CREATE TABLE IF NOT EXISTS usuario_permiso_especial (
  id              BIGSERIAL PRIMARY KEY,
  usuario_id      BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  permiso_id      BIGINT NOT NULL REFERENCES permiso(id) ON DELETE CASCADE,
  tipo            VARCHAR(10) NOT NULL CHECK (tipo IN ('grant', 'deny')),  -- grant=otorgar, deny=denegar
  tenant_id        BIGINT,
  
  motivo          VARCHAR(250),
  otorgado_por_id BIGINT REFERENCES usuario(id),
  fecha_expiracion DATE,  -- NULL = permanente
  
  creado_en       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  
  UNIQUE(usuario_id, permiso_id)
);

-- OBLIGATORIO: tenant_id en permisos especiales y FK compuesta (usuario dentro de su licorería).
-- Escenario: Admin otorga "pos.descuento_sin_limite" temporal a un cajero de su negocio.
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'drinkgo' AND table_name = 'usuario_permiso_especial' AND column_name = 'tenant_id'
  ) THEN
    ALTER TABLE drinkgo.usuario_permiso_especial ADD COLUMN tenant_id BIGINT;
  END IF;

  UPDATE drinkgo.usuario_permiso_especial upe
  SET tenant_id = u.tenant_id
  FROM drinkgo.usuario u
  WHERE upe.tenant_id IS NULL
    AND u.id = upe.usuario_id;

  IF NOT EXISTS (SELECT 1 FROM drinkgo.usuario_permiso_especial WHERE tenant_id IS NULL) THEN
    ALTER TABLE drinkgo.usuario_permiso_especial ALTER COLUMN tenant_id SET NOT NULL;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_usuario_permiso_especial_tenant_usuario'
  ) THEN
    ALTER TABLE drinkgo.usuario_permiso_especial
    ADD CONSTRAINT fk_usuario_permiso_especial_tenant_usuario
    FOREIGN KEY (tenant_id, usuario_id)
    REFERENCES drinkgo.usuario(tenant_id, id)
    ON DELETE CASCADE;
  END IF;
END $$;

CREATE INDEX idx_usuario_permiso_usuario ON usuario_permiso_especial(usuario_id);

-- OBLIGATORIO: índice por tenant para búsquedas de autorizaciones.
-- Escenario: autorización por API en modo multi-tenant con alto volumen.
CREATE INDEX IF NOT EXISTS idx_usuario_permiso_tenant_usuario ON drinkgo.usuario_permiso_especial(tenant_id, usuario_id);

COMMENT ON TABLE usuario_permiso_especial IS 'Permisos especiales por usuario (excepciones al rol)';

-- =============================================================================
-- 6. TABLA: sesion_usuario
-- =============================================================================
-- Registro de sesiones de usuario
CREATE TABLE IF NOT EXISTS sesion_usuario (
  id                  BIGSERIAL PRIMARY KEY,
  usuario_id          BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  tenant_id           BIGINT NOT NULL REFERENCES negocio(id) ON DELETE CASCADE,
  sede_id             BIGINT REFERENCES sede(id) ON DELETE SET NULL,
  
  -- Identificación de sesión
  token_hash          VARCHAR(256) NOT NULL,  -- Hash del token JWT
  refresh_token_hash  VARCHAR(256),
  
  -- Dispositivo
  dispositivo_tipo    VARCHAR(30),  -- 'web', 'mobile', 'tablet', 'pos'
  dispositivo_nombre  VARCHAR(100),
  navegador           VARCHAR(100),
  sistema_operativo   VARCHAR(100),
  
  -- Red
  ip_address          INET,
  ubicacion_aprox     VARCHAR(100),
  
  -- Tiempos
  iniciada_en         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  ultima_actividad    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  expira_en           TIMESTAMPTZ NOT NULL,
  cerrada_en          TIMESTAMPTZ,
  
  -- Estado
  activa              BOOLEAN NOT NULL DEFAULT TRUE,
  cerrada_por         VARCHAR(20) CHECK (cerrada_por IN ('usuario', 'admin', 'timeout', 'otra_sesion', 'seguridad'))
);

-- OBLIGATORIO: asegurar que la sesión pertenezca al usuario dentro del tenant (no cruzar licorerías).
-- Escenario: un token no debe poder asociarse a un usuario de otro negocio por error en backend.
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_sesion_usuario_tenant_usuario') THEN
    ALTER TABLE drinkgo.sesion_usuario
    ADD CONSTRAINT fk_sesion_usuario_tenant_usuario
    FOREIGN KEY (tenant_id, usuario_id)
    REFERENCES drinkgo.usuario(tenant_id, id)
    ON DELETE CASCADE;
  END IF;

  -- OPCIONAL: checks temporales.
  -- Escenario: reportes de seguridad no deben tener sesiones con expiración inválida.
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_sesion_usuario_expira_en') THEN
    ALTER TABLE drinkgo.sesion_usuario
    ADD CONSTRAINT chk_sesion_usuario_expira_en CHECK (expira_en > iniciada_en);
  END IF;
END $$;

CREATE INDEX idx_sesion_usuario ON sesion_usuario(usuario_id);
CREATE INDEX idx_sesion_token ON sesion_usuario(token_hash) WHERE activa = TRUE;
CREATE INDEX idx_sesion_activa ON sesion_usuario(activa, expira_en) WHERE activa = TRUE;

-- OBLIGATORIO: acelerar listados por tenant (panel SuperAdmin/Admin).
-- Escenario: SuperAdmin revisa sesiones activas por licorería.
CREATE INDEX IF NOT EXISTS idx_sesion_tenant_activa ON drinkgo.sesion_usuario(tenant_id, activa, expira_en);

COMMENT ON TABLE sesion_usuario IS 'Sesiones activas e históricas de usuarios';

-- =============================================================================
-- 7. TABLA: auditoria_sistema
-- =============================================================================
-- Log de auditoría de acciones importantes
CREATE TABLE IF NOT EXISTS auditoria_sistema (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT REFERENCES negocio(id) ON DELETE SET NULL,
  usuario_id          BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
  sesion_id           BIGINT REFERENCES sesion_usuario(id) ON DELETE SET NULL,
  
  -- Acción
  modulo              VARCHAR(30) NOT NULL,
  accion              VARCHAR(50) NOT NULL,  -- 'crear', 'editar', 'eliminar', 'login', etc.
  entidad             VARCHAR(50),  -- Tabla afectada
  entidad_id          BIGINT,  -- ID del registro afectado
  
  -- Detalles
  descripcion         VARCHAR(500),
  datos_anteriores    JSONB,  -- Estado anterior (para ediciones)
  datos_nuevos        JSONB,  -- Estado nuevo
  
  -- Contexto
  ip_address          INET,
  user_agent          VARCHAR(500),
  
  -- Resultado
  exitoso             BOOLEAN NOT NULL DEFAULT TRUE,
  mensaje_error       TEXT,

  -- Correlación
  trace_id            UUID,
  
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- OPCIONAL: índice de correlación para investigación de incidentes.
-- Escenario: SuperAdmin investiga una anulación y necesita rastrear toda la cadena de eventos.
CREATE INDEX IF NOT EXISTS idx_auditoria_trace ON drinkgo.auditoria_sistema(trace_id);

CREATE INDEX idx_auditoria_tenant ON auditoria_sistema(tenant_id);
CREATE INDEX idx_auditoria_usuario ON auditoria_sistema(usuario_id);
CREATE INDEX idx_auditoria_fecha ON auditoria_sistema(creado_en);
CREATE INDEX idx_auditoria_entidad ON auditoria_sistema(entidad, entidad_id);
CREATE INDEX idx_auditoria_modulo ON auditoria_sistema(modulo, accion);

COMMENT ON TABLE auditoria_sistema IS 'Log de auditoría de acciones del sistema';

-- =============================================================================
-- 8. TABLA: intento_login
-- =============================================================================
-- Registro de intentos de login (para seguridad)
CREATE TABLE IF NOT EXISTS intento_login (
  id                  BIGSERIAL PRIMARY KEY,
  email               citext NOT NULL,
  tenant_id           BIGINT REFERENCES negocio(id) ON DELETE SET NULL,
  
  exitoso             BOOLEAN NOT NULL,
  ip_address          INET,
  user_agent          VARCHAR(500),
  
  motivo_fallo        VARCHAR(50),  -- 'password_incorrecto', 'usuario_no_existe', 'cuenta_bloqueada', etc.
  
  creado_en           TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_intento_login_email ON intento_login(email);
CREATE INDEX idx_intento_login_fecha ON intento_login(creado_en);
CREATE INDEX idx_intento_login_ip ON intento_login(ip_address);

-- OBLIGATORIO: índice por tenant/fecha para rate-limit y monitoreo por licorería.
-- Escenario: Admin detecta fuerza bruta sobre su negocio.
CREATE INDEX IF NOT EXISTS idx_intento_login_tenant_fecha ON drinkgo.intento_login(tenant_id, creado_en);

COMMENT ON TABLE intento_login IS 'Registro de intentos de login para seguridad';

-- =============================================================================
-- 9. FUNCIÓN: Verificar permiso de usuario
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.tiene_permiso(
  p_usuario_id BIGINT,
  p_codigo_permiso VARCHAR(50)
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_tiene_permiso BOOLEAN := FALSE;
  v_permiso_especial RECORD;
  v_tenant_id BIGINT;
BEGIN
  -- OBLIGATORIO: derivar tenant del usuario (compatibilidad) y evaluar permisos en contexto del tenant.
  -- Escenario: un usuario no debe obtener permisos por registros de otra licorería.
  SELECT tenant_id INTO v_tenant_id
  FROM drinkgo.usuario
  WHERE id = p_usuario_id;

  IF v_tenant_id IS NULL THEN
    RETURN FALSE;
  END IF;

  -- 1. Verificar si tiene permiso especial (grant/deny directo)
  SELECT tipo INTO v_permiso_especial
  FROM drinkgo.usuario_permiso_especial upe
  JOIN drinkgo.permiso p ON upe.permiso_id = p.id
  WHERE upe.usuario_id = p_usuario_id 
    AND upe.tenant_id = v_tenant_id
    AND p.codigo = p_codigo_permiso
    AND (upe.fecha_expiracion IS NULL OR upe.fecha_expiracion >= CURRENT_DATE);
  
  IF FOUND THEN
    RETURN v_permiso_especial.tipo = 'grant';
  END IF;
  
  -- 2. Verificar por roles del usuario
  SELECT EXISTS (
    SELECT 1 
    FROM drinkgo.usuario u
    JOIN drinkgo.rol_permiso rp ON u.rol_id = rp.rol_id
    JOIN drinkgo.permiso p ON rp.permiso_id = p.id
    WHERE u.id = p_usuario_id 
      AND u.tenant_id = v_tenant_id
      AND rp.tenant_id = v_tenant_id
      AND p.codigo = p_codigo_permiso
      AND p.activo = TRUE
  ) INTO v_tiene_permiso;
  
  RETURN v_tiene_permiso;
END $$;

-- OBLIGATORIO: versión tenant-safe explícita.
-- Escenario: endpoints multi-tenant pasan tenant_id y usuario_id; la BD valida consistencia.
CREATE OR REPLACE FUNCTION drinkgo.tiene_permiso(
  p_tenant_id BIGINT,
  p_usuario_id BIGINT,
  p_codigo_permiso VARCHAR(50)
)
RETURNS BOOLEAN LANGUAGE plpgsql AS $$
DECLARE
  v_tiene_permiso BOOLEAN := FALSE;
  v_permiso_especial RECORD;
BEGIN
  -- 1. Permisos especiales
  SELECT tipo INTO v_permiso_especial
  FROM drinkgo.usuario_permiso_especial upe
  JOIN drinkgo.permiso p ON upe.permiso_id = p.id
  WHERE upe.usuario_id = p_usuario_id
    AND upe.tenant_id = p_tenant_id
    AND p.codigo = p_codigo_permiso
    AND (upe.fecha_expiracion IS NULL OR upe.fecha_expiracion >= CURRENT_DATE);

  IF FOUND THEN
    RETURN v_permiso_especial.tipo = 'grant';
  END IF;

  -- 2. Por rol
  SELECT EXISTS (
    SELECT 1
    FROM drinkgo.usuario u
    JOIN drinkgo.rol_permiso rp ON u.rol_id = rp.rol_id
    JOIN drinkgo.permiso p ON rp.permiso_id = p.id
    WHERE u.id = p_usuario_id
      AND u.tenant_id = p_tenant_id
      AND rp.tenant_id = p_tenant_id
      AND p.codigo = p_codigo_permiso
      AND p.activo = TRUE
  ) INTO v_tiene_permiso;

  RETURN v_tiene_permiso;
END $$;

COMMENT ON FUNCTION drinkgo.tiene_permiso IS 'Verifica si un usuario tiene un permiso específico';

-- =============================================================================
-- 10. FUNCIÓN: Obtener todos los permisos de un usuario
-- =============================================================================
CREATE OR REPLACE FUNCTION drinkgo.obtener_permisos_usuario(
  p_usuario_id BIGINT
)
RETURNS TABLE (
  modulo VARCHAR(30),
  codigo_permiso VARCHAR(50),
  nombre_permiso VARCHAR(80),
  origen VARCHAR(20)  -- 'rol' o 'directo'
)
LANGUAGE plpgsql AS $$
DECLARE
  v_tenant_id BIGINT;
BEGIN
  -- OBLIGATORIO: derivar tenant para compatibilidad.
  -- Escenario: panel Admin lista permisos solo de su licorería.
  SELECT tenant_id INTO v_tenant_id
  FROM drinkgo.usuario
  WHERE id = p_usuario_id;

  IF v_tenant_id IS NULL THEN
    RETURN;
  END IF;

  RETURN QUERY
  -- Permisos por rol
  SELECT DISTINCT
    ms.codigo AS modulo,
    p.codigo AS codigo_permiso,
    p.nombre AS nombre_permiso,
    'rol'::VARCHAR(20) AS origen
  FROM drinkgo.usuario u
  JOIN drinkgo.rol_permiso rp ON u.rol_id = rp.rol_id
  JOIN drinkgo.permiso p ON rp.permiso_id = p.id
  JOIN drinkgo.modulo_sistema ms ON p.modulo_id = ms.id
  WHERE u.id = p_usuario_id
    AND u.tenant_id = v_tenant_id
    AND rp.tenant_id = v_tenant_id
    AND p.activo = TRUE
  
  UNION
  
  -- Permisos especiales (grant)
  SELECT DISTINCT
    ms.codigo AS modulo,
    p.codigo AS codigo_permiso,
    p.nombre AS nombre_permiso,
    'directo'::VARCHAR(20) AS origen
  FROM drinkgo.usuario_permiso_especial upe
  JOIN drinkgo.permiso p ON upe.permiso_id = p.id
  JOIN drinkgo.modulo_sistema ms ON p.modulo_id = ms.id
  WHERE upe.usuario_id = p_usuario_id 
    AND upe.tenant_id = v_tenant_id
    AND upe.tipo = 'grant'
    AND (upe.fecha_expiracion IS NULL OR upe.fecha_expiracion >= CURRENT_DATE)
  
  -- Excluir permisos denegados
  EXCEPT
  
  SELECT DISTINCT
    ms.codigo AS modulo,
    p.codigo AS codigo_permiso,
    p.nombre AS nombre_permiso,
    'directo'::VARCHAR(20) AS origen
  FROM drinkgo.usuario_permiso_especial upe
  JOIN drinkgo.permiso p ON upe.permiso_id = p.id
  JOIN drinkgo.modulo_sistema ms ON p.modulo_id = ms.id
  WHERE upe.usuario_id = p_usuario_id 
    AND upe.tenant_id = v_tenant_id
    AND upe.tipo = 'deny'
    AND (upe.fecha_expiracion IS NULL OR upe.fecha_expiracion >= CURRENT_DATE)
  
  ORDER BY modulo, codigo_permiso;
END $$;

-- OBLIGATORIO: versión tenant-safe explícita.
-- Escenario: backend multi-tenant consulta permisos efectivos pasando tenant_id.
CREATE OR REPLACE FUNCTION drinkgo.obtener_permisos_usuario(
  p_tenant_id BIGINT,
  p_usuario_id BIGINT
)
RETURNS TABLE (
  modulo VARCHAR(30),
  codigo_permiso VARCHAR(50),
  nombre_permiso VARCHAR(80),
  origen VARCHAR(20)
)
LANGUAGE plpgsql AS $$
BEGIN
  RETURN QUERY
  SELECT DISTINCT
    ms.codigo AS modulo,
    p.codigo AS codigo_permiso,
    p.nombre AS nombre_permiso,
    'rol'::VARCHAR(20) AS origen
  FROM drinkgo.usuario u
  JOIN drinkgo.rol_permiso rp ON u.rol_id = rp.rol_id
  JOIN drinkgo.permiso p ON rp.permiso_id = p.id
  JOIN drinkgo.modulo_sistema ms ON p.modulo_id = ms.id
  WHERE u.id = p_usuario_id
    AND u.tenant_id = p_tenant_id
    AND rp.tenant_id = p_tenant_id
    AND p.activo = TRUE

  UNION

  SELECT DISTINCT
    ms.codigo AS modulo,
    p.codigo AS codigo_permiso,
    p.nombre AS nombre_permiso,
    'directo'::VARCHAR(20) AS origen
  FROM drinkgo.usuario_permiso_especial upe
  JOIN drinkgo.permiso p ON upe.permiso_id = p.id
  JOIN drinkgo.modulo_sistema ms ON p.modulo_id = ms.id
  WHERE upe.usuario_id = p_usuario_id
    AND upe.tenant_id = p_tenant_id
    AND upe.tipo = 'grant'
    AND (upe.fecha_expiracion IS NULL OR upe.fecha_expiracion >= CURRENT_DATE)

  EXCEPT

  SELECT DISTINCT
    ms.codigo AS modulo,
    p.codigo AS codigo_permiso,
    p.nombre AS nombre_permiso,
    'directo'::VARCHAR(20) AS origen
  FROM drinkgo.usuario_permiso_especial upe
  JOIN drinkgo.permiso p ON upe.permiso_id = p.id
  JOIN drinkgo.modulo_sistema ms ON p.modulo_id = ms.id
  WHERE upe.usuario_id = p_usuario_id
    AND upe.tenant_id = p_tenant_id
    AND upe.tipo = 'deny'
    AND (upe.fecha_expiracion IS NULL OR upe.fecha_expiracion >= CURRENT_DATE)

  ORDER BY modulo, codigo_permiso;
END $$;

COMMENT ON FUNCTION drinkgo.obtener_permisos_usuario IS 'Obtiene todos los permisos efectivos de un usuario';

-- =============================================================================
-- 11. FUNCIÓN: Registrar auditoría
-- =============================================================================
-- OBLIGATORIO: registrar auditoría con más contexto (sesión/ip/user_agent) manteniendo compatibilidad.
-- Escenario: SuperAdmin investiga un incidente y necesita rastrear quién/desde dónde ocurrió.
CREATE OR REPLACE FUNCTION drinkgo.registrar_auditoria(
  p_tenant_id BIGINT,
  p_usuario_id BIGINT,
  p_modulo VARCHAR(30),
  p_accion VARCHAR(50),
  p_entidad VARCHAR(50) DEFAULT NULL,
  p_entidad_id BIGINT DEFAULT NULL,
  p_descripcion VARCHAR(500) DEFAULT NULL,
  p_datos_anteriores JSONB DEFAULT NULL,
  p_datos_nuevos JSONB DEFAULT NULL,
  p_sesion_id BIGINT DEFAULT NULL,
  p_ip_address INET DEFAULT NULL,
  p_user_agent VARCHAR(500) DEFAULT NULL,
  p_trace_id UUID DEFAULT NULL
)
RETURNS BIGINT LANGUAGE plpgsql AS $$
DECLARE
  v_auditoria_id BIGINT;
BEGIN
  INSERT INTO drinkgo.auditoria_sistema (
    tenant_id, usuario_id, sesion_id,
    modulo, accion,
    entidad, entidad_id, descripcion,
    datos_anteriores, datos_nuevos,
    ip_address, user_agent,
    trace_id
  ) VALUES (
    p_tenant_id, p_usuario_id, p_sesion_id,
    p_modulo, p_accion,
    p_entidad, p_entidad_id, p_descripcion,
    p_datos_anteriores, p_datos_nuevos,
    p_ip_address, p_user_agent,
    p_trace_id
  )
  RETURNING id INTO v_auditoria_id;

  RETURN v_auditoria_id;
END $$;

-- OPCIONAL: wrapper de compatibilidad para la firma antigua.
-- Escenario: código existente llama a registrar_auditoria sin parámetros extra.
CREATE OR REPLACE FUNCTION drinkgo.registrar_auditoria(
  p_tenant_id BIGINT,
  p_usuario_id BIGINT,
  p_modulo VARCHAR(30),
  p_accion VARCHAR(50),
  p_entidad VARCHAR(50) DEFAULT NULL,
  p_entidad_id BIGINT DEFAULT NULL,
  p_descripcion VARCHAR(500) DEFAULT NULL,
  p_datos_anteriores JSONB DEFAULT NULL,
  p_datos_nuevos JSONB DEFAULT NULL
)
RETURNS BIGINT LANGUAGE plpgsql AS $$
BEGIN
  RETURN drinkgo.registrar_auditoria(
    p_tenant_id, p_usuario_id, p_modulo, p_accion,
    p_entidad, p_entidad_id, p_descripcion,
    p_datos_anteriores, p_datos_nuevos,
    NULL, NULL, NULL, NULL
  );
END $$;

COMMENT ON FUNCTION drinkgo.registrar_auditoria IS 'Registra una entrada en el log de auditoría';
