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

  /* ─── Usuarios Query ─── */
  const usuariosQuery = useQuery({
    queryKey: ['usuarios-negocio'],
    queryFn: negociosService.getAllUsuarios,
  });

  /* ─── Facturas Query ─── */
  const facturasQuery = useQuery({
    queryKey: ['facturas-suscripcion'],
    queryFn: negociosService.getAllFacturas,
  });

  /* ─── Negocios Mutations ─── */
  const createNegocio = useMutation({
    mutationFn: negociosService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['negocios'] });
      queryClient.invalidateQueries({ queryKey: ['sedes'] });
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

  /* ─── Usuarios Mutations ─── */
  const createUsuario = useMutation({
    mutationFn: negociosService.createUsuario,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios-negocio'] });
      message.success('Usuario creado exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al crear usuario');
    },
  });

  const updateUsuario = useMutation({
    mutationFn: negociosService.updateUsuario,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios-negocio'] });
      message.success('Credenciales actualizadas');
    },
    onError: (err) => {
      message.error(err.response?.data?.message || 'Error al actualizar usuario');
    },
  });

  /* ─── Facturas Mutations ─── */
  const generarFactura = useMutation({
    mutationFn: negociosService.generarFactura,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas-suscripcion'] });
      message.success('Factura generada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al generar factura');
    },
  });

  const pagarFactura = useMutation({
    mutationFn: negociosService.pagarFactura,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas-suscripcion'] });
      queryClient.invalidateQueries({ queryKey: ['suscripciones'] });
      message.success('Factura marcada como pagada. Suscripción extendida un mes.');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al registrar pago');
    },
  });

  const cancelarFacturaMutation = useMutation({
    mutationFn: negociosService.cancelarFactura,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facturas-suscripcion'] });
      message.success('Factura anulada');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al anular factura');
    },
  });

  /* ─── Helpers: filter by negocio ─── */
  const getSedesForNegocio = (negocioId) => {
    if (!sedesQuery.data || !negocioId) return [];
    return sedesQuery.data.filter(
      (s) => (s.negocio?.id ?? s.negocioId) === negocioId,
    );
  };

  const getSuscripcionForNegocio = (negocioId) => {
    if (!suscripcionesQuery.data || !negocioId) return null;
    const match = (s) => (s.negocio?.id ?? s.negocioId) === negocioId;
    return (
      suscripcionesQuery.data.find((s) => match(s) && s.estado === 'activa') ||
      suscripcionesQuery.data.find(match) ||
      null
    );
  };

  const getUsuariosForNegocio = (negocioId) => {
    if (!usuariosQuery.data || !negocioId) return [];
    return usuariosQuery.data.filter(
      (u) => (u.negocio?.id ?? u.negocioId) === negocioId,
    );
  };

  const getFacturasForNegocio = (negocioId) => {
    if (!facturasQuery.data || !negocioId) return [];
    return facturasQuery.data
      .filter((f) => (f.negocio?.id ?? f.negocioId) === negocioId)
      .sort((a, b) => new Date(b.emitidoEn || b.creadoEn) - new Date(a.emitidoEn || a.creadoEn));
  };

  /* ─── Stats ─── */
  const negocios = negociosQuery.data || [];
  const suscripciones = suscripcionesQuery.data || [];
  const planes = planesQuery.data || [];
  const sedes = sedesQuery.data || [];
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
    sedes,
    isLoadingSedes: sedesQuery.isLoading,
    getSedesForNegocio,

    /* Suscripciones */
    suscripciones,
    isLoadingSuscripciones: suscripcionesQuery.isLoading,
    getSuscripcionForNegocio,

    /* Planes */
    planes,
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

    /* Usuarios del negocio */
    getUsuariosForNegocio,
    createUsuario: createUsuario.mutateAsync,
    updateUsuario: updateUsuario.mutateAsync,
    isCreatingUsuario: createUsuario.isPending,
    isUpdatingUsuario: updateUsuario.isPending,

    /* Facturas suscripción */
    getFacturasForNegocio,
    generarFactura: generarFactura.mutateAsync,
    pagarFactura: pagarFactura.mutateAsync,
    cancelarFactura: cancelarFacturaMutation.mutateAsync,
    isGenerandoFactura: generarFactura.isPending,
    isPagandoFactura: pagarFactura.isPending,
    isCancelandoFactura: cancelarFacturaMutation.isPending,
  };
};
