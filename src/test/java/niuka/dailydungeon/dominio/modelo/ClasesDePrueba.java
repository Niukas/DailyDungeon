package niuka.dailydungeon.dominio.modelo;

import java.util.Map;

public final class ClasesDePrueba {

    private ClasesDePrueba() {
    }

    // Mismos valores que el Guerrero de schema.sql.
    public static Clase guerrero() {
        return new Clase(1, "Guerrero", "Aguanta los descuidos.",
                Map.of(Atributo.VITALIDAD, 5, Atributo.SABIDURIA, 5, Atributo.RESISTENCIA, 5, Atributo.SUERTE, 5),
                Map.of(Atributo.VITALIDAD, 3, Atributo.SABIDURIA, 1, Atributo.RESISTENCIA, 2, Atributo.SUERTE, 1));
    }
}
