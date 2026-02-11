1. REQUISITOS FUNCIONALES 
**1.1. MÓDULO I: GESTIÓN DE PLATAFORMA (SUPERADMIN)** 
Este módulo gestiona las funcionalidades de administración de la plataforma SaaS, 
incluyendo la gestión de negocios (tenants), planes de suscripción, facturación de 
suscripciones, y el panel de SuperAdmin para monitoreo y administración de todos los 
negocios en la plataforma. 

**1.1.1. Submódulo: Gestión de Suscripciones y Tenants** 

**RF-PLT-001: Registrar Nuevo Negocio (Tenant)** 
Descripción: Este requerimiento define el proceso mediante el cual se incorpora un 
nuevo negocio a la plataforma. El sistema permite registrar la información legal y 
comercial del negocio, asignar un plan de suscripción inicial y crear automáticamente la 
estructura operativa necesaria para su funcionamiento independiente. Incluye la 
generación del usuario administrador principal, configuraciones iniciales y el 
aislamiento completo de datos para garantizar la arquitectura multi-tenant. 
**RF-PLT-002: Crear Plan de Suscripción** 
Descripción: Este requerimiento permite definir los planes comerciales que ofrece la 
plataforma. El sistema posibilita configurar precios, periodicidad, límites operativos y 
funcionalidades habilitadas, estableciendo así las condiciones bajo las cuales los 
negocios pueden utilizar el sistema. Los planes creados sirven como base para la 
contratación y escalabilidad de los negocios. 
**RF-PLT-003: Editar Plan de Suscripción** 
Permite modificar las características de planes existentes sin afectar a los negocios ya 
suscritos. El sistema gestiona los cambios mediante versionado, asegurando la 
continuidad del servicio y preservando las condiciones contractuales vigentes para 
suscripciones activas. 
**RF-PLT-004: Desactivar Plan de Suscripción** 
Descripción: Este requerimiento controla la descontinuación de planes. El sistema 
impide nuevas suscripciones a planes desactivados, manteniendo operativas las 
suscripciones existentes y conservando la integridad histórica de la información. 
**RF-PLT-005: Listar y Comparar Planes de Suscripción** 
Descripción: Proporciona vistas administrativas y comerciales que permiten visualizar, 
filtrar y comparar planes de suscripción. Facilita la toma de decisiones tanto para el 
SuperAdmin como para los negocios, mostrando de forma clara las diferencias entre 
planes. 
**RF-PLT-006: Cambiar Plan de Suscripción** 
Descripción: Este requerimiento permite a un negocio actualizar o degradar su plan de 
suscripción. El sistema valida límites operativos, calcula ajustes prorrateados y aplica 
los cambios según las reglas establecidas, garantizando una transición controlada entre 
planes.

**1.1.2. Submódulo: Facturación y Ciclo de Pagos**

**RF-FAC-001: Facturar Suscripciones Automáticamente**
Descripción: Define el proceso de facturación recurrente de la plataforma. El sistema 
genera facturas de suscripción, procesa pagos automáticos, gestiona reintentos ante 
fallos y controla estados de morosidad, asegurando la continuidad financiera del modelo 
SaaS. 
**RF-FAC-002: Suspender o Cancelar Suscripción** 
Descripción: Este requerimiento gestiona la pausa temporal o la cancelación definitiva 
de una suscripción. El sistema controla el acceso al servicio, la retención de datos y las 
condiciones de reactivación o cierre, aplicando políticas claras según el estado del 
negocio. 
 
**1.1.3. Submódulo: Reportes y Monitoreo de Plataforma** 

**RF-RPL-001: Generar Reportes Consolidados de la Plataforma** 
Descripción: Permite al SuperAdmin analizar el desempeño global del sistema 
mediante métricas de crecimiento, ingresos, retención y uso. El sistema presenta 
dashboards ejecutivos y reportes exportables que apoyan la toma de decisiones 
estratégicas. 

**1.1.4. Submódulo: Configuración Global** 

**RF-CGL-001: Configurar Parámetros Globales de la Plataforma** 
Descripción: Este requerimiento centraliza la definición de configuraciones que afectan 
a todos los negocios, como impuestos, métodos de pago, integraciones externas y 
políticas generales. El sistema asegura control, versionado y auditoría de cada cambio 
aplicado. 

**1.1.5. Submódulo: Auditoría y Seguridad** 

**RF-L-002: Auditar Actividad de Usuarios y Sistema**
Descripción: Establece el registro obligatorio de todas las acciones críticas realizadas 
en la plataforma. El sistema proporciona herramientas de consulta y análisis de 
auditoría, permitiendo detectar anomalías, cumplir normativas y garantizar la 
trazabilidad de las operaciones. 

**1.2. MÓDULO II: ADMINISTRACIÓN DEL NEGOCIO**
Este Módulo permite la configuración estructural, operativa y de seguridad del sistema 
de licorerías bajo un enfoque multi empresa, asegurando que cada negocio o sus sedes 
operen de forma independiente, segura y conforme a la normativa vigente. 

**1.2.1. Submódulo: Configuración del Negocio y Sedes** 

