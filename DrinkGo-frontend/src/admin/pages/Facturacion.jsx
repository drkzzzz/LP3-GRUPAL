/**
 * Facturacion.jsx
 * ───────────────
 * Layout contenedor del módulo Facturación.
 * Las sub-páginas (Comprobantes, Series, Métodos de Pago) se renderizan vía <Outlet />.
 */
import { Outlet } from 'react-router-dom';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

export const Facturacion = () => {
  const negocio = useAdminAuthStore((s) => s.negocio);
  const negocioId = negocio?.id;

  return (
    <div className="space-y-6">
      <Outlet context={{ negocioId }} />
    </div>
  );
};
