package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RequestViewController {

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
    public void acceptResquest(){
        menuViewController.addFriend(usernameLabel.getText());
    }

    @FXML
    public void cancelResquest(){
        //TODO: cancel request
    }
}
