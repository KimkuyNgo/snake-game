    module com.example.snakegame {
    requires javafx.controls;
    requires javafx.fxml;

        requires java.desktop;

    opens com.example.snakegame to javafx.fxml;
    exports com.example.snakegame;
}