package lib;

import com.mycompany.sistema.models.Empleado;
import com.mycompany.sistema.models.Producto;
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

public class SqlLib {

    private final String URL = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private final String USER = "admin_rest";
    private final String PASS = "rest123";

    

    public Map<Integer, String> obtenerEstadosMesas() {
        Map<Integer, String> listaMesas = new HashMap<>();
        String query = "SELECT id_mesa, estado FROM mesas";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
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
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
            System.out.println("Mesa " + idMesa + " actualizada con éxito.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public boolean isValidCredentials(String correo, String password) throws SQLException {
        String query = "SELECT password FROM usuarios WHERE correo = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
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
        String query = "SELECT r.nombre FROM usuarios u JOIN roles r ON u.rol_id = r.id WHERE u.correo = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
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
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
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

    public List<Producto> obtenerProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql =
            "SELECT p.id_producto, p.nombre, p.descripcion, p.precio, c.nombre AS categoria " +
            "FROM productos p JOIN categorias c ON p.id_categoria = c.id_categoria";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getString("categoria")
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
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Empleado> obtenerEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        String sql =
            "SELECT e.id_empleado, e.nombre, e.apellido, e.telefono, e.correo, " +
            "e.puesto, e.horario, e.estatus, e.salario, " +
            "COALESCE(GROUP_CONCAT(CONCAT(v.fecha_inicio, ' - ', v.fecha_fin) SEPARATOR ', '), 'Sin vacaciones') AS vacaciones " +
            "FROM empleados e " +
            "LEFT JOIN vacaciones v ON e.id_empleado = v.id_empleado " +
            "GROUP BY e.id_empleado";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
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
        String sql = "INSERT INTO empleados(nombre, apellido, telefono, correo, puesto, horario, estatus, salario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarEmpleado(Empleado e) {
        String sql = "UPDATE empleados SET nombre=?, apellido=?, telefono=?, correo=?, puesto=?, horario=?, estatus=?, salario=? WHERE id_empleado=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
    
    
    //CAMBIOS 
    public boolean registrarClienteNuevo(String usuario, String password) {
        try {
            // 'usuario' es el rol por defecto para los clientes
            String sql = "INSERT INTO usuarios (username, password, rol) VALUES (?, ?, 'usuario')";
            // Aquí usas tu lógica de conexión para ejecutar el INSERT
            // db.execute(sql, usuario, password); 
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void registrarHistorial(String usuario) {
        System.out.println("Simulación de historial para: " + usuario);
    }
}