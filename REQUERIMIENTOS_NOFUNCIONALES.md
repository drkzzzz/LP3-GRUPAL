# **REQUERIMIENTOS NO FUNCIONALES DEL SISTEMA DRINKGO**

---

## **Introducción**

Los requerimientos no funcionales definen las características de calidad, rendimiento, seguridad, y restricciones técnicas que el sistema DrinkGo debe cumplir para garantizar una operación confiable, segura, y satisfactoria. Estos requerimientos determinan la experiencia del usuario, la escalabilidad del negocio, y el cumplimiento normativo.

---

## **1. RENDIMIENTO Y ESCALABILIDAD**

### **RNF-001: Rendimiento del Sistema**

**Descripción:**  
El sistema debe mantener tiempos de respuesta óptimos bajo condiciones de carga normal y picos de demanda.

**Especificaciones:**
- **Tiempos de respuesta**:
  - Consultas y búsquedas: **< 500ms** (percentil 95)
  - Transacciones de venta en POS: **< 2 segundos**
  - Carga de páginas web (e-commerce): **< 2 segundos**
  - Generación de reportes simples: **< 5 segundos**
- **Capacidad concurrente**:
  - Soportar **50 cajeros** trabajando simultáneamente por negocio
  - Soportar **500 usuarios** navegando en e-commerce simultáneamente
  - Throughput mínimo: **100 transacciones por segundo (TPS)**
- **Optimización de recursos**:
  - Uso de CPU promedio: **< 70%** en operación normal
  - Imágenes optimizadas con compresión y CDN
  - Lazy loading y cache de contenido estático

**Validación:**  
Pruebas de carga trimestrales con JMeter o k6.

---

### **RNF-002: Escalabilidad Multi-tenant**

**Descripción:**  
El sistema debe escalar horizontalmente para soportar el crecimiento de negocios y usuarios.

**Especificaciones:**
- **Arquitectura escalable**:
  - Arquitectura stateless con balanceo de carga
  - Auto-scaling automático según demanda (CPU > 70%)
  - Base de datos PostgreSQL con replicación y sharding por tenant
  - Cache distribuido con Redis Cluster
- **Capacidad proyectada**:
  - Soportar **10,000+ negocios** sin rediseño arquitectónico
  - Crecimiento de datos: Estimado 50TB en 3 años
  - Aislamiento total de datos por tenant (row-level security)
- **Limitaciones por plan**:
  - Plan Básico: 1,000 productos, 10,000 ventas/mes
  - Plan Pro: 5,000 productos, 50,000 ventas/mes
  - Plan Enterprise: Ilimitado

---

## **2. DISPONIBILIDAD Y CONFIABILIDAD**

### **RNF-003: Alta Disponibilidad**

**Descripción:**  
El sistema debe garantizar disponibilidad continua con mínimas interrupciones.

**Especificaciones:**
- **SLA de Uptime**:
  - Disponibilidad: **99.5%** mensual (máx. 3.6 horas downtime/mes)
  - Horarios críticos (viernes-domingo): **99.9%**
- **Redundancia**:
  - Base de datos con replicación master-slave
  - Múltiples instancias de servidores con failover automático
  - Load balancers redundantes
- **Recuperación ante desastres**:
  - **RTO** (Recovery Time Objective): **4 horas**
  - **RPO** (Recovery Point Objective): **1 hora** (pérdida máxima de datos)
  - Backups diarios completos con retención de 30 días
  - Backups incrementales cada 6 horas
  - Replicación geográfica en 2 regiones diferentes
- **Modo offline**:
  - POS puede operar sin conexión a internet
  - Sincronización automática al restaurar conexión

---

### **RNF-004: Tolerancia a Fallos**

**Descripción:**  
El sistema debe continuar operando de forma degradada ante fallos parciales.

**Especificaciones:**
- **Graceful degradation**:
  - Si servicios externos fallan (SUNAT, pasarelas), encolar operaciones para reintento
  - Circuit breaker: Desactivar servicio después de 5 fallos consecutivos
  - Retry automático con backoff exponencial (máximo 3 intentos)
- **Manejo de errores**:
  - Mensajes de error claros y accionables para usuarios
  - Logs técnicos completos con stack trace
  - Global error handler para errores no capturados
- **Consistencia de datos**:
  - Transacciones ACID para operaciones críticas
  - Validación de integridad en base de datos (constraints, foreign keys)
  - Jobs nocturnos de validación de consistencia

---

## **3. SEGURIDAD**

### **RNF-005: Autenticación y Autorización**

**Descripción:**  
El sistema debe proteger el acceso mediante autenticación robusta y control de acceso granular.

**Especificaciones:**
- **Autenticación segura**:
  - Contraseñas con mínimo 8 caracteres (mayúscula, minúscula, número, especial)
  - Hashing con **bcrypt** (cost factor 12)
  - MFA (Multi-Factor Authentication) opcional con TOTP
  - Tokens JWT con expiración (access: 15 min, refresh: 7 días)
- **Control de acceso**:
  - RBAC (Role-Based Access Control) con 158 permisos granulares
  - Principio de mínimo privilegio
  - Bloqueo de cuenta después de 5 intentos fallidos (15 minutos)
  - CAPTCHA después de 3 intentos fallidos
