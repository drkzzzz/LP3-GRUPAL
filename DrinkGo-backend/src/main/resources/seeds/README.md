# 🚀 Seeds de Base de Datos - DrinkGo SuperAdmin

## 📋 Descripción

Este directorio contiene los scripts SQL necesarios para poblar la base de datos con datos de prueba del módulo **SuperAdmin**. Los seeds incluyen usuarios, planes de suscripción, negocios demo y sus respectivas sedes.

## 📂 Archivos de Seed

### 1. `01_superadmin_usuarios.sql`
Crea usuarios de la plataforma para acceder al panel SuperAdmin.

**Usuarios creados:**
- **SuperAdmin**: admin@drinkgo.com / Admin123!
- **Soporte**: soporte@drinkgo.com / Soporte123!
- **Visualizador**: visualizador@drinkgo.com / Soporte123!

### 2. `02_planes_suscripcion.sql`
Define 4 planes de suscripción con diferentes características y precios.

**Planes creados:**
- **Plan Básico**: S/149/mes - 1 sede, 3 usuarios, 500 productos
- **Plan Profesional**: S/349/mes - 3 sedes, 10 usuarios, 2000 productos, tienda online
- **Plan Enterprise**: S/899/mes - Ilimitado, API, soporte 24/7
- **Plan Free**: S/0 - Prueba 30 días

### 3. `03_negocios_demo.sql`
Crea 4 negocios de ejemplo en diferentes estados con sus suscripciones y sedes.

**Negocios creados:**
- **Don Pepe**: Activo, Plan Básico, 1 sede
- **La Bodega**: Activo, Plan Profesional, 2 sedes (San Isidro + Miraflores)
- **El Imperio**: Pendiente, Sin plan asignado
- **Premium Wines**: Suspendido por falta de pago

### 4. `04_catalogo_demo.sql`
Crea datos completos del módulo Catálogo para los 4 negocios. Útil como base para módulos de Inventario, Ventas, Compras y Pedidos.

**Datos creados por negocio (×4 negocios):**
- **5 Categorías**: Rones ★, Cervezas ★, Vinos y Espumantes ★, Snacks y Piqueos, Gaseosas y Bebidas (★ = alcohólica)
- **5 Marcas**: Cartavio (Perú), Pilsen (Perú), Concha y Toro (Chile), Lay's (USA), Coca-Cola (USA)
- **5 Unidades de Medida**: Unidad, Botella, Paquete, Lata, Six Pack
- **5 Productos**: Ron Cartavio Black 750ml (40°), Cerveza Pilsen 630ml (5°), Vino Casillero del Diablo 750ml (13.5°), Papitas Lay's 200g, Coca-Cola 1.5L
- **2 Combos**: Pack Parrillero (S/22→S/18.90), Combo Ron + Gaseosa (S/57→S/49.90)
- **Detalle Combos**: 2 productos por combo con precios unitarios

**Totales**: 20 categorías, 20 marcas, 20 unidades, 20 productos, 8 combos, 16 detalle combos

### 5. `05_inventario_demo.sql`
Crea datos del módulo Inventario con almacenes, lotes y stock para los productos del catálogo.

**Datos creados:**
- **Almacenes**: 2 por negocio (Principal + Secundario/Refrigerado)
- **Stock Inventario**: Registro consolidado por producto × almacén
- **Lotes**: Múltiples lotes por producto con fechas de vencimiento (formato: LT-YYYYMMDD-NNN)
- **Movimientos**: Historial de entradas, salidas y ajustes de inventario

**Características:**
- Fechas de vencimiento realistas (alcoholes 2+ años, perecibles 6 meses)
- Cantidades de stock variadas (50-400 unidades por producto)
- Costos unitarios de compra registrados por lote
- Movimientos de stock inicial, compras y ventas para demo kardex

### 6. `06_proveedores_compras_demo.sql`
Crea datos del módulo Compras con proveedores, catálogos de proveedor y órdenes de compra.

**Datos creados:**
- **Proveedores**: 3-4 por negocio con datos peruanos (RUC, dirección, contacto)
- **Productos por Proveedor**: Vinculación de productos con precios de compra
- **Órdenes de Compra**: Mixtas entre estados 'recibida' y 'pendiente'
- **Detalle de Órdenes**: Cantidades solicitadas/recibidas, precios, impuestos

**Características:**
- Numeración de órdenes: OC-YYYYMMDD-NNN
- Estados realistas: algunas recibidas (integradas con inventario), otras pendientes (para testing)
- Precios de compra coherentes con los costos en lotes de inventario
- Proveedores reales del mercado peruano (Backus, Inca Kola, importadoras)

