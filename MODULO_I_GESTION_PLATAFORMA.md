# MÓDULO I: GESTIÓN DE PLATAFORMA

**Sistema:** DrinkGo - Sistema Multi-tenant para Licorerías  
**Versión:** 1.0  
**Fecha:** 31 de enero de 2026

---

## Descripción General del Módulo

Este módulo gestiona las funcionalidades de administración de la plataforma SaaS, incluyendo la gestión de negocios (tenants), planes de suscripción, facturación de suscripciones, y el panel de SuperAdmin para monitoreo y administración de todos los negocios en la plataforma.

---

## RF-PLT-001: Registrar Nuevo Negocio (Tenant)

**Descripción:**  
El sistema debe permitir a un SuperAdmin o mediante un proceso de auto-registro crear nuevos negocios (tenants) en la plataforma, configurando sus datos básicos, plan de suscripción inicial, y generando automáticamente la estructura necesaria para su operación independiente.

**Criterios de Aceptación:**
1. El formulario de registro incluye:
   - Información del negocio: Razón social, nombre comercial, RUC/NIT, país, dirección fiscal, teléfono, email
   - Información del administrador principal: Nombre, apellidos, email (será el usuario admin inicial), teléfono
   - Plan de suscripción seleccionado (Free Trial, Basic, Professional, Enterprise)
   - Configuración inicial: Moneda predeterminada (PEN, USD, EUR), zona horaria, idioma
2. El sistema valida:
   - RUC/NIT único en la plataforma (no puede haber duplicados)
   - Email del administrador único
   - Formato de datos correctos
3. Al confirmar el registro:
   - Se crea el registro en la tabla `negocio` con estado `'activo'`
   - Se genera un identificador único del negocio (`negocio_id`)
   - Se crea el usuario administrador en la tabla `usuario` con rol `'Admin'`
   - Se asigna el plan de suscripción en `historial_suscripcion`
   - Se crea una sede principal por defecto
   - Se configura el catálogo de permisos y roles básicos
   - Se envía email de bienvenida con credenciales de acceso
4. Si es plan de prueba (Free Trial):
   - Duración: 30 días
   - Después de 30 días, requiere actualización a plan pagado o se suspende
5. El sistema genera automáticamente:
   - Estructura de base de datos aislada por `negocio_id`
   - Configuraciones por defecto (horarios, políticas, etc.)

**Consideraciones Multi-tenant:**
- Cada negocio es completamente independiente
- Aislamiento estricto de datos a nivel de `negocio_id`
- No hay visibilidad cruzada entre negocios

**Seguridad:**
- Solo SuperAdmin puede crear negocios manualmente
- El proceso de auto-registro requiere validación de email
- Las contraseñas se hashean con bcrypt
- Se generan tokens de activación únicos

**UX:**
- Wizard de 3 pasos: Datos del negocio → Administrador → Plan y confirmación
- Indicadores de progreso visual
- Validación en tiempo real de campos
- Sugerencias automáticas (ej: autocompletar datos desde SUNAT con RUC)

**Reglas de Negocio:**
- Un negocio requiere al menos un usuario administrador
- El plan Free Trial no requiere método de pago inicialmente
- Los planes pagados requieren configuración de método de pago antes de activarse
- El RUC debe ser válido según la SUNAT (Perú) o entidad correspondiente

---

## RF-PLT-002: Gestionar Planes de Suscripción

**Descripción:**  
El sistema debe permitir al SuperAdmin configurar y gestionar los diferentes planes de suscripción disponibles en la plataforma, definiendo características, límites, precios, y periodicidad de cada plan.

**Criterios de Aceptación:**
1. Cada plan de suscripción incluye:
   - Nombre del plan (ej: "Free Trial", "Basic", "Professional", "Enterprise")
   - Descripción detallada
   - Precio (puede ser 0 para Free Trial)
   - Moneda (PEN, USD, EUR)
   - Periodicidad: Mensual, Trimestral, Anual
   - Límites y características:
     - Número máximo de usuarios
     - Número máximo de sedes/sucursales
     - Número máximo de productos en catálogo
     - Almacenamiento de documentos (GB)
     - Funcionalidades incluidas (módulos activos)
     - Soporte técnico (email, chat, prioritario, dedicado)
   - Estado: Activo, Inactivo, Deprecado
   - Visibilidad: Público (visible para nuevos clientes), Privado (solo por invitación)
