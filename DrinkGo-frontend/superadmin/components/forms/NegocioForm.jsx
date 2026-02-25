import { useState, useEffect } from 'react';
import Input from '../ui/Input';
import Select from '../ui/Select';
import Button from '../ui/Button';
import negociosService from '../../services/negociosService';
import planesService from '../../services/planesService';

/**
 * Formulario para crear/editar negocio
 * Incluye datos del negocio y sede principal inicial
 */
const NegocioForm = ({ negocio = null, onSuccess, onCancel }) => {
  const [planes, setPlanes] = useState([]);
  const [formData, setFormData] = useState({
    // Datos del negocio
    razonSocial: '',
    nombreComercial: '',
    ruc: '',
    email: '',
    telefono: '',
    direccionFiscal: '',
    planSuscripcionId: '',
    
    // Datos de la sede principal
    sedeNombre: '',
    sedeDireccion: '',
    sedeTelefono: '',
    sedeEmail: '',
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    loadPlanes();
    if (negocio) {
      setFormData({
        razonSocial: negocio.razonSocial || '',
        nombreComercial: negocio.nombreComercial || '',
        ruc: negocio.ruc || '',
        email: negocio.email || '',
        telefono: negocio.telefono || '',
        direccionFiscal: negocio.direccionFiscal || '',
        planSuscripcionId: negocio.planSuscripcionId || '',
      });
    }
  }, [negocio]);

  const loadPlanes = async () => {
    try {
      const data = await planesService.getAll();
      setPlanes(data.map(plan => ({
        value: plan.id,
        label: `${plan.nombre} - S/ ${plan.precio}/${plan.periodoFacturacion}`
      })));
    } catch (error) {
      console.error('Error cargando planes:', error);
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.razonSocial.trim()) newErrors.razonSocial = 'Razón social es requerida';
    if (!formData.ruc.trim()) newErrors.ruc = 'RUC es requerido';
    if (formData.ruc.trim() && formData.ruc.length !== 11) newErrors.ruc = 'RUC debe tener 11 dígitos';
    if (!formData.email.trim()) newErrors.email = 'Email es requerido';
    if (!formData.planSuscripcionId) newErrors.planSuscripcionId = 'Plan es requerido';
    
    // Validar sede principal solo si es nuevo negocio
    if (!negocio) {
      if (!formData.sedeNombre.trim()) newErrors.sedeNombre = 'Nombre de sede es requerido';
      if (!formData.sedeDireccion.trim()) newErrors.sedeDireccion = 'Dirección de sede es requerida';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    try {
      setIsSubmitting(true);
      
      if (negocio) {
        await negociosService.update(negocio.id, formData);
      } else {
        await negociosService.create(formData);
      }
      
      onSuccess?.();
    } catch (error) {
      console.error('Error guardando negocio:', error);
      alert('Error al guardar el negocio. Por favor intente nuevamente.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Datos del Negocio */}
      <div>
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Datos del Negocio</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Input
            label="Razón Social"
            name="razonSocial"
            value={formData.razonSocial}
            onChange={handleChange}
            error={errors.razonSocial}
            required
          />
          
          <Input
            label="Nombre Comercial"
            name="nombreComercial"
            value={formData.nombreComercial}
            onChange={handleChange}
          />
          
          <Input
            label="RUC"
            name="ruc"
            value={formData.ruc}
            onChange={handleChange}
            error={errors.ruc}
            required
            maxLength={11}
          />
          
          <Input
            label="Email"
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            error={errors.email}
            required
          />
          
          <Input
            label="Teléfono"
            name="telefono"
            value={formData.telefono}
            onChange={handleChange}
          />
          
          <Select
            label="Plan de Suscripción"
            name="planSuscripcionId"
            value={formData.planSuscripcionId}
            onChange={handleChange}
            options={planes}
            error={errors.planSuscripcionId}
            required
          />
        </div>
        
        <Input
          label="Dirección Fiscal"
          name="direccionFiscal"
          value={formData.direccionFiscal}
          onChange={handleChange}
          className="mt-4"
        />
      </div>

      {/* Sede Principal (solo para nuevos negocios) */}
      {!negocio && (
        <div>
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Sede Principal</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Input
              label="Nombre de la Sede"
              name="sedeNombre"
              value={formData.sedeNombre}
              onChange={handleChange}
              error={errors.sedeNombre}
              required
              placeholder="Ej: Sede Central"
            />
            
            <Input
              label="Teléfono de la Sede"
              name="sedeTelefono"
              value={formData.sedeTelefono}
              onChange={handleChange}
            />
            
            <Input
              label="Email de la Sede"
              type="email"
              name="sedeEmail"
              value={formData.sedeEmail}
              onChange={handleChange}
            />
          </div>
          
          <Input
            label="Dirección de la Sede"
            name="sedeDireccion"
            value={formData.sedeDireccion}
            onChange={handleChange}
            error={errors.sedeDireccion}
            required
            className="mt-4"
            placeholder="Dirección completa de la sede"
          />
        </div>
      )}

      {/* Botones */}
      <div className="flex justify-end gap-3 pt-4 border-t">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" variant="primary" disabled={isSubmitting}>
          {isSubmitting ? 'Guardando...' : negocio ? 'Actualizar' : 'Crear Negocio'}
        </Button>
      </div>
    </form>
  );
};

export default NegocioForm;
