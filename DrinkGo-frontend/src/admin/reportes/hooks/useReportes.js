/**
 * useReportes.js
 * ──────────────
 * Hooks de TanStack Query para el módulo de Reportes (Admin).
 * Consume reportesService y expone datos listos para las tabs.
 */
import { useQuery } from '@tanstack/react-query';
import { reportesService } from '../services/reportesService';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ─── Ventas del negocio ─── */
export const useReporteVentas = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'ventas', negocioId],
    queryFn: () => reportesService.getVentasByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};

/* ─── Detalle ventas por negocio (para reporte de productos) ─── */
export const useReporteDetalleVentas = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'detalleVentas', negocioId],
    queryFn: () => reportesService.getDetalleVentasByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};

/* ─── Stock inventario ─── */
export const useReporteStock = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'stock', negocioId],
    queryFn: () => reportesService.getStockInventario(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};

/* ─── Lotes inventario ─── */
export const useReporteLotes = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'lotes', negocioId],
    queryFn: () => reportesService.getLotesInventario(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};

/* ─── Órdenes de compra ─── */
export const useReporteCompras = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'compras', negocioId],
    queryFn: () => reportesService.getOrdenesCompra(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};

/* ─── Gastos ─── */
export const useReporteGastos = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'gastos', negocioId],
    queryFn: () => reportesService.getGastos(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};

/* ─── Comprobantes (boletas y facturas) ─── */
export const useReporteComprobantes = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'comprobantes', negocioId],
    queryFn: () => reportesService.getComprobantes(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};

/* ─── Movimientos de inventario ─── */
export const useReporteMovimientos = () => {
  const negocioId = useAdminAuthStore((s) => s.negocio?.id);

  return useQuery({
    queryKey: ['reportes', 'movimientos', negocioId],
    queryFn: () => reportesService.getMovimientosInventario(negocioId),
    enabled: !!negocioId,
    staleTime: 0,
  });
};
