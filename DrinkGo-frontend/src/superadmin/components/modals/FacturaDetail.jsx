import { Badge } from '../ui/Badge';
import { formatCurrency, formatDate, formatDateTime } from '@/shared/utils/formatters';

const ESTADO_COLORS = {
  borrador: 'info',
  pendiente: 'warning',
  pagada: 'success',
  fallida: 'error',
  reembolsada: 'warning',
  anulada: 'error',
};

const Field = ({ label, value }) => (
  <div>
    <dt className="text-xs font-medium text-gray-500 uppercase">{label}</dt>
    <dd className="mt-1 text-sm text-gray-900">{value ?? '—'}</dd>
  </div>
);

export const FacturaDetail = ({ factura }) => {
  if (!factura) return null;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-gray-900">
          Factura {factura.numeroFactura}
        </h3>
        <Badge variant={ESTADO_COLORS[factura.estado] || 'info'}>
          {factura.estado?.toUpperCase()}
        </Badge>
      </div>

      {/* Business */}
      {factura.negocio && (
        <div className="bg-gray-50 rounded-lg p-4">
          <p className="text-sm font-medium text-gray-700">Negocio</p>
          <p className="text-sm text-gray-900">
            {factura.negocio.razonSocial}
          </p>
          {factura.negocio.ruc && (
            <p className="text-xs text-gray-500">RUC: {factura.negocio.ruc}</p>
          )}
        </div>
      )}

      {/* Amounts */}
      <dl className="grid grid-cols-2 gap-4">
        <Field label="Subtotal" value={formatCurrency(factura.subtotal)} />
        <Field label="Impuesto" value={formatCurrency(factura.montoImpuesto)} />
        <Field label="Descuento" value={formatCurrency(factura.montoDescuento)} />
        <Field
          label="Total"
          value={
            <span className="text-lg font-bold text-gray-900">
              {formatCurrency(factura.total)}
            </span>
          }
        />
      </dl>

      {/* Dates */}
      <div className="border-t border-gray-200 pt-4">
        <dl className="grid grid-cols-2 gap-4">
          <Field label="Período Inicio" value={formatDate(factura.inicioPeriodo)} />
          <Field label="Período Fin" value={formatDate(factura.finPeriodo)} />
          <Field label="Emitida" value={formatDateTime(factura.emitidoEn)} />
          <Field label="Vencimiento" value={formatDate(factura.fechaVencimiento)} />
          <Field label="Pagada" value={formatDateTime(factura.pagadoEn)} />
          <Field label="Método de Pago" value={factura.metodoPago} />
        </dl>
      </div>

      {/* Notes */}
      {factura.notas && (
        <div className="border-t border-gray-200 pt-4">
          <p className="text-xs font-medium text-gray-500 uppercase mb-1">Notas</p>
          <p className="text-sm text-gray-700">{factura.notas}</p>
        </div>
      )}
    </div>
  );
};
