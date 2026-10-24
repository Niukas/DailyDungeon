package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Mision;

import java.util.List;

public interface RepositorioMision {

    List<Mision> listarPorTablero(int idTablero);

    Mision guardar(Mision mision);

    void actualizar(Mision mision);

    void eliminar(int idMision);
}
