/**
 * UnidadesMedidaTab.jsx
 * ─────────────────────
 * CRUD básico de Unidades de Medida.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Plus, Search, Eye, Edit, Trash2, Ruler } from 'lucide-react';
import { useUnidadesMedida } from '../../hooks/useUnidadesMedida';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { UnidadMedidaForm } from '../forms/UnidadMedidaForm';

/* Mapa de colores por tipo de unidad */
const TIPO_BADGE = {
  volumen: 'info',
  peso: 'warning',
  unidad: 'success',
  paquete: 'info',
  otro: 'warning',
};

export const UnidadesMedidaTab = () => {
  const { negocioId } = useOutletContext();
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  const {
    unidades,
    isLoading,
    createUnidad,
    updateUnidad,
    deleteUnidad,
    isCreating,
    isUpdating,
    isDeleting,
  } = useUnidadesMedida();

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return unidades;
    const q = debouncedSearch.toLowerCase();
    return unidades.filter(
      (u) =>
        u.nombre?.toLowerCase().includes(q) ||
        u.codigo?.toLowerCase().includes(q) ||
        u.abreviatura?.toLowerCase().includes(q),
    );
  }, [unidades, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    await createUnidad(data);
    setIsCreateOpen(false);
  };

  const handleView = (unidad) => { setSelected(unidad); setIsDetailOpen(true); };

  const handleEdit = (unidad) => { setSelected(unidad); setIsEditOpen(true); };

  const handleUpdate = async (data) => {
    await updateUnidad(data);
    setIsEditOpen(false);
    setSelected(null);
  };

  const handleDeleteClick = (unidad) => { setSelected(unidad); setIsDeleteOpen(true); };

  const handleDeleteConfirm = async () => {
    await deleteUnidad(selected.id);
    setIsDeleteOpen(false);
    setSelected(null);
  };

  /* ─── Columnas ─── */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => (
        <span className="text-gray-400 text-xs">{(page - 1) * pageSize + i + 1}</span>
      ),
    },
    {
      key: 'codigo',
      title: 'Código',
      width: '100px',
      render: (_, row) => (
        <span className="text-sm font-mono font-medium text-gray-800">{row.codigo}</span>
      ),
    },
    {
      key: 'nombre',
      title: 'Nombre',
      render: (_, row) => (
        <p className="text-sm font-medium text-gray-900">{row.nombre}</p>
      ),
    },
    {
      key: 'abreviatura',
      title: 'Abreviatura',
      width: '100px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.abreviatura}</span>
      ),
    },
    {
      key: 'tipo',
      title: 'Tipo',
      width: '110px',
      align: 'center',
      render: (_, row) => (
        <Badge variant={TIPO_BADGE[row.tipo] || 'info'}>
          {row.tipo || '—'}
        </Badge>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <Badge variant={row.estaActivo ? 'success' : 'error'}>
          {row.estaActivo ? 'Activo' : 'Inactivo'}
        </Badge>
      ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <div className="flex justify-center gap-1">
          <button
            title="Ver detalles"
            onClick={() => handleView(row)}
            className="p-1.5 rounded hover:bg-blue-50 text-blue-500 hover:text-blue-700 transition-colors"
          >
            <Eye size={16} />
          </button>
          <button
            title="Editar"
            onClick={() => handleEdit(row)}
            className="p-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition-colors"
          >
            <Edit size={16} />
          </button>
          <button
            title="Eliminar"
            onClick={() => handleDeleteClick(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
          >
            <Trash2 size={16} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Unidades de Medida</h1>
        <p className="text-gray-600 mt-1">
          Configuración de unidades de medida para los productos del catálogo
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 gap-4">
        <Card className="!p-4 flex items-center gap-3">
          <Ruler size={20} className="text-blue-600" />
          <div>
            <p className="text-sm text-gray-500">Total unidades</p>
            <p className="text-xl font-bold">{unidades.length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <Ruler size={20} className="text-green-600" />
          <div>
            <p className="text-sm text-gray-500">Activas</p>
            <p className="text-xl font-bold">{unidades.filter((u) => u.estaActivo).length}</p>
          </div>
        </Card>
      </div>

      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar unidad de medida..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nueva Unidad
          </Button>
        </div>

        <Table
          columns={columns}
          data={paginatedData}
          loading={isLoading}
          pagination={{
            current: page,
            pageSize,
            total: filtered.length,
            onChange: (newPage, newSize) => { setPage(newPage); setPageSize(newSize); },
          }}
        />
      </Card>

      {/* Crear */}
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Nueva Unidad de Medida" size="md">
        <UnidadMedidaForm
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* Editar */}
      <Modal isOpen={isEditOpen} onClose={() => { setIsEditOpen(false); setSelected(null); }} title="Editar Unidad de Medida" size="md">
        {selected && (
          <UnidadMedidaForm
            initialData={selected}
            negocioId={negocioId}
            onSubmit={handleUpdate}
            onCancel={() => { setIsEditOpen(false); setSelected(null); }}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* Detalle */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title="Detalle de Unidad de Medida"
        size="md"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-lg bg-blue-100 flex items-center justify-center text-blue-700 text-xl font-bold">
                {selected.abreviatura || selected.codigo?.charAt(0)?.toUpperCase()}
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{selected.nombre}</h3>
                <p className="text-sm text-gray-500">Código: {selected.codigo}</p>
                <Badge variant={selected.estaActivo ? 'success' : 'error'}>
                  {selected.estaActivo ? 'Activo' : 'Inactivo'}
                </Badge>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Código</p>
                <p className="font-medium font-mono">{selected.codigo}</p>
              </div>
              <div>
                <p className="text-gray-500">Abreviatura</p>
                <p className="font-medium">{selected.abreviatura}</p>
              </div>
              <div>
                <p className="text-gray-500">Tipo</p>
                <Badge variant={TIPO_BADGE[selected.tipo] || 'info'}>
                  {selected.tipo || '—'}
                </Badge>
              </div>
              <div>
                <p className="text-gray-500">Estado</p>
                <p className="font-medium">{selected.estaActivo ? 'Activo' : 'Inactivo'}</p>
              </div>
            </div>
          </div>
        )}
      </Modal>

      {/* Eliminar */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setSelected(null); }}
        onConfirm={handleDeleteConfirm}
        title="Eliminar Unidad"
        message={`¿Está seguro de eliminar la unidad "${selected?.nombre}"?`}
        confirmText="Eliminar"
        isLoading={isDeleting}
      />
    </div>
  );
};
