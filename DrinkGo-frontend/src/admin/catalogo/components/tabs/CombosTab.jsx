/**
 * CombosTab.jsx
 * ─────────────
 * CRUD de Combos con vista de detalles (productos del combo).
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Plus, Search, Edit, Trash2, Eye, Gift, Percent } from 'lucide-react';
import { useCombos } from '../../hooks/useCombos';
import { useProductos } from '../../hooks/useProductos';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { ComboForm } from '../forms/ComboForm';

export const CombosTab = () => {
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
    combos,
    detalles: allDetalles,
    isLoading,
    createCombo,
    updateCombo,
    deleteCombo,
    createDetalle,
    updateDetalle,
    deleteDetalle,
    getDetallesForCombo,
    isCreating,
    isUpdating,
    isDeleting,
  } = useCombos(negocioId);

  const { productos } = useProductos(negocioId);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return combos;
    const q = debouncedSearch.toLowerCase();
    return combos.filter(
      (c) =>
        c.nombre?.toLowerCase().includes(q) ||
        c.descripcion?.toLowerCase().includes(q),
    );
  }, [combos, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Helpers ─── */
  const isComboActive = (combo) => {
    if (!combo.estaActivo) return false;
    const now = new Date();
    if (combo.fechaInicio && new Date(combo.fechaInicio) > now) return false;
    if (combo.fechaFin && new Date(combo.fechaFin) < now) return false;
    return true;
  };

  const getComboDetalles = (comboId) => getDetallesForCombo(comboId);

  const getProductName = (productoId) =>
    productos.find((p) => p.id === productoId)?.nombre || `Producto #${productoId}`;

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    const { detalles, ...comboData } = data;
    const combo = await createCombo(comboData);
    // Crear cada detalle asociado al combo recién creado
    if (detalles?.length && combo?.id) {
      for (const det of detalles) {
        await createDetalle({
          combo: { id: combo.id },
          producto: { id: det.productoId },
          cantidad: det.cantidad,
          precioUnitario: det.precioUnitario,
        });
      }
    }
    setIsCreateOpen(false);
  };

  const handleEdit = (combo) => { setSelected(combo); setIsEditOpen(true); };

  const handleUpdate = async (data) => {
    const { detalles: newDetalles, ...comboData } = data;
    await updateCombo(comboData);

    // Sincronizar detalles: borrar eliminados, crear nuevos, actualizar existentes
    const existingDetalles = getDetallesForCombo(comboData.id);
    const newDetalleIds = new Set((newDetalles || []).filter((d) => d.id).map((d) => d.id));

    // Eliminar los que ya no están
    for (const existing of existingDetalles) {
      if (!newDetalleIds.has(existing.id)) {
        await deleteDetalle(existing.id);
      }
    }

    // Crear nuevos o actualizar existentes
    for (const det of newDetalles || []) {
      if (det.id) {
        // Actualizar existente
        await updateDetalle({
          id: det.id,
          combo: { id: comboData.id },
          producto: { id: det.productoId },
          cantidad: det.cantidad,
          precioUnitario: det.precioUnitario,
        });
      } else {
        // Crear nuevo
        await createDetalle({
          combo: { id: comboData.id },
          producto: { id: det.productoId },
          cantidad: det.cantidad,
          precioUnitario: det.precioUnitario,
        });
      }
    }

    setIsEditOpen(false);
    setSelected(null);
  };

  const handleView = (combo) => { setSelected(combo); setIsDetailOpen(true); };

  const handleDeleteClick = (combo) => { setSelected(combo); setIsDeleteOpen(true); };

  const handleDeleteConfirm = async () => {
    await deleteCombo(selected.id);
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
      key: 'combo',
      title: 'Combo',
      render: (_, row) => (
        <div className="flex items-center gap-3">
          {row.urlImagen ? (
            <img src={row.urlImagen} alt={row.nombre} className="w-9 h-9 rounded-lg object-cover flex-shrink-0" />
          ) : (
            <div className="w-9 h-9 rounded-lg bg-pink-100 flex items-center justify-center text-pink-700 font-bold text-sm flex-shrink-0">
              <Gift size={18} />
            </div>
          )}
          <div className="min-w-0">
            <p className="text-sm font-medium text-gray-900 truncate">{row.nombre}</p>
            <p className="text-xs text-gray-500 truncate">{row.descripcion || '—'}</p>
          </div>
        </div>
      ),
    },
    {
      key: 'precios',
      title: 'Precio Regular → Combo',
      width: '200px',
      render: (_, row) => (
        <div className="text-sm">
          <span className="text-gray-400 line-through mr-2">{formatCurrency(row.precioRegular)}</span>
          <span className="text-green-600 font-semibold">{formatCurrency(row.precioCombo)}</span>
        </div>
      ),
    },
    {
      key: 'descuento',
      title: 'Dcto.',
      width: '70px',
      align: 'center',
      render: (_, row) => (
        <Badge variant="info">{row.porcentajeDescuento ?? 0}%</Badge>
      ),
    },
    {
      key: 'vigencia',
      title: 'Vigencia',
      width: '160px',
      render: (_, row) => (
        <div className="text-xs text-gray-600">
          <p>{row.fechaInicio ? formatDate(row.fechaInicio) : '—'}</p>
          <p>{row.fechaFin ? formatDate(row.fechaFin) : 'Sin fin'}</p>
        </div>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <Badge variant={isComboActive(row) ? 'success' : 'warning'}>
          {isComboActive(row) ? 'Vigente' : 'No vigente'}
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
            title="Ver productos"
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
        <h1 className="text-2xl font-bold text-gray-900">Combos</h1>
        <p className="text-gray-600 mt-1">
          Creación y gestión de combos de productos con precios especiales
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <Card className="!p-4 flex items-center gap-3">
          <Gift size={20} className="text-pink-600" />
          <div>
            <p className="text-sm text-gray-500">Total combos</p>
            <p className="text-xl font-bold">{combos.length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <Percent size={20} className="text-green-600" />
          <div>
            <p className="text-sm text-gray-500">Vigentes</p>
            <p className="text-xl font-bold">{combos.filter(isComboActive).length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <Gift size={20} className="text-gray-400" />
          <div>
            <p className="text-sm text-gray-500">No vigentes</p>
            <p className="text-xl font-bold">{combos.filter((c) => !isComboActive(c)).length}</p>
          </div>
        </Card>
      </div>

      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar combo..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nuevo Combo
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
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Nuevo Combo" size="lg">
        <ComboForm
          negocioId={negocioId}
          productos={productos}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* Editar */}
      <Modal isOpen={isEditOpen} onClose={() => { setIsEditOpen(false); setSelected(null); }} title="Editar Combo" size="lg">
        {selected && (
          <ComboForm
            initialData={selected}
            initialDetalles={getDetallesForCombo(selected.id)}
            productos={productos}
            negocioId={negocioId}
            onSubmit={handleUpdate}
            onCancel={() => { setIsEditOpen(false); setSelected(null); }}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* Detalle: productos del combo */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title={`Productos de "${selected?.nombre}"`}
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500">Precio combo</p>
                <p className="text-lg font-bold text-green-600">{formatCurrency(selected.precioCombo)}</p>
              </div>
              <Badge variant={isComboActive(selected) ? 'success' : 'warning'}>
                {isComboActive(selected) ? 'Vigente' : 'No vigente'}
              </Badge>
            </div>

            <div className="border rounded-lg overflow-hidden">
              <table className="w-full text-sm">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-2 text-left font-medium text-gray-600">Producto</th>
                    <th className="px-4 py-2 text-center font-medium text-gray-600">Cantidad</th>
                    <th className="px-4 py-2 text-right font-medium text-gray-600">Precio unitario</th>
                    <th className="px-4 py-2 text-right font-medium text-gray-600">Subtotal</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {getComboDetalles(selected.id).length > 0 ? (
                    getComboDetalles(selected.id).map((det) => (
                      <tr key={det.id}>
                        <td className="px-4 py-2">
                          {det.producto?.nombre || getProductName(det.productoId)}
                        </td>
                        <td className="px-4 py-2 text-center">{det.cantidad}</td>
                        <td className="px-4 py-2 text-right">{formatCurrency(det.precioUnitario)}</td>
                        <td className="px-4 py-2 text-right font-medium">
                          {formatCurrency((det.precioUnitario ?? 0) * (det.cantidad ?? 0))}
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="4" className="px-4 py-6 text-center text-gray-400">
                        No hay productos asignados a este combo
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </Modal>

      {/* Eliminar */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => { setIsDeleteOpen(false); setSelected(null); }}
        onConfirm={handleDeleteConfirm}
        title="Eliminar Combo"
        message={`¿Está seguro de eliminar el combo "${selected?.nombre}"?`}
        confirmText="Eliminar"
        isLoading={isDeleting}
      />
    </div>
  );
};
