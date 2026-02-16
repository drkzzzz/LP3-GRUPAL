# FLUJO DE ENDPOINTS — BLOQUE 10: FACTURACIÓN ELECTRÓNICA (SUNAT)

> **Base URL:** `http://localhost:8080`  
> **Multi-tenant:** Todos los endpoints requieren `negocioId` para aislar datos por negocio.  
> **IGV configurable:** Se lee desde `ConfiguracionGlobalPlataforma` (clave: `TASA_IGV`). Default: 18%.  
> **Códigos SUNAT:** El response incluye `codigoTipoDocumentoSunat` (01=factura, 03=boleta, 07=NC, 08=ND, 09=guía).

---

## PASO 0: Configurar tasa IGV (OPCIONAL)

> Solo necesario si la tasa IGV del negocio NO es 18%. Si no se crea esta configuración, el sistema usa 18% por defecto.

**Endpoint:** `POST /restful/configuracion`

```json
{
  "claveConfiguracion": "TASA_IGV",
  "valorConfiguracion": "18",
  "tipoDato": "decimal",
  "tipoValor": "porcentaje",
  "descripcion": "Tasa de IGV aplicable a comprobantes electrónicos (%)"
}
```

**Response esperado:** `201 Created`

---

## PASO 1: Crear Series de Facturación

### 1a. Serie para Boletas

**Endpoint:** `POST /restful/facturacion/series`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "tipoDocumento": "boleta",
  "prefijoSerie": "B001"
}
```

**Response esperado:** `201 Created`
```json
{
  "id": 1,
  "negocioId": 1,
  "sedeId": 1,
  "tipoDocumento": "boleta",
  "prefijoSerie": "B001",
  "numeroActual": 0,
  "estaActivo": true,
  "creadoEn": "2026-02-16T10:00:00",
  "actualizadoEn": "2026-02-16T10:00:00"
}
```

### 1b. Serie para Facturas

**Endpoint:** `POST /restful/facturacion/series`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "tipoDocumento": "factura",
  "prefijoSerie": "F001"
}
```

### 1c. Serie para Notas de Crédito (de facturas)

**Endpoint:** `POST /restful/facturacion/series`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "tipoDocumento": "nota_credito",
  "prefijoSerie": "FC01"
}
```

### 1d. Serie para Notas de Débito (de boletas)

**Endpoint:** `POST /restful/facturacion/series`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "tipoDocumento": "nota_debito",
  "prefijoSerie": "BN01"
}
```

### Reglas de prefijo (normativa SUNAT)

| Tipo de documento | Prefijo obligatorio | Ejemplos válidos |
|---|---|---|
| boleta | Empieza con `B` | B001, B002 |
| factura | Empieza con `F` | F001, F002 |
| nota_credito | Empieza con `B` o `F` | BC01, FC01 |
| nota_debito | Empieza con `B` o `F` | BN01, FN01 |
| guia_remision | Empieza con `T` | T001, T002 |

---

## PASO 2: Listar Series

**Endpoint:** `GET /restful/facturacion/series?negocioId=1`

Con filtro por sede:

**Endpoint:** `GET /restful/facturacion/series?negocioId=1&sedeId=1`