**RF-ADM-001: Configurar Datos del Negocio** 
Descripción: El sistema debe permitir al Administrador registrar, consultar y actualizar 
los datos generales de la licorería, los cuales identifican legal y operativamente a la 
empresa dentro del sistema.Esta información es fundamental para la correcta operación 
del sistema, ya que es utilizada en documentos comerciales, reportes, configuraciones 
de sedes, y visualización en el storefront. Cada negocio opera como una unidad 
independiente (tenant), garantizando que sus datos no sean accesibles por otras 
empresas registradas en la plataforma. 
**RF-ADM-002: Configurar Branding del Negocio** 
Descripción: El sistema debe permitir personalizar la identificación visual del negocio, 
incluyendo logotipo, colores corporativos y nombre visible en el sistema y storefront, 
fortaleciendo la imagen de marca de cada licorería. 
**RF-ADM-003: Registrar Nueva Sede** 
Descripción: El sistema debe permitir registrar nuevas sedes físicas asociadas al 
negocio, representando locales independientes de operación, inventario y ventas. 
**RF-ADM-004: Editar Información de Sede** 
Descripción: Permite actualizar información operativa y administrativa de una sede 
previamente registrada, sin afectar la sin afectar la integridad de la información histórica 
asociada, como ventas, movimientos de inventario, cajas y auditorías. 
**RF-ADM-005: Desactivar Sede** 
Descripción: El sistema debe permitir desactivar  una sede para suspender su operación 
sin eliminar su información histórica. Una sede desactivada conserva todos sus 
registros( ventas,inventario,auditoría), pero queda inhábil para nuevas operaciones 
comerciales. 
**RF-ADM-006: Configurar Almacenes por Sede**  
Descripción: El sistema debe permitir configurar uno o más almacenes asociados a una 
sede, con el objetivo de gestionar correctamente el inventario según condiciones de 
almacenamiento requeridas por los productos( ambiente, frío, congelado).Este 
submódulo se integra directamente con inventario, productos y reglas de conservación. 
**RF-ADM-007: Configurar Horarios de Operación por Sede** 
Descripción: El sistema debe permitir definir los horarios de operación de cada sede, 
los cuales serán utilizados para validar la disponibilidad de ventas, pedidos, atención al 
cliente, asegurando el cumplimiento operativo y legal. 
**RF-ADM-008: Configurar Restricciones de Venta de Alcohol** 
Descripción: El sistema debe permitir configurar restricciones legales para la venta de 
productos alcohólicos, incluyendo validación de edad, horarios permitidos y días de 
venta, conforme a la normativa vigente. 
**RF-ADM-009: Configurar Zonas de Delivery** 
Descripción:El sistema debe permitir  definir zonas geográficas de reparto asociadas a 
una sede, con el objetivo de controlar la cobertura de delivery y validar pedidos online. 
**RF-ADM-010: Activar/Desactivar Módulo de Mesas** 
Descripción: El sistema debe permitir activar o desactivar el módulo de mesas por 
sede, habilitando o deshabilitando la gestión de consumo en local según el modelo 
operativo del negocio. 

**1.2.2. Submódulo: Gestión de Usuarios y Seguridad** 

**RF-ADM-011: Registrar Nuevo Usuario** 
Descripción: El sistema debe permitir al Administrador registrar nuevos usuarios 
dentro de un tenant, definiendo sus datos personales, credenciales de acceso, rol inicial 
y sedes asignadas. Este proceso asegura que solo personal autorizado pueda acceder al 
sistema y operar en las sedes correspondientes. 
**RF-ADM-012: Editar Usuario** 
Descripción: El sistema debe permitir actualizar la información de un usuario existente, 
como datos personales, roles   y estado, sin afectar el historial de operaciones realizadas 
por dicho usuario. 
**RF-ADM-013: Desactivar Usuario** 
Descripción: El sistema debe permitir desactivar un usuario para impedir su acceso al 
sistema sin eliminar su información ni el historial de acciones realizadas. 
**RF-ADM-014: Asignar Rol a Usuario** 
Descripción: El sistema debe asignar uno o más roles a un usuario para definir su nivel 
de acceso y responsabilidades dentro del sistema.. 
**RF-ADM-015: Asignar Usuario a Sedes** 
Descripción: El sistema debe permitir definir en qué sede puede operar un usuario, 
restringiendo su acceso operativo y visual a dichas sedes. 
**RF-ADM-016: Crear Rol Personalizado** 
Descripción: El sistema debe permitir crear roles personalizados adaptados a la 
estructura operativa del negocio, más allá de los roles estándar. 
**RF-ADM-017: Asignar Permisos a Rol** 
Descripción: El sistema debe permitir definir los permisos funcionales asociados a cada 
rol, controlando el acceso granular a los módulos del sistema. 
**RF-ADM-018: Consultar Log de Auditoría** 
Descripción: El sistema debe permitir consultar el registro histórico de acciones 
realizadas por los usuarios, asegurando trazabilidad y control interno. 
**RF-ADM-020: Autenticación de Usuario** 
Descripción: El sistema debe permitir autenticar usuarios mediante credenciales 
válidas, controlando el acceso seguro al sistema.  
**RF-ADM-021: Recuperar Contraseña** 
Descripción: El sistema debe permitir al usuario recuperar su acceso mediante un 
proceso seguro de restablecimiento de contraseña. 

**1.2.3. Submódulo: Configuración General**

RF-ADM-022: Configurar Parámetros del Sistema 
Descripción: El sistema debe permitir al Administrador definir y actualizar parámetros 
globales del sistema que controlan reglas operativas, valores por defectos y 
comportamientos automáticos del sistema ( por ejemplo: moneda, impuesto, límites de 
stock, políticas de seguridad, reglas de ventas). Estos parámetros se aplican a nivel de 
tenant y pueden ser heredados por las sedes, sin afectar datos históricos. 
RF-ADM-023: Configurar Preferencias de Usuario 
Descripción: El sistema debe permitir que cada usuario configure  preferencias 
personales relacionadas con idioma, formato de fecha, notificación, vista inicial del 
sistema y comportamiento del POS, mejorando la experiencia de uso sin alterar la 
configuración global del negocio. 
RF-ADM-024: Gestionar (DEBE SER CRUD) Notificaciones 
Descripción: Permitir al Administrador las notificaciones del sistema relacionadas con 
eventos operativos y administrativos, como alertas de stock bajo, productos por vencer, 
accesos sospechosos o cambios críticos de configuración. 
RF-ADM-025: Configurar Métodos de Pago 
Descripción: El sistema debe permitir definir y administrar los métodos de pago 
habilitados para el negocio, los cuales serán utilizados en ventas, pedidos y operaciones 
financieras, tanto en POS como en storefront. 

**1.3. MÓDULO III: GESTIÓN DE PRODUCTOS, INVENTARIO COMPRAS Y PROVEEDORES** 
Este módulo gestiona el catálogo completo de productos, el control de inventario con 
sistema FIFO para productos perecederos, y las operaciones de compras con 
proveedores. Es fundamental para el control de stock, la trazabilidad de lotes, y la 
gestión eficiente de la cadena de suministro en las licorerías.

