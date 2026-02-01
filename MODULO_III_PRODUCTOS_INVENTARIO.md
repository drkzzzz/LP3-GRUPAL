# MÓDULO III: GESTIÓN DE PRODUCTOS E INVENTARIO

**Sistema:** DrinkGo - Sistema Multi-tenant para Licorerías  
**Versión:** 1.0  
**Fecha:** 31 de enero de 2026

---

## Descripción General del Módulo

Este módulo gestiona el catálogo completo de productos, el control de inventario con sistema FIFO para productos perecederos, y las operaciones de compras con proveedores. Es fundamental para el control de stock, la trazabilidad de lotes, y la gestión eficiente de la cadena de suministro en licorerías.

---

## Submódulo 6.3.1: Catálogo de Productos

---

### **RF-INV-001: Registrar Nuevo Producto**

**Prioridad:** Alta

**Descripción:**
El Administrador debe poder registrar nuevos productos en el catálogo del negocio, ingresando información específica para licorerías como grado alcohólico, volumen, origen, tipo de bebida, temperatura de almacenamiento requerida, y configuración de visibilidad para storefront. Este es el punto de entrada para gestionar el inventario de bebidas alcohólicas y complementos.

**Criterios de Aceptación:**
1. El sistema solicita información básica obligatoria:
   - Código único del producto (alfanumérico)
   - Código de barras (opcional, único si se ingresa)
   - Nombre del producto
   - Tipo de producto (cerveza, vino, destilado, crema_licor, espumante, complemento, accesorio)
   - Precio de venta (mayor a 0)
2. El sistema solicita información específica de licorería:
   - Grado alcohólico (% entre 0-100, opcional)
   - Volumen en mililitros (ej: 750ml, 330ml)
   - País de origen
   - Año de cosecha (para vinos, entre 1900 y año actual+5)
   - Región (ej: Rioja, Mendoza, Valle de Colchagua)
   - Variedad de uva (ej: Cabernet Sauvignon, Merlot)
   - Temperatura de almacenamiento requerida (ambiente, frio, congelado)
3. El sistema permite asignar categoría y marca (de catálogos previamente creados).
4. El sistema permite configurar unidades:
   - Unidad de medida (unidad, paquete, caja, botella)
   - Unidades por paquete (ej: six-pack = 6)
5. El sistema permite configurar precios:
   - Precio de venta (obligatorio)
   - Precio mayorista (opcional)
   - Costo de referencia (opcional)
6. El sistema permite marcar si el producto:
   - Es alcohólico (`es_alcoholico = TRUE`) - CRÍTICO para validación de edad
   - Requiere refrigeración (`requiere_refrigeracion = TRUE`)
   - Es visible en storefront (`visible_storefront = TRUE/FALSE`)
   - Es destacado en storefront (`destacado = TRUE/FALSE`)
7. El sistema valida que el código sea único dentro del tenant.
8. El sistema establece `activo = TRUE` por defecto.
9. El sistema registra quién creó el producto y cuándo.
10. El sistema genera UUID único para el producto.

**Consideraciones Multi-tenant:**
- El producto pertenece exclusivamente al tenant (`tenant_id`).
- Constraint: UNIQUE(tenant_id, codigo).
- El código de barras puede repetirse entre tenants (diferentes licorerías pueden vender el mismo producto).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.crear` pueden registrar productos.
- Validar rangos de valores: grado alcohólico 0-100%, año cosecha razonable, precios >= 0.
- Registrar creación en `log_auditoria`.
- Validar formato de código de barras (EAN-13, UPC-A, etc.).

**Consideraciones UX:**
- Wizard de 3 pasos: 1) Información básica, 2) Atributos de licorería, 3) Precios y configuración.
- Autocompletado de marca y categoría con búsqueda incremental.
- Sugerencia automática de código: PROD-001, PROD-002, etc.
- Validación en tiempo real de código único.
- Campos condicionales: si tipo = 'vino', mostrar campos de año cosecha, región, variedad.
- Selector visual de temperatura: ❄️ frío, 🌡️ ambiente, 🧊 congelado.
- Toggle para "Es alcohólico" destacado visualmente.
- Vista previa de cómo se verá en storefront si está habilitado.
- Opción de "Crear y agregar otro" para registro rápido de múltiples productos.

**Reglas de Negocio:**
- Si `es_alcoholico = TRUE`, el sistema validará mayoría de edad en ventas.
- Si `temperatura_requerida = 'frio'` o `requiere_refrigeracion = TRUE`, el producto debe almacenarse en almacén tipo 'frio'.
- El precio de venta debe ser >= costo de referencia (si existe).
- El precio mayorista debe ser <= precio de venta (si existe).
- Los productos con `visible_storefront = FALSE` no aparecen en la tienda online.
- Los productos `destacado = TRUE` aparecen en sección especial del storefront.

---

### **RF-INV-002: Editar Producto**

**Prioridad:** Alta

**Descripción:**
El Administrador debe poder actualizar la información de productos existentes, modificando precios, atributos, visibilidad, y configuraciones, sin afectar el historial de movimientos de inventario ni las ventas ya realizadas.

**Criterios de Aceptación:**
1. El sistema permite editar toda la información del producto excepto el código (inmutable).
2. El sistema permite modificar: nombre, descripción, categoría, marca, tipo de producto.
3. El sistema permite modificar atributos de licorería: grado alcohólico, volumen, origen, año cosecha, región, variedad.
4. El sistema permite modificar precios: precio de venta, precio mayorista, costo de referencia.
5. El sistema permite modificar configuración: temperatura requerida, es_alcoholico, requiere_refrigeracion, visible_storefront, destacado.
6. El sistema valida que el nuevo código de barras no esté duplicado (si se cambia).
7. El sistema registra todos los cambios en `log_auditoria` con valores anteriores y nuevos.
8. El sistema actualiza `actualizado_en` automáticamente.
9. Los cambios de precio NO afectan ventas ya realizadas (se mantiene snapshot histórico).
10. Los cambios de configuración se aplican inmediatamente a nuevas operaciones.
11. Si se cambia `es_alcoholico` de TRUE a FALSE, alertar sobre impacto en validaciones de edad.

**Consideraciones Multi-tenant:**
- Solo se pueden editar productos del mismo tenant.
- Trigger `check_tenant_isolation()` impide cambiar `tenant_id`.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.editar` pueden modificar productos.
- Registrar todos los cambios en `log_auditoria` con detalle completo.
- Validar que el usuario tenga permiso para ver y editar precios (algunos roles no ven costos).
- Alertar si se reduce precio significativamente (posible error o fraude).

**Consideraciones UX:**
- Formulario similar al de creación pero con datos precargados.
- Campo código deshabilitado (no editable).
- Historial de cambios en panel lateral mostrando últimas 10 modificaciones.
- Advertencia si se cambia `es_alcoholico` a FALSE: "Este cambio eliminará validaciones de mayoría de edad".
- Indicador visual de cambios no guardados.
- Botón de "Revertir cambios" antes de guardar.
- Vista comparativa de precios: "Precio anterior: S/ 45.00 → Nuevo: S/ 42.00".
- Confirmación antes de guardar cambios de precio (protección contra errores).

**Reglas de Negocio:**
- Los cambios de precio no afectan ventas históricas (cada venta guarda snapshot del precio).
- Si se marca como no visible en storefront, desaparece inmediatamente de la tienda online.
- Si se cambia la temperatura requerida, puede requerir mover stock entre almacenes.
- Los cambios en `es_alcoholico` afectan futuras transacciones inmediatamente.
- No se puede cambiar el tipo de producto si ya tiene movimientos de inventario (puede afectar reportes).

---

### **RF-INV-003: Desactivar Producto**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder desactivar temporalmente productos del catálogo, evitando que se vendan o aparezcan en listados activos, pero manteniendo todo el historial de movimientos, ventas, y datos del producto para reportes y auditoría.

**Criterios de Aceptación:**
1. El sistema permite seleccionar un producto activo para desactivar.
2. El sistema valida que el producto no tenga stock disponible en ninguna sede (stock = 0).
3. Si tiene stock disponible, el sistema advierte y solicita confirmación: "Este producto tiene stock. ¿Deseas desactivarlo de todos modos?".
4. El sistema solicita motivo de desactivación (opcional): descontinuado, fuera de temporada, problema de calidad.
5. El sistema cambia `activo = FALSE`.
6. El sistema oculta el producto automáticamente del storefront (`visible_storefront = FALSE`).
7. El producto desactivado no aparece en búsquedas ni selectores de POS/Pedidos.
8. El producto desactivado no puede agregarse a nuevas ventas ni pedidos.
9. El sistema mantiene todo el historial: movimientos de inventario, ventas pasadas, lotes.
10. El sistema permite reactivar el producto en cualquier momento.
11. El sistema registra la desactivación en `log_auditoria` con motivo.

**Consideraciones Multi-tenant:**
- Solo se pueden desactivar productos del mismo tenant.
- La desactivación no elimina datos (soft delete).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.desactivar` pueden desactivar productos.
- Requiere confirmación si el producto tiene stock.
- Registrar desactivación en `log_auditoria` con motivo detallado.
- No permitir desactivar si hay pedidos pendientes con ese producto.

**Consideraciones UX:**
- Confirmación clara del impacto: "Este producto dejará de estar disponible para ventas".
- Advertencia destacada si tiene stock: "⚠️ Stock actual: 45 unidades en 2 sedes".
- Campo de texto para motivo de desactivación.
- Opción de "Agotar stock antes de desactivar" que sugiere acciones.
- Badge de "Desactivado" en listado de productos.
- Filtro rápido para ver solo productos activos/desactivados.
- Botón de "Reactivar" fácilmente accesible.

**Reglas de Negocio:**
- Un producto desactivado no puede venderse ni pedirse.
- Los lotes existentes del producto permanecen en inventario pero no pueden usarse para ventas.
- El producto aparece en reportes históricos normalmente.
- Si se reactiva, vuelve a estar disponible inmediatamente.
- Los combos que contienen el producto desactivado también deben desactivarse (o mostrar advertencia).

---

### **RF-INV-004: Cargar Imágenes de Producto**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder subir múltiples imágenes para cada producto, definir cuál es la imagen principal, ordenar las imágenes, y agregar textos alternativos para accesibilidad. Las imágenes son fundamentales para el storefront y para identificación visual en el sistema.

**Criterios de Aceptación:**
1. El sistema permite subir hasta 5 imágenes por producto.
2. El sistema acepta formatos: JPG, JPEG, PNG, WebP.
3. El sistema valida tamaño máximo: 5MB por imagen.
4. El sistema genera automáticamente versiones optimizadas: thumbnail (150x150), medium (600x600), large (1200x1200).
5. El sistema permite definir una imagen como principal (`es_principal = TRUE`).
6. El sistema valida que solo haya una imagen principal por producto (constraint único).
7. El sistema permite ordenar imágenes (campo `orden` numérico).
8. El sistema permite agregar texto alternativo (`alt_text`) para cada imagen (accesibilidad).
9. El sistema permite eliminar imágenes individuales.
10. Las imágenes se almacenan en servidor de archivos o CDN con URLs accesibles.
11. El sistema muestra vista previa de todas las imágenes del producto.
12. Si no hay imagen principal, la primera imagen se establece como principal automáticamente.

**Consideraciones Multi-tenant:**
- Las imágenes se almacenan segregadas por tenant: `/uploads/{tenant_uuid}/productos/{producto_id}/`.
- Solo se acceden imágenes del tenant actual.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.editar` pueden gestionar imágenes.
- Validar tipo MIME de archivos subidos (no solo extensión).
- Sanitizar nombres de archivo.
- Escanear archivos con antivirus (recomendado).
- Limitar rate de subidas (prevenir abuso).
- Registrar subidas en `log_auditoria`.

**Consideraciones UX:**
- Interfaz de drag & drop para subir múltiples imágenes.
- Vista previa de miniaturas en galería.
- Indicador de progreso durante subida.
- Opción de arrastrar imágenes para reordenar.
- Estrella ⭐ indicando imagen principal.
- Editor de recorte de imagen antes de guardar (opcional).
- Zoom al hacer clic en miniatura.
- Campo de texto alternativo por imagen.
- Botón de "Eliminar" con confirmación por imagen.

**Reglas de Negocio:**
- La primera imagen subida se establece como principal automáticamente.
- Solo puede haber una imagen principal por producto.
- Las imágenes se ordenan según campo `orden` (1, 2, 3...).
- Si se elimina la imagen principal, la siguiente en orden se convierte en principal.
- Las imágenes son opcionales pero recomendadas para storefront.
- El texto alternativo es importante para SEO y accesibilidad.

---

### **RF-INV-005: Crear Categoría de Productos**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder crear categorías para clasificar productos, facilitando la navegación en el storefront, reportes segmentados, y gestión del catálogo. Las categorías son específicas del negocio y reflejan su organización de productos.

