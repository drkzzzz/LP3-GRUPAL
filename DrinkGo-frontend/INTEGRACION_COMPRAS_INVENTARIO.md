# 🔗 Integración: Órdenes de Compra → Recepción → Inventario

## 📋 Resumen

Se ha implementado la integración completa entre el módulo de **Compras** y el módulo de **Inventario**, permitiendo que al recibir una orden de compra se actualice automáticamente el inventario con:

- ✅ Creación de lotes de inventario
- ✅ Actualización del stock consolidado
- ✅ Recalculación de costo promedio ponderado

## 🔄 Flujo de Trabajo

### 1. Crear Orden de Compra
En la pestaña **Compras → Órdenes de Compra**:
- Crear nueva orden seleccionando proveedor, sede, almacén
- Agregar productos con cantidades y precios
- La orden se guarda con estado `pendiente`

### 2. Recibir Mercadería
En la pestaña **Compras → Recepción**:
- Aparecen solo órdenes con estado `pendiente`
- Click en botón "Recibir" abre modal de recepción
- Completar datos por cada producto:
  - **Cantidad recibida**: unidades efectivamente recibidas
  - **N° de Lote**: código de identificación del lote (REQUERIDO si cantidadRecibida > 0)
  - **Fecha de Vencimiento**: opcional, recomendado para perecederos
- Click en "Marcar como recibida"

### 3. Procesamiento Automático
Al marcar como recibida, el sistema ejecuta:

#### a) Actualizar detalles de la orden
```javascript
// Guarda cantidadRecibida en cada DetalleOrdenCompra
```

#### b) Crear lotes en inventario
Por cada producto con `cantidadRecibida > 0`:
```javascript
{
  negocio: {...},
  producto: {...},
  almacen: {...},
  numeroLote: "LT-001",  // del input
  fechaIngreso: "2024-01-15",  // fecha actual
  fechaVencimiento: "2025-01-15",  // del input (opcional)
  cantidadInicial: 100,
  cantidadActual: 100,
  costoUnitario: 15.50,  // precioUnitario de la orden
  creadoPor: {...}
}
```

#### c) Sincronizar stock
- **Si existe stock previo**: recalcula costo promedio ponderado
  ```javascript
  nuevoCostoPromedio = (
    cantidadAnterior * costoAnterior + 
    cantidadRecibida * costoUnitario
  ) / nuevaCantidad
  ```
- **Si no existe stock**: crea nuevo registro con costo = precioUnitario

#### d) Actualizar estado de orden
- Cambia `estado` de `pendiente` a `recibida`

## 📊 Verificación

Después de recibir una orden, puedes verificar en:

### Inventario → Lotes
- Verás los nuevos lotes creados con:
  - Número de lote ingresado
  - Fecha de ingreso (hoy)
  - Fecha de vencimiento (si la completaste)
  - Cantidad = cantidadRecibida

### Inventario → Reportes → Stock Consolidado
- Se actualizó `cantidadActual` sumando lo recibido
- Se recalculó `costoPromedio` ponderado

### Inventario → Reportes → Historial de Movimientos
- No se crea movimiento explícito (el lote es la entrada)

## ⚠️ Validaciones

- **Número de lote obligatorio**: Si `cantidadRecibida > 0`, DEBE tener número de lote
- **Fecha de vencimiento opcional**: Puedes dejarla vacía
- **Orden ya recibida**: No se puede volver a procesar
- **Sin almacén**: Si la orden no tiene almacén asignado, muestra error

## 🛠️ Componentes Modificados

### `RecepcionTab.jsx`
- **Imports agregados**:
  - `useLotesInventario` - para crear lotes
  - `useStockInventario` - para sincronizar stock
  - `useAdminAuthStore` - para obtener usuario que crea el lote

- **State modificado**:
  ```javascript
  // Antes
  cantidades[detalleId] = cantidadRecibida  // número

  // Ahora
  cantidades[detalleId] = {
    cantidadRecibida: 0,
    numeroLote: '',
    fechaVencimiento: ''
  }
  ```

- **Handlers nuevos**:
  - `handleLoteChange(detalleId, field, value)` - actualiza numeroLote o fechaVencimiento

- **Lógica en `handleMarcarRecibida`**:
  1. Validación: verifica que productos con cantidadRecibida > 0 tengan número de lote
  2. Actualiza detalles de orden
  3. **BUCLE**: por cada detalle con cantidadRecibida > 0:
     - Crea lote en `lotes_inventario`
     - Busca stock existente para producto+almacén
     - Actualiza o crea registro en `stock_inventario`
  4. Marca orden como recibida

- **Interfaz del modal**:
  - Columnas agregadas: "Nº Lote" y "F. Vencimiento"
  - Inputs deshabilitados si cantidadRecibida = 0
  - Ayuda explicativa en card azul
  - Modal más ancho (`size="2xl"`)

## 📝 Notas Técnicas

### Costo Promedio Ponderado
El sistema calcula automáticamente el costo promedio:
```javascript
costoPromedio = (
  (cantidadAnterior × costoAnterior) + 
  (cantidadNueva × costoNuevo)
) / cantidadTotal
```

Ejemplo:
- Stock anterior: 50 unidades a S/. 10 = S/. 500
- Recepción nueva: 100 unidades a S/. 15 = S/. 1,500
- **Costo promedio**: (500 + 1500) / 150 = **S/. 13.33**

### FEFO (First Expired, First Out)
Los lotes se ordenan automáticamente por fecha de vencimiento en la pestaña "Lotes", facilitando la rotación de productos.

### Multialmacén
El stock se registra por combinación **producto + almacén**, permitiendo:
- Transferencias entre almacenes
- Reportes independientes por almacén
- Control de ubicaciones

## 🚀 Próximos Pasos (Opcional)

### Backend Optimizado
Actualmente la integración se maneja desde el frontend orquestando múltiples llamadas. Para optimizar, se puede crear endpoint:

```java
POST /restful/ordenes-compra/{id}/recibir
{
  "detalles": [
    {
      "detalleId": 1,
      "cantidadRecibida": 100,
      "numeroLote": "LT-001",
      "fechaVencimiento": "2025-01-15"
    }
  ]
}
```

Este endpoint ejecutaría todo en una transacción:
- ✅ Atomicidad (todo o nada)
- ✅ Menor tráfico de red
- ✅ Rollback automático en caso de error

### Rechazo Parcial
Agregar funcionalidad para rechazar productos:
- Campo `cantidadRechazada`
- Campo `razonRechazo`
- Registro en tabla de incidencias

### Notificaciones
- Email al proveedor confirmando recepción
- Alerta si cantidadRecibida != cantidadSolicitada
- Notificación push en dashboard

## ✅ Testing Checklist

- [ ] Crear orden de compra con 2 productos
- [ ] Verificar que aparece en pestaña Recepción
- [ ] Recibir orden completando lotes
- [ ] Verificar que se crearon lotes en Inventario → Lotes
- [ ] Verificar stock actualizado en Inventario → Reportes → Stock
- [ ] Verificar que orden cambió a estado "recibida"
- [ ] Intentar recibir sin número de lote (debe mostrar error)
- [ ] Recibir con fecha de vencimiento vacía (debe permitir)

---

**Autor**: Sistema de Integración Compras-Inventario  
**Fecha**: 2024  
**Versión**: 1.0  
