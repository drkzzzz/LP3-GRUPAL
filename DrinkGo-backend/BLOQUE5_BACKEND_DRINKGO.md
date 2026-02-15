# BLOQUE 5 — Backend de Inventario, Lotes y Movimientos (DrinkGo)

## 📋 Información General

| Campo | Descripción |
|-------|-------------|
| **Proyecto** | DrinkGo - Sistema SaaS Multi-Tenant |
| **Bloque** | 5 - Inventario, Lotes y Movimientos |
| **Framework** | Spring Boot 4.0.2 |
| **Java** | 17 |
| **Base de datos** | MySQL (XAMPP) - `drinkgo_db` |
| **Autenticación** | JWT con validación en tabla `sesiones_usuario` |
| **ORM** | Spring Data JPA (Hibernate) |

---

## 🏗️ Arquitectura del Proyecto

```
DrinkGo.DrinkGo_backend/
├── controller/          ← Controladores REST
│   ├── AuthController.java
│   ├── StockInventarioController.java
│   ├── LoteInventarioController.java
│   ├── MovimientoInventarioController.java
│   ├── TransferenciaInventarioController.java
│   └── AlertaInventarioController.java
├── service/             ← Lógica de negocio
│   ├── AuthService.java
│   ├── StockInventarioService.java
│   ├── LoteInventarioService.java
│   ├── MovimientoInventarioService.java
│   ├── TransferenciaInventarioService.java
│   └── AlertaInventarioService.java
├── repository/          ← Repositorios JPA
│   ├── UsuarioRepository.java
│   ├── NegocioRepository.java
│   ├── SesionUsuarioRepository.java
│   ├── UsuarioRolRepository.java
│   ├── StockInventarioRepository.java
│   ├── LoteInventarioRepository.java
│   ├── MovimientoInventarioRepository.java
│   ├── TransferenciaInventarioRepository.java
│   ├── AlertaInventarioRepository.java
│   ├── AlmacenRepository.java
│   └── ProductoRepository.java
├── entity/              ← Entidades JPA (mapeo exacto del SQL)
│   ├── Negocio.java
│   ├── Usuario.java
│   ├── Rol.java
│   ├── UsuarioRol.java
│   ├── SesionUsuario.java
│   ├── Almacen.java
│   ├── Producto.java
│   ├── StockInventario.java
│   ├── LoteInventario.java
│   ├── MovimientoInventario.java
│   ├── TransferenciaInventario.java
│   ├── DetalleTransferenciaInventario.java
│   └── AlertaInventario.java
├── dto/                 ← Objetos de transferencia
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ErrorResponse.java
│   ├── StockInventarioRequest.java
│   ├── StockInventarioResponse.java
│   ├── LoteInventarioRequest.java
│   ├── LoteInventarioResponse.java
│   ├── MovimientoInventarioRequest.java
│   ├── MovimientoInventarioResponse.java
│   ├── TransferenciaInventarioRequest.java
│   ├── TransferenciaInventarioResponse.java
│   ├── DetalleTransferenciaRequest.java
│   ├── DetalleTransferenciaResponse.java
│   └── AlertaInventarioResponse.java
├── security/            ← Seguridad JWT
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   ├── SecurityConfig.java
│   └── UsuarioAutenticado.java
└── exception/           ← Manejo de errores
    ├── RecursoNoEncontradoException.java
    ├── StockInsuficienteException.java
    ├── OperacionInvalidaException.java
    └── GlobalExceptionHandler.java
```

---

## 🔐 Sistema de Autenticación JWT

### Flujo Completo de Login

1. **El cliente** envía `POST /restful/token` con email y contraseña
2. **AuthService** busca al usuario por email (activo, no eliminado)
3. **Valida** la contraseña con BCrypt
4. **Verifica** que el usuario no esté bloqueado (`bloqueado_hasta`)
5. **Obtiene** el `negocio_id` del usuario y valida que el negocio esté activo
6. **Consulta** el rol del usuario en `usuarios_roles`
7. **Genera** un JWT con claims: `sub=usuario_id`, `negocio_id`, `rol`
8. **Calcula** el hash SHA-256 del token
9. **Inserta** el registro en la tabla `sesiones_usuario` (nunca se guarda el token en texto plano)
10. **Retorna** el token al cliente

