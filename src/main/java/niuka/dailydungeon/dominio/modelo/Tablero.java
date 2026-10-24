package niuka.dailydungeon.dominio.modelo;

public record Tablero(int id, int idHeroe, String nombre) {

    // La lista de la pantalla de tableros muestra este texto.
    @Override
    public String toString() {
        return nombre;
    }
}
