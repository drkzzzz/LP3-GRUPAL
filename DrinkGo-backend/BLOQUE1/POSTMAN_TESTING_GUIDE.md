# 📮 GUÍA POSTMAN - BLOQUE I
## DrinkGo Platform - Testing Completo

---

## 🚀 CONFIGURACIÓN INICIAL

### **URL Base del Servidor:**
```
http://localhost:8080
```

### **Requisitos:**
- ✅ Servidor Spring Boot corriendo en puerto 8080
- ✅ XAMPP MySQL iniciado
- ✅ Postman instalado

---

## 🔑 PASO 0: OBTENER TOKEN JWT (OPCIONAL - Actualmente no requerido)

### **1. Registrar Usuario (si no tienes uno)**

**Método:** `POST`  
**URL:** `http://localhost:8080/restful/registros`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON - seleccionar "raw" y "JSON"):**
```json
{
  "nombres": "Usuario",
  "apellidos": "Test",
  "email": "test@drinkgo.com",
  "llaveSecreta": "test123",
  "negocioId": 1
}
```

**Respuesta Esperada (Status 201 Created):**
```json
{
  "mensaje": "Registro exitoso",
  "clienteId": "0406a4ce-3557-425d-a524-11e2fdde0431",
  "email": "test@drinkgo.com",
  "negocioId": 1
}
```

**📝 IMPORTANTE:** Guarda el `clienteId` que te devuelve.

---

### **2. Obtener Token JWT**

**Método:** `POST`  
**URL:** `http://localhost:8080/restful/token`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "clienteId": "0406a4ce-3557-425d-a524-11e2fdde0431",
  "llaveSecreta": "test123"
}
```

**Respuesta Esperada (Status 200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "mensaje": "Token generado correctamente",
  "clienteId": "0406a4ce-3557-425d-a524-11e2fdde0431"
}
```

**📝 IMPORTANTE:** Guarda el `accessToken` (aunque actualmente no es necesario por el modo desarrollo).

---

## 📋 MÓDULO 1: PLANES DE SUSCRIPCIÓN

### **✅ 1. GET - Listar Todos los Planes**

**Método:** `GET`  
**URL:** `http://localhost:8080/restful/planes`  
**Headers:** *(ninguno necesario)*

**Respuesta Esperada (Status 200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Plan Básico",
    "slug": "basico",
    "descripcion": "Ideal para pequeños negocios que están comenzando",
    "precio": 49.90,
    "moneda": "PEN",
    "periodoFacturacion": "mensual",
    "maxSedes": 1,
    "maxUsuarios": 3,
    "maxProductos": 100,
    "maxAlmacenesPorSede": 1,
    "permitePos": true,
    "permiteTiendaOnline": false,
    "estaActivo": true,
    "creadoEn": "2026-02-14T09:27:55",
    "actualizadoEn": "2026-02-14T09:27:55"
  },
  {
    "id": 2,
    "nombre": "Plan Pro",
    "slug": "pro",
    "precio": 99.90,
    "moneda": "PEN",
    "estaActivo": true
  }
]
```

**✅ Verificación:** Debe retornar un array de planes activos (status 200).

---

### **✅ 2. GET(id) - Obtener Plan Específico**

**Método:** `GET`  
**URL:** `http://localhost:8080/restful/planes/1`  
**Headers:** *(ninguno necesario)*

**Respuesta Esperada (Status 200 OK):**
```json
{
  "id": 1,
  "nombre": "Plan Básico",
  "slug": "basico",
  "descripcion": "Ideal para pequeños negocios que están comenzando",
  "precio": 49.90,
  "moneda": "PEN",
  "periodoFacturacion": "mensual",
  "maxSedes": 1,
  "maxUsuarios": 3,
  "maxProductos": 100,
  "estaActivo": true
}
```

**✅ Verificación:** Debe retornar el plan con ID 1 (status 200).

**❌ Si el ID no existe (Status 404):**
```json
"Plan no encontrado con ID: 999"
```

---

### **✅ 3. POST - Crear Nuevo Plan**

**Método:** `POST`  
**URL:** `http://localhost:8080/restful/planes`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON - mínimo requerido):**
```json
{
  "nombre": "Plan Premium",
  "slug": "premium",
  "precio": 149.90
}
```