### Claims del JWT

```json
{
  "sub": "1",           // usuario_id
  "negocio_id": 1,      // ID del negocio (tenant)
  "rol": "admin",       // slug del rol
  "iat": 1700000000,    // fecha de emisión
  "exp": 1700086400     // fecha de expiración (24h)
}
```

### Validación en Cada Request (JwtFilter)

En cada petición protegida, el `JwtFilter` realiza **5 validaciones**:

1. **Token válido**: verifica firma y expiración del JWT
2. **Sesión activa**: busca el hash SHA-256 en `sesiones_usuario` donde `esta_activo = true` y `expira_en > NOW()`
3. **Usuario activo**: verifica `esta_activo = true`, `eliminado_en IS NULL`, `bloqueado_hasta IS NULL o < NOW()`
4. **Negocio activo**: verifica `estado = 'activo'`, `esta_activo = true`, `eliminado_en IS NULL`
5. **Actualiza** `ultima_actividad_en` en la sesión

### Multi-Tenant

**REGLA FUNDAMENTAL**: El `negocio_id` **NUNCA** se obtiene del JSON del body. Siempre se extrae del JWT token. Esto garantiza el aislamiento de datos entre negocios (tenants).

---

## 📦 Tablas del Bloque 5

### stock_inventario
Almacena la cantidad actual de cada producto en cada almacén.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT PK AUTO_INCREMENT | Identificador |
| negocio_id | BIGINT FK | Tenant |
| producto_id | BIGINT FK | Producto |
| almacen_id | BIGINT FK | Almacén |
| cantidad_en_mano | INT DEFAULT 0 | Stock físico |
| cantidad_reservada | INT DEFAULT 0 | Reservado para pedidos |
| cantidad_disponible | INT **GENERATED** | `cantidad_en_mano - cantidad_reservada` |
| ultimo_conteo_en | TIMESTAMP | Último inventario físico |
| ultimo_movimiento_en | TIMESTAMP | Último movimiento registrado |

> **Nota**: `cantidad_disponible` es una columna **GENERATED ALWAYS** por MySQL. No se inserta ni actualiza — se calcula automáticamente.

### lotes_inventario
Control de lotes con sistema FIFO.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT PK | Identificador |
| negocio_id | BIGINT FK | Tenant |
| producto_id | BIGINT FK | Producto |
| almacen_id | BIGINT FK | Almacén |
| numero_lote | VARCHAR(50) UNIQUE por negocio | Código del lote |
| cantidad_inicial | INT | Cantidad al recibir |
| cantidad_restante | INT | Cantidad actual |
| precio_compra | DECIMAL(12,2) | Costo unitario |
| fecha_fabricacion | DATE | Fecha de fabricación |
| fecha_vencimiento | DATE | Fecha de vencimiento |
| fecha_recepcion | DATE | **Clave para FIFO** |
| estado | ENUM | disponible, agotado, vencido, cuarentena, devuelto |

### movimientos_inventario
Registro inmutable de todas las entradas y salidas.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT PK | Identificador |
| negocio_id | BIGINT FK | Tenant |
| producto_id | BIGINT FK | Producto |
| almacen_id | BIGINT FK | Almacén |
| lote_id | BIGINT FK (nullable) | Lote referenciado |
| tipo_movimiento | ENUM | 14 tipos posibles |
| cantidad | INT | Cantidad movida |
| costo_unitario | DECIMAL(12,2) | Costo por unidad |
| realizado_por | BIGINT FK | Usuario que ejecutó |

**Tipos de movimiento**: `entrada_compra`, `salida_venta`, `entrada_devolucion`, `salida_devolucion`, `entrada_transferencia`, `salida_transferencia`, `ajuste_entrada`, `ajuste_salida`, `merma`, `rotura`, `vencimiento`, `stock_inicial`, `entrada_produccion`, `salida_produccion`

### transferencias_inventario
Transferencias entre almacenes con flujo de estados.

| Estado | Descripción |
|--------|-------------|
| `borrador` | Creada, aún no aprobada |
| `pendiente` | Aprobada, esperando despacho |
| `en_transito` | Despachada, stock descontado de origen |
| `recibida` | Recibida en destino, stock incrementado |
| `cancelada` | Cancelada (borrado lógico) |

### alertas_inventario
Alertas automáticas generadas por el sistema.

