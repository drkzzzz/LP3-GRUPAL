# **MÓDULO VI: E-COMMERCE Y REPORTES AVANZADOS**

---

## **Descripción del Módulo**

El Módulo VI: E-commerce y Reportes Avanzados proporciona una plataforma completa de comercio electrónico integrada con el sistema POS, permitiendo a las licorerías expandir su alcance mediante ventas online, gestionar catálogos digitales, procesar pedidos web, y ofrecer experiencias de compra personalizadas. Además, incluye un motor de reportes avanzados y analítica de negocio que transforma los datos operativos en insights accionables mediante dashboards interactivos, KPIs en tiempo real, y análisis predictivo.

**Alcance del módulo:**
- Tienda online (e-commerce) con catálogo de productos, carrito de compras, y checkout
- Gestión de contenido web (banners, promociones, páginas de contenido)
- Sistema de reseñas y calificaciones de productos
- Programa de suscripciones y membresías
- Motor de recomendaciones inteligente
- Dashboards ejecutivos con KPIs en tiempo real
- Reportes avanzados de ventas, inventario, clientes, y finanzas
- Análisis de tendencias y predicciones
- Reportes personalizados y programados

**Submódulos incluidos:**
- **6.6.1:** Tienda Online (E-commerce)
- **6.6.2:** Gestión de Contenido y Marketing Digital
- **6.6.3:** Dashboards Ejecutivos y KPIs
- **6.6.4:** Reportes Avanzados y Analítica

---

## **Submódulo 6.6.1: Tienda Online (E-commerce)**

---

### **RF-ECO-001: Configurar Tienda Online**

**Descripción:**  
El sistema debe permitir configurar la tienda online con branding personalizado, información del negocio, políticas comerciales, métodos de pago y entrega, integrándose automáticamente con el inventario, catálogo de productos, y sistema de pedidos del POS.

**Criterios de Aceptación:**
1. Configuración general:
   - **Información del negocio**:
     - Nombre comercial de la tienda online
     - Logo (dimensiones recomendadas: 200x80px)
     - Favicon (icono del navegador)
     - Descripción breve para SEO
     - Slogan o tagline
   - **Datos de contacto**:
     - Dirección física (puede ser diferente a la fiscal)
     - Teléfonos (principal, WhatsApp)
     - Email de contacto
     - Horarios de atención
     - Redes sociales (Facebook, Instagram, TikTok)
   - **URL de la tienda**:
     - Dominio principal: `www.minegocio.com`
     - Subdominio: `tienda.minegocio.com`
     - URL amigable personalizable
2. Personalización visual:
   - **Tema/Template**:
     - Seleccionar de galería de temas prediseñados:
       - Tema Moderno (minimalista, fondos claros)
       - Tema Elegante (sofisticado para licorería premium)
       - Tema Vibrante (colores llamativos para promociones)
     - Previsualización antes de aplicar
   - **Colores de marca**:
     - Color primario (botones, enlaces)
     - Color secundario (encabezados)
     - Color de acento (llamadas a la acción)
     - Fondo (blanco, gris claro, personalizado)
   - **Tipografía**:
     - Fuente principal (títulos)
     - Fuente secundaria (textos)
     - Tamaño base
   - **Layout**:
     - Posición del logo (izquierda, centro)
     - Menú de navegación (horizontal, sidebar)
     - Footer personalizable (enlaces, información legal)
3. Configuración de ventas:
   - **Métodos de pago aceptados**:
     - Tarjetas de crédito/débito (integración con pasarela)
     - Transferencia bancaria (mostrar datos de cuentas)
     - Yape/Plin (QR o número de celular)
     - PagoEfectivo (código de pago en agentes)
     - Pago contraentrega (solo delivery)
     - Billeteras digitales (PayPal, Mercado Pago)
   - **Métodos de entrega**:
     - Delivery a domicilio:
       - Configurar zonas y costos (vinculado a RF-VTA-022)
       - Tiempo estimado de entrega
     - Recojo en tienda (pickup):
       - Sedes disponibles para recojo
       - Horarios de recojo
     - Envío nacional (si aplica):
       - Integración con courier (Olva, Shalom, etc.)
   - **Monto mínimo de compra**:
     - Por modalidad (ej: delivery $25 USD, pickup $10 USD)
     - Por zona de entrega
   - **Descuentos y promociones**:
     - Habilitar cupones de descuento
     - Promociones automáticas
     - Descuento por primera compra
4. Políticas y términos legales:
   - **Políticas obligatorias**:
     - Términos y condiciones de uso
     - Política de privacidad y tratamiento de datos
     - Política de devoluciones y cambios
     - Política de envíos
     - Política de cookies
   - **Editor de políticas**:
     - Plantillas predefinidas (ajustables)
     - Editor de texto enriquecido
     - Versionado de políticas (fecha de última actualización)
   - **Aceptación obligatoria**:
     - Checkbox en registro y checkout
     - Sin aceptación, no puede completar compra
5. Integración con sistemas internos:
   - **Inventario en tiempo real**:
     - Mostrar solo productos con stock disponible
     - Actualización automática al vender en POS o online
     - Configurar si permite preventa (sin stock)
   - **Precios sincronizados**:
     - Mismos precios que POS (o configurar precios diferentes para online)
     - Aplicar descuentos automáticos según promociones activas
   - **Clientes unificados**:
     - Clientes registrados en web acceden a su historial del POS
     - Puntos de fidelización compartidos
6. Configuración de notificaciones:
   - **Al cliente**:
     - Email de confirmación de pedido
     - Email de cambio de estado
     - SMS de entrega próxima
   - **Al negocio**:
     - Email de nuevo pedido a administrador
     - Alerta en dashboard de pedidos pendientes
7. SEO y Marketing:
   - **Configuración SEO básica**:
     - Meta título (por defecto)
     - Meta descripción
     - Keywords principales
     - URL canónica
   - **Tracking y Analytics**:
     - Google Analytics ID
     - Facebook Pixel ID
     - Google Tag Manager
     - TikTok Pixel (opcional)
8. Configuración técnica:
   - **Seguridad**:
     - Certificado SSL (HTTPS) obligatorio
     - Protección contra bots (reCAPTCHA)
     - Límite de intentos de login
   - **Idiomas** (si es multi-idioma):
     - Español (principal)
     - Inglés (opcional para turistas)
   - **Moneda**:
     - PEN (Soles) principal
     - USD (opcional, con tipo de cambio dinámico)

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propia tienda online independiente.
- URL única por negocio (subdominio o dominio propio).
- Personalización completa sin interferir con otros negocios.

**Seguridad:**
- Requiere permiso: `ecommerce_configurar`
- Solo administradores pueden modificar configuración.
- Cambios en políticas legales se auditan con fecha/hora.
- Datos de pasarelas de pago encriptados.

**UX:**
- Wizard de configuración inicial en 5 pasos.
- Vista previa en tiempo real de cambios visuales.
- Modo "Mantenimiento" para hacer cambios sin afectar clientes.
- Plantillas listas para usar (solo personalizar colores y textos).

**Reglas de Negocio:**
- La tienda debe tener al menos un método de pago y un método de entrega configurado.
- Las políticas legales son obligatorias antes de publicar la tienda.
- El certificado SSL es obligatorio para aceptar pagos online.
- Los cambios en métodos de pago/entrega no afectan pedidos ya realizados.

---

### **RF-ECO-002: Gestionar Catálogo de Productos Online**

**Descripción:**  
El sistema debe permitir gestionar el catálogo de productos visible en la tienda online, con descripciones detalladas, imágenes de alta calidad, categorización, filtros, y sincronización automática con el inventario del POS.

**Criterios de Aceptación:**
1. Sincronización con catálogo POS:
   - **Productos automáticamente disponibles**:
     - Todos los productos del POS están disponibles para publicar online
     - Estado: Publicado (visible en tienda) / Borrador (no visible)
   - **Información básica sincronizada**:
     - Nombre del producto
     - SKU
     - Precio
     - Stock disponible (tiempo real)
     - Categoría
2. Información adicional para e-commerce:
   - **Descripción detallada**:
     - Descripción corta (1-2 líneas, para listados)
     - Descripción larga (párrafos, para página de producto):
       - Características del producto
       - Origen/procedencia
       - Notas de cata (para vinos/licores)
       - Maridaje sugerido
       - Graduación alcohólica
       - Contenido neto
     - Editor de texto enriquecido (negrita, cursiva, listas)
   - **Imágenes del producto**:
     - Imagen principal (obligatoria, 800x800px mínimo)
     - Galería de imágenes (hasta 5 adicionales):
       - Vista frontal, lateral, etiqueta, empaque
     - Optimización automática (compresión sin pérdida de calidad)
     - Imagen de placeholder si no hay imagen
   - **Atributos específicos**:
     - Marca
     - Origen (país/región)
     - Tipo (Cerveza: Lager, Ale, Stout; Vino: Tinto, Blanco, Rosado)
     - Año de cosecha (vinos)
     - Varietal/Cepa
     - Bodega/Productor
     - ABV (Alcohol by Volume) %
     - Formato (botella, lata, caja)
   - **Restricciones**:
     - Es producto alcohólico: Sí/No (obligatorio)
     - Edad mínima: 18 años (automático si es alcohólico)
     - Requiere verificación de edad en entrega
3. Categorización y navegación:
   - **Categorías jerárquicas**:
     - Nivel 1: Cervezas, Vinos, Licores, Destilados, Snacks
     - Nivel 2: Cervezas → Nacionales, Importadas, Artesanales
     - Nivel 3: Importadas → Alemanas, Belgas, Holandesas
   - **Etiquetas (tags)**:
     - Etiquetas libres: "oferta", "nuevo", "premium", "edición limitada"
     - Múltiples etiquetas por producto
   - **Colecciones especiales**:
     - "Los más vendidos"
     - "Recomendados para ti"
     - "Ofertas del mes"
     - "Productos nuevos"
4. Filtros y búsqueda:
   - **Filtros en listados**:
     - Por categoría
     - Por rango de precio ($20-$50, $50-$100, etc.)
     - Por marca
     - Por origen
     - Por tipo
     - Por graduación alcohólica
     - Solo productos en oferta
     - Solo productos con stock
   - **Ordenamiento**:
     - Relevancia (por defecto)
     - Precio: Menor a mayor / Mayor a menor
     - Más vendidos
     - Mejor calificados
     - Más recientes
   - **Búsqueda avanzada**:
     - Búsqueda por nombre, marca, descripción
     - Autocompletado con sugerencias
     - Corrección de errores tipográficos
     - Sinónimos ("cerveza" = "chela", "birra")
5. Información de stock y disponibilidad:
   - **Indicadores visuales**:
     - En stock (verde): "Disponible para entrega inmediata"
     - Bajo stock (amarillo): "Solo quedan X unidades"
     - Sin stock (rojo): "Agotado" (no se puede agregar al carrito)
     - Preventa (azul): "Disponible desde [fecha]" (si se permite)
   - **Notificación de disponibilidad**:
     - Botón "Avisarme cuando esté disponible"
     - Cliente ingresa su email
     - Se notifica automáticamente cuando hay stock
6. Configuración de visibilidad:
   - **Control por producto**:
     - Publicar/Despublicar individualmente
     - Programar fecha de publicación (lanzamientos)
     - Destacar en página principal
     - Ocultar precio (solo mostrar en cotización)
   - **Publicación masiva**:
     - Publicar todos los productos de una categoría
     - Despublicar productos sin stock
7. SEO por producto:
   - **Optimización automática**:
     - URL amigable: `/producto/cerveza-corona-355ml`
     - Meta título: "[Nombre producto] - [Marca] | Tienda"
     - Meta descripción: Primeras 160 caracteres de descripción
   - **Optimización manual** (avanzado):
     - Editar URL personalizada
     - Editar meta título y descripción
     - Agregar texto alternativo a imágenes (alt text)
