# GUÍA DE PRUEBAS POSTMAN - BLOQUE 14: TIENDA ONLINE (STOREFRONT)

## INFORMACIÓN GENERAL
- **Base URL**: `http://localhost:8080`
- **Autenticación**: MODO DESARROLLO - No requiere JWT (todos los endpoints públicos)
- **Headers comunes**: 
  - `Content-Type: application/json`

---

## 📋 CHECKLIST DE PRUEBAS (Evaluación Profesor)

### ✅ Antes de demostrar al profesor:
1. ✅ Servidor Spring Boot corriendo en puerto 8080
2. ✅ Base de datos MySQL iniciada (XAMPP)
3. ✅ Tablas `configuracion_tienda_online` y `paginas_tienda_online` creadas
4. ✅ Datos de prueba insertados (ejecutar archivo `datos_prueba_bloque_14.sql`)
5. ✅ Verificar que existe negocio_id = 1 y 2 en la tabla `negocios`

### ✅ Endpoints a demostrar (Configuración de Tienda):
- [ ] GET - Obtener configuración por negocio
- [ ] GET - Obtener configuración por slug (público)
- [ ] POST - Crear configuración de tienda
- [ ] PUT - Actualizar configuración
- [ ] GET - Verificar existencia de configuración

### ✅ Endpoints a demostrar (Páginas de Tienda):
- [ ] GET - Listar todas las páginas (admin)
- [ ] GET - Listar solo páginas publicadas (público)
- [ ] GET - Obtener página por ID
- [ ] GET - Obtener página por slug (público)
- [ ] POST - Crear nueva página
- [ ] PUT - Actualizar página
- [ ] DELETE - Eliminar página

---

## 🏪 PARTE 1: CONFIGURACIÓN DE TIENDA ONLINE

### 1️⃣ GET - OBTENER CONFIGURACIÓN POR NEGOCIO

**Descripción**: Obtiene la configuración de la tienda online del negocio especificado.

#### Request
```
GET http://localhost:8080/api/tienda-online/configuracion?negocioId=1
```

#### Headers
```
Content-Type: application/json
```

#### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "estaHabilitado": true,
  "nombreTienda": "DrinkGo Premium Store",
  "slugTienda": "drinkgo-premium",
  "dominioPersonalizado": "https://premium.drinkgo.pe",
  "mensajeBienvenida": "¡Bienvenido a DrinkGo Premium! Encuentra los mejores licores y bebidas con entrega rápida.",
  "imagenesBanner": "[\"https://cdn.drinkgo.pe/banners/banner-1.jpg\", \"https://cdn.drinkgo.pe/banners/banner-2.jpg\"]",
  "categoriasDestacadas": "[1, 2, 3, 5]",
  "tituloSeo": "DrinkGo Premium - Licorería Online en Lima",
  "descripcionSeo": "Compra licores, vinos, cervezas y más en línea. Entrega rápida en Lima y provincias.",
  "palabrasClaveSeo": "licorería online, vinos, cervezas, licores, delivery alcohol",
  "idGoogleAnalytics": "UA-123456789-1",
  "idPixelFacebook": "pixel-fb-987654321",
  "enlacesSociales": "{\"facebook\": \"https://facebook.com/drinkgopremium\", \"instagram\": \"https://instagram.com/drinkgopremium\", \"whatsapp\": \"+51987654321\"}",
  "montoMinimoPedido": 30.00,
  "montoMaximoPedido": 5000.00,
  "terminosCondiciones": "El cliente acepta los términos y condiciones de compra...",
  "politicaPrivacidad": "Respetamos tu privacidad...",
  "politicaDevoluciones": "Aceptamos devoluciones dentro de 7 días...",
  "mostrarPreciosConImpuesto": true,
  "permitirCompraInvitado": false,
  "requiereVerificacionEdad": true,
  "creadoEn": "2026-02-16T20:00:00-05:00",
  "actualizadoEn": "2026-02-16T20:00:00-05:00"
}
```

#### ✅ Validaciones:
- Status code: 200
- La configuración pertenece al negocioId solicitado
- Todos los campos están presentes

---

### 2️⃣ GET - OBTENER CONFIGURACIÓN POR SLUG (PÚBLICO)

**Descripción**: Endpoint público para obtener configuración de tienda por su slug único.

#### Request
```
GET http://localhost:8080/api/tienda-online/configuracion/slug/drinkgo-premium
```

#### Headers
```
Content-Type: application/json
```

#### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "estaHabilitado": true,
  "nombreTienda": "DrinkGo Premium Store",
  "slugTienda": "drinkgo-premium",
  "mensajeBienvenida": "¡Bienvenido a DrinkGo Premium!...",
  "imagenesBanner": "...",
  "montoMinimoPedido": 30.00,
  "montoMaximoPedido": 5000.00,
  "requiereVerificacionEdad": true
}
```

