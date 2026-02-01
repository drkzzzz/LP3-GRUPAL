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

### **RF-INV-005: Gestionar Categorías de Productos**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder crear, editar y organizar categorías para clasificar productos, facilitando la navegación en el storefront, reportes segmentados, y gestión del catálogo. Las categorías son específicas del negocio y reflejan su organización de productos.

**Criterios de Aceptación:**
1. El sistema permite crear una categoría con: código único, nombre, descripción, imagen de categoría (opcional), orden de visualización.
2. El sistema valida que el código sea único dentro del tenant.
3. El sistema permite editar: nombre, descripción, imagen, orden.
4. El sistema no permite editar el código (inmutable).
5. El sistema permite activar/desactivar categorías.
6. El sistema valida que no se desactive una categoría con productos activos asignados (advertencia).
7. El sistema permite ordenar categorías (campo `orden`) para definir secuencia de presentación.
8. El sistema muestra contador de productos por categoría.
9. El sistema permite asignar imagen representativa a la categoría (para storefront).
10. El sistema registra creación/edición en `log_auditoria`.
11. Las categorías se muestran ordenadas en storefront y selectores del sistema.

**Consideraciones Multi-tenant:**
- Las categorías son específicas del tenant.
- Constraint: UNIQUE(tenant_id, codigo).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_categorias` pueden gestionar categorías.
- Registrar cambios en `log_auditoria`.
- Validar tipo MIME de imágenes subidas.

**Consideraciones UX:**
- Listado de categorías con tarjetas visuales mostrando imagen, nombre, y contador de productos.
- Drag & drop para reordenar categorías.
- Modal de creación/edición rápida.
- Vista previa de cómo se verá en storefront.
- Filtro de categorías: activas, todas.
- Indicador visual: "15 productos en esta categoría".
- Sugerencias de categorías típicas: "Cervezas", "Vinos", "Licores", "Destilados", "Espumantes", "Complementos".

**Reglas de Negocio:**
- Los productos pueden tener una categoría asignada o ninguna (NULL).
- Las categorías desactivadas no aparecen en storefront ni filtros.
- El orden define la secuencia en menús y filtros (categorías más importantes primero).
- La imagen de categoría es opcional pero mejora la experiencia en storefront.
- Los productos sin categoría se agrupan en "Sin categoría" o "Otros".

---

### **RF-INV-006: Gestionar Marcas**

**Prioridad:** Media

**Descripción:**
El Administrador debe poder registrar y gestionar marcas de productos (fabricantes/distribuidores), almacenando información como país de origen, logo, y descripción. Las marcas ayudan a clasificar productos y son importantes para búsquedas y filtros en el storefront.

**Criterios de Aceptación:**
1. El sistema permite crear una marca con: nombre único, país de origen, logo (opcional), descripción.
2. El sistema valida que el nombre sea único dentro del tenant.
3. El sistema permite subir logo de la marca (PNG, JPG; máx 2MB).
4. El sistema permite editar: nombre, país de origen, logo, descripción.
5. El sistema permite activar/desactivar marcas.
6. El sistema valida que no se desactive una marca con productos activos asignados (advertencia).
7. El sistema muestra contador de productos por marca.
8. El sistema permite búsqueda y autocompletado al asignar marca a producto.
9. El sistema registra creación/edición en `log_auditoria`.
10. Las marcas se usan como filtro en storefront y reportes.

**Consideraciones Multi-tenant:**
- Las marcas son específicas del tenant.
- Constraint: UNIQUE(tenant_id, nombre).

**Consideraciones de Seguridad:**
- Solo usuarios con rol Admin o permiso `productos.gestionar_marcas` pueden gestionar marcas.
- Validar tipo MIME de logos subidos.
- Registrar cambios en `log_auditoria`.

**Consideraciones UX:**
- Listado de marcas con tarjetas mostrando logo, nombre, país, y contador de productos.
- Modal de creación/edición rápida.
- Selector de país con banderas.
- Drag & drop para subir logo.
- Vista previa del logo.
- Filtro: activas, todas.
- Búsqueda incremental de marcas.

**Reglas de Negocio:**
- Los productos pueden tener una marca asignada o ninguna (NULL).
- Las marcas desactivadas no aparecen en filtros activos.
- Las marcas importantes en licorerías: Johnnie Walker, Bacardi, Corona, Pilsen, Cusqueña, etc.
- El logo de marca puede mostrarse junto al producto en storefront.
- El país de origen es informativo (puede diferir del país de origen del producto).

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

### **RF-INV-023: Gestionar Productos Vencidos y Dañados**

**Descripción:**  
El sistema debe proporcionar un módulo específico para gestionar productos vencidos o dañados, facilitando su identificación, retiro del inventario disponible, documentación de la pérdida, y gestión de su disposición final (descarte, devolución a proveedor, donación).

**Criterios de Aceptación:**
1. El sistema identifica automáticamente lotes vencidos:
   - Job diario verifica `fecha_vencimiento < CURRENT_DATE`
   - Cambia estado de lote a `'vencido'` automáticamente
   - Bloquea el lote para que no pueda ser seleccionado en ventas (FIFO lo excluye)
   - Genera alerta para el almacenero responsable
2. El módulo "Gestión de Mermas y Productos No Aptos" muestra:
   - Lista de lotes vencidos o dañados pendientes de gestión
   - Producto, lote, cantidad, costo, fecha de vencimiento, almacén
   - Días transcurridos desde el vencimiento
   - Valor total de la pérdida
   - Estado de gestión (Pendiente, En Proceso, Resuelto)
3. El almacenero puede registrar:
   - **Producto dañado**: Antes del vencimiento (rotura, contaminación, daño de embalaje)
     - Capturar fotos del daño
     - Seleccionar causa (transporte, almacenamiento, manipulación, cliente)
     - Determinar responsable (proveedor, transportista, personal interno, cliente)
     - Cantidad afectada (parcial o todo el lote)
   - **Producto vencido**: Confirmación de retiro físico
     - Método de disposición (descarte, devolución a proveedor, donación)
     - Documento de respaldo (acta de destrucción, guía de devolución, certificado de donación)
     - Firma digital del responsable
4. El sistema procesa la gestión:
   - Crea movimiento de inventario con `tipo = 'merma'` o `'producto_dañado'`
   - Descuenta la cantidad del lote y actualiza `inventario_consolidado`
   - Registra el costo de la pérdida para reportes financieros
   - Si hay póliza de seguro, genera reporte para reclamo
   - Si es devolución a proveedor, genera nota de crédito automática
5. Reportes de mermas:
   - "Reporte Mensual de Mermas" por categoría, causa, responsable
   - "Análisis de Causas de Pérdida" para mejorar procesos
   - "Valor Total de Mermas" vs. Ventas (indicador de eficiencia)
   - Comparativa entre almacenes (identificar problemas localizados)

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus mermas independientemente.
- Los límites de tolerancia de merma pueden configurarse por negocio (ej: 2% mensual es aceptable).

**Seguridad:**
- Requiere permiso: `inventario_merma_gestionar`
- Las mermas > $500 USD requieren aprobación de gerente.
- Auditoría completa de quién registró la merma y quién aprobó.
- Las fotos de productos dañados se almacenan en storage seguro con marca de tiempo no modificable.

**UX:**
- App móvil para almacenero: escanear código de barras, tomar foto, registrar merma en 3 pasos.
- Notificaciones push cuando hay productos próximos a vencer (proactivo para evitar mermas).
- Dashboard con KPI: "% de Mermas del Mes", "Valor de Mermas", "Causas Principales".
- Flujo guiado para gestión de devoluciones a proveedor (genera automáticamente emails, notas de crédito).

**Reglas de Negocio:**
- Los productos vencidos **no pueden venderse bajo ninguna circunstancia** (regulación sanitaria).
- Si un producto vencido se detecta después de la fecha de vencimiento, se investiga por qué el sistema no lo bloqueó (auditoría de procesos).
- Las mermas por productos dañados por el cliente (ej: rotura en tienda) pueden generar cobro al cliente si aplica.
- Los productos próximos a vencer (< 7 días) pueden donarse a organizaciones benéficas (exención fiscal posible, dependiendo de legislación local).

---

## Submódulo 6.3.3: Compras y Proveedores

---

### **RF-INV-024: Gestionar Catálogo de Proveedores**

**Descripción:**  
El sistema debe proporcionar un módulo completo para gestionar el catálogo de proveedores, incluyendo información de contacto, términos comerciales, categorías de productos que suministran, historial de compras, y evaluación de desempeño. Esto centraliza la información y facilita la toma de decisiones sobre con quién comprar.

**Criterios de Aceptación:**
1. El formulario de registro de proveedor incluye:
   - **Información básica**: Nombre comercial, razón social, RUC/NIT, país
   - **Contacto**: Dirección, teléfono, email, sitio web, contacto principal (nombre, cargo, teléfono directo)
   - **Información comercial**:
     - Categorías de productos que suministra (multiselección)
     - Términos de pago (contado, 15 días, 30 días, 60 días, consignación)
     - Método de pago preferido (transferencia, cheque, efectivo)
     - Moneda de operación (PEN, USD, EUR)
     - Descuento por volumen (si aplica)
     - Monto mínimo de pedido
   - **Información fiscal**: Certificado de RUC, constancia de no adeudo, certificaciones (DIGESA para alimentos/bebidas)
   - **Banco**: Nombre del banco, número de cuenta, CCI (para pagos)
   - **Estado**: Activo, Inactivo, Suspendido, Bloqueado
   - **Notas internas**: Observaciones sobre calidad, cumplimiento, incidencias
2. El sistema permite:
   - Asignar múltiples contactos por proveedor (ventas, facturación, logística)
   - Subir documentos adjuntos (contratos, certificados, fichas técnicas)
   - Registrar sucursales o centros de distribución del proveedor
   - Asociar productos específicos con proveedores (tabla `producto_proveedor` con precio y tiempo de entrega)
3. Vista de listado de proveedores con:
   - Filtros por: estado, categoría, país, términos de pago
   - Búsqueda por nombre, RUC, o producto que suministran
   - Columnas: Nombre, RUC, categorías, último pedido, deuda pendiente, calificación
   - Acciones rápidas: Ver detalle, nueva orden de compra, contactar
4. Cada proveedor tiene una ficha completa que muestra:
   - Resumen de compras (total histórico, promedio mensual, última compra)
   - Productos comprados frecuentemente
   - Órdenes de compra activas
   - Historial de pagos y saldo pendiente
   - Evaluación de desempeño (cumplimiento, calidad, tiempo de entrega)
   - Timeline de interacciones (llamadas, emails, visitas)
5. El sistema valida:
   - RUC único por negocio (no duplicados)
   - Email en formato válido
   - Al menos un contacto principal obligatorio

**Consideraciones Multi-tenant:**
- Cada negocio mantiene su propio catálogo de proveedores (no compartido).
- Un mismo proveedor real puede estar registrado en múltiples negocios de forma independiente.

**Seguridad:**
- Requiere permiso: `proveedores_gestionar` (crear/editar), `proveedores_ver` (solo lectura)
- Los datos bancarios del proveedor son sensibles: solo visibles para usuarios con permiso `proveedores_datos_financieros_ver`.
- Auditoría de cambios en información crítica (datos bancarios, estado).

**UX:**
- Importación masiva desde Excel/CSV con plantilla predefinida.
- Autocompletado de datos mediante API de SUNAT usando RUC (Perú).
- Vista tipo "tarjeta" para visualización rápida de proveedores principales.
- Etiquetas/tags personalizados (ej: "Proveedor confiable", "Entrega lenta", "Buenos precios").

**Reglas de Negocio:**
- Los proveedores de productos alcohólicos deben tener certificación DIGESA vigente.
- Si un proveedor se marca como "Bloqueado", no se pueden crear nuevas órdenes de compra hasta desbloquearlo.
- Los proveedores "Suspendidos" tienen órdenes activas pero no se permiten nuevas hasta resolver el problema.

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

### **RF-INV-028: Gestionar Devoluciones a Proveedores**

**Descripción:**  
El sistema debe permitir gestionar el proceso completo de devolución de mercancía a proveedores por productos defectuosos, vencidos prematuramente, errores en el pedido, o cualquier no conformidad, incluyendo la generación de documentos de devolución y el seguimiento de notas de crédito o reemplazos.

**Criterios de Aceptación:**
1. El usuario puede iniciar una devolución desde:
   - Una orden de compra recibida (devolución inmediata post-recepción)
   - Un lote de inventario existente (devolución posterior por defecto detectado)
   - Una recepción con productos marcados como "No conformes"
2. El formulario de devolución incluye:
   - **Encabezado**:
     - Número de devolución (auto-generado: DEV-YYYYMMDD-####)
     - Orden de compra origen (si aplica)
     - Proveedor
     - Fecha de devolución
     - Motivo (dropdown): Producto defectuoso, vencimiento prematuro, error en pedido, empaque dañado, otro
     - Tipo de resolución esperada: Reemplazo, nota de crédito, reembolso
   - **Detalle de productos a devolver**:
     - Producto
     - Lote (si se identifica uno específico)
     - Cantidad a devolver
     - Costo unitario (para cálculo de crédito)
     - Motivo específico del ítem
     - Evidencia (fotos, documentos)
   - **Documentos de respaldo**:
     - Guía de remisión de devolución
     - Acta de no conformidad
     - Fotos de los productos
   - **Observaciones para el proveedor**
3. Estados de devolución:
   - **Borrador**: En proceso de documentación
   - **Registrada**: Devolución formalizada, pendiente de envío
   - **Enviada**: Mercancía despachada de vuelta al proveedor
   - **Recibida por Proveedor**: Proveedor confirmó recepción
   - **Aprobada**: Proveedor acepta la devolución y emite nota de crédito/reemplazo
   - **Rechazada**: Proveedor no acepta la devolución (requiere escalamiento)
   - **Cerrada**: Devolución completada y compensada
4. Al registrar la devolución, el sistema:
   - Crea movimiento de inventario tipo `'devolucion_proveedor'`
   - Descuenta la cantidad del lote correspondiente
   - Actualiza `inventario_consolidado`
   - Si el lote queda en cero, marca el lote como `'devuelto'`
   - Genera documento de devolución (PDF) para enviar al proveedor
   - Registra el monto de la devolución como "Crédito Pendiente" con el proveedor
5. Seguimiento de compensación:
   - **Nota de crédito**: Se aplica al saldo con el proveedor, se usa en futuras compras
   - **Reembolso**: Se registra el ingreso de dinero cuando el proveedor paga
   - **Reemplazo**: Se vincula con una nueva recepción de mercancía (orden de reposición)
6. El sistema permite:
   - Ver historial de devoluciones por proveedor (afecta evaluación de desempeño)
   - Generar reporte de "Tasa de Devolución por Proveedor"
   - Escalar devoluciones rechazadas a gerencia o legal

**Consideraciones Multi-tenant:**
- Las devoluciones solo afectan el inventario del negocio que las registra.
- Los créditos con proveedores son independientes por negocio.

**Seguridad:**
- Requiere permiso: `compras_devolucion_crear`
- Devoluciones > $500 USD requieren aprobación de gerente.
- Auditoría completa de todo el ciclo de devolución.

**UX:**
- Botón de "Iniciar Devolución" directamente desde la ficha de recepción.
- Captura de fotos de productos defectuosos con timestamp y geolocalización.
- Notificaciones automáticas al proveedor por email.
- Dashboard de "Devoluciones en Proceso" con estado en tiempo real.

**Reglas de Negocio:**
- Las devoluciones deben iniciarse dentro del plazo acordado con el proveedor (típicamente 7-15 días post-recepción).
- Si el proveedor no responde en 15 días, el sistema escala automáticamente a gerencia.
- Las notas de crédito tienen validez de 6-12 meses (según acuerdo con proveedor).
- Los productos devueltos no pueden venderse bajo ninguna circunstancia hasta resolver con el proveedor.

---

### **RF-INV-029: Conciliar Facturas con Órdenes de Compra**

**Descripción:**  
El sistema debe facilitar el proceso de conciliación entre las órdenes de compra, las recepciones de mercancía y las facturas del proveedor (three-way matching), validando que cantidades, precios y totales coincidan antes de autorizar el pago, previniendo errores y posibles fraudes.

**Criterios de Aceptación:**
1. El módulo "Conciliación de Facturas" muestra:
   - Lista de órdenes de compra recibidas pendientes de conciliar
   - Facturas del proveedor pendientes de validar
   - Estado de conciliación de cada documento
2. El proceso de conciliación valida automáticamente (three-way match):
   - **Orden de Compra (OC)**: Lo que se solicitó y aprobó
   - **Recepción de Mercancía (RM)**: Lo que realmente se recibió
   - **Factura del Proveedor (FP)**: Lo que el proveedor está cobrando
3. La vista de conciliación muestra tabla comparativa:
   | Producto | Cantidad OC | Cantidad RM | Cantidad FP | Precio OC | Precio FP | Monto OC | Monto FP | Estado |
   |----------|-------------|-------------|-------------|-----------|-----------|----------|----------|--------|
   | Whisky X | 100         | 98          | 100         | $25.00    | $25.00    | $2,500   | $2,500   | ⚠️ Dif. Cantidad |
4. El sistema identifica discrepancias:
   - **Cantidad**: FP ≠ RM (facturado diferente a lo recibido)
   - **Precio**: Precio FP ≠ Precio OC (cambio de precio no autorizado)
   - **Total**: Suma de FP ≠ Suma de OC + impuestos
   - **Productos**: Items en FP que no están en OC (cargos adicionales)
5. Estados de conciliación:
   - **Conciliada**: Todo coincide, lista para pagar
   - **Conciliada con Diferencias Menores**: Diferencias < 2% o < $50 USD (aceptable, se aprueba)
   - **Discrepancia Mayor**: Diferencias > 2% o > $50 USD (requiere investigación)
   - **Rechazada**: Factura incorrecta, se devuelve al proveedor para corrección
6. El usuario puede:
   - Aprobar la factura si está conforme
   - Solicitar nota de crédito por diferencias
   - Rechazar la factura y solicitar re-emisión
   - Agregar notas sobre las discrepancias
   - Comunicarse con el proveedor directamente desde el sistema (envío de email)
7. Al aprobar la conciliación:
   - El sistema registra la factura como `'aprobada_para_pago'`
   - Se crea el compromiso de pago según términos (fecha de vencimiento)
   - Se notifica al departamento de finanzas para programar el pago
   - Se vincula la factura con la orden y recepciones correspondientes
8. Reportes de conciliación:
   - "Facturas Pendientes de Conciliar" (aging report)
   - "Discrepancias Frecuentes por Proveedor"
   - "Tiempo Promedio de Conciliación"

**Consideraciones Multi-tenant:**
- Cada negocio concilia sus propias facturas independientemente.
- Los límites de tolerancia de diferencias son configurables por negocio.

**Seguridad:**
- Requiere permiso: `compras_factura_conciliar`
- Las aprobaciones de facturas con discrepancias mayores requieren permiso `compras_factura_aprobar_con_diferencias`.
- Auditoría completa de quién aprobó qué factura y con qué diferencias.

**UX:**
- Vista tipo "diff" mostrando lado a lado OC, RM y FP con colores (verde=match, amarillo=diferencia menor, rojo=discrepancia).
- Botón de "Aprobar Automáticamente" para facturas 100% conciliadas.
- Alertas visuales para facturas próximas a vencer (términos de pago).
- Sugerencia automática de acciones basadas en el tipo de discrepancia.

**Reglas de Negocio:**
- No se puede aprobar pago de una factura sin conciliarla primero con la orden y recepción.
- Si la discrepancia es en favor del negocio (se factura menos de lo recibido), se aprueba automáticamente y se notifica como "ahorro".
- Las facturas no conciliadas en 30 días se escalan automáticamente a gerencia.
- Los descuentos aplicados por el proveedor en la factura deben coincidir con los acordados en la orden.

---

### **RF-INV-030: Analizar Desempeño de Proveedores**

**Descripción:**  
El sistema debe proporcionar herramientas de análisis para evaluar el desempeño de los proveedores basándose en múltiples métricas (cumplimiento de plazos, calidad, precios, devoluciones), facilitando decisiones informadas sobre con quién continuar trabajando y quién merece mejores términos comerciales.

**Criterios de Aceptación:**
1. El sistema calcula automáticamente métricas de desempeño por proveedor:
   - **Cumplimiento de Entrega**:
     - % de órdenes entregadas a tiempo (fecha real ≤ fecha esperada)
     - Promedio de días de retraso (para entregas tardías)
     - % de entregas anticipadas
   - **Calidad de Productos**:
     - % de productos conformes vs. no conformes
     - Tasa de devolución (cantidad devuelta / cantidad total recibida)
     - Número de incidencias de calidad
   - **Precisión de Pedidos**:
     - % de órdenes recibidas completas (cantidad recibida = cantidad solicitada)
     - % de órdenes con errores (productos incorrectos, sobrantes)
   - **Competitividad de Precios**:
     - Variación de precios en el tiempo (tendencia)
     - Comparativa de precios vs. otros proveedores del mismo producto
     - % de descuentos obtenidos
   - **Confiabilidad Financiera**:
     - % de facturas conciliadas sin discrepancias
     - Tiempo promedio de resolución de problemas
     - Monto total de notas de crédito emitidas
2. El sistema genera una "Scorecard de Proveedor" con calificación global:
   - Cada métrica tiene un peso configurable (ej: Cumplimiento 40%, Calidad 30%, Precio 20%, Financiero 10%)
   - Calificación final de 0-100 o escala (A, B, C, D, F)
   - Clasificación visual: 🟢 Excelente (90-100), 🟡 Bueno (70-89), 🟠 Regular (50-69), 🔴 Deficiente (<50)
3. Vista de "Ranking de Proveedores":
   - Lista ordenada por calificación
   - Filtros por categoría de producto
   - Comparativa de top 10 proveedores en gráficos
4. Reportes disponibles:
   - "Análisis de Desempeño por Proveedor" (individual, PDF)
   - "Comparativa de Proveedores" (varios proveedores, mismo producto)
   - "Tendencia de Desempeño" (evolución en el tiempo, gráfico)
   - "Proveedores en Riesgo" (con calificación decreciente o baja)
5. Acciones basadas en el desempeño:
   - Proveedores con calificación A: Elegibles para términos preferenciales (mejores plazos, descuentos)
   - Proveedores con calificación D-F: Alertar para evaluación de descontinuación
   - Proveedores con tendencia negativa: Agendar reunión de mejora
6. El sistema permite:
   - Agregar notas cualitativas sobre el proveedor (servicio al cliente, comunicación, flexibilidad)
   - Registrar incidencias específicas que afectan la calificación
   - Exportar scorecards para reuniones de revisión de proveedores
   - Configurar alertas cuando un proveedor cae por debajo de umbral mínimo

**Consideraciones Multi-tenant:**
- Cada negocio tiene su propia evaluación de proveedores (no compartida).
- Los pesos de las métricas son configurables por negocio.

**Seguridad:**
- Requiere permiso: `proveedores_analisis_ver`
- Las calificaciones son confidenciales, solo visibles para gerencia y compras.

**UX:**
- Dashboard de "Desempeño de Proveedores" con KPIs principales.
- Gráficos interactivos (radar chart para comparar proveedores en múltiples dimensiones).
- Código de colores consistente en toda la interfaz.
- Exportación a PowerPoint para presentaciones ejecutivas.

**Reglas de Negocio:**
- Las métricas se calculan sobre los últimos 12 meses (rolling window).
- Se requiere un mínimo de 5 órdenes de compra para que la calificación sea significativa.
- Los proveedores nuevos (< 5 órdenes) tienen calificación "Nuevo" hasta acumular historial.
- Las incidencias graves (productos vencidos, fraude) pueden descalificar automáticamente al proveedor independientemente de otras métricas.

---

### **RF-INV-031: Gestionar Cotizaciones y Comparar Precios**

**Descripción:**  
El sistema debe permitir solicitar cotizaciones a múltiples proveedores para los mismos productos, compararlas lado a lado en términos de precio, calidad, plazos de entrega y condiciones comerciales, facilitando la toma de decisión de compra basada en el mejor valor global.

**Criterios de Aceptación:**
1. El usuario puede crear una "Solicitud de Cotización" (RFQ - Request for Quotation):
   - **Encabezado**:
     - Número de RFQ (auto-generado: RFQ-YYYYMMDD-####)
     - Fecha de emisión
     - Fecha límite de respuesta
     - Lista de proveedores invitados (multiselección)
   - **Productos a cotizar**:
     - Producto (búsqueda)
     - Cantidad requerida
     - Unidad de medida
     - Especificaciones/requisitos especiales
     - Fecha de entrega deseada
   - **Criterios de evaluación** (pesos):
     - Precio (ej: 50%)
     - Tiempo de entrega (ej: 20%)
     - Términos de pago (ej: 15%)
     - Calidad/certificaciones (ej: 15%)
   - **Condiciones generales**: Lugar de entrega, forma de pago, garantías, etc.
2. El sistema genera automáticamente un documento de RFQ (PDF) y lo envía por email a los proveedores seleccionados.
3. Los proveedores pueden responder:
   - **Vía portal de proveedores** (si está implementado): Ingreso directo de cotización al sistema
   - **Vía email**: El usuario registra manualmente la cotización recibida
   - El sistema registra: Proveedor, fecha de respuesta, precios por ítem, plazos, condiciones
4. Vista de "Comparativa de Cotizaciones":
   | Producto | Cantidad | Proveedor A | Proveedor B | Proveedor C | Mejor Precio |
   |----------|----------|-------------|-------------|-------------|--------------|
   | Whisky X | 100 un   | $25.00 / 15 días / 30 días crédito | $24.50 / 20 días / contado | $26.00 / 10 días / 60 días crédito | Proveedor B |
   - Columnas configurables: Precio unitario, subtotal, plazo de entrega, términos de pago, garantía, marca/origen
   - Resaltado automático del mejor precio en cada fila
   - Cálculo de "Mejor Valor" considerando todos los criterios con pesos configurados
5. El sistema calcula una "Calificación Global" por proveedor para cada RFQ:
   - Normaliza cada criterio (precio, plazo, etc.) a escala 0-100
   - Aplica los pesos configurados
   - Muestra el proveedor recomendado
6. El usuario puede:
   - Negociar con proveedores (registrar contrapropuestas)
   - Dividir la orden entre varios proveedores (split order)
   - Convertir una cotización seleccionada en orden de compra con un clic
   - Archivar cotizaciones para referencia futura
   - Notificar a proveedores no seleccionados (cortesía profesional)
7. Reportes:
   - "Historial de Cotizaciones por Producto" (análisis de tendencia de precios)
   - "Tiempo Promedio de Respuesta por Proveedor"
   - "% de Cotizaciones Ganadas por Proveedor"

**Consideraciones Multi-tenant:**
- Cada negocio gestiona sus propias RFQs y cotizaciones.
- Las cotizaciones no se comparten entre negocios.

**Seguridad:**
- Requiere permiso: `compras_cotizacion_gestionar`
- Las cotizaciones son confidenciales: solo visibles para el equipo de compras.
- Los proveedores no deben ver las ofertas de otros proveedores (competencia justa).

**UX:**
- Plantillas de RFQ para productos frecuentes (ahorro de tiempo).
- Vista de tabla dinámica con filtros y ordenamiento.
- Gráficos de comparación visual (barras para precios, radar para criterios múltiples).
- Notificación automática cuando un proveedor responde.
- Vista móvil para revisión de cotizaciones en cualquier lugar.

**Reglas de Negocio:**
- Los proveedores tienen plazo de 5-7 días para responder (configurable).
- Las cotizaciones vencidas (>30 días) se marcan como "desactualizadas" y no se pueden convertir en OC sin revalidar precios.
- Se recomienda solicitar mínimo 3 cotizaciones para compras > $1000 USD (buena práctica).
- Las cotizaciones ganadoras se notifican al proveedor; las perdedoras reciben un agradecimiento profesional.

---

### **RF-INV-032: Configurar Reorden Automático**

**Descripción:**  
El sistema debe permitir configurar reglas de reorden automático para productos críticos o de alta rotación, generando automáticamente órdenes de compra sugeridas o solicitudes cuando el stock alcance el punto de reorden, minimizando quiebres de stock y optimizando el capital de trabajo.

**Criterios de Aceptación:**
1. Para cada producto, se puede configurar:
   - **Punto de Reorden (ROP - Reorder Point)**: Nivel de stock que activa el reorden
   - **Cantidad de Reorden (ROQ - Reorder Quantity)**: Cuánto ordenar
   - **Stock Mínimo**: Nivel de seguridad para evitar quiebres
   - **Stock Máximo**: Nivel máximo deseado para no sobre-stockear
   - **Lead Time**: Días que tarda el proveedor en entregar
   - **Proveedor Principal**: A quién ordenar por defecto
   - **Proveedor Alternativo**: Backup si el principal no está disponible
2. Métodos de cálculo automático de ROP y ROQ:
   - **Manual**: Usuario define valores fijos
   - **Basado en Historial**: ROP = Demanda Promedio × Lead Time + Stock de Seguridad
   - **Basado en Tendencia**: Considera estacionalidad y crecimiento
3. El sistema ejecuta un job diario que:
   - Revisa todos los productos con reorden automático habilitado
   - Compara el stock actual vs. el punto de reorden
   - Si stock actual ≤ ROP, genera una "Sugerencia de Compra"
4. Vista de "Sugerencias de Compra Automáticas":
   - Lista de productos que alcanzaron su punto de reorden
   - Información: Producto, stock actual, ROP, cantidad sugerida, proveedor recomendado, urgencia
   - Clasificación por urgencia:
     - 🔴 Crítico: Stock < stock mínimo (riesgo de quiebre)
     - 🟡 Urgente: Stock = ROP
     - 🟢 Planificado: Proyección de alcanzar ROP en 7 días
5. El usuario puede:
   - **Aprobar sugerencia**: Crea orden de compra automáticamente
   - **Modificar cantidad**: Ajustar ROQ antes de crear la orden
   - **Cambiar proveedor**: Seleccionar alternativo
   - **Rechazar/Posponer**: Si hay razones especiales (producto en descontinuación, promoción próxima)
   - **Aprobar en lote**: Seleccionar múltiples sugerencias y crear múltiples órdenes con un clic
6. Configuración avanzada:
   - **Agrupación por proveedor**: Consolidar múltiples productos del mismo proveedor en una sola orden
   - **Días de inventario objetivo**: El sistema calcula ROQ para mantener X días de stock
   - **Estacionalidad**: Ajustar ROP/ROQ según época del año (ej: más bebidas en verano)
   - **Promociones planificadas**: Aumentar stock anticipadamente si hay campaña de ventas
7. Reportes de efectividad:
   - "Quiebres de Stock Evitados por Reorden Automático"
   - "Exceso de Inventario por Sobre-pedido"
   - "Precisión del Forecast vs. Demanda Real"

**Consideraciones Multi-tenant:**
- Cada negocio configura sus propias reglas de reorden.
- El cálculo de ROP puede usar diferentes algoritmos según el negocio.

**Seguridad:**
- Requiere permiso: `inventario_reorden_configurar` (para configurar), `compras_sugerencia_aprobar` (para generar órdenes).
- Las órdenes generadas automáticamente pueden requerir aprobación si superan límites de monto.

**UX:**
- Asistente de configuración: "Deja que el sistema calcule ROP/ROQ por ti" (basado en historial).
- Dashboard con widget "Productos que Requieren Reorden" con contador de urgencias.
- Simulador: "¿Qué pasaría si ordeno X cantidad?" (proyección de stock futuro).
- Notificaciones push cuando hay sugerencias críticas.

**Reglas de Negocio:**
- Los productos perecederos deben considerar su vida útil en el cálculo de ROQ (no ordenar más de lo que se puede vender antes del vencimiento).
- El reorden automático se desactiva para productos marcados como "Descontinuado" o "Estacional Fuera de Temporada".
- Si un producto tiene múltiples proveedores, el sistema alterna entre ellos para diversificar riesgo.
- Las sugerencias no aprobadas en 7 días se marcan como "vencidas" y se re-generan con datos actualizados.

---

## Resumen del Módulo

El **Módulo III: Gestión de Productos e Inventario** proporciona las funcionalidades completas para:

### Submódulo 6.3.1: Catálogo de Productos
- **14 requisitos funcionales** (RF-INV-001 a RF-INV-014)
- Gestión completa de productos, categorías, marcas
- Combos promocionales y configuración de visibilidad en storefront
- Búsqueda avanzada, importación masiva, y sistema de promociones

### Submódulo 6.3.2: Control de Inventario
- **9 requisitos funcionales** (RF-INV-015 a RF-INV-023)
- Sistema FIFO obligatorio para productos perecederos
- Control completo de lotes con trazabilidad
- Alertas de stock bajo y productos próximos a vencer
- Gestión de productos vencidos, dañados y mermas

### Submódulo 6.3.3: Compras y Proveedores
- **9 requisitos funcionales** (RF-INV-024 a RF-INV-032)
- Gestión integral de proveedores y evaluación de desempeño
- Órdenes de compra con flujo de aprobación
- Recepción de mercancía y conciliación de facturas (three-way matching)
- Sistema de reorden automático y gestión de cotizaciones

**Total: 32 requisitos funcionales** que garantizan el control completo del inventario, trazabilidad de productos alcohólicos (cumplimiento regulatorio), y optimización de la cadena de suministro para licorerías.

---

**Fin del Módulo III**
