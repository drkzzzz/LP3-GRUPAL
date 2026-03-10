# MEMORIA DESCRIPTIVA

## DRINKGO: APLICACIÓN INFORMÁTICA DISTRIBUIDA PARA NEGOCIOS DE LICORERÍA

Este software ha sido desarrollado íntegramente en base a la experiencia del equipo de desarrollo y tiene como objetivo cumplir todas las necesidades técnico/administrativas en cuanto a las necesidades de una empresa de venta de licores, tanto por el lado del administrador de la empresa, como por el lado del usuario final (cliente).

Conforme a que es nuestra intención que pueda conocer —aunque sea someramente— los alcances de este desarrollo, lo invitamos a descubrir si se adapta a sus necesidades, y a que estamos convencidos que gran parte de sus carencias se encuentran resueltas en este desarrollo.

A continuación, detallamos los módulos existentes en la vista del **Super Administrador del sistema**:

---

**Módulo de Dashboard Plataforma:** Este módulo comprende un panel ejecutivo con métricas clave de toda la plataforma. Solo será visible para el Super Administrador del sistema.

- **Resumen General:** Aquí se centraliza la visualización del total de negocios activos, suspendidos y cancelados en la plataforma. Se presentan los ingresos mensuales y anuales, gráficos de crecimiento de ventas, métricas de retención de clientes, negocios nuevos por mes y tasa de conversión de planes.

- **Métricas en Tiempo Real:** Este apartado muestra la cantidad de negocios activos en el momento, las transacciones realizadas y el uso de recursos por negocio, junto con alertas del sistema.

**Módulo de Gestión de Negocios (Tenants):** Este módulo se encarga de la administración completa de todas las licorerías registradas en la plataforma. Solo será visible para el Super Administrador del sistema.

- **Gestión de Negocios:** Aquí se centraliza el control del registro, actualización, visualización y eliminación de los negocios (licorerías) registrados en la plataforma. El Super Administrador puede buscar por RUC, razón social o email y ver indicadores de estado de cada negocio (activo, suspendido, moroso, cancelado).

- **Gestión de Sedes:** Este apartado se encarga de administrar las sedes vinculadas a cada negocio registrado. Permite registrar, editar y desactivar sedes asociadas a cada licorería. Cada sede cuenta con su dirección completa, teléfono, email de contacto y almacenes asignados. La creación de sedes está controlada por el límite del plan de suscripción del negocio.

- **Detalle de Negocio:** En esta sección se puede visualizar la información completa del negocio, su historial de suscripciones, usuarios activos, estadísticas de uso (productos, ventas, almacenes), facturas de suscripción y logs de actividad.

**Módulo de Planes de Suscripción:** En este módulo se gestionan los planes comerciales de la plataforma SaaS. Solo será visible para el Super Administrador del sistema.

- **Gestión de Planes:** En este apartado se administra la creación, edición, activación y desactivación de los planes de suscripción. Cada plan define un precio, moneda, periodo de facturación (mensual/anual) y límites operativos como máximo de sedes, usuarios, productos y almacenes por sede.

- **Funcionalidades por Plan:** Cada plan habilita o restringe funcionalidades del sistema como: Punto de Venta (POS), Tienda Online, Delivery, Mesas, Facturación Electrónica, Multi-Almacén, Reportes Avanzados y Acceso API.

**Módulo de Facturación de Suscripciones:** Este módulo controla la facturación recurrente y los pagos de la plataforma. Solo será visible para el Super Administrador del sistema.

- **Lista de Facturas:** En esta sección se visualizan todas las facturas emitidas, con filtros por estado (pendiente, pagada, vencida, anulada), búsqueda por negocio, fecha y monto, e indicadores visuales de morosidad.

- **Control de Morosidad:** Este apartado muestra los negocios con facturas vencidas, los días de mora por negocio, las acciones automáticas configuradas y los emails de recordatorio enviados. Se puede suspender un negocio por morosidad.

**Módulo de Configuración Global:** En este módulo se gestionan las configuraciones que afectan a toda la plataforma. Solo será visible para el Super Administrador del sistema.

