import { useState } from 'react';
import {
  Plus,
  Edit,
  Trash2,
  MapPin,
  Phone,
  Star,
  Truck,
  ShoppingBag,
} from 'lucide-react';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { Modal } from '../ui/Modal';
import { ConfirmDialog } from '../ui/ConfirmDialog';
import { SedeForm } from './SedeForm';
import { formatPhone } from '@/shared/utils/formatters';

export const SedesPanel = ({
  negocioId,
  sedes,
  onCreateSede,
  onUpdateSede,
  onDeleteSede,
  isCreating,
  isUpdating,
  isDeleting,
}) => {
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [editingSede, setEditingSede] = useState(null);
  const [deletingSede, setDeletingSede] = useState(null);

  const handleCreate = async (data) => {
    await onCreateSede({
      ...data,
      negocio: { id: negocioId },
    });
    setIsFormOpen(false);
  };

  const handleEdit = (sede) => {
    setEditingSede(sede);
    setIsFormOpen(true);
  };

  const handleUpdate = async (data) => {
    await onUpdateSede({
      ...data,
      id: editingSede.id,
      negocio: { id: negocioId },
    });
    setIsFormOpen(false);
    setEditingSede(null);
  };

  const handleDeleteClick = (sede) => {
    setDeletingSede(sede);
    setIsDeleteOpen(true);
  };

  const handleDeleteConfirm = async () => {
    await onDeleteSede(deletingSede.id);
    setIsDeleteOpen(false);
    setDeletingSede(null);
  };

  const closeForm = () => {
    setIsFormOpen(false);
    setEditingSede(null);
  };

  return (
    <div className="space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h4 className="text-sm font-semibold text-gray-800">
            Sedes del Negocio
          </h4>
          <p className="text-xs text-gray-500 mt-0.5">
            {sedes.length} sede{sedes.length !== 1 ? 's' : ''} registrada
            {sedes.length !== 1 ? 's' : ''}
          </p>
        </div>
        <Button size="sm" onClick={() => setIsFormOpen(true)}>
          <Plus size={16} />
          Nueva Sede
        </Button>
      </div>

      {/* Sedes list */}
      {sedes.length === 0 ? (
        <div className="bg-gray-50 rounded-xl border-2 border-dashed border-gray-200 py-12 text-center">
          <MapPin size={32} className="mx-auto text-gray-300 mb-3" />
          <p className="text-sm font-medium text-gray-500">
            No hay sedes registradas
          </p>
          <p className="text-xs text-gray-400 mt-1">
            Agrega la primera sede para este negocio
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {sedes.map((sede) => (
            <div
              key={sede.id}
              className="bg-white rounded-lg border border-gray-200 p-4 hover:border-blue-200 hover:shadow-sm transition-all"
            >
              <div className="flex items-start justify-between">
                {/* Sede info */}
                <div className="flex gap-3 min-w-0">
                  <div className="flex-shrink-0 w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center">
                    <MapPin size={18} className="text-blue-600" />
                  </div>
                  <div className="min-w-0">
                    <div className="flex items-center gap-2">
                      <h5 className="text-sm font-semibold text-gray-900 truncate">
                        {sede.nombre}
                      </h5>
                      {sede.esPrincipal && (
                        <Badge variant="info" className="flex-shrink-0">
                          <Star size={10} className="mr-1" />
                          Principal
                        </Badge>
                      )}
                    </div>
                    <p className="text-xs text-gray-500 font-mono mt-0.5">
                      {sede.codigo}
                    </p>
                    {sede.direccion && (
                      <p className="text-sm text-gray-600 mt-1 flex items-center gap-1.5">
                        <MapPin size={12} className="text-gray-400 flex-shrink-0" />
                        <span className="truncate">{sede.direccion}</span>
                        {sede.ciudad && (
                          <span className="text-gray-400">
                            , {sede.ciudad}
                          </span>
                        )}
                      </p>
                    )}
                    {sede.telefono && (
                      <p className="text-sm text-gray-600 mt-0.5 flex items-center gap-1.5">
                        <Phone size={12} className="text-gray-400 flex-shrink-0" />
                        {formatPhone(sede.telefono)}
                      </p>
                    )}

                    {/* Feature badges */}
                    <div className="flex flex-wrap gap-1.5 mt-2">
                      {sede.deliveryHabilitado && (
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs bg-green-50 text-green-700 border border-green-200">
                          <Truck size={10} />
                          Delivery
                        </span>
                      )}
                      {sede.recojoHabilitado && (
                        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-xs bg-purple-50 text-purple-700 border border-purple-200">
                          <ShoppingBag size={10} />
                          Recojo
                        </span>
                      )}
                    </div>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-1 flex-shrink-0 ml-2">
                  <button
                    title="Editar sede"
                    onClick={() => handleEdit(sede)}
                    className="p-1.5 rounded hover:bg-gray-100 text-gray-400 hover:text-gray-600 transition-colors"
                  >
                    <Edit size={15} />
                  </button>
                  <button
                    title="Eliminar sede"
                    onClick={() => handleDeleteClick(sede)}
                    className="p-1.5 rounded hover:bg-red-50 text-gray-400 hover:text-red-500 transition-colors"
                  >
                    <Trash2 size={15} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create/Edit Modal */}
      <Modal
        isOpen={isFormOpen}
        onClose={closeForm}
        title={editingSede ? 'Editar Sede' : 'Nueva Sede'}
        size="lg"
      >
        <SedeForm
          initialData={editingSede}
          onSubmit={editingSede ? handleUpdate : handleCreate}
          onCancel={closeForm}
          isLoading={isCreating || isUpdating}
        />
      </Modal>

      {/* Delete Confirm */}
      <ConfirmDialog
        isOpen={isDeleteOpen}
        onClose={() => {
          setIsDeleteOpen(false);
          setDeletingSede(null);
        }}
        onConfirm={handleDeleteConfirm}
        title="Eliminar Sede"
        message={`¿Está seguro de eliminar la sede "${deletingSede?.nombre}"? Esta acción no se puede deshacer.`}
        confirmText="Eliminar"
        isLoading={isDeleting}
      />
    </div>
  );
};
