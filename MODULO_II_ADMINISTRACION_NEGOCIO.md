# MÓDULO II: ADMINISTRACIÓN DEL NEGOCIO

**Sistema:** DrinkGo - Sistema Multi-tenant para Licorerías  
**Versión:** 1.0  
**Fecha:** 31 de enero de 2026

---

## Descripción General del Módulo

Este módulo gestiona las funcionalidades de administración interna de cada negocio (tenant), incluyendo la configuración de sedes, almacenes, gestión de usuarios y permisos, roles de seguridad, y configuraciones generales del negocio. Cada negocio administra estas funcionalidades de forma independiente.

---

## Submódulo 6.2.1: Configuración de Negocio y Sedes

---

### RF-ADM-001: Configurar Información del Negocio

**Descripción:**  
El sistema debe permitir al administrador del negocio actualizar y mantener la información corporativa completa, incluyendo datos fiscales, de contacto, identidad visual, y configuraciones operativas que se aplicarán a todas las sedes del negocio.

**Criterios de Aceptación:**
1. El formulario de configuración incluye:
   - **Información fiscal**:
     - Razón social (no editable después del registro, solo por SuperAdmin)
     - Nombre comercial
     - RUC/NIT (no editable)
     - Dirección fiscal completa
     - Representante legal (nombre, DNI)
     - Régimen tributario (General, MYPE, RUS)
   - **Información de contacto**:
     - Teléfono principal
     - Email principal
     - Sitio web
     - Redes sociales (Facebook, Instagram, WhatsApp Business)
   - **Identidad visual**:
     - Logo del negocio (PNG/JPG, max 2MB)
     - Colores corporativos (primario, secundario)
     - Descripción del negocio (texto libre, 500 caracteres)
   - **Configuraciones operativas**:
     - Moneda predeterminada (PEN, USD, EUR) - aplicable a todas las sedes
     - Zona horaria
     - Idioma del sistema
     - Formato de fecha (DD/MM/YYYY, MM/DD/YYYY)
     - Primer día de la semana (Lunes, Domingo)
2. El sistema valida:
   - Formatos de datos (email, URL, teléfono)
   - Tamaño y formato de imágenes
   - Unicidad de datos críticos (RUC)
3. Al guardar cambios:
   - Se actualizan los datos en la tabla `negocio`
   - Se registra en auditoría quién hizo el cambio y cuándo
   - Los cambios se reflejan inmediatamente en toda la aplicación
   - El logo actualizado aparece en comprobantes, reportes, y header del sistema
4. Vistas previas:
   - Preview del logo en diferentes contextos (header, ticket, factura)
   - Preview de colores corporativos aplicados a la interfaz

**Consideraciones Multi-tenant:**
- Cada negocio gestiona su propia información independientemente
- Los cambios no afectan a otros negocios en la plataforma

**Seguridad:**
- Solo usuarios con permiso `negocio_configurar` pueden editar
- Los cambios en datos fiscales (RUC, razón social) requieren validación de SuperAdmin
- Auditoría completa de cambios

**UX:**
- Formulario organizado en tabs por categoría
- Validación en tiempo real
- Vista previa de logo al subirlo
- Picker de colores visual para colores corporativos
- Opción de "Restaurar logo por defecto"

**Reglas de Negocio:**
- El logo es obligatorio (si no se sube, se usa logo por defecto)
- Los cambios en moneda predeterminada no afectan transacciones históricas
- El RUC no puede cambiarse después del registro (integridad fiscal)
- Los datos fiscales son obligatorios para emisión de comprobantes

---

### RF-ADM-002: Gestionar Sedes/Sucursales

**Descripción:**  
El sistema debe permitir al negocio crear y administrar múltiples sedes o sucursales, cada una con su propia configuración operativa, dirección, horarios, y equipo de trabajo, facilitando la operación de negocios multi-sede.

**Criterios de Aceptación:**
1. El formulario de creación de sede incluye:
   - **Información básica**:
     - Nombre de la sede (ej: "Sede Centro", "Sucursal Norte")
     - Código interno (opcional, alfanumérico)
     - Tipo: Tienda física, Solo delivery, Almacén central
     - Estado: Activa, Inactiva, En construcción
   - **Ubicación**:
     - Dirección completa
     - Distrito/Provincia/Departamento
     - Referencia
     - Coordenadas GPS (latitud, longitud) - para delivery
     - Zona de cobertura de delivery (si aplica)
   - **Contacto**:
     - Teléfono de la sede
     - Email de la sede
     - Encargado/gerente de sede (usuario asignado)
   - **Horarios de atención**:
     - Por cada día de la semana: Hora apertura - Hora cierre
     - Días no laborables (configurar excepciones)
     - Horario de venta de alcohol (específico por legislación)
2. El sistema valida:
   - Nombre de sede único dentro del negocio
   - Coordenadas GPS válidas
   - Horarios coherentes (hora apertura < hora cierre)
   - Al menos una sede debe estar activa en el negocio
3. Al crear la sede:
   - Se genera un registro en `sede`
   - Se crea automáticamente un almacén principal para esa sede
   - Se configura la zona de delivery por defecto (si aplica)
   - Se envía notificación al gerente asignado
4. Gestión de sedes:
   - Lista de sedes con información resumida
   - Filtros por estado, tipo
   - Búsqueda por nombre
   - Acciones: Ver detalle, Editar, Desactivar, Eliminar (solo si no tiene transacciones)
5. Vista de mapa:
   - Mapa mostrando todas las sedes del negocio con pins
   - Click en pin muestra información de la sede
   - Útil para planificar expansión o analizar cobertura

**Consideraciones Multi-tenant:**
- Las sedes pertenecen exclusivamente al negocio que las crea
- No hay visibilidad cruzada entre negocios

**Seguridad:**
- Requiere permiso: `sedes_gestionar`
- Solo gerentes y administradores pueden crear/editar sedes
- La eliminación de sede con transacciones está bloqueada (integridad de datos)

**UX:**
- Wizard de 3 pasos: Información básica → Ubicación → Horarios
- Selector de ubicación con Google Maps integrado
- Configurador visual de horarios (drag & drop)
- Tarjetas tipo "card" para visualizar sedes

**Reglas de Negocio:**
- Un negocio en plan Basic puede tener máximo 2 sedes (según límites del plan)
- La sede principal no puede desactivarse mientras haya otras activas
- Los horarios de venta de alcohol deben cumplir legislación local
- Las sedes inactivas no aparecen en la web/app de clientes

---

### RF-ADM-003: Configurar Almacenes por Sede

**Descripción:**  
El sistema debe permitir crear y gestionar múltiples almacenes dentro de cada sede, definiendo su ubicación física, tipo de almacenamiento, capacidad, y responsable, para organizar el inventario de manera eficiente.

**Criterios de Aceptación:**
1. El formulario de creación de almacén incluye:
   - **Información básica**:
     - Nombre del almacén (ej: "Almacén Principal", "Refrigerador 1", "Bodega Sótano")
     - Código interno (alfanumérico, único por sede)
     - Sede a la que pertenece (dropdown)
     - Tipo: Principal, Secundario, Refrigerado, Exhibición
     - Estado: Activo, Inactivo, Mantenimiento
   - **Ubicación física**:
     - Piso/Nivel
     - Área/Zona
     - Capacidad aproximada (m³ o descripción)
   - **Características**:
     - Tiene refrigeración: Sí/No
     - Temperatura controlada: Sí/No (rango de temperatura)
     - Acceso restringido: Sí/No
   - **Responsable**:
     - Usuario encargado del almacén (rol: Almacenero)
     - Usuario suplente
2. El sistema valida:
   - Nombre único dentro de la sede
   - Código único dentro de la sede
   - Sede activa
   - Usuario responsable tiene rol de almacenero
3. Al crear el almacén:
   - Se genera registro en `almacen`
   - Se vincula a la sede correspondiente
   - El inventario de ese almacén inicia en 0
   - Se notifica al responsable asignado
4. Gestión de almacenes:
   - Lista de almacenes por sede
   - Vista de jerarquía: Sede → Almacenes
   - Filtros por tipo, estado
   - Acciones: Ver inventario, Editar, Desactivar
5. Restricciones:
   - Cada sede debe tener al menos un almacén activo
   - Los productos con temperatura controlada solo pueden almacenarse en almacenes refrigerados

**Consideraciones Multi-tenant:**
- Los almacenes son parte de la estructura del negocio
- No hay cruces entre negocios

**Seguridad:**
- Requiere permiso: `almacenes_gestionar`
- Los almaceneros solo pueden ver/gestionar sus almacenes asignados
- Auditoría de creación y modificaciones

**UX:**
- Vista de árbol: Sede → Almacenes → Inventario
- Iconos visuales por tipo de almacén (❄️ refrigerado, 📦 general, etc.)
- Indicador de capacidad utilizada (barra de progreso)

**Reglas de Negocio:**
- El almacén principal de cada sede no puede desactivarse
- Los almacenes con inventario > 0 no pueden eliminarse
- Los productos alcohólicos pueden estar en cualquier almacén
- Los productos perecederos deben estar en almacenes con control de temperatura

---

### RF-ADM-004: Configurar Horarios y Días Laborables

**Descripción:**  
El sistema debe permitir configurar los horarios de operación de cada sede, incluyendo horarios generales, horarios especiales para venta de alcohol, días festivos, y excepciones, asegurando cumplimiento legal y gestión operativa.

**Criterios de Aceptación:**
1. Configuración de horarios por sede:
   - **Horario general de atención**:
     - Por cada día de la semana:
       - Abierto: Sí/No (checkbox)
       - Hora de apertura (HH:MM)
       - Hora de cierre (HH:MM)
       - Horario partido: Opción de configurar descanso (ej: 13:00-15:00 cerrado)
   - **Horario de venta de alcohol** (legislación):
     - Días permitidos (puede ser diferente al horario general)
     - Hora inicio de venta
     - Hora fin de venta
     - Restricciones especiales (ej: domingos hasta 18:00, no después de 23:00 entre semana)
   - **Horarios especiales**:
     - Fechas específicas con horario diferente
     - Ejemplo: "24 de diciembre: 08:00-14:00", "Año nuevo: Cerrado"
2. Gestión de días festivos:
   - Lista de días festivos del país (pre-cargada)
   - Opción de agregar festivos locales
   - Para cada festivo:
     - Fecha
     - Nombre del festivo
     - Abierto/Cerrado
     - Si está abierto: Horario especial
   - Marcado de "Ley Seca" (no venta de alcohol en días de elecciones, etc.)
3. Validaciones:
   - Hora de apertura < Hora de cierre
   - Horario de venta de alcohol dentro del horario general
   - Cumplimiento con legislación local (alertas si se configura fuera de lo permitido)