- **Parámetros Globales:** Aquí se configuran los impuestos por defecto (IGV 18%), monedas soportadas, métodos de pago disponibles, integraciones externas (SUNAT, pasarelas de pago) y políticas de seguridad (tiempo de sesión, intentos de login).

- **Configuración de Emails:** Este componente permite administrar las plantillas de correos del sistema, la configuración SMTP y los emails de notificaciones automáticas.

**Módulo de Auditoría y Logs:** En este módulo se registran y consultan todas las actividades críticas del sistema. Solo será visible para el Super Administrador del sistema.

- **Registro de Auditoría:** En este apartado se muestran todas las acciones críticas realizadas, con filtros por usuario, fecha, tipo de acción (creación, edición, eliminación), módulo afectado y severidad (info, warning, critical). Cada evento detalla el usuario responsable, la fecha y hora exacta, la IP de origen, la descripción de la acción y los datos antes/después del cambio.

- **Gestión de Usuarios Bloqueados:** Este apartado permite visualizar y gestionar las cuentas de usuarios que han sido bloqueadas por exceder los intentos de inicio de sesión permitidos.

---

A continuación, detallamos los módulos existentes en la vista del **Administrador del sistema** (Administrador de la licorería):

---

**Módulo de Dashboard Negocio:** Este módulo presenta el panel principal con métricas operativas del negocio. Visible para el Administrador del sistema.

- **Resumen General:** En esta sección, el Administrador puede acceder a las ventas del día, semana y mes, productos más vendidos, stock crítico y alertas, pedidos pendientes, caja del día y gráficos de tendencias.

- **Vista por Sede:** Este apartado permite seleccionar la sede activa y visualizar métricas específicas de cada sede, así como comparativas entre sedes del negocio.

**Módulo de Configuración del Negocio, Sedes y Storefront:** En este módulo se gestiona toda la configuración general del negocio. Visible para el Administrador del sistema.

- **Datos del Negocio:** Aquí se administra la información del negocio incluyendo RUC, razón social, nombre comercial, dirección fiscal, teléfono, email, website, logo y branding. También se puede consultar el plan de suscripción actual.

- **Gestión de Sedes:** En este apartado se visualizan las sedes del negocio, se editan datos operativos (nombre, dirección, teléfono, email), se gestionan los almacenes por sede (general, refrigerado, congelado) y se configuran los horarios de operación para cada día de la semana.

- **Restricciones de Alcohol:** Este apartado permite configurar los horarios permitidos para la venta de alcohol, días restringidos (como elecciones) y la verificación de edad obligatoria.

- **Zonas de Delivery:** En esta sección se administran las zonas de entrega, con su costo de envío, tiempo estimado de entrega y monto mínimo de pedido por zona.

- **Módulo de Mesas:** Aquí se puede activar o desactivar la funcionalidad de mesas, realizar el CRUD completo de mesas con su número, capacidad, estado (disponible, ocupada, reservada) y ubicación.

- **Configuración de Tienda Online (StoreFront):** En este apartado se configura la tienda en línea del negocio, incluyendo nombre, URL personalizada, logo, banner principal, colores y tema visual, descripción SEO y estado de la tienda.

- **Métodos de Pago:** Este componente se encarga de la gestión de métodos de pago habilitados: efectivo, tarjetas, transferencias, billeteras digitales (Yape/Plin), Mercado Pago, Niubiz, PayPal y pago contra entrega.

**Módulo de Seguridad, Usuarios y Clientes:** En este módulo se gestiona la seguridad de usuarios, roles, permisos y la base de clientes del negocio. Visible para el Administrador del sistema.

- **Gestión de Usuarios:** En este apartado se administran los usuarios registrados del negocio, permitiendo crear, editar, desactivar usuarios y asignarles roles y sedes específicas. Cada usuario cuenta con su nombre completo, email, contraseña encriptada, roles y sedes asignadas.

- **Gestión de Roles y Permisos:** De manera similar a la gestión de usuarios, el Administrador tiene control sobre los roles utilizados en el sistema, incluyendo roles predefinidos (Admin, Cajero, Inventario) y la posibilidad de crear roles personalizados. Los permisos se asignan de forma granular por módulo, permitiendo configurar acceso a ver, crear, editar, eliminar, exportar y aprobar.

