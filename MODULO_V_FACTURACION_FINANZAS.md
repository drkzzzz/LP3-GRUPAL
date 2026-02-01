# **MÓDULO V: FACTURACIÓN Y FINANZAS**

---

## **Descripción del Módulo**

El Módulo V: Facturación y Finanzas proporciona un sistema integral para la gestión fiscal, contable y financiera del negocio multi-tenant de licorerías. Este módulo garantiza el cumplimiento normativo con SUNAT (Perú), facilita la administración de gastos e ingresos, proporciona visibilidad financiera en tiempo real, y automatiza procesos contables críticos. Está diseñado para manejar facturación electrónica, control de gastos, conciliación bancaria, reportes financieros, y gestión de tributos, asegurando la transparencia fiscal y la salud financiera del negocio.

**Alcance del módulo:**
- Emisión y gestión de comprobantes de pago electrónicos (boletas, facturas, notas de crédito/débito)
- Integración completa con sistemas de SUNAT y OSE (Operador de Servicios Electrónicos)
- Control y categorización de gastos operativos
- Generación de reportes financieros (estado de resultados, flujo de caja, balance)
- Conciliación bancaria automática y manual
- Gestión de tributos (IGV, impuesto a la renta, retenciones)
- Libro de ventas y compras electrónico
- Auditoría fiscal y trazabilidad completa

**Submódulos incluidos:**
- **6.5.1:** Facturación Electrónica (Comprobantes de Pago)
- **6.5.2:** Gestión de Gastos e Ingresos
- **6.5.3:** Reportes Financieros y Contabilidad
- **6.5.4:** Conciliación Bancaria y Tesorería

---

## **Submódulo 6.5.1: Facturación Electrónica**

---

### **RF-FAC-001: Configurar Certificado Digital y Credenciales SUNAT**

**Descripción:**  
El sistema debe permitir configurar y gestionar el certificado digital necesario para la firma electrónica de comprobantes, así como las credenciales de acceso a SUNAT y OSE, garantizando la autenticidad y validez legal de los documentos electrónicos emitidos.

**Criterios de Aceptación:**
1. Configuración del certificado digital:
   - **Carga del certificado**:
     - Formato soportado: .PFX / .P12 (certificado con clave privada)
     - Carga mediante upload de archivo
     - Contraseña del certificado (encriptada en base de datos)
     - Información extraída automáticamente:
       - Titular del certificado (RUC y razón social)
       - Fecha de emisión
       - Fecha de vencimiento
       - Entidad certificadora
   - **Validación del certificado**:
     - Verificar que es válido (no revocado)
     - Verificar que pertenece al RUC del negocio
     - Verificar que no está vencido
     - Alertar si vence en < 30 días
2. Configuración de credenciales SUNAT:
   - **Acceso a SOL (SUNAT Operaciones en Línea)**:
     - RUC del emisor
     - Usuario SOL
     - Clave SOL (encriptada)
   - **Validación de credenciales**:
     - Test de conexión con SUNAT
     - Verificar que el usuario tiene permisos de facturación electrónica
3. Configuración de OSE:
   - **Selección de proveedor OSE**:
     - Lista de OSE homologados por SUNAT:
       - SUNAT (directo)
       - PSE (proveedores privados: Factura.pe, Nubefact, FacturadorPERU, etc.)
     - Para OSE privado, configurar:
       - URL del servicio web
       - Usuario de la plataforma
       - Token/API Key
       - Ambiente: Producción / Pruebas
   - **Test de integración**:
     - Enviar comprobante de prueba
     - Verificar respuesta (CDR - Constancia de Recepción)
4. Gestión de series de comprobantes:
   - **Configuración por tipo de comprobante**:
     - Boletas: Serie(s) autorizada(s) por SUNAT (ej: B001, B002)
     - Facturas: Serie(s) autorizada(s) (ej: F001, F002)
     - Notas de crédito: NC01, NC02
     - Notas de débito: ND01
   - **Numeración correlativa**:
     - Inicio de numeración (ej: 00000001)
     - Número actual (se incrementa automáticamente)
     - Validación: No permitir saltos en numeración
   - **Series por sede** (opcional):
     - Cada sede puede tener su propia serie
     - Facilita identificación de origen del comprobante
5. Renovación y mantenimiento:
   - **Alertas de vencimiento**:
     - Email/notificación 30 días antes del vencimiento del certificado
     - Dashboard con indicador de estado del certificado:
       - 🟢 Válido (> 90 días)
       - 🟡 Por vencer (30-90 días)
       - 🔴 Crítico (< 30 días) o vencido
   - **Renovación del certificado**:
     - Subir nuevo certificado
     - El sistema mantiene el histórico (para verificar comprobantes antiguos)
     - Transición automática en la fecha configurada
6. Auditoría y seguridad:
   - Registro de todos los accesos a configuración
   - Encriptación de claves y certificados
   - Solo administradores pueden modificar configuración

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio certificado digital y credenciales SUNAT.
- Las series de comprobantes son únicas por negocio.
- No se comparten certificados entre negocios.

**Seguridad:**
- Requiere permiso: `facturacion_config_administrar`
- El certificado y claves se almacenan encriptados en base de datos.
- La contraseña del certificado nunca se muestra en texto plano.
- Backup automático del certificado en almacenamiento seguro.

**UX:**
- Wizard de configuración inicial guiado.
- Indicadores visuales del estado de configuración (✓ Completado, ⚠️ Pendiente).
- Botón "Test de Conexión" para validar configuración.
- Vista previa del certificado (información sin clave privada).

**Reglas de Negocio:**
- Es obligatorio tener certificado digital vigente para emitir comprobantes.
- Las series deben estar autorizadas por SUNAT previamente.
- No se puede emitir comprobantes si el certificado está vencido.
- La numeración correlativa nunca debe tener saltos (auditoría SUNAT).

---

### **RF-FAC-002: Emitir Factura Electrónica con Validación SUNAT**

**Descripción:**  
El sistema debe generar facturas electrónicas en formato XML estándar SUNAT, firmarlas digitalmente, enviarlas al sistema de SUNAT/OSE para validación, procesar la constancia de recepción (CDR), y generar el PDF visual con código QR para entrega al cliente.

**Criterios de Aceptación:**
1. Generación de factura desde venta:
   - **Origen de la factura**:
     - Desde venta en POS (RF-VTA-002)
     - Desde pedido completado (RF-VTA-019)
     - Desde cuenta de mesa cerrada (RF-VTA-015)
     - Generación manual (casos especiales)
   - **Datos obligatorios capturados**:
     - **Emisor**: RUC, razón social, dirección fiscal, código de ubigeo
     - **Cliente**: RUC (11 dígitos), razón social, dirección fiscal
     - **Comprobante**: Serie, número correlativo, fecha y hora de emisión
     - **Detalle**: Por cada producto/servicio:
       - Código del producto (SKU)
       - Descripción
       - Unidad de medida (NIU, ZZ, etc.)
       - Cantidad
       - Valor unitario (sin IGV)
       - Precio unitario (con IGV)
       - Código de afectación al IGV (gravado, exonerado, inafecto)
       - IGV por item
       - Subtotal
     - **Totales**:
       - Total valor de venta (sin IGV)
       - Total IGV (18%)
       - Total precio de venta (con IGV)
       - Descuentos globales (si aplican)
       - Monto en letras ("SON: MIL DOSCIENTOS TREINTA Y 45/100 SOLES")
     - **Información adicional**:
       - Forma de pago (contado, crédito)
       - Observaciones
2. Generación del XML:
   - **Formato UBL 2.1** (estándar SUNAT):
     - Estructura XML conforme a esquema XSD de SUNAT
     - Namespace correcto
     - Todos los campos obligatorios presentes
   - **Cálculos validados**:
     - Suma de items = Subtotal
     - Subtotal + IGV = Total
     - No hay errores de redondeo (2 decimales)
   - **Códigos estándar**:
     - Tipo de documento: "01" (Factura)
     - Código de moneda: "PEN" (Soles)
     - Tipo de documento de identidad: "6" (RUC)
     - Código de afectación IGV: "10" (gravado), "20" (exonerado)
3. Firma digital del XML:
   - **Proceso de firma**:
     - Generar hash SHA-256 del XML
     - Firmar con clave privada del certificado digital
     - Incluir firma en nodo `<Signature>` del XML
     - Agregar información del certificado (sin clave privada)
   - **Validación de firma**:
     - Verificar que la firma es válida antes de enviar
     - Verificar que el certificado no está revocado
4. Envío a SUNAT/OSE:
   - **Empaquetado**:
     - Comprimir XML en formato ZIP
     - Nombre del archivo: `{RUC}-{TipoDoc}-{Serie}-{Numero}.zip`
     - Ejemplo: `20123456789-01-F001-00000123.zip`
   - **Envío vía Webservice SOAP**:
     - Conectar a URL del OSE configurado
     - Autenticación con credenciales
     - Método: `sendBill()` para facturas normales
     - Método: `sendSummary()` para resúmenes diarios
   - **Recepción de respuesta**:
     - Si es exitoso: Recibir CDR (Constancia de Recepción)
     - Si es rechazado: Recibir código de error y descripción
5. Procesamiento del CDR:
   - **Análisis de la respuesta**:
     - Código de respuesta SUNAT:
       - `0` = Aceptado
       - `4xxx` = Observado (aceptado con observaciones)
       - `2xxx` = Rechazado
     - Extraer:
       - Código hash del comprobante (validación de integridad)
       - Fecha y hora de recepción por SUNAT
       - Número de ticket (si aplica)
   - **Actualización del estado**:
     - Si es aceptado: Estado = `'emitido'`
     - Si es observado: Estado = `'emitido_con_observaciones'`, registrar observaciones
     - Si es rechazado: Estado = `'rechazado'`, registrar motivo
   - **Almacenamiento**:
     - Guardar XML firmado
     - Guardar CDR (ZIP)
     - Registrar en base de datos con todos los metadatos
6. Generación del PDF visual:
   - **Diseño del comprobante**:
     - Logo del negocio (si está configurado)
     - Encabezado con datos del emisor
     - Título: "FACTURA ELECTRÓNICA"
     - Serie y número destacados
     - Tabla de detalle de productos
     - Totales y IGV desglosado
     - Pie con información adicional
   - **Código QR obligatorio**:
     - Contenido: `{RUC}|{TipoDoc}|{Serie}|{Numero}|{IGV}|{Total}|{Fecha}|{TipoDocCliente}|{NumDocCliente}|`
     - Tamaño: Mínimo 2x2 cm
     - Posición: Esquina inferior izquierda
     - Permite validación rápida con app de SUNAT
   - **Información adicional**:
     - Leyenda: "Representación impresa de la Factura Electrónica"
     - URL de consulta en SUNAT
     - Hash de la firma digital (código alfanumérico)
     - Código de autorización (si OSE lo provee)
7. Entrega al cliente:
   - **Opciones de entrega**:
     - Impresión automática (si hay impresora configurada)
     - Envío por email (PDF + XML adjuntos)
     - Envío por WhatsApp (PDF)
     - Descarga desde portal web del cliente
     - QR para descargar desde smartphone
