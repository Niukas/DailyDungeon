package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import niuka.dailydungeon.aplicacion.servicio.ServicioAutenticacion;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

public class ControladorInicioSesion {

    private final ServicioAutenticacion servicioAutenticacion;
    private final Sesion sesion;
    private final Navegador navegador;

    @FXML
    private TextField campoUsuario;
    @FXML
    private PasswordField campoContrasena;

    public ControladorInicioSesion(ServicioAutenticacion servicioAutenticacion, Sesion sesion, Navegador navegador) {
        this.servicioAutenticacion = servicioAutenticacion;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void iniciarSesion() {
        try {
            sesion.iniciar(servicioAutenticacion.iniciarSesion(campoUsuario.getText(), campoContrasena.getText()));
            navegador.irA("crear-heroe");
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void irARegistro() {
        navegador.irA("registro");
    }
}
