package whisp.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import whisp.client.backend.Client;
import whisp.client.gui.*;
import whisp.client.gui.entities.Message;
import whisp.interfaces.ClientInterface;
import whisp.utils.Logger;
import whisp.interfaces.ServerInterface;
import whisp.utils.encryption.PasswordEncrypter;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import java.io.*;
import java.nio.file.Paths;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.HashMap;

public class ClientApplication extends Application {

    //*******************************************************************************************
    //* ATTRIBUTES
    // *******************************************************************************************

    private static Client client;
    private static ServerInterface server;
    private static MenuViewController view;

    private Stage window;
    private Scene currScene;
    private Scene loadingScene;



    //*******************************************************************************************
    //* MAIN METHODS
    //*******************************************************************************************

    public static void shutdown(){
        System.out.println("Closing application...");
        Runtime.getRuntime().halt(0);
    }

    /**
     * Lanza el hilo principal de JavaFX y llama a la función que crea la ventana inicial (login).
     *
     * @param stage ventana default
     *  */
    @Override
    public void start(Stage stage)  {
        Logger.info("Program started, calling login stage...");
        window = stage;
        createLoadingScene();
        showLoginStage();
    }

    /**
     * Main del programa, define las propiedas a usar en JavaRMI, encuentra el Registry del servidor
     * y obtiene la referencia al objeto servidor para comunicarse a posteriori.
     *
     * @param args argumentos por línea de comandos
     *  */
    public static void main(String[] args) {
        try {
            String basePath = "/Applications/Whisp.app/Contents/app/conf";

            //String logPath = Paths.get(basePath, "app.log").toString();
            //PrintStream logStream = new PrintStream(new FileOutputStream(logPath, true));
            //System.setOut(logStream);
            //System.setErr(logStream);
            //System.out.println("Log Path: " + logPath);

            String filePath = Paths.get(basePath, "ips.conf").toString();
            String[] ips = readIpsFromFile(filePath);
            String serverIp = ips[0];
            String clientIp = ips[1];
            System.out.println("Server IP: " + serverIp);
            System.out.println("Client IP: " + clientIp);

            String trustStorePath = Paths.get(basePath, "client.truststore").toString();
            System.out.println("Trust Store Path: " + trustStorePath);

            System.setProperty("java.rmi.server.hostname", clientIp);
            System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
            System.setProperty("javax.rmi.ssl.client.enabledProtocols", "TLSv1.2,TLSv1.3");
            System.setProperty("javax.net.ssl.trustStore", trustStorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", "password");

            SslRMIClientSocketFactory sslRMIClientSocketFactory = new SslRMIClientSocketFactory();
            Registry registry = LocateRegistry.getRegistry(serverIp, 1099, sslRMIClientSocketFactory);

            server = (ServerInterface) registry.lookup("MessagingServer");

            Runtime.getRuntime().addShutdownHook(new Thread(ClientApplication::shutdown));

            launch(args);

        } catch (Exception e) {
            System.err.println("Error connecting to server, check server connection");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Lee el archivo ips.conf y extrae las direcciones IP.
     *
     * @param filePath Ruta al archivo ips.conf.
     * @return Un array de cadenas con las IPs del servidor y del cliente.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static String[] readIpsFromFile(String filePath) throws IOException {
        String serverIp = null;
        String clientIp = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("SERVER_IP")) {
                    serverIp = line.split("=")[1].trim();
                } else if (line.startsWith("CLIENT_IP")) {
                    clientIp = line.split("=")[1].trim();
                }
            }
        }

        if (serverIp == null || clientIp == null) {
            throw new IOException("Missing SERVER_IP or CLIENT_IP in the file.");
        }

        return new String[]{serverIp, clientIp};
    }



    //*******************************************************************************************
    //* WINDOW CONTROL METHODS
    //*******************************************************************************************

    /**
     * Crea la ventana de Login y llama a una función para acoplarle la escena login.
     * */
    private void showLoginStage() {
        window.setResizable(false);

        Logger.info("Stage created, calling login scene...");
        showLoginScene();
    }

