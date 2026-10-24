package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHistorial;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioHistorialJDBC implements RepositorioHistorial {

    private static final String ERROR_BD = "Error al leer el historial";

    private final ConexionBD conexionBD;

    public RepositorioHistorialJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public List<Integer> listarXpPorHeroe(int idHeroe) {
        // No filtra eliminado: la XP ganada cuenta aunque después se haya borrado la misión o su tablero.
        String sql = """
                SELECT h.xp_obtenida
                FROM historial_mision h
                JOIN mision m ON m.id_mision = h.id_mision
                JOIN tablero t ON t.id_tablero = m.id_tablero
                WHERE t.id_heroe = ?
                """;
        List<Integer> xpPorMision = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idHeroe);
            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    xpPorMision.add(resultado.getInt("xp_obtenida"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
        return xpPorMision;
    }
}
