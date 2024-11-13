package whisp.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import whisp.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MenuViewController {

    Client client;

    String loadedChatUser = " ";

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
    TextArea searchArea;

    @FXML
    TextField myMessageField;
    @FXML
    Button sendButton;

    HashMap<String, Friend> friendsMap;

    public void initialize(Client client)
    {
        this.client = client;

        usernameLabel.setText(client.username + " Whisps");

    }

    public void createDB(){
        friendsMap = new HashMap<>();
        for (String friendName : client.getFriends().keySet()){
            friendsMap.put(friendName, new Friend());
        }
    }

    @FXML
    public void sendMessage()
    {
        Message message = new Message(client.username, myMessageField.getText(), loadedChatUser);
        client.sendMessage(message);
        friendsMap.get(loadedChatUser).addMessage(message);

        myMessageField.clear();
        loadChat();
    }

    @FXML
    public void addFriend(){
        //client.addFriend(searchArea.getText());
        searchArea.clear();
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

                        friendsVbox.getChildren().add(friendNode);
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
    }


}
