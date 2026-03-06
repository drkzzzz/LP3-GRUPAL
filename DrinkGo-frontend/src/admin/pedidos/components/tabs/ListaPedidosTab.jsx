/**
 * Tab principal de gestión de pedidos
 * Muestra tabla con todos los pedidos, filtros y acciones
 */
import { useMemo, useState } from 'react';
import { usePedidos, useUpdatePedido } from '../../hooks/usePedidos';
import { useDetallePedidos } from '../../hooks/useDetallePedidos';
import { useCreateSeguimientoPedido } from '../../hooks/useSeguimientoPedidos';
import { message } from '@/shared/utils/notifications';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Table } from '@/admin/components/ui/Table';
import { Card } from '@/admin/components/ui/Card';
import { StatCard } from '@/admin/components/ui/StatCard';
import { ConfirmDialog } from '@/admin/components/ui/ConfirmDialog';
import { DetallePedidoModal } from '../modals/DetallePedidoModal';
import { Eye, Truck, CheckCircle, XCircle, Clock, Package, Search, Loader2, PackageX, ChefHat } from 'lucide-react';
import {
  ESTADOS_PEDIDO,
  TIPOS_PEDIDO,
  getConfigEstado,
  getEstadosSiguientes,
  formatearFechaPedido,
  calcularUrgencia,
  getClaseUrgencia,
} from '../../utils/estadosPedido';

