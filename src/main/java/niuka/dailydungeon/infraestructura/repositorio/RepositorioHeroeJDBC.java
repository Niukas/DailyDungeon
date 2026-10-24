package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroe;
import niuka.dailydungeon.dominio.modelo.Atributo;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RepositorioHeroeJDBC implements RepositorioHeroe {

    private static final String ERROR_BD = "Error al acceder a la tabla heroe";

    private final ConexionBD conexionBD;

    public RepositorioHeroeJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public boolean existeParaUsuario(int idUsuario) {
        String sql = "SELECT 1 FROM heroe WHERE id_usuario = ? AND eliminado = FALSE";
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idUsuario);
            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public Heroe guardar(Heroe heroe) {
        try (Connection conexion = conexionBD.obtenerConexion()) {
            // Héroe y atributos se guardan juntos: si falla uno, no se guarda ninguno.
            conexion.setAutoCommit(false);
            try {
                int idHeroe = insertarHeroe(conexion, heroe);
                insertarAtributos(conexion, idHeroe, heroe);
                conexion.commit();
                return heroe.conId(idHeroe);
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    private int insertarHeroe(Connection conexion, Heroe heroe) throws SQLException {
        String sql = "INSERT INTO heroe (id_usuario, id_clase, nombre, nivel, xp, hp_actual) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insercion = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            insercion.setInt(1, heroe.idUsuario());
            insercion.setInt(2, heroe.clase().id());
            insercion.setString(3, heroe.nombre());
            insercion.setInt(4, heroe.nivel());
            insercion.setInt(5, heroe.xp());
            insercion.setInt(6, heroe.hpActual());
            insercion.executeUpdate();
            try (ResultSet claves = insercion.getGeneratedKeys()) {
                claves.next();
                return claves.getInt(1);
            }
        }
    }

    private void insertarAtributos(Connection conexion, int idHeroe, Heroe heroe) throws SQLException {
        String sql = "INSERT INTO heroe_atributo (id_heroe, id_atributo, valor) VALUES (?, ?, ?)";
        try (PreparedStatement insercion = conexion.prepareStatement(sql)) {
            for (Atributo atributo : Atributo.values()) {
                insercion.setInt(1, idHeroe);
                insercion.setInt(2, atributo.id());
                insercion.setInt(3, heroe.valorDe(atributo));
                insercion.executeUpdate();
            }
        }
    }
}
