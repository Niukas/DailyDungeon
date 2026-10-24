package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;

import java.util.Optional;

public interface RepositorioHeroe {

    boolean existeParaUsuario(int idUsuario);

    Optional<Heroe> buscarPorUsuario(int idUsuario);

    Heroe guardar(Heroe heroe);
}