**Response esperado:** `200 OK`
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "tipoDocumento": "boleta",
    "prefijoSerie": "B001",
    "numeroActual": 0,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  },
  {
    "id": 2,
    "negocioId": 1,
    "sedeId": 1,
    "tipoDocumento": "factura",
    "prefijoSerie": "F001",
    "numeroActual": 0,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:01:00",
    "actualizadoEn": "2026-02-16T10:01:00"
  }
]
```

---

## PASO 3: Obtener Serie por ID

**Endpoint:** `GET /restful/facturacion/series/1?negocioId=1`

**Response esperado:** `200 OK`
```json
{
  "id": 1,
  "negocioId": 1,
  "sedeId": 1,
  "tipoDocumento": "boleta",
  "prefijoSerie": "B001",
  "numeroActual": 0,
  "estaActivo": true,
  "creadoEn": "2026-02-16T10:00:00",
  "actualizadoEn": "2026-02-16T10:00:00"
}
```

---

## PASO 4: Actualizar Serie

**Endpoint:** `PUT /restful/facturacion/series/1?negocioId=1`

```json
{
  "prefijoSerie": "B002",
  "estaActivo": true
}
```

**Response esperado:** `200 OK`

> **Restricción:** No permite cambiar el prefijo si la serie ya tiene documentos emitidos (`numeroActual > 0`). En ese caso, desactivar la serie y crear una nueva.

**Error esperado si tiene documentos:**
```json
{
  "error": "No se puede cambiar el prefijo de la serie 'B001' porque ya tiene 5 documento(s) emitido(s). Desactive esta serie y cree una nueva."
}
```

---

## PASO 5: Desactivar Serie (DELETE lógico)

**Endpoint:** `DELETE /restful/facturacion/series/1?negocioId=1`

**Response esperado:** `200 OK`
```json
{
  "mensaje": "Serie desactivada correctamente",
  "serie": {
    "id": 1,
    "prefijoSerie": "B001",
    "estaActivo": false
  }
}
```

---

## PASO 6: Emitir Boleta de Venta

**Endpoint:** `POST /restful/facturacion/documentos`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "serieId": 1,
  "tipoDocumento": "boleta",
  "rucEmisor": "20123456789",
  "razonSocialEmisor": "Licorería DrinkGo S.A.C.",
  "direccionEmisor": "Av. Arequipa 1234, Lima",
  "tipoDocumentoReceptor": "1",
  "numeroDocumentoReceptor": "45678912",
  "nombreReceptor": "Juan Pérez García",
  "direccionReceptor": "Calle Los Olivos 456, Lima",
  "emailReceptor": "juan@email.com",
  "moneda": "PEN",
  "creadoPor": 3,
  "detalles": [
    {
      "productoId": 1,
      "descripcion": "Pisco Quebranta 750ml",
      "codigoUnidad": "NIU",
      "cantidad": 2,
      "precioUnitario": 45.00,
      "montoDescuento": 5.00,
      "montoIsc": 8.50
    },
    {
      "productoId": 2,
      "descripcion": "Cerveza Cusqueña 330ml Six Pack",
      "codigoUnidad": "NIU",
      "cantidad": 1,
      "precioUnitario": 28.00,
      "montoDescuento": 0,
      "montoIsc": 3.20
    }
  ]
}
```

**Response esperado:** `201 Created`
```json
{
  "id": 1,
  "negocioId": 1,
  "sedeId": 1,
  "serieId": 1,
  "tipoDocumento": "boleta",
  "codigoTipoDocumentoSunat": "03",
  "serie": "B001",
  "numeroCorrelativo": 1,
  "numeroCompleto": "B001-00000001",
  "rucEmisor": "20123456789",
  "razonSocialEmisor": "Licorería DrinkGo S.A.C.",
  "nombreReceptor": "Juan Pérez García",
  "subtotal": 118.00,
  "totalDescuento": 5.00,
  "totalGravado": 113.00,
  "totalIsc": 11.70,
  "tasaIgv": 18.00,
  "totalIgv": 22.45,
  "total": 147.15,
  "moneda": "PEN",
  "estado": "emitido",
  "estadoSunat": "pendiente",
  "fechaEmision": "2026-02-16",
  "detalles": [
    {
      "id": 1,
      "productoId": 1,
      "numeroItem": 1,
      "descripcion": "Pisco Quebranta 750ml",
      "cantidad": 2,
      "precioUnitario": 45.00,
      "montoDescuento": 5.00,
      "montoGravado": 85.00,
      "montoIsc": 8.50,
      "montoIgv": 16.83,
      "total": 110.33
    },
    {
      "id": 2,
      "productoId": 2,
      "numeroItem": 2,
      "descripcion": "Cerveza Cusqueña 330ml Six Pack",
      "cantidad": 1,
      "precioUnitario": 28.00,
      "montoDescuento": 0,
      "montoGravado": 28.00,
      "montoIsc": 3.20,
      "montoIgv": 5.62,
      "total": 36.82
    }
  ]
}
```