**Criterios de Aceptación:**
1. El sistema permite crear una categoría con: código único, nombre, descripción, imagen de categoría (opcional), orden de visualización.
2. El sistema valida que el código sea único dentro del tenant.
3. El sistema permite asignar imagen representativa a la categoría (para storefront).
4. El sistema establece `activa = TRUE` por defecto.
5. El sistema registra quién creó la categoría y cuándo.
6. El sistema genera UUID único para la categoría.
7. Las categorías creadas quedan disponibles para asignar a productos inmediatamente.

**Consideraciones Multi-tenant:**
- Las categorías son específicas del tenant.
- Constraint: UNIQUE(tenant_id, codigo).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_categorias` pueden gestionar categorías.
- Registrar cambios en `log_auditoria`.
- Validar tipo MIME de imágenes subidas.

**Consideraciones UX:**
- Formulario simple y claro.
- Drag & drop para subir imagen.
- Vista previa de cómo se verá en storefront.
- Sugerencias de categorías típicas: "Cervezas", "Vinos", "Licores", "Destilados", "Espumantes", "Complementos".
- Validación en tiempo real de código único.

**Reglas de Negocio:**
- Los productos pueden tener una categoría asignada o ninguna (NULL).
- El código de categoría es inmutable después de la creación.
- La imagen de categoría es opcional pero mejora la experiencia en storefront.
- Los productos sin categoría se agrupan en "Sin categoría" o "Otros".

---

### **RF-INV-005A: Editar Categoría de Productos**

**Prioridad:** Baja

**Descripción:**
El Administrador debe poder editar categorías existentes, modificando nombre, descripción, imagen y orden de visualización.

**Criterios de Aceptación:**
1. El sistema permite editar: nombre, descripción, imagen, orden.
2. El sistema no permite editar el código (inmutable).
3. El sistema permite activar/desactivar categorías.
4. El sistema valida que no se desactive una categoría con productos activos asignados (advertencia).
5. El sistema registra modificaciones en `log_auditoria`.

**Consideraciones Multi-tenant:**
- Solo se pueden editar categorías del mismo tenant.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_categorias` pueden editar categorías.
- Registrar cambios en `log_auditoria`.

**Consideraciones UX:**
- Formulario con datos precargados.
- Vista previa de impacto en storefront.
- Advertencia si se desactiva categoría con productos activos.

**Reglas de Negocio:**
- Las categorías desactivadas no aparecen en storefront ni filtros.
- Los cambios se aplican inmediatamente.

---

### **RF-INV-005B: Listar Categorías de Productos**

**Prioridad:** Media

**Descripción:**
El sistema debe mostrar lista de todas las categorías con información resumida y opciones de filtrado.

**Criterios de Aceptación:**
1. El sistema muestra listado de categorías con: imagen, nombre, código, contador de productos, estado.
2. El sistema permite filtrar por: activas, inactivas, todas.
3. El sistema permite ordenar por: nombre, orden, cantidad de productos.
4. El sistema muestra contador de productos por categoría.
5. Las categorías se muestran ordenadas según campo `orden`.

**Consideraciones Multi-tenant:**
- Solo se listan categorías del tenant actual.

**Consideraciones de Seguridad:**
- Requiere permiso: `productos.ver` o `productos.gestionar_categorias`.

**Consideraciones UX:**
- Vista de tarjetas con imagen, nombre y contador.
- Drag & drop para reordenar categorías.
- Indicador visual: "15 productos en esta categoría".

**Reglas de Negocio:**
- El orden define la secuencia en menús y filtros del storefront.

---

### **RF-INV-005C: Desactivar Categoría**

**Descripción:**  
El sistema debe permitir desactivar categorías que ya no se utilizan, manteniendo el historial de productos que la tuvieron asignada.

**Criterios de Aceptación:**
1. El sistema permite desactivar categorías existentes.
2. Al desactivar, la categoría no aparece en listados activos ni en storefront.
3. Los productos que tenían esa categoría asignada mantienen la referencia histórica.
4. Se puede reactivar la categoría en cualquier momento.
5. El sistema registra la desactivación en auditoría.

**Consideraciones Multi-tenant:**
- Solo se pueden desactivar categorías del mismo tenant.
- La desactivación no elimina datos (soft delete).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_categorias` pueden desactivar categorías.
- Registrar desactivación en `log_auditoria` con motivo.
- Advertencia si la categoría tiene productos activos asignados.

**Consideraciones UX:**
- Confirmación clara del impacto: "Esta categoría dejará de aparecer en filtros y storefront".
- Advertencia si tiene productos activos: "⚠️ 15 productos tienen esta categoría asignada".
- Campo opcional para motivo de desactivación.
- Botón de "Reactivar" fácilmente accesible en la lista.
- Badge de "Desactivada" en listado de categorías.

**Reglas de Negocio:**
- Las categorías desactivadas no aparecen en storefront ni filtros.
- Los productos mantienen la referencia a la categoría desactivada (para reportes históricos).
- Si se reactiva, vuelve a estar disponible inmediatamente.
- Las categorías desactivadas pueden eliminarse solo si no tienen productos asociados.

**Prioridad:** Baja

---

### **RF-INV-006: Crear Marca**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder registrar marcas de productos (fabricantes/distribuidores), almacenando información como país de origen, logo, y descripción. Las marcas ayudan a clasificar productos y son importantes para búsquedas y filtros en el storefront.

**Criterios de Aceptación:**
1. El sistema permite crear una marca con: nombre único, país de origen, logo (opcional), descripción.
2. El sistema valida que el nombre sea único dentro del tenant.
3. El sistema permite subir logo de la marca (PNG, JPG; máx 2MB).
4. El sistema establece `activa = TRUE` por defecto.
5. El sistema registra quién creó la marca y cuándo.
6. El sistema genera UUID único para la marca.
7. Las marcas creadas quedan disponibles para asignar a productos inmediatamente.

**Consideraciones Multi-tenant:**
- Las marcas son específicas del tenant.
- Constraint: UNIQUE(tenant_id, nombre).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_marcas` pueden gestionar marcas.
- Validar tipo MIME de logos subidos.
- Registrar cambios en `log_auditoria`.

**Consideraciones UX:**
- Formulario simple y claro.
- Selector de país con banderas.
- Drag & drop para subir logo.
- Vista previa del logo.
- Validación en tiempo real de nombre único.

**Reglas de Negocio:**
- Los productos pueden tener una marca asignada o ninguna (NULL).
- Las marcas importantes en licorerías: Johnnie Walker, Bacardi, Corona, Pilsen, Cusqueña, etc.
- El logo de marca puede mostrarse junto al producto en storefront.
- El país de origen es informativo.

---

### **RF-INV-006A: Editar Marca**

**Prioridad:** Baja

**Descripción:**
El Administrador debe poder editar marcas existentes, modificando nombre, país de origen, logo y descripción.

**Criterios de Aceptación:**
1. El sistema permite editar: nombre, país de origen, logo, descripción.
2. El sistema valida que el nuevo nombre sea único (si se cambia).
3. El sistema permite activar/desactivar marcas.
4. El sistema valida que no se desactive una marca con productos activos asignados (advertencia).
5. El sistema registra modificaciones en `log_auditoria`.

**Consideraciones Multi-tenant:**
- Solo se pueden editar marcas del mismo tenant.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_marcas` pueden editar marcas.
- Registrar cambios en `log_auditoria`.

**Consideraciones UX:**
- Formulario con datos precargados.
- Vista previa del logo actualizado.
- Advertencia si se desactiva marca con productos activos.

**Reglas de Negocio:**
- Las marcas desactivadas no aparecen en filtros activos.
- Los cambios se aplican inmediatamente.

---

### **RF-INV-006B: Listar Marcas**

**Prioridad:** Media

**Descripción:**
El sistema debe mostrar lista de todas las marcas con información resumida y opciones de filtrado.

**Criterios de Aceptación:**
1. El sistema muestra listado de marcas con: logo, nombre, país, contador de productos, estado.
2. El sistema permite filtrar por: activas, inactivas, todas.
3. El sistema permite búsqueda incremental por nombre.
4. El sistema muestra contador de productos por marca.
5. El sistema permite ordenar por: nombre, país, cantidad de productos.

**Consideraciones Multi-tenant:**
- Solo se listan marcas del tenant actual.

**Consideraciones de Seguridad:**
- Requiere permiso: `productos.ver` o `productos.gestionar_marcas`.

**Consideraciones UX:**
- Vista de tarjetas con logo, nombre y país.
- Búsqueda incremental con autocompletado.
- Indicador: "23 productos de esta marca".

**Reglas de Negocio:**
- Las marcas se usan como filtro en storefront y reportes.

---

### **RF-INV-006C: Desactivar Marca**

**Descripción:**  
El sistema debe permitir desactivar marcas que ya no se comercializan, manteniendo el historial de productos asociados.

**Criterios de Aceptación:**
1. El sistema permite desactivar marcas existentes.
2. Al desactivar, la marca no aparece en filtros activos ni en storefront.
3. Los productos que tenían esa marca asignada mantienen la referencia histórica.
4. Se puede reactivar la marca en cualquier momento.
5. El sistema registra la desactivación en auditoría.

**Consideraciones Multi-tenant:**
- Solo se pueden desactivar marcas del mismo tenant.
- La desactivación no elimina datos (soft delete).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_marcas` pueden desactivar marcas.
- Registrar desactivación en `log_auditoria` con motivo.
- Advertencia si la marca tiene productos activos asignados.

**Consideraciones UX:**
- Confirmación clara del impacto: "Esta marca dejará de aparecer en filtros y storefront".
- Advertencia si tiene productos activos: "⚠️ 23 productos tienen esta marca asignada".
- Campo opcional para motivo de desactivación.
- Botón de "Reactivar" fácilmente accesible en la lista.
- Badge de "Desactivada" en listado de marcas.

**Reglas de Negocio:**
- Las marcas desactivadas no aparecen en filtros del storefront ni en selectores.
- Los productos mantienen la referencia a la marca desactivada (para reportes históricos).
- Si se reactiva, vuelve a estar disponible inmediatamente.
- Las marcas desactivadas pueden eliminarse solo si no tienen productos asociados.

**Prioridad:** Baja

---

### **RF-INV-007: Crear Combo Promocional**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder crear combos o packs promocionales que agrupan múltiples productos con un precio especial, como "Pack Chilcano" (pisco + ginger ale + limones) o "Six Pack Cervezas". Los combos son productos virtuales que se descomponen en productos individuales al venderse.

**Criterios de Aceptación:**
1. El sistema permite crear un combo con: código único, nombre, descripción, imagen, precio del combo.
2. El sistema solicita precio original (suma de precios individuales) para calcular ahorro automáticamente.
3. El sistema permite definir vigencia del combo: fecha desde, fecha hasta (opcional).
4. El sistema valida que el código sea único dentro del tenant.
5. El sistema permite configurar visibilidad: `visible_storefront`, `destacado`.
6. El sistema establece `activo = TRUE` por defecto.
7. El sistema registra quién creó el combo y cuándo.
8. Tras crear el combo, permite definir composición inmediatamente (productos incluidos).
9. El sistema calcula ahorro automático: `ahorro = precio_original - precio_combo`.
10. El sistema muestra porcentaje de descuento: `(ahorro / precio_original) * 100`.

**Consideraciones Multi-tenant:**
- Los combos son específicos del tenant.
- Constraint: UNIQUE(tenant_id, codigo).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_combos` pueden crear combos.
- Validar que precio_combo > 0 y < precio_original.
- Registrar creación en `log_auditoria`.

**Consideraciones UX:**
- Wizard de 2 pasos: 1) Información del combo, 2) Agregar productos.
- Cálculo automático de ahorro en tiempo real.
- Vista previa visual del combo con productos incluidos.
- Sugerencia de código: COMBO-001, PACK-CHI, etc.
- Selector de fechas de vigencia con calendario.
- Badge destacando el ahorro: "Ahorra S/ 15 (20%)".

**Reglas de Negocio:**
- El precio del combo debe ser menor que la suma de precios individuales (de lo contrario no es promoción).
- Los combos se tratan como productos especiales en ventas.
- Al vender un combo, se descuenta stock de cada producto individual (no del combo).
- Los combos vencidos (fuera de vigencia) se ocultan automáticamente.
- Los combos pueden contener productos de diferentes categorías.

---

### **RF-INV-008: Editar Combo**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder modificar información de combos existentes, actualizar precios, cambiar vigencia, y ajustar configuraciones, sin afectar ventas históricas del combo.

**Criterios de Aceptación:**
1. El sistema permite editar: nombre, descripción, imagen, precio_combo, precio_original, vigencia.
2. El sistema no permite editar el código (inmutable).
3. El sistema permite modificar visibilidad: visible_storefront, destacado, activo.
4. El sistema recalcula ahorro automáticamente al cambiar precios.
5. El sistema registra cambios en `log_auditoria` con valores anteriores y nuevos.
6. El sistema actualiza `actualizado_en` automáticamente.
7. Los cambios de precio no afectan ventas ya realizadas.
8. El sistema permite modificar la composición del combo (agregar/quitar productos).
9. Si el combo está fuera de vigencia, el sistema lo marca visualmente y sugiere extender fechas o desactivar.

**Consideraciones Multi-tenant:**
- Solo se pueden editar combos del mismo tenant.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_combos` pueden editar combos.
- Registrar cambios en `log_auditoria`.
- Validar coherencia de precios: combo < original.

