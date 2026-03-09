import { Routes, Route, Navigate } from 'react-router-dom';
import { ShieldAlert } from 'lucide-react';
import { AdminLayout } from '../layouts/AdminLayout';
import { Login } from '../pages/Login';
import { Dashboard } from '../pages/Dashboard';
import { Usuarios } from '../pages/Usuarios';
import { ComprasPage } from '../pages/Compras';
import { Ventas } from '../pages/Ventas';
import { Pedidos } from '../pages/Pedidos';
import { Devoluciones } from '../pages/Devoluciones';
import { Facturacion } from '../pages/Facturacion';
import { Reportes } from '../pages/Reportes';
import { MiPerfil } from '../pages/MiPerfil';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useAuthStore } from '@/stores/authStore';

/* ─── Catálogo sub-páginas ─── */
import { ProductosTab } from '../catalogo/components/tabs/ProductosTab';
import { ClasificacionesPage } from '../catalogo/components/tabs/ClasificacionesPage';
import { CombosTab } from '../catalogo/components/tabs/CombosTab';
import { PromocionesTab } from '../catalogo/components/tabs/PromocionesTab';
import { CatalogoPage } from '../pages/Catalogo';

/* ─── Inventario sub-páginas ─── */
import { InventarioPage } from '../pages/Inventario';
import { AlmacenesTab } from '../inventario/components/tabs/AlmacenesTab';
import { LotesTab } from '../inventario/components/tabs/LotesTab';
import { TransferenciasTab } from '../inventario/components/tabs/TransferenciasTab';
import { ReportesInventarioPage } from '../inventario/components/tabs/ReportesInventarioPage';


/* ─── Compras sub-páginas ─── */
import { ProveedoresTab } from '../compras/components/tabs/ProveedoresTab';
import { ProductosProveedorTab } from '../compras/components/tabs/ProductosProveedorTab';
import { OrdenesCompraTab } from '../compras/components/tabs/OrdenesCompraTab';
import { RecepcionTab } from '../compras/components/tabs/RecepcionTab';

/* ─── Ventas sub-páginas ─── */
import { POS } from '../ventas/pages/POS';
import { Cajas } from '../ventas/pages/Cajas';
import { MovimientosCajaPage } from '../ventas/pages/MovimientosCajaPage';
import { HistorialVentas } from '../ventas/pages/HistorialVentas';
import { GastosPage } from '../ventas/pages/GastosPage';
import { MesaFloorPage } from '../ventas/pages/MesaFloorPage';

/* ─── Facturación sub-páginas ─── */
import { ComprobantesTab } from '../facturacion/pages/ComprobantesTab';
import { SeriesTab } from '../facturacion/pages/SeriesTab';
import { PseTab } from '../facturacion/pages/PseTab';

/* ─── Configuración sub-páginas ─── */
import { ConfiguracionPage } from '../pages/Configuracion';
import { NegocioYSedesPage } from '../configuracion/components/tabs/NegocioYSedesPage';
import { OperacionesPage } from '../configuracion/components/tabs/OperacionesPage';
import { CategoriasGastosPage } from '../configuracion/components/tabs/CategoriasGastosPage';


const ProtectedRoute = ({ children }) => {
  const { token, negocio, esProgramador } = useAdminAuthStore();
  const esProgramadorSuperadmin = useAuthStore((s) => s.esProgramador());

  if (!token) {
    if (esProgramadorSuperadmin) {
      return <Navigate to="/superadmin/programador/negocios" replace />;
    }
    return <Navigate to="/admin/login" replace />;
  }
  if (!esProgramador && negocio && negocio.estado !== 'activo') {
    return <Navigate to="/admin/login" replace />;
  }
  return children;
};

/**
 * Redirige al primer módulo accesible según los permisos del usuario.
 * Usado como índice de /admin para sustituir el redirect fijo a dashboard.
 */