- **Gestión de Clientes:** En esta sección se lleva a cabo la administración de los clientes del negocio. Se permite registrar clientes con su tipo de documento (DNI, RUC, Carnet de extranjería, Pasaporte), datos personales, email, teléfono, fecha de nacimiento (para validación de edad) y direcciones de entrega. Se puede visualizar el historial de compras de cada cliente, total gastado y productos preferidos.

**Módulo de Catálogo de Productos, Descuentos y Promociones:** En este módulo se gestiona todo el catálogo de productos. Visible para el Administrador del sistema.

- **Gestión de Productos:** En esta sección se lleva a cabo la administración de los productos en la licorería. Cada producto cuenta con información básica (nombre, código, código de barras, descripción, SKU), clasificación por categoría y marca, campos especiales para licores (tipo de bebida, grado alcohólico, volumen, origen, añada), precios de compra y venta, márgenes de ganancia calculados automáticamente y configuración de stock mínimo, máximo y punto de reorden.

- **Gestión de Categorías:** En este apartado se realiza la administración de las categorías de productos existentes, con soporte para subcategorías, imágenes por categoría y orden de visualización.

- **Gestión de Marcas:** En esta sección se efectúa la administración de las marcas asociadas a los productos. Se registra el nombre, país de origen, logo y descripción de cada marca.

- **Combos Promocionales:** Este apartado permite crear y administrar combos que agrupan varios productos a un precio especial, con vigencia configurable (desde/hasta) e imagen del combo.

- **Gestión de Promociones:** En esta sección se crean y administran promociones con diferentes tipos de descuento (porcentaje, monto fijo, 2x1, 3x2, regalo al comprar, descuento por volumen). Las promociones pueden restringirse por días de la semana, horarios, sedes, canales (POS, online), categorías, productos o marcas específicas.

**Módulo de Gestión de Inventario:** Este módulo se centra en el control de stock, lotes con sistema FIFO, movimientos y alertas de inventario. Visible para el Administrador del sistema.

- **Stock Consolidado:** En esta sección se presenta una vista general del inventario con el stock actual por producto, stock por almacén, valor total del inventario y alertas visuales de stock bajo y crítico, con filtros por sede, almacén y categoría.

- **Lotes de Inventario (Sistema FIFO):** Este apartado administra los lotes de inventario bajo el sistema FIFO (primero en entrar, primero en salir). Cada lote registra el producto, almacén, cantidad recibida, fecha de vencimiento, costo unitario, proveedor y número de lote del fabricante. Se incluye un botón de ajuste por lote para realizar ajustes por merma, robo, error de conteo, muestra, donación, vencimiento, rotura o defecto.

- **Movimientos de Inventario (Reporte Kardex):** En esta sección se registra el historial completo de movimientos del inventario: entradas por compra, salidas por venta, ajustes manuales, transferencias entre almacenes, mermas/vencimientos y devoluciones. Incluye filtros avanzados y exportación a Excel, PDF y CSV.

- **Transferencias Entre Almacenes:** Este apartado permite realizar transferencias de productos entre almacenes del negocio, registrando el almacén origen, destino, cantidad y estado (pendiente, en tránsito, recibido).

- **Alertas de Inventario:** En esta sección se configuran alertas automáticas para stock bajo, stock crítico, productos próximos a vencer y productos vencidos. Incluye notificaciones por email.

**Módulo de Proveedores y Compras:** Este módulo se centra en la gestión exclusiva de las compras realizadas por el negocio. Visible para el Administrador del sistema.

- **Gestión de Proveedores:** En este apartado se lleva a cabo la gestión de todos los proveedores registrados por el administrador, incluyendo RUC, razón social, dirección, contacto, términos de pago, días de crédito y categorías de productos que suministra.

- **Órdenes de Compra:** Este apartado se concentra en la gestión de las compras, donde el administrador puede crear órdenes de compra seleccionando proveedor, sede y almacén destino, productos, cantidades, precios y fecha de entrega esperada. Las órdenes siguen un flujo de estados: borrador, pendiente, aprobada, recibida (parcial/total) y cancelada.

- **Recepción de Mercancía:** En esta sección se registra la recepción de los productos comprados, comparando la cantidad recibida con la solicitada, capturando el lote y fecha de vencimiento, y generando automáticamente la entrada de inventario.

