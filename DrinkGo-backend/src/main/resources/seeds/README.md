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

## 🔧 Cómo Ejecutar los Seeds

### Opción 1: Desde MySQL Command Line

```bash
# 1. Ingresar a MySQL
mysql -u root -p

# 2. Ejecutar cada seed en orden
source C:/Users/carlo.CARLOS/Documents/lp32.0/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/01_superadmin_usuarios.sql
source C:/Users/carlo.CARLOS/Documents/lp32.0/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/02_planes_suscripcion.sql
source C:/Users/carlo.CARLOS/Documents/lp32.0/LP3-GRUPAL/DrinkGo-backend/src/main/resources/seeds/03_negocios_demo.sql
```

### Opción 2: Desde XAMPP / phpMyAdmin

1. Abrir **phpMyAdmin** en http://localhost/phpmyadmin
2. Seleccionar base de datos `licores_drinkgo`
3. Ir a la pestaña **SQL**
4. Copiar y pegar el contenido de cada archivo en orden:
   - `01_superadmin_usuarios.sql`
   - `02_planes_suscripcion.sql`
   - `03_negocios_demo.sql`
5. Hacer clic en **Continuar** para ejecutar

### Opción 3: Desde Terminal (Windows)

```powershell
# Ejecutar desde la carpeta seeds
cd "C:\Users\carlo.CARLOS\Documents\lp32.0\LP3-GRUPAL\DrinkGo-backend\src\main\resources\seeds"

# Ejecutar cada seed
mysql -u root -p licores_drinkgo < 01_superadmin_usuarios.sql
mysql -u root -p licores_drinkgo < 02_planes_suscripcion.sql
mysql -u root -p licores_drinkgo < 03_negocios_demo.sql
```

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

### Totales
- ✅ 3 usuarios de plataforma (SuperAdmin, Soporte, Visualizador)
- ✅ 4 planes de suscripción (Basic, Professional, Enterprise, Free)
- ✅ 4 negocios demo
- ✅ 5 sedes (entre todos los negocios)
- ✅ 3 suscripciones activas/suspendidas
- ✅ 20 categorías (5 × 4 negocios)
- ✅ 20 marcas (5 × 4 negocios)
- ✅ 20 unidades de medida (5 × 4 negocios)
- ✅ 20 productos (5 × 4 negocios)
- ✅ 8 combos (2 × 4 negocios)
- ✅ 16 detalle combos (4 × 4 negocios)

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

-- Limpiar en orden por dependencias
DELETE FROM suscripciones WHERE negocio_id IN (SELECT id FROM negocios WHERE email LIKE '%@drinkgo.com' OR email LIKE '%@donpepe.com' OR email LIKE '%@labodega.com%' OR email LIKE '%@elimperio.pe' OR email LIKE '%@premiumwines.pe');

DELETE FROM sedes WHERE negocio_id IN (SELECT id FROM negocios WHERE email LIKE '%@drinkgo.com' OR email LIKE '%@donpepe.com' OR email LIKE '%@labodega.com%' OR email LIKE '%@elimperio.pe' OR email LIKE '%@premiumwines.pe');

DELETE FROM negocios WHERE email LIKE '%@drinkgo.com' OR email LIKE '%@donpepe.com' OR email LIKE '%@labodega.com%' OR email LIKE '%@elimperio.pe' OR email LIKE '%@premiumwines.pe';

DELETE FROM planes_suscripcion WHERE nombre IN ('Plan Básico', 'Plan Profesional', 'Plan Enterprise', 'Plan Free');

DELETE FROM usuarios_plataforma WHERE email IN ('admin@drinkgo.com', 'soporte@drinkgo.com', 'visualizador@drinkgo.com');
```

Luego volver a ejecutar los seeds en orden.

## ⚠️ Notas Importantes

1. **Orden de Ejecución**: Los seeds DEBEN ejecutarse en orden (01, 02, 03, 04) debido a las dependencias entre tablas

2. **Contraseñas**: Las contraseñas están hasheadas con BCrypt (fortaleza 10) compatible con Spring Security

3. **Base de Datos**: Asegúrate de que la base de datos `licores_drinkgo` existe antes de ejecutar los seeds

4. **Esquema**: Si es la primera vez, ejecuta primero el script de creación de tablas:
   ```bash
   mysql -u root -p < DrinkGo-backend/src/main/resources/bd/drinkgo_database.sql
   ```

5. **Configuración Backend**: Verifica que `application.properties` tenga la configuración correcta de conexión a MySQL

## 📝 Próximos Pasos

Después de ejecutar los seeds:

1. ✅ Iniciar el backend Spring Boot
2. ✅ Iniciar el frontend React
3. ✅ Navegar a http://localhost:5173
4. ✅ Hacer login con `admin@drinkgo.com` / `Admin123!`
5. ✅ Probar crear un nuevo negocio desde el módulo "Negocios"
6. ✅ Asignar un plan de suscripción
7. ✅ Explorar todos los módulos (Dashboard, Planes, Facturación, etc.)

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
