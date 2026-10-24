package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroe;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioRecompensa;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Atributo;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Recompensa;
import niuka.dailydungeon.dominio.reglas.ReglasJuego;

import java.util.random.RandomGenerator;

public class ServicioRecompensa {

    private final RepositorioHeroe repositorioHeroe;
    private final RepositorioRecompensa repositorioRecompensa;
    private final RandomGenerator azar;

    public ServicioRecompensa(RepositorioHeroe repositorioHeroe, RepositorioRecompensa repositorioRecompensa,
                              RandomGenerator azar) {
        this.repositorioHeroe = repositorioHeroe;
        this.repositorioRecompensa = repositorioRecompensa;
        this.azar = azar;
    }

    public Recompensa completarMision(int idUsuario, Mision mision) {
        Heroe heroe = repositorioHeroe.buscarPorUsuario(idUsuario)
                .orElseThrow(() -> new ExcepcionNegocio("Todavía no creaste tu héroe."));
        mision.completar();

        boolean critico = ReglasJuego.esCritico(heroe.valorDe(Atributo.SUERTE), azar);
        int xp = ReglasJuego.xpGanada(
                mision.datos().dificultad().xpBase(), heroe.valorDe(Atributo.SABIDURIA), critico);
        int nivelAnterior = heroe.nivel();
        heroe.ganarXp(xp);

        Recompensa recompensa = new Recompensa(xp, critico, nivelAnterior, heroe.nivel());
        repositorioRecompensa.guardar(mision, heroe, recompensa);
        return recompensa;
    }
}