**Body (JSON - completo con todas las opciones):**
```json
{
  "nombre": "Plan Enterprise",
  "slug": "enterprise-new",
  "descripcion": "Plan empresarial con todas las funcionalidades",
  "precio": 299.90,
  "moneda": "PEN",
  "periodoFacturacion": "mensual",
  "maxSedes": 10,
  "maxUsuarios": 50,
  "maxProductos": 10000,
  "maxAlmacenesPorSede": 5,
  "permitePos": true,
  "permiteTiendaOnline": true,
  "permiteDelivery": true,
  "permiteMesas": true,
  "permiteFacturacionElectronica": true,
  "permiteMultiAlmacen": true,
  "permiteReportesAvanzados": true,
  "permiteAccesoApi": true,
  "orden": 4
}
```

**Respuesta Esperada (Status 201 Created):**
```json
{
  "id": 5,
  "nombre": "Plan Premium",
  "slug": "premium",
  "precio": 149.90,
  "moneda": "PEN",
  "maxSedes": 1,
  "maxUsuarios": 5,
  "maxProductos": 500,
  "estaActivo": true,
  "version": 1,
  "creadoEn": "2026-02-14T10:30:00"
}
```

**✅ Verificación:** Debe retornar el plan creado con su nuevo ID (status 201).

**❌ Si falta un campo obligatorio (Status 400):**
```json
"El nombre del plan es obligatorio"
```

**❌ Si el slug ya existe (Status 400):**
```json
"Ya existe un plan con el slug: premium"
```

---

### **✅ 4. PUT - Actualizar Plan Existente**

**Método:** `PUT`  
**URL:** `http://localhost:8080/restful/planes/5`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON - actualizar solo algunos campos):**
```json
{
  "nombre": "Plan Premium Plus",
  "precio": 179.90,
  "descripcion": "Plan premium con beneficios adicionales"
}
```

**Body (JSON - actualizar múltiples campos):**
```json
{
  "nombre": "Plan Premium Plus",
  "precio": 199.90,
  "maxUsuarios": 15,
  "maxProductos": 2000,
  "permiteTiendaOnline": true,
  "permiteReportesAvanzados": true
}
```

**Respuesta Esperada (Status 200 OK):**
```json
{
  "id": 5,
  "nombre": "Plan Premium Plus",
  "slug": "premium",
  "precio": 179.90,
  "descripcion": "Plan premium con beneficios adicionales",
  "moneda": "PEN",
  "maxSedes": 1,
  "maxUsuarios": 5,
  "estaActivo": true,
  "version": 2,
  "actualizadoEn": "2026-02-14T10:35:00"
}
```

**✅ Verificación:** Debe retornar el plan actualizado con los nuevos valores (status 200).

**❌ Si el ID no existe (Status 404):**
```json
"Plan no encontrado con ID: 999"
```

---

### **✅ 5. DELETE - Desactivar Plan**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/restful/planes/5`  
**Headers:** *(ninguno necesario)*

**Respuesta Esperada (Status 200 OK):**
```
Plan desactivado correctamente
```

**✅ Verificación:** Debe retornar mensaje de éxito (status 200).

**Verificar desactivación (hacer GET después):**
```
GET http://localhost:8080/restful/planes
```
El plan con ID 5 NO debe aparecer en la lista (porque `estaActivo = false`).

**❌ Si el ID no existe (Status 404):**
```json
"Plan no encontrado con ID: 999"
```

---

## 🔧 MÓDULO 2: CONFIGURACIÓN GLOBAL

### **✅ 1. GET - Listar Todas las Configuraciones**

**Método:** `GET`  
**URL:** `http://localhost:8080/restful/configuracion`  
**Headers:** *(ninguno necesario)*

**Respuesta Esperada (Status 200 OK):**
```json
[
  {
    "id": 1,
    "claveConfiguracion": "IGV_PERU",
    "valor": "0.18",
    "tipoDato": "decimal",
    "descripcion": "Impuesto General a las Ventas en Perú",
    "esPublica": true,
    "creadoEn": "2026-02-14T08:00:00",
    "actualizadoEn": "2026-02-14T08:00:00"
  },
  {
    "id": 2,
    "claveConfiguracion": "DIAS_PRUEBA_GRATIS",
    "valor": "15",
    "tipoDato": "integer",
    "descripcion": "Días de prueba gratuita para nuevos clientes",
    "esPublica": true
  },
  {
    "id": 3,
    "claveConfiguracion": "EMAIL_SOPORTE",
    "valor": "soporte@drinkgo.pe",
    "tipoDato": "string",
    "descripcion": "Email de contacto para soporte",
    "esPublica": true
  }
]
```

