import { clsx } from 'clsx';

export const Card = ({ children, className, ...props }) => (
  <div
    className={clsx(
      'bg-white rounded-lg border border-gray-200 shadow-sm p-6',
      className,
    )}
    {...props}
  >
    {children}
  </div>
);