## 🔧 Cómo Ejecutar los Seeds

### ⚠️ ORDEN DE EJECUCIÓN IMPORTANTE

Los seeds **DEBEN** ejecutarse en el siguiente orden debido a las dependencias entre tablas:

1. `01_superadmin_usuarios.sql` - Usuarios de la plataforma
2. `02_planes_suscripcion.sql` - Planes de suscripción
3. `03_negocios_demo.sql` - Negocios, sedes, usuarios admin
4. `04_catalogo_demo.sql` - Categorías, marcas, productos, combos
5. `05_inventario_demo.sql` - Almacenes, lotes, stock, movimientos
6. `06_proveedores_compras_demo.sql` - Proveedores, órdenes de compra

### Opción 1: Desde MySQL Command Line

```bash
# 1. Ingresar a MySQL
mysql -u root -p

# 2. Ejecutar cada seed en orden
source E:/VERANO/LP3/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/01_superadmin_usuarios.sql
source E:/VERANO/LP3/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/02_planes_suscripcion.sql
source E:/VERANO/LP3/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/03_negocios_demo.sql
source E:/VERANO/LP3/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/04_catalogo_demo.sql
source E:/VERANO/LP3/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/05_inventario_demo.sql
source E:/VERANO/LP3/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/06_proveedores_compras_demo.sql
```

### Opción 2: Desde XAMPP / phpMyAdmin

1. Abrir **phpMyAdmin** en http://localhost/phpmyadmin
2. Seleccionar base de datos `licores_drinkgo`
3. Ir a la pestaña **SQL**
4. Copiar y pegar el contenido de cada archivo **EN ORDEN** (1→2→3→4→5→6)
5. Hacer clic en **Continuar** para ejecutar

### Opción 3: Desde Terminal (Windows)

```powershell
# Ejecutar desde la carpeta seeds
cd "E:\VERANO\LP3\LP3-GRUPAL\DrinkGo-backend\src\main\resources\seeds"

# Ejecutar cada seed en orden
mysql -u root -p licores_drinkgo < 01_superadmin_usuarios.sql
mysql -u root -p licores_drinkgo < 02_planes_suscripcion.sql
mysql -u root -p licores_drinkgo < 03_negocios_demo.sql
mysql -u root -p licores_drinkgo < 04_catalogo_demo.sql
mysql -u root -p licores_drinkgo < 05_inventario_demo.sql
mysql -u root -p licores_drinkgo < 06_proveedores_compras_demo.sql
```

### 🔄 Ejecución Idempotente

Todos los seeds utilizan `WHERE NOT EXISTS` para evitar duplicados. **Puedes ejecutarlos múltiples veces sin problemas** - no se crearán registros duplicados.

## 🔐 Credenciales de Acceso

### SuperAdmin Principal
```
Email: admin@drinkgo.com
Password: Admin123!
Rol: superadmin
```

### Usuario de Soporte
```
Email: soporte@drinkgo.com
Password: Soporte123!
Rol: soporte_plataforma
```

## 📊 Datos Creados

### Totales Generales
- ✅ 3 usuarios de plataforma (SuperAdmin, Soporte, Visualizador)
- ✅ 4 planes de suscripción (Basic, Professional, Enterprise, Free)
- ✅ 4 negocios demo con 4 usuarios admin (1 por negocio)
- ✅ 5 sedes (entre todos los negocios)
- ✅ 3 suscripciones activas/suspendidas

### Módulo Catálogo
- ✅ 20 categorías (5 × 4 negocios)
- ✅ 20 marcas (5 × 4 negocios)
- ✅ 20 unidades de medida (5 × 4 negocios)
- ✅ 20 productos (5 × 4 negocios)
- ✅ 8 combos (2 × 4 negocios)
- ✅ 16 detalle combos (4 × 4 negocios)

### Módulo Inventario (NUEVO)
- ✅ 7 almacenes (Don Pepe: 2, La Bodega: 2, El Imperio: 2, Premium: 1)
- ✅ 18+ registros de stock consolidado (producto × almacén)
- ✅ 20+ lotes de inventario con fechas de vencimiento
- ✅ 10+ movimientos de inventario (stock inicial, compras, ventas, ajustes)
- ✅ Costos unitarios registrados por lote (S/3.20 - S/42.50)

