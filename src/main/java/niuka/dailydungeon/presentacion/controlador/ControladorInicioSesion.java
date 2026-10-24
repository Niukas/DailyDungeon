package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import niuka.dailydungeon.aplicacion.servicio.ServicioAutenticacion;
import niuka.dailydungeon.aplicacion.servicio.ServicioHeroe;
import niuka.dailydungeon.aplicacion.servicio.ServicioPenalizacion;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Usuario;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

public class ControladorInicioSesion {

    private final ServicioAutenticacion servicioAutenticacion;
    private final ServicioHeroe servicioHeroe;
    private final ServicioPenalizacion servicioPenalizacion;
    private final Sesion sesion;
    private final Navegador navegador;

    @FXML
    private TextField campoUsuario;
    @FXML
    private PasswordField campoContrasena;

    public ControladorInicioSesion(ServicioAutenticacion servicioAutenticacion, ServicioHeroe servicioHeroe,
                                   ServicioPenalizacion servicioPenalizacion, Sesion sesion, Navegador navegador) {
        this.servicioAutenticacion = servicioAutenticacion;
        this.servicioHeroe = servicioHeroe;
        this.servicioPenalizacion = servicioPenalizacion;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void iniciarSesion() {
        try {
            Usuario usuario = servicioAutenticacion.iniciarSesion(campoUsuario.getText(), campoContrasena.getText());
            sesion.iniciar(usuario);
            // Si ya tiene héroe va a sus tableros (antes se revisan las misiones vencidas).
            // Si no, primero tiene que crear el héroe.
            if (servicioHeroe.tieneHeroe(usuario.id())) {
                servicioPenalizacion.revisarVencimientos(usuario.id())
                        .ifPresent(penalizacion -> Alertas.mostrarAviso(penalizacion.mensaje()));
                navegador.irA("tableros");
            } else {
                navegador.irA("crear-heroe");
            }
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void irARegistro() {
        navegador.irA("registro");
    }
}
