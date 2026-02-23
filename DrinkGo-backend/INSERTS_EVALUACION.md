# INSERTS PARA EVALUACIÓN - DRINKGO

## ⚠️ IMPORTANTE: Lee esto antes de ejecutar

### Configuración Previa

Tu `application.properties` tiene `ddl-auto=create`, lo que significa que **cada vez que inicias el backend**, Hibernate **BORRA y RECREA todas las tablas**. Para evitar perder los datos:

1. **Inicia el backend** (se crean las tablas)
2. **Ejecuta los INSERTs** en phpMyAdmin (`http://localhost/phpmyadmin` → BD `drinkgo_db` → pestaña SQL)
3. **Registra un usuario** en Postman (ver Paso 1)
4. **Genera el token** en Postman (ver Paso 2)
5. **Prueba los endpoints** con el token

> **SUGERENCIA:** Después de verificar que todo funciona, cambia `ddl-auto=create` a `ddl-auto=update` en `application.properties` para no perder datos al reiniciar.

---

## 🔑 PASO 1: Registrar Usuario en Postman

**POST** `http://localhost:8080/restful/registros`

```json
{
  "nombres": "Admin",
  "apellidos": "DrinkGo",
  "email": "admin@drinkgo.com"
}
```

> Copia el `cliente_id` que aparezca en la respuesta o al hacer GET `/restful/registros`

---

## 🔑 PASO 2: Generar Token JWT

**POST** `http://localhost:8080/restful/token`

```json
{
  "cliente_id": "PEGA_AQUI_EL_CLIENTE_ID",
  "llave_secreta": "admin@drinkgo.comAdminDrinkGo"
}
```

> La contraseña siempre es: `email + nombres + apellidos` (sin espacios entre campos)

---

## 🔑 PASO 3: Usar el Token

En cualquier endpoint, agregar el Header:

```
Authorization: Bearer PEGA_AQUI_TU_TOKEN
```

---

---

# 📦 INSERTS SQL (Ejecutar en phpMyAdmin en ORDEN)

---

## BLOQUE 1: PLANES DE SUSCRIPCIÓN

```sql
-- ============================================
-- 1. PLANES DE SUSCRIPCIÓN
-- ============================================
INSERT INTO planes_suscripcion (
    id, nombre, descripcion, precio, moneda, periodo_facturacion,
    max_sedes, max_usuarios, max_productos, max_almacenes_por_sede,
    permite_pos, permite_tienda_online, permite_delivery, permite_mesas,
    permite_facturacion_electronica, permite_multi_almacen,
    permite_reportes_avanzados, permite_acceso_api,
    esta_activo, orden, creado_en, actualizado_en
) VALUES
(1, 'Plan Emprendedor', 'Ideal para licorerías pequeñas', 49.90, 'PEN', 'mensual',
 1, 3, 200, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, NOW(), NOW()),

(2, 'Plan Negocio', 'Para licorerías en crecimiento', 99.90, 'PEN', 'mensual',
 3, 10, 1000, 2, 1, 1, 1, 0, 1, 1, 0, 0, 1, 2, NOW(), NOW()),

(3, 'Plan Enterprise', 'Para cadenas de licorerías', 249.90, 'PEN', 'mensual',
 10, 50, 10000, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, NOW(), NOW());
```

---

## BLOQUE 2: NEGOCIOS

```sql
-- ============================================
-- 2. NEGOCIOS (TENANT PRINCIPAL)
-- ============================================
INSERT INTO negocios (
    id, uuid, razon_social, nombre_comercial, ruc,
    tipo_documento_fiscal, representante_legal, documento_representante,
    tipo_negocio, email, telefono, direccion, ciudad, departamento,
    pais, estado, esta_activo, creado_en, actualizado_en
) VALUES
(1, 'a1b2c3d4-e5f6-7890-abcd-000000000001',
 'Licorería Los Andes SAC', 'DrinkGo Los Andes', '20601234567',
 'RUC', 'Carlos Mendoza López', '45678912',
 'Licorería', 'contacto@losandes.com', '01-4567890',
 'Av. Larco 1234, Miraflores', 'Lima', 'Lima',
 'PE', 'activo', 1, NOW(), NOW());
```

---

## BLOQUE 3: SUSCRIPCIONES

```sql
-- ============================================
-- 3. SUSCRIPCIONES
-- ============================================
INSERT INTO suscripciones (
    id, negocio_id, plan_id, estado,
    inicio_periodo_actual, fin_periodo_actual,
    proxima_fecha_facturacion, auto_renovar,
    creado_en, actualizado_en
) VALUES
(1, 1, 2, 'activa',
 '2026-02-01', '2026-03-01',
 '2026-03-01', 1,
 NOW(), NOW());
```

---

## BLOQUE 4: ROLES

```sql
-- ============================================
-- 4. ROLES DEL NEGOCIO
-- ============================================
INSERT INTO roles (id, negocio_id, nombre, descripcion, es_rol_sistema, esta_activo, creado_en, actualizado_en)
VALUES
(1, 1, 'Administrador', 'Acceso total al sistema', 1, 1, NOW(), NOW()),
(2, 1, 'Gerente', 'Gestión de sede y reportes', 0, 1, NOW(), NOW()),
(3, 1, 'Vendedor', 'Ventas y atención al cliente', 0, 1, NOW(), NOW()),
(4, 1, 'Almacenero', 'Control de inventario y stock', 0, 1, NOW(), NOW());
```

---

## BLOQUE 5: USUARIOS DEL NEGOCIO

