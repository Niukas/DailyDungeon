package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;

public interface RepositorioHeroe {

    boolean existeParaUsuario(int idUsuario);

    Heroe guardar(Heroe heroe);
}
