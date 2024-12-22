module org.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;

    opens org.example.demo to javafx.fxml;
    exports org.example;
    opens org.example to javafx.fxml;
}