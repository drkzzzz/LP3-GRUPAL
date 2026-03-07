/**
 * CerrarCuentaModal.jsx
 * ─────────────────────
 * RF-VTA-014: Pre-cuenta y cierre de cuenta de mesa.
 * Sin descuentos, sin propinas.
 * Muestra división por personas (RF-VTA-013).
 */
import { useState } from 'react';
import { Users, CheckCircle } from 'lucide-react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { cuentasMesaService } from '../services/cuentasMesaService';
import { formatCurrency } from '@/shared/utils/formatters';

export const CerrarCuentaModal = ({ isOpen, onClose, cuenta, detalles, usuarioId, onConfirm, isLoading }) => {
  const [personas, setPersonas] = useState(cuenta?.numComensales ?? 1);
  const [montoPorPersona, setMontoPorPersona] = useState(null);
  const [isDividing, setIsDividing] = useState(false);

  const handleDividir = async () => {
    if (!personas || personas < 1 || !cuenta?.id) return;
    setIsDividing(true);
    try {
      const data = await cuentasMesaService.dividirPorPersonas(cuenta.id, personas);
      setMontoPorPersona(data?.montoPorPersona ?? (cuenta.total / personas));
    } catch {
      setMontoPorPersona(cuenta.total / personas);
    } finally {
      setIsDividing(false);
    }
  };

  const handleClose = () => {
    setPersonas(cuenta?.numComensales ?? 1);
    setMontoPorPersona(null);
    onClose();
  };

  const handleConfirm = () => {
    onConfirm({ cuentaId: cuenta.id, usuarioId });
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title="Cerrar cuenta"
      size="md"
      footer={
        <>
          <Button variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancelar
          </Button>
          <Button variant="primary" onClick={handleConfirm} disabled={isLoading}>
            {isLoading ? 'Cerrando...' : 'Confirmar pago y cerrar'}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        {/* Resumen de cuenta */}
        <div className="bg-gray-50 rounded-lg p-3">
          <div className="flex justify-between text-sm mb-1">
            <span className="text-gray-500">Mesa:</span>
            <span className="font-medium text-gray-800">{cuenta?.mesa?.nombre}</span>
          </div>
          <div className="flex justify-between text-sm mb-1">
            <span className="text-gray-500">Cuenta:</span>
            <span className="font-medium text-gray-800">{cuenta?.numeroCuenta}</span>
          </div>
          <div className="flex justify-between text-sm">
            <span className="text-gray-500">Comensales:</span>
            <span className="font-medium text-gray-800">{cuenta?.numComensales}</span>
          </div>
        </div>

        {/* Pre-cuenta (lista de items) */}
        {detalles?.length > 0 && (
          <div>
            <p className="text-sm font-semibold text-gray-700 mb-2">Detalle</p>
            <div className="border border-gray-200 rounded-lg divide-y divide-gray-100 max-h-52 overflow-y-auto">
              {detalles.map((d) => (
                <div key={d.id} className="flex items-center justify-between px-3 py-1.5 text-sm">
                  <span className="text-gray-700">
                    {d.cantidad}× {d.nombreProductoSnapshot}
                  </span>
                  <span className="font-medium text-gray-800">{formatCurrency(d.subtotal ?? d.total ?? 0)}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Total */}
        <div className="flex justify-between items-center border-t border-gray-200 pt-3">
          <span className="text-base font-semibold text-gray-700">Total a cobrar</span>
          <span className="text-2xl font-bold text-gray-900">{formatCurrency(cuenta?.total ?? 0)}</span>
        </div>

        {/* División por personas */}
        <div className="border border-dashed border-gray-300 rounded-lg p-3">
          <p className="text-sm font-medium text-gray-700 mb-2 flex items-center gap-1">
            <Users size={14} />
            Dividir entre personas (opcional)
          </p>
          <div className="flex items-end gap-2">
            <div className="flex-1">
              <Input
                type="number"
                min={1}
                max={50}
                value={personas}
                onChange={(e) => { setPersonas(Number(e.target.value)); setMontoPorPersona(null); }}
                label="Número de personas"
              />
            </div>
            <Button variant="outline" size="sm" onClick={handleDividir} disabled={isDividing || personas < 1}>
              {isDividing ? '...' : 'Calcular'}
            </Button>
          </div>
          {montoPorPersona !== null && (
            <p className="mt-2 text-sm text-green-700 font-semibold flex items-center gap-1">
              <CheckCircle size={14} />
              {formatCurrency(montoPorPersona)} por persona
            </p>
          )}
        </div>
      </div>
    </Modal>
  );
};