2. El sistema permite:
   - Crear nuevos planes
   - Editar planes existentes (sin afectar suscripciones activas)
   - Desactivar planes (los negocios con ese plan continúan, pero no se permite nuevas contrataciones)
   - Versionar planes (crear nueva versión manteniendo la anterior para clientes legacy)
3. Comparativa de planes:
   - Tabla comparativa visual mostrando características de cada plan
   - Recomendación automática según tamaño del negocio
4. Descuentos y promociones:
   - Descuento por pago anual (ej: 2 meses gratis)
   - Cupones de descuento aplicables
   - Precios especiales por región o tipo de negocio

**Consideraciones Multi-tenant:**
- Los planes son globales, no por tenant
- Cada tenant puede estar en un plan diferente

**Seguridad:**
- Solo SuperAdmin puede crear/editar planes
- Los cambios en planes activos requieren confirmación especial
- Auditoría completa de cambios en planes

**UX:**
- Interfaz tipo "pricing page" para visualización de planes
- Comparativa lado a lado de características
- Calculadora de precios según necesidades

**Reglas de Negocio:**
- Un plan deprecado no se puede reactivar
- Los negocios en planes deprecados pueden permanecer o deben migrar a nuevo plan
- El plan Free Trial es único y no renovable
- Los cambios de precio en planes solo aplican a nuevas suscripciones

---

## RF-PLT-003: Cambiar Plan de Suscripción

**Descripción:**  
El sistema debe permitir a los negocios actualizar (upgrade) o degradar (downgrade) su plan de suscripción, calculando ajustes prorrateados, validando límites del nuevo plan, y aplicando los cambios de manera inmediata o al final del período actual.

**Criterios de Aceptación:**
1. El administrador del negocio accede a "Cambiar Plan"
2. Se muestra el plan actual y los planes disponibles
3. Al seleccionar un nuevo plan, el sistema muestra:
   - Comparativa de características (actual vs. nuevo)
   - Diferencia de precio
   - Cálculo de prorrateo:
     - Si es upgrade a mitad de mes: Se cobra la diferencia prorrateada
     - Si es downgrade: Se genera crédito para el próximo período
   - Fecha de aplicación:
     - Inmediato: Los cambios aplican de inmediato
     - Fin de período: Los cambios aplican al renovar
4. Validaciones antes de cambiar:
   - **Para downgrade**: Verificar que el uso actual no excede los límites del nuevo plan
     - Ejemplo: Si el nuevo plan permite 5 usuarios y actualmente hay 8, se debe desactivar 3 usuarios primero
     - Ejemplo: Si el nuevo plan permite 2 sedes y hay 4, se debe desactivar 2 sedes
   - Si hay excesos, se muestra mensaje detallado de qué debe ajustarse
5. Proceso de cambio:
   - Se actualiza el registro en `historial_suscripcion`
   - Se calcula y registra el monto de ajuste (cargo o crédito)
   - Se actualizan los límites del negocio
   - Se notifica al administrador por email
   - Si es upgrade inmediato con pago requerido, se procesa el pago
6. Restricciones de cambio:
   - No se puede downgrade si hay facturas pendientes de pago
   - El cambio a plan inferior no es inmediato si implicaría pérdida de funcionalidad crítica activa

**Consideraciones Multi-tenant:**
- El cambio de plan solo afecta al negocio que lo solicita
- No afecta a otros negocios en la plataforma

**Seguridad:**
- Solo el administrador del negocio o SuperAdmin pueden cambiar el plan
- Los cambios que implican cargos requieren confirmación de método de pago
- Auditoría completa del cambio (quién, cuándo, de qué plan a qué plan)

