import { useQuery } from '@tanstack/react-query';
import { auditoriaService } from '../services/auditoriaService';

export const useAuditoria = () => {
  const query = useQuery({
    queryKey: ['auditoria'],
    queryFn: auditoriaService.getAll,
  });

  return {
    registros: query.data || [],
    isLoading: query.isLoading,
    isError: query.isError,
  };
};
