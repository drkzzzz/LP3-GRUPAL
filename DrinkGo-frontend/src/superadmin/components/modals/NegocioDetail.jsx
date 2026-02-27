import { Badge } from '../ui/Badge';
import { formatDate, formatPhone, formatRUC } from '@/shared/utils/formatters';

const ESTADO_COLORS = {
  activo: 'success',
  pendiente: 'warning',
  suspendido: 'warning',
  cancelado: 'error',
};

const Field = ({ label, value }) => (
  <div>
    <dt className="text-xs font-medium text-gray-500 uppercase">{label}</dt>
    <dd className="mt-1 text-sm text-gray-900">{value || '—'}</dd>
  </div>
);

export const NegocioDetail = ({ negocio }) => {
  if (!negocio) return null;

  return (
    <div className="space-y-6">
      {/* Status + Header */}
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">
            {negocio.razonSocial}
          </h3>
          {negocio.nombreComercial && (
            <p className="text-sm text-gray-500">{negocio.nombreComercial}</p>
          )}
        </div>
        <Badge variant={ESTADO_COLORS[negocio.estado] || 'info'}>
          {negocio.estado?.toUpperCase()}
        </Badge>
      </div>

      {/* Info grid */}
      <dl className="grid grid-cols-2 gap-4">
        <Field label="RUC" value={negocio.ruc ? formatRUC(negocio.ruc) : null} />
        <Field label="Tipo Documento" value={negocio.tipoDocumentoFiscal} />
        <Field label="Representante Legal" value={negocio.representanteLegal} />
        <Field label="Doc. Representante" value={negocio.documentoRepresentante} />
        <Field label="Email" value={negocio.email} />
        <Field label="Teléfono" value={negocio.telefono ? formatPhone(negocio.telefono) : null} />
        <Field label="Dirección" value={negocio.direccion} />
        <Field label="Ciudad" value={negocio.ciudad} />
        <Field label="Departamento" value={negocio.departamento} />
        <Field label="País" value={negocio.pais} />
      </dl>

      {/* Dates */}
      <div className="border-t border-gray-200 pt-4">
        <dl className="grid grid-cols-2 gap-4">
          <Field label="Creado" value={formatDate(negocio.creadoEn)} />
          <Field label="Actualizado" value={formatDate(negocio.actualizadoEn)} />
        </dl>
      </div>
    </div>
  );
};
