import { useQuery } from '@tanstack/react-query';
import { storefrontService } from '../services/storefrontService';

export const useStorefrontSedes = (slug) => {
  const query = useQuery({
    queryKey: ['storefront-sedes', slug],
    queryFn: () => storefrontService.getSedesBySlug(slug),
    enabled: !!slug,
    staleTime: 1000 * 60 * 10,
  });

  return {
    sedes: query.data || [],
    isLoading: query.isLoading,
  };
};
