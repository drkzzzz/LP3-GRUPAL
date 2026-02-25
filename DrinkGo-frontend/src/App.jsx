import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import SuperAdminRoutes from '../superadmin/routes/SuperAdminRoutes';
import Login from '../superadmin/pages/Login';

function App() {
  return (
    <Router>
      <Routes>
        {/* Redirigir raíz al login de SuperAdmin */}
        <Route path="/" element={<Navigate to="/superadmin/login" replace />} />
        
        {/* Login SuperAdmin */}
        <Route path="/login" element={<Login />} />
        
        {/* Rutas SuperAdmin */}
        <Route path="/superadmin/*" element={<SuperAdminRoutes />} />
        
        {/* Admin y Storefront - Pendientes */}
        <Route path="/admin/*" element={
          <div className="min-h-screen flex items-center justify-center">
            <div className="text-center">
              <h1 className="text-2xl font-bold">Módulo Admin</h1>
              <p className="text-gray-600">Próximamente...</p>
            </div>
          </div>
        } />
        
        <Route path="/tienda/*" element={
          <div className="min-h-screen flex items-center justify-center">
            <div className="text-center">
              <h1 className="text-2xl font-bold">Tienda Online</h1>
              <p className="text-gray-600">Próximamente...</p>
            </div>
          </div>
        } />
      </Routes>
    </Router>
  );
}

export default App;