### Fórmula de cálculo por item

```
subtotalItem    = cantidad × precioUnitario
montoGravado    = subtotalItem - montoDescuento
baseIgv         = montoGravado + montoIsc
montoIgv        = baseIgv × tasaIgv (ej: 0.18)
totalItem       = baseIgv + montoIgv
```

### Regla boleta >= S/ 700

Si el total de la boleta es >= S/ 700.00, el `numeroDocumentoReceptor` (DNI) es **obligatorio** (Art. 4 numeral 3 del Reglamento de Comprobantes de Pago).

---

## PASO 7: Emitir Factura

**Endpoint:** `POST /restful/facturacion/documentos`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "serieId": 2,
  "tipoDocumento": "factura",
  "rucEmisor": "20123456789",
  "razonSocialEmisor": "Licorería DrinkGo S.A.C.",
  "direccionEmisor": "Av. Arequipa 1234, Lima",
  "tipoDocumentoReceptor": "6",
  "numeroDocumentoReceptor": "20567891234",
  "nombreReceptor": "Distribuidora Los Andes S.R.L.",
  "direccionReceptor": "Jr. Huallaga 789, Lima",
  "emailReceptor": "compras@losandes.com",
  "moneda": "PEN",
  "creadoPor": 3,
  "detalles": [
    {
      "productoId": 1,
      "descripcion": "Pisco Quebranta 750ml",
      "codigoUnidad": "NIU",
      "cantidad": 12,
      "precioUnitario": 40.00,
      "montoDescuento": 20.00,
      "montoIsc": 51.00
    }
  ]
}
```

### Validaciones para facturas

| Campo | Regla |
|---|---|
| `tipoDocumentoReceptor` | Debe ser `"6"` (RUC) |
| `numeroDocumentoReceptor` | 11 dígitos numéricos |
| Formato RUC | Debe empezar con `10` (persona natural), `20` (jurídica), `15` o `17` |
| `serieId` | Debe ser una serie tipo `factura` con prefijo `F*` |

**Error si RUC inválido:**
```json
{
  "error": "El RUC del receptor debe comenzar con 10 (persona natural), 20 (persona jurídica), 15 o 17 (otros contribuyentes). RUC proporcionado: 99999999999"
}
```

---

## PASO 8: Emitir desde Pedido existente

**Endpoint:** `POST /restful/facturacion/documentos/desde-pedido/{pedidoId}?negocioId=1&serieId=1&tipoDocumento=boleta`

```json
{
  "rucEmisor": "20123456789",
  "razonSocialEmisor": "Licorería DrinkGo S.A.C.",
  "direccionEmisor": "Av. Arequipa 1234, Lima",
  "tipoDocumentoReceptor": "1",
  "numeroDocumentoReceptor": "45678912",
  "emailReceptor": "cliente@email.com"
}
```

**Response esperado:** `201 Created` (mismo formato que PASO 6)

### Comportamiento

- Los **detalles** (items, cantidades, precios, descuentos) se toman automáticamente del pedido.
- El **nombre del receptor** se toma de `clienteNombre` del pedido si no se proporciona en el body.
- Si el pedido ya tiene un documento activo (no anulado), retorna error.
- Si el pedido está anulado o cancelado, retorna error.

**Error si ya facturado:**
```json
{
  "error": "El pedido PED-000001 ya tiene un documento de facturación activo: B001-00000001"
}
```

---

## PASO 9: Enviar documento a SUNAT (simulado)

**Endpoint:** `POST /restful/facturacion/{id}/enviar?negocioId=1`

> No requiere body.

**Response esperado (aceptado — 80% probabilidad):** `200 OK`
```json
{
  "documentoId": 1,
  "numeroCompleto": "B001-00000001",
  "estadoSunat": "aceptado",
  "codigoRespuesta": "0",
  "mensajeRespuesta": "La boleta B001-00000001 ha sido aceptada por SUNAT vía Resumen Diario [SIMULACIÓN]",
  "hashSunat": "a1b2c3d4e5f6...",
  "urlXml": "https://drinkgo-pse-simulacion.local/documentos/B001-00000001.xml",
  "urlCdr": "https://drinkgo-pse-simulacion.local/documentos/B001-00000001-cdr.xml",
  "mensaje": "Documento enviado y aceptado por SUNAT vía Resumen Diario [SIMULACIÓN - PSE: PSE Simulación DrinkGo (Desarrollo)]"
}
```

**Response esperado (rechazado — 5% probabilidad):**
```json
{
  "documentoId": 1,
  "estadoSunat": "rechazado",
  "codigoRespuesta": "2033",
  "mensajeRespuesta": "Error 2033: RUC del emisor no se encuentra activo [SIMULACIÓN]",
  "mensaje": "Documento rechazado por SUNAT [SIMULACIÓN - PSE: PSE Simulación DrinkGo (Desarrollo)]"
}
```

### Mecanismo de envío por tipo

| Tipo | Mecanismo real SUNAT | Simulación |
|---|---|---|
| Factura | Envío individual | ✅ Mensaje indica "envío individual" |
| Boleta | Resumen Diario | ✅ Mensaje indica "Resumen Diario" |
| NC / ND | Envío individual | ✅ |

---

## PASO 10: Consultar estado de un documento

**Endpoint:** `GET /restful/facturacion/{id}/estado?negocioId=1`

**Response esperado:** `200 OK`
```json
{
  "documentoId": 1,
  "numeroCompleto": "B001-00000001",
  "tipoDocumento": "boleta",
  "estado": "aceptado",
  "estadoSunat": "aceptado",
  "codigoRespuestaSunat": "0",
  "mensajeRespuestaSunat": "La boleta B001-00000001 ha sido aceptada por SUNAT vía Resumen Diario [SIMULACIÓN]",
  "total": 147.15,
  "fechaEmision": "2026-02-16"
}
```

---

## PASO 11: Obtener documento completo

**Endpoint:** `GET /restful/facturacion/{id}?negocioId=1`

**Response esperado:** `200 OK`

> Retorna el documento con TODOS sus campos (emisor, receptor, montos, SUNAT, fechas, referencia) + array de `detalles` + `codigoTipoDocumentoSunat` + `tasaIgv`.

---

## PASO 12: Buscar documentos con filtros

**Endpoint:** `GET /restful/facturacion/documentos?negocioId=1`

### Parámetros de filtro (todos opcionales excepto negocioId)

| Parámetro | Tipo | Ejemplo |
|---|---|---|
| `negocioId` | Long (requerido) | `1` |
| `sedeId` | Long | `1` |
| `tipoDocumento` | String | `boleta`, `factura`, `nota_credito`, `nota_debito`, `guia_remision` |
| `estado` | String | `borrador`, `emitido`, `enviado`, `aceptado`, `anulado`, `error` |
| `estadoSunat` | String | `pendiente`, `enviado`, `aceptado`, `rechazado`, `anulado`, `error` |
| `fechaDesde` | Date (ISO) | `2026-01-01` |
| `fechaHasta` | Date (ISO) | `2026-12-31` |
| `numeroCompleto` | String (parcial) | `F001` |

**Ejemplo con múltiples filtros:**
```
GET /restful/facturacion/documentos?negocioId=1&sedeId=1&tipoDocumento=factura&estado=aceptado&fechaDesde=2026-01-01&fechaHasta=2026-12-31
```

**Response esperado:** `200 OK` — Array de documentos completos con detalles.

---

## PASO 13: Emitir Nota de Crédito

**Endpoint:** `POST /restful/facturacion/documentos`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "serieId": 3,
  "tipoDocumento": "nota_credito",
  "rucEmisor": "20123456789",
  "razonSocialEmisor": "Licorería DrinkGo S.A.C.",
  "direccionEmisor": "Av. Arequipa 1234, Lima",
  "tipoDocumentoReceptor": "6",
  "numeroDocumentoReceptor": "20567891234",
  "nombreReceptor": "Distribuidora Los Andes S.R.L.",
  "moneda": "PEN",
  "documentoReferenciadoId": 2,
  "motivoReferencia": "Devolución parcial de mercadería dañada",
  "creadoPor": 3,
  "detalles": [
    {
      "productoId": 1,
      "descripcion": "Pisco Quebranta 750ml (devolución 3 unidades)",
      "codigoUnidad": "NIU",
      "cantidad": 3,
      "precioUnitario": 40.00,
      "montoDescuento": 0,
      "montoIsc": 12.75
    }
  ]
}
```

