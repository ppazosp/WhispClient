package whisp.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import whisp.client.ClientApplication;
import whisp.utils.Logger;

public class AuthViewController {

    int mode;

    ClientApplication clientApp;
    String username;
    String password;

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

    public void initialize(String username, String aux, ClientApplication clientApp, int mode){

        this.clientApp = clientApp;
        this.username = username;

        if(mode == 1)  this.password = aux;

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
        Logger.info("AuthView initialized correctly");
    }

    @FXML
    public void back(){
        Logger.info("Back button pressed, going back to Login scene...");
        clientApp.showLoginScene();
    }

    @FXML
    public void validate(){
        Logger.info("Validate button pressed, trying to validate user...");
        if(digit1.getText().isEmpty() ||
                digit2.getText().isEmpty() ||
                digit3.getText().isEmpty() ||
                digit4.getText().isEmpty() ||
                digit5.getText().isEmpty() ||
                digit6.getText().isEmpty()){
            Logger.info("At least one digit is empty, showing error label...");
            errorLabel.setVisible(true);
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
        Logger.info("Code " + code + " introduced, checking with server...");

        if(clientApp.validate(username, code)){
            Logger.info("Validation completed");
            if(mode == 1){
                Logger.info("Changing password...");
                clientApp.changePassword(username, password);
            }
                clientApp.showMenuStage(username);
        }else{
            Logger.info("Validation failed, showing error label...");
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid Code");
        }
    }
}
