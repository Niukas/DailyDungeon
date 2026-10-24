package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import niuka.dailydungeon.aplicacion.servicio.ServicioMision;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.Dificultad;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Prioridad;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

import java.util.Optional;

public class ControladorFormularioMision {

    private final ServicioMision servicioMision;
    private final Sesion sesion;
    private final Navegador navegador;

    @FXML
    private Label etiquetaEncabezado;
    @FXML
    private TextField campoTitulo;
    @FXML
    private TextArea campoDescripcion;
    @FXML
    private DatePicker campoFecha;
    @FXML
    private ComboBox<Dificultad> comboDificultad;
    @FXML
    private ComboBox<Prioridad> comboPrioridad;

    public ControladorFormularioMision(ServicioMision servicioMision, Sesion sesion, Navegador navegador) {
        this.servicioMision = servicioMision;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        comboDificultad.getItems().setAll(Dificultad.values());
        comboPrioridad.getItems().setAll(Prioridad.values());

        Optional<Mision> enEdicion = sesion.misionEnEdicion();
        if (enEdicion.isPresent()) {
            etiquetaEncabezado.setText("Editar misión");
            cargarDatos(enEdicion.get().datos());
        } else {
            etiquetaEncabezado.setText("Nueva misión");
            comboDificultad.setValue(Dificultad.MEDIA);
            comboPrioridad.setValue(Prioridad.MEDIA);
        }
    }

    private void cargarDatos(DatosMision datos) {
        campoTitulo.setText(datos.titulo());
        campoDescripcion.setText(datos.descripcion());
        campoFecha.setValue(datos.fechaLimite());
        comboDificultad.setValue(datos.dificultad());
        comboPrioridad.setValue(datos.prioridad());
    }

    @FXML
    private void guardar() {
        DatosMision datos = new DatosMision(
                campoTitulo.getText(),
                campoDescripcion.getText(),
                campoFecha.getValue(),
                comboDificultad.getValue(),
                comboPrioridad.getValue());
        try {
            Optional<Mision> enEdicion = sesion.misionEnEdicion();
            if (enEdicion.isPresent()) {
                servicioMision.editarMision(enEdicion.get(), datos);
            } else {
                servicioMision.crearMision(sesion.tableroActual().id(), datos);
            }
            navegador.irA("tablero");
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        navegador.irA("tablero");
    }
}
