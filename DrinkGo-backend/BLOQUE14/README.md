# BLOQUE 14: TIENDA ONLINE (STOREFRONT)

## 📘 Descripción

Implementación completa del módulo de Tienda Online para el sistema multi-tenant DrinkGo. Permite a cada negocio configurar y gestionar su propia tienda online con páginas personalizadas.

## 🗂️ Archivos Creados

### Entidades (entity/)
- ✅ `ConfiguracionTiendaOnline.java` - Configuración general de la tienda
- ✅ `PaginaTiendaOnline.java` - Páginas personalizadas

### DTOs (dto/)
- ✅ `ConfiguracionTiendaOnlineDTO.java`
- ✅ `PaginaTiendaOnlineDTO.java`

### Repositories (repository/)
- ✅ `ConfiguracionTiendaOnlineRepository.java`
- ✅ `PaginaTiendaOnlineRepository.java`

### Services (service/)
- ✅ `ConfiguracionTiendaOnlineService.java`
- ✅ `PaginaTiendaOnlineService.java`

### Controllers (controller/)
- ✅ `ConfiguracionTiendaOnlineController.java`
- ✅ `PaginaTiendaOnlineController.java`

## 📦 Archivos de Prueba

- **`datos_prueba_bloque_14.sql`** - Script SQL con datos de ejemplo
- **`POSTMAN_TESTING_GUIDE_BLOQUE_14.md`** - Guía completa de pruebas en Postman

## 🚀 Instrucciones de Instalación

### 1. Ejecutar el script SQL
```sql
-- En MySQL Workbench o phpMyAdmin
source datos_prueba_bloque_14.sql;
```

O copiar y pegar el contenido directamente.

### 2. Verificar tablas creadas
```sql
USE drinkgo_db;
SHOW TABLES LIKE '%tienda_online%';
```

Deberías ver:
- `configuracion_tienda_online`
- `paginas_tienda_online`

### 3. Iniciar el servidor Spring Boot
```bash
mvn spring-boot:run
```

### 4. Verificar que el servidor esté corriendo
```bash
# Abrir en navegador
http://localhost:8080
```

## 🧪 Pruebas en Postman

Ver la guía completa en: `POSTMAN_TESTING_GUIDE_BLOQUE_14.md`

### Endpoints principales:

#### Configuración de Tienda
```
GET    /api/tienda-online/configuracion?negocioId=1
GET    /api/tienda-online/configuracion/slug/drinkgo-premium
POST   /api/tienda-online/configuracion
PUT    /api/tienda-online/configuracion/1?negocioId=1
PATCH  /api/tienda-online/configuracion/estado?negocioId=1&habilitado=true
```

#### Páginas de Tienda
```
GET    /api/tienda-online/paginas?negocioId=1
GET    /api/tienda-online/paginas/publicadas?negocioId=1
GET    /api/tienda-online/paginas/slug/sobre-nosotros?negocioId=1
POST   /api/tienda-online/paginas
PUT    /api/tienda-online/paginas/1?negocioId=1
DELETE /api/tienda-online/paginas/6?negocioId=1
PATCH  /api/tienda-online/paginas/6/publicar?negocioId=1&publicado=true
```

## 📊 Estructura de Base de Datos

### Tabla: configuracion_tienda_online
```sql
- id (PK)
- negocio_id (FK, UNIQUE)
- esta_habilitado
- nombre_tienda
- slug_tienda (UNIQUE)
- dominio_personalizado
- mensaje_bienvenida
- imagenes_banner (JSON)
- categorias_destacadas (JSON)
- titulo_seo
- descripcion_seo
- palabras_clave_seo
- id_google_analytics
- id_pixel_facebook
- enlaces_sociales (JSON)
- monto_minimo_pedido
- monto_maximo_pedido
- terminos_condiciones
- politica_privacidad
- politica_devoluciones
- mostrar_precios_con_impuesto
- permitir_compra_invitado
- requiere_verificacion_edad
- creado_en
- actualizado_en
```

### Tabla: paginas_tienda_online
```sql
- id (PK)
- negocio_id (FK)
- titulo
- slug (UNIQUE por negocio)
- contenido (LONGTEXT)
- esta_publicado
- orden
- meta_titulo
- meta_descripcion
- creado_en
- actualizado_en
```

## ✨ Características Implementadas

### Configuración de Tienda
- ✅ Crear configuración única por negocio
- ✅ Actualizar configuración
- ✅ Habilitar/Deshabilitar tienda online
- ✅ Slug único para URL amigable
- ✅ Soporte para SEO (meta tags)
- ✅ Integración con Google Analytics y Facebook Pixel
- ✅ Configuración de montos mínimo/máximo de pedido
- ✅ Gestión de términos, condiciones y políticas

### Páginas Personalizadas
- ✅ Crear páginas con contenido HTML
- ✅ Publicar/Despublicar páginas
- ✅ Ordenar páginas
- ✅ Slug único por negocio
- ✅ Meta tags para SEO
- ✅ Endpoint público para páginas publicadas
- ✅ Endpoint privado (admin) para todas las páginas

## 🔒 Multi-tenant

Cada negocio tiene:
- Su propia configuración de tienda aislada
- Sus propias páginas personalizadas
- Slug único en todo el sistema para configuración
- Slug único por negocio para páginas

## 🎯 Casos de Uso

1. **Configurar Tienda Online**
   - El admin del negocio crea la configuración
   - Define nombre, slug, mensaje de bienvenida
   - Configura SEO y redes sociales
   - Establece montos mínimo/máximo de pedido

2. **Crear Páginas Personalizadas**
   - "Sobre Nosotros"
   - "Cómo Comprar"
   - "Zonas de Entrega"
   - "Términos y Condiciones"
   - "Política de Privacidad"
   - "Preguntas Frecuentes"
   - "Contáctanos"

3. **Publicar Contenido**
   - Crear páginas en modo borrador
   - Previsualizar antes de publicar
   - Publicar cuando esté listo
   - Despublicar si es necesario

4. **Gestionar Orden de Páginas**
   - Definir orden de aparición en menú
   - Reordenar según prioridad

## 📝 Notas Adicionales

- El campo `contenido` soporta HTML completo
- Los campos JSON (`imagenes_banner`, `categorias_destacadas`, `enlaces_sociales`) almacenan datos estructurados
- La tienda puede estar deshabilitada mientras se configura
- Las páginas no publicadas solo son visibles para administradores

## 🐛 Troubleshooting

### Error: "Ya existe una configuración"
- Cada negocio solo puede tener UNA configuración
- Usa PUT para actualizar la existente

### Error: "El slug ya está en uso"
- Los slugs de configuración son únicos en todo el sistema
- Los slugs de páginas son únicos por negocio
- Elige otro slug

### Error: 404 en endpoints
- Verifica que el servidor esté corriendo en puerto 8080
- Verifica la URL base: `/api/tienda-online/`

## 📚 Referencias

- Requisitos: Ver archivo SQL `drinkgo_database.sql` (BLOQUE 14)
- Datos de prueba: `datos_prueba_bloque_14.sql`
- Guía de Postman: `POSTMAN_TESTING_GUIDE_BLOQUE_14.md`

---

**Estado: ✅ COMPLETO Y LISTO PARA PRUEBAS**

Desarrollado para: DrinkGo - Sistema Multi-tenant de Licorerías  
Bloque: 14 - Tienda Online (Storefront)  
Fecha: Febrero 2026
