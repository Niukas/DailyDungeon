// Este archivo dice qué usa el proyecto (requires) y qué deja ver a otros (opens / exports).
module niuka.dailydungeon {
    requires javafx.controls;   // botones, listas, campos de texto
    requires javafx.fxml;       // para cargar las pantallas .fxml
    requires java.sql;          // para hablar con MySQL

    // JavaFX necesita entrar a los controladores para llenar los campos @FXML.
    opens niuka.dailydungeon.presentacion.controlador to javafx.fxml;
    // JavaFX necesita ver App para poder arrancarla.
    exports niuka.dailydungeon;
}