const ORDERED_ROUTES = [
  { permiso: 'm.dashboard',                       to: '/admin/dashboard' },
  { permiso: 'm.ventas.pos',                       to: '/admin/ventas/pos' },
  { permiso: 'm.ventas.cajas',                     to: '/admin/ventas/cajas' },
  { permiso: 'm.ventas.movimientos',               to: '/admin/ventas/movimientos' },
  { permiso: 'm.ventas.historial',                 to: '/admin/ventas/historial' },
  { permiso: 'm.ventas.gastos',                    to: '/admin/ventas/gastos' },
  { permiso: 'm.ventas.mesas',                     to: '/admin/ventas/mesas' },
  { permiso: 'm.catalogo.productos',               to: '/admin/catalogo/productos' },
  { permiso: 'm.catalogo.clasificaciones',         to: '/admin/catalogo/clasificaciones' },
  { permiso: 'm.catalogo.combos',                  to: '/admin/catalogo/combos' },
  { permiso: 'm.catalogo.promociones',             to: '/admin/catalogo/promociones' },
  { permiso: 'm.inventario.almacenes',             to: '/admin/inventario/almacenes' },
  { permiso: 'm.inventario.lotes',                 to: '/admin/inventario/lotes' },
  { permiso: 'm.inventario.transferencias',        to: '/admin/inventario/transferencias' },
  { permiso: 'm.inventario.reportes',              to: '/admin/inventario/reportes' },
  { permiso: 'm.compras.proveedores',              to: '/admin/compras/proveedores' },
  { permiso: 'm.compras.productos',                to: '/admin/compras/productos-proveedor' },
  { permiso: 'm.compras.ordenes',                  to: '/admin/compras/ordenes' },
  { permiso: 'm.compras.recepcion',                to: '/admin/compras/recepcion' },
  { permiso: 'm.pedidos',                          to: '/admin/pedidos' },
  { permiso: 'm.devoluciones',                     to: '/admin/devoluciones' },
  { permiso: 'm.facturacion.comprobantes',         to: '/admin/facturacion/comprobantes' },
  { permiso: 'm.facturacion.series',               to: '/admin/facturacion/series' },
  { permiso: 'm.facturacion.pse',                  to: '/admin/facturacion/pse' },
  { permiso: 'm.reportes',                         to: '/admin/reportes' },
  { permiso: 'm.usuarios',                         to: '/admin/usuarios' },
  { permiso: 'm.configuracion.negocio',            to: '/admin/configuracion/negocio' },
  { permiso: 'm.configuracion.operaciones',        to: '/admin/configuracion/operaciones' },
  { permiso: 'm.configuracion.categorias-gastos',  to: '/admin/configuracion/categorias-gastos' },
];

const SmartRedirect = () => {
  const { hasPermiso, permisos } = useAdminAuthStore();
  if (!permisos || permisos.length === 0) return <Navigate to="/admin/login" replace />;
  for (const route of ORDERED_ROUTES) {
    if (hasPermiso(route.permiso)) return <Navigate to={route.to} replace />;
  }
  return (
    <div className="flex flex-col items-center justify-center h-full gap-3 text-gray-400 py-20">
      <ShieldAlert size={48} className="text-gray-300" />
      <p className="text-base font-medium text-gray-600">Sin módulos asignados</p>
      <p className="text-sm">Tu cuenta no tiene módulos habilitados. Contacta al administrador.</p>
    </div>
  );
};

/** Protege una ruta verificando el permiso. Si no tiene acceso, redirige al SmartRedirect. */
const PermissionRoute = ({ permiso, children }) => {
  const { hasPermiso } = useAdminAuthStore();
  if (!hasPermiso(permiso)) return <Navigate to="/admin" replace />;
  return children;
};

/** Redirige al primer sub-módulo accesible dentro de un grupo (ej: /admin/ventas → pos o cajas). */
const SmartModuleRedirect = ({ options }) => {
  const { hasPermiso } = useAdminAuthStore();
  for (const opt of options) {
    if (hasPermiso(opt.permiso)) return <Navigate to={opt.to} replace />;
  }
  return <Navigate to="/admin" replace />;
};

