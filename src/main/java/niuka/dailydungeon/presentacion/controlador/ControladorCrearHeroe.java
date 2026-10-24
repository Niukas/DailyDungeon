package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import niuka.dailydungeon.aplicacion.servicio.ServicioHeroe;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Clase;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

public class ControladorCrearHeroe {

    private final ServicioHeroe servicioHeroe;
    private final Sesion sesion;
    private final Navegador navegador;

    @FXML
    private TextField campoNombre;
    @FXML
    private ComboBox<Clase> comboClase;
    @FXML
    private Label etiquetaDescripcion;

    public ControladorCrearHeroe(ServicioHeroe servicioHeroe, Sesion sesion, Navegador navegador) {
        this.servicioHeroe = servicioHeroe;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        comboClase.getItems().setAll(servicioHeroe.listarClases());
    }

    @FXML
    private void mostrarDescripcion() {
        etiquetaDescripcion.setText(comboClase.getValue().descripcion());
    }

    @FXML
    private void crearHeroe() {
        try {
            servicioHeroe.crearHeroe(sesion.usuarioActual().id(), campoNombre.getText(), comboClase.getValue());
            navegador.irA("tableros");
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }
}
