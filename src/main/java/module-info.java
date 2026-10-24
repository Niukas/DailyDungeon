module niuka.dailydungeon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens niuka.dailydungeon.presentacion.controlador to javafx.fxml;
    exports niuka.dailydungeon;
}
