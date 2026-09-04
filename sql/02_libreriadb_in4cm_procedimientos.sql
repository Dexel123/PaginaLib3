-- Pagina-Libreria | Procedimientos, vistas y compatibilidad DAO
-- Requiere que libreriadb_in4cm exista.
USE libreriadb_in4cm;

DELIMITER $$
DROP PROCEDURE IF EXISTS sp_insertarcategoria$$
DROP PROCEDURE IF EXISTS sp_listarcategorias$$
DROP PROCEDURE IF EXISTS sp_buscarcategoria$$
DROP PROCEDURE IF EXISTS sp_actualizarcategoria$$
DROP PROCEDURE IF EXISTS sp_eliminarcategoria$$
DROP PROCEDURE IF EXISTS sp_insertareditorial$$
DROP PROCEDURE IF EXISTS sp_listareditoriales$$
DROP PROCEDURE IF EXISTS sp_buscareditorial$$
DROP PROCEDURE IF EXISTS sp_actualizareditorial$$
DROP PROCEDURE IF EXISTS sp_eliminareditorial$$
DROP PROCEDURE IF EXISTS sp_insertarautor$$
DROP PROCEDURE IF EXISTS sp_listarautores$$
DROP PROCEDURE IF EXISTS sp_buscarautor$$
DROP PROCEDURE IF EXISTS sp_actualizarautor$$
DROP PROCEDURE IF EXISTS sp_eliminarautor$$
DROP PROCEDURE IF EXISTS sp_insertarcliente$$
DROP PROCEDURE IF EXISTS sp_listarclientes$$
DROP PROCEDURE IF EXISTS sp_buscarcliente$$
DROP PROCEDURE IF EXISTS sp_actualizarcliente$$
DROP PROCEDURE IF EXISTS sp_eliminarcliente$$
DROP PROCEDURE IF EXISTS sp_insertarlibro$$
DROP PROCEDURE IF EXISTS sp_listarlibros$$
DROP PROCEDURE IF EXISTS sp_buscarlibro$$
DROP PROCEDURE IF EXISTS sp_buscar_libros$$
DROP PROCEDURE IF EXISTS sp_actualizarlibro$$
DROP PROCEDURE IF EXISTS sp_eliminarlibro$$
DROP PROCEDURE IF EXISTS sp_insertarautorlibro$$
DROP PROCEDURE IF EXISTS sp_listarautoreslibro$$
DROP PROCEDURE IF EXISTS sp_buscarautorlibro$$
DROP PROCEDURE IF EXISTS sp_actualizarautorlibro$$
DROP PROCEDURE IF EXISTS sp_eliminarautorlibro$$
DROP PROCEDURE IF EXISTS sp_buscar_usuario_username$$
DROP PROCEDURE IF EXISTS sp_restablecer_contrasena$$
DROP PROCEDURE IF EXISTS sp_registrar_usuario$$
DROP PROCEDURE IF EXISTS sp_iniciar_sesion$$
DROP PROCEDURE IF EXISTS sp_listarusuarios$$
DROP PROCEDURE IF EXISTS sp_buscarusuario$$
DROP PROCEDURE IF EXISTS sp_actualizarusuario$$
DROP PROCEDURE IF EXISTS sp_cambiar_estado_usuario$$
DROP PROCEDURE IF EXISTS sp_cambiar_rol_usuario$$
DROP PROCEDURE IF EXISTS sp_cambiar_contrasena$$
DROP PROCEDURE IF EXISTS sp_actualizarstocklibro$$
DROP PROCEDURE IF EXISTS sp_registrar_ingreso_inventario$$
DROP PROCEDURE IF EXISTS sp_registrar_salida_inventario$$
DROP PROCEDURE IF EXISTS sp_listarmovimientosinventario$$
DROP PROCEDURE IF EXISTS sp_movimientosporlibro$$
DROP PROCEDURE IF EXISTS sp_registrar_venta$$
DROP PROCEDURE IF EXISTS sp_registrarventacajero$$
DROP PROCEDURE IF EXISTS sp_listarventas$$
DROP PROCEDURE IF EXISTS sp_buscarventa$$
DROP PROCEDURE IF EXISTS sp_listardetalleventa$$
DROP PROCEDURE IF EXISTS sp_buscardetalleventa$$
DROP PROCEDURE IF EXISTS sp_insertardetalleventa$$
DROP PROCEDURE IF EXISTS sp_ventasdeldiaporusuario$$
DROP PROCEDURE IF EXISTS sp_anularventa$$
DROP PROCEDURE IF EXISTS sp_insertarproveedor$$
DROP PROCEDURE IF EXISTS sp_listarproveedores$$
DROP PROCEDURE IF EXISTS sp_buscarproveedor$$
DROP PROCEDURE IF EXISTS sp_actualizarproveedor$$
DROP PROCEDURE IF EXISTS sp_eliminarproveedor$$

