# Ver estructura completa de un lote
$baseUrl = "http://localhost:8080/restful"

$lotes = Invoke-RestMethod -Uri "$baseUrl/lotes-inventario" -Method Get

$lote1 = $lotes[0]

Write-Host "Estructura completa del lote 1:" -ForegroundColor Cyan
$lote1 | ConvertTo-Json -Depth 5
