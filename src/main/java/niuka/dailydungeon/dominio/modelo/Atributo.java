package niuka.dailydungeon.dominio.modelo;

import java.util.Arrays;

public enum Atributo {

    VITALIDAD(1, "Vitalidad"),
    SABIDURIA(2, "Sabiduría"),
    RESISTENCIA(3, "Resistencia"),
    SUERTE(4, "Suerte");

    private final int id;
    private final String nombre;

    Atributo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public static Atributo desdeId(int id) {
        return Arrays.stream(values())
                .filter(atributo -> atributo.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Atributo desconocido: " + id));
    }
}
