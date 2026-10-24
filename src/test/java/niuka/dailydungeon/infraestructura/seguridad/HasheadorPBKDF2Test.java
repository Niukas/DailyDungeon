package niuka.dailydungeon.infraestructura.seguridad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HasheadorPBKDF2Test {

    private final HasheadorPBKDF2 hasheador = new HasheadorPBKDF2();

    @Test
    void laMismaContrasenaCoincide() {
        String sal = hasheador.generarSal();
        String hash = hasheador.hashear("secreta123", sal);

        assertTrue(hasheador.coincide("secreta123", sal, hash));
    }

    @Test
    void otraContrasenaNoCoincide() {
        String sal = hasheador.generarSal();
        String hash = hasheador.hashear("secreta123", sal);

        assertFalse(hasheador.coincide("otra12345", sal, hash));
    }

    @Test
    void salesDistintasDanHashesDistintos() {
        String hash1 = hasheador.hashear("secreta123", hasheador.generarSal());
        String hash2 = hasheador.hashear("secreta123", hasheador.generarSal());

        assertNotEquals(hash1, hash2);
    }
}
