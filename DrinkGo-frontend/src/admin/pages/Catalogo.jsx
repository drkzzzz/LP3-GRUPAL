/**
 * Catalogo.jsx
 * ────────────
 * Layout contenedor del módulo Catálogo.
 * Las sub-páginas se renderizan vía <Outlet /> (react-router nested routes).
 * negocioId y sedeId se inyectan como contexto a cada sub-ruta.
 * Categorías, marcas y unidades de medida se filtran por negocioId (compartidas).
 * Productos, combos y promociones se filtran por sedeId (por sede).
 */
import { Outlet } from 'react-router-dom';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

export const CatalogoPage = () => {
  const negocio = useAdminAuthStore((s) => s.negocio);
  const sede = useAdminAuthStore((s) => s.sede);
  const negocioId = negocio?.id;
  const sedeId = sede?.id;

  return (
    <div className="space-y-6">
      <Outlet context={{ negocioId, sedeId }} />
    </div>
  );
};
