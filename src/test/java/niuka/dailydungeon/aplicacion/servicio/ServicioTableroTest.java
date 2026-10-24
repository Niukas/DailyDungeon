package niuka.dailydungeon.aplicacion.servicio;

import niuka.dailydungeon.aplicacion.repositorio.RepositorioTableroEnMemoria;
import niuka.dailydungeon.dominio.ExcepcionNegocio;
import niuka.dailydungeon.dominio.modelo.Tablero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServicioTableroTest {

    private static final int ID_HEROE = 1;
    private static final int ID_OTRO_HEROE = 2;

    private ServicioTablero servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new ServicioTablero(new RepositorioTableroEnMemoria());
    }

    @Test
    void crearTableroConNombreValidoLoGuarda() {
        Tablero tablero = servicio.crearTablero(ID_HEROE, "  Facultad  ");

        assertTrue(tablero.id() > 0);
        assertEquals("Facultad", tablero.nombre());
        assertEquals(List.of(tablero), servicio.listarTableros(ID_HEROE));
    }

    @Test
    void crearTableroConNombreVacioFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearTablero(ID_HEROE, "   "));

        assertEquals("El nombre del tablero no puede estar vacío.", error.getMessage());
    }

    @Test
    void crearTableroConNombreLargoFalla() {
        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearTablero(ID_HEROE, "a".repeat(51)));

        assertEquals("El nombre del tablero puede tener hasta 50 caracteres.", error.getMessage());
    }

    @Test
    void crearTableroConNombreRepetidoFallaAunqueCambienLasMayusculas() {
        servicio.crearTablero(ID_HEROE, "Facultad");

        ExcepcionNegocio error = assertThrows(ExcepcionNegocio.class,
                () -> servicio.crearTablero(ID_HEROE, "facultad"));

        assertEquals("Ya tenés un tablero con ese nombre.", error.getMessage());
    }

    @Test
    void otroHeroePuedeUsarElMismoNombre() {
        servicio.crearTablero(ID_HEROE, "Facultad");

        assertDoesNotThrow(() -> servicio.crearTablero(ID_OTRO_HEROE, "Facultad"));
    }

    @Test
    void listarTablerosSoloDevuelveLosDelHeroe() {
        Tablero propio = servicio.crearTablero(ID_HEROE, "Facultad");
        servicio.crearTablero(ID_OTRO_HEROE, "Casa");

        assertEquals(List.of(propio), servicio.listarTableros(ID_HEROE));
    }

    @Test
    void renombrarTableroCambiaElNombre() {
        Tablero tablero = servicio.crearTablero(ID_HEROE, "Facultad");

        servicio.renombrarTablero(tablero, "Universidad");

        assertEquals("Universidad", servicio.listarTableros(ID_HEROE).getFirst().nombre());
    }

    @Test
    void renombrarTableroConSuMismoNombreEstaPermitido() {
        Tablero tablero = servicio.crearTablero(ID_HEROE, "Facultad");

        assertDoesNotThrow(() -> servicio.renombrarTablero(tablero, "FACULTAD"));
    }

    @Test
    void renombrarConElNombreDeOtroTableroFalla() {
        servicio.crearTablero(ID_HEROE, "Facultad");
        Tablero casa = servicio.crearTablero(ID_HEROE, "Casa");

        assertThrows(ExcepcionNegocio.class, () -> servicio.renombrarTablero(casa, "Facultad"));
    }

    @Test
    void eliminarTableroLoSacaDeLaLista() {
        Tablero tablero = servicio.crearTablero(ID_HEROE, "Facultad");

        servicio.eliminarTablero(tablero);

        assertTrue(servicio.listarTableros(ID_HEROE).isEmpty());
    }
}