**1.3.1. Submódulo: Catálogo de Productos** 

**RF-PRO-001: Registrar Nuevo Producto** 
Descripción: El Administrador debe poder registrar nuevos productos en el catálogo del 
negocio, ingresando información específica para licorerías como grado alcohólico, 
volumen, origen, tipo de bebida, temperatura de almacenamiento requerida, y 
configuración de visibilidad para storefront. Este es el punto de entrada para gestionar el 
inventario de bebidas alcohólicas y complementos. 
**RF-PRO-002: Editar Producto** 
Descripción: El Administrador debe poder actualizar la información de productos 
existentes, modificando precios, atributos, visibilidad, y configuraciones, sin afectar el 
historial de movimientos de inventario ni las ventas ya realizadas. 
**RF-PRO-003: Desactivar Producto** 
Descripción: El Administrador debe poder desactivar temporalmente productos del 
catálogo, evitando que se vendan o aparezcan en listados activos, pero manteniendo 
todo el historial de movimientos, ventas, y datos del producto para reportes y auditoría. 
**RF-PRO-004: Listar Productos** 
Descripción: El sistema debe proporcionar una vista completa del catálogo de 
productos con capacidades de búsqueda, filtrado, paginación y ordenamiento, 
permitiendo a los usuarios visualizar, gestionar y realizar acciones sobre los productos 
de manera eficiente tanto en el módulo administrativo como en el punto de venta.  
**RF-PRO-005: Cargar Imágenes de Producto** 
Descripción: El Administrador debe poder subir múltiples imágenes para cada 
producto, definir cuál es la imagen principal, ordenar las imágenes, y agregar textos 
alternativos para accesibilidad. Las imágenes son fundamentales para el storefront y 
para identificación visual en el sistema. 
**RF-PRO-006: Crear Categoría de Productos** 
Descripción: El Administrador debe poder crear categorías para clasificar productos, 
facilitando la navegación en el storefront, reportes segmentados, y gestión del catálogo. 
Las categorías son específicas del negocio y reflejan su organización de productos. 
**RF-PRO-007: Editar Categoría de Productos** 
Descripción: El Administrador debe poder editar categorías existentes, modificando 
nombre, descripción, imagen y orden de visualización. 
**RF-PRO-008: Listar Categorías de Productos** 
Descripción: El sistema debe mostrar lista de todas las categorías con información 
resumida y opciones de filtrado. 
**RF-PRO-009: Desactivar Categoría** 
Descripción: El sistema debe permitir desactivar categorías que ya no se utilizan, 
manteniendo el historial de productos que la tuvieron asignada. 
**RF-PRO-010: Crear Marca** 
Descripción: El Administrador debe poder registrar marcas de productos, almacenando 
información como país de origen, logo, y descripción. Las marcas ayudan a clasificar 
productos y son importantes para búsquedas y filtros en el storefront. 
**RF-PRO-011: Editar Marca** 
Descripción: El Administrador debe poder editar marcas existentes, modificando 
nombre, país de origen, logo y descripción. 
**RF-PRO-012: Listar Marcas** 
Descripción: El sistema debe mostrar lista de todas las marcas con información 
resumida y opciones de filtrado. 
**RF-PRO-013: Desactivar Marca** 
Descripción: El sistema debe permitir desactivar marcas que ya no se comercializan, 
manteniendo el historial de productos asociados. 
**RF-PRO-014: Crear Combo Promocional** 
Descripción: El Administrador debe poder crear combos o packs promocionales que 
agrupan múltiples productos con un precio especial, como "Pack Chilcano" (pisco + 
ginger ale + limones) o "Six Pack Cervezas". Los combos son productos virtuales que 
se descomponen en productos individuales al venderse. 
**RF-PRO-015: Editar Combo** 
Descripción: El Administrador debe poder modificar información de combos 
existentes, actualizar precios, cambiar vigencia, y ajustar configuraciones, sin afectar 
ventas históricas del combo. 
**RF-PRO-016: Definir Composición de Combo** 
Descripción: El Administrador debe poder agregar productos al combo, especificar 
cantidades de cada producto, y gestionar la lista completa de productos incluidos. La 
composición define qué productos se descuentan del inventario al vender el combo. 
**RF-PRO-017: Desactivar Combo** 
Descripción: El sistema debe permitir desactivar combos promocionales que ya no 
están vigentes o no se ofrecen. 
**RF-PRO-018: Configurar Visibilidad en Storefront** 
Descripción: El Administrador debe poder controlar qué productos y combos son 
visibles en la tienda online (storefront), destacar productos específicos, y organizar la 
presentación para maximizar ventas online.  
**RF-PRO-019: Búsqueda de Productos** 
Descripción: El sistema debe proporcionar funcionalidad de búsqueda avanzada de 
productos permitiendo buscar por múltiples criterios (nombre, código, código de barras, 
categoría, marca, tipo), con resultados rápidos y relevantes para uso en POS, gestión de 
inventario, y administración del catálogo. 
**RF-PRO-020: Importar Productos Masivamente** 
Descripción: El Administrador debe poder importar múltiples productos desde un 
archivo Excel o CSV, facilitando la carga inicial del catálogo o actualización masiva de 
datos. El sistema valida los datos y genera reporte de errores para corrección. 
**RF-PRO-021: Crear Promoción** 
Descripción: El Administrador debe poder crear promociones con descuentos 
automáticos que se aplican a productos o categorías específicas durante un período 
definido, facilitando campañas de marketing y liquidaciones. 
**RF-PRO-022: Configurar Reglas de Promoción** 
Descripción: Administrador debe poder definir reglas avanzadas para promociones, 
como "2x1", "3x2", "Descuento escalonado por volumen", o "Regalo al comprar X 
productos", permitiendo estrategias de marketing sofisticadas. 

**1.3.2. Submódulo: Control de Inventario** 

