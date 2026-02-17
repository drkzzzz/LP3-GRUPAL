# GUÍA DE PRUEBAS POSTMAN - BLOQUE 8: VENTAS, POS Y CAJAS

## INFORMACIÓN GENERAL
- **Base URL**: `http://localhost:8080`
- **Autenticación**: JWT Token (obligatorio)
- **Headers comunes**: 
  - `Content-Type: application/json`
  - `Authorization: Bearer {token}` (obtener de `/api/auth/login`)

---

## 🔐 PASO 0: OBTENER TOKEN JWT

### Request - Login
```
POST http://localhost:8080/api/auth/login
```

### Body (JSON)
```json
{
  "username": "admin@drinkgo.com",
  "password": "admin123"
}
```

### Response Esperada (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "nombres": "Admin",
    "apellidos": "DrinkGo"
  }
}
```

**⚠️ IMPORTANTE**: Copiar el `token` y agregarlo en el header `Authorization: Bearer {token}` para todas las siguientes peticiones.

---

## 📋 CHECKLIST DE PRUEBAS

### ✅ Antes de iniciar:
1. Servidor Spring Boot corriendo en puerto 8080
2. Base de datos MySQL iniciada (XAMPP) con datos de prueba
3. Token JWT válido obtenido
4. Verificar tablas: `cajas_registradoras`, `sesiones_caja`, `movimientos_caja`, `ventas`, `detalle_venta`, `pagos_venta`

---

## 1️⃣ MÓDULO: CAJAS REGISTRADORAS

### 1.1 GET - Listar todas las cajas

```
GET http://localhost:8080/api/cajas
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "sedeNombre": "Sede Centro",
    "nombreCaja": "Caja Principal",
    "codigoCaja": "CAJA-001",
    "estaActivo": true,
    "creadoEn": "2026-02-01T08:00:00",
    "actualizadoEn": "2026-02-01T08:00:00"
  }
]
```

---

### 1.2 GET - Listar cajas por sede

```
GET http://localhost:8080/api/cajas/sede/1
Authorization: Bearer {token}
```

---

### 1.3 GET - Listar cajas activas

```
GET http://localhost:8080/api/cajas/activas
Authorization: Bearer {token}
```

---

### 1.4 GET - Obtener caja por ID

```
GET http://localhost:8080/api/cajas/1
Authorization: Bearer {token}
```

---

### 1.5 POST - Crear nueva caja registradora

```
POST http://localhost:8080/api/cajas
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "sedeId": 1,
  "nombreCaja": "Caja Express",
  "codigoCaja": "CAJA-005",
  "estaActivo": true
}
```

**Response Esperada (201 CREATED)**
```json
{
  "mensaje": "Caja registradora creada exitosamente",
  "caja": {
    "id": 7,
    "negocioId": 1,
    "sedeId": 1,
    "sedeNombre": "Sede Centro",
    "nombreCaja": "Caja Express",
    "codigoCaja": "CAJA-005",
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:30:00",
    "actualizadoEn": "2026-02-16T10:30:00"
  }
}
```

---

### 1.6 PUT - Actualizar caja registradora

```
PUT http://localhost:8080/api/cajas/7
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "nombreCaja": "Caja Express 2",
  "estaActivo": true
}
```

---

### 1.7 DELETE - Eliminar caja (soft delete)

```
DELETE http://localhost:8080/api/cajas/7
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
{
  "mensaje": "Caja registradora eliminada exitosamente"
}
```

---

## 2️⃣ MÓDULO: SESIONES DE CAJA

### 2.1 GET - Listar todas las sesiones

```
GET http://localhost:8080/api/sesiones
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "cajaId": 1,
    "cajaNombre": "Caja Principal",
    "usuarioAperturaId": 1,
    "usuarioAperturaNombre": "Admin DrinkGo",
    "usuarioCierreId": null,
    "usuarioCierreNombre": null,
    "montoInicial": 200.00,
    "montoFinal": null,
    "montoEsperado": null,
    "diferencia": null,
    "observacionesApertura": "Turno mañana",
    "observacionesCierre": null,
    "estaAbierta": true,
    "abiertaEn": "2026-02-16T08:00:00",
    "cerradaEn": null,
    "creadoEn": "2026-02-16T08:00:00",
    "actualizadoEn": "2026-02-16T08:00:00"
  }
]
```

---

### 2.2 GET - Listar sesiones por caja

```
GET http://localhost:8080/api/sesiones/caja/1
Authorization: Bearer {token}
```

---

### 2.3 GET - Listar sesiones abiertas

```
GET http://localhost:8080/api/sesiones/abiertas
Authorization: Bearer {token}
```

---

### 2.4 GET - Listar sesiones por fecha

```
GET http://localhost:8080/api/sesiones/fecha/2026-02-16
Authorization: Bearer {token}
```

---

### 2.5 GET - Obtener sesión por ID

```
GET http://localhost:8080/api/sesiones/1
Authorization: Bearer {token}
```

---

### 2.6 GET - Obtener sesión abierta de una caja

```
GET http://localhost:8080/api/sesiones/caja/1/abierta
Authorization: Bearer {token}
```

---

### 2.7 POST - Abrir sesión de caja

```
POST http://localhost:8080/api/sesiones/abrir
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "cajaId": 2,
  "montoInicial": 300.00,
  "observacionesApertura": "Turno tarde - iniciando con fondo de caja"
}
```

**Response Esperada (201 CREATED)**
```json
{
  "mensaje": "Sesión de caja abierta exitosamente",
  "sesion": {
    "id": 6,
    "negocioId": 1,
    "cajaId": 2,
    "cajaNombre": "Caja 2",
    "usuarioAperturaId": 1,
    "usuarioAperturaNombre": "Admin DrinkGo",
    "montoInicial": 300.00,
    "observacionesApertura": "Turno tarde - iniciando con fondo de caja",
    "estaAbierta": true,
    "abiertaEn": "2026-02-16T14:00:00",
    "creadoEn": "2026-02-16T14:00:00"
  }
}
```

---

### 2.8 PUT - Cerrar sesión de caja

```
PUT http://localhost:8080/api/sesiones/6/cerrar
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "montoFinalContado": 1450.50,
  "observacionesCierre": "Cierre turno tarde - todo correcto"
}
```

**Response Esperada (200 OK)**
```json
{
  "mensaje": "Sesión de caja cerrada exitosamente",
  "sesion": {
    "id": 6,
    "negocioId": 1,
    "cajaId": 2,
    "cajaNombre": "Caja 2",
    "usuarioAperturaId": 1,
    "usuarioAperturaNombre": "Admin DrinkGo",
    "usuarioCierreId": 1,
    "usuarioCierreNombre": "Admin DrinkGo",
    "montoInicial": 300.00,
    "montoFinal": 1450.50,
    "montoEsperado": 1450.50,
    "diferencia": 0.00,
    "observacionesApertura": "Turno tarde - iniciando con fondo de caja",
    "observacionesCierre": "Cierre turno tarde - todo correcto",
    "estaAbierta": false,
    "abiertaEn": "2026-02-16T14:00:00",
    "cerradaEn": "2026-02-16T22:00:00",
    "creadoEn": "2026-02-16T14:00:00",
    "actualizadoEn": "2026-02-16T22:00:00"
  }
}
```

---

### 2.9 DELETE - Eliminar sesión (solo si está sin ventas)

```
DELETE http://localhost:8080/api/sesiones/6
Authorization: Bearer {token}
```

---

## 3️⃣ MÓDULO: MOVIMIENTOS DE CAJA

### 3.1 GET - Listar todos los movimientos

```
GET http://localhost:8080/api/movimientos-caja
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sesionId": 1,
    "tipoMovimiento": "INGRESO",
    "monto": 150.00,
    "concepto": "Venta de contado",
    "realizadoPorUsuarioId": 1,
    "realizadoPorUsuarioNombre": "Admin DrinkGo",
    "creadoEn": "2026-02-16T09:30:00"
  }
]
```

---

### 3.2 GET - Listar movimientos por sesión

```
GET http://localhost:8080/api/movimientos-caja/sesion/1
Authorization: Bearer {token}
```

---

### 3.3 GET - Listar movimientos por sesión y tipo

```
GET http://localhost:8080/api/movimientos-caja/sesion/1/tipo/INGRESO
Authorization: Bearer {token}
```

**Tipos válidos**: `INGRESO`, `EGRESO`, `AJUSTE`

---

### 3.4 GET - Listar movimientos en rango de fechas

```
GET http://localhost:8080/api/movimientos-caja/rango?fechaInicio=2026-02-01&fechaFin=2026-02-16
Authorization: Bearer {token}
```

---

### 3.5 GET - Obtener movimiento por ID

```
GET http://localhost:8080/api/movimientos-caja/1
Authorization: Bearer {token}
```

---

### 3.6 POST - Crear movimiento de caja (INGRESO)

```
POST http://localhost:8080/api/movimientos-caja
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "sesionId": 1,
  "tipoMovimiento": "INGRESO",
  "monto": 250.50,
  "concepto": "Pago de cliente moroso"
}
```

**Response Esperada (201 CREATED)**
```json
{
  "mensaje": "Movimiento de caja registrado exitosamente",
  "movimiento": {
    "id": 9,
    "negocioId": 1,
    "sesionId": 1,
    "tipoMovimiento": "INGRESO",
    "monto": 250.50,
    "concepto": "Pago de cliente moroso",
    "realizadoPorUsuarioId": 1,
    "realizadoPorUsuarioNombre": "Admin DrinkGo",
    "creadoEn": "2026-02-16T15:45:00"
  }
}
```

---

### 3.7 POST - Crear movimiento de caja (EGRESO)

```
POST http://localhost:8080/api/movimientos-caja
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "sesionId": 1,
  "tipoMovimiento": "EGRESO",
  "monto": 80.00,
  "concepto": "Compra de cambio en monedas"
}
```

---

### 3.8 PUT - Actualizar movimiento

```
PUT http://localhost:8080/api/movimientos-caja/9
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "monto": 260.00,
  "concepto": "Pago de cliente moroso - corregido"
}
```

---

### 3.9 DELETE - Eliminar movimiento (solo si sesión está abierta)

```
DELETE http://localhost:8080/api/movimientos-caja/9
Authorization: Bearer {token}
```

---

## 4️⃣ MÓDULO: VENTAS

### 4.1 GET - Listar todas las ventas

```
GET http://localhost:8080/api/ventas
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "sedeNombre": "Sede Centro",
    "codigoVenta": "VEN-1-20260216100000",
    "sesionId": 1,
    "clienteId": 1,
    "clienteNombre": "Juan Pérez",
    "vendedorId": 1,
    "vendedorNombre": "Admin DrinkGo",
    "tipoVenta": "LOCAL",
    "canalVenta": "POS",
    "mesaId": null,
    "mesaNumero": null,
    "pedidoId": null,
    "subtotal": 100.00,
    "descuentoMonto": 0.00,
    "impuestoMonto": 18.00,
    "costoDelivery": 0.00,
    "propina": 0.00,
    "total": 118.00,
    "estado": "CONFIRMADA",
    "tipoComprobante": "BOLETA",
    "numeroComprobante": "B001-00001",
    "observaciones": null,
    "direccionEntrega": null,
    "telefonoEntrega": null,
    "nombreClienteManual": null,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### 4.2 GET - Listar ventas por sede

