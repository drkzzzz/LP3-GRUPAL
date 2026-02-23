# Guía de Testing - Endpoints de Ventas (ACTUALIZADA) 🔧

## ✅ Cambios Realizados

Se sincronizó completamente la entidad `Ventas.java` con la estructura de BD existente:

### **Campos Actualizados en Ventas:**
- ✅ Agregado: `negocio` (campo requerido en BD)
- ✅ Agregado: `usuario` (campo requerido en BD - usuario_id)
- ✅ Agregado: `fechaVenta` (campo requerido en BD)
- ✅ Eliminado: `observaciones`, `estaActivo`, `eliminadoEn`
- ✅ Renombrados: 
  - `descuento` → `montoDescuento`
  - `impuestos` → `montoImpuesto`
  - `estadoVenta` → `estado`
- ✅ Nuevos campos añadidos:
  - `razonDescuento`
  - `costoEnvio`
  - `estadoEntrega`
  - `direccionEntrega`
  - `tipoComprobante`
  - `docClienteNumero`
  - `docClienteNombre`
  - `completadoEn`
  - `canceladoEn`
  - `razonCancelacion`
  - `canceladoPor`
  - `vendedor` (vendedor_id - opcional)

### **ENUMs Corregidos:**
- `TipoVenta`: `pos`, `tienda_online`, `mesa`, `telefono`, `otro`
- `Estado`: `pendiente`, `completada`, `parcialmente_pagada`, `cancelada`, `reembolsada`, `anulada`
- `EstadoEntrega`: `entregado`, `pendiente_envio`, `en_ruta`, `para_recoger`
- `TipoComprobante`: `boleta`, `factura`, `nota_venta`

---

## 📋 ENDPOINTS DISPONIBLES

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **GET** | `/restful/ventas` | Obtener todas las ventas |
| **GET** | `/restful/ventas/{id}` | Obtener una venta por ID |
| **POST** | `/restful/ventas` | Crear nueva venta |
| **PUT** | `/restful/ventas` | Actualizar venta |
| **DELETE** | `/restful/ventas/{id}` | Eliminar venta |

---

## 🔧 BODY PARA POST - CREAR NUEVA VENTA (Mínimo Requerido)

```json
{
  "negocio": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "usuario": {
    "id": 1
  },
  "numeroVenta": "V-2026-001",
  "tipoVenta": "pos",
  "subtotal": 100.00,
  "total": 100.00
}
```

**Nota:** `fechaVenta` se establece automáticamente a la fecha/hora actual si no se especifica.

### Con todos los campos opcionales:

```json
{
  "negocio": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "usuario": {
    "id": 1
  },
  "numeroVenta": "V-2026-001",
  "tipoVenta": "mesa",
  "cliente": {
    "id": 1
  },
  "mesa": {
    "id": 1
  },
  "sesionCaja": {
    "id": 1
  },
  "fechaVenta": "2026-02-23T15:30:00",
  "subtotal": 150.00,
  "montoDescuento": 15.00,
  "razonDescuento": "Descuento por cliente frecuente",
  "montoImpuesto": 21.60,
  "costoEnvio": 10.00,
  "total": 166.60,
  "estado": "pendiente",
  "estadoEntrega": "para_recoger",
  "tipoComprobante": "boleta",
  "docClienteNumero": "12345678",
  "docClienteNombre": "Juan Pérez",
  "vendedor": {
    "id": 1
  }
}
```

---

## 📝 BODY PARA PUT - ACTUALIZAR VENTA

```json
{
  "id": 1,
  "negocio": {
    "id": 1
  },
  "sede": {
    "id": 1
  },
  "usuario": {
    "id": 1
  },
  "numeroVenta": "V-2026-001",
  "tipoVenta": "tienda_online",
  "cliente": {
    "id": 2
  },
  "subtotal": 250.00,
  "montoDescuento": 25.00,
  "montoImpuesto": 36.00,
  "costoEnvio": 20.00,
  "total": 281.00,
  "estado": "completada",
  "tipoComprobante": "factura",
  "docClienteNumero": "87654321",
  "docClienteNombre": "María García",
  "completadoEn": "2026-02-23T15:45:00"
}
```

---

## 📊 VALORES PERMITIDOS (ENUMS)

