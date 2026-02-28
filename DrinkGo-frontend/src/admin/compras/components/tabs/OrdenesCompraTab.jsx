/**
 * OrdenesCompraTab.jsx
 * ────────────────────
 * Flujo completo de Órdenes de Compra:
 *  - Listar órdenes con filtros y stats
 *  - Crear orden con ítems inline (productos, cantidades, precios)
 *  - Cálculo automático de subtotal, IGV (18%), total
 *  - Generación automática de número de orden (OC-YYYYMMDD-NNN)
 *  - Detalle con tabla de ítems
 *  - Cancelar orden (solo pendientes)
 *  - Eliminar orden (solo pendientes)
 *  - Solo se editan/eliminan órdenes en estado pendiente
 */
import { useState, useMemo } from 'react';
import { useOutletContext } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Search,
  Plus,
  Trash2,
  Eye,
  ShoppingCart,
  Clock,
  CheckCircle,
  Package,
  Ban,
} from 'lucide-react';
import { useOrdenesCompra } from '../../hooks/useOrdenesCompra';
import { useDetalleOrdenesCompra } from '../../hooks/useDetalleOrdenesCompra';
import { ordenCompraSchema } from '../../validations/comprasSchemas';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatCurrency, formatDateTime } from '@/shared/utils/formatters';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Input } from '@/admin/components/ui/Input';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

const ESTADO_MAP = {
  pendiente: { label: 'Pendiente', variant: 'warning' },
  recibida: { label: 'Recibida', variant: 'success' },
  cancelada: { label: 'Cancelada', variant: 'error' },
};

const IGV_RATE = 0.18;

