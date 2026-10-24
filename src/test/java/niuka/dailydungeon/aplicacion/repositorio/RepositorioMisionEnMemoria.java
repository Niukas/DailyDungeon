package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Mision;

import java.util.ArrayList;
import java.util.List;

// Guardo copias de las misiones. Así, si el servicio cambia una misión y se olvida
// de llamar a actualizar, el test se da cuenta (igual que pasaría con la base real).
public class RepositorioMisionEnMemoria implements RepositorioMision {

    private final List<Mision> misiones = new ArrayList<>();
    private int ultimoId = 0;

    @Override
    public List<Mision> listarPorTablero(int idTablero) {
        return misiones.stream()
                .filter(mision -> mision.idTablero() == idTablero)
                .map(mision -> mision.conId(mision.id()))
                .toList();
    }

    // En los tests hay un solo héroe, así que devuelve todas las misiones.
    @Override
    public List<Mision> listarPorHeroe(int idHeroe) {
        return misiones.stream()
                .map(mision -> mision.conId(mision.id()))
                .toList();
    }

    @Override
    public Mision guardar(Mision mision) {
        ultimoId++;
        misiones.add(mision.conId(ultimoId));
        return mision.conId(ultimoId);
    }

    @Override
    public void actualizar(Mision mision) {
        misiones.replaceAll(guardada -> guardada.id() == mision.id() ? mision.conId(mision.id()) : guardada);
    }

    @Override
    public void eliminar(int idMision) {
        misiones.removeIf(mision -> mision.id() == idMision);
    }
}
