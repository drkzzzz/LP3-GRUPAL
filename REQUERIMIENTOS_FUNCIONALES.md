# **FASE 2 – ESTRUCTURA DEL DOCUMENTO SRS (REVISADA)**

---

# **ESPECIFICACIÓN DE REQUISITOS DE SOFTWARE (SRS)**
## **Sistema de Licorerías Multi-tenant DrinkGo**

**Versión:** 1.0  
**Fecha:** 31 de enero de 2026  
**Curso:** Lenguaje de Programación III  
**Proyecto:** Sistema SaaS Multi-tenant para Licorerías  

---

## **ÍNDICE**

### **1. INTRODUCCIÓN**
   1.1. Propósito del Documento  
   1.2. Alcance del Proyecto  
   1.3. Definiciones, Acrónimos y Abreviaturas  
   1.4. Referencias  
   1.5. Visión General del Documento  

### **2. DESCRIPCIÓN GENERAL DEL SISTEMA**
   2.1. Contexto del Sistema  
   2.2. Perspectiva del Producto  
   2.3. Funciones del Producto  
   2.4. Características de los Usuarios  
   2.5. Restricciones Generales  
   2.6. Suposiciones y Dependencias  

### **3. OBJETIVOS DEL SISTEMA**
   3.1. Objetivo General  
   3.2. Objetivos Específicos  

### **4. ALCANCE DEL SISTEMA**
   4.1. Funcionalidades Incluidas  
   4.2. Funcionalidades Excluidas  
   4.3. Límites del Sistema  

### **5. DEFINICIÓN DE ROLES Y PERMISOS**
   5.1. SuperAdmin (Administrador de la Plataforma)  
   5.2. Admin (Administrador del Negocio)  
   5.3. Gerente de Sede  
   5.4. Cajero  
   5.5. Mesero  
   5.6. Almacenero  
   5.7. Repartidor  
   5.8. Cliente (Usuario Storefront)  
   5.9. Matriz de Roles y Permisos  

### **6. REQUISITOS FUNCIONALES**

#### **6.1. MÓDULO I: GESTIÓN DE PLATAFORMA (SUPERADMIN)**

##### **6.1.1. Submódulo: Gestión de Suscripciones y Tenants**
   - RF-PLT-001: Gestionar Planes de Suscripción
   - RF-PLT-002: Registrar Nuevo Negocio (Tenant)
   - RF-PLT-003: Activar Suscripción de Negocio
   - RF-PLT-004: Suspender Suscripción de Negocio
   - RF-PLT-005: Cancelar Suscripción de Negocio
   - RF-PLT-006: Generar Factura de Suscripción
   - RF-PLT-007: Consultar Historial de Suscripciones
   - RF-PLT-008: Visualizar Dashboard de Plataforma
   - RF-PLT-009: Gestionar SuperAdmins

---

#### **6.2. MÓDULO II: ADMINISTRACIÓN DEL NEGOCIO**

##### **6.2.1. Submódulo: Configuración del Negocio y Sedes**
   - RF-ADM-001: Configurar Datos del Negocio
   - RF-ADM-002: Configurar Branding del Negocio
   - RF-ADM-003: Registrar Nueva Sede
   - RF-ADM-004: Editar Información de Sede
   - RF-ADM-005: Desactivar Sede
   - RF-ADM-006: Configurar Almacenes por Sede
   - RF-ADM-007: Configurar Horarios de Operación por Sede
   - RF-ADM-008: Configurar Restricciones de Venta de Alcohol
   - RF-ADM-009: Configurar Zonas de Delivery
   - RF-ADM-010: Activar/Desactivar Módulo de Mesas

##### **6.2.2. Submódulo: Gestión de Usuarios y Seguridad**
   - RF-ADM-011: Registrar Nuevo Usuario
   - RF-ADM-012: Editar Usuario
   - RF-ADM-013: Desactivar Usuario
   - RF-ADM-014: Asignar Rol a Usuario
   - RF-ADM-015: Asignar Usuario a Sedes
   - RF-ADM-016: Crear Rol Personalizado
   - RF-ADM-017: Asignar Permisos a Rol
   - RF-ADM-018: Consultar Log de Auditoría
   - RF-ADM-019: Gestionar Sesiones Activas
   - RF-ADM-020: Autenticación de Usuario
   - RF-ADM-021: Recuperar Contraseña

##### **6.2.3. Submódulo: Configuración General**
   - RF-ADM-022: Configurar Parámetros del Sistema
   - RF-ADM-023: Configurar Preferencias de Usuario
   - RF-ADM-024: Gestionar Notificaciones
   - RF-ADM-025: Configurar Métodos de Pago

---

#### **6.3. MÓDULO III: GESTIÓN DE PRODUCTOS E INVENTARIO**

