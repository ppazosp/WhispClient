module whisp.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.desktop;
    requires jdk.jshell;

    exports whisp.client;
    opens whisp.client to javafx.fxml;
    exports whisp.interfaces;
    opens whisp.interfaces to javafx.fxml;
}