8. Productos relacionados:
   - **Sugerencias automáticas**:
     - "Productos similares" (misma categoría)
     - "Los clientes también compraron" (algoritmo de asociación)
     - "Te puede interesar" (basado en navegación del usuario)
   - **Configuración manual**:
     - Seleccionar productos relacionados específicos
     - Útil para combos o maridajes

**Consideraciones Multi-tenant:**
- Cada negocio gestiona su catálogo independientemente.
- Los productos publicados son solo del negocio correspondiente.

**Seguridad:**
- Requiere permiso: `ecommerce_catalogo_gestionar`
- Las imágenes se escanean contra malware antes de subir.
- Las URLs de productos son únicas (no se puede duplicar slug).

**UX:**
- Editor visual de producto con vista previa.
- Carga masiva de imágenes (drag-and-drop, hasta 10 a la vez).
- Importación masiva de productos desde Excel.
- Previsualización de cómo se ve el producto en la tienda.

**Reglas de Negocio:**
- Los productos sin imagen usan imagen de placeholder genérica.
- Los productos sin stock no se muestran por defecto (configurable).
- Los productos alcohólicos deben tener marcado el flag obligatoriamente.
- Las descripciones deben ser veraces (responsabilidad legal).

---

### **RF-ECO-003: Gestionar Carrito de Compras y Checkout**

**Descripción:**  
El sistema debe proporcionar un carrito de compras persistente, proceso de checkout optimizado con validación de edad para productos alcohólicos, cálculo automático de impuestos y costos de envío, y múltiples opciones de pago seguras.

**Criterios de Aceptación:**
1. Carrito de compras:
   - **Agregar productos**:
     - Botón "Agregar al carrito" en página de producto
     - Seleccionar cantidad (spinner o input)
     - Validación de stock disponible en tiempo real
     - Feedback visual: "Producto agregado ✓"
   - **Visualización del carrito**:
     - Mini carrito en header (icono con número de items)
     - Carrito completo (página dedicada o sidebar)
     - Por cada producto:
       - Imagen miniatura
       - Nombre y descripción corta
       - Precio unitario
       - Cantidad (editable)
       - Subtotal
       - Botón eliminar
   - **Totales**:
     - Subtotal de productos
     - Descuento (si aplica)
     - Costo de envío (calculado según destino)
     - IGV (18%)
     - Total a pagar (destacado, número grande)
   - **Persistencia**:
     - Carrito se guarda en sesión (si no está logueado)
     - Carrito se guarda en cuenta (si está logueado)
     - Se mantiene entre sesiones (hasta 30 días)
     - Se fusionan carritos si se loguea después de agregar items
2. Validación de restricciones:
   - **Productos alcohólicos**:
     - Al agregar al carrito, mostrar advertencia:
       - "⚠️ Este producto contiene alcohol. Debes ser mayor de 18 años."
     - Checkbox obligatorio: "Confirmo que soy mayor de 18 años"
     - Sin confirmación, no se puede proceder al checkout
   - **Stock disponible**:
     - Validar en cada etapa (agregar, aumentar cantidad, checkout)
     - Si stock cambia mientras compra:
       - Alerta: "El stock de [producto] ha cambiado. Actualizando carrito..."
       - Ajustar cantidad automáticamente al disponible
   - **Monto mínimo**:
     - Validar que el total >= monto mínimo configurado
     - Mostrar mensaje: "Faltan $X para alcanzar el mínimo de compra"
3. Proceso de checkout (multi-paso):
   - **Paso 1: Información de contacto**:
     - Si está logueado: Mostrar datos guardados (editables)
     - Si no está logueado: Opción de "Continuar como invitado" o "Iniciar sesión"
     - Datos a capturar:
       - Nombre completo
       - Email
       - Teléfono (celular para WhatsApp)
       - DNI (obligatorio si hay productos alcohólicos o monto > $700)
   - **Paso 2: Dirección de entrega** (si es delivery):
     - Buscar dirección guardada (si tiene cuenta)
     - O ingresar nueva:
       - Dirección completa
       - Distrito
       - Referencia (ej: "Casa verde, 2do piso")
       - Coordenadas GPS (si usa mapa interactivo)
     - Validar que está dentro de zona de cobertura
     - Calcular costo de envío automáticamente
     - Mostrar tiempo estimado de entrega
   - **Paso 2 alternativo: Sede de recojo** (si es pickup):
     - Seleccionar de lista de sedes disponibles
     - Ver horario de atención
     - Seleccionar fecha y hora estimada de recojo
   - **Paso 3: Método de pago**:
     - Opciones disponibles (según configuración):
       - **Tarjeta de crédito/débito**:
         - Formulario seguro (iframe de pasarela)
         - Número de tarjeta, fecha de vencimiento, CVV
         - Nombre del titular
       - **Transferencia/Depósito bancario**:
         - Mostrar datos de cuentas del negocio
         - Instrucción: "Realiza la transferencia y envía voucher por WhatsApp"
       - **Yape/Plin**:
         - Mostrar QR para escanear (con monto preconfigurado)
         - O número de celular para transferir
       - **Pago contraentrega**:
         - Solo para delivery
         - Opción: Efectivo o POS móvil
       - **PagoEfectivo**:
         - Generar código de pago (CIP)
         - Cliente paga en agente (Kasnet, Western Union, etc.)
     - Validación según método:
       - Tarjeta: Autorización inmediata con pasarela
       - Otros: Pago pendiente de confirmación
   - **Paso 4: Revisión y confirmación**:
     - Resumen completo del pedido:
       - Productos con cantidades
       - Dirección/Sede de entrega
       - Método de pago
       - Totales
     - Checkbox: "Acepto términos y condiciones" (obligatorio)
     - Botón grande: "Confirmar Pedido" o "Procesar Pago"
4. Procesamiento del pedido:
   - **Al confirmar**:
     - Generar número de pedido único
     - Estado inicial según pago:
       - Pago con tarjeta exitoso: `'confirmado'`
       - Otros métodos: `'pendiente_pago'`
     - Reservar inventario (bloqueo soft)
     - Enviar confirmación por email:
       - Número de pedido
       - Detalle de productos
       - Total pagado/por pagar
       - Instrucciones según método de pago
       - Link de seguimiento
     - Enviar notificación por WhatsApp (opcional)
     - Crear pedido en el sistema (RF-VTA-019)
   - **Redirigir a página de confirmación**:
     - "✓ ¡Pedido confirmado!"
     - Número de pedido destacado
     - Tiempo estimado de entrega/preparación
     - Próximos pasos según método de pago
     - Botón "Ver mi pedido" (página de seguimiento)
     - Botón "Seguir comprando"
5. Opciones adicionales en checkout:
   - **Cupón de descuento**:
     - Campo "¿Tienes un cupón?"
     - Ingresar código
     - Validar y aplicar descuento
     - Mostrar ahorro obtenido
   - **Código de referido**:
     - Ingresar código de amigo
     - Aplicar beneficio (si es primera compra)
   - **Notas especiales**:
     - Campo de texto libre
     - Ej: "Es un regalo, incluir tarjeta", "Entregar en la tarde"
   - **Propina para repartidor** (opcional):
     - Slider o botones: $5, $10, $15, Otro
     - Se suma al total
6. Carritos abandonados:
   - **Seguimiento**:
     - Si el usuario llega a checkout pero no completa:
       - Registrar como "carrito abandonado"
       - Capturar email (si lo ingresó)
   - **Recuperación automática**:
     - Enviar email recordatorio después de 1 hora:
       - "¿Olvidaste algo en tu carrito?"
       - Link directo para retomar la compra
     - Segundo recordatorio después de 24 horas con incentivo:
       - "Completa tu compra y recibe 10% OFF con código COMPLETA10"

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio checkout independiente.
- Los carritos no se cruzan entre negocios.

**Seguridad:**
- PCI DSS compliance para procesamiento de tarjetas.
- Los datos de tarjetas nunca se almacenan en el sistema (solo token).
- HTTPS obligatorio en todo el checkout.
- Protección contra bots (reCAPTCHA en confirmación).
- Validación de stock al momento del pago (prevenir overselling).

**UX:**
- Indicador de progreso: "Paso 2 de 4".
- Botón "Volver" en cada paso sin perder datos.
- Autocompletado de direcciones (Google Places API).
- Cálculo de costos en tiempo real (sin recargar página).
- Formularios con validación inline (errores inmediatos).
- Opción de "Checkout Express" (1 clic si tiene todo guardado).

**Reglas de Negocio:**
- El carrito se limpia después de 30 días de inactividad.
- Los productos sin stock se eliminan automáticamente del carrito.
- El monto mínimo es obligatorio (no se puede evadir).
- La verificación de edad es obligatoria por ley para productos alcohólicos.
- Los pagos con tarjeta se procesan inmediatamente; otros requieren confirmación manual.

---

### **RF-ECO-004: Gestionar Pedidos Web y Seguimiento**

**Descripción:**  
El sistema debe integrar los pedidos realizados en la tienda online con el sistema de gestión de pedidos del POS, permitiendo seguimiento en tiempo real, cambios de estado, comunicación con clientes, y gestión unificada desde un solo panel.

**Criterios de Aceptación:**
1. Recepción de pedidos web:
   - **Integración automática**:
     - Al confirmarse un pedido en la web:
       - Se crea automáticamente en el sistema de pedidos (RF-VTA-019)
       - Origen: "E-commerce" (etiqueta visible)
       - Modalidad: Delivery o Pickup (según elección del cliente)
       - Estado inicial: `'pendiente_pago'` o `'confirmado'` (según método de pago)
   - **Notificación al negocio**:
     - Alerta en dashboard de pedidos
     - Notificación push/email al administrador
     - Sonido de alerta (configurable)
2. Panel unificado de pedidos:
   - **Vista consolidada**:
     - Todos los pedidos en un solo lugar:
       - Pedidos de POS
       - Pedidos de e-commerce
       - Pedidos de plataformas externas (Rappi, etc.)
     - Filtros:
       - Por origen (E-commerce, POS, Rappi, etc.)
       - Por estado
       - Por modalidad (Delivery, Pickup)
       - Por fecha
   - **Tarjetas de pedido**:
     - Número de pedido (grande, destacado)
     - Cliente (nombre, teléfono)
     - Productos ordenados (cantidad, nombre)
     - Total
     - Modalidad e ícono (🛵 Delivery, 🏪 Pickup)
     - Estado actual con color coding
     - Tiempo transcurrido desde creación
     - Origen con logo/etiqueta
3. Gestión de estados específicos de e-commerce:
   - **Estados adicionales**:
     - **Pendiente de pago**: Esperando confirmación de pago
     - **Pago verificado**: Pago confirmado, listo para procesar
     - **En preparación**: Productos siendo preparados
     - **Listo para envío**: Empaquetado, esperando repartidor
     - **En camino**: Repartidor en ruta
     - **Listo para recojo**: Cliente puede recoger
     - **Entregado**: Pedido completado
     - **Cancelado**: Por cliente o negocio
   - **Transiciones de estado**:
     - Manual: Administrador cambia estado
     - Automática: Según acciones (ej: pago verificado → confirmar automáticamente)
   - **Notificación al cliente en cada cambio**:
     - Email con actualización
     - SMS (opcional, para estados críticos)
     - WhatsApp (si configurado)
4. Confirmación de pagos pendientes:
   - **Para métodos no instantáneos** (transferencia, Yape, PagoEfectivo):
     - Vista de "Pagos Pendientes"
     - Por cada pedido:
       - Ver comprobante de pago adjunto (si el cliente lo envió)
       - Botón "Verificar Pago":
         - Marcar como pagado
         - Ingresar número de operación
         - Confirmar
       - Estado cambia a `'pago_verificado'` → `'confirmado'`
   - **Alertas de pagos demorados**:
     - Si pago pendiente > 24 horas:
       - Alerta amarilla
       - Opción de contactar al cliente o cancelar pedido
