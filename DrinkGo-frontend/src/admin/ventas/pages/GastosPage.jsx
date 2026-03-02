import { useState } from 'react';
import {
  TrendingDown,
  FileText,
  Plus,
  CreditCard,
  CheckCircle,
  Pencil,
  Trash2,
  Loader2,
  RefreshCw,
  CalendarClock,
  Receipt,
  DollarSign,
  AlertCircle,
  RotateCcw,
} from 'lucide-react';
import { Card } from '@/admin/components/ui/Card';
import { Button } from '@/admin/components/ui/Button';
import { Badge } from '@/admin/components/ui/Badge';
import { Table } from '@/admin/components/ui/Table';
import { Modal } from '@/admin/components/ui/Modal';
import { Input } from '@/admin/components/ui/Input';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { useGastos } from '../hooks/useGastos';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';
import { useAdminAuthStore } from '@/stores/adminAuthStore';

/* ─────────────────────────────────────────── */
/*  Helpers                                    */
/* ─────────────────────────────────────────── */

const ESTADO_FACTURA_BADGE = {
  pendiente: 'warning',
  pagada: 'success',
  vencida: 'error',
  cancelada: 'error',
  anulada: 'error',
  fallida: 'error',
};

const ESTADO_GASTO_BADGE = {
  pendiente: 'warning',
  aprobado: 'info',
  pagado: 'success',
  rechazado: 'error',
  anulado: 'error',
};

const ESTADO_FACTURA_LABEL = {
  pendiente: 'Pendiente',
  pagada: 'Pagada',
  vencida: 'Vencida',
  cancelada: 'Cancelada',
  anulada: 'Anulada',
  fallida: 'Fallida',
};

const ESTADO_GASTO_LABEL = {
  pendiente: 'Pendiente',
  aprobado: 'Aprobado',
  pagado: 'Pagado',
  rechazado: 'Rechazado',
  anulado: 'Anulado',
};

const PERIODO_LABEL = {
  semanal: 'Semanal',
  quincenal: 'Quincenal',
  mensual: 'Mensual',
  trimestral: 'Trimestral',
  anual: 'Anual',
};

const METODOS_PAGO = [
  { value: 'efectivo', label: 'Efectivo' },
  { value: 'transferencia_bancaria', label: 'Transferencia bancaria' },
  { value: 'tarjeta_credito', label: 'Tarjeta de crédito' },
  { value: 'cheque', label: 'Cheque' },
  { value: 'otro', label: 'Otro' },
];

const PERIODOS = [
  { value: 'semanal', label: 'Semanal' },
  { value: 'quincenal', label: 'Quincenal' },
  { value: 'mensual', label: 'Mensual' },
  { value: 'trimestral', label: 'Trimestral' },
  { value: 'anual', label: 'Anual' },
];

/* ─────────────────────────────────────────── */
/*  Modal: Registrar Pago                      */
/* ─────────────────────────────────────────── */

