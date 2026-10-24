package niuka.dailydungeon.infraestructura.seguridad;

import niuka.dailydungeon.aplicacion.repositorio.HasheadorContrasena;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class HasheadorPBKDF2 implements HasheadorContrasena {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 65_536;
    private static final int LARGO_HASH_BITS = 256;
    private static final int LARGO_SAL_BYTES = 16;

    private final SecureRandom azar = new SecureRandom();

    @Override
    public String generarSal() {
        byte[] sal = new byte[LARGO_SAL_BYTES];
        azar.nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal);
    }

    @Override
    public String hashear(String contrasena, String sal) {
        return Base64.getEncoder().encodeToString(calcularHash(contrasena, sal));
    }

    @Override
    public boolean coincide(String contrasena, String sal, String hashGuardado) {
        byte[] hashCalculado = calcularHash(contrasena, sal);
        // isEqual tarda lo mismo sin importar dónde difieren los bytes, así no da pistas por tiempo.
        return MessageDigest.isEqual(hashCalculado, Base64.getDecoder().decode(hashGuardado));
    }

    private byte[] calcularHash(String contrasena, String sal) {
        PBEKeySpec especificacion = new PBEKeySpec(
                contrasena.toCharArray(), Base64.getDecoder().decode(sal), ITERACIONES, LARGO_HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
