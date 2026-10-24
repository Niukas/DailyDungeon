package niuka.dailydungeon.dominio.modelo;

import niuka.dailydungeon.dominio.ExcepcionNegocio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MisionTest {

    private static final DatosMision DATOS = new DatosMision("Estudiar", null, null, Dificultad.MEDIA, Prioridad.MEDIA);

    private static final LocalDate HOY = LocalDate.of(2026, 10, 24);
    private static final LocalDate AYER = HOY.minusDays(1);

    private Mision misionEn(EstadoMision estado) {
        return new Mision(1, 1, DATOS, estado, false);
    }

    private Mision misionConFecha(LocalDate fechaLimite, EstadoMision estado) {
        DatosMision datos = new DatosMision("Estudiar", null, fechaLimite, Dificultad.MEDIA, Prioridad.MEDIA);
        return new Mision(1, 1, datos, estado, false);
    }

    @Test
    void misionNuevaEmpiezaEnElTablon() {
        assertEquals(EstadoMision.TABLON, Mision.nueva(1, DATOS).estado());
    }

    @Test
    void empezarPasaDelTablonAEnAventura() {
        Mision mision = misionEn(EstadoMision.TABLON);

        mision.empezar();

        assertEquals(EstadoMision.EN_AVENTURA, mision.estado());
    }

    @Test
    void empezarUnaMisionQueNoEstaEnElTablonFalla() {
        assertThrows(ExcepcionNegocio.class, () -> misionEn(EstadoMision.EN_AVENTURA).empezar());
        assertThrows(ExcepcionNegocio.class, () -> misionEn(EstadoMision.GLORIA).empezar());
    }

    @Test
    void volverAlTablonDesdeEnAventura() {
        Mision mision = misionEn(EstadoMision.EN_AVENTURA);

        mision.volverAlTablon();

        assertEquals(EstadoMision.TABLON, mision.estado());
    }

    @Test
    void soloSePuedeVolverAlTablonDesdeEnAventura() {
        assertThrows(ExcepcionNegocio.class, () -> misionEn(EstadoMision.TABLON).volverAlTablon());
        assertThrows(ExcepcionNegocio.class, () -> misionEn(EstadoMision.GLORIA).volverAlTablon());
    }

    @Test
    void completarPasaDeEnAventuraAGloria() {
        Mision mision = misionEn(EstadoMision.EN_AVENTURA);

        mision.completar();

        assertEquals(EstadoMision.GLORIA, mision.estado());
    }

    @Test
    void soloSePuedeCompletarDesdeEnAventura() {
        assertThrows(ExcepcionNegocio.class, () -> misionEn(EstadoMision.TABLON).completar());
        assertThrows(ExcepcionNegocio.class, () -> misionEn(EstadoMision.GLORIA).completar());
    }

    @Test
    void editarCambiaLosDatos() {
        Mision mision = misionEn(EstadoMision.TABLON);
        DatosMision nuevos = new DatosMision("Rendir final", "Química", null, Dificultad.EPICA, Prioridad.ALTA);

        mision.editar(nuevos);

        assertEquals(nuevos, mision.datos());
    }

    @Test
    void editarUnaMisionEnGloriaFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> misionEn(EstadoMision.GLORIA).editar(DATOS));

        assertEquals("Una misión en Gloria no se puede editar.", error.getMessage());
    }

    @Test
    void misionConFechaPasadaEstaVencida() {
        assertTrue(misionConFecha(AYER, EstadoMision.TABLON).estaVencida(HOY));
        assertTrue(misionConFecha(AYER, EstadoMision.EN_AVENTURA).estaVencida(HOY));
    }

    @Test
    void misionSinFechaOQueVenceHoyNoEstaVencida() {
        assertFalse(misionConFecha(null, EstadoMision.TABLON).estaVencida(HOY));
        assertFalse(misionConFecha(HOY, EstadoMision.TABLON).estaVencida(HOY));
    }

    @Test
    void misionEnGloriaNoEstaVencida() {
        assertFalse(misionConFecha(AYER, EstadoMision.GLORIA).estaVencida(HOY));
    }

    @Test
    void misionPenalizadaNoVuelveAEstarVencida() {
        Mision mision = misionConFecha(AYER, EstadoMision.TABLON);

        mision.marcarPenalizada();

        assertFalse(mision.estaVencida(HOY));
    }
}
