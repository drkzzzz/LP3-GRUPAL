import { useState, useEffect } from 'react';
import Badge from '../ui/Badge';
import Button from '../ui/Button';
import planesService from '../../services/planesService';

/**
 * Modal con detalle del plan y negocios suscritos
 */
const PlanDetail = ({ plan, onClose }) => {
  const [negocios, setNegocios] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (plan?.id) {
      loadNegocios();
    }
  }, [plan]);

  const loadNegocios = async () => {
    try {
      setIsLoading(true);
      const data = await planesService.getSubscribedBusinesses(plan.id);
      setNegocios(data);
    } catch (error) {
      console.error('Error cargando negocios:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Información del Plan */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-xl font-semibold text-gray-900">{plan.nombre}</h3>
          <Badge variant={plan.estaActivo ? 'active' : 'inactive'}>
            {plan.estaActivo ? 'Activo' : 'Inactivo'}
          </Badge>
        </div>
        
        <div className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <span className="text-gray-600">Precio:</span>
            <span className="ml-2 font-medium">{plan.moneda} {plan.precio} / {plan.periodoFacturacion}</span>
          </div>
          <div>
            <span className="text-gray-600">Negocios Suscritos:</span>
            <span className="ml-2 font-medium">{plan.negociosSuscritos || 0}</span>
          </div>
        </div>
        
        {plan.descripcion && (
          <p className="text-sm text-gray-600 mt-3">{plan.descripcion}</p>
        )}
      </div>

      {/* Límites */}
      <div>
        <h4 className="text-lg font-semibold text-gray-900 mb-3">Límites Operativos</h4>
        <div className="grid grid-cols-2 gap-3 text-sm">
          <div className="flex justify-between p-3 bg-gray-50 rounded-lg">
            <span>Sedes:</span>
            <span className="font-semibold">{plan.maxSedes}</span>
          </div>
          <div className="flex justify-between p-3 bg-gray-50 rounded-lg">
            <span>Usuarios:</span>
            <span className="font-semibold">{plan.maxUsuarios}</span>
          </div>
          <div className="flex justify-between p-3 bg-gray-50 rounded-lg">
            <span>Productos:</span>
            <span className="font-semibold">{plan.maxProductos}</span>
          </div>
          <div className="flex justify-between p-3 bg-gray-50 rounded-lg">
            <span>Almacenes/Sede:</span>
            <span className="font-semibold">{plan.maxAlmacenesPorSede}</span>
          </div>
        </div>
      </div>

      {/* Funcionalidades */}
      <div>
        <h4 className="text-lg font-semibold text-gray-900 mb-3">Funcionalidades</h4>
        <div className="grid grid-cols-2 gap-2 text-sm">
          <div className={`flex items-center ${plan.permitePos ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permitePos ? '✓' : '✗'}</span> POS
          </div>
          <div className={`flex items-center ${plan.permiteTiendaOnline ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permiteTiendaOnline ? '✓' : '✗'}</span> Tienda Online
          </div>
          <div className={`flex items-center ${plan.permiteDelivery ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permiteDelivery ? '✓' : '✗'}</span> Delivery
          </div>
          <div className={`flex items-center ${plan.permiteMesas ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permiteMesas ? '✓' : '✗'}</span> Mesas
          </div>
          <div className={`flex items-center ${plan.permiteFacturacionElectronica ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permiteFacturacionElectronica ? '✓' : '✗'}</span> Facturación Electrónica
          </div>
          <div className={`flex items-center ${plan.permiteMultiAlmacen ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permiteMultiAlmacen ? '✓' : '✗'}</span> Multi-Almacén
          </div>
          <div className={`flex items-center ${plan.permiteReportesAvanzados ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permiteReportesAvanzados ? '✓' : '✗'}</span> Reportes Avanzados
          </div>
          <div className={`flex items-center ${plan.permiteAccesoApi ? 'text-green-600' : 'text-gray-400'}`}>
            <span className="mr-2">{plan.permiteAccesoApi ? '✓' : '✗'}</span> Acceso API
          </div>
        </div>
      </div>

      {/* Negocios suscritos */}
      <div>
        <h4 className="text-lg font-semibold text-gray-900 mb-3">Negocios Suscritos</h4>
        {isLoading ? (
          <p className="text-center text-gray-500 py-4">Cargando...</p>
        ) : negocios && negocios.length > 0 ? (
          <div className="max-h-64 overflow-y-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-200 sticky top-0">
                <tr>
                  <th className="px-4 py-2 text-left">Negocio</th>
                  <th className="px-4 py-2 text-left">Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {negocios.map((negocio) => (
                  <tr key={negocio.id}>
                    <td className="px-4 py-2">{negocio.razonSocial}</td>
                    <td className="px-4 py-2">
                      <Badge variant={negocio.estado === 'activo' ? 'active' : 'inactive'}>
                        {negocio.estado}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-center text-gray-500 py-4">No hay negocios suscritos a este plan</p>
        )}
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

export default PlanDetail;