5. Página de seguimiento para el cliente:
   - **Acceso sin login**:
     - URL única por pedido: `/pedido/seguimiento/PED-20260131-0001`
     - O ingresando número de pedido + email
   - **Información mostrada**:
     - Estado actual con ícono y descripción
     - Timeline de estados anteriores (con fecha/hora)
     - Tiempo estimado de entrega/preparación
     - Productos ordenados
     - Total pagado
     - Dirección de entrega (si delivery)
     - Sede de recojo (si pickup)
   - **Para delivery con repartidor asignado**:
     - Nombre del repartidor
     - Mapa en vivo con ubicación GPS (actualización cada 30 seg)
     - Botón "Contactar repartidor" (llamada o WhatsApp)
   - **Acciones disponibles**:
     - Descargar factura/boleta (si ya se emitió)
     - Solicitar cancelación (si aún no se preparó)
     - Contactar al negocio (botón de WhatsApp/teléfono)
     - Repetir pedido (agregar los mismos productos al carrito)
6. Comunicación con el cliente:
   - **Mensajes automáticos** (templates configurables):
     - "Tu pedido ha sido confirmado 🎉"
     - "Estamos preparando tu pedido 👨‍🍳"
     - "Tu pedido está en camino 🚗"
     - "Tu pedido llegó a destino 🏁"
   - **Mensajes manuales**:
     - Botón "Enviar mensaje al cliente" en cada pedido
     - Plantillas predefinidas:
       - "Producto agotado, sugerimos reemplazo"
       - "Dirección incompleta, confirmar datos"
       - "Retraso estimado de 15 minutos"
     - Envío por WhatsApp o SMS
7. Gestión de cancelaciones:
   - **Cancelación por el cliente**:
     - Botón "Solicitar cancelación" en página de seguimiento
     - Motivo (dropdown + texto libre)
     - Si el pago ya se realizó:
       - Proceso de reembolso automático (RF-VTA-024)
     - Estado cambia a `'cancelado_cliente'`
   - **Cancelación por el negocio**:
     - Desde panel de pedidos, botón "Cancelar"
     - Motivo obligatorio
     - Si hubo pago, procesar reembolso
     - Notificar al cliente con disculpas
     - Estado cambia a `'cancelado_negocio'`
8. Métricas de pedidos web:
   - **Dashboard de e-commerce**:
     - Pedidos web del día: X
     - Tasa de conversión: Y% (visitas → compras)
     - Ticket promedio web: $Z
     - Tiempo promedio de entrega: W minutos
     - Tasa de cancelación: V%
   - **Comparativa**:
     - Pedidos web vs. POS
     - Crecimiento semanal/mensual
     - Productos más vendidos online vs. tienda física

**Consideraciones Multi-tenant:**
- Los pedidos web se segregan por negocio.
- Cada negocio gestiona sus pedidos independientemente.

**Seguridad:**
- La URL de seguimiento tiene token único difícil de adivinar.
- Solo el cliente que hizo el pedido puede acceder con su email.
- Los datos sensibles (teléfono completo, dirección exacta) se ocultan parcialmente en vistas públicas.

**UX:**
- Panel de pedidos tipo kanban (columnas por estado).
- Actualización en tiempo real sin recargar página.
- Sonido de notificación para nuevos pedidos.
- Vista de mapa con todos los deliveries activos (vista de dispatcher).
- Timeline visual del pedido para el cliente (similar a envíos de Amazon).

**Reglas de Negocio:**
- Los pedidos web con pago pendiente > 48 horas se cancelan automáticamente.
- Los pedidos listos para recojo > 24 horas sin recoger se contactan al cliente.
- Los pedidos cancelados después de preparación pueden generar cargo parcial.
- El seguimiento en tiempo real solo aplica cuando hay repartidor asignado.

---

### **RF-ECO-005: Implementar Sistema de Reseñas y Calificaciones**

**Descripción:**  
El sistema debe permitir a los clientes dejar reseñas y calificaciones de productos comprados, moderarlas antes de publicación, responder a reseñas, y usar esta retroalimentación para mejorar el catálogo y tomar decisiones de negocio.

**Criterios de Aceptación:**
1. Dejar reseña (cliente):
   - **Requisito**: Solo clientes que compraron el producto
   - **Activación**:
     - Después de recibir el pedido, enviar email:
       - "¿Qué te pareció [Producto]? Deja tu reseña"
       - Link directo al formulario de reseña
     - O desde la página del producto si ya lo compró
   - **Formulario de reseña**:
     - Calificación con estrellas: 1-5 ⭐
     - Título de la reseña (opcional, ej: "Excelente calidad")
     - Comentario (texto libre, 50-500 caracteres)
     - Subir fotos (opcional, hasta 3 imágenes)
     - Información del reseñador:
       - Nombre (se muestra como "Juan P." - inicial del apellido)
       - Email (no se muestra, solo para verificación)
     - Checkbox: "¿Recomendarías este producto?" Sí/No
     - Fecha de compra (automática, no editable)
   - **Validación**:
     - Prohibir palabras ofensivas (filtro automático)
     - Detectar spam (múltiples reseñas similares)
     - Un cliente puede dejar una reseña por producto comprado
     - Puede editar su reseña dentro de 7 días
2. Moderación de reseñas:
   - **Estado de reseñas**:
     - **Pendiente**: Recién enviada, esperando moderación
     - **Aprobada**: Visible públicamente
     - **Rechazada**: No se publica (spam, ofensiva, fuera de contexto)
   - **Panel de moderación**:
     - Lista de reseñas pendientes
     - Por cada reseña:
       - Producto, calificación, comentario, fotos
       - Nombre del cliente
       - Fecha de compra
       - Acciones: Aprobar, Rechazar, Reportar
   - **Aprobación**:
     - Manual: Administrador revisa y aprueba/rechaza
     - Automática (opcional): Publicación inmediata si:
       - Cliente es verificado (ha comprado antes)
       - No contiene palabras prohibidas
       - Calificación >= 3 estrellas
   - **Rechazo**:
     - Seleccionar motivo:
       - Contenido ofensivo
       - Spam
       - No relacionado con el producto
       - Información falsa
     - Enviar notificación al cliente explicando el rechazo
3. Visualización de reseñas:
   - **En página de producto**:
     - **Resumen de calificaciones**:
       - Promedio: 4.7/5 ⭐⭐⭐⭐⭐
       - Total de reseñas: (basado en X opiniones)
       - Distribución:
         - 5 estrellas: ████████████ 75%
         - 4 estrellas: ███ 15%
         - 3 estrellas: █ 5%
         - 2 estrellas: █ 3%
         - 1 estrella: █ 2%
     - **Lista de reseñas**:
       - Ordenadas por: Más recientes, Más útiles, Calificación más alta
       - Por cada reseña:
         - Calificación en estrellas
         - Título (si tiene)
         - Comentario
         - Fotos (si tiene, thumbnail clickeable)
         - Nombre del reseñador + inicial apellido
         - Fecha de compra: "Comprado el 15/01/2026"
         - Badge: "Compra verificada ✓"
         - Respuesta del negocio (si la hay)
         - Botones: "¿Te fue útil?" 👍 (contador de útiles)
       - Paginación: 10 reseñas por página
   - **Filtros de reseñas**:
     - Ver solo 5 estrellas
     - Ver solo con fotos
     - Ver solo con respuesta del negocio
4. Responder a reseñas:
   - **Desde panel de moderación**:
     - Botón "Responder" en cada reseña
     - Textarea para escribir respuesta
     - Plantillas predefinidas:
       - "¡Gracias por tu comentario! Nos alegra que hayas disfrutado..."
       - "Lamentamos que tu experiencia no haya sido la esperada..."
     - Se muestra como: "Respuesta del vendedor" con fecha
   - **Buenas prácticas**:
     - Responder especialmente a reseñas negativas (mejora reputación)
     - Agradecer reseñas positivas
     - Ofrecer soluciones a problemas mencionados
5. Uso de reseñas en el negocio:
   - **Mejora de productos**:
     - Identificar productos con calificación < 3 estrellas
     - Analizar comentarios para detectar problemas recurrentes
     - Decidir si descatalogar o mejorar
   - **Destacar productos populares**:
     - Filtro automático "Mejor calificados" (> 4.5 estrellas)
     - Badge "⭐ Favorito de clientes" en productos con > 20 reseñas y > 4.7
   - **Prueba social**:
     - Mostrar en página principal: "Más de 500 clientes satisfechos"
     - Testimonios destacados en landing page
6. Incentivos para reseñas:
   - **Gamificación**:
     - Puntos de fidelización por dejar reseña (ej: 50 puntos)
     - Cupón de descuento: "Deja tu reseña y recibe 5% OFF en próxima compra"
     - Sorteo mensual entre reseñadores
   - **Badges de reseñador**:
     - "Top Reseñador" si tiene > 10 reseñas
     - "Reseñador Verificado" si compró > 5 veces
7. Reportes de reseñas:
   - **Dashboard de reseñas**:
     - Promedio general de calificación del catálogo: 4.5/5
     - Total de reseñas: 1,234
     - Reseñas pendientes de moderación: 8
     - Productos sin reseñas: 45 (oportunidad de solicitar feedback)
   - **Análisis de sentimiento**:
     - % de reseñas positivas (4-5 estrellas): 85%
     - % de reseñas neutrales (3 estrellas): 10%
     - % de reseñas negativas (1-2 estrellas): 5%
   - **Palabras clave más mencionadas**:
     - Positivas: "excelente", "rápido", "calidad", "recomendado"
     - Negativas: "demora", "dañado", "caro"

**Consideraciones Multi-tenant:**
- Las reseñas son por negocio (no compartidas).
- Un cliente puede reseñar el mismo producto en diferentes negocios.

**Seguridad:**
- Solo clientes verificados (con compra) pueden dejar reseñas.
- Filtro anti-spam para detectar reseñas falsas masivas.
- Las fotos subidas se escanean (no permitir contenido inapropiado).
- Auditoría de reseñas rechazadas (prevenir censura injusta).

**UX:**
- Formulario de reseña simple y rápido (< 2 minutos).
- Previsualización de cómo se verá la reseña antes de enviar.
- Galería de fotos de reseñas clickeable (lightbox).
- Indicador de "Reseña verificada" genera confianza.

**Reglas de Negocio:**
- Las reseñas son permanentes, no se pueden eliminar (solo ocultar si son inapropiadas).
- Los clientes no pueden editar reseñas después de 7 días (evitar manipulación).
- La calificación promedio se recalcula en tiempo real con cada nueva reseña.
- Las reseñas con fotos tienen más visibilidad (se muestran primero).

---

### **RF-ECO-006: Implementar Motor de Recomendaciones**

**Descripción:**  
El sistema debe proporcionar recomendaciones personalizadas de productos a cada cliente basadas en su historial de compras, navegación, productos en el carrito, y comportamiento de clientes similares, aumentando las ventas cruzadas y el valor promedio del pedido.

**Criterios de Aceptación:**
1. Captura de datos de comportamiento:
   - **Eventos rastreados**:
     - Productos visitados (página de producto)
     - Productos agregados al carrito
     - Productos comprados
     - Productos añadidos a lista de deseos (si aplica)
     - Búsquedas realizadas
     - Categorías navegadas
     - Tiempo en cada producto
     - Productos vistos pero no comprados
   - **Almacenamiento**:
     - Por usuario logueado: Vinculado a su cuenta
     - Por usuario anónimo: Cookie de sesión (hasta 30 días)