### Validaciones para NC/ND

| Regla | Descripción |
|---|---|
| `documentoReferenciadoId` | Obligatorio — debe referenciar un documento existente del mismo negocio |
| `motivoReferencia` | Obligatorio — razón de la nota |
| Compatibilidad de tipos | Serie `F*` solo referencia facturas. Serie `B*` solo referencia boletas |
| Documento referenciado | No puede estar anulado |

**Error si tipo incompatible:**
```json
{
  "error": "Una nota con serie F* solo puede referenciar facturas, pero el documento referenciado es de tipo: boleta"
}
```

---

## PASO 14: Emitir Nota de Débito

**Endpoint:** `POST /restful/facturacion/documentos`

```json
{
  "negocioId": 1,
  "sedeId": 1,
  "serieId": 4,
  "tipoDocumento": "nota_debito",
  "rucEmisor": "20123456789",
  "razonSocialEmisor": "Licorería DrinkGo S.A.C.",
  "direccionEmisor": "Av. Arequipa 1234, Lima",
  "tipoDocumentoReceptor": "1",
  "numeroDocumentoReceptor": "45678912",
  "nombreReceptor": "Juan Pérez García",
  "moneda": "PEN",
  "documentoReferenciadoId": 1,
  "motivoReferencia": "Penalidad por cheque devuelto",
  "creadoPor": 3,
  "detalles": [
    {
      "descripcion": "Penalidad por cheque devuelto",
      "codigoUnidad": "ZZ",
      "cantidad": 1,
      "precioUnitario": 50.00,
      "montoDescuento": 0,
      "montoIsc": 0
    }
  ]
}
```

