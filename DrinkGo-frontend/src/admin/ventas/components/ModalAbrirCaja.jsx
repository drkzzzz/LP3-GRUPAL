/**
 * ModalAbrirCaja.jsx
 * ──────────────────
 * Modal para abrir una nueva sesión de caja (turno).
 */
import { useState } from 'react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';

export const ModalAbrirCaja = ({
  isOpen,
  onClose,
  onConfirm,
  cajas = [],
  isLoading = false,
}) => {
  const [cajaId, setCajaId] = useState('');
  const [montoApertura, setMontoApertura] = useState('100.00');

  const cajasOptions = cajas.map((c) => ({
    value: c.id,
    label: `${c.nombreCaja} (${c.codigo})`,
  }));

  const handleConfirm = () => {
    if (!cajaId) return;
    onConfirm({
      cajaId: parseInt(cajaId),
      montoApertura: parseFloat(montoApertura) || 0,
    });
  };

  const handleClose = () => {
    setCajaId('');
    setMontoApertura('100.00');
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Abrir Caja" size="sm">
      <div className="space-y-4">
        <p className="text-sm text-gray-600">
          Seleccione la caja e ingrese el monto de apertura para iniciar un nuevo turno.
        </p>

        <Select
          label="Caja Registradora"
          required
          options={cajasOptions}
          placeholder="Seleccione una caja..."
          value={cajaId}
          onChange={(e) => setCajaId(e.target.value)}
        />

        <Input
          label="Monto de Apertura (S/)"
          required
          type="number"
          min={0}
          step={0.01}
          value={montoApertura}
          onChange={(e) => setMontoApertura(e.target.value)}
          placeholder="0.00"
        />

        <div className="flex justify-end gap-2 pt-2">
          <Button variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancelar
          </Button>
          <Button
            onClick={handleConfirm}
            disabled={!cajaId || isLoading}
          >
            {isLoading ? 'Abriendo...' : 'Abrir Caja'}
          </Button>
        </div>
      </div>
    </Modal>
  );
};