- **Auditoría**:
  - Registro de todos los logins exitosos/fallidos
  - Logs de cambios en permisos y configuraciones críticas
  - Retención de logs: 90 días (aplicación), 7 años (fiscal)

---

### **RNF-006: Protección de Datos y Comunicaciones**

**Descripción:**  
Los datos sensibles deben estar protegidos en tránsito y en reposo.

**Especificaciones:**
- **Encriptación en tránsito**:
  - **TLS 1.3** obligatorio para todas las comunicaciones
  - Certificados SSL válidos con renovación automática
  - HTTPS estricto (HSTS habilitado)
- **Encriptación en reposo**:
  - Base de datos con encriptación transparente (TDE)
  - Archivos sensibles con AES-256
  - Backups encriptados antes de almacenar
- **Protección de datos específicos**:
  - Contraseñas: bcrypt
  - Tarjetas de crédito: Tokenización (PCI DSS compliance)
  - Certificados digitales: Encriptados con clave maestra
  - Enmascaramiento en logs (DNI: ****1234, Email: j***@domain.com)
- **Protección contra amenazas**:
  - SQL Injection: Prepared statements obligatorios
  - XSS: Escapado automático y CSP headers
  - CSRF: Tokens CSRF y SameSite cookies
  - Rate limiting: 100 requests/min por IP, 1,000/hora por usuario
  - DDoS protection con CloudFlare
  - Headers de seguridad: X-Frame-Options, X-Content-Type-Options

---

## **4. USABILIDAD Y COMPATIBILIDAD**

### **RNF-007: Facilidad de Uso y Experiencia de Usuario**

**Descripción:**  
El sistema debe ser intuitivo, accesible, y proporcionar una experiencia satisfactoria.

**Especificaciones:**
- **Curva de aprendizaje**:
  - Usuario nuevo debe poder hacer venta en POS en **< 10 minutos** de capacitación
  - Tour guiado y tooltips contextuales
  - Navegación con máximo 2 niveles de profundidad
- **Responsive design**:
  - Adaptable a desktop (> 1024px), tablet (768-1024px), móvil (≥ 360px)
  - Touch targets mínimo 44x44px en móviles
- **Accesibilidad (WCAG 2.1 Level AA)**:
  - Contraste de colores mínimo 4.5:1
  - Navegación completa por teclado
  - Compatibilidad con lectores de pantalla (ARIA labels)
  - Texto alternativo en todas las imágenes
- **Experiencia**:
  - Mensajes de confirmación y feedback inmediato
  - Skeleton loaders durante cargas
  - NPS objetivo: **> 50**

---

### **RNF-008: Compatibilidad e Integraciones**

**Descripción:**  
El sistema debe funcionar en múltiples navegadores, dispositivos, y integrarse con servicios externos.

**Especificaciones:**
- **Navegadores soportados** (últimas 2 versiones):
  - Google Chrome, Firefox, Microsoft Edge, Safari
  - PWA instalable en Android/iOS
- **Dispositivos**:
  - Resolución mínima: 1280x720 (desktop), 360x640 (móvil)
  - Hardware POS: Impresoras térmicas, lectores de código de barras, cajones de dinero
- **Integraciones obligatorias**:
  - **SUNAT/OSE**: Facturación electrónica UBL 2.1
  - **Pasarelas de pago**: Culqi, Mercado Pago, Yape, Plin
  - **Mensajería**: WhatsApp Business API, SMS (Twilio), Email (SendGrid)
- **Integraciones opcionales**:
  - Delivery platforms: Rappi, Glovo, PedidosYa
  - Analytics: Google Analytics, Facebook Pixel
  - Contabilidad: Exportación PLE, compatibilidad con software contable

---

## **5. MANTENIBILIDAD Y PORTABILIDAD**

### **RNF-009: Código y Calidad**

**Descripción:**  
El sistema debe seguir buenas prácticas de desarrollo para facilitar mantenimiento y evolución.

**Especificaciones:**
- **Estándares de código**:
  - ESLint (JavaScript/TypeScript), Checkstyle (Java), PEP 8 (Python)
  - Code reviews obligatorios (mínimo 1 aprobación)
  - Nomenclatura consistente: camelCase, PascalCase, UPPER_SNAKE_CASE
- **Testing**:
  - Cobertura de código: **> 80%** (lógica de negocio: 100%)
  - Unit tests, integration tests, E2E tests
  - Tests ejecutados automáticamente en CI/CD
- **CI/CD**:
  - Pipeline automatizado: Build → Test → Deploy
  - Versionado semántico (MAJOR.MINOR.PATCH)
  - GitFlow o Trunk-based development
  - Despliegue blue-green sin downtime
  - Rollback en < 5 minutos
- **Monitoreo**:
  - APM (Application Performance Monitoring)
  - Logging centralizado (ELK Stack)
  - Alertas automáticas (error rate > 1%, latencia > 2s)
  - Dashboard de salud del sistema 24/7

---

### **RNF-010: Portabilidad y Configurabilidad**

