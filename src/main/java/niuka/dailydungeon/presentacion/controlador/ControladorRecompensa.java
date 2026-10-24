package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import niuka.dailydungeon.aplicacion.servicio.ServicioHeroe;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Recompensa;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

public class ControladorRecompensa {

    private final ServicioHeroe servicioHeroe;
    private final Sesion sesion;
    private final Navegador navegador;

    @FXML
    private Label etiquetaXp;
    @FXML
    private Label etiquetaCritico;
    @FXML
    private Label etiquetaNivel;
    @FXML
    private Label etiquetaProgreso;

    public ControladorRecompensa(ServicioHeroe servicioHeroe, Sesion sesion, Navegador navegador) {
        this.servicioHeroe = servicioHeroe;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        Recompensa recompensa = sesion.ultimaRecompensa();
        etiquetaXp.setText("Ganaste %d XP".formatted(recompensa.xpGanada()));
        etiquetaCritico.setText(recompensa.critico()
                ? "¡Golpe crítico! Tu XP se multiplicó por 1,5."
                : "");
        etiquetaNivel.setText(recompensa.subioDeNivel()
                ? "¡Subiste al nivel %d! Tus atributos crecieron y tu vida se restauró."
                        .formatted(recompensa.nivelNuevo())
                : "");

        Heroe heroe = servicioHeroe.obtenerHeroe(sesion.usuarioActual().id());
        etiquetaProgreso.setText("Nivel %d  ·  XP: %d / %d"
                .formatted(heroe.nivel(), heroe.xp(), heroe.xpParaSubirDeNivel()));
    }

    @FXML
    private void volverAlTablero() {
        navegador.irA("tablero");
    }
}
