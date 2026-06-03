package com.mycompany.sistema.services;

import com.mycompany.sistema.models.cajero.Cuenta;
import com.mycompany.sistema.models.cajero.DetallePedido;
import com.mycompany.sistema.models.cajero.Pago;
import com.mycompany.sistema.models.cajero.Pedido;
import com.mycompany.sistema.models.cajero.HistorialVenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Centraliza las operaciones de base de datos usadas por el módulo de cajero.
 *
 * <p>Esta clase funciona como capa de servicio para los casos de uso de
 * facturación, pagos, caja, descuentos, reembolsos e historial de ventas. Su
 * responsabilidad es separar la lógica de acceso a datos de los controladores
 * JavaFX, evitando que las pantallas trabajen directamente con consultas SQL.</p>
 *
 * <p>El servicio trabaja sobre tablas como <code>pedidos</code>,
 * <code>detalle_pedido</code>, <code>cuentas</code>, <code>pagos</code>,
 * <code>caja_turnos</code>, <code>movimientos_caja</code>,
 * <code>descuentos</code> y <code>comprobantes_cancelacion</code>.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see com.mycompany.sistema.models.cajero.Pedido
 * @see com.mycompany.sistema.models.cajero.Cuenta
 * @see com.mycompany.sistema.models.cajero.Pago
 * @see com.mycompany.sistema.models.cajero.HistorialVenta
 */
public class CajeroService {

    private final String URL = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private final String USER = "admin_rest";
    private final String PASS = "rest123";

    /**
    * Abre una conexión nueva con la base de datos del restaurante.
    *
    * <p>Se utiliza internamente por los métodos del servicio cada vez que necesitan
    * consultar o modificar información. La conexión se cierra automáticamente en
    * los bloques <code>try-with-resources</code>.</p>
    *
    * @return conexión activa hacia MySQL.
    * @throws SQLException si ocurre un problema al conectarse con la base de datos.
    */
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // =========================================================
    // PEDIDOS
    // =========================================================

