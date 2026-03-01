# 🧪 GUÍA DE PRUEBA - INVENTARIO PROFESIONAL DRINKGO

## 📋 REQUISITOS PREVIOS

1. ✅ Backend corriendo en puerto 8080 desde tu IDE de Spring Boot
2. ✅ Base de datos MySQL con seeds cargados
3. ✅ PowerShell abierto


## 🚀 PASO A PASO PARA PROBAR LA IMPLEMENTACIÓN

### **OPCIÓN 1: Script Automático (Recomendado)** 

1. **Ejecuta el script de prueba completo:**
   ```powershell
   .\test-implementacion-completa.ps1
   ```

2. **El script te mostrará:**
   - ✅ Estado inicial del inventario (Stock, CPP, Lotes, Movimientos)
   - ✅ Creación de un lote nuevo con 100 unidades a S/32.00
   - ✅ Verificación automática de sincronización de stock
   - ✅ Validación del cálculo automático de CPP
   - ✅ Confirmación de registro automático de movimientos
   - ✅ Análisis completo de resultados

3. **Resultado esperado:**
   ```
   [ÉXITO] Implementación profesional funcionando CORRECTAMENTE!

   La implementación demuestra:
   ✅ Sincronización automática de stock
   ✅ Cálculo automático de CPP
   ✅ Registro automático de movimientos
   ✅ Transaccionalidad ACID garantizada
   ```


### **OPCIÓN 2: Prueba Manual con Postman/Insomnia**

#### **1. Obtener Token JWT (Seguridad habilitada):**

```http
POST http://localhost:8080/restful/token
Content-Type: application/json

{
  "username": "tu_usuario",
  "password": "tu_contraseña"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### **2. Ver Stock ANTES de crear lote:**

```http
GET http://localhost:8080/restful/stock-inventario
Authorization: Bearer TU_TOKEN_AQUI
```

**Anota estos valores:**
- `cantidadActual`: ___________
- `costoPromedio`: ___________


#### **3. Ver Lotes ANTES:**

```http
GET http://localhost:8080/restful/lotes-inventario
Authorization: Bearer TU_TOKEN_AQUI
```

**Cuenta cuántos lotes hay:** ___________


#### **4. Ver Movimientos ANTES:**

```http
GET http://localhost:8080/restful/movimientos-inventario
Authorization: Bearer TU_TOKEN_AQUI
```

**Cuenta cuántos movimientos hay:** ___________


#### **5. CREAR NUEVO LOTE (Aquí sucede la magia 🪄):**

```http
POST http://localhost:8080/restful/lotes-inventario
Authorization: Bearer TU_TOKEN_AQUI
Content-Type: application/json

{
  "negocio": { "id": 1 },
  "producto": { "id": 1 },
  "almacen": { "id": 1 },
  "numeroLote": "PRUEBA-MANUAL-001",
  "cantidadInicial": 50,
  "cantidadActual": 50,
  "costoUnitario": 35.00,
  "fechaIngreso": "2026-02-28",
  "fechaVencimiento": "2027-08-28",
  "usuario": { "id": 1 },
  "estaActivo": true
}
```

**Respuesta esperada:**
```json
{
  "id": 19,
  "numeroLote": "PRUEBA-MANUAL-001",
  "cantidadActual": 50,
  "costoUnitario": 35.00,
  ...
}
```


#### **6. Ver Stock DESPUÉS (Debe haber cambiado automáticamente):**

```http
GET http://localhost:8080/restful/stock-inventario
Authorization: Bearer TU_TOKEN_AQUI
```

**Verifica:**
- ✅ `cantidadActual` aumentó en 50 unidades
- ✅ `costoPromedio` se recalculó automáticamente
- ✅ `cantidadDisponible` = `cantidadActual` - `cantidadReservada`


#### **7. Ver Lotes DESPUÉS (Debe haber +1 lote):**

```http
GET http://localhost:8080/restful/lotes-inventario
Authorization: Bearer TU_TOKEN_AQUI
```

**Verifica:**
- ✅ Total de lotes aumentó en 1


#### **8. Ver Movimientos DESPUÉS (Debe haber +1 movimiento):**

```http
GET http://localhost:8080/restful/movimientos-inventario
Authorization: Bearer TU_TOKEN_AQUI
```

**Verifica:**
- ✅ Total de movimientos aumentó en 1
- ✅ Tipo de movimiento: `"entrada"`
- ✅ Cantidad: 50
- ✅ Costo unitario: 35.00
- ✅ Referencia al lote creado


## 📊 QUÉ DEBES OBSERVAR

### **ANTES de mi implementación (manual):**
```
1. Frontend crea lote via POST /lotes-inventario
   └─> Solo crea el lote, nada más

2. Frontend debe hacer MANUALMENTE:
   └─> PUT /stock-inventario (actualizar cantidad)
   └─> Calcular CPP manualmente en JavaScript
   └─> POST /movimientos-inventario (registrar movimiento)

