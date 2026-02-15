# GUÍA DE PRUEBAS POSTMAN - BLOQUE 11: DEVOLUCIONES Y REEMBOLSOS

## INFORMACIÓN GENERAL
- **Base URL**: `http://localhost:8080`
- **Autenticación**: MODO DESARROLLO - No requiere JWT (todos los endpoints públicos)
- **Headers comunes**: 
  - `Content-Type: application/json`
  - No se requiere `Authorization` por el momento

---

## 📋 CHECKLIST DE PRUEBAS (Evaluación Profesor)

### ✅ Antes de demostrar al profesor:
1. Servidor Spring Boot corriendo en puerto 8080
2. Base de datos MySQL iniciada (XAMPP)
3. Tablas `devoluciones` y `detalle_devoluciones` creadas
4. Datos de prueba insertados (opcional, o crear durante demo)

### ✅ Endpoints a demostrar:
- [ ] GET - Listar todas las devoluciones
- [ ] GET - Obtener devolución por ID
- [ ] POST - Crear nueva devolución
- [ ] PUT - Actualizar devolución
- [ ] PUT - Aprobar devolución
- [ ] PUT - Rechazar devolución
- [ ] PUT - Completar devolución
- [ ] DELETE - Eliminar devolución

---

## 1️⃣ GET - LISTAR TODAS LAS DEVOLUCIONES

### Request
```
GET http://localhost:8080/restful/devoluciones
```

### Headers
```
Content-Type: application/json
```

### Response Esperada (200 OK)
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "ventaId": 101,
    "pedidoId": null,
    "clienteId": null,
    "numeroDevolucion": "DEV-1-20250214001",
    "estado": "solicitada",
    "tipoDevolucion": "total",
    "categoriaMotivo": "defectuoso",
    "detalleMotivo": "Producto llegó con la botella rota",
    "metodoReembolso": "pago_original",
    "subtotal": 102.91,
    "montoImpuesto": 18.52,
    "total": 121.43,
    "notas": "Cliente solicita reembolso completo",
    "solicitadoEn": "2026-02-14T11:45:00",
    "aprobadoEn": null,
    "completadoEn": null,
    "rechazadoEn": null,
    "procesadoPor": null,
    "aprobadoPor": null,
    "solicitadoPor": null,
    "razonRechazo": null,
    "creadoEn": "2026-02-14T11:45:00",
    "actualizadoEn": "2026-02-14T11:45:00",
    "detalles": [
      {
        "id": 1,
        "detalleVentaId": null,
        "productoId": 1,
        "cantidad": 2.000,
        "precioUnitario": 45.00,
        "total": 90.00,
        "devolverStock": true,
        "notas": "Ron Diplomático - 2 botellas rotas"
      }
    ]
  }
]
```

---

## 2️⃣ GET - OBTENER DEVOLUCIÓN POR ID

### Request
```
GET http://localhost:8080/restful/devoluciones/1
```

### Headers
```
Content-Type: application/json
```

### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "sedeId": 1,
  "ventaId": 101,
  "pedidoId": null,
  "clienteId": null,
  "numeroDevolucion": "DEV-1-20250214001",
  "estado": "solicitada",
  "tipoDevolucion": "total",
  "categoriaMotivo": "defectuoso",
  "detalleMotivo": "Producto llegó con la botella rota",
  "metodoReembolso": "pago_original",
  "subtotal": 102.91,
  "montoImpuesto": 18.52,
  "total": 121.43,
  "notas": "Cliente solicita reembolso completo",
  "solicitadoEn": "2026-02-14T11:45:00",
  "aprobadoEn": null,
  "completadoEn": null,
  "rechazadoEn": null,
  "procesadoPor": null,
  "aprobadoPor": null,
  "solicitadoPor": null,
  "razonRechazo": null,
  "creadoEn": "2026-02-14T11:45:00",
  "actualizadoEn": "2026-02-14T11:45:00",
  "detalles": [
    {
      "id": 1,
      "detalleVentaId": null,
      "productoId": 1,
      "cantidad": 2.000,
      "precioUnitario": 45.00,
      "total": 90.00,
      "devolverStock": true,
      "notas": "Ron Diplomático - 2 botellas rotas"
    }
  ]
}
```

