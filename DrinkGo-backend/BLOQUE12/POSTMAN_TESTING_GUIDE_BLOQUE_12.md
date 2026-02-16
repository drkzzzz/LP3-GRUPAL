# 📮 GUÍA POSTMAN - BLOQUE XII: GASTOS E INGRESOS

**Módulo:** Gestión de Gastos y Control de Egresos  
**Base URL:** `http://localhost:8080/restful/gastos`  
**Fecha:** 15/02/2026  
**Estado:** ✅ Todos los endpoints probados y funcionando

---

## 📋 TABLA DE CONTENIDOS

1. [Configuración Inicial](#configuración-inicial)
2. [Flujo de Trabajo de Estados](#flujo-de-trabajo-de-estados)
3. [Endpoints REST](#endpoints-rest)
   - [POST - Crear Gasto](#1-post---crear-gasto)
   - [GET - Listar Gastos](#2-get---listar-gastos)
   - [GET - Obtener Gasto por ID](#3-get---obtener-gasto-por-id)
   - [PUT - Actualizar Gasto](#4-put---actualizar-gasto)
   - [PUT - Aprobar Gasto](#5-put---aprobar-gasto)
   - [PUT - Rechazar Gasto](#6-put---rechazar-gasto)
   - [PUT - Marcar como Pagado](#7-put---marcar-como-pagado)
   - [PUT - Anular Gasto](#8-put---anular-gasto)
   - [DELETE - Eliminar Gasto](#9-delete---eliminar-gasto)
4. [Escenarios de Prueba Completos](#escenarios-de-prueba-completos)
5. [Validaciones y Reglas de Negocio](#validaciones-y-reglas-de-negocio)

---

## 🔧 CONFIGURACIÓN INICIAL

### Datos de Prueba Requeridos

Antes de usar los endpoints, asegúrate de tener estos datos en la BD:

```sql
-- Negocio de prueba
negocio_id: 1

-- Sede de prueba  
sede_id: 1

-- Categorías de gastos (ya creadas en la BD)
1 - Servicios Públicos (6300)
2 - Alquiler de Local (6310)
3 - Sueldos y Salarios (6200)
4 - Marketing y Publicidad (6370)
5 - Mantenimiento (6350)
6 - Transporte y Envíos (6340)
7 - Limpieza y Suministros (6360)
8 - Otros Gastos (6500)

-- Usuario registrado (para registrado_por y aprobado_por)
usuario_id: 2 (admin@drinkgo.com)
```

### Crear Usuario de Prueba (Si no existe)

**Método:** `POST`  
**URL:** `http://localhost:8080/restful/registros`

```json
{
  "nombres": "Admin",
  "apellidos": "DrinkGo",
  "email": "admin@drinkgo.com",
  "llaveSecreta": "admin123",
  "negocioId": 1
}
```

**Respuesta (201 Created):**
```json
{
  "mensaje": "Registro exitoso",
  "clienteId": "26679d96-acad-4588-b5f0-f0f38d17c815",
  "negocioId": 1,
  "email": "admin@drinkgo.com"
}
```

---

## 🔄 FLUJO DE TRABAJO DE ESTADOS

```
┌─────────────┐
│  PENDIENTE  │ (Estado inicial al crear)
└──────┬──────┘
       │
       ├──────────────────────────────────────────┐
       │                                          │
       ▼                                          ▼
┌──────────┐                              ┌────────────┐
│ APROBADO │                              │ RECHAZADO  │
└─────┬────┘                              └────────────┘
      │
      │
      ▼
┌──────────┐
│  PAGADO  │
└─────┬────┘
      │
      ▼
┌──────────┐
│ ANULADO  │ (No se puede anular si está PAGADO)
└──────────┘
```

### Reglas de Transición:
- ✅ **PENDIENTE → APROBADO** (aprobar)
- ✅ **PENDIENTE → RECHAZADO** (rechazar)
- ✅ **APROBADO → PAGADO** (marcar como pagado)
- ✅ **APROBADO → ANULADO** (anular)
- ✅ **PENDIENTE → ANULADO** (anular)
- ✅ **RECHAZADO → ANULADO** (anular)
- ❌ **PAGADO → ANULADO** (NO PERMITIDO)
- ✅ **DELETE solo si PENDIENTE**
- ✅ **UPDATE solo si PENDIENTE**

---

## 🌐 ENDPOINTS REST

### 1. POST - Crear Gasto

Crea un nuevo gasto en estado **PENDIENTE**.

**URL:** `POST http://localhost:8080/restful/gastos`  
**Headers:** `Content-Type: application/json`

**Body (JSON):**
```json
{
  "negocioId": 1,
  "sedeId": 1,
  "categoriaId": 1,
  "descripcion": "Pago de servicios de luz - Diciembre 2025",
  "monto": 85.50,
  "montoImpuesto": 15.39,
  "total": 100.89,
  "moneda": "PEN",
  "fechaGasto": "2025-12-15",
  "metodoPago": "transferencia_bancaria",
  "referenciaPago": "TRANS-2025-12-001",
  "registradoPor": 2
}
```

**Campos Opcionales:**
```json
{
  "proveedorId": 5,
  "esRecurrente": true,
  "periodoRecurrencia": "mensual",
  "urlComprobante": "https://example.com/comprobante.pdf",
  "notas": "Notas adicionales"
}
```

**Respuesta (201 Created):**
```json
{
  "id": 4,
  "negocioId": 1,
  "sedeId": 1,
  "numeroGasto": "GAS-1-20260215102714",
  "categoriaId": 1,
  "categoriaNombre": "Servicios Públicos",
  "proveedorId": null,
  "descripcion": "Pago de servicios de luz - Diciembre 2025",
  "monto": 85.50,
  "montoImpuesto": 15.39,
  "total": 100.89,
  "moneda": "PEN",
  "fechaGasto": "2025-12-15",
  "metodoPago": "transferencia_bancaria",
  "referenciaPago": "TRANS-2025-12-001",
  "urlComprobante": null,
  "estado": "pendiente",
  "esRecurrente": false,
  "periodoRecurrencia": null,
  "aprobadoPor": null,
  "registradoPor": 2,
  "notas": null,
  "creadoEn": "2026-02-15T10:27:14",
  "actualizadoEn": "2026-02-15T10:27:14"
}
```

**💡 PowerShell:**
```powershell
$body = @{
    negocioId = 1
    sedeId = 1
    categoriaId = 1
    descripcion = "Pago de servicios de luz - Diciembre 2025"
    monto = 85.50
    montoImpuesto = 15.39
    total = 100.89
    moneda = "PEN"
    fechaGasto = "2025-12-15"
    metodoPago = "transferencia_bancaria"
    referenciaPago = "TRANS-2025-12-001"
    registradoPor = 2
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos" `
    -Method POST -Body $body -ContentType "application/json"
```

**Validaciones:**
- ✅ `negocioId`, `sedeId`, `categoriaId` requeridos
- ✅ `descripcion`, `total`, `moneda`, `fechaGasto` requeridos
- ✅ `registradoPor` requerido (debe existir en tabla usuarios)
- ✅ Categoría debe existir y pertenecer al negocio
- ✅ `metodoPago` debe ser válido: efectivo, transferencia_bancaria, tarjeta_credito, cheque, otro
- ✅ Genera automáticamente `numeroGasto` en formato: `GAS-{negocioId}-{timestamp}`

---

### 2. GET - Listar Gastos

Obtiene todos los gastos de un negocio, opcionalmente filtrados por sede.

**URL:** `GET http://localhost:8080/restful/gastos?negocioId=1`  
**URL (con filtro):** `GET http://localhost:8080/restful/gastos?negocioId=1&sedeId=1`

**Respuesta (200 OK):**
```json
[
  {
    "id": 4,
    "numeroGasto": "GAS-1-20260215102714",
    "categoriaNombre": "Servicios Públicos",
    "descripcion": "Pago de servicios de luz - Diciembre 2025",
    "total": 100.89,
    "estado": "pendiente",
    "fechaGasto": "2025-12-15",
    "...": "..."
  },
  {
    "id": 5,
    "numeroGasto": "GAS-1-20260215102840",
    "categoriaNombre": "Marketing y Publicidad",
    "descripcion": "Campaña publicitaria",
    "total": 295.00,
    "estado": "rechazado",
    "fechaGasto": "2025-12-16",
    "...": "..."
  }
]
```

**💡 PowerShell:**
```powershell
# Todos los gastos
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos?negocioId=1"

# Filtrado por sede
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos?negocioId=1&sedeId=1"
```

---

### 3. GET - Obtener Gasto por ID

Obtiene un gasto específico por su ID.

**URL:** `GET http://localhost:8080/restful/gastos/{id}?negocioId=1`

**Ejemplo:** `GET http://localhost:8080/restful/gastos/4?negocioId=1`

**Respuesta (200 OK):** (Mismo formato que POST)

**Respuesta (404 Not Found):**
```json
{
  "error": "Gasto no encontrado con ID: 999"
}
```

**💡 PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/4?negocioId=1"
```

---

### 4. PUT - Actualizar Gasto

Actualiza un gasto existente. **Solo funciona si el gasto está en estado PENDIENTE**.

**URL:** `PUT http://localhost:8080/restful/gastos/{id}?negocioId=1`  
**Headers:** `Content-Type: application/json`

**Body (JSON) - Campos editables:**
```json
{
  "categoriaId": 2,
  "descripcion": "Pago de servicios de luz y agua - Diciembre 2025",
  "monto": 120.00,
  "montoImpuesto": 21.60,
  "total": 141.60,
  "metodoPago": "transferencia_bancaria",
  "referenciaPago": "TRANS-2025-12-002",
  "urlComprobante": "https://example.com/nuevo.pdf",
  "notas": "Actualizado con agua"
}
```

**Campos NO editables:**
- ❌ `negocioId`, `sedeId`, `numeroGasto`
- ❌ `registradoPor`, `estado`, `aprobadoPor`
- ❌ Fechas de auditoría

**Respuesta (200 OK):** Gasto actualizado completo

**Respuesta (400 Bad Request):**
```json
{
  "error": "Solo se pueden actualizar gastos en estado 'pendiente'"
}
```

**💡 PowerShell:**
```powershell
$body = @{
    categoriaId = 2
    descripcion = "Pago de servicios de luz y agua - Diciembre 2025"
    monto = 120.00
    montoImpuesto = 21.60
    total = 141.60
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/4?negocioId=1" `
    -Method PUT -Body $body -ContentType "application/json"
```

---

### 5. PUT - Aprobar Gasto

Cambia el estado de PENDIENTE a APROBADO.

**URL:** `PUT http://localhost:8080/restful/gastos/{id}/aprobar?negocioId=1`  
**Headers:** `Content-Type: application/json`

**Body (JSON):**
```json
{
  "aprobadoPor": 2,
  "notas": "Gasto aprobado por gerencia"
}
```

**Respuesta (200 OK):**
```json
{
  "id": 4,
  "estado": "aprobado",
  "aprobadoPor": 2,
  "notas": "APROBACIÓN: Gasto aprobado por gerencia",
  "actualizadoEn": "2026-02-15T10:27:43",
  "...": "..."
}
```

**Validaciones:**
- ✅ El gasto debe estar en estado `pendiente`
- ✅ `aprobadoPor` es requerido
- ✅ Las notas se prefijan con "APROBACIÓN: "

**💡 PowerShell:**
```powershell
$body = @{
    aprobadoPor = 2
    notas = "Gasto aprobado por gerencia"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/4/aprobar?negocioId=1" `
    -Method PUT -Body $body -ContentType "application/json"
```

---

### 6. PUT - Rechazar Gasto

Cambia el estado de PENDIENTE a RECHAZADO.

**URL:** `PUT http://localhost:8080/restful/gastos/{id}/rechazar?negocioId=1`  
**Headers:** `Content-Type: application/json`

**Body (JSON):**
```json
{
  "aprobadoPor": 2,
  "notas": "Gasto rechazado - presupuesto insuficiente"
}
```

**Respuesta (200 OK):**
```json
{
  "id": 5,
  "estado": "rechazado",
  "aprobadoPor": 2,
  "notas": "RECHAZO: Gasto rechazado - presupuesto insuficiente",
  "actualizadoEn": "2026-02-15T10:28:53",
  "...": "..."
}
```

**Validaciones:**
- ✅ El gasto debe estar en estado `pendiente`
- ✅ `aprobadoPor` es requerido (quien lo rechaza)
- ✅ Las notas se prefijan con "RECHAZO: "

**💡 PowerShell:**
```powershell
$body = @{
    aprobadoPor = 2
    notas = "Gasto rechazado - presupuesto insuficiente"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/5/rechazar?negocioId=1" `
    -Method PUT -Body $body -ContentType "application/json"
```

---

### 7. PUT - Marcar como Pagado

Cambia el estado de APROBADO a PAGADO.

**URL:** `PUT http://localhost:8080/restful/gastos/{id}/pagar?negocioId=1`

**Sin Body** (no requiere parámetros adicionales)

**Respuesta (200 OK):**
```json
{
  "id": 4,
  "estado": "pagado",
  "actualizadoEn": "2026-02-15T10:27:54",
  "...": "..."
}
```

**Validaciones:**
- ✅ El gasto debe estar en estado `aprobado`
- ❌ No se puede marcar como pagado si está en otro estado

**Respuesta (400 Bad Request):**
```json
{
  "error": "Solo se pueden marcar como pagados los gastos aprobados"
}
```

**💡 PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/4/pagar?negocioId=1" `
    -Method PUT
```

---

### 8. PUT - Anular Gasto

Cambia el estado a ANULADO. **NO se puede anular si el gasto ya está PAGADO**.

**URL:** `PUT http://localhost:8080/restful/gastos/{id}/anular?negocioId=1`

**Sin Body**

**Respuesta (200 OK):**
```json
{
  "id": 6,
  "estado": "anulado",
  "actualizadoEn": "2026-02-15T10:29:34",
  "...": "..."
}
```

**Validaciones:**
- ✅ Se puede anular si está: pendiente, aprobado, rechazado
- ❌ NO se puede anular si está: pagado

**Respuesta (400 Bad Request):**
```json
{
  "error": "No se puede anular un gasto que ya fue pagado"
}
```

**💡 PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/6/anular?negocioId=1" `
    -Method PUT
```

---

### 9. DELETE - Eliminar Gasto

Elimina físicamente un gasto de la base de datos. **Solo funciona si el gasto está PENDIENTE**.

**URL:** `DELETE http://localhost:8080/restful/gastos/{id}?negocioId=1`

**Sin Body**

**Respuesta (200 OK):**
```json
{
  "mensaje": "Gasto eliminado correctamente"
}
```

**Validaciones:**
- ✅ Solo se pueden eliminar gastos en estado `pendiente`
- ❌ No se pueden eliminar gastos aprobados, rechazados, pagados o anulados

**Respuesta (400 Bad Request):**
```json
{
  "error": "Solo se pueden eliminar gastos en estado 'pendiente'"
}
```

**💡 PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/7?negocioId=1" `
    -Method DELETE
```

---

## 🧪 ESCENARIOS DE PRUEBA COMPLETOS

### Escenario 1: Flujo Completo - Aprobación y Pago ✅

```powershell
# 1. Crear gasto
$gasto = @{
    negocioId = 1; sedeId = 1; categoriaId = 1
    descripcion = "Pago de luz"; total = 100.89
    moneda = "PEN"; fechaGasto = "2025-12-15"
    metodoPago = "transferencia_bancaria"; registradoPor = 2
} | ConvertTo-Json
$resultado = Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos" `
    -Method POST -Body $gasto -ContentType "application/json"
$gastoId = $resultado.id

# 2. Actualizar (opcional)
$update = @{ descripcion = "Pago de luz y agua"; total = 141.60 } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/$gastoId?negocioId=1" `
    -Method PUT -Body $update -ContentType "application/json"

# 3. Aprobar
$aprobar = @{ aprobadoPor = 2; notas = "Aprobado" } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/$gastoId/aprobar?negocioId=1" `
    -Method PUT -Body $aprobar -ContentType "application/json"

# 4. Marcar como pagado
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/$gastoId/pagar?negocioId=1" `
    -Method PUT
```

**Resultado esperado:** Gasto en estado `pagado` ✅

---

### Escenario 2: Flujo de Rechazo ❌

```powershell
# 1. Crear gasto
$gasto = @{
    negocioId = 1; sedeId = 1; categoriaId = 4
    descripcion = "Campaña publicitaria"; total = 295.00
    moneda = "PEN"; fechaGasto = "2025-12-16"
    metodoPago = "tarjeta_credito"; registradoPor = 2
} | ConvertTo-Json
$resultado = Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos" `
    -Method POST -Body $gasto -ContentType "application/json"
$gastoId = $resultado.id

# 2. Rechazar
$rechazar = @{
    aprobadoPor = 2
    notas = "Presupuesto insuficiente"
} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/$gastoId/rechazar?negocioId=1" `
    -Method PUT -Body $rechazar -ContentType "application/json"
```

**Resultado esperado:** Gasto en estado `rechazado` ✅

---

### Escenario 3: Anulación ⚠️

```powershell
# 1. Crear y aprobar
$gasto = @{
    negocioId = 1; sedeId = 1; categoriaId = 5
    descripcion = "Reparación"; total = 212.40
    moneda = "PEN"; fechaGasto = "2025-12-17"
    metodoPago = "efectivo"; registradoPor = 2
} | ConvertTo-Json
$resultado = Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos" `
    -Method POST -Body $gasto -ContentType "application/json"
$gastoId = $resultado.id

$aprobar = @{ aprobadoPor = 2 } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/$gastoId/aprobar?negocioId=1" `
    -Method PUT -Body $aprobar -ContentType "application/json"

# 2. Anular (antes de pagar)
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/$gastoId/anular?negocioId=1" `
    -Method PUT
```

**Resultado esperado:** Gasto en estado `anulado` ✅

---

### Escenario 4: Eliminación (Solo Pendientes) 🗑️

```powershell
# 1. Crear gasto
$gasto = @{
    negocioId = 1; sedeId = 1; categoriaId = 8
    descripcion = "Gasto de prueba"; total = 59.00
    moneda = "PEN"; fechaGasto = "2025-12-18"
    metodoPago = "efectivo"; registradoPor = 2
} | ConvertTo-Json
$resultado = Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos" `
    -Method POST -Body $gasto -ContentType "application/json"
$gastoId = $resultado.id

# 2. Eliminar (debe estar PENDIENTE)
Invoke-RestMethod -Uri "http://localhost:8080/restful/gastos/$gastoId?negocioId=1" `
    -Method DELETE
```

**Resultado esperado:** Mensaje "Gasto eliminado correctamente" ✅

---

## ✔️ VALIDACIONES Y REGLAS DE NEGOCIO

### Validaciones al Crear

| Campo | Validación | Ejemplo |
|-------|-----------|---------|
| `negocioId` | Requerido, debe existir | 1 |
| `sedeId` | Requerido, debe existir, NOT NULL | 1 |
| `categoriaId` | Requerido, debe existir y pertenecer al negocio | 1 |
| `descripcion` | Requerido, máximo 250 caracteres | "Pago de luz" |
| `total` | Requerido, debe ser > 0 | 100.89 |
| `moneda` | Requerido, 3 caracteres | "PEN", "USD" |
| `fechaGasto` | Requerido, formato DATE | "2025-12-15" |
| `metodoPago` | Requerido, debe ser valor ENUM válido | "efectivo", "transferencia_bancaria", "tarjeta_credito", "cheque", "otro" |
| `registradoPor` | Requerido, debe existir en usuarios, NOT NULL | 2 |
| `proveedorId` | Opcional, debe existir si se envía | 5 |
| `esRecurrente` | Opcional, booleano | true/false |
| `periodoRecurrencia` | Opcional si `esRecurrente=true` | "semanal", "quincenal", "mensual", "trimestral", "anual" |

### Reglas de Estado

| Operación | Estado Requerido | Cambio de Estado |
|-----------|------------------|------------------|
| **Crear** | - | ➜ `pendiente` |
| **Actualizar** | `pendiente` | No cambia |
| **Aprobar** | `pendiente` | ➜ `aprobado` |
| **Rechazar** | `pendiente` | ➜ `rechazado` |
| **Pagar** | `aprobado` | ➜ `pagado` |
| **Anular** | NO `pagado` | ➜ `anulado` |
| **Eliminar** | `pendiente` | Se elimina físicamente |

### Generación Automática

- **numeroGasto:** Formato `GAS-{negocioId}-{yyyyMMddHHmmss}`
  - Ejemplo: `GAS-1-20260215102714`
- **categoriaNombre:** Se obtiene automáticamente mediante JOIN con `categorias_gasto`
- **creadoEn, actualizadoEn:** Timestamps automáticos

### Prefijos en Notas

- **Aprobación:** Las notas se prefijan con `"APROBACIÓN: "`
- **Rechazo:** Las notas se prefijan con `"RECHAZO: "`
- Esto permite distinguir el tipo de acción en el historial

### Estados de Gasto (ENUM)

```java
public enum EstadoGasto {
    pendiente,      // Estado inicial
    aprobado,       // Aprobado por gerencia
    pagado,         // Ya fue pagado
    rechazado,      // Rechazado por gerencia
    anulado         // Anulado por cualquier razón
}
```

### Métodos de Pago (ENUM)

```java
public enum MetodoPago {
    efectivo,
    transferencia_bancaria,
    tarjeta_credito,
    cheque,
    otro
}
```

### Período de Recurrencia (ENUM)

```java
public enum PeriodoRecurrencia {
    semanal,
    quincenal,
    mensual,
    trimestral,
    anual
}
```

---

## 📊 RESUMEN DE PRUEBAS REALIZADAS

| Endpoint | Método | Estado | Resultado |
|----------|--------|--------|-----------|
| `/gastos` | POST | ✅ | Gasto creado con ID 4, 5, 6, 7 |
| `/gastos?negocioId=1` | GET | ✅ | Lista de 3 gastos (ID 7 eliminado) |
| `/gastos/{id}` | GET | ✅ | Gasto individual obtenido |
| `/gastos/{id}` | PUT | ✅ | Gasto actualizado (solo pendiente) |
| `/gastos/{id}/aprobar` | PUT | ✅ | Estado: pendiente → aprobado |
| `/gastos/{id}/rechazar` | PUT | ✅ | Estado: pendiente → rechazado |
| `/gastos/{id}/pagar` | PUT | ✅ | Estado: aprobado → pagado |
| `/gastos/{id}/anular` | PUT | ✅ | Estado: aprobado → anulado |
| `/gastos/{id}` | DELETE | ✅ | Gasto eliminado (solo pendiente) |

**Estado Final de Gastos en BD:**
- **ID 4:** `pagado` (flujo completo: pendiente → aprobado → pagado)
- **ID 5:** `rechazado` (flujo de rechazo)
- **ID 6:** `anulado` (flujo de anulación)
- **ID 7:** **ELIMINADO** (no aparece en listado)

---

## 🎯 CONCLUSIONES

✅ **Todos los endpoints funcionan correctamente**  
✅ **Flujos de estado implementados según requerimientos**  
✅ **Validaciones de negocio funcionando**  
✅ **Generación automática de numeroGasto**  
✅ **Restricciones de base de datos aplicadas (sede_id NOT NULL, registrado_por NOT NULL)**  
✅ **Categorías enriquecidas en respuestas (categoriaNombre)**

### Mejoras Aplicadas desde Bloque 11

1. ✅ Restricciones NOT NULL aplicadas proactivamente
2. ✅ 8 categorías de gastos creadas con códigos contables
3. ✅ Validaciones exhaustivas en Service layer
4. ✅ Flujo de estados bien definido y testeado
5. ✅ Documentación completa con ejemplos PowerShell

---

**Fecha de pruebas:** 15/02/2026  
**Versión Spring Boot:** 4.0.2  
**Base de datos:** MySQL 5.5.5 (drinkgo_db)  
**Puerto:** 8080

🎉 **BLOQUE XII COMPLETADO EXITOSAMENTE** 🎉