#### Caso: Slug no existe (404 Not Found)
```
GET http://localhost:8080/api/tienda-online/configuracion/slug/no-existe
```
Response: Status 404

---

### 3️⃣ POST - CREAR CONFIGURACIÓN DE TIENDA

**Descripción**: Crea la configuración inicial de tienda online para un negocio.

#### Request
```
POST http://localhost:8080/api/tienda-online/configuracion
```

#### Headers
```
Content-Type: application/json
```

#### Body
```json
{
  "negocioId": 3,
  "estaHabilitado": false,
  "nombreTienda": "Licorería Los Andes",
  "slugTienda": "licoreria-los-andes",
  "mensajeBienvenida": "¡Bienvenido a Licorería Los Andes! Calidad y precio justo.",
  "tituloSeo": "Licorería Los Andes - Vinos y Licores",
  "descripcionSeo": "Tu licorería de confianza en el centro de Lima.",
  "montoMinimoPedido": 25.00,
  "montoMaximoPedido": 3000.00,
  "mostrarPreciosConImpuesto": true,
  "permitirCompraInvitado": true,
  "requiereVerificacionEdad": true
}
```

#### Response Esperada (201 Created)
```json
{
  "id": 3,
  "negocioId": 3,
  "estaHabilitado": false,
  "nombreTienda": "Licorería Los Andes",
  "slugTienda": "licoreria-los-andes",
  "mensajeBienvenida": "¡Bienvenido a Licorería Los Andes! Calidad y precio justo.",
  "montoMinimoPedido": 25.00,
  "montoMaximoPedido": 3000.00,
  "creadoEn": "2026-02-16T20:30:00-05:00",
  "actualizadoEn": "2026-02-16T20:30:00-05:00"
}
```

#### ✅ Validaciones:
- Status code: 201
- La configuración se creó correctamente
- El slug es único en todo el sistema

#### ❌ Caso de error: Negocio ya tiene configuración
```json
{
  "negocioId": 1,
  "nombreTienda": "Nueva Tienda"
}
```
Response (400 Bad Request):
```json
{
  "error": "Ya existe una configuración de tienda online para este negocio"
}
```

#### ❌ Caso de error: Slug duplicado
```json
{
  "negocioId": 4,
  "slugTienda": "drinkgo-premium"
}
```
Response (400 Bad Request):
```json
{
  "error": "El slug de tienda ya está en uso: drinkgo-premium"
}
```

---

### 4️⃣ PUT - ACTUALIZAR CONFIGURACIÓN DE TIENDA

**Descripción**: Actualiza la configuración existente de una tienda online.

#### Request
```
PUT http://localhost:8080/api/tienda-online/configuracion/1?negocioId=1
```

#### Headers
```
Content-Type: application/json
```

#### Body
```json
{
  "nombreTienda": "DrinkGo Premium Store - Renovado",
  "mensajeBienvenida": "¡Bienvenido a nuestra tienda renovada! Nuevas ofertas cada semana.",
  "imagenesBanner": "[\"https://cdn.drinkgo.pe/banners/nuevo-banner.jpg\"]",
  "tituloSeo": "DrinkGo Premium - La Mejor Licorería Online",
  "descripcionSeo": "Licores premium, vinos selectos y cervezas artesanales. Delivery en Lima.",
  "idGoogleAnalytics": "UA-123456789-2",
  "montoMinimoPedido": 35.00,
  "permitirCompraInvitado": true
}
```

#### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "nombreTienda": "DrinkGo Premium Store - Renovado",
  "mensajeBienvenida": "¡Bienvenido a nuestra tienda renovada!...",
  "montoMinimoPedido": 35.00,
  "permitirCompraInvitado": true,
  "actualizadoEn": "2026-02-16T20:45:00-05:00"
}
```

#### ✅ Validaciones:
- Los campos se actualizaron correctamente
- El campo `actualizadoEn` se modificó
- Los campos no enviados mantienen sus valores

---

### 5️⃣ GET - VERIFICAR EXISTENCIA DE CONFIGURACIÓN

**Descripción**: Verifica si un negocio tiene configuración de tienda online.

#### Request
```
GET http://localhost:8080/api/tienda-online/configuracion/existe?negocioId=1
```

#### Response Esperada (200 OK)
```json
{
  "existe": true
}
```

#### Request: Negocio sin configuración
```
GET http://localhost:8080/api/tienda-online/configuracion/existe?negocioId=999
```

#### Response Esperada (200 OK)
```json
{
  "existe": false
}
```

---

## 📄 PARTE 2: PÁGINAS DE TIENDA ONLINE

### 6️⃣ GET - LISTAR TODAS LAS PÁGINAS (ADMIN)

**Descripción**: Lista todas las páginas de un negocio (publicadas y no publicadas).

#### Request
```
GET http://localhost:8080/api/tienda-online/paginas?negocioId=1
```

#### Headers
```
Content-Type: application/json
```

#### Response Esperada (200 OK)
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "titulo": "Sobre Nosotros",
    "slug": "sobre-nosotros",
    "contenido": "<h1>Sobre DrinkGo Premium</h1>...",
    "estaPublicado": true,
    "orden": 1,
    "metaTitulo": "Sobre Nosotros - DrinkGo Premium",
    "metaDescripcion": "Conoce más sobre DrinkGo Premium...",
    "creadoEn": "2026-02-16T20:00:00-05:00",
    "actualizadoEn": "2026-02-16T20:00:00-05:00"
  },
  {
    "id": 2,
    "negocioId": 1,
    "titulo": "Cómo Comprar",
    "slug": "como-comprar",
    "contenido": "<h1>¿Cómo realizar tu pedido?</h1>...",
    "estaPublicado": true,
    "orden": 2,
    "metaTitulo": "Cómo Comprar - Guía de Compra",
    "metaDescripcion": "Aprende cómo realizar tu pedido...",
    "creadoEn": "2026-02-16T20:00:00-05:00",
    "actualizadoEn": "2026-02-16T20:00:00-05:00"
  },
  {
    "id": 6,
    "negocioId": 1,
    "titulo": "Promociones Especiales",
    "slug": "promociones-especiales",
    "contenido": "<h1>Promociones del Mes</h1>...",
    "estaPublicado": false,
    "orden": 10,
    "metaTitulo": "Promociones Especiales",
    "metaDescripcion": "Descubre nuestras promociones...",
    "creadoEn": "2026-02-16T20:00:00-05:00",
    "actualizadoEn": "2026-02-16T20:00:00-05:00"
  }
]
```

#### ✅ Validaciones:
- Se listan todas las páginas (publicadas y borradores)
- Están ordenadas por el campo `orden`
- Incluye el contenido completo

---

### 7️⃣ GET - LISTAR SOLO PÁGINAS PUBLICADAS (PÚBLICO)

**Descripción**: Endpoint público que lista solo las páginas publicadas.

#### Request
```
GET http://localhost:8080/api/tienda-online/paginas/publicadas?negocioId=1
```

#### Response Esperada (200 OK)
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "titulo": "Sobre Nosotros",
    "slug": "sobre-nosotros",
    "estaPublicado": true,
    "orden": 1
  },
  {
    "id": 2,
    "negocioId": 1,
    "titulo": "Cómo Comprar",
    "slug": "como-comprar",
    "estaPublicado": true,
    "orden": 2
  },
  {
    "id": 3,
    "negocioId": 1,
    "titulo": "Zonas de Entrega",
    "slug": "zonas-entrega",
    "estaPublicado": true,
    "orden": 3
  }
]
```

#### ✅ Validaciones:
- Solo aparecen páginas con `estaPublicado: true`
- No aparece la página "Promociones Especiales" (borrador)

---

### 8️⃣ GET - OBTENER PÁGINA POR ID

**Descripción**: Obtiene una página específica por su ID.

#### Request
```
GET http://localhost:8080/api/tienda-online/paginas/1?negocioId=1
```

#### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "titulo": "Sobre Nosotros",
  "slug": "sobre-nosotros",
  "contenido": "<h1>Sobre DrinkGo Premium</h1><p>Somos una licorería online...</p>",
  "estaPublicado": true,
  "orden": 1,
  "metaTitulo": "Sobre Nosotros - DrinkGo Premium",
  "metaDescripcion": "Conoce más sobre DrinkGo Premium...",
  "creadoEn": "2026-02-16T20:00:00-05:00",
  "actualizadoEn": "2026-02-16T20:00:00-05:00"
}
```

#### ❌ Caso: Página no existe
```
GET http://localhost:8080/api/tienda-online/paginas/999?negocioId=1
```
Response (404 Not Found):
```json
{
  "error": "Página no encontrada"
}
```

