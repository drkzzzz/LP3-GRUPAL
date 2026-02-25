import { useState, useEffect } from 'react';
import { FileText, Eye, CheckCircle, XCircle, Send, Download } from 'lucide-react';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import Modal from '../components/ui/Modal';
import Select from '../components/ui/Select';
import facturacionService from '../services/facturacionService';
import FacturaDetail from '../components/modals/FacturaDetail';

/**
 * Página de Facturación de Suscripciones
 * Control de facturas recurrentes y morosidad
 */
const Facturacion = () => {
  const [facturas, setFacturas] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [estadoFilter, setEstadoFilter] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [selectedFactura, setSelectedFactura] = useState(null);

  useEffect(() => {
    loadFacturas();
  }, [currentPage, searchTerm, estadoFilter]);

  const loadFacturas = async () => {
    try {
      setIsLoading(true);
      const response = await facturacionService.getAll({
        page: currentPage,
        search: searchTerm,
        estado: estadoFilter,
        limit: 10
      });
      setFacturas(response.data || response);
      setTotalPages(response.totalPages || 1);
    } catch (error) {
      console.error('Error cargando facturas:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = (value) => {
    setSearchTerm(value);
    setCurrentPage(1);
  };

  const handleViewDetail = async (factura) => {
    try {
      const detail = await facturacionService.getById(factura.id);
      setSelectedFactura(detail);
      setShowDetailModal(true);
    } catch (error) {
      console.error('Error cargando detalle:', error);
    }
  };

  const handleMarkAsPaid = async (facturaId) => {
    if (confirm('¿Marcar esta factura como pagada?')) {
      try {
        await facturacionService.markAsPaid(facturaId, {
          metodoPago: 'transferencia',
          referenciaPago: 'Pago manual desde SuperAdmin'
        });
        loadFacturas();
      } catch (error) {
        console.error('Error marcando como pagada:', error);
        alert('Error al marcar la factura como pagada');
      }
    }
  };

  const handleCancel = async (facturaId) => {
    const motivo = prompt('Ingrese el motivo de anulación:');
    if (motivo) {
      try {
        await facturacionService.cancel(facturaId, motivo);
        loadFacturas();
      } catch (error) {
        console.error('Error anulando factura:', error);
        alert('Error al anular la factura');
      }
    }
  };

  const handleSendReminder = async (facturaId) => {
    try {
      await facturacionService.sendReminder(facturaId);
      alert('Recordatorio enviado exitosamente');
    } catch (error) {
      console.error('Error enviando recordatorio:', error);
      alert('Error al enviar recordatorio');
    }
  };

  const getEstadoBadge = (estado) => {
    const estados = {
      'pendiente': 'pending',
      'pagada': 'paid',
      'vencida': 'overdue',
      'anulada': 'cancelled',
    };
    return estados[estado?.toLowerCase()] || 'default';
  };

  const columns = [
    {
      header: 'N° Factura',
      accessor: 'numeroFactura',
      render: (row) => (
        <div className="flex items-center">
          <FileText className="w-5 h-5 text-gray-400 mr-2" />
          <span className="font-medium text-gray-900">{row.numeroFactura}</span>
        </div>
      )
    },
    {
      header: 'Negocio',
      accessor: 'negocioNombre',
      render: (row) => (
        <span className="text-sm text-gray-900">{row.negocioNombre}</span>
      )
    },
    {
      header: 'Plan',
      accessor: 'planNombre',
    },
    {
      header: 'Monto',
      accessor: 'monto',
      render: (row) => (
        <span className="font-semibold text-gray-900">
          {row.moneda} {row.monto?.toFixed(2)}
        </span>
      )
    },
    {
      header: 'Fecha Emisión',
      accessor: 'fechaEmision',
      render: (row) => (
        <span className="text-sm text-gray-600">
          {new Date(row.fechaEmision).toLocaleDateString('es-PE')}
        </span>
      )
    },
    {
      header: 'Vencimiento',
      accessor: 'fechaVencimiento',
      render: (row) => (
        <span className="text-sm text-gray-600">
          {new Date(row.fechaVencimiento).toLocaleDateString('es-PE')}
        </span>
      )
    },
    {
      header: 'Estado',
      accessor: 'estado',
      render: (row) => (
        <Badge variant={getEstadoBadge(row.estado)}>
          {row.estado}
        </Badge>
      )
    },
    {
      header: 'Acciones',
      render: (row) => (
        <div className="flex items-center gap-2">
          <button
            onClick={() => handleViewDetail(row)}
            className="text-blue-600 hover:text-blue-800 transition-colors"
            title="Ver detalle"
          >
            <Eye className="w-5 h-5" />
          </button>
          
          {row.estado === 'pendiente' && (
            <>
              <button
                onClick={() => handleMarkAsPaid(row.id)}
                className="text-green-600 hover:text-green-800 transition-colors"
                title="Marcar como pagada"
              >
                <CheckCircle className="w-5 h-5" />
              </button>
              <button
                onClick={() => handleSendReminder(row.id)}
                className="text-yellow-600 hover:text-yellow-800 transition-colors"
                title="Enviar recordatorio"
              >
                <Send className="w-5 h-5" />
              </button>
            </>
          )}
          
          {(row.estado === 'pendiente' || row.estado === 'vencida') && (
            <button
              onClick={() => handleCancel(row.id)}
              className="text-red-600 hover:text-red-800 transition-colors"
              title="Anular factura"
            >
              <XCircle className="w-5 h-5" />
            </button>
          )}
        </div>
      )
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Facturación de Suscripciones</h1>
          <p className="text-gray-600 mt-1">Control de facturación recurrente y morosidad</p>
        </div>
        <Button
          variant="outline"
          onClick={() => window.location.href = '/superadmin/facturacion/reporte'}
        >
          <Download className="w-5 h-5 mr-2" />
          Exportar Reporte
        </Button>
      </div>

      {/* Filtros */}
      <div className="flex gap-4">
        <Select
          value={estadoFilter}
          onChange={(e) => {
            setEstadoFilter(e.target.value);
            setCurrentPage(1);
          }}
          options={[
            { value: '', label: 'Todos los estados' },
            { value: 'pendiente', label: 'Pendientes' },
            { value: 'pagada', label: 'Pagadas' },
            { value: 'vencida', label: 'Vencidas' },
            { value: 'anulada', label: 'Anuladas' },
          ]}
          className="w-64"
        />
      </div>

      {/* Tabla de facturas */}
      <Table
        columns={columns}
        data={facturas}
        searchPlaceholder="Buscar por número de factura o negocio..."
        onSearch={handleSearch}
        currentPage={currentPage}
      totalPages={totalPages}
        onPageChange={setCurrentPage}
        isLoading={isLoading}
      />

      {/* Modal para ver detalle */}
      <Modal
        isOpen={showDetailModal}
        onClose={() => setShowDetailModal(false)}
        title="Detalle de Factura"
        size="lg"
      >
        {selectedFactura && (
          <FacturaDetail
            factura={selectedFactura}
            onClose={() => setShowDetailModal(false)}
            onUpdate={loadFacturas}
          />
        )}
      </Modal>
    </div>
  );
};

export default Facturacion;