**UX:**
- Vista comparativa clara de "Plan Actual vs. Plan Nuevo"
- Calculadora de costos en tiempo real
- Wizard guiado si hay ajustes necesarios (desactivar usuarios/sedes)
- Confirmación final con resumen de cambios

**Reglas de Negocio:**
- Los upgrades pueden ser inmediatos, los downgrades típicamente se aplican al final del período
- El prorrateo se calcula en días (precio mensual / 30 días × días restantes)
- Los créditos por downgrade no son reembolsables, solo aplicables al siguiente pago
- No se permiten cambios de plan durante los primeros 7 días de un período pagado (periodo de garantía)

---

## RF-PLT-004: Facturar Suscripciones Automáticamente

**Descripción:**  
El sistema debe generar automáticamente facturas de suscripción mensual/anual para cada negocio, procesar el pago mediante el método configurado, y gestionar casos de pago fallido con reintentos y notificaciones.

**Criterios de Aceptación:**
1. Job automático que se ejecuta diariamente:
   - Identifica suscripciones próximas a renovar (7 días antes)
   - Envía recordatorio de renovación próxima al administrador del negocio
2. El día de renovación:
   - Se genera factura en `factura_suscripcion`:
     - Número de factura (ej: FS-2026-001234)
     - Negocio (tenant)
     - Plan de suscripción
     - Período facturado (ej: Febrero 2026)
     - Monto base
     - Descuentos aplicados
     - IGV/IVA (según país)
     - Total
     - Fecha de emisión
     - Fecha de vencimiento (ej: 15 días después)
     - Estado: `'pendiente'`
   - Se intenta procesar el pago automáticamente:
     - Se utiliza el método de pago configurado (tarjeta guardada, débito automático)
     - Se integra con gateway de pagos (Stripe, PayPal, etc.)
3. Estados del pago:
   - **Exitoso**:
     - Estado de factura cambia a `'pagada'`
     - Se actualiza `fecha_vencimiento` de la suscripción (próximo mes/año)
     - Se envía recibo por email
     - El negocio continúa operando normalmente
   - **Fallido**:
     - Estado de factura permanece en `'pendiente'`
     - Se registra el intento fallido con motivo (tarjeta rechazada, fondos insuficientes, etc.)
     - Se programa reintento automático:
       - 1er reintento: 3 días después
       - 2do reintento: 7 días después
       - 3er reintento: 10 días después
     - Se notifica al administrador del negocio cada vez
4. Gestión de morosidad:
   - Si pasan 15 días después del vencimiento sin pago:
     - Estado del negocio cambia a `'suspendido'`
     - Los usuarios no pueden acceder al sistema (solo ver notificación de pago pendiente)
     - Los datos se mantienen seguros (no se eliminan)
   - Si pasan 60 días sin pago:
     - Se escala a proceso de cobranza
     - Se puede bloquear permanentemente
5. Proceso de pago manual:
   - Si el pago automático falla, el administrador puede:
     - Pagar con otra tarjeta manualmente
     - Realizar transferencia bancaria (se sube comprobante para validación)
     - Contactar a soporte para arreglos de pago

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propio ciclo de facturación independiente
- Las facturas son por negocio, no consolidadas

**Seguridad:**
- Los datos de tarjetas se almacenan tokenizados (no en la BD directa)
- Cumplimiento PCI DSS para manejo de pagos
- Comunicación encriptada con gateways de pago

**UX:**
- Dashboard del negocio muestra estado de suscripción:
   - "Activa - Próxima renovación: 15/02/2026"
   - "Pago pendiente - Vence en 3 días"
   - "Suspendida - Pago requerido"
- Notificaciones claras por email con link directo para pagar
- Historial de facturas descargable

**Reglas de Negocio:**
- La facturación se genera siempre, incluso si el pago falla (obligación tributaria)
- Los reintentos de pago no generan cargos adicionales
- Los negocios suspendidos por falta de pago pueden reactivarse inmediatamente al pagar
- Las suscripciones anuales se facturan en un solo pago (opción de pago en cuotas puede configurarse)

