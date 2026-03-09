import { useState, useEffect } from 'react';
import { Outlet, useParams } from 'react-router-dom';
import { StorefrontHeader } from '../components/layout/StorefrontHeader';
import { StorefrontFooter } from '../components/layout/StorefrontFooter';
import { useStorefrontConfig } from '../hooks/useStorefrontConfig';
import { useStorefrontSedes } from '../hooks/useStorefrontSedes';
import { useCartStore } from '../stores/cartStore';
import { useStorefrontAuthStore } from '../stores/storefrontAuthStore';
import { Loader2 } from 'lucide-react';

export const StorefrontLayout = () => {
  const { slug } = useParams();
  const { config, isLoading, isError } = useStorefrontConfig(slug);
  const { sedes, isLoading: sedesLoading } = useStorefrontSedes(slug);
  const [selectedSede, setSelectedSede] = useState(null);
  const setCartContext = useCartStore((s) => s.setContext);
  const { setSlug, setNegocioId } = useStorefrontAuthStore();

  // Set slug in auth store for redirects
  useEffect(() => {
    if (slug) setSlug(slug);
  }, [slug, setSlug]);

  // Set negocioId when config loads
  useEffect(() => {
    if (config?.negocio?.id) setNegocioId(config.negocio.id);
  }, [config, setNegocioId]);

  // Inject brand CSS variables from configVisual
  useEffect(() => {
    if (!config?.configVisual) return;
    try {
      const cv = typeof config.configVisual === 'string'
        ? JSON.parse(config.configVisual)
        : config.configVisual;
      if (cv?.color_primario) {
        document.documentElement.style.setProperty('--brand-primary', cv.color_primario);
      }
      if (cv?.color_secundario) {
        document.documentElement.style.setProperty('--brand-secondary', cv.color_secundario);
      }
    } catch (_) { /* ignore malformed JSON */ }
    return () => {
      document.documentElement.style.removeProperty('--brand-primary');
      document.documentElement.style.removeProperty('--brand-secondary');
    };
  }, [config]);

  // Auto-select first sede
  useEffect(() => {
    if (sedes.length > 0 && !selectedSede) {
      setSelectedSede(sedes.find((s) => s.esPrincipal) || sedes[0]);
    }
  }, [sedes, selectedSede]);

  // Sync cart context
  useEffect(() => {
    if (slug && selectedSede?.id) {
      setCartContext(slug, selectedSede.id);
    }
  }, [slug, selectedSede, setCartContext]);

  // Page title
  useEffect(() => {
    if (!config?.negocio) return;
    const nombre = config.negocio.nombreComercial || config.negocio.razonSocial || 'Tienda';
    document.title = nombre;
    return () => { document.title = 'DrinkGo'; };
  }, [config]);

  if (isLoading || sedesLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <Loader2 size={40} className="animate-spin text-amber-500 mx-auto" />
          <p className="text-gray-500 mt-3 text-sm">Cargando tienda...</p>
        </div>
      </div>
    );
  }

  if (isError || !config) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center max-w-md px-6">
          <div className="text-6xl mb-4">🏪</div>
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Tienda no encontrada</h1>
          <p className="text-gray-600">
            No pudimos encontrar una tienda con la dirección <strong>{slug}</strong>.
            Verifica que la URL sea correcta.
          </p>
        </div>
      </div>
    );
  }

  if (!config.estaHabilitado) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center max-w-md px-6">
          <div className="text-6xl mb-4">🔒</div>
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Tienda no disponible</h1>
          <p className="text-gray-600">
            Esta tienda se encuentra temporalmente deshabilitada. Inténtalo más tarde.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-gray-50">
      <StorefrontHeader
        config={config}
        sedes={sedes}
        selectedSede={selectedSede}
        onSedeChange={setSelectedSede}
        slug={slug}
      />
      <main className="flex-1">
        <Outlet
          context={{
            slug,
            config,
            sedes,
            selectedSede,
            setSelectedSede,
          }}
        />
      </main>
      <StorefrontFooter config={config} slug={slug} />
    </div>
  );
};
