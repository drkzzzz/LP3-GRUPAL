import { Routes, Route, Navigate } from 'react-router-dom';
import { AdminLayout } from '../layouts/AdminLayout';
import { Login } from '../pages/Login';
import { Dashboard } from '../pages/Dashboard';
import { Usuarios } from '../pages/Usuarios';
import { ComprasPage } from '../pages/Compras';
import { Ventas } from '../pages/Ventas';
import { Pedidos } from '../pages/Pedidos';
import { Facturacion } from '../pages/Facturacion';
import { Reportes } from '../pages/Reportes';
import { MiPerfil } from '../pages/MiPerfil';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

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

/* ─── Facturación sub-páginas ─── */
import { ComprobantesTab } from '../facturacion/pages/ComprobantesTab';
import { SeriesTab } from '../facturacion/pages/SeriesTab';
import { MetodosPagoTab } from '../facturacion/pages/MetodosPagoTab';

/* ─── Configuración sub-páginas ─── */
import { ConfiguracionPage } from '../pages/Configuracion';
import { NegocioYSedesPage } from '../configuracion/components/tabs/NegocioYSedesPage';
import { OperacionesPage } from '../configuracion/components/tabs/OperacionesPage';


const ProtectedRoute = ({ children }) => {
  const { token } = useAdminAuthStore();
  if (!token) {
    return <Navigate to="/admin/login" replace />;
  }
  return children;
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
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="configuracion" element={<ConfiguracionPage />}>
          <Route index element={<Navigate to="negocio" replace />} />
          <Route path="negocio" element={<NegocioYSedesPage />} />
          <Route path="operaciones" element={<OperacionesPage />} />
        </Route>
        <Route path="usuarios" element={<Usuarios />} />

        {/* Catálogo: sub-rutas independientes */}
        <Route path="catalogo" element={<CatalogoPage />}>
          <Route index element={<Navigate to="productos" replace />} />
          <Route path="productos" element={<ProductosTab />} />
          <Route path="clasificaciones" element={<ClasificacionesPage />} />
          <Route path="combos" element={<CombosTab />} />
          <Route path="promociones" element={<PromocionesTab />} />
        </Route>

        <Route path="inventario" element={<InventarioPage />}>
          <Route index element={<Navigate to="almacenes" replace />} />
          <Route path="almacenes" element={<AlmacenesTab />} />
          <Route path="lotes" element={<LotesTab />} />
          <Route path="transferencias" element={<TransferenciasTab />} />
          <Route path="reportes" element={<ReportesInventarioPage />} />
        </Route>
        <Route path="compras" element={<ComprasPage />}>
          <Route index element={<Navigate to="proveedores" replace />} />
          <Route path="proveedores" element={<ProveedoresTab />} />
          <Route path="productos-proveedor" element={<ProductosProveedorTab />} />
          <Route path="ordenes" element={<OrdenesCompraTab />} />
          <Route path="recepcion" element={<RecepcionTab />} />
        </Route>
        <Route path="ventas" element={<Ventas />}>
          <Route index element={<Navigate to="pos" replace />} />
          <Route path="pos" element={<POS />} />
          <Route path="cajas" element={<Cajas />} />
          <Route path="movimientos" element={<MovimientosCajaPage />} />
          <Route path="historial" element={<HistorialVentas />} />
        </Route>
        <Route path="pedidos" element={<Pedidos />} />
        <Route path="facturacion" element={<Facturacion />}>
          <Route index element={<Navigate to="comprobantes" replace />} />
          <Route path="comprobantes" element={<ComprobantesTab />} />
          <Route path="series" element={<SeriesTab />} />
          <Route path="metodos-pago" element={<MetodosPagoTab />} />
        </Route>
        <Route path="reportes" element={<Reportes />} />
        <Route path="perfil" element={<MiPerfil />} />
      </Route>
    </Routes>
  );
};
