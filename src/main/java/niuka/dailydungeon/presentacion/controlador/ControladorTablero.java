package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import niuka.dailydungeon.aplicacion.servicio.ServicioMision;
import niuka.dailydungeon.aplicacion.servicio.ServicioRecompensa;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.EstadoMision;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.dominio.modelo.Recompensa;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControladorTablero {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ServicioMision servicioMision;
    private final ServicioRecompensa servicioRecompensa;
    private final Sesion sesion;
    private final Navegador navegador;

    // La misión marcada en cualquiera de las tres columnas, o null si no hay ninguna.
    private Mision misionElegida;

    @FXML
    private Label etiquetaTablero;
    @FXML
    private Label etiquetaTablon;
    @FXML
    private Label etiquetaEnAventura;
    @FXML
    private Label etiquetaGloria;
    @FXML
    private ListView<Mision> listaTablon;
    @FXML
    private ListView<Mision> listaEnAventura;
    @FXML
    private ListView<Mision> listaGloria;
    @FXML
    private Button botonEmpezar;
    @FXML
    private Button botonVolverAlTablon;
    @FXML
    private Button botonCompletar;
    @FXML
    private Button botonEditar;
    @FXML
    private Button botonEliminar;

    public ControladorTablero(ServicioMision servicioMision, ServicioRecompensa servicioRecompensa,
                              Sesion sesion, Navegador navegador) {
        this.servicioMision = servicioMision;
        this.servicioRecompensa = servicioRecompensa;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        etiquetaTablero.setText(sesion.tableroActual().nombre());
        for (ListView<Mision> lista : listas()) {
            prepararLista(lista);
        }
        actualizar();
    }

    private List<ListView<Mision>> listas() {
        return List.of(listaTablon, listaEnAventura, listaGloria);
    }

    private void prepararLista(ListView<Mision> lista) {
        lista.setCellFactory(vista -> new ListCell<>() {
            @Override
            protected void updateItem(Mision mision, boolean vacia) {
                super.updateItem(mision, vacia);
                setText(vacia || mision == null ? null : describir(mision));
            }
        });
        lista.getSelectionModel().selectedItemProperty().addListener((propiedad, anterior, elegida) -> {
            if (elegida != null) {
                elegir(lista, elegida);
            }
        });
    }

    // Una sola misión elegida a la vez: al marcar una, se desmarcan las otras columnas.
    private void elegir(ListView<Mision> listaElegida, Mision mision) {
        for (ListView<Mision> lista : listas()) {
            if (lista != listaElegida) {
                lista.getSelectionModel().clearSelection();
            }
        }
        misionElegida = mision;
        actualizarBotones();
    }

    private void actualizar() {
        List<Mision> misiones = servicioMision.listarMisiones(sesion.tableroActual().id());
        mostrarEtapa(EstadoMision.TABLON, misiones, etiquetaTablon, listaTablon);
        mostrarEtapa(EstadoMision.EN_AVENTURA, misiones, etiquetaEnAventura, listaEnAventura);
        mostrarEtapa(EstadoMision.GLORIA, misiones, etiquetaGloria, listaGloria);
        misionElegida = null;
        actualizarBotones();
    }

    private void mostrarEtapa(EstadoMision etapa, List<Mision> misiones, Label etiqueta, ListView<Mision> lista) {
        List<Mision> deLaEtapa = misiones.stream()
                .filter(mision -> mision.estado() == etapa)
                .toList();
        etiqueta.setText("%s (%d)".formatted(etapa.nombre(), deLaEtapa.size()));
        lista.getItems().setAll(deLaEtapa);
    }

    private void actualizarBotones() {
        boolean hayElegida = misionElegida != null;
        botonEmpezar.setDisable(!hayElegida || misionElegida.estado() != EstadoMision.TABLON);
        botonVolverAlTablon.setDisable(!hayElegida || misionElegida.estado() != EstadoMision.EN_AVENTURA);
        botonCompletar.setDisable(!hayElegida || misionElegida.estado() != EstadoMision.EN_AVENTURA);
        botonEditar.setDisable(!hayElegida || misionElegida.estaEnGloria());
        botonEliminar.setDisable(!hayElegida);
    }

    private String describir(Mision mision) {
        DatosMision datos = mision.datos();
        String fecha = datos.fechaLimite() == null
                ? "Sin fecha"
                : "Vence " + datos.fechaLimite().format(FORMATO_FECHA);
        return "%s\n%s  ·  Prioridad %s\n%s"
                .formatted(datos.titulo(), datos.dificultad(), datos.prioridad(), fecha);
    }

    @FXML
    private void empezarMision() {
        try {
            servicioMision.empezarMision(misionElegida);
            actualizar();
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void volverAlTablon() {
        try {
            servicioMision.volverAlTablon(misionElegida);
            actualizar();
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void completarMision() {
        try {
            Recompensa recompensa = servicioRecompensa.completarMision(sesion.usuarioActual().id(), misionElegida);
            sesion.guardarRecompensa(recompensa);
            navegador.irA("recompensa");
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void nuevaMision() {
        sesion.prepararMisionNueva();
        navegador.irA("formulario-mision");
    }

    @FXML
    private void editarMision() {
        sesion.prepararEdicion(misionElegida);
        navegador.irA("formulario-mision");
    }

    @FXML
    private void eliminarMision() {
        String pregunta = "¿Seguro que querés eliminar \"%s\"?".formatted(misionElegida.datos().titulo());
        if (Alertas.confirmar(pregunta)) {
            servicioMision.eliminarMision(misionElegida);
            actualizar();
        }
    }

    @FXML
    private void volverATableros() {
        navegador.irA("tableros");
    }
}
