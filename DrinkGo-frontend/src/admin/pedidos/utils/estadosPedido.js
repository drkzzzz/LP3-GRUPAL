/**
 * Utilidades para gestión de estados de pedidos
 */

/**
 * Estados posibles de un pedido (según backend)
 * Sistema de 7 estados simplificado
 */
export const ESTADOS_PEDIDO = {
  PENDIENTE: 'pendiente',
  CONFIRMADO: 'confirmado',
  PREPARANDO: 'preparando',
  LISTO: 'listo',
  EN_CAMINO: 'en_camino',
  ENTREGADO: 'entregado',
  CANCELADO: 'cancelado',
};

/**
 * Tipos de pedido
 */
export const TIPOS_PEDIDO = {
  DELIVERY: 'delivery',
  RECOJO_TIENDA: 'recojo_tienda',
  CONSUMO_LOCAL: 'consumo_local',
};

/**
 * Configuración de estados con metadata
 * Sistema de 7 estados simplificado
 */
export const CONFIG_ESTADOS = {
  [ESTADOS_PEDIDO.PENDIENTE]: {
    label: 'Pendiente',
    color: 'bg-yellow-100 text-yellow-800 border-yellow-200',
    icon: '⏳',
    descripcion: 'Pedido recibido, pendiente de confirmación',
  },
  [ESTADOS_PEDIDO.CONFIRMADO]: {
    label: 'Confirmado',
    color: 'bg-blue-100 text-blue-800 border-blue-200',
    icon: '✓',
    descripcion: 'Pedido confirmado, listo para preparar',
  },
  [ESTADOS_PEDIDO.PREPARANDO]: {
    label: 'Preparando',
    color: 'bg-purple-100 text-purple-800 border-purple-200',
    icon: '👨‍🍳',
    descripcion: 'Pedido en proceso de preparación',
  },
  [ESTADOS_PEDIDO.LISTO]: {
    label: 'Listo',
    color: 'bg-green-100 text-green-800 border-green-200',
    icon: '📦',
    descripcion: 'Pedido listo para entrega/recojo',
  },
  [ESTADOS_PEDIDO.EN_CAMINO]: {
    label: 'En Camino',
    color: 'bg-indigo-100 text-indigo-800 border-indigo-200',
    icon: '🚚',
    descripcion: 'Pedido en ruta de entrega o esperando recojo',
  },
  [ESTADOS_PEDIDO.ENTREGADO]: {
    label: 'Entregado',
    color: 'bg-green-200 text-green-900 border-green-300',
    icon: '✅',
    descripcion: 'Pedido entregado/recogido exitosamente',
  },
  [ESTADOS_PEDIDO.CANCELADO]: {
    label: 'Cancelado',
    color: 'bg-red-100 text-red-800 border-red-200',
    icon: '❌',
    descripcion: 'Pedido cancelado',
  },
};

/**
 * Transiciones válidas de estado
 * Define qué estados pueden cambiar a qué otros estados
 * Flujo simplificado de 7 estados
 */
export const TRANSICIONES_VALIDAS = {
  [ESTADOS_PEDIDO.PENDIENTE]: [
    ESTADOS_PEDIDO.CONFIRMADO,
    ESTADOS_PEDIDO.CANCELADO,
  ],
  [ESTADOS_PEDIDO.CONFIRMADO]: [
    ESTADOS_PEDIDO.PREPARANDO,
    ESTADOS_PEDIDO.CANCELADO,
  ],
  [ESTADOS_PEDIDO.PREPARANDO]: [
    ESTADOS_PEDIDO.LISTO,
    ESTADOS_PEDIDO.CANCELADO,
  ],
  [ESTADOS_PEDIDO.LISTO]: [
    ESTADOS_PEDIDO.EN_CAMINO,
    ESTADOS_PEDIDO.CANCELADO,
  ],
  [ESTADOS_PEDIDO.EN_CAMINO]: [
    ESTADOS_PEDIDO.ENTREGADO,
    ESTADOS_PEDIDO.CANCELADO,
  ],
  [ESTADOS_PEDIDO.ENTREGADO]: [], // Estado final
  [ESTADOS_PEDIDO.CANCELADO]: [], // Estado final
};

/**
 * Validar si una transición de estado es válida
 */
export const esTransicionValida = (estadoActual, nuevoEstado) => {
  const transiciones = TRANSICIONES_VALIDAS[estadoActual] || [];
  return transiciones.includes(nuevoEstado);
};

/**
 * Obtener estados siguientes válidos
 * Para tipo recojo_tienda, el estado EN_CAMINO significa "esperando recojo"
 * Para delivery, EN_CAMINO significa "en ruta de entrega"
 */
export const getEstadosSiguientes = (estadoActual, tipoPedido) => {
  const transiciones = TRANSICIONES_VALIDAS[estadoActual] || [];
  
  // El flujo simplificado de 7 estados es el mismo para todos los tipos
  // La diferencia solo está en la interpretación del estado EN_CAMINO:
  // - delivery: pedido en ruta
  // - recojo_tienda: pedido listo para recoger
  // - consumo_local: preparando para consumir en local
  
  return transiciones;
};

/**
 * Obtener configuración de un estado
 */
export const getConfigEstado = (estado) => {
  return CONFIG_ESTADOS[estado] || {
    label: estado,
    color: 'bg-gray-100 text-gray-800 border-gray-200',
    icon: '❓',
    descripcion: 'Estado desconocido',
  };
};

/**
 * Formatear fecha de pedido
 */
export const formatearFechaPedido = (fecha) => {
  if (!fecha) return '-';
  
  const date = new Date(fecha);
  const ahora = new Date();
  const diffMs = ahora - date;
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMins / 60);
  const diffDays = Math.floor(diffHours / 24);
  
  if (diffMins < 1) return 'Hace un momento';
  if (diffMins < 60) return `Hace ${diffMins} min`;
  if (diffHours < 24) return `Hace ${diffHours}h`;
  if (diffDays === 1) return 'Ayer';
  if (diffDays < 7) return `Hace ${diffDays} días`;
  
  return date.toLocaleDateString('es-PE', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  });
};

/**
 * Calcular urgencia del pedido
 */
export const calcularUrgencia = (fechaPedido, estadoActual) => {
  // Estados finales no tienen urgencia
  if ([
    ESTADOS_PEDIDO.ENTREGADO, 
    ESTADOS_PEDIDO.CANCELADO,
  ].includes(estadoActual)) {
    return 'normal';
  }
  
  const ahora = new Date();
  const fecha = new Date(fechaPedido);
  const diffMinutos = Math.floor((ahora - fecha) / 60000);
  
  // Más de 60 minutos sin confirmar = urgente
  if (estadoActual === ESTADOS_PEDIDO.PENDIENTE && diffMinutos > 60) {
    return 'urgente';
  }
  
  // Más de 45 minutos en preparación = atención
  if (estadoActual === ESTADOS_PEDIDO.PREPARANDO && diffMinutos > 45) {
    return 'atencion';
  }
  
  // Más de 30 minutos en camino = atención
  if (estadoActual === ESTADOS_PEDIDO.EN_CAMINO && diffMinutos > 30) {
    return 'atencion';
  }
  
  return 'normal';
};

/**
 * Obtener clase CSS para urgencia
 */
export const getClaseUrgencia = (urgencia) => {
  switch (urgencia) {
    case 'urgente':
      return 'bg-red-50 border-l-4 border-red-500';
    case 'atencion':
      return 'bg-orange-50 border-l-4 border-orange-500';
    default:
      return '';
  }
};