### Módulo Compras (NUEVO)
- ✅ 11 proveedores con RUCs peruanos (Don Pepe: 4, La Bodega: 4, El Imperio: 3)
- ✅ 15+ productos por proveedor con precios de compra
- ✅ 10 órdenes de compra (6 recibidas, 4 pendientes)
- ✅ 15+ detalles de órdenes con cantidades y precios
- ✅ Totales de órdenes: S/907 - S/5,640 por orden

### Estados de Negocios
- **Activos**: 2 (Don Pepe, La Bodega)
- **Pendientes**: 1 (El Imperio)
- **Suspendidos**: 1 (Premium Wines)

## 🧪 Uso para Desarrollo

### Frontend - Login

1. Iniciar el frontend:
   ```bash
   cd DrinkGo-frontend
   npm run dev
   ```

2. Abrir http://localhost:5173

3. Ingresar con cualquiera de las credenciales de arriba

### Backend - API
Asegúrate de que el backend Spring Boot esté corriendo en `localhost:8080`

## 🔄 Resetear Datos (Limpiar Seeds)

Si necesitas limpiar los datos de prueba y empezar de nuevo:

```sql
USE licores_drinkgo;

-- Limpiar en orden por dependencias (de hijo a padre)

-- Módulo Compras
DELETE FROM detalle_ordenes_compra WHERE orden_compra_id IN 
  (SELECT id FROM ordenes_compra WHERE numero_orden LIKE 'OC-%');
DELETE FROM ordenes_compra WHERE numero_orden LIKE 'OC-%';
DELETE FROM productos_proveedor WHERE proveedor_id IN 
  (SELECT id FROM proveedores WHERE codigo LIKE 'PROV-%');
DELETE FROM proveedores WHERE codigo LIKE 'PROV-%';

-- Módulo Inventario
DELETE FROM movimientos_inventario WHERE lote_id IN 
  (SELECT id FROM lotes_inventario WHERE numero_lote LIKE 'LT-%');
DELETE FROM lotes_inventario WHERE numero_lote LIKE 'LT-%';
DELETE FROM stock_inventario WHERE almacen_id IN 
  (SELECT id FROM almacenes WHERE codigo LIKE 'ALM-%' OR codigo LIKE 'LB%' OR codigo LIKE 'EI-%' OR codigo LIKE 'PW-%');
DELETE FROM almacenes WHERE codigo LIKE 'ALM-%' OR codigo LIKE 'LB%' OR codigo LIKE 'EI-%' OR codigo LIKE 'PW-%';

-- Módulo Catálogo
DELETE FROM detalle_combos WHERE combo_id IN 
  (SELECT id FROM combos WHERE nombre LIKE 'Pack %' OR nombre LIKE 'Combo %');
DELETE FROM combos WHERE nombre LIKE 'Pack %' OR nombre LIKE 'Combo %';
DELETE FROM productos WHERE sku LIKE 'DP-%' OR sku LIKE 'LB-%' OR sku LIKE 'EI-%' OR sku LIKE 'PW-%';
DELETE FROM unidades_medida WHERE codigo IN ('UND','BOT','PAQ','LAT','SIX');
DELETE FROM marcas WHERE nombre IN ('Cartavio','Pilsen','Concha y Toro','Lay''s','Coca-Cola');
DELETE FROM categorias WHERE nombre IN ('Rones','Cervezas','Vinos y Espumantes','Snacks y Piqueos','Gaseosas y Bebidas');

-- Módulo Negocios
DELETE FROM usuarios_roles WHERE usuario_id IN 
  (SELECT id FROM usuarios WHERE email LIKE '%@donpepe.com' OR email LIKE '%@labodega.com%' OR email LIKE '%@elimperio.pe' OR email LIKE '%@premiumwines.pe');
DELETE FROM usuarios WHERE email LIKE '%@donpepe.com' OR email LIKE '%@labodega.com%' OR email LIKE '%@elimperio.pe' OR email LIKE '%@premiumwines.pe';
DELETE FROM roles WHERE negocio_id IN 
  (SELECT id FROM negocios WHERE ruc IN ('20123456789','20987654321','20456789123','20111222333'));
DELETE FROM suscripciones WHERE negocio_id IN 
  (SELECT id FROM negocios WHERE ruc IN ('20123456789','20987654321','20456789123','20111222333'));
DELETE FROM sedes WHERE negocio_id IN 
  (SELECT id FROM negocios WHERE ruc IN ('20123456789','20987654321','20456789123','20111222333'));
DELETE FROM negocios WHERE ruc IN ('20123456789','20987654321','20456789123','20111222333');

-- Planes y SuperAdmin
DELETE FROM planes_suscripcion WHERE nombre IN ('Plan Básico','Plan Profesional','Plan Enterprise','Plan Free');
DELETE FROM usuarios_plataforma WHERE email IN ('admin@drinkgo.com','soporte@drinkgo.com','visualizador@drinkgo.com');
```

