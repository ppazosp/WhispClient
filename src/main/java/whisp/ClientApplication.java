package whisp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import whisp.client.Client;
import whisp.gui.*;
import whisp.gui.entities.Message;
import whisp.interfaces.ClientInterface;
import whisp.utils.Logger;
import whisp.interfaces.ServerInterface;
import whisp.utils.Encrypter;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Base64;
import java.util.HashMap;

public class ClientApplication extends Application {

    /*******************************************************************************************
     * ATTRIBUTES
     *******************************************************************************************/

    private static Client client;
    private static ServerInterface server;
    private static MenuViewController view;

    private Stage window;



    //*******************************************************************************************
    //* MAIN METHODS
    //*******************************************************************************************

    @Override
    public void start(Stage stage) throws IOException {
        Logger.info("Program started, calling login stage...");
        window = stage;
        showLoginStage();
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
            System.setProperty("javax.rmi.ssl.client.enabledProtocols", "TLSv1.2,TLSv1.3");
            System.setProperty("javax.net.ssl.trustStore", "client.truststore");
            System.setProperty("javax.net.ssl.trustStorePassword", "password");

            SslRMIClientSocketFactory sslRMIClientSocketFactory = new SslRMIClientSocketFactory();


            Registry registry = LocateRegistry.getRegistry("localhost", 1099, sslRMIClientSocketFactory);

            server = (ServerInterface) registry.lookup("MessagingServer");



            launch(args);

        } catch (Exception e) {
            System.err.println("Error connecting to server");
        }
    }



    //*******************************************************************************************
    //* WINDOW CONTROL METHODS
    //*******************************************************************************************

    /**
     * Crea la ventana de Login y llama a una función para acoplarle la escena login
     * */
    private void showLoginStage() {
        window.setResizable(false);

        Logger.info("Stage created, calling login scene...");
        showLoginScene();
    }

    /**
     * Crea la ventana principal del programa, carga la escena menú en ella y cierra la ventana de login.
     * Para cargar el menú crea al Cliente con la información obtenida del login y contacta
     * con el servidor para que le proporcione la información necesaria para la GUI
     * (amigos conectados y solicitudes enviadas y recibidas)
     *
     * @param username nombre del usuario que inició sesión
     * */
    public void showMenuStage(String username) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("menu-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Menu scene, check xml filepath");
            System.exit(1);
        }
        view = fxmlLoader.getController();
        view.initialize(this, scene);

        try {
            Logger.info("Trying to create Client...");
            client = new Client(username, this);
        }catch (Exception e) {
            Logger.error("Could not create a new Client");
            System.exit(1);
        }
        try {
            server.registerClient(client);
        } catch (Exception e) {
            Logger.error("Cannot register client, check server connection");
        }

        Stage oldWindow = window;

        window = new Stage();
        window.setTitle( username + "'s Whisp");
        window.setScene(scene);
        window.show();

        oldWindow.close();
    }

    /**
     * Asigna la escena de Login a la stage actual
     *  */
    public void showLoginScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("login-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Login scene, check xml filepath");
            System.exit(1);
        }
        LoginViewController loginViewController = fxmlLoader.getController();
        loginViewController.initialize(this);

        Logger.info("Login scene created, showing it...");
        window.setScene(scene);
        window.show();
    }

    /**
     * Asigna la escena de Registro a la stage actual
     *  */
    public void showRegisterScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("register-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Register scene, check xml filepath");
            System.exit(1);
        }
        RegisterViewController registerViewController = fxmlLoader.getController();
        registerViewController.initialize(this);

        window.setScene(scene);
        window.show();
    }

    /**
     * Asigna la escena de Autentificación al registrarse a la stage actual
     *
     * @param username nombre del nuevo usuario a registrar
     * @param qr image QR a escanear por el usuario codificada como String con Base64
     *  */
    public void showAuthRegisterScene(String username, String qr) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("auth_register-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Auth Register scene, check xml filepath");
            System.exit(1);
        }
        AuthRegisterViewController authRegisterViewController = fxmlLoader.getController();
        authRegisterViewController.initialize(username, qr, this);

        window.setScene(scene);
        window.show();
    }

    /**
     * Asigna la escena de Autentificación al iniciar sesión a la stage actual
     *
     * @param username nombre del usuario que requiere autentificación
     *  */
    public void showAuthScene(String username) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("auth-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Auth scene, check xml filepath");
            System.exit(1);
        }
        AuthViewController authViewController = fxmlLoader.getController();
        authViewController.initialize(username, null, this, 0);

        Logger.info("Auth scene created, showing it...");
        window.setScene(scene);
        window.show();
    }

    /**
     * Asigna la escena de Autentificación al cambiar contraseña a la stage actual
     *
     * @param username nombre del usuario que requiere autentificación
     * @param password nueva contraseña del usuario
     *  */
    public void showAuthChangesScene(String username, String password) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("auth_register-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Auth Changes scene, check xml filepath");
            System.exit(1);
        }
        AuthViewController authViewController = fxmlLoader.getController();
        authViewController.initialize(username, password, this, 1);

        window.setScene(scene);
        window.show();
    }

    /**
     * Asigna la escena de Cambio de contraseña a la stage actual
     *  */
    public void showChangePasswordScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("change_password-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Change Password scene, check xml filepath");
            System.exit(1);
        }
        ChangePasswordViewController changePasswordViewController = fxmlLoader.getController();
        changePasswordViewController.initialize(this);

        window.setScene(scene);
        window.show();
    }



    //*******************************************************************************************
    //* USER MANAGEMENT METHODS
    //*******************************************************************************************

    /**
     * Se conecta al servidor para checkear si las credenciales de inicio de sesión son validas.
     * La contraseña se hashea antes de ser enviada.
     *
     * @param username nombre de usuario
     * @param password contraseña sin hashear
     * @return {@code true}  si las credenciales son válidas, {@code false}  en caso contrario
     *  */
    public boolean login(String username, String password){
        try{
            String salt = server.getSalt(username);
            if (salt == null) return false;
            return server.login(username, Encrypter.getHashedPassword(password, Base64.getDecoder().decode(salt.getBytes())));
        }catch (Exception e){
            Logger.error("Login failed, check server connection");
        }
        return false;
    }

    /**
     * Se conecta al servidor para registrar al nuevo usuario, recibir su qr de autentificación y
     * mostrar la pantalla donde escanearlo. Antes de registrar al usuario comprueba si su username está usado;
     * si lo está muestra, un error
     * La contraseña se hashea antes de ser enviada.
     *
     *
     * @param username nombre del nuevo usuario
     * @param password contraseña sin hashear
     *  */
    public void register(String username, String password) {
        try {
            if(server.checkUsernameAvailability(username)){
                    // TODO: mostrar ventanita
                Logger.error("Username is taken");
                return;
            }

            String[] pass = Encrypter.createHashPassword(password);
            String qr = server.register(username, pass[1], pass[0]);

            showAuthRegisterScene(username, qr);

        }catch (Exception e){
            Logger.error("Registration failed, check server connection");
        }
    }

    /**
     * Se conecta al servidor para cambiar la contraseña del usuario
     * Antes de enviar la nueva contraseña, se hashea
     *
     * @param username nombre del usuario
     * @param password nueva contraseña sin hashear
     *  */
    public void changePassword(String username, String password){
        try {
            Logger.info("Hashing introduced password...");
            String[] pass = Encrypter.createHashPassword(password);
            server.changePassword(username, pass[1], pass[0]);
            Logger.info("Password changed correctly");
        }catch (Exception e){
            Logger.error("Registration failed when connecting to server");
        }
    }

    /**
     * Se conecta al servidor para validar el código de autentificación introducido por el ususario
     *
     * @param username nombre del nuevo usuario
     * @param code código de autentificación
     *  */
    public boolean validate(String username, int code){
        //try {
            return true;
            //return server.validate(username, code);
        //} catch (RemoteException e) {
          //  Logger.error("Could not validate code");
        //}

        //return false;
    }



    //*******************************************************************************************
    //* BACK TO FRONT PIPE METHODS
    //*******************************************************************************************

    /**
     * Recibe un Hashmap de amigos con el nombre de usuario como clave y su referencia de Java RMI como valor
     * y se lo pasa a la GUI como un Set de nombres de usuario
     *
     * @param friends amigos del usuario conectados
     *  */
    public void setFriends(HashMap<String, ClientInterface> friends){
         view.setFriends(friends.keySet());
    }

    public void receiveMessage(String message, String senderName, boolean isText){
        view.receiveMessage(new Message(senderName, message, client.username, isText));
    }

    public void friendConnected(String friend){
        view.friendConnected(friend);
    }

    public void friendDisconnected(String friend){
        view.friendDisconnected(friend);
    }

    public void sendResquest(String friend){
        Logger.info("checking if " + getUsername() + " and " + friend + "...");
        if(client.isFriend(getUsername())){
            Logger.info("They are already friends, returning...");
            return;
        }
        try{
            Logger.info("They are not friends, sending request to server...");
            server.sendRequest(client.username, friend);
        }catch (Exception e){
            Logger.error("Resquest could not be sended, check server connection");
        }

        Logger.info("Resquest sended");
        view.addResquest(client.username, friend);

    }

    public void requestAccepted(String friendName){
        try {
            server.requestAcepted(friendName, client.username);
            Logger.info("Request accepted on server");
        }catch (Exception e) {
            Logger.error("Accepting request failed when connecting to server, check server connection");
        }
    }

    public void addResquest(String requestSender){
        view.addResquest(requestSender, client.username);
    }

    public void cancelRequest(String requestSender){
        try {
            server.cancelRequest(client.username, requestSender);
            Logger.info("Request cancelled on server");
        }catch (Exception e){
            Logger.error("Cancelling request failed when connecting to server, check server connection");
        }

    }

    public void removeResquest(String receiverName){
         view.removeRequest(receiverName);
    }



    /*******************************************************************************************
     * FRONT TO BACK PIPE METHODS
     *******************************************************************************************/

    public String getUsername(){
         return client.username;
    }

    public void sendMessage(Message message){
        client.sendMessage(message.getContent(), message.getReceiver(), message.isText());
    }
}

