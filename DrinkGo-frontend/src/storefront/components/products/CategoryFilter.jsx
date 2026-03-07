import { clsx } from 'clsx';

export const CategoryFilter = ({ categorias, selectedId, onSelect }) => {
  return (
    <div className="flex flex-wrap gap-2">
      <button
        onClick={() => onSelect(null)}
        className={clsx(
          'px-4 py-2 rounded-full text-sm font-medium transition-colors border',
          !selectedId
            ? 'bg-amber-500 text-white border-amber-500'
            : 'bg-white text-gray-700 border-gray-300 hover:border-amber-400 hover:text-amber-600',
        )}
      >
        Todos
      </button>
      {categorias.map((cat) => (
        <button
          key={cat.id}
          onClick={() => onSelect(cat.id)}
          className={clsx(
            'px-4 py-2 rounded-full text-sm font-medium transition-colors border',
            selectedId === cat.id
              ? 'bg-amber-500 text-white border-amber-500'
              : 'bg-white text-gray-700 border-gray-300 hover:border-amber-400 hover:text-amber-600',
          )}
        >
          {cat.nombre}
        </button>
      ))}
    </div>
  );
};