8. Manejo de errores:
   - **Errores comunes de SUNAT**:
     - "RUC no válido": Validar RUC del cliente antes de emitir
     - "Serie no autorizada": Verificar configuración de series
     - "Numeración duplicada": Verificar correlativo
     - "Certificado vencido": Alerta crítica, bloquear emisión
   - **Reintento automático**:
     - Si SUNAT no responde (timeout): Reintentar cada 5 minutos (máx. 3 intentos)
     - Si hay error de red: Marcar como `'pendiente_envio'`, procesar en job nocturno
   - **Modo contingencia**:
     - Si SUNAT está caído > 2 horas: Permitir emisión en modo contingencia
     - Estado: `'emitido_contingencia'`
     - Enviar cuando SUNAT esté disponible (resumen diario)

**Consideraciones Multi-tenant:**
- Cada negocio emite sus propias facturas con su RUC y series.
- Las facturas se almacenan segregadas por negocio.

**Seguridad:**
- Requiere permiso: `facturacion_emitir`
- La firma digital garantiza autenticidad e integridad.
- No se pueden modificar facturas después de emitidas (inmutabilidad).
- Auditoría completa: quién emitió, cuándo, desde dónde.

**UX:**
- Vista previa del PDF antes de emitir (opcional).
- Indicador de progreso: "Generando XML → Firmando → Enviando a SUNAT → Procesando CDR".
- Notificación de éxito con opción de imprimir/enviar.
- En caso de error, mensaje claro con solución sugerida.

**Reglas de Negocio:**
- Las facturas solo se emiten a clientes con RUC válido.
- La emisión es obligatoria por ley para todas las ventas a empresas.
- Las facturas deben conservarse por 5 años mínimo (auditoría).
- No se puede anular una factura emitida, solo emitir nota de crédito.

---

### **RF-FAC-003: Emitir Boleta Electrónica**

**Descripción:**  
El sistema debe generar boletas de venta electrónicas para clientes sin RUC (personas naturales), siguiendo el mismo proceso de firma y validación que las facturas, pero con requisitos de datos simplificados.

**Criterios de Aceptación:**
1. Generación de boleta:
   - **Diferencias vs. Factura**:
     - Tipo de documento: "03" (Boleta)
     - Cliente: DNI opcional (no obligatorio)
     - Razón social: Nombre completo o "Cliente varios" si no se identifica
     - Dirección del cliente: No obligatoria
   - **Datos obligatorios**:
     - Emisor (igual que factura)
     - Serie de boleta (ej: B001)
     - Número correlativo
     - Fecha y hora de emisión
     - Detalle de productos/servicios
     - Totales (valor de venta, IGV, total)
2. Validación de montos:
   - **Monto máximo sin DNI**: $700 PEN (configurable según normativa)
   - Si el total > $700:
     - Solicitar DNI obligatoriamente
     - Validar que DNI tiene 8 dígitos
   - Para ventas < $700: DNI opcional
3. Proceso de emisión:
   - Igual que factura (RF-FAC-002):
     - Generar XML en formato UBL
     - Firmar digitalmente
     - Enviar a SUNAT/OSE
     - Recibir CDR
     - Generar PDF con QR
   - **Simplificaciones**:
     - Menos validaciones de datos del cliente
     - Formato XML más simple
4. Boleta de contingencia:
   - **Resumen diario de boletas**:
     - Si SUNAT está caído, emitir boletas en contingencia
     - Al final del día, generar "Resumen Diario de Boletas" (RC)
     - Enviar RC a SUNAT para regularizar
     - SUNAT responde con ticket de aceptación
5. Anulación de boletas del mismo día:
   - **Comunicación de baja**:
     - Solo se pueden anular boletas emitidas el mismo día
     - Antes de las 11:59 PM del día de emisión
     - Generar documento "Comunicación de Baja"
     - Enviar a SUNAT
     - La boleta queda anulada (no genera nota de crédito)
6. Boletas a clientes extranjeros:
   - **Documento de identidad**:
     - Tipo: "Pasaporte", "Carnet de Extranjería"
     - Capturar número de documento
   - **Información adicional**:
     - País de residencia
     - Si es turista, puede solicitar devolución de IGV (tax free)

**Consideraciones Multi-tenant:**
- Cada negocio tiene sus propias series de boletas.
- La numeración es independiente por negocio.

**Seguridad:**
- Requiere permiso: `facturacion_emitir_boleta`
- Misma seguridad que facturas (firma digital, inmutabilidad).

**UX:**
- Emisión rápida: No solicitar datos innecesarios si monto < $700.
- Opción "Cliente varios" por defecto.
- Flujo simplificado para agilizar atención en POS.

**Reglas de Negocio:**
- Las boletas no permiten deducción de impuestos (solo facturas).
- Los montos están sujetos a límites legales para identificación.
- Las boletas del día anterior no se pueden anular, solo nota de crédito.

---

### **RF-FAC-004: Emitir Notas de Crédito y Débito**

**Descripción:**  
El sistema debe permitir emitir notas de crédito (para anulaciones, devoluciones, descuentos) y notas de débito (para cargos adicionales), vinculadas a comprobantes previamente emitidos, con validación de motivos y montos.

**Criterios de Aceptación:**
1. Emisión de Nota de Crédito:
   - **Motivos válidos** (catálogo SUNAT):
     - `01`: Anulación de la operación
     - `02`: Anulación por error en el RUC
     - `03`: Corrección por error en la descripción
     - `04`: Descuento global
     - `05`: Descuento por ítem
     - `06`: Devolución total
     - `07`: Devolución por ítem
     - `08`: Bonificación
     - `09`: Disminución en el valor
     - Otros según actualización de SUNAT
   - **Proceso de emisión**:
     - Buscar comprobante original (factura o boleta)
     - Seleccionar motivo de la nota de crédito
     - Ingresar:
       - **Para anulación total**: Monto igual al comprobante original
       - **Para devolución parcial**: Seleccionar items y cantidades a devolver
       - **Para descuento**: Ingresar monto del descuento
     - Descripción detallada del motivo
   - **Generación del XML**:
     - Tipo de documento: "07" (Nota de Crédito)
     - Serie: NC01, NC02, etc.
     - Referencia al comprobante modificado:
       - Tipo de documento referenciado (01 = Factura, 03 = Boleta)
       - Serie y número del comprobante
       - Motivo (código y descripción)
     - Detalle de items afectados (igual que comprobante original)
     - Totales: Monto que se resta del comprobante original
2. Emisión de Nota de Débito:
   - **Motivos válidos**:
     - `01`: Intereses por mora
     - `02`: Aumento en el valor
     - `03`: Penalidades
     - Otros según catálogo SUNAT
   - **Proceso de emisión**:
     - Buscar comprobante original
     - Seleccionar motivo
     - Ingresar:
       - Monto adicional a cobrar
       - Descripción del cargo
   - **Generación del XML**:
     - Tipo de documento: "08" (Nota de Débito)
     - Serie: ND01, ND02, etc.
     - Referencia al comprobante original
     - Detalle del cargo adicional
     - Total: Monto que se suma al comprobante original
3. Validaciones:
   - **Para Nota de Crédito**:
     - El comprobante referenciado existe y está emitido
     - El monto de la NC <= Monto del comprobante original
     - No se puede emitir NC sobre otra NC (solo sobre comprobantes principales)
     - No se puede emitir NC sobre comprobante anulado
   - **Para Nota de Débito**:
     - El comprobante referenciado existe y está emitido
     - El motivo justifica el cargo adicional
4. Proceso de firma y envío:
   - Igual que facturas/boletas:
     - Generar XML
     - Firmar digitalmente
     - Enviar a SUNAT/OSE
     - Recibir CDR
     - Generar PDF
5. Impacto en inventario (para notas de crédito por devolución):
   - **Devolución total o parcial**:
     - Reintegrar productos al inventario (si están en condiciones)
     - Llamar a función `reintegrar_inventario()`
     - Registrar movimiento de inventario
6. Impacto financiero:
   - **Nota de Crédito**:
     - Reduce las ventas del período
     - Si el cliente pagó, generar reembolso o crédito a favor
     - Actualizar reportes financieros
   - **Nota de Débito**:
     - Incrementa las ventas del período
     - Generar cargo adicional al cliente
7. Vista de documentos relacionados:
   - **En la ficha del comprobante original**:
     - Listar todas las NC/ND vinculadas
     - Monto original
     - Monto de notas de crédito (-)
     - Monto de notas de débito (+)
     - Saldo neto

**Consideraciones Multi-tenant:**
- Las notas de crédito/débito son por negocio.
- Solo se pueden referenciar comprobantes del mismo negocio.

**Seguridad:**
- Requiere permiso: `facturacion_notas_emitir`
- Las NC > $500 USD requieren aprobación de supervisor.
- Auditoría: Quién emitió, motivo, comprobante referenciado.

**UX:**
- Búsqueda rápida del comprobante original por número.
- Vista previa del comprobante original mientras se crea la NC/ND.
- Calculadora automática de montos.
- Plantillas de descripción según motivo.

**Reglas de Negocio:**
- Las notas de crédito por anulación total deben emitirse el mismo día del comprobante (recomendación).
- Las notas de crédito por devolución pueden emitirse dentro de los 7 días de la venta.
- El IGV se ajusta proporcionalmente en las NC/ND.
- Las notas de crédito/débito deben conservarse junto con el comprobante original.

---

### **RF-FAC-005: Gestionar Libros Electrónicos (Registro de Ventas y Compras)**

**Descripción:**  
El sistema debe generar automáticamente los libros electrónicos de ventas y compras en formato PLE (Programa de Libros Electrónicos), cumpliendo con los requisitos de SUNAT para la declaración mensual de IGV y otros tributos.

**Criterios de Aceptación:**
1. Registro de Ventas Electrónico:
   - **Contenido del registro**:
     - Todos los comprobantes emitidos en el mes:
       - Facturas
       - Boletas (resumen diario)
       - Notas de crédito
       - Notas de débito
     - Por cada comprobante:
       - Fecha de emisión
       - Tipo de comprobante
       - Serie y número
       - Tipo y número de documento del cliente
       - Nombre/Razón social del cliente
       - Valor de venta (base imponible)
       - IGV
       - Total
       - Tipo de cambio (si es moneda extranjera)
       - Estado (emitido, anulado)
   - **Formato PLE 14.1**:
     - Archivo de texto plano (.txt)
     - Campos separados por pipe (|)
     - Nombre: `LE{RUC}{AÑO}{MES}00140100001111.txt`
     - Ejemplo: `LE20123456789202601001401000011111.txt` (Enero 2026)
   - **Validaciones**:
     - Todas las facturas del mes están incluidas
     - Los totales cuadran (suma de ventas = total del registro)
     - No hay duplicados
     - La numeración es correlativa
2. Registro de Compras Electrónico:
   - **Contenido del registro**:
     - Todos los comprobantes recibidos de proveedores:
       - Facturas de compra (ingresadas manualmente o via integración)
       - Notas de crédito/débito recibidas
     - Por cada comprobante:
       - Fecha de emisión y fecha de pago
       - Tipo de comprobante
       - Serie y número
       - RUC del proveedor
       - Razón social del proveedor
       - Base imponible
       - IGV (crédito fiscal)
       - Total
   - **Formato PLE 8.1**:
     - Archivo de texto plano (.txt)
     - Nombre: `LE{RUC}{AÑO}{MES}00080100001111.txt`
3. Generación automática:
   - **Job mensual**:
     - El día 1 de cada mes, generar automáticamente los libros del mes anterior
     - Estado: `'generado'`
   - **Generación manual**:
     - El usuario puede regenerar el libro de cualquier mes
     - Útil si hubo correcciones o NC/ND posteriores
