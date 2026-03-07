import { Link } from 'react-router-dom';
import { MapPin, Phone, Mail } from 'lucide-react';

export const StorefrontFooter = ({ config, slug }) => {
  const storeName = config?.nombreTienda || 'Tienda';
  const negocio = config?.negocio;

  return (
    <footer className="bg-slate-900 text-gray-400 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Store info */}
          <div>
            <h3 className="text-white font-semibold text-lg mb-3">{storeName}</h3>
            {negocio?.razonSocial && (
              <p className="text-sm mb-1">{negocio.razonSocial}</p>
            )}
            {negocio?.ruc && (
              <p className="text-sm">RUC: {negocio.ruc}</p>
            )}
          </div>

          {/* Contact */}
          <div>
            <h4 className="text-white font-medium mb-3">Contacto</h4>
            <div className="space-y-2 text-sm">
              {negocio?.direccion && (
                <div className="flex items-start gap-2">
                  <MapPin size={14} className="mt-0.5 shrink-0" />
                  <span>{negocio.direccion}</span>
                </div>
              )}
              {negocio?.telefono && (
                <div className="flex items-center gap-2">
                  <Phone size={14} className="shrink-0" />
                  <span>{negocio.telefono}</span>
                </div>
              )}
              {negocio?.email && (
                <div className="flex items-center gap-2">
                  <Mail size={14} className="shrink-0" />
                  <span>{negocio.email}</span>
                </div>
              )}
            </div>
          </div>

          {/* Links */}
          <div>
            <h4 className="text-white font-medium mb-3">Enlaces</h4>
            <div className="space-y-2 text-sm">
              <Link to={`/tienda/${slug}/catalogo`} className="block hover:text-white transition-colors">
                Catálogo
              </Link>
              <Link to={`/tienda/${slug}/mis-pedidos`} className="block hover:text-white transition-colors">
                Mis Pedidos
              </Link>
            </div>
          </div>
        </div>

        <div className="border-t border-slate-800 mt-8 pt-6 text-center text-xs">
          <p>© {new Date().getFullYear()} {storeName}. Todos los derechos reservados.</p>
          <p className="mt-1 text-gray-500">Powered by DrinkGo</p>
        </div>
      </div>
    </footer>
  );
};