---

## RF-PLT-005: Suspender o Cancelar Suscripción

**Descripción:**  
El sistema debe permitir suspender temporalmente o cancelar definitivamente la suscripción de un negocio, gestionando el acceso al sistema, la retención de datos, y los procesos de reembolso o finalización según corresponda.

**Criterios de Aceptación:**
1. **Suspensión temporal** (por el negocio o por SuperAdmin):
   - **Iniciada por el negocio**:
     - El administrador solicita "Pausar suscripción"
     - Motivo: Vacaciones, temporada baja, reestructuración
     - Se puede pausar por 1-6 meses
     - Durante la pausa:
       - No se cobran mensualidades
       - Los usuarios no pueden acceder (modo read-only opcional)
       - Los datos se mantienen intactos
       - No se pierde el historial
   - **Iniciada por SuperAdmin**:
     - Motivos: Falta de pago, violación de términos, investigación
     - Similar a suspensión por falta de pago automática
   - **Reactivación**:
     - El negocio solicita reactivar
     - Se valida que no haya deudas pendientes
     - Se reactiva inmediatamente o en fecha programada
     - Estado vuelve a `'activo'`
2. **Cancelación definitiva** (solo por el negocio):
   - El administrador solicita "Cancelar suscripción"
   - El sistema pregunta motivo (opcional pero recomendado para estadísticas):
     - Muy costoso
     - No cumplió expectativas
     - Cambió de proveedor
     - Cerró el negocio
     - Otro
   - Se muestra advertencia: "Esta acción cancelará tu suscripción. Tus datos se conservarán por 90 días por si cambias de opinión"
   - Proceso de cancelación:
     - Estado cambia a `'cancelado'`
     - Se calcula si hay reembolso (según política):
       - Si es suscripción anual y cancela a mitad de año: Reembolso prorrateado (opcional según política)
       - Si es suscripción mensual: No hay reembolso
     - Se genera última factura de cierre si hay créditos pendientes
     - Los usuarios pierden acceso inmediato
     - Los datos se mantienen en modo "archivado" por 90 días
   - Después de 90 días:
     - Se puede exportar backup completo de datos
     - Opcionalmente se eliminan los datos (según política de retención)
3. **Retención de datos**:
   - Durante suspensión: Datos intactos y accesibles al reactivar
   - Durante cancelación: 
     - 0-30 días: Reactivación instantánea sin costo
     - 31-90 días: Reactivación con cargo de reactivación
     - > 90 días: Datos eliminados (previo aviso), requiere nuevo registro

**Consideraciones Multi-tenant:**
- La suspensión/cancelación solo afecta al negocio específico
- Los datos están aislados y protegidos durante todo el proceso

**Seguridad:**
- La cancelación definitiva requiere confirmación por email (doble verificación)
- Solo el administrador del negocio puede cancelar (no usuarios normales)
- SuperAdmin puede suspender pero no cancelar (solo el negocio puede cancelar)
- Auditoría completa del proceso de suspensión/cancelación

**UX:**
- Proceso de cancelación con pasos claros
- Encuesta de salida (para mejorar el servicio)
- Opciones de retención: "¿Qué podemos hacer para que te quedes?"
- Exportación de datos antes de cancelar (opción de descarga completa)

**Reglas de Negocio:**
- No se puede cancelar si hay facturas pendientes de pago (se debe saldar primero)
- La suspensión temporal no está disponible en plan Free Trial
- Los reembolsos (si aplican) se procesan en 10-15 días hábiles
- Los negocios cancelados pierden su RUC/identificador único (si se re-registran, es como nuevo negocio)

---

## RF-PLT-006: Monitorear Uso de Recursos por Negocio

**Descripción:**  
El sistema debe proporcionar al SuperAdmin herramientas de monitoreo en tiempo real del uso de recursos de cada negocio, permitiendo identificar abusos, planificar capacidad, y hacer cumplir los límites del plan de suscripción.

