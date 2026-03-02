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
  FileText,
  Hash,
  Monitor,
  Wallet,
  Receipt,
  Zap,
  TrendingDown,
  MapPin,
} from 'lucide-react';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ─── Sub-items del menú Configuración ─── */
const CONFIGURACION_SUBITEMS = [
  { to: '/admin/configuracion/negocio', label: 'Negocio y Sedes', icon: Building2, permiso: 'm.configuracion.negocio' },
  { to: '/admin/configuracion/operaciones', label: 'Operaciones', icon: SlidersHorizontal, permiso: 'm.configuracion.operaciones' },
];

/* ─── Sub-items del menú Catálogo ─── */
const CATALOGO_SUBITEMS = [
  { to: '/admin/catalogo/productos', label: 'Productos', icon: Package, permiso: 'm.catalogo.productos' },
  { to: '/admin/catalogo/clasificaciones', label: 'Clasificaciones', icon: Layers, permiso: 'm.catalogo.clasificaciones' },
  { to: '/admin/catalogo/combos', label: 'Combos', icon: Gift, permiso: 'm.catalogo.combos' },
  { to: '/admin/catalogo/promociones', label: 'Promociones', icon: Tag, permiso: 'm.catalogo.promociones' },
];

/* ─── Sub-items del menú Inventario ─── */
const INVENTARIO_SUBITEMS = [
  { to: '/admin/inventario/almacenes', label: 'Almacenes', icon: Warehouse, permiso: 'm.inventario.almacenes' },
  { to: '/admin/inventario/lotes', label: 'Lotes', icon: Boxes, permiso: 'm.inventario.lotes' },
  { to: '/admin/inventario/transferencias', label: 'Transferencias', icon: ArrowRightLeft, permiso: 'm.inventario.transferencias' },
  { to: '/admin/inventario/reportes', label: 'Reportes', icon: BarChart3, permiso: 'm.inventario.reportes' },
];

/* ─── Sub-items del menú Compras ─── */
const COMPRAS_SUBITEMS = [
  { to: '/admin/compras/proveedores', label: 'Proveedores', icon: Users, permiso: 'm.compras.proveedores' },
  { to: '/admin/compras/productos-proveedor', label: 'Productos Proveedor', icon: Package, permiso: 'm.compras.productos' },
  { to: '/admin/compras/ordenes', label: 'Órdenes de Compra', icon: ClipboardList, permiso: 'm.compras.ordenes' },
  { to: '/admin/compras/recepcion', label: 'Recepción', icon: ShoppingCart, permiso: 'm.compras.recepcion' },
];

/* ─── Sub-items del menú Ventas / POS ─── */
const VENTAS_SUBITEMS = [
  { to: '/admin/ventas/pos', label: 'Punto de Venta', icon: Monitor, permiso: 'm.ventas.pos' },
  { to: '/admin/ventas/cajas', label: 'Caja', icon: Wallet, permiso: 'm.ventas.cajas' },
  { to: '/admin/ventas/movimientos', label: 'Movimientos de Caja', icon: Receipt, permiso: 'm.ventas.movimientos' },
  { to: '/admin/ventas/historial', label: 'Historial', icon: History, permiso: 'm.ventas.historial' },
  { to: '/admin/ventas/gastos', label: 'Gastos', icon: TrendingDown, permiso: 'm.ventas.gastos' },
];

/* ─── Sub-items del menú Facturación ─── */
const FACTURACION_SUBITEMS = [
  { to: '/admin/facturacion/comprobantes', label: 'Comprobantes', icon: FileText, permiso: 'm.facturacion.comprobantes' },
  { to: '/admin/facturacion/series', label: 'Series', icon: Hash, permiso: 'm.facturacion.series' },
  { to: '/admin/facturacion/metodos-pago', label: 'Métodos de Pago', icon: CreditCard, permiso: 'm.facturacion.metodos' },
  { to: '/admin/facturacion/pse', label: 'PSE Electrónico', icon: Zap, permiso: 'm.facturacion.pse', requiresPse: true },
];

const NAV_ITEMS = [
  {
    to: '/admin/dashboard',
    icon: LayoutDashboard,
    label: 'Dashboard',
    permiso: 'm.dashboard',
  },
  {
    key: 'configuracion',
    icon: Settings,
    label: 'Configuración',
    permiso: 'm.configuracion',
    children: CONFIGURACION_SUBITEMS,
  },
  {
    to: '/admin/usuarios',
    icon: Users,
    label: 'Usuarios y Clientes',
    permiso: 'm.usuarios',
  },
  {
    key: 'catalogo',
    icon: Package,
    label: 'Catálogo',
    permiso: 'm.catalogo',
    children: CATALOGO_SUBITEMS,
  },
  {
    key: 'inventario',
    icon: Warehouse,
    label: 'Inventario',
    permiso: 'm.inventario',
    children: INVENTARIO_SUBITEMS,
  },
  {
    key: 'compras',
    icon: Truck,
    label: 'Proveedores y Compras',
    permiso: 'm.compras',
    children: COMPRAS_SUBITEMS,
  },
  {
    key: 'ventas',
    icon: CreditCard,
    label: 'Ventas / POS',
    permiso: 'm.ventas',
    children: VENTAS_SUBITEMS,
  },
  {
    to: '/admin/pedidos',
    icon: ShoppingCart,
    label: 'Pedidos',
    permiso: 'm.pedidos',
  },
  {
    key: 'facturacion',
    icon: ClipboardList,
    label: 'Facturación',
    permiso: 'm.facturacion',
    children: FACTURACION_SUBITEMS,
  },
  {
    to: '/admin/reportes',
    icon: BarChart3,
    label: 'Reportes',
    permiso: 'm.reportes',
  },
];

export const AdminLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [openMenus, setOpenMenus] = useState({});
  const navigate = useNavigate();
  const location = useLocation();
  const { user, negocio, sede, logout, hasPermiso, permisos } = useAdminAuthStore();


  /**
   * Filtra los items del menú según los permisos del usuario.
   * Si permisos está vacío el usuario no tiene acceso a ningún módulo.
   */
  const navItems = NAV_ITEMS.map((item) => {
      if (item.children) {
        const childrenFiltrados = item.children.filter((s) => hasPermiso(s.permiso));
        // Mostrar el padre solo si al menos un hijo es accesible O tiene permiso propio
        if (childrenFiltrados.length === 0 && !hasPermiso(item.permiso)) return null;
        return { ...item, children: childrenFiltrados };
      }
      return hasPermiso(item.permiso) ? item : null;
    }).filter(Boolean);

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
            {sede && (
              <p className="text-xs text-green-400 whitespace-nowrap truncate max-w-[140px] flex items-center gap-1 mt-0.5">
                <MapPin size={10} />
                {sede.nombre}
              </p>
            )}
          </div>
        )}
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        {navItems.map((item) =>
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
                  {item.children
                    .filter((sub) => !sub.requiresPse || negocio?.tienePse)
                    .map((sub) => (
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

          {/* Sede badge */}
          {sede && (
            <span className="hidden sm:inline-flex items-center gap-1.5 text-xs text-gray-600 bg-gray-100 px-2.5 py-1 rounded-full cursor-default" title={sede.nombre}>
              <MapPin size={12} className="text-green-600" />
              {sede.nombre}
              {sede.codigo && <span className="text-gray-400">· {sede.codigo}</span>}
            </span>
          )}

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
