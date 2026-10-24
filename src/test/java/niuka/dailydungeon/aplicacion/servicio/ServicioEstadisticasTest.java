package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroeEnMemoria;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioHistorialEnMemoria;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioMisionEnMemoria;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioTableroEnMemoria;
import niuka.dailydungeon.dominio.modelo.ClasesDePrueba;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.Dificultad;
import niuka.dailydungeon.dominio.modelo.EstadoMision;
import niuka.dailydungeon.dominio.modelo.Estadisticas;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Prioridad;
import niuka.dailydungeon.dominio.modelo.ProgresoTablero;
import niuka.dailydungeon.dominio.modelo.Tablero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioEstadisticasTest {

    private static final int ID_USUARIO = 1;

    private RepositorioHistorialEnMemoria repositorioHistorial;
    private RepositorioTableroEnMemoria repositorioTablero;
    private RepositorioMisionEnMemoria repositorioMision;
    private ServicioEstadisticas servicio;
    private Heroe heroe;

    @BeforeEach
    void prepararServicio() {
        RepositorioHeroeEnMemoria repositorioHeroe = new RepositorioHeroeEnMemoria();
        repositorioHistorial = new RepositorioHistorialEnMemoria();
        repositorioTablero = new RepositorioTableroEnMemoria();
        repositorioMision = new RepositorioMisionEnMemoria();
        servicio = new ServicioEstadisticas(repositorioHeroe, repositorioHistorial, repositorioTablero, repositorioMision);
        heroe = repositorioHeroe.guardar(Heroe.nuevo(ID_USUARIO, "Arturo", ClasesDePrueba.guerrero()));
    }

    private void agregarMision(Tablero tablero, EstadoMision estado) {
        DatosMision datos = new DatosMision("Estudiar", null, null, Dificultad.MEDIA, Prioridad.MEDIA);
        repositorioMision.guardar(new Mision(0, tablero.id(), datos, estado, false));
    }

    @Test
    void sinHistorialNiTablerosTodoEstaEnCero() {
        Estadisticas estadisticas = servicio.obtenerEstadisticas(ID_USUARIO);

        assertEquals(0, estadisticas.misionesCompletadas());
        assertEquals(0, estadisticas.xpTotal());
        assertTrue(estadisticas.progresoPorTablero().isEmpty());
    }

    @Test
    void cuentaLasMisionesCompletadasYSumaSuXp() {
        repositorioHistorial.agregar(28);
        repositorioHistorial.agregar(41);
        repositorioHistorial.agregar(110);

        Estadisticas estadisticas = servicio.obtenerEstadisticas(ID_USUARIO);

        assertEquals(3, estadisticas.misionesCompletadas());
        assertEquals(179, estadisticas.xpTotal());
    }

    @Test
    void elProgresoCuentaLasMisionesEnGloriaDeCadaTablero() {
        Tablero facultad = repositorioTablero.guardar(new Tablero(0, heroe.id(), "Facultad"));
        agregarMision(facultad, EstadoMision.GLORIA);
        agregarMision(facultad, EstadoMision.EN_AVENTURA);
        agregarMision(facultad, EstadoMision.TABLON);
        agregarMision(facultad, EstadoMision.TABLON);

        ProgresoTablero progreso = servicio.obtenerEstadisticas(ID_USUARIO).progresoPorTablero().getFirst();

        assertEquals("Facultad", progreso.nombreTablero());
        assertEquals(1, progreso.completadas());
        assertEquals(4, progreso.total());
        assertEquals(25, progreso.porcentaje());
    }

    @Test
    void unTableroSinMisionesTieneCeroPorCiento() {
        repositorioTablero.guardar(new Tablero(0, heroe.id(), "Casa"));

        ProgresoTablero progreso = servicio.obtenerEstadisticas(ID_USUARIO).progresoPorTablero().getFirst();

        assertEquals(0, progreso.total());
        assertEquals(0, progreso.porcentaje());
    }
}
