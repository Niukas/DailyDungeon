package niuka.dailydungeon.dominio.modelo;

public record Recompensa(int xpGanada, boolean critico, int nivelAnterior, int nivelNuevo) {

    public boolean subioDeNivel() {
        return nivelNuevo > nivelAnterior;
    }
}
