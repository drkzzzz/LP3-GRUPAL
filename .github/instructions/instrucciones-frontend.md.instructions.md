---
applyTo: "frontend/**/*.ts,frontend/**/*.tsx,frontend/**/*.jsx"
---

# 🍸 Instrucciones Frontend para DrinkGo

## 🏗️ Resumen del Proyecto y Arquitectura
- **Proyecto:** DrinkGo (SaaS Multi-Tenant para Licorerías).
- **Stack Tecnológico:** React 19, Vite, Tailwind CSS (o librería UI compatible como Shadcn).
- **Gestor de Paquetes:** Uso estricto de `pnpm` (ejecutar comandos con `pnpm dev`).
- **Sincronización Backend:** Se conecta a una API REST construida en Spring Boot.

## 🧭 Enrutamiento y Jerarquía de Vistas
La aplicación se divide estrictamente en tres áreas operativas aisladas:
1. **SuperAdmin (`/superadmin/*`):** Gestión de la plataforma SaaS, creación de tenants y facturación de suscripciones.
2. **Admin (`/admin/*`):** Operaciones del negocio de la licorería (POS, inventario, catálogo, empleados, reportes).
3. **Storefront (`/tienda/*`):** E-commerce público para clientes finales (catálogo, carrito, checkout).
*Regla:* Los componentes y el estado no deben mezclarse entre estos tres límites.

## 🎨 Estándares de UI/UX (Inspirado en la Elegancia de Pegasus)
- **Modales SOBRE Ventanas Nativas:** NUNCA usar alertas nativas del navegador (`alert()`, `confirm()`, `prompt()`).
  - [cite_start]Usar **Modales (Dialogos) centrados** para confirmaciones rápidas o formularios cortos (ej. "Eliminar Producto", "Avanzar Estado")[cite: 38, 75].
  - [cite_start]Usar **Paneles Laterales (Drawers) o Modales Amplios** para formularios complejos (ej. "Crear Envío", "Registrar Pago") para mantener el contexto visual de la tabla de fondo[cite: 39, 40].
- **Paleta de Colores y Diseño:**
  - [cite_start]Usar dashboards limpios y minimalistas con fondos blancos (`#FFFFFF`) o gris claro (`#F9FAFB`)[cite: 67].
  - [cite_start]Aplicar sombras sutiles (`shadow-sm`) y bordes redondeados (`rounded-lg`) a las tarjetas y contenedores de métricas[cite: 68].
- **Tablas de Datos:**
  - [cite_start]Siempre deben incluir una barra de búsqueda global y controles de paginación[cite: 37, 46].
  - [cite_start]Las columnas de acción deben usar íconos limpios (👁️ Ver, ✏️ Editar, 🗑️ Eliminar) en lugar de botones de texto[cite: 23, 29, 36].
- [cite_start]**Indicadores de Estado (Badges):** Usar etiquetas con colores semánticos para una rápida identificación[cite: 22, 38]:
  - [cite_start]**Verde:** Activo, Entregado, Recibido, Emitido[cite: 54, 63, 70].
  - [cite_start]**Amarillo/Naranja:** Pendiente, En Proceso, Stock Bajo[cite: 38, 53].
  - [cite_start]**Rojo:** Inactivo, Cancelado, Error, Rechazado[cite: 65, 66].

## 💻 Estándares de Código
- **Idioma:** Inglés para variables, funciones y nombres de componentes. Español estrictamente para la interfaz de usuario (UI) y mensajes de retroalimentación al cliente.
- **Gestión de Estado:** Mantener el estado local en los componentes; usar estado global (Zustand/Redux) solo para sesiones de usuario, el carrito de compras o datos compartidos entre módulos.
- **Componentes:** Priorizar componentes funcionales con Hooks. Mantener los archivos modulares, limpios y concisos.
- **Formularios:** Siempre incluir validación en tiempo real antes de enviar la petición al backend en Spring. [cite_start]Los campos obligatorios deben estar marcados claramente con un asterisco rojo (`*`)[cite: 39, 40].

## 🚀 Flujo de Desarrollo
- Siempre construir asumiendo un entorno gestionado por `pnpm`.
- Asegurar un diseño responsivo: Enfoque *Mobile-first* para el módulo POS y el Storefront; diseño optimizado para tablets y escritorio amplio en los dashboards de Admin y SuperAdmin.