4. Aplicación de horarios:
   - **En POS**:
     - El sistema bloquea ventas de alcohol fuera del horario permitido
     - Mensaje: "No se puede vender alcohol fuera del horario permitido (Lun-Sáb 08:00-23:00)"
   - **En pedidos online**:
     - Se muestra "Cerrado" si está fuera de horario
     - Se muestra horario de próxima apertura
     - Los pedidos de alcohol se rechazan fuera de horario
   - **En reservas de mesa**:
     - Solo se permiten reservas dentro del horario de atención
5. Vista para clientes:
   - Página de "Horarios" mostrando horario de cada sede
   - Indicador en tiempo real: "Abierto ahora" / "Cerrado - Abre mañana a las 08:00"

**Consideraciones Multi-tenant:**
- Cada sede del negocio tiene sus propios horarios
- Los horarios no se comparten entre negocios

**Seguridad:**
- Requiere permiso: `sede_horarios_configurar`
- Los cambios en horarios de alcohol se auditan (cumplimiento legal)

**UX:**
- Configurador visual tipo calendario
- Toggle switches para cada día (abierto/cerrado)
- Time pickers para seleccionar horas
- Vista previa: "Cómo se verá para los clientes"
- Plantillas de horarios (ej: "Lunes a Viernes 08:00-22:00, Sábado 09:00-23:00, Domingo cerrado")

**Reglas de Negocio:**
- Los horarios de venta de alcohol son obligatorios y deben cumplir ley local
- En días de elecciones (Ley Seca), el sistema bloquea automáticamente venta de alcohol
- Los horarios especiales sobrescriben los horarios regulares
- Se recomienda configurar horarios con 15 minutos de margen para cierre (tiempo de atención al último cliente)

---

### RF-ADM-005: Configurar Cajas/Terminales de Venta

**Descripción:**  
El sistema debe permitir configurar y gestionar las cajas o terminales de punto de venta (POS) de cada sede, asignando hardware, usuarios autorizados, y configuraciones específicas para la operación de ventas.

**Criterios de Aceptación:**
1. El formulario de creación de caja incluye:
   - **Información básica**:
     - Nombre/Número de caja (ej: "Caja 1", "Caja Principal", "Caja Express")
     - Código único (alfanumérico)
     - Sede a la que pertenece
     - Ubicación física (ej: "Planta baja, junto a entrada")
     - Tipo: Principal, Secundaria, Móvil
     - Estado: Activa, Inactiva, Mantenimiento
   - **Hardware asociado**:
     - Computadora/Tablet (identificador, MAC address)
     - Impresora térmica/fiscal (modelo, puerto de conexión)
     - Lector de código de barras (modelo)
     - Terminal de pagos (POS bancario, número de terminal)
     - Gaveta de efectivo: Sí/No
   - **Configuraciones operativas**:
     - Requiere apertura/cierre de caja: Sí/No
     - Monto máximo de efectivo permitido (para seguridad)
     - Almacén vinculado (para descuento de inventario)
     - Series de comprobantes asignadas:
       - Serie de boletas (ej: B001)
       - Serie de facturas (ej: F001)
   - **Usuarios autorizados**:
     - Lista de usuarios (cajeros) que pueden operar esta caja
     - Opción de "Cualquier cajero" o restringir a usuarios específicos
2. El sistema valida:
   - Código único de caja en la sede
   - Series de comprobantes únicas en el negocio (no duplicadas)
   - Al menos una impresora configurada
3. Al crear la caja:
   - Se genera registro en `caja`
   - Se vincula a la sede y almacén
   - Las series de comprobantes quedan reservadas para esa caja
   - Se configura el hardware (conexión con impresora, lector, etc.)
4. Gestión de cajas:
   - Lista de cajas por sede con estado
   - Indicadores:
     - 🟢 Activa con sesión abierta (en uso)
     - 🟡 Activa sin sesión (disponible)
     - ⚪ Inactiva
     - 🔴 Mantenimiento/Error
   - Acciones: Editar, Desactivar, Ver sesiones, Diagnosticar hardware
5. Monitoreo en tiempo real:
   - Dashboard mostrando todas las cajas activas
   - Estado de cada caja (quién la está usando, desde cuándo)
   - Monto en caja actual (efectivo)
   - Última venta procesada

**Consideraciones Multi-tenant:**
- Las cajas son específicas de cada negocio y sede
- Las series de comprobantes son únicas por negocio

**Seguridad:**
- Requiere permiso: `cajas_configurar`
- Solo usuarios autorizados pueden abrir sesiones en cajas específicas
- Los cambios en series de comprobantes requieren aprobación (impacto fiscal)

**UX:**
- Vista tipo "card" para cada caja con estado visual
- Asistente de configuración de hardware (detectar impresoras, lectores)
- Test de hardware: Imprimir ticket de prueba, leer código de barras
- Código de colores por estado

**Reglas de Negocio:**
- Una caja solo puede tener una sesión abierta a la vez
- Las series de comprobantes son correlativas y no pueden tener saltos
- El efectivo en caja > monto máximo genera alerta de seguridad (riesgo de robo)
- Las cajas inactivas no pueden procesar ventas

---

### RF-ADM-006: Configurar Impresoras y Dispositivos

**Descripción:**  
El sistema debe permitir configurar y gestionar impresoras térmicas/fiscales, lectores de código de barras, balanzas, y otros dispositivos de hardware utilizados en las operaciones de venta e inventario.

**Criterios de Aceptación:**
1. Tipos de dispositivos soportados:
   - **Impresoras**:
     - Térmicas (tickets, etiquetas)
     - Fiscales (según legislación local)
     - De red (compartidas)
   - **Lectores de código de barras**: USB, Bluetooth
   - **Balanzas**: Para productos por peso
   - **Displays de cliente**: Pantalla para mostrar total al cliente
   - **Terminales de pago**: POS bancarios
2. El formulario de configuración de dispositivo incluye:
   - **Información básica**:
     - Nombre del dispositivo (ej: "Impresora Caja 1")
     - Tipo de dispositivo (dropdown)
     - Marca y modelo
     - Número de serie
     - Sede y caja asociada
   - **Conexión**:
     - Tipo de conexión: USB, Red (IP), Bluetooth, Serial (COM)
     - Puerto/IP/MAC address
     - Driver requerido (información)
   - **Configuración específica**:
     - Para impresoras:
       - Ancho de papel (58mm, 80mm)
       - Velocidad de impresión
       - Corte automático: Sí/No
       - Número de copias por defecto
     - Para lectores de código de barras:
       - Tipo de códigos soportados (EAN-13, QR, etc.)
       - Modo: Manual, Automático
   - **Estado**: Activo, Inactivo, Error, Mantenimiento
3. Funciones de configuración:
   - **Test de dispositivo**:
     - Botón "Probar dispositivo"
     - Para impresoras: Imprime ticket de prueba
     - Para lectores: Espera lectura de código
     - Para balanzas: Lee peso actual
   - **Detección automática**:
     - Botón "Detectar dispositivos"
     - El sistema escanea puertos y detecta dispositivos conectados
     - Auto-completa configuración si reconoce el modelo
4. Gestión de dispositivos:
   - Lista de dispositivos por sede/caja
   - Indicadores de estado en tiempo real:
     - 🟢 Conectado y funcionando
     - 🟡 Conectado pero inactivo
     - 🔴 Desconectado o con error
     - ⚪ Deshabilitado
   - Acciones: Probar, Reconectar, Editar, Desactivar
5. Diagnóstico y troubleshooting:
   - Log de errores del dispositivo
   - Última conexión exitosa
   - Sugerencias de solución según el error
   - Manual de configuración por modelo

**Consideraciones Multi-tenant:**
- Los dispositivos son específicos de cada negocio y sede
- No hay compartición de dispositivos entre negocios

**Seguridad:**
- Requiere permiso: `dispositivos_configurar`
- Los drivers solo pueden instalarse desde fuentes oficiales
- Los dispositivos fiscales requieren certificación

**UX:**
- Wizard de configuración paso a paso
- Detectar automáticamente al conectar dispositivo
- Iconos visuales por tipo de dispositivo
- Mensajes de error claros con soluciones sugeridas

**Reglas de Negocio:**
- Las impresoras fiscales deben estar certificadas por la autoridad tributaria
- Un dispositivo solo puede estar asignado a una caja a la vez
- Los dispositivos en error deben reportarse a soporte técnico después de 3 intentos fallidos
- Los dispositivos críticos (impresoras) requieren backup configurado

---

### RF-ADM-007: Configurar Zonas de Delivery

**Descripción:**  
El sistema debe permitir definir zonas geográficas de cobertura para el servicio de delivery, configurando costos, tiempos estimados, y condiciones específicas por zona para optimizar las operaciones de entrega a domicilio.

**Criterios de Aceptación:**
1. El formulario de creación de zona de delivery incluye:
   - **Información básica**:
     - Nombre de la zona (ej: "Zona Centro", "Zona Norte", "Zona Premium")
     - Descripción
     - Sede que atiende esta zona
     - Color en el mapa (para identificación visual)
   - **Delimitación geográfica**:
     - Método 1: Dibujar polígono en mapa (Google Maps)
     - Método 2: Listar distritos/barrios incluidos
     - Método 3: Radio desde la sede (ej: 5 km a la redonda)
   - **Costos y tiempos**:
     - Costo de delivery (fijo o por km)
     - Tiempo estimado de entrega (ej: 30-45 minutos)
     - Monto mínimo de pedido (ej: $20 USD)
   - **Disponibilidad**:
     - Horario de delivery (puede ser diferente al horario de tienda)
     - Días de la semana disponibles
     - Capacidad diaria (máximo de pedidos por día en esa zona)
   - **Condiciones especiales**:
     - Delivery gratis si pedido > X monto
     - Recargo por delivery express
     - Restricciones (ej: no entregas en edificios sin ascensor)
2. El sistema valida:
   - Las zonas no se superponen (o se permite con prioridad configurada)
   - El costo de delivery >= 0
   - El tiempo estimado es razonable (5-120 minutos)
   - El monto mínimo es coherente
3. Al crear la zona:
   - Se genera registro en `zona_delivery`
   - Se guarda el polígono geográfico (coordenadas)
   - La zona queda disponible para pedidos online
4. Validación de dirección en pedido:
   - Cuando el cliente ingresa dirección:
     - El sistema geolocaliza la dirección
     - Verifica si está dentro de alguna zona de cobertura
     - Si está dentro: Muestra costo y tiempo estimado
     - Si está fuera: "Lo sentimos, aún no llegamos a tu zona"
