/**
 * Inventario.jsx
 * ──────────────
 * Layout contenedor del módulo Inventario.
 * Las sub-páginas se renderizan vía <Outlet /> (react-router nested routes).
 * El negocioId se inyecta como contexto a cada sub-ruta.
 */
import { Outlet } from 'react-router-dom';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

export const InventarioPage = () => {
  const negocio = useAdminAuthStore((s) => s.negocio);
  const negocioId = negocio?.id;

  return (
    <div className="space-y-6">
      <Outlet context={{ negocioId }} />
    </div>
  );
};
