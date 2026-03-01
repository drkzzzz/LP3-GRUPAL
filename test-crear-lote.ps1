# ===============================================================
# TEST CRUD - CREAR LOTE Y VERIFICAR SINCRONIZACION AUTOMATICA
# ===============================================================

$baseUrl = "http://localhost:8080/restful"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " TEST: CREAR LOTE Y VERIFICAR SINCRONIZACION" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

# === PASO 1: Obtener stock ANTES de crear el lote ===
Write-Host "[1] Consultando stock ANTES de crear lote..." -ForegroundColor Yellow

$productoId = 1  # Ron Cartavio Black
$almacenId = 1   # Almacen Principal

$stocksAntes = Invoke-RestMethod -Uri "$baseUrl/stock-inventario" -Method Get
$stockAntes = $stocksAntes | Where-Object { 
    $_.producto.id -eq $productoId -and $_.almacen.id -eq $almacenId 
}

if ($stockAntes) {
    Write-Host "    Stock ANTES:"
    Write-Host "       Producto: $($stockAntes.producto.nombre)"
    Write-Host "       Almacen: $($stockAntes.almacen.nombre)"
    Write-Host "       Cantidad Actual: $($stockAntes.cantidadActual)"
    Write-Host "       Cantidad Disponible: $($stockAntes.cantidadDisponible)"
    Write-Host "       Cantidad Reservada: $($stockAntes.cantidadReservada)"
    Write-Host "       Costo Promedio: S/$($stockAntes.costoPromedio)"
    
    $cantidadActualAntes = $stockAntes.cantidadActual
    $costoPromedioAntes = $stockAntes.costoPromedio
} else {
    Write-Host "    [!] No existe stock previo para este producto/almacen" -ForegroundColor Yellow
    $cantidadActualAntes = 0
    $costoPromedioAntes = 0
}

# === PASO 2: Obtener número de lotes ANTES ===
$lotesAntes = Invoke-RestMethod -Uri "$baseUrl/lotes-inventario" -Method Get
$numLotesAntes = $lotesAntes.Count
Write-Host "`n    Numero de lotes ANTES: $numLotesAntes"

# === PASO 3: Obtener número de movimientos ANTES ===
$movimientosAntes = Invoke-RestMethod -Uri "$baseUrl/movimientos-inventario" -Method Get
$numMovimientosAntes = $movimientosAntes.Count
Write-Host "    Numero de movimientos ANTES: $numMovimientosAntes"

# === PASO 4: Crear nuevo lote ===
Write-Host "`n[2] Creando nuevo lote de inventario..." -ForegroundColor Yellow

$nuevoLote = @{
    numeroLote = "TEST-LOTE-001"
    fechaIngreso = (Get-Date).ToString("yyyy-MM-dd")
    fechaVencimiento = (Get-Date).AddMonths(12).ToString("yyyy-MM-dd")
    cantidadInicial = 50
    cantidadActual = 50
    costoUnitario = 30.00
    negocio = @{ id = 1 }
    producto = @{ id = $productoId }
    almacen = @{ id = $almacenId }
    estaActivo = $true
} | ConvertTo-Json

