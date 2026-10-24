package niuka.dailydungeon.presentacion;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public final class Alertas {

    private static final String TITULO = "DailyDungeon";

    private Alertas() {
    }

    public static void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setTitle(TITULO);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    public static boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje);
        alerta.setTitle(TITULO);
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static Optional<String> pedirTexto(String mensaje, String valorInicial) {
        TextInputDialog dialogo = new TextInputDialog(valorInicial);
        dialogo.setTitle(TITULO);
        dialogo.setHeaderText(null);
        dialogo.setContentText(mensaje);
        return dialogo.showAndWait();
    }
}