**Consideraciones UX:**
- Formulario similar al de creación con datos precargados.
- Indicador visual de ahorro recalculado en tiempo real.
- Advertencia si el combo está vencido.
- Historial de cambios de precio.
- Botón de "Editar composición" que lleva a gestión de productos del combo.

**Reglas de Negocio:**
- Los cambios de precio no afectan ventas históricas (snapshot).
- Si se modifica la composición, solo afecta ventas futuras.
- Los combos fuera de vigencia se ocultan automáticamente del storefront.
- Si se desactiva un combo, desaparece inmediatamente de todos los puntos de venta.

---

### **RF-INV-009: Definir Composición de Combo**

**Prioridad:** Alta

**Descripción:**
El Administrador debe poder agregar productos al combo, especificar cantidades de cada producto, y gestionar la lista completa de productos incluidos. La composición define qué productos se descuentan del inventario al vender el combo.

**Criterios de Aceptación:**
1. El sistema muestra interfaz de gestión de productos del combo seleccionado.
2. El sistema permite buscar y agregar productos del catálogo al combo.
3. El sistema solicita cantidad de cada producto incluido (ej: 6 cervezas, 1 pisco, 2 limones).
4. El sistema valida que la cantidad sea > 0.
5. El sistema permite modificar cantidad de productos ya agregados.
6. El sistema permite eliminar productos del combo.
7. El sistema valida que el combo tenga al menos un producto.
8. El sistema muestra listado de productos incluidos con: nombre, cantidad, precio unitario, subtotal.
9. El sistema calcula precio original del combo sumando: `cantidad × precio_unitario` de cada producto.
10. El sistema permite guardar la composición y actualizar el combo.
11. El sistema registra cambios de composición en `log_auditoria`.

**Consideraciones Multi-tenant:**
- Solo se pueden agregar productos del mismo tenant al combo.
- Tabla: `combo_item` con relación combo → producto.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_combos` pueden modificar composición.
- Validar que los productos agregados pertenezcan al mismo tenant.
- Registrar cambios en `log_auditoria`.

**Consideraciones UX:**
- Interfaz similar a un carrito de compras.
- Buscador de productos con autocompletado.
- Tabla con productos agregados: columnas [Producto, Cantidad, Precio Unit., Subtotal, Acciones].
- Botones de "+/-" para ajustar cantidades rápidamente.
- Cálculo automático del precio original total.
- Comparación visual: "Precio del combo: S/ 45 vs. Precio original: S/ 60 (Ahorro: S/ 15)".
- Advertencia si el precio del combo es mayor que el precio original calculado.

**Reglas de Negocio:**
- Un combo debe tener al menos un producto.
- La cantidad de cada producto debe ser >= 1.
- Al vender el combo, se descuenta stock de cada producto individual según cantidad.
- Si algún producto del combo no tiene stock suficiente, no se puede vender el combo completo.
- Los productos desactivados no pueden agregarse a combos nuevos (advertencia en combos existentes).

---

### **RF-INV-009A: Desactivar Combo**

**Descripción:**  
El sistema debe permitir desactivar combos promocionales que ya no están vigentes o no se ofrecen.

**Criterios de Aceptación:**
1. El sistema permite desactivar combos existentes.
2. Al desactivar, el combo no aparece en POS ni storefront.
3. No se pueden realizar nuevas ventas del combo desactivado.
4. Se mantiene el historial de ventas realizadas con ese combo.
5. Se puede reactivar el combo en cualquier momento.
6. El sistema registra la desactivación en auditoría.

**Consideraciones Multi-tenant:**
- Solo se pueden desactivar combos del mismo tenant.
- La desactivación no elimina datos ni historial de ventas (soft delete).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_combos` pueden desactivar combos.
- Registrar desactivación en `log_auditoria` con motivo.
- No permitir desactivar si hay pedidos pendientes con ese combo.

**Consideraciones UX:**
- Confirmación clara del impacto: "Este combo dejará de estar disponible para ventas".
- Campo opcional para motivo de desactivación (ej: "Promoción finalizada", "Productos agotados").
- Botón de "Reactivar" fácilmente accesible en la lista.
- Badge de "Desactivado" en listado de combos.
- Filtro rápido para ver solo combos activos/desactivados.

**Reglas de Negocio:**
- Un combo desactivado no puede venderse ni pedirse.
- El historial de ventas del combo permanece intacto.
- Los combos vencidos (fuera de vigencia) pueden desactivarse automáticamente.
- Si se reactiva, vuelve a estar disponible inmediatamente en POS y storefront.
- Al desactivar, automáticamente se marca como `visible_storefront = FALSE`.

**Prioridad:** Media

---

### **RF-INV-010: Configurar Visibilidad en Storefront**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder controlar qué productos y combos son visibles en la tienda online (storefront), destacar productos específicos, y organizar la presentación para maximizar ventas online.

**Criterios de Aceptación:**
1. El sistema permite activar/desactivar `visible_storefront` para cada producto/combo individualmente.
2. El sistema permite marcar productos como destacados (`destacado = TRUE`).
3. El sistema permite configuración masiva: "Hacer visibles todos los productos de categoría X".
4. El sistema muestra vista previa de cómo se verán los productos en el storefront.
5. El sistema valida que productos marcados como destacados tengan imagen principal.
6. El sistema permite definir orden de presentación de productos destacados.
7. El sistema oculta automáticamente productos sin stock del storefront (configurable).
8. El sistema muestra contador: "35 productos visibles de 50 totales".
9. Los productos no visibles no aparecen en búsquedas ni navegación del storefront.
10. Los cambios se reflejan inmediatamente en el storefront (caché se actualiza).

**Consideraciones Multi-tenant:**
- La configuración de visibilidad es específica del tenant.
- Solo afecta al storefront del negocio actual.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.editar` pueden modificar visibilidad.
- Registrar cambios masivos en `log_auditoria`.

**Consideraciones UX:**
- Tabla de productos con toggle switch de "Visible" por fila.
- Checkbox de "Destacado" por fila.
- Filtros: todos, visibles, destacados, ocultos.
- Acciones masivas: "Hacer visibles seleccionados", "Ocultar seleccionados".
- Vista previa del storefront con productos configurados.
- Drag & drop para ordenar productos destacados.
- Badge visual: "Visible 👁️", "Destacado ⭐".

**Reglas de Negocio:**
- Solo productos activos pueden ser visibles en storefront.
- Los productos destacados aparecen en sección especial de la home del storefront.
- Los productos sin imagen pueden ser visibles pero se recomienda agregar imagen.
- Si `negocio.has_storefront = FALSE`, esta configuración no tiene efecto.
- Los productos visibles pero sin stock pueden ocultarse automáticamente (configurable).

---

### **RF-INV-011: Búsqueda de Productos**

**Prioridad:** Alta

**Descripción:**
El sistema debe proporcionar funcionalidad de búsqueda avanzada de productos permitiendo buscar por múltiples criterios (nombre, código, código de barras, categoría, marca, tipo), con resultados rápidos y relevantes para uso en POS, gestión de inventario, y administración del catálogo.

**Criterios de Aceptación:**
1. El sistema permite búsqueda por texto libre que busca en: nombre, código, código de barras, descripción.
2. El sistema implementa búsqueda por similitud (tolerante a errores de escritura) usando pg_trgm.
3. El sistema permite filtros avanzados:
   - Por categoría
   - Por marca
   - Por tipo de producto (cerveza, vino, destilado, etc.)
   - Por rango de precio
   - Por estado (activo/desactivo)
   - Por visibilidad en storefront
   - Por stock disponible (con stock, sin stock, stock bajo)
4. El sistema muestra resultados paginados (20 productos por página por defecto).
5. El sistema ordena resultados por relevancia (coincidencia exacta primero).
6. El sistema permite ordenar por: nombre A-Z, precio menor/mayor, stock, fecha de creación.
7. El sistema resalta términos de búsqueda en resultados.
8. El sistema muestra información resumida: imagen, nombre, código, precio, stock total.
9. El sistema permite selección múltiple de productos (para acciones masivas).
10. La búsqueda responde en menos de 500ms para catálogos de hasta 10,000 productos.

**Consideraciones Multi-tenant:**
- La búsqueda solo muestra productos del tenant actual.
- Índices optimizados: `idx_producto_tenant`, `idx_producto_nombre_trgm`.

**Consideraciones de Seguridad:**
- La búsqueda respeta permisos: algunos roles no ven precios de costo.
- Validar y sanitizar entrada de búsqueda (prevenir inyección).
- Limitar resultados a 1000 máximo (performance).

**Consideraciones UX:**
- Barra de búsqueda prominente con autocompletado.
- Sugerencias mientras se escribe (búsqueda incremental).
- Filtros laterales colapsables por categoría.
- Chips visuales de filtros aplicados (removibles con X).
- Vista de tarjetas o tabla (seleccionable).
- Indicador de "X resultados encontrados".
- Acciones rápidas por producto: editar, ver detalle, clonar.

**Reglas de Negocio:**
- La búsqueda es case-insensitive.
- Los productos desactivados aparecen en resultados pero marcados visualmente.
- El código de barras tiene prioridad en coincidencias exactas (para escáner).
- La búsqueda en POS solo muestra productos activos por defecto.
- La búsqueda en administración muestra todos los productos (con filtro de estado).

---

### **RF-INV-012: Importar Productos Masivamente**

**Prioridad:** Baja

**Descripción:**
El Administrador debe poder importar múltiples productos desde un archivo Excel o CSV, facilitando la carga inicial del catálogo o actualización masiva de datos. El sistema valida los datos y genera reporte de errores para corrección.

**Criterios de Aceptación:**
1. El sistema proporciona plantilla Excel/CSV descargable con las columnas requeridas y formato esperado.
2. El sistema permite subir archivo Excel (.xlsx, .xls) o CSV (.csv; máx 5MB).
3. El sistema valida el archivo:
   - Estructura de columnas correcta
   - Formatos de datos válidos
   - Códigos únicos dentro del archivo y contra BD
   - Precios >= 0
   - Categorías y marcas existen (o se crean automáticamente)
4. El sistema muestra vista previa de los productos a importar con estado: ✓ Válido, ✗ Error, ⚠️ Advertencia.
5. El sistema permite continuar solo si no hay errores críticos (advertencias son opcionales).
6. El sistema realiza importación en segundo plano si son >100 productos.
7. El sistema genera reporte de importación: X productos importados, Y con errores, Z advertencias.
8. El sistema registra la importación en `log_auditoria`.
9. El sistema envía notificación al usuario cuando la importación finaliza.
10. Los productos importados se crean con estado `activo = TRUE` por defecto.

**Consideraciones Multi-tenant:**
- Los productos se importan para el tenant actual.
- El archivo debe incluir `tenant_id` o se asigna automáticamente.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.importar` pueden importar productos.
- Validar tipo MIME del archivo subido.
- Escanear archivo con antivirus (recomendado).
- Limitar tamaño de archivo (5MB máximo).
- Limitar número de productos por importación (1000 máximo).
- Registrar importaciones en `log_auditoria`.

**Consideraciones UX:**
- Wizard de 3 pasos: 1) Descargar plantilla, 2) Subir archivo, 3) Validar y confirmar.
- Indicador de progreso durante validación y carga.
- Vista de tabla con productos validados coloreada por estado (verde/amarillo/rojo).
- Detalle de errores expandible por fila.
- Opción de descargar reporte de errores en Excel.
- Posibilidad de corregir en línea y volver a validar.

**Reglas de Negocio:**
- La plantilla incluye columnas obligatorias y opcionales claramente marcadas.
- Si una categoría o marca no existe, se crea automáticamente (o se marca como error según configuración).
- Los códigos duplicados en el archivo se rechazan.
- Los precios deben estar en formato numérico (sin símbolos de moneda).
- Las fechas deben estar en formato ISO (YYYY-MM-DD).
- La importación es transaccional: si falla, no se importa nada (rollback).

---

### **RF-INV-013: Crear Promoción**

**Prioridad:** Baja

**Descripción:**
El Administrador debe poder crear promociones con descuentos automáticos que se aplican a productos o categorías específicas durante un período definido, facilitando campañas de marketing y liquidaciones.