export function ListaPedidosTab() {
  // Estados locales
  const [filtroEstado, setFiltroEstado] = useState('todos');
  const [filtroTipo, setFiltroTipo] = useState('todos');
  const [busqueda, setBusqueda] = useState('');
  const [selectedPedido, setSelectedPedido] = useState(null);
  const [showConfirmCambioEstado, setShowConfirmCambioEstado] = useState(false);
  const [nuevoEstado, setNuevoEstado] = useState(null);
  const [showDetalleModal, setShowDetalleModal] = useState(false);

  // Debounce search
  const debouncedSearch = useDebounce(busqueda, 400);

  // React Query hooks
  const { data: pedidos = [], isLoading, error } = usePedidos();
  const { data: detallesPedidos = [] } = useDetallePedidos();
  const { mutateAsync: updatePedido } = useUpdatePedido();
  const { mutateAsync: createSeguimiento } = useCreateSeguimientoPedido();

  // Agrupar detalles por pedido
  const detallesPorPedido = useMemo(() => {
    const map = new Map();
    for (const detalle of detallesPedidos) {
      const pedidoId = detalle.pedido?.id;
      if (!map.has(pedidoId)) {
        map.set(pedidoId, []);
      }
      map.get(pedidoId).push(detalle);
    }
    return map;
  }, [detallesPedidos]);

  // Filtrar pedidos
  const pedidosFiltrados = useMemo(() => {
    let resultado = [...pedidos];

    // Filtro por estado
    if (filtroEstado !== 'todos') {
      resultado = resultado.filter((p) => p.estado === filtroEstado);
    }

    // Filtro por tipo
    if (filtroTipo !== 'todos') {
      resultado = resultado.filter((p) => p.tipoPedido === filtroTipo);
    }

    // Búsqueda con debounce
    if (debouncedSearch.trim()) {
      const termino = debouncedSearch.toLowerCase();
      resultado = resultado.filter(
        (p) =>
          p.numeroPedido?.toLowerCase().includes(termino) ||
          p.cliente?.nombre?.toLowerCase().includes(termino) ||
          p.cliente?.apellido?.toLowerCase().includes(termino)
      );
    }

    // Ordenar por fecha más reciente primero
    resultado.sort((a, b) => new Date(b.creado_en) - new Date(a.creado_en));

    return resultado;
  }, [pedidos, filtroEstado, filtroTipo, debouncedSearch]);

  // Estadísticas
  const estadisticas = useMemo(() => {
    const stats = {
      total: pedidos.length,
      pendientes: 0,
      confirmados: 0,
      enPreparacion: 0,
      listos: 0,
      enCamino: 0,
      entregados: 0,
      cancelados: 0,
    };

    for (const pedido of pedidos) {
      switch (pedido.estado) {
        case ESTADOS_PEDIDO.PENDIENTE:
          stats.pendientes++;
          break;
        case ESTADOS_PEDIDO.CONFIRMADO:
          stats.confirmados++;
          break;
        case ESTADOS_PEDIDO.PREPARANDO:
          stats.enPreparacion++;
          break;
        case ESTADOS_PEDIDO.LISTO:
          stats.listos++;
          break;
        case ESTADOS_PEDIDO.EN_CAMINO:
          stats.enCamino++;
          break;
        case ESTADOS_PEDIDO.ENTREGADO:
          stats.entregados++;
          break;
        case ESTADOS_PEDIDO.CANCELADO:
          stats.cancelados++;
          break;
      }
    }

    return stats;
  }, [pedidos]);

  // Handler para cambiar estado
  const handleCambiarEstado = async (pedido, estado) => {
    setSelectedPedido(pedido);
    setNuevoEstado(estado);
    setShowConfirmCambioEstado(true);
  };

  // Confirmar cambio de estado
  const confirmarCambioEstado = async () => {
    if (!selectedPedido || !nuevoEstado) return;

    try {
      // 1. Actualizar estado del pedido
      const pedidoActualizado = {
        ...selectedPedido,
        estado: nuevoEstado,
      };

      await updatePedido(pedidoActualizado);

      // 2. Crear registro de seguimiento
      await createSeguimiento({
        pedido: { id: selectedPedido.id },
        estado: nuevoEstado,
        descripcion: `Estado cambiado de ${getConfigEstado(selectedPedido.estado).label} a ${getConfigEstado(nuevoEstado).label}`,
        visible_para_cliente: 1,
      });

      message.success('Estado actualizado correctamente');
      setShowConfirmCambioEstado(false);
      setSelectedPedido(null);
      setNuevoEstado(null);
    } catch (error) {
      console.error('Error al cambiar estado:', error);
      message.error('Error al cambiar el estado del pedido');
    }
  };

  // Handler para ver detalle
  const handleVerDetalle = (pedido) => {
    setSelectedPedido(pedido);
    setShowDetalleModal(true);
  };

  // Handler para avanzar estado desde el modal
  const handleAvanzarEstadoModal = (pedido) => {
    const estadosSiguientes = getEstadosSiguientes(pedido.estado, pedido.tipoPedido);
    if (estadosSiguientes.length > 0) {
      // Tomar el primer estado siguiente permitido (flujo lineal)
      const siguienteEstado = estadosSiguientes.find(
        (e) => e !== ESTADOS_PEDIDO.CANCELADO
      );
      if (siguienteEstado) {
        handleCambiarEstado(pedido, siguienteEstado);
        setShowDetalleModal(false);
      }
    }
  };

  // Definir columnas de la tabla
  const columns = [
    {
      key: 'numeroPedido',
      title: 'Número',
      width: '120px',
      render: (_, row) => (
        <div className="font-semibold text-blue-600">
          #{row.numeroPedido || row.id}
        </div>
      ),
    },
    {
      key: 'cliente',
      title: 'Cliente',
      width: '180px',
      render: (_, row) => (
        <div>
          <div className="font-medium">
            {row.cliente?.nombre} {row.cliente?.apellido}
          </div>
          <div className="text-xs text-gray-500">{row.cliente?.telefono}</div>
        </div>
      ),
    },
    {
      key: 'tipo',
      title: 'Tipo',
      width: '120px',
      render: (_, row) => (
        <div className="flex items-center gap-1">
          {row.tipoPedido === TIPOS_PEDIDO.DELIVERY ? (
            <>
              <Truck size={14} className="text-blue-600" />
              <span className="text-sm">Delivery</span>
            </>
          ) : (
            <>
              <Package size={14} className="text-green-600" />
              <span className="text-sm">Recojo</span>
            </>
          )}
        </div>
      ),
    },
    {
      key: 'estado',
      title: 'Estado',
      width: '150px',
      render: (_, row) => {
        const config = getConfigEstado(row.estado);
        return (
          <div className="flex items-center gap-2">
            <span className={`px-2 py-1 rounded-md text-xs font-medium border ${config.color}`}>
              {config.icon} {config.label}
            </span>
          </div>
        );
      },
    },
    {
      key: 'fecha',
      title: 'Fecha',
      width: '140px',
      render: (_, row) => (
        <div>
          <div className="text-sm">{formatearFechaPedido(row.creado_en)}</div>
          <div className="text-xs text-gray-500">
            {new Date(row.creado_en).toLocaleTimeString('es-PE', {
              hour: '2-digit',
              minute: '2-digit',
            })}
          </div>
        </div>
      ),
    },
    {
      key: 'total',
      title: 'Total',
      width: '100px',
      render: (_, row) => (
        <div className="font-semibold text-green-700">
          S/ {Number(row.total || 0).toFixed(2)}
        </div>
      ),
    },
    {
      key: 'actions',
      title: 'Acciones',
      width: '200px',
      render: (_, row) => {
        const estadosSiguientes = getEstadosSiguientes(row.estado, row.tipoPedido);
        const urgencia = calcularUrgencia(row.creado_en, row.estado);

        return (
          <div className="flex gap-1">
            {/* Ver detalle */}
            <button
              onClick={() => handleVerDetalle(row)}
              className="p-1.5 rounded-md hover:bg-gray-100 text-gray-600 hover:text-blue-600 transition-colors"
              title="Ver detalle"
            >
              <Eye size={16} />
            </button>

            {/* Acciones rápidas de estado */}
            {estadosSiguientes.includes(ESTADOS_PEDIDO.CONFIRMADO) && (
              <button
                onClick={() => handleCambiarEstado(row, ESTADOS_PEDIDO.CONFIRMADO)}
                className="p-1.5 rounded-md hover:bg-blue-50 text-blue-600 transition-colors"
                title="Confirmar"
              >
                <CheckCircle size={16} />
              </button>
            )}

            {estadosSiguientes.includes(ESTADOS_PEDIDO.LISTO) && (
              <button
                onClick={() => handleCambiarEstado(row, ESTADOS_PEDIDO.LISTO)}
                className="p-1.5 rounded-md hover:bg-green-50 text-green-600 transition-colors"
                title="Marcar como listo"
              >
                <Package size={16} />
              </button>
            )}

            {estadosSiguientes.includes(ESTADOS_PEDIDO.EN_CAMINO) && (
              <button
                onClick={() => handleCambiarEstado(row, ESTADOS_PEDIDO.EN_CAMINO)}
                className="p-1.5 rounded-md hover:bg-indigo-50 text-indigo-600 transition-colors"
                title="En camino"
              >
                <Truck size={16} />
              </button>
            )}

            {estadosSiguientes.includes(ESTADOS_PEDIDO.ENTREGADO) && (
              <button
                onClick={() => handleCambiarEstado(row, ESTADOS_PEDIDO.ENTREGADO)}
                className="p-1.5 rounded-md hover:bg-green-50 text-green-700 transition-colors"
                title="Marcar como entregado"
              >
                <CheckCircle size={16} />
              </button>
            )}

            {estadosSiguientes.includes(ESTADOS_PEDIDO.CANCELADO) && (
              <button
                onClick={() => handleCambiarEstado(row, ESTADOS_PEDIDO.CANCELADO)}
                className="p-1.5 rounded-md hover:bg-red-50 text-red-600 transition-colors"
                title="Cancelar"
              >
                <XCircle size={16} />
              </button>
            )}

            {urgencia !== 'normal' && (
              <div className="flex items-center">
                <Clock
                  size={16}
                  className={urgencia === 'urgente' ? 'text-red-600' : 'text-orange-600'}
                />
              </div>
            )}
          </div>
        );
      },
    },
  ];

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 size={24} className="animate-spin text-green-600" />
        <span className="ml-2 text-gray-500">Cargando pedidos...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center py-12 text-red-500">
        <XCircle size={20} className="mr-2" />
        Error al cargar los pedidos
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Stats Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Pedidos" value={estadisticas.total} icon={Package} />
        <StatCard title="Pendientes" value={estadisticas.pendientes} icon={Clock} />
        <StatCard title="En Preparación" value={estadisticas.enPreparacion} icon={ChefHat} />
        <StatCard title="Entregados Hoy" value={estadisticas.entregados} icon={CheckCircle} />
      </div>

      {/* Filtros */}
      <Card>
        <div className="flex flex-col sm:flex-row gap-3 p-4">
          {/* Search */}
          <div className="relative flex-1">
            <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input
              type="text"
              placeholder="Buscar por número de pedido o cliente..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none"
            />
          </div>

          {/* Filter Estado */}
          <select
            value={filtroEstado}
            onChange={(e) => setFiltroEstado(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none"
          >
            <option value="todos">Todos los estados</option>
            <option value={ESTADOS_PEDIDO.PENDIENTE}>Pendiente</option>
            <option value={ESTADOS_PEDIDO.CONFIRMADO}>Confirmado</option>
            <option value={ESTADOS_PEDIDO.PREPARANDO}>Preparando</option>
            <option value={ESTADOS_PEDIDO.LISTO}>Listo</option>
            <option value={ESTADOS_PEDIDO.EN_CAMINO}>En Camino</option>
            <option value={ESTADOS_PEDIDO.ENTREGADO}>Entregado</option>
            <option value={ESTADOS_PEDIDO.CANCELADO}>Cancelado</option>
          </select>

          {/* Filter Tipo */}
          <select
            value={filtroTipo}
            onChange={(e) => setFiltroTipo(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none"
          >
            <option value="todos">Todos los tipos</option>
            <option value={TIPOS_PEDIDO.DELIVERY}>Delivery</option>
            <option value={TIPOS_PEDIDO.RECOJO_TIENDA}>Recojo en Tienda</option>
          </select>
        </div>
      </Card>

      {/* Tabla */}
      <Card>
        {pedidosFiltrados.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-12 text-gray-400">
            <PackageX size={48} className="mb-3" />
            <p className="font-medium">No se encontraron pedidos</p>
            <p className="text-sm mt-1">Los pedidos registrados aparecerán aquí</p>
          </div>
        ) : (
          <Table
            columns={columns}
            data={pedidosFiltrados}
            rowClassName={(row) => getClaseUrgencia(calcularUrgencia(row.creado_en, row.estado))}
          />
        )}
      </Card>

      {/* Modal de confirmación de cambio de estado */}
      {showConfirmCambioEstado && (
        <ConfirmDialog
          title="Cambiar Estado del Pedido"
          message={
            <div>
              <p className="mb-2">
                ¿Estás seguro de cambiar el estado del pedido{' '}
                <strong>#{selectedPedido?.numeroPedido || selectedPedido?.id}</strong>?
              </p>
              <div className="bg-gray-50 p-3 rounded-md">
                <div className="flex items-center gap-2 mb-2">
                  <span className="text-sm text-gray-600">Estado actual:</span>
                  <span className={`px-2 py-1 rounded text-xs ${getConfigEstado(selectedPedido?.estado).color}`}>
                    {getConfigEstado(selectedPedido?.estado).label}
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="text-sm text-gray-600">Nuevo estado:</span>
                  <span className={`px-2 py-1 rounded text-xs ${getConfigEstado(nuevoEstado).color}`}>
                    {getConfigEstado(nuevoEstado).label}
                  </span>
                </div>
              </div>
            </div>
          }
          onConfirm={confirmarCambioEstado}
          onCancel={() => {
            setShowConfirmCambioEstado(false);
            setSelectedPedido(null);
            setNuevoEstado(null);
          }}
          confirmText="Cambiar Estado"
          cancelText="Cancelar"
        />
      )}

      {/* Modal de detalle del pedido */}
      {showDetalleModal && selectedPedido && (
        <DetallePedidoModal
          pedido={selectedPedido}
          detalles={selectedPedido.detalles || detallesPorPedido.get(selectedPedido.id) || []}
          onClose={() => {
            setShowDetalleModal(false);
            setSelectedPedido(null);
          }}
          onAvanzarEstado={handleAvanzarEstadoModal}
        />
      )}
    </div>
  );
}