**RF-INV-001: Visualizar Stock Consolidado por Producto** 
Descripción: El sistema debe proporcionar una vista consolidada del inventario 
disponible por producto, agregando el stock de todos los lotes activos en todos los 
almacenes de la sede o del negocio completo. Debe mostrar cantidades físicas, valores 
totales, y alertas visuales para stock bajo o crítico. 
**RF-INV-002: Registrar Entrada de Lote de Inventario** 
Descripción: El sistema debe permitir registrar la entrada de un nuevo lote de 
inventario al almacén, capturando toda la información necesaria para el control FIFO y 
la trazabilidad: fecha de vencimiento, costo unitario, proveedor, número de lote del 
fabricante, etc. 
**RF-INV-003: Aplicar Sistema FIFO para Descuento Automático de Inventario** 
Descripción: El sistema debe implementar el método FIFO (First In, First Out) para 
descontar automáticamente el inventario desde los lotes más antiguos (próximos a 
vencer) primero, garantizando la rotación adecuada de productos perecederos y 
minimizando mermas por vencimiento. 
**RF-INV-004: Configurar y Recibir Alertas de Stock Bajo** 
Descripción: El sistema debe monitorear continuamente los niveles de inventario y 
generar alertas automáticas cuando un producto alcance su nivel de stock mínimo o 
crítico, permitiendo a los administradores reaccionar antes de quedarse sin stock. 
**RF-INV-005: Generar Alertas de Productos Próximos a Vencer** 
Descripción: El sistema debe monitorear las fechas de vencimiento de todos los lotes 
activos y generar alertas preventivas cuando se acerque la fecha de vencimiento, 
permitiendo implementar estrategias de salida (descuentos, donaciones) antes de que el 
producto expire. 
**RF-INV-006: Registrar Movimientos de Inventario (Ajustes Manuales)** 
Descripción: El sistema debe permitir registrar movimientos de inventario manuales 
para ajustar discrepancias encontradas en auditorías físicas, registrar mermas, productos 
dañados, muestras, o reclasificaciones, manteniendo trazabilidad completa de todas las 
operaciones. 
**RF-INV-007: Transferir Inventario Entre Almacenes** 
Descripción: El sistema debe facilitar la transferencia de productos entre diferentes 
almacenes de la misma sede o entre sedes del mismo negocio, manteniendo trazabilidad 
completa de los movimientos y asegurando que el inventario consolidado se actualice 
correctamente. 
**RF-INV-008: Rastrear Trazabilidad Completa de Lotes** 
Descripción: El sistema debe proporcionar trazabilidad completa desde el ingreso de un 
lote hasta su salida final (venta, merma, etc.), permitiendo rastrear qué lote específico se 
vendió en cada transacción, a qué cliente, en qué fecha, y quién fue el responsable de 
cada movimiento. 
**RF-INV-009: Registrar Producto Vencido o Dañado** 
Descripción: El sistema debe permitir registrar productos vencidos o dañados, 
facilitando su retiro del inventario disponible, documentación de la pérdida, y gestión de 
su disposición final (descarte, devolución a proveedor, donación). 

**1.3.3. Submódulo: Compras y Proveedores** 

**RF-COP-001: Crear Proveedor** 
Descripción: El sistema debe permitir registrar proveedores en el catálogo, incluyendo 
información de contacto, términos comerciales, y categorías de productos que 
suministran. 
**RF-COP-002: Editar Proveedor** 
Descripción: El Administrador debe poder editar información de proveedores 
existentes, modificando datos de contacto y términos comerciales. 
**RF-COP-003: Desactivar Proveedor** 
Descripción: El sistema debe permitir desactivar proveedores con los que ya no se 
trabaja, manteniendo el historial de compras. 
**RF-COP-004: Crear Orden de Compra** 
Descripción: El sistema debe permitir crear órdenes de compra formales a proveedores, 
especificando productos, cantidades, precios, términos de entrega y pago. La orden de 
compra sirve como documento vinculante y punto de partida para el proceso de 
recepción de mercancía. 
**RF-COP-005: Aprobar/Rechazar Órdenes de Compra** 
Descripción: El sistema debe implementar un flujo de aprobación para órdenes de 
compra que superen ciertos umbrales de monto o criterios especiales, permitiendo a 
gerentes o directores revisar y aprobar/rechazar antes de enviar al proveedor, 
garantizando control de gastos y cumplimiento de presupuestos. 
**RF-COP-006: Recibir Mercancía y Generar Entrada de Inventario** 
Descripción: El sistema debe facilitar el proceso de recepción de mercancía desde 
órdenes de compra, permitiendo al almacenero verificar cantidades recibidas vs. 
solicitadas, identificar discrepancias, registrar números de lote y fechas de vencimiento, 
y generar automáticamente las entradas de inventario correspondientes. 
**RF-COP-007: Crear Devolución a Proveedor** 
Descripción: El sistema debe permitir crear nuevas devoluciones de productos a 
proveedores cuando se detectan productos defectuosos o no conformes. 
**RF-COP-008: Editar Devolución a Proveedor** 
Descripción: El sistema debe permitir modificar devoluciones existentes mientras no 
hayan sido finalizadas. 
**RF-COP-009: Listar Devoluciones a Proveedores** 
Descripción: El sistema debe permitir visualizar todas las devoluciones registradas con 
filtros de búsqueda. 
**RF-COP-010: Consultar Cuentas por Pagar** 
Descripción: El sistema debe proporcionar una vista consolidada de todas las 
obligaciones pendientes de pago con proveedores, mostrando facturas pendientes, 
plazos de vencimiento, y permitiendo la gestión eficiente del flujo de caja y relaciones 
con proveedores. 
**RF-COP-011: Registrar Compra Directa** 
Descripción: El sistema debe permitir registrar compras realizadas directamente sin 
orden de compra previa, típicamente para compras urgentes, de bajo monto, o a 
proveedores ocasionales, generando automáticamente la entrada de inventario y la 
cuenta por pagar correspondiente. 
**RF-COP-012: Registrar Pago a Proveedor** 
Descripción: El sistema debe permitir registrar pagos a proveedores para saldar o 
abonar a cuentas por pagar, permitiendo pagos totales o parciales, múltiples formas de 
pago, y generando comprobantes de egreso con trazabilidad completa. 

**1.4. MÓDULO IV: OPERACIONES DE VENTA**