| Tipo | Condición |
|------|-----------|
| `stock_bajo` | `cantidad_en_mano <= stock_minimo` del producto |
| `sobrestock` | `cantidad_en_mano > stock_maximo` del producto |
| `punto_reorden` | `cantidad_en_mano <= punto_reorden` del producto |
| `proximo_vencer` | Lote vence en los próximos 30 días |
| `vencido` | Lote ya venció, se cambia a estado `vencido` |

---

## 🔄 Lógica FIFO (First In, First Out)

### ¿Qué es FIFO?

FIFO significa **"Primero en Entrar, Primero en Salir"**. Se consumen primero los lotes más antiguos (por `fecha_recepcion`).

### ¿Cómo funciona en DrinkGo?

```
Ejemplo: Producto "Cerveza Artesanal" tiene 3 lotes en stock:

LOTE A → fecha_recepcion: 2024-01-15 → cantidad_restante: 20 ← Se consume primero
LOTE B → fecha_recepcion: 2024-02-20 → cantidad_restante: 50
LOTE C → fecha_recepcion: 2024-03-10 → cantidad_restante: 30

Si se hace una salida de 60 unidades:
  1. LOTE A: se descuentan 20 (queda 0 → estado = 'agotado')
  2. LOTE B: se descuentan 40 (queda 10 → sigue 'disponible')
  3. LOTE C: no se toca (aún hay suficiente con A+B)
```

### Query FIFO en el Repositorio

```java
findByProductoIdAndAlmacenIdAndNegocioIdAndEstadoAndCantidadRestanteGreaterThan
    OrderByFechaRecepcionAsc(
        productoId, almacenId, negocioId,
        EstadoLote.disponible, 0);
```

**Condiciones**: `estado = 'disponible'` AND `cantidad_restante > 0`
**Orden**: `fecha_recepcion ASC` (más antiguo primero)

### ¿Dónde se aplica?

1. **MovimientoInventarioService**: cualquier movimiento de tipo *salida*
2. **TransferenciaInventarioService**: al despachar (descontar de almacén origen)
3. **LoteInventarioService**: método `consumirFIFO()` para descuentos directos

---

## 🌐 Endpoints REST

### 🔓 Autenticación (Público)

| Método | Ruta | Descripción |
|--------|------|-------------|
| `POST` | `/restful/token` | Login - Obtener token JWT |

### 📦 Stock de Inventario

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/restful/stock` | Listar todo el stock |
| `GET` | `/restful/stock/{id}` | Obtener stock por ID |
| `POST` | `/restful/stock` | Crear registro de stock |
| `PUT` | `/restful/stock/{id}` | Actualizar stock |
| `DELETE` | `/restful/stock/{id}` | Borrado lógico (cantidades a 0) |

### 📋 Lotes de Inventario

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/restful/lotes` | Listar todos los lotes |
| `GET` | `/restful/lotes/{id}` | Obtener lote por ID |
| `POST` | `/restful/lotes` | Registrar entrada de lote |
| `PUT` | `/restful/lotes/{id}` | Actualizar datos del lote |
| `DELETE` | `/restful/lotes/{id}` | Borrado lógico (estado → agotado) |

### 🔄 Movimientos de Inventario

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/restful/movimientos` | Listar todos los movimientos |
| `GET` | `/restful/movimientos/{id}` | Obtener movimiento por ID |
| `POST` | `/restful/movimientos` | Registrar movimiento (inmutable) |

> **Nota**: Los movimientos son **inmutables** — no existen endpoints PUT ni DELETE.

### 🚚 Transferencias entre Almacenes

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/restful/transferencias` | Listar todas las transferencias |
| `GET` | `/restful/transferencias/{id}` | Obtener transferencia por ID |
| `POST` | `/restful/transferencias` | Crear transferencia (borrador) |
| `PUT` | `/restful/transferencias/{id}/despachar` | Despachar (FIFO en origen) |
| `PUT` | `/restful/transferencias/{id}/recibir` | Recibir en destino |
| `DELETE` | `/restful/transferencias/{id}` | Cancelar transferencia |