**Criterios de Aceptación:**
1. El sistema permite crear una promoción con: código único, nombre, descripción, tipo de descuento (porcentaje, monto fijo).
2. El sistema solicita valor del descuento: porcentaje (1-100%) o monto fijo (>= 0).
3. El sistema permite definir vigencia: fecha/hora inicio, fecha/hora fin.
4. El sistema permite definir alcance:
   - Todos los productos
   - Productos específicos (selección múltiple)
   - Categorías específicas (todos los productos de la categoría)
5. El sistema permite definir condiciones (opcional):
   - Pedido mínimo (ej: descuento solo si compra > S/ 100)
   - Cantidad mínima (ej: 2x1, 3x2)
   - Días de semana específicos (ej: solo fines de semana)
6. El sistema permite configurar: límite de uso por cliente, límite de uso total.
7. El sistema valida que las fechas sean coherentes (inicio < fin).
8. El sistema establece `activo = TRUE` por defecto.
9. El sistema registra quién creó la promoción y cuándo.
10. La promoción se aplica automáticamente en POS y storefront durante su vigencia.

**Consideraciones Multi-tenant:**
- Las promociones son específicas del tenant.
- Constraint: UNIQUE(tenant_id, codigo).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `promociones.crear` pueden crear promociones.
- Validar que los descuentos no sean excesivos (>90% requiere autorización).
- Registrar creación en `log_auditoria`.

**Consideraciones UX:**
- Wizard de 3 pasos: 1) Información básica, 2) Productos/categorías, 3) Condiciones.
- Selector visual de tipo de descuento: % o S/.
- Calendario para seleccionar vigencia.
- Selector múltiple de productos/categorías.
- Vista previa de cómo se verá la promoción en storefront.
- Estimador de impacto: "Esta promoción podría aplicar a ~50 productos".

**Reglas de Negocio:**
- Las promociones activas se aplican automáticamente al agregar productos a ventas/pedidos.
- Si múltiples promociones aplican a un producto, se usa la de mayor descuento.
- Las promociones vencidas se desactivan automáticamente.
- Las promociones con límite de uso se desactivan al alcanzar el límite.
- Los descuentos de promoción se suman a descuentos manuales (según configuración).

---

### **RF-INV-014: Configurar Reglas de Promoción**

**Prioridad:** Baja

**Descripción:**
El Administrador debe poder definir reglas avanzadas para promociones, como "2x1", "3x2", "Descuento escalonado por volumen", o "Regalo al comprar X productos", permitiendo estrategias de marketing sofisticadas.

**Criterios de Aceptación:**
1. El sistema permite seleccionar tipo de promoción:
   - Descuento simple (% o monto fijo)
   - NxM (ej: 2x1, 3x2, compra 3 paga 2)
   - Descuento escalonado (compra 1-5: 5%, 6-10: 10%, 11+: 15%)
   - Regalo (compra X, llévate Y gratis)
   - Envío gratis (pedido mínimo)
2. Para promociones NxM, el sistema solicita: N (cantidad para comprar), M (cantidad por la que paga).
3. Para descuento escalonado, el sistema permite definir rangos: cantidad desde, cantidad hasta, descuento %.
4. Para regalo, el sistema permite seleccionar producto regalo y cantidad.
5. El sistema valida coherencia de reglas (N > M en 2x1, rangos sin solapamiento).
6. El sistema muestra simulación de cómo funciona la promoción con ejemplos.
7. El sistema permite combinar reglas (ej: 2x1 + envío gratis si pedido > S/ 100).
8. El sistema registra las reglas en `promocion` con estructura JSON flexible.
9. Las reglas se aplican automáticamente en POS y storefront.
10. El sistema muestra el ahorro al cliente claramente: "Ahorraste S/ 15 con esta promoción".

**Consideraciones Multi-tenant:**
- Las reglas son específicas de cada promoción del tenant.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `promociones.crear` pueden configurar reglas.
- Validar que las reglas no generen precios negativos o inconsistencias.
- Registrar cambios en `log_auditoria`.

**Consideraciones UX:**
- Constructor visual de reglas con bloques arrastrables.
- Simulador interactivo: "Si compras 3 cervezas, pagas solo 2".
- Vista previa de cómo se mostrará en POS y storefront.
- Ejemplos predefinidos de promociones comunes.
- Validación en tiempo real de reglas.

**Reglas de Negocio:**
- En 2x1, el producto de menor precio se da gratis.
- En descuento escalonado, se aplica el descuento correspondiente al total de cantidad.
- Los regalos se agregan automáticamente al carrito si se cumplen condiciones.
- Las promociones se indican claramente en comprobantes y detalles de venta.
- Si la promoción no puede aplicarse (ej: stock insuficiente de regalo), se notifica al usuario.

---

## Submódulo 6.3.2: Control de Inventario

---

### **RF-INV-015: Visualizar Stock Consolidado por Producto**

**Descripción:**  
El sistema debe proporcionar una vista consolidada del inventario disponible por producto, agregando el stock de todos los lotes activos en todos los almacenes de la sede o del negocio completo. Debe mostrar cantidades físicas, valores totales, y alertas visuales para stock bajo o crítico.

**Criterios de Aceptación:**
1. La vista de inventario consolidado muestra:
   - Nombre y código del producto
   - Cantidad total disponible (suma de todos los lotes activos)
   - Unidad de medida
   - Costo promedio ponderado
   - Valor total del inventario (cantidad × costo promedio)
   - Stock mínimo configurado
   - Indicador visual de estado (OK, Bajo, Crítico, Agotado)
   - Fecha de próximo vencimiento (del lote más antiguo)
2. El sistema calcula automáticamente el estado del stock:
   - **Agotado**: cantidad = 0
   - **Crítico**: cantidad > 0 y < (stock_mínimo × 0.5)
   - **Bajo**: cantidad >= (stock_mínimo × 0.5) y < stock_mínimo
   - **OK**: cantidad >= stock_mínimo
3. Los usuarios pueden filtrar por:
   - Almacén específico o todos los almacenes
   - Categoría de producto
   - Estado de stock
   - Productos con vencimiento próximo (configurable: 7, 15, 30 días)
4. Se puede exportar el reporte a Excel/PDF para auditorías.
5. La vista se actualiza en tiempo real tras cada movimiento de inventario.

**Consideraciones Multi-tenant:**
- Cada negocio ve únicamente su propio inventario.
- Los usuarios asignados a una sede específica solo ven el inventario de esa sede (configurable por rol).
- El Gerente puede ver inventario de todas las sedes de su negocio.

**Seguridad:**
- Requiere permiso: `inventario_leer` o `inventario_listar`
- Los usuarios con rol Cajero solo pueden consultar disponibilidad, no valores monetarios (configurar permisos granulares).

**UX:**
- Iconos de alerta (⚠️) en productos con stock bajo.
- Color rojo para stock crítico/agotado, amarillo para bajo, verde para OK.
- Tooltip mostrando desglose por almacén al pasar el mouse.
- Vista rápida de "Top 10 productos con menor stock" en dashboard.

**Reglas de Negocio:**
- Solo se cuentan lotes con `estado = 'disponible'`.
- Lotes vencidos, bloqueados o agotados no se incluyen en el cálculo.
- El costo promedio ponderado se recalcula automáticamente con cada entrada de inventario.

---

### **RF-INV-016: Registrar Entrada de Lote de Inventario**

**Descripción:**  
El sistema debe permitir registrar la entrada de un nuevo lote de inventario al almacén, capturando toda la información necesaria para el control FIFO y la trazabilidad: fecha de vencimiento, costo unitario, proveedor, número de lote del fabricante, etc.

**Criterios de Aceptación:**
1. El formulario de registro de lote incluye:
   - Producto (búsqueda autocompletable)
   - Almacén de destino
   - Cantidad recibida (validar > 0)
   - Unidad de medida (heredada del producto)
   - Costo unitario (obligatorio)
   - Fecha de vencimiento (obligatorio para productos perecederos)
   - Número de lote del proveedor (opcional)
   - Proveedor (opcional, relacionado si viene de una orden de compra)
   - Número de factura/documento de ingreso (opcional)
   - Observaciones/notas
2. El sistema valida:
   - La fecha de vencimiento debe ser posterior a la fecha actual
   - El costo unitario debe ser mayor a 0
   - El almacén debe pertenecer a la sede del usuario
   - El producto debe estar activo
3. Al guardar, el sistema:
   - Crea el registro en `lote_inventario` con `estado = 'disponible'`
   - Registra el movimiento en `movimiento_inventario` con `tipo_movimiento = 'entrada'`
   - Actualiza la tabla `inventario_consolidado` sumando la cantidad
   - Recalcula el costo promedio ponderado del producto
   - Genera un comprobante/ticket con código QR para el lote
4. Si el producto requiere temperatura controlada, el sistema valida que el almacén tenga capacidad de refrigeración.
5. Se puede registrar entrada desde:
   - Formulario manual
   - Orden de compra (importación automática)
   - Importación masiva (Excel/CSV)

**Consideraciones Multi-tenant:**
- Solo se pueden crear lotes en almacenes del negocio del usuario.
- El código de lote debe ser único dentro del negocio.

**Seguridad:**
- Requiere permiso: `inventario_crear` o `lote_registrar_entrada`
- Auditoría completa: quién, cuándo, desde qué IP se registró el lote.

**UX:**
- Auto-rellenar costo unitario con el último costo registrado del producto.
- Sugerir fecha de vencimiento basada en vida útil promedio del producto (si está configurado).
- Alerta si el costo unitario tiene variación mayor al 20% respecto al promedio histórico.
- Vista previa del impacto en el stock consolidado antes de confirmar.

**Reglas de Negocio:**
- Los productos alcohólicos deben tener fecha de vencimiento obligatoria.
- Si el producto está configurado como "no perecedero", la fecha de vencimiento es opcional.
- El costo promedio ponderado se calcula como: `(valor_actual + valor_nuevo_lote) / (cantidad_actual + cantidad_nuevo_lote)`

---

### **RF-INV-017: Aplicar Sistema FIFO para Descuento Automático de Inventario**

**Descripción:**  
El sistema debe implementar el método FIFO (First In, First Out) para descontar automáticamente el inventario desde los lotes más antiguos (próximos a vencer) primero, garantizando la rotación adecuada de productos perecederos y minimizando mermas por vencimiento.

**Criterios de Aceptación:**
1. Al registrar una venta o salida de inventario, el sistema:
   - Llama a la función `seleccionar_lotes_fifo(producto_id, cantidad, almacen_id)`
   - Obtiene una lista ordenada de lotes por fecha de vencimiento (más próximo primero)
   - Descuenta primero del lote con fecha de vencimiento más cercana
   - Si un lote no tiene suficiente stock, descuenta lo disponible y continúa con el siguiente lote
2. La función `descontar_inventario_fifo()` valida:
   - Que haya stock suficiente en total para completar la operación
   - Que los lotes estén en estado `'disponible'`
   - Que no estén vencidos (`fecha_vencimiento >= CURRENT_DATE`)
   - Que pertenezcan al mismo almacén (o se busque en otros almacenes si se configura)
3. El sistema registra trazabilidad completa:
   - En `venta_item_lote` para ventas POS
   - En `pedido_item` con relación a lotes para pedidos
   - Permite rastrear qué lote específico se vendió en cada transacción
4. Si no hay stock suficiente, el sistema:
   - Lanza un error claro: "Stock insuficiente para producto X en almacén Y"
   - No realiza descuentos parciales (transacción atómica)
   - Sugiere almacenes alternativos con stock disponible
5. El sistema permite anular la lógica FIFO temporalmente para casos especiales (con permiso `inventario_override_fifo`):
   - Selección manual de lote específico (ej: producto dañado que debe salir primero)
   - Reservas de lotes específicos para clientes VIP

**Consideraciones Multi-tenant:**
- La selección de lotes FIFO respeta estrictamente los límites del negocio (`negocio_id`).
- Cada negocio puede configurar si permite descuento desde otros almacenes o solo del almacén actual.

**Seguridad:**
- El descuento FIFO se ejecuta a nivel de base de datos con validación de `negocio_id`.
- Solo SuperAdmin puede modificar la lógica FIFO en la función PL/pgSQL.

**UX:**
- Transparente para el usuario final (cajero): solo ve el producto vendido.
- Para administradores: vista detallada de "Análisis FIFO" mostrando:
  - Qué lotes se están vendiendo más rápido
  - Lotes con movimiento lento (riesgo de vencimiento)
  - Sugerencias de promociones para lotes próximos a vencer

**Reglas de Negocio:**
- El FIFO es **obligatorio** para productos perecederos con fecha de vencimiento.
- Los productos no perecederos pueden usar lógica LIFO o promedio si el negocio lo configura.
- Si todos los lotes están vencidos, el sistema no permite la venta y sugiere eliminar los productos vencidos.