const PagoModal = ({ isOpen, onClose, titulo, monto, onConfirm, isLoading }) => {
  const [metodoPago, setMetodoPago] = useState('efectivo');
  const [referencia, setReferencia] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onConfirm({ metodoPago, referencia });
  };

  const handleClose = () => {
    setMetodoPago('efectivo');
    setReferencia('');
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title={titulo || 'Registrar Pago'} size="sm">
      <form onSubmit={handleSubmit} className="space-y-4">
        {monto && (
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 flex items-center gap-2">
            <DollarSign size={16} className="text-blue-600" />
            <span className="text-sm text-blue-800">
              Monto a pagar: <strong>{formatCurrency(monto)}</strong>
            </span>
          </div>
        )}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Método de pago <span className="text-red-500">*</span>
          </label>
          <select
            value={metodoPago}
            onChange={(e) => setMetodoPago(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            required
          >
            {METODOS_PAGO.map((m) => (
              <option key={m.value} value={m.value}>{m.label}</option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Referencia / Número de operación
          </label>
          <Input
            value={referencia}
            onChange={(e) => setReferencia(e.target.value)}
            placeholder="Nro. operación, voucher, etc."
          />
        </div>
        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancelar
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? (
              <><Loader2 size={14} className="animate-spin mr-2" />Procesando...</>
            ) : (
              <><CheckCircle size={14} className="mr-2" />Confirmar Pago</>
            )}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

/* ─────────────────────────────────────────── */
/*  Modal: Crear / Editar Gasto                */
/* ─────────────────────────────────────────── */

const GastoFormModal = ({ isOpen, onClose, initialData, categorias, negocioId, onSave, isLoading }) => {
  const isEdit = !!initialData?.id;

  const [form, setForm] = useState(() => ({
    descripcion: initialData?.descripcion || '',
    categoriaId: initialData?.categoria?.id || '',
    monto: initialData?.monto || '',
    fechaGasto: initialData?.fechaGasto || new Date().toISOString().split('T')[0],
    esRecurrente: initialData?.esRecurrente || false,
    periodoRecurrencia: initialData?.periodoRecurrencia || 'mensual',
    metodoPago: initialData?.metodoPago || 'efectivo',
    notas: initialData?.notas || '',
  }));

  const set = (field) => (e) => {
    const val = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm((prev) => ({ ...prev, [field]: val }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      ...(isEdit ? { id: initialData.id } : {}),
      negocio: { id: negocioId },
      descripcion: form.descripcion,
      categoria: { id: Number(form.categoriaId) },
      monto: parseFloat(form.monto),
      montoImpuesto: 0,
      total: parseFloat(form.monto),
      fechaGasto: form.fechaGasto,
      esRecurrente: form.esRecurrente,
      periodoRecurrencia: form.esRecurrente ? form.periodoRecurrencia : null,
      metodoPago: form.metodoPago,
      notas: form.notas || null,
      moneda: 'PEN',
    };
    onSave(payload);
  };

  const handleClose = () => {
    setForm({
      descripcion: '',
      categoriaId: '',
      monto: '',
      fechaGasto: new Date().toISOString().split('T')[0],
      esRecurrente: false,
      periodoRecurrencia: 'mensual',
      metodoPago: 'efectivo',
      notas: '',
    });
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={isEdit ? 'Editar Gasto' : 'Registrar Gasto Externo'}
      size="md"
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Descripción */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Descripción <span className="text-red-500">*</span>
          </label>
          <Input
            value={form.descripcion}
            onChange={set('descripcion')}
            placeholder="Ej: Alquiler de local, Luz, Agua..."
            required
          />
        </div>

        {/* Fila: Categoría + Monto */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Categoría <span className="text-red-500">*</span>
            </label>
            <select
              value={form.categoriaId}
              onChange={set('categoriaId')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              required
            >
              <option value="">Seleccionar...</option>
              {categorias.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Monto (S/) <span className="text-red-500">*</span>
            </label>
            <Input
              type="number"
              step="0.01"
              min="0"
              value={form.monto}
              onChange={set('monto')}
              placeholder="0.00"
              required
            />
          </div>
        </div>

        {/* Fila: Fecha + Método pago */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha del gasto <span className="text-red-500">*</span>
            </label>
            <Input
              type="date"
              value={form.fechaGasto}
              onChange={set('fechaGasto')}
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Método de pago
            </label>
            <select
              value={form.metodoPago}
              onChange={set('metodoPago')}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              {METODOS_PAGO.map((m) => (
                <option key={m.value} value={m.value}>{m.label}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Recurrencia */}
        <div className="flex items-center gap-3 flex-wrap">
          <label className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={form.esRecurrente}
              onChange={set('esRecurrente')}
              className="rounded border-gray-300 text-blue-600"
            />
            Gasto recurrente
          </label>
          {form.esRecurrente && (
            <select
              value={form.periodoRecurrencia}
              onChange={set('periodoRecurrencia')}
              className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:ring-2 focus:ring-blue-500"
            >
              {PERIODOS.map((p) => (
                <option key={p.value} value={p.value}>{p.label}</option>
              ))}
            </select>
          )}
        </div>

        {/* Notas */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Notas</label>
          <textarea
            value={form.notas}
            onChange={set('notas')}
            rows={2}
            placeholder="Observaciones opcionales..."
            className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm resize-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        <div className="flex justify-end gap-2 pt-2 border-t border-gray-100">
          <Button type="button" variant="outline" onClick={handleClose} disabled={isLoading}>
            Cancelar
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? (
              <><Loader2 size={14} className="animate-spin mr-2" />Guardando...</>
            ) : isEdit ? (
              <><Pencil size={14} className="mr-2" />Actualizar</>
            ) : (
              <><Plus size={14} className="mr-2" />Registrar Gasto</>
            )}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

/* ─────────────────────────────────────────── */
/*  Tab 1: Facturas del Sistema                */
/* ─────────────────────────────────────────── */

const FacturasSistemaTab = ({
  facturas,
  isLoading,
  onPagar,
  isPagando,
}) => {
  const [pagarTarget, setPagarTarget] = useState(null);

  const pendientes = facturas.filter((f) => f.estado === 'pendiente');
  const totalPendiente = pendientes.reduce((sum, f) => sum + Number(f.total || 0), 0);

  const columns = [
    { key: 'numero', title: 'N° Factura', render: (_, f) => (
      <span className="font-mono text-xs font-semibold text-blue-700">{f.numeroFactura}</span>
    )},
    { key: 'periodo', title: 'Período', render: (_, f) => (
      <span className="text-xs text-gray-600">
        {formatDate(f.inicioPeriodo)} – {formatDate(f.finPeriodo)}
      </span>
    )},
    { key: 'total', title: 'Total', render: (_, f) => (
      <span className="font-semibold text-gray-900">{formatCurrency(f.total)}</span>
    )},
    { key: 'vencimiento', title: 'Vencimiento', render: (_, f) => {
      const isVencida = f.estado === 'pendiente' && new Date(f.fechaVencimiento) < new Date();
      return (
        <span className={`text-xs ${isVencida ? 'text-red-600 font-semibold' : 'text-gray-600'}`}>
          {formatDate(f.fechaVencimiento)}
          {isVencida && <span className="ml-1">(Vencida)</span>}
        </span>
      );
    }},
    { key: 'estado', title: 'Estado', render: (_, f) => (
      <Badge variant={ESTADO_FACTURA_BADGE[f.estado] || 'info'}>
        {ESTADO_FACTURA_LABEL[f.estado] || f.estado}
      </Badge>
    )},
    { key: 'pagadoEn', title: 'Pagado el', render: (_, f) => (
      <span className="text-xs text-gray-500">
        {f.pagadoEn ? formatDate(f.pagadoEn) : '—'}
      </span>
    )},
    { key: 'acciones', title: '', render: (_, f) => {
      const puedesPagar = f.estado === 'pendiente' || f.estado === 'vencida';
      return puedesPagar ? (
        <Button
          size="sm"
          onClick={() => setPagarTarget(f)}
          disabled={isPagando}
        >
          <CreditCard size={13} className="mr-1" />
          Pagar
        </Button>
      ) : null;
    }},
  ];

  return (
    <div className="space-y-4">
      {/* Summary card */}
      {pendientes.length > 0 && (
        <div className="bg-amber-50 border border-amber-200 rounded-lg p-4 flex items-start gap-3">
          <AlertCircle size={18} className="text-amber-600 mt-0.5 shrink-0" />
          <div>
            <p className="text-sm font-semibold text-amber-800">
              {pendientes.length} factura{pendientes.length > 1 ? 's' : ''} pendiente{pendientes.length > 1 ? 's' : ''} de pago
            </p>
            <p className="text-sm text-amber-700">
              Total a pagar: <strong>{formatCurrency(totalPendiente)}</strong>
            </p>
          </div>
        </div>
      )}

      <Card>
        <Table
          columns={columns}
          data={facturas}
          loading={isLoading}
          emptyText="No hay facturas del sistema para este negocio"
        />
      </Card>

      {/* Modal pago */}
      <PagoModal
        isOpen={!!pagarTarget}
        onClose={() => setPagarTarget(null)}
        titulo="Pagar Factura del Sistema"
        monto={pagarTarget?.total}
        isLoading={isPagando}
        onConfirm={async ({ metodoPago, referencia }) => {
          await onPagar({ id: pagarTarget.id, metodoPago, referencia });
          setPagarTarget(null);
        }}
      />
    </div>
  );
};

/* ─────────────────────────────────────────── */
/*  Tab 2: Gastos Externos                     */
/* ─────────────────────────────────────────── */

const GastosExternosTab = ({
  gastos,
  categorias,
  negocioId,
  isLoading,
  onCrear,
  onActualizar,
  onEliminar,
  onPagar,
  isCreating,
  isUpdating,
  isDeleting,
  isPagando,
}) => {
  const [formOpen, setFormOpen] = useState(false);
  const [editTarget, setEditTarget] = useState(null);
  const [pagarTarget, setPagarTarget] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  const handleOpenCreate = () => {
    setEditTarget(null);
    setFormOpen(true);
  };

  const handleOpenEdit = (g) => {
    setEditTarget(g);
    setFormOpen(true);
  };

  const handleSave = async (payload) => {
    if (editTarget) {
      await onActualizar(payload);
    } else {
      await onCrear(payload);
    }
    setFormOpen(false);
    setEditTarget(null);
  };

  const columns = [
    { key: 'numero', title: 'N° Gasto', render: (_, g) => (
      <span className="font-mono text-xs font-semibold text-gray-700">{g.numeroGasto || '—'}</span>
    )},
    { key: 'descripcion', title: 'Descripción', render: (_, g) => (
      <div>
        <p className="text-sm font-medium text-gray-900 truncate max-w-[180px]">{g.descripcion}</p>
        {g.esRecurrente && (
          <span className="inline-flex items-center gap-1 text-xs text-indigo-600 mt-0.5">
            <RotateCcw size={10} />
            {PERIODO_LABEL[g.periodoRecurrencia] || g.periodoRecurrencia}
          </span>
        )}
      </div>
    )},
    { key: 'categoria', title: 'Categoría', render: (_, g) => (
      <span className="text-xs bg-gray-100 text-gray-700 px-2 py-0.5 rounded-full">
        {g.categoria?.nombre || '—'}
      </span>
    )},
    { key: 'total', title: 'Total', render: (_, g) => (
      <span className="font-semibold text-gray-900">{formatCurrency(g.total || g.monto)}</span>
    )},
    { key: 'fecha', title: 'Fecha', render: (_, g) => (
      <span className="text-xs text-gray-600">{formatDate(g.fechaGasto)}</span>
    )},
    { key: 'estado', title: 'Estado', render: (_, g) => (
      <Badge variant={ESTADO_GASTO_BADGE[g.estado] || 'info'}>
        {ESTADO_GASTO_LABEL[g.estado] || g.estado}
      </Badge>
    )},
    { key: 'acciones', title: '', render: (_, g) => (
      <div className="flex items-center gap-1">
        {(g.estado === 'pendiente' || g.estado === 'aprobado') && (
          <Button size="sm" onClick={() => setPagarTarget(g)} disabled={isPagando}>
            <CreditCard size={13} className="mr-1" />
            Pagar
          </Button>
        )}
        <Button
          size="sm"
          variant="outline"
          onClick={() => handleOpenEdit(g)}
          className="p-1.5"
          title="Editar"
        >
          <Pencil size={13} />
        </Button>
        <Button
          size="sm"
          variant="outline"
          onClick={() => setDeleteTarget(g)}
          className="p-1.5 text-red-500 hover:text-red-700 border-red-200 hover:border-red-400"
          title="Eliminar"
        >
          <Trash2 size={13} />
        </Button>
      </div>
    )},
  ];

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button onClick={handleOpenCreate}>
          <Plus size={15} className="mr-1.5" />
          Registrar Gasto
        </Button>
      </div>

      <Card>
        <Table
          columns={columns}
          data={gastos}
          loading={isLoading}
          emptyText="No hay gastos externos registrados"
        />
      </Card>

      {/* Modales */}
      <GastoFormModal
        isOpen={formOpen}
        onClose={() => { setFormOpen(false); setEditTarget(null); }}
        initialData={editTarget}
        categorias={categorias}
        negocioId={negocioId}
        onSave={handleSave}
        isLoading={isCreating || isUpdating}
      />

      <PagoModal
        isOpen={!!pagarTarget}
        onClose={() => setPagarTarget(null)}
        titulo="Registrar Pago de Gasto"
        monto={pagarTarget?.total || pagarTarget?.monto}
        isLoading={isPagando}
        onConfirm={async ({ metodoPago, referencia }) => {
          await onPagar({ id: pagarTarget.id, metodoPago, referencia });
          setPagarTarget(null);
        }}
      />

      <ConfirmDialog
        isOpen={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        onConfirm={async () => {
          await onEliminar(deleteTarget.id);
          setDeleteTarget(null);
        }}
        isLoading={isDeleting}
        title="Eliminar gasto"
        message={`¿Eliminar el gasto "${deleteTarget?.descripcion}"? Esta acción no se puede deshacer.`}
        confirmText="Eliminar"
        variant="danger"
      />
    </div>
  );
};

/* ─────────────────────────────────────────── */
/*  Page Principal                             */
/* ─────────────────────────────────────────── */

const TABS = [
  { id: 'facturas', label: 'Facturas del Sistema', icon: FileText },
  { id: 'externos', label: 'Gastos Externos', icon: Receipt },
];

export const GastosPage = () => {
  const [activeTab, setActiveTab] = useState('facturas');
  const { negocio } = useAdminAuthStore();

  const {
    negocioId,
    gastos,
    categorias,
    facturasServicio,
    isLoadingGastos,
    isLoadingFacturas,
    crearGasto,
    actualizarGasto,
    eliminarGasto,
    pagarGasto,
    pagarFacturaServicio,
    isCreating,
    isUpdating,
    isDeleting,
    isPagandoGasto,
    isPagandoFactura,
  } = useGastos();

  /* Resumen rápido */
  const pendientesFacturas = facturasServicio.filter((f) => f.estado === 'pendiente' || f.estado === 'vencida').length;
  const pendientesGastos = gastos.filter((g) => g.estado === 'pendiente' || g.estado === 'aprobado').length;
  const totalPendienteFacturas = facturasServicio
    .filter((f) => f.estado === 'pendiente' || f.estado === 'vencida')
    .reduce((s, f) => s + Number(f.total || 0), 0);
  const totalPendienteGastos = gastos
    .filter((g) => g.estado === 'pendiente' || g.estado === 'aprobado')
    .reduce((s, g) => s + Number(g.total || g.monto || 0), 0);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
            <TrendingDown size={24} className="text-red-500" />
            Gastos
          </h1>
          <p className="text-sm text-gray-500 mt-1">
            Control de facturas del sistema y gastos externos del negocio
          </p>
        </div>
      </div>

      {/* Stat cards resumen */}
      <div className="grid grid-cols-2 gap-4">
        <div className="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-4 shadow-sm">
          <div className="w-10 h-10 bg-amber-100 rounded-lg flex items-center justify-center">
            <FileText size={18} className="text-amber-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Facturas pendientes</p>
            <p className="text-lg font-bold text-gray-900">
              {pendientesFacturas}
              <span className="text-sm font-normal text-gray-500 ml-2">
                ({formatCurrency(totalPendienteFacturas)})
              </span>
            </p>
          </div>
        </div>
        <div className="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-4 shadow-sm">
          <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
            <Receipt size={18} className="text-red-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Gastos externos pendientes</p>
            <p className="text-lg font-bold text-gray-900">
              {pendientesGastos}
              <span className="text-sm font-normal text-gray-500 ml-2">
                ({formatCurrency(totalPendienteGastos)})
              </span>
            </p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="flex gap-1">
          {TABS.map((tab) => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-2.5 text-sm font-medium border-b-2 transition-colors ${
                  isActive
                    ? 'border-blue-600 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                }`}
              >
                <Icon size={15} />
                {tab.label}
                {tab.id === 'facturas' && pendientesFacturas > 0 && (
                  <span className="bg-amber-100 text-amber-700 text-xs font-semibold px-1.5 py-0.5 rounded-full">
                    {pendientesFacturas}
                  </span>
                )}
                {tab.id === 'externos' && pendientesGastos > 0 && (
                  <span className="bg-red-100 text-red-700 text-xs font-semibold px-1.5 py-0.5 rounded-full">
                    {pendientesGastos}
                  </span>
                )}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Tab content */}
      {activeTab === 'facturas' && (
        <FacturasSistemaTab
          facturas={facturasServicio}
          isLoading={isLoadingFacturas}
          onPagar={pagarFacturaServicio}
          isPagando={isPagandoFactura}
        />
      )}

      {activeTab === 'externos' && (
        <GastosExternosTab
          gastos={gastos}
          categorias={categorias}
          negocioId={negocioId}
          isLoading={isLoadingGastos}
          onCrear={crearGasto}
          onActualizar={actualizarGasto}
          onEliminar={eliminarGasto}
          onPagar={pagarGasto}
          isCreating={isCreating}
          isUpdating={isUpdating}
          isDeleting={isDeleting}
          isPagando={isPagandoGasto}
        />
      )}
    </div>
  );
};
