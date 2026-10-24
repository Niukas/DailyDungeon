package niuka.dailydungeon.presentacion;

import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Recompensa;
import niuka.dailydungeon.dominio.modelo.Tablero;
import niuka.dailydungeon.dominio.modelo.Usuario;

import java.util.Optional;

// Recuerda lo que el usuario tiene abierto, para que cada pantalla sepa qué mostrar al cargarse.
public class Sesion {

    private Usuario usuarioActual;
    private Tablero tableroActual;
    private Mision misionEnEdicion;
    private Recompensa ultimaRecompensa;

    public void iniciar(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrar() {
        usuarioActual = null;
        tableroActual = null;
        misionEnEdicion = null;
        ultimaRecompensa = null;
    }

    public void abrirTablero(Tablero tablero) {
        tableroActual = tablero;
    }

    public void prepararMisionNueva() {
        misionEnEdicion = null;
    }

    public void prepararEdicion(Mision mision) {
        misionEnEdicion = mision;
    }

    public void guardarRecompensa(Recompensa recompensa) {
        ultimaRecompensa = recompensa;
    }

    public Usuario usuarioActual() {
        return usuarioActual;
    }

    public Tablero tableroActual() {
        return tableroActual;
    }

    public Recompensa ultimaRecompensa() {
        return ultimaRecompensa;
    }

    // Vacío cuando el formulario es para una misión nueva.
    public Optional<Mision> misionEnEdicion() {
        return Optional.ofNullable(misionEnEdicion);
    }
}
