package niuka.dailydungeon.dominio.modelo;

import java.util.Arrays;

// Las tres etapas por las que pasa una misión. El id es el mismo que en la tabla estado_mision.
public enum EstadoMision {

    TABLON(1, "Tablón de misiones"),
    EN_AVENTURA(2, "En aventura"),
    GLORIA(3, "Gloria");

    private final int id;
    private final String nombre;

    EstadoMision(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public static EstadoMision desdeId(int id) {
        return Arrays.stream(values())
                .filter(estado -> estado.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado de misión desconocido: " + id));
    }
}
