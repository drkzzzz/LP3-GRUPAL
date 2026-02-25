import { Routes, Route, Navigate} from 'react-router-dom';
import SuperAdminLayout from '../layouts/SuperAdminLayout';
import Login from '../pages/Login';
import Dashboard from '../pages/Dashboard';
import Negocios from '../pages/Negocios';
import Planes from '../pages/Planes';
import Facturacion from '../pages/Facturacion';
import ConfiguracionGlobal from '../pages/ConfiguracionGlobal';
import AuditoriaLogs from '../pages/AuditoriaLogs';
import authService from '../services/authService';

/**
 * Componente de ruta protegida
 * Verifica autenticación antes de permitir acceso
 */
function ProtectedRoute({ children }) {
  const isAuthenticated = authService.isAuthenticated();
  return isAuthenticated ? children : <Navigate to="/superadmin/login" replace />;
}

/**
 * Rutas del módulo SuperAdmin
 */
export default function SuperAdminRoutes() {
  return (
    <Routes>
      {/* Login - Sin Layout */}
      <Route path="/login" element={<Login />} />

      {/* Rutas protegidas con Layout */}
      <Route
        path="/*"
        element={
          <ProtectedRoute>
            <SuperAdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/superadmin/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="negocios" element={<Negocios />} />
        <Route path="planes" element={<Planes />} />
        <Route path="facturacion" element={<Facturacion />} />
        <Route path="configuracion" element={<ConfiguracionGlobal />} />
        <Route path="auditoria" element={<AuditoriaLogs />} />
      </Route>
    </Routes>
  );
}