⚠️ **IMPORTANTE**: Este script eliminará TODOS los datos de prueba. Ejecutar con cuidado.

Después de limpiar, vuelve a ejecutar todos los seeds en orden (01→02→03→04→05→06).

## ⚠️ Notas Importantes

1. **Orden de Ejecución**: Los seeds DEBEN ejecutarse en orden (01→02→03→04→05→06) debido a las dependencias entre tablas:
   - 05 depende de 03 (negocios/sedes) y 04 (productos)
   - 06 depende de 03, 04 y 05 (almacenes)

2. **Contraseñas**: Todas las contraseñas están hasheadas con BCrypt (fortaleza 10) compatible con Spring Security:
   - SuperAdmin/Usuarios plataforma: `Admin123!` / `Soporte123!`
   - Usuarios admin de negocios: `Admin123!`

3. **Base de Datos**: Asegúrate de que la base de datos `licores_drinkgo` existe antes de ejecutar los seeds

4. **Esquema**: Si es la primera vez, ejecuta primero el script de creación de tablas:
   ```bash
   mysql -u root -p < DrinkGo-backend/src/main/resources/bd/drinkgo_database.sql
   ```

5. **Configuración Backend**: Verifica que `application.properties` tenga la configuración correcta de conexión a MySQL

6. **Integración Inventario-Compras**: Los seeds 05 y 06 están sincronizados:
   - Los lotes en inventario corresponden a órdenes recibidas en compras
   - Los costos unitarios en lotes coinciden con precios de proveedor
   - Las órdenes "pendientes" están listas para testing de recepción

7. **Datos Realistas**: Se usan proveedores reales del mercado peruano (Backus, Inca Kola, importadoras) con RUCs válidos

## 📝 Próximos Pasos

Después de ejecutar todos los seeds (01→02→03→04→05→06):

### Para el Equipo Backend
1. ✅ Verificar que las tablas se poblaron correctamente
2. ✅ Probar endpoints de Inventario con los almacenes y lotes creados
3. ✅ Probar endpoints de Compras con las órdenes pendientes
4. ✅ Implementar endpoints de Ventas usando el stock disponible
5. ✅ Implementar endpoints de Facturación

### Para el Equipo Frontend
1. ✅ Iniciar el backend Spring Boot (`mvn spring-boot:run`)
2. ✅ Iniciar el frontend React (`pnpm dev`)
3. ✅ Navegar a http://localhost:5173
4. ✅ Hacer login con `admin@donpepe.com` / `Admin123!` (o cualquier admin de negocio)
5. ✅ **Probar módulo Inventario**: Ver almacenes, lotes, stock, movimientos
6. ✅ **Probar módulo Compras**: Ver proveedores, órdenes pendientes, marcar como recibidas
7. ✅ **Implementar módulo Ventas**: Crear ventas usando productos con stock disponible
8. ✅ **Implementar módulo Facturación**: Generar facturas a partir de ventas

### Credenciales de Acceso por Negocio
```
Don Pepe:
  Email: admin@donpepe.com
  Password: Admin123!
  RUC: 20123456789
  Almacenes: ALM-PRINCIPAL, ALM-DEPOSITO

La Bodega:
  Email: admin@labodega.com.pe
  Password: Admin123!
  RUC: 20987654321
  Almacenes: LB01-ALM-MAIN (San Isidro), LB02-ALM-MAIN (Miraflores)

El Imperio:
  Email: admin@elimperio.pe
  Password: Admin123!
  RUC: 20456789123
  Almacenes: EI-ALM-GENERAL, EI-ALM-FRIO

Premium Wines (SUSPENDIDO):
  Email: admin@premiumwines.pe
  Password: Admin123!
  RUC: 20111222333
```

## 🐛 Troubleshooting

### Error: "Table doesn't exist"
- Ejecuta primero el script de creación de base de datos (`drinkgo_database.sql`)

### Error: "Duplicate entry"
- Los seeds ya fueron ejecutados. Usa el script de reseteo arriba o cambia los emails/RUCs

### Error: "Cannot add or update a child row"
- Ejecuta los seeds en el orden correcto (01 → 02 → 03)

### Backend no conecta a BD
- Verifica que MySQL esté corriendo en XAMPP
- Revisa las credenciales en `application.properties`
- Confirma que el puerto sea 3306

---

**Desarrollado para DrinkGo - Sistema Multi-Tenant para Licorerías**
