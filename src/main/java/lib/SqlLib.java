
package lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 *Esta clase maneja la conexión con la base de datos
 * @author juego
 */
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

            stmt.executeUpdate(query); // IMPORTANTE: Usar executeUpdate
            System.out.println("Mesa " + idMesa + " actualizada con éxito.");

        } catch (SQLException e) {
            e.printStackTrace(); // Esto te dirá en la consola si falló la conexión o el SQL
        }
    }
}
