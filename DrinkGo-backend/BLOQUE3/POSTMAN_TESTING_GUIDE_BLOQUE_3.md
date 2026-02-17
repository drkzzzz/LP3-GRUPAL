# 📮 GUÍA POSTMAN - BLOQUE 3
## DrinkGo Platform - Sedes, Horarios, Mesas y Configuración

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
- ✅ Base de datos con datos de prueba (ejecutar `datos_prueba_bloque_3.sql`)

### **Autenticación:**
**MODO DESARROLLO** - Actualmente NO se requiere token JWT.
Todos los endpoints son públicos para facilitar pruebas.

---

## 📑 ÍNDICE DE MÓDULOS

1. [Horarios de Sede](#1-horarios-de-sede)
2. [Horarios Especiales](#2-horarios-especiales)
3. [Restricciones de Alcohol](#3-restricciones-de-alcohol)
4. [Áreas de Mesas](#4-áreas-de-mesas)
5. [Mesas](#5-mesas)
6. [Configuración de Negocio](#6-configuración-de-negocio)
7. [Notificaciones](#7-notificaciones)
8. [Métodos de Pago](#8-métodos-de-pago)
9. [Usuario-Sede](#9-usuario-sede)
10. [Plantillas de Notificación](#10-plantillas-de-notificación)

---

## 1️⃣ HORARIOS DE SEDE

### **✅ 1.1 GET - Listar horarios de una sede**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/horarios-sede/sede/1`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "sedeId": 1,
    "diaSemana": 0,
    "horaApertura": "10:00:00",
    "horaCierre": "22:00:00",
    "estaCerrado": false,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 1.2 GET - Obtener horario por ID**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/horarios-sede/1`

---

### **✅ 1.3 POST - Crear nuevo horario**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/horarios-sede`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "sedeId": 1,
  "diaSemana": 3,
  "horaApertura": "09:00:00",
  "horaCierre": "23:00:00",
  "estaCerrado": false
}
```

**Respuesta Esperada (201 Created)**

---

### **✅ 1.4 PUT - Actualizar horario**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/horarios-sede/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "diaSemana": 1,
  "horaApertura": "08:00:00",
  "horaCierre": "00:00:00",
  "estaCerrado": false
}
```

---

### **✅ 1.5 DELETE - Eliminar horario**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/horarios-sede/1`

**Respuesta Esperada (204 No Content)**

---

## 2️⃣ HORARIOS ESPECIALES

### **✅ 2.1 GET - Listar horarios especiales de una sede**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/horarios-especiales/sede/1`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "sedeId": 1,
    "fecha": "2026-12-25",
    "horaApertura": null,
    "horaCierre": null,
    "motivo": "Cerrado por Navidad",
    "creadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 2.2 GET - Obtener horarios especiales por rango de fechas**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/horarios-especiales/sede/1/rango?fechaInicio=2026-12-01&fechaFin=2026-12-31`

---

### **✅ 2.3 POST - Crear horario especial**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/horarios-especiales`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "sedeId": 1,
  "fecha": "2026-12-31",
  "horaApertura": "10:00:00",
  "horaCierre": "18:00:00",
  "motivo": "Año Nuevo - Horario Especial"
}
```

**Para día cerrado (hora_apertura y hora_cierre = null):**
```json
{
  "sedeId": 1,
  "fecha": "2026-01-01",
  "horaApertura": null,
  "horaCierre": null,
  "motivo": "Cerrado por Año Nuevo"
}
```

---

### **✅ 2.4 PUT - Actualizar horario especial**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/horarios-especiales/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "fecha": "2026-12-25",
  "horaApertura": null,
  "horaCierre": null,
  "motivo": "Cerrado por Navidad - Actualizado"
}
```

---

### **✅ 2.5 DELETE - Eliminar horario especial**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/horarios-especiales/1`

---

## 3️⃣ RESTRICCIONES DE ALCOHOL

### **✅ 3.1 GET - Listar restricciones activas del negocio**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/restricciones-alcohol/activas`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sedeId": null,
    "tipoRestriccion": "dia_completo",
    "diasRestringidos": "[\"domingo\"]",
    "horaInicioRestriccion": null,
    "horaFinRestriccion": null,
    "edadMinimaRequerida": 18,
    "aplicaADelivery": true,
    "aplicaAPos": true,
    "aplicaATiendaOnline": true,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 3.2 GET - Listar restricciones de una sede específica**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/restricciones-alcohol/sede/1`

---

### **✅ 3.3 GET - Obtener restricción por ID**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/restricciones-alcohol/1`

---

### **✅ 3.4 POST - Crear restricción de alcohol**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/restricciones-alcohol`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON - Restricción por horario):**
```json
{
  "sedeId": 1,
  "tipoRestriccion": "horario",
  "diasRestringidos": "[\"viernes\",\"sabado\"]",
  "horaInicioRestriccion": "23:00:00",
  "horaFinRestriccion": "06:00:00",
  "edadMinimaRequerida": 18,
  "aplicaADelivery": true,
  "aplicaAPos": true,
  "aplicaATiendaOnline": false,
  "estaActivo": true
}
```

**Body (JSON - Restricción día completo):**
```json
{
  "sedeId": null,
  "tipoRestriccion": "dia_completo",
  "diasRestringidos": "[\"domingo\"]",
  "horaInicioRestriccion": null,
  "horaFinRestriccion": null,
  "edadMinimaRequerida": 18,
  "aplicaADelivery": true,
  "aplicaAPos": true,
  "aplicaATiendaOnline": true,
  "estaActivo": true
}
```

---

### **✅ 3.5 PUT - Actualizar restricción**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/restricciones-alcohol/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "tipoRestriccion": "horario",
  "horaInicioRestriccion": "22:00:00",
  "horaFinRestriccion": "07:00:00",
  "estaActivo": true
}
```

---

### **✅ 3.6 DELETE - Eliminar restricción**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/restricciones-alcohol/1`

---

## 4️⃣ ÁREAS DE MESAS

### **✅ 4.1 GET - Listar áreas de una sede**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/areas-mesas/sede/1`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "nombre": "Terraza",
    "descripcion": "Mesas al aire libre con vista a la calle",
    "orden": 1,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 4.2 GET - Obtener área por ID**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/areas-mesas/1`

---

### **✅ 4.3 POST - Crear área de mesas**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/areas-mesas`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "sedeId": 1,
  "nombre": "Jardín",
  "descripcion": "Área verde al aire libre",
  "orden": 5,
  "estaActivo": true
}
```

---

### **✅ 4.4 PUT - Actualizar área**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/areas-mesas/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nombre": "Terraza Premium",
  "descripcion": "Terraza renovada con nuevas vistas",
  "orden": 1,
  "estaActivo": true
}
```

---

### **✅ 4.5 DELETE - Eliminar área**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/areas-mesas/1`

---

## 5️⃣ MESAS

### **✅ 5.1 GET - Listar mesas de una sede**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/mesas/sede/1`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "sedeId": 1,
    "areaMesaId": 1,
    "numeroMesa": "T01",
    "codigoQr": "QR-MESA-T01-SEDE1",
    "capacidad": 4,
    "estado": "disponible",
    "forma": "circular",
    "posicionX": 10,
    "posicionY": 10,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 5.2 GET - Listar mesas por área**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/mesas/area/1`

---

### **✅ 5.3 GET - Listar mesas por estado**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/mesas/sede/1/estado/disponible`

Valores de estado:
- `disponible`
- `ocupada`
- `reservada`
- `mantenimiento`
- `inactiva`

---

### **✅ 5.4 GET - Buscar mesa por código QR**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/mesas/qr/QR-MESA-T01-SEDE1`

---

### **✅ 5.5 POST - Crear mesa**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/mesas`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "sedeId": 1,
  "areaMesaId": 1,
  "numeroMesa": "T10",
  "codigoQr": "QR-MESA-T10-SEDE1",
  "capacidad": 6,
  "estado": "disponible",
  "forma": "rectangular",
  "posicionX": 200,
  "posicionY": 100,
  "estaActivo": true
}
```

---

### **✅ 5.6 PUT - Actualizar mesa**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/mesas/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "numeroMesa": "T01-PREMIUM",
  "capacidad": 6,
  "forma": "rectangular",
  "posicionX": 15,
  "posicionY": 15,
  "estaActivo": true
}
```

---

### **✅ 5.7 PUT - Cambiar estado de mesa**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/mesas/1/estado/ocupada`

Estados válidos: `disponible`, `ocupada`, `reservada`, `mantenimiento`, `inactiva`

---

### **✅ 5.8 DELETE - Eliminar mesa**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/mesas/1`

---

## 6️⃣ CONFIGURACIÓN DE NEGOCIO

### **✅ 6.1 GET - Listar todas las configuraciones**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/configuracion-negocio`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "claveConfiguracion": "MONEDA_BASE",
    "valor": "PEN",
    "tipoValor": "texto",
    "descripcion": "Moneda base del negocio",
    "categoria": "general",
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 6.2 GET - Obtener configuración por clave**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/configuracion-negocio/clave/MONEDA_BASE`

---

### **✅ 6.3 GET - Listar configuraciones por categoría**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/configuracion-negocio/categoria/mesas`

Categorías comunes:
- `general`
- `mesas`
- `inventario`
- `notificaciones`
- `fidelidad`
- `promociones`

---

### **✅ 6.4 POST - Crear configuración**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/configuracion-negocio`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "claveConfiguracion": "PROPINA_SUGERIDA",
  "valor": "0.10",
  "tipoValor": "numero",
  "descripcion": "Porcentaje de propina sugerida",
  "categoria": "general",
  "estaActivo": true
}
```

---

### **✅ 6.5 PUT - Actualizar configuración**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/configuracion-negocio/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "valor": "USD",
  "descripcion": "Moneda base del negocio - Actualizada",
  "estaActivo": true
}
```

---

### **✅ 6.6 PUT - Guardar o actualizar configuración**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/configuracion-negocio/guardar-o-actualizar`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "claveConfiguracion": "NUEVA_CONFIG",
  "valor": "valor123",
  "tipoValor": "texto",
  "descripcion": "Nueva configuración",
  "categoria": "general",
  "estaActivo": true
}
```

**Nota:** Si la clave existe, actualiza. Si no existe, crea una nueva.

---

### **✅ 6.7 DELETE - Eliminar configuración**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/configuracion-negocio/1`

---

## 7️⃣ NOTIFICACIONES

### **✅ 7.1 GET - Listar notificaciones de un usuario**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/notificaciones/usuario/1`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "plantillaId": 1,
    "titulo": "Bienvenido a DrinkGo!",
    "mensaje": "Tu cuenta ha sido creada exitosamente.",
    "canal": "email",
    "estadoEntrega": "entregada",
    "prioridad": "normal",
    "estaLeido": true,
    "enviadoEn": "2026-02-16T10:00:00",
    "leidoEn": "2026-02-16T11:00:00",
    "creadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 7.2 GET - Listar notificaciones no leídas**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/notificaciones/usuario/1/no-leidas`

---

### **✅ 7.3 GET - Obtener notificación por ID**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/notificaciones/1`

---

### **✅ 7.4 POST - Crear notificación**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/notificaciones`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "usuarioId": 1,
  "plantillaId": 3,
  "titulo": "Pedido #002 Confirmado",
  "mensaje": "Tu pedido #002 ha sido confirmado exitosamente.",
  "canal": "push",
  "estadoEntrega": "pendiente",
  "prioridad": "alta",
  "estaLeido": false
}
```

---

### **✅ 7.5 PUT - Actualizar notificación**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/notificaciones/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "titulo": "Actualizado",
  "mensaje": "Mensaje actualizado",
  "estadoEntrega": "entregada"
}
```

---

### **✅ 7.6 PUT - Marcar como leída**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/notificaciones/1/marcar-leida`

---

### **✅ 7.7 PUT - Marcar todas como leídas**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/notificaciones/usuario/1/marcar-todas-leidas`

---

### **✅ 7.8 DELETE - Eliminar notificación**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/notificaciones/1`

---

## 8️⃣ MÉTODOS DE PAGO

### **✅ 8.1 GET - Listar todos los métodos de pago**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/metodos-pago`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "negocioId": 1,
    "codigo": "EFECTIVO",
    "nombre": "Efectivo",
    "tipo": "efectivo",
    "descripcion": "Pago en efectivo",
    "configuracionJson": null,
    "disponiblePos": true,
    "disponibleTiendaOnline": false,
    "requiereValidacion": false,
    "diasProcesamiento": 0,
    "comisionPorcentaje": 0.0,
    "orden": 1,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 8.2 GET - Listar métodos activos**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/metodos-pago/activos`

---

### **✅ 8.3 GET - Métodos disponibles en POS**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/metodos-pago/disponibles-pos`

---

### **✅ 8.4 GET - Métodos disponibles en tienda online**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/metodos-pago/disponibles-tienda-online`

---

### **✅ 8.5 GET - Buscar por código**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/metodos-pago/codigo/YAPE`

---

### **✅ 8.6 POST - Crear método de pago**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/metodos-pago`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "codigo": "PAYPAL",
  "nombre": "PayPal",
  "tipo": "billetera_digital",
  "descripcion": "Pago mediante PayPal",
  "configuracionJson": "{\"client_id\": \"xxx\", \"secret\": \"yyy\"}",
  "disponiblePos": false,
  "disponibleTiendaOnline": true,
  "requiereValidacion": true,
  "diasProcesamiento": 2,
  "comisionPorcentaje": 5.5,
  "orden": 10,
  "estaActivo": true
}
```

---

### **✅ 8.7 PUT - Actualizar método de pago**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/metodos-pago/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nombre": "Efectivo Soles",
  "descripcion": "Pago en efectivo - Solo soles",
  "disponiblePos": true,
  "disponibleTiendaOnline": false,
  "estaActivo": true
}
```

---

### **✅ 8.8 DELETE - Eliminar método de pago**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/metodos-pago/1`

---

## 9️⃣ USUARIO-SEDE

### **✅ 9.1 GET - Listar sedes de un usuario**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/usuarios-sedes/usuario/1`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "sedeId": 1,
    "esPredeterminado": true,
    "asignadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 9.2 GET - Listar usuarios de una sede**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/usuarios-sedes/sede/1`

---

### **✅ 9.3 GET - Obtener sede predeterminada del usuario**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/usuarios-sedes/usuario/1/predeterminada`

---

### **✅ 9.4 POST - Asignar usuario a sede**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/usuarios-sedes`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "usuarioId": 2,
  "sedeId": 2,
  "esPredeterminado": false
}
```

---

### **✅ 9.5 PUT - Establecer sede predeterminada**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/usuarios-sedes/1/establecer-predeterminada`

