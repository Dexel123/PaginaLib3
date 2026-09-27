-- Página Viva | DATOS INICIALES COMPLETOS - Sprint 3
-- Ejecutar después de 01 y 02.
USE libreriadb_in4cm;

-- Configuración general
INSERT INTO configuracion_sistema(clave,valor,descripcion) VALUES
('DIAS_MAX_DEVOLUCION','30','Plazo máximo para devoluciones de clientes'),
('MONEDA','GTQ','Moneda utilizada por la librería'),
('NOMBRE_NEGOCIO','Librería Página Viva','Nombre mostrado en comprobantes');

-- Catálogos
INSERT INTO categorias(nombre_categoria,descripcion) VALUES
('Ficción','Novelas y narrativa general'),
('Fantasía','Literatura fantástica'),
('Ciencia Ficción','Ciencia ficción y futuros posibles'),
('Misterio','Misterio y detectives'),
('Romance','Novela romántica'),
('Clásicos','Obras clásicas de la literatura'),
('Terror','Terror y literatura gótica'),
('Aventura','Aventura y exploración');

INSERT INTO editoriales(nit,nombre_editorial,telefono_editorial,direccion_editorial,correo_editorial) VALUES
('1001-A','Editorial Planeta','22334455','Zona 1, Ciudad de Guatemala','contacto.planeta@example.com'),
('1002-B','Penguin Random House','22334456','Zona 10, Ciudad de Guatemala','contacto.penguin@example.com'),
('1003-C','Editorial Santillana','22334457','Zona 9, Ciudad de Guatemala','contacto.santillana@example.com'),
('1004-D','Alfaguara','22334458','Zona 4, Ciudad de Guatemala','contacto.alfaguara@example.com'),
('1005-E','Ediciones Cátedra','22334459','Zona 12, Ciudad de Guatemala','contacto.catedra@example.com'),
('1006-F','Editorial Océano','22334460','Zona 13, Ciudad de Guatemala','contacto.oceano@example.com');

INSERT INTO autores(nombre_autor,apellido_autor,nacionalidad) VALUES
('Gabriel','García Márquez','Colombiana'),
('Julio','Cortázar','Argentina'),
('J. K.','Rowling','Británica'),
('Isaac','Asimov','Rusa/Estadounidense'),
('J. R. R.','Tolkien','Británica'),
('George','Orwell','Británica'),
('Antoine','de Saint-Exupéry','Francesa'),
('Jane','Austen','Británica'),
('Agatha','Christie','Británica'),
('Victor','Hugo','Francesa'),
('Franz','Kafka','Checa'),
('Mary','Shelley','Británica'),
('Arthur Conan','Doyle','Británica'),
('Louisa May','Alcott','Estadounidense'),
('Oscar','Wilde','Irlandesa'),
('Fiódor','Dostoyevski','Rusa'),
('Herman','Melville','Estadounidense'),
('Lewis','Carroll','Británica'),
('Bram','Stoker','Irlandesa'),
('Miguel Ángel','Asturias','Guatemalteca');

-- 15 compradores/clientes para pruebas de caja
INSERT INTO clientes(cui,nombre_cliente,apellido_cliente,correo_electronico,telefono) VALUES
(3000000000001,'Ana','López','ana.lopez@example.com','55550001'),
(3000000000002,'Carlos','Méndez','carlos.mendez@example.com','55550002'),
(3000000000003,'María','García','maria.garcia@example.com','55550003'),
(3000000000004,'José','Ramírez','jose.ramirez@example.com','55550004'),
(3000000000005,'Sofía','Morales','sofia.morales@example.com','55550005'),
(3000000000006,'Diego','Castillo','diego.castillo@example.com','55550006'),
(3000000000007,'Valeria','Hernández','valeria.hernandez@example.com','55550007'),
(3000000000008,'Luis','Pérez','luis.perez@example.com','55550008'),
(3000000000009,'Camila','Ruiz','camila.ruiz@example.com','55550009'),
(3000000000010,'Andrés','Santos','andres.santos@example.com','55550010'),
(3000000000011,'Daniela','Flores','daniela.flores@example.com','55550011'),
(3000000000012,'Javier','Cabrera','javier.cabrera@example.com','55550012'),
(3000000000013,'Paola','Vásquez','paola.vasquez@example.com','55550013'),
(3000000000014,'Fernando','Reyes','fernando.reyes@example.com','55550014'),
(3000000000015,'Gabriela','Ortiz','gabriela.ortiz@example.com','55550015');

