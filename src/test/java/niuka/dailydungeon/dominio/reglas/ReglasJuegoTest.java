package niuka.dailydungeon.dominio.reglas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglasJuegoTest {

    @Test
    void vidaMaximaEsCincuentaMasDiezPorVitalidad() {
        assertEquals(100, ReglasJuego.vidaMaxima(5));
        assertEquals(50, ReglasJuego.vidaMaxima(0));
    }

    @Test
    void xpParaSubirEsCienPorNivel() {
        assertEquals(100, ReglasJuego.xpParaSubir(1));
        assertEquals(300, ReglasJuego.xpParaSubir(3));
    }

    @Test
    void xpGanadaSumaDosPorCientoPorPuntoDeSabiduria() {
        assertEquals(10, ReglasJuego.xpGanada(10, 0, false));
        assertEquals(110, ReglasJuego.xpGanada(100, 5, false));
        // 25 × 1,10 = 27,5 → se redondea a 28
        assertEquals(28, ReglasJuego.xpGanada(25, 5, false));
    }

    @Test
    void xpGanadaConCriticoMultiplicaPorUnoYMedio() {
        assertEquals(150, ReglasJuego.xpGanada(100, 0, true));
        // 25 × 1,10 × 1,5 = 41,25 → se redondea a 41
        assertEquals(41, ReglasJuego.xpGanada(25, 5, true));
    }

    @Test
    void probabilidadCriticoEsDosPorCientoPorPuntoDeSuerte() {
        assertEquals(0.10, ReglasJuego.probabilidadCritico(5), 0.0001);
    }

    @Test
    void probabilidadCriticoNoPasaDelCincuentaPorCiento() {
        assertEquals(0.50, ReglasJuego.probabilidadCritico(25), 0.0001);
        assertEquals(0.50, ReglasJuego.probabilidadCritico(40), 0.0001);
    }

    @Test
    void danioEsDiezMenosLaMitadDeLaResistencia() {
        assertEquals(10, ReglasJuego.danioPorVencimiento(0));
        // 5 / 2 = 2 (división entera) → 10 − 2 = 8
        assertEquals(8, ReglasJuego.danioPorVencimiento(5));
        assertEquals(7, ReglasJuego.danioPorVencimiento(7));
    }

    @Test
    void danioNuncaEsMenorQueUno() {
        assertEquals(1, ReglasJuego.danioPorVencimiento(18));
        assertEquals(1, ReglasJuego.danioPorVencimiento(30));
    }

    @Test
    void esCriticoCuandoElAzarQuedaPorDebajoDeLaProbabilidad() {
        // Con Suerte 5 la probabilidad es 0,10
        assertTrue(ReglasJuego.esCritico(5, new AzarFijo(0.05)));
        assertFalse(ReglasJuego.esCritico(5, new AzarFijo(0.10)));
    }
}