---

### 9️⃣ GET - OBTENER PÁGINA POR SLUG (PÚBLICO)

**Descripción**: Endpoint público para obtener una página por su slug.

#### Request
```
GET http://localhost:8080/api/tienda-online/paginas/slug/sobre-nosotros?negocioId=1
```

#### Response Esperada (200 OK)
```json
{
  "id": 1,
  "negocioId": 1,
  "titulo": "Sobre Nosotros",
  "slug": "sobre-nosotros",
  "contenido": "<h1>Sobre DrinkGo Premium</h1>...",
  "estaPublicado": true,
  "orden": 1,
  "metaTitulo": "Sobre Nosotros - DrinkGo Premium",
  "metaDescripcion": "Conoce más sobre DrinkGo Premium..."
}
```

#### ❌ Caso: Slug no existe
```
GET http://localhost:8080/api/tienda-online/paginas/slug/no-existe?negocioId=1
```
Response (404 Not Found):
```json
{
  "error": "Página no encontrada con slug: no-existe"
}
```

---

### 🔟 POST - CREAR NUEVA PÁGINA

**Descripción**: Crea una nueva página personalizada para la tienda online.

#### Request
```
POST http://localhost:8080/api/tienda-online/paginas
```

#### Headers
```
Content-Type: application/json
```

#### Body
```json
{
  "negocioId": 1,
  "titulo": "Política de Envíos",
  "slug": "politica-envios",
  "contenido": "<h1>Política de Envíos</h1><p>Realizamos envíos a todo Lima en menos de 2 horas...</p><ul><li>Envío gratis en compras mayores a S/ 100</li><li>Empaque discreto y seguro</li></ul>",
  "estaPublicado": true,
  "orden": 6,
  "metaTitulo": "Política de Envíos - DrinkGo Premium",
  "metaDescripcion": "Conoce nuestra política de envíos y tiempos de entrega."
}
```

#### Response Esperada (201 Created)
```json
{
  "id": 7,
  "negocioId": 1,
  "titulo": "Política de Envíos",
  "slug": "politica-envios",
  "contenido": "<h1>Política de Envíos</h1>...",
  "estaPublicado": true,
  "orden": 6,
  "metaTitulo": "Política de Envíos - DrinkGo Premium",
  "metaDescripcion": "Conoce nuestra política de envíos...",
  "creadoEn": "2026-02-16T21:30:00-05:00",
  "actualizadoEn": "2026-02-16T21:30:00-05:00"
}
```

#### ✅ Validaciones:
- Status code: 201
- La página se creó con todos los campos
- El slug es único para ese negocio

#### ❌ Caso de error: Slug duplicado
```json
{
  "negocioId": 1,
  "titulo": "Nueva Página",
  "slug": "sobre-nosotros"
}
```
Response (400 Bad Request):
```json
{
  "error": "Ya existe una página con el slug: sobre-nosotros"
}
```

---

### 1️⃣1️⃣ PUT - ACTUALIZAR PÁGINA

**Descripción**: Actualiza una página existente.

#### Request
```
PUT http://localhost:8080/api/tienda-online/paginas/7?negocioId=1
```

#### Headers
```
Content-Type: application/json
```

#### Body
```json
{
  "titulo": "Política de Envíos y Devoluciones",
  "slug": "politica-envios-devoluciones",
  "contenido": "<h1>Política de Envíos y Devoluciones</h1><p>Contenido actualizado...</p>",
  "orden": 5,
  "metaDescripcion": "Información actualizada sobre envíos y devoluciones."
}
```

#### Response Esperada (200 OK)
```json
{
  "id": 7,
  "negocioId": 1,
  "titulo": "Política de Envíos y Devoluciones",
  "slug": "politica-envios-devoluciones",
  "contenido": "<h1>Política de Envíos y Devoluciones</h1>...",
  "estaPublicado": true,
  "orden": 5,
  "actualizadoEn": "2026-02-16T21:45:00-05:00"
}
```

#### ✅ Validaciones:
- Los campos se actualizaron
- El slug se validó como único

---

### 1️⃣2️⃣ DELETE - ELIMINAR PÁGINA

**Descripción**: Elimina una página de la tienda online.

#### Request
```
DELETE http://localhost:8080/api/tienda-online/paginas/7?negocioId=1
```

#### Response Esperada (204 No Content)
```
Status: 204 No Content
(Sin body)
```

#### ✅ Validaciones:
- Status code: 204
- La página fue eliminada de la base de datos
- Ya no aparece en la lista de páginas

