package whisp.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import whisp.Logger;


public class RegisterViewController {

    ClientApplication clientApp;

    @FXML
    Label errorLabel;

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatPasswordField;

    public void initialize(ClientApplication clientApp){
        this.clientApp = clientApp;
    }

    @FXML
    public void register(){
        if(usernameField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                repeatPasswordField.getText().isEmpty()){
            errorLabel.setText("There are empty fields!");
            errorLabel.setVisible(true);
            return;
        }

        if(!passwordField.getText().equals(repeatPasswordField.getText())){
            errorLabel.setText("Passwords must match");
            errorLabel.setVisible(true);
            return;
        }

        clientApp.register(usernameField.getText(), passwordField.getText());

        try {
            clientApp.showLoginScene();
        }catch (Exception e){
            Logger.error("Cannot load Login Scene");
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
}