---

## PASO 15: Anular documento

**Endpoint:** `POST /restful/facturacion/{id}/anular?negocioId=1`

```json
{
  "motivoAnulacion": "Error en datos del receptor",
  "anuladoPor": 3
}
```

**Response esperado:** `200 OK`
```json
{
  "mensaje": "Documento anulado correctamente",
  "documento": {
    "id": 2,
    "numeroCompleto": "F001-00000001",
    "estado": "anulado",
    "estadoSunat": "anulado",
    "motivoAnulacion": "Error en datos del receptor",
    "anuladoPor": 3,
    "anuladoEn": "2026-02-16T15:30:00"
  }
}
```

### Reglas de anulación

| Escenario | Comportamiento |
|---|---|
| Documento NO enviado a SUNAT | Se anula localmente (sin PSE) |
| Factura aceptada por SUNAT | Envía **Comunicación de Baja** vía PSE. Máximo 7 días desde emisión |
| Boleta aceptada por SUNAT | Envía **Resumen Diario de Baja** vía PSE |
| Factura aceptada > 7 días | Rechaza. Debe emitir Nota de Crédito en su lugar |
| Documento ya anulado | Rechaza con error |

**Error si excede 7 días:**
```json
{
  "error": "No se puede anular una factura aceptada por SUNAT después de 7 días calendario desde su emisión (han pasado 15 días). Debe emitir una Nota de Crédito en su lugar."
}
```

---

## PASO 16: Generar PDF (simulado)

**Endpoint:** `GET /restful/facturacion/{id}/pdf?negocioId=1`