### ⚠️ Alertas de Inventario

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/restful/alertas` | Listar todas las alertas |
| `GET` | `/restful/alertas/activas` | Listar alertas no resueltas |
| `GET` | `/restful/alertas/{id}` | Obtener alerta por ID |
| `PUT` | `/restful/alertas/{id}/resolver` | Resolver alerta |

---

## 🧪 Guía de Pruebas en Postman

### Requisitos Previos

1. **XAMPP** con MySQL encendido
2. **Base de datos** `drinkgo_db` creada con el script `drinkgo_database.sql`
3. **Datos mínimos**: Al menos un negocio, un usuario con contraseña BCrypt, un rol asignado, un almacén y un producto
4. **Aplicación** corriendo en `http://localhost:8080`

### Insertar Datos de Prueba en MySQL

Ejecuta estos 8 INSERT en orden en tu cliente MySQL (phpMyAdmin, MySQL Workbench, terminal, etc):

```sql
-- 1. Insertar un negocio
INSERT INTO negocios (uuid, razon_social, nombre_comercial, tipo_negocio, email, estado, esta_activo)
VALUES (UUID(), 'DrinkGo SAC', 'DrinkGo', 'restaurante', 'info@drinkgo.com', 'activo', 1);

-- 2. Insertar un rol
INSERT INTO roles (negocio_id, nombre, slug, descripcion, es_rol_sistema, esta_activo)
VALUES (1, 'Administrador', 'admin', 'Rol de administrador del sistema', 0, 1);

-- 3. Insertar un usuario (contraseña: 123456)
INSERT INTO usuarios (uuid, negocio_id, email, hash_contrasena, nombres, apellidos, idioma, esta_activo)
VALUES (UUID(), 1, 'admin@drinkgo.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92Y.nGPhLxVmRaxgLEaO6',
    'Admin', 'DrinkGo', 'es', 1);

-- 4. Asignar rol al usuario
INSERT INTO usuarios_roles (usuario_id, rol_id)
VALUES (1, 1);

-- 5. Insertar una sede (requerida para almacenes)
INSERT INTO sedes (negocio_id, codigo, nombre, direccion, pais, esta_activo)
VALUES (1, 'SEDE-001', 'Sede Principal', 'Calle Principal 123', 'PE', 1);

-- 6. Insertar almacén principal
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, tipo_almacenamiento, esta_activo)
VALUES (1, 1, 'ALM-001', 'Almacén Principal', 'ambiente', 1);

-- 7. Insertar almacén secundario (para pruebas de transferencia)
INSERT INTO almacenes (negocio_id, sede_id, codigo, nombre, tipo_almacenamiento, esta_activo)
VALUES (1, 1, 'ALM-002', 'Almacén Secundario', 'frio', 1);

-- 8. Insertar un producto
INSERT INTO productos (negocio_id, sku, nombre, slug, precio_compra, precio_venta, 
    stock_minimo, stock_maximo, punto_reorden, es_perecible, dias_vida_util, esta_activo)
VALUES (1, 'PROD-001', 'Cerveza Artesanal 500ml', 'cerveza-artesanal-500ml', 
    8.50, 15.00, 10, 500, 20, 1, 180, 1);
```

> **IMPORTANTE**: 
> - El hash BCrypt `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92Y.nGPhLxVmRaxgLEaO6` corresponde a la contraseña `123456`
> - Los `tipo_almacenamiento` válidos son: `ambiente`, `frio`, `congelado`, `mixto`
> - No especifies campos como `creado_en` ni `actualizado_en` — MySQL los llena automáticamente con DEFAULT/CURRENT_TIMESTAMP
> - Ejecuta los 8 INSERT en orden exacto — cada uno depende del anterior (IDs autoincrementales: 1, 1, 1, 1, 1, 1, 1, 1)

### Paso 1: Obtener Token JWT

**Request:**
```
POST http://localhost:8080/restful/token
Content-Type: application/json

{
    "email": "admin@drinkgo.com",
    "password": "123456"
}
```

**Response esperado (200 OK):**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "usuarioId": 1,
    "negocioId": 1,
    "nombreUsuario": "Admin DrinkGo",
    "rol": "admin",
    "mensaje": "Login exitoso"
}
```

**Guardar el `accessToken`** para usarlo en todas las siguientes peticiones.

### Paso 2: Configurar Autenticación en Postman

En cada petición protegida, agregar el header:
```
Authorization: Bearer <tu_access_token>
```

O en la pestaña **Authorization** de Postman:
- Type: **Bearer Token**
- Token: pegar el `accessToken` obtenido

---

### Paso 3: Operaciones de Stock

#### Crear stock inicial
```
POST http://localhost:8080/restful/stock
Authorization: Bearer <token>
Content-Type: application/json

