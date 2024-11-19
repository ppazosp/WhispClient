package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import whisp.Logger;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class AuthRegisterViewController {

    ClientApplication clientApp;

    @FXML
    ImageView qrView;

    public void initialize(String qr, ClientApplication clientApp){

        this.clientApp = clientApp;

        qrView.setImage(stringToImage(qr));
    }

    @FXML
    public void back(){
        try {
            clientApp.showLoginScene();
        }catch (Exception e){
            Logger.error("Cannot load Login Scene");
        }
    }

    private Image stringToImage(String source){
        byte[] imageBytes = Base64.getDecoder().decode(source);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

        return new Image(inputStream);
    }
}
