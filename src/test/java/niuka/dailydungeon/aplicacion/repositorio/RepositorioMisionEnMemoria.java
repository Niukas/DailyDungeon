package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Mision;

import java.util.ArrayList;
import java.util.List;

// Guarda y devuelve copias: si el servicio cambia una misión y se olvida de llamar a actualizar,
// el cambio no queda guardado y el test lo detecta, igual que pasaría con la base real.
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