{
    "productoId": 1,
    "almacenId": 1,
    "cantidadEnMano": 100,
    "cantidadReservada": 0
}
```

#### Listar stock
```
GET http://localhost:8080/restful/stock
Authorization: Bearer <token>
```

#### Actualizar stock
```
PUT http://localhost:8080/restful/stock/1
Authorization: Bearer <token>
Content-Type: application/json

{
    "productoId": 1,
    "almacenId": 1,
    "cantidadEnMano": 150,
    "cantidadReservada": 10
}
```

---

### Paso 4: Operaciones de Lotes

#### Registrar lote (entrada de inventario)
```
POST http://localhost:8080/restful/lotes
Authorization: Bearer <token>
Content-Type: application/json

{
    "productoId": 1,
    "almacenId": 1,
    "numeroLote": "LOTE-2024-001",
    "cantidadInicial": 50,
    "precioCompra": 8.50,
    "fechaFabricacion": "2024-01-15",
    "fechaVencimiento": "2024-07-15",
    "fechaRecepcion": "2024-02-01",
    "notas": "Primer lote del proveedor"
}
```

#### Registrar segundo lote (para probar FIFO)
```
POST http://localhost:8080/restful/lotes
Authorization: Bearer <token>
Content-Type: application/json

{
    "productoId": 1,
    "almacenId": 1,
    "numeroLote": "LOTE-2024-002",
    "cantidadInicial": 80,
    "precioCompra": 9.00,
    "fechaFabricacion": "2024-03-01",
    "fechaVencimiento": "2024-09-01",
    "fechaRecepcion": "2024-03-15",
    "notas": "Segundo lote - Verificar FIFO"
}
```

#### Listar lotes
```
GET http://localhost:8080/restful/lotes
Authorization: Bearer <token>
```

---

### Paso 5: Movimientos de Inventario

#### Registrar salida (activa FIFO automáticamente)
```
POST http://localhost:8080/restful/movimientos
Authorization: Bearer <token>
Content-Type: application/json

{
    "productoId": 1,
    "almacenId": 1,
    "tipoMovimiento": "salida_venta",
    "cantidad": 60,
    "costoUnitario": 8.50,
    "motivo": "Venta del día - Pedido #123"
}
```

> **Resultado FIFO**: Se consumirán primero las 50 unidades del LOTE-2024-001 (queda agotado) y luego 10 del LOTE-2024-002 (queda con 70).

#### Registrar entrada (ajuste manual)
```
POST http://localhost:8080/restful/movimientos
Authorization: Bearer <token>
Content-Type: application/json

{
    "productoId": 1,
    "almacenId": 1,
    "tipoMovimiento": "ajuste_entrada",
    "cantidad": 20,
    "motivo": "Ajuste por inventario físico"
}
```

#### Listar movimientos
```
GET http://localhost:8080/restful/movimientos
Authorization: Bearer <token>
```

---

### Paso 6: Transferencias entre Almacenes

#### Crear transferencia
```
POST http://localhost:8080/restful/transferencias
Authorization: Bearer <token>
Content-Type: application/json