**1.4.1. Submódulo: Punto de Venta (POS)** 

**RF-VTA-001: Abrir y Cerrar Sesión de Caja** 
Descripción: El sistema debe permitir a los cajeros abrir y cerrar sesiones diariamente 
para garantizar el control del flujo de efectivo. 
● Apertura: Registro de caja/terminal, monto de efectivo inicial (fondo de 
cambio), desglose de denominaciones y validación de permisos. 
● Cierre: Registro de efectivo contado físicamente, cálculo automático de 
diferencias (sobrantes/faltantes), generación de reporte de cierre y bloqueo de 
nuevas ventas tras el cierre. 
● Validaciones: Alertas por diferencias mayores a los límites configurados (ej. > 
$5 USD) y monitoreo de sesiones abiertas por más de 12 horas. 
**RF-VTA-002: Registrar Venta en POS con Verificación de Edad** 
Descripción: Interfaz de punto de venta para registrar transacciones al por menor con 
validaciones legales obligatorias. 
● Búsqueda: Por nombre, código de barras o SKU. 
● Verificación de Alcohol: Bloqueo automático de venta de productos alcohólicos 
si la hora actual está fuera del horario permitido o si no se confirma la mayoría 
de edad del cliente (check de verificación obligatorio). 
● Stock: Validación de disponibilidad en tiempo real. 
**RF-VTA-003: Procesar Múltiples Formas de Pago** 
Descripción: El sistema debe soportar el procesamiento de pagos mixtos en una misma 
venta y el cálculo de vueltos. 
● Efectivo (Soles). 
● Tarjetas de Crédito/Débito (Visa, Mastercard, AMEX). 
● Transferencias Bancarias (con número de operación). 
● Billeteras Digitales (Yape, Plin - con generación de QR). 
● Crédito (para clientes autorizados). 
● Vales/Gift Cards. 
**RF-VTA-004: Emitir Comprobantes de Pago (Boleta/Factura)** 
Descripción: Generación de comprobantes electrónicos cumpliendo con la normativa 
de SUNAT y envío automático a OSE/SUNAT. 
● Boleta de Venta: Para consumidores finales (DNI opcional). 
● Factura Electrónica: Validación de RUC (11 dígitos). 
● Notas de Crédito/Débito: Para anulaciones o correcciones. 
● Inclusión de código QR y Hash de firma digital. 
**RF-VTA-005: Aplicar Descuentos y Promociones Automáticas** 
Descripción: Evaluación automática del carrito de compras para aplicar reglas de 
negocio configuradas. 
● Descuentos por producto (fijo o porcentual). 
● Promociones por cantidad (3x2, 2da unidad al 50%). 
● Combos/Bundles y Happy Hour (por horario). 
● Validación para asegurar que el precio no baje del costo. 
**RF-VTA-006: Administrar Devoluciones y Reembolsos** 
Descripción: Procesamiento de devoluciones validando condiciones sanitarias (sellos 
intactos en licores). 
● Búsqueda de venta original por ticket o cliente. 
● Opciones de resolución: Reembolso (mismo método de pago), Cambio de 
producto o Nota de crédito. 
● Actualización de inventario: Reingreso de productos aptos o registro de 
mermas. 
**RF-VTA-007: Suspender y Recuperar Ventas** 
Descripción: Permite poner en espera una transacción actual para atender a otro cliente 
y recuperarla posteriormente. 
● Guardado del carrito con estado "suspendida". 
● Generación de código de recuperación. 
● Liberación de la interfaz para nueva venta inmediata. 
**RF-VTA-008: Generar Reportes de Ventas del Día** 
Descripción: Sistema de reportes para el análisis operativo diario y cierre de turno. 
● Reporte de Cierre de Caja (detallado por sesión). 
● Reporte por Cajero (desempeño y velocidad). 
● Reporte por Producto/Categoría (Ranking de más vendidos). 
● Reporte por Forma de Pago (Efectivo vs Digital). 
● Exportación en PDF y Excel. 
**RF-VTA-009: Integrar con Impresora Fiscal/Térmica** 
Descripción: Integración con hardware de impresión para emisión física de 
documentos. 
● Impresión de Ticket de venta y Comprobantes. 
● Impresión de Reportes X y Z (Cierres). 
● Impresión de Comandas para cocina/bar. 
● Soporte para impresoras USB, Red y Bluetooth. 
**RF-VTA-010: Gestionar Caja Chica y Gastos Menores** 
Descripción: Registro de salidas de dinero directamente desde el POS para gastos 
operativos. 
● Registro de monto, categoría (movilidad, insumos) y descripción. 
● Captura de comprobante de respaldo (foto/scan). 
● Descuento automático del efectivo disponible en el arqueo de caja. 

**1.4.2. Submódulo: Gestión de Mesas y Cuentas** 

**RF-VTA-011: Abrir Cuenta de Mesa** 
Descripción: Inicio del servicio en mesa registrando datos esenciales. 
● Asignación de número de comensales. 
● Vinculación de mesero responsable. 
● Asignación de cliente (opcional para frecuentes). 
● Generación de número de cuenta único. 
**RF-VTA-012: Agregar Productos a Cuenta de Mesa** 
Descripción: Interfaz de toma de pedidos (comandado) por parte del mesero. 
● Selección de productos con modificadores (término, temperatura, sin hielo). 
● Generación automática de comandas. 
● Validación de stock y restricciones horarias de alcohol al momento del pedido. 
**RF-VTA-013: Dividir y Transferir Cuentas** 
Descripción: Gestión flexible de la cuenta para facilitar el pago. 
● División: Por número de personas, por ítems consumidos o por montos fijos. 
● Transferencia: Mover productos de una mesa a otra o cambiar la cuenta 
completa a otra ubicación. 
**RF-VTA-014: Solicitar y Cerrar Cuenta** 
Descripción: Proceso de finalización del servicio en mesa. 
● Generación de pre-cuenta (estado de cuenta) para el cliente. 
● Cálculo y registro de propinas (sugeridas o manuales). 
● Aplicación de descuentos de cortesía (con autorización). 
● Procesamiento del pago y liberación de la mesa. 

