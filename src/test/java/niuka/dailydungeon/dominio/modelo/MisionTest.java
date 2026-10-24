package niuka.dailydungeon.dominio.modelo;

import niuka.dailydungeon.dominio.ExcepcionNegocio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MisionTest {

    private static final DatosMision DATOS = new DatosMision("Estudiar", null, null, Dificultad.MEDIA, Prioridad.MEDIA);

    private Mision misionEn(EstadoMision estado) {
        return new Mision(1, 1, DATOS, estado);
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
}