5. Mapa de cobertura público:
   - Los clientes pueden ver el mapa de zonas de delivery
   - Ingresar dirección para verificar cobertura antes de ordenar
6. Gestión de zonas:
   - Lista de zonas con información resumida
   - Vista de mapa mostrando todas las zonas coloreadas
   - Acciones: Editar, Desactivar temporalmente, Eliminar

**Consideraciones Multi-tenant:**
- Cada negocio/sede configura sus propias zonas
- Las zonas no se cruzan entre negocios

**Seguridad:**
- Requiere permiso: `delivery_zonas_configurar`
- Solo gerentes pueden crear/editar zonas

**UX:**
- Editor de mapa interactivo con herramientas de dibujo
- Arrastar vértices del polígono para ajustar zona
- Vista previa del costo calculado según configuración
- Código de colores en el mapa

**Reglas de Negocio:**
- El costo de delivery debe cubrir el gasto operativo (combustible, repartidor)
- Las zonas lejanas (> 10 km) pueden tener costos diferenciados
- Los pedidos en zonas sin cobertura se rechazan automáticamente
- Las zonas pueden desactivarse temporalmente (ej: por mal clima, falta de repartidores)

---

### RF-ADM-008: Configurar Métodos de Pago Aceptados

**Descripción:**  
El sistema debe permitir habilitar y configurar los diferentes métodos de pago que el negocio acepta, incluyendo efectivo, tarjetas, transferencias, billeteras digitales, y crédito, con configuraciones específicas por método.

**Criterios de Aceptación:**
1. Métodos de pago disponibles en el sistema:
   - Efectivo
   - Tarjeta de crédito
   - Tarjeta de débito
   - Transferencia bancaria
   - Yape (Perú)
   - Plin (Perú)
   - PayPal
   - Crédito (a clientes corporativos)
   - Otros personalizados
2. Para cada método de pago, el negocio puede configurar:
   - **Estado**: Habilitado/Deshabilitado
   - **Disponible en**:
     - POS (venta en tienda)
     - Delivery/Pickup (pedidos online)
     - Mesas (restaurante/bar)
   - **Comisión/Descuento**:
     - Comisión aplicada (% o monto fijo) - absorbida por el negocio
     - Descuento por uso (ej: 5% descuento pagando en efectivo)
   - **Límites**:
     - Monto mínimo (ej: tarjeta solo para compras > $10)
     - Monto máximo por transacción
   - **Configuración específica**:
     - Para tarjetas: Integración con POS bancario (número de terminal)
     - Para Yape/Plin: Número de teléfono o QR para recibir pagos
     - Para transferencia: Datos bancarios (banco, número de cuenta, CCI)
     - Para PayPal: API credentials
3. Configuración de pagos mixtos:
   - Permitir pagos con múltiples métodos: Sí/No
   - Ejemplo: Pagar $30 en efectivo + $20 con tarjeta
4. El sistema muestra en la interfaz de pago:
   - Solo los métodos habilitados
   - Información adicional si la requiere (ej: datos bancarios para transferencia)
   - Cálculo automático de comisiones/descuentos
5. Reportes:
   - Ventas por método de pago
   - Comisiones pagadas por método
   - Método más utilizado

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propios métodos de pago
- Las integraciones (PayPal, etc.) son por negocio

**Seguridad:**
- Requiere permiso: `metodos_pago_configurar`
- Las credenciales de APIs (PayPal, gateways) se almacenan encriptadas
- Auditoría de cambios en métodos de pago

**UX:**
- Lista de métodos con toggle switches (on/off)
- Formulario de configuración específico por método
- Vista previa de cómo se verá en el POS
- Iconos visuales para cada método

**Reglas de Negocio:**
- Al menos un método de pago debe estar habilitado (típicamente efectivo)
- Las comisiones de tarjetas bancarias son fijas (según contrato con banco)
- Los pagos en efectivo no tienen comisión
- El crédito solo está disponible para clientes autorizados

---

### RF-ADM-009: Configurar Políticas Operativas

**Descripción:**  
El sistema debe permitir definir políticas operativas del negocio, como políticas de devolución, garantías, manejo de productos vencidos, tiempos de preparación, y otras reglas que guían la operación diaria.

**Criterios de Aceptación:**
1. Políticas configurables:
   - **Política de devoluciones**:
     - Aceptar devoluciones: Sí/No
     - Plazo para devoluciones (días): 7, 15, 30, personalizado
     - Productos no retornables: Lista de categorías (ej: alcohol abierto, cigarros)
     - Condiciones: Producto sellado, con ticket, etc.
     - Tipo de compensación: Reembolso, cambio, crédito a favor
   - **Política de stock**:
     - Stock mínimo por defecto (unidades)
     - Punto de reorden automático: Habilitado/Deshabilitado
     - Permitir ventas con stock en 0: Sí/No (para pedidos bajo orden)
     - Reservar inventario en pedidos: Sí/No
   - **Política de precios**:
     - Mostrar precios con IGV incluido: Sí/No
     - Permitir descuentos manuales: Sí/No (y hasta qué %)
     - Redondeo de totales: Al céntimo, al sol, sin redondeo
     - Precios diferenciados por sede: Sí/No
   - **Política de productos vencidos**:
     - Alertar X días antes del vencimiento: 7, 15, 30
     - Retirar automáticamente productos vencidos: Sí/No
     - Permitir venta de productos próximos a vencer con descuento: Sí/No
   - **Tiempos de operación**:
     - Tiempo de preparación de pedidos (minutos): Por defecto
     - Tiempo de entrega de delivery estimado (minutos): Por zona
     - Tiempo de espera máximo en mesa antes de tomar orden (minutos)
   - **Política de propinas**:
     - Sugerir propina en POS: Sí/No
     - Porcentajes sugeridos: 5%, 10%, 15%, Otro
     - Propina obligatoria: Sí/No (solo en eventos especiales)
2. El formulario de configuración:
   - Organizado por categorías (tabs)
   - Cada política con descripción clara
   - Valores por defecto recomendados
   - Opción de "Restaurar valores por defecto"
3. Aplicación de políticas:
   - Las políticas se aplican automáticamente en todo el sistema
   - Ejemplo: Si "Permitir ventas con stock 0" = No, el POS bloquea la venta
   - Ejemplo: Si "Aceptar devoluciones" = Sí con 7 días, el sistema valida la fecha de compra
4. Comunicación de políticas:
   - Las políticas se muestran en:
     - Página web/app del negocio (Términos y condiciones)
     - Ticket de venta (footer con políticas resumidas)
     - Al momento de la compra (ej: "Este producto no admite devoluciones")

**Consideraciones Multi-tenant:**
- Cada negocio define sus propias políticas
- Las políticas no se comparten entre negocios

**Seguridad:**
- Requiere permiso: `politicas_configurar`
- Solo administradores pueden modificar políticas
- Cambios en políticas se auditan

**UX:**
- Interfaz clara y organizada
- Tooltips explicando cada política
- Vista previa de impacto: "Con esta configuración, no podrás vender productos agotados"

**Reglas de Negocio:**
- Las políticas deben cumplir con la legislación local (ej: Código de Protección al Consumidor)
- Cambios en políticas no son retroactivos (no afectan transacciones pasadas)
- Las políticas más restrictivas prevalecen en caso de conflicto
- Las políticas deben ser claras y transparentes para los clientes

---

### RF-ADM-010: Configurar Notificaciones y Alertas

**Descripción:**  
El sistema debe permitir configurar qué notificaciones y alertas se envían, a quién, por qué medio (email, SMS, push), y con qué frecuencia, personalizando la comunicación según las necesidades del negocio.

**Criterios de Aceptación:**
1. Tipos de notificaciones configurables:
   - **Operacionales**:
     - Nueva venta realizada
     - Pedido recibido (online)
     - Stock bajo de producto
     - Producto próximo a vencer
     - Apertura/Cierre de caja
     - Diferencia en arqueo de caja
   - **Clientes**:
     - Confirmación de pedido
     - Pedido en camino
     - Pedido entregado
     - Recordatorio de reserva de mesa
     - Promociones y ofertas
     - Cumpleaños del cliente
   - **Administrativas**:
     - Reporte diario de ventas
     - Reporte semanal de inventario
     - Factura de suscripción
     - Pago pendiente
     - Nuevo usuario registrado
   - **Alertas críticas**:
     - Sistema caído
     - Intento de acceso no autorizado
     - Dispositivo desconectado (impresora, etc.)
     - Pedido sin atender por mucho tiempo
2. Configuración por tipo de notificación:
   - **Habilitada/Deshabilitada**: Toggle switch
   - **Canales de envío**: Email, SMS, Push (app móvil), WhatsApp
   - **Destinatarios**:
     - Roles: Administrador, Gerente, Cajero, Almacenero, Cliente
     - Usuarios específicos: Lista de usuarios
     - Emails adicionales: Para externos (ej: contador)
   - **Frecuencia**:
     - Inmediata (en tiempo real)
     - Agrupada (resumen cada X horas)
     - Diaria (resumen al final del día)
     - Semanal
   - **Condiciones** (para alertas):
     - Ejemplo: Stock bajo solo si cantidad < 10 unidades
     - Ejemplo: Diferencia en caja solo si > $50 USD
3. Plantillas de notificaciones:
   - Cada tipo de notificación tiene una plantilla
   - Personalizable con variables: {nombre_cliente}, {total_pedido}, {numero_orden}, etc.
   - Vista previa de la plantilla antes de guardar
   - Soporta HTML para emails (formato rico)
4. Configuración de horarios:
   - Horarios de envío: No enviar notificaciones entre 23:00 y 07:00 (configurable)
   - Excepciones: Alertas críticas se envían siempre
5. Gestión de suscripciones (para clientes):
   - Los clientes pueden gestionar sus preferencias:
     - Acepto recibir promociones: Sí/No
     - Canal preferido: Email, SMS, WhatsApp
   - Botón "Desuscribirse" en emails
6. Historial de notificaciones:
   - Log de todas las notificaciones enviadas
   - Estado: Enviado, Fallido, Leído (para push)
   - Reintentar envío si falló

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propias notificaciones
- Las plantillas son por negocio

**Seguridad:**
- Solo administradores pueden configurar notificaciones
- Los datos de contacto (emails, teléfonos) se protegen
- Cumplimiento con leyes anti-spam

**UX:**
- Lista de notificaciones con checkboxes
- Editor de plantillas tipo WYSIWYG
- Vista previa de notificación antes de enviar
- Test: "Enviar notificación de prueba a mi email"

