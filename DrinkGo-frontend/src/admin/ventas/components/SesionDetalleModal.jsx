/**
 * SesionDetalleModal.jsx
 * ──────────────────────
 * Modal simple que muestra las observaciones de cierre de una sesión de caja.
 * El resto del detalle financiero se consulta en Movimientos de Caja.
 */
import { Modal } from '@/admin/components/ui/Modal';

export const SesionDetalleModal = ({ isOpen, onClose, sesion }) => {
  if (!sesion) return null;

  const nombreCaja = sesion.caja?.nombreCaja || sesion.cajaNombre || '—';

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Observaciones de Cierre" size="md">
      <div className="space-y-3">
        <p className="text-sm text-gray-500">
          Caja: <span className="font-medium text-gray-800">{nombreCaja}</span>
        </p>
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <p className="text-sm text-gray-700 whitespace-pre-wrap">
            {sesion.observaciones}
          </p>
        </div>
      </div>
    </Modal>
  );
};

