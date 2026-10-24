package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioRecompensa;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Recompensa;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RepositorioRecompensaJDBC implements RepositorioRecompensa {

    private static final String ERROR_BD = "Error al guardar la recompensa";

    private final ConexionBD conexionBD;

    public RepositorioRecompensaJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(Mision mision, Heroe heroe, Recompensa recompensa) {
        try (Connection conexion = conexionBD.obtenerConexion()) {
            // Los tres cambios usan la misma conexión: o se guardan todos, o ninguno.
            conexion.setAutoCommit(false);
            try {
                actualizarEstadoMision(conexion, mision);
                RepositorioHeroeJDBC.actualizarHeroe(conexion, heroe);
                insertarHistorial(conexion, mision, recompensa);
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    private void actualizarEstadoMision(Connection conexion, Mision mision) throws SQLException {
        String sql = "UPDATE mision SET id_estado = ? WHERE id_mision = ?";
        try (PreparedStatement actualizacion = conexion.prepareStatement(sql)) {
            actualizacion.setInt(1, mision.estado().id());
            actualizacion.setInt(2, mision.id());
            actualizacion.executeUpdate();
        }
    }

    private void insertarHistorial(Connection conexion, Mision mision, Recompensa recompensa) throws SQLException {
        String sql = "INSERT INTO historial_mision (id_mision, xp_obtenida, fue_critico) VALUES (?, ?, ?)";
        try (PreparedStatement insercion = conexion.prepareStatement(sql)) {
            insercion.setInt(1, mision.id());
            insercion.setInt(2, recompensa.xpGanada());
            insercion.setBoolean(3, recompensa.critico());
            insercion.executeUpdate();
        }
    }
}
