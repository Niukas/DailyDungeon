package niuka.dailydungeon.aplicacion.repositorio;

public interface HasheadorContrasena {

    String generarSal();

    String hashear(String contrasena, String sal);

    boolean coincide(String contrasena, String sal, String hashGuardado);
}
