package niuka.dailydungeon.dominio.reglas;

import java.util.random.RandomGenerator;

// Acá están todos los números del juego juntos. Si quiero cambiar el balance
// (por ejemplo, cuánto daño hace una misión vencida), lo cambio en un solo lugar.
public final class ReglasJuego {

    public static final int NIVEL_INICIAL = 1;
    public static final int VIDA_BASE = 50;
    public static final int VIDA_POR_VITALIDAD = 10;
    public static final int XP_POR_NIVEL = 100;
    public static final double BONUS_XP_POR_SABIDURIA = 0.02;
    public static final double CRITICO_POR_SUERTE = 0.02;
    public static final double CRITICO_MAXIMO = 0.50;
    public static final double MULTIPLICADOR_CRITICO = 1.5;
    public static final int DANIO_BASE = 10;
    public static final int DANIO_MINIMO = 1;

    private ReglasJuego() {
    }

    public static int vidaMaxima(int vitalidad) {
        return VIDA_BASE + VIDA_POR_VITALIDAD * vitalidad;
    }

    public static int xpParaSubir(int nivel) {
        return XP_POR_NIVEL * nivel;
    }

    // XP base + 2 % por cada punto de Sabiduría, y × 1,5 si fue crítico.
    // Redondeo una sola vez al final para no ir perdiendo decimales.
    public static int xpGanada(int xpBase, int sabiduria, boolean critico) {
        double xp = xpBase * (1 + BONUS_XP_POR_SABIDURIA * sabiduria);
        if (critico) {
            xp = xp * MULTIPLICADOR_CRITICO;
        }
        return (int) Math.round(xp);
    }

    // 2 % por punto de Suerte, pero con Math.min nunca pasa de 50 %.
    public static double probabilidadCritico(int suerte) {
        return Math.min(CRITICO_POR_SUERTE * suerte, CRITICO_MAXIMO);
    }

    // Ojo: resistencia / 2 es división entera (5 / 2 = 2), entonces con Resistencia 5 el daño es 8.
    // Con Math.max me aseguro de que el daño nunca sea menos de 1.
    public static int danioPorVencimiento(int resistencia) {
        return Math.max(DANIO_MINIMO, DANIO_BASE - resistencia / 2);
    }

    // nextDouble() da un número al azar entre 0 y 1. Si sale menor que la probabilidad, es crítico.
    // El azar lo recibo de afuera para que en los tests pueda elegir qué sale.
    public static boolean esCritico(int suerte, RandomGenerator azar) {
        return azar.nextDouble() < probabilidadCritico(suerte);
    }
}
