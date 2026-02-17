-- ============================================================
-- SEED v14: Tienda Online (Storefront)
-- DrinkGo Platform – Bloque 14
-- PREREQUISITOS: ejecutar v3 primero (negocios 1 y 2)
-- ============================================================

USE drinkgo_db;

-- ============================================================
-- 1. CONFIGURACIÓN DE TIENDA ONLINE
-- ============================================================

-- Negocio 1: DrinkGo Premium (Habilitada)
INSERT INTO configuracion_tienda_online (
    negocio_id, esta_habilitado, nombre_tienda, slug_tienda,
    dominio_personalizado, mensaje_bienvenida,
    imagenes_banner, categorias_destacadas,
    titulo_seo, descripcion_seo, palabras_clave_seo,
    id_google_analytics, id_pixel_facebook, enlaces_sociales,
    monto_minimo_pedido, monto_maximo_pedido,
    terminos_condiciones, politica_privacidad, politica_devoluciones,
    mostrar_precios_con_impuesto, permitir_compra_invitado,
    requiere_verificacion_edad,
    creado_en, actualizado_en
) VALUES (
    1, 1, 'DrinkGo Premium Store', 'drinkgo-premium',
    'https://premium.drinkgo.pe',
    '¡Bienvenido a DrinkGo Premium! Encuentra los mejores licores y bebidas con entrega rápida.',
    '["https://cdn.drinkgo.pe/banners/banner-1.jpg","https://cdn.drinkgo.pe/banners/banner-2.jpg"]',
    '[1,2,3,5]',
    'DrinkGo Premium - Licorería Online en Lima',
    'Compra licores, vinos, cervezas y más en línea. Entrega rápida en Lima y provincias.',
    'licorería online, vinos, cervezas, licores, delivery alcohol',
    'UA-123456789-1',
    'pixel-fb-987654321',
    '{"facebook":"https://facebook.com/drinkgopremium","instagram":"https://instagram.com/drinkgopremium","whatsapp":"+51987654321"}',
    30.00, 5000.00,
    'El cliente acepta los términos y condiciones de compra. Prohibida la venta a menores de edad.',
    'Respetamos tu privacidad. Tus datos personales están protegidos según la Ley de Protección de Datos Personales.',
    'Aceptamos devoluciones dentro de 7 días si el producto está sellado y en perfecto estado.',
    1, 0, 1,
    NOW(), NOW()
);

-- Negocio 2: Licorería Express (Deshabilitada, en configuración)
INSERT INTO configuracion_tienda_online (
    negocio_id, esta_habilitado, nombre_tienda, slug_tienda,
    mensaje_bienvenida,
    titulo_seo, descripcion_seo,
    monto_minimo_pedido, monto_maximo_pedido,
    mostrar_precios_con_impuesto, permitir_compra_invitado,
    requiere_verificacion_edad,
    creado_en, actualizado_en
) VALUES (
    2, 0, 'Licorería Express', 'licoreria-express',
    '¡Próximamente! Tu licorería de confianza ahora online.',
    'Licorería Express - Próximamente Online',
    'Estamos preparando nuestra tienda online para ofrecerte el mejor servicio.',
    25.00, 3000.00,
    1, 1, 1,
    NOW(), NOW()
);

-- ============================================================
-- 2. PÁGINAS DE TIENDA ONLINE
-- ============================================================

-- Páginas para Negocio 1 (DrinkGo Premium)
INSERT INTO paginas_tienda_online (
    negocio_id, titulo, slug, contenido,
    esta_publicado, orden, meta_titulo, meta_descripcion,
    creado_en, actualizado_en
) VALUES
-- Sobre Nosotros
(1, 'Sobre Nosotros', 'sobre-nosotros',
 '<h1>Sobre DrinkGo Premium</h1>
<p>Somos una licorería online con más de 10 años de experiencia en el mercado peruano. Ofrecemos productos de la más alta calidad con entrega rápida y segura.</p>
<h2>Nuestra Misión</h2>
<p>Brindar la mejor experiencia de compra online de bebidas alcohólicas, garantizando calidad, variedad y servicio excepcional.</p>
<h2>¿Por qué elegirnos?</h2>
<ul>
  <li>Productos 100% originales y certificados</li>
  <li>Entrega en tiempo récord</li>
  <li>Precios competitivos</li>
  <li>Atención al cliente 24/7</li>
</ul>',
 1, 1, 'Sobre Nosotros - DrinkGo Premium',
 'Conoce más sobre DrinkGo Premium, tu licorería online de confianza.',
 NOW(), NOW()),