CREATE PROCEDURE sp_insertarcategoria(IN _nombre_categoria VARCHAR(100))
BEGIN
    IF TRIM(_nombre_categoria) = '' THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El nombre de categoria es obligatorio'; END IF;
    INSERT INTO categorias(nombre_categoria) VALUES (TRIM(_nombre_categoria));
END$$

CREATE PROCEDURE sp_listarcategorias()
BEGIN SELECT id_categoria,nombre_categoria FROM categorias ORDER BY nombre_categoria; END$$

CREATE PROCEDURE sp_buscarcategoria(IN _id_categoria INT)
BEGIN SELECT id_categoria,nombre_categoria FROM categorias WHERE id_categoria=_id_categoria; END$$

CREATE PROCEDURE sp_actualizarcategoria(IN _id_categoria INT, IN _nombre_categoria VARCHAR(100))
BEGIN UPDATE categorias SET nombre_categoria=TRIM(_nombre_categoria) WHERE id_categoria=_id_categoria; END$$

CREATE PROCEDURE sp_eliminarcategoria(IN _id_categoria INT)
BEGIN DELETE FROM categorias WHERE id_categoria=_id_categoria; END$$

CREATE PROCEDURE sp_insertareditorial(IN _nit VARCHAR(20), IN _nombre_editorial VARCHAR(100), IN _telefono VARCHAR(15), IN _direccion VARCHAR(100))
BEGIN INSERT INTO editoriales(nit,nombre_editorial,telefono_editorial,direccion_editorial) VALUES(_nit,TRIM(_nombre_editorial),_telefono,_direccion); END$$

CREATE PROCEDURE sp_listareditoriales()
BEGIN SELECT nit,nombre_editorial,telefono_editorial,direccion_editorial FROM editoriales ORDER BY nombre_editorial; END$$

CREATE PROCEDURE sp_buscareditorial(IN _nit VARCHAR(20))
BEGIN SELECT nit,nombre_editorial,telefono_editorial,direccion_editorial FROM editoriales WHERE nit=_nit; END$$

CREATE PROCEDURE sp_actualizareditorial(IN _nit VARCHAR(20), IN _nombre_editorial VARCHAR(100), IN _telefono VARCHAR(15), IN _direccion VARCHAR(100))
BEGIN UPDATE editoriales SET nombre_editorial=TRIM(_nombre_editorial),telefono_editorial=_telefono,direccion_editorial=_direccion WHERE nit=_nit; END$$

CREATE PROCEDURE sp_eliminareditorial(IN _nit VARCHAR(20))
BEGIN DELETE FROM editoriales WHERE nit=_nit; END$$

CREATE PROCEDURE sp_insertarautor(IN _nombre VARCHAR(100), IN _apellido VARCHAR(100), IN _nacionalidad VARCHAR(100), IN _biografia TEXT)
BEGIN INSERT INTO autores(nombre_autor,apellido_autor,nacionalidad,biografia) VALUES(TRIM(_nombre),TRIM(_apellido),_nacionalidad,_biografia); END$$

CREATE PROCEDURE sp_listarautores()
BEGIN SELECT id_autor,nombre_autor,apellido_autor,nacionalidad,biografia FROM autores ORDER BY apellido_autor,nombre_autor; END$$

CREATE PROCEDURE sp_buscarautor(IN _id_autor INT)
BEGIN SELECT id_autor,nombre_autor,apellido_autor,nacionalidad,biografia FROM autores WHERE id_autor=_id_autor; END$$