export const AdminRoutes = () => {
  return (
    <Routes>
      <Route path="login" element={<Login />} />
      <Route
        element={
          <ProtectedRoute>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<SmartRedirect />} />
        <Route path="dashboard" element={
          <PermissionRoute permiso="m.dashboard"><Dashboard /></PermissionRoute>
        } />
        <Route path="configuracion" element={<ConfiguracionPage />}>
          <Route index element={
            <SmartModuleRedirect options={[
              { permiso: 'm.configuracion.negocio', to: 'negocio' },
              { permiso: 'm.configuracion.operaciones', to: 'operaciones' },
              { permiso: 'm.configuracion.categorias-gastos', to: 'categorias-gastos' },
            ]} />
          } />
          <Route path="negocio" element={
            <PermissionRoute permiso="m.configuracion.negocio"><NegocioYSedesPage /></PermissionRoute>
          } />
          <Route path="operaciones" element={
            <PermissionRoute permiso="m.configuracion.operaciones"><OperacionesPage /></PermissionRoute>
          } />
          <Route path="categorias-gastos" element={
            <PermissionRoute permiso="m.configuracion.categorias-gastos"><CategoriasGastosPage /></PermissionRoute>
          } />
        </Route>
        <Route path="usuarios" element={
          <PermissionRoute permiso="m.usuarios"><Usuarios /></PermissionRoute>
        } />

        {/* Catálogo: sub-rutas independientes */}
        <Route path="catalogo" element={<CatalogoPage />}>
          <Route index element={
            <SmartModuleRedirect options={[
              { permiso: 'm.catalogo.productos', to: 'productos' },
              { permiso: 'm.catalogo.clasificaciones', to: 'clasificaciones' },
              { permiso: 'm.catalogo.combos', to: 'combos' },
              { permiso: 'm.catalogo.promociones', to: 'promociones' },
            ]} />
          } />
          <Route path="productos" element={
            <PermissionRoute permiso="m.catalogo.productos"><ProductosTab /></PermissionRoute>
          } />
          <Route path="clasificaciones" element={
            <PermissionRoute permiso="m.catalogo.clasificaciones"><ClasificacionesPage /></PermissionRoute>
          } />
          <Route path="combos" element={
            <PermissionRoute permiso="m.catalogo.combos"><CombosTab /></PermissionRoute>
          } />
          <Route path="promociones" element={
            <PermissionRoute permiso="m.catalogo.promociones"><PromocionesTab /></PermissionRoute>
          } />
        </Route>

        <Route path="inventario" element={<InventarioPage />}>
          <Route index element={
            <SmartModuleRedirect options={[
              { permiso: 'm.inventario.almacenes', to: 'almacenes' },
              { permiso: 'm.inventario.lotes', to: 'lotes' },
              { permiso: 'm.inventario.transferencias', to: 'transferencias' },
              { permiso: 'm.inventario.reportes', to: 'reportes' },
            ]} />
          } />
          <Route path="almacenes" element={
            <PermissionRoute permiso="m.inventario.almacenes"><AlmacenesTab /></PermissionRoute>
          } />
          <Route path="lotes" element={
            <PermissionRoute permiso="m.inventario.lotes"><LotesTab /></PermissionRoute>
          } />
          <Route path="transferencias" element={
            <PermissionRoute permiso="m.inventario.transferencias"><TransferenciasTab /></PermissionRoute>
          } />
          <Route path="reportes" element={
            <PermissionRoute permiso="m.inventario.reportes"><ReportesInventarioPage /></PermissionRoute>
          } />
        </Route>
        <Route path="compras" element={<ComprasPage />}>
          <Route index element={
            <SmartModuleRedirect options={[
              { permiso: 'm.compras.proveedores', to: 'proveedores' },
              { permiso: 'm.compras.productos', to: 'productos-proveedor' },
              { permiso: 'm.compras.ordenes', to: 'ordenes' },
              { permiso: 'm.compras.recepcion', to: 'recepcion' },
            ]} />
          } />
          <Route path="proveedores" element={
            <PermissionRoute permiso="m.compras.proveedores"><ProveedoresTab /></PermissionRoute>
          } />
          <Route path="productos-proveedor" element={
            <PermissionRoute permiso="m.compras.productos"><ProductosProveedorTab /></PermissionRoute>
          } />
          <Route path="ordenes" element={
            <PermissionRoute permiso="m.compras.ordenes"><OrdenesCompraTab /></PermissionRoute>
          } />
          <Route path="recepcion" element={
            <PermissionRoute permiso="m.compras.recepcion"><RecepcionTab /></PermissionRoute>
          } />
        </Route>
        <Route path="ventas" element={<Ventas />}>
          <Route index element={
            <SmartModuleRedirect options={[
              { permiso: 'm.ventas.pos', to: 'pos' },
              { permiso: 'm.ventas.cajas', to: 'cajas' },
              { permiso: 'm.ventas.movimientos', to: 'movimientos' },
              { permiso: 'm.ventas.historial', to: 'historial' },
              { permiso: 'm.ventas.gastos', to: 'gastos' },
              { permiso: 'm.ventas.mesas', to: 'mesas' },
            ]} />
          } />
          <Route path="pos" element={
            <PermissionRoute permiso="m.ventas.pos"><POS /></PermissionRoute>
          } />
          <Route path="cajas" element={
            <PermissionRoute permiso="m.ventas.cajas"><Cajas /></PermissionRoute>
          } />
          <Route path="movimientos" element={
            <PermissionRoute permiso="m.ventas.movimientos"><MovimientosCajaPage /></PermissionRoute>
          } />
          <Route path="historial" element={
            <PermissionRoute permiso="m.ventas.historial"><HistorialVentas /></PermissionRoute>
          } />
          <Route path="gastos" element={
            <PermissionRoute permiso="m.ventas.gastos"><GastosPage /></PermissionRoute>
          } />
          <Route path="mesas" element={
            <PermissionRoute permiso="m.ventas.mesas"><MesaFloorPage /></PermissionRoute>
          } />
        </Route>
        <Route path="pedidos" element={
          <PermissionRoute permiso="m.pedidos"><Pedidos /></PermissionRoute>
        } />
        <Route path="devoluciones" element={
          <PermissionRoute permiso="m.devoluciones"><Devoluciones /></PermissionRoute>
        } />
        <Route path="facturacion" element={<Facturacion />}>
          <Route index element={
            <SmartModuleRedirect options={[
              { permiso: 'm.facturacion.comprobantes', to: 'comprobantes' },
              { permiso: 'm.facturacion.series', to: 'series' },
              { permiso: 'm.facturacion.pse', to: 'pse' },
            ]} />
          } />
          <Route path="comprobantes" element={
            <PermissionRoute permiso="m.facturacion.comprobantes"><ComprobantesTab /></PermissionRoute>
          } />
          <Route path="series" element={
            <PermissionRoute permiso="m.facturacion.series"><SeriesTab /></PermissionRoute>
          } />
          <Route path="pse" element={
            <PermissionRoute permiso="m.facturacion.pse"><PseTab /></PermissionRoute>
          } />
        </Route>
        <Route path="reportes" element={
          <PermissionRoute permiso="m.reportes"><Reportes /></PermissionRoute>
        } />
        <Route path="perfil" element={<MiPerfil />} />
      </Route>
    </Routes>
  );
};
