import { useState, useEffect } from 'react';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Button from '../ui/Button';
import planesService from '../../services/planesService';

/**
 * Formulario para crear/editar plan de suscripción
 */
const PlanForm = ({ plan = null, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState({
    nombre: '',
    descripcion: '',
    precio: '',
    moneda: 'PEN',
    periodoFacturacion: 'mensual',
    maxSedes: 1,
    maxUsuarios: 5,
    maxProductos: 500,
    maxAlmacenesPorSede: 2,
    permitePos: true,
    permiteTiendaOnline: false,
    permiteDelivery: false,
    permiteMesas: false,
    permiteFacturacionElectronica: false,
    permiteMultiAlmacen: false,
    permiteReportesAvanzados: false,
    permiteAccesoApi: false,
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (plan) {
      setFormData({
        nombre: plan.nombre || '',
        descripcion: plan.descripcion || '',
        precio: plan.precio || '',
        moneda: plan.moneda || 'PEN',
        periodoFacturacion: plan.periodoFacturacion || 'mensual',
        maxSedes: plan.maxSedes || 1,
        maxUsuarios: plan.maxUsuarios || 5,
        maxProductos: plan.maxProductos || 500,
        maxAlmacenesPorSede: plan.maxAlmacenesPorSede || 2,
        permitePos: plan.permitePos !== undefined ? plan.permitePos : true,
        permiteTiendaOnline: plan.permiteTiendaOnline || false,
        permiteDelivery: plan.permiteDelivery || false,
        permiteMesas: plan.permiteMesas || false,
        permiteFacturacionElectronica: plan.permiteFacturacionElectronica || false,
        permiteMultiAlmacen: plan.permiteMultiAlmacen || false,
        permiteReportesAvanzados: plan.permiteReportesAvanzados || false,
        permiteAccesoApi: plan.permiteAccesoApi || false,
      });
    }
  }, [plan]);

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.nombre.trim()) newErrors.nombre = 'Nombre es requerido';
    if (!formData.precio || formData.precio <= 0) newErrors.precio = 'Precio debe ser mayor a 0';
    if (formData.maxSedes < 1) newErrors.maxSedes = 'Debe permitir al menos 1 sede';
    if (formData.maxUsuarios < 1) newErrors.maxUsuarios = 'Debe permitir al menos 1 usuario';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : type === 'number' ? Number(value) : value
    }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    try {
      setIsSubmitting(true);
      
      if (plan) {
        await planesService.update(plan.id, formData);
      } else {
        await planesService.create(formData);
      }
      
      onSuccess?.();
    } catch (error) {
      console.error('Error guardando plan:', error);
      alert('Error al guardar el plan. Por favor intente nuevamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Información Básica */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Información del Plan</h3>
        <div className="space-y-4">
          <Input
            label="Nombre del Plan"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
            error={errors.nombre}
            required
            placeholder="Ej: Plan Básico, Plan Premium"
          />
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Input
              label="Precio"
              type="number"
              name="precio"
              value={formData.precio}
              onChange={handleChange}
              error={errors.precio}
              required
              min="0"
              step="0.01"
            />
            
            <Select
              label="Moneda"
              name="moneda"
              value={formData.moneda}
              onChange={handleChange}
              options={[
                { value: 'PEN', label: 'PEN (Soles)' },
                { value: 'USD', label: 'USD (Dólares)' },
              ]}
              required
            />
            
            <Select
              label="Periodo"
              name="periodoFacturacion"
              value={formData.periodoFacturacion}
              onChange={handleChange}
              options={[
                { value: 'mensual', label: 'Mensual' },
                { value: 'anual', label: 'Anual' },
              ]}
              required
            />
          </div>
          
          <div className="col-span-full">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descripción
            </label>
            <textarea
              name="descripcion"
              value={formData.descripcion}
              onChange={handleChange}
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Descripción breve del plan"
            />
          </div>
        </div>
      </div>

      {/* Límites Operativos */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Límites Operativos</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Máximo de Sedes"
            type="number"
            name="maxSedes"
            value={formData.maxSedes}
            onChange={handleChange}
            error={errors.maxSedes}
            required
            min="1"
          />
          
          <Input
            label="Máximo de Usuarios"
            type="number"
            name="maxUsuarios"
            value={formData.maxUsuarios}
            onChange={handleChange}
            error={errors.maxUsuarios}
            required
            min="1"
          />
          
          <Input
            label="Máximo de Productos"
            type="number"
            name="maxProductos"
            value={formData.maxProductos}
            onChange={handleChange}
            required
            min="1"
          />
          
          <Input
            label="Almacenes por Sede"
            type="number"
            name="maxAlmacenesPorSede"
            value={formData.maxAlmacenesPorSede}
            onChange={handleChange}
            required
            min="1"
          />
        </div>
      </div>

      {/* Funcionalidades */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Funcionalidades Habilitadas</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permitePos"
              checked={formData.permitePos}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite POS</span>
          </label>
          
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permiteTiendaOnline"
              checked={formData.permiteTiendaOnline}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite Tienda Online</span>
          </label>
          
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permiteDelivery"
              checked={formData.permiteDelivery}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite Delivery</span>
          </label>
          
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permiteMesas"
              checked={formData.permiteMesas}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite Mesas</span>
          </label>
          
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permiteFacturacionElectronica"
              checked={formData.permiteFacturacionElectronica}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite Facturación Electrónica</span>
          </label>
          
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permiteMultiAlmacen"
              checked={formData.permiteMultiAlmacen}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite Multi-Almacén</span>
          </label>
          
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permiteReportesAvanzados"
              checked={formData.permiteReportesAvanzados}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite Reportes Avanzados</span>
          </label>
          
          <label className="flex items-center">
            <input
              type="checkbox"
              name="permiteAccesoApi"
              checked={formData.permiteAccesoApi}
              onChange={handleChange}
              className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
            />
            <span className="ml-2 text-sm text-gray-700">Permite Acceso API</span>
          </label>
        </div>
      </div>

      {/* Botones */}
      <div className="flex justify-end gap-3 pt-4 border-t">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" variant="primary" disabled={isSubmitting}>
          {isSubmitting ? 'Guardando...' : plan ? 'Actualizar Plan' : 'Crear Plan'}
        </Button>
      </div>
    </form>
  );
};

export default PlanForm;
