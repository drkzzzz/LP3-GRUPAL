import clsx from 'clsx';

/**
 * Componente Badge para indicadores de estado
 * Colores semánticos según las instrucciones
 */
const Badge = ({ children, variant = 'default', className = '' }) => {
  const variants = {
    // Estados positivos (Verde)
    success: 'bg-green-100 text-green-800 border-green-200',
    active: 'bg-green-100 text-green-800 border-green-200',
    paid: 'bg-green-100 text-green-800 border-green-200',
    delivered: 'bg-green-100 text-green-800 border-green-200',
    
    // Estados de advertencia (Amarillo/Naranja)
    warning: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    pending: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    process: 'bg-orange-100 text-orange-800 border-orange-200',
    lowStock: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    
    // Estados negativos (Rojo)
    danger: 'bg-red-100 text-red-800 border-red-200',
    inactive: 'bg-red-100 text-red-800 border-red-200',
    cancelled: 'bg-red-100 text-red-800 border-red-200',
    overdue: 'bg-red-100 text-red-800 border-red-200',
    
    // Estados neutrales (Gris/Azul)
    default: 'bg-gray-100 text-gray-800 border-gray-200',
    info: 'bg-blue-100 text-blue-800 border-blue-200',
    suspended: 'bg-gray-100 text-gray-800 border-gray-200',
  };
  
  return (
    <span className={clsx(
      'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border',
      variants[variant],
      className
    )}>
      {children}
    </span>
  );
};

export default Badge;
