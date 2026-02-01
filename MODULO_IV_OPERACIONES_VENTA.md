# **6.4 MÓDULO IV: OPERACIONES DE VENTA**

---

## **Submódulo 6.4.1: Punto de Venta (POS)**

---

### **RF-VTA-001: Abrir y Cerrar Sesión de Caja**

**Descripción:**  
El sistema debe permitir a los cajeros abrir y cerrar sesiones de caja diariamente, registrando el efectivo inicial, los movimientos durante la jornada, y el efectivo final con arqueo, garantizando control de flujo de efectivo y trazabilidad de todas las transacciones del día.

**Criterios de Aceptación:**
1. **Apertura de Caja**:
   - El cajero ingresa:
     - Caja/Terminal (automático según asignación del usuario)
     - Monto de efectivo inicial (fondo de cambio)
     - Desglose de denominaciones (opcional pero recomendado):
       - Billetes: 200, 100, 50, 20, 10 soles (cantidades)
       - Monedas: 5, 2, 1, 0.50, 0.20, 0.10 soles (cantidades)
     - Fecha y hora de apertura (automática)
     - Observaciones
   - El sistema valida:
     - No haya sesión abierta previa sin cerrar en esa caja
     - El usuario tenga permiso de cajero
     - El monto inicial sea > 0 (configurable si se permite sin fondo)
   - Al confirmar, se crea registro en `caja_sesion` con estado `'abierta'`
2. **Durante la sesión**:
   - Todos los pagos en efectivo se registran automáticamente en la sesión
   - Los pagos con tarjeta/transferencia también se vinculan
   - Se pueden registrar movimientos adicionales:
     - Retiros de efectivo (por seguridad o depósito a banco)
     - Ingresos adicionales (ej: pago de factura antigua)
     - Gastos menores (caja chica)
3. **Cierre de Caja**:
   - El cajero ingresa:
     - Monto de efectivo contado físicamente
     - Desglose de denominaciones (recomendado)
     - Observaciones sobre el día
   - El sistema calcula automáticamente:
     - Efectivo esperado = Monto inicial + Ventas en efectivo - Retiros + Ingresos adicionales
     - Diferencia = Efectivo contado - Efectivo esperado
     - Ventas totales del día (todas las formas de pago)
     - Número de transacciones
     - Ticket promedio
   - Si hay diferencia (sobrante o faltante):
     - |Diferencia| ≤ $5 USD: Diferencia menor (aceptable)
     - |Diferencia| > $5 USD: Diferencia mayor (requiere justificación)
     - Diferencias mayores requieren aprobación de supervisor
   - Al confirmar cierre:
     - Estado cambia a `'cerrada'`
     - Se genera reporte de cierre de caja (PDF)
     - No se permiten más ventas en esa sesión
     - El efectivo debe depositarse o entregarse a administración
4. Funcionalidades adicionales:
   - Vista de "Sesiones Abiertas" para supervisores (monitoreo en tiempo real)
   - Alerta si una sesión tiene > 12 horas sin cerrar
   - Historial de sesiones por cajero (evaluación de desempeño)
   - Cierre de emergencia por supervisor (casos especiales)

**Consideraciones Multi-tenant:**
- Cada caja pertenece a una sede específica del negocio.
- Las sesiones no se cruzan entre negocios.

**Seguridad:**
- Requiere permiso: `caja_apertura` (abrir), `caja_cierre` (cerrar)
- Solo el cajero que abrió puede cerrar su sesión (o supervisor con override).
- Auditoría completa de aperturas, cierres, y modificaciones.
- Los montos de efectivo son datos sensibles.

**UX:**
- Wizard de apertura rápida en 2 pasos.
- Calculadora de desglose de denominaciones (ingresar cantidades, calcula total).
- Resumen visual en cierre: gráfico de ventas por forma de pago.
- Código de colores: verde si cierre sin diferencias, amarillo si diferencia menor, rojo si mayor.
- Comparativa con sesiones previas del mismo cajero (benchmark).

**Reglas de Negocio:**
- Una caja solo puede tener una sesión abierta a la vez.
- Las sesiones deben cerrarse dentro de las 24 horas de apertura (buena práctica).
- Los sobrantes acumulados del mes se pueden destinar a bonificación del cajero (política del negocio).
- Los faltantes recurrentes (> 3 en el mes) pueden generar medidas disciplinarias.

---

### **RF-VTA-002: Registrar Venta en POS con Verificación de Edad**

**Descripción:**  
El sistema debe proporcionar una interfaz de punto de venta ágil y eficiente para registrar ventas al por menor, incluyendo verificación obligatoria de edad para productos alcohólicos, aplicación automática de descuentos y promociones, y generación de comprobantes de pago.

**Criterios de Aceptación:**
1. Interfaz de POS muestra:
   - **Panel de productos**: Grid de productos frecuentes con imágenes, acceso rápido
   - **Búsqueda de productos**: Por nombre, código de barras, SKU (scanner compatible)
   - **Carrito de venta**: Lista de items agregados con cantidad, precio, subtotal
   - **Panel de totales**: Subtotal, descuentos, IGV, Total
   - **Cliente**: Opcional, búsqueda rápida por DNI/nombre (para factura o cliente frecuente)
   - **Botones de acción**: Pagar, Cancelar, Suspender venta, Aplicar descuento
2. Flujo de venta:
   - El cajero escanea o busca productos
   - Por cada producto agregado, el sistema:
     - Valida disponibilidad de stock en tiempo real
     - Aplica el precio vigente (considera promociones activas)
     - Muestra advertencia si es producto alcohólico
     - Si es alcohólico Y (hora actual no está en horario permitido O no se verificó edad):
       - **Bloquea la venta**
       - Muestra modal: "⚠️ Producto alcohólico - Verificación de edad requerida"
       - El cajero debe confirmar: "Cliente mayor de 18 años verificado" (checkbox)
       - Si no se confirma, no se puede agregar el producto
   - El cajero puede modificar cantidad directamente en el carrito
   - Se pueden aplicar descuentos:
     - Por monto fijo
     - Por porcentaje
     - Cupón/código promocional
3. **Verificación de Edad** (crítico para licorería):
   - Para cada producto con `es_alcoholico = true`:
     - El sistema valida hora actual vs. `horario_venta_alcohol` de la sede
     - Si está fuera de horario: "🚫 Venta de alcohol no permitida fuera del horario legal"
     - Si está dentro del horario: Solicita verificación de edad
   - El cajero puede escanear DNI (si hay lector) o verificar manualmente
   - Se registra en la venta: `edad_verificada = true`, `verificado_por = usuario_id`
   - El sistema puede configurarse para:
     - Solicitar verificación en CADA venta con alcohol
     - Recordar cliente verificado durante la sesión
4. Al completar la venta:
   - Se selecciona forma de pago (ver RF-VTA-003)
   - Se aplica descuento FIFO al inventario (automático)
   - Se genera comprobante (boleta o factura)
   - Se actualiza inventario y caja en tiempo real
5. Funcionalidades adicionales:
   - **Suspender venta**: Guardar carrito temporal (cliente olvidó dinero)
   - **Ventas suspendidas**: Recuperar carritos guardados
   - **Cliente frecuente**: Búsqueda rápida, aplicar precios especiales
   - **Venta rápida**: Productos predefinidos (combo cerveza, etc.)
   - **Atajo de teclado**: F1-F12 para productos más vendidos

**Consideraciones Multi-tenant:**
- Las ventas se registran en el contexto del negocio y sede del cajero.
- Los precios y promociones son específicos por negocio.

**Seguridad:**
- Requiere permiso: `ventas_registrar`
- La verificación de edad es **obligatoria** por ley para productos alcohólicos.
- El sistema registra quién verificó la edad (responsabilidad legal).
- Los descuentos > 20% requieren autorización de supervisor.

**UX:**
- Interfaz optimizada para pantalla táctil.
- Grid de productos con categorías en tabs (Cervezas, Vinos, Licores, Snacks, etc.).
- Scanner de código de barras con sonido de confirmación.
- Atajos de teclado para operaciones comunes (Enter = Pagar, Esc = Cancelar).
- Modal de verificación de edad con texto grande y claro.
- Vista de "Últimas ventas" para repetir pedidos rápidamente.

**Reglas de Negocio:**
- **CRÍTICO**: No se puede vender alcohol sin verificación de edad (multas y sanciones legales).
- La venta de alcohol tiene restricciones horarias según legislación local (ej: no después de 11 PM).
- Los domingos o días de elecciones puede haber "ley seca" (configuración especial).
- El sistema debe poder bloquearse para auditorías de SUNAT sin previo aviso.

---

### **RF-VTA-003: Procesar Múltiples Formas de Pago**

**Descripción:**  
El sistema debe soportar múltiples formas de pago en una misma venta (pago mixto), incluyendo efectivo, tarjetas de crédito/débito, transferencias bancarias, billeteras digitales (Yape, Plin), y crédito a cliente, con validaciones de montos y generación de vueltos.

**Criterios de Aceptación:**
1. Formas de pago soportadas:
   - **Efectivo**: PEN (soles)
   - **Tarjeta de Crédito**: Visa, Mastercard, AMEX
   - **Tarjeta de Débito**: Local e internacional
   - **Transferencia Bancaria**: Interbancaria
   - **Billeteras Digitales**: Yape, Plin, Tunki
   - **Crédito**: A cliente frecuente con línea de crédito aprobada
   - **Vale/Gift Card**: Vales de regalo internos
2. En la pantalla de pago:
   - Total a pagar (destacado)
   - Saldo pendiente (se actualiza conforme se agregan pagos)
   - Lista de formas de pago disponibles (botones grandes)
   - Para cada forma de pago seleccionada:
     - **Efectivo**: Ingresar monto recibido → Calcular vuelto automáticamente
     - **Tarjeta**: Seleccionar tipo (crédito/débito) → Ingresar últimos 4 dígitos → Número de operación
     - **Transferencia**: Ingresar número de operación → Validar monto
     - **Yape/Plin**: Mostrar QR para escanear → Confirmar recepción → Número de operación
     - **Crédito**: Buscar cliente → Validar línea disponible → Registrar cargo a crédito
3. **Pago mixto** (múltiples formas en una venta):
   - El cajero puede agregar múltiples pagos hasta completar el total
   - Ejemplo: $50 en efectivo + $30 con tarjeta
   - El sistema valida: Suma de pagos >= Total de venta
   - No se permite exceder el total (excepto efectivo que genera vuelto)
4. Validaciones:
   - Efectivo recibido >= Total pendiente (o se genera vuelto)
   - Para tarjeta: Número de operación obligatorio
   - Para Yape/Plin: Confirmación de recepción del dinero
   - Para crédito: Cliente tiene línea disponible suficiente
5. Al confirmar el pago:
   - Se registra la venta en `venta` con estado `'completada'`
   - Se crean registros en `pago_venta` por cada forma de pago utilizada
   - Se descuenta inventario vía FIFO (llamada a `descontar_inventario_fifo()`)
   - Se registra el ingreso en la sesión de caja actual
   - Se genera comprobante de pago (boleta o factura)
   - Se imprime ticket automáticamente (si hay impresora configurada)
6. Funcionalidades adicionales:
   - **Vuelto sugerido**: Si se recibe $100 para pagar $87, sugiere vuelto = $13
   - **Redondeo**: Opción de redondear al sol más cercano (cliente acepta)
   - **Propina**: Permitir agregar propina voluntaria (restaurantes/bares)
   - **Vuelto como crédito**: Cliente puede dejar el vuelto como crédito para próxima compra

**Consideraciones Multi-tenant:**
- Las formas de pago disponibles son configurables por negocio.
- Las billeteras digitales requieren configuración de QR por negocio.

**Seguridad:**
- Requiere permiso: `ventas_cobrar`
- Los pagos con crédito requieren permiso adicional: `ventas_credito_autorizar`
- Los pagos > $500 USD en efectivo generan alerta (prevención de lavado).
- Auditoría completa de todos los pagos procesados.

**UX:**
- Calculadora de vuelto en tiempo real al ingresar monto recibido.
- Botones rápidos para denominaciones comunes: $10, $20, $50, $100.
- QR de Yape/Plin generado dinámicamente con el monto exacto.
- Sonido de confirmación cuando se completa el pago.
- Impresión automática de ticket al finalizar (opcional configurable).

**Reglas de Negocio:**
- El efectivo siempre debe ingresar a la caja física (no caja chica).
- Los pagos con tarjeta tienen comisión del banco (2-3%), pero no se cobra al cliente.
- Los pagos con Yape/Plin deben confirmarse antes de entregar productos (riesgo de fraude).
- Los créditos a clientes tienen plazo máximo de 30 días (configurable).
- Si hay vuelto y no hay cambio disponible, el sistema alerta al cajero.

---

### **RF-VTA-004: Emitir Comprobantes de Pago (Boleta/Factura)**

**Descripción:**  
El sistema debe generar comprobantes de pago electrónicos (boletas y facturas) cumpliendo con la normativa de SUNAT (Perú), incluyendo todos los datos fiscales requeridos, numeración correlativa, y envío automático al sistema de facturación electrónica.

**Criterios de Aceptación:**
1. Tipos de comprobantes soportados:
   - **Boleta de Venta**: Para clientes sin RUC (personas naturales)
   - **Factura Electrónica**: Para clientes con RUC (empresas)
   - **Nota de Crédito**: Para anulaciones o devoluciones
   - **Nota de Débito**: Para cargos adicionales
2. Datos requeridos en el comprobante:
   - **Emisor**:
     - Razón social del negocio
     - RUC
     - Dirección fiscal
     - Logo (si está configurado)
   - **Comprobante**:
     - Tipo (Boleta/Factura)
     - Serie y número correlativo (ej: B001-00001234, F001-00000567)
     - Fecha y hora de emisión
     - Moneda (PEN)
   - **Cliente** (para factura):
     - RUC/DNI
     - Razón social / Nombre completo
     - Dirección
   - **Detalle de venta**:
     - Descripción del producto
     - Cantidad
     - Unidad de medida
     - Precio unitario
     - Descuento (si aplica)
     - Subtotal
   - **Totales**:
     - Subtotal (gravado)
     - IGV (18%)
     - Total
   - **Información adicional**:
     - Forma de pago
     - Código QR (obligatorio para SUNAT)
     - Hash de firma digital
     - Observaciones
3. El sistema valida:
   - Para **Factura**: RUC del cliente es válido (11 dígitos)
   - Para **Boleta**: DNI del cliente (8 dígitos, opcional)
   - La numeración correlativa no tiene saltos
   - Los datos fiscales del negocio están completos
4. Proceso de emisión:
   - Al confirmar la venta, el sistema:
     - Genera el comprobante en formato XML (estándar SUNAT)
     - Lo firma digitalmente con certificado del negocio
     - Genera código QR con URL de validación SUNAT
     - Crea el PDF visual del comprobante
     - Envía el XML a SUNAT (vía OSE - Operador de Servicios Electrónicos)
     - Espera respuesta de SUNAT (CDR - Constancia de Recepción)
     - Si es aceptado: Marca como `'emitido'`
     - Si es rechazado: Marca como `'error'` y alerta al cajero
5. Funcionalidades adicionales:
   - **Reenvío de comprobante**: Por email o WhatsApp al cliente
   - **Reimpresión**: De comprobantes ya emitidos
   - **Anulación**: Genera nota de crédito automáticamente
   - **Descarga masiva**: De comprobantes por fecha (para contabilidad)
   - **Consulta de estado**: En SUNAT directamente desde el sistema
6. Manejo de errores:
   - Si SUNAT no responde (caída del servicio):
     - El comprobante se marca como `'pendiente_envio'`
     - Se reintenta automáticamente cada 5 minutos
     - El cajero puede continuar vendiendo (modo offline)
   - Si hay error en los datos:
     - Se detalla el error específico
     - Se permite corregir sin perder la venta

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propia serie de comprobantes (no compartida).
- Cada negocio debe tener su certificado digital para firmar.
- Las series pueden ser diferentes por sede (B001, B002, etc.).

**Seguridad:**
- El certificado digital debe estar protegido y encriptado.
- Solo usuarios con permiso `facturacion_emitir` pueden generar comprobantes.
- Los comprobantes no pueden editarse después de emitidos (solo anular).
- Auditoría completa de emisión, reenvíos, y anulaciones.

**UX:**
- Previsualización del comprobante antes de emitir.
- Indicador de "Enviando a SUNAT..." con spinner.
- Notificación de éxito con opción de imprimir o enviar por email.
- En caso de error, mensaje claro con solución sugerida.
- Acceso rápido a "Últimos comprobantes emitidos".

**Reglas de Negocio:**
- La emisión de comprobantes es **obligatoria** por ley para todas las ventas.
- Las series de comprobantes deben estar autorizadas por SUNAT previamente.
- Los comprobantes tienen validez legal solo si SUNAT los aceptó (estado "emitido").
- Las notas de crédito/débito deben referenciar el comprobante original.
- Los comprobantes deben conservarse por 5 años mínimo (auditoría).

---

### **RF-VTA-005: Aplicar Descuentos y Promociones Automáticas**

**Descripción:**  
El sistema debe evaluar y aplicar automáticamente descuentos y promociones configuradas cuando se cumplan las condiciones definidas, optimizando ingresos sin perder oportunidades de venta cruzada o por volumen.

**Criterios de Aceptación:**
1. Tipos de descuentos/promociones soportados:
   - **Por producto**:
     - Descuento fijo: $5 OFF
     - Descuento porcentual: 20% OFF
   - **Por cantidad**:
     - Compra 3, paga 2 (3x2)
     - 2da unidad al 50%
     - A partir de 6 unidades: 15% OFF
   - **Por monto total**:
     - Compras > $100: 10% OFF en toda la venta
   - **Combo/Bundle**:
     - Producto A + Producto B = $X (precio especial)
   - **Por categoría**:
     - 20% en todas las cervezas
   - **Por cliente**:
     - Clientes VIP: 5% adicional
     - Cumpleañeros: 10% OFF
   - **Por cupón/código**:
     - "VERANO2026": $10 OFF
   - **Por horario**:
     - Happy Hour (6-8 PM): 2x1 en cervezas
2. El sistema evalúa automáticamente en tiempo real:
   - Al agregar productos al carrito
   - Al modificar cantidades
   - Al identificar al cliente
   - Al acercarse a un umbral (ej: llevas $95, con $5 más obtienes 10% OFF - sugiere)
3. Reglas de aplicación de promociones:
   - **Stackability** (acumulación):
     - Configurar si se pueden combinar múltiples promociones
     - Por defecto: Se aplica la de mayor beneficio para el cliente
   - **Prioridad**:
     - Cupones > Promociones por cliente > Promociones por producto > Promociones generales
   - **Límites**:
     - Usos máximos por cupón (ej: 100 usos)
     - Usos por cliente (ej: 1 vez por persona)
     - Descuento máximo (ej: no más de 50% del total)
