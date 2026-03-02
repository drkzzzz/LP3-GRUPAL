import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { gastosService } from '../services/gastosService';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

export const useGastos = () => {
  const queryClient = useQueryClient();
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;

  /* ─── Queries ─── */
  const {
    data: gastos = [],
    isLoading: isLoadingGastos,
  } = useQuery({
    queryKey: ['gastos', negocioId],
    queryFn: () => gastosService.getByNegocio(negocioId),
    enabled: !!negocioId,
  });

  const {
    data: categorias = [],
    isLoading: isLoadingCategorias,
  } = useQuery({
    queryKey: ['categorias-gasto', negocioId],
    queryFn: () => gastosService.getCategoriasByNegocio(negocioId),
    enabled: !!negocioId,
  });

  const {
    data: facturasServicio = [],
    isLoading: isLoadingFacturas,
  } = useQuery({
    queryKey: ['facturas-suscripcion-negocio', negocioId],
    queryFn: () => gastosService.getFacturasByNegocio(negocioId),
    enabled: !!negocioId,
  });

  /* ─── Mutations: Gastos Externos ─── */
  const crearGasto = useMutation({
    mutationFn: gastosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gastos', negocioId] });
      toast.success('Gasto registrado correctamente');
    },
    onError: (err) => {
      toast.error(err?.response?.data?.error || 'Error al registrar el gasto');
    },
  });

  const actualizarGasto = useMutation({
    mutationFn: gastosService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gastos', negocioId] });
      toast.success('Gasto actualizado');
    },
    onError: (err) => {
      toast.error(err?.response?.data?.error || 'Error al actualizar el gasto');
    },
  });

  const eliminarGasto = useMutation({
    mutationFn: gastosService.remove,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gastos', negocioId] });
      toast.success('Gasto eliminado');
    },
    onError: () => toast.error('Error al eliminar el gasto'),
  });

  const pagarGasto = useMutation({
    mutationFn: gastosService.marcarPagado,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gastos', negocioId] });
      toast.success('Pago registrado correctamente');
    },
    onError: (err) => {
      toast.error(err?.response?.data?.error || 'Error al registrar el pago');
    },
  });

  /* ─── Mutations: Facturas de Suscripción ─── */
  const pagarFacturaServicio = useMutation({
    mutationFn: gastosService.pagarFacturaSuscripcion,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas-suscripcion-negocio', negocioId] });
      toast.success('Factura marcada como pagada');
    },
    onError: (err) => {
      toast.error(err?.response?.data?.error || 'Error al registrar el pago');
    },
  });

  return {
    negocioId,
    /* datos */
    gastos,
    categorias,
    facturasServicio,
    /* estados de carga */
    isLoadingGastos,
    isLoadingCategorias,
    isLoadingFacturas,
    /* acciones gastos */
    crearGasto: crearGasto.mutateAsync,
    actualizarGasto: actualizarGasto.mutateAsync,
    eliminarGasto: (id) => eliminarGasto.mutateAsync(id),
    pagarGasto: pagarGasto.mutateAsync,
    isCreating: crearGasto.isPending,
    isUpdating: actualizarGasto.isPending,
    isDeleting: eliminarGasto.isPending,
    isPagandoGasto: pagarGasto.isPending,
    /* acciones facturas */
    pagarFacturaServicio: pagarFacturaServicio.mutateAsync,
    isPagandoFactura: pagarFacturaServicio.isPending,
  };
};
