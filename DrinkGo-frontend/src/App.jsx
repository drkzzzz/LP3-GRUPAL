import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { SuperAdminRoutes } from '@/superadmin/routes/SuperAdminRoutes';
import { AdminRoutes } from '@/admin/routes/AdminRoutes';
import { StorefrontRoutes } from '@/storefront/routes/StorefrontRoutes';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* SuperAdmin */}
        <Route path="/superadmin/*" element={<SuperAdminRoutes />} />

        {/* Admin */}
        <Route path="/admin/*" element={<AdminRoutes />} />

        {/* Storefront - Tienda Online por Negocio */}
        <Route path="/tienda/*" element={<StorefrontRoutes />} />

        {/* Default redirect */}
        <Route path="*" element={<Navigate to="/superadmin" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
