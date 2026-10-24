package niuka.dailydungeon.dominio.modelo;

public record Penalizacion(int misionesVencidas, int danioTotal, boolean cayo) {

    public String mensaje() {
        String misiones = misionesVencidas == 1 ? "1 misión vencida" : misionesVencidas + " misiones vencidas";
        String mensaje = "Tenías %s y tu héroe perdió %d de vida.".formatted(misiones, danioTotal);
        if (cayo) {
            mensaje += " Su vida llegó a 0: bajó un nivel (nunca baja del 1) y recuperó la mitad de su vida.";
        }
        return mensaje;
    }
}
