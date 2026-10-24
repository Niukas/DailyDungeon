package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Recompensa;

public interface RepositorioRecompensa {

    // Guarda junto la misión en Gloria, el héroe con su XP nueva y la fila del historial.
    void guardar(Mision mision, Heroe heroe, Recompensa recompensa);
}
