import { useState, useMemo } from 'react';
import {
  Search,
  Eye,
  FileText,
  CheckCircle,
  XCircle,
  RefreshCw,
  Plus,
} from 'lucide-react';
import { useFacturacion } from '../hooks/useFacturacion';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Card } from '../components/ui/Card';
import { Table } from '../components/ui/Table';
import { Badge } from '../components/ui/Badge';
import { Modal } from '../components/ui/Modal';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { FacturaDetail } from '../components/modals/FacturaDetail';
import { formatCurrency, formatDate } from '@/shared/utils/formatters';

const ESTADO_COLORS = {
  pendiente: 'warning',
  pagada: 'success',
  vencida: 'error',
  cancelada: 'error',
  anulada: 'error',
};

export const Facturacion = () => {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');
  const [estadoFilter, setEstadoFilter] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 400);

  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isPagarOpen, setIsPagarOpen] = useState(false);
  const [isCancelarOpen, setIsCancelarOpen] = useState(false);
  const [isReintentarOpen, setIsReintentarOpen] = useState(false);
  const [isGenerarOpen, setIsGenerarOpen] = useState(false);
  const [selected, setSelected] = useState(null);
  const [selectedSuscripcionId, setSelectedSuscripcionId] = useState('');
  const [metodoPagoInput, setMetodoPagoInput] = useState('');
  const [referenciaInput, setReferenciaInput] = useState('');

  const {
    facturas,
    isLoading,
    marcarPagada,
    cancelarFactura,
    reintentarFactura,
    generarFactura,
    isMarkingPaid,
    isCanceling,
    isRetrying,
    isGenerating,
    suscripciones,
  } = useFacturacion();

  /* ---------- client-side filtering ---------- */
  const filtered = useMemo(() => {
    let result = facturas;
    if (debouncedSearch) {
      const q = debouncedSearch.toLowerCase();
      result = result.filter(
        (f) =>
          f.numeroFactura?.toLowerCase().includes(q) ||
          f.suscripcion?.negocio?.razonSocial?.toLowerCase().includes(q),
      );
    }
    if (estadoFilter) {
      result = result.filter((f) => f.estado === estadoFilter);
    }
    return result;
  }, [facturas, debouncedSearch, estadoFilter]);

  /* ---------- client-side pagination ---------- */
  const paginatedData = useMemo(() => {
    const start = (page - 1) * pageSize;
    return filtered.slice(start, start + pageSize);
  }, [filtered, page, pageSize]);

  /* ---------- handlers ---------- */
  const handleView = (factura) => {
    setSelected(factura);
    setIsDetailOpen(true);
  };

  const handlePagarClick = (factura) => {
    setSelected(factura);
    setIsPagarOpen(true);
  };

  const handlePagarConfirm = async () => {
    await marcarPagada({ id: selected.id, metodoPago: metodoPagoInput, referenciaPago: referenciaInput });
    setIsPagarOpen(false);
    setSelected(null);
    setMetodoPagoInput('');
    setReferenciaInput('');
  };

  const handleCancelarClick = (factura) => {
    setSelected(factura);
    setIsCancelarOpen(true);
  };

  const handleCancelarConfirm = async () => {
    await cancelarFactura({ id: selected.id });
    setIsCancelarOpen(false);
    setSelected(null);
  };

  const handleReintentarClick = (factura) => {
    setSelected(factura);
    setIsReintentarOpen(true);
  };

  const handleReintentarConfirm = async () => {
    await reintentarFactura({ id: selected.id });
    setIsReintentarOpen(false);
    setSelected(null);
  };

  const handleGenerarConfirm = async () => {
    if (!selectedSuscripcionId) return;
    await generarFactura(selectedSuscripcionId);
    setIsGenerarOpen(false);
    setSelectedSuscripcionId('');
  };

  /* ---------- summary stats ---------- */
  const stats = useMemo(() => {
    const total = facturas.length;
    const pendientes = facturas.filter((f) => f.estado === 'pendiente').length;
    const pagadas = facturas.filter((f) => f.estado === 'pagada').length;
    const ingresoTotal = facturas
      .filter((f) => f.estado === 'pagada')
      .reduce((acc, f) => acc + (f.total || 0), 0);
    return { total, pendientes, pagadas, ingresoTotal };
  }, [facturas]);

  /* ---------- columns ---------- */
  const columns = [
    {
      key: 'index',
      title: '#',
      width: '60px',
      render: (_, __, i) => (page - 1) * pageSize + i + 1,
    },
    {
      key: 'numeroFactura',
      title: 'N° Factura',
      dataIndex: 'numeroFactura',
      width: '140px',
    },
    {
      key: 'negocio',
      title: 'Negocio',
      render: (_, row) => row.suscripcion?.negocio?.razonSocial || '—',
    },
    {
      key: 'subtotal',
      title: 'Subtotal',
      width: '120px',
      render: (_, row) => formatCurrency(row.subtotal),
    },
    {
      key: 'impuesto',
      title: 'Impuesto',
      width: '110px',
      render: (_, row) => formatCurrency(row.impuesto),
    },
    {
      key: 'total',
      title: 'Total',
      width: '120px',
      render: (_, row) => (
        <span className="font-semibold">{formatCurrency(row.total)}</span>
      ),
    },
    {
      key: 'emitidoEn',
      title: 'Emitido',
      width: '120px',
      render: (_, row) => (row.emitidoEn ? formatDate(row.emitidoEn) : '—'),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '120px',
      render: (_, row) => (
        <Badge variant={ESTADO_COLORS[row.estado] || 'info'}>
          {row.estado?.toUpperCase()}
        </Badge>
      ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '130px',
      align: 'center',
      render: (_, row) => {
        const isPendiente = row.estado === 'pendiente';
        const isVencida = row.estado === 'vencida';
        const isPagada = row.estado === 'pagada';
        const isCancelable = isPendiente || isVencida;
        return (
          <div className="flex justify-center gap-1">
            <button
              title="Ver detalles"
              onClick={() => handleView(row)}
              className="p-1.5 rounded hover:bg-gray-100 text-blue-500 hover:text-blue-700"
            >
              <Eye size={16} />
            </button>
            {isPendiente && (
              <button
                title="Marcar como pagada (RF-FAC-001)"
                onClick={() => handlePagarClick(row)}
                className="p-1.5 rounded hover:bg-green-50 text-green-500 hover:text-green-700"
              >
                <CheckCircle size={16} />
              </button>
            )}
            {isVencida && (
              <button
                title="Reintentar cobro (RF-FAC-001)"
                onClick={() => handleReintentarClick(row)}
                className="p-1.5 rounded hover:bg-blue-50 text-blue-400 hover:text-blue-600"
              >
                <RefreshCw size={16} />
              </button>
            )}
            {isCancelable && (
              <button
                title="Cancelar factura (RF-FAC-002)"
                onClick={() => handleCancelarClick(row)}
                className="p-1.5 rounded hover:bg-red-50 text-red-400 hover:text-red-600"
              >
                <XCircle size={16} />
              </button>
            )}
          </div>
        );
      },
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Facturación</h1>
          <p className="text-gray-600 mt-1">
            Control de facturas emitidas por suscripciones
          </p>
        </div>
        <button
          onClick={() => setIsGenerarOpen(true)}
          className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        >
          <Plus size={16} />
          Nueva Factura
        </button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <p className="text-sm text-gray-500">Total Facturas</p>
          <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <p className="text-sm text-gray-500">Pagadas</p>
          <p className="text-2xl font-bold text-green-600">{stats.pagadas}</p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <p className="text-sm text-gray-500">Pendientes</p>
          <p className="text-2xl font-bold text-yellow-600">{stats.pendientes}</p>
        </div>
        <div className="bg-white border border-gray-200 rounded-lg p-4">
          <p className="text-sm text-gray-500">Ingresos Totales</p>
          <p className="text-2xl font-bold text-blue-600">
            {formatCurrency(stats.ingresoTotal)}
          </p>
        </div>
      </div>

      {/* Card */}
      <Card>
        {/* Search + Filter */}
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mb-4">
          <div className="flex gap-3 items-center w-full sm:w-auto">
            <div className="relative flex-1 sm:w-80">
              <Search
                size={16}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <input
                type="text"
                placeholder="Buscar por N° factura o negocio..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setPage(1);
                }}
                className="w-full border border-gray-300 rounded-lg pl-9 pr-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <select
              value={estadoFilter}
              onChange={(e) => {
                setEstadoFilter(e.target.value);
                setPage(1);
              }}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todos los estados</option>
              <option value="pendiente">Pendiente</option>
              <option value="pagada">Pagada</option>
              <option value="vencida">Vencida</option>
              <option value="cancelada">Cancelada</option>
              <option value="anulada">Anulada</option>
            </select>
          </div>
          <div className="flex items-center gap-2 text-sm text-gray-500">
            <FileText size={16} />
            <span>{filtered.length} facturas</span>
          </div>
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

      {/* Detail Modal */}
      <Modal
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelected(null);
        }}
        title="Detalles de la Factura"
        size="lg"
      >
        {selected && <FacturaDetail factura={selected} />}
      </Modal>

      {/* Marcar como Pagada — RF-FAC-001 */}
      <Modal
        isOpen={isPagarOpen}
        onClose={() => { setIsPagarOpen(false); setSelected(null); }}
        title="Marcar como Pagada"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            Confirme el pago de la factura <strong>{selected?.numeroFactura}</strong>.
          </p>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Método de pago</label>
            <input
              type="text"
              value={metodoPagoInput}
              onChange={(e) => setMetodoPagoInput(e.target.value)}
              placeholder="Ej: Transferencia, Yape, Efectivo..."
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Referencia / Número de operación</label>
            <input
              type="text"
              value={referenciaInput}
              onChange={(e) => setReferenciaInput(e.target.value)}
              placeholder="Número de operación o voucher"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button
              onClick={() => { setIsPagarOpen(false); setSelected(null); }}
              className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              onClick={handlePagarConfirm}
              disabled={isMarkingPaid}
              className="px-4 py-2 text-sm bg-green-600 hover:bg-green-700 text-white rounded-lg disabled:opacity-50"
            >
              {isMarkingPaid ? 'Procesando...' : 'Confirmar Pago'}
            </button>
          </div>
        </div>
      </Modal>

      {/* Cancelar Factura — RF-FAC-002 */}
      <ConfirmDialog
        isOpen={isCancelarOpen}
        onClose={() => { setIsCancelarOpen(false); setSelected(null); }}
        onConfirm={handleCancelarConfirm}
        title="Cancelar Factura"
        message={`¿Cancelar la factura "${selected?.numeroFactura}"? Esta acción marcará la factura como cancelada y no generará cobro.`}
        confirmText="Cancelar Factura"
        isLoading={isCanceling}
      />

      {/* Reintentar Cobro — RF-FAC-001 */}
      <ConfirmDialog
        isOpen={isReintentarOpen}
        onClose={() => { setIsReintentarOpen(false); setSelected(null); }}
        onConfirm={handleReintentarConfirm}
        title="Reintentar Cobro"
        message={`¿Reintentar el cobro de la factura "${selected?.numeroFactura}"? La factura volverá al estado pendiente.`}
        confirmText="Reintentar"
        isLoading={isRetrying}
      />

      {/* Nueva Factura — generar cargo mensual */}
      <Modal
        isOpen={isGenerarOpen}
        onClose={() => { setIsGenerarOpen(false); setSelectedSuscripcionId(''); }}
        title="Nueva Factura de Suscripción"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-sm text-gray-600">
            Selecciona la suscripción a la que deseas emitir el cargo mensual. El sistema auto-calculará el monto según el plan.
          </p>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Suscripción / Negocio <span className="text-red-500">*</span>
            </label>
            <select
              value={selectedSuscripcionId}
              onChange={(e) => setSelectedSuscripcionId(e.target.value)}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">-- Seleccionar --</option>
              {suscripciones
                .filter((s) => s.estado === 'activa')
                .map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.negocio?.razonSocial || s.negocio?.nombreComercial || `Negocio #${s.negocio?.id}`}
                    {s.plan ? ` — ${s.plan.nombre} (S/ ${s.plan.precio})` : ''}
                  </option>
                ))}
            </select>
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <button
              onClick={() => { setIsGenerarOpen(false); setSelectedSuscripcionId(''); }}
              className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              onClick={handleGenerarConfirm}
              disabled={!selectedSuscripcionId || isGenerating}
              className="px-4 py-2 text-sm bg-blue-600 hover:bg-blue-700 text-white rounded-lg disabled:opacity-50"
            >
              {isGenerating ? 'Generando...' : 'Generar Factura'}
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
