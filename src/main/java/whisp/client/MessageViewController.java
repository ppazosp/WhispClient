package whisp.client;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MessageViewController {

    @FXML
    Label messageLabel;

    @FXML
    HBox messageHbox;

    public void setMessageLabel(String message) {
        messageLabel.setText(message);
    }

    public void ownMessage(){
        messageHbox.setPadding(new Insets(0, 0, 0, 100));
        messageHbox.setAlignment(Pos.CENTER_RIGHT);
    }

}