- **Cuentas por Pagar:** Este apartado muestra las órdenes pendientes de pago por proveedor, con monto adeudado, fecha de vencimiento, días de mora e historial de pagos.

**Módulo de Ventas, Punto de Venta (POS) y Pedidos:** Este módulo se centra en la gestión de todas las ventas del negocio. Visible para el Administrador y los usuarios con rol de Cajero.

- **Punto de Venta (POS):** Este apartado está diseñado como una interfaz optimizada para ventas rápidas. Permite buscar productos por nombre, código o escaneo de código de barras, agregar productos al carrito, editar cantidades, aplicar descuentos por ítem, seleccionar cliente, tipo de comprobante (boleta/factura) y métodos de pago múltiples. Incluye verificación de edad obligatoria para la venta de alcohol.

- **Cajas Registradoras:** En esta sección se administran las cajas registradoras de la sede: apertura y cierre de sesión de caja. Al abrir caja se define el monto inicial en efectivo; al cerrar caja se realiza el cuadre detallado con monto esperado vs. real, diferencias, desglose por método de pago, gastos realizados y retiros de efectivo.

- **Ventas Realizadas:** Este apartado muestra una lista completa de todas las ventas realizadas, con filtros por fecha, sede, cajero y tipo de comprobante. Cada venta detalla los productos vendidos, cliente, total, método(s) de pago y estado, permitiendo también la reimpresión de comprobantes.

- **Gestión de Pedidos:** En esta sección se administran todos los pedidos del negocio, con filtros por tipo (delivery, recojo en tienda, consumo en mesa, online) y estado (pendiente, confirmado, en preparación, listo, en camino, entregado, cancelado). Se puede crear pedidos manuales, cambiar estados y asignar repartidores o meseros.

- **Gestión de Delivery:** Este apartado es dedicado a la administración de los pedidos de delivery, incluyendo la asignación de repartidores, seguimiento de pedidos en ruta, zonas de entrega y tiempos promedio de entrega.

- **Gestión de Mesas:** Similar al apartado anterior, esta sección permite administrar las mesas del local, visualizar su estado (disponible, ocupada, reservada), abrir y cerrar cuentas de mesa, agregar productos, dividir cuentas y transferir productos entre mesas.

- **Devoluciones:** En esta sección se gestionan las devoluciones de ventas, permitiendo seleccionar la venta original, los productos a devolver, la cantidad, el motivo de devolución, el método de reembolso y la generación de nota de crédito.

**Módulo de Facturación y Comprobantes:** Este módulo se encarga de la facturación electrónica. Visible para el Administrador del sistema.

- **Emisión de Comprobantes:** En este apartado se generan boletas de venta y facturas electrónicas, numeradas según la serie por sede, con la información completa de la venta. Los comprobantes pueden ser reimpresos o anulados.

- **Notas de Crédito y Débito:** En esta sección se generan notas de crédito por devoluciones o anulaciones, y notas de débito por penalidades u otros cargos adicionales.

**Módulo de Reportes y Analíticas:** Este módulo permite visualizar reportes completos del negocio. Visible para el Administrador del sistema.

- **Reporte de Ventas:** En esta sección, el Administrador puede acceder a una vista integral de todas las ventas realizadas, con filtros por rango de fechas, sede, cajero y tipo de comprobante. Se incluye el total vendido, cantidad de transacciones, ticket promedio, productos más vendidos, ventas por método de pago y ventas por hora. Permite la descarga en formato PDF y Excel.

- **Reporte de Inventario:** Este apartado muestra el estado actual del inventario, productos por vencer, movimientos del periodo y valorización del stock.

- **Reporte de Compras:** En esta sección se visualizan las compras realizadas por proveedor, total gastado, productos más comprados y comparativa de precios.

- **Reporte de Rentabilidad:** Este componente presenta el análisis de márgenes de ganancia por producto, categoría y marca, así como gastos vs. ingresos.

---

A continuación, detallamos los módulos existentes en la vista del **Cliente / Tienda Online (StoreFront)**:

---

