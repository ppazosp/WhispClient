package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import whisp.client.gui.entities.Message;
import whisp.utils.GraphicUtils;
import whisp.utils.Logger;

public class MessageViewController {

    //*******************************************************************************************
    //* ATTRIBUTES
    //*******************************************************************************************

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



    //*******************************************************************************************
    //* INITIALIZERS
    //*******************************************************************************************

    /**
     * Settea la imagen del mensaje recibido o enviado y su hora para mostrarlo en pantalla.
     *
     * @param message mensaje a manejar
     */
    public void setImageMessage(Message message){
        Logger.info("Setting image and time...");
        Image image = GraphicUtils.stringToImage(message.getContent());
        imageView.setImage(image);
        timeLabel.setText(message.getTime());
    }

    /**
     * Settea el texto mensaje recibido o enviado y su hora para mostrarlo por pantalla.
     *
     * @param message mensaje a menejar
     */
    public void setMessage(Message message) {
        Logger.info("Setting text and time...");
        messageLabel.setText(message.getContent());
        timeLabel.setText(message.getTime());
    }

    /**
     * Realiza cambios en la distribución de la GUI para indiciar que un mensaje
     * ha sido enviado por el usuario de la sesión actual.
     */
    public void ownMessage(){
        Logger.info("Modifying GUI distribution to show own message...");
        messageHbox.setPadding(new Insets(0, 0, 0, 100));
        messageHbox.setAlignment(Pos.CENTER_RIGHT);
    }

}