---

### **RF-INV-018: Configurar y Recibir Alertas de Stock Bajo**

**Descripción:**  
El sistema debe monitorear continuamente los niveles de inventario y generar alertas automáticas cuando un producto alcance su nivel de stock mínimo o crítico, permitiendo a los administradores reaccionar antes de quedarse sin stock.

**Criterios de Aceptación:**
1. Cada producto tiene configurados dos umbrales:
   - **Stock mínimo**: Nivel que activa alerta temprana
   - **Stock crítico**: 50% del stock mínimo (configurable globalmente)
2. El sistema ejecuta verificaciones automáticas:
   - Cada hora (job programado)
   - Inmediatamente después de cada venta/salida que modifique inventario
3. Cuando el stock cae por debajo de los umbrales, el sistema:
   - Crea un registro en `alerta_inventario` (tabla interna)
   - Envía notificación in-app a usuarios con permisos (`inventario_gestionar`, `compras_gestionar`)
   - Envía email a administradores configurados (opcional)
   - Muestra badge de alerta en el módulo de inventario
4. Las alertas se clasifican por prioridad:
   - **Alta**: Stock crítico (< 50% del mínimo)
   - **Media**: Stock bajo (< stock mínimo)
   - **Baja**: Productos próximos a alcanzar mínimo (85% del mínimo)
5. Los usuarios pueden:
   - Ver lista de alertas activas ordenadas por prioridad
   - Marcar alertas como "gestionadas" (genera orden de compra, se hizo transferencia, etc.)
   - Configurar destinatarios de alertas por categoría de producto
   - Silenciar alertas temporalmente para productos descontinuados

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus propias alertas independientemente.
- Los usuarios de una sede solo ven alertas de sus almacenes asignados.

**Seguridad:**
- Las alertas solo son visibles para usuarios con permiso `inventario_leer` o `alertas_inventario_ver`.
- No se expone información de costos en las alertas (solo cantidades).

**UX:**
- Panel de "Alertas de Inventario" en el dashboard principal con contador de alertas pendientes.
- Notificaciones push en tiempo real para alertas de alta prioridad.
- Acción rápida: "Crear orden de compra" directamente desde la alerta.
- Gráfico de tendencia mostrando velocidad de consumo del producto.

**Reglas de Negocio:**
- Las alertas solo se generan para productos activos.
- Si un producto no tiene stock mínimo configurado, usa el default global del negocio.
- Las alertas se desactivan automáticamente cuando el stock supera el mínimo + 20% (margen de holgura).

---

### **RF-INV-019: Generar Alertas de Productos Próximos a Vencer**

**Descripción:**  
El sistema debe monitorear las fechas de vencimiento de todos los lotes activos y generar alertas preventivas cuando se acerque la fecha de vencimiento, permitiendo implementar estrategias de salida (descuentos, donaciones) antes de que el producto expire.

**Criterios de Aceptación:**
1. El sistema verifica diariamente (job nocturno) todos los lotes con `estado = 'disponible'` y `fecha_vencimiento IS NOT NULL`.
2. Se generan alertas en tres niveles temporales (configurables por negocio):
   - **Alerta roja**: Lotes que vencen en ≤ 7 días
   - **Alerta amarilla**: Lotes que vencen en 8-30 días
   - **Alerta informativa**: Lotes que vencen en 31-60 días
3. Las alertas incluyen:
   - Nombre del producto y código
   - Número de lote
   - Cantidad restante en el lote
   - Días para vencimiento
   - Valor monetario en riesgo (cantidad × costo)
   - Almacén donde se encuentra
   - Sugerencia de acción (crear promoción, transferir, donar)
4. El sistema calcula automáticamente "Índice de Riesgo de Vencimiento":
   - **Alto**: Producto de lenta rotación + vencimiento < 15 días
   - **Medio**: Producto de rotación media + vencimiento < 30 días
   - **Bajo**: Producto de alta rotación + vencimiento < 60 días
5. Acciones disponibles desde la alerta:
   - Crear promoción automática (descuento 20%, 30%, 50% según días restantes)
   - Transferir a otra sede con mayor rotación
   - Marcar para donación
   - Registrar merma anticipada
6. El sistema previene la venta de productos vencidos:
   - Bloquea automáticamente lotes con `fecha_vencimiento < CURRENT_DATE`
   - Cambia estado a `'vencido'`
   - Notifica al almacenero para retiro físico

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propios umbrales de alerta (7, 15, 30 días).
- Las alertas se envían solo a usuarios del negocio correspondiente.

**Seguridad:**
- Requiere permiso: `inventario_alertas_ver`
- Las acciones sobre lotes próximos a vencer (crear promoción, merma) requieren permisos adicionales.

**UX:**
- Dashboard con widget "Productos Próximos a Vencer" mostrando top 10 por riesgo.
- Vista de calendario mostrando vencimientos por mes.
- Notificación push diaria (8:00 AM) con resumen de alertas nuevas.
- Color coding: rojo (crítico), amarillo (atención), azul (informativo).

**Reglas de Negocio:**
- Los productos alcohólicos importados suelen tener vencimientos largos (2-5 años), pero cervezas y vinos tienen vencimientos más cortos (6-12 meses).
- El sistema debe considerar la rotación histórica: si un producto se vende 10 unidades/día y quedan 30 días para vencer con 200 unidades, el riesgo es bajo.
- Las alertas de vencimiento tienen **prioridad alta** para productos de alto valor (> $100 USD por unidad).

---

### **RF-INV-020: Registrar Movimientos de Inventario (Ajustes Manuales)**

**Descripción:**  
El sistema debe permitir registrar movimientos de inventario manuales para ajustar discrepancias encontradas en auditorías físicas, registrar mermas, productos dañados, muestras, o reclasificaciones, manteniendo trazabilidad completa de todas las operaciones.

**Criterios de Aceptación:**
1. Los tipos de movimientos de inventario soportados son:
   - **Entrada**: Compra, devolución de cliente, ajuste positivo por conteo
   - **Salida**: Venta, merma, producto vencido, muestra, donación, ajuste negativo
   - **Transferencia**: Entre almacenes (se registra salida + entrada)
   - **Ajuste**: Corrección por diferencias en inventario físico vs sistema
2. El formulario de movimiento manual incluye:
   - Tipo de movimiento (dropdown)
   - Producto (búsqueda autocompletable)
   - Lote específico (para salidas/ajustes)
   - Almacén de origen (y destino si es transferencia)
   - Cantidad (validar > 0 para entradas, < cantidad disponible para salidas)
   - Motivo/razón del movimiento (obligatorio para auditoría)
   - Costo unitario (solo para entradas)
   - Usuario responsable (auto-completa con usuario actual)
   - Documentos adjuntos (fotos, actas, reportes)
3. El sistema valida:
   - Stock suficiente para movimientos de salida
   - Lote activo y disponible
   - Almacén pertenece al negocio del usuario
   - Motivo obligatorio para ajustes > 10% del stock actual (prevenir fraude)
4. Al guardar, el sistema:
   - Crea registro en `movimiento_inventario`
   - Actualiza `cantidad_actual` en `lote_inventario`
   - Recalcula `inventario_consolidado`
   - Registra auditoría completa (fecha, hora, usuario, IP, cambios)
   - Envía notificación al supervisor si el ajuste es significativo (> 20% del stock)
5. Restricciones de seguridad:
   - Movimientos > $500 USD requieren aprobación de gerente (workflow)
   - Ajustes negativos > 50 unidades o > $1000 USD requieren justificación con foto/documento
   - Los movimientos no pueden editarse después de 24 horas (solo anulación con contraasiento)

**Consideraciones Multi-tenant:**
- Cada movimiento está vinculado a `negocio_id` para aislamiento total.
- Los usuarios solo pueden realizar movimientos en almacenes de su sede asignada.

**Seguridad:**
- Requiere permiso: `inventario_movimiento_crear` o específicos como `inventario_ajuste_manual`
- Los ajustes requieren doble verificación (aprobador + ejecutor) configurable por negocio.
- Log detallado en tabla `auditoria_inventario`.

**UX:**
- Wizard de 3 pasos: Seleccionar tipo → Ingresar datos → Confirmar y justificar.
- Vista previa del impacto antes de confirmar (stock antes/después).
- Historial de movimientos del usuario actual para referencia rápida.
- Plantillas predefinidas para movimientos comunes (ej: "Merma por rotura").

**Reglas de Negocio:**
- Los movimientos de **merma** no afectan el costo promedio ponderado (se resta cantidad pero no valor).
- Los movimientos de **ajuste por conteo** sí recalculan el costo si hay diferencia significativa.
- Todos los movimientos manuales deben pasar por reconciliación mensual del contador.

---

### **RF-INV-021: Transferir Inventario Entre Almacenes**

**Descripción:**  
El sistema debe facilitar la transferencia de productos entre diferentes almacenes de la misma sede o entre sedes del mismo negocio, manteniendo trazabilidad completa de los movimientos y asegurando que el inventario consolidado se actualice correctamente.

**Criterios de Aceptación:**
1. El formulario de transferencia incluye:
   - Almacén de origen (solo almacenes con stock disponible del usuario)
   - Almacén de destino (cualquier almacén del negocio)
   - Lista de productos a transferir (multiselección):
     - Producto
     - Lote específico (opcional, si no se especifica usa FIFO)
     - Cantidad a transferir
     - Cantidad disponible en origen (info)
   - Motivo de la transferencia (reabastecimiento, balanceo de stock, cambio de ubicación)
   - Usuario responsable del despacho (origen)
   - Usuario responsable de la recepción (destino, puede quedar pendiente)
   - Fecha y hora de despacho
   - Observaciones
2. El sistema valida:
   - Stock suficiente en el almacén de origen
   - Almacenes pertenecen al mismo negocio
   - Los lotes seleccionados están disponibles y no vencidos
   - Si el producto requiere temperatura controlada, el almacén destino debe tener capacidad
3. El flujo de transferencia es:
   - **Estado "Pendiente"**: Se registra la solicitud, se reserva el stock en origen (no disponible para venta)
   - **Estado "En Tránsito"**: El usuario de origen confirma el despacho, se descuenta del origen
   - **Estado "Recibido"**: El usuario de destino confirma la recepción, se suma al destino
   - **Estado "Cancelado"**: Se puede cancelar solo en estado "Pendiente", se libera la reserva
4. El sistema registra:
   - Movimiento de salida en almacén origen (`tipo = 'transferencia_salida'`)
   - Movimiento de entrada en almacén destino (`tipo = 'transferencia_entrada'`)
   - Referencia cruzada entre ambos movimientos
   - Comprobante/guía de transferencia con código QR
5. Funcionalidades adicionales:
   - Vista de "Transferencias Pendientes de Recepción" para almaceneros
   - Alertas si una transferencia está en tránsito > 48 horas sin confirmar recepción
   - Opción de transferencia automática entre almacenes cuando uno tiene stock bajo y otro tiene exceso

**Consideraciones Multi-tenant:**
- Solo se permite transferencia entre almacenes del mismo `negocio_id`.
- No se permiten transferencias entre negocios diferentes (incluso si son del mismo superadmin).

**Seguridad:**
- Requiere permisos: `inventario_transferencia_crear` (origen) y `inventario_transferencia_recibir` (destino).
- Las transferencias de alto valor (> $1000 USD) requieren aprobación de gerente.
- Auditoría completa de quién despachó y quién recibió.

**UX:**
- Vista tipo "carrito de compras" para seleccionar múltiples productos a transferir.
- Escaneo de código QR del producto para agregar rápidamente.
- Notificación push al almacenero de destino cuando hay transferencia en tránsito.
- Dashboard de "Transferencias Activas" mostrando estado en tiempo real.

**Reglas de Negocio:**
- Si la transferencia no se confirma en 7 días, el sistema la marca como "Pendiente de Revisión" y notifica al gerente.
- Los lotes transferidos mantienen su fecha de vencimiento original (no se resetea).
- El costo unitario del lote no cambia con la transferencia (se mantiene el costo original).

---

### **RF-INV-022: Rastrear Trazabilidad Completa de Lotes**

**Descripción:**  
El sistema debe proporcionar trazabilidad completa desde el ingreso de un lote hasta su salida final (venta, merma, etc.), permitiendo rastrear qué lote específico se vendió en cada transacción, a qué cliente, en qué fecha, y quién fue el responsable de cada movimiento.

**Criterios de Aceptación:**
1. Cada lote tiene un identificador único (`lote_codigo`) generado automáticamente por el sistema.
2. El sistema registra automáticamente:
   - **En entrada**: Proveedor, fecha, costo, usuario que registró, documento de respaldo
   - **En cada movimiento**: Usuario, fecha/hora, tipo de movimiento, cantidad, motivo
   - **En venta**: Venta_id, cliente (si existe), usuario cajero, método de pago
