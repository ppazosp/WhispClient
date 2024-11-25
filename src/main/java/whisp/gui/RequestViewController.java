package whisp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import whisp.utils.Logger;

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
        Logger.info("Accept resquest button pressed, trying to add friend...");
        menuViewController.addFriend(usernameLabel.getText());
    }

    @FXML
    public void cancelResquest(){
        Logger.info("Cancel resquest button pressed, informing server... ");
        menuViewController.cancelRequest(usernameLabel.getText());
    }
}