**Nota:** Al marcar una sede como predeterminada, automáticamente desmarca las otras del mismo usuario.

---

### **✅ 9.6 DELETE - Eliminar asignación**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/usuarios-sedes/1`

---

## 🔟 PLANTILLAS DE NOTIFICACIÓN

### **✅ 10.1 GET - Listar todas las plantillas**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/plantillas-notificacion`

**Respuesta Esperada (200 OK):**
```json
[
  {
    "id": 1,
    "negocioId": null,
    "codigo": "BIENVENIDA",
    "nombre": "Plantilla de Bienvenida",
    "canal": "email",
    "plantillaAsunto": "Bienvenido a DrinkGo, {{nombre}}!",
    "plantillaCuerpo": "Hola {{nombre}},\n\nGracias por registrarte...",
    "variablesJson": "[\"nombre\", \"email\"]",
    "esGlobal": true,
    "estaActivo": true,
    "creadoEn": "2026-02-16T10:00:00",
    "actualizadoEn": "2026-02-16T10:00:00"
  }
]
```

---

### **✅ 10.2 GET - Listar plantillas globales**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/plantillas-notificacion/globales`

---

### **✅ 10.3 GET - Listar plantillas activas**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/plantillas-notificacion/activas`

---

### **✅ 10.4 GET - Buscar por código**

