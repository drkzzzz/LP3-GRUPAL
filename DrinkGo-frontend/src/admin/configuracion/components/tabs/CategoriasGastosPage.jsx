/**
 * CategoriasGastosPage.jsx
 * ────────────────────────
 * CRUD de categorías de gastos del negocio.
 * Accesible desde Configuración > Categorías de Gastos.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  Loader2,
  Hash,
  X,
} from 'lucide-react';
import { useGastos } from '@/admin/ventas/hooks/useGastos';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { Button } from '@/admin/components/ui/Button';
import { Input } from '@/admin/components/ui/Input';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';

/* ─── Constantes ─── */
const TIPO_OPTIONS = [
  { value: 'operativo', label: 'Operativo' },
  { value: 'administrativo', label: 'Administrativo' },
  { value: 'servicio', label: 'Servicio' },
  { value: 'personal', label: 'Personal' },
  { value: 'marketing', label: 'Marketing' },
  { value: 'mantenimiento', label: 'Mantenimiento' },
  { value: 'tecnologia', label: 'Tecnología' },
  { value: 'otro', label: 'Otro' },
];

const TIPO_COLORS = {
  operativo: 'info',
  administrativo: 'warning',
  servicio: 'success',
  personal: 'info',
  marketing: 'warning',
  mantenimiento: 'info',
  tecnologia: 'info',
  otro: 'info',
};

const TIPO_LABELS = Object.fromEntries(TIPO_OPTIONS.map((t) => [t.value, t.label]));

/* ─── Formulario ─── */
const CategoriaForm = ({ initialData, onSubmit, onCancel, isLoading }) => {
  const isEdit = !!initialData?.id;
  const [form, setForm] = useState({
    nombre: initialData?.nombre || '',
    tipo: initialData?.tipo || 'operativo',
    descripcion: initialData?.descripcion || '',
  });

  const set = (field) => (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }));

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Nombre <span className="text-red-500">*</span>
        </label>
        <Input
          value={form.nombre}
          onChange={set('nombre')}
          placeholder="Ej: Alquiler, Servicios, Sueldos..."
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Tipo
        </label>
        <select
          value={form.tipo}
          onChange={set('tipo')}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        >
          {TIPO_OPTIONS.map((t) => (
            <option key={t.value} value={t.value}>{t.label}</option>
          ))}
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Descripción
        </label>
        <textarea
          value={form.descripcion}
          onChange={set('descripcion')}
          placeholder="Descripción opcional de la categoría..."
          rows={3}
          maxLength={300}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
        />
      </div>

      <div className="flex justify-end gap-2 pt-2 border-t border-gray-100">
        <Button type="button" variant="outline" onClick={onCancel} disabled={isLoading}>
          Cancelar
        </Button>
        <Button type="submit" disabled={isLoading || !form.nombre.trim()}>
          {isLoading ? (
            <><Loader2 size={14} className="animate-spin mr-2" />Guardando...</>
          ) : isEdit ? (
            <><Pencil size={14} className="mr-2" />Actualizar</>
          ) : (
            <><Plus size={14} className="mr-2" />Crear Categoría</>
          )}
        </Button>
      </div>
    </form>
  );
};

/* ─── Página principal ─── */
export const CategoriasGastosPage = () => {
  const { negocioId } = useOutletContext();

  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selected, setSelected] = useState(null);

  const {
    categoriasGasto,
    isLoadingCategorias,
    crearCategoria,
    actualizarCategoria,
    eliminarCategoria,
    isCreatingCategoria,
    isUpdatingCategoria,
    isDeletingCategoria,
  } = useGastos(negocioId);

  /* ─── Filtro ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return categoriasGasto;
    const q = debouncedSearch.toLowerCase();
    return categoriasGasto.filter(
      (c) =>
        c.nombre?.toLowerCase().includes(q) ||
        c.codigo?.toLowerCase().includes(q) ||
        c.tipo?.toLowerCase().includes(q),
    );
  }, [categoriasGasto, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Handlers ─── */
  const openCreate = () => { setEditing(null); setIsFormOpen(true); };
  const openEdit = (item) => { setEditing(item); setIsFormOpen(true); };
  const openDelete = (item) => { setSelected(item); setIsDeleteOpen(true); };

  const handleSubmit = async (formData) => {
    const payload = {
      negocio: { id: negocioId },
      nombre: formData.nombre.trim(),
      tipo: formData.tipo,
      descripcion: formData.descripcion?.trim() || null,
    };

    if (editing) {
      await actualizarCategoria({ ...payload, id: editing.id });
    } else {
      await crearCategoria(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await eliminarCategoria(selected.id);
    setIsDeleteOpen(false);
    setSelected(null);
  };

  /* ─── Columnas ─── */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => <span className="text-gray-400 text-xs">{(page - 1) * pageSize + i + 1}</span>,
    },
    {
      key: 'nombre',
      title: 'Nombre',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.nombre}</p>
          <p className="text-xs text-gray-500 font-mono">{row.codigo}</p>
        </div>
      ),
    },
    {
      key: 'tipo',
      title: 'Tipo',
      width: '140px',
      render: (_, row) => (
        <Badge variant={TIPO_COLORS[row.tipo] || 'info'}>
          {TIPO_LABELS[row.tipo] || row.tipo}
        </Badge>
      ),
    },
    {
      key: 'descripcion',
      title: 'Descripción',
      render: (_, row) => (
        <span className="text-sm text-gray-600 truncate block max-w-xs">
          {row.descripcion || '—'}
        </span>
      ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <div className="flex justify-center gap-1">
          <button
            onClick={() => openEdit(row)}
            className="p-1.5 rounded-md text-gray-500 hover:text-blue-600 hover:bg-blue-50 transition-colors"
            title="Editar"
          >
            <Pencil size={15} />
          </button>
          <button
            onClick={() => openDelete(row)}
            className="p-1.5 rounded-md text-gray-500 hover:text-red-600 hover:bg-red-50 transition-colors"
            title="Eliminar"
          >
            <Trash2 size={15} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Categorías de Gastos</h1>
        <p className="text-gray-600 mt-1">
          Organiza tus gastos en categorías para un mejor control y seguimiento
        </p>
      </div>

      {/* Content */}
      <Card>
        {/* Toolbar */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre, código o tipo..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full pl-9 pr-4 py-2 text-sm border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <Button onClick={openCreate}>
            <Plus size={16} className="mr-2" />
            Nueva Categoría
          </Button>
        </div>

        {/* Table */}
        <Table
          columns={columns}
          data={paginatedData}
          loading={isLoadingCategorias}
          emptyMessage="No se encontraron categorías de gastos"
          pagination={{
            current: page,
            pageSize,
            total: filtered.length,
            onChange: (p, s) => { setPage(p); setPageSize(s); },
          }}
        />
      </Card>

      {/* Form Modal */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => { setIsFormOpen(false); setEditing(null); }}
        title={editing ? 'Editar Categoría' : 'Nueva Categoría de Gasto'}
        size="md"
      >
        <CategoriaForm
          initialData={editing}
          onSubmit={handleSubmit}
          onCancel={() => { setIsFormOpen(false); setEditing(null); }}
          isLoading={isCreatingCategoria || isUpdatingCategoria}
        />
      </Modal>

      {/* Delete Confirm */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setSelected(null); }}
        onConfirm={handleDelete}
        title="Eliminar Categoría"
        message={`¿Está seguro de eliminar la categoría "${selected?.nombre}"? Los gastos asociados no se eliminarán.`}
        confirmText="Eliminar"
        isLoading={isDeletingCategoria}
        variant="danger"
      />
    </div>
  );
};