**Reglas de Negocio:**
- Las notificaciones críticas no pueden deshabilitarse
- Los emails marketing requieren consentimiento del cliente (opt-in)
- Las notificaciones SMS tienen costo (se contabilizan)
- Las notificaciones push requieren que el cliente tenga la app instalada

---

## Submódulo 6.2.2: Gestión de Usuarios y Seguridad

---

### RF-ADM-011: Crear y Gestionar Usuarios

**Descripción:**  
El sistema debe permitir al administrador crear, editar, desactivar y gestionar cuentas de usuario, asignando roles, permisos, y configuraciones específicas para controlar el acceso y las capacidades de cada miembro del equipo.

**Criterios de Aceptación:**
1. El formulario de creación de usuario incluye:
   - **Información personal**:
     - Nombre completo (obligatorio)
     - DNI/Documento de identidad
     - Email (único, obligatorio)
     - Teléfono
     - Fecha de nacimiento
     - Foto de perfil (opcional)
   - **Información laboral**:
     - Cargo/Posición
     - Sede(s) asignada(s): Puede trabajar en una o múltiples sedes
     - Fecha de ingreso
     - Tipo de contrato: Tiempo completo, Medio tiempo, Freelance
     - Salario base (opcional, privado)
   - **Acceso al sistema**:
     - Nombre de usuario (único, obligatorio)
     - Contraseña inicial (generada automáticamente o manual)
     - Rol(es) asignado(s): Admin, Gerente, Cajero, Almacenero, Mesero, etc.
     - Estado: Activo, Inactivo, Suspendido
     - Requerir cambio de contraseña en primer login: Sí/No
2. El sistema valida:
   - Email único en el negocio
   - Nombre de usuario único en el negocio
   - DNI único (si se proporciona)
   - Contraseña cumple política de seguridad (min 8 caracteres, mayúsculas, números)
   - Al menos un rol debe asignarse
3. Al crear el usuario:
   - Se genera registro en `usuario`
   - Se hashea la contraseña con bcrypt
   - Se envía email de bienvenida con credenciales (si contraseña es auto-generada)
   - Se asignan permisos según el rol
   - Se registra en auditoría
4. Gestión de usuarios:
   - Lista de usuarios con información resumida:
     - Nombre, Rol, Sede, Estado
     - Última conexión
     - Acciones: Ver perfil, Editar, Desactivar, Resetear contraseña
   - Filtros:
     - Por rol
     - Por sede
     - Por estado
     - Búsqueda por nombre o email
5. Edición de usuario:
   - Se pueden modificar datos personales, laborales, rol, sede
   - No se puede cambiar email/username (identificadores únicos)
   - Los cambios se registran en auditoría
6. Desactivación de usuario:
   - Motivo: Renuncia, Despido, Suspensión temporal
   - Estado cambia a `'inactivo'`
   - El usuario no puede loguearse
   - Sus datos históricos se mantienen (ventas, movimientos)
   - Puede reactivarse posteriormente

**Consideraciones Multi-tenant:**
- Los usuarios pertenecen a un negocio específico
- No hay visibilidad cruzada entre negocios

**Seguridad:**
- Requiere permiso: `usuarios_gestionar`
- Solo Admin y Gerentes pueden crear usuarios
- Las contraseñas nunca se muestran en texto plano
- Auditoría completa de creación, modificación, desactivación

**UX:**
- Wizard de 3 pasos: Información personal → Información laboral → Acceso y permisos
- Generador de contraseña segura con un clic
- Vista de tarjeta con foto del usuario
- Indicadores visuales de estado (activo=verde, inactivo=gris)

**Reglas de Negocio:**
- El número de usuarios está limitado por el plan de suscripción
- Al menos un usuario con rol Admin debe estar activo
- Los usuarios inactivos no cuentan para el límite del plan
- Los datos de usuarios despedidos se retienen por 5 años (requisitos laborales)

---

### RF-ADM-012: Asignar y Gestionar Roles

**Descripción:**  
El sistema debe proporcionar un conjunto de roles predefinidos y permitir la creación de roles personalizados, cada uno con un conjunto específico de permisos que determinan las acciones que los usuarios pueden realizar en el sistema.

**Criterios de Aceptación:**
1. Roles predefinidos en el sistema:
   - **SuperAdmin**: Control total de la plataforma (solo para personal de DrinkGo)
   - **Admin**: Control total del negocio
   - **Gerente**: Gestión operativa, reportes, configuración
   - **Cajero**: Operación de POS, ventas, cobros
   - **Almacenero**: Gestión de inventario, recepción de mercancía
   - **Mesero**: Gestión de mesas, toma de pedidos
   - **Repartidor**: Gestión de entregas
   - **Contador**: Acceso a reportes financieros y contables
2. Cada rol tiene:
   - Nombre del rol
   - Descripción
   - Lista de permisos asignados (ver RF-ADM-013)
   - Tipo: Sistema (no editable), Personalizado (editable)
   - Nivel de acceso: 1-5 (1=básico, 5=total)
3. Creación de roles personalizados:
   - El administrador puede crear nuevos roles
   - Formulario incluye:
     - Nombre del rol (ej: "Supervisor de Turno")
     - Descripción (ej: "Supervisa operaciones durante su turno")
     - Selección de permisos (checkboxes agrupados por módulo)
     - Heredar de rol existente (opcional, para facilitar creación)
4. Gestión de roles:
   - Lista de roles del negocio (sistema + personalizados)
   - Vista de permisos por rol (matriz)
   - Acciones: Ver permisos, Editar (solo personalizados), Duplicar, Eliminar
5. Asignación de roles a usuarios:
   - Un usuario puede tener uno o múltiples roles
   - Los permisos son la unión de todos los roles asignados
   - Ejemplo: Usuario con rol "Cajero" + "Almacenero" puede vender y gestionar inventario
6. Restricciones de eliminación:
   - No se pueden eliminar roles del sistema
   - No se pueden eliminar roles personalizados si hay usuarios asignados
   - Primero se debe reasignar a los usuarios a otro rol

**Consideraciones Multi-tenant:**
- Los roles personalizados son por negocio
- Los roles del sistema son globales pero los permisos pueden variar por plan de suscripción

**Seguridad:**
- Requiere permiso: `roles_gestionar`
- Solo Admin puede crear/editar roles
- Cambios en roles afectan inmediatamente a todos los usuarios con ese rol
- Auditoría de creación y modificación de roles

**UX:**
- Vista de matriz de permisos (roles en filas, permisos en columnas)
- Checkboxes para selección de permisos
- Agrupación de permisos por módulo (ventas, inventario, reportes, etc.)
- Indicador de "Rol poderoso" si tiene muchos permisos

**Reglas de Negocio:**
- Al menos un usuario con rol Admin debe existir siempre
- Los roles del sistema no pueden modificarse (mantener integridad)
- Los permisos sensibles (eliminar datos, ver finanzas) requieren roles específicos
- Los roles personalizados facilitan la adaptación del sistema a estructuras organizacionales únicas

---

### RF-ADM-013: Configurar Permisos Granulares

**Descripción:**  
El sistema debe implementar un sistema de permisos granulares que controle el acceso a cada funcionalidad específica del sistema, permitiendo asignar permisos a roles de manera precisa y flexible.

**Criterios de Aceptación:**
1. Categorías de permisos (158 permisos en total según la base de datos):
   - **Gestión de Plataforma** (SuperAdmin):
     - negocio_crear, negocio_editar, plan_suscripcion_gestionar, etc.
   - **Usuarios y Seguridad**:
     - usuarios_crear, usuarios_editar, usuarios_eliminar, roles_gestionar, permisos_asignar
   - **Inventario**:
     - inventario_ver, inventario_modificar, lotes_gestionar, movimientos_registrar, inventario_transferir
   - **Ventas**:
     - ventas_registrar, ventas_cancelar, descuentos_aplicar, reembolsos_procesar
   - **Productos**:
     - productos_crear, productos_editar, productos_eliminar, precios_modificar, categorias_gestionar
   - **Compras**:
     - compras_orden_crear, compras_orden_aprobar, proveedores_gestionar, recepcion_registrar
   - **Facturación**:
     - facturacion_emitir, facturacion_anular, notas_credito_emitir
   - **Reportes**:
     - reportes_ventas_ver, reportes_inventario_ver, reportes_financieros_ver
   - **Configuración**:
     - negocio_configurar, sedes_gestionar, cajas_configurar, dispositivos_configurar
   - **Clientes**:
     - clientes_ver, clientes_gestionar, credito_gestionar, marketing_enviar
   - **Mesas (Restaurante/Bar)**:
     - mesas_gestionar, cuentas_abrir, cuentas_cerrar, comandas_gestionar
2. Estructura de permisos:
   - Cada permiso tiene:
     - Código único (ej: `ventas_registrar`)
     - Nombre descriptivo (ej: "Registrar ventas")
     - Descripción (ej: "Permite procesar ventas en el POS")
     - Módulo al que pertenece
     - Nivel de riesgo: Bajo, Medio, Alto, Crítico
   - Los permisos se agrupan por recurso y acción:
     - Recurso: ventas, productos, usuarios, etc.
     - Acción: crear, leer, actualizar, eliminar (CRUD)
     - Ejemplo: `productos_crear`, `productos_editar`, `productos_eliminar`
3. Asignación de permisos:
   - Los permisos se asignan a roles, no directamente a usuarios
   - Un rol puede tener múltiples permisos
   - Los permisos son aditivos (si tiene dos roles, tiene todos los permisos de ambos)
4. Validación de permisos en el sistema:
   - Backend: Cada endpoint de API valida permisos antes de ejecutar
   - Frontend: Los elementos UI se ocultan si el usuario no tiene permiso
   - Ejemplo: Botón "Eliminar producto" solo visible si tiene permiso `productos_eliminar`
5. Permisos especiales:
   - **Override**: Permiso para sobrescribir restricciones (ej: `inventario_override_fifo`)
   - **Aprobación**: Permiso para aprobar acciones de otros (ej: `compras_orden_aprobar`)
   - **Auditoría**: Permiso para ver logs completos (ej: `auditoria_ver`)
6. Matriz de permisos por rol predefinido:
   - Admin: Todos los permisos del negocio (excepto SuperAdmin)
   - Gerente: Mayoría de permisos excepto eliminaciones críticas
   - Cajero: Solo ventas, cobros, clientes básico
   - Almacenero: Solo inventario, compras, productos
   - Mesero: Solo mesas, cuentas, pedidos
   - Repartidor: Solo pedidos, delivery
   - Contador: Solo reportes financieros (read-only)

**Consideraciones Multi-tenant:**
- Los permisos son universales pero su asignación a roles es por negocio
- SuperAdmin tiene permisos globales sobre todos los negocios

