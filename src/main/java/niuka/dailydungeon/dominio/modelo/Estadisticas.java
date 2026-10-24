package niuka.dailydungeon.dominio.modelo;

import java.util.List;

public record Estadisticas(int misionesCompletadas, int xpTotal, List<ProgresoTablero> progresoPorTablero) {
}