3. La vista de "Trazabilidad de Lote" muestra:
   - Timeline cronológico de todos los eventos del lote
   - Cantidad inicial, movimientos intermedios, cantidad actual
   - Origen: Proveedor, factura de compra, fecha de recepción
   - Movimientos intermedios: Transferencias, ajustes, reservas
   - Salidas: Ventas (con detalle de cliente), mermas, donaciones
   - Usuario responsable de cada operación
4. El sistema permite búsqueda de trazabilidad por:
   - Código de lote
   - Producto
   - Rango de fechas
   - Proveedor
   - Cliente (ventas a clientes específicos)
5. Funcionalidades de análisis:
   - "Rastreo inverso": Dado un número de venta, ver qué lotes se usaron
   - "Rastreo hacia adelante": Dado un lote, ver a qué clientes se vendió
   - "Análisis de recall": Si un lote tiene problema, identificar todos los clientes afectados
   - Exportar reporte de trazabilidad para auditorías o entes reguladores

**Consideraciones Multi-tenant:**
- La trazabilidad respeta los límites del negocio: no se cruza información entre negocios.
- Cada negocio puede configurar nivel de detalle en trazabilidad (básico, completo, regulatorio).

**Seguridad:**
- Requiere permiso: `inventario_trazabilidad_ver`
- La información de trazabilidad es sensible (incluye costos y clientes), por lo que requiere rol de administrador o gerente.
- Los datos de trazabilidad no pueden eliminarse (retención mínima: 5 años para auditorías).

**UX:**
- Interfaz visual tipo "Línea de Tiempo" con iconos para cada tipo de evento.
- Código QR en cada etiqueta de lote que redirige a la vista de trazabilidad.
- Búsqueda rápida escaneando código de barras del producto.
- Exportación a PDF con formato de reporte oficial para autoridades sanitarias.

**Reglas de Negocio:**
- Para productos alcohólicos, la trazabilidad es **obligatoria** por regulación (SUNAT, DIGESA).
- En caso de recall o retiro de producto, el sistema debe poder notificar automáticamente a todos los clientes afectados (si tienen datos de contacto).
- La trazabilidad incluye la cadena de frío para productos que requieren refrigeración (registro de temperaturas si hay sensores IoT).

---

### **RF-INV-023: Registrar Producto Vencido o Dañado**

**Prioridad:** Alta

**Descripción:**  
El sistema debe permitir registrar productos vencidos o dañados, facilitando su retiro del inventario disponible, documentación de la pérdida, y gestión de su disposición final (descarte, devolución a proveedor, donación).

**Criterios de Aceptación:**
1. El sistema identifica automáticamente lotes vencidos:
   - Job diario verifica `fecha_vencimiento < CURRENT_DATE`
   - Cambia estado de lote a `'vencido'` automáticamente
   - Bloquea el lote para que no pueda ser seleccionado en ventas (FIFO lo excluye)
   - Genera alerta para el almacenero responsable
2. El almacenero puede registrar productos dañados o vencidos:
   - Seleccionar lote afectado
   - Tipo: Vencido, Dañado, Contaminado
   - Cantidad afectada
   - Fotografía del producto (opcional)
   - Motivo/causa del daño
3. El sistema procesa el registro:
   - Crea movimiento de inventario con `tipo = 'merma'` o `'producto_dañado'`
   - Descuenta la cantidad del lote afectado
   - Actualiza `inventario_consolidado`
   - Calcula valor de la pérdida (cantidad × costo_unitario)
   - Registra en `log_auditoria` con fotos y documentos adjuntos
4. Opciones de disposición final:
   - Descarte/Destrucción
   - Devolución a proveedor
   - Donación
   - Uso interno/muestras

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus mermas independientemente.
- Los límites de tolerancia de merma pueden configurarse por negocio (ej: 2% mensual es aceptable).

**Seguridad:**
- Requiere permiso: `inventario_merma_gestionar`
- Las mermas > $500 USD requieren aprobación de gerente.
- Auditoría completa de quién registró la merma y quién aprobó.
- Las fotos de productos dañados se almacenan en storage seguro con marca de tiempo no modificable.

**UX:**
- Formulario simple para registrar merma.
- Cámara para capturar fotos del producto dañado.
- Alertas automáticas de productos próximos a vencer.
- Dashboard con resumen de mermas del mes.

**Reglas de Negocio:**
- Los productos vencidos **no pueden venderse bajo ninguna circunstancia** (regulación sanitaria).
- Si un producto vencido se detecta después de la fecha de vencimiento, se investiga por qué el sistema no lo bloqueó (auditoría de procesos).
- Las mermas por productos dañados por el cliente (ej: rotura en tienda) pueden generar cobro al cliente si aplica.
- Los productos próximos a vencer (< 7 días) pueden donarse a organizaciones benéficas (exención fiscal posible, dependiendo de legislación local).

---

### **RF-INV-024: Crear Proveedor**

**Prioridad:** Alta

**Descripción:**  
El sistema debe permitir registrar proveedores en el catálogo, incluyendo información de contacto, términos comerciales, y categorías de productos que suministran.

**Criterios de Aceptación:**
1. El formulario de creación de proveedor incluye:
   - Razón social (obligatorio)
   - RUC/NIT (obligatorio, único)
   - Dirección fiscal
   - Persona de contacto (nombre, cargo)
   - Teléfono principal
   - Email de pedidos
   - Email de facturación
   - Días de crédito (ej: 30, 60, 90 días)
   - Monto mínimo de pedido
   - Categorías de productos que suministra
2. El sistema valida que el RUC sea único dentro del tenant.
3. El sistema establece `activo = TRUE` por defecto.
4. El sistema registra quién creó el proveedor y cuándo.
5. El sistema genera UUID único para el proveedor.

**Consideraciones Multi-tenant:**
- Cada negocio gestiona su propio catálogo de proveedores independientemente.
- Un proveedor puede estar registrado en múltiples negocios (sin relación entre ellos).

**Seguridad:**
- Requiere permiso: `proveedores_gestionar`
- Los datos de proveedores son confidenciales.
- Registrar creación en `log_auditoria`.

**Consideraciones UX:**
- Formulario simple y claro.
- Validación en tiempo real de RUC único.
- Selección múltiple de categorías de productos.

**Reglas de Negocio:**
- El RUC es único e inmutable después de la creación.
- Los proveedores creados quedan disponibles para órdenes de compra inmediatamente.

---

### **RF-INV-024A: Editar Proveedor**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder editar información de proveedores existentes, modificando datos de contacto y términos comerciales.

**Criterios de Aceptación:**
1. El sistema permite editar toda la información excepto el RUC (inmutable).
2. El sistema permite cambiar: razón social, dirección, contactos, términos comerciales, categorías.
3. El sistema permite activar/desactivar/bloquear proveedores.
4. El sistema registra modificaciones en `log_auditoria`.

**Consideraciones Multi-tenant:**
- Solo se pueden editar proveedores del mismo tenant.

**Consideraciones de Seguridad:**
- Requiere permiso: `proveedores_gestionar`
- Registrar cambios en `log_auditoria`.

**Consideraciones UX:**
- Formulario con datos precargados.
- Indicador de cambios no guardados.

**Reglas de Negocio:**
- Un proveedor bloqueado no puede recibir nuevas órdenes de compra.
- Los cambios en términos comerciales solo afectan órdenes futuras.

---

### **RF-INV-024B: Listar Proveedores**

**Prioridad:** Media

**Descripción:**
El sistema debe mostrar lista de todos los proveedores con información resumida y opciones de filtrado.

**Criterios de Aceptación:**
1. El sistema muestra listado de proveedores con: nombre, RUC, teléfono, categorías, estado.
2. El sistema permite filtrar por: activos, inactivos, bloqueados, por categoría.
3. El sistema permite búsqueda por nombre o RUC.
4. El sistema permite ordenar por: nombre, fecha de registro.

**Consideraciones Multi-tenant:**
- Solo se listan proveedores del tenant actual.

**Consideraciones de Seguridad:**
- Requiere permiso: `proveedores_ver` o `proveedores_gestionar`.

**Consideraciones UX:**
- Vista de tabla con información clara.
- Búsqueda incremental.
- Indicador visual de estado (activo/bloqueado).

**Reglas de Negocio:**
- Los proveedores se ordenan alfabéticamente por defecto.

---

### **RF-INV-024C: Desactivar Proveedor**

**Descripción:**  
El sistema debe permitir desactivar proveedores con los que ya no se trabaja, manteniendo el historial de compras.

**Criterios de Aceptación:**
1. El sistema permite desactivar proveedores existentes.
2. Al desactivar, el proveedor no aparece en listados activos para nuevas órdenes.
3. Se mantiene el historial completo de órdenes de compra y transacciones.
4. Se puede reactivar el proveedor en cualquier momento.
5. El sistema registra el motivo de desactivación y quién lo realizó.

**Consideraciones Multi-tenant:**
- Solo se pueden desactivar proveedores del mismo tenant.
- La desactivación no elimina datos (soft delete).
- El historial de compras permanece intacto.

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `proveedores_gestionar` pueden desactivar proveedores.
- Registrar desactivación en `log_auditoria` con motivo detallado.
- Advertencia si hay órdenes de compra pendientes con ese proveedor.
- No permitir desactivar si hay cuentas por pagar pendientes (advertencia).

**Consideraciones UX:**
- Confirmación clara del impacto: "Este proveedor no estará disponible para nuevas órdenes de compra".
- Campo obligatorio para motivo de desactivación (ej: "Mal servicio", "Cambio de proveedor", "Proveedor cerró").
- Advertencia si hay órdenes pendientes: "⚠️ 2 órdenes de compra pendientes con este proveedor".
- Botón de "Reactivar" fácilmente accesible en la lista.
- Badge de "Desactivado" en listado de proveedores.
- Historial de desactivaciones/reactivaciones del proveedor.

**Reglas de Negocio:**
- Un proveedor desactivado no puede recibir nuevas órdenes de compra.
- El historial de órdenes de compra, facturas y pagos permanece intacto.
- Los reportes de desempeño de proveedores incluyen proveedores desactivados (con filtro).
- Si hay órdenes pendientes, se puede desactivar pero con advertencia destacada.
- Si se reactiva, vuelve a estar disponible inmediatamente para nuevas órdenes.
- Las cotizaciones activas del proveedor desactivado se marcan como "Proveedor inactivo".

**Prioridad:** Baja

---

### **RF-INV-025: Crear Orden de Compra**

**Descripción:**  
El sistema debe permitir crear órdenes de compra formales a proveedores, especificando productos, cantidades, precios, términos de entrega y pago. La orden de compra sirve como documento vinculante y punto de partida para el proceso de recepción de mercancía.

