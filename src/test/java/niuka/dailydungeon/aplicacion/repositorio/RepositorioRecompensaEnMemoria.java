package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Recompensa;

import java.util.ArrayList;
import java.util.List;

public class RepositorioRecompensaEnMemoria implements RepositorioRecompensa {

    private final List<Recompensa> guardadas = new ArrayList<>();

    @Override
    public void guardar(Mision mision, Heroe heroe, Recompensa recompensa) {
        guardadas.add(recompensa);
    }

    public List<Recompensa> guardadas() {
        return guardadas;
    }
}
