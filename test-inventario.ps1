# ===============================================================
# SCRIPT DE TESTING - SISTEMA DE INVENTARIO DRINKGO
# Fecha: 28 de febrero 2026
# ===============================================================

$baseUrl = "http://localhost:8080/restful"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " TESTING SISTEMA DE INVENTARIO - ESTADO ACTUAL" -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

# === TEST 1: LOTES DE INVENTARIO ===
Write-Host "[1] Obteniendo lotes de inventario..." -ForegroundColor Yellow
$lotes = Invoke-RestMethod -Uri "$baseUrl/lotes-inventario" -Method Get
Write-Host "    OK Total Lotes: $($lotes.Count)" -ForegroundColor Green

if ($lotes.Count -gt 0) {
    Write-Host "    Ejemplos de lotes:"
    $lotes | Select-Object -First 3 | ForEach-Object {
        Write-Host "       - $($_.numeroLote): $($_.producto.nombre) | Cantidad: $($_.cantidadActual)/$($_.cantidadInicial) | Costo: S/$($_.costoUnitario)"
    }
}

# === TEST 2: STOCK CONSOLIDADO ===
Write-Host "`n[2] Obteniendo stock consolidado..." -ForegroundColor Yellow
$stocks = Invoke-RestMethod -Uri "$baseUrl/stock-inventario" -Method Get
Write-Host "    OK Total Stocks: $($stocks.Count)" -ForegroundColor Green

if ($stocks.Count -gt 0) {
    Write-Host "    Ejemplos de stock:"
    $stocks | Select-Object -First 3 | ForEach-Object {
        Write-Host "       - $($_.producto.nombre) en $($_.almacen.nombre)"
        Write-Host "         Actual: $($_.cantidadActual) | Disponible: $($_.cantidadDisponible) | Reservada: $($_.cantidadReservada) | CPP: S/$($_.costoPromedio)"
    }
}

# === TEST 3: MOVIMIENTOS ===
Write-Host "`n[3] Obteniendo movimientos de inventario..." -ForegroundColor Yellow
$movimientos = Invoke-RestMethod -Uri "$baseUrl/movimientos-inventario" -Method Get
Write-Host "    OK Total Movimientos: $($movimientos.Count)" -ForegroundColor Green

if ($movimientos.Count -gt 0) {
    Write-Host "    Ultimos 5 movimientos:"
    $movimientos | Select-Object -First 5 | ForEach-Object {
        $fecha = ([DateTime]$_.fechaMovimiento).ToString("dd/MM/yyyy HH:mm")
        Write-Host "       - [$($_.tipoMovimiento)] $($_.producto.nombre): $($_.cantidad) unidades - $fecha"
    }
    
    # Analisis de tipos de movimiento
    $tiposMovimiento = $movimientos | Group-Object tipoMovimiento | Select-Object Name, Count
    Write-Host "`n    Distribucion de movimientos:"
    $tiposMovimiento | ForEach-Object {
        Write-Host "       - $($_.Name): $($_.Count)"
    }
}

# === TEST 4: ANALISIS DE CONSISTENCIA ===
Write-Host "`n[4] Analisis de consistencia..." -ForegroundColor Yellow

# Verificar cantidad reservada
$stocksConReserva = $stocks | Where-Object { $_.cantidadReservada -gt 0 }
if ($stocksConReserva.Count -eq 0) {
    Write-Host "    [!] Cantidad reservada SIEMPRE es 0 (no se usa)" -ForegroundColor Red
} else {
    Write-Host "    OK Stocks con reserva: $($stocksConReserva.Count)" -ForegroundColor Green
}

# Verificar cantidad disponible
$stocksSinDisponible = $stocks | Where-Object { $_.cantidadDisponible -eq 0 -and $_.cantidadActual -gt 0 }
Write-Host "    Stocks con cantidad actual > 0 pero disponible = 0: $($stocksSinDisponible.Count)"

# Verificar costo promedio
$stocksSinCPP = $stocks | Where-Object { $_.costoPromedio -eq 0 -and $_.cantidadActual -gt 0 }
if ($stocksSinCPP.Count -gt 0) {
    Write-Host "    [!] Stocks con costo promedio = 0: $($stocksSinCPP.Count)" -ForegroundColor Red
}

# === TEST 5: OBTENER DATOS PARA PRUEBA DE CREACION ===
Write-Host "`n[5] Obteniendo datos para testing..." -ForegroundColor Yellow

$negocios = Invoke-RestMethod -Uri "$baseUrl/negocios" -Method Get
$productos = Invoke-RestMethod -Uri "$baseUrl/productos" -Method Get  
$almacenes = Invoke-RestMethod -Uri "$baseUrl/almacenes" -Method Get
$usuarios = Invoke-RestMethod -Uri "$baseUrl/usuarios" -Method Get

$negocio1 = $negocios[0]
$producto1 = $productos[0]
$almacen1 = $almacenes[0]
$usuario1 = $usuarios[0]

Write-Host "    OK Negocio: $($negocio1.nombreComercial) (ID: $($negocio1.id))"
Write-Host "    OK Producto: $($producto1.nombre) - $($producto1.sku) (ID: $($producto1.id))"
Write-Host "    OK Almacen: $($almacen1.nombre) (ID: $($almacen1.id))"
Write-Host "    OK Usuario: $($usuario1.nombre) (ID: $($usuario1.id))"

# Guardar IDs para siguiente test
$Global:testNegocioId = $negocio1.id
$Global:testProductoId = $producto1.id
$Global:testAlmacenId = $almacen1.id
$Global:testUsuarioId = $usuario1.id

# Verificar stock actual de este producto en este almacen
$stockExistente = $stocks | Where-Object { 
    $_.producto.id -eq $producto1.id -and $_.almacen.id -eq $almacen1.id 
}

if ($stockExistente) {
    Write-Host "`n    Stock existente del producto en almacen:"
    Write-Host "       Cantidad Actual: $($stockExistente.cantidadActual)"
    Write-Host "       Cantidad Disponible: $($stockExistente.cantidadDisponible)"
    Write-Host "       Costo Promedio: S/$($stockExistente.costoPromedio)"
    $Global:stockInicialAntes = $stockExistente.cantidadActual
} else {
    Write-Host "`n    [i] No hay stock existente para este producto en este almacen" -ForegroundColor Cyan
    $Global:stockInicialAntes = 0
}

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host " RESUMEN DEL ESTADO ACTUAL" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

Write-Host "`nOK Lotes: $($lotes.Count)"
Write-Host "OK Stocks: $($stocks.Count)"
Write-Host "OK Movimientos: $($movimientos.Count)"
Write-Host "[!] Problema detectado: Cantidad reservada NO se usa (siempre 0)" -ForegroundColor Red
Write-Host "[>] Proximo: Probar sincronizacion automatica al crear lote`n" -ForegroundColor Yellow

