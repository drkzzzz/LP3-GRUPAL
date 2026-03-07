import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ShoppingCart,
  Menu,
  X,
  User,
  LogOut,
  MapPin,
  ChevronDown,
  Package,
  UserCog,
  RotateCcw,
} from 'lucide-react';
import { useCartStore } from '../../stores/cartStore';
import { useStorefrontAuthStore } from '../../stores/storefrontAuthStore';
import { getImageUrl } from '../../services/storefrontService';

export const StorefrontHeader = ({ config, sedes, selectedSede, onSedeChange, slug }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [sedeDropdownOpen, setSedeDropdownOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const navigate = useNavigate();

  const itemCount = useCartStore((s) => s.getItemCount());
  const { customer, isAuthenticated, logout } = useStorefrontAuthStore();
  const isLoggedIn = isAuthenticated();

  const storeName = config?.nombreTienda || 'Tienda';

  const handleLogout = () => {
    logout();
    setUserMenuOpen(false);
  };

  return (
    <header className="sticky top-0 z-40 text-white shadow-lg" style={{ backgroundColor: 'var(--brand-primary, #0f172a)' }}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo / Store name */}
          <Link to={`/tienda/${slug}`} className="flex items-center gap-3 shrink-0">
            {config?.negocio?.urlLogo ? (
              <img
                src={getImageUrl(config.negocio.urlLogo)}
                alt={storeName}
                className="h-8 w-8 rounded-full object-cover"
              />
            ) : (
              <div className="h-8 w-8 rounded-full flex items-center justify-center font-bold text-sm" style={{ backgroundColor: 'var(--brand-secondary, #f59e0b)' }}>
                {storeName.charAt(0).toUpperCase()}
              </div>
            )}
            <span className="text-lg font-semibold hidden sm:block">{storeName}</span>
          </Link>

          {/* Sede selector (desktop) */}
          {sedes.length > 1 && (
            <div className="hidden md:block relative">
              <button
                onClick={() => setSedeDropdownOpen(!sedeDropdownOpen)}
                className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-sm transition-colors"
              >
                <MapPin size={14} className="text-amber-400" />
                <span className="max-w-[200px] truncate">
                  {selectedSede?.nombre || 'Seleccionar sede'}
                </span>
                <ChevronDown size={14} />
              </button>
              {sedeDropdownOpen && (
                <>
                  <div className="fixed inset-0 z-10" onClick={() => setSedeDropdownOpen(false)} />
                  <div className="absolute top-full mt-1 left-0 bg-white text-gray-900 rounded-lg shadow-xl border border-gray-200 py-1 z-20 min-w-[240px]">
                    {sedes.map((sede) => (
                      <button
                        key={sede.id}
                        onClick={() => {
                          onSedeChange(sede);
                          setSedeDropdownOpen(false);
                        }}
                        className={`w-full text-left px-4 py-2.5 text-sm hover:bg-amber-50 transition-colors ${
                          selectedSede?.id === sede.id ? 'bg-amber-50 text-amber-700 font-medium' : ''
                        }`}
                      >
                        <div className="font-medium">{sede.nombre}</div>
                        {sede.direccion && (
                          <div className="text-xs text-gray-500 mt-0.5">{sede.direccion}</div>
                        )}
                      </button>
                    ))}
                  </div>
                </>
              )}
            </div>
          )}

          {/* Nav links (desktop) */}
          <nav className="hidden md:flex items-center gap-1">
            <Link
              to={`/tienda/${slug}`}
              className="px-3 py-2 rounded-lg text-sm text-gray-300 hover:text-white hover:bg-slate-800 transition-colors"
            >
              Inicio
            </Link>
            <Link
              to={`/tienda/${slug}/catalogo`}
              className="px-3 py-2 rounded-lg text-sm text-gray-300 hover:text-white hover:bg-slate-800 transition-colors"
            >
              Catálogo
            </Link>
          </nav>

          {/* Right actions */}
          <div className="flex items-center gap-2">
            {/* Cart button */}
            <button
              onClick={() => navigate(`/tienda/${slug}/carrito`)}
              className="relative p-2 rounded-lg hover:bg-slate-800 transition-colors"
              title="Carrito"
            >
              <ShoppingCart size={20} />
              {itemCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-amber-500 text-white text-xs font-bold rounded-full h-5 w-5 flex items-center justify-center">
                  {itemCount > 99 ? '99+' : itemCount}
                </span>
              )}
            </button>

            {/* User / Auth */}
            {isLoggedIn ? (
              <div className="relative">
                <button
                  onClick={() => setUserMenuOpen(!userMenuOpen)}
                  className="flex items-center gap-2 p-2 rounded-lg hover:bg-slate-800 transition-colors"
                >
                  <User size={20} />
                  <span className="hidden sm:block text-sm max-w-[120px] truncate">
                    {customer?.nombres || 'Mi cuenta'}
                  </span>
                </button>
                {userMenuOpen && (
                  <>
                    <div className="fixed inset-0 z-10" onClick={() => setUserMenuOpen(false)} />
                    <div className="absolute top-full right-0 mt-1 bg-white text-gray-900 rounded-lg shadow-xl border border-gray-200 py-1 z-20 min-w-[180px]">
                      <Link
                        to={`/tienda/${slug}/mis-pedidos`}
                        onClick={() => setUserMenuOpen(false)}
                        className="flex items-center gap-2 px-4 py-2.5 text-sm hover:bg-gray-50"
                      >
                        <Package size={16} />
                        Mis Pedidos
                      </Link>
                      <Link
                        to={`/tienda/${slug}/mi-perfil`}
                        onClick={() => setUserMenuOpen(false)}
                        className="flex items-center gap-2 px-4 py-2.5 text-sm hover:bg-gray-50"
                      >
                        <UserCog size={16} />
                        Mi Perfil
                      </Link>
                      <Link
                        to={`/tienda/${slug}/mis-devoluciones`}
                        onClick={() => setUserMenuOpen(false)}
                        className="flex items-center gap-2 px-4 py-2.5 text-sm hover:bg-gray-50"
                      >
                        <RotateCcw size={16} />
                        Mis Devoluciones
                      </Link>
                      <button
                        onClick={handleLogout}
                        className="flex items-center gap-2 w-full text-left px-4 py-2.5 text-sm text-red-600 hover:bg-red-50"
                      >
                        <LogOut size={16} />
                        Cerrar Sesión
                      </button>
                    </div>
                  </>
                )}
              </div>
            ) : (
              <Link
                to={`/tienda/${slug}/login`}
                className="px-4 py-1.5 rounded-lg text-sm font-medium transition-colors text-white"
                style={{ backgroundColor: 'var(--brand-secondary, #f59e0b)' }}
              >
                Ingresar
              </Link>
            )}

            {/* Mobile menu toggle */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 rounded-lg hover:bg-slate-800 transition-colors"
            >
              {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-white/10" style={{ backgroundColor: 'var(--brand-primary, #0f172a)' }}>
          <div className="px-4 py-3 space-y-1">
            {/* Sede selector (mobile) */}
            {sedes.length > 1 && (
              <div className="pb-2 mb-2 border-b border-slate-700">
                <p className="text-xs text-gray-400 mb-2">Sede</p>
                {sedes.map((sede) => (
                  <button
                    key={sede.id}
                    onClick={() => {
                      onSedeChange(sede);
                      setMobileMenuOpen(false);
                    }}
                    className={`w-full text-left px-3 py-2 rounded-lg text-sm transition-colors ${
                      selectedSede?.id === sede.id
                        ? 'bg-amber-500/20 text-amber-400'
                        : 'text-gray-300 hover:bg-slate-800'
                    }`}
                  >
                    {sede.nombre}
                  </button>
                ))}
              </div>
            )}
            <Link
              to={`/tienda/${slug}`}
              onClick={() => setMobileMenuOpen(false)}
              className="block px-3 py-2 rounded-lg text-sm text-gray-300 hover:bg-slate-800"
            >
              Inicio
            </Link>
            <Link
              to={`/tienda/${slug}/catalogo`}
              onClick={() => setMobileMenuOpen(false)}
              className="block px-3 py-2 rounded-lg text-sm text-gray-300 hover:bg-slate-800"
            >
              Catálogo
            </Link>
          </div>
        </div>
      )}
    </header>
  );
};
