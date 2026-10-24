package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioMision;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.DatosMision;
import niuka.dailydungeon.dominio.modelo.Mision;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ServicioMision {

    private static final int LARGO_MAXIMO_TITULO = 100;

    private final RepositorioMision repositorioMision;

    public ServicioMision(RepositorioMision repositorioMision) {
        this.repositorioMision = repositorioMision;
    }

    public List<Mision> listarMisiones(int idTablero) {
        return repositorioMision.listarPorTablero(idTablero);
    }

    public Mision crearMision(int idTablero, DatosMision datos) {
        DatosMision datosLimpios = limpiar(datos);
        validar(datosLimpios);
        validarFechaNoPasada(datosLimpios.fechaLimite());
        return repositorioMision.guardar(Mision.nueva(idTablero, datosLimpios));
    }

    public void editarMision(Mision mision, DatosMision nuevosDatos) {
        DatosMision datosLimpios = limpiar(nuevosDatos);
        validar(datosLimpios);
        // Solo se valida si cambió: así se puede editar el título de una misión que ya venció.
        if (!Objects.equals(datosLimpios.fechaLimite(), mision.datos().fechaLimite())) {
            validarFechaNoPasada(datosLimpios.fechaLimite());
        }
        mision.editar(datosLimpios);
        repositorioMision.actualizar(mision);
    }

    public void empezarMision(Mision mision) {
        mision.empezar();
        repositorioMision.actualizar(mision);
    }

    public void volverAlTablon(Mision mision) {
        mision.volverAlTablon();
        repositorioMision.actualizar(mision);
    }

    public void eliminarMision(Mision mision) {
        repositorioMision.eliminar(mision.id());
    }

    private DatosMision limpiar(DatosMision datos) {
        String titulo = datos.titulo() == null ? "" : datos.titulo().strip();
        String descripcion = datos.descripcion() == null || datos.descripcion().isBlank()
                ? null
                : datos.descripcion().strip();
        return new DatosMision(titulo, descripcion, datos.fechaLimite(), datos.dificultad(), datos.prioridad());
    }

    private void validar(DatosMision datos) {
        if (datos.titulo().isEmpty()) {
            throw new ExcepcionNegocio("El título de la misión no puede estar vacío.");
        }
        if (datos.titulo().length() > LARGO_MAXIMO_TITULO) {
            throw new ExcepcionNegocio("El título de la misión puede tener hasta %d caracteres."
                    .formatted(LARGO_MAXIMO_TITULO));
        }
        if (datos.dificultad() == null) {
            throw new ExcepcionNegocio("Elegí una dificultad.");
        }
        if (datos.prioridad() == null) {
            throw new ExcepcionNegocio("Elegí una prioridad.");
        }
    }

    private void validarFechaNoPasada(LocalDate fechaLimite) {
        if (fechaLimite != null && fechaLimite.isBefore(LocalDate.now())) {
            throw new ExcepcionNegocio("La fecha límite no puede ser anterior a hoy.");
        }
    }
}
