/**
 * useCajas.js
 * ───────────
 * Hook para gestión de cajas registradoras, sesiones y movimientos.
 * Usa TanStack Query para cache + mutations.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { cajasService } from '../services/cajasService';
import { message } from '@/shared/utils/notifications';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ═══ CAJAS REGISTRADORAS ═══ */

export const useCajas = () => {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['cajas', negocioId],
    queryFn: () => cajasService.getByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 5,
  });

  const crearMutation = useMutation({
    mutationFn: cajasService.crear,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cajas'] });
      message.success('Caja creada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al crear caja');
    },
  });

  const actualizarMutation = useMutation({
    mutationFn: cajasService.actualizar,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cajas'] });
      message.success('Caja actualizada');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al actualizar caja');
    },
  });

  return {
    cajas: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,
    crear: crearMutation.mutateAsync,
    actualizar: actualizarMutation.mutateAsync,
    isCreating: crearMutation.isPending,
    isUpdating: actualizarMutation.isPending,
  };
};

/* ═══ SESIÓN ACTIVA DEL USUARIO ═══ */

export const useSesionActiva = () => {
  const { user } = useAdminAuthStore();
  const userId = user?.id;

  const query = useQuery({
    queryKey: ['sesion-activa', userId],
    queryFn: async () => {
      const data = await cajasService.getSesionActiva(userId);
      // Backend returns {sesionActiva: false} when no active session
      if (data && data.sesionActiva === false) return null;
      return data;
    },
    enabled: !!userId,
    staleTime: 1000 * 60,
    retry: false,
  });

  const sesionData = query.data || null;

  return {
    sesion: sesionData,
    isLoading: query.isLoading,
    hasSesion: !!sesionData && !!sesionData.id,
    refetch: query.refetch,
  };
};

/* ═══ ABRIR / CERRAR CAJA ═══ */

export const useSesionActions = () => {
  const queryClient = useQueryClient();

  const abrirMutation = useMutation({
    mutationFn: cajasService.abrirCaja,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sesion-activa'] });
      queryClient.invalidateQueries({ queryKey: ['sesiones'] });
      message.success('Caja abierta exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al abrir caja');
    },
  });

  const cerrarMutation = useMutation({
    mutationFn: cajasService.cerrarCaja,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['sesion-activa'] });
      queryClient.invalidateQueries({ queryKey: ['sesiones'] });
      queryClient.invalidateQueries({ queryKey: ['resumen-turno'] });
      message.success('Caja cerrada exitosamente');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al cerrar caja');
    },
  });

  return {
    abrirCaja: abrirMutation.mutateAsync,
    cerrarCaja: cerrarMutation.mutateAsync,
    isAbriendo: abrirMutation.isPending,
    isCerrando: cerrarMutation.isPending,
  };
};

/* ═══ SESIONES POR NEGOCIO ═══ */

export const useSesiones = () => {
  const { negocio } = useAdminAuthStore();
  const negocioId = negocio?.id;

  const query = useQuery({
    queryKey: ['sesiones', negocioId],
    queryFn: () => cajasService.getSesionesByNegocio(negocioId),
    enabled: !!negocioId,
    staleTime: 1000 * 60 * 2,
  });

  return {
    sesiones: query.data || [],
    isLoading: query.isLoading,
  };
};

/* ═══ RESUMEN DE TURNO ═══ */

export const useResumenTurno = (sesionCajaId) => {
  const query = useQuery({
    queryKey: ['resumen-turno', sesionCajaId],
    queryFn: () => cajasService.getResumenTurno(sesionCajaId),
    enabled: !!sesionCajaId,
    staleTime: 1000 * 30,
  });

  return {
    resumen: query.data || null,
    isLoading: query.isLoading,
    refetch: query.refetch,
  };
};

/* ═══ MOVIMIENTOS DE CAJA ═══ */

export const useMovimientos = (sesionCajaId) => {
  const queryClient = useQueryClient();

  const query = useQuery({
    queryKey: ['movimientos', sesionCajaId],
    queryFn: () => cajasService.getMovimientos(sesionCajaId),
    enabled: !!sesionCajaId,
    staleTime: 1000 * 60,
  });

  const registrarMutation = useMutation({
    mutationFn: cajasService.registrarMovimiento,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['movimientos'] });
      queryClient.invalidateQueries({ queryKey: ['resumen-turno'] });
      message.success('Movimiento registrado');
    },
    onError: (err) => {
      message.error(err.response?.data?.error || 'Error al registrar movimiento');
    },
  });

  return {
    movimientos: query.data || [],
    isLoading: query.isLoading,
    registrar: registrarMutation.mutateAsync,
    isRegistrando: registrarMutation.isPending,
  };
};
