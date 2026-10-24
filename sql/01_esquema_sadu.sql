-- =====================================================================
-- Sadu Pedidos | 01 - Esquema (8 tablas)
-- Orden de creacion: primero las tablas sin llaves foraneas.
-- Se puede volver a ejecutar: borra y crea todo desde cero.
-- =====================================================================
USE sadu_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS comprobantes;
DROP TABLE IF EXISTS seguimiento_pedido;
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS usuarios;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. usuarios: cuentas para iniciar sesion (los 3 roles)       [Dev 1]
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
  id_usuario      BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre_completo VARCHAR(120) NOT NULL,
  correo          VARCHAR(160) NOT NULL,
  hash_clave      VARCHAR(255) NOT NULL,          -- SHA-256 + Base64 (Guia 2), nunca la clave en texto
  rol             VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE',
  activo          BOOLEAN      NOT NULL DEFAULT TRUE,
  fecha_registro  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_usuario_correo UNIQUE (correo),
  CONSTRAINT chk_usuario_rol   CHECK (rol IN ('CLIENTE','ADMINISTRADOR','COCINA'))
);

-- ---------------------------------------------------------------------
-- 2. clientes: datos extra de los usuarios con rol CLIENTE (1 a 1) [Dev 2]
-- ---------------------------------------------------------------------
CREATE TABLE clientes (
  id_cliente  BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_usuario  BIGINT       NOT NULL,
  telefono    VARCHAR(15)  NOT NULL,
  direccion   VARCHAR(200) NULL,                  -- se sugiere al pedir a domicilio
  activo      BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT uq_cliente_usuario UNIQUE (id_usuario),
  CONSTRAINT fk_cliente_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ---------------------------------------------------------------------
-- 3. categorias: secciones del menu                            [Dev 2]
-- ---------------------------------------------------------------------
CREATE TABLE categorias (
  id_categoria BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre       VARCHAR(80)  NOT NULL,
  descripcion  VARCHAR(200) NULL,
  orden        INT          NOT NULL DEFAULT 0,   -- orden de los botones en la pantalla Menu
  activa       BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
);

-- ---------------------------------------------------------------------
-- 4. productos: platillos y bebidas del menu                   [Dev 2]
-- ---------------------------------------------------------------------
CREATE TABLE productos (
  id_producto    BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_categoria   BIGINT        NOT NULL,
  nombre         VARCHAR(120)  NOT NULL,
  descripcion    VARCHAR(300)  NULL,
  precio         DECIMAL(8,2)  NOT NULL,
  imagen_url     VARCHAR(255)  NULL,              -- ruta dentro de resources/img/menu/
  disponible     BOOLEAN       NOT NULL DEFAULT TRUE,   -- se desactiva en vez de borrar
  destacado      BOOLEAN       NOT NULL DEFAULT FALSE,  -- aparece en la pantalla Inicio
  fecha_creacion TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_producto_nombre    UNIQUE (nombre),
  CONSTRAINT chk_producto_precio   CHECK (precio > 0),
  CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

-- ---------------------------------------------------------------------
-- 5. pedidos: encabezado del pedido                            [Dev 3]
-- ---------------------------------------------------------------------
CREATE TABLE pedidos (
  id_pedido           BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_cliente          BIGINT        NOT NULL,
  tipo_entrega        VARCHAR(20)   NOT NULL,
  estado              VARCHAR(20)   NOT NULL DEFAULT 'RECIBIDO',
  direccion_entrega   VARCHAR(200)  NULL,
  notas               VARCHAR(300)  NULL,
  total               DECIMAL(10,2) NOT NULL DEFAULT 0,
  fecha_creacion      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
  CONSTRAINT chk_pedido_tipo   CHECK (tipo_entrega IN ('EN_LOCAL','PARA_LLEVAR','DOMICILIO')),
  CONSTRAINT chk_pedido_estado CHECK (estado IN ('RECIBIDO','EN_PREPARACION','LISTO','EN_CAMINO','ENTREGADO','CANCELADO')),
  CONSTRAINT chk_pedido_total  CHECK (total >= 0),
  -- a domicilio la direccion es obligatoria
  CONSTRAINT chk_pedido_direccion CHECK (tipo_entrega <> 'DOMICILIO' OR direccion_entrega IS NOT NULL)
);

CREATE INDEX idx_pedido_estado  ON pedidos (estado);          -- tablero de cocina
CREATE INDEX idx_pedido_fecha   ON pedidos (fecha_creacion);  -- dashboard y reportes

-- ---------------------------------------------------------------------
-- 6. detalle_pedido: platillos de cada pedido                  [Dev 3]
-- ---------------------------------------------------------------------
CREATE TABLE detalle_pedido (
  id_detalle      BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_pedido       BIGINT        NOT NULL,
  id_producto     BIGINT        NOT NULL,
  cantidad        INT           NOT NULL,
  precio_unitario DECIMAL(8,2)  NOT NULL,         -- copia del precio al momento de ordenar
  subtotal        DECIMAL(10,2) NOT NULL,         -- cantidad * precio_unitario
  CONSTRAINT uq_detalle_pedido_producto UNIQUE (id_pedido, id_producto),
  CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
  CONSTRAINT chk_detalle_precio   CHECK (precio_unitario > 0),
  CONSTRAINT fk_detalle_pedido    FOREIGN KEY (id_pedido)   REFERENCES pedidos(id_pedido),
  CONSTRAINT fk_detalle_producto  FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- ---------------------------------------------------------------------
-- 7. seguimiento_pedido: historial de estados                  [Dev 4]
-- ---------------------------------------------------------------------
CREATE TABLE seguimiento_pedido (
  id_seguimiento BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_pedido      BIGINT       NOT NULL,
  estado         VARCHAR(20)  NOT NULL,
  comentario     VARCHAR(300) NULL,
  id_usuario     BIGINT       NULL,               -- quien hizo el cambio (cocina/admin/cliente)
  fecha_registro TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_seguimiento_estado CHECK (estado IN ('RECIBIDO','EN_PREPARACION','LISTO','EN_CAMINO','ENTREGADO','CANCELADO')),
  CONSTRAINT fk_seguimiento_pedido  FOREIGN KEY (id_pedido)  REFERENCES pedidos(id_pedido),
  CONSTRAINT fk_seguimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ---------------------------------------------------------------------
-- 8. comprobantes: uno por pedido entregado                    [Dev 4]
-- ---------------------------------------------------------------------
CREATE TABLE comprobantes (
  id_comprobante BIGINT PRIMARY KEY AUTO_INCREMENT,
  id_pedido      BIGINT        NOT NULL,
  numero         VARCHAR(20)   NOT NULL,          -- ej. SADU-000001
  metodo_pago    VARCHAR(20)   NOT NULL,
  total          DECIMAL(10,2) NOT NULL,
  fecha_emision  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_comprobante_pedido UNIQUE (id_pedido),
  CONSTRAINT uq_comprobante_numero UNIQUE (numero),
  CONSTRAINT chk_comprobante_metodo CHECK (metodo_pago IN ('EFECTIVO','TARJETA')),
  CONSTRAINT fk_comprobante_pedido  FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);
