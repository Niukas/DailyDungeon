package niuka.dailydungeon.dominio.modelo;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HeroeTest {

    private Heroe guerreroNuevo() {
        return Heroe.nuevo(1, "Arturo", ClasesDePrueba.guerrero());
    }

    @Test
    void heroeNuevoEmpiezaEnNivelUnoSinXp() {
        Heroe heroe = guerreroNuevo();

        assertEquals(1, heroe.nivel());
        assertEquals(0, heroe.xp());
        assertEquals(100, heroe.xpParaSubirDeNivel());
    }

    @Test
    void heroeNuevoTieneLosAtributosInicialesDeSuClase() {
        Heroe heroe = guerreroNuevo();

        for (Atributo atributo : Atributo.values()) {
            assertEquals(5, heroe.valorDe(atributo));
        }
    }

    @Test
    void heroeNuevoEmpiezaConLaVidaAlMaximo() {
        Heroe heroe = guerreroNuevo();

        assertEquals(100, heroe.vidaMaxima());
        assertEquals(heroe.vidaMaxima(), heroe.hpActual());
    }

    @Test
    void ganarXpSinLlegarAlLimiteNoSubeDeNivel() {
        Heroe heroe = guerreroNuevo();

        heroe.ganarXp(50);

        assertEquals(1, heroe.nivel());
        assertEquals(50, heroe.xp());
    }

    @Test
    void ganarXpJustoHastaElLimiteSubeDeNivel() {
        Heroe heroe = guerreroNuevo();

        heroe.ganarXp(100);

        assertEquals(2, heroe.nivel());
        assertEquals(0, heroe.xp());
    }

    @Test
    void laXpQueSobraPasaAlNivelSiguiente() {
        Heroe heroe = guerreroNuevo();

        heroe.ganarXp(130);

        assertEquals(2, heroe.nivel());
        assertEquals(30, heroe.xp());
    }

    @Test
    void conMuchaXpPuedeSubirVariosNivelesDeUnaVez() {
        Heroe heroe = guerreroNuevo();

        // Nivel 1 → 2 cuesta 100, nivel 2 → 3 cuesta 200: sobran 50
        heroe.ganarXp(350);

        assertEquals(3, heroe.nivel());
        assertEquals(50, heroe.xp());
    }

    @Test
    void subirDeNivelSumaElCrecimientoDeLaClase() {
        Heroe heroe = guerreroNuevo();

        heroe.ganarXp(100);

        // Guerrero: Vitalidad +3, Sabiduría +1, Resistencia +2, Suerte +1
        assertEquals(8, heroe.valorDe(Atributo.VITALIDAD));
        assertEquals(6, heroe.valorDe(Atributo.SABIDURIA));
        assertEquals(7, heroe.valorDe(Atributo.RESISTENCIA));
        assertEquals(6, heroe.valorDe(Atributo.SUERTE));
    }

    @Test
    void subirDeNivelRestauraLaVidaAlNuevoMaximo() {
        Map<Atributo, Integer> atributos = Map.of(
                Atributo.VITALIDAD, 5, Atributo.SABIDURIA, 5, Atributo.RESISTENCIA, 5, Atributo.SUERTE, 5);
        Heroe herido = new Heroe(1, 1, ClasesDePrueba.guerrero(), "Arturo", 1, 0, 10, atributos);

        herido.ganarXp(100);

        // Vitalidad 8 → vida máxima 50 + 10 × 8 = 130
        assertEquals(130, herido.vidaMaxima());
        assertEquals(130, herido.hpActual());
    }

    private Heroe guerrero(int nivel, int xp, int hp, int vitalidad, int sabiduria, int resistencia, int suerte) {
        Map<Atributo, Integer> atributos = Map.of(Atributo.VITALIDAD, vitalidad, Atributo.SABIDURIA, sabiduria,
                Atributo.RESISTENCIA, resistencia, Atributo.SUERTE, suerte);
        return new Heroe(1, 1, ClasesDePrueba.guerrero(), "Arturo", nivel, xp, hp, atributos);
    }

    @Test
    void recibirDanioRestaVida() {
        Heroe heroe = guerreroNuevo();

        boolean cayo = heroe.recibirDanio(30);

        assertFalse(cayo);
        assertEquals(70, heroe.hpActual());
    }

    @Test
    void siLaVidaLlegaACeroPierdeUnNivelYLoQueGanoEnEl() {
        // Guerrero de nivel 3: cada atributo tiene 5 + 2 × su crecimiento
        Heroe heroe = guerrero(3, 40, 10, 11, 7, 9, 7);

        boolean cayo = heroe.recibirDanio(10);

        assertTrue(cayo);
        assertEquals(2, heroe.nivel());
        assertEquals(8, heroe.valorDe(Atributo.VITALIDAD));
        assertEquals(6, heroe.valorDe(Atributo.SABIDURIA));
        assertEquals(7, heroe.valorDe(Atributo.RESISTENCIA));
        assertEquals(6, heroe.valorDe(Atributo.SUERTE));
        assertEquals(0, heroe.xp());
        // Vida máxima con Vitalidad 8 = 130 → la mitad es 65
        assertEquals(65, heroe.hpActual());
    }

    @Test
    void enNivelUnoNoBajaDeNivelNiDeAtributos() {
        Heroe heroe = guerreroNuevo();
        heroe.ganarXp(30);

        heroe.recibirDanio(500);

        assertEquals(1, heroe.nivel());
        assertEquals(5, heroe.valorDe(Atributo.VITALIDAD));
        assertEquals(0, heroe.xp());
        assertEquals(50, heroe.hpActual());
    }

    @Test
    void losAtributosNoBajanDelValorInicialDeLaClase() {
        Heroe heroe = guerrero(2, 0, 1, 5, 5, 5, 5);

        heroe.recibirDanio(1);

        for (Atributo atributo : Atributo.values()) {
            assertEquals(5, heroe.valorDe(atributo));
        }
    }
}
