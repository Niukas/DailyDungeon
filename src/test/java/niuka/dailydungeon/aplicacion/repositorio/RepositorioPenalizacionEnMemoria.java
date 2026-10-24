package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;

import java.util.List;

public class RepositorioPenalizacionEnMemoria implements RepositorioPenalizacion {

    private final RepositorioMisionEnMemoria repositorioMision;
    private int vecesGuardado = 0;

    public RepositorioPenalizacionEnMemoria(RepositorioMisionEnMemoria repositorioMision) {
        this.repositorioMision = repositorioMision;
    }

    // Actualiza las misiones en su repositorio, para que la próxima revisión las vea penalizadas.
    @Override
    public void guardar(Heroe heroe, List<Mision> misionesPenalizadas) {
        for (Mision mision : misionesPenalizadas) {
            repositorioMision.actualizar(mision);
        }
        vecesGuardado++;
    }

    public int vecesGuardado() {
        return vecesGuardado;
    }
}