4. Validación de consistencia:
   - **Verificaciones automáticas**:
     - Total de ventas en libros = Total de ventas en reportes financieros
     - Todos los comprobantes emitidos están registrados
     - No hay comprobantes duplicados
     - Las anulaciones están correctamente marcadas
   - **Reporte de inconsistencias**:
     - Si hay errores, generar reporte con detalle
     - Alertar al contador para corrección
5. Envío a SUNAT:
   - **Programa de Libros Electrónicos (PLE)**:
     - Conectar al sistema PLE de SUNAT
     - Subir archivos .txt generados
     - Recibir constancia de recepción
   - **Plazos**:
     - Libros de ventas: Hasta el día 10 del mes siguiente
     - Libros de compras: Hasta el día 10 del mes siguiente
   - **Estados**:
     - Generado
     - Enviado
     - Aceptado por SUNAT
     - Rechazado (con motivo)
6. Vista y descarga:
   - **Interfaz de gestión**:
     - Lista de libros generados por mes/año
     - Estado de cada libro
     - Botones:
       - Descargar archivo .txt
       - Ver resumen (totales, cantidad de registros)
       - Reenviar a SUNAT
       - Regenerar (si hay correcciones)
7. Integración con declaración de impuestos:
   - **PDT 621 (Declaración de IGV)**:
     - Usar datos de libros para prellenar declaración
     - Calcular IGV a pagar:
       - IGV de ventas (débito fiscal)
       - Menos: IGV de compras (crédito fiscal)
       - Igual: IGV a pagar o saldo a favor

**Consideraciones Multi-tenant:**
- Cada negocio genera sus propios libros electrónicos.
- Los libros son independientes por RUC.

**Seguridad:**
- Requiere permiso: `contabilidad_libros_gestionar`
- Los archivos de libros son sensibles (información fiscal completa).
- Acceso restringido a contador y administrador.

**UX:**
- Dashboard con estado de libros mensuales (calendario).
- Indicadores: ✓ Generado y enviado, ⚠️ Pendiente de envío, ❌ Rechazado.
- Descarga de archivos con un clic.
- Vista previa del contenido del libro en tabla legible.

**Reglas de Negocio:**
- Los libros deben presentarse mensualmente sin falta (obligación legal).
- Los libros deben conservarse por 5 años.
- Las correcciones a libros ya presentados requieren presentar libros rectificatorios.
- Los totales de libros deben coincidir con la declaración de impuestos.

---

### **RF-FAC-006: Validar y Consultar Comprobantes en SUNAT**

**Descripción:**  
El sistema debe permitir consultar el estado de comprobantes emitidos directamente en SUNAT, validar comprobantes de proveedores, y proporcionar al cliente herramientas para verificar la autenticidad de los comprobantes recibidos.

**Criterios de Aceptación:**
1. Consulta de estado de comprobante propio:
   - **Búsqueda**:
     - Por serie y número
     - Por fecha de emisión
     - Por RUC del cliente
   - **Información consultada en SUNAT**:
     - Estado: Aceptado, Observado, Rechazado, Anulado
     - Fecha y hora de aceptación
     - Código CDR
     - Observaciones (si las hay)
   - **Sincronización de estado**:
     - Si el estado en SUNAT difiere del estado local:
       - Actualizar estado local
       - Registrar en auditoría
       - Notificar al administrador
2. Validación de comprobantes de proveedores:
   - **Al registrar una compra**:
     - Ingresar RUC del proveedor, serie y número del comprobante
     - Consultar en SUNAT vía web service
     - Validar:
       - El comprobante existe
       - Está emitido a nuestro RUC
       - No está anulado
       - Los montos coinciden (opcional, manual)
   - **Estados del comprobante**:
     - ✓ Válido: Comprobante existe y está activo
     - ⚠️ No encontrado: No existe en SUNAT (posible falsificación)
     - ❌ Anulado: Comprobante fue anulado por el proveedor
     - ⚠️ Observado: Tiene observaciones de SUNAT
   - **Alerta de comprobante falso**:
     - Si no se encuentra en SUNAT, alertar inmediatamente
     - No permitir registro del gasto hasta validar manualmente
     - Registrar el intento para auditoría (posible fraude)
3. Portal de validación para clientes:
   - **Página pública en el sitio web**:
     - URL: `https://mitienda.com/validar-comprobante`
     - Sin login requerido
   - **Formulario de consulta**:
     - RUC del emisor
     - Tipo de comprobante (Factura, Boleta)
     - Serie y número
     - Fecha de emisión (opcional, ayuda a filtrar)
   - **Resultado de la consulta**:
     - ✓ Comprobante válido:
       - Emitido el: [Fecha]
       - Cliente: [RUC/DNI y nombre]
       - Monto: S/ [Total]
       - Estado: Aceptado por SUNAT
     - ❌ Comprobante no encontrado:
       - "El comprobante ingresado no existe en nuestros registros ni en SUNAT"
       - Sugerencia: Verificar los datos o contactar al establecimiento
   - **Validación con código QR**:
     - Escanear QR del comprobante con smartphone
     - Redirigir a página de validación con datos precargados
     - Mostrar resultado inmediatamente
4. Consulta masiva de comprobantes:
   - **Verificación mensual**:
     - Seleccionar mes/año
     - Consultar en SUNAT el estado de todos los comprobantes emitidos
     - Generar reporte de inconsistencias:
       - Comprobantes locales que SUNAT no tiene
       - Comprobantes con estado diferente
   - **Reconciliación automática**:
     - Actualizar estados según respuesta de SUNAT
     - Marcar comprobantes con problemas para revisión manual
5. Integración con apps móviles de SUNAT:
   - **Link directo a consulta en app SUNAT**:
     - En el PDF del comprobante, incluir link:
       - "Consultar en SUNAT Móvil"
     - Al hacer clic, abre la app con datos precargados
   - **QR estándar SUNAT**:
     - El QR del comprobante es compatible con app de SUNAT
     - El cliente puede validar autenticidad directamente

**Consideraciones Multi-tenant:**
- Cada negocio consulta sus propios comprobantes.
- La validación de proveedores es por negocio.

**Seguridad:**
- La consulta pública tiene rate limiting (evitar abuso).
- La consulta masiva requiere permiso: `facturacion_consulta_masiva`
- Las credenciales de consulta a SUNAT se reutilizan de la configuración.

**UX:**
- Búsqueda rápida con autocompletado.
- Resultado visual claro: ✓ en verde, ❌ en rojo.
- Página de validación pública simple y responsive.
- Botón "Consultar en SUNAT" en cada comprobante de la lista.

**Reglas de Negocio:**
- La consulta en SUNAT es la fuente de verdad (prevalece sobre el estado local).
- Los comprobantes rechazados por SUNAT no tienen validez legal.
- Es responsabilidad del negocio verificar comprobantes de proveedores (evitar gastos ficticios).

---

### **RF-FAC-007: Gestionar Anulaciones y Bajas de Comprobantes**

**Descripción:**  
El sistema debe permitir anular comprobantes emitidos por error, gestionar comunicaciones de baja para boletas del mismo día, y manejar el proceso de baja de comprobantes ante SUNAT según las normativas vigentes.

**Criterios de Aceptación:**
1. Anulación de comprobantes del mismo día (Boletas):
   - **Comunicación de Baja**:
     - Solo boletas emitidas el mismo día (antes de 11:59 PM)
     - Proceso:
       - Seleccionar boleta(s) a anular
       - Ingresar motivo de anulación (obligatorio)
       - Generar documento "Comunicación de Baja" (XML)
       - Tipo de documento: "RA" (Resumen de Anulación)
       - Firmar digitalmente
       - Enviar a SUNAT
   - **Contenido del XML de baja**:
     - Fecha de generación
     - Lista de boletas a anular:
       - Serie y número
       - Motivo de baja
     - Firma digital
   - **Respuesta de SUNAT**:
     - SUNAT asigna un ticket
     - Estado: Pendiente de procesamiento
     - Después de unas horas, consultar estado con el ticket
     - Si es aceptado: Boletas quedan anuladas
     - Si es rechazado: Boletas permanecen vigentes
2. Anulación de comprobantes de días anteriores (Facturas y Boletas):
   - **Nota de Crédito obligatoria**:
     - No se pueden anular directamente
     - Debe emitirse Nota de Crédito por anulación (RF-FAC-004)
     - Motivo: `01` - Anulación de la operación
     - Monto: Igual al comprobante original
3. Resumen diario de reversiones:
   - **Para múltiples anulaciones**:
     - Si hay muchas boletas a anular en un día
     - Generar "Resumen Diario de Reversiones"
     - Enviar un solo documento a SUNAT con todas las bajas
4. Gestión de bajas:
   - **Vista de comprobantes pendientes de baja**:
     - Lista de comunicaciones de baja enviadas
     - Estado:
       - Enviado (esperando respuesta)
       - Aceptado (baja efectiva)
       - Rechazado (baja no procede)
     - Ticket de SUNAT
     - Fecha de envío
   - **Consulta de estado**:
     - Botón "Actualizar Estado" para consultar en SUNAT
     - Si aún está pendiente, mostrar tiempo transcurrido
     - Si es aceptado, actualizar estado de las boletas localmente
5. Impacto de la anulación:
   - **En inventario**:
     - Si la venta fue de productos físicos:
       - Reintegrar al inventario (si no se entregaron)
       - O registrar como merma (si ya se consumieron)
   - **En finanzas**:
     - Restar del total de ventas del día
     - Ajustar reportes financieros
     - Si hubo pago, procesar reembolso
   - **En libros electrónicos**:
     - Marcar comprobante como anulado en el Registro de Ventas
     - No computar en la base imponible
6. Restricciones:
   - **No se puede anular**:
     - Facturas del mismo día (usar NC)
     - Comprobantes ya anulados
     - Comprobantes con notas de crédito vinculadas
   - **Límite de anulaciones**:
     - Alertar si un usuario anula > 5 comprobantes en un día (posible error sistemático)
7. Auditoría de anulaciones:
   - **Registro completo**:
     - Usuario que solicitó anulación
     - Fecha y hora
     - Motivo detallado
     - Comprobante anulado
     - Estado de la comunicación de baja
     - Aprobador (si requirió autorización)

**Consideraciones Multi-tenant:**
- Las bajas son por negocio.
- Cada negocio gestiona sus propias comunicaciones de baja.

**Seguridad:**
- Requiere permiso: `facturacion_anular_comprobante`
- Anulaciones > $500 USD requieren aprobación de gerente.
- Auditoría completa de todas las anulaciones (prevenir fraude).

**UX:**
- Botón "Anular" solo visible si el comprobante es anulable.
- Modal de confirmación: "¿Está seguro de anular este comprobante?"
- Indicador de estado en tiempo real durante el procesamiento.
- Lista de "Bajas Pendientes" con actualización de estado.

**Reglas de Negocio:**
- Las boletas solo se pueden anular el mismo día (hasta medianoche).
- Las facturas requieren nota de crédito (cualquier día).
- Las anulaciones excesivas pueden generar sanciones de SUNAT (auditoría).
- El motivo de anulación debe ser válido y justificable.

---

## **Submódulo 6.5.2: Gestión de Gastos e Ingresos**

---

### **RF-FAC-008: Registrar Gastos Operativos**