**1.4.3. Submódulo: Gestión de Pedidos** 

**RF-VTA-015: Crear Pedido con Múltiples Modalidades** 
Descripción: Creación centralizada de pedidos seleccionando el tipo de servicio. 
● Delivery: Con dirección, cálculo de costo de envío y asignación de repartidor. 
● Pickup: Con hora programada de recojo. 
● Mesa/Barra: Atención presencial. 
● Validación de cobertura de zona para delivery. 
**RF-VTA-016: Confirmar y Procesar Pedidos** 
Descripción: Flujo de gestión para aceptar pedidos entrantes. 
● Validación de stock y confirmación de recepción. 
● Estimación de tiempo de entrega/preparación. 
● Notificación automática al cliente (SMS/WhatsApp). 
● Asignación automática o manual de repartidor (para delivery). 
**RF-VTA-017: Rastrear Estado de Pedido en Tiempo Real** 
Descripción: Seguimiento del ciclo de vida del pedido con trazabilidad completa. 
● Estados: Pendiente > Confirmado > En Preparación > Listo > En Camino > 
Entregado > Cancelado. 
● Vista tipo Kanban para el operador. 
● Link de seguimiento público para el cliente. 
**RF-VTA-018: Configurar Zonas y Costos de Delivery** 
Descripción: Administración geoespacial de la cobertura de reparto. 
● Definición de zonas por polígonos, distritos o radio (km). 
● Configuración de tarifas (fijas o por distancia) y montos mínimos de compra. 
● Validación automática de direcciones al crear pedidos. 
**RF-VTA-019: Asignar y Gestionar Repartidores** 
Descripción: Control de la flota de motorizados. 
● Registro de repartidores (datos, vehículo, placa). 
● App móvil para repartidor: Recepción de pedidos, navegación GPS y 
confirmación de entrega. 
● Monitoreo de ubicación y disponibilidad en tiempo real. 
**RF-VTA-020: Gestionar Cancelaciones y Devoluciones de Pedidos** 
Descripción: Manejo de excepciones en el flujo de pedidos. 
● Cancelación por cliente o negocio con registro de motivo. 
● Gestión de reembolsos automáticos según política y estado del pedido. 
● Restricción de devolución de alcohol (solo defectos de fábrica). 
**RF-VTA-021: Reportes y Analítica de Pedidos** 
Descripción: Dashboard de inteligencia de pedidos. 
● Volumen de pedidos por canal y modalidad. 
● Tiempos promedio de entrega y preparación. 
● Mapa de calor de zonas de entrega. 
● Análisis de rentabilidad (ventas vs comisiones de plataformas). 

**1.4.4. Submódulo: Gestión de Clientes** 

**RF-VTA-022: Administrar Quejas y Reclamos** 
Descripción: Sistema de atención al cliente (SAC). 
● Registro multicanal de incidencias (Presencial, Web, Teléfono). 
● Flujo de resolución con asignación de responsables y SLA (tiempos límite). 
● Historial de soluciones y compensaciones otorgadas. 
**RF-VTA-023: Gestionar clientes** 
Descripción: Permite al sistema administrar la información de los clientes mediante 
operaciones de creación, consulta, actualización y eliminación de registros, facilitando 
el control y mantenimiento de los datos de clientes. 

**1.5. MÓDULO V: FACTURACIÓN Y FINANZAS** 
Facturación y Finanzas proporciona un sistema integral para la gestión fiscal, contable y 
financiera del negocio multi-tenant de licorerías. Este módulo garantiza el cumplimiento 
normativo con SUNAT, facilita la administración de gastos e ingresos. Está diseñado 
para manejar facturación electrónica, control de gastos, conciliación bancaria, reportes 
financieros, y gestión de tributos, asegurando la transparencia fiscal y la salud financiera 
del negocio. 

**1.5.1. Submódulo: Facturación Electrónica** 

**RF-FIN-001: Gestionar Series de Comprobantes Electrónicos** 
Descripción: Este requerimiento define la administración de las series de comprobantes 
utilizadas por el sistema. Incluye la configuración de series por tipo de comprobante y 
punto de emisión, asegurando numeración correlativa, unicidad y cumplimiento 
normativo. Esta capacidad permite un control ordenado de la facturación y evita 
inconsistencias tributarias. 
**RF-FIN-002: Gestionar Emisión de Comprobantes Electrónicos** 
Descripción: Este requerimiento cubre la emisión de boletas y facturas electrónicas. El 
sistema registra los datos del cliente, productos, impuestos y totales, generando 
comprobantes válidos conforme a la normativa vigente. Incluye la validación de 
información fiscal y el almacenamiento del comprobante como parte de la operación de 
venta. 
**RF-FIN-003: Enviar Comprobantes a SUNAT** 
Descripción: Permite el envío automático de comprobantes electrónicos a SUNAT. El 
sistema gestiona la comunicación, procesa las respuestas de aceptación, observación o 
rechazo, y registra el estado de cada comprobante, asegurando cumplimiento tributario 
y reduciendo intervención manual. 
**RF-FIN-004: Consultar y Controlar Estado de Comprobantes** 
Descripción: Este requerimiento permite consultar el estado de los comprobantes 
enviados a SUNAT, así como su estado interno en el sistema. Facilita la detección 
temprana de errores y la corrección oportuna de observaciones, manteniendo control 
fiscal y operativo. 
**RF-FIN-005: Gestionar Notas de Crédito y Débito** 
Descripción: Define la capacidad del sistema para emitir notas de crédito y débito 
asociadas a comprobantes previamente emitidos. Permite corregir, anular parcialmente o 
ajustar montos de operaciones, manteniendo coherencia contable, trazabilidad y 
respaldo tributario. 
**RF-FIN-006: Gestionar Anulación de Comprobantes** 
Descripción: Este requerimiento controla la anulación de comprobantes electrónicos 
dentro de los plazos y condiciones permitidos. El sistema registra la anulación, ajusta 
los estados financieros relacionados y mantiene evidencia para fines de auditoría. 
**RF-FIN-007: Gestionar Reimpresión y Consulta de Comprobantes** 
Descripción: Permite la visualización y reimpresión de comprobantes emitidos sin 
alterar la información original. El sistema garantiza integridad documental y facilita la 
entrega de copias digitales o físicas a clientes y áreas internas. 

