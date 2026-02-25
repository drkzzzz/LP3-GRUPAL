import { Routes, Route, Navigate } from 'react-router-dom';
import SuperAdminLayout from '../layouts/SuperAdminLayout';
import Dashboard from '../pages/Dashboard';
import Negocios from '../pages/Negocios';
import Planes from '../pages/Planes';
import Facturacion from '../pages/Facturacion';
import ConfiguracionGlobal from '../pages/ConfiguracionGlobal';
import AuditoriaLogs from '../pages/AuditoriaLogs';

/**
 * Rutas del módulo SuperAdmin
 * TEMPORAL: Sin autenticación para desarrollo
 */
export default function SuperAdminRoutes() {
  return (
    <Routes>
      {/* Rutas con Layout - Sin protección temporal */}
      <Route path="/*" element={<SuperAdminLayout />}>
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