**Descripción:**  
El sistema debe permitir registrar todos los gastos operativos del negocio (compras a proveedores, servicios, salarios, etc.), categorizarlos adecuadamente, vincularlos con comprobantes de pago, y proporcionar trazabilidad completa para control financiero y declaración de impuestos.

**Criterios de Aceptación:**
1. Registro de gasto:
   - **Información básica**:
     - Fecha del gasto
     - Tipo de gasto (dropdown):
       - Compra de inventario (productos para reventa)
       - Servicios básicos (luz, agua, internet, teléfono)
       - Alquiler/arrendamiento
       - Salarios y beneficios
       - Marketing y publicidad
       - Transporte y logística
       - Mantenimiento y reparaciones
       - Suministros de oficina
       - Honorarios profesionales (contador, abogado)
       - Impuestos y tributos
       - Seguros
       - Financieros (intereses bancarios)
       - Otros
     - Categoría contable (según plan de cuentas):
       - Costo de ventas (60)
       - Gastos de administración (63)
       - Gastos de ventas (64)
       - Gastos financieros (67)
       - Otros gastos (65)
   - **Proveedor/Beneficiario**:
     - Buscar en catálogo de proveedores
     - O crear nuevo:
       - RUC/DNI
       - Razón social / Nombre
       - Dirección
       - Teléfono, email
       - Tipo: Proveedor de productos, servicios, empleado, etc.
   - **Comprobante de pago**:
     - Tipo: Factura, boleta, recibo por honorarios, ticket, sin comprobante
     - Serie y número
     - Fecha de emisión
     - Monto total
     - IGV (si aplica)
     - Validar en SUNAT (RF-FAC-006)
     - Adjuntar foto/scan del comprobante
   - **Detalles del gasto**:
     - Descripción detallada
     - Centro de costo (si aplica): Sede específica, departamento
     - Proyecto (si aplica)
     - Observaciones
   - **Forma de pago**:
     - Efectivo (de caja chica o caja general)
     - Transferencia bancaria (cuenta origen)
     - Tarjeta de crédito/débito
     - Cheque
     - Crédito al proveedor (cuenta por pagar)
2. Validaciones:
   - Si es factura de proveedor:
     - Validar RUC del proveedor
     - Validar que la factura está emitida al RUC del negocio
     - Consultar en SUNAT (opcional pero recomendado)
   - Si es gasto sin comprobante:
     - Alertar que no será deducible de impuestos
     - Requerir justificación adicional
   - Validar que el monto es > 0
3. Registro de gastos recurrentes:
   - **Configurar gasto recurrente**:
     - Ejemplo: Alquiler de local ($1,500 USD mensuales)
     - Frecuencia: Mensual, quincenal, anual
     - Día de registro automático
     - Proveedor y categoría predefinidos
   - **Generación automática**:
     - El sistema crea el registro del gasto automáticamente
     - Estado: `'pendiente_aprobacion'`
     - El administrador revisa y confirma
     - Al confirmar, cambia a `'registrado'`
4. Aprobación de gastos:
   - **Flujo de aprobación** (opcional, configurable):
     - El usuario registra el gasto: Estado = `'pendiente_aprobacion'`
     - El supervisor/gerente revisa
     - Puede: Aprobar, Rechazar, Solicitar corrección
     - Si es aprobado: Estado = `'aprobado'`, se registra en contabilidad
     - Si es rechazado: Estado = `'rechazado'`, no afecta reportes
   - **Límites de aprobación**:
     - Gastos < $100: Aprobación automática
     - Gastos $100-$500: Requiere aprobación de supervisor
     - Gastos > $500: Requiere aprobación de gerente
5. Impacto en inventario (para compras de productos):
   - Si el tipo de gasto es "Compra de inventario":
     - Vincular con ingreso de inventario (RF-INV-022)
     - Los productos comprados se agregan al inventario
     - El costo unitario se registra para cálculo de margen
6. Impacto en cuentas por pagar:
   - Si la forma de pago es "Crédito al proveedor":
     - Crear registro en `cuenta_por_pagar`
     - Fecha de vencimiento (según plazo acordado: 15, 30, 60 días)
     - Estado: `'pendiente'`
     - Se incluye en reporte de cuentas por pagar
7. Vista de gastos:
   - **Lista de gastos**:
     - Tabla con todos los gastos registrados
     - Filtros: Por fecha, tipo, proveedor, categoría, estado
     - Columnas: Fecha, proveedor, tipo, categoría, monto, comprobante, estado
     - Acción: Ver detalle, editar (si pendiente), eliminar (si pendiente)
   - **Detalle del gasto**:
     - Toda la información capturada
     - Comprobante adjunto (imagen)
     - Historial de aprobaciones
     - Impacto en reportes financieros

**Consideraciones Multi-tenant:**
- Los gastos son por negocio.
- Cada negocio tiene su propio catálogo de proveedores.

**Seguridad:**
- Requiere permiso: `gastos_registrar`
- Aprobación de gastos: `gastos_aprobar`
- Los gastos son datos sensibles (información financiera).
- Auditoría completa de registro, aprobación, y modificaciones.

**UX:**
- Formulario simplificado con campos obligatorios destacados.
- Captura de foto del comprobante desde móvil.
- Autocompletado de proveedor al ingresar RUC.
- Plantillas de gastos frecuentes (alquiler, servicios básicos).
- Importación masiva desde Excel.

**Reglas de Negocio:**
- Solo los gastos con comprobante válido son deducibles de impuestos.
- Los gastos sin comprobante > $200 USD requieren justificación escrita.
- Los gastos aprobados no pueden modificarse (solo anular y crear nuevo).
- Los gastos deben registrarse en el período contable correspondiente.

---

### **RF-FAC-009: Gestionar Cuentas por Pagar a Proveedores**

**Descripción:**  
El sistema debe gestionar las deudas con proveedores, controlando fechas de vencimiento, enviando alertas de pagos próximos, registrando pagos realizados, y generando reportes de antigüedad de deudas para optimizar el flujo de caja.

**Criterios de Aceptación:**
1. Generación de cuentas por pagar:
   - **Origen**:
     - Desde registro de gasto con pago a crédito (RF-FAC-008)
     - Desde compra de inventario a crédito (RF-INV-022)
     - Registro manual (facturas atrasadas, ajustes)
   - **Información registrada**:
     - Proveedor (nombre, RUC, contacto)
     - Comprobante de pago (factura del proveedor)
     - Fecha de emisión del comprobante
     - Fecha de vencimiento (según plazo: 15, 30, 60, 90 días)
     - Monto total de la deuda
     - Saldo pendiente (si hay pagos parciales)
     - Estado: Pendiente, Vencida, Pagada parcialmente, Pagada totalmente
     - Condiciones de pago (contado, crédito, con descuento por pronto pago)
2. Alertas de vencimiento:
   - **Notificaciones automáticas**:
     - 7 días antes del vencimiento: "Pago próximo a vencer"
     - 1 día antes: "Pago vence mañana"
     - Día del vencimiento: "Pago vence hoy"
     - Después del vencimiento: "Pago vencido hace X días"
   - **Destinatarios**:
     - Encargado de tesorería
     - Administrador
     - Gerente general
3. Registro de pagos:
   - **Proceso de pago**:
     - Seleccionar cuenta(s) por pagar
     - Ingresar:
       - Fecha de pago
       - Monto pagado (puede ser parcial)
       - Método de pago:
         - Transferencia bancaria (seleccionar cuenta)
         - Cheque (número de cheque)
         - Efectivo
       - Número de operación/transacción
       - Comprobante de pago (opcional: captura/scan del voucher)
     - Confirmar pago
   - **Actualización de estado**:
     - Si pago completo: Estado = `'pagada'`
     - Si pago parcial: Estado = `'pagada_parcialmente'`, actualizar saldo pendiente
     - Registrar en movimientos bancarios (si aplica)
4. Descuentos por pronto pago:
   - **Condiciones especiales**:
     - Ejemplo: "2/10 neto 30" = 2% descuento si paga en 10 días, sino plazo 30 días
   - **Aplicación automática**:
     - Si se paga antes del plazo de descuento:
       - Calcular descuento automáticamente
       - Aplicar descuento al monto a pagar
       - Registrar el ahorro en reportes
5. Gestión de pagos vencidos:
   - **Clasificación por antigüedad**:
     - Corriente (0-30 días)
     - Vencido leve (31-60 días)
     - Vencido moderado (61-90 días)
     - Vencido severo (> 90 días)
   - **Acciones**:
     - Contactar al proveedor para negociar
     - Registrar acuerdos de pago (plan de cuotas)
     - Alerta si hay compras bloqueadas por deuda vencida
6. Historial de pagos a proveedor:
   - **Vista por proveedor**:
     - Total de compras en el período
     - Total pagado
     - Saldo pendiente
     - Antigüedad promedio de pago (días promedio que se tarda en pagar)
     - Historial de facturas y pagos (timeline)
7. Reportes de cuentas por pagar:
   - **Reporte de Aging (Antigüedad de Deudas)**:
     - Tabla con proveedores y deudas agrupadas por antigüedad
     - Columnas: 0-30 días, 31-60, 61-90, > 90 días
     - Total por proveedor y total general
   - **Flujo de Pagos Proyectado**:
     - Calendario con fechas de vencimiento
     - Monto a pagar por día/semana/mes
     - Alerta si hay concentración de pagos (riesgo de liquidez)
   - **Ranking de Proveedores por Deuda**:
     - Top 10 proveedores con mayor saldo pendiente
     - Identificar riesgos de dependencia financiera

**Consideraciones Multi-tenant:**
- Las cuentas por pagar son por negocio.
- Cada negocio gestiona sus propios proveedores y pagos.

**Seguridad:**
- Requiere permiso: `cuentas_pagar_ver`, `cuentas_pagar_registrar_pago`
- Los pagos > $1,000 USD requieren aprobación de gerente.
- Auditoría de todos los pagos registrados.

**UX:**
- Dashboard con resumen: Total por pagar, vencido, próximo a vencer.
- Calendario de pagos visual.
- Botón "Pagar Ahora" en cada cuenta por pagar.
- Filtros por proveedor, estado, rango de fechas.

**Reglas de Negocio:**
- Los pagos vencidos > 30 días pueden bloquear nuevas compras al proveedor.
- Los descuentos por pronto pago deben aprovecharse si mejoran el flujo de caja.
- Los pagos parciales deben acordarse con el proveedor previamente.
- Las deudas > 90 días deben negociarse o provisionarse como posible pérdida.

---

### **RF-FAC-010: Registrar Ingresos Adicionales (No Ventas)**

**Descripción:**  
El sistema debe permitir registrar ingresos que no provienen de ventas de productos (ej: servicios adicionales, intereses ganados, alquileres, subsidios), categorizarlos correctamente, y reflejarlos en los reportes financieros.

**Criterios de Aceptación:**
1. Tipos de ingresos adicionales:
   - **Ingresos financieros**:
     - Intereses bancarios ganados
     - Rendimientos de inversiones
   - **Ingresos por servicios**:
     - Servicios de bar/catering en eventos externos
     - Asesorías o consultorías
   - **Ingresos por alquileres**:
     - Alquiler de espacio del local para eventos
     - Sublease de equipos
   - **Otros ingresos**:
     - Subsidios gubernamentales
     - Donaciones
     - Recuperación de gastos
     - Venta de activos (equipos usados)
