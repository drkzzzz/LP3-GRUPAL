import { useQuery } from '@tanstack/react-query';
import { dashboardService } from '../services/dashboardService';

/**
 * Normaliza la respuesta del backend: acepta tanto arreglo plano como
 * respuesta paginada { content: [...] } — ambos formatos son válidos.
 */
const toArray = (data) => {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
};

export const useDashboard = () => {
  const query = useQuery({
    queryKey: ['dashboard-stats'],
    queryFn: dashboardService.getStats,
    staleTime: 1000 * 60 * 2, // 2 min
  });

  const raw = query.data || {};

  const negocios = toArray(raw.negocios);
  const planes = toArray(raw.planes);
  const facturas = toArray(raw.facturas);
  const suscripciones = toArray(raw.suscripciones);

  const totalNegocios = negocios.length;
  const negociosActivos = negocios.filter((n) => n.estado === 'activo').length;
  const negociosPendientes = negocios.filter((n) => n.estado === 'pendiente').length;
  const negociosSuspendidos = negocios.filter((n) => n.estado === 'suspendido').length;

  const totalPlanes = planes.length;

  const totalFacturas = facturas.length;
  const facturasPendientes = facturas.filter((f) => f.estado === 'pendiente').length;
  const facturasPagadas = facturas.filter((f) => f.estado === 'pagada').length;
  const ingresosMensuales = facturas
    .filter((f) => f.estado === 'pagada')
    .reduce((sum, f) => sum + (Number(f.total) || 0), 0);

  const suscripcionesActivas = suscripciones.filter((s) => s.estado === 'activa').length;

  return {
    isLoading: query.isLoading,
    isError: query.isError,

    // Totales por dominio
    totalNegocios,
    negociosActivos,
    negociosPendientes,
    negociosSuspendidos,
    totalPlanes,
    totalFacturas,
    facturasPendientes,
    facturasPagadas,
    ingresosMensuales,
    suscripcionesActivas,

    // Arrays completos para exportación
    allNegocios: negocios,
    allFacturas: facturas,
    allPlanes: planes,
    allSuscripciones: suscripciones,

    // Últimas entradas para tablas de preview
    recentNegocios: negocios.slice(-5).reverse(),
    recentFacturas: facturas.slice(-5).reverse(),
  };
};