**✅ Verificación:** Debe retornar array de configuraciones (status 200).

---

### **✅ 2. GET(id) - Obtener Configuración Específica**

**Método:** `GET`  
**URL:** `http://localhost:8080/restful/configuracion/1`  
**Headers:** *(ninguno necesario)*

**Respuesta Esperada (Status 200 OK):**
```json
{
  "id": 1,
  "claveConfiguracion": "IGV_PERU",
  "valor": "0.18",
  "tipoDato": "decimal",
  "descripcion": "Impuesto General a las Ventas en Perú",
  "esPublica": true,
  "creadoEn": "2026-02-14T08:00:00",
  "actualizadoEn": "2026-02-14T08:00:00"
}
```

**✅ Verificación:** Debe retornar la configuración con ID 1 (status 200).

**❌ Si el ID no existe (Status 404):**
```json
"Configuración no encontrada con ID: 999"
```

---

### **✅ 3. POST - Crear Nueva Configuración**

**Método:** `POST`  
**URL:** `http://localhost:8080/restful/configuracion`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "claveConfiguracion": "MAX_ARCHIVOS_UPLOAD",
  "valor": "10",
  "tipoDato": "numero",
  "descripcion": "Máximo de archivos por upload",
  "esPublica": false
}
```

**Otro ejemplo:**
```json
{
  "claveConfiguracion": "COMISION_PLATAFORMA",
  "valor": "0.05",
  "tipoDato": "decimal",
  "descripcion": "Comisión de la plataforma (5%)",
  "esPublica": true
}
```

**Respuesta Esperada (Status 201 Created):**
```json
{
  "id": 6,
  "claveConfiguracion": "MAX_ARCHIVOS_UPLOAD",
  "valor": "10",
  "tipoDato": "numero",
  "descripcion": "Máximo de archivos por upload",
  "esPublica": false,
  "creadoEn": "2026-02-14T10:40:00",
  "actualizadoEn": "2026-02-14T10:40:00"
}
```

**✅ Verificación:** Debe retornar la configuración creada con su nuevo ID (status 201).

**❌ Si falta un campo obligatorio (Status 400):**
```json
"La clave de configuración es obligatoria"
```

**❌ Si la clave ya existe (Status 400):**
```json
"Ya existe una configuración con la clave: MAX_ARCHIVOS_UPLOAD"
```

---

### **✅ 4. PUT - Actualizar Configuración**

**Método:** `PUT`  
**URL:** `http://localhost:8080/restful/configuracion/6`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON - actualizar valor y descripción):**
```json
{
  "valor": "20",
  "descripcion": "Límite aumentado a 20 archivos por upload"
}
```

**Otro ejemplo (actualizar solo valor):**
```json
{
  "valor": "0.08"
}
```

**Respuesta Esperada (Status 200 OK):**
```json
{
  "id": 6,
  "claveConfiguracion": "MAX_ARCHIVOS_UPLOAD",
  "valor": "20",
  "tipoDato": "numero",
  "descripcion": "Límite aumentado a 20 archivos por upload",
  "esPublica": false,
  "actualizadoEn": "2026-02-14T10:45:00"
}
```

**✅ Verificación:** Debe retornar la configuración actualizada (status 200).

**❌ Si el ID no existe (Status 404):**
```json
"Configuración no encontrada con ID: 999"
```

---

### **✅ 5. DELETE - Eliminar Configuración**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/restful/configuracion/6`  
**Headers:** *(ninguno necesario)*

**Respuesta Esperada (Status 200 OK):**
```
Configuración eliminada correctamente
```

**✅ Verificación:** Debe retornar mensaje de éxito (status 200).

**Verificar eliminación (hacer GET después):**
```
GET http://localhost:8080/restful/configuracion
```
La configuración con ID 6 NO debe aparecer en la lista.

**❌ Si el ID no existe (Status 404):**
```json
"Configuración no encontrada con ID: 999"
```

---

## 📊 RESUMEN DE TESTING

### **Planes de Suscripción (`/restful/planes`)**

