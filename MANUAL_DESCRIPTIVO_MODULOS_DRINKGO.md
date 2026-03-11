# DrinkGo: Aplicación Informática Distribuida para Negocios de Licorería

## Manual Descriptivo de Módulos

### Configuración · Usuarios y Clientes · Reportes · Dashboard · Visión de Cliente

**Equipo de Desarrollo — DrinkGo**

---

## ÍNDICE

1. [Visión General del Sistema](#1-visión-general-del-sistema)
2. [Módulo de Dashboard del Negocio](#2-módulo-de-dashboard-del-negocio)
3. [Módulo de Configuración del Negocio](#3-módulo-de-configuración-del-negocio)
   - 3.1 [Mi Negocio](#31-mi-negocio)
   - 3.2 [Gestión de Sedes](#32-gestión-de-sedes)
   - 3.3 [Tienda Online (StoreFront)](#33-tienda-online-storefront)
   - 3.4 [Almacenes](#34-almacenes)
   - 3.5 [Métodos de Pago](#35-métodos-de-pago)
   - 3.6 [Zonas de Delivery](#36-zonas-de-delivery)
   - 3.7 [Mesas](#37-mesas)
   - 3.8 [Cajas Registradoras](#38-cajas-registradoras)
   - 3.9 [Categorías de Gastos](#39-categorías-de-gastos)
4. [Módulo de Seguridad, Usuarios y Clientes](#4-módulo-de-seguridad-usuarios-y-clientes)
   - 4.1 [Gestión de Usuarios del Negocio](#41-gestión-de-usuarios-del-negocio)
   - 4.2 [Gestión de Clientes](#42-gestión-de-clientes)
   - 4.3 [Gestión de Roles y Permisos](#43-gestión-de-roles-y-permisos)
5. [Módulo de Reportes y Analíticas](#5-módulo-de-reportes-y-analíticas)
   - 5.1 [Reporte de Ventas](#51-reporte-de-ventas)
   - 5.2 [Reporte de Productos Más Vendidos](#52-reporte-de-productos-más-vendidos)
   - 5.3 [Reporte de Inventario](#53-reporte-de-inventario)
   - 5.4 [Reporte de Movimientos de Inventario](#54-reporte-de-movimientos-de-inventario)
   - 5.5 [Reporte de Compras y Gastos](#55-reporte-de-compras-y-gastos)
   - 5.6 [Reporte de Comprobantes Electrónicos](#56-reporte-de-comprobantes-electrónicos)
6. [Visión de Cliente — Tienda Online (StoreFront)](#6-visión-de-cliente--tienda-online-storefront)
   - 6.1 [Página Principal](#61-página-principal)
   - 6.2 [Registrarse](#62-registrarse)
   - 6.3 [Iniciar Sesión](#63-iniciar-sesión)
   - 6.4 [Catálogo de Productos](#64-catálogo-de-productos)
   - 6.5 [Detalle de Producto](#65-detalle-de-producto)
   - 6.6 [Carrito de Compras](#66-carrito-de-compras)
   - 6.7 [Proceso de Compra (Checkout)](#67-proceso-de-compra-checkout)
   - 6.8 [Mis Pedidos](#68-mis-pedidos)
   - 6.9 [Mi Perfil](#69-mi-perfil)
   - 6.10 [Mis Devoluciones](#610-mis-devoluciones)
7. [Opciones Configurables del Sistema](#7-opciones-configurables-del-sistema)
8. [Resumen de Funcionalidades](#8-resumen-de-funcionalidades)

---

## 1. VISIÓN GENERAL DEL SISTEMA

**DrinkGo** es una plataforma web SaaS (Software como Servicio) multi-tenant diseñada específicamente para la gestión integral de licorerías. El sistema opera bajo tres ámbitos principales:

| Ámbito | Descripción | Acceso |
|--------|-------------|--------|
| **Panel SuperAdmin** | Gestión centralizada de toda la plataforma, negocios, planes y facturación | `/superadmin` |
| **Panel Admin** | Gestión completa de cada licorería: catálogo, inventario, ventas, reportes y configuración | `/admin` |
| **Tienda Online (StoreFront)** | Portal público de e-commerce para clientes finales | `/tienda/{slug}` |

Los módulos que se describen en este documento corresponden a los que el **Administrador del negocio** y el **Cliente final** utilizan en su operación diaria: Configuración, Usuarios y Clientes, Reportes, Dashboard y la Visión de Cliente.

---

## 2. MÓDULO DE DASHBOARD DEL NEGOCIO

El Dashboard es la primera pantalla que visualiza el Administrador al ingresar al sistema. Funciona como un **panel ejecutivo en tiempo real** que centraliza las métricas más importantes del negocio en un solo vistazo, permitiendo tomar decisiones informadas de manera inmediata.

### 2.1 Tarjetas de Indicadores Clave (KPIs)

El Dashboard presenta **6 tarjetas de indicadores clave** organizadas en dos filas:

**Primera fila:**

| Indicador | Descripción | Detalle |
|-----------|-------------|---------|
| **Ventas del Mes** | Muestra el monto total facturado en el mes actual en formato de moneda (S/) | Incluye una flecha de tendencia (verde hacia arriba o roja hacia abajo) con el porcentaje de variación respecto al mes anterior |
| **Clientes Nuevos** | Cuenta la cantidad de clientes registrados durante el mes en curso | Permite al administrador evaluar el crecimiento de su cartera de clientes |
| **Productos con Stock Bajo** | Indica cuántos productos se encuentran por debajo del stock mínimo configurado | Incluye alertas de lotes vencidos y lotes próximos a vencer (dentro de 30 días) |

**Segunda fila:**

| Indicador | Descripción | Detalle |
|-----------|-------------|---------|
| **Pedidos del Mes** | Muestra la cantidad total de órdenes de compra realizadas en el mes | Incluye tendencia porcentual vs. el mes anterior |
| **Devoluciones Pendientes** | Contabiliza las devoluciones en estado pendiente, en proceso o recibido | Alerta al administrador sobre devoluciones que requieren atención |
| **Valor del Inventario** | Presenta el valor total del inventario calculado como cantidad × costo promedio por producto | Permite conocer el capital invertido en mercancía |

### 2.2 Gráficos Visuales

El Dashboard incorpora **3 gráficos interactivos** que facilitan la comprensión visual de los datos:

**Gráfico de Líneas — Ventas de los Últimos 30 Días:**
- Eje X: Fechas de los últimos 30 días
- Eje Y: Monto total vendido por día en soles (S/)
- Color: Línea verde con puntos de datos
- Al pasar el cursor, se muestra un tooltip con la fecha exacta y el monto formateado
- Permite identificar rápidamente tendencias de venta, picos y caídas

**Gráfico de Dona — Pedidos por Estado:**
- Presenta todos los pedidos agrupados por su estado actual
- Estados representados con colores distintivos:
  - 🟡 Pendiente (Ámbar)
  - 🔵 Confirmado (Azul)
  - 🟣 Preparando (Púrpura)
  - 🔵 En camino (Cian)
  - 🟢 Entregado (Verde)
  - 🔴 Cancelado (Rojo)
- Incluye leyenda con la cantidad por cada estado

**Tabla — Top 5 Productos Más Vendidos:**
- Muestra los 5 productos con mayor cantidad de unidades vendidas en el mes
- Columnas: Posición (#), Nombre del Producto, SKU, Unidades Vendidas
- Se calcula a partir de todas las ventas completadas del mes actual

### 2.3 Tablas de Datos Operativos

El Dashboard también presenta **4 tablas informativas** con datos operativos críticos:

| Tabla | Contenido | Cantidad |
|-------|-----------|----------|
| **Pedidos Recientes** | Los 5 pedidos más recientes ordenados por fecha de creación descendente | 5 registros |
| **Alertas de Stock** | Productos cuyo stock disponible es menor o igual al mínimo configurado | Hasta 5 alertas |
| **Lotes Vencidos** | Lotes de inventario cuya fecha de vencimiento ya pasó y aún tienen cantidad disponible | Hasta 5 lotes |
| **Lotes Próximos a Vencer** | Lotes que vencerán en los próximos 30 días y aún tienen stock | Hasta 5 lotes |

### 2.4 Controles del Dashboard

- **Botón de Actualización Manual:** Permite refrescar todos los datos del dashboard con un solo clic. Muestra un ícono de carga (spinner) mientras se actualizan los datos.
- **Contexto del Negocio:** Todos los datos mostrados corresponden exclusivamente al negocio del usuario autenticado, garantizando el aislamiento multi-tenant.
- **Periodo de Referencia:** Los KPIs se calculan sobre el mes calendario actual y se comparan automáticamente con el mes anterior.

---

## 3. MÓDULO DE CONFIGURACIÓN DEL NEGOCIO

El módulo de Configuración es el centro neurálgico donde el Administrador define **cómo opera su licorería**. Desde aquí se controlan los datos del negocio, las sedes, la tienda online, los almacenes, los métodos de pago, las zonas de delivery, las mesas, las cajas registradoras y las categorías de gastos.

El módulo se organiza en **4 secciones principales** con sus respectivas pestañas:

```
Configuración
├── Negocio y Sedes
│   ├── Mi Negocio
│   ├── Sedes
│   └── Tienda Online
├── Almacenes
├── Operaciones
│   ├── Métodos de Pago
│   ├── Zonas de Delivery
│   └── Mesas
└── Categorías de Gastos
└── Cajas Registradoras
```

---

### 3.1 Mi Negocio

Esta pestaña permite visualizar y editar la **información institucional del negocio**. Es el primer apartado que el administrador debe configurar al iniciar su operación en DrinkGo.

**Información que se muestra al ingresar:**
- Una insignia de estado del negocio (Activo, Suspendido o Pendiente)
- 4 tarjetas resumen: Razón Social, RUC, Ciudad e IGV (%)

**Campos editables del negocio:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Razón Social | Texto (obligatorio) | Nombre legal de la empresa registrado en SUNAT |
| Nombre Comercial | Texto | Nombre de marca con el que opera el negocio |
| RUC | Texto (11 dígitos) | Registro Único del Contribuyente. Cuenta con **auto-búsqueda en SUNAT**: al ingresar el RUC, el sistema consulta automáticamente los datos de la empresa |
| Tipo de Documento Fiscal | Desplegable | Opciones: RUC, DNI, CE, Otro |
| Representante Legal | Texto | Nombre completo del representante, con auto-búsqueda por DNI en RENIEC |
| Documento del Representante | Texto | Número del documento del representante legal |
| Tipo de Negocio | Desplegable | Licorería, Distribuidora, Premium, Bodega u Otro |
| Email | Texto | Correo electrónico del negocio |
| Teléfono | Texto | Número de contacto del negocio |
| Dirección | Texto | Dirección fiscal completa |
| Ciudad | Texto | Ciudad donde opera |
| Departamento | Texto | Departamento o región |
| País | Texto | País (por defecto: PE - Perú) |
| URL del Logo | Texto | Enlace a la imagen del logotipo del negocio |
| Aplicar IGV | Interruptor (On/Off) | Activa o desactiva la aplicación del Impuesto General a las Ventas |
| Porcentaje de IGV | Número | Porcentaje del IGV (por defecto: 18%) |

**Operaciones soportadas:** Solo actualización (el negocio se crea desde el panel SuperAdmin).

**Funcionalidad destacada — Consulta SUNAT/RENIEC:**
Al ingresar el RUC del negocio, el sistema realiza una consulta automática a la base de datos de SUNAT y pre-llena automáticamente los campos de razón social, nombre comercial y dirección fiscal. De igual forma, al ingresar el DNI del representante legal, se consulta RENIEC para auto-llenar el nombre.

---

### 3.2 Gestión de Sedes

Esta pestaña permite administrar las **sucursales o locales** del negocio. Cada sede representa un punto físico de operación con su propia configuración independiente.

**Vista principal:**
- 4 tarjetas estadísticas: Total de sedes, Sedes activas, Sedes con delivery y Sedes con recojo en tienda
- Tabla con buscador y paginación (10 registros por página)
- Buscador por nombre, código o ciudad (con un retraso de 400ms para optimizar búsquedas)

**Campos de cada sede:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Nombre | Texto (obligatorio) | Nombre identificador de la sede (ej: "Sede Principal", "Sucursal Miraflores") |
| Dirección | Texto (obligatorio) | Dirección completa del local |
| Ciudad | Texto | Ciudad donde se ubica |
| Departamento | Texto | Departamento o región |
| País | Texto | País (por defecto: PE) |
| Código Postal | Texto | Código postal de la zona |
| Teléfono | Texto | Número de contacto de la sede |
| Es Principal | Interruptor | Marca esta sede como la sede principal del negocio |
| Delivery Habilitado | Interruptor | Activa la opción de entrega a domicilio desde esta sede |
| Recojo Habilitado | Interruptor | Activa la opción de recojo en tienda para esta sede |
| Maneja Mesas | Interruptor | Habilita la funcionalidad de mesas/atención en local para esta sede |

**Columnas de la tabla:**

| Columna | Descripción |
|---------|-------------|
| # | Número de orden |
| Código | Código auto-generado por el sistema |
| Nombre + Dirección | Se muestra en dos líneas |
| Ciudad | Ciudad de la sede |
| Servicios | Insignias visuales: 🚚 Delivery y 🛍️ Recojo |
| Estado | Insignia de Activo o Inactivo |
| Acciones | Botones de Editar (✏️) y Eliminar (🗑️) |

**Operaciones soportadas:** Crear, Leer, Actualizar y Desactivar (eliminación lógica).

**Nota importante:** La cantidad de sedes que se pueden crear está limitada por el plan de suscripción contratado. Si se alcanza el límite, el sistema mostrará una restricción.

---

### 3.3 Tienda Online (StoreFront)

Esta pestaña permite configurar la **tienda en línea pública** del negocio, que es el portal e-commerce donde los clientes finales pueden explorar productos, armar su carrito y realizar pedidos.

**Campos de configuración:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Está Habilitado | Interruptor (maestro) | **Interruptor principal** que activa o desactiva toda la tienda online. Al desactivarla, todos los demás campos se muestran transparentes (opacity-50) y la tienda deja de ser accesible para los clientes |
| Nombre de la Tienda | Texto | Nombre que aparecerá en la tienda online |
| Slug de la Tienda | Texto | Identificador amigable para la URL (se auto-genera a partir del nombre). Ejemplo: `mi-licoreria` |
| Dominio Personalizado | Texto | Dominio propio si se desea (ej: `www.milicoreria.com`) |
| Color Primario | Selector de color + código hexadecimal | Color principal de la marca para la tienda |
| Color Secundario | Selector de color + código hexadecimal | Color secundario complementario |
| Mensaje de Bienvenida | Área de texto | Mensaje que se muestra en la página principal de la tienda |

**Vista previa de URL:** El sistema muestra en tiempo real la URL resultante: `drinkgo.com/{slug}`

**Comportamiento:**
- Cuando la tienda está **deshabilitada**, todos los campos del formulario se atenúan visualmente y la tienda no es accesible públicamente.
- Cuando la tienda está **habilitada**, los clientes pueden acceder a `drinkgo.com/{slug}` y visualizar el catálogo, registrarse, iniciar sesión y realizar pedidos.
- La configuración visual (colores y mensaje de bienvenida) se almacena como JSON.

**Operaciones soportadas:** Crear y Actualizar la configuración.

---

### 3.4 Almacenes

Esta sección permite gestionar los **almacenes o depósitos** de cada sede. Cada almacén es un espacio físico donde se guarda la mercancía y desde donde se despachan los productos.

**Vista principal:**
- 3 tarjetas estadísticas: Total de almacenes, Almacenes activos, Almacenes predeterminados
- Tabla con buscador y paginación (10 registros por página)
- Buscador por código, nombre o nombre de la sede

**Campos de cada almacén:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Código | Texto (obligatorio) | Código identificador único del almacén. **No se puede modificar después de creado** |
| Nombre | Texto (obligatorio) | Nombre descriptivo (ej: "Almacén General", "Refrigerado", "Almacén de Bebidas Premium") |
| Descripción | Área de texto | Descripción detallada del almacén (opcional) |
| Sede | Desplegable (obligatorio) | Sede a la que pertenece el almacén |
| Es Predeterminado | Interruptor | Marca este almacén como el predeterminado para las operaciones de inventario de la sede |

**Columnas de la tabla:**

| Columna | Descripción |
|---------|-------------|
| # | Número de orden |
| Código | Código del almacén en formato monoespaciado |
| Nombre + Descripción | Se muestra en dos líneas |
| Sede | Sede a la que pertenece |
| Predet. | Ícono ⭐ si es predeterminado |
| Estado | Insignia de Activo o Inactivo |
| Acciones | Botones de Editar y Eliminar |

**Operaciones soportadas:** Crear, Leer, Actualizar y Eliminar.

---

### 3.5 Métodos de Pago

Esta pestaña permite configurar los **métodos de pago aceptados** por el negocio, tanto para el punto de venta físico (POS) como para la tienda online.

**Vista principal:**
- 4 tarjetas estadísticas: Total de métodos, Métodos activos, Disponibles en POS, Disponibles en tienda online
- Tabla con buscador y paginación

**Tipos de método de pago disponibles:**

| Tipo | Color de insignia | Descripción |
|------|-------------------|-------------|
| Efectivo | Verde | Pago en efectivo en el local |
| Tarjeta Crédito | Azul | Tarjeta de crédito (Visa, Mastercard, etc.) |
| Tarjeta Débito | Azul | Tarjeta de débito |
| Transferencia Bancaria | Amarillo | Depósito o transferencia entre cuentas |
| Billetera Digital | Azul | Billeteras electrónicas genéricas |
| Yape | Azul | Billetera digital Yape (BCP) |
| Plin | Azul | Billetera digital Plin (multibancos) |
| QR | Amarillo | Pagos mediante código QR |
| Otro | Azul | Cualquier otro método personalizado |

**Campos de cada método de pago:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Nombre | Texto (obligatorio) | Nombre del método de pago (ej: "Efectivo", "Yape") |
| Código | Texto (obligatorio) | Código identificador único, se auto-genera como slug. **No editable después de creado** |
| Tipo | Desplegable (obligatorio) | Uno de los 9 tipos listados arriba |
| Disponible en POS | Interruptor | Habilita este método para ventas en el local físico |
| Disponible en Tienda Online | Interruptor | Habilita este método para ventas por la tienda web |
| Orden | Número | Posición de visualización (el sistema detecta conflictos si se repite un número de orden) |

**Funcionalidad inteligente de creación:**
Al crear un nuevo método de pago, el sistema presenta un **desplegable con 7 presets** predefinidos (Efectivo, Tarjeta, Yape, Plin, QR, Transferencia, Otro). Los presets que ya han sido agregados se filtran automáticamente para evitar duplicados. Si se selecciona "Otro", se habilitan campos para ingresar un nombre y código personalizado. El número de orden se auto-llena con el siguiente disponible.

**Columnas de la tabla:**

| Columna | Descripción |
|---------|-------------|
| # | Número de orden |
| Nombre + Código | Nombre del método y su código en formato monoespaciado |
| Tipo | Insignia con color según el tipo |
| Canales | Insignias múltiples: 🖥️ POS y/o 🛒 Online |
| Orden | Número de posición |
| Estado | Activo o Inactivo |
| Acciones | Editar y Eliminar |

**Operaciones soportadas:** Crear, Leer, Actualizar y Eliminar.

**Sincronización:** Los cambios en métodos de pago se sincronizan automáticamente con el módulo de Punto de Venta (POS) mediante caché compartida.

---

### 3.6 Zonas de Delivery

Esta pestaña permite definir las **zonas geográficas de cobertura** para el servicio de delivery del negocio, incluyendo las tarifas y montos mínimos de pedido por zona.

**Vista principal:**
- 3 tarjetas estadísticas: Total de zonas, Zonas activas, Tarifa promedio de delivery
- Tabla con buscador y paginación

**Campos de cada zona de delivery:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Nombre | Texto (obligatorio) | Nombre identificativo de la zona (ej: "Zona Centro", "Lima Sur") |
| Sede | Desplegable (obligatorio) | Sede desde la cual se despachan los pedidos de esta zona |
| Distritos | Multi-selección | Componente especial que permite seleccionar múltiples distritos de Lima (43 distritos disponibles). Se escriben para filtrar y se agregan como etiquetas verdes |
| Descripción | Área de texto | Descripción de la zona de cobertura (opcional) |
| Tarifa de Delivery | Número en S/ | Costo de envío que paga el cliente por pedidos en esta zona |
| Monto Mínimo de Pedido | Número en S/ | Monto mínimo que debe tener un pedido para calificar en esta zona |

**Componente especial — Selector de Distritos (MultiDistritoInput):**
- Incluye los 43 distritos de Lima Metropolitana
- Se escribe el nombre para filtrar las sugerencias
- Se presiona Enter o el botón "Agregar" para añadir un distrito
- Los distritos seleccionados se muestran como chips/etiquetas verdes
- Se pueden eliminar individualmente haciendo clic en la X del chip

**Componente especial — Calculadora de Monto Mínimo (CalculadoraMontoMinimo):**
Es una herramienta integrada que ayuda al administrador a calcular el monto mínimo ideal para sus zonas de delivery. Funciona así:
1. El administrador ingresa: costo de gasolina, tiempo de delivery (minutos), pago al motorista (S/ por hora) y margen deseado (%)
2. La calculadora aplica la fórmula: `montoMin = costoBase × 3.5 × (1 + margen%)`
3. Muestra un desglose detallado de los costos
4. Ofrece un botón "Usar este monto" que auto-llena el campo de monto mínimo
5. Incluye una explicación de por qué este mínimo evita pérdidas

**Operaciones soportadas:** Crear, Leer, Actualizar y Eliminar.

---

### 3.7 Mesas

Esta pestaña permite configurar las **mesas o espacios de atención** en cada sede del negocio que tenga habilitada esta funcionalidad.

**Requisito previo:** Solo aparecen las sedes que tienen activado el interruptor "Maneja Mesas" en la configuración de sedes. Si ninguna sede lo tiene activo, se muestra un mensaje indicando que no hay sedes configuradas para mesas.

**Vista principal:**
- Pestañas por cada sede habilitada (si hay múltiples sedes con mesas)
- 4 tarjetas estadísticas por sede: Total de mesas, Disponibles, Ocupadas, Reservadas
- Tabla con buscador y paginación

**Estados posibles de una mesa:**

| Estado | Color | Descripción |
|--------|-------|-------------|
| Disponible | Verde | Mesa lista para ser asignada a un cliente |
| Ocupada | Amarillo | Mesa actualmente en uso |
| Reservada | Azul | Mesa apartada para una futura asignación |
| Mantenimiento | Rojo | Mesa temporalmente fuera de servicio |

**Campos de cada mesa:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Nombre | Texto (obligatorio) | Nombre o número de la mesa (ej: "Mesa 1", "Terraza A") |
| Sede | Desplegable (obligatorio) | Solo muestra sedes con mesas habilitadas |
| Capacidad | Número (obligatorio) | Cantidad de personas que pueden sentarse |
| Estado | Desplegable (obligatorio) | Uno de los 4 estados listados arriba |

**Columnas de la tabla:**

| Columna | Descripción |
|---------|-------------|
| # | Número de orden |
| Nombre | Nombre de la mesa |
| Capacidad | Ícono 👥 + cantidad de personas |
| Estado | Insignia con color según el estado |
| Acciones | Botones de Editar y Eliminar |

**Operaciones soportadas:** Crear, Leer, Actualizar y Eliminar.

---

### 3.8 Cajas Registradoras

Esta sección permite configurar las **cajas registradoras** del punto de venta en cada sede.

**Vista principal:**
- 3 tarjetas estadísticas: Total de cajas, Cajas activas, Monto de apertura promedio
- Tabla con buscador y paginación

**Campos de cada caja registradora:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Código | Texto (obligatorio) | Código identificador de la caja. **No editable después de creado** |
| Nombre | Texto (obligatorio) | Nombre descriptivo (ej: "Caja Principal", "Caja Express") |
| Sede | Desplegable (obligatorio) | Sede donde se encuentra la caja |
| Monto de Apertura por Defecto | Número en S/ | Monto inicial en efectivo sugerido al abrir la caja cada día |

**Operaciones soportadas:** Crear, Leer, Actualizar y Eliminar.

---

### 3.9 Categorías de Gastos

Esta sección permite organizar los **tipos de gastos operativos** del negocio para un mejor control financiero y generación de reportes.

**Tipos de categoría disponibles:**

| Tipo | Color | Ejemplo de uso |
|------|-------|----------------|
| Operativo | Azul | Combustible, compras menores, insumos |
| Administrativo | Amarillo | Alquiler, servicios contables, licencias |
| Servicio | Verde | Agua, luz, internet, telefonía |
| Personal | Azul | Sueldos, bonificaciones, gratificaciones |
| Marketing | Amarillo | Publicidad, redes sociales, volantes |
| Mantenimiento | Azul | Reparaciones, limpieza, equipamiento |
| Tecnología | Azul | Software, hosting, equipos de cómputo |
| Otro | Azul | Cualquier gasto que no encaje en las anteriores |

**Campos de cada categoría:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Nombre | Texto (obligatorio) | Nombre de la categoría de gasto |
| Tipo | Desplegable (obligatorio) | Uno de los 8 tipos listados arriba |
| Descripción | Área de texto | Descripción de la categoría (máximo 300 caracteres) |

**Operaciones soportadas:** Crear, Leer, Actualizar y Eliminar.

---

## 4. MÓDULO DE SEGURIDAD, USUARIOS Y CLIENTES

Este módulo gestiona todo lo relacionado con la **seguridad de acceso, las cuentas de usuario, los roles y permisos, y la base de datos de clientes** del negocio. Se organiza en 3 pestañas principales:

```
Usuarios y Clientes
├── Usuarios (personal del negocio)
├── Clientes (compradores)
└── Roles y Permisos
```

---

### 4.1 Gestión de Usuarios del Negocio

Esta pestaña permite administrar los **empleados y operadores** del negocio: administradores, cajeros, inventaristas y demás personal que accede al panel de administración.

**Vista principal:**
- Tabla de usuarios con buscador y paginación (10 registros por página)
- Búsqueda por nombre, email o número de documento (con retraso de 400ms)
- Botón "Nuevo usuario" para abrir el formulario de creación

**Campos del formulario de usuario:**

| Campo | Tipo | Validación | Descripción |
|-------|------|------------|-------------|
| Nombres | Texto (obligatorio) | Mínimo 2 caracteres | Nombres del empleado |
| Apellidos | Texto (obligatorio) | Mínimo 2 caracteres | Apellidos del empleado |
| Email | Texto (obligatorio) | Formato de email válido | Correo electrónico (se usa para iniciar sesión) |
| Tipo de Documento | Desplegable (obligatorio) | — | DNI, CE (Carnet de Extranjería), Pasaporte u Otro |
| Número de Documento | Texto (obligatorio) | DNI: 8 dígitos, CE: 9 alfanuméricos, Pasaporte: mínimo 6 caracteres | Número del documento de identidad |
| Teléfono | Texto (opcional) | 9 dígitos, empieza con 9 | Número de celular |
| Rol | Desplegable (opcional) | — | Rol del sistema que se asignará al usuario |
| Sede | Desplegable (opcional) | — | Sede predeterminada donde operará el usuario |
| Contraseña | Texto (obligatorio al crear) | Mínimo 6 caracteres | Contraseña de acceso. En modo edición se muestra como "Nueva contraseña (opcional)" |
| Confirmar Contraseña | Texto | Debe coincidir con la contraseña | Confirmación de la contraseña |
| Estado | Casilla de verificación | — | Activo o Inactivo |

**Información que se muestra en la tabla:**

| Columna | Descripción |
|---------|-------------|
| Nombre Completo | Nombres + Apellidos |
| Email | Correo electrónico |
| Tipo y Número de Documento | Ej: DNI - 12345678 |
| Teléfono | Número de celular |
| Estado | Insignia de Activo o Inactivo |
| Último Acceso | Fecha y hora del último inicio de sesión |
| Acciones | Editar y Desactivar |

**Flujo de creación de usuario:**
1. El administrador hace clic en "Nuevo usuario"
2. Se abre el formulario modal
3. Se ingresan los datos personales y de acceso
4. Se selecciona opcionalmente un rol y una sede
5. Al guardar, el sistema crea el usuario, asigna el rol seleccionado y la sede predeterminada
6. Se muestra una notificación de éxito y la tabla se actualiza

**Operaciones soportadas:** Crear, Leer, Actualizar y Desactivar (eliminación lógica que preserva el historial).

---

### 4.2 Gestión de Clientes

Esta pestaña permite administrar la **base de datos de clientes** del negocio: las personas o empresas que compran productos, ya sea en el local físico o a través de la tienda online.

**Vista principal:**
- Tabla de clientes con buscador y paginación (10 registros por página)
- Búsqueda por nombre, razón social, email, teléfono o número de documento
- Botón "Nuevo cliente" para abrir el formulario

**Campos del formulario de cliente (persona natural):**

| Campo | Tipo | Validación | Descripción |
|-------|------|------------|-------------|
| Tipo de Documento | Desplegable (obligatorio) | — | DNI, RUC, CE, Pasaporte u Otro |
| Número de Documento | Texto (obligatorio) | DNI: 8 dígitos, RUC: 11 dígitos, 6-20 caracteres | Con botón de **auto-búsqueda** |
| Nombres | Texto | — | Se auto-llena al consultar DNI en RENIEC |
| Apellidos | Texto | — | Se auto-llena al consultar DNI en RENIEC |
| Fecha de Nacimiento | Selector de fecha | **Edad ≥ 18 años obligatorio** | Para validación de edad en venta de alcohol |
| Email | Texto (opcional) | Formato válido | Correo del cliente |
| Teléfono | Texto (opcional) | 9 dígitos, empieza con 9 | Celular del cliente |
| Dirección | Texto (opcional) | — | Dirección de entrega |
| Contraseña | Texto (opcional) | Mínimo 6 caracteres | Para crear cuenta en la tienda online |
| Confirmar Contraseña | Texto | Debe coincidir | Confirmación |

**Campos adicionales para clientes empresa (RUC):**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Razón Social | Texto | Se auto-llena al consultar RUC en SUNAT |
| Nombre Comercial | Texto | Se auto-llena al consultar RUC en SUNAT |

**Funcionalidad de Auto-Búsqueda de Documentos:**

- **Consulta DNI (RENIEC):** Al ingresar un número de DNI y presionar el botón de búsqueda, el sistema consulta automáticamente la base de datos de RENIEC. Si se encuentra, se auto-llenan los campos de nombres, apellidos y fecha de nacimiento. Se muestra un ícono de verificación ✅ al completarse exitosamente.

- **Consulta RUC (SUNAT):** Al ingresar un RUC, el sistema consulta SUNAT y auto-llena la razón social y el nombre comercial de la empresa. Se muestra un ícono de verificación ✅.

**Validación de Edad (Crítica para licorería):**
- El sistema valida en **tiempo real** que el cliente tenga 18 años o más
- Si la fecha de nacimiento indica que el cliente es menor de edad, se muestra una **caja de advertencia roja** con ícono de alerta ⚠️
- Se muestra la edad calculada en años junto con un mensaje informativo
- Si el cliente es mayor de edad, se muestra un ícono de verificación verde ✅
- **La creación del cliente se bloquea si es menor de 18 años**, tanto en el frontend como en el backend

**Información que se muestra en la tabla:**

| Columna | Descripción |
|---------|-------------|
| Nombre completo o Razón Social | Depende del tipo de documento |
| Tipo y Número de Documento | Con insignia del tipo |
| Edad | Calculada a partir de la fecha de nacimiento |
| Email + Teléfono | Datos de contacto |
| Total de Compras | Monto acumulado en soles (S/) |
| Última Compra | Fecha de su compra más reciente |
| Acciones | Editar y Eliminar |

**Operaciones soportadas:** Crear, Leer, Actualizar y Eliminar.

---

### 4.3 Gestión de Roles y Permisos

Esta pestaña permite crear **roles personalizados** y asignar **permisos granulares** por módulo del sistema, controlando exactamente qué puede ver y hacer cada tipo de usuario.

**Tipos de roles:**
- **Roles del Sistema:** Roles predefinidos que vienen con el sistema (ej: Admin, Cajero, Inventarista). **No se pueden editar ni eliminar.**
- **Roles Personalizados:** Roles creados por el administrador del negocio. **Se pueden editar y eliminar.**

**Campos de cada rol:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Nombre | Texto (obligatorio) | Nombre del rol (2-50 caracteres). Ej: "Supervisor de Ventas" |
| Descripción | Texto (opcional) | Descripción de las responsabilidades del rol |
| Estado | Interruptor | Activo o Inactivo |

**Sistema de Permisos por Módulo:**

Los permisos se organizan jerárquicamente por módulos del sistema. Cada módulo puede tener sub-módulos, y cada uno tiene acciones específicas que se pueden otorgar o revocar:

```
Módulo raíz
├── ventas (Ventas)
│   ├── ventas.pos (Punto de Venta)
│   ├── ventas.cajas (Cajas Registradoras)
│   ├── ventas.movimientos (Movimientos de Caja)
│   └── ventas.historial (Historial de Ventas)
├── facturacion (Facturación)
│   └── facturacion.comprobantes (Comprobantes)
├── inventario (Inventario)
├── reportes (Reportes)
└── usuarios (Usuarios)
    ├── usuarios.usuarios (Gestión de Usuarios)
    ├── usuarios.clientes (Gestión de Clientes)
    └── usuarios.roles (Gestión de Roles)
```

**Tipos de permisos (acciones) disponibles:**

| Permiso | Color | Descripción |
|---------|-------|-------------|
| Ver | Azul | Permite visualizar los datos del módulo |
| Crear | Verde | Permite crear nuevos registros |
| Editar | Amarillo | Permite modificar registros existentes |
| Eliminar | Rojo | Permite borrar o desactivar registros |
| Exportar | Púrpura | Permite descargar datos en CSV/PDF |
| Aprobar | Teal | Permite aprobar operaciones pendientes |
| Configurar | Gris | Permite modificar la configuración del módulo |
| Completo | Índigo | Acceso total a todas las acciones del módulo |

**Alcance de los permisos (Scope):**

Algunos permisos soportan un **nivel de alcance** que restringe aún más lo que el usuario puede hacer:

| Alcance | Descripción |
|---------|-------------|
| Completo | Acceso total a todos los registros del módulo |
| Caja Asignada | Solo puede operar con la caja registradora que tiene asignada |

Los permisos con alcance aplican a: `ventas.cajas`, `ventas.movimientos`, `ventas.historial` y `facturacion.comprobantes`.

**Interfaz de asignación de permisos:**
- Los permisos se muestran agrupados por módulo en **secciones colapsables**
- Cada sección muestra un contador: "X/Y permisos asignados"
- Los permisos se otorgan o revocan con un sencillo clic
- Si un permiso soporta alcance, aparece un selector adicional

**Herencia de permisos:** Si un usuario tiene el permiso de un módulo padre (ej: `m.ventas`), automáticamente tiene acceso a todos los sub-módulos hijos (ej: `m.ventas.pos`, `m.ventas.cajas`, etc.).

**Operaciones soportadas:** Crear roles, Actualizar roles, Eliminar roles personalizados, Asignar permisos, Revocar permisos, Actualizar alcance de permisos.

---

## 5. MÓDULO DE REPORTES Y ANALÍTICAS

El módulo de Reportes proporciona **6 tipos de reportes especializados** que permiten al administrador analizar en profundidad cada aspecto de su negocio. Todos los reportes incluyen filtros por rango de fechas, buscador, paginación y opciones de exportación.

**Estructura del módulo:**

```
Reportes
├── Ventas
├── Productos Más Vendidos
├── Inventario
├── Movimientos de Inventario
├── Compras y Gastos
└── Comprobantes Electrónicos
```

**Opciones de exportación disponibles en todos los reportes:**
- **CSV:** Archivo separado por comas con codificación BOM para compatibilidad con Excel. Se descarga automáticamente con nombre `{reporte}_{YYYY-MM-DD}.csv`
- **PDF:** Abre una vista previa de impresión con tabla formateada (encabezados verdes, filas con colores alternados, diseño responsive)

---

### 5.1 Reporte de Ventas

Este reporte presenta un **análisis integral de todas las ventas realizadas** por el negocio, con métricas de rendimiento y comparativas por sede.

**Métricas principales (tarjetas):**

| Métrica | Descripción |
|---------|-------------|
| Total de ventas completadas | Cantidad de transacciones finalizadas exitosamente |
| Monto total de ventas | Suma total en soles (S/) de todas las ventas |
| Ticket promedio | Monto promedio por transacción de venta |
| Ventas canceladas | Cantidad de ventas que fueron anuladas |

**Gráficos:**
- **Gráfico de líneas:** Tendencia diaria de ventas (últimos 7 o 30 días)
- **Gráfico de dona:** Distribución de ventas por sede

**Filtros disponibles:**
- Rango de fechas (desde/hasta)
- Búsqueda por número de venta o estado
- Agrupación: por día, semana o mes

**Sub-pestañas de análisis:**

**Pestaña Temporal:**
- Resumen agrupado por periodo seleccionado (día/semana/mes)
- Columnas: Periodo, Cantidad de ventas, Monto total, Ticket promedio
- Lista detallada de ventas con paginación (10 por página)

**Pestaña Por Sede:**
- Resumen semanal con indicador de la **sede con mejor rendimiento**
- Gráfico de barras mostrando participación porcentual por sede
- Columnas: Posición, Sede, Cantidad de ventas, Monto total, Ticket promedio, % de Participación

**Tipos de venta rastreados:** POS (punto de venta), Tienda Online, Mesa, Teléfono

**Estados de venta:** Completada, Pendiente, Anulada, Cancelada, Reembolsada

**Exportación:** CSV y PDF con columnas: N° Venta, Fecha, Tipo, Sede, Total, Estado

---

### 5.2 Reporte de Productos Más Vendidos

Este reporte presenta el **ranking de productos por popularidad** y volumen de ventas, permitiendo identificar los bestsellers del negocio.

**Métricas principales (tarjetas):**

| Métrica | Descripción |
|---------|-------------|
| Total de productos vendidos | Cantidad de productos únicos que tuvieron ventas |
| Unidades totales vendidas | Suma de todas las unidades vendidas |
| Monto total de productos | Ingreso total generado por las ventas de productos |
| Producto estrella | Nombre del producto más vendido del periodo |

**Gráficos:**
- **Gráfico de barras horizontales:** Top 5 productos más vendidos con sus unidades

**Filtros disponibles:**
- Rango de fechas (desde/hasta)
- Búsqueda por nombre de producto
- Paginación (10 por página)

**Datos por producto:**

| Columna | Descripción |
|---------|-------------|
| Posición | Ranking con colores dorado 🥇, plateado 🥈 y bronce 🥉 para los 3 primeros |
| Producto | Nombre del producto |
| Unidades vendidas | Cantidad total de unidades despachadas |
| Monto total | Ingreso generado por este producto |
| Apariciones en ventas | Cantidad de transacciones donde apareció este producto |

**Exportación:** CSV con: Posición, Producto, Unidades Vendidas, Monto Total, Apariciones en Ventas

---

### 5.3 Reporte de Inventario

Este reporte presenta una **fotografía instantánea del estado actual del inventario** con análisis de rentabilidad por producto.

**Métricas principales (tarjetas):**

| Métrica | Descripción |
|---------|-------------|
| Total de productos en inventario | Cantidad de productos registrados con stock |
| Valor del inventario al costo | Capital invertido en mercancía |
| Valor de venta potencial | Ingreso potencial si se vendiera todo el stock |
| Productos bajo stock mínimo | Cantidad de productos con stock por debajo del umbral |

**Análisis de margen por producto:**
Cada producto muestra su margen de ganancia con un sistema de códigos de color:
- 🟢 **Verde:** Margen superior al 30% (saludable)
- 🟡 **Amarillo:** Margen entre 15% y 30% (aceptable)
- 🔴 **Rojo:** Margen inferior al 15% (bajo)

**Filtros disponibles:**
- Búsqueda por nombre de producto o almacén

**Datos por producto:**

| Columna | Descripción |
|---------|-------------|
| Producto | Nombre del producto |
| Almacén | Ubicación del stock |
| Stock Actual | Cantidad disponible |
| Stock Mínimo | Umbral mínimo configurado |
| Costo Promedio | Costo ponderado de los lotes |
| Precio de Venta | Precio al público |
| Margen (%) | Porcentaje de ganancia (con color) |
| Valor del Inventario | Stock actual × costo promedio |

**Exportación:** CSV con todos los campos mencionados

*Nota: Este reporte muestra datos en tiempo real, no utiliza rango de fechas.*

---

### 5.4 Reporte de Movimientos de Inventario

Este reporte presenta la **trazabilidad completa de todos los movimientos de inventario**, funcionando como un Kardex digital que registra cada entrada, salida, ajuste y merma.

**Métricas principales (tarjetas):**

| Métrica | Color | Descripción |
|---------|-------|-------------|
| Entradas | 🟢 Verde | Productos ingresados al inventario (compras, devoluciones) |
| Salidas | 🔴 Rojo | Productos retirados del inventario (ventas, despachos) |
| Ajustes | 🔵 Azul | Correcciones manuales de stock |
| Mermas | 🟡 Amarillo | Pérdidas por vencimiento, rotura, robo u otros |

**Filtros disponibles:**
- Rango de fechas (desde/hasta)
- Búsqueda por producto, motivo o responsable
- Filtro por tipo de movimiento

**Sub-pestañas:**

**Pestaña Lista de Movimientos:**
- Tabla detallada con: Tipo (con insignia de color), Producto, Cantidad, Fecha, Responsable, Motivo
- Paginación (10 por página)

**Pestaña Por Tipo:**
- 4 tarjetas con colores: Entrada (verde), Salida (rojo), Ajuste (azul), Merma (amarillo)
- Cada tarjeta muestra: ícono, cantidad de movimientos y total de unidades
- Tabla resumen: Tipo, Cantidad de movimientos, Unidades totales, Motivo más común

**Exportación:** CSV y PDF con: Tipo, Producto, Cantidad, Fecha, Responsable, Motivo

---

### 5.5 Reporte de Compras y Gastos

Este reporte presenta el **análisis de los egresos del negocio**, separado en órdenes de compra a proveedores y gastos operativos.

**Métricas principales (tarjetas):**

| Métrica | Descripción |
|---------|-------------|
| Total de compras | Monto total invertido en compras a proveedores |
| Órdenes recibidas | Cantidad de órdenes de compra completadas |
| Total de gastos | Monto total en gastos operativos |
| Total de egresos | Suma total (compras + gastos) |

**Gráficos:**
- **Lista:** Top 5 proveedores con mayor monto de compra
- **Gráfico de barras apiladas:** Distribución de gastos por categoría

**Sub-pestaña Órdenes de Compra:**
- Tabla con: N° Orden, Proveedor, Fecha, Total, Estado
- Estados: Pendiente, Recibida, Cancelada, Parcial
- Top 5 proveedores con: cantidad de órdenes y gasto acumulado

**Sub-pestaña Gastos:**
- Tabla con: Descripción, Categoría, Fecha, Monto, Estado
- Estados: Pendiente, Pagado, Anulado
- Top 5 categorías de gasto con distribución porcentual

**Filtros disponibles:**
- Rango de fechas (desde/hasta)
- Búsqueda por número de orden/proveedor (Compras) o descripción/categoría (Gastos)

**Exportación:** CSV separado para Compras y para Gastos

---

### 5.6 Reporte de Comprobantes Electrónicos

Este reporte permite el **seguimiento y control de la facturación electrónica**, incluyendo boletas, facturas y el estado de envío a SUNAT.

**Métricas principales (tarjetas):**

| Métrica | Descripción |
|---------|-------------|
| Total de comprobantes | Cantidad total de documentos emitidos |
| Monto total facturado | Suma total de todos los comprobantes |
| Cantidad de boletas | Documentos tipo boleta emitidos |
| Cantidad de facturas | Documentos tipo factura emitidos |
| Pendientes de envío a SUNAT | Comprobantes que aún no han sido reportados |

**Tablas y gráficos:**
- **Control de series:** Tabla con Serie, Cantidad, Monto Total, Último N°
- **Top 10 clientes corporativos:** Mejores clientes por monto de factura con RUC
- **Resumen diario para SUNAT:** Fecha, Boletas, Facturas, Total del día
- **Horas pico de emisión:** Análisis de la hora del día con mayor volumen de facturación

**Sub-pestañas:**

**Pestaña Boletas:**
- Control de series de boletas
- Lista detallada: Serie-Número, Fecha, Cliente, Documento, Total, Estado

**Pestaña Facturas:**
- Top 10 clientes corporativos con: Cliente, RUC, Cantidad de facturas, Monto total
- Banner de advertencia si hay facturas pendientes de envío a SUNAT
- Lista detallada con paginación

**Pestaña Análisis SUNAT:**
- Resumen diario: Fecha, Boletas emitidas, Facturas emitidas, Total del día
- Horas pico: Hora, Cantidad de documentos, % del pico máximo

**Estados de comprobante:** Borrador, Pendiente de Envío, Enviado, Aceptado, Observado, Rechazado, Anulado

**Filtros disponibles:**
- Rango de fechas (desde/hasta)
- Búsqueda por serie, número, cliente o RUC

**Exportación:** CSV y PDF para Boletas y Facturas, CSV para resumen SUNAT

---

## 6. VISIÓN DE CLIENTE — TIENDA ONLINE (STOREFRONT)

La Tienda Online de DrinkGo es el portal público de e-commerce donde los **clientes finales** pueden explorar el catálogo de productos de una licorería, registrarse, hacer pedidos y realizar compras con delivery o recojo en tienda. A continuación, describimos la experiencia completa del cliente paso a paso.

---

### 6.1 Página Principal

Al acceder a la tienda online a través de la URL `drinkgo.com/{slug}` (donde `{slug}` es el identificador único de la licorería), el cliente encuentra la **página principal de la tienda** con los siguientes elementos:

- **Banner de bienvenida:** Muestra el mensaje de bienvenida configurado por el administrador, con los colores de la marca
- **Logo del negocio:** Imagen del logotipo de la licorería
- **Nombre de la tienda:** Nombre comercial configurado
- **Verificación de edad:** Como se trata de una licorería, el sistema muestra una verificación de edad que el visitante debe aceptar antes de poder navegar el catálogo
- **Productos destacados:** Selección de productos y promociones activas
- **Categorías principales:** Acceso rápido a las categorías de productos
- **Combos y promociones:** Si la sede seleccionada tiene combos o promociones activas, se muestran de forma destacada

**Navegación principal:**
En la parte superior se encuentran las opciones de navegación: Inicio, Catálogo, Carrito, Iniciar Sesión/Mi Cuenta. Si el cliente ya tiene sesión iniciada, se muestra su nombre y opciones de perfil.

---

### 6.2 Registrarse

Para poder realizar compras en la tienda online, el cliente debe crear una cuenta. El proceso de registro funciona de la siguiente manera:

**Paso 1:** El cliente hace clic en el botón **"Iniciar Sesión"** ubicado en la barra de navegación superior.

**Paso 2:** En la pantalla de inicio de sesión, hace clic en el enlace **"Regístrate aquí"** ubicado debajo del formulario.

**Paso 3:** Se muestra el **formulario de registro** con los siguientes campos:

| Campo | Obligatorio | Validación | Descripción |
|-------|-------------|------------|-------------|
| Nombres | ✅ Sí | Mínimo requerido | Nombres del cliente |
| Apellidos | ✅ Sí | Mínimo requerido | Apellidos del cliente |
| Tipo de Documento | ✅ Sí | — | DNI, CE (Carnet de Extranjería) o Pasaporte |
| Número de Documento | ✅ Sí | DNI: 8 dígitos exactos | Número del documento de identidad |
| Correo Electrónico | ✅ Sí | Formato de email válido | Se usará para iniciar sesión |
| Teléfono | ❌ No | 9 dígitos, empieza con 9 | Número de celular (opcional) |
| Contraseña | ✅ Sí | Mínimo 6 caracteres | Contraseña para acceder a la cuenta |
| Confirmar Contraseña | ✅ Sí | Debe coincidir con la contraseña | Confirmación de seguridad |

**Función de auto-llenado por DNI:**
Al ingresar un número de DNI y presionar el botón de búsqueda 🔍, el sistema consulta la base de datos de **RENIEC** y rellena automáticamente los campos de nombres y apellidos. Se muestra un mensaje de éxito cuando la consulta es exitosa.

**Paso 4:** El cliente verifica que todos los datos son correctos y hace clic en el botón **"Crear cuenta"**.

**Paso 5:** Si el registro es exitoso:
- Se muestra una notificación de éxito: "Cuenta creada exitosamente"
- El sistema **inicia sesión automáticamente** con la nueva cuenta
- El cliente es redirigido a la página principal de la tienda

**Paso 5 (alternativo):** Si hay algún error:
- Se muestra una notificación con el mensaje de error del servidor (ej: "El email ya está registrado")
- El formulario permanece visible para que el cliente corrija los datos

---

### 6.3 Iniciar Sesión

Una vez que el cliente tiene una cuenta creada, puede iniciar sesión en cualquier momento para acceder a sus funcionalidades de compra.

**Paso 1:** El cliente hace clic en el botón **"Iniciar Sesión"** en la barra de navegación.

**Paso 2:** Se muestra el formulario de inicio de sesión con dos campos:

| Campo | Descripción |
|-------|-------------|
| Correo Electrónico | El email utilizado durante el registro |
| Contraseña | La contraseña de la cuenta |

**Paso 3:** El cliente ingresa sus credenciales y hace clic en **"Iniciar sesión"**.

**Paso 4:** Si las credenciales son correctas:
- El sistema genera un **token JWT** de autenticación
- Se almacenan los datos del cliente y el token de forma persistente
- Se redirige automáticamente a la tienda (o a la URL de redirección si venía de intentar una compra)

**Paso 4 (alternativo):** Si las credenciales son incorrectas:
- Se muestra una notificación de error con el mensaje del servidor

**Funcionalidad de redirección inteligente:** Si el cliente intenta acceder a una página que requiere autenticación (como el checkout), el sistema lo redirige al login y, tras iniciar sesión exitosamente, lo lleva de vuelta a la página original.

**Manejo de sesión:**
- La sesión se mantiene activa incluso al cerrar el navegador (persistencia con Zustand)
- Si el token expira o es inválido, el sistema cierra la sesión automáticamente y redirige al login
- Protección automática ante respuestas 401 del servidor

---

### 6.4 Catálogo de Productos

El catálogo es el **corazón de la tienda online**, donde el cliente explora toda la oferta de productos de la licorería.

**Elementos del catálogo:**

- **Barra de búsqueda:** Permite buscar productos por nombre con filtrado instantáneo
- **Filtros por categoría:** Acceso a las categorías de productos definidas por el administrador (Vinos, Cervezas, Whisky, Vodka, Ron, Pisco, etc.)
- **Filtros por marca:** Filtrado por marcas disponibles
- **Opciones de ordenamiento:** Nombre (A-Z, Z-A), Precio (menor a mayor, mayor a menor), Popularidad, Más recientes
- **Selector de Sucursal:** **Es obligatorio elegir una sucursal para poder agregar productos al carrito**. El sistema muestra las sedes disponibles (solo las que tienen delivery o recojo habilitado)

**Tarjetas de producto:**
Cada producto se muestra como una tarjeta con:
- Imagen del producto
- Nombre del producto
- Precio de venta (S/)
- Categoría
- Marca
- Indicador de stock disponible
- Botón "Agregar al carrito"
- Si tiene promoción activa, se muestra el descuento

**Combos y Promociones:**
Si la sede seleccionada tiene combos o promociones activas, se muestran de forma destacada con:
- Nombre del combo/promoción
- Productos incluidos
- Precio especial vs. precio regular
- Vigencia (desde/hasta)

**Nota importante:** Es **obligatorio** seleccionar una sucursal antes de agregar productos al carrito. Si el cliente intenta agregar un producto sin haber elegido sucursal, el sistema no permitirá la acción.

---

### 6.5 Detalle de Producto

Al hacer clic en un producto del catálogo, se abre su **ficha completa** con información detallada:

| Elemento | Descripción |
|----------|-------------|
| Imágenes | Galería de imágenes del producto |
| Nombre | Nombre completo del producto |
| Descripción | Descripción detallada del producto |
| Características del licor | Tipo de bebida, grado alcohólico, volumen, origen, añada |
| Precio | Precio de venta con formato de moneda |
| Disponibilidad | Indicador de stock disponible o agotado |
| Productos relacionados | Sugerencias de productos similares |
| Selector de cantidad | Control numérico para elegir cuántas unidades agregar |
| Botón "Agregar al carrito" | Agrega la cantidad seleccionada al carrito |

---

### 6.6 Carrito de Compras

El carrito es el espacio donde el cliente acumula los productos que desea comprar antes de finalizar el pedido.

**Funcionalidades del carrito:**

| Función | Descripción |
|---------|-------------|
| Ver productos agregados | Lista de todos los productos con imagen, nombre, precio y cantidad |
| Modificar cantidades | Control numérico para aumentar o disminuir la cantidad de cada producto |
| Eliminar productos | Botón para quitar un producto específico del carrito |
| Subtotal por producto | Cálculo automático de cantidad × precio por cada producto |
| Costo de envío | Muestra el costo de delivery según la zona seleccionada |
| Total de la compra | Suma total incluyendo subtotales y costo de envío |
| Promociones automáticas | Si hay combos o promociones vigentes, se aplican automáticamente al carrito |
| Vaciar carrito | Opción para eliminar todos los productos de una vez |

**Comportamiento importante:**
- El carrito se guarda de forma **persistente** en el navegador (no se pierde al cerrar la pestaña)
- Si se cambia la sucursal seleccionada, **el carrito se vacía automáticamente** (ya que el stock puede variar entre sedes)
- El carrito muestra un **contador** en el ícono de la barra de navegación con la cantidad total de productos
- Si una cantidad se reduce a 0, el producto se elimina automáticamente

---

### 6.7 Proceso de Compra (Checkout)

El checkout es el proceso de **finalización de la compra** donde el cliente confirma su pedido.

**Requisito:** El cliente debe tener sesión iniciada para acceder al checkout. Si no ha iniciado sesión, el sistema lo redirige automáticamente al login.

**Pasos del checkout:**

**Paso 1 — Método de Entrega:**
El cliente selecciona cómo desea recibir su pedido:
- **Delivery:** Entrega a domicilio (si la sede tiene delivery habilitado)
- **Recojo en Tienda:** El cliente recoge su pedido en el local (si la sede tiene recojo habilitado)

**Paso 2 — Dirección de Entrega (solo para delivery):**
- Seleccionar una dirección guardada en el perfil
- O ingresar una nueva dirección de entrega
- El sistema verifica que la dirección esté dentro de una zona de delivery válida
- Se muestra el costo de envío correspondiente a la zona

**Paso 3 — Método de Pago:**
El cliente selecciona entre los métodos de pago habilitados para la tienda online:
- Efectivo (pago contra entrega)
- Transferencia bancaria (se muestran los datos bancarios del negocio)
- Billetera digital: Yape o Plin
- Tarjeta de crédito/débito
- Otros métodos configurados por el administrador

**Paso 4 — Resumen y Confirmación:**
Se presenta un resumen completo del pedido:
- Productos con cantidades y precios
- Subtotal
- Costo de delivery (si aplica)
- Total a pagar
- Método de entrega seleccionado
- Dirección de entrega (si es delivery)
- Método de pago seleccionado

**Paso 5 — Confirmar Pedido:**
Al hacer clic en "Confirmar Pedido", el sistema:
- Genera automáticamente un **número de pedido**
- Registra el pedido con estado "Pendiente"
- Si el pago es digital (Yape/Plin), muestra la opción de subir comprobante de pago
- Redirige al historial de pedidos

---

### 6.8 Mis Pedidos

Esta sección permite al cliente **hacer seguimiento de todos sus pedidos** realizados, tanto actuales como anteriores.

**Requisito:** El cliente debe tener sesión iniciada.

**Vista principal:**
Se muestra una lista de todos los pedidos del cliente, cada uno con:

| Dato | Descripción |
|------|-------------|
| Número de pedido | Identificador único del pedido |
| Fecha del pedido | Cuándo se realizó la compra |
| Lista de productos | Detalle de los ítems con precios |
| Subtotal | Valor de los productos |
| Costo de delivery | Costo de envío (si aplica) |
| Total | Monto total del pedido |
| Estado actual | Estado del pedido con ícono y color |
| Método de pago | Cómo se pagó (efectivo, Yape, transferencia, etc.) |

**Flujo de estados del pedido:**

```
🟡 Pendiente → 🔵 Confirmado → 🟣 Preparando → 🔵 Listo → 🟣 En Camino → 🟢 Entregado
                                                                    ↓
                                                              🔴 Cancelado
```

| Estado | Ícono | Color | Descripción |
|--------|-------|-------|-------------|
| Pendiente | 🕐 Reloj | Ámbar | El pedido fue creado y está esperando confirmación |
| Confirmado | ✅ Check | Azul | El negocio confirmó el pedido |
| Preparando | 📦 Paquete | Púrpura | Los productos se están preparando |
| Listo | 📦 Paquete | Teal | El pedido está listo para despacho o recogida |
| En Camino | 🚚 Camión | Índigo | El repartidor está en ruta hacia la dirección del cliente |
| Entregado | ✅ Check | Verde | El pedido fue entregado satisfactoriamente |
| Cancelado | ❌ Cruz | Rojo | El pedido fue cancelado |

**Línea de tiempo visual:** Cada pedido muestra una **línea de tiempo** (timeline) que marca visualmente en qué etapa se encuentra, con fechas y horas de cada cambio de estado.

**Detalle del pedido (modal):**
Al hacer clic en un pedido, se abre un modal con información completa:
- Número de pedido y fecha
- Nombre y documento del cliente
- Dirección de entrega con ícono de ubicación 📍
- Detalle de cada producto: nombre, cantidad, precio unitario y subtotal
- Línea de tiempo del pedido
- Información de pago
- Botones de: **Imprimir recibo** 🖨️ y **Descargar** 📥

**Subir comprobante de pago:**
Para pedidos pagados con métodos digitales (Yape, Plin), el cliente puede subir la captura de pantalla del comprobante de pago haciendo clic en el botón **"Subir comprobante"** junto al detalle del pago.

**Impresión de recibo:**
Se genera un recibo formateado con:
- Datos del negocio
- Datos del cliente
- Detalle completo de productos
- Totales
- Información de pago

---

### 6.9 Mi Perfil

Esta sección permite al cliente gestionar su **información personal** y **cambiar su contraseña**.

**Requisito:** El cliente debe tener sesión iniciada. Si no lo está, se redirige al login.

**Sección de Datos Personales:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Nombres | Texto (obligatorio) | Nombres del cliente |
| Apellidos | Texto (obligatorio) | Apellidos del cliente |
| Teléfono | Texto (opcional) | Número de celular (máximo 9 dígitos) |
| Dirección | Texto (opcional) | Dirección de entrega principal |

Al guardar los cambios, se muestra una notificación: *"Perfil actualizado exitosamente"*.

**Sección de Cambio de Contraseña:**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| Contraseña Actual | Texto con botón mostrar/ocultar 👁️ | Se requiere para verificar la identidad |
| Nueva Contraseña | Texto con botón mostrar/ocultar 👁️ | Mínimo 6 caracteres |
| Confirmar Nueva Contraseña | Texto con botón mostrar/ocultar 👁️ | Debe coincidir con la nueva contraseña |

Al cambiar la contraseña exitosamente, se muestra: *"Contraseña actualizada exitosamente"*.

---

### 6.10 Mis Devoluciones

Esta sección permite al cliente **solicitar la devolución** de productos de pedidos que ya fueron entregados.

**Requisito:** El cliente debe tener sesión iniciada y el pedido debe estar en estado "Entregado".

**Proceso de solicitud de devolución:**

**Paso 1:** El cliente accede a la sección "Mis Devoluciones"

**Paso 2:** Selecciona el **pedido entregado** del cual desea devolver productos

**Paso 3:** Selecciona los **productos específicos** a devolver

**Paso 4:** Indica la **cantidad** de unidades a devolver de cada producto

**Paso 5:** Selecciona el **motivo de devolución** (producto dañado, producto equivocado, calidad insatisfactoria, otro)

**Paso 6:** Envía la solicitud

**Paso 7:** La solicitud queda en estado **"Pendiente"** y será gestionada por el administrador del negocio desde el módulo de Devoluciones del panel de administración.

---

## 7. OPCIONES CONFIGURABLES DEL SISTEMA

A modo de resumen, las siguientes son todas las **opciones que se pueden activar, desactivar o configurar** desde el módulo de Configuración y que afectan el comportamiento del sistema:

### 7.1 Interruptores Principales (On/Off)

| Opción | Ubicación | Efecto |
|--------|-----------|--------|
| **Tienda Online Habilitada** | Configuración > Tienda Online | Activa o desactiva por completo la tienda online del negocio. Si está desactivada, los clientes no pueden acceder |
| **Aplicar IGV** | Configuración > Mi Negocio | Activa o desactiva el cálculo del Impuesto General a las Ventas en facturas y boletas |
| **Delivery Habilitado (por sede)** | Configuración > Sedes | Activa la opción de entrega a domicilio para la sede seleccionada |
| **Recojo Habilitado (por sede)** | Configuración > Sedes | Activa la opción de recojo en tienda para la sede seleccionada |
| **Maneja Mesas (por sede)** | Configuración > Sedes | Habilita la funcionalidad de mesas/atención en local para la sede |
| **Es Sede Principal** | Configuración > Sedes | Marca una sede como la principal del negocio |
| **Es Almacén Predeterminado** | Configuración > Almacenes | Marca un almacén como el predeterminado para operaciones de inventario |
| **Método de Pago en POS** | Configuración > Métodos de Pago | Activa un método de pago para el punto de venta físico |
| **Método de Pago en Tienda Online** | Configuración > Métodos de Pago | Activa un método de pago para la tienda web |

### 7.2 Configuraciones de Valor

| Configuración | Ubicación | Descripción |
|---------------|-----------|-------------|
| **Porcentaje de IGV** | Mi Negocio | Valor del impuesto (por defecto 18%) |
| **Color Primario de la Tienda** | Tienda Online | Color principal de la marca en la tienda |
| **Color Secundario de la Tienda** | Tienda Online | Color secundario complementario |
| **Mensaje de Bienvenida** | Tienda Online | Texto de bienvenida visible en la página principal |
| **Slug de la Tienda** | Tienda Online | URL amigable de la tienda (ej: drinkgo.com/mi-licoreria) |
| **Dominio Personalizado** | Tienda Online | Dominio propio para la tienda |
| **Tarifa de Delivery por zona** | Zonas de Delivery | Costo de envío por zona |
| **Monto Mínimo de Pedido por zona** | Zonas de Delivery | Pedido mínimo requerido |
| **Monto de Apertura por Defecto** | Cajas Registradoras | Monto inicial sugerido al abrir caja |
| **Capacidad de Mesas** | Mesas | Número máximo de personas por mesa |

### 7.3 Configuraciones de Seguridad y Permisos

| Configuración | Ubicación | Descripción |
|---------------|-----------|-------------|
| **Roles personalizados** | Usuarios > Roles | Crear roles con permisos específicos |
| **Permisos granulares** | Usuarios > Roles | Asignar acciones específicas (ver, crear, editar, eliminar, exportar, aprobar, configurar, completo) por cada módulo |
| **Alcance de permisos** | Usuarios > Roles | Configurar si un permiso aplica a todo el negocio (completo) o solo a la caja asignada (caja_asignada) |
| **Asignación de sede por usuario** | Usuarios > Usuarios | Definir en qué sede opera cada empleado |
| **Estado de usuarios** | Usuarios > Usuarios | Activar o desactivar cuentas de empleados |
| **Validación de edad de clientes** | Usuarios > Clientes | El sistema impide registrar clientes menores de 18 años |

---

## 8. RESUMEN DE FUNCIONALIDADES

### Cuadro Resumen por Módulo

| Módulo | Funcionalidades Principales | Operaciones CRUD | Exportación |
|--------|------------------------------|------------------|-------------|
| **Dashboard** | 6 KPIs, 3 gráficos, 4 tablas operativas, actualización en tiempo real | Solo lectura | No |
| **Configuración > Mi Negocio** | Datos del negocio, IGV, consulta SUNAT/RENIEC | Actualizar | No |
| **Configuración > Sedes** | Sucursales, delivery, recojo, mesas | Crear, Leer, Actualizar, Desactivar | No |
| **Configuración > Tienda Online** | E-commerce, colores, slug, dominio | Crear, Actualizar | No |
| **Configuración > Almacenes** | Depósitos por sede | Crear, Leer, Actualizar, Eliminar | No |
| **Configuración > Métodos de Pago** | Canales POS y Online, 9 tipos de pago | Crear, Leer, Actualizar, Eliminar | No |
| **Configuración > Zonas de Delivery** | Zonas, tarifas, calculadora inteligente | Crear, Leer, Actualizar, Eliminar | No |
| **Configuración > Mesas** | Gestión de mesas por sede | Crear, Leer, Actualizar, Eliminar | No |
| **Configuración > Cajas** | Cajas registradoras por sede | Crear, Leer, Actualizar, Eliminar | No |
| **Configuración > Categorías de Gastos** | 8 tipos de gastos operativos | Crear, Leer, Actualizar, Eliminar | No |
| **Usuarios** | Personal del negocio, roles y sedes | Crear, Leer, Actualizar, Desactivar | No |
| **Clientes** | Base de clientes, DNI/RUC, validación edad | Crear, Leer, Actualizar, Eliminar | No |
| **Roles y Permisos** | Roles personalizados, permisos granulares por módulo | Crear, Leer, Actualizar, Eliminar | No |
| **Reporte de Ventas** | Análisis temporal y por sede | Solo lectura | CSV, PDF |
| **Reporte de Productos** | Ranking de bestsellers | Solo lectura | CSV |
| **Reporte de Inventario** | Stock actual y rentabilidad | Solo lectura | CSV |
| **Reporte de Movimientos** | Kardex digital completo | Solo lectura | CSV, PDF |
| **Reporte de Compras y Gastos** | Egresos operativos | Solo lectura | CSV |
| **Reporte de Comprobantes** | Facturación electrónica y SUNAT | Solo lectura | CSV, PDF |
| **Tienda Online (Cliente)** | Catálogo, carrito, checkout, pedidos, perfil, devoluciones | Completo | No |

### Flujo General del Cliente en la Tienda Online

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐     ┌─────────────┐
│   Página    │────>│  Registrarse │────>│   Iniciar    │────>│  Explorar   │
│  Principal  │     │  (Sign Up)   │     │   Sesión     │     │  Catálogo   │
└─────────────┘     └──────────────┘     └──────────────┘     └──────┬──────┘
                                                                      │
                                                                      ▼
┌─────────────┐     ┌──────────────┐     ┌──────────────┐     ┌─────────────┐
│    Mis      │<────│   Confirmar  │<────│   Checkout   │<────│  Carrito de │
│  Pedidos    │     │   Pedido     │     │  (Pago)      │     │   Compras   │
└──────┬──────┘     └──────────────┘     └──────────────┘     └─────────────┘
       │
       ▼
┌─────────────┐     ┌──────────────┐
│ Seguimiento │────>│  Solicitar   │
│ del Pedido  │     │  Devolución  │
└─────────────┘     └──────────────┘
```

---

*DrinkGo — Aplicación Informática Distribuida para Negocios de Licorería*  
*Documento generado en base al análisis del código fuente del sistema*  
*Versión 1.0 — Marzo 2026*
