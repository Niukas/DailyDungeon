package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioUsuarioEnMemoria;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Usuario;
import niuka.dailydungeon.infraestructura.seguridad.HasheadorPBKDF2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioAutenticacionTest {

    private static final String CONTRASENA = "secreta123";

    private ServicioAutenticacion servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new ServicioAutenticacion(new RepositorioUsuarioEnMemoria(), new HasheadorPBKDF2());
    }

    @Test
    void registrarConDatosValidosGuardaUsuarioConHash() {
        Usuario usuario = servicio.registrar("joaquin", CONTRASENA, CONTRASENA);

        assertTrue(usuario.id() > 0);
        assertEquals("joaquin", usuario.nombreUsuario());
        assertNotEquals(CONTRASENA, usuario.contrasenaHash());
    }

    @Test
    void registrarQuitaEspaciosDelNombre() {
        Usuario usuario = servicio.registrar("  joaquin  ", CONTRASENA, CONTRASENA);

        assertEquals("joaquin", usuario.nombreUsuario());
    }

    @Test
    void registrarConNombreCortoFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.registrar("jo", CONTRASENA, CONTRASENA));

        assertEquals("El nombre de usuario debe tener entre 3 y 30 caracteres.", error.getMessage());
    }

    @Test
    void registrarConNombreLargoFalla() {
        String nombreLargo = "a".repeat(31);

        assertThrows(ExcepcionNegocio.class, () -> servicio.registrar(nombreLargo, CONTRASENA, CONTRASENA));
    }

    @Test
    void registrarConNombreRepetidoFalla() {
        servicio.registrar("joaquin", CONTRASENA, CONTRASENA);

        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.registrar("joaquin", CONTRASENA, CONTRASENA));

        assertEquals("Ese nombre de usuario ya está en uso.", error.getMessage());
    }

    @Test
    void registrarConContrasenaCortaFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.registrar("joaquin", "corta", "corta"));

        assertEquals("La contraseña debe tener al menos 8 caracteres.", error.getMessage());
    }

    @Test
    void registrarConConfirmacionDistintaFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.registrar("joaquin", CONTRASENA, "otra12345"));

        assertEquals("Las contraseñas no coinciden.", error.getMessage());
    }

    @Test
    void iniciarSesionConDatosCorrectosDevuelveUsuario() {
        Usuario registrado = servicio.registrar("joaquin", CONTRASENA, CONTRASENA);

        Usuario logueado = servicio.iniciarSesion("joaquin", CONTRASENA);

        assertEquals(registrado.id(), logueado.id());
    }

    @Test
    void iniciarSesionConContrasenaIncorrectaFalla() {
        servicio.registrar("joaquin", CONTRASENA, CONTRASENA);

        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.iniciarSesion("joaquin", "incorrecta"));

        assertEquals("Usuario o contraseña incorrectos", error.getMessage());
    }

    @Test
    void iniciarSesionConUsuarioInexistenteFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.iniciarSesion("nadie", CONTRASENA));

        assertEquals("Usuario o contraseña incorrectos", error.getMessage());
    }
}