CREATE PROCEDURE sp_actualizarautor(IN _id INT, IN _nombre VARCHAR(100), IN _apellido VARCHAR(100), IN _nacionalidad VARCHAR(100), IN _biografia TEXT)
BEGIN UPDATE autores SET nombre_autor=TRIM(_nombre),apellido_autor=TRIM(_apellido),nacionalidad=_nacionalidad,biografia=_biografia WHERE id_autor=_id; END$$

CREATE PROCEDURE sp_eliminarautor(IN _id INT)
BEGIN DELETE FROM autores WHERE id_autor=_id; END$$

CREATE PROCEDURE sp_insertarcliente(IN _cui BIGINT, IN _nombre VARCHAR(100), IN _apellido VARCHAR(100), IN _correo VARCHAR(100))
BEGIN INSERT INTO clientes(cui,nombre_cliente,apellido_cliente,correo_electronico) VALUES(_cui,TRIM(_nombre),TRIM(_apellido),_correo); END$$

CREATE PROCEDURE sp_listarclientes()
BEGIN SELECT cui,nombre_cliente,apellido_cliente,correo_electronico FROM clientes ORDER BY apellido_cliente,nombre_cliente; END$$

CREATE PROCEDURE sp_buscarcliente(IN _cui BIGINT)
BEGIN SELECT cui,nombre_cliente,apellido_cliente,correo_electronico FROM clientes WHERE cui=_cui; END$$

CREATE PROCEDURE sp_actualizarcliente(IN _cui BIGINT, IN _nombre VARCHAR(100), IN _apellido VARCHAR(100), IN _correo VARCHAR(100))
BEGIN UPDATE clientes SET nombre_cliente=TRIM(_nombre),apellido_cliente=TRIM(_apellido),correo_electronico=_correo WHERE cui=_cui; END$$

CREATE PROCEDURE sp_eliminarcliente(IN _cui BIGINT)
BEGIN DELETE FROM clientes WHERE cui=_cui; END$$

CREATE PROCEDURE sp_insertarlibro(IN _isbn VARCHAR(20), IN _titulo VARCHAR(150), IN _fecha DATE, IN _precio DECIMAL(10,2), IN _categoria INT, IN _editorial VARCHAR(20))
BEGIN
    IF _precio < 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El precio no puede ser negativo'; END IF;
    INSERT INTO libros(isbn,titulo,fecha_publicacion,precio,id_categoria,nit_editorial) VALUES(_isbn,TRIM(_titulo),_fecha,_precio,_categoria,_editorial);
END$$

CREATE PROCEDURE sp_listarlibros()
BEGIN
    SELECT l.isbn,l.titulo,l.fecha_publicacion,l.precio,l.id_categoria,l.nit_editorial,l.stock_actual,l.stock_minimo,l.activo
    FROM libros l ORDER BY l.titulo;
END$$

CREATE PROCEDURE sp_buscarlibro(IN _isbn VARCHAR(20))
BEGIN
    SELECT isbn,titulo,fecha_publicacion,precio,id_categoria,nit_editorial,stock_actual,stock_minimo,activo FROM libros WHERE isbn=_isbn;
END$$

CREATE PROCEDURE sp_buscar_libros(IN _texto VARCHAR(150))
BEGIN
    SELECT DISTINCT l.isbn,l.titulo,l.precio,l.stock_actual,l.stock_minimo,l.activo,
           GROUP_CONCAT(DISTINCT CONCAT(a.nombre_autor,' ',a.apellido_autor) SEPARATOR ', ') AS autores
    FROM libros l
    LEFT JOIN autores_libro al ON al.isbn=l.isbn
    LEFT JOIN autores a ON a.id_autor=al.id_autor
    WHERE l.activo=TRUE
      AND (l.isbn LIKE CONCAT('%',_texto,'%') OR l.titulo LIKE CONCAT('%',_texto,'%')
           OR a.nombre_autor LIKE CONCAT('%',_texto,'%') OR a.apellido_autor LIKE CONCAT('%',_texto,'%'))
    GROUP BY l.isbn,l.titulo,l.precio,l.stock_actual,l.stock_minimo,l.activo
    ORDER BY l.titulo;
END$$

