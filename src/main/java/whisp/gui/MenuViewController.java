package whisp.gui;

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
import whisp.ClientApplication;
import whisp.gui.entities.Friend;
import whisp.gui.entities.FriendRequest;
import whisp.gui.entities.Message;
import whisp.utils.Logger;
import whisp.utils.GraphicUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static whisp.utils.GraphicUtils.isImageFile;

public class MenuViewController {

    /*******************************************************************************************
     * ATTRIBUTES
     *******************************************************************************************/

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



    /*******************************************************************************************
     * INITIALIZERS
     *******************************************************************************************/

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

        Logger.info("MenuView initialized correctly");

    }

    public void setLoadedChatUser(String loadedChatUser) {
        this.loadedChatUser = loadedChatUser;
    }

    public void setFriends(Set<String> friends){
        Logger.info("Setting friends on frontend...");
        for (String friendName : friends){
            friendsMap.put(friendName, new Friend());
        }

        showFriends();
    }



    /*******************************************************************************************
     * FXML METHODS
     *******************************************************************************************/

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



    /*******************************************************************************************
     * MORE METHODS
     *******************************************************************************************/

    private void loadChat() {
        Logger.info("Loading chat...");
        Platform.runLater(() -> {
            try {
                chatSideVbox.setVisible(true);

                chatVbox.getChildren().clear();

                for (Message message : friendsMap.get(loadedChatUser).getChat()){
                    if(message.isText()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("message-view.fxml"));
                        Node messageNode = loader.load();

                        MessageViewController controller = loader.getController();
                        controller.setMessage(message);

                        if (message.getSender().equals(mainApp.getUsername())) {
                            controller.ownMessage();
                        }

                        chatVbox.getChildren().add(messageNode);
                    }else{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("image_message-view.fxml"));
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

    public void friendDisconnected(String friend){
        Logger.info("Setting friend to disconnected on gui");
        friendsMap.get(friend).setConnected(false);
        if(loadedChatUser.equals(friend)){
            Logger.info("Friend chat is active, updating...");
            loadChat();
        }

        showFriends();
    }

    private void showFriends() {
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("friend-view.fxml"));
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
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("friend-view.fxml"));
                        Node ownRequestNode = loader.load();

                        FriendViewController controller = loader.getController();
                        controller.setMenuViewController(this);

                        controller.setUsernameLabel(fr.getReceiverUsername());
                        controller.iconView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/send.png")).toExternalForm()));

                        friendsVbox.getChildren().add(ownRequestNode);

                        ownRequestNode.setDisable(true);
                    }else{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("request-view.fxml"));
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

    public void receiveMessage(Message message) {

        Logger.info("Adding message to chat history");
        friendsMap.get(message.getSender()).addMessage(message);

        if(loadedChatUser.equals(message.getSender())){
            Logger.info("User chat is active, updating...");
            loadChat();
        }

        showFriends();
    }

    public void addRequest(String sender, String receiver){
        Logger.info("Adding request item to gui...");
        friendRequests.add(new FriendRequest(sender, receiver));
    }

    public void addFriend(String friendName){
        mainApp.requestAccepted(friendName);

        Logger.info("Removing request item from gui...");
        friendRequests.removeIf(f -> f.getSenderUsername().equals(friendName));
    }

    public void friendAdded(String friendName){
        friendsMap.put(friendName, new Friend());
        showFriends();
    }

    public void cancelRequest(String senderName) {
        mainApp.cancelRequest(senderName);

        Logger.info("Removing request item from gui...");
        friendRequests.removeIf(f -> f.getSenderUsername().equals(senderName));
        showFriends();
    }

    public void removeRequest(String receiverName) {
        Logger.info("Removing request item from gui...");
        friendRequests.removeIf(f -> f.getReceiverUsername().equals(receiverName));
        showFriends();
    }
}
