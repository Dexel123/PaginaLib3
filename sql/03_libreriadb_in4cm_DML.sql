-- Pagina-Libreria | DATOS INICIALES
USE libreriadb_in4cm;


-- Datos minimos para pruebas

INSERT INTO categorias(nombre_categoria) VALUES
('Ficción'),('Fantasía'),('Ciencia Ficción'),('Misterio'),('Romance');

INSERT INTO editoriales(nit,nombre_editorial,telefono_editorial,direccion_editorial) VALUES
('1001-A','Editorial Planeta','22334455','Zona 1, Ciudad'),
('1002-B','Penguin Random House','22334456','Zona 10, Ciudad'),
('1003-C','Editorial Santillana','22334457','Zona 9, Ciudad');

INSERT INTO autores(nombre_autor,apellido_autor,nacionalidad) VALUES
('Gabriel','García Márquez','Colombiana'),('Miguel Ángel','Asturias','Guatemalteca'),('Jane','Austen','Británica'),('Isaac','Asimov','Rusa/Estadounidense');

INSERT INTO clientes(cui,nombre_cliente,apellido_cliente,correo_electronico) VALUES
(2000100010101,'Ana','López','ana@example.com'),(2000100020101,'Carlos','Méndez','carlos@example.com');

INSERT INTO proveedores(nit_proveedor,nombre_proveedor,telefono_proveedor,direccion_proveedor,correo_proveedor) VALUES
('P-001','Distribuidora Central de Libros','22990011','Zona 4, Ciudad','ventas@example.com');

INSERT INTO libros(isbn,titulo,fecha_publicacion,precio,id_categoria,nit_editorial,stock_minimo) VALUES
('978-0-123','Cien Años de Soledad','1967-05-30',150.00,1,'1001-A',5),
('978-0-124','Rayuela','1963-06-28',135.50,1,'1002-B',5),
('978-0-126','Harry Potter y la Piedra Filosofal','1997-06-26',180.00,2,'1002-B',5),
('978-0-128','Fundación','1951-05-01',140.00,3,'1003-C',3);

INSERT INTO autores_libro(id_autor,isbn) VALUES
(1,'978-0-123'),(1,'978-0-124'),(3,'978-0-126'),(4,'978-0-128');

-- Usuarios de prueba

CALL sp_registrar_usuario('admin',SHA2('admin1234',256),'Administrador','General','admin@paginalib3.local','admin');
CALL sp_registrar_usuario('bodega1',SHA2('bodega123',256),'Encargado','Bodega','bodega1@paginalib3.local','bodega');
CALL sp_registrar_usuario('cajero1',SHA2('caj123',256),'Cajero','Uno','cajero1@paginalib3.local','cajero');

-- Inventario inicial

CALL sp_registrar_ingreso_inventario('978-0-123',20,1,'Carga inicial');
CALL sp_registrar_ingreso_inventario('978-0-124',15,1,'Carga inicial');
CALL sp_registrar_ingreso_inventario('978-0-126',10,1,'Carga inicial');
CALL sp_registrar_ingreso_inventario('978-0-128',8,1,'Carga inicial');
