import { useState, useEffect } from 'react';
import { Building, MapPin, Users, Package, Calendar } from 'lucide-react';
import Badge from '../ui/Badge';
import Card from '../ui/Card';
import Button from '../ui/Button';
import negociosService from '../../services/negociosService';

/**
 * Modal con detalle completo del negocio
 * Incluye info, sedes, estadísticas, etc.
 */
const NegocioDetail = ({ negocio, onClose }) => {
  const [sedes, setSedes] = useState([]);
  const [stats, setStats] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (negocio?.id) {
      loadDetailData();
    }
  }, [negocio]);

  const loadDetailData = async () => {
    try {
      setIsLoading(true);
      const [sedesData, statsData] = await Promise.all([
        negociosService.getSedes(negocio.id),
        negociosService.getStats(negocio.id),
      ]);
      setSedes(sedesData);
      setStats(statsData);
    } catch (error) {
      console.error('Error cargando datos:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return <div className="text-center py-8">Cargando...</div>;
  }

  const getEstadoBadge = (estado) => {
    const estados = {
      'activo': 'active',
      'suspendido': 'suspended',
      'moroso': 'overdue',
      'cancelado': 'cancelled',
    };
    return estados[estado?.toLowerCase()] || 'default';
  };

  return (
    <div className="space-y-6">
      {/* Información General */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-xl font-semibold text-gray-900">{negocio.razonSocial}</h3>
          <Badge variant={getEstadoBadge(negocio.estado)}>{negocio.estado}</Badge>
        </div>
        
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <span className="text-gray-600">RUC:</span>
            <span className="ml-2 font-medium">{negocio.ruc}</span>
          </div>
          <div>
            <span className="text-gray-600">Email:</span>
            <span className="ml-2 font-medium">{negocio.email}</span>
          </div>
          <div>
            <span className="text-gray-600">Teléfono:</span>
            <span className="ml-2 font-medium">{negocio.telefono || 'No especificado'}</span>
          </div>
          <div>
            <span className="text-gray-600">Plan:</span>
            <span className="ml-2 font-medium">{negocio.planNombre}</span>
          </div>
          <div className="col-span-2">
            <span className="text-gray-600">Dirección Fiscal:</span>
            <span className="ml-2 font-medium">{negocio.direccionFiscal || 'No especificada'}</span>
          </div>
          <div>
            <span className="text-gray-600">Fecha Registro:</span>
            <span className="ml-2 font-medium">
              {negocio.creadoEn ? new Date(negocio.creadoEn).toLocaleDateString('es-PE') : 'N/A'}
            </span>
          </div>
        </div>
      </div>

      {/* Estadísticas */}
      {stats && (
        <div className="grid grid-cols-4 gap-4">
          <Card noPadding className="p-4">
            <div className="flex items-center">
              <Building className="w-8 h-8 text-blue-600 mr-3" />
              <div>
                <p className="text-2xl font-bold">{stats.sedes || 0}</p>
                <p className="text-xs text-gray-600">Sedes</p>
              </div>
            </div>
          </Card>
          
          <Card noPadding className="p-4">
            <div className="flex items-center">
              <Users className="w-8 h-8 text-green-600 mr-3" />
              <div>
                <p className="text-2xl font-bold">{stats.usuarios || 0}</p>
                <p className="text-xs text-gray-600">Usuarios</p>
              </div>
            </div>
          </Card>
          
          <Card noPadding className="p-4">
            <div className="flex items-center">
              <Package className="w-8 h-8 text-purple-600 mr-3" />
              <div>
                <p className="text-2xl font-bold">{stats.productos || 0}</p>
                <p className="text-xs text-gray-600">Productos</p>
              </div>
            </div>
          </Card>
          
          <Card noPadding className="p-4">
            <div className="flex items-center">
              <Calendar className="w-8 h-8 text-yellow-600 mr-3" />
              <div>
                <p className="text-2xl font-bold">{stats.ventasMes || 0}</p>
                <p className="text-xs text-gray-600">Ventas/Mes</p>
              </div>
            </div>
          </Card>
        </div>
      )}

      {/* Sedes */}
      <div>
        <h4 className="text-lg font-semibold text-gray-900 mb-3">Sedes del Negocio</h4>
        <div className="space-y-3">
          {sedes && sedes.length > 0 ? (
            sedes.map((sede) => (
              <div key={sede.id} className="p-4 border border-gray-200 rounded-lg">
                <div className="flex items-start justify-between">
                  <div className="flex items-start">
                    <MapPin className="w-5 h-5 text-gray-400 mr-3 mt-1" />
                    <div>
                      <h5 className="font-semibold text-gray-900">
                        {sede.nombre}
                        {sede.esSedePrincipal && (
                          <Badge variant="info" className="ml-2">Principal</Badge>
                        )}
                      </h5>
                      <p className="text-sm text-gray-600 mt-1">{sede.direccion}</p>
                      {sede.telefono && (
                        <p className="text-sm text-gray-600">Tel: {sede.telefono}</p>
                      )}
                    </div>
                  </div>
                  <Badge variant={sede.estaActivo ? 'active' : 'inactive'}>
                    {sede.estaActivo ? 'Activa' : 'Inactiva'}
                  </Badge>
                </div>
              </div>
            ))
          ) : (
            <p className="text-center text-gray-500 py-4">No hay sedes registradas</p>
          )}
        </div>
      </div>

      {/* Botones */}
      <div className="flex justify-end gap-3 pt-4 border-t">
        <Button variant="outline" onClick={onClose}>
          Cerrar
        </Button>
      </div>
    </div>
  );
};

export default NegocioDetail;
