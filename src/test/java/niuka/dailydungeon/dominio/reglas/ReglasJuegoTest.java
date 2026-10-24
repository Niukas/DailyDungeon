package niuka.dailydungeon.dominio.reglas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReglasJuegoTest {

    @Test
    void vidaMaximaEsCincuentaMasDiezPorVitalidad() {
        assertEquals(100, ReglasJuego.vidaMaxima(5));
        assertEquals(50, ReglasJuego.vidaMaxima(0));
    }
}