    /**
     * Crea la ventana principal del programa, carga la escena menú en ella y cierra la ventana de login.
     * Para cargar el menú crea al Cliente con la información obtenida del login y contacta.
     * con el servidor para que le proporcione la información necesaria para la GUI
     * (amigos conectados y solicitudes enviadas y recibidas).
     *
     * @param username nombre del usuario que inició sesión
     * */
    public void showMenuStage(String username) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/menu-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            view = fxmlLoader.getController();
            view.initialize(this, scene);

            new Thread(() -> {
                try {
                    Logger.info("Trying to create Client...");
                    client = new Client(username, this);
                } catch (Exception e) {
                    Logger.error("Could not create a new Client");
                    System.exit(1);
                }

                try {
                    server.registerClient(client);
                } catch (RemoteException e) {
                    Logger.error("Cannot register client, check server connection");
                    e.printStackTrace();
                }

                Platform.runLater(() -> {

                    Stage oldWindow = window;

                    window = new Stage();
                    window.setTitle(username + "'s Whisp");
                    window.setScene(scene);
                    window.setResizable(false);
                    window.setOnCloseRequest(_ -> {
                        shutdown();
                    });
                    window.show();

                    oldWindow.close();
                });
            }).start();
        } catch (IOException e) {
            Logger.error("Error loading Menu scene, check xml filepath");
            System.exit(1);
        }

    }

    /**
     * Asigna la escena de Login al stage actual.
     *  */
    public void showLoginScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/login-view.fxml"));
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
        window.setOnCloseRequest(_ -> {
            shutdown();
        });
        window.show();
    }

    /**
     * Asigna la escena de Registro al stage actual.
     *  */
    public void showRegisterScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/register-view.fxml"));
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
     * Asigna la escena de Autentificación al registrarse al stage actual.
     *
     * @param username nombre del nuevo usuario a registrar
     * @param qr image QR a escanear por el usuario codificada como String con Base64
     *  */
    public void showAuthRegisterScene(String username, String qr) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/auth_register-view.fxml"));
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
     * Asigna la escena de Autentificación al iniciar sesión al stage actual.
     *
     * @param username nombre del usuario que requiere autentificación
     *  */
    public void showAuthScene(String username) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/auth-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Auth scene, check xml filepath");
            System.exit(1);
        }
        AuthViewController authViewController = fxmlLoader.getController();
        authViewController.initialize(username, null, null,this, 0);

        Logger.info("Auth scene created, showing it...");
        window.setScene(scene);
        window.show();
    }

    /**
     * Asigna la escena de Autentificación al cambiar contraseña al stage actual.
     *
     * @param username nombre del usuario que requiere autentificación
     * @param oldPassword vieja contraseña del usuario
     * @param newPassword nueva contraseña del usuario
     *  */
    public void showAuthChangesScene(String username, String oldPassword, String newPassword) {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/auth-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        }catch (IOException e){
            Logger.error("Error loading Auth Changes scene, check xml filepath");
            System.exit(1);
        }
        AuthViewController authViewController = fxmlLoader.getController();
        authViewController.initialize(username, oldPassword, newPassword, this, 1);

        window.setScene(scene);
        window.show();
    }

    /**
     * Asigna la escena de Cambio de contraseña al stage actual.
     *  */
    public void showChangePasswordScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/change_password-view.fxml"));
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

    /**
     * Crea la ventana de error y la muestra por pantalla junto a un mensaje.
     *
     * @param message el mensaje de error que se mostrará en la ventana.
     */
    public static void showErrorWindow(String message) {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/error-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            }catch (IOException e){
                Logger.error("Error loading error scene, check xml filepath");
                System.exit(1);
            }
            ErrorViewController controller = fxmlLoader.getController();
            controller.setErrorMessage(message);

            Stage errorStage = new Stage();
            errorStage.initModality(Modality.APPLICATION_MODAL);
            errorStage.setTitle("Error");
            errorStage.setScene(scene);
            errorStage.showAndWait();
    }

    /**
     * Crea la escena de carga (loading) y la prepara para ser utilizada.
     *
     * <p>
     *     Este método debe ejecutarse antes de llamar a {@link #showLoadingScene()}.
     * </p>
     */
    public void createLoadingScene(){
        Logger.info("Creating Loading scene...");
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/gui/loading-view.fxml"));
        try {
            loadingScene = new Scene(fxmlLoader.load());
        }catch (IOException e) {
            Logger.error("Error loading Loading scene, check xml filepath");
            System.exit(1);
        }
        Logger.info("Loading scene created correctly");
    }

    /**
     * Asigna la escena de carga (loading) al stage actual.
     *
     * <p>
     *     La escena actual se guarda en la variable {@link ClientApplication#currScene} para poder restaurarla
     *     posteriormente con {@link #quitLoadingScene()}.
     * </p>
     */
    public void showLoadingScene(){
        Logger.info("Loading...");
        currScene = window.getScene();

        window.setScene(loadingScene);

        window.show();
    }

    /**
     * Restaura la escena anterior a mostrar la escena de carga.
     *
     * <p>
     *     Este método recoge la escena anterior de la variable {@link ClientApplication#currScene}.
     * </p>
     */
    public void quitLoadingScene(){
        Logger.info("Loaded");
        window.setScene(currScene);
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
            if (salt.isEmpty()) return false;
            return server.login(username, PasswordEncrypter.getHashedPassword(password, Base64.getDecoder().decode(salt.getBytes())));
        }catch (RemoteException e){
            Logger.error("Login failed, check server connection");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Se conecta al servidor para comprobar si el nombre de usuario está disponible.
     *
     *
     * @param username nombre del nuevo usuario
     * @return {@code true} si el usuario está disponible, de lo contrario {@code false}
     *  */
    public boolean checkUsernameAvailability(String username) {
        try {
            return server.checkUsernameAvailability(username);
        }catch (RemoteException e){
            Logger.error("Registration failed, check server connection");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Se conecta al servidor para registrar al nuevo usuario, recibir su qr de autentificación y
     * mostrar la pantalla donde escanearlo. Antes de registrar al usuario comprueba si su username está usado;
     * si lo está muestra, un error.
     * La contraseña se hashea antes de ser enviada.
     *
     *
     * @param username nombre del nuevo usuario
     * @param password contraseña sin hashear
     *  */
    public String register(String username, String password) {
        try {
            String[] pass = PasswordEncrypter.createHashPassword(password);
            String qr = server.register(username, pass[1], pass[0]);
            Logger.info("Registrarion completed, moving to validation...");
            return qr;
        }catch (RemoteException e){
            Logger.error("Registration failed, check server connection");
            e.printStackTrace();
        }

        return " ";
    }

    /**
     * Se conecta al servidor para cambiar la contraseña del usuario.
     * Antes de enviar la nueva contraseña, se hashea.
     *
     * @param username nombre del usuario
     * @param oldPassword vieja contraseña sin hashear
     * @param newPassword nueva contraseña sin hashear
     *  */
    public void changePassword(String username, String oldPassword, String newPassword){
        try {
            Logger.info("Hashing introduced password...");

            String salt = server.getSalt(username);
            String oldPass = PasswordEncrypter.getHashedPassword(oldPassword, Base64.getDecoder().decode(salt.getBytes()));

            String[] newPass = PasswordEncrypter.createHashPassword(newPassword);

            server.changePassword(username, oldPass, newPass[1], newPass[0]);
            Logger.info("Password changed correctly");
        }catch (RemoteException e){
            Logger.error("Registration failed when connecting to server");
            e.printStackTrace();
        }
    }

    /**
     * Se conecta al servidor para validar el código de autentificación introducido por el ususario.
     *
     * @param username nombre del nuevo usuario
     * @param code código de autentificación
     *  */
    public boolean validate(String username, int code){
        try {
            return server.validate(username, code);
        } catch (RemoteException e) {
            Logger.error("Could not validate code, check server connection");
            e.printStackTrace();
        }

        return false;
    }



    //*******************************************************************************************
    //* BACK TO FRONT PIPE METHODS
    //*******************************************************************************************

    /**
     * Recibe un Hashmap de amigos con el nombre de usuario como clave y su referencia de Java RMI como valor
     * y se lo pasa a la GUI como un Set de nombres de usuario.
     *
     * @param friends amigos del usuario conectados
     *  */
    public void setFriends(HashMap<String, ClientInterface> friends){
         view.setFriends(friends.keySet());
    }

    /**
     * Crea un objeto mensaje a partir de los parámetros para pasérselo a la GUI.
     *
     * @param message mensaje recibido
     * @param senderName nonbre del remitente
     * @param isText true si es texto plano, false si es una imagen
     *  */
    public void receiveMessage(String message, String senderName, boolean isText){
        view.receiveMessage(new Message(senderName, message, client.username, isText));
    }

    /**
     * Notifica a la GUI de que se ha conectado un amigo.
     *
     * @param friend nombre del amigo conectado
     *  */
    public void friendConnected(String friend){
        view.friendConnected(friend);
    }

    /**
     * Notifica a la GUI de que se ha desconectado un amigo.
     *
     * @param friend nombre del amigo que se ha desconectado
     *  */
    public void friendDisconnected(String friend){
        view.friendDisconnected(friend);
    }

    /**
     * Envía una solicitud de amistad a un usuario comunicándose con el servidor.
     * Antes de eso comprueba si el usuario ya es amigo; si lo es, returnea
     * Si la solicitud se envía correctamente se le comunica a la GUI para que lo  muestre.
     *
     * @param friend nombre del usuario a quien enviar la solicitud
     *  */
    public void sendRequest(String friend){
        try{
            Logger.info("checking if " + getUsername() + " and " + friend + "are already friends...");
            if(server.areFriends(getUsername(), friend)) {
                Logger.info("They are already friends, showing error...");
                showErrorWindow("You are already bros");
                return;
            }
        }catch (RemoteException e){
            Logger.error("Check could not be completed, check server connection");
            e.printStackTrace();
        }

        try{
            Logger.info("They are not friends, sending request to server...");
            if(server.sendRequest(client.username, friend)){
                Logger.info("Request sended");
                addSentRequest(friend);
            }else{
                Logger.info("User " + friend + " does not exist, showing error...");
                showErrorWindow("That bro is not here");

            }
        }catch (RemoteException e){
            Logger.error("Request could not be sended, check server connection");
            e.printStackTrace();
        }
    }

    /**
     * Notifica al servidor que se ha aceptado la solicitud de amistad recibida por un usuario.
     *
     * @param friendName nombre del nuevo amigo
     *  */
    public void requestAccepted(String friendName){
        try {
            server.requestAccepted(friendName, client.username);
            Logger.info("Request accepted on server");
        }catch (RemoteException e) {
            Logger.error("Accepting request failed when connecting to server, check server connection");
            e.printStackTrace();
        }
    }

    /**
     * Notifica a la GUI de que ha llegado una nueva solicitud de amistad.
     *
     * @param requestSender nombre del usuario que envía la solicitud
     *  */
    public void addReceivedRequest(String requestSender){
        view.addRequest(requestSender, client.username);
    }

    /**
     * Notifica a la GUI de que se ha enviado una solicitud de amistad.
     *
     * @param requestReceiver nombre del usuario al que se le envió la solicitud
     *  */
    public void addSentRequest(String requestReceiver){
        view.addRequest(client.username, requestReceiver);
    }

    /**
     * Notifica al servidor que se ha cancelado la solicitud de amistad recibida por un usuario.
     *
     * @param requestSender nombre del usuario rechazado
     *  */
    public void cancelRequest(String requestSender){
        try {
            server.requestCancelled(client.username, requestSender);
            Logger.info("Request cancelled on server");
        }catch (RemoteException e){
            Logger.error("Cancelling request failed when connecting to server, check server connection");
            e.printStackTrace();
        }

    }

    /**
     * Notifica a la GUI de que el usuario al que se le envío una solicitud previamente la ha aceptado o rechazado.
     *
     * @param receiverName nombre del usuario
     *  */
    public void removeResquest(String receiverName){
         view.removeRequest(receiverName);
    }

    public void checkClientStatus(String clientUsername){
        try {
            server.checkClientStatus(clientUsername);
        } catch (RemoteException e) {
            Logger.error("Could not ask for client disconnection, check server connection");
            e.printStackTrace();
        }
    }



    //*******************************************************************************************
    //* FRONT TO BACK PIPE METHODS
    //*******************************************************************************************

    /**
     * Devuelve el username del usuario de la sesión actual.
     *  */
    public String getUsername(){
         return client.username;
    }

    /**
     * Notifica al backend para el envío de una mensaje.
     * El objeto {@link Message} se desempaqueta para enviarlo.
     *
     * @param message objeto Message a enviar
     *  */
    public boolean sendMessage(Message message){
        return client.sendMessage(message.getContent(), message.getReceiver(), message.isText());
    }
}

