import { Routes, Route, Navigate } from 'react-router-dom';
import { SuperAdminLayout } from '../layouts/SuperAdminLayout';
import { Login } from '../pages/Login';
import { Dashboard } from '../pages/Dashboard';
import { Negocios } from '../pages/Negocios';
import { Planes } from '../pages/Planes';
import { Facturacion } from '../pages/Facturacion';
import { ConfiguracionGlobal } from '../pages/ConfiguracionGlobal';
import { AuditoriaLogs } from '../pages/AuditoriaLogs';
import { Reportes } from '../pages/Reportes';
import { useAuthStore } from '@/stores/authStore';

const ProtectedRoute = ({ children }) => {
  const { token } = useAuthStore();
  if (!token) {
    return <Navigate to="/superadmin/login" replace />;
  }
  return children;
};

export const SuperAdminRoutes = () => {
  return (
    <Routes>
      <Route path="login" element={<Login />} />
      <Route
        element={
          <ProtectedRoute>
            <SuperAdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="negocios" element={<Negocios />} />
        <Route path="planes" element={<Planes />} />
        <Route path="facturacion" element={<Facturacion />} />
        <Route path="reportes" element={<Reportes />} />
        <Route path="configuracion" element={<ConfiguracionGlobal />} />
        <Route path="auditoria" element={<AuditoriaLogs />} />
      </Route>
    </Routes>
  );
};