**Módulo de Inicio (Home):** Este módulo presenta la página principal de la tienda online de la licorería. Visible para todos los visitantes y clientes registrados.

- **Página Principal:** En esta sección se muestran los productos destacados, promociones activas, categorías principales y el banner de bienvenida de la licorería. Incluye una verificación de edad que el visitante debe aceptar antes de navegar por el catálogo.

**Módulo de Catálogo:** Este módulo permite a los clientes explorar todos los productos disponibles en la tienda online.

- **Exploración de Productos:** En este apartado los clientes pueden navegar el catálogo completo de productos, con filtros por categoría, marca, tipo de bebida, grado alcohólico, precio y volumen. Incluye búsqueda avanzada y opciones de ordenamiento (nombre, precio, popularidad, más recientes).

- **Detalle de Producto:** En esta sección se presenta la ficha completa del producto con imágenes, descripción detallada, características del licor (tipo, grado, volumen, origen), precio, disponibilidad de stock, productos relacionados y la opción de agregar al carrito.

**Módulo de Carrito de Compras:** Este módulo gestiona el carrito de compras del cliente.

- **Carrito:** Aquí el cliente puede visualizar los productos agregados, modificar cantidades, eliminar productos, ver subtotales, costo de envío y total de la compra. Incluye aplicación automática de promociones y combos vigentes.

**Módulo de Checkout (Proceso de Compra):** Este módulo gestiona el proceso de finalización de la compra.

- **Proceso de Compra:** En esta sección el cliente selecciona el método de entrega (delivery o recojo en tienda), ingresa o selecciona la dirección de entrega, elige el método de pago (efectivo, transferencia, billetera digital, tarjeta) y confirma el pedido. Se genera automáticamente un resumen de la compra con número de pedido.

**Módulo de Autenticación:** Este módulo gestiona el registro e inicio de sesión de los clientes.

- **Registro de Cliente:** En este apartado los nuevos clientes pueden crear su cuenta ingresando nombres, apellidos, email, contraseña, teléfono, DNI y fecha de nacimiento (para validación de edad).

- **Inicio de Sesión:** En esta sección los clientes registrados pueden acceder a su cuenta con email y contraseña.

**Módulo de Perfil del Cliente:** Este módulo permite al cliente gestionar su información personal. Visible solo para clientes autenticados.

- **Mi Perfil:** Aquí el cliente puede visualizar y editar sus datos personales, cambiar su contraseña y gestionar sus direcciones de entrega guardadas.

**Módulo de Mis Pedidos:** Este módulo permite al cliente hacer seguimiento de sus pedidos. Visible solo para clientes autenticados.

- **Historial de Pedidos:** En esta sección el cliente puede visualizar todos sus pedidos realizados, con el estado actual de cada uno (pendiente, confirmado, en preparación, listo, en camino, entregado), fecha, total y detalle de los productos comprados.

**Módulo de Devoluciones del Cliente:** Este módulo permite al cliente solicitar devoluciones. Visible solo para clientes autenticados.

- **Solicitar Devolución:** En este apartado el cliente puede seleccionar un pedido entregado, elegir los productos a devolver, indicar la cantidad y el motivo de la devolución, y realizar la solicitud que será gestionada por el administrador del negocio.

---

En resumen, hemos presentado en esta memoria descriptiva un panorama completo de nuestro software **DRINKGO**. A lo largo de este documento, hemos detallado todos los módulos, funcionalidades y características que componen este sistema, demostrando cómo se integran para proporcionar una solución efectiva y eficiente a las necesidades de nuestros usuarios.

Nuestro compromiso con la calidad y la innovación nos ha llevado a desarrollar un software que no solo cumple con los requisitos actuales, sino que también está diseñado para adaptarse y crecer con las demandas futuras. Cada módulo ha sido diseñado pensando en la facilidad de uso, la escalabilidad y la seguridad.

En **DRINKGO**, valoramos la retroalimentación de nuestros usuarios, por lo que seguiremos mejorando y actualizando constantemente nuestro software para mantenernos a la vanguardia de la tecnología y las necesidades cambiantes del mercado.

Esperamos que **DRINKGO** se convierta en una herramienta fundamental en sus operaciones y que les ayude a alcanzar sus objetivos con mayor eficacia.