**Método:** `GET`  
**URL:** `http://localhost:8080/api/plantillas-notificacion/codigo/BIENVENIDA`

---

### **✅ 10.5 POST - Crear plantilla**

**Método:** `POST`  
**URL:** `http://localhost:8080/api/plantillas-notificacion`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "codigo": "PROMOCION_SEMANA",
  "nombre": "Promoción de la Semana",
  "canal": "push",
  "plantillaAsunto": "¡Nueva Promoción {{nombre_promocion}}!",
  "plantillaCuerpo": "Hola {{nombre_cliente}},\n\nTenemos una nueva promoción: {{nombre_promocion}} con {{descuento}}% de descuento.\n\n¡No te lo pierdas!",
  "variablesJson": "[\"nombre_cliente\", \"nombre_promocion\", \"descuento\"]",
  "esGlobal": false,
  "estaActivo": true
}
```

---

### **✅ 10.6 PUT - Actualizar plantilla**

**Método:** `PUT`  
**URL:** `http://localhost:8080/api/plantillas-notificacion/1`  
**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nombre": "Plantilla de Bienvenida Mejorada",
  "plantillaCuerpo": "Hola {{nombre}},\n\nGracias por unirte a DrinkGo...",
  "estaActivo": true
}
```

---

### **✅ 10.7 DELETE - Eliminar plantilla**

**Método:** `DELETE`  
**URL:** `http://localhost:8080/api/plantillas-notificacion/1`