### Caso de Error (404 NOT FOUND)
```json
{
  "error": "Devolución no encontrada"
}
```

---

## 3️⃣ POST - CREAR NUEVA DEVOLUCIÓN

### Request
```
POST http://localhost:8080/restful/devoluciones
```

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```json
{
  "negocioId": 1,
  "sedeId": 1,
  "ventaId": 1,
  "clienteId": 1,
  "tipoDevolucion": "parcial",
  "categoriaMotivo": "articulo_incorrecto",
  "detalleMotivo": "Se envió producto equivocado - cliente pidió Whisky Black Label pero recibió Red Label",
  "metodoReembolso": "credito_tienda",
  "subtotal": 89.90,
  "montoImpuesto": 16.18,
  "total": 106.08,
  "notas": "Devolución sin cliente registrado",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 1,
      "precioUnitario": 89.90,
      "total": 89.90,
      "devolverStock": true,
      "notas": "Producto incorrecto - devolver a inventario"
    }
  ]
}
```

### Response Esperada (201 CREATED)
```json
{
  "id": 6,
  "negocioId": 1,
  "sedeId": 1,
  "ventaId": 102,
  "clienteId": null,
  "numeroDevolucion": "DEV-1-20260214112045",
  "estado": "solicitada",
  "tipoDevolucion": "parcial",
  "categoriaMotivo": "articulo_incorrecto",
  "detalleMotivo": "Se envió producto equivocado",
  "metodoReembolso": "credito_tienda",
  "subtotal": 89.90,
  "montoImpuesto": 16.18,
  "total": 106.08,
  "notas": "Cliente acepta crédito en tienda",
  "solicitadoEn": "2026-02-14T11:20:45",
  "creadoEn": "2026-02-14T11:20:45",
  "actualizadoEn": "2026-02-14T11:20:45",
  "detalles": [
    {
      "id": 10,
      "productoId": 1,
      "cantidad": 1.000,
      "precioUnitario": 89.90,
      "total": 89.90,
      "devolverStock": true,
      "ns de Error (400 BAD REQUEST)
```json
{
  "error": "El negocioId es obligatorio"
}
```
```json
{
  "error": "El sedeId es obligatorio"
}
```
```json
{
  "error": "El tipo de devolución
```

### Caso de Error (400 BAD REQUEST)
```json
{
  "error": "El negocioId es obligatorio"
}
```

---

## 4️⃣ PUT - ACTUALIZAR DEVOLUCIÓN

**NOTA**: Solo se pueden actualizar devoluciones en estado 'solicitada'

### Request
```
PUT http://localhost:8080/restful/devoluciones/1
```

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```detalleMotivo": "Actualización: Producto llegó con botella rota Y con fecha de vencimiento próxima",
  "metodoReembolso": "efectivo",
  "subtotal": 125.50,
  "montoImpuesto": 22.59,
  "total": 148.09tivo",
  "montoTotal": 125.50,
  "impuestosDevueltos": 22.59,
  "montoReembolso": 125.50,
  "notas": "Cliente prefiere reembolso en efectivo - actualizado por solicitud"
}

```

### Response Esperada (200 OK)
```json
{sedeId": 1,
  "numeroDevolucion": "DEV-1-20250214001",
  "estado": "solicitada",
  "categoriaMotivo": "defectuoso",
  "detalleMotivo": "Actualización: Producto llegó con botella rota Y con fecha de vencimiento próxima",
  "metodoReembolso": "efectivo",
  "subtotal": 125.50,
  "montoImpuesto": 22.59,
  "total": 148.09,
  "actualizadoEn"Actualización: Producto llegó con botella rota Y con fecha de vencimiento próxima",
  "metodoReembolso": "efectivo",
  "montoTotal": 125.50,
  "updatedAt": "2026-02-14T11:25:00"
}
```

### Caso de Error (400 BAD REQUEST)
```json
{
  "error": "Solo se pueden actualizar devoluciones en estado 'solicitada'"
}
```

---

## 5️⃣ PUT - APROBAR DEVOLUCIÓN

**RF-FIN-009**: Gestionar Motivos y Aprobación de Devoluciones

### Request
```
PUT http://localhost:8080/restful/devoluciones/1/aprobar
```

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```jsonBLOQUE 12: GASTOS E INGRESOS
{
  "aprobadoPor": 1,
  "notas": "Devolución aprobada - producto efectivamente llegó dañado según inspección"
}
```

### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "numeroDevolucion": "DEV-1-20250214001",
  "estado": "aprobada",
  "aprobadoEn": "2026-02-14T11:30:00",
  "aprobadoPor": 1,
  "notas": "Cliente solicita reembolso completo\nAPROBACIÓN: Devolución aprobada - producto efectivamente llegó dañado según inspección"
}
```