```
GET http://localhost:8080/api/ventas/sede/1
Authorization: Bearer {token}
```

---

### 4.3 GET - Listar ventas por sesión

```
GET http://localhost:8080/api/ventas/sesion/1
Authorization: Bearer {token}
```

---

### 4.4 GET - Listar ventas por cliente

```
GET http://localhost:8080/api/ventas/cliente/1
Authorization: Bearer {token}
```

---

### 4.5 GET - Listar ventas por vendedor

```
GET http://localhost:8080/api/ventas/vendedor/1
Authorization: Bearer {token}
```

---

### 4.6 GET - Listar ventas por tipo

```
GET http://localhost:8080/api/ventas/tipo/LOCAL
Authorization: Bearer {token}
```

**Tipos válidos**: `LOCAL`, `DELIVERY`, `PICK_UP`, `MARKETPLACE`

---

### 4.7 GET - Listar ventas por estado

```
GET http://localhost:8080/api/ventas/estado/CONFIRMADA
Authorization: Bearer {token}
```

**Estados válidos**: `PENDIENTE`, `CONFIRMADA`, `EN_PROCESO`, `ANULADA`

---

### 4.8 GET - Listar ventas en rango de fechas

```
GET http://localhost:8080/api/ventas/rango?fechaInicio=2026-02-01&fechaFin=2026-02-16
Authorization: Bearer {token}
```

