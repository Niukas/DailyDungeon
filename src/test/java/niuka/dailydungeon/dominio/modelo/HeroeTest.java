package niuka.dailydungeon.dominio.modelo;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
