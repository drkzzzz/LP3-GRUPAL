/**
 * NegocioTab.jsx
 * ──────────────
 * Pestaña "Datos del Negocio": muestra y permite editar la información
 * del negocio actual. No es un CRUD de tabla, solo un formulario.
 */
import { Building2, FileText, MapPin, Receipt } from 'lucide-react';
import { useNegocioConfig } from '../../hooks/useNegocioConfig';
import { NegocioForm } from '../forms/NegocioForm';
import { Card } from '@/admin/components/ui/Card';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Badge } from '@/admin/components/ui/Badge';

export const NegocioTab = ({ context }) => {
  const { negocioId } = context;
  const { negocio, isLoading, updateNegocio, isUpdating } = useNegocioConfig(negocioId);

  const handleUpdate = async (data) => {
    await updateNegocio(data);
  };

  if (isLoading) {
    return (
      <div className="space-y-6 animate-pulse">
        <div className="h-8 bg-gray-200 rounded w-60" />
        <div className="grid grid-cols-3 gap-4">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-24 bg-gray-200 rounded-lg" />
          ))}
        </div>
        <div className="h-96 bg-gray-200 rounded-lg" />
      </div>
    );
  }

  const estadoVariant =
    negocio?.estado === 'activo' ? 'success' :
    negocio?.estado === 'suspendido' ? 'warning' :
    negocio?.estado === 'pendiente' ? 'info' : 'error';

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Datos del Negocio</h1>
          <p className="text-gray-600 mt-1">
            Información general, fiscal y de contacto de tu negocio
          </p>
        </div>
        {negocio?.estado && (
          <Badge variant={estadoVariant} className="text-sm px-3 py-1">
            {negocio.estado.toUpperCase()}
          </Badge>
        )}
      </div>

      {/* Stats rápidos */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Razón Social"
          value={negocio?.razonSocial || '—'}
          icon={Building2}
        />
        <StatCard
          title="RUC"
          value={negocio?.ruc || '—'}
          icon={FileText}
        />
        <StatCard
          title="Ciudad"
          value={negocio?.ciudad || '—'}
          icon={MapPin}
        />
        <StatCard
          title="IGV"
          value={negocio?.aplicaIgv ? `${negocio.porcentajeIgv}%` : 'No aplica'}
          icon={Receipt}
        />
      </div>

      {/* Formulario de edición */}
      <Card>
        <div className="p-1">
          <NegocioForm
            initialData={negocio}
            onSubmit={handleUpdate}
            isLoading={isUpdating}
          />
        </div>
      </Card>
    </div>
  );
};