##### **6.3.1. Submódulo: Catálogo de Productos**
   - RF-INV-001: Registrar Nuevo Producto
   - RF-INV-002: Editar Producto
   - RF-INV-003: Desactivar Producto
   - RF-INV-004: Cargar Imágenes de Producto
   - RF-INV-005: Gestionar Categorías de Productos
   - RF-INV-006: Gestionar Marcas
   - RF-INV-007: Crear Combo Promocional
   - RF-INV-008: Editar Combo
   - RF-INV-009: Definir Composición de Combo
   - RF-INV-010: Configurar Visibilidad en Storefront
   - RF-INV-011: Búsqueda de Productos
   - RF-INV-012: Importar Productos Masivamente
   - RF-INV-013: Crear Promoción
   - RF-INV-014: Configurar Reglas de Promoción

##### **6.3.2. Submódulo: Control de Inventario**
   - RF-INV-015: Visualizar Stock Consolidado
   - RF-INV-016: Consultar Detalle de Lotes
   - RF-INV-017: Registrar Ajuste de Inventario
   - RF-INV-018: Registrar Merma
   - RF-INV-019: Transferir Inventario entre Almacenes
   - RF-INV-020: Visualizar Alertas de Productos Próximos a Vencer
   - RF-INV-021: Visualizar Alertas de Stock Bajo
   - RF-INV-022: Consultar Kardex de Movimientos
   - RF-INV-023: Aplicar Sistema FIFO Automático

##### **6.3.3. Submódulo: Compras y Proveedores**
   - RF-INV-024: Registrar Proveedor
   - RF-INV-025: Editar Proveedor
   - RF-INV-026: Crear Orden de Compra
   - RF-INV-027: Aprobar Orden de Compra
   - RF-INV-028: Recibir Mercadería (Crear Lotes)
   - RF-INV-029: Registrar Compra Directa
   - RF-INV-030: Registrar Pago a Proveedor
   - RF-INV-031: Consultar Cuentas por Pagar
   - RF-INV-032: Registrar Devolución a Proveedor

---

#### **6.4. MÓDULO IV: OPERACIONES DE VENTA**

##### **6.4.1. Submódulo: Punto de Venta (POS)**
   - RF-VTA-001: Apertura de Caja
   - RF-VTA-002: Registrar Venta Rápida
   - RF-VTA-003: Búsqueda de Productos en POS
   - RF-VTA-004: Agregar Producto a Venta
   - RF-VTA-005: Aplicar Descuento a Item
   - RF-VTA-006: Aplicar Descuento Global
   - RF-VTA-007: Verificar Mayoría de Edad del Cliente
   - RF-VTA-008: Procesar Pago de Venta
   - RF-VTA-009: Procesar Pagos Mixtos
   - RF-VTA-010: Imprimir Comprobante
   - RF-VTA-011: Anular Venta
   - RF-VTA-012: Cierre de Caja
   - RF-VTA-013: Realizar Arqueo de Caja
   - RF-VTA-014: Descuento Automático de Inventario (FIFO)

##### **6.4.2. Submódulo: Gestión de Mesas**
   - RF-VTA-015: Visualizar Estado de Mesas
   - RF-VTA-016: Abrir Cuenta de Mesa
   - RF-VTA-017: Tomar Pedido en Mesa
   - RF-VTA-018: Agregar Items a Cuenta
   - RF-VTA-019: Cerrar Cuenta de Mesa
   - RF-VTA-020: Procesar Pago de Cuenta
   - RF-VTA-021: Dividir Cuenta (Pagos Individuales)
   - RF-VTA-022: Transferir Mesa
   - RF-VTA-023: Configurar Mesas del Local

##### **6.4.3. Submódulo: Gestión de Pedidos**
   - RF-VTA-024: Crear Pedido de Delivery
   - RF-VTA-025: Crear Pedido de Pickup
   - RF-VTA-026: Crear Pedido en Mesa
   - RF-VTA-027: Validar Zona de Cobertura para Delivery
   - RF-VTA-028: Calcular Costo de Delivery
   - RF-VTA-029: Asignar Repartidor a Pedido
   - RF-VTA-030: Cambiar Estado de Pedido
   - RF-VTA-031: Registrar Tracking de Pedido
   - RF-VTA-032: Procesar Pago de Pedido
   - RF-VTA-033: Cancelar Pedido

##### **6.4.4. Submódulo: Gestión de Clientes**
   - RF-VTA-034: Registrar Nuevo Cliente
   - RF-VTA-035: Editar Cliente
   - RF-VTA-036: Verificar Mayoría de Edad de Cliente
   - RF-VTA-037: Gestionar Direcciones de Entrega
   - RF-VTA-038: Consultar Historial de Compras
   - RF-VTA-039: Buscar Cliente

---

#### **6.5. MÓDULO V: FACTURACIÓN Y FINANZAS**

