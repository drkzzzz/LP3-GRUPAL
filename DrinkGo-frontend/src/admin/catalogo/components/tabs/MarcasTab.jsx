/**
 * MarcasTab.jsx
 * ─────────────
 * CRUD completo de Marcas.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Plus, Search, Eye, Edit, Trash2, Award, XCircle } from 'lucide-react';
import { useMarcas } from '../../hooks/useMarcas';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { MarcaForm } from '../forms/MarcaForm';

export const MarcasTab = () => {
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
    marcas,
    isLoading,
    createMarca,
    updateMarca,
    deleteMarca,
    isCreating,
    isUpdating,
    isDeleting,
  } = useMarcas();

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return marcas;
    const q = debouncedSearch.toLowerCase();
    return marcas.filter(
      (m) =>
        m.nombre?.toLowerCase().includes(q) ||
        m.paisOrigen?.toLowerCase().includes(q),
    );
  }, [marcas, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    await createMarca(data);
    setIsCreateOpen(false);
  };

  const handleView = (marca) => { setSelected(marca); setIsDetailOpen(true); };

  const handleEdit = (marca) => { setSelected(marca); setIsEditOpen(true); };

  const handleUpdate = async (data) => {
    await updateMarca(data);
    setIsEditOpen(false);
    setSelected(null);
  };

  const handleDeleteClick = (marca) => { setSelected(marca); setIsDeleteOpen(true); };

  const handleDeleteConfirm = async () => {
    await deleteMarca(selected.id);
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
      key: 'marca',
      title: 'Marca',
      render: (_, row) => (
        <div className="flex items-center gap-3">
          {row.urlLogo ? (
            <img src={row.urlLogo} alt={row.nombre} className="w-8 h-8 rounded-lg object-contain flex-shrink-0 bg-gray-50" />
          ) : (
            <div className="w-8 h-8 rounded-lg bg-orange-100 flex items-center justify-center text-orange-700 font-bold text-xs flex-shrink-0">
              {row.nombre?.charAt(0)?.toUpperCase()}
            </div>
          )}
          <div>
            <p className="text-sm font-medium text-gray-900">{row.nombre}</p>
            <p className="text-xs text-gray-500">{row.slug}</p>
          </div>
        </div>
      ),
    },
    {
      key: 'pais',
      title: 'País de origen',
      width: '140px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{row.paisOrigen || '—'}</span>
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
        <h1 className="text-2xl font-bold text-gray-900">Marcas</h1>
        <p className="text-gray-600 mt-1">
          Administración de marcas de productos, país de origen y logotipos
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <Card className="!p-4 flex items-center gap-3">
          <Award size={20} className="text-orange-600" />
          <div>
            <p className="text-sm text-gray-500">Total marcas</p>
            <p className="text-xl font-bold">{marcas.length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <Award size={20} className="text-green-600" />
          <div>
            <p className="text-sm text-gray-500">Activas</p>
            <p className="text-xl font-bold">{marcas.filter((m) => m.estaActivo).length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <XCircle size={20} className="text-red-500" />
          <div>
            <p className="text-sm text-gray-500">Inactivas</p>
            <p className="text-xl font-bold">{marcas.filter((m) => !m.estaActivo).length}</p>
          </div>
        </Card>
      </div>

      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por nombre o país..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nueva Marca
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
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Nueva Marca" size="lg">
        <MarcaForm
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* Editar */}
      <Modal isOpen={isEditOpen} onClose={() => { setIsEditOpen(false); setSelected(null); }} title="Editar Marca" size="lg">
        {selected && (
          <MarcaForm
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
        title="Detalle de Marca"
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              {selected.urlLogo ? (
                <img
                  src={selected.urlLogo}
                  alt={selected.nombre}
                  className="w-16 h-16 rounded-lg object-contain bg-gray-50"
                />
              ) : (
                <div className="w-16 h-16 rounded-lg bg-orange-100 flex items-center justify-center text-orange-700 text-2xl font-bold">
                  {selected.nombre?.charAt(0)?.toUpperCase()}
                </div>
              )}
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{selected.nombre}</h3>
                <p className="text-sm text-gray-500">Slug: {selected.slug}</p>
                <Badge variant={selected.estaActivo ? 'success' : 'error'}>
                  {selected.estaActivo ? 'Activo' : 'Inactivo'}
                </Badge>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500">País de origen</p>
                <p className="font-medium">{selected.paisOrigen || '—'}</p>
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

            {selected.urlLogo && (
              <div>
                <p className="text-sm text-gray-500 mb-2">Logo</p>
                <img
                  src={selected.urlLogo}
                  alt={selected.nombre}
                  className="w-32 h-32 rounded-lg object-contain bg-gray-50 border"
                />
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
        title="Eliminar Marca"
        message={`¿Está seguro de eliminar la marca "${selected?.nombre}"?`}
        confirmText="Eliminar"
        isLoading={isDeleting}
      />
    </div>
  );
};
