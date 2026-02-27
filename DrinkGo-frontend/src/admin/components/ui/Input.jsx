import { forwardRef } from 'react';
import { clsx } from 'clsx';

export const Input = forwardRef(({ label, error, className, required, ...props }, ref) => (
  <div className="w-full">
    {label && (
      <label className="block text-sm font-medium text-gray-700 mb-1">
        {label}
        {required && <span className="text-red-500 ml-0.5">*</span>}
      </label>
    )}
    <input
      ref={ref}
      className={clsx(
        'block w-full border rounded-lg px-3 py-2 text-sm transition-colors',
        'focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500',
        'disabled:bg-gray-100 disabled:cursor-not-allowed',
        error ? 'border-red-300 text-red-900' : 'border-gray-300 text-gray-900',
        className,
      )}
      {...props}
    />
    {error && <p className="text-sm text-red-500 mt-1">{error}</p>}
  </div>
));

Input.displayName = 'Input';
