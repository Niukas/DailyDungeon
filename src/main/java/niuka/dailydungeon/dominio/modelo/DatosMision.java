package niuka.dailydungeon.dominio.modelo;

import java.time.LocalDate;

// Lo que el usuario completa en el formulario. La descripción y la fecha límite pueden ser null.
public record DatosMision(
        String titulo,
        String descripcion,
        LocalDate fechaLimite,
        Dificultad dificultad,
        Prioridad prioridad
) {
}
