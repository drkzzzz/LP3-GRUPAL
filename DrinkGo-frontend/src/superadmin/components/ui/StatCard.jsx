import { clsx } from 'clsx';

export const StatCard = ({ title, value, icon: Icon, trend, trendLabel, className }) => (
  <div
    className={clsx(
      'bg-white rounded-lg border border-gray-200 shadow-sm p-6',
      className,
    )}
  >
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm font-medium text-gray-600">{title}</p>
        <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
        {trend !== undefined && (
          <p
            className={clsx(
              'text-sm mt-1',
              trend >= 0 ? 'text-green-600' : 'text-red-600',
            )}
          >
            {trend >= 0 ? '+' : ''}
            {trend}% {trendLabel}
          </p>
        )}
      </div>
      {Icon && (
        <div className="p-3 bg-blue-50 rounded-lg">
          <Icon size={24} className="text-blue-600" />
        </div>
      )}
    </div>
  </div>
);
