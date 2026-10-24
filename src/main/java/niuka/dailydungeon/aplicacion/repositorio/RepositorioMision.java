package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Mision;

import java.util.List;

public interface RepositorioMision {

    List<Mision> listarPorTablero(int idTablero);

    // Las misiones de todos los tableros del héroe.
    List<Mision> listarPorHeroe(int idHeroe);

    Mision guardar(Mision mision);

    void actualizar(Mision mision);

    void eliminar(int idMision);
}