**Descripción:**  
El sistema debe ser independiente de plataforma y altamente configurable.

**Especificaciones:**
- **Independencia de plataforma**:
  - Contenedores Docker para portabilidad
  - PostgreSQL (multi-cloud compatible)
  - Deployable en AWS, Azure, GCP, o on-premise
- **Configurabilidad**:
  - Variables de entorno para configuraciones sensibles
  - Panel de administración para reglas de negocio
  - Feature flags para activar/desactivar funcionalidades
  - Multi-tenancy configurable (branding, horarios, integraciones)
- **Exportación de datos**:
  - Formatos estándar: CSV, JSON, Excel, SQL dump
  - API REST documentada con Swagger/OpenAPI

---

## **6. CUMPLIMIENTO LEGAL Y NORMATIVO**

### **RNF-011: Cumplimiento Fiscal (SUNAT - Perú)**

**Descripción:**  
El sistema debe cumplir con todas las normativas fiscales y tributarias de Perú.

**Especificaciones:**
- **Facturación Electrónica**:
  - Formato UBL 2.1 con firma digital
  - Envío a OSE o SUNAT con procesamiento de CDR
  - Emisión de facturas, boletas, notas de crédito/débito
- **Libros Electrónicos (PLE)**:
  - PLE 14.1 (Registro de Ventas) y PLE 8.1 (Registro de Compras)
  - Generación mensual en formato pipe-delimited
- **Retención de documentos**:
  - Comprobantes de pago: **Mínimo 5 años** (legal), recomendado 7 años
  - Logs de auditoría fiscal: **7 años**
- **Validaciones**:
  - Consulta de RUC/DNI en API de SUNAT
  - Validación de estado del contribuyente

---

### **RNF-012: Protección de Datos Personales y Regulaciones Comerciales**

**Descripción:**  
El sistema debe cumplir con leyes de protección de datos y regulaciones de comercio de alcohol.

**Especificaciones:**
- **Ley N° 29733 (Protección de Datos Personales - Perú)**:
  - Consentimiento informado para captura de datos
  - Derechos ARCO (Acceso, Rectificación, Cancelación, Oposición)
  - Registro ante la Autoridad Nacional de Protección de Datos Personales
  - Data retention: Solo mientras sea necesario, derecho al olvido
- **Venta de bebidas alcohólicas**:
  - Verificación de edad: **Mayor de 18 años**
  - Restricciones horarias configurables según legislación local
  - Advertencias sanitarias obligatorias
- **Código de Protección al Consumidor**:
  - Libro de Reclamaciones Digital
  - Políticas claras de devolución y términos de uso
  - Aceptación obligatoria antes de comprar

---

## **7. DOCUMENTACIÓN Y SOPORTE**

### **RNF-013: Documentación Completa**

**Descripción:**  
El sistema debe estar completamente documentado para facilitar uso, mantenimiento, y onboarding.

**Especificaciones:**
- **Documentación técnica**:
  - README en repositorio con instrucciones de setup
  - API documentada con Swagger/OpenAPI
  - Diagrama de arquitectura (C4 model) y ERD de base de datos
  - Runbooks para escenarios comunes (despliegue, rollback, DR)
- **Documentación de usuario**:
  - Manual de usuario en PDF y web (con búsqueda)
  - Videos tutoriales (< 5 minutos) con subtítulos
  - Base de conocimientos (FAQ) con casos comunes
  - Tooltips contextuales en la aplicación
- **Soporte**:
  - Chat/WhatsApp para soporte
  - Email con respuesta en < 24 horas
  - Teléfono para urgencias (planes Premium/Enterprise)

---

## **RESUMEN Y PRIORIZACIÓN**

### **Requerimientos CRÍTICOS (P0)**:
- RNF-001: Rendimiento del Sistema
- RNF-003: Alta Disponibilidad
- RNF-005: Autenticación y Autorización
- RNF-006: Protección de Datos
- RNF-011: Cumplimiento Fiscal SUNAT

### **Requerimientos ALTOS (P1)**:
- RNF-002: Escalabilidad Multi-tenant
- RNF-004: Tolerancia a Fallos
- RNF-007: Facilidad de Uso
- RNF-012: Protección de Datos Personales

### **Requerimientos MEDIOS (P2)**:
- RNF-008: Compatibilidad e Integraciones
- RNF-009: Código y Calidad
- RNF-010: Portabilidad
- RNF-013: Documentación

---

## **MÉTRICAS DE VALIDACIÓN**

El cumplimiento se validará mediante:

1. **Tests automatizados** de performance, seguridad, y carga
2. **Auditorías trimestrales** de seguridad y código
3. **Monitoreo continuo** de SLAs y métricas de rendimiento
4. **Feedback de usuarios** (NPS, encuestas)
5. **Pentesting anual** de seguridad externa

---

**Documento creado:** 31 de enero de 2026  
**Versión:** 1.0  
**Estado:** Completo  

---

*Este documento define los estándares de calidad, rendimiento, y seguridad esenciales que el sistema DrinkGo debe cumplir para garantizar una operación exitosa en el mercado peruano.*