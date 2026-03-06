import { useState, useMemo } from 'react';
import {
  TrendingDown,
  Plus,
  CreditCard,
  CheckCircle,
  Pencil,
  Trash2,
  Loader2,
  RefreshCw,
  Receipt,
  DollarSign,
  RotateCcw,
  Clock,
  CalendarClock,
  History,
  X,
  Upload,
  Eye,
  AlertCircle,
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

const METODO_PAGO_LABEL = {
  efectivo: 'Efectivo',
  transferencia_bancaria: 'Transferencia',
  tarjeta_credito: 'Tarjeta de crédito',
  cheque: 'Cheque',
  otro: 'Otro',
};

const ESTADO_GASTO_BADGE = {
  pendiente: 'warning',
  aprobado: 'info',
  pagado: 'success',
  rechazado: 'error',
  anulado: 'error',
};

const ESTADO_GASTO_LABEL = {
  pendiente: 'Pendiente',
  aprobado: 'Aprobado',
  pagado: 'Pagado',
  rechazado: 'Rechazado',
  anulado: 'Anulado',
};

const PERIODO_LABEL = {
  diario: 'Diario',
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
  { value: 'diario', label: 'Diario' },
  { value: 'semanal', label: 'Semanal' },
  { value: 'quincenal', label: 'Quincenal' },
  { value: 'mensual', label: 'Mensual' },
  { value: 'trimestral', label: 'Trimestral' },
  { value: 'anual', label: 'Anual' },
];

/** Fecha local en formato YYYY-MM-DD (sin desfase UTC) */
const hoyLocal = () => {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
};

/* ─────────────────────────────────────────── */
/*  Modal: Registrar Pago                      */
/* ─────────────────────────────────────────── */

const ACCEPTED_FILE_TYPES = 'image/jpeg,image/png,image/webp,application/pdf';

const PagoModal = ({ isOpen, onClose, titulo, monto, onConfirm, isLoading }) => {
  const [metodoPago, setMetodoPago] = useState('efectivo');
  const [referencia, setReferencia] = useState('');
  const [archivoComprobante, setArchivoComprobante] = useState(null);

  const handleSubmit = (e) => {
    e.preventDefault();
    onConfirm({ metodoPago, referencia, archivoComprobante });
  };

  const handleClose = () => {
    setMetodoPago('efectivo');
    setReferencia('');
    setArchivoComprobante(null);
    onClose();
  };

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        e.target.value = '';
        return;
      }
      setArchivoComprobante(file);
    }
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
        {/* Comprobante upload */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Comprobante de pago
          </label>
          {archivoComprobante ? (
            <div className="flex items-center gap-2 border border-green-200 bg-green-50 rounded-lg px-3 py-2">
              <CheckCircle size={14} className="text-green-600 shrink-0" />
              <span className="text-sm text-green-800 truncate flex-1">{archivoComprobante.name}</span>
              <button
                type="button"
                onClick={() => setArchivoComprobante(null)}
                className="text-gray-400 hover:text-red-500 transition-colors shrink-0"
              >
                <X size={14} />
              </button>
            </div>
          ) : (
            <label className="flex items-center justify-center gap-2 border-2 border-dashed border-gray-300 rounded-lg px-3 py-3 cursor-pointer hover:border-blue-400 hover:bg-blue-50/50 transition-colors">
              <Upload size={16} className="text-gray-400" />
              <span className="text-sm text-gray-500">Seleccionar archivo</span>
              <input
                type="file"
                accept={ACCEPTED_FILE_TYPES}
                onChange={handleFileChange}
                className="hidden"
              />
            </label>
          )}
          <p className="text-xs text-gray-400 mt-1">JPG, PNG, WebP o PDF (máx. 5 MB)</p>
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

const GastoFormModal = ({ isOpen, onClose, initialData, negocioId, onSave, isLoading, categorias = [], onCrearCategoria, isCreatingCategoria }) => {
  const isEdit = !!initialData?.id;
  const [showNewCategoria, setShowNewCategoria] = useState(false);
  const [newCategoriaNombre, setNewCategoriaNombre] = useState('');

  const handleCrearCategoria = async () => {
    if (!newCategoriaNombre.trim()) return;
    try {
      const created = await onCrearCategoria({
        negocio: { id: negocioId },
        nombre: newCategoriaNombre.trim(),
        tipo: 'operativo',
      });
      setForm((prev) => ({ ...prev, categoriaGastoId: created?.id || '' }));
      setNewCategoriaNombre('');
      setShowNewCategoria(false);
    } catch { /* error handled by hook */ }
  };

  const [form, setForm] = useState(() => ({
    descripcion: initialData?.descripcion || '',
    monto: initialData?.monto || '',
    fechaGasto: initialData?.fechaGasto || hoyLocal(),
    horaGasto: initialData?.horaGasto || '',
    esRecurrente: initialData?.esRecurrente || false,
    periodoRecurrencia: initialData?.periodoRecurrencia || 'mensual',
    metodoPago: initialData?.metodoPago || 'efectivo',
    categoriaGastoId: initialData?.categoriaGasto?.id || '',
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
      monto: parseFloat(form.monto),
      montoImpuesto: 0,
      total: parseFloat(form.monto),
      fechaGasto: form.fechaGasto,
      horaGasto: form.horaGasto || null,
      esRecurrente: form.esRecurrente,
      periodoRecurrencia: form.esRecurrente ? form.periodoRecurrencia : null,
      metodoPago: form.metodoPago,
      moneda: 'PEN',
      categoriaGasto: form.categoriaGastoId ? { id: Number(form.categoriaGastoId) } : null,
    };
    onSave(payload);
  };

  const handleClose = () => {
    setForm({
      descripcion: '',
      monto: '',
      fechaGasto: hoyLocal(),
      horaGasto: '',
      esRecurrente: false,
      periodoRecurrencia: 'mensual',
      metodoPago: 'efectivo',
      categoriaGastoId: '',
    });
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      title={isEdit ? 'Editar Gasto' : 'Programar Gasto'}
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

        {/* Categoría */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Categoría de Gasto
          </label>
          {!showNewCategoria ? (
            <div className="flex gap-2">
              <select
                value={form.categoriaGastoId}
                onChange={set('categoriaGastoId')}
                className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Sin categoría</option>
                {categorias.map((c) => (
                  <option key={c.id} value={c.id}>{c.nombre}</option>
                ))}
              </select>
              <button
                type="button"
                onClick={() => setShowNewCategoria(true)}
                className="shrink-0 w-9 h-9 flex items-center justify-center rounded-lg border border-dashed border-gray-300 text-gray-500 hover:border-blue-400 hover:text-blue-600 transition-colors"
                title="Crear nueva categoría"
              >
                <Plus size={16} />
              </button>
            </div>
          ) : (
            <div className="flex gap-2">
              <Input
                value={newCategoriaNombre}
                onChange={(e) => setNewCategoriaNombre(e.target.value)}
                placeholder="Nombre de la categoría"
                className="flex-1"
                autoFocus
                onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); handleCrearCategoria(); } }}
              />
              <Button
                type="button"
                size="sm"
                onClick={handleCrearCategoria}
                disabled={isCreatingCategoria || !newCategoriaNombre.trim()}
                className="shrink-0"
              >
                {isCreatingCategoria ? <Loader2 size={14} className="animate-spin" /> : <Plus size={14} />}
              </Button>
              <button
                type="button"
                onClick={() => { setShowNewCategoria(false); setNewCategoriaNombre(''); }}
                className="shrink-0 w-9 h-9 flex items-center justify-center rounded-lg text-gray-400 hover:text-gray-600"
              >
                <X size={16} />
              </button>
            </div>
          )}
        </div>

        {/* Monto */}
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

        {/* Fila: Fecha + Hora */}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Fecha del cobro <span className="text-red-500">*</span>
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
              Hora del cobro
            </label>
            <Input
              type="time"
              value={form.horaGasto}
              onChange={set('horaGasto')}
            />
          </div>
        </div>

        {/* Método de pago */}
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

        {/* Recurrencia */}
        <div className="space-y-2">
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
            <>
              <div className="flex items-center gap-3 pl-6">
                <label className="text-sm text-gray-600 whitespace-nowrap">Periodo <span className="text-red-500">*</span></label>
                <select
                  value={form.periodoRecurrencia}
                  onChange={set('periodoRecurrencia')}
                  className="border border-gray-300 rounded-lg px-3 py-1.5 text-sm focus:ring-2 focus:ring-blue-500"
                  required
                >
                  {PERIODOS.map((p) => (
                    <option key={p.value} value={p.value}>{p.label}</option>
                  ))}
                </select>
              </div>
              <div className="bg-indigo-50 border border-indigo-200 rounded-lg px-3 py-2 text-xs text-indigo-700 flex items-start gap-2">
                <RefreshCw size={13} className="mt-0.5 shrink-0" />
                <span>
                  Este gasto se cobrará automáticamente cada periodo
                  ({PERIODO_LABEL[form.periodoRecurrencia]?.toLowerCase()}) a partir del {form.fechaGasto || '—'}{form.horaGasto ? ` a las ${form.horaGasto}` : ''}.
                </span>
              </div>
            </>
          )}
          {!form.esRecurrente && form.fechaGasto && (
            <div className="bg-blue-50 border border-blue-200 rounded-lg px-3 py-2 text-xs text-blue-700 flex items-start gap-2">
              <CalendarClock size={13} className="mt-0.5 shrink-0" />
              <span>
                Este gasto se cobrará el <strong>{form.fechaGasto}</strong>{form.horaGasto ? <> a las <strong>{form.horaGasto}</strong></> : ''} de forma automática.
              </span>
            </div>
          )}
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
              <><Plus size={14} className="mr-2" />Programar Gasto</>
            )}
          </Button>
        </div>
      </form>
    </Modal>
  );
};

