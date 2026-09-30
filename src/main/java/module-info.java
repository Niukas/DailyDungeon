module niuka.dailydungeon {
    requires javafx.controls;
    requires javafx.fxml;


    opens niuka.dailydungeon to javafx.fxml;
    exports niuka.dailydungeon;
}