2. Registro de ingreso:
   - **Información básica**:
     - Fecha del ingreso
     - Tipo de ingreso (dropdown según lista arriba)
     - Categoría contable:
       - Otros ingresos de gestión (75)
       - Ingresos financieros (77)
       - Ingresos excepcionales (76)
     - Descripción detallada
     - Monto (sin IGV)
     - IGV (si aplica)
     - Total
   - **Origen del ingreso**:
     - Cliente/pagador (si aplica): Nombre, RUC/DNI
     - Comprobante emitido:
       - Si se emitió factura/boleta: Vincular con el comprobante
       - Si es recibo interno: Adjuntar documento
     - Cuenta bancaria receptora (si aplica)
   - **Método de cobro**:
     - Transferencia bancaria
     - Efectivo
     - Cheque
     - Otro
3. Emisión de comprobante (si aplica):
   - Si el ingreso requiere comprobante:
     - Emitir factura o boleta por el servicio/alquiler
     - Vincular el ingreso con el comprobante emitido
     - El comprobante sigue el flujo de facturación electrónica
4. Impacto en contabilidad:
   - **Registro contable**:
     - Débito: Efectivo o Banco (aumento de activo)
     - Crédito: Otros ingresos (aumento de ingreso)
   - **Reportes financieros**:
     - Se incluye en el Estado de Resultados como "Otros ingresos"
     - No forma parte de "Ingresos por ventas" (separado para análisis)
5. Ingresos recurrentes:
   - **Configurar ingreso recurrente**:
     - Ejemplo: Intereses bancarios mensuales
     - Frecuencia: Mensual, trimestral, anual
     - Monto estimado
     - Cuenta bancaria
   - **Generación automática**:
     - El sistema sugiere registrar el ingreso en la fecha esperada
     - El usuario confirma y ajusta el monto real
6. Reportes de ingresos adicionales:
   - **Reporte de Otros Ingresos**:
     - Total por tipo de ingreso
     - Evolución temporal (gráfico)
     - % de representación en ingresos totales
   - **Análisis de diversificación**:
     - Medir dependencia de ingresos por ventas vs. otros
     - Identificar oportunidades de nuevos ingresos

**Consideraciones Multi-tenant:**
- Los ingresos adicionales son por negocio.
- Se registran en el contexto del negocio correspondiente.

**Seguridad:**
- Requiere permiso: `ingresos_adicionales_registrar`
- Auditoría de todos los ingresos registrados.

**UX:**
- Formulario similar al de gastos (consistencia).
- Plantillas de ingresos frecuentes.
- Vinculación automática con comprobantes emitidos.

**Reglas de Negocio:**
- Los ingresos adicionales deben estar respaldados con documentación.
- Si se emite comprobante, debe cumplir con normativa SUNAT.
- Los ingresos deben registrarse en el período contable en que se devengan.

---

### **RF-FAC-011: Gestionar Caja Chica**

**Descripción:**  
El sistema debe proporcionar un módulo de gestión de caja chica para controlar fondos destinados a gastos menores y urgentes, con asignación de fondos, registro de gastos, rendición de cuentas, y reposición periódica.

**Criterios de Aceptación:**
1. Configuración de caja chica:
   - **Creación de caja chica**:
     - Nombre: Ej: "Caja Chica Sede Centro"
     - Monto del fondo fijo: Ej: $200 USD
     - Responsable: Usuario asignado (cajero, administrador)
     - Límite por gasto individual: Ej: $50 USD
     - Categorías de gastos permitidos:
       - Transporte
       - Alimentos para personal
       - Suministros de oficina
       - Reparaciones menores
       - Otros gastos urgentes
   - **Asignación del fondo inicial**:
     - Registrar entrega de efectivo al responsable
     - Fecha de asignación
     - Comprobante de entrega (firma del responsable)
2. Registro de gastos de caja chica:
   - **Cada gasto debe registrarse**:
     - Fecha del gasto
     - Categoría (dropdown según configuración)
     - Descripción detallada
     - Monto
     - Comprobante de respaldo:
       - Tipo: Factura, boleta, ticket, recibo, sin comprobante
       - Adjuntar foto/scan
     - Beneficiario/proveedor (opcional)
   - **Validaciones**:
     - Monto <= Límite por gasto
     - Monto <= Saldo disponible en caja chica
     - Categoría permitida
   - **Actualización de saldo**:
     - Saldo disponible = Fondo asignado - Suma de gastos registrados
3. Rendición de cuentas:
   - **Proceso de rendición**:
     - El responsable presenta todos los comprobantes
     - El sistema genera reporte de gastos:
       - Lista de todos los gastos con comprobantes
       - Total gastado
       - Saldo remanente
       - Comprobantes faltantes (si hay)
     - El administrador revisa:
       - Verifica comprobantes físicos vs. sistema
       - Aprueba o solicita aclaraciones
   - **Aprobación de rendición**:
     - Si todo está correcto: Estado = `'rendido'`
     - Se genera documento de rendición (PDF)
     - Los gastos se trasladan a contabilidad formal
4. Reposición de caja chica:
   - **Solicitud de reposición**:
     - Cuando el saldo < 30% del fondo fijo, alertar
     - El responsable solicita reposición
     - Monto a reponer = Gastos rendidos
     - Ejemplo: Fondo $200, gastado $150, reponer $150 para volver a $200
   - **Aprobación y entrega**:
     - El gerente aprueba la reposición
     - Se entrega efectivo al responsable
     - El sistema registra la reposición
     - Saldo vuelve al fondo fijo original
5. Arqueo de caja chica:
   - **Verificación física**:
     - El supervisor cuenta el efectivo físico
     - Compara con saldo teórico del sistema
     - Registra:
       - Efectivo contado
       - Efectivo esperado (saldo teórico)
       - Diferencia (sobrante o faltante)
     - Si hay diferencia:
       - Solicitar justificación
       - Registrar en auditoría
       - Diferencias recurrentes pueden generar medidas disciplinarias
6. Reportes de caja chica:
   - **Reporte de Gastos de Caja Chica**:
     - Por período: semanal, mensual
     - Gastos por categoría
     - Promedio de gastos diarios
     - Comprobantes sin adjuntar (pendientes)
   - **Reporte de Rendiciones**:
     - Historial de rendiciones
     - Tiempo promedio entre rendiciones
     - Eficiencia del uso de caja chica
   - **Análisis de necesidad de caja chica**:
     - Si se usa poco, reducir el fondo
     - Si se agota rápido, aumentar el fondo

**Consideraciones Multi-tenant:**
- Cada sede puede tener su propia caja chica.
- Los fondos no se cruzan entre sedes.

**Seguridad:**
- Requiere permiso: `caja_chica_gestionar`
- El responsable es accountable de los fondos.
- Auditoría completa de gastos, rendiciones, y reposiciones.

**UX:**
- App móvil para registrar gastos sobre la marcha (con foto de comprobante).
- Indicador de saldo disponible siempre visible.
- Alerta cuando saldo < 30%.
- Proceso de rendición guiado paso a paso.

**Reglas de Negocio:**
- Todos los gastos > $20 USD deben tener comprobante obligatoriamente.
- Los gastos sin comprobante no son reembolsables ni deducibles.
- Las rendiciones deben hacerse cada 15 días máximo.
- El fondo de caja chica no debe usarse para gastos planificables (esos van por aprobación normal).

---

## **Submódulo 6.5.3: Reportes Financieros y Contabilidad**

---

### **RF-FAC-012: Generar Estado de Resultados (P&L)**

**Descripción:**  
El sistema debe generar automáticamente el Estado de Resultados (Profit & Loss Statement) mostrando ingresos, costos, gastos, y utilidad neta en un período determinado, permitiendo análisis de rentabilidad y toma de decisiones financieras.

**Criterios de Aceptación:**
1. Estructura del Estado de Resultados:
   - **Ingresos (Ventas)**:
     - Ventas brutas (todas las ventas del período)
     - Menos: Devoluciones y descuentos
     - = Ventas netas
   - **Costo de Ventas**:
     - Costo de productos vendidos (COGS)
     - Calculado vía FIFO (costo de lotes consumidos)
   - **Utilidad Bruta**:
     - = Ventas netas - Costo de ventas
     - Margen bruto % = (Utilidad bruta / Ventas netas) × 100
   - **Gastos Operativos**:
     - Gastos de administración (salarios, alquiler, servicios)
     - Gastos de ventas (marketing, comisiones)
     - Otros gastos operativos
     - = Total gastos operativos
   - **Utilidad Operativa (EBITDA)**:
     - = Utilidad bruta - Gastos operativos
   - **Otros Ingresos y Gastos**:
     - Ingresos financieros (intereses ganados)
     - Gastos financieros (intereses pagados)
     - Otros ingresos (no operativos)
     - Otros gastos (no operativos)
   - **Utilidad Antes de Impuestos (UAI)**:
     - = Utilidad operativa + Otros ingresos - Otros gastos
   - **Impuesto a la Renta** (29.5% en Perú):
     - Calculado sobre UAI
   - **Utilidad Neta**:
     - = UAI - Impuesto a la renta
     - Margen neto % = (Utilidad neta / Ventas netas) × 100
2. Selección de período:
   - **Períodos disponibles**:
     - Este mes
     - Mes anterior
     - Este trimestre
     - Este año
     - Año anterior
     - Rango personalizado (fecha inicio - fecha fin)
   - **Comparativas**:
     - Mes actual vs. mes anterior
     - Este año vs. año anterior
     - Mostrar variaciones en monto ($) y porcentaje (%)
3. Generación automática:
   - **Job mensual**:
     - El día 1 de cada mes, generar Estado de Resultados del mes anterior
     - Estado: `'generado'`
   - **Regeneración**:
     - Si hay correcciones (nuevas NC/ND, ajustes contables), regenerar
4. Detalle por concepto:
   - **Drill-down**:
     - Click en "Ventas netas" → Ver detalle de ventas por producto, categoría, sede
     - Click en "Gastos de administración" → Ver detalle de cada gasto
     - Click en "Costo de ventas" → Ver detalle de productos vendidos con su costo
5. Visualización:
   - **Formato vertical (tradicional)**:
     - Lista de conceptos con montos
     - Subtotales destacados
     - Utilidad neta al final (negrita, grande)
   - **Gráficos complementarios**:
     - Gráfico de cascada (Waterfall): Ventas → Costos → Gastos → Utilidad
     - Gráfico de pastel: Distribución de gastos por categoría
     - Gráfico de barras: Comparativa mensual de utilidad
6. Análisis de rentabilidad:
   - **Indicadores calculados**:
     - Margen bruto %
     - Margen operativo %
     - Margen neto %
     - EBITDA (Earnings Before Interest, Taxes, Depreciation, Amortization)
     - ROI (Return on Investment) si se tiene inversión inicial
   - **Benchmarks**:
     - Comparar con promedios del sector
     - Identificar si los márgenes son saludables
7. Exportación:
   - PDF: Reporte formal para presentación a stakeholders
   - Excel: Para análisis adicional
   - Envío automático por email (mensual) a gerencia y contador

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio Estado de Resultados.
- No se mezclan datos entre negocios.

**Seguridad:**
- Requiere permiso: `reportes_financieros_ver`
- Acceso restringido a gerencia, administración, contador.

**UX:**
- Dashboard visual atractivo.
- Números destacados: Utilidad neta en verde si positiva, rojo si negativa.
- Comparativas con flechas ↑↓ indicando mejora o deterioro.
- Vista responsive para tablets y móviles.