**Seguridad:**
- Los permisos se validan en cada request (no solo en UI)
- Intentos de acceso sin permiso se registran en auditoría
- Los permisos críticos (eliminar, modificar finanzas) se monitorean especialmente

**UX:**
- Matriz visual de permisos (roles × permisos)
- Búsqueda de permisos por nombre o código
- Agrupación por módulo con checkboxes "Seleccionar todos del módulo"
- Indicador de permisos críticos (⚠️ icono)

**Reglas de Negocio:**
- Los permisos son la base del control de acceso (RBAC - Role-Based Access Control)
- Un usuario sin permisos específicos no puede realizar la acción (principio de menor privilegio)
- Los permisos son inmutables (no se eliminan, se desasignan del rol)
- Los permisos de alto riesgo requieren justificación para asignarse

---

### RF-ADM-014: Gestionar Sesiones de Usuario

**Descripción:**  
El sistema debe controlar las sesiones activas de los usuarios, permitir login/logout, gestionar sesiones concurrentes, y proporcionar herramientas para cerrar sesiones remotamente por seguridad.

**Criterios de Aceptación:**
1. Proceso de login:
   - El usuario ingresa: Email/username y contraseña
   - El sistema valida:
     - Credenciales correctas
     - Usuario activo (no suspendido/inactivo)
     - Negocio activo (no suspendido por falta de pago)
   - Si es exitoso:
     - Se genera token de sesión (JWT o similar)
     - Se registra en `sesiones_usuario`:
       - Usuario
       - Fecha/hora de inicio
       - IP del cliente
       - User agent (navegador/dispositivo)
       - Token de sesión
     - Se redirige al dashboard
   - Si falla:
     - Se registra intento fallido
     - Después de 5 intentos: Se bloquea la cuenta por 30 minutos
     - Se notifica al administrador si hay múltiples intentos fallidos
2. Gestión de sesión activa:
   - Duración de sesión: Configurable (ej: 8 horas)
   - Keep-alive: Se extiende la sesión con cada actividad
   - Timeout de inactividad: Si no hay actividad por X minutos, se cierra sesión
   - Token refresh: Se renueva el token antes de expirar
3. Sesiones concurrentes:
   - Política configurable:
     - **Permitir múltiples sesiones**: Usuario puede estar logueado en varios dispositivos
     - **Una sesión a la vez**: Al loguear en nuevo dispositivo, se cierra sesión anterior
     - **Límite de sesiones**: Máximo N sesiones simultáneas (ej: 3)
4. Vista de sesiones activas (para el usuario):
   - Panel "Mis sesiones activas"
   - Lista de sesiones con:
     - Dispositivo (Windows, Android, iOS, etc.)
     - Ubicación aproximada (ciudad, país) según IP
     - Fecha/hora de inicio
     - Última actividad
     - Estado: Activa, Expirando pronto
   - Acciones: Cerrar sesión remota
5. Vista de sesiones activas (para administrador):
   - Panel "Usuarios conectados"
   - Lista de todos los usuarios con sesión activa
   - Información:
     - Usuario
     - Sede desde donde se conectó
     - Dispositivo e IP
     - Tiempo conectado
   - Acciones: Cerrar sesión de usuario (por seguridad)
6. Proceso de logout:
   - Voluntario: Usuario hace click en "Cerrar sesión"
   - Automático: Sesión expira por timeout
   - Forzado: Administrador cierra la sesión
   - Al cerrar sesión:
     - Se invalida el token
     - Se marca la sesión como cerrada
     - Se registra fecha/hora de cierre
7. Seguridad adicional:
   - Detección de ubicación inusual: Si usuario se loguea desde país diferente, enviar alerta por email
   - Detección de dispositivo nuevo: Notificar al usuario
   - Bloqueo por fuerza bruta: Bloqueo temporal después de intentos fallidos

**Consideraciones Multi-tenant:**
- Las sesiones son independientes por negocio
- SuperAdmin puede ver sesiones de todos los negocios

**Seguridad:**
- Los tokens de sesión son únicos y no reutilizables
- Los tokens se almacenan hasheados en la base de datos
- Las sesiones se transmiten sobre HTTPS únicamente
- Las contraseñas nunca se almacenan en la sesión

**UX:**
- Login simple y rápido
- "Recordarme" (checkbox) para extender sesión
- Notificación antes de que expire la sesión: "Tu sesión expirará en 5 minutos"
- Mensaje claro al cerrar sesión: "Sesión cerrada exitosamente"

**Reglas de Negocio:**
- Las sesiones de cajeros con caja abierta no deben cerrarse automáticamente (evitar pérdida de datos)
- Los administradores pueden cerrar sesiones de cualquier usuario en casos de emergencia
- Las sesiones inactivas > 24 horas se cierran automáticamente
- El historial de sesiones se retiene por 90 días para auditoría

---

### RF-ADM-015: Implementar Autenticación de Dos Factores (2FA)

**Descripción:**  
El sistema debe proporcionar autenticación de dos factores como capa adicional de seguridad, permitiendo a los usuarios y siendo obligatorio para roles críticos validar su identidad con un segundo método además de la contraseña.

**Criterios de Aceptación:**
1. Métodos de 2FA soportados:
   - **Aplicación de autenticación** (TOTP): Google Authenticator, Authy, Microsoft Authenticator
   - **SMS**: Código de 6 dígitos enviado al teléfono
   - **Email**: Código enviado al email registrado
   - **Códigos de respaldo**: Códigos de un solo uso para emergencias
2. Configuración de 2FA por usuario:
   - El usuario accede a "Seguridad de cuenta"
   - Opción: "Habilitar autenticación de dos factores"
   - Proceso de configuración:
     - Seleccionar método (App, SMS, Email)
     - Para App:
       - Se genera QR code
       - El usuario escanea con la app de autenticación
       - Ingresar código generado para verificar
     - Para SMS/Email:
       - Enviar código de verificación
       - Ingresar código recibido
     - Generar códigos de respaldo (10 códigos)
       - Usuario debe guardarlos en lugar seguro
       - Cada código se usa una sola vez
   - 2FA queda habilitado
3. Proceso de login con 2FA:
   - Usuario ingresa email/username y contraseña (primer factor)
   - Sistema solicita segundo factor:
     - Ingresar código de 6 dígitos
     - Validar que el código es correcto y no expiró (30 segundos de validez)
   - Si es correcto: Login exitoso
   - Si es incorrecto: Permitir 3 intentos, luego bloquear temporalmente
   - Opción: "Confiar en este dispositivo por 30 días" (checkbox)
4. Recuperación de acceso si pierde 2FA:
   - Usar código de respaldo
   - Contactar a administrador para desactivar 2FA temporalmente
   - Verificación de identidad (DNI, preguntas de seguridad)
5. Políticas de 2FA por rol:
   - **Obligatorio** para:
     - SuperAdmin
     - Admin del negocio
     - Contador (acceso a datos financieros)
   - **Opcional** para:
     - Gerente
     - Cajero
     - Almacenero
     - Otros roles
   - Configurable por negocio
6. Gestión de 2FA (para administrador):
   - Ver qué usuarios tienen 2FA habilitado
   - Forzar habilitación de 2FA para roles críticos
   - Desactivar 2FA de un usuario (en caso de emergencia)
   - Generar nuevos códigos de respaldo

**Consideraciones Multi-tenant:**
- La configuración de 2FA es por usuario, no por negocio
- Cada negocio puede tener políticas diferentes de obligatoriedad

**Seguridad:**
- Los códigos TOTP se generan según estándar RFC 6238
- Los códigos de respaldo se hashean antes de almacenar
- Los códigos SMS tienen expiración de 5 minutos
- Se limitan los intentos de verificación (prevención de fuerza bruta)

**UX:**
- Wizard guiado para configurar 2FA
- QR code grande y claro
- Instrucciones paso a paso con capturas de pantalla
- Opción de imprimir códigos de respaldo

**Reglas de Negocio:**
- 2FA es altamente recomendado pero no obligatorio para todos (depende del rol)
- Los códigos de respaldo son de un solo uso (se invalidan al usarse)
- El dispositivo "confiable" por 30 días se invalida si hay login desde ubicación diferente
- 2FA no se puede desactivar si es obligatorio para el rol

---

### RF-ADM-016: Gestionar Políticas de Contraseñas

**Descripción:**  
El sistema debe implementar y hacer cumplir políticas de contraseñas robustas, incluyendo requisitos de complejidad, expiración, historial, y herramientas para recuperación y cambio seguro de contraseñas.

