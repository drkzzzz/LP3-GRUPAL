import { useState, useEffect } from 'react';
import { Plus, Edit, Eye, ToggleLeft, ToggleRight } from 'lucide-react';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Badge from '../components/ui/Badge';
import Drawer from '../components/ui/Drawer';
import Modal from '../components/ui/Modal';
import planesService from '../services/planesService';
import PlanForm from '../components/forms/PlanForm';
import PlanDetail from '../components/modals/PlanDetail';

/**
 * Página de Gestión de Planes de Suscripción
 * Vista comparativa y gestión de planes
 */
const Planes = () => {
  const [planes, setPlanes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showCreateDrawer, setShowCreateDrawer] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    loadPlanes();
  }, []);

  const loadPlanes = async () => {
    try {
      setIsLoading(true);
      const data = await planesService.getAll(true); // incluir inactivos
      setPlanes(data);
    } catch (error) {
      console.error('Error cargando planes:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleToggleStatus = async (planId) => {
    try {
      await planesService.toggleStatus(planId);
      loadPlanes();
    } catch (error) {
      console.error('Error cambiando estado:', error);
    }
  };

  const handleEdit = (plan) => {
    setSelectedPlan(plan);
    setIsEditing(true);
    setShowCreateDrawer(true);
  };

  const handleViewDetail = async (plan) => {
    setSelectedPlan(plan);
    setShowDetailModal(true);
  };

  const handleCloseDrawer = () => {
    setShowCreateDrawer(false);
    setSelectedPlan(null);
    setIsEditing(false);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Cargando planes...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Planes de Suscripción</h1>
          <p className="text-gray-600 mt-1">Gestiona los planes comerciales de la plataforma</p>
        </div>
        <Button variant="primary" onClick={() => setShowCreateDrawer(true)}>
          <Plus className="w-5 h-5 mr-2" />
          Nuevo Plan
        </Button>
      </div>

      {/* Vista Comparativa de Planes */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {planes.map((plan) => (
          <Card key={plan.id} className="relative">
            <div className="space-y-4">
              {/* Header del plan */}
              <div className="flex items-center justify-between">
                <h3 className="text-xl font-bold text-gray-900">{plan.nombre}</h3>
                <Badge variant={plan.estaActivo ? 'active' : 'inactive'}>
                  {plan.estaActivo ? 'Activo' : 'Inactivo'}
                </Badge>
              </div>

              {/* Precio */}
              <div>
                <div className="flex items-baseline">
                  <span className="text-4xl font-bold text-gray-900">
                    {plan.moneda} {plan.precio}
                  </span>
                  <span className="text-gray-600 ml-2">/ {plan.periodoFacturacion}</span>
                </div>
                {plan.descripcion && (
                  <p className="text-sm text-gray-600 mt-2">{plan.descripcion}</p>
                )}
              </div>

              {/* Características principales */}
              <div className="space-y-2 py-4 border-y border-gray-200">
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-600">Máx. Sedes:</span>
                  <span className="font-semibold">{plan.maxSedes}</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-600">Máx. Usuarios:</span>
                  <span className="font-semibold">{plan.maxUsuarios}</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-600">Máx. Productos:</span>
                  <span className="font-semibold">{plan.maxProductos}</span>
                </div>
                <div className="flex items-center justify-between text-sm">
                  <span className="text-gray-600">Almacenes/Sede:</span>
                  <span className="font-semibold">{plan.maxAlmacenesPorSede}</span>
                </div>
              </div>

              {/* Funcionalidades */}
              <div className="space-y-2">
                <p className="text-sm font-medium text-gray-700">Funcionalidades:</p>
                <div className="grid grid-cols-2 gap-2 text-xs">
                  {plan.permitePos && (
                    <div className="flex items-center text-green-600">
                      <span className="mr-1">✓</span> POS
                    </div>
                  )}
                  {plan.permiteTiendaOnline && (
                    <div className="flex items-center text-green-600">
                      <span className="mr-1">✓</span> E-commerce
                    </div>
                  )}
                  {plan.permiteDelivery && (
                    <div className="flex items-center text-green-600">
                      <span className="mr-1">✓</span> Delivery
                    </div>
                  )}
                  {plan.permiteFacturacionElectronica && (
                    <div className="flex items-center text-green-600">
                      <span className="mr-1">✓</span> Fact. Electrónica
                    </div>
                  )}
                  {plan.permiteMultiAlmacen && (
                    <div className="flex items-center text-green-600">
                      <span className="mr-1">✓</span> Multi-almacén
                    </div>
                  )}
                  {plan.permiteReportesAvanzados && (
                    <div className="flex items-center text-green-600">
                      <span className="mr-1">✓</span> Reportes Avanzados
                    </div>
                  )}
                </div>
              </div>

              {/* Negocios suscritos */}
              <div className="pt-4 border-t border-gray-200">
                <p className="text-sm text-gray-600">
                  <span className="font-semibold text-gray-900">{plan.negociosSuscritos || 0}</span> negocios suscritos
                </p>
              </div>

              {/* Acciones */}
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleViewDetail(plan)}
                  className="flex-1"
                >
                  <Eye className="w-4 h-4 mr-1" />
                  Ver
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleEdit(plan)}
                  className="flex-1"
                >
                  <Edit className="w-4 h-4 mr-1" />
                  Editar
                </Button>
                <Button
                  variant={plan.estaActivo ? 'danger' : 'success'}
                  size="sm"
                  onClick={() => handleToggleStatus(plan.id)}
                  className="flex-1"
                >
                  {plan.estaActivo ? (
                    <><ToggleRight className="w-4 h-4 mr-1" /> Desactivar</>
                  ) : (
                    <><ToggleLeft className="w-4 h-4 mr-1" /> Activar</>
                  )}
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* Drawer para crear/editar plan */}
      <Drawer
        isOpen={showCreateDrawer}
        onClose={handleCloseDrawer}
        title={isEditing ? 'Editar Plan' : 'Crear Nuevo Plan'}
        size="lg"
      >
        <PlanForm
          plan={selectedPlan}
          onSuccess={() => {
            handleCloseDrawer();
            loadPlanes();
          }}
          onCancel={handleCloseDrawer}
        />
      </Drawer>

      {/* Modal para ver detalle */}
      <Modal
        isOpen={showDetailModal}
        onClose={() => setShowDetailModal(false)}
        title="Detalle del Plan"
        size="lg"
      >
        {selectedPlan && (
          <PlanDetail
            plan={selectedPlan}
            onClose={() => setShowDetailModal(false)}
          />
        )}
      </Modal>
    </div>
  );
};

export default Planes;
