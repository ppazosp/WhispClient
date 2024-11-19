package whisp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import whisp.Logger;
import whisp.interfaces.ServerInterface;

import javax.rmi.ssl.SslRMIClientSocketFactory;

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
        this.window = stage;
        window.setResizable(false);

        showLoginScene();
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
            e.printStackTrace();
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

    public void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginViewController loginViewController = fxmlLoader.getController();
        loginViewController.initialize(this);

        window.setScene(scene);
        window.show();
    }

    public void showRegisterScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        RegisterViewController registerViewController = fxmlLoader.getController();
        registerViewController.initialize(this);

        window.setScene(scene);
        window.show();
    }

    public void showAuthRegisterScene(String qr) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("authRegister-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        AuthRegisterViewController authRegisterViewController = fxmlLoader.getController();
        authRegisterViewController.initialize(qr, this);

        window.setScene(scene);
        window.show();
    }

    public void showChangePasswordScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("changePassword-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ChangePasswordViewController changePasswordViewController = fxmlLoader.getController();
        changePasswordViewController.initialize(this);

        window.setScene(scene);
        window.show();
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "100.79.5.93");
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
            System.setProperty("javax.rmi.ssl.client.enabledProtocols", "TLSv1.2,TLSv1.3");
            System.setProperty("javax.net.ssl.trustStore", "client.truststore");
            System.setProperty("javax.net.ssl.trustStorePassword", "password");

            SslRMIClientSocketFactory sslRMIClientSocketFactory = new SslRMIClientSocketFactory();


            Registry registry = LocateRegistry.getRegistry("100.79.5.93", 1099, sslRMIClientSocketFactory);

            server = (ServerInterface) registry.lookup("MessagingServer");



            launch(args);

        } catch (Exception e) {
            System.err.println("Error connecting to server");
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password){
        try{
            byte[] salt = server.getSalt(username);
            if (salt == null) return false;

            return server.login(username, Encrypter.getHashedPassword(password, salt));
        }catch (Exception e){
            Logger.error("Cannot connect to server");
        }
        return false;
    }

    public void register(String username, String password) {
        try {
            String[] pass = Encrypter.createHashPassword(password);
            String qr = server.register(username, pass[1], pass[0]);

            showAuthRegisterScene(qr);

        }catch (Exception e){
            Logger.error("Registration failed");
            e.printStackTrace();
        }
    }

    public void changePassword(String username, String password){
        try {
            String[] pass = Encrypter.createHashPassword(password);
            server.changePassword(username, pass[1], pass[0]);
        }catch (Exception e){
            Logger.error("Registration failed");
        }
    }
}
