package niuka.dailydungeon.dominio.reglas;

public final class ReglasJuego {

    public static final int NIVEL_INICIAL = 1;
    public static final int VIDA_BASE = 50;
    public static final int VIDA_POR_VITALIDAD = 10;
    public static final int XP_POR_NIVEL = 100;

    private ReglasJuego() {
    }

    public static int vidaMaxima(int vitalidad) {
        return VIDA_BASE + VIDA_POR_VITALIDAD * vitalidad;
    }

    public static int xpParaSubir(int nivel) {
        return XP_POR_NIVEL * nivel;
    }
}
