package niuka.dailydungeon.dominio.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeroeTest {

    @Test
    void heroeNuevoEmpiezaEnNivelUnoSinXp() {
        Heroe heroe = Heroe.nuevo(1, "Arturo", ClasesDePrueba.guerrero());

        assertEquals(1, heroe.nivel());
        assertEquals(0, heroe.xp());
        assertEquals(100, heroe.xpParaSubirDeNivel());
    }

    @Test
    void heroeNuevoTieneLosAtributosInicialesDeSuClase() {
        Heroe heroe = Heroe.nuevo(1, "Arturo", ClasesDePrueba.guerrero());

        for (Atributo atributo : Atributo.values()) {
            assertEquals(5, heroe.valorDe(atributo));
        }
    }

    @Test
    void heroeNuevoEmpiezaConLaVidaAlMaximo() {
        Heroe heroe = Heroe.nuevo(1, "Arturo", ClasesDePrueba.guerrero());

        assertEquals(100, heroe.vidaMaxima());
        assertEquals(heroe.vidaMaxima(), heroe.hpActual());
    }
}
