package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroeEnMemoria;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioMisionEnMemoria;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioPenalizacionEnMemoria;
import niuka.dailydungeon.dominio.modelo.Atributo;
import niuka.dailydungeon.dominio.modelo.ClasesDePrueba;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.Dificultad;
import niuka.dailydungeon.dominio.modelo.EstadoMision;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Penalizacion;
import niuka.dailydungeon.dominio.modelo.Prioridad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioPenalizacionTest {

    private static final int ID_USUARIO = 1;
    private static final int ID_TABLERO = 1;
    private static final LocalDate HOY = LocalDate.now();
    private static final LocalDate AYER = HOY.minusDays(1);

    private RepositorioHeroeEnMemoria repositorioHeroe;
    private RepositorioMisionEnMemoria repositorioMision;
    private RepositorioPenalizacionEnMemoria repositorioPenalizacion;
    private ServicioPenalizacion servicio;

    @BeforeEach
    void prepararServicio() {
        repositorioHeroe = new RepositorioHeroeEnMemoria();
        repositorioMision = new RepositorioMisionEnMemoria();
        repositorioPenalizacion = new RepositorioPenalizacionEnMemoria(repositorioMision);
        servicio = new ServicioPenalizacion(repositorioHeroe, repositorioMision, repositorioPenalizacion);
    }

    private Heroe crearGuerreroNuevo() {
        return repositorioHeroe.guardar(Heroe.nuevo(ID_USUARIO, "Arturo", ClasesDePrueba.guerrero()));
    }

    private void agregarMision(LocalDate fechaLimite, EstadoMision estado) {
        DatosMision datos = new DatosMision("Estudiar", null, fechaLimite, Dificultad.MEDIA, Prioridad.MEDIA);
        repositorioMision.guardar(new Mision(0, ID_TABLERO, datos, estado, false));
    }

    @Test
    void sinMisionesVencidasNoPasaNada() {
        Heroe heroe = crearGuerreroNuevo();
        agregarMision(HOY, EstadoMision.TABLON);
        agregarMision(null, EstadoMision.TABLON);

        Optional<Penalizacion> penalizacion = servicio.revisarVencimientos(ID_USUARIO);

        assertTrue(penalizacion.isEmpty());
        assertEquals(100, heroe.hpActual());
        assertEquals(0, repositorioPenalizacion.vecesGuardado());
    }

    @Test
    void unaMisionVencidaQuitaVidaSegunLaResistencia() {
        Heroe heroe = crearGuerreroNuevo();
        agregarMision(AYER, EstadoMision.TABLON);

        Penalizacion penalizacion = servicio.revisarVencimientos(ID_USUARIO).orElseThrow();

        // Resistencia 5 → daño 8
        assertEquals(1, penalizacion.misionesVencidas());
        assertEquals(8, penalizacion.danioTotal());
        assertFalse(penalizacion.cayo());
        assertEquals(92, heroe.hpActual());
        assertTrue(repositorioMision.listarPorTablero(ID_TABLERO).getFirst().penalizada());
    }

    @Test
    void variasMisionesVencidasSumanElDanio() {
        Heroe heroe = crearGuerreroNuevo();
        agregarMision(AYER, EstadoMision.TABLON);
        agregarMision(AYER, EstadoMision.EN_AVENTURA);

        Penalizacion penalizacion = servicio.revisarVencimientos(ID_USUARIO).orElseThrow();

        assertEquals(2, penalizacion.misionesVencidas());
        assertEquals(16, penalizacion.danioTotal());
        assertEquals(84, heroe.hpActual());
    }

    @Test
    void unaMisionNoSePenalizaDosVeces() {
        Heroe heroe = crearGuerreroNuevo();
        agregarMision(AYER, EstadoMision.TABLON);
        servicio.revisarVencimientos(ID_USUARIO);

        Optional<Penalizacion> segundaRevision = servicio.revisarVencimientos(ID_USUARIO);

        assertTrue(segundaRevision.isEmpty());
        assertEquals(92, heroe.hpActual());
    }

    @Test
    void lasMisionesEnGloriaNoSePenalizan() {
        crearGuerreroNuevo();
        agregarMision(AYER, EstadoMision.GLORIA);

        assertTrue(servicio.revisarVencimientos(ID_USUARIO).isEmpty());
    }

    @Test
    void unUsuarioSinHeroeNoSePenaliza() {
        agregarMision(AYER, EstadoMision.TABLON);

        assertTrue(servicio.revisarVencimientos(ID_USUARIO).isEmpty());
    }

    @Test
    void siLaVidaLlegaACeroElHeroeCae() {
        // Guerrero de nivel 2 con 5 de vida: Resistencia 7 → daño 10 − 3 = 7
        Map<Atributo, Integer> atributos = Map.of(
                Atributo.VITALIDAD, 8, Atributo.SABIDURIA, 6, Atributo.RESISTENCIA, 7, Atributo.SUERTE, 6);
        Heroe heroe = repositorioHeroe.guardar(
                new Heroe(0, ID_USUARIO, ClasesDePrueba.guerrero(), "Arturo", 2, 50, 5, atributos));
        agregarMision(AYER, EstadoMision.TABLON);

        Penalizacion penalizacion = servicio.revisarVencimientos(ID_USUARIO).orElseThrow();

        assertTrue(penalizacion.cayo());
        assertEquals(1, heroe.nivel());
        assertEquals(50, heroe.hpActual());
    }
}
