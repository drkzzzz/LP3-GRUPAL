/**
 * ProductosTab.jsx
 * ────────────────
 * Pestaña de Productos: tabla con búsqueda, paginación, CRUD completo.
 * Patrón copiado de Negocios.jsx del SuperAdmin (adaptado a Admin).
 */
import { useState, useMemo } from 'react';
import {
  Plus,
  Search,
  Eye,
  Edit,
  Power,
  Package,
  DollarSign,
  AlertCircle,
  CheckCircle,
} from 'lucide-react';
import { useProductos } from '../../hooks/useProductos';
import { useCategorias } from '../../hooks/useCategorias';
import { useMarcas } from '../../hooks/useMarcas';
import { useUnidadesMedida } from '../../hooks/useUnidadesMedida';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { Modal } from '@/admin/components/ui/Modal';
import { StatCard } from '@/admin/components/ui/StatCard';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { ProductoForm } from '../forms/ProductoForm';

export const ProductosTab = ({ negocioId }) => {
  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isDeactivateOpen, setIsDeactivateOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const {
    productos,
    isLoading,
    createProducto,
    updateProducto,
    deleteProducto,
    isCreating,
    isUpdating,
    isDeleting,
  } = useProductos();

  const { categorias } = useCategorias();
  const { marcas } = useMarcas();
  const { unidades } = useUnidadesMedida();

  /* ─── Filtrado client-side ─── */
  const filtered = useMemo(() => {
    if (!debouncedSearch) return productos;
    const q = debouncedSearch.toLowerCase();
    return productos.filter(
      (p) =>
        p.nombre?.toLowerCase().includes(q) ||
        p.sku?.toLowerCase().includes(q) ||
        p.descripcion?.toLowerCase().includes(q),
    );
  }, [productos, debouncedSearch]);

  /* ─── Paginación client-side ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: productos.length,
    activos: productos.filter((p) => p.estaActivo).length,
    sinStock: productos.filter((p) => (p.stock ?? 0) <= 0).length,
    conPrecio: productos.filter((p) => p.precioVenta > 0).length,
  }), [productos]);

  /* ─── Handlers ─── */
  const handleCreate = async (data) => {
    await createProducto(data);
    setIsCreateOpen(false);
  };

  const handleEdit = (producto) => {
    setSelected(producto);
    setIsEditOpen(true);
  };

  const handleUpdate = async (data) => {
    await updateProducto(data);
    setIsEditOpen(false);
    setSelected(null);
  };

  const handleView = (producto) => {
    setSelected(producto);
    setIsDetailOpen(true);
  };

  const handleToggleActive = (producto) => {
    setSelected(producto);
    setIsDeactivateOpen(true);
  };

  const handleToggleConfirm = async () => {
    if (selected) {
      await updateProducto({
        ...selected,
        estaActivo: !selected.estaActivo,
        // Mantener relaciones como objetos para el backend
        negocio: selected.negocio || { id: negocioId },
        categoria: selected.categoria || null,
        marca: selected.marca || null,
        unidadMedida: selected.unidadMedida || null,
      });
    }
    setIsDeactivateOpen(false);
    setSelected(null);
  };

  /* ─── Resolver nombre de relación ─── */
  const getCategoriaName = (producto) =>
    producto.categoria?.nombre ||
    categorias.find((c) => c.id === producto.categoriaId)?.nombre ||
    '—';

  const getMarcaName = (producto) =>
    producto.marca?.nombre ||
    marcas.find((m) => m.id === producto.marcaId)?.nombre ||
    '—';

  /* ─── Columnas de la tabla ─── */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '50px',
      render: (_, __, i) => (
        <span className="text-gray-400 text-xs">
          {(page - 1) * pageSize + i + 1}
        </span>
      ),
    },
    {
      key: 'producto',
      title: 'Producto',
      render: (_, row) => (
        <div className="flex items-center gap-3">
          {row.urlImagen ? (
            <img
              src={row.urlImagen}
              alt={row.nombre}
              className="w-9 h-9 rounded-lg object-cover flex-shrink-0"
            />
          ) : (
            <div className="w-9 h-9 rounded-lg bg-green-100 flex items-center justify-center text-green-700 font-bold text-sm flex-shrink-0">
              {row.nombre?.charAt(0)?.toUpperCase() || 'P'}
            </div>
          )}
          <div className="min-w-0">
            <p className="text-sm font-medium text-gray-900 truncate">{row.nombre}</p>
            <p className="text-xs text-gray-500 truncate">SKU: {row.sku || '—'}</p>
          </div>
        </div>
      ),
    },
    {
      key: 'categoria',
      title: 'Categoría',
      width: '140px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{getCategoriaName(row)}</span>
      ),
    },
    {
      key: 'marca',
      title: 'Marca',
      width: '120px',
      render: (_, row) => (
        <span className="text-sm text-gray-600">{getMarcaName(row)}</span>
      ),
    },
    {
      key: 'precio',
      title: 'Precio',
      width: '110px',
      render: (_, row) => (
        <span className="text-sm font-medium text-gray-800">
          {formatCurrency(row.precioVenta)}
        </span>
      ),
    },
    {
      key: 'stock',
      title: 'Stock',
      width: '80px',
      align: 'center',
      render: (_, row) => {
        const stock = row.stock ?? 0;
        return (
          <Badge variant={stock > 0 ? 'success' : 'error'}>
            {stock}
          </Badge>
        );
      },
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
            title={row.estaActivo ? 'Desactivar' : 'Activar'}
            onClick={() => handleToggleActive(row)}
            className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
          >
            <Power size={16} />
          </button>
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total productos" value={stats.total} icon={Package} />
        <StatCard
          title="Activos"
          value={stats.activos}
          icon={CheckCircle}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Sin stock"
          value={stats.sinStock}
          icon={AlertCircle}
          className="border-l-4 border-l-red-500"
        />
        <StatCard
          title="Con precio"
          value={stats.conPrecio}
          icon={DollarSign}
          className="border-l-4 border-l-blue-500"
        />
      </div>

      {/* Tabla */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="relative w-full sm:w-80">
            <Search
              size={16}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <input
              type="text"
              placeholder="Buscar por nombre, SKU..."
              value={searchTerm}
              onChange={(e) => { setSearchTerm(e.target.value); setPage(1); }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <Button onClick={() => setIsCreateOpen(true)}>
            <Plus size={18} />
            Nuevo Producto
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

      {/* ─── Modal: Crear ─── */}
      <Modal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        title="Nuevo Producto"
        size="xl"
      >
        <ProductoForm
          categorias={categorias}
          marcas={marcas}
          unidades={unidades}
          negocioId={negocioId}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateOpen(false)}
          isLoading={isCreating}
        />
      </Modal>

      {/* ─── Modal: Editar ─── */}
      <Modal
        isOpen={isEditOpen}
        onClose={() => { setIsEditOpen(false); setSelected(null); }}
        title="Editar Producto"
        size="xl"
      >
        {selected && (
          <ProductoForm
            initialData={selected}
            categorias={categorias}
            marcas={marcas}
            unidades={unidades}
            negocioId={negocioId}
            onSubmit={handleUpdate}
            onCancel={() => { setIsEditOpen(false); setSelected(null); }}
            isLoading={isUpdating}
          />
        )}
      </Modal>

      {/* ─── Modal: Detalle ─── */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => { setIsDetailOpen(false); setSelected(null); }}
        title="Detalle del Producto"
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            <div className="flex items-center gap-4">
              {selected.urlImagen ? (
                <img
                  src={selected.urlImagen}
                  alt={selected.nombre}
                  className="w-20 h-20 rounded-lg object-cover"
                />
              ) : (
                <div className="w-20 h-20 rounded-lg bg-green-100 flex items-center justify-center text-green-700 text-2xl font-bold">
                  {selected.nombre?.charAt(0)?.toUpperCase()}
                </div>
              )}
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{selected.nombre}</h3>
                <p className="text-sm text-gray-500">SKU: {selected.sku}</p>
                <Badge variant={selected.estaActivo ? 'success' : 'error'}>
                  {selected.estaActivo ? 'Activo' : 'Inactivo'}
                </Badge>
              </div>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-sm">
              <div>
                <p className="text-gray-500">Categoría</p>
                <p className="font-medium">{getCategoriaName(selected)}</p>
              </div>
              <div>
                <p className="text-gray-500">Marca</p>
                <p className="font-medium">{getMarcaName(selected)}</p>
              </div>
              <div>
                <p className="text-gray-500">Precio venta</p>
                <p className="font-medium">{formatCurrency(selected.precioVenta)}</p>
              </div>
              <div>
                <p className="text-gray-500">Precio compra</p>
                <p className="font-medium">{formatCurrency(selected.precioCompra)}</p>
              </div>
              <div>
                <p className="text-gray-500">Stock</p>
                <p className="font-medium">{selected.stock ?? 0}</p>
              </div>
              <div>
                <p className="text-gray-500">Grado alcohólico</p>
                <p className="font-medium">{selected.gradoAlcoholico ? `${selected.gradoAlcoholico}%` : '—'}</p>
              </div>
              <div>
                <p className="text-gray-500">Impuesto</p>
                <p className="font-medium">{selected.tasaImpuesto ?? 18}% {selected.impuestoIncluido ? '(incluido)' : '(no incluido)'}</p>
              </div>
              <div>
                <p className="text-gray-500">Permite descuento</p>
                <p className="font-medium">{selected.permiteDescuento ? 'Sí' : 'No'}</p>
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

      {/* ─── Confirmar activar/desactivar ─── */}
      <ConfirmDialog
        isOpen={isDeactivateOpen}
        onClose={() => { setIsDeactivateOpen(false); setSelected(null); }}
        onConfirm={handleToggleConfirm}
        title={selected?.estaActivo ? 'Desactivar Producto' : 'Activar Producto'}
        message={
          selected?.estaActivo
            ? `¿Está seguro de desactivar "${selected?.nombre}"?`
            : `¿Desea activar nuevamente "${selected?.nombre}"?`
        }
        confirmText={selected?.estaActivo ? 'Desactivar' : 'Activar'}
        variant={selected?.estaActivo ? 'danger' : 'primary'}
        isLoading={isUpdating}
      />
    </div>
  );
};
