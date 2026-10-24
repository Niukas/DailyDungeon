package niuka.dailydungeon.dominio.modelo;

public record Usuario(
        int id,
        String nombreUsuario,
        String contrasenaHash,
        String contrasenaSal
) {

    public Usuario {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
    }

    @Override
    public String toString() {
        return "Usuario[id=" + id + ", nombreUsuario=" + nombreUsuario + "]";
    }
}