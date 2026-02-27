import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { negociosService } from '../services/negociosService';
import { message } from '@/shared/utils/notifications';

/* ================================================================
 *  Hook principal: useNegocios
 *  Maneja negocios CRUD + sedes + suscripciones + planes
 * ================================================================ */
export const useNegocios = () => {
  const queryClient = useQueryClient();

  /* ─── Negocios Query ─── */
  const negociosQuery = useQuery({
    queryKey: ['negocios'],
    queryFn: negociosService.getAll,
  });

  /* ─── Sedes Query ─── */
  const sedesQuery = useQuery({
    queryKey: ['sedes'],
    queryFn: negociosService.getAllSedes,
  });

  /* ─── Suscripciones Query ─── */
  const suscripcionesQuery = useQuery({
    queryKey: ['suscripciones'],
    queryFn: negociosService.getAllSuscripciones,
  });

  /* ─── Planes Query ─── */
  const planesQuery = useQuery({
    queryKey: ['planes-suscripcion'],
    queryFn: negociosService.getAllPlanes,
  });

  /* ─── Negocios Mutations ─── */
  const createNegocio = useMutation({
    mutationFn: negociosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['negocios'] });
      message.success('Negocio creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear negocio');
    },
  });

  const updateNegocio = useMutation({
    mutationFn: negociosService.update,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['negocios'] });
      message.success('Negocio actualizado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar negocio');
    },
  });

  const deleteNegocio = useMutation({
    mutationFn: negociosService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['negocios'] });
      queryClient.invalidateQueries({ queryKey: ['sedes'] });
      queryClient.invalidateQueries({ queryKey: ['suscripciones'] });
      message.success('Negocio eliminado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar negocio');
    },
  });

  /* ─── Sedes Mutations ─── */
  const createSede = useMutation({
    mutationFn: negociosService.createSede,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sedes'] });
      message.success('Sede creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear sede');
    },
  });

  const updateSede = useMutation({
    mutationFn: negociosService.updateSede,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sedes'] });
      message.success('Sede actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar sede');
    },
  });

  const deleteSede = useMutation({
    mutationFn: negociosService.deleteSede,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sedes'] });
      message.success('Sede eliminada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al eliminar sede');
    },
  });

  /* ─── Suscripciones Mutations ─── */
  const createSuscripcion = useMutation({
    mutationFn: negociosService.createSuscripcion,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['suscripciones'] });
      message.success('Suscripción creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear suscripción');
    },
  });

  const updateSuscripcion = useMutation({
    mutationFn: negociosService.updateSuscripcion,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['suscripciones'] });
      message.success('Suscripción actualizada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar suscripción');
    },
  });

  /* ─── Helpers: filter by negocio ─── */
  const getSedesForNegocio = (negocioId) => {
    if (!sedesQuery.data || !negocioId) return [];
    return sedesQuery.data.filter((s) => s.negocio?.id === negocioId);
  };

  const getSuscripcionForNegocio = (negocioId) => {
    if (!suscripcionesQuery.data || !negocioId) return null;
    return suscripcionesQuery.data.find(
      (s) => s.negocio?.id === negocioId && s.estado === 'activa',
    ) || suscripcionesQuery.data.find((s) => s.negocio?.id === negocioId) || null;
  };

  /* ─── Stats ─── */
  const negocios = negociosQuery.data || [];
  const stats = {
    total: negocios.length,
    activos: negocios.filter((n) => n.estado === 'activo').length,
    pendientes: negocios.filter((n) => n.estado === 'pendiente').length,
    suspendidos: negocios.filter((n) => n.estado === 'suspendido').length,
  };

  return {
    /* Negocios */
    negocios,
    isLoading: negociosQuery.isLoading,
    isError: negociosQuery.isError,
    stats,

    /* Sedes */
    sedes: sedesQuery.data || [],
    isLoadingSedes: sedesQuery.isLoading,
    getSedesForNegocio,

    /* Suscripciones */
    suscripciones: suscripcionesQuery.data || [],
    isLoadingSuscripciones: suscripcionesQuery.isLoading,
    getSuscripcionForNegocio,

    /* Planes */
    planes: planesQuery.data || [],
    isLoadingPlanes: planesQuery.isLoading,

    /* Negocio mutations */
    createNegocio: createNegocio.mutateAsync,
    updateNegocio: updateNegocio.mutateAsync,
    deleteNegocio: deleteNegocio.mutateAsync,
    isCreating: createNegocio.isPending,
    isUpdating: updateNegocio.isPending,
    isDeleting: deleteNegocio.isPending,

    /* Sede mutations */
    createSede: createSede.mutateAsync,
    updateSede: updateSede.mutateAsync,
    deleteSede: deleteSede.mutateAsync,
    isCreatingSede: createSede.isPending,
    isUpdatingSede: updateSede.isPending,
    isDeletingSede: deleteSede.isPending,

    /* Suscripcion mutations */
    createSuscripcion: createSuscripcion.mutateAsync,
    updateSuscripcion: updateSuscripcion.mutateAsync,
    isCreatingSuscripcion: createSuscripcion.isPending,
    isUpdatingSuscripcion: updateSuscripcion.isPending,
  };
};
