import { Routes, Route, Navigate } from 'react-router-dom';
import { AdminLayout } from '../layouts/AdminLayout';
import { Login } from '../pages/Login';
import { Dashboard } from '../pages/Dashboard';
import { Configuracion } from '../pages/Configuracion';
import { Usuarios } from '../pages/Usuarios';
import { Catalogo } from '../pages/Catalogo';
import { Inventario } from '../pages/Inventario';
import { Compras } from '../pages/Compras';
import { Ventas } from '../pages/Ventas';
import { Pedidos } from '../pages/Pedidos';
import { Facturacion } from '../pages/Facturacion';
import { Reportes } from '../pages/Reportes';
import { MiPerfil } from '../pages/MiPerfil';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

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
        <Route path="configuracion" element={<Configuracion />} />
        <Route path="usuarios" element={<Usuarios />} />
        <Route path="catalogo" element={<Catalogo />} />
        <Route path="inventario" element={<Inventario />} />
        <Route path="compras" element={<Compras />} />
        <Route path="ventas" element={<Ventas />} />
        <Route path="pedidos" element={<Pedidos />} />
        <Route path="facturacion" element={<Facturacion />} />
        <Route path="reportes" element={<Reportes />} />
        <Route path="perfil" element={<MiPerfil />} />
      </Route>
    </Routes>
  );
};
