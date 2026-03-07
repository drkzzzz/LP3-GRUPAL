import { useQuery } from '@tanstack/react-query';
import { storefrontService } from '../services/storefrontService';

export const useStorefrontCategories = (slug) => {
  const query = useQuery({
    queryKey: ['storefront-categorias', slug],
    queryFn: () => storefrontService.getCategoriasBySlug(slug),
    enabled: !!slug,
    staleTime: 1000 * 60 * 10,
  });

  return {
    categorias: query.data || [],
    isLoading: query.isLoading,
  };
};