3. Riesgo: Si falla uno de los pasos, datos inconsistentes ❌
```

### **DESPUÉS de mi implementación (automático):**
```
1. Frontend crea lote via POST /lotes-inventario
   └─> El BACKEND hace TODO automáticamente:
       ✅ Crea el lote
       ✅ Actualiza stock_inventario (cantidad)
       ✅ Calcula CPP con fórmula correcta
       ✅ Registra movimiento_inventario (tipo: entrada)
       ✅ TODO en UNA transacción ACID

2. Frontend solo espera respuesta ✅

3. Garantía: Si algo falla, ROLLBACK completo (nada se guarda) ✅
```


## 🔍 CÓMO VERIFICAR CPP AUTOMÁTICO

### **Fórmula del CPP:**
```
CPP = (Valor Stock Anterior + Valor Entrada Nueva) / Cantidad Total

Donde:
- Valor Stock Anterior = Stock Antes × CPP Antes
- Valor Entrada Nueva = Cantidad Nueva × Costo Nuevo
- Cantidad Total = Stock Antes + Cantidad Nueva
```

### **Ejemplo práctico:**
```
Stock Antes:    155 unidades a S/28.67 → Valor: S/4,443.85
Entrada Nueva:  50 unidades a S/32.00  → Valor: S/1,600.00
────────────────────────────────────────────────────────────
Total:          205 unidades           → Valor: S/6,043.85

CPP = S/6,043.85 / 205 = S/29.48 ✅
```


## 🧪 PRUEBA ADICIONAL: FIFO (First In, First Out)

Para probar que las salidas usan FIFO automáticamente, necesitarías implementar el endpoint:

```http
POST http://localhost:8080/restful/inventario/salida
Authorization: Bearer TU_TOKEN_AQUI
Content-Type: application/json

{
  "negocioId": 1,
  "productoId": 1,
  "almacenId": 1,
  "cantidad": 30,
  "usuarioId": 1,
  "motivoMovimiento": "Venta a cliente",
  "referenciaDocumento": "VENTA-001"
}
```

**El backend descargará automáticamente:**
1. Del lote más antiguo primero (FIFO)
2. Si no alcanza, del siguiente lote más antiguo
3. Registrará UN movimiento de salida por CADA lote afectado
4. Actualizará el stock consolidado

*Nota: Este endpoint no está expuesto aún, está en el servicio pero faltaría agregarlo al controller.*


## 📝 CHECKLIST DE VERIFICACIÓN

Después de ejecutar el script o las pruebas manuales:

- [ ] ✅ Stock se actualizó automáticamente
- [ ] ✅ CPP se calculó correctamente
- [ ] ✅ Movimiento se registró automáticamente
- [ ] ✅ Movimiento tiene tipo "entrada"
- [ ] ✅ Movimiento referencia al lote creado
- [ ] ✅ Todo sucedió en UNA sola llamada POST
- [ ] ✅ No hubo errores en consola del backend
- [ ] ✅ Si falla, debe hacer ROLLBACK completo


## 🎯 PRÓXIMOS ENDPOINTS A EXPONER (Opcionales)

Si quieres probar más funcionalidades, puedes agregar estos endpoints al controller:

1. **Salida con FIFO:**
   ```java
   POST /restful/inventario/salida
   → registrarSalida()
   ```

2. **Transferencia entre almacenes:**
   ```java
   POST /restful/inventario/transferencia
   → registrarTransferencia()
   ```

3. **Ajustes de inventario:**
   ```java
   POST /restful/inventario/ajuste
   → registrarAjuste()
   ```

4. **Reservas de stock:**
   ```java
   POST /restful/inventario/reservar
   → reservarStock()
   
   POST /restful/inventario/liberar-reserva
   → liberarReserva()
   
   POST /restful/inventario/confirmar-reserva
   → confirmarReservaYSalida()
   ```


## ❓ SOLUCIÓN DE PROBLEMAS

### **Error: "Backend detenido (PID: XXX)"**
- Verifica que el backend esté corriendo desde tu IDE
- Puerto 8080 debe estar libre

### **Error 401 Unauthorized**
- Seguridad JWT está habilitada
- Necesitas obtener token primero con `/restful/token`

### **Error 500 Internal Server Error**
- Revisa logs del backend en tu IDE
- Puede ser un error de compilación
- Verifica que `InventarioTransaccionalService` esté cargado

### **Stock no se sincroniza**
- Verifica que el controller esté usando `inventarioService.registrarEntrada()`
- No debe usar el servicio viejo `service.guardar()`

### **CPP no se calcula**
- Mismo que arriba, debe usar `InventarioTransaccionalService`


## 📚 DOCUMENTACIÓN COMPLETA

Para más detalles técnicos de la implementación, revisa:
- `IMPLEMENTACION_INVENTARIO_PROFESIONAL.md`


## ✅ CONCLUSIÓN

Si ejecutaste el script y viste:
```
[ÉXITO] Implementación profesional funcionando CORRECTAMENTE!
```

**¡Felicidades! 🎉** Tu sistema de inventario ahora es **profesional y funcional** con:
- ✅ Sincronización automática
- ✅ CPP calculado correctamente
- ✅ FIFO implementado
- ✅ Transaccionalidad ACID
- ✅ Trazabilidad completa
- ✅ Validaciones robustas

**Ya no necesitas lógica de inventario en el frontend.** El backend lo hace todo automáticamente. 🚀
