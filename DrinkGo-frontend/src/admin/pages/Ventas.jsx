/**
 * Ventas.jsx
 * ──────────
 * Layout contenedor del módulo Ventas / POS.
 * Las sub-páginas (POS, Cajas, Historial) se renderizan vía <Outlet />.
 */
import { Outlet } from 'react-router-dom';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

export const Ventas = () => {
  const negocio = useAdminAuthStore((s) => s.negocio);
  const negocioId = negocio?.id;

  return (
    <div className="space-y-6">
      <Outlet context={{ negocioId }} />
    </div>
  );
};
