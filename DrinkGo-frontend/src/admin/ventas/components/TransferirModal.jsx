/**
 * TransferirModal.jsx
 * ───────────────────
 * RF-VTA-013: Transferencia de mesa completa o de productos a otra cuenta.
 * Modos:
 *  1. Transferir mesa completa → selecciona mesa disponible → POST /cuentas-mesa/{id}/transferir-mesa
 *  2. Transferir productos → checkboxes en detalles + seleccionar cuenta destino → POST /cuentas-mesa/{id}/transferir-productos
 */
import { useState } from 'react';
import { ArrowRightLeft, Columns, CheckSquare } from 'lucide-react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { formatCurrency } from '@/shared/utils/formatters';

const MODO_MESA = 'mesa';
const MODO_PRODUCTOS = 'productos';

export const TransferirModal = ({
  isOpen,
  onClose,
  cuenta,
  detalles,
  cuentasAbiertas,    // array de CuentasMesa con estado=abierta (excluyendo la actual)
  mesasDisponibles,   // array de Mesas con estado=disponible
  onTransferirMesa,
  onTransferirProductos,
  isLoading,
}) => {
  const [modo, setModo] = useState(MODO_MESA);
  const [nuevaMesaId, setNuevaMesaId] = useState('');
  const [cuentaDestinoId, setCuentaDestinoId] = useState('');
  const [selectedDetalleIds, setSelectedDetalleIds] = useState([]);

  const handleClose = () => {
    setModo(MODO_MESA);
    setNuevaMesaId('');
    setCuentaDestinoId('');
    setSelectedDetalleIds([]);
    onClose();
  };

  const toggleDetalle = (id) => {
    setSelectedDetalleIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id],
    );
  };

  const handleConfirm = () => {
    if (modo === MODO_MESA) {
      if (!nuevaMesaId) return;
      onTransferirMesa({ cuentaId: cuenta.id, nuevaMesaId: Number(nuevaMesaId) });
    } else {
      if (!cuentaDestinoId || selectedDetalleIds.length === 0) return;
      onTransferirProductos({ cuentaId: cuenta.id, cuentaDestinoId: Number(cuentaDestinoId), detalleIds: selectedDetalleIds });
    }
  };

  const canConfirm =
    modo === MODO_MESA
      ? !!nuevaMesaId
      : !!cuentaDestinoId && selectedDetalleIds.length > 0;

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={`Transferir — ${cuenta?.numeroCuenta ?? ''}`}
      size="md"
      footer={
        <>
          <Button variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleConfirm} disabled={!canConfirm || isLoading}>
            {isLoading ? 'Transfiriendo...' : 'Confirmar transferencia'}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        {/* Selector de modo */}
        <div className="flex rounded-lg border border-gray-200 overflow-hidden">
          <button
            className={`flex-1 flex items-center justify-center gap-2 py-2 text-sm font-medium transition-colors ${
              modo === MODO_MESA
                ? 'bg-green-600 text-white'
                : 'bg-white text-gray-600 hover:bg-gray-50'
            }`}
            onClick={() => setModo(MODO_MESA)}
          >
            <ArrowRightLeft size={15} />
            Mesa completa
          </button>
          <button
            className={`flex-1 flex items-center justify-center gap-2 py-2 text-sm font-medium transition-colors border-l border-gray-200 ${
              modo === MODO_PRODUCTOS
                ? 'bg-green-600 text-white'
                : 'bg-white text-gray-600 hover:bg-gray-50'
            }`}
            onClick={() => setModo(MODO_PRODUCTOS)}
          >
            <Columns size={15} />
            Productos
          </button>
        </div>

        {/* ── Modo: mesa completa ── */}
        {modo === MODO_MESA && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Mesa destino (disponibles)
            </label>
            <select
              value={nuevaMesaId}
              onChange={(e) => setNuevaMesaId(e.target.value)}
              className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="">Seleccionar mesa...</option>
              {mesasDisponibles
                .filter((m) => m.id !== cuenta?.mesa?.id)
                .map((m) => (
                  <option key={m.id} value={m.id}>
                    {m.nombre} (cap. {m.capacidad})
                  </option>
                ))}
            </select>
            {mesasDisponibles.filter((m) => m.id !== cuenta?.mesa?.id).length === 0 && (
              <p className="text-xs text-amber-600 mt-1">No hay mesas disponibles en esta sede.</p>
            )}
          </div>
        )}

        {/* ── Modo: transferir productos ── */}
        {modo === MODO_PRODUCTOS && (
          <div className="space-y-3">
            {/* Seleccionar productos */}
            <div>
              <p className="text-sm font-medium text-gray-700 mb-1 flex items-center gap-1">
                <CheckSquare size={14} />
                Seleccionar productos a transferir
              </p>
              {detalles?.length === 0 ? (
                <p className="text-xs text-gray-500">Sin productos en esta cuenta.</p>
              ) : (
                <div className="border border-gray-200 rounded-lg divide-y divide-gray-100 max-h-44 overflow-y-auto">
                  {detalles.map((d) => (
                    <label key={d.id} className="flex items-center gap-2 px-3 py-2 cursor-pointer hover:bg-gray-50">
                      <input
                        type="checkbox"
                        checked={selectedDetalleIds.includes(d.id)}
                        onChange={() => toggleDetalle(d.id)}
                        className="accent-green-600"
                      />
                      <span className="flex-1 text-sm text-gray-700">
                        {d.cantidad}× {d.nombreProductoSnapshot}
                      </span>
                      <span className="text-sm text-gray-600">{formatCurrency(d.subtotal ?? 0)}</span>
                    </label>
                  ))}
                </div>
              )}
            </div>

            {/* Seleccionar cuenta destino */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Cuenta destino
              </label>
              <select
                value={cuentaDestinoId}
                onChange={(e) => setCuentaDestinoId(e.target.value)}
                className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                <option value="">Seleccionar cuenta...</option>
                {cuentasAbiertas
                  .filter((c) => c.id !== cuenta?.id)
                  .map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.numeroCuenta} — {c.mesa?.nombre}
                    </option>
                  ))}
              </select>
              {cuentasAbiertas.filter((c) => c.id !== cuenta?.id).length === 0 && (
                <p className="text-xs text-amber-600 mt-1">No hay otras cuentas abiertas en esta sede.</p>
              )}
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
};
