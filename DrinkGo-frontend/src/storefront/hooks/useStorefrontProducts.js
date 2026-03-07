import { useQuery } from '@tanstack/react-query';
import { storefrontService } from '../services/storefrontService';

export const useStorefrontProducts = (slug, params = {}) => {
  const query = useQuery({
    queryKey: ['storefront-productos', slug, params],
    queryFn: () => storefrontService.getProductosBySlug(slug, params),
    enabled: !!slug,
  });

  const toArray = (d) => (Array.isArray(d) ? d : d?.content || []);

  return {
    productos: toArray(query.data),
    totalPages: query.data?.totalPages || 0,
    totalElements: query.data?.totalElements || 0,
    isLoading: query.isLoading,
    isError: query.isError,
  };
};
