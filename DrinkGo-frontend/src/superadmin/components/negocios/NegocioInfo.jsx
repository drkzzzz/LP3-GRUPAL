import { Badge } from '../ui/Badge';
import {
  formatDate,
  formatPhone,
  formatRUC,
} from '@/shared/utils/formatters';
import {
  Mail,
  Phone,
  MapPin,
  FileText,
  User,
  Calendar,
  Globe,
} from 'lucide-react';

const ESTADO_COLORS = {
  activo: 'success',
  pendiente: 'warning',
  suspendido: 'warning',
  cancelado: 'error',
};

const InfoRow = ({ icon: Icon, label, value }) => (
  <div className="flex items-start gap-3 py-2.5">
    <div className="flex-shrink-0 mt-0.5">
      <Icon size={16} className="text-gray-400" />
    </div>
    <div className="min-w-0">
      <p className="text-xs font-medium text-gray-500 uppercase tracking-wide">
        {label}
      </p>
      <p className="text-sm text-gray-900 mt-0.5">{value || '—'}</p>
    </div>
  </div>
);

export const NegocioInfo = ({ negocio }) => {
  if (!negocio) return null;

  return (
    <div className="space-y-6">
      {/* Header card */}
      <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-xl p-5 border border-blue-100">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-xl bg-blue-600 flex items-center justify-center text-white font-bold text-xl shadow-sm">
              {negocio.razonSocial?.charAt(0)?.toUpperCase() || 'N'}
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900">
                {negocio.razonSocial}
              </h3>
              {negocio.nombreComercial && (
                <p className="text-sm text-gray-600">
                  {negocio.nombreComercial}
                </p>
              )}
              <p className="text-xs text-gray-500 mt-1 font-mono">
                UUID: {negocio.uuid?.substring(0, 8)}...
              </p>
            </div>
          </div>
          <Badge variant={ESTADO_COLORS[negocio.estado] || 'info'}>
            {negocio.estado?.toUpperCase()}
          </Badge>
        </div>
      </div>

      {/* Datos fiscales */}
      <div>
        <h4 className="text-sm font-semibold text-gray-800 mb-3 flex items-center gap-2">
          <FileText size={16} className="text-gray-500" />
          Datos Fiscales
        </h4>
        <div className="bg-white rounded-lg border border-gray-200 px-4 divide-y divide-gray-100">
          <InfoRow
            icon={FileText}
            label="RUC"
            value={negocio.ruc ? formatRUC(negocio.ruc) : null}
          />
          <InfoRow
            icon={FileText}
            label="Tipo Documento"
            value={negocio.tipoDocumentoFiscal}
          />
          <InfoRow
            icon={User}
            label="Representante Legal"
            value={negocio.representanteLegal}
          />
          <InfoRow
            icon={FileText}
            label="Doc. Representante"
            value={negocio.documentoRepresentante}
          />
        </div>
      </div>

      {/* Contacto */}
      <div>
        <h4 className="text-sm font-semibold text-gray-800 mb-3 flex items-center gap-2">
          <Mail size={16} className="text-gray-500" />
          Contacto
        </h4>
        <div className="bg-white rounded-lg border border-gray-200 px-4 divide-y divide-gray-100">
          <InfoRow icon={Mail} label="Email" value={negocio.email} />
          <InfoRow
            icon={Phone}
            label="Teléfono"
            value={negocio.telefono ? formatPhone(negocio.telefono) : null}
          />
        </div>
      </div>

      {/* Ubicación */}
      <div>
        <h4 className="text-sm font-semibold text-gray-800 mb-3 flex items-center gap-2">
          <MapPin size={16} className="text-gray-500" />
          Ubicación
        </h4>
        <div className="bg-white rounded-lg border border-gray-200 px-4 divide-y divide-gray-100">
          <InfoRow icon={MapPin} label="Dirección" value={negocio.direccion} />
          <InfoRow icon={MapPin} label="Ciudad" value={negocio.ciudad} />
          <InfoRow
            icon={MapPin}
            label="Departamento"
            value={negocio.departamento}
          />
          <InfoRow
            icon={Globe}
            label="País"
            value={negocio.pais || 'Perú'}
          />
        </div>
      </div>

      {/* Fechas */}
      <div>
        <h4 className="text-sm font-semibold text-gray-800 mb-3 flex items-center gap-2">
          <Calendar size={16} className="text-gray-500" />
          Fechas
        </h4>
        <div className="bg-white rounded-lg border border-gray-200 px-4 divide-y divide-gray-100">
          <InfoRow
            icon={Calendar}
            label="Fecha de Registro"
            value={formatDate(negocio.creadoEn)}
          />
          <InfoRow
            icon={Calendar}
            label="Última Actualización"
            value={formatDate(negocio.actualizadoEn)}
          />
        </div>
      </div>
    </div>
  );
};
