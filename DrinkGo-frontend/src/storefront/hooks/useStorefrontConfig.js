import { useQuery } from '@tanstack/react-query';
import { storefrontService } from '../services/storefrontService';

export const useStorefrontConfig = (slug) => {
  const query = useQuery({
    queryKey: ['storefront-config', slug],
    queryFn: () => storefrontService.getConfigBySlug(slug),
    enabled: !!slug,
    staleTime: 1000 * 60 * 10,
    retry: 1,
  });

  return {
    config: query.data,
    isLoading: query.isLoading,
    isError: query.isError,
    error: query.error,
  };
};
