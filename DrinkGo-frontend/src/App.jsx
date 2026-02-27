import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { SuperAdminRoutes } from '@/superadmin/routes/SuperAdminRoutes';
import { AdminRoutes } from '@/admin/routes/AdminRoutes';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* SuperAdmin */}
        <Route path="/superadmin/*" element={<SuperAdminRoutes />} />

        {/* Admin */}
        <Route path="/admin/*" element={<AdminRoutes />} />

        {/* Storefront - Pendiente */}
        <Route
          path="/tienda/*"
          element={
            <div className="min-h-screen flex items-center justify-center">
              <div className="text-center">
                <h1 className="text-2xl font-bold text-gray-900">Tienda Online</h1>
                <p className="text-gray-600">Próximamente...</p>
              </div>
            </div>
          }
        />

        {/* Default redirect */}
        <Route path="*" element={<Navigate to="/superadmin" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
