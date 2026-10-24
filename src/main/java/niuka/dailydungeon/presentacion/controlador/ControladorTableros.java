package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import niuka.dailydungeon.aplicacion.servicio.ServicioHeroe;
import niuka.dailydungeon.aplicacion.servicio.ServicioPenalizacion;
import niuka.dailydungeon.aplicacion.servicio.ServicioTablero;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Atributo;
import niuka.dailydungeon.dominio.modelo.Heroe;
import niuka.dailydungeon.dominio.modelo.Tablero;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

import java.util.Optional;

public class ControladorTableros {

    private static final String ELEGI_UN_TABLERO = "Elegí un tablero de la lista.";

    private final ServicioHeroe servicioHeroe;
    private final ServicioTablero servicioTablero;
    private final ServicioPenalizacion servicioPenalizacion;
    private final Sesion sesion;
    private final Navegador navegador;

    private Heroe heroe;

    @FXML
    private Label etiquetaNombre;
    @FXML
    private Label etiquetaClase;
    @FXML
    private Label etiquetaNivel;
    @FXML
    private Label etiquetaXp;
    @FXML
    private Label etiquetaVida;
    @FXML
    private VBox cajaAtributos;
    @FXML
    private ListView<Tablero> listaTableros;

    public ControladorTableros(ServicioHeroe servicioHeroe, ServicioTablero servicioTablero,
                               ServicioPenalizacion servicioPenalizacion, Sesion sesion, Navegador navegador) {
        this.servicioHeroe = servicioHeroe;
        this.servicioTablero = servicioTablero;
        this.servicioPenalizacion = servicioPenalizacion;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        heroe = servicioHeroe.obtenerHeroe(sesion.usuarioActual().id());
        mostrarHeroe();
        actualizarTableros();
    }

    private void mostrarHeroe() {
        etiquetaNombre.setText(heroe.nombre());
        etiquetaClase.setText("Clase: " + heroe.clase().nombre());
        etiquetaNivel.setText("Nivel: " + heroe.nivel());
        etiquetaXp.setText("XP: %d / %d".formatted(heroe.xp(), heroe.xpParaSubirDeNivel()));
        etiquetaVida.setText("Vida: %d / %d".formatted(heroe.hpActual(), heroe.vidaMaxima()));
        for (Atributo atributo : Atributo.values()) {
            cajaAtributos.getChildren().add(new Label(atributo.nombre() + ": " + heroe.valorDe(atributo)));
        }
    }

    private void actualizarTableros() {
        listaTableros.getItems().setAll(servicioTablero.listarTableros(heroe.id()));
    }

    @FXML
    private void abrirTablero() {
        Tablero elegido = listaTableros.getSelectionModel().getSelectedItem();
        if (elegido == null) {
            Alertas.mostrarError(ELEGI_UN_TABLERO);
            return;
        }
        sesion.abrirTablero(elegido);
        servicioPenalizacion.revisarVencimientos(sesion.usuarioActual().id())
                .ifPresent(penalizacion -> Alertas.mostrarAviso(penalizacion.mensaje()));
        navegador.irA("tablero");
    }

    @FXML
    private void crearTablero() {
        Optional<String> nombre = Alertas.pedirTexto("Nombre del tablero:", "");
        if (nombre.isEmpty()) {
            return;
        }
        try {
            servicioTablero.crearTablero(heroe.id(), nombre.get());
            actualizarTableros();
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void renombrarTablero() {
        Tablero elegido = listaTableros.getSelectionModel().getSelectedItem();
        if (elegido == null) {
            Alertas.mostrarError(ELEGI_UN_TABLERO);
            return;
        }
        Optional<String> nuevoNombre = Alertas.pedirTexto("Nuevo nombre:", elegido.nombre());
        if (nuevoNombre.isEmpty()) {
            return;
        }
        try {
            servicioTablero.renombrarTablero(elegido, nuevoNombre.get());
            actualizarTableros();
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void eliminarTablero() {
        Tablero elegido = listaTableros.getSelectionModel().getSelectedItem();
        if (elegido == null) {
            Alertas.mostrarError(ELEGI_UN_TABLERO);
            return;
        }
        String pregunta = "¿Seguro que querés eliminar \"%s\"? También se eliminan sus misiones."
                .formatted(elegido.nombre());
        if (Alertas.confirmar(pregunta)) {
            servicioTablero.eliminarTablero(elegido);
            actualizarTableros();
        }
    }

    @FXML
    private void verEstadisticas() {
        navegador.irA("estadisticas");
    }

    @FXML
    private void cerrarSesion() {
        sesion.cerrar();
        navegador.irA("inicio-sesion");
    }
}
