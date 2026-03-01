# 📐 ESTRUCTURA MODULAR DEL FRONTEND - DRINKGO
**Sistema Multi-Tenant para Licorerías**  
**Fecha:** 24 de Febrero, 2026  
**Versión:** 1.0

---

## 🎯 ÍNDICE
1. [Panel SuperAdmin (Gestión de Plataforma)](#superadmin)
2. [Panel Admin (Gestión de Negocio)](#admin)
3. [Resumen Comparativo](#resumen)

---

<a name="superadmin"></a>
## 👨‍💼 PANEL SUPERADMIN - GESTIÓN DE PLATAFORMA

**Total de Módulos: 6**

El SuperAdmin tiene acceso completo a la gestión de la plataforma SaaS, controlando todos los negocios (tenants), planes de suscripción, facturación y monitoreo global del sistema.

---

### 📊 **MÓDULO 1: DASHBOARD PLATAFORMA**
**Descripción:** Panel ejecutivo con métricas clave de la plataforma  
**RF:** RF-RPL-001

**Componentes/Vistas:**
- 📈 **Vista General**
  - Total de negocios activos, suspendidos, cancelados
  - Ingresos mensuales/anuales de la plataforma
  - Gráficos de crecimiento de ventas
  - Métricas de retención de clientes
  - Negocios nuevos por mes
  - Tasa de conversión de planes

- 📉 **Métricas en Tiempo Real**
  - Negocios activos en este momento
  - Transacciones por segundo
  - Uso de recursos por negocio
  - Alertas de sistema

**Acciones:**
- Ver reportes consolidados
- Exportar dashboards (PDF, Excel)
- Configurar widgets personalizados

---

### 🏢 **MÓDULO 2: GESTIÓN DE NEGOCIOS (TENANTS)**
**Descripción:** Administración completa de todas las licorerías registradas  
**RF:** RF-PLT-001, RF-ADM-003

**Componentes/Vistas:**
- 📋 **Lista de Negocios**
  - Tabla con filtros avanzados (estado, plan, fecha registro)
  - Búsqueda por RUC, razón social, email
  - Vista de tarjetas con información resumida
  - Indicadores de estado (activo, suspendido, moroso, cancelado)
  
- ➕ **Registro de Nuevo Negocio**
  - Formulario de datos legales (RUC, razón social, dirección fiscal)
  - Datos del admin principal del negocio
  - Asignación de plan de suscripción
  - **Configuración de sede principal inicial:**
    - Nombre de la sede
    - Dirección completa
    - Teléfono, email
    - Es sede principal (marcado por defecto)
  - Configuración inicial automática
  
- ✏️ **Detalle de Negocio**
  - Información completa del negocio
  - Historial de suscripciones
  - **Gestión de Sedes:**
    - Lista de sedes del negocio
    - Validación según límite del plan
    - ➕ Crear nueva sede (controla límite del plan)
    - ✏️ Editar sede existente
    - 🗑️ Desactivar sede
    - Ver almacenes por sede
  - Usuarios activos
  - Estadísticas de uso (productos, ventas, almacenes)
  - Facturas de suscripción
  - Logs de actividad
  
- 🏪 **Crear/Editar Sede del Negocio**
  - Validar límite de sedes según plan
  - Nombre de la sede
  - Dirección completa (calle, distrito, provincia, departamento)
  - Coordenadas GPS
  - Teléfono, email de contacto
  - Es sede principal
  - Estado (activa/inactiva)
  - Almacenes de la sede (vista resumida)

**Acciones:**
- Crear nuevo negocio
- Editar información de negocio
- **Crear nueva sede para el negocio**
- **Editar sede existente**
- **Desactivar sede**
- **Validar límite de sedes según plan**
- Suspender/Reactivar negocio
- Cancelar suscripción
- Ver historial completo
- Cambiar plan del negocio

---

### 💳 **MÓDULO 3: PLANES DE SUSCRIPCIÓN**
**Descripción:** Gestión de planes comerciales de la plataforma  
**RF:** RF-PLT-002, RF-PLT-003, RF-PLT-004, RF-PLT-005

**Componentes/Vistas:**
- 📝 **Lista de Planes**
  - Planes activos e inactivos
  - Vista comparativa de características
  - Cantidad de negocios suscritos por plan
  
- ➕ **Crear/Editar Plan**
  - Nombre y descripción del plan
  - Precio y moneda (PEN, USD)
  - Periodo de facturación (mensual/anual)
  - **Límites operativos:**
    - Máximo de sedes
    - Máximo de usuarios
    - Máximo de productos
    - Máximo de almacenes por sede
  - **Funcionalidades habilitadas:**
    - ✅ Permite POS
    - ✅ Permite Tienda Online
    - ✅ Permite Delivery
    - ✅ Permite Mesas
    - ✅ Permite Facturación Electrónica
    - ✅ Permite Multi-Almacén
    - ✅ Permite Reportes Avanzados
    - ✅ Permite Acceso API
  - Funcionalidades adicionales (JSON configurable)

**Acciones:**
- Crear nuevo plan
- Editar plan existente
- Desactivar/Activar plan
- Comparar planes
- Ver negocios suscritos

---

### 💰 **MÓDULO 4: FACTURACIÓN DE SUSCRIPCIONES**
**Descripción:** Control de facturación recurrente y pagos de la plataforma  
**RF:** RF-FAC-001, RF-FAC-002

**Componentes/Vistas:**
- 📄 **Lista de Facturas**
  - Filtros por estado (pendiente, pagada, vencida, anulada)
  - Búsqueda por negocio, fecha, monto
  - Indicadores visuales de morosidad
  
- 🔍 **Detalle de Factura**
  - Información completa de la factura
  - Negocio asociado
  - Plan suscrito
  - Periodo facturado
  - Método de pago utilizado
  - Estado de pago
  - Historial de intentos de cobro
  
- 📊 **Control de Morosidad**
  - Negocios con facturas vencidas
  - Días de mora por negocio
  - Acciones automáticas configuradas
  - Emails de recordatorio enviados

**Acciones:**
- Ver todas las facturas
- Generar factura manual
- Marcar como pagada
- Enviar recordatorio de pago
- Suspender negocio por morosidad
- Configurar reintentos automáticos
- Anular factura
- Exportar reporte de facturación

---

### ⚙️ **MÓDULO 5: CONFIGURACIÓN GLOBAL**
**Descripción:** Configuraciones que afectan a toda la plataforma  
**RF:** RF-CGL-001

**Componentes/Vistas:**
- 🌐 **Parámetros Globales**
  - Impuestos por defecto (IGV 18%)
  - Monedas soportadas
  - Métodos de pago disponibles
  - Integraciones externas (SUNAT, pasarelas de pago)
  - Políticas de seguridad (tiempo de sesión, intentos de login)
  
- 📧 **Configuración de Emails**
  - Plantillas de correos del sistema
  - SMTP configurado
  - Emails de notificaciones automáticas
  
- 🔌 **Integraciones**
  - API Keys para servicios externos
  - Webhooks configurados
  - Logs de integraciones

**Acciones:**
- Editar parámetros globales
- Configurar integraciones
- Gestionar plantillas de email
- Ver historial de cambios

---

### 🔒 **MÓDULO 6: AUDITORÍA Y LOGS**
**Descripción:** Registro y consulta de actividades críticas del sistema  
**RF:** RF-L-002

**Componentes/Vistas:**
- 📜 **Registro de Auditoría**
  - Tabla con todas las acciones críticas
  - Filtros por:
    - Usuario (SuperAdmin o negocio)
    - Fecha
    - Tipo de acción (creación, edición, eliminación)
    - Módulo afectado
    - Severidad (info, warning, critical)
  
- 🔍 **Detalle de Evento**
  - Usuario que realizó la acción
  - Fecha y hora exacta
  - IP de origen
  - Descripción completa de la acción
  - Datos antes/después del cambio
  - Contexto adicional

**Acciones:**
- Buscar en logs
- Filtrar por múltiples criterios
- Exportar logs (CSV, JSON)
- Ver trazabilidad completa
- Detectar anomalías

---
---

<a name="admin"></a>
## 🏪 PANEL ADMIN - GESTIÓN DE NEGOCIO

**Total de Módulos: 10**

El Admin gestiona completamente su licorería (negocio), con acceso a todas las funcionalidades operativas según su plan de suscripción.

---

### 📊 **MÓDULO 1: DASHBOARD NEGOCIO**
**Descripción:** Panel principal con métricas del negocio  
**RF:** RF-RPL-001 (nivel negocio)

**Componentes/Vistas:**
- 📈 **Resumen General**
  - Ventas del día/semana/mes
  - Productos más vendidos
  - Stock crítico y alertas
  - Pedidos pendientes
  - Caja del día
  - Gráficos de tendencias
  
- 🏪 **Vista por Sede**
  - Selector de sede
  - Métricas específicas de cada sede
  - Comparativa entre sedes

**Acciones:**
- Cambiar periodo de visualización
- Exportar reportes rápidos
- Configurar widgets

---

### ⚙️ **MÓDULO 2: CONFIGURACIÓN DEL NEGOCIO, SEDES Y STOREFRONT**
**Descripción:** Configuración general del negocio, sedes, branding y tienda online  
**RF:** RF-ADM-001, RF-ADM-002, RF-ADM-004, RF-ADM-006, RF-ADM-007, RF-ADM-008, RF-ADM-009, RF-ADM-010, RF-ADM-022, RF-ADM-023, RF-ADM-025, RF-ECO-001 a RF-ECO-005, RF-ECO-017

**Componentes/Vistas:**
- 🏢 **Datos del Negocio**
  - RUC, razón social, nombre comercial
  - Dirección fiscal
  - Teléfono, email, website
  - Logo y branding
  - Ver plan de suscripción actual
  
- 🎨 **Branding**
  - Logo del negocio (múltiples versiones)
  - Colores corporativos
  - Tipografía
  - Personalización de tienda online
  
- 🔧 **Parámetros del Sistema**
  - Moneda principal
  - Impuestos aplicables (IGV)
  - Formato de fecha/hora
  - Idioma del sistema
  - Reglas de negocio (stock mínimo, alertas)
  
- 💳 **Métodos de Pago**
  - CRUD de métodos de pago habilitados
  - Efectivo, tarjetas, transferencias, billeteras digitales
  - Configuración de pasarelas de pago online
  - Comisiones por método
  
- 📋 **Lista de Sedes**
  - Tabla con todas las sedes del negocio (solo lectura de estructura)
  - Estado (activa/inactiva)
  - Dirección, teléfono, email
  - Indicador de sede principal
  - **Nota:** La creación de nuevas sedes la realiza SuperAdmin
  
- ✏️ **Editar Configuración de Sede**
  - Nombre de la sede (editable)
  - Dirección completa (editable)
  - Teléfono, email de contacto (editable)
  - Coordenadas GPS
  - **Estado no editable** (solo SuperAdmin puede desactivar)
  
- 🏢 **Almacenes por Sede**
  - Lista de almacenes de la sede
  - Tipo (general, refrigerado, congelado)
  - Crear/editar almacenes
  - Asignar productos a almacenes
  
- 🕐 **Horarios de Operación**
  - Configuración de horarios regulares (JSON)
  - Lunes a Domingo: hora apertura/cierre
  - Horarios especiales (festivos, eventos)
  - Calendario anual de días especiales
  
- 🚫 **Restricciones de Alcohol**
  - Horarios permitidos para venta de alcohol
  - Días restringidos (elecciones, etc.)
  - Verificación de edad obligatoria
  - Configuración por tipo de bebida
  
- 🚚 **Zonas de Delivery**
  - CRUD de zonas de entrega
  - Nombre, descripción
  - Coordenadas/polígonos geográficos
  - Costo de envío por zona
  - Tiempo estimado de entrega
  - Monto mínimo de pedido
  
- 🪑 **Módulo de Mesas**
  - Activar/desactivar funcionalidad
  - CRUD de mesas
  - Número, capacidad
  - Estado (disponible, ocupada, reservada)
  - Ubicación (interior, exterior, barra)
  
- 🛒 **Configuración de Tienda Online (StoreFront)**
  - Nombre de la tienda online
  - Slug/URL de la tienda (ej: mitienda.drinkgo.pe)
  - Dominio personalizado (opcional)
  - Logo y favicon
  - Banner principal
  - Colores y tema (personalización visual)
  - Descripción SEO
  - Keywords
  - Estado (activa/inactiva)
  
- 🎨 **Personalización Visual de StoreFront**
  - Tema/Plantilla
  - Colores corporativos (primario, secundario, enlaces, botones)
  - Tipografía
  - Banner de inicio
  - Slider de imágenes
  - Secciones destacadas
  
- 💳 **Métodos de Pago Online**
  - Configurar pasarelas de pago
  - Mercado Pago, Niubiz, PayPal
  - Yape/Plin (código QR)
  - Transferencia bancaria
  - Pago contra entrega
  - Configurar credenciales API
  
- 🚚 **Métodos de Entrega Online**
  - Delivery (con zonas configuradas)
  - Recojo en tienda
  - Configurar costos y tiempos estimados
  
- 🕐 **Horarios de Venta Online**
  - Configurar horarios de atención online
  - Diferentes a horarios físicos
  - Horarios especiales
  
- 📄 **Páginas del CMS**
  - CRUD de páginas (Inicio, Catálogo, Nosotros, Contacto, Términos y condiciones, Política de privacidad, Política de devoluciones, FAQ)
  - Editor de contenido (WYSIWYG o Markdown)
  - Slug de la página
  - Estado (publicada/borrador)
  
- 📦 **Productos Visibles en StoreFront**
  - Gestionar qué productos mostrar
  - Productos destacados
  - Orden de visualización
  - Categorías visibles
  
- 🔞 **Verificación de Edad en StoreFront**
  - Activar/Desactivar verificación
  - Mensaje de advertencia
  - Método de verificación (checkbox, fecha de nacimiento)

**Acciones:**
- Editar datos del negocio
- Subir logo/imágenes
- Configurar parámetros del sistema
- Gestionar métodos de pago
- Ver plan de suscripción actual
- Ver lista de sedes (creadas por SuperAdmin)
- Editar información de sede (datos operativos)
- Gestionar almacenes
- Configurar horarios de operación
- Definir restricciones de alcohol
- Administrar zonas de delivery
- Gestionar mesas
- Solicitar nueva sede a SuperAdmin
- Configurar tienda online
- Personalizar diseño de storefront
- Configurar métodos de pago online
- Configurar métodos de entrega
- Gestionar páginas CMS
- Activar/Desactivar tienda online
- Gestionar productos visibles

---

### 👥 **MÓDULO 3: SEGURIDAD, USUARIOS Y CLIENTES**
**Descripción:** Gestión de usuarios, roles, permisos y base de clientes del negocio  
**RF:** RF-ADM-011 a RF-ADM-021, RF-VTA-023, RF-ECO-017

**Componentes/Vistas:**
- 👤 **Lista de Usuarios**
  - Tabla con todos los usuarios del negocio
  - Filtros por rol, sede, estado
  - Búsqueda por nombre, email
  
- ➕ **Crear/Editar Usuario**
  - Nombre completo
  - Email (username)
  - Contraseña (encriptada)
  - Rol(es) asignado(s)
  - Sede(s) asignada(s)
  - Estado (activo/inactivo)
  - Foto de perfil
  
- 🎭 **Gestión de Roles**
  - Lista de roles del negocio
  - Roles predefinidos (Admin, Cajero, Inventario, etc.)
  - Crear rol personalizado
  - Editar roles
  
- 🔐 **Permisos por Rol**
  - Matriz de permisos
  - Listado de módulos del sistema
  - Permisos granulares por módulo:
    - Ver/Listar
    - Crear
    - Editar
    - Eliminar/Desactivar
    - Exportar
    - Aprobar
  
- 👤 **Asignación de Usuarios a Sedes**
  - Tabla de relaciones usuario-sede
  - Control de acceso por ubicación
  
- 🔍 **Log de Auditoría del Negocio**
  - Registro de acciones de los usuarios
  - Filtros por usuario, fecha, acción
  - Trazabilidad completa
  
- 📋 **Lista de Clientes**
  - Tabla con todos los clientes
  - Búsqueda por DNI/RUC, nombre, email
  - Filtros por tipo (persona, empresa), estado
  - Indicadores de cliente frecuente
  
- ➕ **Crear/Editar Cliente**
  - Tipo de documento (DNI, RUC, Carnet de extranjería, Pasaporte)
  - Número de documento
  - Nombres y apellidos / Razón social
  - Email
  - Teléfono
  - Fecha de nacimiento (para validación de edad)
  - Dirección
  - Distrito, provincia, departamento
  - Direcciones adicionales (para delivery)
  - Estado (activo/inactivo)
  
- 🔍 **Detalle de Cliente**
  - Información completa
  - Historial de compras
  - Total gastado
  - Productos preferidos
  - Última compra
  - Preferencias de entrega
  
- 📧 **Gestión de Comunicaciones**
  - Enviar email/SMS
  - Plantillas de mensajes
  - Historial de comunicaciones

**Acciones:**
- Registrar nuevo usuario
- Editar usuario
- Desactivar usuario
- Asignar roles
- Asignar sedes
- Crear rol personalizado
- Asignar permisos a rol
- Ver logs de auditoría
- Resetear contraseña
- Crear cliente
- Editar cliente
- Desactivar cliente
- Ver historial de compras
- Enviar comunicaciones
- Exportar lista de clientes

---

### 📦 **MÓDULO 4: CATÁLOGO DE PRODUCTOS, DESCUENTOS Y PROMOCIONES**
**Descripción:** Gestión completa del catálogo de productos, promociones y descuentos  
**RF:** RF-PRO-001 a RF-PRO-022, RF-VTA-005

**Componentes/Vistas:**
- 📋 **Lista de Productos**
  - Tabla con todos los productos
  - Búsqueda avanzada (nombre, código, código de barras)
  - Filtros por categoría, marca, estado
  - Vista de tarjetas con imágenes
  - Indicador de stock actual
  
- ➕ **Crear/Editar Producto**
  - **Información básica:**
    - Nombre del producto
    - Código interno
    - Código de barras (EAN, UPC)
    - Descripción corta y larga
    - SKU
  - **Clasificación:**
    - Categoría
    - Marca
    - Unidad de medida
  - **Para licores (campos especiales):**
    - Tipo de bebida (cerveza, vino, pisco, ron, vodka, etc.)
    - Grado alcohólico (%)
    - Volumen (ml, L)
    - Origen/País
    - Añada (para vinos)
  - **Precios:**
    - Precio de compra (referencia)
    - Precio de venta al público
    - Precio mínimo de venta (opcional, para control de descuentos)
    - Margen de ganancia (calculado automáticamente)
  - **Stock:**
    - Stock mínimo
    - Stock máximo
    - Punto de reorden
  - **Almacenamiento:**
    - Temperatura requerida (ambiente, refrigerado, congelado)
  - **Estado:**
    - Activo/Inactivo
    - Visible en tienda online
    - Destacado
    - Permite fraccionar
  - **Multimedia:**
    - Imágenes del producto (múltiples)
    - Imagen principal
    - Videos (opcional)
  
- 🏷️ **Categorías**
  - CRUD completo de categorías
  - Nombre, descripción
  - Imagen de categoría
  - Orden de visualización
  - Categoría padre (subcategorías)
  - Estado activo/inactivo
  
- 🏭 **Marcas**
  - CRUD completo de marcas
  - Nombre de la marca
  - País de origen
  - Logo de la marca
  - Descripción
  - URL del fabricante
  - Estado activo/inactivo
  
- 📏 **Unidades de Medida**
  - CRUD de unidades
  - Nombre (unidad, caja, six-pack, botella, litro)
  - Abreviatura
  - Factor de conversión
  
- 🎁 **Combos Promocionales**
  - Lista de combos
  - Crear/editar combo
  - Nombre, descripción
  - Precio especial del combo
  - Productos incluidos (composición)
  - Cantidad de cada producto
  - Vigencia (desde/hasta)
  - Imagen del combo
  - Estado activo/inactivo
  
- 🖼️ **Gestión de Imágenes**
  - Subir múltiples imágenes por producto
  - Establecer imagen principal
  - Ordenar imágenes
  - Agregar texto alternativo
  - Eliminar imágenes
  
- 📥 **Importación Masiva**
  - Descargar plantilla Excel/CSV
  - Subir archivo con productos
  - Validación de datos
  - Reporte de errores
  - Vista previa antes de importar
  - Confirmar importación
  
- 📋 **Lista de Promociones**
  - Todas las promociones
  - Filtros por estado (activa, programada, finalizada)
  - Filtros por tipo
  
- ➕ **Crear Promoción**
  - Nombre de la promoción
  - Descripción
  - Tipo de descuento:
    - Porcentaje
    - Monto fijo
    - 2x1, 3x2
    - Regalo al comprar
    - Descuento escalonado por volumen
  - Valor del descuento
  - Vigencia (desde/hasta)
  - Restricciones:
    - Días de la semana
    - Horarios
    - Sedes aplicables
    - Canales (POS, online, ambos)
  - Aplicable a:
    - Todos los productos
    - Categorías específicas
    - Productos específicos
    - Marcas específicas
  - Cantidad máxima de usos
  - Usos por cliente
  - Monto mínimo de compra
  - Estado (activa/inactiva)
  
- 🎯 **Condiciones de Promoción**
  - Configurar reglas avanzadas
  - Tipo de entidad (producto, categoría, marca)
  - Cantidad mínima
  - Producto regalo (si aplica)
  - Combinable con otras promociones
  
- 🔍 **Detalle de Promoción**
  - Información completa
  - Estadísticas de uso
  - Ventas generadas
  - Clientes que lo usaron
  
- 📊 **Análisis de Promociones**
  - Promociones más efectivas
  - ROI de promociones
  - Incremento de ventas

**Acciones:**
- Crear producto
- Editar producto
- Desactivar producto
- Buscar productos
- Filtrar catálogo
- Crear categoría
- Crear marca
- Crear combo
- Definir composición de combo
- Subir imágenes
- Importar productos masivamente
- Exportar catálogo (Excel, CSV)
- Configurar visibilidad en storefront
- Crear promoción
- Editar promoción
- Activar/Desactivar promociones
- Configurar reglas de promoción
- Ver estadísticas de promociones
- Exportar reporte de promociones

---

### 📊 **MÓDULO 5: GESTIÓN DE INVENTARIO**
**Descripción:** Control de stock, lotes FIFO, movimientos y alertas  
**RF:** RF-INV-001 a RF-INV-009

**Componentes/Vistas:**
- 📦 **Stock Consolidado**
  - Vista general del inventario
  - Tabla con todos los productos
  - Stock actual por producto
  - Stock por almacén
  - Valor total del inventario
  - Alertas visuales (stock bajo, stock crítico)
  - Filtros por sede, almacén, categoría
  
- 📥 **Entrada de Lote (Recepción)**
  - Formulario de entrada
  - Producto
  - Almacén destino
  - Cantidad recibida
  - Fecha de vencimiento
  - Costo unitario
  - Proveedor
  - Número de lote del fabricante
  - Documento de respaldo
  - Observaciones
  
- 📜 **Lotes de Inventario**
  - Lista de todos los lotes activos
  - Filtros por producto, almacén, fecha de vencimiento
  - Stock actual de cada lote
  - Fecha de ingreso
  - Fecha de vencimiento
  - Costo unitario
  - Alertas de próximo vencimiento
  - **Acciones por lote:**
    - 🔧 **Botón Ajustar**: Abre modal con formulario de ajuste:
      - Tipo (entrada/salida)
      - Cantidad a ajustar
      - Motivo (merma, robo, error de conteo, muestra, donación, vencimiento, rotura, defecto)
      - Acción tomada (descarte, devolución a proveedor, donación, ajuste contable)
      - Observaciones
      - Documento de respaldo (opcional)
      - Usuario responsable (automático)
    - Ver historial del lote
    - Ver movimientos relacionados
  
- 🔄 **Movimientos de Inventario (Reporte Kardex)**
  - **Historial completo de movimientos con detalles:**
    - Tabla con todos los movimientos realizados
    - Fecha y hora del movimiento
    - Tipo de movimiento:
      - Entrada por compra
      - Salida por venta
      - Ajuste manual (entrada/salida)
      - Transferencia entre almacenes
      - Merma/Vencimiento
      - Devolución
      - Stock inicial
    - Producto afectado
    - Almacén
    - Lote relacionado
    - Cantidad (positiva para entradas, negativa para salidas)
    - **Para ajustes, incluye:**
      - Motivo detallado (merma, robo, error de conteo, muestra, donación, vencimiento, rotura, defecto)
      - Acción tomada (descarte, devolución a proveedor, donación, ajuste contable)
      - Documento de respaldo (si aplica)
    - Usuario responsable
    - Observaciones
  - **Filtros avanzados:**
    - Por tipo de movimiento
    - Por rango de fechas
    - Por producto específico
    - Por almacén
    - Por usuario responsable
    - Por lote
  - **Exportación:**
    - Excel con reporte completo
    - PDF para auditorías
    - CSV para análisis externo
  - **Visualización:**
    - Vista de tabla detallada
    - Gráficos de movimientos por tipo
    - Línea de tiempo por producto
  
- 🔄 **Transferencias Entre Almacenes**
  - Formulario de transferencia
  - Almacén origen
  - Almacén destino
  - Producto(s) a transferir
  - Cantidad
  - Usuario responsable
  - Estado (pendiente, en tránsito, recibido)
  - Fecha de envío/recepción
  
- ⚠️ **Alertas de Inventario**
  - **Stock Bajo:** Productos que alcanzaron el stock mínimo
  - **Stock Crítico:** Productos con menos del 20% del stock mínimo
  - **Próximos a Vencer:** Productos con menos de 30 días para vencer
  - **Vencidos:** Productos que ya pasaron fecha de vencimiento
  - Configuración de alertas automáticas
  - Notificaciones por email
  
- 🧮 **Toma de Inventario Físico**
  - Crear sesión de inventario
  - Fecha de conteo
  - Almacén
  - Usuarios participantes
  - Captura de cantidades físicas
  - Comparación con sistema
  - Generación de ajustes automáticos
  - Cierre de inventario

**Acciones:**
- Ver stock consolidado
- Registrar entrada de lote
- Ver lotes activos
- Consultar kardex/movimientos
- Ajustar lote individualmente (desde botón en tabla)
- Transferir entre almacenes
- Configurar alertas
- Ver productos por vencer
- Realizar inventario físico
- Exportar reportes de inventario
- Ver trazabilidad de lotes

---

### 🏭 **MÓDULO 6: PROVEEDORES Y COMPRAS**
**Descripción:** Gestión de proveedores, órdenes de compra y recepción  
**RF:** RF-COP-001 a RF-COP-012

**Componentes/Vistas:**
- 📋 **Lista de Proveedores**
  - Tabla con todos los proveedores
  - Búsqueda por RUC, razón social
  - Filtros por categoría, estado
  - Indicador de proveedor más usado
  
- ➕ **Crear/Editar Proveedor**
  - RUC/DNI
  - Razón social
  - Nombre comercial
  - Dirección
  - Teléfono, email
  - Persona de contacto
  - Términos de pago
  - Días de crédito
  - Categorías de productos que suministra
  - Banco, cuenta bancaria
  - Estado activo/inactivo
  
- 📦 **Productos por Proveedor**
  - Catálogo de productos del proveedor
  - Código del proveedor
  - Precio de compra
  - Tiempo de entrega
  - Cantidad mínima de orden
  
- 🛒 **Órdenes de Compra**
  - Lista de órdenes
  - Estados: borrador, pendiente, aprobada, recibida (parcial/total), cancelada
  - Filtros por estado, proveedor, fecha
  
- ➕ **Crear Orden de Compra**
  - Seleccionar proveedor
  - Sede destino
  - Almacén destino
  - Fecha de solicitud
  - Fecha de entrega esperada
  - Productos (detalle):
    - Producto
    - Cantidad solicitada
    - Precio unitario
    - Subtotal
  - Subtotal de la orden
  - Impuestos (IGV)
  - Total
  - Método de pago
  - Términos y condiciones
  - Observaciones
  
- 📄 **Detalle de Orden de Compra**
  - Información completa de la orden
  - Estado actual
  - Historial de cambios
  - Productos ordenados
  - Productos recibidos (con marcas de recepción)
  - Documentos adjuntos
  
- ✅ **Aprobar/Rechazar Orden**
  - Flujo de aprobación
  - Comentarios de aprobación/rechazo
  - Usuario aprobador
  
- 📥 **Recepción de Mercancía**
  - Seleccionar orden de compra
  - Marcar productos recibidos
  - Cantidad recibida vs solicitada
  - Estado de cada ítem (completo/parcial/pendiente)
  - Generar entrada de inventario automática
  - Capturar lote y fecha de vencimiento
  - Calcular diferencias
  
- ↩️ **Devoluciones a Proveedor**
  - Lista de devoluciones
  - Crear devolución
  - Orden de compra relacionada
  - Producto(s) devuelto(s)
  - Cantidad
  - Motivo (defectuoso, error en pedido, vencido)
  - Estado (pendiente, aprobado, completado)
  
- 💰 **Cuentas por Pagar**
  - Lista de órdenes pendientes de pago
  - Monto adeudado por proveedor
  - Fecha de vencimiento
  - Días de mora
  - Historial de pagos
  
- 💳 **Registrar Pago a Proveedor**
  - Orden de compra
  - Monto pagado
  - Método de pago
  - Fecha de pago
  - Referencia/Comprobante
  - Observaciones

**Acciones:**
- Crear proveedor
- Editar proveedor
- Desactivar proveedor
- Crear orden de compra
- Editar orden (solo en estado borrador)
- Aprobar/Rechazar orden
- Recibir mercancía
- Crear devolución a proveedor
- Consultar cuentas por pagar
- Registrar pago
- Ver historial de compras
- Exportar reportes

---

### 💰 **MÓDULO 7: VENTAS, PUNTO DE VENTA (POS) Y PEDIDOS**
**Descripción:** Sistema de ventas, cajas registradoras, punto de venta, pedidos y devoluciones  
**RF:** RF-VTA-001 a RF-VTA-021, RF-FIN-008 a RF-FIN-012

**Componentes/Vistas:**
- 🖥️ **Punto de Venta (POS)**
  - Interfaz optimizada para ventas rápidas
  - Búsqueda de productos (por nombre, código, escaneo)
  - Carrito de compra
  - Lista de productos agregados
  - Cantidades editables
  - Precios unitarios y totales
  - Descuentos por ítem
  - Subtotal, IGV, Total
  - Selector de cliente
  - Tipo de comprobante (boleta/factura)
  - Métodos de pago múltiples
  - Botones de acceso rápido a productos frecuentes
  - **Verificación de edad (para alcohol):**
    - Checkbox de confirmación
    - Validación obligatoria
  
- 💵 **Cajas Registradoras**
  - Lista de cajas de la sede
  - Estado (abierta, cerrada, en mantenimiento)
  - CRUD de cajas
  - Código, nombre
  - Sede asignada
  
- 🔓 **Abrir Sesión de Caja**
  - Seleccionar caja
  - Usuario cajero
  - Monto inicial en efectivo
  - Fecha y hora de apertura
  - Observaciones
  
- 🔒 **Cerrar Sesión de Caja**
  - Cuadre de caja
  - Monto esperado en efectivo
  - Monto real contado
  - Diferencia (sobrante/faltante)
  - Desglose por denominación
  - Total de ventas del turno
  - Desglose por método de pago
  - Gastos realizados
  - Retiros de efectivo
  - Informe de cierre (imprimible)
  - Observaciones
  
- 💸 **Movimientos de Caja**
  - Tipos: retiro, depósito, gasto menor
  - Monto
  - Motivo
  - Usuario responsable
  - Fecha y hora
  
- 📊 **Sesiones de Caja**
  - Historial de todas las sesiones
  - Filtros por caja, usuario, fecha
  - Estado de cada sesión
  - Resumen de ventas
  
- 🧾 **Ventas Realizadas**
  - Lista de todas las ventas
  - Filtros por fecha, sede, cajero, tipo de comprobante
  - Búsqueda por número de comprobante, cliente
  - Detalle de cada venta:
    - Productos vendidos
    - Cliente
    - Total
    - Método(s) de pago
    - Estado (completada, anulada, devuelta)
  
- 🎟️ **Comprobantes**
  - Boletas emitidas
  - Facturas emitidas
  - Número de comprobante
  - Estado (emitido, anulado)
  - Reimpresión de comprobantes
  
- 💳 **Pagos Múltiples**
  - Configurar pago mixto
  - Efectivo + Tarjeta
  - Efectivo + Yape/Plin
  - Calcular vuelto automáticamente
  - Referencia de operación digital
  
- 🎯 **Descuentos y Promociones**
  - Aplicar descuento manual (requiere permiso)
  - Descuentos automáticos por promociones
  - Descuentos por porcentaje o monto fijo
  - Descuentos por ítem o por venta completa
  
- ⏸️ **Suspender/Recuperar Venta**
  - Pausar venta en curso
  - Lista de ventas suspendidas
  - Recuperar venta suspendida
  - Identificador único por venta
  
- ↩️ **Devoluciones**
  - Buscar venta original
  - Seleccionar productos a devolver
  - Cantidad devuelta
  - Motivo de devolución
  - Método de reembolso
  - Generar nota de crédito
  
- 📊 **Reporte de Ventas del Día**
  - Total vendido
  - Cantidad de transacciones
  - Ticket promedio
  - Productos más vendidos
  - Ventas por método de pago
  - Ventas por hora
  
- 📋 **Lista de Pedidos**
  - Todos los pedidos del negocio
  - Filtros por:
    - Tipo (delivery, recojo en tienda, consumo en mesa, online)
    - Estado (pendiente, confirmado, en preparación, listo, en camino, entregado, cancelado)
    - Sede
    - Fecha
  - Vista de tarjetas con información resumida
  - Indicadores de tiempo (pedidos atrasados)
  
- ➕ **Crear Pedido Manual**
  - Tipo de pedido
  - Cliente
  - Sede
  - Productos y cantidades
  - **Para Delivery:**
    - Dirección de entrega
    - Zona de delivery
    - Costo de envío
    - Tiempo estimado
    - Repartidor asignado
  - **Para Mesas:**
    - Número de mesa
    - Cantidad de personas
  - Método de pago
  - Observaciones
  
- 🔍 **Detalle de Pedido**
  - Información completa del pedido
  - Cliente y datos de contacto
  - Productos ordenados
  - Subtotal, delivery, total
  - Estado actual
  - Historial de estados
  - Método de pago y estado del pago
  - Asignaciones (repartidor/mesero)
  - Tracking en tiempo real (para delivery)
  - Botones de acción según estado
  
- 🔄 **Cambiar Estado de Pedido**
  - Flujo de estados:
    - Pendiente → Confirmado → En Preparación → Listo → [En Camino] → Entregado
  - Botones de acción rápida
  - Notificaciones automáticas al cliente
  
- 🚚 **Gestión de Delivery**
  - Pedidos para delivery
  - Zonas de entrega
  - Asignar repartidor
  - Repartidores disponibles
  - Repartidores en ruta
  - Pedidos por repartidor
  - Tiempo promedio de entrega
  
- 👤 **Gestión de Repartidores**
  - Lista de repartidores
  - Estado (disponible, en ruta, descanso)
  - Crear/editar repartidor
  - Nombre, DNI, teléfono
  - Vehículo (moto, bicicleta, auto)
  - Pedidos asignados
  - Historial de entregas
  
- 🪑 **Gestión de Mesas**
  - Vista de mesas (plano visual)
  - Estado de cada mesa (disponible, ocupada, reservada)
  - Abrir cuenta de mesa
  - Agregar productos a mesa
  - Dividir cuenta
  - Transferir productos entre mesas
  - Solicitar cierre
  - Cerrar cuenta y cobrar
  
- 📦 **Pedidos para Recojo**
  - Lista de pedidos para recoger en tienda
  - Estado (listo/pendiente)
  - Notificar cliente cuando esté listo
  - Marcar como entregado
  
- ❌ **Cancelaciones de Pedidos**
  - Cancelar pedido
  - Motivo de cancelación
  - Gestionar reembolso
  - Devolver productos al inventario
  
- 📊 **Analítica de Pedidos**
  - Total de pedidos por periodo
  - Tasa de conversión
  - Tiempos promedio de preparación/entrega
  - Pedidos cancelados (análisis de motivos)
  - Productos más pedidos
  - Horarios pico
  
- 📋 **Lista de Devoluciones**
  - Todas las devoluciones
  - Estados (solicitada, aprobada, rechazada, completada)
  - Filtros por fecha, cliente, estado
  
- 📝 **Registrar Devolución**
  - Buscar venta original
  - Seleccionar productos a devolver
  - Cantidad
  - Motivo (producto defectuoso, cambio de opinión, error en pedido, etc.)
  - Tipo de solución (reembolso, cambio de producto, nota de crédito)
  - Observaciones
  - Adjuntar imágenes (opcional)
  
- 🔍 **Detalle de Devolución**
  - Información completa
  - Venta original
  - Productos devueltos
  - Cliente
  - Motivo
  - Estado actual
  - Responsable de la gestión
  - Historial de cambios
  
- ✅ **Aprobar/Rechazar Devolución**
  - Revisión de la solicitud
  - Comentarios
  - Acción: aprobar/rechazar
  
- 💵 **Gestionar Reembolso**
  - Método de reembolso (efectivo, transferencia, voucher)
  - Monto a reembolsar
  - Descuento de comisiones (si aplica)
  - Referencia de pago
  - Comprobante de reembolso
  
- 📦 **Reintegrar al Inventario**
  - Productos devueltos
  - Condición (nuevo, usado, dañado)
  - Almacén de destino
  - Cantidad a reintegrar
  - Ajuste automático de inventario

**Acciones:**
- Registrar venta
- Buscar producto
- Agregar al carrito
- Aplicar descuento
- Seleccionar cliente
- Emitir comprobante
- Procesar pago múltiple
- Verificar edad del cliente
- Suspender venta
- Recuperar venta
- Realizar devolución de venta
- Abrir caja
- Cerrar caja
- Registrar movimiento de caja
- Reimprimir comprobante
- Ver reportes de ventas
- Ver todos los pedidos
- Crear pedido manual
- Confirmar pedido
- Cambiar estado de pedido
- Asignar repartidor
- Ver tracking en tiempo real
- Gestionar mesas
- Abrir/cerrar cuenta de mesa
- Dividir cuenta
- Cancelar pedido
- Registrar devolución
- Aprobar/Rechazar devolución
- Gestionar reembolso
- Reintegrar productos al inventario
- Ver historial de devoluciones
- Exportar reporte de devoluciones

---

### � **MÓDULO 8: FACTURACIÓN ELECTRÓNICA (SUNAT)**
**Descripción:** Emisión y gestión de comprobantes electrónicos  
**RF:** RF-FIN-001 a RF-FIN-007

**Componentes/Vistas:**
- 📊 **Dashboard de Facturación**
  - Resumen de comprobantes emitidos
  - Estados (aceptados, rechazados, pendientes)
  - Gráficos de facturación
  
- 🎫 **Series de Comprobantes**
  - CRUD de series por sede
  - Tipo de documento (boleta, factura, nota de crédito, nota de débito)
  - Prefijo de serie (B001, F001, etc.)
  - Número correlativo actual
  - Estado (activa/inactiva)
  
- 📄 **Lista de Comprobantes**
  - Todos los documentos electrónicos
  - Filtros por:
    - Tipo de comprobante
    - Estado (emitido, aceptado, rechazado, anulado)
    - Fecha
    - Cliente
    - Serie
  
- 🔍 **Detalle de Comprobante**
  - Número completo (serie-correlativo)
  - Tipo de documento
  - Cliente
  - Fecha de emisión
  - Montos (subtotal, IGV, total)
  - Detalle de productos
  - Estado SUNAT
  - Código de respuesta SUNAT
  - Hash del documento
  - XML generado
  - PDF descargable
  - Historial de envíos
  
- 📤 **Enviar a SUNAT**
  - Generar XML del comprobante
  - Firmar digitalmente
  - Enviar a SUNAT
  - Recibir respuesta
  - Actualizar estado
  
- ❌ **Anular Comprobante**
  - Motivo de anulación
  - Generar comunicación de baja
  - Enviar a SUNAT
  
- 📋 **Notas de Crédito/Débito**
  - Crear nota de crédito (devoluciones, anulaciones, descuentos)
  - Crear nota de débito (intereses, penalidades)
  - Comprobante afectado
  - Tipo de nota
  - Motivo
  - Monto
  - Envío automático a SUNAT
  
- 🔄 **Reenvío de Comprobantes**
  - Comprobantes fallidos
  - Reintentar envío
  - Log de errores
  
- 🖨️ **Reimpresión**
  - Buscar comprobante
  - Visualizar PDF
  - Reimprimir
  - Enviar por email al cliente

**Acciones:**
- Gestionar series
- Ver lista de comprobantes
- Emitir comprobante (integrado con ventas)
- Enviar a SUNAT
- Consultar estado
- Anular comprobante
- Generar nota de crédito/débito
- Reimprimir comprobante
- Enviar comprobante por email
- Descargar XML/PDF

---

### 💼 **MÓDULO 9: GASTOS E INGRESOS**
**Descripción:** Control de gastos operativos y otros ingresos  
**RF:** RF-FIN-013 a RF-FIN-016, RF-VTA-010

**Componentes/Vistas:**
- 📋 **Lista de Gastos**
  - Todos los gastos registrados
  - Filtros por categoría, estado, fecha, sede
  - Búsqueda por concepto, proveedor
  
- ➕ **Registrar Gasto**
  - Categoría de gasto
  - Concepto/Descripción
  - Monto
  - Moneda
  - Fecha del gasto
  - Proveedor (opcional)
  - Método de pago
  - Sede
  - Comprobante (tipo y número)
  - Adjuntar imagen/PDF del comprobante
  - Es recurrente
  - Periodicidad (si es recurrente)
  - Estado (pendiente aprobación, aprobado, rechazado, pagado)
  
- 🏷️ **Categorías de Gasto**
  - CRUD de categorías
  - Tipo (gasto/ingreso)
  - Nombre
  - Descripción
  - Icono/Color
  - Es recurrente por defecto
  - Ejemplos: Alquiler, Servicios, Salarios, Mantenimiento, Marketing, etc.
  
- 🔍 **Detalle de Gasto**
  - Información completa
  - Historial de aprobaciones
  - Comprobante adjunto
  - Usuario que registró
  
- ✅ **Aprobar Gastos**
  - Lista de gastos pendientes de aprobación
  - Revisar y aprobar/rechazar
  - Comentarios
  
- 🔄 **Gastos Recurrentes**
  - Lista de gastos recurrentes configurados
  - Programar gasto automático
  - Frecuencia (mensual, semanal, anual)
  - Fecha de inicio/fin
  - Recordatorios
  
- 💰 **Caja Chica**
  - Saldo actual de caja chica
  - Movimientos de caja chica
  - Recargar caja chica
  - Gastos menores desde caja chica
  - Cuadre de caja chica
  
- 📊 **Resumen Financiero**
  - Total de ingresos (ventas)
  - Total de gastos
  - Utilidad neta
  - Gráficos por categoría
  - Comparativa por periodo

**Acciones:**
- Registrar gasto
- Editar gasto
- Aprobar/Rechazar gasto
- Ver detalle
- Gestionar categorías
- Configurar gastos recurrentes
- Gestionar caja chica
- Ver resumen financiero
- Exportar reportes

---

### 📊 **MÓDULO 10: REPORTES Y ANALÍTICA**
**Descripción:** Reportes avanzados y analítica del negocio  
**RF:** RF-ECO-018 a RF-ECO-025

**Componentes/Vistas:**
- 📈 **Dashboard de Reportes**
  - Acceso rápido a reportes frecuentes
  - Gráficos ejecutivos
  
- 💰 **Reporte de Ventas**
  - Ventas por periodo (día, semana, mes, año, personalizado)
  - Ventas por sede
  - Ventas por cajero
  - Ventas por método de pago
  - Ventas por tipo de comprobante
  - Comparativa de periodos
  - Gráficos de tendencias
  - Exportar a Excel, PDF
  
- 🏆 **Productos Más Vendidos**
  - Top 10/20/50 productos
  - Por cantidad vendida
  - Por ingresos generados
  - Por periodo
  - Por categoría
  - Gráficos
  
- 📦 **Reporte de Inventario**
  - Inventario actual por producto
  - Inventario por almacén
  - Inventario por sede
  - Valorización del inventario
  - Stock bajo/crítico
  - Productos sin movimiento
  
- 🔄 **Kardex de Movimientos**
  - Movimientos por producto
  - Movimientos por almacén
  - Movimientos por tipo
  - Filtros por fecha
  - Trazabilidad completa
  
- 🛒 **Reporte de Compras**
  - Compras por periodo
  - Compras por proveedor
  - Productos más comprados
  - Análisis de costos
  
- 💼 **Reporte de Gastos**
  - Gastos por periodo
  - Gastos por categoría
  - Gastos por sede
  - Comparativa de periodos
  
- 📄 **Comprobantes Emitidos**
  - Boletas emitidas
  - Facturas emitidas
  - Por periodo
  - Por serie
  - Resumen SUNAT
  
- 💵 **Reporte Financiero**
  - Estado de resultados
  - Ingresos vs Gastos
  - Utilidad neta
  - Márgenes de ganancia
  - Flujo de caja
  
- 👥 **Reporte de Clientes**
  - Clientes frecuentes
  - Clientes nuevos
  - Ticket promedio por cliente
  - Compras recurrentes
  
- 🚚 **Reporte de Pedidos**
  - Pedidos por modalidad (delivery, recojo, mesa)
  - Tiempos de entrega promedio
  - Pedidos cancelados
  - Análisis de horarios pico
  
- 📊 **Reportes Personalizados**
  - Constructor de reportes
  - Seleccionar campos
  - Agregar filtros
  - Guardar plantillas
  - Programar envío automático

**Acciones:**
- Generar reporte
- Seleccionar periodo
- Aplicar filtros
- Exportar a Excel
- Exportar a PDF
- Programar reporte automático
- Enviar por email
- Guardar favoritos

---

<a name="resumen"></a>
## 📊 RESUMEN COMPARATIVO

### 📈 CANTIDAD DE MÓDULOS

| ROL | CANTIDAD | ENFOQUE |
|-----|----------|---------|
| **SUPERADMIN** | **6 módulos** | Gestión de plataforma SaaS, negocios y facturación de suscripciones |
| **ADMIN** | **10 módulos** | Gestión completa del negocio y operaciones diarias |

---

### 🔑 DIFERENCIAS CLAVE

#### SUPERADMIN (Plataforma)
- ✅ Gestión multi-tenant
- ✅ Control de planes de suscripción
- ✅ Facturación recurrente
- ✅ Monitoreo global
- ✅ Configuración centralizada
- ✅ Auditoría de plataforma
- ❌ No gestiona operaciones de negocio individual

#### ADMIN (Negocio)
- ✅ Operaciones completas del negocio
- ✅ Gestión de inventario y ventas
- ✅ Control de sedes y almacenes
- ✅ POS y facturación electrónica
- ✅ Tienda online y pedidos
- ✅ Reportes operativos
- ❌ No gestiona otros negocios
- ❌ No modifica su plan de suscripción (solo visualiza)

---

### 🎯 MÓDULOS COMPARTIDOS (CON DIFERENTE ALCANCE)

| MÓDULO | SUPERADMIN | ADMIN |
|--------|------------|-------|
| **Dashboard** | Métricas de toda la plataforma | Métricas del negocio propio |
| **Auditoría** | Logs de todos los negocios | Logs del negocio propio |
| **Configuración** | Parámetros globales de plataforma | Parámetros del negocio |

---

### 🚀 MÓDULOS EXCLUSIVOS DE ADMIN

1. Configuración del Negocio, Sedes y StoreFront
2. Seguridad, Usuarios y Clientes
3. Catálogo de Productos, Descuentos y Promociones
4. Gestión de Inventario
5. Proveedores y Compras
6. Ventas, Punto de Venta (POS) y Pedidos
7. Facturación Electrónica (SUNAT)
8. Gastos e Ingresos
9. Reportes y Analítica

---

## 📝 NOTAS PARA DESARROLLO FRONTEND

### 🎨 **Tecnologías Sugeridas**
- **Framework:** React o Vue.js
- **UI Library:** Material-UI, Ant Design, Chakra UI
- **State Management:** Redux, Zustand, Pinia
- **Routing:** React Router / Vue Router
- **API Client:** Axios
- **Autenticación:** JWT con interceptores
- **Gráficos:** Chart.js, Recharts, ApexCharts

### 🔐 **Control de Acceso**
- Implementar guards de ruta por rol
- Verificar permisos a nivel de componente
- Ocultar/deshabilitar acciones según permisos
- Validar plan de suscripción para funcionalidades premium
- **SuperAdmin:** Única entidad que puede crear/desactivar sedes
- **Admin:** Solo configura sedes existentes creadas por SuperAdmin

### 📱 **Responsive Design**
- Mobile-first para POS
- Responsive para módulos administrativos
- Considerar tablets para inventario
- PWA para acceso offline

### 🎯 **Priorización de Desarrollo**

**FASE 1 - MVP (SuperAdmin):**
1. Dashboard Plataforma
2. Gestión de Negocios
3. Planes de Suscripción

**FASE 2 - MVP (Admin):**
1. Dashboard Negocio
2. Catálogo de Productos, Descuentos y Promociones
3. Gestión de Inventario
4. Ventas, Punto de Venta (POS) y Pedidos

**FASE 3 - Extensión:**
1. Configuración del Negocio, Sedes y StoreFront
2. Seguridad, Usuarios y Clientes
3. Proveedores y Compras
4. Facturación Electrónica (SUNAT)

**FASE 4 - Avanzado:**
1. Gastos e Ingresos
2. Reportes y Analítica

---

## ✅ CONCLUSIÓN

Esta estructura modular proporciona una separación clara entre:
- **Gestión de Plataforma (SuperAdmin):** 6 módulos para administrar el SaaS
- **Gestión de Negocio (Admin):** 10 módulos para operar la licorería

Cada módulo tiene responsabilidades bien definidas y está alineado con los requerimientos funcionales del sistema. La consolidación de módulos relacionados facilita el desarrollo, reduce la complejidad de navegación y mejora la experiencia del usuario, manteniendo todas las funcionalidades requeridas.

---

**Documento creado para:** DrinkGo - Sistema Multi-Tenant para Licorerías  
**Fecha:** 24 de Febrero, 2026  
**Versión:** 1.0

