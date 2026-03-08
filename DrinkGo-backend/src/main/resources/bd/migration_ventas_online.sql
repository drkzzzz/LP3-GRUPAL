-- Hace que ventas.usuario_id sea nullable.
-- Los pedidos de la tienda online no tienen un usuario administrador,
-- por lo que la venta generada automáticamente no tiene usuario asignado.
--
-- Ejecutar ANTES de reiniciar el backend con los cambios de VentasOnlineService.
-- Solo necesita correrse una vez.

ALTER TABLE ventas MODIFY COLUMN usuario_id BIGINT NULL;