CREATE PROCEDURE sp_actualizarlibro(IN _isbn VARCHAR(20), IN _titulo VARCHAR(150), IN _fecha DATE, IN _precio DECIMAL(10,2), IN _categoria INT, IN _editorial VARCHAR(20))
BEGIN
    IF _precio < 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El precio no puede ser negativo'; END IF;
    UPDATE libros SET titulo=TRIM(_titulo),fecha_publicacion=_fecha,precio=_precio,id_categoria=_categoria,nit_editorial=_editorial WHERE isbn=_isbn;
END$$

CREATE PROCEDURE sp_eliminarlibro(IN _isbn VARCHAR(20))
BEGIN UPDATE libros SET activo=FALSE WHERE isbn=_isbn; END$$

CREATE PROCEDURE sp_insertarautorlibro(IN _id_autor INT, IN _isbn VARCHAR(20))
BEGIN INSERT INTO autores_libro(id_autor,isbn) VALUES(_id_autor,_isbn); END$$

CREATE PROCEDURE sp_listarautoreslibro()
BEGIN SELECT id_autor_libro,id_autor,isbn FROM autores_libro ORDER BY isbn; END$$

CREATE PROCEDURE sp_buscarautorlibro(IN _id INT)
BEGIN SELECT id_autor_libro,id_autor,isbn FROM autores_libro WHERE id_autor_libro=_id; END$$

CREATE PROCEDURE sp_actualizarautorlibro(IN _id INT, IN _autor INT, IN _isbn VARCHAR(20))
BEGIN UPDATE autores_libro SET id_autor=_autor,isbn=_isbn WHERE id_autor_libro=_id; END$$

CREATE PROCEDURE sp_eliminarautorlibro(IN _id INT)
BEGIN DELETE FROM autores_libro WHERE id_autor_libro=_id; END$$

-- Usuarios / autenticacion

CREATE PROCEDURE sp_buscar_usuario_username(IN _username VARCHAR(50))
BEGIN
    SELECT id,username,nombre,apellido,correo,rol,activo
    FROM usuarios
    WHERE username=TRIM(_username)
    LIMIT 1;
END$$

CREATE PROCEDURE sp_restablecer_contrasena(IN _username VARCHAR(50), IN _nueva CHAR(64), OUT _resultado TINYINT)
BEGIN
    UPDATE usuarios
    SET password_hash=_nueva
    WHERE username=TRIM(_username) AND activo=TRUE;
    SET _resultado=IF(ROW_COUNT()=1,1,0);
END$$


CREATE PROCEDURE sp_registrar_usuario(IN _username VARCHAR(50), IN _hash CHAR(64), IN _nombre VARCHAR(100), IN _apellido VARCHAR(100), IN _correo VARCHAR(100), IN _rol VARCHAR(20))
BEGIN
    IF _rol NOT IN ('admin','bodega','cajero') THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Rol invalido'; END IF;
    INSERT INTO usuarios(username,password_hash,nombre,apellido,correo,rol) VALUES(TRIM(_username),_hash,TRIM(_nombre),TRIM(_apellido),_correo,_rol);
END$$

CREATE PROCEDURE sp_iniciar_sesion(IN _username VARCHAR(50), IN _hash CHAR(64))
BEGIN
    SELECT id,username,nombre,apellido,correo,rol,activo FROM usuarios
    WHERE username=_username AND password_hash=_hash AND activo=TRUE LIMIT 1;
END$$

CREATE PROCEDURE sp_listarusuarios()
BEGIN SELECT id,username,nombre,apellido,correo,rol,activo,fecha_creacion,fecha_actualizacion FROM usuarios ORDER BY fecha_creacion DESC; END$$

CREATE PROCEDURE sp_buscarusuario(IN _id INT)
BEGIN SELECT id,username,nombre,apellido,correo,rol,activo,fecha_creacion,fecha_actualizacion FROM usuarios WHERE id=_id; END$$

CREATE PROCEDURE sp_actualizarusuario(IN _id INT, IN _nombre VARCHAR(100), IN _apellido VARCHAR(100), IN _correo VARCHAR(100))
BEGIN UPDATE usuarios SET nombre=TRIM(_nombre),apellido=TRIM(_apellido),correo=_correo WHERE id=_id; END$$

CREATE PROCEDURE sp_cambiar_estado_usuario(IN _id INT, IN _activo BOOLEAN)
BEGIN UPDATE usuarios SET activo=_activo WHERE id=_id; END$$

