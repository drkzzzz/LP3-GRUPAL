package DrinkGo.DrinkGo_backend.service;

import DrinkGo.DrinkGo_backend.entity.*;
import DrinkGo.DrinkGo_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio transaccional profesional para gestión de inventario
 * Implementa lógica FIFO, cálculo automático de CPP, reservas de stock,
 * y sincronización automática entre lotes, stock y movimientos.
 * 
 * @author DrinkGo Development Team
 * @version 1.0
 */
@Service
public class InventarioTransaccionalService {

    @Autowired
    private LotesInventarioRepository lotesRepository;

    @Autowired
    private StockInventarioRepository stockRepository;

    @Autowired
    private MovimientosInventarioRepository movimientosRepository;

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private AlmacenesRepository almacenesRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    /**
     * Registra una entrada de inventario (compra, devolución de cliente, etc.)
     * Crea el lote, actualiza el stock con CPP automático y registra el movimiento.
     * 
     * @param negocioId ID del negocio
     * @param productoId ID del producto
     * @param almacenId ID del almacén
     * @param numeroLote Número del lote
     * @param cantidad Cantidad a ingresar
     * @param costoUnitario Costo unitario del producto
     * @param fechaVencimiento Fecha de vencimiento (opcional)
     * @param usuarioId ID del usuario que registra
     * @param motivoMovimiento Motivo de la entrada
     * @param referenciaDocumento Referencia del documento (factura, guía, etc.)
     * @return El lote creado
     * @throws IllegalArgumentException Si los datos son inválidos
     */
    @Transactional
    public LotesInventario registrarEntrada(
            Long negocioId,
            Long productoId,
            Long almacenId,
            String numeroLote,
            BigDecimal cantidad,
            BigDecimal costoUnitario,
            LocalDate fechaVencimiento,
            Long usuarioId,
            String motivoMovimiento,
            String referenciaDocumento) {

        // 1. VALIDACIONES
        validarDatosObligatorios(productoId, almacenId, cantidad, costoUnitario, usuarioId);
        
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        if (costoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El costo unitario debe ser mayor a cero");
        }

        // 2. OBTENER ENTIDADES
        Negocios negocio = obtenerNegocio(negocioId);
        Productos producto = obtenerProducto(productoId);
        Almacenes almacen = obtenerAlmacen(almacenId);
        Usuarios usuario = obtenerUsuario(usuarioId);

        // Validar que el producto esté activo
        if (!producto.getEstaActivo()) {
            throw new IllegalArgumentException("El producto no está activo");
        }

        // 3. CREAR LOTE
        LotesInventario lote = new LotesInventario();
        lote.setNegocio(negocio);
        lote.setProducto(producto);
        lote.setAlmacen(almacen);
        lote.setNumeroLote(numeroLote != null ? numeroLote : generarNumeroLote());
        lote.setFechaIngreso(LocalDate.now());
        lote.setFechaVencimiento(fechaVencimiento);
        lote.setCantidadInicial(cantidad);
        lote.setCantidadActual(cantidad);
        lote.setCostoUnitario(costoUnitario);
        lote.setEstaActivo(true);

        lote = lotesRepository.save(lote);

        // 4. ACTUALIZAR O CREAR STOCK CON CÁLCULO DE CPP
        actualizarStockConCPP(producto, almacen, cantidad, costoUnitario, negocio);

        // 5. REGISTRAR MOVIMIENTO
        registrarMovimiento(
            negocio,
            producto,
            null,  // Sin almacén origen (es una entrada)
            almacen,  // Almacén destino
            lote,
            MovimientosInventario.TipoMovimiento.entrada,
            cantidad,
            costoUnitario,
            usuario,
            motivoMovimiento != null ? motivoMovimiento : "Entrada de inventario - Lote " + lote.getNumeroLote(),
            referenciaDocumento
        );

        return lote;
    }

