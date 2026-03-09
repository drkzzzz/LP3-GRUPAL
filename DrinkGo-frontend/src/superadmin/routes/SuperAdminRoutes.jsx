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
import { MiPerfil } from '../pages/MiPerfil';
import { ProgramadorNegocios } from '../pages/ProgramadorNegocios';
import { UsuariosBloqueados } from '../pages/UsuariosBloqueados';
import { useAuthStore } from '@/stores/authStore';

const ProtectedRoute = ({ children }) => {
  const { token } = useAuthStore();
  if (!token) {
    return <Navigate to="/superadmin/login" replace />;
  }
  return children;
};

/** Ruta exclusiva para el rol superadmin/soporte/visualizador (no programador) */
const SuperAdminOnlyRoute = ({ children }) => {
  const { token, user } = useAuthStore();
  if (!token) return <Navigate to="/superadmin/login" replace />;
  if (user?.rol === 'programador') return <Navigate to="/superadmin/programador/negocios" replace />;
  return children;
};

/** Ruta exclusiva para el rol programador */
const ProgramadorRoute = ({ children }) => {
  const { token, user } = useAuthStore();
  if (!token) return <Navigate to="/superadmin/login" replace />;
  if (user?.rol !== 'programador') return <Navigate to="/superadmin/dashboard" replace />;
  return children;
};

export const SuperAdminRoutes = () => {
  return (
    <Routes>
      <Route path="login" element={<Login />} />

      {/* Ruta de selección de negocio para programadores */}
      <Route
        path="programador/negocios"
        element={
          <ProgramadorRoute>
            <ProgramadorNegocios />
          </ProgramadorRoute>
        }
      />

      {/* Panel superadmin (solo roles no-programador) */}
      <Route
        element={
          <SuperAdminOnlyRoute>
            <SuperAdminLayout />
          </SuperAdminOnlyRoute>
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
        <Route path="usuarios-bloqueados" element={<UsuariosBloqueados />} />
        <Route path="perfil" element={<MiPerfil />} />
      </Route>
    </Routes>
  );
};