```sql
-- ============================================
-- 5. USUARIOS
-- ============================================
-- Hash BCrypt de "Password123" (para referencia, no se usa en login por Postman)
INSERT INTO usuarios (
    id, uuid, negocio_id, email, hash_contrasena,
    nombres, apellidos, tipo_documento, numero_documento,
    telefono, esta_activo, creado_en, actualizado_en
) VALUES
(1, 'usr-00000000-0000-0000-0000-000000000001', 1,
 'carlos.mendoza@losandes.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Carlos', 'Mendoza', 'DNI', '45678912',
 '987654321', 1, NOW(), NOW()),

(2, 'usr-00000000-0000-0000-0000-000000000002', 1,
 'ana.garcia@losandes.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Ana', 'García', 'DNI', '78901234',
 '912345678', 1, NOW(), NOW()),

(3, 'usr-00000000-0000-0000-0000-000000000003', 1,
 'luis.torres@losandes.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
 'Luis', 'Torres', 'DNI', '12345678',
 '945678123', 1, NOW(), NOW());
```

---

## BLOQUE 6: ASIGNAR ROLES A USUARIOS

```sql
-- ============================================
-- 6. USUARIOS_ROLES
-- ============================================
INSERT INTO usuarios_roles (id, usuario_id, rol_id, asignado_en) VALUES
(1, 1, 1, NOW()),  -- Carlos = Administrador
(2, 2, 2, NOW()),  -- Ana = Gerente
(3, 3, 3, NOW());  -- Luis = Vendedor
```

---

## BLOQUE 7: SEDES

```sql
-- ============================================
-- 7. SEDES
-- ============================================
INSERT INTO sedes (
    id, negocio_id, codigo, nombre, direccion,
    ciudad, departamento, pais, telefono,
    es_principal, delivery_habilitado, recojo_habilitado,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 'SEDE-001', 'Sede Principal - Miraflores',
 'Av. Larco 1234, Miraflores', 'Lima', 'Lima', 'PE', '01-4567890',
 1, 1, 1, 1, NOW(), NOW()),

(2, 1, 'SEDE-002', 'Sucursal San Isidro',
 'Calle Las Begonias 456, San Isidro', 'Lima', 'Lima', 'PE', '01-9876543',
 0, 0, 0, 1, NOW(), NOW());
```

---

## BLOQUE 8: ASIGNAR USUARIOS A SEDES

```sql
-- ============================================
-- 8. USUARIOS_SEDES
-- ============================================
INSERT INTO usuarios_sedes (id, usuario_id, sede_id, es_predeterminado, asignado_en) VALUES
(1, 1, 1, 1, NOW()),  -- Carlos → Sede Principal
(2, 2, 1, 1, NOW()),  -- Ana → Sede Principal
(3, 3, 1, 1, NOW());  -- Luis → Sede Principal
```

---

## BLOQUE 9: ALMACENES

```sql
-- ============================================
-- 9. ALMACENES
-- ============================================
INSERT INTO almacenes (
    id, negocio_id, sede_id, codigo, nombre, descripcion,
    es_predeterminado, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'ALM-001', 'Almacén Principal Miraflores',
 'Almacén principal de la sede Miraflores', 1, 1, NOW(), NOW()),

(2, 1, 2, 'ALM-002', 'Almacén San Isidro',
 'Almacén de la sucursal San Isidro', 1, 1, NOW(), NOW());
```

---

## BLOQUE 10: CATEGORÍAS DE PRODUCTOS

```sql
-- ============================================
-- 10. CATEGORÍAS
-- ============================================
INSERT INTO categorias (
    id, negocio_id, nombre, slug, descripcion,
    visible_tienda_online, esta_activo, orden, creado_en, actualizado_en
) VALUES
(1, 1, 'Whisky', 'whisky', 'Whiskies nacionales e importados', 1, 1, 1, NOW(), NOW()),
(2, 1, 'Vinos', 'vinos', 'Vinos tintos, blancos y rosados', 1, 1, 2, NOW(), NOW()),
(3, 1, 'Cervezas', 'cervezas', 'Cervezas artesanales y comerciales', 1, 1, 3, NOW(), NOW()),
(4, 1, 'Ron', 'ron', 'Rones premium y comerciales', 1, 1, 4, NOW(), NOW()),
(5, 1, 'Pisco', 'pisco', 'Piscos peruanos denominación de origen', 1, 1, 5, NOW(), NOW()),
(6, 1, 'Vodka', 'vodka', 'Vodkas importados y nacionales', 1, 1, 6, NOW(), NOW());
```

---

## BLOQUE 11: MARCAS

```sql
-- ============================================
-- 11. MARCAS
-- ============================================
INSERT INTO marcas (
    id, negocio_id, nombre, slug, pais_origen,
    descripcion, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 'Johnnie Walker', 'johnnie-walker', 'Escocia', 'Whisky escocés premium', 1, NOW(), NOW()),
(2, 1, 'Casillero del Diablo', 'casillero-del-diablo', 'Chile', 'Vinos chilenos', 1, NOW(), NOW()),
(3, 1, 'Corona', 'corona', 'México', 'Cerveza mexicana', 1, NOW(), NOW()),
(4, 1, 'Flor de Caña', 'flor-de-cana', 'Nicaragua', 'Ron centroamericano premium', 1, NOW(), NOW()),
(5, 1, 'Cuatro Gallos', 'cuatro-gallos', 'Perú', 'Pisco peruano tradicional', 1, NOW(), NOW()),
(6, 1, 'Absolut', 'absolut', 'Suecia', 'Vodka sueco premium', 1, NOW(), NOW());
```

---

## BLOQUE 12: UNIDADES DE MEDIDA