**Criterios de Aceptación:**
1. El formulario de orden de compra incluye:
   - **Encabezado**:
     - Número de orden (auto-generado: OC-YYYYMMDD-####)
     - Fecha de emisión (automática)
     - Proveedor (búsqueda autocompletable)
     - Almacén de destino
     - Fecha de entrega esperada
     - Términos de pago (heredados del proveedor, editables)
     - Moneda (PEN, USD, EUR)
     - Tipo de cambio (si aplica, obtenido de API)
   - **Detalle de productos** (tabla dinámica):
     - Producto (búsqueda, multiselección)
     - Descripción adicional (opcional)
     - Cantidad solicitada
     - Unidad de medida
     - Precio unitario
     - Descuento (%, monto fijo)
     - Subtotal (auto-calculado)
     - Fecha de vencimiento estimada (para productos perecederos)
   - **Totales**:
     - Subtotal
     - Descuento global (si aplica)
     - IGV/IVA (18% en Perú, configurable por país)
     - Total
   - **Condiciones**:
     - Forma de pago (contado, crédito)
     - Plazo de crédito (si aplica)
     - Penalidad por retraso (opcional)
     - Garantía (opcional)
     - Condiciones de devolución
   - **Observaciones/notas internas y para el proveedor**
2. El sistema valida:
   - Proveedor activo y no bloqueado
   - Productos existen en el catálogo
   - Cantidad > 0 para cada ítem
   - Precio unitario > 0
   - Almacén de destino pertenece al negocio
3. Estados de orden de compra:
   - **Borrador**: Editable, no enviada al proveedor
   - **Pendiente de Aprobación**: Si el monto supera límite configurado (ej: > $1000 USD)
   - **Aprobada**: Lista para enviar al proveedor
   - **Enviada**: Enviada al proveedor (email automático con PDF)
   - **Confirmada**: Proveedor confirmó recepción y compromiso de entrega
   - **Recibida Parcial**: Se recibió parte de la mercancía
   - **Recibida Total**: Toda la mercancía fue recibida
   - **Cancelada**: Orden anulada antes de recibir mercancía
   - **Cerrada**: Orden completa y conciliada
4. Funcionalidades adicionales:
   - Duplicar orden de compra existente (para reordenes rápidas)
   - Crear desde lista de productos con stock bajo (automático)
   - Convertir cotización en orden de compra
   - Generar PDF profesional de orden de compra con logo y firma digital
   - Enviar por email al proveedor con un clic
   - Agregar archivos adjuntos (especificaciones técnicas, diseños)
5. El sistema permite vincular la orden con:
   - Solicitud de compra interna (si existe proceso de aprobación previo)
   - Presupuesto del período (control de gasto)
   - Proyecto o centro de costo específico

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propia numeración de órdenes de compra.
- No se pueden crear órdenes desde proveedores de otros negocios.

**Seguridad:**
- Requiere permiso: `compras_orden_crear`
- Las órdenes > límite configurado requieren aprobación de gerente (`compras_orden_aprobar`).
- Auditoría completa de quién creó, aprobó, y envió la orden.

**UX:**
- Wizard de 3 pasos: Seleccionar proveedor → Agregar productos → Revisar y confirmar.
- Sugerencia automática de productos frecuentemente comprados a ese proveedor.
- Alerta si el precio unitario difiere > 15% del último precio pagado.
- Vista previa del PDF antes de enviar al proveedor.
- Plantillas de órdenes recurrentes para proveedores fijos.

**Reglas de Negocio:**
- Los productos alcohólicos importados requieren adjuntar el registro sanitario del producto.
- Si la orden supera el límite de crédito disponible con el proveedor, se alerta al usuario.
- Las órdenes en moneda extranjera usan el tipo de cambio del día de emisión para conversión a moneda local (solo para reporting).

---

### **RF-INV-026: Aprobar/Rechazar Órdenes de Compra**

**Descripción:**  
El sistema debe implementar un flujo de aprobación para órdenes de compra que superen ciertos umbrales de monto o criterios especiales, permitiendo a gerentes o directores revisar y aprobar/rechazar antes de enviar al proveedor, garantizando control de gastos y cumplimiento de presupuestos.

**Criterios de Aceptación:**
1. El sistema permite configurar reglas de aprobación por negocio:
   - **Por monto**: Órdenes > $X USD requieren aprobación de gerente, > $Y USD requieren director
   - **Por categoría**: Productos alcohólicos importados siempre requieren aprobación
   - **Por proveedor**: Proveedores nuevos (< 3 compras) requieren aprobación
   - **Por presupuesto**: Si la orden excede el 10% del presupuesto mensual restante
2. Cuando una orden requiere aprobación:
   - El sistema cambia el estado a `'pendiente_aprobacion'`
   - Se notifica automáticamente al(los) aprobador(es) configurados vía email y notificación in-app
   - La orden queda bloqueada para envío hasta ser aprobada
   - El creador de la orden ve el estado "Pendiente de Aprobación de [Nombre del Aprobador]"
3. El aprobador accede a una vista especial "Mis Órdenes Pendientes de Aprobación" que muestra:
   - Número de orden, fecha, proveedor, total
   - Motivo de aprobación requerida
   - Creador de la orden
   - Productos incluidos (resumen)
   - Comparativa con últimas compras similares
   - Estado de presupuesto (disponible vs. comprometido)
   - Evaluación del proveedor (si existe)
4. El aprobador puede:
   - **Aprobar**: La orden cambia a estado `'aprobada'` y puede ser enviada al proveedor
   - **Rechazar**: Debe indicar motivo del rechazo, la orden vuelve a `'borrador'` para edición
   - **Solicitar Modificación**: Envía comentarios al creador, la orden vuelve a editable
   - **Aprobar con Condiciones**: Aprueba pero con notas especiales (ej: "Verificar precio antes de confirmar")
5. El sistema registra:
   - Quién aprobó/rechazó y cuándo
   - Comentarios del aprobador
   - Timeline completo del flujo de aprobación
   - Notifica al creador de la decisión
6. Aprobación multi-nivel:
   - Si se requieren múltiples aprobaciones (ej: gerente + director), el sistema las gestiona secuencialmente o en paralelo (configurable)
   - Todas las aprobaciones deben estar completas antes de poder enviar la orden

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propias reglas de aprobación y aprobadores.
- Los límites de monto pueden variar por sede si el negocio así lo configura.

**Seguridad:**
- Requiere permiso: `compras_orden_aprobar`
- Los aprobadores solo ven órdenes de su negocio.
- No se puede auto-aprobar (el creador no puede ser el aprobador de la misma orden).

**UX:**
- Badge de notificación mostrando número de órdenes pendientes de aprobar.
- Vista de detalle con comparativa lado a lado de órdenes similares previas.
- Aprobación rápida con un clic para órdenes de bajo riesgo.
- App móvil para aprobar órdenes en cualquier momento.
- Timeline visual mostrando el flujo de aprobación.

**Reglas de Negocio:**
- Las órdenes rechazadas se pueden re-enviar para aprobación después de editarlas.
- Si una orden pendiente de aprobación no es revisada en 48 horas, el sistema escala al siguiente nivel (director).
- Las aprobaciones tienen timestamp y firma digital (IP, fecha, hora) para auditoría.

---

### **RF-INV-027: Recibir Mercancía y Generar Entrada de Inventario**

**Descripción:**  
El sistema debe facilitar el proceso de recepción de mercancía desde órdenes de compra, permitiendo al almacenero verificar cantidades recibidas vs. solicitadas, identificar discrepancias, registrar números de lote y fechas de vencimiento, y generar automáticamente las entradas de inventario correspondientes.

**Criterios de Aceptación:**
1. El módulo "Recepción de Mercancía" muestra:
   - Lista de órdenes de compra en estado `'enviada'` o `'confirmada'`
   - Filtros por: proveedor, fecha esperada de entrega, almacén
   - Indicador de órdenes vencidas (fecha esperada < hoy)
2. Al seleccionar una orden para recibir, el sistema muestra:
   - Detalle de la orden (proveedor, fecha, productos solicitados)
   - Formulario de recepción para cada producto:
     - Producto
     - Cantidad solicitada
     - Cantidad recibida (editable, por defecto = solicitada)
     - Diferencia (auto-calculado)
     - Unidad de medida
     - Estado del producto recibido (Conforme, No Conforme)
     - Lote del proveedor (número de lote del fabricante)
     - Fecha de vencimiento (obligatorio para perecederos)
     - Costo unitario (heredado de la orden, editable si hay variación)
     - Observaciones (calidad, embalaje, etc.)
   - Sección de documentos:
     - Número de factura del proveedor
     - Número de guía de remisión
     - Subir foto/scan de documentos
3. El sistema valida:
   - Cantidad recibida >= 0
   - Fecha de vencimiento > fecha actual
   - Si cantidad recibida ≠ cantidad solicitada, se requiere observación obligatoria
4. Al confirmar la recepción, el sistema:
   - Crea registros en `lote_inventario` por cada producto recibido
   - Genera movimientos de inventario tipo `'entrada'` vinculados a la orden de compra
   - Actualiza `inventario_consolidado`
   - Actualiza el estado de la orden de compra:
     - Si se recibió el 100%: `'recibida_total'`
     - Si se recibió parcial (< 100%): `'recibida_parcial'` (permite recepciones futuras)
   - Genera comprobante de recepción (PDF) con firma del almacenero
   - Notifica al departamento de compras de la recepción
5. Gestión de discrepancias:
   - **Faltantes**: Si cantidad recibida < solicitada
     - El sistema registra el faltante
     - Permite crear nota de crédito o solicitar reenvío al proveedor
     - Marca los ítems faltantes como "Pendientes de recibir"
   - **Sobrantes**: Si cantidad recibida > solicitada
     - El sistema alerta al usuario
     - Permite aceptar el sobrante (genera orden de compra complementaria) o rechazarlo
   - **No conformes**: Productos dañados o defectuosos
     - Registra cantidad no conforme
     - Genera solicitud de devolución al proveedor
     - No se agregan al inventario disponible (estado `'bloqueado'`)
6. Recepciones parciales:
   - Una orden puede tener múltiples recepciones hasta completar el 100%
   - Cada recepción genera lotes independientes con sus propias fechas de vencimiento
   - El sistema muestra historial de recepciones de la orden

**Consideraciones Multi-tenant:**
- Las recepciones solo se pueden realizar en almacenes del negocio del usuario.
- Los lotes generados se vinculan al `negocio_id` correcto.

**Seguridad:**
- Requiere permiso: `compras_recepcion_registrar`
- La confirmación de recepción con discrepancias > 10% del monto total requiere aprobación de gerente.
- Auditoría completa de quién recibió, cuándo, y qué discrepancias se registraron.

**UX:**
- App móvil para almacenero: escanear código de barras de productos para recepción rápida.
- Sugerencia automática de fecha de vencimiento basada en vida útil del producto.
- Alerta si el costo unitario recibido difiere > 10% del costo en la orden.
- Captura de foto de productos dañados directamente desde la app.
- Firmas digitales del almacenero y proveedor (si está presente).

**Reglas de Negocio:**
- Si no se registra recepción en 7 días después de la fecha esperada, el sistema marca la orden como "Retraso de Proveedor" y notifica al comprador.
- Los productos alcohólicos deben tener todos los registros sanitarios verificados antes de confirmar recepción.
- Si hay productos no conformes, no se libera el pago al proveedor hasta resolver la situación.

---

### **RF-INV-028: Crear Devolución a Proveedor**

**Descripción:**  
El sistema debe permitir crear nuevas devoluciones de productos a proveedores cuando se detectan productos defectuosos o no conformes.

**Criterios de Aceptación:**
1. El usuario puede crear una nota de devolución indicando:
   - Proveedor
   - Productos a devolver con cantidades
   - Motivo de devolución
   - Tipo de resolución (nota de crédito, reemplazo, reembolso)
2. Al confirmar, el sistema reduce el inventario automáticamente.

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus propias devoluciones a proveedores.
- Las notas de devolución no se comparten entre negocios.

**Consideraciones de Seguridad:**
- Requiere permiso: `compras_devolucion_crear`
- Devoluciones > $500 USD requieren aprobación de gerente.
- Auditoría completa de todas las devoluciones creadas.

**Consideraciones UX:**
- Interfaz guiada: "¿Qué deseas devolver?" → Selección de producto → Motivo → Resolución esperada.
- Carga de fotos de productos defectuosos (evidencia para el proveedor).
- Plantilla de email para notificar al proveedor sobre la devolución.
- Calculadora automática del monto a acreditar.

**Reglas de Negocio:**
- No se pueden devolver productos ya vendidos (el sistema valida stock real).
- Las devoluciones de productos perecederos deben hacerse dentro de 48 horas de recepción.
- El sistema genera automáticamente un documento de devolución con número único.
- Al crear la devolución, el stock se descuenta inmediatamente del inventario.

**Prioridad:** Media

---

### **RF-INV-028A: Editar Devolución a Proveedor**

**Descripción:**  
El sistema debe permitir modificar devoluciones existentes mientras no hayan sido finalizadas.

**Criterios de Aceptación:**
1. El usuario puede editar cantidades, productos y motivos de devoluciones pendientes.
2. Los cambios recalculan automáticamente los ajustes de inventario.

**Consideraciones Multi-tenant:**
- Solo se pueden editar devoluciones del mismo negocio.
- Las devoluciones de otros tenants no son visibles ni editables.

**Consideraciones de Seguridad:**
- Requiere permiso: `compras_devolucion_editar`
- Solo se pueden editar devoluciones en estado "Pendiente" o "En proceso".
- Registro completo de cambios en `log_auditoria`.

**Consideraciones UX:**
- Formulario con datos precargados de la devolución.
- Advertencia si se cambian cantidades que afectan el inventario.
- Historial de cambios visible en panel lateral.
- Botón de "Revertir cambios" antes de guardar.

**Reglas de Negocio:**
- No se pueden editar devoluciones ya finalizadas o procesadas.
- Los cambios en cantidades ajustan automáticamente el inventario.
- Si se aumenta la cantidad devuelta, se valida que haya stock suficiente.
- Las devoluciones aprobadas por el proveedor no pueden editarse.

**Prioridad:** Media

---

### **RF-INV-028B: Listar Devoluciones a Proveedores**

**Descripción:**  
El sistema debe permitir visualizar todas las devoluciones registradas con filtros de búsqueda.

**Criterios de Aceptación:**
1. Muestra listado con: número de devolución, proveedor, fecha, estado, monto.
2. Permite filtrar por proveedor, estado y rango de fechas.

**Consideraciones Multi-tenant:**
- Solo se listan devoluciones del negocio actual.
- Cada usuario ve devoluciones según sus permisos de sede.

**Consideraciones de Seguridad:**
- Requiere permiso: `compras_devolucion_ver` o `compras_devolucion_listar`
- Los usuarios con rol Almacenero solo ven devoluciones de su sede.
- No se exponen datos sensibles en el listado público.

**Consideraciones UX:**
- Vista de tabla con información clara y ordenada.
- Filtros rápidos por estado: Pendientes, Aprobadas, Rechazadas, Finalizadas.
- Búsqueda por número de devolución o nombre de proveedor.
- Indicadores visuales de estado con colores (amarillo=pendiente, verde=aprobada, rojo=rechazada).
- Exportación a Excel para reportes.

**Reglas de Negocio:**
- Las devoluciones se ordenan por fecha de creación (más recientes primero).
- El listado incluye un contador de devoluciones pendientes de resolución.
- Se pueden ver devoluciones históricas para análisis de proveedores.
- El monto total devuelto por proveedor es visible en la vista de resumen.

**Prioridad:** Media

---

### **RF-INV-029: Consultar Cuentas por Pagar**

**Descripción:**  
El sistema debe proporcionar una vista consolidada de todas las obligaciones pendientes de pago con proveedores, mostrando facturas pendientes, plazos de vencimiento, y permitiendo la gestión eficiente del flujo de caja y relaciones con proveedores.

**Criterios de Aceptación:**
1. El módulo "Cuentas por Pagar" muestra:
   - Lista de facturas/deudas pendientes de pago
   - Información por documento: Proveedor, Número de factura, Fecha de emisión, Fecha de vencimiento, Monto total, Saldo pendiente, Días para vencimiento/vencido
   - Indicadores visuales:
     - 🟢 Verde: Pago al día (> 7 días para vencer)
     - 🟡 Amarillo: Próximo a vencer (1-7 días)
     - 🔴 Rojo: Vencido
2. Filtros disponibles:
   - Por proveedor
   - Por estado (pendiente, parcialmente pagado, vencido)
   - Por rango de fechas de vencimiento
   - Por rango de montos
3. Vista de resumen:
   - Total de cuentas por pagar (todas las deudas)
   - Deudas vencidas (monto total)
   - Deudas por vencer en 7 días
   - Deudas por vencer en 30 días
   - Aging de cuentas por pagar (0-30 días, 31-60, 61-90, >90)
4. El sistema permite:
   - Ver detalle de cada cuenta por pagar (factura vinculada, orden de compra origen)
   - Registrar un pago (total o parcial)
   - Programar recordatorios de pago
   - Aplicar notas de crédito a cuentas por pagar
   - Exportar reporte a Excel/PDF
5. Al seleccionar un proveedor específico:
   - Muestra histórico de pagos realizados
   - Saldo pendiente total con el proveedor
   - Promedio de días de pago histórico
   - Términos de pago acordados vs. cumplimiento real

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus propias cuentas por pagar independientemente.
- Los datos financieros no se comparten entre negocios.

**Consideraciones de Seguridad:**
- Requiere permiso: `finanzas_cuentas_por_pagar_ver`
- Solo usuarios autorizados (Admin, Gerente, Contador) pueden ver montos.
- Registro de auditoría de todas las consultas de cuentas por pagar.

**Consideraciones UX:**
- Dashboard con KPIs principales: Total a pagar, Vencido, Próximo a vencer.
- Gráfico de barras mostrando aging de cuentas.
- Alertas visuales para facturas vencidas.
- Acción rápida: "Registrar pago" desde el listado.
- Calendario mostrando vencimientos del mes.

**Reglas de Negocio:**
- Las cuentas por pagar se generan automáticamente al recibir mercancía con factura.
- El sistema calcula automáticamente días de vencimiento.
- Las notas de crédito por devoluciones se aplican automáticamente a las cuentas por pagar del proveedor.
- Los pagos parciales actualizan el saldo pendiente.
- El sistema envía notificaciones automáticas 3 días antes del vencimiento.

**Prioridad:** Alta

---

### **RF-INV-030: Registrar Compra Directa**

**Descripción:**  
El sistema debe permitir registrar compras realizadas directamente sin orden de compra previa, típicamente para compras urgentes, de bajo monto, o a proveedores ocasionales, generando automáticamente la entrada de inventario y la cuenta por pagar correspondiente.

**Criterios de Aceptación:**
1. El formulario de compra directa incluye:
   - **Encabezado**:
     - Número de compra (auto-generado: CD-YYYYMMDD-####)
     - Fecha de compra
     - Proveedor (búsqueda o crear nuevo si es ocasional)
     - Almacén de destino
     - Número de factura del proveedor
     - Método de pago (efectivo, transferencia, crédito)
     - Términos de pago (si es crédito: días de crédito)
   - **Detalle de productos**:
     - Producto (búsqueda)
     - Cantidad comprada
     - Costo unitario
     - Subtotal (auto-calculado)
     - Lote del proveedor (opcional)
     - Fecha de vencimiento (obligatorio para perecederos)
   - **Totales**:
     - Subtotal
     - IGV (18%)
     - Total
2. El sistema valida:
   - Monto total > 0
   - Fecha de vencimiento > fecha actual (para productos perecederos)
   - Factura del proveedor no duplicada
   - Producto existe en el catálogo (o permite crear uno nuevo rápidamente)
3. Al confirmar la compra directa, el sistema:
   - Crea registros en `lote_inventario` por cada producto
   - Genera movimientos de inventario tipo `'entrada_compra_directa'`
   - Actualiza `inventario_consolidado`
   - Recalcula costo promedio ponderado
   - Si el pago es a crédito: Crea cuenta por pagar en `cuentas_por_pagar`
   - Si el pago es inmediato: Registra el pago y genera egreso de caja
   - Genera comprobante de compra (PDF) con detalle
4. Funcionalidades adicionales:
   - Subir foto/scan de la factura del proveedor
   - Agregar observaciones/notas sobre la compra
   - Imprimir comprobante de entrada al almacén
5. Restricciones configurables:
   - Monto máximo para compras directas sin aprobación (ej: $500 USD)
   - Compras > límite requieren aprobación de gerente
   - Lista de productos autorizados para compra directa (o todos)

**Consideraciones Multi-tenant:**
- Cada negocio registra sus compras directas independientemente.
- Los límites de monto son configurables por negocio.

**Consideraciones de Seguridad:**
- Requiere permiso: `compras_directa_crear`
- Compras > monto límite requieren permiso adicional: `compras_directa_aprobar`
- Auditoría completa: quién compró, cuánto, a qué proveedor.
- Validar que el usuario solo registre compras en almacenes de su sede asignada.

**Consideraciones UX:**
- Formulario simple de un solo paso (no wizard para agilidad).
- Autocompletado de proveedor con sugerencias de proveedores frecuentes.
- Botón de "Agregar producto" para compras múltiples.
- Cálculo automático de totales en tiempo real.
- Opción de "Crear proveedor rápido" si es ocasional (solo nombre y RUC).
- Acción rápida: "Guardar y registrar otra" para múltiples compras.

**Reglas de Negocio:**
- Las compras directas NO generan orden de compra (es compra ya realizada).
- El stock se incrementa inmediatamente al confirmar.
- Si el pago es a crédito, la cuenta por pagar se crea automáticamente.
- Las compras directas aparecen en reportes de compras con etiqueta "Compra Directa".
- Se recomienda usar órdenes de compra para compras planificadas y de alto monto.

**Prioridad:** Media

---

### **RF-INV-031: Registrar Pago a Proveedor**

**Descripción:**  
El sistema debe permitir registrar pagos a proveedores para saldar o abonar a cuentas por pagar, permitiendo pagos totales o parciales, múltiples formas de pago, y generando comprobantes de egreso con trazabilidad completa.

**Criterios de Aceptación:**
1. El módulo "Registrar Pago a Proveedor" permite seleccionar:
   - Proveedor (búsqueda autocompletable)
   - Muestra saldo pendiente total con el proveedor
   - Lista de facturas/cuentas pendientes con checkbox para seleccionar cuáles pagar
2. El formulario de pago incluye:
   - **Encabezado**:
     - Número de pago (auto-generado: PAG-PROV-YYYYMMDD-####)
     - Fecha de pago
     - Proveedor
     - Método de pago (efectivo, transferencia bancaria, cheque)
     - Cuenta bancaria de origen (si es transferencia)
     - Número de cheque (si aplica)
   - **Detalle de cuentas por pagar**:
     - Factura/Documento
     - Fecha de emisión
     - Fecha de vencimiento
     - Monto original
     - Saldo pendiente
     - Monto a pagar (editable, por defecto = saldo pendiente)
   - **Totales**:
     - Total a pagar (suma de montos seleccionados)
     - Descuentos por pronto pago (si aplica)
     - Total neto a pagar
3. El sistema valida:
   - Monto a pagar <= saldo pendiente por factura
   - Total a pagar > 0
   - Fondos suficientes en caja/cuenta (si el método es efectivo o cheque desde caja chica)
4. Al confirmar el pago, el sistema:
   - Registra el pago en `pagos_proveedor`
   - Actualiza el saldo pendiente de cada factura en `cuentas_por_pagar`
   - Marca facturas como `'pagada'` si saldo = 0, o `'parcialmente_pagada'` si saldo > 0
   - Registra movimiento de egreso en caja/banco (según método de pago)
   - Genera comprobante de egreso (PDF) con detalle de facturas pagadas
   - Envía notificación/recibo al proveedor (opcional)
5. El sistema permite:
   - Aplicar descuentos por pronto pago (si se paga antes del vencimiento)
   - Adjuntar comprobante de transferencia bancaria (foto/scan)
   - Agregar observaciones sobre el pago
   - Programar pagos futuros (fecha de ejecución)
6. Funcionalidades adicionales:
   - Pago a múltiples proveedores en un solo proceso (batch payment)
   - Aplicación automática de notas de crédito pendientes
   - Historial de pagos realizados al proveedor

**Consideraciones Multi-tenant:**
- Cada negocio registra sus pagos a proveedores independientemente.
- Los fondos de caja/banco son específicos del negocio.

**Consideraciones de Seguridad:**
- Requiere permiso: `finanzas_pago_proveedor_registrar`
- Pagos > $1000 USD requieren permiso adicional: `finanzas_pago_proveedor_aprobar`
- Auditoría completa: quién pagó, cuánto, a quién, desde qué cuenta.
- Los pagos confirmados no pueden editarse (solo anularse con contraasiento).

**Consideraciones UX:**
- Vista clara del saldo total pendiente con el proveedor.
- Checkbox para seleccionar facturas a pagar con un clic.
- Calculadora automática del total a pagar.
- Sugerencia de "Pagar todo" o "Pagar facturas vencidas solamente".
- Confirmación clara antes de registrar: "Vas a pagar $2,500 a Proveedor XYZ".
- Opción de "Imprimir comprobante" inmediatamente después del pago.

**Reglas de Negocio:**
- Los pagos se aplican primero a las facturas más antiguas (FIFO).
- Si hay descuento por pronto pago, se calcula automáticamente si se paga antes del vencimiento.
- Las notas de crédito pendientes se aplican automáticamente antes de desembolsar efectivo.
- El sistema registra el tipo de cambio si el pago es en moneda diferente a la factura.
- Los pagos parciales se permiten solo si el proveedor lo autoriza (configurable por proveedor).

**Prioridad:** Alta

---

## Resumen del Módulo

El **Módulo III: Gestión de Productos e Inventario** proporciona las funcionalidades completas para:

### Submódulo 6.3.1: Catálogo de Productos
- **21 requisitos funcionales** (RF-INV-001 a RF-INV-014, incluyendo variantes)
- Gestión completa de productos, categorías, marcas
- Combos promocionales y configuración de visibilidad en storefront
- Búsqueda avanzada, importación masiva, y sistema de promociones
- CRUDs completos para Categorías, Marcas, Combos y Proveedores

### Submódulo 6.3.2: Control de Inventario
- **9 requisitos funcionales** (RF-INV-015 a RF-INV-023)
- Sistema FIFO obligatorio para productos perecederos
- Control completo de lotes con trazabilidad
- Alertas de stock bajo y productos próximos a vencer
- Gestión de productos vencidos, dañados y mermas

### Submódulo 6.3.3: Compras y Proveedores
- **14 requisitos funcionales** (RF-INV-024 a RF-INV-031, incluyendo variantes)
- Gestión integral de proveedores (CRUD completo)
- Órdenes de compra con flujo de aprobación
- Recepción de mercancía y entrada de inventario
- Gestión de devoluciones a proveedores (CRUD completo)
- Consulta de cuentas por pagar
- Registro de compras directas
- Registro de pagos a proveedores

**Total: 44 requisitos funcionales** que garantizan el control completo del inventario, trazabilidad de productos alcohólicos (cumplimiento regulatorio), y optimización de la cadena de suministro para licorerías.

---

**Fin del Módulo III**
