package niuka.dailydungeon.presentacion.controlador;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import niuka.dailydungeon.aplicacion.servicio.ServicioMision;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.EstadoMision;
import niuka.dailydungeon.dominio.modelo.Mision;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorTablero {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String ELEGI_UNA_MISION = "Elegí una misión de la lista.";

    private final ServicioMision servicioMision;
    private final Sesion sesion;
    private final Navegador navegador;

    private List<Mision> misiones;

    @FXML
    private Label etiquetaTablero;
    @FXML
    private Label etiquetaEtapa;
    @FXML
    private Label etiquetaContadores;
    @FXML
    private Button botonAnterior;
    @FXML
    private Button botonSiguiente;
    @FXML
    private Button botonEditar;
    @FXML
    private Button botonEmpezar;
    @FXML
    private Button botonVolverAlTablon;
    @FXML
    private ListView<Mision> listaMisiones;

    public ControladorTablero(ServicioMision servicioMision, Sesion sesion, Navegador navegador) {
        this.servicioMision = servicioMision;
        this.sesion = sesion;
        this.navegador = navegador;
    }

    @FXML
    private void initialize() {
        etiquetaTablero.setText(sesion.tableroActual().nombre());
        listaMisiones.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(Mision mision, boolean vacia) {
                super.updateItem(mision, vacia);
                setText(vacia || mision == null ? null : describir(mision));
            }
        });
        actualizar();
    }

    private void actualizar() {
        misiones = servicioMision.listarMisiones(sesion.tableroActual().id());
        EstadoMision etapa = sesion.etapaVisible();

        listaMisiones.getItems().setAll(misionesEn(etapa));
        etiquetaEtapa.setText("%s (%d)".formatted(etapa.nombre(), misionesEn(etapa).size()));
        etiquetaContadores.setText(contadoresDeOtrasEtapas(etapa));

        botonAnterior.setDisable(etapa.ordinal() == 0);
        botonSiguiente.setDisable(etapa.ordinal() == EstadoMision.values().length - 1);
        botonEditar.setDisable(etapa == EstadoMision.GLORIA);
        mostrar(botonEmpezar, etapa == EstadoMision.TABLON);
        mostrar(botonVolverAlTablon, etapa == EstadoMision.EN_AVENTURA);
    }

    private List<Mision> misionesEn(EstadoMision etapa) {
        return misiones.stream()
                .filter(mision -> mision.estado() == etapa)
                .toList();
    }

    private String contadoresDeOtrasEtapas(EstadoMision etapaActual) {
        return Arrays.stream(EstadoMision.values())
                .filter(etapa -> etapa != etapaActual)
                .map(etapa -> etapa.nombre() + ": " + misionesEn(etapa).size())
                .collect(Collectors.joining("     "));
    }

    private String describir(Mision mision) {
        DatosMision datos = mision.datos();
        String fecha = datos.fechaLimite() == null
                ? "sin fecha"
                : "vence " + datos.fechaLimite().format(FORMATO_FECHA);
        return "%s  ·  %s  ·  Prioridad %s  ·  %s"
                .formatted(datos.titulo(), datos.dificultad(), datos.prioridad(), fecha);
    }

    // Un botón oculto tampoco ocupa lugar en la fila.
    private void mostrar(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    private Mision misionElegida() {
        Mision elegida = listaMisiones.getSelectionModel().getSelectedItem();
        if (elegida == null) {
            Alertas.mostrarError(ELEGI_UNA_MISION);
        }
        return elegida;
    }

    @FXML
    private void verEtapaAnterior() {
        cambiarEtapa(-1);
    }

    @FXML
    private void verEtapaSiguiente() {
        cambiarEtapa(1);
    }

    private void cambiarEtapa(int desplazamiento) {
        EstadoMision[] etapas = EstadoMision.values();
        sesion.verEtapa(etapas[sesion.etapaVisible().ordinal() + desplazamiento]);
        actualizar();
    }

    @FXML
    private void nuevaMision() {
        sesion.prepararMisionNueva();
        navegador.irA("formulario-mision");
    }

    @FXML
    private void editarMision() {
        Mision elegida = misionElegida();
        if (elegida == null) {
            return;
        }
        sesion.prepararEdicion(elegida);
        navegador.irA("formulario-mision");
    }

    @FXML
    private void eliminarMision() {
        Mision elegida = misionElegida();
        if (elegida == null) {
            return;
        }
        String pregunta = "¿Seguro que querés eliminar \"%s\"?".formatted(elegida.datos().titulo());
        if (Alertas.confirmar(pregunta)) {
            servicioMision.eliminarMision(elegida);
            actualizar();
        }
    }

    @FXML
    private void empezarMision() {
        Mision elegida = misionElegida();
        if (elegida == null) {
            return;
        }
        try {
            servicioMision.empezarMision(elegida);
            actualizar();
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void volverAlTablon() {
        Mision elegida = misionElegida();
        if (elegida == null) {
            return;
        }
        try {
            servicioMision.volverAlTablon(elegida);
            actualizar();
        } catch (ExcepcionNegocio e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    @FXML
    private void volverATableros() {
        navegador.irA("tableros");
    }
}