```sql
-- ============================================
-- 12. UNIDADES DE MEDIDA
-- ============================================
INSERT INTO unidades_medida (
    id, negocio_id, codigo, nombre, abreviatura, tipo, esta_activo
) VALUES
(1, 1, 'UND', 'Unidad', 'und', 'unidad', 1),
(2, 1, 'BOT', 'Botella', 'bot', 'unidad', 1),
(3, 1, 'CJA', 'Caja', 'cja', 'paquete', 1),
(4, 1, 'LT', 'Litro', 'lt', 'volumen', 1);
```

---

## BLOQUE 13: PRODUCTOS (Catálogo de Licores)

```sql
-- ============================================
-- 13. PRODUCTOS
-- ============================================
INSERT INTO productos (
    id, negocio_id, sku, codigo_barras, nombre, slug,
    descripcion_corta, descripcion,
    categoria_id, marca_id, unidad_medida_id,
    tipo_producto, tipo_bebida, grado_alcoholico, volumen_ml, pais_origen,
    precio_compra, precio_venta, tasa_impuesto, impuesto_incluido,
    stock_minimo, tipo_almacenamiento,
    visible_pos, visible_tienda_online, es_destacado,
    requiere_verificacion_edad, permite_descuento,
    esta_activo, creado_en, actualizado_en
) VALUES
-- WHISKY
(1, 1, 'WHI-JW-BL-750', '7750000001', 'Johnnie Walker Black Label 750ml', 'jw-black-750',
 'Whisky escocés 12 años', 'Johnnie Walker Black Label es un whisky escocés mezclado con notas ahumadas y afrutadas.',
 1, 1, 2, 'alcoholica', 'Whisky', 40.00, 750, 'Escocia',
 85.00, 139.90, 18.00, 1,
 5, 'ambiente', 1, 1, 1, 1, 1, 1, NOW(), NOW()),

(2, 1, 'WHI-JW-RE-750', '7750000002', 'Johnnie Walker Red Label 750ml', 'jw-red-750',
 'Whisky escocés clásico', 'Johnnie Walker Red Label, el whisky más icónico del mundo.',
 1, 1, 2, 'alcoholica', 'Whisky', 40.00, 750, 'Escocia',
 45.00, 69.90, 18.00, 1,
 10, 'ambiente', 1, 1, 0, 1, 1, 1, NOW(), NOW()),

-- VINOS
(3, 1, 'VIN-CD-CS-750', '7750000003', 'Casillero del Diablo Cabernet Sauvignon 750ml', 'casillero-cabernet-750',
 'Vino tinto chileno', 'Vino tinto Cabernet Sauvignon de la bodega Concha y Toro.',
 2, 2, 2, 'alcoholica', 'Vino Tinto', 13.50, 750, 'Chile',
 22.00, 39.90, 18.00, 1,
 15, 'ambiente', 1, 1, 1, 0, 1, 1, NOW(), NOW()),

(4, 1, 'VIN-CD-ME-750', '7750000004', 'Casillero del Diablo Merlot 750ml', 'casillero-merlot-750',
 'Vino tinto Merlot chileno', 'Merlot suave y frutado de la línea Casillero del Diablo.',
 2, 2, 2, 'alcoholica', 'Vino Tinto', 13.00, 750, 'Chile',
 22.00, 39.90, 18.00, 1,
 10, 'ambiente', 1, 1, 0, 0, 1, 1, NOW(), NOW()),

-- CERVEZAS
(5, 1, 'CER-COR-355', '7750000005', 'Corona Extra 355ml', 'corona-extra-355',
 'Cerveza mexicana premium', 'Cerveza clara tipo lager, perfecta para el verano.',
 3, 3, 2, 'alcoholica', 'Cerveza Lager', 4.60, 355, 'México',
 3.50, 6.90, 18.00, 1,
 50, 'frio', 1, 1, 1, 0, 1, 1, NOW(), NOW()),

-- RON
(6, 1, 'RON-FC-7A-750', '7750000006', 'Flor de Caña 7 Años 750ml', 'flor-cana-7-750',
 'Ron añejo 7 años', 'Ron premium centroamericano con 7 años de añejamiento.',
 4, 4, 2, 'alcoholica', 'Ron Añejo', 40.00, 750, 'Nicaragua',
 55.00, 89.90, 18.00, 1,
 8, 'ambiente', 1, 1, 0, 1, 1, 1, NOW(), NOW()),

-- PISCO
(7, 1, 'PIS-CG-QBR-500', '7750000007', 'Cuatro Gallos Quebranta 500ml', 'cuatro-gallos-quebranta-500',
 'Pisco peruano Quebranta', 'Pisco puro de uva Quebranta, ideal para chilcanos.',
 5, 5, 2, 'alcoholica', 'Pisco Puro', 42.00, 500, 'Perú',
 28.00, 45.90, 18.00, 1,
 12, 'ambiente', 1, 1, 1, 1, 1, 1, NOW(), NOW()),

-- VODKA
(8, 1, 'VOD-ABS-ORI-750', '7750000008', 'Absolut Original 750ml', 'absolut-original-750',
 'Vodka sueco premium', 'Vodka sueco puro, destilado de trigo.',
 6, 6, 2, 'alcoholica', 'Vodka', 40.00, 750, 'Suecia',
 42.00, 69.90, 18.00, 1,
 8, 'ambiente', 1, 1, 0, 1, 1, 1, NOW(), NOW());
```

---

## BLOQUE 14: PROVEEDORES