2. Algoritmos de recomendación:
   - **Basado en historial del usuario** (Filtrado colaborativo):
     - "Porque compraste [Producto A], te puede interesar [Producto B]"
     - Analizar productos comprados juntos históricamente
   - **Basado en usuarios similares**:
     - "Clientes como tú también compraron..."
     - Encontrar usuarios con perfil similar (edad, ubicación, productos comprados)
     - Recomendar productos populares en ese segmento
   - **Basado en contenido** (características del producto):
     - "Productos similares a [Producto que estás viendo]"
     - Misma categoría, marca, rango de precio
   - **Trending/Populares**:
     - "Los más vendidos esta semana"
     - Productos con mayor volumen de ventas reciente
   - **Frecuentemente comprados juntos**:
     - "Los clientes que compraron [A] también compraron [B] y [C]"
     - Algoritmo de Market Basket Analysis
   - **Complementarios**:
     - "Completa tu compra con..."
     - Cerveza → Snacks
     - Vino → Quesos, copas
     - Whisky → Hielo, mezcladores
3. Ubicaciones de recomendaciones:
   - **Página de inicio** (personalizada):
     - "Recomendados para ti" (basado en historial)
     - "Continúa donde lo dejaste" (productos vistos recientemente)
     - "Los más vendidos" (generales)
     - "Nuevos productos que te pueden interesar"
   - **Página de producto**:
     - "Productos similares" (carrusel horizontal)
     - "Frecuentemente comprados juntos" (3-4 productos con checkbox)
     - "Los clientes también vieron" (al final de la página)
   - **Carrito de compras**:
     - "Completa tu compra" (productos complementarios)
     - "Te falta poco para envío gratis" (si aplica, sugerir productos)
   - **Página de confirmación de pedido**:
     - "¿Olvidaste algo?" (last-minute upsell)
   - **Emails**:
     - Email de carrito abandonado: "Vuelve y descubre estos productos"
     - Email post-compra: "Basado en tu compra, te recomendamos..."
4. Personalización de recomendaciones:
   - **Por segmento de cliente**:
     - Clientes nuevos: Productos más populares y con mejor reseña
     - Clientes VIP: Productos premium y exclusivos
     - Clientes con preferencia por cervezas: Más variedades de cerveza
   - **Por contexto**:
     - Día de pago (quincena): Productos premium
     - Fin de semana: Packs para fiestas
     - Época de calor: Cervezas, bebidas frías
     - Época de frío: Vinos, licores
   - **Por ubicación**:
     - Productos populares en su zona/distrito
5. Configuración de recomendaciones:
   - **Panel de administración**:
     - Activar/desactivar motor de recomendaciones
     - Seleccionar algoritmos a usar:
       - Prioridad a ventas (productos que generan más ingreso)
       - Prioridad a márgenes (productos con mayor margen)
       - Prioridad a rotación (productos que necesitan salir)
     - Configurar peso de cada algoritmo (mix personalizado)
   - **Promoción manual**:
     - Forzar ciertos productos en recomendaciones (ej: lanzamiento nuevo)
     - Excluir productos de recomendaciones (ej: descatalogados)
6. A/B Testing de recomendaciones:
   - **Experimentación**:
     - Crear variantes de algoritmos
     - Asignar % de usuarios a cada variante
     - Medir performance:
       - Click-through rate (CTR): % de clicks en recomendaciones
       - Conversion rate: % de compras derivadas de recomendaciones
       - AOV (Average Order Value): Valor promedio cuando se usa recomendación vs. cuando no
   - **Optimización continua**:
     - Activar automáticamente la variante ganadora
7. Métricas de efectividad:
   - **Dashboard de recomendaciones**:
     - Productos recomendados mostrados: 10,000
     - Clicks en recomendaciones: 1,500 (CTR: 15%)
     - Ventas atribuibles a recomendaciones: $5,000 USD (20% de ventas web)
     - Productos más recomendados (y su tasa de conversión)
   - **Impacto en negocio**:
     - Incremento en ticket promedio: +18% (con recomendaciones vs. sin)
     - Ventas cruzadas (cross-sell): 35% de pedidos incluyen producto recomendado
     - Up-selling: 12% compran versión premium recomendada

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio motor de recomendaciones.
- Los datos de comportamiento no se comparten entre negocios.

**Seguridad:**
- Los datos de comportamiento son anónimos (cumplimiento GDPR).
- El usuario puede desactivar tracking en configuración de privacidad.

**UX:**
- Carruseles horizontales con scroll suave.
- "Ver más" expandible sin recargar página.
- Animaciones sutiles al aparecer recomendaciones.
- Productos recomendados con badge "Recomendado para ti 🎯".

**Reglas de Negocio:**
- Las recomendaciones se actualizan en tiempo real según el comportamiento actual.
- No recomendar productos sin stock.
- No recomendar productos que el cliente ya compró recientemente (< 30 días).
- Priorizar productos con buen margen pero sin ser invasivo.

---

## **Submódulo 6.6.2: Gestión de Contenido y Marketing Digital**

---

### **RF-ECO-007: Gestionar Banners y Promociones Web**

**Descripción:**  
El sistema debe permitir crear y gestionar banners promocionales, sliders de imágenes, pop-ups, y contenido destacado en la tienda online, con programación temporal, segmentación de audiencia, y medición de efectividad.

**Criterios de Aceptación:**
1. Tipos de contenido promocional:
   - **Slider principal** (página de inicio):
     - Carrusel de imágenes grandes (1920x600px)
     - Hasta 5 slides
     - Transición automática (configurable: 3-10 seg)
     - Navegación manual (flechas, dots)
   - **Banners laterales/secundarios**:
     - Banners medianos (600x400px)
     - Ubicación: Homepage, categorías, sidebar
   - **Banners de categoría**:
     - Banner específico al entrar a una categoría
     - Ej: Entrar a "Vinos" → Banner "20% OFF en vinos franceses"
   - **Pop-ups**:
     - Modal que aparece sobre el contenido
     - Tipos:
       - Bienvenida (al entrar al sitio)
       - Exit intent (al intentar salir)
       - Tiempo (después de X segundos en el sitio)
       - Scroll (al hacer scroll X% de la página)
   - **Notificación tipo barra** (top bar):
     - Barra delgada arriba del header
     - Ej: "🎉 Envío gratis en compras > $50 USD"
2. Creación de banner/promoción:
   - **Información básica**:
     - Título (interno, no se muestra)
     - Tipo de contenido (slider, banner, pop-up, etc.)
     - Ubicación (homepage, categoría X, todas las páginas)
   - **Contenido visual**:
     - **Opción 1: Subir imagen**:
       - Formatos: JPG, PNG, GIF, WebP
       - Tamaño recomendado según tipo
       - Preview antes de guardar
     - **Opción 2: Diseñador integrado** (simple):
       - Fondo: Color sólido o gradiente
       - Texto: Título, subtítulo, descripción
       - Botón: Texto del botón (ej: "Comprar ahora")
       - Previsualización en tiempo real
   - **Enlace/Acción**:
     - Al hacer clic en el banner:
       - Ir a URL específica (ej: `/categoria/cervezas`)
       - Ir a producto específico
       - Abrir pop-up de registro
       - No hacer nada (solo informativo)
     - Abrir en: Misma pestaña / Nueva pestaña
   - **Call to Action (CTA)**:
     - Texto del botón: "Ver ofertas", "Comprar", "Más info"
     - Color y diseño del botón
3. Programación temporal:
   - **Fechas de vigencia**:
     - Fecha de inicio (automático al guardar o programado)
     - Fecha de fin (opcional, si no tiene, es permanente)
     - Hora de inicio/fin (precisión horaria)
   - **Publicación automática**:
     - El banner se publica automáticamente en la fecha/hora configurada
     - Se oculta automáticamente cuando vence
   - **Recurrencia** (opcional):
     - Repetir cada semana (ej: "Happy Hour todos los viernes")
     - Repetir cada mes (ej: "Promo del mes")
4. Segmentación de audiencia:
   - **Mostrar solo a**:
     - Todos los visitantes (por defecto)
     - Solo nuevos visitantes (cookie de primera visita)
     - Solo clientes registrados
     - Solo clientes no registrados
     - Segmentos específicos (VIP, inactivos, etc.)
   - **Mostrar según ubicación**:
     - Solo en ciertas ciudades/distritos
     - Detectar ubicación por IP
   - **Mostrar según dispositivo**:
     - Solo en desktop
     - Solo en móvil
     - En ambos (con imágenes diferentes)
5. Configuración de pop-ups:
   - **Trigger (disparador)**:
     - Al entrar al sitio
     - Después de X segundos en el sitio
     - Al hacer scroll X% de la página
     - Al intentar salir (exit intent)
     - Al agregar producto al carrito
   - **Frecuencia**:
     - Mostrar una vez por sesión
     - Mostrar una vez al día
     - Mostrar una vez cada X días
     - Siempre (molesto, no recomendado)
   - **Diseño del pop-up**:
     - Tamaño: Pequeño, Mediano, Grande, Pantalla completa
     - Posición: Centro, esquina inferior derecha
     - Fondo oscuro detrás (overlay)
     - Botón de cerrar (X)
   - **Contenido del pop-up**:
     - Título
     - Descripción
     - Imagen/GIF
     - Formulario (ej: captura de email para newsletter)
     - Botón CTA
6. Gestión de banners:
   - **Lista de banners/promociones**:
     - Tabla con: Título, Tipo, Ubicación, Estado, Fechas, Clicks, CTR
     - Filtros: Por tipo, estado (activo, programado, vencido), ubicación
     - Acciones: Editar, Duplicar, Eliminar, Pausar/Activar
   - **Vista previa**:
     - Botón "Previsualizar" para ver cómo se ve en la tienda
     - Sin necesidad de publicar
   - **Reordenamiento**:
     - Si hay múltiples sliders, definir orden de aparición
     - Drag-and-drop para reordenar
7. Medición de efectividad:
   - **Métricas por banner**:
     - Impresiones: Cuántas veces se mostró
     - Clicks: Cuántas veces se hizo click
     - CTR (Click-Through Rate): (Clicks / Impresiones) × 100
     - Conversiones: Ventas atribuidas al banner (si enlaza a producto/categoría)
     - Ingresos generados: Monto de ventas atribuidas
   - **Comparativa**:
     - Ranking de banners por CTR
     - Identificar los más efectivos
     - Desactivar los de bajo rendimiento
   - **A/B Testing**:
     - Crear 2 versiones del mismo banner
     - Mostrar versión A al 50% de usuarios, versión B al 50%
     - Medir cuál tiene mejor CTR
     - Activar el ganador permanentemente
8. Integración con campañas:
   - **UTM tracking**:
     - Agregar parámetros UTM al enlace del banner
     - Rastrear en Google Analytics la fuente del tráfico
     - Ej: `?utm_source=banner&utm_medium=homepage&utm_campaign=verano2026`

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus propios banners.
- Los banners no se comparten entre negocios.

**Seguridad:**
- Requiere permiso: `ecommerce_contenido_gestionar`
- Las imágenes subidas se escanean contra malware.
- Los enlaces se validan (no permitir phishing).

**UX:**
- Creador visual drag-and-drop.
- Galería de templates prediseñados.
- Vista previa en tiempo real mientras edita.
- Calendario visual para ver programación de banners.

**Reglas de Negocio:**
- Máximo 5 sliders activos simultáneamente (evitar saturación).
- Los pop-ups deben respetar la experiencia del usuario (no ser invasivos).
- Las imágenes deben optimizarse para carga rápida (< 200KB).
- Los banners vencidos se archivan automáticamente (no se eliminan, para análisis histórico).

---

### **RF-ECO-008: Gestionar Newsletter y Campañas de Email**

**Descripción:**  
El sistema debe permitir crear y enviar campañas de email marketing a clientes suscritos, con plantillas personalizables, segmentación de audiencia, programación de envíos, y análisis de métricas de apertura, clicks, y conversiones.