| Método | URL | Body | Status Esperado |
|--------|-----|------|-----------------|
| GET | `/restful/planes` | - | 200 OK |
| GET | `/restful/planes/1` | - | 200 OK |
| POST | `/restful/planes` | `{"nombre":"Test","slug":"test","precio":99.90}` | 201 Created |
| PUT | `/restful/planes/5` | `{"precio":129.90}` | 200 OK |
| DELETE | `/restful/planes/5` | - | 200 OK |

### **Configuración Global (`/restful/configuracion`)**

| Método | URL | Body | Status Esperado |
|--------|-----|------|-----------------|
| GET | `/restful/configuracion` | - | 200 OK |
| GET | `/restful/configuracion/1` | - | 200 OK |
| POST | `/restful/configuracion` | `{"claveConfiguracion":"TEST","valor":"123","tipoDato":"numero","descripcion":"Test","esPublica":true}` | 201 Created |
| PUT | `/restful/configuracion/6` | `{"valor":"456"}` | 200 OK |
| DELETE | `/restful/configuracion/6` | - | 200 OK |

---

## 🎯 FLUJO DE TESTING SUGERIDO PARA DEMOSTRACIÓN

### **1. Demostrar GET (Listar)**
```
GET http://localhost:8080/restful/planes
GET http://localhost:8080/restful/configuracion
```

### **2. Demostrar GET(id) (Obtener uno)**
```
GET http://localhost:8080/restful/planes/1
GET http://localhost:8080/restful/configuracion/1
```

### **3. Demostrar POST (Crear)**
```
POST http://localhost:8080/restful/planes
Body: {"nombre":"Plan Demo","slug":"demo","precio":99.90}

POST http://localhost:8080/restful/configuracion
Body: {"claveConfiguracion":"TEST_CONFIG","valor":"123","tipoDato":"numero","descripcion":"Testing","esPublica":true}
```

### **4. Demostrar PUT (Actualizar)**
```
PUT http://localhost:8080/restful/planes/{ID_CREADO}
Body: {"precio":129.90}

PUT http://localhost:8080/restful/configuracion/{ID_CREADO}
Body: {"valor":"456"}
```

### **5. Demostrar DELETE (Eliminar)**
```
DELETE http://localhost:8080/restful/planes/{ID_CREADO}
DELETE http://localhost:8080/restful/configuracion/{ID_CREADO}
```

### **6. Verificar Eliminación**
```
GET http://localhost:8080/restful/planes
GET http://localhost:8080/restful/configuracion
```
*(Los registros eliminados NO deben aparecer)*

---

## 💡 TIPS PARA POSTMAN

### **1. Crear una Colección:**
- Click en "New" → "Collection"
- Nombre: "DrinkGo - Bloque I"
- Agregar carpetas para cada módulo

### **2. Organizar Requests:**
```
📁 DrinkGo - Bloque I
  📁 0. Autenticación
    └─ POST Registrar Usuario
    └─ POST Obtener Token
  📁 1. Planes de Suscripción
    └─ GET Listar Planes
    └─ GET Plan por ID
    └─ POST Crear Plan
    └─ PUT Actualizar Plan
    └─ DELETE Desactivar Plan
  📁 2. Configuración Global
    └─ GET Listar Configs
    └─ GET Config por ID
    └─ POST Crear Config
    └─ PUT Actualizar Config
    └─ DELETE Eliminar Config
```

### **3. Variables de Entorno (opcional):**
- Crear environment "DrinkGo Dev"
- Variables:
  - `base_url` = `http://localhost:8080`
  - `token` = `{token obtenido}`
- Usar en requests: `{{base_url}}/restful/planes`

### **4. Ver Status Codes:**
- 200 OK = Operación exitosa
- 201 Created = Recurso creado
- 400 Bad Request = Error en el request
- 404 Not Found = Recurso no encontrado
- 500 Internal Server Error = Error del servidor

---

## ✅ CHECKLIST FINAL

- [ ] Servidor corriendo en puerto 8080
- [ ] XAMPP MySQL iniciado
- [ ] Postman abierto
- [ ] Probado GET en ambos módulos
- [ ] Probado GET(id) en ambos módulos
- [ ] Probado POST en ambos módulos
- [ ] Probado PUT en ambos módulos
- [ ] Probado DELETE en ambos módulos
- [ ] Todos los status codes correctos (200, 201, 404, 400)

---

**✅ ¡TESTING COMPLETO EXITOSO!**

*Guía actualizada: 14 de febrero de 2026*
