# Guía de Pruebas Postman - Órdenes de Compra

**Importante:** Esta guía está basada en la estructura actual de la BD en XAMPP (la BD oficial del proyecto), no en el script SQL del proyecto que puede estar desactualizado.

## Endpoints Disponibles

### 1. GET - Obtener todas las órdenes de compra

```
GET http://localhost:8080/restful/ordenes-compra
```

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "negocio": {
      "id": 1,
      "nombre": "DrinkGo Principal"
    },
    "numeroOrden": "ORD-2024-001",
    "proveedor": {
      "id": 1,
      "nombre": "Distribuidora ABC"
    },
    "sede": {
      "id": 1,
      "nombre": "Sede Centro"
    },
    "almacen": {
      "id": 1,
      "nombre": "Almacén Principal"
    },
    "estado": "pendiente",
    "total": "5000.00",
    "notas": "Orden urgente",
    "creadoPor": null,
    "creadoEn": "2024-01-15T10:30:00",
    "actualizadoEn": "2024-01-15T10:30:00"
  }
]
```

---

## 2. POST - Crear nueva orden de compra

```
POST http://localhost:8080/restful/ordenes-compra
```

**Headers requeridos:**
```
Content-Type: application/json
```

### Ejemplo Completo (con todos los campos)

```json
{
  "negocio": {
    "id": 1
  },
  "numeroOrden": "ORD-2024-001",
  "proveedor": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "almacen": {
    "id": 1
  },
  "usuario": {
    "id": 1
  },
  "fechaOrden": "2024-01-15T10:30:00",
  "subtotal": "4200.00",
  "impuestos": "800.00",
  "total": "5000.00",
  "estado": "pendiente",
  "notas": "Orden urgente - Entregar antes del viernes",
  "creadoPor": {
    "id": 1
  }
}
```

### Ejemplo Mínimo (solo campos requeridos)

```json
{
  "negocio": {
    "id": 1
  },
  "numeroOrden": "ORD-2024-002",
  "proveedor": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "almacen": {
    "id": 1
  },
  "usuario": {
    "id": 1
  },
  "subtotal": "3000.00",
  "total": "3500.50"
}
```

**Nota:** Si no envías `fechaOrden`, se auto-completará con la fecha y hora actual. `impuestos` es opcional (por defecto 0). **`usuario` es REQUERIDO**.

**Respuesta exitosa (201 CREATED):**
```json
{
  "id": 2,
  "negocio": {
    "id": 1
  },
  "numeroOrden": "ORD-2024-002",
  "proveedor": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "almacen": {
    "id": 1
  },
  "usuario": {
    "id": 1
  },
  "estado": "pendiente",
  "subtotal": "3000.00",
  "impuestos": "0.00",
  "total": "3500.50",
  "notas": null,
  "fechaOrden": "2024-01-15T11:00:00",
  "creadoPor": null,
  "creadoEn": "2024-01-15T11:00:00",
  "actualizadoEn": "2024-01-15T11:00:00"
}
```

**Respuesta con error de validación (400 BAD_REQUEST):**
```json
"Error: La sede es requerida"
```

---

## 3. GET - Obtener orden por ID

```
GET http://localhost:8080/restful/ordenes-compra/1
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "negocio": {
    "id": 1
  },
  "numeroOrden": "ORD-2024-001",
  "proveedor": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "almacen": {
    "id": 1
  },
  "estado": "pendiente",
  "subtotal": "4000.00",
  "impuestos": "800.00",
  "total": "5000.00",
  "fechaOrden": "2024-01-15T10:30:00"
}
```

---

## 4. PUT - Modificar orden de compra

```
PUT http://localhost:8080/restful/ordenes-compra
```

```json
{
  "id": 1,
  "negocio": {
    "id": 1
  },
  "numeroOrden": "ORD-2024-001",
  "proveedor": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "almacen": {
    "id": 1
  },
  "estado": "recibida",
  "total": "5000.00",
  "notas": "Orden recibida correctamente"
}
```

**Respuesta exitosa (200 OK):**
```json
{
  "id": 1,
  "estado": "recibida",
  "notas": "Orden recibida correctamente",
  "actualizadoEn": "2024-01-15T14:30:00"
}
```

---

## 5. DELETE - Eliminar orden de compra

```
DELETE http://localhost:8080/restful/ordenes-compra/1
```

**Respuesta exitosa (200 OK):**
```json
"Orden de compra eliminada"
```

---

## Valores Permitidos

### Estados de Orden (enum estado)
- **pendiente** (valor por defecto si no se especifica)
- **recibida**
- **cancelada**

---

## Campos de la Orden

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| id | Long | NO | Generado automáticamente |
| negocio | Object (Negocios) | SÍ | Solo pasar `{"id": X}` |
| numeroOrden | String | SÍ | Debe ser único |
| proveedor | Object (Proveedores) | SÍ | Solo pasar `{"id": X}` |
| sede | Object (Sedes) | SÍ | Solo pasar `{"id": X}` - **IMPORTANTE** |
| almacen | Object (Almacenes) | SÍ | Solo pasar `{"id": X}` |
| usuario | Object (Usuarios) | SÍ | Solo pasar `{"id": X}` - **REQUERIDO** |
| fechaOrden | LocalDateTime | NO | Formato: "2024-01-15T10:30:00". Si no se envía, se auto-completa con la hora actual |
| subtotal | BigDecimal | SÍ | Formato: "4000.00" - **REQUERIDO** |
| impuestos | BigDecimal | NO | Formato: "800.00". Por defecto: 0.00 |
| estado | String | NO | Valores: pendiente, recibida, cancelada. Por defecto: pendiente |
| total | BigDecimal | SÍ | Formato: "1000.00" |
| notas | String | NO | Opcional, puede ser null |
| creadoPor | Object (Usuarios) | NO | Solo pasar `{"id": X}` si se proporciona |
| creadoEn | LocalDateTime | NO | Generado automáticamente |
| actualizadoEn | LocalDateTime | NO | Generado automáticamente |

---

## Errores Comunes y Soluciones

### Error 1: "Field 'sede_id' doesn't have a default value"
**Causa:** Falta enviar el campo `sede` en el body
**Solución:** Agregar `"sede": {"id": X}` en el JSON

```json
{
  "negocio": {"id": 1},
  "numeroOrden": "ORD-2024-003",
  "proveedor": {"id": 1},
  "sede": {"id": 1},           // ← AGREGADO
  "almacen": {"id": 1},
  "total": "2000.00"
}
```

### Error 2: "Error: El número de orden es requerido"
**Causa:** Campo `numeroOrden` no enviado o vacío
**Solución:** Incluir `numeroOrden` con valor válido

### Error 3: "Error: El total es requerido"
**Causa:** Campo `total` no enviado
**Solución:** Incluir `"total": "XXX.XX"`

### Error 4: 500 INTERNAL_SERVER_ERROR con mensaje de BD
**Causa:** Generalmente un ID relacionado no existe
**Solución:** Verificar que los IDs de negocio, proveedor, sede y almacén existan en la BD

### Error 5: "Field 'fecha_orden' doesn't have a default value"
**Causa:** No se envió el campo `fechaOrden` en el body
**Solución:** Agregar `fechaOrden` con formato ISO (ej: "2024-01-15T10:30:00") O no enviarlo, se auto-completa

```json
{
  "negocio": {"id": 1},
  "numeroOrden": "ORD-2024-001",
  "proveedor": {"id": 1},
  "sede": {"id": 1},
  "almacen": {"id": 1},
  "fechaOrden": "2024-01-15T10:30:00",  // ← AGREGADO
  "subtotal": "4000.00",
  "total": "5000.00"
}
```

### Error 6: "Field 'subtotal' doesn't have a default value"
**Causa:** No se envió el campo `subtotal` en el body
**Solución:** Agregar `subtotal` con valor (formato decimal: "XXXX.XX")

```json
{
  "negocio": {"id": 1},
  "numeroOrden": "ORD-2024-001",
  "proveedor": {"id": 1},
  "sede": {"id": 1},
  "almacen": {"id": 1},
  "usuario": {"id": 1},
  "subtotal": "4000.00",    // ← REQUERIDO
  "total": "5000.00"
}
```

### Error 7: "Field 'usuario_id' doesn't have a default value"
**Causa:** No se envió el campo `usuario` en el body
**Solución:** Agregar `usuario` con el ID del usuario que crea la orden

```json
{
  "negocio": {"id": 1},
  "numeroOrden": "ORD-2024-001",
  "proveedor": {"id": 1},
  "sede": {"id": 1},
  "almacen": {"id": 1},
  "usuario": {"id": 1},      // ← REQUERIDO - Usuario que crea la orden
  "subtotal": "4000.00",
  "total": "5000.00"
}
```

## Ejemplos de Prueba Rápida

### Crear una orden simple
```json
{
  "negocio": {"id": 1},
  "numeroOrden": "TEST-001",
  "proveedor": {"id": 1},
  "sede": {"id": 1},
  "almacen": {"id": 1},
  "usuario": {"id": 1},
  "subtotal": "1000.00",
  "total": "1000.00"
}
```
**Nota**: fechaOrden se auto-completa con la hora actual; impuestos es opcional (por defecto 0)

### Crear una orden con detalles completos
```json
{
  "negocio": {"id": 1},
  "numeroOrden": "TEST-002",
  "proveedor": {"id": 1},
  "sede": {"id": 1},
  "almacen": {"id": 1},
  "usuario": {"id": 1},
  "fechaOrden": "2024-01-15T14:30:00",
  "subtotal": "2000.00",
  "impuestos": "400.00",
  "total": "2400.00",
  "notas": "Orden de prueba"
}
```

### Cambiar estado a recibida
```json
{
  "id": 1,
  "negocio": {"id": 1},
  "numeroOrden": "TEST-001",
  "proveedor": {"id": 1},
  "sede": {"id": 1},
  "almacen": {"id": 1},
  "usuario": {"id": 1},
  "fechaOrden": "2024-01-15T10:00:00",
  "subtotal": "1000.00",
  "total": "1000.00",
  "estado": "recibida",
  "notas": "Orden recibida el 15-01-2024"
}
```

### Cancelar una orden
```json
{
  "id": 1,
  "negocio": {"id": 1},
  "numeroOrden": "TEST-001",
  "proveedor": {"id": 1},
  "sede": {"id": 1},
  "almacen": {"id": 1},
  "usuario": {"id": 1},
  "fechaOrden": "2024-01-15T10:00:00",
  "subtotal": "1000.00",
  "total": "1000.00",
  "estado": "cancelada",
  "notas": "Orden cancelada por cambio de plan"
}
```

