package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioTablero;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Tablero;

import java.util.List;

public class ServicioTablero {

    private static final int LARGO_MAXIMO_NOMBRE = 50;
    // Ningún tablero guardado tiene id 0: se usa al crear, cuando todavía no hay tablero propio que excluir.
    private static final int NINGUN_TABLERO = 0;

    private final RepositorioTablero repositorioTablero;

    public ServicioTablero(RepositorioTablero repositorioTablero) {
        this.repositorioTablero = repositorioTablero;
    }

    public List<Tablero> listarTableros(int idHeroe) {
        return repositorioTablero.listarPorHeroe(idHeroe);
    }

    public Tablero crearTablero(int idHeroe, String nombre) {
        String nombreLimpio = quitarEspacios(nombre);
        validarNombre(nombreLimpio);
        validarQueNoSeRepita(idHeroe, nombreLimpio, NINGUN_TABLERO);
        return repositorioTablero.guardar(new Tablero(0, idHeroe, nombreLimpio));
    }

    public void renombrarTablero(Tablero tablero, String nuevoNombre) {
        String nombreLimpio = quitarEspacios(nuevoNombre);
        validarNombre(nombreLimpio);
        validarQueNoSeRepita(tablero.idHeroe(), nombreLimpio, tablero.id());
        repositorioTablero.renombrar(tablero.id(), nombreLimpio);
    }

    public void eliminarTablero(Tablero tablero) {
        repositorioTablero.eliminar(tablero.id());
    }

    private String quitarEspacios(String texto) {
        return texto == null ? "" : texto.strip();
    }

    private void validarNombre(String nombre) {
        if (nombre.isEmpty()) {
            throw new ExcepcionNegocio("El nombre del tablero no puede estar vacío.");
        }
        if (nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw new ExcepcionNegocio("El nombre del tablero puede tener hasta %d caracteres."
                    .formatted(LARGO_MAXIMO_NOMBRE));
        }
    }

    private void validarQueNoSeRepita(int idHeroe, String nombre, int idTableroExcluido) {
        boolean repetido = repositorioTablero.listarPorHeroe(idHeroe).stream()
                .anyMatch(tablero -> tablero.id() != idTableroExcluido && tablero.nombre().equalsIgnoreCase(nombre));
        if (repetido) {
            throw new ExcepcionNegocio("Ya tenés un tablero con ese nombre.");
        }
    }
}