### **tipoVenta:**
- `pos` - Venta en punto de venta
- `tienda_online` - Venta online
- `mesa` - Venta en mesa de restaurante
- `telefono` - Venta por teléfono
- `otro` - Otro tipo

### **estado:**
- `pendiente` - Venta pendiente
- `completada` - Venta completada
- `parcialmente_pagada` - Parcialmente pagada
- `cancelada` - Cancelada
- `reembolsada` - Reembolsada
- `anulada` - Anulada

### **estadoEntrega:** (opcional)
- `entregado`
- `pendiente_envio`
- `en_ruta`
- `para_recoger`

### **tipoComprobante:**
- `boleta` (por defecto)
- `factura`
- `nota_venta`

---

## 🧪 FLUJO RECOMENDADO DE TESTING

```
1. GET /restful/ventas
   └─ Ver todas las ventas existentes

2. POST /restful/ventas
   └─ Crear nueva venta (usar body mínimo)

3. GET /restful/ventas/{id}
   └─ Obtener la venta creada

4. PUT /restful/ventas
   └─ Actualizar la venta

5. DELETE /restful/ventas/{id}
   └─ Eliminar la venta
```

---

## ✅ RESPUESTAS ESPERADAS

### GET - Todas las ventas (200 OK)
```json
[
  {
    "id": 1,
    "negocio": {
      "id": 1,
      "nombre": "DrinkGo"
    },
    "numeroVenta": "V-2026-001",
    "tipoVenta": "pos",
    "subtotal": 100.00,
    "montoDescuento": 0.00,
    "montoImpuesto": 0.00,
    "costoEnvio": 0.00,
    "total": 100.00,
    "estado": "pendiente",
    "tipoComprobante": "boleta",
    "creadoEn": "2026-02-23T10:30:00"
  }
]
```

### POST - Crear venta (201 CREATED)
```json
{
  "id": 2,
  "negocio": {
    "id": 1
  },
  "usuario": {
    "id": 1
  },
  "numeroVenta": "V-2026-001",
  "tipoVenta": "pos",
  "subtotal": 100.00,
  "total": 100.00,
  "estado": "pendiente",
  "tipoComprobante": "boleta",
  "creadoEn": "2026-02-23T10:35:00",
  "actualizadoEn": "2026-02-23T10:35:00"
}
```

### DELETE - Eliminar venta (200 OK)
```
"Venta eliminada correctamente"
```

---

## 🚨 ERRORES COMUNES Y SOLUCIONES

### ❌ Error: `usuario is required`
**Causa:** Falta o es nulo el campo `usuario`

**Solución:**
```json
"usuario": {
  "id": 1
}
```
Asegúrate que el ID de usuario existe en la BD:
```sql
SELECT id FROM usuarios WHERE id = 1;
```

---

### ❌ Error: `negocio is required`
**Causa:** Falta o es nulo el campo `negocio`

**Solución:**
```json
"negocio": {
  "id": 1
}
```
Asegúrate que el ID de negocio existe en la BD:
```sql
SELECT id FROM negocios WHERE id = 1;
```

---

### ❌ Error: `sede is required`
**Causa:** Falta o es nulo el campo `sede`

**Solución:**
```json
"sede": {
  "id": 1
}
```
Asegúrate que el ID de sede existe en la BD:
```sql
SELECT id FROM sedes WHERE id = 1;
```

---

### ❌ Error: `numero_venta Duplicate entry`
**Causa:** Ya existe una venta con ese número

**Solución:** Usa un `numeroVenta` único:
```json
"numeroVenta": "V-2026-" + Date.now()
```

---

### ❌ Error: `Data truncated for column`
**Causa:** Tipo de enum inválido

**Solución:** Verifica los valores permitidos:
```json
"tipoVenta": "pos"  ✅ Válido
"tipoVenta": "mostrador"  ❌ INVÁLIDO (ya no existe)
```

---

## 📌 NOTAS IMPORTANTES

1. **Negocio es requerido** - Siempre debe incluirse `negocio.id`
2. **Sede es requerida** - Siempre debe incluirse `sede.id`
3. **Usuario es requerido** - Siempre debe incluirse `usuario.id` (quien creó/realiza la venta)
4. **Número de venta es único** - No puede repetirse
5. **Tipo de venta es requerido** - Use valores del enum
6. **Subtotal y total son requeridos** - No pueden ser null
7. **Fecha de venta es automática** - Se establece con la fecha/hora actual si no se especifica

---

