-- =====================================================================
-- Sadu Pedidos | 03 - Consultas de referencia por pantalla
-- Son la base de los DAO. En Java se usan con PreparedStatement:
-- cada valor que viene del usuario se reemplaza por ? (aqui van ejemplos fijos).
-- =====================================================================
USE sadu_db;

-- P2 Login (Dev 1): buscar la cuenta activa por correo; Java compara el hash.
SELECT id_usuario, nombre_completo, correo, hash_clave, rol
FROM usuarios
WHERE correo = 'ana.martinez@demo.local' AND activo = TRUE;

-- P1 Inicio (Dev 1): platillos destacados.
SELECT id_producto, nombre, precio, imagen_url
FROM productos
WHERE destacado = TRUE AND disponible = TRUE
ORDER BY nombre;

-- P3 Menu (Dev 2): categorias activas para los botones.
SELECT id_categoria, nombre FROM categorias WHERE activa = TRUE ORDER BY orden;

-- P3 Menu (Dev 2): productos por categoria y busqueda por nombre.
SELECT p.id_producto, p.nombre, p.descripcion, p.precio, p.imagen_url, c.nombre AS categoria
FROM productos p
JOIN categorias c ON c.id_categoria = p.id_categoria
WHERE p.disponible = TRUE AND c.activa = TRUE
  AND p.id_categoria = 2                 -- ?  (filtro opcional)
  AND p.nombre LIKE CONCAT('%', 'pollo', '%')  -- ?  (busqueda opcional)
ORDER BY c.orden, p.nombre;

-- P4 Nuevo pedido (Dev 3): TRANSACCION. En Java: setAutoCommit(false) ... commit() / rollback().
START TRANSACTION;
  -- 1) validar y leer precios reales desde la BD (nunca el precio que manda el formulario)
  SELECT id_producto, precio FROM productos
  WHERE id_producto IN (5, 11) AND disponible = TRUE;
  -- 2) encabezado (total se actualiza al final)
  INSERT INTO pedidos (id_cliente, tipo_entrega, direccion_entrega, notas)
  VALUES (1, 'DOMICILIO', 'Col. Escalón, Calle El Mirador #12, San Salvador', 'Prueba de transacción');
  SET @id_pedido = LAST_INSERT_ID();     -- en Java: getGeneratedKeys()
  -- 3) detalle
  INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
  VALUES (@id_pedido, 5, 2, 7.50, 15.00),
         (@id_pedido, 11, 2, 1.75, 3.50);
  -- 4) total y estado inicial
  UPDATE pedidos SET total = (SELECT SUM(subtotal) FROM detalle_pedido WHERE id_pedido = @id_pedido)
  WHERE id_pedido = @id_pedido;
  INSERT INTO seguimiento_pedido (id_pedido, estado, comentario, id_usuario)
  VALUES (@id_pedido, 'RECIBIDO', 'Pedido registrado', 3);
ROLLBACK;   -- aqui se deshace para no ensuciar los datos; la app hace COMMIT si todo salio bien

-- P5 Mis pedidos (Dev 3): pedidos del cliente con cantidad de platillos.
SELECT p.id_pedido, p.fecha_creacion, p.tipo_entrega, p.estado, p.total,
       SUM(d.cantidad) AS platillos
FROM pedidos p
JOIN detalle_pedido d ON d.id_pedido = p.id_pedido
WHERE p.id_cliente = 1
GROUP BY p.id_pedido, p.fecha_creacion, p.tipo_entrega, p.estado, p.total
ORDER BY p.fecha_creacion DESC;

-- P6 Detalle del pedido (Dev 4): lineas + historial.
SELECT pr.nombre, d.cantidad, d.precio_unitario, d.subtotal
FROM detalle_pedido d JOIN productos pr ON pr.id_producto = d.id_producto
WHERE d.id_pedido = 3;

SELECT s.estado, s.comentario, s.fecha_registro, u.nombre_completo AS cambiado_por
FROM seguimiento_pedido s LEFT JOIN usuarios u ON u.id_usuario = s.id_usuario
WHERE s.id_pedido = 3
ORDER BY s.fecha_registro;

-- P10 Tablero de cocina (Dev 3): pedidos activos con filtro por estado y paginacion.
SELECT p.id_pedido, u.nombre_completo AS cliente, p.tipo_entrega, p.estado, p.total, p.fecha_creacion
FROM pedidos p
JOIN clientes c ON c.id_cliente = p.id_cliente
JOIN usuarios u ON u.id_usuario = c.id_usuario
WHERE p.estado IN ('RECIBIDO','EN_PREPARACION','LISTO','EN_CAMINO')
ORDER BY p.fecha_creacion
LIMIT 10 OFFSET 0;                      -- ? ?  (tamano de pagina, desplazamiento)

-- P10 Cambio de estado con control de concurrencia: solo cambia si sigue en el estado esperado.
-- Si executeUpdate() devuelve 0, otro usuario ya lo cambio -> mostrar aviso.
START TRANSACTION;
  UPDATE pedidos SET estado = 'EN_PREPARACION'
  WHERE id_pedido = 17 AND estado = 'RECIBIDO';
  INSERT INTO seguimiento_pedido (id_pedido, estado, comentario, id_usuario)
  VALUES (17, 'EN_PREPARACION', 'Cocina inició la preparación', 2);
ROLLBACK;   -- la app hace COMMIT; aqui se deshace para conservar los datos de prueba

-- P7 Dashboard (Dev 4): ventas del dia (pedidos entregados).
SELECT COUNT(*) AS pedidos_entregados, COALESCE(SUM(total), 0) AS ventas
FROM pedidos
WHERE estado = 'ENTREGADO' AND DATE(fecha_creacion) = '2026-10-04';  -- en la app: CURDATE()

-- P7 Dashboard: pedidos por estado.
SELECT estado, COUNT(*) AS cantidad FROM pedidos GROUP BY estado;

-- P7 Dashboard: top 5 platillos mas vendidos (sin cancelados).
SELECT pr.nombre, SUM(d.cantidad) AS unidades, SUM(d.subtotal) AS ingresos
FROM detalle_pedido d
JOIN productos pr ON pr.id_producto = d.id_producto
JOIN pedidos p   ON p.id_pedido = d.id_pedido
WHERE p.estado <> 'CANCELADO'
GROUP BY pr.id_producto, pr.nombre
ORDER BY unidades DESC
LIMIT 5;

-- P11 Reportes (Dev 4): ventas por dia en un rango y horas pico.
SELECT DATE(fecha_creacion) AS dia, COUNT(*) AS pedidos, SUM(total) AS ventas
FROM pedidos
WHERE estado = 'ENTREGADO' AND fecha_creacion BETWEEN '2026-10-01' AND '2026-10-04 23:59:59'
GROUP BY DATE(fecha_creacion) ORDER BY dia;

SELECT HOUR(fecha_creacion) AS hora, COUNT(*) AS pedidos
FROM pedidos GROUP BY HOUR(fecha_creacion) ORDER BY pedidos DESC;

-- P8 Mantenimiento menu (Dev 2): desactivar en vez de borrar si ya tiene pedidos.
SELECT COUNT(*) AS veces_pedido FROM detalle_pedido WHERE id_producto = 10;
-- UPDATE productos SET disponible = FALSE WHERE id_producto = ?;
