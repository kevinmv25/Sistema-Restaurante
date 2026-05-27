package com.mycompany.sistema.services;

import com.mycompany.sistema.models.cajero.Cuenta;
import com.mycompany.sistema.models.cajero.DetallePedido;
import com.mycompany.sistema.models.cajero.Pago;
import com.mycompany.sistema.models.cajero.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CajeroService {

    private final String URL = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private final String USER = "admin_rest";
    private final String PASS = "rest123";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // =========================================================
    // PEDIDOS
    // =========================================================

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

    private void registrarMovimientoCaja(int idTurno, int idPago, String tipo, double monto, String descripcion) {
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

    public Pago buscarPagoPorId(int idPago) {
        return obtenerPagoPorId(idPago);
    }
    
    // =========================================================
    // DESCUENTOS
    // =========================================================

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
}