---

## 📊 CHECKLIST DE PRUEBAS

### ✅ Antes de demostrar:
- [ ] Servidor Spring Boot corriendo (puerto 8080)
- [ ] MySQL/XAMPP iniciado
- [ ] Datos de prueba insertados (`datos_prueba_bloque_3.sql`)
- [ ] Postman configurado correctamente

### ✅ Endpoints críticos a probar:
- [ ] Horarios Sede - CRUD completo
- [ ] Horarios Especiales - Rango de fechas
- [ ] Restricciones Alcohol - Activas y por sede
- [ ] Áreas Mesas - Por sede
- [ ] Mesas - Cambio de estado y búsqueda por QR
- [ ] Configuración - Guardar o actualizar
- [ ] Notificaciones - Marcar como leída
- [ ] Métodos de Pago - Disponibles en POS/Online
- [ ] Usuario-Sede - Establecer predeterminada
- [ ] Plantillas - Globales y por código

---

## 🎯 FLUJO DE PRUEBA RECOMENDADO

1. **Configuración inicial:**
   - Crear horarios semanales para una sede
   - Crear horarios especiales (feriados)
   - Configurar restricciones de alcohol

2. **Gestión de mesas:**
   - Crear áreas de mesas
   - Crear mesas en cada área
   - Cambiar estados de mesas
   - Buscar mesa por QR

