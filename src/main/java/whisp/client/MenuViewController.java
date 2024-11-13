package whisp.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MenuViewController {

    Client client;

    @FXML
    Label usernameLabel;

    @FXML
    VBox friendsVbox;
    @FXML
    VBox chatVbox;

    public void initialize(Client client)
    {
        this.client = client;

        usernameLabel.setText(client.username + " " + usernameLabel.getText());
    }

    public void showFriends(Set<String> friends)
    {
        Platform.runLater(() -> {
            friendsVbox.getChildren().clear();

            for (String friendName : friends) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("friend-view.fxml"));
                    Node friendNode = loader.load();

                    FriendViewController controller = loader.getController();
                    controller.setUsernameLabel(friendName);

                    friendsVbox.getChildren().add(friendNode);

                } catch (IOException e) {
                    System.err.println("Error showing friends");
                }
            }
        });
    }

    public void showMessage(String message)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("message-view.fxml"));
            Node friendNode = loader.load();

            MessageViewController controller = loader.getController();
            controller.setMessageLabel(message);

            chatVbox.getChildren().add(friendNode);

        } catch (IOException e) {
            System.err.println("Error showing friends");
        }
    }
}
