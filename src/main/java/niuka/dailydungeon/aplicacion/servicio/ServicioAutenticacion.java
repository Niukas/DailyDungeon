package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.HasheadorContrasena;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioUsuario;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Usuario;

public class ServicioAutenticacion {

    private static final int LARGO_MINIMO_NOMBRE = 3;
    private static final int LARGO_MAXIMO_NOMBRE = 30;
    private static final int LARGO_MINIMO_CONTRASENA = 8;
    private static final String CREDENCIALES_INCORRECTAS = "Usuario o contraseña incorrectos";

    private final RepositorioUsuario repositorioUsuario;
    private final HasheadorContrasena hasheador;

    public ServicioAutenticacion(RepositorioUsuario repositorioUsuario, HasheadorContrasena hasheador) {
        this.repositorioUsuario = repositorioUsuario;
        this.hasheador = hasheador;
    }

    // Pasos del registro: limpiar el nombre, validar todo y recién ahí guardar.
    // La contraseña nunca se guarda como está: se guarda el hash junto con su sal.
    public Usuario registrar(String nombreUsuario, String contrasena, String confirmacion) {
        String nombre = quitarEspacios(nombreUsuario);
        validarNombre(nombre);
        validarContrasena(contrasena, confirmacion);

        String sal = hasheador.generarSal();
        String hash = hasheador.hashear(contrasena, sal);
        return repositorioUsuario.guardar(new Usuario(0, nombre, hash, sal));
    }

    public Usuario iniciarSesion(String nombreUsuario, String contrasena) {
        // Uso el mismo mensaje si no existe el usuario o si la contraseña está mal,
        // así nadie puede averiguar qué usuarios existen.
        Usuario usuario = repositorioUsuario.buscarPorNombre(quitarEspacios(nombreUsuario))
                .orElseThrow(() -> new ExcepcionNegocio(CREDENCIALES_INCORRECTAS));
        if (contrasena == null || !hasheador.coincide(contrasena, usuario.contrasenaSal(), usuario.contrasenaHash())) {
            throw new ExcepcionNegocio(CREDENCIALES_INCORRECTAS);
        }
        return usuario;
    }

    private String quitarEspacios(String texto) {
        return texto == null ? "" : texto.strip();
    }

    private void validarNombre(String nombre) {
        if (nombre.length() < LARGO_MINIMO_NOMBRE || nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw new ExcepcionNegocio("El nombre de usuario debe tener entre %d y %d caracteres."
                    .formatted(LARGO_MINIMO_NOMBRE, LARGO_MAXIMO_NOMBRE));
        }
        if (repositorioUsuario.existeNombre(nombre)) {
            throw new ExcepcionNegocio("Ese nombre de usuario ya está en uso.");
        }
    }

    private void validarContrasena(String contrasena, String confirmacion) {
        if (contrasena == null || contrasena.length() < LARGO_MINIMO_CONTRASENA) {
            throw new ExcepcionNegocio("La contraseña debe tener al menos %d caracteres."
                    .formatted(LARGO_MINIMO_CONTRASENA));
        }
        if (!contrasena.equals(confirmacion)) {
            throw new ExcepcionNegocio("Las contraseñas no coinciden.");
        }
    }
}
