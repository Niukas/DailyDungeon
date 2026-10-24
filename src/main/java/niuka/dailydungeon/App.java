package niuka.dailydungeon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import niuka.dailydungeon.aplicacion.repositorio.HasheadorContrasena;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioClase;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroe;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioUsuario;
import niuka.dailydungeon.aplicacion.servicio.ServicioAutenticacion;
import niuka.dailydungeon.aplicacion.servicio.ServicioHeroe;
import niuka.dailydungeon.infraestructura.bd.ConexionBD;
import niuka.dailydungeon.infraestructura.repositorio.RepositorioClaseJDBC;
import niuka.dailydungeon.infraestructura.repositorio.RepositorioHeroeJDBC;
import niuka.dailydungeon.infraestructura.repositorio.RepositorioUsuarioJDBC;
import niuka.dailydungeon.infraestructura.seguridad.HasheadorPBKDF2;
import niuka.dailydungeon.presentacion.Alertas;
import niuka.dailydungeon.presentacion.Navegador;
import niuka.dailydungeon.presentacion.Sesion;
import niuka.dailydungeon.presentacion.controlador.ControladorCrearHeroe;
import niuka.dailydungeon.presentacion.controlador.ControladorInicioSesion;
import niuka.dailydungeon.presentacion.controlador.ControladorRegistro;

public class App extends Application {

    private static final String TITULO = "DailyDungeon";
    private static final String MENSAJE_SIN_CONEXION =
            "No se pudo conectar a la base de datos. ¿Está encendido MySQL en XAMPP?";
    private static final String MENSAJE_ERROR_INESPERADO =
            "Ocurrió un error inesperado. Revisá que MySQL esté encendido en XAMPP.";

    private Navegador navegador;
    private Sesion sesion;
    private ServicioAutenticacion servicioAutenticacion;
    private ServicioHeroe servicioHeroe;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage ventana) {
        ConexionBD conexionBD;
        try {
            conexionBD = new ConexionBD();
        } catch (IllegalStateException e) {
            Alertas.mostrarError(e.getMessage());
            return;
        }
        if (!conexionBD.hayConexion()) {
            Alertas.mostrarError(MENSAJE_SIN_CONEXION);
            return;
        }

        crearDependencias(ventana, conexionBD);
        // Los errores que no son de negocio (por ejemplo, MySQL se apagó) terminan acá.
        Thread.currentThread().setUncaughtExceptionHandler(this::manejarErrorInesperado);

        ventana.setTitle(TITULO);
        navegador.irA("inicio-sesion");
        ventana.show();
    }

    private void crearDependencias(Stage ventana, ConexionBD conexionBD) {
        RepositorioUsuario repositorioUsuario = new RepositorioUsuarioJDBC(conexionBD);
        HasheadorContrasena hasheador = new HasheadorPBKDF2();
        servicioAutenticacion = new ServicioAutenticacion(repositorioUsuario, hasheador);

        RepositorioHeroe repositorioHeroe = new RepositorioHeroeJDBC(conexionBD);
        RepositorioClase repositorioClase = new RepositorioClaseJDBC(conexionBD);
        servicioHeroe = new ServicioHeroe(repositorioHeroe, repositorioClase);

        sesion = new Sesion();
        navegador = new Navegador(ventana, this::crearControlador);
    }

    private Object crearControlador(Class<?> tipo) {
        if (tipo == ControladorInicioSesion.class) {
            return new ControladorInicioSesion(servicioAutenticacion, servicioHeroe, sesion, navegador);
        }
        if (tipo == ControladorRegistro.class) {
            return new ControladorRegistro(servicioAutenticacion, sesion, navegador);
        }
        if (tipo == ControladorCrearHeroe.class) {
            return new ControladorCrearHeroe(servicioHeroe, sesion, navegador);
        }
        throw new IllegalArgumentException("No hay un controlador registrado para " + tipo.getName());
    }

    private void manejarErrorInesperado(Thread hilo, Throwable error) {
        error.printStackTrace();
        Platform.runLater(() -> Alertas.mostrarError(MENSAJE_ERROR_INESPERADO));
    }
}
