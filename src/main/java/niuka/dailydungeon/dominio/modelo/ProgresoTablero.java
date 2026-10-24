package niuka.dailydungeon.dominio.modelo;

public record ProgresoTablero(String nombreTablero, int completadas, int total) {

    // Un tablero sin misiones da 0 % en vez de dividir por cero.
    public int porcentaje() {
        return total == 0 ? 0 : completadas * 100 / total;
    }
}
