import { Badge } from '../ui/Badge';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { Check, X } from 'lucide-react';
import { clsx } from 'clsx';

const ADMIN_MODULES = [
  { key: 'dashboard',      label: 'Dashboard' },
  { key: 'configuracion', label: 'Configuración' },
  { key: 'usuarios',      label: 'Usuarios' },
  { key: 'catalogo',      label: 'Catálogo' },
  { key: 'inventario',    label: 'Inventario' },
  { key: 'compras',       label: 'Compras' },
  { key: 'ventas',        label: 'Ventas' },
  { key: 'facturacion',   label: 'Facturación' },
  { key: 'gastos',        label: 'Gastos' },
  { key: 'reportes',      label: 'Reportes' },
];

const Field = ({ label, value }) => (
  <div>
    <dt className="text-xs font-medium text-gray-500 uppercase">{label}</dt>
    <dd className="mt-1 text-sm text-gray-900">{value ?? '—'}</dd>
  </div>
);

const BoolField = ({ label, value }) => (
  <div className="flex items-center gap-2">
    {value ? (
      <Check size={16} className="text-green-600" />
    ) : (
      <X size={16} className="text-gray-400" />
    )}
    <span className="text-sm text-gray-700">{label}</span>
  </div>
);

export const PlanDetail = ({ plan }) => {
  if (!plan) return null;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-gray-900">{plan.nombre}</h3>
        <Badge variant={plan.estaActivo ? 'success' : 'error'}>
          {plan.estaActivo ? 'ACTIVO' : 'INACTIVO'}
        </Badge>
      </div>

      {plan.descripcion && (
        <p className="text-sm text-gray-600">{plan.descripcion}</p>
      )}

      {/* Pricing */}
      <dl className="grid grid-cols-3 gap-4">
        <Field label="Precio" value={formatCurrency(plan.precio)} />
        <Field label="Moneda" value={plan.moneda} />
        <Field label="Período" value={plan.periodoFacturacion} />
      </dl>

      {/* Limits */}
      <div className="border-t border-gray-200 pt-4">
        <h4 className="text-sm font-medium text-gray-700 mb-3">Límites</h4>
        <dl className="grid grid-cols-4 gap-4">
          <Field label="Sedes" value={plan.maxSedes} />
          <Field label="Usuarios" value={plan.maxUsuarios} />
          <Field label="Productos" value={plan.maxProductos} />
          <Field label="Almacenes/Sede" value={plan.maxAlmacenesPorSede} />
        </dl>
      </div>

      {/* Features */}
      <div className="border-t border-gray-200 pt-4">
        <h4 className="text-sm font-medium text-gray-700 mb-3">Funcionalidades</h4>
        <div className="grid grid-cols-2 gap-3">
          <BoolField label="Punto de Venta (POS)" value={plan.permitePos} />
          <BoolField label="Tienda Online" value={plan.permiteTiendaOnline} />
          <BoolField label="Delivery" value={plan.permiteDelivery} />
          <BoolField label="Mesas" value={plan.permiteMesas} />
          <BoolField label="Facturación Electrónica" value={plan.permiteFacturacionElectronica} />
          <BoolField label="Multi-Almacén" value={plan.permiteMultiAlmacen} />
          <BoolField label="Reportes Avanzados" value={plan.permiteReportesAvanzados} />
          <BoolField label="Acceso API" value={plan.permiteAccesoApi} />
        </div>
      </div>

      {/* Módulos del Panel Admin */}
      <div className="border-t border-gray-200 pt-4">
        <h4 className="text-sm font-medium text-gray-700 mb-3 flex items-center gap-2">
          Módulos del Panel Admin
          {!plan.modulosHabilitados && (
            <span className="text-xs font-normal bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
              todos habilitados
            </span>
          )}
        </h4>
        <div className="grid grid-cols-2 gap-2">
          {ADMIN_MODULES.map(({ key, label }) => {
            let enabled = true;
            if (plan.modulosHabilitados) {
              try { enabled = JSON.parse(plan.modulosHabilitados).includes(key); }
              catch { enabled = true; }
            }
            return (
              <div
                key={key}
                className={clsx(
                  'flex items-center gap-2 text-sm',
                  enabled ? 'text-gray-700' : 'text-gray-300',
                )}
              >
                {enabled
                  ? <Check size={14} className="text-green-500 shrink-0" />
                  : <X size={14} className="text-gray-300 shrink-0" />}
                {label}
              </div>
            );
          })}
        </div>
      </div>

      {/* Dates */}
      <div className="border-t border-gray-200 pt-4">
        <dl className="grid grid-cols-2 gap-4">
          <Field label="Creado" value={formatDate(plan.creadoEn)} />
          <Field label="Actualizado" value={formatDate(plan.actualizadoEn)} />
        </dl>
      </div>
    </div>
  );
};
