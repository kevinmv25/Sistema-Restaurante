package lib;

import com.mycompany.sistema.models.Asistencia;
import com.mycompany.sistema.models.Empleado;
import com.mycompany.sistema.models.Producto;
import com.mycompany.sistema.models.Reservacion;
import com.mycompany.sistema.models.insumos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SqlLib {

    private final String URL = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private final String USER = "admin_rest";
    private final String PASS = "rest123";

    // ================= MESAS =================

    public Map<Integer, String> obtenerEstadosMesas() {
        Map<Integer, String> listaMesas = new HashMap<>();
        String query = "SELECT id_mesa, estado FROM mesas";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                int id = rs.getInt("id_mesa");
                String estado = rs.getString("estado");
                listaMesas.put(id, estado);
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return listaMesas;
    }

    public void actualizarEstadoMesa(int idMesa, String nuevoEstado) {
        String query = "UPDATE mesas SET estado = ? WHERE id_mesa = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
            System.out.println("Mesa " + idMesa + " actualizada con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sincronizarEstadoMesas() {
        String resetMesas = "UPDATE mesas SET estado = 'Disponible'";
        String ocuparReservadas =
            "UPDATE mesas m " +
            "JOIN reservaciones r ON m.id_mesa = r.id_mesa " +
            "SET m.estado = 'Reservada' " +
            "WHERE r.estatus IN ('Confirmada', 'Modificada') " +
            "AND r.fecha_reserva >= CURDATE()";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // 1. Todas disponibles
            stmt.executeUpdate(resetMesas);

            // 2. Reservar solo las mesas con reservaciones activas
            stmt.executeUpdate(ocuparReservadas);

            System.out.println("Estados de mesas sincronizados.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ================= USUARIOS / LOGIN ================= 

    public boolean isValidCredentials(String correo, String password) throws SQLException {
        String query = "SELECT password FROM usuarios WHERE correo = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword);
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    public String getRole(String correo) throws SQLException {
        String query =
            "SELECT r.nombre " +
            "FROM usuarios u " +
            "JOIN roles r ON u.rol_id = r.id " +
            "WHERE u.correo = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            throw e;
        }
        return "nil";
    }

    public boolean puedeLogin(String correo) {
        String query = "SELECT puede_login FROM usuarios WHERE correo = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("puede_login");
            }
        } catch (SQLException e) {
            System.err.println("Error validando acceso: " + e.getMessage());
        }
        return false;
    }

    public boolean registrarClienteNuevo(String nombre, String correo, String password) {
        String sql = "INSERT INTO usuarios (nombre, correo, password, rol_id, puede_login) VALUES (?, ?, ?, 6, TRUE)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void registrarHistorial(String correo) {
        String sql = "INSERT INTO historial_accesos (correo_usuario) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= PRODUCTOS =================

    public List<Producto> obtenerProductos() {

        List<Producto> lista = new ArrayList<>();

        String sql =
            "SELECT p.id_producto, p.nombre, p.descripcion, p.precio, c.nombre AS categoria " +
            "FROM productos p " +
            "JOIN categorias c ON p.id_categoria = c.id_categoria";

        try (
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {

            while (rs.next()) {

                Producto p = new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getDouble("precio")
             );

            lista.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void eliminarProducto(int idProducto) {

        String sql = "DELETE FROM productos WHERE id_producto=?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idProducto);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= EMPLEADOS =================

    public List<Empleado> obtenerEmpleados() {

        List<Empleado> lista = new ArrayList<>();

        String sql =
            "SELECT e.id_empleado, e.nombre, e.apellido, e.telefono, e.correo, " +
            "e.puesto, e.horario, e.estatus, e.salario, " +
            "COALESCE(GROUP_CONCAT(CONCAT(v.fecha_inicio, ' - ', v.fecha_fin) SEPARATOR ', '), 'Sin vacaciones') AS vacaciones " +
            "FROM empleados e " +
            "LEFT JOIN vacaciones v ON e.id_empleado = v.id_empleado " +
            "GROUP BY e.id_empleado";

        try (
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {

            while (rs.next()) {

                Empleado e = new Empleado(
                    rs.getInt("id_empleado"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("puesto"),
                    rs.getString("horario"),
                    rs.getString("estatus"),
                    rs.getString("vacaciones"),
                    rs.getDouble("salario")
                );

                lista.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void insertarEmpleado(Empleado e) {

        String sql =
            "INSERT INTO empleados(nombre, apellido, telefono, correo, puesto, horario, estatus, salario) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getTelefono());
            ps.setString(4, e.getCorreo());
            ps.setString(5, e.getPuesto());
            ps.setString(6, e.getHorario());
            ps.setString(7, e.getEstatus());
            ps.setDouble(8, e.getSalario());

            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void eliminarEmpleado(int id) {

        String sql = "DELETE FROM empleados WHERE id_empleado=?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarEmpleado(Empleado e) {

        String sql =
            "UPDATE empleados " +
            "SET nombre=?, apellido=?, telefono=?, correo=?, puesto=?, horario=?, estatus=?, salario=? " +
            "WHERE id_empleado=?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getTelefono());
            ps.setString(4, e.getCorreo());
            ps.setString(5, e.getPuesto());
            ps.setString(6, e.getHorario());
            ps.setString(7, e.getEstatus());
            ps.setDouble(8, e.getSalario());
            ps.setInt(9, e.getId());

            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Empleado buscarEmpleadoPorNombreCompleto(String nombreCompleto) {

        String sql =
            "SELECT e.id_empleado, e.nombre, e.apellido, e.telefono, e.correo, " +
            "e.puesto, e.horario, e.estatus, e.salario, " +
            "'Sin vacaciones' AS vacaciones " +
            "FROM empleados e " +
            "WHERE CONCAT(e.nombre, ' ', e.apellido) = ?";

        try (
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, nombreCompleto);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Empleado(
                    rs.getInt("id_empleado"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("telefono"),
                    rs.getString("correo"),
                    rs.getString("puesto"),
                    rs.getString("horario"),
                    rs.getString("estatus"),
                    rs.getString("vacaciones"),
                    rs.getDouble("salario")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ================= ASISTENCIAS =================

    public void insertarAsistencia(
        int idEmpleado,
        String fecha,
        String entrada,
        String salida,
        String estado,
        String horario
    ) {

        String sql =
            "INSERT INTO asistencias(id_empleado, fecha, hora_entrada, hora_salida, estado, horario) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idEmpleado);
            ps.setString(2, fecha);
            ps.setString(3, entrada);

            if (salida.isEmpty()) {
                ps.setNull(4, java.sql.Types.TIME);
            } else {
                ps.setString(4, salida);
            }

            ps.setString(5, estado);
            ps.setString(6, horario);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarAsistencia(int idAsistencia) {

        String sql = "DELETE FROM asistencias WHERE id_asistencia = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idAsistencia);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarAsistencia(
        int idAsistencia,
        String fecha,
        String entrada,
        String salida,
        String estado,
        String horario
    ) {

        String sql =
            "UPDATE asistencias " +
            "SET fecha = ?, hora_entrada = ?, hora_salida = ?, estado = ?, horario = ? " +
            "WHERE id_asistencia = ?";

        try (
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, fecha);
            ps.setString(2, entrada);

            if (salida.isEmpty()) {
                ps.setNull(3, java.sql.Types.TIME);
            } else {
                ps.setString(3, salida);
            }

            ps.setString(4, estado);
            ps.setString(5, horario);
            ps.setInt(6, idAsistencia);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Asistencia> obtenerAsistencias() {

        List<Asistencia> lista = new ArrayList<>();

        String sql =
            "SELECT a.id_asistencia, " +
            "CONCAT(e.nombre, ' ', e.apellido) AS empleado, " +
            "a.fecha, a.hora_entrada, a.hora_salida, " +
            "a.estado, a.horario " +
            "FROM asistencias a " +
            "INNER JOIN empleados e ON a.id_empleado = e.id_empleado";

        try (
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)
        ) {

            while (rs.next()) {

                Asistencia a = new Asistencia(
                    rs.getInt("id_asistencia"),
                    rs.getString("empleado"),
                    rs.getString("fecha"),
                    rs.getString("hora_entrada"),
                    rs.getString("hora_salida"),
                    rs.getString("estado"),
                    rs.getString("horario")
                );

                lista.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Asistencia> buscarAsistenciasPorFiltro(String filtro, String valor) {

        List<Asistencia> lista = new ArrayList<>();

        String sqlBase =
            "SELECT a.id_asistencia, " +
            "CONCAT(e.nombre, ' ', e.apellido) AS empleado, " +
            "a.fecha, a.hora_entrada, a.hora_salida, " +
            "a.estado, a.horario " +
            "FROM asistencias a " +
            "INNER JOIN empleados e ON a.id_empleado = e.id_empleado ";

        String condicion = "";

        if (filtro.equals("Fecha")) {

            condicion = "WHERE a.fecha = ?";

        } else if (filtro.equals("Día")) {

            condicion = "WHERE DAYNAME(a.fecha) = ?";

            if (valor.equalsIgnoreCase("Lunes")) {
                valor = "Monday";
            } else if (valor.equalsIgnoreCase("Martes")) {
                valor = "Tuesday";
            } else if (valor.equalsIgnoreCase("Miércoles") || valor.equalsIgnoreCase("Miercoles")) {
                valor = "Wednesday";
            } else if (valor.equalsIgnoreCase("Jueves")) {
                valor = "Thursday";
            } else if (valor.equalsIgnoreCase("Viernes")) {
                valor = "Friday";
            } else if (valor.equalsIgnoreCase("Sábado") || valor.equalsIgnoreCase("Sabado")) {
                valor = "Saturday";
            } else if (valor.equalsIgnoreCase("Domingo")) {
                valor = "Sunday";
            }

        } else if (
            filtro.equals("Hora") ||
            filtro.equals("Hora entrada") ||
            filtro.equals("Hora Entrada")
        ) {

            condicion = "WHERE a.hora_entrada = ?";

        } else if (
            filtro.equals("Hora salida") ||
            filtro.equals("Hora Salida")
        ) {

            condicion = "WHERE a.hora_salida = ?";

        } else if (filtro.equals("Empleado")) {

            condicion = "WHERE CONCAT(e.nombre, ' ', e.apellido) LIKE ?";
        }

        if (condicion.isEmpty()) {
            return lista;
        }

        String sql = sqlBase + condicion;

        try (
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            if (filtro.equals("Empleado")) {

                ps.setString(1, "%" + valor + "%");

            } else {

                ps.setString(1, valor);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Asistencia a = new Asistencia(
                    rs.getInt("id_asistencia"),
                    rs.getString("empleado"),
                    rs.getString("fecha"),
                    rs.getString("hora_entrada"),
                    rs.getString("hora_salida"),
                    rs.getString("estado"),
                    rs.getString("horario")
                );

                lista.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ================= RESERVACIONES =================
    
    public List<Reservacion> obtenerTodasLasReservaciones() {
        List<Reservacion> lista = new ArrayList<>();
        String sql = 
                "SELECT r.id_reservacion, r.fecha_reserva, r.hora_reserva, " +
                "r.id_mesa, r.num_personas, r.estatus, u.nombre " +
                "FROM reservaciones r " +
                "JOIN usuarios u ON r.id_usuario = u.id " +
                "WHERE r.estatus IN ('Confirmada', 'Reservada', 'En Tolerancia') " +
                "ORDER BY r.fecha_reserva ASC, r.hora_reserva ASC";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String idReserva = String.valueOf(rs.getInt("id_reservacion"));
                String fechaReserva = rs.getString("fecha_reserva");
                String horaReserva = rs.getString("hora_reserva");
                String mesaFormateada = "Mesa " + rs.getInt("id_mesa");
                int numPersonas = rs.getInt("num_personas");
                String nombreCliente = rs.getString("nombre");
                String estatus = rs.getString("estatus");
                
                Reservacion reserva = new Reservacion(
                    idReserva,
                    fechaReserva,
                    horaReserva,
                    mesaFormateada,
                    numPersonas,
                    estatus,
                    nombreCliente
                );
                lista.add(reserva);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las reservaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public ObservableList<Reservacion> getReservacionesPorUsuario(String correo) {
        ObservableList<Reservacion> lista = FXCollections.observableArrayList();

        String query = 
                "SELECT r.id_reservacion, r.fecha_reserva, r.hora_reserva, r.id_mesa, r.num_personas, r.estatus, r.modificada, u.nombre, " +
                "CASE " +
                "    WHEN r.estatus = 'Cancelada' THEN 'Cancelada' " +
                "    WHEN r.modificada = 1 OR r.estatus = 'Modificada' THEN 'Modificada' " +
                "    ELSE r.estatus " +
                "END AS estatus_final " +
                "FROM reservaciones r " +
                "JOIN usuarios u ON r.id_usuario = u.id " +
                "WHERE u.correo = ? " +
                "AND r.estatus NOT IN ('Cancelada', 'Pasada') " + 
                "ORDER BY r.fecha_reserva DESC, r.hora_reserva ASC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS); 
            PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String mesaFormateada = "Mesa " + rs.getInt("id_mesa");

                    lista.add(new Reservacion(
                        String.valueOf(rs.getInt("id_reservacion")),
                        rs.getString("fecha_reserva"),
                        rs.getString("hora_reserva"),
                        mesaFormateada,
                        rs.getInt("num_personas"),
                        rs.getString("estatus_final"),
                        rs.getString("nombre")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar historial: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrarNuevaReservacion(String correoUsuario, String fecha, String hora, int idMesa, int personas) {
        String sqlId = "SELECT id FROM usuarios WHERE correo = ?";
        String sqlInsert = "INSERT INTO reservaciones (id_usuario, fecha_reserva, hora_reserva, id_mesa, num_personas, estatus) VALUES (?, ?, ?, ?, ?, 'Confirmada')";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            int idUsuario = -1;
            try (PreparedStatement psId = conn.prepareStatement(sqlId)) {
                psId.setString(1, correoUsuario);
                try (ResultSet rs = psId.executeQuery()) {
                    if (rs.next()) {
                        idUsuario = rs.getInt("id");
                    }
                }
            }

            if (idUsuario == -1) return false;

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, idUsuario);
                psInsert.setString(2, fecha);
                psInsert.setString(3, hora);
                psInsert.setInt(4, idMesa);
                psInsert.setInt(5, personas);
                psInsert.executeUpdate();
            }

            actualizarEstadoMesa(idMesa, "Reservada");
            sincronizarEstadoMesas();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void actualizarEstatusReserva(String idReserva, String nuevoEstado) {
        String query = "UPDATE reservaciones SET estatus = ? WHERE id_reservacion = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, nuevoEstado);
            ps.setString(2, idReserva);
            ps.executeUpdate();

            System.out.println("Reserva " + idReserva + " actualizada a: " + nuevoEstado);
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estatus de la reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean cancelarReservacion(String idReservacion) {
        String sqlBuscarMesa = "SELECT id_mesa FROM reservaciones WHERE id_reservacion = ?";
        String sqlCancel = "UPDATE reservaciones SET estatus = 'Cancelada' WHERE id_reservacion = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            int idMesa = -1;
            try (PreparedStatement psBuscar = conn.prepareStatement(sqlBuscarMesa)) {
                psBuscar.setString(1, idReservacion);
                try (ResultSet rs = psBuscar.executeQuery()) {
                    if (rs.next()) {
                        idMesa = rs.getInt("id_mesa");
                    }
                }
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlCancel)) {
                ps.setString(1, idReservacion);
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    if (idMesa != -1) {
                        actualizarEstadoMesa(idMesa, "Disponible");
                        sincronizarEstadoMesas();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cancelar la reservación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean modificarReservacion(String idReservacion, String nuevaFecha, String nuevaHora, int nuevaMesa, int nuevasPersonas) {
        String buscarReserva = "SELECT id_usuario, id_mesa FROM reservaciones WHERE id_reservacion = ?";
        String marcarModificada = "UPDATE reservaciones SET estatus = 'Modificada' WHERE id_reservacion = ?";
        String insertarNueva = "INSERT INTO reservaciones (id_usuario, fecha_reserva, hora_reserva, id_mesa, num_personas, estatus) VALUES (?, ?, ?, ?, ?, 'Confirmada')";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            int idUsuario = -1;
            int mesaAnterior = -1;
            
            try (PreparedStatement ps = conn.prepareStatement(buscarReserva)) {
                ps.setString(1, idReservacion);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idUsuario = rs.getInt("id_usuario");
                    mesaAnterior = rs.getInt("id_mesa");
                }
            }
            
            if (idUsuario == -1) {
                return false;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(marcarModificada)) {
                ps.setString(1, idReservacion);
                ps.executeUpdate();
            }
            
            actualizarEstadoMesa(mesaAnterior, "Disponible");

            try (PreparedStatement ps = conn.prepareStatement(insertarNueva)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, nuevaFecha);
                ps.setString(3, nuevaHora);
                ps.setInt(4, nuevaMesa);
                ps.setInt(5, nuevasPersonas);
                ps.executeUpdate();
            }
                
            actualizarEstadoMesa(nuevaMesa, "Reservada");
            sincronizarEstadoMesas();
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al modificar la reservación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ================= LISTA DE ESPERA =================
    
    public List<Map<String, Object>> obtenerListaEsperaVigente() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_espera, nombre_cliente, num_personas FROM lista_espera WHERE estatus = 'En Espera' ORDER BY fecha_registro ASC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int posicion = 1;
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_espera", rs.getInt("id_espera"));
                fila.put("posicion", posicion++);
                fila.put("nombre", rs.getString("nombre_cliente"));
                fila.put("personas", rs.getInt("num_personas"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean registrarListaEspera(String nombre, int personas) {
        String sql = "INSERT INTO lista_espera (nombre_cliente, num_personas) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, personas);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean atenderClienteListaEspera(int idEspera, int idMesa) {
        String sqlEspera = "UPDATE lista_espera SET estatus = 'Atendido', id_mesa_asignada = ? WHERE id_espera = ?";
        String sqlMesa = "UPDATE mesas SET estado = 'Ocupada' WHERE id_mesa = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement psEspera = conn.prepareStatement(sqlEspera);
            PreparedStatement psMesa = conn.prepareStatement(sqlMesa)) {
            
            psEspera.setInt(1, idMesa);
            psEspera.setInt(2, idEspera);
            psEspera.executeUpdate();
            
            psMesa.setInt(1, idMesa);
            psMesa.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelarReservacion(String idReservacion) {
        String sqlBuscarMesa = "SELECT id_mesa FROM reservaciones WHERE id_reservacion = ?";
        String sqlCancel = "UPDATE reservaciones SET estatus = 'Cancelada' WHERE id_reservacion = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            int idMesa = -1;
            try (PreparedStatement psBuscar = conn.prepareStatement(sqlBuscarMesa)) {
                psBuscar.setString(1, idReservacion);
                try (ResultSet rs = psBuscar.executeQuery()) {
                    if (rs.next()) {
                        idMesa = rs.getInt("id_mesa");
                    }
                }
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlCancel)) {
                ps.setString(1, idReservacion);
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    if (idMesa != -1) {
                        actualizarEstadoMesa(idMesa, "Disponible");
                        sincronizarEstadoMesas();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cancelar la reservación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean modificarReservacion(String idReservacion, String nuevaFecha, String nuevaHora, int nuevaMesa, int nuevasPersonas) {
        String buscarReserva = "SELECT id_usuario, id_mesa FROM reservaciones WHERE id_reservacion = ?";
        String marcarModificada = "UPDATE reservaciones SET estatus = 'Modificada' WHERE id_reservacion = ?";
        String insertarNueva = "INSERT INTO reservaciones (id_usuario, fecha_reserva, hora_reserva, id_mesa, num_personas, estatus) VALUES (?, ?, ?, ?, ?, 'Confirmada')";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            int idUsuario = -1;
            int mesaAnterior = -1;
            
            try (PreparedStatement ps = conn.prepareStatement(buscarReserva)) {
                ps.setString(1, idReservacion);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idUsuario = rs.getInt("id_usuario");
                    mesaAnterior = rs.getInt("id_mesa");
                }
            }
            
            if (idUsuario == -1) {
                return false;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(marcarModificada)) {
                ps.setString(1, idReservacion);
                ps.executeUpdate();
            }
            
            actualizarEstadoMesa(mesaAnterior, "Disponible");

            try (PreparedStatement ps = conn.prepareStatement(insertarNueva)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, nuevaFecha);
                ps.setString(3, nuevaHora);
                ps.setInt(4, nuevaMesa);
                ps.setInt(5, nuevasPersonas);
                ps.executeUpdate();
            }
                
            actualizarEstadoMesa(nuevaMesa, "Reservada");
            sincronizarEstadoMesas();
            
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al modificar la reservación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ================= LISTA DE ESPERA =================
    
    public List<Map<String, Object>> obtenerListaEsperaVigente() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_espera, nombre_cliente, num_personas FROM lista_espera WHERE estatus = 'En Espera' ORDER BY fecha_registro ASC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int posicion = 1;
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_espera", rs.getInt("id_espera"));
                fila.put("posicion", posicion++);
                fila.put("nombre", rs.getString("nombre_cliente"));
                fila.put("personas", rs.getInt("num_personas"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean registrarListaEspera(String nombre, int personas) {
        String sql = "INSERT INTO lista_espera (nombre_cliente, num_personas) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, personas);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean atenderClienteListaEspera(int idEspera, int idMesa) {
        String sqlEspera = "UPDATE lista_espera SET estatus = 'Atendido', id_mesa_asignada = ? WHERE id_espera = ?";
        String sqlMesa = "UPDATE mesas SET estado = 'Ocupada' WHERE id_mesa = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement psEspera = conn.prepareStatement(sqlEspera);
            PreparedStatement psMesa = conn.prepareStatement(sqlMesa)) {
            
            psEspera.setInt(1, idMesa);
            psEspera.setInt(2, idEspera);
            psEspera.executeUpdate();
            
            psMesa.setInt(1, idMesa);
            psMesa.executeUpdate();
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
