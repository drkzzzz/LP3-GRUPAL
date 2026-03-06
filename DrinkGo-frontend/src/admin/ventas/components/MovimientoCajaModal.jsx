/**
 * MovimientoCajaModal.jsx
 * ───────────────────────
 * Modal simplificado para registrar ingresos/egresos manuales en la sesión.
 * Solo 2 opciones: Ingreso y Egreso.
 */
import { useState } from 'react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';

const TIPO_MOVIMIENTO_OPTIONS = [
  { value: 'ingreso_manual', label: 'Ingreso' },
  { value: 'egreso_manual', label: 'Egreso' },
];

export const MovimientoCajaModal = ({
  isOpen,
  onClose,
  onConfirm,
  sesionCajaId,
  isLoading = false,
}) => {
  const [tipoMovimiento, setTipoMovimiento] = useState('ingreso_manual');
  const [monto, setMonto] = useState('');
  const [descripcion, setDescripcion] = useState('');

  const isEgreso = tipoMovimiento.startsWith('egreso');

  const handleConfirm = () => {
    if (!monto || parseFloat(monto) <= 0 || !descripcion.trim()) return;
    const payload = {
      sesionCajaId,
      tipo: tipoMovimiento,
      monto: parseFloat(monto),
      concepto: descripcion.trim(),
    };
    onConfirm(payload);
  };

  const handleClose = () => {
    setTipoMovimiento('ingreso_manual');
    setMonto('');
    setDescripcion('');
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Registrar Movimiento de Caja" size="sm">
      <div className="space-y-4">
        <Select
          label="Tipo de Movimiento"
          required
          options={TIPO_MOVIMIENTO_OPTIONS}
          value={tipoMovimiento}
          onChange={(e) => setTipoMovimiento(e.target.value)}
        />

        <Input
          label={`Monto (S/) ${isEgreso ? '- Egreso' : '+ Ingreso'}`}
          required
          type="number"
          min={0.01}
          step={0.01}
          value={monto}
          onChange={(e) => setMonto(e.target.value)}
          placeholder="0.00"
        />

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Descripción <span className="text-red-500">*</span>
          </label>
          <textarea
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            rows={2}
            placeholder="Motivo del movimiento..."
            className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm
                       focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
          />
        </div>

        <div className="flex justify-end gap-2 pt-2">
          <Button variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancelar
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={!monto || parseFloat(monto) <= 0 || !descripcion.trim() || isLoading}
          >
            {isLoading ? 'Registrando...' : 'Registrar'}
          </Button>
        </div>
      </div>
    </Modal>
  );
};
