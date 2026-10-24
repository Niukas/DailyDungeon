package niuka.dailydungeon.dominio.modelo;

public record ProgresoTablero(String nombreTablero, int completadas, int total) {

    // Si el tablero no tiene misiones devuelvo 0, porque dividir por 0 da error.
    // Multiplico por 100 antes de dividir para que la división entera no dé 0.
    public int porcentaje() {
        return total == 0 ? 0 : completadas * 100 / total;
    }
}