```sql
-- ============================================
-- 14. PROVEEDORES
-- ============================================
INSERT INTO proveedores (
    id, negocio_id, razon_social, nombre_comercial,
    tipo_documento, numero_documento,
    direccion, telefono, email,
    contacto_principal, telefono_contacto, email_contacto,
    dias_credito, limite_credito,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 'Distribuidora Nacional de Licores SAC', 'DistriLicores',
 'RUC', '20501234568',
 'Av. Argentina 2345, Callao', '01-5678901', 'ventas@distrilicores.com',
 'Roberto Sánchez', '998877665', 'roberto@distrilicores.com',
 30, '50000.00',
 1, NOW(), NOW()),

(2, 1, 'Importadora de Vinos y Destilados EIRL', 'VinoDest Import',
 'RUC', '20609876543',
 'Jr. Camaná 890, Lima', '01-3456789', 'compras@vinodest.com',
 'María Luisa Rojas', '911223344', 'mluisa@vinodest.com',
 15, '30000.00',
 1, NOW(), NOW());
```

---

## BLOQUE 15: CLIENTES

```sql
-- ============================================
-- 15. CLIENTES
-- ============================================
INSERT INTO clientes (
    id, negocio_id, tipo_documento, numero_documento,
    nombres, apellidos, email, telefono,
    fecha_nacimiento, direccion,
    total_compras, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 'DNI', '40112233',
 'Pedro', 'Ramírez Soto', 'pedro.ramirez@gmail.com', '999111222',
 '1990-05-15', 'Calle Los Olivos 123, Surco',
 0.00, 1, NOW(), NOW()),

(2, 1, 'DNI', '50223344',
 'Lucía', 'Fernández Díaz', 'lucia.fernandez@gmail.com', '988222333',
 '1985-11-20', 'Av. Primavera 456, San Borja',
 0.00, 1, NOW(), NOW()),

(3, 1, 'RUC', '20604455667',
 'Restaurant', 'El Buen Sabor SAC', 'compras@elbuensabor.com', '01-7654321',
 NULL, 'Av. Benavides 789, Miraflores',
 0.00, 1, NOW(), NOW());
```

---

## BLOQUE 16: MÉTODOS DE PAGO

```sql
-- ============================================
-- 16. MÉTODOS DE PAGO
-- ============================================
INSERT INTO metodos_pago (
    id, negocio_id, nombre, codigo, tipo,
    esta_activo, disponible_pos, disponible_tienda_online,
    orden, creado_en, actualizado_en
) VALUES
(1, 1, 'Efectivo', 'EFEC', 'efectivo', 1, 1, 0, 1, NOW(), NOW()),
(2, 1, 'Yape', 'YAPE', 'yape', 1, 1, 1, 2, NOW(), NOW()),
(3, 1, 'Plin', 'PLIN', 'plin', 1, 1, 1, 3, NOW(), NOW()),
(4, 1, 'Tarjeta Visa/MC', 'TARJ', 'tarjeta_credito', 1, 1, 1, 4, NOW(), NOW()),
(5, 1, 'Transferencia BCP', 'TRANBCP', 'transferencia_bancaria', 1, 0, 1, 5, NOW(), NOW());
```

---

## BLOQUE 17: CAJAS REGISTRADORAS

```sql
-- ============================================
-- 17. CAJAS REGISTRADORAS
-- ============================================
INSERT INTO cajas_registradoras (
    id, negocio_id, sede_id, nombre_caja, codigo,
    monto_apertura_defecto, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'Caja Principal', 'CAJA-001', 200.00, 1, NOW(), NOW()),
(2, 1, 1, 'Caja Secundaria', 'CAJA-002', 100.00, 1, NOW(), NOW());
```

---

## BLOQUE 18: SESIONES DE CAJA

```sql
-- ============================================
-- 18. SESIÓN DE CAJA (Una sesión abierta para vender)
-- ============================================
INSERT INTO sesiones_caja (
    id, caja_id, usuario_id,
    fecha_apertura, monto_apertura,
    total_efectivo, total_tarjeta, total_yape, total_plin, total_otros,
    total_ingresos, total_egresos, diferencia_esperado_real,
    estado_sesion, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1,
 NOW(), 200.00,
 0.00, 0.00, 0.00, 0.00, 0.00,
 0.00, 0.00, 0.00,
 'abierta', 1, NOW(), NOW());
```

---

## BLOQUE 19: STOCK E INVENTARIO

```sql
-- ============================================
-- 19. STOCK POR ALMACÉN
-- ============================================
INSERT INTO stock_inventario (
    id, negocio_id, producto_id, almacen_id,
    cantidad_actual, cantidad_reservada, cantidad_disponible,
    costo_promedio, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 1, 24, 0, 24, 85.00, 1, NOW(), NOW()),   -- JW Black
(2, 1, 2, 1, 36, 0, 36, 45.00, 1, NOW(), NOW()),   -- JW Red
(3, 1, 3, 1, 48, 0, 48, 22.00, 1, NOW(), NOW()),   -- Casillero CS
(4, 1, 4, 1, 30, 0, 30, 22.00, 1, NOW(), NOW()),   -- Casillero Merlot
(5, 1, 5, 1, 120, 0, 120, 3.50, 1, NOW(), NOW()),  -- Corona
(6, 1, 6, 1, 18, 0, 18, 55.00, 1, NOW(), NOW()),   -- Flor de Caña
(7, 1, 7, 1, 24, 0, 24, 28.00, 1, NOW(), NOW()),   -- Cuatro Gallos
(8, 1, 8, 1, 20, 0, 20, 42.00, 1, NOW(), NOW());   -- Absolut
```

---

## BLOQUE 20: LOTES DE INVENTARIO

