package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioUsuarioEnMemoria implements RepositorioUsuario {

    private final List<Usuario> usuarios = new ArrayList<>();

    @Override
    public Optional<Usuario> buscarPorNombre(String nombreUsuario) {
        return usuarios.stream()
                .filter(usuario -> usuario.nombreUsuario().equals(nombreUsuario))
                .findFirst();
    }

    @Override
    public boolean existeNombre(String nombreUsuario) {
        return buscarPorNombre(nombreUsuario).isPresent();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        Usuario guardado = new Usuario(usuarios.size() + 1, usuario.nombreUsuario(),
                usuario.contrasenaHash(), usuario.contrasenaSal());
        usuarios.add(guardado);
        return guardado;
    }
}
