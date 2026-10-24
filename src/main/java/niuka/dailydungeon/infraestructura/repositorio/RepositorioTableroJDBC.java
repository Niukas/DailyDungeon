package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioTablero;
import niuka.dailydungeon.dominio.modelo.Tablero;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RepositorioTableroJDBC implements RepositorioTablero {

    private static final String ERROR_BD = "Error al acceder a la tabla tablero";

    private final ConexionBD conexionBD;

    public RepositorioTableroJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public List<Tablero> listarPorHeroe(int idHeroe) {
        String sql = "SELECT id_tablero, nombre FROM tablero WHERE id_heroe = ? AND eliminado = FALSE ORDER BY id_tablero";
        List<Tablero> tableros = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idHeroe);
            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    tableros.add(new Tablero(resultado.getInt("id_tablero"), idHeroe, resultado.getString("nombre")));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
        return tableros;
    }

    @Override
    public Tablero guardar(Tablero tablero) {
        String sql = "INSERT INTO tablero (id_heroe, nombre) VALUES (?, ?)";
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement insercion = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            insercion.setInt(1, tablero.idHeroe());
            insercion.setString(2, tablero.nombre());
            insercion.executeUpdate();
            try (ResultSet claves = insercion.getGeneratedKeys()) {
                claves.next();
                return new Tablero(claves.getInt(1), tablero.idHeroe(), tablero.nombre());
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public void renombrar(int idTablero, String nuevoNombre) {
        String sql = "UPDATE tablero SET nombre = ? WHERE id_tablero = ? AND eliminado = FALSE";
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement actualizacion = conexion.prepareStatement(sql)) {
            actualizacion.setString(1, nuevoNombre);
            actualizacion.setInt(2, idTablero);
            actualizacion.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public void eliminar(int idTablero) {
        try (Connection conexion = conexionBD.obtenerConexion()) {
            // El tablero y sus misiones se eliminan juntos: si falla uno, no se elimina ninguno.
            conexion.setAutoCommit(false);
            try {
                marcarEliminado(conexion, "UPDATE mision SET eliminado = TRUE WHERE id_tablero = ?", idTablero);
                marcarEliminado(conexion, "UPDATE tablero SET eliminado = TRUE WHERE id_tablero = ?", idTablero);
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    private void marcarEliminado(Connection conexion, String sql, int idTablero) throws SQLException {
        try (PreparedStatement actualizacion = conexion.prepareStatement(sql)) {
            actualizacion.setInt(1, idTablero);
            actualizacion.executeUpdate();
        }
    }
}