**1.5.2. Submódulo: Devoluciones y Ajustes** 

**RF-FIN-008: Registrar Solicitudes de Devolución** 
Descripción: Este requerimiento permite registrar devoluciones de productos por parte 
de los clientes, asociándolas a la venta original. El sistema controla cantidades, montos 
y productos involucrados, asegurando trazabilidad completa de la operación. 
**RF-FIN-009: Gestionar Motivos y Aprobación de Devoluciones** 
Descripción: Define la clasificación de devoluciones según motivos predefinidos y su 
proceso de aprobación. El sistema asegura que solo las devoluciones autorizadas 
impacten en inventario y finanzas, fortaleciendo el control interno. 
**RF-FIN-010: Generar Ajustes Financieros por Devolución** 
Descripción: Permite la generación automática de los ajustes necesarios tras una 
devolución, incluyendo notas de crédito y reversos financieros. El sistema vincula estos 
ajustes al comprobante original, manteniendo consistencia contable y tributaria. 
**RF-FIN-011: Gestionar Reembolsos al Cliente** 
Descripción: Este requerimiento gestiona la devolución del dinero al cliente según el 
medio de pago utilizado. El sistema registra el reembolso, actualiza el estado de la 
operación y mantiene trazabilidad financiera. 
**RF-FIN-012: Reintegrar Productos Devueltos al Inventario** 
Descripción: Permite reincorporar al inventario los productos devueltos que se 
encuentren aptos para su venta. El sistema actualiza automáticamente los niveles de 
stock, asegurando coherencia entre inventario, ventas y finanzas. 

**1.5.3. Submódulo: Gestión de Gastos** 

**RF-FIN-013: Registrar Gastos Operativos** 
Descripción: Este requerimiento permite registrar los gastos asociados a la operación 
del negocio. El sistema almacena información clave como monto, fecha, concepto y 
proveedor, integrando el gasto a los registros financieros. 
**RF-FIN-014: Clasificar y Documentar Gastos** 
Descripción: Define la categorización de gastos y la asociación de comprobantes de 
respaldo. El sistema garantiza trazabilidad, facilita auditorías y mejora el análisis de 
costos operativos. 
**RF-FIN-015: Aprobar Gastos Operativos** 
Descripción: Este requerimiento establece el proceso de validación y aprobación de 
gastos antes de su contabilización final. El sistema permite controlar el uso de recursos 
y aplicar políticas internas de autorización. 
**RF-FIN-016: Gestionar Gastos Recurrentes** 
Descripción: Permite configurar gastos periódicos como servicios, alquileres o 
suscripciones. El sistema automatiza su registro según la frecuencia definida, 
asegurando continuidad y control financiero. 

**1.6. MÓDULO VI: E-COMMERCE Y REPORTES** 
Proporciona una plataforma completa de comercio electrónico integrada con el sistema 
POS, permitiendo a las licorerías realizar ventas online, gestionar catálogos digitales, 
procesar pedidos web. Además, incluye un motor de reportes que transforma los datos 
operativos mediante dashboards  

**1.6.1. Submódulo: Configuración de Storefront (Admin)** 

**RF-ECO-001: Configurar Tienda Online** 
Descripción: El sistema debe permitir configurar la tienda, información del negocio, 
políticas comerciales, métodos de pago y entrega, integrándose automáticamente con el 
inventario, catálogo de productos, y sistema de pedidos del POS. 
**RF-ECO-002: Configurar Métodos de Pago Online** 
Descripción: Permite al sistema habilitar y gestionar diversos métodos de pago para las 
compras, incluyendo tarjetas de crédito y débito, transferencias bancarias, Yape/Plin, 
PagoEfectivo, billeteras digitales y pago contra entrega (exclusivo para pedidos por 
delivery), brindando flexibilidad y comodidad al cliente. 
**RF-ECO-003: Configurar Métodos de entrega**  
Descripción: Permite al sistema definir y administrar las modalidades de entrega de los 
pedidos, incluyendo delivery a domicilio con configuración de zonas, costos y tiempos 
estimados, así como el recojo en tienda (pickup), estableciendo sedes disponibles y 
horarios de atención para el recojo. 
**RF-ECO-004: Configurar Horarios de Venta Online** 
Descripción: Este requerimiento permite configurar los horarios en los que la tienda 
online acepta pedidos, con especial énfasis en las restricciones legales de venta de 
alcohol. El sistema debe validar que las compras solo se procesan dentro de horarios 
permitidos por ley. 
**RF-ECO-005: Activar/Desactivar Tienda Online** 
Descripción: Este requerimiento permite al administrador controlar el estado operativo 
de la tienda online, pudiendo activarla, desactivarla temporalmente, o ponerla en modo 
mantenimiento con mensajes personalizados para los clientes. 

**1.6.2. Submódulo: Tienda Online (Cliente)** 