---

## 🧪 PRUEBAS ADICIONALES RECOMENDADAS

### Prueba 1: Multi-tenant
```
# Crear configuración para negocio 2
POST /api/tienda-online/configuracion
Body: { "negocioId": 2, "nombreTienda": "Licorería Express" }

# Crear página para negocio 2
POST /api/tienda-online/paginas
Body: { "negocioId": 2, "titulo": "Nosotros", "slug": "nosotros" }

# Verificar aislamiento: negocio 1 no puede ver páginas de negocio 2
GET /api/tienda-online/paginas?negocioId=1
# No debe incluir la página "Nosotros" del negocio 2
```

### Prueba 2: Validación de slug único
```
# Intentar crear 2 configuraciones con el mismo slug
POST /api/tienda-online/configuracion
Body: { "negocioId": 3, "slugTienda": "drinkgo-premium" }
# Debe retornar error 400
```

### Prueba 3: Validación de campos requeridos
```
# Intentar crear página sin campos obligatorios
POST /api/tienda-online/paginas
Body: { "negocioId": 1, "titulo": "Test" }
# Debe retornar error si falta 'slug'
```

---

## 📊 VERIFICACIÓN EN BASE DE DATOS

### Consultas SQL útiles:

```sql
-- Ver todas las configuraciones
SELECT * FROM configuracion_tienda_online;

-- Ver todas las páginas ordenadas
SELECT negocio_id, titulo, slug, esta_publicado, orden 
FROM paginas_tienda_online 
ORDER BY negocio_id, orden;

-- Contar páginas por negocio
SELECT negocio_id, COUNT(*) as total_paginas 
FROM paginas_tienda_online 
GROUP BY negocio_id;

-- Ver páginas publicadas
SELECT negocio_id, titulo, slug 
FROM paginas_tienda_online 
WHERE esta_publicado = 1;
```

---

## 🎯 CRITERIOS DE EVALUACIÓN

### Funcionalidad Básica (40%)
- ✅ Todos los endpoints responden correctamente
- ✅ Los códigos HTTP son apropiados (200, 201, 204, 400, 404)
- ✅ Los datos se insertan/actualizan en la base de datos

### Validaciones (30%)
- ✅ Slug único por configuración
- ✅ Slug único por negocio en páginas
- ✅ Validación de negocio_id
- ✅ Validación de campos requeridos

### Multi-tenant (20%)
- ✅ Cada negocio solo ve su propia configuración
- ✅ Cada negocio solo ve sus propias páginas
- ✅ No se puede acceder a recursos de otros negocios

### Estructura del Código (10%)
- ✅ Entidades correctamente mapeadas
- ✅ DTOs implementados
- ✅ Servicios con lógica de negocio
- ✅ Controllers REST bien estructurados

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Error: 404 Not Found en todos los endpoints
- Verificar que el servidor Spring Boot esté corriendo
- Verificar que la ruta base sea `/api/tienda-online/`

### Error: 500 Internal Server Error
- Revisar logs de Spring Boot
- Verificar que las tablas existan en la base de datos
- Verificar que los campos JSON sean válidos

### Error: No se crean los registros
- Verificar que existe el negocio_id en la tabla `negocios`
- Revisar constraints de la base de datos
- Verificar valores NULL en campos requeridos

---

## ✅ RESUMEN DE ENDPOINTS

```
# CONFIGURACIÓN
GET    /api/tienda-online/configuracion?negocioId={id}
GET    /api/tienda-online/configuracion/slug/{slug}
POST   /api/tienda-online/configuracion
PUT    /api/tienda-online/configuracion/{id}?negocioId={id}
PATCH  /api/tienda-online/configuracion/estado?negocioId={id}&habilitado={bool}
GET    /api/tienda-online/configuracion/existe?negocioId={id}

# PÁGINAS
GET    /api/tienda-online/paginas?negocioId={id}
GET    /api/tienda-online/paginas/publicadas?negocioId={id}
GET    /api/tienda-online/paginas/{id}?negocioId={id}
GET    /api/tienda-online/paginas/slug/{slug}?negocioId={id}
POST   /api/tienda-online/paginas
PUT    /api/tienda-online/paginas/{id}?negocioId={id}
DELETE /api/tienda-online/paginas/{id}?negocioId={id}
PATCH  /api/tienda-online/paginas/{id}/publicar?negocioId={id}&publicado={bool}
PATCH  /api/tienda-online/paginas/{id}/orden?negocioId={id}&orden={num}
```

---

**¡Listo para evaluar! 🚀**
