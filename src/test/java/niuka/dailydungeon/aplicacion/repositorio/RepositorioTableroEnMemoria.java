package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Tablero;

import java.util.ArrayList;
import java.util.List;

public class RepositorioTableroEnMemoria implements RepositorioTablero {

    private final List<Tablero> tableros = new ArrayList<>();
    private int ultimoId = 0;

    @Override
    public List<Tablero> listarPorHeroe(int idHeroe) {
        return tableros.stream()
                .filter(tablero -> tablero.idHeroe() == idHeroe)
                .toList();
    }

    @Override
    public Tablero guardar(Tablero tablero) {
        ultimoId++;
        Tablero guardado = new Tablero(ultimoId, tablero.idHeroe(), tablero.nombre());
        tableros.add(guardado);
        return guardado;
    }

    @Override
    public void renombrar(int idTablero, String nuevoNombre) {
        tableros.replaceAll(tablero -> tablero.id() == idTablero
                ? new Tablero(tablero.id(), tablero.idHeroe(), nuevoNombre)
                : tablero);
    }

    @Override
    public void eliminar(int idTablero) {
        tableros.removeIf(tablero -> tablero.id() == idTablero);
    }
}
