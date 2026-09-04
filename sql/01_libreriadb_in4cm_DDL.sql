DROP DATABASE IF EXISTS libreriadb_in4cm;
CREATE DATABASE libreriadb_in4cm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libreriadb_in4cm;

-- Catalogos y maestros

CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE editoriales (
    nit VARCHAR(20) PRIMARY KEY,
    nombre_editorial VARCHAR(100) NOT NULL,
    telefono_editorial VARCHAR(15),
    direccion_editorial VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE autores (
    id_autor INT AUTO_INCREMENT PRIMARY KEY,
    nombre_autor VARCHAR(100) NOT NULL,
    apellido_autor VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(100),
    biografia TEXT
) ENGINE=InnoDB;

CREATE TABLE clientes (
    cui BIGINT PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    apellido_cliente VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE proveedores (
    nit_proveedor VARCHAR(20) PRIMARY KEY,
    nombre_proveedor VARCHAR(100) NOT NULL,
    telefono_proveedor VARCHAR(15),
    direccion_proveedor VARCHAR(100),
    correo_proveedor VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE
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
    id_categoria INT NOT NULL,
    nit_editorial VARCHAR(20) NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ck_libro_precio CHECK (precio >= 0),
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

-- Ventas e inventario

CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    fecha_venta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    descuento DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    estado ENUM('COMPLETADA','ANULADA','DEVUELTA') NOT NULL DEFAULT 'COMPLETADA',
    cui_cliente BIGINT NULL,
    id_usuario INT NOT NULL,
    usuario_autoriza_descuento INT NULL,
    fecha_anulacion TIMESTAMP NULL,
    usuario_anulacion INT NULL,
    motivo_anulacion VARCHAR(255) NULL,
    CONSTRAINT ck_venta_subtotal CHECK (subtotal >= 0),
    CONSTRAINT ck_venta_descuento CHECK (descuento >= 0 AND descuento <= subtotal),
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
    CONSTRAINT uq_venta_libro UNIQUE (id_venta, isbn),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_precio CHECK (precio_unitario >= 0),
    CONSTRAINT ck_detalle_subtotal CHECK (subtotal = cantidad * precio_unitario),
    CONSTRAINT fk_detalle_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_detalle_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE movimientos_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL,
    tipo_movimiento ENUM('INGRESO','VENTA','MERMA','TRASLADO','DEVOLUCION','AJUSTE') NOT NULL,
    cantidad INT NOT NULL,
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    id_venta INT NULL,
    nit_proveedor VARCHAR(20) NULL,
    observacion VARCHAR(255),
    CONSTRAINT ck_movimiento_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_movimiento_libro FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_venta FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_proveedor FOREIGN KEY (nit_proveedor) REFERENCES proveedores(nit_proveedor) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Indices

CREATE INDEX idx_libros_titulo ON libros(titulo);
CREATE INDEX idx_libros_categoria ON libros(id_categoria);
CREATE INDEX idx_libros_stock ON libros(activo, stock_actual, stock_minimo);
CREATE INDEX idx_usuarios_rol_activo ON usuarios(rol, activo);
CREATE INDEX idx_ventas_fecha_estado ON ventas(fecha_venta, estado);
CREATE INDEX idx_ventas_usuario_fecha ON ventas(id_usuario, fecha_venta);
CREATE INDEX idx_movimientos_libro_fecha ON movimientos_inventario(isbn, fecha_movimiento);
CREATE INDEX idx_movimientos_tipo_fecha ON movimientos_inventario(tipo_movimiento, fecha_movimiento);