### Caso de Error (400 BAD REQUEST)
```json
{
  "error": "Solo se pueden aprobar devoluciones en estado 'solicitada'"
}
```

---

## 6️⃣ PUT - RECHAZAR DEVOLUCIÓN

**RF-FIN-009**: Gestionar Motivos y Aprobación de Devoluciones

### Request
```
PUT http://localhost:8080/restful/devoluciones/2/rechazar
```

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```json
{
  "aprobadoPor": 1,
  "notas": "Devolución rechazada - producto fue recibido hace más de 45 días, fuera de política de devoluciones"
}
```

### Response Esperada (200 OK)
```json
{
  "id": 2,
  "negocioId": 1,
  "numeroDevolucion": "DEV-1-20250214002",
  "estado": "rechazada",
  "rechazadoEn": "2026-02-14T11:35:00",
  "rechazadoPor": 1,
  "notas": "Cliente acepta crédito en tienda\nRECHAZO: Devolución rechazada - producto fue recibido hace más de 45 días"
}
```

---

## 7️⃣ PUT - COMPLETAR DEVOLUCIÓN

**RF-FIN-010**: Generar Ajustes Financieros  
**RF-FIN-011**: Gestionar Reembolsos al Cliente  
**RF-FIN-012**: Reintegrar Productos Devueltos al Inventario

**NOTA**: Solo se pueden completar devoluciones en estado 'aprobada'

### Request
```
PUT http://localhost:8080/restful/devoluciones/1/completar
```

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```json
{
  "procesadoPor": 1
}
```

### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "numeroDevolucion": "DEV-1-20250214001",
  "estado": "completada",
  "aprobadoEn": "2026-02-14T11:30:00",
  "aprobadoPor": 1,
  "completadoEn": "2026-02-14T11:40:00",
  "procesadoPor": 1,
  "notas": "..."
}
```

### Caso de Error (400 BAD REQUEST)
```json
{
  "error": "Solo se pueden completar devoluciones en estado 'aprobada'"
}
```

### ⚠️ NOTA IMPORTANTE
El endpoint `/completar` está preparado para:
- Reintegrar productos al inventario (cuando `devolver_stock = true`)
- Generar ajustes financieros
- Procesar reembolsos según método seleccionado

**Actualmente**: La lógica de reintegración al inventario está marcada como TODO en el código, se implementará cuando el módulo de inventario esté completamente integrado.

---

## 8️⃣ DELETE - ELIMINAR DEVOLUCIÓN

**NOTA**: Solo se pueden eliminar devoluciones en estado 'solicitada'

### Request
```
DELETE http://localhost:8080/restful/devoluciones/1
```

### Headers
```
Content-Type: application/json
```

### Response Esperada (200 OK)
```json
{
  "mensaje": "Devolución eliminada correctamente"
}
```

### Caso de Error (400 BAD REQUEST)
```json
{
  "error": "Solo se pueden eliminar devoluciones en estado 'solicitada'"
}
```

---

## 📊 FLUJO COMPLETO DE PRUEBA (Orden recomendado)

### Escenario 1: Crear y aprobar una devolución
1. **POST** `/devoluciones` - Crear nueva devolución
2. **GET** `/devoluciones` - Verificar que aparece en la lista
3. **GET** `/devoluciones/{id}` - Ver detalle completo
4. **PUT** `/devoluciones/{id}` - Actualizar (opcional)
5. **PUT** `/devoluciones/{id}/aprobar` - Aprobar la devolución
6. **PUT** `/devoluciones/{id}/completar` - Completar y procesar reembolso

### Escenario 2: Crear y rechazar una devolución
1. **POST** `/devoluciones` - Crear nueva devolución
2. **PUT** `/devoluciones/{id}/rechazar` - Rechazar la devolución

### Escenario 3: Crear y eliminar una devolución
1. **POST** `/devoluciones` - Crear nueva devolución
2. **DELETE** `/devoluciones/{id}` - Eliminar antes de aprobar

---

## 🔍 VALORES VÁLIDOS PARA ENUMS

### **estado** (gestionado automáticamente):
- `solicitada` (estado inicial al crear)
- `aprobada` (después de aprobar)
- `procesando` (reservado para futuro)
- `completada` (después de completar)
- `rechazada` (después de rechazar)

### **tipoDevolucion**:
- `total` - Devolución de toda la compra
- `parcial` - Devolución de algunos productos

### **categoriaMotivo**:
- `defectuoso` - Producto defectuoso o dañado
- `articulo_incorrecto` - Se envió producto equivocado
- `cambio_cliente` - Cliente cambió de opinión
- `vencido` - Producto vencido o próximo a vencer
- `danado` - Producto llegó dañado en transporte
- `otro` - Otra razón

### **metodoReembolso**:
- `efectivo` - Reembolso en efectivo
- `pago_original` - Devolución al método de pago original
- `credito_tienda` - Crédito para próximas compras
- `transferencia_bancaria` - Transferencia a cuenta del cliente

---

## ✅ VALIDACIONES DEL SISTEMA

1. **Al crear devolución**:
   - `negocioId` es obligatorio
   - `sedeId` es obligatorio
   - `ventaId` es obligatorio (toda devolución debe venir de una venta)
   - `clienteId` es obligatorio (se debe saber a quién reembolsar)
   - `tipoDevolucion` es obligatorio
   - Debe incluir al menos un detalle
   - Se genera automáticamente `numeroDevolucion` único formato: `DEV-{negocioId}-{timestamp}`

2. **Al actualizar devolución**:
   - Solo si estado = 'solicitada'
   - Pertenece al negocio actual
   - Campos actualizables: `categoriaMotivo`, `detalleMotivo`, `metodoReembolso`, `subtotal`, `montoImpuesto`, `total`, `notas`

3. **Al aprobar/rechazar**:
   - Solo si estado = 'solicitada'
   - Se registra fecha y usuario que aprobó/rechazó
   - Al rechazar se guarda la razón en `razonRechazo`

4. **Al completar**:
   - Solo si estado = 'aprobada'
   - Se registra fecha y usuario que procesó
   - Se reintegra stock si `devolverStock = true` (pendiente integración con inventario)

5. **Al eliminar**:
   - Solo si estado = 'solicitada'
   - Eliminación física de la base de datos

---

## 🎯 TIPS PARA LA DEMOSTRACIÓN

1. **Preparar los datos**: Insertar 2-3 devoluciones de prueba antes de la demo
2. **Demostrar el flujo completo**: Crear → Aprobar → Completar
3. **Mostrar validaciones**: Intentar aprobar una devolución ya aprobada
4. **Estados de workflow**: Explicar cómo cambia el estado en cada operación
5. **Multi-tenant**: Mencionar que filtra por negocioId (actualmente mock = 1)

---

## 📝 REQUERIMIENTOS FUNCIONALES IMPLEMENTADOS

- ✅ **RF-FIN-008**: Registrar Solicitudes de Devolución (POST)
- ✅ **RF-FIN-009**: Gestionar Motivos y Aprobación (PUT aprobar/rechazar)
- ⚠️ **RF-FIN-010**: Generar Ajustes Financieros (preparado, pendiente integración)
- ⚠️ **RF-FIN-011**: Gestionar Reembolsos (preparado, pendiente integración)
- ⚠️ **RF-FIN-012**: Reintegrar Productos al Inventario (preparado, pendiente integración)

---

## 🚀 PRÓXIMOS PASOS

1. Integrar con módulo de Inventario para reintegración automática de stock
2. Integrar con módulo Financiero para registro de ajustes contables
3. Implementar sistema de reembolsos según método seleccionado
4. Agregar notificaciones al cliente sobre cambios de estado
5. Implementar reportes de devoluciones por período
