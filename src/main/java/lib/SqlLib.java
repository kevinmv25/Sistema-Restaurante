package lib;


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
        String query = "UPDATE mesas SET estado = '" + nuevoEstado + "' WHERE id_mesa = " + idMesa;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
            System.out.println("Mesa " + idMesa + " actualizada con éxito.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidCredentials(String correo, String password) {

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
            System.err.println("Error en login: " + e.getMessage());
        }

        return false;
    }

    public String getRole(String correo) {

        String query = "SELECT r.nombre FROM usuarios u " +
                       "JOIN roles r ON u.rol_id = r.id " +
                       "WHERE u.correo = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo rol: " + e.getMessage());
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
            "FROM productos p " +
            "JOIN categorias c ON p.id_categoria = c.id_categoria";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getString("categoria") // nombre de la categoría
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

}