```sql
-- ============================================
-- 20. LOTES
-- ============================================
INSERT INTO lotes_inventario (
    id, negocio_id, producto_id, almacen_id,
    numero_lote, fecha_ingreso, fecha_vencimiento,
    cantidad_inicial, cantidad_actual, costo_unitario,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 1, 'LOT-2026-001', '2026-01-15', '2030-12-31', 24, 24, 85.00, 1, NOW(), NOW()),
(2, 1, 2, 1, 'LOT-2026-002', '2026-01-15', '2030-12-31', 36, 36, 45.00, 1, NOW(), NOW()),
(3, 1, 3, 1, 'LOT-2026-003', '2026-01-20', '2028-06-30', 48, 48, 22.00, 1, NOW(), NOW()),
(4, 1, 4, 1, 'LOT-2026-004', '2026-01-20', '2028-06-30', 30, 30, 22.00, 1, NOW(), NOW()),
(5, 1, 5, 1, 'LOT-2026-005', '2026-02-01', '2027-02-01', 120, 120, 3.50, 1, NOW(), NOW()),
(6, 1, 6, 1, 'LOT-2026-006', '2026-02-01', '2032-12-31', 18, 18, 55.00, 1, NOW(), NOW()),
(7, 1, 7, 1, 'LOT-2026-007', '2026-02-05', '2029-12-31', 24, 24, 28.00, 1, NOW(), NOW()),
(8, 1, 8, 1, 'LOT-2026-008', '2026-02-05', '2031-12-31', 20, 20, 42.00, 1, NOW(), NOW());
```

---

## BLOQUE 21: ÓRDENES DE COMPRA

```sql
-- ============================================
-- 21. ÓRDENES DE COMPRA
-- ============================================
INSERT INTO ordenes_compra (
    id, negocio_id, proveedor_id, numero_orden,
    fecha_orden, fecha_entrega_estimada, fecha_entrega_real,
    almacen_id, estado_orden, subtotal, impuestos, total,
    observaciones, usuario_id,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'OC-2026-0001',
 '2026-02-10', '2026-02-15', '2026-02-14',
 1, 'recibida', 2940.00, 529.20, 3469.20,
 'Pedido de reposición de whisky y ron', 1,
 1, NOW(), NOW()),

(2, 1, 2, 'OC-2026-0002',
 '2026-02-18', '2026-02-25', NULL,
 1, 'confirmada', 1320.00, 237.60, 1557.60,
 'Pedido de vinos para temporada', 1,
 1, NOW(), NOW());
```

---

## BLOQUE 22: DETALLE ÓRDENES DE COMPRA

```sql
-- ============================================
-- 22. DETALLE ÓRDENES DE COMPRA
-- ============================================
INSERT INTO detalle_ordenes_compra (
    id, orden_compra_id, producto_id,
    cantidad_solicitada, cantidad_recibida,
    precio_unitario, subtotal, impuesto, total,
    esta_activo, creado_en, actualizado_en
) VALUES
-- OC-2026-0001: Whisky y Ron
(1, 1, 1, 24, 24, 85.00, 2040.00, 367.20, 2407.20, 1, NOW(), NOW()),  -- JW Black x24
(2, 1, 6, 12, 12, 55.00, 660.00, 118.80, 778.80, 1, NOW(), NOW()),    -- Flor de Caña x12
(3, 1, 2, 12, 12, 45.00, 540.00, 97.20, 637.20, 1, NOW(), NOW()),     -- JW Red x12

-- OC-2026-0002: Vinos
(4, 2, 3, 36, 0, 22.00, 792.00, 142.56, 934.56, 1, NOW(), NOW()),     -- Casillero CS x36
(5, 2, 4, 24, 0, 22.00, 528.00, 95.04, 623.04, 1, NOW(), NOW());      -- Casillero Merlot x24
```

---

## BLOQUE 23: VENTAS

```sql
-- ============================================
-- 23. VENTAS
-- ============================================
INSERT INTO ventas (
    id, negocio_id, sede_id, numero_venta, tipo_venta,
    cliente_id, sesion_caja_id, usuario_id,
    fecha_venta, subtotal, descuento, impuestos, total,
    estado_venta, esta_activo, creado_en, actualizado_en
) VALUES
-- Venta 1: Cliente Pedro compra whisky
(1, 1, 1, 'V-2026-0001', 'mostrador',
 1, 1, 3,
 NOW(), 209.80, 0.00, 37.76, 209.80,
 'pagada', 1, NOW(), NOW()),

-- Venta 2: Cliente Lucía compra vinos y cerveza
(2, 1, 1, 'V-2026-0002', 'mostrador',
 2, 1, 3,
 NOW(), 86.70, 0.00, 15.61, 86.70,
 'pagada', 1, NOW(), NOW()),

-- Venta 3: Venta pendiente (borrador)
(3, 1, 1, 'V-2026-0003', 'mostrador',
 NULL, 1, 3,
 NOW(), 89.90, 0.00, 16.18, 89.90,
 'borrador', 1, NOW(), NOW());
```

---

## BLOQUE 24: DETALLE DE VENTAS

```sql
-- ============================================
-- 24. DETALLE VENTAS
-- ============================================
INSERT INTO detalle_ventas (
    id, venta_id, producto_id,
    cantidad, precio_unitario, descuento,
    subtotal, impuesto, total,
    esta_activo, creado_en, actualizado_en
) VALUES
-- Venta 1: 1x JW Black + 1x JW Red
(1, 1, 1, 1, 139.90, 0.00, 139.90, 25.18, 139.90, 1, NOW(), NOW()),
(2, 1, 2, 1, 69.90, 0.00, 69.90, 12.58, 69.90, 1, NOW(), NOW()),

-- Venta 2: 1x Casillero CS + 1x Casillero Merlot + 1x Corona
(3, 2, 3, 1, 39.90, 0.00, 39.90, 7.18, 39.90, 1, NOW(), NOW()),
(4, 2, 4, 1, 39.90, 0.00, 39.90, 7.18, 39.90, 1, NOW(), NOW()),
(5, 2, 5, 1, 6.90, 0.00, 6.90, 1.24, 6.90, 1, NOW(), NOW()),

-- Venta 3: 1x Flor de Caña (borrador)
(6, 3, 6, 1, 89.90, 0.00, 89.90, 16.18, 89.90, 1, NOW(), NOW());
```

