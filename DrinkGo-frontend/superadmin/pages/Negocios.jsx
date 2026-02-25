import { useState, useEffect } from 'react';
import { Plus, Eye, Edit, Ban, CheckCircle, Building } from 'lucide-react';
import Table from '../components/ui/Table';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import Drawer from '../components/ui/Drawer';
import Modal from '../components/ui/Modal';
import negociosService from '../services/negociosService';
import NegocioForm from '../components/forms/NegocioForm';
import NegocioDetail from '../components/modals/NegocioDetail';

/**
 * Página de Gestión de Negocios (Licorerías/Tenants)
 * Lista, crea, edita y gestiona negocios
 */
const Negocios = () => {
  const [negocios, setNegocios] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  
  // Estados para modales/drawers
  const [showCreateDrawer, setShowCreateDrawer] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [selectedNegocio, setSelectedNegocio] = useState(null);

  useEffect(() => {
    loadNegocios();
  }, [currentPage, searchTerm]);

  const loadNegocios = async () => {
    try {
      setIsLoading(true);
      const response = await negociosService.getAll({
        page: currentPage,
        search: searchTerm,
        limit: 10
      });
      setNegocios(response.data || response);
      setTotalPages(response.totalPages || 1);
    } catch (error) {
      console.error('Error cargando negocios:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = (value) => {
    setSearchTerm(value);
    setCurrentPage(1);
  };

  const handleViewDetail = async (negocio) => {
    try {
      const detail = await negociosService.getById(negocio.id);
      setSelectedNegocio(detail);
      setShowDetailModal(true);
    } catch (error) {
      console.error('Error cargando detalle:', error);
    }
  };

  const handleSuspend = async (negocioId) => {
    if (confirm('¿Está seguro de suspender este negocio?')) {
      try {
        await negociosService.suspend(negocioId, 'Suspendido desde SuperAdmin');
        loadNegocios();
      } catch (error) {
        console.error('Error suspendiendo negocio:', error);
      }
    }
  };

  const handleReactivate = async (negocioId) => {
    try {
      await negociosService.reactivate(negocioId);
      loadNegocios();
    } catch (error) {
      console.error('Error reactivando negocio:', error);
    }
  };

  const getEstadoBadge = (estado) => {
    const estados = {
      'activo': 'active',
      'suspendido': 'suspended',
      'moroso': 'overdue',
      'cancelado': 'cancelled',
    };
    return estados[estado?.toLowerCase()] || 'default';
  };

  const columns = [
    {
      header: 'Razón Social',
      accessor: 'razonSocial',
      render: (row) => (
        <div className="flex items-center">
          <Building className="w-5 h-5 text-gray-400 mr-2" />
          <span className="font-medium text-gray-900">{row.razonSocial}</span>
        </div>
      )
    },
    {
      header: 'RUC',
      accessor: 'ruc',
    },
    {
      header: 'Email',
      accessor: 'email',
    },
    {
      header: 'Plan',
      accessor: 'planNombre',
      render: (row) => (
        <span className="text-sm text-gray-600">{row.planNombre || 'Sin plan'}</span>
      )
    },
    {
      header: 'Sedes',
      accessor: 'cantidadSedes',
      render: (row) => (
        <span className="text-sm text-gray-600">{row.cantidadSedes || 0} sedes</span>
      )
    },
    {
      header: 'Estado',
      accessor: 'estado',
      render: (row) => (
        <Badge variant={getEstadoBadge(row.estado)}>
          {row.estado || 'Sin estado'}
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
          
          {row.estado?.toLowerCase() === 'activo' ? (
            <button
              onClick={() => handleSuspend(row.id)}
              className="text-red-600 hover:text-red-800 transition-colors"
              title="Suspender"
            >
              <Ban className="w-5 h-5" />
            </button>
          ) : (
            <button
              onClick={() => handleReactivate(row.id)}
              className="text-green-600 hover:text-green-800 transition-colors"
              title="Reactivar"
            >
              <CheckCircle className="w-5 h-5" />
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
          <h1 className="text-2xl font-bold text-gray-900">Gestión de Negocios</h1>
          <p className="text-gray-600 mt-1">Administra todas las licorerías registradas en la plataforma</p>
        </div>
        <Button
          variant="primary"
          onClick={() => setShowCreateDrawer(true)}
        >
          <Plus className="w-5 h-5 mr-2" />
          Nuevo Negocio
        </Button>
      </div>

      {/* Tabla de negocios */}
      <Table
        columns={columns}
        data={negocios}
        searchPlaceholder="Buscar por razón social, RUC o email..."
        onSearch={handleSearch}
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        isLoading={isLoading}
      />

      {/* Drawer para crear negocio */}
      <Drawer
        isOpen={showCreateDrawer}
        onClose={() => setShowCreateDrawer(false)}
        title="Registrar Nuevo Negocio"
        size="lg"
      >
        <NegocioForm
          onSuccess={() => {
            setShowCreateDrawer(false);
            loadNegocios();
          }}
          onCancel={() => setShowCreateDrawer(false)}
        />
      </Drawer>

      {/* Modal para ver detalle */}
      <Modal
        isOpen={showDetailModal}
        onClose={() => setShowDetailModal(false)}
        title="Detalle del Negocio"
        size="xl"
      >
        {selectedNegocio && (
          <NegocioDetail
            negocio={selectedNegocio}
            onClose={() => setShowDetailModal(false)}
          />
        )}
      </Modal>
    </div>
  );
};

export default Negocios;