    /**
    * Obtiene los pedidos que todavía están pendientes de cobro.
    *
    * <p>Este método alimenta principalmente la pantalla de facturación, donde el
    * cajero selecciona o toma un pedido en estado <code>Por pagar</code> para
    * generar una cuenta.</p>
    *
    * @return lista de pedidos pendientes; si no hay resultados, regresa una lista vacía.
    */
    public List<Pedido> obtenerPedidosPorPagar() {
        List<Pedido> lista = new ArrayList<>();

        String sql = """
            SELECT id_pedido, id_mesa, estado, fecha_creacion
            FROM pedidos
            WHERE estado = 'Por pagar'
            ORDER BY fecha_creacion DESC
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                Pedido pedido = new Pedido(
                    rs.getInt("id_pedido"),
                    rs.getInt("id_mesa"),
                    rs.getString("estado"),
                    rs.getString("fecha_creacion")
                );

                lista.add(pedido);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
    * Busca un pedido específico por su identificador.
    *
    * <p>Se usa cuando otra pantalla ya dejó seleccionado un pedido en el contexto
    * del cajero y la pantalla actual necesita recuperar sus datos completos.</p>
    *
    * @param idPedido identificador del pedido buscado.
    * @return pedido encontrado, o <code>null</code> si no existe.
    */
    public Pedido buscarPedidoPorId(int idPedido) {
        String sql = """
            SELECT id_pedido, id_mesa, estado, fecha_creacion
            FROM pedidos
            WHERE id_pedido = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idPedido);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Pedido(
                    rs.getInt("id_pedido"),
                    rs.getInt("id_mesa"),
                    rs.getString("estado"),
                    rs.getString("fecha_creacion")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * Recupera el desglose de productos de un pedido.
    *
    * <p>El detalle incluye producto, cantidad, precio unitario y subtotal. Esta
    * información se muestra en facturación y también sirve para aplicar descuentos
    * sobre productos específicos.</p>
    *
    * @param idPedido identificador del pedido del cual se consultará el detalle.
    * @return lista de productos asociados al pedido.
    */
    public List<DetallePedido> obtenerDetallePedido(int idPedido) {
        List<DetallePedido> lista = new ArrayList<>();

        String sql = """
            SELECT 
                dp.id_detalle,
                dp.id_pedido,
                dp.id_producto,
                p.nombre AS nombre_producto,
                dp.cantidad,
                dp.precio_unitario,
                dp.subtotal
            FROM detalle_pedido dp
            INNER JOIN productos p ON dp.id_producto = p.id_producto
            WHERE dp.id_pedido = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idPedido);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetallePedido detalle = new DetallePedido(
                    rs.getInt("id_detalle"),
                    rs.getInt("id_pedido"),
                    rs.getInt("id_producto"),
                    rs.getString("nombre_producto"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unitario"),
                    rs.getDouble("subtotal")
                );

                lista.add(detalle);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
    * Calcula el subtotal de un pedido sumando sus detalles.
    *
    * <p>El subtotal corresponde a la suma de los renglones registrados en
    * <code>detalle_pedido</code>, antes de impuestos y descuentos.</p>
    *
    * @param idPedido identificador del pedido.
    * @return subtotal calculado; si el pedido no tiene detalle, regresa <code>0</code>.
    */
    public double calcularSubtotalPedido(int idPedido) {
        String sql = """
            SELECT COALESCE(SUM(subtotal), 0) AS subtotal
            FROM detalle_pedido
            WHERE id_pedido = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idPedido);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("subtotal");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // CUENTAS / FACTURACIÓN
    // =========================================================

    /**
    * Obtiene la cuenta más reciente generada para un pedido.
    *
    * <p>Puede usarse para evitar duplicar cuentas o para continuar un flujo cuando
    * la cuenta ya fue generada desde facturación.</p>
    *
    * @param idPedido identificador del pedido relacionado.
    * @return cuenta encontrada, o <code>null</code> si el pedido aún no tiene cuenta.
    */
    public Cuenta obtenerCuentaPorPedido(int idPedido) {
        String sql = """
            SELECT id_cuenta, id_pedido, subtotal, impuestos, descuento, total,
                   estado, tipo_documento, formato, fecha_generacion
            FROM cuentas
            WHERE id_pedido = ?
            ORDER BY id_cuenta DESC
            LIMIT 1
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idPedido);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Cuenta(
                    rs.getInt("id_cuenta"),
                    rs.getInt("id_pedido"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("impuestos"),
                    rs.getDouble("descuento"),
                    rs.getDouble("total"),
                    rs.getString("estado"),
                    rs.getString("tipo_documento"),
                    rs.getString("formato"),
                    rs.getString("fecha_generacion")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * Genera una cuenta en estado pendiente de pago.
    *
    * <p>Este método implementa la parte central del caso de uso de facturación.
    * Calcula subtotal, impuestos cuando corresponda, total final y registra la
    * cuenta en la base de datos con estado <code>Por pagar</code>.</p>
    *
    * @param idPedido pedido que origina la cuenta.
    * @param tipoDocumento tipo de documento solicitado, como ticket o factura.
    * @param formato formato de salida del documento.
    * @param desglosarIVA indica si se debe calcular IVA sobre el subtotal.
    * @return cuenta generada, o <code>null</code> si ocurrió un error.
    */
    public Cuenta generarCuenta(int idPedido, String tipoDocumento, String formato, boolean desglosarIVA) {
        double subtotal = calcularSubtotalPedido(idPedido);
        double impuestos = desglosarIVA ? subtotal * 0.16 : 0;
        double total = subtotal + impuestos;

        String sql = """
            INSERT INTO cuentas
            (id_pedido, subtotal, impuestos, descuento, total, estado, tipo_documento, formato)
            VALUES (?, ?, ?, 0, ?, 'Por pagar', ?, ?)
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setInt(1, idPedido);
            ps.setDouble(2, subtotal);
            ps.setDouble(3, impuestos);
            ps.setDouble(4, total);
            ps.setString(5, tipoDocumento);
            ps.setString(6, formato);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                int idCuenta = keys.getInt(1);
                return obtenerCuentaPorId(idCuenta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * Busca una cuenta por su identificador.
    *
    * @param idCuenta identificador de la cuenta.
    * @return cuenta encontrada, o <code>null</code> si no existe.
    */
    public Cuenta obtenerCuentaPorId(int idCuenta) {
        String sql = """
            SELECT id_cuenta, id_pedido, subtotal, impuestos, descuento, total,
                   estado, tipo_documento, formato, fecha_generacion
            FROM cuentas
            WHERE id_cuenta = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idCuenta);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Cuenta(
                    rs.getInt("id_cuenta"),
                    rs.getInt("id_pedido"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("impuestos"),
                    rs.getDouble("descuento"),
                    rs.getDouble("total"),
                    rs.getString("estado"),
                    rs.getString("tipo_documento"),
                    rs.getString("formato"),
                    rs.getString("fecha_generacion")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * Obtiene todas las cuentas pendientes de pago.
    *
    * <p>Este método es usado por las pantallas de pagos y descuentos, ya que ambas
    * operaciones solo deben trabajar con cuentas que aún no han sido cobradas.</p>
    *
    * @return lista de cuentas en estado <code>Por pagar</code>.
    */
    public List<Cuenta> obtenerCuentasPorPagar() {
        List<Cuenta> lista = new ArrayList<>();

        String sql = """
            SELECT id_cuenta, id_pedido, subtotal, impuestos, descuento, total,
                   estado, tipo_documento, formato, fecha_generacion
            FROM cuentas
            WHERE estado = 'Por pagar'
            ORDER BY fecha_generacion DESC
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {
                Cuenta cuenta = new Cuenta(
                    rs.getInt("id_cuenta"),
                    rs.getInt("id_pedido"),
                    rs.getDouble("subtotal"),
                    rs.getDouble("impuestos"),
                    rs.getDouble("descuento"),
                    rs.getDouble("total"),
                    rs.getString("estado"),
                    rs.getString("tipo_documento"),
                    rs.getString("formato"),
                    rs.getString("fecha_generacion")
                );

                lista.add(cuenta);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================================================
    // CAJA
    // =========================================================

    /**
    * Abre un nuevo turno de caja.
    *
    * <p>El monto inicial representa el fondo con el que inicia el cajero. No es un
    * límite de cobro, sino parte del total esperado al momento de cerrar caja.</p>
    *
    * @param montoInicial fondo inicial registrado por el cajero.
    * @return identificador del turno creado; regresa <code>-1</code> si no pudo abrirse.
    */
    public int abrirCaja(double montoInicial) {
        String sql = """
            INSERT INTO caja_turnos (monto_inicial, estado)
            VALUES (?, 'Abierta')
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setDouble(1, montoInicial);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                return keys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
    * Consulta si existe un turno de caja abierto.
    *
    * <p>Los pagos y cancelaciones necesitan un turno abierto para poder registrar
    * movimientos de caja correctamente.</p>
    *
    * @return identificador del turno abierto, o <code>null</code> si no hay caja abierta.
    */
    public Integer obtenerTurnoAbierto() {
        String sql = """
            SELECT id_turno
            FROM caja_turnos
            WHERE estado = 'Abierta'
            ORDER BY fecha_apertura DESC
            LIMIT 1
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt("id_turno");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * Calcula el total esperado de un turno de caja.
    *
    * <p>El total esperado se obtiene con la regla:
    * <code>total esperado = monto inicial + pagos - cancelaciones</code>.
    * Este valor se compara contra el conteo físico al cerrar caja.</p>
    *
    * @param idTurno identificador del turno de caja.
    * @return total esperado para el cierre del turno.
    */
    public double calcularTotalEsperadoTurno(int idTurno) {
        String sql = """
            SELECT 
                ct.monto_inicial + COALESCE(SUM(
                    CASE 
                        WHEN mc.tipo IN ('Pago', 'Entrada') THEN mc.monto
                        WHEN mc.tipo IN ('Reembolso', 'Cancelación') THEN -mc.monto
                        ELSE 0
                    END
                ), 0) AS total
            FROM caja_turnos ct
            LEFT JOIN movimientos_caja mc ON ct.id_turno = mc.id_turno
            WHERE ct.id_turno = ?
            GROUP BY ct.id_turno, ct.monto_inicial
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idTurno);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
    * Cierra el turno de caja actual registrando el conteo físico.
    *
    * <p>El método calcula la diferencia entre el total físico y el total esperado.
    * Si existe sobrante o faltante, la justificación queda guardada para fines de
    * revisión o auditoría.</p>
    *
    * @param idTurno identificador del turno que se va a cerrar.
    * @param totalFisico cantidad contada físicamente por el cajero.
    * @param justificacion explicación del sobrante o faltante, si aplica.
    * @return <code>true</code> si el cierre se registró correctamente.
    */
    public boolean cerrarCaja(int idTurno, double totalFisico, String justificacion) {
        double totalEsperado = calcularTotalEsperadoTurno(idTurno);
        double diferencia = totalFisico - totalEsperado;

        String sql = """
            UPDATE caja_turnos
            SET fecha_cierre = NOW(),
                total_esperado = ?,
                total_fisico = ?,
                diferencia = ?,
                justificacion = ?,
                estado = 'Cerrada'
            WHERE id_turno = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setDouble(1, totalEsperado);
            ps.setDouble(2, totalFisico);
            ps.setDouble(3, diferencia);
            ps.setString(4, justificacion);
            ps.setInt(5, idTurno);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
    * Verifica si todavía hay cuentas pendientes de cobro.
    *
    * <p>Esta validación impide cerrar caja mientras existan cuentas en estado
    * <code>Por pagar</code>, de acuerdo con el flujo del corte de caja.</p>
    *
    * @return <code>true</code> si existen cuentas pendientes; de lo contrario, <code>false</code>.
    */
    public boolean existenCuentasPendientes() {
        String sql = """
            SELECT COUNT(*) AS total
            FROM cuentas
            WHERE estado = 'Por pagar'
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    // =========================================================
    // PAGOS
    // =========================================================

    /**
    * Registra el pago de una cuenta pendiente.
    *
    * <p>Al registrar el pago, también se actualiza la cuenta a estado
    * <code>Pagada</code>, se libera la mesa del pedido y se agrega el movimiento
    * correspondiente al turno de caja abierto.</p>
    *
    * @param idCuenta cuenta que será pagada.
    * @param metodoPago método seleccionado por el cajero.
    * @param monto monto cobrado al cliente.
    * @return pago registrado, o <code>null</code> si no pudo procesarse.
    */
    public Pago registrarPago(int idCuenta, String metodoPago, double monto) {
        Integer idTurno = obtenerTurnoAbierto();

        if (idTurno == null) {
            System.err.println("No hay una caja abierta.");
            return null;
        }

        String sqlPago = """
            INSERT INTO pagos (id_cuenta, id_turno, metodo_pago, monto, estado)
            VALUES (?, ?, ?, ?, 'Pagado')
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sqlPago, Statement.RETURN_GENERATED_KEYS)
        ) {

            ps.setInt(1, idCuenta);
            ps.setInt(2, idTurno);
            ps.setString(3, metodoPago);
            ps.setDouble(4, monto);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                int idPago = keys.getInt(1);

                actualizarCuentaPagada(idCuenta);
                registrarMovimientoCaja(idTurno, idPago, "Pago", monto, "Pago registrado");
                liberarMesaDelPedidoPorCuenta(idCuenta);

                return obtenerPagoPorId(idPago);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * Cambia una cuenta a estado pagado.
    *
    * @param idCuenta identificador de la cuenta que será marcada como pagada.
    */
    private void actualizarCuentaPagada(int idCuenta) {
        String sql = """
            UPDATE cuentas
            SET estado = 'Pagada'
            WHERE id_cuenta = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idCuenta);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Libera la mesa relacionada con una cuenta pagada.
    *
    * <p>También actualiza el pedido asociado para dejarlo en estado
    * <code>Pagado</code>. Esto conecta el flujo de pago con la operación de mesas
    * del restaurante.</p>
    *
    * @param idCuenta cuenta usada para localizar el pedido y la mesa relacionados.
    */
    private void liberarMesaDelPedidoPorCuenta(int idCuenta) {
        String sql = """
            UPDATE mesas m
            INNER JOIN pedidos p ON m.id_mesa = p.id_mesa
            INNER JOIN cuentas c ON p.id_pedido = c.id_pedido
            SET m.estado = 'Disponible',
                p.estado = 'Pagado'
            WHERE c.id_cuenta = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idCuenta);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Registra un movimiento dentro del turno de caja.
    *
    * <p>Los movimientos permiten calcular el total esperado de caja y alimentar el
    * historial de ventas. Pueden representar pagos, entradas, cancelaciones o
    * reembolsos.</p>
    *
    * @param idTurno turno al que pertenece el movimiento.
    * @param idPago pago relacionado con el movimiento.
    * @param tipo tipo de movimiento registrado.
    * @param monto importe del movimiento.
    * @param descripcion explicación breve de la operación.
    */
    private void registrarMovimientoCaja(int idTurno, int idPago, String tipo,
            double monto, String descripcion) {
        String sql = """
            INSERT INTO movimientos_caja (id_turno, id_pago, tipo, monto, descripcion)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idTurno);
            ps.setInt(2, idPago);
            ps.setString(3, tipo);
            ps.setDouble(4, monto);
            ps.setString(5, descripcion);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
    * Busca un pago por su identificador.
    *
    * @param idPago identificador del pago.
    * @return pago encontrado, o <code>null</code> si no existe.
    */
    public Pago obtenerPagoPorId(int idPago) {
        String sql = """
            SELECT id_pago, id_cuenta, id_turno, metodo_pago, monto, estado, fecha_pago
            FROM pagos
            WHERE id_pago = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idPago);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Pago(
                    rs.getInt("id_pago"),
                    rs.getInt("id_cuenta"),
                    rs.getInt("id_turno"),
                    rs.getString("metodo_pago"),
                    rs.getDouble("monto"),
                    rs.getString("estado"),
                    rs.getString("fecha_pago")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
    * Busca un pago registrado en el sistema.
    *
    * <p>Este método existe para que los controladores puedan consultar pagos sin
    * depender directamente del nombre interno usado por el servicio.</p>
    *
    * @param idPago identificador del pago buscado.
    * @return pago encontrado, o <code>null</code> si no existe.
    * @see #obtenerPagoPorId(int)
    */
    public Pago buscarPagoPorId(int idPago) {
        return obtenerPagoPorId(idPago);
    }
    
    // =========================================================
    // DESCUENTOS
    // =========================================================

    /**
    * Aplica un descuento sobre una cuenta o sobre un producto específico.
    *
    * <p>El descuento puede calcularse como porcentaje o como monto fijo. Después de
    * registrar la rebaja, la cuenta se recalcula para reflejar el nuevo total que
    * deberá pagar el cliente.</p>
    *
    * @param idCuenta cuenta sobre la que se aplicará el descuento.
    * @param idProducto producto afectado, o <code>null</code> si aplica a toda la cuenta.
    * @param tipo tipo de descuento: porcentaje o monto fijo.
    * @param valor valor capturado por el cajero.
    * @param motivo justificación de la rebaja.
    * @param autorizado indica si la operación requirió autorización de gerente.
    * @return <code>true</code> si el descuento se aplicó correctamente.
    */
    public boolean aplicarDescuento(int idCuenta, Integer idProducto, String tipo, double valor, String motivo, boolean autorizado) {
        Cuenta cuenta = obtenerCuentaPorId(idCuenta);

        if (cuenta == null) {
            return false;
        }

        double base = cuenta.getSubtotal() + cuenta.getImpuestos();
        double montoDescontado;

        if (tipo.equalsIgnoreCase("Porcentaje")) {
            montoDescontado = base * (valor / 100.0);
        } else {
            montoDescontado = valor;
        }

        if (montoDescontado <= 0 || montoDescontado > base) {
            return false;
        }

        String sqlDescuento = """
            INSERT INTO descuentos
            (id_cuenta, id_producto, tipo, valor, motivo, monto_descontado, autorizado_por_gerente)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sqlDescuento)
        ) {

            ps.setInt(1, idCuenta);

            if (idProducto == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, idProducto);
            }

            ps.setString(3, tipo);
            ps.setDouble(4, valor);
            ps.setString(5, motivo);
            ps.setDouble(6, montoDescontado);
            ps.setBoolean(7, autorizado);

            ps.executeUpdate();

            return recalcularCuentaConDescuentos(idCuenta);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
    * Recalcula el total de una cuenta después de aplicar descuentos.
    *
    * <p>La regla usada es:
    * <code>total = subtotal + impuestos - descuentos</code>.</p>
    *
    * @param idCuenta cuenta que será recalculada.
    * @return <code>true</code> si la cuenta fue actualizada correctamente.
    */
    private boolean recalcularCuentaConDescuentos(int idCuenta) {
        String sql = """
            UPDATE cuentas c
            SET 
                c.descuento = (
                    SELECT COALESCE(SUM(d.monto_descontado), 0)
                    FROM descuentos d
                    WHERE d.id_cuenta = c.id_cuenta
                ),
                c.total = (c.subtotal + c.impuestos) - (
                    SELECT COALESCE(SUM(d.monto_descontado), 0)
                    FROM descuentos d
                    WHERE d.id_cuenta = c.id_cuenta
                )
            WHERE c.id_cuenta = ?
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idCuenta);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================================================
    // REEMBOLSOS / CANCELACIONES
    // =========================================================

    /**
    * Obtiene los pagos registrados durante el turno de caja abierto.
    *
    * <p>Se usa en la pantalla de reembolsos para permitir que el cajero solo pueda
    * cancelar operaciones del turno actual.</p>
    *
    * @return lista de pagos del turno abierto.
    */
    public List<Pago> obtenerPagosDelTurnoActual() {
        List<Pago> lista = new ArrayList<>();

        Integer idTurno = obtenerTurnoAbierto();

        if (idTurno == null) {
            return lista;
        }

        String sql = """
            SELECT id_pago, id_cuenta, id_turno, metodo_pago, monto, estado, fecha_pago
            FROM pagos
            WHERE id_turno = ?
            AND estado = 'Pagado'
            ORDER BY fecha_pago DESC
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idTurno);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Pago(
                        rs.getInt("id_pago"),
                        rs.getInt("id_cuenta"),
                        rs.getInt("id_turno"),
                        rs.getString("metodo_pago"),
                        rs.getDouble("monto"),
                        rs.getString("estado"),
                        rs.getString("fecha_pago")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
    * Cancela un pago registrado durante el turno actual.
    *
    * <p>Esta operación regresa la cuenta a estado <code>Por pagar</code>, vuelve a
    * marcar el pedido como pendiente, coloca la mesa como ocupada y registra un
    * movimiento de cancelación en caja.</p>
    *
    * <p>Si el pago pertenece a un turno cerrado o diferente, la operación se
    * rechaza para conservar la consistencia del corte de caja.</p>
    *
    * @param idPago identificador del pago que será cancelado.
    * @param motivo justificación capturada por el cajero.
    * @return <code>true</code> si el pago fue cancelado correctamente.
    */
    public boolean cancelarPago(int idPago, String motivo) {
        Integer turnoAbierto = obtenerTurnoAbierto();

        if (turnoAbierto == null) {
            return false;
        }

        Pago pago = obtenerPagoPorId(idPago);

        if (pago == null) {
            return false;
        }

        if (pago.getIdTurno() != turnoAbierto) {
            return false;
        }

        if (!pago.getEstado().equalsIgnoreCase("Pagado")) {
            return false;
        }

        String sqlPago = """
            UPDATE pagos
            SET estado = 'Cancelado'
            WHERE id_pago = ?
        """;

        String sqlComprobante = """
            INSERT INTO comprobantes_cancelacion
            (id_pago, motivo, monto)
            VALUES (?, ?, ?)
        """;

        String sqlCuenta = """
            UPDATE cuentas
            SET estado = 'Por pagar'
            WHERE id_cuenta = ?
        """;

        String sqlPedidoMesa = """
            UPDATE mesas m
            INNER JOIN pedidos p ON m.id_mesa = p.id_mesa
            INNER JOIN cuentas c ON p.id_pedido = c.id_pedido
            SET 
                p.estado = 'Por pagar',
                m.estado = 'Ocupada'
            WHERE c.id_cuenta = ?
        """;

        try (Connection conn = conectar()) {

            conn.setAutoCommit(false);

            try (
                PreparedStatement psPago = conn.prepareStatement(sqlPago);
                PreparedStatement psComprobante = conn.prepareStatement(sqlComprobante);
                PreparedStatement psCuenta = conn.prepareStatement(sqlCuenta);
                PreparedStatement psPedidoMesa = conn.prepareStatement(sqlPedidoMesa)
            ) {

                psPago.setInt(1, idPago);
                psPago.executeUpdate();

                psComprobante.setInt(1, idPago);
                psComprobante.setString(2, motivo);
                psComprobante.setDouble(3, pago.getMonto());
                psComprobante.executeUpdate();

                psCuenta.setInt(1, pago.getIdCuenta());
                psCuenta.executeUpdate();

                psPedidoMesa.setInt(1, pago.getIdCuenta());
                psPedidoMesa.executeUpdate();

                registrarMovimientoCaja(turnoAbierto, idPago, "Cancelación", pago.getMonto(), "Cancelación de pago: " + motivo);

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    /**
    * Obtiene el historial de ventas y movimientos de caja.
    *
    * <p>La consulta combina movimientos de caja con pagos y cuentas para mostrar
    * una vista más clara del turno. Puede filtrarse por tipo de movimiento, como
    * <code>Pago</code>, <code>Cancelación</code> o <code>Todos</code>.</p>
    *
    * @param filtro tipo de movimiento que se desea consultar.
    * @return lista de movimientos encontrados.
    */
    public List<HistorialVenta> obtenerHistorialMovimientos(String filtro) {
        List<HistorialVenta> lista = new ArrayList<>();

        Integer idTurno = obtenerTurnoAbierto();

        String condicion = "";

        if (filtro != null && !filtro.equalsIgnoreCase("Todos")) {
            condicion = " AND mc.tipo = ? ";
        }

        String sql = """
            SELECT 
                mc.id_movimiento,
                mc.id_turno,
                mc.id_pago,
                mc.tipo,
                mc.monto,
                mc.descripcion,
                mc.fecha,
                COALESCE(p.id_cuenta, 0) AS id_cuenta,
                COALESCE(p.metodo_pago, 'N/A') AS metodo_pago,
                COALESCE(p.estado, 'N/A') AS estado_pago
            FROM movimientos_caja mc
            LEFT JOIN pagos p ON mc.id_pago = p.id_pago
            WHERE 1 = 1
        """ + condicion + """
            ORDER BY mc.fecha DESC
        """;

        try (
            Connection conn = conectar();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            if (filtro != null && !filtro.equalsIgnoreCase("Todos")) {
                ps.setString(1, filtro);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HistorialVenta h = new HistorialVenta(
                        rs.getInt("id_movimiento"),
                        rs.getInt("id_turno"),
                        rs.getInt("id_pago"),
                        rs.getString("tipo"),
                        rs.getDouble("monto"),
                        rs.getString("descripcion"),
                        rs.getString("fecha"),
                        rs.getInt("id_cuenta"),
                        rs.getString("metodo_pago"),
                        rs.getString("estado_pago")
                );

                lista.add(h);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}