package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FriendViewController {

    @FXML
    Label usernameLabel;

    public void setUsernameLabel(String username) {
        usernameLabel.setText(username);
    }

}
