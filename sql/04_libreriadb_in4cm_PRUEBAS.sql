-- Página Viva | Pruebas automáticas no destructivas
-- Ejecutar después de 01, 02 y 03.
USE libreriadb_in4cm;

DELIMITER $$
DROP PROCEDURE IF EXISTS sp_validar_instalacion_bd$$
CREATE PROCEDURE sp_validar_instalacion_bd()
BEGIN
    DECLARE _cantidad INT DEFAULT 0;

    SELECT COUNT(*) INTO _cantidad
    FROM information_schema.tables
    WHERE table_schema=DATABASE() AND table_type='BASE TABLE';
    IF _cantidad<18 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Faltan tablas del modelo Página Viva';
    END IF;

    SELECT COUNT(*) INTO _cantidad
    FROM information_schema.routines
    WHERE routine_schema=DATABASE() AND routine_type='PROCEDURE';
    IF _cantidad<50 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Faltan procedimientos almacenados';
    END IF;

    SELECT COUNT(*) INTO _cantidad
    FROM information_schema.views
    WHERE table_schema=DATABASE();
    IF _cantidad<10 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Faltan vistas de reportes o compatibilidad';
    END IF;

    IF EXISTS(SELECT 1 FROM libros WHERE precio<0 OR costo_promedio<0 OR stock_actual<0 OR stock_minimo<0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Existen libros con valores inválidos';
    END IF;

    IF EXISTS(SELECT 1 FROM ventas WHERE subtotal<0 OR descuento<0 OR descuento>subtotal OR total<>subtotal-descuento) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Existen ventas con totales inconsistentes';
    END IF;

    IF EXISTS(SELECT 1 FROM detalle_venta WHERE cantidad<=0 OR cantidad_devuelta<0 OR cantidad_devuelta>cantidad OR subtotal<>cantidad*precio_unitario) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Existen detalles de venta inconsistentes';
    END IF;

    IF EXISTS(SELECT 1 FROM detalle_compra WHERE cantidad<=0 OR subtotal<>cantidad*costo_unitario) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Existen detalles de compra inconsistentes';
    END IF;

    IF EXISTS(SELECT 1 FROM usuarios WHERE rol NOT IN ('admin','bodega','cajero')) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Existe un rol no admitido';
    END IF;

    IF NOT EXISTS(
        SELECT 1 FROM information_schema.routines
        WHERE routine_schema=DATABASE() AND routine_name='sp_devolverventa'
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Falta sp_devolverventa requerido por VentaDAOImpl';
    END IF;

    IF NOT EXISTS(
        SELECT 1 FROM information_schema.columns
        WHERE table_schema=DATABASE() AND table_name='ventas' AND column_name='cui_cliente'
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Falta ventas.cui_cliente requerido por VentaDAOImpl';
    END IF;

    SELECT COUNT(*) INTO _cantidad FROM libros;
    IF _cantidad<20 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La carga demo debe contener al menos 20 libros';
    END IF;

    SELECT COUNT(*) INTO _cantidad FROM clientes;
    IF _cantidad<15 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La carga demo debe contener al menos 15 clientes';
    END IF;

    IF NOT EXISTS(SELECT 1 FROM libros WHERE activo=TRUE AND stock_actual<=stock_minimo) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Se esperaba al menos un libro en stock crítico para las pruebas';
    END IF;

    SELECT 'OK' AS estado,
           'La estructura, restricciones, vistas y procedimientos principales están instalados.' AS resultado,
           (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE() AND table_type='BASE TABLE') AS tablas,
           (SELECT COUNT(*) FROM information_schema.routines WHERE routine_schema=DATABASE() AND routine_type='PROCEDURE') AS procedimientos,
           (SELECT COUNT(*) FROM information_schema.views WHERE table_schema=DATABASE()) AS vistas;
END$$
DELIMITER ;

CALL sp_validar_instalacion_bd();
DROP PROCEDURE sp_validar_instalacion_bd;

-- Resultados funcionales para revisión visual.
CALL sp_listarstockcritico();
CALL sp_dashboardadmin();
CALL sp_reporte_stock_valorizado();

-- Verificación de los tres accesos de prueba usados por la aplicación.
CALL sp_iniciar_sesion('admin1',SHA2('admin123',256));
CALL sp_iniciar_sesion('bodega1',SHA2('bodega123',256));
CALL sp_iniciar_sesion('cajero1',SHA2('cajero123',256));

-- La consulta del cajero ya devuelve cui_cliente e id_usuario.
CALL sp_ventasdeldiaporusuario((SELECT id FROM usuarios WHERE username='cajero1' LIMIT 1));

SELECT COUNT(*) AS total_libros FROM libros;
SELECT COUNT(*) AS total_clientes FROM clientes;
SELECT COUNT(*) AS stock_critico FROM libros WHERE activo=TRUE AND stock_actual<=stock_minimo;
