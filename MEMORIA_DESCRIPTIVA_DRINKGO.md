# MEMORIA DESCRIPTIVA DEL SISTEMA DrinkGo

## Sistema de Gestión Multi-Tenant para Licorerías

**Versión:** 1.0  
**Fecha:** Marzo 2026  
**Tipo de proyecto:** Trabajo Grupal - Laboratorio de Programación III

---

## ÍNDICE

1. [Introducción](#1-introducción)
2. [Descripción General del Sistema](#2-descripción-general-del-sistema)
3. [Objetivos del Sistema](#3-objetivos-del-sistema)
4. [Alcance del Sistema](#4-alcance-del-sistema)
5. [Arquitectura del Sistema](#5-arquitectura-del-sistema)
6. [Tecnologías Utilizadas](#6-tecnologías-utilizadas)
7. [Estructura de la Base de Datos](#7-estructura-de-la-base-de-datos)
8. [Módulos del Sistema](#8-módulos-del-sistema)
9. [Seguridad del Sistema](#9-seguridad-del-sistema)
10. [Interfaces de Usuario](#10-interfaces-de-usuario)
11. [Requerimientos del Sistema](#11-requerimientos-del-sistema)
12. [Diccionario de Datos](#12-diccionario-de-datos)
13. [Conclusiones](#13-conclusiones)

---

## 1. INTRODUCCIÓN

### 1.1 Propósito del Documento

La presente memoria descriptiva tiene como propósito documentar de manera integral el desarrollo del sistema **DrinkGo**, una plataforma SaaS (Software as a Service) multi-tenant orientada a la gestión integral de licorerías. Este documento detalla la arquitectura, módulos, tecnologías, base de datos y funcionalidades implementadas en el sistema.

### 1.2 Definiciones y Acrónimos

| Término | Definición |
|---------|-----------|
| **SaaS** | Software as a Service – Modelo de distribución de software en la nube |
| **Multi-Tenant** | Arquitectura donde una sola instancia del software sirve a múltiples clientes (tenants) |
| **Tenant** | Cada negocio/licorería registrado en la plataforma |
| **POS** | Point of Sale – Punto de Venta |
| **JWT** | JSON Web Token – Estándar para autenticación basada en tokens |
| **API REST** | Interfaz de programación de aplicaciones basada en el estilo arquitectónico REST |
| **CRUD** | Create, Read, Update, Delete – Operaciones básicas de datos |
| **IGV** | Impuesto General a las Ventas (18% en Perú) |
| **SUNAT** | Superintendencia Nacional de Aduanas y de Administración Tributaria |
| **FIFO** | First In, First Out – Método de gestión de inventario |
| **Kardex** | Registro detallado de movimientos de inventario |
| **Storefront** | Tienda online pública para clientes finales |

### 1.3 Descripción del Problema

Las licorerías en Perú enfrentan dificultades para gestionar de manera eficiente sus operaciones diarias: control de inventario con fechas de vencimiento, punto de venta, facturación electrónica, gestión de múltiples sedes y almacenes, pedidos online y delivery. La mayoría utiliza sistemas dispares o procesos manuales, lo cual genera ineficiencias, errores en el stock, pérdida de productos por vencimiento y falta de visibilidad sobre el rendimiento del negocio.

### 1.4 Solución Propuesta

**DrinkGo** es una plataforma web SaaS multi-tenant que centraliza todas las operaciones de una licorería en un solo sistema. Permite a múltiples negocios operar de forma independiente y aislada dentro de la misma plataforma, con funcionalidades que van desde el catálogo de productos hasta el punto de venta, gestión de inventario con lotes FIFO, facturación electrónica, pedidos online, delivery y reportes analíticos.

---

## 2. DESCRIPCIÓN GENERAL DEL SISTEMA

### 2.1 Visión General

DrinkGo es un sistema de gestión empresarial diseñado específicamente para licorerías, con una arquitectura multi-tenant que permite operar como plataforma SaaS. El sistema se divide en **tres ámbitos principales** de operación:

1. **Panel SuperAdmin (Gestión de Plataforma):** Administración centralizada de todos los negocios, planes de suscripción, facturación de la plataforma y monitoreo global.

2. **Panel Admin (Gestión de Negocio):** Gestión completa de cada licorería individual, incluyendo catálogo, inventario, ventas, POS, pedidos, facturación electrónica, usuarios, reportes y configuración.

3. **Storefront (Tienda Online):** Portal público de e-commerce para clientes finales, con catálogo de productos, carrito de compras, checkout, seguimiento de pedidos y gestión de cuenta.

### 2.2 Usuarios del Sistema

| Tipo de Usuario | Descripción | Acceso |
|----------------|-------------|--------|
| **SuperAdmin** | Administrador de la plataforma SaaS | Panel SuperAdmin (`/superadmin`) |
| **Programador** | Usuario de plataforma con acceso limitado a módulos específicos para desarrollo | Panel SuperAdmin (módulos asignados) |
| **Admin del Negocio** | Propietario/administrador de cada licorería | Panel Admin (`/admin`) |
| **Cajero** | Operador del punto de venta | Panel Admin (POS y módulos autorizados) |
| **Inventarista** | Encargado de gestión de stock | Panel Admin (inventario y módulos autorizados) |
| **Cliente** | Comprador final de la tienda online | Storefront (`/tienda`) |

### 2.3 Modelo de Negocio SaaS

El sistema opera bajo un modelo de suscripción con planes configurables:

- **Planes de Suscripción:** Cada plan define límites operativos (sedes, usuarios, productos, almacenes) y funcionalidades habilitadas (POS, tienda online, delivery, mesas, facturación electrónica, reportes avanzados, acceso API).
- **Facturación Recurrente:** Sistema de facturación periódica (mensual/anual) con control de morosidad y suspensión automática.
- **Multi-Sede:** Los negocios pueden operar con múltiples sedes según su plan, cada una con sus propios almacenes, cajas y configuración.

---

## 3. OBJETIVOS DEL SISTEMA

### 3.1 Objetivo General

Desarrollar una plataforma web SaaS multi-tenant que permita la gestión integral de licorerías, centralizando las operaciones de catálogo, inventario, ventas, facturación electrónica, pedidos online y reportes en un único sistema accesible desde cualquier navegador web.

### 3.2 Objetivos Específicos

1. **Implementar una arquitectura multi-tenant** que permita aislar los datos y operaciones de cada negocio dentro de la misma plataforma.
2. **Desarrollar un sistema de catálogo de productos** con campos especializados para bebidas alcohólicas (grado alcohólico, volumen, tipo de bebida, etc.).
3. **Implementar gestión de inventario con lotes FIFO**, control de fechas de vencimiento y alertas de stock.
4. **Construir un Punto de Venta (POS)** optimizado para ventas rápidas, con soporte para múltiples métodos de pago, emisión de comprobantes y verificación de edad.
5. **Desarrollar un módulo de pedidos** que soporte delivery, recojo en tienda y consumo en mesa.
6. **Implementar facturación electrónica** compatible con SUNAT (boletas, facturas, notas de crédito/débito).
7. **Crear una tienda online (Storefront)** personalizable por cada negocio para ventas e-commerce.
8. **Desarrollar un sistema de reportes y analítica** con dashboards interactivos y exportación a Excel/PDF.
9. **Implementar un sistema robusto de seguridad** con autenticación JWT, roles y permisos granulares, y auditoría de acciones.
10. **Construir un panel de administración para SuperAdmin** que permita gestionar negocios, planes y la plataforma de forma centralizada.

---

## 4. ALCANCE DEL SISTEMA

### 4.1 Funcionalidades Incluidas

#### Panel SuperAdmin (6 módulos)
- Dashboard con métricas globales de la plataforma
- Gestión de negocios (tenants) y sus sedes
- Administración de planes de suscripción
- Facturación de suscripciones y control de morosidad
- Configuración global de la plataforma
- Auditoría y logs del sistema
- Gestión de usuarios bloqueados
- Reportes de plataforma

#### Panel Admin (10 módulos)
- Dashboard con métricas del negocio
- Configuración del negocio, sedes, almacenes y tienda online
- Seguridad: usuarios, roles, permisos y clientes
- Catálogo de productos, categorías, marcas, combos y promociones
- Gestión de inventario: stock, lotes FIFO, movimientos (kardex), transferencias y alertas
- Proveedores y órdenes de compra con recepción de mercancía
- Ventas: POS, cajas registradoras, sesiones de caja, comprobantes, pedidos (delivery/recojo/mesa) y devoluciones
- Facturación electrónica: series, comprobantes, envío a SUNAT, notas de crédito/débito
- Gastos e ingresos: registro, categorías, aprobación y caja chica
- Reportes y analítica: ventas, inventario, compras, gastos, clientes, pedidos

#### Storefront (Tienda Online)
- Página de inicio con catálogo de productos
- Detalle de productos
- Carrito de compras
- Proceso de checkout
- Registro e inicio de sesión de clientes
- Perfil de cliente y gestión de cuenta
- Historial y seguimiento de pedidos
- Devoluciones online

### 4.2 Funcionalidades Fuera de Alcance

- Aplicación móvil nativa (Android/iOS)
- Integración con hardware de impresión fiscal
- Contabilidad completa (libro diario, balance general)
- Recursos humanos y planillas
- CRM avanzado con campañas de marketing automatizadas

---

## 5. ARQUITECTURA DEL SISTEMA

### 5.1 Arquitectura General

El sistema sigue una arquitectura **Cliente-Servidor** con separación clara entre frontend y backend, comunicándose a través de una API RESTful.

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENTE (Frontend)                          │
│                                                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐  │
│  │  SuperAdmin   │  │    Admin     │  │       Storefront          │  │
│  │  /superadmin  │  │   /admin     │  │       /tienda             │  │
│  └──────┬───────┘  └──────┬───────┘  └────────────┬─────────────┘  │
│         │                  │                        │                │
│         └──────────────────┼────────────────────────┘                │
│                            │                                        │
│              React 19 + Vite + Tailwind CSS                         │
│              React Router v7 (SPA)                                  │
│              Zustand (State Management)                             │
│              React Query (Server State)                             │
│              Axios (HTTP Client + JWT Interceptors)                 │
└────────────────────────────┬────────────────────────────────────────┘
                             │ API REST (HTTP/JSON)
                             │ JWT Authentication
┌────────────────────────────┴────────────────────────────────────────┐
│                       SERVIDOR (Backend)                            │
│                                                                     │
│              Spring Boot 4.0.2 (Java 17)                            │
│              Spring Security + JWT (jjwt 0.12.6)                    │
│              Spring Data JPA / Hibernate                            │
│              Spring Validation                                      │
│              Lombok                                                 │
│              API RESTful                                            │
│                                                                     │
└────────────────────────────┬────────────────────────────────────────┘
                             │ JDBC / JPA
┌────────────────────────────┴────────────────────────────────────────┐
│                     BASE DE DATOS                                   │
│                                                                     │
│              MySQL 8.x                                              │
│              Base de datos: licores_drinkgo                         │
│              Charset: utf8mb4 (soporte unicode completo)            │
│              60+ tablas relacionales                                │
│              Arquitectura multi-tenant por negocio_id               │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 5.2 Patrón Arquitectónico del Backend

El backend sigue el patrón de arquitectura en capas:

```
┌─────────────────────────────────────┐
│         Controller Layer            │  ← Manejo de peticiones HTTP, validación de entrada
│  (64 controladores REST)            │
├─────────────────────────────────────┤
│          Service Layer              │  ← Lógica de negocio, reglas, transacciones
│  (Interfaces + Implementaciones JPA)│
├─────────────────────────────────────┤
│        Repository Layer             │  ← Acceso a datos, consultas JPA
│  (Spring Data JPA Repositories)     │
├─────────────────────────────────────┤
│          Entity Layer               │  ← Modelo de dominio, entidades JPA
│  (60+ entidades mapeadas)           │
├─────────────────────────────────────┤
│           DTO Layer                 │  ← Objetos de transferencia de datos
│  (Request/Response DTOs)            │
├─────────────────────────────────────┤
│         Security Layer              │  ← Autenticación JWT, filtros, configuración
│  (JwtUtil, JwtFilter, SecurityConfig)│
└─────────────────────────────────────┘
```

### 5.3 Patrón Arquitectónico del Frontend

El frontend sigue una arquitectura modular por ámbitos (scopes):

```
src/
├── admin/           ← Panel de administración del negocio
│   ├── catalogo/         Gestión de productos, categorías, marcas, combos, promociones
│   ├── compras/          Proveedores y órdenes de compra
│   ├── configuracion/    Configuración del negocio, sedes, almacenes
│   ├── devoluciones/     Gestión de devoluciones
│   ├── facturacion/      Facturación electrónica
│   ├── inventario/       Stock, lotes, movimientos, alertas
│   ├── pedidos/          Gestión de pedidos (delivery, recojo, mesa)
│   ├── reportes/         Reportes y analítica
│   ├── usuarios/         Usuarios, roles, permisos, clientes
│   ├── ventas/           POS, cajas, sesiones, comprobantes
│   ├── components/       Componentes compartidos del admin
│   ├── hooks/            Custom hooks del admin
│   ├── layouts/          Layouts del panel admin
│   ├── pages/            Páginas principales del admin
│   ├── routes/           Definición de rutas del admin
│   └── services/         Servicios API del admin
│
├── superadmin/      ← Panel de gestión de la plataforma
│   ├── components/       Componentes del superadmin
│   ├── hooks/            Custom hooks del superadmin
│   ├── layouts/          Layouts del panel superadmin
│   ├── pages/            Páginas del superadmin
│   ├── routes/           Rutas del superadmin
│   └── services/         Servicios API del superadmin
│
├── storefront/      ← Tienda online para clientes finales
│   ├── components/       Componentes del storefront
│   ├── hooks/            Custom hooks del storefront
│   ├── layouts/          Layouts de la tienda
│   ├── pages/            Páginas de la tienda
│   ├── routes/           Rutas del storefront
│   ├── services/         Servicios API del storefront
│   └── stores/           Estado local del storefront
│
├── config/          ← Configuración global (Axios, React Query)
├── shared/          ← Utilidades, hooks y servicios compartidos
├── stores/          ← Estado global (Zustand)
└── assets/          ← Recursos estáticos
```

### 5.4 Modelo Multi-Tenant

El aislamiento de datos entre negocios se logra mediante una estrategia de **tenant por discriminador** (columna `negocio_id`). Cada tabla de datos operativos incluye una referencia al negocio propietario, garantizando que:

- Un negocio solo puede acceder a sus propios datos
- Las consultas siempre filtran por `negocio_id`
- El backend valida la pertenencia del recurso al tenant autenticado
- No existe cruce de datos entre negocios

```
                    ┌─────────────────┐
                    │   Plataforma    │
                    │   (SuperAdmin)  │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
    ┌───────▼──────┐ ┌──────▼───────┐ ┌──────▼───────┐
    │  Negocio A   │ │  Negocio B   │ │  Negocio C   │
    │  negocio_id=1│ │  negocio_id=2│ │  negocio_id=3│
    ├──────────────┤ ├──────────────┤ ├──────────────┤
    │ Sedes        │ │ Sedes        │ │ Sedes        │
    │ Usuarios     │ │ Usuarios     │ │ Usuarios     │
    │ Productos    │ │ Productos    │ │ Productos    │
    │ Inventario   │ │ Inventario   │ │ Inventario   │
    │ Ventas       │ │ Ventas       │ │ Ventas       │
    │ Pedidos      │ │ Pedidos      │ │ Pedidos      │
    │ ...          │ │ ...          │ │ ...          │
    └──────────────┘ └──────────────┘ └──────────────┘
```

---

## 6. TECNOLOGÍAS UTILIZADAS

### 6.1 Backend

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **Java** | 17 | Lenguaje de programación principal del servidor |
| **Spring Boot** | 4.0.2 | Framework principal para la API REST |
| **Spring Security** | (incluido en Spring Boot) | Autenticación y autorización |
| **Spring Data JPA** | (incluido en Spring Boot) | Capa de acceso a datos con Hibernate |
| **Spring Validation** | (incluido en Spring Boot) | Validación de datos de entrada |
| **Spring Actuator** | (incluido en Spring Boot) | Monitoreo y health checks |
| **JJWT (io.jsonwebtoken)** | 0.12.6 | Generación y validación de tokens JWT |
| **Lombok** | (última estable) | Reducción de boilerplate en entidades |
| **MySQL Connector/J** | (última estable) | Driver JDBC para MySQL |
| **Maven** | (wrapper incluido) | Gestión de dependencias y build |

### 6.2 Frontend

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **React** | 19.2 | Librería de UI principal |
| **Vite** | 7.2.4 | Bundler y servidor de desarrollo |
| **React Router DOM** | 7.13 | Enrutamiento SPA |
| **Tailwind CSS** | 3.4.17 | Framework de estilos utility-first |
| **Zustand** | 5.0.11 | Manejo de estado global |
| **TanStack React Query** | 5.90 | Gestión de estado del servidor y caché |
| **Axios** | 1.13.5 | Cliente HTTP con interceptores JWT |
| **React Hook Form** | 7.71 | Gestión de formularios |
| **Zod** | 4.3 | Validación de esquemas |
| **Recharts** | 3.8 | Gráficos y visualizaciones |
| **Lucide React** | 0.575 | Iconografía |
| **React Hot Toast** | 2.6 | Notificaciones toast |
| **date-fns** | 4.1 | Manipulación de fechas |
| **clsx** | 2.1 | Utilidad para clases CSS condicionales |
| **pnpm** | (última estable) | Gestor de paquetes (obligatorio) |

### 6.3 Base de Datos

| Tecnología | Versión | Propósito |
|-----------|---------|-----------|
| **MySQL** | 8.x | Sistema de gestión de base de datos relacional |
| **Charset** | utf8mb4 | Soporte completo de caracteres Unicode |
| **Collation** | utf8mb4_unicode_ci | Comparación de cadenas insensible a mayúsculas |

### 6.4 Herramientas de Desarrollo

| Herramienta | Propósito |
|------------|-----------|
| **VS Code** | IDE principal de desarrollo |
| **GitHub Copilot** | Asistente de programación con IA |
| **ESLint** | Linter de código JavaScript/JSX |
| **PostCSS + Autoprefixer** | Procesamiento de CSS |
| **Git** | Control de versiones |

---

## 7. ESTRUCTURA DE LA BASE DE DATOS

### 7.1 Diagrama General

La base de datos `licores_drinkgo` está organizada en **7 bloques funcionales** con más de **60 tablas** relacionales:

#### Bloque 1: Infraestructura SaaS y Negocios
| Tabla | Descripción |
|-------|-------------|
| `planes_suscripcion` | Planes comerciales de la plataforma con límites y funcionalidades |
| `negocios` | Licorerías/tenants registrados en la plataforma |
| `suscripciones` | Relación negocio-plan con estado y periodo de vigencia |
| `facturas_suscripcion` | Facturación recurrente de las suscripciones |

#### Bloque 2: Usuarios, Roles, Permisos y Seguridad
| Tabla | Descripción |
|-------|-------------|
| `usuarios_plataforma` | SuperAdmins, soporte y programadores de la plataforma |
| `modulos_sistema` | Módulos del sistema para control de acceso |
| `permisos_sistema` | Permisos granulares por módulo (ver, crear, editar, eliminar, exportar, aprobar) |
| `modulos_negocio` | Módulos activos por negocio según su plan |
| `roles` | Roles por negocio (admin, cajero, inventarista, etc.) |
| `roles_permisos` | Asignación de permisos a roles |
| `usuarios` | Usuarios de cada negocio |
| `usuarios_roles` | Asignación de roles a usuarios |
| `sesiones_usuario` | Sesiones JWT activas |
| `registros_auditoria` | Log de auditoría completo con severidad |

#### Bloque 3: Sedes, Almacenes y Configuración
| Tabla | Descripción |
|-------|-------------|
| `sedes` | Sedes/sucursales del negocio con horarios (JSON) y configuraciones |
| `usuarios_sedes` | Asignación de usuarios a sedes |
| `almacenes` | Almacenes por sede (general, refrigerado, etc.) |
| `zonas_delivery` | Zonas de entrega con distritos (JSON) y tarifas |
| `mesas` | Mesas por sede para consumo en local |
| `metodos_pago` | Métodos de pago configurados por negocio |

#### Bloque 4: Catálogo de Productos
| Tabla | Descripción |
|-------|-------------|
| `categorias` | Categorías de productos (con indicador de alcohólica) |
| `marcas` | Marcas de productos |
| `unidades_medida` | Unidades de medida con factor de conversión |
| `productos` | Catálogo de productos con campos especializados para licores |
| `combos` | Combos promocionales con precio especial |
| `detalle_combos` | Composición de productos dentro de un combo |
| `promociones` | Promociones con tipo de descuento, vigencia y restricciones |
| `condiciones_promocion` | Reglas y condiciones avanzadas de promociones |

#### Bloque 5: Inventario
| Tabla | Descripción |
|-------|-------------|
| `stock_inventario` | Stock consolidado por producto/almacén |
| `lotes_inventario` | Lotes con fecha de vencimiento, costo y gestión FIFO |
| `movimientos_inventario` | Kardex completo de movimientos (entradas, salidas, ajustes, transferencias) |

#### Bloque 6: Proveedores y Compras
| Tabla | Descripción |
|-------|-------------|
| `proveedores` | Proveedores del negocio con datos fiscales y bancarios |
| `productos_proveedor` | Catálogo de productos por proveedor |
| `ordenes_compra` | Órdenes de compra con flujo de aprobación |
| `detalle_ordenes_compra` | Detalle de productos por orden de compra |

#### Bloque 7: Ventas, POS, Pedidos y Facturación
| Tabla | Descripción |
|-------|-------------|
| `cajas_registradoras` | Cajas registradoras por sede |
| `sesiones_caja` | Apertura/cierre de caja con cuadre |
| `movimientos_caja` | Retiros, depósitos y gastos menores |
| `ventas` | Registro de ventas con tipo de comprobante |
| `detalle_ventas` | Productos vendidos por venta |
| `pagos_venta` | Pagos recibidos por venta (soporte multi-pago) |
| `pedidos` | Pedidos de todos los canales (POS, online, delivery, mesa) |
| `detalle_pedidos` | Productos por pedido |
| `pagos_pedido` | Pagos de pedidos |
| `seguimiento_pedidos` | Tracking de estados de pedidos |
| `cuentas_mesa` | Cuentas abiertas en mesas |
| `detalle_cuentas_mesa` | Productos por cuenta de mesa |
| `devoluciones` | Devoluciones de ventas |
| `detalle_devoluciones` | Productos devueltos |
| `series_facturacion` | Series de comprobantes electrónicos por sede |
| `documentos_facturacion` | Documentos electrónicos (boletas, facturas, notas) |
| `detalle_documentos_facturacion` | Detalle de documentos de facturación |
| `gastos` | Gastos operativos del negocio |
| `categorias_gasto` | Categorías de gastos |
| `clientes` | Base de clientes del negocio |
| `configuracion_tienda_online` | Configuración del storefront por negocio |
| `paginas_tienda_online` | Páginas CMS de la tienda online |

### 7.2 Relaciones Principales

```
planes_suscripcion ──1:N──> suscripciones ──N:1──> negocios
                                                      │
                    ┌─────────────────────────────────┤
                    │              │                   │
                    ▼              ▼                   ▼
                  sedes        usuarios              roles
                    │              │                   │
                    ▼              ▼                   ▼
               almacenes    usuarios_roles      roles_permisos
                    │              │                   │
                    ▼              ▼                   ▼
           stock_inventario  usuarios_sedes    permisos_sistema
                    │
                    ▼
           lotes_inventario
                    │
                    ▼
        movimientos_inventario

negocios ──1:N──> productos ──1:N──> detalle_ventas ──N:1──> ventas
                      │
                      ▼
              productos_proveedor ──N:1──> proveedores

ventas ──1:N──> pagos_venta
ventas ──1:N──> devoluciones ──1:N──> detalle_devoluciones

negocios ──1:N──> pedidos ──1:N──> detalle_pedidos
                      │
                      ▼
              seguimiento_pedidos

negocios ──1:N──> cajas_registradoras ──1:N──> sesiones_caja ──1:N──> movimientos_caja
```

---

## 8. MÓDULOS DEL SISTEMA

### 8.1 Panel SuperAdmin — Gestión de Plataforma (6 módulos)

#### Módulo 1: Dashboard de Plataforma
- **Descripción:** Panel ejecutivo con métricas clave de toda la plataforma.
- **Funcionalidades:**
  - Total de negocios activos, suspendidos y cancelados
  - Ingresos mensuales/anuales de suscripciones
  - Gráficos de crecimiento y tendencias
  - Métricas de retención de clientes
  - Tasa de conversión de planes

#### Módulo 2: Gestión de Negocios (Tenants)
- **Descripción:** Administración completa de todas las licorerías registradas.
- **Funcionalidades:**
  - Listado con filtros avanzados (estado, plan, fecha de registro)
  - Registro de nuevo negocio con datos fiscales (RUC, razón social)
  - Configuración de sede principal inicial
  - Detalle del negocio: historial de suscripciones, usuarios, estadísticas
  - Gestión de sedes: crear, editar, desactivar (validación de límites del plan)
  - Suspender/reactivar negocios
  - Cambiar plan del negocio

#### Módulo 3: Planes de Suscripción
- **Descripción:** Gestión de los planes comerciales de la plataforma.
- **Funcionalidades:**
  - CRUD completo de planes
  - Configuración de límites operativos (sedes, usuarios, productos, almacenes)
  - Activación/desactivación de funcionalidades por plan
  - Configuración de módulos habilitados por plan
  - Vista comparativa de planes

#### Módulo 4: Facturación de Suscripciones
- **Descripción:** Control de facturación recurrente y pagos.
- **Funcionalidades:**
  - Listado de facturas con filtros por estado
  - Detalle de factura con historial de pagos
  - Control de morosidad con indicadores visuales
  - Generación de facturas manuales
  - Suspensión por morosidad

#### Módulo 5: Configuración Global
- **Descripción:** Configuraciones que afectan a toda la plataforma.
- **Funcionalidades:**
  - Parámetros globales (impuestos, monedas, métodos de pago)
  - Gestión de usuarios bloqueados por intentos fallidos de inicio de sesión

#### Módulo 6: Auditoría y Logs
- **Descripción:** Registro y consulta de actividades críticas.
- **Funcionalidades:**
  - Tabla de acciones críticas con filtros por usuario, fecha, módulo y severidad
  - Detalle de evento: usuario, IP, datos antes/después del cambio
  - Exportación de logs

---

### 8.2 Panel Admin — Gestión de Negocio (10 módulos)

#### Módulo 1: Dashboard del Negocio
- **Descripción:** Panel principal con métricas del negocio.
- **Funcionalidades:**
  - Ventas del día/semana/mes
  - Productos más vendidos
  - Stock crítico y alertas
  - Pedidos pendientes
  - Gráficos de tendencias
  - Vista por sede con selector

#### Módulo 2: Configuración del Negocio, Sedes y Storefront
- **Descripción:** Configuración general del negocio, sedes, almacenes y tienda online.
- **Funcionalidades:**
  - Datos del negocio (RUC, razón social, logo, branding)
  - Parámetros del sistema (moneda, IGV, formato de fecha)
  - Métodos de pago (CRUD con configuración por canal: POS/online)
  - Lista de sedes y edición de datos operativos
  - Gestión de almacenes por sede
  - Horarios de operación (JSON configurable por día)
  - Restricciones de alcohol (horarios, días restringidos)
  - Zonas de delivery con tarifas y distritos
  - Módulo de mesas (CRUD con estados)
  - Configuración de tienda online (slug, logo, banner, colores, SEO)
  - Personalización visual del Storefront
  - Métodos de pago y entrega online
  - Páginas CMS (términos, privacidad, FAQ)

#### Módulo 3: Seguridad, Usuarios y Clientes
- **Descripción:** Gestión de usuarios, roles, permisos granulares y clientes.
- **Funcionalidades:**
  - CRUD de usuarios con asignación de roles y sedes
  - Gestión de roles (predefinidos y personalizados)
  - Matriz de permisos por módulo (ver, crear, editar, eliminar, exportar, aprobar)
  - Asignación de usuarios a sedes
  - Logs de auditoría del negocio
  - CRUD de clientes (DNI/RUC, dirección, historial de compras)
  - Detalle de cliente con métricas (total gastado, productos preferidos)

#### Módulo 4: Catálogo de Productos, Descuentos y Promociones
- **Descripción:** Gestión completa del catálogo de productos.
- **Funcionalidades:**
  - CRUD de productos con campos especializados para licores:
    - Tipo de bebida (cerveza, vino, pisco, ron, vodka, etc.)
    - Grado alcohólico (%)
    - Volumen (ml, L)
    - Origen/País
  - Precios de compra y venta, margen automático
  - Configuración de stock mínimo, máximo y punto de reorden
  - CRUD de categorías (con indicador de alcohólica)
  - CRUD de marcas (con logo y país de origen)
  - CRUD de unidades de medida (con factor de conversión)
  - Combos promocionales (composición de productos con precio especial)
  - Gestión de imágenes de productos
  - Promociones: porcentaje, monto fijo, 2x1, 3x2, escalonado
  - Condiciones de promoción: vigencia, días, horarios, sedes, canales

#### Módulo 5: Gestión de Inventario
- **Descripción:** Control de stock con lotes FIFO, kardex y alertas.
- **Funcionalidades:**
  - Stock consolidado con filtros por sede, almacén, categoría
  - Entrada de lotes con fecha de vencimiento, costo y proveedor
  - Listado de lotes activos con acciones de ajuste:
    - Ajuste por tipo (entrada/salida)
    - Motivos: merma, robo, error de conteo, muestra, donación, vencimiento
    - Acción tomada: descarte, devolución a proveedor, ajuste contable
  - Kardex completo de movimientos (entradas, salidas, ajustes, transferencias)
  - Transferencias entre almacenes con estados
  - Alertas: stock bajo, stock crítico, próximo vencimiento, vencidos
  - Exportación a Excel, PDF, CSV

#### Módulo 6: Proveedores y Compras
- **Descripción:** Gestión de proveedores y proceso completo de compras.
- **Funcionalidades:**
  - CRUD de proveedores (RUC, datos bancarios, términos de pago)
  - Catálogo de productos por proveedor
  - Órdenes de compra con flujo: borrador → pendiente → aprobada → recibida
  - Detalle de orden con productos, cantidades y precios
  - Aprobación/rechazo de órdenes
  - Recepción de mercancía (genera entrada de inventario automática)
  - Devoluciones a proveedor
  - Cuentas por pagar y registro de pagos

#### Módulo 7: Ventas, Punto de Venta (POS) y Pedidos
- **Descripción:** Sistema completo de ventas, POS optimizado, pedidos multicanal y devoluciones.
- **Funcionalidades:**
  - **POS:**
    - Interfaz optimizada para ventas rápidas
    - Búsqueda de productos por nombre, código o escaneo
    - Carrito con cantidades editables, descuentos por ítem
    - Selector de cliente y tipo de comprobante (boleta/factura)
    - Múltiples métodos de pago simultáneos
    - Verificación de edad para alcohol
  - **Cajas:**
    - CRUD de cajas registradoras por sede
    - Apertura de sesión con monto inicial
    - Cierre con cuadre (monto esperado vs. real, diferencias)
    - Movimientos de caja (retiros, depósitos, gastos menores)
    - Historial de sesiones
  - **Ventas:**
    - Listado con filtros por fecha, sede, cajero, comprobante
    - Suspender y recuperar ventas en curso
    - Historial de comprobantes emitidos
    - Reimpresión de comprobantes
  - **Pedidos:**
    - Listado multicanal (delivery, recojo, mesa, online)
    - Flujo de estados: Pendiente → Confirmado → En Preparación → Listo → En Camino → Entregado
    - Gestión de delivery (asignación de repartidores, zonas)
    - Gestión de mesas (abrir/cerrar cuenta, dividir cuenta)
    - Pedidos para recojo en tienda
    - Cancelaciones con gestión de reembolso
  - **Devoluciones:**
    - Registro con motivo y tipo de solución
    - Flujo de aprobación/rechazo
    - Gestión de reembolso
    - Reintegración al inventario

#### Módulo 8: Facturación Electrónica (SUNAT)
- **Descripción:** Emisión y gestión de comprobantes electrónicos.
- **Funcionalidades:**
  - Series de comprobantes por sede (boleta, factura, notas)
  - Listado de documentos electrónicos con estados
  - Generación de XML, firma digital y envío a SUNAT
  - Notas de crédito y débito
  - Anulación de comprobantes (comunicación de baja)
  - Reenvío de comprobantes fallidos
  - Descarga de XML/PDF

#### Módulo 9: Gastos e Ingresos
- **Descripción:** Control de gastos operativos y flujo financiero.
- **Funcionalidades:**
  - Registro de gastos con categoría, comprobante adjunto y método de pago
  - Categorías de gasto (alquiler, servicios, salarios, marketing, etc.)
  - Flujo de aprobación de gastos
  - Gastos recurrentes programados
  - Resumen financiero: ingresos vs. gastos, utilidad neta

#### Módulo 10: Reportes y Analítica
- **Descripción:** Reportes avanzados y analítica del negocio.
- **Funcionalidades:**
  - Reporte de ventas por periodo, sede, cajero, método de pago
  - Productos más vendidos (por cantidad e ingresos)
  - Reporte de inventario (valorización, stock bajo, sin movimiento)
  - Kardex de movimientos por producto/almacén
  - Reporte de compras por proveedor y periodo
  - Reporte de gastos por categoría y periodo
  - Reporte financiero (ingresos vs gastos, márgenes)
  - Reporte de clientes frecuentes
  - Reporte de pedidos por modalidad y tiempos de entrega
  - Exportación a Excel y PDF

---

### 8.3 Storefront — Tienda Online (10 páginas)

| Página | Descripción |
|--------|-------------|
| **Home** | Página de inicio con productos destacados y banner |
| **Catálogo** | Listado de productos con filtros por categoría y búsqueda |
| **Detalle de Producto** | Información completa del producto con imágenes y botón agregar al carrito |
| **Carrito** | Carrito de compras con cantidades editables y resumen de precios |
| **Checkout** | Proceso de pago: dirección de entrega, método de pago, confirmación |
| **Login** | Inicio de sesión para clientes registrados |
| **Registro** | Registro de nuevos clientes |
| **Perfil** | Datos personales y gestión de cuenta |
| **Mis Pedidos** | Historial y seguimiento de pedidos realizados |
| **Devoluciones** | Solicitud y seguimiento de devoluciones |

---

## 9. SEGURIDAD DEL SISTEMA

### 9.1 Autenticación

- **Método:** JSON Web Token (JWT) con la librería JJWT 0.12.6
- **Flujo de autenticación:**
  1. El usuario envía credenciales (email + contraseña) al endpoint de login
  2. El servidor valida las credenciales contra contraseñas hasheadas (BCrypt)
  3. Se genera un JWT firmado con clave secreta
  4. El token se envía al cliente y se almacena en memoria (Zustand)
  5. Cada petición incluye el token en el header `Authorization: Bearer <token>`
  6. El filtro JWT (`JwtFilter`) valida el token en cada request

- **Protección contra ataques de fuerza bruta:**
  - Bloqueo de cuenta tras múltiples intentos fallidos (`intentos_fallidos_acceso`)
  - Campo `bloqueado_hasta` con timestamp de desbloqueo
  - Panel de usuarios bloqueados en SuperAdmin

### 9.2 Autorización

- **Sistema de roles y permisos granulares:**
  - Los roles son definidos por negocio
  - Cada rol tiene permisos asignados por módulo
  - Permisos granulares: ver, crear, editar, eliminar, exportar, aprobar, configurar
  - Validación en backend y ocultamiento en frontend

- **Aislamiento multi-tenant:**
  - Toda consulta de datos filtra por `negocio_id`
  - Los usuarios solo acceden a datos de su negocio
  - Los SuperAdmins operan a nivel de plataforma

- **Control de módulos por plan:**
  - Los planes definen qué módulos están disponibles
  - La tabla `modulos_negocio` controla la activación por negocio
  - El frontend oculta módulos no habilitados

### 9.3 Seguridad de la API

- **Spring Security** configurado con:
  - CORS habilitado para el frontend
  - CSRF deshabilitado (API stateless con JWT)
  - Sesiones stateless (`SessionCreationPolicy.STATELESS`)
  - Filtro JWT ejecutado antes del filtro de autenticación de Spring
  - Endpoints públicos definidos (login, registro, storefront público)
  - Endpoints protegidos requieren autenticación

### 9.4 Auditoría

- **Registro completo de acciones:** La tabla `registros_auditoria` almacena:
  - Usuario que realizó la acción
  - Tipo de acción y entidad afectada
  - Valores anteriores y nuevos (JSON)
  - Dirección IP y agente de usuario
  - Módulo afectado
  - Severidad (info, advertencia, crítico)
  - Timestamp preciso

---

## 10. INTERFACES DE USUARIO

### 10.1 Principios de Diseño

- **Diseño limpio, minimalista y profesional**
- **Tailwind CSS** como único framework de estilos
- **Lucide React** como librería exclusiva de iconos
- **Componentes reutilizables** en carpetas `components/ui/`
- **Diseño responsivo** con enfoque mobile-first para POS
- **Idioma:** Todo el texto visible al usuario está en español

### 10.2 Panel SuperAdmin

- **Layout:** Barra lateral con navegación y área de contenido principal
- **Páginas principales:**
  - Dashboard con gráficos y métricas de la plataforma
  - Gestión de negocios (tabla, formularios, detalle)
  - Planes de suscripción (comparativa, CRUD)
  - Facturación de suscripciones
  - Configuración global
  - Auditoría y logs
  - Usuarios bloqueados
  - Mi perfil
  - Reportes de plataforma
  - Panel de programadores con negocios asignados

### 10.3 Panel Admin

- **Layout:** Barra lateral con navegación por módulos y selector de sede
- **Páginas principales:**
  - Dashboard con métricas del negocio
  - Catálogo (productos, categorías, marcas, combos, promociones)
  - Inventario (stock, lotes, movimientos, transferencias)
  - Compras (proveedores, órdenes, recepción)
  - Ventas (POS en pantalla completa, cajas, sesiones, historial)
  - Pedidos (listado multicanal, detalle, tracking)
  - Devoluciones (registro, aprobación, reembolso)
  - Facturación (series, documentos, SUNAT)
  - Usuarios (gestión, roles, permisos, clientes)
  - Configuración (negocio, sedes, almacenes, storefront)
  - Reportes (múltiples reportes con filtros y exportación)
  - Selección de sede (al iniciar sesión)
  - Mi perfil

### 10.4 Storefront

- **Layout:** Header con navegación, logo del negocio y carrito
- **Diseño** personalizable por cada negocio (colores, logo, banner)
- **Páginas:** Home, catálogo, detalle de producto, carrito, checkout, login, registro, perfil, pedidos, devoluciones

---

## 11. REQUERIMIENTOS DEL SISTEMA

### 11.1 Requerimientos de Hardware (Servidor)

| Componente | Requerimiento Mínimo | Requerimiento Recomendado |
|-----------|---------------------|--------------------------|
| **Procesador** | 2 núcleos | 4 núcleos |
| **Memoria RAM** | 4 GB | 8 GB |
| **Almacenamiento** | 20 GB SSD | 50 GB SSD |
| **Red** | 10 Mbps | 100 Mbps |

### 11.2 Requerimientos de Software (Servidor)

| Software | Versión Mínima |
|---------|---------------|
| **Java JDK** | 17 |
| **MySQL** | 8.0 |
| **Maven** | 3.8+ (o wrapper incluido) |
| **Node.js** | 18+ (para build del frontend) |
| **pnpm** | 8+ |

### 11.3 Requerimientos del Cliente

| Componente | Requerimiento |
|-----------|--------------|
| **Navegador** | Google Chrome 90+, Firefox 90+, Edge 90+, Safari 15+ |
| **Resolución** | Mínimo 1024x768 (óptimo 1920x1080) |
| **Conexión** | Internet estable (mín. 2 Mbps) |
| **JavaScript** | Habilitado |

---

## 12. DICCIONARIO DE DATOS

### 12.1 Tablas Principales

#### Tabla: `negocios`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT UNSIGNED PK | Identificador único del negocio |
| uuid | CHAR(36) UNIQUE | Identificador universal único |
| razon_social | VARCHAR(200) | Razón social legal del negocio |
| nombre_comercial | VARCHAR(200) | Nombre comercial visible |
| ruc | VARCHAR(20) | Registro Único de Contribuyentes |
| email | VARCHAR(150) | Correo electrónico del negocio |
| estado | ENUM | activo, suspendido, cancelado, pendiente |
| aplica_igv | TINYINT(1) | Si aplica IGV en ventas |
| porcentaje_igv | DECIMAL(5,2) | Porcentaje de IGV (default 18.00) |

#### Tabla: `usuarios`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT UNSIGNED PK | Identificador único del usuario |
| uuid | CHAR(36) UNIQUE | Identificador universal único |
| negocio_id | BIGINT UNSIGNED FK | Negocio al que pertenece |
| email | VARCHAR(150) | Correo electrónico (login) |
| hash_contrasena | VARCHAR(255) | Contraseña encriptada (BCrypt) |
| nombres | VARCHAR(100) | Nombres del usuario |
| apellidos | VARCHAR(100) | Apellidos del usuario |
| esta_activo | TINYINT(1) | Estado activo/inactivo |

#### Tabla: `productos`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT UNSIGNED PK | Identificador único del producto |
| negocio_id | BIGINT UNSIGNED FK | Negocio propietario |
| categoria_id | BIGINT UNSIGNED FK | Categoría del producto |
| marca_id | BIGINT UNSIGNED FK | Marca del producto |
| nombre | VARCHAR(200) | Nombre del producto |
| codigo_interno | VARCHAR(50) | Código interno del negocio |
| codigo_barras | VARCHAR(50) | Código de barras (EAN/UPC) |
| precio_compra | DECIMAL(10,2) | Precio de referencia de compra |
| precio_venta | DECIMAL(10,2) | Precio de venta al público |
| grado_alcoholico | DECIMAL(5,2) | Grado de alcohol (%) |
| volumen_ml | INT | Volumen en mililitros |
| tipo_bebida | VARCHAR(50) | Tipo (cerveza, vino, pisco, etc.) |
| stock_minimo | INT | Nivel mínimo de stock |

#### Tabla: `ventas`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT UNSIGNED PK | Identificador único de la venta |
| negocio_id | BIGINT UNSIGNED FK | Negocio que realizó la venta |
| sede_id | BIGINT UNSIGNED FK | Sede donde se realizó |
| sesion_caja_id | BIGINT UNSIGNED FK | Sesión de caja asociada |
| cliente_id | BIGINT UNSIGNED FK | Cliente (opcional) |
| numero_venta | VARCHAR(50) | Número correlativo |
| tipo_comprobante | ENUM | boleta, factura, ticket |
| subtotal | DECIMAL(12,2) | Subtotal antes de impuestos |
| monto_igv | DECIMAL(12,2) | Monto de IGV |
| total | DECIMAL(12,2) | Total de la venta |
| estado | ENUM | completada, anulada, devuelta |

#### Tabla: `pedidos`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT UNSIGNED PK | Identificador único del pedido |
| negocio_id | BIGINT UNSIGNED FK | Negocio que recibe el pedido |
| sede_id | BIGINT UNSIGNED FK | Sede asociada |
| cliente_id | BIGINT UNSIGNED FK | Cliente que realiza el pedido |
| numero_pedido | VARCHAR(50) | Número correlativo |
| tipo_pedido | ENUM | delivery, recojo_tienda, mesa, online |
| estado | ENUM | pendiente, confirmado, en_preparacion, listo, en_camino, entregado, cancelado |
| total | DECIMAL(12,2) | Total del pedido |

#### Tabla: `lotes_inventario`
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT UNSIGNED PK | Identificador único del lote |
| producto_id | BIGINT UNSIGNED FK | Producto del lote |
| almacen_id | BIGINT UNSIGNED FK | Almacén donde se almacena |
| cantidad_actual | INT | Stock actual del lote |
| costo_unitario | DECIMAL(10,2) | Costo unitario de compra |
| fecha_vencimiento | DATE | Fecha de vencimiento del lote |
| fecha_ingreso | DATE | Fecha de ingreso al inventario |

---

## 13. CONCLUSIONES

### 13.1 Logros Alcanzados

1. **Arquitectura Multi-Tenant completa:** Se implementó exitosamente una arquitectura que permite a múltiples negocios operar de forma aislada y segura dentro de la misma plataforma.

2. **Sistema integral de gestión:** DrinkGo cubre el ciclo completo de operaciones de una licorería: desde el registro de productos hasta la venta, inventario, facturación y reportes.

3. **Punto de Venta optimizado:** Se desarrolló un POS funcional con soporte para múltiples métodos de pago, verificación de edad, comprobantes electrónicos y gestión de cajas.

4. **Gestión de inventario avanzada:** Implementación de lotes FIFO con fechas de vencimiento, kardex completo de movimientos, alertas automáticas y transferencias entre almacenes.

5. **Pedidos multicanal:** Soporte para delivery, recojo en tienda, consumo en mesa y pedidos online con tracking de estados.

6. **Tienda online (Storefront):** Portal e-commerce completo con catálogo, carrito, checkout, gestión de cuenta y seguimiento de pedidos.

7. **Seguridad robusta:** Autenticación JWT, roles y permisos granulares, auditoría completa y protección contra ataques de fuerza bruta.

8. **Facturación electrónica:** Sistema preparado para emisión de comprobantes electrónicos (boletas, facturas, notas de crédito/débito) compatible con SUNAT.

9. **Stack tecnológico moderno:** Uso de tecnologías actuales (Spring Boot 4, React 19, Vite 7, Tailwind CSS, Zustand, React Query) que garantizan rendimiento, mantenibilidad y escalabilidad.

10. **Base de datos robusta:** Diseño relacional con más de 60 tablas, integridad referencial mediante foreign keys, índices optimizados y soporte Unicode completo.

### 13.2 Tecnologías y Herramientas Clave

El proyecto demuestra competencias en:
- Desarrollo backend con Java y Spring Boot (API REST, seguridad, JPA)
- Desarrollo frontend con React y ecosistema moderno (hooks, React Query, Zustand)
- Diseño de bases de datos relacionales (MySQL, normalización, integridad referencial)
- Arquitectura de software (multi-tenant, capas, separación frontend/backend)
- Seguridad web (JWT, BCrypt, CORS, roles y permisos)
- Diseño de interfaces de usuario (Tailwind CSS, diseño responsivo)

### 13.3 Trabajo Futuro

- Integración real con SUNAT para facturación electrónica
- Aplicación móvil nativa o PWA
- Integración con pasarelas de pago (Mercado Pago, Niubiz)
- Módulo de contabilidad completo
- Notificaciones push y en tiempo real (WebSockets)
- Despliegue en la nube (AWS, Azure o GCP)

---

**DrinkGo** — Sistema de Gestión Multi-Tenant para Licorerías  
Laboratorio de Programación III — Marzo 2026