---

## BLOQUE 25: PAGOS DE VENTAS

```sql
-- ============================================
-- 25. PAGOS DE VENTAS
-- ============================================
INSERT INTO pagos_venta (
    id, venta_id, metodo_pago_id,
    monto, numero_referencia, fecha_pago,
    esta_activo, creado_en, actualizado_en
) VALUES
-- Venta 1: Pago en efectivo
(1, 1, 1, 209.80, NULL, NOW(), 1, NOW(), NOW()),

-- Venta 2: Pago con Yape
(2, 2, 2, 86.70, 'YAPE-20260222-001', NOW(), 1, NOW(), NOW());
```

---

## BLOQUE 26: MOVIMIENTOS DE INVENTARIO

```sql
-- ============================================
-- 26. MOVIMIENTOS DE INVENTARIO
-- ============================================
INSERT INTO movimientos_inventario (
    id, negocio_id, producto_id, almacen_origen_id,
    lote_id, tipo_movimiento,
    cantidad, costo_unitario, monto_total,
    motivo_movimiento, referencia_documento, usuario_id,
    fecha_movimiento, esta_activo, creado_en, actualizado_en
) VALUES
-- Entradas por compra (OC-2026-0001)
(1, 1, 1, 1, 1, 'entrada', 24, 85.00, 2040.00,
 'Ingreso por orden de compra', 'OC-2026-0001', 1, NOW(), 1, NOW(), NOW()),
(2, 1, 6, 1, 6, 'entrada', 18, 55.00, 990.00,
 'Ingreso por orden de compra', 'OC-2026-0001', 1, NOW(), 1, NOW(), NOW()),

-- Salidas por venta (V-2026-0001)
(3, 1, 1, 1, 1, 'salida', 1, 85.00, 85.00,
 'Salida por venta', 'V-2026-0001', 3, NOW(), 1, NOW(), NOW()),
(4, 1, 2, 1, 2, 'salida', 1, 45.00, 45.00,
 'Salida por venta', 'V-2026-0001', 3, NOW(), 1, NOW(), NOW()),

-- Salidas por venta (V-2026-0002)
(5, 1, 3, 1, 3, 'salida', 1, 22.00, 22.00,
 'Salida por venta', 'V-2026-0002', 3, NOW(), 1, NOW(), NOW()),
(6, 1, 5, 1, 5, 'salida', 1, 3.50, 3.50,
 'Salida por venta', 'V-2026-0002', 3, NOW(), 1, NOW(), NOW());
```

---

## BLOQUE 27: CATEGORÍAS DE GASTOS

```sql
-- ============================================
-- 27. CATEGORÍAS DE GASTOS
-- ============================================
INSERT INTO categorias_gasto (
    id, negocio_id, nombre, codigo, tipo, descripcion,
    esta_activo, creado_en
) VALUES
(1, 1, 'Alquiler de Local', 'ALQUILER', 'operativo', 'Pago de alquiler del local comercial', 1, NOW()),
(2, 1, 'Servicios Básicos', 'SERVICIOS', 'servicio', 'Luz, agua, internet, teléfono', 1, NOW()),
(3, 1, 'Sueldos y Salarios', 'SUELDOS', 'personal', 'Pago de planilla al personal', 1, NOW()),
(4, 1, 'Marketing y Publicidad', 'MARKETING', 'marketing', 'Publicidad en redes sociales y local', 1, NOW()),
(5, 1, 'Mantenimiento', 'MANT', 'mantenimiento', 'Reparaciones y mantenimiento del local', 1, NOW());
```

---

## BLOQUE 28: GASTOS

```sql
-- ============================================
-- 28. GASTOS DEL NEGOCIO
-- ============================================
INSERT INTO gastos (
    id, negocio_id, sede_id, numero_gasto, categoria_id,
    descripcion, monto, monto_impuesto, total,
    moneda, fecha_gasto, metodo_pago, referencia_pago,
    estado, es_recurrente, periodo_recurrencia,
    registrado_por, esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'GAS-2026-0001', 1,
 'Alquiler del local - Febrero 2026', 3500.00, 630.00, 4130.00,
 'PEN', '2026-02-01', 'transferencia_bancaria', 'TRANS-BCP-001',
 'pagado', 1, 'mensual',
 1, 1, NOW(), NOW()),

(2, 1, 1, 'GAS-2026-0002', 2,
 'Pago de luz y agua - Febrero 2026', 450.00, 81.00, 531.00,
 'PEN', '2026-02-15', 'efectivo', NULL,
 'pagado', 1, 'mensual',
 1, 1, NOW(), NOW()),

(3, 1, 1, 'GAS-2026-0003', 3,
 'Planilla empleados - Febrero 2026', 8500.00, 0.00, 8500.00,
 'PEN', '2026-02-28', 'transferencia_bancaria', 'TRANS-BCP-002',
 'pendiente', 1, 'mensual',
 1, 1, NOW(), NOW());
```

---

## BLOQUE 29: SERIES DE FACTURACIÓN

