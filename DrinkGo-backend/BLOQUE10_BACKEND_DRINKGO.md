# BLOQUE 10 — FACTURACIÓN ELECTRÓNICA (SUNAT)
## DrinkGo Backend — Documentación Técnica Completa

**Fecha:** 2026-02-16  
**Versión:** 1.0  
**Módulo:** Bloque 10 — Facturación Electrónica  
**Framework:** Spring Boot 4.0.2 / Java 17  
**Base de datos:** MySQL (XAMPP) — `drinkgo_db`

---

## ÍNDICE

1. [Arquitectura Aplicada](#1-arquitectura-aplicada)
2. [Modelo Multi-Tenant](#2-modelo-multi-tenant)
3. [Tablas Utilizadas](#3-tablas-utilizadas)
4. [Estructura de Archivos](#4-estructura-de-archivos)
5. [Flujo Completo de Emisión](#5-flujo-completo-de-emisión)
6. [Flujo de Simulación PSE](#6-flujo-de-simulación-pse)
7. [Preparación para Integración Real](#7-preparación-para-integración-real)
8. [Endpoints Disponibles](#8-endpoints-disponibles)
9. [Ejemplos Completos Request/Response](#9-ejemplos-completos-requestresponse)
10. [Cómo Probar en Postman](#10-cómo-probar-en-postman)
11. [Diagrama Textual del Proceso](#11-diagrama-textual-del-proceso)
12. [Responsabilidad Legal](#12-responsabilidad-legal)
13. [Confirmación de No Modificación de BD](#13-confirmación-de-no-modificación-de-bd)

---

## 1. ARQUITECTURA APLICADA

El módulo sigue una arquitectura por capas estricta, alineada con los estándares del proyecto:

```
controller/    → Manejo exclusivo de request/response HTTP
dto/           → Objetos de transferencia (Request/Response separados)
entity/        → Mapeo JPA contra tablas existentes
repository/    → Acceso a datos vía Spring Data JPA
service/       → Lógica de negocio con @Transactional
exception/     → Manejo global de errores (@ControllerAdvice)
integration/
  └── pse/     → Capa desacoplada para PSE (interfaz + simulación)
```

### Principios de diseño

| Principio | Implementación |
|-----------|---------------|
| **Separación de responsabilidades** | Controller solo HTTP, Service solo lógica, Repository solo datos |
| **Desacoplamiento PSE** | Interface `PseClient` + implementación `PseSimulationClient` |
| **Multi-tenant** | Filtro por `negocio_id` en TODAS las operaciones |
| **Transaccionalidad** | `@Transactional` en emisión, envío, anulación |
| **DTOs obligatorios** | Request y Response separados, sin exponer entidades |
| **Inmutabilidad del tenant** | `negocio_id` no se puede modificar en updates |

---

## 2. MODELO MULTI-TENANT

El sistema es **multi-tenant por negocio**. Cada negocio (licorería) opera de forma completamente aislada.

### Reglas implementadas

1. **Todas las consultas filtran por `negocio_id`** — Pasado como `@RequestParam` en cada endpoint.
2. **No se permite acceso cruzado** — Si un documento no pertenece al `negocio_id` indicado, se retorna error 404.
3. **`negocio_id` no se modifica en updates** — Solo se establece al crear el recurso.
4. **Consistencia sede-serie-documento** — Se valida que la serie pertenezca a la misma sede del documento.
5. **Unicidad por tenant** — Las series son únicas por combinación `negocio_id + sede_id + tipo_documento + prefijo_serie`.

### Flujo de validación multi-tenant

```
Request → Controller (recibe negocioId) → Service (filtra por negocioId)
  → Repository (findByIdAndNegocioId) → Si no existe → RuntimeException → 404
```

---

## 3. TABLAS UTILIZADAS

Se utilizan **exclusivamente** las 3 tablas existentes del Bloque 10:

### `series_facturacion`
| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT PK | Identificador único |
| negocio_id | BIGINT FK | Tenant propietario |
| sede_id | BIGINT FK | Sede asociada |
| tipo_documento | ENUM | boleta, factura, nota_credito, nota_debito, guia_remision |
| prefijo_serie | VARCHAR(10) | Ej: B001, F001, BC01 |
| numero_actual | INT | Último correlativo emitido |
| esta_activo | TINYINT(1) | DELETE lógico |

### `documentos_facturacion`
| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT PK | Identificador único |
| negocio_id | BIGINT FK | Tenant propietario |
| sede_id | BIGINT FK | Sede donde se emite |
| serie_id | BIGINT FK | Serie utilizada |
| tipo_documento | ENUM | Tipo de comprobante |
| numero_completo | VARCHAR(30) | Ej: B001-00000001 |
| ruc_emisor, razon_social_emisor | VARCHAR | Snapshot del negocio |
| nombre_receptor | VARCHAR | Receptor del documento |
| subtotal, total_gravado, total_igv, total | DECIMAL | Montos calculados |
| estado | ENUM | borrador, emitido, enviado, aceptado, anulado, error |
| estado_sunat | ENUM | pendiente, enviado, aceptado, rechazado, anulado, error |
| hash_sunat, url_xml_sunat, url_cdr_sunat | VARCHAR | Datos de SUNAT |
| documento_referenciado_id | BIGINT FK | Para notas crédito/débito |

### `detalle_documentos_facturacion`
| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT PK | Identificador único |
| documento_id | BIGINT FK | Documento padre |
| producto_id | BIGINT FK | Producto referenciado |
| descripcion | VARCHAR(500) | Descripción del item |
| cantidad, precio_unitario | DECIMAL | Valores del item |
| monto_gravado, monto_igv, total | DECIMAL | Impuestos calculados |

---

## 4. ESTRUCTURA DE ARCHIVOS

```
src/main/java/DrinkGo/DrinkGo_backend/
├── controller/
│   ├── SerieFacturacionController.java      ← CRUD de series
│   └── DocumentoFacturacionController.java  ← Emisión, envío, anulación, PDF
├── dto/
│   ├── CreateSerieFacturacionRequest.java
│   ├── UpdateSerieFacturacionRequest.java
│   ├── SerieFacturacionResponse.java
│   ├── CreateDocumentoFacturacionRequest.java
│   ├── DetalleDocumentoFacturacionRequest.java
│   ├── DocumentoFacturacionResponse.java
│   ├── DetalleDocumentoFacturacionResponse.java
│   ├── AnularDocumentoRequest.java
│   └── EnvioSunatResponse.java
├── entity/
│   ├── SerieFacturacion.java
│   ├── DocumentoFacturacion.java
│   └── DetalleDocumentoFacturacion.java
├── repository/
│   ├── SerieFacturacionRepository.java
│   ├── DocumentoFacturacionRepository.java
│   └── DetalleDocumentoFacturacionRepository.java
├── service/
│   ├── SerieFacturacionService.java
│   └── DocumentoFacturacionService.java
└── integration/
    └── pse/
        ├── PseClient.java              ← Interface desacoplada
        ├── PseResponse.java            ← DTO de respuesta PSE
        └── PseSimulationClient.java    ← Implementación simulada
```

**Total: 17 archivos nuevos** — Ningún archivo existente fue modificado.

---

## 5. FLUJO COMPLETO DE EMISIÓN

### Paso a paso

```
1. CREAR SERIE (una vez por sede/tipo)
   POST /restful/facturacion/series
   → Registra serie B001 para boletas en sede 1

2. EMITIR DOCUMENTO
   POST /restful/facturacion/documentos
   → Valida serie activa y consistencia multi-tenant
   → Incrementa numero_actual de la serie
   → Genera numero_completo: "B001-00000001"
   → Calcula IGV (18%) por cada item
   → Guarda snapshot del emisor (RUC, razón social)
   → Guarda detalles del documento
   → Estado: emitido / pendiente

3. ENVIAR A SUNAT (simulado)
   POST /restful/facturacion/{id}/enviar
   → Invoca PseClient.enviarDocumento()
   → Recibe PseResponse con resultado simulado
   → Actualiza estado_sunat, hash, URLs, timestamps

4. CONSULTAR ESTADO
   GET /restful/facturacion/{id}/estado
   → Retorna resumen del estado del documento

5. GENERAR PDF (simulado)
   GET /restful/facturacion/{id}/pdf
   → Genera URL simulada de descarga
```

### Cálculo de impuestos (IGV 18%)

Para cada item del documento:
```
subtotal_item = cantidad × precio_unitario
monto_gravado = subtotal_item - descuento
monto_igv = monto_gravado × 0.18
total_item = monto_gravado + monto_igv
```

Totales del documento:
```
subtotal = Σ subtotal_item
total_gravado = Σ monto_gravado_item
total_igv = Σ monto_igv_item
total = total_gravado + total_igv
```

---

## 6. FLUJO DE SIMULACIÓN PSE

### Arquitectura desacoplada

```
DocumentoFacturacionService
    │
    ▼
PseClient (interface)
    │
    ├── PseSimulationClient (@Component - ACTUAL)
    │     80% → aceptado
    │     15% → observado (aceptado con warnings)
    │     5%  → rechazado
    │
    ├── (Futuro) PseNubefactClient
    └── (Futuro) PseEfactClient
```

### Comportamiento simulado

| Resultado | Probabilidad | Código SUNAT | Estado |
|-----------|-------------|-------------|--------|
| Aceptado | 80% | 0 | estado_sunat = aceptado |
| Observado | 15% | 4252 | estado_sunat = aceptado (con observación) |
| Rechazado | 5% | 2033 | estado_sunat = rechazado |

### ¿Qué genera la simulación?

- **Hash SHA-256** simulado basado en datos del documento
- **Ticket SUNAT** con formato `SIM-yyyyMMddHHmmss-XXXX`
- **URLs de XML y CDR** simuladas
- **Latencia** simulada de 50-200ms para realismo

---

## 7. PREPARACIÓN PARA INTEGRACIÓN REAL

El sistema está diseñado para que la integración con un PSE real sea **transparente**.

### Pasos para integrar un PSE real (ej: Nubefact)

1. **Crear nueva clase** `PseNubefactClient` que implemente `PseClient`
2. **Configurar credenciales** del negocio (RUC, usuario SOL, clave SOL, API key)
3. **Marcar como `@Primary`** o usar `@Profile("produccion")` para seleccionar implementación
4. **No se modifica ningún servicio ni controller** — La inyección de dependencias resuelve automáticamente

### Datos que cada negocio necesitará configurar (futuro)

| Campo | Descripción |
|-------|-------------|
| RUC | RUC del negocio ante SUNAT |
| Usuario SOL | Usuario de acceso a SUNAT Online |
| Clave SOL | Contraseña SOL |
| API Key PSE | Clave de acceso al proveedor PSE |
| Ambiente | beta / producción |
| Nombre del proveedor | Nubefact, Efact, etc. |

### Ejemplo de implementación futura

```java
@Component
@Profile("produccion")
@Primary
public class PseNubefactClient implements PseClient {

    @Override
    public PseResponse enviarDocumento(DocumentoFacturacion documento) {
        // Llamada real a API de Nubefact
        // POST https://api.nubefact.com/tickets
        // Headers: Authorization: Bearer {API_KEY}
        // Body: XML del documento
    }

    @Override
    public String getNombreProveedor() {
        return "Nubefact";
    }
}
```

---

## 8. ENDPOINTS DISPONIBLES

### Series de Facturación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/restful/facturacion/series?negocioId={id}` | Listar series |
| GET | `/restful/facturacion/series?negocioId={id}&sedeId={id}` | Listar series por sede |
| GET | `/restful/facturacion/series/{id}?negocioId={id}` | Obtener serie por ID |
| POST | `/restful/facturacion/series` | Crear nueva serie |
| PUT | `/restful/facturacion/series/{id}?negocioId={id}` | Actualizar serie |
| DELETE | `/restful/facturacion/series/{id}?negocioId={id}` | Desactivar serie (lógico) |

### Documentos de Facturación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/restful/facturacion/documentos?negocioId={id}` | Listar documentos |
| POST | `/restful/facturacion/documentos` | Emitir documento |
| GET | `/restful/facturacion/{id}/estado?negocioId={id}` | Consultar estado |
| POST | `/restful/facturacion/{id}/enviar?negocioId={id}` | Enviar a SUNAT (simulado) |
| POST | `/restful/facturacion/{id}/anular?negocioId={id}` | Anular documento |
| GET | `/restful/facturacion/{id}/pdf?negocioId={id}` | Obtener PDF simulado |

### Filtros disponibles en GET /documentos

| Parámetro | Tipo | Ejemplo |
|-----------|------|---------|
| negocioId | Long (obligatorio) | 1 |
| sedeId | Long | 1 |
| tipoDocumento | String | boleta, factura |
| estado | String | emitido, aceptado, anulado |
| estadoSunat | String | pendiente, aceptado, rechazado |
| fechaDesde | LocalDate | 2026-01-01 |
| fechaHasta | LocalDate | 2026-12-31 |
| numeroCompleto | String | B001-0000 |

---

## 9. EJEMPLOS COMPLETOS REQUEST/RESPONSE

### 9.1 Crear Serie

**Request:**
```http
POST /restful/facturacion/series
Content-Type: application/json

{
    "negocioId": 1,
    "sedeId": 1,
    "tipoDocumento": "boleta",
    "prefijoSerie": "B001"
}
```

**Response (201):**
```json
{
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "tipoDocumento": "boleta",
    "prefijoSerie": "B001",
    "numeroActual": 0,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:30:00",
    "actualizadoEn": "2026-02-16T10:30:00"
}
```

### 9.2 Crear Serie de Factura

**Request:**
```http
POST /restful/facturacion/series
Content-Type: application/json

{
    "negocioId": 1,
    "sedeId": 1,
    "tipoDocumento": "factura",
    "prefijoSerie": "F001"
}
```

### 9.3 Emitir Boleta

**Request:**
```http
POST /restful/facturacion/documentos
Content-Type: application/json

{
    "negocioId": 1,
    "sedeId": 1,
    "serieId": 1,
    "tipoDocumento": "boleta",
    "rucEmisor": "20123456789",
    "razonSocialEmisor": "Licorería DrinkGo SAC",
    "direccionEmisor": "Av. La Marina 1234, Lima",
    "tipoDocumentoReceptor": "1",
    "numeroDocumentoReceptor": "45678912",
    "nombreReceptor": "Juan Pérez García",
    "direccionReceptor": "Calle Los Olivos 567, Lima",
    "emailReceptor": "juan.perez@email.com",
    "moneda": "PEN",
    "fechaEmision": "2026-02-16",
    "creadoPor": 1,
    "detalles": [
        {
            "productoId": 1,
            "descripcion": "Pisco Quebranta 750ml",
            "codigoUnidad": "NIU",
            "cantidad": 2,
            "precioUnitario": 45.00,
            "montoDescuento": 0
        },
        {
            "productoId": 2,
            "descripcion": "Cerveza Cusqueña 330ml Six Pack",
            "codigoUnidad": "NIU",
            "cantidad": 1,
            "precioUnitario": 28.50,
            "montoDescuento": 3.50
        }
    ]
}
```

**Response (201):**
```json
{
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "serieId": 1,
    "tipoDocumento": "boleta",
    "serie": "B001",
    "numeroCorrelativo": 1,
    "numeroCompleto": "B001-00000001",
    "rucEmisor": "20123456789",
    "razonSocialEmisor": "Licorería DrinkGo SAC",
    "direccionEmisor": "Av. La Marina 1234, Lima",
    "nombreReceptor": "Juan Pérez García",
    "subtotal": 118.50,
    "totalDescuento": 3.50,
    "totalGravado": 115.00,
    "totalIgv": 20.70,
    "total": 135.70,
    "moneda": "PEN",
    "estado": "emitido",
    "estadoSunat": "pendiente",
    "fechaEmision": "2026-02-16",
    "detalles": [
        {
            "id": 1,
            "numeroItem": 1,
            "descripcion": "Pisco Quebranta 750ml",
            "cantidad": 2,
            "precioUnitario": 45.00,
            "montoGravado": 90.00,
            "montoIgv": 16.20,
            "total": 106.20
        },
        {
            "id": 2,
            "numeroItem": 2,
            "descripcion": "Cerveza Cusqueña 330ml Six Pack",
            "cantidad": 1,
            "precioUnitario": 28.50,
            "montoDescuento": 3.50,
            "montoGravado": 25.00,
            "montoIgv": 4.50,
            "total": 29.50
        }
    ]
}
```

### 9.4 Emitir Factura

**Request:**
```http
POST /restful/facturacion/documentos
Content-Type: application/json

{
    "negocioId": 1,
    "sedeId": 1,
    "serieId": 2,
    "tipoDocumento": "factura",
    "rucEmisor": "20123456789",
    "razonSocialEmisor": "Licorería DrinkGo SAC",
    "direccionEmisor": "Av. La Marina 1234, Lima",
    "tipoDocumentoReceptor": "6",
    "numeroDocumentoReceptor": "20987654321",
    "nombreReceptor": "Restaurante El Buen Sabor SAC",
    "direccionReceptor": "Av. Javier Prado 890, San Isidro",
    "emailReceptor": "compras@buensabor.com",
    "moneda": "PEN",
    "fechaEmision": "2026-02-16",
    "fechaVencimiento": "2026-03-16",
    "creadoPor": 1,
    "detalles": [
        {
            "productoId": 5,
            "descripcion": "Whisky Johnnie Walker Black Label 750ml",
            "codigoUnidad": "NIU",
            "cantidad": 6,
            "precioUnitario": 120.00,
            "montoDescuento": 0
        }
    ]
}
```

### 9.5 Enviar a SUNAT (simulado)

**Request:**
```http
POST /restful/facturacion/1/enviar?negocioId=1
```

**Response (200) — Aceptado:**
```json
{
    "documentoId": 1,
    "numeroCompleto": "B001-00000001",
    "estadoSunat": "aceptado",
    "codigoRespuesta": "0",
    "mensajeRespuesta": "La boleta B001-00000001 ha sido aceptada por SUNAT [SIMULACIÓN]",
    "hashSunat": "a1b2c3d4e5f6...sha256hash",
    "urlXml": "https://drinkgo-pse-simulacion.local/documentos/B001-00000001.xml",
    "urlCdr": "https://drinkgo-pse-simulacion.local/documentos/B001-00000001-cdr.xml",
    "mensaje": "Documento enviado y procesado exitosamente por SUNAT [SIMULACIÓN - PSE: PSE Simulación DrinkGo (Desarrollo)]"
}
```

**Response (200) — Rechazado:**
```json
{
    "documentoId": 1,
    "numeroCompleto": "B001-00000001",
    "estadoSunat": "rechazado",
    "codigoRespuesta": "2033",
    "mensajeRespuesta": "Error 2033: RUC del emisor no se encuentra activo [SIMULACIÓN]",
    "hashSunat": null,
    "urlXml": null,
    "urlCdr": null,
    "mensaje": "Documento rechazado por SUNAT [SIMULACIÓN - PSE: PSE Simulación DrinkGo (Desarrollo)]"
}
```

### 9.6 Emitir Nota de Crédito

**Request:**
```http
POST /restful/facturacion/documentos
Content-Type: application/json

{
    "negocioId": 1,
    "sedeId": 1,
    "serieId": 3,
    "tipoDocumento": "nota_credito",
    "rucEmisor": "20123456789",
    "razonSocialEmisor": "Licorería DrinkGo SAC",
    "direccionEmisor": "Av. La Marina 1234, Lima",
    "nombreReceptor": "Juan Pérez García",
    "tipoDocumentoReceptor": "1",
    "numeroDocumentoReceptor": "45678912",
    "moneda": "PEN",
    "fechaEmision": "2026-02-16",
    "documentoReferenciadoId": 1,
    "motivoReferencia": "Devolución de producto defectuoso",
    "creadoPor": 1,
    "detalles": [
        {
            "productoId": 1,
            "descripcion": "Pisco Quebranta 750ml (devolución)",
            "codigoUnidad": "NIU",
            "cantidad": 1,
            "precioUnitario": 45.00,
            "montoDescuento": 0
        }
    ]
}
```

### 9.7 Anular Documento

**Request:**
```http
POST /restful/facturacion/1/anular?negocioId=1
Content-Type: application/json

{
    "anuladoPor": 1,
    "motivoAnulacion": "Error en datos del receptor"
}
```

**Response (200):**
```json
{
    "mensaje": "Documento anulado correctamente",
    "documento": {
        "id": 1,
        "estado": "anulado",
        "estadoSunat": "anulado",
        "anuladoEn": "2026-02-16T15:30:00",
        "motivoAnulacion": "Error en datos del receptor"
    }
}
```

### 9.8 Consultar Estado

**Request:**
```http
GET /restful/facturacion/1/estado?negocioId=1
```

**Response (200):**
```json
{
    "documentoId": 1,
    "numeroCompleto": "B001-00000001",
    "tipoDocumento": "boleta",
    "estado": "aceptado",
    "estadoSunat": "aceptado",
    "codigoRespuestaSunat": "0",
    "mensajeRespuestaSunat": "La boleta B001-00000001 ha sido aceptada por SUNAT [SIMULACIÓN]",
    "total": 135.70,
    "fechaEmision": "2026-02-16"
}
```

### 9.9 Obtener PDF

**Request:**
```http
GET /restful/facturacion/1/pdf?negocioId=1
```

**Response (200):**
```json
{
    "documentoId": 1,
    "numeroCompleto": "B001-00000001",
    "urlPdf": "https://drinkgo-facturacion.local/pdf/1/B001-00000001.pdf",
    "mensaje": "PDF generado exitosamente [SIMULACIÓN]"
}
```

### 9.10 Buscar Documentos con Filtros

**Request:**
```http
GET /restful/facturacion/documentos?negocioId=1&tipoDocumento=boleta&estado=emitido&fechaDesde=2026-02-01&fechaHasta=2026-02-28
```

### 9.11 Desactivar Serie

**Request:**
```http
DELETE /restful/facturacion/series/1?negocioId=1
```

**Response (200):**
```json
{
    "mensaje": "Serie desactivada correctamente",
    "serie": {
        "id": 1,
        "estaActivo": false
    }
}
```

---

## 10. CÓMO PROBAR EN POSTMAN

### Paso 1: Configuración inicial

1. Asegurarse de que MySQL/XAMPP está ejecutando con la BD `drinkgo_db`
2. Verificar que existan datos en las tablas `negocios` y `sedes`
3. Ejecutar el backend: `./mvnw spring-boot:run` (puerto 8080)

### Paso 2: Crear series (ejecutar una vez)

```
POST http://localhost:8080/restful/facturacion/series
Body (JSON):
{
    "negocioId": 1,
    "sedeId": 1,
    "tipoDocumento": "boleta",
    "prefijoSerie": "B001"
}
```

Repetir para factura (F001), nota_credito (BC01), nota_debito (BD01).

### Paso 3: Emitir documento

```
POST http://localhost:8080/restful/facturacion/documentos
Body: (ver ejemplo 9.3)
```

### Paso 4: Enviar a SUNAT (simulado)

```
POST http://localhost:8080/restful/facturacion/{id}/enviar?negocioId=1
```

### Paso 5: Verificar estado

```
GET http://localhost:8080/restful/facturacion/{id}/estado?negocioId=1
```

### Paso 6: Obtener PDF

```
GET http://localhost:8080/restful/facturacion/{id}/pdf?negocioId=1
```

### Paso 7: Probar nota de crédito

```
POST http://localhost:8080/restful/facturacion/documentos
Body: (ver ejemplo 9.6, usando documentoReferenciadoId del documento creado)
```

### Paso 8: Anular documento

```
POST http://localhost:8080/restful/facturacion/{id}/anular?negocioId=1
Body: { "anuladoPor": 1, "motivoAnulacion": "Prueba de anulación" }
```

### Paso 9: Buscar con filtros

```
GET http://localhost:8080/restful/facturacion/documentos?negocioId=1&tipoDocumento=boleta
GET http://localhost:8080/restful/facturacion/documentos?negocioId=1&estadoSunat=aceptado
GET http://localhost:8080/restful/facturacion/documentos?negocioId=1&fechaDesde=2026-02-01&fechaHasta=2026-12-31
```

### Variables de entorno sugeridas para Postman

| Variable | Valor |
|----------|-------|
| `base_url` | `http://localhost:8080` |
| `negocioId` | `1` |
| `sedeId` | `1` |

---

## 11. DIAGRAMA TEXTUAL DEL PROCESO

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUJO DE FACTURACIÓN DRINKGO                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐     ┌──────────────────┐     ┌─────────────┐  │
│  │  1. NEGOCIO  │────▶│  2. CREAR SERIE  │────▶│ 3. EMITIR   │  │
│  │  configura   │     │  por sede/tipo   │     │ DOCUMENTO   │  │
│  │  sus datos   │     │  B001, F001...   │     │ automático  │  │
│  └─────────────┘     └──────────────────┘     └──────┬──────┘  │
│                                                       │         │
│                                              ┌────────▼───────┐ │
│                                              │ estado=emitido │ │
│                                              │ sunat=pendiente│ │
│                                              └────────┬───────┘ │
│                                                       │         │
│           ┌───────────────────────────────────────────┤         │
│           │                                           │         │
│   ┌───────▼────────┐                        ┌────────▼───────┐ │
│   │  MODO INTERNO  │                        │ MODO ELECTRÓN. │ │
│   │  (sin PSE)     │                        │ (con PSE)      │ │
│   │                │                        │                │ │
│   │  • Descargar   │                        │ 4. ENVIAR      │ │
│   │    PDF         │                        │    A SUNAT     │ │
│   │  • Imprimir    │                        │    (simulado)  │ │
│   │  • Consultar   │                        └────────┬───────┘ │
│   └────────────────┘                                 │         │
│                                                       │         │
│                              ┌────────────────────────┤         │
│                              │            │           │         │
│                     ┌────────▼───┐  ┌─────▼──────┐ ┌─▼───────┐ │
│                     │ ACEPTADO   │  │ OBSERVADO  │ │RECHAZADO│ │
│                     │ (80%)      │  │ (15%)      │ │ (5%)    │ │
│                     │ sunat=     │  │ sunat=     │ │ sunat=  │ │
│                     │ aceptado   │  │ aceptado   │ │rechazado│ │
│                     └────────────┘  └────────────┘ └─────────┘ │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │ OPERACIONES ADICIONALES:                                 │  │
│  │ • Anular documento (POST /{id}/anular)                   │  │
│  │ • Nota de crédito (referencia documento original)        │  │
│  │ • Nota de débito (referencia documento original)         │  │
│  │ • Consultar estado (GET /{id}/estado)                    │  │
│  │ • Generar PDF (GET /{id}/pdf)                            │  │
│  │ • Buscar con filtros (GET /documentos?...)               │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Diagrama de integración PSE

```
┌────────────────────┐
│ DocumentoService   │
│ (lógica negocio)   │
└────────┬───────────┘
         │ inyecta
         ▼
┌────────────────────┐
│ PseClient          │  ◄── Interface (contrato)
│ (interface)        │
└────────┬───────────┘
         │ implementa
         ▼
┌────────────────────┐     ┌────────────────────┐
│ PseSimulationClient│     │ PseNubefactClient  │
│ (@Component)       │     │ (FUTURO)           │
│ ACTUAL - Simulación│     │ @Primary           │
└────────────────────┘     └────────────────────┘
                           ┌────────────────────┐
                           │ PseEfactClient     │
                           │ (FUTURO)           │
                           └────────────────────┘
```

---

## 12. RESPONSABILIDAD LEGAL

### Importante: El negocio es el emisor ante SUNAT

```
┌──────────────────────────────────────────────────────────────┐
│                    RESPONSABILIDAD LEGAL                     │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  DrinkGo es una PLATAFORMA DE GESTIÓN, NO un emisor de      │
│  comprobantes electrónicos.                                  │
│                                                              │
│  • El NEGOCIO (licorería) es el emisor ante SUNAT.          │
│  • DrinkGo facilita la generación y gestión de documentos.  │
│  • El envío oficial a SUNAT se realiza a través de un PSE   │
│    contratado directamente por el negocio.                   │
│  • DrinkGo NO es responsable de la validez fiscal ni del    │
│    cumplimiento tributario del negocio.                      │
│                                                              │
│  Cada negocio debe:                                          │
│  1. Tener RUC activo y habido ante SUNAT                    │
│  2. Contar con certificado digital vigente                   │
│  3. Contratar un PSE autorizado por SUNAT                   │
│  4. Configurar sus credenciales SOL en el sistema           │
│  5. Cumplir con la normativa tributaria vigente              │
│                                                              │
│  El modo simulación NO genera documentos válidos ante SUNAT. │
│  Es exclusivamente para desarrollo y pruebas internas.       │
└──────────────────────────────────────────────────────────────┘
```

---

## 13. CONFIRMACIÓN DE NO MODIFICACIÓN DE BD

### Verificación de integridad

| Aspecto | Estado |
|---------|--------|
| ¿Se modificó el archivo `drinkgo_database.sql`? | **NO** |
| ¿Se crearon tablas nuevas? | **NO** |
| ¿Se alteraron tablas existentes? | **NO** |
| ¿Se agregaron columnas? | **NO** |
| ¿Se modificaron constraints? | **NO** |
| ¿Se cambiaron tipos de datos? | **NO** |

### Tablas utilizadas (existentes)

Las 3 entidades creadas mapean directamente a las tablas definidas en el Bloque 10 del script SQL original:

1. `SerieFacturacion.java` → `series_facturacion` (existente)
2. `DocumentoFacturacion.java` → `documentos_facturacion` (existente)
3. `DetalleDocumentoFacturacion.java` → `detalle_documentos_facturacion` (existente)

### Nota sobre JPA

El proyecto usa `spring.jpa.hibernate.ddl-auto=update`, por lo que Hibernate puede crear o actualizar tablas automáticamente si mapea correctamente. Los mapeos se diseñaron para coincidir **exactamente** con la estructura SQL existente, por lo que no se generarán modificaciones.

---

## RESUMEN EJECUTIVO

| Componente | Cantidad | Archivos |
|------------|----------|----------|
| Entities | 3 | SerieFacturacion, DocumentoFacturacion, DetalleDocumentoFacturacion |
| DTOs | 9 | Create/Update/Response para series y documentos |
| Repositories | 3 | Con queries personalizadas y filtros |
| Services | 2 | SerieFacturacionService, DocumentoFacturacionService |
| Controllers | 2 | SerieFacturacionController, DocumentoFacturacionController |
| Integration | 3 | PseClient (interface), PseResponse, PseSimulationClient |
| **Total** | **17** | **Archivos nuevos creados** |

### Funcionalidades implementadas

- ✅ Gestión completa de series (CRUD + DELETE lógico)
- ✅ Emisión de documentos (boleta, factura, nota crédito, nota débito)
- ✅ Cálculo automático de impuestos (IGV 18%)
- ✅ Generación automática de correlativo
- ✅ Envío simulado a SUNAT con probabilidades realistas
- ✅ Anulación de documentos sin eliminación
- ✅ Notas de crédito/débito con referencia a documento original
- ✅ Consulta avanzada con múltiples filtros
- ✅ Generación simulada de PDF
- ✅ Multi-tenant estricto
- ✅ Arquitectura preparada para PSE real
- ✅ Sin modificación de base de datos
