/**
 * ProductosProveedorTab.jsx
 * ─────────────────────────
 * Gestión de productos asignados a cada proveedor.
 * Permite ver, crear, editar y eliminar relaciones producto-proveedor.
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  Package,
  Star,
  Filter,
} from 'lucide-react';
import { useProductosProveedor } from '../../hooks/useProductosProveedor';
import { useProveedores } from '../../hooks/useProveedores';
import { productoProveedorSchema } from '../../validations/comprasSchemas';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Button } from '@/admin/components/ui/Button';

export const ProductosProveedorTab = () => {
  const { negocioId } = useOutletContext();

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [filterProveedor, setFilterProveedor] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const {
    productosProveedor,
    productos,
    isLoading,
    createProductoProveedor,
    updateProductoProveedor,
    deleteProductoProveedor,
    isCreating,
    isUpdating,
    isDeleting,
  } = useProductosProveedor(negocioId);

  const { proveedores } = useProveedores(negocioId);

  /* ─── Form ─── */
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(productoProveedorSchema),
    defaultValues: {
      proveedorId: '',
      productoId: '',
      skuProveedor: '',
      precioCompra: '0',
      tiempoEntregaDias: '',
      esPredeterminado: false,
    },
  });

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    let result = productosProveedor;

    if (filterProveedor) {
      result = result.filter(
        (pp) => String(pp.proveedor?.id) === filterProveedor,
      );
    }

    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (pp) =>
          pp.producto?.nombre?.toLowerCase().includes(q) ||
          pp.proveedor?.razonSocial?.toLowerCase().includes(q) ||
          pp.skuProveedor?.toLowerCase().includes(q),
      );
    }

    return result;
  }, [productosProveedor, debouncedSearch, filterProveedor]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(
    () => ({
      total: productosProveedor.length,
      proveedoresConProductos: new Set(productosProveedor.map((pp) => pp.proveedor?.id)).size,
      predeterminados: productosProveedor.filter((pp) => pp.esPredeterminado).length,
    }),
    [productosProveedor],
  );

  /* ─── Handlers ─── */
  const openCreate = () => {
    setEditing(null);
    reset({
      proveedorId: filterProveedor || '',
      productoId: '',
      skuProveedor: '',
      precioCompra: '0',
      tiempoEntregaDias: '',
      esPredeterminado: false,
    });
    setIsFormOpen(true);
  };

  const openEdit = (item) => {
    setEditing(item);
    reset({
      proveedorId: String(item.proveedor?.id || ''),
      productoId: String(item.producto?.id || ''),
      skuProveedor: item.skuProveedor || '',
      precioCompra: String(item.precioCompra ?? 0),
      tiempoEntregaDias: String(item.tiempoEntregaDias ?? ''),
      esPredeterminado: item.esPredeterminado || false,
    });
    setIsFormOpen(true);
  };

  const openDelete = (item) => {
    setSelected(item);
    setIsDeleteOpen(true);
  };

  const onSubmit = async (formData) => {
    const payload = {
      negocio: { id: negocioId },
      proveedor: { id: Number(formData.proveedorId) },
      producto: { id: Number(formData.productoId) },
      skuProveedor: formData.skuProveedor?.trim() || null,
      precioCompra: Number(formData.precioCompra) || 0,
      tiempoEntregaDias: formData.tiempoEntregaDias
        ? Number(formData.tiempoEntregaDias)
        : null,
      esPredeterminado: formData.esPredeterminado,
    };

    if (editing) {
      await updateProductoProveedor({ ...payload, id: editing.id });
    } else {
      await createProductoProveedor(payload);
    }
    setIsFormOpen(false);
    setEditing(null);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteProductoProveedor(selected.id);
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
      key: 'proveedor',
      title: 'Proveedor',
      render: (_, row) => (
        <span className="text-sm font-medium text-gray-900 truncate">
          {row.proveedor?.razonSocial || '—'}
        </span>
      ),
    },
    {
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">
            {row.producto?.nombre || '—'}
          </p>
          {row.skuProveedor && (
            <p className="text-xs text-gray-500 font-mono">SKU: {row.skuProveedor}</p>
          )}
        </div>
      ),
    },
    {
      key: 'precioCompra',
      title: 'Precio',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm font-medium text-gray-900">
          {formatCurrency(row.precioCompra)}
        </span>
      ),
    },
    {
      key: 'tiempoEntrega',
      title: 'Entrega',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <span className="text-sm text-gray-700">
          {row.tiempoEntregaDias ? `${row.tiempoEntregaDias} días` : '—'}
        </span>
      ),
    },
    {
      key: 'predeterminado',
      title: 'Predet.',
      width: '80px',
      align: 'center',
      render: (_, row) =>
        row.esPredeterminado ? (
          <Star size={16} className="text-yellow-500 mx-auto" fill="currentColor" />
        ) : (
          <span className="text-gray-300">—</span>
        ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '100px',
      align: 'center',
      render: (_, row) => (
        <div className="flex items-center justify-center gap-1">
          <button
            title="Editar"
            onClick={() => openEdit(row)}
            className="p-1.5 rounded hover:bg-blue-50 text-blue-500 hover:text-blue-700 transition-colors"
          >
            <Pencil size={15} />
          </button>
          <button
            title="Eliminar"
            onClick={() => openDelete(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
          >
            <Trash2 size={15} />
          </button>
        </div>
      ),
    },
  ];

  const isSaving = isCreating || isUpdating;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Productos por Proveedor</h1>
          <p className="text-gray-600 mt-1">
            Catálogo de productos suministrados por cada proveedor
          </p>
        </div>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
        >
          <Plus size={18} />
          Asignar Producto
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard title="Total asignaciones" value={stats.total} icon={Package} />
        <StatCard
          title="Proveedores con productos"
          value={stats.proveedoresConProductos}
          icon={Filter}
          className="border-l-4 border-l-blue-500"
        />
        <StatCard
          title="Predeterminados"
          value={stats.predeterminados}
          icon={Star}
          className="border-l-4 border-l-yellow-500"
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-72">
            <Search
              size={16}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <input
              type="text"
              placeholder="Buscar por producto o proveedor..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPage(1);
              }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <select
            value={filterProveedor}
            onChange={(e) => {
              setFilterProveedor(e.target.value);
              setPage(1);
            }}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
          >
            <option value="">Todos los proveedores</option>
            {proveedores.map((p) => (
              <option key={p.id} value={String(p.id)}>
                {p.razonSocial}
              </option>
            ))}
          </select>
        </div>

        <Table
          columns={columns}
          data={paginatedData}
          loading={isLoading}
          pagination={{
            current: page,
            pageSize,
            total: filtered.length,
            onChange: (newPage, newSize) => {
              setPage(newPage);
              setPageSize(newSize);
            },
          }}
        />
      </Card>

      {/* ─── Modal: Crear / Editar ─── */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => {
          setIsFormOpen(false);
          setEditing(null);
        }}
        title={editing ? 'Editar Producto del Proveedor' : 'Asignar Producto a Proveedor'}
        size="md"
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Select
            label="Proveedor"
            required
            placeholder="Seleccione un proveedor"
            options={proveedores.map((p) => ({ value: String(p.id), label: p.razonSocial }))}
            {...register('proveedorId')}
            error={errors.proveedorId?.message}
          />

          <Select
            label="Producto"
            required
            placeholder="Seleccione un producto"
            options={productos.map((p) => ({ value: String(p.id), label: `${p.nombre}${p.sku ? ` (${p.sku})` : ''}` }))}
            {...register('productoId')}
            error={errors.productoId?.message}
          />

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="SKU del Proveedor"
              placeholder="SKU-PROV-001"
              {...register('skuProveedor')}
              error={errors.skuProveedor?.message}
            />
            <Input
              label="Precio de Compra (S/)"
              type="number"
              step="0.01"
              min="0"
              placeholder="0.00"
              {...register('precioCompra')}
              error={errors.precioCompra?.message}
            />
          </div>

          <Input
            label="Tiempo de Entrega (días)"
            type="number"
            min="0"
            placeholder="5"
            {...register('tiempoEntregaDias')}
            error={errors.tiempoEntregaDias?.message}
          />

          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="esPredeterminado"
              {...register('esPredeterminado')}
              className="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500"
            />
            <label htmlFor="esPredeterminado" className="text-sm text-gray-700">
              Marcar como proveedor predeterminado para este producto
            </label>
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <Button
              type="button"
              variant="secondary"
              onClick={() => {
                setIsFormOpen(false);
                setEditing(null);
              }}
            >
              Cancelar
            </Button>
            <Button type="submit" disabled={isSaving}>
              {isSaving ? 'Guardando...' : editing ? 'Guardar cambios' : 'Asignar producto'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* ─── Modal: Confirmar eliminación ─── */}
      <Modal
        isOpen={isDeleteOpen}
        onClose={() => {
          setIsDeleteOpen(false);
          setSelected(null);
        }}
        title="Eliminar Asignación"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar la asignación del producto{' '}
            <strong className="text-gray-900">{selected?.producto?.nombre}</strong>{' '}
            del proveedor{' '}
            <strong className="text-gray-900">{selected?.proveedor?.razonSocial}</strong>?
          </p>
          <div className="flex justify-end gap-3">
            <Button
              variant="secondary"
              onClick={() => {
                setIsDeleteOpen(false);
                setSelected(null);
              }}
            >
              Cancelar
            </Button>
            <Button variant="danger" onClick={handleDelete} disabled={isDeleting}>
              {isDeleting ? 'Eliminando...' : 'Eliminar'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
