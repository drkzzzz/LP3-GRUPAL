/**
 * TiendaOnlineTab.jsx
 * ───────────────────
 * Tab de configuración de la tienda online del negocio.
 * Similar a NegocioTab: formulario único, no tabla CRUD.
 */
import { Globe, ShoppingBag, Link2, AlertCircle } from 'lucide-react';
import { useTiendaOnline } from '../../hooks/useTiendaOnline';
import { Card } from '@/admin/components/ui/Card';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { TiendaOnlineForm } from '../forms/TiendaOnlineForm';

export const TiendaOnlineTab = ({ context }) => {
  const { negocioId } = context;

  const {
    tiendaConfig,
    isLoading,
    isError,
    createTienda,
    updateTienda,
    isCreating,
    isUpdating,
  } = useTiendaOnline(negocioId);

  const handleSubmit = async (formData) => {
    const payload = {
      negocio: { id: negocioId },
      estaHabilitado: formData.estaHabilitado,
      nombreTienda: formData.nombreTienda?.trim() || '',
      slugTienda: formData.slugTienda?.trim() || '',
      dominioPersonalizado: formData.dominioPersonalizado?.trim() || '',
    };

    if (tiendaConfig?.id) {
      await updateTienda({ ...payload, id: tiendaConfig.id });
    } else {
      await createTienda(payload);
    }
  };

  const isSaving = isCreating || isUpdating;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-24">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-center">
        <AlertCircle size={40} className="text-red-400 mb-3" />
        <p className="text-gray-600 text-sm">Error al cargar la configuración de la tienda online.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Tienda Online</h1>
          <p className="text-gray-600 mt-1">Configura tu presencia de e-commerce para vender por internet</p>
        </div>
        <Badge variant={tiendaConfig?.estaHabilitado ? 'success' : 'warning'}>
          {tiendaConfig?.estaHabilitado ? 'Habilitada' : 'Deshabilitada'}
        </Badge>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard
          title="Tienda"
          value={tiendaConfig?.nombreTienda || 'Sin configurar'}
          icon={ShoppingBag}
        />
        <StatCard
          title="Slug"
          value={tiendaConfig?.slugTienda || '—'}
          icon={Link2}
          className="border-l-4 border-l-blue-500"
        />
        <StatCard
          title="Dominio"
          value={tiendaConfig?.dominioPersonalizado || 'No configurado'}
          icon={Globe}
          className="border-l-4 border-l-purple-500"
        />
      </div>

      {/* Formulario */}
      <Card>
        <div className="px-1 py-2">
          <TiendaOnlineForm
            initialData={tiendaConfig}
            onSubmit={handleSubmit}
            isLoading={isSaving}
          />
        </div>
      </Card>
    </div>
  );
};
