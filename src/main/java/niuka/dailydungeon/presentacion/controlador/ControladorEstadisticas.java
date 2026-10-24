package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import niuka.dailydungeon.aplicacion.servicio.ServicioEstadisticas;
import niuka.dailydungeon.dominio.modelo.Estadisticas;
import niuka.dailydungeon.dominio.modelo.ProgresoTablero;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

public class ControladorEstadisticas {

    private final ServicioEstadisticas servicioEstadisticas;
    private final Sesion sesion;
    private final Navegador navegador;

    @FXML
    private Label etiquetaCompletadas;
    @FXML
    private Label etiquetaXpTotal;
    @FXML
    private VBox cajaProgreso;

    public ControladorEstadisticas(ServicioEstadisticas servicioEstadisticas, Sesion sesion, Navegador navegador) {
        this.servicioEstadisticas = servicioEstadisticas;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        Estadisticas estadisticas = servicioEstadisticas.obtenerEstadisticas(sesion.usuarioActual().id());
        etiquetaCompletadas.setText("Misiones completadas: " + estadisticas.misionesCompletadas());
        etiquetaXpTotal.setText("XP total ganada: " + estadisticas.xpTotal());

        if (estadisticas.progresoPorTablero().isEmpty()) {
            cajaProgreso.getChildren().add(new Label("Todavía no tenés tableros."));
        }
        for (ProgresoTablero progreso : estadisticas.progresoPorTablero()) {
            String texto = "%s: %d de %d completadas (%d %%)".formatted(
                    progreso.nombreTablero(), progreso.completadas(), progreso.total(), progreso.porcentaje());
            cajaProgreso.getChildren().add(new Label(texto));
        }
    }

    @FXML
    private void volverATableros() {
        navegador.irA("tableros");
    }
}
