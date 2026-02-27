import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { facturacionService } from '../services/facturacionService';
import { message } from '@/shared/utils/notifications';

export const useFacturacion = () => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['facturas'],
    queryFn: facturacionService.getAll,
  });

  const createMutation = useMutation({
    mutationFn: facturacionService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura creada exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al crear factura');
    },
  });

  const updateMutation = useMutation({
    mutationFn: facturacionService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura actualizada exitosamente');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al actualizar factura');
    },
  });

  const marcarPagadaMutation = useMutation({
    mutationFn: facturacionService.marcarPagada,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura marcada como pagada');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al marcar factura');
    },
  });

  const cancelarMutation = useMutation({
    mutationFn: facturacionService.cancelar,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Factura cancelada');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al cancelar factura');
    },
  });

  const reintentarMutation = useMutation({
    mutationFn: facturacionService.reintentar,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas'] });
      message.success('Reintento de cobro encolado');
    },
    onError: (error) => {
      message.error(error.response?.data?.message || 'Error al reintentar factura');
    },
  });

  return {
    facturas: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,

    createFactura: createMutation.mutateAsync,
    updateFactura: updateMutation.mutateAsync,
    marcarPagada: marcarPagadaMutation.mutateAsync,
    cancelarFactura: cancelarMutation.mutateAsync,
    reintentarFactura: reintentarMutation.mutateAsync,
    isCreating: createMutation.isPending,
    isUpdating: updateMutation.isPending,
    isMarkingPaid: marcarPagadaMutation.isPending,
    isCanceling: cancelarMutation.isPending,
    isRetrying: reintentarMutation.isPending,
  };
};
