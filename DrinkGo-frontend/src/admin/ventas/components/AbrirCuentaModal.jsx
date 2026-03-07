/**
 * AbrirCuentaModal.jsx
 * ────────────────────
 * RF-VTA-011: Modal para abrir una cuenta de mesa.
 * Permite seleccionar mesero, número de comensales y cliente opcional.
 */
import { useState } from 'react';
import { Users, User, UserCheck } from 'lucide-react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';

export const AbrirCuentaModal = ({
  isOpen,
  onClose,
  mesa,
  meseros,
  clientes,
  onConfirm,
  isLoading,
}) => {
  const [meseroId, setMeseroId] = useState('');
  const [numComensales, setNumComensales] = useState(1);
  const [clienteId, setClienteId] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!meseroId) return;
    onConfirm({ meseroId: Number(meseroId), numComensales, clienteId: clienteId ? Number(clienteId) : null });
  };

  const handleClose = () => {
    setMeseroId('');
    setNumComensales(1);
    setClienteId('');
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={`Abrir cuenta — ${mesa?.nombre ?? ''}`}
      size="sm"
      footer={
        <>
          <Button variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancelar
          </Button>
          <Button type="submit" form="abrir-cuenta-form" disabled={!meseroId || isLoading}>
            {isLoading ? 'Abriendo...' : 'Abrir cuenta'}
          </Button>
        </>
      }
    >
      <form id="abrir-cuenta-form" onSubmit={handleSubmit} className="space-y-4">
        {/* Mesero */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Mesero <span className="text-red-500">*</span>
          </label>
          <div className="relative">
            <UserCheck size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <select
              value={meseroId}
              onChange={(e) => setMeseroId(e.target.value)}
              required
              className="block w-full border border-gray-300 rounded-lg pl-9 pr-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="">Seleccionar mesero...</option>
              {meseros.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.nombres} {u.apellidos}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Número de comensales */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            <Users size={14} className="inline mr-1" />
            Número de comensales
          </label>
          <Input
            type="number"
            min={1}
            max={mesa?.capacidad ?? 20}
            value={numComensales}
            onChange={(e) => setNumComensales(Number(e.target.value))}
          />
          {mesa?.capacidad && (
            <p className="text-xs text-gray-500 mt-1">Capacidad máxima: {mesa.capacidad}</p>
          )}
        </div>

        {/* Cliente opcional */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            <User size={14} className="inline mr-1" />
            Cliente (opcional)
          </label>
          <div className="relative">
            <select
              value={clienteId}
              onChange={(e) => setClienteId(e.target.value)}
              className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="">Sin cliente asignado</option>
              {clientes.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombres} {c.apellidos ?? ''} {c.numeroDocumento ? `(${c.numeroDocumento})` : ''}
                </option>
              ))}
            </select>
          </div>
        </div>
      </form>
    </Modal>
  );
};
