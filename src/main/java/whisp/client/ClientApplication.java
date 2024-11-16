package whisp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import whisp.Logger;
import whisp.interfaces.ServerInterface;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientApplication extends Application {

    private static Client client;
    private static ServerInterface server;

    private Stage window;

    // Getter de server
    public static ServerInterface getServer() {
        return server;
    }

    @Override
    public void start(Stage stage) throws IOException {
        showLoginStage(stage);
    }

    private void showLoginStage(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginViewController loginViewController = fxmlLoader.getController();
        loginViewController.initialize(this);

        this.window = stage;

        window.setTitle("Whisp");
        window.setScene(scene);
        window.setResizable(false);
        window.show();
    }

    public void showMenuStage(String username) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MenuViewController menuViewController = fxmlLoader.getController();

        try {
            client = new Client(username);
            menuViewController.initialize(client);
            client.setController(menuViewController);
            server.registerClient(client);
            menuViewController.createDB();
        } catch (Exception e) {
            Logger.error(" Cannot register client:");
        }

        Stage oldWindow = window;

        window = new Stage();

        window.setTitle("Whisp");

        window.setScene(scene);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (menuViewController.searchField.isFocused()) {
                    menuViewController.sendRequest();
                } else if (menuViewController.myMessageField.isFocused() && !menuViewController.sendButton.isDisable()) {
                    menuViewController.sendMessage();
                }
            }
        });

        window.show();

        oldWindow.close();
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (ServerInterface) registry.lookup("MessagingServer");

            launch(args);

        } catch (Exception e) {
            System.err.println("Error connecting to server");
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password){
        try{
            return server.login(username, password);
        }catch (Exception e){
            Logger.error("Cannot connect to server");
        }
        return false;
    }
}