```sql
-- ============================================
-- 29. SERIES DE FACTURACIÓN
-- ============================================
INSERT INTO series_facturacion (
    id, negocio_id, sede_id, tipo_documento,
    serie, numero_actual, es_predeterminada,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'boleta', 'B001', 2, 1, 1, NOW(), NOW()),
(2, 1, 1, 'factura', 'F001', 0, 1, 1, NOW(), NOW()),
(3, 1, 1, 'nota_credito', 'BC01', 0, 1, 1, NOW(), NOW());
```

---

## BLOQUE 30: DOCUMENTOS DE FACTURACIÓN

```sql
-- ============================================
-- 30. DOCUMENTOS DE FACTURACIÓN
-- ============================================
INSERT INTO documentos_facturacion (
    id, negocio_id, serie_facturacion_id,
    tipo_documento, numero_documento,
    cliente_id, venta_id,
    fecha_emision, subtotal, impuestos, total,
    estado_documento, usuario_id,
    esta_activo, creado_en, actualizado_en
) VALUES
-- Boleta para Venta 1
(1, 1, 1, 'boleta', 'B001-00000001',
 1, 1,
 CURDATE(), 177.80, 32.00, 209.80,
 'aceptado', 3,
 1, NOW(), NOW()),

-- Boleta para Venta 2
(2, 1, 1, 'boleta', 'B001-00000002',
 2, 2,
 CURDATE(), 73.47, 13.23, 86.70,
 'aceptado', 3,
 1, NOW(), NOW());
```

---

## BLOQUE 31: PROMOCIONES

```sql
-- ============================================
-- 31. PROMOCIONES
-- ============================================
INSERT INTO promociones (
    id, negocio_id, nombre, codigo,
    tipo_descuento, valor_descuento, monto_minimo_compra,
    max_usos, usos_actuales,
    aplica_a, valido_desde, valido_hasta,
    esta_activo, creado_por, creado_en, actualizado_en
) VALUES
(1, 1, 'Descuento Verano 2026', 'VERANO2026',
 'porcentaje', 10.00, 50.00,
 100, 5,
 'todo', '2026-02-01 00:00:00', '2026-03-31 23:59:59',
 1, 1, NOW(), NOW()),

(2, 1, 'Descuento Vinos S/15', 'VINOS15',
 'monto_fijo', 15.00, 80.00,
 50, 0,
 'categoria', '2026-02-15 00:00:00', '2026-04-30 23:59:59',
 1, 1, NOW(), NOW());
```

---

## BLOQUE 32: DEVOLUCIONES

```sql
-- ============================================
-- 32. DEVOLUCIONES
-- ============================================
INSERT INTO devoluciones (
    id, negocio_id, sede_id, numero_devolucion,
    venta_id, cliente_id,
    tipo_devolucion, categoria_motivo, detalle_motivo,
    subtotal, monto_impuesto, total,
    metodo_reembolso, estado,
    solicitado_por, solicitado_en,
    esta_activo, creado_en, actualizado_en
) VALUES
(1, 1, 1, 'DEV-2026-0001',
 1, 1,
 'parcial', 'cambio_cliente', 'Cliente desea cambiar Johnnie Walker Red por otro producto',
 69.90, 12.58, 69.90,
 'efectivo', 'solicitada',
 3, NOW(),
 1, NOW(), NOW());
```

---

# ✅ VERIFICACIÓN FINAL

```sql
-- ============================================
-- VERIFICACIÓN DE TODOS LOS DATOS
-- ============================================
SELECT 'planes_suscripcion' AS Tabla, COUNT(*) AS Total FROM planes_suscripcion
UNION ALL SELECT 'negocios', COUNT(*) FROM negocios
UNION ALL SELECT 'suscripciones', COUNT(*) FROM suscripciones
UNION ALL SELECT 'roles', COUNT(*) FROM roles
UNION ALL SELECT 'usuarios', COUNT(*) FROM usuarios
UNION ALL SELECT 'usuarios_roles', COUNT(*) FROM usuarios_roles
UNION ALL SELECT 'sedes', COUNT(*) FROM sedes
UNION ALL SELECT 'usuarios_sedes', COUNT(*) FROM usuarios_sedes
UNION ALL SELECT 'almacenes', COUNT(*) FROM almacenes
UNION ALL SELECT 'categorias', COUNT(*) FROM categorias
UNION ALL SELECT 'marcas', COUNT(*) FROM marcas
UNION ALL SELECT 'unidades_medida', COUNT(*) FROM unidades_medida
UNION ALL SELECT 'productos', COUNT(*) FROM productos
UNION ALL SELECT 'proveedores', COUNT(*) FROM proveedores
UNION ALL SELECT 'ordenes_compra', COUNT(*) FROM ordenes_compra
UNION ALL SELECT 'detalle_ordenes_compra', COUNT(*) FROM detalle_ordenes_compra
UNION ALL SELECT 'clientes', COUNT(*) FROM clientes
UNION ALL SELECT 'metodos_pago', COUNT(*) FROM metodos_pago
UNION ALL SELECT 'cajas_registradoras', COUNT(*) FROM cajas_registradoras
UNION ALL SELECT 'sesiones_caja', COUNT(*) FROM sesiones_caja
UNION ALL SELECT 'stock_inventario', COUNT(*) FROM stock_inventario
UNION ALL SELECT 'lotes_inventario', COUNT(*) FROM lotes_inventario
UNION ALL SELECT 'ventas', COUNT(*) FROM ventas
UNION ALL SELECT 'detalle_ventas', COUNT(*) FROM detalle_ventas
UNION ALL SELECT 'pagos_venta', COUNT(*) FROM pagos_venta
UNION ALL SELECT 'movimientos_inventario', COUNT(*) FROM movimientos_inventario
UNION ALL SELECT 'categorias_gasto', COUNT(*) FROM categorias_gasto
UNION ALL SELECT 'gastos', COUNT(*) FROM gastos
UNION ALL SELECT 'series_facturacion', COUNT(*) FROM series_facturacion
UNION ALL SELECT 'documentos_facturacion', COUNT(*) FROM documentos_facturacion
UNION ALL SELECT 'promociones', COUNT(*) FROM promociones
UNION ALL SELECT 'devoluciones', COUNT(*) FROM devoluciones;
```

