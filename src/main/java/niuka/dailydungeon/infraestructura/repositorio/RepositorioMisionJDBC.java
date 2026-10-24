package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioMision;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.Dificultad;
import niuka.dailydungeon.dominio.modelo.EstadoMision;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Prioridad;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioMisionJDBC implements RepositorioMision {

    private static final String ERROR_BD = "Error al acceder a la tabla mision";

    private final ConexionBD conexionBD;

    public RepositorioMisionJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public List<Mision> listarPorTablero(int idTablero) {
        // Primero prioridad alta, después la fecha más cercana y al final las que no tienen fecha.
        String sql = """
                SELECT m.id_mision, m.id_tablero, m.titulo, m.descripcion, m.fecha_limite,
                       m.id_dificultad, m.id_prioridad, m.id_estado, m.penalizada
                FROM mision m
                JOIN prioridad p ON p.id_prioridad = m.id_prioridad
                WHERE m.id_tablero = ? AND m.eliminado = FALSE
                ORDER BY p.orden DESC, m.fecha_limite IS NULL, m.fecha_limite, m.id_mision
                """;
        List<Mision> misiones = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idTablero);
            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    misiones.add(leerMision(resultado));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
        return misiones;
    }

    @Override
    public List<Mision> listarPorHeroe(int idHeroe) {
        String sql = """
                SELECT m.id_mision, m.id_tablero, m.titulo, m.descripcion, m.fecha_limite,
                       m.id_dificultad, m.id_prioridad, m.id_estado, m.penalizada
                FROM mision m
                JOIN tablero t ON t.id_tablero = m.id_tablero
                WHERE t.id_heroe = ? AND t.eliminado = FALSE AND m.eliminado = FALSE
                """;
        List<Mision> misiones = new ArrayList<>();
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setInt(1, idHeroe);
            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    misiones.add(leerMision(resultado));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
        return misiones;
    }

    @Override
    public Mision guardar(Mision mision) {
        String sql = """
                INSERT INTO mision (id_dificultad, id_prioridad, id_estado, titulo, descripcion, fecha_limite, id_tablero)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement insercion = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            cargarDatos(insercion, mision);
            insercion.setInt(7, mision.idTablero());
            insercion.executeUpdate();
            try (ResultSet claves = insercion.getGeneratedKeys()) {
                claves.next();
                return mision.conId(claves.getInt(1));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public void actualizar(Mision mision) {
        String sql = """
                UPDATE mision
                SET id_dificultad = ?, id_prioridad = ?, id_estado = ?, titulo = ?, descripcion = ?, fecha_limite = ?
                WHERE id_mision = ? AND eliminado = FALSE
                """;
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement actualizacion = conexion.prepareStatement(sql)) {
            cargarDatos(actualizacion, mision);
            actualizacion.setInt(7, mision.id());
            actualizacion.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public void eliminar(int idMision) {
        String sql = "UPDATE mision SET eliminado = TRUE WHERE id_mision = ?";
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement actualizacion = conexion.prepareStatement(sql)) {
            actualizacion.setInt(1, idMision);
            actualizacion.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    // INSERT y UPDATE usan el mismo orden en los primeros 6 parámetros.
    private void cargarDatos(PreparedStatement sentencia, Mision mision) throws SQLException {
        DatosMision datos = mision.datos();
        sentencia.setInt(1, datos.dificultad().id());
        sentencia.setInt(2, datos.prioridad().id());
        sentencia.setInt(3, mision.estado().id());
        sentencia.setString(4, datos.titulo());
        sentencia.setString(5, datos.descripcion());
        sentencia.setObject(6, datos.fechaLimite(), Types.DATE);
    }

    private Mision leerMision(ResultSet resultado) throws SQLException {
        DatosMision datos = new DatosMision(
                resultado.getString("titulo"),
                resultado.getString("descripcion"),
                resultado.getObject("fecha_limite", LocalDate.class),
                Dificultad.desdeId(resultado.getInt("id_dificultad")),
                Prioridad.desdeId(resultado.getInt("id_prioridad")));
        return new Mision(
                resultado.getInt("id_mision"),
                resultado.getInt("id_tablero"),
                datos,
                EstadoMision.desdeId(resultado.getInt("id_estado")),
                resultado.getBoolean("penalizada"));
    }
}
