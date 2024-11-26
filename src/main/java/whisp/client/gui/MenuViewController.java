package whisp.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import whisp.client.ClientApplication;
import whisp.client.gui.entities.Friend;
import whisp.client.gui.entities.FriendRequest;
import whisp.client.gui.entities.Message;
import whisp.utils.Logger;
import whisp.utils.GraphicUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static whisp.utils.GraphicUtils.isImageFile;

public class MenuViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    //*******************************************************************************************

    ClientApplication mainApp;
    String loadedChatUser = "";
    HashMap<String, Friend> friendsMap = new HashMap<>();
    ArrayList<FriendRequest> friendRequests = new ArrayList<>();

    @FXML
    Label chatLabel;
    @FXML
    VBox chatSideVbox;
    @FXML
    VBox friendsVbox;
    @FXML
    VBox chatVbox;
    @FXML
    TextField searchField;
    @FXML
    ScrollPane chatScroll;
    @FXML
    HBox myMessageHbox;
    @FXML
    TextField myMessageField;
    @FXML
    Button sendButton;



    //*******************************************************************************************
    //* INITIALIZERS
    //*******************************************************************************************/

    /**
     * Inicializa el controlador de la escena principal.
     *
     * <p>
     *     Añade los eventos de Drag and Drop en el chat para poder enviar imágenes y de onKeyPressed
     *     para poder enviar mensajes o solicitudes al presionar Enter
     * </p>
     *
     * @param mainApp referencia a la clase princial de flujo de ejecución
     * @param scene   la escena actual utilizada para registrar eventos de teclado.
     */
    public void initialize(ClientApplication mainApp, Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (searchField.isFocused()) {
                    sendRequest();
                } else if (myMessageField.isFocused() && !sendButton.isDisable()) {
                    sendMessage();
                }
            }
        });

        this.mainApp = mainApp;

        chatVbox.heightProperty().addListener((_, _, _) -> {
            chatScroll.setVvalue(1.0);
        });

        myMessageHbox.setOnDragOver(event -> {
            if(loadedChatUser.isEmpty()) return;

            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                if (dragboard.getFiles().stream().allMatch(GraphicUtils::isImageFile)) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        myMessageHbox.setOnDragDropped(event -> {
            if(loadedChatUser.isEmpty()) return;

            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles()) {
                for (File file : dragboard.getFiles()) {
                    if (isImageFile(file)) {
                        try {
                            Image image = new Image(file.toURI().toString());
                            String simage = GraphicUtils.imageToString(image);

                            Message message = new Message(mainApp.getUsername(), simage, loadedChatUser, false);
                            mainApp.sendMessage(message);
                            friendsMap.get(loadedChatUser).addMessage(message);
                            loadChat();

                            success = true;
                        } catch (Exception e) {
                            Logger.error("Could not convert image to String");
                        }

                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        Logger.info("Menu view initialized correctly");

    }

    /**
     * Setter del atributo {@code loadedChatUser}
     *
     * @param loadedChatUser usuario al que pertenece el chat actual
     */
    public void setLoadedChatUser(String loadedChatUser) {
        this.loadedChatUser = loadedChatUser;
    }

    /**
     * Setter del HashMap friends
     *
     * @param friends set de nombres de amigos conectados
     */
    public void setFriends(Set<String> friends){
        Logger.info("Setting friends on frontend...");
        for (String friendName : friends){
            friendsMap.put(friendName, new Friend());
        }

        showFriends();
    }



    //*******************************************************************************************
    //* FXML METHODS
    //*******************************************************************************************

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de enviar mensaje.
     *
     * <p>
     *     Envía un mensaje al usuario actualmente cargado en el chat (atributo {@code loadedChatUser}).
     *     Guarda el mensaje enviado en el historial de mensajes del objeto Friend correspondiente y recarga el chat
     *     para que el mensaje sea visible.
     * </p>
     *
     * <p>
     *     Antes de enviar el mensaje comprueba si está vacío; si lo está, no lo envía
     * </p>
     */
    @FXML
    public void sendMessage() {
        Logger.info("Send buton pressed, trying to send message...");

        if(myMessageField.getText().isEmpty()){
            Logger.info("MessageField was empty, returning...");
            return;
        }

        Message message = new Message(mainApp.getUsername(), myMessageField.getText(), loadedChatUser, true);
        mainApp.sendMessage(message);

        Logger.info("Adding sended message to gui...");
        friendsMap.get(loadedChatUser).addMessage(message);
        myMessageField.clear();

        loadChat();
    }

    /**
     * Función lanzada por {@code FXML} al pulsar el botón de añadir amigo.
     *
     * <p>
     *     Envía una solicitud de amistad al servidor comunicándose con él a través la clase principal.
     *     Luego de enviarla recarga la lista de amigos para confirmar que se ha enviado
     * </p>
     *
     * <p>
     *     Antes de enviar la solicitud comprueba si el nombre vacío; si lo está, no la envía
     * </p>
     */
    @FXML
    public void sendRequest(){
        Logger.info("Send resquest button pressed, trying to send request...");
        if (searchField.getText().isEmpty()){
            Logger.info("SearchField was empty, returning...");
            return;
        }

        mainApp.sendRequest(searchField.getText());
        searchField.clear();

        showFriends();
    }



    //*******************************************************************************************
    //* MORE METHODS
    //*******************************************************************************************

    /**
     * Carga y muestra por pantalla el historial de chat para el usuario actualmente seleccionado.
     *
     * <p>
     *     Comprueba si el usuario está desconectado para deshabilitar el envío de mesanjes
     * </p>
     */
    public void loadChat() {
        Logger.info("Loading chat...");
        Platform.runLater(() -> {
            try {
                chatSideVbox.setVisible(true);

                chatVbox.getChildren().clear();

                for (Message message : friendsMap.get(loadedChatUser).getChat()){
                    if(message.isText()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/message-view.fxml"));
                        Node messageNode = loader.load();

                        MessageViewController controller = loader.getController();
                        controller.setMessage(message);

                        if (message.getSender().equals(mainApp.getUsername())) {
                            controller.ownMessage();
                        }

                        chatVbox.getChildren().add(messageNode);
                    }else{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/image_message-view.fxml"));
                        Node messageNode = loader.load();

                        MessageViewController controller = loader.getController();
                        controller.setImageMessage(message);

                        if (message.getSender().equals(mainApp.getUsername())) {
                            controller.ownMessage();
                        }

                        chatVbox.getChildren().add(messageNode);
                    }
                }

                if (friendsMap.get(loadedChatUser).isConnected()) {
                    sendButton.setDisable(false);
                    myMessageField.setDisable(false);
                }else{
                    sendButton.setDisable(true);
                    myMessageField.setDisable(true);
                    myMessageField.setPromptText("User disconnected");
                }

                chatLabel.setText(loadedChatUser);

                friendsMap.get(loadedChatUser).readMessage();
                showFriends();
            } catch (IOException e) {
                Logger.error("Cannot load chat, check xml path files");
            }
        });
    }

    /**
     * Maneja de conexión de un nuevo amigo. Al acabar actualiza la lista de amigos para mostrar el cambio
     *
     * <p>
     *     Si el amigo ya se había conectado previamente, se actualiza su estado a conectado.
     * </p>
     *
     * <p>
     *     Si el amigo tenía una solicitud, se elimina de solicitudes pendientes
     * </p>
     *
     * @param friend nombre del nuevo usuario conectado
     */
    public void friendConnected(String friend){
        Logger.info("Updating frontend...");

        if(!friendsMap.containsKey(friend)) {
            friendsMap.put(friend, new Friend());
        }else{
            Logger.info("User was connected before, setting connected to true...");
            friendsMap.get(friend).setConnected(true);
        }

        Logger.info("Checking if user had a friend request...");
        friendRequests.removeIf(f -> f.getReceiverUsername().equals(friend));

        showFriends();
    }

    /**
     * Maneja de desconexión de un amigo. Al acabar actualiza la lista de amigos para mostrar el cambio.
     *
     * <p>
     *     Se guarda su estado como desconectado.
     * </p>
     *
     * <p>
     *     Si el chat actual era con él, se actualiza el chat para deshabilitar el envío de mensajes.
     * </p>
     *
     * @param friend nombre del usuario desconectado
     */
    public void friendDisconnected(String friend){
        Logger.info("Setting friend to disconnected on gui");
        friendsMap.get(friend).setConnected(false);
        if(loadedChatUser.equals(friend)){
            Logger.info("Friend chat is active, updating...");
            loadChat();
        }

        showFriends();
    }

    /**
     * Muestra la lista de amigos y manejo los casos posibles.
     *
     * <p>
     *     Si se recibe un mensaje de un amigo, aparecerá una exclamación al lado de su nombre.
     * </p>
     *
     * <p>
     *     Si se envía una solicitud a un usuario, aparecerá con un avión al lado de su nombre.
     * </p>
     *
     * <p>
     *     Si se desconecta un amigo, aparecerá un símbolo de desconexión a su lado.
     * </p>
     */
    public void showFriends() {
        Logger.info("Showing friends...");
        Platform.runLater(() -> {
            try {
                friendsVbox.getChildren().clear();

                //CHECK: if friendsMap is empty, show message
                if(friendsMap.isEmpty()){
                    System.out.println("No friends to show");
                }else{
                    System.out.println("Friends to show");
                }

                for (String friendName : friendsMap.keySet()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/friend-view.fxml"));
                    Node friendNode = loader.load();

                    FriendViewController controller = loader.getController();
                    controller.setMenuViewController(this);
                    controller.setUsernameLabel(friendName);

                    if(friendsMap.get(friendName).hasMessages()){
                        controller.iconView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/exclamation.png")).toExternalForm()));
                    }else if (!friendsMap.get(friendName).isConnected()){
                        controller.iconView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/disconnect.png")).toExternalForm()));
                    }else{
                        controller.iconView.setImage(null);
                    }

                    if (friendName.equals(loadedChatUser)) {
                        friendNode.getStyleClass().add("hbox-selected");
                    }

                    friendsVbox.getChildren().add(friendNode);
                }

                for (FriendRequest fr : friendRequests){
                    if(fr.getSenderUsername().equals(mainApp.getUsername())){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/friend-view.fxml"));
                        Node ownRequestNode = loader.load();

                        FriendViewController controller = loader.getController();
                        controller.setMenuViewController(this);

                        controller.setUsernameLabel(fr.getReceiverUsername());
                        controller.iconView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/send.png")).toExternalForm()));

                        friendsVbox.getChildren().add(ownRequestNode);

                        ownRequestNode.setDisable(true);
                    }else{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/request-view.fxml"));
                        Node requestNode = loader.load();

                        RequestViewController controller = loader.getController();
                        controller.setMenuViewController(this);

                        controller.setUsernameLabel(fr.getSenderUsername());

                        friendsVbox.getChildren().add(requestNode);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error showing friends");
            }
        });
    }

    /**
     * Recibe un mensaje enviado por un amigo y lo guarda en el historial de mensajes del objeto Friend correspondiente.
     * Al acabar actualiza la lista de amigos para mostrar que hay un mensaje nuevo.
     *
     * <p>
     *     Si el chat actual es del amigo que envió el mensaje, se actualiza el chat para mostrarlo.
     * </p>
     *
     * @param message mensaje recibido
     */
    public void receiveMessage(Message message) {

        Logger.info("Adding message to chat history");
        friendsMap.get(message.getSender()).addMessage(message);

        if(loadedChatUser.equals(message.getSender())){
            Logger.info("User chat is active, updating...");
            loadChat();
        }

        showFriends();
    }

    /**
     * Recibe y guarda un solicitud de amistad para mostrar en la lista de amigos.
     * Al acabar actualiza la lista de amigos para mostrar el cambio.
     *
     * @param sender nombre del usuario que envió la solicitud
     * @param receiver nombre del usuario que recibe la solicitud
     */
    public void addRequest(String sender, String receiver){
        Logger.info("Adding request item to gui...");
        friendRequests.add(new FriendRequest(sender, receiver));

        showFriends();
    }

    /**
     * Añade a un amigo al aceptar una solicitud de amistad.
     *
     * <p>
     *     Comunica al servidor de la aceptación a través de la clase principal y luego llama a
     *     {@code friendConnected} para guardar al nuevo amigo
     * </p>
     */
    public void addFriend(String friendName){
        mainApp.requestAccepted(friendName);
        friendConnected(friendName);
    }

    /**
     * Cancela la solicitud de amistad de un amigo
     *
     * <p>
     *     Comunica al servidor de la cancelación a través de la clase principal y luego llama a
     *     {@code removeRequest} para manejar la eliminación de la solicitud en la GUI.
     * </p>
     */
    public void cancelRequest(String senderName) {
        mainApp.cancelRequest(senderName);

        removeRequest(senderName);
    }

    /**
     * Elimina una solicitud de la lista de solicitudes pendientes para no mostrarla más por pantalla.
     * Al acabar actualiza la lista de amigos para mostrar el cambio.
     */
    public void removeRequest(String receiverName) {
        Logger.info("Removing request item from gui...");
        friendRequests.removeIf(f -> f.getReceiverUsername().equals(receiverName));
        showFriends();
    }
}
