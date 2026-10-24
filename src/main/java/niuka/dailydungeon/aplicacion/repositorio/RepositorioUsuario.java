package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Usuario;

import java.util.Optional;

public interface RepositorioUsuario {

    Optional<Usuario> buscarPorNombre(String nombreUsuario);

    boolean existeNombre(String nombreUsuario);

    Usuario guardar(Usuario usuario);
}
