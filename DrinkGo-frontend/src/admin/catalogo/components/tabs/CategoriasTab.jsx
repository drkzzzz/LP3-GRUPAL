/**
 * CategoriasTab.jsx
 * ─────────────────
 * CRUD completo de Categorías con tabla, búsqueda, modales.
 */
import { useState, useMemo } from 'react';
import { Plus, Search, Eye, Edit, Trash2, FolderTree, CheckCircle, XCircle, Wine } from 'lucide-react';
import { useCategorias } from '../../hooks/useCategorias';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { CategoriaForm } from '../forms/CategoriaForm';

export const CategoriasTab = ({ context }) => {
  const { negocioId } = context;
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
    categorias,
    isLoading,
    createCategoria,
    updateCategoria,
    deleteCategoria,
    isCreating,
    isUpdating,
    isDeleting,
  } = useCategorias();

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return categorias;
    const q = debouncedSearch.toLowerCase();
    return categorias.filter(
      (c) =>
        c.nombre?.toLowerCase().includes(q) ||
        c.descripcion?.toLowerCase().includes(q),
    );
  }, [categorias, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    await createCategoria(data);
    setIsCreateOpen(false);
  };

  const handleEdit = (cat) => {
    setSelected(cat);
    setIsEditOpen(true);
  };

  const handleUpdate = async (data) => {
    await updateCategoria(data);
    setIsEditOpen(false);
    setSelected(null);
  };

  const handleView = (cat) => {
    setSelected(cat);
    setIsDetailOpen(true);
  };

  const handleDeleteClick = (cat) => {
    setSelected(cat);
    setIsDeleteOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await deleteCategoria(selected.id);
    } catch {
      /* Error ya manejado por el hook (toast) */
    }
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
      key: 'nombre',
      title: 'Nombre',
      render: (_, row) => (
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg bg-purple-100 flex items-center justify-center text-purple-700 font-bold text-xs flex-shrink-0">
            {row.nombre?.charAt(0)?.toUpperCase()}
          </div>
          <div>
            <p className="text-sm font-medium text-gray-900">{row.nombre}</p>
            <p className="text-xs text-gray-500">{row.slug}</p>
          </div>
        </div>
      ),
    },
    {
      key: 'alcohol',
      title: 'Alcohol',
      width: '90px',
      align: 'center',
      render: (_, row) => (
        row.esAlcoholica ? (
          <span className="inline-flex items-center gap-1 text-xs font-medium text-amber-700 bg-amber-50 px-2 py-0.5 rounded-full">
            <Wine size={12} />
            Sí
          </span>
        ) : (
          <span className="text-xs text-gray-400">No</span>
        )
      ),
    },
    {
      key: 'visible',
      title: 'Tienda online',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <Badge variant={row.visibleTiendaOnline ? 'success' : 'warning'}>
          {row.visibleTiendaOnline ? 'Visible' : 'Oculto'}
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
    <div className="space-y-4">

      {/* Stats rápidos */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <Card className="!p-4 flex items-center gap-3">
          <FolderTree size={20} className="text-purple-600" />
          <div>
            <p className="text-sm text-gray-500">Total categorías</p>
            <p className="text-xl font-bold">{categorias.length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <CheckCircle size={20} className="text-green-600" />
          <div>
            <p className="text-sm text-gray-500">Activas</p>
            <p className="text-xl font-bold">{categorias.filter((c) => c.estaActivo).length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <XCircle size={20} className="text-red-500" />
          <div>
            <p className="text-sm text-gray-500">Inactivas</p>
            <p className="text-xl font-bold">{categorias.filter((c) => !c.estaActivo).length}</p>
          </div>
        </Card>
      </div>

      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar categoría..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nueva Categoría
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
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Nueva Categoría" size="lg">
        <CategoriaForm
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* Editar */}
      <Modal isOpen={isEditOpen} onClose={() => { setIsEditOpen(false); setSelected(null); }} title="Editar Categoría" size="lg">
        {selected && (
          <CategoriaForm
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
        title="Detalle de Categoría"
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-lg bg-purple-100 flex items-center justify-center text-purple-700 text-2xl font-bold">
                {selected.nombre?.charAt(0)?.toUpperCase()}
              </div>
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{selected.nombre}</h3>
                <p className="text-sm text-gray-500">Slug: {selected.slug}</p>
                <Badge variant={selected.estaActivo ? 'success' : 'error'}>
                  {selected.estaActivo ? 'Activo' : 'Inactivo'}
                </Badge>
              </div>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Categoría con alcohol</p>
                <p className="font-medium">{selected.esAlcoholica ? 'Sí' : 'No'}</p>
              </div>
              <div>
                <p className="text-gray-500">Tienda online</p>
                <p className="font-medium">{selected.visibleTiendaOnline ? 'Visible' : 'Oculto'}</p>
              </div>
              <div>
                <p className="text-gray-500">Estado</p>
                <p className="font-medium">{selected.estaActivo ? 'Activo' : 'Inactivo'}</p>
              </div>
            </div>

            {selected.descripcion && (
              <div>
                <p className="text-sm text-gray-500">Descripción</p>
                <p className="text-sm text-gray-700 mt-1">{selected.descripcion}</p>
              </div>
            )}
          </div>
        )}
      </Modal>

      {/* Eliminar */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setSelected(null); }}
        onConfirm={handleDeleteConfirm}
        title="Eliminar Categoría"
        message={`¿Está seguro de eliminar la categoría "${selected?.nombre}"? Esta acción desactivará la categoría.`}
        confirmText="Eliminar"
        isLoading={isDeleting}
      />
    </div>
  );
};