**Criterios de Aceptación:**
1. Métricas monitoreadas por negocio:
   - **Usuarios**: 
     - Usuarios activos vs. límite del plan
     - Usuarios conectados en tiempo real
     - Pico de usuarios concurrentes
   - **Sedes/Sucursales**:
     - Sedes activas vs. límite del plan
   - **Productos en catálogo**:
     - Productos activos vs. límite del plan
   - **Almacenamiento**:
     - Espacio utilizado (documentos, imágenes, backups)
     - Espacio disponible
     - % de uso
   - **Transacciones**:
     - Ventas procesadas (por día/mes)
     - Pedidos procesados
     - Comprobantes emitidos
   - **API Calls** (si aplica):
     - Número de llamadas a APIs externas
     - Rate limiting
2. Dashboard de SuperAdmin:
   - Lista de negocios con indicadores de uso
   - Visualización tipo tabla:
     - Nombre del negocio
     - Plan actual
     - Uso de recursos (barras de progreso)
     - Estado (Activo, Suspendido, etc.)
     - Alertas (si hay excesos o problemas)
   - Filtros:
     - Por plan
     - Por estado
     - Por nivel de uso (> 80% de recursos, etc.)
   - Ordenamiento por consumo de recursos
3. Alertas automáticas:
   - **Al negocio**: Cuando se acerca al límite del plan (80%, 90%, 100%)
     - "Has usado 8 de 10 usuarios permitidos. Considera actualizar tu plan"
   - **Al SuperAdmin**: Cuando un negocio excede límites repetidamente
     - "Negocio X ha intentado crear más usuarios del límite 15 veces este mes"
4. Enforcement de límites:
   - Límites "duros" (hard limits): No se permite exceder
     - Ejemplo: Si el plan permite 10 usuarios, no se puede crear el usuario #11
     - Mensaje: "Has alcanzado el límite de usuarios de tu plan. Actualiza tu plan para agregar más"
   - Límites "suaves" (soft limits): Se permite exceder temporalmente con aviso
     - Ejemplo: Almacenamiento al 105% (se permite por unos días con notificación)
5. Reportes de uso:
   - "Uso de Recursos por Negocio" (histórico)
   - "Negocios cerca del límite de su plan" (oportunidad de venta de upgrade)
   - "Crecimiento de uso por negocio" (tendencia)
   - "Negocios con uso anormalmente alto" (posible abuso o error)

**Consideraciones Multi-tenant:**
- Cada negocio tiene métricas independientes
- El SuperAdmin ve métricas agregadas de todos los negocios

**Seguridad:**
- Solo SuperAdmin tiene acceso a métricas globales
- Los negocios solo ven sus propias métricas
- Las métricas no exponen datos sensibles de transacciones (solo conteos)

**UX:**
- Dashboard visual con gráficos de uso
- Código de colores: Verde (uso normal), amarillo (80-95%), rojo (> 95%)
- Alertas destacadas en panel principal
- Exportación de reportes a Excel/PDF

**Reglas de Negocio:**
- Las métricas se actualizan en tiempo real (o con delay de 5 minutos máximo)
- Los límites del plan se revisan antes de cada operación crítica (crear usuario, subir archivo, etc.)
- El exceso de recursos debe corregirse en 7 días o se aplica upgrade forzoso o suspensión
- Las alertas se envían máximo una vez al día por tipo (evitar spam)

---

## RF-PLT-007: Generar Reportes Consolidados de la Plataforma

**Descripción:**  
El sistema debe proporcionar al SuperAdmin reportes consolidados con métricas clave de toda la plataforma, permitiendo analizar el crecimiento, ingresos, salud del negocio SaaS, y tomar decisiones estratégicas.