---

# 📊 RESUMEN DE DATOS GENERADOS

| # | Tabla | Registros | Descripción |
|---|---|---|---|
| 1 | `planes_suscripcion` | 3 | Emprendedor, Negocio, Enterprise |
| 2 | `negocios` | 1 | Licorería Los Andes SAC |
| 3 | `suscripciones` | 1 | Plan Negocio activa |
| 4 | `roles` | 4 | Admin, Gerente, Vendedor, Almacenero |
| 5 | `usuarios` | 3 | Carlos (Admin), Ana (Gerente), Luis (Vendedor) |
| 6 | `usuarios_roles` | 3 | Asignación de roles |
| 7 | `sedes` | 2 | Miraflores (principal), San Isidro |
| 8 | `usuarios_sedes` | 3 | Todos asignados a sede principal |
| 9 | `almacenes` | 2 | Uno por sede |
| 10 | `categorias` | 6 | Whisky, Vinos, Cervezas, Ron, Pisco, Vodka |
| 11 | `marcas` | 6 | JW, Casillero, Corona, FlorCaña, CuatroGallos, Absolut |
| 12 | `unidades_medida` | 4 | Unidad, Botella, Caja, Litro |
| 13 | `productos` | 8 | Licores variados con precios reales |
| 14 | `proveedores` | 2 | DistriLicores, VinoDest Import |
| 15 | `ordenes_compra` | 2 | Una recibida, una confirmada |
| 16 | `detalle_ordenes_compra` | 5 | Detalle de ambas órdenes |
| 17 | `clientes` | 3 | 2 personas + 1 empresa |
| 18 | `metodos_pago` | 5 | Efectivo, Yape, Plin, Tarjeta, Transferencia |
| 19 | `cajas_registradoras` | 2 | Caja Principal y Secundaria |
| 20 | `sesiones_caja` | 1 | Sesión abierta para ventas |
| 21 | `stock_inventario` | 8 | Stock por producto en almacén |
| 22 | `lotes_inventario` | 8 | Lotes con fechas de vencimiento |
| 23 | `ventas` | 3 | 2 pagadas + 1 borrador |
| 24 | `detalle_ventas` | 6 | Items de cada venta |
| 25 | `pagos_venta` | 2 | Efectivo + Yape |
| 26 | `movimientos_inventario` | 6 | Entradas (compra) y Salidas (venta) |
| 27 | `categorias_gasto` | 5 | Alquiler, Servicios, Sueldos, Marketing, Mantenimiento |
| 28 | `gastos` | 3 | Alquiler, Servicios, Planilla |
| 29 | `series_facturacion` | 3 | Boleta, Factura, Nota Crédito |
| 30 | `documentos_facturacion` | 2 | Boletas emitidas |
| 31 | `promociones` | 2 | Verano 10% + Vinos S/15 |
| 32 | `devoluciones` | 1 | Devolución parcial solicitada |

**TOTAL: 32 tablas con 89 registros**

---

## 🚀 FLUJOS COMPLETOS PARA LA EVALUACIÓN

### Flujo Negocios
`planes_suscripcion` → `negocios` → `suscripciones`

### Flujo Usuarios
`usuarios` → `roles` → `usuarios_roles` → `sedes` → `usuarios_sedes`

### Flujo Compras
`proveedores` → `ordenes_compra` → `detalle_ordenes_compra` → `movimientos_inventario` (entrada)

### Flujo Ventas (POS)
`cajas_registradoras` → `sesiones_caja` → `ventas` → `detalle_ventas` → `pagos_venta` → `movimientos_inventario` (salida)

### Flujo Inventario
`stock_inventario` → `lotes_inventario` → `movimientos_inventario`

### Flujo Facturación
`series_facturacion` → `documentos_facturacion` (vinculado a ventas)

### Flujo Gastos
`categorias_gasto` → `gastos`

### Flujo Devoluciones
`devoluciones` (vinculado a ventas y clientes)

---

## ⚡ TABLAS OMITIDAS (No esenciales para la evaluación)

| Tabla | Razón |
|---|---|
| `mesas` | No solicitado |
| `configuracion_tienda_online` | Storefront no prioritario |
| `paginas_tienda_online` | Storefront no prioritario |
| `zonas_delivery` | No prioritario |
| `pedidos` / `detalle_pedidos` / `pagos_pedido` | Pedidos online no prioritarios |
| `seguimiento_pedidos` | Depende de pedidos |
| `combos` / `detalle_combos` | Opcional |
| `condiciones_promocion` | Opcional |
| `detalle_devoluciones` | Opcional |
| `detalle_documentos_facturacion` | Opcional |
| `movimientos_caja` | Opcional |
| `productos_proveedor` | Opcional |
| `modulos_sistema` / `permisos_sistema` / `modulos_negocio` / `roles_permisos` | Infraestructura de permisos |
| `usuarios_plataforma` / `sesiones_usuario` / `registros_auditoria` | Auditoría/SuperAdmin |
| `facturas_suscripcion` | Facturación de la plataforma SaaS |
