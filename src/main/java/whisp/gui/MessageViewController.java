package whisp.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import whisp.gui.entities.Message;
import whisp.utils.GraphicUtils;

public class MessageViewController {

    @FXML
    Label messageLabel;
    @FXML
    Label timeLabel;
    @FXML
    HBox messageHbox;
    @FXML
    HBox textHbox;
    @FXML
    ImageView imageView;

    public void setImageMessage(Message message){
        Image image = GraphicUtils.stringToImage(message.getContent());
        imageView.setImage(image);
        timeLabel.setText(message.getTime());
    }

    public void setMessage(Message message) {
        messageLabel.setText(message.getContent());
        timeLabel.setText(message.getTime());
    }

    public void ownMessage(){
        messageHbox.setPadding(new Insets(0, 0, 0, 100));
        messageHbox.setAlignment(Pos.CENTER_RIGHT);
    }

}