**Criterios de Aceptación:**
1. Métricas principales (KPIs):
   - **Crecimiento**:
     - Total de negocios activos en la plataforma
     - Nuevos negocios registrados (mes actual)
     - Tasa de crecimiento mensual (%)
     - Negocios por plan (distribución)
   - **Ingresos**:
     - MRR (Monthly Recurring Revenue): Ingresos recurrentes mensuales
     - ARR (Annual Recurring Revenue): Ingresos recurrentes anuales
     - Ingresos totales del mes
     - Promedio de ingresos por negocio (ARPU - Average Revenue Per User)
   - **Retención**:
     - Churn rate: % de negocios que cancelaron
     - Tasa de retención: % de negocios que renovaron
     - LTV (Lifetime Value): Valor promedio de vida de un negocio
   - **Salud financiera**:
     - Negocios al día con pagos
     - Negocios con pagos pendientes
     - Monto total por cobrar
     - Tasa de cobro exitoso
   - **Uso de la plataforma**:
     - Usuarios totales en la plataforma
     - Transacciones procesadas (ventas)
     - Comprobantes emitidos
     - Almacenamiento total utilizado
2. Reportes disponibles:
   - **Dashboard Ejecutivo**:
     - Cards con números grandes: Total negocios, MRR, ARR, Churn
     - Gráficos de tendencia temporal
     - Comparativa mes actual vs. mes anterior
   - **Reporte de Crecimiento**:
     - Nuevos registros por mes (gráfico de barras)
     - Tasa de conversión de Free Trial a planes pagos
     - Fuentes de adquisición (si se trackea)
   - **Reporte Financiero**:
     - Ingresos por plan (gráfico de pastel)
     - Ingresos por país/región
     - Proyección de ingresos (basado en suscripciones activas)
     - Estado de cobros (al día, vencidos, morosos)
   - **Reporte de Retención y Churn**:
     - Negocios que cancelaron (lista con motivos)
     - Análisis de cohortes: Retención mes a mes por cohorte
     - Razones de cancelación más comunes
   - **Reporte de Uso de Plataforma**:
     - Actividad por negocio (transacciones, usuarios activos)
     - Negocios más activos vs. menos activos
     - Funcionalidades más utilizadas
3. Filtros y segmentación:
   - Por rango de fechas
   - Por plan de suscripción
   - Por país/región
   - Por estado del negocio (activo, suspendido, cancelado)
4. Exportación:
   - PDF para presentaciones ejecutivas
   - Excel para análisis detallado
   - CSV para importar a otras herramientas (BI, CRM)

**Consideraciones Multi-tenant:**
- Los reportes son agregados de todos los negocios
- No se exponen datos sensibles de negocios individuales (solo agregados)

**Seguridad:**
- Solo SuperAdmin tiene acceso a reportes consolidados
- Los datos financieros son altamente sensibles
- Exportaciones se auditan (quién exportó qué y cuándo)

**UX:**
- Dashboard interactivo estilo Business Intelligence
- Gráficos modernos y profesionales
- Drill-down: Click en un KPI para ver detalle
- Comparativas temporales claras (↑ 15% vs mes anterior)

**Reglas de Negocio:**
- Los KPIs se calculan en tiempo real (con cache de 1 hora para performance)
- El MRR se calcula normalizando todas las suscripciones a equivalente mensual (anuales / 12)
- El churn rate se calcula como: (Cancelaciones del mes / Negocios activos al inicio del mes) × 100
- Los reportes históricos se conservan indefinidamente para análisis de tendencias

---

## RF-PLT-008: Configurar Parámetros Globales de la Plataforma

**Descripción:**  
El sistema debe permitir al SuperAdmin configurar parámetros globales que afectan a toda la plataforma, como tasas de impuestos, métodos de pago disponibles, integraciones con servicios externos, y políticas generales.

