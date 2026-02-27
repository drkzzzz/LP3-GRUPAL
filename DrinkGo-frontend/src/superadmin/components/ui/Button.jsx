import { clsx } from 'clsx';

const BASE =
  'inline-flex items-center justify-center font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';

const VARIANTS = {
  primary: 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500',
  secondary: 'bg-gray-200 text-gray-800 hover:bg-gray-300 focus:ring-gray-400',
  outline: 'border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 focus:ring-blue-500',
  ghost: 'text-gray-600 hover:bg-gray-100 focus:ring-gray-400',
  danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
};

const SIZES = {
  sm: 'px-3 py-1.5 text-sm gap-1.5',
  md: 'px-4 py-2 text-sm gap-2',
  lg: 'px-6 py-3 text-base gap-2',
  icon: 'p-2',
};

export const Button = ({
  children,
  variant = 'primary',
  size = 'md',
  className,
  ...props
}) => (
  <button
    className={clsx(BASE, VARIANTS[variant], SIZES[size], className)}
    {...props}
  >
    {children}
  </button>
);
