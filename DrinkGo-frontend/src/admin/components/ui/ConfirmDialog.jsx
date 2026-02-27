import { Modal } from './Modal';
import { Button } from './Button';
import { AlertTriangle } from 'lucide-react';

export const ConfirmDialog = ({
  isOpen,
  onClose,
  onConfirm,
  title = 'Confirmar acción',
  message = '¿Está seguro de realizar esta acción?',
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
  variant = 'danger',
  isLoading = false,
}) => (
  <Modal isOpen={isOpen} onClose={onClose} title={title} size="sm">
    <div className="flex flex-col items-center text-center py-2">
      <div className="p-3 bg-red-100 rounded-full mb-4">
        <AlertTriangle size={24} className="text-red-600" />
      </div>
      <p className="text-sm text-gray-600">{message}</p>
    </div>
    <div className="flex justify-end gap-2 pt-4">
      <Button variant="outline" onClick={onClose} disabled={isLoading}>
        {cancelText}
      </Button>
      <Button variant={variant} onClick={onConfirm} disabled={isLoading}>
        {isLoading ? 'Procesando...' : confirmText}
      </Button>
    </div>
  </Modal>
);
