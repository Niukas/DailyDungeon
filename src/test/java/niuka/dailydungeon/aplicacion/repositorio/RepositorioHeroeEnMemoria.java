package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;

import java.util.ArrayList;
import java.util.List;

public class RepositorioHeroeEnMemoria implements RepositorioHeroe {

    private final List<Heroe> heroes = new ArrayList<>();

    @Override
    public boolean existeParaUsuario(int idUsuario) {
        return heroes.stream().anyMatch(heroe -> heroe.idUsuario() == idUsuario);
    }

    @Override
    public Heroe guardar(Heroe heroe) {
        Heroe guardado = heroe.conId(heroes.size() + 1);
        heroes.add(guardado);
        return guardado;
    }
}
