-- =====================================================================
-- Sadu Pedidos | 04 - Pruebas de integridad (CADA UNA DEBE FALLAR)
-- Ejecutar una por una y tomar captura del error para la matriz de pruebas.
-- =====================================================================
USE sadu_db;

-- I01 Correo repetido -> Duplicate entry
INSERT INTO usuarios (nombre_completo, correo, hash_clave) VALUES ('Copia', 'ana.martinez@demo.local', 'x');

-- I02 Precio 0 -> Check constraint 'chk_producto_precio'
INSERT INTO productos (id_categoria, nombre, precio) VALUES (1, 'Platillo gratis', 0);

-- I03 Categoria inexistente -> foreign key fails
INSERT INTO productos (id_categoria, nombre, precio) VALUES (999, 'Sin categoria', 5.00);

-- I04 Pedido a domicilio sin direccion -> Check constraint 'chk_pedido_direccion'
INSERT INTO pedidos (id_cliente, tipo_entrega) VALUES (1, 'DOMICILIO');

-- I05 Estado no permitido -> Check constraint 'chk_pedido_estado'
UPDATE pedidos SET estado = 'PERDIDO' WHERE id_pedido = 1;

-- I06 Cantidad 0 -> Check constraint 'chk_detalle_cantidad'
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal) VALUES (1, 2, 0, 3.75, 0);

-- I07 Mismo platillo dos veces en un pedido -> Duplicate entry (sumar cantidad en su lugar)
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
SELECT id_pedido, id_producto, 1, precio_unitario, precio_unitario FROM detalle_pedido WHERE id_detalle = 1;

-- I08 Borrar un producto que ya fue pedido -> foreign key fails (se debe desactivar)
DELETE FROM productos WHERE id_producto = 1;

-- I09 Segundo comprobante para el mismo pedido -> Duplicate entry
INSERT INTO comprobantes (id_pedido, numero, metodo_pago, total) VALUES (1, 'SADU-999999', 'EFECTIVO', 1.00);
