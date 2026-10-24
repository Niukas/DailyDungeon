package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioPenalizacion;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RepositorioPenalizacionJDBC implements RepositorioPenalizacion {

    private static final String ERROR_BD = "Error al guardar la penalización";

    private final ConexionBD conexionBD;

    public RepositorioPenalizacionJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void guardar(Heroe heroe, List<Mision> misionesPenalizadas) {
        try (Connection conexion = conexionBD.obtenerConexion()) {
            // Igual que en la recompensa: misiones y héroe en una sola transacción.
            conexion.setAutoCommit(false);
            try {
                marcarPenalizadas(conexion, misionesPenalizadas);
                RepositorioHeroeJDBC.actualizarHeroe(conexion, heroe);
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    private void marcarPenalizadas(Connection conexion, List<Mision> misiones) throws SQLException {
        String sql = "UPDATE mision SET penalizada = TRUE WHERE id_mision = ?";
        try (PreparedStatement actualizacion = conexion.prepareStatement(sql)) {
            for (Mision mision : misiones) {
                actualizacion.setInt(1, mision.id());
                actualizacion.executeUpdate();
            }
        }
    }
}
