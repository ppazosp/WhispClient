module whisp.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.desktop;
    requires jdk.jshell;

    exports whisp.client.backend;
    opens whisp.client.backend to javafx.fxml;
    exports whisp.interfaces;
    opens whisp.interfaces to javafx.fxml;
    exports whisp.utils;
    opens whisp.utils to javafx.fxml;
    exports whisp.client.gui;
    opens whisp.client.gui to javafx.fxml;
    exports whisp.client.gui.entities;
    opens whisp.client.gui.entities to javafx.fxml;
    exports whisp.client;
    opens whisp.client to javafx.fxml;
    exports whisp.utils.encryption;
    opens whisp.utils.encryption to javafx.fxml;
}