3. **Configuración del negocio:**
   - Crear configuraciones clave-valor
   - Buscar por clave y categoría
   - Usar guardar-o-actualizar

4. **Notificaciones:**
   - Crear plantillas
   - Crear notificaciones
   - Marcar como leídas

5. **Métodos de pago:**
   - Crear métodos de pago
   - Filtrar por disponibilidad (POS/Online)

---

## 🐛 TROUBLESHOOTING

### Error 404 - Endpoint no encontrado
- Verificar que el servidor esté corriendo
- Verificar la URL (debe incluir `/api/`)
- Verificar que el controlador esté correctamente mapeado

### Error 500 - Internal Server Error
- Revisar logs del servidor en consola
- Verificar que los datos de prueba estén insertados
- Verificar integridad referencial (IDs de sede, negocio, etc.)

### Error 400 - Bad Request
- Verificar formato JSON en el Body
- Verificar que todos los campos requeridos estén presentes
- Verificar tipos de datos (números, strings, booleanos)

### No se insertan datos
- Verificar restricciones de negocioId en JWT
- Revisar logs para ver errores de base de datos
- Verificar que las tablas existan

---

## 📝 NOTAS IMPORTANTES

1. **Multi-tenant:** Todos los endpoints filtran automáticamente por `negocioId` extraído del JWT (o asumido como 1 en modo desarrollo).

2. **Códigos QR:** Deben ser únicos globalmente en la tabla `mesas`.

3. **Estados de Mesa:** Solo se puede cambiar entre estados válidos. No cambiar directamente de `mantenimiento` a `ocupada`.

4. **Horarios Especiales:** Si `hora_apertura` y `hora_cierre` son `null`, significa que está cerrado todo el día.

5. **Restricciones Alcohol:** Pueden ser globales (sin `sedeId`) o específicas por sede.

6. **Plantillas Globales:** Las plantillas con `negocioId = null` están disponibles para todos los negocios.

7. **Configuración:** Use `guardar-o-actualizar` para evitar duplicados por clave.

---

## ✅ ÉXITO

Si completas todas las pruebas exitosamente, tu **Bloque 3** está funcionando correctamente. 

**¡A probar! 🚀**
