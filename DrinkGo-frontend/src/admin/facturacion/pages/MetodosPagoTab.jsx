/**
 * MetodosPagoTab.jsx
 * ──────────────────
 * Métodos de pago disponibles para el negocio.
 *
 * Lógica simplificada: el usuario selecciona de una lista
 * predefinida (Efectivo, Yape, Plin, etc.) y solo configura
 * si está disponible en POS, tienda online y el orden.
 * El nombre, código y tipo se deducen automáticamente.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Plus, Edit, Trash2, CreditCard, ToggleLeft, ToggleRight, X } from 'lucide-react';
import {
  useMetodosPago,
  useCrearMetodoPago,
  useActualizarMetodoPago,
  useEliminarMetodoPago,
} from '../hooks/useFacturacion';

/* ─── Métodos predefinidos ─── */
const METODOS_PRESET = [
  { nombre: 'Efectivo', codigo: 'efectivo', tipo: 'efectivo' },
  { nombre: 'Tarjeta de Crédito', codigo: 'tarjeta_credito', tipo: 'tarjeta_credito' },
  { nombre: 'Tarjeta de Débito', codigo: 'tarjeta_debito', tipo: 'tarjeta_debito' },
  { nombre: 'Yape', codigo: 'yape', tipo: 'yape' },
  { nombre: 'Plin', codigo: 'plin', tipo: 'plin' },
  { nombre: 'Transferencia Bancaria', codigo: 'transferencia_bancaria', tipo: 'transferencia_bancaria' },
  { nombre: 'QR', codigo: 'qr', tipo: 'qr' },
  { nombre: 'Otro', codigo: 'otro', tipo: 'otro' },
];