export const OrdenesCompraTab = () => {
  const { negocioId } = useOutletContext();
  const user = useAdminAuthStore((s) => s.user);

  /* ─── State ─── */
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [filterEstado, setFilterEstado] = useState('todos');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isCancelOpen, setIsCancelOpen] = useState(false);
  const [selected, setSelected] = useState(null);

  /* ─── Data hooks ─── */
  const {
    ordenes,
    proveedores: proveedoresOrden,
    sedes: sedesOrden,
    almacenes: almacenesOrden,
    productos: productosOrden,
    isLoading,
    generateNumeroOrden,
    createOrdenWithDetalles,
    changeEstado,
    deleteOrden,
    isCreating,
    isChangingEstado,
    isDeleting,
  } = useOrdenesCompra(negocioId);

  const { detalles, getDetallesForOrden } = useDetalleOrdenesCompra(negocioId);

  /* ─── Form ─── */
  const {
    register,
    handleSubmit,
    reset,
    control,
    watch,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(ordenCompraSchema),
    defaultValues: {
      proveedorId: '',
      sedeId: '',
      almacenId: '',
      notas: '',
      items: [{ productoId: '', cantidadSolicitada: '', precioUnitario: '' }],
    },
  });

  const { fields, append, remove } = useFieldArray({
    control,
    name: 'items',
  });

  const watchedItems = watch('items');

  /* ─── Cálculos ─── */
  const formTotals = useMemo(() => {
    let subtotal = 0;
    (watchedItems || []).forEach((item) => {
      const qty = Number(item.cantidadSolicitada) || 0;
      const price = Number(item.precioUnitario) || 0;
      subtotal += qty * price;
    });
    const impuestos = subtotal * IGV_RATE;
    const total = subtotal + impuestos;
    return {
      subtotal: Number(subtotal.toFixed(2)),
      impuestos: Number(impuestos.toFixed(2)),
      total: Number(total.toFixed(2)),
    };
  }, [watchedItems]);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    let result = ordenes;

    if (filterEstado !== 'todos') {
      result = result.filter((o) => o.estado === filterEstado);
    }

    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (o) =>
          o.numeroOrden?.toLowerCase().includes(q) ||
          o.proveedor?.razonSocial?.toLowerCase().includes(q),
      );
    }

    return result;
  }, [ordenes, debouncedSearch, filterEstado]);

  /* ─── Paginación ─── */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ─── Stats ─── */
  const stats = useMemo(() => {
    const total = ordenes.length;
    const pendientes = ordenes.filter((o) => o.estado === 'pendiente').length;
    const recibidas = ordenes.filter((o) => o.estado === 'recibida').length;
    const montoTotal = ordenes.reduce((sum, o) => sum + (Number(o.total) || 0), 0);
    return { total, pendientes, recibidas, montoTotal };
  }, [ordenes]);

  /* ─── Detail data ─── */
  const selectedDetalles = useMemo(() => {
    if (!selected) return [];
    return getDetallesForOrden(selected.id);
  }, [selected, detalles, getDetallesForOrden]);

  /* ─── Options for selects ─── */
  const proveedorOptions = useMemo(
    () => proveedoresOrden.map((p) => ({ value: String(p.id), label: p.razonSocial })),
    [proveedoresOrden],
  );

  const sedeOptions = useMemo(
    () => sedesOrden.map((s) => ({ value: String(s.id), label: s.nombre })),
    [sedesOrden],
  );

  const almacenOptions = useMemo(
    () => almacenesOrden.map((a) => ({ value: String(a.id), label: a.nombre })),
    [almacenesOrden],
  );

  const productoOptions = useMemo(
    () => productosOrden.map((p) => ({ value: String(p.id), label: p.nombre })),
    [productosOrden],
  );

  /* ─── Handlers ─── */
  const openCreate = () => {
    reset({
      proveedorId: '',
      sedeId: '',
      almacenId: '',
      notas: '',
      items: [{ productoId: '', cantidadSolicitada: '', precioUnitario: '' }],
    });
    setIsFormOpen(true);
  };

  const openDetail = (item) => {
    setSelected(item);
    setIsDetailOpen(true);
  };

  const openDelete = (item) => {
    setSelected(item);
    setIsDeleteOpen(true);
  };

  const openCancel = (item) => {
    setSelected(item);
    setIsCancelOpen(true);
  };

  const onSubmit = async (formData) => {
    const numeroOrden = generateNumeroOrden();

    const headerPayload = {
      negocio: { id: negocioId },
      numeroOrden,
      proveedor: { id: Number(formData.proveedorId) },
      sede: { id: Number(formData.sedeId) },
      almacen: { id: Number(formData.almacenId) },
      estado: 'pendiente',
      subtotal: formTotals.subtotal,
      impuestos: formTotals.impuestos,
      total: formTotals.total,
      notas: formData.notas?.trim() || null,
      usuario: user?.id ? { id: user.id } : null,
    };

    await createOrdenWithDetalles({
      headerPayload,
      items: formData.items,
    });

    setIsFormOpen(false);
  };

  const handleDelete = async () => {
    if (!selected) return;
    await deleteOrden(selected.id);
    setIsDeleteOpen(false);
    setSelected(null);
  };

  const handleCancel = async () => {
    if (!selected) return;
    await changeEstado({ orden: selected, nuevoEstado: 'cancelada' });
    setIsCancelOpen(false);
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
      key: 'numeroOrden',
      title: 'N° Orden',
      width: '180px',
      render: (_, row) => (
        <span className="text-sm font-mono font-medium text-gray-900">
          {row.numeroOrden || `#${row.id}`}
        </span>
      ),
    },
    {
      key: 'proveedor',
      title: 'Proveedor',
      render: (_, row) => (
        <span className="text-sm text-gray-900 truncate">
          {row.proveedor?.razonSocial || '—'}
        </span>
      ),
    },
    {
      key: 'sede',
      title: 'Sede / Almacén',
      width: '180px',
      render: (_, row) => (
        <div>
          <p className="text-sm text-gray-900">{row.sede?.nombre || '—'}</p>
          <p className="text-xs text-gray-500">{row.almacen?.nombre || ''}</p>
        </div>
      ),
    },
    {
      key: 'fecha',
      title: 'Fecha',
      width: '130px',
      render: (_, row) => (
        <span className="text-sm text-gray-700">
          {formatDateTime(row.fechaOrden || row.creadoEn)}
        </span>
      ),
    },
    {
      key: 'total',
      title: 'Total',
      width: '120px',
      align: 'right',
      render: (_, row) => (
        <span className="text-sm font-medium text-gray-900">
          {formatCurrency(row.total)}
        </span>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '110px',
      align: 'center',
      render: (_, row) => {
        const cfg = ESTADO_MAP[row.estado] || ESTADO_MAP.pendiente;
        return <Badge variant={cfg.variant}>{cfg.label}</Badge>;
      },
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '120px',
      align: 'center',
      render: (_, row) => (
        <div className="flex items-center justify-center gap-1">
          <button
            title="Ver detalle"
            onClick={() => openDetail(row)}
            className="p-1.5 rounded hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition-colors"
          >
            <Eye size={15} />
          </button>
          {row.estado === 'pendiente' && (
            <>
              <button
                title="Cancelar orden"
                onClick={() => openCancel(row)}
                className="p-1.5 rounded hover:bg-orange-50 text-orange-400 hover:text-orange-600 transition-colors"
              >
                <Ban size={15} />
              </button>
              <button
                title="Eliminar"
                onClick={() => openDelete(row)}
                className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600 transition-colors"
              >
                <Trash2 size={15} />
              </button>
            </>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Órdenes de Compra</h1>
          <p className="text-gray-600 mt-1">
            Registro y seguimiento de órdenes de compra a proveedores
          </p>
        </div>
        <button
          onClick={openCreate}
          className="flex items-center gap-2 bg-green-600 text-white px-4 py-2.5 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors"
        >
          <Plus size={18} />
          Nueva Orden
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total órdenes" value={stats.total} icon={ShoppingCart} />
        <StatCard
          title="Pendientes"
          value={stats.pendientes}
          icon={Clock}
          className="border-l-4 border-l-yellow-500"
        />
        <StatCard
          title="Recibidas"
          value={stats.recibidas}
          icon={CheckCircle}
          className="border-l-4 border-l-green-500"
        />
        <StatCard
          title="Monto total"
          value={formatCurrency(stats.montoTotal)}
          icon={Package}
          className="border-l-4 border-l-blue-500"
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
              placeholder="Buscar por N° orden, proveedor..."
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                setPage(1);
              }}
              className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>
          <select
            value={filterEstado}
            onChange={(e) => {
              setFilterEstado(e.target.value);
              setPage(1);
            }}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500"
          >
            <option value="todos">Todos los estados</option>
            <option value="pendiente">Pendientes</option>
            <option value="recibida">Recibidas</option>
            <option value="cancelada">Canceladas</option>
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

      {/* ─── Modal: Crear Orden de Compra ─── */}
      <Modal
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        title="Nueva Orden de Compra"
        size="xl"
      >
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          {/* Cabecera */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <Select
              label="Proveedor"
              required
              placeholder="Seleccione proveedor"
              options={proveedorOptions}
              {...register('proveedorId')}
              error={errors.proveedorId?.message}
            />
            <Select
              label="Sede"
              required
              placeholder="Seleccione sede"
              options={sedeOptions}
              {...register('sedeId')}
              error={errors.sedeId?.message}
            />
            <Select
              label="Almacén"
              required
              placeholder="Seleccione almacén"
              options={almacenOptions}
              {...register('almacenId')}
              error={errors.almacenId?.message}
            />
          </div>

          {/* Ítems */}
          <div className="border-t border-gray-200 pt-4">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-semibold text-gray-700">
                Productos de la orden
              </h3>
              <button
                type="button"
                onClick={() =>
                  append({ productoId: '', cantidadSolicitada: '', precioUnitario: '' })
                }
                className="flex items-center gap-1 text-xs font-medium text-green-700 bg-green-50 px-3 py-1.5 rounded-lg hover:bg-green-100 transition-colors"
              >
                <Plus size={14} />
                Agregar producto
              </button>
            </div>

            {errors.items?.message && (
              <p className="text-sm text-red-500 mb-2">{errors.items.message}</p>
            )}

            <div className="space-y-3">
              {fields.map((field, index) => (
                <div
                  key={field.id}
                  className="grid grid-cols-12 gap-2 items-start bg-gray-50 p-3 rounded-lg"
                >
                  {/* Producto */}
                  <div className="col-span-12 sm:col-span-5">
                    <Select
                      label={index === 0 ? 'Producto' : undefined}
                      placeholder="Seleccione..."
                      options={productoOptions}
                      {...register(`items.${index}.productoId`)}
                      error={errors.items?.[index]?.productoId?.message}
                    />
                  </div>

                  {/* Cantidad */}
                  <div className="col-span-4 sm:col-span-2">
                    <Input
                      label={index === 0 ? 'Cantidad' : undefined}
                      type="number"
                      min="1"
                      step="1"
                      placeholder="0"
                      {...register(`items.${index}.cantidadSolicitada`)}
                      error={errors.items?.[index]?.cantidadSolicitada?.message}
                    />
                  </div>

                  {/* Precio unitario */}
                  <div className="col-span-4 sm:col-span-2">
                    <Input
                      label={index === 0 ? 'P. Unit.' : undefined}
                      type="number"
                      min="0.01"
                      step="0.01"
                      placeholder="0.00"
                      {...register(`items.${index}.precioUnitario`)}
                      error={errors.items?.[index]?.precioUnitario?.message}
                    />
                  </div>

                  {/* Subtotal (calculado) */}
                  <div className="col-span-3 sm:col-span-2">
                    {index === 0 && (
                      <label className="block text-sm font-medium text-gray-700 mb-1">
                        Subtotal
                      </label>
                    )}
                    <div className="h-[38px] flex items-center justify-end bg-white border border-gray-200 rounded-lg px-2 text-sm font-medium text-gray-900">
                      {formatCurrency(
                        (Number(watchedItems?.[index]?.cantidadSolicitada) || 0) *
                          (Number(watchedItems?.[index]?.precioUnitario) || 0),
                      )}
                    </div>
                  </div>

                  {/* Eliminar */}
                  <div className="col-span-1 flex items-end justify-center">
                    {index === 0 && (
                      <label className="block text-sm font-medium text-transparent mb-1">X</label>
                    )}
                    <button
                      type="button"
                      onClick={() => fields.length > 1 && remove(index)}
                      disabled={fields.length <= 1}
                      className="h-[38px] w-[38px] flex items-center justify-center rounded-lg text-red-400 hover:bg-red-50 hover:text-red-600 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Totales */}
          <div className="flex justify-end border-t border-gray-200 pt-4">
            <div className="w-64 space-y-1.5 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-600">Subtotal:</span>
                <span className="font-medium">{formatCurrency(formTotals.subtotal)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">IGV (18%):</span>
                <span className="font-medium">{formatCurrency(formTotals.impuestos)}</span>
              </div>
              <div className="flex justify-between font-semibold text-base border-t pt-1.5">
                <span>Total:</span>
                <span className="text-green-700">{formatCurrency(formTotals.total)}</span>
              </div>
            </div>
          </div>

          {/* Notas */}
          <Textarea
            label="Notas / Observaciones"
            placeholder="Observaciones adicionales de la orden..."
            rows={2}
            {...register('notas')}
            error={errors.notas?.message}
          />

          {/* Acciones */}
          <div className="flex justify-end gap-3 pt-2">
            <Button
              type="button"
              variant="secondary"
              onClick={() => setIsFormOpen(false)}
            >
              Cancelar
            </Button>
            <Button type="submit" disabled={isCreating}>
              {isCreating ? 'Creando orden...' : 'Crear Orden de Compra'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* ─── Modal: Detalle de Orden ─── */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelected(null);
        }}
        title={`Orden ${selected?.numeroOrden || `#${selected?.id}`}`}
        size="lg"
      >
        {selected && (
          <div className="space-y-4">
            {/* Encabezado */}
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 bg-gray-50 p-4 rounded-lg">
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Proveedor</p>
                <p className="text-sm text-gray-900">{selected.proveedor?.razonSocial || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Sede</p>
                <p className="text-sm text-gray-900">{selected.sede?.nombre || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Almacén</p>
                <p className="text-sm text-gray-900">{selected.almacen?.nombre || '—'}</p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Fecha</p>
                <p className="text-sm text-gray-900">
                  {formatDateTime(selected.fechaOrden || selected.creadoEn)}
                </p>
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">Estado</p>
                {(() => {
                  const cfg = ESTADO_MAP[selected.estado] || ESTADO_MAP.pendiente;
                  return <Badge variant={cfg.variant}>{cfg.label}</Badge>;
                })()}
              </div>
              <div>
                <p className="text-xs text-gray-500 uppercase font-semibold">N° Orden</p>
                <p className="text-sm font-mono text-gray-900">{selected.numeroOrden}</p>
              </div>
            </div>

            {/* Ítems */}
            <div>
              <h3 className="text-sm font-semibold text-gray-700 mb-2">
                Detalle de ítems ({selectedDetalles.length})
              </h3>
              {selectedDetalles.length === 0 ? (
                <p className="text-sm text-gray-500 italic">
                  No hay ítems registrados para esta orden.
                </p>
              ) : (
                <div className="border rounded-lg overflow-hidden">
                  <table className="w-full text-sm">
                    <thead className="bg-gray-50 text-xs uppercase text-gray-500">
                      <tr>
                        <th className="px-3 py-2 text-left">Producto</th>
                        <th className="px-3 py-2 text-center">Solicitado</th>
                        <th className="px-3 py-2 text-center">Recibido</th>
                        <th className="px-3 py-2 text-right">P. Unit.</th>
                        <th className="px-3 py-2 text-right">Subtotal</th>
                        <th className="px-3 py-2 text-right">Impuesto</th>
                        <th className="px-3 py-2 text-right">Total</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                      {selectedDetalles.map((d) => (
                        <tr key={d.id}>
                          <td className="px-3 py-2">{d.producto?.nombre || '—'}</td>
                          <td className="px-3 py-2 text-center">{d.cantidadSolicitada}</td>
                          <td className="px-3 py-2 text-center">{d.cantidadRecibida ?? 0}</td>
                          <td className="px-3 py-2 text-right">{formatCurrency(d.precioUnitario)}</td>
                          <td className="px-3 py-2 text-right">{formatCurrency(d.subtotal)}</td>
                          <td className="px-3 py-2 text-right">{formatCurrency(d.impuesto)}</td>
                          <td className="px-3 py-2 text-right font-medium">
                            {formatCurrency(d.total)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>

            {/* Totales */}
            <div className="flex justify-end">
              <div className="w-64 space-y-1 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Subtotal:</span>
                  <span>{formatCurrency(selected.subtotal)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">IGV (18%):</span>
                  <span>{formatCurrency(selected.impuestos)}</span>
                </div>
                <div className="flex justify-between font-semibold text-base border-t pt-1">
                  <span>Total:</span>
                  <span>{formatCurrency(selected.total)}</span>
                </div>
              </div>
            </div>

            {selected.notas && (
              <div className="border-t pt-3">
                <p className="text-xs text-gray-500 uppercase font-semibold">Notas</p>
                <p className="text-sm text-gray-900">{selected.notas}</p>
              </div>
            )}

            {/* Actions in detail modal */}
            <div className="flex justify-between items-center pt-2 border-t">
              <div>
                {selected.estado === 'pendiente' && (
                  <Button
                    variant="danger"
                    onClick={() => {
                      setIsDetailOpen(false);
                      openCancel(selected);
                    }}
                    className="flex items-center gap-2"
                  >
                    <Ban size={15} />
                    Cancelar orden
                  </Button>
                )}
              </div>
              <Button
                variant="secondary"
                onClick={() => {
                  setIsDetailOpen(false);
                  setSelected(null);
                }}
              >
                Cerrar
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* ─── Modal: Confirmar cancelación ─── */}
      <Modal
        isOpen={isCancelOpen}
        onClose={() => {
          setIsCancelOpen(false);
          setSelected(null);
        }}
        title="Cancelar Orden de Compra"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de cancelar la orden{' '}
            <strong className="text-gray-900">
              {selected?.numeroOrden || `#${selected?.id}`}
            </strong>
            ? Esta acción cambiará su estado a <strong>cancelada</strong>.
          </p>
          <div className="flex justify-end gap-3">
            <Button
              variant="secondary"
              onClick={() => {
                setIsCancelOpen(false);
                setSelected(null);
              }}
            >
              Volver
            </Button>
            <Button variant="danger" onClick={handleCancel} disabled={isChangingEstado}>
              {isChangingEstado ? 'Cancelando...' : 'Confirmar cancelación'}
            </Button>
          </div>
        </div>
      </Modal>

      {/* ─── Modal: Confirmar eliminación ─── */}
      <Modal
        isOpen={isDeleteOpen}
        onClose={() => {
          setIsDeleteOpen(false);
          setSelected(null);
        }}
        title="Eliminar Orden de Compra"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            ¿Estás seguro de eliminar la orden{' '}
            <strong className="text-gray-900">
              {selected?.numeroOrden || `#${selected?.id}`}
            </strong>
            ? Esta acción no se puede deshacer.
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
