package niuka.dailydungeon.dominio.modelo;

import java.util.Arrays;

public enum Prioridad {

    BAJA(1, "Baja"),
    MEDIA(2, "Media"),
    ALTA(3, "Alta");

    private final int id;
    private final String nombre;

    Prioridad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public static Prioridad desdeId(int id) {
        return Arrays.stream(values())
                .filter(prioridad -> prioridad.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Prioridad desconocida: " + id));
    }

    // Los combos y listas muestran este texto.
    @Override
    public String toString() {
        return nombre;
    }
}
