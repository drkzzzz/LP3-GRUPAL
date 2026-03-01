/**
 * MovimientoCajaModal.jsx
 * ───────────────────────
 * Modal para registrar ingresos/egresos manuales en la sesión.
 * Si es egreso, permite seleccionar una categoría de gasto predefinida.
 */
import { useState, useEffect } from 'react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';

const TIPO_MOVIMIENTO_OPTIONS = [
  { value: 'ingreso_otro', label: 'Ingreso manual' },
  { value: 'egreso_gasto', label: 'Egreso / Gasto' },
  { value: 'egreso_otro', label: 'Egreso otro' },
];

export const MovimientoCajaModal = ({
  isOpen,
  onClose,
  onConfirm,
  sesionCajaId,
  isLoading = false,
  categorias = [],
}) => {
  const [tipoMovimiento, setTipoMovimiento] = useState('ingreso_otro');
  const [monto, setMonto] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [categoriaGastoId, setCategoriaGastoId] = useState('');

  const isEgreso = tipoMovimiento.startsWith('egreso');
  const isEgresoGasto = tipoMovimiento === 'egreso_gasto';

  useEffect(() => {
    if (!isEgresoGasto) setCategoriaGastoId('');
  }, [tipoMovimiento, isEgresoGasto]);

  const handleConfirm = () => {
    if (!monto || parseFloat(monto) <= 0 || !descripcion.trim()) return;
    const payload = {
      sesionCajaId,
      tipo: tipoMovimiento,
      monto: parseFloat(monto),
      concepto: descripcion.trim(),
    };
    if (isEgresoGasto && categoriaGastoId) {
      payload.categoriaGastoId = parseInt(categoriaGastoId);
    }
    onConfirm(payload);
  };

  const handleClose = () => {
    setTipoMovimiento('ingreso_otro');
    setMonto('');
    setDescripcion('');
    setCategoriaGastoId('');
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

        {isEgresoGasto && categorias.length > 0 && (
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Categoría de Gasto
            </label>
            <select
              value={categoriaGastoId}
              onChange={(e) => setCategoriaGastoId(e.target.value)}
              className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm
                         focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
            >
              <option value="">Sin categoría</option>
              {categorias.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre}</option>
              ))}
            </select>
          </div>
        )}

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
            {isEgresoGasto ? 'Justificación' : 'Descripción'} <span className="text-red-500">*</span>
          </label>
          <textarea
            value={descripcion}
            onChange={(e) => setDescripcion(e.target.value)}
            rows={2}
            placeholder={isEgresoGasto ? 'Detalle del gasto...' : 'Motivo del movimiento...'}
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
