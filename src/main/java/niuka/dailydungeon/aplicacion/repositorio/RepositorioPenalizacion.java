package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;

import java.util.List;

public interface RepositorioPenalizacion {

    // Guarda junto las misiones marcadas como penalizadas y el héroe con su vida nueva.
    void guardar(Heroe heroe, List<Mision> misionesPenalizadas);
}
