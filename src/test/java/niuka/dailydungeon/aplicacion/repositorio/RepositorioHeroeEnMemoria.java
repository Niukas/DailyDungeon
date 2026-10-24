package niuka.dailydungeon.aplicacion.repositorio;

import niuka.dailydungeon.dominio.modelo.Heroe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioHeroeEnMemoria implements RepositorioHeroe {

    private final List<Heroe> heroes = new ArrayList<>();

    @Override
    public boolean existeParaUsuario(int idUsuario) {
        return buscarPorUsuario(idUsuario).isPresent();
    }

    @Override
    public Optional<Heroe> buscarPorUsuario(int idUsuario) {
        return heroes.stream()
                .filter(heroe -> heroe.idUsuario() == idUsuario)
                .findFirst();
    }

    @Override
    public Heroe guardar(Heroe heroe) {
        Heroe guardado = heroe.conId(heroes.size() + 1);
        heroes.add(guardado);
        return guardado;
    }
}
