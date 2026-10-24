package niuka.dailydungeon.dominio.modelo;

import java.util.Arrays;

public enum Dificultad {

    FACIL(1, "Fácil", 10),
    MEDIA(2, "Media", 25),
    DIFICIL(3, "Difícil", 50),
    EPICA(4, "Épica", 100);

    private final int id;
    private final String nombre;
    private final int xpBase;

    Dificultad(int id, String nombre, int xpBase) {
        this.id = id;
        this.nombre = nombre;
        this.xpBase = xpBase;
    }

    public int id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public int xpBase() {
        return xpBase;
    }

    public static Dificultad desdeId(int id) {
        return Arrays.stream(values())
                .filter(dificultad -> dificultad.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Dificultad desconocida: " + id));
    }

    // Los combos y listas muestran este texto.
    @Override
    public String toString() {
        return nombre;
    }
}