**Criterios de Aceptación:**
1. Gestión de suscriptores:
   - **Captura de emails**:
     - Formulario en footer de la web: "Suscríbete al newsletter"
     - Pop-up de captura: "Recibe ofertas exclusivas"
     - En checkout: Checkbox "Quiero recibir ofertas por email"
     - Importación masiva desde Excel/CSV
   - **Información del suscriptor**:
     - Email (obligatorio, único)
     - Nombre (opcional)
     - Fecha de suscripción
     - Origen (web, pop-up, checkout, importación)
     - Estado: Activo, Inactivo, Cancelado (unsubscribe)
   - **Doble opt-in** (recomendado):
     - Después de suscribirse, enviar email de confirmación
     - El usuario hace click en "Confirmar suscripción"
     - Solo después se activa para recibir emails
     - Cumplimiento con leyes anti-spam
2. Segmentación de lista:
   - **Listas predefinidas**:
     - Todos los suscriptores
     - Solo clientes (han comprado)
     - Solo prospectos (no han comprado)
     - Clientes VIP
     - Clientes inactivos
   - **Segmentación personalizada**:
     - Por ubicación
     - Por productos comprados
     - Por monto gastado
     - Por fecha de última compra
     - Por engagement (abren emails regularmente)
3. Creación de campaña de email:
   - **Información de la campaña**:
     - Nombre de la campaña (interno)
     - Asunto del email (obligatorio, máx. 60 caracteres)
     - Preheader (texto que aparece después del asunto en bandeja)
     - Remitente: Nombre y email (ej: "DrinkGo <ofertas@drinkgo.com>")
   - **Diseño del email**:
     - **Opción 1: Editor visual drag-and-drop**:
       - Bloques: Texto, Imagen, Botón, Producto, Espaciador, Divisor
       - Arrastrar y soltar para construir el email
       - Personalización: Colores, fuentes, tamaños
     - **Opción 2: Plantillas prediseñadas**:
       - "Oferta de la semana"
       - "Nuevos productos"
       - "Carrito abandonado"
       - "Feliz cumpleaños"
       - Personalizar texto e imágenes
     - **Opción 3: HTML personalizado** (avanzado):
       - Subir HTML propio
       - Para diseñadores con código personalizado
   - **Personalización con variables**:
     - `{{nombre}}`: Nombre del suscriptor
     - `{{producto_favorito}}`: Producto más comprado
     - `{{puntos}}`: Puntos de fidelización
     - Se reemplazan automáticamente al enviar
4. Contenido dinámico:
   - **Bloques condicionales**:
     - Mostrar contenido diferente según segmento
     - Ej: Clientes VIP ven "20% OFF exclusivo", otros ven "10% OFF"
   - **Recomendaciones de productos**:
     - Bloque que se llena automáticamente con productos recomendados
     - Basado en historial del suscriptor
5. Configuración de envío:
   - **Destinatarios**:
     - Seleccionar lista/segmento
     - O ingresar emails manualmente
     - Excluir suscriptores (ej: excluir los que ya compraron)
   - **Programación**:
     - Enviar ahora (inmediatamente)
     - Programar fecha y hora específica
     - Recurrente: Semanal, mensual (ej: newsletter semanal todos los lunes 10 AM)
   - **Prueba antes de enviar**:
     - Enviar email de prueba a tu propio email
     - Revisar que se ve correctamente
     - Probar en diferentes clientes de email (Gmail, Outlook, etc.)
6. Cumplimiento legal:
   - **Link de desuscripción obligatorio**:
     - Footer del email debe tener "Desuscribirse"
     - Al hacer click, se marca como `'cancelado'`
     - No recibe más emails
   - **Información del remitente**:
     - Nombre y dirección física del negocio
     - Link a política de privacidad
7. Envío y entregabilidad:
   - **Proveedor de envío**:
     - Integración con servicios de email marketing:
       - SendGrid
       - Mailchimp
       - Brevo (ex-Sendinblue)
       - Amazon SES
     - Configurar API Key
   - **Validación de emails**:
     - Verificar emails válidos antes de enviar
     - Eliminar rebotes (bounces) automáticamente
   - **Warmup de dominio**:
     - Si es dominio nuevo, enviar gradualmente
     - Evitar ser marcado como spam
8. Métricas y análisis:
   - **Dashboard de campaña**:
     - Emails enviados: 5,000
     - Emails entregados: 4,850 (97%)
     - Rebotes (bounces): 150 (3%)
       - Hard bounces: Email no existe
       - Soft bounces: Buzón lleno, temporalmente no disponible
     - Tasa de apertura: 1,200 (24.7%)
     - Clicks: 300 (6.2% de enviados, 25% de aperturas)
     - Desuscripciones: 10 (0.2%)
     - Quejas de spam: 2 (0.04%)
   - **Clicks por enlace**:
     - Ver qué enlaces tuvieron más clicks
     - Mapa de calor del email (dónde hicieron más click)
   - **Conversiones**:
     - Ventas atribuibles al email: 50
     - Ingresos generados: $2,500 USD
     - ROI de la campaña: (Ingresos - Costo) / Costo × 100
   - **Análisis temporal**:
     - Mejor hora/día para enviar (basado en tasa de apertura)
     - Optimizar futuros envíos
9. Automatizaciones:
   - **Emails automáticos**:
     - **Bienvenida**: Al suscribirse
     - **Carrito abandonado**: 1 hora después de abandonar (RF-ECO-003)
     - **Post-compra**: Agradecer + recomendar productos
     - **Cumpleaños**: Descuento especial
     - **Reactivación**: Si no compra en 90 días
   - **Flujos de automatización**:
     - Serie de emails secuenciales
     - Ej: "Serie de bienvenida" (3 emails en 7 días)
       - Día 1: Bienvenida + 10% OFF
       - Día 3: Conoce nuestros productos
       - Día 7: Última oportunidad para usar tu descuento

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propia lista de suscriptores.
- Las campañas no se cruzan entre negocios.

**Seguridad:**
- Cumplimiento con CAN-SPAM Act y GDPR.
- Opt-in obligatorio (no comprar listas de emails).
- Desuscripción debe procesarse inmediatamente.
- Los datos de suscriptores son confidenciales.

**UX:**
- Editor de email tipo drag-and-drop intuitivo.
- Vista previa en desktop y móvil antes de enviar.
- Galería de plantillas profesionales.
- Estadísticas visuales con gráficos.

**Reglas de Negocio:**
- Máximo 2 emails por semana a la misma lista (evitar saturación).
- Los emails de carrito abandonado tienen tasa de conversión 3x mayor que emails genéricos.
- Las líneas de asunto con emojis tienen 10% más apertura.
- Enviar entre martes y jueves 10-11 AM tiene mejor tasa de apertura.

---

### **RF-ECO-009: Implementar Programa de Afiliados**

**Descripción:**  
El sistema debe proporcionar un programa de afiliados donde influencers, bloggers, y clientes leales pueden promover productos y ganar comisiones por ventas referidas, con tracking de conversiones, cálculo automático de comisiones, y panel de afiliado.

**Criterios de Aceptación:**
1. Inscripción al programa de afiliados:
   - **Solicitud de afiliación**:
     - Formulario público: "Únete como afiliado"
     - Datos a capturar:
       - Nombre completo
       - Email
       - Teléfono
       - URL de sitio web / redes sociales (si aplica)
       - Audiencia estimada (número de seguidores)
       - ¿Por qué quieres ser afiliado?
   - **Aprobación manual**:
     - Administrador revisa solicitudes
     - Puede: Aprobar, Rechazar, Solicitar más información
     - Al aprobar:
       - Se crea cuenta de afiliado
       - Se genera código único de afiliado (ej: JUAN2024)
       - Se envía email de bienvenida con credenciales
2. Configuración del programa:
   - **Comisiones**:
     - % de comisión por venta (ej: 10%)
     - Comisión fija por venta (ej: $5 USD)
     - Diferentes comisiones por categoría:
       - Cervezas: 5%
       - Vinos premium: 15%
       - Licores: 10%
   - **Condiciones**:
     - Monto mínimo de compra para generar comisión (ej: $30 USD)
     - Cookie duration: Tiempo de validez del referido (ej: 30 días)
       - Si el cliente hace click en link de afiliado, tiene 30 días para comprar
       - La comisión se atribuye al afiliado dentro de ese plazo
     - Comisión solo en primera compra o en todas las compras
   - **Pagos**:
     - Umbral mínimo para pago (ej: acumular $100 USD para cobrar)
     - Frecuencia de pago: Mensual, quincenal
     - Método de pago: Transferencia bancaria, PayPal, Yape
3. Panel del afiliado:
   - **Login independiente**:
     - URL: `/afiliado/login`
     - Credenciales únicas
   - **Dashboard del afiliado**:
     - Resumen de performance:
       - Clicks en links de afiliado: 1,234
       - Ventas generadas: 45
       - Tasa de conversión: 3.6%
       - Comisiones ganadas (mes actual): $150 USD
       - Comisiones acumuladas (pendientes de pago): $350 USD
       - Próximo pago programado: 05/02/2026
     - Gráficos:
       - Evolución de clicks y ventas
       - Top productos que generan más comisión
   - **Herramientas de promoción**:
     - **Links de afiliado**:
       - Link general: `https://tienda.com?ref=JUAN2024`
       - Links por producto: `https://tienda.com/producto/cerveza-corona?ref=JUAN2024`
       - Links por categoría: `https://tienda.com/categoria/vinos?ref=JUAN2024`
       - Copiable con un click
     - **Banners promocionales**:
       - Galería de banners prediseñados
       - Diferentes tamaños (300x250, 728x90, etc.)
       - Descargar o copiar código HTML
       - Ya incluyen link de afiliado
     - **Cupones personalizados**:
       - Crear cupones con su código de afiliado
       - Ej: "JUAN10" = 10% OFF + comisión para Juan
   - **Reportes detallados**:
     - Tabla de conversiones:
       - Fecha, Cliente (anónimo), Producto, Monto, Comisión
     - Filtros por fecha, producto, estado
     - Exportar a Excel
4. Tracking de referidos:
   - **Funcionamiento técnico**:
     - Al hacer click en link de afiliado:
       - Se guarda cookie en navegador del cliente: `ref=JUAN2024`
       - Duración: 30 días (configurable)
     - Al comprar:
       - Se verifica si hay cookie de referido válida
       - Se atribuye la venta al afiliado
       - Se calcula y registra la comisión
   - **Reglas de atribución**:
     - Último click gana: Si el cliente hace click en links de 2 afiliados, se atribuye al último
     - Primer click gana: Se atribuye al primer afiliado que lo refirió (opcional)
5. Cálculo de comisiones:
   - **Cálculo automático**:
     - Al completarse una venta con referido:
       - Subtotal de productos × % de comisión = Comisión
       - Ejemplo: Venta de $100 × 10% = $10 comisión
     - Estado de comisión:
       - Pendiente: Recién generada
       - Aprobada: Venta confirmada, lista para pagar
       - Pagada: Ya se transfirió al afiliado
       - Cancelada: Si la venta fue devuelta/cancelada
   - **Validaciones**:
     - Si el cliente devuelve productos, revertir comisión
     - Si es fraude detectado, no pagar comisión
6. Pagos a afiliados:
   - **Proceso de pago**:
     - Vista de "Comisiones por pagar"
     - Filtrar afiliados con saldo >= umbral mínimo
     - Generar reporte de pago (Excel con lista de afiliados y montos)
     - Realizar transferencias bancarias
     - Marcar comisiones como `'pagadas'`
     - Enviar comprobante de pago al afiliado
   - **Historial de pagos**:
     - Fecha, monto, método, comprobante
     - El afiliado puede ver su historial en su panel
7. Gestión de afiliados (administrador):
   - **Lista de afiliados**:
     - Tabla con: Nombre, código, ventas generadas, comisiones, estado
     - Filtros: Por estado (activo, inactivo, suspendido), por performance
     - Acciones: Ver detalle, Editar, Suspender, Eliminar
   - **Performance de afiliados**:
     - Ranking por ventas generadas
     - Ranking por comisiones ganadas
     - Identificar top performers para incentivar
   - **Configuración individual**:
     - Ajustar comisión específica para un afiliado
     - Ej: Top afiliado: 15% en vez de 10%