-- Proveedores
INSERT INTO proveedores(nit_proveedor,nombre_proveedor,telefono_proveedor,direccion_proveedor,correo_proveedor) VALUES
('P-001','Distribuidora Central de Libros','22990011','Zona 4, Ciudad de Guatemala','ventas.central@example.com'),
('P-002','Mundo Editorial GT','22990012','Zona 10, Ciudad de Guatemala','pedidos.mundo@example.com'),
('P-003','Libros del Centro','22990013','Zona 1, Ciudad de Guatemala','contacto.centro@example.com');

-- 20 libros. El stock se carga después mediante compras para conservar trazabilidad.
INSERT INTO libros(isbn,titulo,fecha_publicacion,precio,id_categoria,nit_editorial,stock_minimo) VALUES
('978-0-123','Cien Años de Soledad','1967-05-30',150.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Ficción'),'1001-A',5),
('978-0-124','Rayuela','1963-06-28',135.50,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Ficción'),'1002-B',5),
('978-0-126','Harry Potter y la Piedra Filosofal','1997-06-26',180.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Fantasía'),'1002-B',5),
('978-0-128','Fundación','1951-05-01',140.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Ciencia Ficción'),'1003-C',3),
('978-0-130','El Señor de los Anillos','1954-07-29',225.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Fantasía'),'1002-B',6),
('978-0-131','1984','1949-06-08',110.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Ciencia Ficción'),'1004-D',4),
('978-0-132','El Principito','1943-04-06',85.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Clásicos'),'1003-C',5),
('978-0-133','Orgullo y Prejuicio','1813-01-28',105.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Romance'),'1005-E',4),
('978-0-134','Asesinato en el Orient Express','1934-01-01',125.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Misterio'),'1002-B',4),
('978-0-135','Los Miserables','1862-01-01',175.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Clásicos'),'1001-A',3),
('978-0-136','La Metamorfosis','1915-01-01',90.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Clásicos'),'1005-E',4),
('978-0-137','Frankenstein','1818-01-01',115.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Terror'),'1006-F',4),
('978-0-138','Estudio en Escarlata','1887-01-01',100.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Misterio'),'1006-F',3),
('978-0-139','Mujercitas','1868-01-01',120.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Clásicos'),'1004-D',4),
('978-0-140','El Retrato de Dorian Gray','1890-07-01',118.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Clásicos'),'1005-E',4),
('978-0-141','Crimen y Castigo','1866-01-01',165.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Clásicos'),'1001-A',3),
('978-0-142','Moby Dick','1851-10-18',145.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Aventura'),'1004-D',4),
('978-0-143','Alicia en el País de las Maravillas','1865-11-26',98.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Fantasía'),'1003-C',5),
('978-0-144','Drácula','1897-05-26',130.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Terror'),'1006-F',4),
('978-0-145','El Señor Presidente','1946-01-01',125.00,(SELECT id_categoria FROM categorias WHERE nombre_categoria='Ficción'),'1004-D',3);

-- Relación libro-autor
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-123' FROM autores WHERE nombre_autor='Gabriel' AND apellido_autor='García Márquez' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-124' FROM autores WHERE nombre_autor='Julio' AND apellido_autor='Cortázar' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-126' FROM autores WHERE nombre_autor='J. K.' AND apellido_autor='Rowling' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-128' FROM autores WHERE nombre_autor='Isaac' AND apellido_autor='Asimov' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-130' FROM autores WHERE nombre_autor='J. R. R.' AND apellido_autor='Tolkien' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-131' FROM autores WHERE nombre_autor='George' AND apellido_autor='Orwell' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-132' FROM autores WHERE nombre_autor='Antoine' AND apellido_autor='de Saint-Exupéry' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-133' FROM autores WHERE nombre_autor='Jane' AND apellido_autor='Austen' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-134' FROM autores WHERE nombre_autor='Agatha' AND apellido_autor='Christie' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-135' FROM autores WHERE nombre_autor='Victor' AND apellido_autor='Hugo' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-136' FROM autores WHERE nombre_autor='Franz' AND apellido_autor='Kafka' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-137' FROM autores WHERE nombre_autor='Mary' AND apellido_autor='Shelley' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-138' FROM autores WHERE nombre_autor='Arthur Conan' AND apellido_autor='Doyle' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-139' FROM autores WHERE nombre_autor='Louisa May' AND apellido_autor='Alcott' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-140' FROM autores WHERE nombre_autor='Oscar' AND apellido_autor='Wilde' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-141' FROM autores WHERE nombre_autor='Fiódor' AND apellido_autor='Dostoyevski' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-142' FROM autores WHERE nombre_autor='Herman' AND apellido_autor='Melville' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-143' FROM autores WHERE nombre_autor='Lewis' AND apellido_autor='Carroll' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-144' FROM autores WHERE nombre_autor='Bram' AND apellido_autor='Stoker' LIMIT 1;
INSERT INTO autores_libro(id_autor,isbn)
SELECT id_autor,'978-0-145' FROM autores WHERE nombre_autor='Miguel Ángel' AND apellido_autor='Asturias' LIMIT 1;

-- Usuarios para iniciar sesión en la aplicación
-- admin1  / admin123
-- bodega1 / bodega123
-- cajero1 / cajero123
CALL sp_registrar_usuario('admin1',SHA2('admin123',256),'Administrador','General','admin@paginalib3.local','admin');
CALL sp_registrar_usuario('bodega1',SHA2('bodega123',256),'Encargado','Bodega','bodega1@paginalib3.local','bodega');
CALL sp_registrar_usuario('cajero1',SHA2('cajero123',256),'Cajero','Uno','cajero1@paginalib3.local','cajero');

-- Inventario inicial mediante COMPRAS para conservar costo, proveedor y movimientos.
-- Algunos títulos quedan deliberadamente en stock crítico para probar US-3.3/US-3.5.
SET @compra_1=0;
CALL sp_registrar_compra('P-001',1,'INICIAL-001','Carga inicial proveedor 1',
JSON_ARRAY(
 JSON_OBJECT('isbn','978-0-123','cantidad',20,'costoUnitario',95.00),
 JSON_OBJECT('isbn','978-0-135','cantidad',7,'costoUnitario',110.00),
 JSON_OBJECT('isbn','978-0-141','cantidad',6,'costoUnitario',105.00),
 JSON_OBJECT('isbn','978-0-145','cantidad',9,'costoUnitario',78.00)
),@compra_1);

SET @compra_2=0;
CALL sp_registrar_compra('P-002',1,'INICIAL-002','Carga inicial proveedor 2',
JSON_ARRAY(
 JSON_OBJECT('isbn','978-0-124','cantidad',15,'costoUnitario',82.00),
 JSON_OBJECT('isbn','978-0-126','cantidad',10,'costoUnitario',120.00),
 JSON_OBJECT('isbn','978-0-130','cantidad',18,'costoUnitario',150.00),
 JSON_OBJECT('isbn','978-0-134','cantidad',4,'costoUnitario',80.00),
 JSON_OBJECT('isbn','978-0-133','cantidad',9,'costoUnitario',65.00)
),@compra_2);

SET @compra_3=0;
CALL sp_registrar_compra('P-003',1,'INICIAL-003','Carga inicial proveedor 3',
JSON_ARRAY(
 JSON_OBJECT('isbn','978-0-128','cantidad',8,'costoUnitario',90.00),
 JSON_OBJECT('isbn','978-0-131','cantidad',12,'costoUnitario',70.00),
 JSON_OBJECT('isbn','978-0-132','cantidad',5,'costoUnitario',50.00),
 JSON_OBJECT('isbn','978-0-136','cantidad',3,'costoUnitario',55.00),
 JSON_OBJECT('isbn','978-0-137','cantidad',11,'costoUnitario',72.00),
 JSON_OBJECT('isbn','978-0-138','cantidad',2,'costoUnitario',60.00),
 JSON_OBJECT('isbn','978-0-139','cantidad',8,'costoUnitario',75.00),
 JSON_OBJECT('isbn','978-0-140','cantidad',4,'costoUnitario',74.00),
 JSON_OBJECT('isbn','978-0-142','cantidad',10,'costoUnitario',92.00),
 JSON_OBJECT('isbn','978-0-143','cantidad',14,'costoUnitario',58.00),
 JSON_OBJECT('isbn','978-0-144','cantidad',3,'costoUnitario',82.00)
),@compra_3);

-- Resumen al terminar la carga
SELECT COUNT(*) AS total_libros FROM libros;
SELECT COUNT(*) AS total_clientes FROM clientes;
SELECT COUNT(*) AS total_proveedores FROM proveedores;
SELECT COUNT(*) AS libros_stock_critico FROM libros WHERE activo=TRUE AND stock_actual<=stock_minimo;
