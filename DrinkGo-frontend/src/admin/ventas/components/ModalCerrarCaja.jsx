/**
 * ModalCerrarCaja.jsx
 * ───────────────────
 * Modal de arqueo y cierre de caja con resumen de turno.
 */
import { useState } from 'react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { Badge } from '@/admin/components/ui/Badge';
import { formatCurrency } from '@/shared/utils/formatters';

export const ModalCerrarCaja = ({
  isOpen,
  onClose,
  onConfirm,
  sesion,
  resumen,
  isLoading = false,
}) => {
  const [montoContado, setMontoContado] = useState('');
  const [observaciones, setObservaciones] = useState('');

  const efectivoEsperado = resumen?.efectivoEsperado ?? 0;
  const montoContadoNum = parseFloat(montoContado) || 0;
  const diferencia = montoContadoNum - efectivoEsperado;

  const handleConfirm = () => {
    onConfirm({
      sesionCajaId: sesion?.id,
      montoCierre: montoContadoNum,
      observaciones: observaciones || null,
    });
  };

  const handleClose = () => {
    setMontoContado('');
    setObservaciones('');
    onClose();
  };

  if (!sesion) return null;

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Cerrar Caja – Arqueo" size="lg">
      <div className="space-y-5">
        {/* Info de sesión */}
        <div className="bg-gray-50 rounded-lg p-4">
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <span className="text-gray-500">Caja:</span>{' '}
              <span className="font-medium">
                {sesion.caja?.nombreCaja || '—'}
              </span>
            </div>
            <div>
              <span className="text-gray-500">Apertura:</span>{' '}
              <span className="font-medium">
                {formatCurrency(sesion.montoApertura)}
              </span>
            </div>
          </div>
        </div>

        {/* Resumen del turno */}
        {resumen && (
          <div className="space-y-2">
            <h3 className="text-sm font-semibold text-gray-700">Resumen del turno</h3>
            <div className="grid grid-cols-2 gap-2 text-sm">
              <div className="flex justify-between bg-green-50 rounded p-2">
                <span className="text-gray-600">Ventas completadas</span>
                <span className="font-medium">{resumen.totalVentasCompletadas ?? 0}</span>
              </div>
              <div className="flex justify-between bg-red-50 rounded p-2">
                <span className="text-gray-600">Ventas anuladas</span>
                <span className="font-medium">{resumen.totalVentasAnuladas ?? 0}</span>
              </div>
              <div className="flex justify-between p-2">
                <span className="text-gray-600">Total Efectivo</span>
                <span className="font-medium">{formatCurrency(resumen.totalEfectivo ?? 0)}</span>
              </div>
              <div className="flex justify-between p-2">
                <span className="text-gray-600">Total Tarjeta</span>
                <span className="font-medium">{formatCurrency(resumen.totalTarjeta ?? 0)}</span>
              </div>
              <div className="flex justify-between p-2">
                <span className="text-gray-600">Total Yape</span>
                <span className="font-medium">{formatCurrency(resumen.totalYape ?? 0)}</span>
              </div>
              <div className="flex justify-between p-2">
                <span className="text-gray-600">Total Plin</span>
                <span className="font-medium">{formatCurrency(resumen.totalPlin ?? 0)}</span>
              </div>
              <div className="flex justify-between p-2">
                <span className="text-gray-600">Otros ingresos</span>
                <span className="font-medium">{formatCurrency(resumen.totalIngresos ?? 0)}</span>
              </div>
              <div className="flex justify-between p-2">
                <span className="text-gray-600">Egresos</span>
                <span className="font-medium text-red-600">
                  -{formatCurrency(resumen.totalEgresos ?? 0)}
                </span>
              </div>
            </div>

            <div className="border-t border-gray-200 pt-3">
              <div className="flex justify-between text-base font-bold">
                <span>Efectivo esperado en caja</span>
                <span className="text-green-700">
                  {formatCurrency(efectivoEsperado)}
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Arqueo */}
        <div className="space-y-3">
          <h3 className="text-sm font-semibold text-gray-700">Arqueo de caja</h3>

          <Input
            label="Monto contado en caja (S/)"
            required
            type="number"
            min={0}
            step={0.01}
            value={montoContado}
            onChange={(e) => setMontoContado(e.target.value)}
            placeholder="Ingrese el efectivo contado"
          />

          {montoContado !== '' && (
            <div className="flex items-center gap-2">
              <span className="text-sm text-gray-600">Diferencia:</span>
              <Badge
                variant={
                  Math.abs(diferencia) < 0.01
                    ? 'success'
                    : diferencia > 0
                      ? 'info'
                      : 'error'
                }
              >
                {diferencia >= 0 ? '+' : ''}
                {formatCurrency(diferencia)}
              </Badge>
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Observaciones
            </label>
            <textarea
              value={observaciones}
              onChange={(e) => setObservaciones(e.target.value)}
              rows={2}
              placeholder="Notas sobre el cierre de caja..."
              className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm
                         focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
            />
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="flex justify-end gap-2 pt-4 border-t border-gray-200 mt-4">
        <Button variant="outline" onClick={handleClose} disabled={isLoading}>
          Cancelar
        </Button>
        <Button
          variant="danger"
          onClick={handleConfirm}
          disabled={montoContado === '' || isLoading}
        >
          {isLoading ? 'Cerrando...' : 'Cerrar Caja'}
        </Button>
      </div>
    </Modal>
  );
};
