/**
 * Selector de productos para pedido manual
 * Permite buscar y agregar productos al carrito
 */
import { useState } from 'react';
import { useBuscarProductos } from '@/admin/hooks/useProductos';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Search, Plus, Minus, Trash2, Package, Loader2 } from 'lucide-react';

export function SelectorProductos({ items = [], onItemsChange }) {
  const [busqueda, setBusqueda] = useState('');
  
  const debouncedSearch = useDebounce(busqueda, 400);
  const { data: productosEncontrados = [], isLoading: buscandoProductos } = 
    useBuscarProductos(debouncedSearch, debouncedSearch.trim().length >= 2);
  
  const agregarProducto = (producto) => {
    // Verificar si ya existe en el carrito
    const existente = items.find(item => item.productoId === producto.id);
    
    if (existente) {
      // Incrementar cantidad
      onItemsChange(
        items.map(item =>
          item.productoId === producto.id
            ? { ...item, cantidad: item.cantidad + 1 }
            : item
        )
      );
    } else {
      // Agregar nuevo item
      onItemsChange([
        ...items,
        {
          productoId: producto.id,
          nombreProducto: producto.nombre,
          skuProducto: producto.sku,
          cantidad: 1,
          precioUnitario: parseFloat(producto.precioVenta || 0),
          stock: producto.stock,
        },
      ]);
    }
    
    setBusqueda('');
  };
  
  const cambiarCantidad = (productoId, nuevaCantidad) => {
    if (nuevaCantidad <= 0) {
      eliminarProducto(productoId);
      return;
    }
    
    onItemsChange(
      items.map(item =>
        item.productoId === productoId
          ? { ...item, cantidad: nuevaCantidad }
          : item
      )
    );
  };
  
  const eliminarProducto = (productoId) => {
    onItemsChange(items.filter(item => item.productoId !== productoId));
  };
  
  const calcularSubtotal = (item) => {
    return item.precioUnitario * item.cantidad;
  };
  
  const calcularTotal = () => {
    return items.reduce((total, item) => total + calcularSubtotal(item), 0);
  };
  
  return (
    <div className="space-y-4">
      {/* Buscador de productos */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Buscar Producto *
        </label>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="Buscar por nombre o SKU..."
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
            className="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
          {buscandoProductos && (
            <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400 animate-spin" />
          )}
        </div>
        
        {/* Resultados de búsqueda */}
        {debouncedSearch.trim().length >= 2 && productosEncontrados.length > 0 && (
          <div className="mt-2 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
            {productosEncontrados.map((producto) => (
              <button
                key={producto.id}
                type="button"
                onClick={() => agregarProducto(producto)}
                className="w-full text-left px-4 py-3 hover:bg-gray-50 border-b border-gray-100 last:border-b-0 flex items-center justify-between"
              >
                <div>
                  <p className="font-medium text-gray-900">{producto.nombre}</p>
                  <div className="flex items-center gap-3 mt-1 text-sm text-gray-600">
                    <span className="font-semibold text-blue-600">S/ {parseFloat(producto.precioVenta || 0).toFixed(2)}</span>
                    <span>• SKU: {producto.sku}</span>
                    {producto.stock !== null && producto.stock !== undefined && (
                      <span className={producto.stock > 0 ? 'text-green-600 font-medium' : 'text-red-600 font-medium'}>
                        • Stock: {producto.stock}
                      </span>
                    )}
                  </div>
                </div>
                <Plus className="w-5 h-5 text-blue-600" />
              </button>
            ))}
          </div>
        )}
        
        {/* Sin resultados */}
        {debouncedSearch.trim().length >= 2 && 
         !buscandoProductos && 
         productosEncontrados.length === 0 && (
          <p className="mt-2 text-sm text-gray-500">
            No se encontraron productos
          </p>
        )}
      </div>
      
      {/* Lista de productos seleccionados (Carrito) */}
      <div>
        <div className="flex items-center justify-between mb-2">
          <label className="text-sm font-medium text-gray-700">
            Productos Seleccionados
          </label>
          <span className="text-sm text-gray-500">
            {items.length} {items.length === 1 ? 'producto' : 'productos'}
          </span>
        </div>
        
        {items.length === 0 ? (
          <div className="bg-gray-50 border border-dashed border-gray-300 rounded-lg p-8 text-center">
            <Package className="w-12 h-12 text-gray-400 mx-auto mb-2" />
            <p className="text-gray-600">No hay productos agregados</p>
            <p className="text-sm text-gray-500 mt-1">
              Busca y agrega productos arriba
            </p>
          </div>
        ) : (
          <div className="bg-gray-50 rounded-lg divide-y divide-gray-200">
            {items.map((item) => (
              <div key={item.productoId} className="p-4 flex items-center gap-4">
                {/* Info del producto */}
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-900 truncate">
                    {item.nombreProducto}
                  </p>
                  <div className="flex items-center gap-3 mt-1 text-sm text-gray-600">
                    <span>S/ {item.precioUnitario.toFixed(2)}</span>
                    <span>• SKU: {item.skuProducto}</span>
                    {item.stock !== null && item.stock !== undefined && (
                      <span className={item.cantidad > item.stock ? 'text-red-600 font-medium' : 'text-gray-500'}>
                        • Stock: {item.stock}
                      </span>
                    )}
                  </div>
                </div>
                
                {/* Controlador de cantidad */}
                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={() => cambiarCantidad(item.productoId, item.cantidad - 1)}
                    className="p-1.5 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    <Minus className="w-4 h-4 text-gray-600" />
                  </button>
                  
                  <input
                    type="number"
                    min="1"
                    value={item.cantidad}
                    onChange={(e) => cambiarCantidad(item.productoId, parseInt(e.target.value) || 1)}
                    className="w-16 px-2 py-1.5 text-center border border-gray-300 rounded-lg"
                  />
                  
                  <button
                    type="button"
                    onClick={() => cambiarCantidad(item.productoId, item.cantidad + 1)}
                    className="p-1.5 bg-white border border-gray-300 rounded-lg hover:bg-gray-50"
                  >
                    <Plus className="w-4 h-4 text-gray-600" />
                  </button>
                </div>
                
                {/* Subtotal */}
                <div className="text-right min-w-[80px]">
                  <p className="font-semibold text-gray-900">
                    S/ {calcularSubtotal(item).toFixed(2)}
                  </p>
                </div>
                
                {/* Botón eliminar */}
                <button
                  type="button"
                  onClick={() => eliminarProducto(item.productoId)}
                  className="p-2 text-red-600 hover:bg-red-50 rounded-lg"
                  title="Eliminar producto"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
            
            {/* Total de items */}
            <div className="p-4 bg-blue-50">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-gray-700">
                  Subtotal ({items.length} {items.length === 1 ? 'producto' : 'productos'})
                </span>
                <span className="text-lg font-bold text-gray-900">
                  S/ {calcularTotal().toFixed(2)}
                </span>
              </div>
            </div>
          </div>
        )}
      </div>
      
      {/* Advertencias de stock */}
      {items.some(item => item.stock !== null && item.cantidad > item.stock) && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
          <p className="text-sm text-yellow-800">
            ⚠️ Algunos productos exceden el stock disponible
          </p>
        </div>
      )}
    </div>
  );
}