-- Cómo Comprar
(1, 'Cómo Comprar', 'como-comprar',
 '<h1>¿Cómo realizar tu pedido?</h1>
<p>Comprar en DrinkGo Premium es muy fácil. Sigue estos simples pasos:</p>
<ol>
  <li><strong>Explora nuestro catálogo:</strong> Navega por nuestras categorías de productos.</li>
  <li><strong>Agrega al carrito:</strong> Selecciona la cantidad y añade tus productos favoritos.</li>
  <li><strong>Verifica tu edad:</strong> Recuerda que la venta de alcohol está prohibida a menores de edad.</li>
  <li><strong>Completa tu pedido:</strong> Ingresa tus datos de entrega y elige tu método de pago.</li>
  <li><strong>Recibe tu pedido:</strong> ¡Listo! Recibirás tu pedido en la puerta de tu casa.</li>
</ol>
<p><strong>Métodos de pago aceptados:</strong></p>
<ul>
  <li>Tarjetas de crédito/débito (Visa, Mastercard)</li>
  <li>Transferencias bancarias</li>
  <li>Yape / Plin</li>
  <li>Efectivo contra entrega</li>
</ul>',
 1, 2, 'Cómo Comprar - Guía de Compra',
 'Aprende cómo realizar tu pedido en DrinkGo Premium de manera fácil y rápida.',
 NOW(), NOW()),

-- Zonas de Entrega
(1, 'Zonas de Entrega', 'zonas-entrega',
 '<h1>Zonas de Delivery</h1>
<p>Realizamos entregas en las siguientes zonas de Lima:</p>
<h2>Lima Moderna</h2>
<ul>
  <li>San Isidro - Tiempo estimado: 30-45 min</li>
  <li>Miraflores - Tiempo estimado: 30-45 min</li>
  <li>San Borja - Tiempo estimado: 30-45 min</li>
  <li>Surco - Tiempo estimado: 45-60 min</li>
  <li>La Molina - Tiempo estimado: 45-60 min</li>
</ul>
<h2>Lima Norte</h2>
<ul>
  <li>Los Olivos - Tiempo estimado: 60-90 min</li>
  <li>Independencia - Tiempo estimado: 60-90 min</li>
  <li>San Martín de Porres - Tiempo estimado: 60-90 min</li>
</ul>
<p><strong>Costo de delivery:</strong> Varía según la zona. Consulta al momento de realizar tu pedido.</p>',
 1, 3, 'Zonas de Entrega - Cobertura de Delivery',
 'Conoce las zonas donde realizamos entregas y los tiempos estimados.',
 NOW(), NOW()),

-- Preguntas Frecuentes
(1, 'Preguntas Frecuentes', 'preguntas-frecuentes',
 '<h1>Preguntas Frecuentes (FAQ)</h1>
<h3>¿Cuál es el monto mínimo de pedido?</h3>
<p>El monto mínimo de pedido es S/ 30.00.</p>
<h3>¿Cuánto tiempo demora la entrega?</h3>
<p>Dependiendo de la zona, entre 30 minutos y 2 horas.</p>
<h3>¿Puedo comprar sin registrarme?</h3>
<p>No, por temas de seguridad y verificación de edad, necesitas crear una cuenta.</p>
<h3>¿Qué hago si mi producto llega dañado?</h3>
<p>Contáctanos inmediatamente a través de nuestro WhatsApp y gestionaremos tu devolución.</p>
<h3>¿Venden a menores de edad?</h3>
<p>No. La venta de bebidas alcohólicas a menores de 18 años está prohibida por ley.</p>',
 1, 4, 'Preguntas Frecuentes - FAQ',
 'Encuentra respuestas a las preguntas más comunes sobre nuestro servicio.',
 NOW(), NOW()),

-- Contáctanos
(1, 'Contáctanos', 'contactanos',
 '<h1>Contáctanos</h1>
<p>Estamos aquí para ayudarte. Contáctanos por cualquiera de estos medios:</p>
<h2>Atención al Cliente</h2>
<ul>
  <li><strong>WhatsApp:</strong> +51 987 654 321</li>
  <li><strong>Teléfono:</strong> (01) 234-5678</li>
  <li><strong>Email:</strong> contacto@drinkgopremium.pe</li>
</ul>
<h2>Horarios de Atención</h2>
<p>Lunes a Domingo: 10:00 AM - 11:00 PM</p>
<h2>Redes Sociales</h2>
<ul>
  <li>Facebook: @drinkgopremium</li>
  <li>Instagram: @drinkgopremium</li>
</ul>
<p>También puedes visitarnos en nuestras tiendas físicas.</p>',
 1, 5, 'Contáctanos - DrinkGo Premium',
 'Ponte en contacto con nosotros por WhatsApp, teléfono o redes sociales.',
 NOW(), NOW()),

-- Borrador (no publicada)
(1, 'Promociones Especiales', 'promociones-especiales',
 '<h1>Promociones del Mes</h1>
<p>Contenido en desarrollo...</p>',
 0, 10, 'Promociones Especiales',
 'Descubre nuestras promociones y ofertas del mes.',
 NOW(), NOW());

-- Página para Negocio 2 (Licorería Express)
INSERT INTO paginas_tienda_online (
    negocio_id, titulo, slug, contenido,
    esta_publicado, orden, meta_titulo, meta_descripcion,
    creado_en, actualizado_en
) VALUES (
    2, 'Próximamente', 'proximamente',
    '<h1>¡Muy Pronto!</h1>
<p>Estamos preparando nuestra tienda online. Regresa pronto para disfrutar de nuestras ofertas.</p>',
    1, 1, 'Próximamente - Licorería Express',
    'Nuestra tienda online estará disponible pronto.',
    NOW(), NOW()
);

-- ============================================================
-- FIN v14
-- ============================================================
