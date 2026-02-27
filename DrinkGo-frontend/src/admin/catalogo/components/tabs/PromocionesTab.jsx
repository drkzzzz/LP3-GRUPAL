/**
 * PromocionesTab.jsx
 * ──────────────────
 * CRUD de Promociones con vista de condiciones.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { Plus, Search, Edit, Trash2, Eye, Tag, Percent, DollarSign } from 'lucide-react';
import { usePromociones } from '../../hooks/usePromociones';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { PromocionForm } from '../forms/PromocionForm';

/* Mapa de variantes por tipo_descuento */
const TIPO_BADGE = {
  porcentaje: 'info',
  monto_fijo: 'success',
};

export const PromocionesTab = () => {
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
    promociones,
    condiciones: allCondiciones,
    isLoading,
    createPromocion,
    updatePromocion,
    deletePromocion,
    getCondicionesForPromocion,
    isCreating,
    isUpdating,
    isDeleting,
  } = usePromociones();

  /* ─── Helpers ─── */
  const isPromoActive = (promo) => {
    if (!promo.estaActivo) return false;
    const now = new Date();
    if (promo.fechaInicio && new Date(promo.fechaInicio) > now) return false;
    if (promo.fechaFin && new Date(promo.fechaFin) < now) return false;
    return true;
  };

  const formatDescuento = (promo) => {
    if (promo.tipoDescuento === 'porcentaje') return `${promo.valorDescuento}%`;
    return formatCurrency(promo.valorDescuento);
  };

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return promociones;
    const q = debouncedSearch.toLowerCase();
    return promociones.filter(
      (p) =>
        p.nombre?.toLowerCase().includes(q) ||
        p.descripcion?.toLowerCase().includes(q) ||
        p.codigoPromocion?.toLowerCase().includes(q),
    );
  }, [promociones, debouncedSearch]);

  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    await createPromocion(data);
    setIsCreateOpen(false);
  };

  const handleEdit = (promo) => { setSelected(promo); setIsEditOpen(true); };

  const handleUpdate = async (data) => {
    await updatePromocion(data);
    setIsEditOpen(false);
    setSelected(null);
  };

  const handleView = (promo) => { setSelected(promo); setIsDetailOpen(true); };

  const handleDeleteClick = (promo) => { setSelected(promo); setIsDeleteOpen(true); };

  const handleDeleteConfirm = async () => {
    await deletePromocion(selected.id);
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
      key: 'promo',
      title: 'Promoción',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{row.nombre}</p>
          <p className="text-xs text-gray-500 truncate">
            {row.codigoPromocion ? `Código: ${row.codigoPromocion}` : row.descripcion || '—'}
          </p>
        </div>
      ),
    },
    {
      key: 'tipo',
      title: 'Tipo',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <Badge variant={TIPO_BADGE[row.tipoDescuento] || 'info'}>
          {row.tipoDescuento === 'porcentaje' ? 'Porcentaje' : 'Monto fijo'}
        </Badge>
      ),
    },
    {
      key: 'valor',
      title: 'Descuento',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm font-semibold text-green-600">{formatDescuento(row)}</span>
      ),
    },
    {
      key: 'aplica',
      title: 'Aplica a',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <Badge variant="warning">{row.aplicaA || 'todo'}</Badge>
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
        <Badge variant={isPromoActive(row) ? 'success' : 'warning'}>
          {isPromoActive(row) ? 'Vigente' : 'No vigente'}
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
            title="Ver condiciones"
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
        <h1 className="text-2xl font-bold text-gray-900">Promociones</h1>
        <p className="text-gray-600 mt-1">
          Gestión de promociones, descuentos y condiciones especiales de venta
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <Card className="!p-4 flex items-center gap-3">
          <Tag size={20} className="text-indigo-600" />
          <div>
            <p className="text-sm text-gray-500">Total promociones</p>
            <p className="text-xl font-bold">{promociones.length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <Percent size={20} className="text-green-600" />
          <div>
            <p className="text-sm text-gray-500">Vigentes</p>
            <p className="text-xl font-bold">{promociones.filter(isPromoActive).length}</p>
          </div>
        </Card>
        <Card className="!p-4 flex items-center gap-3">
          <DollarSign size={20} className="text-yellow-600" />
          <div>
            <p className="text-sm text-gray-500">Tipo monto fijo</p>
            <p className="text-xl font-bold">{promociones.filter((p) => p.tipoDescuento === 'monto_fijo').length}</p>
          </div>
        </Card>
      </div>

      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar promoción..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nueva Promoción
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
      <Modal isOpen={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Nueva Promoción" size="lg">
        <PromocionForm
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* Editar */}
      <Modal isOpen={isEditOpen} onClose={() => { setIsEditOpen(false); setSelected(null); }} title="Editar Promoción" size="lg">
        {selected && (
          <PromocionForm
            initialData={selected}
            negocioId={negocioId}
            onSubmit={handleUpdate}
            onCancel={() => { setIsEditOpen(false); setSelected(null); }}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* Detalle: condiciones */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title={`Detalles de "${selected?.nombre}"`}
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            {/* Info general */}
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Tipo descuento</p>
                <p className="font-medium">{selected.tipoDescuento === 'porcentaje' ? 'Porcentaje' : 'Monto fijo'}</p>
              </div>
              <div>
                <p className="text-gray-500">Valor</p>
                <p className="font-medium text-green-600">{formatDescuento(selected)}</p>
              </div>
              <div>
                <p className="text-gray-500">Aplica a</p>
                <p className="font-medium capitalize">{selected.aplicaA || 'todo'}</p>
              </div>
              <div>
                <p className="text-gray-500">Código</p>
                <p className="font-medium">{selected.codigoPromocion || '—'}</p>
              </div>
              <div>
                <p className="text-gray-500">Compra mínima</p>
                <p className="font-medium">{selected.montoMinimoCompra ? formatCurrency(selected.montoMinimoCompra) : '—'}</p>
              </div>
              <div>
                <p className="text-gray-500">Uso máximo</p>
                <p className="font-medium">{selected.usoMaximo || 'Ilimitado'}</p>
              </div>
            </div>

            {selected.descripcion && (
              <div className="text-sm">
                <p className="text-gray-500">Descripción</p>
                <p className="text-gray-700 mt-1">{selected.descripcion}</p>
              </div>
            )}

            {/* Condiciones */}
            <div>
              <h4 className="text-sm font-semibold text-gray-700 mb-2">Condiciones</h4>
              <div className="border rounded-lg overflow-hidden">
                <table className="w-full text-sm">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-4 py-2 text-left font-medium text-gray-600">Tipo condición</th>
                      <th className="px-4 py-2 text-left font-medium text-gray-600">Valor</th>
                      <th className="px-4 py-2 text-left font-medium text-gray-600">Descripción</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {getCondicionesForPromocion(selected.id).length > 0 ? (
                      getCondicionesForPromocion(selected.id).map((cond) => (
                        <tr key={cond.id}>
                          <td className="px-4 py-2">{cond.tipoCondicion}</td>
                          <td className="px-4 py-2">{cond.valorCondicion}</td>
                          <td className="px-4 py-2 text-gray-500">{cond.descripcion || '—'}</td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan="3" className="px-4 py-6 text-center text-gray-400">
                          Sin condiciones configuradas
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
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
        title="Eliminar Promoción"
        message={`¿Está seguro de eliminar la promoción "${selected?.nombre}"?`}
        confirmText="Eliminar"
        isLoading={isDeleting}
      />
    </div>
  );
};