##### **6.5.1. Submódulo: Facturación Electrónica**
   - RF-FIN-001: Configurar Series de Comprobantes
   - RF-FIN-002: Emitir Boleta de Venta
   - RF-FIN-003: Emitir Factura
   - RF-FIN-004: Enviar Comprobante a SUNAT
   - RF-FIN-005: Consultar Estado en SUNAT
   - RF-FIN-006: Emitir Nota de Crédito
   - RF-FIN-007: Emitir Nota de Débito
   - RF-FIN-008: Anular Comprobante
   - RF-FIN-009: Reimprimir Comprobante

##### **6.5.2. Submódulo: Devoluciones**
   - RF-FIN-010: Registrar Devolución de Cliente
   - RF-FIN-011: Seleccionar Motivo de Devolución
   - RF-FIN-012: Aprobar Devolución
   - RF-FIN-013: Generar Nota de Crédito Automática
   - RF-FIN-014: Procesar Reembolso
   - RF-FIN-015: Reingreso de Producto a Inventario

##### **6.5.3. Submódulo: Gestión de Gastos**
   - RF-FIN-016: Registrar Gasto Operativo
   - RF-FIN-017: Categorizar Gasto
   - RF-FIN-018: Adjuntar Comprobante de Gasto
   - RF-FIN-019: Aprobar Gasto
   - RF-FIN-020: Configurar Gastos Recurrentes

---

#### **6.6. MÓDULO VI: E-COMMERCE Y REPORTES**

##### **6.6.1. Submódulo: Configuración de Storefront (Admin)**
   - RF-ECO-001: Configurar Tienda Online
   - RF-ECO-002: Configurar Age Gate (Verificación de Edad)
   - RF-ECO-003: Configurar Mensajes Legales
   - RF-ECO-004: Configurar Métodos de Pago Online
   - RF-ECO-005: Gestionar Banners Promocionales
   - RF-ECO-006: Crear Páginas Estáticas
   - RF-ECO-007: Configurar Horarios de Venta Online
   - RF-ECO-008: Activar/Desactivar Tienda Online

##### **6.6.2. Submódulo: Tienda Online (Cliente)**
   - RF-ECO-009: Verificación de Mayoría de Edad (Age Gate)
   - RF-ECO-010: Navegar Catálogo de Productos
   - RF-ECO-011: Filtrar Productos por Categoría/Marca
   - RF-ECO-012: Buscar Productos
   - RF-ECO-013: Ver Detalle de Producto
   - RF-ECO-014: Agregar Producto al Carrito
   - RF-ECO-015: Modificar Carrito de Compras
   - RF-ECO-016: Seleccionar Modalidad de Entrega
   - RF-ECO-017: Confirmar Mayoría de Edad en Checkout
   - RF-ECO-018: Procesar Pago Online
   - RF-ECO-019: Rastrear Pedido
   - RF-ECO-020: Registro y Login de Cliente

##### **6.6.3. Submódulo: Reportes y Análisis**
   - RF-ECO-021: Reporte de Ventas (Diarias, por Período, por Usuario, por Sede)
   - RF-ECO-022: Reporte de Productos Más Vendidos
   - RF-ECO-023: Reporte de Inventario (Stock, Valorización, Próximos a Vencer)
   - RF-ECO-024: Reporte de Movimientos de Inventario
   - RF-ECO-025: Reporte de Compras y Gastos
   - RF-ECO-026: Reporte de Clientes Frecuentes
   - RF-ECO-027: Reporte de Pedidos por Modalidad
   - RF-ECO-028: Dashboard de Indicadores Clave (KPIs)
   - RF-ECO-029: Exportar Reportes (Excel, PDF)

---

### **7. REQUISITOS NO FUNCIONALES**

#### **7.1. REQUISITOS DE SEGURIDAD**
   - RNF-SEG-001: Autenticación y Autorización
   - RNF-SEG-002: Encriptación de Contraseñas
   - RNF-SEG-003: Encriptación de Datos Sensibles
   - RNF-SEG-004: Tokens de Sesión
   - RNF-SEG-005: Validación de Entrada
   - RNF-SEG-006: Protección contra Inyección SQL
   - RNF-SEG-007: Protección contra XSS
   - RNF-SEG-008: Protección contra CSRF
   - RNF-SEG-009: Auditoría de Acciones Críticas
   - RNF-SEG-010: Cumplimiento de Ley de Protección de Datos

#### **7.2. REQUISITOS DE RENDIMIENTO**
   - RNF-REN-001: Tiempo de Respuesta del POS
   - RNF-REN-002: Tiempo de Carga del Storefront
   - RNF-REN-003: Procesamiento de Transacciones Concurrentes
   - RNF-REN-004: Optimización de Consultas de Base de Datos
   - RNF-REN-005: Caché de Datos Frecuentes