/* ─────────────────────────────────────────── */
/*  Tab 1: Historial de Pagos                  */
/* ─────────────────────────────────────────── */

const HistorialPagosTab = ({ gastos, isLoading, onSubirComprobante, onEliminarComprobante, isSubiendoComprobante }) => {
  const totalPagado = gastos.reduce((sum, g) => sum + Number(g.total || g.monto || 0), 0);
  const sinComprobante = gastos.filter((g) => !g.urlComprobante).length;

  const handleUpload = (gastoId) => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/jpeg,image/png,image/webp,application/pdf';
    input.onchange = (e) => {
      const archivo = e.target.files?.[0];
      if (archivo) {
        onSubirComprobante({ id: gastoId, archivo });
      }
    };
    input.click();
  };

  const columns = [
    { key: 'numero', title: 'N° Gasto', render: (_, g) => (
      <span className="font-mono text-xs font-semibold text-gray-700">{g.numeroGasto || '—'}</span>
    )},
    { key: 'descripcion', title: 'Descripción', render: (_, g) => (
      <div>
        <p className="text-sm font-medium text-gray-900 truncate max-w-[220px]">{g.descripcion}</p>
        {g.referenciaPago && (
          <span className="text-xs text-gray-400">Ref: {g.referenciaPago}</span>
        )}
      </div>
    )},
    { key: 'categoria', title: 'Categoría', render: (_, g) => (
      <span className="text-sm text-gray-700">{g.categoriaGasto?.nombre || '—'}</span>
    )},
    { key: 'total', title: 'Total', render: (_, g) => (
      <span className="font-semibold text-gray-900">{formatCurrency(g.total || g.monto)}</span>
    )},
    { key: 'metodo', title: 'Método', render: (_, g) => (
      <span className="text-sm text-gray-700">
        {METODO_PAGO_LABEL[g.metodoPago] || g.metodoPago || '—'}
      </span>
    )},
    { key: 'fecha', title: 'Fecha', render: (_, g) => (
      <div className="text-xs text-gray-600">
        <span>{g.creadoEn ? formatDate(g.creadoEn) : '—'}</span>
      </div>
    )},
    { key: 'comprobante', title: 'Comprobante', width: '140px', align: 'center', render: (_, g) => {
      if (g.urlComprobante) {
        return (
          <div className="flex items-center justify-center gap-1">
            <a
              href={`/uploads/${g.urlComprobante}`}
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium text-blue-700 bg-blue-50 rounded-md hover:bg-blue-100 transition-colors"
              title="Ver comprobante"
            >
              <Eye size={13} />
              Ver
            </a>
            <button
              onClick={() => onEliminarComprobante(g.id)}
              className="p-1 text-gray-400 hover:text-red-500 transition-colors"
              title="Eliminar comprobante"
            >
              <Trash2 size={13} />
            </button>
          </div>
        );
      }
      return (
        <button
          onClick={() => handleUpload(g.id)}
          disabled={isSubiendoComprobante}
          className="inline-flex items-center gap-1 px-2.5 py-1 text-xs font-medium text-amber-700 bg-amber-50 border border-amber-200 rounded-md hover:bg-amber-100 transition-colors disabled:opacity-50"
          title="Subir comprobante"
        >
          {isSubiendoComprobante ? <Loader2 size={13} className="animate-spin" /> : <Upload size={13} />}
          Subir
        </button>
      );
    }},
  ];

  return (
    <div className="space-y-4">
      {gastos.length > 0 && (
        <div className="flex gap-3">
          <div className="flex-1 bg-green-50 border border-green-200 rounded-lg p-4 flex items-start gap-3">
            <CheckCircle size={18} className="text-green-600 mt-0.5 shrink-0" />
            <div>
              <p className="text-sm font-semibold text-green-800">
                {gastos.length} gasto{gastos.length > 1 ? 's' : ''} pagado{gastos.length > 1 ? 's' : ''}
              </p>
              <p className="text-sm text-green-700">
                Total pagado: <strong>{formatCurrency(totalPagado)}</strong>
              </p>
            </div>
          </div>
          {sinComprobante > 0 && (
            <div className="flex-1 bg-amber-50 border border-amber-200 rounded-lg p-4 flex items-start gap-3">
              <AlertCircle size={18} className="text-amber-600 mt-0.5 shrink-0" />
              <div>
                <p className="text-sm font-semibold text-amber-800">
                  {sinComprobante} sin comprobante
                </p>
                <p className="text-sm text-amber-700">
                  Suba el comprobante del proveedor para justificar el gasto
                </p>
              </div>
            </div>
          )}
        </div>
      )}

      <Card>
        <Table
          columns={columns}
          data={gastos}
          loading={isLoading}
          emptyText="Aún no hay pagos realizados"
        />
      </Card>
    </div>
  );
};