**Criterios de Aceptación:**
1. Configuraciones disponibles:
   - **Impuestos**:
     - Tasa de IGV/IVA por país (ej: Perú 18%, México 16%)
     - Aplicación de impuestos (incluido en precio, o se suma)
   - **Métodos de pago**:
     - Habilitar/deshabilitar métodos globalmente: Tarjeta, PayPal, Yape, Plin, Transferencia
     - Configurar comisiones por método de pago
   - **Integraciones externas**:
     - Gateway de pagos: Credenciales de Stripe, PayPal, Mercado Pago
     - SUNAT (Perú): Credenciales para facturación electrónica
     - Email service: SMTP, SendGrid, AWS SES
     - SMS service: Twilio, Nexmo
     - Almacenamiento: AWS S3, Google Cloud Storage
   - **Políticas de la plataforma**:
     - Duración del Free Trial (días)
     - Período de gracia antes de suspender por falta de pago (días)
     - Retención de datos después de cancelación (días)
     - Política de reembolsos
   - **Límites técnicos**:
     - Tamaño máximo de archivo upload (MB)
     - Rate limiting de API (requests por minuto)
     - Duración de sesiones de usuario (minutos)
   - **Notificaciones**:
     - Email remitente por defecto
     - Plantillas de emails globales
     - Frecuencia de notificaciones automáticas
2. Interfaz de configuración:
   - Organizada por categorías (Impuestos, Pagos, Integraciones, etc.)
   - Cada configuración tiene:
     - Nombre descriptivo
     - Campo de valor
     - Descripción/ayuda
     - Valor por defecto
     - Botón "Restaurar valor por defecto"
   - Validación de formatos (ej: URLs, emails, porcentajes)
3. Cambios y versionado:
   - Los cambios se guardan con confirmación
   - Se registra historial de cambios:
     - Qué se cambió
     - Valor anterior → Valor nuevo
     - Quién lo cambió
     - Fecha y hora
   - Opción de revertir a configuración anterior
4. Impacto de cambios:
   - Algunos cambios son inmediatos (ej: cambiar tasa de impuesto)
   - Otros requieren reinicio de servicios (se alerta)
   - Se notifica a equipos internos de cambios críticos

**Consideraciones Multi-tenant:**
- Los parámetros globales afectan a todos los negocios
- Algunos parámetros pueden tener overrides por negocio (ej: tasa de impuesto específica)

**Seguridad:**
- Solo SuperAdmin puede modificar configuraciones globales
- Credenciales de servicios externos se almacenan encriptadas
- Cambios críticos requieren doble confirmación
- Auditoría completa de todos los cambios

**UX:**
- Interfaz tipo "Settings" con tabs por categoría
- Búsqueda de configuraciones por nombre
- Indicadores de "Configuración requerida" si falta algo crítico
- Exportación de configuración actual (backup)

**Reglas de Negocio:**
- Los cambios en tasas de impuestos solo aplican a nuevas transacciones (no retroactivo)
- Las credenciales de integraciones deben validarse antes de guardar (test de conexión)
- Algunos parámetros tienen valores mínimos/máximos permitidos (ej: Free Trial no puede ser > 90 días)
- Los cambios se propagan a todos los negocios en máximo 5 minutos

---

## RF-PLT-009: Auditar Actividad de Usuarios y Sistema

**Descripción:**  
El sistema debe registrar automáticamente todas las acciones críticas realizadas por usuarios y procesos del sistema, proporcionando al SuperAdmin herramientas de auditoría para investigar incidentes, detectar anomalías, y cumplir con requisitos de compliance.

**Criterios de Aceptación:**
1. Eventos auditados:
   - **Autenticación**:
     - Login exitoso/fallido
     - Logout
     - Cambio de contraseña
     - Recuperación de contraseña
     - Bloqueo de cuenta por intentos fallidos
   - **Gestión de usuarios**:
     - Creación de usuario
     - Modificación de usuario (rol, permisos, datos)
     - Desactivación/eliminación de usuario
     - Asignación de permisos
   - **Operaciones críticas de negocio**:
     - Creación/modificación/eliminación de productos
     - Ajustes de inventario (especialmente negativos)
     - Ventas y cancelaciones
     - Emisión de comprobantes
     - Reembolsos y notas de crédito
     - Cambios en precios
   - **Operaciones de plataforma**:
     - Registro de nuevo negocio
     - Cambio de plan de suscripción
     - Suspensión/cancelación de suscripción
     - Cambios en configuraciones globales
     - Emisión de facturas de suscripción
   - **Acceso a datos sensibles**:
     - Visualización de reportes financieros
     - Exportación de datos
     - Acceso a información de clientes