---

### 4.9 GET - Obtener venta por ID (con detalles y pagos)

```
GET http://localhost:8080/api/ventas/1
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
{
  "id": 1,
  "negocioId": 1,
  "sedeId": 1,
  "sedeNombre": "Sede Centro",
  "codigoVenta": "VEN-1-20260216100000",
  "sesionId": 1,
  "clienteId": 1,
  "clienteNombre": "Juan Pérez",
  "vendedorId": 1,
  "vendedorNombre": "Admin DrinkGo",
  "tipoVenta": "LOCAL",
  "canalVenta": "POS",
  "subtotal": 100.00,
  "impuestoMonto": 18.00,
  "total": 118.00,
  "estado": "CONFIRMADA",
  "tipoComprobante": "BOLETA",
  "numeroComprobante": "B001-00001",
  "detalles": [
    {
      "id": 1,
      "productoId": 1,
      "productoNombre": "Whisky Johnnie Walker Red Label 750ml",
      "cantidad": 2.0,
      "precioUnitario": 50.00,
      "descuentoPorItem": 0.00,
      "impuestoPorItem": 0.00,
      "subtotalItem": 100.00,
      "observaciones": null
    }
  ],
  "pagos": [
    {
      "id": 1,
      "metodoPagoId": 1,
      "metodoPagoNombre": "Efectivo",
      "monto": 118.00,
      "moneda": "PEN",
      "referenciaPago": null,
      "estado": "COMPLETADO",
      "notas": null
    }
  ],
  "creadoEn": "2026-02-16T10:00:00"
}
```

