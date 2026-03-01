/**
 * CategoriasGastoModal.jsx
 * ────────────────────────
 * Modal de configuración de categorías de gasto.
 * Permite crear, editar y eliminar categorías desde un mismo lugar.
 */
import { useState } from 'react';
import { Plus, Edit, Trash2, Tag, X } from 'lucide-react';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { Badge } from '@/admin/components/ui/Badge';
import { useCategoriasGasto } from '../hooks/useCajas';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

const TIPO_OPTIONS = [
  { value: 'operativo', label: 'Operativo' },
  { value: 'administrativo', label: 'Administrativo' },
  { value: 'servicio', label: 'Servicio' },
  { value: 'personal', label: 'Personal' },
  { value: 'mantenimiento', label: 'Mantenimiento' },
  { value: 'otro', label: 'Otro' },
];

const TIPO_COLORS = {
  operativo: 'info',
  administrativo: 'warning',
  servicio: 'success',
  personal: 'error',
  mantenimiento: 'warning',
  marketing: 'info',
  tecnologia: 'info',
  otro: 'info',
};

export const CategoriasGastoModal = ({ isOpen, onClose }) => {
  const { negocio } = useAdminAuthStore();
  const {
    categorias,
    isLoading,
    crear,
    actualizar,
    eliminar,
    isCreating,
    isUpdating,
    isDeleting,
  } = useCategoriasGasto();

  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState({ nombre: '', codigo: '', tipo: 'operativo', descripcion: '' });

  const resetForm = () => {
    setShowForm(false);
    setEditId(null);
    setForm({ nombre: '', codigo: '', tipo: 'operativo', descripcion: '' });
  };

  const openCreate = () => {
    resetForm();
    setShowForm(true);
  };

  const openEdit = (cat) => {
    setEditId(cat.id);
    setForm({
      nombre: cat.nombre || '',
      codigo: cat.codigo || '',
      tipo: cat.tipo || 'operativo',
      descripcion: cat.descripcion || '',
    });
    setShowForm(true);
  };

  const handleSave = async () => {
    if (!form.nombre.trim()) return;

    const payload = {
      negocioId: negocio?.id,
      nombre: form.nombre.trim(),
      codigo: form.codigo.trim() || form.nombre.trim().toUpperCase().replace(/\s+/g, '-').slice(0, 20),
      tipo: form.tipo,
      descripcion: form.descripcion.trim() || null,
    };

    if (editId) {
      await actualizar({ id: editId, data: payload });
    } else {
      await crear(payload);
    }
    resetForm();
  };

  const handleDelete = async (id) => {
    await eliminar(id);
  };

  const updateField = (field, value) => setForm((prev) => ({ ...prev, [field]: value }));

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Categorías de Gasto" size="lg">
      <div className="space-y-4">
        <p className="text-sm text-gray-500">
          Configura las categorías que aparecen al registrar un egreso. Esto ayuda a clasificar los gastos
          y facilita los reportes.
        </p>

        {/* Formulario de creación/edición */}
        {showForm ? (
          <div className="bg-gray-50 rounded-lg p-4 border border-gray-200 space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-semibold text-gray-700">
                {editId ? 'Editar Categoría' : 'Nueva Categoría'}
              </h3>
              <button onClick={resetForm} className="text-gray-400 hover:text-gray-600">
                <X size={16} />
              </button>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <Input
                label="Nombre"
                required
                value={form.nombre}
                onChange={(e) => updateField('nombre', e.target.value)}
                placeholder="Ej: Pago de luz"
              />
              <Input
                label="Código"
                value={form.codigo}
                onChange={(e) => updateField('codigo', e.target.value)}
                placeholder="Se genera automáticamente"
              />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
                <select
                  value={form.tipo}
                  onChange={(e) => updateField('tipo', e.target.value)}
                  className="block w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  {TIPO_OPTIONS.map((o) => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                  ))}
                </select>
              </div>
              <Input
                label="Descripción (opcional)"
                value={form.descripcion}
                onChange={(e) => updateField('descripcion', e.target.value)}
                placeholder="Detalle adicional"
              />
            </div>
            <div className="flex justify-end gap-2 pt-1">
              <Button variant="outline" size="sm" onClick={resetForm}>
                Cancelar
              </Button>
              <Button
                size="sm"
                onClick={handleSave}
                disabled={!form.nombre.trim() || isCreating || isUpdating}
              >
                {isCreating || isUpdating ? 'Guardando...' : editId ? 'Actualizar' : 'Crear'}
              </Button>
            </div>
          </div>
        ) : (
          <Button variant="outline" size="sm" onClick={openCreate}>
            <Plus size={16} className="mr-1" />
            Nueva Categoría
          </Button>
        )}

        {/* Lista de categorías */}
        <div className="border border-gray-200 rounded-lg divide-y divide-gray-100 max-h-80 overflow-y-auto">
          {isLoading ? (
            <div className="flex justify-center py-8">
              <div className="w-6 h-6 border-4 border-green-500 border-t-transparent rounded-full animate-spin" />
            </div>
          ) : categorias.length === 0 ? (
            <div className="flex flex-col items-center py-8 text-gray-400">
              <Tag size={32} className="mb-2 opacity-50" />
              <p className="text-sm">No hay categorías configuradas</p>
              <p className="text-xs mt-1">Crea una para clasificar tus egresos</p>
            </div>
          ) : (
            categorias.map((cat) => (
              <div key={cat.id} className="flex items-center justify-between px-4 py-3 hover:bg-gray-50">
                <div className="flex items-center gap-3 min-w-0">
                  <div className="flex-shrink-0">
                    <Badge variant={TIPO_COLORS[cat.tipo] || 'info'}>
                      {cat.tipo || 'otro'}
                    </Badge>
                  </div>
                  <div className="min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">{cat.nombre}</p>
                    {cat.descripcion && (
                      <p className="text-xs text-gray-400 truncate">{cat.descripcion}</p>
                    )}
                  </div>
                </div>
                <div className="flex items-center gap-1 flex-shrink-0 ml-2">
                  <button
                    title="Editar"
                    onClick={() => openEdit(cat)}
                    className="p-1.5 text-gray-400 hover:text-gray-700 rounded"
                  >
                    <Edit size={14} />
                  </button>
                  <button
                    title="Eliminar"
                    onClick={() => handleDelete(cat.id)}
                    disabled={isDeleting}
                    className="p-1.5 text-gray-400 hover:text-red-600 rounded"
                  >
                    <Trash2 size={14} />
                  </button>
                </div>
              </div>
            ))
          )}
        </div>

        <div className="flex justify-end pt-2">
          <Button variant="outline" onClick={onClose}>
            Cerrar
          </Button>
        </div>
      </div>
    </Modal>
  );
};
