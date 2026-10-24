package niuka.dailydungeon.infraestructura.repositorio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioUsuario;
import niuka.dailydungeon.dominio.modelo.Usuario;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class RepositorioUsuarioJDBC implements RepositorioUsuario {

    private static final String ERROR_BD = "Error al acceder a la tabla usuario";

    private final ConexionBD conexionBD;

    public RepositorioUsuarioJDBC(ConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Optional<Usuario> buscarPorNombre(String nombreUsuario) {
        String sql = """
                SELECT id_usuario, nombre_usuario, contrasena_hash, contrasena_sal
                FROM usuario
                WHERE nombre_usuario = ? AND eliminado = FALSE
                """;
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, nombreUsuario);
            try (ResultSet resultado = consulta.executeQuery()) {
                if (!resultado.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Usuario(
                        resultado.getInt("id_usuario"),
                        resultado.getString("nombre_usuario"),
                        resultado.getString("contrasena_hash"),
                        resultado.getString("contrasena_sal")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public boolean existeNombre(String nombreUsuario) {
        // No filtra eliminado: el UNIQUE de nombre_usuario también incluye a los usuarios eliminados.
        String sql = "SELECT 1 FROM usuario WHERE nombre_usuario = ?";
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement consulta = conexion.prepareStatement(sql)) {
            consulta.setString(1, nombreUsuario);
            try (ResultSet resultado = consulta.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nombre_usuario, contrasena_hash, contrasena_sal) VALUES (?, ?, ?)";
        try (Connection conexion = conexionBD.obtenerConexion();
             PreparedStatement insercion = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            insercion.setString(1, usuario.nombreUsuario());
            insercion.setString(2, usuario.contrasenaHash());
            insercion.setString(3, usuario.contrasenaSal());
            insercion.executeUpdate();
            try (ResultSet claves = insercion.getGeneratedKeys()) {
                claves.next();
                return new Usuario(claves.getInt(1), usuario.nombreUsuario(),
                        usuario.contrasenaHash(), usuario.contrasenaSal());
            }
        } catch (SQLException e) {
            throw new IllegalStateException(ERROR_BD, e);
        }
    }
}
