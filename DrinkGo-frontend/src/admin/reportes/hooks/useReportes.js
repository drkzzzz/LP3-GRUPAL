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
  return useQuery({
    queryKey: ['reportes', 'stock'],
    queryFn: () => reportesService.getStockInventario(),
    staleTime: 0,
  });
};

/* ─── Lotes inventario ─── */
export const useReporteLotes = () => {
  return useQuery({
    queryKey: ['reportes', 'lotes'],
    queryFn: () => reportesService.getLotesInventario(),
    staleTime: 0,
  });
};

/* ─── Órdenes de compra ─── */
export const useReporteCompras = () => {
  return useQuery({
    queryKey: ['reportes', 'compras'],
    queryFn: () => reportesService.getOrdenesCompra(),
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
  return useQuery({
    queryKey: ['reportes', 'movimientos'],
    queryFn: () => reportesService.getMovimientosInventario(),
    staleTime: 0,
  });
};