---

### 4.10 POST - Crear nueva venta (POS)

```
POST http://localhost:8080/api/ventas
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON) - Venta Simple**
```json
{
  "sedeId": 1,
  "sesionId": 1,
  "clienteId": 1,
  "tipoVenta": "LOCAL",
  "canalVenta": "POS",
  "tipoComprobante": "BOLETA",
  "numeroComprobante": "B001-00010",
  "observaciones": "Venta de mostrador",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 3,
      "precioUnitario": 50.00,
      "descuento": 0.00,
      "observaciones": ""
    },
    {
      "productoId": 2,
      "cantidad": 2,
      "precioUnitario": 35.00,
      "descuento": 0.00,
      "observaciones": ""
    }
  ],
  "pagos": [
    {
      "metodoPagoId": 1,
      "monto": 220.00,
      "referenciaPago": null
    }
  ]
}
```

**Response Esperada (201 CREATED)**
```json
{
  "mensaje": "Venta creada exitosamente",
  "venta": {
    "id": 10,
    "negocioId": 1,
    "sedeId": 1,
    "codigoVenta": "VEN-1-20260216154500",
    "sesionId": 1,
    "clienteId": 1,
    "vendedorId": 1,
    "tipoVenta": "LOCAL",
    "canalVenta": "POS",
    "subtotal": 220.00,
    "impuestoMonto": 0.00,
    "total": 220.00,
    "estado": "CONFIRMADA",
    "tipoComprobante": "BOLETA",
    "numeroComprobante": "B001-00010",
    "detalles": [...],
    "pagos": [...]
  }
}
```

---

### 4.11 POST - Crear venta con pago mixto

**Body (JSON)**
```json
{
  "sedeId": 1,
  "sesionId": 1,
  "clienteId": 1,
  "tipoVenta": "LOCAL",
  "canalVenta": "POS",
  "tipoComprobante": "BOLETA",
  "numeroComprobante": "B001-00011",
  "detalles": [
    {
      "productoId": 5,
      "cantidad": 1,
      "precioUnitario": 250.00,
      "descuento": 0.00
    }
  ],
  "pagos": [
    {
      "metodoPagoId": 1,
      "monto": 100.00,
      "referenciaPago": null
    },
    {
      "metodoPagoId": 2,
      "monto": 150.00,
      "referenciaPago": "TARJETA-1234"
    }
  ]
}
```

---

### 4.12 POST - Crear venta DELIVERY

**Body (JSON)**
```json
{
  "sedeId": 1,
  "sesionId": null,
  "clienteId": 2,
  "tipoVenta": "DELIVERY",
  "canalVenta": "TIENDA_ONLINE",
  "direccionEntrega": "Av. Example 123, Miraflores",
  "telefonoEntrega": "987654321",
  "costoDelivery": 10.00,
  "tipoComprobante": "BOLETA",
  "numeroComprobante": "B001-00012",
  "detalles": [
    {
      "productoId": 3,
      "cantidad": 2,
      "precioUnitario": 40.00
    }
  ],
  "pagos": [
    {
      "metodoPagoId": 3,
      "monto": 90.00,
      "referenciaPago": "TRANS-98765"
    }
  ]
}
```

---

### 4.13 PUT - Anular venta

```
PUT http://localhost:8080/api/ventas/10/anular
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON)**
```json
{
  "motivoAnulacion": "Cliente solicitó cancelación por error en pedido"
}
```