export const MetodosPagoTab = () => {
  const { negocioId } = useOutletContext();
  const { data: metodos = [], isLoading } = useMetodosPago(negocioId);
  const crearMetodo = useCrearMetodoPago();
  const actualizarMetodo = useActualizarMetodoPago();
  const eliminarMetodo = useEliminarMetodoPago();

  const [showForm, setShowForm] = useState(false);
  const [editingMetodo, setEditingMetodo] = useState(null);
  const [formData, setFormData] = useState({
    presetIndex: 0,
    disponiblePos: true,
    disponibleTiendaOnline: false,
    orden: 0,
  });

  /* Métodos ya agregados (por código) */
  const codigosUsados = useMemo(() => new Set(metodos.map((m) => m.codigo)), [metodos]);

  /* Presets disponibles (excluir los ya agregados al crear) */
  const presetsDisponibles = useMemo(() => {
    if (editingMetodo) return METODOS_PRESET;
    return METODOS_PRESET.filter((p) => !codigosUsados.has(p.codigo));
  }, [codigosUsados, editingMetodo]);

  const resetForm = () => {
    setFormData({ presetIndex: 0, disponiblePos: true, disponibleTiendaOnline: false, orden: 0 });
    setEditingMetodo(null);
    setShowForm(false);
  };

  const openCreate = () => {
    if (presetsDisponibles.length === 0) return;
    setEditingMetodo(null);
    setFormData({
      presetIndex: 0,
      disponiblePos: true,
      disponibleTiendaOnline: false,
      orden: metodos.length,
    });
    setShowForm(true);
  };

  const handleEdit = (metodo) => {
    setEditingMetodo(metodo);
    setFormData({
      presetIndex: -1,
      disponiblePos: metodo.disponiblePos ?? true,
      disponibleTiendaOnline: metodo.disponibleTiendaOnline ?? false,
      orden: metodo.orden || 0,
    });
    setShowForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingMetodo) {
        await actualizarMetodo.mutateAsync({
          id: editingMetodo.id,
          data: {
            disponiblePos: formData.disponiblePos,
            disponibleTiendaOnline: formData.disponibleTiendaOnline,
            orden: formData.orden,
          },
        });
      } else {
        const preset = presetsDisponibles[formData.presetIndex];
        if (!preset) return;
        await crearMetodo.mutateAsync({
          negocio: { id: negocioId },
          nombre: preset.nombre,
          codigo: preset.codigo,
          tipo: preset.tipo,
          disponiblePos: formData.disponiblePos,
          disponibleTiendaOnline: formData.disponibleTiendaOnline,
          orden: formData.orden,
        });
      }
      resetForm();
    } catch (err) {
      console.error('Error al guardar método de pago:', err);
    }
  };

  const handleDelete = async (id) => {
    try {
      await eliminarMetodo.mutateAsync(id);
    } catch (err) {
      console.error('Error al eliminar método de pago:', err);
    }
  };

  return (
    <div className="space-y-6">
      {/* ─── Header ─── */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Métodos de Pago</h1>
        <p className="text-gray-600 mt-1">
          Configura los métodos de pago disponibles para ventas POS y tienda online
        </p>
      </div>

      {/* ─── Card ─── */}
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        {/* Contador + Botón */}
        <div className="flex items-center justify-between mb-5">
          <div className="flex items-center gap-2 text-gray-600">
            <CreditCard size={18} className="text-gray-400" />
            <span className="text-sm">{metodos.length} método(s) configurado(s)</span>
          </div>
          <button
            onClick={openCreate}
            disabled={presetsDisponibles.length === 0}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <Plus size={18} />
            Agregar Método
          </button>
        </div>

        {/* ─── Tabla ─── */}
        {isLoading ? (
          <div className="text-center py-12 text-gray-500">Cargando métodos de pago...</div>
        ) : metodos.length === 0 ? (
          <div>
            <table className="w-full text-sm">
              <thead>
                <tr className="border-y border-gray-200 bg-gray-50/50">
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[50px]">#</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Método</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">POS</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Online</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Orden</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Estado</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[120px]">Acciones</th>
                </tr>
              </thead>
            </table>
            <div className="text-center py-10 text-gray-400">
              No hay métodos de pago configurados
            </div>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-y border-gray-200 bg-gray-50/50">
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[50px]">#</th>
                  <th className="text-left py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Método</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">POS</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Online</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Orden</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider">Estado</th>
                  <th className="text-center py-3 px-3 text-gray-500 font-semibold text-xs uppercase tracking-wider w-[120px]">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {metodos.map((metodo, idx) => (
                  <tr key={metodo.id} className="border-b border-gray-100 hover:bg-gray-50/50">
                    <td className="py-3 px-3 text-gray-400">{idx + 1}</td>
                    <td className="py-3 px-3">
                      <span className="font-medium text-gray-900">{metodo.nombre}</span>
                    </td>
                    <td className="py-3 px-3 text-center">
                      {metodo.disponiblePos ? (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700">Sí</span>
                      ) : (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-500">No</span>
                      )}
                    </td>
                    <td className="py-3 px-3 text-center">
                      {metodo.disponibleTiendaOnline ? (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-700">Sí</span>
                      ) : (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-500">No</span>
                      )}
                    </td>
                    <td className="py-3 px-3 text-center text-gray-600">{metodo.orden}</td>
                    <td className="py-3 px-3 text-center">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border ${
                        metodo.estaActivo
                          ? 'bg-green-100 text-green-700 border-green-300'
                          : 'bg-red-100 text-red-700 border-red-300'
                      }`}>
                        {metodo.estaActivo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td className="py-3 px-3">
                      <div className="flex justify-center gap-2">
                        <button title="Editar" onClick={() => handleEdit(metodo)} className="text-gray-500 hover:text-gray-700">
                          <Edit size={16} />
                        </button>
                        <button title="Eliminar" onClick={() => handleDelete(metodo.id)} className="text-red-500 hover:text-red-700">
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ─── Modal Agregar / Editar ─── */}
      {showForm && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4" onClick={() => resetForm()}>
          <div className="bg-white rounded-xl max-w-sm w-full p-6" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-5">
              <h2 className="text-lg font-bold text-gray-900">
                {editingMetodo ? `Editar: ${editingMetodo.nombre}` : 'Agregar Método de Pago'}
              </h2>
              <button onClick={resetForm} className="text-gray-400 hover:text-gray-600">
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-5">
              {/* Selector de método (solo al crear) */}
              {!editingMetodo && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Método de pago <span className="text-red-500">*</span>
                  </label>
                  <select
                    value={formData.presetIndex}
                    onChange={(e) => setFormData({ ...formData, presetIndex: Number(e.target.value) })}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 bg-white"
                  >
                    {presetsDisponibles.map((p, i) => (
                      <option key={p.codigo} value={i}>{p.nombre}</option>
                    ))}
                  </select>
                </div>
              )}

              {/* Canales */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Disponible en</label>
                <div className="flex items-center gap-6">
                  <label className="flex items-center gap-2 cursor-pointer select-none">
                    <input
                      type="checkbox"
                      checked={formData.disponiblePos}
                      onChange={(e) => setFormData({ ...formData, disponiblePos: e.target.checked })}
                      className="rounded border-gray-300 text-green-600 focus:ring-green-500"
                    />
                    <span className="text-sm text-gray-700">POS (tienda física)</span>
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer select-none">
                    <input
                      type="checkbox"
                      checked={formData.disponibleTiendaOnline}
                      onChange={(e) => setFormData({ ...formData, disponibleTiendaOnline: e.target.checked })}
                      className="rounded border-gray-300 text-green-600 focus:ring-green-500"
                    />
                    <span className="text-sm text-gray-700">Tienda online</span>
                  </label>
                </div>
              </div>

              {/* Orden */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Orden de aparición
                </label>
                <input
                  type="number"
                  value={formData.orden}
                  onChange={(e) => setFormData({ ...formData, orden: parseInt(e.target.value) || 0 })}
                  min={0}
                  className="w-24 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                />
                <p className="text-xs text-gray-400 mt-1">Menor número = aparece primero</p>
              </div>

              {/* Botones */}
              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={resetForm} className="px-4 py-2 text-sm bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300">
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={crearMetodo.isPending || actualizarMetodo.isPending}
                  className="px-4 py-2 text-sm bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
                >
                  {(crearMetodo.isPending || actualizarMetodo.isPending) ? 'Guardando...' : 'Guardar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