**Reglas de Negocio:**
- El Estado de Resultados es base devengado (registra ingresos y gastos cuando se devengan, no cuando se cobran/pagan).
- Los ajustes contables deben reflejarse en el período correspondiente.
- Los reportes históricos no se modifican (se generan nuevos si hay correcciones).

---

### **RF-FAC-013: Generar Flujo de Caja (Cash Flow)**

**Descripción:**  
El sistema debe generar el reporte de flujo de caja mostrando los movimientos reales de efectivo (entradas y salidas), permitiendo gestionar la liquidez del negocio y proyectar necesidades futuras de efectivo.

**Criterios de Aceptación:**
1. Estructura del Flujo de Caja:
   - **Saldo Inicial de Caja y Bancos**:
     - Efectivo en caja
     - Saldo en cuentas bancarias
     - Total disponible al inicio del período
   - **Entradas de Efectivo**:
     - **Operaciones**:
       - Cobros de ventas en efectivo
       - Cobros de ventas con tarjeta (cuando se acredita)
       - Cobros de cuentas por cobrar
     - **Otras entradas**:
       - Préstamos recibidos
       - Aportes de capital
       - Otros ingresos
     - = Total entradas
   - **Salidas de Efectivo**:
     - **Operaciones**:
       - Pagos a proveedores (compras)
       - Pagos de gastos operativos
       - Pagos de salarios
     - **Inversiones**:
       - Compra de activos (equipos, mobiliario)
     - **Financiamiento**:
       - Pago de préstamos (capital + intereses)
       - Dividendos pagados
     - **Otros**:
       - Pago de impuestos
       - Retiros de socios
     - = Total salidas
   - **Flujo Neto de Efectivo**:
     - = Total entradas - Total salidas
   - **Saldo Final de Caja y Bancos**:
     - = Saldo inicial + Flujo neto
2. Clasificación por actividades:
   - **Flujo de Operación** (actividades del día a día)
   - **Flujo de Inversión** (compra/venta de activos)
   - **Flujo de Financiamiento** (préstamos, aportes de capital)
   - Permite identificar de dónde proviene y hacia dónde va el efectivo
3. Proyección de flujo de caja:
   - **Flujo de caja proyectado** (próximos 30/60/90 días):
     - Basado en:
       - Cuentas por cobrar con fecha de vencimiento
       - Cuentas por pagar con fecha de vencimiento
       - Ventas promedio proyectadas
       - Gastos recurrentes conocidos
     - Identificar:
       - Períodos de superávit (mucho efectivo)
       - Períodos de déficit (falta de efectivo)
     - Alertar si se proyecta saldo negativo
4. Análisis de liquidez:
   - **Ratio de liquidez corriente**:
     - = Activo corriente / Pasivo corriente
     - Ideal: > 1.5 (tiene más activos líquidos que deudas a corto plazo)
   - **Días de caja** (cuántos días puede operar con el efectivo actual):
     - = Saldo de caja / Gastos operativos diarios promedio
     - Ejemplo: $5,000 / $200 = 25 días
   - **Capital de trabajo**:
     - = Activo corriente - Pasivo corriente
     - Mide la capacidad de cubrir obligaciones a corto plazo
5. Reconciliación con estado de resultados:
   - **Diferencias entre P&L y Cash Flow**:
     - El P&L puede mostrar utilidad pero el Cash Flow negativo (ventas a crédito no cobradas)
     - El Cash Flow puede ser positivo pero P&L negativo (cobros de períodos anteriores)
   - **Análisis de conversión**:
     - ¿Cuánto de la utilidad se convierte en efectivo?
     - Identificar problemas de cobranza o pagos adelantados
6. Visualización:
   - **Tabla de flujo de caja**:
     - Columnas: Concepto | Monto
     - Subtotales por sección
     - Saldo final destacado
   - **Gráfico de línea temporal**:
     - Evolución del saldo de caja día a día
     - Identificar picos y valles
   - **Gráfico de cascada**:
     - Saldo inicial → Entradas → Salidas → Saldo final
7. Alertas de liquidez:
   - **Saldo crítico**:
     - Si el saldo < monto mínimo configurado (ej: $1,000)
     - Alerta roja al gerente
   - **Proyección de déficit**:
     - Si se proyecta saldo negativo en próximos días
     - Sugerir acciones: Acelerar cobranzas, negociar pagos, buscar financiamiento

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio flujo de caja.
- Las cuentas bancarias son por negocio.

**Seguridad:**
- Requiere permiso: `reportes_financieros_flujo_ver`
- Acceso restringido (información sensible sobre liquidez).

**UX:**
- Dashboard con saldo actual en número grande.
- Semáforo de liquidez: 🟢 Saludable, 🟡 Atención, 🔴 Crítico.
- Proyección visual (gráfico) de próximos 30 días.
- Exportación a Excel para análisis de escenarios.

**Reglas de Negocio:**
- El flujo de caja es base caja (registra cuando se cobra/paga, no cuando se devenga).
- El saldo final de caja debe coincidir con arqueos físicos y saldos bancarios.
- La proyección es estimada, sujeta a cambios según comportamiento real.

---

### **RF-FAC-014: Generar Balance General (Estado de Situación Financiera)**

**Descripción:**  
El sistema debe generar el Balance General mostrando la situación financiera del negocio en un momento específico, con activos, pasivos, y patrimonio, permitiendo evaluar la solidez financiera y capacidad de endeudamiento.

**Criterios de Aceptación:**
1. Estructura del Balance General:
   - **ACTIVOS** (recursos que posee el negocio):
     - **Activo Corriente** (convertibles en efectivo < 1 año):
       - Caja y bancos
       - Cuentas por cobrar
       - Inventarios (productos)
       - Gastos pagados por adelantado
       - = Total activo corriente
     - **Activo No Corriente** (uso a largo plazo):
       - Muebles y equipos (valor de compra - depreciación acumulada)
       - Vehículos
       - Inmuebles (si aplica)
       - Inversiones a largo plazo
       - = Total activo no corriente
     - **TOTAL ACTIVOS**
   - **PASIVOS** (obligaciones/deudas):
     - **Pasivo Corriente** (vencimiento < 1 año):
       - Cuentas por pagar a proveedores
       - Préstamos a corto plazo
       - Impuestos por pagar
       - Salarios por pagar
       - = Total pasivo corriente
     - **Pasivo No Corriente** (vencimiento > 1 año):
       - Préstamos a largo plazo
       - Obligaciones por arrendamiento financiero
       - = Total pasivo no corriente
     - **TOTAL PASIVOS**
   - **PATRIMONIO** (capital propio):
     - Capital social (aportes de socios)
     - Utilidades retenidas (acumuladas de años anteriores)
     - Utilidad del ejercicio (del año actual)
     - = Total patrimonio
   - **TOTAL PASIVO + PATRIMONIO**
   - **Validación**: Total Activos = Total Pasivos + Patrimonio (ecuación contable fundamental)
2. Valoración de activos:
   - **Inventarios**:
     - Calculado automáticamente desde el módulo de inventario
     - Valor = Suma del costo de todos los productos en stock
   - **Cuentas por cobrar**:
     - Suma de facturas pendientes de cobro
     - Provisión por cuentas incobrables (opcional): % de deudas muy antiguas
   - **Activos fijos**:
     - Valor de compra registrado
     - Menos: Depreciación acumulada
     - Depreciación calculada según método:
       - Línea recta: (Costo - Valor residual) / Vida útil
       - Ejemplo: Computadora $1,000, vida útil 4 años, depreciación anual $250
3. Valoración de pasivos:
   - **Cuentas por pagar**:
     - Suma de facturas de proveedores pendientes de pago
   - **Préstamos**:
     - Saldo pendiente del préstamo
     - Clasificar en corto plazo (vence < 1 año) y largo plazo (vence > 1 año)
   - **Impuestos por pagar**:
     - IGV por pagar (débito fiscal - crédito fiscal)
     - Impuesto a la renta por pagar
4. Análisis de ratios financieros:
   - **Liquidez**:
     - Ratio corriente = Activo corriente / Pasivo corriente
     - Prueba ácida = (Activo corriente - Inventarios) / Pasivo corriente
   - **Endeudamiento**:
     - Ratio de endeudamiento = Total pasivos / Total activos
     - Apalancamiento = Total pasivos / Patrimonio
   - **Solidez**:
     - Patrimonio como % del total de activos
5. Comparativas:
   - **Balance actual vs. períodos anteriores**:
     - Este mes vs. mes anterior
     - Este año vs. año anterior
     - Identificar crecimiento en activos, aumento de deudas, cambios en patrimonio
6. Visualización:
   - **Formato vertical**:
     - Activos a la izquierda, Pasivos + Patrimonio a la derecha
     - Subtotales destacados
     - Totales en negrita
   - **Gráficos**:
     - Gráfico de barras: Activos vs. Pasivos + Patrimonio
     - Gráfico de pastel: Composición de activos (cuánto es inventario, cuánto es caja, etc.)
     - Gráfico de pastel: Composición de pasivo + patrimonio
7. Generación periódica:
   - **Balance mensual**: Último día de cada mes
   - **Balance anual**: 31 de diciembre
   - Estado: `'generado'`

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio Balance General.
- Los activos y pasivos son independientes por negocio.

**Seguridad:**
- Requiere permiso: `reportes_financieros_balance_ver`
- Acceso muy restringido (información estratégica).

**UX:**
- Dashboard con números clave destacados.
- Comparativas con variaciones en monto y porcentaje.
- Exportación a PDF (formato formal) y Excel.

**Reglas de Negocio:**
- El Balance siempre debe estar balanceado (Activos = Pasivos + Patrimonio).
- Los activos fijos deben depreciarse según normativa contable.
- Los activos y pasivos deben clasificarse correctamente en corriente y no corriente.

---

### **RF-FAC-015: Calcular y Declarar Impuestos (IGV, Renta)**

**Descripción:**  
El sistema debe calcular automáticamente los impuestos a pagar (IGV mensual, Impuesto a la Renta anual), generar los datos para la declaración en PDT, y proporcionar trazabilidad completa para auditorías fiscales.

**Criterios de Aceptación:**
1. Cálculo de IGV mensual:
   - **IGV de ventas (Débito Fiscal)**:
     - Suma del IGV de todas las ventas del mes
     - Fuente: Registro de Ventas (facturas y boletas)
     - Tasa: 18% (16% IGV + 2% IPM)
   - **IGV de compras (Crédito Fiscal)**:
     - Suma del IGV de todas las compras del mes
     - Fuente: Registro de Compras (facturas de proveedores)
     - Solo compras relacionadas con el giro del negocio (deducibles)
   - **IGV a pagar o saldo a favor**:
     - IGV a pagar = Débito fiscal - Crédito fiscal
     - Si es positivo: Pagar a SUNAT
     - Si es negativo: Saldo a favor (aplicar al siguiente mes)
   - **Retenciones y percepciones** (si aplica):
     - Retenciones de IGV (si el negocio es agente de retención)
     - Percepciones de IGV (si aplica)
     - Ajustar el IGV a pagar según estos conceptos
2. Declaración mensual PDT 621:
   - **Generación automática de datos**:
     - El sistema prepara todos los datos necesarios:
       - Ventas gravadas con IGV
       - Ventas exoneradas
       - Exportaciones (si aplica)
       - Compras gravadas con IGV
       - Crédito fiscal del mes
       - IGV a pagar
     - Generar archivo prellenado para PDT 621
   - **Exportación**:
     - Archivo en formato compatible con PDT SUNAT
     - O datos en Excel para transcripción manual
   - **Vencimiento**:
     - Según último dígito del RUC
     - Calendario de vencimientos configurado
     - Alerta 5 días antes del vencimiento