2. Información registrada en cada evento:
   - **Quién**: Usuario que realizó la acción (usuario_id, nombre)
   - **Qué**: Descripción de la acción (ej: "Modificó precio de producto #123")
   - **Cuándo**: Fecha y hora exacta (timestamp)
   - **Dónde**: IP del usuario, ubicación geográfica (si disponible)
   - **Cómo**: Navegador/dispositivo (user agent)
   - **Contexto**:
     - Negocio afectado (si aplica)
     - Entidad afectada (ej: producto_id, venta_id)
     - Valores antes y después (para modificaciones)
   - **Resultado**: Éxito o fallo, mensaje de error si aplica
3. Almacenamiento de auditoría:
   - Tabla dedicada: `auditoria` con campos completos
   - Indexación por usuario, fecha, acción, negocio
   - Retención: Mínimo 5 años (requisito de compliance)
   - Respaldo periódico en almacenamiento externo inmutable
4. Herramientas de consulta de auditoría:
   - **Búsqueda de eventos**:
     - Por usuario
     - Por tipo de acción
     - Por rango de fechas
     - Por negocio
     - Por IP
   - **Filtros avanzados**:
     - Combinar múltiples criterios
     - Buscar por texto libre en descripción
   - **Vista de timeline**:
     - Línea temporal de eventos
     - Agrupación por usuario o entidad
   - **Exportación**:
     - CSV/Excel para análisis externo
     - PDF para reportes de auditoría
5. Detección de anomalías:
   - Alertas automáticas:
     - Múltiples logins fallidos (posible ataque)
     - Login desde ubicación inusual
     - Acciones inusuales (ej: eliminación masiva de productos)
     - Modificaciones fuera de horario habitual
   - Dashboard de seguridad:
     - Eventos sospechosos recientes
     - Usuarios con mayor actividad
     - Intentos de acceso bloqueados
6. Cumplimiento (Compliance):
   - Reportes de auditoría para autoridades reguladoras
   - Exportación de auditoría de un negocio específico (para cliente)
   - Certificación de integridad de logs (hash criptográfico)

**Consideraciones Multi-tenant:**
- Los logs de auditoría están separados por negocio
- SuperAdmin puede ver logs de todos los negocios
- Los negocios solo ven sus propios logs

**Seguridad:**
- Los logs de auditoría son inmutables (no se pueden editar o eliminar)
- Solo SuperAdmin tiene acceso completo a auditoría
- Los logs se replican a almacenamiento externo inmutable (prevención de tampering)
- Encriptación de datos sensibles en logs

**UX:**
- Interfaz de búsqueda tipo "Log Viewer"
- Filtros rápidos predefinidos: "Logins fallidos últimas 24h", "Cambios de precios", etc.
- Vista detallada al hacer click en un evento
- Timeline visual de eventos secuenciales

**Reglas de Negocio:**
- Todos los eventos críticos DEBEN auditarse (no opcional)
- Los logs de auditoría son evidencia legal en caso de disputas
- El acceso a logs de auditoría se audita también (meta-auditoría)
- Los logs antiguos (> 5 años) pueden archivarse en almacenamiento frío pero no eliminarse

---

## Resumen del Módulo

El **Módulo I: Gestión de Plataforma** proporciona las funcionalidades fundamentales para la operación de la plataforma SaaS multi-tenant, incluyendo:

- **9 requisitos funcionales** (RF-PLT-001 a RF-PLT-009)
- Gestión completa del ciclo de vida de negocios (registro, suscripción, suspensión, cancelación)
- Facturación automatizada con manejo de pagos y morosidad
- Monitoreo de recursos y cumplimiento de límites de planes
- Reportes ejecutivos para toma de decisiones
- Auditoría comprehensiva para seguridad y compliance

Este módulo es la base que permite la operación escalable y segura de múltiples negocios independientes en una sola plataforma centralizada.

---

**Fin del Módulo I**
