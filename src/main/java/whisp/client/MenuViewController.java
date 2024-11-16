package whisp.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import whisp.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MenuViewController {

    Client client;

    String loadedChatUser = " ";

    HashMap<String, Friend> friendsMap;
    ArrayList<FriendRequest> friendRequests;

    public void setLoadedChatUser(String loadedChatUser) {
        this.loadedChatUser = loadedChatUser;
    }

    @FXML
    Label usernameLabel;
    @FXML
    Label chatLabel;

    @FXML
    VBox friendsVbox;
    @FXML
    VBox chatVbox;

    @FXML
    TextField searchField;

    @FXML
    ScrollPane chatScroll;

    @FXML
    TextField myMessageField;
    @FXML
    Button sendButton;

    public void initialize(Client client)
    {
        this.client = client;
        usernameLabel.setText(client.username + " Whisp's");

        chatVbox.heightProperty().addListener((_, _, _) -> {
            chatScroll.setVvalue(1.0);
        });

    }

    public void createDB(){
        friendsMap = new HashMap<>();
        for (String friendName : client.getFriends().keySet()){
            friendsMap.put(friendName, new Friend());
        }

        friendRequests = new ArrayList<>();
    }

    @FXML
    public void sendMessage()
    {
        if(myMessageField.getText().isEmpty()) return;

        Message message = new Message(client.username, myMessageField.getText(), loadedChatUser);
        client.sendMessage(message);
        friendsMap.get(loadedChatUser).addMessage(message);

        myMessageField.clear();
        loadChat();
    }

    @FXML
    public void sendRequest(){
        if (searchField.getText().isEmpty()) return;

        client.sendRequest(searchField.getText());
        searchField.clear();
    }

    public void loadChat()
    {
        Platform.runLater(() -> {
            try {
                chatVbox.getChildren().clear();

                for (Message message : friendsMap.get(loadedChatUser).getChat()){
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("message-view.fxml"));
                    Node messageNode = loader.load();

                    MessageViewController controller = loader.getController();
                    controller.setMessageLabel(message.getContent());

                    if(message.getSender().equals(client.username)){
                        controller.ownMessage();
                    }

                    chatVbox.getChildren().add(messageNode);
                }

                if (friendsMap.get(loadedChatUser).isConnected()) {
                    sendButton.setDisable(false);
                    myMessageField.setDisable(false);
                }else{
                    sendButton.setDisable(true);
                    myMessageField.setDisable(true);
                    myMessageField.setPromptText("User disconnected");
                }

                chatLabel.setText(loadedChatUser + "'s chat");

                friendsMap.get(loadedChatUser).readMessage();
                showFriends();
            } catch (IOException e) {
                Logger.error("Cannot load chat");
            }
        });
    }

    public void friendConnected(String friend){
        if(!friendsMap.containsKey(friend)) {
            friendsMap.put(friend, new Friend());
        }else{
            friendsMap.get(friend).setConnected(true);
        }

        friendRequests.removeIf(f -> f.getReceiverUsername().equals(friend));
    }

    public void friendDisconnected(String friend){
        friendsMap.get(friend).setConnected(false);
        if(loadedChatUser.equals(friend)) loadChat();
    }

    public void showFriends()
    {
        Platform.runLater(() -> {
            try {
                friendsVbox.getChildren().clear();

                for (String friendName : friendsMap.keySet()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("friend-view.fxml"));
                    Node friendNode = loader.load();

                    FriendViewController controller = loader.getController();
                    controller.setMenuViewController(this);
                    controller.setUsernameLabel(friendName);

                    if(!friendsMap.get(friendName).isConnected()){
                        friendNode.getStyleClass().add("hbox-disconnected");
                    }

                    if (friendsMap.get(friendName).hasMessages()){
                        friendNode.getStyleClass().add("hbox-alert");
                    }

                    if (friendName.equals(loadedChatUser)) {
                        friendNode.getStyleClass().add("hbox-selected");
                    }

                    friendsVbox.getChildren().add(friendNode);
                }

                for (FriendRequest fr : friendRequests){
                    if(fr.getSenderUsername().equals(client.username)){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("friend-view.fxml"));
                        Node ownRequestNode = loader.load();

                        FriendViewController controller = loader.getController();
                        controller.setMenuViewController(this);

                        controller.setUsernameLabel(fr.getReceiverUsername());
                        ownRequestNode.setStyle("-fx-background-color: #4CE868;");

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

    public void receiveMessage(Message message)
    {
        friendsMap.get(message.getSender()).addMessage(message);

        if(loadedChatUser.equals(message.getSender())) loadChat();

        showFriends();
    }

    public void addResquest(String sender, String receiver){
        friendRequests.add(new FriendRequest(sender, receiver));

        showFriends();
    }

    public void addFriend(String friendName){
        client.addFriend(friendName);
    }

    public void friendAdded(String friendName){
        friendRequests.removeIf(f -> f.getSenderUsername().equals(friendName));
        friendsMap.put(friendName, new Friend());
    }


}