**Response Esperada (200 OK)**
```json
{
  "mensaje": "Venta anulada exitosamente",
  "venta": {
    "id": 10,
    "estado": "ANULADA",
    "observaciones": "Motivo anulación: Cliente solicitó cancelación por error en pedido"
  }
}
```

---

### 4.14 GET - Calcular total ventas de sede en rango

```
GET http://localhost:8080/api/ventas/sede/1/total?fechaInicio=2026-02-01&fechaFin=2026-02-16
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
{
  "sedeId": 1,
  "fechaInicio": "2026-02-01",
  "fechaFin": "2026-02-16",
  "total": 8450.50
}
```

---

### 4.15 GET - Calcular total ventas de sesión

```
GET http://localhost:8080/api/ventas/sesion/1/total
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
{
  "sesionId": 1,
  "total": 2340.00
}
```

---

### 4.16 GET - Calcular total compras de cliente

```
GET http://localhost:8080/api/ventas/cliente/1/total
Authorization: Bearer {token}
```

**Response Esperada (200 OK)**
```json
{
  "clienteId": 1,
  "total": 1890.50
}
```

---

## 🔧 ESCENARIOS DE PRUEBA COMPLETOS

### Escenario 1: Flujo Completo POS (Turno de Caja)

**Paso 1**: Abrir sesión de caja
```
POST /api/sesiones/abrir
{
  "cajaId": 1,
  "montoInicial": 500.00,
  "observacionesApertura": "Turno mañana"
}
```

**Paso 2**: Crear venta 1 (efectivo)
```
POST /api/ventas
{
  "sedeId": 1,
  "sesionId": {ID_SESION_ABIERTA},
  "clienteId": 1,
  "tipoVenta": "LOCAL",
  "detalles": [...],
  "pagos": [{"metodoPagoId": 1, "monto": 150.00}]
}
```

**Paso 3**: Crear venta 2 (tarjeta)
```
POST /api/ventas
{...}
```

**Paso 4**: Registrar ingreso adicional
```
POST /api/movimientos-caja
{
  "sesionId": {ID_SESION_ABIERTA},
  "tipoMovimiento": "INGRESO",
  "monto": 100.00,
  "concepto": "Pago de deuda"
}
```

**Paso 5**: Registrar egreso
```
POST /api/movimientos-caja
{
  "sesionId": {ID_SESION_ABIERTA},
  "tipoMovimiento": "EGRESO",
  "monto": 50.00,
  "concepto": "Compra de insumos"
}
```

**Paso 6**: Cerrar sesión
```
PUT /api/sesiones/{ID_SESION}/cerrar
{
  "montoFinalContado": 1200.00,
  "observacionesCierre": "Cierre correcto"
}
```

---

### Escenario 2: Venta con Anulación

**Paso 1**: Crear venta
```
POST /api/ventas
{...}
```

