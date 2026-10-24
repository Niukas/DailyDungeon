package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroe;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioHistorial;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioMision;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioTablero;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Estadisticas;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.ProgresoTablero;
import niuka.dailydungeon.dominio.modelo.Tablero;

import java.util.List;

public class ServicioEstadisticas {

    private final RepositorioHeroe repositorioHeroe;
    private final RepositorioHistorial repositorioHistorial;
    private final RepositorioTablero repositorioTablero;
    private final RepositorioMision repositorioMision;

    public ServicioEstadisticas(RepositorioHeroe repositorioHeroe, RepositorioHistorial repositorioHistorial,
                                RepositorioTablero repositorioTablero, RepositorioMision repositorioMision) {
        this.repositorioHeroe = repositorioHeroe;
        this.repositorioHistorial = repositorioHistorial;
        this.repositorioTablero = repositorioTablero;
        this.repositorioMision = repositorioMision;
    }

    public Estadisticas obtenerEstadisticas(int idUsuario) {
        Heroe heroe = repositorioHeroe.buscarPorUsuario(idUsuario)
                .orElseThrow(() -> new ExcepcionNegocio("Todavía no creaste tu héroe."));

        List<Integer> xpPorMision = repositorioHistorial.listarXpPorHeroe(heroe.id());
        int xpTotal = xpPorMision.stream().mapToInt(xp -> xp).sum();

        List<ProgresoTablero> progreso = repositorioTablero.listarPorHeroe(heroe.id()).stream()
                .map(tablero -> calcularProgreso(tablero))
                .toList();

        return new Estadisticas(xpPorMision.size(), xpTotal, progreso);
    }

    private ProgresoTablero calcularProgreso(Tablero tablero) {
        List<Mision> misiones = repositorioMision.listarPorTablero(tablero.id());
        int completadas = (int) misiones.stream()
                .filter(mision -> mision.estaEnGloria())
                .count();
        return new ProgresoTablero(tablero.nombre(), completadas, misiones.size());
    }
}