try {
    $loteCreado = Invoke-RestMethod -Uri "$baseUrl/lotes-inventario" -Method Post -Body $nuevoLote -ContentType "application/json"
    Write-Host "    OK Lote creado exitosamente!" -ForegroundColor Green
    Write-Host "       ID: $($loteCreado.id)"
    Write-Host "       Numero Lote: $($loteCreado.numeroLote)"
    Write-Host "       Cantidad: $($loteCreado.cantidadActual)"
    Write-Host "       Costo Unitario: S/$($loteCreado.costoUnitario)"
    
    $loteIdCreado = $loteCreado.id
    
} catch {
    Write-Host "    [ERROR] No se pudo crear el lote:" -ForegroundColor Red
    Write-Host "       $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

# === PASO 5: Esperar 2 segundos ===
Write-Host "`n[3] Esperando 2 segundos para sincronizacion..." -ForegroundColor Yellow
Start-Sleep -Seconds 2

# === PASO 6: Verificar stock DESPUES ===
Write-Host "`n[4] Consultando stock DESPUES de crear lote..." -ForegroundColor Yellow

$stocksDespues = Invoke-RestMethod -Uri "$baseUrl/stock-inventario" -Method Get
$stockDespues = $stocksDespues | Where-Object { 
    $_.producto.id -eq $productoId -and $_.almacen.id -eq $almacenId 
}

if ($stockDespues) {
    Write-Host "    Stock DESPUES:"
    Write-Host "       Cantidad Actual: $($stockDespues.cantidadActual)"
    Write-Host "       Cantidad Disponible: $($stockDespues.cantidadDisponible)"
    Write-Host "       Cantidad Reservada: $($stockDespues.cantidadReservada)"
    Write-Host "       Costo Promedio: S/$($stockDespues.costoPromedio)"
    
    $cantidadActualDespues = $stockDespues.cantidadActual
    $costoPromedioDespues = $stockDespues.costoPromedio
} else {
    Write-Host "    [!] ERROR: No se encontro stock despues de crear lote" -ForegroundColor Red
    $cantidadActualDespues = 0
    $costoPromedioDespues = 0
}

# === PASO 7: Verificar lotes DESPUES ===
$lotesDespues = Invoke-RestMethod -Uri "$baseUrl/lotes-inventario" -Method Get
$numLotesDespues = $lotesDespues.Count
Write-Host "`n    Numero de lotes DESPUES: $numLotesDespues"

# === PASO 8: Verificar movimientos DESPUES ===
$movimientosDespues = Invoke-RestMethod -Uri "$baseUrl/movimientos-inventario" -Method Get
$numMovimientosDespues = $movimientosDespues.Count
Write-Host "    Numero de movimientos DESPUES: $numMovimientosDespues"

# === PASO 9: ANALISIS DE RESULTADOS ===
Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " ANALISIS DE SINCRONIZACION AUTOMATICA" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

Write-Host "[A] Incremento de lotes:" -ForegroundColor Yellow
$incrementoLotes = $numLotesDespues - $numLotesAntes
Write-Host "    Antes: $numLotesAntes | Despues: $numLotesDespues | Incremento: +$incrementoLotes"
if ($incrementoLotes -eq 1) {
    Write-Host "    OK Lote registrado correctamente" -ForegroundColor Green
}

Write-Host "`n[B] Sincronizacion de stock_inventario:" -ForegroundColor Yellow
$incrementoStock = $cantidadActualDespues - $cantidadActualAntes
Write-Host "    Stock Antes: $cantidadActualAntes | Despues: $cantidadActualDespues | Incremento: +$incrementoStock"

if ($incrementoStock -eq 50) {
    Write-Host "    OK Stock se sincronizo AUTOMATICAMENTE (+50 unidades)" -ForegroundColor Green
} elseif ($incrementoStock -eq 0) {
    Write-Host "    [!] Stock NO se sincronizo (requiere actualizacion MANUAL)" -ForegroundColor Red
} else {
    Write-Host "    [?] Sincronizacion parcial o incorrecta" -ForegroundColor Yellow
}

Write-Host "`n[C] Calculo automatico de CPP (Costo Promedio):" -ForegroundColor Yellow
Write-Host "    CPP Antes: S/$costoPromedioAntes | Despues: S/$costoPromedioDespues"

# Calcular CPP esperado manualmente
if ($cantidadActualAntes -gt 0) {
    $valorStockAntes = $cantidadActualAntes * $costoPromedioAntes
    $valorLoteNuevo = 50 * 30.00
    $valorTotal = $valorStockAntes + $valorLoteNuevo
    $cantidadTotal = $cantidadActualAntes + 50
    $cppEsperado = [math]::Round($valorTotal / $cantidadTotal, 2)
    
    Write-Host "    CPP Esperado (calculado): S/$cppEsperado"
    
    if ([math]::Abs($costoPromedioDespues - $cppEsperado) -lt 0.02) {
        Write-Host "    OK CPP se actualizo AUTOMATICAMENTE" -ForegroundColor Green
    } else {
        Write-Host "    [!] CPP NO se actualizo correctamente" -ForegroundColor Red
    }
}

Write-Host "`n[D] Creacion automatica de movimiento_inventario:" -ForegroundColor Yellow
$incrementoMovimientos = $numMovimientosDespues - $numMovimientosAntes
Write-Host "    Movimientos Antes: $numMovimientosAntes | Despues: $numMovimientosDespues | Incremento: +$incrementoMovimientos"

if ($incrementoMovimientos -eq 1) {
    Write-Host "    OK Movimiento se registro AUTOMATICAMENTE" -ForegroundColor Green
    
    # Buscar el movimiento creado
    $nuevoMovimiento = $movimientosDespues | Where-Object { $_.lote.id -eq $loteIdCreado }
    if ($nuevoMovimiento) {
        Write-Host "    Detalles del movimiento:"
        Write-Host "       Tipo: $($nuevoMovimiento.tipoMovimiento)"
        Write-Host "       Cantidad: $($nuevoMovimiento.cantidad)"
        Write-Host "       Costo Unitario: S/$($nuevoMovimiento.costoUnitario)"
    }
} elseif ($incrementoMovimientos -eq 0) {
    Write-Host "    [!] Movimiento NO se registro (requiere creacion MANUAL)" -ForegroundColor Red
}

Write-Host "`n============================================================`n" -ForegroundColor Cyan
