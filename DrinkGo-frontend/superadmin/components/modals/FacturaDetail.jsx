import Badge from '../ui/Badge';
import Button from '../ui/Button';
import facturacionService from '../../services/facturacionService';

/**
 * Modal con detalle completo de la factura
 */
const FacturaDetail = ({ factura, onClose, onUpdate }) => {
  const handleMarkAsPaid = async () => {
    if (confirm('¿Marcar esta factura como pagada?')) {
      try {
        await facturacionService.markAsPaid(factura.id, {
          metodoPago: 'transferencia',
          referenciaPago: 'Pago manual desde SuperAdmin',
          fechaPago: new Date().toISOString()
        });
        onUpdate?.();
        onClose();
      } catch (error) {
        console.error('Error marcando como pagada:', error);
        alert('Error al marcar la factura como pagada');
      }
    }
  };

  const handleCancel = async () => {
    const motivo = prompt('Ingrese el motivo de anulación:');
    if (motivo) {
      try {
        await facturacionService.cancel(factura.id, motivo);
        onUpdate?.();
        onClose();
      } catch (error) {
        console.error('Error anulando factura:', error);
        alert('Error al anular la factura');
      }
    }
  };

  const handleSendReminder = async () => {
    try {
      await facturacionService.sendReminder(factura.id);
      alert('Recordatorio enviado exitosamente');
    } catch (error) {
      console.error('Error enviando recordatorio:', error);
      alert('Error al enviar recordatorio');
    }
  };

  const getEstadoBadge = (estado) => {
    const estados = {
      'pendiente': 'pending',
      'pagada': 'paid',
      'vencida': 'overdue',
      'anulada': 'cancelled',
    };
    return estados[estado?.toLowerCase()] || 'default';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-xl font-semibold text-gray-900">
            Factura {factura.numeroFactura}
          </h3>
          <p className="text-sm text-gray-600 mt-1">
            Emisión: {new Date(factura.fechaEmision).toLocaleDateString('es-PE')}
          </p>
        </div>
        <Badge variant={getEstadoBadge(factura.estado)}>
          {factura.estado}
        </Badge>
      </div>

      {/* Información del Negocio */}
      <div className="p-4 bg-gray-50 rounded-lg">
        <h4 className="font-semibold text-gray-900 mb-2">Información del Negocio</h4>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <div>
            <span className="text-gray-600">Razón Social:</span>
            <br />
            <span className="font-medium">{factura.negocioNombre}</span>
          </div>
          <div>
            <span className="text-gray-600">RUC:</span>
            <br />
            <span className="font-medium">{factura.negocioRuc}</span>
          </div>
          <div>
            <span className="text-gray-600">Email:</span>
            <br />
            <span className="font-medium">{factura.negocioEmail}</span>
          </div>
          <div>
            <span className="text-gray-600">Plan:</span>
            <br />
            <span className="font-medium">{factura.planNombre}</span>
          </div>
        </div>
      </div>

      {/* Detalle de la Factura */}
      <div>
        <h4 className="font-semibold text-gray-900 mb-3">Detalle de Facturación</h4>
        <div className="space-y-2 text-sm">
          <div className="flex justify-between py-2 border-b border-gray-200">
            <span className="text-gray-600">Periodo Facturado:</span>
            <span className="font-medium">
              {factura.periodoInicio && new Date(factura.periodoInicio).toLocaleDateString('es-PE')} - 
              {' '}{factura.periodoFin && new Date(factura.periodoFin).toLocaleDateString('es-PE')}
            </span>
          </div>
          
          <div className="flex justify-between py-2 border-b border-gray-200">
            <span className="text-gray-600">Subtotal:</span>
            <span className="font-medium">{factura.moneda} {factura.subtotal?.toFixed(2)}</span>
          </div>
          
          <div className="flex justify-between py-2 border-b border-gray-200">
            <span className="text-gray-600">IGV (18%):</span>
            <span className="font-medium">{factura.moneda} {factura.impuestos?.toFixed(2)}</span>
          </div>
          
          <div className="flex justify-between py-3 bg-gray-50 px-3 rounded-lg">
            <span className="font-semibold text-gray-900">Total:</span>
            <span className="font-bold text-lg text-gray-900">
              {factura.moneda} {factura.monto?.toFixed(2)}
            </span>
          </div>
          
          <div className="flex justify-between py-2">
            <span className="text-gray-600">Fecha de Vencimiento:</span>
            <span className={`font-medium ${
              new Date(factura.fechaVencimiento) < new Date() && factura.estado === 'pendiente'
                ? 'text-red-600'
                : ''
            }`}>
              {new Date(factura.fechaVencimiento).toLocaleDateString('es-PE')}
            </span>
          </div>
        </div>
      </div>

      {/* Información de Pago */}
      {factura.estado === 'pagada' && factura.fechaPago && (
        <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
          <h4 className="font-semibold text-green-900 mb-2">Información de Pago</h4>
          <div className="space-y-1 text-sm text-green-800">
            <div>
              <span className="font-medium">Fecha de Pago:</span>{' '}
              {new Date(factura.fechaPago).toLocaleDateString('es-PE')}
            </div>
            {factura.metodoPago && (
              <div>
                <span className="font-medium">Método:</span> {factura.metodoPago}
              </div>
            )}
            {factura.referenciaPago && (
              <div>
                <span className="font-medium">Referencia:</span> {factura.referenciaPago}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Acciones */}
      <div className="flex justify-between gap-3 pt-4 border-t">
        <Button variant="outline" onClick={onClose}>
          Cerrar
        </Button>
        
        <div className="flex gap-2">
          {factura.estado === 'pendiente' && (
            <>
              <Button variant="warning" onClick={handleSendReminder}>
                Enviar Recordatorio
              </Button>
              <Button variant="success" onClick={handleMarkAsPaid}>
                Marcar como Pagada
              </Button>
            </>
          )}
          
          {(factura.estado === 'pendiente' || factura.estado === 'vencida') && (
            <Button variant="danger" onClick={handleCancel}>
              Anular Factura
            </Button>
          )}
        </div>
      </div>
    </div>
  );
};

export default FacturaDetail;