CREATE PROCEDURE sp_cambiar_rol_usuario(IN _id INT, IN _rol VARCHAR(20))
BEGIN IF _rol NOT IN ('admin','bodega','cajero') THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Rol invalido'; END IF; UPDATE usuarios SET rol=_rol WHERE id=_id; END$$

CREATE PROCEDURE sp_cambiar_contrasena(IN _id INT, IN _actual CHAR(64), IN _nueva CHAR(64), OUT _resultado TINYINT)
BEGIN
    IF EXISTS(SELECT 1 FROM usuarios WHERE id=_id AND password_hash=_actual AND activo=TRUE) THEN
        UPDATE usuarios SET password_hash=_nueva WHERE id=_id;
        SET _resultado=1;
    ELSE SET _resultado=0;
    END IF;
END$$

-- Inventario

CREATE PROCEDURE sp_actualizarstocklibro(IN _isbn VARCHAR(20), IN _stock_minimo INT, IN _activo BOOLEAN)
BEGIN
    IF _stock_minimo < 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El stock minimo no puede ser negativo'; END IF;
    UPDATE libros SET stock_minimo=_stock_minimo,activo=_activo WHERE isbn=_isbn;
END$$

CREATE PROCEDURE sp_registrar_ingreso_inventario(IN _isbn VARCHAR(20), IN _cantidad INT, IN _id_usuario INT, IN _observacion VARCHAR(255))
BEGIN
    DECLARE _isbn_bloqueado VARCHAR(20);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
    IF _cantidad <= 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La cantidad debe ser mayor a 0'; END IF;
    START TRANSACTION;
    SELECT isbn INTO _isbn_bloqueado FROM libros WHERE isbn=_isbn AND activo=TRUE FOR UPDATE;
    IF _isbn_bloqueado IS NULL THEN ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Libro inexistente o inactivo'; END IF;
    UPDATE libros SET stock_actual=stock_actual+_cantidad WHERE isbn=_isbn;
    INSERT INTO movimientos_inventario(isbn,tipo_movimiento,cantidad,id_usuario,observacion) VALUES(_isbn,'INGRESO',_cantidad,_id_usuario,_observacion);
    COMMIT;
END$$

CREATE PROCEDURE sp_registrar_salida_inventario(IN _isbn VARCHAR(20), IN _cantidad INT, IN _tipo VARCHAR(20), IN _id_usuario INT, IN _observacion VARCHAR(255))
BEGIN
    DECLARE _stock INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
    IF _cantidad <= 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La cantidad debe ser mayor a 0'; END IF;
    IF _tipo NOT IN ('MERMA','TRASLADO','AJUSTE') THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Tipo de salida invalido'; END IF;
    START TRANSACTION;
    SELECT stock_actual INTO _stock FROM libros WHERE isbn=_isbn AND activo=TRUE FOR UPDATE;
    IF _stock IS NULL THEN ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Libro inexistente o inactivo'; END IF;
    IF _stock < _cantidad THEN ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Stock insuficiente'; END IF;
    UPDATE libros SET stock_actual=stock_actual-_cantidad WHERE isbn=_isbn;
    INSERT INTO movimientos_inventario(isbn,tipo_movimiento,cantidad,id_usuario,observacion) VALUES(_isbn,_tipo,_cantidad,_id_usuario,_observacion);
    COMMIT;
END$$

CREATE PROCEDURE sp_listarmovimientosinventario()
BEGIN
    SELECT m.id_movimiento,m.isbn,l.titulo,m.tipo_movimiento,m.cantidad,m.fecha_movimiento,m.id_usuario,u.username,m.id_venta,m.nit_proveedor,m.observacion
    FROM movimientos_inventario m JOIN libros l ON l.isbn=m.isbn JOIN usuarios u ON u.id=m.id_usuario
    ORDER BY m.fecha_movimiento DESC;
END$$

CREATE PROCEDURE sp_movimientosporlibro(IN _isbn VARCHAR(20))
BEGIN
    SELECT id_movimiento,isbn,tipo_movimiento,cantidad,fecha_movimiento,id_usuario,id_venta,nit_proveedor,observacion
    FROM movimientos_inventario WHERE isbn=_isbn ORDER BY fecha_movimiento DESC;
END$$

