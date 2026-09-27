DROP DATABASE IF EXISTS libreriadb_in4cm;
CREATE DATABASE libreriadb_in4cm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libreriadb_in4cm;

-- Catalogos y maestros

CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE editoriales (
    nit VARCHAR(20) PRIMARY KEY,
    nombre_editorial VARCHAR(100) NOT NULL,
    telefono_editorial VARCHAR(15),
    direccion_editorial VARCHAR(150),
    correo_editorial VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE autores (
    id_autor INT AUTO_INCREMENT PRIMARY KEY,
    nombre_autor VARCHAR(100) NOT NULL,
    apellido_autor VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(100),
    biografia TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE clientes (
    cui BIGINT PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    apellido_cliente VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(100),
    telefono VARCHAR(15),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE proveedores (
    nit_proveedor VARCHAR(20) PRIMARY KEY,
    nombre_proveedor VARCHAR(100) NOT NULL,
    telefono_proveedor VARCHAR(15),
    direccion_proveedor VARCHAR(100),
    correo_proveedor VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE configuracion_sistema (
    clave VARCHAR(60) PRIMARY KEY,
    valor VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(100),
    rol ENUM('admin','bodega','cajero') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE libros (
    isbn VARCHAR(20) PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    fecha_publicacion DATE,
    precio DECIMAL(10,2) NOT NULL,
    costo_promedio DECIMAL(10,2) NOT NULL DEFAULT 0,
    id_categoria INT NOT NULL,
    nit_editorial VARCHAR(20) NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ck_libro_precio CHECK (precio >= 0),
    CONSTRAINT ck_libro_costo CHECK (costo_promedio >= 0),
    CONSTRAINT ck_libro_stock CHECK (stock_actual >= 0),
    CONSTRAINT ck_libro_stock_min CHECK (stock_minimo >= 0),
    CONSTRAINT fk_libro_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_libro_editorial FOREIGN KEY (nit_editorial) REFERENCES editoriales(nit) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE autores_libro (
    id_autor_libro INT AUTO_INCREMENT PRIMARY KEY,
    id_autor INT NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    CONSTRAINT uq_autor_libro UNIQUE (id_autor, isbn),
    CONSTRAINT fk_autorlibro_autor FOREIGN KEY (id_autor) REFERENCES autores(id_autor) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_autorlibro_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE proveedores_libro (
    id_proveedor_libro INT AUTO_INCREMENT PRIMARY KEY,
    nit_proveedor VARCHAR(20) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    codigo_proveedor VARCHAR(60),
    costo_ultima_compra DECIMAL(10,2) NOT NULL DEFAULT 0,
    proveedor_preferido BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_proveedor_libro UNIQUE (nit_proveedor, isbn),
    CONSTRAINT ck_proveedor_libro_costo CHECK (costo_ultima_compra >= 0),
    CONSTRAINT fk_proveedorlibro_proveedor FOREIGN KEY (nit_proveedor) REFERENCES proveedores(nit_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_proveedorlibro_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Las compras representan abastecimiento desde proveedores. Se conservan
-- para no perder el concepto original, pero ya no se confunden con ventas.
CREATE TABLE compras (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    fecha_compra TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nit_proveedor VARCHAR(20) NOT NULL,
    id_usuario INT NOT NULL,
    numero_documento VARCHAR(60),
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    estado ENUM('RECIBIDA','ANULADA') NOT NULL DEFAULT 'RECIBIDA',
    observacion VARCHAR(255),
    fecha_anulacion TIMESTAMP NULL,
    usuario_anulacion INT NULL,
    motivo_anulacion VARCHAR(255) NULL,
    CONSTRAINT ck_compra_subtotal CHECK (subtotal >= 0),
    CONSTRAINT ck_compra_total CHECK (total >= 0),
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (nit_proveedor) REFERENCES proveedores(nit_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_compra_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_compra_usuario_anulacion FOREIGN KEY (usuario_anulacion) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalle_compra (
    id_detalle_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    costo_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT uq_compra_libro UNIQUE (id_compra, isbn),
    CONSTRAINT ck_detalle_compra_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_compra_costo CHECK (costo_unitario >= 0),
    CONSTRAINT ck_detalle_compra_subtotal CHECK (subtotal = cantidad * costo_unitario),
    CONSTRAINT fk_detallecompra_compra FOREIGN KEY (id_compra) REFERENCES compras(id_compra) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_detallecompra_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Ventas e inventario

CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    fecha_venta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    descuento DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    estado ENUM('COMPLETADA','ANULADA','PARCIALMENTE_DEVUELTA','DEVUELTA') NOT NULL DEFAULT 'COMPLETADA',
    numero_comprobante VARCHAR(40) NULL UNIQUE,
    tipo_descuento ENUM('NINGUNO','MONTO','PORCENTAJE') NOT NULL DEFAULT 'NINGUNO',
    valor_descuento DECIMAL(10,2) NOT NULL DEFAULT 0,
    cui_cliente BIGINT NULL,
    id_usuario INT NOT NULL,
    usuario_autoriza_descuento INT NULL,
    fecha_anulacion TIMESTAMP NULL,
    usuario_anulacion INT NULL,
    motivo_anulacion VARCHAR(255) NULL,
    CONSTRAINT ck_venta_subtotal CHECK (subtotal >= 0),
    CONSTRAINT ck_venta_descuento CHECK (descuento >= 0 AND descuento <= subtotal),
    CONSTRAINT ck_venta_valor_descuento CHECK (valor_descuento >= 0),
    CONSTRAINT ck_venta_total CHECK (total = subtotal - descuento),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (cui_cliente) REFERENCES clientes(cui) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_venta_autoriza FOREIGN KEY (usuario_autoriza_descuento) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_venta_anula FOREIGN KEY (usuario_anulacion) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalle_venta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    cantidad_devuelta INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_venta_libro UNIQUE (id_venta, isbn),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_precio CHECK (precio_unitario >= 0),
    CONSTRAINT ck_detalle_subtotal CHECK (subtotal = cantidad * precio_unitario),
    CONSTRAINT ck_detalle_devuelto CHECK (cantidad_devuelta >= 0 AND cantidad_devuelta <= cantidad),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_detalle_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE devoluciones (
    id_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    tipo ENUM('ANULACION','DEVOLUCION') NOT NULL,
    fecha_devolucion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    total_devuelto DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT ck_devolucion_total CHECK (total_devuelto >= 0),
    CONSTRAINT fk_devolucion_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_devolucion_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE detalle_devolucion (
    id_detalle_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_devolucion INT NOT NULL,
    id_detalle_venta INT NOT NULL,
    cantidad INT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    CONSTRAINT uq_devolucion_detalle UNIQUE (id_devolucion, id_detalle_venta),
    CONSTRAINT ck_detalle_devolucion_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_devolucion_monto CHECK (monto >= 0),
    CONSTRAINT fk_detalledevolucion_devolucion FOREIGN KEY (id_devolucion) REFERENCES devoluciones(id_devolucion) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_detalledevolucion_venta FOREIGN KEY (id_detalle_venta) REFERENCES detalle_venta(id_detalle) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE movimientos_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL,
    tipo_movimiento ENUM('INGRESO','COMPRA','VENTA','MERMA','TRASLADO','DEVOLUCION','DEVOLUCION_PROVEEDOR','ANULACION_VENTA','AJUSTE','AJUSTE_ENTRADA') NOT NULL,
    cantidad INT NOT NULL,
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    id_venta INT NULL,
    id_compra INT NULL,
    id_devolucion INT NULL,
    nit_proveedor VARCHAR(20) NULL,
    observacion VARCHAR(255),
    CONSTRAINT ck_movimiento_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_movimiento_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_compra FOREIGN KEY (id_compra) REFERENCES compras(id_compra) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_devolucion FOREIGN KEY (id_devolucion) REFERENCES devoluciones(id_devolucion) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_proveedor FOREIGN KEY (nit_proveedor) REFERENCES proveedores(nit_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE historial_precios (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL,
    precio_anterior DECIMAL(10,2) NOT NULL,
    precio_nuevo DECIMAL(10,2) NOT NULL,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    motivo VARCHAR(255),
    CONSTRAINT ck_historial_precio_anterior CHECK (precio_anterior >= 0),
    CONSTRAINT ck_historial_precio_nuevo CHECK (precio_nuevo >= 0),
    CONSTRAINT fk_historialprecio_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_historialprecio_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE bitacora_sistema (
    id_evento BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_evento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NULL,
    accion VARCHAR(60) NOT NULL,
    entidad VARCHAR(60) NOT NULL,
    id_referencia VARCHAR(80) NULL,
    detalle VARCHAR(500) NULL,
    CONSTRAINT fk_bitacora_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Indices

CREATE INDEX idx_libros_titulo ON libros(titulo);
CREATE INDEX idx_libros_categoria ON libros(id_categoria);
CREATE INDEX idx_libros_stock ON libros(activo, stock_actual, stock_minimo);
CREATE INDEX idx_libros_editorial ON libros(nit_editorial);
CREATE INDEX idx_usuarios_rol_activo ON usuarios(rol, activo);
CREATE INDEX idx_ventas_fecha_estado ON ventas(fecha_venta, estado);
CREATE INDEX idx_ventas_usuario_fecha ON ventas(id_usuario, fecha_venta);
CREATE INDEX idx_ventas_cliente_fecha ON ventas(cui_cliente, fecha_venta);
CREATE INDEX idx_detalle_venta_isbn ON detalle_venta(isbn, id_venta);
CREATE INDEX idx_movimientos_libro_fecha ON movimientos_inventario(isbn, fecha_movimiento);
CREATE INDEX idx_movimientos_tipo_fecha ON movimientos_inventario(tipo_movimiento, fecha_movimiento);
CREATE INDEX idx_compras_proveedor_fecha ON compras(nit_proveedor, fecha_compra);
CREATE INDEX idx_devoluciones_venta_fecha ON devoluciones(id_venta, fecha_devolucion);
CREATE INDEX idx_historial_precios_libro_fecha ON historial_precios(isbn, fecha_cambio);

-- -----------------------------------------------------------------------------
-- Usuario de conexión utilizado por src/db.properties
-- Ejecutar este script con root u otro usuario con permisos para CREATE USER.
-- -----------------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'IN4CM'@'localhost' IDENTIFIED BY '#NdimAM4';
ALTER USER 'IN4CM'@'localhost' IDENTIFIED BY '#NdimAM4';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE
ON libreriadb_in4cm.*
TO 'IN4CM'@'localhost';
FLUSH PRIVILEGES;