8. Reportes del programa:
   - **Dashboard del programa**:
     - Afiliados activos: 45
     - Ventas totales generadas: $25,000 USD
     - Comisiones pagadas (mes): $2,500 USD
     - ROI del programa: Ventas / Comisiones = 10x
   - **Comparativa con otros canales**:
     - Afiliados vs. Publicidad pagada vs. Orgánico
     - Identificar rentabilidad de cada canal

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio programa de afiliados.
- Los afiliados están vinculados a un negocio específico.

**Seguridad:**
- Los links de afiliado tienen protección contra fraude (clicks falsos).
- Auditoría de conversiones sospechosas (misma IP, patrón anormal).
- Los afiliados no ven información personal de clientes (privacidad).

**UX:**
- Panel de afiliado simple e intuitivo.
- Copiado de links con un click ("Copiado ✓").
- Estadísticas visuales fáciles de entender.
- Notificación cuando ganan una comisión.

**Reglas de Negocio:**
- Los afiliados deben promover de forma ética (no spam, no anuncios engañosos).
- Las comisiones se calculan sobre el subtotal (sin incluir envío ni impuestos).
- Los afiliados pueden ser suspendidos si detectan fraude.
- El programa debe ser rentable: Comisiones < Margen de ganancia.

---

## **Submódulo 6.6.3: Dashboards Ejecutivos y KPIs**

---

### **RF-REP-001: Dashboard Ejecutivo General**

**Descripción:**  
El sistema debe proporcionar un dashboard ejecutivo centralizado con los KPIs más importantes del negocio en tiempo real, visualizaciones interactivas, comparativas con períodos anteriores, y acceso rápido a reportes detallados para toma de decisiones estratégicas.

**Criterios de Aceptación:**
1. Vista principal del dashboard:
   - **KPIs principales** (cards grandes):
     - **Ventas del día**:
       - Monto: $5,450 USD
       - Variación vs. ayer: +12% ↑ (en verde)
       - Progreso vs. meta del día: 85% (barra de progreso)
     - **Ventas del mes**:
       - Monto: $125,000 USD
       - Variación vs. mes anterior: +8% ↑
       - Progreso vs. meta mensual: 72%
     - **Número de transacciones**:
       - Hoy: 145 ventas
       - Mes: 2,340 ventas
       - Ticket promedio: $53.42 USD
     - **Utilidad neta del mes**:
       - Monto: $28,000 USD
       - Margen: 22.4%
       - Variación vs. mes anterior: +5%
   - **Alertas críticas** (panel lateral):
     - 🔴 Stock crítico: 12 productos con stock < 10 unidades
     - 🟡 Cuentas por pagar vencidas: 3 facturas ($8,500 USD)
     - 🟡 Sesión de caja abierta > 12 horas (Caja 1)
     - 🟢 Sistema operativo normalmente
2. Gráficos interactivos:
   - **Ventas por canal** (gráfico de pastel):
     - POS: 60%
     - E-commerce: 25%
     - Delivery apps (Rappi, etc.): 15%
     - Clickeable para ver detalle
   - **Tendencia de ventas** (gráfico de línea):
     - Últimos 30 días
     - Línea de ventas diarias
     - Línea de meta diaria
     - Promedio móvil (7 días)
   - **Top 10 productos más vendidos** (gráfico de barras horizontal):
     - Producto, unidades vendidas, ingresos generados
     - Código de colores por categoría
   - **Ventas por hora del día** (gráfico de área):
     - Identificar horas pico
     - Útil para staffing
3. Métricas operativas:
   - **Inventario**:
     - Valor total del inventario: $45,000 USD
     - Productos únicos en stock: 350
     - Productos con bajo stock: 12
     - Rotación de inventario (días): 18
   - **Clientes**:
     - Clientes activos (compraron en últimos 30 días): 450
     - Nuevos clientes (mes): 85
     - Tasa de retención: 65%
     - NPS (Net Promoter Score): 72
   - **Pedidos**:
     - Pedidos pendientes: 8
     - Pedidos en preparación: 3
     - Tiempo promedio de entrega: 35 min
     - Tasa de cumplimiento: 95%
4. Métricas financieras:
   - **Flujo de caja**:
     - Saldo actual en caja y bancos: $12,500 USD
     - Ingresos del día: +$5,450
     - Salidas del día: -$2,100
     - Flujo neto: +$3,350
     - Proyección para fin de mes: +$8,000 USD (color verde)
   - **Cuentas por cobrar/pagar**:
     - Por cobrar: $15,000 USD
     - Por pagar: $22,000 USD
     - Capital de trabajo: -$7,000 USD (alerta)
5. Filtros y período:
   - **Selector de período**:
     - Hoy
     - Ayer
     - Últimos 7 días
     - Últimos 30 días
     - Este mes
     - Mes anterior
     - Este año
     - Rango personalizado (daterangepicker)
   - **Filtros adicionales**:
     - Por sede (si hay múltiples)
     - Por canal de venta
     - Por categoría de producto
   - **Comparativa**:
     - Toggle "Comparar con período anterior"
     - Muestra datos lado a lado
6. Personalización del dashboard:
   - **Widgets arrastrables**:
     - El usuario puede reordenar los gráficos
     - Drag-and-drop para organizar según preferencia
   - **Mostrar/ocultar widgets**:
     - Checkbox para seleccionar qué métricas mostrar
     - Guardar configuración por usuario
   - **Múltiples dashboards**:
     - Dashboard ejecutivo (gerente general)
     - Dashboard de ventas (gerente de ventas)
     - Dashboard de inventario (encargado de almacén)
     - Dashboard financiero (contador)
7. Acciones rápidas:
   - **Botones de acción directa desde el dashboard**:
     - "Ver detalles" en cada KPI → Redirige a reporte detallado
     - "Resolver" en alertas → Abre modal para tomar acción
     - "Exportar" en gráficos → Descarga PNG o datos en Excel
     - "Compartir" → Generar link de vista previa del dashboard
8. Actualización en tiempo real:
   - **Datos en vivo**:
     - Ventas del día se actualiza automáticamente cada 30 segundos
     - Sin necesidad de recargar página (WebSockets o polling)
   - **Indicador de actualización**:
     - Timestamp: "Última actualización: hace 15 segundos"
     - Spinner mientras actualiza

**Consideraciones Multi-tenant:**
- Cada negocio ve solo sus propios datos en el dashboard.
- Los dashboards son independientes por negocio.

**Seguridad:**
- Requiere permiso: `dashboard_ejecutivo_ver`
- Solo gerencia y administración tienen acceso.
- Los datos sensibles (utilidad neta, márgenes) requieren permiso adicional.

**UX:**
- Diseño limpio y moderno (inspirado en Google Analytics, Tableau).
- Números grandes y legibles.
- Código de colores consistente: Verde (positivo), rojo (negativo), amarillo (alerta).
- Responsive: Se adapta a tablets y móviles.
- Modo oscuro (opcional, para uso nocturno).

**Reglas de Negocio:**
- Los datos del dashboard deben ser precisos (confiabilidad al 100%).
- Las métricas críticas (ventas, stock) deben actualizarse en tiempo real.
- El dashboard debe cargar en < 3 segundos (performance).
- Las alertas deben ser accionables (no solo informativas).

---

### **RF-REP-002: Dashboard de Ventas y Performance**

**Descripción:**  
El sistema debe proporcionar un dashboard especializado en análisis de ventas con métricas detalladas de rendimiento por producto, categoría, canal, vendedor, y cliente, permitiendo identificar oportunidades de crecimiento y áreas de mejora.

**Criterios de Aceptación:**
1. Métricas de ventas detalladas:
   - **Ventas por período**:
     - Total de ventas: $125,000 USD
     - Cantidad de transacciones: 2,340
     - Ticket promedio: $53.42 USD
     - Unidades vendidas: 8,500
   - **Desglose por modalidad**:
     - En tienda (POS): $75,000 USD (60%)
     - Delivery: $37,500 USD (30%)
     - Pickup: $12,500 USD (10%)
   - **Comparativa**:
     - vs. Mismo período año anterior: +15%
     - vs. Meta del mes: 85% alcanzado
2. Análisis por producto:
   - **Top productos más vendidos**:
     - Tabla con: Producto, Categoría, Unidades vendidas, Ingresos, Margen, % del total
     - Ordenable por cualquier columna
     - Top 20 productos
   - **Productos con bajo rendimiento**:
     - Productos con < 5 unidades vendidas en el mes
     - Analizar: ¿Descatalogar? ¿Hacer promoción?
   - **Productos de alto margen**:
     - Productos con margen > 40%
     - Oportunidad de promover más
   - **Análisis ABC**:
     - Categoría A: 20% de productos generan 80% de ingresos
     - Categoría B: 30% de productos generan 15% de ingresos
     - Categoría C: 50% de productos generan 5% de ingresos
3. Análisis por categoría:
   - **Ventas por categoría** (gráfico de barras):
     - Cervezas: $50,000 (40%)
     - Vinos: $30,000 (24%)
     - Licores: $25,000 (20%)
     - Snacks: $15,000 (12%)
     - Otros: $5,000 (4%)
   - **Crecimiento por categoría**:
     - Cervezas: +10% vs. mes anterior
     - Vinos: +25% (crecimiento fuerte)
     - Licores: -5% (decrecimiento)
   - **Margen por categoría**:
     - Identificar categorías más rentables
4. Análisis por canal de venta:
   - **Performance por canal**:
     - POS: 1,500 transacciones, ticket promedio $50
     - E-commerce: 650 transacciones, ticket promedio $58 (mayor)
     - Rappi: 190 transacciones, ticket promedio $52
   - **Tendencias**:
     - E-commerce creciendo +30% mensual
     - POS estable
     - Plataformas externas creciendo +15%
5. Análisis por vendedor/cajero:
   - **Performance individual**:
     - Tabla con: Vendedor, Ventas realizadas, Monto total, Ticket promedio, Productos/venta
     - Ranking de vendedores
   - **Métricas de eficiencia**:
     - Transacciones por hora
     - Tiempo promedio de atención
     - Tasa de upselling (venta de productos adicionales)
   - **Comisiones** (si aplica):
     - Comisiones ganadas por vendedor
     - Basadas en metas alcanzadas
6. Análisis de clientes:
   - **Segmentación RFM**:
     - Distribución de clientes en segmentos (Champions, Leales, En riesgo, etc.)
     - Ventas por segmento
   - **Nuevos vs. Recurrentes**:
     - Ventas de nuevos clientes: 15%
     - Ventas de clientes recurrentes: 85%
   - **Clientes más valiosos**:
     - Top 10 clientes por monto gastado
     - Análisis de concentración: ¿Dependencia de pocos clientes?
7. Análisis temporal:
   - **Ventas por día de la semana**:
     - Identificar días más fuertes (viernes, sábado)
     - Días más débiles (lunes, martes)
   - **Ventas por hora**:
     - Horas pico: 7-9 PM
     - Horas valle: 10 AM - 2 PM
     - Optimizar staffing según demanda
   - **Estacionalidad**:
     - Comparar mismo mes de años anteriores
     - Identificar patrones estacionales
8. Análisis geográfico (para delivery):
   - **Ventas por zona/distrito**:
     - Mapa de calor con ventas por zona
     - Top 5 distritos con más ventas
   - **Oportunidades de expansión**:
     - Zonas con demanda pero sin cobertura
     - Análisis de competencia por zona
9. Conversión y funnel:
   - **E-commerce funnel**:
     - Visitantes únicos: 10,000
     - Agregaron al carrito: 2,000 (20%)
     - Iniciaron checkout: 1,200 (12%)
     - Completaron compra: 850 (8.5%)
     - Identificar dónde se pierden clientes
   - **Tasa de conversión por canal**:
     - Redes sociales: 5%
     - Google Ads: 8%
     - Orgánico: 12%
     - Email marketing: 15%