3. Cálculo de Impuesto a la Renta anual:
   - **Renta de Tercera Categoría** (empresas):
     - Base imponible = Utilidad del ejercicio (del Estado de Resultados anual)
     - Ajustes:
       - Agregar: Gastos no deducibles
       - Deducir: Ingresos no gravados
       - Depreciación según SUNAT (vs. contable)
     - Tasa: 29.5% (para utilidades > 15 UIT)
   - **Pagos a cuenta mensuales**:
     - Sistema de coeficiente o sistema de porcentaje (1.5%)
     - Calcular pago a cuenta cada mes
     - Acumular pagos realizados durante el año
   - **Declaración anual**:
     - Impuesto anual calculado
     - Menos: Pagos a cuenta realizados
     - Resultado: Saldo a pagar o saldo a favor
4. Otros impuestos:
   - **Impuesto Temporal a los Activos Netos (ITAN)** (si activos netos > 1 millón):
     - Calculado sobre activos netos al 31 de diciembre
     - Tasa: 0.4%
   - **Impuesto a las Transacciones Financieras (ITF)**:
     - 0.005% sobre transacciones bancarias
     - Descontado automáticamente por bancos
5. Gestión de declaraciones:
   - **Vista de declaraciones**:
     - Lista de declaraciones por mes/año
     - Estado: Pendiente, Presentada, Pagada
     - Fecha de vencimiento
     - Monto a pagar
     - Número de orden de pago (si se pagó)
   - **Registro de pago**:
     - Al pagar en banco/portal SUNAT:
       - Registrar número de operación
       - Adjuntar voucher
       - Estado → `'pagada'`
       - Registrar en gastos (para contabilidad)
6. Auditoría fiscal:
   - **Documentación completa**:
     - Todos los comprobantes emitidos
     - Todos los comprobantes de compras
     - Libros electrónicos
     - Declaraciones presentadas
     - Comprobantes de pago de impuestos
   - **Reporte de auditoría**:
     - Resumen anual para auditor externo o SUNAT
     - Conciliación de ventas, compras, e impuestos
     - Evidencia de cumplimiento normativo

**Consideraciones Multi-tenant:**
- Cada negocio calcula sus impuestos independientemente.
- Cada negocio tiene su propio RUC y obligaciones fiscales.

**Seguridad:**
- Requiere permiso: `impuestos_gestionar`
- Acceso restringido a contador y administrador.
- Auditoría completa de cálculos y declaraciones.

**UX:**
- Dashboard de impuestos con resumen mensual.
- Alerta visual: "IGV vence el 15 de febrero".
- Calculadora automática: Ingresar ventas y compras, obtener IGV a pagar.
- Exportación directa a formato PDT.

**Reglas de Negocio:**
- Las declaraciones deben presentarse en las fechas de vencimiento (multas por atraso).
- Los pagos de impuestos son gastos no deducibles (se pagan con utilidad después de impuestos).
- Los saldos a favor de IGV pueden aplicarse a meses futuros o solicitarse devolución.
- Las declaraciones rectificatorias pueden presentarse si hay errores (con multa reducida si es voluntaria).

---

## **Submódulo 6.5.4: Conciliación Bancaria y Tesorería**

---

### **RF-FAC-016: Registrar Cuentas Bancarias y Movimientos**

**Descripción:**  
El sistema debe permitir registrar las cuentas bancarias del negocio, importar extractos bancarios, y registrar manualmente movimientos bancarios (depósitos, transferencias, retiros) para control de tesorería y conciliación.

**Criterios de Aceptación:**
1. Configuración de cuentas bancarias:
   - **Registro de cuenta**:
     - Banco (dropdown: BCP, BBVA, Interbank, Scotiabank, etc.)
     - Tipo de cuenta: Corriente, Ahorros
     - Número de cuenta
     - Moneda: PEN (Soles), USD (Dólares)
     - Titular de la cuenta (negocio)
     - Saldo inicial (al momento de configurar)
     - Estado: Activa, Inactiva, Cerrada
     - Uso: Operaciones diarias, Ahorros, Impuestos, Proveedores, etc.
   - **Múltiples cuentas**:
     - El negocio puede tener N cuentas bancarias
     - Cada cuenta se gestiona independientemente
2. Registro manual de movimientos:
   - **Tipos de movimientos**:
     - **Ingreso** (crédito):
       - Depósito en efectivo
       - Transferencia recibida
       - Cobro con tarjeta (cuando se acredita)
       - Intereses ganados
     - **Salida** (débito):
       - Retiro de efectivo
       - Transferencia enviada
       - Pago a proveedor
       - Pago de impuestos
       - Comisiones bancarias
       - Pago con cheque
   - **Información a capturar**:
     - Fecha del movimiento (fecha valor)
     - Tipo de movimiento (dropdown)
     - Monto
     - Número de operación (único del banco)
     - Concepto/descripción
     - Tercero (de quién se recibió / a quién se pagó)
     - Categoría (si se vincula a un gasto/ingreso registrado)
     - Comprobante (adjuntar voucher/comprobante)
   - **Actualización de saldo**:
     - Saldo = Saldo anterior + Ingresos - Salidas
     - El sistema calcula automáticamente
3. Importación de extractos bancarios:
   - **Formatos soportados**:
     - Excel (.xlsx)
     - CSV
     - Formato de cada banco (mapeo configurable)
   - **Proceso de importación**:
     - Subir archivo de extracto
     - Mapear columnas:
       - Fecha → Columna A
       - Descripción → Columna B
       - Débito → Columna C
       - Crédito → Columna D
       - Saldo → Columna E
     - Vista previa de datos
     - Confirmar importación
   - **Validaciones**:
     - No duplicar movimientos ya importados (validar por número de operación + fecha + monto)
     - Verificar que el saldo final del extracto coincide con el saldo calculado
   - **Clasificación automática**:
     - El sistema intenta clasificar movimientos según:
       - Descripción del banco (ej: "TRANSF DE JUAN PEREZ" → Ingreso por venta)
       - Montos conocidos (ej: $1,500 = pago de alquiler mensual)
     - Los movimientos sin clasificar quedan como `'sin_clasificar'` para revisión manual
4. Vinculación con operaciones:
   - **Vincular movimiento con registro existente**:
     - Movimiento de ingreso → Vincular con venta/cobro de factura
     - Movimiento de salida → Vincular con gasto/pago a proveedor
   - **Beneficios de vinculación**:
     - Trazabilidad completa
     - Facilita conciliación bancaria
     - Valida que los registros contables tienen respaldo bancario
5. Vista de cuenta bancaria:
   - **Detalle de cuenta**:
     - Información de la cuenta (banco, número, etc.)
     - Saldo actual (destacado en número grande)
     - Últimos movimientos (lista)
     - Gráfico de evolución del saldo (línea temporal)
   - **Filtros**:
     - Por rango de fechas
     - Por tipo de movimiento (ingresos / salidas)
     - Por monto (> $X)
     - Por clasificación (clasificados / sin clasificar)
   - **Acciones**:
     - Agregar movimiento manual
     - Importar extracto
     - Descargar historial (Excel)
     - Conciliar (ver RF-FAC-017)
6. Reportes de movimientos bancarios:
   - **Reporte de Ingresos y Salidas por Cuenta**:
     - Total de ingresos del mes
     - Total de salidas del mes
     - Saldo inicial vs. saldo final
     - Movimiento neto
   - **Análisis de flujo por cuenta**:
     - Identificar cuenta más usada
     - Cuenta con mayor saldo
     - Cuenta con movimientos más frecuentes

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus propias cuentas bancarias.
- Las cuentas no se comparten entre negocios.

**Seguridad:**
- Requiere permiso: `tesoreria_cuentas_gestionar`
- Los datos bancarios son altamente sensibles.
- Solo personal de tesorería y administración tienen acceso.
- Auditoría completa de todos los movimientos.

**UX:**
- Dashboard de cuentas con saldo de cada una.
- Importación de extractos drag-and-drop.
- Clasificación de movimientos con autocompletado.
- Colores: Verde para ingresos, rojo para salidas.

**Reglas de Negocio:**
- Todos los movimientos bancarios deben estar respaldados con voucher.
- Los saldos bancarios deben cuadrar con los extractos del banco.
- Los movimientos sin clasificar deben revisarse semanalmente.
- Las comisiones bancarias son gastos financieros deducibles.

---

### **RF-FAC-017: Realizar Conciliación Bancaria**

**Descripción:**  
El sistema debe facilitar el proceso de conciliación bancaria comparando los registros internos de movimientos con los extractos bancarios, identificando diferencias, partidas en tránsito, y errores, asegurando que los saldos contables coincidan con los saldos bancarios.

**Criterios de Aceptación:**
1. Inicio de conciliación:
   - **Seleccionar**:
     - Cuenta bancaria
     - Período a conciliar (mes/año)
   - El sistema muestra:
     - Saldo según libros (registros internos) al final del mes
     - Saldo según banco (extracto bancario) al final del mes
     - Diferencia (si la hay)
2. Proceso de conciliación:
   - **Pantalla dividida**:
     - Lado izquierdo: Movimientos según libros (sistema)
     - Lado derecho: Movimientos según banco (extracto importado)
   - **Matching automático**:
     - El sistema intenta emparejar movimientos:
       - Mismo monto
       - Fechas cercanas (±3 días de tolerancia)
       - Mismo tipo (ingreso/salida)
     - Los movimientos emparejados se marcan como ✓ Conciliados
   - **Matching manual**:
     - El usuario selecciona movimiento del libro
     - Selecciona movimiento del extracto
     - Hace clic en "Emparejar"
     - Los movimientos se marcan como conciliados
3. Identificación de diferencias:
   - **Partidas en tránsito**:
     - **Cheques emitidos no cobrados**:
       - Registrados en libros pero no aparecen en extracto
       - Son salidas pendientes
     - **Depósitos en tránsito**:
       - Registrados en libros pero no acreditados en banco
       - Son ingresos pendientes
   - **Errores u omisiones**:
     - Movimientos solo en libros (no en banco):
       - Posible error de registro
       - O transacción que no se efectuó
     - Movimientos solo en banco (no en libros):
       - Comisiones bancarias no registradas
       - Intereses ganados no registrados
       - Errores del banco (poco común)
       - Transacciones fraudulentas (alerta)
4. Ajustes de conciliación:
   - **Registrar movimientos faltantes**:
     - Si hay movimiento en banco no registrado:
       - Botón "Registrar en libros"
       - Capturar información y crear el movimiento
       - Automáticamente se empareja y concilia
   - **Anular movimientos erróneos**:
     - Si hay movimiento en libros que no se efectuó:
       - Marcar como "Anulado" o "Revertido"
       - Ajustar el saldo
5. Cálculo del saldo conciliado:
   - **Fórmula de conciliación**:
     - Saldo según banco (extracto)
     - Más: Depósitos en tránsito (en libros, no en banco)
     - Menos: Cheques no cobrados (en libros, no en banco)
     - Más/Menos: Ajustes identificados
     - = Saldo conciliado (debe igualar saldo según libros ajustado)
