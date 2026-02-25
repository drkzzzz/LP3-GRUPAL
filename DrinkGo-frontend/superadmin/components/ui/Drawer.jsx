import { X } from 'lucide-react';
import { useEffect } from 'react';
import clsx from 'clsx';

/**
 * Componente Drawer (Panel Lateral)
 * Para formularios complejos manteniendo contexto visual
 */
const Drawer = ({ 
  isOpen, 
  onClose, 
  title, 
  children, 
  position = 'right',
  size = 'md',
  footer
}) => {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const sizes = {
    sm: 'max-w-md',
    md: 'max-w-2xl',
    lg: 'max-w-4xl',
  };

  const positions = {
    right: 'right-0',
    left: 'left-0',
  };

  return (
    <div className="fixed inset-0 z-50 overflow-hidden">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black bg-opacity-50 transition-opacity"
        onClick={onClose}
      />
      
      {/* Drawer */}
      <div className={clsx('fixed inset-y-0 flex', positions[position])}>
        <div 
          className={clsx(
            'relative w-screen bg-white shadow-xl',
            sizes[size]
          )}
          onClick={(e) => e.stopPropagation()}
        >
          {/* Header */}
          <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900">{title}</h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
          
          {/* Body */}
          <div className="px-6 py-4 overflow-y-auto h-[calc(100vh-8rem)]">
            {children}
          </div>
          
          {/* Footer */}
          {footer && (
            <div className="absolute bottom-0 left-0 right-0 px-6 py-4 border-t border-gray-200 bg-gray-50">
              {footer}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Drawer;
