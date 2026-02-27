import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { SuperAdminRoutes } from '@/superadmin/routes/SuperAdminRoutes';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* SuperAdmin */}
        <Route path="/superadmin/*" element={<SuperAdminRoutes />} />

        {/* Admin - Pendiente */}
        <Route
          path="/admin/*"
          element={
            <div className="min-h-screen flex items-center justify-center">
              <div className="text-center">
                <h1 className="text-2xl font-bold text-gray-900">Módulo Admin</h1>
                <p className="text-gray-600">Próximamente...</p>
              </div>
            </div>
          }
        />

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
