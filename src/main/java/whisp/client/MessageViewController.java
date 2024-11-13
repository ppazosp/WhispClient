package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MessageViewController {

    @FXML
    Label messageLabel;

    public void setMessageLabel(String message) {
        messageLabel.setText(message);
    }
}