**Paso 2**: Verificar venta creada
```
GET /api/ventas/{ID_VENTA}
```

**Paso 3**: Anular venta
```
PUT /api/ventas/{ID_VENTA}/anular
{
  "motivoAnulacion": "Error en pedido"
}
```

**Paso 4**: Verificar estado ANULADA
```
GET /api/ventas/{ID_VENTA}
```

---

## ⚠️ ERRORES COMUNES Y SOLUCIONES

### Error 401: Unauthorized
- **Causa**: Token JWT expirado o inválido
- **Solución**: Obtener nuevo token con `/api/auth/login`

### Error 404: Caja no encontrada
- **Causa**: ID de caja no existe o pertenece a otro negocio
- **Solución**: Verificar IDs con `GET /api/cajas`

### Error 400: Ya existe una sesión abierta
- **Causa**: La caja ya tiene una sesión activa
- **Solución**: Cerrar sesión existente primero con `PUT /api/sesiones/{id}/cerrar`

### Error 400: La sesión de caja no está abierta
- **Causa**: Intentando registrar movimientos/ventas en sesión cerrada
- **Solución**: Abrir nueva sesión con `POST /api/sesiones/abrir`

### Error 400: Total de pagos no coincide
- **Causa**: Suma de pagos ≠ total de la venta
- **Solución**: Verificar cálculos en array `pagos[]`

### Error 400: No se pueden registrar movimientos en sesión cerrada
- **Causa**: La sesión ya fue cerrada
- **Solución**: No se pueden modificar sesiones cerradas, crear nueva

---

## 📊 DATOS DE PRUEBA REQUERIDOS

Antes de iniciar las pruebas, asegúrate de tener:

1. **Al menos 1 sede activa** (tabla `sedes`)
2. **Al menos 3 cajas registradoras** (tabla `cajas_registradoras`)
3. **Al menos 2 usuarios** (tabla `usuarios`)
4. **Al menos 5 productos** (tabla `productos`)
5. **Al menos 3 métodos de pago** (tabla `metodos_pago`):
   - Efectivo
   - Tarjeta
   - Transferencia
6. **Al menos 2 clientes** (tabla `clientes`)

💡 **TIP**: Ejecutar el archivo `datos_prueba_bloque_8.sql` para cargar datos de prueba completos.

---

## 📝 NOTAS IMPORTANTES

1. **Multi-tenancy**: Todos los endpoints filtran automáticamente por `negocioId` extraído del JWT
2. **Soft Delete**: Las eliminaciones son lógicas (campo `eliminado_en`)
3. **Cálculos Automáticos**: 
   - `subtotal` = suma de `subtotalItem` de detalles
   - `total` = `subtotal` + `impuestoMonto` + `costoDelivery` - `descuentoMonto`
4. **Validaciones**:
   - No se puede abrir 2 sesiones en la misma caja
   - No se pueden registrar movimientos en sesiones cerradas
   - Suma de pagos debe ser igual al total
5. **Estados de Venta**:
   - `PENDIENTE`: Venta creada sin confirmar
   - `CONFIRMADA`: Venta pagada y confirmada
   - `ANULADA`: Venta cancelada
6. **Códigos Únicos**: Cada venta genera código automático formato `VEN-{negocioId}-{timestamp}`

---

## ✅ CHECKLIST FINAL PARA DEMOSTRACIÓN

- [ ] Token JWT obtenido y válido
- [ ] Listar cajas registradoras
- [ ] Crear nueva caja
- [ ] Abrir sesión de caja
- [ ] Verificar sesión abierta
- [ ] Crear venta POS simple
- [ ] Crear venta con pago mixto
- [ ] Registrar movimiento ingreso
- [ ] Registrar movimiento egreso
- [ ] Listar movimientos de sesión
- [ ] Calcular total ventas de sesión
- [ ] Cerrar sesión de caja con arqueo
- [ ] Anular una venta
- [ ] Listar ventas por rango de fechas
- [ ] Calcular total ventas de sede

---

**Guía creada para**: DrinkGo Backend - Bloque 8  
**Fecha**: Febrero 2026  
**Versión**: 1.0
