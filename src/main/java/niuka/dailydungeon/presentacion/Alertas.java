package niuka.dailydungeon.presentacion;

import javafx.scene.control.Alert;

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
}
