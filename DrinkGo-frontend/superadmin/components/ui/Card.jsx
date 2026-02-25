import clsx from 'clsx';

/**
 * Componente Card para métricas y contenedores
 * Diseño limpio con sombras sutiles
 */
const Card = ({ 
  title, 
  subtitle, 
  children, 
  className = '',
  headerAction,
  noPadding = false 
}) => {
  return (
    <div className={clsx(
      'bg-white rounded-lg shadow-sm border border-gray-200',
      className
    )}>
      {(title || headerAction) && (
        <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
          <div>
            {title && <h3 className="text-lg font-semibold text-gray-900">{title}</h3>}
            {subtitle && <p className="text-sm text-gray-600 mt-1">{subtitle}</p>}
          </div>
          {headerAction && <div>{headerAction}</div>}
        </div>
      )}
      <div className={clsx(!noPadding && 'px-6 py-4')}>
        {children}
      </div>
    </div>
  );
};

export default Card;