    /**
     * Registra una salida de inventario aplicando FIFO automáticamente
     * Descuenta de los lotes más antiguos primero, actualiza stock y registra movimientos.
     * 
     * @param productoId ID del producto
     * @param almacenId ID del almacén
     * @param cantidad Cantidad a sacar
     * @param usuarioId ID del usuario que registra
     * @param motivoMovimiento Motivo de la salida
     * @param referenciaDocumento Referencia del documento
     * @throws IllegalArgumentException Si no hay stock suficiente o datos inválidos
     */
    @Transactional
    public void registrarSalida(
            Long negocioId,
            Long productoId,
            Long almacenId,
            BigDecimal cantidad,
            Long usuarioId,
            String motivoMovimiento,
            String referenciaDocumento) {

        // 1. VALIDACIONES
        validarDatosObligatorios(productoId, almacenId, cantidad, BigDecimal.ONE, usuarioId);
        
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        // 2. OBTENER ENTIDADES
        Negocios negocio = obtenerNegocio(negocioId);
        Productos producto = obtenerProducto(productoId);
        Almacenes almacen = obtenerAlmacen(almacenId);
        Usuarios usuario = obtenerUsuario(usuarioId);

        // 3. VERIFICAR STOCK DISPONIBLE
        StockInventario stock = stockRepository.findByProductoAndAlmacen(producto, almacen)
            .orElseThrow(() -> new IllegalArgumentException("No existe stock para este producto en el almacén"));

        if (stock.getCantidadDisponible().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Disponible: %s, Solicitado: %s", 
                    stock.getCantidadDisponible(), cantidad)
            );
        }

        // 4. APLICAR FIFO: Obtener lotes ordenados por fecha de ingreso
        List<LotesInventario> lotesFIFO = lotesRepository.findLotesDisponiblesFIFO(producto, almacen);

        if (lotesFIFO.isEmpty()) {
            throw new IllegalArgumentException("No hay lotes disponibles para realizar la salida");
        }

        // 5. DESCONTAR DE LOTES APLICANDO FIFO
        BigDecimal cantidadPendiente = cantidad;
        BigDecimal costoTotalSalida = BigDecimal.ZERO;

        for (LotesInventario lote : lotesFIFO) {
            if (cantidadPendiente.compareTo(BigDecimal.ZERO) == 0) {
                break;  // Ya se descontó toda la cantidad
            }

            BigDecimal cantidadDescontar = cantidadPendiente.min(lote.getCantidadActual());
            
            // Descontar del lote
            lote.setCantidadActual(lote.getCantidadActual().subtract(cantidadDescontar));
            lotesRepository.save(lote);

            // Calcular costo de esta porción
            BigDecimal costoPorcion = cantidadDescontar.multiply(lote.getCostoUnitario());
            costoTotalSalida = costoTotalSalida.add(costoPorcion);

            // Registrar movimiento por cada lote afectado
            registrarMovimiento(
                negocio,
                producto,
                almacen,  // Almacén origen
                null,  // Sin almacén destino (es una salida)
                lote,
                MovimientosInventario.TipoMovimiento.salida,
                cantidadDescontar,
                lote.getCostoUnitario(),
                usuario,
                motivoMovimiento != null ? motivoMovimiento : "Salida de inventario - Lote " + lote.getNumeroLote(),
                referenciaDocumento
            );

            cantidadPendiente = cantidadPendiente.subtract(cantidadDescontar);
        }

        // 6. ACTUALIZAR STOCK
        stock.setCantidadActual(stock.getCantidadActual().subtract(cantidad));
        stock.setCantidadDisponible(stock.getCantidadActual().subtract(stock.getCantidadReservada()));
        stockRepository.save(stock);
    }

    /**
     * Registra una transferencia entre almacenes aplicando FIFO en el origen
     * 
     * @param productoId ID del producto
     * @param almacenOrigenId ID del almacén origen
     * @param almacenDestinoId ID del almacén destino
     * @param cantidad Cantidad a transferir
     * @param usuarioId ID del usuario que registra
     * @param motivoMovimiento Motivo de la transferencia
     * @param referenciaDocumento Referencia del documento
     */
    @Transactional
    public void registrarTransferencia(
            Long negocioId,
            Long productoId,
            Long almacenOrigenId,
            Long almacenDestinoId,
            BigDecimal cantidad,
            Long usuarioId,
            String motivoMovimiento,
            String referenciaDocumento) {

        if (almacenOrigenId.equals(almacenDestinoId)) {
            throw new IllegalArgumentException("El almacén origen y destino no pueden ser el mismo");
        }

        // 1. Registrar salida del almacén origen
        registrarSalida(negocioId, productoId, almacenOrigenId, cantidad, usuarioId, 
            "Transferencia a otro almacén: " + motivoMovimiento, referenciaDocumento);

        // 2. Obtener entidades para la entrada en destino
        Negocios negocio = obtenerNegocio(negocioId);
        Productos producto = obtenerProducto(productoId);
        Almacenes almacenDestino = obtenerAlmacen(almacenDestinoId);

        // Calcular CPP del origen para usar en el destino
        Almacenes almacenOrigen = obtenerAlmacen(almacenOrigenId);
        StockInventario stockOrigen = stockRepository.findByProductoAndAlmacen(producto, almacenOrigen)
            .orElseThrow(() -> new IllegalArgumentException("No existe stock en el almacén origen"));

        // 3. Registrar entrada en almacén destino con el CPP del origen
        registrarEntrada(
            negocioId,
            productoId,
            almacenDestinoId,
            "TRANSFER-" + System.currentTimeMillis(),
            cantidad,
            stockOrigen.getCostoPromedio(),
            null,  // Sin fecha de vencimiento para transferencias
            usuarioId,
            "Transferencia desde almacén origen: " + motivoMovimiento,
            referenciaDocumento
        );
    }

    /**
     * Registra un ajuste de inventario (positivo o negativo)
     * Usado para correcciones, mermas, daños, etc.
     * 
     * @param productoId ID del producto
     * @param almacenId ID del almacén
     * @param cantidad Cantidad del ajuste (positiva o negativa)
     * @param esPositivo true para ajuste positivo, false para negativo
     * @param usuarioId ID del usuario que registra
     * @param motivoMovimiento Motivo del ajuste (obligatorio)
     * @param referenciaDocumento Referencia del documento
     */
    @Transactional
    public void registrarAjuste(
            Long negocioId,
            Long productoId,
            Long almacenId,
            BigDecimal cantidad,
            boolean esPositivo,
            Long usuarioId,
            String motivoMovimiento,
            String referenciaDocumento) {

        if (motivoMovimiento == null || motivoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo del ajuste es obligatorio");
        }

        Negocios negocio = obtenerNegocio(negocioId);
        Productos producto = obtenerProducto(productoId);
        Almacenes almacen = obtenerAlmacen(almacenId);
        Usuarios usuario = obtenerUsuario(usuarioId);

        StockInventario stock = stockRepository.findByProductoAndAlmacen(producto, almacen)
            .orElseThrow(() -> new IllegalArgumentException("No existe stock para este producto en el almacén"));

        if (esPositivo) {
            // Ajuste positivo: aumenta el stock
            stock.setCantidadActual(stock.getCantidadActual().add(cantidad));
            
            registrarMovimiento(
                negocio, producto, null, almacen, null,
                MovimientosInventario.TipoMovimiento.ajuste_positivo,
                cantidad, stock.getCostoPromedio(), usuario,
                motivoMovimiento, referenciaDocumento
            );
        } else {
            // Ajuste negativo: reduce el stock (merma, daño, etc.)
            if (stock.getCantidadDisponible().compareTo(cantidad) < 0) {
                throw new IllegalArgumentException("No hay stock suficiente para el ajuste negativo");
            }

            stock.setCantidadActual(stock.getCantidadActual().subtract(cantidad));
            
            registrarMovimiento(
                negocio, producto, almacen, null, null,
                MovimientosInventario.TipoMovimiento.ajuste_negativo,
                cantidad, stock.getCostoPromedio(), usuario,
                motivoMovimiento, referenciaDocumento
            );
        }

        stock.setCantidadDisponible(stock.getCantidadActual().subtract(stock.getCantidadReservada()));
        stockRepository.save(stock);
    }

    /**
     * Reserva stock para un pedido u orden
     * Reduce la cantidad disponible pero no la cantidad actual
     * 
     * @param productoId ID del producto
     * @param almacenId ID del almacén
     * @param cantidad Cantidad a reservar
     * @throws IllegalArgumentException Si no hay stock disponible suficiente
     */
    @Transactional
    public void reservarStock(Long productoId, Long almacenId, BigDecimal cantidad) {
        Productos producto = obtenerProducto(productoId);
        Almacenes almacen = obtenerAlmacen(almacenId);

        StockInventario stock = stockRepository.findByProductoAndAlmacen(producto, almacen)
            .orElseThrow(() -> new IllegalArgumentException("No existe stock para este producto en el almacén"));

        if (stock.getCantidadDisponible().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException(
                String.format("Stock disponible insuficiente para reservar. Disponible: %s, Solicitado: %s",
                    stock.getCantidadDisponible(), cantidad)
            );
        }

        stock.setCantidadReservada(stock.getCantidadReservada().add(cantidad));
        stock.setCantidadDisponible(stock.getCantidadActual().subtract(stock.getCantidadReservada()));
        stockRepository.save(stock);
    }

    /**
     * Libera stock previamente reservado
     * Aumenta la cantidad disponible sin modificar la cantidad actual
     * 
     * @param productoId ID del producto
     * @param almacenId ID del almacén
     * @param cantidad Cantidad a liberar
     */
    @Transactional
    public void liberarReserva(Long productoId, Long almacenId, BigDecimal cantidad) {
        Productos producto = obtenerProducto(productoId);
        Almacenes almacen = obtenerAlmacen(almacenId);

        StockInventario stock = stockRepository.findByProductoAndAlmacen(producto, almacen)
            .orElseThrow(() -> new IllegalArgumentException("No existe stock para este producto en el almacén"));

        if (stock.getCantidadReservada().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException("No hay suficiente cantidad reservada para liberar");
        }

        stock.setCantidadReservada(stock.getCantidadReservada().subtract(cantidad));
        stock.setCantidadDisponible(stock.getCantidadActual().subtract(stock.getCantidadReservada()));
        stockRepository.save(stock);
    }

    /**
     * Confirma una reserva registrando la salida física del stock
     * Se usa cuando un pedido reservado se despacha/entrega
     * 
     * @param productoId ID del producto
     * @param almacenId ID del almacén
     * @param cantidad Cantidad a confirmar
     * @param usuarioId ID del usuario
     * @param motivoMovimiento Motivo de la salida
     * @param referenciaDocumento Referencia del documento
     */
    @Transactional
    public void confirmarReservaYSalida(
            Long negocioId,
            Long productoId,
            Long almacenId,
            BigDecimal cantidad,
            Long usuarioId,
            String motivoMovimiento,
            String referenciaDocumento) {
        
        // 1. Verificar que haya reserva suficiente
        Productos producto = obtenerProducto(productoId);
        Almacenes almacen = obtenerAlmacen(almacenId);
        
        StockInventario stock = stockRepository.findByProductoAndAlmacen(producto, almacen)
            .orElseThrow(() -> new IllegalArgumentException("No existe stock para este producto en el almacén"));

        if (stock.getCantidadReservada().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException("No hay suficiente cantidad reservada");
        }

        // 2. Liberar la reserva (aumenta disponible, reduce reservada)
        stock.setCantidadReservada(stock.getCantidadReservada().subtract(cantidad));
        stock.setCantidadDisponible(stock.getCantidadActual().subtract(stock.getCantidadReservada()));
        stockRepository.save(stock);

        // 3. Registrar la salida física (descuenta de lotes con FIFO y reduce cantidad actual)
        registrarSalida(negocioId, productoId, almacenId, cantidad, usuarioId, motivoMovimiento, referenciaDocumento);
    }

    // ==================== MÉTODOS PRIVADOS DE SOPORTE ====================

    /**
     * Actualiza el stock consolidado calculando automáticamente el CPP (Costo Promedio Ponderado)
     * Si no existe el stock, lo crea.
     */
    private void actualizarStockConCPP(Productos producto, Almacenes almacen, BigDecimal cantidadEntrada, 
                                       BigDecimal costoUnitarioEntrada, Negocios negocio) {
        Optional<StockInventario> stockOpt = stockRepository.findByProductoAndAlmacen(producto, almacen);

        StockInventario stock;
        if (stockOpt.isPresent()) {
            stock = stockOpt.get();

            // CALCULAR CPP: ((Stock Anterior * CPP Anterior) + (Cantidad Nueva * Costo Nuevo)) / (Stock Anterior + Cantidad Nueva)
            BigDecimal valorStockAnterior = stock.getCantidadActual().multiply(stock.getCostoPromedio());
            BigDecimal valorEntradaNueva = cantidadEntrada.multiply(costoUnitarioEntrada);
            BigDecimal valorTotal = valorStockAnterior.add(valorEntradaNueva);
            BigDecimal cantidadTotal = stock.getCantidadActual().add(cantidadEntrada);

            BigDecimal nuevoCPP = cantidadTotal.compareTo(BigDecimal.ZERO) > 0
                ? valorTotal.divide(cantidadTotal, 2, RoundingMode.HALF_UP)
                : costoUnitarioEntrada;

            stock.setCostoPromedio(nuevoCPP);
            stock.setCantidadActual(cantidadTotal);
        } else {
            // Crear nuevo registro de stock
            stock = new StockInventario();
            stock.setNegocio(negocio);
            stock.setProducto(producto);
            stock.setAlmacen(almacen);
            stock.setCantidadActual(cantidadEntrada);
            stock.setCantidadReservada(BigDecimal.ZERO);
            stock.setCostoPromedio(costoUnitarioEntrada);
            stock.setEstaActivo(true);
        }

        stock.setCantidadDisponible(stock.getCantidadActual().subtract(stock.getCantidadReservada()));
        stockRepository.save(stock);
    }

    /**
     * Registra un movimiento de inventario (entrada, salida, transferencia, ajuste)
     */
    private void registrarMovimiento(
            Negocios negocio,
            Productos producto,
            Almacenes almacenOrigen,
            Almacenes almacenDestino,
            LotesInventario lote,
            MovimientosInventario.TipoMovimiento tipoMovimiento,
            BigDecimal cantidad,
            BigDecimal costoUnitario,
            Usuarios usuario,
            String motivoMovimiento,
            String referenciaDocumento) {

        MovimientosInventario movimiento = new MovimientosInventario();
        movimiento.setNegocio(negocio);
        movimiento.setProducto(producto);
        movimiento.setAlmacenOrigen(almacenOrigen);
        movimiento.setAlmacenDestino(almacenDestino);
        movimiento.setLote(lote);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setCantidad(cantidad);
        movimiento.setCostoUnitario(costoUnitario);
        movimiento.setMontoTotal(cantidad.multiply(costoUnitario));
        movimiento.setUsuario(usuario);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setMotivoMovimiento(motivoMovimiento);
        movimiento.setReferenciaDocumento(referenciaDocumento);
        movimiento.setEstaActivo(true);

        movimientosRepository.save(movimiento);
    }

    /**
     * Genera un número de lote automático único
     */
    private String generarNumeroLote() {
        return "LOTE-" + System.currentTimeMillis();
    }

    /**
     * Valida que los datos obligatorios no sean nulos
     */
    private void validarDatosObligatorios(Long productoId, Long almacenId, BigDecimal cantidad, 
                                         BigDecimal costoUnitario, Long usuarioId) {
        if (productoId == null) throw new IllegalArgumentException("El ID del producto es obligatorio");
        if (almacenId == null) throw new IllegalArgumentException("El ID del almacén es obligatorio");
        if (cantidad == null) throw new IllegalArgumentException("La cantidad es obligatoria");
        if (costoUnitario == null) throw new IllegalArgumentException("El costo unitario es obligatorio");
        if (usuarioId == null) throw new IllegalArgumentException("El ID del usuario es obligatorio");
    }

    /**
     * Obtiene un negocio por ID con validación
     */
    private Negocios obtenerNegocio(Long negocioId) {
        if (negocioId == null) {
            throw new IllegalArgumentException("El ID del negocio es obligatorio");
        }
        return new Negocios() {{ setId(negocioId); }};
    }

    /**
     * Obtiene un producto por ID con validación
     */
    private Productos obtenerProducto(Long productoId) {
        return productosRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));
    }

    /**
     * Obtiene un almacén por ID con validación
     */
    private Almacenes obtenerAlmacen(Long almacenId) {
        return almacenesRepository.findById(almacenId)
            .orElseThrow(() -> new IllegalArgumentException("Almacén no encontrado con ID: " + almacenId));
    }

    /**
     * Obtiene un usuario por ID con validación
     */
    private Usuarios obtenerUsuario(Long usuarioId) {
        return usuariosRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
    }

    /**
     * Obtiene productos con stock bajo el mínimo especificado
     */
    public List<StockInventario> obtenerStockBajo(BigDecimal cantidadMinima) {
        return stockRepository.findStockBajo(cantidadMinima);
    }

    /**
     * Obtiene lotes próximos a vencer en un almacén
     */
    public List<LotesInventario> obtenerLotesProximosAVencer(Long almacenId, int diasAntelacion) {
        Almacenes almacen = obtenerAlmacen(almacenId);
        LocalDate fechaLimite = LocalDate.now().plusDays(diasAntelacion);
        return lotesRepository.findLotesProximosAVencer(almacen, fechaLimite);
    }
}
