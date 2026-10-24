package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Clase;

import java.util.List;

public class RepositorioClaseEnMemoria implements RepositorioClase {

    private final List<Clase> clases;

    public RepositorioClaseEnMemoria(List<Clase> clases) {
        this.clases = clases;
    }

    @Override
    public List<Clase> listarTodas() {
        return clases;
    }
}
