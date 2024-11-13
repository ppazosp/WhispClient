package whisp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import whisp.interfaces.ServerInterface;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientApplication extends Application {

    private static Client client;
    private static ServerInterface server;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), -1 , -1);
        MenuViewController menuViewController = fxmlLoader.getController();

        menuViewController.initialize(client);
        client.setController(menuViewController);
        server.registerClient(client);

        stage.setTitle("Whisp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (ServerInterface) registry.lookup("MessagingServer");

            String username = args[0];
            client = new Client(username);
            launch();

        } catch (Exception e) {
            System.err.println("Error connecting to server ");
            e.printStackTrace();
        }
    }
    
}
