import { useState } from 'react';
import { Outlet, NavLink, useNavigate, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Settings,
  Users,
  Package,
  Warehouse,
  ShoppingCart,
  CreditCard,
  ClipboardList,
  BarChart3,
  LogOut,
  Menu,
  Wine,
  ChevronLeft,
  ChevronDown,
  Truck,
  UserCircle,
  Layers,
  Gift,
  Tag,
  Boxes,
  ArrowRightLeft,
  SlidersHorizontal,
  History,
  AlertTriangle,
  Building2,
} from 'lucide-react';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ─── Sub-items del menú Configuración ─── */
const CONFIGURACION_SUBITEMS = [
  { to: '/admin/configuracion/negocio', label: 'Negocio y Sedes', icon: Building2 },
  { to: '/admin/configuracion/operaciones', label: 'Operaciones', icon: SlidersHorizontal },
];

/* ─── Sub-items del menú Catálogo ─── */
const CATALOGO_SUBITEMS = [
  { to: '/admin/catalogo/productos', label: 'Productos', icon: Package },
  { to: '/admin/catalogo/clasificaciones', label: 'Clasificaciones', icon: Layers },
  { to: '/admin/catalogo/combos', label: 'Combos', icon: Gift },
  { to: '/admin/catalogo/promociones', label: 'Promociones', icon: Tag },
];

/* ─── Sub-items del menú Inventario ─── */
const INVENTARIO_SUBITEMS = [
  { to: '/admin/inventario/almacenes', label: 'Almacenes', icon: Warehouse },
  { to: '/admin/inventario/lotes', label: 'Lotes', icon: Boxes },
  { to: '/admin/inventario/ajustes', label: 'Ajustes', icon: SlidersHorizontal },
  { to: '/admin/inventario/transferencias', label: 'Transferencias', icon: ArrowRightLeft },
  { to: '/admin/inventario/reportes', label: 'Reportes', icon: BarChart3 },
];

const NAV_ITEMS = [
  {
    to: '/admin/dashboard',
    icon: LayoutDashboard,
    label: 'Dashboard',
  },
  {
    key: 'configuracion',
    icon: Settings,
    label: 'Configuración',
    children: CONFIGURACION_SUBITEMS,
  },
  {
    to: '/admin/usuarios',
    icon: Users,
    label: 'Usuarios y Clientes',
  },
  {
    /* Catálogo es ahora un grupo colapsable, no un NavLink directo */
    key: 'catalogo',
    icon: Package,
    label: 'Catálogo',
    children: CATALOGO_SUBITEMS,
  },
  {
    key: 'inventario',
    icon: Warehouse,
    label: 'Inventario',
    children: INVENTARIO_SUBITEMS,
  },
  {
    to: '/admin/compras',
    icon: Truck,
    label: 'Proveedores y Compras',
  },
  {
    to: '/admin/ventas',
    icon: CreditCard,
    label: 'Ventas / POS',
  },
  {
    to: '/admin/pedidos',
    icon: ShoppingCart,
    label: 'Pedidos',
  },
  {
    to: '/admin/facturacion',
    icon: ClipboardList,
    label: 'Facturación',
  },
  {
    to: '/admin/reportes',
    icon: BarChart3,
    label: 'Reportes',
  },
];