{
    "almacenOrigenId": 1,
    "almacenDestinoId": 2,
    "notas": "Reabastecimiento de almacén secundario",
    "detalles": [
        {
            "productoId": 1,
            "cantidadSolicitada": 30,
            "notas": "Cerveza Artesanal para bar"
        }
    ]
}
```

#### Despachar transferencia (descuenta FIFO en origen)
```
PUT http://localhost:8080/restful/transferencias/1/despachar
Authorization: Bearer <token>
```

#### Recibir transferencia (incrementa stock en destino)
```
PUT http://localhost:8080/restful/transferencias/1/recibir
Authorization: Bearer <token>
```

#### Cancelar transferencia (solo en borrador/pendiente)
```
DELETE http://localhost:8080/restful/transferencias/1
Authorization: Bearer <token>
```

---

### Paso 7: Alertas de Inventario

#### Listar alertas activas
```
GET http://localhost:8080/restful/alertas/activas
Authorization: Bearer <token>
```

#### Resolver alerta
```
PUT http://localhost:8080/restful/alertas/1/resolver
Authorization: Bearer <token>
```

---

## ⚠️ Manejo de Errores

Todas las respuestas de error siguen el formato estándar:

```json
{
    "status": 404,
    "error": "No Encontrado",
    "mensaje": "Stock de inventario con ID 99 no encontrado",
    "timestamp": "2024-12-01T10:30:00"
}
```

### Códigos de Error

| Código | Excepción | Cuando se lanza |
|--------|-----------|-----------------|
| 400 | `OperacionInvalidaException` | Operación no valid (producto duplicado, transferencia al mismo almacén, etc.) |
| 400 | `StockInsuficienteException` | No hay stock suficiente para la operación |
| 400 | `MethodArgumentNotValidException` | Validación de campos (@NotNull, @Min, @Email) |
| 401 | — | Token JWT inválido, expirado o sesión inactiva |
| 404 | `RecursoNoEncontradoException` | Recurso no encontrado o no pertenece al negocio |
| 500 | `Exception` | Error interno del servidor |

---

## 🔒 Reglas de Seguridad Multi-Tenant

1. **Todas las queries** incluyen `WHERE negocio_id = :negocioId`
2. **El negocio_id** se obtiene del JWT token, nunca del body JSON
3. **Productos y almacenes** se validan contra el negocio antes de usarlos
4. **No se pueden ver** datos de otros negocios bajo ninguna circunstancia
5. **Las sesiones** se validan en cada request contra `sesiones_usuario`
6. **El endpoint `/restful/token`** es el único público (sin autenticación)

---

## 📝 Borrado Lógico por Tabla

| Tabla | Mecanismo de borrado lógico |
|-------|---------------------------|
| `stock_inventario` | `cantidad_en_mano = 0, cantidad_reservada = 0` (no tiene columna de eliminación) |
| `lotes_inventario` | `estado = 'agotado'` |
| `movimientos_inventario` | **No se eliminan** (inmutables) |
| `transferencias_inventario` | `estado = 'cancelada'` |
| `alertas_inventario` | `esta_resuelta = true, resuelta_en = NOW()` |

---

## 🛠️ Cómo Ejecutar

### 1. Preparar la base de datos
```bash
# En XAMPP, iniciar MySQL
# Crear la base de datos y ejecutar el script SQL
mysql -u root -e "CREATE DATABASE IF NOT EXISTS drinkgo_db;"
mysql -u root drinkgo_db < src/main/resources/bd/drinkgo_database.sql
```

### 2. Compilar y ejecutar
```bash
cd DrinkGo-backend
./mvnw spring-boot:run
```

### 3. Verificar
- La aplicación arranca en `http://localhost:8080`
- Probar con `POST http://localhost:8080/restful/token`

---

## 📊 Resumen de Archivos Creados

| Capa | Cantidad | Archivos |
|------|----------|----------|
| Entidades | 13 | 7 referencia + 6 Bloque 5 |
| Repositorios | 11 | 4 referencia + 7 Bloque 5 |
| DTOs | 16 | 2 auth + 14 inventario |
| Servicios | 6 | 1 auth + 5 inventario |
| Controladores | 6 | 1 auth + 5 inventario |
| Seguridad | 4 | JwtUtil, JwtFilter, SecurityConfig, UsuarioAutenticado |
| Excepciones | 4 | 3 excepciones + GlobalExceptionHandler |
| **Total** | **60** | archivos Java |

---

## 📌 Notas Importantes

1. **No se inventaron tablas**: todas las entidades mapean tablas existentes en `drinkgo_database.sql`
2. **No se modificaron columnas**: los `@Column(name = ...)` corresponden exactamente al SQL
3. **Hibernate DDL**: configurado en `validate` (solo valida, no modifica la BD)
4. **BCrypt**: la contraseña de prueba `123456` corresponde al hash proporcionado en los datos de prueba
5. **Token SHA-256**: el token JWT nunca se almacena en texto plano en `sesiones_usuario`, solo su hash SHA-256
6. **Spring Boot 4.0.2**: usa `jakarta.*` (no `javax.*`) y `spring-boot-starter-webmvc` (no `spring-boot-starter-web`)
