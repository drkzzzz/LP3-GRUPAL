/**
 * VentasPage.jsx
 * ──────────────
 * Layout contenedor del módulo Ventas / POS.
 * Las sub-páginas se renderizan vía <Outlet />.
 */
import { Outlet } from 'react-router-dom';

export const VentasPage = () => {
  return (
    <div className="space-y-6">
      <Outlet />
    </div>
  );
};
