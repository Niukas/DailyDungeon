package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioClaseEnMemoria;
import niuka.dailydungeon.aplicacion.repositorio.RepositorioHeroeEnMemoria;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Clase;
import niuka.dailydungeon.dominio.modelo.ClasesDePrueba;
import niuka.dailydungeon.dominio.modelo.Heroe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioHeroeTest {

    private static final int ID_USUARIO = 1;

    private final Clase guerrero = ClasesDePrueba.guerrero();
    private ServicioHeroe servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new ServicioHeroe(new RepositorioHeroeEnMemoria(), new RepositorioClaseEnMemoria(List.of(guerrero)));
    }

    @Test
    void listarClasesDevuelveLasDelRepositorio() {
        assertEquals(List.of(guerrero), servicio.listarClases());
    }

    @Test
    void crearHeroeConDatosValidosLoGuarda() {
        Heroe heroe = servicio.crearHeroe(ID_USUARIO, "  Arturo  ", guerrero);

        assertTrue(heroe.id() > 0);
        assertEquals("Arturo", heroe.nombre());
        assertEquals(guerrero, heroe.clase());
    }

    @Test
    void tieneHeroeCambiaDespuesDeCrearlo() {
        assertFalse(servicio.tieneHeroe(ID_USUARIO));

        servicio.crearHeroe(ID_USUARIO, "Arturo", guerrero);

        assertTrue(servicio.tieneHeroe(ID_USUARIO));
    }

    @Test
    void obtenerHeroeDevuelveElHeroeCreado() {
        Heroe creado = servicio.crearHeroe(ID_USUARIO, "Arturo", guerrero);

        assertEquals(creado.id(), servicio.obtenerHeroe(ID_USUARIO).id());
    }

    @Test
    void obtenerHeroeSinHaberloCreadoFalla() {
        assertThrows(ExcepcionNegocio.class, () -> servicio.obtenerHeroe(ID_USUARIO));
    }

    @Test
    void crearHeroeConNombreCortoFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearHeroe(ID_USUARIO, "Al", guerrero));

        assertEquals("El nombre del héroe debe tener entre 3 y 30 caracteres.", error.getMessage());
    }

    @Test
    void crearHeroeConNombreLargoFalla() {
        assertThrows(ExcepcionNegocio.class, () -> servicio.crearHeroe(ID_USUARIO, "a".repeat(31), guerrero));
    }

    @Test
    void crearHeroeSinClaseFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearHeroe(ID_USUARIO, "Arturo", null));

        assertEquals("Elegí una clase para tu héroe.", error.getMessage());
    }

    @Test
    void crearSegundoHeroeParaElMismoUsuarioFalla() {
        servicio.crearHeroe(ID_USUARIO, "Arturo", guerrero);

        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearHeroe(ID_USUARIO, "Merlin", guerrero));

        assertEquals("Ya tenés un héroe.", error.getMessage());
    }
}
