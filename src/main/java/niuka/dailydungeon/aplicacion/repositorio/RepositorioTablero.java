package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Tablero;

import java.util.List;

public interface RepositorioTablero {

    List<Tablero> listarPorHeroe(int idHeroe);

    Tablero guardar(Tablero tablero);

    void renombrar(int idTablero, String nuevoNombre);

    void eliminar(int idTablero);
}
