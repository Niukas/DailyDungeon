package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioClase;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroe;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Clase;
import niuka.dailydungeon.dominio.modelo.Heroe;

import java.util.List;

public class ServicioHeroe {

    private static final int LARGO_MINIMO_NOMBRE = 3;
    private static final int LARGO_MAXIMO_NOMBRE = 30;

    private final RepositorioHeroe repositorioHeroe;
    private final RepositorioClase repositorioClase;

    public ServicioHeroe(RepositorioHeroe repositorioHeroe, RepositorioClase repositorioClase) {
        this.repositorioHeroe = repositorioHeroe;
        this.repositorioClase = repositorioClase;
    }

    public List<Clase> listarClases() {
        return repositorioClase.listarTodas();
    }

    public boolean tieneHeroe(int idUsuario) {
        return repositorioHeroe.existeParaUsuario(idUsuario);
    }

    public Heroe crearHeroe(int idUsuario, String nombre, Clase clase) {
        String nombreLimpio = nombre == null ? "" : nombre.strip();
        validarNombre(nombreLimpio);
        if (clase == null) {
            throw new ExcepcionNegocio("Elegí una clase para tu héroe.");
        }
        if (tieneHeroe(idUsuario)) {
            throw new ExcepcionNegocio("Ya tenés un héroe.");
        }
        return repositorioHeroe.guardar(Heroe.nuevo(idUsuario, nombreLimpio, clase));
    }

    private void validarNombre(String nombre) {
        if (nombre.length() < LARGO_MINIMO_NOMBRE || nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw new ExcepcionNegocio("El nombre del héroe debe tener entre %d y %d caracteres."
                    .formatted(LARGO_MINIMO_NOMBRE, LARGO_MAXIMO_NOMBRE));
        }
    }
}
