package niuka.dailydungeon.dominio.reglas;

import java.util.random.RandomGenerator;

public final class ReglasJuego {

    public static final int NIVEL_INICIAL = 1;
    public static final int VIDA_BASE = 50;
    public static final int VIDA_POR_VITALIDAD = 10;
    public static final int XP_POR_NIVEL = 100;
    public static final double BONUS_XP_POR_SABIDURIA = 0.02;
    public static final double CRITICO_POR_SUERTE = 0.02;
    public static final double CRITICO_MAXIMO = 0.50;
    public static final double MULTIPLICADOR_CRITICO = 1.5;

    private ReglasJuego() {
    }

    public static int vidaMaxima(int vitalidad) {
        return VIDA_BASE + VIDA_POR_VITALIDAD * vitalidad;
    }

    public static int xpParaSubir(int nivel) {
        return XP_POR_NIVEL * nivel;
    }

    // Se redondea una sola vez, al final, para no perder XP en los pasos intermedios.
    public static int xpGanada(int xpBase, int sabiduria, boolean critico) {
        double xp = xpBase * (1 + BONUS_XP_POR_SABIDURIA * sabiduria);
        if (critico) {
            xp = xp * MULTIPLICADOR_CRITICO;
        }
        return (int) Math.round(xp);
    }

    public static double probabilidadCritico(int suerte) {
        return Math.min(CRITICO_POR_SUERTE * suerte, CRITICO_MAXIMO);
    }

    // El azar llega de afuera para que los tests puedan decidir el resultado.
    public static boolean esCritico(int suerte, RandomGenerator azar) {
        return azar.nextDouble() < probabilidadCritico(suerte);
    }
}
