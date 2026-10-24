package niuka.dailydungeon;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;
import niuka.dailydungeon.presentacion.Navegador;

public class App extends Application {

    private static final String TITULO = "DailyDungeon";
    private static final String MENSAJE_SIN_CONEXION =
            "No se pudo conectar a la base de datos. ¿Está encendido MySQL en XAMPP?";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ventana) {
        ConexionBD conexionBD;
        try {
            conexionBD = new ConexionBD();
        } catch (IllegalStateException e) {
            mostrarError(e.getMessage());
            return;
        }
        if (!conexionBD.hayConexion()) {
            mostrarError(MENSAJE_SIN_CONEXION);
            return;
        }

        Navegador navegador = new Navegador(ventana, this::crearControlador);
        ventana.setTitle(TITULO);
        navegador.irA("inicio-sesion");
        ventana.show();
    }

    private Object crearControlador(Class<?> tipo) {
        throw new IllegalArgumentException("No hay un controlador registrado para " + tipo.getName());
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensaje);
        alerta.setTitle(TITULO);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