4. Visualización en el POS:
   - Badge de "🎁 PROMO" en productos con descuento
   - En el carrito, mostrar:
     - Subtotal original (tachado)
     - Descuento aplicado (-$X o -Y%)
     - Subtotal con descuento (en verde)
   - Mensaje motivacional: "¡Agregar 1 producto más te da 10% OFF!"
5. El sistema registra:
   - Qué promoción se aplicó en cada venta
   - Monto del descuento
   - Si fue automática o manual (aplicada por cajero con código)
6. Funcionalidades de gestión:
   - Ver "Reporte de Efectividad de Promociones":
     - Ventas incrementales atribuibles a la promo
     - Margen de ganancia vs. sin promo
     - ROI de la promoción
   - Activar/desactivar promociones en tiempo real
   - Programar promociones futuras (fecha inicio/fin)

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propias promociones independientemente.
- Las promociones no se cruzan entre negocios.

**Seguridad:**
- Requiere permiso: `promociones_aplicar` (automáticas), `descuentos_manuales` (para cajero aplicar ad-hoc)
- Los descuentos manuales > 30% requieren autorización de supervisor.
- Auditoría de qué usuario aplicó qué descuento.

**UX:**
- Animación visual cuando se aplica una promoción ("¡Descuento aplicado! 🎉").
- Sugerencias proactivas: "Agrega una cerveza más y obtienes 3x2".
- Preview de cómo quedaría el total si se aplica cierta promoción.
- Código de colores: descuentos en verde, precio original tachado.

**Reglas de Negocio:**
- Las promociones no deben llevar el precio de venta por debajo del costo (alerta si esto ocurre).
- Los productos alcohólicos en promoción aún requieren verificación de edad.
- Las promociones de Happy Hour solo aplican en el horario configurado.
- Los cupones de un solo uso se marcan como "utilizados" inmediatamente al aplicarse.

---

### **RF-VTA-006: Gestionar Devoluciones y Reembolsos**

**Descripción:**  
El sistema debe permitir procesar devoluciones de productos por parte de clientes, validando las condiciones de devolución, reintegrando el producto al inventario (si aplica), y gestionando el reembolso económico o cambio de producto.

**Criterios de Aceptación:**
1. El usuario inicia una devolución desde:
   - Búsqueda de venta por número de comprobante
   - Búsqueda por DNI/RUC del cliente
   - Escaneo del código QR del ticket
2. El sistema muestra:
   - Detalle completo de la venta original
   - Productos comprados con cantidades y precios
   - Fecha de venta
   - Tiempo transcurrido desde la compra
3. El usuario selecciona:
   - Productos a devolver (multiselección)
   - Cantidad a devolver por producto
   - Motivo de la devolución (dropdown):
     - Producto defectuoso/dañado
     - Producto vencido
     - Error en el pedido (se entregó otro producto)
     - Cliente cambió de opinión
     - Otro (especificar)
   - Tipo de resolución deseada:
     - **Reembolso**: Devolución del dinero
     - **Cambio**: Por otro producto
     - **Nota de crédito**: Saldo a favor para futuras compras
4. El sistema valida:
   - La venta existe y está completada
   - No han pasado más de X días desde la compra (política de devolución, ej: 7 días)
   - Para productos alcohólicos: Botella sin abrir, con sello intacto
   - El producto no está en condiciones que impidan su reventa (dañado por el cliente)
   - Si fue pagada con tarjeta, el reembolso debe ser por el mismo método
5. Proceso de devolución:
   - **Si es reembolso**:
     - Se calcula el monto a devolver (precio pagado × cantidad devuelta)
     - Si hubo descuento en la venta original, se mantiene la proporción
     - Se genera nota de crédito en SUNAT
     - Se registra salida de efectivo de la caja (si fue efectivo)
     - Se entrega el dinero al cliente o se programa reembolso a tarjeta
   - **Si es cambio**:
     - Se selecciona el producto de reemplazo
     - Si hay diferencia de precio:
       - Cliente paga la diferencia
       - Cliente recibe saldo a favor
     - Se registra como devolución + nueva venta
   - **Inventario**:
     - Si el producto está en condiciones de reventa:
       - Se reintegra al inventario en el mismo lote (si se identifica) o se crea nuevo lote
       - Estado del lote: `'disponible'` si está perfecto, `'bloqueado'` si requiere revisión
     - Si el producto no es apto:
       - Se registra como merma
       - No se reintegra al inventario
6. El sistema genera:
   - Nota de crédito electrónica (SUNAT)
   - Comprobante de devolución interno
   - Actualización de inventario
   - Registro en auditoría
7. Restricciones configurables:
   - Plazo máximo de devolución por categoría (ej: alimentos 3 días, licores 7 días)
   - Productos no retornables (ej: cigarros, productos de uso personal)
   - Monto máximo de devolución sin autorización ($100 USD)

**Consideraciones Multi-tenant:**
- Las políticas de devolución son configurables por negocio.
- Las devoluciones solo afectan el inventario del negocio correspondiente.

**Seguridad:**
- Requiere permiso: `ventas_devolucion_procesar`
- Devoluciones > $200 USD requieren autorización de gerente.
- Auditoría completa de quién procesó la devolución y por qué motivo.
- Alerta si un mismo cliente tiene > 5 devoluciones en el mes (posible abuso).

**UX:**
- Búsqueda rápida de venta por QR del ticket.
- Vista comparativa: "Venta Original vs. Devolución".
- Calculadora automática de reembolso según cantidades.
- Flujo guiado con pasos claros: Buscar venta → Seleccionar productos → Validar condiciones → Procesar.
- Impresión automática de comprobante de devolución.

**Reglas de Negocio:**
- **CRÍTICO para licorería**: Los productos alcohólicos solo se aceptan con sello intacto (por regulación sanitaria).
- No se aceptan devoluciones de productos en promoción "última oportunidad" o "liquidación" (debe avisarse al cliente al vender).
- Los reembolsos en efectivo se hacen inmediatamente; los reembolsos a tarjeta toman 5-10 días hábiles.
- Las devoluciones fraudulentas (ej: comprar, consumir, intentar devolver) se reportan y el cliente puede ser bloqueado.

---

### **RF-VTA-007: Suspender y Recuperar Ventas**

**Descripción:**  
El sistema debe permitir suspender temporalmente una venta en proceso para atender a otro cliente o resolver una situación, guardando el carrito actual, y luego recuperarlo para continuar exactamente donde quedó.

**Criterios de Aceptación:**
1. Durante una venta en proceso, el cajero puede hacer clic en "Suspender Venta".
2. El sistema solicita:
   - Motivo (opcional): Cliente fue por dinero, atender urgencia, falta verificar precio, etc.
   - Identificador del cliente (opcional): Nombre, DNI, o ticket número
3. Al suspender:
   - El carrito actual se guarda en base de datos con estado `'suspendida'`
   - Se genera un código único (ej: SUSP-001)
   - Se limpia el carrito actual para atender al siguiente cliente
   - La venta suspendida queda en lista de "Ventas Pendientes"
4. El cajero puede ver "Ventas Suspendidas":
   - Lista de todas las ventas suspendidas en la sesión actual o del día
   - Información: Código, hora, productos en carrito, total, cliente (si se ingresó)
   - Filtro por cajero, cliente, o rango de tiempo
5. Para recuperar una venta suspendida:
   - El cajero selecciona de la lista o ingresa el código
   - El sistema carga el carrito exactamente como estaba
   - El cajero continúa agregando productos o procede al pago
6. Las ventas suspendidas:
   - No reservan inventario (el stock sigue disponible para otras ventas)
   - Pueden ser eliminadas si el cliente no regresa
   - Expiran automáticamente después de 24 horas (configurable)
   - Pueden transferirse a otro cajero/caja
7. Casos de uso:
   - Cliente olvidó su billetera
   - Cliente está comparando precios y volverá
   - Se necesita verificar algo con gerencia
   - Atender una urgencia mientras se sirve a un cliente
   - Cambio de turno (traspasar ventas pendientes)

**Consideraciones Multi-tenant:**
- Las ventas suspendidas son por negocio y sede.
- Cada caja puede ver solo sus ventas suspendidas (o todas según configuración).

**Seguridad:**
- Cualquier cajero con sesión abierta puede suspender ventas.
- Solo el cajero que suspendió (o un supervisor) puede eliminar una venta suspendida.
- Las ventas suspendidas se auditan (quién, cuándo, por qué).

**UX:**
- Botón grande "Suspender" siempre visible en el POS.
- Badge con número de ventas suspendidas pendientes (ej: "🔄 3").
- Vista de lista con vista previa del carrito al pasar el mouse.
- Recuperación con un clic desde la lista.
- Notificación si hay ventas suspendidas > 2 horas sin recuperar.

**Reglas de Negocio:**
- Las ventas suspendidas no afectan los reportes de venta hasta que se completan.
- Si un producto en el carrito suspendido se agota, al recuperarlo se alerta al cajero.
- Se pueden suspender múltiples ventas simultáneamente (sin límite).
- Al cerrar sesión de caja, el sistema alerta si hay ventas suspendidas pendientes.

---

### **RF-VTA-008: Generar Reportes de Ventas del Día**

**Descripción:**  
El sistema debe proporcionar reportes detallados de las ventas realizadas durante el día o período específico, con múltiples vistas y filtros, permitiendo análisis de desempeño, productos más vendidos, formas de pago, y comparativas.

**Criterios de Aceptación:**
1. Tipos de reportes disponibles:
   - **Reporte de Cierre de Caja**: Detallado por sesión
   - **Reporte de Ventas del Día**: Todas las ventas de la fecha
   - **Reporte por Cajero**: Desempeño individual
   - **Reporte por Producto**: Ranking de más vendidos
   - **Reporte por Categoría**: Ventas agrupadas
   - **Reporte por Forma de Pago**: Efectivo vs. tarjetas vs. digital
   - **Reporte Comparativo**: Hoy vs. ayer, esta semana vs. semana pasada
2. Métricas principales mostradas:
   - **Ventas**:
     - Cantidad de transacciones
     - Monto total vendido
     - Ticket promedio
     - Producto más vendido
     - Categoría más vendida
   - **Por cajero**:
     - Transacciones realizadas
     - Monto vendido
     - Ticket promedio
     - Velocidad (transacciones/hora)
   - **Por producto**:
     - Unidades vendidas
     - Monto generado
     - % del total de ventas
   - **Por forma de pago**:
     - Monto y % por cada forma
     - Gráfico de pastel visual
3. Filtros disponibles:
   - Rango de fechas
   - Sede/sucursal
   - Cajero específico
   - Categoría de producto
   - Forma de pago
   - Hora del día (análisis por franja horaria)
4. Visualización:
   - **Tablas**: Datos tabulares detallados, ordenables
   - **Gráficos**: Barras, líneas, pastel, según el tipo de análisis
   - **KPIs**: Cards con números grandes de métricas clave
   - **Comparativas**: Variación porcentual vs. período anterior
5. Exportación:
   - PDF: Formato profesional para impresión
   - Excel: Para análisis adicional
   - CSV: Para importar a otros sistemas
6. Reportes programados:
   - Envío automático por email al cierre del día
   - Destinatarios configurables (gerente, administrador, contador)
   - Periodicidad: Diario, semanal, mensual

**Consideraciones Multi-tenant:**
- Cada negocio ve solo sus propios reportes.
- Los usuarios solo ven datos de sedes a las que tienen acceso.

**Seguridad:**
- Requiere permiso: `reportes_ventas_ver`
- Los datos de ingresos son sensibles, solo accesibles para administración.
- Los cajeros pueden ver solo sus propias ventas (no las de otros).

**UX:**
- Dashboard visual con gráficos interactivos.
- Selector de fecha tipo calendario.
- Vista previa antes de exportar.
- Gráficos con colores institucionales del negocio.
- Modo "presentación" para reuniones gerenciales.

**Reglas de Negocio:**
- Los reportes se generan en tiempo real (no requieren procesos batch).
- Los montos incluyen o excluyen IGV según configuración.
- Las devoluciones se restan del total de ventas (neto).
- Los reportes históricos se conservan por 5 años mínimo.

---

### **RF-VTA-009: Integrar con Impresora Fiscal/Térmica**

**Descripción:**  
El sistema debe integrarse con impresoras fiscales o térmicas para imprimir automáticamente tickets de venta, comprobantes, reportes de cierre de caja, y comandas de cocina/bar, soportando diferentes modelos y configuraciones.

**Criterios de Aceptación:**
1. Tipos de impresoras soportadas:
   - **Impresora Fiscal**: Para países que lo requieran (Argentina, etc.)
   - **Impresora Térmica**: 80mm (estándar POS)
   - **Impresora de Red**: Ethernet o WiFi
   - **Impresora USB**: Conexión directa
2. Documentos a imprimir:
   - **Ticket de venta**: Después de cada venta
   - **Comprobante de pago**: Boleta/factura
   - **Reporte de cierre de caja**: Al cerrar sesión
   - **Reporte X**: Reporte intermedio sin cerrar caja
   - **Comandas**: Para cocina o bar (si aplica módulo de mesas)
   - **Etiquetas**: Precios de productos
3. Configuración de impresora:
   - Selección de modelo/marca (Epson, Star, Bixolon, etc.)
   - Puerto de conexión (USB, COM, IP)
   - Tamaño de papel (58mm, 80mm)
   - Densidad de impresión
   - Número de copias (original + copia)
   - Corte automático de papel
4. Formato del ticket:
   - **Encabezado**:
     - Logo del negocio (si la impresora soporta gráficos)
     - Razón social
     - RUC
     - Dirección
     - Teléfono
   - **Cuerpo**:
     - Fecha y hora
     - Número de ticket
     - Cajero
     - Detalle de productos (descripción, cantidad, precio, subtotal)
     - Descuentos aplicados
   - **Totales**:
     - Subtotal
     - IGV
     - Total
     - Forma de pago
     - Efectivo recibido (si aplica)
     - Vuelto (si aplica)
   - **Pie**:
     - Mensaje personalizado ("¡Gracias por su compra!")
     - Código QR (para validación en SUNAT)
     - URL del comprobante electrónico
5. El sistema gestiona:
   - **Estado de la impresora**: Conectada, desconectada, sin papel, error
   - **Cola de impresión**: Si falla, reintenta automáticamente
   - **Modo offline**: Si la impresora no está disponible, guarda para imprimir después
   - **Reimpresión**: Permite reimprimir el último ticket o cualquier ticket histórico
6. Validaciones:
   - Alerta si la impresora está sin papel
   - Alerta si hay error de comunicación
   - Permite continuar vendiendo sin impresora (contingencia)

**Consideraciones Multi-tenant:**
- Cada caja/terminal puede tener su propia impresora configurada.
- Las configuraciones de impresora son por sede.

**Seguridad:**
- La conexión con impresoras fiscales debe cumplir normativas locales.
- Los drivers de impresora deben ser oficiales (no modificados).

**UX:**
- Configuración guiada: "Detectar impresora automáticamente".
- Test de impresión desde la configuración.
- Vista previa del ticket antes de imprimir (opcional).
- Sonido de confirmación cuando la impresión es exitosa.

**Reglas de Negocio:**
- La impresión de tickets es obligatoria en algunos países (regulación fiscal).
- Si la impresora fiscal falla, debe haber protocolo de contingencia (emisión manual).
- Los tickets deben conservarse por 5 años (auditoría).
- No se puede alterar el formato del ticket fiscal (normativa).

---

### **RF-VTA-010: Gestionar Caja Chica y Gastos Menores**

**Descripción:**  
El sistema debe permitir registrar gastos menores realizados desde la caja chica del punto de venta (ej: compra de insumos urgentes, propinas, transporte), descontándolos del efectivo disponible y generando comprobantes para su posterior conciliación contable.

**Criterios de Aceptación:**
1. Durante una sesión de caja abierta, el cajero puede registrar un "Gasto de Caja Chica".
2. El formulario incluye:
   - Monto del gasto (obligatorio)
   - Categoría del gasto (dropdown):
     - Insumos/materiales
     - Transporte/delivery
     - Servicios (luz, agua por emergencia)
     - Propinas a personal externo
     - Otros (especificar)
   - Descripción detallada (obligatorio)
   - Beneficiario/proveedor (opcional)
   - Comprobante de respaldo:
     - Tipo (ticket, boleta, factura, recibo simple, sin comprobante)
     - Número de comprobante (si aplica)
     - Adjuntar foto/scan del comprobante
3. El sistema valida:
   - El monto del gasto no excede el efectivo disponible en caja
   - El monto no supera el límite configurado para gastos sin autorización (ej: $50 USD)
   - Hay sesión de caja abierta
4. Al registrar el gasto:
   - Se crea registro en `gasto_caja_chica`
   - Se descuenta del efectivo disponible en la sesión de caja
   - Se vincula al `caja_sesion_id` actual
   - Se notifica al administrador (configurable según monto)
5. Gastos mayores al límite:
   - Requieren aprobación de gerente
   - Se marcan como `'pendiente_aprobacion'`
   - El gerente recibe notificación
   - Si se aprueba, se procesa; si se rechaza, se revierte
6. Al cierre de caja:
   - Los gastos se listan por separado en el reporte
   - Se restan del efectivo esperado
   - Fórmula: Efectivo Final = Efectivo Inicial + Ventas en Efectivo - Retiros - Gastos
7. Reportes de caja chica:
   - "Gastos de Caja Chica por Categoría"
   - "Gastos por Cajero"
   - "Gastos sin Comprobante" (para seguimiento)
   - Exportación para contabilidad

**Consideraciones Multi-tenant:**
- Los gastos de caja chica son por negocio y sede.
- Los límites de gasto sin autorización son configurables por negocio.

**Seguridad:**
- Requiere permiso: `caja_gasto_registrar`
- Gastos > límite requieren `caja_gasto_aprobar`.
- Auditoría completa de todos los gastos registrados.
- Alerta si un cajero registra > 5 gastos en un día (posible irregularidad).

**UX:**
- Botón "Registrar Gasto" accesible desde el POS.
- Captura de foto del comprobante directamente desde la app móvil.
- Calculadora para ingresar el monto.
- Plantillas de gastos frecuentes (ej: "Propina delivery: $5").

**Reglas de Negocio:**
- Todos los gastos > $20 USD deben tener comprobante obligatoriamente.
- Los gastos sin comprobante no son deducibles de impuestos.
- La caja chica debe reponerse semanalmente (rendición de gastos).
- Los gastos recurrentes (ej: agua, luz) deben salir de otra cuenta, no de caja chica.

---

Continúo con el **Submódulo 6.4.2: Gestión de Mesas y Cuentas**.

---

## **Submódulo 6.4.2: Gestión de Mesas y Cuentas**

---

### **RF-VTA-011: Gestionar Mapa de Mesas**

**Descripción:**  
El sistema debe proporcionar una representación visual del layout del restaurante/bar con todas las mesas, mostrando su estado en tiempo real (disponible, ocupada, reservada) y permitiendo operaciones rápidas de asignación, transferencia y cierre de mesas.