**Criterios de Aceptación:**
1. Políticas de contraseñas configurables:
   - **Longitud mínima**: 8, 10, 12 caracteres (recomendado: 12)
   - **Complejidad requerida**:
     - Al menos una mayúscula: Sí/No
     - Al menos una minúscula: Sí/No
     - Al menos un número: Sí/No
     - Al menos un carácter especial (!@#$%): Sí/No
   - **Expiración**:
     - Las contraseñas expiran cada X días: 30, 60, 90, nunca
     - Alertar X días antes de expiración: 7, 5, 3 días
   - **Historial**:
     - No permitir reutilizar últimas N contraseñas: 3, 5, 10
   - **Bloqueo de contraseñas comunes**:
     - Rechazar contraseñas en lista de contraseñas débiles (ej: "123456", "password")
   - **Intentos de login**:
     - Bloquear cuenta después de N intentos fallidos: 3, 5, 10
     - Duración del bloqueo: 15, 30, 60 minutos
2. Validación en tiempo real:
   - Al ingresar contraseña en formulario de registro/cambio:
     - Indicador visual de fortaleza (débil, media, fuerte)
     - Checklist de requisitos cumplidos
     - Estimación de tiempo para crackear (ej: "Se crackearía en 3 años")
3. Cambio de contraseña:
   - El usuario puede cambiar su contraseña desde "Mi perfil"
   - Proceso:
     - Ingresar contraseña actual (validar que es correcta)
     - Ingresar nueva contraseña
     - Confirmar nueva contraseña
     - Validar que cumple políticas
     - Validar que no está en el historial
   - Al cambiar:
     - Se hashea la nueva contraseña
     - Se agrega la anterior al historial
     - Se invalidan todas las sesiones activas (requerir re-login)
     - Se envía email de confirmación
4. Recuperación de contraseña (olvidé mi contraseña):
   - Usuario hace click en "Olvidé mi contraseña"
   - Ingresa su email
   - Si el email existe:
     - Se genera token de recuperación único (válido por 1 hora)
     - Se envía email con link: `https://sistema.com/reset-password?token=XXXXX`
   - Usuario hace click en el link
   - Ingresa nueva contraseña (validar que cumple políticas)
   - Contraseña actualizada
   - Todas las sesiones se cierran (seguridad)
5. Forzar cambio de contraseña:
   - Administrador puede marcar un usuario con "Requerir cambio de contraseña"
   - Al siguiente login:
     - Usuario es redirigido a pantalla de cambio de contraseña
     - No puede acceder al sistema hasta cambiarla
   - Usar en casos de:
     - Contraseña comprometida
     - Usuario nuevo (contraseña temporal)
     - Política de seguridad

**Consideraciones Multi-tenant:**
- Las políticas de contraseñas son configurables por negocio
- SuperAdmin puede tener políticas más estrictas

**Seguridad:**
- Las contraseñas se hashean con bcrypt (factor de costo 12)
- Los tokens de recuperación son de un solo uso
- Los emails de recuperación no revelan si el email existe (prevención de enumeración)
- Los cambios de contraseña se auditan

**UX:**
- Indicador de fortaleza visual con colores (rojo=débil, amarillo=medio, verde=fuerte)
- Sugerencias para mejorar contraseña: "Agrega números y símbolos"
- Generador de contraseñas seguras con un botón
- Copy-paste habilitado en campos de contraseña (facilita uso de password managers)

**Reglas de Negocio:**
- Las contraseñas nunca se almacenan en texto plano
- Las contraseñas nunca se envían por email (solo links de recuperación)
- Los administradores no pueden ver las contraseñas de otros usuarios (solo resetearlas)
- Las políticas estrictas reducen el riesgo de compromiso de cuentas

---

### RF-ADM-017: Auditar Accesos y Acciones de Usuarios

**Descripción:**  
El sistema debe registrar automáticamente todos los accesos y acciones significativas de los usuarios para auditoría, investigación de incidentes, y cumplimiento de normativas, proporcionando trazabilidad completa.

**Criterios de Aceptación:**
1. Eventos de acceso auditados:
   - Login exitoso (usuario, fecha/hora, IP, dispositivo)
   - Login fallido (usuario intentado, IP, motivo del fallo)
   - Logout (manual o automático)
   - Cambio de contraseña
   - Recuperación de contraseña
   - Sesión expirada
   - Bloqueo de cuenta
   - Acceso denegado por permisos insuficientes
2. Acciones auditadas (ejemplos por módulo):
   - **Usuarios**: Crear, editar, desactivar usuario; cambiar rol
   - **Inventario**: Ajuste de inventario, transferencia, descuento FIFO aplicado
   - **Ventas**: Venta realizada, venta cancelada, reembolso procesado
   - **Productos**: Producto creado, precio modificado, producto eliminado
   - **Configuración**: Cambio en configuración de negocio, horarios, métodos de pago
   - **Reportes**: Reporte generado, datos exportados
3. Información registrada por cada evento:
   - **Quién**: Usuario (ID, nombre, rol)
   - **Qué**: Acción realizada (descripción detallada)
   - **Cuándo**: Timestamp preciso
   - **Dónde**: IP, ubicación geográfica (ciudad/país), dispositivo
   - **Contexto**: 
     - Entidad afectada (ej: producto_id=123, venta_id=456)
     - Valores antes/después (para modificaciones)
     - Resultado: Éxito/Fallo
     - Mensaje de error (si falló)
4. Almacenamiento de auditoría:
   - Tabla dedicada: `auditoria_usuarios`
   - Indexación por: usuario, fecha, tipo de acción
   - Retención: Mínimo 5 años
   - Backup periódico en almacenamiento inmutable (prevención de tampering)
5. Consulta de auditoría:
   - **Filtros disponibles**:
     - Usuario específico
     - Tipo de acción (login, venta, modificación, etc.)
     - Rango de fechas
     - Resultado (exitoso, fallido)
     - IP o ubicación
   - **Vista de resultados**:
     - Lista cronológica de eventos
     - Click en evento para ver detalles completos
     - Exportar a Excel/PDF
   - **Búsqueda de texto libre**: En descripción de acciones
6. Alertas automáticas basadas en auditoría:
   - Múltiples logins fallidos (posible ataque de fuerza bruta)
   - Login desde ubicación inusual (posible cuenta comprometida)
   - Acciones masivas (ej: eliminar 50 productos en 5 minutos)
   - Acceso fuera de horario habitual
   - Modificaciones en datos sensibles (precios, configuración)
7. Reportes de auditoría:
   - "Actividad por usuario" (últimos 30 días)
   - "Logins fallidos" (última semana)
   - "Cambios en configuración" (último mes)
   - "Exportaciones de datos" (quién exportó qué y cuándo)

**Consideraciones Multi-tenant:**
- Los logs de auditoría están separados por negocio
- SuperAdmin puede ver auditoría de todos los negocios
- Los negocios solo ven su propia auditoría

**Seguridad:**
- Los logs de auditoría son inmutables (append-only)
- Solo usuarios con permiso `auditoria_ver` pueden consultar
- Los logs se replican a sistema externo para prevenir eliminación maliciosa
- Encriptación de datos sensibles en logs

**UX:**
- Interfaz de búsqueda tipo "Log Viewer"
- Timeline visual de eventos
- Código de colores por tipo de evento (verde=exitoso, rojo=fallido)
- Detalles expandibles por evento

**Reglas de Negocio:**
- Todos los accesos y acciones críticas DEBEN auditarse (obligatorio)
- Los logs son evidencia legal en caso de disputas o investigaciones
- El acceso a logs de auditoría se audita también (meta-auditoría)
- Los logs antiguos (> 5 años) pueden archivarse pero no eliminarse

---

### RF-ADM-018: Configurar Restricciones de Acceso por IP

**Descripción:**  
El sistema debe permitir configurar listas blancas (whitelist) o listas negras (blacklist) de direcciones IP para controlar desde dónde los usuarios pueden acceder al sistema, mejorando la seguridad.

**Criterios de Aceptación:**
1. Tipos de restricción de IP:
   - **Lista blanca (Whitelist)**: Solo IPs en la lista pueden acceder
   - **Lista negra (Blacklist)**: IPs en la lista están bloqueadas
   - **Sin restricción**: Acceso desde cualquier IP (por defecto)
2. Configuración de restricciones:
   - El administrador accede a "Seguridad → Restricciones de IP"
   - Puede agregar:
     - IPs individuales (ej: 192.168.1.100)
     - Rangos de IPs (ej: 192.168.1.0/24)
     - Países completos (bloquear/permitir por geolocalización)
   - Para cada entrada:
     - IP o rango
     - Tipo: Permitir / Bloquear
     - Descripción (ej: "Oficina principal", "IP sospechosa de ataque")
     - Fecha de creación
     - Estado: Activa / Inactiva
3. Aplicación de restricciones:
   - Al intentar login:
     - Se obtiene la IP del cliente
     - Se verifica contra la lista
     - **Si whitelist activa**: Solo permite IPs en la lista
     - **Si blacklist activa**: Bloquea IPs en la lista
   - Si se bloquea:
     - Mensaje: "Acceso denegado desde tu ubicación por políticas de seguridad"
     - Se registra en auditoría
     - Se notifica al administrador (opcional)
4. Restricciones por rol:
   - Opción de configurar restricciones específicas por rol
   - Ejemplo: Los cajeros solo pueden acceder desde IPs de la sede (whitelist)
   - Ejemplo: Los administradores pueden acceder desde cualquier lugar
5. Excepciones temporales:
   - El administrador puede crear excepciones temporales
   - Ejemplo: Permitir IP específica por 24 horas (para trabajo remoto)
   - Después del tiempo, la excepción expira automáticamente
6. Vista de intentos bloqueados:
   - Lista de intentos de acceso bloqueados por IP
   - Información:
     - IP bloqueada
     - Usuario que intentó acceder
     - Fecha/hora
     - País/ubicación
   - Acción: Agregar a whitelist si fue legítimo

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propias restricciones de IP
- SuperAdmin puede tener restricciones globales

**Seguridad:**
- Requiere permiso: `seguridad_ip_configurar`
- Solo administradores pueden modificar restricciones
- Cambios en restricciones se auditan
- Las IPs se verifican en cada request (no solo en login)

**UX:**
- Lista de IPs con acciones (Editar, Eliminar, Activar/Desactivar)
- Mapa mostrando ubicación de IPs permitidas/bloqueadas
- Botón "Agregar mi IP actual" para facilitar whitelist
- Validación de formato de IP en tiempo real

**Reglas de Negocio:**
- Las restricciones de IP son adicionales a la autenticación (no la reemplazan)
- Las IPs privadas (192.168.x.x) son típicamente para redes locales
- Se recomienda whitelist para negocios con oficinas fijas
- Se recomienda blacklist para bloquear ataques conocidos

---

### RF-ADM-019: Gestionar Backups de Datos

**Descripción:**  
El sistema debe proporcionar herramientas para realizar backups (respaldos) de los datos del negocio, programar backups automáticos, y permitir la restauración de datos en caso de pérdida o corrupción.

**Criterios de Aceptación:**
1. Tipos de backup:
   - **Backup completo**: Todos los datos del negocio
   - **Backup incremental**: Solo cambios desde último backup
   - **Backup de módulo**: Solo datos de un módulo (ej: solo inventario)
2. Backup manual:
   - El administrador accede a "Configuración → Backups"
   - Click en "Crear backup ahora"
   - Seleccionar tipo: Completo / Incremental / Módulo específico
   - El sistema inicia el proceso:
     - Extrae datos de la base de datos (filtrados por `negocio_id`)
     - Comprime en archivo .zip
     - Encripta el archivo (opcional)
     - Almacena en servidor o nube (S3, Google Cloud, etc.)
   - Tiempo estimado según tamaño de datos
   - Notificación al completar
3. Backups automáticos programados:
   - Configuración de frecuencia:
     - Diario (hora específica, ej: 02:00 AM)
     - Semanal (día y hora)
     - Mensual (día del mes y hora)
   - Retención: Mantener últimos X backups (ej: 30 diarios, 12 mensuales)
   - Backups antiguos se eliminan automáticamente
4. Gestión de backups:
   - Lista de backups realizados:
     - Fecha y hora
     - Tipo (completo, incremental)
     - Tamaño del archivo
     - Estado (Exitoso, Fallido, En proceso)
     - Ubicación de almacenamiento
   - Acciones:
     - Descargar backup
     - Restaurar desde backup
     - Eliminar backup
5. Proceso de restauración:
   - El administrador selecciona un backup
   - Click en "Restaurar"
   - Advertencia: "Esta acción sobrescribirá los datos actuales. ¿Deseas continuar?"
   - Requiere confirmación adicional (escribir "RESTAURAR")
   - El sistema:
     - Descomprime el archivo
     - Valida la integridad
     - Crea backup del estado actual (por si acaso)
     - Restaura los datos
     - Registra en auditoría
   - Tiempo de restauración según tamaño
   - Notificación al completar
6. Backup de archivos adjuntos:
   - Además de datos de BD, incluir:
     - Imágenes de productos
     - Documentos subidos
     - Comprobantes PDF
   - Almacenar en el mismo backup o por separado

**Consideraciones Multi-tenant:**
- Cada backup contiene solo datos del negocio específico (aislamiento)
- Los backups se identifican por `negocio_id`

**Seguridad:**
- Requiere permiso: `backups_gestionar`
- Los backups se encriptan antes de almacenar
- Solo SuperAdmin puede restaurar backups (prevención de pérdida accidental)
- Los backups se almacenan en ubicación segura (no en el mismo servidor)

**UX:**
- Indicador de progreso durante backup/restauración
- Estimación de tiempo restante
- Notificación por email al completar
- Descarga directa del archivo de backup

**Reglas de Negocio:**
- Los backups automáticos son obligatorios (protección de datos)
- Se recomienda almacenar backups en múltiples ubicaciones (redundancia)
- Los backups deben probarse periódicamente (restaurar en ambiente de prueba)
- Los backups > 1 año pueden archivarse en almacenamiento frío (reducir costos)

---

### RF-ADM-020: Implementar Control de Acceso Basado en Horarios

**Descripción:**  
El sistema debe permitir configurar horarios de acceso para usuarios o roles, restringiendo el login fuera de los horarios laborales asignados para mejorar la seguridad y el control operativo.

**Criterios de Aceptación:**
1. Configuración de horarios de acceso:
   - **Por usuario individual**:
     - Definir días de la semana permitidos (Lun-Vie, solo Sábados, etc.)
     - Hora de inicio y fin de acceso (ej: 08:00-18:00)
   - **Por rol**:
     - Ejemplo: Cajeros solo pueden acceder de 08:00-22:00
     - Ejemplo: Almaceneros solo de 07:00-17:00
   - **Por sede**:
     - Los usuarios asignados a una sede solo pueden acceder durante el horario de la sede
2. Validación en login:
   - Al intentar login:
     - Se obtiene día y hora actual
     - Se verifica contra el horario configurado del usuario/rol
     - **Si está dentro del horario**: Login permitido
     - **Si está fuera del horario**: Login bloqueado
       - Mensaje: "Tu horario de acceso es Lun-Vie 08:00-18:00. Intenta nuevamente en tu horario laboral"
       - Se registra en auditoría
3. Excepciones:
   - Roles con acceso 24/7:
     - Administrador
     - Gerente
     - Roles de emergencia
   - Excepciones temporales:
     - El administrador puede otorgar acceso fuera de horario por 24-48 horas
     - Ejemplo: Cajero necesita trabajar domingo por evento especial
4. Acceso durante sesión activa:
   - Si el usuario ya tiene sesión activa y llega el fin de su horario:
     - Opción 1: Permitir terminar sesión actual (no interrumpir trabajo)
     - Opción 2: Cerrar sesión automáticamente (estricto)
     - Configurable por negocio
5. Reportes:
   - "Intentos de acceso fuera de horario"
   - "Excepciones de horario otorgadas"
   - "Usuarios activos por horario" (análisis de turnos)

**Consideraciones Multi-tenant:**
- Cada negocio configura horarios independientemente
- Los horarios no afectan a otros negocios

**Seguridad:**
- Requiere permiso: `horarios_acceso_configurar`
- Los administradores quedan exentos de restricciones (pueden acceder siempre)
- Las excepciones se auditan
- Previene accesos no autorizados fuera de horario laboral

**UX:**
- Configurador visual de horarios (similar a calendario)
- Toggle por día de la semana
- Time pickers para horas
- Vista previa: "El usuario podrá acceder Lun-Vie 08:00-18:00"

**Reglas de Negocio:**
- Los horarios de acceso deben coincidir con los horarios laborales del usuario
- No se recomienda horarios muy estrictos (puede afectar operaciones)
- Los administradores deben tener acceso ilimitado (para emergencias)
- Las excepciones son temporales (se revocan automáticamente)

---

### RF-ADM-021: Gestionar Tokens de API y Acceso Externo

**Descripción:**  
El sistema debe permitir generar tokens de API para integraciones externas, proporcionando acceso controlado y seguro a funcionalidades del sistema sin compartir credenciales de usuario.

**Criterios de Aceptación:**
1. Generación de tokens de API:
   - El administrador accede a "Configuración → API y Integraciones"
   - Click en "Crear nuevo token"
   - Formulario:
     - Nombre del token (ej: "Integración con E-commerce")
     - Descripción/propósito
     - Permisos asignados (selección de endpoints permitidos)
       - Lectura de productos
       - Actualización de inventario
       - Creación de pedidos
       - Consulta de ventas
     - Fecha de expiración (opcional): 30 días, 90 días, 1 año, nunca
     - Restricción de IP (opcional): Solo desde IPs específicas
   - Al crear:
     - Se genera token único (API Key): alfanumérico, 32-64 caracteres
     - Se muestra UNA SOLA VEZ (el usuario debe copiarlo y guardarlo)
     - Se hashea antes de almacenar en BD
2. Uso del token de API:
   - En requests HTTP, incluir en header:
     - `Authorization: Bearer {token}`
   - El sistema valida:
     - Token existe y es válido
     - Token no expiró
     - El endpoint solicitado está permitido para ese token
     - IP del request está permitida (si hay restricción)
   - Si es válido: Procesar request
   - Si no es válido: Responder con 401 Unauthorized
3. Gestión de tokens:
   - Lista de tokens activos:
     - Nombre
     - Fecha de creación
     - Última vez usado
     - Fecha de expiración
     - Estado: Activo, Expirado, Revocado
   - Acciones:
     - Ver permisos
     - Editar (cambiar nombre, permisos, expiración)
     - Revocar (invalidar token inmediatamente)
     - Renovar (generar nuevo token manteniendo configuración)
4. Monitoreo de uso:
   - Log de requests por token:
     - Fecha/hora
     - Endpoint accedido
     - IP del cliente
     - Resultado (éxito/fallo)
   - Alertas:
     - Uso anormal (muchos requests en poco tiempo)
     - Intentos de acceso a endpoints no autorizados
     - Acceso desde IP no permitida
5. Rate limiting:
   - Límites configurables por token:
     - X requests por minuto
     - Y requests por hora
     - Z requests por día
   - Si se excede: Responder con 429 Too Many Requests

**Consideraciones Multi-tenant:**
- Cada negocio tiene sus propios tokens de API
- Los tokens solo acceden a datos del negocio correspondiente

**Seguridad:**
- Requiere permiso: `api_tokens_gestionar`
- Los tokens se transmiten solo sobre HTTPS
- Los tokens se hashean antes de almacenar (no se pueden recuperar)
- Los tokens expiran automáticamente
- Se recomienda rotación periódica de tokens

**UX:**
- Generador de token con un clic
- Modal mostrando el token con botón "Copiar"
- Advertencia: "Guarda este token, no podrás verlo nuevamente"
- Documentación de API con ejemplos de uso

**Reglas de Negocio:**
- Los tokens de API no reemplazan la autenticación de usuarios (son complementarios)
- Se recomienda tokens con permisos mínimos (principio de menor privilegio)
- Los tokens comprometidos deben revocarse inmediatamente
- Los tokens son para aplicaciones, no para usuarios finales

---

## Submódulo 6.2.3: Configuración General

---

### RF-ADM-022: Configurar Impuestos y Retenciones

**Descripción:**  
El sistema debe permitir configurar tasas impositivas (IGV, IVA, ISR, etc.) y retenciones aplicables según la legislación del país, asegurando cálculos correctos en facturación y cumplimiento tributario.

**Criterios de Aceptación:**
1. Configuración de impuestos:
   - **IGV/IVA** (Impuesto General a las Ventas):
     - Tasa del impuesto (%): Ej: 18% (Perú), 16% (México), 21% (España)
     - Tipo de aplicación:
       - Incluido en el precio (precio final incluye impuesto)
       - Se suma al precio (precio + impuesto = total)
     - Productos exentos: Lista de categorías exentas (ej: alimentos básicos)
   - **ISR** (Impuesto Sobre la Renta) - opcional:
     - Para retenciones a proveedores
     - Tasa de retención (%)
   - **Otros impuestos especiales**:
     - Impuesto al alcohol (adicional)
     - Impuesto al tabaco
     - Tasa por bolsa plástica (legislaciones ecológicas)
2. El formulario de configuración incluye:
   - Lista de impuestos configurados:
     - Nombre del impuesto (ej: "IGV", "IVA")
     - Tipo (ventas, retención, especial)
     - Tasa (%)
     - Estado: Activo/Inactivo
     - Fecha de vigencia (desde cuándo aplica)
   - Botón "Agregar impuesto"
3. Aplicación de impuestos:
   - **En ventas**:
     - Se calcula automáticamente en cada transacción
     - Se muestra desglosado en el ticket:
       - Subtotal: $100
       - IGV (18%): $18
       - Total: $118
   - **En comprobantes**:
     - La factura electrónica incluye el desglose tributario
     - Formato según regulación (SUNAT, SAT, etc.)
   - **En reportes**:
     - Reporte de impuestos recaudados por período
     - Reporte para declaración mensual ante autoridad tributaria
4. Historial de cambios:
   - Si la tasa de impuesto cambia (ej: IGV sube de 18% a 19%):
     - Se registra fecha de cambio
     - Se aplica nueva tasa solo a transacciones futuras
     - Las transacciones pasadas mantienen la tasa vigente en su momento
     - Reporte de impacto del cambio

**Consideraciones Multi-tenant:**
- Cada negocio puede tener configuraciones diferentes según su país/región
- El sistema detecta el país del negocio y sugiere impuestos aplicables

**Seguridad:**
- Requiere permiso: `impuestos_configurar`
- Solo administradores o contadores pueden modificar
- Cambios en tasas se auditan (impacto fiscal)

**UX:**
- Plantillas predefinidas por país: "Configurar impuestos para Perú"
- Calculadora de impuestos en tiempo real
- Vista previa de ticket con impuestos aplicados

**Reglas de Negocio:**
- La configuración de impuestos debe coincidir con la legislación vigente
- Los cambios en tasas no son retroactivos
- Todos los comprobantes deben incluir desglose de impuestos (obligatorio)
- Los impuestos son una obligación legal (no opcional)

---

### RF-ADM-023: Configurar Numeración de Comprobantes

**Descripción:**  
El sistema debe permitir configurar las series y numeración correlativa de comprobantes de pago (boletas, facturas, notas de crédito) según los requisitos de la autoridad tributaria de cada país.

**Criterios de Aceptación:**
1. Tipos de comprobantes:
   - Boleta de Venta (para consumidores finales)
   - Factura Electrónica (para empresas con RUC)
   - Nota de Crédito (anulaciones, devoluciones)
   - Nota de Débito (cargos adicionales)
2. Configuración de series:
   - Para cada tipo de comprobante:
     - **Serie**: 4 caracteres alfanuméricos (ej: B001, F002, NC01)
     - **Número inicial**: Desde qué número comienza (ej: 1, 1000)
     - **Número actual**: Último número usado
     - **Autorizada por**: SUNAT, SAT, etc. (referencia)
     - **Fecha de autorización**
     - **Caja/Terminal asignada**: A qué caja corresponde esta serie
     - **Estado**: Activa, Agotada (llegó a 9999999), Anulada
3. Reglas de numeración:
   - **Correlativa**: Sin saltos (1, 2, 3, 4...)
   - **Formato**: SSSS-NNNNNNNN (Serie-Número)
     - Ejemplo: B001-00001234
   - **Límite**: Hasta 99999999 por serie
   - **Al llegar al límite**: Crear nueva serie (ej: B001 → B002)
4. Asignación de series por contexto:
   - **Por sede**: Cada sede tiene sus propias series
   - **Por caja**: Cada caja tiene series específicas
   - **Por tipo de venta**: POS, online, delivery (opcional)
5. Validaciones:
   - No puede haber series duplicadas en el negocio
   - No se pueden editar números ya emitidos
   - Los saltos en numeración generan alerta (requieren justificación para SUNAT)
6. Gestión de series:
   - Lista de series configuradas:
     - Serie
     - Tipo de comprobante
     - Rango: 00000001 - 00001234 (actual)
     - Estado
     - Caja asignada
   - Acciones: Ver detalle, Editar (solo descripción), Desactivar, Crear nueva
7. Reportes de numeración:
   - "Comprobantes emitidos por serie" (auditoría)
   - "Series próximas a agotarse" (> 95% del límite)
   - "Saltos en numeración" (para investigar)

**Consideraciones Multi-tenant:**
- Cada negocio tiene sus propias series independientes
- Las series son únicas por negocio (no pueden repetirse entre negocios)

**Seguridad:**
- Requiere permiso: `series_comprobantes_configurar`
- Solo administradores pueden crear/editar series
- La numeración es inmutable (prevención de fraude)
- Los cambios se auditan

**UX:**
- Wizard para crear serie: Tipo → Serie → Número inicial → Caja asignada
- Indicador visual de uso (barra de progreso): "B001 - 45% utilizado (4500/10000)"
- Alerta cuando serie llega a 90% de uso

**Reglas de Negocio:**
- Las series deben estar autorizadas por la autoridad tributaria antes de usarse
- La numeración correlativa es obligatoria (ley tributaria)
- Los saltos en numeración requieren explicación formal a SUNAT
- Las series agotadas no pueden reutilizarse (crear nueva serie)

---

### RF-ADM-024: Configurar Prefijos y Formatos de Códigos

**Descripción:**  
El sistema debe permitir configurar prefijos y formatos automáticos para códigos internos de entidades (productos, clientes, pedidos, etc.), facilitando la identificación y organización.

**Criterios de Aceptación:**
1. Entidades con códigos configurables:
   - **Productos**: SKU interno (ej: PROD-0001, BEB-CERVEZA-001)
   - **Clientes**: Código de cliente (ej: CLI-2024-0001)
   - **Pedidos**: Número de pedido (ej: PED-20240131-0001)
   - **Órdenes de compra**: Número de orden (ej: OC-2024-001)
   - **Lotes de inventario**: Código de lote (ej: LOTE-20240131-001)
   - **Usuarios**: ID de empleado (ej: EMP-001)
2. Configuración de formato:
   - **Prefijo**: Texto fijo al inicio (ej: "PROD-", "CLI-")
   - **Componentes variables**:
     - {YYYY}: Año (4 dígitos)
     - {YY}: Año (2 dígitos)
     - {MM}: Mes (2 dígitos)
     - {DD}: Día (2 dígitos)
     - {SEDE}: Código de sede
     - {SEQ}: Secuencia numérica (001, 002, ...)
   - **Sufijo**: Texto fijo al final (opcional)
   - **Ejemplo de formato**: "PROD-{YYYY}{MM}-{SEQ:4}"
     - Resultado: PROD-202401-0001
3. Configuración por entidad:
   - Para cada entidad, el administrador define:
     - Formato del código (usando variables)
     - Longitud de la secuencia (3, 4, 5, 6 dígitos)
     - Reiniciar secuencia: Nunca, Cada mes, Cada año
     - Ejemplo: Pedidos reinician secuencia cada día (PED-20240131-0001)
4. Generación automática:
   - Al crear una nueva entidad:
     - El sistema aplica el formato configurado
     - Incrementa la secuencia automáticamente
     - Valida que el código generado sea único
     - Si hay conflicto, intenta con siguiente número
5. Validación de códigos manuales:
   - Si el usuario ingresa código manualmente:
     - Validar que cumple el formato (opcional)
     - Validar unicidad
6. Vista previa:
   - Al configurar el formato, mostrar ejemplo:
     - "Con este formato, los códigos se verán así: PROD-202401-0001, PROD-202401-0002..."

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propios formatos independientemente
- Los códigos son únicos por negocio

**Seguridad:**
- Requiere permiso: `configuracion_codigos`
- Solo administradores pueden modificar formatos
- Los cambios en formatos no afectan códigos ya generados

**UX:**
- Editor de formato con componentes arrastrables
- Vista previa en tiempo real
- Sugerencias de formatos comunes: "Formato estándar", "Formato con fecha", "Formato simple"

**Reglas de Negocio:**
- Los códigos deben ser únicos dentro del negocio
- Se recomienda incluir año o fecha para facilitar búsquedas
- Los códigos cortos (< 10 caracteres) son más manejables
- No se recomienda cambiar formatos frecuentemente (causa confusión)

---

### RF-ADM-025: Configurar Monedas y Tipos de Cambio

**Descripción:**  
El sistema debe permitir configurar múltiples monedas, definir la moneda predeterminada del negocio, y actualizar tipos de cambio para transacciones multi-moneda.

**Criterios de Aceptación:**
1. Configuración de monedas:
   - **Moneda predeterminada**: La moneda principal del negocio (ej: PEN, USD)
   - **Monedas adicionales**: Otras monedas aceptadas (ej: USD, EUR)
   - Para cada moneda:
     - Código ISO (ej: PEN, USD, EUR)
     - Nombre (ej: Sol Peruano, Dólar Americano)
     - Símbolo (ej: S/, $, €)
     - Posición del símbolo: Antes o después del monto
     - Decimales: 0, 2, 3 decimales
     - Separador de miles: Coma, punto, espacio
     - Separador de decimales: Punto, coma
     - Estado: Activa, Inactiva
2. Tipos de cambio:
   - Para cada moneda no predeterminada:
     - Tipo de cambio de compra (cuánto vale 1 unidad de moneda extranjera)
     - Tipo de cambio de venta (a cuánto se vende 1 unidad de moneda extranjera)
     - Fecha de actualización
     - Fuente del tipo de cambio (manual, API de banco central)
   - Actualización:
     - Manual: Administrador ingresa el tipo de cambio
     - Automática: Integración con API (ej: Banco Central del Perú, Exchange Rate API)
     - Frecuencia: Diaria, en tiempo real
3. Aplicación en transacciones:
   - **Ventas en moneda extranjera**:
     - El cajero puede seleccionar moneda de pago
     - Si cliente paga en USD pero los precios están en PEN:
       - Se convierte usando tipo de cambio de compra
       - Ejemplo: Total $100 × TC 3.80 = S/380
     - El ticket muestra ambas monedas
   - **Reportes**:
     - Los reportes financieros pueden mostrarse en cualquier moneda
     - Conversión automática usando tipo de cambio de la fecha
4. Redondeo:
   - Configurar reglas de redondeo por moneda:
     - Redondear a céntimo más cercano
     - Redondear hacia arriba
     - Redondear hacia abajo
5. Gestión de tipos de cambio:
   - Historial de tipos de cambio:
     - Fecha, tipo de compra, tipo de venta, usuario que actualizó
   - Gráfico de evolución del tipo de cambio

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propias monedas según su país
- Los tipos de cambio pueden variar por negocio (algunos usan TC de banco, otros TC del mercado)

**Seguridad:**
- Requiere permiso: `monedas_configurar`
- Solo administradores o contadores pueden modificar tipos de cambio
- Los cambios se auditan (impacto financiero)

**UX:**
- Selector de moneda en formularios de venta
- Calculadora de conversión en tiempo real
- Gráfico de tendencia del tipo de cambio

**Reglas de Negocio:**
- La moneda predeterminada no puede cambiarse después de las primeras transacciones
- Los reportes históricos usan el tipo de cambio de la fecha de transacción (no el actual)
- Se recomienda actualizar tipos de cambio diariamente
- Los tipos de cambio para reportes siempre usan el TC oficial (banco central)

---

## Resumen del Módulo

El **Módulo II: Administración del Negocio** proporciona las funcionalidades para gestionar la configuración y operación interna de cada negocio (tenant), incluyendo:

### Submódulo 6.2.1: Configuración de Negocio y Sedes
- **10 requisitos funcionales** (RF-ADM-001 a RF-ADM-010)
- Configuración de datos del negocio, sedes, almacenes, horarios
- Gestión de cajas, dispositivos, zonas de delivery
- Configuración de métodos de pago, políticas operativas y notificaciones

### Submódulo 6.2.2: Gestión de Usuarios y Seguridad
- **11 requisitos funcionales** (RF-ADM-011 a RF-ADM-021)
- Gestión completa de usuarios, roles y permisos granulares
- Seguridad: 2FA, políticas de contraseñas, auditoría de accesos
- Control de acceso por IP, horarios, tokens de API
- Backups y recuperación de datos

### Submódulo 6.2.3: Configuración General
- **4 requisitos funcionales** (RF-ADM-022 a RF-ADM-025)
- Configuración de impuestos y retenciones
- Numeración de comprobantes según normativa fiscal
- Formatos de códigos internos
- Gestión de monedas y tipos de cambio

**Total: 25 requisitos funcionales** que permiten a cada negocio configurar y operar de manera independiente con altos estándares de seguridad y flexibilidad operativa.

---

**Fin del Módulo II**
