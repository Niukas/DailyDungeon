package niuka.dailydungeon.dominio.modelo;

import niuka.dailydungeon.dominio.ExcepcionNegocio;

import java.time.LocalDate;

public class Mision {

    private final int id;
    private final int idTablero;
    private DatosMision datos;
    private EstadoMision estado;
    private boolean penalizada;

    public Mision(int id, int idTablero, DatosMision datos, EstadoMision estado, boolean penalizada) {
        this.id = id;
        this.idTablero = idTablero;
        this.datos = datos;
        this.estado = estado;
        this.penalizada = penalizada;
    }

    public static Mision nueva(int idTablero, DatosMision datos) {
        return new Mision(0, idTablero, datos, EstadoMision.TABLON, false);
    }

    public Mision conId(int idAsignado) {
        return new Mision(idAsignado, idTablero, datos, estado, penalizada);
    }

    public void empezar() {
        if (estado != EstadoMision.TABLON) {
            throw new ExcepcionNegocio("Solo se puede empezar una misión que está en el Tablón.");
        }
        estado = EstadoMision.EN_AVENTURA;
    }

    public void volverAlTablon() {
        if (estado != EstadoMision.EN_AVENTURA) {
            throw new ExcepcionNegocio("Solo una misión en aventura puede volver al Tablón.");
        }
        estado = EstadoMision.TABLON;
    }

    public void completar() {
        if (estado != EstadoMision.EN_AVENTURA) {
            throw new ExcepcionNegocio("Solo se puede completar una misión que está en aventura.");
        }
        estado = EstadoMision.GLORIA;
    }

    public void editar(DatosMision nuevosDatos) {
        if (estaEnGloria()) {
            throw new ExcepcionNegocio("Una misión en Gloria no se puede editar.");
        }
        datos = nuevosDatos;
    }

    public boolean estaVencida(LocalDate hoy) {
        LocalDate fechaLimite = datos.fechaLimite();
        return fechaLimite != null && fechaLimite.isBefore(hoy) && !estaEnGloria() && !penalizada;
    }

    public void marcarPenalizada() {
        penalizada = true;
    }

    public boolean estaEnGloria() {
        return estado == EstadoMision.GLORIA;
    }

    public int id() {
        return id;
    }

    public int idTablero() {
        return idTablero;
    }

    public DatosMision datos() {
        return datos;
    }

    public EstadoMision estado() {
        return estado;
    }

    public boolean penalizada() {
        return penalizada;
    }
}
