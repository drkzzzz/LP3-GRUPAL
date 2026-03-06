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
    data: facturasServicio = [],
    isLoading: isLoadingFacturas,
  } = useQuery({
    queryKey: ['facturas-suscripcion-negocio', negocioId],
    queryFn: () => gastosService.getFacturasByNegocio(negocioId),
    enabled: !!negocioId,
  });

  const {
    data: categoriasGasto = [],
    isLoading: isLoadingCategorias,
  } = useQuery({
    queryKey: ['categorias-gasto', negocioId],
    queryFn: () => gastosService.getCategoriasByNegocio(negocioId),
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

  const subirComprobante = useMutation({
    mutationFn: gastosService.subirComprobante,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gastos', negocioId] });
      toast.success('Comprobante subido correctamente');
    },
    onError: (err) => {
      toast.error(err?.response?.data?.error || 'Error al subir el comprobante');
    },
  });

  const eliminarComprobante = useMutation({
    mutationFn: gastosService.eliminarComprobante,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gastos', negocioId] });
      toast.success('Comprobante eliminado');
    },
    onError: () => toast.error('Error al eliminar el comprobante'),
  });

  /* ─── Mutations: Categorías de Gasto ─── */
  const crearCategoria = useMutation({
    mutationFn: gastosService.createCategoria,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categorias-gasto', negocioId] });
      toast.success('Categoría creada correctamente');
    },
    onError: (err) => {
      toast.error(err?.response?.data?.error || 'Error al crear la categoría');
    },
  });

  const actualizarCategoria = useMutation({
    mutationFn: gastosService.updateCategoria,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categorias-gasto', negocioId] });
      toast.success('Categoría actualizada');
    },
    onError: (err) => {
      toast.error(err?.response?.data?.error || 'Error al actualizar la categoría');
    },
  });

  const eliminarCategoria = useMutation({
    mutationFn: gastosService.deleteCategoria,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categorias-gasto', negocioId] });
      toast.success('Categoría eliminada');
    },
    onError: () => toast.error('Error al eliminar la categoría'),
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
    facturasServicio,
    categoriasGasto,
    /* estados de carga */
    isLoadingGastos,
    isLoadingFacturas,
    isLoadingCategorias,
    /* acciones gastos */
    crearGasto: crearGasto.mutateAsync,
    actualizarGasto: actualizarGasto.mutateAsync,
    eliminarGasto: (id) => eliminarGasto.mutateAsync(id),
    pagarGasto: pagarGasto.mutateAsync,
    isCreating: crearGasto.isPending,
    isUpdating: actualizarGasto.isPending,
    isDeleting: eliminarGasto.isPending,
    isPagandoGasto: pagarGasto.isPending,
    /* acciones comprobante */
    subirComprobante: subirComprobante.mutateAsync,
    eliminarComprobante: (id) => eliminarComprobante.mutateAsync(id),
    isSubiendoComprobante: subirComprobante.isPending,
    isEliminandoComprobante: eliminarComprobante.isPending,
    /* acciones categorías */
    crearCategoria: crearCategoria.mutateAsync,
    actualizarCategoria: actualizarCategoria.mutateAsync,
    eliminarCategoria: (id) => eliminarCategoria.mutateAsync(id),
    isCreatingCategoria: crearCategoria.isPending,
    isUpdatingCategoria: actualizarCategoria.isPending,
    isDeletingCategoria: eliminarCategoria.isPending,
    /* acciones facturas */
    pagarFacturaServicio: pagarFacturaServicio.mutateAsync,
    isPagandoFactura: pagarFacturaServicio.isPending,
  };
};