**Consideraciones Multi-tenant:**
- Los datos de ventas son por negocio.
- Los vendedores solo ven sus propias métricas (o todas si son supervisores).

**Seguridad:**
- Requiere permiso: `dashboard_ventas_ver`
- Los datos de comisiones son sensibles (solo gerencia).

**UX:**
- Gráficos interactivos con drill-down (click para ver detalle).
- Tablas con ordenamiento y búsqueda.
- Exportación de cualquier gráfico/tabla a Excel/PDF.
- Comparativas visuales con barras lado a lado.

**Reglas de Negocio:**
- Las ventas se consideran completas solo cuando están pagadas.
- Las devoluciones se restan de las ventas del período.
- Los datos de vendedores son para evaluación de desempeño (transparencia).

---

### **RF-REP-003: Dashboard de Inventario y Stock**

**Descripción:**  
El sistema debe proporcionar un dashboard especializado en gestión de inventario con visibilidad en tiempo real de niveles de stock, alertas de reorden, análisis de rotación, y métricas de eficiencia logística.

**Criterios de Aceptación:**
1. Vista general de inventario:
   - **Métricas principales**:
     - Valor total del inventario: $45,000 USD
     - Número de SKUs: 350
     - Productos en stock: 320
     - Productos sin stock: 30
     - Productos con bajo stock: 12 (< punto de reorden)
   - **Estado de salud del inventario**:
     - Inventario saludable: 75%
     - Con alertas: 20%
     - Crítico: 5%
2. Alertas de stock:
   - **Panel de alertas** (prioritario):
     - 🔴 Stock crítico: 8 productos con 0 unidades
     - 🟡 Bajo stock: 12 productos < punto de reorden
     - 🟠 Productos próximos a vencer: 5 productos < 30 días
     - 🔵 Sobrestockados: 3 productos con > 6 meses de inventario
   - **Acción rápida**:
     - Botón "Generar orden de compra" directamente desde alerta
     - Click en producto → Ver detalle y movimientos
3. Análisis de rotación:
   - **Rotación de inventario**:
     - Promedio general: 18 días
     - Por categoría:
       - Cervezas: 10 días (rotación rápida)
       - Vinos: 25 días
       - Licores premium: 45 días (rotación lenta)
   - **Productos de lenta rotación**:
     - Lista de productos con > 90 días sin venta
     - Valor inmovilizado: $3,500 USD
     - Acción sugerida: Promoción o descatalogación
   - **Productos de alta rotación**:
     - Oportunidad de aumentar stock
     - Evitar quiebres de stock
4. Análisis por categoría:
   - **Distribución de inventario** (gráfico de pastel):
     - Cervezas: 40% del valor
     - Vinos: 30%
     - Licores: 20%
     - Snacks: 10%
   - **Stock vs. Ventas**:
     - Comparar stock actual con ventas promedio
     - Identificar desbalances
     - Ej: Cervezas = 30% del stock pero 50% de ventas → Aumentar stock
5. Análisis de movimientos:
   - **Entradas y salidas** (gráfico de barras):
     - Por día/semana/mes
     - Entradas (compras): Barras en azul
     - Salidas (ventas): Barras en naranja
     - Balance neto: Línea
   - **Movimientos por tipo**:
     - Ventas: 80%
     - Mermas/vencimientos: 3%
     - Devoluciones: 2%
     - Ajustes de inventario: 1%
     - Muestras/degustaciones: 0.5%
6. Análisis de vencimientos:
   - **Productos próximos a vencer**:
     - Tabla con: Producto, Lote, Fecha de vencimiento, Días restantes, Unidades, Valor
     - Ordenado por urgencia
     - Código de colores:
       - Rojo: < 15 días
       - Amarillo: 15-30 días
       - Verde: 31-60 días
   - **Acciones**:
     - Botón "Aplicar descuento" para liquidar
     - Marcar como "Para promoción"
7. Análisis de mermas:
   - **Mermas del período**:
     - Total de mermas: $450 USD (1% del inventario)
     - Por motivo:
       - Vencimiento: 60%
       - Daño/rotura: 30%
       - Robo/pérdida: 10%
   - **Tendencia de mermas**:
     - Comparar con períodos anteriores
     - Meta: Mantener mermas < 2%
8. Valorización de inventario:
   - **Métodos de valorización**:
     - FIFO (First In, First Out): $45,000 USD
     - Promedio ponderado: $44,500 USD
     - LIFO (Last In, First Out): $45,500 USD
   - **Análisis de márgenes**:
     - Margen bruto promedio del inventario: 35%
     - Productos de alto margen: Top 20
     - Productos de bajo margen: Bottom 20
9. Eficiencia logística:
   - **Precisión de inventario**:
     - Diferencias entre sistema y conteo físico
     - Meta: > 98% de precisión
     - Última auditoría: 97.5%
   - **Tiempo de reposición**:
     - Tiempo promedio desde orden de compra hasta recepción: 5 días
     - Por proveedor (identificar los más rápidos)
   - **Fill rate** (tasa de cumplimiento):
     - % de pedidos completos sin faltantes: 92%
     - Meta: > 95%
10. Proyección de necesidades:
    - **Análisis de demanda futura**:
      - Basado en ventas históricas + tendencias
      - Proyección de stock necesario próximos 30 días
    - **Sugerencias de compra**:
      - Lista de productos a ordenar
      - Cantidad sugerida por producto
      - Punto de reorden alcanzado o proyectado

**Consideraciones Multi-tenant:**
- Cada negocio ve solo su inventario.
- Las alertas son específicas por negocio.

**Seguridad:**
- Requiere permiso: `dashboard_inventario_ver`
- Los datos de valorización son sensibles (solo administración).

**UX:**
- Mapa de calor de categorías (más stock = color más intenso).
- Alertas interactivas con acciones directas.
- Drill-down por categoría → producto → lote.
- Exportación de reportes de stock.

**Reglas de Negocio:**
- El inventario negativo no está permitido (excepción: preventas).
- Las mermas deben justificarse y aprobarse.
- Los productos vencidos deben retirarse inmediatamente del inventario.
- La rotación ideal varía por categoría de producto.

---

## **Submódulo 6.6.4: Reportes Avanzados y Analítica**

---

### **RF-REP-004: Generador de Reportes Personalizados**

**Descripción:**  
El sistema debe proporcionar un generador de reportes personalizado donde los usuarios pueden seleccionar métricas, dimensiones, filtros, y formato de visualización para crear reportes a medida que respondan preguntas específicas del negocio.

**Criterios de Aceptación:**
1. Constructor de reportes:
   - **Paso 1: Selección de fuente de datos**:
     - Ventas
     - Inventario
     - Clientes
     - Finanzas
     - Pedidos
     - Productos
   - **Paso 2: Selección de métricas** (qué medir):
     - Ejemplos para Ventas:
       - Monto total de ventas
       - Número de transacciones
       - Ticket promedio
       - Unidades vendidas
       - Margen de ganancia
       - Descuentos aplicados
     - Multiselección (hasta 10 métricas)
   - **Paso 3: Selección de dimensiones** (cómo agrupar):
     - Por tiempo: Día, semana, mes, año
     - Por producto: Producto, categoría, marca
     - Por cliente: Cliente, segmento, ubicación
     - Por canal: POS, e-commerce, plataformas
     - Por vendedor/cajero
   - **Paso 4: Filtros**:
     - Período: Rango de fechas
     - Categoría de producto: Seleccionar categorías específicas
     - Sede: Si hay múltiples sedes
     - Estado: Completado, cancelado, pendiente
     - Monto: Mayor a, menor a, entre
     - Cliente: Tipo (nuevo, recurrente, VIP)
   - **Paso 5: Ordenamiento**:
     - Ordenar por: Métrica seleccionada
     - Dirección: Ascendente / Descendente
     - Limitar a Top N resultados (ej: Top 20)
2. Visualización:
   - **Tipo de visualización**:
     - Tabla (datos tabulares)
     - Gráfico de barras
     - Gráfico de línea
     - Gráfico de pastel
     - Gráfico de área
     - Mapa de calor
     - Tabla pivote (Excel-like)
   - **Configuración visual**:
     - Colores personalizados
     - Mostrar/ocultar leyenda
     - Mostrar valores en los gráficos
     - Título del reporte
3. Vista previa y ajustes:
   - **Vista previa en tiempo real**:
     - Al seleccionar métricas/filtros, ver resultado inmediatamente
     - Ajustar hasta obtener el reporte deseado
   - **Datos de muestra**:
     - Mostrar primeras 100 filas
     - Indicar total de registros
4. Guardar y compartir:
   - **Guardar reporte**:
     - Nombre del reporte
     - Descripción (opcional)
     - Guardar configuración para reutilizar
     - Aparece en "Mis Reportes"
   - **Compartir reporte**:
     - Generar link público (con expira después de 7 días)
     - Compartir con otros usuarios del sistema
     - Permisos: Solo lectura / Puede editar
   - **Programar envío**:
     - Enviar por email automáticamente
     - Frecuencia: Diaria, semanal, mensual
     - Destinatarios: Lista de emails
     - Formato: PDF, Excel
5. Exportación:
   - **Formatos disponibles**:
     - Excel (.xlsx): Datos crudos para análisis
     - CSV: Importar a otras herramientas
     - PDF: Reporte formateado profesional
     - PNG/JPG: Gráfico como imagen
   - **Opciones de exportación**:
     - Solo datos visibles / Todos los datos
     - Incluir gráficos / Solo tabla
6. Reportes predefinidos (templates):
   - **Galería de plantillas**:
     - "Top 10 productos más vendidos"
     - "Ventas por categoría últimos 30 días"
     - "Clientes más valiosos"
     - "Análisis de margen por producto"
     - "Performance de vendedores"
   - **Usar plantilla**:
     - Click en plantilla → Se prellenan métricas/filtros
     - Ajustar según necesidad
     - Guardar como copia personalizada
7. Cálculos personalizados:
   - **Campos calculados**:
     - Crear métricas personalizadas
     - Ej: "Margen % = (Precio venta - Costo) / Precio venta × 100"
     - Ej: "AOV Growth = (AOV mes actual - AOV mes anterior) / AOV mes anterior"
   - **Fórmulas soportadas**:
     - Suma, Resta, Multiplicación, División
     - Promedio, Mín, Máx
     - Contar, Contar únicos
     - Porcentaje del total
8. Análisis avanzado:
   - **Comparativas**:
     - Comparar dos períodos lado a lado
     - Ej: Enero 2026 vs. Enero 2025
     - Mostrar diferencia absoluta y porcentual
   - **Tendencias**:
     - Línea de tendencia en gráficos
     - Regresión lineal
     - Proyección futura (próximos 30 días)
   - **Análisis de cohortes**:
     - Agrupar clientes por mes de primera compra
     - Analizar retención por cohorte

**Consideraciones Multi-tenant:**
- Los reportes solo muestran datos del negocio del usuario.
- Los reportes compartidos respetan permisos del negocio.

**Seguridad:**
- Requiere permiso: `reportes_personalizados_crear`
- Los usuarios solo ven métricas según sus permisos.
- Los reportes compartidos públicamente no exponen datos sensibles.

**UX:**
- Constructor tipo drag-and-drop intuitivo.
- Vista previa en tiempo real sin generar el reporte completo.
- Galería de templates para empezar rápido.
- Ayuda contextual en cada paso.

**Reglas de Negocio:**
- Los reportes pesados (> 10,000 registros) se procesan en background.
- Los reportes se pueden guardar pero ocupan espacio (límite por usuario).
- Las exportaciones están limitadas a 50,000 filas máximo.

---

### **RF-REP-005: Análisis Predictivo y Forecasting**

