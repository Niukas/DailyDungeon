package niuka.dailydungeon.dominio.reglas;

import java.util.random.RandomGenerator;

// Un "azar" que siempre devuelve el mismo número, para que los tests sepan si habrá crítico.
public class AzarFijo implements RandomGenerator {

    private final double valor;

    public AzarFijo(double valor) {
        this.valor = valor;
    }

    @Override
    public double nextDouble() {
        return valor;
    }

    @Override
    public long nextLong() {
        return 0;
    }
}