-- Ventas: transaccion atomica (cabecera + detalle + stock)

CREATE PROCEDURE sp_registrar_venta(
    IN _id_usuario INT,
    IN _cui_cliente BIGINT,
    IN _descuento DECIMAL(10,2),
    IN _usuario_autoriza INT,
    IN _detalles JSON,
    OUT _id_venta INT
)
BEGIN
    DECLARE _i INT DEFAULT 0;
    DECLARE _n INT DEFAULT 0;
    DECLARE _isbn VARCHAR(20);
    DECLARE _cantidad INT;
    DECLARE _precio DECIMAL(10,2);
    DECLARE _stock INT;
    DECLARE _subtotal DECIMAL(10,2) DEFAULT 0;
    DECLARE _total DECIMAL(10,2) DEFAULT 0;
    DECLARE _rol VARCHAR(20);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    IF _descuento IS NULL OR _descuento < 0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Descuento invalido'; END IF;
    IF JSON_TYPE(_detalles) <> 'ARRAY' OR JSON_LENGTH(_detalles)=0 THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La venta debe contener al menos un libro'; END IF;

    SELECT rol INTO _rol FROM usuarios WHERE id=_id_usuario AND activo=TRUE;
    IF _rol IS NULL OR _rol <> 'cajero' THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El usuario no es un cajero activo'; END IF;

    IF _descuento > 0 THEN
        IF _usuario_autoriza IS NULL OR NOT EXISTS(SELECT 1 FROM usuarios WHERE id=_usuario_autoriza AND activo=TRUE AND rol='admin') THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El descuento requiere autorizacion de un administrador';
        END IF;
    ELSE SET _usuario_autoriza=NULL;
    END IF;

    START TRANSACTION;
    INSERT INTO ventas(subtotal,descuento,total,cui_cliente,id_usuario,usuario_autoriza_descuento)
    VALUES(0,0,0,_cui_cliente,_id_usuario,_usuario_autoriza);
    SET _id_venta=LAST_INSERT_ID();

    SET _n=JSON_LENGTH(_detalles);
    WHILE _i < _n DO
        SET _isbn=JSON_UNQUOTE(JSON_EXTRACT(_detalles,CONCAT('$[',_i,'].isbn')));
        SET _cantidad=CAST(JSON_UNQUOTE(JSON_EXTRACT(_detalles,CONCAT('$[',_i,'].cantidad'))) AS UNSIGNED);
        IF _isbn IS NULL OR _cantidad IS NULL OR _cantidad <= 0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Detalle de venta invalido';
        END IF;

        SELECT precio,stock_actual INTO _precio,_stock FROM libros WHERE isbn=_isbn AND activo=TRUE FOR UPDATE;
        IF _precio IS NULL THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Libro inexistente o inactivo'; END IF;
        IF _stock < _cantidad THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Stock insuficiente para uno de los libros'; END IF;

        INSERT INTO detalle_venta(id_venta,isbn,cantidad,precio_unitario,subtotal)
        VALUES(_id_venta,_isbn,_cantidad,_precio,_cantidad*_precio);
        UPDATE libros SET stock_actual=stock_actual-_cantidad WHERE isbn=_isbn;
        INSERT INTO movimientos_inventario(isbn,tipo_movimiento,cantidad,id_usuario,id_venta,observacion)
        VALUES(_isbn,'VENTA',_cantidad,_id_usuario,_id_venta,CONCAT('Venta #',_id_venta));
        SET _subtotal=_subtotal+(_cantidad*_precio);
        SET _i=_i+1;
    END WHILE;

    IF _descuento > _subtotal THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El descuento no puede superar el subtotal'; END IF;
    SET _total=_subtotal-_descuento;
    UPDATE ventas SET subtotal=_subtotal,descuento=_descuento,total=_total WHERE id_venta=_id_venta;
    COMMIT;
END$$

CREATE PROCEDURE sp_registrarventacajero(IN _subtotal DECIMAL(10,2), IN _descuento DECIMAL(10,2), IN _total DECIMAL(10,2), IN _cui_cliente BIGINT, IN _id_usuario INT, IN _usuario_autoriza INT)
BEGIN
    IF _total <> _subtotal-_descuento THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Total de venta inconsistente'; END IF;
    INSERT INTO ventas(subtotal,descuento,total,cui_cliente,id_usuario,usuario_autoriza_descuento) VALUES(_subtotal,_descuento,_total,_cui_cliente,_id_usuario,_usuario_autoriza);
