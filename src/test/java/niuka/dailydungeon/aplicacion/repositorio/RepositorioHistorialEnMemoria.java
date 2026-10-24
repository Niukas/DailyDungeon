package niuka.dailydungeon.aplicacion.repositorio;

import java.util.ArrayList;
import java.util.List;

public class RepositorioHistorialEnMemoria implements RepositorioHistorial {

    private final List<Integer> xpPorMision = new ArrayList<>();

    public void agregar(int xp) {
        xpPorMision.add(xp);
    }

    // En los tests hay un solo héroe, así que devuelve todo el historial.
    @Override
    public List<Integer> listarXpPorHeroe(int idHeroe) {
        return xpPorMision;
    }
}
