package niuka.dailydungeon.presentacion;

import niuka.dailydungeon.dominio.modelo.Usuario;

public class Sesion {

    private Usuario usuarioActual;

    public void iniciar(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrar() {
        this.usuarioActual = null;
    }

    public Usuario usuarioActual() {
        return usuarioActual;
    }
}
