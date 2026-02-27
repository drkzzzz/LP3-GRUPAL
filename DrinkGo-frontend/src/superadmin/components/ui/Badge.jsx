import { clsx } from 'clsx';

const VARIANTS = {
  success: 'bg-green-100 text-green-700 border-green-300',
  warning: 'bg-yellow-100 text-yellow-700 border-yellow-300',
  error: 'bg-red-100 text-red-700 border-red-300',
  info: 'bg-blue-100 text-blue-700 border-blue-300',
};

export const Badge = ({ children, variant = 'info', className }) => (
  <span
    className={clsx(
      'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border',
      VARIANTS[variant],
      className,
    )}
  >
    {children}
  </span>
);