#### **7.3. REQUISITOS DE ESCALABILIDAD (MULTI-TENANT)**
   - RNF-ESC-001: Aislamiento Total de Datos entre Tenants
   - RNF-ESC-002: Soporte para Múltiples Tenants Concurrentes
   - RNF-ESC-003: Recursos Dedicados por Tenant según Plan
   - RNF-ESC-004: Escalamiento Horizontal de Base de Datos
   - RNF-ESC-005: Balanceo de Carga

#### **7.4. REQUISITOS DE DISPONIBILIDAD**
   - RNF-DIS-001: Disponibilidad del Sistema (99.5%)
   - RNF-DIS-002: Backup Automático Diario
   - RNF-DIS-003: Recuperación ante Desastres
   - RNF-DIS-004: Monitoreo de Salud del Sistema

#### **7.5. REQUISITOS DE USABILIDAD**
   - RNF-USA-001: Interfaz Intuitiva para POS
   - RNF-USA-002: Responsive Design (Móvil, Tablet, Desktop)
   - RNF-USA-003: Accesibilidad Web (WCAG 2.1 AA)
   - RNF-USA-004: Mensajes de Error Claros
   - RNF-USA-005: Ayuda Contextual

#### **7.6. REQUISITOS DE COMPATIBILIDAD**
   - RNF-COM-001: Navegadores Web Modernos
   - RNF-COM-002: Integración con Pasarelas de Pago
   - RNF-COM-003: Integración con SUNAT
   - RNF-COM-004: Exportación de Datos (Excel, PDF)

#### **7.7. REQUISITOS DE MANTENIBILIDAD**
   - RNF-MAN-001: Código Modular y Reutilizable
   - RNF-MAN-002: Documentación de Código
   - RNF-MAN-003: Logs de Sistema
   - RNF-MAN-004: Versionamiento de Base de Datos

#### **7.8. REQUISITOS LEGALES Y DE CUMPLIMIENTO**
   - RNF-LEG-001: Cumplimiento de Normativa de Venta de Alcohol
   - RNF-LEG-002: Verificación Obligatoria de Mayoría de Edad
   - RNF-LEG-003: Facturación Electrónica según SUNAT
   - RNF-LEG-004: Protección de Datos Personales (Ley N° 29733)
   - RNF-LEG-005: Libro de Reclamaciones Digital

### **8. RESTRICCIONES DEL SISTEMA**
   8.1. Restricciones Técnicas  
   8.2. Restricciones de Negocio  
   8.3. Restricciones Legales  
   8.4. Restricciones de Tiempo  

### **9. SUPOSICIONES Y DEPENDENCIAS**
   9.1. Suposiciones  
   9.2. Dependencias Externas  
   9.3. Dependencias Técnicas  

### **10. ANEXOS**
   10.1. Glosario de Términos  
   10.2. Modelo de Base de Datos (Diagrama ER)  
   10.3. Diagrama de Arquitectura del Sistema  
   10.4. Matriz de Trazabilidad de Requisitos  
   10.5. Casos de Uso Principales  

---

## **RESUMEN DE LA NUEVA ESTRUCTURA**

### **6 MÓDULOS PRINCIPALES:**

1. **MÓDULO I: GESTIÓN DE PLATAFORMA (SUPERADMIN)** - 9 requisitos
   - Gestión de Suscripciones y Tenants

2. **MÓDULO II: ADMINISTRACIÓN DEL NEGOCIO** - 25 requisitos
   - Configuración del Negocio y Sedes
   - Gestión de Usuarios y Seguridad
   - Configuración General

3. **MÓDULO III: GESTIÓN DE PRODUCTOS E INVENTARIO** - 32 requisitos
   - Catálogo de Productos
   - Control de Inventario
   - Compras y Proveedores

4. **MÓDULO IV: OPERACIONES DE VENTA** - 39 requisitos
   - Punto de Venta (POS)
   - Gestión de Mesas
   - Gestión de Pedidos
   - Gestión de Clientes

5. **MÓDULO V: FACTURACIÓN Y FINANZAS** - 20 requisitos
   - Facturación Electrónica
   - Devoluciones
   - Gestión de Gastos

6. **MÓDULO VI: E-COMMERCE Y REPORTES** - 29 requisitos
   - Configuración de Storefront (Admin)
   - Tienda Online (Cliente)
   - Reportes y Análisis

**Total: ~154 requisitos funcionales** (más compacto y manejable)

---

**✅ FASE 2 COMPLETADA (VERSIÓN REVISADA)**

La estructura ahora es más lógica y coherente:
- **6 módulos principales** en lugar de 17
- **13 submódulos** organizados por contexto funcional
- Más fácil de navegar y mantener
- Mantiene toda la funcionalidad del análisis de BD
