package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FriendViewController {

    MenuViewController menuViewController;

    @FXML
    Label usernameLabel;

    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

    public void setMenuViewController(MenuViewController menuViewController) {
        this.menuViewController = menuViewController;
    }

    @FXML
    public void loadChat()
    {
        menuViewController.setLoadedChatUser(usernameLabel.getText());
        menuViewController.loadChat();
    }
}