END$$

CREATE PROCEDURE sp_listarventas()
BEGIN
    SELECT v.id_venta,v.fecha_venta,v.subtotal,v.descuento,v.total,v.estado,v.cui_cliente,v.id_usuario,u.username
    FROM ventas v JOIN usuarios u ON u.id=v.id_usuario ORDER BY v.fecha_venta DESC;
END$$

CREATE PROCEDURE sp_buscarventa(IN _id_venta INT)
BEGIN
    SELECT v.id_venta,v.fecha_venta,v.subtotal,v.descuento,v.total,v.estado,v.cui_cliente,v.id_usuario,v.usuario_autoriza_descuento,v.fecha_anulacion,v.usuario_anulacion,v.motivo_anulacion
    FROM ventas v WHERE v.id_venta=_id_venta;
END$$

CREATE PROCEDURE sp_listardetalleventa()
BEGIN SELECT id_detalle,id_venta,isbn,cantidad,precio_unitario,subtotal FROM detalle_venta ORDER BY id_venta; END$$

CREATE PROCEDURE sp_buscardetalleventa(IN _id INT)
BEGIN SELECT id_detalle,id_venta,isbn,cantidad,precio_unitario,subtotal FROM detalle_venta WHERE id_detalle=_id; END$$

CREATE PROCEDURE sp_insertardetalleventa(IN _id_venta INT, IN _isbn VARCHAR(20), IN _cantidad INT, IN _precio DECIMAL(10,2))
BEGIN
    INSERT INTO detalle_venta(id_venta,isbn,cantidad,precio_unitario,subtotal) VALUES(_id_venta,_isbn,_cantidad,_precio,_cantidad*_precio);
END$$

CREATE PROCEDURE sp_ventasdeldiaporusuario(IN _id_usuario INT)
BEGIN
    SELECT id_venta,fecha_venta,subtotal,descuento,total,estado FROM ventas
    WHERE id_usuario=_id_usuario AND fecha_venta >= CURDATE() AND fecha_venta < CURDATE()+INTERVAL 1 DAY
    ORDER BY fecha_venta DESC;
END$$

CREATE PROCEDURE sp_anularventa(IN _id_venta INT, IN _id_usuario_anula INT, IN _motivo VARCHAR(255))
BEGIN
    DECLARE _estado VARCHAR(20);
    DECLARE _isbn VARCHAR(20);
    DECLARE _cantidad INT;
    DECLARE _fin INT DEFAULT 0;
    DECLARE cur_det CURSOR FOR SELECT isbn,cantidad FROM detalle_venta WHERE id_venta=_id_venta;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET _fin=1;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

    START TRANSACTION;
    SELECT estado INTO _estado FROM ventas WHERE id_venta=_id_venta FOR UPDATE;
    IF _estado IS NULL THEN ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Venta no encontrada'; END IF;
    IF _estado <> 'COMPLETADA' THEN ROLLBACK; SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La venta ya fue anulada o devuelta'; END IF;

    UPDATE ventas SET estado='ANULADA',fecha_anulacion=CURRENT_TIMESTAMP,usuario_anulacion=_id_usuario_anula,motivo_anulacion=_motivo WHERE id_venta=_id_venta;
    OPEN cur_det;
    ciclo: LOOP
        FETCH cur_det INTO _isbn,_cantidad;
        IF _fin=1 THEN LEAVE ciclo; END IF;
        UPDATE libros SET stock_actual=stock_actual+_cantidad WHERE isbn=_isbn;
        INSERT INTO movimientos_inventario(isbn,tipo_movimiento,cantidad,id_usuario,id_venta,observacion)
        VALUES(_isbn,'DEVOLUCION',_cantidad,_id_usuario_anula,_id_venta,CONCAT('Anulacion venta #',_id_venta));
    END LOOP;
    CLOSE cur_det;
    COMMIT;
END$$

-- Proveedores

