package niuka.dailydungeon.dominio.modelo;

import java.util.Map;

public record Clase(
        int id,
        String nombre,
        String descripcion,
        Map<Atributo, Integer> valoresIniciales,
        Map<Atributo, Integer> crecimientosPorNivel
) {

    public Clase {
        valoresIniciales = Map.copyOf(valoresIniciales);
        crecimientosPorNivel = Map.copyOf(crecimientosPorNivel);
    }

    // El ComboBox de la pantalla de creación muestra este texto.
    @Override
    public String toString() {
        return nombre;
    }
}
