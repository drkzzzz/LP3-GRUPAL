/**
 * Hooks de React Query para zonas de delivery
 */
import { useQuery } from '@tanstack/react-query';
import {
  getZonasDelivery,
  buscarZonaPorDistrito,
  getZonaDeliveryById,
  calcularCostoDelivery,
} from '@/admin/configuracion/services/zonasDeliveryApi';

/**
 * Hook para obtener todas las zonas de delivery activas
 */
export function useZonasDelivery() {
  return useQuery({
    queryKey: ['zonas-delivery'],
    queryFn: getZonasDelivery,
    staleTime: 5 * 60 * 1000, // 5 minutos
  });
}

/**
 * Hook para buscar zona por distrito
 * @param {string} distrito - Nombre del distrito
 * @param {boolean} enabled - Si la búsqueda debe ejecutarse
 */
export function useBuscarZonaPorDistrito(distrito, enabled = true) {
  return useQuery({
    queryKey: ['zona-distrito', distrito],
    queryFn: () => buscarZonaPorDistrito(distrito),
    enabled: enabled && distrito && distrito.trim().length > 0,
    staleTime: 5 * 60 * 1000, // 5 minutos
  });
}

/**
 * Hook para obtener una zona por ID
 * @param {number} id - ID de la zona
 */
export function useZonaDeliveryById(id) {
  return useQuery({
    queryKey: ['zonas-delivery', id],
    queryFn: () => getZonaDeliveryById(id),
    enabled: !!id,
  });
}

/**
 * Hook para calcular costo de delivery según distrito
 * @param {string} distrito - Distrito del cliente
 * @param {boolean} enabled - Si el cálculo debe ejecutarse
 */
export function useCalcularCostoDelivery(distrito, enabled = true) {
  return useQuery({
    queryKey: ['costo-delivery', distrito],
    queryFn: () => calcularCostoDelivery(distrito),
    enabled: enabled && distrito && distrito.trim().length > 0,
    staleTime: 5 * 60 * 1000, // 5 minutos
  });
}