**Criterios de Aceptación:**
1. Configuración del layout:
   - El administrador define áreas/zonas (ej: Salón Principal, Terraza, Barra, VIP)
   - Para cada área, se agregan mesas con:
     - Número de mesa (único por sede)
     - Capacidad (número de comensales)
     - Forma (cuadrada, rectangular, redonda - visual)
     - Posición en el mapa (coordenadas X, Y para arrastrar y soltar)
     - Estado inicial: `'disponible'`
2. Vista del mapa de mesas:
   - **Representación gráfica**: Cajas/círculos representando mesas
   - **Código de colores**:
     - 🟢 Verde: Disponible
     - 🔴 Rojo: Ocupada
     - 🟡 Amarillo: Reservada
     - ⚪ Gris: Inactiva (fuera de servicio)
   - **Información en cada mesa**:
     - Número de mesa
     - Capacidad (iconos de personas)
     - Tiempo transcurrido desde ocupación (para mesas ocupadas)
     - Monto acumulado en la cuenta (para mesas ocupadas)
     - Mesero asignado
3. Operaciones desde el mapa:
   - **Click en mesa disponible**:
     - Abrir cuenta nueva
     - Reservar mesa (asignar cliente y hora)
   - **Click en mesa ocupada**:
     - Ver detalle de la cuenta
     - Agregar productos
     - Imprimir comanda
     - Solicitar cuenta
     - Cerrar cuenta
   - **Arrastrar y soltar**:
     - Transferir clientes de una mesa a otra
     - Unir mesas (ej: juntar mesa 5 y 6 para grupo grande)
   - **Click derecho**:
     - Marcar mesa como inactiva
     - Cambiar mesero asignado
     - Ver historial de la mesa
4. Filtros y vistas:
   - Filtrar por área/zona
   - Mostrar solo mesas disponibles
   - Mostrar solo mesas de un mesero específico
   - Vista de lista (alternativa al mapa visual)
5. Indicadores:
   - Total de mesas: X
   - Disponibles: Y (color verde)
   - Ocupadas: Z (color rojo)
   - % de ocupación del local
   - Tiempo promedio de ocupación

**Consideraciones Multi-tenant:**
- Cada sede del negocio tiene su propio layout de mesas.
- Las configuraciones no se comparten entre sedes.

**Seguridad:**
- Requiere permiso: `mesas_ver` (ver mapa), `mesas_gestionar` (modificar layout)
- Solo gerentes pueden editar el layout.
- Los meseros solo ven mesas de su área asignada (configurable).

**UX:**
- Interfaz drag-and-drop para configurar el layout.
- Actualización en tiempo real (sin recargar página).
- Zoom in/out para locales con muchas mesas.
- Vista en pantalla completa para tablets/displays.
- Sonido/notificación cuando una mesa solicita atención.

**Reglas de Negocio:**
- Una mesa solo puede tener una cuenta activa a la vez.
- Las mesas reservadas se liberan automáticamente si el cliente no llega en 15 minutos (configurable).
- El tiempo de ocupación se usa para calcular la rotación de mesas (métrica de eficiencia).

---

### **RF-VTA-012: Abrir Cuenta de Mesa**

**Descripción:**  
El sistema debe permitir abrir una nueva cuenta cuando los clientes se sientan en una mesa, capturando información básica del servicio y asignando un mesero responsable, iniciando el proceso de atención y consumo.

**Criterios de Aceptación:**
1. Desde el mapa de mesas, el mesero selecciona una mesa disponible y hace clic en "Abrir Cuenta".
2. El sistema solicita:
   - Número de comensales (obligatorio)
   - Mesero responsable (auto-completa con el usuario actual, editable)
   - Cliente/Reserva (opcional): Si hay reserva, vincular; si es cliente frecuente, buscar por nombre/DNI
   - Observaciones (opcional): Ej: "Celebración de cumpleaños", "Cliente alérgico a mariscos"
3. El sistema valida:
   - La mesa está disponible
   - El número de comensales <= capacidad de la mesa (alerta si excede, pero permite continuar)
   - Hay sesión de caja abierta (mesero o caja general)
4. Al abrir la cuenta:
   - Se crea registro en `cuenta_mesa` con estado `'abierta'`
   - La mesa cambia a estado `'ocupada'` (color rojo en el mapa)
   - Se asigna un número de cuenta único (ej: CM-001234)
   - Se registra fecha/hora de apertura
   - Se inicia cronómetro de tiempo de ocupación
5. La cuenta queda lista para:
   - Agregar productos (ver RF-VTA-013)
   - Tomar pedidos
   - Procesar pagos
6. Funcionalidades adicionales:
   - **Pre-orden**: Asociar pedidos tomados mientras se preparaba la mesa
   - **División de cuenta**: Marcar desde el inicio si se dividirá (por persona, por pareja, etc.)
   - **Notificación a cocina/bar**: Alertar que hay mesa nueva (preparar cubiertos, agua, etc.)

**Consideraciones Multi-tenant:**
- Las cuentas de mesa son por negocio y sede.
- La numeración de cuentas es única por negocio.

**Seguridad:**
- Requiere permiso: `mesas_cuenta_abrir`
- Los meseros solo pueden abrir cuentas en su área asignada.
- Auditoría de quién abrió la cuenta y cuándo.

**UX:**
- Formulario simplificado: solo datos esenciales.
- Botón grande "Abrir Cuenta" en el mapa.
- Autocompletado de cliente si es frecuente.
- Confirmación visual: mesa cambia de color inmediatamente.

**Reglas de Negocio:**
- Las cuentas abiertas sin consumo en 30 minutos generan alerta al supervisor (posible olvido).
- Si la mesa excede su capacidad, se sugiere unir con otra mesa cercana.
- El mesero asignado al abrir la cuenta recibe la propina (salvo redistribución manual).

---

### **RF-VTA-013: Agregar Productos a Cuenta de Mesa**

**Descripción:**  
El sistema debe permitir al mesero agregar productos (bebidas, platos, snacks) a la cuenta de una mesa abierta, especificando cantidades, modificadores, y observaciones especiales, generando comandas para cocina/bar automáticamente.

**Criterios de Aceptación:**
1. El mesero selecciona una mesa ocupada y hace clic en "Agregar Productos" o "Tomar Pedido".
2. Interfaz de pedido muestra:
   - **Catálogo de productos**: Organizado por categorías (Bebidas, Entradas, Platos Fuertes, Postres, etc.)
   - **Búsqueda rápida**: Por nombre o código
   - **Productos frecuentes**: Acceso rápido a más pedidos
   - **Carrito de pedido**: Items seleccionados con cantidades
3. Al agregar un producto:
   - Cantidad (incrementar/decrementar)
   - Modificadores (si aplica):
     - Tamaño (chico, mediano, grande)
     - Temperatura (frío, natural, caliente)
     - Acompañamiento (papas, ensalada, arroz)
     - Nivel de cocción (carne: término medio, 3/4, bien cocido)
   - Observaciones especiales (texto libre):
     - "Sin cebolla"
     - "Extra picante"
     - "Al centro de la mesa"
   - Para productos alcohólicos: Marcar si requiere verificación de edad
4. El sistema valida:
   - Stock disponible en tiempo real
   - Si es producto alcohólico, está dentro del horario permitido
5. Al confirmar el pedido:
   - Se agregan items a `cuenta_mesa_item` vinculados a la cuenta
   - Estado del item: `'pendiente'` (no preparado aún)
   - Se genera comanda automática:
     - **Para cocina**: Platos, entradas, postres
     - **Para bar**: Bebidas, cócteles
   - La comanda incluye:
     - Número de mesa
     - Número de cuenta
     - Fecha/hora
     - Items con cantidades y observaciones
     - Mesero que tomó el pedido
     - Prioridad (normal, urgente)
   - Se imprime comanda en impresora de cocina/bar (o se muestra en pantalla KDS - Kitchen Display System)
6. Seguimiento de items:
   - El mesero ve el estado de cada item: Pendiente, En Preparación, Listo, Servido
   - Se puede marcar manualmente como "Servido" al entregar al cliente
7. Operaciones adicionales:
   - **Cancelar item**: Antes de que se prepare (con motivo)
   - **Modificar item**: Si aún no se envió a cocina
   - **Priorizar item**: Marcar como urgente

**Consideraciones Multi-tenant:**
- Los productos disponibles son del catálogo del negocio.
- Las comandas se envían a las impresoras/pantallas configuradas en la sede.

**Seguridad:**
- Requiere permiso: `mesas_pedido_tomar`
- La cancelación de items requiere permiso adicional o aprobación.
- Auditoría de qué mesero agregó qué items.

**UX:**
- Interfaz táctil optimizada para tablets.
- Imágenes de los productos para identificación rápida.
- Botones grandes para modificadores comunes.
- Vista de "Pedidos Pendientes de Enviar" antes de confirmar.
- Confirmación: "Pedido enviado a cocina ✓".

**Reglas de Negocio:**
- Los items con descuento FIFO se aplican al servir, no al pedir (el inventario se descuenta al cerrar la cuenta).
- Las comandas de bar tienen prioridad sobre cocina (bebidas se sirven primero).
- Si un producto se agota durante el servicio, se notifica inmediatamente al mesero para informar al cliente.

---

### **RF-VTA-014: Dividir y Transferir Cuentas**

**Descripción:**  
El sistema debe permitir dividir la cuenta de una mesa en varias cuentas parciales (por comensal, por pareja, por concepto) y transferir items o cuentas completas entre mesas, facilitando el cobro flexible según las necesidades de los clientes.

**Criterios de Aceptación:**
1. **Dividir Cuenta**:
   - Desde la vista de cuenta, el mesero selecciona "Dividir Cuenta".
   - Opciones de división:
     - **Por número de personas**: Divide equitativamente el total
     - **Por comensal**: Asigna cada item a una persona específica
     - **Por concepto**: Agrupa items (ej: "bebidas", "platos")
     - **Personalizada**: El mesero selecciona manualmente qué items van a cada cuenta
   - El sistema muestra:
     - Vista de la cuenta original con todos los items
     - Opciones para crear N sub-cuentas
     - Arrastrar items entre sub-cuentas
     - Calcular total de cada sub-cuenta en tiempo real
   - Al confirmar:
     - Se crean N registros de `cuenta_mesa` vinculados a la cuenta original
     - Cada sub-cuenta tiene su propio total y puede pagarse independientemente
     - Los items se reasignan a las sub-cuentas correspondientes
     - La cuenta original cambia a estado `'dividida'`
2. **Transferir Items entre Mesas**:
   - Desde la vista de cuenta, seleccionar items y hacer clic en "Transferir".
   - Seleccionar mesa de destino (debe tener cuenta abierta).
   - Los items se mueven de la cuenta origen a la cuenta destino.
   - Se actualizan los totales de ambas cuentas.
   - Se registra la transferencia en auditoría.
3. **Unir Cuentas**:
   - Seleccionar múltiples mesas con cuentas abiertas.
   - Hacer clic en "Unir Cuentas".
   - Todos los items se consolidan en una sola cuenta.
   - Las cuentas originales se cierran.
   - Se asigna una mesa principal (o se crea cuenta sin mesa para grupos grandes).
4. **Transferir Mesa Completa**:
   - Arrastrar cuenta de una mesa a otra en el mapa.
   - La mesa destino debe estar disponible.
   - Toda la cuenta se transfiere completa.
   - La mesa origen queda disponible.
5. Validaciones:
   - No se pueden dividir cuentas ya pagadas.
   - No se pueden transferir items ya servidos sin permiso especial.
   - La suma de las sub-cuentas debe igualar el total original.

**Consideraciones Multi-tenant:**
- Las operaciones solo afectan mesas del mismo negocio/sede.

**Seguridad:**
- Requiere permiso: `mesas_cuenta_dividir`, `mesas_cuenta_transferir`
- Auditoría completa de divisiones y transferencias (prevención de fraudes).

**UX:**
- Interfaz drag-and-drop para asignar items a sub-cuentas.
- Vista de resumen antes de confirmar división.
- Colores diferentes para cada sub-cuenta.
- Confirmación: "Cuenta dividida en 3 partes ✓".

**Reglas de Negocio:**
- Cada sub-cuenta puede pagarse con forma de pago diferente.
- Las propinas se dividen proporcionalmente al monto de cada sub-cuenta.
- Las transferencias entre mesas deben notificarse a cocina (cambio de destino de las comandas).

---

### **RF-VTA-015: Solicitar y Cerrar Cuenta**

**Descripción:**  
El sistema debe gestionar el proceso de cierre de cuenta de mesa, permitiendo al mesero generar el resumen de consumo, aplicar descuentos y propinas, procesar el pago, y liberar la mesa para nuevos clientes.

**Criterios de Aceptación:**
1. El mesero selecciona la mesa ocupada y hace clic en "Solicitar Cuenta" o "Cerrar Cuenta".
2. El sistema genera el resumen de cuenta:
   - Número de mesa
   - Número de cuenta
   - Mesero responsable
   - Fecha/hora de apertura y duración
   - **Detalle de consumo**:
     - Descripción de cada producto
     - Cantidad
     - Precio unitario
     - Subtotal
   - **Totales**:
     - Subtotal
     - Descuentos (si se aplican)
     - IGV (18%)
     - **Propina sugerida** (configurable: 0%, 5%, 10%, 15%)
     - **Total a pagar**
3. Opciones antes de cobrar:
   - **Aplicar descuento**:
     - Por monto fijo o porcentaje
     - Código promocional
     - Cortesía de la casa (requiere autorización)
   - **Propina**:
     - Seleccionar porcentaje (5%, 10%, 15%, otro)
     - Ingresar monto fijo
     - Sin propina
   - **Imprimir pre-cuenta**: Para que cliente revise antes de pagar
4. Procesar pago (similar a RF-VTA-003):
   - Seleccionar forma(s) de pago
   - Efectivo, tarjeta, transferencia, Yape/Plin, mixto
   - Calcular vuelto si es efectivo
5. Al confirmar el pago:
   - Se registra el pago en `pago_cuenta_mesa`
   - Se aplica descuento FIFO al inventario (ahora sí se descuenta)
   - Se genera comprobante de pago (boleta o factura)
   - La cuenta cambia a estado `'cerrada'`
   - La mesa cambia a estado `'disponible'`
   - Se imprime ticket para el cliente
   - Se registra la propina al mesero (si aplica)
6. Cierre parcial:
   - Si la cuenta fue dividida, cerrar cada sub-cuenta independientemente.
   - La mesa queda disponible solo cuando todas las sub-cuentas están cerradas.
7. Funcionalidades adicionales:
   - **Invitar consumo**: Marcar items como cortesía (no se cobran)
   - **Reabrir cuenta**: Si se cerró por error (con autorización)
   - **Enviar comprobante por email/WhatsApp**: Al cliente

**Consideraciones Multi-tenant:**
- Las cuentas se cierran en el contexto del negocio correspondiente.

**Seguridad:**
- Requiere permiso: `mesas_cuenta_cerrar`
- Los descuentos > 20% requieren autorización de supervisor.
- Las cortesías requieren `mesas_cortesia_aplicar`.
- Auditoría completa del proceso de cierre.

**UX:**
- Resumen visual claro y ordenado.
- Botones grandes para porcentajes de propina.
- Calculadora de vuelto en tiempo real.
- Confirmación: "Cuenta cerrada. Mesa disponible ✓".
- Opción de imprimir ticket duplicado (cliente + copia interna).

**Reglas de Negocio:**
- Las propinas son adicionales al total y son del mesero (no del negocio).
- Si hay descuento, la propina se calcula sobre el monto original o descontado (configurable).
- Las mesas deben limpiarse físicamente antes de marcarlas como disponibles en el sistema.
- El tiempo de ocupación se registra para análisis de rotación.

---

### **RF-VTA-016: Gestionar Reservas de Mesa**

**Descripción:**  
El sistema debe permitir gestionar reservas de mesas con anticipación, asignando mesas a clientes específicos para fecha y hora determinadas, con notificaciones de confirmación y recordatorios automáticos.

**Criterios de Aceptación:**
1. Crear nueva reserva:
   - **Datos del cliente**:
     - Nombre completo (obligatorio)
     - Teléfono (obligatorio para confirmación)
     - Email (opcional)
     - DNI (opcional, útil para clientes frecuentes)
   - **Datos de la reserva**:
     - Fecha (calendario)
     - Hora (dropdown: intervalos de 30 min)
     - Número de personas (obligatorio)
     - Mesa específica (opcional) o "Asignar automáticamente"
     - Área/zona preferida (Terraza, Interior, VIP, etc.)
     - Duración estimada (default: 2 horas)
   - **Observaciones**:
     - Ocasión especial (cumpleaños, aniversario, cena de negocios)
     - Preferencias (ventana, tranquilo, cerca de la barra)
     - Alergias o restricciones alimentarias
2. El sistema valida:
   - Fecha de reserva >= fecha actual
   - Hora está dentro del horario de operación
   - Hay mesas disponibles con capacidad suficiente
   - No hay doble reserva en la misma mesa/hora
