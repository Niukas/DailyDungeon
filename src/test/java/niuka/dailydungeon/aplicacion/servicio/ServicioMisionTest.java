package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioMisionEnMemoria;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.Dificultad;
import niuka.dailydungeon.dominio.modelo.EstadoMision;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Prioridad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioMisionTest {

    private static final int ID_TABLERO = 1;
    private static final int ID_OTRO_TABLERO = 2;
    private static final LocalDate HOY = LocalDate.now();
    private static final LocalDate AYER = HOY.minusDays(1);

    private RepositorioMisionEnMemoria repositorio;
    private ServicioMision servicio;

    @BeforeEach
    void prepararServicio() {
        repositorio = new RepositorioMisionEnMemoria();
        servicio = new ServicioMision(repositorio);
    }

    private DatosMision datos(String titulo, LocalDate fechaLimite) {
        return new DatosMision(titulo, null, fechaLimite, Dificultad.MEDIA, Prioridad.MEDIA);
    }

    private Mision unicaMisionGuardada() {
        return servicio.listarMisiones(ID_TABLERO).getFirst();
    }

    @Test
    void crearMisionValidaQuedaEnElTablon() {
        Mision mision = servicio.crearMision(ID_TABLERO, datos("  Estudiar  ", null));

        assertTrue(mision.id() > 0);
        assertEquals("Estudiar", mision.datos().titulo());
        assertEquals(EstadoMision.TABLON, mision.estado());
    }

    @Test
    void descripcionEnBlancoSeGuardaComoVacia() {
        DatosMision conDescripcionEnBlanco = new DatosMision("Estudiar", "   ", null, Dificultad.MEDIA, Prioridad.MEDIA);

        Mision mision = servicio.crearMision(ID_TABLERO, conDescripcionEnBlanco);

        assertNull(mision.datos().descripcion());
    }

    @Test
    void crearMisionConTituloVacioFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearMision(ID_TABLERO, datos("  ", null)));

        assertEquals("El título de la misión no puede estar vacío.", error.getMessage());
    }

    @Test
    void crearMisionConTituloLargoFalla() {
        assertThrows(ExcepcionNegocio.class, () -> servicio.crearMision(ID_TABLERO, datos("a".repeat(101), null)));
    }

    @Test
    void crearMisionSinDificultadOSinPrioridadFalla() {
        DatosMision sinDificultad = new DatosMision("Estudiar", null, null, null, Prioridad.MEDIA);
        DatosMision sinPrioridad = new DatosMision("Estudiar", null, null, Dificultad.MEDIA, null);

        assertThrows(ExcepcionNegocio.class, () -> servicio.crearMision(ID_TABLERO, sinDificultad));
        assertThrows(ExcepcionNegocio.class, () -> servicio.crearMision(ID_TABLERO, sinPrioridad));
    }

    @Test
    void crearMisionConFechaPasadaFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearMision(ID_TABLERO, datos("Estudiar", AYER)));

        assertEquals("La fecha límite no puede ser anterior a hoy.", error.getMessage());
    }

    @Test
    void crearMisionParaHoyOSinFechaEsValido() {
        assertDoesNotThrow(() -> servicio.crearMision(ID_TABLERO, datos("Para hoy", HOY)));
        assertDoesNotThrow(() -> servicio.crearMision(ID_TABLERO, datos("Sin fecha", null)));
    }

    @Test
    void editarMisionGuardaLosNuevosDatos() {
        Mision mision = servicio.crearMision(ID_TABLERO, datos("Estudiar", null));

        servicio.editarMision(mision, datos("Rendir final", HOY));

        assertEquals("Rendir final", unicaMisionGuardada().datos().titulo());
        assertEquals(HOY, unicaMisionGuardada().datos().fechaLimite());
    }

    @Test
    void editarMisionVencidaSinCambiarLaFechaEsValido() {
        Mision vencida = repositorio.guardar(new Mision(0, ID_TABLERO, datos("Estudiar", AYER), EstadoMision.TABLON));

        assertDoesNotThrow(() -> servicio.editarMision(vencida, datos("Estudiar mucho", AYER)));
    }

    @Test
    void editarMisionCambiandoAFechaPasadaFalla() {
        Mision mision = servicio.crearMision(ID_TABLERO, datos("Estudiar", null));

        assertThrows(ExcepcionNegocio.class, () -> servicio.editarMision(mision, datos("Estudiar", AYER)));
    }

    @Test
    void empezarMisionQuedaGuardadaEnAventura() {
        Mision mision = servicio.crearMision(ID_TABLERO, datos("Estudiar", null));

        servicio.empezarMision(mision);

        assertEquals(EstadoMision.EN_AVENTURA, unicaMisionGuardada().estado());
    }

    @Test
    void volverAlTablonQuedaGuardado() {
        Mision mision = servicio.crearMision(ID_TABLERO, datos("Estudiar", null));
        servicio.empezarMision(mision);

        servicio.volverAlTablon(mision);

        assertEquals(EstadoMision.TABLON, unicaMisionGuardada().estado());
    }

    @Test
    void eliminarMisionLaSacaDeLaLista() {
        Mision mision = servicio.crearMision(ID_TABLERO, datos("Estudiar", null));

        servicio.eliminarMision(mision);

        assertTrue(servicio.listarMisiones(ID_TABLERO).isEmpty());
    }

    @Test
    void listarSoloDevuelveLasMisionesDelTablero() {
        servicio.crearMision(ID_TABLERO, datos("Estudiar", null));
        servicio.crearMision(ID_OTRO_TABLERO, datos("Lavar los platos", null));

        assertEquals(1, servicio.listarMisiones(ID_TABLERO).size());
        assertEquals("Estudiar", unicaMisionGuardada().datos().titulo());
    }
}
