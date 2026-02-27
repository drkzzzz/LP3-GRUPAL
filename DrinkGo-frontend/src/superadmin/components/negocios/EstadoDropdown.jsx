import { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';
import { Badge } from '../ui/Badge';

const ESTADO_CONFIG = {
  activo: { label: 'Activo', variant: 'success' },
  pendiente: { label: 'Pendiente', variant: 'warning' },
  suspendido: { label: 'Suspendido', variant: 'warning' },
  cancelado: { label: 'Cancelado', variant: 'error' },
};

export const EstadoDropdown = ({ current, onChange, isUpdating }) => {
  const [isOpen, setIsOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const config = ESTADO_CONFIG[current] || ESTADO_CONFIG.pendiente;

  const handleSelect = async (estado) => {
    if (estado === current) {
      setIsOpen(false);
      return;
    }
    setIsOpen(false);
    await onChange(estado);
  };

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        disabled={isUpdating}
        className="flex items-center gap-1 group cursor-pointer"
        title="Cambiar estado"
      >
        <Badge variant={config.variant}>{config.label}</Badge>
        <ChevronDown
          size={12}
          className="text-gray-400 group-hover:text-gray-600 transition-colors"
        />
      </button>

      {isOpen && (
        <div className="absolute z-50 mt-1 w-40 bg-white border border-gray-200 rounded-lg shadow-lg py-1 left-0">
          {Object.entries(ESTADO_CONFIG).map(([key, cfg]) => (
            <button
              key={key}
              onClick={() => handleSelect(key)}
              className={`w-full text-left px-3 py-2 text-sm flex items-center gap-2 hover:bg-gray-50 transition-colors ${
                key === current ? 'bg-gray-50 font-medium' : ''
              }`}
            >
              <Badge variant={cfg.variant}>{cfg.label}</Badge>
              {key === current && (
                <span className="text-xs text-gray-400 ml-auto">actual</span>
              )}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};
