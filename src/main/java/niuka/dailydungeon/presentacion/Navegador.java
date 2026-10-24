package niuka.dailydungeon.presentacion;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.io.UncheckedIOException;

public class Navegador {

    private static final String RUTA_VISTAS = "/niuka/dailydungeon/vistas/";
    private static final String RUTA_ESTILOS = "/niuka/dailydungeon/estilos/estilos.css";
    private static final double ANCHO = 1000;
    private static final double ALTO = 650;

    private final Stage ventana;
    // Permite que App cree cada controlador pasándole sus servicios por constructor.
    private final Callback<Class<?>, Object> fabricaControladores;

    public Navegador(Stage ventana, Callback<Class<?>, Object> fabricaControladores) {
        this.ventana = ventana;
        this.fabricaControladores = fabricaControladores;
    }

    public void irA(String vista) {
        Scene escena = new Scene(cargarVista(vista), ANCHO, ALTO);
        escena.getStylesheets().add(Navegador.class.getResource(RUTA_ESTILOS).toExternalForm());
        ventana.setScene(escena);
    }

    private Parent cargarVista(String vista) {
        FXMLLoader cargador = new FXMLLoader(Navegador.class.getResource(RUTA_VISTAS + vista + ".fxml"));
        cargador.setControllerFactory(fabricaControladores);
        try {
            return cargador.load();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar la vista " + vista, e);
        }
    }
}
