import { useState } from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Store,
  CreditCard,
  Receipt,
  Settings,
  Shield,
  LogOut,
  Menu,
  X,
  Wine,
  ChevronLeft,
  BarChart3,
  UserCircle,
} from 'lucide-react';
import { useAuthStore } from '@/stores/authStore';

const NAV_ITEMS = [
  {
    to: '/superadmin/dashboard',
    icon: LayoutDashboard,
    label: 'Dashboard',
  },
  {
    to: '/superadmin/negocios',
    icon: Store,
    label: 'Negocios',
  },
  {
    to: '/superadmin/planes',
    icon: CreditCard,
    label: 'Planes',
  },
  {
    to: '/superadmin/facturacion',
    icon: Receipt,
    label: 'Facturación',
  },
  {
    to: '/superadmin/reportes',
    icon: BarChart3,
    label: 'Reportes',
  },
  {
    to: '/superadmin/configuracion',
    icon: Settings,
    label: 'Configuración',
  },
  {
    to: '/superadmin/auditoria',
    icon: Shield,
    label: 'Auditoría',
  },
];

export const SuperAdminLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [mobileOpen, setMobileOpen] = useState(false);
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate('/superadmin/login');
  };

  const SidebarContent = () => (
    <div className="flex flex-col h-full">
      {/* Logo */}
      <div className="flex items-center gap-3 px-4 py-5 border-b border-gray-700">
        <div className="p-2 bg-blue-600 rounded-lg shrink-0">
          <Wine size={20} className="text-white" />
        </div>
        {sidebarOpen && (
          <div className="overflow-hidden">
            <h1 className="text-lg font-bold text-white whitespace-nowrap">DrinkGo</h1>
            <p className="text-xs text-gray-400 whitespace-nowrap">Super Admin</p>
          </div>
        )}
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            onClick={() => setMobileOpen(false)}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                isActive
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-300 hover:bg-gray-700 hover:text-white'
              }`
            }
          >
            <item.icon size={20} className="shrink-0" />
            {sidebarOpen && <span className="whitespace-nowrap">{item.label}</span>}
          </NavLink>
        ))}
      </nav>

      {/* User Section */}
      <div className="border-t border-gray-700 p-4">
        {sidebarOpen ? (
          <div className="flex items-center gap-3">
            <button
              onClick={() => { navigate('/superadmin/perfil'); setMobileOpen(false); }}
              className="flex items-center gap-3 flex-1 min-w-0 rounded-lg hover:bg-gray-800 p-1 transition-colors"
              title="Mi Perfil"
            >
              <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center shrink-0">
                <span className="text-white text-sm font-medium">
                  {user?.nombre?.[0]?.toUpperCase() || user?.nombres?.[0]?.toUpperCase() || 'A'}
                </span>
              </div>
              <div className="flex-1 overflow-hidden text-left">
                <p className="text-sm font-medium text-white truncate">
                  {user?.nombre || user?.nombres || 'Admin'}
                </p>
                <p className="text-xs text-gray-400 truncate">
                  {user?.email || 'admin@drinkgo.com'}
                </p>
              </div>
            </button>
            <button
              onClick={handleLogout}
              title="Cerrar sesión"
              className="p-1.5 rounded-lg hover:bg-gray-700 text-gray-400 hover:text-white transition-colors"
            >
              <LogOut size={18} />
            </button>
          </div>
        ) : (
          <button
            onClick={handleLogout}
            title="Cerrar sesión"
            className="w-full flex justify-center p-2 rounded-lg hover:bg-gray-700 text-gray-400 hover:text-white transition-colors"
          >
            <LogOut size={18} />
          </button>
        )}
      </div>
    </div>
  );

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Desktop Sidebar */}
      <aside
        className={`hidden lg:flex flex-col bg-gray-900 transition-all duration-300 ${
          sidebarOpen ? 'w-64' : 'w-[72px]'
        }`}
      >
        <SidebarContent />
      </aside>

      {/* Mobile Sidebar Overlay */}
      {mobileOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* Mobile Sidebar */}
      <aside
        className={`fixed inset-y-0 left-0 z-50 w-64 bg-gray-900 transform transition-transform lg:hidden ${
          mobileOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <SidebarContent />
      </aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Top Bar */}
        <header className="bg-white border-b border-gray-200 px-4 lg:px-6 py-3 flex items-center gap-3">
          {/* Mobile menu button */}
          <button
            onClick={() => setMobileOpen(true)}
            className="lg:hidden p-1.5 rounded-lg hover:bg-gray-100"
          >
            <Menu size={20} />
          </button>

          {/* Desktop collapse button */}
          <button
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="hidden lg:block p-1.5 rounded-lg hover:bg-gray-100 text-gray-500"
            title={sidebarOpen ? 'Colapsar menú' : 'Expandir menú'}
          >
            <ChevronLeft
              size={20}
              className={`transition-transform ${sidebarOpen ? '' : 'rotate-180'}`}
            />
          </button>

          <div className="flex-1" />

          {/* User info (desktop) */}
          <button
            onClick={() => navigate('/superadmin/perfil')}
            className="hidden sm:flex items-center gap-2 text-sm text-gray-600 px-3 py-1.5 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
            title="Mi Perfil"
          >
            <UserCircle size={18} className="text-gray-500" />
            <span>{user?.nombre || user?.nombres || 'Admin'}</span>
            <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
              SuperAdmin
            </span>
          </button>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto p-4 lg:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
