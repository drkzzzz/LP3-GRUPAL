import { clsx } from 'clsx';

/**
 * Tarjeta de estadística para el header del módulo.
 * Misma API que la del SuperAdmin pero con verde para Admin.
 */
export const StatCard = ({ title, value, icon: Icon, className }) => (
  <div
    className={clsx(
      'bg-white rounded-lg border border-gray-200 shadow-sm p-4 flex items-center gap-4',
      className,
    )}
  >
    {Icon && (
      <div className="p-2.5 bg-green-50 rounded-lg flex-shrink-0">
        <Icon size={22} className="text-green-600" />
      </div>
    )}
    <div>
      <p className="text-sm text-gray-500">{title}</p>
      <p className="text-2xl font-bold text-gray-900">{value}</p>
    </div>
  </div>
);