/* ─────────────────────────────────────────── */
/*  Tab 2: Gastos Externos                     */
/* ─────────────────────────────────────────── */

const GastosExternosTab = ({
  gastos,
  negocioId,
  isLoading,
  onCrear,
  onActualizar,
  onEliminar,
  onPagar,
  onSubirComprobante,
  isCreating,
  isUpdating,
  isDeleting,
  isPagando,
  isSubiendoComprobante,
  categorias,
  onCrearCategoria,
  isCreatingCategoria,
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
        <p className="text-sm font-medium text-gray-900 truncate max-w-[220px]">{g.descripcion}</p>
        {g.esRecurrente && (
          <span className="inline-flex items-center gap-1 text-xs text-indigo-600 mt-0.5">
            <RotateCcw size={10} />
            {PERIODO_LABEL[g.periodoRecurrencia] || g.periodoRecurrencia}
            {g.proximaEjecucion && (
              <span className="text-gray-400 ml-1">· Próx: {formatDate(g.proximaEjecucion)}</span>
            )}
          </span>
        )}
        {!g.esRecurrente && g.estado === 'pendiente' && (
          <span className="inline-flex items-center gap-1 text-xs text-blue-600 mt-0.5">
            <CalendarClock size={10} />
            Programado: {formatDate(g.fechaGasto)}{g.horaGasto ? ` a las ${g.horaGasto.slice(0,5)}` : ''}
          </span>
        )}
      </div>
    )},
    { key: 'categoria', title: 'Categoría', render: (_, g) => (
      <span className="text-sm text-gray-700">{g.categoriaGasto?.nombre || '—'}</span>
    )},
    { key: 'total', title: 'Total', render: (_, g) => (
      <span className="font-semibold text-gray-900">{formatCurrency(g.total || g.monto)}</span>
    )},
    { key: 'fecha', title: 'Fecha del cobro', render: (_, g) => (
      <div className="text-xs text-gray-600">
        <span>{formatDate(g.fechaGasto)}</span>
        {g.horaGasto && (
          <span className="flex items-center gap-0.5 text-gray-500 mt-0.5">
            <Clock size={10} />
            {g.horaGasto.slice(0, 5)}
          </span>
        )}
      </div>
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

  const recurrentes = gastos.filter((g) => g.esRecurrente);
  const costoMensualTab = useMemo(() => {
    return recurrentes.reduce((sum, g) => {
      const monto = Number(g.monto || 0);
      switch (g.periodoRecurrencia) {
        case 'diario': return sum + monto * 30;
        case 'semanal': return sum + monto * 4.33;
        case 'quincenal': return sum + monto * 2;
        case 'mensual': return sum + monto;
        case 'trimestral': return sum + monto / 3;
        case 'anual': return sum + monto / 12;
        default: return sum + monto;
      }
    }, 0);
  }, [recurrentes]);

  return (
    <div className="space-y-4">
      {/* Info recurrentes */}
      {recurrentes.length > 0 && (
        <div className="bg-indigo-50 border border-indigo-200 rounded-lg p-4 flex items-start gap-3">
          <RotateCcw size={18} className="text-indigo-600 mt-0.5 shrink-0" />
          <div>
            <p className="text-sm font-semibold text-indigo-800">
              {recurrentes.length} gasto{recurrentes.length > 1 ? 's' : ''} recurrente{recurrentes.length > 1 ? 's' : ''} activo{recurrentes.length > 1 ? 's' : ''}
            </p>
            <p className="text-sm text-indigo-700">
              Costo mensual estimado: <strong>{formatCurrency(costoMensualTab)}</strong>.
              Estos gastos se cobran automáticamente en cada periodo.
            </p>
          </div>
        </div>
      )}

      <div className="flex justify-end">
        <Button onClick={handleOpenCreate}>
          <Plus size={15} className="mr-1.5" />
          Programar Gasto
        </Button>
      </div>

      <Card>
        <Table
          columns={columns}
          data={gastos}
          loading={isLoading}
          emptyText="No hay gastos programados"
        />
      </Card>

      {/* Modales */}
      <GastoFormModal
        isOpen={formOpen}
        onClose={() => { setFormOpen(false); setEditTarget(null); }}
        initialData={editTarget}
        negocioId={negocioId}
        onSave={handleSave}
        isLoading={isCreating || isUpdating}
        categorias={categorias}
        onCrearCategoria={onCrearCategoria}
        isCreatingCategoria={isCreatingCategoria}
      />

      <PagoModal
        isOpen={!!pagarTarget}
        onClose={() => setPagarTarget(null)}
        titulo="Registrar Pago de Gasto"
        monto={pagarTarget?.total || pagarTarget?.monto}
        isLoading={isPagando || isSubiendoComprobante}
        onConfirm={async ({ metodoPago, referencia, archivoComprobante }) => {
          await onPagar({ id: pagarTarget.id, metodoPago, referencia });
          if (archivoComprobante) {
            await onSubirComprobante({ id: pagarTarget.id, archivo: archivoComprobante });
          }
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
  { id: 'historial', label: 'Historial de Pagos', icon: History },
  { id: 'programados', label: 'Gastos Programados', icon: CalendarClock },
];

export const GastosPage = () => {
  const [activeTab, setActiveTab] = useState('programados');
  const { negocio } = useAdminAuthStore();

  const {
    negocioId,
    gastos,
    categoriasGasto,
    isLoadingGastos,
    crearGasto,
    actualizarGasto,
    eliminarGasto,
    pagarGasto,
    isCreating,
    isUpdating,
    isDeleting,
    isPagandoGasto,
    crearCategoria,
    isCreatingCategoria,
    subirComprobante,
    eliminarComprobante,
    isSubiendoComprobante,
  } = useGastos();

  /* Separar gastos pagados vs pendientes/programados */
  const gastosPagados = useMemo(() => gastos.filter((g) => g.estado === 'pagado'), [gastos]);
  const gastosProgramados = useMemo(() => gastos.filter((g) => g.estado !== 'pagado'), [gastos]);

  const pendientesGastos = gastosProgramados.filter((g) => g.estado === 'pendiente' || g.estado === 'aprobado').length;
  const gastosRecurrentes = gastos.filter((g) => g.esRecurrente).length;
  const totalPagado = gastosPagados.reduce((s, g) => s + Number(g.total || g.monto || 0), 0);
  const totalPendienteGastos = gastosProgramados
    .filter((g) => g.estado === 'pendiente' || g.estado === 'aprobado')
    .reduce((s, g) => s + Number(g.total || g.monto || 0), 0);
  const costoMensualEstimado = useMemo(() => {
    return gastos.filter((g) => g.esRecurrente).reduce((sum, g) => {
      const monto = Number(g.monto || 0);
      switch (g.periodoRecurrencia) {
        case 'diario': return sum + monto * 30;
        case 'semanal': return sum + monto * 4.33;
        case 'quincenal': return sum + monto * 2;
        case 'mensual': return sum + monto;
        case 'trimestral': return sum + monto / 3;
        case 'anual': return sum + monto / 12;
        default: return sum + monto;
      }
    }, 0);
  }, [gastos]);

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
            Programa y controla los gastos automáticos del negocio
          </p>
        </div>
      </div>

      {/* Stat cards resumen */}
      <div className="grid grid-cols-3 gap-4">
        <div className="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-4 shadow-sm">
          <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
            <CheckCircle size={18} className="text-green-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Pagos realizados</p>
            <p className="text-lg font-bold text-gray-900">
              {gastosPagados.length}
              <span className="text-sm font-normal text-gray-500 ml-2">
                ({formatCurrency(totalPagado)})
              </span>
            </p>
          </div>
        </div>
        <div className="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-4 shadow-sm">
          <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
            <Receipt size={18} className="text-red-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Gastos pendientes</p>
            <p className="text-lg font-bold text-gray-900">
              {pendientesGastos}
              <span className="text-sm font-normal text-gray-500 ml-2">
                ({formatCurrency(totalPendienteGastos)})
              </span>
            </p>
          </div>
        </div>
        <div className="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-4 shadow-sm">
          <div className="w-10 h-10 bg-indigo-100 rounded-lg flex items-center justify-center">
            <RotateCcw size={18} className="text-indigo-600" />
          </div>
          <div>
            <p className="text-xs text-gray-500">Gastos recurrentes</p>
            <p className="text-lg font-bold text-gray-900">
              {gastosRecurrentes}
              {costoMensualEstimado > 0 && (
                <span className="text-sm font-normal text-gray-500 ml-2">
                  (~{formatCurrency(costoMensualEstimado)}/mes)
                </span>
              )}
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
                {tab.id === 'historial' && gastosPagados.length > 0 && (
                  <span className="bg-green-100 text-green-700 text-xs font-semibold px-1.5 py-0.5 rounded-full">
                    {gastosPagados.length}
                  </span>
                )}
                {tab.id === 'programados' && pendientesGastos > 0 && (
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
      {activeTab === 'historial' && (
        <HistorialPagosTab
          gastos={gastosPagados}
          isLoading={isLoadingGastos}
          onSubirComprobante={subirComprobante}
          onEliminarComprobante={eliminarComprobante}
          isSubiendoComprobante={isSubiendoComprobante}
        />
      )}

      {activeTab === 'programados' && (
        <GastosExternosTab
          gastos={gastosProgramados}
          negocioId={negocioId}
          isLoading={isLoadingGastos}
          onCrear={crearGasto}
          onActualizar={actualizarGasto}
          onEliminar={eliminarGasto}
          onPagar={pagarGasto}
          onSubirComprobante={subirComprobante}
          isCreating={isCreating}
          isUpdating={isUpdating}
          isDeleting={isDeleting}
          isPagando={isPagandoGasto}
          isSubiendoComprobante={isSubiendoComprobante}
          categorias={categoriasGasto}
          onCrearCategoria={crearCategoria}
          isCreatingCategoria={isCreatingCategoria}
        />
      )}
    </div>
  );
};