6. Documentación de la conciliación:
   - **Reporte de conciliación**:
     - Saldo inicial según libros
     - Saldo inicial según banco
     - Lista de movimientos conciliados
     - Lista de partidas en tránsito
     - Ajustes realizados
     - Saldo final conciliado
   - **Estado de conciliación**:
     - Conciliado ✓ (saldos cuadran)
     - Con diferencias ⚠️ (pendiente de resolver)
   - **Firma/aprobación**:
     - Preparado por: (usuario que hizo la conciliación)
     - Revisado por: (supervisor)
     - Fecha de conciliación
7. Generación y almacenamiento:
   - **Guardar conciliación**:
     - Se crea registro de conciliación mensual
     - Estado: `'conciliado'` o `'pendiente'`
     - PDF del reporte de conciliación
     - Se archiva para auditorías
8. Alertas y seguimiento:
   - **Diferencias no resueltas**:
     - Si después de conciliar sigue habiendo diferencia:
       - Alertar al contador
       - Requiere investigación
       - No cerrar el mes hasta resolver
   - **Transacciones sospechosas**:
     - Movimientos grandes no registrados
     - Movimientos duplicados
     - Patrones anormales
     - Alertar para revisión de seguridad

**Consideraciones Multi-tenant:**
- Cada negocio concilia sus propias cuentas independientemente.

**Seguridad:**
- Requiere permiso: `tesoreria_conciliacion_realizar`
- Acceso restringido a contador y tesorero.
- Auditoría de ajustes realizados durante conciliación.

**UX:**
- Vista lado a lado (libros vs. banco).
- Drag-and-drop para emparejar movimientos.
- Código de colores: Verde (conciliados), amarillo (pendientes), rojo (diferencias).
- Progreso: "85% conciliado (120 de 142 movimientos)".

**Reglas de Negocio:**
- La conciliación debe realizarse mensualmente (buena práctica).
- Los saldos deben cuadrar (diferencia = 0) antes de cerrar el mes.
- Las diferencias > $100 USD deben investigarse inmediatamente.
- Los ajustes de conciliación deben tener justificación documentada.

---

### **RF-FAC-018: Gestionar Transferencias entre Cuentas**

**Descripción:**  
El sistema debe permitir registrar transferencias de fondos entre las cuentas bancarias del negocio, actualizando automáticamente los saldos de ambas cuentas y evitando duplicar ingresos o gastos.

**Criterios de Aceptación:**
1. Registro de transferencia:
   - **Información a capturar**:
     - Fecha de la transferencia
     - Cuenta origen (dropdown de cuentas del negocio)
     - Cuenta destino (dropdown de cuentas del negocio)
     - Monto transferido
     - Moneda (si es conversión entre PEN y USD, registrar tipo de cambio)
     - Concepto/motivo de la transferencia
     - Número de operación (del banco)
     - Comisión bancaria (si aplica)
     - Comprobante (voucher de la transferencia)
   - **Validaciones**:
     - Cuenta origen != Cuenta destino
     - Saldo en cuenta origen >= Monto + Comisión
     - Fecha no puede ser futura
2. Registro automático de movimientos:
   - **En cuenta origen**:
     - Tipo: Salida
     - Concepto: "Transferencia a [Cuenta destino]"
     - Monto: - (Monto + Comisión)
     - Estado: Vinculado a transferencia interna
   - **En cuenta destino**:
     - Tipo: Ingreso
     - Concepto: "Transferencia desde [Cuenta origen]"
     - Monto: + Monto
     - Estado: Vinculado a transferencia interna
   - **Vínculo entre movimientos**:
     - Los dos movimientos se vinculan entre sí
     - ID de transferencia único
     - Permite rastrear origen y destino
3. Actualización de saldos:
   - **Cuenta origen**:
     - Saldo -= (Monto + Comisión)
   - **Cuenta destino**:
     - Saldo += Monto
4. Tratamiento contable:
   - **No afecta ingresos ni gastos**:
     - Es un movimiento de activos (de una cuenta a otra)
     - No se registra en Estado de Resultados
   - **Solo afecta si hay comisión bancaria**:
     - La comisión SÍ es un gasto financiero
     - Se registra como gasto operativo
5. Transferencias con conversión de moneda:
   - **Si origen es PEN y destino es USD (o viceversa)**:
     - Capturar tipo de cambio del día
     - Ejemplo: Transferir $100 USD a cuenta en soles
       - Tipo de cambio: 3.75 PEN por USD
       - Salida en USD: -$100
       - Ingreso en PEN: +$375
     - Registrar diferencia cambiaria (ganancia o pérdida)
6. Vista de transferencias:
   - **Lista de transferencias**:
     - Fecha, origen, destino, monto, estado
     - Filtros por fecha, cuenta, monto
   - **Detalle de transferencia**:
     - Información completa
     - Movimientos vinculados en cada cuenta
     - Comprobante adjunto
7. Reversión de transferencia:
   - **Si se registró por error**:
     - Botón "Revertir transferencia"
     - Requiere autorización
     - Se crean movimientos inversos:
       - Devolver dinero a cuenta origen
       - Descontar de cuenta destino
     - Se marca transferencia original como "Revertida"
     - Se crea nueva transferencia de reversión

**Consideraciones Multi-tenant:**
- Solo se pueden hacer transferencias entre cuentas del mismo negocio.
- No se permite transferir entre negocios diferentes.

**Seguridad:**
- Requiere permiso: `tesoreria_transferencias_gestionar`
- Transferencias > $5,000 USD requieren aprobación de gerente.
- Auditoría completa de transferencias y reversiones.

**UX:**
- Formulario simple de 3 pasos: Origen → Destino → Confirmar.
- Vista previa del impacto en saldos antes de confirmar.
- Notificación de éxito con resumen de movimientos.

**Reglas de Negocio:**
- Las transferencias internas no generan ingresos ni gastos (salvo comisiones).
- Toda transferencia debe tener voucher de respaldo.
- Las transferencias entre monedas deben usar tipo de cambio oficial del día.
- No se pueden hacer transferencias si el saldo origen es insuficiente.

---

### **RF-FAC-019: Proyectar Necesidades de Liquidez**

**Descripción:**  
El sistema debe analizar el flujo de caja histórico, las cuentas por cobrar/pagar proyectadas, y las ventas/gastos esperados para proyectar las necesidades futuras de efectivo, alertando sobre posibles déficits de liquidez y sugiriendo acciones preventivas.

**Criterios de Aceptación:**
1. Análisis de datos históricos:
   - **Ingresos históricos**:
     - Ventas promedio por día/semana/mes (últimos 3-6 meses)
     - Estacionalidad (meses altos y bajos)
     - Tendencia (creciente, estable, decreciente)
   - **Gastos históricos**:
     - Gastos operativos promedio mensuales
     - Gastos variables (ligados a ventas)
     - Gastos fijos (alquiler, salarios, servicios)
   - **Ciclo de conversión de efectivo**:
     - Días promedio de cobro (cuentas por cobrar)
     - Días promedio de pago (cuentas por pagar)
     - Días de rotación de inventario
2. Proyección de ingresos:
   - **Ventas proyectadas**:
     - Basado en promedio histórico + tendencia
     - Ajustado por estacionalidad
     - Ejemplo: Si diciembre vende 50% más que promedio, proyectar con ese factor
   - **Cobros de cuentas por cobrar**:
     - Facturas con fecha de vencimiento en próximos 30/60/90 días
     - Aplicar % de cobranza esperada (ej: 90% se cobra a tiempo, 10% se atrasa)
   - **Otros ingresos esperados**:
     - Préstamos aprobados
     - Aportes de capital programados
3. Proyección de salidas:
   - **Pagos de cuentas por pagar**:
     - Facturas de proveedores con vencimiento próximo
   - **Gastos recurrentes**:
     - Alquiler, servicios, salarios (fechas fijas conocidas)
   - **Gastos proyectados**:
     - Compras de inventario necesarias (según rotación)
     - Impuestos por pagar (fechas de vencimiento conocidas)
   - **Inversiones planeadas**:
     - Compra de equipos
     - Ampliación del local
4. Proyección del flujo de caja (próximos 90 días):
   - **Tabla de proyección semanal/mensual**:
     - Columnas: Semana 1, Semana 2, ..., Semana 12 (3 meses)
     - Filas:
       - Saldo inicial
       - Ingresos proyectados
       - Salidas proyectadas
       - Flujo neto
       - Saldo final
   - **Identificar déficits**:
     - Si saldo final < 0: Déficit de liquidez
     - Si saldo final < monto mínimo: Alerta de liquidez baja
5. Análisis de escenarios:
   - **Escenario optimista**:
     - Ventas +20%, cobros puntuales, gastos controlados
   - **Escenario pesimista**:
     - Ventas -20%, cobros atrasados, gastos inesperados
   - **Escenario realista**:
     - Basado en promedio histórico
   - Visualizar los 3 escenarios en gráfico comparativo
6. Alertas y recomendaciones:
   - **Alerta de déficit proyectado**:
     - "Se proyecta déficit de $3,500 USD en semana 6"
     - Color rojo en la celda del saldo
   - **Recomendaciones automáticas**:
     - "Acelerar cobranza de facturas vencidas"
     - "Negociar extensión de plazo con proveedor X"
     - "Considerar línea de crédito bancaria de $5,000 USD"
     - "Posponer compra de equipo programada para semana 4"
     - "Reducir gastos variables en 15%"
7. Visualización:
   - **Gráfico de línea temporal**:
     - Eje X: Semanas
     - Eje Y: Saldo de caja
     - Línea del saldo proyectado
     - Línea horizontal del saldo mínimo deseado
     - Zona de déficit en rojo
   - **Semáforo de liquidez**:
     - 🟢 Saludable: Saldo > mínimo por >30 días
     - 🟡 Atención: Saldo cerca del mínimo en <30 días
     - 🔴 Crítico: Déficit proyectado en <15 días
8. Acciones preventivas sugeridas:
   - **Mejorar cobranza**:
     - Enviar recordatorios a clientes con facturas por vencer
     - Ofrecer descuentos por pronto pago
   - **Negociar pagos**:
     - Solicitar extensión de plazo a proveedores con saldo alto
     - Negociar plan de pagos
   - **Obtener financiamiento**:
     - Solicitar línea de crédito bancaria
     - Considerar factoring (vender facturas por cobrar)
   - **Controlar gastos**:
     - Reducir gastos no esenciales
     - Posponer inversiones no urgentes

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propia proyección de liquidez.
- Los análisis son independientes por negocio.

**Seguridad:**
- Requiere permiso: `tesoreria_proyeccion_ver`
- Acceso restringido a gerencia y tesorería.

**UX:**
- Dashboard visual con gráfico principal.
- Tabla de proyección editable (ajustar supuestos).
- Slider para cambiar entre escenarios.
- Alertas destacadas con colores.

**Reglas de Negocio:**
- Las proyecciones son estimaciones, sujetas a cambio.
- Deben revisarse semanalmente y ajustarse según realidad.
- Los déficits proyectados requieren acción inmediata (no esperar a que ocurran).
- El saldo mínimo debe cubrir al menos 1 mes de gastos operativos (buena práctica).

---

Este es el **Módulo V: Facturación y Finanzas** completo con 19 requisitos funcionales distribuidos en 4 submódulos. El documento cubre todos los aspectos críticos de facturación electrónica, gestión de gastos e ingresos, reportes financieros, y tesorería para una licorería multi-tenant en Perú.