**RF-ECO-006: Verificación de Mayoría de Edad** 
Descripción: Sistema de verificación de edad para cumplir con regulaciones legales de 
venta de alcohol. Implementa una barrera que requiere confirmación de mayoría de 
edad antes de permitir el acceso a detalles de productos. 
**RF-ECO-007: Navegar Catálogo de Productos** 
Descripción: Permite a los clientes explorar el catálogo completo de productos 
disponibles en la tienda online, con información relevante para la toma de decisión de 
compra. 
**RF-ECO-008: Filtrar Productos por Categoría/Marca/precio** 
Descripción: Sistema de filtros para segmentar el catálogo y facilitar la búsqueda de 
productos específicos por categorías, marcas y rango de precios. 
**RF-ECO-009: Buscar Productos** 
Descripción: Búsqueda textual que permite a los clientes encontrar productos por 
nombre, descripción, marca o características específicas. 
**RF-ECO-010: Ver Detalle de Producto** 
Descripción: Página de detalle completa del producto con toda la información necesaria 
para decisión de compra, incluyendo precio, disponibilidad, características y 
restricciones legales. 
**RF-ECO-011: Agregar Producto al Carrito** 
Descripción: Funcionalidad para agregar productos al carrito de compras con 
validación de stock, edad (productos alcohólicos) y disponibilidad en la tienda online. 
**RF-ECO-012: Modificar Carrito de Compras** 
Descripción: Permite al cliente actualizar cantidades, eliminar productos o vaciar 
completamente el carrito antes de proceder al checkout. 
**RF-ECO-013: Seleccionar Modalidad de Entrega** 
Descripción: Permite al cliente elegir entre las modalidades de entrega disponibles: 
delivery a domicilio o recoger en tienda, con cálculo de costos y tiempo. 
**RF-ECO-014: Confirmar Mayoría de Edad en Checkout** 
Descripción: Confirmación de mayoría de edad en el momento del checkout como 
medida de seguridad adicional para pedidos con productos alcohólicos. 
**RF-ECO-015: Procesar Pago Online** 
Descripción: Integración con pasarelas de pago para procesar transacciones online de 
forma segura, con registro de intentos, confirmaciones y reconciliación financiera. 
**RF-ECO-016: Rastrear Pedido** 
Descripción: Sistema de seguimiento que permite a los clientes visualizar el estado 
actual de su pedido, desde la confirmación hasta la entrega. 
Estados adicionales: 
● Pendiente de pago: Esperando confirmación de pago 
● Pago verificado: Pago confirmado, listo para procesar 
● En preparación: Productos siendo preparados 
● Listo para envío: Empaquetado, esperando repartidor 
● En camino: Repartidor en ruta 
● Listo para recojo: Cliente puede recoger 
● Entregado: Pedido completado 
● Cancelado: Por cliente o negocio 
**RF-ECO-017: Registro y Login de Cliente** 
Descripción: Sistema completo de autenticación y gestión de cuentas de cliente, 
permitiendo registro, login, recuperación de contraseña y gestión de perfil para facilitar 
compras recurrentes. 

**1.6.3. Submódulo: Reportes y Análisis** 

**RF-ECO-018: Reporte de Ventas** 
Descripción: Sistema integral de reportes de ventas con múltiples dimensiones de 
análisis para evaluar el desempeño comercial y tomar decisiones basadas en datos 
históricos y tendencias. 
● Temporal: Ventas por día,semana, mes. 
● Por Sede: Ventas por sede 
● Comparativa entre sedes 
● Identificación de sede con mejores ventas 
**RF-ECO-019: Reporte de Productos Más Vendidos** 
Descripción: Análisis de productos top por volumen de ventas, identificando 
bestsellers(Productos más vendidos), productos de alta rotación y oportunidades de 
optimización del catálogo. 
**RF-ECO-020: Reporte de Inventario** 
Descripción: Reporte del estado del inventario con margen de ganancia o sea, precio de 
costo con el precio de venta, análisis de stock disponible, para control de capital de 
trabajo y toma de decisiones de compra. 
**RF-ECO-021: Reporte de Movimientos de Inventario** 
Descripción: Trazabilidad completa de todos los movimientos de inventario (entradas, 
salidas, ajustes, mermas) con registro auditable para control de pérdidas, análisis de 
causas y reconciliación. 
**RF-ECO-022: Reporte de Compras y Gastos** 
Descripción: Consolidación de todas las compras a proveedores y gastos operativos, 
con análisis de costos, proveedores. 
**RF-ECO-023: Reporte de Boletas Emitidas** 
Descripción: Genera reporte consolidado de boletas de venta emitidas, con análisis de 
ventas a consumidores finales, resumen diario para SUNAT, y seguimiento de 
comprobantes enviados. Permite identificar patrones de consumo, horarios pico y 
control de series de boletas. 
**RF-ECO-024: Reporte de Facturas Emitidas** 
Descripción: Genera reporte detallado de todas las facturas emitidas en un período 
específico, con análisis de montos, estados, clientes y cumplimiento tributario ante 
SUNAT. Permite identificar tendencias de facturación, clientes corporativos más 
importantes y seguimiento de documentos pendientes de envío. 
**RF-ECO-025: Exportar Reportes (Excel, PDF)** 
Descripción: Funcionalidad de exportación de cualquier reporte generado en múltiples 
formatos para distribución, presentaciones. 

2. REQUISITOS NO FUNCIONALES 

2.1. REQUISITOS DE SEGURIDAD 
El sistema Drink Go implementa medidas de seguridad esenciales para proteger la 
información del negocio. Toda la comunicación entre cliente y servidor utiliza HTTPS 
para encriptar datos sensibles. Las contraseñas de los usuarios se almacenan con hash 
bcrypt, nunca en texto plano. 
El sistema implementa control de acceso basado en roles donde cada usuario tiene 
permisos específicos según su función (administrador, vendedor, almacenero). Las 
sesiones se manejan con tokens únicos que expiran después de inactividad, obligando a 
iniciar sesión nuevamente. Para la arquitectura multi-tenant, cada negocio está 
completamente aislado. 

2.2. REQUISITOS DE ESCALABILIDAD (MULTI-TENANT) 
La arquitectura multi-tenant permite que múltiples negocios usen el mismo sistema de 
forma independiente y segura. Cada negocio se identifica con id único que se valida en 
todas las operaciones, asegurando que solo vean y modifiquen sus propios datos. 
El sistema puede soportar negocios diferentes en la misma instancia, cada uno con sus 
propias sedes, productos, usuarios y configuraciones.  

2.3. REQUISITOS DE DISPONIBILIDAD 
Drink Go está diseñado para mantener el negocio operativo el mayor tiempo posible. 

2.4. REQUISITOS DE USABILIDAD 
DrinkGo prioriza la facilidad de uso para que cualquier persona pueda operar el sistema 
con capacitación mínima. La interfaz es responsive y dinámica. 

2.5. REQUISITOS DE MANTENIBILIDAD 
Drink Go estará desarrollado siguiendo buenas prácticas que facilitan su mantenimiento 
y evolución. El código fuente está organizado en capas (controladores, servicios, 
repositorios) siguiendo el patrón MVC, haciendo más fácil localizar y modificar 
funcionalidades específicas. 
