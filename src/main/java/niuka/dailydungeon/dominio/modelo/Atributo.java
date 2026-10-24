package niuka.dailydungeon.dominio.modelo;

import java.util.Arrays;

public enum Atributo {

    VITALIDAD(1),
    SABIDURIA(2),
    RESISTENCIA(3),
    SUERTE(4);

    private final int id;

    Atributo(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public static Atributo desdeId(int id) {
        return Arrays.stream(values())
                .filter(atributo -> atributo.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Atributo desconocido: " + id));
    }
}
