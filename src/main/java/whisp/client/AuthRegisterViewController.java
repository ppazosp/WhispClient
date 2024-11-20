package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import whisp.Logger;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class AuthRegisterViewController {

    ClientApplication clientApp;
    String username;

    @FXML
    Label errorLabel;

    @FXML
    TextField digit1;
    @FXML
    TextField digit2;
    @FXML
    TextField digit3;
    @FXML
    TextField digit4;
    @FXML
    TextField digit5;
    @FXML
    TextField digit6;

    @FXML
    ImageView qrView;

    public void initialize(String username, String qr, ClientApplication clientApp){

        this.clientApp = clientApp;
        this.username = username;

        qrView.setImage(Utils.stringToImage(qr));

        TextField[] code = {digit1, digit2, digit3, digit4, digit5, digit6};

        for (int i = 0; i < code.length; i++) {
            TextField currentField = code[i];
            TextField nextField = (i < code.length - 1) ? code[i + 1] : null;

            currentField.textProperty().addListener((_, oldValue, newValue) -> {
                if (!newValue.matches("\\d?")) {
                    currentField.setText(oldValue);
                } else if (!newValue.isEmpty() && nextField != null) {
                    nextField.requestFocus();
                }
            });
        }


    }

    @FXML
    public void back(){
        try {
            clientApp.showLoginScene();
        }catch (Exception e){
            Logger.error("Cannot load Login Scene");
        }
    }

    @FXML
    public void validate(){
        if(digit1.getText().isEmpty() ||
            digit2.getText().isEmpty() ||
            digit3.getText().isEmpty() ||
            digit4.getText().isEmpty() ||
            digit5.getText().isEmpty() ||
            digit6.getText().isEmpty()){

            errorLabel.setText("Blanks are not allowed");
            return;
        }

        String scode = digit1.getText() +
                digit2.getText() +
                digit3.getText() +
                digit4.getText() +
                digit5.getText() +
                digit6.getText();
        int code = Integer.parseInt(scode);

        if(clientApp.validate(username, code)){
            back();
        }else{
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid Code");
        }
    }
}
