import { forwardRef } from 'react';
import { clsx } from 'clsx';

export const Select = forwardRef(
  ({ label, error, options = [], placeholder, className, required, ...props }, ref) => (
    <div className="w-full">
      {label && (
        <label className="block text-sm font-medium text-gray-700 mb-1">
          {label}
          {required && <span className="text-red-500 ml-0.5">*</span>}
        </label>
      )}
      <select
        ref={ref}
        className={clsx(
          'block w-full border rounded-lg px-3 py-2 text-sm transition-colors',
          'focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500',
          'disabled:bg-gray-100 disabled:cursor-not-allowed',
          error ? 'border-red-300' : 'border-gray-300',
          className,
        )}
        {...props}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      {error && <p className="text-sm text-red-500 mt-1">{error}</p>}
    </div>
  ),
);

Select.displayName = 'Select';