3. Al crear la reserva:
   - Se asigna un código de reserva (RES-YYYYMMDD-####)
   - Estado inicial: `'confirmada'`
   - Se envía SMS/email de confirmación al cliente con:
     - Código de reserva
     - Fecha, hora, número de personas
     - Dirección del local
     - Teléfono de contacto
     - Recordatorio: "Llegar 10 min antes"
   - La mesa asignada (si aplica) cambia a estado `'reservada'` en el horario correspondiente
4. Gestión de reservas:
   - **Vista de calendario**: Todas las reservas por día/semana/mes
   - **Vista de lista**: Filtrable por fecha, estado, mesa
   - **Vista de timeline**: Reservas del día con horarios visuales
5. Estados de reserva:
   - **Confirmada**: Reserva activa
   - **Sentados**: Cliente llegó y se abrió la cuenta
   - **Completada**: Cliente pagó y se fue
   - **Cancelada**: Cliente canceló
   - **No Show**: Cliente no llegó
6. Operaciones:
   - **Confirmar llegada**: Al llegar el cliente, marcar como "Sentados" y abrir cuenta automáticamente
   - **Modificar reserva**: Cambiar hora, personas, mesa
   - **Cancelar reserva**: Por el cliente o por el local
   - **Marcar No Show**: Si pasan 15 min y no llegó
7. Notificaciones automáticas:
   - **24 horas antes**: Recordatorio al cliente por SMS/email
   - **1 hora antes**: Recordatorio al personal del local
   - **Al llegar**: Notificación al mesero asignado
8. Funcionalidades adicionales:
   - **Lista de espera**: Si no hay mesas, agregar cliente a waitlist
   - **Reporte de No Shows**: Para identificar clientes problemáticos
   - **Reservas recurrentes**: Para clientes habituales (ej: todos los viernes 8 PM)

**Consideraciones Multi-tenant:**
- Las reservas son por negocio y sede.
- Cada sede gestiona su propio calendario de reservas.

**Seguridad:**
- Requiere permiso: `reservas_gestionar`
- Los datos del cliente son sensibles (GDPR/protección de datos).

**UX:**
- Calendario visual tipo Google Calendar.
- Drag-and-drop para mover reservas entre mesas.
- Código de colores por estado.
- Búsqueda rápida de reservas por nombre o teléfono.
- Integración con WhatsApp para confirmaciones.

**Reglas de Negocio:**
- Las reservas se mantienen por 15 minutos después de la hora programada (tolerancia).
- Si el cliente no llega (No Show), la mesa se libera.
- Los clientes con 3 No Shows en 3 meses pueden ser bloqueados para reservas futuras.
- Las reservas para grupos grandes (> 10 personas) requieren confirmación telefónica.

---

### **RF-VTA-017: Gestionar Comandas de Cocina/Bar**

**Descripción:**  
El sistema debe gestionar el flujo de comandas desde que se toma el pedido hasta que se sirve, mostrando en cocina/bar qué preparar, en qué orden (por prioridad), y permitiendo actualizar el estado de cada item para comunicación en tiempo real con el mesero.

**Criterios de Aceptación:**
1. Generación de comandas:
   - Al confirmar un pedido en una cuenta de mesa (RF-VTA-013), el sistema:
     - Separa los items por área de preparación:
       - **Cocina**: Platos, entradas, acompañamientos
       - **Bar**: Bebidas, cócteles, tragos
       - **Snacks**: Items rápidos sin preparación
     - Genera una comanda por cada área
     - Envía a imprimir en la impresora correspondiente o muestra en KDS (Kitchen Display System)
2. Formato de comanda impresa:
   - **Encabezado**:
     - Número de mesa
     - Número de cuenta
     - Hora del pedido
     - Mesero
     - Prioridad (Normal, Urgente)
   - **Items**:
     - Cantidad
     - Descripción del producto
     - Modificadores (ej: "sin cebolla")
     - Observaciones especiales
   - Se imprimen duplicados: Original (cocina) + Copia (archivo)
3. Kitchen Display System (KDS) - Pantalla en Cocina/Bar:
   - Vista de todas las comandas activas
   - Organización:
     - Por orden de llegada (FIFO)
     - Por prioridad (urgentes primero)
     - Por tiempo transcurrido (más antiguas destacadas)
   - Cada comanda muestra:
     - Número de mesa (grande y destacado)
     - Items a preparar con cantidades
     - Tiempo transcurrido desde que se tomó el pedido (cronómetro)
     - Color coding:
       - 🟢 Verde: < 10 min (normal)
       - 🟡 Amarillo: 10-20 min (atención)
       - 🔴 Rojo: > 20 min (urgente/retrasado)
4. Estados de items en comanda:
   - **Pendiente**: Recién llegó, no se ha empezado
   - **En Preparación**: El cocinero/bartender está trabajando en ello
   - **Listo**: Terminado, esperando ser servido
   - **Servido**: El mesero lo entregó al cliente
5. Operaciones del personal de cocina/bar:
   - **Marcar como "En Preparación"**: Al comenzar a preparar
   - **Marcar como "Listo"**: Al terminar (notifica al mesero)
   - **Solicitar aclaración**: Si hay duda en el pedido (comunica al mesero)
   - **Rechazar item**: Si no hay insumos (notifica al mesero para informar al cliente)
6. Notificaciones:
   - **Al mesero**: Cuando items están listos para servir
   - **Al supervisor**: Si una comanda lleva > 30 min sin terminar
   - **Sonido**: En cocina al llegar nueva comanda
7. Reportes:
   - "Tiempo Promedio de Preparación por Producto"
   - "Comandas Atrasadas" (análisis de eficiencia)
   - "Items Más Solicitados por Horario"

**Consideraciones Multi-tenant:**
- Las comandas son por negocio y sede.
- Cada sede configura sus áreas de preparación independientemente.

**Seguridad:**
- El personal de cocina solo ve comandas, no precios ni totales.
- Los cambios de estado se auditan.

**UX:**
- Pantalla grande (monitor/tablet) en cocina con vista clara.
- Fuente grande y legible incluso a distancia.
- Botones táctiles grandes: "Iniciar", "Listo".
- Sonido distintivo para comandas urgentes.
- Modo nocturno (fondo oscuro) para reducir fatiga visual.

**Reglas de Negocio:**
- Las comandas urgentes (ej: "Cliente apurado") se priorizan visualmente.
- Si un item no se marca como "Listo" en 25 min, se alerta automáticamente al supervisor.
- Los items de bar (bebidas) se preparan más rápido que los de cocina (platos).
- Las comandas completadas se archivan después de 2 horas.

---

### **RF-VTA-018: Reportes de Ocupación y Rotación de Mesas**

**Descripción:**  
El sistema debe proporcionar reportes analíticos sobre el uso de mesas, incluyendo tasas de ocupación, tiempos promedio de ocupación, rotación, ingresos por mesa, y análisis de eficiencia operativa para optimizar la capacidad del restaurante/bar.

**Criterios de Aceptación:**
1. Métricas calculadas:
   - **Tasa de Ocupación**:
     - % de mesas ocupadas en un período
     - Fórmula: (Horas-mesa ocupadas / Horas-mesa disponibles) × 100
   - **Rotación de Mesas**:
     - Número promedio de veces que cada mesa se ocupa por día/turno
     - Fórmula: Total de cuentas cerradas / Número de mesas
   - **Tiempo Promedio de Ocupación**:
     - Duración promedio desde apertura hasta cierre de cuenta
     - Por tipo de servicio (almuerzo vs. cena)
   - **Ingresos por Mesa**:
     - Ticket promedio por cuenta
     - Ingreso total generado por cada mesa en el período
   - **Eficiencia de Meseros**:
     - Número de mesas atendidas
     - Ticket promedio
     - Propinas recibidas
2. Reportes disponibles:
   - **Reporte de Ocupación Diaria**:
     - Gráfico de línea mostrando % de ocupación por hora
     - Identificar horas pico y horas valle
   - **Análisis de Rotación**:
     - Tabla con cada mesa y su rotación del día
     - Identificar mesas "lentas" (baja rotación)
   - **Comparativo de Períodos**:
     - Hoy vs. ayer
     - Esta semana vs. semana pasada
     - Este mes vs. mes pasado
   - **Reporte de Ingresos por Mesa**:
     - Ranking de mesas más rentables
     - Identificar mesas problemáticas (baja facturación)
   - **Análisis de Reservas vs. Walk-ins**:
     - % de mesas ocupadas por reservas vs. clientes sin reserva
     - Tasa de No Show
   - **Heatmap de Ocupación**:
     - Mapa visual del local coloreado por frecuencia de uso
     - Identificar zonas más/menos populares
3. Filtros:
   - Rango de fechas
   - Día de la semana
   - Turno (almuerzo, cena, todo el día)
   - Área/zona del local
   - Mesa específica
4. Visualización:
   - Gráficos interactivos (líneas, barras, pastel)
   - Tablas con datos detallados
   - KPIs destacados con números grandes
   - Comparativas con indicadores ↑↓ de variación
5. Exportación:
   - PDF para reportes ejecutivos
   - Excel para análisis adicional
6. Insights automáticos:
   - "Las mesas se ocupan más rápido los viernes entre 8-10 PM"
   - "La mesa 12 tiene 30% más rotación que el promedio"
   - "El tiempo de ocupación aumentó 15% esta semana (posible servicio lento)"

**Consideraciones Multi-tenant:**
- Los reportes son por negocio y sede.
- Los gerentes pueden comparar entre sus propias sedes.

**Seguridad:**
- Requiere permiso: `reportes_mesas_ver`
- Solo accesible para gerencia y administración.

**UX:**
- Dashboard visual con gráficos dinámicos.
- Selector de fecha tipo daterangepicker.
- Heatmap interactivo (click en mesa para ver detalles).
- Descarga rápida de PDF con un clic.

**Reglas de Negocio:**
- La ocupación se calcula solo en horario de operación (no cuenta horas cerradas).
- Las mesas reservadas pero no ocupadas (No Show) no cuentan como ocupación.
- Los tiempos anormalmente largos (> 4 horas) se marcan como outliers para investigación.
- La rotación ideal varía según el tipo de establecimiento (bar vs. restaurante formal).

---
Perfecto, continuaré con el **Submódulo 6.4.3: Gestión de Pedidos**.

---

## **Submódulo 6.4.3: Gestión de Pedidos**

---

### **RF-VTA-019: Crear Pedido con Múltiples Modalidades**

**Descripción:**  
El sistema debe permitir crear pedidos en diferentes modalidades (delivery, pickup, mesa, barra) desde una interfaz unificada, capturando la información específica de cada tipo y asignando el flujo de gestión correspondiente.

**Criterios de Aceptación:**
1. El usuario inicia un nuevo pedido seleccionando la modalidad:
   - **Delivery**: Entrega a domicilio
   - **Pickup**: Recojo en tienda
   - **Mesa**: Servicio en mesa (integrado con RF-VTA-013)
   - **Barra**: Servicio directo en barra del bar
2. **Formulario base** (común a todas las modalidades):
   - **Cliente**:
     - Búsqueda por DNI/teléfono/nombre
     - Si es nuevo: Capturar datos básicos (nombre, teléfono, email opcional)
     - Si es registrado: Auto-completar dirección y preferencias
   - **Productos**:
     - Búsqueda y selección de productos
     - Cantidades
     - Modificadores y observaciones
     - Validación de stock en tiempo real
     - Verificación de edad para productos alcohólicos (obligatorio)
   - **Totales**:
     - Subtotal de productos
     - Descuentos/promociones (automáticos o manuales)
     - Costo de delivery (si aplica)
     - IGV (18%)
     - Total
3. **Campos específicos por modalidad**:
   - **Delivery**:
     - Dirección de entrega (autocompletar de historial)
     - Referencia/indicaciones (ej: "Casa verde, 2do piso")
     - Distrito/zona (para calcular costo de envío)
     - Coordenadas GPS (si se captura desde app móvil)
     - Tiempo estimado de entrega (calculado automáticamente)
     - Repartidor asignado (manual o automático)
     - Tipo de entrega: Estándar (45-60 min), Express (20-30 min - costo adicional)
   - **Pickup**:
     - Hora estimada de recojo (cliente selecciona)
     - Sede de recojo (si hay múltiples)
     - Código de retiro (generado automáticamente)
     - Observaciones para preparación
   - **Mesa**:
     - Número de mesa (selección del mapa)
     - Mesero asignado
     - Hora de toma del pedido
     - Prioridad (normal, urgente)
   - **Barra**:
     - Posición en barra (opcional)
     - Bartender asignado
     - Nombre del cliente (para llamar al entregar)
4. **Método de pago**:
   - Pago anticipado: Efectivo, tarjeta, transferencia, Yape/Plin
   - Pago contraentrega (solo delivery): Efectivo o POS móvil
   - Estado de pago: Pagado, Pendiente
5. Al crear el pedido:
   - Se genera número de pedido único (PED-YYYYMMDD-####)
   - Estado inicial según modalidad:
     - Delivery/Pickup: `'pendiente'` (espera confirmación)
     - Mesa/Barra: `'confirmado'` (va directo a preparación)
   - Se envía notificación al cliente (SMS/WhatsApp):
     - Número de pedido
     - Detalle de productos
     - Total a pagar
     - Tiempo estimado
     - Link de seguimiento en tiempo real (para delivery)
   - Se genera comanda para cocina/bar
   - Se reserva el inventario (no se descuenta aún)
6. Validaciones:
   - Para delivery: Dirección dentro del área de cobertura
   - Para pickup: Hora de recojo dentro del horario de atención
   - Para todos: Stock disponible, edad verificada para alcohol

**Consideraciones Multi-tenant:**
- Los pedidos son por negocio y sede.
- Cada sede tiene su zona de cobertura para delivery.

**Seguridad:**
- Requiere permiso: `pedidos_crear`
- La verificación de edad es obligatoria y se registra.
- Auditoría de quién creó el pedido.

**UX:**
- Wizard de 4 pasos: Modalidad → Cliente → Productos → Pago.
- Autocompletado de direcciones con Google Maps API.
- Calculadora de costo de delivery en tiempo real.
- Vista previa del pedido antes de confirmar.
- Opción de "Repetir Último Pedido" para clientes frecuentes.

**Reglas de Negocio:**
- Los pedidos de delivery tienen monto mínimo (ej: $20 USD) configurable por zona.
- Los pedidos con alcohol requieren que el cliente esté registrado (para verificar edad en entrega).
- Las órdenes pickup con más de 20 items requieren 1 hora de anticipación.
- Los pedidos express tienen recargo del 30% en el costo de delivery.

---

### **RF-VTA-020: Confirmar y Procesar Pedidos**

**Descripción:**  
El sistema debe gestionar el flujo de confirmación de pedidos, permitiendo al personal revisar, validar disponibilidad final, estimar tiempos de preparación, y cambiar el estado a "confirmado" para iniciar la preparación.

**Criterios de Aceptación:**
1. Vista de "Pedidos Pendientes de Confirmación":
   - Lista de pedidos en estado `'pendiente'`
   - Información resumida: Número, cliente, modalidad, total, hora
   - Filtros: Por modalidad, por hora, por estado de pago
   - Ordenamiento: Por hora de creación (más antiguos primero)
   - Alertas visuales: Pedidos > 5 min sin confirmar (color amarillo)
2. Al abrir un pedido para confirmar, el sistema muestra:
   - **Datos del cliente**: Nombre, teléfono, dirección (delivery), historial de pedidos
   - **Detalle de productos**: Lista completa con cantidades y observaciones
   - **Validación de stock**: Indicador verde (hay stock) o rojo (sin stock)
   - **Tiempo estimado**: Sugerencia automática basada en:
     - Tipo de productos (bebidas frías: 10 min, comida: 30 min, cócteles elaborados: 20 min)
     - Carga actual de cocina/bar (si hay muchos pedidos activos, incrementa tiempo)
     - Distancia de delivery (si aplica)
   - **Forma de pago y estado**: Pagado o pendiente
3. Operaciones de confirmación:
   - **Confirmar**:
     - Validar stock final (doble verificación)
     - Editar tiempo estimado si es necesario
     - Para delivery: Asignar repartidor (manual o automático)
     - El pedido cambia a estado `'confirmado'`
     - Se envía notificación al cliente: "Tu pedido #X ha sido confirmado. Tiempo estimado: Y minutos"
     - Se genera comanda impresa para cocina/bar
     - Se reserva el inventario (bloqueo soft)
   - **Rechazar**:
     - Motivo obligatorio: Sin stock, fuera de zona de delivery, cliente no respondió, etc.
     - El pedido cambia a estado `'rechazado'`
     - Se notifica al cliente con el motivo
     - Si hubo pago anticipado, se procesa reembolso automáticamente
     - Se libera la reserva de inventario
   - **Solicitar aclaración**:
     - Llamar al cliente para confirmar dirección, especificaciones del producto, etc.
     - El pedido permanece en `'pendiente'` hasta resolver
4. Asignación automática de repartidores (para delivery):
   - El sistema selecciona al repartidor más cercano disponible
   - Considera:
     - Ubicación actual del repartidor (GPS)
     - Número de pedidos actuales asignados
     - Calificación del repartidor
   - Se puede reasignar manualmente si es necesario
5. Notificaciones:
   - **Al cliente**: SMS/WhatsApp con confirmación y tiempo estimado
   - **A cocina/bar**: Visual en KDS + impresión de comanda
   - **Al repartidor** (si delivery): Notificación push en app móvil

**Consideraciones Multi-tenant:**
- Los pedidos se confirman en el contexto del negocio correspondiente.

**Seguridad:**
- Requiere permiso: `pedidos_confirmar`
- Los rechazos se auditan (prevenir abuso).
- Los reembolsos automáticos requieren configuración del gateway de pagos.

**UX:**
- Vista tipo kanban: Pendientes | Confirmados | En Preparación | Listos | En Camino | Entregados
- Confirmación con un clic para pedidos estándar.
- Botón de "Llamar al Cliente" con integración telefónica.
- Cronómetro mostrando tiempo transcurrido desde la creación del pedido.

**Reglas de Negocio:**
- Los pedidos deben confirmarse en máximo 5 minutos (SLA).
- Si pasan 10 minutos sin confirmación, se notifica al supervisor.
- Los pedidos no confirmados después de 30 minutos se auto-cancelan y se notifica al cliente.
- Los pedidos con pago pendiente tienen menor prioridad de confirmación.

---

### **RF-VTA-021: Rastrear Estado de Pedido en Tiempo Real**

**Descripción:**  
El sistema debe proporcionar seguimiento en tiempo real del estado de cada pedido, mostrando su progreso a través de las diferentes etapas del flujo (confirmado → en preparación → listo → en camino → entregado), con actualización automática y notificaciones al cliente.

**Criterios de Aceptación:**
1. Estados del ciclo de vida del pedido:
   - **Pendiente**: Esperando confirmación del negocio
   - **Confirmado**: Aceptado, en espera de preparación
   - **En Preparación**: Cocina/bar está preparando
   - **Listo**: Preparado, esperando entrega/recojo
   - **En Camino**: Repartidor en ruta (solo delivery)
   - **Entregado/Completado**: Cliente recibió el pedido
   - **Cancelado**: Pedido cancelado por cliente o negocio
   - **Rechazado**: No se pudo procesar
2. Vista de "Seguimiento de Pedidos" (para personal):
   - **Panel Kanban**: Columnas por estado, tarjetas de pedidos
   - Información en cada tarjeta:
     - Número de pedido (destacado)
     - Cliente
     - Modalidad (icono: 🛵 delivery, 🏪 pickup, 🍽️ mesa, 🍸 barra)
     - Hora de creación
     - Tiempo transcurrido (cronómetro)
     - Estado actual
     - Próxima acción requerida
   - Drag & drop: Arrastrar tarjetas entre columnas para cambiar estado
   - Color coding por urgencia:
     - 🟢 Verde: Dentro del tiempo estimado
     - 🟡 Amarillo: Cerca del límite (80% del tiempo)
     - 🔴 Rojo: Sobrepasó el tiempo estimado (retrasado)
3. Actualización de estados:
   - **Manual**: El personal hace clic en "Marcar como [estado]"
   - **Automática** (con integraciones):
     - "En Preparación": Al imprimir comanda en cocina
     - "Listo": Al marcar todos los items como terminados en KDS
     - "En Camino": Cuando repartidor confirma salida con GPS
     - "Entregado": Cuando repartidor marca como entregado (con firma/foto)
4. Cada cambio de estado:
   - Registra fecha/hora exacta y usuario responsable
   - Envía notificación push al cliente (si tiene app móvil o WhatsApp)
   - Actualiza el timeline visible del pedido
   - Si hay retraso, calcula nueva ETA (Estimated Time of Arrival)
5. Página de seguimiento para el cliente:
   - URL única por pedido: `https://mitienda.com/pedido/PED-20260131-0001`
   - Sin login requerido (seguridad por obscuridad del URL)
   - Muestra:
     - Timeline vertical con estados completados ✓ y pendientes ○
     - Hora de cada transición
     - Tiempo estimado de entrega
     - Para delivery: Mapa en vivo con ubicación del repartidor (actualización cada 30 seg)
     - Botón "Contactar al Negocio" (llama o abre WhatsApp)
     - Detalle de productos ordenados
6. Notificaciones automáticas al cliente:
   - "Tu pedido ha sido confirmado ✓"
   - "Tu pedido está siendo preparado 👨‍🍳"
   - "Tu pedido está listo y en camino 🛵"
   - "Tu pedido ha llegado 🎉 ¡Buen provecho!"
7. Alertas internas:
   - Pedido > 15 min en "Confirmado" sin pasar a "En Preparación" → Alerta a cocina
   - Pedido > tiempo estimado + 10 min → Alerta a gerente
   - Repartidor con GPS inactivo > 10 min → Alerta a supervisor

**Consideraciones Multi-tenant:**
- Cada cliente solo ve sus propios pedidos.
- El personal ve todos los pedidos de su sede.

**Seguridad:**
- Las URLs de seguimiento son difíciles de adivinar (token único).
- No se exponen datos sensibles de otros clientes.
- Solo el personal autorizado puede cambiar estados manualmente.

**UX:**
- Actualización en tiempo real sin recargar página (WebSockets/polling).
- Animaciones al cambiar de estado.
- Sonido de notificación cuando pedido cambia a "Listo" (para repartidores).
- Vista de mapa integrada con Google Maps para delivery.

**Reglas de Negocio:**
- No se puede retroceder estados (ej: de "Entregado" a "En Preparación") sin autorización.
- Los estados se registran con timestamp inmutable para auditoría.
- Los pedidos "Entregados" se archivan después de 7 días (no aparecen en panel activo).

---

### **RF-VTA-022: Gestionar Zonas y Costos de Delivery**

**Descripción:**  
El sistema debe permitir configurar zonas geográficas de cobertura para delivery, asignando costos diferenciados por distancia o zona, tiempos estimados de entrega, y validando que las direcciones de pedidos estén dentro del área de servicio.

**Criterios de Aceptación:**
1. Configuración de zonas de delivery:
   - El administrador accede a "Configuración de Delivery"
   - Puede crear zonas mediante:
     - **Dibujo en mapa**: Polígono en Google Maps delimitando el área
     - **Por distrito/barrio**: Selección de lista predefinida
     - **Por radio**: Círculo de X km desde la sede
   - Para cada zona se configura:
     - Nombre de la zona (ej: "Zona Centro", "Zona Norte")
     - Costo de delivery (monto fijo o calculado por distancia)
     - Tiempo estimado de entrega (ej: 30-45 min)
     - Monto mínimo de pedido (ej: $25 USD)
     - Disponibilidad horaria (ej: Solo hasta 11 PM)
     - Estado (activa/inactiva)
     - Color en el mapa (identificación visual)
2. Cálculo automático de costo:
   - **Por zona**: Costo fijo según la zona (ej: Centro $5, Norte $8, Sur $10)
   - **Por distancia**: Costo base + $X por km adicional
     - Ejemplo: $3 base + $1 por km después del primer km
     - Se calcula usando Google Distance Matrix API
   - **Por tiempo**: Delivery express (20 min) cuesta más que estándar (45 min)
   - **Promociones**: "Delivery gratis en pedidos > $50 USD"
3. Validación de dirección al crear pedido:
   - El cliente ingresa su dirección
   - El sistema:
     - Geolocaliza la dirección (Google Geocoding API)
     - Verifica si está dentro de alguna zona configurada
     - Si está dentro: Calcula y muestra costo de delivery
     - Si está fuera: "Lo sentimos, no hacemos entregas en tu zona aún"
   - Muestra en el resumen:
     - Zona identificada (ej: "Zona Centro")
     - Distancia desde la sede (ej: 3.2 km)
     - Costo de delivery
     - Tiempo estimado de entrega
4. Mapa de cobertura público:
   - Página en el sitio web/app: "¿Hacemos delivery en tu zona?"
   - Muestra mapa con todas las zonas coloreadas
   - El cliente puede:
     - Ver visualmente el área de cobertura
     - Ingresar su dirección para validar
     - Ver el costo estimado antes de ordenar
5. Gestión de excepciones:
   - **Fuera de zona**: Permitir pedidos excepcionales con aprobación
   - **Zona temporal**: Crear zona de delivery especial para eventos (ej: concierto, festival)
   - **Bloqueo de zona**: Desactivar delivery en zona específica (ej: por peligro, mal clima)
6. Reportes:
   - "Pedidos por Zona" (identificar zonas más rentables)
   - "Tiempo Promedio de Entrega por Zona"
   - "Zonas con Mayor Tasa de Cancelación"
   - "Análisis de Cobertura": Zonas potenciales para expandir

**Consideraciones Multi-tenant:**
- Cada sede configura sus propias zonas de delivery.
- Las zonas no se cruzan entre negocios diferentes.

**Seguridad:**
- Requiere permiso: `delivery_zonas_configurar`
- Solo administradores pueden modificar costos y zonas.

**UX:**
- Editor de mapa interactivo con herramientas de dibujo.
- Vista previa del área en el mapa al configurar.
- Sugerencias de zonas basadas en pedidos históricos.
- Calculadora de costos en tiempo real al crear pedido.

**Reglas de Negocio:**
- El costo de delivery no puede ser negativo (gratis = 0, no negativo).
- Las zonas pueden superponerse; en ese caso, se aplica el costo más bajo.
- Los costos de delivery son adicionales al total del pedido (no se descuentan del producto).
- Las promociones de "delivery gratis" solo aplican si el pedido supera el monto mínimo configurado.

---

### **RF-VTA-023: Asignar y Gestionar Repartidores**

**Descripción:**  
El sistema debe facilitar la asignación de pedidos delivery a repartidores disponibles, permitiendo seguimiento en tiempo real de su ubicación, gestión de múltiples pedidos simultáneos, y evaluación de desempeño.

**Criterios de Aceptación:**
1. Registro y gestión de repartidores:
   - Información básica:
     - Nombre completo
     - DNI/ID
     - Teléfono
     - Email
     - Foto
   - Información laboral:
     - Tipo de vehículo (moto, bicicleta, auto, a pie)
     - Placa del vehículo
     - Licencia de conducir (número y vencimiento)
     - Sede/zona asignada
     - Turno (mañana, tarde, noche, completo)
     - Estado: Activo, Inactivo, Suspendido
   - Credenciales de acceso a app móvil
2. Estados del repartidor:
   - **Disponible**: Listo para recibir pedidos
   - **En Ruta**: Tiene pedido(s) asignado(s) en entrega
   - **No Disponible**: En descanso, fuera de turno
   - **Desconectado**: No ha iniciado sesión en la app
3. Asignación de pedidos:
   - **Manual**:
     - Desde el panel de pedidos, seleccionar "Asignar Repartidor"
     - Lista de repartidores disponibles con:
       - Nombre
       - Ubicación actual (distancia a la sede)
       - Pedidos activos (número)
       - Calificación promedio
       - Última entrega hace X minutos
     - Seleccionar repartidor y confirmar
   - **Automática** (algoritmo inteligente):
     - Al confirmar el pedido, el sistema asigna automáticamente
     - Criterios de selección:
       - Repartidor más cercano a la sede
       - Menor carga (menos pedidos activos)
       - Mejor calificación
       - Disponibilidad inmediata
     - Se puede configurar la prioridad de cada criterio
4. App móvil del repartidor:
   - **Login**: Con credenciales únicas
   - **Dashboard**:
     - Estado actual (Disponible / En Ruta)
     - Pedidos asignados (lista)
     - Botón grande: "Marcar como Disponible" / "Iniciar Descanso"
   - **Notificación de nuevo pedido**:
     - Alerta push: "Nuevo pedido asignado #PED-001"
     - Detalles: Cliente, dirección, productos, total
     - Mapa con ruta desde sede a destino
     - Botón: "Aceptar" / "Rechazar" (si puede rechazar)
   - **En ruta**:
     - Lista de pedidos activos (si hay múltiples)
     - Navegación GPS integrada (Google Maps / Waze)
     - Botón: "Llegué al destino"
     - Tracking GPS activo (envía ubicación cada 30 seg)
   - **En el destino**:
     - Confirmar entrega:
       - Método de pago (si es contraentrega)
       - Captura de firma del cliente (en pantalla táctil)
       - Foto de evidencia (opcional)
       - Observaciones (ej: "Cliente no estaba, dejé con vecino")
     - Botón: "Pedido Entregado"
   - **Historial**: Pedidos entregados del día/semana
5. Panel de seguimiento (para supervisor):
   - Mapa con todos los repartidores activos (pins en tiempo real)
   - Click en repartidor: Ver pedidos asignados, ruta, ETA
   - Estadísticas en vivo:
     - Repartidores disponibles / en ruta / no disponibles
     - Pedidos en espera de asignación
     - Tiempo promedio de entrega del día
6. Gestión de múltiples pedidos:
   - Un repartidor puede tener hasta N pedidos asignados simultáneamente (configurable, ej: 3)
   - El sistema optimiza la ruta para entregar todos en orden eficiente
   - El repartidor ve la secuencia sugerida: "Entregar primero #PED-001, luego #PED-002"
7. Evaluación de desempeño:
   - Métricas automáticas:
     - Pedidos entregados (total)
     - Tiempo promedio de entrega
     - Calificación promedio del cliente (estrellas)
     - Tasa de entregas a tiempo (dentro del tiempo estimado)
     - Incidencias (quejas, pedidos no entregados)
   - Reportes:
     - "Repartidor del Mes" (mejor desempeño)
     - "Análisis de Eficiencia por Repartidor"

**Consideraciones Multi-tenant:**
- Los repartidores son por negocio/sede.
- No se comparten entre negocios.

**Seguridad:**
- App móvil con autenticación segura (JWT tokens).
- Tracking GPS solo activo cuando está "En Ruta" (privacidad).
- Las firmas digitales tienen validez legal.
- Auditoría de todas las asignaciones y entregas.

**UX:**
- App móvil intuitiva y rápida (repartidor en movimiento).
- Botones grandes para operación con guantes/una mano.
- Modo nocturno en la app (conducción nocturna).
- Notificaciones sonoras claras.
- Offline mode: Si pierde conexión, sincroniza al recuperarla.

**Reglas de Negocio:**
- Los repartidores deben marcar inicio y fin de turno para control de asistencia.
- Los rechazos frecuentes de pedidos pueden resultar en penalización.
- El tracking GPS es obligatorio durante entregas (por seguridad y transparencia).
- Los repartidores deben verificar edad del cliente al entregar productos alcohólicos (capacitación obligatoria).

---

### **RF-VTA-024: Gestionar Cancelaciones y Devoluciones de Pedidos**

**Descripción:**  
El sistema debe permitir gestionar cancelaciones de pedidos en diferentes etapas, procesar devoluciones post-entrega, y manejar reembolsos de manera transparente, protegiendo tanto al negocio como al cliente.

**Criterios de Aceptación:**
1. Cancelación por el cliente:
   - **Antes de confirmación** (estado `'pendiente'`):
     - Cliente puede cancelar libremente desde su página de seguimiento
     - Motivo opcional
     - Si hubo pago anticipado: Reembolso automático del 100%
     - Estado cambia a `'cancelado_cliente'`
   - **Después de confirmación** (estados `'confirmado'` o `'en_preparacion'`):
     - Cliente debe contactar al negocio (botón "Solicitar Cancelación")
     - El negocio evalúa:
       - Si no se inició preparación: Acepta cancelación, reembolso 100%
       - Si ya se preparó: Puede rechazar cancelación o aplicar penalidad (ej: reembolso 50%)
     - Requiere aprobación de supervisor
   - **En ruta o entregado**:
     - No se permite cancelación, solo devolución (ver abajo)
2. Cancelación por el negocio:
   - Motivos válidos:
     - Sin stock después de confirmar
     - No se pudo contactar al cliente
     - Dirección inválida o fuera de zona
     - Cliente solicitó cancelación
     - Problema operativo (falla de sistema, emergencia)
   - Proceso:
     - Seleccionar pedido y clic en "Cancelar Pedido"
     - Motivo obligatorio (dropdown + texto libre)
     - Si hubo pago: Procesar reembolso automáticamente
     - Notificar al cliente inmediatamente con disculpas
     - Estado cambia a `'cancelado_negocio'`
   - Compensación opcional:
     - Cupón de descuento para próxima compra
     - Delivery gratis en próximo pedido
3. Políticas de reembolso configurables:
   - Por estado del pedido:
     - Pendiente: 100%
     - Confirmado (< 5 min): 100%
     - En Preparación: 70% (se descontó preparación)
     - Listo/En Camino: 50% o No reembolsable (política del negocio)
   - Por responsable:
     - Cancelación por negocio: Siempre 100% + compensación
     - Cancelación por cliente: Según política
4. Devolución post-entrega:
   - El cliente puede solicitar devolución en estos casos:
     - Producto defectuoso o en mal estado
     - Producto equivocado (error en el pedido)
     - Producto vencido
     - No cumplió expectativas (solo si política lo permite)
   - Proceso:
     - Cliente contacta al negocio (llamada, WhatsApp, app)
     - Se registra la solicitud de devolución
     - Se asigna a un agente de atención al cliente
     - Validación:
       - Verificar que el pedido fue entregado
       - Revisar evidencia (fotos del cliente)
       - Evaluar motivo
     - Resoluciones posibles:
       - **Reenvío**: Enviar el producto correcto sin costo
       - **Reembolso**: Parcial o total según el caso
       - **Crédito**: Saldo a favor para futuras compras
       - **Rechazo**: Si no procede la reclamación (con justificación)
5. Gestión de reembolsos:
   - **Efectivo**: Se genera vale de reembolso, cliente puede cobrarlo en sede
   - **Tarjeta**: Reversión automática via gateway de pagos (5-10 días hábiles)
   - **Yape/Plin**: Transferencia manual a cuenta del cliente (mismo día)
   - **Crédito**: Se agrega saldo a cuenta del cliente en el sistema
6. Reportes y análisis:
   - "Tasa de Cancelación" (por período)
   - "Motivos Frecuentes de Cancelación" (identificar problemas recurrentes)
   - "Tasa de Devolución por Producto" (productos problemáticos)
   - "Impacto Financiero de Cancelaciones"

**Consideraciones Multi-tenant:**
- Las políticas de cancelación son configurables por negocio.
- Los reembolsos afectan la contabilidad del negocio correspondiente.

**Seguridad:**
- Requiere permiso: `pedidos_cancelar` (negocio), cliente puede cancelar sus propios pedidos
- Reembolsos > $100 USD requieren aprobación de gerente.
- Auditoría completa de cancelaciones y reembolsos (prevenir fraude).

**UX:**
- Botón "Cancelar Pedido" visible en página de seguimiento.
- Wizard guiado para devoluciones con captura de fotos.
- Notificaciones claras sobre el estado del reembolso.
- Timeline mostrando: Solicitud → Evaluación → Aprobación → Reembolso procesado.

**Reglas de Negocio:**
- Las cancelaciones frecuentes (> 3 en un mes) por el mismo cliente generan alerta (posible abuso).
- Los productos alcohólicos entregados NO se aceptan de vuelta por regulación sanitaria (salvo defecto de fábrica).
- Los reembolsos se procesan en máximo 7 días hábiles.
- Las cancelaciones por el negocio afectan negativamente las métricas de servicio.

---

### **RF-VTA-025: Calificar Pedidos y Repartidores**

**Descripción:**  
El sistema debe permitir a los clientes calificar su experiencia después de recibir un pedido, evaluando la calidad de productos, servicio, tiempo de entrega, y desempeño del repartidor, utilizando esta retroalimentación para mejora continua.

**Criterios de Aceptación:**
1. Activación de calificación:
   - Después de que un pedido se marca como `'entregado'`
   - El cliente recibe notificación (SMS/email/push):
     - "¡Gracias por tu pedido! ¿Cómo estuvo todo? Califícanos"
     - Link directo a formulario de calificación
   - Plazo para calificar: 48 horas después de la entrega
2. Formulario de calificación:
   - **Calificación general** (obligatoria):
     - Estrellas de 1 a 5 ⭐⭐⭐⭐⭐
     - 1 = Muy malo, 5 = Excelente
   - **Aspectos específicos** (opcionales pero recomendados):
     - Calidad de los productos: 1-5 estrellas
     - Tiempo de entrega: 1-5 estrellas (o: Más rápido de lo esperado / Como esperaba / Más lento)
     - Empaque/presentación: 1-5 estrellas
     - Para delivery: Atención del repartidor: 1-5 estrellas
   - **Comentarios** (texto libre, opcional):
     - "Cuéntanos más sobre tu experiencia"
     - Límite: 500 caracteres
   - **Problemas específicos** (multiselección, solo si calificación < 4):
     - Producto equivocado
     - Producto en mal estado
     - Entrega tardía
     - Mal trato
     - Empaque dañado
     - Otro (especificar)
3. Incentivos para calificar:
   - "Califica este pedido y recibe 10% OFF en tu próxima compra"
   - Sorteo mensual entre clientes que califiquen
   - Puntos de fidelidad (si hay programa de lealtad)
4. Gestión de calificaciones:
   - **Vista de administración**:
     - Lista de pedidos con calificaciones
     - Filtros: Por calificación (5⭐, 4⭐, etc.), por fecha, por repartidor
     - Calificaciones negativas (< 3⭐) destacadas en rojo (requieren atención)
   - **Calificaciones bajas** (< 3 estrellas):
     - Generan alerta automática al gerente
     - Se asigna a agente de atención para follow-up
     - Contactar al cliente: Ofrecer disculpas, solución, compensación
     - Registrar acciones tomadas
   - **Calificaciones altas** (5 estrellas):
     - Oportunidad de compartir en redes sociales
     - Solicitar testimonio para marketing
5. Impacto en repartidores:
   - Cada repartidor tiene:
     - Calificación promedio general (1-5)
     - Número total de calificaciones
     - % de calificaciones de 5 estrellas
     - Comentarios recientes
   - Calificaciones bajas recurrentes:
     - Generan alerta a supervisor
     - Se programa capacitación o coaching
     - Si promedio cae < 3.5, suspensión temporal hasta mejorar
   - Calificaciones altas:
     - Bonificación mensual por excelencia
     - Reconocimiento en tablero de líderes
6. Reportes y análisis:
   - **Dashboard de Satisfacción**:
     - NPS (Net Promoter Score): % de clientes que recomendarían
     - Promedio general de calificación (ej: 4.7/5)
     - Tendencia en el tiempo (gráfico)
   - **Análisis de problemas**:
     - Top 5 problemas reportados
     - Productos con más quejas
     - Repartidores con mejor/peor desempeño
   - **Feedback por categoría**:
     - Calidad: Promedio 4.5/5
     - Tiempo: Promedio 4.2/5
     - Servicio: Promedio 4.8/5
7. Respuestas a calificaciones:
   - El negocio puede responder a comentarios:
     - Agradecer calificaciones positivas
     - Ofrecer disculpas y soluciones para negativas
     - Las respuestas se muestran al cliente
   - Plantillas predefinidas para respuestas rápidas

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio sistema de calificaciones independiente.
- Los repartidores solo son evaluados en el contexto de su negocio.

**Seguridad:**
- Solo el cliente que hizo el pedido puede calificarlo (validación por token único).
- Las calificaciones son anónimas para el repartidor (no ve quién calificó, solo el promedio).
- No se permiten calificaciones spam o abusivas.

**UX:**
- Formulario corto y simple (< 1 minuto para completar).
- Interfaz visual atractiva con iconos y colores.
- Confirmación: "¡Gracias por tu feedback! Nos ayuda a mejorar ✓".
- Opción de calificar directamente desde WhatsApp (mensajes interactivos).

**Reglas de Negocio:**
- Las calificaciones son permanentes, no se pueden editar después de enviadas.
- Los comentarios ofensivos o inapropiados se moderan y eliminan.
- El promedio de calificación se actualiza en tiempo real.
- Los clientes que nunca califican pierden acceso a ciertos descuentos exclusivos (incentivo suave).

---

### **RF-VTA-026: Integrar con Plataformas de Delivery Externas**

**Descripción:**  
El sistema debe integrarse con plataformas de delivery de terceros (Rappi, PedidosYa, Uber Eats) para recibir automáticamente pedidos en el sistema central, sincronizar inventario, y gestionar todos los pedidos desde una sola interfaz unificada.

**Criterios de Aceptación:**
1. Integraciones soportadas:
   - **Rappi**: Via API oficial
   - **PedidosYa**: Via API oficial
   - **Uber Eats**: Via API oficial
   - Otras plataformas según disponibilidad de API
2. Configuración de integración:
   - Para cada plataforma, el administrador ingresa:
     - Credenciales de API (API Key, Secret)
     - ID de restaurante en la plataforma
     - Configuración de sincronización:
       - Inventario: Sí/No (actualizar stock automáticamente)
       - Precios: Usar precios de la plataforma o sobrescribir
       - Categorías: Mapeo de categorías internas a categorías de la plataforma
   - Estado: Activa/Inactiva (se puede pausar temporalmente)
3. Sincronización de productos:
   - **Exportar catálogo**: Enviar productos del sistema a la plataforma
     - Nombre, descripción, precio, foto
     - Categoría, disponibilidad
     - Modificadores (tamaños, opciones)
   - **Sincronización de stock**:
     - Cuando un producto se vende en POS, actualizar disponibilidad en todas las plataformas
     - Si un producto se agota, marcarlo como "no disponible" en las plataformas
     - Actualización en tiempo real o cada X minutos (configurable)
4. Recepción de pedidos:
   - Cuando llega un pedido desde una plataforma externa:
     - El sistema recibe webhook/notificación
     - Crea automáticamente un pedido en el sistema con:
       - Origen: Rappi/PedidosYa/Uber Eats (etiqueta visible)
       - Cliente: Datos anonimizados (nombre, teléfono)
       - Dirección de entrega (ya viene de la plataforma)
       - Productos ordenados con cantidades
       - Observaciones del cliente
       - Total (ya incluye comisiones de la plataforma)
       - Modalidad: Siempre "Delivery"
       - Estado: `'confirmado'` (auto-confirmar, la plataforma ya validó)
     - Se genera comanda automáticamente para cocina/bar
     - Se descuenta inventario vía FIFO
     - El repartidor es de la plataforma (no del negocio)
5. Gestión unificada:
   - **Panel de pedidos consolidado**:
     - Muestra pedidos de todas las fuentes en una sola vista:
       - POS directo
       - App propia
       - Rappi (icono 🟢)
       - PedidosYa (icono 🔴)
       - Uber Eats (icono ⚫)
     - Filtro por origen
   - **Flujo de trabajo único**:
     - Cocina recibe comandas de todos los orígenes sin distinción
     - El personal actualiza estados normalmente
     - Los estados se sincronizan de vuelta a la plataforma (si la API lo soporta):
       - "En Preparación" → Actualiza en Rappi
       - "Listo" → Notifica a la plataforma
6. Sincronización de estados:
   - Cuando el negocio actualiza el estado del pedido:
     - Se envía actualización a la plataforma correspondiente
     - Ej: Marcar como "Listo" en el sistema → Rappi notifica al repartidor
   - Cuando la plataforma actualiza (ej: repartidor entregó):
     - Se recibe webhook
     - Se actualiza estado en el sistema a `'entregado'`
7. Gestión de rechazos:
   - Si el negocio no puede procesar un pedido de plataforma externa:
     - Marcar como rechazado con motivo
     - Enviar rechazo a la plataforma (API)
     - La plataforma notifica al cliente y gestiona reembolso
8. Reportes financieros:
   - Ventas separadas por canal:
     - Directas: $X
     - Rappi: $Y (con comisión Z%)
     - PedidosYa: $W (con comisión V%)
   - Cálculo de comisiones automático
   - Reporte de "Rentabilidad por Canal"

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propias integraciones.
- Las credenciales de API son únicas por negocio.

**Seguridad:**
- Las credenciales de API se almacenan encriptadas.
- Solo administradores pueden configurar integraciones.
- Validación de firma en webhooks (autenticidad).

**UX:**
- Configuración guiada: "Conectar con Rappi en 3 pasos".
- Indicador visual de estado de integración (Conectado ✓ / Error ❌).
- Etiquetas de color en pedidos según origen.
- Notificación sonora distinta para pedidos de plataformas (opcional).

**Reglas de Negocio:**
- Los pedidos de plataformas externas no se pueden modificar (ya están confirmados por el cliente en la plataforma).
- Las comisiones de las plataformas se descuentan del ingreso bruto (no del cliente).
- Si hay error de sincronización, se prioriza la venta (no perder pedido) y se corrige después.
- Los productos alcohólicos en plataformas externas requieren configuración especial (verificación de edad por la plataforma).

---

### **RF-VTA-027: Reportes y Analítica de Pedidos**

**Descripción:**  
El sistema debe proporcionar reportes comprehensivos y análisis de los pedidos realizados, permitiendo entender patrones de venta, eficiencia operativa, rentabilidad por canal, y comportamiento de clientes para toma de decisiones estratégicas.

**Criterios de Aceptación:**
1. Métricas principales (KPIs):
   - **Volumen de pedidos**:
     - Total de pedidos (período seleccionado)
     - Pedidos por modalidad (delivery, pickup, mesa, barra)
     - Pedidos por canal (directo, Rappi, PedidosYa, etc.)
     - Tendencia (vs. período anterior): ↑ 15%
   - **Valor de pedidos**:
     - Ventas totales ($)
     - Ticket promedio
     - Ticket más alto/bajo del período
   - **Eficiencia operativa**:
     - Tiempo promedio de preparación
     - Tiempo promedio de entrega (delivery)
     - % de pedidos a tiempo (dentro del tiempo estimado)
     - % de pedidos retrasados
   - **Calidad de servicio**:
     - Calificación promedio (estrellas)
     - % de pedidos cancelados
     - Tasa de devolución
   - **Rentabilidad**:
     - Margen bruto por modalidad
     - Costo de delivery promedio
     - Comisiones pagadas a plataformas
2. Reportes disponibles:
   - **Reporte de Ventas por Modalidad**:
     - Tabla y gráfico comparando delivery, pickup, mesa, barra
     - Monto y % de cada modalidad
     - Identificar la más rentable
   - **Análisis Temporal**:
     - Ventas por hora del día (identificar horas pico)
     - Ventas por día de la semana
     - Tendencia mensual/anual
     - Gráfico de línea temporal
   - **Productos Más Pedidos**:
     - Top 20 productos por cantidad vendida
     - Top 20 por ingresos generados
     - Productos con mayor crecimiento
     - Productos en declive
   - **Análisis Geográfico** (para delivery):
     - Mapa de calor: Zonas con más pedidos
     - Ventas por zona de delivery
     - Identificar zonas potenciales para expandir
   - **Análisis de Canales**:
     - Comparativa: Directo vs. Rappi vs. PedidosYa vs. Uber Eats
     - Comisiones pagadas por canal
     - Rentabilidad neta por canal
     - Gráfico de pastel: Distribución de ventas
   - **Reporte de Clientes**:
     - Nuevos clientes vs. recurrentes
     - Frecuencia de compra promedio
     - Clientes más valiosos (mayor valor de vida - LTV)
     - Tasa de retención
   - **Reporte de Eficiencia**:
     - Tiempo promedio por etapa (confirmación → preparación → entrega)
     - Pedidos atendidos por hora/día
     - Cuellos de botella identificados (ej: cocina lenta en fines de semana)
   - **Reporte de Satisfacción**:
     - Evolución de calificaciones en el tiempo
     - Comparativa de calificación por repartidor
     - Análisis de comentarios (palabras clave más mencionadas)
3. Filtros y segmentación:
   - **Por fecha**: Hoy, ayer, últimos 7 días, últimos 30 días, mes actual, rango personalizado
   - **Por modalidad**: Delivery, pickup, mesa, barra
   - **Por canal**: Directo, plataformas externas
   - **Por sede**: Si hay múltiples sedes
   - **Por producto/categoría**: Enfocarse en línea específica
   - **Por cliente**: Análisis de cliente individual
   - **Por repartidor**: Desempeño individual
4. Visualización:
   - **Dashboards interactivos**:
     - Gráficos dinámicos (barras, líneas, pastel, área)
     - Tablas con ordenamiento y paginación
     - Filtros laterales para drilling down
   - **Comparativas visuales**:
     - Este mes vs. mes pasado (barras lado a lado)
     - Tendencia con línea de regresión
   - **Indicadores de variación**:
     - Porcentaje de cambio con flechas ↑↓
     - Color coding: Verde (crecimiento), rojo (caída)
5. Exportación:
   - **PDF**: Reporte formateado profesionalmente con logo
   - **Excel**: Datos crudos para análisis adicional
   - **CSV**: Para importar a otros sistemas
   - **Email programado**: Envío automático de reportes semanales/mensuales
6. Insights automáticos (IA/ML):
   - "Los pedidos de delivery aumentan 40% los viernes después de las 8 PM"
   - "El producto 'Cerveza Corona' tiene alta demanda pero bajo stock actualmente"
   - "La zona Norte tiene potencial de crecimiento: +25% en pedidos este mes"
   - "Los clientes que compran vino tinto también suelen comprar quesos (cross-sell opportunity)"

**Consideraciones Multi-tenant:**
- Cada negocio ve solo sus propios reportes.
- Los gerentes pueden comparar entre sedes de su mismo negocio.

**Seguridad:**
- Requiere permiso: `reportes_pedidos_ver`
- Los datos financieros son sensibles, solo para administración.
- Los reportes de repartidores individuales requieren permiso adicional.

**UX:**
- Dashboard visual atractivo como página de inicio.
- Gráficos interactivos con tooltips.
- Descarga de reportes con un clic.
- Selector de período tipo daterangepicker.
- Modo presentación para reuniones gerenciales.

**Reglas de Negocio:**
- Los reportes históricos se conservan indefinidamente (o según política de retención).
- Los datos se actualizan en tiempo real (o con delay de 5 minutos máximo).
- Los reportes financieros deben cuadrar con la contabilidad.
- Los insights de IA requieren mínimo 30 días de datos históricos para ser significativos.

---
Perfecto, continuaré con el **Submódulo 6.4.4: Gestión de Clientes**.

---

## **Submódulo 6.4.4: Gestión de Clientes**

---

### **RF-VTA-028: Registrar y Gestionar Base de Clientes**

**Descripción:**  
El sistema debe proporcionar un módulo completo de CRM (Customer Relationship Management) para registrar, almacenar y gestionar información de clientes, incluyendo datos personales, historial de compras, preferencias, y segmentación para marketing personalizado.

**Criterios de Aceptación:**
1. Registro de clientes:
   - **Captura inicial** (en el primer contacto):
     - Durante una venta en POS: Solicitar DNI/nombre/teléfono
     - Durante un pedido online: Formulario de registro
     - Importación masiva desde Excel/CSV
   - **Información básica** (obligatoria):
     - Nombre completo
     - Tipo de documento (DNI, RUC, Pasaporte, Carnet de Extranjería)
     - Número de documento (único)
     - Teléfono (validación de formato)
     - Email (validación de formato)
   - **Información adicional** (opcional):
     - Fecha de nacimiento (para promociones de cumpleaños)
     - Género
     - Dirección principal
     - Direcciones adicionales (delivery)
     - Empresa/negocio (si es cliente corporativo)
     - Cargo/posición
     - Cómo nos conoció (referencia, redes sociales, publicidad, etc.)
   - **Preferencias**:
     - Acepta marketing por email: Sí/No
     - Acepta marketing por SMS: Sí/No
     - Acepta marketing por WhatsApp: Sí/No
     - Productos favoritos (se llena automáticamente con compras)
     - Alergias o restricciones
2. Validación de datos:
   - DNI: 8 dígitos (Perú)
   - RUC: 11 dígitos, validar con API de SUNAT
   - Email: Formato válido (regex)
   - Teléfono: 9 dígitos con código de país
   - No duplicados: Validar que DNI/email no existan previamente
3. Enriquecimiento automático de datos:
   - **Integración con SUNAT** (para RUC):
     - Auto-completar razón social, dirección fiscal, estado
   - **Integración con RENIEC** (si disponible):
     - Validar DNI y obtener nombres
   - **Geolocalización**:
     - Validar direcciones con Google Maps
     - Guardar coordenadas para delivery
4. Ficha completa del cliente:
   - **Información personal**: Todos los datos capturados
   - **Historial de compras**:
     - Fecha de primera compra
     - Fecha de última compra
     - Total de compras (cantidad)
     - Total gastado (monto acumulado)
     - Ticket promedio
     - Frecuencia de compra (cada X días)
   - **Productos comprados**: Lista de todos los productos que ha pedido
   - **Preferencias identificadas**:
     - Modalidad preferida (delivery, pickup, mesa)
     - Horario frecuente de pedidos
     - Método de pago preferido
     - Categorías de productos más comprados
   - **Segmentos** (etiquetas automáticas):
     - Cliente nuevo (< 30 días desde primera compra)
     - Cliente frecuente (> 5 compras)
     - Cliente VIP (top 10% en valor de vida)
     - Cliente inactivo (> 90 días sin comprar)
     - Cliente en riesgo (frecuencia decreciente)
   - **Interacciones**:
     - Llamadas telefónicas registradas
     - Emails enviados
     - Quejas o reclamos
     - Calificaciones dejadas
   - **Notas internas**: Observaciones del personal (ej: "Cliente exigente", "Siempre pide sin cebolla")
5. Gestión de direcciones:
   - Un cliente puede tener múltiples direcciones guardadas
   - Cada dirección tiene:
     - Alias (ej: "Casa", "Oficina", "Casa de mi mamá")
     - Calle y número
     - Distrito/zona
     - Referencia
     - Coordenadas GPS
     - Marcar como dirección principal
6. Búsqueda de clientes:
   - Por DNI/RUC
   - Por nombre (búsqueda parcial)
   - Por teléfono
   - Por email
   - Búsqueda avanzada: Por segmento, por fecha de registro, por valor total

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propia base de clientes (no compartida).
- Un cliente real puede estar registrado en múltiples negocios de forma independiente.

**Seguridad:**
- Requiere permiso: `clientes_gestionar` (crear/editar), `clientes_ver` (solo lectura)
- Los datos personales están protegidos (GDPR/Ley de Protección de Datos).
- Solo personal autorizado puede ver información sensible.
- Auditoría de quién accede a qué datos de clientes.

**UX:**
- Autocompletado al buscar por DNI o nombre.
- Vista de tarjeta con información resumida.
- Ficha completa con tabs: Información | Compras | Direcciones | Notas.
- Importación masiva con plantilla Excel predefinida.
- Validación en línea mientras el usuario escribe.

**Reglas de Negocio:**
- El DNI/RUC es único por negocio (no puede haber duplicados).
- Los clientes inactivos > 1 año se marcan como "dormidos" pero no se eliminan.
- El historial de compras nunca se elimina (requerimiento legal y de negocio).
- Los datos de contacto (email, teléfono) se validan antes de enviar comunicaciones masivas.

---

### **RF-VTA-029: Implementar Programa de Fidelización**

**Descripción:**  
El sistema debe proporcionar un programa de puntos de fidelización donde los clientes acumulan puntos por cada compra y pueden canjearlos por descuentos, productos gratis, o beneficios especiales, incentivando la lealtad y compras recurrentes.

**Criterios de Aceptación:**
1. Configuración del programa:
   - **Acumulación de puntos**:
     - Regla base: X puntos por cada $1 USD gastado (ej: 1 punto por $1)
     - Regla por categoría: Bebidas alcohólicas: 2 puntos por $1, snacks: 1 punto por $1
     - Multiplicadores especiales:
       - Cumpleaños del cliente: Puntos dobles
       - Días especiales (Black Friday): Puntos triples
       - Primera compra: Bonus de 100 puntos de bienvenida
   - **Expiración de puntos**:
     - Puntos válidos por X meses (ej: 12 meses)
     - Los puntos próximos a vencer generan alerta al cliente
   - **Niveles de membresía** (opcional):
     - Bronce: 0-999 puntos acumulados (beneficios básicos)
     - Plata: 1000-4999 puntos (5% descuento adicional)
     - Oro: 5000-9999 puntos (10% descuento + delivery gratis)
     - Platino: 10000+ puntos (15% descuento + acceso VIP + regalos exclusivos)
   - **Canje de puntos**:
     - Tabla de canje:
       - 100 puntos = $5 USD de descuento
       - 500 puntos = Producto específico gratis (ej: Cerveza)
       - 1000 puntos = Delivery gratis por un mes
       - 2000 puntos = Cóctel premium gratis
2. Acumulación automática:
   - En cada venta completada:
     - El sistema calcula puntos según el total gastado
     - Aplica multiplicadores si corresponde
     - Suma los puntos a la cuenta del cliente
     - Muestra en el ticket: "Has ganado X puntos. Total acumulado: Y puntos"
     - Envía notificación: "¡Felicidades! Ganaste 50 puntos 🎉"
3. Consulta de puntos:
   - **Para el cliente**:
     - En página de seguimiento de pedido
     - En app móvil (si existe)
     - Consultando en tienda (POS)
     - Vía WhatsApp: "Escribe PUNTOS y tu DNI"
   - **Información mostrada**:
     - Puntos disponibles
     - Puntos por vencer (fecha de expiración)
     - Nivel de membresía actual
     - Puntos para siguiente nivel
     - Historial de acumulación y canjes
4. Canje de puntos:
   - **En POS**:
     - Al procesar venta, cajero pregunta: "¿Desea canjear puntos?"
     - Cliente elige opción de canje
     - Se descuentan los puntos y se aplica beneficio
     - El descuento se refleja en el ticket
   - **En pedido online**:
     - Durante checkout, sección "Canjear Puntos"
     - Mostrar opciones disponibles según puntos actuales
     - Seleccionar y aplicar automáticamente
   - Validaciones:
     - El cliente tiene suficientes puntos
     - Los puntos no están vencidos
     - El beneficio aplica al pedido actual (ej: delivery gratis solo si es delivery)
5. Notificaciones del programa:
   - "¡Felicidades! Subiste a nivel Plata 🥈"
   - "Tienes 500 puntos próximos a vencer el 15/02. ¡Úsalos pronto!"
   - "Estás a 200 puntos de nivel Oro. ¡Sigue comprando!"
   - "Cumpleaños feliz 🎂 Hoy ganas puntos dobles"
6. Reportes del programa:
   - **Para el negocio**:
     - Total de clientes inscritos en el programa
     - Puntos emitidos vs. puntos canjeados
     - Pasivo por puntos (valor económico de puntos pendientes)
     - Tasa de canje (% de puntos que se usan)
     - ROI del programa (ventas incrementales vs. costo de beneficios)
   - **Análisis de efectividad**:
     - Clientes con programa vs. sin programa (frecuencia de compra)
     - Impacto en ticket promedio
     - Tasa de retención

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio programa de fidelización independiente.
- Los puntos no se transfieren entre negocios.

**Seguridad:**
- Los puntos son activos del cliente, no pueden eliminarse sin justificación.
- Los ajustes manuales de puntos requieren permiso especial y se auditan.
- Prevención de fraude: Validar que las compras son legítimas antes de otorgar puntos.

**UX:**
- Badge de "Ganaste X puntos" en confirmación de venta.
- Barra de progreso visual: "Estás a 30% de nivel Oro".
- Catálogo de canjes atractivo con imágenes.
- Notificaciones gamificadas: "🏆 Logro desbloqueado: Nivel Plata".

**Reglas de Negocio:**
- Los puntos solo se otorgan en ventas pagadas completamente (no en crédito pendiente).
- Las devoluciones/cancelaciones revierten los puntos otorgados.
- Los puntos vencidos se eliminan automáticamente y no son recuperables.
- El valor económico de los puntos debe estar cubierto por el margen de las ventas (sostenibilidad del programa).

---

### **RF-VTA-030: Segmentar Clientes para Marketing Dirigido**

**Descripción:**  
El sistema debe permitir crear segmentos de clientes basados en criterios múltiples (comportamiento, demográficos, valor, preferencias) para ejecutar campañas de marketing personalizadas, aumentando la efectividad de las comunicaciones y el ROI de marketing.

**Criterios de Aceptación:**
1. Criterios de segmentación disponibles:
   - **Demográficos**:
     - Edad (rango)
     - Género
     - Ubicación (distrito, zona)
     - Tipo de cliente (persona natural, empresa)
   - **Comportamiento de compra**:
     - Frecuencia: Clientes frecuentes (> X compras/mes), ocasionales, únicos
     - Recencia: Compraron en últimos 7, 30, 90 días o > 90 días (inactivos)
     - Valor: Alto valor (top 10%), medio, bajo
     - Modalidad preferida: Solo delivery, solo pickup, solo en mesa
   - **Preferencias de producto**:
     - Compran bebidas alcohólicas
     - Compran solo cervezas
     - Compran vinos premium
     - Compran snacks/acompañamientos
   - **Etapa del ciclo de vida**:
     - Nuevos (< 30 días desde primera compra)
     - Activos (compraron en últimos 30 días)
     - En riesgo (compraban frecuente, ahora no)
     - Inactivos (> 90 días sin comprar)
     - Perdidos (> 180 días sin comprar)
   - **Programa de fidelización**:
     - Con/sin puntos
     - Nivel de membresía (Bronce, Plata, Oro, Platino)
   - **Engagement**:
     - Abre emails (tasa de apertura alta)
     - Hace clic en promociones
     - Califica pedidos regularmente
     - Responde a encuestas
2. Creación de segmentos:
   - **Segmentos predefinidos** (automáticos):
     - "Clientes VIP" (top 10% en valor)
     - "Nuevos clientes" (primera compra en últimos 30 días)
     - "Cumpleañeros del mes"
     - "Inactivos" (> 90 días sin comprar)
     - "Amantes del vino" (> 60% de compras son vinos)
   - **Segmentos personalizados**:
     - Nombre del segmento (ej: "Cerveceros del viernes")
     - Descripción
     - Criterios múltiples con operadores:
       - AND: Deben cumplir todos los criterios
       - OR: Deben cumplir al menos uno
     - Ejemplo: "Clientes que compraron cervezas en últimos 7 días AND gastaron > $50 USD AND viven en Zona Norte"
   - **Segmentos dinámicos**:
     - Se actualizan automáticamente cuando clientes cumplen/dejan de cumplir criterios
     - Ejemplo: "Cumpleañeros del mes" se actualiza cada mes
3. Vista de segmento:
   - Número de clientes en el segmento
   - Lista de clientes con información básica
   - Estadísticas del segmento:
     - Valor total del segmento ($)
     - Ticket promedio
     - Frecuencia de compra promedio
     - Productos más comprados
   - Exportación de lista (Excel, CSV)
4. Acciones sobre segmentos:
   - **Enviar campaña de marketing**:
     - Email masivo con plantilla personalizada
     - SMS masivo
     - Notificaciones push (si tienen app)
     - WhatsApp Business (mensajes masivos)
   - **Crear promoción exclusiva**:
     - Cupón único para el segmento
     - Ejemplo: "VIERNES20" solo para clientes del segmento "Cerveceros del viernes"
   - **Exportar para análisis externo**:
     - Descargar lista para importar en herramientas de email marketing (Mailchimp, etc.)
5. Análisis de efectividad:
   - **Por segmento**:
     - Tasa de respuesta a campañas
     - Conversión (% que compró después de la campaña)
     - ROI (ingresos generados vs. costo de la campaña)
   - **Comparativa**:
     - Segmento A vs. Segmento B (A/B testing)
     - Clientes en segmento vs. fuera del segmento
6. Casos de uso ejemplo:
   - **Reactivación de inactivos**:
     - Segmento: Clientes que no compran hace > 90 días
     - Acción: Email con cupón 20% OFF "Te extrañamos"
   - **Upsell de vinos**:
     - Segmento: Clientes que compran vinos pero ticket < $30
     - Acción: Promoción "Compra 2 vinos premium, lleva 10% OFF"
   - **Fidelización de VIPs**:
     - Segmento: Top 10% en valor
     - Acción: Invitación a evento exclusivo de degustación

**Consideraciones Multi-tenant:**
- Cada negocio crea y gestiona sus propios segmentos.
- Los datos de clientes no se comparten entre negocios.

**Seguridad:**
- Requiere permiso: `marketing_segmentos_gestionar`
- Los envíos masivos requieren que los clientes hayan dado consentimiento (opt-in).
- Cumplimiento con leyes anti-spam y protección de datos.

**UX:**
- Constructor de segmentos tipo "query builder" con dropdowns.
- Vista previa: "Este segmento incluye 234 clientes".
- Plantillas de segmentos predefinidos para uso rápido.
- Visualización de segmentos en gráficos (tamaño, valor, etc.).

**Reglas de Negocio:**
- Los clientes que no aceptaron marketing no pueden incluirse en segmentos para campañas (respeto al opt-out).
- Los segmentos dinámicos se recalculan diariamente (job nocturno).
- Las campañas masivas tienen límites de envío (ej: máximo 10,000 emails/día según plan de email marketing).
- Los segmentos se pueden archivar pero no eliminar (mantener historial de campañas).

---

### **RF-VTA-031: Gestionar Crédito a Clientes Corporativos**

**Descripción:**  
El sistema debe permitir otorgar líneas de crédito a clientes corporativos o frecuentes confiables, permitiéndoles realizar compras a cuenta y pagar posteriormente, con control de límites de crédito, plazos, y gestión de cobranza.

**Criterios de Aceptación:**
1. Configuración de línea de crédito:
   - El administrador accede a la ficha del cliente y configura:
     - **Línea de crédito**: Monto máximo que puede deber (ej: $1000 USD)
     - **Plazo de pago**: Días para pagar (ej: 15 días, 30 días, 60 días)
     - **Método de pago preferido**: Transferencia, cheque, efectivo
     - **Día de corte**: Día del mes para cierre de cuenta (ej: día 25)
     - **Contacto de facturación**: Persona responsable de pagos
     - **Estado del crédito**: Activo, Suspendido, Bloqueado
   - Requiere aprobación de gerente (workflow)
   - Se adjuntan documentos de respaldo:
     - RUC y ficha registral (empresas)
     - Carta de solicitud de crédito
     - Estados financieros (opcionales)
     - Referencias comerciales
2. Validación al realizar venta a crédito:
   - Al procesar venta en POS o pedido:
     - Seleccionar forma de pago: "Crédito"
     - Buscar cliente por RUC/nombre
     - El sistema valida:
       - Cliente tiene línea de crédito activa
       - Crédito disponible >= Total de venta
       - No tiene facturas vencidas (configurable si se permite)
     - Si todo OK: Procede con la venta
     - Si NO: "Crédito insuficiente. Disponible: $X. Requerido: $Y"
3. Registro de la deuda:
   - Al confirmar la venta a crédito:
     - Se genera la factura normalmente (obligatorio para crédito corporativo)
     - Se crea registro en `cuenta_por_cobrar`:
       - Cliente
       - Factura asociada
       - Monto total
       - Fecha de venta
       - Fecha de vencimiento (fecha venta + plazo configurado)
       - Estado: `'pendiente'`
     - Se descuenta del crédito disponible
     - Fórmula: Crédito disponible = Línea de crédito - Deuda pendiente
4. Vista de estado de cuenta del cliente:
   - **Información del crédito**:
     - Línea de crédito total: $1000 USD
     - Crédito utilizado: $600 USD
     - Crédito disponible: $400 USD
     - Barra de progreso visual (60% utilizado)
   - **Facturas pendientes** (tabla):
     - Número de factura
     - Fecha de emisión
     - Monto
     - Fecha de vencimiento
     - Días para vencer / Días vencidos
     - Estado: Vigente (verde), Por vencer < 5 días (amarillo), Vencida (rojo)
   - **Historial de pagos**:
     - Fecha de pago
     - Factura(s) pagada(s)
     - Monto pagado
     - Método de pago
     - Comprobante de pago
5. Registro de pago:
   - El administrador accede a "Registrar Pago de Cliente":
     - Seleccionar cliente
     - Ingresar:
       - Monto del pago
       - Fecha de pago
       - Método de pago
       - Número de operación/cheque/transferencia
       - Comprobante de pago adjunto (foto/scan)
     - Seleccionar qué facturas pagar:
       - **Automático**: Paga facturas más antiguas primero (FIFO)
       - **Manual**: El usuario selecciona qué facturas liquidar
     - Confirmar pago:
       - Se marcan facturas como `'pagada'`
       - Se libera el crédito utilizado
       - Se genera recibo de pago
       - Se notifica al cliente
6. Gestión de cobranza:
   - **Alertas automáticas**:
     - 5 días antes de vencimiento: Recordatorio al cliente
     - Día de vencimiento: "Hoy vence su factura #X"
     - 1 día después de vencimiento: "Tiene una factura vencida"
     - 7 días vencido: Alerta a gerente de cobranza
     - 15 días vencido: Suspender crédito automáticamente
     - 30 días vencido: Bloquear crédito, proceso de cobranza judicial
   - **Estados de morosidad**:
     - Al día (0 días vencidos)
     - Leve (1-7 días vencidos)
     - Moderada (8-15 días vencidos)
     - Severa (16-30 días vencidos)
     - Crítica (> 30 días vencidos)
   - **Acciones de cobranza**:
     - Llamada telefónica (registrar en sistema)
     - Email formal de cobranza
     - Visita presencial (para montos altos)
     - Suspensión de crédito
     - Reporte a centrales de riesgo (casos extremos)
7. Reportes de crédito:
   - **Estado de Cartera**:
     - Total por cobrar ($)
     - Por cobrar vigente vs. vencido
     - Aging report: 0-30 días, 31-60, 61-90, > 90 días
   - **Clientes con Mayor Deuda**: Top 10
   - **Clientes Morosos**: Lista de deudores
   - **Eficiencia de Cobranza**: % de facturas pagadas a tiempo
   - **Provisión por Incobrables**: Estimación de pérdida por deudas no recuperables

**Consideraciones Multi-tenant:**
- Las líneas de crédito son por negocio.
- Cada negocio gestiona su propia cartera de cobranza.

**Seguridad:**
- Requiere permiso: `credito_gestionar` (otorgar crédito), `cobranza_gestionar` (cobrar)
- Solo gerentes/administración pueden aprobar líneas de crédito.
- Los ajustes de deuda requieren justificación y aprobación.
- Auditoría completa de otorgamiento, ventas a crédito, y pagos.

**UX:**
- Indicador visual de crédito en ficha del cliente (barra de progreso).
- Alertas de facturas vencidas en dashboard principal.
- Filtrado de clientes con deuda vencida (color coding).
- Generación automática de cartas de cobranza con plantillas.

**Reglas de Negocio:**
- El crédito es solo para clientes corporativos con RUC (no personas naturales, salvo excepciones aprobadas).
- Las ventas a crédito siempre generan factura (no boleta).
- Los pagos parciales se aceptan y se aplican a facturas más antiguas.
- El crédito se suspende automáticamente si hay > 15 días de mora.
- Los intereses por mora son configurables y se calculan automáticamente (si aplica según política).

---

### **RF-VTA-032: Analizar Valor de Vida del Cliente (LTV)**

**Descripción:**  
El sistema debe calcular el Valor de Vida del Cliente (Customer Lifetime Value - LTV) para identificar a los clientes más valiosos, predecir ingresos futuros, y optimizar inversiones en adquisición y retención de clientes.

**Criterios de Aceptación:**
1. Cálculo del LTV:
   - **LTV Histórico** (basado en datos pasados):
     - Fórmula básica: LTV = Valor Promedio de Compra × Frecuencia de Compra × Tiempo de Vida del Cliente
     - Componentes:
       - **Valor promedio de compra**: Total gastado / Número de compras
       - **Frecuencia de compra**: Número de compras / Meses activo
       - **Tiempo de vida**: Meses desde primera compra hasta última compra (o hasta hoy si sigue activo)
     - Ejemplo: Cliente con ticket promedio $50, compra 2 veces/mes, activo 12 meses = LTV = $50 × 2 × 12 = $1,200
   - **LTV Predictivo** (proyección a futuro):
     - Considera tendencias: ¿La frecuencia está aumentando o disminuyendo?
     - Fórmula avanzada: LTV = (Valor promedio × Frecuencia × Margen de ganancia) / Tasa de abandono
     - Ejemplo: Si la tasa de abandono es 5% mensual, el LTV predictivo ajusta el tiempo de vida esperado
2. Segmentación por LTV:
   - **Clientes de Alto Valor**: LTV > $1,000 USD (top 10%)
   - **Clientes de Valor Medio**: LTV entre $300-$1,000
   - **Clientes de Bajo Valor**: LTV < $300
   - **Clientes Nuevos**: Sin suficiente historial para calcular LTV (< 3 compras)
3. Vista de LTV en ficha del cliente:
   - **Métricas de LTV**:
     - LTV histórico: $1,250 USD
     - LTV proyectado: $2,500 USD (si continúa comprando al mismo ritmo)
     - Ranking: "Top 5% de clientes más valiosos"
   - **Componentes del cálculo**:
     - Primera compra: 15/05/2024
     - Última compra: 28/01/2026
     - Meses activo: 20 meses
     - Total de compras: 38
     - Total gastado: $1,250
     - Ticket promedio: $32.89
     - Frecuencia: 1.9 compras/mes
   - **Gráfico de evolución**:
     - Línea temporal mostrando gasto acumulado mes a mes
     - Identificar tendencia: Creciente, estable, decreciente
4. Reportes de LTV:
   - **Dashboard de LTV**:
     - LTV promedio de todos los clientes
     - LTV total del negocio (suma de todos)
     - Distribución de clientes por rango de LTV (gráfico de barras)
   - **Top Clientes por LTV**:
     - Lista de top 50 clientes más valiosos
     - Información: Nombre, LTV, frecuencia, última compra
     - Recomendaciones: "Estos son tus clientes VIP, ¡cuídalos!"
   - **Análisis de Cohortes**:
     - Agrupar clientes por mes de primera compra
     - Comparar LTV entre cohortes
     - Identificar: "Los clientes que llegaron en Diciembre 2025 tienen LTV 20% mayor que promedio"
   - **Predicción de Abandono** (Churn):
     - Lista de clientes de alto LTV con riesgo de abandono:
       - Alta frecuencia histórica pero no compran hace > 30 días
       - Frecuencia decreciente en últimos 3 meses
     - Acción sugerida: "Contactar para evitar pérdida"
5. Análisis de inversión en adquisición:
   - **CAC (Customer Acquisition Cost)** vs. LTV:
     - Si se registra el costo de adquisición por canal:
       - Anuncios Facebook: CAC = $10 USD
       - Referidos: CAC = $2 USD
     - Comparar: LTV / CAC (ratio ideal: > 3)
     - Ejemplo: Si LTV = $500 y CAC = $10, ratio = 50 (excelente)
   - **Recomendaciones automáticas**:
     - "Invertir más en canal X porque tiene mejor ratio LTV/CAC"
     - "Reducir inversión en canal Y porque el LTV de sus clientes es bajo"
6. Estrategias basadas en LTV:
   - **Clientes de Alto LTV**:
     - Tratamiento VIP
     - Atención personalizada
     - Promociones exclusivas
     - Programa de referidos (pueden traer más clientes valiosos)
   - **Clientes de Valor Medio**:
     - Estrategias de upsell
     - Aumentar frecuencia de compra
     - Programa de fidelización
   - **Clientes de Bajo LTV**:
     - Evaluar si vale la pena retenerlos
     - Automatizar comunicación (bajo costo)
     - No invertir recursos altos en retención

**Consideraciones Multi-tenant:**
- El LTV se calcula por negocio independientemente.
- Los promedios son específicos de cada tipo de negocio.

**Seguridad:**
- Los datos de LTV son estratégicos, solo accesibles para gerencia.
- Requiere permiso: `analytics_ltv_ver`

**UX:**
- Dashboard visual con gráficos interactivos.
- Código de colores: Verde (alto LTV), amarillo (medio), rojo (bajo).
- Alertas proactivas: "3 clientes VIP no compran hace 30 días".
- Exportación de reportes a PDF/Excel.

**Reglas de Negocio:**
- El LTV solo es significativo para clientes con > 3 compras (mínimo de datos).
- El LTV se recalcula semanalmente (job programado).
- Los clientes inactivos > 180 días tienen LTV proyectado = 0 (se considera perdidos).
- El margen de ganancia usado en el cálculo es configurable por negocio (ej: 30% sobre ventas).

---

### **RF-VTA-033: Gestionar Quejas y Reclamos**

**Descripción:**  
El sistema debe proporcionar un módulo de atención al cliente para registrar, rastrear, y resolver quejas y reclamos de manera estructurada, mejorando la satisfacción del cliente y identificando problemas recurrentes para mejora continua.

**Criterios de Aceptación:**
1. Canales de recepción de quejas:
   - **Presencial**: Cliente va a la sede y se registra en el sistema
   - **Telefónica**: Agente registra durante la llamada
   - **Email**: Se importa automáticamente desde email de atención
   - **WhatsApp**: Se registra desde conversación
   - **Formulario web**: Cliente llena formulario online
   - **Redes sociales**: Se registra manualmente desde comentarios/mensajes
2. Registro de queja/reclamo:
   - **Información básica**:
     - Cliente (búsqueda por DNI/nombre/teléfono)
     - Canal de recepción
     - Fecha y hora
     - Agente que registra
   - **Detalles del reclamo**:
     - Tipo (dropdown):
       - Producto defectuoso
       - Demora en entrega
       - Mala atención
       - Error en el pedido
       - Problema de facturación
       - Problema con repartidor
       - Otro (especificar)
     - Descripción detallada (texto libre)
     - Pedido relacionado (si aplica): Vincular con número de pedido
     - Nivel de urgencia: Baja, Media, Alta, Crítica
     - Evidencia: Subir fotos, documentos, capturas
   - **Expectativa del cliente**:
     - Reembolso
     - Cambio de producto
     - Disculpa formal
     - Compensación
     - Solo quiere que se resuelva el problema
3. Asignación y gestión:
   - **Asignación automática**:
     - Según tipo de reclamo:
       - Productos: A supervisor de almacén
       - Delivery: A supervisor de repartidores
       - Facturación: A contador
       - Atención: A gerente de servicio
   - **Asignación manual**: El supervisor asigna a agente específico
   - Estados del reclamo:
     - **Nuevo**: Recién registrado
     - **En Revisión**: Agente está investigando
     - **Pendiente de Cliente**: Esperando respuesta/información del cliente
     - **En Resolución**: Se está implementando la solución
     - **Resuelto**: Problema solucionado, esperando confirmación del cliente
     - **Cerrado**: Cliente confirmó satisfacción
     - **Escalado**: Se elevó a gerencia/legal por complejidad
4. Seguimiento y resolución:
   - **Timeline de acciones**:
     - Cada actualización se registra con fecha/hora/usuario
     - "10:30 AM - Juan: Se contactó al cliente vía telefónica"
     - "11:15 AM - María: Se aprobó reembolso de $50"
     - "2:00 PM - Sistema: Se procesó reembolso"
   - **Comunicación con el cliente**:
     - Enviar actualizaciones por email/SMS
     - Plantillas de mensajes predefinidas
     - "Estimado cliente, estamos revisando su caso. Le responderemos en 24 horas"
   - **Acciones de resolución**:
     - Emitir nota de crédito
     - Procesar reembolso
     - Enviar producto de reemplazo (genera orden automática)
     - Ofrecer cupón de descuento
     - Disculpa formal del gerente
5. SLA (Service Level Agreement):
   - **Tiempos de respuesta configurables**:
     - Urgencia Alta: Primera respuesta en 2 horas, resolución en 24 horas
     - Urgencia Media: Primera respuesta en 24 horas, resolución en 72 horas
     - Urgencia Baja: Primera respuesta en 48 horas, resolución en 5 días
   - **Alertas automáticas**:
     - Si se acerca el tiempo límite: Alerta amarilla
     - Si se pasa el tiempo límite: Alerta roja al supervisor
   - **Métricas de cumplimiento**:
     - % de reclamos resueltos dentro del SLA
     - Tiempo promedio de resolución
6. Cierre y seguimiento:
   - Cuando se marca como "Resuelto":
     - Se envía encuesta de satisfacción al cliente:
       - "¿Quedó satisfecho con la resolución? Sí/No"
       - "Califique la atención recibida (1-5 estrellas)"
       - Comentarios adicionales
   - Si el cliente confirma: Estado → "Cerrado"
   - Si el cliente no está satisfecho: Estado → "Reabrir" y se reasigna
7. Reportes y análisis:
   - **Dashboard de Reclamos**:
     - Total de reclamos abiertos
     - % de reclamos resueltos dentro del SLA
     - Tiempo promedio de resolución
     - Reclamos por tipo (gráfico de pastel)
   - **Análisis de causas raíz**:
     - Top 5 tipos de reclamos
     - Productos con más quejas
     - Repartidores con más quejas
     - Días/horarios con más incidencias
   - **Tendencias**:
     - Evolución de reclamos en el tiempo
     - Comparativa mes a mes
     - Identificar picos anómalos
   - **Impacto económico**:
     - Costo total de compensaciones/reembolsos
     - Clientes perdidos por mala experiencia

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus propios reclamos.
- Los agentes solo ven reclamos de su negocio.

**Seguridad:**
- Requiere permiso: `reclamos_gestionar`
- Los datos de reclamos son sensibles (pueden usarse en disputas legales).
- Auditoría completa de todas las acciones.

**UX:**
- Vista tipo ticket system (similar a Zendesk).
- Código de colores por urgencia.
- Filtros: Por estado, tipo, fecha, agente.
- Notificaciones push cuando se asigna nuevo reclamo.
- Vista de cliente: "Mis reclamos" con estado de cada uno.

**Reglas de Negocio:**
- Todos los reclamos deben tener resolución dentro de 7 días hábiles (máximo legal en muchos países).
- Los reclamos resueltos se archivan después de 30 días (no se eliminan, solo se ocultan de vista activa).
- Las compensaciones > $100 USD requieren aprobación de gerente.
- Los reclamos reincidentes del mismo cliente generan alerta para evaluar el caso en profundidad.

---

### **RF-VTA-034: Implementar Sistema de Referidos**

**Descripción:**  
El sistema debe proporcionar un programa de referidos donde los clientes existentes pueden invitar a nuevos clientes a cambio de recompensas (descuentos, puntos, productos gratis), incentivando el crecimiento orgánico y reduciendo el costo de adquisición.

**Criterios de Aceptación:**
1. Configuración del programa:
   - **Recompensas para el referidor** (cliente que invita):
     - Cupón de descuento: $10 USD o 20% OFF
     - Puntos de fidelización: 500 puntos
     - Producto gratis: Una cerveza
     - Descuento en próximo pedido
   - **Recompensas para el referido** (nuevo cliente):
     - Cupón de bienvenida: 15% OFF en primera compra
     - Delivery gratis en primer pedido
     - Producto de cortesía
   - **Condiciones de validez**:
     - El referido debe realizar mínimo una compra > $X USD
     - La recompensa se otorga después de la primera compra exitosa del referido
     - Cada cliente puede referir hasta N personas por mes (ej: 10)
2. Generación de código de referido:
   - Cada cliente tiene un código único:
     - Generado automáticamente al registrarse (ej: JUAN2024XY)
     - O personalizado (si está disponible): JUANPEREZ
   - El cliente puede compartir:
     - Código alfanumérico
     - Link único: `https://mitienda.com/ref/JUAN2024XY`
     - QR code para escanear
3. Compartir referido:
   - **Desde la app/web del cliente**:
     - Sección "Invita y Gana"
     - Botones para compartir:
       - WhatsApp: "¡Prueba esta licorería! Usa mi código JUAN2024XY y recibe 15% OFF"
       - Facebook / Instagram
       - Email
       - Copiar link
     - Vista de "Mis Referidos": Lista de personas que usaron su código
4. Uso del código de referido:
   - **Durante el registro de nuevo cliente**:
     - Campo opcional: "¿Tienes un código de referido?"
     - Ingresar código o escanear QR
     - Validar: El código existe y está activo
     - Aplicar: Se vincula el nuevo cliente con el referidor
     - Mensaje: "¡Bienvenido! Has recibido 15% OFF en tu primera compra"
   - **Durante la primera compra**:
     - Se aplica automáticamente el descuento del cupón de referido
     - Se valida que es la primera compra del cliente
5. Activación de recompensas:
   - **Cuando el referido completa su primera compra**:
     - El sistema verifica:
       - Primera compra completada y pagada
       - Monto >= mínimo requerido
       - No es compra fraudulenta
     - Se activan las recompensas:
       - Referidor recibe notificación: "¡Tu amigo Juan compró! Ganaste $10 USD de descuento"
       - Se crea cupón o se agregan puntos a la cuenta del referidor
       - Referido recibe confirmación de su beneficio aplicado
6. Seguimiento y análisis:
   - **Panel del referidor** (vista del cliente):
     - Total de referidos: 8 personas
     - Referidos activos (que compraron): 5
     - Referidos pendientes (registrados pero no compraron): 3
     - Recompensas ganadas: $50 USD acumulado
     - Ranking: "Estás en top 20% de referidores"
   - **Reportes del negocio**:
     - Total de clientes adquiridos por referidos
     - Tasa de conversión de referidos (% que compran)
     - CAC (Costo de Adquisición) por referidos (muy bajo comparado con publicidad)
     - ROI del programa (ventas generadas vs. costo de recompensas)
     - Top referidores: Clientes que más refieren
7. Gamificación:
   - **Niveles de referidor**:
     - Bronce: 1-4 referidos → Recompensa estándar
     - Plata: 5-9 referidos → Recompensa +50%
     - Oro: 10-19 referidos → Recompensa doble
     - Platino: 20+ referidos → Beneficios exclusivos + comisión del 5% en compras de sus referidos
   - **Tabla de líderes**:
     - "Top 10 Referidores del Mes" con premios especiales
     - Reconocimiento público (con permiso del cliente)
   - **Desafíos especiales**:
     - "Refiere 3 amigos en Febrero y gana una caja de vinos"

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio programa de referidos independiente.
- Los códigos de referido solo funcionan dentro del mismo negocio.

**Seguridad:**
- Prevención de fraude:
   - Validar que el referido es una persona diferente (email, teléfono, dirección únicos)
   - No permitir auto-referidos
   - Validar que las compras son legítimas (no crear cuentas falsas)
- Los códigos de referido no tienen valor monetario directo (solo descuentos/puntos).
- Auditoría de todos los referidos y recompensas otorgadas.

**UX:**
- Sección destacada "Invita y Gana" en el perfil del cliente.
- Botones de compartir en redes sociales integrados.
- Visualización del progreso: "Referiste a 3 de 5 amigos para alcanzar nivel Plata".
- Notificaciones celebratorias: "🎉 ¡Tu amigo compró! Ganaste $10".

**Reglas de Negocio:**
- Las recompensas se otorgan solo después de la primera compra exitosa del referido (evitar spam).
- Los cupones de referido tienen fecha de expiración (ej: 30 días desde el registro).
- Un cliente no puede usar su propio código de referido.
- Las recompensas tienen un costo para el negocio, pero el CAC es mucho menor que publicidad pagada.
- El programa puede pausarse temporalmente si el costo de recompensas supera el presupuesto.

---

### **RF-VTA-035: Analizar Comportamiento y Preferencias de Clientes**

**Descripción:**  
El sistema debe proporcionar análisis avanzado del comportamiento de compra y preferencias de los clientes, utilizando técnicas de análisis de datos para identificar patrones, predecir comportamientos futuros, y personalizar la experiencia de compra.

**Criterios de Aceptación:**
1. Análisis de patrones de compra:
   - **Por cliente individual**:
     - Día de la semana preferido para comprar (ej: Viernes)
     - Horario preferido (ej: 7-9 PM)
     - Frecuencia de compra (cada X días)
     - Modalidad preferida (delivery 80%, pickup 15%, mesa 5%)
     - Método de pago preferido (tarjeta 60%, efectivo 40%)
     - Ticket promedio: $45 USD
   - **Productos y categorías favoritos**:
     - Top 5 productos más comprados por el cliente
     - Categorías preferidas (ej: Cervezas 60%, Vinos 25%, Licores 15%)
     - Marcas favoritas
     - Productos nunca comprados (oportunidad de cross-sell)
   - **Sensibilidad al precio**:
     - Responde a descuentos: Sí/No (si compra más con promociones)
     - Rango de precios habitual: $30-$50
     - Dispuesto a pagar premium: Sí (compra productos caros ocasionalmente)
2. Análisis de asociación de productos (Market Basket Analysis):
   - **Productos frecuentemente comprados juntos**:
     - "Los clientes que compran Cerveza Corona también compran Limón" (80% de las veces)
     - "Whisky + Coca-Cola" (65%)
     - "Vino tinto + Queso gourmet" (55%)
   - **Reglas de asociación**:
     - Soporte: % de transacciones que incluyen ambos productos
     - Confianza: % de transacciones con A que también incluyen B
     - Lift: Cuánto más probable es comprar B si ya compraste A
   - **Aplicaciones**:
     - Recomendaciones: "Clientes que compraron esto también compraron..."
     - Bundling: Crear combos basados en asociaciones
     - Layout de tienda: Colocar productos relacionados cerca
3. Segmentación RFM (Recency, Frequency, Monetary):
   - **Recency** (Recencia): ¿Cuándo fue la última compra?
     - Muy reciente (< 7 días): 5 puntos
     - Reciente (7-30 días): 4 puntos
     - Moderado (31-90 días): 3 puntos
     - Antiguo (91-180 días): 2 puntos
     - Muy antiguo (> 180 días): 1 punto
   - **Frequency** (Frecuencia): ¿Cuántas veces compró?
     - Muy frecuente (> 20 compras): 5 puntos
     - Frecuente (11-20): 4 puntos
     - Moderado (6-10): 3 puntos
     - Ocasional (3-5): 2 puntos
     - Una sola vez: 1 punto
   - **Monetary** (Valor monetario): ¿Cuánto gastó en total?
     - Muy alto (> $1000): 5 puntos
     - Alto ($500-$1000): 4 puntos
     - Medio ($200-$499): 3 puntos
     - Bajo ($50-$199): 2 puntos
     - Muy bajo (< $50): 1 punto
   - **Score RFM**: Combinación de los 3 (ej: 555 = Cliente ideal, 111 = Cliente de bajo valor)
   - **Segmentos automáticos**:
     - Champions (555, 554, 544): Mejores clientes, compran frecuente y recientemente
     - Loyal (543, 444, 435): Clientes leales, compran regularmente
     - Potential Loyalists (553, 551): Buenos clientes recientes, potencial de fidelización
     - At Risk (244, 334): Buenos clientes pero hace tiempo que no compran
     - Can't Lose (155, 154): Clientes valiosos que dejaron de comprar
     - Lost (111, 112): Clientes perdidos
4. Predicciones con Machine Learning:
   - **Predicción de abandono (Churn)**:
     - Probabilidad de que un cliente no vuelva a comprar (0-100%)
     - Basado en: Disminución de frecuencia, tiempo sin comprar, cambios en ticket promedio
     - Lista de clientes en riesgo con % de probabilidad
   - **Predicción de próxima compra**:
     - "Este cliente probablemente comprará en los próximos 7 días"
     - Mejor momento para enviar oferta personalizada
   - **Recomendación de productos**:
     - Basado en historial de compras y comportamiento de clientes similares
     - "Recomendamos ofrecerle Whisky Johnnie Walker Black (85% probabilidad de compra)"
   - **Predicción de valor futuro**:
     - LTV proyectado para próximos 12 meses
5. Análisis de cohortes:
   - Agrupar clientes por mes de primera compra (cohorte)
   - Analizar comportamiento de cada cohorte a lo largo del tiempo:
     - Tasa de retención mes a mes
     - Valor generado por cohorte
     - Comparar cohortes: "Clientes de Diciembre 2025 tienen mejor retención que Noviembre 2025"
   - Identificar: Qué campañas/eventos generaron mejores cohortes
6. Dashboards y reportes:
   - **Dashboard de Comportamiento de Clientes**:
     - Distribución de clientes por segmento RFM (gráfico)
     - Productos más vendidos por segmento
     - Tendencias de compra (gráfico temporal)
   - **Reporte de Oportunidades**:
     - Clientes listos para upsell (compran productos básicos, tienen capacidad para premium)
     - Clientes para cross-sell (compran solo una categoría, pueden probar otras)
     - Clientes en riesgo de abandono (requieren atención)
   - **Análisis de Efectividad de Personalización**:
     - Comparar: Clientes con recomendaciones personalizadas vs. sin ellas
     - Incremento en ticket, frecuencia, satisfacción
7. Aplicaciones prácticas:
   - **Personalización en tiempo real**:
     - Al ingresar al sitio web/app, mostrar productos recomendados
     - En POS, sugerir al cajero: "Ofrécele X, suele comprarlo"
   - **Campañas dirigidas**:
     - Enviar ofertas específicas según perfil:
       - Amantes de vinos: Promoción de nuevos vinos
       - Cerveceros: 3x2 en cervezas
       - Clientes inactivos: "Te extrañamos, vuelve con 20% OFF"
   - **Optimización de inventario**:
     - Predecir demanda por producto según comportamiento histórico
     - Evitar quiebres de stock en productos clave

**Consideraciones Multi-tenant:**
- Los análisis son por negocio independientemente.
- Los modelos de ML se entrenan con datos del negocio específico.

**Seguridad:**
- Los datos de comportamiento son sensibles.
- Los insights no se comparten fuera del negocio.
- Cumplimiento con leyes de privacidad (anonimización si es necesario).

**UX:**
- Dashboards visuales e interactivos.
- Gráficos de fácil interpretación (heatmaps, scatter plots, etc.).
- Insights en lenguaje simple: "Juan probablemente comprará esta semana".
- Exportación de segmentos para uso en marketing.

**Reglas de Negocio:**
- Los análisis requieren mínimo 100 clientes y 1000 transacciones para ser estadísticamente significativos.
- Los modelos predictivos se reentrenan mensualmente con nuevos datos.
- Las predicciones son probabilísticas, no garantías (se usa para toma de decisiones, no automatización total).
- Los insights de comportamiento no deben usarse de manera invasiva o que incomode al cliente.

---

**Módulo IV: Operaciones de Venta** con sus 35 requisitos funcionales (RF-VTA-001 a RF-VTA-035) distribuidos en 4 submódulos:
- Submódulo 6.4.1: Punto de Venta (POS) - 10 requisitos
- Submódulo 6.4.2: Gestión de Mesas y Cuentas - 8 requisitos
- Submódulo 6.4.3: Gestión de Pedidos - 9 requisitos
- Submódulo 6.4.4: Gestión de Clientes - 8 requisitos
 todo eso es el modulo IV, genera el documento por favor.