**Response esperado:** `200 OK`
```json
{
  "documentoId": 1,
  "numeroCompleto": "B001-00000001",
  "urlPdf": "https://drinkgo-facturacion.local/pdf/1/B001-00000001.pdf",
  "mensaje": "PDF generado exitosamente [SIMULACIÓN]"
}
```

---

## RESUMEN DE ENDPOINTS

| # | Método | URL | Descripción |
|---|--------|-----|-------------|
| 1 | `POST` | `/restful/facturacion/series` | Crear serie de facturación |
| 2 | `GET` | `/restful/facturacion/series?negocioId=` | Listar series (filtro opcional: `sedeId`) |
| 3 | `GET` | `/restful/facturacion/series/{id}?negocioId=` | Obtener serie por ID |
| 4 | `PUT` | `/restful/facturacion/series/{id}?negocioId=` | Actualizar serie (prefijo/estado) |
| 5 | `DELETE` | `/restful/facturacion/series/{id}?negocioId=` | Desactivar serie (DELETE lógico) |
| 6 | `POST` | `/restful/facturacion/documentos` | Emitir documento (boleta/factura/NC/ND) |
| 7 | `POST` | `/restful/facturacion/documentos/desde-pedido/{pedidoId}?negocioId=&serieId=&tipoDocumento=` | Emitir desde pedido existente |
| 8 | `GET` | `/restful/facturacion/documentos?negocioId=` | Buscar documentos con filtros |
| 9 | `GET` | `/restful/facturacion/{id}?negocioId=` | Obtener documento completo |
| 10 | `GET` | `/restful/facturacion/{id}/estado?negocioId=` | Consultar estado rápido |
| 11 | `POST` | `/restful/facturacion/{id}/enviar?negocioId=` | Enviar a SUNAT (simulado) |
| 12 | `POST` | `/restful/facturacion/{id}/anular?negocioId=` | Anular documento |
| 13 | `GET` | `/restful/facturacion/{id}/pdf?negocioId=` | Generar PDF (simulado) |

---

## REQUERIMIENTOS FUNCIONALES CUBIERTOS

| Código | Requerimiento | Implementado |
|--------|---------------|:---:|
| RF-FIN-001 | Gestionar Series de Comprobantes Electrónicos | ✅ |
| RF-FIN-002 | Gestionar Emisión de Comprobantes Electrónicos | ✅ |
| RF-FIN-003 | Enviar Comprobantes a SUNAT | ✅ (simulado) |
| RF-FIN-004 | Consultar y Controlar Estado de Comprobantes | ✅ |
| RF-FIN-005 | Gestionar Notas de Crédito y Débito | ✅ |
| RF-FIN-006 | Gestionar Anulación de Comprobantes | ✅ |
| RF-FIN-007 | Gestionar Reimpresión y Consulta de Comprobantes | ✅ |
| RF-CGL-001 | Configurar Parámetros Globales (IGV configurable) | ✅ |
| RF-VTA-004 | Emitir Comprobantes de Pago (Boleta/Factura) | ✅ |

---

## VALORES PERMITIDOS PARA ENUMS

### tipoDocumento
`boleta` | `factura` | `nota_credito` | `nota_debito` | `guia_remision`

### estado (documento)
`borrador` | `emitido` | `enviado` | `aceptado` | `anulado` | `error`

### estadoSunat
`pendiente` | `enviado` | `aceptado` | `rechazado` | `anulado` | `error`

### tipoDocumentoReceptor (código SUNAT)
| Código | Tipo |
|--------|------|
| `0` | No domiciliado |
| `1` | DNI |
| `4` | Carnet de extranjería |
| `6` | RUC |
| `7` | Pasaporte |
| `A` | Cédula diplomática |

### codigoTipoDocumentoSunat (Catálogo N° 01)
| Código | Tipo |
|--------|------|
| `01` | Factura |
| `03` | Boleta de Venta |
| `07` | Nota de Crédito |
| `08` | Nota de Débito |
| `09` | Guía de Remisión |