CREATE PROCEDURE sp_insertarproveedor(IN _nit VARCHAR(20), IN _nombre VARCHAR(100), IN _telefono VARCHAR(15), IN _direccion VARCHAR(100), IN _correo VARCHAR(100))
BEGIN INSERT INTO proveedores(nit_proveedor,nombre_proveedor,telefono_proveedor,direccion_proveedor,correo_proveedor) VALUES(_nit,TRIM(_nombre),_telefono,_direccion,_correo); END$$

CREATE PROCEDURE sp_listarproveedores()
BEGIN SELECT nit_proveedor,nombre_proveedor,telefono_proveedor,direccion_proveedor,correo_proveedor,activo FROM proveedores ORDER BY nombre_proveedor; END$$

CREATE PROCEDURE sp_buscarproveedor(IN _nit VARCHAR(20))
BEGIN SELECT nit_proveedor,nombre_proveedor,telefono_proveedor,direccion_proveedor,correo_proveedor,activo FROM proveedores WHERE nit_proveedor=_nit; END$$

CREATE PROCEDURE sp_actualizarproveedor(IN _nit VARCHAR(20), IN _nombre VARCHAR(100), IN _telefono VARCHAR(15), IN _direccion VARCHAR(100), IN _correo VARCHAR(100))
BEGIN UPDATE proveedores SET nombre_proveedor=TRIM(_nombre),telefono_proveedor=_telefono,direccion_proveedor=_direccion,correo_proveedor=_correo WHERE nit_proveedor=_nit; END$$

CREATE PROCEDURE sp_eliminarproveedor(IN _nit VARCHAR(20))
BEGIN UPDATE proveedores SET activo=FALSE WHERE nit_proveedor=_nit; END$$

DELIMITER ;

-- Vistas para dashboards y reportes

CREATE OR REPLACE VIEW vw_stock_critico AS
SELECT l.isbn,l.titulo,l.stock_actual,l.stock_minimo,c.nombre_categoria
FROM libros l JOIN categorias c ON c.id_categoria=l.id_categoria
WHERE l.activo=TRUE AND l.stock_actual <= l.stock_minimo;

CREATE OR REPLACE VIEW vw_lista_movimientos_inventario AS
SELECT m.id_movimiento,l.titulo,m.isbn,m.tipo_movimiento,m.cantidad,m.fecha_movimiento,u.username,m.id_venta,m.nit_proveedor,m.observacion
FROM movimientos_inventario m JOIN libros l ON l.isbn=m.isbn JOIN usuarios u ON u.id=m.id_usuario
ORDER BY m.fecha_movimiento DESC;

CREATE OR REPLACE VIEW vw_dashboard_admin AS
SELECT
    (SELECT COALESCE(SUM(total),0) FROM ventas WHERE estado='COMPLETADA') AS ventas_totales,
    (SELECT COUNT(*) FROM libros WHERE activo=TRUE) AS libros_activos,
    (SELECT COALESCE(SUM(stock_actual),0) FROM libros WHERE activo=TRUE) AS unidades_en_stock,
    (SELECT COUNT(*) FROM usuarios WHERE activo=TRUE) AS usuarios_activos,
    (SELECT COUNT(*) FROM libros WHERE activo=TRUE AND stock_actual <= stock_minimo) AS libros_stock_critico;

CREATE OR REPLACE VIEW vw_ventas_diarias AS
SELECT DATE(fecha_venta) fecha,COUNT(*) cantidad_ventas,COALESCE(SUM(subtotal),0) subtotal,COALESCE(SUM(descuento),0) descuentos,COALESCE(SUM(total),0) total
FROM ventas WHERE estado='COMPLETADA' GROUP BY DATE(fecha_venta);

CREATE OR REPLACE VIEW vw_libros_mas_vendidos AS
SELECT dv.isbn,l.titulo,SUM(dv.cantidad) unidades_vendidas,SUM(dv.subtotal) ingresos
FROM detalle_venta dv JOIN ventas v ON v.id_venta=dv.id_venta JOIN libros l ON l.isbn=dv.isbn
WHERE v.estado='COMPLETADA' GROUP BY dv.isbn,l.titulo ORDER BY unidades_vendidas DESC;

CREATE OR REPLACE VIEW vw_stock_valorizado AS
SELECT l.isbn,l.titulo,l.precio,l.stock_actual,(l.precio*l.stock_actual) valor_stock
FROM libros l WHERE l.activo=TRUE;
