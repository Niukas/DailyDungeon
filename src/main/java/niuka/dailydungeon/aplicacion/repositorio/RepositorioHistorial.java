package niuka.dailydungeon.aplicacion.repositorio;

import java.util.List;

public interface RepositorioHistorial {

    // La XP de cada misión completada por el héroe, una por fila del historial.
    List<Integer> listarXpPorHeroe(int idHeroe);
}