export const AdminLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [openMenus, setOpenMenus] = useState({});
  const navigate = useNavigate();
  const location = useLocation();
  const { user, negocio, logout } = useAdminAuthStore();

  /* Toggle para grupos colapsables */
  const toggleMenu = (key) => setOpenMenus((prev) => ({ ...prev, [key]: !prev[key] }));

  /* Auto-expandir el grupo si la ruta actual coincide */
  const isGroupActive = (key) => location.pathname.startsWith(`/admin/${key}`);
  const isGroupOpen = (key) => openMenus[key] || isGroupActive(key);

  const handleLogout = () => {
    logout();
    navigate('/admin/login');
  };

  const SidebarContent = () => (
    <div className="flex flex-col h-full">
      {/* Logo */}
      <div className="flex items-center gap-3 px-4 py-5 border-b border-gray-700">
        <div className="p-2 bg-green-600 rounded-lg shrink-0">
          <Wine size={20} className="text-white" />
        </div>
        {sidebarOpen && (
          <div className="overflow-hidden">
            <h1 className="text-lg font-bold text-white whitespace-nowrap">DrinkGo</h1>
            <p className="text-xs text-gray-400 whitespace-nowrap truncate max-w-[140px]">
              {negocio?.nombre || negocio?.razonSocial || 'Panel Admin'}
            </p>
          </div>
        )}
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        {NAV_ITEMS.map((item) =>
          item.children ? (
            /* ─── Grupo colapsable (Catálogo) ─── */
            <div key={item.key}>
              <button
                onClick={() => toggleMenu(item.key)}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isGroupActive(item.key)
                    ? 'bg-green-600/20 text-green-400'
                    : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                }`}
              >
                <item.icon size={20} className="shrink-0" />
                {sidebarOpen && (
                  <>
                    <span className="whitespace-nowrap flex-1 text-left">{item.label}</span>
                    <ChevronDown
                      size={16}
                      className={`shrink-0 transition-transform duration-200 ${
                        isGroupOpen(item.key) ? 'rotate-180' : ''
                      }`}
                    />
                  </>
                )}
              </button>
              {sidebarOpen && (
                <div
                  className={`ml-3 pl-4 border-l border-gray-700 space-y-0.5 overflow-hidden transition-all duration-300 ease-in-out ${
                    isGroupOpen(item.key)
                      ? 'max-h-96 opacity-100 mt-1'
                      : 'max-h-0 opacity-0 mt-0'
                  }`}
                >
                  {item.children.map((sub) => (
                    <NavLink
                      key={sub.to}
                      to={sub.to}
                      onClick={() => setMobileOpen(false)}
                      className={({ isActive }) =>
                        `flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-colors ${
                          isActive
                            ? 'bg-green-600 text-white'
                            : 'text-gray-400 hover:bg-gray-700 hover:text-white'
                        }`
                      }
                    >
                      <sub.icon size={16} className="shrink-0" />
                      <span className="whitespace-nowrap">{sub.label}</span>
                    </NavLink>
                  ))}
                </div>
              )}
            </div>
          ) : (
            /* ─── Item normal ─── */
            <NavLink
              key={item.to}
              to={item.to}
              onClick={() => setMobileOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-green-600 text-white'
                    : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                }`
              }
            >
              <item.icon size={20} className="shrink-0" />
              {sidebarOpen && <span className="whitespace-nowrap">{item.label}</span>}
            </NavLink>
          ),
        )}
      </nav>

      {/* User Section */}
      <div className="border-t border-gray-700 p-4">
        {sidebarOpen ? (
          <div className="flex items-center gap-3">
            <button
              onClick={() => { navigate('/admin/perfil'); setMobileOpen(false); }}
              className="flex items-center gap-3 flex-1 min-w-0 rounded-lg hover:bg-gray-800 p-1 transition-colors"
              title="Mi Perfil"
            >
              <div className="w-8 h-8 bg-green-600 rounded-full flex items-center justify-center shrink-0">
                <span className="text-white text-sm font-medium">
                  {user?.nombre?.[0]?.toUpperCase() || user?.nombres?.[0]?.toUpperCase() || 'A'}
                </span>
              </div>
              <div className="flex-1 overflow-hidden text-left">
                <p className="text-sm font-medium text-white truncate">
                  {user?.nombre || user?.nombres || 'Usuario'}
                </p>
                <p className="text-xs text-gray-400 truncate">
                  {user?.email || ''}
                </p>
              </div>
            </button>
            <button
              onClick={handleLogout}
              className="p-1.5 text-gray-400 hover:text-white hover:bg-gray-700 rounded-lg transition-colors shrink-0"
              title="Cerrar sesión"
            >
              <LogOut size={18} />
            </button>
          </div>
        ) : (
          <div className="flex flex-col items-center gap-2">
            <button
              onClick={() => { navigate('/admin/perfil'); }}
              className="w-8 h-8 bg-green-600 rounded-full flex items-center justify-center hover:bg-green-700 transition-colors"
              title="Mi Perfil"
            >
              <span className="text-white text-sm font-medium">
                {user?.nombre?.[0]?.toUpperCase() || 'A'}
              </span>
            </button>
            <button
              onClick={handleLogout}
              className="p-1.5 text-gray-400 hover:text-white hover:bg-gray-700 rounded-lg transition-colors"
              title="Cerrar sesión"
            >
              <LogOut size={18} />
            </button>
          </div>
        )}
      </div>
    </div>
  );

  return (
    <div className="flex h-screen bg-gray-100 overflow-hidden">
      {/* Desktop Sidebar */}
      <aside
        className={`hidden lg:flex flex-col bg-gray-800 transition-all duration-300 shrink-0 ${
          sidebarOpen ? 'w-64' : 'w-16'
        }`}
      >
        <SidebarContent />
      </aside>

      {/* Mobile Sidebar Overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* Mobile Sidebar */}
      <aside
        className={`fixed top-0 left-0 h-full w-64 bg-gray-800 z-50 flex flex-col transition-transform duration-300 lg:hidden ${
          mobileOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <SidebarContent />
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Topbar */}
        <header className="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-3 shrink-0">
          {/* Mobile menu button */}
          <button
            onClick={() => setMobileOpen(true)}
            className="lg:hidden p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg"
          >
            <Menu size={20} />
          </button>

          {/* Desktop collapse button */}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="hidden lg:flex p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
            title={sidebarOpen ? 'Colapsar menú' : 'Expandir menú'}
          >
            <ChevronLeft
              size={20}
              className={`transition-transform duration-300 ${sidebarOpen ? '' : 'rotate-180'}`}
            />
          </button>

          <div className="flex-1" />

          {/* Negocio badge */}
          {negocio && (
            <span className="hidden sm:inline text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-full">
              {negocio?.nombre || negocio?.razonSocial || ''}
            </span>
          )}

          {/* User icon */}
          <button
            onClick={() => navigate('/admin/perfil')}
            className="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg"
            title="Mi perfil"
          >
            <UserCircle size={20} />
          </button>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
