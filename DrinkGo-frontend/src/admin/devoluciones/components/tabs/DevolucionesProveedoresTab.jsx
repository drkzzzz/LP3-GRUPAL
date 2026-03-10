/**
 * DevolucionesProveedoresTab.jsx
 * ──────────────────────────────
 * Pestaña de devoluciones a proveedores: listado, registro y detalle.
 * - Muestra los lotes de inventario disponibles para seleccionar.
 * - Parcial: permite elegir lotes y cantidades a devolver.
 * - Total: todos los lotes se incluyen automáticamente.
 * - Al aprobar, el stock disminuye porque se devuelve producto al proveedor.
 */
import { useState, useMemo, useEffect, useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import {
  Search, Plus, Eye, Trash2, RotateCcw, Loader2,
  AlertCircle, PackageX, Truck, CheckCircle, XCircle, Minus,
} from 'lucide-react';
import { useDevoluciones } from '../../hooks/useDevoluciones';
import { devolucionSchema } from '../../validations/devolucionesSchemas';
import { lotesInventarioService } from '@/admin/inventario/services/inventarioService';
import { detalleDevolucionesService } from '../../services/devolucionesService';
import { useAdminAuthStore } from '@/stores/adminAuthStore';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { formatDateTime, formatCurrency } from '@/shared/utils/formatters';
import { Card } from '@/admin/components/ui/Card';
import { Table } from '@/admin/components/ui/Table';
import { Badge } from '@/admin/components/ui/Badge';
import { StatCard } from '@/admin/components/ui/StatCard';
import { Modal } from '@/admin/components/ui/Modal';
import { Select } from '@/admin/components/ui/Select';
import { Textarea } from '@/admin/components/ui/Textarea';
import { Button } from '@/admin/components/ui/Button';

/* ─── Opciones de selects ─── */
const TIPO_DEVOLUCION_OPTIONS = [
  { value: 'total', label: 'Total' },
  { value: 'parcial', label: 'Parcial' },
];
const CATEGORIA_MOTIVO_OPTIONS = [
  { value: 'defectuoso', label: 'Producto defectuoso' },
  { value: 'articulo_incorrecto', label: 'Artículo incorrecto' },
  { value: 'vencido', label: 'Producto vencido' },
  { value: 'danado', label: 'Producto dañado en transporte' },
  { value: 'otro', label: 'Otro' },
];
const METODO_REEMBOLSO_OPTIONS = [
  { value: 'efectivo', label: 'Efectivo' },
  { value: 'pago_original', label: 'Método de pago original' },
  { value: 'credito_tienda', label: 'Crédito / Nota de crédito' },
  { value: 'transferencia_bancaria', label: 'Transferencia bancaria' },
];
const ESTADO_COLORS = {
  solicitada: 'warning', aprobada: 'info', procesando: 'info',
  completada: 'success', rechazada: 'error',
};
const ESTADO_LABELS = {
  solicitada: 'Solicitada', aprobada: 'Aprobada', procesando: 'Procesando',
  completada: 'Completada', rechazada: 'Rechazada',
};
const MOTIVO_LABELS = {
  defectuoso: 'Producto defectuoso', articulo_incorrecto: 'Artículo incorrecto',
  vencido: 'Producto vencido', danado: 'Producto dañado', otro: 'Otro',
};

export const DevolucionesProveedoresTab = ({ negocioId }) => {
  const user = useAdminAuthStore((s) => s.user);
  const sede = useAdminAuthStore((s) => s.sede);
  const queryClient = useQueryClient();

  /* ─── State ─── */
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);
  const [filterEstado, setFilterEstado] = useState('todos');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [isRejectOpen, setIsRejectOpen] = useState(false);
  const [selected, setSelected] = useState(null);
  const [razonRechazo, setRazonRechazo] = useState('');

  /* Lotes seleccionados para devolver: { [loteId]: cantidadADevolver } */
  const [productosSeleccionados, setProductosSeleccionados] = useState({});

  /* ─── Data ─── */
  const {
    devoluciones, isLoading, isError,
    createDevolucion, isCreating,
    deleteDevolucion, isDeleting,
    aprobarDevolucion, isAprobando,
    rechazarDevolucion, isRechazando,
  } = useDevoluciones(negocioId, 'proveedores');

  /* ─── Lotes de inventario del negocio (para seleccionar al crear) ─── */
  const { data: lotes = [] } = useQuery({
    queryKey: ['lotes-inventario-devoluciones', negocioId],
    queryFn: lotesInventarioService.getAll,
    enabled: !!negocioId && isFormOpen,
    select: (data) => data.filter((l) =>
      (l.negocio?.id ?? l.negocioId) === negocioId &&
      Number(l.cantidadActual) > 0
    ),
  });

  /* ─── Detalle de la devolución seleccionada (para modal detalle) ─── */
  const { data: detalleDevolucionItems = [], isLoading: loadingDetalleDevolucion } = useQuery({
    queryKey: ['detalle-devoluciones', selected?.id],
    queryFn: () => detalleDevolucionesService.getByDevolucion(selected.id),
    enabled: !!selected?.id && isDetailOpen,
  });

  /* ─── Form ─── */
  const { register, handleSubmit, reset, watch, formState: { errors } } = useForm({
    resolver: zodResolver(devolucionSchema),
    defaultValues: {
      tipoDevolucion: 'parcial', categoriaMotivo: '',
      detalleMotivo: '', metodoReembolso: 'pago_original', notas: '',
    },
  });

  const tipoDevolucion = watch('tipoDevolucion');

  /* ─── Cuando cambia el tipo a total, seleccionar todos los lotes; a parcial, limpiar ─── */
  useEffect(() => {
    if (!lotes.length) return;
    if (tipoDevolucion === 'total') {
      const sel = {};
      lotes.forEach((lote) => { sel[lote.id] = Number(lote.cantidadActual); });
      setProductosSeleccionados(sel);
    } else {
      setProductosSeleccionados({});
    }
  }, [tipoDevolucion, lotes]);

  /* ─── Helpers de selección de productos ─── */
  const toggleProducto = (detalleId) => {
    setProductosSeleccionados((prev) => {
      const copy = { ...prev };
      if (copy[detalleId]) { delete copy[detalleId]; } else { copy[detalleId] = 1; }
      return copy;
    });
  };

  const updateCantidad = (detalleId, newQty, maxCantidad) => {
    const qty = Math.max(1, Math.min(Number(newQty), maxCantidad));
    setProductosSeleccionados((prev) => ({ ...prev, [detalleId]: qty }));
  };

  /* Calcular total de devolución */
  const calcularTotalDevolucion = useCallback(() => {
    let total = 0;
    for (const [loteId, qty] of Object.entries(productosSeleccionados)) {
      const lote = lotes.find((l) => String(l.id) === String(loteId));
      if (lote) total += Number(lote.costoUnitario) * qty;
    }
    return total;
  }, [productosSeleccionados, lotes]);

  /* ─── Filtrado ─── */
  const filtered = useMemo(() => {
    let result = devoluciones;
    if (filterEstado !== 'todos') result = result.filter((d) => d.estado === filterEstado);
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter((d) =>
        (d.numeroDevolucion || '').toLowerCase().includes(q) ||
        (d.detalleMotivo || '').toLowerCase().includes(q) ||
        (d.categoriaMotivo || '').toLowerCase().includes(q)
      );
    }
    return result;
  }, [devoluciones, filterEstado, debouncedSearch]);

  /* ─── Stats ─── */
  const stats = useMemo(() => ({
    total: devoluciones.length,
    solicitadas: devoluciones.filter((d) => d.estado === 'solicitada').length,
    completadas: devoluciones.filter((d) => d.estado === 'completada').length,
    rechazadas: devoluciones.filter((d) => d.estado === 'rechazada').length,
  }), [devoluciones]);

  /* ─── Handlers ─── */
  const handleOpenCreate = () => {
    reset({ tipoDevolucion: 'parcial', categoriaMotivo: '', detalleMotivo: '', metodoReembolso: 'pago_original', notas: '' });
    setProductosSeleccionados({});
    setIsFormOpen(true);
  };

  const handleCreate = async (formData) => {
    const totalDev = calcularTotalDevolucion();
    const selectedItems = Object.entries(productosSeleccionados);

    if (selectedItems.length === 0) return;

    /* 1) Crear la devolución cabecera */
    const devolucionData = {
      negocio: { id: negocioId },
      sede: { id: sede?.id },
      tipoDevolucion: formData.tipoDevolucion,
      categoriaMotivo: formData.categoriaMotivo,
      detalleMotivo: formData.detalleMotivo,
      metodoReembolso: formData.metodoReembolso,
      notas: formData.notas || null,
      estado: 'solicitada',
      subtotal: totalDev,
      montoImpuesto: 0,
      total: totalDev,
      solicitadoPor: user ? { id: user.id } : null,
    };

    const devolucionCreada = await createDevolucion(devolucionData);
    const devolucionId = devolucionCreada?.id;

    /* 2) Crear cada detalle de devolución con referencia al lote */
    if (devolucionId) {
      const promises = selectedItems.map(([loteId, qty]) => {
        const lote = lotes.find((l) => String(l.id) === String(loteId));
        if (!lote) return null;
        return detalleDevolucionesService.create({
          devolucion: { id: devolucionId },
          producto: { id: lote.producto?.id || lote.productoId },
          lote: { id: lote.id },
          cantidad: qty,
          precioUnitario: lote.costoUnitario,
          total: Number(lote.costoUnitario) * qty,
          estadoCondicion: 'bueno',
          devolverStock: true,
        });
      }).filter(Boolean);

      await Promise.all(promises);
    }

    queryClient.invalidateQueries({ queryKey: ['devoluciones'] });
    setIsFormOpen(false);
  };

  const handleView = (item) => { setSelected(item); setIsDetailOpen(true); };
  const handleDeleteConfirm = (item) => { setSelected(item); setIsDeleteOpen(true); };
  const handleDelete = async () => { if (selected) { await deleteDevolucion(selected.id); setIsDeleteOpen(false); setSelected(null); } };

  const handleAprobar = async (item) => {
    if (!user?.id) return;
    await aprobarDevolucion({ id: item.id, usuarioId: user.id });
  };
  const handleOpenReject = (item) => { setSelected(item); setRazonRechazo(''); setIsRejectOpen(true); };
  const handleRechazar = async () => {
    if (!selected || !user?.id) return;
    await rechazarDevolucion({ id: selected.id, usuarioId: user.id, razon: razonRechazo });
    setIsRejectOpen(false); setSelected(null);
  };

  /* ─── Columnas de la tabla ─── */
  const columns = [
    { key: 'index', title: '#', width: '50px', render: (_, __, i) => i + 1 },
    { key: 'numeroDevolucion', title: 'N° Devolución', dataIndex: 'numeroDevolucion',
      render: (val) => <span className="font-mono text-sm">{val || '—'}</span> },
    { key: 'tipoDevolucion', title: 'Tipo', dataIndex: 'tipoDevolucion',
      render: (val) => <Badge variant={val === 'total' ? 'error' : 'warning'}>{val === 'total' ? 'Total' : 'Parcial'}</Badge> },
    { key: 'categoriaMotivo', title: 'Motivo', dataIndex: 'categoriaMotivo',
      render: (val) => MOTIVO_LABELS[val] || val || '—' },
    { key: 'total', title: 'Monto', dataIndex: 'total', render: (val) => formatCurrency(val) },
    { key: 'estado', title: 'Estado', dataIndex: 'estado',
      render: (val) => <Badge variant={ESTADO_COLORS[val] || 'default'}>{ESTADO_LABELS[val] || val}</Badge> },
    { key: 'creadoEn', title: 'Fecha', dataIndex: 'creadoEn', render: (val) => formatDateTime(val) },
    { key: 'actions', title: 'Acciones', width: '140px', align: 'center',
      render: (_, record) => (
        <div className="flex justify-center gap-1">
          <button onClick={() => handleView(record)} className="p-1.5 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors" title="Ver detalle"><Eye size={16} /></button>
          {record.estado === 'solicitada' && (<>
            <button onClick={() => handleAprobar(record)} className="p-1.5 text-gray-500 hover:text-green-600 hover:bg-green-50 rounded-lg transition-colors" title="Aprobar" disabled={isAprobando}><CheckCircle size={16} /></button>
            <button onClick={() => handleOpenReject(record)} className="p-1.5 text-gray-500 hover:text-orange-600 hover:bg-orange-50 rounded-lg transition-colors" title="Rechazar"><XCircle size={16} /></button>
          </>)}
          <button onClick={() => handleDeleteConfirm(record)} className="p-1.5 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors" title="Eliminar"><Trash2 size={16} /></button>
        </div>
      ),
    },
  ];

  /* ─── Render ─── */
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-xl font-semibold text-gray-900">Devoluciones a Proveedores</h2>
          <p className="text-sm text-gray-500 mt-1">Gestiona las devoluciones de productos a proveedores basadas en lotes de inventario</p>
        </div>
        <Button onClick={handleOpenCreate} className="gap-2"><Plus size={18} />Nueva Devolución</Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total" value={stats.total} icon={Truck} color="blue" />
        <StatCard title="Solicitadas" value={stats.solicitadas} icon={AlertCircle} color="yellow" />
        <StatCard title="Completadas" value={stats.completadas} icon={PackageX} color="green" />
        <StatCard title="Rechazadas" value={stats.rechazadas} icon={AlertCircle} color="red" />
      </div>

      {/* Filtros */}
      <Card>
        <div className="flex flex-col sm:flex-row gap-3 p-4">
          <div className="relative flex-1">
            <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input type="text" placeholder="Buscar por número, motivo..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none" />
          </div>
          <select value={filterEstado} onChange={(e) => setFilterEstado(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none">
            <option value="todos">Todos los estados</option>
            <option value="solicitada">Solicitada</option>
            <option value="aprobada">Aprobada</option>
            <option value="procesando">Procesando</option>
            <option value="completada">Completada</option>
            <option value="rechazada">Rechazada</option>
          </select>
        </div>
      </Card>

      {/* Tabla */}
      <Card>
        {isLoading ? (
          <div className="flex items-center justify-center py-12"><Loader2 size={24} className="animate-spin text-green-600" /><span className="ml-2 text-gray-500">Cargando devoluciones...</span></div>
        ) : isError ? (
          <div className="flex items-center justify-center py-12 text-red-500"><AlertCircle size={20} className="mr-2" />Error al cargar las devoluciones</div>
        ) : filtered.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 text-gray-400"><RotateCcw size={40} className="mb-3" /><p className="font-medium">No se encontraron devoluciones a proveedores</p><p className="text-sm mt-1">Las devoluciones registradas aparecerán aquí</p></div>
        ) : (
          <Table columns={columns} data={filtered} />
        )}
      </Card>

      {/* ═══════════════════════════════════════════════════════════
          Modal: Nueva devolución a proveedor
         ═══════════════════════════════════════════════════════════ */}
      <Modal isOpen={isFormOpen} onClose={() => setIsFormOpen(false)} title="Registrar Devolución a Proveedor" size="xl">
        <form onSubmit={handleSubmit(handleCreate)} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Tipo de Devolución <span className="text-red-500">*</span></label>
              <Select {...register('tipoDevolucion')} options={TIPO_DEVOLUCION_OPTIONS} />
              {errors.tipoDevolucion && <p className="text-sm text-red-500 mt-1">{errors.tipoDevolucion.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Método de Reembolso <span className="text-red-500">*</span></label>
              <Select {...register('metodoReembolso')} options={METODO_REEMBOLSO_OPTIONS} />
              {errors.metodoReembolso && <p className="text-sm text-red-500 mt-1">{errors.metodoReembolso.message}</p>}
            </div>
          </div>

          {/* ─── Lotes de inventario disponibles ─── */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Lotes a devolver {tipoDevolucion === 'total' ? '(todos incluidos)' : <span className="text-red-500">*</span>}
            </label>
            <p className="text-xs text-gray-400 mb-2">Se muestran los lotes con stock disponible en el inventario del negocio</p>
            {lotes.length === 0 ? (
              <p className="text-sm text-gray-400 py-2">No se encontraron lotes con stock disponible</p>
            ) : (
              <div className="border border-gray-200 rounded-lg overflow-x-auto">
                <table className="w-full text-sm min-w-[700px]">
                  <thead className="bg-gray-50">
                    <tr>
                      {tipoDevolucion === 'parcial' && <th className="px-3 py-2 text-left w-10"></th>}
                      <th className="px-3 py-2 text-left">N° Lote</th>
                      <th className="px-3 py-2 text-left">Producto</th>
                      <th className="px-3 py-2 text-center">Disponible</th>
                      <th className="px-3 py-2 text-center">Cant. a Devolver</th>
                      <th className="px-3 py-2 text-right">Costo Unit.</th>
                      <th className="px-3 py-2 text-right">Subtotal Dev.</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {lotes.map((lote) => {
                      const maxQty = Number(lote.cantidadActual);
                      const isSelected = !!productosSeleccionados[lote.id];
                      const qty = productosSeleccionados[lote.id] || 0;
                      const isTotal = tipoDevolucion === 'total';
                      return (
                        <tr key={lote.id} className={isSelected ? 'bg-purple-50' : ''}>
                          {!isTotal && (
                            <td className="px-3 py-2 text-center">
                              <input type="checkbox" checked={isSelected}
                                onChange={() => toggleProducto(lote.id)}
                                className="h-4 w-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500" />
                            </td>
                          )}
                          <td className="px-3 py-2 font-mono text-xs">{lote.numeroLote || '—'}</td>
                          <td className="px-3 py-2 font-medium">{lote.producto?.nombre || `Producto #${lote.producto?.id || lote.productoId}`}</td>
                          <td className="px-3 py-2 text-center text-gray-500">{maxQty}</td>
                          <td className="px-3 py-2">
                            {(isSelected || isTotal) ? (
                              <div className="flex items-center justify-center gap-1">
                                <button type="button" onClick={() => updateCantidad(lote.id, qty - 1, maxQty)}
                                  disabled={isTotal || qty <= 1}
                                  className="p-1 rounded hover:bg-gray-200 disabled:opacity-30"><Minus size={14} /></button>
                                <input type="number" min={1} max={maxQty} value={qty}
                                  onChange={(e) => updateCantidad(lote.id, e.target.value, maxQty)}
                                  disabled={isTotal}
                                  className="w-14 text-center border border-gray-300 rounded px-1 py-0.5 text-sm focus:ring-1 focus:ring-purple-500 outline-none" />
                                <button type="button" onClick={() => updateCantidad(lote.id, qty + 1, maxQty)}
                                  disabled={isTotal || qty >= maxQty}
                                  className="p-1 rounded hover:bg-gray-200 disabled:opacity-30"><Plus size={14} /></button>
                              </div>
                            ) : <span className="text-gray-300 text-center block">—</span>}
                          </td>
                          <td className="px-3 py-2 text-right">{formatCurrency(lote.costoUnitario)}</td>
                          <td className="px-3 py-2 text-right font-medium">
                            {(isSelected || isTotal) ? formatCurrency(Number(lote.costoUnitario) * qty) : '—'}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                  <tfoot className="bg-gray-50 font-medium">
                    <tr>
                      <td colSpan={tipoDevolucion === 'parcial' ? 6 : 5} className="px-3 py-2 text-right">Total a devolver:</td>
                      <td className="px-3 py-2 text-right text-purple-700 text-base">{formatCurrency(calcularTotalDevolucion())}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>
            )}
            {tipoDevolucion === 'parcial' && Object.keys(productosSeleccionados).length === 0 && lotes.length > 0 && (
              <p className="text-xs text-amber-600 mt-1">Seleccione al menos un lote para devolver</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Categoría del Motivo <span className="text-red-500">*</span></label>
            <Select {...register('categoriaMotivo')} options={CATEGORIA_MOTIVO_OPTIONS} placeholder="Seleccione un motivo" />
            {errors.categoriaMotivo && <p className="text-sm text-red-500 mt-1">{errors.categoriaMotivo.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Detalle del Motivo <span className="text-red-500">*</span></label>
            <Textarea {...register('detalleMotivo')} placeholder="Describa detalladamente el motivo de la devolución al proveedor..." rows={3} />
            {errors.detalleMotivo && <p className="text-sm text-red-500 mt-1">{errors.detalleMotivo.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Notas adicionales</label>
            <Textarea {...register('notas')} placeholder="Observaciones opcionales..." rows={2} />
          </div>

          <div className="flex justify-end gap-2 pt-4 border-t">
            <Button type="button" variant="outline" onClick={() => setIsFormOpen(false)}>Cancelar</Button>
            <Button type="submit" disabled={isCreating || Object.keys(productosSeleccionados).length === 0}>
              {isCreating ? (<><Loader2 size={16} className="animate-spin mr-1" />Registrando...</>) : 'Registrar Devolución'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* ═══════════════════════════════════════════════════════════
          Modal: Detalle de devolución (con tabla de productos)
         ═══════════════════════════════════════════════════════════ */}
      <Modal isOpen={isDetailOpen} onClose={() => setIsDetailOpen(false)} title="Detalle de Devolución a Proveedor" size="xl">
        {selected && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div><p className="text-xs text-gray-500 uppercase tracking-wider">N° Devolución</p><p className="font-medium">{selected.numeroDevolucion || '—'}</p></div>
              <div><p className="text-xs text-gray-500 uppercase tracking-wider">Estado</p><Badge variant={ESTADO_COLORS[selected.estado] || 'default'}>{ESTADO_LABELS[selected.estado] || selected.estado}</Badge></div>
              <div><p className="text-xs text-gray-500 uppercase tracking-wider">Tipo</p><p className="font-medium capitalize">{selected.tipoDevolucion || '—'}</p></div>
              <div><p className="text-xs text-gray-500 uppercase tracking-wider">Método de Reembolso</p><p className="font-medium capitalize">{(selected.metodoReembolso || '').replace(/_/g, ' ') || '—'}</p></div>
              <div><p className="text-xs text-gray-500 uppercase tracking-wider">Motivo</p><p className="font-medium">{MOTIVO_LABELS[selected.categoriaMotivo] || selected.categoriaMotivo || '—'}</p></div>
              <div><p className="text-xs text-gray-500 uppercase tracking-wider">Monto Total</p><p className="font-medium text-lg">{formatCurrency(selected.total)}</p></div>
            </div>

            {/* ─── Tabla de productos devueltos ─── */}
            <div>
              <p className="text-xs text-gray-500 uppercase tracking-wider mb-2">Productos / Lotes en devolución</p>
              {loadingDetalleDevolucion ? (
                <div className="flex items-center justify-center py-4"><Loader2 size={18} className="animate-spin text-purple-600" /><span className="ml-2 text-sm text-gray-500">Cargando productos...</span></div>
              ) : detalleDevolucionItems.length === 0 ? (
                <p className="text-sm text-gray-400 bg-gray-50 p-3 rounded-lg">No hay productos registrados en esta devolución</p>
              ) : (
                <div className="border border-gray-200 rounded-lg overflow-x-auto">
                  <table className="w-full text-sm min-w-[650px]">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-3 py-2 text-left">#</th>
                        <th className="px-3 py-2 text-left">Lote</th>
                        <th className="px-3 py-2 text-left">Producto</th>
                        <th className="px-3 py-2 text-center">Cantidad</th>
                        <th className="px-3 py-2 text-right">Costo Unit.</th>
                        <th className="px-3 py-2 text-right">Total</th>
                        <th className="px-3 py-2 text-center">Condición</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                      {detalleDevolucionItems.map((det, i) => (
                        <tr key={det.id}>
                          <td className="px-3 py-2 text-gray-500">{i + 1}</td>
                          <td className="px-3 py-2 font-mono text-xs">{det.lote?.numeroLote || '—'}</td>
                          <td className="px-3 py-2 font-medium">{det.producto?.nombre || `Producto #${det.producto?.id || det.productoId}`}</td>
                          <td className="px-3 py-2 text-center">{det.cantidad}</td>
                          <td className="px-3 py-2 text-right">{formatCurrency(det.precioUnitario)}</td>
                          <td className="px-3 py-2 text-right font-medium">{formatCurrency(det.total)}</td>
                          <td className="px-3 py-2 text-center">
                            <Badge variant={det.estadoCondicion === 'bueno' ? 'success' : 'warning'}>
                              {det.estadoCondicion || 'N/A'}
                            </Badge>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot className="bg-gray-50 font-medium">
                      <tr>
                        <td colSpan={5} className="px-3 py-2 text-right">Total devolución:</td>
                        <td className="px-3 py-2 text-right text-purple-700">{formatCurrency(detalleDevolucionItems.reduce((s, d) => s + Number(d.total || 0), 0))}</td>
                        <td></td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
              )}
            </div>

            {selected.detalleMotivo && (<div><p className="text-xs text-gray-500 uppercase tracking-wider mb-1">Detalle del Motivo</p><p className="text-sm text-gray-700 bg-gray-50 p-3 rounded-lg">{selected.detalleMotivo}</p></div>)}
            {selected.notas && (<div><p className="text-xs text-gray-500 uppercase tracking-wider mb-1">Notas</p><p className="text-sm text-gray-700 bg-gray-50 p-3 rounded-lg">{selected.notas}</p></div>)}
            <div className="grid grid-cols-2 gap-4 pt-2 border-t">
              <div><p className="text-xs text-gray-500 uppercase tracking-wider">Fecha de Solicitud</p><p className="text-sm">{formatDateTime(selected.solicitadoEn || selected.creadoEn)}</p></div>
              {selected.aprobadoEn && (<div><p className="text-xs text-gray-500 uppercase tracking-wider">Fecha de Aprobación</p><p className="text-sm">{formatDateTime(selected.aprobadoEn)}</p></div>)}
              {selected.completadoEn && (<div><p className="text-xs text-gray-500 uppercase tracking-wider">Fecha de Completado</p><p className="text-sm">{formatDateTime(selected.completadoEn)}</p></div>)}
              {selected.rechazadoEn && (<div><p className="text-xs text-gray-500 uppercase tracking-wider">Fecha de Rechazo</p><p className="text-sm">{formatDateTime(selected.rechazadoEn)}</p></div>)}
            </div>
            {selected.razonRechazo && (<div><p className="text-xs text-gray-500 uppercase tracking-wider mb-1">Razón de Rechazo</p><p className="text-sm text-red-600 bg-red-50 p-3 rounded-lg">{selected.razonRechazo}</p></div>)}
            {selected.estado === 'solicitada' && (
              <div className="flex justify-end gap-2 pt-3 border-t">
                <Button variant="outline" onClick={() => { setIsDetailOpen(false); handleOpenReject(selected); }} className="text-red-600 border-red-300 hover:bg-red-50"><XCircle size={16} className="mr-1" />Rechazar</Button>
                <Button onClick={() => { handleAprobar(selected); setIsDetailOpen(false); }} disabled={isAprobando}><CheckCircle size={16} className="mr-1" />{isAprobando ? 'Aprobando...' : 'Aprobar'}</Button>
              </div>
            )}
          </div>
        )}
      </Modal>

      {/* Modal: Rechazar devolución */}
      <Modal isOpen={isRejectOpen} onClose={() => setIsRejectOpen(false)} title="Rechazar Devolución" size="sm">
        <div className="space-y-4">
          <p className="text-gray-600">¿Está seguro de rechazar la devolución <span className="font-semibold">{selected?.numeroDevolucion}</span>?</p>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Razón del rechazo</label>
            <Textarea value={razonRechazo} onChange={(e) => setRazonRechazo(e.target.value)} placeholder="Indique la razón del rechazo..." rows={3} />
          </div>
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setIsRejectOpen(false)}>Cancelar</Button>
            <Button variant="danger" onClick={handleRechazar} disabled={isRechazando}>
              {isRechazando ? (<><Loader2 size={16} className="animate-spin mr-1" />Rechazando...</>) : 'Confirmar Rechazo'}
            </Button>
          </div>
        </div>
      </Modal>

      {/* Modal: Confirmar eliminación */}
      <Modal isOpen={isDeleteOpen} onClose={() => setIsDeleteOpen(false)} title="Confirmar Eliminación" size="sm">
        <div className="space-y-4">
          <p className="text-gray-600">¿Está seguro de eliminar la devolución <span className="font-semibold">{selected?.numeroDevolucion}</span>? Esta acción no se puede deshacer.</p>
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setIsDeleteOpen(false)}>Cancelar</Button>
            <Button variant="danger" onClick={handleDelete} disabled={isDeleting}>
              {isDeleting ? (<><Loader2 size={16} className="animate-spin mr-1" />Eliminando...</>) : 'Eliminar'}
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
