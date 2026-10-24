package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroeEnMemoria;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioRecompensaEnMemoria;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.ClasesDePrueba;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.Dificultad;
import niuka.dailydungeon.dominio.modelo.EstadoMision;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Prioridad;
import niuka.dailydungeon.dominio.modelo.Recompensa;
import niuka.dailydungeon.dominio.reglas.AzarFijo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioRecompensaTest {

    private static final int ID_USUARIO = 1;
    // El Guerrero empieza con Suerte 5: 10 % de crítico.
    private static final double AZAR_SIN_CRITICO = 0.99;
    private static final double AZAR_CON_CRITICO = 0.0;

    private RepositorioHeroeEnMemoria repositorioHeroe;
    private RepositorioRecompensaEnMemoria repositorioRecompensa;
    private Heroe heroe;

    @BeforeEach
    void prepararHeroe() {
        repositorioHeroe = new RepositorioHeroeEnMemoria();
        repositorioRecompensa = new RepositorioRecompensaEnMemoria();
        heroe = repositorioHeroe.guardar(Heroe.nuevo(ID_USUARIO, "Arturo", ClasesDePrueba.guerrero()));
    }

    private ServicioRecompensa servicioConAzar(double azar) {
        return new ServicioRecompensa(repositorioHeroe, repositorioRecompensa, new AzarFijo(azar));
    }

    private Mision misionEn(EstadoMision estado, Dificultad dificultad) {
        DatosMision datos = new DatosMision("Estudiar", null, null, dificultad, Prioridad.MEDIA);
        return new Mision(1, 1, datos, estado);
    }

    @Test
    void completarSinCriticoDaLaXpConElBonusDeSabiduria() {
        Mision mision = misionEn(EstadoMision.EN_AVENTURA, Dificultad.MEDIA);

        Recompensa recompensa = servicioConAzar(AZAR_SIN_CRITICO).completarMision(ID_USUARIO, mision);

        // 25 × (1 + 0,02 × 5) = 27,5 → 28
        assertEquals(28, recompensa.xpGanada());
        assertFalse(recompensa.critico());
        assertEquals(EstadoMision.GLORIA, mision.estado());
        assertEquals(28, heroe.xp());
        assertEquals(1, repositorioRecompensa.guardadas().size());
    }

    @Test
    void completarConCriticoMultiplicaLaXpPorUnoYMedio() {
        Mision mision = misionEn(EstadoMision.EN_AVENTURA, Dificultad.MEDIA);

        Recompensa recompensa = servicioConAzar(AZAR_CON_CRITICO).completarMision(ID_USUARIO, mision);

        // 27,5 × 1,5 = 41,25 → 41
        assertEquals(41, recompensa.xpGanada());
        assertTrue(recompensa.critico());
    }

    @Test
    void completarUnaMisionEpicaPuedeSubirDeNivel() {
        Mision mision = misionEn(EstadoMision.EN_AVENTURA, Dificultad.EPICA);

        Recompensa recompensa = servicioConAzar(AZAR_SIN_CRITICO).completarMision(ID_USUARIO, mision);

        // 100 × 1,10 = 110: sube a nivel 2 y le sobran 10
        assertTrue(recompensa.subioDeNivel());
        assertEquals(1, recompensa.nivelAnterior());
        assertEquals(2, recompensa.nivelNuevo());
        assertEquals(10, heroe.xp());
    }

    @Test
    void completarUnaMisionQueNoEstaEnAventuraFallaYNoGuardaNada() {
        Mision mision = misionEn(EstadoMision.TABLON, Dificultad.MEDIA);
        ServicioRecompensa servicio = servicioConAzar(AZAR_SIN_CRITICO);

        assertThrows(ExcepcionNegocio.class, () -> servicio.completarMision(ID_USUARIO, mision));

        assertEquals(0, heroe.xp());
        assertTrue(repositorioRecompensa.guardadas().isEmpty());
    }
}
