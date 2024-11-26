package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class FriendViewController {

    MenuViewController menuViewController;

    @FXML
    Label usernameLabel;
    @FXML
    HBox friendHbox;
    @FXML
    ImageView iconView;

    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    public void setMenuViewController(MenuViewController menuViewController) {
        this.menuViewController = menuViewController;
    }

    @FXML
    public void loadChat()
    {
        menuViewController.showFriends();
        menuViewController.setLoadedChatUser(usernameLabel.getText());
        menuViewController.loadChat();
    }
}