**Descripción:**  
El sistema debe proporcionar capacidades de análisis predictivo utilizando algoritmos de machine learning para pronosticar ventas futuras, demanda de productos, comportamiento de clientes, y tendencias del negocio, ayudando en la planificación estratégica.

**Criterios de Aceptación:**
1. Pronóstico de ventas:
   - **Predicción de ventas futuras**:
     - Horizonte de predicción: Próximos 7, 30, 60, 90 días
     - Basado en:
       - Ventas históricas (mínimo 90 días de datos)
       - Estacionalidad (patrones semanales, mensuales, anuales)
       - Tendencias (crecimiento, decrecimiento)
       - Eventos especiales (feriados, campañas)
   - **Visualización**:
     - Gráfico de línea con:
       - Ventas históricas (línea sólida)
       - Ventas proyectadas (línea punteada)
       - Rango de confianza (área sombreada: escenario pesimista - optimista)
   - **Precisión del modelo**:
     - MAPE (Mean Absolute Percentage Error): 8.5%
     - Indica qué tan preciso es el pronóstico
     - Se recalcula mensualmente con nuevos datos
2. Pronóstico de demanda por producto:
   - **Predicción de unidades a vender**:
     - Por cada producto:
       - Unidades esperadas próximos 30 días
       - Rango de confianza (mín - máx)
     - Útil para planificar compras
   - **Productos con demanda creciente**:
     - Identificar productos con tendencia al alza
     - Oportunidad de aumentar stock
   - **Productos con demanda decreciente**:
     - Identificar productos perdiendo popularidad
     - Reducir órdenes futuras
3. Detección de anomalías:
   - **Alertas de comportamientos anormales**:
     - Ventas inusualmente altas/bajas en un día
     - Productos con pico súbito de demanda
     - Caída abrupta en tráfico web
   - **Investigación de causas**:
     - ¿Campaña de marketing?
     - ¿Problema técnico?
     - ¿Acción de competencia?
     - ¿Factor externo (clima, evento)?
4. Segmentación predictiva de clientes:
   - **Propensión a comprar**:
     - Score de 0-100 indicando probabilidad de compra próximos 30 días
     - Basado en:
       - Frecuencia histórica
       - Recencia de última compra
       - Engagement (emails abiertos, visitas al sitio)
   - **Propensión al churn** (abandono):
     - Identificar clientes con alta probabilidad de no volver
     - Score de riesgo: Bajo, Medio, Alto
     - Acciones sugeridas: Enviar cupón, contactar directamente
   - **Lifetime Value proyectado**:
     - Cuánto se espera que gaste un cliente en su vida
     - Útil para decidir cuánto invertir en adquirirlo/retenerlo
5. Recomendación de precios dinámicos:
   - **Precio óptimo por producto**:
     - Basado en:
       - Elasticidad de demanda (cómo cambia demanda al cambiar precio)
       - Precios de competencia
       - Nivel de stock (si hay exceso, sugerir bajar precio)
       - Temporada (alta demanda = subir precio)
     - Sugerencia: "Aumentar precio de Cerveza X en 5% puede incrementar margen en 12% sin reducir volumen"
   - **Simulador de impacto**:
     - Ajustar precio manualmente
     - Ver proyección de impacto en ventas y margen
6. Análisis de tendencias de mercado:
   - **Detección de tendencias emergentes**:
     - Productos/categorías con crecimiento acelerado
     - Ej: "Cervezas artesanales creciendo 40% mensual"
   - **Benchmarking**:
     - Comparar con promedios del sector (si hay datos disponibles)
     - Identificar oportunidades
7. Proyección financiera:
   - **Forecast de ingresos y gastos**:
     - Proyectar Estado de Resultados próximos 3 meses
     - Basado en:
       - Ventas proyectadas
       - Gastos históricos (fijos y variables)
       - Planes de inversión conocidos
   - **Proyección de flujo de caja**:
     - Identificar períodos de superávit/déficit
     - Planificar financiamiento si se necesita
8. Escenarios "What-if":
   - **Simulador de escenarios**:
     - "¿Qué pasa si las ventas caen 20%?"
     - "¿Qué pasa si lanzo una campaña que incrementa tráfico 50%?"
     - "¿Qué pasa si aumento precios 10%?"
   - **Comparar escenarios**:
     - Lado a lado
     - Ver impacto en métricas clave
9. Configuración de modelos:
   - **Entrenamiento de modelos**:
     - Botón "Reentrenar modelo" (actualizar con últimos datos)
     - Frecuencia: Mensual automático
   - **Ajuste de parámetros**:
     - Peso de estacionalidad
     - Peso de tendencia
     - Eventos especiales a considerar
   - **Validación**:
     - Test del modelo con datos históricos
     - Ver qué tan bien predijo el pasado

**Consideraciones Multi-tenant:**
- Cada negocio tiene sus propios modelos predictivos.
- Los datos no se comparten entre negocios.
- Se requiere mínimo 90 días de datos históricos para predicciones confiables.

**Seguridad:**
- Requiere permiso: `analytics_predictivo_ver`
- Los modelos no exponen datos de clientes individuales (privacidad).

**UX:**
- Visualizaciones claras con rangos de confianza.
- Explicaciones simples de términos técnicos (tooltips).
- Alertas proactivas: "Tus ventas están proyectadas 15% bajo de la meta este mes".
- Sugerencias accionables: "Aumentar stock de [Producto X] en 30%".

**Reglas de Negocio:**
- Las predicciones son probabilísticas, no garantías.
- Los modelos mejoran con más datos (cuanto más tiempo, más precisos).
- Los eventos externos impredecibles (crisis, pandemias) afectan precisión.
- Los modelos deben revisarse y ajustarse periódicamente.

---

### **RF-REP-006: Reportes Programados y Automatizados**

**Descripción:**  
El sistema debe permitir programar la generación y envío automático de reportes en períodos específicos, con destinatarios configurables, formatos personalizados, y alertas basadas en condiciones, reduciendo trabajo manual y asegurando visibilidad continua de métricas clave.

**Criterios de Aceptación:**
1. Creación de reporte programado:
   - **Seleccionar reporte**:
     - Elegir de "Mis Reportes guardados"
     - O reportes predefinidos del sistema
     - O crear nuevo reporte personalizado
   - **Configuración de la programación**:
     - Frecuencia:
       - Diaria: Todos los días, días laborables, fines de semana
       - Semanal: Seleccionar día(s) de la semana
       - Mensual: Día específico del mes (ej: día 1, día 15, último día)
       - Trimestral, Anual
     - Hora de envío: Selector de hora (ej: 8:00 AM)
     - Zona horaria: Automática según configuración del negocio
   - **Destinatarios**:
     - Ingresar emails (separados por coma)
     - O seleccionar de lista de usuarios del sistema
     - O grupos predefinidos:
       - Gerencia
       - Equipo de ventas
       - Contador
   - **Formato de salida**:
     - PDF: Reporte formateado profesional
     - Excel: Datos crudos para análisis
     - Ambos: Adjuntar los 2 formatos
   - **Configuración de email**:
     - Asunto personalizado (variables: `{{periodo}}`, `{{negocio}}`)
     - Cuerpo del email (texto plano o HTML simple)
     - Ejemplo: "Adjunto reporte de ventas del {{periodo}} para {{negocio}}"
2. Reportes condicionales (alertas):
   - **Enviar solo si se cumple condición**:
     - Ejemplo: "Enviar reporte de stock bajo solo si hay productos < 10 unidades"
     - Condiciones disponibles:
       - Métrica mayor/menor que umbral
       - Cambio porcentual vs. período anterior
       - Presencia de valores críticos
   - **Alertas automáticas**:
     - "Alerta: Ventas del día < $1,000 USD"
     - "Alerta: Caja abierta > 12 horas sin cerrar"
     - "Alerta: Stock crítico en 5 productos"
   - **Destinatarios de alertas**:
     - Diferentes según criticidad
     - Alerta baja: Solo administrador
     - Alerta crítica: Gerente + Administrador + SMS
3. Gestión de reportes programados:
   - **Lista de reportes programados**:
     - Tabla con: Nombre, frecuencia, próximo envío, destinatarios, estado
     - Filtros: Por frecuencia, estado (activo, pausado)
     - Acciones: Editar, Pausar, Eliminar, Ejecutar ahora
   - **Historial de envíos**:
     - Por cada programación, ver:
       - Fecha/hora de envíos realizados
       - Estado: Enviado exitosamente, Error
       - Destinatarios
       - Acceso al reporte generado
   - **Logs de errores**:
     - Si falla un envío (ej: email inválido, servidor caído)
     - Registrar error y reintentar automáticamente
4. Dashboards automáticos (suscripción):
   - **Suscribirse a dashboard**:
     - En cualquier dashboard, botón "Suscribirme"
     - Recibir snapshot del dashboard por email
     - Frecuencia: Diaria, semanal
   - **Formato de dashboard por email**:
     - Imagen del dashboard (PNG)
     - Números clave en texto
     - Link para ver dashboard completo en el sistema
5. Reportes embebidos en emails:
   - **Contenido rico en email**:
     - En vez de solo adjunto, incluir:
       - Tabla con top 5 productos
       - Mini gráfico de ventas
       - KPIs destacados
     - Útil para lectura rápida sin abrir adjunto
6. Configuración de notificaciones:
   - **Preferencias por usuario**:
     - Cada usuario puede configurar:
       - Qué reportes quiere recibir
       - Por qué canal: Email, SMS, WhatsApp, Push notification
       - Frecuencia preferida
   - **Opt-out**:
     - Link en el email para desuscribirse de reportes
7. Reportes on-demand automatizados:
   - **API para solicitar reportes**:
     - Integración con otros sistemas
     - Webhook: Al cerrar caja, enviar reporte automáticamente
   - **Trigger por evento**:
     - Cuando se completa una venta > $1,000, enviar reporte de la venta al gerente
     - Cuando se crea un pedido con alcohol, enviar confirmación al administrador
8. Optimización de envíos:
   - **Batch processing**:
     - Si hay 10 reportes programados a las 8 AM, procesarlos juntos
     - Evitar sobrecargar el sistema
   - **Priorización**:
     - Reportes críticos (alertas) tienen prioridad alta
     - Reportes rutinarios tienen prioridad normal
   - **Rate limiting**:
     - No enviar más de 50 emails por hora (evitar ser marcado como spam)

**Consideraciones Multi-tenant:**
- Los reportes programados son por negocio.
- Los destinatarios solo reciben reportes del negocio al que tienen acceso.

**Seguridad:**
- Requiere permiso: `reportes_programados_gestionar`
- Los destinatarios deben ser usuarios validados o emails aprobados.
- Los reportes sensibles (finanzas) requieren confirmación adicional para programar.

**UX:**
- Wizard de configuración en 3 pasos: Reporte → Programación → Destinatarios.
- Vista previa del email antes de activar la programación.
- Toggle rápido para pausar/reanudar sin eliminar configuración.
- Notificación cuando un reporte programado se envía exitosamente.

**Reglas de Negocio:**
- Los reportes programados deben tener al menos 1 destinatario.
- Los destinatarios pueden desuscribirse individualmente.
- Los reportes fallidos se reintentan máximo 3 veces antes de alertar.
- Los reportes muy pesados (> 10MB) se envían como link de descarga en vez de adjunto.

---

**¡Módulo VI completado!** 🎉

Este es el último módulo del SRS, que incluye:
- **E-commerce completo** (configuración, catálogo, carrito, pedidos, reseñas, recomendaciones)
- **Marketing digital** (banners, newsletters, afiliados)
- **Dashboards ejecutivos** (general, ventas, inventario)
- **Reportes avanzados** (personalizados, predictivos, programados)

**Total del Módulo VI: 15 requisitos funcionales** (RF-ECO-001 a RF-ECO-009, RF-REP-001 a RF-REP-006).

Ahora tienes los **6 módulos completos** del sistema DrinkGo documentados en archivos MD separados. 📄✨
