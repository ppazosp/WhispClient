module whisp.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;


    opens whisp.client to javafx.fxml;
    exports whisp.client;
}